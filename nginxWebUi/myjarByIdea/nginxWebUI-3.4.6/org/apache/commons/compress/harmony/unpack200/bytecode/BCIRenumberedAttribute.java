package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.commons.compress.harmony.pack200.Pack200Exception;

public abstract class BCIRenumberedAttribute extends Attribute {
   protected boolean renumbered;

   public boolean hasBCIRenumbering() {
      return true;
   }

   public BCIRenumberedAttribute(CPUTF8 attributeName) {
      super(attributeName);
   }

   protected abstract int getLength();

   protected abstract void writeBody(DataOutputStream var1) throws IOException;

   public abstract String toString();

   protected abstract int[] getStartPCs();

   public void renumber(List byteCodeOffsets) throws Pack200Exception {
      if (this.renumbered) {
         throw new Error("Trying to renumber a line number table that has already been renumbered");
      } else {
         this.renumbered = true;
         int[] startPCs = this.getStartPCs();

         for(int index = 0; index < startPCs.length; ++index) {
            startPCs[index] = (Integer)byteCodeOffsets.get(startPCs[index]);
         }

      }
   }
}
