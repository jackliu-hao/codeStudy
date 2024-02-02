/*     */ package com.sun.mail.imap;
/*     */ 
/*     */ import com.sun.mail.iap.ConnectionException;
/*     */ import com.sun.mail.iap.ProtocolException;
/*     */ import com.sun.mail.imap.protocol.BODY;
/*     */ import com.sun.mail.imap.protocol.BODYSTRUCTURE;
/*     */ import com.sun.mail.imap.protocol.IMAPProtocol;
/*     */ import com.sun.mail.util.LineOutputStream;
/*     */ import com.sun.mail.util.PropUtil;
/*     */ import com.sun.mail.util.ReadableMime;
/*     */ import com.sun.mail.util.SharedByteArrayOutputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.SequenceInputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.Enumeration;
/*     */ import javax.activation.DataHandler;
/*     */ import javax.activation.DataSource;
/*     */ import javax.mail.FolderClosedException;
/*     */ import javax.mail.IllegalWriteException;
/*     */ import javax.mail.MessagingException;
/*     */ import javax.mail.Multipart;
/*     */ import javax.mail.internet.ContentType;
/*     */ import javax.mail.internet.InternetHeaders;
/*     */ import javax.mail.internet.MimeBodyPart;
/*     */ import javax.mail.internet.MimePart;
/*     */ import javax.mail.internet.MimeUtility;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IMAPBodyPart
/*     */   extends MimeBodyPart
/*     */   implements ReadableMime
/*     */ {
/*     */   private IMAPMessage message;
/*     */   private BODYSTRUCTURE bs;
/*     */   private String sectionId;
/*     */   private String type;
/*     */   private String description;
/*     */   private boolean headersLoaded = false;
/*  72 */   private static final boolean decodeFileName = PropUtil.getBooleanSystemProperty("mail.mime.decodefilename", false);
/*     */ 
/*     */ 
/*     */   
/*     */   protected IMAPBodyPart(BODYSTRUCTURE bs, String sid, IMAPMessage message) {
/*  77 */     this.bs = bs;
/*  78 */     this.sectionId = sid;
/*  79 */     this.message = message;
/*     */     
/*  81 */     ContentType ct = new ContentType(bs.type, bs.subtype, bs.cParams);
/*  82 */     this.type = ct.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void updateHeaders() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSize() throws MessagingException {
/*  95 */     return this.bs.size;
/*     */   }
/*     */   
/*     */   public int getLineCount() throws MessagingException {
/*  99 */     return this.bs.lines;
/*     */   }
/*     */   
/*     */   public String getContentType() throws MessagingException {
/* 103 */     return this.type;
/*     */   }
/*     */   
/*     */   public String getDisposition() throws MessagingException {
/* 107 */     return this.bs.disposition;
/*     */   }
/*     */   
/*     */   public void setDisposition(String disposition) throws MessagingException {
/* 111 */     throw new IllegalWriteException("IMAPBodyPart is read-only");
/*     */   }
/*     */   
/*     */   public String getEncoding() throws MessagingException {
/* 115 */     return this.bs.encoding;
/*     */   }
/*     */   
/*     */   public String getContentID() throws MessagingException {
/* 119 */     return this.bs.id;
/*     */   }
/*     */   
/*     */   public String getContentMD5() throws MessagingException {
/* 123 */     return this.bs.md5;
/*     */   }
/*     */   
/*     */   public void setContentMD5(String md5) throws MessagingException {
/* 127 */     throw new IllegalWriteException("IMAPBodyPart is read-only");
/*     */   }
/*     */   
/*     */   public String getDescription() throws MessagingException {
/* 131 */     if (this.description != null) {
/* 132 */       return this.description;
/*     */     }
/* 134 */     if (this.bs.description == null) {
/* 135 */       return null;
/*     */     }
/*     */     try {
/* 138 */       this.description = MimeUtility.decodeText(this.bs.description);
/* 139 */     } catch (UnsupportedEncodingException ex) {
/* 140 */       this.description = this.bs.description;
/*     */     } 
/*     */     
/* 143 */     return this.description;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDescription(String description, String charset) throws MessagingException {
/* 148 */     throw new IllegalWriteException("IMAPBodyPart is read-only");
/*     */   }
/*     */   
/*     */   public String getFileName() throws MessagingException {
/* 152 */     String filename = null;
/* 153 */     if (this.bs.dParams != null)
/* 154 */       filename = this.bs.dParams.get("filename"); 
/* 155 */     if (filename == null && this.bs.cParams != null)
/* 156 */       filename = this.bs.cParams.get("name"); 
/* 157 */     if (decodeFileName && filename != null) {
/*     */       try {
/* 159 */         filename = MimeUtility.decodeText(filename);
/* 160 */       } catch (UnsupportedEncodingException ex) {
/* 161 */         throw new MessagingException("Can't decode filename", ex);
/*     */       } 
/*     */     }
/* 164 */     return filename;
/*     */   }
/*     */   
/*     */   public void setFileName(String filename) throws MessagingException {
/* 168 */     throw new IllegalWriteException("IMAPBodyPart is read-only");
/*     */   }
/*     */   
/*     */   protected InputStream getContentStream() throws MessagingException {
/* 172 */     InputStream is = null;
/* 173 */     boolean pk = this.message.getPeek();
/*     */ 
/*     */     
/* 176 */     synchronized (this.message.getMessageCacheLock()) {
/*     */       try {
/* 178 */         BODY b; IMAPProtocol p = this.message.getProtocol();
/*     */ 
/*     */         
/* 181 */         this.message.checkExpunged();
/*     */         
/* 183 */         if (p.isREV1() && this.message.getFetchBlockSize() != -1) {
/* 184 */           return new IMAPInputStream(this.message, this.sectionId, this.message.ignoreBodyStructureSize() ? -1 : this.bs.size, pk);
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 189 */         int seqnum = this.message.getSequenceNumber();
/*     */         
/* 191 */         if (pk) {
/* 192 */           b = p.peekBody(seqnum, this.sectionId);
/*     */         } else {
/* 194 */           b = p.fetchBody(seqnum, this.sectionId);
/* 195 */         }  if (b != null)
/* 196 */           is = b.getByteArrayInputStream(); 
/* 197 */       } catch (ConnectionException cex) {
/* 198 */         throw new FolderClosedException(this.message.getFolder(), cex.getMessage());
/*     */       }
/* 200 */       catch (ProtocolException pex) {
/* 201 */         throw new MessagingException(pex.getMessage(), pex);
/*     */       } 
/*     */     } 
/*     */     
/* 205 */     if (is == null) {
/* 206 */       throw new MessagingException("No content");
/*     */     }
/* 208 */     return is;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private InputStream getHeaderStream() throws MessagingException {
/* 215 */     if (!this.message.isREV1()) {
/* 216 */       loadHeaders();
/*     */     }
/*     */     
/* 219 */     synchronized (this.message.getMessageCacheLock()) {
/*     */       
/* 221 */       IMAPProtocol p = this.message.getProtocol();
/*     */ 
/*     */       
/* 224 */       this.message.checkExpunged();
/*     */       
/* 226 */       if (p.isREV1()) {
/* 227 */         int seqnum = this.message.getSequenceNumber();
/* 228 */         BODY b = p.peekBody(seqnum, this.sectionId + ".MIME");
/*     */         
/* 230 */         if (b == null) {
/* 231 */           throw new MessagingException("Failed to fetch headers");
/*     */         }
/* 233 */         ByteArrayInputStream bis = b.getByteArrayInputStream();
/* 234 */         if (bis == null)
/* 235 */           throw new MessagingException("Failed to fetch headers"); 
/* 236 */         return bis;
/*     */       } 
/*     */ 
/*     */       
/* 240 */       SharedByteArrayOutputStream bos = new SharedByteArrayOutputStream(0);
/*     */       
/* 242 */       LineOutputStream los = new LineOutputStream((OutputStream)bos);
/*     */ 
/*     */       
/*     */       try {
/* 246 */         Enumeration hdrLines = super.getAllHeaderLines();
/* 247 */         while (hdrLines.hasMoreElements()) {
/* 248 */           los.writeln(hdrLines.nextElement());
/*     */         }
/*     */         
/* 251 */         los.writeln();
/* 252 */       } catch (IOException ioex) {
/*     */       
/*     */       } finally {
/*     */         try {
/* 256 */           los.close();
/* 257 */         } catch (IOException cex) {}
/*     */       } 
/* 259 */       return bos.toStream();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream getMimeStream() throws MessagingException {
/* 282 */     return new SequenceInputStream(getHeaderStream(), getContentStream());
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized DataHandler getDataHandler() throws MessagingException {
/* 287 */     if (this.dh == null) {
/* 288 */       if (this.bs.isMulti()) {
/* 289 */         this.dh = new DataHandler((DataSource)new IMAPMultipartDataSource((MimePart)this, this.bs.bodies, this.sectionId, this.message));
/*     */ 
/*     */       
/*     */       }
/* 293 */       else if (this.bs.isNested() && this.message.isREV1() && this.bs.envelope != null) {
/* 294 */         this.dh = new DataHandler(new IMAPNestedMessage(this.message, this.bs.bodies[0], this.bs.envelope, this.sectionId), this.type);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 303 */     return super.getDataHandler();
/*     */   }
/*     */   
/*     */   public void setDataHandler(DataHandler content) throws MessagingException {
/* 307 */     throw new IllegalWriteException("IMAPBodyPart is read-only");
/*     */   }
/*     */   
/*     */   public void setContent(Object o, String type) throws MessagingException {
/* 311 */     throw new IllegalWriteException("IMAPBodyPart is read-only");
/*     */   }
/*     */   
/*     */   public void setContent(Multipart mp) throws MessagingException {
/* 315 */     throw new IllegalWriteException("IMAPBodyPart is read-only");
/*     */   }
/*     */   
/*     */   public String[] getHeader(String name) throws MessagingException {
/* 319 */     loadHeaders();
/* 320 */     return super.getHeader(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setHeader(String name, String value) throws MessagingException {
/* 325 */     throw new IllegalWriteException("IMAPBodyPart is read-only");
/*     */   }
/*     */ 
/*     */   
/*     */   public void addHeader(String name, String value) throws MessagingException {
/* 330 */     throw new IllegalWriteException("IMAPBodyPart is read-only");
/*     */   }
/*     */   
/*     */   public void removeHeader(String name) throws MessagingException {
/* 334 */     throw new IllegalWriteException("IMAPBodyPart is read-only");
/*     */   }
/*     */   
/*     */   public Enumeration getAllHeaders() throws MessagingException {
/* 338 */     loadHeaders();
/* 339 */     return super.getAllHeaders();
/*     */   }
/*     */ 
/*     */   
/*     */   public Enumeration getMatchingHeaders(String[] names) throws MessagingException {
/* 344 */     loadHeaders();
/* 345 */     return super.getMatchingHeaders(names);
/*     */   }
/*     */ 
/*     */   
/*     */   public Enumeration getNonMatchingHeaders(String[] names) throws MessagingException {
/* 350 */     loadHeaders();
/* 351 */     return super.getNonMatchingHeaders(names);
/*     */   }
/*     */   
/*     */   public void addHeaderLine(String line) throws MessagingException {
/* 355 */     throw new IllegalWriteException("IMAPBodyPart is read-only");
/*     */   }
/*     */   
/*     */   public Enumeration getAllHeaderLines() throws MessagingException {
/* 359 */     loadHeaders();
/* 360 */     return super.getAllHeaderLines();
/*     */   }
/*     */ 
/*     */   
/*     */   public Enumeration getMatchingHeaderLines(String[] names) throws MessagingException {
/* 365 */     loadHeaders();
/* 366 */     return super.getMatchingHeaderLines(names);
/*     */   }
/*     */ 
/*     */   
/*     */   public Enumeration getNonMatchingHeaderLines(String[] names) throws MessagingException {
/* 371 */     loadHeaders();
/* 372 */     return super.getNonMatchingHeaderLines(names);
/*     */   }
/*     */   
/*     */   private synchronized void loadHeaders() throws MessagingException {
/* 376 */     if (this.headersLoaded) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 382 */     if (this.headers == null) {
/* 383 */       this.headers = new InternetHeaders();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 388 */     synchronized (this.message.getMessageCacheLock()) {
/*     */       try {
/* 390 */         IMAPProtocol p = this.message.getProtocol();
/*     */ 
/*     */         
/* 393 */         this.message.checkExpunged();
/*     */         
/* 395 */         if (p.isREV1()) {
/* 396 */           int seqnum = this.message.getSequenceNumber();
/* 397 */           BODY b = p.peekBody(seqnum, this.sectionId + ".MIME");
/*     */           
/* 399 */           if (b == null) {
/* 400 */             throw new MessagingException("Failed to fetch headers");
/*     */           }
/* 402 */           ByteArrayInputStream bis = b.getByteArrayInputStream();
/* 403 */           if (bis == null) {
/* 404 */             throw new MessagingException("Failed to fetch headers");
/*     */           }
/* 406 */           this.headers.load(bis);
/*     */ 
/*     */ 
/*     */         
/*     */         }
/*     */         else {
/*     */ 
/*     */ 
/*     */           
/* 415 */           this.headers.addHeader("Content-Type", this.type);
/*     */           
/* 417 */           this.headers.addHeader("Content-Transfer-Encoding", this.bs.encoding);
/*     */           
/* 419 */           if (this.bs.description != null) {
/* 420 */             this.headers.addHeader("Content-Description", this.bs.description);
/*     */           }
/*     */           
/* 423 */           if (this.bs.id != null) {
/* 424 */             this.headers.addHeader("Content-ID", this.bs.id);
/*     */           }
/* 426 */           if (this.bs.md5 != null)
/* 427 */             this.headers.addHeader("Content-MD5", this.bs.md5); 
/*     */         } 
/* 429 */       } catch (ConnectionException cex) {
/* 430 */         throw new FolderClosedException(this.message.getFolder(), cex.getMessage());
/*     */       }
/* 432 */       catch (ProtocolException pex) {
/* 433 */         throw new MessagingException(pex.getMessage(), pex);
/*     */       } 
/*     */     } 
/* 436 */     this.headersLoaded = true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\IMAPBodyPart.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */