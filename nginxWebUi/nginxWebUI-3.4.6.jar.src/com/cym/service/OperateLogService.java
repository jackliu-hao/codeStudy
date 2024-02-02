/*    */ package com.cym.service;
/*    */ 
/*    */ import com.cym.model.OperateLog;
/*    */ import com.cym.sqlhelper.bean.Page;
/*    */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*    */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*    */ import com.cym.sqlhelper.utils.SqlHelper;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.aspect.annotation.Service;
/*    */ 
/*    */ @Service
/*    */ public class OperateLogService {
/*    */   @Inject
/*    */   SqlHelper sqlHelper;
/*    */   
/*    */   public Page search(Page page) {
/* 17 */     ConditionAndWrapper conditionAndWrapper = new ConditionAndWrapper();
/*    */     
/* 19 */     page = this.sqlHelper.findPage((ConditionWrapper)conditionAndWrapper, page, OperateLog.class);
/*    */     
/* 21 */     return page;
/*    */   }
/*    */   
/*    */   public void addLog(String beforeConf, String afterConf, String adminName) {
/* 25 */     OperateLog operateLog = new OperateLog();
/* 26 */     operateLog.setAdminName(adminName);
/* 27 */     operateLog.setBeforeConf(beforeConf);
/* 28 */     operateLog.setAfterConf(afterConf);
/*    */     
/* 30 */     this.sqlHelper.insert(operateLog);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\service\OperateLogService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */