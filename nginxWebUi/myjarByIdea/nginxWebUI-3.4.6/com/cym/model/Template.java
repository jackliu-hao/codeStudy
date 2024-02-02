package com.cym.model;

import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.config.Table;

@Table
public class Template extends BaseModel {
   String name;
   String def;

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDef() {
      return this.def;
   }

   public void setDef(String def) {
      this.def = def;
   }
}
