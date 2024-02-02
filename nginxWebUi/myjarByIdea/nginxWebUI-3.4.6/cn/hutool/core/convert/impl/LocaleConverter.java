package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.util.StrUtil;
import java.util.Locale;

public class LocaleConverter extends AbstractConverter<Locale> {
   private static final long serialVersionUID = 1L;

   protected Locale convertInternal(Object value) {
      try {
         String str = this.convertToStr(value);
         if (StrUtil.isEmpty(str)) {
            return null;
         } else {
            String[] items = str.split("_");
            if (items.length == 1) {
               return new Locale(items[0]);
            } else {
               return items.length == 2 ? new Locale(items[0], items[1]) : new Locale(items[0], items[1], items[2]);
            }
         }
      } catch (Exception var4) {
         return null;
      }
   }
}
