/*     */ package cn.hutool.core.io;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.Flushable;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.nio.CharBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AppendableWriter
/*     */   extends Writer
/*     */   implements Appendable
/*     */ {
/*     */   private final Appendable appendable;
/*     */   private final boolean flushable;
/*     */   private boolean closed;
/*     */   
/*     */   public AppendableWriter(Appendable appendable) {
/*  23 */     this.appendable = appendable;
/*  24 */     this.flushable = appendable instanceof Flushable;
/*  25 */     this.closed = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(char[] cbuf, int off, int len) throws IOException {
/*  30 */     checkNotClosed();
/*  31 */     this.appendable.append(CharBuffer.wrap(cbuf), off, off + len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int c) throws IOException {
/*  36 */     checkNotClosed();
/*  37 */     this.appendable.append((char)c);
/*     */   }
/*     */ 
/*     */   
/*     */   public Writer append(char c) throws IOException {
/*  42 */     checkNotClosed();
/*  43 */     this.appendable.append(c);
/*  44 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Writer append(CharSequence csq, int start, int end) throws IOException {
/*  49 */     checkNotClosed();
/*  50 */     this.appendable.append(csq, start, end);
/*  51 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Writer append(CharSequence csq) throws IOException {
/*  56 */     checkNotClosed();
/*  57 */     this.appendable.append(csq);
/*  58 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(String str, int off, int len) throws IOException {
/*  63 */     checkNotClosed();
/*  64 */     this.appendable.append(str, off, off + len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(String str) throws IOException {
/*  69 */     this.appendable.append(str);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(char[] cbuf) throws IOException {
/*  74 */     this.appendable.append(CharBuffer.wrap(cbuf));
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/*  79 */     checkNotClosed();
/*  80 */     if (this.flushable) {
/*  81 */       ((Flushable)this.appendable).flush();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkNotClosed() throws IOException {
/*  91 */     if (this.closed) {
/*  92 */       throw new IOException("Writer is closed!" + this);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  98 */     if (false == this.closed) {
/*  99 */       flush();
/* 100 */       if (this.appendable instanceof Closeable) {
/* 101 */         ((Closeable)this.appendable).close();
/*     */       }
/* 103 */       this.closed = true;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\AppendableWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */