/*     */ package cn.hutool.core.lang.tree;
/*     */ 
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
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
/*     */ public class TreeNode<T>
/*     */   implements Node<T>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private T id;
/*     */   private T parentId;
/*     */   private CharSequence name;
/*  36 */   private Comparable<?> weight = Integer.valueOf(0);
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
/*     */   private Map<String, Object> extra;
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
/*     */   public TreeNode(T id, T parentId, String name, Comparable<?> weight) {
/*  59 */     this.id = id;
/*  60 */     this.parentId = parentId;
/*  61 */     this.name = name;
/*  62 */     if (weight != null) {
/*  63 */       this.weight = weight;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public T getId() {
/*  70 */     return this.id;
/*     */   }
/*     */ 
/*     */   
/*     */   public TreeNode<T> setId(T id) {
/*  75 */     this.id = id;
/*  76 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public T getParentId() {
/*  81 */     return this.parentId;
/*     */   }
/*     */ 
/*     */   
/*     */   public TreeNode<T> setParentId(T parentId) {
/*  86 */     this.parentId = parentId;
/*  87 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public CharSequence getName() {
/*  92 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public TreeNode<T> setName(CharSequence name) {
/*  97 */     this.name = name;
/*  98 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Comparable<?> getWeight() {
/* 103 */     return this.weight;
/*     */   }
/*     */ 
/*     */   
/*     */   public TreeNode<T> setWeight(Comparable<?> weight) {
/* 108 */     this.weight = weight;
/* 109 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, Object> getExtra() {
/* 119 */     return this.extra;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TreeNode<T> setExtra(Map<String, Object> extra) {
/* 130 */     this.extra = extra;
/* 131 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 136 */     if (this == o) {
/* 137 */       return true;
/*     */     }
/* 139 */     if (o == null || getClass() != o.getClass()) {
/* 140 */       return false;
/*     */     }
/* 142 */     TreeNode<?> treeNode = (TreeNode)o;
/* 143 */     return Objects.equals(this.id, treeNode.id);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 148 */     return Objects.hash(new Object[] { this.id });
/*     */   }
/*     */   
/*     */   public TreeNode() {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\tree\TreeNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */