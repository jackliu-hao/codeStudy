package com.cym.controller.adminPage;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.cym.ext.AsycPack;
import com.cym.model.Cert;
import com.cym.model.CertCode;
import com.cym.service.CertService;
import com.cym.service.ConfService;
import com.cym.sqlhelper.utils.ConditionAndWrapper;
import com.cym.utils.BaseController;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.DownloadedFile;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@Mapping("/adminPage/export")
public class ExportController extends BaseController {
   Logger logger = LoggerFactory.getLogger(this.getClass());
   @Inject
   ConfService confService;
   @Inject
   CertService certService;

   @Mapping("")
   public ModelAndView index(ModelAndView modelAndView) {
      modelAndView.view("/adminPage/export/index.html");
      return modelAndView;
   }

   @Mapping("dataExport")
   public DownloadedFile dataExport(Context context) throws IOException {
      AsycPack asycPack = this.confService.getAsycPack(new String[]{"all"});
      asycPack.setCertList(this.sqlHelper.findAll(Cert.class));
      asycPack.setCertCodeList(this.sqlHelper.findAll(CertCode.class));
      asycPack.setAcmeZip(this.certService.getAcmeZipBase64());
      String json = JSONUtil.toJsonPrettyStr((Object)asycPack);
      String date = DateUtil.format(new Date(), "yyyy-MM-dd_HH-mm-ss");
      DownloadedFile downloadedFile = new DownloadedFile("application/octet-stream", new ByteArrayInputStream(json.getBytes(Charset.forName("UTF-8"))), date + ".json");
      return downloadedFile;
   }

   @Mapping("dataImport")
   public void dataImport(UploadedFile file, Context context) throws IOException {
      if (file != null) {
         File tempFile = new File(this.homeConfig.home + "temp" + File.separator + file.name);
         FileUtil.mkdir(tempFile.getParentFile());
         file.transferTo(tempFile);
         String json = FileUtil.readString(tempFile, Charset.forName("UTF-8"));
         tempFile.delete();
         AsycPack asycPack = (AsycPack)JSONUtil.toBean(json, AsycPack.class);
         this.confService.setAsycPack(asycPack);
         if (asycPack.getCertList() != null) {
            this.sqlHelper.deleteByQuery(new ConditionAndWrapper(), Cert.class);
            this.sqlHelper.insertAll(asycPack.getCertList());
         }

         if (asycPack.getCertCodeList() != null) {
            this.sqlHelper.deleteByQuery(new ConditionAndWrapper(), CertCode.class);
            this.sqlHelper.insertAll(asycPack.getCertList());
         }

         this.certService.writeAcmeZipBase64(asycPack.getAcmeZip());
      }

      context.redirect("/adminPage/export?over=true");
   }

   @Mapping("logExport")
   public DownloadedFile logExport(Context context) throws FileNotFoundException {
      File file = new File(this.homeConfig.home + "log/nginxWebUI.log");
      if (file.exists()) {
         DownloadedFile downloadedFile = new DownloadedFile("application/octet-stream", new FileInputStream(file), file.getName());
         return downloadedFile;
      } else {
         return null;
      }
   }
}
