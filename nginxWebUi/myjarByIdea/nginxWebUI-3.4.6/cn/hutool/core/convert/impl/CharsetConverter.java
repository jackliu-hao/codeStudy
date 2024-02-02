package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.util.CharsetUtil;
import java.nio.charset.Charset;

public class CharsetConverter extends AbstractConverter<Charset> {
   private static final long serialVersionUID = 1L;

   protected Charset convertInternal(Object value) {
      return CharsetUtil.charset(this.convertToStr(value));
   }
}
