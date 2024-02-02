package freemarker.template.utility;

import freemarker.log.Logger;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class SecurityUtilities {
   private static final Logger LOG = Logger.getLogger("freemarker.security");

   private SecurityUtilities() {
   }

   public static String getSystemProperty(final String key) {
      return (String)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return System.getProperty(key);
         }
      });
   }

   public static String getSystemProperty(final String key, final String defValue) {
      try {
         return (String)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               return System.getProperty(key, defValue);
            }
         });
      } catch (AccessControlException var3) {
         LOG.warn("Insufficient permissions to read system property " + StringUtil.jQuoteNoXSS(key) + ", using default value " + StringUtil.jQuoteNoXSS(defValue));
         return defValue;
      }
   }

   public static Integer getSystemProperty(final String key, final int defValue) {
      try {
         return (Integer)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               return Integer.getInteger(key, defValue);
            }
         });
      } catch (AccessControlException var3) {
         LOG.warn("Insufficient permissions to read system property " + StringUtil.jQuote(key) + ", using default value " + defValue);
         return defValue;
      }
   }
}
