package ch.qos.logback.core.util;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ContextUtil extends ContextAwareBase {
   public ContextUtil(Context context) {
      this.setContext(context);
   }

   public static String getLocalHostName() throws UnknownHostException, SocketException {
      try {
         InetAddress localhost = InetAddress.getLocalHost();
         return localhost.getHostName();
      } catch (UnknownHostException var1) {
         return getLocalAddressAsString();
      }
   }

   private static String getLocalAddressAsString() throws UnknownHostException, SocketException {
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

      while(interfaces != null && interfaces.hasMoreElements()) {
         Enumeration<InetAddress> addresses = ((NetworkInterface)interfaces.nextElement()).getInetAddresses();

         while(addresses != null && addresses.hasMoreElements()) {
            InetAddress address = (InetAddress)addresses.nextElement();
            if (acceptableAddress(address)) {
               return address.getHostAddress();
            }
         }
      }

      throw new UnknownHostException();
   }

   private static boolean acceptableAddress(InetAddress address) {
      return address != null && !address.isLoopbackAddress() && !address.isAnyLocalAddress() && !address.isLinkLocalAddress();
   }

   public String safelyGetLocalHostName() {
      try {
         String localhostName = getLocalHostName();
         return localhostName;
      } catch (UnknownHostException var2) {
         this.addError("Failed to get local hostname", var2);
      } catch (SocketException var3) {
         this.addError("Failed to get local hostname", var3);
      } catch (SecurityException var4) {
         this.addError("Failed to get local hostname", var4);
      }

      return "UNKNOWN_LOCALHOST";
   }

   public void addProperties(Properties props) {
      if (props != null) {
         Iterator i = props.keySet().iterator();

         while(i.hasNext()) {
            String key = (String)i.next();
            this.context.putProperty(key, props.getProperty(key));
         }

      }
   }

   public static Map<String, String> getFilenameCollisionMap(Context context) {
      if (context == null) {
         return null;
      } else {
         Map<String, String> map = (Map)context.getObject("FA_FILENAME_COLLISION_MAP");
         return map;
      }
   }

   public static Map<String, FileNamePattern> getFilenamePatternCollisionMap(Context context) {
      if (context == null) {
         return null;
      } else {
         Map<String, FileNamePattern> map = (Map)context.getObject("RFA_FILENAME_PATTERN_COLLISION_MAP");
         return map;
      }
   }

   public void addGroovyPackages(List<String> frameworkPackages) {
      this.addFrameworkPackage(frameworkPackages, "org.codehaus.groovy.runtime");
   }

   public void addFrameworkPackage(List<String> frameworkPackages, String packageName) {
      if (!frameworkPackages.contains(packageName)) {
         frameworkPackages.add(packageName);
      }

   }
}
