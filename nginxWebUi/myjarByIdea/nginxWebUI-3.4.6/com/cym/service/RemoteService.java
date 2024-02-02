package com.cym.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import com.cym.model.Admin;
import com.cym.model.Group;
import com.cym.model.Remote;
import com.cym.sqlhelper.utils.ConditionAndWrapper;
import com.cym.sqlhelper.utils.ConditionOrWrapper;
import com.cym.sqlhelper.utils.ConditionWrapper;
import com.cym.sqlhelper.utils.SqlHelper;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RemoteService {
   Logger logger = LoggerFactory.getLogger(this.getClass());
   @Inject
   SqlHelper sqlHelper;
   @Inject
   AdminService adminService;

   public void getCreditKey(Remote remote, String code, String auth) {
      Map<String, Object> paramMap = new HashMap();
      paramMap.put("name", Base64.encode((CharSequence)Base64.encode((CharSequence)remote.getName())));
      paramMap.put("pass", Base64.encode((CharSequence)Base64.encode((CharSequence)remote.getPass())));
      paramMap.put("code", Base64.encode((CharSequence)Base64.encode((CharSequence)code)));
      paramMap.put("auth", auth);

      try {
         String rs = HttpUtil.post(remote.getProtocol() + "://" + remote.getIp() + ":" + remote.getPort() + "/adminPage/login/getCredit", (Map)paramMap, 2000);
         if (StrUtil.isNotEmpty(rs)) {
            JSONObject jsonObject = new JSONObject(rs);
            if (jsonObject.getBool("success")) {
               remote.setSystem(jsonObject.getJSONObject("obj").getStr("system"));
               remote.setCreditKey(jsonObject.getJSONObject("obj").getStr("creditKey"));
            }
         }
      } catch (Exception var7) {
         this.logger.error((String)var7.getMessage(), (Throwable)var7);
      }

   }

   public List<Remote> getBySystem(String system) {
      return this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((String)"system", system), Remote.class);
   }

   public List<Remote> getListByParent(String parentId) {
      ConditionAndWrapper conditionAndWrapper = new ConditionAndWrapper();
      if (StrUtil.isEmpty(parentId)) {
         conditionAndWrapper.and((new ConditionOrWrapper()).eq((String)"parentId", "").isNull("parentId"));
      } else {
         conditionAndWrapper.eq((String)"parentId", parentId);
      }

      return this.sqlHelper.findListByQuery((ConditionWrapper)conditionAndWrapper, Remote.class);
   }

   public List<Remote> getMonitorRemoteList() {
      return this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((String)"monitor", 1), Remote.class);
   }

   public boolean hasSame(Remote remote) {
      Long count = 0L;
      if (StrUtil.isEmpty(remote.getId())) {
         count = this.sqlHelper.findCountByQuery((new ConditionAndWrapper()).eq((String)"ip", remote.getIp()).eq((String)"port", remote.getPort()), Remote.class);
      } else {
         count = this.sqlHelper.findCountByQuery((new ConditionAndWrapper()).eq((String)"ip", remote.getIp()).eq((String)"port", remote.getPort()).ne((String)"id", remote.getId()), Remote.class);
      }

      return count > 0L;
   }

   public List<Group> getGroupByAdmin(Admin admin) {
      if (admin.getType() == 0) {
         return this.sqlHelper.findAll(Group.class);
      } else {
         List<String> groupIds = this.adminService.getGroupIds(admin.getId());
         return this.sqlHelper.findListByIds((Collection)groupIds, Group.class);
      }
   }
}
