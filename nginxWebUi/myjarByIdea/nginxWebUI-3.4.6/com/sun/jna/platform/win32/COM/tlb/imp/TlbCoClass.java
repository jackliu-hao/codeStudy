package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.COM.ITypeInfo;
import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;

public class TlbCoClass extends TlbBase {
   public TlbCoClass(int index, String packagename, TypeLibUtil typeLibUtil, String bindingMode) {
      super(index, typeLibUtil, (TypeInfoUtil)null);
      TypeInfoUtil typeInfoUtil = typeLibUtil.getTypeInfoUtil(index);
      TypeLibUtil.TypeLibDoc typeLibDoc = this.typeLibUtil.getDocumentation(index);
      String docString = typeLibDoc.getDocString();
      if (typeLibDoc.getName().length() > 0) {
         this.name = typeLibDoc.getName();
      }

      this.logInfo("Type of kind 'CoClass' found: " + this.name);
      this.createPackageName(packagename);
      this.createClassName(this.name);
      this.setFilename(this.name);
      String guidStr = this.typeLibUtil.getLibAttr().guid.toGuidString();
      int majorVerNum = this.typeLibUtil.getLibAttr().wMajorVerNum.intValue();
      int minorVerNum = this.typeLibUtil.getLibAttr().wMinorVerNum.intValue();
      String version = majorVerNum + "." + minorVerNum;
      String clsid = typeInfoUtil.getTypeAttr().guid.toGuidString();
      this.createJavaDocHeader(guidStr, version, docString);
      this.createCLSID(clsid);
      this.createCLSIDName(this.name);
      OaIdl.TYPEATTR typeAttr = typeInfoUtil.getTypeAttr();
      int cImplTypes = typeAttr.cImplTypes.intValue();
      String interfaces = "";

      for(int i = 0; i < cImplTypes; ++i) {
         OaIdl.HREFTYPE refTypeOfImplType = typeInfoUtil.getRefTypeOfImplType(i);
         ITypeInfo refTypeInfo = typeInfoUtil.getRefTypeInfo(refTypeOfImplType);
         TypeInfoUtil refTypeInfoUtil = new TypeInfoUtil(refTypeInfo);
         this.createFunctions(refTypeInfoUtil, bindingMode);
         TypeInfoUtil.TypeInfoDoc documentation = refTypeInfoUtil.getDocumentation(new OaIdl.MEMBERID(-1));
         interfaces = interfaces + documentation.getName();
         if (i < cImplTypes - 1) {
            interfaces = interfaces + ", ";
         }
      }

      this.createInterfaces(interfaces);
      this.createContent(this.content);
   }

   protected void createFunctions(TypeInfoUtil typeInfoUtil, String bindingMode) {
      OaIdl.TYPEATTR typeAttr = typeInfoUtil.getTypeAttr();
      int cFuncs = typeAttr.cFuncs.intValue();

      for(int i = 0; i < cFuncs; ++i) {
         OaIdl.FUNCDESC funcDesc = typeInfoUtil.getFuncDesc(i);
         TlbAbstractMethod method = null;
         if (funcDesc.invkind.value == OaIdl.INVOKEKIND.INVOKE_FUNC.value) {
            if (this.isVTableMode()) {
               method = new TlbFunctionVTable(i, this.index, this.typeLibUtil, funcDesc, typeInfoUtil);
            } else {
               method = new TlbFunctionDispId(i, this.index, this.typeLibUtil, funcDesc, typeInfoUtil);
            }
         } else if (funcDesc.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYGET.value) {
            method = new TlbPropertyGet(i, this.index, this.typeLibUtil, funcDesc, typeInfoUtil);
         } else if (funcDesc.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYPUT.value) {
            method = new TlbPropertyPut(i, this.index, this.typeLibUtil, funcDesc, typeInfoUtil);
         } else if (funcDesc.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYPUTREF.value) {
            method = new TlbPropertyPut(i, this.index, this.typeLibUtil, funcDesc, typeInfoUtil);
         }

         if (!this.isReservedMethod(((TlbAbstractMethod)method).getMethodName())) {
            this.content = this.content + ((TlbAbstractMethod)method).getClassBuffer();
            if (i < cFuncs - 1) {
               this.content = this.content + "\n";
            }
         }

         typeInfoUtil.ReleaseFuncDesc(funcDesc);
      }

   }

   protected void createJavaDocHeader(String guid, String version, String helpstring) {
      this.replaceVariable("uuid", guid);
      this.replaceVariable("version", version);
      this.replaceVariable("helpstring", helpstring);
   }

   protected void createCLSIDName(String clsidName) {
      this.replaceVariable("clsidname", clsidName.toUpperCase());
   }

   protected void createCLSID(String clsid) {
      this.replaceVariable("clsid", clsid);
   }

   protected void createInterfaces(String interfaces) {
      this.replaceVariable("interfaces", interfaces);
   }

   protected String getClassTemplate() {
      return "com/sun/jna/platform/win32/COM/tlb/imp/TlbCoClass.template";
   }
}
