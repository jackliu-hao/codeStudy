/*     */ package org.apache.http.impl.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HeaderIterator;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpMessage;
/*     */ import org.apache.http.io.HttpMessageWriter;
/*     */ import org.apache.http.io.SessionOutputBuffer;
/*     */ import org.apache.http.message.BasicLineFormatter;
/*     */ import org.apache.http.message.LineFormatter;
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
/*     */ public abstract class AbstractMessageWriter<T extends HttpMessage>
/*     */   implements HttpMessageWriter<T>
/*     */ {
/*     */   protected final SessionOutputBuffer sessionBuffer;
/*     */   protected final CharArrayBuffer lineBuf;
/*     */   protected final LineFormatter lineFormatter;
/*     */   
/*     */   @Deprecated
/*     */   public AbstractMessageWriter(SessionOutputBuffer buffer, LineFormatter formatter, HttpParams params) {
/*  72 */     Args.notNull(buffer, "Session input buffer");
/*  73 */     this.sessionBuffer = buffer;
/*  74 */     this.lineBuf = new CharArrayBuffer(128);
/*  75 */     this.lineFormatter = (formatter != null) ? formatter : (LineFormatter)BasicLineFormatter.INSTANCE;
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
/*     */   public AbstractMessageWriter(SessionOutputBuffer buffer, LineFormatter formatter) {
/*  91 */     this.sessionBuffer = (SessionOutputBuffer)Args.notNull(buffer, "Session input buffer");
/*  92 */     this.lineFormatter = (formatter != null) ? formatter : (LineFormatter)BasicLineFormatter.INSTANCE;
/*  93 */     this.lineBuf = new CharArrayBuffer(128);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void writeHeadLine(T paramT) throws IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(T message) throws IOException, HttpException {
/* 107 */     Args.notNull(message, "HTTP message");
/* 108 */     writeHeadLine(message);
/* 109 */     for (HeaderIterator it = message.headerIterator(); it.hasNext(); ) {
/* 110 */       Header header = it.nextHeader();
/* 111 */       this.sessionBuffer.writeLine(this.lineFormatter.formatHeader(this.lineBuf, header));
/*     */     } 
/*     */     
/* 114 */     this.lineBuf.clear();
/* 115 */     this.sessionBuffer.writeLine(this.lineBuf);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\AbstractMessageWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */