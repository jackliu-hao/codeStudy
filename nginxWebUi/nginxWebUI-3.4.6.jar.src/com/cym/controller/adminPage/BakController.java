/*     */ package com.cym.controller.adminPage;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import com.cym.model.Bak;
/*     */ import com.cym.model.BakSub;
/*     */ import com.cym.service.BakService;
/*     */ import com.cym.service.SettingService;
/*     */ import com.cym.sqlhelper.bean.Page;
/*     */ import com.cym.utils.BaseController;
/*     */ import com.cym.utils.JsonResult;
/*     */ import java.io.File;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.noear.solon.annotation.Controller;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.annotation.Mapping;
/*     */ import org.noear.solon.core.handle.ModelAndView;
/*     */ 
/*     */ 
/*     */ @Controller
/*     */ @Mapping("/adminPage/bak")
/*     */ public class BakController
/*     */   extends BaseController
/*     */ {
/*     */   @Inject
/*     */   SettingService settingService;
/*     */   @Inject
/*     */   BakService bakService;
/*     */   
/*     */   @Mapping("")
/*     */   public ModelAndView index(ModelAndView modelAndView, Page page) {
/*  35 */     page = this.bakService.getList(page);
/*     */     
/*  37 */     modelAndView.put("page", page);
/*  38 */     modelAndView.view("/adminPage/bak/index.html");
/*  39 */     return modelAndView;
/*     */   }
/*     */   
/*     */   @Mapping("getCompare")
/*     */   public JsonResult getCompare(String id) {
/*  44 */     Bak bak = (Bak)this.sqlHelper.findById(id, Bak.class);
/*     */     
/*  46 */     Bak pre = this.bakService.getPre(id);
/*  47 */     if (pre == null) {
/*  48 */       return renderError("没有更早的备份文件");
/*     */     }
/*     */     
/*  51 */     Map<Object, Object> map = new HashMap<>();
/*  52 */     map.put("bak", bak);
/*  53 */     map.put("pre", pre);
/*     */     
/*  55 */     return renderSuccess(map);
/*     */   }
/*     */   
/*     */   @Mapping("content")
/*     */   public JsonResult content(String id) {
/*  60 */     Bak bak = (Bak)this.sqlHelper.findById(id, Bak.class);
/*  61 */     return renderSuccess(bak);
/*     */   }
/*     */   
/*     */   @Mapping("replace")
/*     */   public JsonResult replace(String id) {
/*  66 */     Bak bak = (Bak)this.sqlHelper.findById(id, Bak.class);
/*     */     
/*  68 */     String nginxPath = this.settingService.get("nginxPath");
/*     */     
/*  70 */     if (StrUtil.isNotEmpty(nginxPath)) {
/*  71 */       File pathFile = new File(nginxPath);
/*     */       
/*  73 */       FileUtil.writeString(bak.getContent(), pathFile, StandardCharsets.UTF_8);
/*     */ 
/*     */       
/*  76 */       String confd = pathFile.getParent() + File.separator + "conf.d" + File.separator;
/*  77 */       FileUtil.del(confd);
/*  78 */       FileUtil.mkdir(confd);
/*     */       
/*  80 */       List<BakSub> subList = this.bakService.getSubList(bak.getId());
/*  81 */       for (BakSub bakSub : subList) {
/*  82 */         FileUtil.writeString(bakSub.getContent(), confd + bakSub.getName(), StandardCharsets.UTF_8);
/*     */       }
/*  84 */       return renderSuccess();
/*     */     } 
/*  86 */     return renderError(this.m.get("bakStr.pathNotice"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("del")
/*     */   public JsonResult del(String id) {
/*  94 */     this.bakService.del(id);
/*  95 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("delAll")
/*     */   public JsonResult delAll() {
/* 100 */     this.bakService.delAll();
/*     */     
/* 102 */     return renderSuccess();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\BakController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */