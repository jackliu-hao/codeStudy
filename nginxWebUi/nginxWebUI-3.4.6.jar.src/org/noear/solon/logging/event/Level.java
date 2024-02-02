/*    */ package org.noear.solon.logging.event;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum Level
/*    */ {
/* 10 */   TRACE(10),
/* 11 */   DEBUG(20),
/* 12 */   INFO(30),
/* 13 */   WARN(40),
/* 14 */   ERROR(50);
/*    */   
/*    */   public final int code;
/*    */   
/*    */   public static Level of(int code, Level def) {
/* 19 */     for (Level v : values()) {
/* 20 */       if (v.code == code) {
/* 21 */         return v;
/*    */       }
/*    */     } 
/*    */     
/* 25 */     return def;
/*    */   }
/*    */   
/*    */   public static Level of(String name, Level def) {
/* 29 */     if (name == null || name.length() == 0) {
/* 30 */       return def;
/*    */     }
/*    */     
/* 33 */     switch (name.toUpperCase()) {
/*    */       case "TRACE":
/* 35 */         return TRACE;
/*    */       case "DEBUG":
/* 37 */         return DEBUG;
/*    */       case "INFO":
/* 39 */         return INFO;
/*    */       case "WARN":
/* 41 */         return WARN;
/*    */       case "ERROR":
/* 43 */         return ERROR;
/*    */     } 
/* 45 */     return def;
/*    */   }
/*    */ 
/*    */   
/*    */   Level(int code) {
/* 50 */     this.code = code;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\logging\event\Level.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */