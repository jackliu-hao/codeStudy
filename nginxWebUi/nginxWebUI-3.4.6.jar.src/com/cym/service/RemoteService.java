/*    */ package com.cym.service;
/*    */ 
/*    */ import cn.hutool.core.codec.Base64;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.http.HttpUtil;
/*    */ import cn.hutool.json.JSONObject;
/*    */ import com.cym.model.Admin;
/*    */ import com.cym.model.Group;
/*    */ import com.cym.model.Remote;
/*    */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*    */ import com.cym.sqlhelper.utils.ConditionOrWrapper;
/*    */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*    */ import com.cym.sqlhelper.utils.SqlHelper;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.aspect.annotation.Service;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ @Service
/*    */ public class RemoteService
/*    */ {
/* 26 */   Logger logger = LoggerFactory.getLogger(getClass());
/*    */   @Inject
/*    */   SqlHelper sqlHelper;
/*    */   @Inject
/*    */   AdminService adminService;
/*    */   
/*    */   public void getCreditKey(Remote remote, String code, String auth) {
/* 33 */     Map<String, Object> paramMap = new HashMap<>();
/*    */     
/* 35 */     paramMap.put("name", Base64.encode(Base64.encode(remote.getName())));
/* 36 */     paramMap.put("pass", Base64.encode(Base64.encode(remote.getPass())));
/* 37 */     paramMap.put("code", Base64.encode(Base64.encode(code)));
/* 38 */     paramMap.put("auth", auth);
/*    */     try {
/* 40 */       String rs = HttpUtil.post(remote.getProtocol() + "://" + remote.getIp() + ":" + remote.getPort() + "/adminPage/login/getCredit", paramMap, 2000);
/*    */       
/* 42 */       if (StrUtil.isNotEmpty(rs)) {
/* 43 */         JSONObject jsonObject = new JSONObject(rs);
/* 44 */         if (jsonObject.getBool("success").booleanValue()) {
/* 45 */           remote.setSystem(jsonObject.getJSONObject("obj").getStr("system"));
/* 46 */           remote.setCreditKey(jsonObject.getJSONObject("obj").getStr("creditKey"));
/*    */         } 
/*    */       } 
/* 49 */     } catch (Exception e) {
/* 50 */       this.logger.error(e.getMessage(), e);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public List<Remote> getBySystem(String system) {
/* 56 */     return this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("system", system), Remote.class);
/*    */   }
/*    */   
/*    */   public List<Remote> getListByParent(String parentId) {
/* 60 */     ConditionAndWrapper conditionAndWrapper = new ConditionAndWrapper();
/* 61 */     if (StrUtil.isEmpty(parentId)) {
/* 62 */       conditionAndWrapper.and((ConditionWrapper)(new ConditionOrWrapper()).eq("parentId", "").isNull("parentId"));
/*    */     } else {
/* 64 */       conditionAndWrapper.eq("parentId", parentId);
/*    */     } 
/*    */     
/* 67 */     return this.sqlHelper.findListByQuery((ConditionWrapper)conditionAndWrapper, Remote.class);
/*    */   }
/*    */   
/*    */   public List<Remote> getMonitorRemoteList() {
/* 71 */     return this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("monitor", Integer.valueOf(1)), Remote.class);
/*    */   }
/*    */   
/*    */   public boolean hasSame(Remote remote) {
/* 75 */     Long count = Long.valueOf(0L);
/* 76 */     if (StrUtil.isEmpty(remote.getId())) {
/* 77 */       count = this.sqlHelper.findCountByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("ip", remote.getIp()).eq("port", remote.getPort()), Remote.class);
/*    */     } else {
/* 79 */       count = this.sqlHelper.findCountByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("ip", remote.getIp()).eq("port", remote.getPort()).ne("id", remote.getId()), Remote.class);
/*    */     } 
/*    */     
/* 82 */     if (count.longValue() > 0L) {
/* 83 */       return true;
/*    */     }
/* 85 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public List<Group> getGroupByAdmin(Admin admin) {
/* 90 */     if (admin.getType().intValue() == 0) {
/* 91 */       return this.sqlHelper.findAll(Group.class);
/*    */     }
/* 93 */     List<String> groupIds = this.adminService.getGroupIds(admin.getId());
/* 94 */     return this.sqlHelper.findListByIds(groupIds, Group.class);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\service\RemoteService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */