/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.PointerType;
/*     */ import com.sun.jna.Structure;
/*     */ import java.io.UnsupportedEncodingException;
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
/*     */ public interface WTypes
/*     */ {
/*     */   public static final int CLSCTX_INPROC_SERVER = 1;
/*     */   public static final int CLSCTX_INPROC_HANDLER = 2;
/*     */   public static final int CLSCTX_LOCAL_SERVER = 4;
/*     */   public static final int CLSCTX_INPROC_SERVER16 = 8;
/*     */   public static final int CLSCTX_REMOTE_SERVER = 16;
/*     */   public static final int CLSCTX_INPROC_HANDLER16 = 32;
/*     */   public static final int CLSCTX_RESERVED1 = 64;
/*     */   public static final int CLSCTX_RESERVED2 = 128;
/*     */   public static final int CLSCTX_RESERVED3 = 256;
/*     */   public static final int CLSCTX_RESERVED4 = 512;
/*     */   public static final int CLSCTX_NO_CODE_DOWNLOAD = 1024;
/*     */   public static final int CLSCTX_RESERVED5 = 2048;
/*     */   public static final int CLSCTX_NO_CUSTOM_MARSHAL = 4096;
/*     */   public static final int CLSCTX_ENABLE_CODE_DOWNLOAD = 8192;
/*     */   public static final int CLSCTX_NO_FAILURE_LOG = 16384;
/*     */   public static final int CLSCTX_DISABLE_AAA = 32768;
/*     */   public static final int CLSCTX_ENABLE_AAA = 65536;
/*     */   public static final int CLSCTX_FROM_DEFAULT_CONTEXT = 131072;
/*     */   public static final int CLSCTX_ACTIVATE_32_BIT_SERVER = 262144;
/*     */   public static final int CLSCTX_ACTIVATE_64_BIT_SERVER = 524288;
/*     */   public static final int CLSCTX_ENABLE_CLOAKING = 1048576;
/*     */   public static final int CLSCTX_APPCONTAINER = 4194304;
/*     */   public static final int CLSCTX_ACTIVATE_AAA_AS_IU = 8388608;
/*     */   public static final int CLSCTX_PS_DLL = -2147483648;
/*     */   public static final int CLSCTX_SERVER = 21;
/*     */   public static final int CLSCTX_ALL = 7;
/*     */   
/*     */   public static class BSTR
/*     */     extends PointerType
/*     */   {
/*     */     public BSTR() {
/* 103 */       super(Pointer.NULL);
/*     */     }
/*     */     
/*     */     public BSTR(Pointer pointer) {
/* 107 */       super(pointer);
/*     */     }
/*     */ 
/*     */     
/*     */     public BSTR(String value) {
/* 112 */       setValue(value);
/*     */     }
/*     */     
/*     */     public void setValue(String value) {
/* 116 */       if (value == null) {
/* 117 */         value = "";
/*     */       }
/*     */       try {
/* 120 */         byte[] encodedValue = value.getBytes("UTF-16LE");
/*     */ 
/*     */         
/* 123 */         Memory mem = new Memory((4 + encodedValue.length + 2));
/* 124 */         mem.clear();
/* 125 */         mem.setInt(0L, encodedValue.length);
/* 126 */         mem.write(4L, encodedValue, 0, encodedValue.length);
/* 127 */         setPointer(mem.share(4L));
/* 128 */       } catch (UnsupportedEncodingException ex) {
/* 129 */         throw new RuntimeException("UTF-16LE charset is not supported", ex);
/*     */       } 
/*     */     }
/*     */     
/*     */     public String getValue() {
/*     */       try {
/* 135 */         Pointer pointer = getPointer();
/* 136 */         if (pointer == null) {
/* 137 */           return "";
/*     */         }
/* 139 */         int stringLength = pointer.getInt(-4L);
/* 140 */         return new String(pointer.getByteArray(0L, stringLength), "UTF-16LE");
/* 141 */       } catch (UnsupportedEncodingException ex) {
/* 142 */         throw new RuntimeException("UTF-16LE charset is not supported", ex);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 148 */       return getValue();
/*     */     }
/*     */   }
/*     */   
/*     */   public static class BSTRByReference extends com.sun.jna.ptr.ByReference {
/*     */     public BSTRByReference() {
/* 154 */       super(Native.POINTER_SIZE);
/*     */     }
/*     */     
/*     */     public BSTRByReference(WTypes.BSTR value) {
/* 158 */       this();
/* 159 */       setValue(value);
/*     */     }
/*     */     
/*     */     public void setValue(WTypes.BSTR value) {
/* 163 */       getPointer().setPointer(0L, value.getPointer());
/*     */     }
/*     */     
/*     */     public WTypes.BSTR getValue() {
/* 167 */       return new WTypes.BSTR(getPointer().getPointer(0L));
/*     */     }
/*     */     
/*     */     public String getString() {
/* 171 */       return getValue().getValue();
/*     */     }
/*     */   }
/*     */   
/*     */   public static class LPSTR
/*     */     extends PointerType {
/*     */     public static class ByReference
/*     */       extends LPSTR implements Structure.ByReference {}
/*     */     
/*     */     public LPSTR() {
/* 181 */       super(Pointer.NULL);
/*     */     }
/*     */     
/*     */     public LPSTR(Pointer pointer) {
/* 185 */       super(pointer);
/*     */     }
/*     */     
/*     */     public LPSTR(String value) {
/* 189 */       this((Pointer)new Memory(value.length() + 1L));
/* 190 */       setValue(value);
/*     */     }
/*     */     
/*     */     public void setValue(String value) {
/* 194 */       getPointer().setString(0L, value);
/*     */     }
/*     */     
/*     */     public String getValue() {
/* 198 */       Pointer pointer = getPointer();
/* 199 */       String str = null;
/* 200 */       if (pointer != null) {
/* 201 */         str = pointer.getString(0L);
/*     */       }
/* 203 */       return str;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 208 */       return getValue();
/*     */     }
/*     */   }
/*     */   
/*     */   public static class LPWSTR
/*     */     extends PointerType {
/*     */     public static class ByReference
/*     */       extends LPWSTR implements Structure.ByReference {}
/*     */     
/*     */     public LPWSTR() {
/* 218 */       super(Pointer.NULL);
/*     */     }
/*     */     
/*     */     public LPWSTR(Pointer pointer) {
/* 222 */       super(pointer);
/*     */     }
/*     */     
/*     */     public LPWSTR(String value) {
/* 226 */       this((Pointer)new Memory((value.length() + 1L) * Native.WCHAR_SIZE));
/* 227 */       setValue(value);
/*     */     }
/*     */     
/*     */     public void setValue(String value) {
/* 231 */       getPointer().setWideString(0L, value);
/*     */     }
/*     */     
/*     */     public String getValue() {
/* 235 */       Pointer pointer = getPointer();
/* 236 */       String str = null;
/* 237 */       if (pointer != null) {
/* 238 */         str = pointer.getWideString(0L);
/*     */       }
/* 240 */       return str;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 245 */       return getValue();
/*     */     }
/*     */   }
/*     */   
/*     */   public static class LPOLESTR
/*     */     extends PointerType {
/*     */     public static class ByReference
/*     */       extends LPOLESTR implements Structure.ByReference {}
/*     */     
/*     */     public LPOLESTR() {
/* 255 */       super(Pointer.NULL);
/*     */     }
/*     */     
/*     */     public LPOLESTR(Pointer pointer) {
/* 259 */       super(pointer);
/*     */     }
/*     */     
/*     */     public LPOLESTR(String value) {
/* 263 */       super((Pointer)new Memory((value.length() + 1L) * Native.WCHAR_SIZE));
/* 264 */       setValue(value);
/*     */     }
/*     */     
/*     */     public void setValue(String value) {
/* 268 */       getPointer().setWideString(0L, value);
/*     */     }
/*     */     
/*     */     public String getValue() {
/* 272 */       Pointer pointer = getPointer();
/* 273 */       String str = null;
/* 274 */       if (pointer != null) {
/* 275 */         str = pointer.getWideString(0L);
/*     */       }
/* 277 */       return str;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 282 */       return getValue();
/*     */     }
/*     */   }
/*     */   
/*     */   public static class VARTYPE extends WinDef.USHORT {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */     public VARTYPE() {
/* 290 */       this(0);
/*     */     }
/*     */     
/*     */     public VARTYPE(int value) {
/* 294 */       super(value);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class VARTYPEByReference extends com.sun.jna.ptr.ByReference {
/*     */     public VARTYPEByReference() {
/* 300 */       super(2);
/*     */     }
/*     */     
/*     */     public VARTYPEByReference(WTypes.VARTYPE type) {
/* 304 */       super(2);
/* 305 */       setValue(type);
/*     */     }
/*     */     
/*     */     public VARTYPEByReference(short type) {
/* 309 */       super(2);
/* 310 */       getPointer().setShort(0L, type);
/*     */     }
/*     */     
/*     */     public void setValue(WTypes.VARTYPE value) {
/* 314 */       getPointer().setShort(0L, value.shortValue());
/*     */     }
/*     */     
/*     */     public WTypes.VARTYPE getValue() {
/* 318 */       return new WTypes.VARTYPE(getPointer().getShort(0L));
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\WTypes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */