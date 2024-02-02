package com.cym.service;

import com.cym.model.Param;
import com.cym.model.Stream;
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
public class StreamService {
   @Inject
   SqlHelper sqlHelper;

   public void setSeq(String streamId, Integer seqAdd) {
      Stream http = (Stream)this.sqlHelper.findById(streamId, Stream.class);
      List<Stream> httpList = this.sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Stream.class);
      if (httpList.size() > 0) {
         Stream tagert = null;
         int i;
         if (seqAdd < 0) {
            for(i = 0; i < httpList.size(); ++i) {
               if (((Stream)httpList.get(i)).getSeq() < http.getSeq()) {
                  tagert = (Stream)httpList.get(i);
               }
            }
         } else {
            for(i = httpList.size() - 1; i >= 0; --i) {
               if (((Stream)httpList.get(i)).getSeq() > http.getSeq()) {
                  tagert = (Stream)httpList.get(i);
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

   public List<Stream> findAll() {
      return this.sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Stream.class);
   }

   public void addTemplate(String templateId) {
      List<Param> parmList = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((SerializableFunction)(Param::getTemplateId), templateId), Param.class);
      Iterator var3 = parmList.iterator();

      while(var3.hasNext()) {
         Param param = (Param)var3.next();
         Stream stream = new Stream();
         stream.setName(param.getName());
         stream.setValue(param.getValue());
         stream.setSeq(SnowFlakeUtils.getId());
         this.sqlHelper.insert(stream);
      }

   }

   public void setAll(List<Stream> streams) {
      Stream stream;
      for(Iterator var2 = streams.iterator(); var2.hasNext(); this.sqlHelper.insert(stream)) {
         stream = (Stream)var2.next();
         Stream streamOrg = (Stream)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((String)"name", stream.getName()), Stream.class);
         if (streamOrg != null) {
            this.sqlHelper.deleteById(streamOrg.getId(), Stream.class);
         }
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
