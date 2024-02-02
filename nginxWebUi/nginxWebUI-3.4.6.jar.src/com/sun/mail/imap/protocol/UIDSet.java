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
/*     */ 
/*     */ public class UIDSet
/*     */ {
/*     */   public long start;
/*     */   public long end;
/*     */   
/*     */   public UIDSet() {}
/*     */   
/*     */   public UIDSet(long start, long end) {
/*  57 */     this.start = start;
/*  58 */     this.end = end;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long size() {
/*  65 */     return this.end - this.start + 1L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static UIDSet[] createUIDSets(long[] msgs) {
/*  72 */     Vector v = new Vector();
/*     */ 
/*     */     
/*  75 */     for (int i = 0; i < msgs.length; i++) {
/*  76 */       UIDSet ms = new UIDSet();
/*  77 */       ms.start = msgs[i];
/*     */       
/*     */       int j;
/*  80 */       for (j = i + 1; j < msgs.length && 
/*  81 */         msgs[j] == msgs[j - 1] + 1L; j++);
/*     */ 
/*     */       
/*  84 */       ms.end = msgs[j - 1];
/*  85 */       v.addElement(ms);
/*  86 */       i = j - 1;
/*     */     } 
/*  88 */     UIDSet[] msgsets = new UIDSet[v.size()];
/*  89 */     v.copyInto((Object[])msgsets);
/*  90 */     return msgsets;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toString(UIDSet[] msgsets) {
/*  97 */     if (msgsets == null || msgsets.length == 0) {
/*  98 */       return null;
/*     */     }
/* 100 */     int i = 0;
/* 101 */     StringBuffer s = new StringBuffer();
/* 102 */     int size = msgsets.length;
/*     */ 
/*     */     
/*     */     while (true) {
/* 106 */       long start = (msgsets[i]).start;
/* 107 */       long end = (msgsets[i]).end;
/*     */       
/* 109 */       if (end > start) {
/* 110 */         s.append(start).append(':').append(end);
/*     */       } else {
/* 112 */         s.append(start);
/*     */       } 
/* 114 */       i++;
/* 115 */       if (i >= size) {
/*     */         break;
/*     */       }
/* 118 */       s.append(',');
/*     */     } 
/* 120 */     return s.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long size(UIDSet[] msgsets) {
/* 128 */     long count = 0L;
/*     */     
/* 130 */     if (msgsets == null) {
/* 131 */       return 0L;
/*     */     }
/* 133 */     for (int i = 0; i < msgsets.length; i++) {
/* 134 */       count += msgsets[i].size();
/*     */     }
/* 136 */     return count;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\UIDSet.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */