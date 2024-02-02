package freemarker.ext.jsp;

import freemarker.log.Logger;
import freemarker.template.utility.ClassUtil;
import java.util.Iterator;
import java.util.LinkedList;
import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELContextEvent;
import javax.el.ELContextListener;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.el.ImplicitObjectELResolver;
import javax.servlet.jsp.el.ScopedAttributeELResolver;

class FreeMarkerJspApplicationContext implements JspApplicationContext {
   private static final Logger LOG = Logger.getLogger("freemarker.jsp");
   private static final ExpressionFactory expressionFactoryImpl = findExpressionFactoryImplementation();
   private final LinkedList listeners = new LinkedList();
   private final CompositeELResolver elResolver = new CompositeELResolver();
   private final CompositeELResolver additionalResolvers = new CompositeELResolver();

   FreeMarkerJspApplicationContext() {
      this.elResolver.add(new ImplicitObjectELResolver());
      this.elResolver.add(this.additionalResolvers);
      this.elResolver.add(new MapELResolver());
      this.elResolver.add(new ResourceBundleELResolver());
      this.elResolver.add(new ListELResolver());
      this.elResolver.add(new ArrayELResolver());
      this.elResolver.add(new BeanELResolver());
      this.elResolver.add(new ScopedAttributeELResolver());
   }

   public void addELContextListener(ELContextListener listener) {
      synchronized(this.listeners) {
         this.listeners.addLast(listener);
      }
   }

   private static ExpressionFactory findExpressionFactoryImplementation() {
      ExpressionFactory ef = tryExpressionFactoryImplementation("com.sun");
      if (ef == null) {
         ef = tryExpressionFactoryImplementation("org.apache");
         if (ef == null) {
            LOG.warn("Could not find any implementation for " + ExpressionFactory.class.getName());
         }
      }

      return ef;
   }

   private static ExpressionFactory tryExpressionFactoryImplementation(String packagePrefix) {
      String className = packagePrefix + ".el.ExpressionFactoryImpl";

      try {
         Class cl = ClassUtil.forName(className);
         if (ExpressionFactory.class.isAssignableFrom(cl)) {
            LOG.info("Using " + className + " as implementation of " + ExpressionFactory.class.getName());
            return (ExpressionFactory)cl.newInstance();
         }

         LOG.warn("Class " + className + " does not implement " + ExpressionFactory.class.getName());
      } catch (ClassNotFoundException var3) {
      } catch (Exception var4) {
         LOG.error("Failed to instantiate " + className, var4);
      }

      return null;
   }

   public void addELResolver(ELResolver resolver) {
      this.additionalResolvers.add(resolver);
   }

   public ExpressionFactory getExpressionFactory() {
      return expressionFactoryImpl;
   }

   ELContext createNewELContext(FreeMarkerPageContext pageCtx) {
      ELContext ctx = new FreeMarkerELContext(pageCtx);
      ELContextEvent event = new ELContextEvent(ctx);
      synchronized(this.listeners) {
         Iterator iter = this.listeners.iterator();

         while(iter.hasNext()) {
            ELContextListener l = (ELContextListener)iter.next();
            l.contextCreated(event);
         }

         return ctx;
      }
   }

   private class FreeMarkerELContext extends ELContext {
      private final FreeMarkerPageContext pageCtx;

      FreeMarkerELContext(FreeMarkerPageContext pageCtx) {
         this.pageCtx = pageCtx;
      }

      public ELResolver getELResolver() {
         return FreeMarkerJspApplicationContext.this.elResolver;
      }

      public FunctionMapper getFunctionMapper() {
         return null;
      }

      public VariableMapper getVariableMapper() {
         return new VariableMapper() {
            public ValueExpression resolveVariable(String name) {
               Object obj = FreeMarkerELContext.this.pageCtx.findAttribute(name);
               return obj == null ? null : FreeMarkerJspApplicationContext.expressionFactoryImpl.createValueExpression(obj, obj.getClass());
            }

            public ValueExpression setVariable(String name, ValueExpression value) {
               ValueExpression prev = this.resolveVariable(name);
               FreeMarkerELContext.this.pageCtx.setAttribute(name, value.getValue(FreeMarkerELContext.this));
               return prev;
            }
         };
      }
   }
}
