package io.undertow.protocols.alpn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Function;
import javax.net.ssl.SSLEngine;

public class ALPNManager {
   private final ALPNProvider[] alpnProviders;
   private final ALPNEngineManager[] alpnEngineManagers;
   public static final ALPNManager INSTANCE = new ALPNManager(ALPNManager.class.getClassLoader());

   public ALPNManager(ClassLoader classLoader) {
      ServiceLoader<ALPNProvider> loader = ServiceLoader.load(ALPNProvider.class, classLoader);
      List<ALPNProvider> provider = new ArrayList();
      Iterator var4 = loader.iterator();

      while(var4.hasNext()) {
         ALPNProvider prov = (ALPNProvider)var4.next();
         provider.add(prov);
      }

      Collections.sort(provider, new Comparator<ALPNProvider>() {
         public int compare(ALPNProvider o1, ALPNProvider o2) {
            return Integer.compare(o2.getPriority(), o1.getPriority());
         }
      });
      this.alpnProviders = (ALPNProvider[])provider.toArray(new ALPNProvider[0]);
      ServiceLoader<ALPNEngineManager> managerLoader = ServiceLoader.load(ALPNEngineManager.class, classLoader);
      List<ALPNEngineManager> managers = new ArrayList();
      Iterator var6 = managerLoader.iterator();

      while(var6.hasNext()) {
         ALPNEngineManager manager = (ALPNEngineManager)var6.next();
         managers.add(manager);
      }

      Collections.sort(managers, new Comparator<ALPNEngineManager>() {
         public int compare(ALPNEngineManager o1, ALPNEngineManager o2) {
            return Integer.compare(o2.getPriority(), o1.getPriority());
         }
      });
      this.alpnEngineManagers = (ALPNEngineManager[])managers.toArray(new ALPNEngineManager[0]);
   }

   public ALPNProvider getProvider(SSLEngine engine) {
      ALPNProvider[] var2 = this.alpnProviders;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ALPNProvider provider = var2[var4];
         if (provider.isEnabled(engine)) {
            return provider;
         }
      }

      return null;
   }

   public void registerEngineCallback(SSLEngine original, Function<SSLEngine, SSLEngine> selectionFunction) {
      ALPNEngineManager[] var3 = this.alpnEngineManagers;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ALPNEngineManager manager = var3[var5];
         if (manager.registerEngine(original, selectionFunction)) {
            return;
         }
      }

   }
}
