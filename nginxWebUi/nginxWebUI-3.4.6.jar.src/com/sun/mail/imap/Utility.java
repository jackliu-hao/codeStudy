/*     */ package com.sun.mail.imap;
/*     */ 
/*     */ import com.sun.mail.imap.protocol.MessageSet;
/*     */ import com.sun.mail.imap.protocol.UIDSet;
/*     */ import java.util.Vector;
/*     */ import javax.mail.Message;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Utility
/*     */ {
/*     */   public static MessageSet[] toMessageSet(Message[] msgs, Condition cond) {
/*  75 */     Vector v = new Vector(1);
/*     */ 
/*     */ 
/*     */     
/*  79 */     for (int i = 0; i < msgs.length; i++) {
/*  80 */       IMAPMessage msg = (IMAPMessage)msgs[i];
/*  81 */       if (!msg.isExpunged()) {
/*     */ 
/*     */         
/*  84 */         int current = msg.getSequenceNumber();
/*     */         
/*  86 */         if (cond == null || cond.test(msg)) {
/*     */ 
/*     */           
/*  89 */           MessageSet set = new MessageSet();
/*  90 */           set.start = current;
/*     */ 
/*     */           
/*  93 */           for (; ++i < msgs.length; i++) {
/*     */             
/*  95 */             msg = (IMAPMessage)msgs[i];
/*     */             
/*  97 */             if (!msg.isExpunged()) {
/*     */               
/*  99 */               int next = msg.getSequenceNumber();
/*     */ 
/*     */               
/* 102 */               if (cond == null || cond.test(msg))
/*     */               {
/*     */                 
/* 105 */                 if (next == current + 1) {
/* 106 */                   current = next;
/*     */                 
/*     */                 }
/*     */                 else {
/*     */                   
/* 111 */                   i--; break;
/*     */                 }  } 
/*     */             } 
/*     */           } 
/* 115 */           set.end = current;
/* 116 */           v.addElement(set);
/*     */         } 
/*     */       } 
/* 119 */     }  if (v.isEmpty()) {
/* 120 */       return null;
/*     */     }
/* 122 */     MessageSet[] sets = new MessageSet[v.size()];
/* 123 */     v.copyInto((Object[])sets);
/* 124 */     return sets;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static UIDSet[] toUIDSet(Message[] msgs) {
/* 133 */     Vector v = new Vector(1);
/*     */ 
/*     */ 
/*     */     
/* 137 */     for (int i = 0; i < msgs.length; i++) {
/* 138 */       IMAPMessage msg = (IMAPMessage)msgs[i];
/* 139 */       if (!msg.isExpunged()) {
/*     */ 
/*     */         
/* 142 */         long current = msg.getUID();
/*     */         
/* 144 */         UIDSet set = new UIDSet();
/* 145 */         set.start = current;
/*     */ 
/*     */         
/* 148 */         for (; ++i < msgs.length; i++) {
/*     */           
/* 150 */           msg = (IMAPMessage)msgs[i];
/*     */           
/* 152 */           if (!msg.isExpunged()) {
/*     */             
/* 154 */             long next = msg.getUID();
/*     */             
/* 156 */             if (next == current + 1L) {
/* 157 */               current = next;
/*     */             
/*     */             }
/*     */             else {
/*     */               
/* 162 */               i--; break;
/*     */             } 
/*     */           } 
/*     */         } 
/* 166 */         set.end = current;
/* 167 */         v.addElement(set);
/*     */       } 
/*     */     } 
/* 170 */     if (v.isEmpty()) {
/* 171 */       return null;
/*     */     }
/* 173 */     UIDSet[] sets = new UIDSet[v.size()];
/* 174 */     v.copyInto((Object[])sets);
/* 175 */     return sets;
/*     */   }
/*     */   
/*     */   public static interface Condition {
/*     */     boolean test(IMAPMessage param1IMAPMessage);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\Utility.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */