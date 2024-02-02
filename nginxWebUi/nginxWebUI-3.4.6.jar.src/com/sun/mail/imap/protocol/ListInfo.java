/*     */ package com.sun.mail.imap.protocol;
/*     */ 
/*     */ import com.sun.mail.iap.ParsingException;
/*     */ import java.util.Vector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ListInfo
/*     */ {
/*  55 */   public String name = null;
/*  56 */   public char separator = '/';
/*     */   public boolean hasInferiors = true;
/*     */   public boolean canOpen = true;
/*  59 */   public int changeState = 3;
/*     */   
/*     */   public String[] attrs;
/*     */   public static final int CHANGED = 1;
/*     */   public static final int UNCHANGED = 2;
/*     */   public static final int INDETERMINATE = 3;
/*     */   
/*     */   public ListInfo(IMAPResponse r) throws ParsingException {
/*  67 */     String[] s = r.readSimpleList();
/*     */     
/*  69 */     Vector v = new Vector();
/*  70 */     if (s != null)
/*     */     {
/*  72 */       for (int i = 0; i < s.length; i++) {
/*  73 */         if (s[i].equalsIgnoreCase("\\Marked")) {
/*  74 */           this.changeState = 1;
/*  75 */         } else if (s[i].equalsIgnoreCase("\\Unmarked")) {
/*  76 */           this.changeState = 2;
/*  77 */         } else if (s[i].equalsIgnoreCase("\\Noselect")) {
/*  78 */           this.canOpen = false;
/*  79 */         } else if (s[i].equalsIgnoreCase("\\Noinferiors")) {
/*  80 */           this.hasInferiors = false;
/*  81 */         }  v.addElement(s[i]);
/*     */       } 
/*     */     }
/*  84 */     this.attrs = new String[v.size()];
/*  85 */     v.copyInto((Object[])this.attrs);
/*     */     
/*  87 */     r.skipSpaces();
/*  88 */     if (r.readByte() == 34) {
/*  89 */       if ((this.separator = (char)r.readByte()) == '\\')
/*     */       {
/*  91 */         this.separator = (char)r.readByte(); } 
/*  92 */       r.skip(1);
/*     */     } else {
/*  94 */       r.skip(2);
/*     */     } 
/*  96 */     r.skipSpaces();
/*  97 */     this.name = r.readAtomString();
/*     */ 
/*     */     
/* 100 */     this.name = BASE64MailboxDecoder.decode(this.name);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\ListInfo.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */