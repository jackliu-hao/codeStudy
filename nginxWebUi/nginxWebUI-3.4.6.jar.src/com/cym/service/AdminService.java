/*    */ package com.cym.service;
/*    */ 
/*    */ import com.cym.model.Admin;
/*    */ import com.cym.model.AdminGroup;
/*    */ import com.cym.model.Credit;
/*    */ import com.cym.sqlhelper.bean.BaseModel;
/*    */ import com.cym.sqlhelper.bean.Page;
/*    */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*    */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*    */ import com.cym.sqlhelper.utils.SqlHelper;
/*    */ import com.cym.utils.EncodePassUtils;
/*    */ import java.lang.invoke.SerializedLambda;
/*    */ import java.util.List;
/*    */ import java.util.UUID;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.aspect.annotation.Service;
/*    */ 
/*    */ 
/*    */ @Service
/*    */ public class AdminService
/*    */ {
/*    */   public Admin login(String name, String pass) {
/* 23 */     return (Admin)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(Admin::getName, name).eq(Admin::getPass, EncodePassUtils.encode(pass)), Admin.class);
/*    */   } @Inject
/*    */   SqlHelper sqlHelper;
/*    */   public Page search(Page page) {
/* 27 */     page = this.sqlHelper.findPage(page, Admin.class);
/*    */     
/* 29 */     return page;
/*    */   }
/*    */   
/*    */   public Long getCountByName(String name) {
/* 33 */     return this.sqlHelper.findCountByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(Admin::getName, name), Admin.class);
/*    */   }
/*    */   
/*    */   public Long getCountByNameWithOutId(String name, String id) {
/* 37 */     return this.sqlHelper.findCountByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(Admin::getName, name).ne(BaseModel::getId, id), Admin.class);
/*    */   }
/*    */   
/*    */   public Admin getOneByName(String name) {
/* 41 */     return (Admin)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(Admin::getName, name), Admin.class);
/*    */   }
/*    */   
/*    */   public String makeToken(String id) {
/* 45 */     String token = UUID.randomUUID().toString();
/* 46 */     Admin admin = new Admin();
/* 47 */     admin.setId(id);
/* 48 */     admin.setToken(token);
/* 49 */     this.sqlHelper.updateById(admin);
/*    */     
/* 51 */     return token;
/*    */   }
/*    */   
/*    */   public Admin getByToken(String token) {
/* 55 */     return (Admin)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(Admin::getToken, token), Admin.class);
/*    */   }
/*    */ 
/*    */   
/*    */   public Admin getByCreditKey(String creditKey) {
/* 60 */     Credit credit = (Credit)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(Credit::getKey, creditKey), Credit.class);
/* 61 */     if (credit != null) {
/* 62 */       Admin admin = (Admin)this.sqlHelper.findById(credit.getAdminId(), Admin.class);
/* 63 */       return admin;
/*    */     } 
/* 65 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public List<String> getGroupIds(String adminId) {
/* 70 */     return this.sqlHelper.findPropertiesByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(AdminGroup::getAdminId, adminId), AdminGroup.class, AdminGroup::getGroupId);
/*    */   }
/*    */   
/*    */   public void addOver(Admin admin, String[] groupIds) {
/* 74 */     admin.setPass(EncodePassUtils.encode(admin.getPass()));
/* 75 */     this.sqlHelper.insertOrUpdate(admin);
/*    */     
/* 77 */     this.sqlHelper.deleteByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(AdminGroup::getAdminId, admin.getId()), AdminGroup.class);
/* 78 */     if (admin.getType().intValue() == 1)
/* 79 */       for (String id : groupIds) {
/* 80 */         AdminGroup adminGroup = new AdminGroup();
/* 81 */         adminGroup.setAdminId(admin.getId());
/* 82 */         adminGroup.setGroupId(id);
/* 83 */         this.sqlHelper.insert(adminGroup);
/*    */       }  
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\service\AdminService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */