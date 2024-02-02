/*     */ package com.cym.task;
/*     */ 
/*     */ import cn.hutool.core.date.DateTime;
/*     */ import cn.hutool.core.date.DateUtil;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.lang.TypeReference;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.ZipUtil;
/*     */ import cn.hutool.http.HttpUtil;
/*     */ import cn.hutool.json.JSONUtil;
/*     */ import com.cym.config.HomeConfig;
/*     */ import com.cym.controller.adminPage.CertController;
/*     */ import com.cym.controller.adminPage.ConfController;
/*     */ import com.cym.controller.adminPage.RemoteController;
/*     */ import com.cym.model.Cert;
/*     */ import com.cym.model.Http;
/*     */ import com.cym.model.Remote;
/*     */ import com.cym.model.Upstream;
/*     */ import com.cym.model.UpstreamServer;
/*     */ import com.cym.service.HttpService;
/*     */ import com.cym.service.LogService;
/*     */ import com.cym.service.RemoteService;
/*     */ import com.cym.service.SettingService;
/*     */ import com.cym.service.UpstreamService;
/*     */ import com.cym.sqlhelper.utils.SqlHelper;
/*     */ import com.cym.utils.BLogFileTailer;
/*     */ import com.cym.utils.MessageUtils;
/*     */ import com.cym.utils.SendMailUtils;
/*     */ import com.cym.utils.TelnetUtils;
/*     */ import java.io.File;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.noear.solon.annotation.Component;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.schedule.annotation.Scheduled;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Component
/*     */ public class ScheduleTask
/*     */ {
/*  50 */   Logger logger = LoggerFactory.getLogger(getClass());
/*     */   
/*     */   @Inject("${server.port}")
/*     */   Integer port;
/*     */   @Inject("${solon.logging.appender.file.maxHistory}")
/*     */   Integer maxHistory;
/*     */   @Inject
/*     */   SqlHelper sqlHelper;
/*     */   @Inject
/*     */   CertController certController;
/*     */   @Inject
/*     */   SettingService settingService;
/*     */   @Inject
/*     */   ConfController confController;
/*     */   @Inject
/*     */   RemoteController remoteController;
/*     */   @Inject
/*     */   RemoteService remoteService;
/*     */   @Inject
/*     */   UpstreamService upstreamService;
/*     */   @Inject
/*     */   LogService logInfoService;
/*     */   @Inject
/*     */   SendMailUtils sendMailUtils;
/*     */   @Inject
/*     */   HttpService httpService;
/*     */   @Inject
/*     */   MessageUtils m;
/*     */   @Inject
/*     */   HomeConfig homeConfig;
/*     */   @Inject
/*     */   BLogFileTailer bLogFileTailer;
/*     */   
/*     */   @Scheduled(cron = "0 0 2 * * ?")
/*     */   public void certTasks() {
/*  85 */     List<Cert> certList = this.sqlHelper.findAll(Cert.class);
/*     */ 
/*     */     
/*  88 */     long time = System.currentTimeMillis();
/*  89 */     for (Cert cert : certList) {
/*     */       
/*  91 */       if (cert.getMakeTime() != null && cert.getAutoRenew().intValue() == 1 && time - cert.getMakeTime().longValue() > TimeUnit.DAYS.toMillis(50L)) {
/*  92 */         this.certController.apply(cert.getId(), "renew");
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Scheduled(cron = "0 55 23 * * ?")
/*     */   public void diviLog() {
/* 100 */     Http access = this.httpService.getName("access_log");
/* 101 */     if (access != null) {
/* 102 */       cutLog(access);
/*     */     }
/*     */     
/* 105 */     Http error = this.httpService.getName("error_log");
/* 106 */     if (access != null) {
/* 107 */       cutLog(error);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void cutLog(Http http) {
/* 113 */     String path = http.getValue();
/*     */     
/* 115 */     if (StrUtil.isNotEmpty(path)) {
/*     */       
/* 117 */       path = path.split(" ")[0];
/* 118 */       if (FileUtil.exist(path)) {
/* 119 */         String date = DateUtil.format(new Date(), "yyyy-MM-dd");
/*     */         
/* 121 */         File dist = new File(path + "." + date);
/* 122 */         FileUtil.move(new File(path), dist, true);
/* 123 */         ZipUtil.zip(dist.getPath(), dist.getPath() + ".zip", false);
/* 124 */         FileUtil.del(dist);
/*     */         
/* 126 */         this.confController.reload(null, null, null);
/*     */ 
/*     */         
/* 129 */         long time = System.currentTimeMillis();
/*     */         
/* 131 */         File dir = (new File(path)).getParentFile();
/* 132 */         for (File file : dir.listFiles()) {
/* 133 */           if (file.getName().contains((new File(path)).getName()) && file.getName().endsWith(".zip")) {
/* 134 */             String[] array = file.getName().split("[.]");
/* 135 */             String dateStr = array[array.length - 2];
/* 136 */             DateTime dateTime = DateUtil.parse(dateStr, "yyyy-MM-dd");
/* 137 */             if (time - dateTime.getTime() > TimeUnit.DAYS.toMillis(this.maxHistory.intValue())) {
/* 138 */               FileUtil.del(file);
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Scheduled(cron = "0/30 * * * * ?")
/*     */   public void nginxTasks() {
/* 151 */     String lastNginxSend = this.settingService.get("lastNginxSend");
/* 152 */     String mail = this.settingService.get("mail");
/* 153 */     String nginxMonitor = this.settingService.get("nginxMonitor");
/* 154 */     String mailInterval = this.settingService.get("mail_interval");
/* 155 */     if (StrUtil.isEmpty(mailInterval)) {
/* 156 */       mailInterval = "30";
/*     */     }
/*     */     
/* 159 */     if ("true".equals(nginxMonitor) && StrUtil.isNotEmpty(mail) && (
/* 160 */       StrUtil.isEmpty(lastNginxSend) || System.currentTimeMillis() - Long.parseLong(lastNginxSend) > TimeUnit.MINUTES.toMillis(Integer.parseInt(mailInterval)))) {
/* 161 */       List<String> names = new ArrayList<>();
/*     */ 
/*     */       
/* 164 */       List<Remote> remoteList = this.remoteService.getMonitorRemoteList();
/* 165 */       for (Remote remote : remoteList) {
/*     */         try {
/* 167 */           String json = HttpUtil.get(remote.getProtocol() + "://" + remote.getIp() + ":" + remote.getPort() + "/adminPage/remote/version?creditKey=" + remote.getCreditKey(), 1000);
/* 168 */           Map<String, Object> map = (Map<String, Object>)JSONUtil.toBean(json, (new TypeReference<Map<String, Object>>() { 
/* 169 */               },  ).getType(), false);
/*     */           
/* 171 */           if (((Integer)map.get("nginx")).intValue() == 0 && remote.getMonitor().intValue() == 1) {
/* 172 */             names.add(remote.getDescr() + "[" + remote.getIp() + ":" + remote.getPort() + "]");
/*     */           }
/* 174 */         } catch (Exception e) {
/* 175 */           this.logger.error(e.getMessage(), e);
/*     */           
/* 177 */           names.add(remote.getDescr() + "[" + remote.getIp() + ":" + remote.getPort() + "]");
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 182 */       if ("1".equals(this.settingService.get("monitorLocal"))) {
/* 183 */         Map<String, Object> map = this.remoteController.version();
/* 184 */         if (((Integer)map.get("nginx")).intValue() == 0) {
/* 185 */           names.add(0, this.m.get("remoteStr.local") + "[127.0.0.1:" + this.port + "]");
/*     */         }
/*     */       } 
/*     */       
/* 189 */       if (names.size() > 0) {
/* 190 */         this.sendMailUtils.sendMailSmtp(mail, this.m.get("mailStr.nginxFail"), this.m.get("mailStr.nginxTips") + StrUtil.join(" ", names));
/* 191 */         this.settingService.set("lastNginxSend", String.valueOf(System.currentTimeMillis()));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Scheduled(cron = "0/30 * * * * ?")
/*     */   public void nodeTasks() {
/* 200 */     String lastUpstreamSend = this.settingService.get("lastUpstreamSend");
/* 201 */     String mail = this.settingService.get("mail");
/* 202 */     String upstreamMonitor = this.settingService.get("upstreamMonitor");
/* 203 */     String mailInterval = this.settingService.get("mail_interval");
/* 204 */     if (StrUtil.isEmpty(mailInterval)) {
/* 205 */       mailInterval = "30";
/*     */     }
/*     */     
/* 208 */     if ("true".equals(upstreamMonitor)) {
/*     */       
/* 210 */       List<UpstreamServer> upstreamServers = this.upstreamService.getAllServer();
/*     */       
/* 212 */       List<String> ips = new ArrayList<>();
/* 213 */       for (UpstreamServer upstreamServer : upstreamServers) {
/* 214 */         if (!TelnetUtils.isRunning(upstreamServer.getServer(), upstreamServer.getPort().intValue())) {
/* 215 */           Upstream upstream = (Upstream)this.sqlHelper.findById(upstreamServer.getUpstreamId(), Upstream.class);
/* 216 */           if (upstream.getMonitor().intValue() == 1 && StrUtil.isNotEmpty(mail) && (
/* 217 */             StrUtil.isEmpty(lastUpstreamSend) || System.currentTimeMillis() - Long.parseLong(lastUpstreamSend) > TimeUnit.MINUTES.toMillis(Integer.parseInt(mailInterval)))) {
/* 218 */             ips.add(upstreamServer.getServer() + ":" + upstreamServer.getPort());
/*     */           }
/* 220 */           upstreamServer.setMonitorStatus(Integer.valueOf(0));
/*     */         } else {
/* 222 */           upstreamServer.setMonitorStatus(Integer.valueOf(1));
/*     */         } 
/*     */         
/* 225 */         this.sqlHelper.updateById(upstreamServer);
/*     */       } 
/*     */       
/* 228 */       if (ips.size() > 0) {
/* 229 */         String dateStr = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
/* 230 */         if (this.settingService.get("lang") != null && this.settingService.get("lang").equals("en_US")) {
/*     */           
/* 232 */           SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.ENGLISH);
/* 233 */           dateStr = dateFormat.format(new Date());
/*     */         } 
/*     */         
/* 236 */         this.sendMailUtils.sendMailSmtp(mail, this.m.get("mailStr.upstreamFail"), this.m.get("mailStr.upstreamTips") + StrUtil.join(" ", ips) + "\r\n" + dateStr);
/* 237 */         this.settingService.set("lastUpstreamSend", String.valueOf(System.currentTimeMillis()));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\task\ScheduleTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */