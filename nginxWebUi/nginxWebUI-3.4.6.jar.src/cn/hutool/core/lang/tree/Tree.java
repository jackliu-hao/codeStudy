/*     */ package cn.hutool.core.lang.tree;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.lang.Filter;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.function.Consumer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Tree<T>
/*     */   extends LinkedHashMap<String, Object>
/*     */   implements Node<T>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final TreeNodeConfig treeNodeConfig;
/*     */   private Tree<T> parent;
/*     */   
/*     */   public Tree() {
/*  32 */     this((TreeNodeConfig)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Tree(TreeNodeConfig treeNodeConfig) {
/*  41 */     this.treeNodeConfig = (TreeNodeConfig)ObjectUtil.defaultIfNull(treeNodeConfig, TreeNodeConfig.DEFAULT_CONFIG);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TreeNodeConfig getConfig() {
/*  52 */     return this.treeNodeConfig;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Tree<T> getParent() {
/*  62 */     return this.parent;
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
/*     */   public Tree<T> getNode(T id) {
/*  74 */     return TreeUtil.getNode(this, id);
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
/*     */   public List<CharSequence> getParentsName(T id, boolean includeCurrentNode) {
/*  90 */     return TreeUtil.getParentsName(getNode(id), includeCurrentNode);
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
/*     */   public List<CharSequence> getParentsName(boolean includeCurrentNode) {
/* 105 */     return TreeUtil.getParentsName(this, includeCurrentNode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Tree<T> setParent(Tree<T> parent) {
/* 116 */     this.parent = parent;
/* 117 */     if (null != parent) {
/* 118 */       setParentId(parent.getId());
/*     */     }
/* 120 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public T getId() {
/* 126 */     return (T)get(this.treeNodeConfig.getIdKey());
/*     */   }
/*     */ 
/*     */   
/*     */   public Tree<T> setId(T id) {
/* 131 */     put(this.treeNodeConfig.getIdKey(), id);
/* 132 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public T getParentId() {
/* 138 */     return (T)get(this.treeNodeConfig.getParentIdKey());
/*     */   }
/*     */ 
/*     */   
/*     */   public Tree<T> setParentId(T parentId) {
/* 143 */     put(this.treeNodeConfig.getParentIdKey(), parentId);
/* 144 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public CharSequence getName() {
/* 149 */     return (CharSequence)get(this.treeNodeConfig.getNameKey());
/*     */   }
/*     */ 
/*     */   
/*     */   public Tree<T> setName(CharSequence name) {
/* 154 */     put(this.treeNodeConfig.getNameKey(), name);
/* 155 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Comparable<?> getWeight() {
/* 160 */     return (Comparable)get(this.treeNodeConfig.getWeightKey());
/*     */   }
/*     */ 
/*     */   
/*     */   public Tree<T> setWeight(Comparable<?> weight) {
/* 165 */     put(this.treeNodeConfig.getWeightKey(), weight);
/* 166 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Tree<T>> getChildren() {
/* 176 */     return (List<Tree<T>>)get(this.treeNodeConfig.getChildrenKey());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasChild() {
/* 186 */     return CollUtil.isNotEmpty(getChildren());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void walk(Consumer<Tree<T>> consumer) {
/* 196 */     consumer.accept(this);
/* 197 */     List<Tree<T>> children = getChildren();
/* 198 */     if (CollUtil.isNotEmpty(children)) {
/* 199 */       children.forEach(tree -> tree.walk(consumer));
/*     */     }
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
/*     */   public Tree<T> filterNew(Filter<Tree<T>> filter) {
/* 213 */     return cloneTree().filter(filter);
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
/*     */   public Tree<T> filter(Filter<Tree<T>> filter) {
/* 226 */     if (filter.accept(this))
/*     */     {
/* 228 */       return this;
/*     */     }
/*     */     
/* 231 */     List<Tree<T>> children = getChildren();
/* 232 */     if (CollUtil.isNotEmpty(children)) {
/*     */       
/* 234 */       List<Tree<T>> filteredChildren = new ArrayList<>(children.size());
/*     */       
/* 236 */       for (Tree<T> child : children) {
/* 237 */         Tree<T> filteredChild = child.filter(filter);
/* 238 */         if (null != filteredChild) {
/* 239 */           filteredChildren.add(filteredChild);
/*     */         }
/*     */       } 
/* 242 */       if (CollUtil.isNotEmpty(filteredChildren))
/*     */       {
/* 244 */         return setChildren(filteredChildren);
/*     */       }
/* 246 */       setChildren((List<Tree<T>>)null);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 251 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Tree<T> setChildren(List<Tree<T>> children) {
/* 261 */     if (null == children) {
/* 262 */       remove(this.treeNodeConfig.getChildrenKey());
/*     */     }
/* 264 */     put(this.treeNodeConfig.getChildrenKey(), children);
/* 265 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SafeVarargs
/*     */   public final Tree<T> addChildren(Tree<T>... children) {
/* 277 */     if (ArrayUtil.isNotEmpty((Object[])children)) {
/* 278 */       List<Tree<T>> childrenList = getChildren();
/* 279 */       if (null == childrenList) {
/* 280 */         childrenList = new ArrayList<>();
/* 281 */         setChildren(childrenList);
/*     */       } 
/* 283 */       for (Tree<T> child : children) {
/* 284 */         child.setParent(this);
/* 285 */         childrenList.add(child);
/*     */       } 
/*     */     } 
/* 288 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void putExtra(String key, Object value) {
/* 298 */     Assert.notEmpty(key, "Key must be not empty !", new Object[0]);
/* 299 */     put(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 304 */     StringWriter stringWriter = new StringWriter();
/* 305 */     printTree(this, new PrintWriter(stringWriter), 0);
/* 306 */     return stringWriter.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Tree<T> cloneTree() {
/* 317 */     Tree<T> result = (Tree<T>)ObjectUtil.clone(this);
/* 318 */     result.setChildren(cloneChildren());
/* 319 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private List<Tree<T>> cloneChildren() {
/* 328 */     List<Tree<T>> children = getChildren();
/* 329 */     if (null == children) {
/* 330 */       return null;
/*     */     }
/* 332 */     List<Tree<T>> newChildren = new ArrayList<>(children.size());
/* 333 */     children.forEach(t -> newChildren.add(t.cloneTree()));
/* 334 */     return newChildren;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void printTree(Tree<?> tree, PrintWriter writer, int intent) {
/* 345 */     writer.println(StrUtil.format("{}{}[{}]", new Object[] { StrUtil.repeat(' ', intent), tree.getName(), tree.getId() }));
/* 346 */     writer.flush();
/*     */     
/* 348 */     List<? extends Tree<?>> children = tree.getChildren();
/* 349 */     if (CollUtil.isNotEmpty(children))
/* 350 */       for (Tree<?> child : children)
/* 351 */         printTree(child, writer, intent + 2);  
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\tree\Tree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */