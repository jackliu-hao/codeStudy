package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;

public class TlbFunctionDispId extends TlbAbstractMethod {
   public TlbFunctionDispId(int count, int index, TypeLibUtil typeLibUtil, OaIdl.FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
      super(index, typeLibUtil, funcDesc, typeInfoUtil);
      String[] names = typeInfoUtil.getNames(funcDesc.memid, this.paramCount + 1);

      for(int i = 0; i < this.paramCount; ++i) {
         OaIdl.ELEMDESC elemdesc = funcDesc.lprgelemdescParam.elemDescArg[i];
         String methodName = names[i + 1].toLowerCase();
         String type = this.getType(elemdesc.tdesc);
         String _methodName = this.replaceJavaKeyword(methodName);
         this.methodparams = this.methodparams + type + " " + _methodName;
         if (type.equals("VARIANT")) {
            this.methodvariables = this.methodvariables + _methodName;
         } else {
            this.methodvariables = this.methodvariables + "new VARIANT(" + _methodName + ")";
         }

         if (i < this.paramCount - 1) {
            this.methodparams = this.methodparams + ", ";
            this.methodvariables = this.methodvariables + ", ";
         }
      }

      String returnValue;
      if (this.returnType.equalsIgnoreCase("VARIANT")) {
         returnValue = "pResult";
      } else {
         returnValue = "((" + this.returnType + ") pResult.getValue())";
      }

      this.replaceVariable("helpstring", this.docStr);
      this.replaceVariable("returntype", this.returnType);
      this.replaceVariable("returnvalue", returnValue);
      this.replaceVariable("methodname", this.methodName);
      this.replaceVariable("methodparams", this.methodparams);
      this.replaceVariable("methodvariables", this.methodvariables);
      this.replaceVariable("vtableid", String.valueOf(this.vtableId));
      this.replaceVariable("memberid", String.valueOf(this.memberid));
      this.replaceVariable("functionCount", String.valueOf(count));
   }

   protected String getClassTemplate() {
      return "com/sun/jna/platform/win32/COM/tlb/imp/TlbFunctionDispId.template";
   }
}
