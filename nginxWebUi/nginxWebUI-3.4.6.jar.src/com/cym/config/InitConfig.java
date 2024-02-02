/*     */ package com.cym.config;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.resource.ClassPathResource;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.RuntimeUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.ZipUtil;
/*     */ import cn.hutool.crypto.SecureUtil;
/*     */ import com.cym.model.Admin;
/*     */ import com.cym.model.Basic;
/*     */ import com.cym.model.Cert;
/*     */ import com.cym.model.Http;
/*     */ import com.cym.service.BasicService;
/*     */ import com.cym.service.ConfService;
/*     */ import com.cym.service.SettingService;
/*     */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*     */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*     */ import com.cym.sqlhelper.utils.JdbcTemplate;
/*     */ import com.cym.sqlhelper.utils.SqlHelper;
/*     */ import com.cym.utils.EncodePassUtils;
/*     */ import com.cym.utils.MessageUtils;
/*     */ import com.cym.utils.NginxUtils;
/*     */ import com.cym.utils.SystemTool;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.invoke.SerializedLambda;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.noear.solon.annotation.Component;
/*     */ import org.noear.solon.annotation.Init;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ @Component
/*     */ public class InitConfig
/*     */ {
/*  42 */   Logger logger = LoggerFactory.getLogger(getClass());
/*     */   
/*     */   @Inject
/*     */   MessageUtils m;
/*     */   
/*     */   @Inject
/*     */   HomeConfig homeConfig;
/*     */   
/*     */   @Inject
/*     */   VersionConfig versionConfig;
/*     */   
/*     */   @Inject
/*     */   SettingService settingService;
/*     */   
/*     */   @Inject
/*     */   BasicService basicService;
/*     */   
/*     */   @Inject
/*     */   SqlHelper sqlHelper;
/*     */   @Inject
/*     */   JdbcTemplate jdbcTemplate;
/*     */   @Inject
/*     */   ConfService confService;
/*     */   @Inject("${project.findPass}")
/*     */   Boolean findPass;
/*     */   
/*     */   @Init
/*     */   public void init() throws IOException {
/*  70 */     if (this.findPass.booleanValue()) {
/*  71 */       List<Admin> list = this.sqlHelper.findAll(Admin.class);
/*  72 */       for (Admin admin : list) {
/*  73 */         System.out.println(this.m.get("adminStr.name") + ":" + admin.getName() + " " + this.m.get("adminStr.pass") + ":" + EncodePassUtils.defaultPass);
/*  74 */         admin.setAuth(Boolean.valueOf(false));
/*  75 */         admin.setPass(EncodePassUtils.encodeDefaultPass());
/*  76 */         this.sqlHelper.updateById(admin);
/*     */       } 
/*  78 */       System.exit(1);
/*     */     } 
/*     */ 
/*     */     
/*  82 */     Long count = this.sqlHelper.findAllCount(Basic.class);
/*  83 */     if (count.longValue() == 0L) {
/*  84 */       List<Basic> basics = new ArrayList<>();
/*  85 */       basics.add(new Basic("worker_processes", "auto", Long.valueOf(1L)));
/*  86 */       basics.add(new Basic("events", "{\r\n    worker_connections  1024;\r\n    accept_mutex on;\r\n}", Long.valueOf(2L)));
/*  87 */       this.sqlHelper.insertAll(basics);
/*     */     } 
/*     */ 
/*     */     
/*  91 */     count = this.sqlHelper.findAllCount(Http.class);
/*  92 */     if (count.longValue() == 0L) {
/*  93 */       List<Http> https = new ArrayList<>();
/*  94 */       https.add(new Http("include", "mime.types", Long.valueOf(0L)));
/*  95 */       https.add(new Http("default_type", "application/octet-stream", Long.valueOf(1L)));
/*  96 */       this.sqlHelper.insertAll(https);
/*     */     } 
/*     */ 
/*     */     
/* 100 */     if (!FileUtil.exist(this.homeConfig.home + "nginx.conf")) {
/* 101 */       ClassPathResource classPathResource = new ClassPathResource("nginx.conf");
/* 102 */       FileUtil.writeFromStream(classPathResource.getStream(), this.homeConfig.home + "nginx.conf");
/*     */     } 
/* 104 */     if (!FileUtil.exist(this.homeConfig.home + "mime.types")) {
/* 105 */       ClassPathResource classPathResource = new ClassPathResource("mime.types");
/* 106 */       FileUtil.writeFromStream(classPathResource.getStream(), this.homeConfig.home + "mime.types");
/*     */     } 
/*     */ 
/*     */     
/* 110 */     String nginxPath = this.settingService.get("nginxPath");
/* 111 */     if (StrUtil.isEmpty(nginxPath)) {
/* 112 */       nginxPath = this.homeConfig.home + "nginx.conf";
/*     */       
/* 114 */       this.settingService.set("nginxPath", nginxPath);
/*     */     } 
/*     */ 
/*     */     
/* 118 */     ClassPathResource resource = new ClassPathResource("acme.zip");
/* 119 */     InputStream inputStream = resource.getStream();
/* 120 */     FileUtil.writeFromStream(inputStream, this.homeConfig.home + "acme.zip");
/* 121 */     FileUtil.mkdir(this.homeConfig.acmeShDir);
/* 122 */     ZipUtil.unzip(this.homeConfig.home + "acme.zip", this.homeConfig.acmeShDir);
/* 123 */     FileUtil.del(this.homeConfig.home + "acme.zip");
/*     */ 
/*     */     
/* 126 */     List<String> res = FileUtil.readUtf8Lines(this.homeConfig.acmeSh);
/* 127 */     for (int i = 0; i < res.size(); i++) {
/* 128 */       if (((String)res.get(i)).contains("DEFAULT_INSTALL_HOME=\"$HOME/.$PROJECT_NAME\"")) {
/* 129 */         res.set(i, "DEFAULT_INSTALL_HOME=\"" + this.homeConfig.acmeShDir + "\"");
/*     */       }
/*     */     } 
/* 132 */     FileUtil.writeUtf8Lines(res, this.homeConfig.acmeSh);
/*     */     
/* 134 */     if (SystemTool.isLinux().booleanValue()) {
/* 135 */       RuntimeUtil.exec(new String[] { "chmod a+x " + this.homeConfig.acmeSh });
/*     */ 
/*     */       
/* 138 */       if (!this.basicService.contain("ngx_stream_module.so")) {
/* 139 */         if (FileUtil.exist("/usr/lib/nginx/modules/ngx_stream_module.so")) {
/* 140 */           Basic basic = new Basic("load_module", "/usr/lib/nginx/modules/ngx_stream_module.so", Long.valueOf(-10L));
/* 141 */           this.sqlHelper.insert(basic);
/*     */         } else {
/* 143 */           this.logger.info(this.m.get("commonStr.ngxStream"));
/* 144 */           List<String> list = RuntimeUtil.execForLines(CharsetUtil.systemCharset(), new String[] { "find / -name ngx_stream_module.so" });
/* 145 */           for (String path : list) {
/* 146 */             if (path.contains("ngx_stream_module.so") && path.length() < 80) {
/* 147 */               Basic basic = new Basic("load_module", path, Long.valueOf(-10L));
/* 148 */               this.sqlHelper.insert(basic);
/*     */               
/*     */               break;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       }
/*     */       
/* 156 */       if (hasNginx())
/*     */       {
/* 158 */         this.settingService.set("nginxExe", "nginx");
/*     */       }
/*     */ 
/*     */       
/* 162 */       String nginxExe = this.settingService.get("nginxExe");
/* 163 */       String nginxDir = this.settingService.get("nginxDir");
/*     */       
/* 165 */       this.logger.info("nginxIsRun:" + NginxUtils.isRun());
/* 166 */       if (!NginxUtils.isRun() && StrUtil.isNotEmpty(nginxExe) && StrUtil.isNotEmpty(nginxPath)) {
/* 167 */         String cmd = nginxExe + " -c " + nginxPath;
/*     */         
/* 169 */         if (StrUtil.isNotEmpty(nginxDir)) {
/* 170 */           cmd = cmd + " -p " + nginxDir;
/*     */         }
/* 172 */         this.logger.info("runCmd:" + cmd);
/* 173 */         RuntimeUtil.execForStr(new String[] { "/bin/sh", "-c", cmd });
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 178 */     List<Cert> certs = this.confService.getApplyCerts();
/* 179 */     for (Cert cert : certs) {
/* 180 */       boolean update = false;
/* 181 */       if (cert.getPem() != null && cert.getPem().equals(this.homeConfig.home + "cert/" + cert.getDomain() + ".fullchain.cer")) {
/* 182 */         cert.setPem(this.homeConfig.acmeShDir + cert.getDomain() + "/fullchain.cer");
/* 183 */         update = true;
/*     */       } 
/* 185 */       if (cert.getKey() != null && cert.getKey().equals(this.homeConfig.home + "cert/" + cert.getDomain() + ".key")) {
/* 186 */         cert.setKey(this.homeConfig.acmeShDir + cert.getDomain() + "/" + cert.getDomain() + ".key");
/* 187 */         update = true;
/*     */       } 
/*     */       
/* 190 */       if (update) {
/* 191 */         this.sqlHelper.updateById(cert);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 196 */     certs = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(Cert::getEncryption, "RAS"), Cert.class);
/* 197 */     for (Cert cert : certs) {
/* 198 */       cert.setEncryption("RSA");
/* 199 */       this.sqlHelper.updateById(cert);
/*     */     } 
/*     */ 
/*     */     
/* 203 */     List<Admin> admins = this.sqlHelper.findAll(Admin.class);
/* 204 */     for (Admin admin : admins) {
/* 205 */       if (!StrUtil.endWith(admin.getPass(), SecureUtil.md5(EncodePassUtils.defaultPass))) {
/* 206 */         admin.setPass(EncodePassUtils.encode(admin.getPass()));
/* 207 */         this.sqlHelper.updateById(admin);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 212 */     showLogo();
/*     */   }
/*     */   
/*     */   private boolean hasNginx() {
/* 216 */     String rs = RuntimeUtil.execForStr(new String[] { "which nginx" });
/* 217 */     if (StrUtil.isNotEmpty(rs)) {
/* 218 */       return true;
/*     */     }
/*     */     
/* 221 */     return false;
/*     */   }
/*     */   
/*     */   private void showLogo() throws IOException {
/* 225 */     ClassPathResource resource = new ClassPathResource("banner.txt");
/* 226 */     BufferedReader reader = resource.getReader(Charset.forName("utf-8"));
/* 227 */     String str = null;
/* 228 */     StringBuilder stringBuilder = new StringBuilder();
/*     */     
/* 230 */     while (null != (str = reader.readLine())) {
/* 231 */       stringBuilder.append(str + "\n");
/*     */     }
/* 233 */     reader.close();
/*     */     
/* 235 */     stringBuilder.append("nginxWebUI " + this.versionConfig.currentVersion + "\n");
/*     */     
/* 237 */     this.logger.info(stringBuilder.toString());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\config\InitConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */