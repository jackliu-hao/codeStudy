package org.codehaus.plexus.util.dag;

import java.util.Iterator;
import java.util.List;

public class CycleDetectedException extends Exception {
   private List cycle;

   public CycleDetectedException(String message, List cycle) {
      super(message);
      this.cycle = cycle;
   }

   public List getCycle() {
      return this.cycle;
   }

   public String cycleToString() {
      StringBuffer buffer = new StringBuffer();
      Iterator iterator = this.cycle.iterator();

      while(iterator.hasNext()) {
         buffer.append(iterator.next());
         if (iterator.hasNext()) {
            buffer.append(" --> ");
         }
      }

      return buffer.toString();
   }

   public String getMessage() {
      return super.getMessage() + " " + this.cycleToString();
   }
}
