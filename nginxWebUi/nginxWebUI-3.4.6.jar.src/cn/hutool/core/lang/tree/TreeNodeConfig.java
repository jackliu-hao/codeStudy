/*     */ package cn.hutool.core.lang.tree;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TreeNodeConfig
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  16 */   public static TreeNodeConfig DEFAULT_CONFIG = new TreeNodeConfig();
/*     */ 
/*     */   
/*  19 */   private String idKey = "id";
/*  20 */   private String parentIdKey = "parentId";
/*  21 */   private String weightKey = "weight";
/*  22 */   private String nameKey = "name";
/*  23 */   private String childrenKey = "children";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Integer deep;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getIdKey() {
/*  34 */     return this.idKey;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TreeNodeConfig setIdKey(String idKey) {
/*  44 */     this.idKey = idKey;
/*  45 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getWeightKey() {
/*  54 */     return this.weightKey;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TreeNodeConfig setWeightKey(String weightKey) {
/*  64 */     this.weightKey = weightKey;
/*  65 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getNameKey() {
/*  74 */     return this.nameKey;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TreeNodeConfig setNameKey(String nameKey) {
/*  84 */     this.nameKey = nameKey;
/*  85 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getChildrenKey() {
/*  94 */     return this.childrenKey;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TreeNodeConfig setChildrenKey(String childrenKey) {
/* 104 */     this.childrenKey = childrenKey;
/* 105 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getParentIdKey() {
/* 114 */     return this.parentIdKey;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TreeNodeConfig setParentIdKey(String parentIdKey) {
/* 125 */     this.parentIdKey = parentIdKey;
/* 126 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getDeep() {
/* 135 */     return this.deep;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TreeNodeConfig setDeep(Integer deep) {
/* 145 */     this.deep = deep;
/* 146 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\tree\TreeNodeConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */