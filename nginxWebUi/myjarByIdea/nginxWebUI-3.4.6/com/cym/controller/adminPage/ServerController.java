package com.cym.controller.adminPage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.cym.ext.ServerExt;
import com.cym.model.Cert;
import com.cym.model.Http;
import com.cym.model.Location;
import com.cym.model.Password;
import com.cym.model.Remote;
import com.cym.model.Server;
import com.cym.model.Stream;
import com.cym.model.Upstream;
import com.cym.model.Www;
import com.cym.service.ConfService;
import com.cym.service.ParamService;
import com.cym.service.ServerService;
import com.cym.service.SettingService;
import com.cym.service.UpstreamService;
import com.cym.sqlhelper.bean.Page;
import com.cym.sqlhelper.bean.Sort;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import com.cym.utils.SnowFlakeUtils;
import com.cym.utils.TelnetUtils;
import com.cym.utils.ToolUtils;
import com.github.odiszapc.nginxparser.NgxBlock;
import com.github.odiszapc.nginxparser.NgxConfig;
import com.github.odiszapc.nginxparser.NgxDumper;
import com.github.odiszapc.nginxparser.NgxParam;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@Mapping("/adminPage/server")
public class ServerController extends BaseController {
   Logger logger = LoggerFactory.getLogger(this.getClass());
   @Inject
   ServerService serverService;
   @Inject
   UpstreamService upstreamService;
   @Inject
   ParamService paramService;
   @Inject
   SettingService settingService;
   @Inject
   ConfService confService;

   @Mapping("")
   public ModelAndView index(ModelAndView modelAndView, Page page, String keywords) {
      page = this.serverService.search(page, keywords);
      List<ServerExt> exts = new ArrayList();

      ServerExt serverExt;
      for(Iterator var5 = page.getRecords().iterator(); var5.hasNext(); exts.add(serverExt)) {
         Server server = (Server)var5.next();
         serverExt = new ServerExt();
         if (server.getEnable() == null) {
            server.setEnable(false);
         }

         if (StrUtil.isNotEmpty(server.getDescr())) {
            server.setDescr(server.getDescr().replace("\n", "<br>").replace(" ", "&nbsp;"));
         }

         serverExt.setServer(server);
         if (server.getProxyType() == 0) {
            serverExt.setLocationStr(this.buildLocationStr(server.getId()));
         } else {
            Upstream upstream = (Upstream)this.sqlHelper.findById(server.getProxyUpstreamId(), Upstream.class);
            serverExt.setLocationStr(this.m.get("serverStr.server") + ": " + (upstream != null ? upstream.getName() : ""));
         }
      }

      page.setRecords(exts);
      modelAndView.put("page", page);
      List<Upstream> upstreamList = this.upstreamService.getListByProxyType(0);
      modelAndView.put("upstreamList", upstreamList);
      modelAndView.put("upstreamSize", upstreamList.size());
      List<Upstream> upstreamTcpList = this.upstreamService.getListByProxyType(1);
      modelAndView.put("upstreamTcpList", upstreamTcpList);
      modelAndView.put("upstreamTcpSize", upstreamTcpList.size());
      List<Cert> certs = this.sqlHelper.findAll(Cert.class);
      Iterator var13 = certs.iterator();

      while(true) {
         Cert cert;
         do {
            if (!var13.hasNext()) {
               modelAndView.put("certList", certs);
               modelAndView.put("wwwList", this.sqlHelper.findAll(Www.class));
               modelAndView.put("passwordList", this.sqlHelper.findAll(Password.class));
               modelAndView.put("keywords", keywords);
               modelAndView.view("/adminPage/server/index.html");
               return modelAndView;
            }

            cert = (Cert)var13.next();
         } while(cert.getType() != 0 && cert.getType() != 2);

         cert.setDomain(cert.getDomain() + "(" + cert.getEncryption() + ")");
      }
   }

   private String buildLocationStr(String id) {
      List<String> str = new ArrayList();
      List<Location> locations = this.serverService.getLocationByServerId(id);
      Iterator var4 = locations.iterator();

      while(var4.hasNext()) {
         Location location = (Location)var4.next();
         String descr = this.m.get("commonStr.descr");
         if (StrUtil.isNotEmpty(location.getDescr())) {
            descr = location.getDescr();
         }

         if (location.getType() == 0) {
            str.add("<span class='path'>" + location.getPath() + "</span><a class='descrBtn' href='javascript:editLocationDescr(\"" + location.getId() + "\")'>" + descr + "</a><br><span class='value'>" + location.getValue() + "</span>");
         } else if (location.getType() == 1) {
            str.add("<span class='path'>" + location.getPath() + "</span><a class='descrBtn' href='javascript:editLocationDescr(\"" + location.getId() + "\")'>" + descr + "</a><br><span class='value'>" + location.getRootPath() + "</span>");
         } else if (location.getType() == 2) {
            Upstream upstream = (Upstream)this.sqlHelper.findById(location.getUpstreamId(), Upstream.class);
            if (upstream != null) {
               str.add("<span class='path'>" + location.getPath() + "</span><a class='descrBtn' href='javascript:editLocationDescr(\"" + location.getId() + "\")'>" + descr + "</a><br><span class='value'>http://" + upstream.getName() + (location.getUpstreamPath() != null ? location.getUpstreamPath() : "") + "</span>");
            }
         } else if (location.getType() == 3) {
            str.add("<span class='path'>" + location.getPath() + "</span><a class='descrBtn' href='javascript:editLocationDescr(\"" + location.getId() + "\")'>" + descr + "</a>");
         }
      }

      return StrUtil.join("<br>", str);
   }

   @Mapping("addOver")
   public JsonResult addOver(String serverJson, String serverParamJson, String locationJson) {
      Server server = (Server)JSONUtil.toBean(serverJson, Server.class);
      List<Location> locations = JSONUtil.toList(JSONUtil.parseArray(locationJson), Location.class);
      if (StrUtil.isEmpty(server.getId())) {
         server.setSeq(SnowFlakeUtils.getId());
      }

      if (server.getProxyType() == 0) {
         try {
            this.serverService.addOver(server, serverParamJson, locations);
         } catch (Exception var7) {
            return this.renderError(var7.getMessage());
         }
      } else {
         this.serverService.addOverTcp(server, serverParamJson);
      }

      return this.renderSuccess();
   }

   @Mapping("setEnable")
   public JsonResult setEnable(Server server) {
      this.sqlHelper.updateById(server);
      return this.renderSuccess();
   }

   @Mapping("detail")
   public JsonResult detail(String id) {
      Server server = (Server)this.sqlHelper.findById(id, Server.class);
      ServerExt serverExt = new ServerExt();
      serverExt.setServer(server);
      List<Location> list = this.serverService.getLocationByServerId(id);
      Iterator var5 = list.iterator();

      while(var5.hasNext()) {
         Location location = (Location)var5.next();
         String json = this.paramService.getJsonByTypeId(location.getId(), "location");
         location.setLocationParamJson(json != null ? json : null);
      }

      serverExt.setLocationList(list);
      String json = this.paramService.getJsonByTypeId(server.getId(), "server");
      serverExt.setParamJson(json != null ? json : null);
      return this.renderSuccess(serverExt);
   }

   @Mapping("del")
   public JsonResult del(String id) {
      this.serverService.deleteById(id);
      return this.renderSuccess();
   }

   @Mapping("importServer")
   public JsonResult importServer(String nginxPath) {
      if (!StrUtil.isEmpty(nginxPath) && FileUtil.exist(nginxPath)) {
         try {
            this.serverService.importServer(nginxPath);
            return this.renderSuccess(this.m.get("serverStr.importSuccess"));
         } catch (Exception var3) {
            this.logger.error((String)var3.getMessage(), (Throwable)var3);
            return this.renderError(this.m.get("serverStr.importFail"));
         }
      } else {
         return this.renderError(this.m.get("serverStr.fileNotExist"));
      }
   }

   @Mapping("testPort")
   public JsonResult testPort() {
      List<Server> servers = this.sqlHelper.findAll(Server.class);
      List<String> ips = new ArrayList();
      Iterator var3 = servers.iterator();

      while(var3.hasNext()) {
         Server server = (Server)var3.next();
         String ip = "";
         String port = "";
         if (server.getListen().contains(":")) {
            String[] strArray = server.getListen().split(":");
            port = strArray[strArray.length - 1];
            ip = server.getListen().replace(":" + port, "");
         } else {
            ip = "127.0.0.1";
            port = server.getListen();
         }

         if (TelnetUtils.isRunning(ip, Integer.parseInt(port)) && !ips.contains(server.getListen())) {
            ips.add(server.getListen());
         }
      }

      if (ips.size() == 0) {
         return this.renderSuccess();
      } else {
         return this.renderError(this.m.get("serverStr.portUserdList") + ": " + StrUtil.join(" , ", ips));
      }
   }

   @Mapping("editDescr")
   public JsonResult editDescr(String id, String descr) {
      Server server = new Server();
      server.setId(id);
      server.setDescr(descr);
      this.sqlHelper.updateById(server);
      return this.renderSuccess();
   }

   @Mapping("preview")
   public JsonResult preview(String id, String type) {
      NgxBlock ngxBlock = null;
      if (type.equals("server")) {
         Server server = (Server)this.sqlHelper.findById(id, Server.class);
         ngxBlock = this.confService.bulidBlockServer(server);
      } else if (type.equals("upstream")) {
         Upstream upstream = (Upstream)this.sqlHelper.findById(id, Upstream.class);
         ngxBlock = this.confService.buildBlockUpstream(upstream);
      } else {
         Iterator var5;
         NgxParam ngxParam;
         List streamList;
         if (type.equals("http")) {
            streamList = this.sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Http.class);
            ngxBlock = new NgxBlock();
            ngxBlock.addValue("http");
            var5 = streamList.iterator();

            while(var5.hasNext()) {
               Http http = (Http)var5.next();
               ngxParam = new NgxParam();
               ngxParam.addValue(http.getName().trim() + " " + http.getValue().trim());
               ngxBlock.addEntry(ngxParam);
            }
         } else if (type.equals("stream")) {
            streamList = this.sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Stream.class);
            ngxBlock = new NgxBlock();
            ngxBlock.addValue("stream");
            var5 = streamList.iterator();

            while(var5.hasNext()) {
               Stream stream = (Stream)var5.next();
               ngxParam = new NgxParam();
               ngxParam.addValue(stream.getName() + " " + stream.getValue());
               ngxBlock.addEntry(ngxParam);
            }
         }
      }

      NgxConfig ngxConfig = new NgxConfig();
      ngxConfig.addEntry(ngxBlock);
      String conf = ToolUtils.handleConf((new NgxDumper(ngxConfig)).dump());
      return this.renderSuccess(conf);
   }

   @Mapping("setOrder")
   public JsonResult setOrder(String id, Integer count) {
      this.serverService.setSeq(id, count);
      return this.renderSuccess();
   }

   @Mapping("getDescr")
   public JsonResult getDescr(String id) {
      Server server = (Server)this.sqlHelper.findById(id, Server.class);
      return this.renderSuccess(server.getDescr());
   }

   @Mapping("getLocationDescr")
   public JsonResult getLocationDescr(String id) {
      Location location = (Location)this.sqlHelper.findById(id, Location.class);
      return this.renderSuccess(location.getDescr());
   }

   @Mapping("setLocationDescr")
   public JsonResult setLocationDescr(String id, String descr) {
      Location location = new Location();
      location.setId(id);
      location.setDescr(descr);
      this.sqlHelper.updateById(location);
      return this.renderSuccess();
   }

   @Mapping("upload")
   public JsonResult upload(Context context, UploadedFile file) {
      try {
         File temp = new File(FileUtil.getTmpDir() + "/" + file.name);
         file.transferTo(temp);

         File dest;
         for(dest = new File(this.homeConfig.home + "cert/" + file.name); FileUtil.exist(dest); dest = new File(dest.getPath() + "_1")) {
         }

         FileUtil.move(temp, dest, true);
         String localType = (String)context.session("localType");
         if ("remote".equals(localType)) {
            Remote remote = (Remote)context.session("remote");
            HashMap<String, Object> paramMap = new HashMap();
            paramMap.put("file", temp);
            String rs = HttpUtil.post(remote.getProtocol() + "://" + remote.getIp() + ":" + remote.getPort() + "/upload", (Map)paramMap);
            JsonResult jsonResult = (JsonResult)JSONUtil.toBean(rs, JsonResult.class);
            FileUtil.del(temp);
            return jsonResult;
         } else {
            return this.renderSuccess(dest.getPath().replace("\\", "/"));
         }
      } catch (IOException | IllegalStateException var10) {
         this.logger.error((String)var10.getMessage(), (Throwable)var10);
         return this.renderError();
      }
   }
}
