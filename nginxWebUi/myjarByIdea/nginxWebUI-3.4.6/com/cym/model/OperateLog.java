package com.cym.model;

import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.config.Table;

@Table
public class OperateLog extends BaseModel {
   String adminName;
   String beforeConf;
   String afterConf;

   public String getAdminName() {
      return this.adminName;
   }

   public void setAdminName(String adminName) {
      this.adminName = adminName;
   }

   public String getBeforeConf() {
      return this.beforeConf;
   }

   public void setBeforeConf(String beforeConf) {
      this.beforeConf = beforeConf;
   }

   public String getAfterConf() {
      return this.afterConf;
   }

   public void setAfterConf(String afterConf) {
      this.afterConf = afterConf;
   }
}
