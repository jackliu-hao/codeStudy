/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.PointerType;
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.Structure.FieldOrder;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface Guid
/*     */ {
/*  42 */   public static final IID IID_NULL = new IID();
/*     */   
/*     */   @FieldOrder({"Data1", "Data2", "Data3", "Data4"})
/*     */   public static class GUID
/*     */     extends Structure
/*     */   {
/*     */     public int Data1;
/*     */     public short Data2;
/*     */     public short Data3;
/*     */     
/*     */     public static class ByValue
/*     */       extends GUID
/*     */       implements Structure.ByValue {
/*     */       public ByValue() {}
/*     */       
/*     */       public ByValue(Guid.GUID guid) {
/*  58 */         super(guid.getPointer());
/*     */         
/*  60 */         this.Data1 = guid.Data1;
/*  61 */         this.Data2 = guid.Data2;
/*  62 */         this.Data3 = guid.Data3;
/*  63 */         this.Data4 = guid.Data4;
/*     */       }
/*     */       public ByValue(Pointer memory) {
/*  66 */         super(memory);
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
/*     */     public static class ByReference
/*     */       extends GUID
/*     */       implements Structure.ByReference
/*     */     {
/*     */       public ByReference() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       public ByReference(Guid.GUID guid) {
/*  91 */         super(guid.getPointer());
/*     */         
/*  93 */         this.Data1 = guid.Data1;
/*  94 */         this.Data2 = guid.Data2;
/*  95 */         this.Data3 = guid.Data3;
/*  96 */         this.Data4 = guid.Data4;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       public ByReference(Pointer memory) {
/* 106 */         super(memory);
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
/* 120 */     public byte[] Data4 = new byte[8];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public GUID() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public GUID(GUID guid) {
/* 135 */       this.Data1 = guid.Data1;
/* 136 */       this.Data2 = guid.Data2;
/* 137 */       this.Data3 = guid.Data3;
/* 138 */       this.Data4 = guid.Data4;
/*     */       
/* 140 */       writeFieldsToMemory();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public GUID(String guid) {
/* 150 */       this(fromString(guid));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public GUID(byte[] data) {
/* 160 */       this(fromBinary(data));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public GUID(Pointer memory) {
/* 170 */       super(memory);
/* 171 */       read();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 176 */       if (o == null) {
/* 177 */         return false;
/*     */       }
/* 179 */       if (this == o) {
/* 180 */         return true;
/*     */       }
/* 182 */       if (getClass() != o.getClass()) {
/* 183 */         return false;
/*     */       }
/*     */       
/* 186 */       GUID other = (GUID)o;
/* 187 */       return (this.Data1 == other.Data1 && this.Data2 == other.Data2 && this.Data3 == other.Data3 && 
/*     */ 
/*     */         
/* 190 */         Arrays.equals(this.Data4, other.Data4));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 195 */       return this.Data1 + this.Data2 & 65535 + this.Data3 & 65535 + Arrays.hashCode(this.Data4);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static GUID fromBinary(byte[] data) {
/* 206 */       if (data.length != 16) {
/* 207 */         throw new IllegalArgumentException("Invalid data length: " + data.length);
/*     */       }
/*     */ 
/*     */       
/* 211 */       GUID newGuid = new GUID();
/* 212 */       long data1Temp = (data[0] & 0xFF);
/* 213 */       data1Temp <<= 8L;
/* 214 */       data1Temp |= (data[1] & 0xFF);
/* 215 */       data1Temp <<= 8L;
/* 216 */       data1Temp |= (data[2] & 0xFF);
/* 217 */       data1Temp <<= 8L;
/* 218 */       data1Temp |= (data[3] & 0xFF);
/* 219 */       newGuid.Data1 = (int)data1Temp;
/*     */       
/* 221 */       int data2Temp = data[4] & 0xFF;
/* 222 */       data2Temp <<= 8;
/* 223 */       data2Temp |= data[5] & 0xFF;
/* 224 */       newGuid.Data2 = (short)data2Temp;
/*     */       
/* 226 */       int data3Temp = data[6] & 0xFF;
/* 227 */       data3Temp <<= 8;
/* 228 */       data3Temp |= data[7] & 0xFF;
/* 229 */       newGuid.Data3 = (short)data3Temp;
/*     */       
/* 231 */       newGuid.Data4[0] = data[8];
/* 232 */       newGuid.Data4[1] = data[9];
/* 233 */       newGuid.Data4[2] = data[10];
/* 234 */       newGuid.Data4[3] = data[11];
/* 235 */       newGuid.Data4[4] = data[12];
/* 236 */       newGuid.Data4[5] = data[13];
/* 237 */       newGuid.Data4[6] = data[14];
/* 238 */       newGuid.Data4[7] = data[15];
/*     */       
/* 240 */       newGuid.writeFieldsToMemory();
/*     */       
/* 242 */       return newGuid;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static GUID fromString(String guid) {
/* 253 */       int y = 0;
/* 254 */       char[] _cnewguid = new char[32];
/* 255 */       char[] _cguid = guid.toCharArray();
/* 256 */       byte[] bdata = new byte[16];
/* 257 */       GUID newGuid = new GUID();
/*     */ 
/*     */       
/* 260 */       if (guid.length() > 38) {
/* 261 */         throw new IllegalArgumentException("Invalid guid length: " + guid
/* 262 */             .length());
/*     */       }
/*     */       
/*     */       int i;
/* 266 */       for (i = 0; i < _cguid.length; i++) {
/* 267 */         if (_cguid[i] != '{' && _cguid[i] != '-' && _cguid[i] != '}')
/*     */         {
/* 269 */           _cnewguid[y++] = _cguid[i];
/*     */         }
/*     */       } 
/*     */       
/* 273 */       for (i = 0; i < 32; i += 2) {
/* 274 */         bdata[i / 2] = 
/* 275 */           (byte)((Character.digit(_cnewguid[i], 16) << 4) + Character.digit(_cnewguid[i + 1], 16) & 0xFF);
/*     */       }
/*     */       
/* 278 */       if (bdata.length != 16) {
/* 279 */         throw new IllegalArgumentException("Invalid data length: " + bdata.length);
/*     */       }
/*     */ 
/*     */       
/* 283 */       long data1Temp = (bdata[0] & 0xFF);
/* 284 */       data1Temp <<= 8L;
/* 285 */       data1Temp |= (bdata[1] & 0xFF);
/* 286 */       data1Temp <<= 8L;
/* 287 */       data1Temp |= (bdata[2] & 0xFF);
/* 288 */       data1Temp <<= 8L;
/* 289 */       data1Temp |= (bdata[3] & 0xFF);
/* 290 */       newGuid.Data1 = (int)data1Temp;
/*     */       
/* 292 */       int data2Temp = bdata[4] & 0xFF;
/* 293 */       data2Temp <<= 8;
/* 294 */       data2Temp |= bdata[5] & 0xFF;
/* 295 */       newGuid.Data2 = (short)data2Temp;
/*     */       
/* 297 */       int data3Temp = bdata[6] & 0xFF;
/* 298 */       data3Temp <<= 8;
/* 299 */       data3Temp |= bdata[7] & 0xFF;
/* 300 */       newGuid.Data3 = (short)data3Temp;
/*     */       
/* 302 */       newGuid.Data4[0] = bdata[8];
/* 303 */       newGuid.Data4[1] = bdata[9];
/* 304 */       newGuid.Data4[2] = bdata[10];
/* 305 */       newGuid.Data4[3] = bdata[11];
/* 306 */       newGuid.Data4[4] = bdata[12];
/* 307 */       newGuid.Data4[5] = bdata[13];
/* 308 */       newGuid.Data4[6] = bdata[14];
/* 309 */       newGuid.Data4[7] = bdata[15];
/*     */       
/* 311 */       newGuid.writeFieldsToMemory();
/*     */       
/* 313 */       return newGuid;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static GUID newGuid() {
/* 323 */       SecureRandom ng = new SecureRandom();
/* 324 */       byte[] randomBytes = new byte[16];
/*     */       
/* 326 */       ng.nextBytes(randomBytes);
/* 327 */       randomBytes[6] = (byte)(randomBytes[6] & 0xF);
/* 328 */       randomBytes[6] = (byte)(randomBytes[6] | 0x40);
/* 329 */       randomBytes[8] = (byte)(randomBytes[8] & 0x3F);
/* 330 */       randomBytes[8] = (byte)(randomBytes[8] | 0x80);
/*     */       
/* 332 */       return new GUID(randomBytes);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public byte[] toByteArray() {
/* 341 */       byte[] guid = new byte[16];
/*     */       
/* 343 */       byte[] bytes1 = new byte[4];
/* 344 */       bytes1[0] = (byte)(this.Data1 >> 24);
/* 345 */       bytes1[1] = (byte)(this.Data1 >> 16);
/* 346 */       bytes1[2] = (byte)(this.Data1 >> 8);
/* 347 */       bytes1[3] = (byte)(this.Data1 >> 0);
/*     */       
/* 349 */       byte[] bytes2 = new byte[4];
/* 350 */       bytes2[0] = (byte)(this.Data2 >> 24);
/* 351 */       bytes2[1] = (byte)(this.Data2 >> 16);
/* 352 */       bytes2[2] = (byte)(this.Data2 >> 8);
/* 353 */       bytes2[3] = (byte)(this.Data2 >> 0);
/*     */       
/* 355 */       byte[] bytes3 = new byte[4];
/* 356 */       bytes3[0] = (byte)(this.Data3 >> 24);
/* 357 */       bytes3[1] = (byte)(this.Data3 >> 16);
/* 358 */       bytes3[2] = (byte)(this.Data3 >> 8);
/* 359 */       bytes3[3] = (byte)(this.Data3 >> 0);
/*     */       
/* 361 */       System.arraycopy(bytes1, 0, guid, 0, 4);
/* 362 */       System.arraycopy(bytes2, 2, guid, 4, 2);
/* 363 */       System.arraycopy(bytes3, 2, guid, 6, 2);
/* 364 */       System.arraycopy(this.Data4, 0, guid, 8, 8);
/*     */       
/* 366 */       return guid;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String toGuidString() {
/* 376 */       String HEXES = "0123456789ABCDEF";
/* 377 */       byte[] bGuid = toByteArray();
/*     */       
/* 379 */       StringBuilder hexStr = new StringBuilder(2 * bGuid.length);
/* 380 */       hexStr.append("{");
/*     */       
/* 382 */       for (int i = 0; i < bGuid.length; i++) {
/* 383 */         char ch1 = "0123456789ABCDEF".charAt((bGuid[i] & 0xF0) >> 4);
/* 384 */         char ch2 = "0123456789ABCDEF".charAt(bGuid[i] & 0xF);
/* 385 */         hexStr.append(ch1).append(ch2);
/*     */         
/* 387 */         if (i == 3 || i == 5 || i == 7 || i == 9) {
/* 388 */           hexStr.append("-");
/*     */         }
/*     */       } 
/* 391 */       hexStr.append("}");
/* 392 */       return hexStr.toString();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void writeFieldsToMemory() {
/* 399 */       for (String name : getFieldOrder()) {
/* 400 */         writeField(name);
/*     */       }
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
/*     */   public static class CLSID
/*     */     extends GUID
/*     */   {
/*     */     public static class ByReference
/*     */       extends Guid.GUID
/*     */     {
/*     */       public ByReference() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       public ByReference(Guid.GUID guid) {
/* 433 */         super(guid);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       public ByReference(Pointer memory) {
/* 443 */         super(memory);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CLSID() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CLSID(String guid) {
/* 460 */       super(guid);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CLSID(Guid.GUID guid) {
/* 469 */       super(guid);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class REFIID
/*     */     extends PointerType
/*     */   {
/*     */     public REFIID() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public REFIID(Pointer memory) {
/* 515 */       super(memory);
/*     */     }
/*     */     
/*     */     public REFIID(Guid.IID guid) {
/* 519 */       super(guid.getPointer());
/*     */     }
/*     */     
/*     */     public void setValue(Guid.IID value) {
/* 523 */       setPointer(value.getPointer());
/*     */     }
/*     */     
/*     */     public Guid.IID getValue() {
/* 527 */       return new Guid.IID(getPointer());
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 532 */       if (o == null) {
/* 533 */         return false;
/*     */       }
/* 535 */       if (this == o) {
/* 536 */         return true;
/*     */       }
/* 538 */       if (getClass() != o.getClass()) {
/* 539 */         return false;
/*     */       }
/*     */       
/* 542 */       REFIID other = (REFIID)o;
/* 543 */       return getValue().equals(other.getValue());
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 548 */       return getValue().hashCode();
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
/*     */   public static class IID
/*     */     extends GUID
/*     */   {
/*     */     public IID() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public IID(Pointer memory) {
/* 573 */       super(memory);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public IID(String iid) {
/* 582 */       super(iid);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public IID(byte[] data) {
/* 592 */       super(data);
/*     */     }
/*     */     
/*     */     public IID(Guid.GUID guid) {
/* 596 */       this(guid.toGuidString());
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\Guid.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */