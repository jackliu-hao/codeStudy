/*    */ package cn.hutool.core.lang.tree.parser;
/*    */ 
/*    */ import cn.hutool.core.lang.tree.Tree;
/*    */ import cn.hutool.core.lang.tree.TreeNode;
/*    */ import cn.hutool.core.map.MapUtil;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DefaultNodeParser<T>
/*    */   implements NodeParser<TreeNode<T>, T>
/*    */ {
/*    */   public void parse(TreeNode<T> treeNode, Tree<T> tree) {
/* 19 */     tree.setId(treeNode.getId());
/* 20 */     tree.setParentId(treeNode.getParentId());
/* 21 */     tree.setWeight(treeNode.getWeight());
/* 22 */     tree.setName(treeNode.getName());
/*    */ 
/*    */     
/* 25 */     Map<String, Object> extra = treeNode.getExtra();
/* 26 */     if (MapUtil.isNotEmpty(extra))
/* 27 */       extra.forEach(tree::putExtra); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\tree\parser\DefaultNodeParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */