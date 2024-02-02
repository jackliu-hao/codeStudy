/*     */ package org.apache.http.message;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.BitSet;
/*     */ import java.util.List;
/*     */ import org.apache.http.HeaderElement;
/*     */ import org.apache.http.NameValuePair;
/*     */ import org.apache.http.ParseException;
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
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class BasicHeaderValueParser
/*     */   implements HeaderValueParser
/*     */ {
/*     */   @Deprecated
/*  61 */   public static final BasicHeaderValueParser DEFAULT = new BasicHeaderValueParser();
/*     */   
/*  63 */   public static final BasicHeaderValueParser INSTANCE = new BasicHeaderValueParser();
/*     */ 
/*     */   
/*     */   private static final char PARAM_DELIMITER = ';';
/*     */   
/*     */   private static final char ELEM_DELIMITER = ',';
/*     */   
/*  70 */   private static final BitSet TOKEN_DELIMS = TokenParser.INIT_BITSET(new int[] { 61, 59, 44 });
/*  71 */   private static final BitSet VALUE_DELIMS = TokenParser.INIT_BITSET(new int[] { 59, 44 });
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  76 */   private final TokenParser tokenParser = TokenParser.INSTANCE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HeaderElement[] parseElements(String value, HeaderValueParser parser) throws ParseException {
/*  91 */     Args.notNull(value, "Value");
/*     */     
/*  93 */     CharArrayBuffer buffer = new CharArrayBuffer(value.length());
/*  94 */     buffer.append(value);
/*  95 */     ParserCursor cursor = new ParserCursor(0, value.length());
/*  96 */     return ((parser != null) ? parser : INSTANCE).parseElements(buffer, cursor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HeaderElement[] parseElements(CharArrayBuffer buffer, ParserCursor cursor) {
/* 105 */     Args.notNull(buffer, "Char array buffer");
/* 106 */     Args.notNull(cursor, "Parser cursor");
/* 107 */     List<HeaderElement> elements = new ArrayList<HeaderElement>();
/* 108 */     while (!cursor.atEnd()) {
/* 109 */       HeaderElement element = parseHeaderElement(buffer, cursor);
/* 110 */       if (!element.getName().isEmpty() || element.getValue() != null) {
/* 111 */         elements.add(element);
/*     */       }
/*     */     } 
/* 114 */     return elements.<HeaderElement>toArray(new HeaderElement[elements.size()]);
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
/*     */   public static HeaderElement parseHeaderElement(String value, HeaderValueParser parser) throws ParseException {
/* 129 */     Args.notNull(value, "Value");
/*     */     
/* 131 */     CharArrayBuffer buffer = new CharArrayBuffer(value.length());
/* 132 */     buffer.append(value);
/* 133 */     ParserCursor cursor = new ParserCursor(0, value.length());
/* 134 */     return ((parser != null) ? parser : INSTANCE).parseHeaderElement(buffer, cursor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HeaderElement parseHeaderElement(CharArrayBuffer buffer, ParserCursor cursor) {
/* 143 */     Args.notNull(buffer, "Char array buffer");
/* 144 */     Args.notNull(cursor, "Parser cursor");
/* 145 */     NameValuePair nvp = parseNameValuePair(buffer, cursor);
/* 146 */     NameValuePair[] params = null;
/* 147 */     if (!cursor.atEnd()) {
/* 148 */       char ch = buffer.charAt(cursor.getPos() - 1);
/* 149 */       if (ch != ',') {
/* 150 */         params = parseParameters(buffer, cursor);
/*     */       }
/*     */     } 
/* 153 */     return createHeaderElement(nvp.getName(), nvp.getValue(), params);
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
/*     */   protected HeaderElement createHeaderElement(String name, String value, NameValuePair[] params) {
/* 167 */     return new BasicHeaderElement(name, value, params);
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
/*     */   public static NameValuePair[] parseParameters(String value, HeaderValueParser parser) throws ParseException {
/* 182 */     Args.notNull(value, "Value");
/*     */     
/* 184 */     CharArrayBuffer buffer = new CharArrayBuffer(value.length());
/* 185 */     buffer.append(value);
/* 186 */     ParserCursor cursor = new ParserCursor(0, value.length());
/* 187 */     return ((parser != null) ? parser : INSTANCE).parseParameters(buffer, cursor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NameValuePair[] parseParameters(CharArrayBuffer buffer, ParserCursor cursor) {
/* 197 */     Args.notNull(buffer, "Char array buffer");
/* 198 */     Args.notNull(cursor, "Parser cursor");
/* 199 */     this.tokenParser.skipWhiteSpace(buffer, cursor);
/* 200 */     List<NameValuePair> params = new ArrayList<NameValuePair>();
/* 201 */     while (!cursor.atEnd()) {
/* 202 */       NameValuePair param = parseNameValuePair(buffer, cursor);
/* 203 */       params.add(param);
/* 204 */       char ch = buffer.charAt(cursor.getPos() - 1);
/* 205 */       if (ch == ',') {
/*     */         break;
/*     */       }
/*     */     } 
/* 209 */     return params.<NameValuePair>toArray(new NameValuePair[params.size()]);
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
/*     */   public static NameValuePair parseNameValuePair(String value, HeaderValueParser parser) throws ParseException {
/* 223 */     Args.notNull(value, "Value");
/*     */     
/* 225 */     CharArrayBuffer buffer = new CharArrayBuffer(value.length());
/* 226 */     buffer.append(value);
/* 227 */     ParserCursor cursor = new ParserCursor(0, value.length());
/* 228 */     return ((parser != null) ? parser : INSTANCE).parseNameValuePair(buffer, cursor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NameValuePair parseNameValuePair(CharArrayBuffer buffer, ParserCursor cursor) {
/* 237 */     Args.notNull(buffer, "Char array buffer");
/* 238 */     Args.notNull(cursor, "Parser cursor");
/*     */     
/* 240 */     String name = this.tokenParser.parseToken(buffer, cursor, TOKEN_DELIMS);
/* 241 */     if (cursor.atEnd()) {
/* 242 */       return new BasicNameValuePair(name, null);
/*     */     }
/* 244 */     int delim = buffer.charAt(cursor.getPos());
/* 245 */     cursor.updatePos(cursor.getPos() + 1);
/* 246 */     if (delim != 61) {
/* 247 */       return createNameValuePair(name, null);
/*     */     }
/* 249 */     String value = this.tokenParser.parseValue(buffer, cursor, VALUE_DELIMS);
/* 250 */     if (!cursor.atEnd()) {
/* 251 */       cursor.updatePos(cursor.getPos() + 1);
/*     */     }
/* 253 */     return createNameValuePair(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public NameValuePair parseNameValuePair(CharArrayBuffer buffer, ParserCursor cursor, char[] delimiters) {
/* 263 */     Args.notNull(buffer, "Char array buffer");
/* 264 */     Args.notNull(cursor, "Parser cursor");
/*     */     
/* 266 */     BitSet delimSet = new BitSet();
/* 267 */     if (delimiters != null) {
/* 268 */       for (char delimiter : delimiters) {
/* 269 */         delimSet.set(delimiter);
/*     */       }
/*     */     }
/* 272 */     delimSet.set(61);
/* 273 */     String name = this.tokenParser.parseToken(buffer, cursor, delimSet);
/* 274 */     if (cursor.atEnd()) {
/* 275 */       return new BasicNameValuePair(name, null);
/*     */     }
/* 277 */     int delim = buffer.charAt(cursor.getPos());
/* 278 */     cursor.updatePos(cursor.getPos() + 1);
/* 279 */     if (delim != 61) {
/* 280 */       return createNameValuePair(name, null);
/*     */     }
/* 282 */     delimSet.clear(61);
/* 283 */     String value = this.tokenParser.parseValue(buffer, cursor, delimSet);
/* 284 */     if (!cursor.atEnd()) {
/* 285 */       cursor.updatePos(cursor.getPos() + 1);
/*     */     }
/* 287 */     return createNameValuePair(name, value);
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
/*     */   protected NameValuePair createNameValuePair(String name, String value) {
/* 300 */     return new BasicNameValuePair(name, value);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\BasicHeaderValueParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */