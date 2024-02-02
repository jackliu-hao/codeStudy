/*    */ package org.h2.command.ddl;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class DefineCommand
/*    */   extends Prepared
/*    */ {
/*    */   protected boolean transactional;
/*    */   
/*    */   DefineCommand(SessionLocal paramSessionLocal) {
/* 30 */     super(paramSessionLocal);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isReadOnly() {
/* 35 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public ResultInterface queryMeta() {
/* 40 */     return null;
/*    */   }
/*    */   
/*    */   public void setTransactional(boolean paramBoolean) {
/* 44 */     this.transactional = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isTransactional() {
/* 49 */     return this.transactional;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\DefineCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */