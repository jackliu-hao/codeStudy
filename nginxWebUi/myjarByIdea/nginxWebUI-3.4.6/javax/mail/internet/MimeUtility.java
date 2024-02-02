package javax.mail.internet;

import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;
import com.sun.mail.util.BEncoderStream;
import com.sun.mail.util.LineInputStream;
import com.sun.mail.util.PropUtil;
import com.sun.mail.util.QDecoderStream;
import com.sun.mail.util.QEncoderStream;
import com.sun.mail.util.QPDecoderStream;
import com.sun.mail.util.QPEncoderStream;
import com.sun.mail.util.UUDecoderStream;
import com.sun.mail.util.UUEncoderStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;

public class MimeUtility {
   public static final int ALL = -1;
   private static final Map nonAsciiCharsetMap = new HashMap();
   private static final boolean decodeStrict = PropUtil.getBooleanSystemProperty("mail.mime.decodetext.strict", true);
   private static final boolean encodeEolStrict = PropUtil.getBooleanSystemProperty("mail.mime.encodeeol.strict", false);
   private static final boolean ignoreUnknownEncoding = PropUtil.getBooleanSystemProperty("mail.mime.ignoreunknownencoding", false);
   private static final boolean foldEncodedWords = PropUtil.getBooleanSystemProperty("mail.mime.foldencodedwords", false);
   private static final boolean foldText = PropUtil.getBooleanSystemProperty("mail.mime.foldtext", true);
   private static String defaultJavaCharset;
   private static String defaultMIMECharset;
   private static Hashtable mime2java = new Hashtable(10);
   private static Hashtable java2mime = new Hashtable(40);
   static final int ALL_ASCII = 1;
   static final int MOSTLY_ASCII = 2;
   static final int MOSTLY_NONASCII = 3;

   private MimeUtility() {
   }

   public static String getEncoding(DataSource ds) {
      ContentType cType = null;
      InputStream is = null;
      String encoding = null;

      String var5;
      try {
         cType = new ContentType(ds.getContentType());
         is = ds.getInputStream();
         boolean isText = cType.match("text/*");
         int i = checkAscii(is, -1, !isText);
         switch (i) {
            case 1:
               encoding = "7bit";
               return encoding;
            case 2:
               if (isText && nonAsciiCharset(cType)) {
                  encoding = "base64";
                  return encoding;
               }

               encoding = "quoted-printable";
               return encoding;
            default:
               encoding = "base64";
               return encoding;
         }
      } catch (Exception var15) {
         var5 = "base64";
      } finally {
         try {
            if (is != null) {
               is.close();
            }
         } catch (IOException var14) {
         }

      }

      return var5;
   }

   private static boolean nonAsciiCharset(ContentType ct) {
      String charset = ct.getParameter("charset");
      if (charset == null) {
         return false;
      } else {
         charset = charset.toLowerCase(Locale.ENGLISH);
         Boolean bool;
         synchronized(nonAsciiCharsetMap) {
            bool = (Boolean)nonAsciiCharsetMap.get(charset);
         }

         if (bool == null) {
            try {
               byte[] b = "\r\n".getBytes(charset);
               bool = b.length != 2 || b[0] != 13 || b[1] != 10;
            } catch (UnsupportedEncodingException var8) {
               bool = Boolean.FALSE;
            } catch (RuntimeException var9) {
               bool = Boolean.TRUE;
            }

            synchronized(nonAsciiCharsetMap) {
               nonAsciiCharsetMap.put(charset, bool);
            }
         }

         return bool;
      }
   }

   public static String getEncoding(DataHandler dh) {
      ContentType cType = null;
      String encoding = null;
      if (dh.getName() != null) {
         return getEncoding(dh.getDataSource());
      } else {
         try {
            cType = new ContentType(dh.getContentType());
         } catch (Exception var7) {
            return "base64";
         }

         AsciiOutputStream aos;
         if (cType.match("text/*")) {
            aos = new AsciiOutputStream(false, false);

            try {
               dh.writeTo(aos);
            } catch (IOException var6) {
            }

            switch (aos.getAscii()) {
               case 1:
                  encoding = "7bit";
                  break;
               case 2:
                  encoding = "quoted-printable";
                  break;
               default:
                  encoding = "base64";
            }
         } else {
            aos = new AsciiOutputStream(true, encodeEolStrict);

            try {
               dh.writeTo(aos);
            } catch (IOException var5) {
            }

            if (aos.getAscii() == 1) {
               encoding = "7bit";
            } else {
               encoding = "base64";
            }
         }

         return encoding;
      }
   }

   public static InputStream decode(InputStream is, String encoding) throws MessagingException {
      if (encoding.equalsIgnoreCase("base64")) {
         return new BASE64DecoderStream(is);
      } else if (encoding.equalsIgnoreCase("quoted-printable")) {
         return new QPDecoderStream(is);
      } else if (!encoding.equalsIgnoreCase("uuencode") && !encoding.equalsIgnoreCase("x-uuencode") && !encoding.equalsIgnoreCase("x-uue")) {
         if (!encoding.equalsIgnoreCase("binary") && !encoding.equalsIgnoreCase("7bit") && !encoding.equalsIgnoreCase("8bit")) {
            if (!ignoreUnknownEncoding) {
               throw new MessagingException("Unknown encoding: " + encoding);
            } else {
               return is;
            }
         } else {
            return is;
         }
      } else {
         return new UUDecoderStream(is);
      }
   }

   public static OutputStream encode(OutputStream os, String encoding) throws MessagingException {
      if (encoding == null) {
         return os;
      } else if (encoding.equalsIgnoreCase("base64")) {
         return new BASE64EncoderStream(os);
      } else if (encoding.equalsIgnoreCase("quoted-printable")) {
         return new QPEncoderStream(os);
      } else if (!encoding.equalsIgnoreCase("uuencode") && !encoding.equalsIgnoreCase("x-uuencode") && !encoding.equalsIgnoreCase("x-uue")) {
         if (!encoding.equalsIgnoreCase("binary") && !encoding.equalsIgnoreCase("7bit") && !encoding.equalsIgnoreCase("8bit")) {
            throw new MessagingException("Unknown encoding: " + encoding);
         } else {
            return os;
         }
      } else {
         return new UUEncoderStream(os);
      }
   }

   public static OutputStream encode(OutputStream os, String encoding, String filename) throws MessagingException {
      if (encoding == null) {
         return os;
      } else if (encoding.equalsIgnoreCase("base64")) {
         return new BASE64EncoderStream(os);
      } else if (encoding.equalsIgnoreCase("quoted-printable")) {
         return new QPEncoderStream(os);
      } else if (!encoding.equalsIgnoreCase("uuencode") && !encoding.equalsIgnoreCase("x-uuencode") && !encoding.equalsIgnoreCase("x-uue")) {
         if (!encoding.equalsIgnoreCase("binary") && !encoding.equalsIgnoreCase("7bit") && !encoding.equalsIgnoreCase("8bit")) {
            throw new MessagingException("Unknown encoding: " + encoding);
         } else {
            return os;
         }
      } else {
         return new UUEncoderStream(os, filename);
      }
   }

   public static String encodeText(String text) throws UnsupportedEncodingException {
      return encodeText(text, (String)null, (String)null);
   }

   public static String encodeText(String text, String charset, String encoding) throws UnsupportedEncodingException {
      return encodeWord(text, charset, encoding, false);
   }

   public static String decodeText(String etext) throws UnsupportedEncodingException {
      String lwsp = " \t\n\r";
      if (etext.indexOf("=?") == -1) {
         return etext;
      } else {
         StringTokenizer st = new StringTokenizer(etext, lwsp, true);
         StringBuffer sb = new StringBuffer();
         StringBuffer wsb = new StringBuffer();
         boolean prevWasEncoded = false;

         while(true) {
            while(st.hasMoreTokens()) {
               String s = st.nextToken();
               char c;
               if ((c = s.charAt(0)) != ' ' && c != '\t' && c != '\r' && c != '\n') {
                  String word;
                  try {
                     word = decodeWord(s);
                     if (!prevWasEncoded && wsb.length() > 0) {
                        sb.append(wsb);
                     }

                     prevWasEncoded = true;
                  } catch (ParseException var11) {
                     word = s;
                     if (!decodeStrict) {
                        String dword = decodeInnerWords(s);
                        if (dword != s) {
                           if ((!prevWasEncoded || !s.startsWith("=?")) && wsb.length() > 0) {
                              sb.append(wsb);
                           }

                           prevWasEncoded = s.endsWith("?=");
                           word = dword;
                        } else {
                           if (wsb.length() > 0) {
                              sb.append(wsb);
                           }

                           prevWasEncoded = false;
                        }
                     } else {
                        if (wsb.length() > 0) {
                           sb.append(wsb);
                        }

                        prevWasEncoded = false;
                     }
                  }

                  sb.append(word);
                  wsb.setLength(0);
               } else {
                  wsb.append(c);
               }
            }

            sb.append(wsb);
            return sb.toString();
         }
      }
   }

   public static String encodeWord(String word) throws UnsupportedEncodingException {
      return encodeWord(word, (String)null, (String)null);
   }

   public static String encodeWord(String word, String charset, String encoding) throws UnsupportedEncodingException {
      return encodeWord(word, charset, encoding, true);
   }

   private static String encodeWord(String string, String charset, String encoding, boolean encodingWord) throws UnsupportedEncodingException {
      int ascii = checkAscii(string);
      if (ascii == 1) {
         return string;
      } else {
         String jcharset;
         if (charset == null) {
            jcharset = getDefaultJavaCharset();
            charset = getDefaultMIMECharset();
         } else {
            jcharset = javaCharset(charset);
         }

         if (encoding == null) {
            if (ascii != 3) {
               encoding = "Q";
            } else {
               encoding = "B";
            }
         }

         boolean b64;
         if (encoding.equalsIgnoreCase("B")) {
            b64 = true;
         } else {
            if (!encoding.equalsIgnoreCase("Q")) {
               throw new UnsupportedEncodingException("Unknown transfer encoding: " + encoding);
            }

            b64 = false;
         }

         StringBuffer outb = new StringBuffer();
         doEncode(string, b64, jcharset, 68 - charset.length(), "=?" + charset + "?" + encoding + "?", true, encodingWord, outb);
         return outb.toString();
      }
   }

   private static void doEncode(String string, boolean b64, String jcharset, int avail, String prefix, boolean first, boolean encodingWord, StringBuffer buf) throws UnsupportedEncodingException {
      byte[] bytes = string.getBytes(jcharset);
      int len;
      if (b64) {
         len = BEncoderStream.encodedLength(bytes);
      } else {
         len = QEncoderStream.encodedLength(bytes, encodingWord);
      }

      int size;
      if (len > avail && (size = string.length()) > 1) {
         doEncode(string.substring(0, size / 2), b64, jcharset, avail, prefix, first, encodingWord, buf);
         doEncode(string.substring(size / 2, size), b64, jcharset, avail, prefix, false, encodingWord, buf);
      } else {
         ByteArrayOutputStream os = new ByteArrayOutputStream();
         Object eos;
         if (b64) {
            eos = new BEncoderStream(os);
         } else {
            eos = new QEncoderStream(os, encodingWord);
         }

         try {
            ((OutputStream)eos).write(bytes);
            ((OutputStream)eos).close();
         } catch (IOException var15) {
         }

         byte[] encodedBytes = os.toByteArray();
         if (!first) {
            if (foldEncodedWords) {
               buf.append("\r\n ");
            } else {
               buf.append(" ");
            }
         }

         buf.append(prefix);

         for(int i = 0; i < encodedBytes.length; ++i) {
            buf.append((char)encodedBytes[i]);
         }

         buf.append("?=");
      }

   }

   public static String decodeWord(String eword) throws ParseException, UnsupportedEncodingException {
      if (!eword.startsWith("=?")) {
         throw new ParseException("encoded word does not start with \"=?\": " + eword);
      } else {
         int start = 2;
         int pos;
         if ((pos = eword.indexOf(63, start)) == -1) {
            throw new ParseException("encoded word does not include charset: " + eword);
         } else {
            String charset = eword.substring(start, pos);
            int lpos = charset.indexOf(42);
            if (lpos >= 0) {
               charset = charset.substring(0, lpos);
            }

            charset = javaCharset(charset);
            start = pos + 1;
            if ((pos = eword.indexOf(63, start)) == -1) {
               throw new ParseException("encoded word does not include encoding: " + eword);
            } else {
               String encoding = eword.substring(start, pos);
               start = pos + 1;
               if ((pos = eword.indexOf("?=", start)) == -1) {
                  throw new ParseException("encoded word does not end with \"?=\": " + eword);
               } else {
                  String word = eword.substring(start, pos);

                  try {
                     String decodedWord;
                     if (word.length() > 0) {
                        ByteArrayInputStream bis = new ByteArrayInputStream(ASCIIUtility.getBytes(word));
                        Object is;
                        if (encoding.equalsIgnoreCase("B")) {
                           is = new BASE64DecoderStream(bis);
                        } else {
                           if (!encoding.equalsIgnoreCase("Q")) {
                              throw new UnsupportedEncodingException("unknown encoding: " + encoding);
                           }

                           is = new QDecoderStream(bis);
                        }

                        int count = bis.available();
                        byte[] bytes = new byte[count];
                        count = ((InputStream)is).read(bytes, 0, count);
                        decodedWord = count <= 0 ? "" : new String(bytes, 0, count, charset);
                     } else {
                        decodedWord = "";
                     }

                     if (pos + 2 < eword.length()) {
                        String rest = eword.substring(pos + 2);
                        if (!decodeStrict) {
                           rest = decodeInnerWords(rest);
                        }

                        decodedWord = decodedWord + rest;
                     }

                     return decodedWord;
                  } catch (UnsupportedEncodingException var12) {
                     throw var12;
                  } catch (IOException var13) {
                     throw new ParseException(var13.toString());
                  } catch (IllegalArgumentException var14) {
                     throw new UnsupportedEncodingException(charset);
                  }
               }
            }
         }
      }
   }

   private static String decodeInnerWords(String word) throws UnsupportedEncodingException {
      int start = 0;

      int i;
      StringBuffer buf;
      int end;
      for(buf = new StringBuffer(); (i = word.indexOf("=?", start)) >= 0; start = end + 2) {
         buf.append(word.substring(start, i));
         end = word.indexOf(63, i + 2);
         if (end < 0) {
            break;
         }

         end = word.indexOf(63, end + 1);
         if (end < 0) {
            break;
         }

         end = word.indexOf("?=", end + 1);
         if (end < 0) {
            break;
         }

         String s = word.substring(i, end + 2);

         try {
            s = decodeWord(s);
         } catch (ParseException var7) {
         }

         buf.append(s);
      }

      if (start == 0) {
         return word;
      } else {
         if (start < word.length()) {
            buf.append(word.substring(start));
         }

         return buf.toString();
      }
   }

   public static String quote(String word, String specials) {
      int len = word.length();
      if (len == 0) {
         return "\"\"";
      } else {
         boolean needQuoting = false;

         for(int i = 0; i < len; ++i) {
            char c = word.charAt(i);
            if (c == '"' || c == '\\' || c == '\r' || c == '\n') {
               StringBuffer sb = new StringBuffer(len + 3);
               sb.append('"');
               sb.append(word.substring(0, i));
               int lastc = 0;

               for(int j = i; j < len; ++j) {
                  char cc = word.charAt(j);
                  if ((cc == '"' || cc == '\\' || cc == '\r' || cc == '\n') && (cc != '\n' || lastc != '\r')) {
                     sb.append('\\');
                  }

                  sb.append(cc);
                  lastc = cc;
               }

               sb.append('"');
               return sb.toString();
            }

            if (c < ' ' || c >= 127 || specials.indexOf(c) >= 0) {
               needQuoting = true;
            }
         }

         if (needQuoting) {
            StringBuffer sb = new StringBuffer(len + 2);
            sb.append('"').append(word).append('"');
            return sb.toString();
         } else {
            return word;
         }
      }
   }

   public static String fold(int used, String s) {
      if (!foldText) {
         return s;
      } else {
         int end;
         char c;
         for(end = s.length() - 1; end >= 0; --end) {
            c = s.charAt(end);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
               break;
            }
         }

         if (end != s.length() - 1) {
            s = s.substring(0, end + 1);
         }

         if (used + s.length() <= 76) {
            return s;
         } else {
            StringBuffer sb = new StringBuffer(s.length() + 4);

            for(char lastc = 0; used + s.length() > 76; used = 1) {
               int lastspace = -1;

               for(int i = 0; i < s.length() && (lastspace == -1 || used + i <= 76); ++i) {
                  c = s.charAt(i);
                  if ((c == ' ' || c == '\t') && lastc != ' ' && lastc != '\t') {
                     lastspace = i;
                  }

                  lastc = c;
               }

               if (lastspace == -1) {
                  sb.append(s);
                  s = "";
                  int used = false;
                  break;
               }

               sb.append(s.substring(0, lastspace));
               sb.append("\r\n");
               lastc = s.charAt(lastspace);
               sb.append(lastc);
               s = s.substring(lastspace + 1);
            }

            sb.append(s);
            return sb.toString();
         }
      }
   }

   public static String unfold(String s) {
      if (!foldText) {
         return s;
      } else {
         StringBuffer sb = null;

         while(true) {
            int i;
            while((i = indexOfAny(s, "\r\n")) >= 0) {
               int start = i;
               int l = s.length();
               ++i;
               if (i < l && s.charAt(i - 1) == '\r' && s.charAt(i) == '\n') {
                  ++i;
               }

               if (start != 0 && s.charAt(start - 1) == '\\') {
                  if (sb == null) {
                     sb = new StringBuffer(s.length());
                  }

                  sb.append(s.substring(0, start - 1));
                  sb.append(s.substring(start, i));
                  s = s.substring(i);
               } else {
                  char c;
                  if (i >= l || (c = s.charAt(i)) != ' ' && c != '\t') {
                     if (sb == null) {
                        sb = new StringBuffer(s.length());
                     }

                     sb.append(s.substring(0, i));
                     s = s.substring(i);
                  } else {
                     ++i;

                     while(i < l && ((c = s.charAt(i)) == ' ' || c == '\t')) {
                        ++i;
                     }

                     if (sb == null) {
                        sb = new StringBuffer(s.length());
                     }

                     if (start != 0) {
                        sb.append(s.substring(0, start));
                        sb.append(' ');
                     }

                     s = s.substring(i);
                  }
               }
            }

            if (sb != null) {
               sb.append(s);
               return sb.toString();
            }

            return s;
         }
      }
   }

   private static int indexOfAny(String s, String any) {
      return indexOfAny(s, any, 0);
   }

   private static int indexOfAny(String s, String any, int start) {
      try {
         int len = s.length();

         for(int i = start; i < len; ++i) {
            if (any.indexOf(s.charAt(i)) >= 0) {
               return i;
            }
         }

         return -1;
      } catch (StringIndexOutOfBoundsException var5) {
         return -1;
      }
   }

   public static String javaCharset(String charset) {
      if (mime2java != null && charset != null) {
         String alias = (String)mime2java.get(charset.toLowerCase(Locale.ENGLISH));
         return alias == null ? charset : alias;
      } else {
         return charset;
      }
   }

   public static String mimeCharset(String charset) {
      if (java2mime != null && charset != null) {
         String alias = (String)java2mime.get(charset.toLowerCase(Locale.ENGLISH));
         return alias == null ? charset : alias;
      } else {
         return charset;
      }
   }

   public static String getDefaultJavaCharset() {
      if (defaultJavaCharset == null) {
         String mimecs = null;

         try {
            mimecs = System.getProperty("mail.mime.charset");
         } catch (SecurityException var3) {
         }

         if (mimecs != null && mimecs.length() > 0) {
            defaultJavaCharset = javaCharset(mimecs);
            return defaultJavaCharset;
         }

         try {
            defaultJavaCharset = System.getProperty("file.encoding", "8859_1");
         } catch (SecurityException var4) {
            class NullInputStream extends InputStream {
               public int read() {
                  return 0;
               }
            }

            InputStreamReader reader = new InputStreamReader(new NullInputStream());
            defaultJavaCharset = reader.getEncoding();
            if (defaultJavaCharset == null) {
               defaultJavaCharset = "8859_1";
            }
         }
      }

      return defaultJavaCharset;
   }

   static String getDefaultMIMECharset() {
      if (defaultMIMECharset == null) {
         try {
            defaultMIMECharset = System.getProperty("mail.mime.charset");
         } catch (SecurityException var1) {
         }
      }

      if (defaultMIMECharset == null) {
         defaultMIMECharset = mimeCharset(getDefaultJavaCharset());
      }

      return defaultMIMECharset;
   }

   private static void loadMappings(LineInputStream is, Hashtable table) {
      while(true) {
         String currLine;
         try {
            currLine = is.readLine();
         } catch (IOException var7) {
            break;
         }

         if (currLine == null || currLine.startsWith("--") && currLine.endsWith("--")) {
            break;
         }

         if (currLine.trim().length() != 0 && !currLine.startsWith("#")) {
            StringTokenizer tk = new StringTokenizer(currLine, " \t");

            try {
               String key = tk.nextToken();
               String value = tk.nextToken();
               table.put(key.toLowerCase(Locale.ENGLISH), value);
            } catch (NoSuchElementException var6) {
            }
         }
      }

   }

   static int checkAscii(String s) {
      int ascii = 0;
      int non_ascii = 0;
      int l = s.length();

      for(int i = 0; i < l; ++i) {
         if (nonascii(s.charAt(i))) {
            ++non_ascii;
         } else {
            ++ascii;
         }
      }

      if (non_ascii == 0) {
         return 1;
      } else if (ascii > non_ascii) {
         return 2;
      } else {
         return 3;
      }
   }

   static int checkAscii(byte[] b) {
      int ascii = 0;
      int non_ascii = 0;

      for(int i = 0; i < b.length; ++i) {
         if (nonascii(b[i] & 255)) {
            ++non_ascii;
         } else {
            ++ascii;
         }
      }

      if (non_ascii == 0) {
         return 1;
      } else if (ascii > non_ascii) {
         return 2;
      } else {
         return 3;
      }
   }

   static int checkAscii(InputStream is, int max, boolean breakOnNonAscii) {
      int ascii = 0;
      int non_ascii = 0;
      int block = 4096;
      int linelen = 0;
      boolean longLine = false;
      boolean badEOL = false;
      boolean checkEOL = encodeEolStrict && breakOnNonAscii;
      byte[] buf = null;
      if (max != 0) {
         block = max == -1 ? 4096 : Math.min(max, 4096);
         buf = new byte[block];
      }

      while(max != 0) {
         int len;
         try {
            if ((len = is.read(buf, 0, block)) == -1) {
               break;
            }

            int lastb = 0;

            for(int i = 0; i < len; ++i) {
               int b = buf[i] & 255;
               if (checkEOL && (lastb == 13 && b != 10 || lastb != 13 && b == 10)) {
                  badEOL = true;
               }

               if (b != 13 && b != 10) {
                  ++linelen;
                  if (linelen > 998) {
                     longLine = true;
                  }
               } else {
                  linelen = 0;
               }

               if (nonascii(b)) {
                  if (breakOnNonAscii) {
                     return 3;
                  }

                  ++non_ascii;
               } else {
                  ++ascii;
               }

               lastb = b;
            }
         } catch (IOException var15) {
            break;
         }

         if (max != -1) {
            max -= len;
         }
      }

      if (max == 0 && breakOnNonAscii) {
         return 3;
      } else if (non_ascii == 0) {
         if (badEOL) {
            return 3;
         } else {
            return longLine ? 2 : 1;
         }
      } else {
         return ascii > non_ascii ? 2 : 3;
      }
   }

   static final boolean nonascii(int b) {
      return b >= 127 || b < 32 && b != 13 && b != 10 && b != 9;
   }

   static {
      try {
         InputStream is = MimeUtility.class.getResourceAsStream("/META-INF/javamail.charset.map");
         if (is != null) {
            try {
               is = new LineInputStream((InputStream)is);
               loadMappings((LineInputStream)is, java2mime);
               loadMappings((LineInputStream)is, mime2java);
            } finally {
               try {
                  ((InputStream)is).close();
               } catch (Exception var8) {
               }

            }
         }
      } catch (Exception var10) {
      }

      if (java2mime.isEmpty()) {
         java2mime.put("8859_1", "ISO-8859-1");
         java2mime.put("iso8859_1", "ISO-8859-1");
         java2mime.put("iso8859-1", "ISO-8859-1");
         java2mime.put("8859_2", "ISO-8859-2");
         java2mime.put("iso8859_2", "ISO-8859-2");
         java2mime.put("iso8859-2", "ISO-8859-2");
         java2mime.put("8859_3", "ISO-8859-3");
         java2mime.put("iso8859_3", "ISO-8859-3");
         java2mime.put("iso8859-3", "ISO-8859-3");
         java2mime.put("8859_4", "ISO-8859-4");
         java2mime.put("iso8859_4", "ISO-8859-4");
         java2mime.put("iso8859-4", "ISO-8859-4");
         java2mime.put("8859_5", "ISO-8859-5");
         java2mime.put("iso8859_5", "ISO-8859-5");
         java2mime.put("iso8859-5", "ISO-8859-5");
         java2mime.put("8859_6", "ISO-8859-6");
         java2mime.put("iso8859_6", "ISO-8859-6");
         java2mime.put("iso8859-6", "ISO-8859-6");
         java2mime.put("8859_7", "ISO-8859-7");
         java2mime.put("iso8859_7", "ISO-8859-7");
         java2mime.put("iso8859-7", "ISO-8859-7");
         java2mime.put("8859_8", "ISO-8859-8");
         java2mime.put("iso8859_8", "ISO-8859-8");
         java2mime.put("iso8859-8", "ISO-8859-8");
         java2mime.put("8859_9", "ISO-8859-9");
         java2mime.put("iso8859_9", "ISO-8859-9");
         java2mime.put("iso8859-9", "ISO-8859-9");
         java2mime.put("sjis", "Shift_JIS");
         java2mime.put("jis", "ISO-2022-JP");
         java2mime.put("iso2022jp", "ISO-2022-JP");
         java2mime.put("euc_jp", "euc-jp");
         java2mime.put("koi8_r", "koi8-r");
         java2mime.put("euc_cn", "euc-cn");
         java2mime.put("euc_tw", "euc-tw");
         java2mime.put("euc_kr", "euc-kr");
      }

      if (mime2java.isEmpty()) {
         mime2java.put("iso-2022-cn", "ISO2022CN");
         mime2java.put("iso-2022-kr", "ISO2022KR");
         mime2java.put("utf-8", "UTF8");
         mime2java.put("utf8", "UTF8");
         mime2java.put("ja_jp.iso2022-7", "ISO2022JP");
         mime2java.put("ja_jp.eucjp", "EUCJIS");
         mime2java.put("euc-kr", "KSC5601");
         mime2java.put("euckr", "KSC5601");
         mime2java.put("us-ascii", "ISO-8859-1");
         mime2java.put("x-us-ascii", "ISO-8859-1");
      }

   }
}
