/*    */ package com.beust.jcommander.internal;
/*    */ 
/*    */ import com.beust.jcommander.ParameterException;
/*    */ import java.io.BufferedReader;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStreamReader;
/*    */ 
/*    */ public class DefaultConsole
/*    */   implements Console
/*    */ {
/*    */   public void print(String msg) {
/* 12 */     System.out.print(msg);
/*    */   }
/*    */   
/*    */   public void println(String msg) {
/* 16 */     System.out.println(msg);
/*    */   }
/*    */ 
/*    */   
/*    */   public char[] readPassword(boolean echoInput) {
/*    */     try {
/* 22 */       InputStreamReader isr = new InputStreamReader(System.in);
/* 23 */       BufferedReader in = new BufferedReader(isr);
/* 24 */       String result = in.readLine();
/* 25 */       return result.toCharArray();
/*    */     }
/* 27 */     catch (IOException e) {
/* 28 */       throw new ParameterException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\internal\DefaultConsole.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */