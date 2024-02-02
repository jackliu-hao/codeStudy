/*     */ package javax.mail;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Vector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Multipart
/*     */ {
/*  72 */   protected Vector parts = new Vector();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  78 */   protected String contentType = "multipart/mixed";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Part parent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected synchronized void setMultipartDataSource(MultipartDataSource mp) throws MessagingException {
/* 109 */     this.contentType = mp.getContentType();
/*     */     
/* 111 */     int count = mp.getCount();
/* 112 */     for (int i = 0; i < count; i++) {
/* 113 */       addBodyPart(mp.getBodyPart(i));
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
/*     */   public synchronized String getContentType() {
/* 126 */     return this.contentType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized int getCount() throws MessagingException {
/* 136 */     if (this.parts == null) {
/* 137 */       return 0;
/*     */     }
/* 139 */     return this.parts.size();
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
/*     */   public synchronized BodyPart getBodyPart(int index) throws MessagingException {
/* 153 */     if (this.parts == null) {
/* 154 */       throw new IndexOutOfBoundsException("No such BodyPart");
/*     */     }
/* 156 */     return this.parts.elementAt(index);
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
/*     */   public synchronized boolean removeBodyPart(BodyPart part) throws MessagingException {
/* 172 */     if (this.parts == null) {
/* 173 */       throw new MessagingException("No such body part");
/*     */     }
/* 175 */     boolean ret = this.parts.removeElement(part);
/* 176 */     part.setParent(null);
/* 177 */     return ret;
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
/*     */   public synchronized void removeBodyPart(int index) throws MessagingException {
/* 194 */     if (this.parts == null) {
/* 195 */       throw new IndexOutOfBoundsException("No such BodyPart");
/*     */     }
/* 197 */     BodyPart part = this.parts.elementAt(index);
/* 198 */     this.parts.removeElementAt(index);
/* 199 */     part.setParent(null);
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
/*     */   public synchronized void addBodyPart(BodyPart part) throws MessagingException {
/* 214 */     if (this.parts == null) {
/* 215 */       this.parts = new Vector();
/*     */     }
/* 217 */     this.parts.addElement(part);
/* 218 */     part.setParent(this);
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
/*     */   public synchronized void addBodyPart(BodyPart part, int index) throws MessagingException {
/* 237 */     if (this.parts == null) {
/* 238 */       this.parts = new Vector();
/*     */     }
/* 240 */     this.parts.insertElementAt(part, index);
/* 241 */     part.setParent(this);
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
/*     */   public abstract void writeTo(OutputStream paramOutputStream) throws IOException, MessagingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Part getParent() {
/* 262 */     return this.parent;
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
/*     */   public synchronized void setParent(Part parent) {
/* 275 */     this.parent = parent;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\Multipart.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */