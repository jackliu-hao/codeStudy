package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;

public class TlbPropertyGetStub extends TlbAbstractMethod {
   public TlbPropertyGetStub(int index, TypeLibUtil typeLibUtil, OaIdl.FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
      super(index, typeLibUtil, funcDesc, typeInfoUtil);
      TypeInfoUtil.TypeInfoDoc typeInfoDoc = typeInfoUtil.getDocumentation(funcDesc.memid);
      String docStr = typeInfoDoc.getDocString();
      String methodname = "get" + typeInfoDoc.getName();
      this.replaceVariable("helpstring", docStr);
      this.replaceVariable("returntype", this.returnType);
      this.replaceVariable("methodname", methodname);
      this.replaceVariable("vtableid", String.valueOf(this.vtableId));
      this.replaceVariable("memberid", String.valueOf(this.memberid));
   }

   protected String getClassTemplate() {
      return "com/sun/jna/platform/win32/COM/tlb/imp/TlbPropertyGetStub.template";
   }
}
