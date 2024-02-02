/*     */ package org.apache.http.message;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.http.FormattedHeader;
/*     */ import org.apache.http.HeaderElement;
/*     */ import org.apache.http.ParseException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BufferedHeader
/*     */   implements FormattedHeader, Cloneable, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -2768352615787625448L;
/*     */   private final String name;
/*     */   private final CharArrayBuffer buffer;
/*     */   private final int valuePos;
/*     */   
/*     */   public BufferedHeader(CharArrayBuffer buffer) throws ParseException {
/*  77 */     Args.notNull(buffer, "Char array buffer");
/*  78 */     int colon = buffer.indexOf(58);
/*  79 */     if (colon == -1) {
/*  80 */       throw new ParseException("Invalid header: " + buffer.toString());
/*     */     }
/*     */     
/*  83 */     String s = buffer.substringTrimmed(0, colon);
/*  84 */     if (s.isEmpty()) {
/*  85 */       throw new ParseException("Invalid header: " + buffer.toString());
/*     */     }
/*     */     
/*  88 */     this.buffer = buffer;
/*  89 */     this.name = s;
/*  90 */     this.valuePos = colon + 1;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  96 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getValue() {
/* 101 */     return this.buffer.substringTrimmed(this.valuePos, this.buffer.length());
/*     */   }
/*     */ 
/*     */   
/*     */   public HeaderElement[] getElements() throws ParseException {
/* 106 */     ParserCursor cursor = new ParserCursor(0, this.buffer.length());
/* 107 */     cursor.updatePos(this.valuePos);
/* 108 */     return BasicHeaderValueParser.INSTANCE.parseElements(this.buffer, cursor);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValuePos() {
/* 113 */     return this.valuePos;
/*     */   }
/*     */ 
/*     */   
/*     */   public CharArrayBuffer getBuffer() {
/* 118 */     return this.buffer;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 123 */     return this.buffer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 130 */     return super.clone();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\BufferedHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */