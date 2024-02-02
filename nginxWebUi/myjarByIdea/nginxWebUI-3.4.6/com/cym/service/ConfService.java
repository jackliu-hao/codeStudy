package com.cym.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.cym.config.HomeConfig;
import com.cym.ext.AsycPack;
import com.cym.ext.ConfExt;
import com.cym.ext.ConfFile;
import com.cym.model.Bak;
import com.cym.model.BakSub;
import com.cym.model.Basic;
import com.cym.model.Cert;
import com.cym.model.Http;
import com.cym.model.Location;
import com.cym.model.Param;
import com.cym.model.Password;
import com.cym.model.Server;
import com.cym.model.Stream;
import com.cym.model.Template;
import com.cym.model.Upstream;
import com.cym.model.UpstreamServer;
import com.cym.sqlhelper.bean.Sort;
import com.cym.sqlhelper.reflection.SerializableFunction;
import com.cym.sqlhelper.utils.ConditionAndWrapper;
import com.cym.sqlhelper.utils.ConditionWrapper;
import com.cym.sqlhelper.utils.SqlHelper;
import com.cym.utils.ToolUtils;
import com.github.odiszapc.nginxparser.NgxBlock;
import com.github.odiszapc.nginxparser.NgxConfig;
import com.github.odiszapc.nginxparser.NgxDumper;
import com.github.odiszapc.nginxparser.NgxEntry;
import com.github.odiszapc.nginxparser.NgxParam;
import java.io.File;
import java.lang.invoke.SerializedLambda;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ConfService {
   Logger logger = LoggerFactory.getLogger(this.getClass());
   @Inject
   UpstreamService upstreamService;
   @Inject
   SettingService settingService;
   @Inject
   ServerService serverService;
   @Inject
   LocationService locationService;
   @Inject
   ParamService paramService;
   @Inject
   SqlHelper sqlHelper;
   @Inject
   TemplateService templateService;
   @Inject
   OperateLogService operateLogService;
   @Inject
   HomeConfig homeConfig;

   public synchronized ConfExt buildConf(Boolean decompose, Boolean check) {
      ConfExt confExt = new ConfExt();
      confExt.setFileList(new ArrayList());
      String nginxPath = this.settingService.get("nginxPath");
      if (check) {
         nginxPath = this.homeConfig.home + "temp/nginx.conf";
      }

      try {
         NgxConfig ngxConfig = new NgxConfig();
         List<Basic> basicList = this.sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Basic.class);
         Iterator var7 = basicList.iterator();

         while(var7.hasNext()) {
            Basic basic = (Basic)var7.next();
            NgxParam ngxParam = new NgxParam();
            ngxParam.addValue(basic.getName().trim() + " " + basic.getValue().trim());
            ngxConfig.addEntry(ngxParam);
         }

         List<Http> httpList = this.sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Http.class);
         boolean hasHttp = false;
         NgxBlock ngxBlockHttp = new NgxBlock();
         ngxBlockHttp.addValue("http");

         for(Iterator var10 = httpList.iterator(); var10.hasNext(); hasHttp = true) {
            Http http = (Http)var10.next();
            NgxParam ngxParam = new NgxParam();
            ngxParam.addValue(http.getName().trim() + " " + http.getValue().trim());
            ngxBlockHttp.addEntry(ngxParam);
         }

         List<Upstream> upstreams = this.upstreamService.getListByProxyType(0);
         Iterator var26 = upstreams.iterator();

         String type;
         NgxParam ngxParam;
         Iterator var34;
         while(var26.hasNext()) {
            Upstream upstream = (Upstream)var26.next();
            NgxBlock ngxBlockServer = new NgxBlock();
            ngxBlockServer.addValue("upstream " + upstream.getName().trim());
            if (StrUtil.isNotEmpty(upstream.getDescr())) {
               String[] descrs = upstream.getDescr().split("\n");
               String[] var16 = descrs;
               int var17 = descrs.length;

               for(int var18 = 0; var18 < var17; ++var18) {
                  type = var16[var18];
                  ngxParam = new NgxParam();
                  ngxParam.addValue("# " + type);
                  ngxBlockServer.addEntry(ngxParam);
               }
            }

            if (StrUtil.isNotEmpty(upstream.getTactics())) {
               ngxParam = new NgxParam();
               ngxParam.addValue(upstream.getTactics());
               ngxBlockServer.addEntry(ngxParam);
            }

            List<UpstreamServer> upstreamServers = this.upstreamService.getUpstreamServers(upstream.getId());
            var34 = upstreamServers.iterator();

            while(var34.hasNext()) {
               UpstreamServer upstreamServer = (UpstreamServer)var34.next();
               ngxParam = new NgxParam();
               ngxParam.addValue("server " + this.buildNodeStr(upstreamServer));
               ngxBlockServer.addEntry(ngxParam);
            }

            List<Param> paramList = this.paramService.getListByTypeId(upstream.getId(), "upstream");
            Iterator var38 = paramList.iterator();

            while(var38.hasNext()) {
               Param param = (Param)var38.next();
               this.setSameParam(param, ngxBlockServer);
            }

            hasHttp = true;
            if (decompose) {
               this.addConfFile(confExt, "upstreams." + upstream.getName() + ".conf", ngxBlockServer);
               ngxParam = new NgxParam();
               ngxParam.addValue("include " + (new File(nginxPath)).getParent().replace("\\", "/") + "/conf.d/upstreams." + upstream.getName().replace(" ", "_").replace("*", "-") + ".conf");
               ngxBlockHttp.addEntry(ngxParam);
            } else {
               ngxBlockHttp.addEntry(ngxBlockServer);
            }
         }

         List<Server> servers = this.serverService.getListByProxyType(new String[]{"0"});
         Iterator var28 = servers.iterator();

         NgxBlock ngxBlockStream;
         String conf;
         while(var28.hasNext()) {
            Server server = (Server)var28.next();
            if (server.getEnable() != null && server.getEnable()) {
               ngxBlockStream = this.bulidBlockServer(server);
               hasHttp = true;
               if (decompose) {
                  conf = "all";
                  if (StrUtil.isNotEmpty(server.getServerName())) {
                     conf = server.getServerName();
                  }

                  this.addConfFile(confExt, conf + ".conf", ngxBlockStream);
                  ngxParam = new NgxParam();
                  ngxParam.addValue("include " + (new File(nginxPath)).getParent().replace("\\", "/") + "/conf.d/" + conf.replace(" ", "_").replace("*", "-") + ".conf");
                  if (this.noContain(ngxBlockHttp, ngxParam)) {
                     ngxBlockHttp.addEntry(ngxParam);
                  }
               } else {
                  ngxBlockHttp.addEntry(ngxBlockStream);
               }
            }
         }

         if (hasHttp) {
            ngxConfig.addEntry(ngxBlockHttp);
         }

         List<Stream> streamList = this.sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Stream.class);
         boolean hasStream = false;
         ngxBlockStream = new NgxBlock();
         ngxBlockStream.addValue("stream");

         for(var34 = streamList.iterator(); var34.hasNext(); hasStream = true) {
            Stream stream = (Stream)var34.next();
            ngxParam = new NgxParam();
            ngxParam.addValue(stream.getName() + " " + stream.getValue());
            ngxBlockStream.addEntry(ngxParam);
         }

         upstreams = this.upstreamService.getListByProxyType(1);

         NgxBlock ngxBlockServer;
         for(var34 = upstreams.iterator(); var34.hasNext(); hasStream = true) {
            Upstream upstream = (Upstream)var34.next();
            ngxBlockServer = this.buildBlockUpstream(upstream);
            if (decompose) {
               this.addConfFile(confExt, "upstreams." + upstream.getName() + ".conf", ngxBlockServer);
               ngxParam = new NgxParam();
               ngxParam.addValue("include " + (new File(nginxPath)).getParent().replace("\\", "/") + "/conf.d/upstreams." + upstream.getName().replace(" ", "_").replace("*", "-") + ".conf");
               ngxBlockStream.addEntry(ngxParam);
            } else {
               ngxBlockStream.addEntry(ngxBlockServer);
            }
         }

         servers = this.serverService.getListByProxyType(new String[]{"1", "2"});
         var34 = servers.iterator();

         while(var34.hasNext()) {
            Server server = (Server)var34.next();
            if (server.getEnable() != null && server.getEnable()) {
               ngxBlockServer = this.bulidBlockServer(server);
               if (decompose) {
                  type = "";
                  if (server.getProxyType() == 0) {
                     type = "http";
                  } else if (server.getProxyType() == 1) {
                     type = "tcp";
                  } else if (server.getProxyType() == 2) {
                     type = "udp";
                  }

                  this.addConfFile(confExt, type + "." + server.getListen() + ".conf", ngxBlockServer);
                  ngxParam = new NgxParam();
                  ngxParam.addValue("include " + (new File(nginxPath)).getParent().replace("\\", "/") + "/conf.d/" + type + "." + server.getListen() + ".conf");
                  ngxBlockStream.addEntry(ngxParam);
               } else {
                  ngxBlockStream.addEntry(ngxBlockServer);
               }

               hasStream = true;
            }
         }

         if (hasStream) {
            ngxConfig.addEntry(ngxBlockStream);
         }

         conf = ToolUtils.handleConf((new NgxDumper(ngxConfig)).dump());
         confExt.setConf(conf);
         return confExt;
      } catch (Exception var20) {
         this.logger.error((String)var20.getMessage(), (Throwable)var20);
         return null;
      }
   }

   public NgxBlock buildBlockUpstream(Upstream upstream) {
      NgxParam ngxParam = null;
      NgxBlock ngxBlockServer = new NgxBlock();
      ngxBlockServer.addValue("upstream " + upstream.getName());
      if (StrUtil.isNotEmpty(upstream.getDescr())) {
         String[] descrs = upstream.getDescr().split("\n");
         String[] var5 = descrs;
         int var6 = descrs.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String d = var5[var7];
            ngxParam = new NgxParam();
            ngxParam.addValue("# " + d);
            ngxBlockServer.addEntry(ngxParam);
         }
      }

      if (StrUtil.isNotEmpty(upstream.getTactics())) {
         ngxParam = new NgxParam();
         ngxParam.addValue(upstream.getTactics());
         ngxBlockServer.addEntry(ngxParam);
      }

      List<UpstreamServer> upstreamServers = this.upstreamService.getUpstreamServers(upstream.getId());
      Iterator var10 = upstreamServers.iterator();

      while(var10.hasNext()) {
         UpstreamServer upstreamServer = (UpstreamServer)var10.next();
         ngxParam = new NgxParam();
         ngxParam.addValue("server " + this.buildNodeStr(upstreamServer));
         ngxBlockServer.addEntry(ngxParam);
      }

      List<Param> paramList = this.paramService.getListByTypeId(upstream.getId(), "upstream");
      Iterator var13 = paramList.iterator();

      while(var13.hasNext()) {
         Param param = (Param)var13.next();
         this.setSameParam(param, ngxBlockServer);
      }

      return ngxBlockServer;
   }

   public NgxBlock bulidBlockServer(Server server) {
      NgxParam ngxParam = null;
      NgxBlock ngxBlockServer = new NgxBlock();
      String value;
      List locationList;
      Iterator var25;
      if (server.getProxyType() == 0) {
         ngxBlockServer.addValue("server");
         if (StrUtil.isNotEmpty(server.getDescr())) {
            String[] descrs = server.getDescr().split("\n");
            String[] var5 = descrs;
            int var6 = descrs.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               String d = var5[var7];
               ngxParam = new NgxParam();
               ngxParam.addValue("# " + d);
               ngxBlockServer.addEntry(ngxParam);
            }
         }

         if (StrUtil.isNotEmpty(server.getServerName())) {
            ngxParam = new NgxParam();
            ngxParam.addValue("server_name " + server.getServerName());
            ngxBlockServer.addEntry(ngxParam);
         }

         ngxParam = new NgxParam();
         value = "listen " + server.getListen();
         if (server.getDef() == 1) {
            value = value + " default";
         }

         if (server.getProxyProtocol() == 1) {
            value = value + " proxy_protocol";
         }

         if (server.getSsl() != null && server.getSsl() == 1) {
            value = value + " ssl";
            if (server.getHttp2() != null && server.getHttp2() == 1) {
               value = value + " http2";
            }
         }

         ngxParam.addValue(value);
         ngxBlockServer.addEntry(ngxParam);
         if (StrUtil.isNotEmpty(server.getPasswordId())) {
            Password password = (Password)this.sqlHelper.findById(server.getPasswordId(), Password.class);
            if (password != null) {
               ngxParam = new NgxParam();
               ngxParam.addValue("auth_basic \"" + password.getDescr() + "\"");
               ngxBlockServer.addEntry(ngxParam);
               ngxParam = new NgxParam();
               ngxParam.addValue("auth_basic_user_file " + password.getPath());
               ngxBlockServer.addEntry(ngxParam);
            }
         }

         this.setServerSsl(server, ngxBlockServer);
         String type = "server";
         if (server.getProxyType() != 0) {
            type = type + server.getProxyType();
         }

         List<Param> paramList = this.paramService.getListByTypeId(server.getId(), type);
         Iterator var22 = paramList.iterator();

         while(var22.hasNext()) {
            Param param = (Param)var22.next();
            this.setSameParam(param, ngxBlockServer);
         }

         locationList = this.serverService.getLocationByServerId(server.getId());
         var25 = locationList.iterator();

         while(var25.hasNext()) {
            Location location = (Location)var25.next();
            NgxBlock ngxBlockLocation = new NgxBlock();
            ngxBlockLocation.addValue("location");
            ngxBlockLocation.addValue(location.getPath());
            if (StrUtil.isNotEmpty(location.getDescr())) {
               String[] descrs = location.getDescr().split("\n");
               String[] var12 = descrs;
               int var13 = descrs.length;

               for(int var14 = 0; var14 < var13; ++var14) {
                  String d = var12[var14];
                  ngxParam = new NgxParam();
                  ngxParam.addValue("# " + d);
                  ngxBlockLocation.addEntry(ngxParam);
               }
            }

            if (location.getType() != 0 && location.getType() != 2) {
               if (location.getType() != 1) {
                  if (location.getType() == 3) {
                  }
               } else {
                  if (location.getRootType() != null && location.getRootType().equals("alias")) {
                     ngxParam = new NgxParam();
                     ngxParam.addValue("alias " + ToolUtils.handlePath(location.getRootPath()));
                     ngxBlockLocation.addEntry(ngxParam);
                  } else {
                     ngxParam = new NgxParam();
                     ngxParam.addValue("root " + ToolUtils.handlePath(location.getRootPath()));
                     ngxBlockLocation.addEntry(ngxParam);
                  }

                  if (StrUtil.isNotEmpty(location.getRootPage())) {
                     ngxParam = new NgxParam();
                     ngxParam.addValue("index " + location.getRootPage());
                     ngxBlockLocation.addEntry(ngxParam);
                  }
               }
            } else {
               if (location.getType() == 0) {
                  ngxParam = new NgxParam();
                  ngxParam.addValue("proxy_pass " + location.getValue());
                  ngxBlockLocation.addEntry(ngxParam);
               } else if (location.getType() == 2) {
                  Upstream upstream = (Upstream)this.sqlHelper.findById(location.getUpstreamId(), Upstream.class);
                  if (upstream != null) {
                     ngxParam = new NgxParam();
                     ngxParam.addValue("proxy_pass " + location.getUpstreamType() + "://" + upstream.getName() + (location.getUpstreamPath() != null ? location.getUpstreamPath() : ""));
                     ngxBlockLocation.addEntry(ngxParam);
                  }
               }

               if (location.getHeader() == 1) {
                  ngxParam = new NgxParam();
                  ngxParam.addValue("proxy_set_header Host $host");
                  ngxBlockLocation.addEntry(ngxParam);
                  ngxParam = new NgxParam();
                  ngxParam.addValue("proxy_set_header X-Real-IP $remote_addr");
                  ngxBlockLocation.addEntry(ngxParam);
                  ngxParam = new NgxParam();
                  ngxParam.addValue("proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for");
                  ngxBlockLocation.addEntry(ngxParam);
                  ngxParam = new NgxParam();
                  ngxParam.addValue("proxy_set_header X-Forwarded-Host $http_host");
                  ngxBlockLocation.addEntry(ngxParam);
                  ngxParam = new NgxParam();
                  ngxParam.addValue("proxy_set_header X-Forwarded-Port $server_port");
                  ngxBlockLocation.addEntry(ngxParam);
                  ngxParam = new NgxParam();
                  ngxParam.addValue("proxy_set_header X-Forwarded-Proto $scheme");
                  ngxBlockLocation.addEntry(ngxParam);
               }

               if (location.getWebsocket() == 1) {
                  ngxParam = new NgxParam();
                  ngxParam.addValue("proxy_http_version 1.1");
                  ngxBlockLocation.addEntry(ngxParam);
                  ngxParam = new NgxParam();
                  ngxParam.addValue("proxy_set_header Upgrade $http_upgrade");
                  ngxBlockLocation.addEntry(ngxParam);
                  ngxParam = new NgxParam();
                  ngxParam.addValue("proxy_set_header Connection \"upgrade\"");
                  ngxBlockLocation.addEntry(ngxParam);
               }

               if (server.getSsl() == 1 && server.getRewrite() == 1) {
                  ngxParam = new NgxParam();
                  ngxParam.addValue("proxy_redirect http:// https://");
                  ngxBlockLocation.addEntry(ngxParam);
               }
            }

            paramList = this.paramService.getListByTypeId(location.getId(), "location");
            Iterator var28 = paramList.iterator();

            while(var28.hasNext()) {
               Param param = (Param)var28.next();
               this.setSameParam(param, ngxBlockLocation);
            }

            ngxBlockServer.addEntry(ngxBlockLocation);
         }
      } else {
         ngxBlockServer.addValue("server");
         ngxParam = new NgxParam();
         value = "listen " + server.getListen();
         if (server.getProxyProtocol() == 1) {
            value = value + " proxy_protocol";
         }

         if (server.getProxyType() == 2) {
            value = value + " udp reuseport";
         }

         if (server.getSsl() != null && server.getSsl() == 1) {
            value = value + " ssl";
         }

         ngxParam.addValue(value);
         ngxBlockServer.addEntry(ngxParam);
         Upstream upstream = (Upstream)this.sqlHelper.findById(server.getProxyUpstreamId(), Upstream.class);
         if (upstream != null) {
            ngxParam = new NgxParam();
            ngxParam.addValue("proxy_pass " + upstream.getName());
            ngxBlockServer.addEntry(ngxParam);
         }

         this.setServerSsl(server, ngxBlockServer);
         String type = "server";
         if (server.getProxyType() != 0) {
            type = type + server.getProxyType();
         }

         locationList = this.paramService.getListByTypeId(server.getId(), type);
         var25 = locationList.iterator();

         while(var25.hasNext()) {
            Param param = (Param)var25.next();
            this.setSameParam(param, ngxBlockServer);
         }
      }

      return ngxBlockServer;
   }

   private void setServerSsl(Server server, NgxBlock ngxBlockServer) {
      NgxParam ngxParam = null;
      if (server.getSsl() == 1) {
         if (StrUtil.isNotEmpty(server.getPem()) && StrUtil.isNotEmpty(server.getKey())) {
            ngxParam = new NgxParam();
            ngxParam.addValue("ssl_certificate " + ToolUtils.handlePath(server.getPem()));
            ngxBlockServer.addEntry(ngxParam);
            ngxParam = new NgxParam();
            ngxParam.addValue("ssl_certificate_key " + ToolUtils.handlePath(server.getKey()));
            ngxBlockServer.addEntry(ngxParam);
            if (StrUtil.isNotEmpty(server.getProtocols())) {
               ngxParam = new NgxParam();
               ngxParam.addValue("ssl_protocols " + server.getProtocols());
               ngxBlockServer.addEntry(ngxParam);
            }
         }

         if (server.getProxyType() == 0 && server.getRewrite() == 1) {
            String reValue;
            if (StrUtil.isNotEmpty(server.getRewriteListen())) {
               ngxParam = new NgxParam();
               reValue = "listen " + server.getRewriteListen();
               if (server.getDef() == 1) {
                  reValue = reValue + " default";
               }

               if (server.getProxyProtocol() == 1) {
                  reValue = reValue + " proxy_protocol";
               }

               ngxParam.addValue(reValue);
               ngxBlockServer.addEntry(ngxParam);
            }

            reValue = "";
            if (server.getListen().contains(":")) {
               reValue = server.getListen().split(":")[1];
            } else {
               reValue = server.getListen();
            }

            NgxBlock ngxBlock = new NgxBlock();
            ngxBlock.addValue("if ($scheme = http)");
            ngxParam = new NgxParam();
            ngxParam.addValue("return 301 https://$host:" + reValue + "$request_uri");
            ngxBlock.addEntry(ngxParam);
            ngxBlockServer.addEntry(ngxBlock);
         }
      }

   }

   private boolean noContain(NgxBlock ngxBlockHttp, NgxParam ngxParam) {
      Iterator var3 = ngxBlockHttp.getEntries().iterator();

      NgxEntry ngxEntry;
      do {
         if (!var3.hasNext()) {
            return true;
         }

         ngxEntry = (NgxEntry)var3.next();
      } while(!ngxEntry.toString().equals(ngxParam.toString()));

      return false;
   }

   public String buildNodeStr(UpstreamServer upstreamServer) {
      String status = "";
      if (!"none".equals(upstreamServer.getStatus())) {
         status = upstreamServer.getStatus();
      }

      if (upstreamServer.getServer().contains(":")) {
         upstreamServer.setServer("[" + upstreamServer.getServer() + "]");
      }

      String conf = upstreamServer.getServer() + ":" + upstreamServer.getPort();
      if (upstreamServer.getWeight() != null) {
         conf = conf + " weight=" + upstreamServer.getWeight();
      }

      if (upstreamServer.getFailTimeout() != null) {
         conf = conf + " fail_timeout=" + upstreamServer.getFailTimeout() + "s";
      }

      if (upstreamServer.getMaxFails() != null) {
         conf = conf + " max_fails=" + upstreamServer.getMaxFails();
      }

      if (upstreamServer.getMaxConns() != null) {
         conf = conf + " max_conns=" + upstreamServer.getMaxConns();
      }

      conf = conf + " " + status;
      return conf;
   }

   private void setSameParam(Param param, NgxBlock ngxBlock) {
      if (StrUtil.isEmpty(param.getTemplateValue())) {
         NgxParam ngxParam = new NgxParam();
         if (StrUtil.isNotEmpty(param.getName().trim())) {
            param.setName(param.getName().trim() + " ");
         }

         ngxParam.addValue(param.getName() + param.getValue().trim());
         ngxBlock.addEntry(ngxParam);
      } else {
         List<Param> params = this.templateService.getParamList(param.getTemplateValue());
         Iterator var4 = params.iterator();

         while(var4.hasNext()) {
            Param paramSub = (Param)var4.next();
            NgxParam ngxParam = new NgxParam();
            if (StrUtil.isNotEmpty(paramSub.getName().trim())) {
               paramSub.setName(paramSub.getName().trim() + " ");
            }

            ngxParam.addValue(paramSub.getName() + paramSub.getValue().trim());
            ngxBlock.addEntry(ngxParam);
         }
      }

   }

   private void addConfFile(ConfExt confExt, String name, NgxBlock ngxBlockServer) {
      name = name.replace(" ", "_").replace("*", "-");
      boolean hasSameName = false;
      Iterator var5 = confExt.getFileList().iterator();

      while(var5.hasNext()) {
         ConfFile confFile = (ConfFile)var5.next();
         if (confFile.getName().equals(name)) {
            confFile.setConf(confFile.getConf() + "\n" + this.buildStr(ngxBlockServer));
            hasSameName = true;
         }
      }

      if (!hasSameName) {
         ConfFile confFile = new ConfFile();
         confFile.setName(name);
         confFile.setConf(this.buildStr(ngxBlockServer));
         confExt.getFileList().add(confFile);
      }

   }

   private String buildStr(NgxBlock ngxBlockServer) {
      NgxConfig ngxConfig = new NgxConfig();
      ngxConfig.addEntry(ngxBlockServer);
      return ToolUtils.handleConf((new NgxDumper(ngxConfig)).dump());
   }

   public void replace(String nginxPath, String nginxContent, List<String> subContent, List<String> subName, Boolean isReplace, String adminName) {
      String beforeConf = null;
      if (isReplace) {
         beforeConf = FileUtil.readString(nginxPath, StandardCharsets.UTF_8);
      }

      String confd = (new File(nginxPath)).getParent().replace("\\", "/") + "/conf.d/";
      FileUtil.del(confd);
      FileUtil.mkdir(confd);
      FileUtil.writeString(nginxContent, nginxPath.replace(" ", "_"), StandardCharsets.UTF_8);
      String decompose = this.settingService.get("decompose");
      if ("true".equals(decompose) && subContent != null) {
         for(int i = 0; i < subContent.size(); ++i) {
            String tagert = ((new File(nginxPath)).getParent().replace("\\", "/") + "/conf.d/" + (String)subName.get(i)).replace(" ", "_");
            FileUtil.writeString((String)subContent.get(i), tagert, StandardCharsets.UTF_8);
         }
      }

      if (isReplace) {
         Bak bak = new Bak();
         bak.setTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
         bak.setContent(nginxContent);
         this.sqlHelper.insert(bak);

         for(int i = 0; i < subContent.size(); ++i) {
            BakSub bakSub = new BakSub();
            bakSub.setBakId(bak.getId());
            bakSub.setName((String)subName.get(i));
            bakSub.setContent((String)subContent.get(i));
            this.sqlHelper.insert(bakSub);
         }

         if (StrUtil.isNotEmpty(adminName)) {
            this.operateLogService.addLog(beforeConf, nginxContent, adminName);
         }
      }

   }

   public AsycPack getAsycPack(String[] asycData) {
      AsycPack asycPack = new AsycPack();
      if (this.hasStr(asycData, "basic") || this.hasStr(asycData, "all")) {
         asycPack.setBasicList(this.sqlHelper.findAll(Basic.class));
      }

      if (this.hasStr(asycData, "http") || this.hasStr(asycData, "all")) {
         asycPack.setHttpList(this.sqlHelper.findAll(Http.class));
      }

      List passwordList;
      Iterator var4;
      if (this.hasStr(asycData, "server") || this.hasStr(asycData, "all")) {
         passwordList = this.sqlHelper.findAll(Server.class);
         var4 = passwordList.iterator();

         while(var4.hasNext()) {
            Server server = (Server)var4.next();
            if (StrUtil.isNotEmpty(server.getPem()) && FileUtil.exist(server.getPem())) {
               server.setPemStr(FileUtil.readString(server.getPem(), StandardCharsets.UTF_8));
            }

            if (StrUtil.isNotEmpty(server.getKey()) && FileUtil.exist(server.getKey())) {
               server.setKeyStr(FileUtil.readString(server.getKey(), StandardCharsets.UTF_8));
            }
         }

         asycPack.setServerList(passwordList);
         asycPack.setLocationList(this.sqlHelper.findAll(Location.class));
      }

      if (this.hasStr(asycData, "password") || this.hasStr(asycData, "all")) {
         passwordList = this.sqlHelper.findAll(Password.class);
         var4 = passwordList.iterator();

         while(var4.hasNext()) {
            Password password = (Password)var4.next();
            if (StrUtil.isNotEmpty(password.getPath()) && FileUtil.exist(password.getPath())) {
               password.setPathStr(FileUtil.readString(password.getPath(), StandardCharsets.UTF_8));
            }
         }

         asycPack.setPasswordList(passwordList);
      }

      if (this.hasStr(asycData, "upstream") || this.hasStr(asycData, "all")) {
         asycPack.setUpstreamList(this.sqlHelper.findAll(Upstream.class));
         asycPack.setUpstreamServerList(this.sqlHelper.findAll(UpstreamServer.class));
      }

      if (this.hasStr(asycData, "stream") || this.hasStr(asycData, "all")) {
         asycPack.setStreamList(this.sqlHelper.findAll(Stream.class));
      }

      if (this.hasStr(asycData, "param") || this.hasStr(asycData, "all")) {
         asycPack.setTemplateList(this.sqlHelper.findAll(Template.class));
         asycPack.setParamList(this.sqlHelper.findAll(Param.class));
      }

      return asycPack;
   }

   private boolean hasStr(String[] asycData, String data) {
      String[] var3 = asycData;
      int var4 = asycData.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String str = var3[var5];
         if (str.equals(data)) {
            return true;
         }
      }

      return false;
   }

   public void setAsycPack(AsycPack asycPack) {
      try {
         if (asycPack.getBasicList() != null) {
            this.sqlHelper.deleteByQuery(new ConditionAndWrapper(), Basic.class);
            this.sqlHelper.insertAll(asycPack.getBasicList());
         }

         if (asycPack.getHttpList() != null) {
            this.sqlHelper.deleteByQuery(new ConditionAndWrapper(), Http.class);
            this.sqlHelper.insertAll(asycPack.getHttpList());
         }

         Iterator var2;
         if (asycPack.getServerList() != null) {
            this.sqlHelper.deleteByQuery(new ConditionAndWrapper(), Server.class);
            this.sqlHelper.insertAll(asycPack.getServerList());
            this.sqlHelper.deleteByQuery(new ConditionAndWrapper(), Location.class);
            this.sqlHelper.insertAll(asycPack.getLocationList());
            var2 = asycPack.getServerList().iterator();

            while(var2.hasNext()) {
               Server server = (Server)var2.next();

               try {
                  if (StrUtil.isNotEmpty(server.getPem()) && StrUtil.isNotEmpty(server.getPemStr())) {
                     FileUtil.writeString(server.getPemStr(), server.getPem(), StandardCharsets.UTF_8);
                  }

                  if (StrUtil.isNotEmpty(server.getKey()) && StrUtil.isNotEmpty(server.getKeyStr())) {
                     FileUtil.writeString(server.getKeyStr(), server.getKey(), StandardCharsets.UTF_8);
                  }
               } catch (Exception var5) {
                  var5.printStackTrace();
               }
            }
         }

         if (asycPack.getUpstreamList() != null) {
            this.sqlHelper.deleteByQuery(new ConditionAndWrapper(), Upstream.class);
            this.sqlHelper.insertAll(asycPack.getUpstreamList());
            this.sqlHelper.deleteByQuery(new ConditionAndWrapper(), UpstreamServer.class);
            this.sqlHelper.insertAll(asycPack.getUpstreamServerList());
         }

         if (asycPack.getStreamList() != null) {
            this.sqlHelper.deleteByQuery(new ConditionAndWrapper(), Stream.class);
            this.sqlHelper.insertAll(asycPack.getStreamList());
         }

         if (asycPack.getTemplateList() != null) {
            this.sqlHelper.deleteByQuery(new ConditionAndWrapper(), Template.class);
            this.sqlHelper.insertAll(asycPack.getTemplateList());
            this.sqlHelper.deleteByQuery(new ConditionAndWrapper(), Param.class);
            this.sqlHelper.insertAll(asycPack.getParamList());
         }

         if (asycPack.getPasswordList() != null) {
            this.sqlHelper.deleteByQuery(new ConditionAndWrapper(), Password.class);
            this.sqlHelper.insertAll(asycPack.getPasswordList());
            var2 = asycPack.getPasswordList().iterator();

            while(var2.hasNext()) {
               Password password = (Password)var2.next();
               if (StrUtil.isNotEmpty(password.getPath()) && StrUtil.isNotEmpty(password.getPathStr())) {
                  FileUtil.writeString(password.getPathStr(), password.getPath(), StandardCharsets.UTF_8);
               }
            }
         }
      } catch (Exception var6) {
         this.logger.error((String)var6.getMessage(), (Throwable)var6);
      }

   }

   public List<Cert> getApplyCerts() {
      List<Cert> certs = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).ne((SerializableFunction)(Cert::getType), 1), Cert.class);
      return certs;
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "getType":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Cert") && lambda.getImplMethodSignature().equals("()Ljava/lang/Integer;")) {
               return Cert::getType;
            }
         default:
            throw new IllegalArgumentException("Invalid lambda deserialization");
      }
   }
}
