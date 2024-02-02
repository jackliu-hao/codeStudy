package org.apache.http;

public interface ExceptionLogger {
   ExceptionLogger NO_OP = new ExceptionLogger() {
      public void log(Exception ex) {
      }
   };
   ExceptionLogger STD_ERR = new ExceptionLogger() {
      public void log(Exception ex) {
         ex.printStackTrace();
      }
   };

   void log(Exception var1);
}
