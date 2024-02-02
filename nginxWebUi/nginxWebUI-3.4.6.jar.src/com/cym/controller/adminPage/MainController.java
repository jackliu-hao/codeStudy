/*     */ package com.cym.controller.adminPage;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.http.HttpUtil;
/*     */ import com.cym.service.SettingService;
/*     */ import com.cym.utils.BaseController;
/*     */ import com.cym.utils.JarUtil;
/*     */ import com.cym.utils.JsonResult;
/*     */ import com.cym.utils.UpdateUtils;
/*     */ import java.io.File;
/*     */ import org.noear.solon.annotation.Controller;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.annotation.Mapping;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.handle.ModelAndView;
/*     */ import org.noear.solon.core.handle.UploadedFile;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Mapping("")
/*     */ @Controller
/*     */ public class MainController
/*     */   extends BaseController
/*     */ {
/*  33 */   Logger logger = LoggerFactory.getLogger(getClass());
/*     */   
/*     */   @Inject
/*     */   UpdateUtils updateUtils;
/*     */   @Inject
/*     */   SettingService settingService;
/*  39 */   private static final Logger LOG = LoggerFactory.getLogger(MainController.class);
/*     */   
/*     */   @Mapping("")
/*     */   public ModelAndView index(ModelAndView modelAndView, String keywords) {
/*  43 */     modelAndView.view("/adminPage/index.html");
/*  44 */     return modelAndView;
/*     */   }
/*     */   
/*     */   @Mapping("doc.html")
/*     */   public void doc(Context context) {
/*  49 */     context.redirect("doc/api.html");
/*     */   }
/*     */ 
/*     */   
/*     */   @Mapping("/adminPage/main/upload")
/*     */   public JsonResult upload(Context context, UploadedFile file) {
/*     */     try {
/*  56 */       File temp = new File(FileUtil.getTmpDir() + "/" + file.name);
/*  57 */       file.transferTo(temp);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  79 */       return renderSuccess(temp.getPath().replace("\\", "/"));
/*  80 */     } catch (IllegalStateException|java.io.IOException e) {
/*  81 */       this.logger.error(e.getMessage(), e);
/*     */ 
/*     */       
/*  84 */       return renderError();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("/adminPage/main/autoUpdate")
/*     */   public JsonResult autoUpdate(String url) {
/*  93 */     File jar = JarUtil.getCurrentFile();
/*  94 */     String path = jar.getParent() + "/nginxWebUI.jar.update";
/*  95 */     LOG.info("download:" + path);
/*  96 */     HttpUtil.downloadFile(url, path);
/*  97 */     this.updateUtils.run(path);
/*  98 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("/adminPage/main/changeLang")
/*     */   public JsonResult changeLang() {
/* 103 */     if (this.settingService.get("lang") != null && this.settingService.get("lang").equals("en_US")) {
/* 104 */       this.settingService.set("lang", "");
/*     */     } else {
/* 106 */       this.settingService.set("lang", "en_US");
/*     */     } 
/*     */     
/* 109 */     return renderSuccess();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\MainController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */