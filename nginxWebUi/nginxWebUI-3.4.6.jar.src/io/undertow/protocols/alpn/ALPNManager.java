/*    */ package io.undertow.protocols.alpn;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.Comparator;
/*    */ import java.util.List;
/*    */ import java.util.ServiceLoader;
/*    */ import java.util.function.Function;
/*    */ import javax.net.ssl.SSLEngine;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ALPNManager
/*    */ {
/*    */   private final ALPNProvider[] alpnProviders;
/*    */   private final ALPNEngineManager[] alpnEngineManagers;
/* 38 */   public static final ALPNManager INSTANCE = new ALPNManager(ALPNManager.class.getClassLoader());
/*    */   
/*    */   public ALPNManager(ClassLoader classLoader) {
/* 41 */     ServiceLoader<ALPNProvider> loader = ServiceLoader.load(ALPNProvider.class, classLoader);
/* 42 */     List<ALPNProvider> provider = new ArrayList<>();
/* 43 */     for (ALPNProvider prov : loader) {
/* 44 */       provider.add(prov);
/*    */     }
/* 46 */     Collections.sort(provider, new Comparator<ALPNProvider>()
/*    */         {
/*    */           public int compare(ALPNProvider o1, ALPNProvider o2) {
/* 49 */             return Integer.compare(o2.getPriority(), o1.getPriority());
/*    */           }
/*    */         });
/* 52 */     this.alpnProviders = provider.<ALPNProvider>toArray(new ALPNProvider[0]);
/*    */     
/* 54 */     ServiceLoader<ALPNEngineManager> managerLoader = ServiceLoader.load(ALPNEngineManager.class, classLoader);
/* 55 */     List<ALPNEngineManager> managers = new ArrayList<>();
/* 56 */     for (ALPNEngineManager manager : managerLoader) {
/* 57 */       managers.add(manager);
/*    */     }
/* 59 */     Collections.sort(managers, new Comparator<ALPNEngineManager>()
/*    */         {
/*    */           public int compare(ALPNEngineManager o1, ALPNEngineManager o2) {
/* 62 */             return Integer.compare(o2.getPriority(), o1.getPriority());
/*    */           }
/*    */         });
/* 65 */     this.alpnEngineManagers = managers.<ALPNEngineManager>toArray(new ALPNEngineManager[0]);
/*    */   }
/*    */ 
/*    */   
/*    */   public ALPNProvider getProvider(SSLEngine engine) {
/* 70 */     for (ALPNProvider provider : this.alpnProviders) {
/* 71 */       if (provider.isEnabled(engine)) {
/* 72 */         return provider;
/*    */       }
/*    */     } 
/* 75 */     return null;
/*    */   }
/*    */   
/*    */   public void registerEngineCallback(SSLEngine original, Function<SSLEngine, SSLEngine> selectionFunction) {
/* 79 */     for (ALPNEngineManager manager : this.alpnEngineManagers) {
/* 80 */       if (manager.registerEngine(original, selectionFunction))
/*    */         return; 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\alpn\ALPNManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */