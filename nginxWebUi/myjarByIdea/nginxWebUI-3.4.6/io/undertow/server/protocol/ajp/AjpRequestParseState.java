package io.undertow.server.protocol.ajp;

import io.undertow.UndertowLogger;
import io.undertow.server.BasicSSLSessionInfo;
import io.undertow.util.HttpString;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.util.Map;

class AjpRequestParseState {
   public static final int BEGIN = 0;
   public static final int READING_MAGIC_NUMBER = 1;
   public static final int READING_DATA_SIZE = 2;
   public static final int READING_PREFIX_CODE = 3;
   public static final int READING_METHOD = 4;
   public static final int READING_PROTOCOL = 5;
   public static final int READING_REQUEST_URI = 6;
   public static final int READING_REMOTE_ADDR = 7;
   public static final int READING_REMOTE_HOST = 8;
   public static final int READING_SERVER_NAME = 9;
   public static final int READING_SERVER_PORT = 10;
   public static final int READING_IS_SSL = 11;
   public static final int READING_NUM_HEADERS = 12;
   public static final int READING_HEADERS = 13;
   public static final int READING_ATTRIBUTES = 14;
   public static final int DONE = 15;
   int state;
   byte prefix;
   int dataSize;
   int numHeaders = 0;
   HttpString currentHeader;
   String currentAttribute;
   Map<String, String> attributes;
   String remoteAddress;
   int remotePort = -1;
   int serverPort = 80;
   String serverAddress;
   public int stringLength = -1;
   private StringBuilder currentString = new StringBuilder();
   public int currentIntegerPart = -1;
   boolean containsUrlCharacters = false;
   public int readHeaders = 0;
   public String sslSessionId;
   public String sslCipher;
   public String sslCert;
   public String sslKeySize;
   boolean badRequest;
   public boolean containsUnencodedUrlCharacters;

   public void reset() {
      this.stringLength = -1;
      this.currentIntegerPart = -1;
      this.readHeaders = 0;
      this.badRequest = false;
      this.currentString.setLength(0);
      this.containsUnencodedUrlCharacters = false;
   }

   public boolean isComplete() {
      return this.state == 15;
   }

   BasicSSLSessionInfo createSslSessionInfo() {
      String sessionId = this.sslSessionId;
      String cypher = this.sslCipher;
      String cert = this.sslCert;
      Integer keySize = null;
      if (cert == null && sessionId == null) {
         return null;
      } else {
         if (this.sslKeySize != null) {
            try {
               keySize = Integer.parseUnsignedInt(this.sslKeySize);
            } catch (NumberFormatException var8) {
               UndertowLogger.REQUEST_LOGGER.debugf("Invalid sslKeySize %s", this.sslKeySize);
            }
         }

         try {
            return new BasicSSLSessionInfo(sessionId, cypher, cert, keySize);
         } catch (CertificateException var6) {
            return null;
         } catch (javax.security.cert.CertificateException var7) {
            return null;
         }
      }
   }

   InetSocketAddress createPeerAddress() {
      if (this.remoteAddress == null) {
         return null;
      } else {
         int port = this.remotePort > 0 ? this.remotePort : 0;

         try {
            InetAddress address = InetAddress.getByName(this.remoteAddress);
            return new InetSocketAddress(address, port);
         } catch (UnknownHostException var3) {
            return null;
         }
      }
   }

   InetSocketAddress createDestinationAddress() {
      return this.serverAddress == null ? null : InetSocketAddress.createUnresolved(this.serverAddress, this.serverPort);
   }

   public void addStringByte(byte b) {
      this.currentString.append((char)(b & 255));
   }

   public String getStringAndClear() throws UnsupportedEncodingException {
      String ret = this.currentString.toString();
      this.currentString.setLength(0);
      return ret;
   }

   public int getCurrentStringLength() {
      return this.currentString.length();
   }
}
