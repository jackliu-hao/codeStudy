package com.cym.model;

import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.config.Table;

@Table
public class Group extends BaseModel {
   String parentId;
   String name;

   public String getParentId() {
      return this.parentId;
   }

   public void setParentId(String parentId) {
      this.parentId = parentId;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
