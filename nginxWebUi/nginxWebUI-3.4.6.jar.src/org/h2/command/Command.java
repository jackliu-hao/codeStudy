/*     */ package org.h2.command;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Set;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.Mode;
/*     */ import org.h2.engine.Session;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.ParameterInterface;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.message.Trace;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.ResultWithGeneratedKeys;
/*     */ import org.h2.result.ResultWithPaddedStrings;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Command
/*     */   implements CommandInterface
/*     */ {
/*     */   protected final SessionLocal session;
/*     */   protected long startTimeNanos;
/*     */   private final Trace trace;
/*     */   private volatile boolean cancel;
/*     */   private final String sql;
/*     */   private boolean canReuse;
/*     */   
/*     */   Command(SessionLocal paramSessionLocal, String paramString) {
/*  56 */     this.session = paramSessionLocal;
/*  57 */     this.sql = paramString;
/*  58 */     this.trace = paramSessionLocal.getDatabase().getTrace(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean isTransactional();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean isQuery();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract ArrayList<? extends ParameterInterface> getParameters();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean isReadOnly();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract ResultInterface queryMeta();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract ResultWithGeneratedKeys update(Object paramObject);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract ResultInterface query(long paramLong);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ResultInterface getMetaData() {
/* 125 */     return queryMeta();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void start() {
/* 132 */     if (this.trace.isInfoEnabled() || this.session.getDatabase().getQueryStatistics()) {
/* 133 */       this.startTimeNanos = Utils.currentNanoTime();
/*     */     }
/*     */   }
/*     */   
/*     */   void setProgress(int paramInt) {
/* 138 */     this.session.getDatabase().setProgress(paramInt, this.sql, 0L, 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void checkCanceled() {
/* 147 */     if (this.cancel) {
/* 148 */       this.cancel = false;
/* 149 */       throw DbException.get(57014);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void stop() {
/* 155 */     commitIfNonTransactional();
/* 156 */     if (isTransactional() && this.session.getAutoCommit()) {
/* 157 */       this.session.commit(false);
/*     */     }
/* 159 */     if (this.trace.isInfoEnabled() && this.startTimeNanos != 0L) {
/* 160 */       long l = (System.nanoTime() - this.startTimeNanos) / 1000000L;
/* 161 */       if (l > 100L) {
/* 162 */         this.trace.info("slow query: {0} ms", new Object[] { Long.valueOf(l) });
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
/*     */ 
/*     */   
/*     */   public ResultInterface executeQuery(long paramLong, boolean paramBoolean) {
/* 177 */     this.startTimeNanos = 0L;
/* 178 */     long l = 0L;
/* 179 */     Database database = this.session.getDatabase();
/* 180 */     this.session.waitIfExclusiveModeEnabled();
/* 181 */     boolean bool = true;
/*     */     
/* 183 */     synchronized (this.session) {
/* 184 */       this.session.startStatementWithinTransaction(this);
/* 185 */       Session session = this.session.setThreadLocalSession();
/*     */       try {
/*     */         while (true) {
/* 188 */           database.checkPowerOff();
/*     */           try {
/* 190 */             ResultInterface resultInterface = query(paramLong);
/* 191 */             bool = !resultInterface.isLazy() ? true : false;
/* 192 */             if ((database.getMode()).charPadding == Mode.CharPadding.IN_RESULT_SETS) {
/* 193 */               return ResultWithPaddedStrings.get(resultInterface);
/*     */             }
/* 195 */             return resultInterface;
/* 196 */           } catch (DbException dbException) {
/*     */             
/* 198 */             if (isCurrentCommandADefineCommand()) {
/* 199 */               throw dbException;
/*     */             }
/* 201 */             l = filterConcurrentUpdate(dbException, l);
/* 202 */           } catch (OutOfMemoryError outOfMemoryError) {
/* 203 */             bool = false;
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 208 */             database.shutdownImmediately();
/* 209 */             throw DbException.convert(outOfMemoryError);
/* 210 */           } catch (Throwable throwable) {
/* 211 */             throw DbException.convert(throwable);
/*     */           } 
/*     */         } 
/* 214 */       } catch (DbException dbException) {
/* 215 */         dbException = dbException.addSQL(this.sql);
/* 216 */         SQLException sQLException = dbException.getSQLException();
/* 217 */         database.exceptionThrown(sQLException, this.sql);
/* 218 */         if (sQLException.getErrorCode() == 90108) {
/* 219 */           bool = false;
/* 220 */           database.shutdownImmediately();
/* 221 */           throw dbException;
/*     */         } 
/* 223 */         database.checkPowerOff();
/* 224 */         throw dbException;
/*     */       } finally {
/* 226 */         this.session.resetThreadLocalSession(session);
/* 227 */         this.session.endStatement();
/* 228 */         if (bool) {
/* 229 */           stop();
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultWithGeneratedKeys executeUpdate(Object paramObject) {
/* 237 */     long l = 0L;
/* 238 */     Database database = this.session.getDatabase();
/* 239 */     this.session.waitIfExclusiveModeEnabled();
/* 240 */     boolean bool = true;
/*     */     
/* 242 */     synchronized (this.session) {
/* 243 */       commitIfNonTransactional();
/* 244 */       SessionLocal.Savepoint savepoint = this.session.setSavepoint();
/* 245 */       this.session.startStatementWithinTransaction(this);
/* 246 */       DbException dbException = null;
/* 247 */       Session session = this.session.setThreadLocalSession();
/*     */       try {
/*     */         while (true) {
/* 250 */           database.checkPowerOff();
/*     */           try {
/* 252 */             return update(paramObject);
/* 253 */           } catch (DbException dbException1) {
/*     */             
/* 255 */             if (isCurrentCommandADefineCommand()) {
/* 256 */               throw dbException1;
/*     */             }
/* 258 */             l = filterConcurrentUpdate(dbException1, l);
/* 259 */           } catch (OutOfMemoryError outOfMemoryError) {
/* 260 */             bool = false;
/* 261 */             database.shutdownImmediately();
/* 262 */             throw DbException.convert(outOfMemoryError);
/* 263 */           } catch (Throwable throwable) {
/* 264 */             throw DbException.convert(throwable);
/*     */           } 
/*     */         } 
/* 267 */       } catch (DbException dbException1) {
/* 268 */         dbException1 = dbException1.addSQL(this.sql);
/* 269 */         SQLException sQLException = dbException1.getSQLException();
/* 270 */         database.exceptionThrown(sQLException, this.sql);
/* 271 */         if (sQLException.getErrorCode() == 90108) {
/* 272 */           bool = false;
/* 273 */           database.shutdownImmediately();
/* 274 */           throw dbException1;
/*     */         } 
/*     */         try {
/* 277 */           database.checkPowerOff();
/* 278 */           if (sQLException.getErrorCode() == 40001) {
/* 279 */             this.session.rollback();
/*     */           } else {
/* 281 */             this.session.rollbackTo(savepoint);
/*     */           } 
/* 283 */         } catch (Throwable throwable) {
/* 284 */           dbException1.addSuppressed(throwable);
/*     */         } 
/* 286 */         dbException = dbException1;
/* 287 */         throw dbException1;
/*     */       } finally {
/* 289 */         this.session.resetThreadLocalSession(session);
/*     */         try {
/* 291 */           this.session.endStatement();
/* 292 */           if (bool) {
/* 293 */             stop();
/*     */           }
/* 295 */         } catch (Throwable throwable) {
/* 296 */           if (dbException == null) {
/* 297 */             throw throwable;
/*     */           }
/* 299 */           dbException.addSuppressed(throwable);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void commitIfNonTransactional() {
/* 307 */     if (!isTransactional()) {
/* 308 */       boolean bool = this.session.getAutoCommit();
/* 309 */       this.session.commit(true);
/* 310 */       if (!bool && this.session.getAutoCommit()) {
/* 311 */         this.session.begin();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private long filterConcurrentUpdate(DbException paramDbException, long paramLong) {
/* 317 */     int i = paramDbException.getErrorCode();
/* 318 */     if (i != 90131 && i != 90143 && i != 90112)
/*     */     {
/* 320 */       throw paramDbException;
/*     */     }
/* 322 */     long l = Utils.currentNanoTime();
/* 323 */     if (paramLong != 0L && l - paramLong > this.session.getLockTimeout() * 1000000L) {
/* 324 */       throw DbException.get(50200, paramDbException, new String[0]);
/*     */     }
/* 326 */     return (paramLong == 0L) ? l : paramLong;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 331 */     this.canReuse = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void cancel() {
/* 336 */     this.cancel = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 341 */     return this.sql + Trace.formatParams(getParameters());
/*     */   }
/*     */   
/*     */   public boolean isCacheable() {
/* 345 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canReuse() {
/* 354 */     return this.canReuse;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reuse() {
/* 362 */     this.canReuse = false;
/* 363 */     ArrayList<? extends ParameterInterface> arrayList = getParameters();
/* 364 */     for (ParameterInterface parameterInterface : arrayList) {
/* 365 */       parameterInterface.setValue(null, true);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setCanReuse(boolean paramBoolean) {
/* 370 */     this.canReuse = paramBoolean;
/*     */   }
/*     */   
/*     */   public abstract Set<DbObject> getDependencies();
/*     */   
/*     */   protected abstract boolean isCurrentCommandADefineCommand();
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\Command.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */