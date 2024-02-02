/*     */ package org.xnio.sasl;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.Provider;
/*     */ import java.security.Security;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.ServiceLoader;
/*     */ import java.util.Set;
/*     */ import javax.security.sasl.SaslClient;
/*     */ import javax.security.sasl.SaslClientFactory;
/*     */ import javax.security.sasl.SaslException;
/*     */ import javax.security.sasl.SaslServer;
/*     */ import javax.security.sasl.SaslServerFactory;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.Property;
/*     */ import org.xnio.Sequence;
/*     */ import org.xnio._private.Messages;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SaslUtils
/*     */ {
/*  65 */   public static final byte[] EMPTY_BYTES = new byte[0];
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
/*     */   public static Iterator<SaslServerFactory> getSaslServerFactories(ClassLoader classLoader, boolean includeGlobal) {
/*  77 */     return getFactories(SaslServerFactory.class, classLoader, includeGlobal);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Iterator<SaslServerFactory> getSaslServerFactories() {
/*  87 */     return getFactories(SaslServerFactory.class, null, true);
/*     */   }
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
/*     */   public static Iterator<SaslClientFactory> getSaslClientFactories(ClassLoader classLoader, boolean includeGlobal) {
/* 100 */     return getFactories(SaslClientFactory.class, classLoader, includeGlobal);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Iterator<SaslClientFactory> getSaslClientFactories() {
/* 110 */     return getFactories(SaslClientFactory.class, null, true);
/*     */   }
/*     */   
/*     */   private static <T> Iterator<T> getFactories(Class<T> type, ClassLoader classLoader, boolean includeGlobal) {
/* 114 */     Set<T> factories = new LinkedHashSet<>();
/* 115 */     ServiceLoader<T> loader = ServiceLoader.load(type, classLoader);
/* 116 */     for (T factory : loader) {
/* 117 */       factories.add(factory);
/*     */     }
/* 119 */     if (includeGlobal) {
/* 120 */       Set<String> loadedClasses = new HashSet<>();
/* 121 */       String filter = type.getSimpleName() + ".";
/*     */       
/* 123 */       Provider[] providers = Security.getProviders();
/* 124 */       SecurityManager sm = System.getSecurityManager();
/* 125 */       for (Provider currentProvider : providers) {
/* 126 */         ClassLoader cl = (sm == null) ? currentProvider.getClass().getClassLoader() : AccessController.<ClassLoader>doPrivileged(new PrivilegedAction<ClassLoader>() {
/*     */               public ClassLoader run() {
/* 128 */                 return currentProvider.getClass().getClassLoader();
/*     */               }
/*     */             });
/* 131 */         for (Object currentKey : currentProvider.keySet()) {
/* 132 */           if (currentKey instanceof String && ((String)currentKey)
/* 133 */             .startsWith(filter) && ((String)currentKey)
/* 134 */             .indexOf(' ') < 0) {
/* 135 */             String className = currentProvider.getProperty((String)currentKey);
/* 136 */             if (className != null && loadedClasses.add(className)) {
/*     */               
/* 138 */               try { factories.add(Class.forName(className, true, cl).<T>asSubclass(type).newInstance()); }
/* 139 */               catch (ClassNotFoundException classNotFoundException) {  }
/* 140 */               catch (ClassCastException classCastException) {  }
/* 141 */               catch (InstantiationException instantiationException) {  }
/* 142 */               catch (IllegalAccessException illegalAccessException) {}
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 149 */     return factories.iterator();
/*     */   }
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
/*     */   public static boolean evaluateChallenge(SaslClient client, ByteBuffer destination, ByteBuffer source) throws SaslException {
/* 169 */     byte[] result = evaluateChallenge(client, source);
/* 170 */     if (result != null) {
/* 171 */       if (destination == null) {
/* 172 */         throw Messages.msg.extraChallenge();
/*     */       }
/* 174 */       destination.put(result);
/* 175 */       return false;
/*     */     } 
/* 177 */     return true;
/*     */   }
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
/*     */   public static byte[] evaluateChallenge(SaslClient client, ByteBuffer source) throws SaslException {
/* 196 */     return client.evaluateChallenge(Buffers.take(source));
/*     */   }
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
/*     */   public static boolean evaluateResponse(SaslServer server, ByteBuffer destination, ByteBuffer source) throws SaslException {
/* 217 */     byte[] result = evaluateResponse(server, source);
/* 218 */     if (result != null) {
/* 219 */       if (destination == null) {
/* 220 */         throw Messages.msg.extraResponse();
/*     */       }
/* 222 */       destination.put(result);
/* 223 */       return server.isComplete();
/*     */     } 
/* 225 */     return true;
/*     */   }
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
/*     */   public static byte[] evaluateResponse(SaslServer server, ByteBuffer source) throws SaslException {
/* 245 */     return server.evaluateResponse(source.hasRemaining() ? Buffers.take(source) : EMPTY_BYTES);
/*     */   }
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
/*     */   public static void wrap(SaslClient client, ByteBuffer destination, ByteBuffer source) throws SaslException {
/* 262 */     destination.put(wrap(client, source));
/*     */   }
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
/*     */   public static byte[] wrap(SaslClient client, ByteBuffer source) throws SaslException {
/*     */     byte[] result;
/* 280 */     int len = source.remaining();
/* 281 */     if (len == 0) {
/* 282 */       result = client.wrap(EMPTY_BYTES, 0, len);
/* 283 */     } else if (source.hasArray()) {
/* 284 */       byte[] array = source.array();
/* 285 */       int offs = source.arrayOffset();
/* 286 */       source.position(source.position() + len);
/* 287 */       result = client.wrap(array, offs, len);
/*     */     } else {
/* 289 */       result = client.wrap(Buffers.take(source, len), 0, len);
/*     */     } 
/* 291 */     return result;
/*     */   }
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
/*     */   public static void wrap(SaslServer server, ByteBuffer destination, ByteBuffer source) throws SaslException {
/* 308 */     destination.put(wrap(server, source));
/*     */   }
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
/*     */   public static byte[] wrap(SaslServer server, ByteBuffer source) throws SaslException {
/*     */     byte[] result;
/* 326 */     int len = source.remaining();
/* 327 */     if (len == 0) {
/* 328 */       result = server.wrap(EMPTY_BYTES, 0, len);
/* 329 */     } else if (source.hasArray()) {
/* 330 */       byte[] array = source.array();
/* 331 */       int offs = source.arrayOffset();
/* 332 */       source.position(source.position() + len);
/* 333 */       result = server.wrap(array, offs, len);
/*     */     } else {
/* 335 */       result = server.wrap(Buffers.take(source, len), 0, len);
/*     */     } 
/* 337 */     return result;
/*     */   }
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
/*     */   public static void unwrap(SaslClient client, ByteBuffer destination, ByteBuffer source) throws SaslException {
/* 354 */     destination.put(unwrap(client, source));
/*     */   }
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
/*     */   public static byte[] unwrap(SaslClient client, ByteBuffer source) throws SaslException {
/*     */     byte[] result;
/* 372 */     int len = source.remaining();
/* 373 */     if (len == 0) {
/* 374 */       result = client.unwrap(EMPTY_BYTES, 0, len);
/* 375 */     } else if (source.hasArray()) {
/* 376 */       byte[] array = source.array();
/* 377 */       int offs = source.arrayOffset();
/* 378 */       source.position(source.position() + len);
/* 379 */       result = client.unwrap(array, offs, len);
/*     */     } else {
/* 381 */       result = client.unwrap(Buffers.take(source, len), 0, len);
/*     */     } 
/* 383 */     return result;
/*     */   }
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
/*     */   public static void unwrap(SaslServer server, ByteBuffer destination, ByteBuffer source) throws SaslException {
/* 400 */     destination.put(unwrap(server, source));
/*     */   }
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
/*     */   public static byte[] unwrap(SaslServer server, ByteBuffer source) throws SaslException {
/*     */     byte[] result;
/* 418 */     int len = source.remaining();
/* 419 */     if (len == 0) {
/* 420 */       result = server.unwrap(EMPTY_BYTES, 0, len);
/* 421 */     } else if (source.hasArray()) {
/* 422 */       byte[] array = source.array();
/* 423 */       int offs = source.arrayOffset();
/* 424 */       source.position(source.position() + len);
/* 425 */       result = server.unwrap(array, offs, len);
/*     */     } else {
/* 427 */       result = server.unwrap(Buffers.take(source, len), 0, len);
/*     */     } 
/* 429 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Map<String, Object> createPropertyMap(OptionMap optionMap, boolean secure) {
/* 440 */     Map<String, Object> propertyMap = new HashMap<>();
/* 441 */     add(optionMap, Options.SASL_POLICY_FORWARD_SECRECY, propertyMap, "javax.security.sasl.policy.forward", null);
/* 442 */     add(optionMap, Options.SASL_POLICY_NOACTIVE, propertyMap, "javax.security.sasl.policy.noactive", null);
/* 443 */     add(optionMap, Options.SASL_POLICY_NOANONYMOUS, propertyMap, "javax.security.sasl.policy.noanonymous", Boolean.TRUE);
/* 444 */     add(optionMap, Options.SASL_POLICY_NODICTIONARY, propertyMap, "javax.security.sasl.policy.nodictionary", null);
/* 445 */     add(optionMap, Options.SASL_POLICY_NOPLAINTEXT, propertyMap, "javax.security.sasl.policy.noplaintext", Boolean.valueOf(!secure));
/* 446 */     add(optionMap, Options.SASL_POLICY_PASS_CREDENTIALS, propertyMap, "javax.security.sasl.policy.credentials", null);
/* 447 */     add(optionMap, Options.SASL_REUSE, propertyMap, "javax.security.sasl.reuse", null);
/* 448 */     add(optionMap, Options.SASL_SERVER_AUTH, propertyMap, "javax.security.sasl.server.authentication", null);
/* 449 */     addQopList(optionMap, Options.SASL_QOP, propertyMap, "javax.security.sasl.qop");
/* 450 */     add(optionMap, Options.SASL_STRENGTH, propertyMap, "javax.security.sasl.strength", null);
/* 451 */     addSaslProperties(optionMap, Options.SASL_PROPERTIES, propertyMap);
/* 452 */     return propertyMap;
/*     */   }
/*     */   
/*     */   private static <T> void add(OptionMap optionMap, Option<T> option, Map<String, Object> map, String propName, T defaultVal) {
/* 456 */     Object value = optionMap.get(option, defaultVal);
/* 457 */     if (value != null) map.put(propName, value.toString().toLowerCase(Locale.US)); 
/*     */   }
/*     */   
/*     */   private static void addQopList(OptionMap optionMap, Option<Sequence<SaslQop>> option, Map<String, Object> map, String propName) {
/* 461 */     Sequence<SaslQop> value = (Sequence<SaslQop>)optionMap.get(option);
/* 462 */     if (value == null)
/* 463 */       return;  Sequence<SaslQop> seq = value;
/* 464 */     StringBuilder builder = new StringBuilder();
/* 465 */     Iterator<SaslQop> iterator = seq.iterator();
/* 466 */     if (!iterator.hasNext())
/*     */       return; 
/*     */     while (true) {
/* 469 */       builder.append(((SaslQop)iterator.next()).getString());
/* 470 */       if (iterator.hasNext()) {
/* 471 */         builder.append(',');
/*     */       }
/* 473 */       if (!iterator.hasNext()) {
/* 474 */         map.put(propName, builder.toString());
/*     */         return;
/*     */       } 
/*     */     }  } private static void addSaslProperties(OptionMap optionMap, Option<Sequence<Property>> option, Map<String, Object> map) {
/* 478 */     Sequence<Property> value = (Sequence<Property>)optionMap.get(option);
/* 479 */     if (value == null)
/* 480 */       return;  for (Property current : value)
/* 481 */       map.put(current.getKey(), current.getValue()); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\sasl\SaslUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */