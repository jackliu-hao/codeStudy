package org.h2.util.json;

import java.math.BigDecimal;

public class JSONNumber extends JSONValue {
   private final BigDecimal value;

   JSONNumber(BigDecimal var1) {
      this.value = var1;
   }

   public void addTo(JSONTarget<?> var1) {
      var1.valueNumber(this.value);
   }

   public BigDecimal getBigDecimal() {
      return this.value;
   }
}
