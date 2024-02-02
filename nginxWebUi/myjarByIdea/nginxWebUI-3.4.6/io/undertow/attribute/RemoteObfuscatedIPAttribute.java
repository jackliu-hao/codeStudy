package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.NetworkUtils;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class RemoteObfuscatedIPAttribute implements ExchangeAttribute {
   public static final String REMOTE_OBFUSCATED_IP_SHORT = "%o";
   public static final String REMOTE_OBFUSCATED_IP = "%{REMOTE_OBFUSCATED_IP}";
   public static final ExchangeAttribute INSTANCE = new RemoteObfuscatedIPAttribute();

   private RemoteObfuscatedIPAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      InetSocketAddress sourceAddress = exchange.getSourceAddress();
      InetAddress address = sourceAddress.getAddress();
      if (address == null) {
         address = ((InetSocketAddress)exchange.getConnection().getPeerAddress()).getAddress();
      }

      return address == null ? null : NetworkUtils.toObfuscatedString(address);
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Remote Obfuscated IP", newValue);
   }

   public String toString() {
      return "%{REMOTE_OBFUSCATED_IP}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Remote Obfuscated IP";
      }

      public ExchangeAttribute build(String token) {
         return !token.equals("%{REMOTE_OBFUSCATED_IP}") && !token.equals("%o") ? null : RemoteObfuscatedIPAttribute.INSTANCE;
      }

      public int priority() {
         return 0;
      }
   }
}
