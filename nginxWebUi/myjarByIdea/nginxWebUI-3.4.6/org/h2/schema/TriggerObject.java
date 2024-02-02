package org.h2.schema;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import org.h2.api.Trigger;
import org.h2.command.CommandInterface;
import org.h2.engine.SessionLocal;
import org.h2.jdbc.JdbcConnection;
import org.h2.jdbc.JdbcResultSet;
import org.h2.jdbc.JdbcStatement;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SimpleResult;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.tools.TriggerAdapter;
import org.h2.util.JdbcUtils;
import org.h2.util.SourceCompiler;
import org.h2.util.StringUtils;
import org.h2.value.Value;
import org.h2.value.ValueToObjectConverter;

public final class TriggerObject extends SchemaObject {
   public static final int DEFAULT_QUEUE_SIZE = 1024;
   private boolean insteadOf;
   private boolean before;
   private int typeMask;
   private boolean rowBased;
   private boolean onRollback;
   private int queueSize = 1024;
   private boolean noWait;
   private Table table;
   private String triggerClassName;
   private String triggerSource;
   private Trigger triggerCallback;

   public TriggerObject(Schema var1, int var2, String var3, Table var4) {
      super(var1, var2, var3, 12);
      this.table = var4;
      this.setTemporary(var4.isTemporary());
   }

   public void setBefore(boolean var1) {
      this.before = var1;
   }

   public boolean isInsteadOf() {
      return this.insteadOf;
   }

   public void setInsteadOf(boolean var1) {
      this.insteadOf = var1;
   }

   private synchronized void load() {
      if (this.triggerCallback == null) {
         try {
            SessionLocal var1 = this.database.getSystemSession();
            JdbcConnection var2 = var1.createConnection(false);
            Object var3;
            if (this.triggerClassName != null) {
               var3 = JdbcUtils.loadUserClass(this.triggerClassName).getDeclaredConstructor().newInstance();
            } else {
               var3 = this.loadFromSource();
            }

            this.triggerCallback = (Trigger)var3;
            this.triggerCallback.init(var2, this.getSchema().getName(), this.getName(), this.table.getName(), this.before, this.typeMask);
         } catch (Throwable var4) {
            this.triggerCallback = null;
            throw DbException.get(90043, var4, this.getName(), this.triggerClassName != null ? this.triggerClassName : "..source..", var4.toString());
         }
      }
   }

   private Trigger loadFromSource() {
      SourceCompiler var1 = this.database.getCompiler();
      synchronized(var1) {
         String var3 = "org.h2.dynamic.trigger." + this.getName();
         var1.setSource(var3, this.triggerSource);

         Trigger var10000;
         try {
            if (SourceCompiler.isJavaxScriptSource(this.triggerSource)) {
               var10000 = (Trigger)var1.getCompiledScript(var3).eval();
               return var10000;
            }

            Method var4 = var1.getMethod(var3);
            if (var4.getParameterTypes().length > 0) {
               throw new IllegalStateException("No parameters are allowed for a trigger");
            }

            var10000 = (Trigger)var4.invoke((Object)null);
         } catch (DbException var6) {
            throw var6;
         } catch (Exception var7) {
            throw DbException.get(42000, var7, this.triggerSource);
         }

         return var10000;
      }
   }

   public void setTriggerClassName(String var1, boolean var2) {
      this.setTriggerAction(var1, (String)null, var2);
   }

   public void setTriggerSource(String var1, boolean var2) {
      this.setTriggerAction((String)null, var1, var2);
   }

   private void setTriggerAction(String var1, String var2, boolean var3) {
      this.triggerClassName = var1;
      this.triggerSource = var2;

      try {
         this.load();
      } catch (DbException var5) {
         if (!var3) {
            throw var5;
         }
      }

   }

   public void fire(SessionLocal var1, int var2, boolean var3) {
      if (!this.rowBased && this.before == var3 && (this.typeMask & var2) != 0) {
         this.load();
         JdbcConnection var4 = var1.createConnection(false);
         boolean var5 = false;
         if (var2 != 8) {
            var5 = var1.setCommitOrRollbackDisabled(true);
         }

         Value var6 = var1.getLastIdentity();

         try {
            if (this.triggerCallback instanceof TriggerAdapter) {
               ((TriggerAdapter)this.triggerCallback).fire(var4, (ResultSet)((ResultSet)null), (ResultSet)((ResultSet)null));
            } else {
               this.triggerCallback.fire(var4, (Object[])null, (Object[])null);
            }
         } catch (Throwable var11) {
            throw this.getErrorExecutingTrigger(var11);
         } finally {
            var1.setLastIdentity(var6);
            if (var2 != 8) {
               var1.setCommitOrRollbackDisabled(var5);
            }

         }

      }
   }

   private static Object[] convertToObjectList(Row var0, JdbcConnection var1) {
      if (var0 == null) {
         return null;
      } else {
         int var2 = var0.getColumnCount();
         Object[] var3 = new Object[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = ValueToObjectConverter.valueToDefaultObject(var0.getValue(var4), var1, false);
         }

         return var3;
      }
   }

   public boolean fireRow(SessionLocal var1, Table var2, Row var3, Row var4, boolean var5, boolean var6) {
      if (this.rowBased && this.before == var5) {
         if (var6 && !this.onRollback) {
            return false;
         } else {
            this.load();
            boolean var7 = false;
            if ((this.typeMask & 1) != 0 && var3 == null && var4 != null) {
               var7 = true;
            }

            if ((this.typeMask & 2) != 0 && var3 != null && var4 != null) {
               var7 = true;
            }

            if ((this.typeMask & 4) != 0 && var3 != null && var4 == null) {
               var7 = true;
            }

            if (!var7) {
               return false;
            } else {
               JdbcConnection var8 = var1.createConnection(false);
               boolean var9 = var1.getAutoCommit();
               boolean var10 = var1.setCommitOrRollbackDisabled(true);
               Value var11 = var1.getLastIdentity();

               try {
                  var1.setAutoCommit(false);
                  boolean var15;
                  int var16;
                  if (this.triggerCallback instanceof TriggerAdapter) {
                     JdbcResultSet var28 = var3 != null ? createResultSet(var8, var2, var3, false) : null;
                     JdbcResultSet var29 = var4 != null ? createResultSet(var8, var2, var4, this.before) : null;

                     try {
                        ((TriggerAdapter)this.triggerCallback).fire(var8, (ResultSet)var28, (ResultSet)var29);
                     } catch (Throwable var25) {
                        throw this.getErrorExecutingTrigger(var25);
                     }

                     if (var29 != null) {
                        Value[] var30 = var29.getUpdateRow();
                        if (var30 != null) {
                           var15 = false;
                           var16 = 0;

                           for(int var31 = var30.length; var16 < var31; ++var16) {
                              Value var18 = var30[var16];
                              if (var18 != null) {
                                 var15 = true;
                                 var4.setValue(var16, var18);
                              }
                           }

                           if (var15) {
                              var2.convertUpdateRow(var1, var4, true);
                           }
                        }
                     }
                  } else {
                     Object[] var12 = convertToObjectList(var3, var8);
                     Object[] var13 = convertToObjectList(var4, var8);
                     Object[] var14 = this.before && var13 != null ? Arrays.copyOf(var13, var13.length) : null;

                     try {
                        this.triggerCallback.fire(var8, var12, var13);
                     } catch (Throwable var24) {
                        throw this.getErrorExecutingTrigger(var24);
                     }

                     if (var14 != null) {
                        var15 = false;

                        for(var16 = 0; var16 < var13.length; ++var16) {
                           Object var17 = var13[var16];
                           if (var17 != var14[var16]) {
                              var15 = true;
                              var4.setValue(var16, ValueToObjectConverter.objectToValue(var1, var17, -1));
                           }
                        }

                        if (var15) {
                           var2.convertUpdateRow(var1, var4, true);
                        }
                     }
                  }
               } catch (Exception var26) {
                  if (!this.onRollback) {
                     throw DbException.convert(var26);
                  }
               } finally {
                  var1.setLastIdentity(var11);
                  var1.setCommitOrRollbackDisabled(var10);
                  var1.setAutoCommit(var9);
               }

               return this.insteadOf;
            }
         }
      } else {
         return false;
      }
   }

   private static JdbcResultSet createResultSet(JdbcConnection var0, Table var1, Row var2, boolean var3) throws SQLException {
      SimpleResult var4 = new SimpleResult(var1.getSchema().getName(), var1.getName());
      Column[] var5 = var1.getColumns();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Column var8 = var5[var7];
         var4.addColumn(var8.getName(), var8.getType());
      }

      var4.addRow(var2.getValueList());
      var4.addRow(var2.getValueList());
      JdbcResultSet var9 = new JdbcResultSet(var0, (JdbcStatement)null, (CommandInterface)null, var4, -1, false, false, var3);
      var9.next();
      return var9;
   }

   private DbException getErrorExecutingTrigger(Throwable var1) {
      if (var1 instanceof DbException) {
         return (DbException)var1;
      } else {
         return var1 instanceof SQLException ? DbException.convert(var1) : DbException.get(90044, var1, this.getName(), this.triggerClassName != null ? this.triggerClassName : "..source..", var1.toString());
      }
   }

   public int getTypeMask() {
      return this.typeMask;
   }

   public void setTypeMask(int var1) {
      this.typeMask = var1;
   }

   public void setRowBased(boolean var1) {
      this.rowBased = var1;
   }

   public boolean isRowBased() {
      return this.rowBased;
   }

   public void setQueueSize(int var1) {
      this.queueSize = var1;
   }

   public int getQueueSize() {
      return this.queueSize;
   }

   public void setNoWait(boolean var1) {
      this.noWait = var1;
   }

   public boolean isNoWait() {
      return this.noWait;
   }

   public void setOnRollback(boolean var1) {
      this.onRollback = var1;
   }

   public boolean isOnRollback() {
      return this.onRollback;
   }

   public String getCreateSQLForCopy(Table var1, String var2) {
      StringBuilder var3 = new StringBuilder("CREATE FORCE TRIGGER ");
      var3.append(var2);
      if (this.insteadOf) {
         var3.append(" INSTEAD OF ");
      } else if (this.before) {
         var3.append(" BEFORE ");
      } else {
         var3.append(" AFTER ");
      }

      this.getTypeNameList(var3).append(" ON ");
      var1.getSQL(var3, 0);
      if (this.rowBased) {
         var3.append(" FOR EACH ROW");
      }

      if (this.noWait) {
         var3.append(" NOWAIT");
      } else {
         var3.append(" QUEUE ").append(this.queueSize);
      }

      if (this.triggerClassName != null) {
         StringUtils.quoteStringSQL(var3.append(" CALL "), this.triggerClassName);
      } else {
         StringUtils.quoteStringSQL(var3.append(" AS "), this.triggerSource);
      }

      return var3.toString();
   }

   public StringBuilder getTypeNameList(StringBuilder var1) {
      boolean var2 = false;
      if ((this.typeMask & 1) != 0) {
         var2 = true;
         var1.append("INSERT");
      }

      if ((this.typeMask & 2) != 0) {
         if (var2) {
            var1.append(", ");
         }

         var2 = true;
         var1.append("UPDATE");
      }

      if ((this.typeMask & 4) != 0) {
         if (var2) {
            var1.append(", ");
         }

         var2 = true;
         var1.append("DELETE");
      }

      if ((this.typeMask & 8) != 0) {
         if (var2) {
            var1.append(", ");
         }

         var2 = true;
         var1.append("SELECT");
      }

      if (this.onRollback) {
         if (var2) {
            var1.append(", ");
         }

         var1.append("ROLLBACK");
      }

      return var1;
   }

   public String getCreateSQL() {
      return this.getCreateSQLForCopy(this.table, this.getSQL(0));
   }

   public int getType() {
      return 4;
   }

   public void removeChildrenAndResources(SessionLocal var1) {
      this.table.removeTrigger(this);
      this.database.removeMeta(var1, this.getId());
      if (this.triggerCallback != null) {
         try {
            this.triggerCallback.remove();
         } catch (SQLException var3) {
            throw DbException.convert(var3);
         }
      }

      this.table = null;
      this.triggerClassName = null;
      this.triggerSource = null;
      this.triggerCallback = null;
      this.invalidate();
   }

   public Table getTable() {
      return this.table;
   }

   public boolean isBefore() {
      return this.before;
   }

   public String getTriggerClassName() {
      return this.triggerClassName;
   }

   public String getTriggerSource() {
      return this.triggerSource;
   }

   public void close() throws SQLException {
      if (this.triggerCallback != null) {
         this.triggerCallback.close();
      }

   }

   public boolean isSelectTrigger() {
      return (this.typeMask & 8) != 0;
   }
}
