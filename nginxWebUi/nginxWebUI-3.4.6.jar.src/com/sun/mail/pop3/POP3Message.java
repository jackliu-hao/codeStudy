/*     */ package com.sun.mail.pop3;
/*     */ 
/*     */ import com.sun.mail.util.ReadableMime;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.util.Enumeration;
/*     */ import java.util.logging.Level;
/*     */ import javax.mail.Flags;
/*     */ import javax.mail.Folder;
/*     */ import javax.mail.FolderClosedException;
/*     */ import javax.mail.IllegalWriteException;
/*     */ import javax.mail.Message;
/*     */ import javax.mail.MessageRemovedException;
/*     */ import javax.mail.MessagingException;
/*     */ import javax.mail.Session;
/*     */ import javax.mail.internet.InternetHeaders;
/*     */ import javax.mail.internet.MimeMessage;
/*     */ import javax.mail.internet.SharedInputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class POP3Message
/*     */   extends MimeMessage
/*     */   implements ReadableMime
/*     */ {
/*     */   static final String UNKNOWN = "UNKNOWN";
/*     */   private POP3Folder folder;
/*  70 */   private int hdrSize = -1;
/*  71 */   private int msgSize = -1;
/*  72 */   String uid = "UNKNOWN";
/*     */ 
/*     */   
/*  75 */   private SoftReference rawData = new SoftReference(null);
/*     */   static final boolean $assertionsDisabled;
/*     */   
/*     */   public POP3Message(Folder folder, int msgno) throws MessagingException {
/*  79 */     super(folder, msgno);
/*  80 */     this.folder = (POP3Folder)folder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void setFlags(Flags newFlags, boolean set) throws MessagingException {
/*  91 */     Flags oldFlags = (Flags)this.flags.clone();
/*  92 */     super.setFlags(newFlags, set);
/*  93 */     if (!this.flags.equals(oldFlags)) {
/*  94 */       this.folder.notifyMessageChangedListeners(1, (Message)this);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSize() throws MessagingException {
/*     */     try {
/* 111 */       synchronized (this) {
/*     */         
/* 113 */         if (this.msgSize > 0) {
/* 114 */           return this.msgSize;
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 132 */       if (this.headers == null) {
/* 133 */         loadHeaders();
/*     */       }
/* 135 */       synchronized (this) {
/* 136 */         if (this.msgSize < 0)
/* 137 */           this.msgSize = this.folder.getProtocol().list(this.msgnum) - this.hdrSize; 
/* 138 */         return this.msgSize;
/*     */       } 
/* 140 */     } catch (EOFException eex) {
/* 141 */       this.folder.close(false);
/* 142 */       throw new FolderClosedException(this.folder, eex.toString());
/* 143 */     } catch (IOException ex) {
/* 144 */       throw new MessagingException("error getting size", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private InputStream getRawStream(boolean skipHeader) throws MessagingException {
/* 155 */     InputStream rawcontent = null;
/*     */     try {
/* 157 */       synchronized (this) {
/* 158 */         rawcontent = this.rawData.get();
/* 159 */         if (rawcontent == null) {
/* 160 */           TempFile cache = this.folder.getFileCache();
/* 161 */           if (cache != null) {
/* 162 */             Session s = ((POP3Store)this.folder.getStore()).getSession();
/* 163 */             if (this.folder.logger.isLoggable(Level.FINE)) {
/* 164 */               this.folder.logger.fine("caching message #" + this.msgnum + " in temp file");
/*     */             }
/* 166 */             AppendStream os = cache.getAppendStream();
/* 167 */             BufferedOutputStream bos = new BufferedOutputStream(os);
/*     */             try {
/* 169 */               this.folder.getProtocol().retr(this.msgnum, bos);
/*     */             } finally {
/* 171 */               bos.close();
/*     */             } 
/* 173 */             rawcontent = os.getInputStream();
/*     */           } else {
/* 175 */             rawcontent = this.folder.getProtocol().retr(this.msgnum, (this.msgSize > 0) ? (this.msgSize + this.hdrSize) : 0);
/*     */           } 
/*     */           
/* 178 */           if (rawcontent == null) {
/* 179 */             this.expunged = true;
/* 180 */             throw new MessageRemovedException("can't retrieve message #" + this.msgnum + " in POP3Message.getContentStream");
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 185 */           if (this.headers == null || ((POP3Store)this.folder.getStore()).forgetTopHeaders) {
/*     */             
/* 187 */             this.headers = new InternetHeaders(rawcontent);
/* 188 */             this.hdrSize = (int)((SharedInputStream)rawcontent).getPosition();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           }
/*     */           else {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 203 */             int len, offset = 0;
/*     */             do {
/* 205 */               len = 0;
/*     */               int c1;
/* 207 */               while ((c1 = rawcontent.read()) >= 0 && 
/* 208 */                 c1 != 10) {
/*     */                 
/* 210 */                 if (c1 == 13) {
/*     */                   
/* 212 */                   if (rawcontent.available() > 0) {
/* 213 */                     rawcontent.mark(1);
/* 214 */                     if (rawcontent.read() != 10) {
/* 215 */                       rawcontent.reset();
/*     */                     }
/*     */                   } 
/*     */                   
/*     */                   break;
/*     */                 } 
/* 221 */                 len++;
/*     */               } 
/*     */ 
/*     */ 
/*     */               
/* 226 */               if (rawcontent.available() == 0) {
/*     */                 break;
/*     */               }
/*     */             }
/* 230 */             while (len != 0);
/*     */ 
/*     */             
/* 233 */             this.hdrSize = (int)((SharedInputStream)rawcontent).getPosition();
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 238 */           this.msgSize = rawcontent.available();
/*     */           
/* 240 */           this.rawData = new SoftReference(rawcontent);
/*     */         } 
/*     */       } 
/* 243 */     } catch (EOFException eex) {
/* 244 */       this.folder.close(false);
/* 245 */       throw new FolderClosedException(this.folder, eex.toString());
/* 246 */     } catch (IOException ex) {
/* 247 */       throw new MessagingException("error fetching POP3 content", ex);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 255 */     rawcontent = ((SharedInputStream)rawcontent).newStream(skipHeader ? this.hdrSize : 0L, -1L);
/*     */     
/* 257 */     return rawcontent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected synchronized InputStream getContentStream() throws MessagingException {
/* 268 */     if (this.contentStream != null) {
/* 269 */       return ((SharedInputStream)this.contentStream).newStream(0L, -1L);
/*     */     }
/* 271 */     InputStream cstream = getRawStream(true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 277 */     TempFile cache = this.folder.getFileCache();
/* 278 */     if (cache != null || ((POP3Store)this.folder.getStore()).keepMessageContent)
/*     */     {
/* 280 */       this.contentStream = ((SharedInputStream)cstream).newStream(0L, -1L); } 
/* 281 */     return cstream;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream getMimeStream() throws MessagingException {
/* 291 */     return getRawStream(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void invalidate(boolean invalidateHeaders) {
/* 303 */     this.content = null;
/* 304 */     InputStream rstream = this.rawData.get();
/* 305 */     if (rstream != null) {
/*     */ 
/*     */       
/*     */       try {
/* 309 */         rstream.close();
/* 310 */       } catch (IOException ex) {}
/*     */ 
/*     */       
/* 313 */       this.rawData = new SoftReference(null);
/*     */     } 
/* 315 */     if (this.contentStream != null) {
/*     */       try {
/* 317 */         this.contentStream.close();
/* 318 */       } catch (IOException ex) {}
/*     */ 
/*     */       
/* 321 */       this.contentStream = null;
/*     */     } 
/* 323 */     this.msgSize = -1;
/* 324 */     if (invalidateHeaders) {
/* 325 */       this.headers = null;
/* 326 */       this.hdrSize = -1;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream top(int n) throws MessagingException {
/*     */     try {
/* 340 */       synchronized (this) {
/* 341 */         return this.folder.getProtocol().top(this.msgnum, n);
/*     */       } 
/* 343 */     } catch (EOFException eex) {
/* 344 */       this.folder.close(false);
/* 345 */       throw new FolderClosedException(this.folder, eex.toString());
/* 346 */     } catch (IOException ex) {
/* 347 */       throw new MessagingException("error getting size", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getHeader(String name) throws MessagingException {
/* 363 */     if (this.headers == null)
/* 364 */       loadHeaders(); 
/* 365 */     return this.headers.getHeader(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHeader(String name, String delimiter) throws MessagingException {
/* 382 */     if (this.headers == null)
/* 383 */       loadHeaders(); 
/* 384 */     return this.headers.getHeader(name, delimiter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHeader(String name, String value) throws MessagingException {
/* 402 */     throw new IllegalWriteException("POP3 messages are read-only");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addHeader(String name, String value) throws MessagingException {
/* 420 */     throw new IllegalWriteException("POP3 messages are read-only");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeHeader(String name) throws MessagingException {
/* 435 */     throw new IllegalWriteException("POP3 messages are read-only");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration getAllHeaders() throws MessagingException {
/* 451 */     if (this.headers == null)
/* 452 */       loadHeaders(); 
/* 453 */     return this.headers.getAllHeaders();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration getMatchingHeaders(String[] names) throws MessagingException {
/* 464 */     if (this.headers == null)
/* 465 */       loadHeaders(); 
/* 466 */     return this.headers.getMatchingHeaders(names);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration getNonMatchingHeaders(String[] names) throws MessagingException {
/* 477 */     if (this.headers == null)
/* 478 */       loadHeaders(); 
/* 479 */     return this.headers.getNonMatchingHeaders(names);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addHeaderLine(String line) throws MessagingException {
/* 493 */     throw new IllegalWriteException("POP3 messages are read-only");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration getAllHeaderLines() throws MessagingException {
/* 504 */     if (this.headers == null)
/* 505 */       loadHeaders(); 
/* 506 */     return this.headers.getAllHeaderLines();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration getMatchingHeaderLines(String[] names) throws MessagingException {
/* 518 */     if (this.headers == null)
/* 519 */       loadHeaders(); 
/* 520 */     return this.headers.getMatchingHeaderLines(names);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration getNonMatchingHeaderLines(String[] names) throws MessagingException {
/* 532 */     if (this.headers == null)
/* 533 */       loadHeaders(); 
/* 534 */     return this.headers.getNonMatchingHeaderLines(names);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void saveChanges() throws MessagingException {
/* 546 */     throw new IllegalWriteException("POP3 messages are read-only");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void writeTo(OutputStream os, String[] ignoreList) throws IOException, MessagingException {
/* 565 */     InputStream rawcontent = this.rawData.get();
/* 566 */     if (rawcontent == null && ignoreList == null && !((POP3Store)this.folder.getStore()).cacheWriteTo) {
/*     */       
/* 568 */       Session s = ((POP3Store)this.folder.getStore()).getSession();
/* 569 */       if (this.folder.logger.isLoggable(Level.FINE))
/* 570 */         this.folder.logger.fine("streaming msg " + this.msgnum); 
/* 571 */       if (!this.folder.getProtocol().retr(this.msgnum, os)) {
/* 572 */         this.expunged = true;
/* 573 */         throw new MessageRemovedException("can't retrieve message #" + this.msgnum + " in POP3Message.writeTo");
/*     */       }
/*     */     
/* 576 */     } else if (rawcontent != null && ignoreList == null) {
/*     */       
/* 578 */       InputStream in = ((SharedInputStream)rawcontent).newStream(0L, -1L);
/*     */       try {
/* 580 */         byte[] buf = new byte[16384];
/*     */         int len;
/* 582 */         while ((len = in.read(buf)) > 0)
/* 583 */           os.write(buf, 0, len); 
/*     */       } finally {
/*     */         try {
/* 586 */           if (in != null)
/* 587 */             in.close(); 
/* 588 */         } catch (IOException ex) {}
/*     */       } 
/*     */     } else {
/* 591 */       super.writeTo(os, ignoreList);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void loadHeaders() throws MessagingException {
/* 599 */     assert !Thread.holdsLock(this);
/*     */     try {
/* 601 */       boolean fetchContent = false;
/* 602 */       synchronized (this) {
/* 603 */         if (this.headers != null)
/*     */           return; 
/* 605 */         InputStream hdrs = null;
/* 606 */         if (((POP3Store)this.folder.getStore()).disableTop || (hdrs = this.folder.getProtocol().top(this.msgnum, 0)) == null) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 611 */           fetchContent = true;
/*     */         } else {
/*     */           try {
/* 614 */             this.hdrSize = hdrs.available();
/* 615 */             this.headers = new InternetHeaders(hdrs);
/*     */           } finally {
/* 617 */             hdrs.close();
/*     */           } 
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 632 */       if (fetchContent) {
/* 633 */         InputStream cs = null;
/*     */         try {
/* 635 */           cs = getContentStream();
/*     */         } finally {
/* 637 */           if (cs != null)
/* 638 */             cs.close(); 
/*     */         } 
/*     */       } 
/* 641 */     } catch (EOFException eex) {
/* 642 */       this.folder.close(false);
/* 643 */       throw new FolderClosedException(this.folder, eex.toString());
/* 644 */     } catch (IOException ex) {
/* 645 */       throw new MessagingException("error loading POP3 headers", ex);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\pop3\POP3Message.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */