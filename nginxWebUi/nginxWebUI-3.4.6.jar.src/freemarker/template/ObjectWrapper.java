/*    */ package freemarker.template;
/*    */ 
/*    */ import freemarker.ext.beans.BeansWrapper;
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
/*    */ 
/*    */ public interface ObjectWrapper
/*    */ {
/*    */   @Deprecated
/* 56 */   public static final ObjectWrapper BEANS_WRAPPER = (ObjectWrapper)BeansWrapper.getDefaultInstance();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/* 66 */   public static final ObjectWrapper DEFAULT_WRAPPER = (ObjectWrapper)DefaultObjectWrapper.instance;
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
/*    */   @Deprecated
/* 79 */   public static final ObjectWrapper SIMPLE_WRAPPER = (ObjectWrapper)SimpleObjectWrapper.instance;
/*    */   
/*    */   TemplateModel wrap(Object paramObject) throws TemplateModelException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\ObjectWrapper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */