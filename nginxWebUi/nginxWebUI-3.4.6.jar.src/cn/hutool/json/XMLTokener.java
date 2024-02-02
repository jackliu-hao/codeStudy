/*     */ package cn.hutool.json;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class XMLTokener
/*     */   extends JSONTokener
/*     */ {
/*  17 */   public static final HashMap<String, Character> entity = new HashMap<>(8); static {
/*  18 */     entity.put("amp", XML.AMP);
/*  19 */     entity.put("apos", XML.APOS);
/*  20 */     entity.put("gt", XML.GT);
/*  21 */     entity.put("lt", XML.LT);
/*  22 */     entity.put("quot", XML.QUOT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XMLTokener(CharSequence s, JSONConfig config) {
/*  32 */     super(s, config);
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
/*     */   public String nextCDATA() throws JSONException {
/*  44 */     StringBuilder sb = new StringBuilder();
/*     */     while (true) {
/*  46 */       char c = next();
/*  47 */       if (end()) {
/*  48 */         throw syntaxError("Unclosed CDATA");
/*     */       }
/*  50 */       sb.append(c);
/*  51 */       int i = sb.length() - 3;
/*  52 */       if (i >= 0 && sb.charAt(i) == ']' && sb.charAt(i + 1) == ']' && sb.charAt(i + 2) == '>') {
/*  53 */         sb.setLength(i);
/*  54 */         return sb.toString();
/*     */       } 
/*     */     } 
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
/*     */   public Object nextContent() throws JSONException {
/*     */     while (true) {
/*  70 */       char c = next();
/*  71 */       if (!Character.isWhitespace(c)) {
/*  72 */         if (c == '\000') {
/*  73 */           return null;
/*     */         }
/*  75 */         if (c == '<') {
/*  76 */           return XML.LT;
/*     */         }
/*  78 */         StringBuilder sb = new StringBuilder();
/*     */         while (true) {
/*  80 */           if (c == '<' || c == '\000') {
/*  81 */             back();
/*  82 */             return sb.toString().trim();
/*     */           } 
/*  84 */           if (c == '&') {
/*  85 */             sb.append(nextEntity(c));
/*     */           } else {
/*  87 */             sb.append(c);
/*     */           } 
/*  89 */           c = next();
/*     */         } 
/*     */         break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object nextEntity(char ampersand) throws JSONException {
/*     */     char c;
/* 101 */     StringBuilder sb = new StringBuilder();
/*     */     while (true) {
/* 103 */       c = next();
/* 104 */       if (Character.isLetterOrDigit(c) || c == '#')
/* 105 */       { sb.append(Character.toLowerCase(c)); continue; }  break;
/* 106 */     }  if (c == ';') {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 112 */       String string = sb.toString();
/* 113 */       Object object = entity.get(string);
/* 114 */       return (object != null) ? object : (ampersand + string + ";");
/*     */     } 
/*     */     throw syntaxError("Missing ';' in XML entity: &" + sb);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object nextMeta() throws JSONException {
/*     */     char c, q;
/*     */     do {
/* 127 */       c = next();
/* 128 */     } while (Character.isWhitespace(c));
/* 129 */     switch (c) {
/*     */       case '\000':
/* 131 */         throw syntaxError("Misshaped meta tag");
/*     */       case '<':
/* 133 */         return XML.LT;
/*     */       case '>':
/* 135 */         return XML.GT;
/*     */       case '/':
/* 137 */         return XML.SLASH;
/*     */       case '=':
/* 139 */         return XML.EQ;
/*     */       case '!':
/* 141 */         return XML.BANG;
/*     */       case '?':
/* 143 */         return XML.QUEST;
/*     */       case '"':
/*     */       case '\'':
/* 146 */         q = c;
/*     */         while (true) {
/* 148 */           c = next();
/* 149 */           if (c == '\000') {
/* 150 */             throw syntaxError("Unterminated string");
/*     */           }
/* 152 */           if (c == q) {
/* 153 */             return Boolean.TRUE;
/*     */           }
/*     */         } 
/*     */     } 
/*     */     while (true) {
/* 158 */       c = next();
/* 159 */       if (Character.isWhitespace(c)) {
/* 160 */         return Boolean.TRUE;
/*     */       }
/* 162 */       switch (c) { case '\000':
/*     */         case '!':
/*     */         case '"':
/*     */         case '\'':
/*     */         case '/':
/*     */         case '<':
/*     */         case '=':
/*     */         case '>':
/*     */         case '?':
/*     */           break; } 
/* 172 */     }  back();
/* 173 */     return Boolean.TRUE;
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
/*     */   public Object nextToken() throws JSONException {
/*     */     char c, q;
/*     */     do {
/* 192 */       c = next();
/* 193 */     } while (Character.isWhitespace(c));
/* 194 */     switch (c) {
/*     */       case '\000':
/* 196 */         throw syntaxError("Misshaped element");
/*     */       case '<':
/* 198 */         throw syntaxError("Misplaced '<'");
/*     */       case '>':
/* 200 */         return XML.GT;
/*     */       case '/':
/* 202 */         return XML.SLASH;
/*     */       case '=':
/* 204 */         return XML.EQ;
/*     */       case '!':
/* 206 */         return XML.BANG;
/*     */       case '?':
/* 208 */         return XML.QUEST;
/*     */ 
/*     */ 
/*     */       
/*     */       case '"':
/*     */       case '\'':
/* 214 */         q = c;
/* 215 */         sb = new StringBuilder();
/*     */         while (true) {
/* 217 */           c = next();
/* 218 */           if (c == '\000') {
/* 219 */             throw syntaxError("Unterminated string");
/*     */           }
/* 221 */           if (c == q) {
/* 222 */             return sb.toString();
/*     */           }
/* 224 */           if (c == '&') {
/* 225 */             sb.append(nextEntity(c)); continue;
/*     */           } 
/* 227 */           sb.append(c);
/*     */         } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 234 */     StringBuilder sb = new StringBuilder();
/*     */     while (true)
/* 236 */     { sb.append(c);
/* 237 */       c = next();
/* 238 */       if (Character.isWhitespace(c)) {
/* 239 */         return sb.toString();
/*     */       }
/* 241 */       switch (c)
/*     */       { case '\000':
/* 243 */           return sb.toString();
/*     */         case '!':
/*     */         case '/':
/*     */         case '=':
/*     */         case '>':
/*     */         case '?':
/*     */         case '[':
/*     */         case ']':
/* 251 */           back();
/* 252 */           return sb.toString();
/*     */         case '"':
/*     */         case '\'':
/*     */         case '<':
/* 256 */           break; }  }  throw syntaxError("Bad character in a name");
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
/*     */   public boolean skipPast(String to) throws JSONException {
/* 274 */     int offset = 0;
/* 275 */     int length = to.length();
/* 276 */     char[] circle = new char[length];
/*     */ 
/*     */     
/*     */     int i;
/*     */ 
/*     */     
/* 282 */     for (i = 0; i < length; i++) {
/* 283 */       char c = next();
/* 284 */       if (c == '\000') {
/* 285 */         return false;
/*     */       }
/* 287 */       circle[i] = c;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*     */     while (true) {
/* 293 */       int j = offset;
/* 294 */       boolean b = true;
/*     */ 
/*     */ 
/*     */       
/* 298 */       for (i = 0; i < length; i++) {
/* 299 */         if (circle[j] != to.charAt(i)) {
/* 300 */           b = false;
/*     */           break;
/*     */         } 
/* 303 */         j++;
/* 304 */         if (j >= length) {
/* 305 */           j -= length;
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 311 */       if (b) {
/* 312 */         return true;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 317 */       char c = next();
/* 318 */       if (c == '\000') {
/* 319 */         return false;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 324 */       circle[offset] = c;
/* 325 */       offset++;
/* 326 */       if (offset >= length)
/* 327 */         offset -= length; 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\XMLTokener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */