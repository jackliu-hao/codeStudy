package org.h2.command.dml;

import org.h2.command.Parser;
import org.h2.command.Prepared;
import org.h2.engine.Database;
import org.h2.engine.Mode;
import org.h2.engine.SessionLocal;
import org.h2.engine.Setting;
import org.h2.expression.Expression;
import org.h2.expression.TimeZoneOperation;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.mode.DefaultNullOrdering;
import org.h2.result.ResultInterface;
import org.h2.schema.Schema;
import org.h2.security.auth.Authenticator;
import org.h2.security.auth.AuthenticatorFactory;
import org.h2.table.Table;
import org.h2.util.DateTimeUtils;
import org.h2.util.StringUtils;
import org.h2.util.TimeZoneProvider;
import org.h2.value.CompareMode;
import org.h2.value.DataType;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;

public class Set extends Prepared {
   private final int type;
   private Expression expression;
   private String stringValue;
   private String[] stringValueList;

   public Set(SessionLocal var1, int var2) {
      super(var1);
      this.type = var2;
   }

   public void setString(String var1) {
      this.stringValue = var1;
   }

   public boolean isTransactional() {
      switch (this.type) {
         case 4:
         case 8:
         case 9:
         case 12:
         case 13:
         case 18:
         case 22:
         case 24:
         case 29:
         case 30:
         case 33:
         case 36:
         case 40:
         case 41:
         case 42:
         case 43:
         case 45:
            return true;
         case 5:
         case 6:
         case 7:
         case 10:
         case 11:
         case 14:
         case 15:
         case 16:
         case 17:
         case 19:
         case 20:
         case 21:
         case 23:
         case 25:
         case 26:
         case 27:
         case 28:
         case 31:
         case 32:
         case 34:
         case 35:
         case 37:
         case 38:
         case 39:
         case 44:
         default:
            return false;
      }
   }

   public long update() {
      Database var1;
      var1 = this.session.getDatabase();
      String var2 = SetTypes.getTypeName(this.type);
      String var47;
      int var48;
      label446:
      switch (this.type) {
         case 0:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            synchronized(var1) {
               var1.setIgnoreCase(var48 == 1);
               this.addOrUpdateSetting(var2, (String)null, var48);
               break;
            }
         case 1:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            if (var48 < 0) {
               throw DbException.getInvalidValueException("MAX_LOG_SIZE", var48);
            }
            break;
         case 2:
            Mode var57 = Mode.getInstance(this.stringValue);
            if (var57 == null) {
               throw DbException.get(90088, this.stringValue);
            }

            if (var1.getMode() != var57) {
               this.session.getUser().checkAdmin();
               var1.setMode(var57);
            }
            break;
         case 3:
         default:
            throw DbException.getInternalError("type=" + this.type);
         case 4:
            var48 = this.getIntValue();
            if (var48 < 0) {
               throw DbException.getInvalidValueException("LOCK_TIMEOUT", var48);
            }

            this.session.setLockTimeout(var48);
            break;
         case 5:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            if (var48 < 0) {
               throw DbException.getInvalidValueException("DEFAULT_LOCK_TIMEOUT", var48);
            }

            synchronized(var1) {
               this.addOrUpdateSetting(var2, (String)null, var48);
               break;
            }
         case 6:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            synchronized(var1) {
               var1.setDefaultTableType(var48);
               this.addOrUpdateSetting(var2, (String)null, var48);
               break;
            }
         case 7:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            if (var48 < 0) {
               throw DbException.getInvalidValueException("CACHE_SIZE", var48);
            }

            synchronized(var1) {
               var1.setCacheSize(var48);
               this.addOrUpdateSetting(var2, (String)null, var48);
               break;
            }
         case 8:
            this.session.getUser().checkAdmin();
            if (this.getPersistedObjectId() == 0) {
               var1.getTraceSystem().setLevelSystemOut(this.getIntValue());
            }
            break;
         case 9:
            this.session.getUser().checkAdmin();
            if (this.getPersistedObjectId() == 0) {
               var1.getTraceSystem().setLevelFile(this.getIntValue());
            }
            break;
         case 10:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            if (var48 < 0) {
               throw DbException.getInvalidValueException("TRACE_MAX_FILE_SIZE", var48);
            }

            int var53 = var48 * 1048576;
            synchronized(var1) {
               var1.getTraceSystem().setMaxFileSize(var53);
               this.addOrUpdateSetting(var2, (String)null, var48);
               break;
            }
         case 11:
            this.session.getUser().checkAdmin();
            StringBuilder var52 = new StringBuilder(this.stringValue);
            CompareMode var56;
            if (this.stringValue.equals("OFF")) {
               var56 = CompareMode.getInstance((String)null, 0);
            } else {
               int var5 = this.getIntValue();
               var52.append(" STRENGTH ");
               if (var5 == 3) {
                  var52.append("IDENTICAL");
               } else if (var5 == 0) {
                  var52.append("PRIMARY");
               } else if (var5 == 1) {
                  var52.append("SECONDARY");
               } else if (var5 == 2) {
                  var52.append("TERTIARY");
               }

               var56 = CompareMode.getInstance(this.stringValue, var5);
            }

            synchronized(var1) {
               CompareMode var6 = var1.getCompareMode();
               if (!var6.equals(var56)) {
                  Table var7 = var1.getFirstUserTable();
                  if (var7 != null) {
                     throw DbException.get(90089, var7.getTraceSQL());
                  }

                  this.addOrUpdateSetting(var2, var52.toString(), 0);
                  var1.setCompareMode(var56);
               }
               break;
            }
         case 12:
            if (!"TRUE".equals(this.stringValue)) {
               var47 = StringUtils.quoteStringSQL(this.stringValue);
               if (!var47.equals(var1.getCluster())) {
                  if (!var47.equals("''")) {
                     this.session.getUser().checkAdmin();
                  }

                  var1.setCluster(var47);
                  SessionLocal var51 = var1.getSystemSession();
                  synchronized(var51) {
                     synchronized(var1) {
                        this.addOrUpdateSetting(var51, var2, var47, 0);
                        var51.commit(true);
                     }
                  }
               }
            }
            break;
         case 13:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            if (var48 < 0) {
               throw DbException.getInvalidValueException("WRITE_DELAY", var48);
            }

            synchronized(var1) {
               var1.setWriteDelay(var48);
               this.addOrUpdateSetting(var2, (String)null, var48);
               break;
            }
         case 14:
            this.session.getUser().checkAdmin();
            var1.setEventListenerClass(this.stringValue);
            break;
         case 15:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            if (var48 < 0) {
               throw DbException.getInvalidValueException("MAX_MEMORY_ROWS", var48);
            }

            synchronized(var1) {
               var1.setMaxMemoryRows(var48);
               this.addOrUpdateSetting(var2, (String)null, var48);
               break;
            }
         case 16:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            synchronized(var1) {
               var1.setLockMode(var48);
               this.addOrUpdateSetting(var2, (String)null, var48);
               break;
            }
         case 17:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            if (var48 != -1 && var48 < 0) {
               throw DbException.getInvalidValueException("DB_CLOSE_DELAY", var48);
            }

            synchronized(var1) {
               var1.setCloseDelay(var48);
               this.addOrUpdateSetting(var2, (String)null, var48);
               break;
            }
         case 18:
            var48 = this.getIntValue();
            if (var48 < 0) {
               throw DbException.getInvalidValueException("THROTTLE", var48);
            }

            this.session.setThrottle(var48);
            break;
         case 19:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            if (var48 < 0) {
               throw DbException.getInvalidValueException("MAX_MEMORY_UNDO", var48);
            }

            synchronized(var1) {
               this.addOrUpdateSetting(var2, (String)null, var48);
               break;
            }
         case 20:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            if (var48 < 0) {
               throw DbException.getInvalidValueException("MAX_LENGTH_INPLACE_LOB", var48);
            }

            synchronized(var1) {
               var1.setMaxLengthInplaceLob(var48);
               this.addOrUpdateSetting(var2, (String)null, var48);
               break;
            }
         case 21:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            if (var48 >= 0 && var48 <= 2) {
               synchronized(var1) {
                  var1.setAllowLiterals(var48);
                  this.addOrUpdateSetting(var2, (String)null, var48);
                  break;
               }
            }

            throw DbException.getInvalidValueException("ALLOW_LITERALS", var48);
         case 22:
            Schema var55 = var1.getSchema(this.expression.optimize(this.session).getValue(this.session).getString());
            this.session.setCurrentSchema(var55);
            break;
         case 23:
            this.session.getUser().checkAdmin();
            var1.setOptimizeReuseResults(this.getIntValue() != 0);
            break;
         case 24:
            this.session.setSchemaSearchPath(this.stringValueList);
            break;
         case 25:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            if (var48 >= 0 && var48 <= 1) {
               var1.setReferentialIntegrity(var48 == 1);
               break;
            }

            throw DbException.getInvalidValueException("REFERENTIAL_INTEGRITY", var48);
         case 26:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            if (var48 < 0) {
               throw DbException.getInvalidValueException("MAX_OPERATION_MEMORY", var48);
            }

            var1.setMaxOperationMemory(var48);
            break;
         case 27:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            switch (var48) {
               case 0:
                  if (!var1.unsetExclusiveSession(this.session)) {
                     throw DbException.get(90135);
                  }
                  break label446;
               case 1:
                  if (!var1.setExclusiveSession(this.session, false)) {
                     throw DbException.get(90135);
                  }
                  break label446;
               case 2:
                  if (!var1.setExclusiveSession(this.session, true)) {
                     throw DbException.get(90135);
                  }
                  break label446;
               default:
                  throw DbException.getInvalidValueException("EXCLUSIVE", var48);
            }
         case 28:
            this.session.getUser().checkAdmin();
            if (var1.isStarting()) {
               var48 = this.getIntValue();
               synchronized(var1) {
                  this.addOrUpdateSetting(var2, (String)null, var48);
               }
            }
            break;
         case 29:
            Expression var54 = this.expression.optimize(this.session);
            this.session.setVariable(this.stringValue, var54.getValue(this.session));
            break;
         case 30:
            var48 = this.getIntValue();
            if (var48 < 0) {
               throw DbException.getInvalidValueException("QUERY_TIMEOUT", var48);
            }

            this.session.setQueryTimeout(var48);
            break;
         case 31:
            DbException.getUnsupportedException("MV_STORE + SET REDO_LOG_BINARY");
            break;
         case 32:
            this.session.getUser().checkAdmin();
            synchronized(var1) {
               Table var50 = var1.getFirstUserTable();
               if (var50 != null) {
                  throw DbException.get(90141, var50.getTraceSQL());
               }

               var1.setJavaObjectSerializerName(this.stringValue);
               this.addOrUpdateSetting(var2, this.stringValue, 0);
               break;
            }
         case 33:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            if (var48 < 0) {
               throw DbException.getInvalidValueException("RETENTION_TIME", var48);
            }

            synchronized(var1) {
               var1.setRetentionTime(var48);
               this.addOrUpdateSetting(var2, (String)null, var48);
               break;
            }
         case 34:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            if (var48 >= 0 && var48 <= 1) {
               var1.setQueryStatistics(var48 == 1);
               break;
            }

            throw DbException.getInvalidValueException("QUERY_STATISTICS", var48);
         case 35:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            if (var48 < 1) {
               throw DbException.getInvalidValueException("QUERY_STATISTICS_MAX_ENTRIES", var48);
            }

            var1.setQueryStatisticsMaxEntries(var48);
            break;
         case 36:
            var48 = this.getIntValue();
            if (var48 != 0 && var48 != 1) {
               throw DbException.getInvalidValueException("LAZY_QUERY_EXECUTION", var48);
            }

            this.session.setLazyQueryExecution(var48 == 1);
            break;
         case 37:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            if (var48 != 0 && var48 != 1) {
               throw DbException.getInvalidValueException("BUILTIN_ALIAS_OVERRIDE", var48);
            }

            var1.setAllowBuiltinAliasOverride(var48 == 1);
            break;
         case 38:
            this.session.getUser().checkAdmin();
            boolean var49 = this.expression.optimize(this.session).getBooleanValue(this.session);

            try {
               synchronized(var1) {
                  if (var49) {
                     var1.setAuthenticator(AuthenticatorFactory.createAuthenticator());
                  } else {
                     var1.setAuthenticator((Authenticator)null);
                  }

                  this.addOrUpdateSetting(var2, var49 ? "TRUE" : "FALSE", 0);
                  break;
               }
            } catch (Exception var46) {
               if (var1.isStarting()) {
                  var1.getTrace(2).error(var46, "{0}: failed to set authenticator during database start ", this.expression.toString());
                  break;
               }

               throw DbException.convert(var46);
            }
         case 39:
            this.session.getUser().checkAdmin();
            var48 = this.getIntValue();
            synchronized(var1) {
               var1.setIgnoreCatalogs(var48 == 1);
               this.addOrUpdateSetting(var2, (String)null, var48);
               break;
            }
         case 40:
            var47 = var1.getShortName();
            String var4 = this.expression.optimize(this.session).getValue(this.session).getString();
            if (var4 != null && (var1.equalsIdentifiers(var47, var4) || var1.equalsIdentifiers(var47, var4.trim()))) {
               break;
            }

            throw DbException.get(90013, this.stringValue);
         case 41:
            this.session.setNonKeywords(Parser.parseNonKeywords(this.stringValueList));
            break;
         case 42:
            this.session.setTimeZone(this.expression == null ? DateTimeUtils.getTimeZone() : parseTimeZone(this.expression.getValue(this.session)));
            break;
         case 43:
            this.session.setVariableBinary(this.expression.getBooleanValue(this.session));
            break;
         case 44:
            DefaultNullOrdering var3;
            try {
               var3 = DefaultNullOrdering.valueOf(StringUtils.toUpperEnglish(this.stringValue));
            } catch (RuntimeException var25) {
               throw DbException.getInvalidValueException("DEFAULT_NULL_ORDERING", this.stringValue);
            }

            if (var1.getDefaultNullOrdering() != var3) {
               this.session.getUser().checkAdmin();
               var1.setDefaultNullOrdering(var3);
            }
            break;
         case 45:
            this.session.setTruncateLargeLength(this.expression.getBooleanValue(this.session));
      }

      var1.getNextModificationDataId();
      var1.getNextModificationMetaId();
      return 0L;
   }

   private static TimeZoneProvider parseTimeZone(Value var0) {
      if (DataType.isCharacterStringType(var0.getValueType())) {
         try {
            TimeZoneProvider var1 = TimeZoneProvider.ofId(var0.getString());
            return var1;
         } catch (IllegalArgumentException var3) {
            throw DbException.getInvalidValueException("TIME ZONE", var0.getTraceSQL());
         }
      } else if (var0 == ValueNull.INSTANCE) {
         throw DbException.getInvalidValueException("TIME ZONE", var0);
      } else {
         return TimeZoneProvider.ofOffset(TimeZoneOperation.parseInterval(var0));
      }
   }

   private int getIntValue() {
      this.expression = this.expression.optimize(this.session);
      return this.expression.getValue(this.session).getInt();
   }

   public void setInt(int var1) {
      this.expression = ValueExpression.get(ValueInteger.get(var1));
   }

   public void setExpression(Expression var1) {
      this.expression = var1;
   }

   private void addOrUpdateSetting(String var1, String var2, int var3) {
      this.addOrUpdateSetting(this.session, var1, var2, var3);
   }

   private void addOrUpdateSetting(SessionLocal var1, String var2, String var3, int var4) {
      Database var5 = var1.getDatabase();

      assert Thread.holdsLock(var5);

      if (!var5.isReadOnly()) {
         Setting var6 = var5.findSetting(var2);
         boolean var7 = false;
         if (var6 == null) {
            var7 = true;
            int var8 = this.getObjectId();
            var6 = new Setting(var5, var8, var2);
         }

         if (var3 != null) {
            if (!var7 && var6.getStringValue().equals(var3)) {
               return;
            }

            var6.setStringValue(var3);
         } else {
            if (!var7 && var6.getIntValue() == var4) {
               return;
            }

            var6.setIntValue(var4);
         }

         if (var7) {
            var5.addDatabaseObject(var1, var6);
         } else {
            var5.updateMeta(var1, var6);
         }

      }
   }

   public boolean needRecompile() {
      return false;
   }

   public ResultInterface queryMeta() {
      return null;
   }

   public void setStringArray(String[] var1) {
      this.stringValueList = var1;
   }

   public int getType() {
      return 67;
   }
}
