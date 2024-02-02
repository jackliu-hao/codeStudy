package org.apache.http.client.entity;

import org.apache.http.HttpEntity;

public class DeflateDecompressingEntity extends DecompressingEntity {
   public DeflateDecompressingEntity(HttpEntity entity) {
      super(entity, DeflateInputStreamFactory.getInstance());
   }
}
