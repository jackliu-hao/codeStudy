package org.h2.value;

import org.h2.util.HasSQL;

public abstract class ExtTypeInfo implements HasSQL {
   public String toString() {
      return this.getSQL(1);
   }
}
