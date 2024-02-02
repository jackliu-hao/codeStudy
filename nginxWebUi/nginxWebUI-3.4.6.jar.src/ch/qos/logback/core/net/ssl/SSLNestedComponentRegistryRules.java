/*    */ package ch.qos.logback.core.net.ssl;
/*    */ 
/*    */ import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
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
/*    */ public class SSLNestedComponentRegistryRules
/*    */ {
/*    */   public static void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry registry) {
/* 27 */     registry.add(SSLComponent.class, "ssl", SSLConfiguration.class);
/* 28 */     registry.add(SSLConfiguration.class, "parameters", SSLParametersConfiguration.class);
/* 29 */     registry.add(SSLConfiguration.class, "keyStore", KeyStoreFactoryBean.class);
/* 30 */     registry.add(SSLConfiguration.class, "trustStore", KeyStoreFactoryBean.class);
/* 31 */     registry.add(SSLConfiguration.class, "keyManagerFactory", KeyManagerFactoryFactoryBean.class);
/* 32 */     registry.add(SSLConfiguration.class, "trustManagerFactory", TrustManagerFactoryFactoryBean.class);
/* 33 */     registry.add(SSLConfiguration.class, "secureRandom", SecureRandomFactoryBean.class);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\ssl\SSLNestedComponentRegistryRules.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */