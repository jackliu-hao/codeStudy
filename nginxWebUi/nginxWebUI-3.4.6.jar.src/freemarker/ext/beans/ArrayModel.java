/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.ext.util.ModelFactory;
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateModelIterator;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import java.lang.reflect.Array;
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
/*     */ public class ArrayModel
/*     */   extends BeanModel
/*     */   implements TemplateCollectionModel, TemplateSequenceModel
/*     */ {
/*  43 */   static final ModelFactory FACTORY = new ModelFactory()
/*     */     {
/*     */       
/*     */       public TemplateModel create(Object object, ObjectWrapper wrapper)
/*     */       {
/*  48 */         return (TemplateModel)new ArrayModel(object, (BeansWrapper)wrapper);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int length;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayModel(Object array, BeansWrapper wrapper) {
/*  65 */     super(array, wrapper);
/*  66 */     Class<?> clazz = array.getClass();
/*  67 */     if (!clazz.isArray())
/*  68 */       throw new IllegalArgumentException("Object is not an array, it's " + array.getClass().getName()); 
/*  69 */     this.length = Array.getLength(array);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModelIterator iterator() {
/*  75 */     return new Iterator();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModel get(int index) throws TemplateModelException {
/*     */     try {
/*  82 */       return wrap(Array.get(this.object, index));
/*  83 */     } catch (IndexOutOfBoundsException e) {
/*  84 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private class Iterator
/*     */     implements TemplateSequenceModel, TemplateModelIterator
/*     */   {
/*  93 */     private int position = 0;
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/*  97 */       return (this.position < ArrayModel.this.length);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public TemplateModel get(int index) throws TemplateModelException {
/* 103 */       return ArrayModel.this.get(index);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public TemplateModel next() throws TemplateModelException {
/* 109 */       return (this.position < ArrayModel.this.length) ? get(this.position++) : null;
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 114 */       return ArrayModel.this.size();
/*     */     }
/*     */     
/*     */     private Iterator() {} }
/*     */   
/*     */   public int size() {
/* 120 */     return this.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 125 */     return (this.length == 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\ArrayModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */