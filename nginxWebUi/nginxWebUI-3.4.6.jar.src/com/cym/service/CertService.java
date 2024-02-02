/*     */ package com.cym.service;
/*     */ 
/*     */ import cn.hutool.core.codec.Base64;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.ZipUtil;
/*     */ import com.cym.config.HomeConfig;
/*     */ import com.cym.model.Cert;
/*     */ import com.cym.model.CertCode;
/*     */ import com.cym.sqlhelper.bean.Page;
/*     */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*     */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*     */ import com.cym.sqlhelper.utils.SqlHelper;
/*     */ import java.io.File;
/*     */ import java.lang.invoke.SerializedLambda;
/*     */ import java.util.List;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.aspect.annotation.Service;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ @Service
/*     */ public class CertService
/*     */ {
/*  26 */   Logger logger = LoggerFactory.getLogger(getClass());
/*     */   
/*     */   @Inject
/*     */   SqlHelper sqlHelper;
/*     */   @Inject
/*     */   HomeConfig homeConfig;
/*     */   
/*     */   public boolean hasSame(Cert cert) {
/*  34 */     if (StrUtil.isEmpty(cert.getId())) {
/*     */       
/*  36 */       if (this.sqlHelper.findCountByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("domain", cert.getDomain()), Cert.class).longValue() > 0L) {
/*  37 */         return true;
/*     */       
/*     */       }
/*     */     }
/*  41 */     else if (this.sqlHelper.findCountByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("domain", cert.getDomain()).ne("id", cert.getId()), Cert.class).longValue() > 0L) {
/*  42 */       return true;
/*     */     } 
/*     */ 
/*     */     
/*  46 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<CertCode> getCertCodes(String certId) {
/*  51 */     return this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(CertCode::getCertId, certId), CertCode.class);
/*     */   }
/*     */   
/*     */   public void insertOrUpdate(Cert cert, String[] domain, String[] type, String[] value) {
/*  55 */     this.sqlHelper.insertOrUpdate(cert);
/*     */     
/*  57 */     this.sqlHelper.deleteByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(CertCode::getCertId, cert.getId()), CertCode.class);
/*     */     
/*  59 */     if (domain != null && type != null && value != null) {
/*  60 */       for (int i = 0; i < domain.length; i++) {
/*  61 */         CertCode certCode = new CertCode();
/*  62 */         certCode.setCertId(cert.getId());
/*  63 */         certCode.setDomain(domain[i]);
/*  64 */         certCode.setType(type[i]);
/*  65 */         certCode.setValue(value[i]);
/*  66 */         this.sqlHelper.insert(certCode);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Page getPage(String keywords, Page page) {
/*  73 */     ConditionAndWrapper conditionAndWrapper = new ConditionAndWrapper();
/*  74 */     if (StrUtil.isNotEmpty(keywords)) {
/*  75 */       conditionAndWrapper.like(Cert::getDomain, keywords);
/*     */     }
/*     */     
/*  78 */     return this.sqlHelper.findPage((ConditionWrapper)conditionAndWrapper, page, Cert.class);
/*     */   }
/*     */   
/*     */   public void saveCertCode(String certId, List<CertCode> mapList) {
/*  82 */     this.sqlHelper.deleteByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(CertCode::getCertId, certId), CertCode.class);
/*  83 */     for (CertCode certCode : mapList) {
/*  84 */       certCode.setCertId(certId);
/*  85 */       this.sqlHelper.insert(certCode);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasCode(String certId) {
/*  91 */     return (this.sqlHelper.findCountByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(CertCode::getCertId, certId), CertCode.class).longValue() > 0L);
/*     */   }
/*     */   
/*     */   public String getAcmeZipBase64() {
/*  95 */     File file = ZipUtil.zip(this.homeConfig.home + ".acme.sh", this.homeConfig.home + "temp" + File.separator + "cert.zip");
/*  96 */     String str = Base64.encode(file);
/*  97 */     file.delete();
/*  98 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeAcmeZipBase64(String acmeZip) {
/* 103 */     Base64.decodeToFile(acmeZip, new File(this.homeConfig.home + "acme.zip"));
/* 104 */     FileUtil.mkdir(this.homeConfig.acmeShDir);
/* 105 */     ZipUtil.unzip(this.homeConfig.home + "acme.zip", this.homeConfig.acmeShDir);
/* 106 */     FileUtil.del(this.homeConfig.home + "acme.zip");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\service\CertService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */