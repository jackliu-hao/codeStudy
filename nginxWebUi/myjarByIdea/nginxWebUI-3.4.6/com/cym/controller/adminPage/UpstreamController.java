package com.cym.controller.adminPage;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cym.ext.UpstreamExt;
import com.cym.model.Upstream;
import com.cym.model.UpstreamServer;
import com.cym.service.ParamService;
import com.cym.service.SettingService;
import com.cym.service.UpstreamService;
import com.cym.sqlhelper.bean.Page;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import com.cym.utils.SnowFlakeUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;

@Controller
@Mapping("/adminPage/upstream")
public class UpstreamController extends BaseController {
   @Inject
   UpstreamService upstreamService;
   @Inject
   ParamService paramService;
   @Inject
   SettingService settingService;

   @Mapping("")
   public ModelAndView index(ModelAndView modelAndView, Page page, String keywords) {
      page = this.upstreamService.search(page, keywords);
      List<UpstreamExt> list = new ArrayList();
      Iterator var5 = page.getRecords().iterator();

      while(var5.hasNext()) {
         Upstream upstream = (Upstream)var5.next();
         UpstreamExt upstreamExt = new UpstreamExt();
         upstreamExt.setUpstream(upstream);
         List<String> str = new ArrayList();
         List<UpstreamServer> servers = this.upstreamService.getUpstreamServers(upstream.getId());
         Iterator var10 = servers.iterator();

         while(var10.hasNext()) {
            UpstreamServer upstreamServer = (UpstreamServer)var10.next();
            str.add(this.buildStr(upstreamServer, upstream.getProxyType()));
         }

         if (StrUtil.isNotEmpty(upstream.getDescr())) {
            upstream.setDescr(upstream.getDescr().replace("\n", "<br>"));
         }

         upstreamExt.setServerStr(StrUtil.join("", str));
         list.add(upstreamExt);
      }

      page.setRecords(list);
      modelAndView.put("page", page);
      modelAndView.put("keywords", keywords);
      modelAndView.put("upstreamMonitor", this.settingService.get("upstreamMonitor"));
      modelAndView.view("/adminPage/upstream/index.html");
      return modelAndView;
   }

   public String buildStr(UpstreamServer upstreamServer, Integer proxyType) {
      String status = this.m.get("upstreamStr.noStatus");
      if (!"none".equals(upstreamServer.getStatus())) {
         status = upstreamServer.getStatus();
      }

      String monitorStatus = "";
      String upstreamMonitor = this.settingService.get("upstreamMonitor");
      if ("true".equals(upstreamMonitor)) {
         monitorStatus = monitorStatus + "<td class='short50'>";
         if (upstreamServer.getMonitorStatus() == -1) {
            monitorStatus = monitorStatus + "<span class='gray'>" + this.m.get("upstreamStr.gray") + "</span>";
         } else if (upstreamServer.getMonitorStatus() == 1) {
            monitorStatus = monitorStatus + "<span class='green'>" + this.m.get("upstreamStr.green") + "</span>";
         } else {
            monitorStatus = monitorStatus + "<span class='red'>" + this.m.get("upstreamStr.red") + "</span>";
         }

         monitorStatus = monitorStatus + "</td>";
      }

      if (upstreamServer.getServer().contains(":")) {
         upstreamServer.setServer("[" + upstreamServer.getServer() + "]");
      }

      String html = "<tr><td class='short100'>" + upstreamServer.getServer() + ":" + upstreamServer.getPort() + "</td><td>";
      if (upstreamServer.getWeight() != null) {
         html = html + "weight=" + upstreamServer.getWeight() + " ";
      }

      if (upstreamServer.getFailTimeout() != null) {
         html = html + "fail_timeout=" + upstreamServer.getFailTimeout() + "s ";
      }

      if (upstreamServer.getMaxFails() != null) {
         html = html + "max_fails=" + upstreamServer.getMaxFails() + " ";
      }

      if (upstreamServer.getMaxConns() != null) {
         html = html + "max_conns=" + upstreamServer.getMaxConns() + " ";
      }

      html = html + "</td><td class='short50'>" + status + "</td>" + monitorStatus + "</tr>";
      return html;
   }

   @Mapping("addOver")
   public JsonResult addOver(String upstreamJson, String upstreamParamJson, String upstreamServerJson) {
      Upstream upstream = (Upstream)JSONUtil.toBean(upstreamJson, Upstream.class);
      List<UpstreamServer> upstreamServers = JSONUtil.toList(JSONUtil.parseArray(upstreamServerJson), UpstreamServer.class);
      if (StrUtil.isEmpty(upstream.getId())) {
         upstream.setSeq(SnowFlakeUtils.getId());
      }

      Long count;
      if (StrUtil.isEmpty(upstream.getId())) {
         count = this.upstreamService.getCountByName(upstream.getName());
         if (count > 0L) {
            return this.renderError(this.m.get("upstreamStr.sameName"));
         }
      } else {
         count = this.upstreamService.getCountByNameWithOutId(upstream.getName(), upstream.getId());
         if (count > 0L) {
            return this.renderError(this.m.get("upstreamStr.sameName"));
         }
      }

      this.upstreamService.addOver(upstream, upstreamServers, upstreamParamJson);
      return this.renderSuccess();
   }

   @Mapping("detail")
   public JsonResult detail(String id) {
      UpstreamExt upstreamExt = new UpstreamExt();
      upstreamExt.setUpstream((Upstream)this.sqlHelper.findById(id, Upstream.class));
      upstreamExt.setUpstreamServerList(this.upstreamService.getUpstreamServers(id));
      upstreamExt.setParamJson(this.paramService.getJsonByTypeId(upstreamExt.getUpstream().getId(), "upstream"));
      return this.renderSuccess(upstreamExt);
   }

   @Mapping("del")
   public JsonResult del(String id) {
      this.upstreamService.deleteById(id);
      return this.renderSuccess();
   }

   @Mapping("setMonitor")
   public JsonResult setMonitor(String id, Integer monitor) {
      Upstream upstream = new Upstream();
      upstream.setId(id);
      upstream.setMonitor(monitor);
      this.sqlHelper.updateById(upstream);
      return this.renderSuccess();
   }

   @Mapping("upstreamStatus")
   public JsonResult upstreamStatus() {
      Map<String, String> map = new HashMap();
      map.put("mail", this.settingService.get("mail"));
      String upstreamMonitor = this.settingService.get("upstreamMonitor");
      map.put("upstreamMonitor", upstreamMonitor != null ? upstreamMonitor : "false");
      return this.renderSuccess(map);
   }

   @Mapping("upstreamOver")
   public JsonResult upstreamOver(String mail, String upstreamMonitor) {
      this.settingService.set("mail", mail);
      this.settingService.set("upstreamMonitor", upstreamMonitor);
      if (upstreamMonitor.equals("true")) {
         this.upstreamService.resetMonitorStatus();
      }

      return this.renderSuccess();
   }

   @Mapping("setOrder")
   public JsonResult setOrder(String id, Integer count) {
      this.upstreamService.setSeq(id, count);
      return this.renderSuccess();
   }

   @Mapping("getDescr")
   public JsonResult getDescr(String id) {
      Upstream upstream = (Upstream)this.sqlHelper.findById(id, Upstream.class);
      return this.renderSuccess(upstream.getDescr());
   }

   @Mapping("editDescr")
   public JsonResult editDescr(String id, String descr) {
      Upstream upstream = new Upstream();
      upstream.setId(id);
      upstream.setDescr(descr);
      this.sqlHelper.updateById(upstream);
      return this.renderSuccess();
   }
}
