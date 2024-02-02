/*    */ package org.noear.solon.core.util;
/*    */ 
/*    */ import java.io.File;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PrintUtil
/*    */ {
/*    */   public static final String ANSI_RESET = "\033[0m";
/*    */   public static final String ANSI_BLACK = "\033[30m";
/*    */   public static final String ANSI_RED = "\033[31m";
/*    */   public static final String ANSI_GREEN = "\033[32m";
/*    */   public static final String ANSI_YELLOW = "\033[33m";
/*    */   public static final String ANSI_BLUE = "\033[34m";
/*    */   public static final String ANSI_PURPLE = "\033[35m";
/*    */   public static final String ANSI_CYAN = "\033[36m";
/*    */   public static final String ANSI_WHITE = "\033[37m";
/* 25 */   public static final boolean IS_WINDOWS = (File.separatorChar == '\\');
/*    */ 
/*    */   
/*    */   public static void blackln(Object txt) {
/* 29 */     colorln("\033[30m", txt);
/*    */   }
/*    */   
/*    */   public static void redln(Object txt) {
/* 33 */     colorln("\033[31m", txt);
/*    */   }
/*    */   
/*    */   public static void blueln(Object txt) {
/* 37 */     colorln("\033[34m", txt);
/*    */   }
/*    */   
/*    */   public static void greenln(Object txt) {
/* 41 */     colorln("\033[32m", txt);
/*    */   }
/*    */   
/*    */   public static void purpleln(Object txt) {
/* 45 */     colorln("\033[35m", txt);
/*    */   }
/*    */   
/*    */   public static void yellowln(Object txt) {
/* 49 */     colorln("\033[33m", txt);
/*    */   }
/*    */   
/*    */   public static void colorln(String color, Object s) {
/* 53 */     if (IS_WINDOWS) {
/* 54 */       System.out.println(s);
/*    */     } else {
/* 56 */       System.out.println(color + s);
/* 57 */       System.out.print("\033[0m");
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public static void debug(Object content) {
/* 63 */     System.out.print("[Solon] ");
/* 64 */     blueln(content);
/*    */   }
/*    */   
/*    */   public static void debug(String label, Object content) {
/* 68 */     System.out.print("[Solon] ");
/* 69 */     blueln(label + ": " + content);
/*    */   }
/*    */   
/*    */   public static void info(Object content) {
/* 73 */     System.out.println("[Solon] " + content);
/*    */   }
/*    */   
/*    */   public static void info(String label, Object content) {
/* 77 */     System.out.print("[Solon] ");
/* 78 */     greenln(label + ": " + content);
/*    */   }
/*    */   
/*    */   public static void warn(Object content) {
/* 82 */     System.out.print("[Solon] ");
/* 83 */     yellowln(content);
/*    */   }
/*    */   
/*    */   public static void warn(String label, Object content) {
/* 87 */     System.out.print("[Solon] ");
/* 88 */     yellowln(label + ": " + content);
/*    */   }
/*    */   
/*    */   public static void error(Object content) {
/* 92 */     System.out.print("[Solon] ");
/* 93 */     redln(content);
/*    */   }
/*    */   
/*    */   public static void error(String label, Object content) {
/* 97 */     System.out.print("[Solon] ");
/* 98 */     redln(label + ": " + content);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\cor\\util\PrintUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */