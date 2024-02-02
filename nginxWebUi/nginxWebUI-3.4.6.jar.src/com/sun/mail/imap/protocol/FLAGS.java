/*     */ package com.sun.mail.imap.protocol;
/*     */ 
/*     */ import com.sun.mail.iap.ParsingException;
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
/*     */ 
/*     */ public class FLAGS
/*     */   extends Flags
/*     */   implements Item
/*     */ {
/*  55 */   static final char[] name = new char[] { 'F', 'L', 'A', 'G', 'S' };
/*     */ 
/*     */   
/*     */   public int msgno;
/*     */   
/*     */   private static final long serialVersionUID = 439049847053756670L;
/*     */ 
/*     */   
/*     */   public FLAGS(IMAPResponse r) throws ParsingException {
/*  64 */     this.msgno = r.getNumber();
/*     */     
/*  66 */     r.skipSpaces();
/*  67 */     String[] flags = r.readSimpleList();
/*  68 */     if (flags != null)
/*  69 */       for (int i = 0; i < flags.length; i++) {
/*  70 */         String s = flags[i];
/*  71 */         if (s.length() >= 2 && s.charAt(0) == '\\') {
/*  72 */           switch (Character.toUpperCase(s.charAt(1))) {
/*     */             case 'S':
/*  74 */               add(Flags.Flag.SEEN);
/*     */               break;
/*     */             case 'R':
/*  77 */               add(Flags.Flag.RECENT);
/*     */               break;
/*     */             case 'D':
/*  80 */               if (s.length() >= 3) {
/*  81 */                 char c = s.charAt(2);
/*  82 */                 if (c == 'e' || c == 'E') {
/*  83 */                   add(Flags.Flag.DELETED); break;
/*  84 */                 }  if (c == 'r' || c == 'R')
/*  85 */                   add(Flags.Flag.DRAFT);  break;
/*     */               } 
/*  87 */               add(s);
/*     */               break;
/*     */             case 'A':
/*  90 */               add(Flags.Flag.ANSWERED);
/*     */               break;
/*     */             case 'F':
/*  93 */               add(Flags.Flag.FLAGGED);
/*     */               break;
/*     */             case '*':
/*  96 */               add(Flags.Flag.USER);
/*     */               break;
/*     */             default:
/*  99 */               add(s);
/*     */               break;
/*     */           } 
/*     */         } else {
/* 103 */           add(s);
/*     */         } 
/*     */       }  
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\FLAGS.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */