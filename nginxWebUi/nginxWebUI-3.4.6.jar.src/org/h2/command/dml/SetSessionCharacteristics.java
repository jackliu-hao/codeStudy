/*    */ package org.h2.command.dml;
/*    */ 
/*    */ import org.h2.command.Prepared;
/*    */ import org.h2.engine.IsolationLevel;
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
/*    */ public class SetSessionCharacteristics
/*    */   extends Prepared
/*    */ {
/*    */   private final IsolationLevel isolationLevel;
/*    */   
/*    */   public SetSessionCharacteristics(SessionLocal paramSessionLocal, IsolationLevel paramIsolationLevel) {
/* 22 */     super(paramSessionLocal);
/* 23 */     this.isolationLevel = paramIsolationLevel;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isTransactional() {
/* 28 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 33 */     this.session.setIsolationLevel(this.isolationLevel);
/* 34 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean needRecompile() {
/* 39 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public ResultInterface queryMeta() {
/* 44 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 49 */     return 67;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\SetSessionCharacteristics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */