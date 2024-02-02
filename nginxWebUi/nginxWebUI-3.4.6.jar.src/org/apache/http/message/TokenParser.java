/*     */ package org.apache.http.message;
/*     */ 
/*     */ import java.util.BitSet;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class TokenParser
/*     */ {
/*     */   public static final char CR = '\r';
/*     */   public static final char LF = '\n';
/*     */   public static final char SP = ' ';
/*     */   public static final char HT = '\t';
/*     */   public static final char DQUOTE = '"';
/*     */   public static final char ESCAPE = '\\';
/*     */   
/*     */   public static BitSet INIT_BITSET(int... b) {
/*  48 */     BitSet bitset = new BitSet();
/*  49 */     for (int aB : b) {
/*  50 */       bitset.set(aB);
/*     */     }
/*  52 */     return bitset;
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
/*     */   public static boolean isWhitespace(char ch) {
/*  74 */     return (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n');
/*     */   }
/*     */   
/*  77 */   public static final TokenParser INSTANCE = new TokenParser();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String parseToken(CharArrayBuffer buf, ParserCursor cursor, BitSet delimiters) {
/*  89 */     StringBuilder dst = new StringBuilder();
/*  90 */     boolean whitespace = false;
/*  91 */     while (!cursor.atEnd()) {
/*  92 */       char current = buf.charAt(cursor.getPos());
/*  93 */       if (delimiters != null && delimiters.get(current))
/*     */         break; 
/*  95 */       if (isWhitespace(current)) {
/*  96 */         skipWhiteSpace(buf, cursor);
/*  97 */         whitespace = true; continue;
/*     */       } 
/*  99 */       if (whitespace && dst.length() > 0) {
/* 100 */         dst.append(' ');
/*     */       }
/* 102 */       copyContent(buf, cursor, delimiters, dst);
/* 103 */       whitespace = false;
/*     */     } 
/*     */     
/* 106 */     return dst.toString();
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
/*     */   public String parseValue(CharArrayBuffer buf, ParserCursor cursor, BitSet delimiters) {
/* 120 */     StringBuilder dst = new StringBuilder();
/* 121 */     boolean whitespace = false;
/* 122 */     while (!cursor.atEnd()) {
/* 123 */       char current = buf.charAt(cursor.getPos());
/* 124 */       if (delimiters != null && delimiters.get(current))
/*     */         break; 
/* 126 */       if (isWhitespace(current)) {
/* 127 */         skipWhiteSpace(buf, cursor);
/* 128 */         whitespace = true; continue;
/* 129 */       }  if (current == '"') {
/* 130 */         if (whitespace && dst.length() > 0) {
/* 131 */           dst.append(' ');
/*     */         }
/* 133 */         copyQuotedContent(buf, cursor, dst);
/* 134 */         whitespace = false; continue;
/*     */       } 
/* 136 */       if (whitespace && dst.length() > 0) {
/* 137 */         dst.append(' ');
/*     */       }
/* 139 */       copyUnquotedContent(buf, cursor, delimiters, dst);
/* 140 */       whitespace = false;
/*     */     } 
/*     */     
/* 143 */     return dst.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void skipWhiteSpace(CharArrayBuffer buf, ParserCursor cursor) {
/* 154 */     int pos = cursor.getPos();
/* 155 */     int indexFrom = cursor.getPos();
/* 156 */     int indexTo = cursor.getUpperBound();
/* 157 */     for (int i = indexFrom; i < indexTo; i++) {
/* 158 */       char current = buf.charAt(i);
/* 159 */       if (!isWhitespace(current)) {
/*     */         break;
/*     */       }
/* 162 */       pos++;
/*     */     } 
/* 164 */     cursor.updatePos(pos);
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
/*     */   public void copyContent(CharArrayBuffer buf, ParserCursor cursor, BitSet delimiters, StringBuilder dst) {
/* 179 */     int pos = cursor.getPos();
/* 180 */     int indexFrom = cursor.getPos();
/* 181 */     int indexTo = cursor.getUpperBound();
/* 182 */     for (int i = indexFrom; i < indexTo; i++) {
/* 183 */       char current = buf.charAt(i);
/* 184 */       if ((delimiters != null && delimiters.get(current)) || isWhitespace(current)) {
/*     */         break;
/*     */       }
/* 187 */       pos++;
/* 188 */       dst.append(current);
/*     */     } 
/* 190 */     cursor.updatePos(pos);
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
/*     */   public void copyUnquotedContent(CharArrayBuffer buf, ParserCursor cursor, BitSet delimiters, StringBuilder dst) {
/* 205 */     int pos = cursor.getPos();
/* 206 */     int indexFrom = cursor.getPos();
/* 207 */     int indexTo = cursor.getUpperBound();
/* 208 */     for (int i = indexFrom; i < indexTo; i++) {
/* 209 */       char current = buf.charAt(i);
/* 210 */       if ((delimiters != null && delimiters.get(current)) || isWhitespace(current) || current == '"') {
/*     */         break;
/*     */       }
/*     */       
/* 214 */       pos++;
/* 215 */       dst.append(current);
/*     */     } 
/* 217 */     cursor.updatePos(pos);
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
/*     */   public void copyQuotedContent(CharArrayBuffer buf, ParserCursor cursor, StringBuilder dst) {
/* 229 */     if (cursor.atEnd()) {
/*     */       return;
/*     */     }
/* 232 */     int pos = cursor.getPos();
/* 233 */     int indexFrom = cursor.getPos();
/* 234 */     int indexTo = cursor.getUpperBound();
/* 235 */     char current = buf.charAt(pos);
/* 236 */     if (current != '"') {
/*     */       return;
/*     */     }
/* 239 */     pos++;
/* 240 */     indexFrom++;
/* 241 */     boolean escaped = false;
/* 242 */     for (int i = indexFrom; i < indexTo; i++, pos++) {
/* 243 */       current = buf.charAt(i);
/* 244 */       if (escaped) {
/* 245 */         if (current != '"' && current != '\\') {
/* 246 */           dst.append('\\');
/*     */         }
/* 248 */         dst.append(current);
/* 249 */         escaped = false;
/*     */       } else {
/* 251 */         if (current == '"') {
/* 252 */           pos++;
/*     */           break;
/*     */         } 
/* 255 */         if (current == '\\') {
/* 256 */           escaped = true;
/* 257 */         } else if (current != '\r' && current != '\n') {
/* 258 */           dst.append(current);
/*     */         } 
/*     */       } 
/*     */     } 
/* 262 */     cursor.updatePos(pos);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\TokenParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */