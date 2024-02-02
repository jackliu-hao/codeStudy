/*     */ package com.sun.mail.imap;
/*     */ 
/*     */ import com.sun.mail.iap.ProtocolException;
/*     */ import com.sun.mail.imap.protocol.IMAPProtocol;
/*     */ import com.sun.mail.imap.protocol.ListInfo;
/*     */ import javax.mail.Folder;
/*     */ import javax.mail.Message;
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
/*     */ public class DefaultFolder
/*     */   extends IMAPFolder
/*     */ {
/*     */   protected DefaultFolder(IMAPStore store) {
/*  58 */     super("", '￿', store, (Boolean)null);
/*  59 */     this.exists = true;
/*  60 */     this.type = 2;
/*     */   }
/*     */   
/*     */   public synchronized String getName() {
/*  64 */     return this.fullName;
/*     */   }
/*     */   
/*     */   public Folder getParent() {
/*  68 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized Folder[] list(final String pattern) throws MessagingException {
/*  73 */     ListInfo[] li = null;
/*     */     
/*  75 */     li = (ListInfo[])doCommand(new IMAPFolder.ProtocolCommand() { private final String val$pattern;
/*     */           public Object doCommand(IMAPProtocol p) throws ProtocolException {
/*  77 */             return p.list("", pattern);
/*     */           }
/*     */           private final DefaultFolder this$0; }
/*     */       );
/*  81 */     if (li == null) {
/*  82 */       return new Folder[0];
/*     */     }
/*  84 */     IMAPFolder[] folders = new IMAPFolder[li.length];
/*  85 */     for (int i = 0; i < folders.length; i++)
/*  86 */       folders[i] = ((IMAPStore)this.store).newIMAPFolder(li[i]); 
/*  87 */     return (Folder[])folders;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized Folder[] listSubscribed(final String pattern) throws MessagingException {
/*  92 */     ListInfo[] li = null;
/*     */     
/*  94 */     li = (ListInfo[])doCommand(new IMAPFolder.ProtocolCommand() { private final String val$pattern; private final DefaultFolder this$0;
/*     */           public Object doCommand(IMAPProtocol p) throws ProtocolException {
/*  96 */             return p.lsub("", pattern);
/*     */           } }
/*     */       );
/*     */     
/* 100 */     if (li == null) {
/* 101 */       return new Folder[0];
/*     */     }
/* 103 */     IMAPFolder[] folders = new IMAPFolder[li.length];
/* 104 */     for (int i = 0; i < folders.length; i++)
/* 105 */       folders[i] = ((IMAPStore)this.store).newIMAPFolder(li[i]); 
/* 106 */     return (Folder[])folders;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasNewMessages() throws MessagingException {
/* 111 */     return false;
/*     */   }
/*     */   
/*     */   public Folder getFolder(String name) throws MessagingException {
/* 115 */     return ((IMAPStore)this.store).newIMAPFolder(name, '￿');
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean delete(boolean recurse) throws MessagingException {
/* 120 */     throw new MethodNotSupportedException("Cannot delete Default Folder");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean renameTo(Folder f) throws MessagingException {
/* 125 */     throw new MethodNotSupportedException("Cannot rename Default Folder");
/*     */   }
/*     */ 
/*     */   
/*     */   public void appendMessages(Message[] msgs) throws MessagingException {
/* 130 */     throw new MethodNotSupportedException("Cannot append to Default Folder");
/*     */   }
/*     */ 
/*     */   
/*     */   public Message[] expunge() throws MessagingException {
/* 135 */     throw new MethodNotSupportedException("Cannot expunge Default Folder");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\DefaultFolder.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */