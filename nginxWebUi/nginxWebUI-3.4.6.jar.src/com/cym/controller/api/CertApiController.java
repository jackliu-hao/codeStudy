/*     */ package com.cym.controller.api;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import com.cym.controller.adminPage.CertController;
/*     */ import com.cym.model.Cert;
/*     */ import com.cym.model.CertCode;
/*     */ import com.cym.service.CertService;
/*     */ import com.cym.sqlhelper.bean.Page;
/*     */ import com.cym.utils.BaseController;
/*     */ import com.cym.utils.JsonResult;
/*     */ import java.io.IOException;
/*     */ import java.util.List;
/*     */ import org.noear.solon.annotation.Controller;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.annotation.Mapping;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Mapping("/api/cert")
/*     */ @Controller
/*     */ public class CertApiController
/*     */   extends BaseController
/*     */ {
/*     */   @Inject
/*     */   CertController certController;
/*     */   @Inject
/*     */   CertService certService;
/*     */   
/*     */   @Mapping("getPage")
/*     */   public JsonResult<Page<Cert>> getPage(Integer current, Integer limit, String keywords) {
/*  45 */     Page page = new Page();
/*  46 */     page.setCurr(current);
/*  47 */     page.setLimit(limit);
/*  48 */     page = this.certService.getPage(keywords, page);
/*     */     
/*  50 */     return renderSuccess(page);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("addOver")
/*     */   public JsonResult addOver(Cert cert) {
/*  61 */     if (StrUtil.isEmpty(cert.getDomain())) {
/*  62 */       return renderError("域名为空");
/*     */     }
/*     */     
/*  65 */     if (cert.getType().intValue() == 0) {
/*  66 */       if (StrUtil.isEmpty(cert.getDnsType())) {
/*  67 */         return renderError("dns提供商为空");
/*     */       }
/*     */       
/*  70 */       if (cert.getDnsType().equals("ali") && (StrUtil.isEmpty(cert.getAliKey()) || StrUtil.isEmpty(cert.getAliSecret()))) {
/*  71 */         return renderError("aliKey 或 aliSecret为空");
/*     */       }
/*  73 */       if (cert.getDnsType().equals("dp") && (StrUtil.isEmpty(cert.getDpId()) || StrUtil.isEmpty(cert.getDpKey()))) {
/*  74 */         return renderError("dpId 或 dpKey为空");
/*     */       }
/*  76 */       if (cert.getDnsType().equals("cf") && (StrUtil.isEmpty(cert.getCfEmail()) || StrUtil.isEmpty(cert.getCfKey()))) {
/*  77 */         return renderError("cfEmail 或 cfKey为空");
/*     */       }
/*  79 */       if (cert.getDnsType().equals("gd") && (StrUtil.isEmpty(cert.getGdKey()) || StrUtil.isEmpty(cert.getGdSecret()))) {
/*  80 */         return renderError("gdKey 或 gdSecret为空");
/*     */       }
/*     */     } 
/*  83 */     return this.certController.addOver(cert, null, null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("getTxtValue")
/*     */   public JsonResult<List<CertCode>> getTxtValue(String certId) {
/*  94 */     List<CertCode> certCodes = this.certService.getCertCodes(certId);
/*  95 */     return renderSuccess(certCodes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("setAutoRenew")
/*     */   public JsonResult setAutoRenew(String id, Integer autoRenew) {
/* 107 */     Cert cert = new Cert();
/* 108 */     cert.setId(id);
/* 109 */     cert.setAutoRenew(autoRenew);
/*     */     
/* 111 */     this.certController.setAutoRenew(cert);
/* 112 */     return renderSuccess();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("del")
/*     */   public JsonResult del(String id) {
/* 123 */     return this.certController.del(id);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("apply")
/*     */   public JsonResult<List<CertCode>> apply(String id, String type) {
/* 136 */     JsonResult<List<CertCode>> jsonResult = this.certController.apply(id, type);
/*     */     
/* 138 */     if (jsonResult.isSuccess() && jsonResult.getObj() != null) {
/* 139 */       jsonResult.setMsg(this.m.get("certStr.dnsDescr"));
/*     */     }
/*     */     
/* 142 */     return jsonResult;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("download")
/*     */   public void download(String id) throws IOException {
/* 153 */     this.certController.download(id);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\api\CertApiController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */