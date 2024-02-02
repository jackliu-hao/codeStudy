/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import freemarker.template.TemplateCollectionModel;
/*    */ import java.util.Set;
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
/*    */ class SetAdapter
/*    */   extends CollectionAdapter
/*    */   implements Set
/*    */ {
/*    */   SetAdapter(TemplateCollectionModel model, BeansWrapper wrapper) {
/* 30 */     super(model, wrapper);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\SetAdapter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */