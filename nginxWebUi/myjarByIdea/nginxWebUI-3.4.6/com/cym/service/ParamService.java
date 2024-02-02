package com.cym.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cym.model.Param;
import com.cym.model.Template;
import com.cym.sqlhelper.reflection.SerializableFunction;
import com.cym.sqlhelper.utils.ConditionAndWrapper;
import com.cym.sqlhelper.utils.ConditionWrapper;
import com.cym.sqlhelper.utils.SqlHelper;
import java.lang.invoke.SerializedLambda;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;

@Service
public class ParamService {
   @Inject
   SqlHelper sqlHelper;

   public String getJsonByTypeId(String id, String type) {
      List<Param> list = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((String)(type + "Id"), id), Param.class);
      Iterator var4 = list.iterator();

      while(var4.hasNext()) {
         Param param = (Param)var4.next();
         if (StrUtil.isNotEmpty(param.getTemplateValue())) {
            Template template = (Template)this.sqlHelper.findById(param.getTemplateValue(), Template.class);
            param.setTemplateName(template.getName());
         }
      }

      return JSONUtil.toJsonStr((Object)list);
   }

   public List<Param> getListByTypeId(String id, String type) {
      List<Param> list = new ArrayList();
      List<Template> templateList = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((SerializableFunction)(Template::getDef), type), Template.class);
      Iterator var5 = templateList.iterator();

      while(var5.hasNext()) {
         Template template = (Template)var5.next();
         List<Param> addList = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((SerializableFunction)(Param::getTemplateId), template.getId()), Param.class);
         list.addAll(addList);
      }

      if (type.contains("server")) {
         type = "server";
      }

      list.addAll(this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((String)(type + "Id"), id), Param.class));
      return list;
   }

   public List<Param> getList(String serverId, String locationId, String upstreamId) {
      ConditionAndWrapper conditionAndWrapper = new ConditionAndWrapper();
      if (StrUtil.isNotEmpty(serverId)) {
         conditionAndWrapper.eq((String)"serverId", serverId);
      }

      if (StrUtil.isNotEmpty(locationId)) {
         conditionAndWrapper.eq((String)"locationId", locationId);
      }

      if (StrUtil.isNotEmpty(upstreamId)) {
         conditionAndWrapper.eq((String)"upstreamId", upstreamId);
      }

      return this.sqlHelper.findListByQuery((ConditionWrapper)conditionAndWrapper, Param.class);
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "getTemplateId":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Param") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Param::getTemplateId;
            }
            break;
         case "getDef":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Template") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Template::getDef;
            }
      }

      throw new IllegalArgumentException("Invalid lambda deserialization");
   }
}
