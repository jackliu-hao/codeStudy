package org.h2.mvstore;

public class MVStoreException extends RuntimeException {
   private static final long serialVersionUID = 2847042930249663807L;
   private final int errorCode;

   public MVStoreException(int var1, String var2) {
      super(var2);
      this.errorCode = var1;
   }

   public int getErrorCode() {
      return this.errorCode;
   }
}
