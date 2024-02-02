/*    */ package org.slf4j.impl;
/*    */ 
/*    */ import ch.qos.logback.classic.util.LogbackMDCAdapter;
/*    */ import org.slf4j.spi.MDCAdapter;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StaticMDCBinder
/*    */ {
/* 30 */   public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MDCAdapter getMDCA() {
/* 40 */     return (MDCAdapter)new LogbackMDCAdapter();
/*    */   }
/*    */   
/*    */   public String getMDCAdapterClassStr() {
/* 44 */     return LogbackMDCAdapter.class.getName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\slf4j\impl\StaticMDCBinder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */