package cn.hutool.core.lang.tree;

import cn.hutool.core.builder.Builder;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.tree.parser.NodeParser;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TreeBuilder<E> implements Builder<Tree<E>> {
   private static final long serialVersionUID = 1L;
   private final Tree<E> root;
   private final Map<E, Tree<E>> idTreeMap;
   private boolean isBuild;

   public static <T> TreeBuilder<T> of(T rootId) {
      return of(rootId, (TreeNodeConfig)null);
   }

   public static <T> TreeBuilder<T> of(T rootId, TreeNodeConfig config) {
      return new TreeBuilder(rootId, config);
   }

   public TreeBuilder(E rootId, TreeNodeConfig config) {
      this.root = new Tree(config);
      this.root.setId(rootId);
      this.idTreeMap = new HashMap();
   }

   public TreeBuilder<E> setId(E id) {
      this.root.setId(id);
      return this;
   }

   public TreeBuilder<E> setParentId(E parentId) {
      this.root.setParentId(parentId);
      return this;
   }

   public TreeBuilder<E> setName(CharSequence name) {
      this.root.setName(name);
      return this;
   }

   public TreeBuilder<E> setWeight(Comparable<?> weight) {
      this.root.setWeight(weight);
      return this;
   }

   public TreeBuilder<E> putExtra(String key, Object value) {
      Assert.notEmpty((CharSequence)key, "Key must be not empty !");
      this.root.put(key, value);
      return this;
   }

   public TreeBuilder<E> append(Map<E, Tree<E>> map) {
      this.checkBuilt();
      this.idTreeMap.putAll(map);
      return this;
   }

   public TreeBuilder<E> append(Iterable<Tree<E>> trees) {
      this.checkBuilt();
      Iterator var2 = trees.iterator();

      while(var2.hasNext()) {
         Tree<E> tree = (Tree)var2.next();
         this.idTreeMap.put(tree.getId(), tree);
      }

      return this;
   }

   public <T> TreeBuilder<E> append(List<T> list, NodeParser<T, E> nodeParser) {
      this.checkBuilt();
      TreeNodeConfig config = this.root.getConfig();
      Map<E, Tree<E>> map = new LinkedHashMap(list.size(), 1.0F);
      Iterator var6 = list.iterator();

      while(var6.hasNext()) {
         T t = var6.next();
         Tree<E> node = new Tree(config);
         nodeParser.parse(t, node);
         map.put(node.getId(), node);
      }

      return this.append((Map)map);
   }

   public TreeBuilder<E> reset() {
      this.idTreeMap.clear();
      this.root.setChildren((List)null);
      this.isBuild = false;
      return this;
   }

   public Tree<E> build() {
      this.checkBuilt();
      this.buildFromMap();
      this.cutTree();
      this.isBuild = true;
      this.idTreeMap.clear();
      return this.root;
   }

   public List<Tree<E>> buildList() {
      return this.isBuild ? this.root.getChildren() : this.build().getChildren();
   }

   private void buildFromMap() {
      if (!MapUtil.isEmpty(this.idTreeMap)) {
         Map<E, Tree<E>> eTreeMap = MapUtil.sortByValue(this.idTreeMap, false);
         Iterator var3 = eTreeMap.values().iterator();

         while(var3.hasNext()) {
            Tree<E> node = (Tree)var3.next();
            if (null != node) {
               E parentId = node.getParentId();
               if (ObjectUtil.equals(this.root.getId(), parentId)) {
                  this.root.addChildren(node);
               } else {
                  Tree<E> parentNode = (Tree)eTreeMap.get(parentId);
                  if (null != parentNode) {
                     parentNode.addChildren(node);
                  }
               }
            }
         }

      }
   }

   private void cutTree() {
      TreeNodeConfig config = this.root.getConfig();
      Integer deep = config.getDeep();
      if (null != deep && deep >= 0) {
         this.cutTree(this.root, 0, deep);
      }
   }

   private void cutTree(Tree<E> tree, int currentDepp, int maxDeep) {
      if (null != tree) {
         if (currentDepp == maxDeep) {
            tree.setChildren((List)null);
         } else {
            List<Tree<E>> children = tree.getChildren();
            if (CollUtil.isNotEmpty((Collection)children)) {
               Iterator var5 = children.iterator();

               while(var5.hasNext()) {
                  Tree<E> child = (Tree)var5.next();
                  this.cutTree(child, currentDepp + 1, maxDeep);
               }
            }

         }
      }
   }

   private void checkBuilt() {
      Assert.isFalse(this.isBuild, "Current tree has been built.");
   }
}
