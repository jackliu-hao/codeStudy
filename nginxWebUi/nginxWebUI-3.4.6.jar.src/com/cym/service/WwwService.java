/*    */ package com.cym.service;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import com.cym.model.Www;
/*    */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*    */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*    */ import com.cym.sqlhelper.utils.SqlHelper;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.aspect.annotation.Service;
/*    */ 
/*    */ 
/*    */ @Service
/*    */ public class WwwService
/*    */ {
/*    */   @Inject
/*    */   SqlHelper sqlHelper;
/*    */   
/*    */   public Boolean hasDir(String dir, String id) {
/* 19 */     ConditionAndWrapper conditionAndWrapper = (new ConditionAndWrapper()).eq("dir", dir);
/* 20 */     if (StrUtil.isNotEmpty(id)) {
/* 21 */       conditionAndWrapper.ne("id", id);
/*    */     }
/* 23 */     return Boolean.valueOf((this.sqlHelper.findCountByQuery((ConditionWrapper)conditionAndWrapper, Www.class).longValue() > 0L));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\service\WwwService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */