/*    */ package com.cym.controller.adminPage;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import com.cym.ext.MonitorInfo;
/*    */ import com.cym.ext.NetworkInfo;
/*    */ import com.cym.service.MonitorService;
/*    */ import com.cym.service.SettingService;
/*    */ import com.cym.utils.BaseController;
/*    */ import com.cym.utils.JsonResult;
/*    */ import com.cym.utils.NetWorkUtil;
/*    */ import org.noear.solon.annotation.Controller;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.annotation.Mapping;
/*    */ import org.noear.solon.core.handle.ModelAndView;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ @Mapping("/adminPage/monitor")
/*    */ @Controller
/*    */ public class MonitorController
/*    */   extends BaseController
/*    */ {
/*    */   @Inject
/*    */   MonitorService monitorService;
/*    */   @Inject
/*    */   SettingService settingService;
/* 27 */   Logger logger = LoggerFactory.getLogger(getClass());
/*    */ 
/*    */   
/*    */   @Mapping("")
/*    */   public ModelAndView index(ModelAndView modelAndView) {
/* 32 */     modelAndView.put("list", this.monitorService.getDiskInfo());
/*    */     
/* 34 */     String nginxPath = this.settingService.get("nginxPath");
/* 35 */     String nginxExe = this.settingService.get("nginxExe");
/* 36 */     String nginxDir = this.settingService.get("nginxDir");
/*    */     
/* 38 */     modelAndView.put("nginxDir", nginxDir);
/* 39 */     modelAndView.put("nginxExe", nginxExe);
/* 40 */     modelAndView.put("nginxPath", nginxPath);
/*    */     
/* 42 */     Boolean isInit = Boolean.valueOf(StrUtil.isNotEmpty(nginxExe));
/* 43 */     modelAndView.put("isInit", isInit.toString());
/*    */     
/* 45 */     modelAndView.view("/adminPage/monitor/index.html");
/* 46 */     return modelAndView;
/*    */   }
/*    */ 
/*    */   
/*    */   @Mapping("check")
/*    */   public JsonResult check() {
/* 52 */     MonitorInfo monitorInfo = this.monitorService.getMonitorInfoOshi();
/*    */     
/* 54 */     return renderSuccess(monitorInfo);
/*    */   }
/*    */   
/*    */   @Mapping("network")
/*    */   public JsonResult network() {
/* 59 */     NetworkInfo networkInfo = NetWorkUtil.getNetworkDownUp();
/*    */     
/* 61 */     return renderSuccess(networkInfo);
/*    */   }
/*    */ 
/*    */   
/*    */   @Mapping("addNginxGiudeOver")
/*    */   public JsonResult addNginxGiudeOver(String nginxDir, String nginxExe) {
/* 67 */     this.settingService.set("nginxDir", nginxDir);
/* 68 */     this.settingService.set("nginxExe", nginxExe);
/* 69 */     return renderSuccess();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\MonitorController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */