package io.undertow.protocols.ssl;

import io.undertow.UndertowMessages;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.SSLException;

final class ALPNHackServerHelloExplorer {
   private ALPNHackServerHelloExplorer() {
   }

   static byte[] addAlpnExtensionsToServerHello(byte[] source, String selectedAlpnProtocol) throws SSLException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ByteBuffer input = ByteBuffer.wrap(source);

      try {
         exploreHandshake(input, source.length, new AtomicReference(selectedAlpnProtocol), out);
         int serverHelloLength = out.size() - 4;
         out.write(source, input.position(), input.remaining());
         byte[] data = out.toByteArray();
         data[1] = (byte)(serverHelloLength >> 16 & 255);
         data[2] = (byte)(serverHelloLength >> 8 & 255);
         data[3] = (byte)(serverHelloLength & 255);
         return data;
      } catch (AlpnProcessingException var6) {
         return source;
      }
   }

   static byte[] removeAlpnExtensionsFromServerHello(ByteBuffer source, AtomicReference<String> selectedAlpnProtocol) throws SSLException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      try {
         exploreHandshake(source, source.remaining(), selectedAlpnProtocol, out);
         int serverHelloLength = out.size() - 4;
         byte[] data = out.toByteArray();
         data[1] = (byte)(serverHelloLength >> 16 & 255);
         data[2] = (byte)(serverHelloLength >> 8 & 255);
         data[3] = (byte)(serverHelloLength & 255);
         return data;
      } catch (AlpnProcessingException var5) {
         return null;
      }
   }

   private static void exploreHandshake(ByteBuffer input, int recordLength, AtomicReference<String> selectedAlpnProtocol, ByteArrayOutputStream out) throws SSLException {
      byte handshakeType = input.get();
      if (handshakeType != 2) {
         throw UndertowMessages.MESSAGES.expectedServerHello();
      } else {
         out.write(handshakeType);
         int handshakeLength = getInt24(input);
         out.write(0);
         out.write(0);
         out.write(0);
         if (handshakeLength > recordLength - 4) {
            throw UndertowMessages.MESSAGES.multiRecordSSLHandshake();
         } else {
            int old = input.limit();
            input.limit(handshakeLength + input.position());
            exploreServerHello(input, selectedAlpnProtocol, out);
            input.limit(old);
         }
      }
   }

   private static void exploreServerHello(ByteBuffer input, AtomicReference<String> alpnProtocolReference, ByteArrayOutputStream out) throws SSLException {
      byte helloMajorVersion = input.get();
      byte helloMinorVersion = input.get();
      out.write(helloMajorVersion);
      out.write(helloMinorVersion);

      for(int i = 0; i < 32; ++i) {
         out.write(input.get() & 255);
      }

      processByteVector8(input, out);
      out.write(input.get() & 255);
      out.write(input.get() & 255);
      out.write(input.get() & 255);
      String existingAlpn = null;
      ByteArrayOutputStream extensionsOutput = null;
      if (input.remaining() > 0) {
         extensionsOutput = new ByteArrayOutputStream();
         existingAlpn = exploreExtensions(input, extensionsOutput, alpnProtocolReference.get() == null);
      }

      byte[] existing;
      if (existingAlpn != null) {
         if (alpnProtocolReference.get() != null) {
            throw new AlpnProcessingException();
         }

         alpnProtocolReference.set(existingAlpn);
         existing = extensionsOutput.toByteArray();
         out.write(existing, 0, existing.length);
      } else if (alpnProtocolReference.get() != null) {
         String selectedAlpnProtocol = (String)alpnProtocolReference.get();
         ByteArrayOutputStream alpnBits = new ByteArrayOutputStream();
         alpnBits.write(0);
         alpnBits.write(16);
         int length = 3 + selectedAlpnProtocol.length();
         alpnBits.write(length >> 8 & 255);
         alpnBits.write(length & 255);
         length -= 2;
         alpnBits.write(length >> 8 & 255);
         alpnBits.write(length & 255);
         alpnBits.write(selectedAlpnProtocol.length() & 255);

         int al;
         for(al = 0; al < selectedAlpnProtocol.length(); ++al) {
            alpnBits.write(selectedAlpnProtocol.charAt(al) & 255);
         }

         if (extensionsOutput != null) {
            byte[] existing = extensionsOutput.toByteArray();
            int newLength = existing.length - 2 + alpnBits.size();
            existing[0] = (byte)(newLength >> 8 & 255);
            existing[1] = (byte)(newLength & 255);

            try {
               out.write(existing);
               out.write(alpnBits.toByteArray());
            } catch (IOException var14) {
               throw new RuntimeException(var14);
            }
         } else {
            al = alpnBits.size();
            out.write(al >> 8 & 255);
            out.write(al & 255);

            try {
               out.write(alpnBits.toByteArray());
            } catch (IOException var13) {
               throw new RuntimeException(var13);
            }
         }
      } else if (extensionsOutput != null) {
         existing = extensionsOutput.toByteArray();
         out.write(existing, 0, existing.length);
      }

   }

   static List<ByteBuffer> extractRecords(ByteBuffer data) {
      List<ByteBuffer> ret = new ArrayList();

      while(data.hasRemaining()) {
         byte d1 = data.get();
         byte d2 = data.get();
         byte d3 = data.get();
         byte d4 = data.get();
         byte d5 = data.get();
         int length = (d4 & 255) << 8 | d5 & 255;
         byte[] b = new byte[length + 5];
         b[0] = d1;
         b[1] = d2;
         b[2] = d3;
         b[3] = d4;
         b[4] = d5;
         data.get(b, 5, length);
         ret.add(ByteBuffer.wrap(b));
      }

      return ret;
   }

   private static String exploreExtensions(ByteBuffer input, ByteArrayOutputStream extensionOut, boolean removeAlpn) throws SSLException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      String ret = null;
      int length = getInt16(input);
      out.write(length >> 8 & 255);
      out.write(length & 255);

      int originalLength;
      int extLen;
      for(originalLength = length; length > 0; length -= extLen + 4) {
         int extType = getInt16(input);
         extLen = getInt16(input);
         if (extType == 16) {
            int vlen = getInt16(input);
            ret = readByteVector8(input);
            if (!removeAlpn) {
               out.write(extType >> 8 & 255);
               out.write(extType & 255);
               out.write(extLen >> 8 & 255);
               out.write(extLen & 255);
               out.write(vlen >> 8 & 255);
               out.write(vlen & 255);
               out.write(ret.length() & 255);

               for(int i = 0; i < ret.length(); ++i) {
                  out.write(ret.charAt(i) & 255);
               }
            } else {
               originalLength -= 6;
               originalLength -= vlen;
            }
         } else {
            out.write(extType >> 8 & 255);
            out.write(extType & 255);
            out.write(extLen >> 8 & 255);
            out.write(extLen & 255);
            processByteVector(input, extLen, out);
         }
      }

      if (removeAlpn && ret == null) {
         throw new AlpnProcessingException();
      } else {
         byte[] data = out.toByteArray();
         data[0] = (byte)(originalLength >> 8 & 255);
         data[1] = (byte)(originalLength & 255);
         extensionOut.write(data, 0, data.length);
         return ret;
      }
   }

   private static String readByteVector8(ByteBuffer input) {
      int length = getInt8(input);
      byte[] data = new byte[length];
      input.get(data);
      return new String(data, StandardCharsets.US_ASCII);
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
      out.write(int8 & 255);
      processByteVector(input, int8, out);
   }

   private static void processByteVector(ByteBuffer input, int length, ByteArrayOutputStream out) {
      for(int i = 0; i < length; ++i) {
         out.write(input.get() & 255);
      }

   }

   static ByteBuffer createNewOutputRecords(byte[] newFirstMessage, List<ByteBuffer> records) {
      int length = newFirstMessage.length;
      length += 5;

      ByteBuffer ret;
      for(int i = 1; i < records.size(); ++i) {
         ret = (ByteBuffer)records.get(i);
         length += ret.remaining();
      }

      byte[] newData = new byte[length];
      ret = ByteBuffer.wrap(newData);
      ByteBuffer oldHello = (ByteBuffer)records.get(0);
      ret.put(oldHello.get());
      ret.put(oldHello.get());
      ret.put(oldHello.get());
      ret.put((byte)(newFirstMessage.length >> 8 & 255));
      ret.put((byte)(newFirstMessage.length & 255));
      ret.put(newFirstMessage);

      for(int i = 1; i < records.size(); ++i) {
         ByteBuffer rec = (ByteBuffer)records.get(i);
         ret.put(rec);
      }

      ret.flip();
      return ret;
   }

   private static final class AlpnProcessingException extends RuntimeException {
      private AlpnProcessingException() {
      }

      // $FF: synthetic method
      AlpnProcessingException(Object x0) {
         this();
      }
   }
}
