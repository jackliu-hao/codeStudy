package com.cym.ext;

public class TreeNode {
   String id;
   String pid;
   String name;
   String isParent;

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getPid() {
      return this.pid;
   }

   public void setPid(String pid) {
      this.pid = pid;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getIsParent() {
      return this.isParent;
   }

   public void setIsParent(String isParent) {
      this.isParent = isParent;
   }
}
