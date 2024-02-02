package com.sun.mail.imap.protocol;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;

public class BASE64MailboxEncoder {
   protected byte[] buffer = new byte[4];
   protected int bufsize = 0;
   protected boolean started = false;
   protected Writer out = null;
   private static final char[] pem_array = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', ','};

   public static String encode(String original) {
      BASE64MailboxEncoder base64stream = null;
      char[] origchars = original.toCharArray();
      int length = origchars.length;
      boolean changedString = false;
      CharArrayWriter writer = new CharArrayWriter(length);

      for(int index = 0; index < length; ++index) {
         char current = origchars[index];
         if (current >= ' ' && current <= '~') {
            if (base64stream != null) {
               base64stream.flush();
            }

            if (current == '&') {
               changedString = true;
               writer.write(38);
               writer.write(45);
            } else {
               writer.write(current);
            }
         } else {
            if (base64stream == null) {
               base64stream = new BASE64MailboxEncoder(writer);
               changedString = true;
            }

            base64stream.write(current);
         }
      }

      if (base64stream != null) {
         base64stream.flush();
      }

      if (changedString) {
         return writer.toString();
      } else {
         return original;
      }
   }

   public BASE64MailboxEncoder(Writer what) {
      this.out = what;
   }

   public void write(int c) {
      try {
         if (!this.started) {
            this.started = true;
            this.out.write(38);
         }

         this.buffer[this.bufsize++] = (byte)(c >> 8);
         this.buffer[this.bufsize++] = (byte)(c & 255);
         if (this.bufsize >= 3) {
            this.encode();
            this.bufsize -= 3;
         }
      } catch (IOException var3) {
      }

   }

   public void flush() {
      try {
         if (this.bufsize > 0) {
            this.encode();
            this.bufsize = 0;
         }

         if (this.started) {
            this.out.write(45);
            this.started = false;
         }
      } catch (IOException var2) {
      }

   }

   protected void encode() throws IOException {
      byte a;
      if (this.bufsize == 1) {
         a = this.buffer[0];
         byte b = 0;
         byte c = false;
         this.out.write(pem_array[a >>> 2 & 63]);
         this.out.write(pem_array[(a << 4 & 48) + (b >>> 4 & 15)]);
      } else {
         byte b;
         if (this.bufsize == 2) {
            a = this.buffer[0];
            b = this.buffer[1];
            byte c = 0;
            this.out.write(pem_array[a >>> 2 & 63]);
            this.out.write(pem_array[(a << 4 & 48) + (b >>> 4 & 15)]);
            this.out.write(pem_array[(b << 2 & 60) + (c >>> 6 & 3)]);
         } else {
            a = this.buffer[0];
            b = this.buffer[1];
            byte c = this.buffer[2];
            this.out.write(pem_array[a >>> 2 & 63]);
            this.out.write(pem_array[(a << 4 & 48) + (b >>> 4 & 15)]);
            this.out.write(pem_array[(b << 2 & 60) + (c >>> 6 & 3)]);
            this.out.write(pem_array[c & 63]);
            if (this.bufsize == 4) {
               this.buffer[0] = this.buffer[3];
            }
         }
      }

   }
}
