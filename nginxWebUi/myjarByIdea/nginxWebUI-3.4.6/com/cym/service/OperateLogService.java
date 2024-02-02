package com.cym.service;

import com.cym.model.OperateLog;
import com.cym.sqlhelper.bean.Page;
import com.cym.sqlhelper.utils.ConditionAndWrapper;
import com.cym.sqlhelper.utils.ConditionWrapper;
import com.cym.sqlhelper.utils.SqlHelper;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;

@Service
public class OperateLogService {
   @Inject
   SqlHelper sqlHelper;

   public Page search(Page page) {
      ConditionAndWrapper conditionAndWrapper = new ConditionAndWrapper();
      page = this.sqlHelper.findPage((ConditionWrapper)conditionAndWrapper, page, OperateLog.class);
      return page;
   }

   public void addLog(String beforeConf, String afterConf, String adminName) {
      OperateLog operateLog = new OperateLog();
      operateLog.setAdminName(adminName);
      operateLog.setBeforeConf(beforeConf);
      operateLog.setAfterConf(afterConf);
      this.sqlHelper.insert(operateLog);
   }
}
