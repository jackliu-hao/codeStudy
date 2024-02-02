package io.undertow.protocols.ajp;

import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import java.io.IOException;
import java.nio.ByteBuffer;

class AjpResponseParser {
   public static final AjpResponseParser INSTANCE = new AjpResponseParser();
   private static final int AB = 16706;
   public static final int BEGIN = 0;
   public static final int READING_MAGIC_NUMBER = 1;
   public static final int READING_DATA_SIZE = 2;
   public static final int READING_PREFIX_CODE = 3;
   public static final int READING_STATUS_CODE = 4;
   public static final int READING_REASON_PHRASE = 5;
   public static final int READING_NUM_HEADERS = 6;
   public static final int READING_HEADERS = 7;
   public static final int READING_PERSISTENT_BOOLEAN = 8;
   public static final int READING_BODY_CHUNK_LENGTH = 9;
   public static final int DONE = 10;
   int state;
   byte prefix;
   int numHeaders = 0;
   HttpString currentHeader;
   int statusCode;
   String reasonPhrase;
   HeaderMap headers = new HeaderMap();
   int readBodyChunkSize;
   public static final int STRING_LENGTH_MASK = Integer.MIN_VALUE;
   public int stringLength = -1;
   public StringBuilder currentString;
   public int currentIntegerPart = -1;
   boolean containsUrlCharacters = false;
   public int readHeaders = 0;

   public boolean isComplete() {
      return this.state == 10;
   }

   public void parse(ByteBuffer buf) throws IOException {
      if (buf.hasRemaining()) {
         IntegerHolder result;
         int prefix;
         switch (this.state) {
            case 0:
               result = this.parse16BitInteger(buf);
               if (!result.readComplete) {
                  return;
               }

               if (result.value != 16706) {
                  throw new IOException("Wrong magic number");
               }
            case 2:
               result = this.parse16BitInteger(buf);
               if (!result.readComplete) {
                  this.state = 2;
                  return;
               }
            case 3:
               if (!buf.hasRemaining()) {
                  this.state = 3;
                  return;
               }

               prefix = buf.get();
               this.prefix = (byte)prefix;
               if (prefix == 5) {
                  this.state = 8;
                  break;
               } else if (prefix == 3) {
                  this.state = 9;
                  break;
               } else if (prefix != 4 && prefix != 6) {
                  this.state = 10;
                  return;
               }
            case 4:
               result = this.parse16BitInteger(buf);
               if (!result.readComplete) {
                  this.state = 4;
                  return;
               }

               if (this.prefix != 4) {
                  this.state = 10;
                  this.readBodyChunkSize = result.value;
                  return;
               }

               this.statusCode = result.value;
            case 5:
               StringHolder result = this.parseString(buf, false);
               if (!result.readComplete) {
                  this.state = 5;
                  return;
               }

               this.reasonPhrase = result.value;
            case 6:
               result = this.parse16BitInteger(buf);
               if (!result.readComplete) {
                  this.state = 6;
                  return;
               }

               this.numHeaders = result.value;
            case 7:
               for(prefix = this.readHeaders; prefix < this.numHeaders; ++prefix) {
                  StringHolder result;
                  if (this.currentHeader == null) {
                     result = this.parseString(buf, true);
                     if (!result.readComplete) {
                        this.state = 7;
                        this.readHeaders = prefix;
                        return;
                     }

                     if (result.header != null) {
                        this.currentHeader = result.header;
                     } else {
                        this.currentHeader = HttpString.tryFromString(result.value);
                     }
                  }

                  result = this.parseString(buf, false);
                  if (!result.readComplete) {
                     this.state = 7;
                     this.readHeaders = prefix;
                     return;
                  }

                  this.headers.add(this.currentHeader, result.value);
                  this.currentHeader = null;
               }
            case 1:
         }

         if (this.state == 8) {
            if (buf.hasRemaining()) {
               this.currentIntegerPart = buf.get();
               this.state = 10;
            }
         } else if (this.state == 9) {
            result = this.parse16BitInteger(buf);
            if (result.readComplete) {
               this.currentIntegerPart = result.value;
               this.state = 10;
            }

         } else {
            this.state = 10;
         }
      }
   }

   protected HttpString headers(int offset) {
      return AjpConstants.HTTP_HEADERS_ARRAY[offset];
   }

   public HeaderMap getHeaders() {
      return this.headers;
   }

   public int getStatusCode() {
      return this.statusCode;
   }

   public String getReasonPhrase() {
      return this.reasonPhrase;
   }

   public int getReadBodyChunkSize() {
      return this.readBodyChunkSize;
   }

   public void reset() {
      this.state = 0;
      this.prefix = 0;
      this.numHeaders = 0;
      this.currentHeader = null;
      this.statusCode = 0;
      this.reasonPhrase = null;
      this.headers = new HeaderMap();
      this.stringLength = -1;
      this.currentString = null;
      this.currentIntegerPart = -1;
      this.readHeaders = 0;
   }

   protected IntegerHolder parse16BitInteger(ByteBuffer buf) {
      if (!buf.hasRemaining()) {
         return new IntegerHolder(-1, false);
      } else {
         int number = this.currentIntegerPart;
         if (number == -1) {
            number = buf.get() & 255;
         }

         if (buf.hasRemaining()) {
            byte b = buf.get();
            int result = ((255 & number) << 8) + (b & 255);
            this.currentIntegerPart = -1;
            return new IntegerHolder(result, true);
         } else {
            this.currentIntegerPart = number;
            return new IntegerHolder(-1, false);
         }
      }
   }

   protected StringHolder parseString(ByteBuffer buf, boolean header) {
      boolean containsUrlCharacters = this.containsUrlCharacters;
      if (!buf.hasRemaining()) {
         return new StringHolder((String)null, false, false);
      } else {
         int stringLength = this.stringLength;
         int number;
         int length;
         if (stringLength == -1) {
            number = buf.get() & 255;
            if (!buf.hasRemaining()) {
               this.stringLength = number | Integer.MIN_VALUE;
               return new StringHolder((String)null, false, false);
            }

            length = buf.get();
            stringLength = ((255 & number) << 8) + (length & 255);
         } else if ((stringLength & Integer.MIN_VALUE) != 0) {
            number = stringLength & Integer.MAX_VALUE;
            stringLength = ((255 & number) << 8) + (buf.get() & 255);
         }

         if (header && (stringLength & '\uff00') != 0) {
            this.stringLength = -1;
            return new StringHolder(this.headers(stringLength & 255));
         } else if (stringLength == 65535) {
            this.stringLength = -1;
            return new StringHolder((String)null, true, false);
         } else {
            StringBuilder builder = this.currentString;
            if (builder == null) {
               builder = new StringBuilder();
               this.currentString = builder;
            }

            for(length = builder.length(); length < stringLength; ++length) {
               if (!buf.hasRemaining()) {
                  this.stringLength = stringLength;
                  this.containsUrlCharacters = containsUrlCharacters;
                  return new StringHolder((String)null, false, false);
               }

               char c = (char)buf.get();
               if (c == '+' || c == '%') {
                  containsUrlCharacters = true;
               }

               builder.append(c);
            }

            if (buf.hasRemaining()) {
               buf.get();
               this.currentString = null;
               this.stringLength = -1;
               this.containsUrlCharacters = false;
               return new StringHolder(builder.toString(), true, containsUrlCharacters);
            } else {
               this.stringLength = stringLength;
               this.containsUrlCharacters = containsUrlCharacters;
               return new StringHolder((String)null, false, false);
            }
         }
      }
   }

   protected static class StringHolder {
      public final String value;
      public final HttpString header;
      public final boolean readComplete;
      public final boolean containsUrlCharacters;

      private StringHolder(String value, boolean readComplete, boolean containsUrlCharacters) {
         this.value = value;
         this.readComplete = readComplete;
         this.containsUrlCharacters = containsUrlCharacters;
         this.header = null;
      }

      private StringHolder(HttpString value) {
         this.value = null;
         this.readComplete = true;
         this.header = value;
         this.containsUrlCharacters = false;
      }

      // $FF: synthetic method
      StringHolder(String x0, boolean x1, boolean x2, Object x3) {
         this(x0, x1, x2);
      }

      // $FF: synthetic method
      StringHolder(HttpString x0, Object x1) {
         this(x0);
      }
   }

   protected static class IntegerHolder {
      public final int value;
      public final boolean readComplete;

      private IntegerHolder(int value, boolean readComplete) {
         this.value = value;
         this.readComplete = readComplete;
      }

      // $FF: synthetic method
      IntegerHolder(int x0, boolean x1, Object x2) {
         this(x0, x1);
      }
   }
}
