package org.apache.maven.model;

import java.io.Serializable;

public class InputSource implements Serializable, Cloneable {
   private String modelId;
   private String location;

   public InputSource clone() {
      try {
         InputSource copy = (InputSource)super.clone();
         return copy;
      } catch (Exception var2) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var2);
      }
   }

   public String getLocation() {
      return this.location;
   }

   public String getModelId() {
      return this.modelId;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public void setModelId(String modelId) {
      this.modelId = modelId;
   }
}
