/*    */ package com.sun.mail.imap;
/*    */ 
/*    */ import com.sun.mail.imap.protocol.BODYSTRUCTURE;
/*    */ import java.util.Vector;
/*    */ import javax.mail.BodyPart;
/*    */ import javax.mail.MessagingException;
/*    */ import javax.mail.MultipartDataSource;
/*    */ import javax.mail.internet.MimePart;
/*    */ import javax.mail.internet.MimePartDataSource;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class IMAPMultipartDataSource
/*    */   extends MimePartDataSource
/*    */   implements MultipartDataSource
/*    */ {
/*    */   private Vector parts;
/*    */   
/*    */   protected IMAPMultipartDataSource(MimePart part, BODYSTRUCTURE[] bs, String sectionId, IMAPMessage msg) {
/* 66 */     super(part);
/*    */     
/* 68 */     this.parts = new Vector(bs.length);
/* 69 */     for (int i = 0; i < bs.length; i++) {
/* 70 */       this.parts.addElement(new IMAPBodyPart(bs[i], (sectionId == null) ? Integer.toString(i + 1) : (sectionId + "." + Integer.toString(i + 1)), msg));
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getCount() {
/* 80 */     return this.parts.size();
/*    */   }
/*    */   
/*    */   public BodyPart getBodyPart(int index) throws MessagingException {
/* 84 */     return this.parts.elementAt(index);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\IMAPMultipartDataSource.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */