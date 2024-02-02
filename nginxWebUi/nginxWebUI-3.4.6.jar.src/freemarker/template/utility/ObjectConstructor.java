/*    */ package freemarker.template.utility;
/*    */ 
/*    */ import freemarker.ext.beans.BeansWrapper;
/*    */ import freemarker.template.TemplateMethodModelEx;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import java.util.List;
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
/*    */ public class ObjectConstructor
/*    */   implements TemplateMethodModelEx
/*    */ {
/*    */   public Object exec(List<E> args) throws TemplateModelException {
/* 44 */     if (args.isEmpty()) {
/* 45 */       throw new TemplateModelException("This method must have at least one argument, the name of the class to instantiate.");
/*    */     }
/* 47 */     String classname = args.get(0).toString();
/* 48 */     Class cl = null;
/*    */     try {
/* 50 */       cl = ClassUtil.forName(classname);
/* 51 */     } catch (Exception e) {
/* 52 */       throw new TemplateModelException(e.getMessage());
/*    */     } 
/* 54 */     BeansWrapper bw = BeansWrapper.getDefaultInstance();
/* 55 */     Object obj = bw.newInstance(cl, args.subList(1, args.size()));
/* 56 */     return bw.wrap(obj);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\ObjectConstructor.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */