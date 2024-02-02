package com.cym.ext;

import com.cym.model.Admin;
import java.util.List;

public class AdminExt {
   Admin admin;
   List<String> groupIds;

   public Admin getAdmin() {
      return this.admin;
   }

   public void setAdmin(Admin admin) {
      this.admin = admin;
   }

   public List<String> getGroupIds() {
      return this.groupIds;
   }

   public void setGroupIds(List<String> groupIds) {
      this.groupIds = groupIds;
   }
}
