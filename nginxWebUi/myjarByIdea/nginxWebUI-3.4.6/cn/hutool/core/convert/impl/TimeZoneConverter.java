package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import java.util.TimeZone;

public class TimeZoneConverter extends AbstractConverter<TimeZone> {
   private static final long serialVersionUID = 1L;

   protected TimeZone convertInternal(Object value) {
      return TimeZone.getTimeZone(this.convertToStr(value));
   }
}
