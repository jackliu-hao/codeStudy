package io.undertow.protocols.http2;

import io.undertow.UndertowMessages;
import io.undertow.util.HttpString;
import java.nio.ByteBuffer;

final class Hpack {
   private static final byte LOWER_DIFF = 32;
   static final int DEFAULT_TABLE_SIZE = 4096;
   private static final int MAX_INTEGER_OCTETS = 8;
   private static final int[] PREFIX_TABLE = new int[32];
   static final HeaderField[] STATIC_TABLE;
   static final int STATIC_TABLE_LENGTH;

   static int decodeInteger(ByteBuffer source, int n) throws HpackException {
      if (source.remaining() == 0) {
         return -1;
      } else if (n >= PREFIX_TABLE.length) {
         throw UndertowMessages.MESSAGES.integerEncodedOverTooManyOctets(8);
      } else {
         int count = 1;
         int sp = source.position();
         int mask = PREFIX_TABLE[n];
         int i = mask & source.get();
         if (i < PREFIX_TABLE[n]) {
            return i;
         } else {
            int m = 0;

            byte b;
            do {
               if (count++ > 8) {
                  throw UndertowMessages.MESSAGES.integerEncodedOverTooManyOctets(8);
               }

               if (source.remaining() == 0) {
                  source.position(sp);
                  return -1;
               }

               if (m >= PREFIX_TABLE.length) {
                  throw UndertowMessages.MESSAGES.integerEncodedOverTooManyOctets(8);
               }

               b = source.get();
               i += (b & 127) * (PREFIX_TABLE[m] + 1);
               m += 7;
            } while((b & 128) == 128);

            return i;
         }
      }
   }

   static void encodeInteger(ByteBuffer source, int value, int n) {
      int twoNminus1 = PREFIX_TABLE[n];
      int pos = source.position() - 1;
      if (value < twoNminus1) {
         source.put(pos, (byte)(source.get(pos) | value));
      } else {
         source.put(pos, (byte)(source.get(pos) | twoNminus1));

         for(value -= twoNminus1; value >= 128; value /= 128) {
            source.put((byte)(value % 128 + 128));
         }

         source.put((byte)value);
      }

   }

   static byte toLower(byte b) {
      return b >= 65 && b <= 90 ? (byte)(b + 32) : b;
   }

   private Hpack() {
   }

   static {
      for(int i = 0; i < 32; ++i) {
         int n = 0;

         for(int j = 0; j < i; ++j) {
            n <<= 1;
            n |= 1;
         }

         PREFIX_TABLE[i] = n;
      }

      HeaderField[] fields = new HeaderField[]{null, new HeaderField(new HttpString(":authority"), (String)null), new HeaderField(new HttpString(":method"), "GET"), new HeaderField(new HttpString(":method"), "POST"), new HeaderField(new HttpString(":path"), "/"), new HeaderField(new HttpString(":path"), "/index.html"), new HeaderField(new HttpString(":scheme"), "http"), new HeaderField(new HttpString(":scheme"), "https"), new HeaderField(new HttpString(":status"), "200"), new HeaderField(new HttpString(":status"), "204"), new HeaderField(new HttpString(":status"), "206"), new HeaderField(new HttpString(":status"), "304"), new HeaderField(new HttpString(":status"), "400"), new HeaderField(new HttpString(":status"), "404"), new HeaderField(new HttpString(":status"), "500"), new HeaderField(new HttpString("accept-charset"), (String)null), new HeaderField(new HttpString("accept-encoding"), "gzip, deflate"), new HeaderField(new HttpString("accept-language"), (String)null), new HeaderField(new HttpString("accept-ranges"), (String)null), new HeaderField(new HttpString("accept"), (String)null), new HeaderField(new HttpString("access-control-allow-origin"), (String)null), new HeaderField(new HttpString("age"), (String)null), new HeaderField(new HttpString("allow"), (String)null), new HeaderField(new HttpString("authorization"), (String)null), new HeaderField(new HttpString("cache-control"), (String)null), new HeaderField(new HttpString("content-disposition"), (String)null), new HeaderField(new HttpString("content-encoding"), (String)null), new HeaderField(new HttpString("content-language"), (String)null), new HeaderField(new HttpString("content-length"), (String)null), new HeaderField(new HttpString("content-location"), (String)null), new HeaderField(new HttpString("content-range"), (String)null), new HeaderField(new HttpString("content-type"), (String)null), new HeaderField(new HttpString("cookie"), (String)null), new HeaderField(new HttpString("date"), (String)null), new HeaderField(new HttpString("etag"), (String)null), new HeaderField(new HttpString("expect"), (String)null), new HeaderField(new HttpString("expires"), (String)null), new HeaderField(new HttpString("from"), (String)null), new HeaderField(new HttpString("host"), (String)null), new HeaderField(new HttpString("if-match"), (String)null), new HeaderField(new HttpString("if-modified-since"), (String)null), new HeaderField(new HttpString("if-none-match"), (String)null), new HeaderField(new HttpString("if-range"), (String)null), new HeaderField(new HttpString("if-unmodified-since"), (String)null), new HeaderField(new HttpString("last-modified"), (String)null), new HeaderField(new HttpString("link"), (String)null), new HeaderField(new HttpString("location"), (String)null), new HeaderField(new HttpString("max-forwards"), (String)null), new HeaderField(new HttpString("proxy-authenticate"), (String)null), new HeaderField(new HttpString("proxy-authorization"), (String)null), new HeaderField(new HttpString("range"), (String)null), new HeaderField(new HttpString("referer"), (String)null), new HeaderField(new HttpString("refresh"), (String)null), new HeaderField(new HttpString("retry-after"), (String)null), new HeaderField(new HttpString("server"), (String)null), new HeaderField(new HttpString("set-cookie"), (String)null), new HeaderField(new HttpString("strict-transport-security"), (String)null), new HeaderField(new HttpString("transfer-encoding"), (String)null), new HeaderField(new HttpString("user-agent"), (String)null), new HeaderField(new HttpString("vary"), (String)null), new HeaderField(new HttpString("via"), (String)null), new HeaderField(new HttpString("www-authenticate"), (String)null)};
      STATIC_TABLE = fields;
      STATIC_TABLE_LENGTH = STATIC_TABLE.length - 1;
   }

   static class HeaderField {
      final HttpString name;
      final String value;
      final int size;

      HeaderField(HttpString name, String value) {
         this.name = name;
         this.value = value;
         if (value != null) {
            this.size = 32 + name.length() + value.length();
         } else {
            this.size = -1;
         }

      }
   }
}
