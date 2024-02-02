/*     */ package org.h2.value;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Locale;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.message.DbException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ExtTypeInfoEnum
/*     */   extends ExtTypeInfo
/*     */ {
/*     */   private final String[] enumerators;
/*     */   private final String[] cleaned;
/*     */   private TypeInfo type;
/*     */   
/*     */   public static ExtTypeInfoEnum getEnumeratorsForBinaryOperation(Value paramValue1, Value paramValue2) {
/*  36 */     if (paramValue1.getValueType() == 36)
/*  37 */       return ((ValueEnum)paramValue1).getEnumerators(); 
/*  38 */     if (paramValue2.getValueType() == 36) {
/*  39 */       return ((ValueEnum)paramValue2).getEnumerators();
/*     */     }
/*  41 */     throw DbException.get(50004, "type1=" + paramValue1
/*  42 */         .getValueType() + ", type2=" + paramValue2.getValueType());
/*     */   }
/*     */ 
/*     */   
/*     */   private static String sanitize(String paramString) {
/*  47 */     if (paramString == null) {
/*  48 */       return null;
/*     */     }
/*  50 */     int i = paramString.length();
/*  51 */     if (i > 1048576) {
/*  52 */       throw DbException.getValueTooLongException("ENUM", paramString, i);
/*     */     }
/*  54 */     return paramString.trim().toUpperCase(Locale.ENGLISH);
/*     */   }
/*     */   
/*     */   private static StringBuilder toSQL(StringBuilder paramStringBuilder, String[] paramArrayOfString) {
/*  58 */     paramStringBuilder.append('(');
/*  59 */     for (byte b = 0; b < paramArrayOfString.length; b++) {
/*  60 */       if (b != 0) {
/*  61 */         paramStringBuilder.append(", ");
/*     */       }
/*  63 */       paramStringBuilder.append('\'');
/*  64 */       String str = paramArrayOfString[b]; byte b1; int i;
/*  65 */       for (b1 = 0, i = str.length(); b1 < i; b1++) {
/*  66 */         char c = str.charAt(b1);
/*  67 */         if (c == '\'') {
/*  68 */           paramStringBuilder.append('\'');
/*     */         }
/*  70 */         paramStringBuilder.append(c);
/*     */       } 
/*  72 */       paramStringBuilder.append('\'');
/*     */     } 
/*  74 */     return paramStringBuilder.append(')');
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExtTypeInfoEnum(String[] paramArrayOfString) {
/*     */     int i;
/*  85 */     if (paramArrayOfString == null || (i = paramArrayOfString.length) == 0) {
/*  86 */       throw DbException.get(22032);
/*     */     }
/*  88 */     if (i > 65536) {
/*  89 */       throw DbException.getValueTooLongException("ENUM", "(" + i + " elements)", i);
/*     */     }
/*  91 */     String[] arrayOfString = new String[i];
/*  92 */     for (byte b = 0; b < i; b++) {
/*  93 */       String str = sanitize(paramArrayOfString[b]);
/*  94 */       if (str == null || str.isEmpty()) {
/*  95 */         throw DbException.get(22032);
/*     */       }
/*  97 */       for (byte b1 = 0; b1 < b; b1++) {
/*  98 */         if (str.equals(arrayOfString[b1])) {
/*  99 */           throw DbException.get(22033, 
/* 100 */               toSQL(new StringBuilder(), paramArrayOfString).toString());
/*     */         }
/*     */       } 
/* 103 */       arrayOfString[b] = str;
/*     */     } 
/* 105 */     this.enumerators = paramArrayOfString;
/* 106 */     this.cleaned = Arrays.equals((Object[])arrayOfString, (Object[])paramArrayOfString) ? paramArrayOfString : arrayOfString;
/*     */   }
/*     */   
/*     */   TypeInfo getType() {
/* 110 */     TypeInfo typeInfo = this.type;
/* 111 */     if (typeInfo == null) {
/* 112 */       int i = 0;
/* 113 */       for (String str : this.enumerators) {
/* 114 */         int j = str.length();
/* 115 */         if (j > i) {
/* 116 */           i = j;
/*     */         }
/*     */       } 
/* 119 */       this.type = typeInfo = new TypeInfo(36, i, 0, this);
/*     */     } 
/* 121 */     return typeInfo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCount() {
/* 130 */     return this.enumerators.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getEnumerator(int paramInt) {
/* 141 */     return this.enumerators[paramInt];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ValueEnum getValue(int paramInt, CastDataProvider paramCastDataProvider) {
/*     */     String str;
/* 152 */     if (paramCastDataProvider == null || !paramCastDataProvider.zeroBasedEnums()) {
/* 153 */       if (paramInt < 1 || paramInt > this.enumerators.length) {
/* 154 */         throw DbException.get(22030, new String[] { getTraceSQL(), Integer.toString(paramInt) });
/*     */       }
/* 156 */       str = this.enumerators[paramInt - 1];
/*     */     } else {
/* 158 */       if (paramInt < 0 || paramInt >= this.enumerators.length) {
/* 159 */         throw DbException.get(22030, new String[] { getTraceSQL(), Integer.toString(paramInt) });
/*     */       }
/* 161 */       str = this.enumerators[paramInt];
/*     */     } 
/* 163 */     return new ValueEnum(this, str, paramInt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ValueEnum getValue(String paramString, CastDataProvider paramCastDataProvider) {
/* 173 */     ValueEnum valueEnum = getValueOrNull(paramString, paramCastDataProvider);
/* 174 */     if (valueEnum == null) {
/* 175 */       throw DbException.get(22030, new String[] { toString(), paramString });
/*     */     }
/* 177 */     return valueEnum;
/*     */   }
/*     */   
/*     */   private ValueEnum getValueOrNull(String paramString, CastDataProvider paramCastDataProvider) {
/* 181 */     String str = sanitize(paramString);
/* 182 */     if (str != null) {
/* 183 */       byte b1 = 0, b2 = (paramCastDataProvider == null || !paramCastDataProvider.zeroBasedEnums()) ? 1 : 0;
/* 184 */       for (; b1 < this.cleaned.length; b1++, b2++) {
/* 185 */         if (str.equals(this.cleaned[b1])) {
/* 186 */           return new ValueEnum(this, this.enumerators[b1], b2);
/*     */         }
/*     */       } 
/*     */     } 
/* 190 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 195 */     return Arrays.hashCode((Object[])this.enumerators) + 203117;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 200 */     if (this == paramObject) {
/* 201 */       return true;
/*     */     }
/* 203 */     if (paramObject == null || paramObject.getClass() != ExtTypeInfoEnum.class) {
/* 204 */       return false;
/*     */     }
/* 206 */     return Arrays.equals((Object[])this.enumerators, (Object[])((ExtTypeInfoEnum)paramObject).enumerators);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 211 */     return toSQL(paramStringBuilder, this.enumerators);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ExtTypeInfoEnum.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */