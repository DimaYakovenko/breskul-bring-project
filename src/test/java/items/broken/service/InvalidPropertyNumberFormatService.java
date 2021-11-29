package items.broken.service;

import com.bringframework.annotation.BoboValue;
import com.bringframework.annotation.Item;

import java.math.BigDecimal;

@Item
public class InvalidPropertyNumberFormatService {

    @BoboValue("invalid.big.decimal.value")
    private BigDecimal bigDecimalValue;
}
