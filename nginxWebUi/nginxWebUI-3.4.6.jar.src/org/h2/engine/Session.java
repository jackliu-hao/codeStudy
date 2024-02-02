/*     */ package org.h2.engine;
/*     */ import org.h2.command.CommandInterface;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.util.TimeZoneProvider;
/*     */ 
/*     */ public abstract class Session implements CastDataProvider, AutoCloseable {
/*     */   private ArrayList<String> sessionState;
/*     */   boolean sessionStateChanged;
/*     */   private boolean sessionStateUpdating;
/*     */   volatile StaticSettings staticSettings;
/*     */   
/*     */   public abstract ArrayList<String> getClusterServers();
/*     */   
/*     */   public abstract CommandInterface prepareCommand(String paramString, int paramInt);
/*     */   
/*     */   public abstract void close();
/*     */   
/*     */   public abstract Trace getTrace();
/*     */   
/*     */   public abstract boolean isClosed();
/*     */   
/*     */   public abstract DataHandler getDataHandler();
/*     */   
/*     */   public abstract boolean hasPendingTransaction();
/*     */   
/*     */   public abstract void cancel();
/*     */   
/*     */   public abstract boolean getAutoCommit();
/*     */   
/*     */   public abstract void setAutoCommit(boolean paramBoolean);
/*     */   
/*     */   public abstract ValueLob addTemporaryLob(ValueLob paramValueLob);
/*     */   
/*     */   public abstract boolean isRemote();
/*     */   
/*     */   public abstract void setCurrentSchemaName(String paramString);
/*     */   
/*     */   public abstract String getCurrentSchemaName();
/*     */   
/*     */   public abstract void setNetworkConnectionInfo(NetworkConnectionInfo paramNetworkConnectionInfo);
/*     */   
/*     */   public abstract IsolationLevel getIsolationLevel();
/*     */   
/*     */   public abstract void setIsolationLevel(IsolationLevel paramIsolationLevel);
/*     */   
/*     */   public abstract StaticSettings getStaticSettings();
/*     */   
/*     */   public abstract DynamicSettings getDynamicSettings();
/*     */   
/*     */   public abstract DatabaseMeta getDatabaseMeta();
/*     */   
/*     */   public abstract boolean isOldInformationSchema();
/*     */   
/*     */   public static final class StaticSettings {
/*     */     public StaticSettings(boolean param1Boolean1, boolean param1Boolean2, boolean param1Boolean3) {
/*  56 */       this.databaseToUpper = param1Boolean1;
/*  57 */       this.databaseToLower = param1Boolean2;
/*  58 */       this.caseInsensitiveIdentifiers = param1Boolean3;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final boolean databaseToUpper;
/*     */ 
/*     */ 
/*     */     
/*     */     public final boolean databaseToLower;
/*     */ 
/*     */ 
/*     */     
/*     */     public final boolean caseInsensitiveIdentifiers;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class DynamicSettings
/*     */   {
/*     */     public final Mode mode;
/*     */ 
/*     */     
/*     */     public final TimeZoneProvider timeZone;
/*     */ 
/*     */ 
/*     */     
/*     */     public DynamicSettings(Mode param1Mode, TimeZoneProvider param1TimeZoneProvider) {
/*  87 */       this.mode = param1Mode;
/*  88 */       this.timeZone = param1TimeZoneProvider;
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
/*     */   void recreateSessionState() {
/* 259 */     if (this.sessionState != null && !this.sessionState.isEmpty()) {
/* 260 */       this.sessionStateUpdating = true;
/*     */       try {
/* 262 */         for (String str : this.sessionState) {
/* 263 */           CommandInterface commandInterface = prepareCommand(str, 2147483647);
/* 264 */           commandInterface.executeUpdate(null);
/*     */         } 
/*     */       } finally {
/* 267 */         this.sessionStateUpdating = false;
/* 268 */         this.sessionStateChanged = false;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void readSessionState() {
/* 277 */     if (!this.sessionStateChanged || this.sessionStateUpdating) {
/*     */       return;
/*     */     }
/* 280 */     this.sessionStateChanged = false;
/* 281 */     this.sessionState = Utils.newSmallArrayList();
/* 282 */     CommandInterface commandInterface = prepareCommand(!isOldInformationSchema() ? "SELECT STATE_COMMAND FROM INFORMATION_SCHEMA.SESSION_STATE" : "SELECT SQL FROM INFORMATION_SCHEMA.SESSION_STATE", 2147483647);
/*     */ 
/*     */     
/* 285 */     ResultInterface resultInterface = commandInterface.executeQuery(0L, false);
/* 286 */     while (resultInterface.next()) {
/* 287 */       this.sessionState.add(resultInterface.currentRow()[0].getString());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Session setThreadLocalSession() {
/* 298 */     return null;
/*     */   }
/*     */   
/*     */   public void resetThreadLocalSession(Session paramSession) {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\Session.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */