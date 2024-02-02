/*    */ package freemarker.template;
/*    */ 
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
/*    */ public class TemplateModelListSequence
/*    */   implements TemplateSequenceModel
/*    */ {
/*    */   private List list;
/*    */   
/*    */   public TemplateModelListSequence(List list) {
/* 34 */     this.list = list;
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateModel get(int index) {
/* 39 */     return this.list.get(index);
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() {
/* 44 */     return this.list.size();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object getWrappedObject() {
/* 51 */     return this.list;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TemplateModelListSequence.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */