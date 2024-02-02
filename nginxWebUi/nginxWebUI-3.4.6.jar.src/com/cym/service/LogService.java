/*    */ package com.cym.service;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import com.cym.model.Log;
/*    */ import com.cym.sqlhelper.bean.Page;
/*    */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*    */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*    */ import com.cym.sqlhelper.utils.JdbcTemplate;
/*    */ import com.cym.sqlhelper.utils.SqlHelper;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.aspect.annotation.Service;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ @Service
/*    */ public class LogService
/*    */ {
/*    */   @Inject
/*    */   SqlHelper sqlHelper;
/*    */   @Inject
/*    */   JdbcTemplate jdbcTemplate;
/* 23 */   Logger logger = LoggerFactory.getLogger(getClass());
/*    */   
/*    */   public boolean hasDir(String path, String id) {
/* 26 */     ConditionAndWrapper conditionAndWrapper = (new ConditionAndWrapper()).eq("path", path);
/* 27 */     if (StrUtil.isNotEmpty(id)) {
/* 28 */       conditionAndWrapper.ne("id", id);
/*    */     }
/* 30 */     return (this.sqlHelper.findCountByQuery((ConditionWrapper)conditionAndWrapper, Log.class).longValue() > 0L);
/*    */   }
/*    */ 
/*    */   
/*    */   public Page search(Page page) {
/* 35 */     return this.sqlHelper.findPage(page, Log.class);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\service\LogService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */