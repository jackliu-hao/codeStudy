/*     */ package cn.hutool.core.lang.tree;
/*     */ 
/*     */ import cn.hutool.core.builder.Builder;
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.lang.tree.parser.NodeParser;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
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
/*     */ public class TreeBuilder<E>
/*     */   implements Builder<Tree<E>>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final Tree<E> root;
/*     */   private final Map<E, Tree<E>> idTreeMap;
/*     */   private boolean isBuild;
/*     */   
/*     */   public static <T> TreeBuilder<T> of(T rootId) {
/*  35 */     return of(rootId, null);
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
/*     */   public static <T> TreeBuilder<T> of(T rootId, TreeNodeConfig config) {
/*  47 */     return new TreeBuilder<>(rootId, config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TreeBuilder(E rootId, TreeNodeConfig config) {
/*  57 */     this.root = new Tree<>(config);
/*  58 */     this.root.setId(rootId);
/*  59 */     this.idTreeMap = new HashMap<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TreeBuilder<E> setId(E id) {
/*  70 */     this.root.setId(id);
/*  71 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TreeBuilder<E> setParentId(E parentId) {
/*  82 */     this.root.setParentId(parentId);
/*  83 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TreeBuilder<E> setName(CharSequence name) {
/*  94 */     this.root.setName(name);
/*  95 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TreeBuilder<E> setWeight(Comparable<?> weight) {
/* 106 */     this.root.setWeight(weight);
/* 107 */     return this;
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
/*     */   public TreeBuilder<E> putExtra(String key, Object value) {
/* 119 */     Assert.notEmpty(key, "Key must be not empty !", new Object[0]);
/* 120 */     this.root.put(key, value);
/* 121 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TreeBuilder<E> append(Map<E, Tree<E>> map) {
/* 131 */     checkBuilt();
/*     */     
/* 133 */     this.idTreeMap.putAll(map);
/* 134 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TreeBuilder<E> append(Iterable<Tree<E>> trees) {
/* 144 */     checkBuilt();
/*     */     
/* 146 */     for (Tree<E> tree : trees) {
/* 147 */       this.idTreeMap.put(tree.getId(), tree);
/*     */     }
/* 149 */     return this;
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
/*     */   public <T> TreeBuilder<E> append(List<T> list, NodeParser<T, E> nodeParser) {
/* 161 */     checkBuilt();
/*     */     
/* 163 */     TreeNodeConfig config = this.root.getConfig();
/* 164 */     Map<E, Tree<E>> map = new LinkedHashMap<>(list.size(), 1.0F);
/*     */     
/* 166 */     for (T t : list) {
/* 167 */       Tree<E> node = new Tree<>(config);
/* 168 */       nodeParser.parse(t, node);
/* 169 */       map.put(node.getId(), node);
/*     */     } 
/* 171 */     return append(map);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TreeBuilder<E> reset() {
/* 181 */     this.idTreeMap.clear();
/* 182 */     this.root.setChildren((List<Tree<E>>)null);
/* 183 */     this.isBuild = false;
/* 184 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Tree<E> build() {
/* 189 */     checkBuilt();
/*     */     
/* 191 */     buildFromMap();
/* 192 */     cutTree();
/*     */     
/* 194 */     this.isBuild = true;
/* 195 */     this.idTreeMap.clear();
/*     */     
/* 197 */     return this.root;
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
/*     */   
/*     */   public List<Tree<E>> buildList() {
/* 215 */     if (this.isBuild)
/*     */     {
/* 217 */       return this.root.getChildren();
/*     */     }
/* 219 */     return build().getChildren();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void buildFromMap() {
/* 226 */     if (MapUtil.isEmpty(this.idTreeMap)) {
/*     */       return;
/*     */     }
/*     */     
/* 230 */     Map<E, Tree<E>> eTreeMap = MapUtil.sortByValue(this.idTreeMap, false);
/*     */     
/* 232 */     for (Tree<E> node : eTreeMap.values()) {
/* 233 */       if (null == node) {
/*     */         continue;
/*     */       }
/* 236 */       E parentId = node.getParentId();
/* 237 */       if (ObjectUtil.equals(this.root.getId(), parentId)) {
/* 238 */         this.root.addChildren((Tree<E>[])new Tree[] { node });
/*     */         
/*     */         continue;
/*     */       } 
/* 242 */       Tree<E> parentNode = eTreeMap.get(parentId);
/* 243 */       if (null != parentNode) {
/* 244 */         parentNode.addChildren((Tree<E>[])new Tree[] { node });
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void cutTree() {
/* 253 */     TreeNodeConfig config = this.root.getConfig();
/* 254 */     Integer deep = config.getDeep();
/* 255 */     if (null == deep || deep.intValue() < 0) {
/*     */       return;
/*     */     }
/* 258 */     cutTree(this.root, 0, deep.intValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void cutTree(Tree<E> tree, int currentDepp, int maxDeep) {
/* 269 */     if (null == tree) {
/*     */       return;
/*     */     }
/* 272 */     if (currentDepp == maxDeep) {
/*     */       
/* 274 */       tree.setChildren((List<Tree<E>>)null);
/*     */       
/*     */       return;
/*     */     } 
/* 278 */     List<Tree<E>> children = tree.getChildren();
/* 279 */     if (CollUtil.isNotEmpty(children)) {
/* 280 */       for (Tree<E> child : children) {
/* 281 */         cutTree(child, currentDepp + 1, maxDeep);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkBuilt() {
/* 290 */     Assert.isFalse(this.isBuild, "Current tree has been built.", new Object[0]);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\tree\TreeBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */