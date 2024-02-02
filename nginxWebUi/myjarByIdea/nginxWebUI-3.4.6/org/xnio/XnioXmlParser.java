package org.xnio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import org.wildfly.client.config.ClientConfiguration;
import org.wildfly.client.config.ConfigXMLParseException;
import org.wildfly.client.config.ConfigurationXMLStreamReader;
import org.wildfly.common.net.CidrAddress;

final class XnioXmlParser {
   private static final String NS_XNIO_3_5 = "urn:xnio:3.5";

   static XnioWorker parseWorker(Xnio xnio) throws ConfigXMLParseException, IOException {
      return parseWorker(xnio, ClientConfiguration.getInstance());
   }

   static XnioWorker parseWorker(Xnio xnio, ClientConfiguration clientConfiguration) throws ConfigXMLParseException, IOException {
      XnioWorker.Builder builder = xnio.createWorkerBuilder();
      if (clientConfiguration == null) {
         return null;
      } else {
         builder.setDaemon(true);
         ConfigurationXMLStreamReader reader = clientConfiguration.readConfiguration(Collections.singleton("urn:xnio:3.5"));
         parseDocument(reader, builder);
         return builder.build();
      }
   }

   private static void parseDocument(ConfigurationXMLStreamReader reader, XnioWorker.Builder workerBuilder) throws ConfigXMLParseException {
      if (reader.hasNext()) {
         switch (reader.nextTag()) {
            case 1:
               checkElementNamespace(reader);
               switch (reader.getLocalName()) {
                  case "worker":
                     parseWorkerElement(reader, workerBuilder);
                     return;
                  default:
                     throw reader.unexpectedElement();
               }
            default:
               throw reader.unexpectedContent();
         }
      }
   }

   private static void parseWorkerElement(ConfigurationXMLStreamReader reader, XnioWorker.Builder workerBuilder) throws ConfigXMLParseException {
      requireNoAttributes(reader);
      int foundBits = 0;

      while(reader.hasNext()) {
         switch (reader.nextTag()) {
            case 1:
               checkElementNamespace(reader);
               switch (reader.getLocalName()) {
                  case "daemon-threads":
                     if (isSet(foundBits, 0)) {
                        throw reader.unexpectedElement();
                     }

                     foundBits = setBit(foundBits, 0);
                     parseDaemonThreads(reader, workerBuilder);
                     continue;
                  case "worker-name":
                     if (isSet(foundBits, 1)) {
                        throw reader.unexpectedElement();
                     }

                     foundBits = setBit(foundBits, 1);
                     parseWorkerName(reader, workerBuilder);
                     continue;
                  case "pool-size":
                     if (isSet(foundBits, 2)) {
                        throw reader.unexpectedElement();
                     }

                     foundBits = setBit(foundBits, 2);
                     parsePoolSize(reader, workerBuilder);
                     continue;
                  case "task-keepalive":
                     if (isSet(foundBits, 3)) {
                        throw reader.unexpectedElement();
                     }

                     foundBits = setBit(foundBits, 3);
                     parseTaskKeepalive(reader, workerBuilder);
                     continue;
                  case "io-threads":
                     if (isSet(foundBits, 4)) {
                        throw reader.unexpectedElement();
                     }

                     foundBits = setBit(foundBits, 4);
                     parseIoThreads(reader, workerBuilder);
                     continue;
                  case "stack-size":
                     if (isSet(foundBits, 5)) {
                        throw reader.unexpectedElement();
                     }

                     foundBits = setBit(foundBits, 5);
                     parseStackSize(reader, workerBuilder);
                     continue;
                  case "outbound-bind-addresses":
                     if (isSet(foundBits, 6)) {
                        throw reader.unexpectedElement();
                     }

                     foundBits = setBit(foundBits, 6);
                     parseOutboundBindAddresses(reader, workerBuilder);
                     continue;
                  default:
                     throw reader.unexpectedElement();
               }
            case 2:
               return;
         }
      }

      throw reader.unexpectedDocumentEnd();
   }

   private static void parseDaemonThreads(ConfigurationXMLStreamReader reader, XnioWorker.Builder workerBuilder) throws ConfigXMLParseException {
      requireSingleAttribute(reader, "value");
      boolean daemon = reader.getBooleanAttributeValueResolved(0);
      if (!reader.hasNext()) {
         throw reader.unexpectedDocumentEnd();
      } else if (reader.nextTag() != 2) {
         throw reader.unexpectedElement();
      } else {
         workerBuilder.setDaemon(daemon);
      }
   }

   private static void parseWorkerName(ConfigurationXMLStreamReader reader, XnioWorker.Builder workerBuilder) throws ConfigXMLParseException {
      requireSingleAttribute(reader, "value");
      String name = reader.getAttributeValueResolved(0);
      if (!reader.hasNext()) {
         throw reader.unexpectedDocumentEnd();
      } else if (reader.nextTag() != 2) {
         throw reader.unexpectedElement();
      } else {
         workerBuilder.setWorkerName(name);
      }
   }

   private static void parsePoolSize(ConfigurationXMLStreamReader reader, XnioWorker.Builder workerBuilder) throws ConfigXMLParseException {
      requireSingleAttribute(reader, "max-threads");
      int threadCount = reader.getIntAttributeValueResolved(0);
      if (!reader.hasNext()) {
         throw reader.unexpectedDocumentEnd();
      } else if (reader.nextTag() != 2) {
         throw reader.unexpectedElement();
      } else {
         workerBuilder.setCoreWorkerPoolSize(threadCount);
         workerBuilder.setMaxWorkerPoolSize(threadCount);
      }
   }

   private static void parseTaskKeepalive(ConfigurationXMLStreamReader reader, XnioWorker.Builder workerBuilder) throws ConfigXMLParseException {
      requireSingleAttribute(reader, "value");
      int duration = reader.getIntAttributeValueResolved(0);
      if (!reader.hasNext()) {
         throw reader.unexpectedDocumentEnd();
      } else if (reader.nextTag() != 2) {
         throw reader.unexpectedElement();
      } else {
         workerBuilder.setWorkerKeepAlive(duration);
      }
   }

   private static void parseIoThreads(ConfigurationXMLStreamReader reader, XnioWorker.Builder workerBuilder) throws ConfigXMLParseException {
      requireSingleAttribute(reader, "value");
      int threadCount = reader.getIntAttributeValueResolved(0);
      if (!reader.hasNext()) {
         throw reader.unexpectedDocumentEnd();
      } else if (reader.nextTag() != 2) {
         throw reader.unexpectedElement();
      } else {
         workerBuilder.setWorkerIoThreads(threadCount);
      }
   }

   private static void parseStackSize(ConfigurationXMLStreamReader reader, XnioWorker.Builder workerBuilder) throws ConfigXMLParseException {
      requireSingleAttribute(reader, "value");
      long stackSize = reader.getLongAttributeValueResolved(0);
      if (!reader.hasNext()) {
         throw reader.unexpectedDocumentEnd();
      } else if (reader.nextTag() != 2) {
         throw reader.unexpectedElement();
      } else {
         workerBuilder.setWorkerStackSize(stackSize);
      }
   }

   private static void parseOutboundBindAddresses(ConfigurationXMLStreamReader reader, XnioWorker.Builder workerBuilder) throws ConfigXMLParseException {
      requireNoAttributes(reader);

      while(reader.hasNext()) {
         if (reader.nextTag() != 1) {
            assert reader.getEventType() == 2;

            return;
         }

         checkElementNamespace(reader);
         if (!reader.getLocalName().equals("bind-address")) {
            throw reader.unexpectedElement();
         }

         parseBindAddress(reader, workerBuilder);
      }

      throw reader.unexpectedDocumentEnd();
   }

   private static void parseBindAddress(ConfigurationXMLStreamReader reader, XnioWorker.Builder workerBuilder) throws ConfigXMLParseException {
      int cnt = reader.getAttributeCount();
      InetAddress address = null;
      int port = 0;
      CidrAddress match = null;

      for(int i = 0; i < cnt; ++i) {
         checkAttributeNamespace(reader, i);
         switch (reader.getAttributeLocalName(i)) {
            case "match":
               match = reader.getCidrAddressAttributeValueResolved(i);
               break;
            case "bind-address":
               address = reader.getInetAddressAttributeValueResolved(i);
               break;
            case "bind-port":
               port = reader.getIntAttributeValueResolved(i, 0, 65535);
               break;
            default:
               throw reader.unexpectedAttribute(i);
         }
      }

      if (match == null) {
         throw reader.missingRequiredAttribute((String)null, "match");
      } else if (address == null) {
         throw reader.missingRequiredAttribute((String)null, "bind-address");
      } else {
         workerBuilder.addBindAddressConfiguration(match, new InetSocketAddress(address, port));
         if (!reader.hasNext()) {
            throw reader.unexpectedDocumentEnd();
         } else if (reader.nextTag() != 2) {
            throw reader.unexpectedElement();
         }
      }
   }

   private static void checkElementNamespace(ConfigurationXMLStreamReader reader) throws ConfigXMLParseException {
      if (!reader.getNamespaceURI().equals("urn:xnio:3.5")) {
         throw reader.unexpectedElement();
      }
   }

   private static void checkAttributeNamespace(ConfigurationXMLStreamReader reader, int idx) throws ConfigXMLParseException {
      String attributeNamespace = reader.getAttributeNamespace(idx);
      if (attributeNamespace != null && !attributeNamespace.isEmpty()) {
         throw reader.unexpectedAttribute(idx);
      }
   }

   private static void requireNoAttributes(ConfigurationXMLStreamReader reader) throws ConfigXMLParseException {
      int attributeCount = reader.getAttributeCount();
      if (attributeCount > 0) {
         throw reader.unexpectedAttribute(0);
      }
   }

   private static void requireSingleAttribute(ConfigurationXMLStreamReader reader, String attributeName) throws ConfigXMLParseException {
      int attributeCount = reader.getAttributeCount();
      if (attributeCount < 1) {
         throw reader.missingRequiredAttribute("", attributeName);
      } else {
         checkAttributeNamespace(reader, 0);
         if (!reader.getAttributeLocalName(0).equals(attributeName)) {
            throw reader.unexpectedAttribute(0);
         } else if (attributeCount > 1) {
            throw reader.unexpectedAttribute(1);
         }
      }
   }

   private static boolean isSet(int var, int bit) {
      return (var & 1 << bit) != 0;
   }

   private static int setBit(int var, int bit) {
      return var | 1 << bit;
   }
}
