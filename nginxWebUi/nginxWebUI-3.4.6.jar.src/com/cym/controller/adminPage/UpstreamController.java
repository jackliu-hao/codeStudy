/*     */ package com.cym.controller.adminPage;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.json.JSONUtil;
/*     */ import com.cym.ext.UpstreamExt;
/*     */ import com.cym.model.Upstream;
/*     */ import com.cym.model.UpstreamServer;
/*     */ import com.cym.service.ParamService;
/*     */ import com.cym.service.SettingService;
/*     */ import com.cym.service.UpstreamService;
/*     */ import com.cym.sqlhelper.bean.Page;
/*     */ import com.cym.utils.BaseController;
/*     */ import com.cym.utils.JsonResult;
/*     */ import com.cym.utils.SnowFlakeUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.noear.solon.annotation.Controller;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.annotation.Mapping;
/*     */ import org.noear.solon.core.handle.ModelAndView;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Controller
/*     */ @Mapping("/adminPage/upstream")
/*     */ public class UpstreamController
/*     */   extends BaseController
/*     */ {
/*     */   @Inject
/*     */   UpstreamService upstreamService;
/*     */   @Inject
/*     */   ParamService paramService;
/*     */   @Inject
/*     */   SettingService settingService;
/*     */   
/*     */   @Mapping("")
/*     */   public ModelAndView index(ModelAndView modelAndView, Page page, String keywords) {
/*  41 */     page = this.upstreamService.search(page, keywords);
/*     */     
/*  43 */     List<UpstreamExt> list = new ArrayList<>();
/*  44 */     for (Upstream upstream : page.getRecords()) {
/*  45 */       UpstreamExt upstreamExt = new UpstreamExt();
/*  46 */       upstreamExt.setUpstream(upstream);
/*     */       
/*  48 */       List<String> str = new ArrayList<>();
/*  49 */       List<UpstreamServer> servers = this.upstreamService.getUpstreamServers(upstream.getId());
/*  50 */       for (UpstreamServer upstreamServer : servers) {
/*  51 */         str.add(buildStr(upstreamServer, upstream.getProxyType()));
/*     */       }
/*     */ 
/*     */       
/*  55 */       if (StrUtil.isNotEmpty(upstream.getDescr())) {
/*  56 */         upstream.setDescr(upstream.getDescr().replace("\n", "<br>"));
/*     */       }
/*     */       
/*  59 */       upstreamExt.setServerStr(StrUtil.join("", str));
/*  60 */       list.add(upstreamExt);
/*     */     } 
/*  62 */     page.setRecords(list);
/*     */     
/*  64 */     modelAndView.put("page", page);
/*  65 */     modelAndView.put("keywords", keywords);
/*     */     
/*  67 */     modelAndView.put("upstreamMonitor", this.settingService.get("upstreamMonitor"));
/*  68 */     modelAndView.view("/adminPage/upstream/index.html");
/*  69 */     return modelAndView;
/*     */   }
/*     */   
/*     */   public String buildStr(UpstreamServer upstreamServer, Integer proxyType) {
/*  73 */     String status = this.m.get("upstreamStr.noStatus");
/*  74 */     if (!"none".equals(upstreamServer.getStatus())) {
/*  75 */       status = upstreamServer.getStatus();
/*     */     }
/*     */     
/*  78 */     String monitorStatus = "";
/*     */     
/*  80 */     String upstreamMonitor = this.settingService.get("upstreamMonitor");
/*  81 */     if ("true".equals(upstreamMonitor)) {
/*  82 */       monitorStatus = monitorStatus + "<td class='short50'>";
/*  83 */       if (upstreamServer.getMonitorStatus().intValue() == -1) {
/*  84 */         monitorStatus = monitorStatus + "<span class='gray'>" + this.m.get("upstreamStr.gray") + "</span>";
/*  85 */       } else if (upstreamServer.getMonitorStatus().intValue() == 1) {
/*  86 */         monitorStatus = monitorStatus + "<span class='green'>" + this.m.get("upstreamStr.green") + "</span>";
/*     */       } else {
/*  88 */         monitorStatus = monitorStatus + "<span class='red'>" + this.m.get("upstreamStr.red") + "</span>";
/*     */       } 
/*  90 */       monitorStatus = monitorStatus + "</td>";
/*     */     } 
/*     */     
/*  93 */     if (upstreamServer.getServer().contains(":")) {
/*  94 */       upstreamServer.setServer("[" + upstreamServer.getServer() + "]");
/*     */     }
/*     */     
/*  97 */     String html = "<tr><td class='short100'>" + upstreamServer.getServer() + ":" + upstreamServer.getPort() + "</td><td>";
/*     */     
/*  99 */     if (upstreamServer.getWeight() != null) {
/* 100 */       html = html + "weight=" + upstreamServer.getWeight() + " ";
/*     */     }
/* 102 */     if (upstreamServer.getFailTimeout() != null) {
/* 103 */       html = html + "fail_timeout=" + upstreamServer.getFailTimeout() + "s ";
/*     */     }
/* 105 */     if (upstreamServer.getMaxFails() != null) {
/* 106 */       html = html + "max_fails=" + upstreamServer.getMaxFails() + " ";
/*     */     }
/* 108 */     if (upstreamServer.getMaxConns() != null) {
/* 109 */       html = html + "max_conns=" + upstreamServer.getMaxConns() + " ";
/*     */     }
/* 111 */     html = html + "</td><td class='short50'>" + status + "</td>" + monitorStatus + "</tr>";
/* 112 */     return html;
/*     */   }
/*     */   
/*     */   @Mapping("addOver")
/*     */   public JsonResult addOver(String upstreamJson, String upstreamParamJson, String upstreamServerJson) {
/* 117 */     Upstream upstream = (Upstream)JSONUtil.toBean(upstreamJson, Upstream.class);
/* 118 */     List<UpstreamServer> upstreamServers = JSONUtil.toList(JSONUtil.parseArray(upstreamServerJson), UpstreamServer.class);
/*     */     
/* 120 */     if (StrUtil.isEmpty(upstream.getId())) {
/* 121 */       upstream.setSeq(SnowFlakeUtils.getId());
/*     */     }
/*     */     
/* 124 */     if (StrUtil.isEmpty(upstream.getId())) {
/* 125 */       Long count = this.upstreamService.getCountByName(upstream.getName());
/* 126 */       if (count.longValue() > 0L) {
/* 127 */         return renderError(this.m.get("upstreamStr.sameName"));
/*     */       }
/*     */     } else {
/* 130 */       Long count = this.upstreamService.getCountByNameWithOutId(upstream.getName(), upstream.getId());
/* 131 */       if (count.longValue() > 0L) {
/* 132 */         return renderError(this.m.get("upstreamStr.sameName"));
/*     */       }
/*     */     } 
/*     */     
/* 136 */     this.upstreamService.addOver(upstream, upstreamServers, upstreamParamJson);
/*     */     
/* 138 */     return renderSuccess();
/*     */   }
/*     */ 
/*     */   
/*     */   @Mapping("detail")
/*     */   public JsonResult detail(String id) {
/* 144 */     UpstreamExt upstreamExt = new UpstreamExt();
/* 145 */     upstreamExt.setUpstream((Upstream)this.sqlHelper.findById(id, Upstream.class));
/* 146 */     upstreamExt.setUpstreamServerList(this.upstreamService.getUpstreamServers(id));
/*     */     
/* 148 */     upstreamExt.setParamJson(this.paramService.getJsonByTypeId(upstreamExt.getUpstream().getId(), "upstream"));
/*     */     
/* 150 */     return renderSuccess(upstreamExt);
/*     */   }
/*     */ 
/*     */   
/*     */   @Mapping("del")
/*     */   public JsonResult del(String id) {
/* 156 */     this.upstreamService.deleteById(id);
/*     */     
/* 158 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("setMonitor")
/*     */   public JsonResult setMonitor(String id, Integer monitor) {
/* 163 */     Upstream upstream = new Upstream();
/* 164 */     upstream.setId(id);
/* 165 */     upstream.setMonitor(monitor);
/* 166 */     this.sqlHelper.updateById(upstream);
/*     */     
/* 168 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("upstreamStatus")
/*     */   public JsonResult upstreamStatus() {
/* 173 */     Map<String, String> map = new HashMap<>();
/* 174 */     map.put("mail", this.settingService.get("mail"));
/*     */     
/* 176 */     String upstreamMonitor = this.settingService.get("upstreamMonitor");
/* 177 */     map.put("upstreamMonitor", (upstreamMonitor != null) ? upstreamMonitor : "false");
/*     */     
/* 179 */     return renderSuccess(map);
/*     */   }
/*     */   
/*     */   @Mapping("upstreamOver")
/*     */   public JsonResult upstreamOver(String mail, String upstreamMonitor) {
/* 184 */     this.settingService.set("mail", mail);
/* 185 */     this.settingService.set("upstreamMonitor", upstreamMonitor);
/*     */     
/* 187 */     if (upstreamMonitor.equals("true")) {
/* 188 */       this.upstreamService.resetMonitorStatus();
/*     */     }
/*     */     
/* 191 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("setOrder")
/*     */   public JsonResult setOrder(String id, Integer count) {
/* 196 */     this.upstreamService.setSeq(id, count);
/* 197 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("getDescr")
/*     */   public JsonResult getDescr(String id) {
/* 202 */     Upstream upstream = (Upstream)this.sqlHelper.findById(id, Upstream.class);
/*     */     
/* 204 */     return renderSuccess(upstream.getDescr());
/*     */   }
/*     */   
/*     */   @Mapping("editDescr")
/*     */   public JsonResult editDescr(String id, String descr) {
/* 209 */     Upstream upstream = new Upstream();
/* 210 */     upstream.setId(id);
/* 211 */     upstream.setDescr(descr);
/* 212 */     this.sqlHelper.updateById(upstream);
/*     */     
/* 214 */     return renderSuccess();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\UpstreamController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */