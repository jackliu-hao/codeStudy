package cn.hutool.core.lang.tree;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

public class Tree<T> extends LinkedHashMap<String, Object> implements Node<T> {
   private static final long serialVersionUID = 1L;
   private final TreeNodeConfig treeNodeConfig;
   private Tree<T> parent;

   public Tree() {
      this((TreeNodeConfig)null);
   }

   public Tree(TreeNodeConfig treeNodeConfig) {
      this.treeNodeConfig = (TreeNodeConfig)ObjectUtil.defaultIfNull(treeNodeConfig, (Object)TreeNodeConfig.DEFAULT_CONFIG);
   }

   public TreeNodeConfig getConfig() {
      return this.treeNodeConfig;
   }

   public Tree<T> getParent() {
      return this.parent;
   }

   public Tree<T> getNode(T id) {
      return TreeUtil.getNode(this, id);
   }

   public List<CharSequence> getParentsName(T id, boolean includeCurrentNode) {
      return TreeUtil.getParentsName(this.getNode(id), includeCurrentNode);
   }

   public List<CharSequence> getParentsName(boolean includeCurrentNode) {
      return TreeUtil.getParentsName(this, includeCurrentNode);
   }

   public Tree<T> setParent(Tree<T> parent) {
      this.parent = parent;
      if (null != parent) {
         this.setParentId(parent.getId());
      }

      return this;
   }

   public T getId() {
      return this.get(this.treeNodeConfig.getIdKey());
   }

   public Tree<T> setId(T id) {
      this.put(this.treeNodeConfig.getIdKey(), id);
      return this;
   }

   public T getParentId() {
      return this.get(this.treeNodeConfig.getParentIdKey());
   }

   public Tree<T> setParentId(T parentId) {
      this.put(this.treeNodeConfig.getParentIdKey(), parentId);
      return this;
   }

   public CharSequence getName() {
      return (CharSequence)this.get(this.treeNodeConfig.getNameKey());
   }

   public Tree<T> setName(CharSequence name) {
      this.put(this.treeNodeConfig.getNameKey(), name);
      return this;
   }

   public Comparable<?> getWeight() {
      return (Comparable)this.get(this.treeNodeConfig.getWeightKey());
   }

   public Tree<T> setWeight(Comparable<?> weight) {
      this.put(this.treeNodeConfig.getWeightKey(), weight);
      return this;
   }

   public List<Tree<T>> getChildren() {
      return (List)this.get(this.treeNodeConfig.getChildrenKey());
   }

   public boolean hasChild() {
      return CollUtil.isNotEmpty((Collection)this.getChildren());
   }

   public void walk(Consumer<Tree<T>> consumer) {
      consumer.accept(this);
      List<Tree<T>> children = this.getChildren();
      if (CollUtil.isNotEmpty((Collection)children)) {
         children.forEach((tree) -> {
            tree.walk(consumer);
         });
      }

   }

   public Tree<T> filterNew(Filter<Tree<T>> filter) {
      return this.cloneTree().filter(filter);
   }

   public Tree<T> filter(Filter<Tree<T>> filter) {
      if (filter.accept(this)) {
         return this;
      } else {
         List<Tree<T>> children = this.getChildren();
         if (CollUtil.isNotEmpty((Collection)children)) {
            List<Tree<T>> filteredChildren = new ArrayList(children.size());
            Iterator var5 = children.iterator();

            while(var5.hasNext()) {
               Tree<T> child = (Tree)var5.next();
               Tree<T> filteredChild = child.filter(filter);
               if (null != filteredChild) {
                  filteredChildren.add(filteredChild);
               }
            }

            if (CollUtil.isNotEmpty((Collection)filteredChildren)) {
               return this.setChildren(filteredChildren);
            }

            this.setChildren((List)null);
         }

         return null;
      }
   }

   public Tree<T> setChildren(List<Tree<T>> children) {
      if (null == children) {
         this.remove(this.treeNodeConfig.getChildrenKey());
      }

      this.put(this.treeNodeConfig.getChildrenKey(), children);
      return this;
   }

   @SafeVarargs
   public final Tree<T> addChildren(Tree<T>... children) {
      if (ArrayUtil.isNotEmpty((Object[])children)) {
         List<Tree<T>> childrenList = this.getChildren();
         if (null == childrenList) {
            childrenList = new ArrayList();
            this.setChildren((List)childrenList);
         }

         Tree[] var3 = children;
         int var4 = children.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Tree<T> child = var3[var5];
            child.setParent(this);
            ((List)childrenList).add(child);
         }
      }

      return this;
   }

   public void putExtra(String key, Object value) {
      Assert.notEmpty((CharSequence)key, "Key must be not empty !");
      this.put(key, value);
   }

   public String toString() {
      StringWriter stringWriter = new StringWriter();
      printTree(this, new PrintWriter(stringWriter), 0);
      return stringWriter.toString();
   }

   public Tree<T> cloneTree() {
      Tree<T> result = (Tree)ObjectUtil.clone(this);
      result.setChildren(this.cloneChildren());
      return result;
   }

   private List<Tree<T>> cloneChildren() {
      List<Tree<T>> children = this.getChildren();
      if (null == children) {
         return null;
      } else {
         List<Tree<T>> newChildren = new ArrayList(children.size());
         children.forEach((t) -> {
            newChildren.add(t.cloneTree());
         });
         return newChildren;
      }
   }

   private static void printTree(Tree<?> tree, PrintWriter writer, int intent) {
      writer.println(StrUtil.format("{}{}[{}]", new Object[]{StrUtil.repeat(' ', intent), tree.getName(), tree.getId()}));
      writer.flush();
      List<? extends Tree<?>> children = tree.getChildren();
      if (CollUtil.isNotEmpty((Collection)children)) {
         Iterator var4 = children.iterator();

         while(var4.hasNext()) {
            Tree<?> child = (Tree)var4.next();
            printTree(child, writer, intent + 2);
         }
      }

   }
}
