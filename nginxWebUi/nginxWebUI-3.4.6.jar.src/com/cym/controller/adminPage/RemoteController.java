/*     */ package com.cym.controller.adminPage;
/*     */ 
/*     */ import cn.hutool.core.codec.Base64;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.lang.TypeReference;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.http.HttpUtil;
/*     */ import cn.hutool.json.JSONUtil;
/*     */ import com.cym.config.VersionConfig;
/*     */ import com.cym.controller.api.NginxApiController;
/*     */ import com.cym.ext.AsycPack;
/*     */ import com.cym.ext.Tree;
/*     */ import com.cym.model.Admin;
/*     */ import com.cym.model.Group;
/*     */ import com.cym.model.Remote;
/*     */ import com.cym.service.ConfService;
/*     */ import com.cym.service.GroupService;
/*     */ import com.cym.service.RemoteService;
/*     */ import com.cym.service.SettingService;
/*     */ import com.cym.utils.BaseController;
/*     */ import com.cym.utils.JsonResult;
/*     */ import com.cym.utils.NginxUtils;
/*     */ import com.cym.utils.SystemTool;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URL;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ @Mapping("/adminPage/remote")
/*     */ public class RemoteController
/*     */   extends BaseController
/*     */ {
/*  48 */   Logger logger = LoggerFactory.getLogger(getClass());
/*     */   @Inject
/*     */   RemoteService remoteService;
/*     */   @Inject
/*     */   SettingService settingService;
/*     */   @Inject
/*     */   ConfService confService;
/*     */   @Inject
/*     */   GroupService groupService;
/*     */   @Inject
/*     */   ConfController confController;
/*     */   @Inject
/*     */   MainController mainController;
/*     */   @Inject
/*     */   NginxApiController nginxApiController;
/*     */   @Inject
/*     */   VersionConfig versionConfig;
/*     */   @Inject("${server.port}")
/*     */   Integer port;
/*     */   
/*     */   @Mapping("version")
/*     */   public Map<String, Object> version() {
/*  70 */     Map<String, Object> map = new HashMap<>();
/*  71 */     map.put("version", this.versionConfig.currentVersion);
/*     */     
/*  73 */     if (NginxUtils.isRun()) {
/*  74 */       map.put("nginx", Integer.valueOf(1));
/*     */     } else {
/*  76 */       map.put("nginx", Integer.valueOf(0));
/*     */     } 
/*     */     
/*  79 */     return map;
/*     */   }
/*     */ 
/*     */   
/*     */   @Mapping("")
/*     */   public ModelAndView index(ModelAndView modelAndView) {
/*  85 */     JsonResult<List<String>> jsonResult = this.nginxApiController.getNginxStartCmd();
/*  86 */     modelAndView.put("startCmds", jsonResult.getObj());
/*     */     
/*  88 */     jsonResult = this.nginxApiController.getNginxStopCmd();
/*  89 */     modelAndView.put("stopCmds", jsonResult.getObj());
/*     */     
/*  91 */     modelAndView.put("projectVersion", this.versionConfig.currentVersion);
/*  92 */     modelAndView.view("/adminPage/remote/index.html");
/*     */     
/*  94 */     return modelAndView;
/*     */   }
/*     */   
/*     */   @Mapping("allTable")
/*     */   public List<Remote> allTable() {
/*  99 */     Admin admin = getAdmin();
/* 100 */     List<Remote> remoteList = this.sqlHelper.findAll(Remote.class);
/*     */     
/* 102 */     for (Remote remote : remoteList) {
/* 103 */       remote.setStatus(Integer.valueOf(0));
/* 104 */       remote.setType(Integer.valueOf(0));
/* 105 */       if (remote.getParentId() == null) {
/* 106 */         remote.setParentId("");
/*     */       }
/*     */       
/*     */       try {
/* 110 */         String json = HttpUtil.get(remote.getProtocol() + "://" + remote.getIp() + ":" + remote.getPort() + "/adminPage/remote/version?creditKey=" + remote.getCreditKey(), 1000);
/* 111 */         if (StrUtil.isNotEmpty(json)) {
/* 112 */           Map<String, Object> map1 = (Map<String, Object>)JSONUtil.toBean(json, (new TypeReference<Map<String, Object>>() { 
/* 113 */               },  ).getType(), false);
/*     */           
/* 115 */           remote.setStatus(Integer.valueOf(1));
/* 116 */           remote.setVersion((String)map1.get("version"));
/* 117 */           remote.setNginx((Integer)map1.get("nginx"));
/*     */         } 
/* 119 */       } catch (Exception e) {
/* 120 */         this.logger.error(e.getMessage(), e);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 125 */     Remote remoteLocal = new Remote();
/* 126 */     remoteLocal.setId("local");
/* 127 */     remoteLocal.setIp("");
/* 128 */     remoteLocal.setProtocol("");
/* 129 */     remoteLocal.setParentId("");
/* 130 */     remoteLocal.setDescr(this.m.get("remoteStr.local"));
/* 131 */     Map<String, Object> map = version();
/* 132 */     remoteLocal.setVersion((String)map.get("version"));
/* 133 */     remoteLocal.setNginx((Integer)map.get("nginx"));
/* 134 */     remoteLocal.setPort(this.port);
/* 135 */     remoteLocal.setStatus(Integer.valueOf(1));
/* 136 */     remoteLocal.setType(Integer.valueOf(0));
/* 137 */     remoteLocal.setMonitor(Integer.valueOf((this.settingService.get("monitorLocal") != null) ? Integer.parseInt(this.settingService.get("monitorLocal")) : 0));
/* 138 */     remoteLocal.setSystem(SystemTool.getSystem());
/* 139 */     remoteList.add(0, remoteLocal);
/*     */     
/* 141 */     List<Group> groupList = this.remoteService.getGroupByAdmin(admin);
/* 142 */     for (Group group : groupList) {
/* 143 */       Remote remoteGroup = new Remote();
/* 144 */       remoteGroup.setDescr(group.getName());
/* 145 */       remoteGroup.setId(group.getId());
/* 146 */       remoteGroup.setParentId(checkParent(group.getParentId(), groupList));
/* 147 */       remoteGroup.setType(Integer.valueOf(1));
/*     */       
/* 149 */       remoteGroup.setIp("");
/* 150 */       remoteGroup.setProtocol("");
/* 151 */       remoteGroup.setVersion("");
/* 152 */       remoteGroup.setSystem("");
/*     */       
/* 154 */       remoteList.add(remoteGroup);
/*     */     } 
/*     */     
/* 157 */     return remoteList;
/*     */   }
/*     */ 
/*     */   
/*     */   private String checkParent(String parentId, List<Group> groupList) {
/* 162 */     if (parentId == null) {
/* 163 */       return "";
/*     */     }
/*     */     
/* 166 */     for (Group group : groupList) {
/* 167 */       if (group.getId().equals(parentId)) {
/* 168 */         return parentId;
/*     */       }
/*     */     } 
/*     */     
/* 172 */     return "";
/*     */   }
/*     */   
/*     */   @Mapping("addGroupOver")
/*     */   public JsonResult addGroupOver(Group group) {
/* 177 */     if (StrUtil.isNotEmpty(group.getParentId()) && StrUtil.isNotEmpty(group.getId()) && group.getId().equals(group.getParentId())) {
/* 178 */       return renderError(this.m.get("remoteStr.parentGroupMsg"));
/*     */     }
/* 180 */     this.sqlHelper.insertOrUpdate(group);
/*     */     
/* 182 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("groupDetail")
/*     */   public JsonResult groupDetail(String id) {
/* 187 */     return renderSuccess(this.sqlHelper.findById(id, Group.class));
/*     */   }
/*     */ 
/*     */   
/*     */   @Mapping("delGroup")
/*     */   public JsonResult delGroup(String id) {
/* 193 */     this.groupService.delete(id);
/* 194 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("getGroupTree")
/*     */   public JsonResult getGroupTree() {
/* 199 */     Admin admin = getAdmin();
/*     */     
/* 201 */     List<Tree> treeList = new ArrayList<>();
/* 202 */     Tree tree = new Tree();
/* 203 */     tree.setName(this.m.get("remoteStr.noGroup"));
/* 204 */     tree.setValue("");
/* 205 */     treeList.add(0, tree);
/*     */     
/* 207 */     List<Group> groups = this.remoteService.getGroupByAdmin(admin);
/* 208 */     fillTree(groups, treeList);
/*     */     
/* 210 */     return renderSuccess(treeList);
/*     */   }
/*     */   
/*     */   public void fillTree(List<Group> groups, List<Tree> treeList) {
/* 214 */     for (Group group : groups) {
/* 215 */       if (!hasParentIn(group.getParentId(), groups)) {
/* 216 */         Tree tree = new Tree();
/* 217 */         tree.setName(group.getName());
/* 218 */         tree.setValue(group.getId());
/*     */         
/* 220 */         List<Tree> treeSubList = new ArrayList<>();
/* 221 */         fillTree(this.groupService.getListByParent(group.getId()), treeSubList);
/* 222 */         tree.setChildren(treeSubList);
/*     */         
/* 224 */         treeList.add(tree);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean hasParentIn(String parentId, List<Group> groups) {
/* 231 */     for (Group group : groups) {
/* 232 */       if (group.getId().equals(parentId)) {
/* 233 */         return true;
/*     */       }
/*     */     } 
/* 236 */     return false;
/*     */   }
/*     */   
/*     */   @Mapping("getCmdRemote")
/*     */   public JsonResult getCmdRemote() {
/* 241 */     Admin admin = getAdmin();
/*     */     
/* 243 */     List<Group> groups = this.remoteService.getGroupByAdmin(admin);
/* 244 */     List<Remote> remotes = this.remoteService.getListByParent(null);
/*     */     
/* 246 */     List<Tree> treeList = new ArrayList<>();
/* 247 */     fillTreeRemote(groups, remotes, treeList);
/*     */     
/* 249 */     Tree tree = new Tree();
/* 250 */     tree.setName(this.m.get("remoteStr.local"));
/* 251 */     tree.setValue("local");
/*     */     
/* 253 */     treeList.add(0, tree);
/*     */     
/* 255 */     return renderSuccess(treeList);
/*     */   }
/*     */   
/*     */   private void fillTreeRemote(List<Group> groups, List<Remote> remotes, List<Tree> treeList) {
/* 259 */     for (Group group : groups) {
/* 260 */       if (!hasParentIn(group.getParentId(), groups)) {
/* 261 */         Tree tree = new Tree();
/* 262 */         tree.setName(group.getName());
/* 263 */         tree.setValue(group.getId());
/*     */         
/* 265 */         List<Tree> treeSubList = new ArrayList<>();
/*     */         
/* 267 */         fillTreeRemote(this.groupService.getListByParent(group.getId()), this.remoteService.getListByParent(group.getId()), treeSubList);
/*     */         
/* 269 */         tree.setChildren(treeSubList);
/*     */         
/* 271 */         treeList.add(tree);
/*     */       } 
/*     */     } 
/*     */     
/* 275 */     for (Remote remote : remotes) {
/* 276 */       Tree tree = new Tree();
/* 277 */       tree.setName(remote.getIp() + "【" + remote.getDescr() + "】");
/* 278 */       tree.setValue(remote.getId());
/*     */       
/* 280 */       treeList.add(tree);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Mapping("cmdOver")
/*     */   public JsonResult cmdOver(String[] remoteId, String cmd, Integer interval) {
/* 287 */     if (remoteId == null || remoteId.length == 0) {
/* 288 */       return renderSuccess(this.m.get("remoteStr.noSelect"));
/*     */     }
/*     */     
/* 291 */     StringBuilder rs = new StringBuilder();
/* 292 */     for (String id : remoteId) {
/* 293 */       JsonResult jsonResult = null;
/* 294 */       if (id.equals("local")) {
/* 295 */         if (cmd.contentEquals("check")) {
/* 296 */           jsonResult = this.confController.checkBase();
/*     */         }
/* 298 */         if (cmd.contentEquals("reload")) {
/* 299 */           jsonResult = this.confController.reload(null, null, null);
/*     */         }
/* 301 */         if (cmd.contentEquals("replace")) {
/* 302 */           jsonResult = this.confController.replace(this.confController.getReplaceJson(), null);
/*     */         }
/* 304 */         if (cmd.startsWith("start") || cmd.startsWith("stop")) {
/* 305 */           jsonResult = this.confController.runCmd(cmd.replace("start ", "").replace("stop ", ""), null);
/*     */         }
/* 307 */         if (cmd.contentEquals("update")) {
/* 308 */           jsonResult = renderError(this.m.get("remoteStr.notAllow"));
/*     */         }
/* 310 */         rs.append("<span class='blue'>" + this.m.get("remoteStr.local") + "> </span>");
/*     */       } else {
/* 312 */         Remote remote = (Remote)this.sqlHelper.findById(id, Remote.class);
/* 313 */         rs.append("<span class='blue'>").append(remote.getIp() + ":" + remote.getPort()).append("> </span>");
/*     */         
/* 315 */         if (cmd.contentEquals("check")) {
/* 316 */           cmd = "checkBase";
/*     */         }
/*     */         
/*     */         try {
/* 320 */           String action = cmd;
/* 321 */           if (cmd.startsWith("start") || cmd.startsWith("stop")) {
/* 322 */             action = "runCmd";
/*     */           }
/*     */           
/* 325 */           String url = remote.getProtocol() + "://" + remote.getIp() + ":" + remote.getPort() + "/adminPage/conf/" + action + "?creditKey=" + remote.getCreditKey();
/*     */           
/* 327 */           Map<String, Object> map = new HashMap<>();
/* 328 */           if (cmd.startsWith("start") || cmd.startsWith("stop")) {
/* 329 */             map.put("cmd", cmd.replace("start ", "").replace("stop ", ""));
/*     */           }
/*     */           
/* 332 */           String json = HttpUtil.post(url, map);
/* 333 */           jsonResult = (JsonResult)JSONUtil.toBean(json, JsonResult.class);
/* 334 */         } catch (Exception e) {
/* 335 */           this.logger.error(e.getMessage(), e);
/*     */         } 
/*     */       } 
/*     */       
/* 339 */       if (jsonResult != null) {
/* 340 */         if (jsonResult.isSuccess()) {
/* 341 */           rs.append(jsonResult.getObj().toString());
/*     */         } else {
/* 343 */           rs.append(jsonResult.getMsg());
/*     */         } 
/*     */       }
/* 346 */       rs.append("<br>");
/*     */       
/* 348 */       if (interval != null) {
/*     */         try {
/* 350 */           Thread.sleep((interval.intValue() * 1000));
/* 351 */         } catch (InterruptedException e) {
/* 352 */           this.logger.error(e.getMessage(), e);
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 357 */     return renderSuccess(rs.toString());
/*     */   }
/*     */   @Mapping("asyc")
/*     */   public JsonResult asyc(String fromId, String[] remoteId, String[] asycData) {
/*     */     String json;
/* 362 */     if (StrUtil.isEmpty(fromId) || remoteId == null || remoteId.length == 0) {
/* 363 */       return renderError(this.m.get("remoteStr.noChoice"));
/*     */     }
/*     */     
/* 366 */     Remote remoteFrom = (Remote)this.sqlHelper.findById(fromId, Remote.class);
/*     */     
/* 368 */     if (remoteFrom == null) {
/*     */       
/* 370 */       json = getAsycPack(asycData);
/*     */     } else {
/*     */       
/* 373 */       json = HttpUtil.get(remoteFrom.getProtocol() + "://" + remoteFrom.getIp() + ":" + remoteFrom.getPort() + "/adminPage/remote/getAsycPack?creditKey=" + remoteFrom.getCreditKey() + "&asycData=" + 
/* 374 */           StrUtil.join(",", Arrays.asList(asycData)), 1000);
/*     */     } 
/*     */     
/* 377 */     String adminName = getAdmin().getName();
/*     */     
/* 379 */     for (String remoteToId : remoteId) {
/* 380 */       if (remoteToId.equals("local") || remoteToId.equals("本地")) {
/* 381 */         setAsycPack(json, adminName);
/*     */       } else {
/* 383 */         Remote remoteTo = (Remote)this.sqlHelper.findById(remoteToId, Remote.class);
/*     */         try {
/* 385 */           String version = HttpUtil.get(remoteTo.getProtocol() + "://" + remoteTo.getIp() + ":" + remoteTo.getPort() + "/adminPage/remote/version?creditKey=" + remoteTo.getCreditKey(), 1000);
/*     */           
/* 387 */           if (StrUtil.isNotEmpty(version))
/*     */           {
/* 389 */             Map<String, Object> map = new HashMap<>();
/* 390 */             map.put("json", json);
/* 391 */             HttpUtil.post(remoteTo.getProtocol() + "://" + remoteTo.getIp() + ":" + remoteTo.getPort() + "/adminPage/remote/setAsycPack?creditKey=" + remoteTo.getCreditKey() + "&adminName=" + adminName, map);
/*     */           }
/*     */         
/* 394 */         } catch (Exception e) {
/* 395 */           this.logger.error(e.getMessage(), e);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 400 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("getAsycPack")
/*     */   public String getAsycPack(String[] asycData) {
/* 405 */     AsycPack asycPack = this.confService.getAsycPack(asycData);
/*     */     
/* 407 */     return JSONUtil.toJsonPrettyStr(asycPack);
/*     */   }
/*     */   
/*     */   @Mapping("setAsycPack")
/*     */   public JsonResult setAsycPack(String json, String adminName) {
/* 412 */     AsycPack asycPack = (AsycPack)JSONUtil.toBean(json, AsycPack.class);
/*     */     
/* 414 */     this.confService.setAsycPack(asycPack);
/*     */     
/* 416 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("addOver")
/*     */   public JsonResult addOver(Remote remote, String code, String auth) {
/* 421 */     remote.setIp(remote.getIp().trim());
/*     */     
/* 423 */     if (this.remoteService.hasSame(remote)) {
/* 424 */       return renderError(this.m.get("remoteStr.sameIp"));
/*     */     }
/*     */     
/* 427 */     this.remoteService.getCreditKey(remote, code, auth);
/*     */     
/* 429 */     if (StrUtil.isNotEmpty(remote.getCreditKey())) {
/* 430 */       this.sqlHelper.insertOrUpdate(remote);
/* 431 */       return renderSuccess();
/*     */     } 
/* 433 */     return renderError(this.m.get("remoteStr.noAuth"));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("getAuth")
/*     */   public JsonResult getAuth(Remote remote) {
/*     */     try {
/* 441 */       Map<String, Object> map = new HashMap<>();
/* 442 */       map.put("name", Base64.encode(Base64.encode(remote.getName())));
/* 443 */       map.put("pass", Base64.encode(Base64.encode(remote.getPass())));
/* 444 */       map.put("remote", Integer.valueOf(1));
/*     */       
/* 446 */       String rs = HttpUtil.post(remote.getProtocol() + "://" + remote.getIp() + ":" + remote.getPort() + "/adminPage/login/getAuth", map, 3000);
/*     */       
/* 448 */       if (StrUtil.isNotEmpty(rs)) {
/* 449 */         JsonResult jsonResult = (JsonResult)JSONUtil.toBean(rs, JsonResult.class);
/* 450 */         if (jsonResult.isSuccess()) {
/* 451 */           return renderSuccess(jsonResult.getObj());
/*     */         }
/* 453 */         return renderError(jsonResult.getMsg());
/*     */       } 
/*     */       
/* 456 */       return renderError(this.m.get("remoteStr.noAuth"));
/*     */     }
/* 458 */     catch (Exception e) {
/* 459 */       this.logger.error(e.getMessage(), e);
/* 460 */       return renderError(this.m.get("remoteStr.noAuth"));
/*     */     } 
/*     */   }
/*     */   
/*     */   @Mapping("detail")
/*     */   public JsonResult detail(String id) {
/* 466 */     return renderSuccess(this.sqlHelper.findById(id, Remote.class));
/*     */   }
/*     */   
/*     */   @Mapping("del")
/*     */   public JsonResult del(String id) {
/* 471 */     this.sqlHelper.deleteById(id, Remote.class);
/*     */     
/* 473 */     return renderSuccess();
/*     */   }
/*     */ 
/*     */   
/*     */   @Mapping("content")
/*     */   public JsonResult content(String id) {
/* 479 */     Remote remote = (Remote)this.sqlHelper.findById(id, Remote.class);
/*     */     
/* 481 */     String rs = HttpUtil.get(remote.getProtocol() + "://" + remote.getIp() + ":" + remote.getPort() + "/adminPage/remote/readContent?creditKey=" + remote.getCreditKey());
/*     */     
/* 483 */     return renderSuccess(rs);
/*     */   }
/*     */ 
/*     */   
/*     */   @Mapping("readContent")
/*     */   public String readContent() {
/* 489 */     String nginxPath = this.settingService.get("nginxPath");
/*     */     
/* 491 */     if (FileUtil.exist(nginxPath)) {
/* 492 */       return FileUtil.readString(nginxPath, StandardCharsets.UTF_8);
/*     */     }
/* 494 */     return this.m.get("remoteStr.noFile");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("change")
/*     */   public JsonResult change(String id, Context context) {
/* 501 */     Remote remote = (Remote)this.sqlHelper.findById(id, Remote.class);
/*     */     
/* 503 */     if (remote == null) {
/* 504 */       context.sessionSet("localType", "local");
/* 505 */       context.sessionRemove("remote");
/*     */     } else {
/* 507 */       context.sessionSet("localType", "remote");
/* 508 */       context.sessionSet("remote", remote);
/*     */     } 
/*     */     
/* 511 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("nginxStatus")
/*     */   public JsonResult nginxStatus() {
/* 516 */     Map<String, String> map = new HashMap<>();
/* 517 */     map.put("mail", this.settingService.get("mail"));
/*     */     
/* 519 */     String nginxMonitor = this.settingService.get("nginxMonitor");
/* 520 */     map.put("nginxMonitor", (nginxMonitor != null) ? nginxMonitor : "false");
/*     */     
/* 522 */     return renderSuccess(map);
/*     */   }
/*     */   
/*     */   @Mapping("nginxOver")
/*     */   public JsonResult nginxOver(String mail, String nginxMonitor) {
/* 527 */     this.settingService.set("mail", mail);
/* 528 */     this.settingService.set("nginxMonitor", nginxMonitor);
/*     */     
/* 530 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("setMonitor")
/*     */   public JsonResult setMonitor(String id, Integer monitor) {
/* 535 */     if (!"local".equals(id)) {
/* 536 */       Remote remote = new Remote();
/* 537 */       remote.setId(id);
/* 538 */       remote.setMonitor(monitor);
/* 539 */       this.sqlHelper.updateById(remote);
/*     */     } else {
/* 541 */       this.settingService.set("monitorLocal", monitor.toString());
/*     */     } 
/*     */     
/* 544 */     return renderSuccess();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("/src")
/*     */   public void src(String url) throws Exception {
/* 553 */     byte[] buffer = new byte[1024];
/* 554 */     URL downUrl = new URL(url);
/* 555 */     BufferedInputStream bis = null;
/*     */     try {
/* 557 */       bis = new BufferedInputStream(downUrl.openConnection().getInputStream());
/* 558 */       OutputStream os = Context.current().outputStream();
/* 559 */       int i = bis.read(buffer);
/* 560 */       while (i != -1) {
/* 561 */         os.write(buffer, 0, i);
/* 562 */         i = bis.read(buffer);
/*     */       } 
/* 564 */     } catch (Exception e) {
/* 565 */       this.logger.error(e.getMessage(), e);
/*     */     } finally {
/* 567 */       if (bis != null)
/*     */         try {
/* 569 */           bis.close();
/* 570 */         } catch (IOException e) {
/* 571 */           this.logger.error(e.getMessage(), e);
/*     */         }  
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\RemoteController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */