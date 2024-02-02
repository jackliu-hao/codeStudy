package io.undertow.util;

import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class MultipartParser {
   public static final byte HTAB = 9;
   public static final byte CR = 13;
   public static final byte LF = 10;
   public static final byte SP = 32;
   public static final byte DASH = 45;
   private static final byte[] BOUNDARY_PREFIX = new byte[]{13, 10, 45, 45};

   public static ParseState beginParse(ByteBufferPool bufferPool, PartHandler handler, byte[] boundary, String requestCharset) {
      byte[] boundaryToken = new byte[boundary.length + BOUNDARY_PREFIX.length];
      System.arraycopy(BOUNDARY_PREFIX, 0, boundaryToken, 0, BOUNDARY_PREFIX.length);
      System.arraycopy(boundary, 0, boundaryToken, BOUNDARY_PREFIX.length, boundary.length);
      return new ParseState(bufferPool, handler, requestCharset, boundaryToken);
   }

   private static class QuotedPrintableEncoding implements Encoding {
      private final ByteBufferPool bufferPool;
      boolean equalsSeen;
      byte firstCharacter;

      private QuotedPrintableEncoding(ByteBufferPool bufferPool) {
         this.bufferPool = bufferPool;
      }

      public void handle(PartHandler handler, ByteBuffer rawData) throws IOException {
         boolean equalsSeen = this.equalsSeen;
         byte firstCharacter = this.firstCharacter;
         PooledByteBuffer resource = this.bufferPool.allocate();
         ByteBuffer buf = resource.getBuffer();

         try {
            while(rawData.hasRemaining()) {
               byte b = rawData.get();
               if (equalsSeen) {
                  if (firstCharacter == 0) {
                     if (b != 10 && b != 13) {
                        firstCharacter = b;
                     } else {
                        equalsSeen = false;
                     }
                  } else {
                     int result = Character.digit((char)firstCharacter, 16);
                     result <<= 4;
                     result += Character.digit((char)b, 16);
                     buf.put((byte)result);
                     equalsSeen = false;
                     firstCharacter = 0;
                  }
               } else if (b == 61) {
                  equalsSeen = true;
               } else {
                  buf.put(b);
                  if (!buf.hasRemaining()) {
                     buf.flip();
                     handler.data(buf);
                     buf.clear();
                  }
               }
            }

            buf.flip();
            handler.data(buf);
         } finally {
            resource.close();
            this.equalsSeen = equalsSeen;
            this.firstCharacter = firstCharacter;
         }

      }

      // $FF: synthetic method
      QuotedPrintableEncoding(ByteBufferPool x0, Object x1) {
         this(x0);
      }
   }

   private static class Base64Encoding implements Encoding {
      private final FlexBase64.Decoder decoder;
      private final ByteBufferPool bufferPool;

      private Base64Encoding(ByteBufferPool bufferPool) {
         this.decoder = FlexBase64.createDecoder();
         this.bufferPool = bufferPool;
      }

      public void handle(PartHandler handler, ByteBuffer rawData) throws IOException {
         PooledByteBuffer resource = this.bufferPool.allocate();
         ByteBuffer buf = resource.getBuffer();

         try {
            do {
               buf.clear();

               try {
                  this.decoder.decode(rawData, buf);
               } catch (IOException var9) {
                  throw new RuntimeException(var9);
               }

               buf.flip();
               handler.data(buf);
            } while(rawData.hasRemaining());
         } finally {
            resource.close();
         }

      }

      // $FF: synthetic method
      Base64Encoding(ByteBufferPool x0, Object x1) {
         this(x0);
      }
   }

   private static class IdentityEncoding implements Encoding {
      private IdentityEncoding() {
      }

      public void handle(PartHandler handler, ByteBuffer rawData) throws IOException {
         handler.data(rawData);
         rawData.clear();
      }

      // $FF: synthetic method
      IdentityEncoding(Object x0) {
         this();
      }
   }

   private interface Encoding {
      void handle(PartHandler var1, ByteBuffer var2) throws IOException;
   }

   public static class ParseState {
      private final ByteBufferPool bufferPool;
      private final PartHandler partHandler;
      private String requestCharset;
      private final byte[] boundary;
      private int state = 0;
      private int subState = Integer.MAX_VALUE;
      private ByteArrayOutputStream currentString = null;
      private String currentHeaderName = null;
      private HeaderMap headers;
      private Encoding encodingHandler;

      public ParseState(ByteBufferPool bufferPool, PartHandler partHandler, String requestCharset, byte[] boundary) {
         this.bufferPool = bufferPool;
         this.partHandler = partHandler;
         this.requestCharset = requestCharset;
         this.boundary = boundary;
      }

      public void setCharacterEncoding(String encoding) {
         this.requestCharset = encoding;
      }

      public void parse(ByteBuffer buffer) throws IOException {
         while(buffer.hasRemaining()) {
            switch (this.state) {
               case -1:
                  return;
               case 0:
                  this.preamble(buffer);
                  break;
               case 1:
                  this.headerName(buffer);
                  break;
               case 2:
                  this.headerValue(buffer);
                  break;
               case 3:
                  this.entity(buffer);
                  break;
               default:
                  throw new IllegalStateException("" + this.state);
            }
         }

      }

      private void preamble(ByteBuffer buffer) {
         while(buffer.hasRemaining()) {
            byte b = buffer.get();
            if (this.subState >= 0) {
               if (this.subState == Integer.MAX_VALUE) {
                  if (this.boundary[2] == b) {
                     this.subState = 2;
                  } else {
                     this.subState = 0;
                  }
               }

               if (b == this.boundary[this.subState]) {
                  ++this.subState;
                  if (this.subState == this.boundary.length) {
                     this.subState = -1;
                  }
               } else if (b == this.boundary[0]) {
                  this.subState = 1;
               } else {
                  this.subState = 0;
               }
            } else if (this.subState == -1) {
               if (b == 13) {
                  this.subState = -2;
               }
            } else if (this.subState == -2) {
               if (b == 10) {
                  this.subState = 0;
                  this.state = 1;
                  this.headers = new HeaderMap();
                  return;
               }

               this.subState = -1;
            }
         }

      }

      private void headerName(ByteBuffer buffer) throws MalformedMessageException, UnsupportedEncodingException {
         while(buffer.hasRemaining()) {
            byte b = buffer.get();
            if (b == 58) {
               if (this.currentString != null && this.subState == 0) {
                  this.currentHeaderName = new String(this.currentString.toByteArray(), this.requestCharset);
                  this.currentString.reset();
                  this.subState = 0;
                  this.state = 2;
                  return;
               }

               throw new MalformedMessageException();
            }

            if (b == 13) {
               if (this.currentString != null) {
                  throw new MalformedMessageException();
               }

               this.subState = 1;
            } else {
               if (b == 10) {
                  if (this.currentString == null && this.subState == 1) {
                     this.state = 3;
                     this.subState = 0;
                     this.partHandler.beginPart(this.headers);
                     String encoding = this.headers.getFirst(Headers.CONTENT_TRANSFER_ENCODING);
                     if (encoding == null) {
                        this.encodingHandler = new IdentityEncoding();
                     } else if (encoding.equalsIgnoreCase("base64")) {
                        this.encodingHandler = new Base64Encoding(this.bufferPool);
                     } else if (encoding.equalsIgnoreCase("quoted-printable")) {
                        this.encodingHandler = new QuotedPrintableEncoding(this.bufferPool);
                     } else {
                        this.encodingHandler = new IdentityEncoding();
                     }

                     this.headers = null;
                     return;
                  }

                  throw new MalformedMessageException();
               }

               if (this.subState != 0) {
                  throw new MalformedMessageException();
               }

               if (this.currentString == null) {
                  this.currentString = new ByteArrayOutputStream();
               }

               this.currentString.write(b);
            }
         }

      }

      private void headerValue(ByteBuffer buffer) throws MalformedMessageException, UnsupportedEncodingException {
         while(buffer.hasRemaining()) {
            byte b = buffer.get();
            if (this.subState == 2) {
               if (b == 13) {
                  this.headers.put(new HttpString(this.currentHeaderName.trim()), (new String(this.currentString.toByteArray(), this.requestCharset)).trim());
                  this.state = 1;
                  this.subState = 1;
                  this.currentString = null;
                  return;
               }

               if (b != 32 && b != 9) {
                  this.headers.put(new HttpString(this.currentHeaderName.trim()), (new String(this.currentString.toByteArray(), this.requestCharset)).trim());
                  this.state = 1;
                  this.subState = 0;
                  this.currentString = new ByteArrayOutputStream();
                  this.currentString.write(b);
                  return;
               }

               this.currentString.write(b);
               this.subState = 0;
            } else if (b == 13) {
               this.subState = 1;
            } else if (b == 10) {
               if (this.subState != 1) {
                  throw new MalformedMessageException();
               }

               this.subState = 2;
            } else {
               if (this.subState != 0) {
                  throw new MalformedMessageException();
               }

               this.currentString.write(b);
            }
         }

      }

      private void entity(ByteBuffer buffer) throws IOException {
         int startingSubState = this.subState;
         int pos = buffer.position();

         while(buffer.hasRemaining()) {
            byte b = buffer.get();
            if (this.subState >= 0) {
               if (b == this.boundary[this.subState]) {
                  ++this.subState;
                  if (this.subState == this.boundary.length) {
                     startingSubState = 0;
                     ByteBuffer retBuffer = buffer.duplicate();
                     retBuffer.position(pos);
                     retBuffer.limit(Math.max(buffer.position() - this.boundary.length, 0));
                     this.encodingHandler.handle(this.partHandler, retBuffer);
                     this.partHandler.endPart();
                     this.subState = -1;
                  }
               } else if (b == this.boundary[0]) {
                  if (startingSubState > 0) {
                     this.encodingHandler.handle(this.partHandler, ByteBuffer.wrap(this.boundary, 0, startingSubState));
                     startingSubState = 0;
                  }

                  this.subState = 1;
               } else {
                  if (startingSubState > 0) {
                     this.encodingHandler.handle(this.partHandler, ByteBuffer.wrap(this.boundary, 0, startingSubState));
                     startingSubState = 0;
                  }

                  this.subState = 0;
               }
            } else if (this.subState == -1) {
               if (b == 13) {
                  this.subState = -2;
               } else if (b == 45) {
                  this.subState = -3;
               }
            } else if (this.subState == -2) {
               if (b == 10) {
                  this.subState = 0;
                  this.state = 1;
                  this.headers = new HeaderMap();
                  return;
               }

               if (b == 45) {
                  this.subState = -3;
               } else {
                  this.subState = -1;
               }
            } else if (this.subState == -3) {
               if (b == 45) {
                  this.state = -1;
                  return;
               }

               this.subState = -1;
            }
         }

         ByteBuffer retBuffer = buffer.duplicate();
         retBuffer.position(pos);
         if (this.subState == 0) {
            this.encodingHandler.handle(this.partHandler, retBuffer);
         } else if (retBuffer.remaining() > this.subState && this.subState > 0) {
            retBuffer.limit(retBuffer.limit() - this.subState);
            this.encodingHandler.handle(this.partHandler, retBuffer);
         }

      }

      public boolean isComplete() {
         return this.state == -1;
      }
   }

   public interface PartHandler {
      void beginPart(HeaderMap var1);

      void data(ByteBuffer var1) throws IOException;

      void endPart();
   }
}
