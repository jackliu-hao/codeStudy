/*     */ package org.apache.http.client.utils;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.net.URI;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.BitSet;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Scanner;
/*     */ import org.apache.http.Consts;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HeaderElement;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.NameValuePair;
/*     */ import org.apache.http.entity.ContentType;
/*     */ import org.apache.http.message.BasicNameValuePair;
/*     */ import org.apache.http.message.ParserCursor;
/*     */ import org.apache.http.message.TokenParser;
/*     */ import org.apache.http.protocol.HTTP;
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
/*     */ public class URLEncodedUtils
/*     */ {
/*     */   public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
/*     */   private static final char QP_SEP_A = '&';
/*     */   private static final char QP_SEP_S = ';';
/*     */   private static final String NAME_VALUE_SEPARATOR = "=";
/*     */   private static final char PATH_SEPARATOR = '/';
/*  75 */   private static final BitSet PATH_SEPARATORS = new BitSet(256);
/*     */   static {
/*  77 */     PATH_SEPARATORS.set(47);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static List<NameValuePair> parse(URI uri, String charsetName) {
/*  85 */     return parse(uri, (charsetName != null) ? Charset.forName(charsetName) : null);
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
/*     */   public static List<NameValuePair> parse(URI uri, Charset charset) {
/*  99 */     Args.notNull(uri, "URI");
/* 100 */     String query = uri.getRawQuery();
/* 101 */     if (query != null && !query.isEmpty()) {
/* 102 */       return parse(query, charset);
/*     */     }
/* 104 */     return createEmptyList();
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
/*     */   public static List<NameValuePair> parse(HttpEntity entity) throws IOException {
/*     */     CharArrayBuffer buf;
/* 121 */     Args.notNull(entity, "HTTP entity");
/* 122 */     ContentType contentType = ContentType.get(entity);
/* 123 */     if (contentType == null || !contentType.getMimeType().equalsIgnoreCase("application/x-www-form-urlencoded")) {
/* 124 */       return createEmptyList();
/*     */     }
/* 126 */     long len = entity.getContentLength();
/* 127 */     Args.check((len <= 2147483647L), "HTTP entity is too large");
/* 128 */     Charset charset = (contentType.getCharset() != null) ? contentType.getCharset() : HTTP.DEF_CONTENT_CHARSET;
/* 129 */     InputStream inStream = entity.getContent();
/* 130 */     if (inStream == null) {
/* 131 */       return createEmptyList();
/*     */     }
/*     */     
/*     */     try {
/* 135 */       buf = new CharArrayBuffer((len > 0L) ? (int)len : 1024);
/* 136 */       Reader reader = new InputStreamReader(inStream, charset);
/* 137 */       char[] tmp = new char[1024];
/*     */       int l;
/* 139 */       while ((l = reader.read(tmp)) != -1) {
/* 140 */         buf.append(tmp, 0, l);
/*     */       }
/*     */     } finally {
/*     */       
/* 144 */       inStream.close();
/*     */     } 
/* 146 */     if (buf.isEmpty()) {
/* 147 */       return createEmptyList();
/*     */     }
/* 149 */     return parse(buf, charset, new char[] { '&' });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isEncoded(HttpEntity entity) {
/* 157 */     Args.notNull(entity, "HTTP entity");
/* 158 */     Header h = entity.getContentType();
/* 159 */     if (h != null) {
/* 160 */       HeaderElement[] elems = h.getElements();
/* 161 */       if (elems.length > 0) {
/* 162 */         String contentType = elems[0].getName();
/* 163 */         return contentType.equalsIgnoreCase("application/x-www-form-urlencoded");
/*     */       } 
/*     */     } 
/* 166 */     return false;
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
/*     */   @Deprecated
/*     */   public static void parse(List<NameValuePair> parameters, Scanner scanner, String charset) {
/* 189 */     parse(parameters, scanner, "[&;]", charset);
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
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void parse(List<NameValuePair> parameters, Scanner scanner, String parameterSepartorPattern, String charset) {
/* 216 */     scanner.useDelimiter(parameterSepartorPattern);
/* 217 */     while (scanner.hasNext()) {
/*     */ 
/*     */       
/* 220 */       String name, value, token = scanner.next();
/* 221 */       int i = token.indexOf("=");
/* 222 */       if (i != -1) {
/* 223 */         name = decodeFormFields(token.substring(0, i).trim(), charset);
/* 224 */         value = decodeFormFields(token.substring(i + 1).trim(), charset);
/*     */       } else {
/* 226 */         name = decodeFormFields(token.trim(), charset);
/* 227 */         value = null;
/*     */       } 
/* 229 */       parameters.add(new BasicNameValuePair(name, value));
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
/*     */   
/*     */   public static List<NameValuePair> parse(String s, Charset charset) {
/* 244 */     if (s == null) {
/* 245 */       return createEmptyList();
/*     */     }
/* 247 */     CharArrayBuffer buffer = new CharArrayBuffer(s.length());
/* 248 */     buffer.append(s);
/* 249 */     return parse(buffer, charset, new char[] { '&', ';' });
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
/*     */   public static List<NameValuePair> parse(String s, Charset charset, char... separators) {
/* 264 */     if (s == null) {
/* 265 */       return createEmptyList();
/*     */     }
/* 267 */     CharArrayBuffer buffer = new CharArrayBuffer(s.length());
/* 268 */     buffer.append(s);
/* 269 */     return parse(buffer, charset, separators);
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
/*     */   public static List<NameValuePair> parse(CharArrayBuffer buf, Charset charset, char... separators) {
/* 287 */     Args.notNull(buf, "Char array buffer");
/* 288 */     TokenParser tokenParser = TokenParser.INSTANCE;
/* 289 */     BitSet delimSet = new BitSet();
/* 290 */     for (char separator : separators) {
/* 291 */       delimSet.set(separator);
/*     */     }
/* 293 */     ParserCursor cursor = new ParserCursor(0, buf.length());
/* 294 */     List<NameValuePair> list = new ArrayList<NameValuePair>();
/* 295 */     while (!cursor.atEnd()) {
/* 296 */       delimSet.set(61);
/* 297 */       String name = tokenParser.parseToken(buf, cursor, delimSet);
/* 298 */       String value = null;
/* 299 */       if (!cursor.atEnd()) {
/* 300 */         int delim = buf.charAt(cursor.getPos());
/* 301 */         cursor.updatePos(cursor.getPos() + 1);
/* 302 */         if (delim == 61) {
/* 303 */           delimSet.clear(61);
/* 304 */           value = tokenParser.parseToken(buf, cursor, delimSet);
/* 305 */           if (!cursor.atEnd()) {
/* 306 */             cursor.updatePos(cursor.getPos() + 1);
/*     */           }
/*     */         } 
/*     */       } 
/* 310 */       if (!name.isEmpty()) {
/* 311 */         list.add(new BasicNameValuePair(decodeFormFields(name, charset), decodeFormFields(value, charset)));
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 316 */     return list;
/*     */   }
/*     */   
/*     */   static List<String> splitSegments(CharSequence s, BitSet separators) {
/* 320 */     ParserCursor cursor = new ParserCursor(0, s.length());
/*     */     
/* 322 */     if (cursor.atEnd()) {
/* 323 */       return Collections.emptyList();
/*     */     }
/* 325 */     if (separators.get(s.charAt(cursor.getPos()))) {
/* 326 */       cursor.updatePos(cursor.getPos() + 1);
/*     */     }
/* 328 */     List<String> list = new ArrayList<String>();
/* 329 */     StringBuilder buf = new StringBuilder();
/*     */     while (true) {
/* 331 */       if (cursor.atEnd()) {
/* 332 */         list.add(buf.toString());
/*     */         break;
/*     */       } 
/* 335 */       char current = s.charAt(cursor.getPos());
/* 336 */       if (separators.get(current)) {
/* 337 */         list.add(buf.toString());
/* 338 */         buf.setLength(0);
/*     */       } else {
/* 340 */         buf.append(current);
/*     */       } 
/* 342 */       cursor.updatePos(cursor.getPos() + 1);
/*     */     } 
/* 344 */     return list;
/*     */   }
/*     */   
/*     */   static List<String> splitPathSegments(CharSequence s) {
/* 348 */     return splitSegments(s, PATH_SEPARATORS);
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
/*     */   public static List<String> parsePathSegments(CharSequence s, Charset charset) {
/* 361 */     Args.notNull(s, "Char sequence");
/* 362 */     List<String> list = splitPathSegments(s);
/* 363 */     for (int i = 0; i < list.size(); i++) {
/* 364 */       list.set(i, urlDecode(list.get(i), (charset != null) ? charset : Consts.UTF_8, false));
/*     */     }
/* 366 */     return list;
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
/*     */   public static List<String> parsePathSegments(CharSequence s) {
/* 378 */     return parsePathSegments(s, Consts.UTF_8);
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
/*     */   public static String formatSegments(Iterable<String> segments, Charset charset) {
/* 391 */     Args.notNull(segments, "Segments");
/* 392 */     StringBuilder result = new StringBuilder();
/* 393 */     for (String segment : segments) {
/* 394 */       result.append('/').append(urlEncode(segment, charset, PATHSAFE, false));
/*     */     }
/* 396 */     return result.toString();
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
/*     */   public static String formatSegments(String... segments) {
/* 408 */     return formatSegments(Arrays.asList(segments), Consts.UTF_8);
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
/*     */   public static String format(List<? extends NameValuePair> parameters, String charset) {
/* 422 */     return format(parameters, '&', charset);
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
/*     */   public static String format(List<? extends NameValuePair> parameters, char parameterSeparator, String charset) {
/* 440 */     StringBuilder result = new StringBuilder();
/* 441 */     for (NameValuePair parameter : parameters) {
/* 442 */       String encodedName = encodeFormFields(parameter.getName(), charset);
/* 443 */       String encodedValue = encodeFormFields(parameter.getValue(), charset);
/* 444 */       if (result.length() > 0) {
/* 445 */         result.append(parameterSeparator);
/*     */       }
/* 447 */       result.append(encodedName);
/* 448 */       if (encodedValue != null) {
/* 449 */         result.append("=");
/* 450 */         result.append(encodedValue);
/*     */       } 
/*     */     } 
/* 453 */     return result.toString();
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
/*     */   public static String format(Iterable<? extends NameValuePair> parameters, Charset charset) {
/* 469 */     return format(parameters, '&', charset);
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
/*     */   public static String format(Iterable<? extends NameValuePair> parameters, char parameterSeparator, Charset charset) {
/* 487 */     Args.notNull(parameters, "Parameters");
/* 488 */     StringBuilder result = new StringBuilder();
/* 489 */     for (NameValuePair parameter : parameters) {
/* 490 */       String encodedName = encodeFormFields(parameter.getName(), charset);
/* 491 */       String encodedValue = encodeFormFields(parameter.getValue(), charset);
/* 492 */       if (result.length() > 0) {
/* 493 */         result.append(parameterSeparator);
/*     */       }
/* 495 */       result.append(encodedName);
/* 496 */       if (encodedValue != null) {
/* 497 */         result.append("=");
/* 498 */         result.append(encodedValue);
/*     */       } 
/*     */     } 
/* 501 */     return result.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 510 */   private static final BitSet UNRESERVED = new BitSet(256);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 516 */   private static final BitSet PUNCT = new BitSet(256);
/*     */ 
/*     */   
/* 519 */   private static final BitSet USERINFO = new BitSet(256);
/*     */ 
/*     */   
/* 522 */   private static final BitSet PATHSAFE = new BitSet(256);
/*     */ 
/*     */   
/* 525 */   private static final BitSet URIC = new BitSet(256);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 535 */   private static final BitSet RESERVED = new BitSet(256);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 542 */   private static final BitSet URLENCODER = new BitSet(256);
/*     */   
/* 544 */   private static final BitSet PATH_SPECIAL = new BitSet(256);
/*     */   private static final int RADIX = 16;
/*     */   
/*     */   static {
/*     */     int i;
/* 549 */     for (i = 97; i <= 122; i++) {
/* 550 */       UNRESERVED.set(i);
/*     */     }
/* 552 */     for (i = 65; i <= 90; i++) {
/* 553 */       UNRESERVED.set(i);
/*     */     }
/*     */     
/* 556 */     for (i = 48; i <= 57; i++) {
/* 557 */       UNRESERVED.set(i);
/*     */     }
/* 559 */     UNRESERVED.set(95);
/* 560 */     UNRESERVED.set(45);
/* 561 */     UNRESERVED.set(46);
/* 562 */     UNRESERVED.set(42);
/* 563 */     URLENCODER.or(UNRESERVED);
/* 564 */     UNRESERVED.set(33);
/* 565 */     UNRESERVED.set(126);
/* 566 */     UNRESERVED.set(39);
/* 567 */     UNRESERVED.set(40);
/* 568 */     UNRESERVED.set(41);
/*     */     
/* 570 */     PUNCT.set(44);
/* 571 */     PUNCT.set(59);
/* 572 */     PUNCT.set(58);
/* 573 */     PUNCT.set(36);
/* 574 */     PUNCT.set(38);
/* 575 */     PUNCT.set(43);
/* 576 */     PUNCT.set(61);
/*     */     
/* 578 */     USERINFO.or(UNRESERVED);
/* 579 */     USERINFO.or(PUNCT);
/*     */ 
/*     */     
/* 582 */     PATHSAFE.or(UNRESERVED);
/* 583 */     PATHSAFE.set(59);
/* 584 */     PATHSAFE.set(58);
/* 585 */     PATHSAFE.set(64);
/* 586 */     PATHSAFE.set(38);
/* 587 */     PATHSAFE.set(61);
/* 588 */     PATHSAFE.set(43);
/* 589 */     PATHSAFE.set(36);
/* 590 */     PATHSAFE.set(44);
/*     */     
/* 592 */     PATH_SPECIAL.or(PATHSAFE);
/* 593 */     PATH_SPECIAL.set(47);
/*     */     
/* 595 */     RESERVED.set(59);
/* 596 */     RESERVED.set(47);
/* 597 */     RESERVED.set(63);
/* 598 */     RESERVED.set(58);
/* 599 */     RESERVED.set(64);
/* 600 */     RESERVED.set(38);
/* 601 */     RESERVED.set(61);
/* 602 */     RESERVED.set(43);
/* 603 */     RESERVED.set(36);
/* 604 */     RESERVED.set(44);
/* 605 */     RESERVED.set(91);
/* 606 */     RESERVED.set(93);
/*     */     
/* 608 */     URIC.or(RESERVED);
/* 609 */     URIC.or(UNRESERVED);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static List<NameValuePair> createEmptyList() {
/* 615 */     return new ArrayList<NameValuePair>(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String urlEncode(String content, Charset charset, BitSet safechars, boolean blankAsPlus) {
/* 623 */     if (content == null) {
/* 624 */       return null;
/*     */     }
/* 626 */     StringBuilder buf = new StringBuilder();
/* 627 */     ByteBuffer bb = charset.encode(content);
/* 628 */     while (bb.hasRemaining()) {
/* 629 */       int b = bb.get() & 0xFF;
/* 630 */       if (safechars.get(b)) {
/* 631 */         buf.append((char)b); continue;
/* 632 */       }  if (blankAsPlus && b == 32) {
/* 633 */         buf.append('+'); continue;
/*     */       } 
/* 635 */       buf.append("%");
/* 636 */       char hex1 = Character.toUpperCase(Character.forDigit(b >> 4 & 0xF, 16));
/* 637 */       char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
/* 638 */       buf.append(hex1);
/* 639 */       buf.append(hex2);
/*     */     } 
/*     */     
/* 642 */     return buf.toString();
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
/*     */   private static String urlDecode(String content, Charset charset, boolean plusAsBlank) {
/* 657 */     if (content == null) {
/* 658 */       return null;
/*     */     }
/* 660 */     ByteBuffer bb = ByteBuffer.allocate(content.length());
/* 661 */     CharBuffer cb = CharBuffer.wrap(content);
/* 662 */     while (cb.hasRemaining()) {
/* 663 */       char c = cb.get();
/* 664 */       if (c == '%' && cb.remaining() >= 2) {
/* 665 */         char uc = cb.get();
/* 666 */         char lc = cb.get();
/* 667 */         int u = Character.digit(uc, 16);
/* 668 */         int l = Character.digit(lc, 16);
/* 669 */         if (u != -1 && l != -1) {
/* 670 */           bb.put((byte)((u << 4) + l)); continue;
/*     */         } 
/* 672 */         bb.put((byte)37);
/* 673 */         bb.put((byte)uc);
/* 674 */         bb.put((byte)lc); continue;
/*     */       } 
/* 676 */       if (plusAsBlank && c == '+') {
/* 677 */         bb.put((byte)32); continue;
/*     */       } 
/* 679 */       bb.put((byte)c);
/*     */     } 
/*     */     
/* 682 */     bb.flip();
/* 683 */     return charset.decode(bb).toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String decodeFormFields(String content, String charset) {
/* 694 */     if (content == null) {
/* 695 */       return null;
/*     */     }
/* 697 */     return urlDecode(content, (charset != null) ? Charset.forName(charset) : Consts.UTF_8, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String decodeFormFields(String content, Charset charset) {
/* 708 */     if (content == null) {
/* 709 */       return null;
/*     */     }
/* 711 */     return urlDecode(content, (charset != null) ? charset : Consts.UTF_8, true);
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
/*     */   private static String encodeFormFields(String content, String charset) {
/* 726 */     if (content == null) {
/* 727 */       return null;
/*     */     }
/* 729 */     return urlEncode(content, (charset != null) ? Charset.forName(charset) : Consts.UTF_8, URLENCODER, true);
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
/*     */   private static String encodeFormFields(String content, Charset charset) {
/* 744 */     if (content == null) {
/* 745 */       return null;
/*     */     }
/* 747 */     return urlEncode(content, (charset != null) ? charset : Consts.UTF_8, URLENCODER, true);
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
/*     */   static String encUserInfo(String content, Charset charset) {
/* 760 */     return urlEncode(content, charset, USERINFO, false);
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
/*     */   static String encUric(String content, Charset charset) {
/* 773 */     return urlEncode(content, charset, URIC, false);
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
/*     */   static String encPath(String content, Charset charset) {
/* 786 */     return urlEncode(content, charset, PATH_SPECIAL, false);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\clien\\utils\URLEncodedUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */