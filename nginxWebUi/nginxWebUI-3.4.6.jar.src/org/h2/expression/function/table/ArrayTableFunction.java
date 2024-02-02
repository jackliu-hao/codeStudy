/*     */ package org.h2.expression.function.table;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.LocalResult;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueArray;
/*     */ import org.h2.value.ValueCollectionBase;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ArrayTableFunction
/*     */   extends TableFunction
/*     */ {
/*     */   public static final int UNNEST = 0;
/*     */   public static final int TABLE = 1;
/*     */   public static final int TABLE_DISTINCT = 2;
/*     */   private Column[] columns;
/*  46 */   private static final String[] NAMES = new String[] { "UNNEST", "TABLE", "TABLE_DISTINCT" };
/*     */ 
/*     */   
/*     */   private final int function;
/*     */ 
/*     */   
/*     */   public ArrayTableFunction(int paramInt) {
/*  53 */     super(new Expression[1]);
/*  54 */     this.function = paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getValue(SessionLocal paramSessionLocal) {
/*  59 */     return getTable(paramSessionLocal, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public void optimize(SessionLocal paramSessionLocal) {
/*  64 */     super.optimize(paramSessionLocal);
/*  65 */     if (this.args.length < 1) {
/*  66 */       throw DbException.get(7001, new String[] { getName(), ">0" });
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  72 */     if (this.function == 0) {
/*  73 */       super.getSQL(paramStringBuilder, paramInt);
/*  74 */       if (this.args.length < this.columns.length) {
/*  75 */         paramStringBuilder.append(" WITH ORDINALITY");
/*     */       }
/*     */     } else {
/*  78 */       paramStringBuilder.append(getName()).append('(');
/*  79 */       for (byte b = 0; b < this.args.length; b++) {
/*  80 */         if (b > 0) {
/*  81 */           paramStringBuilder.append(", ");
/*     */         }
/*  83 */         paramStringBuilder.append(this.columns[b].getCreateSQL()).append('=');
/*  84 */         this.args[b].getUnenclosedSQL(paramStringBuilder, paramInt);
/*     */       } 
/*  86 */       paramStringBuilder.append(')');
/*     */     } 
/*  88 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getValueTemplate(SessionLocal paramSessionLocal) {
/*  93 */     return getTable(paramSessionLocal, true);
/*     */   }
/*     */   
/*     */   public void setColumns(ArrayList<Column> paramArrayList) {
/*  97 */     this.columns = paramArrayList.<Column>toArray(new Column[0]);
/*     */   }
/*     */   
/*     */   private ResultInterface getTable(SessionLocal paramSessionLocal, boolean paramBoolean) {
/* 101 */     int i = this.columns.length;
/* 102 */     Expression[] arrayOfExpression = new Expression[i];
/* 103 */     Database database = paramSessionLocal.getDatabase();
/* 104 */     for (byte b = 0; b < i; b++) {
/* 105 */       Column column = this.columns[b];
/* 106 */       ExpressionColumn expressionColumn = new ExpressionColumn(database, column);
/* 107 */       arrayOfExpression[b] = (Expression)expressionColumn;
/*     */     } 
/* 109 */     LocalResult localResult = new LocalResult(paramSessionLocal, arrayOfExpression, i, i);
/* 110 */     if (!paramBoolean && this.function == 2) {
/* 111 */       localResult.setDistinct();
/*     */     }
/* 113 */     if (!paramBoolean) {
/* 114 */       int j = i;
/* 115 */       boolean bool1 = (this.function == 0) ? true : false, bool2 = false;
/* 116 */       if (bool1) {
/* 117 */         j = this.args.length;
/* 118 */         if (j < i) {
/* 119 */           bool2 = true;
/*     */         }
/*     */       } 
/* 122 */       Value[][] arrayOfValue = new Value[j][];
/* 123 */       int k = 0; byte b1;
/* 124 */       for (b1 = 0; b1 < j; b1++) {
/* 125 */         Value value = this.args[b1].getValue(paramSessionLocal);
/* 126 */         if (value == ValueNull.INSTANCE) {
/* 127 */           arrayOfValue[b1] = Value.EMPTY_VALUES;
/*     */         } else {
/* 129 */           ValueArray valueArray; int m = value.getValueType();
/* 130 */           if (m != 40 && m != 41) {
/* 131 */             valueArray = value.convertToAnyArray((CastDataProvider)paramSessionLocal);
/*     */           }
/* 133 */           Value[] arrayOfValue1 = ((ValueCollectionBase)valueArray).getList();
/* 134 */           arrayOfValue[b1] = arrayOfValue1;
/* 135 */           k = Math.max(k, arrayOfValue1.length);
/*     */         } 
/*     */       } 
/* 138 */       for (b1 = 0; b1 < k; b1++) {
/* 139 */         Value[] arrayOfValue1 = new Value[i];
/* 140 */         for (byte b2 = 0; b2 < j; b2++) {
/* 141 */           Value value, arrayOfValue2[] = arrayOfValue[b2];
/*     */           
/* 143 */           if (arrayOfValue2.length <= b1) {
/* 144 */             ValueNull valueNull = ValueNull.INSTANCE;
/*     */           } else {
/* 146 */             Column column = this.columns[b2];
/* 147 */             value = arrayOfValue2[b1];
/* 148 */             if (!bool1) {
/* 149 */               value = value.convertForAssignTo(column.getType(), (CastDataProvider)paramSessionLocal, column);
/*     */             }
/*     */           } 
/* 152 */           arrayOfValue1[b2] = value;
/*     */         } 
/* 154 */         if (bool2) {
/* 155 */           arrayOfValue1[j] = (Value)ValueInteger.get(b1 + 1);
/*     */         }
/* 157 */         localResult.addRow(arrayOfValue1);
/*     */       } 
/*     */     } 
/* 160 */     localResult.done();
/* 161 */     return (ResultInterface)localResult;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 166 */     return NAMES[this.function];
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDeterministic() {
/* 171 */     return true;
/*     */   }
/*     */   
/*     */   public int getFunctionType() {
/* 175 */     return this.function;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\table\ArrayTableFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */