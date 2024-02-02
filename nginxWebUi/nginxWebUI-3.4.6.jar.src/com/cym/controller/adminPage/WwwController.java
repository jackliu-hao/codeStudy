/*     */ package com.cym.controller.adminPage;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.util.ZipUtil;
/*     */ import com.cym.model.Www;
/*     */ import com.cym.service.WwwService;
/*     */ import com.cym.sqlhelper.bean.Sort;
/*     */ import com.cym.utils.BaseController;
/*     */ import com.cym.utils.JsonResult;
/*     */ import java.net.URL;
/*     */ import java.nio.charset.Charset;
/*     */ import org.noear.solon.annotation.Controller;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.annotation.Mapping;
/*     */ import org.noear.solon.core.handle.ModelAndView;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ @Mapping("/adminPage/www")
/*     */ @Controller
/*     */ public class WwwController
/*     */   extends BaseController
/*     */ {
/*  26 */   Logger logger = LoggerFactory.getLogger(getClass());
/*     */   
/*     */   @Inject
/*     */   WwwService wwwService;
/*     */   
/*     */   @Mapping("")
/*     */   public ModelAndView index(ModelAndView modelAndView) {
/*  33 */     modelAndView.put("list", this.sqlHelper.findAll(new Sort("dir", Sort.Direction.ASC), Www.class));
/*  34 */     modelAndView.view("/adminPage/www/index.html");
/*  35 */     return modelAndView;
/*     */   }
/*     */   
/*     */   @Mapping("addOver")
/*     */   public JsonResult addOver(Www www, String dirTemp) {
/*  40 */     if (this.wwwService.hasDir(www.getDir(), www.getId()).booleanValue()) {
/*  41 */       return renderError(this.m.get("wwwStr.sameDir"));
/*     */     }
/*     */     
/*     */     try {
/*     */       try {
/*  46 */         ZipUtil.unzip(dirTemp, www.getDir());
/*  47 */       } catch (Exception e) {
/*     */         
/*  49 */         ZipUtil.unzip(dirTemp, www.getDir(), Charset.forName("GBK"));
/*     */       } 
/*     */       
/*  52 */       FileUtil.del(dirTemp);
/*  53 */       this.sqlHelper.insertOrUpdate(www);
/*     */       
/*  55 */       return renderSuccess();
/*     */     }
/*  57 */     catch (Exception e) {
/*  58 */       this.logger.error(e.getMessage(), e);
/*     */ 
/*     */       
/*  61 */       return renderError(this.m.get("wwwStr.zipError"));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Mapping("del")
/*     */   public JsonResult del(String id) {
/*  68 */     this.sqlHelper.deleteById(id, Www.class);
/*     */     
/*  70 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("detail")
/*     */   public JsonResult detail(String id) {
/*  75 */     Www www = (Www)this.sqlHelper.findById(id, Www.class);
/*     */     
/*  77 */     return renderSuccess(www);
/*     */   }
/*     */   
/*     */   public String getClassPath() throws Exception {
/*     */     try {
/*  82 */       String strClassName = getClass().getName();
/*  83 */       String strPackageName = "";
/*  84 */       if (getClass().getPackage() != null) {
/*  85 */         strPackageName = getClass().getPackage().getName();
/*     */       }
/*  87 */       String strClassFileName = "";
/*  88 */       if (!"".equals(strPackageName)) {
/*  89 */         strClassFileName = strClassName.substring(strPackageName.length() + 1, strClassName.length());
/*     */       } else {
/*  91 */         strClassFileName = strClassName;
/*     */       } 
/*  93 */       URL url = null;
/*  94 */       url = getClass().getResource(strClassFileName + ".class");
/*  95 */       String strURL = url.toString();
/*  96 */       strURL = strURL.substring(strURL.indexOf('/') + 1, strURL.lastIndexOf('/'));
/*     */ 
/*     */       
/*  99 */       return strURL.replaceAll("%20", " ");
/* 100 */     } catch (Exception e) {
/* 101 */       this.logger.error(e.getMessage(), e);
/* 102 */       throw e;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\WwwController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */