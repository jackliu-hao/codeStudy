package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;

public class TlbPropertyGet extends TlbAbstractMethod {
   public TlbPropertyGet(int count, int index, TypeLibUtil typeLibUtil, OaIdl.FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
      super(index, typeLibUtil, funcDesc, typeInfoUtil);
      this.methodName = "get" + this.getMethodName();
      this.replaceVariable("helpstring", this.docStr);
      this.replaceVariable("returntype", this.returnType);
      this.replaceVariable("methodname", this.methodName);
      this.replaceVariable("vtableid", String.valueOf(this.vtableId));
      this.replaceVariable("memberid", String.valueOf(this.memberid));
      this.replaceVariable("functionCount", String.valueOf(count));
   }

   protected String getClassTemplate() {
      return "com/sun/jna/platform/win32/COM/tlb/imp/TlbPropertyGet.template";
   }
}
