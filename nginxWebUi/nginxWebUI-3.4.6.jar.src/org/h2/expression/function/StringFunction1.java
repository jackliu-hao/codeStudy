/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Arrays;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Mode;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueInteger;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class StringFunction1
/*     */   extends Function1
/*     */ {
/*     */   public static final int UPPER = 0;
/*     */   public static final int LOWER = 1;
/*     */   public static final int ASCII = 2;
/*     */   public static final int CHAR = 3;
/*     */   public static final int STRINGENCODE = 4;
/*     */   public static final int STRINGDECODE = 5;
/*     */   public static final int STRINGTOUTF8 = 6;
/*     */   public static final int UTF8TOSTRING = 7;
/*     */   public static final int HEXTORAW = 8;
/*     */   public static final int RAWTOHEX = 9;
/*     */   public static final int SPACE = 10;
/*     */   public static final int QUOTE_IDENT = 11;
/*  96 */   private static final String[] NAMES = new String[] { "UPPER", "LOWER", "ASCII", "CHAR", "STRINGENCODE", "STRINGDECODE", "STRINGTOUTF8", "UTF8TOSTRING", "HEXTORAW", "RAWTOHEX", "SPACE", "QUOTE_IDENT" };
/*     */ 
/*     */   
/*     */   private final int function;
/*     */ 
/*     */ 
/*     */   
/*     */   public StringFunction1(Expression paramExpression, int paramInt) {
/* 104 */     super(paramExpression);
/* 105 */     this.function = paramInt; } public Value getValue(SessionLocal paramSessionLocal) {
/*     */     ValueVarbinary valueVarbinary;
/*     */     Value value1;
/*     */     String str;
/*     */     byte[] arrayOfByte;
/* 110 */     Value value2 = this.arg.getValue(paramSessionLocal);
/* 111 */     if (value2 == ValueNull.INSTANCE) {
/* 112 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/* 114 */     switch (this.function) {
/*     */ 
/*     */       
/*     */       case 0:
/* 118 */         value2 = ValueVarchar.get(value2.getString().toUpperCase(), (CastDataProvider)paramSessionLocal);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 163 */         return value2;case 1: value2 = ValueVarchar.get(value2.getString().toLowerCase(), (CastDataProvider)paramSessionLocal); return value2;case 2: str = value2.getString(); value2 = str.isEmpty() ? (Value)ValueNull.INSTANCE : (Value)ValueInteger.get(str.charAt(0)); return value2;case 3: value2 = ValueVarchar.get(String.valueOf((char)value2.getInt()), (CastDataProvider)paramSessionLocal); return value2;case 4: value2 = ValueVarchar.get(StringUtils.javaEncode(value2.getString()), (CastDataProvider)paramSessionLocal); return value2;case 5: value2 = ValueVarchar.get(StringUtils.javaDecode(value2.getString()), (CastDataProvider)paramSessionLocal); return value2;case 6: valueVarbinary = ValueVarbinary.getNoCopy(value2.getString().getBytes(StandardCharsets.UTF_8)); return (Value)valueVarbinary;case 7: value1 = ValueVarchar.get(new String(valueVarbinary.getBytesNoCopy(), StandardCharsets.UTF_8), (CastDataProvider)paramSessionLocal); return value1;case 8: value1 = hexToRaw(value1.getString(), paramSessionLocal); return value1;case 9: value1 = ValueVarchar.get(rawToHex(value1, paramSessionLocal.getMode()), (CastDataProvider)paramSessionLocal); return value1;case 10: arrayOfByte = new byte[Math.max(0, value1.getInt())]; Arrays.fill(arrayOfByte, (byte)32); value1 = ValueVarchar.get(new String(arrayOfByte, StandardCharsets.ISO_8859_1), (CastDataProvider)paramSessionLocal); return value1;case 11: value1 = ValueVarchar.get(StringUtils.quoteIdentifier(value1.getString()), (CastDataProvider)paramSessionLocal); return value1;
/*     */     } 
/*     */     throw DbException.getInternalError("function=" + this.function);
/*     */   } private static Value hexToRaw(String paramString, SessionLocal paramSessionLocal) {
/* 167 */     if (paramSessionLocal.getMode().getEnum() == Mode.ModeEnum.Oracle) {
/* 168 */       return (Value)ValueVarbinary.get(StringUtils.convertHexToBytes(paramString));
/*     */     }
/* 170 */     int i = paramString.length();
/* 171 */     if (i % 4 != 0) {
/* 172 */       throw DbException.get(22018, paramString);
/*     */     }
/* 174 */     StringBuilder stringBuilder = new StringBuilder(i / 4);
/* 175 */     for (byte b = 0; b < i; b += 4) {
/*     */       try {
/* 177 */         stringBuilder.append((char)Integer.parseInt(paramString.substring(b, b + 4), 16));
/* 178 */       } catch (NumberFormatException numberFormatException) {
/* 179 */         throw DbException.get(22018, paramString);
/*     */       } 
/*     */     } 
/* 182 */     return ValueVarchar.get(stringBuilder.toString(), (CastDataProvider)paramSessionLocal);
/*     */   }
/*     */   
/*     */   private static String rawToHex(Value paramValue, Mode paramMode) {
/* 186 */     if (DataType.isBinaryStringOrSpecialBinaryType(paramValue.getValueType())) {
/* 187 */       return StringUtils.convertBytesToHex(paramValue.getBytesNoCopy());
/*     */     }
/* 189 */     String str = paramValue.getString();
/* 190 */     if (paramMode.getEnum() == Mode.ModeEnum.Oracle) {
/* 191 */       return StringUtils.convertBytesToHex(str.getBytes(StandardCharsets.UTF_8));
/*     */     }
/* 193 */     int i = str.length();
/* 194 */     StringBuilder stringBuilder = new StringBuilder(4 * i);
/* 195 */     for (byte b = 0; b < i; b++) {
/* 196 */       String str1 = Integer.toHexString(str.charAt(b) & Character.MAX_VALUE);
/* 197 */       for (int j = str1.length(); j < 4; j++) {
/* 198 */         stringBuilder.append('0');
/*     */       }
/* 200 */       stringBuilder.append(str1);
/*     */     } 
/* 202 */     return stringBuilder.toString();
/*     */   } public Expression optimize(SessionLocal paramSessionLocal) {
/*     */     TypeInfo typeInfo;
/*     */     long l;
/*     */     byte b;
/* 207 */     this.arg = this.arg.optimize(paramSessionLocal);
/* 208 */     switch (this.function) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case 0:
/*     */       case 1:
/*     */       case 4:
/*     */       case 10:
/*     */       case 11:
/* 218 */         this.type = TypeInfo.TYPE_VARCHAR;
/*     */         break;
/*     */       case 2:
/* 221 */         this.type = TypeInfo.TYPE_INTEGER;
/*     */         break;
/*     */       case 3:
/* 224 */         this.type = TypeInfo.getTypeInfo(2, 1L, 0, null);
/*     */         break;
/*     */       case 5:
/* 227 */         typeInfo = this.arg.getType();
/* 228 */         this
/* 229 */           .type = DataType.isCharacterStringType(typeInfo.getValueType()) ? TypeInfo.getTypeInfo(2, typeInfo.getPrecision(), 0, null) : TypeInfo.TYPE_VARCHAR;
/*     */         break;
/*     */ 
/*     */       
/*     */       case 6:
/* 234 */         this.type = TypeInfo.TYPE_VARBINARY;
/*     */         break;
/*     */       case 7:
/* 237 */         typeInfo = this.arg.getType();
/* 238 */         this
/* 239 */           .type = DataType.isBinaryStringType(typeInfo.getValueType()) ? TypeInfo.getTypeInfo(2, typeInfo.getPrecision(), 0, null) : TypeInfo.TYPE_VARCHAR;
/*     */         break;
/*     */ 
/*     */       
/*     */       case 8:
/* 244 */         typeInfo = this.arg.getType();
/* 245 */         if (paramSessionLocal.getMode().getEnum() == Mode.ModeEnum.Oracle) {
/* 246 */           if (DataType.isCharacterStringType(typeInfo.getValueType())) {
/* 247 */             this.type = TypeInfo.getTypeInfo(6, typeInfo.getPrecision() / 2L, 0, null); break;
/*     */           } 
/* 249 */           this.type = TypeInfo.TYPE_VARBINARY;
/*     */           break;
/*     */         } 
/* 252 */         if (DataType.isCharacterStringType(typeInfo.getValueType())) {
/* 253 */           this.type = TypeInfo.getTypeInfo(2, typeInfo.getPrecision() / 4L, 0, null); break;
/*     */         } 
/* 255 */         this.type = TypeInfo.TYPE_VARCHAR;
/*     */         break;
/*     */ 
/*     */ 
/*     */       
/*     */       case 9:
/* 261 */         typeInfo = this.arg.getType();
/* 262 */         l = typeInfo.getPrecision();
/*     */         
/* 264 */         b = DataType.isBinaryStringOrSpecialBinaryType(typeInfo.getValueType()) ? 2 : ((paramSessionLocal.getMode().getEnum() == Mode.ModeEnum.Oracle) ? 6 : 4);
/* 265 */         this.type = TypeInfo.getTypeInfo(2, (l <= Long.MAX_VALUE / b) ? (l * b) : Long.MAX_VALUE, 0, null);
/*     */         break;
/*     */ 
/*     */       
/*     */       default:
/* 270 */         throw DbException.getInternalError("function=" + this.function);
/*     */     } 
/* 272 */     if (this.arg.isConstant()) {
/* 273 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*     */     }
/* 275 */     return (Expression)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 280 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\StringFunction1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */