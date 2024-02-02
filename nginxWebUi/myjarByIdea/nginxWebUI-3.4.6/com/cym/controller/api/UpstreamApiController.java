package com.cym.controller.api;

import cn.hutool.core.util.StrUtil;
import com.cym.model.Upstream;
import com.cym.model.UpstreamServer;
import com.cym.service.UpstreamService;
import com.cym.sqlhelper.bean.Page;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import com.cym.utils.SnowFlakeUtils;
import java.util.List;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

@Mapping("/api/upstream")
@Controller
public class UpstreamApiController extends BaseController {
   @Inject
   UpstreamService upstreamService;

   @Mapping("getPage")
   public JsonResult<Page<Upstream>> getPage(Integer current, Integer limit, String keywords) {
      Page page = new Page();
      page.setCurr(current);
      page.setLimit(limit);
      page = this.upstreamService.search(page, keywords);
      return this.renderSuccess(page);
   }

   @Mapping("insertOrUpdate")
   public JsonResult<?> insertOrUpdate(Upstream upstream) {
      if (StrUtil.isEmpty(upstream.getName())) {
         return this.renderError("name" + this.m.get("apiStr.notFill"));
      } else {
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

         if (StrUtil.isEmpty(upstream.getId())) {
            upstream.setSeq(SnowFlakeUtils.getId());
         }

         this.sqlHelper.insertOrUpdate(upstream);
         return this.renderSuccess(upstream);
      }
   }

   @Mapping("delete")
   public JsonResult<?> delete(String id) {
      this.upstreamService.deleteById(id);
      return this.renderSuccess();
   }

   @Mapping("getServerByUpstreamId")
   public JsonResult<List<UpstreamServer>> getServerByUpstreamId(String upstreamId) {
      List<UpstreamServer> list = this.upstreamService.getUpstreamServers(upstreamId);
      return this.renderSuccess(list);
   }

   @Mapping("insertOrUpdateServer")
   public JsonResult insertOrUpdateServer(UpstreamServer upstreamServer) {
      if (StrUtil.isEmpty(upstreamServer.getUpstreamId())) {
         return this.renderError("upstreamId" + this.m.get("apiStr.notFill"));
      } else if (null == upstreamServer.getPort()) {
         return this.renderError("port" + this.m.get("apiStr.notFill"));
      } else if (StrUtil.isEmpty(upstreamServer.getServer())) {
         return this.renderError("server" + this.m.get("apiStr.notFill"));
      } else {
         this.sqlHelper.insertOrUpdate(upstreamServer);
         return this.renderSuccess(upstreamServer);
      }
   }
}
