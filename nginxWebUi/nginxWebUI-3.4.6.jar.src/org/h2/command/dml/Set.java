/*     */ package org.h2.command.dml;
/*     */ 
/*     */ import org.h2.command.Parser;
/*     */ import org.h2.command.Prepared;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.Mode;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.engine.Setting;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.TimeZoneOperation;
/*     */ import org.h2.expression.ValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mode.DefaultNullOrdering;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.security.auth.AuthenticatorFactory;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.util.DateTimeUtils;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.TimeZoneProvider;
/*     */ import org.h2.value.CompareMode;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueInteger;
/*     */ import org.h2.value.ValueNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Set
/*     */   extends Prepared
/*     */ {
/*     */   private final int type;
/*     */   private Expression expression;
/*     */   private String stringValue;
/*     */   private String[] stringValueList;
/*     */   
/*     */   public Set(SessionLocal paramSessionLocal, int paramInt) {
/*  50 */     super(paramSessionLocal);
/*  51 */     this.type = paramInt;
/*     */   }
/*     */   
/*     */   public void setString(String paramString) {
/*  55 */     this.stringValue = paramString;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isTransactional() {
/*  60 */     switch (this.type) {
/*     */       case 4:
/*     */       case 8:
/*     */       case 9:
/*     */       case 12:
/*     */       case 13:
/*     */       case 18:
/*     */       case 22:
/*     */       case 24:
/*     */       case 29:
/*     */       case 30:
/*     */       case 33:
/*     */       case 36:
/*     */       case 40:
/*     */       case 41:
/*     */       case 42:
/*     */       case 43:
/*     */       case 45:
/*  78 */         return true;
/*     */     } 
/*     */     
/*  81 */     return false; } public long update() { int i1; CompareMode compareMode; int n; Mode mode; int m; Schema schema; String str2; int k; Expression expression; int j; boolean bool; int i;
/*     */     DefaultNullOrdering defaultNullOrdering;
/*     */     StringBuilder stringBuilder;
/*     */     String str3;
/*     */     int i2;
/*  86 */     Database database = this.session.getDatabase();
/*  87 */     String str1 = SetTypes.getTypeName(this.type);
/*  88 */     switch (this.type) {
/*     */       case 21:
/*  90 */         this.session.getUser().checkAdmin();
/*  91 */         i1 = getIntValue();
/*  92 */         if (i1 < 0 || i1 > 2) {
/*  93 */           throw DbException.getInvalidValueException("ALLOW_LITERALS", Integer.valueOf(i1));
/*     */         }
/*  95 */         synchronized (database) {
/*  96 */           database.setAllowLiterals(i1);
/*  97 */           addOrUpdateSetting(str1, (String)null, i1);
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 563 */         database.getNextModificationDataId();
/*     */ 
/*     */         
/* 566 */         database.getNextModificationMetaId();
/* 567 */         return 0L;case 7: this.session.getUser().checkAdmin(); i1 = getIntValue(); if (i1 < 0) throw DbException.getInvalidValueException("CACHE_SIZE", Integer.valueOf(i1));  synchronized (database) { database.setCacheSize(i1); addOrUpdateSetting(str1, (String)null, i1); }  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 12: if (!"TRUE".equals(this.stringValue)) { String str = StringUtils.quoteStringSQL(this.stringValue); if (!str.equals(database.getCluster())) { if (!str.equals("''")) this.session.getUser().checkAdmin();  database.setCluster(str); SessionLocal sessionLocal = database.getSystemSession(); synchronized (sessionLocal) { synchronized (database) { addOrUpdateSetting(sessionLocal, str1, str, 0); sessionLocal.commit(true); }  }  }  }  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 11: this.session.getUser().checkAdmin(); stringBuilder = new StringBuilder(this.stringValue); if (this.stringValue.equals("OFF")) { compareMode = CompareMode.getInstance(null, 0); } else { int i3 = getIntValue(); stringBuilder.append(" STRENGTH "); if (i3 == 3) { stringBuilder.append("IDENTICAL"); } else if (i3 == 0) { stringBuilder.append("PRIMARY"); } else if (i3 == 1) { stringBuilder.append("SECONDARY"); } else if (i3 == 2) { stringBuilder.append("TERTIARY"); }  compareMode = CompareMode.getInstance(this.stringValue, i3); }  synchronized (database) { CompareMode compareMode1 = database.getCompareMode(); if (compareMode1.equals(compareMode)) {  } else { Table table = database.getFirstUserTable(); if (table != null) throw DbException.get(90089, table.getTraceSQL());  addOrUpdateSetting(str1, stringBuilder.toString(), 0); database.setCompareMode(compareMode); }  }  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 28: this.session.getUser().checkAdmin(); if (database.isStarting()) { int i3 = getIntValue(); synchronized (database) { addOrUpdateSetting(str1, (String)null, i3); }  }  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 14: this.session.getUser().checkAdmin(); database.setEventListenerClass(this.stringValue); database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 17: this.session.getUser().checkAdmin(); n = getIntValue(); if (n != -1) if (n < 0) throw DbException.getInvalidValueException("DB_CLOSE_DELAY", Integer.valueOf(n));   synchronized (database) { database.setCloseDelay(n); addOrUpdateSetting(str1, (String)null, n); }  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 5: this.session.getUser().checkAdmin(); n = getIntValue(); if (n < 0) throw DbException.getInvalidValueException("DEFAULT_LOCK_TIMEOUT", Integer.valueOf(n));  synchronized (database) { addOrUpdateSetting(str1, (String)null, n); }  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 6: this.session.getUser().checkAdmin(); n = getIntValue(); synchronized (database) { database.setDefaultTableType(n); addOrUpdateSetting(str1, (String)null, n); }  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 27: this.session.getUser().checkAdmin(); n = getIntValue(); switch (n) { case 0: if (!database.unsetExclusiveSession(this.session)) throw DbException.get(90135);  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 1: if (!database.setExclusiveSession(this.session, false)) throw DbException.get(90135);  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 2: if (!database.setExclusiveSession(this.session, true)) throw DbException.get(90135);  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L; }  throw DbException.getInvalidValueException("EXCLUSIVE", Integer.valueOf(n));case 32: this.session.getUser().checkAdmin(); synchronized (database) { Table table = database.getFirstUserTable(); if (table != null) throw DbException.get(90141, table.getTraceSQL());  database.setJavaObjectSerializerName(this.stringValue); addOrUpdateSetting(str1, this.stringValue, 0); }  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 0: this.session.getUser().checkAdmin(); n = getIntValue(); synchronized (database) { database.setIgnoreCase((n == 1)); addOrUpdateSetting(str1, (String)null, n); }  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 16: this.session.getUser().checkAdmin(); n = getIntValue(); synchronized (database) { database.setLockMode(n); addOrUpdateSetting(str1, (String)null, n); }  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 4: n = getIntValue(); if (n < 0) throw DbException.getInvalidValueException("LOCK_TIMEOUT", Integer.valueOf(n));  this.session.setLockTimeout(n); database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 20: this.session.getUser().checkAdmin(); n = getIntValue(); if (n < 0) throw DbException.getInvalidValueException("MAX_LENGTH_INPLACE_LOB", Integer.valueOf(n));  synchronized (database) { database.setMaxLengthInplaceLob(n); addOrUpdateSetting(str1, (String)null, n); }  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 1: this.session.getUser().checkAdmin(); n = getIntValue(); if (n < 0) throw DbException.getInvalidValueException("MAX_LOG_SIZE", Integer.valueOf(n));  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 15: this.session.getUser().checkAdmin(); n = getIntValue(); if (n < 0) throw DbException.getInvalidValueException("MAX_MEMORY_ROWS", Integer.valueOf(n));  synchronized (database) { database.setMaxMemoryRows(n); addOrUpdateSetting(str1, (String)null, n); }  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 19: this.session.getUser().checkAdmin(); n = getIntValue(); if (n < 0) throw DbException.getInvalidValueException("MAX_MEMORY_UNDO", Integer.valueOf(n));  synchronized (database) { addOrUpdateSetting(str1, (String)null, n); }  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 26: this.session.getUser().checkAdmin(); n = getIntValue(); if (n < 0) throw DbException.getInvalidValueException("MAX_OPERATION_MEMORY", Integer.valueOf(n));  database.setMaxOperationMemory(n); database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 2: mode = Mode.getInstance(this.stringValue); if (mode == null) throw DbException.get(90088, this.stringValue);  if (database.getMode() != mode) { this.session.getUser().checkAdmin(); database.setMode(mode); }  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 23: this.session.getUser().checkAdmin(); database.setOptimizeReuseResults((getIntValue() != 0)); database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 30: m = getIntValue(); if (m < 0) throw DbException.getInvalidValueException("QUERY_TIMEOUT", Integer.valueOf(m));  this.session.setQueryTimeout(m); database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 31: DbException.getUnsupportedException("MV_STORE + SET REDO_LOG_BINARY"); database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 25: this.session.getUser().checkAdmin(); m = getIntValue(); if (m < 0 || m > 1) throw DbException.getInvalidValueException("REFERENTIAL_INTEGRITY", Integer.valueOf(m));  database.setReferentialIntegrity((m == 1)); database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 34: this.session.getUser().checkAdmin(); m = getIntValue(); if (m < 0 || m > 1) throw DbException.getInvalidValueException("QUERY_STATISTICS", Integer.valueOf(m));  database.setQueryStatistics((m == 1)); database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 35: this.session.getUser().checkAdmin(); m = getIntValue(); if (m < 1) throw DbException.getInvalidValueException("QUERY_STATISTICS_MAX_ENTRIES", Integer.valueOf(m));  database.setQueryStatisticsMaxEntries(m); database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 22: schema = database.getSchema(this.expression.optimize(this.session).getValue(this.session).getString()); this.session.setCurrentSchema(schema); database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 24: this.session.setSchemaSearchPath(this.stringValueList); database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 40: str2 = database.getShortName(); str3 = this.expression.optimize(this.session).getValue(this.session).getString(); if (str3 == null || (!database.equalsIdentifiers(str2, str3) && !database.equalsIdentifiers(str2, str3.trim()))) throw DbException.get(90013, this.stringValue);  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 9: this.session.getUser().checkAdmin(); if (getPersistedObjectId() == 0) database.getTraceSystem().setLevelFile(getIntValue());  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 8: this.session.getUser().checkAdmin(); if (getPersistedObjectId() == 0) database.getTraceSystem().setLevelSystemOut(getIntValue());  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 10: this.session.getUser().checkAdmin(); k = getIntValue(); if (k < 0) throw DbException.getInvalidValueException("TRACE_MAX_FILE_SIZE", Integer.valueOf(k));  i2 = k * 1048576; synchronized (database) { database.getTraceSystem().setMaxFileSize(i2); addOrUpdateSetting(str1, (String)null, k); }  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 18: k = getIntValue(); if (k < 0) throw DbException.getInvalidValueException("THROTTLE", Integer.valueOf(k));  this.session.setThrottle(k); database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 29: expression = this.expression.optimize(this.session); this.session.setVariable(this.stringValue, expression.getValue(this.session)); database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 13: this.session.getUser().checkAdmin(); j = getIntValue(); if (j < 0) throw DbException.getInvalidValueException("WRITE_DELAY", Integer.valueOf(j));  synchronized (database) { database.setWriteDelay(j); addOrUpdateSetting(str1, (String)null, j); }  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 33: this.session.getUser().checkAdmin(); j = getIntValue(); if (j < 0) throw DbException.getInvalidValueException("RETENTION_TIME", Integer.valueOf(j));  synchronized (database) { database.setRetentionTime(j); addOrUpdateSetting(str1, (String)null, j); }  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 36: j = getIntValue(); if (j != 0 && j != 1) throw DbException.getInvalidValueException("LAZY_QUERY_EXECUTION", Integer.valueOf(j));  this.session.setLazyQueryExecution((j == 1)); database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 37: this.session.getUser().checkAdmin(); j = getIntValue(); if (j != 0 && j != 1) throw DbException.getInvalidValueException("BUILTIN_ALIAS_OVERRIDE", Integer.valueOf(j));  database.setAllowBuiltinAliasOverride((j == 1)); database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 38: this.session.getUser().checkAdmin(); bool = this.expression.optimize(this.session).getBooleanValue(this.session); try { synchronized (database) { if (bool) { database.setAuthenticator(AuthenticatorFactory.createAuthenticator()); } else { database.setAuthenticator(null); }  addOrUpdateSetting(str1, bool ? "TRUE" : "FALSE", 0); }  } catch (Exception exception) { if (database.isStarting()) { database.getTrace(2).error(exception, "{0}: failed to set authenticator during database start ", new Object[] { this.expression.toString() }); } else { throw DbException.convert(exception); }  }  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 39: this.session.getUser().checkAdmin(); i = getIntValue(); synchronized (database) { database.setIgnoreCatalogs((i == 1)); addOrUpdateSetting(str1, (String)null, i); }  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 41: this.session.setNonKeywords(Parser.parseNonKeywords(this.stringValueList)); database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 42: this.session.setTimeZone((this.expression == null) ? DateTimeUtils.getTimeZone() : parseTimeZone(this.expression.getValue(this.session))); database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 43: this.session.setVariableBinary(this.expression.getBooleanValue(this.session)); database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 44: try { defaultNullOrdering = DefaultNullOrdering.valueOf(StringUtils.toUpperEnglish(this.stringValue)); } catch (RuntimeException runtimeException) { throw DbException.getInvalidValueException("DEFAULT_NULL_ORDERING", this.stringValue); }  if (database.getDefaultNullOrdering() != defaultNullOrdering) { this.session.getUser().checkAdmin(); database.setDefaultNullOrdering(defaultNullOrdering); }  database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;case 45: this.session.setTruncateLargeLength(this.expression.getBooleanValue(this.session)); database.getNextModificationDataId(); database.getNextModificationMetaId(); return 0L;
/*     */     } 
/*     */     throw DbException.getInternalError("type=" + this.type); }
/*     */    private static TimeZoneProvider parseTimeZone(Value paramValue) {
/* 571 */     if (DataType.isCharacterStringType(paramValue.getValueType())) {
/*     */       TimeZoneProvider timeZoneProvider;
/*     */       try {
/* 574 */         timeZoneProvider = TimeZoneProvider.ofId(paramValue.getString());
/* 575 */       } catch (IllegalArgumentException illegalArgumentException) {
/* 576 */         throw DbException.getInvalidValueException("TIME ZONE", paramValue.getTraceSQL());
/*     */       } 
/* 578 */       return timeZoneProvider;
/* 579 */     }  if (paramValue == ValueNull.INSTANCE) {
/* 580 */       throw DbException.getInvalidValueException("TIME ZONE", paramValue);
/*     */     }
/* 582 */     return TimeZoneProvider.ofOffset(TimeZoneOperation.parseInterval(paramValue));
/*     */   }
/*     */   
/*     */   private int getIntValue() {
/* 586 */     this.expression = this.expression.optimize(this.session);
/* 587 */     return this.expression.getValue(this.session).getInt();
/*     */   }
/*     */   
/*     */   public void setInt(int paramInt) {
/* 591 */     this.expression = (Expression)ValueExpression.get((Value)ValueInteger.get(paramInt));
/*     */   }
/*     */   
/*     */   public void setExpression(Expression paramExpression) {
/* 595 */     this.expression = paramExpression;
/*     */   }
/*     */   
/*     */   private void addOrUpdateSetting(String paramString1, String paramString2, int paramInt) {
/* 599 */     addOrUpdateSetting(this.session, paramString1, paramString2, paramInt);
/*     */   }
/*     */   
/*     */   private void addOrUpdateSetting(SessionLocal paramSessionLocal, String paramString1, String paramString2, int paramInt) {
/* 603 */     Database database = paramSessionLocal.getDatabase();
/* 604 */     assert Thread.holdsLock(database);
/* 605 */     if (database.isReadOnly()) {
/*     */       return;
/*     */     }
/* 608 */     Setting setting = database.findSetting(paramString1);
/* 609 */     boolean bool = false;
/* 610 */     if (setting == null) {
/* 611 */       bool = true;
/* 612 */       int i = getObjectId();
/* 613 */       setting = new Setting(database, i, paramString1);
/*     */     } 
/* 615 */     if (paramString2 != null) {
/* 616 */       if (!bool && setting.getStringValue().equals(paramString2)) {
/*     */         return;
/*     */       }
/* 619 */       setting.setStringValue(paramString2);
/*     */     } else {
/* 621 */       if (!bool && setting.getIntValue() == paramInt) {
/*     */         return;
/*     */       }
/* 624 */       setting.setIntValue(paramInt);
/*     */     } 
/* 626 */     if (bool) {
/* 627 */       database.addDatabaseObject(paramSessionLocal, (DbObject)setting);
/*     */     } else {
/* 629 */       database.updateMeta(paramSessionLocal, (DbObject)setting);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needRecompile() {
/* 635 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface queryMeta() {
/* 640 */     return null;
/*     */   }
/*     */   
/*     */   public void setStringArray(String[] paramArrayOfString) {
/* 644 */     this.stringValueList = paramArrayOfString;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 649 */     return 67;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\Set.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */