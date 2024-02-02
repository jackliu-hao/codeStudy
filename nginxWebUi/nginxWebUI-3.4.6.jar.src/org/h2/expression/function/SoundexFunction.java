/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueInteger;
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
/*     */ public final class SoundexFunction
/*     */   extends Function1_2
/*     */ {
/*     */   public static final int SOUNDEX = 0;
/*     */   public static final int DIFFERENCE = 1;
/*  34 */   private static final String[] NAMES = new String[] { "SOUNDEX", "DIFFERENCE" };
/*     */ 
/*     */ 
/*     */   
/*  38 */   private static final byte[] SOUNDEX_INDEX = "71237128722455712623718272\000\000\000\000\000\00071237128722455712623718272"
/*     */     
/*  40 */     .getBytes(StandardCharsets.ISO_8859_1);
/*     */   
/*     */   private final int function;
/*     */   
/*     */   public SoundexFunction(Expression paramExpression1, Expression paramExpression2, int paramInt) {
/*  45 */     super(paramExpression1, paramExpression2);
/*  46 */     this.function = paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2) {
/*  51 */     switch (this.function) {
/*     */       case 0:
/*  53 */         paramValue1 = ValueVarchar.get(new String(getSoundex(paramValue1.getString()), StandardCharsets.ISO_8859_1), (CastDataProvider)paramSessionLocal);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  62 */         return paramValue1;
/*     */       case 1:
/*     */         return (Value)ValueInteger.get(getDifference(paramValue1.getString(), paramValue2.getString()));
/*     */     } 
/*     */     throw DbException.getInternalError("function=" + this.function); } private static int getDifference(String paramString1, String paramString2) {
/*  67 */     byte[] arrayOfByte1 = getSoundex(paramString1), arrayOfByte2 = getSoundex(paramString2);
/*  68 */     byte b1 = 0;
/*  69 */     for (byte b2 = 0; b2 < 4; b2++) {
/*  70 */       if (arrayOfByte1[b2] == arrayOfByte2[b2]) {
/*  71 */         b1++;
/*     */       }
/*     */     } 
/*  74 */     return b1;
/*     */   }
/*     */   
/*     */   private static byte[] getSoundex(String paramString) {
/*  78 */     byte[] arrayOfByte = { 48, 48, 48, 48 };
/*  79 */     byte b = 48; byte b1, b2; int i;
/*  80 */     for (b1 = 0, b2 = 0, i = paramString.length(); b1 < i && b2 < 4; b1++) {
/*  81 */       char c = paramString.charAt(b1);
/*  82 */       if (c >= 'A' && c <= 'z') {
/*  83 */         byte b3 = SOUNDEX_INDEX[c - 65];
/*  84 */         if (b3 != 0) {
/*  85 */           if (b2 == 0) {
/*  86 */             arrayOfByte[b2++] = (byte)c;
/*  87 */             b = b3;
/*  88 */           } else if (b3 <= 54) {
/*  89 */             if (b3 != b) {
/*  90 */               arrayOfByte[b2++] = b = b3;
/*     */             }
/*  92 */           } else if (b3 == 55) {
/*  93 */             b = b3;
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/*  98 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 103 */     this.left = this.left.optimize(paramSessionLocal);
/* 104 */     if (this.right != null) {
/* 105 */       this.right = this.right.optimize(paramSessionLocal);
/*     */     }
/* 107 */     switch (this.function) {
/*     */       case 0:
/* 109 */         this.type = TypeInfo.getTypeInfo(2, 4L, 0, null);
/*     */         break;
/*     */       case 1:
/* 112 */         this.type = TypeInfo.TYPE_INTEGER;
/*     */         break;
/*     */       default:
/* 115 */         throw DbException.getInternalError("function=" + this.function);
/*     */     } 
/* 117 */     if (this.left.isConstant() && (this.right == null || this.right.isConstant())) {
/* 118 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*     */     }
/* 120 */     return (Expression)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 125 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\SoundexFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */