package com.cym.model;

import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.config.Table;

@Table
public class AdminGroup extends BaseModel {
   String adminId;
   String groupId;

   public String getAdminId() {
      return this.adminId;
   }

   public void setAdminId(String adminId) {
      this.adminId = adminId;
   }

   public String getGroupId() {
      return this.groupId;
   }

   public void setGroupId(String groupId) {
      this.groupId = groupId;
   }
}
