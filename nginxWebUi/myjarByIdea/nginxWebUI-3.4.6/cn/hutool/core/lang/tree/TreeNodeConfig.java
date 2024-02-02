package cn.hutool.core.lang.tree;

import java.io.Serializable;

public class TreeNodeConfig implements Serializable {
   private static final long serialVersionUID = 1L;
   public static TreeNodeConfig DEFAULT_CONFIG = new TreeNodeConfig();
   private String idKey = "id";
   private String parentIdKey = "parentId";
   private String weightKey = "weight";
   private String nameKey = "name";
   private String childrenKey = "children";
   private Integer deep;

   public String getIdKey() {
      return this.idKey;
   }

   public TreeNodeConfig setIdKey(String idKey) {
      this.idKey = idKey;
      return this;
   }

   public String getWeightKey() {
      return this.weightKey;
   }

   public TreeNodeConfig setWeightKey(String weightKey) {
      this.weightKey = weightKey;
      return this;
   }

   public String getNameKey() {
      return this.nameKey;
   }

   public TreeNodeConfig setNameKey(String nameKey) {
      this.nameKey = nameKey;
      return this;
   }

   public String getChildrenKey() {
      return this.childrenKey;
   }

   public TreeNodeConfig setChildrenKey(String childrenKey) {
      this.childrenKey = childrenKey;
      return this;
   }

   public String getParentIdKey() {
      return this.parentIdKey;
   }

   public TreeNodeConfig setParentIdKey(String parentIdKey) {
      this.parentIdKey = parentIdKey;
      return this;
   }

   public Integer getDeep() {
      return this.deep;
   }

   public TreeNodeConfig setDeep(Integer deep) {
      this.deep = deep;
      return this;
   }
}
