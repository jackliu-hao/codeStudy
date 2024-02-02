package com.cym.service;

import com.cym.model.Param;
import com.cym.model.Template;
import com.cym.sqlhelper.utils.ConditionAndWrapper;
import com.cym.sqlhelper.utils.ConditionWrapper;
import com.cym.sqlhelper.utils.SqlHelper;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;

@Service
public class TemplateService {
   @Inject
   SqlHelper sqlHelper;

   public void addOver(Template template, List<Param> params) {
      this.sqlHelper.insertOrUpdate(template);
      this.sqlHelper.deleteByQuery((new ConditionAndWrapper()).eq((String)"templateId", template.getId()), Param.class);
      Collections.reverse(params);
      Iterator var3 = params.iterator();

      while(var3.hasNext()) {
         Param param = (Param)var3.next();
         param.setTemplateId(template.getId());
         this.sqlHelper.insertOrUpdate(param);
      }

   }

   public List<Param> getParamList(String templateId) {
      return this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((String)"templateId", templateId), Param.class);
   }

   public void del(String id) {
      this.sqlHelper.deleteById(id, Template.class);
      this.sqlHelper.deleteByQuery((new ConditionAndWrapper()).eq((String)"templateId", id), Param.class);
      this.sqlHelper.deleteByQuery((new ConditionAndWrapper()).eq((String)"templateValue", id), Param.class);
   }

   public Long getCountByName(String name) {
      return this.sqlHelper.findCountByQuery((new ConditionAndWrapper()).eq((String)"name", name), Template.class);
   }

   public Long getCountByNameWithOutId(String name, String id) {
      return this.sqlHelper.findCountByQuery((new ConditionAndWrapper()).eq((String)"name", name).ne((String)"id", id), Template.class);
   }
}
