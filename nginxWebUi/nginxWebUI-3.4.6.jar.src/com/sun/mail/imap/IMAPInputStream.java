/*     */ package com.sun.mail.imap;
/*     */ 
/*     */ import com.sun.mail.iap.ByteArray;
/*     */ import com.sun.mail.iap.ConnectionException;
/*     */ import com.sun.mail.iap.ProtocolException;
/*     */ import com.sun.mail.imap.protocol.BODY;
/*     */ import com.sun.mail.imap.protocol.IMAPProtocol;
/*     */ import com.sun.mail.util.FolderClosedIOException;
/*     */ import com.sun.mail.util.MessageRemovedIOException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import javax.mail.Flags;
/*     */ import javax.mail.Folder;
/*     */ import javax.mail.FolderClosedException;
/*     */ import javax.mail.MessagingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IMAPInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private IMAPMessage msg;
/*     */   private String section;
/*     */   private int pos;
/*     */   private int blksize;
/*     */   private int max;
/*     */   private byte[] buf;
/*     */   private int bufcount;
/*     */   private int bufpos;
/*     */   private boolean lastBuffer;
/*     */   private boolean peek;
/*     */   private ByteArray readbuf;
/*     */   private static final int slop = 64;
/*     */   
/*     */   public IMAPInputStream(IMAPMessage msg, String section, int max, boolean peek) {
/*  80 */     this.msg = msg;
/*  81 */     this.section = section;
/*  82 */     this.max = max;
/*  83 */     this.peek = peek;
/*  84 */     this.pos = 0;
/*  85 */     this.blksize = msg.getFetchBlockSize();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void forceCheckExpunged() throws MessageRemovedIOException, FolderClosedIOException {
/*  94 */     synchronized (this.msg.getMessageCacheLock()) {
/*     */       try {
/*  96 */         this.msg.getProtocol().noop();
/*  97 */       } catch (ConnectionException cex) {
/*  98 */         throw new FolderClosedIOException(this.msg.getFolder(), cex.getMessage());
/*     */       }
/* 100 */       catch (FolderClosedException fex) {
/* 101 */         throw new FolderClosedIOException(fex.getFolder(), fex.getMessage());
/*     */       }
/* 103 */       catch (ProtocolException pex) {}
/*     */     } 
/*     */ 
/*     */     
/* 107 */     if (this.msg.isExpunged()) {
/* 108 */       throw new MessageRemovedIOException();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void fill() throws IOException {
/*     */     ByteArray ba;
/*     */     int cnt;
/* 121 */     if (this.lastBuffer || (this.max != -1 && this.pos >= this.max)) {
/* 122 */       if (this.pos == 0)
/* 123 */         checkSeen(); 
/* 124 */       this.readbuf = null;
/*     */       
/*     */       return;
/*     */     } 
/* 128 */     BODY b = null;
/* 129 */     if (this.readbuf == null) {
/* 130 */       this.readbuf = new ByteArray(this.blksize + 64);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 135 */     synchronized (this.msg.getMessageCacheLock()) {
/*     */       try {
/* 137 */         IMAPProtocol p = this.msg.getProtocol();
/*     */ 
/*     */         
/* 140 */         if (this.msg.isExpunged()) {
/* 141 */           throw new MessageRemovedIOException("No content for expunged message");
/*     */         }
/*     */         
/* 144 */         int seqnum = this.msg.getSequenceNumber();
/* 145 */         cnt = this.blksize;
/* 146 */         if (this.max != -1 && this.pos + this.blksize > this.max)
/* 147 */           cnt = this.max - this.pos; 
/* 148 */         if (this.peek)
/* 149 */         { b = p.peekBody(seqnum, this.section, this.pos, cnt, this.readbuf); }
/*     */         else
/* 151 */         { b = p.fetchBody(seqnum, this.section, this.pos, cnt, this.readbuf); } 
/* 152 */       } catch (ProtocolException pex) {
/* 153 */         forceCheckExpunged();
/* 154 */         throw new IOException(pex.getMessage());
/* 155 */       } catch (FolderClosedException fex) {
/* 156 */         throw new FolderClosedIOException(fex.getFolder(), fex.getMessage());
/*     */       } 
/*     */ 
/*     */       
/* 160 */       if (b == null || (ba = b.getByteArray()) == null) {
/* 161 */         forceCheckExpunged();
/* 162 */         throw new IOException("No content");
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 167 */     if (this.pos == 0) {
/* 168 */       checkSeen();
/*     */     }
/*     */     
/* 171 */     this.buf = ba.getBytes();
/* 172 */     this.bufpos = ba.getStart();
/* 173 */     int n = ba.getCount();
/*     */ 
/*     */     
/* 176 */     this.lastBuffer = (n < cnt);
/* 177 */     this.bufcount = this.bufpos + n;
/* 178 */     this.pos += n;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized int read() throws IOException {
/* 186 */     if (this.bufpos >= this.bufcount) {
/* 187 */       fill();
/* 188 */       if (this.bufpos >= this.bufcount)
/* 189 */         return -1; 
/*     */     } 
/* 191 */     return this.buf[this.bufpos++] & 0xFF;
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
/*     */   
/*     */   public synchronized int read(byte[] b, int off, int len) throws IOException {
/* 211 */     int avail = this.bufcount - this.bufpos;
/* 212 */     if (avail <= 0) {
/* 213 */       fill();
/* 214 */       avail = this.bufcount - this.bufpos;
/* 215 */       if (avail <= 0)
/* 216 */         return -1; 
/*     */     } 
/* 218 */     int cnt = (avail < len) ? avail : len;
/* 219 */     System.arraycopy(this.buf, this.bufpos, b, off, cnt);
/* 220 */     this.bufpos += cnt;
/* 221 */     return cnt;
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
/*     */   public int read(byte[] b) throws IOException {
/* 239 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized int available() throws IOException {
/* 247 */     return this.bufcount - this.bufpos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkSeen() {
/* 258 */     if (this.peek)
/*     */       return; 
/*     */     try {
/* 261 */       Folder f = this.msg.getFolder();
/* 262 */       if (f != null && f.getMode() != 1 && !this.msg.isSet(Flags.Flag.SEEN))
/*     */       {
/* 264 */         this.msg.setFlag(Flags.Flag.SEEN, true); } 
/* 265 */     } catch (MessagingException ex) {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\IMAPInputStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */