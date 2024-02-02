/*     */ package org.h2.jmx;
/*     */ 
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Map;
/*     */ import javax.management.JMException;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.ObjectName;
/*     */ import org.h2.command.Command;
/*     */ import org.h2.engine.ConnectionInfo;
/*     */ import org.h2.engine.Constants;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.util.NetworkConnectionInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DatabaseInfo
/*     */   implements DatabaseInfoMBean
/*     */ {
/*  32 */   private static final Map<String, ObjectName> MBEANS = new HashMap<>();
/*     */   
/*     */   private final Database database;
/*     */ 
/*     */   
/*     */   private DatabaseInfo(Database paramDatabase) {
/*  38 */     if (paramDatabase == null) {
/*  39 */       throw new IllegalArgumentException("Argument 'database' must not be null");
/*     */     }
/*  41 */     this.database = paramDatabase;
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
/*     */   private static ObjectName getObjectName(String paramString1, String paramString2) throws JMException {
/*  54 */     paramString1 = paramString1.replace(':', '_');
/*  55 */     paramString2 = paramString2.replace(':', '_');
/*  56 */     Hashtable<Object, Object> hashtable = new Hashtable<>();
/*  57 */     hashtable.put("name", paramString1);
/*  58 */     hashtable.put("path", paramString2);
/*  59 */     return new ObjectName("org.h2", (Hashtable)hashtable);
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
/*     */   public static void registerMBean(ConnectionInfo paramConnectionInfo, Database paramDatabase) throws JMException {
/*  71 */     String str = paramConnectionInfo.getName();
/*  72 */     if (!MBEANS.containsKey(str)) {
/*  73 */       MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
/*  74 */       String str1 = paramDatabase.getShortName();
/*  75 */       ObjectName objectName = getObjectName(str1, str);
/*  76 */       MBEANS.put(str, objectName);
/*  77 */       DatabaseInfo databaseInfo = new DatabaseInfo(paramDatabase);
/*  78 */       DocumentedMBean documentedMBean = new DocumentedMBean((T)databaseInfo, (Class)DatabaseInfoMBean.class);
/*  79 */       mBeanServer.registerMBean(documentedMBean, objectName);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void unregisterMBean(String paramString) throws Exception {
/*  90 */     ObjectName objectName = MBEANS.remove(paramString);
/*  91 */     if (objectName != null) {
/*  92 */       MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
/*  93 */       mBeanServer.unregisterMBean(objectName);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isExclusive() {
/*  99 */     return (this.database.getExclusiveSession() != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadOnly() {
/* 104 */     return this.database.isReadOnly();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMode() {
/* 109 */     return this.database.getMode().getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTraceLevel() {
/* 114 */     return this.database.getTraceSystem().getLevelFile();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTraceLevel(int paramInt) {
/* 119 */     this.database.getTraceSystem().setLevelFile(paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getFileWriteCount() {
/* 124 */     if (this.database.isPersistent()) {
/* 125 */       return this.database.getStore().getMvStore().getFileStore().getWriteCount();
/*     */     }
/* 127 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getFileReadCount() {
/* 132 */     if (this.database.isPersistent()) {
/* 133 */       return this.database.getStore().getMvStore().getFileStore().getReadCount();
/*     */     }
/* 135 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getFileSize() {
/* 140 */     long l = 0L;
/* 141 */     if (this.database.isPersistent()) {
/* 142 */       l = this.database.getStore().getMvStore().getFileStore().size();
/*     */     }
/* 144 */     return l / 1024L;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCacheSizeMax() {
/* 149 */     if (this.database.isPersistent()) {
/* 150 */       return this.database.getStore().getMvStore().getCacheSize() * 1024;
/*     */     }
/* 152 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCacheSizeMax(int paramInt) {
/* 157 */     if (this.database.isPersistent()) {
/* 158 */       this.database.setCacheSize(paramInt);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCacheSize() {
/* 164 */     if (this.database.isPersistent()) {
/* 165 */       return this.database.getStore().getMvStore().getCacheSizeUsed() * 1024;
/*     */     }
/* 167 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getVersion() {
/* 172 */     return Constants.FULL_VERSION;
/*     */   }
/*     */ 
/*     */   
/*     */   public String listSettings() {
/* 177 */     StringBuilder stringBuilder = new StringBuilder();
/* 178 */     for (Map.Entry entry : this.database.getSettings().getSortedSettings()) {
/* 179 */       stringBuilder.append((String)entry.getKey()).append(" = ").append((String)entry.getValue()).append('\n');
/*     */     }
/* 181 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String listSessions() {
/* 186 */     StringBuilder stringBuilder = new StringBuilder();
/* 187 */     for (SessionLocal sessionLocal : this.database.getSessions(false)) {
/* 188 */       stringBuilder.append("session id: ").append(sessionLocal.getId());
/* 189 */       stringBuilder.append(" user: ")
/* 190 */         .append(sessionLocal.getUser().getName())
/* 191 */         .append('\n');
/* 192 */       NetworkConnectionInfo networkConnectionInfo = sessionLocal.getNetworkConnectionInfo();
/* 193 */       if (networkConnectionInfo != null) {
/* 194 */         stringBuilder.append("server: ").append(networkConnectionInfo.getServer()).append('\n')
/* 195 */           .append("clientAddr: ").append(networkConnectionInfo.getClient()).append('\n');
/* 196 */         String str = networkConnectionInfo.getClientInfo();
/* 197 */         if (str != null) {
/* 198 */           stringBuilder.append("clientInfo: ").append(str).append('\n');
/*     */         }
/*     */       } 
/* 201 */       stringBuilder.append("connected: ")
/* 202 */         .append(sessionLocal.getSessionStart().getString())
/* 203 */         .append('\n');
/* 204 */       Command command = sessionLocal.getCurrentCommand();
/* 205 */       if (command != null) {
/* 206 */         stringBuilder.append("statement: ")
/* 207 */           .append(command)
/* 208 */           .append('\n')
/* 209 */           .append("started: ")
/* 210 */           .append(sessionLocal.getCommandStartOrEnd().getString())
/* 211 */           .append('\n');
/*     */       }
/* 213 */       for (Table table : sessionLocal.getLocks()) {
/* 214 */         if (table.isLockedExclusivelyBy(sessionLocal)) {
/* 215 */           stringBuilder.append("write lock on ");
/*     */         } else {
/* 217 */           stringBuilder.append("read lock on ");
/*     */         } 
/* 219 */         stringBuilder.append(table.getSchema().getName())
/* 220 */           .append('.').append(table.getName())
/* 221 */           .append('\n');
/*     */       } 
/* 223 */       stringBuilder.append('\n');
/*     */     } 
/* 225 */     return stringBuilder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jmx\DatabaseInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */