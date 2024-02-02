/*     */ package com.sun.mail.smtp;
/*     */ 
/*     */ import java.io.InputStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SMTPMessage
/*     */   extends MimeMessage
/*     */ {
/*     */   public static final int NOTIFY_NEVER = -1;
/*     */   public static final int NOTIFY_SUCCESS = 1;
/*     */   public static final int NOTIFY_FAILURE = 2;
/*     */   public static final int NOTIFY_DELAY = 4;
/*     */   public static final int RETURN_FULL = 1;
/*     */   public static final int RETURN_HDRS = 2;
/*  77 */   private static final String[] returnOptionString = new String[] { null, "FULL", "HDRS" };
/*     */   
/*     */   private String envelopeFrom;
/*  80 */   private int notifyOptions = 0;
/*  81 */   private int returnOption = 0;
/*     */   private boolean sendPartial = false;
/*     */   private boolean allow8bitMIME = false;
/*  84 */   private String submitter = null;
/*  85 */   private String extension = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SMTPMessage(Session session) {
/*  94 */     super(session);
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
/*     */   public SMTPMessage(Session session, InputStream is) throws MessagingException {
/* 109 */     super(session, is);
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
/*     */   public SMTPMessage(MimeMessage source) throws MessagingException {
/* 124 */     super(source);
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
/*     */   public void setEnvelopeFrom(String from) {
/* 139 */     this.envelopeFrom = from;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getEnvelopeFrom() {
/* 148 */     return this.envelopeFrom;
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
/*     */   public void setNotifyOptions(int options) {
/* 164 */     if (options < -1 || options >= 8)
/* 165 */       throw new IllegalArgumentException("Bad return option"); 
/* 166 */     this.notifyOptions = options;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNotifyOptions() {
/* 175 */     return this.notifyOptions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String getDSNNotify() {
/* 183 */     if (this.notifyOptions == 0)
/* 184 */       return null; 
/* 185 */     if (this.notifyOptions == -1)
/* 186 */       return "NEVER"; 
/* 187 */     StringBuffer sb = new StringBuffer();
/* 188 */     if ((this.notifyOptions & 0x1) != 0)
/* 189 */       sb.append("SUCCESS"); 
/* 190 */     if ((this.notifyOptions & 0x2) != 0) {
/* 191 */       if (sb.length() != 0)
/* 192 */         sb.append(','); 
/* 193 */       sb.append("FAILURE");
/*     */     } 
/* 195 */     if ((this.notifyOptions & 0x4) != 0) {
/* 196 */       if (sb.length() != 0)
/* 197 */         sb.append(','); 
/* 198 */       sb.append("DELAY");
/*     */     } 
/* 200 */     return sb.toString();
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
/*     */   public void setReturnOption(int option) {
/* 214 */     if (option < 0 || option > 2)
/* 215 */       throw new IllegalArgumentException("Bad return option"); 
/* 216 */     this.returnOption = option;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getReturnOption() {
/* 225 */     return this.returnOption;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String getDSNRet() {
/* 233 */     return returnOptionString[this.returnOption];
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
/*     */   public void setAllow8bitMIME(boolean allow) {
/* 247 */     this.allow8bitMIME = allow;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getAllow8bitMIME() {
/* 256 */     return this.allow8bitMIME;
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
/*     */   public void setSendPartial(boolean partial) {
/* 271 */     this.sendPartial = partial;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getSendPartial() {
/* 280 */     return this.sendPartial;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSubmitter() {
/* 290 */     return this.submitter;
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
/*     */   public void setSubmitter(String submitter) {
/* 304 */     this.submitter = submitter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMailExtension() {
/* 315 */     return this.extension;
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
/*     */   public void setMailExtension(String extension) {
/* 340 */     this.extension = extension;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\smtp\SMTPMessage.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */