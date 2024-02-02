/*     */ package org.apache.http.impl.cookie;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.BitSet;
/*     */ import java.util.List;
/*     */ import org.apache.http.HeaderElement;
/*     */ import org.apache.http.NameValuePair;
/*     */ import org.apache.http.ParseException;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.message.BasicHeaderElement;
/*     */ import org.apache.http.message.BasicNameValuePair;
/*     */ import org.apache.http.message.ParserCursor;
/*     */ import org.apache.http.message.TokenParser;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class NetscapeDraftHeaderParser
/*     */ {
/*  53 */   public static final NetscapeDraftHeaderParser DEFAULT = new NetscapeDraftHeaderParser();
/*     */ 
/*     */   
/*     */   private static final char PARAM_DELIMITER = ';';
/*     */ 
/*     */   
/*  59 */   private static final BitSet TOKEN_DELIMS = TokenParser.INIT_BITSET(new int[] { 61, 59 });
/*  60 */   private static final BitSet VALUE_DELIMS = TokenParser.INIT_BITSET(new int[] { 59 });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   private final TokenParser tokenParser = TokenParser.INSTANCE;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HeaderElement parseHeader(CharArrayBuffer buffer, ParserCursor cursor) throws ParseException {
/*  72 */     Args.notNull(buffer, "Char array buffer");
/*  73 */     Args.notNull(cursor, "Parser cursor");
/*  74 */     NameValuePair nvp = parseNameValuePair(buffer, cursor);
/*  75 */     List<NameValuePair> params = new ArrayList<NameValuePair>();
/*  76 */     while (!cursor.atEnd()) {
/*  77 */       NameValuePair param = parseNameValuePair(buffer, cursor);
/*  78 */       params.add(param);
/*     */     } 
/*  80 */     return (HeaderElement)new BasicHeaderElement(nvp.getName(), nvp.getValue(), params.<NameValuePair>toArray(new NameValuePair[params.size()]));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private NameValuePair parseNameValuePair(CharArrayBuffer buffer, ParserCursor cursor) {
/*  87 */     String name = this.tokenParser.parseToken(buffer, cursor, TOKEN_DELIMS);
/*  88 */     if (cursor.atEnd()) {
/*  89 */       return (NameValuePair)new BasicNameValuePair(name, null);
/*     */     }
/*  91 */     int delim = buffer.charAt(cursor.getPos());
/*  92 */     cursor.updatePos(cursor.getPos() + 1);
/*  93 */     if (delim != 61) {
/*  94 */       return (NameValuePair)new BasicNameValuePair(name, null);
/*     */     }
/*  96 */     String value = this.tokenParser.parseToken(buffer, cursor, VALUE_DELIMS);
/*  97 */     if (!cursor.atEnd()) {
/*  98 */       cursor.updatePos(cursor.getPos() + 1);
/*     */     }
/* 100 */     return (NameValuePair)new BasicNameValuePair(name, value);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\NetscapeDraftHeaderParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */