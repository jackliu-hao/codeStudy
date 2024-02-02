package io.undertow.protocols.ssl;

import io.undertow.UndertowMessages;
import java.io.ByteArrayOutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.SSLException;

final class ALPNHackClientHelloExplorer {
   public static final int RECORD_HEADER_SIZE = 5;

   private ALPNHackClientHelloExplorer() {
   }

   static List<String> exploreClientHello(ByteBuffer source) throws SSLException {
      ByteBuffer input = source.duplicate();
      if (input.remaining() < 5) {
         throw new BufferUnderflowException();
      } else {
         List<String> alpnProtocols = new ArrayList();
         byte firstByte = input.get();
         byte secondByte = input.get();
         byte thirdByte = input.get();
         if ((firstByte & 128) != 0 && thirdByte == 1) {
            return null;
         } else if (firstByte == 22) {
            if (secondByte == 3 && thirdByte >= 1 && thirdByte <= 3) {
               exploreTLSRecord(input, firstByte, secondByte, thirdByte, alpnProtocols, (ByteArrayOutputStream)null);
               return alpnProtocols;
            } else {
               return null;
            }
         } else {
            throw UndertowMessages.MESSAGES.notHandshakeRecord();
         }
      }
   }

   static byte[] rewriteClientHello(byte[] source, List<String> alpnProtocols) throws SSLException {
      ByteBuffer input = ByteBuffer.wrap(source);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      if (input.remaining() < 5) {
         throw new BufferUnderflowException();
      } else {
         try {
            byte firstByte = input.get();
            byte secondByte = input.get();
            byte thirdByte = input.get();
            out.write(firstByte & 255);
            out.write(secondByte & 255);
            out.write(thirdByte & 255);
            if ((firstByte & 128) != 0 && thirdByte == 1) {
               return null;
            } else if (firstByte == 22) {
               if (secondByte == 3 && thirdByte == 3) {
                  exploreTLSRecord(input, firstByte, secondByte, thirdByte, alpnProtocols, out);
                  int clientHelloLength = out.size() - 9;
                  byte[] data = out.toByteArray();
                  int newLength = data.length - 5;
                  data[3] = (byte)(newLength >> 8 & 255);
                  data[4] = (byte)(newLength & 255);
                  data[6] = (byte)(clientHelloLength >> 16 & 255);
                  data[7] = (byte)(clientHelloLength >> 8 & 255);
                  data[8] = (byte)(clientHelloLength & 255);
                  return data;
               } else {
                  return null;
               }
            } else {
               throw UndertowMessages.MESSAGES.notHandshakeRecord();
            }
         } catch (ALPNPresentException var10) {
            return null;
         }
      }
   }

   private static void exploreTLSRecord(ByteBuffer input, byte firstByte, byte secondByte, byte thirdByte, List<String> alpnProtocols, ByteArrayOutputStream out) throws SSLException {
      if (firstByte != 22) {
         throw UndertowMessages.MESSAGES.notHandshakeRecord();
      } else {
         int recordLength = getInt16(input);
         if (recordLength > input.remaining()) {
            throw new BufferUnderflowException();
         } else {
            if (out != null) {
               out.write(0);
               out.write(0);
            }

            try {
               exploreHandshake(input, secondByte, thirdByte, recordLength, alpnProtocols, out);
            } catch (BufferUnderflowException var8) {
               throw UndertowMessages.MESSAGES.invalidHandshakeRecord();
            }
         }
      }
   }

   private static void exploreHandshake(ByteBuffer input, byte recordMajorVersion, byte recordMinorVersion, int recordLength, List<String> alpnProtocols, ByteArrayOutputStream out) throws SSLException {
      byte handshakeType = input.get();
      if (handshakeType != 1) {
         throw UndertowMessages.MESSAGES.expectedClientHello();
      } else {
         if (out != null) {
            out.write(handshakeType & 255);
         }

         int handshakeLength = getInt24(input);
         if (out != null) {
            out.write(0);
            out.write(0);
            out.write(0);
         }

         if (handshakeLength > recordLength - 4) {
            throw UndertowMessages.MESSAGES.multiRecordSSLHandshake();
         } else {
            input = input.duplicate();
            input.limit(handshakeLength + input.position());
            exploreClientHello(input, alpnProtocols, out);
         }
      }
   }

   private static void exploreClientHello(ByteBuffer input, List<String> alpnProtocols, ByteArrayOutputStream out) throws SSLException {
      byte helloMajorVersion = input.get();
      byte helloMinorVersion = input.get();
      if (out != null) {
         out.write(helloMajorVersion & 255);
         out.write(helloMinorVersion & 255);
      }

      if (helloMajorVersion == 3 || helloMinorVersion == 3) {
         for(int i = 0; i < 32; ++i) {
            byte d = input.get();
            if (out != null) {
               out.write(d & 255);
            }
         }

         processByteVector8(input, out);
         processByteVector16(input, out);
         processByteVector8(input, out);
         if (input.remaining() > 0) {
            exploreExtensions(input, alpnProtocols, out);
         } else if (out != null) {
            byte[] data = generateAlpnExtension(alpnProtocols);
            writeInt16(out, data.length);
            out.write(data, 0, data.length);
         }

      }
   }

   private static void writeInt16(ByteArrayOutputStream out, int l) {
      if (out != null) {
         out.write(l >> 8 & 255);
         out.write(l & 255);
      }
   }

   private static byte[] generateAlpnExtension(List<String> alpnProtocols) {
      ByteArrayOutputStream alpnBits = new ByteArrayOutputStream();
      alpnBits.write(0);
      alpnBits.write(16);
      int length = 2;

      Iterator var3;
      String p;
      for(var3 = alpnProtocols.iterator(); var3.hasNext(); length += p.length()) {
         p = (String)var3.next();
         ++length;
      }

      writeInt16(alpnBits, length);
      length -= 2;
      writeInt16(alpnBits, length);
      var3 = alpnProtocols.iterator();

      while(var3.hasNext()) {
         p = (String)var3.next();
         alpnBits.write(p.length() & 255);

         for(int i = 0; i < p.length(); ++i) {
            alpnBits.write(p.charAt(i) & 255);
         }
      }

      return alpnBits.toByteArray();
   }

   private static void exploreExtensions(ByteBuffer input, List<String> alpnProtocols, ByteArrayOutputStream out) throws SSLException {
      ByteArrayOutputStream extensionOut = out == null ? null : new ByteArrayOutputStream();
      int length = getInt16(input);
      writeInt16(extensionOut, 0);

      int extLen;
      for(; length > 0; length -= extLen + 4) {
         int extType = getInt16(input);
         writeInt16(extensionOut, extType);
         extLen = getInt16(input);
         writeInt16(extensionOut, extLen);
         if (extType == 16) {
            if (out != null) {
               throw new ALPNPresentException();
            }

            exploreALPNExt(input, alpnProtocols);
         } else {
            processByteVector(input, extLen, extensionOut);
         }
      }

      if (out != null) {
         byte[] alpnBits = generateAlpnExtension(alpnProtocols);
         extensionOut.write(alpnBits, 0, alpnBits.length);
         byte[] extensionsData = extensionOut.toByteArray();
         int newLength = extensionsData.length - 2;
         extensionsData[0] = (byte)(newLength >> 8 & 255);
         extensionsData[1] = (byte)(newLength & 255);
         out.write(extensionsData, 0, extensionsData.length);
      }

   }

   private static void exploreALPNExt(ByteBuffer input, List<String> alpnProtocols) {
      int length = getInt16(input);
      int end = input.position() + length;

      while(input.position() < end) {
         alpnProtocols.add(readByteVector8(input));
      }

   }

   private static int getInt8(ByteBuffer input) {
      return input.get();
   }

   private static int getInt16(ByteBuffer input) {
      return (input.get() & 255) << 8 | input.get() & 255;
   }

   private static int getInt24(ByteBuffer input) {
      return (input.get() & 255) << 16 | (input.get() & 255) << 8 | input.get() & 255;
   }

   private static void processByteVector8(ByteBuffer input, ByteArrayOutputStream out) {
      int int8 = getInt8(input);
      if (out != null) {
         out.write(int8 & 255);
      }

      processByteVector(input, int8, out);
   }

   private static void processByteVector(ByteBuffer input, int length, ByteArrayOutputStream out) {
      for(int i = 0; i < length; ++i) {
         byte b = input.get();
         if (out != null) {
            out.write(b & 255);
         }
      }

   }

   private static String readByteVector8(ByteBuffer input) {
      int length = getInt8(input);
      byte[] data = new byte[length];
      input.get(data);
      return new String(data, StandardCharsets.US_ASCII);
   }

   private static void processByteVector16(ByteBuffer input, ByteArrayOutputStream out) {
      int int16 = getInt16(input);
      writeInt16(out, int16);
      processByteVector(input, int16, out);
   }

   private static final class ALPNPresentException extends RuntimeException {
      private ALPNPresentException() {
      }

      // $FF: synthetic method
      ALPNPresentException(Object x0) {
         this();
      }
   }
}
