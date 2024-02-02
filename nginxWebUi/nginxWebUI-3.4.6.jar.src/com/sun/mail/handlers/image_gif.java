/*     */ package com.sun.mail.handlers;
/*     */ 
/*     */ import java.awt.Image;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import javax.activation.ActivationDataFlavor;
/*     */ import javax.activation.DataContentHandler;
/*     */ import javax.activation.DataSource;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class image_gif
/*     */   implements DataContentHandler
/*     */ {
/*  53 */   private static ActivationDataFlavor myDF = new ActivationDataFlavor(Image.class, "image/gif", "GIF Image");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ActivationDataFlavor getDF() {
/*  59 */     return myDF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DataFlavor[] getTransferDataFlavors() {
/*  68 */     return new DataFlavor[] { (DataFlavor)getDF() };
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
/*     */   public Object getTransferData(DataFlavor df, DataSource ds) throws IOException {
/*  82 */     if (getDF().equals(df)) {
/*  83 */       return getContent(ds);
/*     */     }
/*  85 */     return null;
/*     */   }
/*     */   
/*     */   public Object getContent(DataSource ds) throws IOException {
/*  89 */     InputStream is = ds.getInputStream();
/*  90 */     int pos = 0;
/*     */     
/*  92 */     byte[] buf = new byte[1024];
/*     */     int count;
/*  94 */     while ((count = is.read(buf, pos, buf.length - pos)) != -1) {
/*  95 */       pos += count;
/*  96 */       if (pos >= buf.length) {
/*  97 */         int size = buf.length;
/*  98 */         if (size < 262144) {
/*  99 */           size += size;
/*     */         } else {
/* 101 */           size += 262144;
/* 102 */         }  byte[] tbuf = new byte[size];
/* 103 */         System.arraycopy(buf, 0, tbuf, 0, pos);
/* 104 */         buf = tbuf;
/*     */       } 
/*     */     } 
/* 107 */     Toolkit tk = Toolkit.getDefaultToolkit();
/* 108 */     return tk.createImage(buf, 0, pos);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeTo(Object obj, String type, OutputStream os) throws IOException {
/* 116 */     if (!(obj instanceof Image)) {
/* 117 */       throw new IOException("\"" + getDF().getMimeType() + "\" DataContentHandler requires Image object, " + "was given object of type " + obj.getClass().toString());
/*     */     }
/*     */ 
/*     */     
/* 121 */     throw new IOException(getDF().getMimeType() + " encoding not supported");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\handlers\image_gif.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */