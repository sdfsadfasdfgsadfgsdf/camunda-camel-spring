package de.seven.fate.converter;

import com.e2open.model.converter.AbstractConverter;
import de.seven.fate.dto.PurchaseItemDto;
import de.seven.fate.model.PurchaseItem;
import org.springframework.stereotype.Component;

@Component
public class PurchaseItem2PurchaseItemDtoConverter extends AbstractConverter<PurchaseItemDto, PurchaseItem> {

}
