/*     */ package cn.hutool.core.lang.tree;
/*     */ 
/*     */ import cn.hutool.core.collection.IterUtil;
/*     */ import cn.hutool.core.lang.tree.parser.DefaultNodeParser;
/*     */ import cn.hutool.core.lang.tree.parser.NodeParser;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class TreeUtil
/*     */ {
/*     */   public static Tree<Integer> buildSingle(List<TreeNode<Integer>> list) {
/*  27 */     return buildSingle(list, Integer.valueOf(0));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<Tree<Integer>> build(List<TreeNode<Integer>> list) {
/*  37 */     return build(list, Integer.valueOf(0));
/*     */   }
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
/*     */   public static <E> Tree<E> buildSingle(List<TreeNode<E>> list, E parentId) {
/*  51 */     return buildSingle(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, (NodeParser<TreeNode<E>, E>)new DefaultNodeParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E> List<Tree<E>> build(List<TreeNode<E>> list, E parentId) {
/*  63 */     return build(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, (NodeParser<TreeNode<E>, E>)new DefaultNodeParser());
/*     */   }
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
/*     */   public static <T, E> Tree<E> buildSingle(List<T> list, E parentId, NodeParser<T, E> nodeParser) {
/*  79 */     return buildSingle(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, nodeParser);
/*     */   }
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
/*     */   public static <T, E> List<Tree<E>> build(List<T> list, E parentId, NodeParser<T, E> nodeParser) {
/*  93 */     return build(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, nodeParser);
/*     */   }
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
/*     */   public static <T, E> List<Tree<E>> build(List<T> list, E rootId, TreeNodeConfig treeNodeConfig, NodeParser<T, E> nodeParser) {
/* 108 */     return buildSingle(list, rootId, treeNodeConfig, nodeParser).getChildren();
/*     */   }
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
/*     */   public static <T, E> Tree<E> buildSingle(List<T> list, E rootId, TreeNodeConfig treeNodeConfig, NodeParser<T, E> nodeParser) {
/* 125 */     return TreeBuilder.<E>of(rootId, treeNodeConfig)
/* 126 */       .<T>append(list, nodeParser).build();
/*     */   }
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
/*     */   public static <E> List<Tree<E>> build(Map<E, Tree<E>> map, E rootId) {
/* 139 */     return buildSingle(map, rootId).getChildren();
/*     */   }
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
/*     */   public static <E> Tree<E> buildSingle(Map<E, Tree<E>> map, E rootId) {
/* 153 */     Tree<E> tree = (Tree<E>)IterUtil.getFirstNoneNull(map.values());
/* 154 */     if (null != tree) {
/* 155 */       TreeNodeConfig config = tree.getConfig();
/* 156 */       return TreeBuilder.<E>of(rootId, config)
/* 157 */         .append(map)
/* 158 */         .build();
/*     */     } 
/*     */     
/* 161 */     return createEmptyNode(rootId);
/*     */   }
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
/*     */   public static <T> Tree<T> getNode(Tree<T> node, T id) {
/* 175 */     if (ObjectUtil.equal(id, node.getId())) {
/* 176 */       return node;
/*     */     }
/*     */     
/* 179 */     List<Tree<T>> children = node.getChildren();
/* 180 */     if (null == children) {
/* 181 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 186 */     for (Tree<T> child : children) {
/* 187 */       Tree<T> childNode = child.getNode(id);
/* 188 */       if (null != childNode) {
/* 189 */         return childNode;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 194 */     return null;
/*     */   }
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
/*     */   public static <T> List<CharSequence> getParentsName(Tree<T> node, boolean includeCurrentNode) {
/* 211 */     List<CharSequence> result = new ArrayList<>();
/* 212 */     if (null == node) {
/* 213 */       return result;
/*     */     }
/*     */     
/* 216 */     if (includeCurrentNode) {
/* 217 */       result.add(node.getName());
/*     */     }
/*     */     
/* 220 */     Tree<T> parent = node.getParent();
/* 221 */     while (null != parent) {
/* 222 */       result.add(parent.getName());
/* 223 */       parent = parent.getParent();
/*     */     } 
/* 225 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E> Tree<E> createEmptyNode(E id) {
/* 237 */     return (new Tree<>()).setId(id);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\tree\TreeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */