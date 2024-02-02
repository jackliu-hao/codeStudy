/*     */ package com.sun.mail.pop3;
/*     */ 
/*     */ import javax.mail.Flags;
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
/*     */ public class DefaultFolder
/*     */   extends Folder
/*     */ {
/*     */   DefaultFolder(POP3Store store) {
/*  53 */     super(store);
/*     */   }
/*     */   
/*     */   public String getName() {
/*  57 */     return "";
/*     */   }
/*     */   
/*     */   public String getFullName() {
/*  61 */     return "";
/*     */   }
/*     */   
/*     */   public Folder getParent() {
/*  65 */     return null;
/*     */   }
/*     */   
/*     */   public boolean exists() {
/*  69 */     return true;
/*     */   }
/*     */   
/*     */   public Folder[] list(String pattern) throws MessagingException {
/*  73 */     Folder[] f = { getInbox() };
/*  74 */     return f;
/*     */   }
/*     */   
/*     */   public char getSeparator() {
/*  78 */     return '/';
/*     */   }
/*     */   
/*     */   public int getType() {
/*  82 */     return 2;
/*     */   }
/*     */   
/*     */   public boolean create(int type) throws MessagingException {
/*  86 */     return false;
/*     */   }
/*     */   
/*     */   public boolean hasNewMessages() throws MessagingException {
/*  90 */     return false;
/*     */   }
/*     */   
/*     */   public Folder getFolder(String name) throws MessagingException {
/*  94 */     if (!name.equalsIgnoreCase("INBOX")) {
/*  95 */       throw new MessagingException("only INBOX supported");
/*     */     }
/*  97 */     return getInbox();
/*     */   }
/*     */ 
/*     */   
/*     */   protected Folder getInbox() throws MessagingException {
/* 102 */     return getStore().getFolder("INBOX");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean delete(boolean recurse) throws MessagingException {
/* 107 */     throw new MethodNotSupportedException("delete");
/*     */   }
/*     */   
/*     */   public boolean renameTo(Folder f) throws MessagingException {
/* 111 */     throw new MethodNotSupportedException("renameTo");
/*     */   }
/*     */   
/*     */   public void open(int mode) throws MessagingException {
/* 115 */     throw new MethodNotSupportedException("open");
/*     */   }
/*     */   
/*     */   public void close(boolean expunge) throws MessagingException {
/* 119 */     throw new MethodNotSupportedException("close");
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/* 123 */     return false;
/*     */   }
/*     */   
/*     */   public Flags getPermanentFlags() {
/* 127 */     return new Flags();
/*     */   }
/*     */   
/*     */   public int getMessageCount() throws MessagingException {
/* 131 */     return 0;
/*     */   }
/*     */   
/*     */   public Message getMessage(int msgno) throws MessagingException {
/* 135 */     throw new MethodNotSupportedException("getMessage");
/*     */   }
/*     */   
/*     */   public void appendMessages(Message[] msgs) throws MessagingException {
/* 139 */     throw new MethodNotSupportedException("Append not supported");
/*     */   }
/*     */   
/*     */   public Message[] expunge() throws MessagingException {
/* 143 */     throw new MethodNotSupportedException("expunge");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\pop3\DefaultFolder.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */