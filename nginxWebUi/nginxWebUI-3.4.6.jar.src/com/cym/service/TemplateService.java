/*    */ package com.cym.service;
/*    */ 
/*    */ import com.cym.model.Param;
/*    */ import com.cym.model.Template;
/*    */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*    */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*    */ import com.cym.sqlhelper.utils.SqlHelper;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.aspect.annotation.Service;
/*    */ 
/*    */ 
/*    */ @Service
/*    */ public class TemplateService
/*    */ {
/*    */   @Inject
/*    */   SqlHelper sqlHelper;
/*    */   
/*    */   public void addOver(Template template, List<Param> params) {
/* 21 */     this.sqlHelper.insertOrUpdate(template);
/*    */     
/* 23 */     this.sqlHelper.deleteByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("templateId", template.getId()), Param.class);
/*    */     
/* 25 */     Collections.reverse(params);
/* 26 */     for (Param param : params) {
/* 27 */       param.setTemplateId(template.getId());
/* 28 */       this.sqlHelper.insertOrUpdate(param);
/*    */     } 
/*    */   }
/*    */   
/*    */   public List<Param> getParamList(String templateId) {
/* 33 */     return this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("templateId", templateId), Param.class);
/*    */   }
/*    */ 
/*    */   
/*    */   public void del(String id) {
/* 38 */     this.sqlHelper.deleteById(id, Template.class);
/* 39 */     this.sqlHelper.deleteByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("templateId", id), Param.class);
/* 40 */     this.sqlHelper.deleteByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("templateValue", id), Param.class);
/*    */   }
/*    */   
/*    */   public Long getCountByName(String name) {
/* 44 */     return this.sqlHelper.findCountByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("name", name), Template.class);
/*    */   }
/*    */   
/*    */   public Long getCountByNameWithOutId(String name, String id) {
/* 48 */     return this.sqlHelper.findCountByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("name", name).ne("id", id), Template.class);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\service\TemplateService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */