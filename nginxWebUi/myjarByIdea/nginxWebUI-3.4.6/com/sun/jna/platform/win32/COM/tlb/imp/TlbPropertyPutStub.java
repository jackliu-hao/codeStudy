package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;

public class TlbPropertyPutStub extends TlbAbstractMethod {
   public TlbPropertyPutStub(int index, TypeLibUtil typeLibUtil, OaIdl.FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
      super(index, typeLibUtil, funcDesc, typeInfoUtil);
      TypeInfoUtil.TypeInfoDoc typeInfoDoc = typeInfoUtil.getDocumentation(funcDesc.memid);
      String docStr = typeInfoDoc.getDocString();
      String methodname = "set" + typeInfoDoc.getName();
      String[] names = typeInfoUtil.getNames(funcDesc.memid, this.paramCount + 1);

      for(int i = 0; i < this.paramCount; ++i) {
         OaIdl.ELEMDESC elemdesc = funcDesc.lprgelemdescParam.elemDescArg[i];
         String varType = this.getType(elemdesc);
         this.methodparams = this.methodparams + varType + " " + this.replaceJavaKeyword(names[i].toLowerCase());
         if (i < this.paramCount - 1) {
            this.methodparams = this.methodparams + ", ";
         }
      }

      this.replaceVariable("helpstring", docStr);
      this.replaceVariable("methodname", methodname);
      this.replaceVariable("methodparams", this.methodparams);
      this.replaceVariable("vtableid", String.valueOf(this.vtableId));
      this.replaceVariable("memberid", String.valueOf(this.memberid));
   }

   protected String getClassTemplate() {
      return "com/sun/jna/platform/win32/COM/tlb/imp/TlbPropertyPutStub.template";
   }
}
