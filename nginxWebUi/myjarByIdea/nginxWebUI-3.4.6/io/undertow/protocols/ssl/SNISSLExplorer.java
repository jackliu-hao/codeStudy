package io.undertow.protocols.ssl;

import io.undertow.UndertowMessages;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLException;

final class SNISSLExplorer {
   public static final int RECORD_HEADER_SIZE = 5;

   private SNISSLExplorer() {
   }

   public static int getRequiredSize(ByteBuffer source) {
      ByteBuffer input = source.duplicate();
      if (input.remaining() < 5) {
         throw new BufferUnderflowException();
      } else {
         byte firstByte = input.get();
         input.get();
         byte thirdByte = input.get();
         return (firstByte & 128) != 0 && thirdByte == 1 ? 5 : ((input.get() & 255) << 8 | input.get() & 255) + 5;
      }
   }

   public static int getRequiredSize(byte[] source, int offset, int length) throws IOException {
      ByteBuffer byteBuffer = ByteBuffer.wrap(source, offset, length).asReadOnlyBuffer();
      return getRequiredSize(byteBuffer);
   }

   public static List<SNIServerName> explore(ByteBuffer source) throws SSLException {
      ByteBuffer input = source.duplicate();
      if (input.remaining() < 5) {
         throw new BufferUnderflowException();
      } else {
         byte firstByte = input.get();
         byte secondByte = input.get();
         byte thirdByte = input.get();
         if ((firstByte & 128) != 0 && thirdByte == 1) {
            return Collections.emptyList();
         } else if (firstByte == 22) {
            return exploreTLSRecord(input, firstByte, secondByte, thirdByte);
         } else {
            throw UndertowMessages.MESSAGES.notHandshakeRecord();
         }
      }
   }

   public static List<SNIServerName> explore(byte[] source, int offset, int length) throws IOException {
      ByteBuffer byteBuffer = ByteBuffer.wrap(source, offset, length).asReadOnlyBuffer();
      return explore(byteBuffer);
   }

   private static List<SNIServerName> exploreTLSRecord(ByteBuffer input, byte firstByte, byte secondByte, byte thirdByte) throws SSLException {
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

   private static List<SNIServerName> exploreHandshake(ByteBuffer input, byte recordMajorVersion, byte recordMinorVersion, int recordLength) throws SSLException {
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
            return exploreClientHello(input, recordMajorVersion, recordMinorVersion);
         }
      }
   }

   private static List<SNIServerName> exploreClientHello(ByteBuffer input, byte recordMajorVersion, byte recordMinorVersion) throws SSLException {
      ExtensionInfo info = null;
      input.get();
      input.get();
      int position = input.position();
      input.position(position + 32);
      ignoreByteVector8(input);

      for(int csLen = getInt16(input); csLen > 0; csLen -= 2) {
         getInt8(input);
         getInt8(input);
      }

      ignoreByteVector8(input);
      if (input.remaining() > 0) {
         info = exploreExtensions(input);
      }

      List<SNIServerName> snList = info != null ? info.sni : Collections.emptyList();
      return snList;
   }

   private static ExtensionInfo exploreExtensions(ByteBuffer input) throws SSLException {
      List<SNIServerName> sni = Collections.emptyList();
      List<String> alpn = Collections.emptyList();

      int extLen;
      for(int length = getInt16(input); length > 0; length -= extLen + 4) {
         int extType = getInt16(input);
         extLen = getInt16(input);
         if (extType == 0) {
            sni = exploreSNIExt(input, extLen);
         } else if (extType == 16) {
            alpn = exploreALPN(input, extLen);
         } else {
            ignoreByteVector(input, extLen);
         }
      }

      return new ExtensionInfo(sni, alpn);
   }

   private static List<String> exploreALPN(ByteBuffer input, int extLen) throws SSLException {
      ArrayList<String> strings = new ArrayList();
      if (extLen >= 2) {
         int listLen = getInt16(input);
         if (listLen == 0 || listLen + 2 != extLen) {
            throw UndertowMessages.MESSAGES.invalidTlsExt();
         }

         int len;
         for(int rem = extLen - 2; rem > 0; rem -= len + 1) {
            len = getInt8(input);
            if (len > rem) {
               throw UndertowMessages.MESSAGES.notEnoughData();
            }

            byte[] b = new byte[len];
            input.get(b);
            strings.add(new String(b, StandardCharsets.UTF_8));
         }
      }

      return (List)(strings.isEmpty() ? Collections.emptyList() : strings);
   }

   private static List<SNIServerName> exploreSNIExt(ByteBuffer input, int extLen) throws SSLException {
      Map<Integer, SNIServerName> sniMap = new LinkedHashMap();
      int remains = extLen;
      if (extLen >= 2) {
         int listLen = getInt16(input);
         if (listLen == 0 || listLen + 2 != extLen) {
            throw UndertowMessages.MESSAGES.invalidTlsExt();
         }

         byte[] encoded;
         for(remains = extLen - 2; remains > 0; remains -= encoded.length + 3) {
            int code = getInt8(input);
            int snLen = getInt16(input);
            if (snLen > remains) {
               throw UndertowMessages.MESSAGES.notEnoughData();
            }

            encoded = new byte[snLen];
            input.get(encoded);
            Object serverName;
            switch (code) {
               case 0:
                  if (encoded.length == 0) {
                     throw UndertowMessages.MESSAGES.emptyHostNameSni();
                  }

                  serverName = new SNIHostName(encoded);
                  break;
               default:
                  serverName = new UnknownServerName(code, encoded);
            }

            if (sniMap.put(((SNIServerName)serverName).getType(), serverName) != null) {
               throw UndertowMessages.MESSAGES.duplicatedSniServerName(((SNIServerName)serverName).getType());
            }
         }
      } else if (extLen == 0) {
         throw UndertowMessages.MESSAGES.invalidTlsExt();
      }

      if (remains != 0) {
         throw UndertowMessages.MESSAGES.invalidTlsExt();
      } else {
         return Collections.unmodifiableList(new ArrayList(sniMap.values()));
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

   private static void ignoreByteVector8(ByteBuffer input) {
      ignoreByteVector(input, getInt8(input));
   }

   private static void ignoreByteVector(ByteBuffer input, int length) {
      if (length != 0) {
         int position = input.position();
         input.position(position + length);
      }

   }

   static final class ExtensionInfo {
      final List<SNIServerName> sni;
      final List<String> alpn;

      ExtensionInfo(List<SNIServerName> sni, List<String> alpn) {
         this.sni = sni;
         this.alpn = alpn;
      }
   }

   static final class UnknownServerName extends SNIServerName {
      UnknownServerName(int code, byte[] encoded) {
         super(code, encoded);
      }
   }
}
