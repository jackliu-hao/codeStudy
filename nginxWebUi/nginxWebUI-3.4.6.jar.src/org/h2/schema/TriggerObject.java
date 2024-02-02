/*     */ package org.h2.schema;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Arrays;
/*     */ import org.h2.api.Trigger;
/*     */ import org.h2.engine.Session;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.jdbc.JdbcConnection;
/*     */ import org.h2.jdbc.JdbcResultSet;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.result.SimpleResult;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.tools.TriggerAdapter;
/*     */ import org.h2.util.JdbcUtils;
/*     */ import org.h2.util.SourceCompiler;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueToObjectConverter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class TriggerObject
/*     */   extends SchemaObject
/*     */ {
/*     */   public static final int DEFAULT_QUEUE_SIZE = 1024;
/*     */   private boolean insteadOf;
/*     */   private boolean before;
/*     */   private int typeMask;
/*     */   private boolean rowBased;
/*     */   private boolean onRollback;
/*  51 */   private int queueSize = 1024;
/*     */   private boolean noWait;
/*     */   private Table table;
/*     */   private String triggerClassName;
/*     */   private String triggerSource;
/*     */   private Trigger triggerCallback;
/*     */   
/*     */   public TriggerObject(Schema paramSchema, int paramInt, String paramString, Table paramTable) {
/*  59 */     super(paramSchema, paramInt, paramString, 12);
/*  60 */     this.table = paramTable;
/*  61 */     setTemporary(paramTable.isTemporary());
/*     */   }
/*     */   
/*     */   public void setBefore(boolean paramBoolean) {
/*  65 */     this.before = paramBoolean;
/*     */   }
/*     */   
/*     */   public boolean isInsteadOf() {
/*  69 */     return this.insteadOf;
/*     */   }
/*     */   
/*     */   public void setInsteadOf(boolean paramBoolean) {
/*  73 */     this.insteadOf = paramBoolean;
/*     */   }
/*     */   
/*     */   private synchronized void load() {
/*  77 */     if (this.triggerCallback != null)
/*     */       return; 
/*     */     try {
/*     */       Trigger trigger;
/*  81 */       SessionLocal sessionLocal = this.database.getSystemSession();
/*  82 */       JdbcConnection jdbcConnection = sessionLocal.createConnection(false);
/*     */       
/*  84 */       if (this.triggerClassName != null) {
/*  85 */         trigger = (Trigger)JdbcUtils.loadUserClass(this.triggerClassName).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/*     */       } else {
/*  87 */         trigger = loadFromSource();
/*     */       } 
/*  89 */       this.triggerCallback = trigger;
/*  90 */       this.triggerCallback.init((Connection)jdbcConnection, getSchema().getName(), getName(), this.table
/*  91 */           .getName(), this.before, this.typeMask);
/*  92 */     } catch (Throwable throwable) {
/*     */       
/*  94 */       this.triggerCallback = null;
/*  95 */       throw DbException.get(90043, throwable, new String[] { getName(), (this.triggerClassName != null) ? this.triggerClassName : "..source..", throwable
/*  96 */             .toString() });
/*     */     } 
/*     */   }
/*     */   
/*     */   private Trigger loadFromSource() {
/* 101 */     SourceCompiler sourceCompiler = this.database.getCompiler();
/* 102 */     synchronized (sourceCompiler) {
/* 103 */       String str = "org.h2.dynamic.trigger." + getName();
/* 104 */       sourceCompiler.setSource(str, this.triggerSource);
/*     */       try {
/* 106 */         if (SourceCompiler.isJavaxScriptSource(this.triggerSource)) {
/* 107 */           return (Trigger)sourceCompiler.getCompiledScript(str).eval();
/*     */         }
/* 109 */         Method method = sourceCompiler.getMethod(str);
/* 110 */         if ((method.getParameterTypes()).length > 0) {
/* 111 */           throw new IllegalStateException("No parameters are allowed for a trigger");
/*     */         }
/* 113 */         return (Trigger)method.invoke(null, new Object[0]);
/*     */       }
/* 115 */       catch (DbException dbException) {
/* 116 */         throw dbException;
/* 117 */       } catch (Exception exception) {
/* 118 */         throw DbException.get(42000, exception, new String[] { this.triggerSource });
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
/*     */   public void setTriggerClassName(String paramString, boolean paramBoolean) {
/* 131 */     setTriggerAction(paramString, (String)null, paramBoolean);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTriggerSource(String paramString, boolean paramBoolean) {
/* 142 */     setTriggerAction((String)null, paramString, paramBoolean);
/*     */   }
/*     */   
/*     */   private void setTriggerAction(String paramString1, String paramString2, boolean paramBoolean) {
/* 146 */     this.triggerClassName = paramString1;
/* 147 */     this.triggerSource = paramString2;
/*     */     try {
/* 149 */       load();
/* 150 */     } catch (DbException dbException) {
/* 151 */       if (!paramBoolean) {
/* 152 */         throw dbException;
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
/*     */   public void fire(SessionLocal paramSessionLocal, int paramInt, boolean paramBoolean) {
/* 167 */     if (this.rowBased || this.before != paramBoolean || (this.typeMask & paramInt) == 0) {
/*     */       return;
/*     */     }
/* 170 */     load();
/* 171 */     JdbcConnection jdbcConnection = paramSessionLocal.createConnection(false);
/* 172 */     boolean bool = false;
/* 173 */     if (paramInt != 8) {
/* 174 */       bool = paramSessionLocal.setCommitOrRollbackDisabled(true);
/*     */     }
/* 176 */     Value value = paramSessionLocal.getLastIdentity();
/*     */     try {
/* 178 */       if (this.triggerCallback instanceof TriggerAdapter) {
/* 179 */         ((TriggerAdapter)this.triggerCallback).fire((Connection)jdbcConnection, (ResultSet)null, (ResultSet)null);
/*     */       } else {
/* 181 */         this.triggerCallback.fire((Connection)jdbcConnection, null, null);
/*     */       } 
/* 183 */     } catch (Throwable throwable) {
/* 184 */       throw getErrorExecutingTrigger(throwable);
/*     */     } finally {
/* 186 */       paramSessionLocal.setLastIdentity(value);
/* 187 */       if (paramInt != 8) {
/* 188 */         paramSessionLocal.setCommitOrRollbackDisabled(bool);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Object[] convertToObjectList(Row paramRow, JdbcConnection paramJdbcConnection) {
/* 194 */     if (paramRow == null) {
/* 195 */       return null;
/*     */     }
/* 197 */     int i = paramRow.getColumnCount();
/* 198 */     Object[] arrayOfObject = new Object[i];
/* 199 */     for (byte b = 0; b < i; b++) {
/* 200 */       arrayOfObject[b] = ValueToObjectConverter.valueToDefaultObject(paramRow.getValue(b), paramJdbcConnection, false);
/*     */     }
/* 202 */     return arrayOfObject;
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
/*     */   public boolean fireRow(SessionLocal paramSessionLocal, Table paramTable, Row paramRow1, Row paramRow2, boolean paramBoolean1, boolean paramBoolean2) {
/* 222 */     if (!this.rowBased || this.before != paramBoolean1) {
/* 223 */       return false;
/*     */     }
/* 225 */     if (paramBoolean2 && !this.onRollback) {
/* 226 */       return false;
/*     */     }
/* 228 */     load();
/* 229 */     boolean bool = false;
/* 230 */     if ((this.typeMask & 0x1) != 0 && 
/* 231 */       paramRow1 == null && paramRow2 != null) {
/* 232 */       bool = true;
/*     */     }
/*     */     
/* 235 */     if ((this.typeMask & 0x2) != 0 && 
/* 236 */       paramRow1 != null && paramRow2 != null) {
/* 237 */       bool = true;
/*     */     }
/*     */     
/* 240 */     if ((this.typeMask & 0x4) != 0 && 
/* 241 */       paramRow1 != null && paramRow2 == null) {
/* 242 */       bool = true;
/*     */     }
/*     */     
/* 245 */     if (!bool) {
/* 246 */       return false;
/*     */     }
/* 248 */     JdbcConnection jdbcConnection = paramSessionLocal.createConnection(false);
/* 249 */     boolean bool1 = paramSessionLocal.getAutoCommit();
/* 250 */     boolean bool2 = paramSessionLocal.setCommitOrRollbackDisabled(true);
/* 251 */     Value value = paramSessionLocal.getLastIdentity();
/*     */     
/* 253 */     try { paramSessionLocal.setAutoCommit(false);
/* 254 */       if (this.triggerCallback instanceof TriggerAdapter) {
/* 255 */         JdbcResultSet jdbcResultSet1 = (paramRow1 != null) ? createResultSet(jdbcConnection, paramTable, paramRow1, false) : null;
/* 256 */         JdbcResultSet jdbcResultSet2 = (paramRow2 != null) ? createResultSet(jdbcConnection, paramTable, paramRow2, this.before) : null;
/*     */         try {
/* 258 */           ((TriggerAdapter)this.triggerCallback).fire((Connection)jdbcConnection, (ResultSet)jdbcResultSet1, (ResultSet)jdbcResultSet2);
/* 259 */         } catch (Throwable throwable) {
/* 260 */           throw getErrorExecutingTrigger(throwable);
/*     */         } 
/* 262 */         if (jdbcResultSet2 != null) {
/* 263 */           Value[] arrayOfValue = jdbcResultSet2.getUpdateRow();
/* 264 */           if (arrayOfValue != null) {
/* 265 */             boolean bool3 = false; byte b; int i;
/* 266 */             for (b = 0, i = arrayOfValue.length; b < i; b++) {
/* 267 */               Value value1 = arrayOfValue[b];
/* 268 */               if (value1 != null) {
/* 269 */                 bool3 = true;
/* 270 */                 paramRow2.setValue(b, value1);
/*     */               } 
/*     */             } 
/* 273 */             if (bool3) {
/* 274 */               paramTable.convertUpdateRow(paramSessionLocal, paramRow2, true);
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } else {
/* 279 */         Object[] arrayOfObject1 = convertToObjectList(paramRow1, jdbcConnection);
/* 280 */         Object[] arrayOfObject2 = convertToObjectList(paramRow2, jdbcConnection);
/* 281 */         T[] arrayOfT = (this.before && arrayOfObject2 != null) ? Arrays.<T>copyOf((T[])arrayOfObject2, arrayOfObject2.length) : null;
/*     */         try {
/* 283 */           this.triggerCallback.fire((Connection)jdbcConnection, arrayOfObject1, arrayOfObject2);
/* 284 */         } catch (Throwable throwable) {
/* 285 */           throw getErrorExecutingTrigger(throwable);
/*     */         } 
/* 287 */         if (arrayOfT != null) {
/* 288 */           boolean bool3 = false;
/* 289 */           for (byte b = 0; b < arrayOfObject2.length; b++) {
/* 290 */             Object object = arrayOfObject2[b];
/* 291 */             if (object != arrayOfT[b]) {
/* 292 */               bool3 = true;
/* 293 */               paramRow2.setValue(b, ValueToObjectConverter.objectToValue((Session)paramSessionLocal, object, -1));
/*     */             } 
/*     */           } 
/* 296 */           if (bool3) {
/* 297 */             paramTable.convertUpdateRow(paramSessionLocal, paramRow2, true);
/*     */           }
/*     */         } 
/*     */       }  }
/* 301 */     catch (Exception exception)
/* 302 */     { if (this.onRollback)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 312 */         return this.insteadOf; }  throw DbException.convert(exception); } finally { paramSessionLocal.setLastIdentity(value); paramSessionLocal.setCommitOrRollbackDisabled(bool2); paramSessionLocal.setAutoCommit(bool1); }  return this.insteadOf;
/*     */   }
/*     */ 
/*     */   
/*     */   private static JdbcResultSet createResultSet(JdbcConnection paramJdbcConnection, Table paramTable, Row paramRow, boolean paramBoolean) throws SQLException {
/* 317 */     SimpleResult simpleResult = new SimpleResult(paramTable.getSchema().getName(), paramTable.getName());
/* 318 */     for (Column column : paramTable.getColumns()) {
/* 319 */       simpleResult.addColumn(column.getName(), column.getType());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 325 */     simpleResult.addRow(paramRow.getValueList());
/* 326 */     simpleResult.addRow(paramRow.getValueList());
/* 327 */     JdbcResultSet jdbcResultSet = new JdbcResultSet(paramJdbcConnection, null, null, (ResultInterface)simpleResult, -1, false, false, paramBoolean);
/* 328 */     jdbcResultSet.next();
/* 329 */     return jdbcResultSet;
/*     */   }
/*     */   
/*     */   private DbException getErrorExecutingTrigger(Throwable paramThrowable) {
/* 333 */     if (paramThrowable instanceof DbException) {
/* 334 */       return (DbException)paramThrowable;
/*     */     }
/* 336 */     if (paramThrowable instanceof SQLException) {
/* 337 */       return DbException.convert(paramThrowable);
/*     */     }
/* 339 */     return DbException.get(90044, paramThrowable, new String[] { getName(), (this.triggerClassName != null) ? this.triggerClassName : "..source..", paramThrowable
/* 340 */           .toString() });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTypeMask() {
/* 349 */     return this.typeMask;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTypeMask(int paramInt) {
/* 358 */     this.typeMask = paramInt;
/*     */   }
/*     */   
/*     */   public void setRowBased(boolean paramBoolean) {
/* 362 */     this.rowBased = paramBoolean;
/*     */   }
/*     */   
/*     */   public boolean isRowBased() {
/* 366 */     return this.rowBased;
/*     */   }
/*     */   
/*     */   public void setQueueSize(int paramInt) {
/* 370 */     this.queueSize = paramInt;
/*     */   }
/*     */   
/*     */   public int getQueueSize() {
/* 374 */     return this.queueSize;
/*     */   }
/*     */   
/*     */   public void setNoWait(boolean paramBoolean) {
/* 378 */     this.noWait = paramBoolean;
/*     */   }
/*     */   
/*     */   public boolean isNoWait() {
/* 382 */     return this.noWait;
/*     */   }
/*     */   
/*     */   public void setOnRollback(boolean paramBoolean) {
/* 386 */     this.onRollback = paramBoolean;
/*     */   }
/*     */   
/*     */   public boolean isOnRollback() {
/* 390 */     return this.onRollback;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQLForCopy(Table paramTable, String paramString) {
/* 395 */     StringBuilder stringBuilder = new StringBuilder("CREATE FORCE TRIGGER ");
/* 396 */     stringBuilder.append(paramString);
/* 397 */     if (this.insteadOf) {
/* 398 */       stringBuilder.append(" INSTEAD OF ");
/* 399 */     } else if (this.before) {
/* 400 */       stringBuilder.append(" BEFORE ");
/*     */     } else {
/* 402 */       stringBuilder.append(" AFTER ");
/*     */     } 
/* 404 */     getTypeNameList(stringBuilder).append(" ON ");
/* 405 */     paramTable.getSQL(stringBuilder, 0);
/* 406 */     if (this.rowBased) {
/* 407 */       stringBuilder.append(" FOR EACH ROW");
/*     */     }
/* 409 */     if (this.noWait) {
/* 410 */       stringBuilder.append(" NOWAIT");
/*     */     } else {
/* 412 */       stringBuilder.append(" QUEUE ").append(this.queueSize);
/*     */     } 
/* 414 */     if (this.triggerClassName != null) {
/* 415 */       StringUtils.quoteStringSQL(stringBuilder.append(" CALL "), this.triggerClassName);
/*     */     } else {
/* 417 */       StringUtils.quoteStringSQL(stringBuilder.append(" AS "), this.triggerSource);
/*     */     } 
/* 419 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringBuilder getTypeNameList(StringBuilder paramStringBuilder) {
/* 429 */     boolean bool = false;
/* 430 */     if ((this.typeMask & 0x1) != 0) {
/* 431 */       bool = true;
/* 432 */       paramStringBuilder.append("INSERT");
/*     */     } 
/* 434 */     if ((this.typeMask & 0x2) != 0) {
/* 435 */       if (bool) {
/* 436 */         paramStringBuilder.append(", ");
/*     */       }
/* 438 */       bool = true;
/* 439 */       paramStringBuilder.append("UPDATE");
/*     */     } 
/* 441 */     if ((this.typeMask & 0x4) != 0) {
/* 442 */       if (bool) {
/* 443 */         paramStringBuilder.append(", ");
/*     */       }
/* 445 */       bool = true;
/* 446 */       paramStringBuilder.append("DELETE");
/*     */     } 
/* 448 */     if ((this.typeMask & 0x8) != 0) {
/* 449 */       if (bool) {
/* 450 */         paramStringBuilder.append(", ");
/*     */       }
/* 452 */       bool = true;
/* 453 */       paramStringBuilder.append("SELECT");
/*     */     } 
/* 455 */     if (this.onRollback) {
/* 456 */       if (bool) {
/* 457 */         paramStringBuilder.append(", ");
/*     */       }
/* 459 */       paramStringBuilder.append("ROLLBACK");
/*     */     } 
/* 461 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/* 466 */     return getCreateSQLForCopy(this.table, getSQL(0));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 471 */     return 4;
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/* 476 */     this.table.removeTrigger(this);
/* 477 */     this.database.removeMeta(paramSessionLocal, getId());
/* 478 */     if (this.triggerCallback != null) {
/*     */       try {
/* 480 */         this.triggerCallback.remove();
/* 481 */       } catch (SQLException sQLException) {
/* 482 */         throw DbException.convert(sQLException);
/*     */       } 
/*     */     }
/* 485 */     this.table = null;
/* 486 */     this.triggerClassName = null;
/* 487 */     this.triggerSource = null;
/* 488 */     this.triggerCallback = null;
/* 489 */     invalidate();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Table getTable() {
/* 498 */     return this.table;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isBefore() {
/* 507 */     return this.before;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getTriggerClassName() {
/* 516 */     return this.triggerClassName;
/*     */   }
/*     */   
/*     */   public String getTriggerSource() {
/* 520 */     return this.triggerSource;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws SQLException {
/* 528 */     if (this.triggerCallback != null) {
/* 529 */       this.triggerCallback.close();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSelectTrigger() {
/* 539 */     return ((this.typeMask & 0x8) != 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\schema\TriggerObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */