/*    */ package io.undertow.server.handlers.builder;
/*    */ 
/*    */ import io.undertow.server.HandlerWrapper;
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
/*    */ public class HandlerParser
/*    */ {
/*    */   public static HandlerWrapper parse(String string, ClassLoader classLoader) {
/* 43 */     return PredicatedHandlersParser.parseHandler(string, classLoader);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\builder\HandlerParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */