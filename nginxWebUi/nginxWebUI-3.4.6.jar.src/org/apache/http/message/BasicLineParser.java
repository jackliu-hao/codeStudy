/*     */ package org.apache.http.message;
/*     */ 
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpVersion;
/*     */ import org.apache.http.ParseException;
/*     */ import org.apache.http.ProtocolVersion;
/*     */ import org.apache.http.RequestLine;
/*     */ import org.apache.http.StatusLine;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public class BasicLineParser
/*     */   implements LineParser
/*     */ {
/*     */   @Deprecated
/*  72 */   public static final BasicLineParser DEFAULT = new BasicLineParser();
/*     */   
/*  74 */   public static final BasicLineParser INSTANCE = new BasicLineParser();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final ProtocolVersion protocol;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BasicLineParser(ProtocolVersion proto) {
/*  91 */     this.protocol = (proto != null) ? proto : (ProtocolVersion)HttpVersion.HTTP_1_1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BasicLineParser() {
/*  99 */     this(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ProtocolVersion parseProtocolVersion(String value, LineParser parser) throws ParseException {
/* 106 */     Args.notNull(value, "Value");
/*     */     
/* 108 */     CharArrayBuffer buffer = new CharArrayBuffer(value.length());
/* 109 */     buffer.append(value);
/* 110 */     ParserCursor cursor = new ParserCursor(0, value.length());
/* 111 */     return ((parser != null) ? parser : INSTANCE).parseProtocolVersion(buffer, cursor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ProtocolVersion parseProtocolVersion(CharArrayBuffer buffer, ParserCursor cursor) throws ParseException {
/*     */     int major, minor;
/* 120 */     Args.notNull(buffer, "Char array buffer");
/* 121 */     Args.notNull(cursor, "Parser cursor");
/* 122 */     String protoname = this.protocol.getProtocol();
/* 123 */     int protolength = protoname.length();
/*     */     
/* 125 */     int indexFrom = cursor.getPos();
/* 126 */     int indexTo = cursor.getUpperBound();
/*     */     
/* 128 */     skipWhitespace(buffer, cursor);
/*     */     
/* 130 */     int i = cursor.getPos();
/*     */ 
/*     */     
/* 133 */     if (i + protolength + 4 > indexTo) {
/* 134 */       throw new ParseException("Not a valid protocol version: " + buffer.substring(indexFrom, indexTo));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 140 */     boolean ok = true;
/* 141 */     for (int j = 0; ok && j < protolength; j++) {
/* 142 */       ok = (buffer.charAt(i + j) == protoname.charAt(j));
/*     */     }
/* 144 */     if (ok) {
/* 145 */       ok = (buffer.charAt(i + protolength) == '/');
/*     */     }
/* 147 */     if (!ok) {
/* 148 */       throw new ParseException("Not a valid protocol version: " + buffer.substring(indexFrom, indexTo));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 153 */     i += protolength + 1;
/*     */     
/* 155 */     int period = buffer.indexOf(46, i, indexTo);
/* 156 */     if (period == -1) {
/* 157 */       throw new ParseException("Invalid protocol version number: " + buffer.substring(indexFrom, indexTo));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 163 */       major = Integer.parseInt(buffer.substringTrimmed(i, period));
/* 164 */     } catch (NumberFormatException e) {
/* 165 */       throw new ParseException("Invalid protocol major version number: " + buffer.substring(indexFrom, indexTo));
/*     */     } 
/*     */ 
/*     */     
/* 169 */     i = period + 1;
/*     */     
/* 171 */     int blank = buffer.indexOf(32, i, indexTo);
/* 172 */     if (blank == -1) {
/* 173 */       blank = indexTo;
/*     */     }
/*     */     
/*     */     try {
/* 177 */       minor = Integer.parseInt(buffer.substringTrimmed(i, blank));
/* 178 */     } catch (NumberFormatException e) {
/* 179 */       throw new ParseException("Invalid protocol minor version number: " + buffer.substring(indexFrom, indexTo));
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 184 */     cursor.updatePos(blank);
/*     */     
/* 186 */     return createProtocolVersion(major, minor);
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
/*     */   protected ProtocolVersion createProtocolVersion(int major, int minor) {
/* 201 */     return this.protocol.forVersion(major, minor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasProtocolVersion(CharArrayBuffer buffer, ParserCursor cursor) {
/* 210 */     Args.notNull(buffer, "Char array buffer");
/* 211 */     Args.notNull(cursor, "Parser cursor");
/* 212 */     int index = cursor.getPos();
/*     */     
/* 214 */     String protoname = this.protocol.getProtocol();
/* 215 */     int protolength = protoname.length();
/*     */     
/* 217 */     if (buffer.length() < protolength + 4)
/*     */     {
/* 219 */       return false;
/*     */     }
/*     */     
/* 222 */     if (index < 0) {
/*     */ 
/*     */       
/* 225 */       index = buffer.length() - 4 - protolength;
/* 226 */     } else if (index == 0) {
/*     */       
/* 228 */       while (index < buffer.length() && HTTP.isWhitespace(buffer.charAt(index)))
/*     */       {
/* 230 */         index++;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 235 */     if (index + protolength + 4 > buffer.length()) {
/* 236 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 241 */     boolean ok = true;
/* 242 */     for (int j = 0; ok && j < protolength; j++) {
/* 243 */       ok = (buffer.charAt(index + j) == protoname.charAt(j));
/*     */     }
/* 245 */     if (ok) {
/* 246 */       ok = (buffer.charAt(index + protolength) == '/');
/*     */     }
/*     */     
/* 249 */     return ok;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestLine parseRequestLine(String value, LineParser parser) throws ParseException {
/* 257 */     Args.notNull(value, "Value");
/*     */     
/* 259 */     CharArrayBuffer buffer = new CharArrayBuffer(value.length());
/* 260 */     buffer.append(value);
/* 261 */     ParserCursor cursor = new ParserCursor(0, value.length());
/* 262 */     return ((parser != null) ? parser : INSTANCE).parseRequestLine(buffer, cursor);
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
/*     */   public RequestLine parseRequestLine(CharArrayBuffer buffer, ParserCursor cursor) throws ParseException {
/* 280 */     Args.notNull(buffer, "Char array buffer");
/* 281 */     Args.notNull(cursor, "Parser cursor");
/* 282 */     int indexFrom = cursor.getPos();
/* 283 */     int indexTo = cursor.getUpperBound();
/*     */     
/*     */     try {
/* 286 */       skipWhitespace(buffer, cursor);
/* 287 */       int i = cursor.getPos();
/*     */       
/* 289 */       int blank = buffer.indexOf(32, i, indexTo);
/* 290 */       if (blank < 0) {
/* 291 */         throw new ParseException("Invalid request line: " + buffer.substring(indexFrom, indexTo));
/*     */       }
/*     */       
/* 294 */       String method = buffer.substringTrimmed(i, blank);
/* 295 */       cursor.updatePos(blank);
/*     */       
/* 297 */       skipWhitespace(buffer, cursor);
/* 298 */       i = cursor.getPos();
/*     */       
/* 300 */       blank = buffer.indexOf(32, i, indexTo);
/* 301 */       if (blank < 0) {
/* 302 */         throw new ParseException("Invalid request line: " + buffer.substring(indexFrom, indexTo));
/*     */       }
/*     */       
/* 305 */       String uri = buffer.substringTrimmed(i, blank);
/* 306 */       cursor.updatePos(blank);
/*     */       
/* 308 */       ProtocolVersion ver = parseProtocolVersion(buffer, cursor);
/*     */       
/* 310 */       skipWhitespace(buffer, cursor);
/* 311 */       if (!cursor.atEnd()) {
/* 312 */         throw new ParseException("Invalid request line: " + buffer.substring(indexFrom, indexTo));
/*     */       }
/*     */ 
/*     */       
/* 316 */       return createRequestLine(method, uri, ver);
/* 317 */     } catch (IndexOutOfBoundsException e) {
/* 318 */       throw new ParseException("Invalid request line: " + buffer.substring(indexFrom, indexTo));
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected RequestLine createRequestLine(String method, String uri, ProtocolVersion ver) {
/* 337 */     return new BasicRequestLine(method, uri, ver);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StatusLine parseStatusLine(String value, LineParser parser) throws ParseException {
/* 345 */     Args.notNull(value, "Value");
/*     */     
/* 347 */     CharArrayBuffer buffer = new CharArrayBuffer(value.length());
/* 348 */     buffer.append(value);
/* 349 */     ParserCursor cursor = new ParserCursor(0, value.length());
/* 350 */     return ((parser != null) ? parser : INSTANCE).parseStatusLine(buffer, cursor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StatusLine parseStatusLine(CharArrayBuffer buffer, ParserCursor cursor) throws ParseException {
/* 359 */     Args.notNull(buffer, "Char array buffer");
/* 360 */     Args.notNull(cursor, "Parser cursor");
/* 361 */     int indexFrom = cursor.getPos();
/* 362 */     int indexTo = cursor.getUpperBound();
/*     */     try {
/*     */       int statusCode;
/*     */       String reasonPhrase;
/* 366 */       ProtocolVersion ver = parseProtocolVersion(buffer, cursor);
/*     */ 
/*     */       
/* 369 */       skipWhitespace(buffer, cursor);
/* 370 */       int i = cursor.getPos();
/*     */       
/* 372 */       int blank = buffer.indexOf(32, i, indexTo);
/* 373 */       if (blank < 0) {
/* 374 */         blank = indexTo;
/*     */       }
/*     */       
/* 377 */       String s = buffer.substringTrimmed(i, blank);
/* 378 */       for (int j = 0; j < s.length(); j++) {
/* 379 */         if (!Character.isDigit(s.charAt(j))) {
/* 380 */           throw new ParseException("Status line contains invalid status code: " + buffer.substring(indexFrom, indexTo));
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/*     */       try {
/* 386 */         statusCode = Integer.parseInt(s);
/* 387 */       } catch (NumberFormatException e) {
/* 388 */         throw new ParseException("Status line contains invalid status code: " + buffer.substring(indexFrom, indexTo));
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 393 */       i = blank;
/*     */       
/* 395 */       if (i < indexTo) {
/* 396 */         reasonPhrase = buffer.substringTrimmed(i, indexTo);
/*     */       } else {
/* 398 */         reasonPhrase = "";
/*     */       } 
/* 400 */       return createStatusLine(ver, statusCode, reasonPhrase);
/*     */     }
/* 402 */     catch (IndexOutOfBoundsException e) {
/* 403 */       throw new ParseException("Invalid status line: " + buffer.substring(indexFrom, indexTo));
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected StatusLine createStatusLine(ProtocolVersion ver, int status, String reason) {
/* 422 */     return new BasicStatusLine(ver, status, reason);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Header parseHeader(String value, LineParser parser) throws ParseException {
/* 430 */     Args.notNull(value, "Value");
/*     */     
/* 432 */     CharArrayBuffer buffer = new CharArrayBuffer(value.length());
/* 433 */     buffer.append(value);
/* 434 */     return ((parser != null) ? parser : INSTANCE).parseHeader(buffer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Header parseHeader(CharArrayBuffer buffer) throws ParseException {
/* 445 */     return (Header)new BufferedHeader(buffer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void skipWhitespace(CharArrayBuffer buffer, ParserCursor cursor) {
/* 453 */     int pos = cursor.getPos();
/* 454 */     int indexTo = cursor.getUpperBound();
/* 455 */     while (pos < indexTo && HTTP.isWhitespace(buffer.charAt(pos)))
/*     */     {
/* 457 */       pos++;
/*     */     }
/* 459 */     cursor.updatePos(pos);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\BasicLineParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */