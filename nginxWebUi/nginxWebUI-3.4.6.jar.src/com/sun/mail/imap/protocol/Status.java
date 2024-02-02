/*     */ package com.sun.mail.imap.protocol;
/*     */ 
/*     */ import com.sun.mail.iap.ParsingException;
/*     */ import com.sun.mail.iap.Response;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Status
/*     */ {
/*  52 */   public String mbox = null;
/*  53 */   public int total = -1;
/*  54 */   public int recent = -1;
/*  55 */   public long uidnext = -1L;
/*  56 */   public long uidvalidity = -1L;
/*  57 */   public int unseen = -1;
/*     */   
/*  59 */   static final String[] standardItems = new String[] { "MESSAGES", "RECENT", "UNSEEN", "UIDNEXT", "UIDVALIDITY" };
/*     */ 
/*     */   
/*     */   public Status(Response r) throws ParsingException {
/*  63 */     this.mbox = r.readAtomString();
/*     */ 
/*     */ 
/*     */     
/*  67 */     StringBuffer buffer = new StringBuffer();
/*  68 */     boolean onlySpaces = true;
/*     */     
/*  70 */     while (r.peekByte() != 40 && r.peekByte() != 0) {
/*  71 */       char next = (char)r.readByte();
/*     */       
/*  73 */       buffer.append(next);
/*     */       
/*  75 */       if (next != ' ') {
/*  76 */         onlySpaces = false;
/*     */       }
/*     */     } 
/*     */     
/*  80 */     if (!onlySpaces) {
/*  81 */       this.mbox = (this.mbox + buffer).trim();
/*     */     }
/*     */     
/*  84 */     if (r.readByte() != 40) {
/*  85 */       throw new ParsingException("parse error in STATUS");
/*     */     }
/*     */     do {
/*  88 */       String attr = r.readAtom();
/*  89 */       if (attr.equalsIgnoreCase("MESSAGES"))
/*  90 */       { this.total = r.readNumber(); }
/*  91 */       else if (attr.equalsIgnoreCase("RECENT"))
/*  92 */       { this.recent = r.readNumber(); }
/*  93 */       else if (attr.equalsIgnoreCase("UIDNEXT"))
/*  94 */       { this.uidnext = r.readLong(); }
/*  95 */       else if (attr.equalsIgnoreCase("UIDVALIDITY"))
/*  96 */       { this.uidvalidity = r.readLong(); }
/*  97 */       else if (attr.equalsIgnoreCase("UNSEEN"))
/*  98 */       { this.unseen = r.readNumber(); } 
/*  99 */     } while (r.readByte() != 41);
/*     */   }
/*     */   
/*     */   public static void add(Status s1, Status s2) {
/* 103 */     if (s2.total != -1)
/* 104 */       s1.total = s2.total; 
/* 105 */     if (s2.recent != -1)
/* 106 */       s1.recent = s2.recent; 
/* 107 */     if (s2.uidnext != -1L)
/* 108 */       s1.uidnext = s2.uidnext; 
/* 109 */     if (s2.uidvalidity != -1L)
/* 110 */       s1.uidvalidity = s2.uidvalidity; 
/* 111 */     if (s2.unseen != -1)
/* 112 */       s1.unseen = s2.unseen; 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\Status.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */