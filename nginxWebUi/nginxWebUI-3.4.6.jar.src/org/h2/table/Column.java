/*     */ package org.h2.table;
/*     */ 
/*     */ import java.util.Objects;
/*     */ import org.h2.command.Parser;
/*     */ import org.h2.command.ddl.SequenceOptions;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.expression.ValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.schema.Domain;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.schema.SchemaObject;
/*     */ import org.h2.schema.Sequence;
/*     */ import org.h2.util.HasSQL;
/*     */ import org.h2.util.ParserUtil;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Typed;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueUuid;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Column
/*     */   implements HasSQL, Typed, ColumnTemplate
/*     */ {
/*     */   public static final String ROWID = "_ROWID_";
/*     */   public static final int NOT_NULLABLE = 0;
/*     */   public static final int NULLABLE = 1;
/*     */   public static final int NULLABLE_UNKNOWN = 2;
/*     */   private TypeInfo type;
/*     */   private Table table;
/*     */   private String name;
/*     */   private int columnId;
/*     */   private boolean nullable = true;
/*     */   private Expression defaultExpression;
/*     */   private Expression onUpdateExpression;
/*     */   private SequenceOptions identityOptions;
/*     */   private boolean defaultOnNull;
/*     */   private Sequence sequence;
/*     */   private boolean isGeneratedAlways;
/*     */   private GeneratedColumnResolver generatedTableFilter;
/*     */   private int selectivity;
/*     */   private String comment;
/*     */   private boolean primaryKey;
/*     */   private boolean visible = true;
/*     */   private boolean rowId;
/*     */   private Domain domain;
/*     */   
/*     */   public static StringBuilder writeColumns(StringBuilder paramStringBuilder, Column[] paramArrayOfColumn, int paramInt) {
/*     */     byte b;
/*     */     int i;
/*  93 */     for (b = 0, i = paramArrayOfColumn.length; b < i; b++) {
/*  94 */       if (b > 0) {
/*  95 */         paramStringBuilder.append(", ");
/*     */       }
/*  97 */       paramArrayOfColumn[b].getSQL(paramStringBuilder, paramInt);
/*     */     } 
/*  99 */     return paramStringBuilder;
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
/*     */   public static StringBuilder writeColumns(StringBuilder paramStringBuilder, Column[] paramArrayOfColumn, String paramString1, String paramString2, int paramInt) {
/*     */     byte b;
/*     */     int i;
/* 119 */     for (b = 0, i = paramArrayOfColumn.length; b < i; b++) {
/* 120 */       if (b > 0) {
/* 121 */         paramStringBuilder.append(paramString1);
/*     */       }
/* 123 */       paramArrayOfColumn[b].getSQL(paramStringBuilder, paramInt).append(paramString2);
/*     */     } 
/* 125 */     return paramStringBuilder;
/*     */   }
/*     */   
/*     */   public Column(String paramString, TypeInfo paramTypeInfo) {
/* 129 */     this.name = paramString;
/* 130 */     this.type = paramTypeInfo;
/*     */   }
/*     */   
/*     */   public Column(String paramString, TypeInfo paramTypeInfo, Table paramTable, int paramInt) {
/* 134 */     this.name = paramString;
/* 135 */     this.type = paramTypeInfo;
/* 136 */     this.table = paramTable;
/* 137 */     this.columnId = paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 142 */     if (paramObject == this)
/* 143 */       return true; 
/* 144 */     if (!(paramObject instanceof Column)) {
/* 145 */       return false;
/*     */     }
/* 147 */     Column column = (Column)paramObject;
/* 148 */     if (this.table == null || column.table == null || this.name == null || column.name == null)
/*     */     {
/* 150 */       return false;
/*     */     }
/* 152 */     if (this.table != column.table) {
/* 153 */       return false;
/*     */     }
/* 155 */     return this.name.equals(column.name);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 160 */     if (this.table == null || this.name == null) {
/* 161 */       return 0;
/*     */     }
/* 163 */     return this.table.getId() ^ this.name.hashCode();
/*     */   }
/*     */   
/*     */   public Column getClone() {
/* 167 */     Column column = new Column(this.name, this.type);
/* 168 */     column.copy(this);
/* 169 */     return column;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Value convert(CastDataProvider paramCastDataProvider, Value paramValue) {
/*     */     try {
/* 181 */       return paramValue.convertTo(this.type, paramCastDataProvider, this);
/* 182 */     } catch (DbException dbException) {
/* 183 */       if (dbException.getErrorCode() == 22018) {
/* 184 */         dbException = getDataConversionError(paramValue, dbException);
/*     */       }
/* 186 */       throw dbException;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isIdentity() {
/* 196 */     return (this.sequence != null || this.identityOptions != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isGenerated() {
/* 205 */     return (this.isGeneratedAlways && this.defaultExpression != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isGeneratedAlways() {
/* 216 */     return this.isGeneratedAlways;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGeneratedExpression(Expression paramExpression) {
/* 226 */     this.isGeneratedAlways = true;
/* 227 */     this.defaultExpression = paramExpression;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTable(Table paramTable, int paramInt) {
/* 237 */     this.table = paramTable;
/* 238 */     this.columnId = paramInt;
/*     */   }
/*     */   
/*     */   public Table getTable() {
/* 242 */     return this.table;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDefaultExpression(SessionLocal paramSessionLocal, Expression paramExpression) {
/*     */     ValueExpression valueExpression;
/* 248 */     if (paramExpression != null) {
/* 249 */       paramExpression = paramExpression.optimize(paramSessionLocal);
/* 250 */       if (paramExpression.isConstant()) {
/* 251 */         valueExpression = ValueExpression.get(paramExpression
/* 252 */             .getValue(paramSessionLocal));
/*     */       }
/*     */     } 
/* 255 */     this.defaultExpression = (Expression)valueExpression;
/* 256 */     this.isGeneratedAlways = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setOnUpdateExpression(SessionLocal paramSessionLocal, Expression paramExpression) {
/*     */     ValueExpression valueExpression;
/* 262 */     if (paramExpression != null) {
/* 263 */       paramExpression = paramExpression.optimize(paramSessionLocal);
/* 264 */       if (paramExpression.isConstant()) {
/* 265 */         valueExpression = ValueExpression.get(paramExpression.getValue(paramSessionLocal));
/*     */       }
/*     */     } 
/* 268 */     this.onUpdateExpression = (Expression)valueExpression;
/*     */   }
/*     */   
/*     */   public int getColumnId() {
/* 272 */     return this.columnId;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSQL(int paramInt) {
/* 277 */     return this.rowId ? this.name : Parser.quoteIdentifier(this.name, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 282 */     return this.rowId ? paramStringBuilder.append(this.name) : ParserUtil.quoteIdentifier(paramStringBuilder, this.name, paramInt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringBuilder getSQLWithTable(StringBuilder paramStringBuilder, int paramInt) {
/* 293 */     return getSQL(this.table.getSQL(paramStringBuilder, paramInt).append('.'), paramInt);
/*     */   }
/*     */   
/*     */   public String getName() {
/* 297 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 302 */     return this.type;
/*     */   }
/*     */   
/*     */   public void setNullable(boolean paramBoolean) {
/* 306 */     this.nullable = paramBoolean;
/*     */   }
/*     */   
/*     */   public boolean getVisible() {
/* 310 */     return this.visible;
/*     */   }
/*     */   
/*     */   public void setVisible(boolean paramBoolean) {
/* 314 */     this.visible = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public Domain getDomain() {
/* 319 */     return this.domain;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDomain(Domain paramDomain) {
/* 324 */     this.domain = paramDomain;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRowId() {
/* 333 */     return this.rowId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRowId(boolean paramBoolean) {
/* 342 */     this.rowId = paramBoolean;
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
/*     */   Value validateConvertUpdateSequence(SessionLocal paramSessionLocal, Value paramValue, Row paramRow) {
/*     */     // Byte code:
/*     */     //   0: aload_2
/*     */     //   1: ifnonnull -> 31
/*     */     //   4: aload_0
/*     */     //   5: getfield sequence : Lorg/h2/schema/Sequence;
/*     */     //   8: ifnull -> 24
/*     */     //   11: aload_1
/*     */     //   12: aload_0
/*     */     //   13: getfield sequence : Lorg/h2/schema/Sequence;
/*     */     //   16: aconst_null
/*     */     //   17: invokevirtual getNextValueFor : (Lorg/h2/schema/Sequence;Lorg/h2/command/Prepared;)Lorg/h2/value/Value;
/*     */     //   20: astore_2
/*     */     //   21: goto -> 56
/*     */     //   24: aload_0
/*     */     //   25: aload_1
/*     */     //   26: aload_3
/*     */     //   27: invokespecial getDefaultOrGenerated : (Lorg/h2/engine/SessionLocal;Lorg/h2/result/Row;)Lorg/h2/value/Value;
/*     */     //   30: astore_2
/*     */     //   31: aload_2
/*     */     //   32: getstatic org/h2/value/ValueNull.INSTANCE : Lorg/h2/value/ValueNull;
/*     */     //   35: if_acmpne -> 56
/*     */     //   38: aload_0
/*     */     //   39: getfield nullable : Z
/*     */     //   42: ifne -> 56
/*     */     //   45: sipush #23502
/*     */     //   48: aload_0
/*     */     //   49: getfield name : Ljava/lang/String;
/*     */     //   52: invokestatic get : (ILjava/lang/String;)Lorg/h2/message/DbException;
/*     */     //   55: athrow
/*     */     //   56: aload_2
/*     */     //   57: aload_0
/*     */     //   58: getfield type : Lorg/h2/value/TypeInfo;
/*     */     //   61: aload_1
/*     */     //   62: aload_0
/*     */     //   63: getfield name : Ljava/lang/String;
/*     */     //   66: invokevirtual convertForAssignTo : (Lorg/h2/value/TypeInfo;Lorg/h2/engine/CastDataProvider;Ljava/lang/Object;)Lorg/h2/value/Value;
/*     */     //   69: astore_2
/*     */     //   70: goto -> 98
/*     */     //   73: astore #4
/*     */     //   75: aload #4
/*     */     //   77: invokevirtual getErrorCode : ()I
/*     */     //   80: sipush #22018
/*     */     //   83: if_icmpne -> 95
/*     */     //   86: aload_0
/*     */     //   87: aload_2
/*     */     //   88: aload #4
/*     */     //   90: invokespecial getDataConversionError : (Lorg/h2/value/Value;Lorg/h2/message/DbException;)Lorg/h2/message/DbException;
/*     */     //   93: astore #4
/*     */     //   95: aload #4
/*     */     //   97: athrow
/*     */     //   98: aload_0
/*     */     //   99: getfield domain : Lorg/h2/schema/Domain;
/*     */     //   102: ifnull -> 114
/*     */     //   105: aload_0
/*     */     //   106: getfield domain : Lorg/h2/schema/Domain;
/*     */     //   109: aload_1
/*     */     //   110: aload_2
/*     */     //   111: invokevirtual checkConstraints : (Lorg/h2/engine/SessionLocal;Lorg/h2/value/Value;)V
/*     */     //   114: aload_0
/*     */     //   115: getfield sequence : Lorg/h2/schema/Sequence;
/*     */     //   118: ifnull -> 140
/*     */     //   121: aload_1
/*     */     //   122: invokevirtual getMode : ()Lorg/h2/engine/Mode;
/*     */     //   125: getfield updateSequenceOnManualIdentityInsertion : Z
/*     */     //   128: ifeq -> 140
/*     */     //   131: aload_0
/*     */     //   132: aload_1
/*     */     //   133: aload_2
/*     */     //   134: invokevirtual getLong : ()J
/*     */     //   137: invokespecial updateSequenceIfRequired : (Lorg/h2/engine/SessionLocal;J)V
/*     */     //   140: aload_2
/*     */     //   141: areturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #357	-> 0
/*     */     //   #358	-> 4
/*     */     //   #359	-> 11
/*     */     //   #360	-> 21
/*     */     //   #362	-> 24
/*     */     //   #364	-> 31
/*     */     //   #365	-> 45
/*     */     //   #369	-> 56
/*     */     //   #375	-> 70
/*     */     //   #370	-> 73
/*     */     //   #371	-> 75
/*     */     //   #372	-> 86
/*     */     //   #374	-> 95
/*     */     //   #376	-> 98
/*     */     //   #377	-> 105
/*     */     //   #379	-> 114
/*     */     //   #380	-> 131
/*     */     //   #382	-> 140
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   56	70	73	org/h2/message/DbException
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
/*     */   private Value getDefaultOrGenerated(SessionLocal paramSessionLocal, Row paramRow) {
/*     */     Value value;
/* 387 */     Expression expression = getEffectiveDefaultExpression();
/* 388 */     if (expression == null) {
/* 389 */       ValueNull valueNull = ValueNull.INSTANCE;
/*     */     }
/* 391 */     else if (this.isGeneratedAlways) {
/* 392 */       synchronized (this) {
/* 393 */         this.generatedTableFilter.set(paramRow);
/*     */         try {
/* 395 */           value = expression.getValue(paramSessionLocal);
/*     */         } finally {
/* 397 */           this.generatedTableFilter.set(null);
/*     */         } 
/*     */       } 
/*     */     } else {
/* 401 */       value = expression.getValue(paramSessionLocal);
/*     */     } 
/*     */     
/* 404 */     return value;
/*     */   }
/*     */   
/*     */   private DbException getDataConversionError(Value paramValue, DbException paramDbException) {
/* 408 */     StringBuilder stringBuilder = (new StringBuilder()).append(paramValue.getTraceSQL()).append(" (");
/* 409 */     if (this.table != null) {
/* 410 */       stringBuilder.append(this.table.getName()).append(": ");
/*     */     }
/* 412 */     stringBuilder.append(getCreateSQL()).append(')');
/* 413 */     return DbException.get(22018, (Throwable)paramDbException, new String[] { stringBuilder.toString() });
/*     */   }
/*     */   
/*     */   private void updateSequenceIfRequired(SessionLocal paramSessionLocal, long paramLong) {
/* 417 */     if (this.sequence.getCycle() == Sequence.Cycle.EXHAUSTED) {
/*     */       return;
/*     */     }
/* 420 */     long l1 = this.sequence.getCurrentValue();
/* 421 */     long l2 = this.sequence.getIncrement();
/* 422 */     if (l2 > 0L) {
/* 423 */       if (paramLong < l1) {
/*     */         return;
/*     */       }
/* 426 */     } else if (paramLong > l1) {
/*     */       return;
/*     */     } 
/*     */     try {
/* 430 */       this.sequence.modify(Long.valueOf(paramLong + l2), null, null, null, null, null, null);
/* 431 */     } catch (DbException dbException) {
/* 432 */       if (dbException.getErrorCode() == 90009) {
/*     */         return;
/*     */       }
/* 435 */       throw dbException;
/*     */     } 
/* 437 */     this.sequence.flush(paramSessionLocal);
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
/*     */   public void initializeSequence(SessionLocal paramSessionLocal, Schema paramSchema, int paramInt, boolean paramBoolean) {
/* 450 */     if (this.identityOptions == null) {
/* 451 */       throw DbException.getInternalError();
/*     */     }
/*     */ 
/*     */     
/*     */     while (true) {
/* 456 */       String str = "SYSTEM_SEQUENCE_" + StringUtils.toUpperEnglish(ValueUuid.getNewRandom().getString().replace('-', '_'));
/* 457 */       if (paramSchema.findSequence(str) == null) {
/* 458 */         this.identityOptions.setDataType(this.type);
/* 459 */         Sequence sequence = new Sequence(paramSessionLocal, paramSchema, paramInt, str, this.identityOptions, true);
/* 460 */         sequence.setTemporary(paramBoolean);
/* 461 */         paramSessionLocal.getDatabase().addSchemaObject(paramSessionLocal, (SchemaObject)sequence);
/*     */         
/* 463 */         setSequence(sequence, this.isGeneratedAlways);
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   } public void prepareExpressions(SessionLocal paramSessionLocal) {
/* 468 */     if (this.defaultExpression != null) {
/* 469 */       if (this.isGeneratedAlways) {
/* 470 */         this.generatedTableFilter = new GeneratedColumnResolver(this.table);
/* 471 */         this.defaultExpression.mapColumns(this.generatedTableFilter, 0, 0);
/*     */       } 
/* 473 */       this.defaultExpression = this.defaultExpression.optimize(paramSessionLocal);
/*     */     } 
/* 475 */     if (this.onUpdateExpression != null) {
/* 476 */       this.onUpdateExpression = this.onUpdateExpression.optimize(paramSessionLocal);
/*     */     }
/* 478 */     if (this.domain != null) {
/* 479 */       this.domain.prepareExpressions(paramSessionLocal);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getCreateSQLWithoutName() {
/* 484 */     return getCreateSQL(new StringBuilder(), false);
/*     */   }
/*     */   
/*     */   public String getCreateSQL() {
/* 488 */     return getCreateSQL(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCreateSQL(boolean paramBoolean) {
/* 498 */     StringBuilder stringBuilder = new StringBuilder();
/* 499 */     if (this.name != null) {
/* 500 */       ParserUtil.quoteIdentifier(stringBuilder, this.name, 0).append(' ');
/*     */     }
/* 502 */     return getCreateSQL(stringBuilder, paramBoolean);
/*     */   }
/*     */   
/*     */   private String getCreateSQL(StringBuilder paramStringBuilder, boolean paramBoolean) {
/* 506 */     if (this.domain != null) {
/* 507 */       this.domain.getSQL(paramStringBuilder, 0);
/*     */     } else {
/* 509 */       this.type.getSQL(paramStringBuilder, 0);
/*     */     } 
/* 511 */     if (!this.visible) {
/* 512 */       paramStringBuilder.append(" INVISIBLE ");
/*     */     }
/* 514 */     if (this.sequence != null) {
/* 515 */       paramStringBuilder.append(" GENERATED ").append(this.isGeneratedAlways ? "ALWAYS" : "BY DEFAULT").append(" AS IDENTITY");
/* 516 */       if (!paramBoolean) {
/* 517 */         this.sequence.getSequenceOptionsSQL(paramStringBuilder.append('(')).append(')');
/*     */       }
/* 519 */     } else if (this.defaultExpression != null) {
/* 520 */       if (this.isGeneratedAlways) {
/* 521 */         this.defaultExpression.getEnclosedSQL(paramStringBuilder.append(" GENERATED ALWAYS AS "), 0);
/*     */       } else {
/* 523 */         this.defaultExpression.getUnenclosedSQL(paramStringBuilder.append(" DEFAULT "), 0);
/*     */       } 
/*     */     } 
/* 526 */     if (this.onUpdateExpression != null) {
/* 527 */       this.onUpdateExpression.getUnenclosedSQL(paramStringBuilder.append(" ON UPDATE "), 0);
/*     */     }
/* 529 */     if (this.defaultOnNull) {
/* 530 */       paramStringBuilder.append(" DEFAULT ON NULL");
/*     */     }
/* 532 */     if (paramBoolean && this.sequence != null) {
/* 533 */       this.sequence.getSQL(paramStringBuilder.append(" SEQUENCE "), 0);
/*     */     }
/* 535 */     if (this.selectivity != 0) {
/* 536 */       paramStringBuilder.append(" SELECTIVITY ").append(this.selectivity);
/*     */     }
/* 538 */     if (this.comment != null) {
/* 539 */       StringUtils.quoteStringSQL(paramStringBuilder.append(" COMMENT "), this.comment);
/*     */     }
/* 541 */     if (!this.nullable) {
/* 542 */       paramStringBuilder.append(" NOT NULL");
/*     */     }
/* 544 */     return paramStringBuilder.toString();
/*     */   }
/*     */   
/*     */   public boolean isNullable() {
/* 548 */     return this.nullable;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getDefaultExpression() {
/* 553 */     return this.defaultExpression;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression getEffectiveDefaultExpression() {
/* 564 */     if (this.sequence != null) {
/* 565 */       return null;
/*     */     }
/* 567 */     return (this.defaultExpression != null) ? this.defaultExpression : ((this.domain != null) ? this.domain
/* 568 */       .getEffectiveDefaultExpression() : null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getOnUpdateExpression() {
/* 573 */     return this.onUpdateExpression;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression getEffectiveOnUpdateExpression() {
/* 582 */     if (this.sequence != null || this.isGeneratedAlways) {
/* 583 */       return null;
/*     */     }
/* 585 */     return (this.onUpdateExpression != null) ? this.onUpdateExpression : ((this.domain != null) ? this.domain
/* 586 */       .getEffectiveOnUpdateExpression() : null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasIdentityOptions() {
/* 595 */     return (this.identityOptions != null);
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
/*     */   public void setIdentityOptions(SequenceOptions paramSequenceOptions, boolean paramBoolean) {
/* 607 */     this.identityOptions = paramSequenceOptions;
/* 608 */     this.isGeneratedAlways = paramBoolean;
/* 609 */     removeNonIdentityProperties();
/*     */   }
/*     */   
/*     */   private void removeNonIdentityProperties() {
/* 613 */     this.nullable = false;
/* 614 */     this.onUpdateExpression = this.defaultExpression = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SequenceOptions getIdentityOptions() {
/* 624 */     return this.identityOptions;
/*     */   }
/*     */   
/*     */   public void setDefaultOnNull(boolean paramBoolean) {
/* 628 */     this.defaultOnNull = paramBoolean;
/*     */   }
/*     */   
/*     */   public boolean isDefaultOnNull() {
/* 632 */     return this.defaultOnNull;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void rename(String paramString) {
/* 642 */     this.name = paramString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSequence(Sequence paramSequence, boolean paramBoolean) {
/* 652 */     this.sequence = paramSequence;
/* 653 */     this.isGeneratedAlways = paramBoolean;
/* 654 */     this.identityOptions = null;
/* 655 */     if (paramSequence != null) {
/* 656 */       removeNonIdentityProperties();
/* 657 */       if ((paramSequence.getDatabase().getMode()).identityColumnsHaveDefaultOnNull) {
/* 658 */         this.defaultOnNull = true;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public Sequence getSequence() {
/* 664 */     return this.sequence;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSelectivity() {
/* 674 */     return (this.selectivity == 0) ? 50 : this.selectivity;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSelectivity(int paramInt) {
/* 683 */     paramInt = (paramInt < 0) ? 0 : ((paramInt > 100) ? 100 : paramInt);
/* 684 */     this.selectivity = paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDefaultSQL() {
/* 689 */     return (this.defaultExpression == null) ? null : this.defaultExpression
/* 690 */       .getUnenclosedSQL(new StringBuilder(), 0).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getOnUpdateSQL() {
/* 695 */     return (this.onUpdateExpression == null) ? null : this.onUpdateExpression
/* 696 */       .getUnenclosedSQL(new StringBuilder(), 0).toString();
/*     */   }
/*     */   
/*     */   public void setComment(String paramString) {
/* 700 */     this.comment = (paramString != null && !paramString.isEmpty()) ? paramString : null;
/*     */   }
/*     */   
/*     */   public String getComment() {
/* 704 */     return this.comment;
/*     */   }
/*     */   
/*     */   public void setPrimaryKey(boolean paramBoolean) {
/* 708 */     this.primaryKey = paramBoolean;
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
/*     */   boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 720 */     if (paramExpressionVisitor.getType() == 7 && 
/* 721 */       this.sequence != null) {
/* 722 */       paramExpressionVisitor.getDependencies().add(this.sequence);
/*     */     }
/*     */     
/* 725 */     Expression expression = getEffectiveDefaultExpression();
/* 726 */     if (expression != null && !expression.isEverything(paramExpressionVisitor)) {
/* 727 */       return false;
/*     */     }
/* 729 */     expression = getEffectiveOnUpdateExpression();
/* 730 */     if (expression != null && !expression.isEverything(paramExpressionVisitor)) {
/* 731 */       return false;
/*     */     }
/* 733 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isPrimaryKey() {
/* 737 */     return this.primaryKey;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 742 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWideningConversion(Column paramColumn) {
/* 753 */     TypeInfo typeInfo = paramColumn.type;
/* 754 */     int i = this.type.getValueType();
/* 755 */     if (i != typeInfo.getValueType()) {
/* 756 */       return false;
/*     */     }
/* 758 */     long l1 = this.type.getPrecision();
/* 759 */     long l2 = typeInfo.getPrecision();
/* 760 */     if (l1 > l2 || (l1 < l2 && (i == 1 || i == 5)))
/*     */     {
/* 762 */       return false;
/*     */     }
/* 764 */     if (this.type.getScale() != typeInfo.getScale()) {
/* 765 */       return false;
/*     */     }
/* 767 */     if (!Objects.equals(this.type.getExtTypeInfo(), typeInfo.getExtTypeInfo())) {
/* 768 */       return false;
/*     */     }
/* 770 */     if (this.nullable && !paramColumn.nullable) {
/* 771 */       return false;
/*     */     }
/* 773 */     if (this.primaryKey != paramColumn.primaryKey) {
/* 774 */       return false;
/*     */     }
/* 776 */     if (this.identityOptions != null || paramColumn.identityOptions != null) {
/* 777 */       return false;
/*     */     }
/* 779 */     if (this.domain != paramColumn.domain) {
/* 780 */       return false;
/*     */     }
/* 782 */     if (this.defaultExpression != null || paramColumn.defaultExpression != null) {
/* 783 */       return false;
/*     */     }
/* 785 */     if (this.isGeneratedAlways || paramColumn.isGeneratedAlways) {
/* 786 */       return false;
/*     */     }
/* 788 */     if (this.onUpdateExpression != null || paramColumn.onUpdateExpression != null) {
/* 789 */       return false;
/*     */     }
/* 791 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void copy(Column paramColumn) {
/* 800 */     this.name = paramColumn.name;
/* 801 */     this.type = paramColumn.type;
/* 802 */     this.domain = paramColumn.domain;
/*     */ 
/*     */     
/* 805 */     this.nullable = paramColumn.nullable;
/* 806 */     this.defaultExpression = paramColumn.defaultExpression;
/* 807 */     this.onUpdateExpression = paramColumn.onUpdateExpression;
/*     */     
/* 809 */     this.defaultOnNull = paramColumn.defaultOnNull;
/* 810 */     this.sequence = paramColumn.sequence;
/* 811 */     this.comment = paramColumn.comment;
/* 812 */     this.generatedTableFilter = paramColumn.generatedTableFilter;
/* 813 */     this.isGeneratedAlways = paramColumn.isGeneratedAlways;
/* 814 */     this.selectivity = paramColumn.selectivity;
/* 815 */     this.primaryKey = paramColumn.primaryKey;
/* 816 */     this.visible = paramColumn.visible;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\Column.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */