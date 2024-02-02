/*     */ package org.xnio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.Collections;
/*     */ import org.wildfly.client.config.ClientConfiguration;
/*     */ import org.wildfly.client.config.ConfigXMLParseException;
/*     */ import org.wildfly.client.config.ConfigurationXMLStreamReader;
/*     */ import org.wildfly.common.net.CidrAddress;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class XnioXmlParser
/*     */ {
/*     */   private static final String NS_XNIO_3_5 = "urn:xnio:3.5";
/*     */   
/*     */   static XnioWorker parseWorker(Xnio xnio) throws ConfigXMLParseException, IOException {
/*  43 */     return parseWorker(xnio, ClientConfiguration.getInstance());
/*     */   }
/*     */   
/*     */   static XnioWorker parseWorker(Xnio xnio, ClientConfiguration clientConfiguration) throws ConfigXMLParseException, IOException {
/*  47 */     XnioWorker.Builder builder = xnio.createWorkerBuilder();
/*  48 */     if (clientConfiguration == null) {
/*  49 */       return null;
/*     */     }
/*  51 */     builder.setDaemon(true);
/*  52 */     ConfigurationXMLStreamReader reader = clientConfiguration.readConfiguration(Collections.singleton("urn:xnio:3.5"));
/*     */     
/*  54 */     parseDocument(reader, builder);
/*     */     
/*  56 */     return builder.build();
/*     */   }
/*     */   
/*     */   private static void parseDocument(ConfigurationXMLStreamReader reader, XnioWorker.Builder workerBuilder) throws ConfigXMLParseException {
/*  60 */     if (reader.hasNext()) { switch (reader.nextTag()) {
/*     */         case 1:
/*  62 */           checkElementNamespace(reader);
/*  63 */           switch (reader.getLocalName()) {
/*     */             case "worker":
/*  65 */               parseWorkerElement(reader, workerBuilder);
/*     */               return;
/*     */           } 
/*  68 */           throw reader.unexpectedElement();
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/*  73 */       throw reader.unexpectedContent(); }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   private static void parseWorkerElement(ConfigurationXMLStreamReader reader, XnioWorker.Builder workerBuilder) throws ConfigXMLParseException {
/*  79 */     requireNoAttributes(reader);
/*  80 */     int foundBits = 0;
/*     */     
/*  82 */     while (reader.hasNext()) {
/*  83 */       switch (reader.nextTag()) {
/*     */         case 2:
/*     */           return;
/*     */         
/*     */         case 1:
/*  88 */           checkElementNamespace(reader);
/*  89 */           switch (reader.getLocalName()) {
/*     */             case "daemon-threads":
/*  91 */               if (isSet(foundBits, 0)) throw reader.unexpectedElement(); 
/*  92 */               foundBits = setBit(foundBits, 0);
/*  93 */               parseDaemonThreads(reader, workerBuilder);
/*     */               continue;
/*     */             
/*     */             case "worker-name":
/*  97 */               if (isSet(foundBits, 1)) throw reader.unexpectedElement(); 
/*  98 */               foundBits = setBit(foundBits, 1);
/*  99 */               parseWorkerName(reader, workerBuilder);
/*     */               continue;
/*     */             
/*     */             case "pool-size":
/* 103 */               if (isSet(foundBits, 2)) throw reader.unexpectedElement(); 
/* 104 */               foundBits = setBit(foundBits, 2);
/* 105 */               parsePoolSize(reader, workerBuilder);
/*     */               continue;
/*     */             
/*     */             case "task-keepalive":
/* 109 */               if (isSet(foundBits, 3)) throw reader.unexpectedElement(); 
/* 110 */               foundBits = setBit(foundBits, 3);
/* 111 */               parseTaskKeepalive(reader, workerBuilder);
/*     */               continue;
/*     */             
/*     */             case "io-threads":
/* 115 */               if (isSet(foundBits, 4)) throw reader.unexpectedElement(); 
/* 116 */               foundBits = setBit(foundBits, 4);
/* 117 */               parseIoThreads(reader, workerBuilder);
/*     */               continue;
/*     */             
/*     */             case "stack-size":
/* 121 */               if (isSet(foundBits, 5)) throw reader.unexpectedElement(); 
/* 122 */               foundBits = setBit(foundBits, 5);
/* 123 */               parseStackSize(reader, workerBuilder);
/*     */               continue;
/*     */             
/*     */             case "outbound-bind-addresses":
/* 127 */               if (isSet(foundBits, 6)) throw reader.unexpectedElement(); 
/* 128 */               foundBits = setBit(foundBits, 6);
/* 129 */               parseOutboundBindAddresses(reader, workerBuilder);
/*     */               continue;
/*     */           } 
/*     */           
/* 133 */           throw reader.unexpectedElement();
/*     */       } 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/* 139 */     throw reader.unexpectedDocumentEnd();
/*     */   }
/*     */   
/*     */   private static void parseDaemonThreads(ConfigurationXMLStreamReader reader, XnioWorker.Builder workerBuilder) throws ConfigXMLParseException {
/* 143 */     requireSingleAttribute(reader, "value");
/* 144 */     boolean daemon = reader.getBooleanAttributeValueResolved(0);
/* 145 */     if (!reader.hasNext()) throw reader.unexpectedDocumentEnd(); 
/* 146 */     if (reader.nextTag() != 2) throw reader.unexpectedElement(); 
/* 147 */     workerBuilder.setDaemon(daemon);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void parseWorkerName(ConfigurationXMLStreamReader reader, XnioWorker.Builder workerBuilder) throws ConfigXMLParseException {
/* 152 */     requireSingleAttribute(reader, "value");
/* 153 */     String name = reader.getAttributeValueResolved(0);
/* 154 */     if (!reader.hasNext()) throw reader.unexpectedDocumentEnd(); 
/* 155 */     if (reader.nextTag() != 2) throw reader.unexpectedElement(); 
/* 156 */     workerBuilder.setWorkerName(name);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void parsePoolSize(ConfigurationXMLStreamReader reader, XnioWorker.Builder workerBuilder) throws ConfigXMLParseException {
/* 161 */     requireSingleAttribute(reader, "max-threads");
/* 162 */     int threadCount = reader.getIntAttributeValueResolved(0);
/* 163 */     if (!reader.hasNext()) throw reader.unexpectedDocumentEnd(); 
/* 164 */     if (reader.nextTag() != 2) throw reader.unexpectedElement(); 
/* 165 */     workerBuilder.setCoreWorkerPoolSize(threadCount);
/* 166 */     workerBuilder.setMaxWorkerPoolSize(threadCount);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void parseTaskKeepalive(ConfigurationXMLStreamReader reader, XnioWorker.Builder workerBuilder) throws ConfigXMLParseException {
/* 171 */     requireSingleAttribute(reader, "value");
/* 172 */     int duration = reader.getIntAttributeValueResolved(0);
/* 173 */     if (!reader.hasNext()) throw reader.unexpectedDocumentEnd(); 
/* 174 */     if (reader.nextTag() != 2) throw reader.unexpectedElement(); 
/* 175 */     workerBuilder.setWorkerKeepAlive(duration);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void parseIoThreads(ConfigurationXMLStreamReader reader, XnioWorker.Builder workerBuilder) throws ConfigXMLParseException {
/* 180 */     requireSingleAttribute(reader, "value");
/* 181 */     int threadCount = reader.getIntAttributeValueResolved(0);
/* 182 */     if (!reader.hasNext()) throw reader.unexpectedDocumentEnd(); 
/* 183 */     if (reader.nextTag() != 2) throw reader.unexpectedElement(); 
/* 184 */     workerBuilder.setWorkerIoThreads(threadCount);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void parseStackSize(ConfigurationXMLStreamReader reader, XnioWorker.Builder workerBuilder) throws ConfigXMLParseException {
/* 189 */     requireSingleAttribute(reader, "value");
/* 190 */     long stackSize = reader.getLongAttributeValueResolved(0);
/* 191 */     if (!reader.hasNext()) throw reader.unexpectedDocumentEnd(); 
/* 192 */     if (reader.nextTag() != 2) throw reader.unexpectedElement(); 
/* 193 */     workerBuilder.setWorkerStackSize(stackSize);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void parseOutboundBindAddresses(ConfigurationXMLStreamReader reader, XnioWorker.Builder workerBuilder) throws ConfigXMLParseException {
/* 198 */     requireNoAttributes(reader);
/*     */     while (true) {
/* 200 */       if (!reader.hasNext()) throw reader.unexpectedDocumentEnd(); 
/* 201 */       if (reader.nextTag() == 1) {
/* 202 */         checkElementNamespace(reader);
/* 203 */         if (!reader.getLocalName().equals("bind-address")) {
/* 204 */           throw reader.unexpectedElement();
/*     */         }
/* 206 */         parseBindAddress(reader, workerBuilder); continue;
/*     */       }  break;
/* 208 */     }  assert reader.getEventType() == 2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void parseBindAddress(ConfigurationXMLStreamReader reader, XnioWorker.Builder workerBuilder) throws ConfigXMLParseException {
/* 215 */     int cnt = reader.getAttributeCount();
/* 216 */     InetAddress address = null;
/* 217 */     int port = 0;
/* 218 */     CidrAddress match = null;
/* 219 */     for (int i = 0; i < cnt; i++) {
/* 220 */       checkAttributeNamespace(reader, i);
/* 221 */       switch (reader.getAttributeLocalName(i)) {
/*     */         case "match":
/* 223 */           match = reader.getCidrAddressAttributeValueResolved(i);
/*     */           break;
/*     */         
/*     */         case "bind-address":
/* 227 */           address = reader.getInetAddressAttributeValueResolved(i);
/*     */           break;
/*     */         
/*     */         case "bind-port":
/* 231 */           port = reader.getIntAttributeValueResolved(i, 0, 65535);
/*     */           break;
/*     */         
/*     */         default:
/* 235 */           throw reader.unexpectedAttribute(i);
/*     */       } 
/*     */     
/*     */     } 
/* 239 */     if (match == null) throw reader.missingRequiredAttribute(null, "match"); 
/* 240 */     if (address == null) throw reader.missingRequiredAttribute(null, "bind-address"); 
/* 241 */     workerBuilder.addBindAddressConfiguration(match, new InetSocketAddress(address, port));
/* 242 */     if (!reader.hasNext()) throw reader.unexpectedDocumentEnd(); 
/* 243 */     if (reader.nextTag() != 2) throw reader.unexpectedElement(); 
/*     */   }
/*     */   
/*     */   private static void checkElementNamespace(ConfigurationXMLStreamReader reader) throws ConfigXMLParseException {
/* 247 */     if (!reader.getNamespaceURI().equals("urn:xnio:3.5")) {
/* 248 */       throw reader.unexpectedElement();
/*     */     }
/*     */   }
/*     */   
/*     */   private static void checkAttributeNamespace(ConfigurationXMLStreamReader reader, int idx) throws ConfigXMLParseException {
/* 253 */     String attributeNamespace = reader.getAttributeNamespace(idx);
/* 254 */     if (attributeNamespace != null && !attributeNamespace.isEmpty()) {
/* 255 */       throw reader.unexpectedAttribute(idx);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void requireNoAttributes(ConfigurationXMLStreamReader reader) throws ConfigXMLParseException {
/* 260 */     int attributeCount = reader.getAttributeCount();
/* 261 */     if (attributeCount > 0) {
/* 262 */       throw reader.unexpectedAttribute(0);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void requireSingleAttribute(ConfigurationXMLStreamReader reader, String attributeName) throws ConfigXMLParseException {
/* 267 */     int attributeCount = reader.getAttributeCount();
/* 268 */     if (attributeCount < 1) {
/* 269 */       throw reader.missingRequiredAttribute("", attributeName);
/*     */     }
/* 271 */     checkAttributeNamespace(reader, 0);
/* 272 */     if (!reader.getAttributeLocalName(0).equals(attributeName)) {
/* 273 */       throw reader.unexpectedAttribute(0);
/*     */     }
/* 275 */     if (attributeCount > 1) {
/* 276 */       throw reader.unexpectedAttribute(1);
/*     */     }
/*     */   }
/*     */   
/*     */   private static boolean isSet(int var, int bit) {
/* 281 */     return ((var & 1 << bit) != 0);
/*     */   }
/*     */   
/*     */   private static int setBit(int var, int bit) {
/* 285 */     return var | 1 << bit;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\XnioXmlParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */