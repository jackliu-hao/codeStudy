/*     */ package org.h2.command.dml;
/*     */ 
/*     */ import org.h2.command.Prepared;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.ResultInterface;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TransactionCommand
/*     */   extends Prepared
/*     */ {
/*     */   private final int type;
/*     */   private String savepointName;
/*     */   private String transactionName;
/*     */   
/*     */   public TransactionCommand(SessionLocal paramSessionLocal, int paramInt) {
/*  25 */     super(paramSessionLocal);
/*  26 */     this.type = paramInt;
/*     */   }
/*     */   
/*     */   public void setSavepointName(String paramString) {
/*  30 */     this.savepointName = paramString;
/*     */   }
/*     */   
/*     */   public long update() {
/*     */     Database database;
/*  35 */     switch (this.type) {
/*     */       case 69:
/*  37 */         this.session.setAutoCommit(true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  98 */         return 0L;case 70: this.session.setAutoCommit(false); return 0L;case 83: this.session.begin(); return 0L;case 71: this.session.commit(false); return 0L;case 72: this.session.rollback(); return 0L;case 73: this.session.getUser().checkAdmin(); this.session.getDatabase().checkpoint(); return 0L;case 74: this.session.addSavepoint(this.savepointName); return 0L;case 75: this.session.rollbackToSavepoint(this.savepointName); return 0L;case 76: this.session.getUser().checkAdmin(); this.session.getDatabase().sync(); return 0L;case 77: this.session.prepareCommit(this.transactionName); return 0L;case 78: this.session.getUser().checkAdmin(); this.session.setPreparedTransaction(this.transactionName, true); return 0L;case 79: this.session.getUser().checkAdmin(); this.session.setPreparedTransaction(this.transactionName, false); return 0L;case 80: case 82: case 84: this.session.commit(false);case 81: this.session.getUser().checkAdmin(); this.session.throttle(); database = this.session.getDatabase(); if (database.setExclusiveSession(this.session, true)) { database.setCompactMode(this.type); database.setCloseDelay(0); this.session.close(); }  return 0L;
/*     */     } 
/*     */     throw DbException.getInternalError("type=" + this.type);
/*     */   }
/*     */   public boolean isTransactional() {
/* 103 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needRecompile() {
/* 108 */     return false;
/*     */   }
/*     */   
/*     */   public void setTransactionName(String paramString) {
/* 112 */     this.transactionName = paramString;
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface queryMeta() {
/* 117 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 122 */     return this.type;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCacheable() {
/* 127 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\TransactionCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */