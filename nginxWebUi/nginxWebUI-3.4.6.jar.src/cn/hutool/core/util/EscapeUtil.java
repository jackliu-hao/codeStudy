/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.lang.Filter;
/*     */ import cn.hutool.core.text.escape.Html4Escape;
/*     */ import cn.hutool.core.text.escape.Html4Unescape;
/*     */ import cn.hutool.core.text.escape.XmlEscape;
/*     */ import cn.hutool.core.text.escape.XmlUnescape;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EscapeUtil
/*     */ {
/*     */   private static final String NOT_ESCAPE_CHARS = "*@-_+./";
/*     */   private static final Filter<Character> JS_ESCAPE_FILTER;
/*     */   
/*     */   static {
/*  23 */     JS_ESCAPE_FILTER = (c -> (false == ((Character.isDigit(c.charValue()) || Character.isLowerCase(c.charValue()) || Character.isUpperCase(c.charValue()) || StrUtil.contains("*@-_+./", c.charValue())) ? true : false)));
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
/*     */   public static String escapeXml(CharSequence xml) {
/*  45 */     XmlEscape escape = new XmlEscape();
/*  46 */     return escape.replace(xml).toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String unescapeXml(CharSequence xml) {
/*  57 */     XmlUnescape unescape = new XmlUnescape();
/*  58 */     return unescape.replace(xml).toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String escapeHtml4(CharSequence html) {
/*  69 */     Html4Escape escape = new Html4Escape();
/*  70 */     return escape.replace(html).toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String unescapeHtml4(CharSequence html) {
/*  81 */     Html4Unescape unescape = new Html4Unescape();
/*  82 */     return unescape.replace(html).toString();
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
/*     */   public static String escape(CharSequence content) {
/*  94 */     return escape(content, JS_ESCAPE_FILTER);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String escapeAll(CharSequence content) {
/* 105 */     return escape(content, c -> true);
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
/*     */   public static String escape(CharSequence content, Filter<Character> filter) {
/* 117 */     if (StrUtil.isEmpty(content)) {
/* 118 */       return StrUtil.str(content);
/*     */     }
/*     */     
/* 121 */     StringBuilder tmp = new StringBuilder(content.length() * 6);
/*     */     
/* 123 */     for (int i = 0; i < content.length(); i++) {
/* 124 */       char c = content.charAt(i);
/* 125 */       if (false == filter.accept(Character.valueOf(c))) {
/* 126 */         tmp.append(c);
/* 127 */       } else if (c < 'Ā') {
/* 128 */         tmp.append("%");
/* 129 */         if (c < '\020') {
/* 130 */           tmp.append("0");
/*     */         }
/* 132 */         tmp.append(Integer.toString(c, 16));
/*     */       } else {
/* 134 */         tmp.append("%u");
/* 135 */         if (c <= '࿿')
/*     */         {
/* 137 */           tmp.append("0");
/*     */         }
/* 139 */         tmp.append(Integer.toString(c, 16));
/*     */       } 
/*     */     } 
/* 142 */     return tmp.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String unescape(String content) {
/* 152 */     if (StrUtil.isBlank(content)) {
/* 153 */       return content;
/*     */     }
/*     */     
/* 156 */     StringBuilder tmp = new StringBuilder(content.length());
/* 157 */     int lastPos = 0;
/*     */ 
/*     */     
/* 160 */     while (lastPos < content.length()) {
/* 161 */       int pos = content.indexOf("%", lastPos);
/* 162 */       if (pos == lastPos) {
/* 163 */         if (content.charAt(pos + 1) == 'u') {
/* 164 */           char c = (char)Integer.parseInt(content.substring(pos + 2, pos + 6), 16);
/* 165 */           tmp.append(c);
/* 166 */           lastPos = pos + 6; continue;
/*     */         } 
/* 168 */         char ch = (char)Integer.parseInt(content.substring(pos + 1, pos + 3), 16);
/* 169 */         tmp.append(ch);
/* 170 */         lastPos = pos + 3;
/*     */         continue;
/*     */       } 
/* 173 */       if (pos == -1) {
/* 174 */         tmp.append(content.substring(lastPos));
/* 175 */         lastPos = content.length(); continue;
/*     */       } 
/* 177 */       tmp.append(content, lastPos, pos);
/* 178 */       lastPos = pos;
/*     */     } 
/*     */ 
/*     */     
/* 182 */     return tmp.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String safeUnescape(String content) {
/*     */     try {
/* 193 */       return unescape(content);
/* 194 */     } catch (Exception exception) {
/*     */ 
/*     */       
/* 197 */       return content;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\EscapeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */