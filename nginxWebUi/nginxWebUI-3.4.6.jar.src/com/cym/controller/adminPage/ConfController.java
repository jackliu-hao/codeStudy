/*     */ package com.cym.controller.adminPage;
/*     */ 
/*     */ import cn.hutool.core.codec.Base64;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.resource.ClassPathResource;
/*     */ import cn.hutool.core.net.URLDecoder;
/*     */ import cn.hutool.core.net.URLEncodeUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.RuntimeUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.json.JSONArray;
/*     */ import cn.hutool.json.JSONObject;
/*     */ import cn.hutool.json.JSONUtil;
/*     */ import com.cym.config.VersionConfig;
/*     */ import com.cym.ext.ConfExt;
/*     */ import com.cym.ext.ConfFile;
/*     */ import com.cym.model.Admin;
/*     */ import com.cym.service.ConfService;
/*     */ import com.cym.service.ServerService;
/*     */ import com.cym.service.SettingService;
/*     */ import com.cym.service.UpstreamService;
/*     */ import com.cym.utils.BaseController;
/*     */ import com.cym.utils.JsonResult;
/*     */ import com.cym.utils.NginxUtils;
/*     */ import com.cym.utils.SystemTool;
/*     */ import com.cym.utils.ToolUtils;
/*     */ import java.io.File;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.List;
/*     */ import org.noear.solon.annotation.Controller;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.annotation.Mapping;
/*     */ import org.noear.solon.core.handle.ModelAndView;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Controller
/*     */ @Mapping("/adminPage/conf")
/*     */ public class ConfController
/*     */   extends BaseController
/*     */ {
/*  45 */   Logger logger = LoggerFactory.getLogger(getClass());
/*     */   
/*     */   @Inject
/*     */   UpstreamService upstreamService;
/*     */   
/*     */   @Inject
/*     */   SettingService settingService;
/*     */   @Inject
/*     */   ServerService serverService;
/*     */   @Inject
/*     */   ConfService confService;
/*     */   @Inject
/*     */   MainController mainController;
/*     */   @Inject
/*     */   VersionConfig versionConfig;
/*     */   
/*     */   @Mapping("")
/*     */   public ModelAndView index(ModelAndView modelAndView) {
/*  63 */     String nginxPath = this.settingService.get("nginxPath");
/*  64 */     modelAndView.put("nginxPath", nginxPath);
/*     */     
/*  66 */     String nginxExe = this.settingService.get("nginxExe");
/*  67 */     modelAndView.put("nginxExe", nginxExe);
/*     */     
/*  69 */     String nginxDir = this.settingService.get("nginxDir");
/*  70 */     modelAndView.put("nginxDir", nginxDir);
/*     */     
/*  72 */     String decompose = this.settingService.get("decompose");
/*  73 */     modelAndView.put("decompose", decompose);
/*     */     
/*  75 */     modelAndView.put("tmp", this.homeConfig.home + "temp/nginx.conf");
/*     */     
/*  77 */     modelAndView.view("/adminPage/conf/index.html");
/*  78 */     return modelAndView;
/*     */   }
/*     */   
/*     */   @Mapping("nginxStatus")
/*     */   public JsonResult nginxStatus() {
/*  83 */     if (NginxUtils.isRun()) {
/*  84 */       return renderSuccess(this.m.get("confStr.nginxStatus") + "：<span class='green'>" + this.m.get("confStr.running") + "</span>");
/*     */     }
/*  86 */     return renderSuccess(this.m.get("confStr.nginxStatus") + "：<span class='red'>" + this.m.get("confStr.stopped") + "</span>");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("replace")
/*     */   public JsonResult replace(String json, String adminName) {
/*  94 */     if (StrUtil.isEmpty(json)) {
/*  95 */       json = getReplaceJson();
/*     */     }
/*     */     
/*  98 */     JSONObject jsonObject = JSONUtil.parseObj(json);
/*     */     
/* 100 */     String nginxPath = jsonObject.getStr("nginxPath");
/* 101 */     String nginxContent = Base64.decodeStr(jsonObject.getStr("nginxContent"), CharsetUtil.CHARSET_UTF_8);
/* 102 */     nginxContent = URLDecoder.decode(nginxContent, CharsetUtil.CHARSET_UTF_8).replace("<wave>", "~");
/*     */     
/* 104 */     List<String> subContent = jsonObject.getJSONArray("subContent").toList(String.class);
/* 105 */     for (int i = 0; i < subContent.size(); i++) {
/* 106 */       String content = Base64.decodeStr(subContent.get(i), CharsetUtil.CHARSET_UTF_8);
/* 107 */       content = URLDecoder.decode(content, CharsetUtil.CHARSET_UTF_8).replace("<wave>", "~");
/* 108 */       subContent.set(i, content);
/*     */     } 
/* 110 */     List<String> subName = jsonObject.getJSONArray("subName").toList(String.class);
/*     */     
/* 112 */     if (nginxPath == null) {
/* 113 */       nginxPath = this.settingService.get("nginxPath");
/*     */     }
/*     */     
/* 116 */     if (FileUtil.isDirectory(nginxPath))
/*     */     {
/* 118 */       return renderError(this.m.get("confStr.error2"));
/*     */     }
/*     */     
/*     */     try {
/* 122 */       if (StrUtil.isEmpty(adminName)) {
/* 123 */         Admin admin = getAdmin();
/* 124 */         adminName = admin.getName();
/*     */       } 
/*     */       
/* 127 */       this.confService.replace(nginxPath, nginxContent, subContent, subName, Boolean.valueOf(true), adminName);
/* 128 */       return renderSuccess(this.m.get("confStr.replaceSuccess"));
/* 129 */     } catch (Exception e) {
/* 130 */       this.logger.error(e.getMessage(), e);
/* 131 */       return renderError(this.m.get("confStr.error3") + ":" + e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getReplaceJson() {
/* 137 */     String decompose = this.settingService.get("decompose");
/* 138 */     ConfExt confExt = this.confService.buildConf(Boolean.valueOf((StrUtil.isNotEmpty(decompose) && decompose.equals("true"))), Boolean.valueOf(false));
/*     */ 
/*     */     
/* 141 */     JSONObject jsonObject = new JSONObject();
/* 142 */     jsonObject.set("nginxContent", Base64.encode(URLEncodeUtil.encode(confExt.getConf(), CharsetUtil.CHARSET_UTF_8)));
/* 143 */     jsonObject.set("subContent", new JSONArray());
/* 144 */     jsonObject.set("subName", new JSONArray());
/* 145 */     for (ConfFile confFile : confExt.getFileList()) {
/* 146 */       jsonObject.getJSONArray("subContent").add(Base64.encode(URLEncodeUtil.encode(confFile.getConf(), CharsetUtil.CHARSET_UTF_8)));
/* 147 */       jsonObject.getJSONArray("subName").add(confFile.getName());
/*     */     } 
/* 149 */     return jsonObject.toStringPretty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("checkBase")
/*     */   public JsonResult checkBase() {
/* 162 */     String nginxExe = this.settingService.get("nginxExe");
/* 163 */     String nginxDir = this.settingService.get("nginxDir");
/*     */     
/* 165 */     String rs = null;
/* 166 */     String cmd = null;
/*     */     
/* 168 */     FileUtil.del(this.homeConfig.home + "temp");
/* 169 */     String fileTemp = this.homeConfig.home + "temp/nginx.conf";
/*     */     
/*     */     try {
/* 172 */       ConfExt confExt = this.confService.buildConf(Boolean.valueOf(false), Boolean.valueOf(true));
/* 173 */       FileUtil.writeString(confExt.getConf(), fileTemp, CharsetUtil.CHARSET_UTF_8);
/*     */       
/* 175 */       ClassPathResource resource = new ClassPathResource("mime.types");
/* 176 */       FileUtil.writeFromStream(resource.getStream(), this.homeConfig.home + "temp/mime.types");
/*     */       
/* 178 */       cmd = nginxExe + " -t -c " + fileTemp;
/* 179 */       if (StrUtil.isNotEmpty(nginxDir)) {
/* 180 */         cmd = cmd + " -p " + nginxDir;
/*     */       }
/* 182 */       rs = RuntimeUtil.execForStr(new String[] { cmd });
/* 183 */     } catch (Exception e) {
/* 184 */       this.logger.error(e.getMessage(), e);
/* 185 */       rs = e.getMessage().replace("\n", "<br>");
/*     */     } 
/*     */     
/* 188 */     cmd = "<span class='blue'>" + cmd + "</span>";
/* 189 */     if (rs.contains("successful")) {
/* 190 */       return renderSuccess(cmd + "<br>" + this.m.get("confStr.verifySuccess") + "<br>" + rs.replace("\n", "<br>"));
/*     */     }
/* 192 */     return renderError(cmd + "<br>" + this.m.get("confStr.verifyFail") + "<br>" + rs.replace("\n", "<br>"));
/*     */   }
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
/*     */   @Mapping("check")
/*     */   public JsonResult check(String nginxPath, String nginxExe, String nginxDir, String json) {
/* 208 */     if (nginxExe == null) {
/* 209 */       nginxExe = this.settingService.get("nginxExe");
/*     */     }
/* 211 */     if (nginxDir == null) {
/* 212 */       nginxDir = this.settingService.get("nginxDir");
/*     */     }
/*     */     
/* 215 */     JSONObject jsonObject = JSONUtil.parseObj(json);
/* 216 */     String nginxContent = Base64.decodeStr(jsonObject.getStr("nginxContent"), CharsetUtil.CHARSET_UTF_8);
/* 217 */     nginxContent = URLDecoder.decode(nginxContent, CharsetUtil.CHARSET_UTF_8).replace("<wave>", "~");
/*     */     
/* 219 */     List<String> subContent = jsonObject.getJSONArray("subContent").toList(String.class);
/* 220 */     for (int i = 0; i < subContent.size(); i++) {
/* 221 */       String content = Base64.decodeStr(subContent.get(i), CharsetUtil.CHARSET_UTF_8);
/* 222 */       content = URLDecoder.decode(content, CharsetUtil.CHARSET_UTF_8).replace("<wave>", "~");
/* 223 */       subContent.set(i, content);
/*     */     } 
/*     */ 
/*     */     
/* 227 */     String confDir = ToolUtils.handlePath((new File(nginxPath)).getParent()) + "/conf.d/";
/* 228 */     String tempDir = this.homeConfig.home + "temp/conf.d/";
/* 229 */     List<String> subName = jsonObject.getJSONArray("subName").toList(String.class);
/* 230 */     for (String sn : subName) {
/* 231 */       nginxContent = nginxContent.replace("include " + confDir + sn, "include " + tempDir + sn);
/*     */     }
/*     */ 
/*     */     
/* 235 */     FileUtil.del(this.homeConfig.home + "temp");
/* 236 */     String fileTemp = this.homeConfig.home + "temp/nginx.conf";
/*     */     
/* 238 */     this.confService.replace(fileTemp, nginxContent, subContent, subName, Boolean.valueOf(false), null);
/*     */     
/* 240 */     String rs = null;
/* 241 */     String cmd = null;
/*     */     
/*     */     try {
/* 244 */       ClassPathResource resource = new ClassPathResource("mime.types");
/* 245 */       FileUtil.writeFromStream(resource.getStream(), this.homeConfig.home + "temp/mime.types");
/*     */       
/* 247 */       cmd = nginxExe + " -t -c " + fileTemp;
/* 248 */       if (StrUtil.isNotEmpty(nginxDir)) {
/* 249 */         cmd = cmd + " -p " + nginxDir;
/*     */       }
/* 251 */       rs = RuntimeUtil.execForStr(new String[] { cmd });
/* 252 */     } catch (Exception e) {
/* 253 */       this.logger.error(e.getMessage(), e);
/* 254 */       rs = e.getMessage().replace("\n", "<br>");
/*     */     } 
/*     */     
/* 257 */     cmd = "<span class='blue'>" + cmd + "</span>";
/* 258 */     if (rs.contains("successful")) {
/* 259 */       return renderSuccess(cmd + "<br>" + this.m.get("confStr.verifySuccess") + "<br>" + rs.replace("\n", "<br>"));
/*     */     }
/* 261 */     return renderSuccess(cmd + "<br>" + this.m.get("confStr.verifyFail") + "<br>" + rs.replace("\n", "<br>"));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("saveCmd")
/*     */   public JsonResult saveCmd(String nginxPath, String nginxExe, String nginxDir) {
/* 268 */     nginxPath = ToolUtils.handlePath(nginxPath);
/* 269 */     this.settingService.set("nginxPath", nginxPath);
/*     */     
/* 271 */     nginxExe = ToolUtils.handlePath(nginxExe);
/* 272 */     this.settingService.set("nginxExe", nginxExe);
/*     */     
/* 274 */     nginxDir = ToolUtils.handlePath(nginxDir);
/* 275 */     this.settingService.set("nginxDir", nginxDir);
/*     */     
/* 277 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("reload")
/*     */   public synchronized JsonResult reload(String nginxPath, String nginxExe, String nginxDir) {
/* 282 */     if (nginxPath == null) {
/* 283 */       nginxPath = this.settingService.get("nginxPath");
/*     */     }
/* 285 */     if (nginxExe == null) {
/* 286 */       nginxExe = this.settingService.get("nginxExe");
/*     */     }
/* 288 */     if (nginxDir == null) {
/* 289 */       nginxDir = this.settingService.get("nginxDir");
/*     */     }
/*     */     
/*     */     try {
/* 293 */       String cmd = nginxExe + " -s reload -c " + nginxPath;
/* 294 */       if (StrUtil.isNotEmpty(nginxDir)) {
/* 295 */         cmd = cmd + " -p " + nginxDir;
/*     */       }
/* 297 */       String rs = RuntimeUtil.execForStr(new String[] { cmd });
/*     */       
/* 299 */       cmd = "<span class='blue'>" + cmd + "</span>";
/* 300 */       if (StrUtil.isEmpty(rs) || rs.contains("signal process started")) {
/* 301 */         return renderSuccess(cmd + "<br>" + this.m.get("confStr.reloadSuccess") + "<br>" + rs.replace("\n", "<br>"));
/*     */       }
/* 303 */       if (rs.contains("The system cannot find the file specified") || rs.contains("nginx.pid") || rs.contains("PID")) {
/* 304 */         rs = rs + this.m.get("confStr.mayNotRun");
/*     */       }
/*     */       
/* 307 */       return renderSuccess(cmd + "<br>" + this.m.get("confStr.reloadFail") + "<br>" + rs.replace("\n", "<br>"));
/*     */     }
/* 309 */     catch (Exception e) {
/* 310 */       this.logger.error(e.getMessage(), e);
/* 311 */       return renderSuccess(this.m.get("confStr.reloadFail") + "<br>" + e.getMessage().replace("\n", "<br>"));
/*     */     } 
/*     */   }
/*     */   
/*     */   @Mapping("runCmd")
/*     */   public JsonResult runCmd(String cmd, String type) {
/* 317 */     if (StrUtil.isNotEmpty(type)) {
/* 318 */       this.settingService.set(type, cmd);
/*     */     }
/*     */     
/*     */     try {
/* 322 */       String rs = "";
/* 323 */       if (SystemTool.isWindows().booleanValue()) {
/* 324 */         RuntimeUtil.exec(new String[] { "cmd /c start " + cmd });
/*     */       } else {
/* 326 */         rs = RuntimeUtil.execForStr(new String[] { "/bin/sh", "-c", cmd });
/*     */       } 
/*     */       
/* 329 */       cmd = "<span class='blue'>" + cmd + "</span>";
/* 330 */       if (StrUtil.isEmpty(rs) || rs.contains("已终止进程") || rs
/* 331 */         .contains("signal process started") || rs
/* 332 */         .toLowerCase().contains("terminated process") || rs
/* 333 */         .toLowerCase().contains("starting") || rs
/* 334 */         .toLowerCase().contains("stopping")) {
/* 335 */         return renderSuccess(cmd + "<br>" + this.m.get("confStr.runSuccess") + "<br>" + rs.replace("\n", "<br>"));
/*     */       }
/* 337 */       return renderSuccess(cmd + "<br>" + this.m.get("confStr.runFail") + "<br>" + rs.replace("\n", "<br>"));
/*     */     }
/* 339 */     catch (Exception e) {
/* 340 */       this.logger.error(e.getMessage(), e);
/* 341 */       return renderSuccess(this.m.get("confStr.runFail") + "<br>" + e.getMessage().replace("\n", "<br>"));
/*     */     } 
/*     */   }
/*     */   
/*     */   @Mapping("getLastCmd")
/*     */   public JsonResult getLastCmd(String type) {
/* 347 */     return renderSuccess(this.settingService.get(type));
/*     */   }
/*     */   
/*     */   @Mapping("loadConf")
/*     */   public JsonResult loadConf() {
/* 352 */     String decompose = this.settingService.get("decompose");
/*     */     
/* 354 */     ConfExt confExt = this.confService.buildConf(Boolean.valueOf((StrUtil.isNotEmpty(decompose) && decompose.equals("true"))), Boolean.valueOf(false));
/* 355 */     return renderSuccess(confExt);
/*     */   }
/*     */   
/*     */   @Mapping("loadOrg")
/*     */   public JsonResult loadOrg(String nginxPath) {
/* 360 */     String decompose = this.settingService.get("decompose");
/* 361 */     ConfExt confExt = this.confService.buildConf(Boolean.valueOf((StrUtil.isNotEmpty(decompose) && decompose.equals("true"))), Boolean.valueOf(false));
/*     */     
/* 363 */     if (StrUtil.isNotEmpty(nginxPath) && FileUtil.exist(nginxPath) && FileUtil.isFile(nginxPath)) {
/* 364 */       String orgStr = FileUtil.readString(nginxPath, StandardCharsets.UTF_8);
/* 365 */       confExt.setConf(orgStr);
/*     */       
/* 367 */       for (ConfFile confFile : confExt.getFileList()) {
/* 368 */         confFile.setConf("");
/*     */         
/* 370 */         String filePath = (new File(nginxPath)).getParent() + "/conf.d/" + confFile.getName();
/* 371 */         if (FileUtil.exist(filePath)) {
/* 372 */           confFile.setConf(FileUtil.readString(filePath, StandardCharsets.UTF_8));
/*     */         }
/*     */       } 
/*     */       
/* 376 */       return renderSuccess(confExt);
/*     */     } 
/* 378 */     if (FileUtil.isDirectory(nginxPath)) {
/* 379 */       return renderError(this.m.get("confStr.error2"));
/*     */     }
/*     */     
/* 382 */     return renderError(this.m.get("confStr.notExist"));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("decompose")
/*     */   public JsonResult decompose(String decompose) {
/* 389 */     this.settingService.set("decompose", decompose);
/* 390 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("update")
/*     */   public JsonResult update() {
/* 395 */     this.versionConfig.checkVersion();
/* 396 */     if (Integer.parseInt(this.versionConfig.currentVersion.replace(".", "").replace("v", "")) < Integer.parseInt(this.versionConfig.newVersion.getVersion().replace(".", "").replace("v", ""))) {
/* 397 */       this.mainController.autoUpdate(this.versionConfig.newVersion.getUrl());
/* 398 */       return renderSuccess(this.m.get("confStr.updateSuccess"));
/*     */     } 
/* 400 */     return renderSuccess(this.m.get("confStr.noNeedUpdate"));
/*     */   }
/*     */ 
/*     */   
/*     */   @Mapping("getKey")
/*     */   public JsonResult getKey(String key) {
/* 406 */     return renderSuccess(this.settingService.get(key));
/*     */   }
/*     */   
/*     */   @Mapping("setKey")
/*     */   public JsonResult setKey(String key, String val) {
/* 411 */     this.settingService.set(key, val);
/* 412 */     return renderSuccess();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\ConfController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */