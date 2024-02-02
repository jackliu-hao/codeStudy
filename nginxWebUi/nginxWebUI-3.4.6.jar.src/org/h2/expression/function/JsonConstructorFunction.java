/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionWithFlags;
/*     */ import org.h2.expression.Format;
/*     */ import org.h2.expression.OperationN;
/*     */ import org.h2.expression.Subquery;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.json.JsonConstructorUtils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueJson;
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
/*     */ public final class JsonConstructorFunction
/*     */   extends OperationN
/*     */   implements ExpressionWithFlags, NamedExpression
/*     */ {
/*     */   private final boolean array;
/*     */   private int flags;
/*     */   
/*     */   public JsonConstructorFunction(boolean paramBoolean) {
/*  41 */     super(new Expression[4]);
/*  42 */     this.array = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFlags(int paramInt) {
/*  47 */     this.flags = paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getFlags() {
/*  52 */     return this.flags;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  57 */     return this.array ? jsonArray(paramSessionLocal, this.args) : jsonObject(paramSessionLocal, this.args);
/*     */   }
/*     */   
/*     */   private Value jsonObject(SessionLocal paramSessionLocal, Expression[] paramArrayOfExpression) {
/*  61 */     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/*  62 */     byteArrayOutputStream.write(123); byte b; int i;
/*  63 */     for (b = 0, i = paramArrayOfExpression.length; b < i; ) {
/*  64 */       ValueJson valueJson; String str = paramArrayOfExpression[b++].getValue(paramSessionLocal).getString();
/*  65 */       if (str == null) {
/*  66 */         throw DbException.getInvalidValueException("JSON_OBJECT key", "NULL");
/*     */       }
/*  68 */       Value value = paramArrayOfExpression[b++].getValue(paramSessionLocal);
/*  69 */       if (value == ValueNull.INSTANCE) {
/*  70 */         if ((this.flags & 0x1) != 0) {
/*     */           continue;
/*     */         }
/*  73 */         valueJson = ValueJson.NULL;
/*     */       } 
/*     */       
/*  76 */       JsonConstructorUtils.jsonObjectAppend(byteArrayOutputStream, str, (Value)valueJson);
/*     */     } 
/*  78 */     return JsonConstructorUtils.jsonObjectFinish(byteArrayOutputStream, this.flags);
/*     */   }
/*     */   
/*     */   private Value jsonArray(SessionLocal paramSessionLocal, Expression[] paramArrayOfExpression) {
/*  82 */     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/*  83 */     byteArrayOutputStream.write(91);
/*  84 */     int i = paramArrayOfExpression.length;
/*     */     
/*  86 */     if (i == 1) {
/*  87 */       Expression expression = paramArrayOfExpression[0];
/*  88 */       if (expression instanceof Subquery)
/*  89 */       { Subquery subquery = (Subquery)expression;
/*  90 */         for (Value value : subquery.getAllRows(paramSessionLocal)) {
/*  91 */           JsonConstructorUtils.jsonArrayAppend(byteArrayOutputStream, value, this.flags);
/*     */         } }
/*     */       else
/*  94 */       { if (expression instanceof Format)
/*  95 */         { Format format = (Format)expression;
/*  96 */           expression = format.getSubexpression(0);
/*  97 */           if (expression instanceof Subquery)
/*  98 */           { Subquery subquery = (Subquery)expression;
/*  99 */             for (Value value : subquery.getAllRows(paramSessionLocal)) {
/* 100 */               JsonConstructorUtils.jsonArrayAppend(byteArrayOutputStream, format.getValue(value), this.flags);
/*     */             }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 110 */             byteArrayOutputStream.write(93);
/* 111 */             return (Value)ValueJson.getInternal(byteArrayOutputStream.toByteArray()); }  }  boolean bool1 = false; }  byteArrayOutputStream.write(93); return (Value)ValueJson.getInternal(byteArrayOutputStream.toByteArray());
/*     */     } 
/*     */     boolean bool = false;
/*     */   }
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 116 */     boolean bool = optimizeArguments(paramSessionLocal, true);
/* 117 */     this.type = TypeInfo.TYPE_JSON;
/* 118 */     if (bool) {
/* 119 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*     */     }
/* 121 */     return (Expression)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 126 */     paramStringBuilder.append(getName()).append('(');
/* 127 */     if (this.array) {
/* 128 */       writeExpressions(paramStringBuilder, this.args, paramInt);
/*     */     } else {
/* 130 */       byte b; int i; for (b = 0, i = this.args.length; b < i; ) {
/* 131 */         if (b > 0) {
/* 132 */           paramStringBuilder.append(", ");
/*     */         }
/* 134 */         this.args[b++].getUnenclosedSQL(paramStringBuilder, paramInt).append(": ");
/* 135 */         this.args[b++].getUnenclosedSQL(paramStringBuilder, paramInt);
/*     */       } 
/*     */     } 
/* 138 */     return getJsonFunctionFlagsSQL(paramStringBuilder, this.flags, this.array).append(')');
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
/*     */   public static StringBuilder getJsonFunctionFlagsSQL(StringBuilder paramStringBuilder, int paramInt, boolean paramBoolean) {
/* 153 */     if ((paramInt & 0x1) != 0) {
/* 154 */       if (!paramBoolean) {
/* 155 */         paramStringBuilder.append(" ABSENT ON NULL");
/*     */       }
/* 157 */     } else if (paramBoolean) {
/* 158 */       paramStringBuilder.append(" NULL ON NULL");
/*     */     } 
/* 160 */     if (!paramBoolean && (paramInt & 0x2) != 0) {
/* 161 */       paramStringBuilder.append(" WITH UNIQUE KEYS");
/*     */     }
/* 163 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 168 */     return this.array ? "JSON_ARRAY" : "JSON_OBJECT";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\JsonConstructorFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */