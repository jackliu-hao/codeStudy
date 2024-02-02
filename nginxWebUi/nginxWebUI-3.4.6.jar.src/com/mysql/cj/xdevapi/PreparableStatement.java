/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.MysqlxSession;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.x.XMessage;
/*     */ import com.mysql.cj.protocol.x.XMessageBuilder;
/*     */ import com.mysql.cj.protocol.x.XProtocolError;
/*     */ import java.lang.ref.PhantomReference;
/*     */ import java.lang.ref.ReferenceQueue;
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
/*     */ public abstract class PreparableStatement<RES_T>
/*     */ {
/*     */   protected enum PreparedState
/*     */   {
/*  48 */     UNSUPPORTED,
/*  49 */     UNPREPARED,
/*  50 */     SUSPENDED,
/*  51 */     PREPARED,
/*  52 */     PREPARE,
/*  53 */     DEALLOCATE,
/*  54 */     REPREPARE;
/*     */   }
/*     */   
/*  57 */   protected int preparedStatementId = 0;
/*  58 */   protected PreparedState preparedState = PreparedState.UNPREPARED;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected MysqlxSession mysqlxSession;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected XMessageBuilder getMessageBuilder() {
/*  69 */     return (XMessageBuilder)this.mysqlxSession.getMessageBuilder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void resetPrepareState() {
/*  76 */     if (this.preparedState == PreparedState.PREPARED || this.preparedState == PreparedState.REPREPARE) {
/*  77 */       this.preparedState = PreparedState.DEALLOCATE;
/*  78 */     } else if (this.preparedState == PreparedState.PREPARE) {
/*  79 */       this.preparedState = PreparedState.UNPREPARED;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setReprepareState() {
/*  87 */     if (this.preparedState == PreparedState.PREPARED) {
/*  88 */       this.preparedState = PreparedState.REPREPARE;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RES_T execute() {
/*     */     while (true) {
/*     */       RES_T result;
/* 102 */       switch (this.preparedState) {
/*     */         
/*     */         case UNSUPPORTED:
/* 105 */           return executeStatement();
/*     */         
/*     */         case UNPREPARED:
/* 108 */           result = executeStatement();
/* 109 */           this.preparedState = PreparedState.PREPARE;
/* 110 */           return result;
/*     */ 
/*     */         
/*     */         case SUSPENDED:
/* 114 */           if (!this.mysqlxSession.supportsPreparedStatements()) {
/* 115 */             this.preparedState = PreparedState.UNSUPPORTED; continue;
/* 116 */           }  if (this.mysqlxSession.readyForPreparingStatements()) {
/* 117 */             this.preparedState = PreparedState.PREPARE; continue;
/*     */           } 
/* 119 */           return executeStatement();
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         case PREPARE:
/* 125 */           this.preparedState = prepareStatement() ? PreparedState.PREPARED : PreparedState.SUSPENDED;
/*     */ 
/*     */         
/*     */         case PREPARED:
/* 129 */           return executePreparedStatement();
/*     */         
/*     */         case DEALLOCATE:
/* 132 */           deallocatePrepared();
/* 133 */           this.preparedState = PreparedState.UNPREPARED;
/*     */ 
/*     */         
/*     */         case REPREPARE:
/* 137 */           deallocatePrepared();
/* 138 */           this.preparedState = PreparedState.PREPARE;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract RES_T executeStatement();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract XMessage getPrepareStatementXMessage();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean prepareStatement() {
/* 167 */     if (!this.mysqlxSession.supportsPreparedStatements()) {
/* 168 */       return false;
/*     */     }
/*     */     try {
/* 171 */       this.preparedStatementId = this.mysqlxSession.getNewPreparedStatementId(this);
/* 172 */       this.mysqlxSession.query((Message)getPrepareStatementXMessage(), new UpdateResultBuilder<>());
/* 173 */     } catch (XProtocolError e) {
/* 174 */       if (this.mysqlxSession.failedPreparingStatement(this.preparedStatementId, e)) {
/* 175 */         this.preparedStatementId = 0;
/* 176 */         return false;
/*     */       } 
/* 178 */       this.preparedStatementId = 0;
/* 179 */       throw e;
/* 180 */     } catch (Throwable t) {
/* 181 */       this.preparedStatementId = 0;
/* 182 */       throw t;
/*     */     } 
/* 184 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract RES_T executePreparedStatement();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void deallocatePrepared() {
/* 199 */     if (this.preparedState == PreparedState.PREPARED || this.preparedState == PreparedState.DEALLOCATE || this.preparedState == PreparedState.REPREPARE) {
/*     */       try {
/* 201 */         this.mysqlxSession.query((Message)getMessageBuilder().buildPrepareDeallocate(this.preparedStatementId), new UpdateResultBuilder<>());
/*     */       } finally {
/* 203 */         this.mysqlxSession.freePreparedStatementId(this.preparedStatementId);
/* 204 */         this.preparedStatementId = 0;
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class PreparableStatementFinalizer
/*     */     extends PhantomReference<PreparableStatement<?>>
/*     */   {
/*     */     int prepredStatementId;
/*     */ 
/*     */     
/*     */     public PreparableStatementFinalizer(PreparableStatement<?> referent, ReferenceQueue<? super PreparableStatement<?>> q, int preparedStatementId) {
/* 218 */       super(referent, q);
/* 219 */       this.prepredStatementId = preparedStatementId;
/*     */     }
/*     */     
/*     */     public int getPreparedStatementId() {
/* 223 */       return this.prepredStatementId;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\PreparableStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */