/*     */ package com.sun.mail.imap.protocol;
/*     */ 
/*     */ import com.sun.mail.iap.ParsingException;
/*     */ import com.sun.mail.iap.Response;
/*     */ import java.util.Date;
/*     */ import java.util.Vector;
/*     */ import javax.mail.internet.InternetAddress;
/*     */ import javax.mail.internet.MailDateFormat;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ENVELOPE
/*     */   implements Item
/*     */ {
/*  63 */   static final char[] name = new char[] { 'E', 'N', 'V', 'E', 'L', 'O', 'P', 'E' };
/*     */   
/*     */   public int msgno;
/*  66 */   public Date date = null;
/*     */   
/*     */   public String subject;
/*     */   
/*     */   public InternetAddress[] from;
/*     */   public InternetAddress[] sender;
/*     */   public InternetAddress[] replyTo;
/*     */   public InternetAddress[] to;
/*     */   public InternetAddress[] cc;
/*     */   public InternetAddress[] bcc;
/*     */   public String inReplyTo;
/*     */   public String messageId;
/*  78 */   private static MailDateFormat mailDateFormat = new MailDateFormat();
/*     */   
/*     */   public ENVELOPE(FetchResponse r) throws ParsingException {
/*  81 */     this.msgno = r.getNumber();
/*     */     
/*  83 */     r.skipSpaces();
/*     */     
/*  85 */     if (r.readByte() != 40) {
/*  86 */       throw new ParsingException("ENVELOPE parse error");
/*     */     }
/*  88 */     String s = r.readString();
/*  89 */     if (s != null) {
/*     */       try {
/*  91 */         this.date = mailDateFormat.parse(s);
/*  92 */       } catch (Exception pex) {}
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 100 */     this.subject = r.readString();
/* 101 */     this.from = parseAddressList(r);
/* 102 */     this.sender = parseAddressList(r);
/* 103 */     this.replyTo = parseAddressList(r);
/* 104 */     this.to = parseAddressList(r);
/* 105 */     this.cc = parseAddressList(r);
/* 106 */     this.bcc = parseAddressList(r);
/* 107 */     this.inReplyTo = r.readString();
/* 108 */     this.messageId = r.readString();
/*     */     
/* 110 */     if (r.readByte() != 41) {
/* 111 */       throw new ParsingException("ENVELOPE parse error");
/*     */     }
/*     */   }
/*     */   
/*     */   private InternetAddress[] parseAddressList(Response r) throws ParsingException {
/* 116 */     r.skipSpaces();
/*     */     
/* 118 */     byte b = r.readByte();
/* 119 */     if (b == 40) {
/* 120 */       Vector v = new Vector();
/*     */       
/*     */       do {
/* 123 */         IMAPAddress iMAPAddress = new IMAPAddress(r);
/*     */         
/* 125 */         if (iMAPAddress.isEndOfGroup())
/* 126 */           continue;  v.addElement(iMAPAddress);
/* 127 */       } while (r.peekByte() != 41);
/*     */ 
/*     */       
/* 130 */       r.skip(1);
/*     */       
/* 132 */       InternetAddress[] a = new InternetAddress[v.size()];
/* 133 */       v.copyInto((Object[])a);
/* 134 */       return a;
/* 135 */     }  if (b == 78 || b == 110) {
/* 136 */       r.skip(2);
/* 137 */       return null;
/*     */     } 
/* 139 */     throw new ParsingException("ADDRESS parse error");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\ENVELOPE.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */