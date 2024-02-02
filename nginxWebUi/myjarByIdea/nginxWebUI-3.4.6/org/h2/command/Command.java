package org.h2.command;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.Mode;
import org.h2.engine.Session;
import org.h2.engine.SessionLocal;
import org.h2.expression.ParameterInterface;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.result.ResultInterface;
import org.h2.result.ResultWithGeneratedKeys;
import org.h2.result.ResultWithPaddedStrings;
import org.h2.util.Utils;
import org.h2.value.Value;

public abstract class Command implements CommandInterface {
   protected final SessionLocal session;
   protected long startTimeNanos;
   private final Trace trace;
   private volatile boolean cancel;
   private final String sql;
   private boolean canReuse;

   Command(SessionLocal var1, String var2) {
      this.session = var1;
      this.sql = var2;
      this.trace = var1.getDatabase().getTrace(0);
   }

   public abstract boolean isTransactional();

   public abstract boolean isQuery();

   public abstract ArrayList<? extends ParameterInterface> getParameters();

   public abstract boolean isReadOnly();

   public abstract ResultInterface queryMeta();

   public abstract ResultWithGeneratedKeys update(Object var1);

   public abstract ResultInterface query(long var1);

   public final ResultInterface getMetaData() {
      return this.queryMeta();
   }

   void start() {
      if (this.trace.isInfoEnabled() || this.session.getDatabase().getQueryStatistics()) {
         this.startTimeNanos = Utils.currentNanoTime();
      }

   }

   void setProgress(int var1) {
      this.session.getDatabase().setProgress(var1, this.sql, 0L, 0L);
   }

   protected void checkCanceled() {
      if (this.cancel) {
         this.cancel = false;
         throw DbException.get(57014);
      }
   }

   public void stop() {
      this.commitIfNonTransactional();
      if (this.isTransactional() && this.session.getAutoCommit()) {
         this.session.commit(false);
      }

      if (this.trace.isInfoEnabled() && this.startTimeNanos != 0L) {
         long var1 = (System.nanoTime() - this.startTimeNanos) / 1000000L;
         if (var1 > 100L) {
            this.trace.info("slow query: {0} ms", var1);
         }
      }

   }

   public ResultInterface executeQuery(long var1, boolean var3) {
      this.startTimeNanos = 0L;
      long var4 = 0L;
      Database var6 = this.session.getDatabase();
      this.session.waitIfExclusiveModeEnabled();
      boolean var7 = true;
      synchronized(this.session) {
         this.session.startStatementWithinTransaction(this);
         Session var9 = this.session.setThreadLocalSession();

         try {
            while(true) {
               var6.checkPowerOff();

               try {
                  ResultInterface var26 = this.query(var1);
                  var7 = !var26.isLazy();
                  ResultInterface var27;
                  if (var6.getMode().charPadding == Mode.CharPadding.IN_RESULT_SETS) {
                     var27 = ResultWithPaddedStrings.get(var26);
                     return var27;
                  }

                  var27 = var26;
                  return var27;
               } catch (DbException var20) {
                  if (this.isCurrentCommandADefineCommand()) {
                     throw var20;
                  }

                  var4 = this.filterConcurrentUpdate(var20, var4);
               } catch (OutOfMemoryError var21) {
                  var7 = false;
                  var6.shutdownImmediately();
                  throw DbException.convert(var21);
               } catch (Throwable var22) {
                  throw DbException.convert(var22);
               }
            }
         } catch (DbException var23) {
            DbException var10 = var23.addSQL(this.sql);
            SQLException var11 = var10.getSQLException();
            var6.exceptionThrown(var11, this.sql);
            if (var11.getErrorCode() == 90108) {
               var7 = false;
               var6.shutdownImmediately();
               throw var10;
            } else {
               var6.checkPowerOff();
               throw var10;
            }
         } finally {
            this.session.resetThreadLocalSession(var9);
            this.session.endStatement();
            if (var7) {
               this.stop();
            }

         }
      }
   }

   public ResultWithGeneratedKeys executeUpdate(Object var1) {
      long var2 = 0L;
      Database var4 = this.session.getDatabase();
      this.session.waitIfExclusiveModeEnabled();
      boolean var5 = true;
      synchronized(this.session) {
         this.commitIfNonTransactional();
         SessionLocal.Savepoint var7 = this.session.setSavepoint();
         this.session.startStatementWithinTransaction(this);
         DbException var8 = null;
         Session var9 = this.session.setThreadLocalSession();

         try {
            while(true) {
               var4.checkPowerOff();

               try {
                  ResultWithGeneratedKeys var33 = this.update(var1);
                  return var33;
               } catch (DbException var27) {
                  if (this.isCurrentCommandADefineCommand()) {
                     throw var27;
                  }

                  var2 = this.filterConcurrentUpdate(var27, var2);
               } catch (OutOfMemoryError var28) {
                  var5 = false;
                  var4.shutdownImmediately();
                  throw DbException.convert(var28);
               } catch (Throwable var29) {
                  throw DbException.convert(var29);
               }
            }
         } catch (DbException var30) {
            DbException var10 = var30.addSQL(this.sql);
            SQLException var11 = var10.getSQLException();
            var4.exceptionThrown(var11, this.sql);
            if (var11.getErrorCode() == 90108) {
               var5 = false;
               var4.shutdownImmediately();
               throw var10;
            } else {
               try {
                  var4.checkPowerOff();
                  if (var11.getErrorCode() == 40001) {
                     this.session.rollback();
                  } else {
                     this.session.rollbackTo(var7);
                  }
               } catch (Throwable var25) {
                  var10.addSuppressed(var25);
               }

               var8 = var10;
               throw var10;
            }
         } finally {
            this.session.resetThreadLocalSession(var9);

            try {
               this.session.endStatement();
               if (var5) {
                  this.stop();
               }
            } catch (Throwable var26) {
               if (var8 == null) {
                  throw var26;
               }

               var8.addSuppressed(var26);
            }

         }
      }
   }

   private void commitIfNonTransactional() {
      if (!this.isTransactional()) {
         boolean var1 = this.session.getAutoCommit();
         this.session.commit(true);
         if (!var1 && this.session.getAutoCommit()) {
            this.session.begin();
         }
      }

   }

   private long filterConcurrentUpdate(DbException var1, long var2) {
      int var4 = var1.getErrorCode();
      if (var4 != 90131 && var4 != 90143 && var4 != 90112) {
         throw var1;
      } else {
         long var5 = Utils.currentNanoTime();
         if (var2 != 0L && var5 - var2 > (long)this.session.getLockTimeout() * 1000000L) {
            throw DbException.get(50200, var1);
         } else {
            return var2 == 0L ? var5 : var2;
         }
      }
   }

   public void close() {
      this.canReuse = true;
   }

   public void cancel() {
      this.cancel = true;
   }

   public String toString() {
      return this.sql + Trace.formatParams(this.getParameters());
   }

   public boolean isCacheable() {
      return false;
   }

   public boolean canReuse() {
      return this.canReuse;
   }

   public void reuse() {
      this.canReuse = false;
      ArrayList var1 = this.getParameters();
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         ParameterInterface var3 = (ParameterInterface)var2.next();
         var3.setValue((Value)null, true);
      }

   }

   public void setCanReuse(boolean var1) {
      this.canReuse = var1;
   }

   public abstract Set<DbObject> getDependencies();

   protected abstract boolean isCurrentCommandADefineCommand();
}
