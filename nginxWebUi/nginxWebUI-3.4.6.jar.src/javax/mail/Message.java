/*     */ package javax.mail;
/*     */ 
/*     */ import java.io.InvalidObjectException;
/*     */ import java.io.ObjectStreamException;
/*     */ import java.io.Serializable;
/*     */ import java.util.Date;
/*     */ import javax.mail.search.SearchTerm;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Message
/*     */   implements Part
/*     */ {
/*  89 */   protected int msgnum = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean expunged = false;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  99 */   protected Folder folder = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 104 */   protected Session session = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Message() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Message(Folder folder, int msgnum) {
/* 119 */     this.folder = folder;
/* 120 */     this.msgnum = msgnum;
/* 121 */     this.session = folder.store.session;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Message(Session session) {
/* 131 */     this.session = session;
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
/*     */   public abstract Address[] getFrom() throws MessagingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void setFrom() throws MessagingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void setFrom(Address paramAddress) throws MessagingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void addFrom(Address[] paramArrayOfAddress) throws MessagingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract Address[] getRecipients(RecipientType paramRecipientType) throws MessagingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class RecipientType
/*     */     implements Serializable
/*     */   {
/* 220 */     public static final RecipientType TO = new RecipientType("To");
/*     */ 
/*     */ 
/*     */     
/* 224 */     public static final RecipientType CC = new RecipientType("Cc");
/*     */ 
/*     */ 
/*     */     
/* 228 */     public static final RecipientType BCC = new RecipientType("Bcc");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected String type;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private static final long serialVersionUID = -7479791750606340008L;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected RecipientType(String type) {
/* 244 */       this.type = type;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected Object readResolve() throws ObjectStreamException {
/* 255 */       if (this.type.equals("To"))
/* 256 */         return TO; 
/* 257 */       if (this.type.equals("Cc"))
/* 258 */         return CC; 
/* 259 */       if (this.type.equals("Bcc")) {
/* 260 */         return BCC;
/*     */       }
/* 262 */       throw new InvalidObjectException("Attempt to resolve unknown RecipientType: " + this.type);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 267 */       return this.type;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Address[] getAllRecipients() throws MessagingException {
/* 305 */     Address[] to = getRecipients(RecipientType.TO);
/* 306 */     Address[] cc = getRecipients(RecipientType.CC);
/* 307 */     Address[] bcc = getRecipients(RecipientType.BCC);
/*     */     
/* 309 */     if (cc == null && bcc == null) {
/* 310 */       return to;
/*     */     }
/* 312 */     int numRecip = ((to != null) ? to.length : 0) + ((cc != null) ? cc.length : 0) + ((bcc != null) ? bcc.length : 0);
/*     */ 
/*     */ 
/*     */     
/* 316 */     Address[] addresses = new Address[numRecip];
/* 317 */     int pos = 0;
/* 318 */     if (to != null) {
/* 319 */       System.arraycopy(to, 0, addresses, pos, to.length);
/* 320 */       pos += to.length;
/*     */     } 
/* 322 */     if (cc != null) {
/* 323 */       System.arraycopy(cc, 0, addresses, pos, cc.length);
/* 324 */       pos += cc.length;
/*     */     } 
/* 326 */     if (bcc != null) {
/* 327 */       System.arraycopy(bcc, 0, addresses, pos, bcc.length);
/* 328 */       pos += bcc.length;
/*     */     } 
/* 330 */     return addresses;
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
/*     */   public abstract void setRecipients(RecipientType paramRecipientType, Address[] paramArrayOfAddress) throws MessagingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRecipient(RecipientType type, Address address) throws MessagingException {
/* 364 */     Address[] a = new Address[1];
/* 365 */     a[0] = address;
/* 366 */     setRecipients(type, a);
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
/*     */   public abstract void addRecipients(RecipientType paramRecipientType, Address[] paramArrayOfAddress) throws MessagingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addRecipient(RecipientType type, Address address) throws MessagingException {
/* 398 */     Address[] a = new Address[1];
/* 399 */     a[0] = address;
/* 400 */     addRecipients(type, a);
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
/*     */   public Address[] getReplyTo() throws MessagingException {
/* 420 */     return getFrom();
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
/*     */ 
/*     */ 
/*     */   
/*     */   public void setReplyTo(Address[] addresses) throws MessagingException {
/* 444 */     throw new MethodNotSupportedException("setReplyTo not supported");
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
/*     */   public abstract String getSubject() throws MessagingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void setSubject(String paramString) throws MessagingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract Date getSentDate() throws MessagingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void setSentDate(Date paramDate) throws MessagingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract Date getReceivedDate() throws MessagingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract Flags getFlags() throws MessagingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSet(Flags.Flag flag) throws MessagingException {
/* 531 */     return getFlags().contains(flag);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void setFlags(Flags paramFlags, boolean paramBoolean) throws MessagingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFlag(Flags.Flag flag, boolean set) throws MessagingException {
/* 577 */     Flags f = new Flags(flag);
/* 578 */     setFlags(f, set);
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
/*     */   public int getMessageNumber() {
/* 595 */     return this.msgnum;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setMessageNumber(int msgnum) {
/* 603 */     this.msgnum = msgnum;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Folder getFolder() {
/* 614 */     return this.folder;
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
/*     */   public boolean isExpunged() {
/* 635 */     return this.expunged;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setExpunged(boolean expunged) {
/* 645 */     this.expunged = expunged;
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
/*     */   
/*     */   public abstract Message reply(boolean paramBoolean) throws MessagingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void saveChanges() throws MessagingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean match(SearchTerm term) throws MessagingException {
/* 705 */     return term.match(this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\Message.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */