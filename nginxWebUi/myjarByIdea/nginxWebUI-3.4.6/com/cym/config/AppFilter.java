package com.cym.config;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.cym.model.Admin;
import com.cym.model.Message;
import com.cym.model.Remote;
import com.cym.service.AdminService;
import com.cym.service.CreditService;
import com.cym.service.SettingService;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import com.cym.utils.MessageUtils;
import com.cym.utils.PropertiesUtils;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;
import org.noear.solon.core.handle.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AppFilter implements Filter {
   Logger logger = LoggerFactory.getLogger(this.getClass());
   @Inject
   AdminService adminService;
   @Inject
   MessageUtils m;
   @Inject
   CreditService creditService;
   @Inject("${solon.app.name}")
   String projectName;
   @Inject
   VersionConfig versionConfig;
   @Inject
   PropertiesUtils propertiesUtils;
   @Inject
   SettingService settingService;

   public void doFilter(Context ctx, FilterChain chain) throws Throwable {
      if (!ctx.path().contains("/lib/") && !ctx.path().contains("/js/") && !ctx.path().contains("/doc/") &&
              !ctx.path().contains("/img/") && !ctx.path().contains("/css/")) {
         this.frontInterceptor(ctx);
      }

      if (ctx.path().contains("/adminPage/") && !ctx.path().contains("/lib/") && !ctx.path().contains("/doc/") && !ctx.path().contains("/js/") && !ctx.path().contains("/img/") && !ctx.path().contains("/css/") && !this.adminInterceptor(ctx)) {
         ctx.setHandled(true);
      } else if (ctx.path().contains("/api/") && !ctx.path().contains("/lib/") && !ctx.path().contains("/doc/") && !ctx.path().contains("/js/") && !ctx.path().contains("/img/") && !ctx.path().contains("/css/") && !this.apiInterceptor(ctx)) {
         ctx.setHandled(true);
      } else {
         chain.doFilter(ctx);
      }
   }

   private boolean apiInterceptor(Context ctx) {
      String token = ctx.header("token");
      Admin admin = this.adminService.getByToken(token);
      if (admin != null && admin.getApi()) {
         return true;
      } else {
         JsonResult result = new JsonResult();
         result.setSuccess(false);
         result.setStatus("401");
         result.setMsg(this.m.get("apiStr.wrongToken"));
         ctx.output(JSONUtil.toJsonPrettyStr((Object)result));
         return false;
      }
   }

   private boolean adminInterceptor(Context ctx) {
      String ctxStr = this.getCtxStr(ctx);
      if (ctx.path().contains("adminPage/login")) {
         return true;
      } else {
         String creditKey = ctx.param("creditKey");
         Boolean isCredit = this.creditService.check(creditKey);
         Boolean isLogin = (Boolean)ctx.session("isLogin");
         if ((isLogin == null || !isLogin) && !isCredit) {
            ctx.redirect("/adminPage/login");
            return false;
         } else {
            String localType = (String)ctx.session("localType");
            if (localType != null && localType.equals("remote") && !ctx.path().contains("adminPage/remote") && !ctx.path().contains("adminPage/admin") && !ctx.path().contains("adminPage/about")) {
               Remote remote = (Remote)ctx.session("remote");
               String url = this.buildUrl(ctxStr, ctx, remote);

               try {
                  String rs = null;
                  if (url.contains("main/upload")) {
                     Map<String, Object> map = new HashMap();
                     map.put("creditKey", remote.getCreditKey());
                     UploadedFile uploadedFile = ctx.file("file");
                     File temp = new File(FileUtil.getTmpDir() + "/" + uploadedFile.name);
                     uploadedFile.transferTo(temp);
                     map.put("file", temp);
                     rs = HttpUtil.post(url, (Map)map);
                  } else {
                     Admin admin = (new BaseController()).getAdmin();
                     String body = this.buldBody(ctx.paramsMap(), remote, admin);
                     rs = HttpUtil.post(url, body);
                  }

                  ctx.charset("utf-8");
                  ctx.contentType("text/html;charset=utf-8");
                  if (JSONUtil.isTypeJSON(rs)) {
                     String date = DateUtil.format(new Date(), "yyyy-MM-dd_HH-mm-ss");
                     ctx.header("Content-Type", "application/octet-stream");
                     ctx.header("content-disposition", "attachment;filename=" + URLEncoder.encode(date + ".json", "UTF-8"));
                     byte[] buffer = new byte[1024];
                     BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(rs.getBytes(Charset.forName("UTF-8"))));
                     OutputStream os = ctx.outputStream();

                     for(int i = bis.read(buffer); i != -1; i = bis.read(buffer)) {
                        os.write(buffer, 0, i);
                     }
                  } else {
                     ctx.output(rs);
                  }
               } catch (Exception var15) {
                  this.logger.error((String)var15.getMessage(), (Throwable)var15);
                  ctx.redirect("/adminPage/login/noServer");
               }

               return false;
            } else {
               return true;
            }
         }
      }
   }

   private void frontInterceptor(Context ctx) {
      String ctxStr = this.getCtxStr(ctx);
      if (StrUtil.isNotEmpty(ctx.param("ctx"))) {
         ctxStr = Base64.decodeStr(ctx.param("ctx"));
      }

      ctx.attrSet("ctx", ctxStr);
      ctx.attrSet("jsrandom", this.versionConfig.currentVersion);
      ctx.attrSet("currentVersion", this.versionConfig.currentVersion);
      ctx.attrSet("projectName", this.projectName);
      ctx.attrSet("showAdmin", ctx.param("showAdmin"));
      ctx.attrSet("admin", ctx.session("admin"));
      if (this.versionConfig.newVersion != null) {
         ctx.attrSet("newVersion", this.versionConfig.newVersion);
         if (Integer.parseInt(this.versionConfig.currentVersion.replace(".", "").replace("v", "")) < Integer.parseInt(this.versionConfig.newVersion.getVersion().replace(".", "").replace("v", ""))) {
            ctx.attrSet("hasNewVersion", 1);
         }
      }

      Properties properties = null;
      String l = ctx.param("l");
      if ((!StrUtil.isNotEmpty(l) || !l.equals("en_US")) && (this.settingService.get("lang") == null || !this.settingService.get("lang").equals("en_US"))) {
         this.settingService.set("lang", "");
         properties = this.m.getProperties();
      } else {
         this.settingService.set("lang", "en_US");
         properties = this.m.getPropertiesEN();
      }

      Set<String> messageHeaders = new HashSet();
      List<Message> messages = new ArrayList();
      Iterator var7 = properties.stringPropertyNames().iterator();

      String key;
      while(var7.hasNext()) {
         key = (String)var7.next();
         Message message = new Message();
         message.setKey(key);
         message.setValue(properties.getProperty(key));
         messages.add(message);
         messageHeaders.add(key.split("\\.")[0]);
      }

      ctx.attrSet("messageHeaders", messageHeaders);
      ctx.attrSet("messages", messages);
      var7 = messageHeaders.iterator();

      while(var7.hasNext()) {
         key = (String)var7.next();
         Map<String, String> map = new HashMap();
         Iterator var10 = messages.iterator();

         while(var10.hasNext()) {
            Message message = (Message)var10.next();
            if (message.getKey().split("\\.")[0].equals(key)) {
               map.put(message.getKey().split("\\.")[1], message.getValue());
            }
         }

         ctx.attrSet(key, map);
      }

      if (this.settingService.get("lang") != null && this.settingService.get("lang").equals("en_US")) {
         ctx.attrSet("langType", "切换到中文");
      } else {
         ctx.attrSet("langType", "Switch to English");
      }

   }

   private String buldBody(Map<String, List<String>> parameterMap, Remote remote, Admin admin) throws UnsupportedEncodingException {
      List<String> body = new ArrayList();
      body.add("creditKey=" + remote.getCreditKey());
      if (admin != null) {
         body.add("adminName=" + admin.getName());
      }

      Iterator itr = parameterMap.entrySet().iterator();

      while(itr.hasNext()) {
         Map.Entry me = (Map.Entry)itr.next();
         Iterator var7 = ((List)me.getValue()).iterator();

         while(var7.hasNext()) {
            String value = (String)var7.next();
            body.add(me.getKey() + "=" + URLEncoder.encode(value, "UTF-8"));
         }
      }

      return StrUtil.join("&", body);
   }

   private String buildUrl(String ctxStr, Context ctx, Remote remote) {
      String url = ctx.url().replace(ctxStr, "//" + remote.getIp() + ":" + remote.getPort() + "/");
      if (url.startsWith("http")) {
         url = url.replace("http:", "").replace("https:", "");
      }

      url = remote.getProtocol() + ":" + url;
      Admin admin = (Admin)ctx.session("admin");
      String showAdmin = "false";
      if (admin != null && admin.getType() == 0) {
         showAdmin = "true";
      }

      return url + "?jsrandom=" + System.currentTimeMillis() + "&protocol=" + remote.getProtocol() + "&showAdmin=" + showAdmin + "&ctx=" + Base64.encode((CharSequence)ctxStr);
   }

   public String getCtxStr(Context context) {
      String httpHost = context.header("X-Forwarded-Host");
      String realPort = context.header("X-Forwarded-Port");
      String host = context.header("Host");
      String ctx = "//";
      if (StrUtil.isNotEmpty(httpHost)) {
         ctx = ctx + httpHost;
      } else if (StrUtil.isNotEmpty(host)) {
         ctx = ctx + host;
         if (!host.contains(":") && StrUtil.isNotEmpty(realPort)) {
            ctx = ctx + ":" + realPort;
         }
      } else {
         host = context.url().split("/")[2];
         ctx = ctx + host;
         if (!host.contains(":") && StrUtil.isNotEmpty(realPort)) {
            ctx = ctx + ":" + realPort;
         }
      }

      return ctx;
   }
}
