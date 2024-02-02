package org.xnio;

import java.io.IOError;
import java.io.IOException;
import java.security.AccessController;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import org.wildfly.client.config.ConfigXMLParseException;
import org.xnio._private.Messages;

final class DefaultXnioWorkerHolder {
   static final XnioWorker INSTANCE = (XnioWorker)AccessController.doPrivileged(() -> {
      Xnio xnio = Xnio.getInstance();
      XnioWorker worker = null;

      try {
         worker = XnioXmlParser.parseWorker(xnio);
      } catch (IOException | ConfigXMLParseException var7) {
         Messages.msg.trace("Failed to parse worker XML definition", var7);
      }

      if (worker == null) {
         Iterator<XnioWorkerConfigurator> iterator = ServiceLoader.load(XnioWorkerConfigurator.class, DefaultXnioWorkerHolder.class.getClassLoader()).iterator();

         while(worker == null) {
            try {
               if (!iterator.hasNext()) {
                  break;
               }

               XnioWorkerConfigurator configurator = (XnioWorkerConfigurator)iterator.next();
               if (configurator != null) {
                  try {
                     worker = configurator.createWorker();
                  } catch (IOException var6) {
                     Messages.msg.trace("Failed to configure the default worker", var6);
                  }
               }
            } catch (ServiceConfigurationError var8) {
               Messages.msg.trace("Failed to configure a service", var8);
            }
         }
      }

      if (worker == null) {
         try {
            worker = xnio.createWorker(OptionMap.create(Options.THREAD_DAEMON, Boolean.TRUE));
         } catch (IOException var5) {
            throw new IOError(var5);
         }
      }

      return worker;
   });
}
