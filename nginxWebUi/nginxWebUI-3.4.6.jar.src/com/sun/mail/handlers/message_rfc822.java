/*     */ package com.sun.mail.handlers;
/*     */ 
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Properties;
/*     */ import javax.activation.ActivationDataFlavor;
/*     */ import javax.activation.DataContentHandler;
/*     */ import javax.activation.DataSource;
/*     */ import javax.mail.Message;
/*     */ import javax.mail.MessageAware;
/*     */ import javax.mail.MessageContext;
/*     */ import javax.mail.MessagingException;
/*     */ import javax.mail.Session;
/*     */ import javax.mail.internet.MimeMessage;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class message_rfc822
/*     */   implements DataContentHandler
/*     */ {
/*  58 */   ActivationDataFlavor ourDataFlavor = new ActivationDataFlavor(Message.class, "message/rfc822", "Message");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DataFlavor[] getTransferDataFlavors() {
/*  68 */     return new DataFlavor[] { (DataFlavor)this.ourDataFlavor };
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
/*     */   public Object getTransferData(DataFlavor df, DataSource ds) throws IOException {
/*  80 */     if (this.ourDataFlavor.equals(df)) {
/*  81 */       return getContent(ds);
/*     */     }
/*  83 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getContent(DataSource ds) throws IOException {
/*     */     try {
/*     */       Session session;
/*  93 */       if (ds instanceof MessageAware) {
/*  94 */         MessageContext mc = ((MessageAware)ds).getMessageContext();
/*  95 */         session = mc.getSession();
/*     */       
/*     */       }
/*     */       else {
/*     */ 
/*     */         
/* 101 */         session = Session.getDefaultInstance(new Properties(), null);
/*     */       } 
/* 103 */       return new MimeMessage(session, ds.getInputStream());
/* 104 */     } catch (MessagingException me) {
/* 105 */       throw new IOException("Exception creating MimeMessage in message/rfc822 DataContentHandler: " + me.toString());
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
/*     */   public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
/* 118 */     if (obj instanceof Message) {
/* 119 */       Message m = (Message)obj;
/*     */       try {
/* 121 */         m.writeTo(os);
/* 122 */       } catch (MessagingException me) {
/* 123 */         throw new IOException(me.toString());
/*     */       } 
/*     */     } else {
/*     */       
/* 127 */       throw new IOException("unsupported object");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\handlers\message_rfc822.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */