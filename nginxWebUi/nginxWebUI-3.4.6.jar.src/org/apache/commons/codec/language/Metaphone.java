/*     */ package org.apache.commons.codec.language;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ import org.apache.commons.codec.StringEncoder;
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
/*     */ public class Metaphone
/*     */   implements StringEncoder
/*     */ {
/*     */   private static final String VOWELS = "AEIOU";
/*     */   private static final String FRONTV = "EIY";
/*     */   private static final String VARSON = "CSPTG";
/*  72 */   private int maxCodeLen = 4;
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
/*     */   public String metaphone(String txt) {
/*  92 */     boolean hard = false;
/*     */     int txtLength;
/*  94 */     if (txt == null || (txtLength = txt.length()) == 0) {
/*  95 */       return "";
/*     */     }
/*     */     
/*  98 */     if (txtLength == 1) {
/*  99 */       return txt.toUpperCase(Locale.ENGLISH);
/*     */     }
/*     */     
/* 102 */     char[] inwd = txt.toUpperCase(Locale.ENGLISH).toCharArray();
/*     */     
/* 104 */     StringBuilder local = new StringBuilder(40);
/* 105 */     StringBuilder code = new StringBuilder(10);
/*     */     
/* 107 */     switch (inwd[0]) {
/*     */       case 'G':
/*     */       case 'K':
/*     */       case 'P':
/* 111 */         if (inwd[1] == 'N') {
/* 112 */           local.append(inwd, 1, inwd.length - 1); break;
/*     */         } 
/* 114 */         local.append(inwd);
/*     */         break;
/*     */       
/*     */       case 'A':
/* 118 */         if (inwd[1] == 'E') {
/* 119 */           local.append(inwd, 1, inwd.length - 1); break;
/*     */         } 
/* 121 */         local.append(inwd);
/*     */         break;
/*     */       
/*     */       case 'W':
/* 125 */         if (inwd[1] == 'R') {
/* 126 */           local.append(inwd, 1, inwd.length - 1);
/*     */           break;
/*     */         } 
/* 129 */         if (inwd[1] == 'H') {
/* 130 */           local.append(inwd, 1, inwd.length - 1);
/* 131 */           local.setCharAt(0, 'W'); break;
/*     */         } 
/* 133 */         local.append(inwd);
/*     */         break;
/*     */       
/*     */       case 'X':
/* 137 */         inwd[0] = 'S';
/* 138 */         local.append(inwd);
/*     */         break;
/*     */       default:
/* 141 */         local.append(inwd);
/*     */         break;
/*     */     } 
/* 144 */     int wdsz = local.length();
/* 145 */     int n = 0;
/*     */     
/* 147 */     while (code.length() < getMaxCodeLen() && n < wdsz) {
/*     */       
/* 149 */       char symb = local.charAt(n);
/*     */       
/* 151 */       if (symb != 'C' && isPreviousChar(local, n, symb)) {
/* 152 */         n++;
/*     */       } else {
/* 154 */         switch (symb) {
/*     */           case 'A':
/*     */           case 'E':
/*     */           case 'I':
/*     */           case 'O':
/*     */           case 'U':
/* 160 */             if (n == 0) {
/* 161 */               code.append(symb);
/*     */             }
/*     */             break;
/*     */           case 'B':
/* 165 */             if (isPreviousChar(local, n, 'M') && 
/* 166 */               isLastChar(wdsz, n)) {
/*     */               break;
/*     */             }
/* 169 */             code.append(symb);
/*     */             break;
/*     */           
/*     */           case 'C':
/* 173 */             if (isPreviousChar(local, n, 'S') && 
/* 174 */               !isLastChar(wdsz, n) && "EIY"
/* 175 */               .indexOf(local.charAt(n + 1)) >= 0) {
/*     */               break;
/*     */             }
/* 178 */             if (regionMatch(local, n, "CIA")) {
/* 179 */               code.append('X');
/*     */               break;
/*     */             } 
/* 182 */             if (!isLastChar(wdsz, n) && "EIY"
/* 183 */               .indexOf(local.charAt(n + 1)) >= 0) {
/* 184 */               code.append('S');
/*     */               break;
/*     */             } 
/* 187 */             if (isPreviousChar(local, n, 'S') && 
/* 188 */               isNextChar(local, n, 'H')) {
/* 189 */               code.append('K');
/*     */               break;
/*     */             } 
/* 192 */             if (isNextChar(local, n, 'H')) {
/* 193 */               if (n == 0 && wdsz >= 3 && 
/*     */                 
/* 195 */                 isVowel(local, 2)) {
/* 196 */                 code.append('K'); break;
/*     */               } 
/* 198 */               code.append('X');
/*     */               break;
/*     */             } 
/* 201 */             code.append('K');
/*     */             break;
/*     */           
/*     */           case 'D':
/* 205 */             if (!isLastChar(wdsz, n + 1) && 
/* 206 */               isNextChar(local, n, 'G') && "EIY"
/* 207 */               .indexOf(local.charAt(n + 2)) >= 0) {
/* 208 */               code.append('J'); n += 2; break;
/*     */             } 
/* 210 */             code.append('T');
/*     */             break;
/*     */           
/*     */           case 'G':
/* 214 */             if (isLastChar(wdsz, n + 1) && 
/* 215 */               isNextChar(local, n, 'H')) {
/*     */               break;
/*     */             }
/* 218 */             if (!isLastChar(wdsz, n + 1) && 
/* 219 */               isNextChar(local, n, 'H') && 
/* 220 */               !isVowel(local, n + 2)) {
/*     */               break;
/*     */             }
/* 223 */             if (n > 0 && (
/* 224 */               regionMatch(local, n, "GN") || 
/* 225 */               regionMatch(local, n, "GNED"))) {
/*     */               break;
/*     */             }
/* 228 */             if (isPreviousChar(local, n, 'G')) {
/*     */               
/* 230 */               hard = true;
/*     */             } else {
/* 232 */               hard = false;
/*     */             } 
/* 234 */             if (!isLastChar(wdsz, n) && "EIY"
/* 235 */               .indexOf(local.charAt(n + 1)) >= 0 && !hard) {
/*     */               
/* 237 */               code.append('J'); break;
/*     */             } 
/* 239 */             code.append('K');
/*     */             break;
/*     */           
/*     */           case 'H':
/* 243 */             if (isLastChar(wdsz, n)) {
/*     */               break;
/*     */             }
/* 246 */             if (n > 0 && "CSPTG"
/* 247 */               .indexOf(local.charAt(n - 1)) >= 0) {
/*     */               break;
/*     */             }
/* 250 */             if (isVowel(local, n + 1)) {
/* 251 */               code.append('H');
/*     */             }
/*     */             break;
/*     */           case 'F':
/*     */           case 'J':
/*     */           case 'L':
/*     */           case 'M':
/*     */           case 'N':
/*     */           case 'R':
/* 260 */             code.append(symb);
/*     */             break;
/*     */           case 'K':
/* 263 */             if (n > 0) {
/* 264 */               if (!isPreviousChar(local, n, 'C'))
/* 265 */                 code.append(symb); 
/*     */               break;
/*     */             } 
/* 268 */             code.append(symb);
/*     */             break;
/*     */           
/*     */           case 'P':
/* 272 */             if (isNextChar(local, n, 'H')) {
/*     */               
/* 274 */               code.append('F'); break;
/*     */             } 
/* 276 */             code.append(symb);
/*     */             break;
/*     */           
/*     */           case 'Q':
/* 280 */             code.append('K');
/*     */             break;
/*     */           case 'S':
/* 283 */             if (regionMatch(local, n, "SH") || 
/* 284 */               regionMatch(local, n, "SIO") || 
/* 285 */               regionMatch(local, n, "SIA")) {
/* 286 */               code.append('X'); break;
/*     */             } 
/* 288 */             code.append('S');
/*     */             break;
/*     */           
/*     */           case 'T':
/* 292 */             if (regionMatch(local, n, "TIA") || 
/* 293 */               regionMatch(local, n, "TIO")) {
/* 294 */               code.append('X');
/*     */               break;
/*     */             } 
/* 297 */             if (regionMatch(local, n, "TCH")) {
/*     */               break;
/*     */             }
/*     */ 
/*     */             
/* 302 */             if (regionMatch(local, n, "TH")) {
/* 303 */               code.append('0'); break;
/*     */             } 
/* 305 */             code.append('T');
/*     */             break;
/*     */           
/*     */           case 'V':
/* 309 */             code.append('F'); break;
/*     */           case 'W':
/*     */           case 'Y':
/* 312 */             if (!isLastChar(wdsz, n) && 
/* 313 */               isVowel(local, n + 1)) {
/* 314 */               code.append(symb);
/*     */             }
/*     */             break;
/*     */           case 'X':
/* 318 */             code.append('K');
/* 319 */             code.append('S');
/*     */             break;
/*     */           case 'Z':
/* 322 */             code.append('S');
/*     */             break;
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 328 */         n++;
/*     */       } 
/* 330 */       if (code.length() > getMaxCodeLen()) {
/* 331 */         code.setLength(getMaxCodeLen());
/*     */       }
/*     */     } 
/* 334 */     return code.toString();
/*     */   }
/*     */   
/*     */   private boolean isVowel(StringBuilder string, int index) {
/* 338 */     return ("AEIOU".indexOf(string.charAt(index)) >= 0);
/*     */   }
/*     */   
/*     */   private boolean isPreviousChar(StringBuilder string, int index, char c) {
/* 342 */     boolean matches = false;
/* 343 */     if (index > 0 && index < string
/* 344 */       .length()) {
/* 345 */       matches = (string.charAt(index - 1) == c);
/*     */     }
/* 347 */     return matches;
/*     */   }
/*     */   
/*     */   private boolean isNextChar(StringBuilder string, int index, char c) {
/* 351 */     boolean matches = false;
/* 352 */     if (index >= 0 && index < string
/* 353 */       .length() - 1) {
/* 354 */       matches = (string.charAt(index + 1) == c);
/*     */     }
/* 356 */     return matches;
/*     */   }
/*     */   
/*     */   private boolean regionMatch(StringBuilder string, int index, String test) {
/* 360 */     boolean matches = false;
/* 361 */     if (index >= 0 && index + test
/* 362 */       .length() - 1 < string.length()) {
/* 363 */       String substring = string.substring(index, index + test.length());
/* 364 */       matches = substring.equals(test);
/*     */     } 
/* 366 */     return matches;
/*     */   }
/*     */   
/*     */   private boolean isLastChar(int wdsz, int n) {
/* 370 */     return (n + 1 == wdsz);
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
/*     */   public Object encode(Object obj) throws EncoderException {
/* 388 */     if (!(obj instanceof String)) {
/* 389 */       throw new EncoderException("Parameter supplied to Metaphone encode is not of type java.lang.String");
/*     */     }
/* 391 */     return metaphone((String)obj);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String encode(String str) {
/* 402 */     return metaphone(str);
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
/*     */   public boolean isMetaphoneEqual(String str1, String str2) {
/* 414 */     return metaphone(str1).equals(metaphone(str2));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxCodeLen() {
/* 421 */     return this.maxCodeLen;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxCodeLen(int maxCodeLen) {
/* 427 */     this.maxCodeLen = maxCodeLen;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\language\Metaphone.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */