/*      */ package javax.mail.internet;
/*      */ 
/*      */ import com.sun.mail.util.ASCIIUtility;
/*      */ import com.sun.mail.util.LineInputStream;
/*      */ import com.sun.mail.util.LineOutputStream;
/*      */ import com.sun.mail.util.PropUtil;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import javax.activation.DataSource;
/*      */ import javax.mail.BodyPart;
/*      */ import javax.mail.MessageAware;
/*      */ import javax.mail.MessageContext;
/*      */ import javax.mail.MessagingException;
/*      */ import javax.mail.Multipart;
/*      */ import javax.mail.MultipartDataSource;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class MimeMultipart
/*      */   extends Multipart
/*      */ {
/*  122 */   protected DataSource ds = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean parsed = true;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean complete = true;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  141 */   private String preamble = null;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean ignoreMissingEndBoundary = true;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean ignoreMissingBoundaryParameter = true;
/*      */ 
/*      */   
/*      */   private boolean ignoreExistingBoundaryParameter = false;
/*      */ 
/*      */   
/*      */   private boolean allowEmpty = false;
/*      */ 
/*      */   
/*      */   private boolean bmparse = true;
/*      */ 
/*      */ 
/*      */   
/*      */   public MimeMultipart() {
/*  163 */     this("mixed");
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
/*      */   public MimeMultipart(String subtype) {
/*  179 */     String boundary = UniqueValue.getUniqueBoundaryValue();
/*  180 */     ContentType cType = new ContentType("multipart", subtype, null);
/*  181 */     cType.setParameter("boundary", boundary);
/*  182 */     this.contentType = cType.toString();
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
/*      */   public MimeMultipart(DataSource ds) throws MessagingException {
/*  206 */     if (ds instanceof MessageAware) {
/*  207 */       MessageContext mc = ((MessageAware)ds).getMessageContext();
/*  208 */       setParent(mc.getPart());
/*      */     } 
/*      */     
/*  211 */     if (ds instanceof MultipartDataSource) {
/*      */       
/*  213 */       setMultipartDataSource((MultipartDataSource)ds);
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  219 */     this.parsed = false;
/*  220 */     this.ds = ds;
/*  221 */     this.contentType = ds.getContentType();
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
/*      */   public synchronized void setSubType(String subtype) throws MessagingException {
/*  233 */     ContentType cType = new ContentType(this.contentType);
/*  234 */     cType.setSubType(subtype);
/*  235 */     this.contentType = cType.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized int getCount() throws MessagingException {
/*  244 */     parse();
/*  245 */     return super.getCount();
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
/*      */   public synchronized BodyPart getBodyPart(int index) throws MessagingException {
/*  257 */     parse();
/*  258 */     return super.getBodyPart(index);
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
/*      */   public synchronized BodyPart getBodyPart(String CID) throws MessagingException {
/*  270 */     parse();
/*      */     
/*  272 */     int count = getCount();
/*  273 */     for (int i = 0; i < count; i++) {
/*  274 */       MimeBodyPart part = (MimeBodyPart)getBodyPart(i);
/*  275 */       String s = part.getContentID();
/*  276 */       if (s != null && s.equals(CID))
/*  277 */         return part; 
/*      */     } 
/*  279 */     return null;
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
/*      */   public boolean removeBodyPart(BodyPart part) throws MessagingException {
/*  294 */     parse();
/*  295 */     return super.removeBodyPart(part);
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
/*      */   public void removeBodyPart(int index) throws MessagingException {
/*  311 */     parse();
/*  312 */     super.removeBodyPart(index);
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
/*      */   public synchronized void addBodyPart(BodyPart part) throws MessagingException {
/*  327 */     parse();
/*  328 */     super.addBodyPart(part);
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
/*      */   public synchronized void addBodyPart(BodyPart part, int index) throws MessagingException {
/*  347 */     parse();
/*  348 */     super.addBodyPart(part, index);
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
/*      */   public synchronized boolean isComplete() throws MessagingException {
/*  366 */     parse();
/*  367 */     return this.complete;
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
/*      */   public synchronized String getPreamble() throws MessagingException {
/*  379 */     parse();
/*  380 */     return this.preamble;
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
/*      */   public synchronized void setPreamble(String preamble) throws MessagingException {
/*  396 */     this.preamble = preamble;
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
/*  417 */     parse();
/*  418 */     for (int i = 0; i < this.parts.size(); i++) {
/*  419 */       ((MimeBodyPart)this.parts.elementAt(i)).updateHeaders();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void writeTo(OutputStream os) throws IOException, MessagingException {
/*  428 */     parse();
/*      */     
/*  430 */     String boundary = "--" + (new ContentType(this.contentType)).getParameter("boundary");
/*      */     
/*  432 */     LineOutputStream los = new LineOutputStream(os);
/*      */ 
/*      */     
/*  435 */     if (this.preamble != null) {
/*  436 */       byte[] pb = ASCIIUtility.getBytes(this.preamble);
/*  437 */       los.write(pb);
/*      */       
/*  439 */       if (pb.length > 0 && pb[pb.length - 1] != 13 && pb[pb.length - 1] != 10)
/*      */       {
/*  441 */         los.writeln();
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  446 */     if (this.parts.size() == 0) {
/*      */ 
/*      */ 
/*      */       
/*  450 */       this.allowEmpty = PropUtil.getBooleanSystemProperty("mail.mime.multipart.allowempty", false);
/*      */       
/*  452 */       if (this.allowEmpty) {
/*      */         
/*  454 */         los.writeln(boundary);
/*  455 */         los.writeln();
/*      */       } else {
/*  457 */         throw new MessagingException("Empty multipart: " + this.contentType);
/*      */       } 
/*      */     } else {
/*  460 */       for (int i = 0; i < this.parts.size(); i++) {
/*  461 */         los.writeln(boundary);
/*  462 */         ((MimeBodyPart)this.parts.elementAt(i)).writeTo(os);
/*  463 */         los.writeln();
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  468 */     los.writeln(boundary + "--");
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
/*      */   protected synchronized void parse() throws MessagingException {
/*  481 */     if (this.parsed) {
/*      */       return;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  487 */     this.ignoreMissingEndBoundary = PropUtil.getBooleanSystemProperty("mail.mime.multipart.ignoremissingendboundary", true);
/*      */ 
/*      */     
/*  490 */     this.ignoreMissingBoundaryParameter = PropUtil.getBooleanSystemProperty("mail.mime.multipart.ignoremissingboundaryparameter", true);
/*      */ 
/*      */     
/*  493 */     this.ignoreExistingBoundaryParameter = PropUtil.getBooleanSystemProperty("mail.mime.multipart.ignoreexistingboundaryparameter", false);
/*      */ 
/*      */     
/*  496 */     this.allowEmpty = PropUtil.getBooleanSystemProperty("mail.mime.multipart.allowempty", false);
/*      */ 
/*      */     
/*  499 */     this.bmparse = PropUtil.getBooleanSystemProperty("mail.mime.multipart.bmparse", true);
/*      */ 
/*      */     
/*  502 */     if (this.bmparse) {
/*  503 */       parsebm();
/*      */       
/*      */       return;
/*      */     } 
/*  507 */     InputStream in = null;
/*  508 */     SharedInputStream sin = null;
/*  509 */     long start = 0L, end = 0L;
/*      */     
/*      */     try {
/*  512 */       in = this.ds.getInputStream();
/*  513 */       if (!(in instanceof java.io.ByteArrayInputStream) && !(in instanceof BufferedInputStream) && !(in instanceof SharedInputStream))
/*      */       {
/*      */         
/*  516 */         in = new BufferedInputStream(in); } 
/*  517 */     } catch (Exception ex) {
/*  518 */       throw new MessagingException("No inputstream from datasource", ex);
/*      */     } 
/*  520 */     if (in instanceof SharedInputStream) {
/*  521 */       sin = (SharedInputStream)in;
/*      */     }
/*  523 */     ContentType cType = new ContentType(this.contentType);
/*  524 */     String boundary = null;
/*  525 */     if (!this.ignoreExistingBoundaryParameter) {
/*  526 */       String bp = cType.getParameter("boundary");
/*  527 */       if (bp != null)
/*  528 */         boundary = "--" + bp; 
/*      */     } 
/*  530 */     if (boundary == null && !this.ignoreMissingBoundaryParameter && !this.ignoreExistingBoundaryParameter)
/*      */     {
/*  532 */       throw new MessagingException("Missing boundary parameter");
/*      */     }
/*      */     
/*      */     try {
/*  536 */       LineInputStream lin = new LineInputStream(in);
/*  537 */       StringBuffer preamblesb = null;
/*      */       
/*  539 */       String lineSeparator = null; String line;
/*  540 */       while ((line = lin.readLine()) != null) {
/*      */         int i;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  548 */         for (i = line.length() - 1; i >= 0; i--) {
/*  549 */           char c = line.charAt(i);
/*  550 */           if (c != ' ' && c != '\t')
/*      */             break; 
/*      */         } 
/*  553 */         line = line.substring(0, i + 1);
/*  554 */         if (boundary != null) {
/*  555 */           if (line.equals(boundary))
/*      */             break; 
/*  557 */           if (line.length() == boundary.length() + 2 && line.startsWith(boundary) && line.endsWith("--")) {
/*      */             
/*  559 */             line = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             break;
/*      */           } 
/*  568 */         } else if (line.length() > 2 && line.startsWith("--") && (
/*  569 */           line.length() <= 4 || !allDashes(line))) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  577 */           boundary = line;
/*      */ 
/*      */           
/*      */           break;
/*      */         } 
/*      */ 
/*      */         
/*  584 */         if (line.length() > 0) {
/*      */ 
/*      */           
/*  587 */           if (lineSeparator == null) {
/*      */             try {
/*  589 */               lineSeparator = System.getProperty("line.separator", "\n");
/*      */             }
/*  591 */             catch (SecurityException ex) {
/*  592 */               lineSeparator = "\n";
/*      */             } 
/*      */           }
/*      */           
/*  596 */           if (preamblesb == null)
/*  597 */             preamblesb = new StringBuffer(line.length() + 2); 
/*  598 */           preamblesb.append(line).append(lineSeparator);
/*      */         } 
/*      */       } 
/*      */       
/*  602 */       if (preamblesb != null) {
/*  603 */         this.preamble = preamblesb.toString();
/*      */       }
/*  605 */       if (line == null) {
/*  606 */         if (this.allowEmpty) {
/*      */           return;
/*      */         }
/*  609 */         throw new MessagingException("Missing start boundary");
/*      */       } 
/*      */ 
/*      */       
/*  613 */       byte[] bndbytes = ASCIIUtility.getBytes(boundary);
/*  614 */       int bl = bndbytes.length;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  620 */       boolean done = false;
/*      */       
/*  622 */       while (!done) {
/*  623 */         MimeBodyPart part; InternetHeaders headers = null;
/*  624 */         if (sin != null) {
/*  625 */           start = sin.getPosition();
/*      */           
/*  627 */           while ((line = lin.readLine()) != null && line.length() > 0);
/*      */           
/*  629 */           if (line == null) {
/*  630 */             if (!this.ignoreMissingEndBoundary) {
/*  631 */               throw new MessagingException("missing multipart end boundary");
/*      */             }
/*      */             
/*  634 */             this.complete = false;
/*      */             
/*      */             break;
/*      */           } 
/*      */         } else {
/*  639 */           headers = createInternetHeaders(in);
/*      */         } 
/*      */         
/*  642 */         if (!in.markSupported()) {
/*  643 */           throw new MessagingException("Stream doesn't support mark");
/*      */         }
/*  645 */         ByteArrayOutputStream buf = null;
/*      */         
/*  647 */         if (sin == null) {
/*  648 */           buf = new ByteArrayOutputStream();
/*      */         } else {
/*  650 */           end = sin.getPosition();
/*      */         } 
/*  652 */         boolean bol = true;
/*      */         
/*  654 */         int eol1 = -1, eol2 = -1;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         while (true) {
/*  660 */           if (bol) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  666 */             in.mark(bl + 4 + 1000);
/*      */             int i;
/*  668 */             for (i = 0; i < bl && 
/*  669 */               in.read() == (bndbytes[i] & 0xFF); i++);
/*      */             
/*  671 */             if (i == bl) {
/*      */               
/*  673 */               int b2 = in.read();
/*  674 */               if (b2 == 45 && 
/*  675 */                 in.read() == 45) {
/*  676 */                 this.complete = true;
/*  677 */                 done = true;
/*      */                 
/*      */                 break;
/*      */               } 
/*      */               
/*  682 */               while (b2 == 32 || b2 == 9) {
/*  683 */                 b2 = in.read();
/*      */               }
/*  685 */               if (b2 == 10)
/*      */                 break; 
/*  687 */               if (b2 == 13) {
/*  688 */                 in.mark(1);
/*  689 */                 if (in.read() != 10) {
/*  690 */                   in.reset();
/*      */                 }
/*      */                 break;
/*      */               } 
/*      */             } 
/*  695 */             in.reset();
/*      */ 
/*      */ 
/*      */             
/*  699 */             if (buf != null && eol1 != -1) {
/*  700 */               buf.write(eol1);
/*  701 */               if (eol2 != -1)
/*  702 */                 buf.write(eol2); 
/*  703 */               eol1 = eol2 = -1;
/*      */             } 
/*      */           } 
/*      */           
/*      */           int b;
/*  708 */           if ((b = in.read()) < 0) {
/*  709 */             if (!this.ignoreMissingEndBoundary) {
/*  710 */               throw new MessagingException("missing multipart end boundary");
/*      */             }
/*  712 */             this.complete = false;
/*  713 */             done = true;
/*      */ 
/*      */ 
/*      */             
/*      */             break;
/*      */           } 
/*      */ 
/*      */           
/*  721 */           if (b == 13 || b == 10) {
/*  722 */             bol = true;
/*  723 */             if (sin != null)
/*  724 */               end = sin.getPosition() - 1L; 
/*  725 */             eol1 = b;
/*  726 */             if (b == 13) {
/*  727 */               in.mark(1);
/*  728 */               if ((b = in.read()) == 10) {
/*  729 */                 eol2 = b; continue;
/*      */               } 
/*  731 */               in.reset();
/*      */             }  continue;
/*      */           } 
/*  734 */           bol = false;
/*  735 */           if (buf != null) {
/*  736 */             buf.write(b);
/*      */           }
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  744 */         if (sin != null) {
/*  745 */           part = createMimeBodyPartIs(sin.newStream(start, end));
/*      */         } else {
/*  747 */           part = createMimeBodyPart(headers, buf.toByteArray());
/*  748 */         }  super.addBodyPart(part);
/*      */       } 
/*  750 */     } catch (IOException ioex) {
/*  751 */       throw new MessagingException("IO Error", ioex);
/*      */     } finally {
/*      */       try {
/*  754 */         in.close();
/*  755 */       } catch (IOException cex) {}
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  760 */     this.parsed = true;
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
/*      */   private synchronized void parsebm() throws MessagingException {
/*  777 */     if (this.parsed) {
/*      */       return;
/*      */     }
/*  780 */     InputStream in = null;
/*  781 */     SharedInputStream sin = null;
/*  782 */     long start = 0L, end = 0L;
/*      */     
/*      */     try {
/*  785 */       in = this.ds.getInputStream();
/*  786 */       if (!(in instanceof java.io.ByteArrayInputStream) && !(in instanceof BufferedInputStream) && !(in instanceof SharedInputStream))
/*      */       {
/*      */         
/*  789 */         in = new BufferedInputStream(in); } 
/*  790 */     } catch (Exception ex) {
/*  791 */       throw new MessagingException("No inputstream from datasource", ex);
/*      */     } 
/*  793 */     if (in instanceof SharedInputStream) {
/*  794 */       sin = (SharedInputStream)in;
/*      */     }
/*  796 */     ContentType cType = new ContentType(this.contentType);
/*  797 */     String boundary = null;
/*  798 */     if (!this.ignoreExistingBoundaryParameter) {
/*  799 */       String bp = cType.getParameter("boundary");
/*  800 */       if (bp != null)
/*  801 */         boundary = "--" + bp; 
/*      */     } 
/*  803 */     if (boundary == null && !this.ignoreMissingBoundaryParameter && !this.ignoreExistingBoundaryParameter)
/*      */     {
/*  805 */       throw new MessagingException("Missing boundary parameter");
/*      */     }
/*      */     
/*      */     try {
/*  809 */       LineInputStream lin = new LineInputStream(in);
/*  810 */       StringBuffer preamblesb = null;
/*      */       
/*  812 */       String lineSeparator = null; String line;
/*  813 */       while ((line = lin.readLine()) != null) {
/*      */         int k;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  821 */         for (k = line.length() - 1; k >= 0; k--) {
/*  822 */           char c = line.charAt(k);
/*  823 */           if (c != ' ' && c != '\t')
/*      */             break; 
/*      */         } 
/*  826 */         line = line.substring(0, k + 1);
/*  827 */         if (boundary != null) {
/*  828 */           if (line.equals(boundary))
/*      */             break; 
/*  830 */           if (line.length() == boundary.length() + 2 && line.startsWith(boundary) && line.endsWith("--")) {
/*      */             
/*  832 */             line = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             break;
/*      */           } 
/*  841 */         } else if (line.length() > 2 && line.startsWith("--") && (
/*  842 */           line.length() <= 4 || !allDashes(line))) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  850 */           boundary = line;
/*      */ 
/*      */           
/*      */           break;
/*      */         } 
/*      */ 
/*      */         
/*  857 */         if (line.length() > 0) {
/*      */ 
/*      */           
/*  860 */           if (lineSeparator == null) {
/*      */             try {
/*  862 */               lineSeparator = System.getProperty("line.separator", "\n");
/*      */             }
/*  864 */             catch (SecurityException ex) {
/*  865 */               lineSeparator = "\n";
/*      */             } 
/*      */           }
/*      */           
/*  869 */           if (preamblesb == null)
/*  870 */             preamblesb = new StringBuffer(line.length() + 2); 
/*  871 */           preamblesb.append(line).append(lineSeparator);
/*      */         } 
/*      */       } 
/*      */       
/*  875 */       if (preamblesb != null) {
/*  876 */         this.preamble = preamblesb.toString();
/*      */       }
/*  878 */       if (line == null) {
/*  879 */         if (this.allowEmpty) {
/*      */           return;
/*      */         }
/*  882 */         throw new MessagingException("Missing start boundary");
/*      */       } 
/*      */ 
/*      */       
/*  886 */       byte[] bndbytes = ASCIIUtility.getBytes(boundary);
/*  887 */       int bl = bndbytes.length;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  894 */       int[] bcs = new int[256];
/*  895 */       for (int i = 0; i < bl; i++) {
/*  896 */         bcs[bndbytes[i] & 0xFF] = i + 1;
/*      */       }
/*      */       
/*  899 */       int[] gss = new int[bl];
/*      */       
/*  901 */       for (int j = bl; j > 0; j--) {
/*      */         
/*  903 */         int k = bl - 1; while (true) { if (k >= j) {
/*      */             
/*  905 */             if (bndbytes[k] == bndbytes[k - j]) {
/*      */               
/*  907 */               gss[k - 1] = j;
/*      */               
/*      */               k--;
/*      */             } 
/*      */             
/*      */             break;
/*      */           } 
/*  914 */           while (k > 0)
/*  915 */             gss[--k] = j;  break; }
/*      */       
/*  917 */       }  gss[bl - 1] = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  923 */       boolean done = false;
/*      */       
/*  925 */       while (!done) {
/*  926 */         int eolLen; MimeBodyPart part; InternetHeaders headers = null;
/*  927 */         if (sin != null) {
/*  928 */           start = sin.getPosition();
/*      */           
/*  930 */           while ((line = lin.readLine()) != null && line.length() > 0);
/*      */           
/*  932 */           if (line == null) {
/*  933 */             if (!this.ignoreMissingEndBoundary) {
/*  934 */               throw new MessagingException("missing multipart end boundary");
/*      */             }
/*      */             
/*  937 */             this.complete = false;
/*      */             
/*      */             break;
/*      */           } 
/*      */         } else {
/*  942 */           headers = createInternetHeaders(in);
/*      */         } 
/*      */         
/*  945 */         if (!in.markSupported()) {
/*  946 */           throw new MessagingException("Stream doesn't support mark");
/*      */         }
/*  948 */         ByteArrayOutputStream buf = null;
/*      */         
/*  950 */         if (sin == null) {
/*  951 */           buf = new ByteArrayOutputStream();
/*      */         } else {
/*  953 */           end = sin.getPosition();
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  965 */         byte[] inbuf = new byte[bl];
/*  966 */         byte[] previnbuf = new byte[bl];
/*  967 */         int inSize = 0;
/*  968 */         int prevSize = 0;
/*      */         
/*  970 */         boolean first = true;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         while (true) {
/*  976 */           in.mark(bl + 4 + 1000);
/*  977 */           eolLen = 0;
/*  978 */           inSize = readFully(in, inbuf, 0, bl);
/*  979 */           if (inSize < bl) {
/*      */             
/*  981 */             if (!this.ignoreMissingEndBoundary) {
/*  982 */               throw new MessagingException("missing multipart end boundary");
/*      */             }
/*  984 */             if (sin != null)
/*  985 */               end = sin.getPosition(); 
/*  986 */             this.complete = false;
/*  987 */             done = true;
/*      */             
/*      */             break;
/*      */           } 
/*      */           int k;
/*  992 */           for (k = bl - 1; k >= 0 && 
/*  993 */             inbuf[k] == bndbytes[k]; k--);
/*      */ 
/*      */           
/*  996 */           if (k < 0) {
/*  997 */             eolLen = 0;
/*  998 */             if (!first) {
/*      */ 
/*      */               
/* 1001 */               int b = previnbuf[prevSize - 1];
/* 1002 */               if (b == 13 || b == 10) {
/* 1003 */                 eolLen = 1;
/* 1004 */                 if (b == 10 && prevSize >= 2) {
/* 1005 */                   b = previnbuf[prevSize - 2];
/* 1006 */                   if (b == 13)
/* 1007 */                     eolLen = 2; 
/*      */                 } 
/*      */               } 
/*      */             } 
/* 1011 */             if (first || eolLen > 0) {
/* 1012 */               if (sin != null)
/*      */               {
/*      */                 
/* 1015 */                 end = sin.getPosition() - bl - eolLen;
/*      */               }
/*      */               
/* 1018 */               int b2 = in.read();
/* 1019 */               if (b2 == 45 && 
/* 1020 */                 in.read() == 45) {
/* 1021 */                 this.complete = true;
/* 1022 */                 done = true;
/*      */                 
/*      */                 break;
/*      */               } 
/*      */               
/* 1027 */               while (b2 == 32 || b2 == 9) {
/* 1028 */                 b2 = in.read();
/*      */               }
/* 1030 */               if (b2 == 10)
/*      */                 break; 
/* 1032 */               if (b2 == 13) {
/* 1033 */                 in.mark(1);
/* 1034 */                 if (in.read() != 10)
/* 1035 */                   in.reset(); 
/*      */                 break;
/*      */               } 
/*      */             } 
/* 1039 */             k = 0;
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1049 */           int skip = Math.max(k + 1 - bcs[inbuf[k] & Byte.MAX_VALUE], gss[k]);
/*      */           
/* 1051 */           if (skip < 2) {
/*      */ 
/*      */ 
/*      */             
/* 1055 */             if (sin == null && prevSize > 1)
/* 1056 */               buf.write(previnbuf, 0, prevSize - 1); 
/* 1057 */             in.reset();
/* 1058 */             skipFully(in, 1L);
/* 1059 */             if (prevSize >= 1) {
/*      */               
/* 1061 */               previnbuf[0] = previnbuf[prevSize - 1];
/* 1062 */               previnbuf[1] = inbuf[0];
/* 1063 */               prevSize = 2;
/*      */             } else {
/*      */               
/* 1066 */               previnbuf[0] = inbuf[0];
/* 1067 */               prevSize = 1;
/*      */             }
/*      */           
/*      */           } else {
/*      */             
/* 1072 */             if (prevSize > 0 && sin == null) {
/* 1073 */               buf.write(previnbuf, 0, prevSize);
/*      */             }
/* 1075 */             prevSize = skip;
/* 1076 */             in.reset();
/* 1077 */             skipFully(in, prevSize);
/*      */             
/* 1079 */             byte[] tmp = inbuf;
/* 1080 */             inbuf = previnbuf;
/* 1081 */             previnbuf = tmp;
/*      */           } 
/* 1083 */           first = false;
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1090 */         if (sin != null) {
/* 1091 */           part = createMimeBodyPartIs(sin.newStream(start, end));
/*      */         } else {
/*      */           
/* 1094 */           if (prevSize - eolLen > 0) {
/* 1095 */             buf.write(previnbuf, 0, prevSize - eolLen);
/*      */           }
/*      */           
/* 1098 */           if (!this.complete && inSize > 0)
/* 1099 */             buf.write(inbuf, 0, inSize); 
/* 1100 */           part = createMimeBodyPart(headers, buf.toByteArray());
/*      */         } 
/* 1102 */         super.addBodyPart(part);
/*      */       } 
/* 1104 */     } catch (IOException ioex) {
/* 1105 */       throw new MessagingException("IO Error", ioex);
/*      */     } finally {
/*      */       try {
/* 1108 */         in.close();
/* 1109 */       } catch (IOException cex) {}
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1114 */     this.parsed = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean allDashes(String s) {
/* 1121 */     for (int i = 0; i < s.length(); i++) {
/* 1122 */       if (s.charAt(i) != '-')
/* 1123 */         return false; 
/*      */     } 
/* 1125 */     return true;
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
/*      */   private static int readFully(InputStream in, byte[] buf, int off, int len) throws IOException {
/* 1144 */     if (len == 0)
/* 1145 */       return 0; 
/* 1146 */     int total = 0;
/* 1147 */     while (len > 0) {
/* 1148 */       int bsize = in.read(buf, off, len);
/* 1149 */       if (bsize <= 0)
/*      */         break; 
/* 1151 */       off += bsize;
/* 1152 */       total += bsize;
/* 1153 */       len -= bsize;
/*      */     } 
/* 1155 */     return (total > 0) ? total : -1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void skipFully(InputStream in, long offset) throws IOException {
/* 1163 */     while (offset > 0L) {
/* 1164 */       long cur = in.skip(offset);
/* 1165 */       if (cur <= 0L)
/* 1166 */         throw new EOFException("can't skip"); 
/* 1167 */       offset -= cur;
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
/* 1184 */     return new InternetHeaders(is);
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
/*      */   protected MimeBodyPart createMimeBodyPart(InternetHeaders headers, byte[] content) throws MessagingException {
/* 1201 */     return new MimeBodyPart(headers, content);
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
/*      */   protected MimeBodyPart createMimeBodyPart(InputStream is) throws MessagingException {
/* 1217 */     return new MimeBodyPart(is);
/*      */   }
/*      */ 
/*      */   
/*      */   private MimeBodyPart createMimeBodyPartIs(InputStream is) throws MessagingException {
/*      */     try {
/* 1223 */       return createMimeBodyPart(is);
/*      */     } finally {
/*      */       try {
/* 1226 */         is.close();
/* 1227 */       } catch (IOException ex) {}
/*      */     } 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\internet\MimeMultipart.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */