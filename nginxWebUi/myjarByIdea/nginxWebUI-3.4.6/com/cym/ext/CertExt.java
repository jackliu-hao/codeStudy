package com.cym.ext;

import com.cym.model.Cert;
import com.cym.model.CertCode;
import java.util.List;

public class CertExt {
   Cert cert;
   List<CertCode> certCodes;

   public Cert getCert() {
      return this.cert;
   }

   public void setCert(Cert cert) {
      this.cert = cert;
   }

   public List<CertCode> getCertCodes() {
      return this.certCodes;
   }

   public void setCertCodes(List<CertCode> certCodes) {
      this.certCodes = certCodes;
   }
}
