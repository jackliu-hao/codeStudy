package com.cym.controller.adminPage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.cym.model.Cert;
import com.cym.model.CertCode;
import com.cym.service.CertService;
import com.cym.service.SettingService;
import com.cym.sqlhelper.bean.Page;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import com.cym.utils.SystemTool;
import com.cym.utils.TimeExeUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.DownloadedFile;
import org.noear.solon.core.handle.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@Mapping("/adminPage/cert")
public class CertController extends BaseController {
   @Inject
   SettingService settingService;
   @Inject
   CertService certService;
   @Inject
   TimeExeUtils timeExeUtils;
   @Inject
   ConfController confController;
   Logger logger = LoggerFactory.getLogger(this.getClass());
   Boolean isInApply = false;

   @Mapping("")
   public ModelAndView index(ModelAndView modelAndView, Page page, String keywords) {
      page = this.certService.getPage(keywords, page);
      Iterator var4 = page.getRecords().iterator();

      while(var4.hasNext()) {
         Cert cert = (Cert)var4.next();
         if (cert.getType() == 0 || cert.getType() == 2) {
            cert.setDomain(cert.getDomain() + "(" + cert.getEncryption() + ")");
         }

         if (cert.getMakeTime() != null) {
            cert.setEndTime(cert.getMakeTime() + 7776000000L);
         }
      }

      modelAndView.put("keywords", keywords);
      modelAndView.put("page", page);
      modelAndView.view("/adminPage/cert/index.html");
      return modelAndView;
   }

   @Mapping("addOver")
   public JsonResult addOver(Cert cert, String[] domains, String[] types, String[] values) {
      Integer type = cert.getType();
      if (type == null && StrUtil.isNotEmpty(cert.getId())) {
         Cert certOrg = (Cert)this.sqlHelper.findById(cert.getId(), Cert.class);
         type = certOrg.getType();
      }

      if (type != null && type == 1) {
         String pemName;
         if (cert.getKey().contains(FileUtil.getTmpDir().toString().replace("\\", "/"))) {
            pemName = (new File(cert.getKey())).getName();
            FileUtil.move(new File(cert.getKey()), new File(this.homeConfig.home + "cert/" + pemName), true);
            cert.setKey(this.homeConfig.home + "cert/" + pemName);
         }

         if (cert.getPem().contains(FileUtil.getTmpDir().toString().replace("\\", "/"))) {
            pemName = (new File(cert.getPem())).getName();
            FileUtil.move(new File(cert.getPem()), new File(this.homeConfig.home + "cert/"), true);
            cert.setPem(this.homeConfig.home + "cert/" + pemName);
         }
      }

      this.certService.insertOrUpdate(cert, domains, types, values);
      return this.renderSuccess(cert);
   }

   @Mapping("setAutoRenew")
   public JsonResult setAutoRenew(Cert cert) {
      this.sqlHelper.updateById(cert);
      return this.renderSuccess();
   }

   @Mapping("detail")
   public JsonResult detail(String id) {
      return this.renderSuccess(this.sqlHelper.findById(id, Cert.class));
   }

   @Mapping("del")
   public JsonResult del(String id) {
      Cert cert = (Cert)this.sqlHelper.findById(id, Cert.class);
      if (cert.getType() == 1) {
         if (cert.getPem().contains(this.homeConfig.home + "cert/")) {
            FileUtil.del(cert.getPem());
         }

         if (cert.getKey().contains(this.homeConfig.home + "cert/")) {
            FileUtil.del(cert.getKey());
         }
      } else {
         String domain = cert.getDomain().split(",")[0];
         String path = this.homeConfig.acmeShDir + domain;
         if ("ECC".equals(cert.getEncryption())) {
            path = path + "_ecc";
         }

         if (FileUtil.exist(path)) {
            FileUtil.del(path);
         }
      }

      this.sqlHelper.deleteById(id, Cert.class);
      return this.renderSuccess();
   }

   @Mapping("apply")
   public JsonResult apply(String id, String type) {
      if (!SystemTool.isLinux()) {
         return this.renderError(this.m.get("certStr.error2"));
      } else {
         Cert cert = (Cert)this.sqlHelper.findById(id, Cert.class);
         if (cert.getDnsType() == null) {
            return this.renderError(this.m.get("certStr.error3"));
         } else if (this.isInApply) {
            return this.renderError(this.m.get("certStr.error4"));
         } else {
            this.isInApply = true;
            String keylength = "";
            String ecc = "";
            if ("ECC".equals(cert.getEncryption())) {
               keylength = " --keylength ec-256 ";
               ecc = " --ecc";
            }

            String rs = "";
            String cmd = "";
            String[] env = this.getEnv(cert);
            String domain;
            if (type.equals("issue")) {
               String[] split = cert.getDomain().split(",");
               StringBuffer sb = new StringBuffer();
               Arrays.stream(split).forEach((s) -> {
                  sb.append(" -d ").append(s);
               });
               String domain = sb.toString();
               if (cert.getType() == 0) {
                  String dnsType = "";
                  if (cert.getDnsType().equals("ali")) {
                     dnsType = "dns_ali";
                  } else if (cert.getDnsType().equals("dp")) {
                     dnsType = "dns_dp";
                  } else if (cert.getDnsType().equals("cf")) {
                     dnsType = "dns_cf";
                  } else if (cert.getDnsType().equals("gd")) {
                     dnsType = "dns_gd";
                  } else if (cert.getDnsType().equals("hw")) {
                     dnsType = "dns_huaweicloud";
                  }

                  cmd = this.homeConfig.acmeSh + " --issue --force --dns " + dnsType + domain + keylength + " --server letsencrypt";
               } else if (cert.getType() == 2) {
                  if (this.certService.hasCode(cert.getId())) {
                     cmd = this.homeConfig.acmeSh + " --renew --force --dns" + domain + " --server letsencrypt --yes-I-know-dns-manual-mode-enough-go-ahead-please";
                  } else {
                     cmd = this.homeConfig.acmeSh + " --issue --force --dns" + domain + keylength + " --server letsencrypt --yes-I-know-dns-manual-mode-enough-go-ahead-please";
                  }
               }
            } else if (type.equals("renew")) {
               domain = cert.getDomain().split(",")[0];
               if (cert.getType() == 0) {
                  cmd = this.homeConfig.acmeSh + " --renew --force " + ecc + " -d " + domain;
               } else if (cert.getType() == 2) {
                  cmd = this.homeConfig.acmeSh + " --renew --force " + ecc + " -d " + domain + " --server letsencrypt --yes-I-know-dns-manual-mode-enough-go-ahead-please";
               }
            }

            this.logger.info(cmd);
            rs = this.timeExeUtils.execCMD(cmd, env, 120000L);
            this.logger.info(rs);
            if (rs.contains("Your cert is in")) {
               domain = cert.getDomain().split(",")[0];
               String certDir = this.homeConfig.acmeShDir + domain;
               if ("ECC".equals(cert.getEncryption())) {
                  certDir = certDir + "_ecc";
               }

               certDir = certDir + "/";
               cert.setPem(certDir + "fullchain.cer");
               cert.setKey(certDir + domain + ".key");
               cert.setMakeTime(System.currentTimeMillis());
               this.sqlHelper.updateById(cert);
               if (type.equals("renew")) {
                  this.confController.reload((String)null, (String)null, (String)null);
               }

               this.isInApply = false;
               return this.renderSuccess();
            } else if (rs.contains("TXT value")) {
               List<CertCode> mapList = new ArrayList();
               CertCode map1 = null;
               CertCode map2 = null;
               String[] var21 = rs.split("\n");
               int var13 = var21.length;

               for(int var14 = 0; var14 < var13; ++var14) {
                  String str = var21[var14];
                  this.logger.info(str);
                  if (str.contains("Domain:")) {
                     map1 = new CertCode();
                     map1.setDomain(str.split("'")[1]);
                     map1.setType("TXT");
                     map2 = new CertCode();
                     map2.setDomain(map1.getDomain().replace("_acme-challenge.", ""));
                     map2.setType(this.m.get("certStr.any"));
                  }

                  if (str.contains("TXT value:")) {
                     map1.setValue(str.split("'")[1]);
                     mapList.add(map1);
                     map2.setValue(this.m.get("certStr.any"));
                     mapList.add(map2);
                  }
               }

               this.certService.saveCertCode(id, mapList);
               this.isInApply = false;
               return this.renderSuccess(mapList);
            } else {
               this.isInApply = false;
               return this.renderError("<span class='blue'>" + cmd + "</span><br>" + this.m.get("certStr.applyFail") + "<br>" + rs.replace("\n", "<br>"));
            }
         }
      }
   }

   private String[] getEnv(Cert cert) {
      List<String> list = new ArrayList();
      if (cert.getDnsType().equals("ali")) {
         list.add("Ali_Key=" + cert.getAliKey());
         list.add("Ali_Secret=" + cert.getAliSecret());
      }

      if (cert.getDnsType().equals("dp")) {
         list.add("DP_Id=" + cert.getDpId());
         list.add("DP_Key=" + cert.getDpKey());
      }

      if (cert.getDnsType().equals("cf")) {
         list.add("CF_Email=" + cert.getCfEmail());
         list.add("CF_Key=" + cert.getCfKey());
      }

      if (cert.getDnsType().equals("gd")) {
         list.add("GD_Key=" + cert.getGdKey());
         list.add("GD_Secret=" + cert.getGdSecret());
      }

      if (cert.getDnsType().equals("hw")) {
         list.add("HUAWEICLOUD_Username=" + cert.getHwUsername());
         list.add("HUAWEICLOUD_Password=" + cert.getHwPassword());
         list.add("HUAWEICLOUD_ProjectID=" + cert.getHwProjectId());
         list.add("HUAWEICLOUD_DomainName=" + cert.getHwDomainName());
      }

      return (String[])list.toArray(new String[0]);
   }

   @Mapping("getTxtValue")
   public JsonResult getTxtValue(String id) {
      List<CertCode> certCodes = this.certService.getCertCodes(id);
      return this.renderSuccess(certCodes);
   }

   @Mapping("download")
   public DownloadedFile download(String id) throws IOException {
      Cert cert = (Cert)this.sqlHelper.findById(id, Cert.class);
      if (StrUtil.isNotEmpty(cert.getPem()) && StrUtil.isNotEmpty(cert.getKey())) {
         String dir = this.homeConfig.home + "/temp/cert";
         FileUtil.del(dir);
         FileUtil.del(dir + ".zip");
         FileUtil.mkdir(dir);
         File pem = new File(cert.getPem());
         File key = new File(cert.getKey());
         FileUtil.copy(pem, new File(dir + "/" + pem.getName()), true);
         FileUtil.copy(key, new File(dir + "/" + key.getName()), true);
         ZipUtil.zip(dir);
         FileUtil.del(dir);
         DownloadedFile downloadedFile = new DownloadedFile("application/octet-stream", new FileInputStream(dir + ".zip"), "cert.zip");
         return downloadedFile;
      } else {
         return null;
      }
   }
}
