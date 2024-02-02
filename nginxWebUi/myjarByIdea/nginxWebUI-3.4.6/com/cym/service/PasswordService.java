package com.cym.service;

import com.cym.model.Password;
import com.cym.sqlhelper.bean.Page;
import com.cym.sqlhelper.utils.ConditionAndWrapper;
import com.cym.sqlhelper.utils.SqlHelper;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;

@Service
public class PasswordService {
   @Inject
   SqlHelper sqlHelper;

   public Page search(Page page) {
      page = this.sqlHelper.findPage(page, Password.class);
      return page;
   }

   public Long getCountByName(String name) {
      return this.sqlHelper.findCountByQuery((new ConditionAndWrapper()).eq((String)"name", name), Password.class);
   }

   public Long getCountByNameWithOutId(String name, String id) {
      return this.sqlHelper.findCountByQuery((new ConditionAndWrapper()).eq((String)"name", name).ne((String)"id", id), Password.class);
   }
}
