package com.cym.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.crypto.SecureUtil;
import com.cym.model.Admin;
import com.cym.model.Basic;
import com.cym.model.Cert;
import com.cym.model.Http;
import com.cym.service.BasicService;
import com.cym.service.ConfService;
import com.cym.service.SettingService;
import com.cym.sqlhelper.reflection.SerializableFunction;
import com.cym.sqlhelper.utils.ConditionAndWrapper;
import com.cym.sqlhelper.utils.ConditionWrapper;
import com.cym.sqlhelper.utils.JdbcTemplate;
import com.cym.sqlhelper.utils.SqlHelper;
import com.cym.utils.EncodePassUtils;
import com.cym.utils.MessageUtils;
import com.cym.utils.NginxUtils;
import com.cym.utils.SystemTool;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.SerializedLambda;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class InitConfig {
   Logger logger = LoggerFactory.getLogger(this.getClass());
   @Inject
   MessageUtils m;
   @Inject
   HomeConfig homeConfig;
   @Inject
   VersionConfig versionConfig;
   @Inject
   SettingService settingService;
   @Inject
   BasicService basicService;
   @Inject
   SqlHelper sqlHelper;
   @Inject
   JdbcTemplate jdbcTemplate;
   @Inject
   ConfService confService;
   @Inject("${project.findPass}")
   Boolean findPass;

   @Init
   public void init() throws IOException {
      if (this.findPass) {
         List<Admin> admins = this.sqlHelper.findAll(Admin.class);
         Iterator var2 = admins.iterator();

         while(var2.hasNext()) {
            Admin admin = (Admin)var2.next();
            System.out.println(this.m.get("adminStr.name") + ":" + admin.getName() + " " + this.m.get("adminStr.pass") + ":" + EncodePassUtils.defaultPass);
            admin.setAuth(false);
            admin.setPass(EncodePassUtils.encodeDefaultPass());
            this.sqlHelper.updateById(admin);
         }

         System.exit(1);
      }

      Long count = this.sqlHelper.findAllCount(Basic.class);
      ArrayList https;
      if (count == 0L) {
         https = new ArrayList();
         https.add(new Basic("worker_processes", "auto", 1L));
         https.add(new Basic("events", "{\r\n    worker_connections  1024;\r\n    accept_mutex on;\r\n}", 2L));
         this.sqlHelper.insertAll(https);
      }

      count = this.sqlHelper.findAllCount(Http.class);
      if (count == 0L) {
         https = new ArrayList();
         https.add(new Http("include", "mime.types", 0L));
         https.add(new Http("default_type", "application/octet-stream", 1L));
         this.sqlHelper.insertAll(https);
      }

      ClassPathResource resource;
      if (!FileUtil.exist(this.homeConfig.home + "nginx.conf")) {
         resource = new ClassPathResource("nginx.conf");
         FileUtil.writeFromStream(resource.getStream(), this.homeConfig.home + "nginx.conf");
      }

      if (!FileUtil.exist(this.homeConfig.home + "mime.types")) {
         resource = new ClassPathResource("mime.types");
         FileUtil.writeFromStream(resource.getStream(), this.homeConfig.home + "mime.types");
      }

      String nginxPath = this.settingService.get("nginxPath");
      if (StrUtil.isEmpty(nginxPath)) {
         nginxPath = this.homeConfig.home + "nginx.conf";
         this.settingService.set("nginxPath", nginxPath);
      }

      ClassPathResource resource = new ClassPathResource("acme.zip");
      InputStream inputStream = resource.getStream();
      FileUtil.writeFromStream(inputStream, this.homeConfig.home + "acme.zip");
      FileUtil.mkdir(this.homeConfig.acmeShDir);
      ZipUtil.unzip(this.homeConfig.home + "acme.zip", this.homeConfig.acmeShDir);
      FileUtil.del(this.homeConfig.home + "acme.zip");
      List<String> res = FileUtil.readUtf8Lines(this.homeConfig.acmeSh);

      for(int i = 0; i < res.size(); ++i) {
         if (((String)res.get(i)).contains("DEFAULT_INSTALL_HOME=\"$HOME/.$PROJECT_NAME\"")) {
            res.set(i, "DEFAULT_INSTALL_HOME=\"" + this.homeConfig.acmeShDir + "\"");
         }
      }

      FileUtil.writeUtf8Lines(res, (String)this.homeConfig.acmeSh);
      Iterator var7;
      List list;
      if (SystemTool.isLinux()) {
         RuntimeUtil.exec("chmod a+x " + this.homeConfig.acmeSh);
         String cmd;
         if (!this.basicService.contain("ngx_stream_module.so")) {
            if (FileUtil.exist("/usr/lib/nginx/modules/ngx_stream_module.so")) {
               Basic basic = new Basic("load_module", "/usr/lib/nginx/modules/ngx_stream_module.so", -10L);
               this.sqlHelper.insert(basic);
            } else {
               this.logger.info(this.m.get("commonStr.ngxStream"));
               list = RuntimeUtil.execForLines(CharsetUtil.systemCharset(), "find / -name ngx_stream_module.so");
               var7 = list.iterator();

               while(var7.hasNext()) {
                  cmd = (String)var7.next();
                  if (cmd.contains("ngx_stream_module.so") && cmd.length() < 80) {
                     Basic basic = new Basic("load_module", cmd, -10L);
                     this.sqlHelper.insert(basic);
                     break;
                  }
               }
            }
         }

         if (this.hasNginx()) {
            this.settingService.set("nginxExe", "nginx");
         }

         String nginxExe = this.settingService.get("nginxExe");
         String nginxDir = this.settingService.get("nginxDir");
         this.logger.info("nginxIsRun:" + NginxUtils.isRun());
         if (!NginxUtils.isRun() && StrUtil.isNotEmpty(nginxExe) && StrUtil.isNotEmpty(nginxPath)) {
            cmd = nginxExe + " -c " + nginxPath;
            if (StrUtil.isNotEmpty(nginxDir)) {
               cmd = cmd + " -p " + nginxDir;
            }

            this.logger.info("runCmd:" + cmd);
            RuntimeUtil.execForStr("/bin/sh", "-c", cmd);
         }
      }

      list = this.confService.getApplyCerts();
      var7 = list.iterator();

      Cert cert;
      while(var7.hasNext()) {
         cert = (Cert)var7.next();
         boolean update = false;
         if (cert.getPem() != null && cert.getPem().equals(this.homeConfig.home + "cert/" + cert.getDomain() + ".fullchain.cer")) {
            cert.setPem(this.homeConfig.acmeShDir + cert.getDomain() + "/fullchain.cer");
            update = true;
         }

         if (cert.getKey() != null && cert.getKey().equals(this.homeConfig.home + "cert/" + cert.getDomain() + ".key")) {
            cert.setKey(this.homeConfig.acmeShDir + cert.getDomain() + "/" + cert.getDomain() + ".key");
            update = true;
         }

         if (update) {
            this.sqlHelper.updateById(cert);
         }
      }

      list = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((SerializableFunction)(Cert::getEncryption), "RAS"), Cert.class);
      var7 = list.iterator();

      while(var7.hasNext()) {
         cert = (Cert)var7.next();
         cert.setEncryption("RSA");
         this.sqlHelper.updateById(cert);
      }

      List<Admin> admins = this.sqlHelper.findAll(Admin.class);
      Iterator var22 = admins.iterator();

      while(var22.hasNext()) {
         Admin admin = (Admin)var22.next();
         if (!StrUtil.endWith(admin.getPass(), SecureUtil.md5(EncodePassUtils.defaultPass))) {
            admin.setPass(EncodePassUtils.encode(admin.getPass()));
            this.sqlHelper.updateById(admin);
         }
      }

      this.showLogo();
   }

   private boolean hasNginx() {
      String rs = RuntimeUtil.execForStr("which nginx");
      return StrUtil.isNotEmpty(rs);
   }

   private void showLogo() throws IOException {
      ClassPathResource resource = new ClassPathResource("banner.txt");
      BufferedReader reader = resource.getReader(Charset.forName("utf-8"));
      String str = null;
      StringBuilder stringBuilder = new StringBuilder();

      while(null != (str = reader.readLine())) {
         stringBuilder.append(str + "\n");
      }

      reader.close();
      stringBuilder.append("nginxWebUI " + this.versionConfig.currentVersion + "\n");
      this.logger.info(stringBuilder.toString());
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "getEncryption":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Cert") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Cert::getEncryption;
            }
         default:
            throw new IllegalArgumentException("Invalid lambda deserialization");
      }
   }
}
