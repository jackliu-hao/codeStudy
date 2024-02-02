/*    */ package com.beust.jcommander.internal;
/*    */ 
/*    */ import com.beust.jcommander.ParameterException;
/*    */ import java.io.PrintWriter;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ 
/*    */ public class JDK6Console
/*    */   implements Console
/*    */ {
/*    */   private Object console;
/*    */   private PrintWriter writer;
/*    */   
/*    */   public JDK6Console(Object console) throws Exception {
/* 15 */     this.console = console;
/* 16 */     Method writerMethod = console.getClass().getDeclaredMethod("writer", new Class[0]);
/* 17 */     this.writer = (PrintWriter)writerMethod.invoke(console, new Object[0]);
/*    */   }
/*    */   
/*    */   public void print(String msg) {
/* 21 */     this.writer.print(msg);
/*    */   }
/*    */   
/*    */   public void println(String msg) {
/* 25 */     this.writer.println(msg);
/*    */   }
/*    */   
/*    */   public char[] readPassword(boolean echoInput) {
/*    */     try {
/* 30 */       this.writer.flush();
/*    */       
/* 32 */       if (echoInput) {
/* 33 */         Method method1 = this.console.getClass().getDeclaredMethod("readLine", new Class[0]);
/* 34 */         return ((String)method1.invoke(this.console, new Object[0])).toCharArray();
/*    */       } 
/* 36 */       Method method = this.console.getClass().getDeclaredMethod("readPassword", new Class[0]);
/* 37 */       return (char[])method.invoke(this.console, new Object[0]);
/*    */     
/*    */     }
/* 40 */     catch (Exception e) {
/* 41 */       throw new ParameterException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\internal\JDK6Console.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */