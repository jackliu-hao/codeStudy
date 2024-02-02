package com.cym.model;

import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.config.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Table
public class Password extends BaseModel {
   String name;
   String pass;
   @JsonIgnore
   String path;
   String descr;
   @JsonIgnore
   String pathStr;

   public String getPathStr() {
      return this.pathStr;
   }

   public void setPathStr(String pathStr) {
      this.pathStr = pathStr;
   }

   public String getDescr() {
      return this.descr;
   }

   public void setDescr(String descr) {
      this.descr = descr;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getPass() {
      return this.pass;
   }

   public void setPass(String pass) {
      this.pass = pass;
   }

   public String getPath() {
      return this.path;
   }

   public void setPath(String path) {
      this.path = path;
   }
}
