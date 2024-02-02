/*    */ package org.h2.engine;
/*    */ 
/*    */ import org.h2.command.Prepared;
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
/*    */ public class Procedure
/*    */ {
/*    */   private final String name;
/*    */   private final Prepared prepared;
/*    */   
/*    */   public Procedure(String paramString, Prepared paramPrepared) {
/* 20 */     this.name = paramString;
/* 21 */     this.prepared = paramPrepared;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 25 */     return this.name;
/*    */   }
/*    */   
/*    */   public Prepared getPrepared() {
/* 29 */     return this.prepared;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\Procedure.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */