/*    */ package freemarker.ext.rhino;
/*    */ 
/*    */ import freemarker.ext.beans.BeansWrapper;
/*    */ import freemarker.ext.util.ModelFactory;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.utility.UndeclaredThrowableException;
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedExceptionAction;
/*    */ import org.mozilla.javascript.Scriptable;
/*    */ import org.mozilla.javascript.Undefined;
/*    */ import org.mozilla.javascript.UniqueTag;
/*    */ import org.mozilla.javascript.Wrapper;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RhinoWrapper
/*    */   extends BeansWrapper
/*    */ {
/*    */   private static final Object UNDEFINED_INSTANCE;
/*    */   
/*    */   static {
/*    */     try {
/* 45 */       UNDEFINED_INSTANCE = AccessController.doPrivileged(new PrivilegedExceptionAction()
/*    */           {
/*    */             public Object run() throws Exception {
/* 48 */               return Undefined.class.getField("instance").get(null);
/*    */             }
/*    */           });
/* 51 */     } catch (RuntimeException e) {
/* 52 */       throw e;
/* 53 */     } catch (Exception e) {
/* 54 */       throw new UndeclaredThrowableException(e);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public TemplateModel wrap(Object obj) throws TemplateModelException {
/* 61 */     if (obj == UNDEFINED_INSTANCE || obj == UniqueTag.NOT_FOUND) {
/* 62 */       return null;
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 71 */     if (obj == UniqueTag.NULL_VALUE) {
/* 72 */       return super.wrap(null);
/*    */     }
/*    */     
/* 75 */     if (obj instanceof Wrapper) {
/* 76 */       obj = ((Wrapper)obj).unwrap();
/*    */     }
/* 78 */     return super.wrap(obj);
/*    */   }
/*    */ 
/*    */   
/*    */   protected ModelFactory getModelFactory(Class<?> clazz) {
/* 83 */     if (Scriptable.class.isAssignableFrom(clazz)) {
/* 84 */       return RhinoScriptableModel.FACTORY;
/*    */     }
/* 86 */     return super.getModelFactory(clazz);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\rhino\RhinoWrapper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */