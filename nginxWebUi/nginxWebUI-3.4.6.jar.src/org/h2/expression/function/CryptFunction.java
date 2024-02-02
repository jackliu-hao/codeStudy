/*    */ package org.h2.expression.function;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.TypedValueExpression;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.security.BlockCipher;
/*    */ import org.h2.security.CipherFactory;
/*    */ import org.h2.util.MathUtils;
/*    */ import org.h2.util.Utils;
/*    */ import org.h2.value.DataType;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueVarbinary;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class CryptFunction
/*    */   extends FunctionN
/*    */ {
/*    */   public static final int ENCRYPT = 0;
/*    */   public static final int DECRYPT = 1;
/* 36 */   private static final String[] NAMES = new String[] { "ENCRYPT", "DECRYPT" };
/*    */ 
/*    */   
/*    */   private final int function;
/*    */ 
/*    */   
/*    */   public CryptFunction(Expression paramExpression1, Expression paramExpression2, Expression paramExpression3, int paramInt) {
/* 43 */     super(new Expression[] { paramExpression1, paramExpression2, paramExpression3 });
/* 44 */     this.function = paramInt;
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, Value paramValue3) {
/* 49 */     BlockCipher blockCipher = CipherFactory.getBlockCipher(paramValue1.getString());
/* 50 */     blockCipher.setKey(getPaddedArrayCopy(paramValue2.getBytesNoCopy(), blockCipher.getKeyLength()));
/* 51 */     byte[] arrayOfByte = getPaddedArrayCopy(paramValue3.getBytesNoCopy(), 16);
/* 52 */     switch (this.function) {
/*    */       case 0:
/* 54 */         blockCipher.encrypt(arrayOfByte, 0, arrayOfByte.length);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */         
/* 62 */         return (Value)ValueVarbinary.getNoCopy(arrayOfByte);case 1: blockCipher.decrypt(arrayOfByte, 0, arrayOfByte.length); return (Value)ValueVarbinary.getNoCopy(arrayOfByte);
/*    */     } 
/*    */     throw DbException.getInternalError("function=" + this.function);
/*    */   } private static byte[] getPaddedArrayCopy(byte[] paramArrayOfbyte, int paramInt) {
/* 66 */     return Utils.copyBytes(paramArrayOfbyte, MathUtils.roundUpInt(paramArrayOfbyte.length, paramInt));
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 71 */     boolean bool = optimizeArguments(paramSessionLocal, true);
/* 72 */     TypeInfo typeInfo = this.args[2].getType();
/* 73 */     this
/* 74 */       .type = DataType.isBinaryStringType(typeInfo.getValueType()) ? TypeInfo.getTypeInfo(6, typeInfo.getPrecision(), 0, null) : TypeInfo.TYPE_VARBINARY;
/*    */     
/* 76 */     if (bool) {
/* 77 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*    */     }
/* 79 */     return (Expression)this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 84 */     return NAMES[this.function];
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\CryptFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */