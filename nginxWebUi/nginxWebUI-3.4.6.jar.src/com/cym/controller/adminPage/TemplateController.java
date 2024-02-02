/*    */ package com.cym.controller.adminPage;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.json.JSONUtil;
/*    */ import com.cym.ext.TemplateExt;
/*    */ import com.cym.model.Param;
/*    */ import com.cym.model.Template;
/*    */ import com.cym.service.TemplateService;
/*    */ import com.cym.utils.BaseController;
/*    */ import com.cym.utils.JsonResult;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.noear.solon.annotation.Controller;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.annotation.Mapping;
/*    */ import org.noear.solon.core.handle.ModelAndView;
/*    */ 
/*    */ 
/*    */ @Controller
/*    */ @Mapping("/adminPage/template")
/*    */ public class TemplateController
/*    */   extends BaseController
/*    */ {
/*    */   @Inject
/*    */   TemplateService templateService;
/*    */   
/*    */   @Mapping("")
/*    */   public ModelAndView index(ModelAndView modelAndView) {
/* 29 */     List<Template> templateList = this.sqlHelper.findAll(Template.class);
/*    */     
/* 31 */     List<TemplateExt> extList = new ArrayList<>();
/* 32 */     for (Template template : templateList) {
/* 33 */       TemplateExt templateExt = new TemplateExt();
/* 34 */       templateExt.setTemplate(template);
/*    */       
/* 36 */       templateExt.setParamList(this.templateService.getParamList(template.getId()));
/* 37 */       templateExt.setCount(Integer.valueOf(templateExt.getParamList().size()));
/*    */       
/* 39 */       extList.add(templateExt);
/*    */     } 
/*    */     
/* 42 */     modelAndView.put("templateList", extList);
/* 43 */     modelAndView.view("/adminPage/template/index.html");
/* 44 */     return modelAndView;
/*    */   }
/*    */ 
/*    */   
/*    */   @Mapping("addOver")
/*    */   public JsonResult addOver(Template template, String paramJson) {
/* 50 */     if (StrUtil.isEmpty(template.getId())) {
/* 51 */       Long count = this.templateService.getCountByName(template.getName());
/* 52 */       if (count.longValue() > 0L) {
/* 53 */         return renderError(this.m.get("templateStr.sameName"));
/*    */       }
/*    */     } else {
/* 56 */       Long count = this.templateService.getCountByNameWithOutId(template.getName(), template.getId());
/* 57 */       if (count.longValue() > 0L) {
/* 58 */         return renderError(this.m.get("templateStr.sameName"));
/*    */       }
/*    */     } 
/*    */     
/* 62 */     List<Param> params = JSONUtil.toList(JSONUtil.parseArray(paramJson), Param.class);
/*    */     
/* 64 */     this.templateService.addOver(template, params);
/*    */     
/* 66 */     return renderSuccess();
/*    */   }
/*    */   
/*    */   @Mapping("detail")
/*    */   public JsonResult detail(String id) {
/* 71 */     Template template = (Template)this.sqlHelper.findById(id, Template.class);
/* 72 */     TemplateExt templateExt = new TemplateExt();
/* 73 */     templateExt.setTemplate(template);
/*    */     
/* 75 */     templateExt.setParamList(this.templateService.getParamList(template.getId()));
/* 76 */     templateExt.setCount(Integer.valueOf(templateExt.getParamList().size()));
/*    */     
/* 78 */     return renderSuccess(templateExt);
/*    */   }
/*    */ 
/*    */   
/*    */   @Mapping("del")
/*    */   public JsonResult del(String id) {
/* 84 */     this.templateService.del(id);
/* 85 */     return renderSuccess();
/*    */   }
/*    */ 
/*    */   
/*    */   @Mapping("getTemplate")
/*    */   public JsonResult getTemplate() {
/* 91 */     return renderSuccess(this.sqlHelper.findAll(Template.class));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\TemplateController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */