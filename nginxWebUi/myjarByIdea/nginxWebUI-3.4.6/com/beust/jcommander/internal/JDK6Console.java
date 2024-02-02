package com.beust.jcommander.internal;

import com.beust.jcommander.ParameterException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

public class JDK6Console implements Console {
   private Object console;
   private PrintWriter writer;

   public JDK6Console(Object console) throws Exception {
      this.console = console;
      Method writerMethod = console.getClass().getDeclaredMethod("writer");
      this.writer = (PrintWriter)writerMethod.invoke(console);
   }

   public void print(String msg) {
      this.writer.print(msg);
   }

   public void println(String msg) {
      this.writer.println(msg);
   }

   public char[] readPassword(boolean echoInput) {
      try {
         this.writer.flush();
         Method method;
         if (echoInput) {
            method = this.console.getClass().getDeclaredMethod("readLine");
            return ((String)method.invoke(this.console)).toCharArray();
         } else {
            method = this.console.getClass().getDeclaredMethod("readPassword");
            return (char[])((char[])method.invoke(this.console));
         }
      } catch (Exception var3) {
         throw new ParameterException(var3);
      }
   }
}
