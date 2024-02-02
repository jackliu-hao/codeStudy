/*    */ package com.cym.service;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.json.JSONUtil;
/*    */ import com.cym.model.Param;
/*    */ import com.cym.model.Template;
/*    */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*    */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*    */ import com.cym.sqlhelper.utils.SqlHelper;
/*    */ import java.lang.invoke.SerializedLambda;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.aspect.annotation.Service;
/*    */ 
/*    */ 
/*    */ @Service
/*    */ public class ParamService
/*    */ {
/*    */   @Inject
/*    */   SqlHelper sqlHelper;
/*    */   
/*    */   public String getJsonByTypeId(String id, String type) {
/* 24 */     List<Param> list = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(type + "Id", id), Param.class);
/* 25 */     for (Param param : list) {
/* 26 */       if (StrUtil.isNotEmpty(param.getTemplateValue())) {
/* 27 */         Template template = (Template)this.sqlHelper.findById(param.getTemplateValue(), Template.class);
/* 28 */         param.setTemplateName(template.getName());
/*    */       } 
/*    */     } 
/*    */     
/* 32 */     return JSONUtil.toJsonStr(list);
/*    */   }
/*    */   
/*    */   public List<Param> getListByTypeId(String id, String type) {
/* 36 */     List<Param> list = new ArrayList<>();
/*    */     
/* 38 */     List<Template> templateList = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(Template::getDef, type), Template.class);
/* 39 */     for (Template template : templateList) {
/* 40 */       List<Param> addList = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(Param::getTemplateId, template.getId()), Param.class);
/* 41 */       list.addAll(addList);
/*    */     } 
/*    */     
/* 44 */     if (type.contains("server")) {
/* 45 */       type = "server";
/*    */     }
/*    */     
/* 48 */     list.addAll(this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(type + "Id", id), Param.class));
/*    */     
/* 50 */     return list;
/*    */   }
/*    */   
/*    */   public List<Param> getList(String serverId, String locationId, String upstreamId) {
/* 54 */     ConditionAndWrapper conditionAndWrapper = new ConditionAndWrapper();
/* 55 */     if (StrUtil.isNotEmpty(serverId)) {
/* 56 */       conditionAndWrapper.eq("serverId", serverId);
/*    */     }
/* 58 */     if (StrUtil.isNotEmpty(locationId)) {
/* 59 */       conditionAndWrapper.eq("locationId", locationId);
/*    */     }
/* 61 */     if (StrUtil.isNotEmpty(upstreamId)) {
/* 62 */       conditionAndWrapper.eq("upstreamId", upstreamId);
/*    */     }
/*    */     
/* 65 */     return this.sqlHelper.findListByQuery((ConditionWrapper)conditionAndWrapper, Param.class);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\service\ParamService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */