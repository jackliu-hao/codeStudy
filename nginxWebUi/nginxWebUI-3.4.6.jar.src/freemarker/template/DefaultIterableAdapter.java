/*    */ package freemarker.template;
/*    */ 
/*    */ import freemarker.ext.util.WrapperTemplateModel;
/*    */ import freemarker.template.utility.ObjectWrapperWithAPISupport;
/*    */ import java.io.Serializable;
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
/*    */ public class DefaultIterableAdapter
/*    */   extends WrappingTemplateModel
/*    */   implements TemplateCollectionModel, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport, Serializable
/*    */ {
/*    */   private final Iterable<?> iterable;
/*    */   
/*    */   public static DefaultIterableAdapter adapt(Iterable<?> iterable, ObjectWrapperWithAPISupport wrapper) {
/* 62 */     return new DefaultIterableAdapter(iterable, wrapper);
/*    */   }
/*    */   
/*    */   private DefaultIterableAdapter(Iterable<?> iterable, ObjectWrapperWithAPISupport wrapper) {
/* 66 */     super((ObjectWrapper)wrapper);
/* 67 */     this.iterable = iterable;
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateModelIterator iterator() throws TemplateModelException {
/* 72 */     return new IteratorToTemplateModelIteratorAdapter(this.iterable.iterator(), getObjectWrapper());
/*    */   }
/*    */ 
/*    */   
/*    */   public Object getWrappedObject() {
/* 77 */     return this.iterable;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object getAdaptedObject(Class hint) {
/* 82 */     return getWrappedObject();
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateModel getAPI() throws TemplateModelException {
/* 87 */     return ((ObjectWrapperWithAPISupport)getObjectWrapper()).wrapAsAPI(this.iterable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\DefaultIterableAdapter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */