/*     */ package javax.servlet;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ServletInputStream
/*     */   extends InputStream
/*     */ {
/*     */   public int readLine(byte[] b, int off, int len) throws IOException {
/*  88 */     if (len <= 0) {
/*  89 */       return 0;
/*     */     }
/*  91 */     int count = 0;
/*     */     int c;
/*  93 */     while ((c = read()) != -1) {
/*  94 */       b[off++] = (byte)c;
/*  95 */       count++;
/*  96 */       if (c == 10 || count == len) {
/*     */         break;
/*     */       }
/*     */     } 
/* 100 */     return (count > 0) ? count : -1;
/*     */   }
/*     */   
/*     */   public abstract boolean isFinished();
/*     */   
/*     */   public abstract boolean isReady();
/*     */   
/*     */   public abstract void setReadListener(ReadListener paramReadListener);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\ServletInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */