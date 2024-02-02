/*     */ package com.mysql.cj.protocol;
/*     */ 
/*     */ import com.mysql.cj.exceptions.CJOperationNotSupportedException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public interface ServerSessionStateController
/*     */ {
/*     */   public static final int SESSION_TRACK_SYSTEM_VARIABLES = 0;
/*     */   public static final int SESSION_TRACK_SCHEMA = 1;
/*     */   public static final int SESSION_TRACK_STATE_CHANGE = 2;
/*     */   public static final int SESSION_TRACK_GTIDS = 3;
/*     */   public static final int SESSION_TRACK_TRANSACTION_CHARACTERISTICS = 4;
/*     */   public static final int SESSION_TRACK_TRANSACTION_STATE = 5;
/*     */   
/*     */   default void setSessionStateChanges(ServerSessionStateChanges changes) {
/*  54 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
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
/*     */   default ServerSessionStateChanges getSessionStateChanges() {
/*  68 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
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
/*     */   default void addSessionStateChangesListener(SessionStateChangesListener l) {
/*  83 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void removeSessionStateChangesListener(SessionStateChangesListener l) {
/*  93 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
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
/*     */   public static class SessionStateChange
/*     */   {
/*     */     private int type;
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
/* 127 */     private List<String> values = new ArrayList<>();
/*     */     
/*     */     public SessionStateChange(int type) {
/* 130 */       this.type = type;
/*     */     }
/*     */     
/*     */     public int getType() {
/* 134 */       return this.type;
/*     */     }
/*     */     
/*     */     public List<String> getValues() {
/* 138 */       return this.values;
/*     */     }
/*     */     
/*     */     public SessionStateChange addValue(String value) {
/* 142 */       this.values.add(value);
/* 143 */       return this;
/*     */     }
/*     */   }
/*     */   
/*     */   public static interface ServerSessionStateChanges {
/*     */     List<ServerSessionStateController.SessionStateChange> getSessionStateChangesList();
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   public static interface SessionStateChangesListener {
/*     */     void handleSessionStateChanges(ServerSessionStateController.ServerSessionStateChanges param1ServerSessionStateChanges);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\ServerSessionStateController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */