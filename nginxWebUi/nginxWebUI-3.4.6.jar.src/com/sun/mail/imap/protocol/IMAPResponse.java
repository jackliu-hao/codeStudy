/*     */ package com.sun.mail.imap.protocol;
/*     */ 
/*     */ import com.sun.mail.iap.Protocol;
/*     */ import com.sun.mail.iap.ProtocolException;
/*     */ import com.sun.mail.iap.Response;
/*     */ import com.sun.mail.util.ASCIIUtility;
/*     */ import java.io.IOException;
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
/*     */ public class IMAPResponse
/*     */   extends Response
/*     */ {
/*     */   private String key;
/*     */   private int number;
/*     */   
/*     */   public IMAPResponse(Protocol c) throws IOException, ProtocolException {
/*  60 */     super(c);
/*  61 */     init();
/*     */   }
/*     */ 
/*     */   
/*     */   private void init() throws IOException, ProtocolException {
/*  66 */     if (isUnTagged() && !isOK() && !isNO() && !isBAD() && !isBYE()) {
/*  67 */       this.key = readAtom();
/*     */ 
/*     */       
/*     */       try {
/*  71 */         this.number = Integer.parseInt(this.key);
/*  72 */         this.key = readAtom();
/*  73 */       } catch (NumberFormatException ne) {}
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IMAPResponse(IMAPResponse r) {
/*  81 */     super(r);
/*  82 */     this.key = r.key;
/*  83 */     this.number = r.number;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IMAPResponse(String r) throws IOException, ProtocolException {
/*  90 */     super(r);
/*  91 */     init();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] readSimpleList() {
/* 101 */     skipSpaces();
/*     */     
/* 103 */     if (this.buffer[this.index] != 40)
/* 104 */       return null; 
/* 105 */     this.index++;
/*     */     
/* 107 */     Vector v = new Vector();
/*     */     int start;
/* 109 */     for (start = this.index; this.buffer[this.index] != 41; this.index++) {
/* 110 */       if (this.buffer[this.index] == 32) {
/* 111 */         v.addElement(ASCIIUtility.toString(this.buffer, start, this.index));
/* 112 */         start = this.index + 1;
/*     */       } 
/*     */     } 
/* 115 */     if (this.index > start)
/* 116 */       v.addElement(ASCIIUtility.toString(this.buffer, start, this.index)); 
/* 117 */     this.index++;
/*     */     
/* 119 */     int size = v.size();
/* 120 */     if (size > 0) {
/* 121 */       String[] s = new String[size];
/* 122 */       v.copyInto((Object[])s);
/* 123 */       return s;
/*     */     } 
/* 125 */     return null;
/*     */   }
/*     */   
/*     */   public String getKey() {
/* 129 */     return this.key;
/*     */   }
/*     */   
/*     */   public boolean keyEquals(String k) {
/* 133 */     if (this.key != null && this.key.equalsIgnoreCase(k)) {
/* 134 */       return true;
/*     */     }
/* 136 */     return false;
/*     */   }
/*     */   
/*     */   public int getNumber() {
/* 140 */     return this.number;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\IMAPResponse.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */