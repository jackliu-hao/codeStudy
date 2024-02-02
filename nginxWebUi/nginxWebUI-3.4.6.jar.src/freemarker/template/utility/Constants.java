/*     */ package freemarker.template.utility;
/*     */ 
/*     */ import freemarker.template.SimpleNumber;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateHashModelEx2;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateModelIterator;
/*     */ import freemarker.template.TemplateNumberModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import java.io.Serializable;
/*     */ import java.util.NoSuchElementException;
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
/*     */ public class Constants
/*     */ {
/*  49 */   public static final TemplateBooleanModel TRUE = TemplateBooleanModel.TRUE;
/*     */   
/*  51 */   public static final TemplateBooleanModel FALSE = TemplateBooleanModel.FALSE;
/*     */   
/*  53 */   public static final TemplateScalarModel EMPTY_STRING = (TemplateScalarModel)TemplateScalarModel.EMPTY_STRING;
/*     */   
/*  55 */   public static final TemplateNumberModel ZERO = (TemplateNumberModel)new SimpleNumber(0);
/*     */   
/*  57 */   public static final TemplateNumberModel ONE = (TemplateNumberModel)new SimpleNumber(1);
/*     */   
/*  59 */   public static final TemplateNumberModel MINUS_ONE = (TemplateNumberModel)new SimpleNumber(-1);
/*     */   
/*  61 */   public static final TemplateModelIterator EMPTY_ITERATOR = new EmptyIteratorModel();
/*     */   
/*     */   private static class EmptyIteratorModel implements TemplateModelIterator, Serializable {
/*     */     private EmptyIteratorModel() {}
/*     */     
/*     */     public TemplateModel next() throws TemplateModelException {
/*  67 */       throw new TemplateModelException("The collection has no more elements.");
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasNext() throws TemplateModelException {
/*  72 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*  77 */   public static final TemplateCollectionModel EMPTY_COLLECTION = new EmptyCollectionModel();
/*     */   
/*     */   private static class EmptyCollectionModel implements TemplateCollectionModel, Serializable {
/*     */     private EmptyCollectionModel() {}
/*     */     
/*     */     public TemplateModelIterator iterator() throws TemplateModelException {
/*  83 */       return Constants.EMPTY_ITERATOR;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*  88 */   public static final TemplateSequenceModel EMPTY_SEQUENCE = new EmptySequenceModel();
/*     */   
/*     */   private static class EmptySequenceModel implements TemplateSequenceModel, Serializable {
/*     */     private EmptySequenceModel() {}
/*     */     
/*     */     public TemplateModel get(int index) throws TemplateModelException {
/*  94 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() throws TemplateModelException {
/*  99 */       return 0;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 107 */   public static final TemplateHashModelEx2 EMPTY_HASH_EX2 = new EmptyHashModel();
/*     */   
/* 109 */   public static final TemplateHashModelEx EMPTY_HASH = (TemplateHashModelEx)EMPTY_HASH_EX2;
/*     */ 
/*     */   
/*     */   private static class EmptyHashModel
/*     */     implements TemplateHashModelEx2, Serializable
/*     */   {
/*     */     private EmptyHashModel() {}
/*     */ 
/*     */     
/*     */     public int size() throws TemplateModelException {
/* 119 */       return 0;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateCollectionModel keys() throws TemplateModelException {
/* 124 */       return Constants.EMPTY_COLLECTION;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateCollectionModel values() throws TemplateModelException {
/* 129 */       return Constants.EMPTY_COLLECTION;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(String key) throws TemplateModelException {
/* 134 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty() throws TemplateModelException {
/* 139 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() throws TemplateModelException {
/* 144 */       return Constants.EMPTY_KEY_VALUE_PAIR_ITERATOR;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 152 */   public static final TemplateHashModelEx2.KeyValuePairIterator EMPTY_KEY_VALUE_PAIR_ITERATOR = new EmptyKeyValuePairIterator();
/*     */ 
/*     */   
/*     */   private static class EmptyKeyValuePairIterator
/*     */     implements TemplateHashModelEx2.KeyValuePairIterator
/*     */   {
/*     */     private EmptyKeyValuePairIterator() {}
/*     */     
/*     */     public boolean hasNext() throws TemplateModelException {
/* 161 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateHashModelEx2.KeyValuePair next() throws TemplateModelException {
/* 166 */       throw new NoSuchElementException("Can't retrieve element from empty key-value pair iterator.");
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\Constants.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */