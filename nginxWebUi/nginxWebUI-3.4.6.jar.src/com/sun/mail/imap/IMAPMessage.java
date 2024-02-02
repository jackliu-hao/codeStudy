/*      */ package com.sun.mail.imap;
/*      */ 
/*      */ import com.sun.mail.iap.ConnectionException;
/*      */ import com.sun.mail.iap.ProtocolException;
/*      */ import com.sun.mail.iap.Response;
/*      */ import com.sun.mail.imap.protocol.BODY;
/*      */ import com.sun.mail.imap.protocol.BODYSTRUCTURE;
/*      */ import com.sun.mail.imap.protocol.ENVELOPE;
/*      */ import com.sun.mail.imap.protocol.FetchItem;
/*      */ import com.sun.mail.imap.protocol.FetchResponse;
/*      */ import com.sun.mail.imap.protocol.IMAPProtocol;
/*      */ import com.sun.mail.imap.protocol.INTERNALDATE;
/*      */ import com.sun.mail.imap.protocol.Item;
/*      */ import com.sun.mail.imap.protocol.RFC822DATA;
/*      */ import com.sun.mail.imap.protocol.RFC822SIZE;
/*      */ import com.sun.mail.imap.protocol.UID;
/*      */ import com.sun.mail.util.ReadableMime;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.util.Date;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashSet;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import javax.activation.DataHandler;
/*      */ import javax.activation.DataSource;
/*      */ import javax.mail.Address;
/*      */ import javax.mail.FetchProfile;
/*      */ import javax.mail.Flags;
/*      */ import javax.mail.FolderClosedException;
/*      */ import javax.mail.Header;
/*      */ import javax.mail.IllegalWriteException;
/*      */ import javax.mail.Message;
/*      */ import javax.mail.MessageRemovedException;
/*      */ import javax.mail.MessagingException;
/*      */ import javax.mail.Session;
/*      */ import javax.mail.UIDFolder;
/*      */ import javax.mail.internet.ContentType;
/*      */ import javax.mail.internet.InternetAddress;
/*      */ import javax.mail.internet.InternetHeaders;
/*      */ import javax.mail.internet.MimeMessage;
/*      */ import javax.mail.internet.MimePart;
/*      */ import javax.mail.internet.MimeUtility;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class IMAPMessage
/*      */   extends MimeMessage
/*      */   implements ReadableMime
/*      */ {
/*      */   protected BODYSTRUCTURE bs;
/*      */   protected ENVELOPE envelope;
/*      */   protected Map items;
/*      */   private Date receivedDate;
/*   94 */   private int size = -1;
/*      */ 
/*      */   
/*      */   private boolean peek;
/*      */   
/*   99 */   private long uid = -1L;
/*      */ 
/*      */ 
/*      */   
/*      */   protected String sectionId;
/*      */ 
/*      */ 
/*      */   
/*      */   private String type;
/*      */ 
/*      */ 
/*      */   
/*      */   private String subject;
/*      */ 
/*      */ 
/*      */   
/*      */   private String description;
/*      */ 
/*      */   
/*      */   private volatile boolean headersLoaded = false;
/*      */ 
/*      */   
/*  121 */   private Hashtable loadedHeaders = new Hashtable(1);
/*      */ 
/*      */ 
/*      */   
/*      */   static final String EnvelopeCmd = "ENVELOPE INTERNALDATE RFC822.SIZE";
/*      */ 
/*      */ 
/*      */   
/*      */   protected IMAPMessage(IMAPFolder folder, int msgnum) {
/*  130 */     super(folder, msgnum);
/*  131 */     this.flags = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected IMAPMessage(Session session) {
/*  138 */     super(session);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected IMAPProtocol getProtocol() throws ProtocolException, FolderClosedException {
/*  150 */     ((IMAPFolder)this.folder).waitIfIdle();
/*  151 */     IMAPProtocol p = ((IMAPFolder)this.folder).protocol;
/*  152 */     if (p == null) {
/*  153 */       throw new FolderClosedException(this.folder);
/*      */     }
/*  155 */     return p;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean isREV1() throws FolderClosedException {
/*  164 */     IMAPProtocol p = ((IMAPFolder)this.folder).protocol;
/*  165 */     if (p == null) {
/*  166 */       throw new FolderClosedException(this.folder);
/*      */     }
/*  168 */     return p.isREV1();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object getMessageCacheLock() {
/*  176 */     return ((IMAPFolder)this.folder).messageCacheLock;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int getSequenceNumber() {
/*  186 */     return ((IMAPFolder)this.folder).messageCache.seqnumOf(getMessageNumber());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setMessageNumber(int msgnum) {
/*  194 */     super.setMessageNumber(msgnum);
/*      */   }
/*      */   
/*      */   protected long getUID() {
/*  198 */     return this.uid;
/*      */   }
/*      */   
/*      */   protected void setUID(long uid) {
/*  202 */     this.uid = uid;
/*      */   }
/*      */ 
/*      */   
/*      */   protected void setExpunged(boolean set) {
/*  207 */     super.setExpunged(set);
/*      */   }
/*      */ 
/*      */   
/*      */   protected void checkExpunged() throws MessageRemovedException {
/*  212 */     if (this.expunged) {
/*  213 */       throw new MessageRemovedException();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void forceCheckExpunged() throws MessageRemovedException, FolderClosedException {
/*  222 */     synchronized (getMessageCacheLock()) {
/*      */       try {
/*  224 */         getProtocol().noop();
/*  225 */       } catch (ConnectionException cex) {
/*  226 */         throw new FolderClosedException(this.folder, cex.getMessage());
/*  227 */       } catch (ProtocolException pex) {}
/*      */     } 
/*      */ 
/*      */     
/*  231 */     if (this.expunged) {
/*  232 */       throw new MessageRemovedException();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected int getFetchBlockSize() {
/*  238 */     return ((IMAPStore)this.folder.getStore()).getFetchBlockSize();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean ignoreBodyStructureSize() {
/*  244 */     return ((IMAPStore)this.folder.getStore()).ignoreBodyStructureSize();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Address[] getFrom() throws MessagingException {
/*  251 */     checkExpunged();
/*  252 */     loadEnvelope();
/*  253 */     InternetAddress[] a = this.envelope.from;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  262 */     if (a == null || a.length == 0)
/*  263 */       a = this.envelope.sender; 
/*  264 */     return (Address[])aaclone(a);
/*      */   }
/*      */   
/*      */   public void setFrom(Address address) throws MessagingException {
/*  268 */     throw new IllegalWriteException("IMAPMessage is read-only");
/*      */   }
/*      */   
/*      */   public void addFrom(Address[] addresses) throws MessagingException {
/*  272 */     throw new IllegalWriteException("IMAPMessage is read-only");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Address getSender() throws MessagingException {
/*  279 */     checkExpunged();
/*  280 */     loadEnvelope();
/*  281 */     if (this.envelope.sender != null) {
/*  282 */       return (Address)this.envelope.sender[0];
/*      */     }
/*  284 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setSender(Address address) throws MessagingException {
/*  289 */     throw new IllegalWriteException("IMAPMessage is read-only");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Address[] getRecipients(Message.RecipientType type) throws MessagingException {
/*  297 */     checkExpunged();
/*  298 */     loadEnvelope();
/*      */     
/*  300 */     if (type == Message.RecipientType.TO)
/*  301 */       return (Address[])aaclone(this.envelope.to); 
/*  302 */     if (type == Message.RecipientType.CC)
/*  303 */       return (Address[])aaclone(this.envelope.cc); 
/*  304 */     if (type == Message.RecipientType.BCC) {
/*  305 */       return (Address[])aaclone(this.envelope.bcc);
/*      */     }
/*  307 */     return super.getRecipients(type);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setRecipients(Message.RecipientType type, Address[] addresses) throws MessagingException {
/*  312 */     throw new IllegalWriteException("IMAPMessage is read-only");
/*      */   }
/*      */ 
/*      */   
/*      */   public void addRecipients(Message.RecipientType type, Address[] addresses) throws MessagingException {
/*  317 */     throw new IllegalWriteException("IMAPMessage is read-only");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Address[] getReplyTo() throws MessagingException {
/*  324 */     checkExpunged();
/*  325 */     loadEnvelope();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  332 */     if (this.envelope.replyTo == null || this.envelope.replyTo.length == 0)
/*  333 */       return getFrom(); 
/*  334 */     return (Address[])aaclone(this.envelope.replyTo);
/*      */   }
/*      */   
/*      */   public void setReplyTo(Address[] addresses) throws MessagingException {
/*  338 */     throw new IllegalWriteException("IMAPMessage is read-only");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getSubject() throws MessagingException {
/*  345 */     checkExpunged();
/*      */     
/*  347 */     if (this.subject != null) {
/*  348 */       return this.subject;
/*      */     }
/*  350 */     loadEnvelope();
/*  351 */     if (this.envelope.subject == null) {
/*  352 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  358 */       this.subject = MimeUtility.decodeText(MimeUtility.unfold(this.envelope.subject));
/*      */     }
/*  360 */     catch (UnsupportedEncodingException ex) {
/*  361 */       this.subject = this.envelope.subject;
/*      */     } 
/*      */     
/*  364 */     return this.subject;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setSubject(String subject, String charset) throws MessagingException {
/*  369 */     throw new IllegalWriteException("IMAPMessage is read-only");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getSentDate() throws MessagingException {
/*  376 */     checkExpunged();
/*  377 */     loadEnvelope();
/*  378 */     if (this.envelope.date == null) {
/*  379 */       return null;
/*      */     }
/*  381 */     return new Date(this.envelope.date.getTime());
/*      */   }
/*      */   
/*      */   public void setSentDate(Date d) throws MessagingException {
/*  385 */     throw new IllegalWriteException("IMAPMessage is read-only");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getReceivedDate() throws MessagingException {
/*  392 */     checkExpunged();
/*  393 */     loadEnvelope();
/*  394 */     if (this.receivedDate == null) {
/*  395 */       return null;
/*      */     }
/*  397 */     return new Date(this.receivedDate.getTime());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getSize() throws MessagingException {
/*  407 */     checkExpunged();
/*  408 */     if (this.size == -1)
/*  409 */       loadEnvelope(); 
/*  410 */     return this.size;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getLineCount() throws MessagingException {
/*  421 */     checkExpunged();
/*  422 */     loadBODYSTRUCTURE();
/*  423 */     return this.bs.lines;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String[] getContentLanguage() throws MessagingException {
/*  430 */     checkExpunged();
/*  431 */     loadBODYSTRUCTURE();
/*  432 */     if (this.bs.language != null) {
/*  433 */       return (String[])this.bs.language.clone();
/*      */     }
/*  435 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setContentLanguage(String[] languages) throws MessagingException {
/*  440 */     throw new IllegalWriteException("IMAPMessage is read-only");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getInReplyTo() throws MessagingException {
/*  449 */     checkExpunged();
/*  450 */     loadEnvelope();
/*  451 */     return this.envelope.inReplyTo;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized String getContentType() throws MessagingException {
/*  461 */     checkExpunged();
/*      */ 
/*      */     
/*  464 */     if (this.type == null) {
/*  465 */       loadBODYSTRUCTURE();
/*      */       
/*  467 */       ContentType ct = new ContentType(this.bs.type, this.bs.subtype, this.bs.cParams);
/*  468 */       this.type = ct.toString();
/*      */     } 
/*  470 */     return this.type;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDisposition() throws MessagingException {
/*  477 */     checkExpunged();
/*  478 */     loadBODYSTRUCTURE();
/*  479 */     return this.bs.disposition;
/*      */   }
/*      */   
/*      */   public void setDisposition(String disposition) throws MessagingException {
/*  483 */     throw new IllegalWriteException("IMAPMessage is read-only");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getEncoding() throws MessagingException {
/*  490 */     checkExpunged();
/*  491 */     loadBODYSTRUCTURE();
/*  492 */     return this.bs.encoding;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getContentID() throws MessagingException {
/*  499 */     checkExpunged();
/*  500 */     loadBODYSTRUCTURE();
/*  501 */     return this.bs.id;
/*      */   }
/*      */   
/*      */   public void setContentID(String cid) throws MessagingException {
/*  505 */     throw new IllegalWriteException("IMAPMessage is read-only");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getContentMD5() throws MessagingException {
/*  512 */     checkExpunged();
/*  513 */     loadBODYSTRUCTURE();
/*  514 */     return this.bs.md5;
/*      */   }
/*      */   
/*      */   public void setContentMD5(String md5) throws MessagingException {
/*  518 */     throw new IllegalWriteException("IMAPMessage is read-only");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDescription() throws MessagingException {
/*  525 */     checkExpunged();
/*      */     
/*  527 */     if (this.description != null) {
/*  528 */       return this.description;
/*      */     }
/*  530 */     loadBODYSTRUCTURE();
/*  531 */     if (this.bs.description == null) {
/*  532 */       return null;
/*      */     }
/*      */     try {
/*  535 */       this.description = MimeUtility.decodeText(this.bs.description);
/*  536 */     } catch (UnsupportedEncodingException ex) {
/*  537 */       this.description = this.bs.description;
/*      */     } 
/*      */     
/*  540 */     return this.description;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setDescription(String description, String charset) throws MessagingException {
/*  545 */     throw new IllegalWriteException("IMAPMessage is read-only");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getMessageID() throws MessagingException {
/*  552 */     checkExpunged();
/*  553 */     loadEnvelope();
/*  554 */     return this.envelope.messageId;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getFileName() throws MessagingException {
/*  563 */     checkExpunged();
/*      */     
/*  565 */     String filename = null;
/*  566 */     loadBODYSTRUCTURE();
/*      */     
/*  568 */     if (this.bs.dParams != null)
/*  569 */       filename = this.bs.dParams.get("filename"); 
/*  570 */     if (filename == null && this.bs.cParams != null)
/*  571 */       filename = this.bs.cParams.get("name"); 
/*  572 */     return filename;
/*      */   }
/*      */   
/*      */   public void setFileName(String filename) throws MessagingException {
/*  576 */     throw new IllegalWriteException("IMAPMessage is read-only");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected InputStream getContentStream() throws MessagingException {
/*  587 */     InputStream is = null;
/*  588 */     boolean pk = getPeek();
/*      */ 
/*      */     
/*  591 */     synchronized (getMessageCacheLock()) {
/*      */       try {
/*  593 */         IMAPProtocol p = getProtocol();
/*      */ 
/*      */ 
/*      */         
/*  597 */         checkExpunged();
/*      */         
/*  599 */         if (p.isREV1() && getFetchBlockSize() != -1) {
/*  600 */           return new IMAPInputStream(this, toSection("TEXT"), (this.bs != null && !ignoreBodyStructureSize()) ? this.bs.size : -1, pk);
/*      */         }
/*      */ 
/*      */         
/*  604 */         if (p.isREV1()) {
/*      */           BODY b;
/*  606 */           if (pk) {
/*  607 */             b = p.peekBody(getSequenceNumber(), toSection("TEXT"));
/*      */           } else {
/*  609 */             b = p.fetchBody(getSequenceNumber(), toSection("TEXT"));
/*  610 */           }  if (b != null)
/*  611 */             is = b.getByteArrayInputStream(); 
/*      */         } else {
/*  613 */           RFC822DATA rd = p.fetchRFC822(getSequenceNumber(), "TEXT");
/*  614 */           if (rd != null)
/*  615 */             is = rd.getByteArrayInputStream(); 
/*      */         } 
/*  617 */       } catch (ConnectionException cex) {
/*  618 */         throw new FolderClosedException(this.folder, cex.getMessage());
/*  619 */       } catch (ProtocolException pex) {
/*  620 */         forceCheckExpunged();
/*  621 */         throw new MessagingException(pex.getMessage(), pex);
/*      */       } 
/*      */     } 
/*      */     
/*  625 */     if (is == null) {
/*  626 */       throw new MessagingException("No content");
/*      */     }
/*  628 */     return is;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized DataHandler getDataHandler() throws MessagingException {
/*  636 */     checkExpunged();
/*      */     
/*  638 */     if (this.dh == null) {
/*  639 */       loadBODYSTRUCTURE();
/*  640 */       if (this.type == null) {
/*      */         
/*  642 */         ContentType ct = new ContentType(this.bs.type, this.bs.subtype, this.bs.cParams);
/*      */         
/*  644 */         this.type = ct.toString();
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  650 */       if (this.bs.isMulti()) {
/*  651 */         this.dh = new DataHandler((DataSource)new IMAPMultipartDataSource((MimePart)this, this.bs.bodies, this.sectionId, this));
/*      */ 
/*      */       
/*      */       }
/*  655 */       else if (this.bs.isNested() && isREV1() && this.bs.envelope != null) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  660 */         this.dh = new DataHandler(new IMAPNestedMessage(this, this.bs.bodies[0], this.bs.envelope, (this.sectionId == null) ? "1" : (this.sectionId + ".1")), this.type);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  669 */     return super.getDataHandler();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setDataHandler(DataHandler content) throws MessagingException {
/*  674 */     throw new IllegalWriteException("IMAPMessage is read-only");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InputStream getMimeStream() throws MessagingException {
/*  684 */     InputStream is = null;
/*  685 */     boolean pk = getPeek();
/*      */ 
/*      */     
/*  688 */     synchronized (getMessageCacheLock()) {
/*      */       try {
/*  690 */         IMAPProtocol p = getProtocol();
/*      */         
/*  692 */         checkExpunged();
/*      */         
/*  694 */         if (p.isREV1() && getFetchBlockSize() != -1) {
/*  695 */           return new IMAPInputStream(this, this.sectionId, -1, pk);
/*      */         }
/*  697 */         if (p.isREV1()) {
/*      */           BODY b;
/*  699 */           if (pk) {
/*  700 */             b = p.peekBody(getSequenceNumber(), this.sectionId);
/*      */           } else {
/*  702 */             b = p.fetchBody(getSequenceNumber(), this.sectionId);
/*  703 */           }  if (b != null)
/*  704 */             is = b.getByteArrayInputStream(); 
/*      */         } else {
/*  706 */           RFC822DATA rd = p.fetchRFC822(getSequenceNumber(), null);
/*  707 */           if (rd != null)
/*  708 */             is = rd.getByteArrayInputStream(); 
/*      */         } 
/*  710 */       } catch (ConnectionException cex) {
/*  711 */         throw new FolderClosedException(this.folder, cex.getMessage());
/*  712 */       } catch (ProtocolException pex) {
/*  713 */         forceCheckExpunged();
/*  714 */         throw new MessagingException(pex.getMessage(), pex);
/*      */       } 
/*      */     } 
/*      */     
/*  718 */     if (is == null) {
/*  719 */       forceCheckExpunged();
/*      */ 
/*      */       
/*  722 */       throw new MessagingException("No content");
/*      */     } 
/*  724 */     return is;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeTo(OutputStream os) throws IOException, MessagingException {
/*  732 */     InputStream is = getMimeStream();
/*      */     
/*      */     try {
/*  735 */       byte[] bytes = new byte[16384];
/*      */       int count;
/*  737 */       while ((count = is.read(bytes)) != -1)
/*  738 */         os.write(bytes, 0, count); 
/*      */     } finally {
/*  740 */       is.close();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String[] getHeader(String name) throws MessagingException {
/*  748 */     checkExpunged();
/*      */     
/*  750 */     if (isHeaderLoaded(name)) {
/*  751 */       return this.headers.getHeader(name);
/*      */     }
/*      */     
/*  754 */     InputStream is = null;
/*      */ 
/*      */     
/*  757 */     synchronized (getMessageCacheLock()) {
/*      */       try {
/*  759 */         IMAPProtocol p = getProtocol();
/*      */ 
/*      */ 
/*      */         
/*  763 */         checkExpunged();
/*      */         
/*  765 */         if (p.isREV1()) {
/*  766 */           BODY b = p.peekBody(getSequenceNumber(), toSection("HEADER.FIELDS (" + name + ")"));
/*      */ 
/*      */           
/*  769 */           if (b != null)
/*  770 */             is = b.getByteArrayInputStream(); 
/*      */         } else {
/*  772 */           RFC822DATA rd = p.fetchRFC822(getSequenceNumber(), "HEADER.LINES (" + name + ")");
/*      */           
/*  774 */           if (rd != null)
/*  775 */             is = rd.getByteArrayInputStream(); 
/*      */         } 
/*  777 */       } catch (ConnectionException cex) {
/*  778 */         throw new FolderClosedException(this.folder, cex.getMessage());
/*  779 */       } catch (ProtocolException pex) {
/*  780 */         forceCheckExpunged();
/*  781 */         throw new MessagingException(pex.getMessage(), pex);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  787 */     if (is == null) {
/*  788 */       return null;
/*      */     }
/*  790 */     if (this.headers == null)
/*  791 */       this.headers = new InternetHeaders(); 
/*  792 */     this.headers.load(is);
/*  793 */     setHeaderLoaded(name);
/*      */     
/*  795 */     return this.headers.getHeader(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getHeader(String name, String delimiter) throws MessagingException {
/*  803 */     checkExpunged();
/*      */ 
/*      */     
/*  806 */     if (getHeader(name) == null)
/*  807 */       return null; 
/*  808 */     return this.headers.getHeader(name, delimiter);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setHeader(String name, String value) throws MessagingException {
/*  813 */     throw new IllegalWriteException("IMAPMessage is read-only");
/*      */   }
/*      */ 
/*      */   
/*      */   public void addHeader(String name, String value) throws MessagingException {
/*  818 */     throw new IllegalWriteException("IMAPMessage is read-only");
/*      */   }
/*      */ 
/*      */   
/*      */   public void removeHeader(String name) throws MessagingException {
/*  823 */     throw new IllegalWriteException("IMAPMessage is read-only");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Enumeration getAllHeaders() throws MessagingException {
/*  830 */     checkExpunged();
/*  831 */     loadHeaders();
/*  832 */     return super.getAllHeaders();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Enumeration getMatchingHeaders(String[] names) throws MessagingException {
/*  840 */     checkExpunged();
/*  841 */     loadHeaders();
/*  842 */     return super.getMatchingHeaders(names);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Enumeration getNonMatchingHeaders(String[] names) throws MessagingException {
/*  850 */     checkExpunged();
/*  851 */     loadHeaders();
/*  852 */     return super.getNonMatchingHeaders(names);
/*      */   }
/*      */   
/*      */   public void addHeaderLine(String line) throws MessagingException {
/*  856 */     throw new IllegalWriteException("IMAPMessage is read-only");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Enumeration getAllHeaderLines() throws MessagingException {
/*  863 */     checkExpunged();
/*  864 */     loadHeaders();
/*  865 */     return super.getAllHeaderLines();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Enumeration getMatchingHeaderLines(String[] names) throws MessagingException {
/*  873 */     checkExpunged();
/*  874 */     loadHeaders();
/*  875 */     return super.getMatchingHeaderLines(names);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Enumeration getNonMatchingHeaderLines(String[] names) throws MessagingException {
/*  883 */     checkExpunged();
/*  884 */     loadHeaders();
/*  885 */     return super.getNonMatchingHeaderLines(names);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Flags getFlags() throws MessagingException {
/*  892 */     checkExpunged();
/*  893 */     loadFlags();
/*  894 */     return super.getFlags();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean isSet(Flags.Flag flag) throws MessagingException {
/*  902 */     checkExpunged();
/*  903 */     loadFlags();
/*  904 */     return super.isSet(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setFlags(Flags flag, boolean set) throws MessagingException {
/*  913 */     synchronized (getMessageCacheLock()) {
/*      */       try {
/*  915 */         IMAPProtocol p = getProtocol();
/*  916 */         checkExpunged();
/*  917 */         p.storeFlags(getSequenceNumber(), flag, set);
/*  918 */       } catch (ConnectionException cex) {
/*  919 */         throw new FolderClosedException(this.folder, cex.getMessage());
/*  920 */       } catch (ProtocolException pex) {
/*  921 */         throw new MessagingException(pex.getMessage(), pex);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setPeek(boolean peek) {
/*  933 */     this.peek = peek;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean getPeek() {
/*  943 */     return this.peek;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void invalidateHeaders() {
/*  954 */     this.headersLoaded = false;
/*  955 */     this.loadedHeaders.clear();
/*  956 */     this.headers = null;
/*  957 */     this.envelope = null;
/*  958 */     this.bs = null;
/*  959 */     this.receivedDate = null;
/*  960 */     this.size = -1;
/*  961 */     this.type = null;
/*  962 */     this.subject = null;
/*  963 */     this.description = null;
/*  964 */     this.flags = null;
/*      */   }
/*      */ 
/*      */   
/*      */   public static class FetchProfileCondition
/*      */     implements Utility.Condition
/*      */   {
/*      */     private boolean needEnvelope = false;
/*      */     
/*      */     private boolean needFlags = false;
/*      */     
/*      */     private boolean needBodyStructure = false;
/*      */     
/*      */     private boolean needUID = false;
/*      */     
/*      */     private boolean needHeaders = false;
/*      */     private boolean needSize = false;
/*  981 */     private String[] hdrs = null;
/*  982 */     private Set need = new HashSet();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public FetchProfileCondition(FetchProfile fp, FetchItem[] fitems) {
/*  989 */       if (fp.contains(FetchProfile.Item.ENVELOPE))
/*  990 */         this.needEnvelope = true; 
/*  991 */       if (fp.contains(FetchProfile.Item.FLAGS))
/*  992 */         this.needFlags = true; 
/*  993 */       if (fp.contains(FetchProfile.Item.CONTENT_INFO))
/*  994 */         this.needBodyStructure = true; 
/*  995 */       if (fp.contains((FetchProfile.Item)UIDFolder.FetchProfileItem.UID))
/*  996 */         this.needUID = true; 
/*  997 */       if (fp.contains(IMAPFolder.FetchProfileItem.HEADERS))
/*  998 */         this.needHeaders = true; 
/*  999 */       if (fp.contains(IMAPFolder.FetchProfileItem.SIZE))
/* 1000 */         this.needSize = true; 
/* 1001 */       this.hdrs = fp.getHeaderNames();
/* 1002 */       for (int i = 0; i < fitems.length; i++) {
/* 1003 */         if (fp.contains(fitems[i].getFetchProfileItem())) {
/* 1004 */           this.need.add(fitems[i]);
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean test(IMAPMessage m) {
/* 1013 */       if (this.needEnvelope && m._getEnvelope() == null)
/* 1014 */         return true; 
/* 1015 */       if (this.needFlags && m._getFlags() == null)
/* 1016 */         return true; 
/* 1017 */       if (this.needBodyStructure && m._getBodyStructure() == null)
/* 1018 */         return true; 
/* 1019 */       if (this.needUID && m.getUID() == -1L)
/* 1020 */         return true; 
/* 1021 */       if (this.needHeaders && !m.areHeadersLoaded())
/* 1022 */         return true; 
/* 1023 */       if (this.needSize && m.size == -1) {
/* 1024 */         return true;
/*      */       }
/*      */       
/* 1027 */       for (int i = 0; i < this.hdrs.length; i++) {
/* 1028 */         if (!m.isHeaderLoaded(this.hdrs[i]))
/* 1029 */           return true; 
/*      */       } 
/* 1031 */       Iterator it = this.need.iterator();
/* 1032 */       while (it.hasNext()) {
/* 1033 */         FetchItem fitem = it.next();
/* 1034 */         if (m.items == null || m.items.get(fitem.getName()) == null) {
/* 1035 */           return true;
/*      */         }
/*      */       } 
/* 1038 */       return false;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean handleFetchItem(Item item, String[] hdrs, boolean allHeaders) throws MessagingException {
/* 1053 */     if (item instanceof Flags) {
/* 1054 */       this.flags = (Flags)item;
/*      */     }
/* 1056 */     else if (item instanceof ENVELOPE) {
/* 1057 */       this.envelope = (ENVELOPE)item;
/* 1058 */     } else if (item instanceof INTERNALDATE) {
/* 1059 */       this.receivedDate = ((INTERNALDATE)item).getDate();
/* 1060 */     } else if (item instanceof RFC822SIZE) {
/* 1061 */       this.size = ((RFC822SIZE)item).size;
/*      */     
/*      */     }
/* 1064 */     else if (item instanceof BODYSTRUCTURE) {
/* 1065 */       this.bs = (BODYSTRUCTURE)item;
/*      */     }
/* 1067 */     else if (item instanceof UID) {
/* 1068 */       UID u = (UID)item;
/* 1069 */       this.uid = u.uid;
/*      */       
/* 1071 */       if (((IMAPFolder)this.folder).uidTable == null)
/* 1072 */         ((IMAPFolder)this.folder).uidTable = new Hashtable(); 
/* 1073 */       ((IMAPFolder)this.folder).uidTable.put(new Long(u.uid), this);
/*      */ 
/*      */     
/*      */     }
/* 1077 */     else if (item instanceof RFC822DATA || item instanceof BODY) {
/*      */       InputStream headerStream;
/*      */       
/* 1080 */       if (item instanceof RFC822DATA) {
/* 1081 */         headerStream = ((RFC822DATA)item).getByteArrayInputStream();
/*      */       } else {
/*      */         
/* 1084 */         headerStream = ((BODY)item).getByteArrayInputStream();
/*      */       } 
/*      */ 
/*      */       
/* 1088 */       InternetHeaders h = new InternetHeaders();
/*      */ 
/*      */ 
/*      */       
/* 1092 */       if (headerStream != null)
/* 1093 */         h.load(headerStream); 
/* 1094 */       if (this.headers == null || allHeaders) {
/* 1095 */         this.headers = h;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*      */       else {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1111 */         Enumeration e = h.getAllHeaders();
/* 1112 */         while (e.hasMoreElements()) {
/* 1113 */           Header he = e.nextElement();
/* 1114 */           if (!isHeaderLoaded(he.getName())) {
/* 1115 */             this.headers.addHeader(he.getName(), he.getValue());
/*      */           }
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 1121 */       if (allHeaders) {
/* 1122 */         setHeadersLoaded(true);
/*      */       } else {
/*      */         
/* 1125 */         for (int k = 0; k < hdrs.length; k++)
/* 1126 */           setHeaderLoaded(hdrs[k]); 
/*      */       } 
/*      */     } else {
/* 1129 */       return false;
/* 1130 */     }  return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void handleExtensionFetchItems(Map extensionItems) throws MessagingException {
/* 1145 */     if (this.items == null) {
/* 1146 */       this.items = extensionItems;
/*      */     } else {
/* 1148 */       this.items.putAll(extensionItems);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object fetchItem(FetchItem fitem) throws MessagingException {
/* 1163 */     synchronized (getMessageCacheLock()) {
/* 1164 */       Object robj = null;
/*      */       
/*      */       try {
/* 1167 */         IMAPProtocol p = getProtocol();
/*      */         
/* 1169 */         checkExpunged();
/*      */         
/* 1171 */         int seqnum = getSequenceNumber();
/* 1172 */         Response[] r = p.fetch(seqnum, fitem.getName());
/*      */         
/* 1174 */         for (int i = 0; i < r.length; i++) {
/*      */ 
/*      */           
/* 1177 */           if (r[i] != null && r[i] instanceof FetchResponse && ((FetchResponse)r[i]).getNumber() == seqnum) {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1182 */             FetchResponse f = (FetchResponse)r[i];
/* 1183 */             Object o = f.getExtensionItems().get(fitem.getName());
/* 1184 */             if (o != null) {
/* 1185 */               robj = o;
/*      */             }
/*      */           } 
/*      */         } 
/* 1189 */         p.notifyResponseHandlers(r);
/* 1190 */         p.handleResult(r[r.length - 1]);
/* 1191 */       } catch (ConnectionException cex) {
/* 1192 */         throw new FolderClosedException(this.folder, cex.getMessage());
/* 1193 */       } catch (ProtocolException pex) {
/* 1194 */         forceCheckExpunged();
/* 1195 */         throw new MessagingException(pex.getMessage(), pex);
/*      */       } 
/* 1197 */       return robj;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Object getItem(FetchItem fitem) throws MessagingException {
/* 1212 */     Object item = (this.items == null) ? null : this.items.get(fitem.getName());
/* 1213 */     if (item == null)
/* 1214 */       item = fetchItem(fitem); 
/* 1215 */     return item;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private synchronized void loadEnvelope() throws MessagingException {
/* 1222 */     if (this.envelope != null) {
/*      */       return;
/*      */     }
/* 1225 */     Response[] r = null;
/*      */ 
/*      */     
/* 1228 */     synchronized (getMessageCacheLock()) {
/*      */       try {
/* 1230 */         IMAPProtocol p = getProtocol();
/*      */         
/* 1232 */         checkExpunged();
/*      */         
/* 1234 */         int seqnum = getSequenceNumber();
/* 1235 */         r = p.fetch(seqnum, "ENVELOPE INTERNALDATE RFC822.SIZE");
/*      */         
/* 1237 */         for (int i = 0; i < r.length; i++) {
/*      */ 
/*      */           
/* 1240 */           if (r[i] != null && r[i] instanceof FetchResponse && ((FetchResponse)r[i]).getNumber() == seqnum) {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1245 */             FetchResponse f = (FetchResponse)r[i];
/*      */ 
/*      */             
/* 1248 */             int count = f.getItemCount();
/* 1249 */             for (int j = 0; j < count; j++) {
/* 1250 */               Item item = f.getItem(j);
/*      */               
/* 1252 */               if (item instanceof ENVELOPE) {
/* 1253 */                 this.envelope = (ENVELOPE)item;
/* 1254 */               } else if (item instanceof INTERNALDATE) {
/* 1255 */                 this.receivedDate = ((INTERNALDATE)item).getDate();
/* 1256 */               } else if (item instanceof RFC822SIZE) {
/* 1257 */                 this.size = ((RFC822SIZE)item).size;
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/* 1262 */         p.notifyResponseHandlers(r);
/* 1263 */         p.handleResult(r[r.length - 1]);
/* 1264 */       } catch (ConnectionException cex) {
/* 1265 */         throw new FolderClosedException(this.folder, cex.getMessage());
/* 1266 */       } catch (ProtocolException pex) {
/* 1267 */         forceCheckExpunged();
/* 1268 */         throw new MessagingException(pex.getMessage(), pex);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1273 */     if (this.envelope == null) {
/* 1274 */       throw new MessagingException("Failed to load IMAP envelope");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private synchronized void loadBODYSTRUCTURE() throws MessagingException {
/* 1282 */     if (this.bs != null) {
/*      */       return;
/*      */     }
/*      */     
/* 1286 */     synchronized (getMessageCacheLock()) {
/*      */       try {
/* 1288 */         IMAPProtocol p = getProtocol();
/*      */ 
/*      */ 
/*      */         
/* 1292 */         checkExpunged();
/*      */         
/* 1294 */         this.bs = p.fetchBodyStructure(getSequenceNumber());
/* 1295 */       } catch (ConnectionException cex) {
/* 1296 */         throw new FolderClosedException(this.folder, cex.getMessage());
/* 1297 */       } catch (ProtocolException pex) {
/* 1298 */         forceCheckExpunged();
/* 1299 */         throw new MessagingException(pex.getMessage(), pex);
/*      */       } 
/* 1301 */       if (this.bs == null) {
/*      */ 
/*      */ 
/*      */         
/* 1305 */         forceCheckExpunged();
/* 1306 */         throw new MessagingException("Unable to load BODYSTRUCTURE");
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private synchronized void loadHeaders() throws MessagingException {
/* 1315 */     if (this.headersLoaded) {
/*      */       return;
/*      */     }
/* 1318 */     InputStream is = null;
/*      */ 
/*      */     
/* 1321 */     synchronized (getMessageCacheLock()) {
/*      */       try {
/* 1323 */         IMAPProtocol p = getProtocol();
/*      */ 
/*      */ 
/*      */         
/* 1327 */         checkExpunged();
/*      */         
/* 1329 */         if (p.isREV1()) {
/* 1330 */           BODY b = p.peekBody(getSequenceNumber(), toSection("HEADER"));
/*      */           
/* 1332 */           if (b != null)
/* 1333 */             is = b.getByteArrayInputStream(); 
/*      */         } else {
/* 1335 */           RFC822DATA rd = p.fetchRFC822(getSequenceNumber(), "HEADER");
/*      */           
/* 1337 */           if (rd != null)
/* 1338 */             is = rd.getByteArrayInputStream(); 
/*      */         } 
/* 1340 */       } catch (ConnectionException cex) {
/* 1341 */         throw new FolderClosedException(this.folder, cex.getMessage());
/* 1342 */       } catch (ProtocolException pex) {
/* 1343 */         forceCheckExpunged();
/* 1344 */         throw new MessagingException(pex.getMessage(), pex);
/*      */       } 
/*      */     } 
/*      */     
/* 1348 */     if (is == null)
/* 1349 */       throw new MessagingException("Cannot load header"); 
/* 1350 */     this.headers = new InternetHeaders(is);
/* 1351 */     this.headersLoaded = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private synchronized void loadFlags() throws MessagingException {
/* 1358 */     if (this.flags != null) {
/*      */       return;
/*      */     }
/*      */     
/* 1362 */     synchronized (getMessageCacheLock()) {
/*      */       try {
/* 1364 */         IMAPProtocol p = getProtocol();
/*      */ 
/*      */ 
/*      */         
/* 1368 */         checkExpunged();
/*      */         
/* 1370 */         this.flags = p.fetchFlags(getSequenceNumber());
/*      */         
/* 1372 */         if (this.flags == null)
/* 1373 */           this.flags = new Flags(); 
/* 1374 */       } catch (ConnectionException cex) {
/* 1375 */         throw new FolderClosedException(this.folder, cex.getMessage());
/* 1376 */       } catch (ProtocolException pex) {
/* 1377 */         forceCheckExpunged();
/* 1378 */         throw new MessagingException(pex.getMessage(), pex);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean areHeadersLoaded() {
/* 1387 */     return this.headersLoaded;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setHeadersLoaded(boolean loaded) {
/* 1394 */     this.headersLoaded = loaded;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isHeaderLoaded(String name) {
/* 1401 */     if (this.headersLoaded) {
/* 1402 */       return true;
/*      */     }
/* 1404 */     return this.loadedHeaders.containsKey(name.toUpperCase(Locale.ENGLISH));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setHeaderLoaded(String name) {
/* 1411 */     this.loadedHeaders.put(name.toUpperCase(Locale.ENGLISH), name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String toSection(String what) {
/* 1419 */     if (this.sectionId == null) {
/* 1420 */       return what;
/*      */     }
/* 1422 */     return this.sectionId + "." + what;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private InternetAddress[] aaclone(InternetAddress[] aa) {
/* 1429 */     if (aa == null) {
/* 1430 */       return null;
/*      */     }
/* 1432 */     return (InternetAddress[])aa.clone();
/*      */   }
/*      */   
/*      */   private Flags _getFlags() {
/* 1436 */     return this.flags;
/*      */   }
/*      */   
/*      */   private ENVELOPE _getEnvelope() {
/* 1440 */     return this.envelope;
/*      */   }
/*      */   
/*      */   private BODYSTRUCTURE _getBodyStructure() {
/* 1444 */     return this.bs;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void _setFlags(Flags flags) {
/* 1457 */     this.flags = flags;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Session _getSession() {
/* 1464 */     return this.session;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\IMAPMessage.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */