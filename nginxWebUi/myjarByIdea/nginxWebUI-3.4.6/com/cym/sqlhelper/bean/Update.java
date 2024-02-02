package com.cym.sqlhelper.bean;

import java.util.HashMap;
import java.util.Map;

public class Update {
   Map<String, Object> sets = new HashMap();

   public Update set(String key, Object value) {
      this.sets.put(key, value);
      return this;
   }

   public Map<String, Object> getSets() {
      return this.sets;
   }

   public void setSets(Map<String, Object> sets) {
      this.sets = sets;
   }
}
