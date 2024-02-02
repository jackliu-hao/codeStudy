package com.cym.controller.api;

import cn.hutool.core.util.StrUtil;
import com.cym.model.Param;
import com.cym.service.ParamService;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import java.io.IOException;
import java.util.List;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

@Mapping("/api/param")
@Controller
public class ParamApiController extends BaseController {
   @Inject
   ParamService paramService;

   @Mapping("getList")
   public JsonResult<List<Param>> getList(String serverId, String locationId, String upstreamId) {
      if (StrUtil.isEmpty(serverId) && StrUtil.isEmpty(locationId) && StrUtil.isEmpty(upstreamId)) {
         return this.renderError(this.m.get("apiStr.paramError"));
      } else {
         List<Param> list = this.paramService.getList(serverId, locationId, upstreamId);
         return this.renderSuccess(list);
      }
   }

   @Mapping("insertOrUpdate")
   public JsonResult<?> insertOrUpdate(Param param) throws IOException {
      Integer count = 0;
      if (StrUtil.isNotEmpty(param.getLocationId())) {
         count = count + 1;
      }

      if (StrUtil.isNotEmpty(param.getServerId())) {
         count = count + 1;
      }

      if (StrUtil.isNotEmpty(param.getUpstreamId())) {
         count = count + 1;
      }

      if (count != 1) {
         return this.renderError(this.m.get("apiStr.paramError"));
      } else {
         this.sqlHelper.insertOrUpdate(param);
         return this.renderSuccess(param);
      }
   }

   @Mapping("del")
   public JsonResult<?> del(String id) {
      this.sqlHelper.deleteById(id, Param.class);
      return this.renderSuccess();
   }
}
