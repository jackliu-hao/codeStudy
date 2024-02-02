/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Mode;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueInteger;
/*     */ import org.h2.value.ValueNull;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class StringFunction
/*     */   extends FunctionN
/*     */ {
/*     */   public static final int LOCATE = 0;
/*     */   public static final int INSERT = 1;
/*     */   public static final int REPLACE = 2;
/*     */   public static final int LPAD = 3;
/*     */   public static final int RPAD = 4;
/*     */   public static final int TRANSLATE = 5;
/*  55 */   private static final String[] NAMES = new String[] { "LOCATE", "INSERT", "REPLACE", "LPAD", "RPAD", "TRANSLATE" };
/*     */ 
/*     */   
/*     */   private final int function;
/*     */ 
/*     */   
/*     */   public StringFunction(Expression paramExpression1, Expression paramExpression2, Expression paramExpression3, int paramInt) {
/*  62 */     super((paramExpression3 == null) ? new Expression[2] : new Expression[3]);
/*  63 */     this.function = paramInt;
/*     */   }
/*     */   
/*     */   public StringFunction(Expression paramExpression1, Expression paramExpression2, Expression paramExpression3, Expression paramExpression4, int paramInt) {
/*  67 */     super(new Expression[] { paramExpression1, paramExpression2, paramExpression3, paramExpression4 });
/*  68 */     this.function = paramInt;
/*     */   }
/*     */   
/*     */   public StringFunction(Expression[] paramArrayOfExpression, int paramInt) {
/*  72 */     super(paramArrayOfExpression);
/*  73 */     this.function = paramInt; } public Value getValue(SessionLocal paramSessionLocal) { ValueInteger valueInteger;
/*     */     Value value1, value4;
/*     */     String str1;
/*     */     Value value5;
/*     */     String str2, str3;
/*  78 */     Value value2 = this.args[0].getValue(paramSessionLocal), value3 = this.args[1].getValue(paramSessionLocal);
/*  79 */     switch (this.function) {
/*     */       case 0:
/*  81 */         if (value2 == ValueNull.INSTANCE || value3 == ValueNull.INSTANCE) {
/*  82 */           return (Value)ValueNull.INSTANCE;
/*     */         }
/*  84 */         value4 = (this.args.length >= 3) ? this.args[2].getValue(paramSessionLocal) : null;
/*  85 */         if (value4 == ValueNull.INSTANCE) {
/*  86 */           return (Value)ValueNull.INSTANCE;
/*     */         }
/*  88 */         valueInteger = ValueInteger.get(locate(value2.getString(), value3.getString(), (value4 == null) ? 1 : value4.getInt()));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 157 */         return (Value)valueInteger;case 1: value4 = this.args[2].getValue(paramSessionLocal); value5 = this.args[3].getValue(paramSessionLocal); if (value3 != ValueNull.INSTANCE && value4 != ValueNull.INSTANCE) { String str = insert(valueInteger.getString(), value3.getInt(), value4.getInt(), value5.getString()); valueInteger = (str != null) ? (ValueInteger)ValueVarchar.get(str, (CastDataProvider)paramSessionLocal) : (ValueInteger)ValueNull.INSTANCE; }  return (Value)valueInteger;case 2: if (valueInteger == ValueNull.INSTANCE || value3 == ValueNull.INSTANCE) return (Value)ValueNull.INSTANCE;  if (this.args.length >= 3) { value5 = this.args[2].getValue(paramSessionLocal); if (value5 == ValueNull.INSTANCE && paramSessionLocal.getMode().getEnum() != Mode.ModeEnum.Oracle) return (Value)ValueNull.INSTANCE;  str1 = value5.getString(); if (str1 == null) str1 = "";  } else { str1 = ""; }  value1 = ValueVarchar.get(StringUtils.replaceAll(valueInteger.getString(), value3.getString(), str1), (CastDataProvider)paramSessionLocal); return value1;case 3: case 4: if (value1 == ValueNull.INSTANCE || value3 == ValueNull.INSTANCE) return (Value)ValueNull.INSTANCE;  if (this.args.length >= 3) { value5 = this.args[2].getValue(paramSessionLocal); if (value5 == ValueNull.INSTANCE) return (Value)ValueNull.INSTANCE;  str1 = value5.getString(); } else { str1 = null; }  value1 = ValueVarchar.get(StringUtils.pad(value1.getString(), value3.getInt(), str1, (this.function == 4)), (CastDataProvider)paramSessionLocal); return value1;case 5: if (value1 == ValueNull.INSTANCE || value3 == ValueNull.INSTANCE) return (Value)ValueNull.INSTANCE;  value5 = this.args[2].getValue(paramSessionLocal); if (value5 == ValueNull.INSTANCE) return (Value)ValueNull.INSTANCE;  str2 = value3.getString(); str3 = value5.getString(); if (paramSessionLocal.getMode().getEnum() == Mode.ModeEnum.DB2) { String str = str2; str2 = str3; str3 = str; }  value1 = ValueVarchar.get(translate(value1.getString(), str2, str3), (CastDataProvider)paramSessionLocal); return value1;
/*     */     } 
/*     */     throw DbException.getInternalError("function=" + this.function); }
/*     */    private static int locate(String paramString1, String paramString2, int paramInt) {
/* 161 */     if (paramInt < 0) {
/* 162 */       return paramString2.lastIndexOf(paramString1, paramString2.length() + paramInt) + 1;
/*     */     }
/* 164 */     return paramString2.indexOf(paramString1, (paramInt == 0) ? 0 : (paramInt - 1)) + 1;
/*     */   }
/*     */   
/*     */   private static String insert(String paramString1, int paramInt1, int paramInt2, String paramString2) {
/* 168 */     if (paramString1 == null) {
/* 169 */       return paramString2;
/*     */     }
/* 171 */     if (paramString2 == null) {
/* 172 */       return paramString1;
/*     */     }
/* 174 */     int i = paramString1.length();
/* 175 */     int j = paramString2.length();
/* 176 */     paramInt1--;
/* 177 */     if (paramInt1 < 0 || paramInt2 <= 0 || j == 0 || paramInt1 > i) {
/* 178 */       return paramString1;
/*     */     }
/* 180 */     if (paramInt1 + paramInt2 > i) {
/* 181 */       paramInt2 = i - paramInt1;
/*     */     }
/* 183 */     return paramString1.substring(0, paramInt1) + paramString2 + paramString1.substring(paramInt1 + paramInt2);
/*     */   }
/*     */   
/*     */   private static String translate(String paramString1, String paramString2, String paramString3) {
/* 187 */     if (StringUtils.isNullOrEmpty(paramString1) || StringUtils.isNullOrEmpty(paramString2)) {
/* 188 */       return paramString1;
/*     */     }
/*     */     
/* 191 */     StringBuilder stringBuilder = null;
/*     */ 
/*     */     
/* 194 */     byte b1 = (paramString3 == null) ? 0 : paramString3.length(); byte b2; int i;
/* 195 */     for (b2 = 0, i = paramString1.length(); b2 < i; b2++) {
/* 196 */       char c = paramString1.charAt(b2);
/* 197 */       int j = paramString2.indexOf(c);
/* 198 */       if (j >= 0) {
/* 199 */         if (stringBuilder == null) {
/* 200 */           stringBuilder = new StringBuilder(i);
/* 201 */           if (b2 > 0) {
/* 202 */             stringBuilder.append(paramString1, 0, b2);
/*     */           }
/*     */         } 
/* 205 */         if (j < b1) {
/* 206 */           c = paramString3.charAt(j);
/*     */         }
/*     */       } 
/* 209 */       if (stringBuilder != null) {
/* 210 */         stringBuilder.append(c);
/*     */       }
/*     */     } 
/* 213 */     return (stringBuilder == null) ? paramString1 : stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 218 */     boolean bool = optimizeArguments(paramSessionLocal, true);
/* 219 */     switch (this.function) {
/*     */       case 0:
/* 221 */         this.type = TypeInfo.TYPE_INTEGER;
/*     */         break;
/*     */       case 1:
/*     */       case 2:
/*     */       case 3:
/*     */       case 4:
/*     */       case 5:
/* 228 */         this.type = TypeInfo.TYPE_VARCHAR;
/*     */         break;
/*     */       default:
/* 231 */         throw DbException.getInternalError("function=" + this.function);
/*     */     } 
/* 233 */     if (bool) {
/* 234 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*     */     }
/* 236 */     return (Expression)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 241 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\StringFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */