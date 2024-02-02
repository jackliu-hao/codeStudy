/*     */ package com.cym.controller.adminPage;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.util.URLUtil;
/*     */ import com.cym.config.AppFilter;
/*     */ import com.cym.model.Log;
/*     */ import com.cym.service.LogService;
/*     */ import com.cym.service.SettingService;
/*     */ import com.cym.sqlhelper.bean.Page;
/*     */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*     */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*     */ import com.cym.utils.BLogFileTailer;
/*     */ import com.cym.utils.BaseController;
/*     */ import com.cym.utils.JsonResult;
/*     */ import com.cym.utils.SystemTool;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.InputStream;
/*     */ import org.noear.solon.annotation.Controller;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.annotation.Mapping;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.handle.ModelAndView;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ @Controller
/*     */ @Mapping("/adminPage/log")
/*     */ public class LogController
/*     */   extends BaseController
/*     */ {
/*  34 */   Logger logger = LoggerFactory.getLogger(getClass());
/*     */   @Inject
/*     */   SettingService settingService;
/*     */   @Inject
/*     */   LogService logService;
/*     */   @Inject
/*     */   AppFilter appFilter;
/*     */   @Inject
/*     */   BLogFileTailer bLogFileTailer;
/*     */   
/*     */   @Mapping("")
/*     */   public ModelAndView index(ModelAndView modelAndView, Page page) {
/*  46 */     page = this.logService.search(page);
/*  47 */     modelAndView.put("page", page);
/*     */     
/*  49 */     modelAndView.put("isLinux", SystemTool.isLinux());
/*  50 */     modelAndView.view("/adminPage/log/index.html");
/*  51 */     return modelAndView;
/*     */   }
/*     */   
/*     */   @Mapping("addOver")
/*     */   public JsonResult addOver(Log log) {
/*  56 */     if (this.logService.hasDir(log.getPath(), log.getId())) {
/*  57 */       return renderError(this.m.get("logStr.sameDir"));
/*     */     }
/*     */     
/*  60 */     if (FileUtil.isDirectory(log.getPath())) {
/*  61 */       return renderError(this.m.get("logStr.notFile"));
/*     */     }
/*     */     
/*  64 */     this.sqlHelper.insertOrUpdate(log);
/*  65 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("detail")
/*     */   public JsonResult detail(String id) {
/*  70 */     return renderSuccess(this.sqlHelper.findById(id, Log.class));
/*     */   }
/*     */   
/*     */   @Mapping("del")
/*     */   public JsonResult del(String id) {
/*  75 */     this.sqlHelper.deleteById(id, Log.class);
/*  76 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("clean")
/*     */   public JsonResult clean() {
/*  81 */     this.sqlHelper.deleteByQuery((ConditionWrapper)new ConditionAndWrapper(), Log.class);
/*  82 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("tail")
/*     */   public ModelAndView tail(ModelAndView modelAndView, String id, String protocol) {
/*  87 */     modelAndView.put("id", id);
/*  88 */     modelAndView.view("/adminPage/log/tail.html");
/*  89 */     return modelAndView;
/*     */   }
/*     */   
/*     */   @Mapping("down")
/*     */   public void down(ModelAndView modelAndView, String id) throws FileNotFoundException {
/*  94 */     Log log = (Log)this.sqlHelper.findById(id, Log.class);
/*  95 */     File file = new File(log.getPath());
/*     */     
/*  97 */     Context.current().contentType("application/octet-stream");
/*  98 */     String headerKey = "Content-Disposition";
/*  99 */     String headerValue = "attachment; filename=" + URLUtil.encode(file.getName());
/* 100 */     Context.current().header(headerKey, headerValue);
/*     */     
/* 102 */     InputStream inputStream = new FileInputStream(file);
/* 103 */     Context.current().output(inputStream);
/*     */   }
/*     */   
/*     */   @Mapping("tailCmd")
/*     */   public JsonResult tailCmd(String id, String guid) throws Exception {
/* 108 */     Log log = (Log)this.sqlHelper.findById(id, Log.class);
/* 109 */     if (!FileUtil.exist(log.getPath())) {
/* 110 */       return renderSuccess("");
/*     */     }
/*     */     
/* 113 */     String rs = this.bLogFileTailer.run(guid, log.getPath());
/* 114 */     return renderSuccess(rs);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\LogController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */