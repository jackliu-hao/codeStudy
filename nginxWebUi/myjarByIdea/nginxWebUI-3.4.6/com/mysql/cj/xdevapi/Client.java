package com.mysql.cj.xdevapi;

public interface Client {
   Session getSession();

   void close();

   public static enum ClientProperty {
      POOLING_ENABLED("pooling.enabled"),
      POOLING_MAX_SIZE("pooling.maxSize"),
      POOLING_MAX_IDLE_TIME("pooling.maxIdleTime"),
      POOLING_QUEUE_TIMEOUT("pooling.queueTimeout");

      private String keyName = "";

      private ClientProperty(String keyName) {
         this.keyName = keyName;
      }

      public String getKeyName() {
         return this.keyName;
      }
   }
}
