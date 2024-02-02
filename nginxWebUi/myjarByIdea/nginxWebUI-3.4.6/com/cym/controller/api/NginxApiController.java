package com.cym.controller.api;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import com.cym.controller.adminPage.ConfController;
import com.cym.service.AdminService;
import com.cym.service.SettingService;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import com.cym.utils.NginxUtils;
import java.util.ArrayList;
import java.util.List;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

@Mapping("/api/nginx")
@Controller
public class NginxApiController extends BaseController {
   @Inject
   ConfController confController;
   @Inject
   AdminService adminService;
   @Inject
   SettingService settingService;

   @Mapping("nginxStatus")
   public JsonResult<?> nginxStatus() {
      return NginxUtils.isRun() ? this.renderSuccess(this.m.get("confStr.running")) : this.renderError(this.m.get("confStr.stopped"));
   }

   @Mapping("replace")
   public JsonResult<?> replace() {
      JsonResult jsonResult = this.confController.replace(this.confController.getReplaceJson(), (String)null);
      return jsonResult.isSuccess() ? this.renderSuccess("替换成功") : this.renderError("替换失败");
   }

   @Mapping("check")
   public JsonResult<?> checkBase() {
      JsonResult jsonResult = this.confController.checkBase();
      return jsonResult.isSuccess() ? this.renderSuccess("效验成功") : this.renderError("效验失败");
   }

   @Mapping("reload")
   public synchronized JsonResult<?> reload() {
      JsonResult jsonResult = this.confController.reload((String)null, (String)null, (String)null);
      return jsonResult.isSuccess() ? this.renderSuccess("重载成功") : this.renderError("重载失败");
   }

   @Mapping("getNginxStartCmd")
   public JsonResult<List<String>> getNginxStartCmd() {
      String nginxExe = StrUtil.nullToEmpty(this.settingService.get("nginxExe"));
      String nginxPath = StrUtil.nullToEmpty(this.settingService.get("nginxPath"));
      String nginxDir = StrUtil.nullToEmpty(this.settingService.get("nginxDir"));
      if (StrUtil.isNotEmpty(nginxDir)) {
         nginxDir = " -p " + nginxDir;
      }

      List<String> list = new ArrayList();
      list.add(nginxExe + " -c " + nginxPath + nginxDir);
      list.add("systemctl start nginx");
      list.add("service nginx start");
      return this.renderSuccess(list);
   }

   @Mapping("getNginxStopCmd")
   public JsonResult<List<String>> getNginxStopCmd() {
      String nginxExe = StrUtil.nullToEmpty(this.settingService.get("nginxExe"));
      String nginxDir = StrUtil.nullToEmpty(this.settingService.get("nginxDir"));
      if (StrUtil.isNotEmpty(nginxDir)) {
         nginxDir = " -p " + nginxDir;
      }

      List<String> list = new ArrayList();
      list.add(nginxExe + "  -s stop " + nginxDir);
      list.add("pkill nginx");
      list.add("taskkill /f /im nginx.exe");
      list.add("systemctl stop nginx");
      list.add("service nginx stop");
      return this.renderSuccess(list);
   }

   @Mapping("runNginxCmd")
   public JsonResult<?> runNginxCmd(String cmd) {
      JsonResult jsonResult = this.confController.runCmd(cmd, (String)null);
      jsonResult.setObj(HtmlUtil.cleanHtmlTag(jsonResult.getObj().toString()));
      return jsonResult;
   }
}
