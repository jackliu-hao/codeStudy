package com.cym.service;

import com.cym.model.Bak;
import com.cym.model.BakSub;
import com.cym.sqlhelper.bean.Page;
import com.cym.sqlhelper.bean.Sort;
import com.cym.sqlhelper.reflection.SerializableFunction;
import com.cym.sqlhelper.utils.ConditionAndWrapper;
import com.cym.sqlhelper.utils.ConditionWrapper;
import com.cym.sqlhelper.utils.SqlHelper;
import java.lang.invoke.SerializedLambda;
import java.util.List;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;

@Service
public class BakService {
   @Inject
   SqlHelper sqlHelper;

   public Page<Bak> getList(Page page) {
      return this.sqlHelper.findPage(new ConditionAndWrapper(), new Sort(Bak::getTime, Sort.Direction.DESC), page, Bak.class);
   }

   public List<BakSub> getSubList(String id) {
      return this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((SerializableFunction)(BakSub::getBakId), id), BakSub.class);
   }

   public void del(String id) {
      this.sqlHelper.deleteById(id, Bak.class);
      this.sqlHelper.deleteByQuery((new ConditionAndWrapper()).eq((SerializableFunction)(BakSub::getBakId), id), BakSub.class);
   }

   public void delAll() {
      this.sqlHelper.deleteByQuery(new ConditionAndWrapper(), Bak.class);
      this.sqlHelper.deleteByQuery(new ConditionAndWrapper(), BakSub.class);
   }

   public Bak getPre(String id) {
      Bak bak = (Bak)this.sqlHelper.findById(id, Bak.class);
      Bak pre = (Bak)this.sqlHelper.findOneByQuery((new ConditionAndWrapper()).lt((SerializableFunction)(Bak::getTime), bak.getTime()), new Sort(Bak::getTime, Sort.Direction.DESC), Bak.class);
      return pre;
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "getBakId":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/BakSub") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return BakSub::getBakId;
            }

            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/BakSub") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return BakSub::getBakId;
            }
            break;
         case "getTime":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Bak") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Bak::getTime;
            }

            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Bak") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Bak::getTime;
            }

            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Bak") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Bak::getTime;
            }
      }

      throw new IllegalArgumentException("Invalid lambda deserialization");
   }
}
