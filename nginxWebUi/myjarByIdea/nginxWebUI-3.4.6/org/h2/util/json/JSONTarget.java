package org.h2.util.json;

import java.math.BigDecimal;

public abstract class JSONTarget<R> {
   public abstract void startObject();

   public abstract void endObject();

   public abstract void startArray();

   public abstract void endArray();

   public abstract void member(String var1);

   public abstract void valueNull();

   public abstract void valueFalse();

   public abstract void valueTrue();

   public abstract void valueNumber(BigDecimal var1);

   public abstract void valueString(String var1);

   public abstract boolean isPropertyExpected();

   public abstract boolean isValueSeparatorExpected();

   public abstract R getResult();
}
