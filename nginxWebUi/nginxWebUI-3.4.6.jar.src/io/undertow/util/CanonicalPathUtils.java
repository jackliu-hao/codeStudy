/*     */ package io.undertow.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CanonicalPathUtils
/*     */ {
/*  32 */   private static final boolean DONT_CANONICALIZE_BACKSLASH = Boolean.parseBoolean("io.undertow.DONT_CANONICALIZE_BACKSLASH"); static final int START = -1; static final int NORMAL = 0;
/*     */   static final int FIRST_SLASH = 1;
/*     */   
/*     */   public static String canonicalize(String path) {
/*  36 */     return canonicalize(path, false);
/*     */   }
/*     */   static final int ONE_DOT = 2; static final int TWO_DOT = 3; static final int FIRST_BACKSLASH = 4;
/*     */   public static String canonicalize(String path, boolean nullAllowed) {
/*  40 */     int state = -1;
/*  41 */     for (int i = path.length() - 1; i >= 0; i--) {
/*  42 */       char c = path.charAt(i);
/*  43 */       switch (c) {
/*     */         case '/':
/*  45 */           if (state == 1)
/*  46 */             return realCanonicalize(path, i + 1, 1, nullAllowed); 
/*  47 */           if (state == 2)
/*  48 */             return realCanonicalize(path, i + 2, 1, nullAllowed); 
/*  49 */           if (state == 3) {
/*  50 */             return realCanonicalize(path, i + 3, 1, nullAllowed);
/*     */           }
/*  52 */           state = 1;
/*     */           break;
/*     */         case '.':
/*  55 */           if (state == 1 || state == -1 || state == 4) {
/*  56 */             state = 2; break;
/*  57 */           }  if (state == 2) {
/*  58 */             state = 3; break;
/*     */           } 
/*  60 */           state = 0;
/*     */           break;
/*     */         
/*     */         case '\\':
/*  64 */           if (!DONT_CANONICALIZE_BACKSLASH) {
/*  65 */             if (state == 4)
/*  66 */               return realCanonicalize(path, i + 1, 4, nullAllowed); 
/*  67 */             if (state == 2)
/*  68 */               return realCanonicalize(path, i + 2, 4, nullAllowed); 
/*  69 */             if (state == 3) {
/*  70 */               return realCanonicalize(path, i + 3, 4, nullAllowed);
/*     */             }
/*  72 */             state = 4;
/*     */             break;
/*     */           } 
/*     */         
/*     */         default:
/*  77 */           state = 0;
/*     */           break;
/*     */       } 
/*     */     } 
/*  81 */     return path;
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
/*     */   private static String realCanonicalize(String path, int lastDot, int initialState, boolean nullAllowed) {
/*  93 */     int state = initialState;
/*  94 */     int eatCount = 0;
/*  95 */     int tokenEnd = path.length();
/*  96 */     List<String> parts = new ArrayList<>();
/*  97 */     for (int i = lastDot - 1; i >= 0; i--) {
/*  98 */       char c = path.charAt(i);
/*  99 */       switch (state) {
/*     */         
/*     */         case 0:
/* 102 */           if (c == '/') {
/* 103 */             state = 1;
/* 104 */             if (eatCount > 0) {
/* 105 */               eatCount--;
/* 106 */               tokenEnd = i;
/*     */             }  break;
/* 108 */           }  if (c == '\\' && !DONT_CANONICALIZE_BACKSLASH) {
/* 109 */             state = 4;
/* 110 */             if (eatCount > 0) {
/* 111 */               eatCount--;
/* 112 */               tokenEnd = i;
/*     */             } 
/*     */           } 
/*     */           break;
/*     */         
/*     */         case 1:
/* 118 */           if (c == '.') {
/* 119 */             state = 2; break;
/* 120 */           }  if (c == '/') {
/* 121 */             if (eatCount > 0) {
/* 122 */               eatCount--;
/* 123 */               tokenEnd = i; break;
/*     */             } 
/* 125 */             parts.add(path.substring(i + 1, tokenEnd));
/* 126 */             tokenEnd = i;
/*     */             break;
/*     */           } 
/* 129 */           state = 0;
/*     */           break;
/*     */ 
/*     */         
/*     */         case 4:
/* 134 */           if (c == '.') {
/* 135 */             state = 2; break;
/* 136 */           }  if (c == '\\') {
/* 137 */             if (eatCount > 0) {
/* 138 */               eatCount--;
/* 139 */               tokenEnd = i; break;
/*     */             } 
/* 141 */             parts.add(path.substring(i + 1, tokenEnd));
/* 142 */             tokenEnd = i;
/*     */             break;
/*     */           } 
/* 145 */           state = 0;
/*     */           break;
/*     */ 
/*     */         
/*     */         case 2:
/* 150 */           if (c == '.') {
/* 151 */             state = 3; break;
/* 152 */           }  if (c == '/' || (c == '\\' && !DONT_CANONICALIZE_BACKSLASH)) {
/* 153 */             if (i + 2 != tokenEnd) {
/* 154 */               parts.add(path.substring(i + 2, tokenEnd));
/*     */             }
/* 156 */             tokenEnd = i;
/* 157 */             state = (c == '/') ? 1 : 4; break;
/*     */           } 
/* 159 */           state = 0;
/*     */           break;
/*     */ 
/*     */         
/*     */         case 3:
/* 164 */           if (c == '/' || (c == '\\' && !DONT_CANONICALIZE_BACKSLASH)) {
/* 165 */             if (i + 3 != tokenEnd) {
/* 166 */               parts.add(path.substring(i + 3, tokenEnd));
/*     */             }
/* 168 */             tokenEnd = i;
/* 169 */             eatCount++;
/* 170 */             state = (c == '/') ? 1 : 4; break;
/*     */           } 
/* 172 */           state = 0;
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 177 */     if (eatCount > 0 && nullAllowed)
/*     */     {
/* 179 */       return null;
/*     */     }
/* 181 */     StringBuilder result = new StringBuilder();
/* 182 */     if (tokenEnd != 0) {
/* 183 */       result.append(path.substring(0, tokenEnd));
/*     */     }
/* 185 */     for (int j = parts.size() - 1; j >= 0; j--) {
/* 186 */       result.append(parts.get(j));
/*     */     }
/* 188 */     if (result.length() == 0) {
/* 189 */       return "/";
/*     */     }
/* 191 */     return result.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\CanonicalPathUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */