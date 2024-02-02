/*    */ package freemarker.ext.dom;
/*    */ 
/*    */ import freemarker.template.TemplateScalarModel;
/*    */ import org.w3c.dom.CharacterData;
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
/*    */ class CharacterDataNodeModel
/*    */   extends NodeModel
/*    */   implements TemplateScalarModel
/*    */ {
/*    */   public CharacterDataNodeModel(CharacterData text) {
/* 30 */     super(text);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getAsString() {
/* 35 */     return ((CharacterData)this.node).getData();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getNodeName() {
/* 40 */     return (this.node instanceof org.w3c.dom.Comment) ? "@comment" : "@text";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 45 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\dom\CharacterDataNodeModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */