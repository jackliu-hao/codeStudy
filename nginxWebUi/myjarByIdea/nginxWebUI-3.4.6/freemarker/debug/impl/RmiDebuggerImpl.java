package freemarker.debug.impl;

import freemarker.debug.Breakpoint;
import freemarker.debug.Debugger;
import freemarker.debug.DebuggerListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.List;

class RmiDebuggerImpl extends UnicastRemoteObject implements Debugger {
   private static final long serialVersionUID = 1L;
   private final RmiDebuggerService service;

   protected RmiDebuggerImpl(RmiDebuggerService service) throws RemoteException {
      this.service = service;
   }

   public void addBreakpoint(Breakpoint breakpoint) {
      this.service.addBreakpoint(breakpoint);
   }

   public Object addDebuggerListener(DebuggerListener listener) {
      return this.service.addDebuggerListener(listener);
   }

   public List getBreakpoints() {
      return this.service.getBreakpointsSpi();
   }

   public List getBreakpoints(String templateName) {
      return this.service.getBreakpointsSpi(templateName);
   }

   public Collection getSuspendedEnvironments() {
      return this.service.getSuspendedEnvironments();
   }

   public void removeBreakpoint(Breakpoint breakpoint) {
      this.service.removeBreakpoint(breakpoint);
   }

   public void removeDebuggerListener(Object id) {
      this.service.removeDebuggerListener(id);
   }

   public void removeBreakpoints() {
      this.service.removeBreakpoints();
   }

   public void removeBreakpoints(String templateName) {
      this.service.removeBreakpoints(templateName);
   }
}
