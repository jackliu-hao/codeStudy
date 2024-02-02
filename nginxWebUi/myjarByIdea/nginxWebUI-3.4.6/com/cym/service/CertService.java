package com.cym.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.cym.config.HomeConfig;
import com.cym.model.Cert;
import com.cym.model.CertCode;
import com.cym.sqlhelper.bean.Page;
import com.cym.sqlhelper.reflection.SerializableFunction;
import com.cym.sqlhelper.utils.ConditionAndWrapper;
import com.cym.sqlhelper.utils.ConditionWrapper;
import com.cym.sqlhelper.utils.SqlHelper;
import java.io.File;
import java.lang.invoke.SerializedLambda;
import java.util.Iterator;
import java.util.List;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CertService {
   Logger logger = LoggerFactory.getLogger(this.getClass());
   @Inject
   SqlHelper sqlHelper;
   @Inject
   HomeConfig homeConfig;

   public boolean hasSame(Cert cert) {
      if (StrUtil.isEmpty(cert.getId())) {
         if (this.sqlHelper.findCountByQuery((new ConditionAndWrapper()).eq((String)"domain", cert.getDomain()), Cert.class) > 0L) {
            return true;
         }
      } else if (this.sqlHelper.findCountByQuery((new ConditionAndWrapper()).eq((String)"domain", cert.getDomain()).ne((String)"id", cert.getId()), Cert.class) > 0L) {
         return true;
      }

      return false;
   }

   public List<CertCode> getCertCodes(String certId) {
      return this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((SerializableFunction)(CertCode::getCertId), certId), CertCode.class);
   }

   public void insertOrUpdate(Cert cert, String[] domain, String[] type, String[] value) {
      this.sqlHelper.insertOrUpdate(cert);
      this.sqlHelper.deleteByQuery((new ConditionAndWrapper()).eq((SerializableFunction)(CertCode::getCertId), cert.getId()), CertCode.class);
      if (domain != null && type != null && value != null) {
         for(int i = 0; i < domain.length; ++i) {
            CertCode certCode = new CertCode();
            certCode.setCertId(cert.getId());
            certCode.setDomain(domain[i]);
            certCode.setType(type[i]);
            certCode.setValue(value[i]);
            this.sqlHelper.insert(certCode);
         }
      }

   }

   public Page getPage(String keywords, Page page) {
      ConditionAndWrapper conditionAndWrapper = new ConditionAndWrapper();
      if (StrUtil.isNotEmpty(keywords)) {
         conditionAndWrapper.like(Cert::getDomain, keywords);
      }

      return this.sqlHelper.findPage((ConditionWrapper)conditionAndWrapper, page, Cert.class);
   }

   public void saveCertCode(String certId, List<CertCode> mapList) {
      this.sqlHelper.deleteByQuery((new ConditionAndWrapper()).eq((SerializableFunction)(CertCode::getCertId), certId), CertCode.class);
      Iterator var3 = mapList.iterator();

      while(var3.hasNext()) {
         CertCode certCode = (CertCode)var3.next();
         certCode.setCertId(certId);
         this.sqlHelper.insert(certCode);
      }

   }

   public boolean hasCode(String certId) {
      return this.sqlHelper.findCountByQuery((new ConditionAndWrapper()).eq((SerializableFunction)(CertCode::getCertId), certId), CertCode.class) > 0L;
   }

   public String getAcmeZipBase64() {
      File file = ZipUtil.zip(this.homeConfig.home + ".acme.sh", this.homeConfig.home + "temp" + File.separator + "cert.zip");
      String str = Base64.encode(file);
      file.delete();
      return str;
   }

   public void writeAcmeZipBase64(String acmeZip) {
      Base64.decodeToFile(acmeZip, new File(this.homeConfig.home + "acme.zip"));
      FileUtil.mkdir(this.homeConfig.acmeShDir);
      ZipUtil.unzip(this.homeConfig.home + "acme.zip", this.homeConfig.acmeShDir);
      FileUtil.del(this.homeConfig.home + "acme.zip");
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "getDomain":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/Cert") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Cert::getDomain;
            }
            break;
         case "getCertId":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/CertCode") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return CertCode::getCertId;
            }

            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/CertCode") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return CertCode::getCertId;
            }

            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/CertCode") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return CertCode::getCertId;
            }

            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/cym/sqlhelper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/cym/model/CertCode") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return CertCode::getCertId;
            }
      }

      throw new IllegalArgumentException("Invalid lambda deserialization");
   }
}
