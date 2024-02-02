/*     */ package org.h2.server.web;
/*     */ 
/*     */ import java.text.ParseException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class PageParser
/*     */ {
/*     */   private static final int TAB_WIDTH = 4;
/*     */   private final String page;
/*     */   private int pos;
/*     */   private final Map<String, Object> settings;
/*     */   private final int len;
/*     */   private StringBuilder result;
/*     */   
/*     */   private PageParser(String paramString, Map<String, Object> paramMap, int paramInt) {
/*  30 */     this.page = paramString;
/*  31 */     this.pos = paramInt;
/*  32 */     this.len = paramString.length();
/*  33 */     this.settings = paramMap;
/*  34 */     this.result = new StringBuilder(this.len);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String parse(String paramString, Map<String, Object> paramMap) {
/*  45 */     PageParser pageParser = new PageParser(paramString, paramMap, 0);
/*  46 */     return pageParser.replaceTags();
/*     */   }
/*     */   
/*     */   private void setError(int paramInt) {
/*  50 */     String str = this.page.substring(0, paramInt) + "####BUG####" + this.page.substring(paramInt);
/*  51 */     str = escapeHtml(str);
/*  52 */     this.result = new StringBuilder();
/*  53 */     this.result.append(str);
/*     */   }
/*     */   
/*     */   private String parseBlockUntil(String paramString) throws ParseException {
/*  57 */     PageParser pageParser = new PageParser(this.page, this.settings, this.pos);
/*  58 */     pageParser.parseAll();
/*  59 */     if (!pageParser.readIf(paramString)) {
/*  60 */       throw new ParseException(this.page, pageParser.pos);
/*     */     }
/*  62 */     this.pos = pageParser.pos;
/*  63 */     return pageParser.result.toString();
/*     */   }
/*     */   
/*     */   private String replaceTags() {
/*     */     try {
/*  68 */       parseAll();
/*  69 */       if (this.pos != this.len) {
/*  70 */         setError(this.pos);
/*     */       }
/*  72 */     } catch (ParseException parseException) {
/*  73 */       setError(this.pos);
/*     */     } 
/*  75 */     return this.result.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   private void parseAll() throws ParseException {
/*  80 */     StringBuilder stringBuilder = this.result;
/*  81 */     String str = this.page;
/*  82 */     int i = this.pos;
/*  83 */     for (; i < this.len; i++) {
/*  84 */       char c = str.charAt(i);
/*  85 */       switch (c) {
/*     */         case '<':
/*  87 */           if (str.charAt(i + 3) == ':' && str.charAt(i + 1) == '/') {
/*     */             
/*  89 */             this.pos = i; return;
/*     */           } 
/*  91 */           if (str.charAt(i + 2) == ':') {
/*  92 */             this.pos = i;
/*  93 */             if (readIf("<c:forEach")) {
/*  94 */               String str1 = readParam("var");
/*  95 */               String str2 = readParam("items");
/*  96 */               read(">");
/*  97 */               int j = this.pos;
/*  98 */               List list = (List)get(str2);
/*  99 */               if (list == null) {
/* 100 */                 this.result.append("?items?");
/* 101 */                 list = new ArrayList();
/*     */               } 
/* 103 */               if (list.isEmpty()) {
/* 104 */                 parseBlockUntil("</c:forEach>");
/*     */               }
/* 106 */               for (Object object : list) {
/* 107 */                 this.settings.put(str1, object);
/* 108 */                 this.pos = j;
/* 109 */                 String str3 = parseBlockUntil("</c:forEach>");
/* 110 */                 this.result.append(str3);
/*     */               } 
/* 112 */             } else if (readIf("<c:if")) {
/* 113 */               String str1 = readParam("test");
/* 114 */               int j = str1.indexOf("=='");
/* 115 */               if (j < 0) {
/* 116 */                 setError(i);
/*     */                 return;
/*     */               } 
/* 119 */               String str2 = str1.substring(j + 3, str1.length() - 1);
/* 120 */               str1 = str1.substring(0, j);
/* 121 */               String str3 = (String)get(str1);
/* 122 */               read(">");
/* 123 */               String str4 = parseBlockUntil("</c:if>");
/* 124 */               this.pos--;
/* 125 */               if (str3.equals(str2)) {
/* 126 */                 this.result.append(str4);
/*     */               }
/*     */             } else {
/* 129 */               setError(i);
/*     */               return;
/*     */             } 
/* 132 */             i = this.pos; break;
/*     */           } 
/* 134 */           stringBuilder.append(c);
/*     */           break;
/*     */ 
/*     */         
/*     */         case '$':
/* 139 */           if (str.length() > i + 1 && str.charAt(i + 1) == '{') {
/* 140 */             i += 2;
/* 141 */             int j = str.indexOf('}', i);
/* 142 */             if (j < 0) {
/* 143 */               setError(i);
/*     */               return;
/*     */             } 
/* 146 */             String str1 = StringUtils.trimSubstring(str, i, j);
/* 147 */             i = j;
/* 148 */             String str2 = (String)get(str1);
/* 149 */             replaceTags(str2); break;
/*     */           } 
/* 151 */           stringBuilder.append(c);
/*     */           break;
/*     */         
/*     */         default:
/* 155 */           stringBuilder.append(c);
/*     */           break;
/*     */       } 
/*     */     } 
/* 159 */     this.pos = i;
/*     */   }
/*     */ 
/*     */   
/*     */   private Object get(String paramString) {
/* 164 */     int i = paramString.indexOf('.');
/* 165 */     if (i >= 0) {
/* 166 */       String str = paramString.substring(i + 1);
/* 167 */       paramString = paramString.substring(0, i);
/* 168 */       HashMap hashMap = (HashMap)this.settings.get(paramString);
/* 169 */       if (hashMap == null) {
/* 170 */         return "?" + paramString + "?";
/*     */       }
/* 172 */       return hashMap.get(str);
/*     */     } 
/* 174 */     return this.settings.get(paramString);
/*     */   }
/*     */   
/*     */   private void replaceTags(String paramString) {
/* 178 */     if (paramString != null) {
/* 179 */       this.result.append(parse(paramString, this.settings));
/*     */     }
/*     */   }
/*     */   
/*     */   private String readParam(String paramString) throws ParseException {
/* 184 */     read(paramString);
/* 185 */     read("=");
/* 186 */     read("\"");
/* 187 */     int i = this.pos;
/* 188 */     while (this.page.charAt(this.pos) != '"') {
/* 189 */       this.pos++;
/*     */     }
/* 191 */     int j = this.pos;
/* 192 */     read("\"");
/* 193 */     String str = this.page.substring(i, j);
/* 194 */     return parse(str, this.settings);
/*     */   }
/*     */   
/*     */   private void skipSpaces() {
/* 198 */     while (this.page.charAt(this.pos) == ' ') {
/* 199 */       this.pos++;
/*     */     }
/*     */   }
/*     */   
/*     */   private void read(String paramString) throws ParseException {
/* 204 */     if (!readIf(paramString)) {
/* 205 */       throw new ParseException(paramString, this.pos);
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean readIf(String paramString) {
/* 210 */     skipSpaces();
/* 211 */     if (this.page.regionMatches(this.pos, paramString, 0, paramString.length())) {
/* 212 */       this.pos += paramString.length();
/* 213 */       skipSpaces();
/* 214 */       return true;
/*     */     } 
/* 216 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String escapeHtmlData(String paramString) {
/* 226 */     return escapeHtml(paramString, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String escapeHtml(String paramString) {
/* 236 */     return escapeHtml(paramString, true);
/*     */   }
/*     */   
/*     */   private static String escapeHtml(String paramString, boolean paramBoolean) {
/* 240 */     if (paramString == null) {
/* 241 */       return null;
/*     */     }
/* 243 */     int i = paramString.length();
/* 244 */     if (paramBoolean && 
/* 245 */       i == 0) {
/* 246 */       return "&nbsp;";
/*     */     }
/*     */     
/* 249 */     StringBuilder stringBuilder = new StringBuilder(i);
/* 250 */     boolean bool = true; int j;
/* 251 */     for (j = 0; j < i; ) {
/* 252 */       int k = paramString.codePointAt(j);
/* 253 */       if (k == 32 || k == 9) {
/*     */         
/* 255 */         for (byte b = 0; b < ((k == 32) ? 1 : 4); b++) {
/* 256 */           if (bool && paramBoolean) {
/* 257 */             stringBuilder.append("&nbsp;");
/*     */           } else {
/* 259 */             stringBuilder.append(' ');
/* 260 */             bool = true;
/*     */           } 
/*     */         } 
/*     */       } else {
/* 264 */         bool = false;
/* 265 */         switch (k) {
/*     */           
/*     */           case 36:
/* 268 */             stringBuilder.append("&#36;");
/*     */             break;
/*     */           case 60:
/* 271 */             stringBuilder.append("&lt;");
/*     */             break;
/*     */           case 62:
/* 274 */             stringBuilder.append("&gt;");
/*     */             break;
/*     */           case 38:
/* 277 */             stringBuilder.append("&amp;");
/*     */             break;
/*     */           case 34:
/* 280 */             stringBuilder.append("&quot;");
/*     */             break;
/*     */           case 39:
/* 283 */             stringBuilder.append("&#39;");
/*     */             break;
/*     */           case 10:
/* 286 */             if (paramBoolean) {
/* 287 */               stringBuilder.append("<br />");
/* 288 */               bool = true; break;
/*     */             } 
/* 290 */             stringBuilder.append(k);
/*     */             break;
/*     */           
/*     */           default:
/* 294 */             if (k >= 128) {
/* 295 */               stringBuilder.append("&#").append(k).append(';'); break;
/*     */             } 
/* 297 */             stringBuilder.append((char)k);
/*     */             break;
/*     */         } 
/*     */       } 
/* 301 */       j += Character.charCount(k);
/*     */     } 
/* 303 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String escapeJavaScript(String paramString) {
/* 313 */     if (paramString == null) {
/* 314 */       return null;
/*     */     }
/* 316 */     int i = paramString.length();
/* 317 */     if (i == 0) {
/* 318 */       return "";
/*     */     }
/* 320 */     StringBuilder stringBuilder = new StringBuilder(i);
/* 321 */     for (byte b = 0; b < i; b++) {
/* 322 */       char c = paramString.charAt(b);
/* 323 */       switch (c) {
/*     */         case '"':
/* 325 */           stringBuilder.append("\\\"");
/*     */           break;
/*     */         case '\'':
/* 328 */           stringBuilder.append("\\'");
/*     */           break;
/*     */         case '\\':
/* 331 */           stringBuilder.append("\\\\");
/*     */           break;
/*     */         case '\n':
/* 334 */           stringBuilder.append("\\n");
/*     */           break;
/*     */         case '\r':
/* 337 */           stringBuilder.append("\\r");
/*     */           break;
/*     */         case '\t':
/* 340 */           stringBuilder.append("\\t");
/*     */           break;
/*     */         default:
/* 343 */           stringBuilder.append(c);
/*     */           break;
/*     */       } 
/*     */     } 
/* 347 */     return stringBuilder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\server\web\PageParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */