package com.cym.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cym.model.Location;
import com.cym.model.Param;
import com.cym.model.Server;
import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.bean.Page;
import com.cym.sqlhelper.bean.Sort;
import com.cym.sqlhelper.reflection.SerializableFunction;
import com.cym.sqlhelper.utils.ConditionAndWrapper;
import com.cym.sqlhelper.utils.ConditionOrWrapper;
import com.cym.sqlhelper.utils.ConditionWrapper;
import com.cym.sqlhelper.utils.SqlHelper;
import com.cym.utils.SnowFlakeUtils;
import com.github.odiszapc.nginxparser.NgxBlock;
import com.github.odiszapc.nginxparser.NgxConfig;
import com.github.odiszapc.nginxparser.NgxEntry;
import com.github.odiszapc.nginxparser.NgxParam;
import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ServerService {
   Logger logger = LoggerFactory.getLogger(this.getClass());
   @Inject
   SqlHelper sqlHelper;

   public Page search(Page page, String keywords) {
      ConditionOrWrapper conditionOrWrapper = new ConditionOrWrapper();
      if (StrUtil.isNotEmpty(keywords)) {
         conditionOrWrapper.like(Server::getDescr, keywords.trim()).like(Server::getServerName, keywords.trim()).like(Server::getListen, keywords.trim());
         List<String> serverIds = this.sqlHelper.findPropertiesByQuery((new ConditionOrWrapper()).like(Location::getDescr, keywords).like(Location::getValue, keywords).like(Location::getPath, keywords), Location.class, (SerializableFunction)(Location::getServerId));
         conditionOrWrapper.in((SerializableFunction)(BaseModel::getId), (Collection)serverIds);
      }

      Sort sort = (new Sort()).add("seq", Sort.Direction.DESC);
      page = this.sqlHelper.findPage(conditionOrWrapper, sort, page, Server.class);
      return page;
   }

   public void deleteById(String id) {
      this.sqlHelper.deleteById(id, Server.class);
      this.sqlHelper.deleteByQuery((new ConditionAndWrapper()).eq((String)"serverId", id), Location.class);
   }

   public List<Location> getLocationByServerId(String serverId) {
      return this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((String)"serverId", serverId), Location.class);
   }

   public void addOver(Server server, String serverParamJson, List<Location> locations) throws Exception {
      if (server.getDef() != null && server.getDef() == 1) {
         this.clearDef();
      }

      this.sqlHelper.insertOrUpdate(server);
      List<Param> paramList = new ArrayList();
      if (StrUtil.isNotEmpty(serverParamJson) && JSONUtil.isTypeJSON(serverParamJson)) {
         paramList = JSONUtil.toList(JSONUtil.parseArray(serverParamJson), Param.class);
      }

      List<String> locationIds = this.sqlHelper.findIdsByQuery((new ConditionAndWrapper()).eq((String)"serverId", server.getId()), Location.class);
      this.sqlHelper.deleteByQuery((new ConditionOrWrapper()).eq((String)"serverId", server.getId()).in((String)"locationId", (Collection)locationIds), Param.class);
      Collections.reverse((List)paramList);
      Iterator var6 = ((List)paramList).iterator();

      while(var6.hasNext()) {
         Param param = (Param)var6.next();
         param.setServerId(server.getId());
         this.sqlHelper.insert(param);
      }

      List<Location> locationOlds = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((String)"serverId", server.getId()), Location.class);
      this.sqlHelper.deleteByQuery((new ConditionAndWrapper()).eq((String)"serverId", server.getId()), Location.class);
      if (locations != null) {
         Collections.reverse(locations);
         Iterator var12 = locations.iterator();

         while(var12.hasNext()) {
            Location location = (Location)var12.next();
            location.setServerId(server.getId());
            location.setDescr(this.findLocationDescr(locationOlds, location));
            this.sqlHelper.insert(location);
            paramList = new ArrayList();
            if (StrUtil.isNotEmpty(location.getLocationParamJson()) && JSONUtil.isJson(location.getLocationParamJson())) {
               paramList = JSONUtil.toList(JSONUtil.parseArray(location.getLocationParamJson()), Param.class);
            }

            Collections.reverse((List)paramList);
            Iterator var9 = ((List)paramList).iterator();

            while(var9.hasNext()) {
               Param param = (Param)var9.next();
               param.setLocationId(location.getId());
               this.sqlHelper.insert(param);
            }
         }
      }

   }

   private String findLocationDescr(List<Location> locationOlds, Location locationNew) {
      Iterator var3 = locationOlds.iterator();

      Location location;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         location = (Location)var3.next();
      } while(!location.getPath().equals(locationNew.getPath()) || location.getType() != locationNew.getType());

      return location.getDescr();
   }

   private void clearDef() {
      List<Server> servers = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((String)"def", 1), Server.class);
      Iterator var2 = servers.iterator();

      while(var2.hasNext()) {
         Server server = (Server)var2.next();
         server.setDef(0);
         this.sqlHelper.updateById(server);
      }

   }

   public void addOverTcp(Server server, String serverParamJson) {
      this.sqlHelper.insertOrUpdate(server);
      List<String> locationIds = this.sqlHelper.findIdsByQuery((new ConditionAndWrapper()).eq((String)"serverId", server.getId()), Location.class);
      this.sqlHelper.deleteByQuery((new ConditionOrWrapper()).eq((String)"serverId", server.getId()).in((String)"locationId", (Collection)locationIds), Param.class);
      List<Param> paramList = new ArrayList();
      if (StrUtil.isNotEmpty(serverParamJson) && JSONUtil.isTypeJSON(serverParamJson)) {
         paramList = JSONUtil.toList(JSONUtil.parseArray(serverParamJson), Param.class);
      }

      Iterator var5 = ((List)paramList).iterator();

      while(var5.hasNext()) {
         Param param = (Param)var5.next();
         param.setServerId(server.getId());
         this.sqlHelper.insert(param);
      }

      this.sqlHelper.deleteByQuery((new ConditionAndWrapper()).eq((String)"serverId", server.getId()), Location.class);
   }

   public List<Server> getListByProxyType(String[] proxyType) {
      Sort sort = (new Sort()).add("seq", Sort.Direction.DESC);
      return this.sqlHelper.findListByQuery((new ConditionAndWrapper()).in((String)"proxyType", (Object[])proxyType), sort, Server.class);
   }

   public void importServer(String nginxPath) throws Exception {
      String initNginxPath = this.initNginx(nginxPath);
      NgxConfig conf = null;

      try {
         conf = NgxConfig.read(initNginxPath);
      } catch (IOException var21) {
         this.logger.error((String)var21.getMessage(), (Throwable)var21);
         throw new Exception("文件读取失败");
      }

      List<NgxEntry> servers = conf.findAll(NgxConfig.BLOCK, new String[]{"server"});
      servers.addAll(conf.findAll(NgxConfig.BLOCK, new String[]{"http", "server"}));
      Collections.reverse(servers);
      Iterator var5 = servers.iterator();

      label88:
      while(var5.hasNext()) {
         NgxEntry ngxEntry = (NgxEntry)var5.next();
         NgxBlock serverNgx = (NgxBlock)ngxEntry;
         NgxParam serverName = serverNgx.findParam("server_name");
         Server server = new Server();
         if (serverName == null) {
            server.setServerName("");
         } else {
            server.setServerName(serverName.getValue());
         }

         server.setProxyType(0);
         List<NgxEntry> listens = serverNgx.findAll(NgxConfig.PARAM, "listen");
         Iterator var11 = listens.iterator();

         while(var11.hasNext()) {
            NgxEntry item = (NgxEntry)var11.next();
            NgxParam param = (NgxParam)item;
            if (server.getListen() == null) {
               server.setListen((String)param.getValues().toArray()[0]);
            }

            if (param.getTokens().stream().anyMatch((item2) -> {
               return "ssl".equals(item2.getToken());
            })) {
               server.setSsl(1);
               NgxParam key = serverNgx.findParam("ssl_certificate_key");
               NgxParam perm = serverNgx.findParam("ssl_certificate");
               server.setKey(key == null ? "" : key.getValue());
               server.setPem(perm == null ? "" : perm.getValue());
            }

            if (param.getTokens().stream().anyMatch((item2) -> {
               return "http2".equals(item2.getToken());
            })) {
               server.setHttp2(1);
            }
         }

         long rewriteCount = serverNgx.getEntries().stream().filter((itemx) -> {
            if (itemx instanceof NgxBlock) {
               NgxBlock itemNgx = (NgxBlock)itemx;
               return itemNgx.getEntries().toString().contains("rewrite");
            } else {
               return false;
            }
         }).count();
         if (rewriteCount > 0L) {
            server.setRewrite(1);
         } else {
            server.setRewrite(0);
         }

         List<Location> locations = new ArrayList();
         List<NgxEntry> locationBlocks = serverNgx.findAll(NgxBlock.class, "location");
         Iterator var25 = locationBlocks.iterator();

         while(true) {
            Location location;
            while(true) {
               if (!var25.hasNext()) {
                  server.setDef(0);
                  server.setSeq(SnowFlakeUtils.getId());
                  this.addOver(server, "", locations);
                  continue label88;
               }

               NgxEntry item = (NgxEntry)var25.next();
               location = new Location();
               NgxParam proxyPassParam = ((NgxBlock)item).findParam("proxy_pass");
               location.setPath(((NgxBlock)item).getValue());
               if (proxyPassParam != null) {
                  location.setValue(proxyPassParam.getValue());
                  location.setType(0);
                  break;
               }

               NgxParam rootParam = ((NgxBlock)item).findParam("root");
               if (rootParam == null) {
                  rootParam = ((NgxBlock)item).findParam("alias");
               }

               if (rootParam != null) {
                  location.setRootType(rootParam.getName());
                  location.setRootPath(rootParam.getValue());
                  NgxParam indexParam = ((NgxBlock)item).findParam("index");
                  if (indexParam != null) {
                     location.setRootPage(indexParam.getValue());
                  }

                  location.setType(1);
                  break;
               }
            }

            location.setLocationParamJson((String)null);
            locations.add(location);
         }
      }

      FileUtil.del(initNginxPath);
   }

   private String initNginx(String nginxPath) {
      List<String> lines = FileUtil.readLines(nginxPath, CharsetUtil.CHARSET_UTF_8);
      List<String> rs = new ArrayList();
      Iterator var4 = lines.iterator();

      while(var4.hasNext()) {
         String str = (String)var4.next();
         if (str.trim().indexOf("#") != 0) {
            rs.add(str);
         }
      }

      String initNginxPath = FileUtil.getTmpDirPath() + UUID.randomUUID().toString();
      FileUtil.writeLines(rs, (String)initNginxPath, (Charset)CharsetUtil.CHARSET_UTF_8);
      return initNginxPath;
   }

   public void setSeq(String serverId, Integer seqAdd) {
      Server server = (Server)this.sqlHelper.findById(serverId, Server.class);
      List<Server> serverList = this.sqlHelper.findAll(new Sort("seq", Sort.Direction.DESC), Server.class);
      if (serverList.size() > 0) {
         Server tagert = null;
         int i;
         if (seqAdd < 0) {
            for(i = 0; i < serverList.size(); ++i) {
               if (((Server)serverList.get(i)).getSeq() < server.getSeq()) {
                  tagert = (Server)serverList.get(i);
                  break;
               }
            }
         } else {
            System.out.println(server.getSeq());

            for(i = serverList.size() - 1; i >= 0; --i) {
               System.out.println(((Server)serverList.get(i)).getSeq());
               if (((Server)serverList.get(i)).getSeq() > server.getSeq()) {
                  tagert = (Server)serverList.get(i);
                  break;
               }
            }
         }

         if (tagert != null) {
            System.err.println("tagert:" + tagert.getServerName() + tagert.getListen());
            System.err.println("server:" + server.getServerName() + server.getListen());
            Long seq = tagert.getSeq();
            tagert.setSeq(server.getSeq());
            server.setSeq(seq);
            this.sqlHelper.updateById(tagert);
            this.sqlHelper.updateById(server);
         }
      }

   }

   public void moveLocation(String locationId, Integer seqAdd) {
      Location location = (Location)this.sqlHelper.findById(locationId, Location.class);
      List<Location> locationList = this.sqlHelper.findListByQuery((new ConditionAndWrapper()).eq((SerializableFunction)(Location::getServerId), location.getServerId()), new Sort("id", Sort.Direction.DESC), Location.class);
      if (locationList.size() > 0) {
         Location tagert = null;
         int i;
         if (seqAdd > 0) {
            for(i = 0; i < locationList.size(); ++i) {
               if (Long.parseLong(((Location)locationList.get(i)).getId()) < Long.parseLong(location.getId())) {
                  tagert = (Location)locationList.get(i);
               }
            }
         } else {
            for(i = locationList.size() - 1; i >= 0; --i) {
               if (Long.parseLong(((Location)locationList.get(i)).getId()) > Long.parseLong(location.getId())) {
                  tagert = (Location)locationList.get(i);
               }
            }
         }

         if (tagert != null) {
            String id = tagert.getId();
            tagert.setId(location.getId());
            location.setId(id);
            this.sqlHelper.updateById(tagert);
            this.sqlHelper.updateById(location);
         }
      }

   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "getValue":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Location") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Location::getValue;
            }
            break;
         case "getDescr":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Server") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Server::getDescr;
            }

            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Location") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Location::getDescr;
            }
            break;
         case "getPath":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Location") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Location::getPath;
            }
            break;
         case "getServerId":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Location") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Location::getServerId;
            }

            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Location") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Location::getServerId;
            }
            break;
         case "getId":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/sqlhelper/bean/BaseModel") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return BaseModel::getId;
            }
            break;
         case "getServerName":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Server") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Server::getServerName;
            }
            break;
         case "getListen":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Server") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Server::getListen;
            }
      }

      throw new IllegalArgumentException("Invalid lambda deserialization");
   }
}
