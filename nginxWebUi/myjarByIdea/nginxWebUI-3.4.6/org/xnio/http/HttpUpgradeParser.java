package org.xnio.http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class HttpUpgradeParser {
   private static final int VERSION = 0;
   private static final int STATUS_CODE = 1;
   private static final int MESSAGE = 2;
   private static final int HEADER_NAME = 3;
   private static final int HEADER_VALUE = 4;
   private static final int COMPLETE = 5;
   private int parseState = 0;
   private String httpVersion;
   private int responseCode;
   private String message;
   private final Map<String, List<String>> headers = new HashMap();
   private final StringBuilder current = new StringBuilder();
   private String headerName;

   void parse(ByteBuffer buffer) throws IOException {
      while(buffer.hasRemaining() && !this.isComplete()) {
         switch (this.parseState) {
            case 0:
               this.parseVersion(buffer);
               break;
            case 1:
               this.parseStatusCode(buffer);
               break;
            case 2:
               this.parseMessage(buffer);
               break;
            case 3:
               this.parseHeaderName(buffer);
               break;
            case 4:
               this.parseHeaderValue(buffer);
               break;
            case 5:
               return;
         }
      }

   }

   private void parseHeaderValue(ByteBuffer buffer) {
      while(true) {
         if (buffer.hasRemaining()) {
            byte b = buffer.get();
            if (b != 13 && b != 10) {
               this.current.append((char)b);
               continue;
            }

            String key = this.headerName.toLowerCase(Locale.ENGLISH);
            List<String> list = (List)this.headers.get(key);
            if (list == null) {
               this.headers.put(key, list = new ArrayList());
            }

            ((List)list).add(this.current.toString().trim());
            --this.parseState;
            this.current.setLength(0);
            return;
         }

         return;
      }
   }

   private void parseHeaderName(ByteBuffer buffer) throws IOException {
      while(buffer.hasRemaining()) {
         byte b = buffer.get();
         if (b != 13 && b != 10) {
            if (b == 58) {
               this.headerName = this.current.toString().trim();
               ++this.parseState;
               this.current.setLength(0);
               return;
            }

            this.current.append((char)b);
         } else {
            if (this.current.length() > 2) {
               throw new IOException("Invalid response");
            }

            if (this.current.length() == 2) {
               if (this.current.charAt(0) == '\n' && this.current.charAt(1) == '\r' && b == 10) {
                  this.parseState = 5;
                  return;
               }

               throw new IOException("Invalid response");
            }

            this.current.append((char)b);
         }
      }

   }

   private void parseMessage(ByteBuffer buffer) throws IOException {
      while(true) {
         if (buffer.hasRemaining()) {
            byte b = buffer.get();
            if (b != 13 && b != 10) {
               this.current.append((char)b);
               continue;
            }

            this.message = this.current.toString().trim();
            ++this.parseState;
            this.current.setLength(0);
            return;
         }

         return;
      }
   }

   private void parseStatusCode(ByteBuffer buffer) throws IOException {
      while(true) {
         if (buffer.hasRemaining()) {
            byte b = buffer.get();
            if (b != 32 && b != 9) {
               if (Character.isDigit(b)) {
                  this.current.append((char)b);
                  continue;
               }

               throw new IOException("Invalid response");
            }

            this.responseCode = Integer.parseInt(this.current.toString().trim());
            ++this.parseState;
            this.current.setLength(0);
            return;
         }

         return;
      }
   }

   private void parseVersion(ByteBuffer buffer) throws IOException {
      while(true) {
         if (buffer.hasRemaining()) {
            byte b = buffer.get();
            if (b != 32 && b != 9) {
               if (!Character.isDigit(b) && !Character.isAlphabetic(b) && b != 46 && b != 47) {
                  throw new IOException("Invalid response");
               }

               this.current.append((char)b);
               continue;
            }

            this.httpVersion = this.current.toString().trim();
            ++this.parseState;
            this.current.setLength(0);
            return;
         }

         return;
      }
   }

   boolean isComplete() {
      return this.parseState == 5;
   }

   public String getHttpVersion() {
      return this.httpVersion;
   }

   public int getResponseCode() {
      return this.responseCode;
   }

   public String getMessage() {
      return this.message;
   }

   public Map<String, List<String>> getHeaders() {
      return this.headers;
   }
}
