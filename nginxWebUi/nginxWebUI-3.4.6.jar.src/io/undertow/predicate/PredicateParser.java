/*    */ package io.undertow.predicate;
/*    */ 
/*    */ import io.undertow.server.handlers.builder.PredicatedHandlersParser;
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
/*    */ public class PredicateParser
/*    */ {
/*    */   public static final Predicate parse(String string, ClassLoader classLoader) {
/* 53 */     return PredicatedHandlersParser.parsePredicate(string, classLoader);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\PredicateParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */