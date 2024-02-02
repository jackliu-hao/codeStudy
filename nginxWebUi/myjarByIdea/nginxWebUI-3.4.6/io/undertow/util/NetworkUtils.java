package io.undertow.util;

import io.undertow.UndertowMessages;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

public class NetworkUtils {
   public static final String IP4_EXACT = "(?:\\d{1,3}\\.){3}\\d{1,3}";
   public static final String IP6_EXACT = "^(?:([0-9a-fA-F]{1,4}:){7,7}(?:[0-9a-fA-F]){1,4}|(?:([0-9a-fA-F]{1,4}:)){1,7}(?:(:))|(?:([0-9a-fA-F]{1,4}:)){1,6}(?:(:[0-9a-fA-F]){1,4})|(?:([0-9a-fA-F]{1,4}:)){1,5}(?:(:[0-9a-fA-F]{1,4})){1,2}|(?:([0-9a-fA-F]{1,4}:)){1,4}(?:(:[0-9a-fA-F]{1,4})){1,3}|(?:([0-9a-fA-F]{1,4}:)){1,3}(?:(:[0-9a-fA-F]{1,4})){1,4}|(?:([0-9a-fA-F]{1,4}:)){1,2}(?:(:[0-9a-fA-F]{1,4})){1,5}|(?:([0-9a-fA-F]{1,4}:))(?:(:[0-9a-fA-F]{1,4})){1,6}|(?:(:))(?:((:[0-9a-fA-F]{1,4}){1,7}|(?:(:)))))$";

   public static String formatPossibleIpv6Address(String address) {
      if (address == null) {
         return null;
      } else if (!address.contains(":")) {
         return address;
      } else {
         return address.startsWith("[") && address.endsWith("]") ? address : "[" + address + "]";
      }
   }

   public static InetAddress parseIpv4Address(String addressString) throws IOException {
      String[] parts = addressString.split("\\.");
      if (parts.length != 4) {
         throw UndertowMessages.MESSAGES.invalidIpAddress(addressString);
      } else {
         byte[] data = new byte[4];

         for(int i = 0; i < 4; ++i) {
            String part = parts[i];
            if (part.length() == 0 || part.charAt(0) == '0' && part.length() > 1) {
               throw UndertowMessages.MESSAGES.invalidIpAddress(addressString);
            }

            data[i] = (byte)Integer.parseInt(part);
         }

         return InetAddress.getByAddress(data);
      }
   }

   public static InetAddress parseIpv6Address(String addressString) throws IOException {
      return InetAddress.getByAddress(parseIpv6AddressToBytes(addressString));
   }

   public static byte[] parseIpv6AddressToBytes(String addressString) throws IOException {
      boolean startsWithColon = addressString.startsWith(":");
      if (startsWithColon && !addressString.startsWith("::")) {
         throw UndertowMessages.MESSAGES.invalidIpAddress(addressString);
      } else {
         String[] parts = (startsWithColon ? addressString.substring(1) : addressString).split(":");
         byte[] data = new byte[16];
         int partOffset = 0;
         boolean seenEmpty = false;
         if (parts.length > 8) {
            throw UndertowMessages.MESSAGES.invalidIpAddress(addressString);
         } else {
            for(int i = 0; i < parts.length; ++i) {
               String part = parts[i];
               if (part.length() > 4) {
                  throw UndertowMessages.MESSAGES.invalidIpAddress(addressString);
               }

               int off;
               if (part.isEmpty()) {
                  if (seenEmpty) {
                     throw UndertowMessages.MESSAGES.invalidIpAddress(addressString);
                  }

                  seenEmpty = true;
                  off = 8 - parts.length;
                  if (off < 0) {
                     throw UndertowMessages.MESSAGES.invalidIpAddress(addressString);
                  }

                  partOffset = off * 2;
               } else {
                  off = Integer.parseInt(part, 16);
                  data[i * 2 + partOffset] = (byte)(off >> 8);
                  data[i * 2 + partOffset + 1] = (byte)off;
               }
            }

            if (parts.length < 8 && !addressString.endsWith("::") && !seenEmpty) {
               throw UndertowMessages.MESSAGES.invalidIpAddress(addressString);
            } else {
               return data;
            }
         }
      }
   }

   public static String toObfuscatedString(InetAddress address) {
      if (address == null) {
         return null;
      } else {
         String s = address.getHostAddress();
         return address instanceof Inet4Address ? s.substring(0, s.lastIndexOf(".") + 1) : s.substring(0, s.indexOf(":", s.indexOf(":") + 1) + 1);
      }
   }

   private NetworkUtils() {
   }
}
