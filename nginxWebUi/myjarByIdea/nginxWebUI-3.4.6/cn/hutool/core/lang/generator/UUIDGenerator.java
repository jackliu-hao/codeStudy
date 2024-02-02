package cn.hutool.core.lang.generator;

import cn.hutool.core.util.IdUtil;

public class UUIDGenerator implements Generator<String> {
   public String next() {
      return IdUtil.fastUUID();
   }
}
