/*     */ package org.h2.engine;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import org.h2.command.Command;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.security.auth.AuthenticationException;
/*     */ import org.h2.security.auth.AuthenticationInfo;
/*     */ import org.h2.security.auth.Authenticator;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.util.MathUtils;
/*     */ import org.h2.util.ParserUtil;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.ThreadDeadlockDetector;
/*     */ import org.h2.util.TimeZoneProvider;
/*     */ import org.h2.util.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Engine
/*     */ {
/*  35 */   private static final Map<String, DatabaseHolder> DATABASES = new HashMap<>();
/*     */   
/*  37 */   private static volatile long WRONG_PASSWORD_DELAY = SysProperties.DELAY_WRONG_PASSWORD_MIN;
/*     */   
/*     */   private static boolean JMX;
/*     */   
/*     */   static {
/*  42 */     if (SysProperties.THREAD_DEADLOCK_DETECTOR)
/*  43 */       ThreadDeadlockDetector.init(); 
/*     */   }
/*     */   
/*     */   private static SessionLocal openSession(ConnectionInfo paramConnectionInfo, boolean paramBoolean1, boolean paramBoolean2, String paramString) {
/*     */     Database database;
/*     */     DatabaseHolder databaseHolder;
/*  49 */     String str = paramConnectionInfo.getName();
/*     */     
/*  51 */     paramConnectionInfo.removeProperty("NO_UPGRADE", false);
/*  52 */     boolean bool = paramConnectionInfo.getProperty("OPEN_NEW", false);
/*  53 */     boolean bool1 = false;
/*  54 */     User user = null;
/*     */     
/*  56 */     if (!paramConnectionInfo.isUnnamedInMemory()) {
/*  57 */       synchronized (DATABASES) {
/*  58 */         databaseHolder = DATABASES.computeIfAbsent(str, paramString -> new DatabaseHolder());
/*     */       } 
/*     */     } else {
/*  61 */       databaseHolder = new DatabaseHolder();
/*     */     } 
/*  63 */     synchronized (databaseHolder) {
/*  64 */       database = databaseHolder.database;
/*  65 */       if (database == null || bool) {
/*  66 */         if (paramConnectionInfo.isPersistent()) {
/*  67 */           String str2, str1 = paramConnectionInfo.getProperty("MV_STORE");
/*     */           
/*  69 */           if (str1 == null) {
/*  70 */             str2 = str + ".mv.db";
/*  71 */             if (!FileUtils.exists(str2)) {
/*  72 */               throwNotFound(paramBoolean1, paramBoolean2, str);
/*  73 */               str2 = str + ".data.db";
/*  74 */               if (FileUtils.exists(str2)) {
/*  75 */                 throw DbException.getFileVersionError(str2);
/*     */               }
/*  77 */               str2 = null;
/*     */             } 
/*     */           } else {
/*  80 */             str2 = str + ".mv.db";
/*  81 */             if (!FileUtils.exists(str2)) {
/*  82 */               throwNotFound(paramBoolean1, paramBoolean2, str);
/*  83 */               str2 = null;
/*     */             } 
/*     */           } 
/*  86 */           if (str2 != null && !FileUtils.canWrite(str2)) {
/*  87 */             paramConnectionInfo.setProperty("ACCESS_MODE_DATA", "r");
/*     */           }
/*     */         } else {
/*  90 */           throwNotFound(paramBoolean1, paramBoolean2, str);
/*     */         } 
/*  92 */         database = new Database(paramConnectionInfo, paramString);
/*  93 */         bool1 = true;
/*  94 */         boolean bool2 = false;
/*  95 */         for (RightOwner rightOwner : database.getAllUsersAndRoles()) {
/*  96 */           if (rightOwner instanceof User) {
/*  97 */             bool2 = true;
/*     */             break;
/*     */           } 
/*     */         } 
/* 101 */         if (!bool2) {
/*     */ 
/*     */           
/* 104 */           user = new User(database, database.allocateObjectId(), paramConnectionInfo.getUserName(), false);
/* 105 */           user.setAdmin(true);
/* 106 */           user.setUserPasswordHash(paramConnectionInfo.getUserPasswordHash());
/* 107 */           database.setMasterUser(user);
/*     */         } 
/* 109 */         databaseHolder.database = database;
/*     */       } 
/*     */     } 
/*     */     
/* 113 */     if (bool1)
/*     */     {
/*     */ 
/*     */       
/* 117 */       database.opened();
/*     */     }
/* 119 */     if (database.isClosing()) {
/* 120 */       return null;
/*     */     }
/* 122 */     if (user == null) {
/* 123 */       if (database.validateFilePasswordHash(paramString, paramConnectionInfo.getFilePasswordHash())) {
/* 124 */         if (paramConnectionInfo.getProperty("AUTHREALM") == null) {
/* 125 */           user = database.findUser(paramConnectionInfo.getUserName());
/* 126 */           if (user != null && 
/* 127 */             !user.validateUserPasswordHash(paramConnectionInfo.getUserPasswordHash())) {
/* 128 */             user = null;
/*     */           }
/*     */         } else {
/*     */           
/* 132 */           Authenticator authenticator = database.getAuthenticator();
/* 133 */           if (authenticator == null) {
/* 134 */             throw DbException.get(90144, str);
/*     */           }
/*     */           try {
/* 137 */             AuthenticationInfo authenticationInfo = new AuthenticationInfo(paramConnectionInfo);
/* 138 */             user = database.getAuthenticator().authenticate(authenticationInfo, database);
/* 139 */           } catch (AuthenticationException authenticationException) {
/* 140 */             database.getTrace(2).error((Throwable)authenticationException, "an error occurred during authentication; user: \"" + paramConnectionInfo
/*     */                 
/* 142 */                 .getUserName() + "\"");
/*     */           } 
/*     */         } 
/*     */       }
/*     */       
/* 147 */       if (bool1 && (user == null || !user.isAdmin()))
/*     */       {
/*     */         
/* 150 */         database.setEventListener(null);
/*     */       }
/*     */     } 
/* 153 */     if (user == null) {
/* 154 */       DbException dbException = DbException.get(28000);
/* 155 */       database.getTrace(2).error((Throwable)dbException, "wrong user or password; user: \"" + paramConnectionInfo
/* 156 */           .getUserName() + "\"");
/* 157 */       database.removeSession(null);
/* 158 */       throw dbException;
/*     */     } 
/*     */     
/* 161 */     paramConnectionInfo.cleanAuthenticationInfo();
/* 162 */     checkClustering(paramConnectionInfo, database);
/* 163 */     SessionLocal sessionLocal = database.createSession(user, paramConnectionInfo.getNetworkConnectionInfo());
/* 164 */     if (sessionLocal == null)
/*     */     {
/* 166 */       return null;
/*     */     }
/* 168 */     if (paramConnectionInfo.getProperty("OLD_INFORMATION_SCHEMA", false)) {
/* 169 */       sessionLocal.setOldInformationSchema(true);
/*     */     }
/* 171 */     if (paramConnectionInfo.getProperty("JMX", false)) {
/*     */       try {
/* 173 */         Utils.callStaticMethod("org.h2.jmx.DatabaseInfo.registerMBean", new Object[] { paramConnectionInfo, database });
/*     */       }
/* 175 */       catch (Exception exception) {
/* 176 */         database.removeSession(sessionLocal);
/* 177 */         throw DbException.get(50100, exception, new String[] { "JMX" });
/*     */       } 
/* 179 */       JMX = true;
/*     */     } 
/* 181 */     return sessionLocal;
/*     */   }
/*     */   
/*     */   private static void throwNotFound(boolean paramBoolean1, boolean paramBoolean2, String paramString) {
/* 185 */     if (paramBoolean1) {
/* 186 */       throw DbException.get(90146, paramString);
/*     */     }
/* 188 */     if (paramBoolean2) {
/* 189 */       throw DbException.get(90149, paramString);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SessionLocal createSession(ConnectionInfo paramConnectionInfo) {
/*     */     try {
/* 201 */       SessionLocal sessionLocal = openSession(paramConnectionInfo);
/* 202 */       validateUserAndPassword(true);
/* 203 */       return sessionLocal;
/* 204 */     } catch (DbException dbException) {
/* 205 */       if (dbException.getErrorCode() == 28000) {
/* 206 */         validateUserAndPassword(false);
/*     */       }
/* 208 */       throw dbException;
/*     */     } 
/*     */   }
/*     */   private static SessionLocal openSession(ConnectionInfo paramConnectionInfo) {
/*     */     SessionLocal sessionLocal;
/* 213 */     boolean bool1 = paramConnectionInfo.removeProperty("IFEXISTS", false);
/* 214 */     boolean bool2 = paramConnectionInfo.removeProperty("FORBID_CREATION", false);
/* 215 */     boolean bool3 = paramConnectionInfo.removeProperty("IGNORE_UNKNOWN_SETTINGS", false);
/*     */     
/* 217 */     String str1 = paramConnectionInfo.removeProperty("CIPHER", (String)null);
/* 218 */     String str2 = paramConnectionInfo.removeProperty("INIT", (String)null);
/*     */     
/* 220 */     long l = System.nanoTime();
/*     */     while (true) {
/* 222 */       sessionLocal = openSession(paramConnectionInfo, bool1, bool2, str1);
/* 223 */       if (sessionLocal != null) {
/*     */         break;
/*     */       }
/*     */ 
/*     */       
/* 228 */       if (System.nanoTime() - l > 60000000000L) {
/* 229 */         throw DbException.get(90020, "Waited for database closing longer than 1 minute");
/*     */       }
/*     */       
/*     */       try {
/* 233 */         Thread.sleep(1L);
/* 234 */       } catch (InterruptedException interruptedException) {
/* 235 */         throw DbException.get(90121);
/*     */       } 
/*     */     } 
/* 238 */     synchronized (sessionLocal) {
/* 239 */       sessionLocal.setAllowLiterals(true);
/* 240 */       DbSettings dbSettings = DbSettings.DEFAULT;
/* 241 */       for (String str : paramConnectionInfo.getKeys()) {
/* 242 */         if (!dbSettings.containsKey(str)) {
/*     */ 
/*     */ 
/*     */           
/* 246 */           String str3 = paramConnectionInfo.getProperty(str);
/* 247 */           StringBuilder stringBuilder = (new StringBuilder("SET ")).append(str).append(' ');
/* 248 */           if (!ParserUtil.isSimpleIdentifier(str, false, false)) {
/* 249 */             if (!str.equalsIgnoreCase("TIME ZONE")) {
/* 250 */               throw DbException.get(90113, str);
/*     */             }
/* 252 */             StringUtils.quoteStringSQL(stringBuilder, str3);
/*     */           } else {
/* 254 */             stringBuilder.append(str3);
/*     */           } 
/*     */           try {
/* 257 */             Command command = sessionLocal.prepareLocal(stringBuilder.toString());
/* 258 */             command.executeUpdate(null);
/* 259 */           } catch (DbException dbException) {
/* 260 */             if (dbException.getErrorCode() == 90040) {
/* 261 */               sessionLocal.getTrace().error((Throwable)dbException, "admin rights required; user: \"" + paramConnectionInfo
/* 262 */                   .getUserName() + "\"");
/*     */             } else {
/* 264 */               sessionLocal.getTrace().error((Throwable)dbException, "");
/*     */             } 
/* 266 */             if (!bool3) {
/* 267 */               sessionLocal.close();
/* 268 */               throw dbException;
/*     */             } 
/*     */           } 
/*     */         } 
/* 272 */       }  TimeZoneProvider timeZoneProvider = paramConnectionInfo.getTimeZone();
/* 273 */       if (timeZoneProvider != null) {
/* 274 */         sessionLocal.setTimeZone(timeZoneProvider);
/*     */       }
/* 276 */       if (str2 != null) {
/*     */         try {
/* 278 */           Command command = sessionLocal.prepareLocal(str2);
/* 279 */           command.executeUpdate(null);
/* 280 */         } catch (DbException dbException) {
/* 281 */           if (!bool3) {
/* 282 */             sessionLocal.close();
/* 283 */             throw dbException;
/*     */           } 
/*     */         } 
/*     */       }
/* 287 */       sessionLocal.setAllowLiterals(false);
/* 288 */       sessionLocal.commit(true);
/*     */     } 
/* 290 */     return sessionLocal;
/*     */   }
/*     */   
/*     */   private static void checkClustering(ConnectionInfo paramConnectionInfo, Database paramDatabase) {
/* 294 */     String str1 = paramConnectionInfo.getProperty(12, (String)null);
/* 295 */     if ("''".equals(str1)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 300 */     String str2 = paramDatabase.getCluster();
/* 301 */     if (!"''".equals(str2) && 
/* 302 */       !"TRUE".equals(str1) && 
/* 303 */       !Objects.equals(str1, str2)) {
/* 304 */       if (str2.equals("''")) {
/* 305 */         throw DbException.get(90093);
/*     */       }
/*     */       
/* 308 */       throw DbException.get(90094, str2);
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
/*     */   static void close(String paramString) {
/* 323 */     if (JMX) {
/*     */       try {
/* 325 */         Utils.callStaticMethod("org.h2.jmx.DatabaseInfo.unregisterMBean", new Object[] { paramString });
/* 326 */       } catch (Exception exception) {
/* 327 */         throw DbException.get(50100, exception, new String[] { "JMX" });
/*     */       } 
/*     */     }
/* 330 */     synchronized (DATABASES) {
/* 331 */       DATABASES.remove(paramString);
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
/*     */   private static void validateUserAndPassword(boolean paramBoolean) {
/* 353 */     int i = SysProperties.DELAY_WRONG_PASSWORD_MIN;
/* 354 */     if (paramBoolean) {
/* 355 */       long l = WRONG_PASSWORD_DELAY;
/* 356 */       if (l > i && l > 0L)
/*     */       {
/*     */         
/* 359 */         synchronized (Engine.class)
/*     */         {
/*     */           
/* 362 */           l = MathUtils.secureRandomInt((int)l);
/*     */           try {
/* 364 */             Thread.sleep(l);
/* 365 */           } catch (InterruptedException interruptedException) {}
/*     */ 
/*     */           
/* 368 */           WRONG_PASSWORD_DELAY = i;
/*     */         }
/*     */       
/*     */       }
/*     */     } else {
/*     */       
/* 374 */       synchronized (Engine.class) {
/* 375 */         long l = WRONG_PASSWORD_DELAY;
/* 376 */         int j = SysProperties.DELAY_WRONG_PASSWORD_MAX;
/* 377 */         if (j <= 0) {
/* 378 */           j = Integer.MAX_VALUE;
/*     */         }
/* 380 */         WRONG_PASSWORD_DELAY += WRONG_PASSWORD_DELAY;
/* 381 */         if (WRONG_PASSWORD_DELAY > j || WRONG_PASSWORD_DELAY < 0L) {
/* 382 */           WRONG_PASSWORD_DELAY = j;
/*     */         }
/* 384 */         if (i > 0) {
/*     */           
/* 386 */           l += Math.abs(MathUtils.secureRandomLong() % 100L);
/*     */           try {
/* 388 */             Thread.sleep(l);
/* 389 */           } catch (InterruptedException interruptedException) {}
/*     */         } 
/*     */ 
/*     */         
/* 393 */         throw DbException.get(28000);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static final class DatabaseHolder {
/*     */     volatile Database database;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\Engine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */