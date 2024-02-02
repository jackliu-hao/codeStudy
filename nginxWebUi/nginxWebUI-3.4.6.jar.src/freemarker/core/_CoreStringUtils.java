/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.utility.StringUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class _CoreStringUtils
/*     */ {
/*     */   public static String toFTLIdentifierReferenceAfterDot(String name) {
/*  37 */     return backslashEscapeIdentifier(name);
/*     */   }
/*     */   
/*     */   public static String toFTLTopLevelIdentifierReference(String name) {
/*  41 */     return backslashEscapeIdentifier(name);
/*     */   }
/*     */   
/*     */   public static String toFTLTopLevelTragetIdentifier(String name) {
/*  45 */     char quotationType = Character.MIN_VALUE;
/*  46 */     for (int i = 0; i < name.length(); i++) {
/*  47 */       char c = name.charAt(i);
/*  48 */       if (((i == 0) ? StringUtil.isFTLIdentifierStart(c) : StringUtil.isFTLIdentifierPart(c)) && c != '@') {
/*  49 */         if ((quotationType == '\000' || quotationType == '\\') && 
/*  50 */           StringUtil.isBackslashEscapedFTLIdentifierCharacter(c)) {
/*  51 */           quotationType = '\\';
/*     */         } else {
/*  53 */           quotationType = '"';
/*     */           break;
/*     */         } 
/*     */       }
/*     */     } 
/*  58 */     switch (quotationType) {
/*     */       case '\000':
/*  60 */         return name;
/*     */       case '"':
/*  62 */         return StringUtil.ftlQuote(name);
/*     */       case '\\':
/*  64 */         return backslashEscapeIdentifier(name);
/*     */     } 
/*  66 */     throw new BugException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String backslashEscapeIdentifier(String name) {
/*  76 */     StringBuilder sb = null;
/*  77 */     for (int i = 0; i < name.length(); i++) {
/*  78 */       char c = name.charAt(i);
/*  79 */       if (StringUtil.isBackslashEscapedFTLIdentifierCharacter(c)) {
/*  80 */         if (sb == null) {
/*  81 */           sb = new StringBuilder(name.length() + 8);
/*  82 */           sb.append(name, 0, i);
/*     */         } 
/*  84 */         sb.append('\\');
/*     */       } 
/*  86 */       if (sb != null) {
/*  87 */         sb.append(c);
/*     */       }
/*     */     } 
/*  90 */     return (sb == null) ? name : sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getIdentifierNamingConvention(String name) {
/*  98 */     int ln = name.length();
/*  99 */     for (int i = 0; i < ln; i++) {
/* 100 */       char c = name.charAt(i);
/* 101 */       if (c == '_') {
/* 102 */         return 11;
/*     */       }
/* 104 */       if (isUpperUSASCII(c)) {
/* 105 */         return 12;
/*     */       }
/*     */     } 
/* 108 */     return 10;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String camelCaseToUnderscored(String camelCaseName) {
/* 117 */     int i = 0;
/* 118 */     while (i < camelCaseName.length() && Character.isLowerCase(camelCaseName.charAt(i))) {
/* 119 */       i++;
/*     */     }
/* 121 */     if (i == camelCaseName.length())
/*     */     {
/* 123 */       return camelCaseName;
/*     */     }
/*     */     
/* 126 */     StringBuilder sb = new StringBuilder();
/* 127 */     sb.append(camelCaseName.substring(0, i));
/* 128 */     while (i < camelCaseName.length()) {
/* 129 */       char c = camelCaseName.charAt(i);
/* 130 */       if (isUpperUSASCII(c)) {
/* 131 */         sb.append('_');
/* 132 */         sb.append(Character.toLowerCase(c));
/*     */       } else {
/* 134 */         sb.append(c);
/*     */       } 
/* 136 */       i++;
/*     */     } 
/* 138 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public static boolean isUpperUSASCII(char c) {
/* 142 */     return (c >= 'A' && c <= 'Z');
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_CoreStringUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */