package cn.hutool.core.net;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.BitSet;

/** @deprecated */
@Deprecated
public class URLEncoder implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final URLEncoder DEFAULT = createDefault();
   public static final URLEncoder PATH_SEGMENT = createPathSegment();
   public static final URLEncoder FRAGMENT = createFragment();
   public static final URLEncoder QUERY = createQuery();
   public static final URLEncoder ALL = createAll();
   private final BitSet safeCharacters;
   private boolean encodeSpaceAsPlus;

   public static URLEncoder createDefault() {
      URLEncoder encoder = new URLEncoder();
      encoder.addSafeCharacter('-');
      encoder.addSafeCharacter('.');
      encoder.addSafeCharacter('_');
      encoder.addSafeCharacter('~');
      addSubDelims(encoder);
      encoder.addSafeCharacter(':');
      encoder.addSafeCharacter('@');
      encoder.addSafeCharacter('/');
      return encoder;
   }

   public static URLEncoder createPathSegment() {
      URLEncoder encoder = new URLEncoder();
      encoder.addSafeCharacter('-');
      encoder.addSafeCharacter('.');
      encoder.addSafeCharacter('_');
      encoder.addSafeCharacter('~');
      addSubDelims(encoder);
      encoder.addSafeCharacter('@');
      return encoder;
   }

   public static URLEncoder createFragment() {
      URLEncoder encoder = new URLEncoder();
      encoder.addSafeCharacter('-');
      encoder.addSafeCharacter('.');
      encoder.addSafeCharacter('_');
      encoder.addSafeCharacter('~');
      addSubDelims(encoder);
      encoder.addSafeCharacter(':');
      encoder.addSafeCharacter('@');
      encoder.addSafeCharacter('/');
      encoder.addSafeCharacter('?');
      return encoder;
   }

   public static URLEncoder createQuery() {
      URLEncoder encoder = new URLEncoder();
      encoder.setEncodeSpaceAsPlus(true);
      encoder.addSafeCharacter('*');
      encoder.addSafeCharacter('-');
      encoder.addSafeCharacter('.');
      encoder.addSafeCharacter('_');
      encoder.addSafeCharacter('=');
      encoder.addSafeCharacter('&');
      return encoder;
   }

   public static URLEncoder createAll() {
      URLEncoder encoder = new URLEncoder();
      encoder.addSafeCharacter('*');
      encoder.addSafeCharacter('-');
      encoder.addSafeCharacter('.');
      encoder.addSafeCharacter('_');
      return encoder;
   }

   public URLEncoder() {
      this(new BitSet(256));
      this.addAlpha();
      this.addDigit();
   }

   private URLEncoder(BitSet safeCharacters) {
      this.encodeSpaceAsPlus = false;
      this.safeCharacters = safeCharacters;
   }

   public void addSafeCharacter(char c) {
      this.safeCharacters.set(c);
   }

   public void removeSafeCharacter(char c) {
      this.safeCharacters.clear(c);
   }

   public void setEncodeSpaceAsPlus(boolean encodeSpaceAsPlus) {
      this.encodeSpaceAsPlus = encodeSpaceAsPlus;
   }

   public String encode(String path, Charset charset) {
      if (null != charset && !StrUtil.isEmpty(path)) {
         StringBuilder rewrittenPath = new StringBuilder(path.length());
         ByteArrayOutputStream buf = new ByteArrayOutputStream();
         OutputStreamWriter writer = new OutputStreamWriter(buf, charset);

         for(int i = 0; i < path.length(); ++i) {
            int c = path.charAt(i);
            if (this.safeCharacters.get(c)) {
               rewrittenPath.append((char)c);
            } else if (this.encodeSpaceAsPlus && c == ' ') {
               rewrittenPath.append('+');
            } else {
               try {
                  writer.write((char)c);
                  writer.flush();
               } catch (IOException var13) {
                  buf.reset();
                  continue;
               }

               byte[] ba = buf.toByteArray();
               byte[] var9 = ba;
               int var10 = ba.length;

               for(int var11 = 0; var11 < var10; ++var11) {
                  byte toEncode = var9[var11];
                  rewrittenPath.append('%');
                  HexUtil.appendHex(rewrittenPath, toEncode, false);
               }

               buf.reset();
            }
         }

         return rewrittenPath.toString();
      } else {
         return path;
      }
   }

   private void addAlpha() {
      char i;
      for(i = 'a'; i <= 'z'; ++i) {
         this.addSafeCharacter(i);
      }

      for(i = 'A'; i <= 'Z'; ++i) {
         this.addSafeCharacter(i);
      }

   }

   private void addDigit() {
      for(char i = '0'; i <= '9'; ++i) {
         this.addSafeCharacter(i);
      }

   }

   private static void addSubDelims(URLEncoder encoder) {
      encoder.addSafeCharacter('!');
      encoder.addSafeCharacter('$');
      encoder.addSafeCharacter('&');
      encoder.addSafeCharacter('\'');
      encoder.addSafeCharacter('(');
      encoder.addSafeCharacter(')');
      encoder.addSafeCharacter('*');
      encoder.addSafeCharacter('+');
      encoder.addSafeCharacter(',');
      encoder.addSafeCharacter(';');
      encoder.addSafeCharacter('=');
   }
}
