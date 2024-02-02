package cn.hutool.core.net;

import cn.hutool.core.codec.PercentCodec;

public class FormUrlencoded {
   public static final PercentCodec ALL;

   static {
      ALL = PercentCodec.of(RFC3986.UNRESERVED).removeSafe('~').addSafe('*').setEncodeSpaceAsPlus(true);
   }
}
