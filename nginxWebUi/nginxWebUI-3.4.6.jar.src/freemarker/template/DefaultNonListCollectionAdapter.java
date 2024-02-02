/*     */ package freemarker.template;
/*     */ 
/*     */ import freemarker.core._DelayedShortClassName;
/*     */ import freemarker.core._TemplateModelException;
/*     */ import freemarker.ext.util.WrapperTemplateModel;
/*     */ import freemarker.template.utility.ObjectWrapperWithAPISupport;
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
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
/*     */ public class DefaultNonListCollectionAdapter
/*     */   extends WrappingTemplateModel
/*     */   implements TemplateCollectionModelEx, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport, Serializable
/*     */ {
/*     */   private final Collection collection;
/*     */   
/*     */   public static DefaultNonListCollectionAdapter adapt(Collection collection, ObjectWrapperWithAPISupport wrapper) {
/*  64 */     return new DefaultNonListCollectionAdapter(collection, wrapper);
/*     */   }
/*     */   
/*     */   private DefaultNonListCollectionAdapter(Collection collection, ObjectWrapperWithAPISupport wrapper) {
/*  68 */     super((ObjectWrapper)wrapper);
/*  69 */     this.collection = collection;
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModelIterator iterator() throws TemplateModelException {
/*  74 */     return new IteratorToTemplateModelIteratorAdapter(this.collection.iterator(), getObjectWrapper());
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  79 */     return this.collection.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  84 */     return this.collection.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getWrappedObject() {
/*  89 */     return this.collection;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getAdaptedObject(Class hint) {
/*  94 */     return getWrappedObject();
/*     */   }
/*     */   
/*     */   public boolean contains(TemplateModel item) throws TemplateModelException {
/*  98 */     Object itemPojo = ((ObjectWrapperAndUnwrapper)getObjectWrapper()).unwrap(item);
/*     */     try {
/* 100 */       return this.collection.contains(itemPojo);
/* 101 */     } catch (ClassCastException e) {
/* 102 */       throw new _TemplateModelException(e, new Object[] { "Failed to check if the collection contains the item. Probably the item's Java type, ", (itemPojo != null) ? new _DelayedShortClassName(itemPojo
/*     */               
/* 104 */               .getClass()) : "Null", ", doesn't match the type of (some of) the collection items; see cause exception." });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModel getAPI() throws TemplateModelException {
/* 111 */     return ((ObjectWrapperWithAPISupport)getObjectWrapper()).wrapAsAPI(this.collection);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\DefaultNonListCollectionAdapter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */