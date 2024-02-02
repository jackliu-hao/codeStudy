/*     */ package freemarker.debug.impl;
/*     */ 
/*     */ import freemarker.core.DebugBreak;
/*     */ import freemarker.core.Environment;
/*     */ import freemarker.core.TemplateElement;
/*     */ import freemarker.core._CoreAPI;
/*     */ import freemarker.debug.Breakpoint;
/*     */ import freemarker.debug.DebuggerListener;
/*     */ import freemarker.debug.EnvironmentSuspendedEvent;
/*     */ import freemarker.template.Template;
/*     */ import freemarker.template.utility.UndeclaredThrowableException;
/*     */ import java.io.Serializable;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.rmi.RemoteException;
/*     */ import java.rmi.server.RemoteObject;
/*     */ import java.rmi.server.UnicastRemoteObject;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.tree.TreeNode;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class RmiDebuggerService
/*     */   extends DebuggerService
/*     */ {
/*  55 */   private final Map templateDebugInfos = new HashMap<>();
/*  56 */   private final HashSet suspendedEnvironments = new HashSet();
/*  57 */   private final Map listeners = new HashMap<>();
/*  58 */   private final ReferenceQueue refQueue = new ReferenceQueue();
/*     */   
/*     */   private final RmiDebuggerImpl debugger;
/*     */   
/*     */   private DebuggerServer server;
/*     */   
/*     */   RmiDebuggerService() {
/*     */     try {
/*  66 */       this.debugger = new RmiDebuggerImpl(this);
/*  67 */       this.server = new DebuggerServer((Serializable)RemoteObject.toStub(this.debugger));
/*  68 */       this.server.start();
/*  69 */     } catch (RemoteException e) {
/*  70 */       e.printStackTrace();
/*  71 */       throw new UndeclaredThrowableException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   List getBreakpointsSpi(String templateName) {
/*  77 */     synchronized (this.templateDebugInfos) {
/*  78 */       TemplateDebugInfo tdi = findTemplateDebugInfo(templateName);
/*  79 */       return (tdi == null) ? Collections.EMPTY_LIST : tdi.breakpoints;
/*     */     } 
/*     */   }
/*     */   
/*     */   List getBreakpointsSpi() {
/*  84 */     List<Comparable> sumlist = new ArrayList();
/*  85 */     synchronized (this.templateDebugInfos) {
/*  86 */       for (Iterator iter = this.templateDebugInfos.values().iterator(); iter.hasNext();) {
/*  87 */         sumlist.addAll(((TemplateDebugInfo)iter.next()).breakpoints);
/*     */       }
/*     */     } 
/*  90 */     Collections.sort(sumlist);
/*  91 */     return sumlist;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean suspendEnvironmentSpi(Environment env, String templateName, int line) throws RemoteException {
/* 101 */     RmiDebuggedEnvironmentImpl denv = (RmiDebuggedEnvironmentImpl)RmiDebuggedEnvironmentImpl.getCachedWrapperFor(env);
/*     */     
/* 103 */     synchronized (this.suspendedEnvironments) {
/* 104 */       this.suspendedEnvironments.add(denv);
/*     */     } 
/*     */     try {
/* 107 */       EnvironmentSuspendedEvent breakpointEvent = new EnvironmentSuspendedEvent(this, templateName, line, denv);
/*     */ 
/*     */       
/* 110 */       synchronized (this.listeners) {
/* 111 */         for (Iterator<DebuggerListener> iter = this.listeners.values().iterator(); iter.hasNext(); ) {
/* 112 */           DebuggerListener listener = iter.next();
/* 113 */           listener.environmentSuspended(breakpointEvent);
/*     */         } 
/*     */       } 
/* 116 */       synchronized (denv) {
/*     */         try {
/* 118 */           denv.wait();
/* 119 */         } catch (InterruptedException interruptedException) {}
/*     */       } 
/*     */ 
/*     */       
/* 123 */       return denv.isStopped();
/*     */     } finally {
/* 125 */       synchronized (this.suspendedEnvironments) {
/* 126 */         this.suspendedEnvironments.remove(denv);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   void registerTemplateSpi(Template template) {
/* 133 */     String templateName = template.getName();
/* 134 */     synchronized (this.templateDebugInfos) {
/* 135 */       TemplateDebugInfo tdi = createTemplateDebugInfo(templateName);
/* 136 */       tdi.templates.add(new TemplateReference(templateName, template, this.refQueue));
/*     */       
/* 138 */       for (Iterator<Breakpoint> iter = tdi.breakpoints.iterator(); iter.hasNext(); ) {
/* 139 */         Breakpoint breakpoint = iter.next();
/* 140 */         insertDebugBreak(template, breakpoint);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   Collection getSuspendedEnvironments() {
/* 146 */     return (Collection)this.suspendedEnvironments.clone();
/*     */   }
/*     */   
/*     */   Object addDebuggerListener(DebuggerListener listener) {
/*     */     Object id;
/* 151 */     synchronized (this.listeners) {
/* 152 */       id = Long.valueOf(System.currentTimeMillis());
/* 153 */       this.listeners.put(id, listener);
/*     */     } 
/* 155 */     return id;
/*     */   }
/*     */   
/*     */   void removeDebuggerListener(Object id) {
/* 159 */     synchronized (this.listeners) {
/* 160 */       this.listeners.remove(id);
/*     */     } 
/*     */   }
/*     */   
/*     */   void addBreakpoint(Breakpoint breakpoint) {
/* 165 */     String templateName = breakpoint.getTemplateName();
/* 166 */     synchronized (this.templateDebugInfos) {
/* 167 */       TemplateDebugInfo tdi = createTemplateDebugInfo(templateName);
/* 168 */       List<? extends Comparable<? super Breakpoint>> breakpoints = tdi.breakpoints;
/* 169 */       int pos = Collections.binarySearch(breakpoints, breakpoint);
/* 170 */       if (pos < 0) {
/*     */         
/* 172 */         breakpoints.add(-pos - 1, breakpoint);
/*     */         
/* 174 */         for (Iterator<TemplateReference> iter = tdi.templates.iterator(); iter.hasNext(); ) {
/* 175 */           TemplateReference ref = iter.next();
/* 176 */           Template t = ref.getTemplate();
/* 177 */           if (t == null) {
/* 178 */             iter.remove(); continue;
/*     */           } 
/* 180 */           insertDebugBreak(t, breakpoint);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void insertDebugBreak(Template t, Breakpoint breakpoint) {
/* 188 */     TemplateElement te = findTemplateElement(t.getRootTreeNode(), breakpoint.getLine());
/* 189 */     if (te == null) {
/*     */       return;
/*     */     }
/* 192 */     TemplateElement parent = _CoreAPI.getParentElement(te);
/* 193 */     DebugBreak db = new DebugBreak(te);
/*     */ 
/*     */ 
/*     */     
/* 197 */     parent.setChildAt(parent.getIndex((TreeNode)te), (TemplateElement)db);
/*     */   }
/*     */   
/*     */   private static TemplateElement findTemplateElement(TemplateElement te, int line) {
/* 201 */     if (te.getBeginLine() > line || te.getEndLine() < line) {
/* 202 */       return null;
/*     */     }
/*     */     
/* 205 */     List<TemplateElement> childMatches = new ArrayList();
/* 206 */     for (Enumeration<TemplateElement> children = te.children(); children.hasMoreElements(); ) {
/* 207 */       TemplateElement child = children.nextElement();
/* 208 */       TemplateElement childmatch = findTemplateElement(child, line);
/* 209 */       if (childmatch != null) {
/* 210 */         childMatches.add(childmatch);
/*     */       }
/*     */     } 
/*     */     
/* 214 */     TemplateElement bestMatch = null;
/* 215 */     for (int i = 0; i < childMatches.size(); i++) {
/* 216 */       TemplateElement e = childMatches.get(i);
/*     */       
/* 218 */       if (bestMatch == null) {
/* 219 */         bestMatch = e;
/*     */       }
/*     */       
/* 222 */       if (e.getBeginLine() == line && e.getEndLine() > line) {
/* 223 */         bestMatch = e;
/*     */       }
/*     */       
/* 226 */       if (e.getBeginLine() == e.getEndLine() && e.getBeginLine() == line) {
/* 227 */         bestMatch = e;
/*     */         break;
/*     */       } 
/*     */     } 
/* 231 */     if (bestMatch != null) {
/* 232 */       return bestMatch;
/*     */     }
/*     */     
/* 235 */     return te;
/*     */   }
/*     */   
/*     */   private TemplateDebugInfo findTemplateDebugInfo(String templateName) {
/* 239 */     processRefQueue();
/* 240 */     return (TemplateDebugInfo)this.templateDebugInfos.get(templateName);
/*     */   }
/*     */   
/*     */   private TemplateDebugInfo createTemplateDebugInfo(String templateName) {
/* 244 */     TemplateDebugInfo tdi = findTemplateDebugInfo(templateName);
/* 245 */     if (tdi == null) {
/* 246 */       tdi = new TemplateDebugInfo();
/* 247 */       this.templateDebugInfos.put(templateName, tdi);
/*     */     } 
/* 249 */     return tdi;
/*     */   }
/*     */   
/*     */   void removeBreakpoint(Breakpoint breakpoint) {
/* 253 */     String templateName = breakpoint.getTemplateName();
/* 254 */     synchronized (this.templateDebugInfos) {
/* 255 */       TemplateDebugInfo tdi = findTemplateDebugInfo(templateName);
/* 256 */       if (tdi != null) {
/* 257 */         List<? extends Comparable<? super Breakpoint>> breakpoints = tdi.breakpoints;
/* 258 */         int pos = Collections.binarySearch(breakpoints, breakpoint);
/* 259 */         if (pos >= 0) {
/* 260 */           breakpoints.remove(pos);
/* 261 */           for (Iterator<TemplateReference> iter = tdi.templates.iterator(); iter.hasNext(); ) {
/* 262 */             TemplateReference ref = iter.next();
/* 263 */             Template t = ref.getTemplate();
/* 264 */             if (t == null) {
/* 265 */               iter.remove(); continue;
/*     */             } 
/* 267 */             removeDebugBreak(t, breakpoint);
/*     */           } 
/*     */         } 
/*     */         
/* 271 */         if (tdi.isEmpty()) {
/* 272 */           this.templateDebugInfos.remove(templateName);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void removeDebugBreak(Template t, Breakpoint breakpoint) {
/* 279 */     TemplateElement te = findTemplateElement(t.getRootTreeNode(), breakpoint.getLine());
/* 280 */     if (te == null) {
/*     */       return;
/*     */     }
/* 283 */     DebugBreak db = null;
/* 284 */     while (te != null) {
/* 285 */       if (te instanceof DebugBreak) {
/* 286 */         db = (DebugBreak)te;
/*     */         break;
/*     */       } 
/* 289 */       te = _CoreAPI.getParentElement(te);
/*     */     } 
/* 291 */     if (db == null) {
/*     */       return;
/*     */     }
/* 294 */     TemplateElement parent = _CoreAPI.getParentElement((TemplateElement)db);
/* 295 */     parent.setChildAt(parent.getIndex((TreeNode)db), _CoreAPI.getChildElement((TemplateElement)db, 0));
/*     */   }
/*     */   
/*     */   void removeBreakpoints(String templateName) {
/* 299 */     synchronized (this.templateDebugInfos) {
/* 300 */       TemplateDebugInfo tdi = findTemplateDebugInfo(templateName);
/* 301 */       if (tdi != null) {
/* 302 */         removeBreakpoints(tdi);
/* 303 */         if (tdi.isEmpty()) {
/* 304 */           this.templateDebugInfos.remove(templateName);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   void removeBreakpoints() {
/* 311 */     synchronized (this.templateDebugInfos) {
/* 312 */       for (Iterator<TemplateDebugInfo> iter = this.templateDebugInfos.values().iterator(); iter.hasNext(); ) {
/* 313 */         TemplateDebugInfo tdi = iter.next();
/* 314 */         removeBreakpoints(tdi);
/* 315 */         if (tdi.isEmpty()) {
/* 316 */           iter.remove();
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void removeBreakpoints(TemplateDebugInfo tdi) {
/* 323 */     tdi.breakpoints.clear();
/* 324 */     for (Iterator<TemplateReference> iter = tdi.templates.iterator(); iter.hasNext(); ) {
/* 325 */       TemplateReference ref = iter.next();
/* 326 */       Template t = ref.getTemplate();
/* 327 */       if (t == null) {
/* 328 */         iter.remove(); continue;
/*     */       } 
/* 330 */       removeDebugBreaks(t.getRootTreeNode());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void removeDebugBreaks(TemplateElement te) {
/* 336 */     int count = te.getChildCount();
/* 337 */     for (int i = 0; i < count; i++) {
/* 338 */       TemplateElement child = _CoreAPI.getChildElement(te, i);
/* 339 */       while (child instanceof DebugBreak) {
/* 340 */         TemplateElement dbchild = _CoreAPI.getChildElement(child, 0);
/* 341 */         te.setChildAt(i, dbchild);
/* 342 */         child = dbchild;
/*     */       } 
/* 344 */       removeDebugBreaks(child);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static final class TemplateDebugInfo {
/* 349 */     final List templates = new ArrayList();
/* 350 */     final List breakpoints = new ArrayList();
/*     */     
/*     */     boolean isEmpty() {
/* 353 */       return (this.templates.isEmpty() && this.breakpoints.isEmpty());
/*     */     }
/*     */     
/*     */     private TemplateDebugInfo() {}
/*     */   }
/*     */   
/*     */   private static final class TemplateReference extends WeakReference {
/*     */     TemplateReference(String templateName, Template template, ReferenceQueue<? super T> queue) {
/* 361 */       super((T)template, queue);
/* 362 */       this.templateName = templateName;
/*     */     }
/*     */     final String templateName;
/*     */     Template getTemplate() {
/* 366 */       return (Template)get();
/*     */     }
/*     */   }
/*     */   
/*     */   private void processRefQueue() {
/*     */     while (true) {
/* 372 */       TemplateReference ref = (TemplateReference)this.refQueue.poll();
/* 373 */       if (ref == null) {
/*     */         break;
/*     */       }
/* 376 */       TemplateDebugInfo tdi = findTemplateDebugInfo(ref.templateName);
/* 377 */       if (tdi != null) {
/* 378 */         tdi.templates.remove(ref);
/* 379 */         if (tdi.isEmpty()) {
/* 380 */           this.templateDebugInfos.remove(ref.templateName);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   void shutdownSpi() {
/* 388 */     this.server.stop();
/*     */     try {
/* 390 */       UnicastRemoteObject.unexportObject(this.debugger, true);
/* 391 */     } catch (Exception exception) {}
/*     */ 
/*     */     
/* 394 */     RmiDebuggedEnvironmentImpl.cleanup();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\debug\impl\RmiDebuggerService.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */