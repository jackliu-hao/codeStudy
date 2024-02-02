package org.h2.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraceWriterAdapter implements TraceWriter {
   private String name;
   private final Logger logger = LoggerFactory.getLogger("h2database");

   public void setName(String var1) {
      this.name = var1;
   }

   public boolean isEnabled(int var1) {
      switch (var1) {
         case 1:
            return this.logger.isErrorEnabled();
         case 2:
            return this.logger.isInfoEnabled();
         case 3:
            return this.logger.isDebugEnabled();
         default:
            return false;
      }
   }

   public void write(int var1, int var2, String var3, Throwable var4) {
      this.write(var1, Trace.MODULE_NAMES[var2], var3, var4);
   }

   public void write(int var1, String var2, String var3, Throwable var4) {
      if (this.isEnabled(var1)) {
         if (this.name != null) {
            var3 = this.name + ":" + var2 + " " + var3;
         } else {
            var3 = var2 + " " + var3;
         }

         switch (var1) {
            case 1:
               this.logger.error(var3, var4);
               break;
            case 2:
               this.logger.info(var3, var4);
               break;
            case 3:
               this.logger.debug(var3, var4);
         }
      }

   }
}
