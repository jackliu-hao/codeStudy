/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.Template;
/*    */ import javax.swing.JTree;
/*    */ import javax.swing.tree.DefaultTreeModel;
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
/*    */ @Deprecated
/*    */ public class FreeMarkerTree
/*    */   extends JTree
/*    */ {
/*    */   public FreeMarkerTree(Template template) {
/* 36 */     super(template.getRootTreeNode());
/*    */   }
/*    */   
/*    */   public void setTemplate(Template template) {
/* 40 */     setModel(new DefaultTreeModel(template.getRootTreeNode()));
/* 41 */     invalidate();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
/* 48 */     if (value instanceof TemplateElement) {
/* 49 */       return ((TemplateElement)value).getDescription();
/*    */     }
/* 51 */     return value.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\FreeMarkerTree.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */