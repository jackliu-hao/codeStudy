/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateCollectionModel;
/*    */ import freemarker.template.TemplateCollectionModelEx;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateModelIterator;
/*    */ import freemarker.template.TemplateSequenceModel;
/*    */ import java.io.Serializable;
/*    */ import java.util.ArrayList;
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
/*    */ public final class CollectionAndSequence
/*    */   implements TemplateCollectionModel, TemplateSequenceModel, Serializable
/*    */ {
/*    */   private TemplateCollectionModel collection;
/*    */   private TemplateSequenceModel sequence;
/*    */   private ArrayList<TemplateModel> data;
/*    */   
/*    */   public CollectionAndSequence(TemplateCollectionModel collection) {
/* 43 */     this.collection = collection;
/*    */   }
/*    */   
/*    */   public CollectionAndSequence(TemplateSequenceModel sequence) {
/* 47 */     this.sequence = sequence;
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateModelIterator iterator() throws TemplateModelException {
/* 52 */     if (this.collection != null) {
/* 53 */       return this.collection.iterator();
/*    */     }
/* 55 */     return new SequenceIterator(this.sequence);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public TemplateModel get(int i) throws TemplateModelException {
/* 61 */     if (this.sequence != null) {
/* 62 */       return this.sequence.get(i);
/*    */     }
/* 64 */     initSequence();
/* 65 */     return this.data.get(i);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int size() throws TemplateModelException {
/* 71 */     if (this.sequence != null)
/* 72 */       return this.sequence.size(); 
/* 73 */     if (this.collection instanceof TemplateCollectionModelEx) {
/* 74 */       return ((TemplateCollectionModelEx)this.collection).size();
/*    */     }
/* 76 */     initSequence();
/* 77 */     return this.data.size();
/*    */   }
/*    */ 
/*    */   
/*    */   private void initSequence() throws TemplateModelException {
/* 82 */     if (this.data == null) {
/* 83 */       this.data = new ArrayList<>();
/* 84 */       TemplateModelIterator it = this.collection.iterator();
/* 85 */       while (it.hasNext())
/* 86 */         this.data.add(it.next()); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\CollectionAndSequence.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */