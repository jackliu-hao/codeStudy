package com.cym.controller.api;

import cn.hutool.core.util.StrUtil;
import com.cym.model.Location;
import com.cym.model.Server;
import com.cym.service.ParamService;
import com.cym.service.ServerService;
import com.cym.sqlhelper.bean.Page;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import com.cym.utils.SnowFlakeUtils;
import java.util.Iterator;
import java.util.List;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

@Mapping("/api/server")
@Controller
public class ServerApiController extends BaseController {
   @Inject
   ServerService serverService;
   @Inject
   ParamService paramService;

   @Mapping("getPage")
   public JsonResult<Page<Server>> getPage(Integer current, Integer limit, String keywords) {
      Page page = new Page();
      page.setCurr(current);
      page.setLimit(limit);
      page = this.serverService.search(page, keywords);
      return this.renderSuccess(page);
   }

   @Mapping("insertOrUpdate")
   public JsonResult<?> insertOrUpdate(Server server) {
      if (StrUtil.isEmpty(server.getListen())) {
         return this.renderError("listen" + this.m.get("apiStr.notFill"));
      } else {
         if (StrUtil.isEmpty(server.getId())) {
            server.setSeq(SnowFlakeUtils.getId());
         }

         this.sqlHelper.insertOrUpdate(server);
         return this.renderSuccess(server);
      }
   }

   @Mapping("delete")
   public JsonResult<?> delete(String id) {
      this.serverService.deleteById(id);
      return this.renderSuccess();
   }

   @Mapping("getLocationByServerId")
   public JsonResult<List<Location>> getLocationByServerId(String serverId) {
      List<Location> locationList = this.serverService.getLocationByServerId(serverId);
      Iterator var3 = locationList.iterator();

      while(var3.hasNext()) {
         Location location = (Location)var3.next();
         String json = this.paramService.getJsonByTypeId(location.getId(), "location");
         location.setLocationParamJson(json != null ? json : null);
      }

      return this.renderSuccess(locationList);
   }

   @Mapping("insertOrUpdateLocation")
   public JsonResult<?> insertOrUpdateLocation(Location location) {
      if (StrUtil.isEmpty(location.getServerId())) {
         return this.renderError("serverId" + this.m.get("apiStr.notFill"));
      } else if (StrUtil.isEmpty(location.getPath())) {
         return this.renderError("path" + this.m.get("apiStr.notFill"));
      } else {
         this.sqlHelper.insertOrUpdate(location);
         return this.renderSuccess(location);
      }
   }

   @Mapping("deleteLocation")
   public JsonResult<?> deleteLocation(String id) {
      this.sqlHelper.deleteById(id, Location.class);
      return this.renderSuccess();
   }
}
