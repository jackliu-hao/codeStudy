/*     */ package com.sun.mail.handlers;
/*     */ 
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import javax.activation.ActivationDataFlavor;
/*     */ import javax.activation.DataContentHandler;
/*     */ import javax.activation.DataSource;
/*     */ import javax.mail.MessagingException;
/*     */ import javax.mail.internet.MimeMultipart;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class multipart_mixed
/*     */   implements DataContentHandler
/*     */ {
/*  51 */   private ActivationDataFlavor myDF = new ActivationDataFlavor(MimeMultipart.class, "multipart/mixed", "Multipart");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DataFlavor[] getTransferDataFlavors() {
/*  62 */     return new DataFlavor[] { (DataFlavor)this.myDF };
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
/*  76 */     if (this.myDF.equals(df)) {
/*  77 */       return getContent(ds);
/*     */     }
/*  79 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getContent(DataSource ds) throws IOException {
/*     */     try {
/*  87 */       return new MimeMultipart(ds);
/*  88 */     } catch (MessagingException e) {
/*  89 */       IOException ioex = new IOException("Exception while constructing MimeMultipart");
/*     */       
/*  91 */       ioex.initCause((Throwable)e);
/*  92 */       throw ioex;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
/* 101 */     if (obj instanceof MimeMultipart)
/*     */       try {
/* 103 */         ((MimeMultipart)obj).writeTo(os);
/* 104 */       } catch (MessagingException e) {
/* 105 */         throw new IOException(e.toString());
/*     */       }  
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\handlers\multipart_mixed.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */