/*     */ package com.sun.mail.imap.protocol;
/*     */ 
/*     */ import com.sun.mail.iap.ParsingException;
/*     */ import java.text.FieldPosition;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
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
/*     */ public class INTERNALDATE
/*     */   implements Item
/*     */ {
/*  63 */   static final char[] name = new char[] { 'I', 'N', 'T', 'E', 'R', 'N', 'A', 'L', 'D', 'A', 'T', 'E' };
/*     */ 
/*     */ 
/*     */   
/*     */   public int msgno;
/*     */ 
/*     */ 
/*     */   
/*     */   protected Date date;
/*     */ 
/*     */ 
/*     */   
/*  75 */   private static MailDateFormat mailDateFormat = new MailDateFormat();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public INTERNALDATE(FetchResponse r) throws ParsingException {
/*  81 */     this.msgno = r.getNumber();
/*  82 */     r.skipSpaces();
/*  83 */     String s = r.readString();
/*  84 */     if (s == null)
/*  85 */       throw new ParsingException("INTERNALDATE is NIL"); 
/*     */     try {
/*  87 */       this.date = mailDateFormat.parse(s);
/*  88 */     } catch (ParseException pex) {
/*  89 */       throw new ParsingException("INTERNALDATE parse error");
/*     */     } 
/*     */   }
/*     */   
/*     */   public Date getDate() {
/*  94 */     return this.date;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  99 */   private static SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss ", Locale.US);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String format(Date d) {
/* 119 */     StringBuffer sb = new StringBuffer();
/* 120 */     synchronized (df) {
/* 121 */       df.format(d, sb, new FieldPosition(0));
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 126 */     int rawOffsetInMins = -d.getTimezoneOffset();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 134 */     if (rawOffsetInMins < 0) {
/* 135 */       sb.append('-');
/* 136 */       rawOffsetInMins = -rawOffsetInMins;
/*     */     } else {
/* 138 */       sb.append('+');
/*     */     } 
/* 140 */     int offsetInHrs = rawOffsetInMins / 60;
/* 141 */     int offsetInMins = rawOffsetInMins % 60;
/*     */     
/* 143 */     sb.append(Character.forDigit(offsetInHrs / 10, 10));
/* 144 */     sb.append(Character.forDigit(offsetInHrs % 10, 10));
/* 145 */     sb.append(Character.forDigit(offsetInMins / 10, 10));
/* 146 */     sb.append(Character.forDigit(offsetInMins % 10, 10));
/*     */     
/* 148 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\INTERNALDATE.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */