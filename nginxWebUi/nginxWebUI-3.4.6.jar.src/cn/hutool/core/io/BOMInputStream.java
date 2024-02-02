/*     */ package cn.hutool.core.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PushbackInputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BOMInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private final PushbackInputStream in;
/*     */   private boolean isInited = false;
/*     */   private final String defaultCharset;
/*     */   private String charset;
/*     */   private static final int BOM_SIZE = 4;
/*     */   
/*     */   public BOMInputStream(InputStream in) {
/*  47 */     this(in, "UTF-8");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BOMInputStream(InputStream in, String defaultCharset) {
/*  57 */     this.in = new PushbackInputStream(in, 4);
/*  58 */     this.defaultCharset = defaultCharset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDefaultCharset() {
/*  68 */     return this.defaultCharset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCharset() {
/*  77 */     if (false == this.isInited) {
/*     */       try {
/*  79 */         init();
/*  80 */       } catch (IOException ex) {
/*  81 */         throw new IORuntimeException(ex);
/*     */       } 
/*     */     }
/*  84 */     return this.charset;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  89 */     this.isInited = true;
/*  90 */     this.in.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*  95 */     this.isInited = true;
/*  96 */     return this.in.read();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void init() throws IOException {
/*     */     int unread;
/* 105 */     if (this.isInited) {
/*     */       return;
/*     */     }
/*     */     
/* 109 */     byte[] bom = new byte[4];
/*     */     
/* 111 */     int n = this.in.read(bom, 0, bom.length);
/*     */     
/* 113 */     if (bom[0] == 0 && bom[1] == 0 && bom[2] == -2 && bom[3] == -1) {
/* 114 */       this.charset = "UTF-32BE";
/* 115 */       unread = n - 4;
/* 116 */     } else if (bom[0] == -1 && bom[1] == -2 && bom[2] == 0 && bom[3] == 0) {
/* 117 */       this.charset = "UTF-32LE";
/* 118 */       unread = n - 4;
/* 119 */     } else if (bom[0] == -17 && bom[1] == -69 && bom[2] == -65) {
/* 120 */       this.charset = "UTF-8";
/* 121 */       unread = n - 3;
/* 122 */     } else if (bom[0] == -2 && bom[1] == -1) {
/* 123 */       this.charset = "UTF-16BE";
/* 124 */       unread = n - 2;
/* 125 */     } else if (bom[0] == -1 && bom[1] == -2) {
/* 126 */       this.charset = "UTF-16LE";
/* 127 */       unread = n - 2;
/*     */     } else {
/*     */       
/* 130 */       this.charset = this.defaultCharset;
/* 131 */       unread = n;
/*     */     } 
/*     */     
/* 134 */     if (unread > 0) {
/* 135 */       this.in.unread(bom, n - unread, unread);
/*     */     }
/*     */     
/* 138 */     this.isInited = true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\BOMInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */