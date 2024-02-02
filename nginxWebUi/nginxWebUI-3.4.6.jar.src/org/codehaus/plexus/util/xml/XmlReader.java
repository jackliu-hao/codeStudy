/*     */ package org.codehaus.plexus.util.xml;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Locale;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class XmlReader
/*     */   extends Reader
/*     */ {
/*     */   private static final int BUFFER_SIZE = 4096;
/*     */   private static final String UTF_8 = "UTF-8";
/*     */   private static final String US_ASCII = "US-ASCII";
/*     */   private static final String UTF_16BE = "UTF-16BE";
/*     */   private static final String UTF_16LE = "UTF-16LE";
/*     */   private static final String UTF_16 = "UTF-16";
/*     */   private static final String EBCDIC = "CP1047";
/*  75 */   private static String _staticDefaultEncoding = null;
/*     */ 
/*     */ 
/*     */   
/*     */   private Reader _reader;
/*     */ 
/*     */ 
/*     */   
/*     */   private String _encoding;
/*     */ 
/*     */ 
/*     */   
/*     */   private String _defaultEncoding;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setDefaultEncoding(String encoding) {
/*  93 */     _staticDefaultEncoding = encoding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getDefaultEncoding() {
/* 104 */     return _staticDefaultEncoding;
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
/*     */   public XmlReader(File file) throws IOException {
/* 124 */     this(new FileInputStream(file));
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
/*     */   public XmlReader(InputStream is) throws IOException {
/* 143 */     this(is, true);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XmlReader(InputStream is, boolean lenient) throws IOException, XmlStreamReaderException {
/* 177 */     this._defaultEncoding = _staticDefaultEncoding;
/*     */     
/*     */     try {
/* 180 */       doRawStream(is, lenient);
/*     */     }
/* 182 */     catch (XmlStreamReaderException ex) {
/*     */       
/* 184 */       if (!lenient)
/*     */       {
/* 186 */         throw ex;
/*     */       }
/*     */ 
/*     */       
/* 190 */       doLenientDetection(null, ex);
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
/*     */ 
/*     */   
/*     */   public XmlReader(URL url) throws IOException {
/* 215 */     this(url.openConnection());
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
/*     */   
/*     */   public XmlReader(URLConnection conn) throws IOException {
/* 238 */     this._defaultEncoding = _staticDefaultEncoding;
/* 239 */     boolean lenient = true;
/* 240 */     if (conn instanceof java.net.HttpURLConnection) {
/*     */       
/*     */       try
/*     */       {
/* 244 */         doHttpStream(conn.getInputStream(), conn.getContentType(), lenient);
/*     */       }
/* 246 */       catch (XmlStreamReaderException ex)
/*     */       {
/* 248 */         doLenientDetection(conn.getContentType(), ex);
/*     */       }
/*     */     
/* 251 */     } else if (conn.getContentType() != null) {
/*     */ 
/*     */       
/*     */       try {
/* 255 */         doHttpStream(conn.getInputStream(), conn.getContentType(), lenient);
/*     */       }
/* 257 */       catch (XmlStreamReaderException ex) {
/*     */         
/* 259 */         doLenientDetection(conn.getContentType(), ex);
/*     */       } 
/*     */     } else {
/*     */ 
/*     */       
/*     */       try {
/*     */         
/* 266 */         doRawStream(conn.getInputStream(), lenient);
/*     */       }
/* 268 */       catch (XmlStreamReaderException ex) {
/*     */         
/* 270 */         doLenientDetection(null, ex);
/*     */       } 
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
/*     */   
/*     */   public XmlReader(InputStream is, String httpContentType) throws IOException {
/* 295 */     this(is, httpContentType, true);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XmlReader(InputStream is, String httpContentType, boolean lenient, String defaultEncoding) throws IOException, XmlStreamReaderException {
/* 335 */     this._defaultEncoding = (defaultEncoding == null) ? _staticDefaultEncoding : defaultEncoding;
/*     */     
/*     */     try {
/* 338 */       doHttpStream(is, httpContentType, lenient);
/*     */     }
/* 340 */     catch (XmlStreamReaderException ex) {
/*     */       
/* 342 */       if (!lenient)
/*     */       {
/* 344 */         throw ex;
/*     */       }
/*     */ 
/*     */       
/* 348 */       doLenientDetection(httpContentType, ex);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XmlReader(InputStream is, String httpContentType, boolean lenient) throws IOException, XmlStreamReaderException {
/* 389 */     this(is, httpContentType, lenient, null);
/*     */   }
/*     */ 
/*     */   
/*     */   private void doLenientDetection(String httpContentType, XmlStreamReaderException ex) throws IOException {
/* 394 */     if (httpContentType != null)
/*     */     {
/* 396 */       if (httpContentType.startsWith("text/html")) {
/*     */         
/* 398 */         httpContentType = httpContentType.substring("text/html".length());
/* 399 */         httpContentType = "text/xml" + httpContentType;
/*     */         
/*     */         try {
/* 402 */           doHttpStream(ex.getInputStream(), httpContentType, true);
/* 403 */           ex = null;
/*     */         }
/* 405 */         catch (XmlStreamReaderException ex2) {
/*     */           
/* 407 */           ex = ex2;
/*     */         } 
/*     */       } 
/*     */     }
/* 411 */     if (ex != null) {
/*     */       
/* 413 */       String encoding = ex.getXmlEncoding();
/* 414 */       if (encoding == null)
/*     */       {
/* 416 */         encoding = ex.getContentTypeEncoding();
/*     */       }
/* 418 */       if (encoding == null)
/*     */       {
/* 420 */         encoding = (this._defaultEncoding == null) ? "UTF-8" : this._defaultEncoding;
/*     */       }
/* 422 */       prepareReader(ex.getInputStream(), encoding);
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
/*     */   public String getEncoding() {
/* 435 */     return this._encoding;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(char[] buf, int offset, int len) throws IOException {
/* 440 */     return this._reader.read(buf, offset, len);
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
/*     */   public void close() throws IOException {
/* 453 */     this._reader.close();
/*     */   }
/*     */ 
/*     */   
/*     */   private void doRawStream(InputStream is, boolean lenient) throws IOException {
/* 458 */     BufferedInputStream pis = new BufferedInputStream(is, 4096);
/* 459 */     String bomEnc = getBOMEncoding(pis);
/* 460 */     String xmlGuessEnc = getXMLGuessEncoding(pis);
/* 461 */     String xmlEnc = getXmlProlog(pis, xmlGuessEnc);
/* 462 */     String encoding = calculateRawEncoding(bomEnc, xmlGuessEnc, xmlEnc, pis);
/* 463 */     prepareReader(pis, encoding);
/*     */   }
/*     */ 
/*     */   
/*     */   private void doHttpStream(InputStream is, String httpContentType, boolean lenient) throws IOException {
/* 468 */     BufferedInputStream pis = new BufferedInputStream(is, 4096);
/* 469 */     String cTMime = getContentTypeMime(httpContentType);
/* 470 */     String cTEnc = getContentTypeEncoding(httpContentType);
/* 471 */     String bomEnc = getBOMEncoding(pis);
/* 472 */     String xmlGuessEnc = getXMLGuessEncoding(pis);
/* 473 */     String xmlEnc = getXmlProlog(pis, xmlGuessEnc);
/* 474 */     String encoding = calculateHttpEncoding(cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc, pis, lenient);
/* 475 */     prepareReader(pis, encoding);
/*     */   }
/*     */ 
/*     */   
/*     */   private void prepareReader(InputStream is, String encoding) throws IOException {
/* 480 */     this._reader = new InputStreamReader(is, encoding);
/* 481 */     this._encoding = encoding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String calculateRawEncoding(String bomEnc, String xmlGuessEnc, String xmlEnc, InputStream is) throws IOException {
/*     */     String encoding;
/* 489 */     if (bomEnc == null) {
/*     */       
/* 491 */       if (xmlGuessEnc == null || xmlEnc == null)
/*     */       {
/* 493 */         encoding = (this._defaultEncoding == null) ? "UTF-8" : this._defaultEncoding;
/*     */       }
/* 495 */       else if (xmlEnc.equals("UTF-16") && (xmlGuessEnc.equals("UTF-16BE") || xmlGuessEnc.equals("UTF-16LE")))
/*     */       {
/* 497 */         encoding = xmlGuessEnc;
/*     */       }
/*     */       else
/*     */       {
/* 501 */         encoding = xmlEnc;
/*     */       }
/*     */     
/* 504 */     } else if (bomEnc.equals("UTF-8")) {
/*     */       
/* 506 */       if (xmlGuessEnc != null && !xmlGuessEnc.equals("UTF-8"))
/*     */       {
/* 508 */         throw new XmlStreamReaderException(RAW_EX_1.format(new Object[] { bomEnc, xmlGuessEnc, xmlEnc }, ), bomEnc, xmlGuessEnc, xmlEnc, is);
/*     */       }
/*     */       
/* 511 */       if (xmlEnc != null && !xmlEnc.equals("UTF-8"))
/*     */       {
/* 513 */         throw new XmlStreamReaderException(RAW_EX_1.format(new Object[] { bomEnc, xmlGuessEnc, xmlEnc }, ), bomEnc, xmlGuessEnc, xmlEnc, is);
/*     */       }
/*     */       
/* 516 */       encoding = "UTF-8";
/*     */     }
/* 518 */     else if (bomEnc.equals("UTF-16BE") || bomEnc.equals("UTF-16LE")) {
/*     */       
/* 520 */       if (xmlGuessEnc != null && !xmlGuessEnc.equals(bomEnc))
/*     */       {
/* 522 */         throw new IOException(RAW_EX_1.format(new Object[] { bomEnc, xmlGuessEnc, xmlEnc }));
/*     */       }
/* 524 */       if (xmlEnc != null && !xmlEnc.equals("UTF-16") && !xmlEnc.equals(bomEnc))
/*     */       {
/* 526 */         throw new XmlStreamReaderException(RAW_EX_1.format(new Object[] { bomEnc, xmlGuessEnc, xmlEnc }, ), bomEnc, xmlGuessEnc, xmlEnc, is);
/*     */       }
/*     */       
/* 529 */       encoding = bomEnc;
/*     */     }
/*     */     else {
/*     */       
/* 533 */       throw new XmlStreamReaderException(RAW_EX_2.format(new Object[] { bomEnc, xmlGuessEnc, xmlEnc }, ), bomEnc, xmlGuessEnc, xmlEnc, is);
/*     */     } 
/*     */     
/* 536 */     return encoding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String calculateHttpEncoding(String cTMime, String cTEnc, String bomEnc, String xmlGuessEnc, String xmlEnc, InputStream is, boolean lenient) throws IOException {
/*     */     String encoding;
/* 544 */     if ((lenient & ((xmlEnc != null) ? 1 : 0)) != 0) {
/*     */       
/* 546 */       encoding = xmlEnc;
/*     */     }
/*     */     else {
/*     */       
/* 550 */       boolean appXml = isAppXml(cTMime);
/* 551 */       boolean textXml = isTextXml(cTMime);
/* 552 */       if (appXml || textXml) {
/*     */         
/* 554 */         if (cTEnc == null) {
/*     */           
/* 556 */           if (appXml) {
/*     */             
/* 558 */             encoding = calculateRawEncoding(bomEnc, xmlGuessEnc, xmlEnc, is);
/*     */           }
/*     */           else {
/*     */             
/* 562 */             encoding = (this._defaultEncoding == null) ? "US-ASCII" : this._defaultEncoding;
/*     */           } 
/*     */         } else {
/* 565 */           if (bomEnc != null && (cTEnc.equals("UTF-16BE") || cTEnc.equals("UTF-16LE")))
/*     */           {
/* 567 */             throw new XmlStreamReaderException(HTTP_EX_1.format(new Object[] { cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc }, ), cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc, is);
/*     */           }
/*     */           
/* 570 */           if (cTEnc.equals("UTF-16")) {
/*     */             
/* 572 */             if (bomEnc != null && bomEnc.startsWith("UTF-16"))
/*     */             {
/* 574 */               encoding = bomEnc;
/*     */             }
/*     */             else
/*     */             {
/* 578 */               throw new XmlStreamReaderException(HTTP_EX_2.format(new Object[] { cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc }, ), cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc, is);
/*     */             }
/*     */           
/*     */           }
/*     */           else {
/*     */             
/* 584 */             encoding = cTEnc;
/*     */           } 
/*     */         } 
/*     */       } else {
/*     */         
/* 589 */         throw new XmlStreamReaderException(HTTP_EX_3.format(new Object[] { cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc }, ), cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc, is);
/*     */       } 
/*     */     } 
/*     */     
/* 593 */     return encoding;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static String getContentTypeMime(String httpContentType) {
/* 599 */     String mime = null;
/* 600 */     if (httpContentType != null) {
/*     */       
/* 602 */       int i = httpContentType.indexOf(";");
/* 603 */       mime = ((i == -1) ? httpContentType : httpContentType.substring(0, i)).trim();
/*     */     } 
/* 605 */     return mime;
/*     */   }
/*     */   
/* 608 */   private static final Pattern CHARSET_PATTERN = Pattern.compile("charset=([.[^; ]]*)");
/*     */ 
/*     */ 
/*     */   
/*     */   private static String getContentTypeEncoding(String httpContentType) {
/* 613 */     String encoding = null;
/* 614 */     if (httpContentType != null) {
/*     */       
/* 616 */       int i = httpContentType.indexOf(";");
/* 617 */       if (i > -1) {
/*     */         
/* 619 */         String postMime = httpContentType.substring(i + 1);
/* 620 */         Matcher m = CHARSET_PATTERN.matcher(postMime);
/* 621 */         encoding = m.find() ? m.group(1) : null;
/* 622 */         encoding = (encoding != null) ? encoding.toUpperCase(Locale.ENGLISH) : null;
/*     */       } 
/*     */     } 
/* 625 */     return encoding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String getBOMEncoding(BufferedInputStream is) throws IOException {
/* 632 */     String encoding = null;
/* 633 */     int[] bytes = new int[3];
/* 634 */     is.mark(3);
/* 635 */     bytes[0] = is.read();
/* 636 */     bytes[1] = is.read();
/* 637 */     bytes[2] = is.read();
/*     */     
/* 639 */     if (bytes[0] == 254 && bytes[1] == 255) {
/*     */       
/* 641 */       encoding = "UTF-16BE";
/* 642 */       is.reset();
/* 643 */       is.read();
/* 644 */       is.read();
/*     */     }
/* 646 */     else if (bytes[0] == 255 && bytes[1] == 254) {
/*     */       
/* 648 */       encoding = "UTF-16LE";
/* 649 */       is.reset();
/* 650 */       is.read();
/* 651 */       is.read();
/*     */     }
/* 653 */     else if (bytes[0] == 239 && bytes[1] == 187 && bytes[2] == 191) {
/*     */       
/* 655 */       encoding = "UTF-8";
/*     */     }
/*     */     else {
/*     */       
/* 659 */       is.reset();
/*     */     } 
/* 661 */     return encoding;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static String getXMLGuessEncoding(BufferedInputStream is) throws IOException {
/* 667 */     String encoding = null;
/* 668 */     int[] bytes = new int[4];
/* 669 */     is.mark(4);
/* 670 */     bytes[0] = is.read();
/* 671 */     bytes[1] = is.read();
/* 672 */     bytes[2] = is.read();
/* 673 */     bytes[3] = is.read();
/* 674 */     is.reset();
/*     */     
/* 676 */     if (bytes[0] == 0 && bytes[1] == 60 && bytes[2] == 0 && bytes[3] == 63) {
/*     */       
/* 678 */       encoding = "UTF-16BE";
/*     */     }
/* 680 */     else if (bytes[0] == 60 && bytes[1] == 0 && bytes[2] == 63 && bytes[3] == 0) {
/*     */       
/* 682 */       encoding = "UTF-16LE";
/*     */     }
/* 684 */     else if (bytes[0] == 60 && bytes[1] == 63 && bytes[2] == 120 && bytes[3] == 109) {
/*     */       
/* 686 */       encoding = "UTF-8";
/*     */     }
/* 688 */     else if (bytes[0] == 76 && bytes[1] == 111 && bytes[2] == 167 && bytes[3] == 148) {
/*     */       
/* 690 */       encoding = "CP1047";
/*     */     } 
/* 692 */     return encoding;
/*     */   }
/*     */   
/* 695 */   static final Pattern ENCODING_PATTERN = Pattern.compile("<\\?xml.*encoding[\\s]*=[\\s]*((?:\".[^\"]*\")|(?:'.[^']*'))", 8);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String getXmlProlog(BufferedInputStream is, String guessedEnc) throws IOException {
/* 701 */     String encoding = null;
/* 702 */     if (guessedEnc != null) {
/*     */       
/* 704 */       byte[] bytes = new byte[4096];
/* 705 */       is.mark(4096);
/* 706 */       int offset = 0;
/* 707 */       int max = 4096;
/* 708 */       int c = is.read(bytes, offset, max);
/* 709 */       int firstGT = -1;
/* 710 */       String xmlProlog = null;
/* 711 */       while (c != -1 && firstGT == -1 && offset < 4096) {
/*     */         
/* 713 */         offset += c;
/* 714 */         max -= c;
/* 715 */         c = is.read(bytes, offset, max);
/* 716 */         xmlProlog = new String(bytes, 0, offset, guessedEnc);
/* 717 */         firstGT = xmlProlog.indexOf('>');
/*     */       } 
/* 719 */       if (firstGT == -1) {
/*     */         
/* 721 */         if (c == -1)
/*     */         {
/* 723 */           throw new IOException("Unexpected end of XML stream");
/*     */         }
/*     */ 
/*     */         
/* 727 */         throw new IOException("XML prolog or ROOT element not found on first " + offset + " bytes");
/*     */       } 
/*     */       
/* 730 */       int bytesRead = offset;
/* 731 */       if (bytesRead > 0) {
/*     */         
/* 733 */         is.reset();
/* 734 */         BufferedReader bReader = new BufferedReader(new StringReader(xmlProlog.substring(0, firstGT + 1)));
/* 735 */         StringBuffer prolog = new StringBuffer();
/* 736 */         String line = bReader.readLine();
/* 737 */         while (line != null) {
/*     */           
/* 739 */           prolog.append(line);
/* 740 */           line = bReader.readLine();
/*     */         } 
/* 742 */         Matcher m = ENCODING_PATTERN.matcher(prolog);
/* 743 */         if (m.find()) {
/*     */           
/* 745 */           encoding = m.group(1).toUpperCase(Locale.ENGLISH);
/* 746 */           encoding = encoding.substring(1, encoding.length() - 1);
/*     */         } 
/*     */       } 
/*     */     } 
/* 750 */     return encoding;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isAppXml(String mime) {
/* 756 */     return (mime != null && (mime.equals("application/xml") || mime.equals("application/xml-dtd") || mime.equals("application/xml-external-parsed-entity") || (mime.startsWith("application/") && mime.endsWith("+xml"))));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isTextXml(String mime) {
/* 764 */     return (mime != null && (mime.equals("text/xml") || mime.equals("text/xml-external-parsed-entity") || (mime.startsWith("text/") && mime.endsWith("+xml"))));
/*     */   }
/*     */ 
/*     */   
/* 768 */   private static final MessageFormat RAW_EX_1 = new MessageFormat("Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] encoding mismatch");
/*     */ 
/*     */   
/* 771 */   private static final MessageFormat RAW_EX_2 = new MessageFormat("Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] unknown BOM");
/*     */ 
/*     */   
/* 774 */   private static final MessageFormat HTTP_EX_1 = new MessageFormat("Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], BOM must be NULL");
/*     */ 
/*     */ 
/*     */   
/* 778 */   private static final MessageFormat HTTP_EX_2 = new MessageFormat("Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], encoding mismatch");
/*     */ 
/*     */ 
/*     */   
/* 782 */   private static final MessageFormat HTTP_EX_3 = new MessageFormat("Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], Invalid MIME");
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\xml\XmlReader.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */