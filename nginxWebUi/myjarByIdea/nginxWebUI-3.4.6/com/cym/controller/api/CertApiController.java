package com.cym.controller.api;

import cn.hutool.core.util.StrUtil;
import com.cym.controller.adminPage.CertController;
import com.cym.model.Cert;
import com.cym.model.CertCode;
import com.cym.service.CertService;
import com.cym.sqlhelper.bean.Page;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import java.io.IOException;
import java.util.List;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

@Mapping("/api/cert")
@Controller
public class CertApiController extends BaseController {
   @Inject
   CertController certController;
   @Inject
   CertService certService;

   @Mapping("getPage")
   public JsonResult<Page<Cert>> getPage(Integer current, Integer limit, String keywords) {
      Page page = new Page();
      page.setCurr(current);
      page.setLimit(limit);
      page = this.certService.getPage(keywords, page);
      return this.renderSuccess(page);
   }

   @Mapping("addOver")
   public JsonResult addOver(Cert cert) {
      if (StrUtil.isEmpty(cert.getDomain())) {
         return this.renderError("域名为空");
      } else {
         if (cert.getType() == 0) {
            if (StrUtil.isEmpty(cert.getDnsType())) {
               return this.renderError("dns提供商为空");
            }

            if (cert.getDnsType().equals("ali") && (StrUtil.isEmpty(cert.getAliKey()) || StrUtil.isEmpty(cert.getAliSecret()))) {
               return this.renderError("aliKey 或 aliSecret为空");
            }

            if (cert.getDnsType().equals("dp") && (StrUtil.isEmpty(cert.getDpId()) || StrUtil.isEmpty(cert.getDpKey()))) {
               return this.renderError("dpId 或 dpKey为空");
            }

            if (cert.getDnsType().equals("cf") && (StrUtil.isEmpty(cert.getCfEmail()) || StrUtil.isEmpty(cert.getCfKey()))) {
               return this.renderError("cfEmail 或 cfKey为空");
            }

            if (cert.getDnsType().equals("gd") && (StrUtil.isEmpty(cert.getGdKey()) || StrUtil.isEmpty(cert.getGdSecret()))) {
               return this.renderError("gdKey 或 gdSecret为空");
            }
         }

         return this.certController.addOver(cert, (String[])null, (String[])null, (String[])null);
      }
   }

   @Mapping("getTxtValue")
   public JsonResult<List<CertCode>> getTxtValue(String certId) {
      List<CertCode> certCodes = this.certService.getCertCodes(certId);
      return this.renderSuccess(certCodes);
   }

   @Mapping("setAutoRenew")
   public JsonResult setAutoRenew(String id, Integer autoRenew) {
      Cert cert = new Cert();
      cert.setId(id);
      cert.setAutoRenew(autoRenew);
      this.certController.setAutoRenew(cert);
      return this.renderSuccess();
   }

   @Mapping("del")
   public JsonResult del(String id) {
      return this.certController.del(id);
   }

   @Mapping("apply")
   public JsonResult<List<CertCode>> apply(String id, String type) {
      JsonResult jsonResult = this.certController.apply(id, type);
      if (jsonResult.isSuccess() && jsonResult.getObj() != null) {
         jsonResult.setMsg(this.m.get("certStr.dnsDescr"));
      }

      return jsonResult;
   }

   @Mapping("download")
   public void download(String id) throws IOException {
      this.certController.download(id);
   }
}
