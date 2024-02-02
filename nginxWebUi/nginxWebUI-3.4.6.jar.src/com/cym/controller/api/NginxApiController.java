/*     */ package com.cym.controller.api;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.http.HtmlUtil;
/*     */ import com.cym.controller.adminPage.ConfController;
/*     */ import com.cym.service.AdminService;
/*     */ import com.cym.service.SettingService;
/*     */ import com.cym.utils.BaseController;
/*     */ import com.cym.utils.JsonResult;
/*     */ import com.cym.utils.NginxUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.noear.solon.annotation.Controller;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.annotation.Mapping;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Mapping("/api/nginx")
/*     */ @Controller
/*     */ public class NginxApiController
/*     */   extends BaseController
/*     */ {
/*     */   @Inject
/*     */   ConfController confController;
/*     */   @Inject
/*     */   AdminService adminService;
/*     */   @Inject
/*     */   SettingService settingService;
/*     */   
/*     */   @Mapping("nginxStatus")
/*     */   public JsonResult<?> nginxStatus() {
/*  38 */     if (NginxUtils.isRun()) {
/*  39 */       return renderSuccess(this.m.get("confStr.running"));
/*     */     }
/*  41 */     return renderError(this.m.get("confStr.stopped"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("replace")
/*     */   public JsonResult<?> replace() {
/*  51 */     JsonResult jsonResult = this.confController.replace(this.confController.getReplaceJson(), null);
/*  52 */     if (jsonResult.isSuccess()) {
/*  53 */       return renderSuccess("替换成功");
/*     */     }
/*  55 */     return renderError("替换失败");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("check")
/*     */   public JsonResult<?> checkBase() {
/*  65 */     JsonResult jsonResult = this.confController.checkBase();
/*  66 */     if (jsonResult.isSuccess()) {
/*  67 */       return renderSuccess("效验成功");
/*     */     }
/*  69 */     return renderError("效验失败");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("reload")
/*     */   public synchronized JsonResult<?> reload() {
/*  79 */     JsonResult jsonResult = this.confController.reload(null, null, null);
/*  80 */     if (jsonResult.isSuccess()) {
/*  81 */       return renderSuccess("重载成功");
/*     */     }
/*  83 */     return renderError("重载失败");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("getNginxStartCmd")
/*     */   public JsonResult<List<String>> getNginxStartCmd() {
/*  93 */     String nginxExe = StrUtil.nullToEmpty(this.settingService.get("nginxExe"));
/*  94 */     String nginxPath = StrUtil.nullToEmpty(this.settingService.get("nginxPath"));
/*  95 */     String nginxDir = StrUtil.nullToEmpty(this.settingService.get("nginxDir"));
/*     */     
/*  97 */     if (StrUtil.isNotEmpty(nginxDir)) {
/*  98 */       nginxDir = " -p " + nginxDir;
/*     */     }
/*     */     
/* 101 */     List<String> list = new ArrayList<>();
/* 102 */     list.add(nginxExe + " -c " + nginxPath + nginxDir);
/* 103 */     list.add("systemctl start nginx");
/* 104 */     list.add("service nginx start");
/*     */     
/* 106 */     return renderSuccess(list);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("getNginxStopCmd")
/*     */   public JsonResult<List<String>> getNginxStopCmd() {
/* 115 */     String nginxExe = StrUtil.nullToEmpty(this.settingService.get("nginxExe"));
/* 116 */     String nginxDir = StrUtil.nullToEmpty(this.settingService.get("nginxDir"));
/*     */     
/* 118 */     if (StrUtil.isNotEmpty(nginxDir)) {
/* 119 */       nginxDir = " -p " + nginxDir;
/*     */     }
/*     */     
/* 122 */     List<String> list = new ArrayList<>();
/* 123 */     list.add(nginxExe + "  -s stop " + nginxDir);
/* 124 */     list.add("pkill nginx");
/* 125 */     list.add("taskkill /f /im nginx.exe");
/* 126 */     list.add("systemctl stop nginx");
/* 127 */     list.add("service nginx stop");
/*     */     
/* 129 */     return renderSuccess(list);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("runNginxCmd")
/*     */   public JsonResult<?> runNginxCmd(String cmd) {
/* 141 */     JsonResult<?> jsonResult = this.confController.runCmd(cmd, null);
/* 142 */     jsonResult.setObj(HtmlUtil.cleanHtmlTag(jsonResult.getObj().toString()));
/* 143 */     return jsonResult;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\api\NginxApiController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */