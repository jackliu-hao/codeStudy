package com.cym.service;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.cym.model.Credit;
import com.cym.sqlhelper.utils.ConditionAndWrapper;
import com.cym.sqlhelper.utils.ConditionWrapper;
import com.cym.sqlhelper.utils.SqlHelper;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;

@Service
public class CreditService {
   @Inject
   SqlHelper sqlHelper;

   public String make(String adminId) {
      Credit credit = new Credit();
      credit.setKey(UUID.randomUUID().toString());
      credit.setAdminId(adminId);
      this.sqlHelper.insertOrUpdate(credit);
      return credit.getKey();
   }

   public boolean check(String key) {
      if (StrUtil.isEmpty(key)) {
         return false;
      } else {
         Credit credit = (Credit)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((String)"key", key), Credit.class);
         return credit != null;
      }
   }
}
