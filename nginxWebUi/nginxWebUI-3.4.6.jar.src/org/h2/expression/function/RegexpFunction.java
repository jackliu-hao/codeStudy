/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.regex.PatternSyntaxException;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Mode;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBoolean;
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
/*     */ public final class RegexpFunction
/*     */   extends FunctionN
/*     */ {
/*     */   public static final int REGEXP_LIKE = 0;
/*     */   public static final int REGEXP_REPLACE = 1;
/*     */   public static final int REGEXP_SUBSTR = 2;
/*  45 */   private static final String[] NAMES = new String[] { "REGEXP_LIKE", "REGEXP_REPLACE", "REGEXP_SUBSTR" };
/*     */ 
/*     */   
/*     */   private final int function;
/*     */ 
/*     */   
/*     */   public RegexpFunction(int paramInt) {
/*  52 */     super(new Expression[(paramInt == 0) ? 3 : 6]);
/*  53 */     this.function = paramInt; } public Value getValue(SessionLocal paramSessionLocal) { ValueBoolean valueBoolean; Value value1, value5; String str1; Value value4; String str2;
/*     */     Value value6;
/*     */     String str3;
/*     */     Value value7;
/*     */     int j;
/*  58 */     Value value8, value2 = this.args[0].getValue(paramSessionLocal);
/*  59 */     Value value3 = this.args[1].getValue(paramSessionLocal);
/*  60 */     int i = this.args.length;
/*  61 */     switch (this.function) {
/*     */       case 0:
/*  63 */         value5 = (i >= 3) ? this.args[2].getValue(paramSessionLocal) : null;
/*  64 */         if (value2 == ValueNull.INSTANCE || value3 == ValueNull.INSTANCE || value5 == ValueNull.INSTANCE) {
/*  65 */           return (Value)ValueNull.INSTANCE;
/*     */         }
/*  67 */         str2 = value3.getString();
/*  68 */         str3 = (value5 != null) ? value5.getString() : null;
/*  69 */         j = makeRegexpFlags(str3, false);
/*     */         try {
/*  71 */           valueBoolean = ValueBoolean.get(Pattern.compile(str2, j).matcher(value2.getString()).find());
/*  72 */         } catch (PatternSyntaxException patternSyntaxException) {
/*  73 */           throw DbException.get(22025, patternSyntaxException, new String[] { str2 });
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 118 */         return (Value)valueBoolean;case 1: str1 = valueBoolean.getString(); if (paramSessionLocal.getMode().getEnum() == Mode.ModeEnum.Oracle) { str2 = this.args[2].getValue(paramSessionLocal).getString(); boolean bool = (i >= 4) ? this.args[3].getValue(paramSessionLocal).getInt() : true; j = (i >= 5) ? this.args[4].getValue(paramSessionLocal).getInt() : 0; String str = (i >= 6) ? this.args[5].getValue(paramSessionLocal).getString() : null; if (str1 == null) { ValueNull valueNull = ValueNull.INSTANCE; } else { String str4 = value3.getString(); value1 = regexpReplace(paramSessionLocal, str1, (str4 != null) ? str4 : "", (str2 != null) ? str2 : "", bool, j, str); }  } else { if (i > 4) throw DbException.get(7001, new String[] { getName(), "3..4" });  Value value9 = this.args[2].getValue(paramSessionLocal); Value value10 = (i == 4) ? this.args[3].getValue(paramSessionLocal) : null; if (value1 == ValueNull.INSTANCE || value3 == ValueNull.INSTANCE || value9 == ValueNull.INSTANCE || value10 == ValueNull.INSTANCE) { ValueNull valueNull = ValueNull.INSTANCE; } else { value1 = regexpReplace(paramSessionLocal, str1, value3.getString(), value9.getString(), 1, 0, (value10 != null) ? value10.getString() : null); }  }  return value1;case 2: value4 = (i >= 3) ? this.args[2].getValue(paramSessionLocal) : null; value6 = (i >= 4) ? this.args[3].getValue(paramSessionLocal) : null; value7 = (i >= 5) ? this.args[4].getValue(paramSessionLocal) : null; value8 = (i >= 6) ? this.args[5].getValue(paramSessionLocal) : null; value1 = regexpSubstr(value1, value3, value4, value6, value7, value8, paramSessionLocal); return value1;
/*     */     } 
/*     */     throw DbException.getInternalError("function=" + this.function); }
/*     */   
/*     */   private static Value regexpReplace(SessionLocal paramSessionLocal, String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, String paramString4) {
/* 123 */     Mode mode = paramSessionLocal.getMode();
/* 124 */     if (mode.regexpReplaceBackslashReferences && (
/* 125 */       paramString3.indexOf('\\') >= 0 || paramString3.indexOf('$') >= 0)) {
/* 126 */       StringBuilder stringBuilder = new StringBuilder();
/* 127 */       for (byte b = 0; b < paramString3.length(); b++) {
/* 128 */         char c = paramString3.charAt(b);
/* 129 */         if (c == '$') {
/* 130 */           stringBuilder.append('\\');
/* 131 */         } else if (c == '\\' && ++b < paramString3.length()) {
/* 132 */           c = paramString3.charAt(b);
/* 133 */           stringBuilder.append((c >= '0' && c <= '9') ? 36 : 92);
/*     */         } 
/* 135 */         stringBuilder.append(c);
/*     */       } 
/* 137 */       paramString3 = stringBuilder.toString();
/*     */     } 
/*     */     
/* 140 */     boolean bool = (mode.getEnum() == Mode.ModeEnum.PostgreSQL) ? true : false;
/* 141 */     int i = makeRegexpFlags(paramString4, bool);
/* 142 */     if (bool && (paramString4 == null || paramString4.isEmpty() || !paramString4.contains("g"))) {
/* 143 */       paramInt2 = 1;
/*     */     }
/*     */     try {
/* 146 */       Matcher matcher = Pattern.compile(paramString2, i).matcher(paramString1).region(paramInt1 - 1, paramString1.length());
/* 147 */       if (paramInt2 == 0) {
/* 148 */         return ValueVarchar.get(matcher.replaceAll(paramString3), (CastDataProvider)paramSessionLocal);
/*     */       }
/* 150 */       StringBuffer stringBuffer = new StringBuffer();
/* 151 */       int j = 1;
/* 152 */       while (matcher.find()) {
/* 153 */         if (j == paramInt2) {
/* 154 */           matcher.appendReplacement(stringBuffer, paramString3);
/*     */           break;
/*     */         } 
/* 157 */         j++;
/*     */       } 
/* 159 */       matcher.appendTail(stringBuffer);
/* 160 */       return ValueVarchar.get(stringBuffer.toString(), (CastDataProvider)paramSessionLocal);
/*     */     }
/* 162 */     catch (PatternSyntaxException patternSyntaxException) {
/* 163 */       throw DbException.get(22025, patternSyntaxException, new String[] { paramString2 });
/* 164 */     } catch (StringIndexOutOfBoundsException|IllegalArgumentException stringIndexOutOfBoundsException) {
/* 165 */       throw DbException.get(22025, stringIndexOutOfBoundsException, new String[] { paramString3 });
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static Value regexpSubstr(Value paramValue1, Value paramValue2, Value paramValue3, Value paramValue4, Value paramValue5, Value paramValue6, SessionLocal paramSessionLocal) {
/* 171 */     if (paramValue1 == ValueNull.INSTANCE || paramValue2 == ValueNull.INSTANCE || paramValue3 == ValueNull.INSTANCE || paramValue4 == ValueNull.INSTANCE || paramValue6 == ValueNull.INSTANCE)
/*     */     {
/* 173 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/* 175 */     String str1 = paramValue2.getString();
/*     */     
/* 177 */     boolean bool1 = (paramValue3 != null) ? (paramValue3.getInt() - 1) : false;
/* 178 */     byte b = (paramValue4 != null) ? paramValue4.getInt() : 1;
/* 179 */     String str2 = (paramValue5 != null) ? paramValue5.getString() : null;
/* 180 */     boolean bool2 = (paramValue6 != null) ? paramValue6.getInt() : false;
/* 181 */     int i = makeRegexpFlags(str2, false);
/*     */     try {
/* 183 */       Matcher matcher = Pattern.compile(str1, i).matcher(paramValue1.getString());
/*     */       
/* 185 */       boolean bool = matcher.find(bool1);
/* 186 */       for (byte b1 = 1; b1 < b && bool; b1++) {
/* 187 */         bool = matcher.find();
/*     */       }
/*     */       
/* 190 */       if (!bool) {
/* 191 */         return (Value)ValueNull.INSTANCE;
/*     */       }
/* 193 */       return ValueVarchar.get(matcher.group(bool2), (CastDataProvider)paramSessionLocal);
/*     */     }
/* 195 */     catch (PatternSyntaxException patternSyntaxException) {
/* 196 */       throw DbException.get(22025, patternSyntaxException, new String[] { str1 });
/* 197 */     } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
/* 198 */       return (Value)ValueNull.INSTANCE;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static int makeRegexpFlags(String paramString, boolean paramBoolean) {
/* 203 */     int i = 64;
/* 204 */     if (paramString != null) {
/* 205 */       for (byte b = 0; b < paramString.length(); b++) {
/* 206 */         switch (paramString.charAt(b)) {
/*     */           case 'i':
/* 208 */             i |= 0x2;
/*     */             break;
/*     */           case 'c':
/* 211 */             i &= 0xFFFFFFFD;
/*     */             break;
/*     */           case 'n':
/* 214 */             i |= 0x20;
/*     */             break;
/*     */           case 'm':
/* 217 */             i |= 0x8;
/*     */             break;
/*     */           case 'g':
/* 220 */             if (paramBoolean) {
/*     */               break;
/*     */             }
/*     */           
/*     */           default:
/* 225 */             throw DbException.get(90008, paramString);
/*     */         } 
/*     */       } 
/*     */     }
/* 229 */     return i;
/*     */   }
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*     */     byte b1, b2;
/* 234 */     boolean bool = optimizeArguments(paramSessionLocal, true);
/*     */     
/* 236 */     switch (this.function) {
/*     */       case 0:
/* 238 */         b1 = 2;
/* 239 */         b2 = 3;
/* 240 */         this.type = TypeInfo.TYPE_BOOLEAN;
/*     */         break;
/*     */       case 1:
/* 243 */         b1 = 3;
/* 244 */         b2 = 6;
/* 245 */         this.type = TypeInfo.TYPE_VARCHAR;
/*     */         break;
/*     */       case 2:
/* 248 */         b1 = 2;
/* 249 */         b2 = 6;
/* 250 */         this.type = TypeInfo.TYPE_VARCHAR;
/*     */         break;
/*     */       default:
/* 253 */         throw DbException.getInternalError("function=" + this.function);
/*     */     } 
/* 255 */     int i = this.args.length;
/* 256 */     if (i < b1 || i > b2) {
/* 257 */       throw DbException.get(7001, new String[] { getName(), b1 + ".." + b2 });
/*     */     }
/* 259 */     if (bool) {
/* 260 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*     */     }
/* 262 */     return (Expression)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 267 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\RegexpFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */