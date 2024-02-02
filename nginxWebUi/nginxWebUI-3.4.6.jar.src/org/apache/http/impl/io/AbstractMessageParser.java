/*     */ package org.apache.http.impl.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpMessage;
/*     */ import org.apache.http.MessageConstraintException;
/*     */ import org.apache.http.ParseException;
/*     */ import org.apache.http.ProtocolException;
/*     */ import org.apache.http.config.MessageConstraints;
/*     */ import org.apache.http.io.HttpMessageParser;
/*     */ import org.apache.http.io.SessionInputBuffer;
/*     */ import org.apache.http.message.BasicLineParser;
/*     */ import org.apache.http.message.LineParser;
/*     */ import org.apache.http.params.HttpParamConfig;
/*     */ import org.apache.http.params.HttpParams;
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
/*     */ public abstract class AbstractMessageParser<T extends HttpMessage>
/*     */   implements HttpMessageParser<T>
/*     */ {
/*     */   private static final int HEAD_LINE = 0;
/*     */   private static final int HEADERS = 1;
/*     */   private final SessionInputBuffer sessionBuffer;
/*     */   private final MessageConstraints messageConstraints;
/*     */   private final List<CharArrayBuffer> headerLines;
/*     */   protected final LineParser lineParser;
/*     */   private int state;
/*     */   private T message;
/*     */   
/*     */   @Deprecated
/*     */   public AbstractMessageParser(SessionInputBuffer buffer, LineParser parser, HttpParams params) {
/*  86 */     Args.notNull(buffer, "Session input buffer");
/*  87 */     Args.notNull(params, "HTTP parameters");
/*  88 */     this.sessionBuffer = buffer;
/*  89 */     this.messageConstraints = HttpParamConfig.getMessageConstraints(params);
/*  90 */     this.lineParser = (parser != null) ? parser : (LineParser)BasicLineParser.INSTANCE;
/*  91 */     this.headerLines = new ArrayList<CharArrayBuffer>();
/*  92 */     this.state = 0;
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
/*     */   public AbstractMessageParser(SessionInputBuffer buffer, LineParser lineParser, MessageConstraints constraints) {
/* 111 */     this.sessionBuffer = (SessionInputBuffer)Args.notNull(buffer, "Session input buffer");
/* 112 */     this.lineParser = (lineParser != null) ? lineParser : (LineParser)BasicLineParser.INSTANCE;
/* 113 */     this.messageConstraints = (constraints != null) ? constraints : MessageConstraints.DEFAULT;
/* 114 */     this.headerLines = new ArrayList<CharArrayBuffer>();
/* 115 */     this.state = 0;
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
/*     */   
/*     */   public static Header[] parseHeaders(SessionInputBuffer inBuffer, int maxHeaderCount, int maxLineLen, LineParser parser) throws HttpException, IOException {
/* 142 */     List<CharArrayBuffer> headerLines = new ArrayList<CharArrayBuffer>();
/* 143 */     return parseHeaders(inBuffer, maxHeaderCount, maxLineLen, (parser != null) ? parser : (LineParser)BasicLineParser.INSTANCE, headerLines);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Header[] parseHeaders(SessionInputBuffer inBuffer, int maxHeaderCount, int maxLineLen, LineParser parser, List<CharArrayBuffer> headerLines) throws HttpException, IOException {
/* 178 */     Args.notNull(inBuffer, "Session input buffer");
/* 179 */     Args.notNull(parser, "Line parser");
/* 180 */     Args.notNull(headerLines, "Header line list");
/*     */     
/* 182 */     CharArrayBuffer current = null;
/* 183 */     CharArrayBuffer previous = null;
/*     */     while (true) {
/* 185 */       if (current == null) {
/* 186 */         current = new CharArrayBuffer(64);
/*     */       } else {
/* 188 */         current.clear();
/*     */       } 
/* 190 */       int readLen = inBuffer.readLine(current);
/* 191 */       if (readLen == -1 || current.length() < 1) {
/*     */         break;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 198 */       if ((current.charAt(0) == ' ' || current.charAt(0) == '\t') && previous != null) {
/*     */ 
/*     */         
/* 201 */         int j = 0;
/* 202 */         while (j < current.length()) {
/* 203 */           char ch = current.charAt(j);
/* 204 */           if (ch != ' ' && ch != '\t') {
/*     */             break;
/*     */           }
/* 207 */           j++;
/*     */         } 
/* 209 */         if (maxLineLen > 0 && previous.length() + 1 + current.length() - j > maxLineLen)
/*     */         {
/* 211 */           throw new MessageConstraintException("Maximum line length limit exceeded");
/*     */         }
/* 213 */         previous.append(' ');
/* 214 */         previous.append(current, j, current.length() - j);
/*     */       } else {
/* 216 */         headerLines.add(current);
/* 217 */         previous = current;
/* 218 */         current = null;
/*     */       } 
/* 220 */       if (maxHeaderCount > 0 && headerLines.size() >= maxHeaderCount) {
/* 221 */         throw new MessageConstraintException("Maximum header count exceeded");
/*     */       }
/*     */     } 
/* 224 */     Header[] headers = new Header[headerLines.size()];
/* 225 */     for (int i = 0; i < headerLines.size(); i++) {
/* 226 */       CharArrayBuffer buffer = headerLines.get(i);
/*     */       try {
/* 228 */         headers[i] = parser.parseHeader(buffer);
/* 229 */       } catch (ParseException ex) {
/* 230 */         throw new ProtocolException(ex.getMessage());
/*     */       } 
/*     */     } 
/* 233 */     return headers;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract T parseHead(SessionInputBuffer paramSessionInputBuffer) throws IOException, HttpException, ParseException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T parse() throws IOException, HttpException {
/*     */     Header[] headers;
/*     */     T result;
/* 255 */     int st = this.state;
/* 256 */     switch (st) {
/*     */       case 0:
/*     */         try {
/* 259 */           this.message = parseHead(this.sessionBuffer);
/* 260 */         } catch (ParseException px) {
/* 261 */           throw new ProtocolException(px.getMessage(), px);
/*     */         } 
/* 263 */         this.state = 1;
/*     */       
/*     */       case 1:
/* 266 */         headers = parseHeaders(this.sessionBuffer, this.messageConstraints.getMaxHeaderCount(), this.messageConstraints.getMaxLineLength(), this.lineParser, this.headerLines);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 272 */         this.message.setHeaders(headers);
/* 273 */         result = this.message;
/* 274 */         this.message = null;
/* 275 */         this.headerLines.clear();
/* 276 */         this.state = 0;
/* 277 */         return result;
/*     */     } 
/* 279 */     throw new IllegalStateException("Inconsistent parser state");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\AbstractMessageParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */