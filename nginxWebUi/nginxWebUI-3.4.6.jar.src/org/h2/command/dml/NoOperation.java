/*    */ package org.h2.command.dml;
/*    */ 
/*    */ import org.h2.command.Prepared;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.result.ResultInterface;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NoOperation
/*    */   extends Prepared
/*    */ {
/*    */   public NoOperation(SessionLocal paramSessionLocal) {
/* 19 */     super(paramSessionLocal);
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 24 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isTransactional() {
/* 29 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean needRecompile() {
/* 34 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isReadOnly() {
/* 39 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public ResultInterface queryMeta() {
/* 44 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 49 */     return 63;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\NoOperation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */