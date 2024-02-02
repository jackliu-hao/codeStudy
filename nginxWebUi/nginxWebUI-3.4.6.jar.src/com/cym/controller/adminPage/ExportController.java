/*     */ package com.cym.controller.adminPage;
/*     */ 
/*     */ import cn.hutool.core.date.DateUtil;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.json.JSONUtil;
/*     */ import com.cym.ext.AsycPack;
/*     */ import com.cym.model.Cert;
/*     */ import com.cym.model.CertCode;
/*     */ import com.cym.service.CertService;
/*     */ import com.cym.service.ConfService;
/*     */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*     */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*     */ import com.cym.utils.BaseController;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Date;
/*     */ import org.noear.solon.annotation.Controller;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.annotation.Mapping;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.handle.DownloadedFile;
/*     */ import org.noear.solon.core.handle.ModelAndView;
/*     */ import org.noear.solon.core.handle.UploadedFile;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ @Controller
/*     */ @Mapping("/adminPage/export")
/*     */ public class ExportController
/*     */   extends BaseController
/*     */ {
/*  37 */   Logger logger = LoggerFactory.getLogger(getClass());
/*     */   
/*     */   @Inject
/*     */   ConfService confService;
/*     */   @Inject
/*     */   CertService certService;
/*     */   
/*     */   @Mapping("")
/*     */   public ModelAndView index(ModelAndView modelAndView) {
/*  46 */     modelAndView.view("/adminPage/export/index.html");
/*  47 */     return modelAndView;
/*     */   }
/*     */   
/*     */   @Mapping("dataExport")
/*     */   public DownloadedFile dataExport(Context context) throws IOException {
/*  52 */     AsycPack asycPack = this.confService.getAsycPack(new String[] { "all" });
/*     */     
/*  54 */     asycPack.setCertList(this.sqlHelper.findAll(Cert.class));
/*  55 */     asycPack.setCertCodeList(this.sqlHelper.findAll(CertCode.class));
/*  56 */     asycPack.setAcmeZip(this.certService.getAcmeZipBase64());
/*     */     
/*  58 */     String json = JSONUtil.toJsonPrettyStr(asycPack);
/*     */     
/*  60 */     String date = DateUtil.format(new Date(), "yyyy-MM-dd_HH-mm-ss");
/*  61 */     DownloadedFile downloadedFile = new DownloadedFile("application/octet-stream", new ByteArrayInputStream(json.getBytes(Charset.forName("UTF-8"))), date + ".json");
/*  62 */     return downloadedFile;
/*     */   }
/*     */   
/*     */   @Mapping("dataImport")
/*     */   public void dataImport(UploadedFile file, Context context) throws IOException {
/*  67 */     if (file != null) {
/*  68 */       File tempFile = new File(this.homeConfig.home + "temp" + File.separator + file.name);
/*  69 */       FileUtil.mkdir(tempFile.getParentFile());
/*  70 */       file.transferTo(tempFile);
/*  71 */       String json = FileUtil.readString(tempFile, Charset.forName("UTF-8"));
/*  72 */       tempFile.delete();
/*     */       
/*  74 */       AsycPack asycPack = (AsycPack)JSONUtil.toBean(json, AsycPack.class);
/*  75 */       this.confService.setAsycPack(asycPack);
/*     */ 
/*     */       
/*  78 */       if (asycPack.getCertList() != null) {
/*  79 */         this.sqlHelper.deleteByQuery((ConditionWrapper)new ConditionAndWrapper(), Cert.class);
/*  80 */         this.sqlHelper.insertAll(asycPack.getCertList());
/*     */       } 
/*  82 */       if (asycPack.getCertCodeList() != null) {
/*  83 */         this.sqlHelper.deleteByQuery((ConditionWrapper)new ConditionAndWrapper(), CertCode.class);
/*  84 */         this.sqlHelper.insertAll(asycPack.getCertList());
/*     */       } 
/*     */       
/*  87 */       this.certService.writeAcmeZipBase64(asycPack.getAcmeZip());
/*     */     } 
/*  89 */     context.redirect("/adminPage/export?over=true");
/*     */   }
/*     */   
/*     */   @Mapping("logExport")
/*     */   public DownloadedFile logExport(Context context) throws FileNotFoundException {
/*  94 */     File file = new File(this.homeConfig.home + "log/nginxWebUI.log");
/*  95 */     if (file.exists()) {
/*  96 */       DownloadedFile downloadedFile = new DownloadedFile("application/octet-stream", new FileInputStream(file), file.getName());
/*  97 */       return downloadedFile;
/*     */     } 
/*     */     
/* 100 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\ExportController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */