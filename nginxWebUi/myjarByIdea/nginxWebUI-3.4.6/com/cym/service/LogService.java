package com.cym.service;

import cn.hutool.core.util.StrUtil;
import com.cym.model.Log;
import com.cym.sqlhelper.bean.Page;
import com.cym.sqlhelper.utils.ConditionAndWrapper;
import com.cym.sqlhelper.utils.JdbcTemplate;
import com.cym.sqlhelper.utils.SqlHelper;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class LogService {
   @Inject
   SqlHelper sqlHelper;
   @Inject
   JdbcTemplate jdbcTemplate;
   Logger logger = LoggerFactory.getLogger(this.getClass());

   public boolean hasDir(String path, String id) {
      ConditionAndWrapper conditionAndWrapper = (new ConditionAndWrapper()).eq((String)"path", path);
      if (StrUtil.isNotEmpty(id)) {
         conditionAndWrapper.ne((String)"id", id);
      }

      return this.sqlHelper.findCountByQuery(conditionAndWrapper, Log.class) > 0L;
   }

   public Page search(Page page) {
      return this.sqlHelper.findPage(page, Log.class);
   }
}
