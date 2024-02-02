package com.cym.controller.adminPage;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.cym.config.VersionConfig;
import com.cym.controller.api.NginxApiController;
import com.cym.ext.AsycPack;
import com.cym.ext.Tree;
import com.cym.model.Admin;
import com.cym.model.Group;
import com.cym.model.Remote;
import com.cym.service.ConfService;
import com.cym.service.GroupService;
import com.cym.service.RemoteService;
import com.cym.service.SettingService;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import com.cym.utils.NginxUtils;
import com.cym.utils.SystemTool;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@Mapping("/adminPage/remote")
public class RemoteController extends BaseController {
   Logger logger = LoggerFactory.getLogger(this.getClass());
   @Inject
   RemoteService remoteService;
   @Inject
   SettingService settingService;
   @Inject
   ConfService confService;
   @Inject
   GroupService groupService;
   @Inject
   ConfController confController;
   @Inject
   MainController mainController;
   @Inject
   NginxApiController nginxApiController;
   @Inject
   VersionConfig versionConfig;
   @Inject("${server.port}")
   Integer port;

   @Mapping("version")
   public Map<String, Object> version() {
      Map<String, Object> map = new HashMap();
      map.put("version", this.versionConfig.currentVersion);
      if (NginxUtils.isRun()) {
         map.put("nginx", 1);
      } else {
         map.put("nginx", 0);
      }

      return map;
   }

   @Mapping("")
   public ModelAndView index(ModelAndView modelAndView) {
      JsonResult<List<String>> jsonResult = this.nginxApiController.getNginxStartCmd();
      modelAndView.put("startCmds", jsonResult.getObj());
      jsonResult = this.nginxApiController.getNginxStopCmd();
      modelAndView.put("stopCmds", jsonResult.getObj());
      modelAndView.put("projectVersion", this.versionConfig.currentVersion);
      modelAndView.view("/adminPage/remote/index.html");
      return modelAndView;
   }

   @Mapping("allTable")
   public List<Remote> allTable() {
      Admin admin = this.getAdmin();
      List<Remote> remoteList = this.sqlHelper.findAll(Remote.class);
      Iterator var3 = remoteList.iterator();

      while(var3.hasNext()) {
         Remote remote = (Remote)var3.next();
         remote.setStatus(0);
         remote.setType(0);
         if (remote.getParentId() == null) {
            remote.setParentId("");
         }

         try {
            String json = HttpUtil.get(remote.getProtocol() + "://" + remote.getIp() + ":" + remote.getPort() + "/adminPage/remote/version?creditKey=" + remote.getCreditKey(), 1000);
            if (StrUtil.isNotEmpty(json)) {
               Map<String, Object> map = (Map)JSONUtil.toBean(json, (new TypeReference<Map<String, Object>>() {
               }).getType(), false);
               remote.setStatus(1);
               remote.setVersion((String)map.get("version"));
               remote.setNginx((Integer)map.get("nginx"));
            }
         } catch (Exception var9) {
            this.logger.error((String)var9.getMessage(), (Throwable)var9);
         }
      }

      Remote remoteLocal = new Remote();
      remoteLocal.setId("local");
      remoteLocal.setIp("");
      remoteLocal.setProtocol("");
      remoteLocal.setParentId("");
      remoteLocal.setDescr(this.m.get("remoteStr.local"));
      Map<String, Object> map = this.version();
      remoteLocal.setVersion((String)map.get("version"));
      remoteLocal.setNginx((Integer)map.get("nginx"));
      remoteLocal.setPort(this.port);
      remoteLocal.setStatus(1);
      remoteLocal.setType(0);
      remoteLocal.setMonitor(this.settingService.get("monitorLocal") != null ? Integer.parseInt(this.settingService.get("monitorLocal")) : 0);
      remoteLocal.setSystem(SystemTool.getSystem());
      remoteList.add(0, remoteLocal);
      List<Group> groupList = this.remoteService.getGroupByAdmin(admin);
      Iterator var13 = groupList.iterator();

      while(var13.hasNext()) {
         Group group = (Group)var13.next();
         Remote remoteGroup = new Remote();
         remoteGroup.setDescr(group.getName());
         remoteGroup.setId(group.getId());
         remoteGroup.setParentId(this.checkParent(group.getParentId(), groupList));
         remoteGroup.setType(1);
         remoteGroup.setIp("");
         remoteGroup.setProtocol("");
         remoteGroup.setVersion("");
         remoteGroup.setSystem("");
         remoteList.add(remoteGroup);
      }

      return remoteList;
   }

   private String checkParent(String parentId, List<Group> groupList) {
      if (parentId == null) {
         return "";
      } else {
         Iterator var3 = groupList.iterator();

         Group group;
         do {
            if (!var3.hasNext()) {
               return "";
            }

            group = (Group)var3.next();
         } while(!group.getId().equals(parentId));

         return parentId;
      }
   }

   @Mapping("addGroupOver")
   public JsonResult addGroupOver(Group group) {
      if (StrUtil.isNotEmpty(group.getParentId()) && StrUtil.isNotEmpty(group.getId()) && group.getId().equals(group.getParentId())) {
         return this.renderError(this.m.get("remoteStr.parentGroupMsg"));
      } else {
         this.sqlHelper.insertOrUpdate(group);
         return this.renderSuccess();
      }
   }

   @Mapping("groupDetail")
   public JsonResult groupDetail(String id) {
      return this.renderSuccess(this.sqlHelper.findById(id, Group.class));
   }

   @Mapping("delGroup")
   public JsonResult delGroup(String id) {
      this.groupService.delete(id);
      return this.renderSuccess();
   }

   @Mapping("getGroupTree")
   public JsonResult getGroupTree() {
      Admin admin = this.getAdmin();
      List<Tree> treeList = new ArrayList();
      Tree tree = new Tree();
      tree.setName(this.m.get("remoteStr.noGroup"));
      tree.setValue("");
      treeList.add(0, tree);
      List<Group> groups = this.remoteService.getGroupByAdmin(admin);
      this.fillTree(groups, treeList);
      return this.renderSuccess(treeList);
   }

   public void fillTree(List<Group> groups, List<Tree> treeList) {
      Iterator var3 = groups.iterator();

      while(var3.hasNext()) {
         Group group = (Group)var3.next();
         if (!this.hasParentIn(group.getParentId(), groups)) {
            Tree tree = new Tree();
            tree.setName(group.getName());
            tree.setValue(group.getId());
            List<Tree> treeSubList = new ArrayList();
            this.fillTree(this.groupService.getListByParent(group.getId()), treeSubList);
            tree.setChildren(treeSubList);
            treeList.add(tree);
         }
      }

   }

   private boolean hasParentIn(String parentId, List<Group> groups) {
      Iterator var3 = groups.iterator();

      Group group;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         group = (Group)var3.next();
      } while(!group.getId().equals(parentId));

      return true;
   }

   @Mapping("getCmdRemote")
   public JsonResult getCmdRemote() {
      Admin admin = this.getAdmin();
      List<Group> groups = this.remoteService.getGroupByAdmin(admin);
      List<Remote> remotes = this.remoteService.getListByParent((String)null);
      List<Tree> treeList = new ArrayList();
      this.fillTreeRemote(groups, remotes, treeList);
      Tree tree = new Tree();
      tree.setName(this.m.get("remoteStr.local"));
      tree.setValue("local");
      treeList.add(0, tree);
      return this.renderSuccess(treeList);
   }

   private void fillTreeRemote(List<Group> groups, List<Remote> remotes, List<Tree> treeList) {
      Iterator var4 = groups.iterator();

      Tree tree;
      while(var4.hasNext()) {
         Group group = (Group)var4.next();
         if (!this.hasParentIn(group.getParentId(), groups)) {
            tree = new Tree();
            tree.setName(group.getName());
            tree.setValue(group.getId());
            List<Tree> treeSubList = new ArrayList();
            this.fillTreeRemote(this.groupService.getListByParent(group.getId()), this.remoteService.getListByParent(group.getId()), treeSubList);
            tree.setChildren(treeSubList);
            treeList.add(tree);
         }
      }

      var4 = remotes.iterator();

      while(var4.hasNext()) {
         Remote remote = (Remote)var4.next();
         tree = new Tree();
         tree.setName(remote.getIp() + "【" + remote.getDescr() + "】");
         tree.setValue(remote.getId());
         treeList.add(tree);
      }

   }

   @Mapping("cmdOver")
   public JsonResult cmdOver(String[] remoteId, String cmd, Integer interval) {
      if (remoteId != null && remoteId.length != 0) {
         StringBuilder rs = new StringBuilder();
         String[] var5 = remoteId;
         int var6 = remoteId.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String id = var5[var7];
            JsonResult jsonResult = null;
            if (id.equals("local")) {
               if (cmd.contentEquals("check")) {
                  jsonResult = this.confController.checkBase();
               }

               if (cmd.contentEquals("reload")) {
                  jsonResult = this.confController.reload((String)null, (String)null, (String)null);
               }

               if (cmd.contentEquals("replace")) {
                  jsonResult = this.confController.replace(this.confController.getReplaceJson(), (String)null);
               }

               if (cmd.startsWith("start") || cmd.startsWith("stop")) {
                  jsonResult = this.confController.runCmd(cmd.replace("start ", "").replace("stop ", ""), (String)null);
               }

               if (cmd.contentEquals("update")) {
                  jsonResult = this.renderError(this.m.get("remoteStr.notAllow"));
               }

               rs.append("<span class='blue'>" + this.m.get("remoteStr.local") + "> </span>");
            } else {
               Remote remote = (Remote)this.sqlHelper.findById(id, Remote.class);
               rs.append("<span class='blue'>").append(remote.getIp() + ":" + remote.getPort()).append("> </span>");
               if (cmd.contentEquals("check")) {
                  cmd = "checkBase";
               }

               try {
                  String action = cmd;
                  if (cmd.startsWith("start") || cmd.startsWith("stop")) {
                     action = "runCmd";
                  }

                  String url = remote.getProtocol() + "://" + remote.getIp() + ":" + remote.getPort() + "/adminPage/conf/" + action + "?creditKey=" + remote.getCreditKey();
                  Map<String, Object> map = new HashMap();
                  if (cmd.startsWith("start") || cmd.startsWith("stop")) {
                     map.put("cmd", cmd.replace("start ", "").replace("stop ", ""));
                  }

                  String json = HttpUtil.post(url, (Map)map);
                  jsonResult = (JsonResult)JSONUtil.toBean(json, JsonResult.class);
               } catch (Exception var16) {
                  this.logger.error((String)var16.getMessage(), (Throwable)var16);
               }
            }

            if (jsonResult != null) {
               if (jsonResult.isSuccess()) {
                  rs.append(jsonResult.getObj().toString());
               } else {
                  rs.append(jsonResult.getMsg());
               }
            }

            rs.append("<br>");
            if (interval != null) {
               try {
                  Thread.sleep((long)(interval * 1000));
               } catch (InterruptedException var15) {
                  this.logger.error((String)var15.getMessage(), (Throwable)var15);
               }
            }
         }

         return this.renderSuccess(rs.toString());
      } else {
         return this.renderSuccess(this.m.get("remoteStr.noSelect"));
      }
   }

   @Mapping("asyc")
   public JsonResult asyc(String fromId, String[] remoteId, String[] asycData) {
      if (!StrUtil.isEmpty(fromId) && remoteId != null && remoteId.length != 0) {
         Remote remoteFrom = (Remote)this.sqlHelper.findById(fromId, Remote.class);
         String json;
         if (remoteFrom == null) {
            json = this.getAsycPack(asycData);
         } else {
            json = HttpUtil.get(remoteFrom.getProtocol() + "://" + remoteFrom.getIp() + ":" + remoteFrom.getPort() + "/adminPage/remote/getAsycPack?creditKey=" + remoteFrom.getCreditKey() + "&asycData=" + StrUtil.join(",", Arrays.asList(asycData)), 1000);
         }

         String adminName = this.getAdmin().getName();
         String[] var7 = remoteId;
         int var8 = remoteId.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            String remoteToId = var7[var9];
            if (!remoteToId.equals("local") && !remoteToId.equals("本地")) {
               Remote remoteTo = (Remote)this.sqlHelper.findById(remoteToId, Remote.class);

               try {
                  String version = HttpUtil.get(remoteTo.getProtocol() + "://" + remoteTo.getIp() + ":" + remoteTo.getPort() + "/adminPage/remote/version?creditKey=" + remoteTo.getCreditKey(), 1000);
                  if (StrUtil.isNotEmpty(version)) {
                     Map<String, Object> map = new HashMap();
                     map.put("json", json);
                     HttpUtil.post(remoteTo.getProtocol() + "://" + remoteTo.getIp() + ":" + remoteTo.getPort() + "/adminPage/remote/setAsycPack?creditKey=" + remoteTo.getCreditKey() + "&adminName=" + adminName, (Map)map);
                  }
               } catch (Exception var14) {
                  this.logger.error((String)var14.getMessage(), (Throwable)var14);
               }
            } else {
               this.setAsycPack(json, adminName);
            }
         }

         return this.renderSuccess();
      } else {
         return this.renderError(this.m.get("remoteStr.noChoice"));
      }
   }

   @Mapping("getAsycPack")
   public String getAsycPack(String[] asycData) {
      AsycPack asycPack = this.confService.getAsycPack(asycData);
      return JSONUtil.toJsonPrettyStr((Object)asycPack);
   }

   @Mapping("setAsycPack")
   public JsonResult setAsycPack(String json, String adminName) {
      AsycPack asycPack = (AsycPack)JSONUtil.toBean(json, AsycPack.class);
      this.confService.setAsycPack(asycPack);
      return this.renderSuccess();
   }

   @Mapping("addOver")
   public JsonResult addOver(Remote remote, String code, String auth) {
      remote.setIp(remote.getIp().trim());
      if (this.remoteService.hasSame(remote)) {
         return this.renderError(this.m.get("remoteStr.sameIp"));
      } else {
         this.remoteService.getCreditKey(remote, code, auth);
         if (StrUtil.isNotEmpty(remote.getCreditKey())) {
            this.sqlHelper.insertOrUpdate(remote);
            return this.renderSuccess();
         } else {
            return this.renderError(this.m.get("remoteStr.noAuth"));
         }
      }
   }

   @Mapping("getAuth")
   public JsonResult getAuth(Remote remote) {
      try {
         Map<String, Object> map = new HashMap();
         map.put("name", Base64.encode((CharSequence)Base64.encode((CharSequence)remote.getName())));
         map.put("pass", Base64.encode((CharSequence)Base64.encode((CharSequence)remote.getPass())));
         map.put("remote", 1);
         String rs = HttpUtil.post(remote.getProtocol() + "://" + remote.getIp() + ":" + remote.getPort() + "/adminPage/login/getAuth", (Map)map, 3000);
         if (StrUtil.isNotEmpty(rs)) {
            JsonResult jsonResult = (JsonResult)JSONUtil.toBean(rs, JsonResult.class);
            return jsonResult.isSuccess() ? this.renderSuccess(jsonResult.getObj()) : this.renderError(jsonResult.getMsg());
         } else {
            return this.renderError(this.m.get("remoteStr.noAuth"));
         }
      } catch (Exception var5) {
         this.logger.error((String)var5.getMessage(), (Throwable)var5);
         return this.renderError(this.m.get("remoteStr.noAuth"));
      }
   }

   @Mapping("detail")
   public JsonResult detail(String id) {
      return this.renderSuccess(this.sqlHelper.findById(id, Remote.class));
   }

   @Mapping("del")
   public JsonResult del(String id) {
      this.sqlHelper.deleteById(id, Remote.class);
      return this.renderSuccess();
   }

   @Mapping("content")
   public JsonResult content(String id) {
      Remote remote = (Remote)this.sqlHelper.findById(id, Remote.class);
      String rs = HttpUtil.get(remote.getProtocol() + "://" + remote.getIp() + ":" + remote.getPort() + "/adminPage/remote/readContent?creditKey=" + remote.getCreditKey());
      return this.renderSuccess(rs);
   }

   @Mapping("readContent")
   public String readContent() {
      String nginxPath = this.settingService.get("nginxPath");
      return FileUtil.exist(nginxPath) ? FileUtil.readString(nginxPath, StandardCharsets.UTF_8) : this.m.get("remoteStr.noFile");
   }

   @Mapping("change")
   public JsonResult change(String id, Context context) {
      Remote remote = (Remote)this.sqlHelper.findById(id, Remote.class);
      if (remote == null) {
         context.sessionSet("localType", "local");
         context.sessionRemove("remote");
      } else {
         context.sessionSet("localType", "remote");
         context.sessionSet("remote", remote);
      }

      return this.renderSuccess();
   }

   @Mapping("nginxStatus")
   public JsonResult nginxStatus() {
      Map<String, String> map = new HashMap();
      map.put("mail", this.settingService.get("mail"));
      String nginxMonitor = this.settingService.get("nginxMonitor");
      map.put("nginxMonitor", nginxMonitor != null ? nginxMonitor : "false");
      return this.renderSuccess(map);
   }

   @Mapping("nginxOver")
   public JsonResult nginxOver(String mail, String nginxMonitor) {
      this.settingService.set("mail", mail);
      this.settingService.set("nginxMonitor", nginxMonitor);
      return this.renderSuccess();
   }

   @Mapping("setMonitor")
   public JsonResult setMonitor(String id, Integer monitor) {
      if (!"local".equals(id)) {
         Remote remote = new Remote();
         remote.setId(id);
         remote.setMonitor(monitor);
         this.sqlHelper.updateById(remote);
      } else {
         this.settingService.set("monitorLocal", monitor.toString());
      }

      return this.renderSuccess();
   }

   @Mapping("/src")
   public void src(String url) throws Exception {
      byte[] buffer = new byte[1024];
      URL downUrl = new URL(url);
      BufferedInputStream bis = null;

      try {
         bis = new BufferedInputStream(downUrl.openConnection().getInputStream());
         OutputStream os = Context.current().outputStream();

         for(int i = bis.read(buffer); i != -1; i = bis.read(buffer)) {
            os.write(buffer, 0, i);
         }
      } catch (Exception var15) {
         this.logger.error((String)var15.getMessage(), (Throwable)var15);
      } finally {
         if (bis != null) {
            try {
               bis.close();
            } catch (IOException var14) {
               this.logger.error((String)var14.getMessage(), (Throwable)var14);
            }
         }

      }

   }
}
