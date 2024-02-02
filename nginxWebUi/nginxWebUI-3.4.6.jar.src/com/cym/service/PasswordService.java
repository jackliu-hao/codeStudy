/*    */ package com.cym.service;
/*    */ 
/*    */ import com.cym.model.Password;
/*    */ import com.cym.sqlhelper.bean.Page;
/*    */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*    */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*    */ import com.cym.sqlhelper.utils.SqlHelper;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.aspect.annotation.Service;
/*    */ 
/*    */ @Service
/*    */ public class PasswordService {
/*    */   @Inject
/*    */   SqlHelper sqlHelper;
/*    */   
/*    */   public Page search(Page page) {
/* 17 */     page = this.sqlHelper.findPage(page, Password.class);
/*    */     
/* 19 */     return page;
/*    */   }
/*    */   
/*    */   public Long getCountByName(String name) {
/* 23 */     return this.sqlHelper.findCountByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("name", name), Password.class);
/*    */   }
/*    */   
/*    */   public Long getCountByNameWithOutId(String name, String id) {
/* 27 */     return this.sqlHelper.findCountByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("name", name).ne("id", id), Password.class);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\service\PasswordService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */