package freemarker.debug;

import freemarker.debug.impl.RmiDebuggerListenerImpl;
import freemarker.template.utility.UndeclaredThrowableException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.List;

public class DebuggerClient {
   private DebuggerClient() {
   }

   public static Debugger getDebugger(InetAddress host, int port, String password) throws IOException {
      try {
         Socket s = new Socket(host, port);
         Throwable var4 = null;

         LocalDebuggerProxy var10;
         try {
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            int protocolVersion = in.readInt();
            if (protocolVersion > 220) {
               throw new IOException("Incompatible protocol version " + protocolVersion + ". At most 220 was expected.");
            }

            byte[] challenge = (byte[])((byte[])in.readObject());
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(password.getBytes("UTF-8"));
            md.update(challenge);
            out.writeObject(md.digest());
            var10 = new LocalDebuggerProxy((Debugger)in.readObject());
         } catch (Throwable var21) {
            var4 = var21;
            throw var21;
         } finally {
            if (s != null) {
               if (var4 != null) {
                  try {
                     s.close();
                  } catch (Throwable var20) {
                     var4.addSuppressed(var20);
                  }
               } else {
                  s.close();
               }
            }

         }

         return var10;
      } catch (IOException var23) {
         throw var23;
      } catch (Exception var24) {
         throw new UndeclaredThrowableException(var24);
      }
   }

   private static class LocalDebuggerProxy implements Debugger {
      private final Debugger remoteDebugger;

      LocalDebuggerProxy(Debugger remoteDebugger) {
         this.remoteDebugger = remoteDebugger;
      }

      public void addBreakpoint(Breakpoint breakpoint) throws RemoteException {
         this.remoteDebugger.addBreakpoint(breakpoint);
      }

      public Object addDebuggerListener(DebuggerListener listener) throws RemoteException {
         if (listener instanceof RemoteObject) {
            return this.remoteDebugger.addDebuggerListener(listener);
         } else {
            RmiDebuggerListenerImpl remotableListener = new RmiDebuggerListenerImpl(listener);
            return this.remoteDebugger.addDebuggerListener(remotableListener);
         }
      }

      public List getBreakpoints() throws RemoteException {
         return this.remoteDebugger.getBreakpoints();
      }

      public List getBreakpoints(String templateName) throws RemoteException {
         return this.remoteDebugger.getBreakpoints(templateName);
      }

      public Collection getSuspendedEnvironments() throws RemoteException {
         return this.remoteDebugger.getSuspendedEnvironments();
      }

      public void removeBreakpoint(Breakpoint breakpoint) throws RemoteException {
         this.remoteDebugger.removeBreakpoint(breakpoint);
      }

      public void removeBreakpoints(String templateName) throws RemoteException {
         this.remoteDebugger.removeBreakpoints(templateName);
      }

      public void removeBreakpoints() throws RemoteException {
         this.remoteDebugger.removeBreakpoints();
      }

      public void removeDebuggerListener(Object id) throws RemoteException {
         this.remoteDebugger.removeDebuggerListener(id);
      }
   }
}
