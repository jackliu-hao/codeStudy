package com.cym.controller.adminPage;

import cn.hutool.core.util.StrUtil;
import com.cym.ext.MonitorInfo;
import com.cym.ext.NetworkInfo;
import com.cym.service.MonitorService;
import com.cym.service.SettingService;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import com.cym.utils.NetWorkUtil;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mapping("/adminPage/monitor")
@Controller
public class MonitorController extends BaseController {
   @Inject
   MonitorService monitorService;
   @Inject
   SettingService settingService;
   Logger logger = LoggerFactory.getLogger(this.getClass());

   @Mapping("")
   public ModelAndView index(ModelAndView modelAndView) {
      modelAndView.put("list", this.monitorService.getDiskInfo());
      String nginxPath = this.settingService.get("nginxPath");
      String nginxExe = this.settingService.get("nginxExe");
      String nginxDir = this.settingService.get("nginxDir");
      modelAndView.put("nginxDir", nginxDir);
      modelAndView.put("nginxExe", nginxExe);
      modelAndView.put("nginxPath", nginxPath);
      Boolean isInit = StrUtil.isNotEmpty(nginxExe);
      modelAndView.put("isInit", isInit.toString());
      modelAndView.view("/adminPage/monitor/index.html");
      return modelAndView;
   }

   @Mapping("check")
   public JsonResult check() {
      MonitorInfo monitorInfo = this.monitorService.getMonitorInfoOshi();
      return this.renderSuccess(monitorInfo);
   }

   @Mapping("network")
   public JsonResult network() {
      NetworkInfo networkInfo = NetWorkUtil.getNetworkDownUp();
      return this.renderSuccess(networkInfo);
   }

   @Mapping("addNginxGiudeOver")
   public JsonResult addNginxGiudeOver(String nginxDir, String nginxExe) {
      this.settingService.set("nginxDir", nginxDir);
      this.settingService.set("nginxExe", nginxExe);
      return this.renderSuccess();
   }
}
