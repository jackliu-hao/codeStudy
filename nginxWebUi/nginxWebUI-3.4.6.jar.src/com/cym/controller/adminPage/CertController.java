/*     */ package com.cym.controller.adminPage;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.ZipUtil;
/*     */ import com.cym.model.Cert;
/*     */ import com.cym.model.CertCode;
/*     */ import com.cym.service.CertService;
/*     */ import com.cym.service.SettingService;
/*     */ import com.cym.sqlhelper.bean.Page;
/*     */ import com.cym.utils.BaseController;
/*     */ import com.cym.utils.JsonResult;
/*     */ import com.cym.utils.SystemTool;
/*     */ import com.cym.utils.TimeExeUtils;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.noear.solon.annotation.Controller;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.annotation.Mapping;
/*     */ import org.noear.solon.core.handle.DownloadedFile;
/*     */ import org.noear.solon.core.handle.ModelAndView;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ @Controller
/*     */ @Mapping("/adminPage/cert")
/*     */ public class CertController
/*     */   extends BaseController
/*     */ {
/*     */   @Inject
/*     */   SettingService settingService;
/*     */   @Inject
/*     */   CertService certService;
/*     */   @Inject
/*     */   TimeExeUtils timeExeUtils;
/*     */   @Inject
/*     */   ConfController confController;
/*  44 */   Logger logger = LoggerFactory.getLogger(getClass());
/*     */   
/*  46 */   Boolean isInApply = Boolean.valueOf(false);
/*     */   
/*     */   @Mapping("")
/*     */   public ModelAndView index(ModelAndView modelAndView, Page page, String keywords) {
/*  50 */     page = this.certService.getPage(keywords, page);
/*     */     
/*  52 */     for (Cert cert : page.getRecords()) {
/*  53 */       if (cert.getType().intValue() == 0 || cert.getType().intValue() == 2) {
/*  54 */         cert.setDomain(cert.getDomain() + "(" + cert.getEncryption() + ")");
/*     */       }
/*     */       
/*  57 */       if (cert.getMakeTime() != null) {
/*  58 */         cert.setEndTime(Long.valueOf(cert.getMakeTime().longValue() + 7776000000L));
/*     */       }
/*     */     } 
/*     */     
/*  62 */     modelAndView.put("keywords", keywords);
/*  63 */     modelAndView.put("page", page);
/*  64 */     modelAndView.view("/adminPage/cert/index.html");
/*  65 */     return modelAndView;
/*     */   }
/*     */   
/*     */   @Mapping("addOver")
/*     */   public JsonResult addOver(Cert cert, String[] domains, String[] types, String[] values) {
/*  70 */     Integer type = cert.getType();
/*  71 */     if (type == null && StrUtil.isNotEmpty(cert.getId())) {
/*  72 */       Cert certOrg = (Cert)this.sqlHelper.findById(cert.getId(), Cert.class);
/*  73 */       type = certOrg.getType();
/*     */     } 
/*     */     
/*  76 */     if (type != null && type.intValue() == 1) {
/*     */       
/*  78 */       if (cert.getKey().contains(FileUtil.getTmpDir().toString().replace("\\", "/"))) {
/*  79 */         String keyName = (new File(cert.getKey())).getName();
/*  80 */         FileUtil.move(new File(cert.getKey()), new File(this.homeConfig.home + "cert/" + keyName), true);
/*  81 */         cert.setKey(this.homeConfig.home + "cert/" + keyName);
/*     */       } 
/*     */       
/*  84 */       if (cert.getPem().contains(FileUtil.getTmpDir().toString().replace("\\", "/"))) {
/*  85 */         String pemName = (new File(cert.getPem())).getName();
/*  86 */         FileUtil.move(new File(cert.getPem()), new File(this.homeConfig.home + "cert/"), true);
/*  87 */         cert.setPem(this.homeConfig.home + "cert/" + pemName);
/*     */       } 
/*     */     } 
/*     */     
/*  91 */     this.certService.insertOrUpdate(cert, domains, types, values);
/*     */     
/*  93 */     return renderSuccess(cert);
/*     */   }
/*     */   
/*     */   @Mapping("setAutoRenew")
/*     */   public JsonResult setAutoRenew(Cert cert) {
/*  98 */     this.sqlHelper.updateById(cert);
/*  99 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("detail")
/*     */   public JsonResult detail(String id) {
/* 104 */     return renderSuccess(this.sqlHelper.findById(id, Cert.class));
/*     */   }
/*     */   
/*     */   @Mapping("del")
/*     */   public JsonResult del(String id) {
/* 109 */     Cert cert = (Cert)this.sqlHelper.findById(id, Cert.class);
/*     */     
/* 111 */     if (cert.getType().intValue() == 1) {
/*     */       
/* 113 */       if (cert.getPem().contains(this.homeConfig.home + "cert/")) {
/* 114 */         FileUtil.del(cert.getPem());
/*     */       }
/* 116 */       if (cert.getKey().contains(this.homeConfig.home + "cert/")) {
/* 117 */         FileUtil.del(cert.getKey());
/*     */       }
/*     */     } else {
/*     */       
/* 121 */       String domain = cert.getDomain().split(",")[0];
/* 122 */       String path = this.homeConfig.acmeShDir + domain;
/*     */       
/* 124 */       if ("ECC".equals(cert.getEncryption())) {
/* 125 */         path = path + "_ecc";
/*     */       }
/* 127 */       if (FileUtil.exist(path)) {
/* 128 */         FileUtil.del(path);
/*     */       }
/*     */     } 
/*     */     
/* 132 */     this.sqlHelper.deleteById(id, Cert.class);
/* 133 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("apply")
/*     */   public JsonResult apply(String id, String type) {
/* 138 */     if (!SystemTool.isLinux().booleanValue()) {
/* 139 */       return renderError(this.m.get("certStr.error2"));
/*     */     }
/*     */     
/* 142 */     Cert cert = (Cert)this.sqlHelper.findById(id, Cert.class);
/*     */     
/* 144 */     if (cert.getDnsType() == null) {
/* 145 */       return renderError(this.m.get("certStr.error3"));
/*     */     }
/*     */     
/* 148 */     if (this.isInApply.booleanValue()) {
/* 149 */       return renderError(this.m.get("certStr.error4"));
/*     */     }
/* 151 */     this.isInApply = Boolean.valueOf(true);
/*     */     
/* 153 */     String keylength = "";
/* 154 */     String ecc = "";
/* 155 */     if ("ECC".equals(cert.getEncryption())) {
/* 156 */       keylength = " --keylength ec-256 ";
/* 157 */       ecc = " --ecc";
/*     */     } 
/*     */     
/* 160 */     String rs = "";
/* 161 */     String cmd = "";
/*     */     
/* 163 */     String[] env = getEnv(cert);
/*     */     
/* 165 */     if (type.equals("issue")) {
/* 166 */       String[] split = cert.getDomain().split(",");
/* 167 */       StringBuffer sb = new StringBuffer();
/* 168 */       Arrays.<String>stream(split).forEach(s -> sb.append(" -d ").append(s));
/* 169 */       String domain = sb.toString();
/*     */       
/* 171 */       if (cert.getType().intValue() == 0) {
/* 172 */         String dnsType = "";
/* 173 */         if (cert.getDnsType().equals("ali")) {
/* 174 */           dnsType = "dns_ali";
/* 175 */         } else if (cert.getDnsType().equals("dp")) {
/* 176 */           dnsType = "dns_dp";
/* 177 */         } else if (cert.getDnsType().equals("cf")) {
/* 178 */           dnsType = "dns_cf";
/* 179 */         } else if (cert.getDnsType().equals("gd")) {
/* 180 */           dnsType = "dns_gd";
/* 181 */         } else if (cert.getDnsType().equals("hw")) {
/* 182 */           dnsType = "dns_huaweicloud";
/*     */         } 
/* 184 */         cmd = this.homeConfig.acmeSh + " --issue --force --dns " + dnsType + domain + keylength + " --server letsencrypt";
/* 185 */       } else if (cert.getType().intValue() == 2) {
/* 186 */         if (this.certService.hasCode(cert.getId())) {
/* 187 */           cmd = this.homeConfig.acmeSh + " --renew --force --dns" + domain + " --server letsencrypt --yes-I-know-dns-manual-mode-enough-go-ahead-please";
/*     */         } else {
/* 189 */           cmd = this.homeConfig.acmeSh + " --issue --force --dns" + domain + keylength + " --server letsencrypt --yes-I-know-dns-manual-mode-enough-go-ahead-please";
/*     */         }
/*     */       
/*     */       } 
/* 193 */     } else if (type.equals("renew")) {
/*     */       
/* 195 */       String domain = cert.getDomain().split(",")[0];
/*     */       
/* 197 */       if (cert.getType().intValue() == 0) {
/* 198 */         cmd = this.homeConfig.acmeSh + " --renew --force " + ecc + " -d " + domain;
/* 199 */       } else if (cert.getType().intValue() == 2) {
/* 200 */         cmd = this.homeConfig.acmeSh + " --renew --force " + ecc + " -d " + domain + " --server letsencrypt --yes-I-know-dns-manual-mode-enough-go-ahead-please";
/*     */       } 
/*     */     } 
/* 203 */     this.logger.info(cmd);
/*     */     
/* 205 */     rs = this.timeExeUtils.execCMD(cmd, env, 120000L);
/* 206 */     this.logger.info(rs);
/*     */     
/* 208 */     if (rs.contains("Your cert is in")) {
/*     */       
/* 210 */       String domain = cert.getDomain().split(",")[0];
/* 211 */       String certDir = this.homeConfig.acmeShDir + domain;
/* 212 */       if ("ECC".equals(cert.getEncryption())) {
/* 213 */         certDir = certDir + "_ecc";
/*     */       }
/* 215 */       certDir = certDir + "/";
/*     */       
/* 217 */       cert.setPem(certDir + "fullchain.cer");
/* 218 */       cert.setKey(certDir + domain + ".key");
/*     */       
/* 220 */       cert.setMakeTime(Long.valueOf(System.currentTimeMillis()));
/* 221 */       this.sqlHelper.updateById(cert);
/*     */ 
/*     */       
/* 224 */       if (type.equals("renew")) {
/* 225 */         this.confController.reload(null, null, null);
/*     */       }
/*     */       
/* 228 */       this.isInApply = Boolean.valueOf(false);
/* 229 */       return renderSuccess();
/* 230 */     }  if (rs.contains("TXT value")) {
/*     */       
/* 232 */       List<CertCode> mapList = new ArrayList<>();
/*     */       
/* 234 */       CertCode map1 = null;
/* 235 */       CertCode map2 = null;
/* 236 */       for (String str : rs.split("\n")) {
/* 237 */         this.logger.info(str);
/* 238 */         if (str.contains("Domain:")) {
/* 239 */           map1 = new CertCode();
/* 240 */           map1.setDomain(str.split("'")[1]);
/* 241 */           map1.setType("TXT");
/*     */           
/* 243 */           map2 = new CertCode();
/* 244 */           map2.setDomain(map1.getDomain().replace("_acme-challenge.", ""));
/* 245 */           map2.setType(this.m.get("certStr.any"));
/*     */         } 
/*     */         
/* 248 */         if (str.contains("TXT value:")) {
/* 249 */           map1.setValue(str.split("'")[1]);
/* 250 */           mapList.add(map1);
/*     */           
/* 252 */           map2.setValue(this.m.get("certStr.any"));
/* 253 */           mapList.add(map2);
/*     */         } 
/*     */       } 
/* 256 */       this.certService.saveCertCode(id, mapList);
/* 257 */       this.isInApply = Boolean.valueOf(false);
/* 258 */       return renderSuccess(mapList);
/*     */     } 
/* 260 */     this.isInApply = Boolean.valueOf(false);
/* 261 */     return renderError("<span class='blue'>" + cmd + "</span><br>" + this.m.get("certStr.applyFail") + "<br>" + rs.replace("\n", "<br>"));
/*     */   }
/*     */ 
/*     */   
/*     */   private String[] getEnv(Cert cert) {
/* 266 */     List<String> list = new ArrayList<>();
/* 267 */     if (cert.getDnsType().equals("ali")) {
/* 268 */       list.add("Ali_Key=" + cert.getAliKey());
/* 269 */       list.add("Ali_Secret=" + cert.getAliSecret());
/*     */     } 
/* 271 */     if (cert.getDnsType().equals("dp")) {
/* 272 */       list.add("DP_Id=" + cert.getDpId());
/* 273 */       list.add("DP_Key=" + cert.getDpKey());
/*     */     } 
/* 275 */     if (cert.getDnsType().equals("cf")) {
/* 276 */       list.add("CF_Email=" + cert.getCfEmail());
/* 277 */       list.add("CF_Key=" + cert.getCfKey());
/*     */     } 
/* 279 */     if (cert.getDnsType().equals("gd")) {
/* 280 */       list.add("GD_Key=" + cert.getGdKey());
/* 281 */       list.add("GD_Secret=" + cert.getGdSecret());
/*     */     } 
/* 283 */     if (cert.getDnsType().equals("hw")) {
/* 284 */       list.add("HUAWEICLOUD_Username=" + cert.getHwUsername());
/* 285 */       list.add("HUAWEICLOUD_Password=" + cert.getHwPassword());
/* 286 */       list.add("HUAWEICLOUD_ProjectID=" + cert.getHwProjectId());
/* 287 */       list.add("HUAWEICLOUD_DomainName=" + cert.getHwDomainName());
/*     */     } 
/*     */     
/* 290 */     return list.<String>toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   @Mapping("getTxtValue")
/*     */   public JsonResult getTxtValue(String id) {
/* 296 */     List<CertCode> certCodes = this.certService.getCertCodes(id);
/* 297 */     return renderSuccess(certCodes);
/*     */   }
/*     */   
/*     */   @Mapping("download")
/*     */   public DownloadedFile download(String id) throws IOException {
/* 302 */     Cert cert = (Cert)this.sqlHelper.findById(id, Cert.class);
/* 303 */     if (StrUtil.isNotEmpty(cert.getPem()) && StrUtil.isNotEmpty(cert.getKey())) {
/* 304 */       String dir = this.homeConfig.home + "/temp/cert";
/* 305 */       FileUtil.del(dir);
/* 306 */       FileUtil.del(dir + ".zip");
/* 307 */       FileUtil.mkdir(dir);
/*     */       
/* 309 */       File pem = new File(cert.getPem());
/* 310 */       File key = new File(cert.getKey());
/* 311 */       FileUtil.copy(pem, new File(dir + "/" + pem.getName()), true);
/* 312 */       FileUtil.copy(key, new File(dir + "/" + key.getName()), true);
/*     */       
/* 314 */       ZipUtil.zip(dir);
/* 315 */       FileUtil.del(dir);
/*     */       
/* 317 */       DownloadedFile downloadedFile = new DownloadedFile("application/octet-stream", new FileInputStream(dir + ".zip"), "cert.zip");
/* 318 */       return downloadedFile;
/*     */     } 
/*     */     
/* 321 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\CertController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */