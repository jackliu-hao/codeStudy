package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import java.util.Arrays;
import java.util.Collections;

public class ExchangeAttributes {
   public static ExchangeAttributeParser parser(ClassLoader classLoader) {
      return new ExchangeAttributeParser(classLoader, Collections.emptyList());
   }

   public static ExchangeAttributeParser parser(ClassLoader classLoader, ExchangeAttributeWrapper... wrappers) {
      return new ExchangeAttributeParser(classLoader, Arrays.asList(wrappers));
   }

   public static ExchangeAttribute cookie(String cookieName) {
      return new CookieAttribute(cookieName);
   }

   public static ExchangeAttribute bytesSent(boolean dashIfZero) {
      return new BytesSentAttribute(dashIfZero);
   }

   public static ExchangeAttribute dateTime() {
      return DateTimeAttribute.INSTANCE;
   }

   public static ExchangeAttribute localIp() {
      return LocalIPAttribute.INSTANCE;
   }

   public static ExchangeAttribute localPort() {
      return LocalPortAttribute.INSTANCE;
   }

   public static ExchangeAttribute localServerName() {
      return LocalServerNameAttribute.INSTANCE;
   }

   public static ExchangeAttribute queryString() {
      return QueryStringAttribute.INSTANCE;
   }

   public static ExchangeAttribute relativePath() {
      return RelativePathAttribute.INSTANCE;
   }

   public static ExchangeAttribute remoteIp() {
      return RemoteIPAttribute.INSTANCE;
   }

   public static ExchangeAttribute remoteObfuscatedIp() {
      return RemoteObfuscatedIPAttribute.INSTANCE;
   }

   public static ExchangeAttribute remoteUser() {
      return RemoteUserAttribute.INSTANCE;
   }

   public static ExchangeAttribute requestHeader(HttpString header) {
      return new RequestHeaderAttribute(header);
   }

   public static ExchangeAttribute requestList() {
      return RequestLineAttribute.INSTANCE;
   }

   public static ExchangeAttribute requestMethod() {
      return RequestMethodAttribute.INSTANCE;
   }

   public static ExchangeAttribute requestProtocol() {
      return RequestProtocolAttribute.INSTANCE;
   }

   public static ExchangeAttribute requestURL() {
      return RequestURLAttribute.INSTANCE;
   }

   public static ExchangeAttribute responseCode() {
      return ResponseCodeAttribute.INSTANCE;
   }

   public static ExchangeAttribute responseReasonPhrase() {
      return ResponseReasonPhraseAttribute.INSTANCE;
   }

   public static ExchangeAttribute responseHeader(HttpString header) {
      return new ResponseHeaderAttribute(header);
   }

   public static ExchangeAttribute transportProtocol() {
      return TransportProtocolAttribute.INSTANCE;
   }

   public static ExchangeAttribute threadName() {
      return ThreadNameAttribute.INSTANCE;
   }

   public static ExchangeAttribute constant(String value) {
      return new ConstantExchangeAttribute(value);
   }

   public static String resolve(HttpServerExchange exchange, ExchangeAttribute[] attributes) {
      StringBuilder result = new StringBuilder();

      for(int i = 0; i < attributes.length; ++i) {
         String str = attributes[i].readAttribute(exchange);
         if (str != null) {
            result.append(str);
         }
      }

      return result.toString();
   }

   private ExchangeAttributes() {
   }

   public static ExchangeAttribute authenticationType() {
      return AuthenticationTypeExchangeAttribute.INSTANCE;
   }
}
