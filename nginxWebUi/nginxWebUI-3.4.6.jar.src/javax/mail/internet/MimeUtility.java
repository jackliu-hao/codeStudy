/*      */ package javax.mail.internet;
/*      */ 
/*      */ import com.sun.mail.util.ASCIIUtility;
/*      */ import com.sun.mail.util.BASE64DecoderStream;
/*      */ import com.sun.mail.util.BASE64EncoderStream;
/*      */ import com.sun.mail.util.BEncoderStream;
/*      */ import com.sun.mail.util.LineInputStream;
/*      */ import com.sun.mail.util.PropUtil;
/*      */ import com.sun.mail.util.QDecoderStream;
/*      */ import com.sun.mail.util.QEncoderStream;
/*      */ import com.sun.mail.util.QPDecoderStream;
/*      */ import com.sun.mail.util.QPEncoderStream;
/*      */ import com.sun.mail.util.UUDecoderStream;
/*      */ import com.sun.mail.util.UUEncoderStream;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.OutputStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.util.HashMap;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.StringTokenizer;
/*      */ import javax.activation.DataHandler;
/*      */ import javax.activation.DataSource;
/*      */ import javax.mail.MessagingException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class MimeUtility
/*      */ {
/*      */   public static final int ALL = -1;
/*  150 */   private static final Map nonAsciiCharsetMap = new HashMap();
/*      */   
/*  152 */   private static final boolean decodeStrict = PropUtil.getBooleanSystemProperty("mail.mime.decodetext.strict", true);
/*      */   
/*  154 */   private static final boolean encodeEolStrict = PropUtil.getBooleanSystemProperty("mail.mime.encodeeol.strict", false);
/*      */   
/*  156 */   private static final boolean ignoreUnknownEncoding = PropUtil.getBooleanSystemProperty("mail.mime.ignoreunknownencoding", false);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  165 */   private static final boolean foldEncodedWords = PropUtil.getBooleanSystemProperty("mail.mime.foldencodedwords", false);
/*      */   
/*  167 */   private static final boolean foldText = PropUtil.getBooleanSystemProperty("mail.mime.foldtext", true);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String defaultJavaCharset;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String defaultMIMECharset;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static Hashtable mime2java;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getEncoding(DataSource ds) {
/*  195 */     ContentType cType = null;
/*  196 */     InputStream is = null;
/*  197 */     String encoding = null;
/*      */     
/*      */     try {
/*  200 */       cType = new ContentType(ds.getContentType());
/*  201 */       is = ds.getInputStream();
/*      */       
/*  203 */       boolean isText = cType.match("text/*");
/*      */       
/*  205 */       int i = checkAscii(is, -1, !isText);
/*  206 */       switch (i) {
/*      */         case 1:
/*  208 */           encoding = "7bit";
/*      */           break;
/*      */         case 2:
/*  211 */           if (isText && nonAsciiCharset(cType)) {
/*  212 */             encoding = "base64"; break;
/*      */           } 
/*  214 */           encoding = "quoted-printable";
/*      */           break;
/*      */         default:
/*  217 */           encoding = "base64";
/*      */           break;
/*      */       } 
/*      */     
/*  221 */     } catch (Exception ex) {
/*  222 */       return "base64";
/*      */     } finally {
/*      */       
/*      */       try {
/*  226 */         if (is != null)
/*  227 */           is.close(); 
/*  228 */       } catch (IOException ioex) {}
/*      */     } 
/*      */     
/*  231 */     return encoding;
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
/*      */   private static boolean nonAsciiCharset(ContentType ct) {
/*      */     Boolean bool;
/*  244 */     String charset = ct.getParameter("charset");
/*  245 */     if (charset == null)
/*  246 */       return false; 
/*  247 */     charset = charset.toLowerCase(Locale.ENGLISH);
/*      */     
/*  249 */     synchronized (nonAsciiCharsetMap) {
/*  250 */       bool = (Boolean)nonAsciiCharsetMap.get(charset);
/*      */     } 
/*  252 */     if (bool == null) {
/*      */       try {
/*  254 */         byte[] b = "\r\n".getBytes(charset);
/*  255 */         bool = Boolean.valueOf((b.length != 2 || b[0] != 13 || b[1] != 10));
/*      */       }
/*  257 */       catch (UnsupportedEncodingException uex) {
/*  258 */         bool = Boolean.FALSE;
/*  259 */       } catch (RuntimeException ex) {
/*  260 */         bool = Boolean.TRUE;
/*      */       } 
/*  262 */       synchronized (nonAsciiCharsetMap) {
/*  263 */         nonAsciiCharsetMap.put(charset, bool);
/*      */       } 
/*      */     } 
/*  266 */     return bool.booleanValue();
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
/*      */   public static String getEncoding(DataHandler dh) {
/*  283 */     ContentType cType = null;
/*  284 */     String encoding = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  299 */     if (dh.getName() != null) {
/*  300 */       return getEncoding(dh.getDataSource());
/*      */     }
/*      */     try {
/*  303 */       cType = new ContentType(dh.getContentType());
/*  304 */     } catch (Exception ex) {
/*  305 */       return "base64";
/*      */     } 
/*      */     
/*  308 */     if (cType.match("text/*"))
/*      */     
/*  310 */     { AsciiOutputStream aos = new AsciiOutputStream(false, false);
/*      */       try {
/*  312 */         dh.writeTo(aos);
/*  313 */       } catch (IOException ex) {}
/*      */ 
/*      */       
/*  316 */       switch (aos.getAscii())
/*      */       { case 1:
/*  318 */           encoding = "7bit";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  341 */           return encoding;case 2: encoding = "quoted-printable"; return encoding; }  encoding = "base64"; } else { AsciiOutputStream aos = new AsciiOutputStream(true, encodeEolStrict); try { dh.writeTo(aos); } catch (IOException ex) {} if (aos.getAscii() == 1) { encoding = "7bit"; } else { encoding = "base64"; }  }  return encoding;
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
/*      */   public static InputStream decode(InputStream is, String encoding) throws MessagingException {
/*  363 */     if (encoding.equalsIgnoreCase("base64"))
/*  364 */       return (InputStream)new BASE64DecoderStream(is); 
/*  365 */     if (encoding.equalsIgnoreCase("quoted-printable"))
/*  366 */       return (InputStream)new QPDecoderStream(is); 
/*  367 */     if (encoding.equalsIgnoreCase("uuencode") || encoding.equalsIgnoreCase("x-uuencode") || encoding.equalsIgnoreCase("x-uue"))
/*      */     {
/*      */       
/*  370 */       return (InputStream)new UUDecoderStream(is); } 
/*  371 */     if (encoding.equalsIgnoreCase("binary") || encoding.equalsIgnoreCase("7bit") || encoding.equalsIgnoreCase("8bit"))
/*      */     {
/*      */       
/*  374 */       return is;
/*      */     }
/*  376 */     if (!ignoreUnknownEncoding)
/*  377 */       throw new MessagingException("Unknown encoding: " + encoding); 
/*  378 */     return is;
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
/*      */   public static OutputStream encode(OutputStream os, String encoding) throws MessagingException {
/*  396 */     if (encoding == null)
/*  397 */       return os; 
/*  398 */     if (encoding.equalsIgnoreCase("base64"))
/*  399 */       return (OutputStream)new BASE64EncoderStream(os); 
/*  400 */     if (encoding.equalsIgnoreCase("quoted-printable"))
/*  401 */       return (OutputStream)new QPEncoderStream(os); 
/*  402 */     if (encoding.equalsIgnoreCase("uuencode") || encoding.equalsIgnoreCase("x-uuencode") || encoding.equalsIgnoreCase("x-uue"))
/*      */     {
/*      */       
/*  405 */       return (OutputStream)new UUEncoderStream(os); } 
/*  406 */     if (encoding.equalsIgnoreCase("binary") || encoding.equalsIgnoreCase("7bit") || encoding.equalsIgnoreCase("8bit"))
/*      */     {
/*      */       
/*  409 */       return os;
/*      */     }
/*  411 */     throw new MessagingException("Unknown encoding: " + encoding);
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
/*      */   public static OutputStream encode(OutputStream os, String encoding, String filename) throws MessagingException {
/*  433 */     if (encoding == null)
/*  434 */       return os; 
/*  435 */     if (encoding.equalsIgnoreCase("base64"))
/*  436 */       return (OutputStream)new BASE64EncoderStream(os); 
/*  437 */     if (encoding.equalsIgnoreCase("quoted-printable"))
/*  438 */       return (OutputStream)new QPEncoderStream(os); 
/*  439 */     if (encoding.equalsIgnoreCase("uuencode") || encoding.equalsIgnoreCase("x-uuencode") || encoding.equalsIgnoreCase("x-uue"))
/*      */     {
/*      */       
/*  442 */       return (OutputStream)new UUEncoderStream(os, filename); } 
/*  443 */     if (encoding.equalsIgnoreCase("binary") || encoding.equalsIgnoreCase("7bit") || encoding.equalsIgnoreCase("8bit"))
/*      */     {
/*      */       
/*  446 */       return os;
/*      */     }
/*  448 */     throw new MessagingException("Unknown encoding: " + encoding);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String encodeText(String text) throws UnsupportedEncodingException {
/*  489 */     return encodeText(text, null, null);
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
/*      */   public static String encodeText(String text, String charset, String encoding) throws UnsupportedEncodingException {
/*  520 */     return encodeWord(text, charset, encoding, false);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String decodeText(String etext) throws UnsupportedEncodingException {
/*  562 */     String lwsp = " \t\n\r";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  574 */     if (etext.indexOf("=?") == -1) {
/*  575 */       return etext;
/*      */     }
/*      */ 
/*      */     
/*  579 */     StringTokenizer st = new StringTokenizer(etext, lwsp, true);
/*  580 */     StringBuffer sb = new StringBuffer();
/*  581 */     StringBuffer wsb = new StringBuffer();
/*  582 */     boolean prevWasEncoded = false;
/*      */     
/*  584 */     while (st.hasMoreTokens()) {
/*      */       
/*  586 */       String str1, s = st.nextToken();
/*      */       char c;
/*  588 */       if ((c = s.charAt(0)) == ' ' || c == '\t' || c == '\r' || c == '\n') {
/*      */         
/*  590 */         wsb.append(c);
/*      */         
/*      */         continue;
/*      */       } 
/*      */       try {
/*  595 */         str1 = decodeWord(s);
/*      */         
/*  597 */         if (!prevWasEncoded && wsb.length() > 0)
/*      */         {
/*      */ 
/*      */           
/*  601 */           sb.append(wsb);
/*      */         }
/*  603 */         prevWasEncoded = true;
/*  604 */       } catch (ParseException pex) {
/*      */         
/*  606 */         str1 = s;
/*      */         
/*  608 */         if (!decodeStrict) {
/*  609 */           String dword = decodeInnerWords(str1);
/*  610 */           if (dword != str1) {
/*      */ 
/*      */             
/*  613 */             if (!prevWasEncoded || !str1.startsWith("=?"))
/*      */             {
/*      */ 
/*      */ 
/*      */               
/*  618 */               if (wsb.length() > 0) {
/*  619 */                 sb.append(wsb);
/*      */               }
/*      */             }
/*  622 */             prevWasEncoded = str1.endsWith("?=");
/*  623 */             str1 = dword;
/*      */           } else {
/*      */             
/*  626 */             if (wsb.length() > 0)
/*  627 */               sb.append(wsb); 
/*  628 */             prevWasEncoded = false;
/*      */           } 
/*      */         } else {
/*      */           
/*  632 */           if (wsb.length() > 0)
/*  633 */             sb.append(wsb); 
/*  634 */           prevWasEncoded = false;
/*      */         } 
/*      */       } 
/*  637 */       sb.append(str1);
/*  638 */       wsb.setLength(0);
/*      */     } 
/*      */     
/*  641 */     sb.append(wsb);
/*  642 */     return sb.toString();
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
/*      */   public static String encodeWord(String word) throws UnsupportedEncodingException {
/*  668 */     return encodeWord(word, null, null);
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
/*      */   public static String encodeWord(String word, String charset, String encoding) throws UnsupportedEncodingException {
/*  696 */     return encodeWord(word, charset, encoding, true);
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
/*      */   private static String encodeWord(String string, String charset, String encoding, boolean encodingWord) throws UnsupportedEncodingException {
/*      */     String jcharset;
/*      */     boolean b64;
/*  712 */     int ascii = checkAscii(string);
/*  713 */     if (ascii == 1) {
/*  714 */       return string;
/*      */     }
/*      */ 
/*      */     
/*  718 */     if (charset == null) {
/*  719 */       jcharset = getDefaultJavaCharset();
/*  720 */       charset = getDefaultMIMECharset();
/*      */     } else {
/*  722 */       jcharset = javaCharset(charset);
/*      */     } 
/*      */     
/*  725 */     if (encoding == null) {
/*  726 */       if (ascii != 3) {
/*  727 */         encoding = "Q";
/*      */       } else {
/*  729 */         encoding = "B";
/*      */       } 
/*      */     }
/*      */     
/*  733 */     if (encoding.equalsIgnoreCase("B")) {
/*  734 */       b64 = true;
/*  735 */     } else if (encoding.equalsIgnoreCase("Q")) {
/*  736 */       b64 = false;
/*      */     } else {
/*  738 */       throw new UnsupportedEncodingException("Unknown transfer encoding: " + encoding);
/*      */     } 
/*      */     
/*  741 */     StringBuffer outb = new StringBuffer();
/*  742 */     doEncode(string, b64, jcharset, 68 - charset.length(), "=?" + charset + "?" + encoding + "?", true, encodingWord, outb);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  750 */     return outb.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void doEncode(String string, boolean b64, String jcharset, int avail, String prefix, boolean first, boolean encodingWord, StringBuffer buf) throws UnsupportedEncodingException {
/*      */     int len;
/*  760 */     byte[] bytes = string.getBytes(jcharset);
/*      */     
/*  762 */     if (b64) {
/*  763 */       len = BEncoderStream.encodedLength(bytes);
/*      */     } else {
/*  765 */       len = QEncoderStream.encodedLength(bytes, encodingWord);
/*      */     } 
/*      */     int size;
/*  768 */     if (len > avail && (size = string.length()) > 1) {
/*      */ 
/*      */       
/*  771 */       doEncode(string.substring(0, size / 2), b64, jcharset, avail, prefix, first, encodingWord, buf);
/*      */       
/*  773 */       doEncode(string.substring(size / 2, size), b64, jcharset, avail, prefix, false, encodingWord, buf);
/*      */     } else {
/*      */       QEncoderStream qEncoderStream;
/*      */       
/*  777 */       ByteArrayOutputStream os = new ByteArrayOutputStream();
/*      */       
/*  779 */       if (b64) {
/*  780 */         BEncoderStream bEncoderStream = new BEncoderStream(os);
/*      */       } else {
/*  782 */         qEncoderStream = new QEncoderStream(os, encodingWord);
/*      */       } 
/*      */       try {
/*  785 */         qEncoderStream.write(bytes);
/*  786 */         qEncoderStream.close();
/*  787 */       } catch (IOException ioex) {}
/*      */       
/*  789 */       byte[] encodedBytes = os.toByteArray();
/*      */ 
/*      */       
/*  792 */       if (!first)
/*  793 */         if (foldEncodedWords) {
/*  794 */           buf.append("\r\n ");
/*      */         } else {
/*  796 */           buf.append(" ");
/*      */         }  
/*  798 */       buf.append(prefix);
/*  799 */       for (int i = 0; i < encodedBytes.length; i++)
/*  800 */         buf.append((char)encodedBytes[i]); 
/*  801 */       buf.append("?=");
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
/*      */   public static String decodeWord(String eword) throws ParseException, UnsupportedEncodingException {
/*  821 */     if (!eword.startsWith("=?")) {
/*  822 */       throw new ParseException("encoded word does not start with \"=?\": " + eword);
/*      */     }
/*      */ 
/*      */     
/*  826 */     int start = 2; int pos;
/*  827 */     if ((pos = eword.indexOf('?', start)) == -1) {
/*  828 */       throw new ParseException("encoded word does not include charset: " + eword);
/*      */     }
/*  830 */     String charset = eword.substring(start, pos);
/*  831 */     int lpos = charset.indexOf('*');
/*  832 */     if (lpos >= 0)
/*  833 */       charset = charset.substring(0, lpos); 
/*  834 */     charset = javaCharset(charset);
/*      */ 
/*      */     
/*  837 */     start = pos + 1;
/*  838 */     if ((pos = eword.indexOf('?', start)) == -1) {
/*  839 */       throw new ParseException("encoded word does not include encoding: " + eword);
/*      */     }
/*  841 */     String encoding = eword.substring(start, pos);
/*      */ 
/*      */     
/*  844 */     start = pos + 1;
/*  845 */     if ((pos = eword.indexOf("?=", start)) == -1) {
/*  846 */       throw new ParseException("encoded word does not end with \"?=\": " + eword);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  855 */     String word = eword.substring(start, pos);
/*      */     
/*      */     try {
/*      */       String decodedWord;
/*  859 */       if (word.length() > 0) {
/*      */         QDecoderStream qDecoderStream;
/*  861 */         ByteArrayInputStream bis = new ByteArrayInputStream(ASCIIUtility.getBytes(word));
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  866 */         if (encoding.equalsIgnoreCase("B")) {
/*  867 */           BASE64DecoderStream bASE64DecoderStream = new BASE64DecoderStream(bis);
/*  868 */         } else if (encoding.equalsIgnoreCase("Q")) {
/*  869 */           qDecoderStream = new QDecoderStream(bis);
/*      */         } else {
/*  871 */           throw new UnsupportedEncodingException("unknown encoding: " + encoding);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  879 */         int count = bis.available();
/*  880 */         byte[] bytes = new byte[count];
/*      */         
/*  882 */         count = qDecoderStream.read(bytes, 0, count);
/*      */ 
/*      */ 
/*      */         
/*  886 */         decodedWord = (count <= 0) ? "" : new String(bytes, 0, count, charset);
/*      */       }
/*      */       else {
/*      */         
/*  890 */         decodedWord = "";
/*      */       } 
/*  892 */       if (pos + 2 < eword.length()) {
/*      */         
/*  894 */         String rest = eword.substring(pos + 2);
/*  895 */         if (!decodeStrict)
/*  896 */           rest = decodeInnerWords(rest); 
/*  897 */         decodedWord = decodedWord + rest;
/*      */       } 
/*  899 */       return decodedWord;
/*  900 */     } catch (UnsupportedEncodingException uex) {
/*      */ 
/*      */       
/*  903 */       throw uex;
/*  904 */     } catch (IOException ioex) {
/*      */       
/*  906 */       throw new ParseException(ioex.toString());
/*  907 */     } catch (IllegalArgumentException iex) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  915 */       throw new UnsupportedEncodingException(charset);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String decodeInnerWords(String word) throws UnsupportedEncodingException {
/*  926 */     int start = 0;
/*  927 */     StringBuffer buf = new StringBuffer(); int i;
/*  928 */     while ((i = word.indexOf("=?", start)) >= 0) {
/*  929 */       buf.append(word.substring(start, i));
/*      */       
/*  931 */       int end = word.indexOf('?', i + 2);
/*  932 */       if (end < 0) {
/*      */         break;
/*      */       }
/*  935 */       end = word.indexOf('?', end + 1);
/*  936 */       if (end < 0) {
/*      */         break;
/*      */       }
/*  939 */       end = word.indexOf("?=", end + 1);
/*  940 */       if (end < 0)
/*      */         break; 
/*  942 */       String s = word.substring(i, end + 2);
/*      */       try {
/*  944 */         s = decodeWord(s);
/*  945 */       } catch (ParseException pex) {}
/*      */ 
/*      */       
/*  948 */       buf.append(s);
/*  949 */       start = end + 2;
/*      */     } 
/*  951 */     if (start == 0)
/*  952 */       return word; 
/*  953 */     if (start < word.length())
/*  954 */       buf.append(word.substring(start)); 
/*  955 */     return buf.toString();
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
/*      */   public static String quote(String word, String specials) {
/*  975 */     int len = word.length();
/*  976 */     if (len == 0) {
/*  977 */       return "\"\"";
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  983 */     boolean needQuoting = false;
/*  984 */     for (int i = 0; i < len; i++) {
/*  985 */       char c = word.charAt(i);
/*  986 */       if (c == '"' || c == '\\' || c == '\r' || c == '\n') {
/*      */         
/*  988 */         StringBuffer sb = new StringBuffer(len + 3);
/*  989 */         sb.append('"');
/*  990 */         sb.append(word.substring(0, i));
/*  991 */         int lastc = 0;
/*  992 */         for (int j = i; j < len; j++) {
/*  993 */           char cc = word.charAt(j);
/*  994 */           if (cc == '"' || cc == '\\' || cc == '\r' || cc == '\n')
/*      */           {
/*  996 */             if (cc != '\n' || lastc != 13)
/*      */             {
/*      */               
/*  999 */               sb.append('\\'); }  } 
/* 1000 */           sb.append(cc);
/* 1001 */           lastc = cc;
/*      */         } 
/* 1003 */         sb.append('"');
/* 1004 */         return sb.toString();
/* 1005 */       }  if (c < ' ' || c >= '' || specials.indexOf(c) >= 0)
/*      */       {
/* 1007 */         needQuoting = true;
/*      */       }
/*      */     } 
/* 1010 */     if (needQuoting) {
/* 1011 */       StringBuffer sb = new StringBuffer(len + 2);
/* 1012 */       sb.append('"').append(word).append('"');
/* 1013 */       return sb.toString();
/*      */     } 
/* 1015 */     return word;
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
/*      */   public static String fold(int used, String s) {
/* 1035 */     if (!foldText) {
/* 1036 */       return s;
/*      */     }
/*      */     
/*      */     int end;
/*      */     
/* 1041 */     for (end = s.length() - 1; end >= 0; end--) {
/* 1042 */       char c = s.charAt(end);
/* 1043 */       if (c != ' ' && c != '\t' && c != '\r' && c != '\n')
/*      */         break; 
/*      */     } 
/* 1046 */     if (end != s.length() - 1) {
/* 1047 */       s = s.substring(0, end + 1);
/*      */     }
/*      */     
/* 1050 */     if (used + s.length() <= 76) {
/* 1051 */       return s;
/*      */     }
/*      */     
/* 1054 */     StringBuffer sb = new StringBuffer(s.length() + 4);
/* 1055 */     char lastc = Character.MIN_VALUE;
/* 1056 */     while (used + s.length() > 76) {
/* 1057 */       int lastspace = -1;
/* 1058 */       for (int i = 0; i < s.length() && (
/* 1059 */         lastspace == -1 || used + i <= 76); i++) {
/*      */         
/* 1061 */         char c = s.charAt(i);
/* 1062 */         if ((c == ' ' || c == '\t') && 
/* 1063 */           lastc != ' ' && lastc != '\t')
/* 1064 */           lastspace = i; 
/* 1065 */         lastc = c;
/*      */       } 
/* 1067 */       if (lastspace == -1) {
/*      */         
/* 1069 */         sb.append(s);
/* 1070 */         s = "";
/* 1071 */         used = 0;
/*      */         break;
/*      */       } 
/* 1074 */       sb.append(s.substring(0, lastspace));
/* 1075 */       sb.append("\r\n");
/* 1076 */       lastc = s.charAt(lastspace);
/* 1077 */       sb.append(lastc);
/* 1078 */       s = s.substring(lastspace + 1);
/* 1079 */       used = 1;
/*      */     } 
/* 1081 */     sb.append(s);
/* 1082 */     return sb.toString();
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
/*      */   public static String unfold(String s) {
/* 1094 */     if (!foldText) {
/* 1095 */       return s;
/*      */     }
/* 1097 */     StringBuffer sb = null;
/*      */     int i;
/* 1099 */     while ((i = indexOfAny(s, "\r\n")) >= 0) {
/* 1100 */       int start = i;
/* 1101 */       int l = s.length();
/* 1102 */       i++;
/* 1103 */       if (i < l && s.charAt(i - 1) == '\r' && s.charAt(i) == '\n')
/* 1104 */         i++; 
/* 1105 */       if (start == 0 || s.charAt(start - 1) != '\\') {
/*      */         char c;
/*      */ 
/*      */         
/* 1109 */         if (i < l && ((c = s.charAt(i)) == ' ' || c == '\t')) {
/* 1110 */           i++;
/* 1111 */           while (i < l && ((c = s.charAt(i)) == ' ' || c == '\t'))
/* 1112 */             i++; 
/* 1113 */           if (sb == null)
/* 1114 */             sb = new StringBuffer(s.length()); 
/* 1115 */           if (start != 0) {
/* 1116 */             sb.append(s.substring(0, start));
/* 1117 */             sb.append(' ');
/*      */           } 
/* 1119 */           s = s.substring(i);
/*      */           
/*      */           continue;
/*      */         } 
/* 1123 */         if (sb == null)
/* 1124 */           sb = new StringBuffer(s.length()); 
/* 1125 */         sb.append(s.substring(0, i));
/* 1126 */         s = s.substring(i);
/*      */         
/*      */         continue;
/*      */       } 
/* 1130 */       if (sb == null)
/* 1131 */         sb = new StringBuffer(s.length()); 
/* 1132 */       sb.append(s.substring(0, start - 1));
/* 1133 */       sb.append(s.substring(start, i));
/* 1134 */       s = s.substring(i);
/*      */     } 
/*      */     
/* 1137 */     if (sb != null) {
/* 1138 */       sb.append(s);
/* 1139 */       return sb.toString();
/*      */     } 
/* 1141 */     return s;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int indexOfAny(String s, String any) {
/* 1151 */     return indexOfAny(s, any, 0);
/*      */   }
/*      */   
/*      */   private static int indexOfAny(String s, String any, int start) {
/*      */     try {
/* 1156 */       int len = s.length();
/* 1157 */       for (int i = start; i < len; i++) {
/* 1158 */         if (any.indexOf(s.charAt(i)) >= 0)
/* 1159 */           return i; 
/*      */       } 
/* 1161 */       return -1;
/* 1162 */     } catch (StringIndexOutOfBoundsException e) {
/* 1163 */       return -1;
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
/*      */   public static String javaCharset(String charset) {
/* 1175 */     if (mime2java == null || charset == null)
/*      */     {
/* 1177 */       return charset;
/*      */     }
/* 1179 */     String alias = (String)mime2java.get(charset.toLowerCase(Locale.ENGLISH));
/*      */     
/* 1181 */     return (alias == null) ? charset : alias;
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
/*      */   public static String mimeCharset(String charset) {
/* 1198 */     if (java2mime == null || charset == null)
/*      */     {
/* 1200 */       return charset;
/*      */     }
/* 1202 */     String alias = (String)java2mime.get(charset.toLowerCase(Locale.ENGLISH));
/*      */     
/* 1204 */     return (alias == null) ? charset : alias;
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
/*      */   public static String getDefaultJavaCharset() {
/* 1221 */     if (defaultJavaCharset == null) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1226 */       String mimecs = null;
/*      */       try {
/* 1228 */         mimecs = System.getProperty("mail.mime.charset");
/* 1229 */       } catch (SecurityException ex) {}
/* 1230 */       if (mimecs != null && mimecs.length() > 0) {
/* 1231 */         defaultJavaCharset = javaCharset(mimecs);
/* 1232 */         return defaultJavaCharset;
/*      */       } 
/*      */       
/*      */       try {
/* 1236 */         defaultJavaCharset = System.getProperty("file.encoding", "8859_1");
/*      */       }
/* 1238 */       catch (SecurityException sex) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1245 */         InputStreamReader reader = new InputStreamReader(new NullInputStream());
/*      */         
/* 1247 */         defaultJavaCharset = reader.getEncoding();
/* 1248 */         if (defaultJavaCharset == null)
/* 1249 */           defaultJavaCharset = "8859_1"; 
/*      */       } 
/*      */     }  class NullInputStream extends InputStream { public int read() {
/*      */         return 0;
/* 1253 */       } }; return defaultJavaCharset;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static String getDefaultMIMECharset() {
/* 1260 */     if (defaultMIMECharset == null) {
/*      */       try {
/* 1262 */         defaultMIMECharset = System.getProperty("mail.mime.charset");
/* 1263 */       } catch (SecurityException ex) {}
/*      */     }
/* 1265 */     if (defaultMIMECharset == null)
/* 1266 */       defaultMIMECharset = mimeCharset(getDefaultJavaCharset()); 
/* 1267 */     return defaultMIMECharset;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1276 */   private static Hashtable java2mime = new Hashtable(40); static final int ALL_ASCII = 1; static final int MOSTLY_ASCII = 2; static final int MOSTLY_NONASCII = 3; static {
/* 1277 */     mime2java = new Hashtable(10);
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 1282 */       InputStream is = MimeUtility.class.getResourceAsStream("/META-INF/javamail.charset.map");
/*      */ 
/*      */ 
/*      */       
/* 1286 */       if (is != null) {
/*      */         LineInputStream lineInputStream; try {
/* 1288 */           lineInputStream = new LineInputStream(is);
/*      */ 
/*      */           
/* 1291 */           loadMappings(lineInputStream, java2mime);
/*      */ 
/*      */           
/* 1294 */           loadMappings(lineInputStream, mime2java);
/*      */         } finally {
/*      */           try {
/* 1297 */             lineInputStream.close();
/* 1298 */           } catch (Exception cex) {}
/*      */         }
/*      */       
/*      */       }
/*      */     
/* 1303 */     } catch (Exception ex) {}
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1308 */     if (java2mime.isEmpty()) {
/* 1309 */       java2mime.put("8859_1", "ISO-8859-1");
/* 1310 */       java2mime.put("iso8859_1", "ISO-8859-1");
/* 1311 */       java2mime.put("iso8859-1", "ISO-8859-1");
/*      */       
/* 1313 */       java2mime.put("8859_2", "ISO-8859-2");
/* 1314 */       java2mime.put("iso8859_2", "ISO-8859-2");
/* 1315 */       java2mime.put("iso8859-2", "ISO-8859-2");
/*      */       
/* 1317 */       java2mime.put("8859_3", "ISO-8859-3");
/* 1318 */       java2mime.put("iso8859_3", "ISO-8859-3");
/* 1319 */       java2mime.put("iso8859-3", "ISO-8859-3");
/*      */       
/* 1321 */       java2mime.put("8859_4", "ISO-8859-4");
/* 1322 */       java2mime.put("iso8859_4", "ISO-8859-4");
/* 1323 */       java2mime.put("iso8859-4", "ISO-8859-4");
/*      */       
/* 1325 */       java2mime.put("8859_5", "ISO-8859-5");
/* 1326 */       java2mime.put("iso8859_5", "ISO-8859-5");
/* 1327 */       java2mime.put("iso8859-5", "ISO-8859-5");
/*      */       
/* 1329 */       java2mime.put("8859_6", "ISO-8859-6");
/* 1330 */       java2mime.put("iso8859_6", "ISO-8859-6");
/* 1331 */       java2mime.put("iso8859-6", "ISO-8859-6");
/*      */       
/* 1333 */       java2mime.put("8859_7", "ISO-8859-7");
/* 1334 */       java2mime.put("iso8859_7", "ISO-8859-7");
/* 1335 */       java2mime.put("iso8859-7", "ISO-8859-7");
/*      */       
/* 1337 */       java2mime.put("8859_8", "ISO-8859-8");
/* 1338 */       java2mime.put("iso8859_8", "ISO-8859-8");
/* 1339 */       java2mime.put("iso8859-8", "ISO-8859-8");
/*      */       
/* 1341 */       java2mime.put("8859_9", "ISO-8859-9");
/* 1342 */       java2mime.put("iso8859_9", "ISO-8859-9");
/* 1343 */       java2mime.put("iso8859-9", "ISO-8859-9");
/*      */       
/* 1345 */       java2mime.put("sjis", "Shift_JIS");
/* 1346 */       java2mime.put("jis", "ISO-2022-JP");
/* 1347 */       java2mime.put("iso2022jp", "ISO-2022-JP");
/* 1348 */       java2mime.put("euc_jp", "euc-jp");
/* 1349 */       java2mime.put("koi8_r", "koi8-r");
/* 1350 */       java2mime.put("euc_cn", "euc-cn");
/* 1351 */       java2mime.put("euc_tw", "euc-tw");
/* 1352 */       java2mime.put("euc_kr", "euc-kr");
/*      */     } 
/* 1354 */     if (mime2java.isEmpty()) {
/* 1355 */       mime2java.put("iso-2022-cn", "ISO2022CN");
/* 1356 */       mime2java.put("iso-2022-kr", "ISO2022KR");
/* 1357 */       mime2java.put("utf-8", "UTF8");
/* 1358 */       mime2java.put("utf8", "UTF8");
/* 1359 */       mime2java.put("ja_jp.iso2022-7", "ISO2022JP");
/* 1360 */       mime2java.put("ja_jp.eucjp", "EUCJIS");
/* 1361 */       mime2java.put("euc-kr", "KSC5601");
/* 1362 */       mime2java.put("euckr", "KSC5601");
/* 1363 */       mime2java.put("us-ascii", "ISO-8859-1");
/* 1364 */       mime2java.put("x-us-ascii", "ISO-8859-1");
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private static void loadMappings(LineInputStream is, Hashtable table) {
/*      */     while (true) {
/*      */       String currLine;
/*      */       try {
/* 1373 */         currLine = is.readLine();
/* 1374 */       } catch (IOException ioex) {
/*      */         break;
/*      */       } 
/*      */       
/* 1378 */       if (currLine == null)
/*      */         break; 
/* 1380 */       if (currLine.startsWith("--") && currLine.endsWith("--")) {
/*      */         break;
/*      */       }
/*      */ 
/*      */       
/* 1385 */       if (currLine.trim().length() == 0 || currLine.startsWith("#")) {
/*      */         continue;
/*      */       }
/*      */ 
/*      */       
/* 1390 */       StringTokenizer tk = new StringTokenizer(currLine, " \t");
/*      */       try {
/* 1392 */         String key = tk.nextToken();
/* 1393 */         String value = tk.nextToken();
/* 1394 */         table.put(key.toLowerCase(Locale.ENGLISH), value);
/* 1395 */       } catch (NoSuchElementException nex) {}
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
/*      */   static int checkAscii(String s) {
/* 1412 */     int ascii = 0, non_ascii = 0;
/* 1413 */     int l = s.length();
/*      */     
/* 1415 */     for (int i = 0; i < l; i++) {
/* 1416 */       if (nonascii(s.charAt(i))) {
/* 1417 */         non_ascii++;
/*      */       } else {
/* 1419 */         ascii++;
/*      */       } 
/*      */     } 
/* 1422 */     if (non_ascii == 0)
/* 1423 */       return 1; 
/* 1424 */     if (ascii > non_ascii) {
/* 1425 */       return 2;
/*      */     }
/* 1427 */     return 3;
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
/*      */   static int checkAscii(byte[] b) {
/* 1441 */     int ascii = 0, non_ascii = 0;
/*      */     
/* 1443 */     for (int i = 0; i < b.length; i++) {
/*      */ 
/*      */ 
/*      */       
/* 1447 */       if (nonascii(b[i] & 0xFF)) {
/* 1448 */         non_ascii++;
/*      */       } else {
/* 1450 */         ascii++;
/*      */       } 
/*      */     } 
/* 1453 */     if (non_ascii == 0)
/* 1454 */       return 1; 
/* 1455 */     if (ascii > non_ascii) {
/* 1456 */       return 2;
/*      */     }
/* 1458 */     return 3;
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
/*      */   static int checkAscii(InputStream is, int max, boolean breakOnNonAscii) {
/* 1483 */     int ascii = 0, non_ascii = 0;
/*      */     
/* 1485 */     int block = 4096;
/* 1486 */     int linelen = 0;
/* 1487 */     boolean longLine = false, badEOL = false;
/* 1488 */     boolean checkEOL = (encodeEolStrict && breakOnNonAscii);
/* 1489 */     byte[] buf = null;
/* 1490 */     if (max != 0) {
/* 1491 */       block = (max == -1) ? 4096 : Math.min(max, 4096);
/* 1492 */       buf = new byte[block];
/*      */     } 
/* 1494 */     while (max != 0) {
/*      */       int len; try {
/* 1496 */         if ((len = is.read(buf, 0, block)) == -1)
/*      */           break; 
/* 1498 */         int lastb = 0;
/* 1499 */         for (int i = 0; i < len; i++) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1504 */           int b = buf[i] & 0xFF;
/* 1505 */           if (checkEOL && ((lastb == 13 && b != 10) || (lastb != 13 && b == 10)))
/*      */           {
/*      */             
/* 1508 */             badEOL = true; } 
/* 1509 */           if (b == 13 || b == 10) {
/* 1510 */             linelen = 0;
/*      */           } else {
/* 1512 */             linelen++;
/* 1513 */             if (linelen > 998)
/* 1514 */               longLine = true; 
/*      */           } 
/* 1516 */           if (nonascii(b)) {
/* 1517 */             if (breakOnNonAscii) {
/* 1518 */               return 3;
/*      */             }
/* 1520 */             non_ascii++;
/*      */           } else {
/* 1522 */             ascii++;
/* 1523 */           }  lastb = b;
/*      */         } 
/* 1525 */       } catch (IOException ioex) {
/*      */         break;
/*      */       } 
/* 1528 */       if (max != -1) {
/* 1529 */         max -= len;
/*      */       }
/*      */     } 
/* 1532 */     if (max == 0 && breakOnNonAscii)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1539 */       return 3;
/*      */     }
/* 1541 */     if (non_ascii == 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1546 */       if (badEOL) {
/* 1547 */         return 3;
/*      */       }
/* 1549 */       if (longLine) {
/* 1550 */         return 2;
/*      */       }
/* 1552 */       return 1;
/*      */     } 
/* 1554 */     if (ascii > non_ascii)
/* 1555 */       return 2; 
/* 1556 */     return 3;
/*      */   }
/*      */   
/*      */   static final boolean nonascii(int b) {
/* 1560 */     return (b >= 127 || (b < 32 && b != 13 && b != 10 && b != 9));
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\internet\MimeUtility.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */