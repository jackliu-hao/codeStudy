/*     */ package com.sun.mail.imap;
/*     */ 
/*     */ import com.sun.mail.util.MailLogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.logging.Level;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MessageCache
/*     */ {
/*     */   private IMAPMessage[] messages;
/*     */   private int[] seqnums;
/*     */   private int size;
/*     */   private IMAPFolder folder;
/*     */   private MailLogger logger;
/*     */   private static final int SLOP = 64;
/*     */   static final boolean $assertionsDisabled;
/*     */   
/*     */   MessageCache(IMAPFolder folder, IMAPStore store, int size) {
/* 100 */     this.folder = folder;
/* 101 */     this.logger = folder.logger.getSubLogger("messagecache", "DEBUG IMAP MC", store.getMessageCacheDebug());
/*     */     
/* 103 */     if (this.logger.isLoggable(Level.CONFIG))
/* 104 */       this.logger.config("create cache of size " + size); 
/* 105 */     ensureCapacity(size, 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   MessageCache(int size, boolean debug) {
/* 112 */     this.folder = null;
/* 113 */     this.logger = new MailLogger(getClass(), "messagecache", "DEBUG IMAP MC", debug, System.out);
/*     */ 
/*     */     
/* 116 */     if (this.logger.isLoggable(Level.CONFIG))
/* 117 */       this.logger.config("create DEBUG cache of size " + size); 
/* 118 */     ensureCapacity(size, 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 125 */     return this.size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IMAPMessage getMessage(int msgnum) {
/* 134 */     if (msgnum < 1 || msgnum > this.size) {
/* 135 */       throw new ArrayIndexOutOfBoundsException("message number (" + msgnum + ") out of bounds (" + this.size + ")");
/*     */     }
/* 137 */     IMAPMessage msg = this.messages[msgnum - 1];
/* 138 */     if (msg == null) {
/* 139 */       if (this.logger.isLoggable(Level.FINE))
/* 140 */         this.logger.fine("create message number " + msgnum); 
/* 141 */       msg = this.folder.newIMAPMessage(msgnum);
/* 142 */       this.messages[msgnum - 1] = msg;
/*     */       
/* 144 */       if (seqnumOf(msgnum) <= 0) {
/* 145 */         this.logger.fine("it's expunged!");
/* 146 */         msg.setExpunged(true);
/*     */       } 
/*     */     } 
/* 149 */     return msg;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IMAPMessage getMessageBySeqnum(int seqnum) {
/* 158 */     int msgnum = msgnumOf(seqnum);
/* 159 */     if (msgnum < 0) {
/* 160 */       if (this.logger.isLoggable(Level.FINE))
/* 161 */         this.logger.fine("no message seqnum " + seqnum); 
/* 162 */       return null;
/*     */     } 
/* 164 */     return getMessage(msgnum);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void expungeMessage(int seqnum) {
/* 171 */     int msgnum = msgnumOf(seqnum);
/* 172 */     if (msgnum < 0) {
/* 173 */       if (this.logger.isLoggable(Level.FINE))
/* 174 */         this.logger.fine("expunge no seqnum " + seqnum); 
/*     */       return;
/*     */     } 
/* 177 */     IMAPMessage msg = this.messages[msgnum - 1];
/* 178 */     if (msg != null) {
/* 179 */       if (this.logger.isLoggable(Level.FINE))
/* 180 */         this.logger.fine("expunge existing " + msgnum); 
/* 181 */       msg.setExpunged(true);
/*     */     } 
/* 183 */     if (this.seqnums == null) {
/* 184 */       this.logger.fine("create seqnums array");
/* 185 */       this.seqnums = new int[this.messages.length]; int i;
/* 186 */       for (i = 1; i < msgnum; i++)
/* 187 */         this.seqnums[i - 1] = i; 
/* 188 */       this.seqnums[msgnum - 1] = 0;
/* 189 */       for (i = msgnum + 1; i <= this.seqnums.length; i++)
/* 190 */         this.seqnums[i - 1] = i - 1; 
/*     */     } else {
/* 192 */       this.seqnums[msgnum - 1] = 0;
/* 193 */       for (int i = msgnum + 1; i <= this.seqnums.length; i++) {
/* 194 */         assert this.seqnums[i - 1] != 1;
/* 195 */         if (this.seqnums[i - 1] > 0) {
/* 196 */           this.seqnums[i - 1] = this.seqnums[i - 1] - 1;
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IMAPMessage[] removeExpungedMessages() {
/* 206 */     this.logger.fine("remove expunged messages");
/* 207 */     List mlist = new ArrayList();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 217 */     int oldnum = 1;
/* 218 */     int newnum = 1;
/* 219 */     while (oldnum <= this.size) {
/*     */       
/* 221 */       if (seqnumOf(oldnum) <= 0) {
/* 222 */         IMAPMessage m = getMessage(oldnum);
/* 223 */         mlist.add(m);
/*     */       } else {
/*     */         
/* 226 */         if (newnum != oldnum) {
/*     */           
/* 228 */           this.messages[newnum - 1] = this.messages[oldnum - 1];
/* 229 */           if (this.messages[newnum - 1] != null)
/* 230 */             this.messages[newnum - 1].setMessageNumber(newnum); 
/*     */         } 
/* 232 */         newnum++;
/*     */       } 
/* 234 */       oldnum++;
/*     */     } 
/* 236 */     this.seqnums = null;
/* 237 */     shrink(newnum, oldnum);
/*     */     
/* 239 */     IMAPMessage[] rmsgs = new IMAPMessage[mlist.size()];
/* 240 */     if (this.logger.isLoggable(Level.FINE))
/* 241 */       this.logger.fine("return " + rmsgs.length); 
/* 242 */     mlist.toArray(rmsgs);
/* 243 */     return rmsgs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IMAPMessage[] removeExpungedMessages(Message[] msgs) {
/* 253 */     this.logger.fine("remove expunged messages");
/* 254 */     List mlist = new ArrayList();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 261 */     int[] mnum = new int[msgs.length];
/* 262 */     for (int i = 0; i < msgs.length; i++)
/* 263 */       mnum[i] = msgs[i].getMessageNumber(); 
/* 264 */     Arrays.sort(mnum);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 280 */     int oldnum = 1;
/* 281 */     int newnum = 1;
/* 282 */     int mnumi = 0;
/* 283 */     boolean keepSeqnums = false;
/* 284 */     while (oldnum <= this.size) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 290 */       if (mnumi < mnum.length && oldnum == mnum[mnumi] && seqnumOf(oldnum) <= 0) {
/*     */ 
/*     */         
/* 293 */         IMAPMessage m = getMessage(oldnum);
/* 294 */         mlist.add(m);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 300 */         while (mnumi < mnum.length && mnum[mnumi] <= oldnum) {
/* 301 */           mnumi++;
/*     */         }
/*     */       } else {
/* 304 */         if (newnum != oldnum) {
/*     */           
/* 306 */           this.messages[newnum - 1] = this.messages[oldnum - 1];
/* 307 */           if (this.messages[newnum - 1] != null)
/* 308 */             this.messages[newnum - 1].setMessageNumber(newnum); 
/* 309 */           if (this.seqnums != null)
/* 310 */             this.seqnums[newnum - 1] = this.seqnums[oldnum - 1]; 
/*     */         } 
/* 312 */         if (this.seqnums != null && this.seqnums[newnum - 1] != newnum)
/* 313 */           keepSeqnums = true; 
/* 314 */         newnum++;
/*     */       } 
/* 316 */       oldnum++;
/*     */     } 
/*     */     
/* 319 */     if (!keepSeqnums)
/* 320 */       this.seqnums = null; 
/* 321 */     shrink(newnum, oldnum);
/*     */     
/* 323 */     IMAPMessage[] rmsgs = new IMAPMessage[mlist.size()];
/* 324 */     if (this.logger.isLoggable(Level.FINE))
/* 325 */       this.logger.fine("return " + rmsgs.length); 
/* 326 */     mlist.toArray(rmsgs);
/* 327 */     return rmsgs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void shrink(int newend, int oldend) {
/* 335 */     this.size = newend - 1;
/* 336 */     if (this.logger.isLoggable(Level.FINE))
/* 337 */       this.logger.fine("size now " + this.size); 
/* 338 */     if (this.size == 0) {
/* 339 */       this.messages = null;
/* 340 */       this.seqnums = null;
/* 341 */     } else if (this.size > 64 && this.size < this.messages.length / 2) {
/*     */       
/* 343 */       this.logger.fine("reallocate array");
/* 344 */       IMAPMessage[] newm = new IMAPMessage[this.size + 64];
/* 345 */       System.arraycopy(this.messages, 0, newm, 0, this.size);
/* 346 */       this.messages = newm;
/* 347 */       if (this.seqnums != null) {
/* 348 */         int[] news = new int[this.size + 64];
/* 349 */         System.arraycopy(this.seqnums, 0, news, 0, this.size);
/* 350 */         this.seqnums = news;
/*     */       } 
/*     */     } else {
/* 353 */       if (this.logger.isLoggable(Level.FINE)) {
/* 354 */         this.logger.fine("clean " + newend + " to " + oldend);
/*     */       }
/* 356 */       for (int msgnum = newend; msgnum < oldend; msgnum++) {
/* 357 */         this.messages[msgnum - 1] = null;
/* 358 */         if (this.seqnums != null) {
/* 359 */           this.seqnums[msgnum - 1] = 0;
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addMessages(int count, int newSeqNum) {
/* 369 */     if (this.logger.isLoggable(Level.FINE)) {
/* 370 */       this.logger.fine("add " + count + " messages");
/*     */     }
/* 372 */     ensureCapacity(this.size + count, newSeqNum);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void ensureCapacity(int newsize, int newSeqNum) {
/* 380 */     if (this.messages == null) {
/* 381 */       this.messages = new IMAPMessage[newsize + 64];
/* 382 */     } else if (this.messages.length < newsize) {
/* 383 */       if (this.logger.isLoggable(Level.FINE))
/* 384 */         this.logger.fine("expand capacity to " + newsize); 
/* 385 */       IMAPMessage[] newm = new IMAPMessage[newsize + 64];
/* 386 */       System.arraycopy(this.messages, 0, newm, 0, this.messages.length);
/* 387 */       this.messages = newm;
/* 388 */       if (this.seqnums != null) {
/* 389 */         int[] news = new int[newsize + 64];
/* 390 */         System.arraycopy(this.seqnums, 0, news, 0, this.seqnums.length);
/* 391 */         for (int i = this.size; i < news.length; i++)
/* 392 */           news[i] = newSeqNum++; 
/* 393 */         this.seqnums = news;
/* 394 */         if (this.logger.isLoggable(Level.FINE)) {
/* 395 */           this.logger.fine("message " + newsize + " has sequence number " + this.seqnums[newsize - 1]);
/*     */         }
/*     */       } 
/* 398 */     } else if (newsize < this.size) {
/*     */       
/* 400 */       if (this.logger.isLoggable(Level.FINE))
/* 401 */         this.logger.fine("shrink capacity to " + newsize); 
/* 402 */       for (int msgnum = newsize + 1; msgnum <= this.size; msgnum++) {
/* 403 */         this.messages[msgnum - 1] = null;
/* 404 */         if (this.seqnums != null)
/* 405 */           this.seqnums[msgnum - 1] = -1; 
/*     */       } 
/*     */     } 
/* 408 */     this.size = newsize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int seqnumOf(int msgnum) {
/* 415 */     if (this.seqnums == null) {
/* 416 */       return msgnum;
/*     */     }
/* 418 */     if (this.logger.isLoggable(Level.FINE)) {
/* 419 */       this.logger.fine("msgnum " + msgnum + " is seqnum " + this.seqnums[msgnum - 1]);
/*     */     }
/* 421 */     return this.seqnums[msgnum - 1];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int msgnumOf(int seqnum) {
/* 429 */     if (this.seqnums == null)
/* 430 */       return seqnum; 
/* 431 */     if (seqnum < 1) {
/* 432 */       if (this.logger.isLoggable(Level.FINE))
/* 433 */         this.logger.fine("bad seqnum " + seqnum); 
/* 434 */       return -1;
/*     */     } 
/* 436 */     for (int msgnum = seqnum; msgnum <= this.size; msgnum++) {
/* 437 */       if (this.seqnums[msgnum - 1] == seqnum)
/* 438 */         return msgnum; 
/* 439 */       if (this.seqnums[msgnum - 1] > seqnum)
/*     */         break; 
/*     */     } 
/* 442 */     return -1;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\MessageCache.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */