/*     */ package freemarker.template;
/*     */ 
/*     */ import freemarker.ext.util.WrapperTemplateModel;
/*     */ import freemarker.template.utility.ObjectWrapperWithAPISupport;
/*     */ import freemarker.template.utility.RichObjectWrapper;
/*     */ import java.io.Serializable;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ public class DefaultListAdapter
/*     */   extends WrappingTemplateModel
/*     */   implements TemplateSequenceModel, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport, Serializable
/*     */ {
/*     */   protected final List list;
/*     */   
/*     */   public static DefaultListAdapter adapt(List list, RichObjectWrapper wrapper) {
/*  65 */     return (list instanceof java.util.AbstractSequentialList) ? new DefaultListAdapterWithCollectionSupport(list, wrapper) : new DefaultListAdapter(list, wrapper);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private DefaultListAdapter(List list, RichObjectWrapper wrapper) {
/*  71 */     super((ObjectWrapper)wrapper);
/*  72 */     this.list = list;
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModel get(int index) throws TemplateModelException {
/*  77 */     return (index >= 0 && index < this.list.size()) ? wrap(this.list.get(index)) : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() throws TemplateModelException {
/*  82 */     return this.list.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getAdaptedObject(Class hint) {
/*  87 */     return getWrappedObject();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getWrappedObject() {
/*  92 */     return this.list;
/*     */   }
/*     */   
/*     */   private static class DefaultListAdapterWithCollectionSupport
/*     */     extends DefaultListAdapter
/*     */     implements TemplateCollectionModel {
/*     */     private DefaultListAdapterWithCollectionSupport(List list, RichObjectWrapper wrapper) {
/*  99 */       super(list, wrapper);
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModelIterator iterator() throws TemplateModelException {
/* 104 */       return new IteratorToTemplateModelIteratorAdapter(this.list.iterator(), getObjectWrapper());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModel getAPI() throws TemplateModelException {
/* 111 */     return ((ObjectWrapperWithAPISupport)getObjectWrapper()).wrapAsAPI(this.list);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\DefaultListAdapter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */