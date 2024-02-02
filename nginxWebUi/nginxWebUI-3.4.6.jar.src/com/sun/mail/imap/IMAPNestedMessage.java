/*     */ package com.sun.mail.imap;
/*     */ 
/*     */ import com.sun.mail.iap.ProtocolException;
/*     */ import com.sun.mail.imap.protocol.BODYSTRUCTURE;
/*     */ import com.sun.mail.imap.protocol.ENVELOPE;
/*     */ import com.sun.mail.imap.protocol.IMAPProtocol;
/*     */ import javax.mail.Flags;
/*     */ import javax.mail.FolderClosedException;
/*     */ import javax.mail.MessageRemovedException;
/*     */ import javax.mail.MessagingException;
/*     */ import javax.mail.MethodNotSupportedException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IMAPNestedMessage
/*     */   extends IMAPMessage
/*     */ {
/*     */   private IMAPMessage msg;
/*     */   
/*     */   IMAPNestedMessage(IMAPMessage m, BODYSTRUCTURE b, ENVELOPE e, String sid) {
/*  64 */     super(m._getSession());
/*  65 */     this.msg = m;
/*  66 */     this.bs = b;
/*  67 */     this.envelope = e;
/*  68 */     this.sectionId = sid;
/*  69 */     setPeek(m.getPeek());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected IMAPProtocol getProtocol() throws ProtocolException, FolderClosedException {
/*  78 */     return this.msg.getProtocol();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isREV1() throws FolderClosedException {
/*  85 */     return this.msg.isREV1();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object getMessageCacheLock() {
/*  93 */     return this.msg.getMessageCacheLock();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getSequenceNumber() {
/* 101 */     return this.msg.getSequenceNumber();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void checkExpunged() throws MessageRemovedException {
/* 109 */     this.msg.checkExpunged();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isExpunged() {
/* 117 */     return this.msg.isExpunged();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getFetchBlockSize() {
/* 124 */     return this.msg.getFetchBlockSize();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean ignoreBodyStructureSize() {
/* 131 */     return this.msg.ignoreBodyStructureSize();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSize() throws MessagingException {
/* 139 */     return this.bs.size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void setFlags(Flags flag, boolean set) throws MessagingException {
/* 148 */     throw new MethodNotSupportedException("Cannot set flags on this nested message");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\IMAPNestedMessage.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */