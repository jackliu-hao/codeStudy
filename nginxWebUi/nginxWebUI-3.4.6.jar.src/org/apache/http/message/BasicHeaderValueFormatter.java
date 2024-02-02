/*     */ package org.apache.http.message;
/*     */ 
/*     */ import org.apache.http.HeaderElement;
/*     */ import org.apache.http.NameValuePair;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.CharArrayBuffer;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class BasicHeaderValueFormatter
/*     */   implements HeaderValueFormatter
/*     */ {
/*     */   @Deprecated
/*  56 */   public static final BasicHeaderValueFormatter DEFAULT = new BasicHeaderValueFormatter();
/*     */   
/*  58 */   public static final BasicHeaderValueFormatter INSTANCE = new BasicHeaderValueFormatter();
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
/*     */   public static final String SEPARATORS = " ;,:@()<>\\\"/[]?={}\t";
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
/*     */   public static final String UNSAFE_CHARS = "\"\\";
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
/*     */   public static String formatElements(HeaderElement[] elems, boolean quote, HeaderValueFormatter formatter) {
/*  92 */     return ((formatter != null) ? formatter : INSTANCE).formatElements(null, elems, quote).toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CharArrayBuffer formatElements(CharArrayBuffer charBuffer, HeaderElement[] elems, boolean quote) {
/* 102 */     Args.notNull(elems, "Header element array");
/* 103 */     int len = estimateElementsLen(elems);
/* 104 */     CharArrayBuffer buffer = charBuffer;
/* 105 */     if (buffer == null) {
/* 106 */       buffer = new CharArrayBuffer(len);
/*     */     } else {
/* 108 */       buffer.ensureCapacity(len);
/*     */     } 
/*     */     
/* 111 */     for (int i = 0; i < elems.length; i++) {
/* 112 */       if (i > 0) {
/* 113 */         buffer.append(", ");
/*     */       }
/* 115 */       formatHeaderElement(buffer, elems[i], quote);
/*     */     } 
/*     */     
/* 118 */     return buffer;
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
/*     */   protected int estimateElementsLen(HeaderElement[] elems) {
/* 130 */     if (elems == null || elems.length < 1) {
/* 131 */       return 0;
/*     */     }
/*     */     
/* 134 */     int result = (elems.length - 1) * 2;
/* 135 */     for (HeaderElement elem : elems) {
/* 136 */       result += estimateHeaderElementLen(elem);
/*     */     }
/*     */     
/* 139 */     return result;
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
/*     */   public static String formatHeaderElement(HeaderElement elem, boolean quote, HeaderValueFormatter formatter) {
/* 159 */     return ((formatter != null) ? formatter : INSTANCE).formatHeaderElement(null, elem, quote).toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CharArrayBuffer formatHeaderElement(CharArrayBuffer charBuffer, HeaderElement elem, boolean quote) {
/* 169 */     Args.notNull(elem, "Header element");
/* 170 */     int len = estimateHeaderElementLen(elem);
/* 171 */     CharArrayBuffer buffer = charBuffer;
/* 172 */     if (buffer == null) {
/* 173 */       buffer = new CharArrayBuffer(len);
/*     */     } else {
/* 175 */       buffer.ensureCapacity(len);
/*     */     } 
/*     */     
/* 178 */     buffer.append(elem.getName());
/* 179 */     String value = elem.getValue();
/* 180 */     if (value != null) {
/* 181 */       buffer.append('=');
/* 182 */       doFormatValue(buffer, value, quote);
/*     */     } 
/*     */     
/* 185 */     int parcnt = elem.getParameterCount();
/* 186 */     if (parcnt > 0) {
/* 187 */       for (int i = 0; i < parcnt; i++) {
/* 188 */         buffer.append("; ");
/* 189 */         formatNameValuePair(buffer, elem.getParameter(i), quote);
/*     */       } 
/*     */     }
/*     */     
/* 193 */     return buffer;
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
/*     */   protected int estimateHeaderElementLen(HeaderElement elem) {
/* 205 */     if (elem == null) {
/* 206 */       return 0;
/*     */     }
/*     */     
/* 209 */     int result = elem.getName().length();
/* 210 */     String value = elem.getValue();
/* 211 */     if (value != null)
/*     */     {
/* 213 */       result += 3 + value.length();
/*     */     }
/*     */     
/* 216 */     int parcnt = elem.getParameterCount();
/* 217 */     if (parcnt > 0) {
/* 218 */       for (int i = 0; i < parcnt; i++) {
/* 219 */         result += 2 + estimateNameValuePairLen(elem.getParameter(i));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/* 224 */     return result;
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
/*     */   public static String formatParameters(NameValuePair[] nvps, boolean quote, HeaderValueFormatter formatter) {
/* 245 */     return ((formatter != null) ? formatter : INSTANCE).formatParameters(null, nvps, quote).toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CharArrayBuffer formatParameters(CharArrayBuffer charBuffer, NameValuePair[] nvps, boolean quote) {
/* 255 */     Args.notNull(nvps, "Header parameter array");
/* 256 */     int len = estimateParametersLen(nvps);
/* 257 */     CharArrayBuffer buffer = charBuffer;
/* 258 */     if (buffer == null) {
/* 259 */       buffer = new CharArrayBuffer(len);
/*     */     } else {
/* 261 */       buffer.ensureCapacity(len);
/*     */     } 
/*     */     
/* 264 */     for (int i = 0; i < nvps.length; i++) {
/* 265 */       if (i > 0) {
/* 266 */         buffer.append("; ");
/*     */       }
/* 268 */       formatNameValuePair(buffer, nvps[i], quote);
/*     */     } 
/*     */     
/* 271 */     return buffer;
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
/*     */   protected int estimateParametersLen(NameValuePair[] nvps) {
/* 283 */     if (nvps == null || nvps.length < 1) {
/* 284 */       return 0;
/*     */     }
/*     */     
/* 287 */     int result = (nvps.length - 1) * 2;
/* 288 */     for (NameValuePair nvp : nvps) {
/* 289 */       result += estimateNameValuePairLen(nvp);
/*     */     }
/*     */     
/* 292 */     return result;
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
/*     */   public static String formatNameValuePair(NameValuePair nvp, boolean quote, HeaderValueFormatter formatter) {
/* 311 */     return ((formatter != null) ? formatter : INSTANCE).formatNameValuePair(null, nvp, quote).toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CharArrayBuffer formatNameValuePair(CharArrayBuffer charBuffer, NameValuePair nvp, boolean quote) {
/* 321 */     Args.notNull(nvp, "Name / value pair");
/* 322 */     int len = estimateNameValuePairLen(nvp);
/* 323 */     CharArrayBuffer buffer = charBuffer;
/* 324 */     if (buffer == null) {
/* 325 */       buffer = new CharArrayBuffer(len);
/*     */     } else {
/* 327 */       buffer.ensureCapacity(len);
/*     */     } 
/*     */     
/* 330 */     buffer.append(nvp.getName());
/* 331 */     String value = nvp.getValue();
/* 332 */     if (value != null) {
/* 333 */       buffer.append('=');
/* 334 */       doFormatValue(buffer, value, quote);
/*     */     } 
/*     */     
/* 337 */     return buffer;
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
/*     */   protected int estimateNameValuePairLen(NameValuePair nvp) {
/* 349 */     if (nvp == null) {
/* 350 */       return 0;
/*     */     }
/*     */     
/* 353 */     int result = nvp.getName().length();
/* 354 */     String value = nvp.getValue();
/* 355 */     if (value != null)
/*     */     {
/* 357 */       result += 3 + value.length();
/*     */     }
/* 359 */     return result;
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
/*     */   protected void doFormatValue(CharArrayBuffer buffer, String value, boolean quote) {
/* 377 */     boolean quoteFlag = quote;
/* 378 */     if (!quoteFlag) {
/* 379 */       for (int j = 0; j < value.length() && !quoteFlag; j++) {
/* 380 */         quoteFlag = isSeparator(value.charAt(j));
/*     */       }
/*     */     }
/*     */     
/* 384 */     if (quoteFlag) {
/* 385 */       buffer.append('"');
/*     */     }
/* 387 */     for (int i = 0; i < value.length(); i++) {
/* 388 */       char ch = value.charAt(i);
/* 389 */       if (isUnsafe(ch)) {
/* 390 */         buffer.append('\\');
/*     */       }
/* 392 */       buffer.append(ch);
/*     */     } 
/* 394 */     if (quoteFlag) {
/* 395 */       buffer.append('"');
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
/*     */   protected boolean isSeparator(char ch) {
/* 409 */     return (" ;,:@()<>\\\"/[]?={}\t".indexOf(ch) >= 0);
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
/*     */   protected boolean isUnsafe(char ch) {
/* 422 */     return ("\"\\".indexOf(ch) >= 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\BasicHeaderValueFormatter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */