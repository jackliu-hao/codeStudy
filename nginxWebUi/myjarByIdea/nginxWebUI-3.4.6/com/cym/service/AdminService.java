package com.cym.service;

import com.cym.model.Admin;
import com.cym.model.AdminGroup;
import com.cym.model.Credit;
import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.bean.Page;
import com.cym.sqlhelper.reflection.SerializableFunction;
import com.cym.sqlhelper.utils.ConditionAndWrapper;
import com.cym.sqlhelper.utils.ConditionWrapper;
import com.cym.sqlhelper.utils.SqlHelper;
import com.cym.utils.EncodePassUtils;
import java.lang.invoke.SerializedLambda;
import java.util.List;
import java.util.UUID;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;

@Service
public class AdminService {
   @Inject
   SqlHelper sqlHelper;

   public Admin login(String name, String pass) {
      return (Admin)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((SerializableFunction)(Admin::getName), name).eq((SerializableFunction)(Admin::getPass), EncodePassUtils.encode(pass)), Admin.class);
   }

   public Page search(Page page) {
      page = this.sqlHelper.findPage(page, Admin.class);
      return page;
   }

   public Long getCountByName(String name) {
      return this.sqlHelper.findCountByQuery((new ConditionAndWrapper()).eq((SerializableFunction)(Admin::getName), name), Admin.class);
   }

   public Long getCountByNameWithOutId(String name, String id) {
      return this.sqlHelper.findCountByQuery((new ConditionAndWrapper()).eq((SerializableFunction)(Admin::getName), name).ne((SerializableFunction)(BaseModel::getId), id), Admin.class);
   }

   public Admin getOneByName(String name) {
      return (Admin)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((SerializableFunction)(Admin::getName), name), Admin.class);
   }

   public String makeToken(String id) {
      String token = UUID.randomUUID().toString();
      Admin admin = new Admin();
      admin.setId(id);
      admin.setToken(token);
      this.sqlHelper.updateById(admin);
      return token;
   }

   public Admin getByToken(String token) {
      return (Admin)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((SerializableFunction)(Admin::getToken), token), Admin.class);
   }

   public Admin getByCreditKey(String creditKey) {
      Credit credit = (Credit)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((SerializableFunction)(Credit::getKey), creditKey), Credit.class);
      if (credit != null) {
         Admin admin = (Admin)this.sqlHelper.findById(credit.getAdminId(), Admin.class);
         return admin;
      } else {
         return null;
      }
   }

   public List<String> getGroupIds(String adminId) {
      return this.sqlHelper.findPropertiesByQuery((new ConditionAndWrapper()).eq((SerializableFunction)(AdminGroup::getAdminId), adminId), AdminGroup.class, (SerializableFunction)(AdminGroup::getGroupId));
   }

   public void addOver(Admin admin, String[] groupIds) {
      admin.setPass(EncodePassUtils.encode(admin.getPass()));
      this.sqlHelper.insertOrUpdate(admin);
      this.sqlHelper.deleteByQuery((new ConditionAndWrapper()).eq((SerializableFunction)(AdminGroup::getAdminId), admin.getId()), AdminGroup.class);
      if (admin.getType() == 1) {
         String[] var3 = groupIds;
         int var4 = groupIds.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String id = var3[var5];
            AdminGroup adminGroup = new AdminGroup();
            adminGroup.setAdminId(admin.getId());
            adminGroup.setGroupId(id);
            this.sqlHelper.insert(adminGroup);
         }
      }

   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "getKey":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Credit") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Credit::getKey;
            }
            break;
         case "getName":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Admin") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Admin::getName;
            }

            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Admin") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Admin::getName;
            }

            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Admin") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Admin::getName;
            }

            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Admin") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Admin::getName;
            }
            break;
         case "getPass":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Admin") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Admin::getPass;
            }
            break;
         case "getToken":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Admin") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Admin::getToken;
            }
            break;
         case "getId":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/sqlhelper/bean/BaseModel") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return BaseModel::getId;
            }
            break;
         case "getAdminId":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/AdminGroup") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return AdminGroup::getAdminId;
            }

            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/AdminGroup") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return AdminGroup::getAdminId;
            }
            break;
         case "getGroupId":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/AdminGroup") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return AdminGroup::getGroupId;
            }
      }

      throw new IllegalArgumentException("Invalid lambda deserialization");
   }
}
