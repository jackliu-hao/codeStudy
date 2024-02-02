/*     */ package com.sun.mail.imap.protocol;
/*     */ 
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
/*     */ public class MessageSet
/*     */ {
/*     */   public int start;
/*     */   public int end;
/*     */   
/*     */   public MessageSet() {}
/*     */   
/*     */   public MessageSet(int start, int end) {
/*  56 */     this.start = start;
/*  57 */     this.end = end;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/*  64 */     return this.end - this.start + 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MessageSet[] createMessageSets(int[] msgs) {
/*  71 */     Vector v = new Vector();
/*     */ 
/*     */     
/*  74 */     for (int i = 0; i < msgs.length; i++) {
/*  75 */       MessageSet ms = new MessageSet();
/*  76 */       ms.start = msgs[i];
/*     */       
/*     */       int j;
/*  79 */       for (j = i + 1; j < msgs.length && 
/*  80 */         msgs[j] == msgs[j - 1] + 1; j++);
/*     */ 
/*     */       
/*  83 */       ms.end = msgs[j - 1];
/*  84 */       v.addElement(ms);
/*  85 */       i = j - 1;
/*     */     } 
/*  87 */     MessageSet[] msgsets = new MessageSet[v.size()];
/*  88 */     v.copyInto((Object[])msgsets);
/*  89 */     return msgsets;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toString(MessageSet[] msgsets) {
/*  96 */     if (msgsets == null || msgsets.length == 0) {
/*  97 */       return null;
/*     */     }
/*  99 */     int i = 0;
/* 100 */     StringBuffer s = new StringBuffer();
/* 101 */     int size = msgsets.length;
/*     */ 
/*     */     
/*     */     while (true) {
/* 105 */       int start = (msgsets[i]).start;
/* 106 */       int end = (msgsets[i]).end;
/*     */       
/* 108 */       if (end > start) {
/* 109 */         s.append(start).append(':').append(end);
/*     */       } else {
/* 111 */         s.append(start);
/*     */       } 
/* 113 */       i++;
/* 114 */       if (i >= size) {
/*     */         break;
/*     */       }
/* 117 */       s.append(',');
/*     */     } 
/* 119 */     return s.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int size(MessageSet[] msgsets) {
/* 127 */     int count = 0;
/*     */     
/* 129 */     if (msgsets == null) {
/* 130 */       return 0;
/*     */     }
/* 132 */     for (int i = 0; i < msgsets.length; i++) {
/* 133 */       count += msgsets[i].size();
/*     */     }
/* 135 */     return count;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\MessageSet.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */