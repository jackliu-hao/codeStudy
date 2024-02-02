/*     */ package freemarker.ext.jsp;
/*     */ 
/*     */ import freemarker.core.Environment;
/*     */ import freemarker.ext.servlet.HttpRequestHashModel;
/*     */ import freemarker.ext.servlet.ServletContextHashModel;
/*     */ import freemarker.ext.util.WrapperTemplateModel;
/*     */ import freemarker.template.AdapterTemplateModel;
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.ObjectWrapperAndUnwrapper;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateDateModel;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateModelIterator;
/*     */ import freemarker.template.TemplateNumberModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import freemarker.template.utility.UndeclaredThrowableException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Writer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import javax.servlet.GenericServlet;
/*     */ import javax.servlet.Servlet;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletOutputStream;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.servlet.http.HttpServletResponseWrapper;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import javax.servlet.jsp.JspWriter;
/*     */ import javax.servlet.jsp.PageContext;
/*     */ import javax.servlet.jsp.tagext.BodyContent;
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
/*     */ abstract class FreeMarkerPageContext
/*     */   extends PageContext
/*     */   implements TemplateModel
/*     */ {
/*  70 */   private static final Class OBJECT_CLASS = Object.class;
/*     */   
/*     */   private final Environment environment;
/*     */   private final int incompatibleImprovements;
/*  74 */   private List tags = new ArrayList();
/*  75 */   private List outs = new ArrayList();
/*     */   private final GenericServlet servlet;
/*     */   private HttpSession session;
/*     */   private final HttpServletRequest request;
/*     */   private final HttpServletResponse response;
/*     */   private final ObjectWrapper wrapper;
/*     */   private final ObjectWrapperAndUnwrapper unwrapper;
/*     */   private JspWriter jspOut;
/*     */   
/*     */   protected FreeMarkerPageContext() throws TemplateModelException {
/*  85 */     this.environment = Environment.getCurrentEnvironment();
/*  86 */     this.incompatibleImprovements = this.environment.getConfiguration().getIncompatibleImprovements().intValue();
/*     */     
/*  88 */     TemplateModel appModel = this.environment.getGlobalVariable("__FreeMarkerServlet.Application__");
/*     */     
/*  90 */     if (!(appModel instanceof ServletContextHashModel)) {
/*  91 */       appModel = this.environment.getGlobalVariable("Application");
/*     */     }
/*     */     
/*  94 */     if (appModel instanceof ServletContextHashModel) {
/*  95 */       this.servlet = ((ServletContextHashModel)appModel).getServlet();
/*     */     } else {
/*  97 */       throw new TemplateModelException("Could not find an instance of " + ServletContextHashModel.class
/*  98 */           .getName() + " in the data model under either the name " + "__FreeMarkerServlet.Application__" + " or " + "Application");
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 105 */     TemplateModel requestModel = this.environment.getGlobalVariable("__FreeMarkerServlet.Request__");
/* 106 */     if (!(requestModel instanceof HttpRequestHashModel)) {
/* 107 */       requestModel = this.environment.getGlobalVariable("Request");
/*     */     }
/*     */     
/* 110 */     if (requestModel instanceof HttpRequestHashModel) {
/* 111 */       HttpRequestHashModel reqHash = (HttpRequestHashModel)requestModel;
/* 112 */       this.request = reqHash.getRequest();
/* 113 */       this.session = this.request.getSession(false);
/* 114 */       this.response = reqHash.getResponse();
/* 115 */       this.wrapper = reqHash.getObjectWrapper();
/* 116 */       this.unwrapper = (this.wrapper instanceof ObjectWrapperAndUnwrapper) ? (ObjectWrapperAndUnwrapper)this.wrapper : null;
/*     */     } else {
/*     */       
/* 119 */       throw new TemplateModelException("Could not find an instance of " + HttpRequestHashModel.class
/* 120 */           .getName() + " in the data model under either the name " + "__FreeMarkerServlet.Request__" + " or " + "Request");
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 127 */     setAttribute("javax.servlet.jsp.jspRequest", this.request);
/* 128 */     setAttribute("javax.servlet.jsp.jspResponse", this.response);
/* 129 */     if (this.session != null)
/* 130 */       setAttribute("javax.servlet.jsp.jspSession", this.session); 
/* 131 */     setAttribute("javax.servlet.jsp.jspPage", this.servlet);
/* 132 */     setAttribute("javax.servlet.jsp.jspConfig", this.servlet.getServletConfig());
/* 133 */     setAttribute("javax.servlet.jsp.jspPageContext", this);
/* 134 */     setAttribute("javax.servlet.jsp.jspApplication", this.servlet.getServletContext());
/*     */   }
/*     */   
/*     */   ObjectWrapper getObjectWrapper() {
/* 138 */     return this.wrapper;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void initialize(Servlet servlet, ServletRequest request, ServletResponse response, String errorPageURL, boolean needsSession, int bufferSize, boolean autoFlush) {
/* 146 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void release() {}
/*     */ 
/*     */   
/*     */   public void setAttribute(String name, Object value) {
/* 155 */     setAttribute(name, value, 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAttribute(String name, Object value, int scope) {
/* 160 */     switch (scope) {
/*     */       case 1:
/*     */         try {
/* 163 */           this.environment.setGlobalVariable(name, this.wrapper.wrap(value));
/*     */         }
/* 165 */         catch (TemplateModelException e) {
/* 166 */           throw new UndeclaredThrowableException(e);
/*     */         } 
/*     */         return;
/*     */       case 2:
/* 170 */         getRequest().setAttribute(name, value);
/*     */         return;
/*     */       
/*     */       case 3:
/* 174 */         getSession(true).setAttribute(name, value);
/*     */         return;
/*     */       
/*     */       case 4:
/* 178 */         getServletContext().setAttribute(name, value);
/*     */         return;
/*     */     } 
/*     */     
/* 182 */     throw new IllegalArgumentException("Invalid scope " + scope);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getAttribute(String name) {
/* 189 */     return getAttribute(name, 1);
/*     */   }
/*     */   
/*     */   public Object getAttribute(String name, int scope) {
/*     */     HttpSession session;
/* 194 */     switch (scope) {
/*     */       case 1:
/*     */         try {
/* 197 */           TemplateModel tm = this.environment.getGlobalNamespace().get(name);
/* 198 */           if (this.incompatibleImprovements >= _TemplateAPI.VERSION_INT_2_3_22 && this.unwrapper != null) {
/* 199 */             return this.unwrapper.unwrap(tm);
/*     */           }
/* 201 */           if (tm instanceof AdapterTemplateModel) {
/* 202 */             return ((AdapterTemplateModel)tm).getAdaptedObject(OBJECT_CLASS);
/*     */           }
/* 204 */           if (tm instanceof WrapperTemplateModel) {
/* 205 */             return ((WrapperTemplateModel)tm).getWrappedObject();
/*     */           }
/* 207 */           if (tm instanceof TemplateScalarModel) {
/* 208 */             return ((TemplateScalarModel)tm).getAsString();
/*     */           }
/* 210 */           if (tm instanceof TemplateNumberModel) {
/* 211 */             return ((TemplateNumberModel)tm).getAsNumber();
/*     */           }
/* 213 */           if (tm instanceof TemplateBooleanModel) {
/* 214 */             return Boolean.valueOf(((TemplateBooleanModel)tm).getAsBoolean());
/*     */           }
/* 216 */           if (this.incompatibleImprovements >= _TemplateAPI.VERSION_INT_2_3_22 && tm instanceof TemplateDateModel)
/*     */           {
/* 218 */             return ((TemplateDateModel)tm).getAsDate();
/*     */           }
/* 220 */           return tm;
/*     */         }
/* 222 */         catch (TemplateModelException e) {
/* 223 */           throw new UndeclaredThrowableException("Failed to unwrapp FTL global variable", e);
/*     */         } 
/*     */       
/*     */       case 2:
/* 227 */         return getRequest().getAttribute(name);
/*     */       
/*     */       case 3:
/* 230 */         session = getSession(false);
/* 231 */         if (session == null) {
/* 232 */           return null;
/*     */         }
/* 234 */         return session.getAttribute(name);
/*     */       
/*     */       case 4:
/* 237 */         return getServletContext().getAttribute(name);
/*     */     } 
/*     */     
/* 240 */     throw new IllegalArgumentException("Invalid scope " + scope);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object findAttribute(String name) {
/* 247 */     Object retval = getAttribute(name, 1);
/* 248 */     if (retval != null) return retval; 
/* 249 */     retval = getAttribute(name, 2);
/* 250 */     if (retval != null) return retval; 
/* 251 */     retval = getAttribute(name, 3);
/* 252 */     if (retval != null) return retval; 
/* 253 */     return getAttribute(name, 4);
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeAttribute(String name) {
/* 258 */     removeAttribute(name, 1);
/* 259 */     removeAttribute(name, 2);
/* 260 */     removeAttribute(name, 3);
/* 261 */     removeAttribute(name, 4);
/*     */   }
/*     */   
/*     */   public void removeAttribute(String name, int scope) {
/*     */     HttpSession session;
/* 266 */     switch (scope) {
/*     */       case 1:
/* 268 */         this.environment.getGlobalNamespace().remove(name);
/*     */         return;
/*     */       
/*     */       case 2:
/* 272 */         getRequest().removeAttribute(name);
/*     */         return;
/*     */       
/*     */       case 3:
/* 276 */         session = getSession(false);
/* 277 */         if (session != null) {
/* 278 */           session.removeAttribute(name);
/*     */         }
/*     */         return;
/*     */       
/*     */       case 4:
/* 283 */         getServletContext().removeAttribute(name);
/*     */         return;
/*     */     } 
/*     */     
/* 287 */     throw new IllegalArgumentException("Invalid scope: " + scope);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getAttributesScope(String name) {
/* 294 */     if (getAttribute(name, 1) != null) return 1; 
/* 295 */     if (getAttribute(name, 2) != null) return 2; 
/* 296 */     if (getAttribute(name, 3) != null) return 3; 
/* 297 */     if (getAttribute(name, 4) != null) return 4; 
/* 298 */     return 0;
/*     */   }
/*     */   
/*     */   public Enumeration getAttributeNamesInScope(int scope) {
/*     */     HttpSession session;
/* 303 */     switch (scope) {
/*     */       case 1:
/*     */         try {
/* 306 */           return new TemplateHashModelExEnumeration((TemplateHashModelEx)this.environment
/* 307 */               .getGlobalNamespace());
/* 308 */         } catch (TemplateModelException e) {
/* 309 */           throw new UndeclaredThrowableException(e);
/*     */         } 
/*     */       
/*     */       case 2:
/* 313 */         return getRequest().getAttributeNames();
/*     */       
/*     */       case 3:
/* 316 */         session = getSession(false);
/* 317 */         if (session != null) {
/* 318 */           return session.getAttributeNames();
/*     */         }
/* 320 */         return Collections.enumeration(Collections.EMPTY_SET);
/*     */       
/*     */       case 4:
/* 323 */         return getServletContext().getAttributeNames();
/*     */     } 
/*     */     
/* 326 */     throw new IllegalArgumentException("Invalid scope " + scope);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JspWriter getOut() {
/* 333 */     return this.jspOut;
/*     */   }
/*     */   
/*     */   private HttpSession getSession(boolean create) {
/* 337 */     if (this.session == null) {
/* 338 */       this.session = this.request.getSession(create);
/* 339 */       if (this.session != null) {
/* 340 */         setAttribute("javax.servlet.jsp.jspSession", this.session);
/*     */       }
/*     */     } 
/* 343 */     return this.session;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpSession getSession() {
/* 348 */     return getSession(false);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getPage() {
/* 353 */     return this.servlet;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServletRequest getRequest() {
/* 358 */     return (ServletRequest)this.request;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServletResponse getResponse() {
/* 363 */     return (ServletResponse)this.response;
/*     */   }
/*     */ 
/*     */   
/*     */   public Exception getException() {
/* 368 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ServletConfig getServletConfig() {
/* 373 */     return this.servlet.getServletConfig();
/*     */   }
/*     */ 
/*     */   
/*     */   public ServletContext getServletContext() {
/* 378 */     return this.servlet.getServletContext();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void forward(String url) throws ServletException, IOException {
/* 384 */     this.request.getRequestDispatcher(url).forward((ServletRequest)this.request, (ServletResponse)this.response);
/*     */   }
/*     */ 
/*     */   
/*     */   public void include(String url) throws ServletException, IOException {
/* 389 */     this.jspOut.flush();
/* 390 */     this.request.getRequestDispatcher(url).include((ServletRequest)this.request, (ServletResponse)this.response);
/*     */   }
/*     */ 
/*     */   
/*     */   public void include(String url, boolean flush) throws ServletException, IOException {
/* 395 */     if (flush) {
/* 396 */       this.jspOut.flush();
/*     */     }
/* 398 */     final PrintWriter pw = new PrintWriter((Writer)this.jspOut);
/* 399 */     this.request.getRequestDispatcher(url).include((ServletRequest)this.request, (ServletResponse)new HttpServletResponseWrapper(this.response)
/*     */         {
/*     */           public PrintWriter getWriter() {
/* 402 */             return pw;
/*     */           }
/*     */ 
/*     */           
/*     */           public ServletOutputStream getOutputStream() {
/* 407 */             throw new UnsupportedOperationException("JSP-included resource must use getWriter()");
/*     */           }
/*     */         });
/* 410 */     pw.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlePageException(Exception e) {
/* 415 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlePageException(Throwable e) {
/* 420 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public BodyContent pushBody() {
/* 425 */     return (BodyContent)pushWriter((JspWriter)new TagTransformModel.BodyContentImpl(getOut(), true));
/*     */   }
/*     */ 
/*     */   
/*     */   public JspWriter pushBody(Writer w) {
/* 430 */     return pushWriter(new JspWriterAdapter(w));
/*     */   }
/*     */ 
/*     */   
/*     */   public JspWriter popBody() {
/* 435 */     popWriter();
/* 436 */     return (JspWriter)getAttribute("javax.servlet.jsp.jspOut");
/*     */   }
/*     */   
/*     */   Object peekTopTag(Class tagClass) {
/* 440 */     for (ListIterator iter = this.tags.listIterator(this.tags.size()); iter.hasPrevious(); ) {
/* 441 */       Object tag = iter.previous();
/* 442 */       if (tagClass.isInstance(tag)) {
/* 443 */         return tag;
/*     */       }
/*     */     } 
/* 446 */     return null;
/*     */   }
/*     */   
/*     */   void popTopTag() {
/* 450 */     this.tags.remove(this.tags.size() - 1);
/*     */   }
/*     */   
/*     */   void popWriter() {
/* 454 */     this.jspOut = this.outs.remove(this.outs.size() - 1);
/* 455 */     setAttribute("javax.servlet.jsp.jspOut", this.jspOut);
/*     */   }
/*     */   
/*     */   void pushTopTag(Object tag) {
/* 459 */     this.tags.add(tag);
/*     */   }
/*     */   
/*     */   JspWriter pushWriter(JspWriter out) {
/* 463 */     this.outs.add(this.jspOut);
/* 464 */     this.jspOut = out;
/* 465 */     setAttribute("javax.servlet.jsp.jspOut", this.jspOut);
/* 466 */     return out;
/*     */   }
/*     */   
/*     */   private static class TemplateHashModelExEnumeration implements Enumeration {
/*     */     private final TemplateModelIterator it;
/*     */     
/*     */     private TemplateHashModelExEnumeration(TemplateHashModelEx hashEx) throws TemplateModelException {
/* 473 */       this.it = hashEx.keys().iterator();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasMoreElements() {
/*     */       try {
/* 479 */         return this.it.hasNext();
/* 480 */       } catch (TemplateModelException tme) {
/* 481 */         throw new UndeclaredThrowableException(tme);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public Object nextElement() {
/*     */       try {
/* 488 */         return ((TemplateScalarModel)this.it.next()).getAsString();
/* 489 */       } catch (TemplateModelException tme) {
/* 490 */         throw new UndeclaredThrowableException(tme);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jsp\FreeMarkerPageContext.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */