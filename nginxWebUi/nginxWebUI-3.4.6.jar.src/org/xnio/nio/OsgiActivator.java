/*    */ package org.xnio.nio;
/*    */ 
/*    */ import java.util.Hashtable;
/*    */ import org.osgi.framework.BundleActivator;
/*    */ import org.osgi.framework.BundleContext;
/*    */ import org.osgi.framework.ServiceRegistration;
/*    */ import org.xnio.Xnio;
/*    */ import org.xnio.XnioProvider;
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
/*    */ 
/*    */ public class OsgiActivator
/*    */   implements BundleActivator
/*    */ {
/*    */   private ServiceRegistration<Xnio> registrationS;
/*    */   private ServiceRegistration<XnioProvider> registrationP;
/*    */   
/*    */   public void start(BundleContext context) throws Exception {
/* 41 */     XnioProvider provider = new NioXnioProvider();
/* 42 */     Xnio xnio = provider.getInstance();
/* 43 */     String name = xnio.getName();
/* 44 */     Hashtable<String, String> props = new Hashtable<>();
/* 45 */     props.put("name", name);
/* 46 */     this.registrationS = context.registerService(Xnio.class, xnio, props);
/* 47 */     this.registrationP = context.registerService(XnioProvider.class, provider, props);
/*    */   }
/*    */ 
/*    */   
/*    */   public void stop(BundleContext context) throws Exception {
/* 52 */     this.registrationS.unregister();
/* 53 */     this.registrationP.unregister();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\OsgiActivator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */