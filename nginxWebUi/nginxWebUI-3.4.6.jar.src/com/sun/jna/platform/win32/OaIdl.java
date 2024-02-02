/*      */ package com.sun.jna.platform.win32;
/*      */ 
/*      */ import com.sun.jna.IntegerType;
/*      */ import com.sun.jna.Memory;
/*      */ import com.sun.jna.NativeLong;
/*      */ import com.sun.jna.Pointer;
/*      */ import com.sun.jna.Structure;
/*      */ import com.sun.jna.Structure.FieldOrder;
/*      */ import com.sun.jna.Union;
/*      */ import com.sun.jna.platform.win32.COM.COMUtils;
/*      */ import com.sun.jna.platform.win32.COM.Dispatch;
/*      */ import com.sun.jna.platform.win32.COM.TypeComp;
/*      */ import com.sun.jna.platform.win32.COM.Unknown;
/*      */ import com.sun.jna.ptr.PointerByReference;
/*      */ import java.io.Closeable;
/*      */ import java.util.Date;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public interface OaIdl
/*      */ {
/*   91 */   public static final long DATE_OFFSET = (new Date(-1, 11, 30, 0, 0, 0)).getTime();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"wCode", "wReserved", "bstrSource", "bstrDescription", "bstrHelpFile", "dwHelpContext", "pvReserved", "pfnDeferredFillIn", "scode"})
/*      */   public static class EXCEPINFO
/*      */     extends Structure
/*      */   {
/*      */     public WinDef.WORD wCode;
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.WORD wReserved;
/*      */ 
/*      */ 
/*      */     
/*      */     public WTypes.BSTR bstrSource;
/*      */ 
/*      */ 
/*      */     
/*      */     public WTypes.BSTR bstrDescription;
/*      */ 
/*      */ 
/*      */     
/*      */     public WTypes.BSTR bstrHelpFile;
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORD dwHelpContext;
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.PVOID pvReserved;
/*      */ 
/*      */ 
/*      */     
/*      */     public ByReference pfnDeferredFillIn;
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.SCODE scode;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends EXCEPINFO
/*      */       implements Structure.ByReference {}
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public EXCEPINFO() {}
/*      */ 
/*      */ 
/*      */     
/*      */     public EXCEPINFO(Pointer p) {
/*  149 */       super(p);
/*      */     }
/*      */   }
/*      */   
/*      */   public static class VARIANT_BOOL extends IntegerType {
/*      */     private static final long serialVersionUID = 1L;
/*      */     public static final int SIZE = 2;
/*      */     
/*      */     public VARIANT_BOOL() {
/*  158 */       this(0L);
/*      */     }
/*      */     
/*      */     public VARIANT_BOOL(long value) {
/*  162 */       super(2, value);
/*      */     }
/*      */     
/*      */     public VARIANT_BOOL(boolean value) {
/*  166 */       this(value ? 65535L : 0L);
/*      */     }
/*      */     
/*      */     public boolean booleanValue() {
/*  170 */       return (shortValue() != 0);
/*      */     }
/*      */   }
/*      */   
/*      */   public static class _VARIANT_BOOL extends VARIANT_BOOL {
/*      */     private static final long serialVersionUID = 1L;
/*      */     
/*      */     public _VARIANT_BOOL() {
/*  178 */       this(0L);
/*      */     }
/*      */     
/*      */     public _VARIANT_BOOL(long value) {
/*  182 */       super(value);
/*      */     }
/*      */   }
/*      */   
/*      */   public static class VARIANT_BOOLByReference extends com.sun.jna.ptr.ByReference {
/*      */     public VARIANT_BOOLByReference() {
/*  188 */       this(new OaIdl.VARIANT_BOOL(0L));
/*      */     }
/*      */     
/*      */     public VARIANT_BOOLByReference(OaIdl.VARIANT_BOOL value) {
/*  192 */       super(2);
/*  193 */       setValue(value);
/*      */     }
/*      */     
/*      */     public void setValue(OaIdl.VARIANT_BOOL value) {
/*  197 */       getPointer().setShort(0L, value.shortValue());
/*      */     }
/*      */     
/*      */     public OaIdl.VARIANT_BOOL getValue() {
/*  201 */       return new OaIdl.VARIANT_BOOL(getPointer().getShort(0L));
/*      */     }
/*      */   }
/*      */   
/*      */   public static class _VARIANT_BOOLByReference extends com.sun.jna.ptr.ByReference {
/*      */     public _VARIANT_BOOLByReference() {
/*  207 */       this(new OaIdl.VARIANT_BOOL(0L));
/*      */     }
/*      */     
/*      */     public _VARIANT_BOOLByReference(OaIdl.VARIANT_BOOL value) {
/*  211 */       super(2);
/*  212 */       setValue(value);
/*      */     }
/*      */     
/*      */     public void setValue(OaIdl.VARIANT_BOOL value) {
/*  216 */       getPointer().setShort(0L, value.shortValue());
/*      */     }
/*      */     
/*      */     public OaIdl.VARIANT_BOOL getValue() {
/*  220 */       return new OaIdl.VARIANT_BOOL(getPointer().getShort(0L));
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"date"})
/*      */   public static class DATE
/*      */     extends Structure
/*      */   {
/*      */     private static final long MICRO_SECONDS_PER_DAY = 86400000L;
/*      */     public double date;
/*      */     
/*      */     public static class ByReference
/*      */       extends DATE
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public DATE() {}
/*      */     
/*      */     public DATE(double date) {
/*  239 */       this.date = date;
/*      */     }
/*      */     
/*      */     public DATE(Date javaDate) {
/*  243 */       setFromJavaDate(javaDate);
/*      */     }
/*      */     
/*      */     public Date getAsJavaDate() {
/*  247 */       long days = (long)this.date * 86400000L + OaIdl.DATE_OFFSET;
/*  248 */       double timePart = 24.0D * Math.abs(this.date - (long)this.date);
/*  249 */       int hours = (int)timePart;
/*  250 */       timePart = 60.0D * (timePart - (int)timePart);
/*  251 */       int minutes = (int)timePart;
/*  252 */       timePart = 60.0D * (timePart - (int)timePart);
/*  253 */       int seconds = (int)timePart;
/*  254 */       timePart = 1000.0D * (timePart - (int)timePart);
/*  255 */       int milliseconds = (int)timePart;
/*      */       
/*  257 */       Date baseDate = new Date(days);
/*  258 */       baseDate.setHours(hours);
/*  259 */       baseDate.setMinutes(minutes);
/*  260 */       baseDate.setSeconds(seconds);
/*  261 */       baseDate.setTime(baseDate.getTime() + milliseconds);
/*  262 */       return baseDate;
/*      */     }
/*      */     
/*      */     public void setFromJavaDate(Date javaDate) {
/*  266 */       double msSinceOrigin = (javaDate.getTime() - OaIdl.DATE_OFFSET);
/*  267 */       double daysAsFract = msSinceOrigin / 8.64E7D;
/*      */       
/*  269 */       Date dayDate = new Date(javaDate.getTime());
/*  270 */       dayDate.setHours(0);
/*  271 */       dayDate.setMinutes(0);
/*  272 */       dayDate.setSeconds(0);
/*  273 */       dayDate.setTime(dayDate.getTime() / 1000L * 1000L);
/*      */       
/*  275 */       double integralPart = Math.floor(daysAsFract);
/*  276 */       double fractionalPart = Math.signum(daysAsFract) * (javaDate.getTime() - dayDate.getTime()) / 8.64E7D;
/*      */       
/*  278 */       this.date = integralPart + fractionalPart;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public static class DISPID
/*      */     extends WinDef.LONG
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */     
/*      */     public DISPID() {
/*  289 */       this(0);
/*      */     }
/*      */     
/*      */     public DISPID(int value) {
/*  293 */       super(value);
/*      */     }
/*      */   }
/*      */   
/*      */   public static class DISPIDByReference extends com.sun.jna.ptr.ByReference {
/*      */     public DISPIDByReference() {
/*  299 */       this(new OaIdl.DISPID(0));
/*      */     }
/*      */     
/*      */     public DISPIDByReference(OaIdl.DISPID value) {
/*  303 */       super(OaIdl.DISPID.SIZE);
/*  304 */       setValue(value);
/*      */     }
/*      */     
/*      */     public void setValue(OaIdl.DISPID value) {
/*  308 */       getPointer().setInt(0L, value.intValue());
/*      */     }
/*      */     
/*      */     public OaIdl.DISPID getValue() {
/*  312 */       return new OaIdl.DISPID(getPointer().getInt(0L));
/*      */     }
/*      */   }
/*      */   
/*      */   public static class MEMBERID extends DISPID {
/*      */     private static final long serialVersionUID = 1L;
/*      */     
/*      */     public MEMBERID() {
/*  320 */       this(0);
/*      */     }
/*      */     
/*      */     public MEMBERID(int value) {
/*  324 */       super(value);
/*      */     }
/*      */   }
/*      */   
/*      */   public static class MEMBERIDByReference extends com.sun.jna.ptr.ByReference {
/*      */     public MEMBERIDByReference() {
/*  330 */       this(new OaIdl.MEMBERID(0));
/*      */     }
/*      */     
/*      */     public MEMBERIDByReference(OaIdl.MEMBERID value) {
/*  334 */       super(OaIdl.MEMBERID.SIZE);
/*  335 */       setValue(value);
/*      */     }
/*      */     
/*      */     public void setValue(OaIdl.MEMBERID value) {
/*  339 */       getPointer().setInt(0L, value.intValue());
/*      */     }
/*      */     
/*      */     public OaIdl.MEMBERID getValue() {
/*  343 */       return new OaIdl.MEMBERID(getPointer().getInt(0L));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  350 */   public static final DISPID DISPID_COLLECT = new DISPID(-8);
/*      */ 
/*      */ 
/*      */   
/*  354 */   public static final DISPID DISPID_CONSTRUCTOR = new DISPID(-6);
/*      */ 
/*      */ 
/*      */   
/*  358 */   public static final DISPID DISPID_DESTRUCTOR = new DISPID(-7);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  364 */   public static final DISPID DISPID_EVALUATE = new DISPID(-5);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  370 */   public static final DISPID DISPID_NEWENUM = new DISPID(-4);
/*      */ 
/*      */ 
/*      */   
/*  374 */   public static final DISPID DISPID_PROPERTYPUT = new DISPID(-3);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  379 */   public static final DISPID DISPID_UNKNOWN = new DISPID(-1);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  385 */   public static final DISPID DISPID_VALUE = new DISPID(0);
/*      */   
/*  387 */   public static final MEMBERID MEMBERID_NIL = new MEMBERID(DISPID_UNKNOWN
/*  388 */       .intValue());
/*      */   
/*      */   public static final int FADF_AUTO = 1;
/*      */   
/*      */   public static final int FADF_STATIC = 2;
/*      */   
/*      */   public static final int FADF_EMBEDDED = 4;
/*      */   
/*      */   public static final int FADF_FIXEDSIZE = 16;
/*      */   
/*      */   public static final int FADF_RECORD = 32;
/*      */   
/*      */   public static final int FADF_HAVEIID = 64;
/*      */   
/*      */   public static final int FADF_HAVEVARTYPE = 128;
/*      */   
/*      */   public static final int FADF_BSTR = 256;
/*      */   
/*      */   public static final int FADF_UNKNOWN = 512;
/*      */   
/*      */   public static final int FADF_DISPATCH = 1024;
/*      */   
/*      */   public static final int FADF_VARIANT = 2048;
/*      */   
/*      */   public static final int FADF_RESERVED = 61448;
/*      */ 
/*      */   
/*      */   @FieldOrder({"value"})
/*      */   public static class TYPEKIND
/*      */     extends Structure
/*      */   {
/*      */     public int value;
/*      */     
/*      */     public static final int TKIND_ENUM = 0;
/*      */     
/*      */     public static final int TKIND_RECORD = 1;
/*      */     
/*      */     public static final int TKIND_MODULE = 2;
/*      */     
/*      */     public static final int TKIND_INTERFACE = 3;
/*      */     
/*      */     public static final int TKIND_DISPATCH = 4;
/*      */     
/*      */     public static final int TKIND_COCLASS = 5;
/*      */     
/*      */     public static final int TKIND_ALIAS = 6;
/*      */     
/*      */     public static final int TKIND_UNION = 7;
/*      */     public static final int TKIND_MAX = 8;
/*      */     
/*      */     public static class ByReference
/*      */       extends TYPEKIND
/*      */       implements Structure.ByReference
/*      */     {
/*      */       public ByReference() {}
/*      */       
/*      */       public ByReference(int value) {
/*  445 */         super(value);
/*      */       }
/*      */       
/*      */       public ByReference(OaIdl.TYPEKIND typekind) {
/*  449 */         super(typekind.getPointer());
/*  450 */         this.value = typekind.value;
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public TYPEKIND() {}
/*      */ 
/*      */ 
/*      */     
/*      */     public TYPEKIND(int value) {
/*  461 */       this.value = value;
/*      */     }
/*      */     
/*      */     public TYPEKIND(Pointer pointer) {
/*  465 */       super(pointer);
/*  466 */       read();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"value"})
/*      */   public static class DESCKIND
/*      */     extends Structure
/*      */   {
/*      */     public int value;
/*      */ 
/*      */     
/*      */     public static final int DESCKIND_NONE = 0;
/*      */ 
/*      */     
/*      */     public static final int DESCKIND_FUNCDESC = 1;
/*      */     
/*      */     public static final int DESCKIND_VARDESC = 2;
/*      */     
/*      */     public static final int DESCKIND_TYPECOMP = 3;
/*      */     
/*      */     public static final int DESCKIND_IMPLICITAPPOBJ = 4;
/*      */     
/*      */     public static final int DESCKIND_MAX = 5;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends DESCKIND
/*      */       implements Structure.ByReference {}
/*      */ 
/*      */     
/*      */     public DESCKIND() {}
/*      */ 
/*      */     
/*      */     public DESCKIND(int value) {
/*  502 */       this.value = value;
/*      */     }
/*      */     
/*      */     public DESCKIND(Pointer pointer) {
/*  506 */       super(pointer);
/*  507 */       read();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"cDims", "fFeatures", "cbElements", "cLocks", "pvData", "rgsabound"})
/*      */   public static class SAFEARRAY
/*      */     extends Structure
/*      */     implements Closeable
/*      */   {
/*      */     public WinDef.USHORT cDims;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.USHORT fFeatures;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.ULONG cbElements;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.ULONG cLocks;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.PVOID pvData;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends SAFEARRAY
/*      */       implements Structure.ByReference {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  573 */     public OaIdl.SAFEARRAYBOUND[] rgsabound = new OaIdl.SAFEARRAYBOUND[] { new OaIdl.SAFEARRAYBOUND() };
/*      */ 
/*      */     
/*      */     public SAFEARRAY() {}
/*      */ 
/*      */     
/*      */     public SAFEARRAY(Pointer pointer) {
/*  580 */       super(pointer);
/*  581 */       read();
/*      */     }
/*      */ 
/*      */     
/*      */     public void read() {
/*  586 */       super.read();
/*  587 */       if (this.cDims.intValue() > 0) {
/*  588 */         this.rgsabound = (OaIdl.SAFEARRAYBOUND[])this.rgsabound[0].toArray(this.cDims.intValue());
/*      */       } else {
/*  590 */         this.rgsabound = new OaIdl.SAFEARRAYBOUND[] { new OaIdl.SAFEARRAYBOUND() };
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static ByReference createSafeArray(int... size) {
/*  607 */       return createSafeArray(new WTypes.VARTYPE(12), size);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static ByReference createSafeArray(WTypes.VARTYPE vartype, int... size) {
/*  623 */       OaIdl.SAFEARRAYBOUND[] rgsabound = (OaIdl.SAFEARRAYBOUND[])(new OaIdl.SAFEARRAYBOUND()).toArray(size.length);
/*  624 */       for (int i = 0; i < size.length; i++) {
/*  625 */         (rgsabound[i]).lLbound = new WinDef.LONG(0L);
/*  626 */         (rgsabound[i]).cElements = new WinDef.ULONG(size[size.length - i - 1]);
/*      */       } 
/*  628 */       ByReference data = OleAuto.INSTANCE.SafeArrayCreate(vartype, new WinDef.UINT(size.length), rgsabound);
/*  629 */       return data;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void putElement(Object arg, int... indices) {
/*      */       WinNT.HRESULT hr;
/*      */       Memory mem;
/*  643 */       WinDef.LONG[] paramIndices = new WinDef.LONG[indices.length];
/*  644 */       for (int i = 0; i < indices.length; i++) {
/*  645 */         paramIndices[i] = new WinDef.LONG(indices[indices.length - i - 1]);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  650 */       switch (getVarType().intValue()) {
/*      */         case 11:
/*  652 */           mem = new Memory(2L);
/*  653 */           if (arg instanceof Boolean) {
/*  654 */             mem.setShort(0L, (short)(((Boolean)arg).booleanValue() ? 65535 : 0));
/*      */           } else {
/*  656 */             mem.setShort(0L, (short)((((Number)arg).intValue() > 0) ? 65535 : 0));
/*      */           } 
/*  658 */           hr = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, (Pointer)mem);
/*  659 */           COMUtils.checkRC(hr);
/*      */           return;
/*      */         case 16:
/*      */         case 17:
/*  663 */           mem = new Memory(1L);
/*  664 */           mem.setByte(0L, ((Number)arg).byteValue());
/*  665 */           hr = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, (Pointer)mem);
/*  666 */           COMUtils.checkRC(hr);
/*      */           return;
/*      */         case 2:
/*      */         case 18:
/*  670 */           mem = new Memory(2L);
/*  671 */           mem.setShort(0L, ((Number)arg).shortValue());
/*  672 */           hr = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, (Pointer)mem);
/*  673 */           COMUtils.checkRC(hr);
/*      */           return;
/*      */         case 3:
/*      */         case 19:
/*      */         case 22:
/*      */         case 23:
/*  679 */           mem = new Memory(4L);
/*  680 */           mem.setInt(0L, ((Number)arg).intValue());
/*  681 */           hr = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, (Pointer)mem);
/*  682 */           COMUtils.checkRC(hr);
/*      */           return;
/*      */         case 10:
/*  685 */           mem = new Memory(4L);
/*  686 */           mem.setInt(0L, ((Number)arg).intValue());
/*  687 */           hr = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, (Pointer)mem);
/*  688 */           COMUtils.checkRC(hr);
/*      */           return;
/*      */         case 4:
/*  691 */           mem = new Memory(4L);
/*  692 */           mem.setFloat(0L, ((Number)arg).floatValue());
/*  693 */           hr = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, (Pointer)mem);
/*  694 */           COMUtils.checkRC(hr);
/*      */           return;
/*      */         case 5:
/*  697 */           mem = new Memory(8L);
/*  698 */           mem.setDouble(0L, ((Number)arg).doubleValue());
/*  699 */           hr = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, (Pointer)mem);
/*  700 */           COMUtils.checkRC(hr);
/*      */           return;
/*      */         case 7:
/*  703 */           mem = new Memory(8L);
/*  704 */           mem.setDouble(0L, ((OaIdl.DATE)arg).date);
/*  705 */           hr = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, (Pointer)mem);
/*  706 */           COMUtils.checkRC(hr);
/*      */           return;
/*      */         case 8:
/*  709 */           if (arg instanceof String) {
/*  710 */             WTypes.BSTR bstr = OleAuto.INSTANCE.SysAllocString((String)arg);
/*  711 */             hr = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, bstr.getPointer());
/*  712 */             OleAuto.INSTANCE.SysFreeString(bstr);
/*  713 */             COMUtils.checkRC(hr);
/*      */           } else {
/*  715 */             hr = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, ((WTypes.BSTR)arg).getPointer());
/*  716 */             COMUtils.checkRC(hr);
/*      */           } 
/*      */           return;
/*      */         case 12:
/*  720 */           hr = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, ((Variant.VARIANT)arg).getPointer());
/*  721 */           COMUtils.checkRC(hr);
/*      */           return;
/*      */         case 13:
/*  724 */           hr = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, ((Unknown)arg).getPointer());
/*  725 */           COMUtils.checkRC(hr);
/*      */           return;
/*      */         case 9:
/*  728 */           hr = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, ((Dispatch)arg).getPointer());
/*  729 */           COMUtils.checkRC(hr);
/*      */           return;
/*      */         case 6:
/*  732 */           hr = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, ((OaIdl.CURRENCY)arg).getPointer());
/*  733 */           COMUtils.checkRC(hr);
/*      */           return;
/*      */         case 14:
/*  736 */           hr = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, ((OaIdl.DECIMAL)arg).getPointer());
/*  737 */           COMUtils.checkRC(hr);
/*      */           return;
/*      */       } 
/*      */       
/*  741 */       throw new IllegalStateException("Can't parse array content - type not supported: " + getVarType().intValue());
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Object getElement(int... indices) {
/*      */       Object result;
/*      */       WinNT.HRESULT hr;
/*      */       Memory mem;
/*      */       PointerByReference pbr;
/*      */       WTypes.BSTR bstr;
/*      */       Variant.VARIANT holder;
/*      */       OaIdl.CURRENCY currency;
/*      */       OaIdl.DECIMAL decimal;
/*  755 */       WinDef.LONG[] paramIndices = new WinDef.LONG[indices.length];
/*  756 */       for (int i = 0; i < indices.length; i++) {
/*  757 */         paramIndices[i] = new WinDef.LONG(indices[indices.length - i - 1]);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  764 */       switch (getVarType().intValue()) {
/*      */         case 11:
/*  766 */           mem = new Memory(2L);
/*  767 */           hr = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, (Pointer)mem);
/*  768 */           COMUtils.checkRC(hr);
/*  769 */           result = Boolean.valueOf((mem.getShort(0L) != 0));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  861 */           return result;case 16: case 17: mem = new Memory(1L); hr = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, (Pointer)mem); COMUtils.checkRC(hr); result = Byte.valueOf(mem.getByte(0L)); return result;case 2: case 18: mem = new Memory(2L); hr = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, (Pointer)mem); COMUtils.checkRC(hr); result = Short.valueOf(mem.getShort(0L)); return result;case 3: case 19: case 22: case 23: mem = new Memory(4L); hr = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, (Pointer)mem); COMUtils.checkRC(hr); result = Integer.valueOf(mem.getInt(0L)); return result;case 10: mem = new Memory(4L); hr = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, (Pointer)mem); COMUtils.checkRC(hr); result = new WinDef.SCODE(mem.getInt(0L)); return result;case 4: mem = new Memory(4L); hr = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, (Pointer)mem); COMUtils.checkRC(hr); result = Float.valueOf(mem.getFloat(0L)); return result;case 5: mem = new Memory(8L); hr = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, (Pointer)mem); COMUtils.checkRC(hr); result = Double.valueOf(mem.getDouble(0L)); return result;case 7: mem = new Memory(8L); hr = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, (Pointer)mem); COMUtils.checkRC(hr); result = new OaIdl.DATE(mem.getDouble(0L)); return result;case 8: pbr = new PointerByReference(); hr = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, pbr.getPointer()); COMUtils.checkRC(hr); bstr = new WTypes.BSTR(pbr.getValue()); result = bstr.getValue(); OleAuto.INSTANCE.SysFreeString(bstr); return result;case 12: holder = new Variant.VARIANT(); hr = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, holder.getPointer()); COMUtils.checkRC(hr); result = holder; return result;case 13: pbr = new PointerByReference(); hr = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, pbr.getPointer()); COMUtils.checkRC(hr); result = new Unknown(pbr.getValue()); return result;case 9: pbr = new PointerByReference(); hr = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, pbr.getPointer()); COMUtils.checkRC(hr); result = new Dispatch(pbr.getValue()); return result;case 6: currency = new OaIdl.CURRENCY(); hr = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, currency.getPointer()); COMUtils.checkRC(hr); result = currency; return result;case 14: decimal = new OaIdl.DECIMAL(); hr = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, decimal.getPointer()); COMUtils.checkRC(hr); result = decimal; return result;
/*      */       } 
/*      */       throw new IllegalStateException("Can't parse array content - type not supported: " + getVarType().intValue());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Pointer ptrOfIndex(int... indices) {
/*  877 */       WinDef.LONG[] paramIndices = new WinDef.LONG[indices.length];
/*  878 */       for (int i = 0; i < indices.length; i++) {
/*  879 */         paramIndices[i] = new WinDef.LONG(indices[indices.length - i - 1]);
/*      */       }
/*  881 */       PointerByReference pbr = new PointerByReference();
/*  882 */       WinNT.HRESULT hr = OleAuto.INSTANCE.SafeArrayPtrOfIndex(this, paramIndices, pbr);
/*  883 */       COMUtils.checkRC(hr);
/*  884 */       return pbr.getValue();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void destroy() {
/*  891 */       WinNT.HRESULT res = OleAuto.INSTANCE.SafeArrayDestroy(this);
/*  892 */       COMUtils.checkRC(res);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void close() {
/*  899 */       destroy();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getLBound(int dimension) {
/*  912 */       int targetDimension = getDimensionCount() - dimension;
/*  913 */       WinDef.LONGByReference bound = new WinDef.LONGByReference();
/*  914 */       WinNT.HRESULT res = OleAuto.INSTANCE.SafeArrayGetLBound(this, new WinDef.UINT(targetDimension), bound);
/*  915 */       COMUtils.checkRC(res);
/*  916 */       return bound.getValue().intValue();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getUBound(int dimension) {
/*  929 */       int targetDimension = getDimensionCount() - dimension;
/*  930 */       WinDef.LONGByReference bound = new WinDef.LONGByReference();
/*  931 */       WinNT.HRESULT res = OleAuto.INSTANCE.SafeArrayGetUBound(this, new WinDef.UINT(targetDimension), bound);
/*  932 */       COMUtils.checkRC(res);
/*  933 */       return bound.getValue().intValue();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getDimensionCount() {
/*  942 */       return OleAuto.INSTANCE.SafeArrayGetDim(this).intValue();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Pointer accessData() {
/*  951 */       PointerByReference pbr = new PointerByReference();
/*  952 */       WinNT.HRESULT hr = OleAuto.INSTANCE.SafeArrayAccessData(this, pbr);
/*  953 */       COMUtils.checkRC(hr);
/*  954 */       return pbr.getValue();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void unaccessData() {
/*  962 */       WinNT.HRESULT hr = OleAuto.INSTANCE.SafeArrayUnaccessData(this);
/*  963 */       COMUtils.checkRC(hr);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void lock() {
/*  971 */       WinNT.HRESULT res = OleAuto.INSTANCE.SafeArrayLock(this);
/*  972 */       COMUtils.checkRC(res);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void unlock() {
/*  979 */       WinNT.HRESULT res = OleAuto.INSTANCE.SafeArrayUnlock(this);
/*  980 */       COMUtils.checkRC(res);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void redim(int cElements, int lLbound) {
/*  991 */       WinNT.HRESULT res = OleAuto.INSTANCE.SafeArrayRedim(this, new OaIdl.SAFEARRAYBOUND(cElements, lLbound));
/*  992 */       COMUtils.checkRC(res);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WTypes.VARTYPE getVarType() {
/* 1001 */       WTypes.VARTYPEByReference resultHolder = new WTypes.VARTYPEByReference();
/* 1002 */       WinNT.HRESULT res = OleAuto.INSTANCE.SafeArrayGetVartype(this, resultHolder);
/* 1003 */       COMUtils.checkRC(res);
/* 1004 */       return resultHolder.getValue();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public long getElemsize() {
/* 1013 */       return OleAuto.INSTANCE.SafeArrayGetElemsize(this).longValue();
/*      */     }
/*      */   }
/*      */   
/*      */   @FieldOrder({"pSAFEARRAY"})
/*      */   public static class SAFEARRAYByReference extends Structure implements Structure.ByReference {
/*      */     public OaIdl.SAFEARRAY.ByReference pSAFEARRAY;
/*      */     
/*      */     public SAFEARRAYByReference() {}
/*      */     
/*      */     public SAFEARRAYByReference(Pointer p) {
/* 1024 */       super(p);
/* 1025 */       read();
/*      */     }
/*      */     
/*      */     public SAFEARRAYByReference(OaIdl.SAFEARRAY.ByReference safeArray) {
/* 1029 */       this.pSAFEARRAY = safeArray;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"cElements", "lLbound"})
/*      */   public static class SAFEARRAYBOUND
/*      */     extends Structure
/*      */   {
/*      */     public WinDef.ULONG cElements;
/*      */     
/*      */     public WinDef.LONG lLbound;
/*      */     
/*      */     public static class ByReference
/*      */       extends SAFEARRAYBOUND
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public SAFEARRAYBOUND() {}
/*      */     
/*      */     public SAFEARRAYBOUND(Pointer pointer) {
/* 1049 */       super(pointer);
/* 1050 */       read();
/*      */     }
/*      */     
/*      */     public SAFEARRAYBOUND(int cElements, int lLbound) {
/* 1054 */       this.cElements = new WinDef.ULONG(cElements);
/* 1055 */       this.lLbound = new WinDef.LONG(lLbound);
/* 1056 */       write();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public static class CURRENCY
/*      */     extends Union
/*      */   {
/*      */     public _CURRENCY currency;
/*      */     public WinDef.LONGLONG int64;
/*      */     
/*      */     public static class ByReference
/*      */       extends CURRENCY
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public CURRENCY() {}
/*      */     
/*      */     public CURRENCY(Pointer pointer) {
/* 1074 */       super(pointer);
/* 1075 */       read();
/*      */     }
/*      */     
/*      */     @FieldOrder({"Lo", "Hi"})
/*      */     public static class _CURRENCY
/*      */       extends Structure
/*      */     {
/*      */       public WinDef.ULONG Lo;
/*      */       public WinDef.LONG Hi;
/*      */       
/*      */       public _CURRENCY() {}
/*      */       
/*      */       public _CURRENCY(Pointer pointer) {
/* 1088 */         super(pointer);
/* 1089 */         read();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   @FieldOrder({"wReserved", "decimal1", "Hi32", "decimal2"})
/*      */   public static class DECIMAL extends Structure { public short wReserved;
/*      */     public _DECIMAL1 decimal1;
/*      */     public NativeLong Hi32;
/*      */     public _DECIMAL2 decimal2;
/*      */     
/*      */     public static class ByReference extends DECIMAL implements Structure.ByReference {}
/*      */     
/*      */     public static class _DECIMAL1 extends Union {
/*      */       public WinDef.USHORT signscale;
/*      */       public _DECIMAL1_DECIMAL decimal1_DECIMAL;
/*      */       
/*      */       public _DECIMAL1() {
/* 1107 */         setType("signscale");
/*      */       }
/*      */       
/*      */       public _DECIMAL1(Pointer pointer) {
/* 1111 */         super(pointer);
/* 1112 */         setType("signscale");
/* 1113 */         read();
/*      */       }
/*      */       
/*      */       @FieldOrder({"scale", "sign"})
/*      */       public static class _DECIMAL1_DECIMAL
/*      */         extends Structure
/*      */       {
/*      */         public WinDef.BYTE scale;
/*      */         public WinDef.BYTE sign;
/*      */         
/*      */         public _DECIMAL1_DECIMAL() {}
/*      */         
/*      */         public _DECIMAL1_DECIMAL(Pointer pointer) {
/* 1126 */           super(pointer);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     public static class _DECIMAL2 extends Union {
/*      */       public WinDef.ULONGLONG Lo64;
/*      */       public _DECIMAL2_DECIMAL decimal2_DECIMAL;
/*      */       
/*      */       public _DECIMAL2() {
/* 1136 */         setType("Lo64");
/*      */       }
/*      */       
/*      */       public _DECIMAL2(Pointer pointer) {
/* 1140 */         super(pointer);
/* 1141 */         setType("Lo64");
/* 1142 */         read();
/*      */       }
/*      */ 
/*      */       
/*      */       @FieldOrder({"Lo32", "Mid32"})
/*      */       public static class _DECIMAL2_DECIMAL
/*      */         extends Structure
/*      */       {
/*      */         public WinDef.BYTE Lo32;
/*      */         public WinDef.BYTE Mid32;
/*      */         
/*      */         public _DECIMAL2_DECIMAL() {}
/*      */         
/*      */         public _DECIMAL2_DECIMAL(Pointer pointer) {
/* 1156 */           super(pointer);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public DECIMAL() {}
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public DECIMAL(Pointer pointer) {
/* 1171 */       super(pointer);
/*      */     } }
/*      */   
/*      */   @FieldOrder({"value"})
/*      */   public static class SYSKIND extends Structure {
/*      */     public int value;
/*      */     public static final int SYS_WIN16 = 0;
/*      */     public static final int SYS_WIN32 = 1;
/*      */     public static final int SYS_MAC = 2;
/*      */     public static final int SYS_WIN64 = 3;
/*      */     
/*      */     public static class ByReference extends SYSKIND implements Structure.ByReference {}
/*      */     
/*      */     public SYSKIND() {}
/*      */     
/*      */     public SYSKIND(int value) {
/* 1187 */       this.value = value;
/*      */     }
/*      */     
/*      */     public SYSKIND(Pointer pointer) {
/* 1191 */       super(pointer);
/* 1192 */       read();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"value"})
/*      */   public static class LIBFLAGS
/*      */     extends Structure
/*      */   {
/*      */     public int value;
/*      */     public static final int LIBFLAG_FRESTRICTED = 1;
/*      */     public static final int LIBFLAG_FCONTROL = 2;
/*      */     public static final int LIBFLAG_FHIDDEN = 4;
/*      */     public static final int LIBFLAG_FHASDISKIMAGE = 8;
/*      */     
/*      */     public static class ByReference
/*      */       extends LIBFLAGS
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public LIBFLAGS() {}
/*      */     
/*      */     public LIBFLAGS(int value) {
/* 1214 */       this.value = value;
/*      */     }
/*      */     
/*      */     public LIBFLAGS(Pointer pointer) {
/* 1218 */       super(pointer);
/* 1219 */       read();
/*      */     }
/*      */   }
/*      */   
/*      */   @FieldOrder({"guid", "lcid", "syskind", "wMajorVerNum", "wMinorVerNum", "wLibFlags"})
/*      */   public static class TLIBATTR
/*      */     extends Structure {
/*      */     public Guid.GUID guid;
/*      */     public WinDef.LCID lcid;
/*      */     public OaIdl.SYSKIND syskind;
/*      */     public WinDef.WORD wMajorVerNum;
/*      */     public WinDef.WORD wMinorVerNum;
/*      */     public WinDef.WORD wLibFlags;
/*      */     
/*      */     public static class ByReference
/*      */       extends TLIBATTR implements Structure.ByReference {
/*      */       public ByReference() {}
/*      */       
/*      */       public ByReference(Pointer pointer) {
/* 1238 */         super(pointer);
/* 1239 */         read();
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public TLIBATTR() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public TLIBATTR(Pointer pointer) {
/* 1255 */       super(pointer);
/* 1256 */       read();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public static class BINDPTR
/*      */     extends Union
/*      */   {
/*      */     public OaIdl.FUNCDESC lpfuncdesc;
/*      */     
/*      */     public OaIdl.VARDESC lpvardesc;
/*      */     
/*      */     public TypeComp lptcomp;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends BINDPTR
/*      */       implements Structure.ByReference {}
/*      */ 
/*      */     
/*      */     public BINDPTR() {}
/*      */     
/*      */     public BINDPTR(OaIdl.VARDESC lpvardesc) {
/* 1279 */       this.lpvardesc = lpvardesc;
/* 1280 */       setType(OaIdl.VARDESC.class);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public BINDPTR(TypeComp lptcomp) {
/* 1286 */       this.lptcomp = lptcomp;
/* 1287 */       setType(TypeComp.class);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public BINDPTR(OaIdl.FUNCDESC lpfuncdesc) {
/* 1293 */       this.lpfuncdesc = lpfuncdesc;
/* 1294 */       setType(OaIdl.FUNCDESC.class);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"memid", "lprgscode", "lprgelemdescParam", "funckind", "invkind", "callconv", "cParams", "cParamsOpt", "oVft", "cScodes", "elemdescFunc", "wFuncFlags"})
/*      */   public static class FUNCDESC
/*      */     extends Structure
/*      */   {
/*      */     public OaIdl.MEMBERID memid;
/*      */     
/*      */     public OaIdl.ScodeArg.ByReference lprgscode;
/*      */     public OaIdl.ElemDescArg.ByReference lprgelemdescParam;
/*      */     public OaIdl.FUNCKIND funckind;
/*      */     public OaIdl.INVOKEKIND invkind;
/*      */     public OaIdl.CALLCONV callconv;
/*      */     public WinDef.SHORT cParams;
/*      */     public WinDef.SHORT cParamsOpt;
/*      */     public WinDef.SHORT oVft;
/*      */     public WinDef.SHORT cScodes;
/*      */     public OaIdl.ELEMDESC elemdescFunc;
/*      */     public WinDef.WORD wFuncFlags;
/*      */     
/*      */     public static class ByReference
/*      */       extends FUNCDESC
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public FUNCDESC() {}
/*      */     
/*      */     public FUNCDESC(Pointer pointer) {
/* 1324 */       super(pointer);
/* 1325 */       read();
/*      */       
/* 1327 */       if (this.cParams.shortValue() > 1) {
/* 1328 */         this.lprgelemdescParam
/* 1329 */           .elemDescArg = new OaIdl.ELEMDESC[this.cParams.shortValue()];
/* 1330 */         this.lprgelemdescParam.read();
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   @FieldOrder({"elemDescArg"})
/*      */   public static class ElemDescArg
/*      */     extends Structure {
/*      */     public static class ByReference
/*      */       extends ElemDescArg implements Structure.ByReference {}
/*      */     
/* 1341 */     public OaIdl.ELEMDESC[] elemDescArg = new OaIdl.ELEMDESC[] { new OaIdl.ELEMDESC() };
/*      */ 
/*      */     
/*      */     public ElemDescArg() {}
/*      */ 
/*      */     
/*      */     public ElemDescArg(Pointer pointer) {
/* 1348 */       super(pointer);
/* 1349 */       read();
/*      */     }
/*      */   }
/*      */   
/*      */   @FieldOrder({"scodeArg"})
/*      */   public static class ScodeArg
/*      */     extends Structure {
/*      */     public static class ByReference
/*      */       extends ScodeArg implements Structure.ByReference {}
/*      */     
/* 1359 */     public WinDef.SCODE[] scodeArg = new WinDef.SCODE[] { new WinDef.SCODE() };
/*      */ 
/*      */     
/*      */     public ScodeArg() {}
/*      */ 
/*      */     
/*      */     public ScodeArg(Pointer pointer) {
/* 1366 */       super(pointer);
/* 1367 */       read();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"memid", "lpstrSchema", "_vardesc", "elemdescVar", "wVarFlags", "varkind"})
/*      */   public static class VARDESC
/*      */     extends Structure
/*      */   {
/*      */     public OaIdl.MEMBERID memid;
/*      */     
/*      */     public WTypes.LPOLESTR lpstrSchema;
/*      */     
/*      */     public _VARDESC _vardesc;
/*      */     
/*      */     public OaIdl.ELEMDESC elemdescVar;
/*      */     
/*      */     public WinDef.WORD wVarFlags;
/*      */     
/*      */     public OaIdl.VARKIND varkind;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends VARDESC
/*      */       implements Structure.ByReference {}
/*      */ 
/*      */     
/*      */     public static class _VARDESC
/*      */       extends Union
/*      */     {
/*      */       public NativeLong oInst;
/*      */       
/*      */       public Variant.VARIANT.ByReference lpvarValue;
/*      */ 
/*      */       
/*      */       public static class ByReference
/*      */         extends _VARDESC
/*      */         implements Structure.ByReference {}
/*      */ 
/*      */       
/*      */       public _VARDESC() {
/* 1408 */         setType("lpvarValue");
/* 1409 */         read();
/*      */       }
/*      */       
/*      */       public _VARDESC(Pointer pointer) {
/* 1413 */         super(pointer);
/* 1414 */         setType("lpvarValue");
/* 1415 */         read();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public _VARDESC(Variant.VARIANT.ByReference lpvarValue) {
/* 1424 */         this.lpvarValue = lpvarValue;
/* 1425 */         setType("lpvarValue");
/*      */       }
/*      */ 
/*      */       
/*      */       public _VARDESC(NativeLong oInst) {
/* 1430 */         this.oInst = oInst;
/* 1431 */         setType("oInst");
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public VARDESC() {}
/*      */ 
/*      */     
/*      */     public VARDESC(Pointer pointer) {
/* 1440 */       super(pointer);
/* 1441 */       this._vardesc.setType("lpvarValue");
/* 1442 */       read();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"tdesc", "_elemdesc"})
/*      */   public static class ELEMDESC
/*      */     extends Structure
/*      */   {
/*      */     public OaIdl.TYPEDESC tdesc;
/*      */ 
/*      */     
/*      */     public _ELEMDESC _elemdesc;
/*      */ 
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends ELEMDESC
/*      */       implements Structure.ByReference {}
/*      */ 
/*      */ 
/*      */     
/*      */     public static class _ELEMDESC
/*      */       extends Union
/*      */     {
/*      */       public OaIdl.IDLDESC idldesc;
/*      */       
/*      */       public OaIdl.PARAMDESC paramdesc;
/*      */ 
/*      */       
/*      */       public static class ByReference
/*      */         extends _ELEMDESC
/*      */         implements Structure.ByReference {}
/*      */ 
/*      */       
/*      */       public _ELEMDESC() {}
/*      */ 
/*      */       
/*      */       public _ELEMDESC(Pointer pointer) {
/* 1482 */         super(pointer);
/* 1483 */         setType("paramdesc");
/* 1484 */         read();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public _ELEMDESC(OaIdl.PARAMDESC paramdesc) {
/* 1493 */         this.paramdesc = paramdesc;
/* 1494 */         setType("paramdesc");
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public _ELEMDESC(OaIdl.IDLDESC idldesc) {
/* 1503 */         this.idldesc = idldesc;
/* 1504 */         setType("idldesc");
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public ELEMDESC() {}
/*      */ 
/*      */     
/*      */     public ELEMDESC(Pointer pointer) {
/* 1513 */       super(pointer);
/* 1514 */       read();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"value"})
/*      */   public static class FUNCKIND
/*      */     extends Structure
/*      */   {
/*      */     public static final int FUNC_VIRTUAL = 0;
/*      */     
/*      */     public static final int FUNC_PUREVIRTUAL = 1;
/*      */     
/*      */     public static final int FUNC_NONVIRTUAL = 2;
/*      */     
/*      */     public static final int FUNC_STATIC = 3;
/*      */     
/*      */     public static final int FUNC_DISPATCH = 4;
/*      */     
/*      */     public int value;
/*      */     
/*      */     public static class ByReference
/*      */       extends FUNCKIND
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public FUNCKIND() {}
/*      */     
/*      */     public FUNCKIND(int value) {
/* 1542 */       this.value = value;
/*      */     }
/*      */   }
/*      */   
/*      */   @FieldOrder({"value"})
/*      */   public static class INVOKEKIND
/*      */     extends Structure
/*      */   {
/*      */     public static class ByReference
/*      */       extends INVOKEKIND
/*      */       implements Structure.ByReference {}
/*      */     
/* 1554 */     public static final INVOKEKIND INVOKE_FUNC = new INVOKEKIND(1);
/*      */     
/* 1556 */     public static final INVOKEKIND INVOKE_PROPERTYGET = new INVOKEKIND(2);
/*      */     
/* 1558 */     public static final INVOKEKIND INVOKE_PROPERTYPUT = new INVOKEKIND(4);
/*      */     
/* 1560 */     public static final INVOKEKIND INVOKE_PROPERTYPUTREF = new INVOKEKIND(8);
/*      */     
/*      */     public int value;
/*      */ 
/*      */     
/*      */     public INVOKEKIND() {}
/*      */ 
/*      */     
/*      */     public INVOKEKIND(int value) {
/* 1569 */       this.value = value;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"value"})
/*      */   public static class CALLCONV
/*      */     extends Structure
/*      */   {
/*      */     public static final int CC_FASTCALL = 0;
/*      */     
/*      */     public static final int CC_CDECL = 1;
/*      */     
/*      */     public static final int CC_MSCPASCAL = 2;
/*      */     
/*      */     public static final int CC_PASCAL = 2;
/*      */     
/*      */     public static final int CC_MACPASCAL = 3;
/*      */     
/*      */     public static final int CC_STDCALL = 4;
/*      */     
/*      */     public static final int CC_FPFASTCALL = 5;
/*      */     
/*      */     public static final int CC_SYSCALL = 6;
/*      */     
/*      */     public static final int CC_MPWCDECL = 7;
/*      */     
/*      */     public static final int CC_MPWPASCAL = 8;
/*      */     
/*      */     public static final int CC_MAX = 9;
/*      */     
/*      */     public int value;
/*      */     
/*      */     public static class ByReference
/*      */       extends CALLCONV
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public CALLCONV() {}
/*      */     
/*      */     public CALLCONV(int value) {
/* 1609 */       this.value = value;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"value"})
/*      */   public static class VARKIND
/*      */     extends Structure
/*      */   {
/*      */     public static final int VAR_PERINSTANCE = 0;
/*      */     
/*      */     public static final int VAR_STATIC = 1;
/*      */     
/*      */     public static final int VAR_CONST = 2;
/*      */     
/*      */     public static final int VAR_DISPATCH = 3;
/*      */     
/*      */     public int value;
/*      */     
/*      */     public static class ByReference
/*      */       extends VARKIND
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public VARKIND() {}
/*      */     
/*      */     public VARKIND(int value) {
/* 1635 */       this.value = value;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"_typedesc", "vt"})
/*      */   public static class TYPEDESC
/*      */     extends Structure
/*      */   {
/*      */     public _TYPEDESC _typedesc;
/*      */     
/*      */     public WTypes.VARTYPE vt;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends TYPEDESC
/*      */       implements Structure.ByReference {}
/*      */ 
/*      */     
/*      */     public static class _TYPEDESC
/*      */       extends Union
/*      */     {
/*      */       public OaIdl.TYPEDESC.ByReference lptdesc;
/*      */       
/*      */       public OaIdl.ARRAYDESC.ByReference lpadesc;
/*      */       
/*      */       public OaIdl.HREFTYPE hreftype;
/*      */       
/*      */       public _TYPEDESC() {
/* 1664 */         setType("hreftype");
/* 1665 */         read();
/*      */       }
/*      */       
/*      */       public _TYPEDESC(Pointer pointer) {
/* 1669 */         super(pointer);
/* 1670 */         setType("hreftype");
/* 1671 */         read();
/*      */       }
/*      */       
/*      */       public OaIdl.TYPEDESC.ByReference getLptdesc() {
/* 1675 */         setType("lptdesc");
/* 1676 */         read();
/* 1677 */         return this.lptdesc;
/*      */       }
/*      */       
/*      */       public OaIdl.ARRAYDESC.ByReference getLpadesc() {
/* 1681 */         setType("lpadesc");
/* 1682 */         read();
/* 1683 */         return this.lpadesc;
/*      */       }
/*      */       
/*      */       public OaIdl.HREFTYPE getHreftype() {
/* 1687 */         setType("hreftype");
/* 1688 */         read();
/* 1689 */         return this.hreftype;
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public TYPEDESC() {
/* 1697 */       read();
/*      */     }
/*      */     
/*      */     public TYPEDESC(Pointer pointer) {
/* 1701 */       super(pointer);
/* 1702 */       read();
/*      */     }
/*      */     
/*      */     public TYPEDESC(_TYPEDESC _typedesc, WTypes.VARTYPE vt) {
/* 1706 */       this._typedesc = _typedesc;
/* 1707 */       this.vt = vt;
/*      */     }
/*      */   }
/*      */   
/*      */   @FieldOrder({"dwReserved", "wIDLFlags"})
/*      */   public static class IDLDESC
/*      */     extends Structure {
/*      */     public BaseTSD.ULONG_PTR dwReserved;
/*      */     public WinDef.USHORT wIDLFlags;
/*      */     
/*      */     public static class ByReference extends IDLDESC implements Structure.ByReference {
/*      */       public ByReference() {}
/*      */       
/*      */       public ByReference(OaIdl.IDLDESC idldesc) {
/* 1721 */         super(idldesc.dwReserved, idldesc.wIDLFlags);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public IDLDESC() {}
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public IDLDESC(Pointer pointer) {
/* 1734 */       super(pointer);
/* 1735 */       read();
/*      */     }
/*      */ 
/*      */     
/*      */     public IDLDESC(BaseTSD.ULONG_PTR dwReserved, WinDef.USHORT wIDLFlags) {
/* 1740 */       this.dwReserved = dwReserved;
/* 1741 */       this.wIDLFlags = wIDLFlags;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"tdescElem", "cDims", "rgbounds"})
/*      */   public static class ARRAYDESC
/*      */     extends Structure
/*      */   {
/*      */     public OaIdl.TYPEDESC tdescElem;
/*      */     
/*      */     public short cDims;
/*      */     
/* 1754 */     public OaIdl.SAFEARRAYBOUND[] rgbounds = new OaIdl.SAFEARRAYBOUND[] { new OaIdl.SAFEARRAYBOUND() };
/*      */ 
/*      */     
/*      */     public ARRAYDESC() {}
/*      */ 
/*      */     
/*      */     public ARRAYDESC(Pointer pointer) {
/* 1761 */       super(pointer);
/* 1762 */       read();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ARRAYDESC(OaIdl.TYPEDESC tdescElem, short cDims, OaIdl.SAFEARRAYBOUND[] rgbounds) {
/* 1774 */       this.tdescElem = tdescElem;
/* 1775 */       this.cDims = cDims;
/* 1776 */       if (rgbounds.length != this.rgbounds.length)
/* 1777 */         throw new IllegalArgumentException("Wrong array size !"); 
/* 1778 */       this.rgbounds = rgbounds;
/*      */     }
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends ARRAYDESC
/*      */       implements Structure.ByReference {}
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"pparamdescex", "wParamFlags"})
/*      */   public static class PARAMDESC
/*      */     extends Structure
/*      */   {
/*      */     public Pointer pparamdescex;
/*      */     
/*      */     public WinDef.USHORT wParamFlags;
/*      */     
/*      */     public static class ByReference
/*      */       extends PARAMDESC
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public PARAMDESC() {}
/*      */     
/*      */     public PARAMDESC(Pointer pointer) {
/* 1803 */       super(pointer);
/* 1804 */       read();
/*      */     }
/*      */   }
/*      */   
/*      */   @FieldOrder({"cBytes", "varDefaultValue"})
/*      */   public static class PARAMDESCEX
/*      */     extends Structure
/*      */   {
/*      */     public WinDef.ULONG cBytes;
/*      */     public Variant.VariantArg varDefaultValue;
/*      */     
/*      */     public static class ByReference
/*      */       extends PARAMDESCEX
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public PARAMDESCEX() {}
/*      */     
/*      */     public PARAMDESCEX(Pointer pointer) {
/* 1822 */       super(pointer);
/* 1823 */       read();
/*      */     }
/*      */   }
/*      */   
/*      */   public static class HREFTYPE
/*      */     extends WinDef.DWORD
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */     
/*      */     public HREFTYPE() {}
/*      */     
/*      */     public HREFTYPE(long value) {
/* 1835 */       super(value);
/*      */     }
/*      */   }
/*      */   
/*      */   public static class HREFTYPEByReference extends WinDef.DWORDByReference {
/*      */     public HREFTYPEByReference() {
/* 1841 */       this(new OaIdl.HREFTYPE(0L));
/*      */     }
/*      */     
/*      */     public HREFTYPEByReference(WinDef.DWORD value) {
/* 1845 */       super(value);
/*      */     }
/*      */     
/*      */     public void setValue(OaIdl.HREFTYPE value) {
/* 1849 */       getPointer().setInt(0L, value.intValue());
/*      */     }
/*      */ 
/*      */     
/*      */     public OaIdl.HREFTYPE getValue() {
/* 1854 */       return new OaIdl.HREFTYPE(getPointer().getInt(0L));
/*      */     }
/*      */   }
/*      */   
/*      */   @FieldOrder({"guid", "lcid", "dwReserved", "memidConstructor", "memidDestructor", "lpstrSchema", "cbSizeInstance", "typekind", "cFuncs", "cVars", "cImplTypes", "cbSizeVft", "cbAlignment", "wTypeFlags", "wMajorVerNum", "wMinorVerNum", "tdescAlias", "idldescType"})
/*      */   public static class TYPEATTR
/*      */     extends Structure {
/*      */     public Guid.GUID guid;
/*      */     public WinDef.LCID lcid;
/*      */     public WinDef.DWORD dwReserved;
/*      */     public OaIdl.MEMBERID memidConstructor;
/*      */     public OaIdl.MEMBERID memidDestructor;
/*      */     public WTypes.LPOLESTR lpstrSchema;
/*      */     public WinDef.ULONG cbSizeInstance;
/*      */     public OaIdl.TYPEKIND typekind;
/*      */     public WinDef.WORD cFuncs;
/*      */     public WinDef.WORD cVars;
/*      */     public WinDef.WORD cImplTypes;
/*      */     public WinDef.WORD cbSizeVft;
/*      */     public WinDef.WORD cbAlignment;
/*      */     public WinDef.WORD wTypeFlags;
/*      */     public WinDef.WORD wMajorVerNum;
/*      */     public WinDef.WORD wMinorVerNum;
/*      */     public OaIdl.TYPEDESC tdescAlias;
/*      */     public OaIdl.IDLDESC idldescType;
/*      */     public static final int TYPEFLAGS_FAPPOBJECT = 1;
/*      */     public static final int TYPEFLAGS_FCANCREATE = 2;
/*      */     public static final int TYPEFLAGS_FLICENSED = 4;
/*      */     public static final int TYPEFLAGS_FPREDECLID = 8;
/*      */     public static final int TYPEFLAGS_FHIDDEN = 16;
/*      */     public static final int TYPEFLAGS_FCONTROL = 32;
/*      */     public static final int TYPEFLAGS_FDUAL = 64;
/*      */     public static final int TYPEFLAGS_FNONEXTENSIBLE = 128;
/*      */     public static final int TYPEFLAGS_FOLEAUTOMATION = 256;
/*      */     public static final int TYPEFLAGS_FRESTRICTED = 512;
/*      */     public static final int TYPEFLAGS_FAGGREGATABLE = 1024;
/*      */     public static final int TYPEFLAGS_FREPLACEABLE = 2048;
/*      */     public static final int TYPEFLAGS_FDISPATCHABLE = 4096;
/*      */     public static final int TYPEFLAGS_FREVERSEBIND = 8192;
/*      */     public static final int TYPEFLAGS_FPROXY = 16384;
/*      */     
/*      */     public static class ByReference
/*      */       extends TYPEATTR implements Structure.ByReference {}
/*      */     
/*      */     public TYPEATTR() {}
/*      */     
/*      */     public TYPEATTR(Pointer pointer) {
/* 1901 */       super(pointer);
/* 1902 */       read();
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\OaIdl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */