package com.cym.service;

import com.cym.model.Http;
import com.cym.model.Param;
import com.cym.sqlhelper.bean.Sort;
import com.cym.sqlhelper.reflection.SerializableFunction;
import com.cym.sqlhelper.utils.ConditionAndWrapper;
import com.cym.sqlhelper.utils.ConditionWrapper;
import com.cym.sqlhelper.utils.SqlHelper;
import com.cym.utils.SnowFlakeUtils;
import java.lang.invoke.SerializedLambda;
import java.util.Iterator;
import java.util.List;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;

@Service
public class HttpService {
   @Inject
   SqlHelper sqlHelper;

   public List<Http> findAll() {
      return this.sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Http.class);
   }

   public void setAll(List<Http> https) {
      Http logFormat = null;
      Http accessLog = null;
      Iterator var4 = https.iterator();

      Http http;
      while(var4.hasNext()) {
         http = (Http)var4.next();
         if (http.getName().equals("log_format")) {
            logFormat = http;
         }

         if (http.getName().equals("access_log")) {
            accessLog = http;
         }
      }

      if (logFormat != null) {
         https.remove(logFormat);
         https.add(logFormat);
      }

      if (accessLog != null) {
         https.remove(accessLog);
         https.add(accessLog);
      }

      var4 = https.iterator();

      while(var4.hasNext()) {
         http = (Http)var4.next();
         Http httpOrg = (Http)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((String)"name", http.getName()), Http.class);
         if (httpOrg != null) {
            http.setId(httpOrg.getId());
         }

         http.setSeq(SnowFlakeUtils.getId());
         http.setValue(http.getValue() + http.getUnit());
         this.sqlHelper.insertOrUpdate(http);
      }

   }

   public void setSeq(String httpId, Integer seqAdd) {
      Http http = (Http)this.sqlHelper.findById(httpId, Http.class);
      List<Http> httpList = this.sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Http.class);
      if (httpList.size() > 0) {
         Http tagert = null;
         int i;
         if (seqAdd < 0) {
            for(i = 0; i < httpList.size(); ++i) {
               if (((Http)httpList.get(i)).getSeq() < http.getSeq()) {
                  tagert = (Http)httpList.get(i);
               }
            }
         } else {
            for(i = httpList.size() - 1; i >= 0; --i) {
               if (((Http)httpList.get(i)).getSeq() > http.getSeq()) {
                  tagert = (Http)httpList.get(i);
               }
            }
         }

         if (tagert != null) {
            Long seq = tagert.getSeq();
            tagert.setSeq(http.getSeq());
            http.setSeq(seq);
            this.sqlHelper.updateById(tagert);
            this.sqlHelper.updateById(http);
         }
      }

   }

   public Http getName(String name) {
      Http http = (Http)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((String)"name", name), Http.class);
      return http;
   }

   public void addTemplate(String templateId) {
      List<Param> parmList = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((SerializableFunction)(Param::getTemplateId), templateId), Param.class);
      Iterator var3 = parmList.iterator();

      while(var3.hasNext()) {
         Param param = (Param)var3.next();
         Http http = new Http();
         http.setName(param.getName());
         http.setValue(param.getValue());
         http.setSeq(SnowFlakeUtils.getId());
         this.sqlHelper.insert(http);
      }

   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "getTemplateId":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Param") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Param::getTemplateId;
            }
         default:
            throw new IllegalArgumentException("Invalid lambda deserialization");
      }
   }
}
