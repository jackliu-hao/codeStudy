/*      */ package javax.mail.internet;
/*      */ 
/*      */ import com.sun.mail.util.ASCIIUtility;
/*      */ import com.sun.mail.util.FolderClosedIOException;
/*      */ import com.sun.mail.util.LineOutputStream;
/*      */ import com.sun.mail.util.MessageRemovedIOException;
/*      */ import com.sun.mail.util.MimeUtil;
/*      */ import com.sun.mail.util.PropUtil;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.ObjectStreamException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.text.ParseException;
/*      */ import java.util.Date;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Vector;
/*      */ import javax.activation.DataHandler;
/*      */ import javax.mail.Address;
/*      */ import javax.mail.Flags;
/*      */ import javax.mail.Folder;
/*      */ import javax.mail.FolderClosedException;
/*      */ import javax.mail.Message;
/*      */ import javax.mail.MessageRemovedException;
/*      */ import javax.mail.MessagingException;
/*      */ import javax.mail.Multipart;
/*      */ import javax.mail.Session;
/*      */ import javax.mail.util.SharedByteArrayInputStream;
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
/*      */ public class MimeMessage
/*      */   extends Message
/*      */   implements MimePart
/*      */ {
/*      */   protected DataHandler dh;
/*      */   protected byte[] content;
/*      */   protected InputStream contentStream;
/*      */   protected InternetHeaders headers;
/*      */   protected Flags flags;
/*      */   protected boolean modified;
/*      */   protected boolean saved;
/*      */   Object cachedContent;
/*  172 */   private static final MailDateFormat mailDateFormat = new MailDateFormat();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean strict;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MimeMessage(Session session) {
/*  184 */     super(session); this.modified = false; this.saved = false; this.strict = true;
/*  185 */     this.modified = true;
/*  186 */     this.headers = new InternetHeaders();
/*  187 */     this.flags = new Flags();
/*  188 */     initStrict();
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
/*      */ 
/*      */ 
/*      */   
/*      */   public MimeMessage(Session session, InputStream is) throws MessagingException {
/*  206 */     super(session); this.modified = false; this.saved = false; this.strict = true;
/*  207 */     this.flags = new Flags();
/*  208 */     initStrict();
/*  209 */     parse(is);
/*  210 */     this.saved = true;
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
/*      */   
/*      */   public MimeMessage(MimeMessage source) throws MessagingException {
/*  226 */     super(source.session); ByteArrayOutputStream bos; this.modified = false; this.saved = false; this.strict = true;
/*  227 */     this.flags = source.getFlags();
/*  228 */     if (this.flags == null) {
/*  229 */       this.flags = new Flags();
/*      */     }
/*  231 */     int size = source.getSize();
/*  232 */     if (size > 0) {
/*  233 */       bos = new ByteArrayOutputStream(size);
/*      */     } else {
/*  235 */       bos = new ByteArrayOutputStream();
/*      */     }  try {
/*  237 */       this.strict = source.strict;
/*  238 */       source.writeTo(bos);
/*  239 */       bos.close();
/*  240 */       SharedByteArrayInputStream bis = new SharedByteArrayInputStream(bos.toByteArray());
/*      */       
/*  242 */       parse((InputStream)bis);
/*  243 */       bis.close();
/*  244 */       this.saved = true;
/*  245 */     } catch (IOException ex) {
/*      */       
/*  247 */       throw new MessagingException("IOException while copying message", ex);
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
/*      */   protected MimeMessage(Folder folder, int msgnum) {
/*  259 */     super(folder, msgnum); this.modified = false; this.saved = false; this.strict = true;
/*  260 */     this.flags = new Flags();
/*  261 */     this.saved = true;
/*  262 */     initStrict();
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
/*      */ 
/*      */ 
/*      */   
/*      */   protected MimeMessage(Folder folder, InputStream is, int msgnum) throws MessagingException {
/*  280 */     this(folder, msgnum);
/*  281 */     initStrict();
/*  282 */     parse(is);
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
/*      */ 
/*      */   
/*      */   protected MimeMessage(Folder folder, InternetHeaders headers, byte[] content, int msgnum) throws MessagingException {
/*  299 */     this(folder, msgnum);
/*  300 */     this.headers = headers;
/*  301 */     this.content = content;
/*  302 */     initStrict();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void initStrict() {
/*  309 */     if (this.session != null) {
/*  310 */       this.strict = PropUtil.getBooleanSessionProperty(this.session, "mail.mime.address.strict", true);
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
/*      */ 
/*      */   
/*      */   protected void parse(InputStream is) throws MessagingException {
/*  327 */     if (!(is instanceof java.io.ByteArrayInputStream) && !(is instanceof BufferedInputStream) && !(is instanceof SharedInputStream))
/*      */     {
/*      */       
/*  330 */       is = new BufferedInputStream(is);
/*      */     }
/*  332 */     this.headers = createInternetHeaders(is);
/*      */     
/*  334 */     if (is instanceof SharedInputStream) {
/*  335 */       SharedInputStream sis = (SharedInputStream)is;
/*  336 */       this.contentStream = sis.newStream(sis.getPosition(), -1L);
/*      */     } else {
/*      */       try {
/*  339 */         this.content = ASCIIUtility.getBytes(is);
/*  340 */       } catch (IOException ioex) {
/*  341 */         throw new MessagingException("IOException", ioex);
/*      */       } 
/*      */     } 
/*      */     
/*  345 */     this.modified = false;
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
/*      */ 
/*      */   
/*      */   public Address[] getFrom() throws MessagingException {
/*  362 */     Address[] a = getAddressHeader("From");
/*  363 */     if (a == null) {
/*  364 */       a = getAddressHeader("Sender");
/*      */     }
/*  366 */     return a;
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
/*      */ 
/*      */   
/*      */   public void setFrom(Address address) throws MessagingException {
/*  383 */     if (address == null) {
/*  384 */       removeHeader("From");
/*      */     } else {
/*  386 */       setHeader("From", address.toString());
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
/*      */   public void setFrom() throws MessagingException {
/*  401 */     InternetAddress me = null;
/*      */     try {
/*  403 */       me = InternetAddress._getLocalAddress(this.session);
/*  404 */     } catch (Exception ex) {
/*      */ 
/*      */       
/*  407 */       throw new MessagingException("No From address", ex);
/*      */     } 
/*  409 */     if (me != null) {
/*  410 */       setFrom(me);
/*      */     } else {
/*  412 */       throw new MessagingException("No From address");
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
/*      */   
/*      */   public void addFrom(Address[] addresses) throws MessagingException {
/*  428 */     addAddressHeader("From", addresses);
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
/*      */ 
/*      */   
/*      */   public Address getSender() throws MessagingException {
/*  445 */     Address[] a = getAddressHeader("Sender");
/*  446 */     if (a == null || a.length == 0)
/*  447 */       return null; 
/*  448 */     return a[0];
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
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSender(Address address) throws MessagingException {
/*  466 */     if (address == null) {
/*  467 */       removeHeader("Sender");
/*      */     } else {
/*  469 */       setHeader("Sender", address.toString());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class RecipientType
/*      */     extends Message.RecipientType
/*      */   {
/*      */     private static final long serialVersionUID = -5468290701714395543L;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  486 */     public static final RecipientType NEWSGROUPS = new RecipientType("Newsgroups");
/*      */     
/*      */     protected RecipientType(String type) {
/*  489 */       super(type);
/*      */     }
/*      */     
/*      */     protected Object readResolve() throws ObjectStreamException {
/*  493 */       if (this.type.equals("Newsgroups")) {
/*  494 */         return NEWSGROUPS;
/*      */       }
/*  496 */       return super.readResolve();
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
/*      */   public Address[] getRecipients(Message.RecipientType type) throws MessagingException {
/*  530 */     if (type == RecipientType.NEWSGROUPS) {
/*  531 */       String s = getHeader("Newsgroups", ",");
/*  532 */       return (s == null) ? null : (Address[])NewsAddress.parse(s);
/*      */     } 
/*  534 */     return getAddressHeader(getHeaderName(type));
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
/*      */   public Address[] getAllRecipients() throws MessagingException {
/*  549 */     Address[] all = super.getAllRecipients();
/*  550 */     Address[] ng = getRecipients(RecipientType.NEWSGROUPS);
/*      */     
/*  552 */     if (ng == null)
/*  553 */       return all; 
/*  554 */     if (all == null) {
/*  555 */       return ng;
/*      */     }
/*  557 */     Address[] addresses = new Address[all.length + ng.length];
/*  558 */     System.arraycopy(all, 0, addresses, 0, all.length);
/*  559 */     System.arraycopy(ng, 0, addresses, all.length, ng.length);
/*  560 */     return addresses;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRecipients(Message.RecipientType type, Address[] addresses) throws MessagingException {
/*  580 */     if (type == RecipientType.NEWSGROUPS)
/*  581 */     { if (addresses == null || addresses.length == 0) {
/*  582 */         removeHeader("Newsgroups");
/*      */       } else {
/*  584 */         setHeader("Newsgroups", NewsAddress.toString(addresses));
/*      */       }  }
/*  586 */     else { setAddressHeader(getHeaderName(type), addresses); }
/*      */   
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRecipients(Message.RecipientType type, String addresses) throws MessagingException {
/*  609 */     if (type == RecipientType.NEWSGROUPS)
/*  610 */     { if (addresses == null || addresses.length() == 0) {
/*  611 */         removeHeader("Newsgroups");
/*      */       } else {
/*  613 */         setHeader("Newsgroups", addresses);
/*      */       }  }
/*  615 */     else { setAddressHeader(getHeaderName(type), (addresses == null) ? null : (Address[])InternetAddress.parse(addresses)); }
/*      */   
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
/*      */ 
/*      */   
/*      */   public void addRecipients(Message.RecipientType type, Address[] addresses) throws MessagingException {
/*  633 */     if (type == RecipientType.NEWSGROUPS) {
/*  634 */       String s = NewsAddress.toString(addresses);
/*  635 */       if (s != null)
/*  636 */         addHeader("Newsgroups", s); 
/*      */     } else {
/*  638 */       addAddressHeader(getHeaderName(type), addresses);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addRecipients(Message.RecipientType type, String addresses) throws MessagingException {
/*  658 */     if (type == RecipientType.NEWSGROUPS) {
/*  659 */       if (addresses != null && addresses.length() != 0)
/*  660 */         addHeader("Newsgroups", addresses); 
/*      */     } else {
/*  662 */       addAddressHeader(getHeaderName(type), (Address[])InternetAddress.parse(addresses));
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
/*      */   public Address[] getReplyTo() throws MessagingException {
/*  677 */     Address[] a = getAddressHeader("Reply-To");
/*  678 */     if (a == null || a.length == 0)
/*  679 */       a = getFrom(); 
/*  680 */     return a;
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
/*      */   public void setReplyTo(Address[] addresses) throws MessagingException {
/*  695 */     setAddressHeader("Reply-To", addresses);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private Address[] getAddressHeader(String name) throws MessagingException {
/*  701 */     String s = getHeader(name, ",");
/*  702 */     return (s == null) ? null : (Address[])InternetAddress.parseHeader(s, this.strict);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void setAddressHeader(String name, Address[] addresses) throws MessagingException {
/*  708 */     String s = InternetAddress.toString(addresses);
/*  709 */     if (s == null) {
/*  710 */       removeHeader(name);
/*      */     } else {
/*  712 */       setHeader(name, s);
/*      */     } 
/*      */   }
/*      */   private void addAddressHeader(String name, Address[] addresses) throws MessagingException {
/*      */     Address[] anew;
/*  717 */     if (addresses == null || addresses.length == 0)
/*      */       return; 
/*  719 */     Address[] a = getAddressHeader(name);
/*      */     
/*  721 */     if (a == null || a.length == 0) {
/*  722 */       anew = addresses;
/*      */     } else {
/*  724 */       anew = new Address[a.length + addresses.length];
/*  725 */       System.arraycopy(a, 0, anew, 0, a.length);
/*  726 */       System.arraycopy(addresses, 0, anew, a.length, addresses.length);
/*      */     } 
/*  728 */     String s = InternetAddress.toString(anew);
/*  729 */     if (s == null)
/*      */       return; 
/*  731 */     setHeader(name, s);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getSubject() throws MessagingException {
/*  750 */     String rawvalue = getHeader("Subject", null);
/*      */     
/*  752 */     if (rawvalue == null) {
/*  753 */       return null;
/*      */     }
/*      */     try {
/*  756 */       return MimeUtility.decodeText(MimeUtility.unfold(rawvalue));
/*  757 */     } catch (UnsupportedEncodingException ex) {
/*  758 */       return rawvalue;
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
/*      */   public void setSubject(String subject) throws MessagingException {
/*  790 */     setSubject(subject, null);
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
/*      */   public void setSubject(String subject, String charset) throws MessagingException {
/*  822 */     if (subject == null) {
/*  823 */       removeHeader("Subject");
/*      */     } else {
/*      */       try {
/*  826 */         setHeader("Subject", MimeUtility.fold(9, MimeUtility.encodeText(subject, charset, null)));
/*      */       }
/*  828 */       catch (UnsupportedEncodingException uex) {
/*  829 */         throw new MessagingException("Encoding error", uex);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getSentDate() throws MessagingException {
/*  846 */     String s = getHeader("Date", null);
/*  847 */     if (s != null) {
/*      */       try {
/*  849 */         synchronized (mailDateFormat) {
/*  850 */           return mailDateFormat.parse(s);
/*      */         } 
/*  852 */       } catch (ParseException pex) {
/*  853 */         return null;
/*      */       } 
/*      */     }
/*      */     
/*  857 */     return null;
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
/*      */   
/*      */   public void setSentDate(Date d) throws MessagingException {
/*  873 */     if (d == null) {
/*  874 */       removeHeader("Date");
/*      */     } else {
/*  876 */       synchronized (mailDateFormat) {
/*  877 */         setHeader("Date", mailDateFormat.format(d));
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getReceivedDate() throws MessagingException {
/*  896 */     return null;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getSize() throws MessagingException {
/*  917 */     if (this.content != null)
/*  918 */       return this.content.length; 
/*  919 */     if (this.contentStream != null) {
/*      */       try {
/*  921 */         int size = this.contentStream.available();
/*      */ 
/*      */         
/*  924 */         if (size > 0)
/*  925 */           return size; 
/*  926 */       } catch (IOException ex) {}
/*      */     }
/*      */ 
/*      */     
/*  930 */     return -1;
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
/*      */ 
/*      */   
/*      */   public int getLineCount() throws MessagingException {
/*  947 */     return -1;
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
/*      */ 
/*      */   
/*      */   public String getContentType() throws MessagingException {
/*  964 */     String s = getHeader("Content-Type", null);
/*  965 */     s = MimeUtil.cleanContentType(this, s);
/*  966 */     if (s == null)
/*  967 */       return "text/plain"; 
/*  968 */     return s;
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
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isMimeType(String mimeType) throws MessagingException {
/*  986 */     return MimeBodyPart.isMimeType(this, mimeType);
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
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDisposition() throws MessagingException {
/* 1004 */     return MimeBodyPart.getDisposition(this);
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
/*      */   public void setDisposition(String disposition) throws MessagingException {
/* 1019 */     MimeBodyPart.setDisposition(this, disposition);
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
/*      */   
/*      */   public String getEncoding() throws MessagingException {
/* 1035 */     return MimeBodyPart.getEncoding(this);
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
/*      */   public String getContentID() throws MessagingException {
/* 1050 */     return getHeader("Content-Id", null);
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
/*      */   public void setContentID(String cid) throws MessagingException {
/* 1065 */     if (cid == null) {
/* 1066 */       removeHeader("Content-ID");
/*      */     } else {
/* 1068 */       setHeader("Content-ID", cid);
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
/*      */   public String getContentMD5() throws MessagingException {
/* 1083 */     return getHeader("Content-MD5", null);
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
/*      */   public void setContentMD5(String md5) throws MessagingException {
/* 1096 */     setHeader("Content-MD5", md5);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDescription() throws MessagingException {
/* 1116 */     return MimeBodyPart.getDescription(this);
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
/*      */   public void setDescription(String description) throws MessagingException {
/* 1145 */     setDescription(description, null);
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
/*      */   public void setDescription(String description, String charset) throws MessagingException {
/* 1176 */     MimeBodyPart.setDescription(this, description, charset);
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
/*      */   
/*      */   public String[] getContentLanguage() throws MessagingException {
/* 1192 */     return MimeBodyPart.getContentLanguage(this);
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
/*      */   
/*      */   public void setContentLanguage(String[] languages) throws MessagingException {
/* 1208 */     MimeBodyPart.setContentLanguage(this, languages);
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
/*      */ 
/*      */ 
/*      */   
/*      */   public String getMessageID() throws MessagingException {
/* 1226 */     return getHeader("Message-ID", null);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getFileName() throws MessagingException {
/* 1250 */     return MimeBodyPart.getFileName(this);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setFileName(String filename) throws MessagingException {
/* 1274 */     MimeBodyPart.setFileName(this, filename);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private String getHeaderName(Message.RecipientType type) throws MessagingException {
/*      */     String headerName;
/* 1281 */     if (type == Message.RecipientType.TO) {
/* 1282 */       headerName = "To";
/* 1283 */     } else if (type == Message.RecipientType.CC) {
/* 1284 */       headerName = "Cc";
/* 1285 */     } else if (type == Message.RecipientType.BCC) {
/* 1286 */       headerName = "Bcc";
/* 1287 */     } else if (type == RecipientType.NEWSGROUPS) {
/* 1288 */       headerName = "Newsgroups";
/*      */     } else {
/* 1290 */       throw new MessagingException("Invalid Recipient Type");
/* 1291 */     }  return headerName;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InputStream getInputStream() throws IOException, MessagingException {
/* 1312 */     return getDataHandler().getInputStream();
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
/*      */ 
/*      */   
/*      */   protected InputStream getContentStream() throws MessagingException {
/* 1329 */     if (this.contentStream != null)
/* 1330 */       return ((SharedInputStream)this.contentStream).newStream(0L, -1L); 
/* 1331 */     if (this.content != null) {
/* 1332 */       return (InputStream)new SharedByteArrayInputStream(this.content);
/*      */     }
/* 1334 */     throw new MessagingException("No MimeMessage content");
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InputStream getRawInputStream() throws MessagingException {
/* 1353 */     return getContentStream();
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
/*      */   public synchronized DataHandler getDataHandler() throws MessagingException {
/* 1386 */     if (this.dh == null) {
/* 1387 */       this.dh = new MimeBodyPart.MimePartDataHandler(new MimePartDataSource(this));
/*      */     }
/* 1389 */     return this.dh;
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
/*      */   public Object getContent() throws IOException, MessagingException {
/*      */     Object c;
/* 1416 */     if (this.cachedContent != null) {
/* 1417 */       return this.cachedContent;
/*      */     }
/*      */     try {
/* 1420 */       c = getDataHandler().getContent();
/* 1421 */     } catch (FolderClosedIOException fex) {
/* 1422 */       throw new FolderClosedException(fex.getFolder(), fex.getMessage());
/* 1423 */     } catch (MessageRemovedIOException mex) {
/* 1424 */       throw new MessageRemovedException(mex.getMessage());
/*      */     } 
/* 1426 */     if (MimeBodyPart.cacheMultipart && (c instanceof Multipart || c instanceof Message) && (this.content != null || this.contentStream != null)) {
/*      */ 
/*      */       
/* 1429 */       this.cachedContent = c;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1434 */       if (c instanceof MimeMultipart)
/* 1435 */         ((MimeMultipart)c).parse(); 
/*      */     } 
/* 1437 */     return c;
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
/*      */   
/*      */   public synchronized void setDataHandler(DataHandler dh) throws MessagingException {
/* 1453 */     this.dh = dh;
/* 1454 */     this.cachedContent = null;
/* 1455 */     MimeBodyPart.invalidateContentHeaders(this);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setContent(Object o, String type) throws MessagingException {
/* 1479 */     if (o instanceof Multipart) {
/* 1480 */       setContent((Multipart)o);
/*      */     } else {
/* 1482 */       setDataHandler(new DataHandler(o, type));
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setText(String text) throws MessagingException {
/* 1505 */     setText(text, null);
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
/*      */   
/*      */   public void setText(String text, String charset) throws MessagingException {
/* 1521 */     MimeBodyPart.setText(this, text, charset, "plain");
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
/*      */ 
/*      */ 
/*      */   
/*      */   public void setText(String text, String charset, String subtype) throws MessagingException {
/* 1539 */     MimeBodyPart.setText(this, text, charset, subtype);
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
/*      */   public void setContent(Multipart mp) throws MessagingException {
/* 1554 */     setDataHandler(new DataHandler(mp, mp.getContentType()));
/* 1555 */     mp.setParent(this);
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
/*      */   public Message reply(boolean replyToAll) throws MessagingException {
/* 1588 */     MimeMessage reply = createMimeMessage(this.session);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1596 */     String subject = getHeader("Subject", null);
/* 1597 */     if (subject != null) {
/* 1598 */       if (!subject.regionMatches(true, 0, "Re: ", 0, 4))
/* 1599 */         subject = "Re: " + subject; 
/* 1600 */       reply.setHeader("Subject", subject);
/*      */     } 
/* 1602 */     Address[] a = getReplyTo();
/* 1603 */     reply.setRecipients(Message.RecipientType.TO, a);
/* 1604 */     if (replyToAll) {
/* 1605 */       Vector v = new Vector();
/*      */       
/* 1607 */       InternetAddress me = InternetAddress.getLocalAddress(this.session);
/* 1608 */       if (me != null) {
/* 1609 */         v.addElement(me);
/*      */       }
/* 1611 */       String alternates = null;
/* 1612 */       if (this.session != null)
/* 1613 */         alternates = this.session.getProperty("mail.alternates"); 
/* 1614 */       if (alternates != null) {
/* 1615 */         eliminateDuplicates(v, (Address[])InternetAddress.parse(alternates, false));
/*      */       }
/*      */       
/* 1618 */       String replyallccStr = null;
/* 1619 */       boolean replyallcc = false;
/* 1620 */       if (this.session != null) {
/* 1621 */         replyallcc = PropUtil.getBooleanSessionProperty(this.session, "mail.replyallcc", false);
/*      */       }
/*      */       
/* 1624 */       eliminateDuplicates(v, a);
/* 1625 */       a = getRecipients(Message.RecipientType.TO);
/* 1626 */       a = eliminateDuplicates(v, a);
/* 1627 */       if (a != null && a.length > 0)
/* 1628 */         if (replyallcc) {
/* 1629 */           reply.addRecipients(Message.RecipientType.CC, a);
/*      */         } else {
/* 1631 */           reply.addRecipients(Message.RecipientType.TO, a);
/*      */         }  
/* 1633 */       a = getRecipients(Message.RecipientType.CC);
/* 1634 */       a = eliminateDuplicates(v, a);
/* 1635 */       if (a != null && a.length > 0) {
/* 1636 */         reply.addRecipients(Message.RecipientType.CC, a);
/*      */       }
/* 1638 */       a = getRecipients(RecipientType.NEWSGROUPS);
/* 1639 */       if (a != null && a.length > 0) {
/* 1640 */         reply.setRecipients(RecipientType.NEWSGROUPS, a);
/*      */       }
/*      */     } 
/* 1643 */     String msgId = getHeader("Message-Id", null);
/* 1644 */     if (msgId != null) {
/* 1645 */       reply.setHeader("In-Reply-To", msgId);
/*      */     }
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
/* 1661 */     String refs = getHeader("References", " ");
/* 1662 */     if (refs == null)
/*      */     {
/* 1664 */       refs = getHeader("In-Reply-To", " ");
/*      */     }
/* 1666 */     if (msgId != null)
/* 1667 */       if (refs != null) {
/* 1668 */         refs = MimeUtility.unfold(refs) + " " + msgId;
/*      */       } else {
/* 1670 */         refs = msgId;
/*      */       }  
/* 1672 */     if (refs != null) {
/* 1673 */       reply.setHeader("References", MimeUtility.fold(12, refs));
/*      */     }
/*      */     try {
/* 1676 */       setFlags(answeredFlag, true);
/* 1677 */     } catch (MessagingException mex) {}
/*      */ 
/*      */     
/* 1680 */     return reply;
/*      */   }
/*      */ 
/*      */   
/* 1684 */   private static final Flags answeredFlag = new Flags(Flags.Flag.ANSWERED);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Address[] eliminateDuplicates(Vector v, Address[] addrs) {
/* 1692 */     if (addrs == null)
/* 1693 */       return null; 
/* 1694 */     int gone = 0;
/* 1695 */     for (int i = 0; i < addrs.length; i++) {
/* 1696 */       boolean found = false;
/*      */       
/* 1698 */       for (int j = 0; j < v.size(); j++) {
/* 1699 */         if (((InternetAddress)v.elementAt(j)).equals(addrs[i])) {
/*      */           
/* 1701 */           found = true;
/* 1702 */           gone++;
/* 1703 */           addrs[i] = null;
/*      */           break;
/*      */         } 
/*      */       } 
/* 1707 */       if (!found) {
/* 1708 */         v.addElement(addrs[i]);
/*      */       }
/*      */     } 
/* 1711 */     if (gone != 0) {
/*      */       Address[] a;
/*      */ 
/*      */       
/* 1715 */       if (addrs instanceof InternetAddress[]) {
/* 1716 */         InternetAddress[] arrayOfInternetAddress = new InternetAddress[addrs.length - gone];
/*      */       } else {
/* 1718 */         a = new Address[addrs.length - gone];
/* 1719 */       }  for (int k = 0, j = 0; k < addrs.length; k++) {
/* 1720 */         if (addrs[k] != null)
/* 1721 */           a[j++] = addrs[k]; 
/* 1722 */       }  addrs = a;
/*      */     } 
/* 1724 */     return addrs;
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
/*      */   public void writeTo(OutputStream os) throws IOException, MessagingException {
/* 1749 */     writeTo(os, null);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeTo(OutputStream os, String[] ignoreList) throws IOException, MessagingException {
/* 1769 */     if (!this.saved) {
/* 1770 */       saveChanges();
/*      */     }
/* 1772 */     if (this.modified) {
/* 1773 */       MimeBodyPart.writeTo(this, os, ignoreList);
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/* 1779 */     Enumeration hdrLines = getNonMatchingHeaderLines(ignoreList);
/* 1780 */     LineOutputStream los = new LineOutputStream(os);
/* 1781 */     while (hdrLines.hasMoreElements()) {
/* 1782 */       los.writeln(hdrLines.nextElement());
/*      */     }
/*      */     
/* 1785 */     los.writeln();
/*      */ 
/*      */     
/* 1788 */     if (this.content == null) {
/*      */ 
/*      */       
/* 1791 */       InputStream is = null;
/* 1792 */       byte[] buf = new byte[8192];
/*      */       try {
/* 1794 */         is = getContentStream();
/*      */         
/*      */         int len;
/* 1797 */         while ((len = is.read(buf)) > 0)
/* 1798 */           os.write(buf, 0, len); 
/*      */       } finally {
/* 1800 */         if (is != null)
/* 1801 */           is.close(); 
/* 1802 */         buf = null;
/*      */       } 
/*      */     } else {
/* 1805 */       os.write(this.content);
/*      */     } 
/* 1807 */     os.flush();
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
/*      */ 
/*      */ 
/*      */   
/*      */   public String[] getHeader(String name) throws MessagingException {
/* 1825 */     return this.headers.getHeader(name);
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
/*      */ 
/*      */   
/*      */   public String getHeader(String name, String delimiter) throws MessagingException {
/* 1842 */     return this.headers.getHeader(name, delimiter);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setHeader(String name, String value) throws MessagingException {
/* 1863 */     this.headers.setHeader(name, value);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addHeader(String name, String value) throws MessagingException {
/* 1883 */     this.headers.addHeader(name, value);
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
/*      */   public void removeHeader(String name) throws MessagingException {
/* 1896 */     this.headers.removeHeader(name);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Enumeration getAllHeaders() throws MessagingException {
/* 1915 */     return this.headers.getAllHeaders();
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
/*      */   public Enumeration getMatchingHeaders(String[] names) throws MessagingException {
/* 1927 */     return this.headers.getMatchingHeaders(names);
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
/*      */   public Enumeration getNonMatchingHeaders(String[] names) throws MessagingException {
/* 1939 */     return this.headers.getNonMatchingHeaders(names);
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
/*      */   public void addHeaderLine(String line) throws MessagingException {
/* 1952 */     this.headers.addHeaderLine(line);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Enumeration getAllHeaderLines() throws MessagingException {
/* 1963 */     return this.headers.getAllHeaderLines();
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
/*      */   public Enumeration getMatchingHeaderLines(String[] names) throws MessagingException {
/* 1975 */     return this.headers.getMatchingHeaderLines(names);
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
/*      */   public Enumeration getNonMatchingHeaderLines(String[] names) throws MessagingException {
/* 1987 */     return this.headers.getNonMatchingHeaderLines(names);
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
/*      */   
/*      */   public synchronized Flags getFlags() throws MessagingException {
/* 2003 */     return (Flags)this.flags.clone();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean isSet(Flags.Flag flag) throws MessagingException {
/* 2026 */     return this.flags.contains(flag);
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
/*      */   
/*      */   public synchronized void setFlags(Flags flag, boolean set) throws MessagingException {
/* 2042 */     if (set) {
/* 2043 */       this.flags.add(flag);
/*      */     } else {
/* 2045 */       this.flags.remove(flag);
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
/*      */   public void saveChanges() throws MessagingException {
/* 2073 */     this.modified = true;
/* 2074 */     this.saved = true;
/* 2075 */     updateHeaders();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void updateMessageID() throws MessagingException {
/* 2086 */     setHeader("Message-ID", "<" + UniqueValue.getUniqueMessageIDValue(this.session) + ">");
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected synchronized void updateHeaders() throws MessagingException {
/* 2107 */     MimeBodyPart.updateHeaders(this);
/* 2108 */     setHeader("MIME-Version", "1.0");
/* 2109 */     updateMessageID();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2117 */     if (this.cachedContent != null) {
/* 2118 */       this.dh = new DataHandler(this.cachedContent, getContentType());
/* 2119 */       this.cachedContent = null;
/* 2120 */       this.content = null;
/* 2121 */       if (this.contentStream != null) {
/*      */         try {
/* 2123 */           this.contentStream.close();
/* 2124 */         } catch (IOException ioex) {}
/*      */       }
/* 2126 */       this.contentStream = null;
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
/*      */ 
/*      */   
/*      */   protected InternetHeaders createInternetHeaders(InputStream is) throws MessagingException {
/* 2143 */     return new InternetHeaders(is);
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
/*      */   
/*      */   protected MimeMessage createMimeMessage(Session session) throws MessagingException {
/* 2159 */     return new MimeMessage(session);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\internet\MimeMessage.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */