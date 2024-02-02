package com.cym.service;

import com.cym.model.Basic;
import com.cym.sqlhelper.bean.Sort;
import com.cym.sqlhelper.utils.ConditionOrWrapper;
import com.cym.sqlhelper.utils.SqlHelper;
import java.lang.invoke.SerializedLambda;
import java.util.List;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;

@Service
public class BasicService {
   @Inject
   SqlHelper sqlHelper;

   public List<Basic> findAll() {
      return this.sqlHelper.findAll((new Sort()).add("seq", Sort.Direction.ASC), Basic.class);
   }

   public void setSeq(String basicId, Integer seqAdd) {
      Basic basic = (Basic)this.sqlHelper.findById(basicId, Basic.class);
      List<Basic> basicList = this.sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Basic.class);
      if (basicList.size() > 0) {
         Basic tagert = null;
         int i;
         if (seqAdd < 0) {
            for(i = 0; i < basicList.size(); ++i) {
               if (((Basic)basicList.get(i)).getSeq() < basic.getSeq()) {
                  tagert = (Basic)basicList.get(i);
               }
            }
         } else {
            for(i = basicList.size() - 1; i >= 0; --i) {
               if (((Basic)basicList.get(i)).getSeq() > basic.getSeq()) {
                  tagert = (Basic)basicList.get(i);
               }
            }
         }

         if (tagert != null) {
            Long seq = tagert.getSeq();
            tagert.setSeq(basic.getSeq());
            basic.setSeq(seq);
            this.sqlHelper.updateById(tagert);
            this.sqlHelper.updateById(basic);
         }
      }

   }

   public boolean contain(String content) {
      return this.sqlHelper.findCountByQuery((new ConditionOrWrapper()).like(Basic::getValue, content).like(Basic::getName, content), Basic.class) > 0L;
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "getValue":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Basic") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Basic::getValue;
            }
            break;
         case "getName":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Basic") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Basic::getName;
            }
      }

      throw new IllegalArgumentException("Invalid lambda deserialization");
   }
}
