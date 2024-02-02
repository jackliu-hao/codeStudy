package ch.qos.logback.core.util;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JNDIUtil {
   static final String RESTRICTION_MSG = "JNDI name must start with java: but was ";

   public static Context getInitialContext() throws NamingException {
      return new InitialContext();
   }

   public static Context getInitialContext(Hashtable<?, ?> props) throws NamingException {
      return new InitialContext(props);
   }

   public static Object lookupObject(Context ctx, String name) throws NamingException {
      if (ctx == null) {
         return null;
      } else if (OptionHelper.isEmpty(name)) {
         return null;
      } else {
         jndiNameSecurityCheck(name);
         Object lookup = ctx.lookup(name);
         return lookup;
      }
   }

   private static void jndiNameSecurityCheck(String name) throws NamingException {
      if (!name.startsWith("java:")) {
         throw new NamingException("JNDI name must start with java: but was " + name);
      }
   }

   public static String lookupString(Context ctx, String name) throws NamingException {
      Object lookup = lookupObject(ctx, name);
      return (String)lookup;
   }
}
