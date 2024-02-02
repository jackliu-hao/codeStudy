package freemarker.debug.impl;

import freemarker.core.Environment;
import freemarker.template.Template;
import freemarker.template.utility.SecurityUtilities;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;

public abstract class DebuggerService {
   private static final DebuggerService instance = createInstance();

   private static DebuggerService createInstance() {
      return (DebuggerService)(SecurityUtilities.getSystemProperty("freemarker.debug.password", (String)null) == null ? new NoOpDebuggerService() : new RmiDebuggerService());
   }

   public static List getBreakpoints(String templateName) {
      return instance.getBreakpointsSpi(templateName);
   }

   abstract List getBreakpointsSpi(String var1);

   public static void registerTemplate(Template template) {
      instance.registerTemplateSpi(template);
   }

   abstract void registerTemplateSpi(Template var1);

   public static boolean suspendEnvironment(Environment env, String templateName, int line) throws RemoteException {
      return instance.suspendEnvironmentSpi(env, templateName, line);
   }

   abstract boolean suspendEnvironmentSpi(Environment var1, String var2, int var3) throws RemoteException;

   abstract void shutdownSpi();

   public static void shutdown() {
      instance.shutdownSpi();
   }

   private static class NoOpDebuggerService extends DebuggerService {
      private NoOpDebuggerService() {
      }

      List getBreakpointsSpi(String templateName) {
         return Collections.EMPTY_LIST;
      }

      boolean suspendEnvironmentSpi(Environment env, String templateName, int line) {
         throw new UnsupportedOperationException();
      }

      void registerTemplateSpi(Template template) {
      }

      void shutdownSpi() {
      }

      // $FF: synthetic method
      NoOpDebuggerService(Object x0) {
         this();
      }
   }
}
