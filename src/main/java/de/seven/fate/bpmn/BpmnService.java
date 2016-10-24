package de.seven.fate.bpmn;

import lombok.extern.java.Log;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.Query;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.xml.Model;
import org.camunda.bpm.model.xml.ModelInstance;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Log
@Service
public class BpmnService {

    @Inject
    private ResourcePatternResolver resourceLoader;

    @PostConstruct
    private void init() {

        log.info("###########################################################");

        BpmnModelInstance bpmnModelInstance = getBpmnModelInstance();

        Model model = bpmnModelInstance.getModel();
        log.info("modelName: " + model.getModelName());

        List<StartEvent> startEvents = getModelElementsByType(bpmnModelInstance, StartEvent.class);
        startEvents.forEach(startEvent -> {

            Query<FlowNode> succeedingNodes = startEvent.getSucceedingNodes();
            succeedingNodes.list().forEach(flowNode -> {
                log.info(" -> flowNode: " + flowNode.getId() + " : " + flowNode.getName());
            });

            String camundaFormKey = startEvent.getCamundaFormKey();
            ModelElementType elementType = startEvent.getElementType();
            ModelInstance modelInstance = startEvent.getModelInstance();

            log.info("StartEvent: " + startEvent.getId() + " : " + startEvent.getName());
        });

        List<Task> tasks = getModelElementsByType(bpmnModelInstance, Task.class);
        tasks.forEach(task -> {

            Query<FlowNode> succeedingNodes = task.getSucceedingNodes();
            succeedingNodes.list().forEach(flowNode -> {
                log.info(" -> flowNode: " + flowNode.getId() + " : " + flowNode.getName());
            });

            log.info("Task: " + task.getId() + " : " + task.getName());
        });

        List<Gateway> gateways = getModelElementsByType(bpmnModelInstance, Gateway.class);
        gateways.forEach(gateway -> {

            Query<FlowNode> succeedingNodes = gateway.getSucceedingNodes();
            succeedingNodes.list().forEach(flowNode -> {
                log.info(" -> flowNode: " + flowNode.getId() + " : " + flowNode.getName());
            });

            log.info("Gateway: " + gateway.getId() + " : " + gateway.getName());
        });

        List<SequenceFlow> sequenceFlows = getModelElementsByType(bpmnModelInstance, SequenceFlow.class);
        sequenceFlows.forEach(sequenceFlow -> {

            ConditionExpression conditionExpression = sequenceFlow.getConditionExpression();
            log.info("SequenceFlow: " + sequenceFlow.getId() + " : " + (conditionExpression != null ? conditionExpression.getType() : ""));
        });

        List<EndEvent> endEvents = getModelElementsByType(bpmnModelInstance, EndEvent.class);
        endEvents.forEach(endEvent -> {

            Query<FlowNode> succeedingNodes = endEvent.getSucceedingNodes();

            succeedingNodes.list().forEach(flowNode -> {
                log.info(" -> flowNode: " + flowNode.getId() + " : " + flowNode.getName());
            });

            log.info("EndEvent: " + endEvent.getId() + " : " + endEvent.getName());
        });

        log.info("###########################################################");
    }

    private BpmnModelInstance getBpmnModelInstance() {

        Resource resource = resourceLoader.getResource("classpath:/processes/purchaseOrderRequest.bpmn");

        BpmnModelInstance bpmnModelInstance = null;

        try {
            InputStream inputStream = resource.getInputStream();
            bpmnModelInstance = Bpmn.readModelFromStream(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bpmnModelInstance;
    }


    /**
     * @param modelInstance the process model as stream
     * @param type          the ModelElementInstance type
     * @param <T>           ModelElementInstance Type
     * @return the model elements by type
     */
    private <T extends ModelElementInstance> List<T> getModelElementsByType(BpmnModelInstance modelInstance, Class<T> type) {

        ModelElementType modelElementType = modelInstance.getModel().getType(type);

        Collection<ModelElementInstance> elementInstances = modelInstance.getModelElementsByType(modelElementType);

        return elementInstances.stream().map(elementInstance -> (T) elementInstance).collect(Collectors.toList());
    }
}
