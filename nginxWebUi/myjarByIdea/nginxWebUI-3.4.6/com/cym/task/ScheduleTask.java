package com.cym.task;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.cym.config.HomeConfig;
import com.cym.controller.adminPage.CertController;
import com.cym.controller.adminPage.ConfController;
import com.cym.controller.adminPage.RemoteController;
import com.cym.model.Cert;
import com.cym.model.Http;
import com.cym.model.Remote;
import com.cym.model.Upstream;
import com.cym.model.UpstreamServer;
import com.cym.service.HttpService;
import com.cym.service.LogService;
import com.cym.service.RemoteService;
import com.cym.service.SettingService;
import com.cym.service.UpstreamService;
import com.cym.sqlhelper.utils.SqlHelper;
import com.cym.utils.BLogFileTailer;
import com.cym.utils.MessageUtils;
import com.cym.utils.SendMailUtils;
import com.cym.utils.TelnetUtils;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.schedule.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ScheduleTask {
   Logger logger = LoggerFactory.getLogger(this.getClass());
   @Inject("${server.port}")
   Integer port;
   @Inject("${solon.logging.appender.file.maxHistory}")
   Integer maxHistory;
   @Inject
   SqlHelper sqlHelper;
   @Inject
   CertController certController;
   @Inject
   SettingService settingService;
   @Inject
   ConfController confController;
   @Inject
   RemoteController remoteController;
   @Inject
   RemoteService remoteService;
   @Inject
   UpstreamService upstreamService;
   @Inject
   LogService logInfoService;
   @Inject
   SendMailUtils sendMailUtils;
   @Inject
   HttpService httpService;
   @Inject
   MessageUtils m;
   @Inject
   HomeConfig homeConfig;
   @Inject
   BLogFileTailer bLogFileTailer;

   @Scheduled(
      cron = "0 0 2 * * ?"
   )
   public void certTasks() {
      List<Cert> certList = this.sqlHelper.findAll(Cert.class);
      long time = System.currentTimeMillis();
      Iterator var4 = certList.iterator();

      while(var4.hasNext()) {
         Cert cert = (Cert)var4.next();
         if (cert.getMakeTime() != null && cert.getAutoRenew() == 1 && time - cert.getMakeTime() > TimeUnit.DAYS.toMillis(50L)) {
            this.certController.apply(cert.getId(), "renew");
         }
      }

   }

   @Scheduled(
      cron = "0 55 23 * * ?"
   )
   public void diviLog() {
      Http access = this.httpService.getName("access_log");
      if (access != null) {
         this.cutLog(access);
      }

      Http error = this.httpService.getName("error_log");
      if (access != null) {
         this.cutLog(error);
      }

   }

   private void cutLog(Http http) {
      String path = http.getValue();
      if (StrUtil.isNotEmpty(path)) {
         path = path.split(" ")[0];
         if (FileUtil.exist(path)) {
            String date = DateUtil.format(new Date(), "yyyy-MM-dd");
            File dist = new File(path + "." + date);
            FileUtil.move(new File(path), dist, true);
            ZipUtil.zip(dist.getPath(), dist.getPath() + ".zip", false);
            FileUtil.del(dist);
            this.confController.reload((String)null, (String)null, (String)null);
            long time = System.currentTimeMillis();
            File dir = (new File(path)).getParentFile();
            File[] var8 = dir.listFiles();
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               File file = var8[var10];
               if (file.getName().contains((new File(path)).getName()) && file.getName().endsWith(".zip")) {
                  String[] array = file.getName().split("[.]");
                  String dateStr = array[array.length - 2];
                  DateTime dateTime = DateUtil.parse((CharSequence)dateStr, (String)"yyyy-MM-dd");
                  if (time - dateTime.getTime() > TimeUnit.DAYS.toMillis((long)this.maxHistory)) {
                     FileUtil.del(file);
                  }
               }
            }
         }
      }

   }

   @Scheduled(
      cron = "0/30 * * * * ?"
   )
   public void nginxTasks() {
      String lastNginxSend = this.settingService.get("lastNginxSend");
      String mail = this.settingService.get("mail");
      String nginxMonitor = this.settingService.get("nginxMonitor");
      String mailInterval = this.settingService.get("mail_interval");
      if (StrUtil.isEmpty(mailInterval)) {
         mailInterval = "30";
      }

      if ("true".equals(nginxMonitor) && StrUtil.isNotEmpty(mail) && (StrUtil.isEmpty(lastNginxSend) || System.currentTimeMillis() - Long.parseLong(lastNginxSend) > TimeUnit.MINUTES.toMillis((long)Integer.parseInt(mailInterval)))) {
         List<String> names = new ArrayList();
         List<Remote> remoteList = this.remoteService.getMonitorRemoteList();
         Iterator var7 = remoteList.iterator();

         while(var7.hasNext()) {
            Remote remote = (Remote)var7.next();

            try {
               String json = HttpUtil.get(remote.getProtocol() + "://" + remote.getIp() + ":" + remote.getPort() + "/adminPage/remote/version?creditKey=" + remote.getCreditKey(), 1000);
               Map<String, Object> map = (Map)JSONUtil.toBean(json, (new TypeReference<Map<String, Object>>() {
               }).getType(), false);
               if ((Integer)map.get("nginx") == 0 && remote.getMonitor() == 1) {
                  names.add(remote.getDescr() + "[" + remote.getIp() + ":" + remote.getPort() + "]");
               }
            } catch (Exception var11) {
               this.logger.error((String)var11.getMessage(), (Throwable)var11);
               names.add(remote.getDescr() + "[" + remote.getIp() + ":" + remote.getPort() + "]");
            }
         }

         if ("1".equals(this.settingService.get("monitorLocal"))) {
            Map<String, Object> map = this.remoteController.version();
            if ((Integer)map.get("nginx") == 0) {
               names.add(0, this.m.get("remoteStr.local") + "[127.0.0.1:" + this.port + "]");
            }
         }

         if (names.size() > 0) {
            this.sendMailUtils.sendMailSmtp(mail, this.m.get("mailStr.nginxFail"), this.m.get("mailStr.nginxTips") + StrUtil.join(" ", names));
            this.settingService.set("lastNginxSend", String.valueOf(System.currentTimeMillis()));
         }
      }

   }

   @Scheduled(
      cron = "0/30 * * * * ?"
   )
   public void nodeTasks() {
      String lastUpstreamSend = this.settingService.get("lastUpstreamSend");
      String mail = this.settingService.get("mail");
      String upstreamMonitor = this.settingService.get("upstreamMonitor");
      String mailInterval = this.settingService.get("mail_interval");
      if (StrUtil.isEmpty(mailInterval)) {
         mailInterval = "30";
      }

      if ("true".equals(upstreamMonitor)) {
         List<UpstreamServer> upstreamServers = this.upstreamService.getAllServer();
         List<String> ips = new ArrayList();

         UpstreamServer upstreamServer;
         for(Iterator var7 = upstreamServers.iterator(); var7.hasNext(); this.sqlHelper.updateById(upstreamServer)) {
            upstreamServer = (UpstreamServer)var7.next();
            if (TelnetUtils.isRunning(upstreamServer.getServer(), upstreamServer.getPort())) {
               upstreamServer.setMonitorStatus(1);
            } else {
               Upstream upstream = (Upstream)this.sqlHelper.findById(upstreamServer.getUpstreamId(), Upstream.class);
               if (upstream.getMonitor() == 1 && StrUtil.isNotEmpty(mail) && (StrUtil.isEmpty(lastUpstreamSend) || System.currentTimeMillis() - Long.parseLong(lastUpstreamSend) > TimeUnit.MINUTES.toMillis((long)Integer.parseInt(mailInterval)))) {
                  ips.add(upstreamServer.getServer() + ":" + upstreamServer.getPort());
               }

               upstreamServer.setMonitorStatus(0);
            }
         }

         if (ips.size() > 0) {
            String dateStr = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
            if (this.settingService.get("lang") != null && this.settingService.get("lang").equals("en_US")) {
               SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.ENGLISH);
               dateStr = dateFormat.format(new Date());
            }

            this.sendMailUtils.sendMailSmtp(mail, this.m.get("mailStr.upstreamFail"), this.m.get("mailStr.upstreamTips") + StrUtil.join(" ", ips) + "\r\n" + dateStr);
            this.settingService.set("lastUpstreamSend", String.valueOf(System.currentTimeMillis()));
         }
      }

   }
}
