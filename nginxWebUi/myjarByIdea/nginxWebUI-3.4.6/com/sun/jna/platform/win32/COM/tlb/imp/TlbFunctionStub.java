package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;

public class TlbFunctionStub extends TlbAbstractMethod {
   public TlbFunctionStub(int index, TypeLibUtil typeLibUtil, OaIdl.FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
      super(index, typeLibUtil, funcDesc, typeInfoUtil);
      TypeInfoUtil.TypeInfoDoc typeInfoDoc = typeInfoUtil.getDocumentation(funcDesc.memid);
      String methodname = typeInfoDoc.getName();
      String docStr = typeInfoDoc.getDocString();
      String[] names = typeInfoUtil.getNames(funcDesc.memid, this.paramCount + 1);
      if (this.paramCount > 0) {
         this.methodvariables = ", ";
      }

      for(int i = 0; i < this.paramCount; ++i) {
         OaIdl.ELEMDESC elemdesc = funcDesc.lprgelemdescParam.elemDescArg[i];
         String methodName = names[i + 1].toLowerCase();
         this.methodparams = this.methodparams + this.getType(elemdesc.tdesc) + " " + this.replaceJavaKeyword(methodName);
         this.methodvariables = this.methodvariables + methodName;
         if (i < this.paramCount - 1) {
            this.methodparams = this.methodparams + ", ";
            this.methodvariables = this.methodvariables + ", ";
         }
      }

      this.replaceVariable("helpstring", docStr);
      this.replaceVariable("returntype", this.returnType);
      this.replaceVariable("methodname", methodname);
      this.replaceVariable("methodparams", this.methodparams);
      this.replaceVariable("vtableid", String.valueOf(this.vtableId));
      this.replaceVariable("memberid", String.valueOf(this.memberid));
   }

   protected String getClassTemplate() {
      return "com/sun/jna/platform/win32/COM/tlb/imp/TlbFunctionStub.template";
   }
}
