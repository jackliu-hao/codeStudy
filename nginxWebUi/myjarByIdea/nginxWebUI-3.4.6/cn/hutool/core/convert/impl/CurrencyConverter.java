package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import java.util.Currency;

public class CurrencyConverter extends AbstractConverter<Currency> {
   private static final long serialVersionUID = 1L;

   protected Currency convertInternal(Object value) {
      return Currency.getInstance(this.convertToStr(value));
   }
}
