package org.noear.solon.ext;

import java.io.Serializable;

public class DataThrowable extends RuntimeException implements Serializable {
   private Object data;

   public Object data() {
      return this.data;
   }

   public DataThrowable data(Object data) {
      this.data = data;
      return this;
   }

   public DataThrowable() {
   }

   public DataThrowable(Throwable cause) {
      super(cause);
   }

   public DataThrowable(String message) {
      super(message);
   }

   public DataThrowable(String message, Throwable cause) {
      super(message, cause);
   }
}
