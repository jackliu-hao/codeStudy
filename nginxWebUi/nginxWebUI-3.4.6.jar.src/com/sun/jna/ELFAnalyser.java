/*     */ package com.sun.jna;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.math.BigInteger;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ELFAnalyser
/*     */ {
/*  51 */   private static final byte[] ELF_MAGIC = new byte[] { Byte.MAX_VALUE, 69, 76, 70 };
/*     */   
/*     */   private static final int EF_ARM_ABI_FLOAT_HARD = 1024;
/*     */   
/*     */   private static final int EF_ARM_ABI_FLOAT_SOFT = 512;
/*     */   
/*     */   private static final int EI_DATA_BIG_ENDIAN = 2;
/*     */   
/*     */   private static final int E_MACHINE_ARM = 40;
/*     */   
/*     */   private static final int EI_CLASS_64BIT = 2;
/*     */   
/*     */   private final String filename;
/*     */ 
/*     */   
/*     */   public static ELFAnalyser analyse(String filename) throws IOException {
/*  67 */     ELFAnalyser res = new ELFAnalyser(filename);
/*  68 */     res.runDetection();
/*  69 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean ELF = false;
/*     */   
/*     */   private boolean _64Bit = false;
/*     */   
/*     */   private boolean bigEndian = false;
/*     */   
/*     */   private boolean armHardFloatFlag = false;
/*     */   private boolean armSoftFloatFlag = false;
/*     */   private boolean armEabiAapcsVfp = false;
/*     */   private boolean arm = false;
/*     */   
/*     */   public boolean isELF() {
/*  85 */     return this.ELF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean is64Bit() {
/*  93 */     return this._64Bit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isBigEndian() {
/* 101 */     return this.bigEndian;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFilename() {
/* 108 */     return this.filename;
/*     */   }
/*     */   
/*     */   public boolean isArmHardFloat() {
/* 112 */     return (isArmEabiAapcsVfp() || isArmHardFloatFlag());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isArmEabiAapcsVfp() {
/* 120 */     return this.armEabiAapcsVfp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isArmHardFloatFlag() {
/* 128 */     return this.armHardFloatFlag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isArmSoftFloatFlag() {
/* 136 */     return this.armSoftFloatFlag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isArm() {
/* 144 */     return this.arm;
/*     */   }
/*     */   
/*     */   private ELFAnalyser(String filename) {
/* 148 */     this.filename = filename;
/*     */   }
/*     */   
/*     */   private void runDetection() throws IOException {
/* 152 */     RandomAccessFile raf = new RandomAccessFile(this.filename, "r");
/*     */ 
/*     */     
/*     */     try {
/* 156 */       if (raf.length() > 4L) {
/* 157 */         byte[] magic = new byte[4];
/* 158 */         raf.seek(0L);
/* 159 */         raf.read(magic);
/* 160 */         if (Arrays.equals(magic, ELF_MAGIC)) {
/* 161 */           this.ELF = true;
/*     */         }
/*     */       } 
/* 164 */       if (!this.ELF) {
/*     */         return;
/*     */       }
/* 167 */       raf.seek(4L);
/*     */ 
/*     */       
/* 170 */       byte sizeIndicator = raf.readByte();
/* 171 */       byte endianessIndicator = raf.readByte();
/* 172 */       this._64Bit = (sizeIndicator == 2);
/* 173 */       this.bigEndian = (endianessIndicator == 2);
/* 174 */       raf.seek(0L);
/*     */       
/* 176 */       ByteBuffer headerData = ByteBuffer.allocate(this._64Bit ? 64 : 52);
/* 177 */       raf.getChannel().read(headerData, 0L);
/*     */       
/* 179 */       headerData.order(this.bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
/*     */ 
/*     */       
/* 182 */       this.arm = (headerData.get(18) == 40);
/*     */       
/* 184 */       if (this.arm) {
/*     */         
/* 186 */         int flags = headerData.getInt(this._64Bit ? 48 : 36);
/* 187 */         this.armHardFloatFlag = ((flags & 0x400) == 1024);
/* 188 */         this.armSoftFloatFlag = ((flags & 0x200) == 512);
/*     */         
/* 190 */         parseEabiAapcsVfp(headerData, raf);
/*     */       } 
/*     */     } finally {
/*     */       try {
/* 194 */         raf.close();
/* 195 */       } catch (IOException iOException) {}
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void parseEabiAapcsVfp(ByteBuffer headerData, RandomAccessFile raf) throws IOException {
/* 202 */     ELFSectionHeaders sectionHeaders = new ELFSectionHeaders(this._64Bit, this.bigEndian, headerData, raf);
/*     */     
/* 204 */     for (ELFSectionHeaderEntry eshe : sectionHeaders.getEntries()) {
/* 205 */       if (".ARM.attributes".equals(eshe.getName())) {
/* 206 */         ByteBuffer armAttributesBuffer = ByteBuffer.allocate(eshe.getSize());
/* 207 */         armAttributesBuffer.order(this.bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
/* 208 */         raf.getChannel().read(armAttributesBuffer, eshe.getOffset());
/* 209 */         armAttributesBuffer.rewind();
/* 210 */         Map<Integer, Map<ArmAeabiAttributesTag, Object>> armAttributes = parseArmAttributes(armAttributesBuffer);
/* 211 */         Map<ArmAeabiAttributesTag, Object> fileAttributes = armAttributes.get(Integer.valueOf(1));
/* 212 */         if (fileAttributes == null) {
/*     */           continue;
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 222 */         Object abiVFPargValue = fileAttributes.get(ArmAeabiAttributesTag.ABI_VFP_args);
/* 223 */         if (abiVFPargValue instanceof Integer && ((Integer)abiVFPargValue).equals(Integer.valueOf(1))) {
/* 224 */           this.armEabiAapcsVfp = true; continue;
/* 225 */         }  if (abiVFPargValue instanceof BigInteger && ((BigInteger)abiVFPargValue).intValue() == 1)
/* 226 */           this.armEabiAapcsVfp = true; 
/*     */       } 
/*     */     } 
/*     */   } static class ELFSectionHeaders { public ELFSectionHeaders(boolean _64bit, boolean bigEndian, ByteBuffer headerData, RandomAccessFile raf) throws IOException {
/*     */       long shoff;
/*     */       int shentsize, shnum;
/*     */       short shstrndx;
/* 233 */       this.entries = new ArrayList<ELFAnalyser.ELFSectionHeaderEntry>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 240 */       if (_64bit) {
/* 241 */         shoff = headerData.getLong(40);
/* 242 */         shentsize = headerData.getShort(58);
/* 243 */         shnum = headerData.getShort(60);
/* 244 */         shstrndx = headerData.getShort(62);
/*     */       } else {
/* 246 */         shoff = headerData.getInt(32);
/* 247 */         shentsize = headerData.getShort(46);
/* 248 */         shnum = headerData.getShort(48);
/* 249 */         shstrndx = headerData.getShort(50);
/*     */       } 
/*     */       
/* 252 */       int tableLength = shnum * shentsize;
/*     */       
/* 254 */       ByteBuffer data = ByteBuffer.allocate(tableLength);
/* 255 */       data.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
/* 256 */       raf.getChannel().read(data, shoff);
/*     */       
/* 258 */       for (int i = 0; i < shnum; i++) {
/* 259 */         data.position(i * shentsize);
/* 260 */         ByteBuffer header = data.slice();
/* 261 */         header.order(data.order());
/* 262 */         header.limit(shentsize);
/* 263 */         this.entries.add(new ELFAnalyser.ELFSectionHeaderEntry(_64bit, header));
/*     */       } 
/*     */       
/* 266 */       ELFAnalyser.ELFSectionHeaderEntry stringTable = this.entries.get(shstrndx);
/* 267 */       ByteBuffer stringBuffer = ByteBuffer.allocate(stringTable.getSize());
/* 268 */       stringBuffer.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
/* 269 */       raf.getChannel().read(stringBuffer, stringTable.getOffset());
/* 270 */       stringBuffer.rewind();
/*     */       
/* 272 */       ByteArrayOutputStream baos = new ByteArrayOutputStream(20);
/* 273 */       for (ELFAnalyser.ELFSectionHeaderEntry eshe : this.entries) {
/* 274 */         baos.reset();
/*     */         
/* 276 */         stringBuffer.position(eshe.getNameOffset());
/*     */         
/* 278 */         while (stringBuffer.position() < stringBuffer.limit()) {
/* 279 */           byte b = stringBuffer.get();
/* 280 */           if (b == 0) {
/*     */             break;
/*     */           }
/* 283 */           baos.write(b);
/*     */         } 
/*     */ 
/*     */         
/* 287 */         eshe.setName(baos.toString("ASCII"));
/*     */       } 
/*     */     }
/*     */     private final List<ELFAnalyser.ELFSectionHeaderEntry> entries;
/*     */     public List<ELFAnalyser.ELFSectionHeaderEntry> getEntries() {
/* 292 */       return this.entries;
/*     */     } }
/*     */ 
/*     */   
/*     */   static class ELFSectionHeaderEntry {
/*     */     private final int nameOffset;
/*     */     private String name;
/*     */     private final int type;
/*     */     private final int flags;
/*     */     private final int offset;
/*     */     private final int size;
/*     */     
/*     */     public ELFSectionHeaderEntry(boolean _64bit, ByteBuffer sectionHeaderData) {
/* 305 */       this.nameOffset = sectionHeaderData.getInt(0);
/* 306 */       this.type = sectionHeaderData.getInt(4);
/* 307 */       this.flags = (int)(_64bit ? sectionHeaderData.getLong(8) : sectionHeaderData.getInt(8));
/* 308 */       this.offset = (int)(_64bit ? sectionHeaderData.getLong(24) : sectionHeaderData.getInt(16));
/* 309 */       this.size = (int)(_64bit ? sectionHeaderData.getLong(32) : sectionHeaderData.getInt(20));
/*     */     }
/*     */     
/*     */     public String getName() {
/* 313 */       return this.name;
/*     */     }
/*     */     
/*     */     public void setName(String name) {
/* 317 */       this.name = name;
/*     */     }
/*     */     
/*     */     public int getNameOffset() {
/* 321 */       return this.nameOffset;
/*     */     }
/*     */     
/*     */     public int getType() {
/* 325 */       return this.type;
/*     */     }
/*     */     
/*     */     public int getFlags() {
/* 329 */       return this.flags;
/*     */     }
/*     */     
/*     */     public int getOffset() {
/* 333 */       return this.offset;
/*     */     }
/*     */     
/*     */     public int getSize() {
/* 337 */       return this.size;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 342 */       return "ELFSectionHeaderEntry{nameIdx=" + this.nameOffset + ", name=" + this.name + ", type=" + this.type + ", flags=" + this.flags + ", offset=" + this.offset + ", size=" + this.size + '}';
/*     */     } }
/*     */   
/*     */   static class ArmAeabiAttributesTag { private final int value;
/*     */     private final String name;
/*     */     private final ParameterType parameterType;
/*     */     
/* 349 */     public enum ParameterType { UINT32, NTBS, ULEB128; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ArmAeabiAttributesTag(int value, String name, ParameterType parameterType) {
/* 357 */       this.value = value;
/* 358 */       this.name = name;
/* 359 */       this.parameterType = parameterType;
/*     */     }
/*     */     
/*     */     public int getValue() {
/* 363 */       return this.value;
/*     */     }
/*     */     
/*     */     public String getName() {
/* 367 */       return this.name;
/*     */     }
/*     */     
/*     */     public ParameterType getParameterType() {
/* 371 */       return this.parameterType;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 376 */       return this.name + " (" + this.value + ")";
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 381 */       int hash = 7;
/* 382 */       hash = 67 * hash + this.value;
/* 383 */       return hash;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 388 */       if (this == obj) {
/* 389 */         return true;
/*     */       }
/* 391 */       if (obj == null) {
/* 392 */         return false;
/*     */       }
/* 394 */       if (getClass() != obj.getClass()) {
/* 395 */         return false;
/*     */       }
/* 397 */       ArmAeabiAttributesTag other = (ArmAeabiAttributesTag)obj;
/* 398 */       if (this.value != other.value) {
/* 399 */         return false;
/*     */       }
/* 401 */       return true;
/*     */     }
/*     */     
/* 404 */     private static final List<ArmAeabiAttributesTag> tags = new LinkedList<ArmAeabiAttributesTag>();
/* 405 */     private static final Map<Integer, ArmAeabiAttributesTag> valueMap = new HashMap<Integer, ArmAeabiAttributesTag>();
/* 406 */     private static final Map<String, ArmAeabiAttributesTag> nameMap = new HashMap<String, ArmAeabiAttributesTag>();
/*     */ 
/*     */     
/* 409 */     public static final ArmAeabiAttributesTag File = addTag(1, "File", ParameterType.UINT32);
/* 410 */     public static final ArmAeabiAttributesTag Section = addTag(2, "Section", ParameterType.UINT32);
/* 411 */     public static final ArmAeabiAttributesTag Symbol = addTag(3, "Symbol", ParameterType.UINT32);
/* 412 */     public static final ArmAeabiAttributesTag CPU_raw_name = addTag(4, "CPU_raw_name", ParameterType.NTBS);
/* 413 */     public static final ArmAeabiAttributesTag CPU_name = addTag(5, "CPU_name", ParameterType.NTBS);
/* 414 */     public static final ArmAeabiAttributesTag CPU_arch = addTag(6, "CPU_arch", ParameterType.ULEB128);
/* 415 */     public static final ArmAeabiAttributesTag CPU_arch_profile = addTag(7, "CPU_arch_profile", ParameterType.ULEB128);
/* 416 */     public static final ArmAeabiAttributesTag ARM_ISA_use = addTag(8, "ARM_ISA_use", ParameterType.ULEB128);
/* 417 */     public static final ArmAeabiAttributesTag THUMB_ISA_use = addTag(9, "THUMB_ISA_use", ParameterType.ULEB128);
/* 418 */     public static final ArmAeabiAttributesTag FP_arch = addTag(10, "FP_arch", ParameterType.ULEB128);
/* 419 */     public static final ArmAeabiAttributesTag WMMX_arch = addTag(11, "WMMX_arch", ParameterType.ULEB128);
/* 420 */     public static final ArmAeabiAttributesTag Advanced_SIMD_arch = addTag(12, "Advanced_SIMD_arch", ParameterType.ULEB128);
/* 421 */     public static final ArmAeabiAttributesTag PCS_config = addTag(13, "PCS_config", ParameterType.ULEB128);
/* 422 */     public static final ArmAeabiAttributesTag ABI_PCS_R9_use = addTag(14, "ABI_PCS_R9_use", ParameterType.ULEB128);
/* 423 */     public static final ArmAeabiAttributesTag ABI_PCS_RW_data = addTag(15, "ABI_PCS_RW_data", ParameterType.ULEB128);
/* 424 */     public static final ArmAeabiAttributesTag ABI_PCS_RO_data = addTag(16, "ABI_PCS_RO_data", ParameterType.ULEB128);
/* 425 */     public static final ArmAeabiAttributesTag ABI_PCS_GOT_use = addTag(17, "ABI_PCS_GOT_use", ParameterType.ULEB128);
/* 426 */     public static final ArmAeabiAttributesTag ABI_PCS_wchar_t = addTag(18, "ABI_PCS_wchar_t", ParameterType.ULEB128);
/* 427 */     public static final ArmAeabiAttributesTag ABI_FP_rounding = addTag(19, "ABI_FP_rounding", ParameterType.ULEB128);
/* 428 */     public static final ArmAeabiAttributesTag ABI_FP_denormal = addTag(20, "ABI_FP_denormal", ParameterType.ULEB128);
/* 429 */     public static final ArmAeabiAttributesTag ABI_FP_exceptions = addTag(21, "ABI_FP_exceptions", ParameterType.ULEB128);
/* 430 */     public static final ArmAeabiAttributesTag ABI_FP_user_exceptions = addTag(22, "ABI_FP_user_exceptions", ParameterType.ULEB128);
/* 431 */     public static final ArmAeabiAttributesTag ABI_FP_number_model = addTag(23, "ABI_FP_number_model", ParameterType.ULEB128);
/* 432 */     public static final ArmAeabiAttributesTag ABI_align_needed = addTag(24, "ABI_align_needed", ParameterType.ULEB128);
/* 433 */     public static final ArmAeabiAttributesTag ABI_align8_preserved = addTag(25, "ABI_align8_preserved", ParameterType.ULEB128);
/* 434 */     public static final ArmAeabiAttributesTag ABI_enum_size = addTag(26, "ABI_enum_size", ParameterType.ULEB128);
/* 435 */     public static final ArmAeabiAttributesTag ABI_HardFP_use = addTag(27, "ABI_HardFP_use", ParameterType.ULEB128);
/* 436 */     public static final ArmAeabiAttributesTag ABI_VFP_args = addTag(28, "ABI_VFP_args", ParameterType.ULEB128);
/* 437 */     public static final ArmAeabiAttributesTag ABI_WMMX_args = addTag(29, "ABI_WMMX_args", ParameterType.ULEB128);
/* 438 */     public static final ArmAeabiAttributesTag ABI_optimization_goals = addTag(30, "ABI_optimization_goals", ParameterType.ULEB128);
/* 439 */     public static final ArmAeabiAttributesTag ABI_FP_optimization_goals = addTag(31, "ABI_FP_optimization_goals", ParameterType.ULEB128);
/* 440 */     public static final ArmAeabiAttributesTag compatibility = addTag(32, "compatibility", ParameterType.NTBS);
/* 441 */     public static final ArmAeabiAttributesTag CPU_unaligned_access = addTag(34, "CPU_unaligned_access", ParameterType.ULEB128);
/* 442 */     public static final ArmAeabiAttributesTag FP_HP_extension = addTag(36, "FP_HP_extension", ParameterType.ULEB128);
/* 443 */     public static final ArmAeabiAttributesTag ABI_FP_16bit_format = addTag(38, "ABI_FP_16bit_format", ParameterType.ULEB128);
/* 444 */     public static final ArmAeabiAttributesTag MPextension_use = addTag(42, "MPextension_use", ParameterType.ULEB128);
/* 445 */     public static final ArmAeabiAttributesTag DIV_use = addTag(44, "DIV_use", ParameterType.ULEB128);
/* 446 */     public static final ArmAeabiAttributesTag nodefaults = addTag(64, "nodefaults", ParameterType.ULEB128);
/* 447 */     public static final ArmAeabiAttributesTag also_compatible_with = addTag(65, "also_compatible_with", ParameterType.NTBS);
/* 448 */     public static final ArmAeabiAttributesTag conformance = addTag(67, "conformance", ParameterType.NTBS);
/* 449 */     public static final ArmAeabiAttributesTag T2EE_use = addTag(66, "T2EE_use", ParameterType.ULEB128);
/* 450 */     public static final ArmAeabiAttributesTag Virtualization_use = addTag(68, "Virtualization_use", ParameterType.ULEB128);
/* 451 */     public static final ArmAeabiAttributesTag MPextension_use2 = addTag(70, "MPextension_use", ParameterType.ULEB128);
/*     */     
/*     */     private static ArmAeabiAttributesTag addTag(int value, String name, ParameterType type) {
/* 454 */       ArmAeabiAttributesTag tag = new ArmAeabiAttributesTag(value, name, type);
/*     */       
/* 456 */       if (!valueMap.containsKey(Integer.valueOf(tag.getValue()))) {
/* 457 */         valueMap.put(Integer.valueOf(tag.getValue()), tag);
/*     */       }
/* 459 */       if (!nameMap.containsKey(tag.getName())) {
/* 460 */         nameMap.put(tag.getName(), tag);
/*     */       }
/* 462 */       tags.add(tag);
/* 463 */       return tag;
/*     */     }
/*     */     
/*     */     public static List<ArmAeabiAttributesTag> getTags() {
/* 467 */       return Collections.unmodifiableList(tags);
/*     */     }
/*     */     
/*     */     public static ArmAeabiAttributesTag getByName(String name) {
/* 471 */       return nameMap.get(name);
/*     */     }
/*     */     
/*     */     public static ArmAeabiAttributesTag getByValue(int value) {
/* 475 */       if (valueMap.containsKey(Integer.valueOf(value))) {
/* 476 */         return valueMap.get(Integer.valueOf(value));
/*     */       }
/* 478 */       ArmAeabiAttributesTag pseudoTag = new ArmAeabiAttributesTag(value, "Unknown " + value, getParameterType(value));
/* 479 */       return pseudoTag;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private static ParameterType getParameterType(int value) {
/* 485 */       ArmAeabiAttributesTag tag = getByValue(value);
/* 486 */       if (tag == null) {
/* 487 */         if (value % 2 == 0) {
/* 488 */           return ParameterType.ULEB128;
/*     */         }
/* 490 */         return ParameterType.NTBS;
/*     */       } 
/*     */       
/* 493 */       return tag.getParameterType();
/*     */     } }
/*     */ 
/*     */   
/*     */   public enum ParameterType { UINT32, NTBS, ULEB128; }
/*     */   
/*     */   private static Map<Integer, Map<ArmAeabiAttributesTag, Object>> parseArmAttributes(ByteBuffer bb) {
/* 500 */     byte format = bb.get();
/* 501 */     if (format != 65)
/*     */     {
/*     */       
/* 504 */       return Collections.EMPTY_MAP;
/*     */     }
/* 506 */     while (bb.position() < bb.limit()) {
/* 507 */       int posSectionStart = bb.position();
/* 508 */       int sectionLength = bb.getInt();
/* 509 */       if (sectionLength <= 0) {
/*     */         break;
/*     */       }
/*     */       
/* 513 */       String vendorName = readNTBS(bb, null);
/* 514 */       if ("aeabi".equals(vendorName)) {
/* 515 */         return parseAEABI(bb);
/*     */       }
/* 517 */       bb.position(posSectionStart + sectionLength);
/*     */     } 
/* 519 */     return Collections.EMPTY_MAP;
/*     */   }
/*     */   
/*     */   private static Map<Integer, Map<ArmAeabiAttributesTag, Object>> parseAEABI(ByteBuffer buffer) {
/* 523 */     Map<Integer, Map<ArmAeabiAttributesTag, Object>> data = new HashMap<Integer, Map<ArmAeabiAttributesTag, Object>>();
/* 524 */     while (buffer.position() < buffer.limit()) {
/* 525 */       int pos = buffer.position();
/* 526 */       int subsectionTag = readULEB128(buffer).intValue();
/* 527 */       int length = buffer.getInt();
/* 528 */       if (subsectionTag == 1) {
/* 529 */         data.put(Integer.valueOf(subsectionTag), parseFileAttribute(buffer));
/*     */       }
/* 531 */       buffer.position(pos + length);
/*     */     } 
/* 533 */     return data;
/*     */   }
/*     */   
/*     */   private static Map<ArmAeabiAttributesTag, Object> parseFileAttribute(ByteBuffer bb) {
/* 537 */     Map<ArmAeabiAttributesTag, Object> result = new HashMap<ArmAeabiAttributesTag, Object>();
/* 538 */     while (bb.position() < bb.limit()) {
/* 539 */       int tagValue = readULEB128(bb).intValue();
/* 540 */       ArmAeabiAttributesTag tag = ArmAeabiAttributesTag.getByValue(tagValue);
/* 541 */       switch (tag.getParameterType()) {
/*     */         case UINT32:
/* 543 */           result.put(tag, Integer.valueOf(bb.getInt()));
/*     */         
/*     */         case NTBS:
/* 546 */           result.put(tag, readNTBS(bb, null));
/*     */         
/*     */         case ULEB128:
/* 549 */           result.put(tag, readULEB128(bb));
/*     */       } 
/*     */     
/*     */     } 
/* 553 */     return result;
/*     */   }
/*     */   private static String readNTBS(ByteBuffer buffer, Integer position) {
/*     */     byte currentByte;
/* 557 */     if (position != null) {
/* 558 */       buffer.position(position.intValue());
/*     */     }
/* 560 */     int startingPos = buffer.position();
/*     */     
/*     */     do {
/* 563 */       currentByte = buffer.get();
/* 564 */     } while (currentByte != 0 && buffer.position() <= buffer.limit());
/* 565 */     int terminatingPosition = buffer.position();
/* 566 */     byte[] data = new byte[terminatingPosition - startingPos - 1];
/* 567 */     buffer.position(startingPos);
/* 568 */     buffer.get(data);
/* 569 */     buffer.position(buffer.position() + 1);
/*     */     try {
/* 571 */       return new String(data, "ASCII");
/* 572 */     } catch (UnsupportedEncodingException ex) {
/* 573 */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static BigInteger readULEB128(ByteBuffer buffer) {
/* 578 */     BigInteger result = BigInteger.ZERO;
/* 579 */     int shift = 0;
/*     */     while (true) {
/* 581 */       byte b = buffer.get();
/* 582 */       result = result.or(BigInteger.valueOf((b & Byte.MAX_VALUE)).shiftLeft(shift));
/* 583 */       if ((b & 0x80) == 0) {
/*     */         break;
/*     */       }
/* 586 */       shift += 7;
/*     */     } 
/* 588 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\ELFAnalyser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */