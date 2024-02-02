/*     */ package com.sun.mail.smtp;
/*     */ 
/*     */ import com.sun.mail.util.CRLFOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SMTPOutputStream
/*     */   extends CRLFOutputStream
/*     */ {
/*     */   public SMTPOutputStream(OutputStream os) {
/*  57 */     super(os);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/*  63 */     if ((this.lastb == 10 || this.lastb == 13 || this.lastb == -1) && b == 46) {
/*  64 */       this.out.write(46);
/*     */     }
/*     */     
/*  67 */     super.write(b);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/*  74 */     int lastc = (this.lastb == -1) ? 10 : this.lastb;
/*  75 */     int start = off;
/*     */     
/*  77 */     len += off;
/*  78 */     for (int i = off; i < len; i++) {
/*  79 */       if ((lastc == 10 || lastc == 13) && b[i] == 46) {
/*  80 */         super.write(b, start, i - start);
/*  81 */         this.out.write(46);
/*  82 */         start = i;
/*     */       } 
/*  84 */       lastc = b[i];
/*     */     } 
/*  86 */     if (len - start > 0) {
/*  87 */       super.write(b, start, len - start);
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
/*     */   public void flush() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ensureAtBOL() throws IOException {
/* 111 */     if (!this.atBOL)
/* 112 */       writeln(); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\smtp\SMTPOutputStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */