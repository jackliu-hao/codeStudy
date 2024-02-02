/*      */ package javax.mail.internet;
/*      */ 
/*      */ import com.sun.mail.util.ASCIIUtility;
/*      */ import com.sun.mail.util.FolderClosedIOException;
/*      */ import com.sun.mail.util.LineOutputStream;
/*      */ import com.sun.mail.util.MessageRemovedIOException;
/*      */ import com.sun.mail.util.MimeUtil;
/*      */ import com.sun.mail.util.PropUtil;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Vector;
/*      */ import javax.activation.DataHandler;
/*      */ import javax.activation.DataSource;
/*      */ import javax.activation.FileDataSource;
/*      */ import javax.mail.BodyPart;
/*      */ import javax.mail.FolderClosedException;
/*      */ import javax.mail.MessageRemovedException;
/*      */ import javax.mail.MessagingException;
/*      */ import javax.mail.Multipart;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class MimeBodyPart
/*      */   extends BodyPart
/*      */   implements MimePart
/*      */ {
/*   85 */   private static final boolean setDefaultTextCharset = PropUtil.getBooleanSystemProperty("mail.mime.setdefaulttextcharset", true);
/*      */ 
/*      */ 
/*      */   
/*   89 */   private static final boolean setContentTypeFileName = PropUtil.getBooleanSystemProperty("mail.mime.setcontenttypefilename", true);
/*      */ 
/*      */ 
/*      */   
/*   93 */   private static final boolean encodeFileName = PropUtil.getBooleanSystemProperty("mail.mime.encodefilename", false);
/*      */   
/*   95 */   private static final boolean decodeFileName = PropUtil.getBooleanSystemProperty("mail.mime.decodefilename", false);
/*      */   
/*   97 */   private static final boolean ignoreMultipartEncoding = PropUtil.getBooleanSystemProperty("mail.mime.ignoremultipartencoding", true);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  103 */   static final boolean cacheMultipart = PropUtil.getBooleanSystemProperty("mail.mime.cachemultipart", true);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected DataHandler dh;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected byte[] content;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected InputStream contentStream;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected InternetHeaders headers;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Object cachedContent;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MimeBodyPart() {
/*  148 */     this.headers = new InternetHeaders();
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
/*      */   public MimeBodyPart(InputStream is) throws MessagingException {
/*  167 */     if (!(is instanceof ByteArrayInputStream) && !(is instanceof BufferedInputStream) && !(is instanceof SharedInputStream))
/*      */     {
/*      */       
/*  170 */       is = new BufferedInputStream(is);
/*      */     }
/*  172 */     this.headers = new InternetHeaders(is);
/*      */     
/*  174 */     if (is instanceof SharedInputStream) {
/*  175 */       SharedInputStream sis = (SharedInputStream)is;
/*  176 */       this.contentStream = sis.newStream(sis.getPosition(), -1L);
/*      */     } else {
/*      */       try {
/*  179 */         this.content = ASCIIUtility.getBytes(is);
/*  180 */       } catch (IOException ioex) {
/*  181 */         throw new MessagingException("Error reading input stream", ioex);
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
/*      */   public MimeBodyPart(InternetHeaders headers, byte[] content) throws MessagingException {
/*  199 */     this.headers = headers;
/*  200 */     this.content = content;
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
/*      */   public int getSize() throws MessagingException {
/*  220 */     if (this.content != null)
/*  221 */       return this.content.length; 
/*  222 */     if (this.contentStream != null) {
/*      */       try {
/*  224 */         int size = this.contentStream.available();
/*      */ 
/*      */         
/*  227 */         if (size > 0)
/*  228 */           return size; 
/*  229 */       } catch (IOException ex) {}
/*      */     }
/*      */ 
/*      */     
/*  233 */     return -1;
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
/*      */   public int getLineCount() throws MessagingException {
/*  249 */     return -1;
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
/*      */   public String getContentType() throws MessagingException {
/*  264 */     String s = getHeader("Content-Type", null);
/*  265 */     s = MimeUtil.cleanContentType(this, s);
/*  266 */     if (s == null)
/*  267 */       s = "text/plain"; 
/*  268 */     return s;
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
/*  286 */     return isMimeType(this, mimeType);
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
/*      */   public String getDisposition() throws MessagingException {
/*  303 */     return getDisposition(this);
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
/*      */   public void setDisposition(String disposition) throws MessagingException {
/*  317 */     setDisposition(this, disposition);
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
/*      */   public String getEncoding() throws MessagingException {
/*  332 */     return getEncoding(this);
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
/*      */   public String getContentID() throws MessagingException {
/*  344 */     return getHeader("Content-Id", null);
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
/*      */   public void setContentID(String cid) throws MessagingException {
/*  360 */     if (cid == null) {
/*  361 */       removeHeader("Content-ID");
/*      */     } else {
/*  363 */       setHeader("Content-ID", cid);
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
/*      */   public String getContentMD5() throws MessagingException {
/*  375 */     return getHeader("Content-MD5", null);
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
/*      */   public void setContentMD5(String md5) throws MessagingException {
/*  387 */     setHeader("Content-MD5", md5);
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
/*      */   public String[] getContentLanguage() throws MessagingException {
/*  400 */     return getContentLanguage(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setContentLanguage(String[] languages) throws MessagingException {
/*  411 */     setContentLanguage(this, languages);
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
/*      */   public String getDescription() throws MessagingException {
/*  430 */     return getDescription(this);
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
/*  459 */     setDescription(description, null);
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
/*  490 */     setDescription(this, description, charset);
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
/*      */   public String getFileName() throws MessagingException {
/*  513 */     return getFileName(this);
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
/*      */   public void setFileName(String filename) throws MessagingException {
/*  538 */     setFileName(this, filename);
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
/*      */   public InputStream getInputStream() throws IOException, MessagingException {
/*  558 */     return getDataHandler().getInputStream();
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
/*      */   protected InputStream getContentStream() throws MessagingException {
/*  571 */     if (this.contentStream != null)
/*  572 */       return ((SharedInputStream)this.contentStream).newStream(0L, -1L); 
/*  573 */     if (this.content != null) {
/*  574 */       return new ByteArrayInputStream(this.content);
/*      */     }
/*  576 */     throw new MessagingException("No MimeBodyPart content");
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
/*  595 */     return getContentStream();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DataHandler getDataHandler() throws MessagingException {
/*  606 */     if (this.dh == null)
/*  607 */       this.dh = new MimePartDataHandler(new MimePartDataSource(this)); 
/*  608 */     return this.dh;
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
/*      */   public Object getContent() throws IOException, MessagingException {
/*      */     Object c;
/*  633 */     if (this.cachedContent != null) {
/*  634 */       return this.cachedContent;
/*      */     }
/*      */     try {
/*  637 */       c = getDataHandler().getContent();
/*  638 */     } catch (FolderClosedIOException fex) {
/*  639 */       throw new FolderClosedException(fex.getFolder(), fex.getMessage());
/*  640 */     } catch (MessageRemovedIOException mex) {
/*  641 */       throw new MessageRemovedException(mex.getMessage());
/*      */     } 
/*  643 */     if (cacheMultipart && (c instanceof Multipart || c instanceof javax.mail.Message) && (this.content != null || this.contentStream != null)) {
/*      */ 
/*      */       
/*  646 */       this.cachedContent = c;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  651 */       if (c instanceof MimeMultipart)
/*  652 */         ((MimeMultipart)c).parse(); 
/*      */     } 
/*  654 */     return c;
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
/*      */   public void setDataHandler(DataHandler dh) throws MessagingException {
/*  669 */     this.dh = dh;
/*  670 */     this.cachedContent = null;
/*  671 */     invalidateContentHeaders(this);
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
/*      */   public void setContent(Object o, String type) throws MessagingException {
/*  694 */     if (o instanceof Multipart) {
/*  695 */       setContent((Multipart)o);
/*      */     } else {
/*  697 */       setDataHandler(new DataHandler(o, type));
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
/*      */   public void setText(String text) throws MessagingException {
/*  721 */     setText(text, null);
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
/*  737 */     setText(this, text, charset, "plain");
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
/*  755 */     setText(this, text, charset, subtype);
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
/*      */   public void setContent(Multipart mp) throws MessagingException {
/*  769 */     setDataHandler(new DataHandler(mp, mp.getContentType()));
/*  770 */     mp.setParent(this);
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
/*      */   public void attachFile(File file) throws IOException, MessagingException {
/*  786 */     FileDataSource fds = new FileDataSource(file);
/*  787 */     setDataHandler(new DataHandler((DataSource)fds));
/*  788 */     setFileName(fds.getName());
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
/*      */   public void attachFile(String file) throws IOException, MessagingException {
/*  804 */     File f = new File(file);
/*  805 */     attachFile(f);
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
/*      */   public void saveFile(File file) throws IOException, MessagingException {
/*  818 */     OutputStream out = null;
/*  819 */     InputStream in = null;
/*      */     try {
/*  821 */       out = new BufferedOutputStream(new FileOutputStream(file));
/*  822 */       in = getInputStream();
/*  823 */       byte[] buf = new byte[8192];
/*      */       int len;
/*  825 */       while ((len = in.read(buf)) > 0) {
/*  826 */         out.write(buf, 0, len);
/*      */       }
/*      */     } finally {
/*      */       try {
/*  830 */         if (in != null)
/*  831 */           in.close(); 
/*  832 */       } catch (IOException ex) {}
/*      */       try {
/*  834 */         if (out != null)
/*  835 */           out.close(); 
/*  836 */       } catch (IOException ex) {}
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
/*      */   public void saveFile(String file) throws IOException, MessagingException {
/*  850 */     File f = new File(file);
/*  851 */     saveFile(f);
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
/*      */   public void writeTo(OutputStream os) throws IOException, MessagingException {
/*  865 */     writeTo(this, os, null);
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
/*      */   public String[] getHeader(String name) throws MessagingException {
/*  878 */     return this.headers.getHeader(name);
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
/*  895 */     return this.headers.getHeader(name, delimiter);
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
/*      */   public void setHeader(String name, String value) throws MessagingException {
/*  911 */     this.headers.setHeader(name, value);
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
/*      */   public void addHeader(String name, String value) throws MessagingException {
/*  926 */     this.headers.addHeader(name, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeHeader(String name) throws MessagingException {
/*  933 */     this.headers.removeHeader(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Enumeration getAllHeaders() throws MessagingException {
/*  941 */     return this.headers.getAllHeaders();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Enumeration getMatchingHeaders(String[] names) throws MessagingException {
/*  950 */     return this.headers.getMatchingHeaders(names);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Enumeration getNonMatchingHeaders(String[] names) throws MessagingException {
/*  959 */     return this.headers.getNonMatchingHeaders(names);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addHeaderLine(String line) throws MessagingException {
/*  966 */     this.headers.addHeaderLine(line);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Enumeration getAllHeaderLines() throws MessagingException {
/*  975 */     return this.headers.getAllHeaderLines();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Enumeration getMatchingHeaderLines(String[] names) throws MessagingException {
/*  985 */     return this.headers.getMatchingHeaderLines(names);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Enumeration getNonMatchingHeaderLines(String[] names) throws MessagingException {
/*  995 */     return this.headers.getNonMatchingHeaderLines(names);
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
/*      */   protected void updateHeaders() throws MessagingException {
/* 1021 */     updateHeaders(this);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1028 */     if (this.cachedContent != null) {
/* 1029 */       this.dh = new DataHandler(this.cachedContent, getContentType());
/* 1030 */       this.cachedContent = null;
/* 1031 */       this.content = null;
/* 1032 */       if (this.contentStream != null) {
/*      */         try {
/* 1034 */           this.contentStream.close();
/* 1035 */         } catch (IOException ioex) {}
/*      */       }
/* 1037 */       this.contentStream = null;
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
/*      */   static boolean isMimeType(MimePart part, String mimeType) throws MessagingException {
/*      */     try {
/* 1050 */       ContentType ct = new ContentType(part.getContentType());
/* 1051 */       return ct.match(mimeType);
/* 1052 */     } catch (ParseException ex) {
/* 1053 */       return part.getContentType().equalsIgnoreCase(mimeType);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   static void setText(MimePart part, String text, String charset, String subtype) throws MessagingException {
/* 1059 */     if (charset == null) {
/* 1060 */       if (MimeUtility.checkAscii(text) != 1) {
/* 1061 */         charset = MimeUtility.getDefaultMIMECharset();
/*      */       } else {
/* 1063 */         charset = "us-ascii";
/*      */       } 
/*      */     }
/* 1066 */     part.setContent(text, "text/" + subtype + "; charset=" + MimeUtility.quote(charset, "()<>@,;:\\\"\t []/?="));
/*      */   }
/*      */ 
/*      */   
/*      */   static String getDisposition(MimePart part) throws MessagingException {
/* 1071 */     String s = part.getHeader("Content-Disposition", (String)null);
/*      */     
/* 1073 */     if (s == null) {
/* 1074 */       return null;
/*      */     }
/* 1076 */     ContentDisposition cd = new ContentDisposition(s);
/* 1077 */     return cd.getDisposition();
/*      */   }
/*      */ 
/*      */   
/*      */   static void setDisposition(MimePart part, String disposition) throws MessagingException {
/* 1082 */     if (disposition == null) {
/* 1083 */       part.removeHeader("Content-Disposition");
/*      */     } else {
/* 1085 */       String s = part.getHeader("Content-Disposition", (String)null);
/* 1086 */       if (s != null) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1092 */         ContentDisposition cd = new ContentDisposition(s);
/* 1093 */         cd.setDisposition(disposition);
/* 1094 */         disposition = cd.toString();
/*      */       } 
/* 1096 */       part.setHeader("Content-Disposition", disposition);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   static String getDescription(MimePart part) throws MessagingException {
/* 1102 */     String rawvalue = part.getHeader("Content-Description", (String)null);
/*      */     
/* 1104 */     if (rawvalue == null) {
/* 1105 */       return null;
/*      */     }
/*      */     try {
/* 1108 */       return MimeUtility.decodeText(MimeUtility.unfold(rawvalue));
/* 1109 */     } catch (UnsupportedEncodingException ex) {
/* 1110 */       return rawvalue;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static void setDescription(MimePart part, String description, String charset) throws MessagingException {
/* 1117 */     if (description == null) {
/* 1118 */       part.removeHeader("Content-Description");
/*      */       
/*      */       return;
/*      */     } 
/*      */     try {
/* 1123 */       part.setHeader("Content-Description", MimeUtility.fold(21, MimeUtility.encodeText(description, charset, null)));
/*      */     }
/* 1125 */     catch (UnsupportedEncodingException uex) {
/* 1126 */       throw new MessagingException("Encoding error", uex);
/*      */     } 
/*      */   }
/*      */   
/*      */   static String getFileName(MimePart part) throws MessagingException {
/* 1131 */     String filename = null;
/* 1132 */     String s = part.getHeader("Content-Disposition", (String)null);
/*      */     
/* 1134 */     if (s != null) {
/*      */       
/* 1136 */       ContentDisposition cd = new ContentDisposition(s);
/* 1137 */       filename = cd.getParameter("filename");
/*      */     } 
/* 1139 */     if (filename == null) {
/*      */       
/* 1141 */       s = part.getHeader("Content-Type", (String)null);
/* 1142 */       s = MimeUtil.cleanContentType(part, s);
/* 1143 */       if (s != null) {
/*      */         try {
/* 1145 */           ContentType ct = new ContentType(s);
/* 1146 */           filename = ct.getParameter("name");
/* 1147 */         } catch (ParseException pex) {}
/*      */       }
/*      */     } 
/* 1150 */     if (decodeFileName && filename != null) {
/*      */       try {
/* 1152 */         filename = MimeUtility.decodeText(filename);
/* 1153 */       } catch (UnsupportedEncodingException ex) {
/* 1154 */         throw new MessagingException("Can't decode filename", ex);
/*      */       } 
/*      */     }
/* 1157 */     return filename;
/*      */   }
/*      */ 
/*      */   
/*      */   static void setFileName(MimePart part, String name) throws MessagingException {
/* 1162 */     if (encodeFileName && name != null) {
/*      */       try {
/* 1164 */         name = MimeUtility.encodeText(name);
/* 1165 */       } catch (UnsupportedEncodingException ex) {
/* 1166 */         throw new MessagingException("Can't encode filename", ex);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 1171 */     String s = part.getHeader("Content-Disposition", (String)null);
/* 1172 */     ContentDisposition cd = new ContentDisposition((s == null) ? "attachment" : s);
/*      */     
/* 1174 */     cd.setParameter("filename", name);
/* 1175 */     part.setHeader("Content-Disposition", cd.toString());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1181 */     if (setContentTypeFileName) {
/* 1182 */       s = part.getHeader("Content-Type", (String)null);
/* 1183 */       s = MimeUtil.cleanContentType(part, s);
/* 1184 */       if (s != null) {
/*      */         try {
/* 1186 */           ContentType cType = new ContentType(s);
/* 1187 */           cType.setParameter("name", name);
/* 1188 */           part.setHeader("Content-Type", cType.toString());
/* 1189 */         } catch (ParseException pex) {}
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   static String[] getContentLanguage(MimePart part) throws MessagingException {
/* 1196 */     String s = part.getHeader("Content-Language", (String)null);
/*      */     
/* 1198 */     if (s == null) {
/* 1199 */       return null;
/*      */     }
/*      */     
/* 1202 */     HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
/* 1203 */     Vector v = new Vector();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 1209 */       HeaderTokenizer.Token tk = h.next();
/* 1210 */       int tkType = tk.getType();
/* 1211 */       if (tkType == -4)
/*      */         break; 
/* 1213 */       if (tkType == -1) {
/* 1214 */         v.addElement(tk.getValue());
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1219 */     if (v.size() == 0) {
/* 1220 */       return null;
/*      */     }
/* 1222 */     String[] language = new String[v.size()];
/* 1223 */     v.copyInto((Object[])language);
/* 1224 */     return language;
/*      */   }
/*      */ 
/*      */   
/*      */   static void setContentLanguage(MimePart part, String[] languages) throws MessagingException {
/* 1229 */     StringBuffer sb = new StringBuffer(languages[0]);
/* 1230 */     int len = "Content-Language".length() + 2 + languages[0].length();
/* 1231 */     for (int i = 1; i < languages.length; i++) {
/* 1232 */       sb.append(',');
/* 1233 */       len++;
/* 1234 */       if (len > 76) {
/* 1235 */         sb.append("\r\n\t");
/* 1236 */         len = 8;
/*      */       } 
/* 1238 */       sb.append(languages[i]);
/* 1239 */       len += languages[i].length();
/*      */     } 
/* 1241 */     part.setHeader("Content-Language", sb.toString());
/*      */   }
/*      */   
/*      */   static String getEncoding(MimePart part) throws MessagingException {
/* 1245 */     String s = part.getHeader("Content-Transfer-Encoding", (String)null);
/*      */     
/* 1247 */     if (s == null) {
/* 1248 */       return null;
/*      */     }
/* 1250 */     s = s.trim();
/*      */ 
/*      */     
/* 1253 */     if (s.equalsIgnoreCase("7bit") || s.equalsIgnoreCase("8bit") || s.equalsIgnoreCase("quoted-printable") || s.equalsIgnoreCase("binary") || s.equalsIgnoreCase("base64"))
/*      */     {
/*      */ 
/*      */       
/* 1257 */       return s;
/*      */     }
/*      */     
/* 1260 */     HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 1266 */       HeaderTokenizer.Token tk = h.next();
/* 1267 */       int tkType = tk.getType();
/* 1268 */       if (tkType == -4)
/*      */         break; 
/* 1270 */       if (tkType == -1) {
/* 1271 */         return tk.getValue();
/*      */       }
/*      */     } 
/*      */     
/* 1275 */     return s;
/*      */   }
/*      */ 
/*      */   
/*      */   static void setEncoding(MimePart part, String encoding) throws MessagingException {
/* 1280 */     part.setHeader("Content-Transfer-Encoding", encoding);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static String restrictEncoding(MimePart part, String encoding) throws MessagingException {
/* 1290 */     if (!ignoreMultipartEncoding || encoding == null) {
/* 1291 */       return encoding;
/*      */     }
/* 1293 */     if (encoding.equalsIgnoreCase("7bit") || encoding.equalsIgnoreCase("8bit") || encoding.equalsIgnoreCase("binary"))
/*      */     {
/*      */       
/* 1296 */       return encoding;
/*      */     }
/* 1298 */     String type = part.getContentType();
/* 1299 */     if (type == null) {
/* 1300 */       return encoding;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 1308 */       ContentType cType = new ContentType(type);
/* 1309 */       if (cType.match("multipart/*"))
/* 1310 */         return null; 
/* 1311 */       if (cType.match("message/*") && !PropUtil.getBooleanSystemProperty("mail.mime.allowencodedmessages", false))
/*      */       {
/*      */         
/* 1314 */         return null; } 
/* 1315 */     } catch (ParseException pex) {}
/*      */ 
/*      */     
/* 1318 */     return encoding;
/*      */   }
/*      */   
/*      */   static void updateHeaders(MimePart part) throws MessagingException {
/* 1322 */     DataHandler dh = part.getDataHandler();
/* 1323 */     if (dh == null) {
/*      */       return;
/*      */     }
/*      */     try {
/* 1327 */       String type = dh.getContentType();
/* 1328 */       boolean composite = false;
/* 1329 */       boolean needCTHeader = (part.getHeader("Content-Type") == null);
/*      */       
/* 1331 */       ContentType cType = new ContentType(type);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1339 */       if (cType.match("multipart/*")) {
/*      */         Object o;
/* 1341 */         composite = true;
/*      */         
/* 1343 */         if (part instanceof MimeBodyPart) {
/* 1344 */           MimeBodyPart mbp = (MimeBodyPart)part;
/* 1345 */           o = (mbp.cachedContent != null) ? mbp.cachedContent : dh.getContent();
/*      */         }
/* 1347 */         else if (part instanceof MimeMessage) {
/* 1348 */           MimeMessage msg = (MimeMessage)part;
/* 1349 */           o = (msg.cachedContent != null) ? msg.cachedContent : dh.getContent();
/*      */         } else {
/*      */           
/* 1352 */           o = dh.getContent();
/* 1353 */         }  if (o instanceof MimeMultipart) {
/* 1354 */           ((MimeMultipart)o).updateHeaders();
/*      */         } else {
/* 1356 */           throw new MessagingException("MIME part of type \"" + type + "\" contains object of type " + o.getClass().getName() + " instead of MimeMultipart");
/*      */         }
/*      */       
/* 1359 */       } else if (cType.match("message/rfc822")) {
/* 1360 */         composite = true;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1368 */       if (dh instanceof MimePartDataHandler) {
/*      */         return;
/*      */       }
/*      */ 
/*      */       
/* 1373 */       if (!composite) {
/* 1374 */         if (part.getHeader("Content-Transfer-Encoding") == null) {
/* 1375 */           setEncoding(part, MimeUtility.getEncoding(dh));
/*      */         }
/* 1377 */         if (needCTHeader && setDefaultTextCharset && cType.match("text/*") && cType.getParameter("charset") == null) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1391 */           String charset, enc = part.getEncoding();
/* 1392 */           if (enc != null && enc.equalsIgnoreCase("7bit")) {
/* 1393 */             charset = "us-ascii";
/*      */           } else {
/* 1395 */             charset = MimeUtility.getDefaultMIMECharset();
/* 1396 */           }  cType.setParameter("charset", charset);
/* 1397 */           type = cType.toString();
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1404 */       if (needCTHeader) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1411 */         String s = part.getHeader("Content-Disposition", (String)null);
/* 1412 */         if (s != null) {
/*      */           
/* 1414 */           ContentDisposition cd = new ContentDisposition(s);
/* 1415 */           String filename = cd.getParameter("filename");
/* 1416 */           if (filename != null) {
/* 1417 */             cType.setParameter("name", filename);
/* 1418 */             type = cType.toString();
/*      */           } 
/*      */         } 
/*      */         
/* 1422 */         part.setHeader("Content-Type", type);
/*      */       } 
/* 1424 */     } catch (IOException ex) {
/* 1425 */       throw new MessagingException("IOException updating headers", ex);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   static void invalidateContentHeaders(MimePart part) throws MessagingException {
/* 1431 */     part.removeHeader("Content-Type");
/* 1432 */     part.removeHeader("Content-Transfer-Encoding");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static void writeTo(MimePart part, OutputStream os, String[] ignoreList) throws IOException, MessagingException {
/* 1439 */     LineOutputStream los = null;
/* 1440 */     if (os instanceof LineOutputStream) {
/* 1441 */       los = (LineOutputStream)os;
/*      */     } else {
/* 1443 */       los = new LineOutputStream(os);
/*      */     } 
/*      */ 
/*      */     
/* 1447 */     Enumeration hdrLines = part.getNonMatchingHeaderLines(ignoreList);
/* 1448 */     while (hdrLines.hasMoreElements()) {
/* 1449 */       los.writeln(hdrLines.nextElement());
/*      */     }
/*      */     
/* 1452 */     los.writeln();
/*      */ 
/*      */ 
/*      */     
/* 1456 */     InputStream is = null;
/* 1457 */     byte[] buf = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 1464 */       DataHandler dh = part.getDataHandler();
/* 1465 */       if (dh instanceof MimePartDataHandler)
/*      */       {
/*      */         
/* 1468 */         if (part instanceof MimeBodyPart) {
/* 1469 */           MimeBodyPart mbp = (MimeBodyPart)part;
/* 1470 */           is = mbp.getContentStream();
/* 1471 */         } else if (part instanceof MimeMessage) {
/* 1472 */           MimeMessage msg = (MimeMessage)part;
/* 1473 */           is = msg.getContentStream();
/*      */         } 
/*      */       }
/* 1476 */       if (is != null) {
/*      */         
/* 1478 */         buf = new byte[8192];
/*      */         int len;
/* 1480 */         while ((len = is.read(buf)) > 0)
/* 1481 */           os.write(buf, 0, len); 
/*      */       } else {
/* 1483 */         os = MimeUtility.encode(os, restrictEncoding(part, part.getEncoding()));
/*      */         
/* 1485 */         part.getDataHandler().writeTo(os);
/*      */       } 
/*      */     } finally {
/* 1488 */       if (is != null)
/* 1489 */         is.close(); 
/* 1490 */       buf = null;
/*      */     } 
/* 1492 */     os.flush();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class MimePartDataHandler
/*      */     extends DataHandler
/*      */   {
/*      */     public MimePartDataHandler(DataSource ds) {
/* 1505 */       super(ds);
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\internet\MimeBodyPart.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */