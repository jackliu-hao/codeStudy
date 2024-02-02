package freemarker.ext.jsp;

import freemarker.core.Environment;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.ObjectWrapperAndUnwrapper;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.UndeclaredThrowableException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;
import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;

abstract class FreeMarkerPageContext extends PageContext implements TemplateModel {
   private static final Class OBJECT_CLASS = Object.class;
   private final Environment environment = Environment.getCurrentEnvironment();
   private final int incompatibleImprovements;
   private List tags = new ArrayList();
   private List outs = new ArrayList();
   private final GenericServlet servlet;
   private HttpSession session;
   private final HttpServletRequest request;
   private final HttpServletResponse response;
   private final ObjectWrapper wrapper;
   private final ObjectWrapperAndUnwrapper unwrapper;
   private JspWriter jspOut;

   protected FreeMarkerPageContext() throws TemplateModelException {
      this.incompatibleImprovements = this.environment.getConfiguration().getIncompatibleImprovements().intValue();
      TemplateModel appModel = this.environment.getGlobalVariable("__FreeMarkerServlet.Application__");
      if (!(appModel instanceof ServletContextHashModel)) {
         appModel = this.environment.getGlobalVariable("Application");
      }

      if (appModel instanceof ServletContextHashModel) {
         this.servlet = ((ServletContextHashModel)appModel).getServlet();
         TemplateModel requestModel = this.environment.getGlobalVariable("__FreeMarkerServlet.Request__");
         if (!(requestModel instanceof HttpRequestHashModel)) {
            requestModel = this.environment.getGlobalVariable("Request");
         }

         if (requestModel instanceof HttpRequestHashModel) {
            HttpRequestHashModel reqHash = (HttpRequestHashModel)requestModel;
            this.request = reqHash.getRequest();
            this.session = this.request.getSession(false);
            this.response = reqHash.getResponse();
            this.wrapper = reqHash.getObjectWrapper();
            this.unwrapper = this.wrapper instanceof ObjectWrapperAndUnwrapper ? (ObjectWrapperAndUnwrapper)this.wrapper : null;
            this.setAttribute("javax.servlet.jsp.jspRequest", this.request);
            this.setAttribute("javax.servlet.jsp.jspResponse", this.response);
            if (this.session != null) {
               this.setAttribute("javax.servlet.jsp.jspSession", this.session);
            }

            this.setAttribute("javax.servlet.jsp.jspPage", this.servlet);
            this.setAttribute("javax.servlet.jsp.jspConfig", this.servlet.getServletConfig());
            this.setAttribute("javax.servlet.jsp.jspPageContext", this);
            this.setAttribute("javax.servlet.jsp.jspApplication", this.servlet.getServletContext());
         } else {
            throw new TemplateModelException("Could not find an instance of " + HttpRequestHashModel.class.getName() + " in the data model under either the name " + "__FreeMarkerServlet.Request__" + " or " + "Request");
         }
      } else {
         throw new TemplateModelException("Could not find an instance of " + ServletContextHashModel.class.getName() + " in the data model under either the name " + "__FreeMarkerServlet.Application__" + " or " + "Application");
      }
   }

   ObjectWrapper getObjectWrapper() {
      return this.wrapper;
   }

   public void initialize(Servlet servlet, ServletRequest request, ServletResponse response, String errorPageURL, boolean needsSession, int bufferSize, boolean autoFlush) {
      throw new UnsupportedOperationException();
   }

   public void release() {
   }

   public void setAttribute(String name, Object value) {
      this.setAttribute(name, value, 1);
   }

   public void setAttribute(String name, Object value, int scope) {
      switch (scope) {
         case 1:
            try {
               this.environment.setGlobalVariable(name, this.wrapper.wrap(value));
               break;
            } catch (TemplateModelException var5) {
               throw new UndeclaredThrowableException(var5);
            }
         case 2:
            this.getRequest().setAttribute(name, value);
            break;
         case 3:
            this.getSession(true).setAttribute(name, value);
            break;
         case 4:
            this.getServletContext().setAttribute(name, value);
            break;
         default:
            throw new IllegalArgumentException("Invalid scope " + scope);
      }

   }

   public Object getAttribute(String name) {
      return this.getAttribute(name, 1);
   }

   public Object getAttribute(String name, int scope) {
      switch (scope) {
         case 1:
            try {
               TemplateModel tm = this.environment.getGlobalNamespace().get(name);
               if (this.incompatibleImprovements >= _TemplateAPI.VERSION_INT_2_3_22 && this.unwrapper != null) {
                  return this.unwrapper.unwrap(tm);
               } else if (tm instanceof AdapterTemplateModel) {
                  return ((AdapterTemplateModel)tm).getAdaptedObject(OBJECT_CLASS);
               } else if (tm instanceof WrapperTemplateModel) {
                  return ((WrapperTemplateModel)tm).getWrappedObject();
               } else if (tm instanceof TemplateScalarModel) {
                  return ((TemplateScalarModel)tm).getAsString();
               } else if (tm instanceof TemplateNumberModel) {
                  return ((TemplateNumberModel)tm).getAsNumber();
               } else if (tm instanceof TemplateBooleanModel) {
                  return ((TemplateBooleanModel)tm).getAsBoolean();
               } else {
                  if (this.incompatibleImprovements >= _TemplateAPI.VERSION_INT_2_3_22 && tm instanceof TemplateDateModel) {
                     return ((TemplateDateModel)tm).getAsDate();
                  }

                  return tm;
               }
            } catch (TemplateModelException var4) {
               throw new UndeclaredThrowableException("Failed to unwrapp FTL global variable", var4);
            }
         case 2:
            return this.getRequest().getAttribute(name);
         case 3:
            HttpSession session = this.getSession(false);
            if (session == null) {
               return null;
            }

            return session.getAttribute(name);
         case 4:
            return this.getServletContext().getAttribute(name);
         default:
            throw new IllegalArgumentException("Invalid scope " + scope);
      }
   }

   public Object findAttribute(String name) {
      Object retval = this.getAttribute(name, 1);
      if (retval != null) {
         return retval;
      } else {
         retval = this.getAttribute(name, 2);
         if (retval != null) {
            return retval;
         } else {
            retval = this.getAttribute(name, 3);
            return retval != null ? retval : this.getAttribute(name, 4);
         }
      }
   }

   public void removeAttribute(String name) {
      this.removeAttribute(name, 1);
      this.removeAttribute(name, 2);
      this.removeAttribute(name, 3);
      this.removeAttribute(name, 4);
   }

   public void removeAttribute(String name, int scope) {
      switch (scope) {
         case 1:
            this.environment.getGlobalNamespace().remove(name);
            break;
         case 2:
            this.getRequest().removeAttribute(name);
            break;
         case 3:
            HttpSession session = this.getSession(false);
            if (session != null) {
               session.removeAttribute(name);
            }
            break;
         case 4:
            this.getServletContext().removeAttribute(name);
            break;
         default:
            throw new IllegalArgumentException("Invalid scope: " + scope);
      }

   }

   public int getAttributesScope(String name) {
      if (this.getAttribute(name, 1) != null) {
         return 1;
      } else if (this.getAttribute(name, 2) != null) {
         return 2;
      } else if (this.getAttribute(name, 3) != null) {
         return 3;
      } else {
         return this.getAttribute(name, 4) != null ? 4 : 0;
      }
   }

   public Enumeration getAttributeNamesInScope(int scope) {
      switch (scope) {
         case 1:
            try {
               return new TemplateHashModelExEnumeration(this.environment.getGlobalNamespace());
            } catch (TemplateModelException var3) {
               throw new UndeclaredThrowableException(var3);
            }
         case 2:
            return this.getRequest().getAttributeNames();
         case 3:
            HttpSession session = this.getSession(false);
            if (session != null) {
               return session.getAttributeNames();
            }

            return Collections.enumeration(Collections.EMPTY_SET);
         case 4:
            return this.getServletContext().getAttributeNames();
         default:
            throw new IllegalArgumentException("Invalid scope " + scope);
      }
   }

   public JspWriter getOut() {
      return this.jspOut;
   }

   private HttpSession getSession(boolean create) {
      if (this.session == null) {
         this.session = this.request.getSession(create);
         if (this.session != null) {
            this.setAttribute("javax.servlet.jsp.jspSession", this.session);
         }
      }

      return this.session;
   }

   public HttpSession getSession() {
      return this.getSession(false);
   }

   public Object getPage() {
      return this.servlet;
   }

   public ServletRequest getRequest() {
      return this.request;
   }

   public ServletResponse getResponse() {
      return this.response;
   }

   public Exception getException() {
      throw new UnsupportedOperationException();
   }

   public ServletConfig getServletConfig() {
      return this.servlet.getServletConfig();
   }

   public ServletContext getServletContext() {
      return this.servlet.getServletContext();
   }

   public void forward(String url) throws ServletException, IOException {
      this.request.getRequestDispatcher(url).forward(this.request, this.response);
   }

   public void include(String url) throws ServletException, IOException {
      this.jspOut.flush();
      this.request.getRequestDispatcher(url).include(this.request, this.response);
   }

   public void include(String url, boolean flush) throws ServletException, IOException {
      if (flush) {
         this.jspOut.flush();
      }

      final PrintWriter pw = new PrintWriter(this.jspOut);
      this.request.getRequestDispatcher(url).include(this.request, new HttpServletResponseWrapper(this.response) {
         public PrintWriter getWriter() {
            return pw;
         }

         public ServletOutputStream getOutputStream() {
            throw new UnsupportedOperationException("JSP-included resource must use getWriter()");
         }
      });
      pw.flush();
   }

   public void handlePageException(Exception e) {
      throw new UnsupportedOperationException();
   }

   public void handlePageException(Throwable e) {
      throw new UnsupportedOperationException();
   }

   public BodyContent pushBody() {
      return (BodyContent)this.pushWriter(new TagTransformModel.BodyContentImpl(this.getOut(), true));
   }

   public JspWriter pushBody(Writer w) {
      return this.pushWriter(new JspWriterAdapter(w));
   }

   public JspWriter popBody() {
      this.popWriter();
      return (JspWriter)this.getAttribute("javax.servlet.jsp.jspOut");
   }

   Object peekTopTag(Class tagClass) {
      ListIterator iter = this.tags.listIterator(this.tags.size());

      Object tag;
      do {
         if (!iter.hasPrevious()) {
            return null;
         }

         tag = iter.previous();
      } while(!tagClass.isInstance(tag));

      return tag;
   }

   void popTopTag() {
      this.tags.remove(this.tags.size() - 1);
   }

   void popWriter() {
      this.jspOut = (JspWriter)this.outs.remove(this.outs.size() - 1);
      this.setAttribute("javax.servlet.jsp.jspOut", this.jspOut);
   }

   void pushTopTag(Object tag) {
      this.tags.add(tag);
   }

   JspWriter pushWriter(JspWriter out) {
      this.outs.add(this.jspOut);
      this.jspOut = out;
      this.setAttribute("javax.servlet.jsp.jspOut", this.jspOut);
      return out;
   }

   private static class TemplateHashModelExEnumeration implements Enumeration {
      private final TemplateModelIterator it;

      private TemplateHashModelExEnumeration(TemplateHashModelEx hashEx) throws TemplateModelException {
         this.it = hashEx.keys().iterator();
      }

      public boolean hasMoreElements() {
         try {
            return this.it.hasNext();
         } catch (TemplateModelException var2) {
            throw new UndeclaredThrowableException(var2);
         }
      }

      public Object nextElement() {
         try {
            return ((TemplateScalarModel)this.it.next()).getAsString();
         } catch (TemplateModelException var2) {
            throw new UndeclaredThrowableException(var2);
         }
      }

      // $FF: synthetic method
      TemplateHashModelExEnumeration(TemplateHashModelEx x0, Object x1) throws TemplateModelException {
         this(x0);
      }
   }
}
