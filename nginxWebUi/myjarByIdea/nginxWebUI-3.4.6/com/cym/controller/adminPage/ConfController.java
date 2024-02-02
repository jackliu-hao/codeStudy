package com.cym.controller.adminPage;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cym.config.VersionConfig;
import com.cym.ext.ConfExt;
import com.cym.ext.ConfFile;
import com.cym.model.Admin;
import com.cym.service.ConfService;
import com.cym.service.ServerService;
import com.cym.service.SettingService;
import com.cym.service.UpstreamService;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import com.cym.utils.NginxUtils;
import com.cym.utils.SystemTool;
import com.cym.utils.ToolUtils;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@Mapping("/adminPage/conf")
public class ConfController extends BaseController {
   Logger logger = LoggerFactory.getLogger(this.getClass());
   @Inject
   UpstreamService upstreamService;
   @Inject
   SettingService settingService;
   @Inject
   ServerService serverService;
   @Inject
   ConfService confService;
   @Inject
   MainController mainController;
   @Inject
   VersionConfig versionConfig;

   @Mapping("")
   public ModelAndView index(ModelAndView modelAndView) {
      String nginxPath = this.settingService.get("nginxPath");
      modelAndView.put("nginxPath", nginxPath);
      String nginxExe = this.settingService.get("nginxExe");
      modelAndView.put("nginxExe", nginxExe);
      String nginxDir = this.settingService.get("nginxDir");
      modelAndView.put("nginxDir", nginxDir);
      String decompose = this.settingService.get("decompose");
      modelAndView.put("decompose", decompose);
      modelAndView.put("tmp", this.homeConfig.home + "temp/nginx.conf");
      modelAndView.view("/adminPage/conf/index.html");
      return modelAndView;
   }

   @Mapping("nginxStatus")
   public JsonResult nginxStatus() {
      return NginxUtils.isRun() ? this.renderSuccess(this.m.get("confStr.nginxStatus") + "：<span class='green'>" + this.m.get("confStr.running") + "</span>") : this.renderSuccess(this.m.get("confStr.nginxStatus") + "：<span class='red'>" + this.m.get("confStr.stopped") + "</span>");
   }

   @Mapping("replace")
   public JsonResult replace(String json, String adminName) {
      if (StrUtil.isEmpty(json)) {
         json = this.getReplaceJson();
      }

      JSONObject jsonObject = JSONUtil.parseObj(json);
      String nginxPath = jsonObject.getStr("nginxPath");
      String nginxContent = Base64.decodeStr(jsonObject.getStr("nginxContent"), (Charset)CharsetUtil.CHARSET_UTF_8);
      nginxContent = URLDecoder.decode(nginxContent, CharsetUtil.CHARSET_UTF_8).replace("<wave>", "~");
      List<String> subContent = jsonObject.getJSONArray("subContent").toList(String.class);

      for(int i = 0; i < subContent.size(); ++i) {
         String content = Base64.decodeStr((CharSequence)subContent.get(i), CharsetUtil.CHARSET_UTF_8);
         content = URLDecoder.decode(content, CharsetUtil.CHARSET_UTF_8).replace("<wave>", "~");
         subContent.set(i, content);
      }

      List<String> subName = jsonObject.getJSONArray("subName").toList(String.class);
      if (nginxPath == null) {
         nginxPath = this.settingService.get("nginxPath");
      }

      if (FileUtil.isDirectory(nginxPath)) {
         return this.renderError(this.m.get("confStr.error2"));
      } else {
         try {
            if (StrUtil.isEmpty(adminName)) {
               Admin admin = this.getAdmin();
               adminName = admin.getName();
            }

            this.confService.replace(nginxPath, nginxContent, subContent, subName, true, adminName);
            return this.renderSuccess(this.m.get("confStr.replaceSuccess"));
         } catch (Exception var9) {
            this.logger.error((String)var9.getMessage(), (Throwable)var9);
            return this.renderError(this.m.get("confStr.error3") + ":" + var9.getMessage());
         }
      }
   }

   public String getReplaceJson() {
      String decompose = this.settingService.get("decompose");
      ConfExt confExt = this.confService.buildConf(StrUtil.isNotEmpty(decompose) && decompose.equals("true"), false);
      JSONObject jsonObject = new JSONObject();
      jsonObject.set("nginxContent", Base64.encode((CharSequence)URLEncodeUtil.encode(confExt.getConf(), CharsetUtil.CHARSET_UTF_8)));
      jsonObject.set("subContent", new JSONArray());
      jsonObject.set("subName", new JSONArray());
      Iterator var4 = confExt.getFileList().iterator();

      while(var4.hasNext()) {
         ConfFile confFile = (ConfFile)var4.next();
         jsonObject.getJSONArray("subContent").add(Base64.encode((CharSequence)URLEncodeUtil.encode(confFile.getConf(), CharsetUtil.CHARSET_UTF_8)));
         jsonObject.getJSONArray("subName").add(confFile.getName());
      }

      return jsonObject.toStringPretty();
   }

   @Mapping("checkBase")
   public JsonResult checkBase() {
      String nginxExe = this.settingService.get("nginxExe");
      String nginxDir = this.settingService.get("nginxDir");
      String rs = null;
      String cmd = null;
      FileUtil.del(this.homeConfig.home + "temp");
      String fileTemp = this.homeConfig.home + "temp/nginx.conf";

      try {
         ConfExt confExt = this.confService.buildConf(false, true);
         FileUtil.writeString(confExt.getConf(), fileTemp, CharsetUtil.CHARSET_UTF_8);
         ClassPathResource resource = new ClassPathResource("mime.types");
         FileUtil.writeFromStream(resource.getStream(), this.homeConfig.home + "temp/mime.types");
         cmd = nginxExe + " -t -c " + fileTemp;
         if (StrUtil.isNotEmpty(nginxDir)) {
            cmd = cmd + " -p " + nginxDir;
         }

         rs = RuntimeUtil.execForStr(cmd);
      } catch (Exception var8) {
         this.logger.error((String)var8.getMessage(), (Throwable)var8);
         rs = var8.getMessage().replace("\n", "<br>");
      }

      cmd = "<span class='blue'>" + cmd + "</span>";
      return rs.contains("successful") ? this.renderSuccess(cmd + "<br>" + this.m.get("confStr.verifySuccess") + "<br>" + rs.replace("\n", "<br>")) : this.renderError(cmd + "<br>" + this.m.get("confStr.verifyFail") + "<br>" + rs.replace("\n", "<br>"));
   }

   @Mapping("check")
   public JsonResult check(String nginxPath, String nginxExe, String nginxDir, String json) {
      if (nginxExe == null) {
         nginxExe = this.settingService.get("nginxExe");
      }

      if (nginxDir == null) {
         nginxDir = this.settingService.get("nginxDir");
      }

      JSONObject jsonObject = JSONUtil.parseObj(json);
      String nginxContent = Base64.decodeStr(jsonObject.getStr("nginxContent"), (Charset)CharsetUtil.CHARSET_UTF_8);
      nginxContent = URLDecoder.decode(nginxContent, CharsetUtil.CHARSET_UTF_8).replace("<wave>", "~");
      List<String> subContent = jsonObject.getJSONArray("subContent").toList(String.class);

      String tempDir;
      for(int i = 0; i < subContent.size(); ++i) {
         tempDir = Base64.decodeStr((CharSequence)subContent.get(i), CharsetUtil.CHARSET_UTF_8);
         tempDir = URLDecoder.decode(tempDir, CharsetUtil.CHARSET_UTF_8).replace("<wave>", "~");
         subContent.set(i, tempDir);
      }

      String confDir = ToolUtils.handlePath((new File(nginxPath)).getParent()) + "/conf.d/";
      tempDir = this.homeConfig.home + "temp/conf.d/";
      List<String> subName = jsonObject.getJSONArray("subName").toList(String.class);

      String rs;
      for(Iterator var11 = subName.iterator(); var11.hasNext(); nginxContent = nginxContent.replace("include " + confDir + rs, "include " + tempDir + rs)) {
         rs = (String)var11.next();
      }

      FileUtil.del(this.homeConfig.home + "temp");
      String fileTemp = this.homeConfig.home + "temp/nginx.conf";
      this.confService.replace(fileTemp, nginxContent, subContent, subName, false, (String)null);
      rs = null;
      String cmd = null;

      try {
         ClassPathResource resource = new ClassPathResource("mime.types");
         FileUtil.writeFromStream(resource.getStream(), this.homeConfig.home + "temp/mime.types");
         cmd = nginxExe + " -t -c " + fileTemp;
         if (StrUtil.isNotEmpty(nginxDir)) {
            cmd = cmd + " -p " + nginxDir;
         }

         rs = RuntimeUtil.execForStr(cmd);
      } catch (Exception var15) {
         this.logger.error((String)var15.getMessage(), (Throwable)var15);
         rs = var15.getMessage().replace("\n", "<br>");
      }

      cmd = "<span class='blue'>" + cmd + "</span>";
      return rs.contains("successful") ? this.renderSuccess(cmd + "<br>" + this.m.get("confStr.verifySuccess") + "<br>" + rs.replace("\n", "<br>")) : this.renderSuccess(cmd + "<br>" + this.m.get("confStr.verifyFail") + "<br>" + rs.replace("\n", "<br>"));
   }

   @Mapping("saveCmd")
   public JsonResult saveCmd(String nginxPath, String nginxExe, String nginxDir) {
      nginxPath = ToolUtils.handlePath(nginxPath);
      this.settingService.set("nginxPath", nginxPath);
      nginxExe = ToolUtils.handlePath(nginxExe);
      this.settingService.set("nginxExe", nginxExe);
      nginxDir = ToolUtils.handlePath(nginxDir);
      this.settingService.set("nginxDir", nginxDir);
      return this.renderSuccess();
   }

   @Mapping("reload")
   public synchronized JsonResult reload(String nginxPath, String nginxExe, String nginxDir) {
      if (nginxPath == null) {
         nginxPath = this.settingService.get("nginxPath");
      }

      if (nginxExe == null) {
         nginxExe = this.settingService.get("nginxExe");
      }

      if (nginxDir == null) {
         nginxDir = this.settingService.get("nginxDir");
      }

      try {
         String cmd = nginxExe + " -s reload -c " + nginxPath;
         if (StrUtil.isNotEmpty(nginxDir)) {
            cmd = cmd + " -p " + nginxDir;
         }

         String rs = RuntimeUtil.execForStr(cmd);
         cmd = "<span class='blue'>" + cmd + "</span>";
         if (!StrUtil.isEmpty(rs) && !rs.contains("signal process started")) {
            if (rs.contains("The system cannot find the file specified") || rs.contains("nginx.pid") || rs.contains("PID")) {
               rs = rs + this.m.get("confStr.mayNotRun");
            }

            return this.renderSuccess(cmd + "<br>" + this.m.get("confStr.reloadFail") + "<br>" + rs.replace("\n", "<br>"));
         } else {
            return this.renderSuccess(cmd + "<br>" + this.m.get("confStr.reloadSuccess") + "<br>" + rs.replace("\n", "<br>"));
         }
      } catch (Exception var6) {
         this.logger.error((String)var6.getMessage(), (Throwable)var6);
         return this.renderSuccess(this.m.get("confStr.reloadFail") + "<br>" + var6.getMessage().replace("\n", "<br>"));
      }
   }

   @Mapping("runCmd")
   public JsonResult runCmd(String cmd, String type) {
      if (StrUtil.isNotEmpty(type)) {
         this.settingService.set(type, cmd);
      }

      try {
         String rs = "";
         if (SystemTool.isWindows()) {
            RuntimeUtil.exec("cmd /c start " + cmd);
         } else {
            rs = RuntimeUtil.execForStr("/bin/sh", "-c", cmd);
         }

         cmd = "<span class='blue'>" + cmd + "</span>";
         return !StrUtil.isEmpty(rs) && !rs.contains("已终止进程") && !rs.contains("signal process started") && !rs.toLowerCase().contains("terminated process") && !rs.toLowerCase().contains("starting") && !rs.toLowerCase().contains("stopping") ? this.renderSuccess(cmd + "<br>" + this.m.get("confStr.runFail") + "<br>" + rs.replace("\n", "<br>")) : this.renderSuccess(cmd + "<br>" + this.m.get("confStr.runSuccess") + "<br>" + rs.replace("\n", "<br>"));
      } catch (Exception var4) {
         this.logger.error((String)var4.getMessage(), (Throwable)var4);
         return this.renderSuccess(this.m.get("confStr.runFail") + "<br>" + var4.getMessage().replace("\n", "<br>"));
      }
   }

   @Mapping("getLastCmd")
   public JsonResult getLastCmd(String type) {
      return this.renderSuccess(this.settingService.get(type));
   }

   @Mapping("loadConf")
   public JsonResult loadConf() {
      String decompose = this.settingService.get("decompose");
      ConfExt confExt = this.confService.buildConf(StrUtil.isNotEmpty(decompose) && decompose.equals("true"), false);
      return this.renderSuccess(confExt);
   }

   @Mapping("loadOrg")
   public JsonResult loadOrg(String nginxPath) {
      String decompose = this.settingService.get("decompose");
      ConfExt confExt = this.confService.buildConf(StrUtil.isNotEmpty(decompose) && decompose.equals("true"), false);
      if (StrUtil.isNotEmpty(nginxPath) && FileUtil.exist(nginxPath) && FileUtil.isFile(nginxPath)) {
         String orgStr = FileUtil.readString(nginxPath, StandardCharsets.UTF_8);
         confExt.setConf(orgStr);
         Iterator var5 = confExt.getFileList().iterator();

         while(var5.hasNext()) {
            ConfFile confFile = (ConfFile)var5.next();
            confFile.setConf("");
            String filePath = (new File(nginxPath)).getParent() + "/conf.d/" + confFile.getName();
            if (FileUtil.exist(filePath)) {
               confFile.setConf(FileUtil.readString(filePath, StandardCharsets.UTF_8));
            }
         }

         return this.renderSuccess(confExt);
      } else {
         return FileUtil.isDirectory(nginxPath) ? this.renderError(this.m.get("confStr.error2")) : this.renderError(this.m.get("confStr.notExist"));
      }
   }

   @Mapping("decompose")
   public JsonResult decompose(String decompose) {
      this.settingService.set("decompose", decompose);
      return this.renderSuccess();
   }

   @Mapping("update")
   public JsonResult update() {
      this.versionConfig.checkVersion();
      if (Integer.parseInt(this.versionConfig.currentVersion.replace(".", "").replace("v", "")) < Integer.parseInt(this.versionConfig.newVersion.getVersion().replace(".", "").replace("v", ""))) {
         this.mainController.autoUpdate(this.versionConfig.newVersion.getUrl());
         return this.renderSuccess(this.m.get("confStr.updateSuccess"));
      } else {
         return this.renderSuccess(this.m.get("confStr.noNeedUpdate"));
      }
   }

   @Mapping("getKey")
   public JsonResult getKey(String key) {
      return this.renderSuccess(this.settingService.get(key));
   }

   @Mapping("setKey")
   public JsonResult setKey(String key, String val) {
      this.settingService.set(key, val);
      return this.renderSuccess();
   }
}
