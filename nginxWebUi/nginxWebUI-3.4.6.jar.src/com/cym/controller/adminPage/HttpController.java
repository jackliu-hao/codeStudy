/*     */ package com.cym.controller.adminPage;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.json.JSONUtil;
/*     */ import com.cym.model.Http;
/*     */ import com.cym.service.HttpService;
/*     */ import com.cym.service.SettingService;
/*     */ import com.cym.utils.BaseController;
/*     */ import com.cym.utils.JsonResult;
/*     */ import com.cym.utils.SnowFlakeUtils;
/*     */ import java.util.List;
/*     */ import org.noear.solon.annotation.Controller;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.annotation.Mapping;
/*     */ import org.noear.solon.core.handle.ModelAndView;
/*     */ 
/*     */ 
/*     */ 
/*     */ @Controller
/*     */ @Mapping("/adminPage/http")
/*     */ public class HttpController
/*     */   extends BaseController
/*     */ {
/*     */   @Inject
/*     */   HttpService httpService;
/*     */   @Inject
/*     */   SettingService settingService;
/*     */   
/*     */   @Mapping("")
/*     */   public ModelAndView index(ModelAndView modelAndView) {
/*  31 */     List<Http> httpList = this.httpService.findAll();
/*     */     
/*  33 */     modelAndView.put("httpList", httpList);
/*  34 */     modelAndView.view("/adminPage/http/index.html");
/*  35 */     return modelAndView;
/*     */   }
/*     */   
/*     */   @Mapping("addOver")
/*     */   public JsonResult addOver(Http http) {
/*  40 */     if (StrUtil.isEmpty(http.getId())) {
/*  41 */       http.setSeq(SnowFlakeUtils.getId());
/*     */     }
/*  43 */     this.sqlHelper.insertOrUpdate(http);
/*     */     
/*  45 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("addTemplate")
/*     */   public JsonResult addTemplate(String templateId) {
/*  50 */     this.httpService.addTemplate(templateId);
/*     */     
/*  52 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("detail")
/*     */   public JsonResult detail(String id) {
/*  57 */     return renderSuccess(this.sqlHelper.findById(id, Http.class));
/*     */   }
/*     */   
/*     */   @Mapping("del")
/*     */   public JsonResult del(String id) {
/*  62 */     this.sqlHelper.deleteById(id, Http.class);
/*     */     
/*  64 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("addGiudeOver")
/*     */   public JsonResult addGiudeOver(String json, Boolean logStatus, Boolean webSocket) {
/*  69 */     List<Http> https = JSONUtil.toList(JSONUtil.parseArray(json), Http.class);
/*     */     
/*  71 */     if (logStatus.booleanValue()) {
/*     */       
/*  73 */       Http http = new Http();
/*  74 */       http.setName("access_log");
/*  75 */       http.setValue(this.homeConfig.home + "log/access.log");
/*  76 */       http.setUnit("");
/*  77 */       https.add(http);
/*     */       
/*  79 */       http = new Http();
/*  80 */       http.setName("error_log");
/*  81 */       http.setValue(this.homeConfig.home + "log/error.log");
/*  82 */       http.setUnit("");
/*  83 */       https.add(http);
/*     */     } 
/*     */ 
/*     */     
/*  87 */     if (webSocket.booleanValue()) {
/*  88 */       Http http = new Http();
/*  89 */       http.setName("map");
/*  90 */       http.setValue("$http_upgrade $connection_upgrade {\r\n    default upgrade;\r\n    '' close;\r\n}\r\n");
/*     */ 
/*     */       
/*  93 */       http.setUnit("");
/*  94 */       https.add(http);
/*     */     } 
/*     */     
/*  97 */     this.httpService.setAll(https);
/*     */     
/*  99 */     return renderSuccess();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("setOrder")
/*     */   public JsonResult setOrder(String id, Integer count) {
/* 106 */     this.httpService.setSeq(id, count);
/* 107 */     return renderSuccess();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\HttpController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */