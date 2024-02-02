package com.cym.controller.api;

import cn.hutool.core.util.StrUtil;
import com.cym.model.Basic;
import com.cym.model.Http;
import com.cym.model.Stream;
import com.cym.service.BasicService;
import com.cym.service.HttpService;
import com.cym.service.StreamService;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import com.cym.utils.SnowFlakeUtils;
import java.util.List;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

@Mapping("/api/basic")
@Controller
public class BasicApiController extends BaseController {
   @Inject
   HttpService httpService;
   @Inject
   BasicService basicService;
   @Inject
   StreamService streamService;

   @Mapping("getHttp")
   public JsonResult<List<Http>> getHttp() {
      return this.renderSuccess(this.httpService.findAll());
   }

   @Mapping("insertOrUpdateHttp")
   public JsonResult<Http> insertOrUpdateHttp(Http http) {
      if (!StrUtil.isEmpty(http.getName()) && !StrUtil.isEmpty(http.getValue())) {
         if (StrUtil.isEmpty(http.getId())) {
            http.setSeq(SnowFlakeUtils.getId());
         }

         this.sqlHelper.insertOrUpdate(http);
         return this.renderSuccess(http);
      } else {
         return this.renderError(this.m.get("apiStr.noContent"));
      }
   }

   @Mapping("delHttp")
   public JsonResult delHttp(String id) {
      this.sqlHelper.deleteById(id, Http.class);
      return this.renderSuccess();
   }

   @Mapping("getBasic")
   public JsonResult<List<Basic>> getBasic() {
      return this.renderSuccess(this.basicService.findAll());
   }

   @Mapping("insertOrUpdateBasic")
   public JsonResult<Basic> insertOrUpdateBasic(Basic basic) {
      if (!StrUtil.isEmpty(basic.getName()) && !StrUtil.isEmpty(basic.getValue())) {
         if (StrUtil.isEmpty(basic.getId())) {
            basic.setSeq(SnowFlakeUtils.getId());
         }

         this.sqlHelper.insertOrUpdate(basic);
         return this.renderSuccess(basic);
      } else {
         return this.renderError(this.m.get("apiStr.noContent"));
      }
   }

   @Mapping("delBasic")
   public JsonResult delBasic(String id) {
      this.sqlHelper.deleteById(id, Basic.class);
      return this.renderSuccess();
   }

   @Mapping("getStream")
   public JsonResult<List<Stream>> getStream() {
      return this.renderSuccess(this.streamService.findAll());
   }

   @Mapping("insertOrUpdateStream")
   public JsonResult<Stream> insertOrUpdateStream(Stream stream) {
      if (!StrUtil.isEmpty(stream.getName()) && !StrUtil.isEmpty(stream.getValue())) {
         if (StrUtil.isEmpty(stream.getId())) {
            stream.setSeq(SnowFlakeUtils.getId());
         }

         this.sqlHelper.insertOrUpdate(stream);
         return this.renderSuccess(stream);
      } else {
         return this.renderError(this.m.get("apiStr.noContent"));
      }
   }

   @Mapping("delStream")
   public JsonResult delStream(String id) {
      this.sqlHelper.deleteById(id, Stream.class);
      return this.renderSuccess();
   }
}
