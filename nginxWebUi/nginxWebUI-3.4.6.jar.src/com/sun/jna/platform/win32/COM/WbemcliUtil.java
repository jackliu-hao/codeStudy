/*     */ package com.sun.jna.platform.win32.COM;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.WString;
/*     */ import com.sun.jna.platform.win32.Ole32;
/*     */ import com.sun.jna.platform.win32.OleAuto;
/*     */ import com.sun.jna.platform.win32.Variant;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WbemcliUtil
/*     */ {
/*  58 */   public static final WbemcliUtil INSTANCE = new WbemcliUtil();
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String DEFAULT_NAMESPACE = "ROOT\\CIMV2";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private enum NamespaceProperty
/*     */   {
/*  69 */     NAME;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class WmiQuery<T extends Enum<T>>
/*     */   {
/*     */     private String nameSpace;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private String wmiClassName;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private Class<T> propertyEnum;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WmiQuery(String nameSpace, String wmiClassName, Class<T> propertyEnum) {
/*  95 */       this.nameSpace = nameSpace;
/*  96 */       this.wmiClassName = wmiClassName;
/*  97 */       this.propertyEnum = propertyEnum;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WmiQuery(String wmiClassName, Class<T> propertyEnum) {
/* 108 */       this("ROOT\\CIMV2", wmiClassName, propertyEnum);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Class<T> getPropertyEnum() {
/* 115 */       return this.propertyEnum;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getNameSpace() {
/* 122 */       return this.nameSpace;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void setNameSpace(String nameSpace) {
/* 130 */       this.nameSpace = nameSpace;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getWmiClassName() {
/* 137 */       return this.wmiClassName;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void setWmiClassName(String wmiClassName) {
/* 145 */       this.wmiClassName = wmiClassName;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WbemcliUtil.WmiResult<T> execute() {
/*     */       try {
/* 156 */         return execute(-1);
/* 157 */       } catch (TimeoutException e) {
/* 158 */         throw new COMException("Got a WMI timeout when infinite wait was specified. This should never happen.");
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WbemcliUtil.WmiResult<T> execute(int timeout) throws TimeoutException {
/* 179 */       if (((Enum[])getPropertyEnum().getEnumConstants()).length < 1) {
/* 180 */         throw new IllegalArgumentException("The query's property enum has no values.");
/*     */       }
/*     */ 
/*     */       
/* 184 */       Wbemcli.IWbemServices svc = WbemcliUtil.connectServer(getNameSpace());
/*     */ 
/*     */       
/*     */       try {
/* 188 */         Wbemcli.IEnumWbemClassObject enumerator = selectProperties(svc, this);
/*     */ 
/*     */ 
/*     */       
/*     */       }
/*     */       finally {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 198 */         svc.Release();
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private static <T extends Enum<T>> Wbemcli.IEnumWbemClassObject selectProperties(Wbemcli.IWbemServices svc, WmiQuery<T> query) {
/* 218 */       Enum[] arrayOfEnum = query.getPropertyEnum().getEnumConstants();
/* 219 */       StringBuilder sb = new StringBuilder("SELECT ");
/*     */       
/* 221 */       sb.append(arrayOfEnum[0].name());
/* 222 */       for (int i = 1; i < arrayOfEnum.length; i++) {
/* 223 */         sb.append(',').append(arrayOfEnum[i].name());
/*     */       }
/* 225 */       sb.append(" FROM ").append(query.getWmiClassName());
/*     */ 
/*     */       
/* 228 */       return svc.ExecQuery("WQL", sb.toString().replaceAll("\\\\", "\\\\\\\\"), 48, null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private static <T extends Enum<T>> WbemcliUtil.WmiResult<T> enumerateProperties(Wbemcli.IEnumWbemClassObject enumerator, Class<T> propertyEnum, int timeout) throws TimeoutException {
/* 287 */       WbemcliUtil.INSTANCE.getClass(); WbemcliUtil.WmiResult<T> values = new WbemcliUtil.WmiResult<T>(propertyEnum);
/*     */ 
/*     */       
/* 290 */       Pointer[] pclsObj = new Pointer[1];
/* 291 */       IntByReference uReturn = new IntByReference(0);
/* 292 */       Map<T, WString> wstrMap = new HashMap<T, WString>();
/* 293 */       WinNT.HRESULT hres = null;
/* 294 */       for (Enum enum_ : (Enum[])propertyEnum.getEnumConstants()) {
/* 295 */         wstrMap.put((T)enum_, new WString(enum_.name()));
/*     */       }
/* 297 */       while (enumerator.getPointer() != Pointer.NULL) {
/*     */ 
/*     */         
/* 300 */         hres = enumerator.Next(timeout, pclsObj.length, pclsObj, uReturn);
/*     */         
/* 302 */         if (hres.intValue() == 1 || hres.intValue() == 262149) {
/*     */           break;
/*     */         }
/*     */         
/* 306 */         if (hres.intValue() == 262148) {
/* 307 */           throw new TimeoutException("No results after " + timeout + " ms.");
/*     */         }
/*     */         
/* 310 */         if (COMUtils.FAILED(hres)) {
/* 311 */           throw new COMException("Failed to enumerate results.", hres);
/*     */         }
/*     */         
/* 314 */         Variant.VARIANT.ByReference pVal = new Variant.VARIANT.ByReference();
/* 315 */         IntByReference pType = new IntByReference();
/*     */ 
/*     */         
/* 318 */         Wbemcli.IWbemClassObject clsObj = new Wbemcli.IWbemClassObject(pclsObj[0]);
/* 319 */         for (Enum enum_ : (Enum[])propertyEnum.getEnumConstants()) {
/* 320 */           clsObj.Get(wstrMap.get(enum_), 0, pVal, pType, (IntByReference)null);
/* 321 */           int vtType = ((pVal.getValue() == null) ? Integer.valueOf(1) : pVal.getVarType()).intValue();
/* 322 */           int cimType = pType.getValue();
/* 323 */           switch (vtType) {
/*     */             case 8:
/* 325 */               values.add(vtType, cimType, (T)enum_, pVal.stringValue());
/*     */               break;
/*     */             case 3:
/* 328 */               values.add(vtType, cimType, (T)enum_, Integer.valueOf(pVal.intValue()));
/*     */               break;
/*     */             case 17:
/* 331 */               values.add(vtType, cimType, (T)enum_, Byte.valueOf(pVal.byteValue()));
/*     */               break;
/*     */             case 2:
/* 334 */               values.add(vtType, cimType, (T)enum_, Short.valueOf(pVal.shortValue()));
/*     */               break;
/*     */             case 11:
/* 337 */               values.add(vtType, cimType, (T)enum_, Boolean.valueOf(pVal.booleanValue()));
/*     */               break;
/*     */             case 4:
/* 340 */               values.add(vtType, cimType, (T)enum_, Float.valueOf(pVal.floatValue()));
/*     */               break;
/*     */             case 5:
/* 343 */               values.add(vtType, cimType, (T)enum_, Double.valueOf(pVal.doubleValue()));
/*     */               break;
/*     */             case 0:
/*     */             case 1:
/* 347 */               values.add(vtType, cimType, (T)enum_, null);
/*     */               break;
/*     */             
/*     */             default:
/* 351 */               if ((vtType & 0x2000) == 8192 || (vtType & 0xD) == 13 || (vtType & 0x9) == 9 || (vtType & 0x1000) == 4096) {
/*     */ 
/*     */ 
/*     */                 
/* 355 */                 values.add(vtType, cimType, (T)enum_, null); break;
/*     */               } 
/* 357 */               values.add(vtType, cimType, (T)enum_, pVal.getValue());
/*     */               break;
/*     */           } 
/* 360 */           OleAuto.INSTANCE.VariantClear((Variant.VARIANT)pVal);
/*     */         } 
/* 362 */         clsObj.Release();
/*     */         
/* 364 */         values.incrementResultCount();
/*     */       } 
/* 366 */       return values;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public class WmiResult<T extends Enum<T>>
/*     */   {
/*     */     private Map<T, List<Object>> propertyMap;
/*     */     
/*     */     private Map<T, Integer> vtTypeMap;
/*     */     private Map<T, Integer> cimTypeMap;
/* 377 */     private int resultCount = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WmiResult(Class<T> propertyEnum) {
/* 384 */       this.propertyMap = new EnumMap<T, List<Object>>(propertyEnum);
/* 385 */       this.vtTypeMap = new EnumMap<T, Integer>(propertyEnum);
/* 386 */       this.cimTypeMap = new EnumMap<T, Integer>(propertyEnum);
/* 387 */       for (Enum enum_ : (Enum[])propertyEnum.getEnumConstants()) {
/* 388 */         this.propertyMap.put((T)enum_, new ArrayList());
/* 389 */         this.vtTypeMap.put((T)enum_, Integer.valueOf(1));
/* 390 */         this.cimTypeMap.put((T)enum_, Integer.valueOf(0));
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object getValue(T property, int index) {
/* 408 */       return ((List)this.propertyMap.get(property)).get(index);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getVtType(T property) {
/* 421 */       return ((Integer)this.vtTypeMap.get(property)).intValue();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getCIMType(T property) {
/* 433 */       return ((Integer)this.cimTypeMap.get(property)).intValue();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private void add(int vtType, int cimType, T property, Object o) {
/* 449 */       ((List<Object>)this.propertyMap.get(property)).add(o);
/* 450 */       if (vtType != 1 && ((Integer)this.vtTypeMap.get(property)).equals(Integer.valueOf(1))) {
/* 451 */         this.vtTypeMap.put(property, Integer.valueOf(vtType));
/*     */       }
/* 453 */       if (((Integer)this.cimTypeMap.get(property)).equals(Integer.valueOf(0))) {
/* 454 */         this.cimTypeMap.put(property, Integer.valueOf(cimType));
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getResultCount() {
/* 462 */       return this.resultCount;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private void incrementResultCount() {
/* 469 */       this.resultCount++;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean hasNamespace(String namespace) {
/* 484 */     String ns = namespace;
/* 485 */     if (namespace.toUpperCase().startsWith("ROOT\\")) {
/* 486 */       ns = namespace.substring(5);
/*     */     }
/*     */     
/* 489 */     WmiQuery<NamespaceProperty> namespaceQuery = new WmiQuery<NamespaceProperty>("ROOT", "__NAMESPACE", NamespaceProperty.class);
/* 490 */     WmiResult<NamespaceProperty> namespaces = namespaceQuery.execute();
/* 491 */     for (int i = 0; i < namespaces.getResultCount(); i++) {
/* 492 */       if (ns.equalsIgnoreCase((String)namespaces.getValue(NamespaceProperty.NAME, i))) {
/* 493 */         return true;
/*     */       }
/*     */     } 
/* 496 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Wbemcli.IWbemServices connectServer(String namespace) {
/* 523 */     Wbemcli.IWbemLocator loc = Wbemcli.IWbemLocator.create();
/* 524 */     if (loc == null) {
/* 525 */       throw new COMException("Failed to create WbemLocator object.");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 532 */     Wbemcli.IWbemServices services = loc.ConnectServer(namespace, (String)null, (String)null, (String)null, 0, (String)null, (Wbemcli.IWbemContext)null);
/*     */ 
/*     */     
/* 535 */     loc.Release();
/*     */ 
/*     */ 
/*     */     
/* 539 */     WinNT.HRESULT hres = Ole32.INSTANCE.CoSetProxyBlanket(services, 10, 0, null, 3, 3, null, 0);
/*     */     
/* 541 */     if (COMUtils.FAILED(hres)) {
/* 542 */       services.Release();
/* 543 */       throw new COMException("Could not set proxy blanket.", hres);
/*     */     } 
/* 545 */     return services;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\WbemcliUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */