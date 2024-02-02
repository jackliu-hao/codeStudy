package com.cym.service;

import cn.hutool.core.util.StrUtil;
import com.cym.model.Group;
import com.cym.model.Remote;
import com.cym.sqlhelper.utils.ConditionAndWrapper;
import com.cym.sqlhelper.utils.ConditionOrWrapper;
import com.cym.sqlhelper.utils.ConditionWrapper;
import com.cym.sqlhelper.utils.SqlHelper;
import java.util.Iterator;
import java.util.List;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;

@Service
public class GroupService {
   @Inject
   SqlHelper sqlHelper;

   public void delete(String id) {
      this.sqlHelper.deleteById(id, Group.class);
      List<Remote> remoteList = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((String)"parentId", id), Remote.class);
      Iterator var3 = remoteList.iterator();

      while(var3.hasNext()) {
         Remote remote = (Remote)var3.next();
         remote.setParentId((String)null);
         this.sqlHelper.updateAllColumnById(remote);
      }

      List<Group> groupList = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((String)"parentId", id), Group.class);
      Iterator var7 = groupList.iterator();

      while(var7.hasNext()) {
         Group group = (Group)var7.next();
         group.setParentId((String)null);
         this.sqlHelper.updateAllColumnById(group);
      }

   }

   public List<Group> getListByParent(String id) {
      ConditionAndWrapper conditionAndWrapper = new ConditionAndWrapper();
      if (StrUtil.isEmpty(id)) {
         conditionAndWrapper.and((new ConditionOrWrapper()).eq((String)"parentId", "").isNull("parentId"));
      } else {
         conditionAndWrapper.eq((String)"parentId", id);
      }

      return this.sqlHelper.findListByQuery((ConditionWrapper)conditionAndWrapper, Group.class);
   }
}
