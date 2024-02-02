package freemarker.debug.impl;

import freemarker.core.DebugBreak;
import freemarker.core.Environment;
import freemarker.core.TemplateElement;
import freemarker.core._CoreAPI;
import freemarker.debug.Breakpoint;
import freemarker.debug.DebuggerListener;
import freemarker.debug.EnvironmentSuspendedEvent;
import freemarker.template.Template;
import freemarker.template.utility.UndeclaredThrowableException;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class RmiDebuggerService extends DebuggerService {
   private final Map templateDebugInfos = new HashMap();
   private final HashSet suspendedEnvironments = new HashSet();
   private final Map listeners = new HashMap();
   private final ReferenceQueue refQueue = new ReferenceQueue();
   private final RmiDebuggerImpl debugger;
   private DebuggerServer server;

   RmiDebuggerService() {
      try {
         this.debugger = new RmiDebuggerImpl(this);
         this.server = new DebuggerServer((Serializable)RemoteObject.toStub(this.debugger));
         this.server.start();
      } catch (RemoteException var2) {
         var2.printStackTrace();
         throw new UndeclaredThrowableException(var2);
      }
   }

   List getBreakpointsSpi(String templateName) {
      synchronized(this.templateDebugInfos) {
         TemplateDebugInfo tdi = this.findTemplateDebugInfo(templateName);
         return tdi == null ? Collections.EMPTY_LIST : tdi.breakpoints;
      }
   }

   List getBreakpointsSpi() {
      List sumlist = new ArrayList();
      synchronized(this.templateDebugInfos) {
         Iterator iter = this.templateDebugInfos.values().iterator();

         while(true) {
            if (!iter.hasNext()) {
               break;
            }

            sumlist.addAll(((TemplateDebugInfo)iter.next()).breakpoints);
         }
      }

      Collections.sort(sumlist);
      return sumlist;
   }

   boolean suspendEnvironmentSpi(Environment env, String templateName, int line) throws RemoteException {
      RmiDebuggedEnvironmentImpl denv = (RmiDebuggedEnvironmentImpl)RmiDebuggedEnvironmentImpl.getCachedWrapperFor(env);
      synchronized(this.suspendedEnvironments) {
         this.suspendedEnvironments.add(denv);
      }

      boolean var22 = false;

      boolean var6;
      try {
         var22 = true;
         EnvironmentSuspendedEvent breakpointEvent = new EnvironmentSuspendedEvent(this, templateName, line, denv);
         synchronized(this.listeners) {
            Iterator iter = this.listeners.values().iterator();

            while(true) {
               if (!iter.hasNext()) {
                  break;
               }

               DebuggerListener listener = (DebuggerListener)iter.next();
               listener.environmentSuspended(breakpointEvent);
            }
         }

         synchronized(denv) {
            try {
               denv.wait();
            } catch (InterruptedException var25) {
            }
         }

         var6 = denv.isStopped();
         var22 = false;
      } finally {
         if (var22) {
            synchronized(this.suspendedEnvironments) {
               this.suspendedEnvironments.remove(denv);
            }
         }
      }

      synchronized(this.suspendedEnvironments) {
         this.suspendedEnvironments.remove(denv);
         return var6;
      }
   }

   void registerTemplateSpi(Template template) {
      String templateName = template.getName();
      synchronized(this.templateDebugInfos) {
         TemplateDebugInfo tdi = this.createTemplateDebugInfo(templateName);
         tdi.templates.add(new TemplateReference(templateName, template, this.refQueue));
         Iterator iter = tdi.breakpoints.iterator();

         while(iter.hasNext()) {
            Breakpoint breakpoint = (Breakpoint)iter.next();
            insertDebugBreak(template, breakpoint);
         }

      }
   }

   Collection getSuspendedEnvironments() {
      return (Collection)this.suspendedEnvironments.clone();
   }

   Object addDebuggerListener(DebuggerListener listener) {
      synchronized(this.listeners) {
         Object id = System.currentTimeMillis();
         this.listeners.put(id, listener);
         return id;
      }
   }

   void removeDebuggerListener(Object id) {
      synchronized(this.listeners) {
         this.listeners.remove(id);
      }
   }

   void addBreakpoint(Breakpoint breakpoint) {
      String templateName = breakpoint.getTemplateName();
      synchronized(this.templateDebugInfos) {
         TemplateDebugInfo tdi = this.createTemplateDebugInfo(templateName);
         List breakpoints = tdi.breakpoints;
         int pos = Collections.binarySearch(breakpoints, breakpoint);
         if (pos < 0) {
            breakpoints.add(-pos - 1, breakpoint);
            Iterator iter = tdi.templates.iterator();

            while(iter.hasNext()) {
               TemplateReference ref = (TemplateReference)iter.next();
               Template t = ref.getTemplate();
               if (t == null) {
                  iter.remove();
               } else {
                  insertDebugBreak(t, breakpoint);
               }
            }
         }

      }
   }

   private static void insertDebugBreak(Template t, Breakpoint breakpoint) {
      TemplateElement te = findTemplateElement(t.getRootTreeNode(), breakpoint.getLine());
      if (te != null) {
         TemplateElement parent = _CoreAPI.getParentElement(te);
         DebugBreak db = new DebugBreak(te);
         parent.setChildAt(parent.getIndex(te), db);
      }
   }

   private static TemplateElement findTemplateElement(TemplateElement te, int line) {
      if (te.getBeginLine() <= line && te.getEndLine() >= line) {
         List childMatches = new ArrayList();
         Enumeration children = te.children();

         TemplateElement e;
         while(children.hasMoreElements()) {
            TemplateElement child = (TemplateElement)children.nextElement();
            e = findTemplateElement(child, line);
            if (e != null) {
               childMatches.add(e);
            }
         }

         TemplateElement bestMatch = null;

         for(int i = 0; i < childMatches.size(); ++i) {
            e = (TemplateElement)childMatches.get(i);
            if (bestMatch == null) {
               bestMatch = e;
            }

            if (e.getBeginLine() == line && e.getEndLine() > line) {
               bestMatch = e;
            }

            if (e.getBeginLine() == e.getEndLine() && e.getBeginLine() == line) {
               bestMatch = e;
               break;
            }
         }

         return bestMatch != null ? bestMatch : te;
      } else {
         return null;
      }
   }

   private TemplateDebugInfo findTemplateDebugInfo(String templateName) {
      this.processRefQueue();
      return (TemplateDebugInfo)this.templateDebugInfos.get(templateName);
   }

   private TemplateDebugInfo createTemplateDebugInfo(String templateName) {
      TemplateDebugInfo tdi = this.findTemplateDebugInfo(templateName);
      if (tdi == null) {
         tdi = new TemplateDebugInfo();
         this.templateDebugInfos.put(templateName, tdi);
      }

      return tdi;
   }

   void removeBreakpoint(Breakpoint breakpoint) {
      String templateName = breakpoint.getTemplateName();
      synchronized(this.templateDebugInfos) {
         TemplateDebugInfo tdi = this.findTemplateDebugInfo(templateName);
         if (tdi != null) {
            List breakpoints = tdi.breakpoints;
            int pos = Collections.binarySearch(breakpoints, breakpoint);
            if (pos >= 0) {
               breakpoints.remove(pos);
               Iterator iter = tdi.templates.iterator();

               while(iter.hasNext()) {
                  TemplateReference ref = (TemplateReference)iter.next();
                  Template t = ref.getTemplate();
                  if (t == null) {
                     iter.remove();
                  } else {
                     this.removeDebugBreak(t, breakpoint);
                  }
               }
            }

            if (tdi.isEmpty()) {
               this.templateDebugInfos.remove(templateName);
            }
         }

      }
   }

   private void removeDebugBreak(Template t, Breakpoint breakpoint) {
      TemplateElement te = findTemplateElement(t.getRootTreeNode(), breakpoint.getLine());
      if (te != null) {
         DebugBreak db;
         for(db = null; te != null; te = _CoreAPI.getParentElement(te)) {
            if (te instanceof DebugBreak) {
               db = (DebugBreak)te;
               break;
            }
         }

         if (db != null) {
            TemplateElement parent = _CoreAPI.getParentElement(db);
            parent.setChildAt(parent.getIndex(db), _CoreAPI.getChildElement(db, 0));
         }
      }
   }

   void removeBreakpoints(String templateName) {
      synchronized(this.templateDebugInfos) {
         TemplateDebugInfo tdi = this.findTemplateDebugInfo(templateName);
         if (tdi != null) {
            this.removeBreakpoints(tdi);
            if (tdi.isEmpty()) {
               this.templateDebugInfos.remove(templateName);
            }
         }

      }
   }

   void removeBreakpoints() {
      synchronized(this.templateDebugInfos) {
         Iterator iter = this.templateDebugInfos.values().iterator();

         while(iter.hasNext()) {
            TemplateDebugInfo tdi = (TemplateDebugInfo)iter.next();
            this.removeBreakpoints(tdi);
            if (tdi.isEmpty()) {
               iter.remove();
            }
         }

      }
   }

   private void removeBreakpoints(TemplateDebugInfo tdi) {
      tdi.breakpoints.clear();
      Iterator iter = tdi.templates.iterator();

      while(iter.hasNext()) {
         TemplateReference ref = (TemplateReference)iter.next();
         Template t = ref.getTemplate();
         if (t == null) {
            iter.remove();
         } else {
            this.removeDebugBreaks(t.getRootTreeNode());
         }
      }

   }

   private void removeDebugBreaks(TemplateElement te) {
      int count = te.getChildCount();

      for(int i = 0; i < count; ++i) {
         TemplateElement child;
         TemplateElement dbchild;
         for(child = _CoreAPI.getChildElement(te, i); child instanceof DebugBreak; child = dbchild) {
            dbchild = _CoreAPI.getChildElement(child, 0);
            te.setChildAt(i, dbchild);
         }

         this.removeDebugBreaks(child);
      }

   }

   private void processRefQueue() {
      while(true) {
         TemplateReference ref = (TemplateReference)this.refQueue.poll();
         if (ref == null) {
            return;
         }

         TemplateDebugInfo tdi = this.findTemplateDebugInfo(ref.templateName);
         if (tdi != null) {
            tdi.templates.remove(ref);
            if (tdi.isEmpty()) {
               this.templateDebugInfos.remove(ref.templateName);
            }
         }
      }
   }

   void shutdownSpi() {
      this.server.stop();

      try {
         UnicastRemoteObject.unexportObject(this.debugger, true);
      } catch (Exception var2) {
      }

      RmiDebuggedEnvironmentImpl.cleanup();
   }

   private static final class TemplateReference extends WeakReference {
      final String templateName;

      TemplateReference(String templateName, Template template, ReferenceQueue queue) {
         super(template, queue);
         this.templateName = templateName;
      }

      Template getTemplate() {
         return (Template)this.get();
      }
   }

   private static final class TemplateDebugInfo {
      final List templates;
      final List breakpoints;

      private TemplateDebugInfo() {
         this.templates = new ArrayList();
         this.breakpoints = new ArrayList();
      }

      boolean isEmpty() {
         return this.templates.isEmpty() && this.breakpoints.isEmpty();
      }

      // $FF: synthetic method
      TemplateDebugInfo(Object x0) {
         this();
      }
   }
}
