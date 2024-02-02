/*     */ package javax.mail.internet;
/*     */ 
/*     */ import com.sun.mail.util.FolderClosedIOException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.UnknownServiceException;
/*     */ import javax.activation.DataSource;
/*     */ import javax.mail.FolderClosedException;
/*     */ import javax.mail.MessageAware;
/*     */ import javax.mail.MessageContext;
/*     */ import javax.mail.MessagingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MimePartDataSource
/*     */   implements DataSource, MessageAware
/*     */ {
/*     */   protected MimePart part;
/*     */   private MessageContext context;
/*     */   
/*     */   public MimePartDataSource(MimePart part) {
/*  73 */     this.part = part;
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
/*     */   public InputStream getInputStream() throws IOException {
/*     */     try {
/*     */       InputStream is;
/*  96 */       if (this.part instanceof MimeBodyPart) {
/*  97 */         is = ((MimeBodyPart)this.part).getContentStream();
/*  98 */       } else if (this.part instanceof MimeMessage) {
/*  99 */         is = ((MimeMessage)this.part).getContentStream();
/*     */       } else {
/* 101 */         throw new MessagingException("Unknown part");
/*     */       } 
/* 103 */       String encoding = MimeBodyPart.restrictEncoding(this.part, this.part.getEncoding());
/*     */       
/* 105 */       if (encoding != null) {
/* 106 */         return MimeUtility.decode(is, encoding);
/*     */       }
/* 108 */       return is;
/* 109 */     } catch (FolderClosedException fex) {
/* 110 */       throw new FolderClosedIOException(fex.getFolder(), fex.getMessage());
/*     */     }
/* 112 */     catch (MessagingException mex) {
/* 113 */       throw new IOException(mex.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OutputStream getOutputStream() throws IOException {
/* 123 */     throw new UnknownServiceException("Writing not supported");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getContentType() {
/*     */     try {
/* 134 */       return this.part.getContentType();
/* 135 */     } catch (MessagingException mex) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 140 */       return "application/octet-stream";
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*     */     try {
/* 151 */       if (this.part instanceof MimeBodyPart)
/* 152 */         return ((MimeBodyPart)this.part).getFileName(); 
/* 153 */     } catch (MessagingException mex) {}
/*     */ 
/*     */     
/* 156 */     return "";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized MessageContext getMessageContext() {
/* 164 */     if (this.context == null)
/* 165 */       this.context = new MessageContext(this.part); 
/* 166 */     return this.context;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\internet\MimePartDataSource.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */