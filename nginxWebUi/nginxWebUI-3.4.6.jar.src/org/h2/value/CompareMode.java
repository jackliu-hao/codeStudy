/*     */ package org.h2.value;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import java.text.Collator;
/*     */ import java.util.Comparator;
/*     */ import java.util.Locale;
/*     */ import java.util.Objects;
/*     */ import org.h2.util.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CompareMode
/*     */   implements Comparator<Value>
/*     */ {
/*     */   public static final String OFF = "OFF";
/*     */   public static final String DEFAULT = "DEFAULT_";
/*     */   public static final String ICU4J = "ICU4J_";
/*     */   public static final String CHARSET = "CHARSET_";
/*     */   private static Locale[] LOCALES;
/*     */   private static volatile CompareMode lastUsed;
/*     */   private static final boolean CAN_USE_ICU4J;
/*     */   private final String name;
/*     */   private final int strength;
/*     */   
/*     */   static {
/*  53 */     boolean bool = false;
/*     */     try {
/*  55 */       Class.forName("com.ibm.icu.text.Collator");
/*  56 */       bool = true;
/*  57 */     } catch (Exception exception) {}
/*     */ 
/*     */     
/*  60 */     CAN_USE_ICU4J = bool;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected CompareMode(String paramString, int paramInt) {
/*  67 */     this.name = paramString;
/*  68 */     this.strength = paramInt;
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
/*     */   public static CompareMode getInstance(String paramString, int paramInt) {
/*  82 */     CompareMode compareMode = lastUsed;
/*  83 */     if (compareMode != null && Objects.equals(compareMode.name, paramString) && compareMode.strength == paramInt) {
/*  84 */       return compareMode;
/*     */     }
/*  86 */     if (paramString == null || paramString.equals("OFF")) {
/*  87 */       compareMode = new CompareMode(paramString, paramInt);
/*     */     } else {
/*     */       boolean bool;
/*  90 */       if (paramString.startsWith("ICU4J_")) {
/*  91 */         bool = true;
/*  92 */         paramString = paramString.substring("ICU4J_".length());
/*  93 */       } else if (paramString.startsWith("DEFAULT_")) {
/*  94 */         bool = false;
/*  95 */         paramString = paramString.substring("DEFAULT_".length());
/*  96 */       } else if (paramString.startsWith("CHARSET_")) {
/*  97 */         bool = false;
/*     */       } else {
/*  99 */         bool = CAN_USE_ICU4J;
/*     */       } 
/* 101 */       if (bool) {
/* 102 */         compareMode = new CompareModeIcu4J(paramString, paramInt);
/*     */       } else {
/* 104 */         compareMode = new CompareModeDefault(paramString, paramInt);
/*     */       } 
/*     */     } 
/* 107 */     lastUsed = compareMode;
/* 108 */     return compareMode;
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
/*     */   public static Locale[] getCollationLocales(boolean paramBoolean) {
/* 120 */     Locale[] arrayOfLocale = LOCALES;
/* 121 */     if (arrayOfLocale == null && !paramBoolean) {
/* 122 */       LOCALES = arrayOfLocale = Collator.getAvailableLocales();
/*     */     }
/* 124 */     return arrayOfLocale;
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
/*     */   public boolean equalsChars(String paramString1, int paramInt1, String paramString2, int paramInt2, boolean paramBoolean) {
/* 138 */     char c1 = paramString1.charAt(paramInt1);
/* 139 */     char c2 = paramString2.charAt(paramInt2);
/* 140 */     if (c1 == c2) {
/* 141 */       return true;
/*     */     }
/* 143 */     if (paramBoolean && (
/* 144 */       Character.toUpperCase(c1) == Character.toUpperCase(c2) || 
/* 145 */       Character.toLowerCase(c1) == Character.toLowerCase(c2))) {
/* 146 */       return true;
/*     */     }
/*     */     
/* 149 */     return false;
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
/*     */   public int compareString(String paramString1, String paramString2, boolean paramBoolean) {
/* 162 */     if (paramBoolean) {
/* 163 */       return paramString1.compareToIgnoreCase(paramString2);
/*     */     }
/* 165 */     return paramString1.compareTo(paramString2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getName(Locale paramLocale) {
/* 175 */     Locale locale = Locale.ENGLISH;
/*     */     
/* 177 */     String str = paramLocale.getDisplayLanguage(locale) + ' ' + paramLocale.getDisplayCountry(locale) + ' ' + paramLocale.getVariant();
/* 178 */     str = StringUtils.toUpperEnglish(str.trim().replace(' ', '_'));
/* 179 */     return str;
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
/*     */   static boolean compareLocaleNames(Locale paramLocale, String paramString) {
/* 191 */     return (paramString.equalsIgnoreCase(paramLocale.toString()) || paramString.equalsIgnoreCase(paramLocale.toLanguageTag()) || paramString
/* 192 */       .equalsIgnoreCase(getName(paramLocale)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Collator getCollator(String paramString) {
/* 203 */     Collator collator = null;
/* 204 */     if (paramString.startsWith("ICU4J_")) {
/* 205 */       paramString = paramString.substring("ICU4J_".length());
/* 206 */     } else if (paramString.startsWith("DEFAULT_")) {
/* 207 */       paramString = paramString.substring("DEFAULT_".length());
/* 208 */     } else if (paramString.startsWith("CHARSET_")) {
/* 209 */       return new CharsetCollator(Charset.forName(paramString.substring("CHARSET_".length())));
/*     */     } 
/* 211 */     int i = paramString.length();
/* 212 */     if (i == 2) {
/* 213 */       Locale locale = new Locale(StringUtils.toLowerEnglish(paramString), "");
/* 214 */       if (compareLocaleNames(locale, paramString)) {
/* 215 */         collator = Collator.getInstance(locale);
/*     */       }
/* 217 */     } else if (i == 5) {
/*     */       
/* 219 */       int j = paramString.indexOf('_');
/* 220 */       if (j >= 0) {
/* 221 */         String str1 = StringUtils.toLowerEnglish(paramString.substring(0, j));
/* 222 */         String str2 = paramString.substring(j + 1);
/* 223 */         Locale locale = new Locale(str1, str2);
/* 224 */         if (compareLocaleNames(locale, paramString)) {
/* 225 */           collator = Collator.getInstance(locale);
/*     */         }
/*     */       } 
/* 228 */     } else if (paramString.indexOf('-') > 0) {
/* 229 */       Locale locale = Locale.forLanguageTag(paramString);
/* 230 */       if (!locale.getLanguage().isEmpty()) {
/* 231 */         return Collator.getInstance(locale);
/*     */       }
/*     */     } 
/* 234 */     if (collator == null) {
/* 235 */       for (Locale locale : getCollationLocales(false)) {
/* 236 */         if (compareLocaleNames(locale, paramString)) {
/* 237 */           collator = Collator.getInstance(locale);
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     }
/* 242 */     return collator;
/*     */   }
/*     */   
/*     */   public String getName() {
/* 246 */     return (this.name == null) ? "OFF" : this.name;
/*     */   }
/*     */   
/*     */   public int getStrength() {
/* 250 */     return this.strength;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 255 */     if (paramObject == this)
/* 256 */       return true; 
/* 257 */     if (!(paramObject instanceof CompareMode)) {
/* 258 */       return false;
/*     */     }
/* 260 */     CompareMode compareMode = (CompareMode)paramObject;
/* 261 */     if (!getName().equals(compareMode.getName())) {
/* 262 */       return false;
/*     */     }
/* 264 */     if (this.strength != compareMode.strength) {
/* 265 */       return false;
/*     */     }
/* 267 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 272 */     int i = 1;
/* 273 */     i = 31 * i + getName().hashCode();
/* 274 */     i = 31 * i + this.strength;
/* 275 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compare(Value paramValue1, Value paramValue2) {
/* 280 */     return paramValue1.compareTo(paramValue2, null, this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\CompareMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */