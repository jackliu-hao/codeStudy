/*    */ package com.cym.service;
/*    */ 
/*    */ import cn.hutool.core.lang.UUID;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import com.cym.model.Credit;
/*    */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*    */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*    */ import com.cym.sqlhelper.utils.SqlHelper;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.aspect.annotation.Service;
/*    */ 
/*    */ 
/*    */ @Service
/*    */ public class CreditService
/*    */ {
/*    */   @Inject
/*    */   SqlHelper sqlHelper;
/*    */   
/*    */   public String make(String adminId) {
/* 20 */     Credit credit = new Credit();
/* 21 */     credit.setKey(UUID.randomUUID().toString());
/* 22 */     credit.setAdminId(adminId);
/* 23 */     this.sqlHelper.insertOrUpdate(credit);
/*    */     
/* 25 */     return credit.getKey();
/*    */   }
/*    */   
/*    */   public boolean check(String key) {
/* 29 */     if (StrUtil.isEmpty(key)) {
/* 30 */       return false;
/*    */     }
/*    */     
/* 33 */     Credit credit = (Credit)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("key", key), Credit.class);
/*    */     
/* 35 */     if (credit == null) {
/* 36 */       return false;
/*    */     }
/* 38 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\service\CreditService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */