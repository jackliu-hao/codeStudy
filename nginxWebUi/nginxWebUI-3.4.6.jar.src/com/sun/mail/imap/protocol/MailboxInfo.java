/*     */ package com.sun.mail.imap.protocol;
/*     */ 
/*     */ import com.sun.mail.iap.ParsingException;
/*     */ import com.sun.mail.iap.Response;
/*     */ import javax.mail.Flags;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MailboxInfo
/*     */ {
/*  53 */   public Flags availableFlags = null;
/*  54 */   public Flags permanentFlags = null;
/*  55 */   public int total = -1;
/*  56 */   public int recent = -1;
/*  57 */   public int first = -1;
/*  58 */   public long uidvalidity = -1L;
/*  59 */   public long uidnext = -1L;
/*     */   public int mode;
/*     */   
/*     */   public MailboxInfo(Response[] r) throws ParsingException {
/*  63 */     for (int i = 0; i < r.length; i++) {
/*  64 */       if (r[i] != null && r[i] instanceof IMAPResponse) {
/*     */ 
/*     */         
/*  67 */         IMAPResponse ir = (IMAPResponse)r[i];
/*     */         
/*  69 */         if (ir.keyEquals("EXISTS")) {
/*  70 */           this.total = ir.getNumber();
/*  71 */           r[i] = null;
/*     */         }
/*  73 */         else if (ir.keyEquals("RECENT")) {
/*  74 */           this.recent = ir.getNumber();
/*  75 */           r[i] = null;
/*     */         }
/*  77 */         else if (ir.keyEquals("FLAGS")) {
/*  78 */           this.availableFlags = new FLAGS(ir);
/*  79 */           r[i] = null;
/*     */         }
/*  81 */         else if (ir.isUnTagged() && ir.isOK()) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  88 */           ir.skipSpaces();
/*     */           
/*  90 */           if (ir.readByte() != 91) {
/*  91 */             ir.reset();
/*     */           }
/*     */           else {
/*     */             
/*  95 */             boolean handled = true;
/*  96 */             String s = ir.readAtom();
/*  97 */             if (s.equalsIgnoreCase("UNSEEN")) {
/*  98 */               this.first = ir.readNumber();
/*  99 */             } else if (s.equalsIgnoreCase("UIDVALIDITY")) {
/* 100 */               this.uidvalidity = ir.readLong();
/* 101 */             } else if (s.equalsIgnoreCase("PERMANENTFLAGS")) {
/* 102 */               this.permanentFlags = new FLAGS(ir);
/* 103 */             } else if (s.equalsIgnoreCase("UIDNEXT")) {
/* 104 */               this.uidnext = ir.readLong();
/*     */             } else {
/* 106 */               handled = false;
/*     */             } 
/* 108 */             if (handled) {
/* 109 */               r[i] = null;
/*     */             } else {
/* 111 */               ir.reset();
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 120 */     if (this.permanentFlags == null)
/* 121 */       if (this.availableFlags != null) {
/* 122 */         this.permanentFlags = new Flags(this.availableFlags);
/*     */       } else {
/* 124 */         this.permanentFlags = new Flags();
/*     */       }  
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\MailboxInfo.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */