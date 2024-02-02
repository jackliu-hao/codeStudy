package org.xnio.nio;

import java.util.Hashtable;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.xnio.Xnio;
import org.xnio.XnioProvider;

public class OsgiActivator implements BundleActivator {
   private ServiceRegistration<Xnio> registrationS;
   private ServiceRegistration<XnioProvider> registrationP;

   public void start(BundleContext context) throws Exception {
      XnioProvider provider = new NioXnioProvider();
      Xnio xnio = provider.getInstance();
      String name = xnio.getName();
      Hashtable<String, String> props = new Hashtable();
      props.put("name", name);
      this.registrationS = context.registerService(Xnio.class, xnio, props);
      this.registrationP = context.registerService(XnioProvider.class, provider, props);
   }

   public void stop(BundleContext context) throws Exception {
      this.registrationS.unregister();
      this.registrationP.unregister();
   }
}
