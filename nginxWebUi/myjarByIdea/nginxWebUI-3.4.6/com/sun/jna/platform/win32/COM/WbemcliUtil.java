package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class WbemcliUtil {
   public static final WbemcliUtil INSTANCE = new WbemcliUtil();
   public static final String DEFAULT_NAMESPACE = "ROOT\\CIMV2";

   public static boolean hasNamespace(String namespace) {
      String ns = namespace;
      if (namespace.toUpperCase().startsWith("ROOT\\")) {
         ns = namespace.substring(5);
      }

      WmiQuery<NamespaceProperty> namespaceQuery = new WmiQuery("ROOT", "__NAMESPACE", NamespaceProperty.class);
      WmiResult<NamespaceProperty> namespaces = namespaceQuery.execute();

      for(int i = 0; i < namespaces.getResultCount(); ++i) {
         if (ns.equalsIgnoreCase((String)namespaces.getValue(WbemcliUtil.NamespaceProperty.NAME, i))) {
            return true;
         }
      }

      return false;
   }

   public static Wbemcli.IWbemServices connectServer(String namespace) {
      Wbemcli.IWbemLocator loc = Wbemcli.IWbemLocator.create();
      if (loc == null) {
         throw new COMException("Failed to create WbemLocator object.");
      } else {
         Wbemcli.IWbemServices services = loc.ConnectServer(namespace, (String)null, (String)null, (String)null, 0, (String)null, (Wbemcli.IWbemContext)null);
         loc.Release();
         WinNT.HRESULT hres = Ole32.INSTANCE.CoSetProxyBlanket(services, 10, 0, (WTypes.LPOLESTR)null, 3, 3, (Pointer)null, 0);
         if (COMUtils.FAILED(hres)) {
            services.Release();
            throw new COMException("Could not set proxy blanket.", hres);
         } else {
            return services;
         }
      }
   }

   public class WmiResult<T extends Enum<T>> {
      private Map<T, List<Object>> propertyMap;
      private Map<T, Integer> vtTypeMap;
      private Map<T, Integer> cimTypeMap;
      private int resultCount = 0;

      public WmiResult(Class<T> propertyEnum) {
         this.propertyMap = new EnumMap(propertyEnum);
         this.vtTypeMap = new EnumMap(propertyEnum);
         this.cimTypeMap = new EnumMap(propertyEnum);
         Enum[] var3 = (Enum[])propertyEnum.getEnumConstants();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            T prop = var3[var5];
            this.propertyMap.put(prop, new ArrayList());
            this.vtTypeMap.put(prop, 1);
            this.cimTypeMap.put(prop, 0);
         }

      }

      public Object getValue(T property, int index) {
         return ((List)this.propertyMap.get(property)).get(index);
      }

      public int getVtType(T property) {
         return (Integer)this.vtTypeMap.get(property);
      }

      public int getCIMType(T property) {
         return (Integer)this.cimTypeMap.get(property);
      }

      private void add(int vtType, int cimType, T property, Object o) {
         ((List)this.propertyMap.get(property)).add(o);
         if (vtType != 1 && ((Integer)this.vtTypeMap.get(property)).equals(1)) {
            this.vtTypeMap.put(property, vtType);
         }

         if (((Integer)this.cimTypeMap.get(property)).equals(0)) {
            this.cimTypeMap.put(property, cimType);
         }

      }

      public int getResultCount() {
         return this.resultCount;
      }

      private void incrementResultCount() {
         ++this.resultCount;
      }
   }

   public static class WmiQuery<T extends Enum<T>> {
      private String nameSpace;
      private String wmiClassName;
      private Class<T> propertyEnum;

      public WmiQuery(String nameSpace, String wmiClassName, Class<T> propertyEnum) {
         this.nameSpace = nameSpace;
         this.wmiClassName = wmiClassName;
         this.propertyEnum = propertyEnum;
      }

      public WmiQuery(String wmiClassName, Class<T> propertyEnum) {
         this("ROOT\\CIMV2", wmiClassName, propertyEnum);
      }

      public Class<T> getPropertyEnum() {
         return this.propertyEnum;
      }

      public String getNameSpace() {
         return this.nameSpace;
      }

      public void setNameSpace(String nameSpace) {
         this.nameSpace = nameSpace;
      }

      public String getWmiClassName() {
         return this.wmiClassName;
      }

      public void setWmiClassName(String wmiClassName) {
         this.wmiClassName = wmiClassName;
      }

      public WmiResult<T> execute() {
         try {
            return this.execute(-1);
         } catch (TimeoutException var2) {
            throw new COMException("Got a WMI timeout when infinite wait was specified. This should never happen.");
         }
      }

      public WmiResult<T> execute(int timeout) throws TimeoutException {
         if (((Enum[])this.getPropertyEnum().getEnumConstants()).length < 1) {
            throw new IllegalArgumentException("The query's property enum has no values.");
         } else {
            Wbemcli.IWbemServices svc = WbemcliUtil.connectServer(this.getNameSpace());

            WmiResult var4;
            try {
               Wbemcli.IEnumWbemClassObject enumerator = selectProperties(svc, this);

               try {
                  var4 = enumerateProperties(enumerator, this.getPropertyEnum(), timeout);
               } finally {
                  enumerator.Release();
               }
            } finally {
               svc.Release();
            }

            return var4;
         }
      }

      private static <T extends Enum<T>> Wbemcli.IEnumWbemClassObject selectProperties(Wbemcli.IWbemServices svc, WmiQuery<T> query) {
         T[] props = (Enum[])query.getPropertyEnum().getEnumConstants();
         StringBuilder sb = new StringBuilder("SELECT ");
         sb.append(props[0].name());

         for(int i = 1; i < props.length; ++i) {
            sb.append(',').append(props[i].name());
         }

         sb.append(" FROM ").append(query.getWmiClassName());
         return svc.ExecQuery("WQL", sb.toString().replaceAll("\\\\", "\\\\\\\\"), 48, (Wbemcli.IWbemContext)null);
      }

      private static <T extends Enum<T>> WmiResult<T> enumerateProperties(Wbemcli.IEnumWbemClassObject enumerator, Class<T> propertyEnum, int timeout) throws TimeoutException {
         WmiResult<T> values = WbemcliUtil.INSTANCE.new WmiResult(propertyEnum);
         Pointer[] pclsObj = new Pointer[1];
         IntByReference uReturn = new IntByReference(0);
         Map<T, WString> wstrMap = new HashMap();
         WinNT.HRESULT hres = null;
         Enum[] var8 = (Enum[])propertyEnum.getEnumConstants();
         int var9 = var8.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            T property = var8[var10];
            wstrMap.put(property, new WString(property.name()));
         }

         while(enumerator.getPointer() != Pointer.NULL) {
            hres = enumerator.Next(timeout, pclsObj.length, pclsObj, uReturn);
            if (hres.intValue() == 1 || hres.intValue() == 262149) {
               break;
            }

            if (hres.intValue() == 262148) {
               throw new TimeoutException("No results after " + timeout + " ms.");
            }

            if (COMUtils.FAILED(hres)) {
               throw new COMException("Failed to enumerate results.", hres);
            }

            Variant.VARIANT.ByReference pVal = new Variant.VARIANT.ByReference();
            IntByReference pType = new IntByReference();
            Wbemcli.IWbemClassObject clsObj = new Wbemcli.IWbemClassObject(pclsObj[0]);
            Enum[] var20 = (Enum[])propertyEnum.getEnumConstants();
            int var12 = var20.length;

            for(int var13 = 0; var13 < var12; ++var13) {
               T property = var20[var13];
               clsObj.Get((WString)((WString)wstrMap.get(property)), 0, pVal, pType, (IntByReference)null);
               int vtType = ((Number)(pVal.getValue() == null ? 1 : pVal.getVarType())).intValue();
               int cimType = pType.getValue();
               switch (vtType) {
                  case 0:
                  case 1:
                     values.add(vtType, cimType, property, (Object)null);
                     break;
                  case 2:
                     values.add(vtType, cimType, property, pVal.shortValue());
                     break;
                  case 3:
                     values.add(vtType, cimType, property, pVal.intValue());
                     break;
                  case 4:
                     values.add(vtType, cimType, property, pVal.floatValue());
                     break;
                  case 5:
                     values.add(vtType, cimType, property, pVal.doubleValue());
                     break;
                  case 6:
                  case 7:
                  case 9:
                  case 10:
                  case 12:
                  case 13:
                  case 14:
                  case 15:
                  case 16:
                  default:
                     if ((vtType & 8192) != 8192 && (vtType & 13) != 13 && (vtType & 9) != 9 && (vtType & 4096) != 4096) {
                        values.add(vtType, cimType, property, pVal.getValue());
                     } else {
                        values.add(vtType, cimType, property, (Object)null);
                     }
                     break;
                  case 8:
                     values.add(vtType, cimType, property, pVal.stringValue());
                     break;
                  case 11:
                     values.add(vtType, cimType, property, pVal.booleanValue());
                     break;
                  case 17:
                     values.add(vtType, cimType, property, pVal.byteValue());
               }

               OleAuto.INSTANCE.VariantClear(pVal);
            }

            clsObj.Release();
            values.incrementResultCount();
         }

         return values;
      }
   }

   private static enum NamespaceProperty {
      NAME;
   }
}
