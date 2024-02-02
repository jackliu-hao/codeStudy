package org.xnio.ssl;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Sequence;
import org.xnio._private.Messages;

public final class JsseSslUtils {
   private JsseSslUtils() {
   }

   public static SSLContext createSSLContext(OptionMap optionMap) throws NoSuchProviderException, NoSuchAlgorithmException, KeyManagementException {
      return createSSLContext((KeyManager[])null, (TrustManager[])null, (SecureRandom)null, optionMap);
   }

   public static SSLContext createSSLContext(KeyManager[] keyManagers, TrustManager[] trustManagers, SecureRandom secureRandom, OptionMap optionMap) throws NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
      String provider = (String)optionMap.get(Options.SSL_PROVIDER);
      String protocol = (String)optionMap.get(Options.SSL_PROTOCOL);
      if (protocol == null) {
         return SSLContext.getDefault();
      } else {
         SSLContext sslContext;
         if (provider == null) {
            sslContext = SSLContext.getInstance(protocol);
         } else {
            sslContext = SSLContext.getInstance(protocol, provider);
         }

         Sequence trustManagerClasses;
         int size;
         int i;
         if (keyManagers == null) {
            trustManagerClasses = (Sequence)optionMap.get(Options.SSL_JSSE_KEY_MANAGER_CLASSES);
            if (trustManagerClasses != null) {
               size = trustManagerClasses.size();
               keyManagers = new KeyManager[size];

               for(i = 0; i < size; ++i) {
                  keyManagers[i] = (KeyManager)instantiate((Class)trustManagerClasses.get(i));
               }
            }
         }

         if (trustManagers == null) {
            trustManagerClasses = (Sequence)optionMap.get(Options.SSL_JSSE_TRUST_MANAGER_CLASSES);
            if (trustManagerClasses != null) {
               size = trustManagerClasses.size();
               trustManagers = new TrustManager[size];

               for(i = 0; i < size; ++i) {
                  trustManagers[i] = (TrustManager)instantiate((Class)trustManagerClasses.get(i));
               }
            }
         }

         sslContext.init(keyManagers, trustManagers, secureRandom);
         sslContext.getClientSessionContext().setSessionCacheSize(optionMap.get(Options.SSL_CLIENT_SESSION_CACHE_SIZE, 0));
         sslContext.getClientSessionContext().setSessionTimeout(optionMap.get(Options.SSL_CLIENT_SESSION_TIMEOUT, 0));
         sslContext.getServerSessionContext().setSessionCacheSize(optionMap.get(Options.SSL_SERVER_SESSION_CACHE_SIZE, 0));
         sslContext.getServerSessionContext().setSessionTimeout(optionMap.get(Options.SSL_SERVER_SESSION_TIMEOUT, 0));
         return sslContext;
      }
   }

   private static <T> T instantiate(Class<T> clazz) {
      try {
         return clazz.getConstructor().newInstance();
      } catch (InstantiationException var2) {
         throw Messages.msg.cantInstantiate(clazz, var2);
      } catch (IllegalAccessException var3) {
         throw Messages.msg.cantInstantiate(clazz, var3);
      } catch (NoSuchMethodException var4) {
         throw Messages.msg.cantInstantiate(clazz, var4);
      } catch (InvocationTargetException var5) {
         throw Messages.msg.cantInstantiate(clazz, var5.getCause());
      }
   }

   public static SSLEngine createSSLEngine(SSLContext sslContext, OptionMap optionMap, InetSocketAddress peerAddress) {
      SSLEngine engine = sslContext.createSSLEngine((String)optionMap.get(Options.SSL_PEER_HOST_NAME, peerAddress.getHostString()), optionMap.get(Options.SSL_PEER_PORT, peerAddress.getPort()));
      engine.setUseClientMode(true);
      engine.setEnableSessionCreation(optionMap.get(Options.SSL_ENABLE_SESSION_CREATION, true));
      Sequence<String> cipherSuites = (Sequence)optionMap.get(Options.SSL_ENABLED_CIPHER_SUITES);
      if (cipherSuites != null) {
         Set<String> supported = new HashSet(Arrays.asList(engine.getSupportedCipherSuites()));
         List<String> finalList = new ArrayList();
         Iterator var7 = cipherSuites.iterator();

         while(var7.hasNext()) {
            String name = (String)var7.next();
            if (supported.contains(name)) {
               finalList.add(name);
            }
         }

         engine.setEnabledCipherSuites((String[])finalList.toArray(new String[finalList.size()]));
      }

      Sequence<String> protocols = (Sequence)optionMap.get(Options.SSL_ENABLED_PROTOCOLS);
      if (protocols != null) {
         Set<String> supported = new HashSet(Arrays.asList(engine.getSupportedProtocols()));
         List<String> finalList = new ArrayList();
         Iterator var13 = protocols.iterator();

         while(var13.hasNext()) {
            String name = (String)var13.next();
            if (supported.contains(name)) {
               finalList.add(name);
            }
         }

         engine.setEnabledProtocols((String[])finalList.toArray(new String[finalList.size()]));
      }

      return engine;
   }
}
