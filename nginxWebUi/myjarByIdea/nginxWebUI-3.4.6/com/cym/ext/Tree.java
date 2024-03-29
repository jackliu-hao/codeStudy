package com.cym.ext;

import java.util.List;

public class Tree {
   String name;
   String value;
   List<Tree> children;

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getValue() {
      return this.value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public List<Tree> getChildren() {
      return this.children;
   }

   public void setChildren(List<Tree> children) {
      this.children = children;
   }
}
