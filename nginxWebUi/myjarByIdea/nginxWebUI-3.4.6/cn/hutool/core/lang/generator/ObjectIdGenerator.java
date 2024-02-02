package cn.hutool.core.lang.generator;

import cn.hutool.core.lang.ObjectId;

public class ObjectIdGenerator implements Generator<String> {
   public String next() {
      return ObjectId.next();
   }
}
