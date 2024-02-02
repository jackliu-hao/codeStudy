package com.mysql.cj.xdevapi;

public class WarningImpl implements Warning {
   private com.mysql.cj.protocol.Warning message;

   public WarningImpl(com.mysql.cj.protocol.Warning message) {
      this.message = message;
   }

   public int getLevel() {
      return this.message.getLevel();
   }

   public long getCode() {
      return this.message.getCode();
   }

   public String getMessage() {
      return this.message.getMessage();
   }
}
