package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.util.List;

public class CPField extends CPMember {
   public CPField(CPUTF8 name, CPUTF8 descriptor, long flags, List attributes) {
      super(name, descriptor, flags, attributes);
   }

   public String toString() {
      return "Field: " + this.name + "(" + this.descriptor + ")";
   }
}
