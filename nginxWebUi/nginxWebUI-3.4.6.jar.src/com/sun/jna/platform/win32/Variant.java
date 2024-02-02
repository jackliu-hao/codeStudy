/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.Structure.FieldOrder;
/*     */ import com.sun.jna.Union;
/*     */ import com.sun.jna.platform.win32.COM.Dispatch;
/*     */ import com.sun.jna.platform.win32.COM.IDispatch;
/*     */ import com.sun.jna.platform.win32.COM.Unknown;
/*     */ import com.sun.jna.ptr.ByteByReference;
/*     */ import com.sun.jna.ptr.DoubleByReference;
/*     */ import com.sun.jna.ptr.FloatByReference;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import com.sun.jna.ptr.ShortByReference;
/*     */ import java.util.Date;
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
/*     */ public interface Variant
/*     */ {
/*     */   public static final int VT_EMPTY = 0;
/*     */   public static final int VT_NULL = 1;
/*     */   public static final int VT_I2 = 2;
/*     */   public static final int VT_I4 = 3;
/*     */   public static final int VT_R4 = 4;
/*     */   public static final int VT_R8 = 5;
/*     */   public static final int VT_CY = 6;
/*     */   public static final int VT_DATE = 7;
/*     */   public static final int VT_BSTR = 8;
/*     */   public static final int VT_DISPATCH = 9;
/*     */   public static final int VT_ERROR = 10;
/*     */   public static final int VT_BOOL = 11;
/*     */   public static final int VT_VARIANT = 12;
/*     */   public static final int VT_UNKNOWN = 13;
/*     */   public static final int VT_DECIMAL = 14;
/*     */   public static final int VT_I1 = 16;
/*     */   public static final int VT_UI1 = 17;
/*     */   public static final int VT_UI2 = 18;
/*     */   public static final int VT_UI4 = 19;
/*     */   public static final int VT_I8 = 20;
/*     */   public static final int VT_UI8 = 21;
/*     */   public static final int VT_INT = 22;
/*     */   public static final int VT_UINT = 23;
/*     */   public static final int VT_VOID = 24;
/*     */   public static final int VT_HRESULT = 25;
/*     */   public static final int VT_PTR = 26;
/*     */   public static final int VT_SAFEARRAY = 27;
/*     */   public static final int VT_CARRAY = 28;
/*     */   public static final int VT_USERDEFINED = 29;
/*     */   public static final int VT_LPSTR = 30;
/*     */   public static final int VT_LPWSTR = 31;
/*     */   public static final int VT_RECORD = 36;
/*     */   public static final int VT_INT_PTR = 37;
/*     */   public static final int VT_UINT_PTR = 38;
/*     */   public static final int VT_FILETIME = 64;
/*     */   public static final int VT_BLOB = 65;
/*     */   public static final int VT_STREAM = 66;
/*     */   public static final int VT_STORAGE = 67;
/*     */   public static final int VT_STREAMED_OBJECT = 68;
/*     */   public static final int VT_STORED_OBJECT = 69;
/*     */   public static final int VT_BLOB_OBJECT = 70;
/*     */   public static final int VT_CF = 71;
/*     */   public static final int VT_CLSID = 72;
/*     */   public static final int VT_VERSIONED_STREAM = 73;
/*     */   public static final int VT_BSTR_BLOB = 4095;
/*     */   public static final int VT_VECTOR = 4096;
/*     */   public static final int VT_ARRAY = 8192;
/*     */   public static final int VT_BYREF = 16384;
/*     */   public static final int VT_RESERVED = 32768;
/*     */   public static final int VT_ILLEGAL = 65535;
/*     */   public static final int VT_ILLEGALMASKED = 4095;
/*     */   public static final int VT_TYPEMASK = 4095;
/* 126 */   public static final OaIdl.VARIANT_BOOL VARIANT_TRUE = new OaIdl.VARIANT_BOOL(65535L);
/* 127 */   public static final OaIdl.VARIANT_BOOL VARIANT_FALSE = new OaIdl.VARIANT_BOOL(0L);
/*     */   
/*     */   public static class VARIANT
/*     */     extends Union {
/*     */     public static class ByReference
/*     */       extends VARIANT implements Structure.ByReference {
/*     */       public ByReference(Variant.VARIANT variant) {
/* 134 */         setValue(variant.getVarType(), variant.getValue());
/*     */       }
/*     */       
/*     */       public ByReference(Pointer variant) {
/* 138 */         super(variant);
/*     */       }
/*     */       
/*     */       public ByReference() {}
/*     */     }
/*     */     
/*     */     public static class ByValue
/*     */       extends VARIANT
/*     */       implements Structure.ByValue
/*     */     {
/*     */       public ByValue(Variant.VARIANT variant) {
/* 149 */         setValue(variant.getVarType(), variant.getValue());
/*     */       }
/*     */       
/*     */       public ByValue(Pointer variant) {
/* 153 */         super(variant);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       public ByValue() {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 164 */     public static final VARIANT VARIANT_MISSING = new VARIANT(); public _VARIANT _variant; public OaIdl.DECIMAL decVal; static {
/* 165 */       VARIANT_MISSING.setValue(10, new WinDef.SCODE(-2147352572L));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public VARIANT() {
/* 173 */       setType("_variant");
/* 174 */       read();
/*     */     }
/*     */     
/*     */     public VARIANT(Pointer pointer) {
/* 178 */       super(pointer);
/* 179 */       setType("_variant");
/* 180 */       read();
/*     */     }
/*     */     
/*     */     public VARIANT(WTypes.BSTR value) {
/* 184 */       this();
/* 185 */       setValue(8, value);
/*     */     }
/*     */     
/*     */     public VARIANT(WTypes.BSTRByReference value) {
/* 189 */       this();
/* 190 */       setValue(16392, value);
/*     */     }
/*     */     
/*     */     public VARIANT(OaIdl.VARIANT_BOOL value) {
/* 194 */       this();
/* 195 */       setValue(11, value);
/*     */     }
/*     */     
/*     */     public VARIANT(WinDef.BOOL value) {
/* 199 */       this(value.booleanValue());
/*     */     }
/*     */     
/*     */     public VARIANT(WinDef.LONG value) {
/* 203 */       this();
/* 204 */       setValue(3, value);
/*     */     }
/*     */     
/*     */     public VARIANT(WinDef.SHORT value) {
/* 208 */       this();
/* 209 */       setValue(2, value);
/*     */     }
/*     */     
/*     */     public VARIANT(OaIdl.DATE value) {
/* 213 */       this();
/* 214 */       setValue(7, value);
/*     */     }
/*     */     
/*     */     public VARIANT(byte value) {
/* 218 */       this(new WinDef.BYTE(value));
/*     */     }
/*     */     
/*     */     public VARIANT(WinDef.BYTE value) {
/* 222 */       this();
/* 223 */       setValue(17, value);
/*     */     }
/*     */     
/*     */     public VARIANT(char value) {
/* 227 */       this();
/* 228 */       setValue(18, new WinDef.USHORT(value));
/*     */     }
/*     */     
/*     */     public VARIANT(WinDef.CHAR value) {
/* 232 */       this();
/* 233 */       setValue(16, value);
/*     */     }
/*     */     
/*     */     public VARIANT(short value) {
/* 237 */       this();
/* 238 */       setValue(2, new WinDef.SHORT(value));
/*     */     }
/*     */     
/*     */     public VARIANT(int value) {
/* 242 */       this();
/* 243 */       setValue(3, new WinDef.LONG(value));
/*     */     }
/*     */     
/*     */     public VARIANT(IntByReference value) {
/* 247 */       this();
/* 248 */       setValue(16406, value);
/*     */     }
/*     */     
/*     */     public VARIANT(long value) {
/* 252 */       this();
/* 253 */       setValue(20, new WinDef.LONGLONG(value));
/*     */     }
/*     */     
/*     */     public VARIANT(float value) {
/* 257 */       this();
/* 258 */       setValue(4, Float.valueOf(value));
/*     */     }
/*     */     
/*     */     public VARIANT(double value) {
/* 262 */       this();
/* 263 */       setValue(5, Double.valueOf(value));
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
/*     */     public VARIANT(String value) {
/* 277 */       this();
/* 278 */       WTypes.BSTR bstrValue = OleAuto.INSTANCE.SysAllocString(value);
/* 279 */       setValue(8, bstrValue);
/*     */     }
/*     */     
/*     */     public VARIANT(boolean value) {
/* 283 */       this();
/* 284 */       setValue(11, new OaIdl.VARIANT_BOOL(value));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public VARIANT(IDispatch value) {
/* 292 */       this();
/* 293 */       setValue(9, value);
/*     */     }
/*     */     
/*     */     public VARIANT(Dispatch value) {
/* 297 */       this();
/* 298 */       setValue(9, value);
/*     */     }
/*     */     
/*     */     public VARIANT(Date value) {
/* 302 */       this();
/* 303 */       setValue(7, new OaIdl.DATE(value));
/*     */     }
/*     */     
/*     */     public VARIANT(OaIdl.SAFEARRAY array) {
/* 307 */       this();
/* 308 */       setValue(array);
/*     */     }
/*     */     
/*     */     public VARIANT(OaIdl.SAFEARRAYByReference array) {
/* 312 */       this();
/* 313 */       setValue(array);
/*     */     }
/*     */     
/*     */     public WTypes.VARTYPE getVarType() {
/* 317 */       read();
/* 318 */       return this._variant.vt;
/*     */     }
/*     */     
/*     */     public void setVarType(short vt) {
/* 322 */       this._variant.vt = new WTypes.VARTYPE(vt);
/*     */     }
/*     */     
/*     */     public void setValue(int vt, Object value) {
/* 326 */       setValue(new WTypes.VARTYPE(vt), value);
/*     */     }
/*     */     
/*     */     public void setValue(OaIdl.SAFEARRAY array) {
/* 330 */       setValue(array.getVarType().intValue() | 0x2000, array);
/*     */     }
/*     */     
/*     */     public void setValue(OaIdl.SAFEARRAYByReference array) {
/* 334 */       setValue(array.pSAFEARRAY.getVarType().intValue() | 0x2000 | 0x4000, array);
/*     */     }
/*     */     
/*     */     public void setValue(WTypes.VARTYPE vt, Object value) {
/* 338 */       int varType = vt.intValue();
/* 339 */       switch (varType) {
/*     */         case 17:
/* 341 */           this._variant.__variant.writeField("bVal", value);
/*     */           break;
/*     */         case 2:
/* 344 */           this._variant.__variant.writeField("iVal", value);
/*     */           break;
/*     */         case 3:
/* 347 */           this._variant.__variant.writeField("lVal", value);
/*     */           break;
/*     */         case 20:
/* 350 */           this._variant.__variant.writeField("llVal", value);
/*     */           break;
/*     */         case 4:
/* 353 */           this._variant.__variant.writeField("fltVal", value);
/*     */           break;
/*     */         case 5:
/* 356 */           this._variant.__variant.writeField("dblVal", value);
/*     */           break;
/*     */         case 11:
/* 359 */           this._variant.__variant.writeField("boolVal", value);
/*     */           break;
/*     */         case 10:
/* 362 */           this._variant.__variant.writeField("scode", value);
/*     */           break;
/*     */         case 6:
/* 365 */           this._variant.__variant.writeField("cyVal", value);
/*     */           break;
/*     */         case 7:
/* 368 */           this._variant.__variant.writeField("date", value);
/*     */           break;
/*     */         case 8:
/* 371 */           this._variant.__variant.writeField("bstrVal", value);
/*     */           break;
/*     */         case 13:
/* 374 */           this._variant.__variant.writeField("punkVal", value);
/*     */           break;
/*     */         case 9:
/* 377 */           this._variant.__variant.writeField("pdispVal", value);
/*     */           break;
/*     */         case 16401:
/* 380 */           this._variant.__variant.writeField("pbVal", value);
/*     */           break;
/*     */         case 16386:
/* 383 */           this._variant.__variant.writeField("piVal", value);
/*     */           break;
/*     */         case 16387:
/* 386 */           this._variant.__variant.writeField("plVal", value);
/*     */           break;
/*     */         case 16404:
/* 389 */           this._variant.__variant.writeField("pllVal", value);
/*     */           break;
/*     */         case 16388:
/* 392 */           this._variant.__variant.writeField("pfltVal", value);
/*     */           break;
/*     */         case 16389:
/* 395 */           this._variant.__variant.writeField("pdblVal", value);
/*     */           break;
/*     */         case 16395:
/* 398 */           this._variant.__variant.writeField("pboolVal", value);
/*     */           break;
/*     */         case 16394:
/* 401 */           this._variant.__variant.writeField("pscode", value);
/*     */           break;
/*     */         case 16390:
/* 404 */           this._variant.__variant.writeField("pcyVal", value);
/*     */           break;
/*     */         case 16391:
/* 407 */           this._variant.__variant.writeField("pdate", value);
/*     */           break;
/*     */         case 16392:
/* 410 */           this._variant.__variant.writeField("pbstrVal", value);
/*     */           break;
/*     */         case 16397:
/* 413 */           this._variant.__variant.writeField("ppunkVal", value);
/*     */           break;
/*     */         case 16393:
/* 416 */           this._variant.__variant.writeField("ppdispVal", value);
/*     */           break;
/*     */         case 16396:
/* 419 */           this._variant.__variant.writeField("pvarVal", value);
/*     */           break;
/*     */         case 16384:
/* 422 */           this._variant.__variant.writeField("byref", value);
/*     */           break;
/*     */         case 16:
/* 425 */           this._variant.__variant.writeField("cVal", value);
/*     */           break;
/*     */         case 18:
/* 428 */           this._variant.__variant.writeField("uiVal", value);
/*     */           break;
/*     */         case 19:
/* 431 */           this._variant.__variant.writeField("ulVal", value);
/*     */           break;
/*     */         case 21:
/* 434 */           this._variant.__variant.writeField("ullVal", value);
/*     */           break;
/*     */         case 22:
/* 437 */           this._variant.__variant.writeField("intVal", value);
/*     */           break;
/*     */         case 23:
/* 440 */           this._variant.__variant.writeField("uintVal", value);
/*     */           break;
/*     */         case 16398:
/* 443 */           this._variant.__variant.writeField("pdecVal", value);
/*     */           break;
/*     */         case 16400:
/* 446 */           this._variant.__variant.writeField("pcVal", value);
/*     */           break;
/*     */         case 16402:
/* 449 */           this._variant.__variant.writeField("puiVal", value);
/*     */           break;
/*     */         case 16403:
/* 452 */           this._variant.__variant.writeField("pulVal", value);
/*     */           break;
/*     */         case 16405:
/* 455 */           this._variant.__variant.writeField("pullVal", value);
/*     */           break;
/*     */         case 16406:
/* 458 */           this._variant.__variant.writeField("pintVal", value);
/*     */           break;
/*     */         case 16407:
/* 461 */           this._variant.__variant.writeField("puintVal", value);
/*     */           break;
/*     */         case 36:
/* 464 */           this._variant.__variant.writeField("pvRecord", value);
/*     */           break;
/*     */         default:
/* 467 */           if ((varType & 0x2000) > 0) {
/* 468 */             if ((varType & 0x4000) > 0) {
/* 469 */               this._variant.__variant.writeField("pparray", value); break;
/*     */             } 
/* 471 */             this._variant.__variant.writeField("parray", value);
/*     */           } 
/*     */           break;
/*     */       } 
/*     */       
/* 476 */       this._variant.writeField("vt", vt);
/* 477 */       write();
/*     */     }
/*     */     
/*     */     public Object getValue() {
/* 481 */       read();
/* 482 */       int varType = getVarType().intValue();
/* 483 */       switch (getVarType().intValue()) {
/*     */         case 17:
/* 485 */           return this._variant.__variant.readField("bVal");
/*     */         case 2:
/* 487 */           return this._variant.__variant.readField("iVal");
/*     */         case 3:
/* 489 */           return this._variant.__variant.readField("lVal");
/*     */         case 20:
/* 491 */           return this._variant.__variant.readField("llVal");
/*     */         case 4:
/* 493 */           return this._variant.__variant.readField("fltVal");
/*     */         case 5:
/* 495 */           return this._variant.__variant.readField("dblVal");
/*     */         case 11:
/* 497 */           return this._variant.__variant.readField("boolVal");
/*     */         case 10:
/* 499 */           return this._variant.__variant.readField("scode");
/*     */         case 6:
/* 501 */           return this._variant.__variant.readField("cyVal");
/*     */         case 7:
/* 503 */           return this._variant.__variant.readField("date");
/*     */         case 8:
/* 505 */           return this._variant.__variant.readField("bstrVal");
/*     */         case 13:
/* 507 */           return this._variant.__variant.readField("punkVal");
/*     */         case 9:
/* 509 */           return this._variant.__variant.readField("pdispVal");
/*     */         case 16401:
/* 511 */           return this._variant.__variant.readField("pbVal");
/*     */         case 16386:
/* 513 */           return this._variant.__variant.readField("piVal");
/*     */         case 16387:
/* 515 */           return this._variant.__variant.readField("plVal");
/*     */         case 16404:
/* 517 */           return this._variant.__variant.readField("pllVal");
/*     */         case 16388:
/* 519 */           return this._variant.__variant.readField("pfltVal");
/*     */         case 16389:
/* 521 */           return this._variant.__variant.readField("pdblVal");
/*     */         case 16395:
/* 523 */           return this._variant.__variant.readField("pboolVal");
/*     */         case 16394:
/* 525 */           return this._variant.__variant.readField("pscode");
/*     */         case 16390:
/* 527 */           return this._variant.__variant.readField("pcyVal");
/*     */         case 16391:
/* 529 */           return this._variant.__variant.readField("pdate");
/*     */         case 16392:
/* 531 */           return this._variant.__variant.readField("pbstrVal");
/*     */         case 16397:
/* 533 */           return this._variant.__variant.readField("ppunkVal");
/*     */         case 16393:
/* 535 */           return this._variant.__variant.readField("ppdispVal");
/*     */         case 16396:
/* 537 */           return this._variant.__variant.readField("pvarVal");
/*     */         case 16384:
/* 539 */           return this._variant.__variant.readField("byref");
/*     */         case 16:
/* 541 */           return this._variant.__variant.readField("cVal");
/*     */         case 18:
/* 543 */           return this._variant.__variant.readField("uiVal");
/*     */         case 19:
/* 545 */           return this._variant.__variant.readField("ulVal");
/*     */         case 21:
/* 547 */           return this._variant.__variant.readField("ullVal");
/*     */         case 22:
/* 549 */           return this._variant.__variant.readField("intVal");
/*     */         case 23:
/* 551 */           return this._variant.__variant.readField("uintVal");
/*     */         case 16398:
/* 553 */           return this._variant.__variant.readField("pdecVal");
/*     */         case 16400:
/* 555 */           return this._variant.__variant.readField("pcVal");
/*     */         case 16402:
/* 557 */           return this._variant.__variant.readField("puiVal");
/*     */         case 16403:
/* 559 */           return this._variant.__variant.readField("pulVal");
/*     */         case 16405:
/* 561 */           return this._variant.__variant.readField("pullVal");
/*     */         case 16406:
/* 563 */           return this._variant.__variant.readField("pintVal");
/*     */         case 16407:
/* 565 */           return this._variant.__variant.readField("puintVal");
/*     */         case 36:
/* 567 */           return this._variant.__variant.readField("pvRecord");
/*     */       } 
/* 569 */       if ((varType & 0x2000) > 0) {
/* 570 */         if ((varType & 0x4000) > 0) {
/* 571 */           return this._variant.__variant.readField("pparray");
/*     */         }
/* 573 */         return this._variant.__variant.readField("parray");
/*     */       } 
/*     */       
/* 576 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public byte byteValue() {
/* 581 */       return ((Number)getValue()).byteValue();
/*     */     }
/*     */     
/*     */     public short shortValue() {
/* 585 */       return ((Number)getValue()).shortValue();
/*     */     }
/*     */     
/*     */     public int intValue() {
/* 589 */       return ((Number)getValue()).intValue();
/*     */     }
/*     */     
/*     */     public long longValue() {
/* 593 */       return ((Number)getValue()).longValue();
/*     */     }
/*     */     
/*     */     public float floatValue() {
/* 597 */       return ((Number)getValue()).floatValue();
/*     */     }
/*     */     
/*     */     public double doubleValue() {
/* 601 */       return ((Number)getValue()).doubleValue();
/*     */     }
/*     */     
/*     */     public String stringValue() {
/* 605 */       WTypes.BSTR bstr = (WTypes.BSTR)getValue();
/* 606 */       if (bstr == null) {
/* 607 */         return null;
/*     */       }
/* 609 */       return bstr.getValue();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean booleanValue() {
/* 615 */       return ((OaIdl.VARIANT_BOOL)getValue()).booleanValue();
/*     */     }
/*     */     
/*     */     public Date dateValue() {
/* 619 */       OaIdl.DATE varDate = (OaIdl.DATE)getValue();
/* 620 */       if (varDate == null) {
/* 621 */         return null;
/*     */       }
/* 623 */       return varDate.getAsJavaDate();
/*     */     }
/*     */     @FieldOrder({"vt", "wReserved1", "wReserved2", "wReserved3", "__variant"})
/*     */     public static class _VARIANT extends Structure { public WTypes.VARTYPE vt; public short wReserved1; public short wReserved2; public short wReserved3; public __VARIANT __variant;
/*     */       public static class __VARIANT extends Union { public WinDef.LONGLONG llVal; public WinDef.LONG lVal; public WinDef.BYTE bVal; public WinDef.SHORT iVal; public Float fltVal; public Double dblVal; public OaIdl.VARIANT_BOOL boolVal; public WinDef.SCODE scode; public OaIdl.CURRENCY cyVal; public OaIdl.DATE date; public WTypes.BSTR bstrVal; public Unknown punkVal; public Dispatch pdispVal; public OaIdl.SAFEARRAY.ByReference parray; public ByteByReference pbVal; public ShortByReference piVal; public WinDef.LONGByReference plVal; public WinDef.LONGLONGByReference pllVal; public FloatByReference pfltVal; public DoubleByReference pdblVal; public OaIdl.VARIANT_BOOLByReference pboolVal; public OaIdl._VARIANT_BOOLByReference pbool; public WinDef.SCODEByReference pscode; public OaIdl.CURRENCY.ByReference pcyVal; public OaIdl.DATE.ByReference pdate; public WTypes.BSTRByReference pbstrVal; public Unknown.ByReference ppunkVal; public Dispatch.ByReference ppdispVal; public OaIdl.SAFEARRAYByReference pparray; public Variant.VARIANT.ByReference pvarVal; public WinDef.PVOID byref; public WinDef.CHAR cVal; public WinDef.USHORT uiVal; public WinDef.ULONG ulVal; public WinDef.ULONGLONG ullVal; public Integer intVal; public WinDef.UINT uintVal; public OaIdl.DECIMAL.ByReference pdecVal; public WinDef.CHARByReference pcVal;
/*     */         public WinDef.USHORTByReference puiVal;
/*     */         public WinDef.ULONGByReference pulVal;
/*     */         public WinDef.ULONGLONGByReference pullVal;
/*     */         public IntByReference pintVal;
/*     */         public WinDef.UINTByReference puintVal;
/*     */         public BRECORD pvRecord;
/*     */         
/*     */         @FieldOrder({"pvRecord", "pRecInfo"})
/*     */         public static class BRECORD extends Structure { public WinDef.PVOID pvRecord;
/*     */           public Pointer pRecInfo;
/*     */           
/*     */           public static class ByReference extends BRECORD implements Structure.ByReference {}
/*     */           
/*     */           public BRECORD() {}
/*     */           
/*     */           public BRECORD(Pointer pointer) {
/* 644 */             super(pointer);
/*     */           } }
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
/*     */         public __VARIANT() {
/* 741 */           read();
/*     */         }
/*     */         
/*     */         public __VARIANT(Pointer pointer) {
/* 745 */           super(pointer);
/* 746 */           read();
/*     */         } }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       public _VARIANT() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       public _VARIANT(Pointer pointer) {
/* 761 */         super(pointer);
/* 762 */         read();
/*     */       } }
/*     */      public static class __VARIANT extends Union { public WinDef.LONGLONG llVal; public WinDef.LONG lVal; public WinDef.BYTE bVal; public WinDef.SHORT iVal; public Float fltVal; public Double dblVal; public OaIdl.VARIANT_BOOL boolVal; public WinDef.SCODE scode; public OaIdl.CURRENCY cyVal; public OaIdl.DATE date; public WTypes.BSTR bstrVal; public Unknown punkVal; public Dispatch pdispVal; public OaIdl.SAFEARRAY.ByReference parray; public ByteByReference pbVal; public ShortByReference piVal; public WinDef.LONGByReference plVal; public WinDef.LONGLONGByReference pllVal; public FloatByReference pfltVal; public DoubleByReference pdblVal; public OaIdl.VARIANT_BOOLByReference pboolVal; public OaIdl._VARIANT_BOOLByReference pbool; public WinDef.SCODEByReference pscode; public OaIdl.CURRENCY.ByReference pcyVal; public OaIdl.DATE.ByReference pdate; public WTypes.BSTRByReference pbstrVal; public Unknown.ByReference ppunkVal; public Dispatch.ByReference ppdispVal; public OaIdl.SAFEARRAYByReference pparray; public Variant.VARIANT.ByReference pvarVal; public WinDef.PVOID byref; public WinDef.CHAR cVal; public WinDef.USHORT uiVal; public WinDef.ULONG ulVal; public WinDef.ULONGLONG ullVal; public Integer intVal; public WinDef.UINT uintVal; public OaIdl.DECIMAL.ByReference pdecVal; public WinDef.CHARByReference pcVal; public WinDef.USHORTByReference puiVal; public WinDef.ULONGByReference pulVal; public WinDef.ULONGLONGByReference pullVal; public IntByReference pintVal; public WinDef.UINTByReference puintVal; public BRECORD pvRecord; @FieldOrder({"pvRecord", "pRecInfo"})
/*     */       public static class BRECORD extends Structure { public WinDef.PVOID pvRecord; public Pointer pRecInfo; public static class ByReference extends BRECORD implements Structure.ByReference {} public BRECORD() {} public BRECORD(Pointer pointer) {
/*     */           super(pointer);
/*     */         } } public __VARIANT() {
/*     */         read();
/*     */       } public __VARIANT(Pointer pointer) {
/*     */         super(pointer);
/*     */         read();
/*     */       } }
/*     */   }
/*     */   @FieldOrder({"variantArg"})
/*     */   public static class VariantArg extends Structure { public static class ByReference extends VariantArg implements Structure.ByReference { public ByReference(Variant.VARIANT[] variantArg) {
/* 776 */         this.variantArg = variantArg;
/*     */       }
/*     */       public ByReference() {} }
/*     */     
/* 780 */     public Variant.VARIANT[] variantArg = new Variant.VARIANT[1];
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public VariantArg() {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public VariantArg(Pointer pointer) {
/* 791 */       super(pointer);
/*     */     }
/*     */     
/*     */     public VariantArg(Variant.VARIANT[] variantArg) {
/* 795 */       this.variantArg = variantArg;
/*     */     }
/*     */     
/*     */     public void setArraySize(int size) {
/* 799 */       this.variantArg = new Variant.VARIANT[size];
/* 800 */       read();
/*     */     } }
/*     */ 
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\Variant.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */