/*     */ package javax.mail.util;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.InputStream;
/*     */ import javax.mail.internet.SharedInputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SharedByteArrayInputStream
/*     */   extends ByteArrayInputStream
/*     */   implements SharedInputStream
/*     */ {
/*  59 */   protected int start = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SharedByteArrayInputStream(byte[] buf) {
/*  68 */     super(buf);
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
/*     */   public SharedByteArrayInputStream(byte[] buf, int offset, int length) {
/*  81 */     super(buf, offset, length);
/*  82 */     this.start = offset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getPosition() {
/*  92 */     return (this.pos - this.start);
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
/*     */   public InputStream newStream(long start, long end) {
/* 108 */     if (start < 0L)
/* 109 */       throw new IllegalArgumentException("start < 0"); 
/* 110 */     if (end == -1L)
/* 111 */       end = (this.count - this.start); 
/* 112 */     return new SharedByteArrayInputStream(this.buf, this.start + (int)start, (int)(end - start));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mai\\util\SharedByteArrayInputStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */