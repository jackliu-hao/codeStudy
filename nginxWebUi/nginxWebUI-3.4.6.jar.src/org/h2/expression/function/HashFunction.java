/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.MessageDigest;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.security.SHA3;
/*     */ import org.h2.util.Bits;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBigint;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueVarbinary;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class HashFunction
/*     */   extends FunctionN
/*     */ {
/*     */   public static final int HASH = 0;
/*     */   public static final int ORA_HASH = 1;
/*  40 */   private static final String[] NAMES = new String[] { "HASH", "ORA_HASH" };
/*     */ 
/*     */   
/*     */   private final int function;
/*     */ 
/*     */   
/*     */   public HashFunction(Expression paramExpression, int paramInt) {
/*  47 */     super(new Expression[] { paramExpression });
/*  48 */     this.function = paramInt;
/*     */   }
/*     */   
/*     */   public HashFunction(Expression paramExpression1, Expression paramExpression2, Expression paramExpression3, int paramInt) {
/*  52 */     super((paramExpression3 == null) ? new Expression[2] : new Expression[3]);
/*  53 */     this.function = paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, Value paramValue3) {
/*  58 */     switch (this.function) {
/*     */       case 0:
/*  60 */         paramValue1 = getHash(paramValue1.getString(), paramValue2, (paramValue3 == null) ? 1 : paramValue3.getInt());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  68 */         return paramValue1;case 1: paramValue1 = oraHash(paramValue1, (paramValue2 == null) ? 4294967295L : paramValue2.getLong(), (paramValue3 == null) ? 0L : paramValue3.getLong()); return paramValue1;
/*     */     } 
/*     */     throw DbException.getInternalError("function=" + this.function); } private static Value getHash(String paramString, Value paramValue, int paramInt) {
/*     */     MessageDigest messageDigest;
/*  72 */     if (paramInt <= 0) {
/*  73 */       throw DbException.getInvalidValueException("iterations", Integer.valueOf(paramInt));
/*     */     }
/*     */     
/*  76 */     switch (StringUtils.toUpperEnglish(paramString)) {
/*     */       case "MD5":
/*     */       case "SHA-1":
/*     */       case "SHA-224":
/*     */       case "SHA-256":
/*     */       case "SHA-384":
/*     */       case "SHA-512":
/*  83 */         messageDigest = hashImpl(paramValue, paramString);
/*     */         break;
/*     */       case "SHA256":
/*  86 */         messageDigest = hashImpl(paramValue, "SHA-256");
/*     */         break;
/*     */       case "SHA3-224":
/*  89 */         messageDigest = hashImpl(paramValue, (MessageDigest)SHA3.getSha3_224());
/*     */         break;
/*     */       case "SHA3-256":
/*  92 */         messageDigest = hashImpl(paramValue, (MessageDigest)SHA3.getSha3_256());
/*     */         break;
/*     */       case "SHA3-384":
/*  95 */         messageDigest = hashImpl(paramValue, (MessageDigest)SHA3.getSha3_384());
/*     */         break;
/*     */       case "SHA3-512":
/*  98 */         messageDigest = hashImpl(paramValue, (MessageDigest)SHA3.getSha3_512());
/*     */         break;
/*     */       default:
/* 101 */         throw DbException.getInvalidValueException("algorithm", paramString);
/*     */     } 
/* 103 */     byte[] arrayOfByte = messageDigest.digest();
/* 104 */     for (byte b = 1; b < paramInt; b++) {
/* 105 */       arrayOfByte = messageDigest.digest(arrayOfByte);
/*     */     }
/* 107 */     return (Value)ValueVarbinary.getNoCopy(arrayOfByte);
/*     */   }
/*     */   
/*     */   private static Value oraHash(Value paramValue, long paramLong1, long paramLong2) {
/* 111 */     if ((paramLong1 & 0xFFFFFFFF00000000L) != 0L) {
/* 112 */       throw DbException.getInvalidValueException("bucket", Long.valueOf(paramLong1));
/*     */     }
/* 114 */     if ((paramLong2 & 0xFFFFFFFF00000000L) != 0L) {
/* 115 */       throw DbException.getInvalidValueException("seed", Long.valueOf(paramLong2));
/*     */     }
/* 117 */     MessageDigest messageDigest = hashImpl(paramValue, "SHA-1");
/* 118 */     if (messageDigest == null) {
/* 119 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/* 121 */     if (paramLong2 != 0L) {
/* 122 */       byte[] arrayOfByte = new byte[4];
/* 123 */       Bits.writeInt(arrayOfByte, 0, (int)paramLong2);
/* 124 */       messageDigest.update(arrayOfByte);
/*     */     } 
/* 126 */     long l = Bits.readLong(messageDigest.digest(), 0);
/*     */ 
/*     */     
/* 129 */     return (Value)ValueBigint.get((l & Long.MAX_VALUE) % (paramLong1 + 1L));
/*     */   }
/*     */   
/*     */   private static MessageDigest hashImpl(Value paramValue, String paramString) {
/*     */     MessageDigest messageDigest;
/*     */     try {
/* 135 */       messageDigest = MessageDigest.getInstance(paramString);
/* 136 */     } catch (Exception exception) {
/* 137 */       throw DbException.convert(exception);
/*     */     } 
/* 139 */     return hashImpl(paramValue, messageDigest);
/*     */   }
/*     */   private static MessageDigest hashImpl(Value paramValue, MessageDigest paramMessageDigest) {
/*     */     try {
/*     */       byte[] arrayOfByte;
/* 144 */       switch (paramValue.getValueType())
/*     */       { case 1:
/*     */         case 2:
/*     */         case 4:
/* 148 */           paramMessageDigest.update(paramValue.getString().getBytes(StandardCharsets.UTF_8));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 163 */           return paramMessageDigest;case 3: case 7: arrayOfByte = new byte[4096]; try (InputStream null = paramValue.getInputStream()) { int i; while ((i = inputStream.read(arrayOfByte)) > 0) paramMessageDigest.update(arrayOfByte, 0, i);  }  return paramMessageDigest; }  paramMessageDigest.update(paramValue.getBytesNoCopy()); return paramMessageDigest;
/* 164 */     } catch (Exception exception) {
/* 165 */       throw DbException.convert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 171 */     boolean bool = optimizeArguments(paramSessionLocal, true);
/* 172 */     switch (this.function) {
/*     */       case 0:
/* 174 */         this.type = TypeInfo.TYPE_VARBINARY;
/*     */         break;
/*     */       case 1:
/* 177 */         this.type = TypeInfo.TYPE_BIGINT;
/*     */         break;
/*     */       default:
/* 180 */         throw DbException.getInternalError("function=" + this.function);
/*     */     } 
/* 182 */     if (bool) {
/* 183 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*     */     }
/* 185 */     return (Expression)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 190 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\HashFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */