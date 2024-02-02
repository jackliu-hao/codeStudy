package cn.hutool.json;

import java.io.Serializable;

public class JSONNull implements Serializable {
   private static final long serialVersionUID = 2633815155870764938L;
   public static final JSONNull NULL = new JSONNull();

   public boolean equals(Object object) {
      return object == null || object == this;
   }

   public String toString() {
      return "null";
   }
}
