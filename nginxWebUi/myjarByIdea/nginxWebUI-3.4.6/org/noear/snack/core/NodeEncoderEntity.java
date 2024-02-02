package org.noear.snack.core;

import org.noear.snack.ONode;

public class NodeEncoderEntity<T> implements NodeEncoder<T> {
   private final Class<T> type;
   private final NodeEncoder<T> encoder;

   public NodeEncoderEntity(Class<T> type, NodeEncoder<T> encoder) {
      this.type = type;
      this.encoder = encoder;
   }

   public boolean isEncodable(Class<?> cls) {
      return this.type.isAssignableFrom(cls);
   }

   public void encode(T source, ONode target) {
      this.encoder.encode(source, target);
   }
}
