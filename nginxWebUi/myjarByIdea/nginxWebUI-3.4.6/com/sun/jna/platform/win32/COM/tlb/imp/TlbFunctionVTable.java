package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;

public class TlbFunctionVTable extends TlbAbstractMethod {
   public TlbFunctionVTable(int count, int index, TypeLibUtil typeLibUtil, OaIdl.FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
      super(index, typeLibUtil, funcDesc, typeInfoUtil);
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

      this.replaceVariable("helpstring", this.docStr);
      this.replaceVariable("returntype", this.returnType);
      this.replaceVariable("methodname", this.methodName);
      this.replaceVariable("methodparams", this.methodparams);
      this.replaceVariable("methodvariables", this.methodvariables);
      this.replaceVariable("vtableid", String.valueOf(this.vtableId));
      this.replaceVariable("memberid", String.valueOf(this.memberid));
      this.replaceVariable("functionCount", String.valueOf(count));
   }

   protected String getClassTemplate() {
      return "com/sun/jna/platform/win32/COM/tlb/imp/TlbFunctionVTable.template";
   }
}
