/*     */ package javax.mail.internet;
/*     */ 
/*     */ import com.sun.mail.util.LineOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Enumeration;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PreencodedMimeBodyPart
/*     */   extends MimeBodyPart
/*     */ {
/*     */   private String encoding;
/*     */   
/*     */   public PreencodedMimeBodyPart(String encoding) {
/*  72 */     this.encoding = encoding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getEncoding() throws MessagingException {
/*  80 */     return this.encoding;
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
/*     */   public void writeTo(OutputStream os) throws IOException, MessagingException {
/*  96 */     LineOutputStream los = null;
/*  97 */     if (os instanceof LineOutputStream) {
/*  98 */       los = (LineOutputStream)os;
/*     */     } else {
/* 100 */       los = new LineOutputStream(os);
/*     */     } 
/*     */ 
/*     */     
/* 104 */     Enumeration hdrLines = getAllHeaderLines();
/* 105 */     while (hdrLines.hasMoreElements()) {
/* 106 */       los.writeln(hdrLines.nextElement());
/*     */     }
/*     */     
/* 109 */     los.writeln();
/*     */ 
/*     */     
/* 112 */     getDataHandler().writeTo(os);
/* 113 */     os.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void updateHeaders() throws MessagingException {
/* 121 */     super.updateHeaders();
/* 122 */     MimeBodyPart.setEncoding(this, this.encoding);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\internet\PreencodedMimeBodyPart.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */