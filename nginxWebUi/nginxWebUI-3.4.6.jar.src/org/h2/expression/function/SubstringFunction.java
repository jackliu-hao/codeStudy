/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueVarbinary;
/*     */ import org.h2.value.ValueVarchar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SubstringFunction
/*     */   extends FunctionN
/*     */ {
/*     */   public SubstringFunction() {
/*  28 */     super(new Expression[3]);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, Value paramValue3) {
/*  33 */     if (this.type.getValueType() == 6) {
/*  34 */       byte[] arrayOfByte = paramValue1.getBytesNoCopy();
/*  35 */       int m = arrayOfByte.length;
/*  36 */       int n = paramValue2.getInt();
/*     */       
/*  38 */       if (n == 0) {
/*  39 */         n = 1;
/*  40 */       } else if (n < 0) {
/*  41 */         n = m + n + 1;
/*     */       } 
/*  43 */       int i1 = (paramValue3 == null) ? Math.max(m + 1, n) : (n + paramValue3.getInt());
/*     */ 
/*     */       
/*  46 */       n = Math.max(n, 1);
/*  47 */       i1 = Math.min(i1, m + 1);
/*  48 */       if (n > m || i1 <= n) {
/*  49 */         return (Value)ValueVarbinary.EMPTY;
/*     */       }
/*  51 */       n--;
/*  52 */       i1--;
/*  53 */       if (n == 0 && i1 == arrayOfByte.length) {
/*  54 */         return paramValue1.convertTo(TypeInfo.TYPE_VARBINARY);
/*     */       }
/*  56 */       return (Value)ValueVarbinary.getNoCopy(Arrays.copyOfRange(arrayOfByte, n, i1));
/*     */     } 
/*  58 */     String str = paramValue1.getString();
/*  59 */     int i = str.length();
/*  60 */     int j = paramValue2.getInt();
/*     */     
/*  62 */     if (j == 0) {
/*  63 */       j = 1;
/*  64 */     } else if (j < 0) {
/*  65 */       j = i + j + 1;
/*     */     } 
/*  67 */     int k = (paramValue3 == null) ? Math.max(i + 1, j) : (j + paramValue3.getInt());
/*     */ 
/*     */     
/*  70 */     j = Math.max(j, 1);
/*  71 */     k = Math.min(k, i + 1);
/*  72 */     if (j > i || k <= j) {
/*  73 */       return (paramSessionLocal.getMode()).treatEmptyStringsAsNull ? (Value)ValueNull.INSTANCE : (Value)ValueVarchar.EMPTY;
/*     */     }
/*  75 */     return ValueVarchar.get(str.substring(j - 1, k - 1), null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  81 */     boolean bool = optimizeArguments(paramSessionLocal, true);
/*  82 */     int i = this.args.length;
/*  83 */     if (i < 2 || i > 3) {
/*  84 */       throw DbException.get(7001, new String[] { getName(), "2..3" });
/*     */     }
/*  86 */     TypeInfo typeInfo = this.args[0].getType();
/*  87 */     long l = typeInfo.getPrecision();
/*  88 */     Expression expression = this.args[1];
/*     */     Value value;
/*  90 */     if (expression.isConstant() && (value = expression.getValue(paramSessionLocal)) != ValueNull.INSTANCE)
/*     */     {
/*     */       
/*  93 */       l -= value.getLong() - 1L;
/*     */     }
/*  95 */     if (this.args.length == 3) {
/*  96 */       expression = this.args[2];
/*  97 */       if (expression.isConstant() && (value = expression.getValue(paramSessionLocal)) != ValueNull.INSTANCE)
/*     */       {
/*  99 */         l = Math.min(l, value.getLong());
/*     */       }
/*     */     } 
/* 102 */     l = Math.max(0L, l);
/* 103 */     this.type = TypeInfo.getTypeInfo(
/* 104 */         DataType.isBinaryStringType(typeInfo.getValueType()) ? 6 : 2, l, 0, null);
/* 105 */     if (bool) {
/* 106 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*     */     }
/* 108 */     return (Expression)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 113 */     this.args[0].getUnenclosedSQL(paramStringBuilder.append(getName()).append('('), paramInt);
/* 114 */     this.args[1].getUnenclosedSQL(paramStringBuilder.append(" FROM "), paramInt);
/* 115 */     if (this.args.length > 2) {
/* 116 */       this.args[2].getUnenclosedSQL(paramStringBuilder.append(" FOR "), paramInt);
/*     */     }
/* 118 */     return paramStringBuilder.append(')');
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 123 */     return "SUBSTRING";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\SubstringFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */