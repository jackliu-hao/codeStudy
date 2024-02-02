/*    */ package com.cym.service;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import com.cym.model.Group;
/*    */ import com.cym.model.Remote;
/*    */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*    */ import com.cym.sqlhelper.utils.ConditionOrWrapper;
/*    */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*    */ import com.cym.sqlhelper.utils.SqlHelper;
/*    */ import java.util.List;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.aspect.annotation.Service;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Service
/*    */ public class GroupService
/*    */ {
/*    */   @Inject
/*    */   SqlHelper sqlHelper;
/*    */   
/*    */   public void delete(String id) {
/* 24 */     this.sqlHelper.deleteById(id, Group.class);
/*    */     
/* 26 */     List<Remote> remoteList = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("parentId", id), Remote.class);
/* 27 */     for (Remote remote : remoteList) {
/* 28 */       remote.setParentId(null);
/* 29 */       this.sqlHelper.updateAllColumnById(remote);
/*    */     } 
/*    */     
/* 32 */     List<Group> groupList = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("parentId", id), Group.class);
/* 33 */     for (Group group : groupList) {
/* 34 */       group.setParentId(null);
/* 35 */       this.sqlHelper.updateAllColumnById(group);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public List<Group> getListByParent(String id) {
/* 41 */     ConditionAndWrapper conditionAndWrapper = new ConditionAndWrapper();
/* 42 */     if (StrUtil.isEmpty(id)) {
/* 43 */       conditionAndWrapper.and((ConditionWrapper)(new ConditionOrWrapper()).eq("parentId", "").isNull("parentId"));
/*    */     } else {
/* 45 */       conditionAndWrapper.eq("parentId", id);
/*    */     } 
/*    */     
/* 48 */     return this.sqlHelper.findListByQuery((ConditionWrapper)conditionAndWrapper, Group.class);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\service\GroupService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */