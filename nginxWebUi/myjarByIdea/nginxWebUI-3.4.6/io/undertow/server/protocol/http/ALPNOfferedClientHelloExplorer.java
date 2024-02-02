package io.undertow.server.protocol.http;

import io.undertow.UndertowMessages;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLException;

final class ALPNOfferedClientHelloExplorer {
   private static final int RECORD_HEADER_SIZE = 5;

   private ALPNOfferedClientHelloExplorer() {
   }

   static boolean isIncompleteHeader(ByteBuffer source) {
      return source.remaining() < 5;
   }

   static List<Integer> parseClientHello(ByteBuffer source) throws SSLException {
      ByteBuffer input = source.duplicate();
      if (isIncompleteHeader(input)) {
         throw new BufferUnderflowException();
      } else {
         byte firstByte = input.get();
         byte secondByte = input.get();
         byte thirdByte = input.get();
         if ((firstByte & 128) != 0 && thirdByte == 1) {
            return null;
         } else {
            return firstByte == 22 && secondByte == 3 && thirdByte >= 1 && thirdByte <= 3 ? exploreTLSRecord(input, firstByte, secondByte, thirdByte) : null;
         }
      }
   }

   private static List<Integer> exploreTLSRecord(ByteBuffer input, byte firstByte, byte secondByte, byte thirdByte) throws SSLException {
      if (firstByte != 22) {
         throw UndertowMessages.MESSAGES.notHandshakeRecord();
      } else {
         int recordLength = getInt16(input);
         if (recordLength > input.remaining()) {
            throw new BufferUnderflowException();
         } else {
            try {
               return exploreHandshake(input, secondByte, thirdByte, recordLength);
            } catch (BufferUnderflowException var6) {
               throw UndertowMessages.MESSAGES.invalidHandshakeRecord();
            }
         }
      }
   }

   private static List<Integer> exploreHandshake(ByteBuffer input, byte recordMajorVersion, byte recordMinorVersion, int recordLength) throws SSLException {
      byte handshakeType = input.get();
      if (handshakeType != 1) {
         throw UndertowMessages.MESSAGES.expectedClientHello();
      } else {
         int handshakeLength = getInt24(input);
         if (handshakeLength > recordLength - 4) {
            throw UndertowMessages.MESSAGES.multiRecordSSLHandshake();
         } else {
            input = input.duplicate();
            input.limit(handshakeLength + input.position());
            return exploreRecord(input);
         }
      }
   }

   private static List<Integer> exploreRecord(ByteBuffer input) throws SSLException {
      byte helloMajorVersion = input.get();
      byte helloMinorVersion = input.get();
      if (helloMajorVersion != 3 && helloMinorVersion != 3) {
         return null;
      } else {
         int int16;
         for(int16 = 0; int16 < 32; ++int16) {
            byte var4 = input.get();
         }

         processByteVector8(input);
         int16 = getInt16(input);
         List<Integer> ciphers = new ArrayList();

         for(int i = 0; i < int16; i += 2) {
            ciphers.add(getInt16(input));
         }

         processByteVector8(input);
         return input.remaining() > 0 ? exploreExtensions(input, ciphers) : null;
      }
   }

   private static List<Integer> exploreExtensions(ByteBuffer input, List<Integer> ciphers) throws SSLException {
      int extLen;
      for(int length = getInt16(input); length > 0; length -= extLen + 4) {
         int extType = getInt16(input);
         extLen = getInt16(input);
         if (extType == 16) {
            return ciphers;
         }

         processByteVector(input, extLen);
      }

      return null;
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

   private static void processByteVector8(ByteBuffer input) {
      int int8 = getInt8(input);
      processByteVector(input, int8);
   }

   private static void processByteVector(ByteBuffer input, int length) {
      for(int i = 0; i < length; ++i) {
         byte var3 = input.get();
      }

   }
}
