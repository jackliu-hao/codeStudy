/*     */ package com.mysql.cj;
/*     */ 
/*     */ import com.mysql.cj.conf.HostInfo;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.exceptions.OperationCancelledException;
/*     */ import com.mysql.cj.protocol.a.NativeMessageBuilder;
/*     */ import java.util.TimerTask;
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
/*     */ public class CancelQueryTaskImpl
/*     */   extends TimerTask
/*     */   implements CancelQueryTask
/*     */ {
/*     */   Query queryToCancel;
/*  50 */   Throwable caughtWhileCancelling = null;
/*     */   boolean queryTimeoutKillsConnection = false;
/*     */   
/*     */   public CancelQueryTaskImpl(Query cancellee) {
/*  54 */     this.queryToCancel = cancellee;
/*  55 */     NativeSession session = (NativeSession)cancellee.getSession();
/*  56 */     this.queryTimeoutKillsConnection = ((Boolean)session.getPropertySet().getBooleanProperty(PropertyKey.queryTimeoutKillsConnection).getValue()).booleanValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean cancel() {
/*  61 */     boolean res = super.cancel();
/*  62 */     this.queryToCancel = null;
/*  63 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*  69 */     Thread cancelThread = new Thread()
/*     */       {
/*     */         public void run()
/*     */         {
/*  73 */           Query localQueryToCancel = CancelQueryTaskImpl.this.queryToCancel;
/*  74 */           if (localQueryToCancel == null) {
/*     */             return;
/*     */           }
/*  77 */           NativeSession session = (NativeSession)localQueryToCancel.getSession();
/*  78 */           if (session == null) {
/*     */             return;
/*     */           }
/*     */           
/*     */           try {
/*  83 */             if (CancelQueryTaskImpl.this.queryTimeoutKillsConnection) {
/*  84 */               localQueryToCancel.setCancelStatus(Query.CancelStatus.CANCELED_BY_TIMEOUT);
/*  85 */               session.invokeCleanupListeners((Throwable)new OperationCancelledException(Messages.getString("Statement.ConnectionKilledDueToTimeout")));
/*     */             } else {
/*  87 */               synchronized (localQueryToCancel.getCancelTimeoutMutex()) {
/*  88 */                 long origConnId = session.getThreadId();
/*  89 */                 HostInfo hostInfo = session.getHostInfo();
/*  90 */                 String database = hostInfo.getDatabase();
/*  91 */                 String user = hostInfo.getUser();
/*  92 */                 String password = hostInfo.getPassword();
/*     */                 
/*  94 */                 NativeSession newSession = null;
/*     */                 try {
/*  96 */                   newSession = new NativeSession(hostInfo, session.getPropertySet());
/*  97 */                   newSession.connect(hostInfo, user, password, database, 30000, new TransactionEventHandler()
/*     */                       {
/*     */                         public void transactionCompleted() {}
/*     */ 
/*     */ 
/*     */                         
/*     */                         public void transactionBegun() {}
/*     */                       });
/* 105 */                   newSession.sendCommand((new NativeMessageBuilder(newSession.getServerSession().supportsQueryAttributes()))
/* 106 */                       .buildComQuery(newSession.getSharedSendPacket(), "KILL QUERY " + origConnId), false, 0);
/*     */                 } finally {
/*     */                   try {
/* 109 */                     newSession.forceClose();
/* 110 */                   } catch (Throwable throwable) {}
/*     */                 } 
/*     */ 
/*     */                 
/* 114 */                 localQueryToCancel.setCancelStatus(Query.CancelStatus.CANCELED_BY_TIMEOUT);
/*     */               
/*     */               }
/*     */             
/*     */             }
/*     */           
/*     */           }
/* 121 */           catch (Throwable t) {
/* 122 */             CancelQueryTaskImpl.this.caughtWhileCancelling = t;
/*     */           } finally {
/* 124 */             CancelQueryTaskImpl.this.setQueryToCancel(null);
/*     */           } 
/*     */         }
/*     */       };
/*     */     
/* 129 */     cancelThread.start();
/*     */   }
/*     */   
/*     */   public Throwable getCaughtWhileCancelling() {
/* 133 */     return this.caughtWhileCancelling;
/*     */   }
/*     */   
/*     */   public void setCaughtWhileCancelling(Throwable caughtWhileCancelling) {
/* 137 */     this.caughtWhileCancelling = caughtWhileCancelling;
/*     */   }
/*     */   
/*     */   public Query getQueryToCancel() {
/* 141 */     return this.queryToCancel;
/*     */   }
/*     */   
/*     */   public void setQueryToCancel(Query queryToCancel) {
/* 145 */     this.queryToCancel = queryToCancel;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\CancelQueryTaskImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */