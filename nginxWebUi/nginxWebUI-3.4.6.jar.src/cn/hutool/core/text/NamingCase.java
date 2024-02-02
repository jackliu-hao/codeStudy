/*     */ package cn.hutool.core.text;
/*     */ 
/*     */ import cn.hutool.core.util.CharUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NamingCase
/*     */ {
/*     */   public static String toUnderlineCase(CharSequence str) {
/*  34 */     return toSymbolCase(str, '_');
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toKebabCase(CharSequence str) {
/*  57 */     return toSymbolCase(str, '-');
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
/*     */   public static String toSymbolCase(CharSequence str, char symbol) {
/*  69 */     if (str == null) {
/*  70 */       return null;
/*     */     }
/*     */     
/*  73 */     int length = str.length();
/*  74 */     StrBuilder sb = new StrBuilder();
/*     */     
/*  76 */     for (int i = 0; i < length; i++) {
/*  77 */       char c = str.charAt(i);
/*  78 */       if (Character.isUpperCase(c)) {
/*  79 */         Character preChar = (i > 0) ? Character.valueOf(str.charAt(i - 1)) : null;
/*  80 */         Character nextChar = (i < str.length() - 1) ? Character.valueOf(str.charAt(i + 1)) : null;
/*     */         
/*  82 */         if (null != preChar) {
/*  83 */           if (symbol == preChar.charValue())
/*     */           {
/*  85 */             if (null == nextChar || Character.isLowerCase(nextChar.charValue()))
/*     */             {
/*  87 */               c = Character.toLowerCase(c);
/*     */             }
/*     */           }
/*  90 */           else if (Character.isLowerCase(preChar.charValue()))
/*     */           {
/*  92 */             sb.append(symbol);
/*  93 */             if (null == nextChar || Character.isLowerCase(nextChar.charValue()) || CharUtil.isNumber(nextChar.charValue()))
/*     */             {
/*  95 */               c = Character.toLowerCase(c);
/*     */             
/*     */             }
/*     */           
/*     */           }
/* 100 */           else if (null != nextChar && Character.isLowerCase(nextChar.charValue()))
/*     */           {
/* 102 */             sb.append(symbol);
/* 103 */             c = Character.toLowerCase(c);
/*     */           
/*     */           }
/*     */ 
/*     */         
/*     */         }
/* 109 */         else if (null == nextChar || Character.isLowerCase(nextChar.charValue())) {
/*     */           
/* 111 */           c = Character.toLowerCase(c);
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 116 */       sb.append(c);
/*     */     } 
/* 118 */     return sb.toString();
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toPascalCase(CharSequence name) {
/* 136 */     return StrUtil.upperFirst(toCamelCase(name));
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
/*     */ 
/*     */   
/*     */   public static String toCamelCase(CharSequence name) {
/* 153 */     return toCamelCase(name, '_');
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
/*     */   public static String toCamelCase(CharSequence name, char symbol) {
/* 165 */     if (null == name) {
/* 166 */       return null;
/*     */     }
/*     */     
/* 169 */     String name2 = name.toString();
/* 170 */     if (StrUtil.contains(name2, symbol)) {
/* 171 */       int length = name2.length();
/* 172 */       StringBuilder sb = new StringBuilder(length);
/* 173 */       boolean upperCase = false;
/* 174 */       for (int i = 0; i < length; i++) {
/* 175 */         char c = name2.charAt(i);
/*     */         
/* 177 */         if (c == symbol) {
/* 178 */           upperCase = true;
/* 179 */         } else if (upperCase) {
/* 180 */           sb.append(Character.toUpperCase(c));
/* 181 */           upperCase = false;
/*     */         } else {
/* 183 */           sb.append(Character.toLowerCase(c));
/*     */         } 
/*     */       } 
/* 186 */       return sb.toString();
/*     */     } 
/* 188 */     return name2;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\NamingCase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */