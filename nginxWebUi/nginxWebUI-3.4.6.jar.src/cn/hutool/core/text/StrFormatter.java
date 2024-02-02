/*     */ package cn.hutool.core.text;
/*     */ 
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StrFormatter
/*     */ {
/*     */   public static String format(String strPattern, Object... argArray) {
/*  29 */     return formatWith(strPattern, "{}", argArray);
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
/*     */   public static String formatWith(String strPattern, String placeHolder, Object... argArray) {
/*  48 */     if (StrUtil.isBlank(strPattern) || StrUtil.isBlank(placeHolder) || ArrayUtil.isEmpty(argArray)) {
/*  49 */       return strPattern;
/*     */     }
/*  51 */     int strPatternLength = strPattern.length();
/*  52 */     int placeHolderLength = placeHolder.length();
/*     */ 
/*     */     
/*  55 */     StringBuilder sbuf = new StringBuilder(strPatternLength + 50);
/*     */     
/*  57 */     int handledPosition = 0;
/*     */     
/*  59 */     for (int argIndex = 0; argIndex < argArray.length; argIndex++) {
/*  60 */       int delimIndex = strPattern.indexOf(placeHolder, handledPosition);
/*  61 */       if (delimIndex == -1) {
/*  62 */         if (handledPosition == 0) {
/*  63 */           return strPattern;
/*     */         }
/*     */         
/*  66 */         sbuf.append(strPattern, handledPosition, strPatternLength);
/*  67 */         return sbuf.toString();
/*     */       } 
/*     */ 
/*     */       
/*  71 */       if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == '\\') {
/*  72 */         if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == '\\') {
/*     */           
/*  74 */           sbuf.append(strPattern, handledPosition, delimIndex - 1);
/*  75 */           sbuf.append(StrUtil.utf8Str(argArray[argIndex]));
/*  76 */           handledPosition = delimIndex + placeHolderLength;
/*     */         } else {
/*     */           
/*  79 */           argIndex--;
/*  80 */           sbuf.append(strPattern, handledPosition, delimIndex - 1);
/*  81 */           sbuf.append(placeHolder.charAt(0));
/*  82 */           handledPosition = delimIndex + 1;
/*     */         } 
/*     */       } else {
/*  85 */         sbuf.append(strPattern, handledPosition, delimIndex);
/*  86 */         sbuf.append(StrUtil.utf8Str(argArray[argIndex]));
/*  87 */         handledPosition = delimIndex + placeHolderLength;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  92 */     sbuf.append(strPattern, handledPosition, strPatternLength);
/*     */     
/*  94 */     return sbuf.toString();
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
/*     */   public static String format(CharSequence template, Map<?, ?> map, boolean ignoreNull) {
/* 108 */     if (null == template) {
/* 109 */       return null;
/*     */     }
/* 111 */     if (null == map || map.isEmpty()) {
/* 112 */       return template.toString();
/*     */     }
/*     */     
/* 115 */     String template2 = template.toString();
/*     */     
/* 117 */     for (Map.Entry<?, ?> entry : map.entrySet()) {
/* 118 */       String value = StrUtil.utf8Str(entry.getValue());
/* 119 */       if (null == value && ignoreNull) {
/*     */         continue;
/*     */       }
/* 122 */       template2 = StrUtil.replace(template2, "{" + entry.getKey() + "}", value);
/*     */     } 
/* 124 */     return template2;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\StrFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */