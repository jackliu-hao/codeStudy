/*     */ package freemarker.template;
/*     */ 
/*     */ import freemarker.ext.util.WrapperTemplateModel;
/*     */ import freemarker.template.utility.ObjectWrapperWithAPISupport;
/*     */ import java.io.Serializable;
/*     */ import java.util.Enumeration;
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
/*     */ public class DefaultEnumerationAdapter
/*     */   extends WrappingTemplateModel
/*     */   implements TemplateCollectionModel, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport, Serializable
/*     */ {
/*     */   private final Enumeration<?> enumeration;
/*     */   private boolean enumerationOwnedBySomeone;
/*     */   
/*     */   public static DefaultEnumerationAdapter adapt(Enumeration<?> enumeration, ObjectWrapper wrapper) {
/*  51 */     return new DefaultEnumerationAdapter(enumeration, wrapper);
/*     */   }
/*     */   
/*     */   private DefaultEnumerationAdapter(Enumeration<?> enumeration, ObjectWrapper wrapper) {
/*  55 */     super(wrapper);
/*  56 */     this.enumeration = enumeration;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getWrappedObject() {
/*  61 */     return this.enumeration;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getAdaptedObject(Class<?> hint) {
/*  66 */     return getWrappedObject();
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModelIterator iterator() throws TemplateModelException {
/*  71 */     return new SimpleTemplateModelIterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModel getAPI() throws TemplateModelException {
/*  76 */     return ((ObjectWrapperWithAPISupport)getObjectWrapper()).wrapAsAPI(this.enumeration);
/*     */   }
/*     */ 
/*     */   
/*     */   private class SimpleTemplateModelIterator
/*     */     implements TemplateModelIterator
/*     */   {
/*     */     private boolean enumerationOwnedByMe;
/*     */     
/*     */     private SimpleTemplateModelIterator() {}
/*     */     
/*     */     public TemplateModel next() throws TemplateModelException {
/*  88 */       if (!this.enumerationOwnedByMe) {
/*  89 */         checkNotOwner();
/*  90 */         DefaultEnumerationAdapter.this.enumerationOwnedBySomeone = true;
/*  91 */         this.enumerationOwnedByMe = true;
/*     */       } 
/*     */       
/*  94 */       if (!DefaultEnumerationAdapter.this.enumeration.hasMoreElements()) {
/*  95 */         throw new TemplateModelException("The collection has no more items.");
/*     */       }
/*     */       
/*  98 */       Object value = DefaultEnumerationAdapter.this.enumeration.nextElement();
/*  99 */       return (value instanceof TemplateModel) ? (TemplateModel)value : DefaultEnumerationAdapter.this.wrap(value);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean hasNext() throws TemplateModelException {
/* 105 */       if (!this.enumerationOwnedByMe) {
/* 106 */         checkNotOwner();
/*     */       }
/*     */       
/* 109 */       return DefaultEnumerationAdapter.this.enumeration.hasMoreElements();
/*     */     }
/*     */     
/*     */     private void checkNotOwner() throws TemplateModelException {
/* 113 */       if (DefaultEnumerationAdapter.this.enumerationOwnedBySomeone)
/* 114 */         throw new TemplateModelException("This collection value wraps a java.util.Enumeration, thus it can be listed only once."); 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\DefaultEnumerationAdapter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */