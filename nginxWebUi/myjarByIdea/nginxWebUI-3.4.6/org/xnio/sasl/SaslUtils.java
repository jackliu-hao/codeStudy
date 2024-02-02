package org.xnio.sasl;

import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslServerFactory;
import org.xnio.Buffers;
import org.xnio.Option;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Property;
import org.xnio.Sequence;
import org.xnio._private.Messages;

public final class SaslUtils {
   public static final byte[] EMPTY_BYTES = new byte[0];

   private SaslUtils() {
   }

   public static Iterator<SaslServerFactory> getSaslServerFactories(ClassLoader classLoader, boolean includeGlobal) {
      return getFactories(SaslServerFactory.class, classLoader, includeGlobal);
   }

   public static Iterator<SaslServerFactory> getSaslServerFactories() {
      return getFactories(SaslServerFactory.class, (ClassLoader)null, true);
   }

   public static Iterator<SaslClientFactory> getSaslClientFactories(ClassLoader classLoader, boolean includeGlobal) {
      return getFactories(SaslClientFactory.class, classLoader, includeGlobal);
   }

   public static Iterator<SaslClientFactory> getSaslClientFactories() {
      return getFactories(SaslClientFactory.class, (ClassLoader)null, true);
   }

   private static <T> Iterator<T> getFactories(Class<T> type, ClassLoader classLoader, boolean includeGlobal) {
      Set<T> factories = new LinkedHashSet();
      ServiceLoader<T> loader = ServiceLoader.load(type, classLoader);
      Iterator var5 = loader.iterator();

      while(var5.hasNext()) {
         T factory = var5.next();
         factories.add(factory);
      }

      if (includeGlobal) {
         Set<String> loadedClasses = new HashSet();
         String filter = type.getSimpleName() + ".";
         Provider[] providers = Security.getProviders();
         SecurityManager sm = System.getSecurityManager();
         Provider[] var9 = providers;
         int var10 = providers.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            final Provider currentProvider = var9[var11];
            ClassLoader cl = sm == null ? currentProvider.getClass().getClassLoader() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
               public ClassLoader run() {
                  return currentProvider.getClass().getClassLoader();
               }
            });
            Iterator var14 = currentProvider.keySet().iterator();

            while(var14.hasNext()) {
               Object currentKey = var14.next();
               if (currentKey instanceof String && ((String)currentKey).startsWith(filter) && ((String)currentKey).indexOf(32) < 0) {
                  String className = currentProvider.getProperty((String)currentKey);
                  if (className != null && loadedClasses.add(className)) {
                     try {
                        factories.add(Class.forName(className, true, cl).asSubclass(type).newInstance());
                     } catch (ClassNotFoundException var18) {
                     } catch (ClassCastException var19) {
                     } catch (InstantiationException var20) {
                     } catch (IllegalAccessException var21) {
                     }
                  }
               }
            }
         }
      }

      return factories.iterator();
   }

   public static boolean evaluateChallenge(SaslClient client, ByteBuffer destination, ByteBuffer source) throws SaslException {
      byte[] result = evaluateChallenge(client, source);
      if (result != null) {
         if (destination == null) {
            throw Messages.msg.extraChallenge();
         } else {
            destination.put(result);
            return false;
         }
      } else {
         return true;
      }
   }

   public static byte[] evaluateChallenge(SaslClient client, ByteBuffer source) throws SaslException {
      return client.evaluateChallenge(Buffers.take(source));
   }

   public static boolean evaluateResponse(SaslServer server, ByteBuffer destination, ByteBuffer source) throws SaslException {
      byte[] result = evaluateResponse(server, source);
      if (result != null) {
         if (destination == null) {
            throw Messages.msg.extraResponse();
         } else {
            destination.put(result);
            return server.isComplete();
         }
      } else {
         return true;
      }
   }

   public static byte[] evaluateResponse(SaslServer server, ByteBuffer source) throws SaslException {
      return server.evaluateResponse(source.hasRemaining() ? Buffers.take(source) : EMPTY_BYTES);
   }

   public static void wrap(SaslClient client, ByteBuffer destination, ByteBuffer source) throws SaslException {
      destination.put(wrap(client, source));
   }

   public static byte[] wrap(SaslClient client, ByteBuffer source) throws SaslException {
      int len = source.remaining();
      byte[] result;
      if (len == 0) {
         result = client.wrap(EMPTY_BYTES, 0, len);
      } else if (source.hasArray()) {
         byte[] array = source.array();
         int offs = source.arrayOffset();
         source.position(source.position() + len);
         result = client.wrap(array, offs, len);
      } else {
         result = client.wrap(Buffers.take(source, len), 0, len);
      }

      return result;
   }

   public static void wrap(SaslServer server, ByteBuffer destination, ByteBuffer source) throws SaslException {
      destination.put(wrap(server, source));
   }

   public static byte[] wrap(SaslServer server, ByteBuffer source) throws SaslException {
      int len = source.remaining();
      byte[] result;
      if (len == 0) {
         result = server.wrap(EMPTY_BYTES, 0, len);
      } else if (source.hasArray()) {
         byte[] array = source.array();
         int offs = source.arrayOffset();
         source.position(source.position() + len);
         result = server.wrap(array, offs, len);
      } else {
         result = server.wrap(Buffers.take(source, len), 0, len);
      }

      return result;
   }

   public static void unwrap(SaslClient client, ByteBuffer destination, ByteBuffer source) throws SaslException {
      destination.put(unwrap(client, source));
   }

   public static byte[] unwrap(SaslClient client, ByteBuffer source) throws SaslException {
      int len = source.remaining();
      byte[] result;
      if (len == 0) {
         result = client.unwrap(EMPTY_BYTES, 0, len);
      } else if (source.hasArray()) {
         byte[] array = source.array();
         int offs = source.arrayOffset();
         source.position(source.position() + len);
         result = client.unwrap(array, offs, len);
      } else {
         result = client.unwrap(Buffers.take(source, len), 0, len);
      }

      return result;
   }

   public static void unwrap(SaslServer server, ByteBuffer destination, ByteBuffer source) throws SaslException {
      destination.put(unwrap(server, source));
   }

   public static byte[] unwrap(SaslServer server, ByteBuffer source) throws SaslException {
      int len = source.remaining();
      byte[] result;
      if (len == 0) {
         result = server.unwrap(EMPTY_BYTES, 0, len);
      } else if (source.hasArray()) {
         byte[] array = source.array();
         int offs = source.arrayOffset();
         source.position(source.position() + len);
         result = server.unwrap(array, offs, len);
      } else {
         result = server.unwrap(Buffers.take(source, len), 0, len);
      }

      return result;
   }

   public static Map<String, Object> createPropertyMap(OptionMap optionMap, boolean secure) {
      Map<String, Object> propertyMap = new HashMap();
      add(optionMap, Options.SASL_POLICY_FORWARD_SECRECY, propertyMap, "javax.security.sasl.policy.forward", (Object)null);
      add(optionMap, Options.SASL_POLICY_NOACTIVE, propertyMap, "javax.security.sasl.policy.noactive", (Object)null);
      add(optionMap, Options.SASL_POLICY_NOANONYMOUS, propertyMap, "javax.security.sasl.policy.noanonymous", Boolean.TRUE);
      add(optionMap, Options.SASL_POLICY_NODICTIONARY, propertyMap, "javax.security.sasl.policy.nodictionary", (Object)null);
      add(optionMap, Options.SASL_POLICY_NOPLAINTEXT, propertyMap, "javax.security.sasl.policy.noplaintext", !secure);
      add(optionMap, Options.SASL_POLICY_PASS_CREDENTIALS, propertyMap, "javax.security.sasl.policy.credentials", (Object)null);
      add(optionMap, Options.SASL_REUSE, propertyMap, "javax.security.sasl.reuse", (Object)null);
      add(optionMap, Options.SASL_SERVER_AUTH, propertyMap, "javax.security.sasl.server.authentication", (Object)null);
      addQopList(optionMap, Options.SASL_QOP, propertyMap, "javax.security.sasl.qop");
      add(optionMap, Options.SASL_STRENGTH, propertyMap, "javax.security.sasl.strength", (Object)null);
      addSaslProperties(optionMap, Options.SASL_PROPERTIES, propertyMap);
      return propertyMap;
   }

   private static <T> void add(OptionMap optionMap, Option<T> option, Map<String, Object> map, String propName, T defaultVal) {
      Object value = optionMap.get(option, defaultVal);
      if (value != null) {
         map.put(propName, value.toString().toLowerCase(Locale.US));
      }

   }

   private static void addQopList(OptionMap optionMap, Option<Sequence<SaslQop>> option, Map<String, Object> map, String propName) {
      Sequence<SaslQop> value = (Sequence)optionMap.get(option);
      if (value != null) {
         StringBuilder builder = new StringBuilder();
         Iterator<SaslQop> iterator = value.iterator();
         if (iterator.hasNext()) {
            do {
               builder.append(((SaslQop)iterator.next()).getString());
               if (iterator.hasNext()) {
                  builder.append(',');
               }
            } while(iterator.hasNext());

            map.put(propName, builder.toString());
         }
      }
   }

   private static void addSaslProperties(OptionMap optionMap, Option<Sequence<Property>> option, Map<String, Object> map) {
      Sequence<Property> value = (Sequence)optionMap.get(option);
      if (value != null) {
         Iterator var4 = value.iterator();

         while(var4.hasNext()) {
            Property current = (Property)var4.next();
            map.put(current.getKey(), current.getValue());
         }

      }
   }
}
