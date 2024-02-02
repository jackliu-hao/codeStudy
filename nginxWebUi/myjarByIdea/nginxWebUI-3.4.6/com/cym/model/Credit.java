package com.cym.model;

import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.config.Table;

@Table
public class Credit extends BaseModel {
   String key;
   String adminId;

   public String getAdminId() {
      return this.adminId;
   }

   public void setAdminId(String adminId) {
      this.adminId = adminId;
   }

   public String getKey() {
      return this.key;
   }

   public void setKey(String key) {
      this.key = key;
   }
}
