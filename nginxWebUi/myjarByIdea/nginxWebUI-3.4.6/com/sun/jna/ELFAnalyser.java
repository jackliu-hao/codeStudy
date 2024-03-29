package com.sun.jna;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class ELFAnalyser {
   private static final byte[] ELF_MAGIC = new byte[]{127, 69, 76, 70};
   private static final int EF_ARM_ABI_FLOAT_HARD = 1024;
   private static final int EF_ARM_ABI_FLOAT_SOFT = 512;
   private static final int EI_DATA_BIG_ENDIAN = 2;
   private static final int E_MACHINE_ARM = 40;
   private static final int EI_CLASS_64BIT = 2;
   private final String filename;
   private boolean ELF = false;
   private boolean _64Bit = false;
   private boolean bigEndian = false;
   private boolean armHardFloatFlag = false;
   private boolean armSoftFloatFlag = false;
   private boolean armEabiAapcsVfp = false;
   private boolean arm = false;

   public static ELFAnalyser analyse(String filename) throws IOException {
      ELFAnalyser res = new ELFAnalyser(filename);
      res.runDetection();
      return res;
   }

   public boolean isELF() {
      return this.ELF;
   }

   public boolean is64Bit() {
      return this._64Bit;
   }

   public boolean isBigEndian() {
      return this.bigEndian;
   }

   public String getFilename() {
      return this.filename;
   }

   public boolean isArmHardFloat() {
      return this.isArmEabiAapcsVfp() || this.isArmHardFloatFlag();
   }

   public boolean isArmEabiAapcsVfp() {
      return this.armEabiAapcsVfp;
   }

   public boolean isArmHardFloatFlag() {
      return this.armHardFloatFlag;
   }

   public boolean isArmSoftFloatFlag() {
      return this.armSoftFloatFlag;
   }

   public boolean isArm() {
      return this.arm;
   }

   private ELFAnalyser(String filename) {
      this.filename = filename;
   }

   private void runDetection() throws IOException {
      RandomAccessFile raf = new RandomAccessFile(this.filename, "r");

      try {
         if (raf.length() > 4L) {
            byte[] magic = new byte[4];
            raf.seek(0L);
            raf.read(magic);
            if (Arrays.equals(magic, ELF_MAGIC)) {
               this.ELF = true;
            }
         }

         if (this.ELF) {
            raf.seek(4L);
            byte sizeIndicator = raf.readByte();
            byte endianessIndicator = raf.readByte();
            this._64Bit = sizeIndicator == 2;
            this.bigEndian = endianessIndicator == 2;
            raf.seek(0L);
            ByteBuffer headerData = ByteBuffer.allocate(this._64Bit ? 64 : 52);
            raf.getChannel().read(headerData, 0L);
            headerData.order(this.bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
            this.arm = headerData.get(18) == 40;
            if (this.arm) {
               int flags = headerData.getInt(this._64Bit ? 48 : 36);
               this.armHardFloatFlag = (flags & 1024) == 1024;
               this.armSoftFloatFlag = (flags & 512) == 512;
               this.parseEabiAapcsVfp(headerData, raf);
            }

            return;
         }
      } finally {
         try {
            raf.close();
         } catch (IOException var12) {
         }

      }

   }

   private void parseEabiAapcsVfp(ByteBuffer headerData, RandomAccessFile raf) throws IOException {
      ELFSectionHeaders sectionHeaders = new ELFSectionHeaders(this._64Bit, this.bigEndian, headerData, raf);
      Iterator var4 = sectionHeaders.getEntries().iterator();

      while(true) {
         while(true) {
            Map fileAttributes;
            do {
               ELFSectionHeaderEntry eshe;
               do {
                  if (!var4.hasNext()) {
                     return;
                  }

                  eshe = (ELFSectionHeaderEntry)var4.next();
               } while(!".ARM.attributes".equals(eshe.getName()));

               ByteBuffer armAttributesBuffer = ByteBuffer.allocate(eshe.getSize());
               armAttributesBuffer.order(this.bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
               raf.getChannel().read(armAttributesBuffer, (long)eshe.getOffset());
               armAttributesBuffer.rewind();
               Map<Integer, Map<ArmAeabiAttributesTag, Object>> armAttributes = parseArmAttributes(armAttributesBuffer);
               fileAttributes = (Map)armAttributes.get(1);
            } while(fileAttributes == null);

            Object abiVFPargValue = fileAttributes.get(ELFAnalyser.ArmAeabiAttributesTag.ABI_VFP_args);
            if (abiVFPargValue instanceof Integer && ((Integer)abiVFPargValue).equals(1)) {
               this.armEabiAapcsVfp = true;
            } else if (abiVFPargValue instanceof BigInteger && ((BigInteger)abiVFPargValue).intValue() == 1) {
               this.armEabiAapcsVfp = true;
            }
         }
      }
   }

   private static Map<Integer, Map<ArmAeabiAttributesTag, Object>> parseArmAttributes(ByteBuffer bb) {
      byte format = bb.get();
      if (format != 65) {
         return Collections.EMPTY_MAP;
      } else {
         while(bb.position() < bb.limit()) {
            int posSectionStart = bb.position();
            int sectionLength = bb.getInt();
            if (sectionLength <= 0) {
               break;
            }

            String vendorName = readNTBS(bb, (Integer)null);
            if ("aeabi".equals(vendorName)) {
               return parseAEABI(bb);
            }

            bb.position(posSectionStart + sectionLength);
         }

         return Collections.EMPTY_MAP;
      }
   }

   private static Map<Integer, Map<ArmAeabiAttributesTag, Object>> parseAEABI(ByteBuffer buffer) {
      HashMap data;
      int pos;
      int length;
      for(data = new HashMap(); buffer.position() < buffer.limit(); buffer.position(pos + length)) {
         pos = buffer.position();
         int subsectionTag = readULEB128(buffer).intValue();
         length = buffer.getInt();
         if (subsectionTag == 1) {
            data.put(subsectionTag, parseFileAttribute(buffer));
         }
      }

      return data;
   }

   private static Map<ArmAeabiAttributesTag, Object> parseFileAttribute(ByteBuffer bb) {
      Map<ArmAeabiAttributesTag, Object> result = new HashMap();

      while(bb.position() < bb.limit()) {
         int tagValue = readULEB128(bb).intValue();
         ArmAeabiAttributesTag tag = ELFAnalyser.ArmAeabiAttributesTag.getByValue(tagValue);
         switch (tag.getParameterType()) {
            case UINT32:
               result.put(tag, bb.getInt());
               break;
            case NTBS:
               result.put(tag, readNTBS(bb, (Integer)null));
               break;
            case ULEB128:
               result.put(tag, readULEB128(bb));
         }
      }

      return result;
   }

   private static String readNTBS(ByteBuffer buffer, Integer position) {
      if (position != null) {
         buffer.position(position);
      }

      int startingPos = buffer.position();

      byte currentByte;
      do {
         currentByte = buffer.get();
      } while(currentByte != 0 && buffer.position() <= buffer.limit());

      int terminatingPosition = buffer.position();
      byte[] data = new byte[terminatingPosition - startingPos - 1];
      buffer.position(startingPos);
      buffer.get(data);
      buffer.position(buffer.position() + 1);

      try {
         return new String(data, "ASCII");
      } catch (UnsupportedEncodingException var7) {
         throw new RuntimeException(var7);
      }
   }

   private static BigInteger readULEB128(ByteBuffer buffer) {
      BigInteger result = BigInteger.ZERO;
      int shift = 0;

      while(true) {
         byte b = buffer.get();
         result = result.or(BigInteger.valueOf((long)(b & 127)).shiftLeft(shift));
         if ((b & 128) == 0) {
            return result;
         }

         shift += 7;
      }
   }

   static class ArmAeabiAttributesTag {
      private final int value;
      private final String name;
      private final ParameterType parameterType;
      private static final List<ArmAeabiAttributesTag> tags = new LinkedList();
      private static final Map<Integer, ArmAeabiAttributesTag> valueMap = new HashMap();
      private static final Map<String, ArmAeabiAttributesTag> nameMap = new HashMap();
      public static final ArmAeabiAttributesTag File;
      public static final ArmAeabiAttributesTag Section;
      public static final ArmAeabiAttributesTag Symbol;
      public static final ArmAeabiAttributesTag CPU_raw_name;
      public static final ArmAeabiAttributesTag CPU_name;
      public static final ArmAeabiAttributesTag CPU_arch;
      public static final ArmAeabiAttributesTag CPU_arch_profile;
      public static final ArmAeabiAttributesTag ARM_ISA_use;
      public static final ArmAeabiAttributesTag THUMB_ISA_use;
      public static final ArmAeabiAttributesTag FP_arch;
      public static final ArmAeabiAttributesTag WMMX_arch;
      public static final ArmAeabiAttributesTag Advanced_SIMD_arch;
      public static final ArmAeabiAttributesTag PCS_config;
      public static final ArmAeabiAttributesTag ABI_PCS_R9_use;
      public static final ArmAeabiAttributesTag ABI_PCS_RW_data;
      public static final ArmAeabiAttributesTag ABI_PCS_RO_data;
      public static final ArmAeabiAttributesTag ABI_PCS_GOT_use;
      public static final ArmAeabiAttributesTag ABI_PCS_wchar_t;
      public static final ArmAeabiAttributesTag ABI_FP_rounding;
      public static final ArmAeabiAttributesTag ABI_FP_denormal;
      public static final ArmAeabiAttributesTag ABI_FP_exceptions;
      public static final ArmAeabiAttributesTag ABI_FP_user_exceptions;
      public static final ArmAeabiAttributesTag ABI_FP_number_model;
      public static final ArmAeabiAttributesTag ABI_align_needed;
      public static final ArmAeabiAttributesTag ABI_align8_preserved;
      public static final ArmAeabiAttributesTag ABI_enum_size;
      public static final ArmAeabiAttributesTag ABI_HardFP_use;
      public static final ArmAeabiAttributesTag ABI_VFP_args;
      public static final ArmAeabiAttributesTag ABI_WMMX_args;
      public static final ArmAeabiAttributesTag ABI_optimization_goals;
      public static final ArmAeabiAttributesTag ABI_FP_optimization_goals;
      public static final ArmAeabiAttributesTag compatibility;
      public static final ArmAeabiAttributesTag CPU_unaligned_access;
      public static final ArmAeabiAttributesTag FP_HP_extension;
      public static final ArmAeabiAttributesTag ABI_FP_16bit_format;
      public static final ArmAeabiAttributesTag MPextension_use;
      public static final ArmAeabiAttributesTag DIV_use;
      public static final ArmAeabiAttributesTag nodefaults;
      public static final ArmAeabiAttributesTag also_compatible_with;
      public static final ArmAeabiAttributesTag conformance;
      public static final ArmAeabiAttributesTag T2EE_use;
      public static final ArmAeabiAttributesTag Virtualization_use;
      public static final ArmAeabiAttributesTag MPextension_use2;

      public ArmAeabiAttributesTag(int value, String name, ParameterType parameterType) {
         this.value = value;
         this.name = name;
         this.parameterType = parameterType;
      }

      public int getValue() {
         return this.value;
      }

      public String getName() {
         return this.name;
      }

      public ParameterType getParameterType() {
         return this.parameterType;
      }

      public String toString() {
         return this.name + " (" + this.value + ")";
      }

      public int hashCode() {
         int hash = 7;
         hash = 67 * hash + this.value;
         return hash;
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            ArmAeabiAttributesTag other = (ArmAeabiAttributesTag)obj;
            return this.value == other.value;
         }
      }

      private static ArmAeabiAttributesTag addTag(int value, String name, ParameterType type) {
         ArmAeabiAttributesTag tag = new ArmAeabiAttributesTag(value, name, type);
         if (!valueMap.containsKey(tag.getValue())) {
            valueMap.put(tag.getValue(), tag);
         }

         if (!nameMap.containsKey(tag.getName())) {
            nameMap.put(tag.getName(), tag);
         }

         tags.add(tag);
         return tag;
      }

      public static List<ArmAeabiAttributesTag> getTags() {
         return Collections.unmodifiableList(tags);
      }

      public static ArmAeabiAttributesTag getByName(String name) {
         return (ArmAeabiAttributesTag)nameMap.get(name);
      }

      public static ArmAeabiAttributesTag getByValue(int value) {
         if (valueMap.containsKey(value)) {
            return (ArmAeabiAttributesTag)valueMap.get(value);
         } else {
            ArmAeabiAttributesTag pseudoTag = new ArmAeabiAttributesTag(value, "Unknown " + value, getParameterType(value));
            return pseudoTag;
         }
      }

      private static ParameterType getParameterType(int value) {
         ArmAeabiAttributesTag tag = getByValue(value);
         if (tag == null) {
            return value % 2 == 0 ? ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128 : ELFAnalyser.ArmAeabiAttributesTag.ParameterType.NTBS;
         } else {
            return tag.getParameterType();
         }
      }

      static {
         File = addTag(1, "File", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.UINT32);
         Section = addTag(2, "Section", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.UINT32);
         Symbol = addTag(3, "Symbol", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.UINT32);
         CPU_raw_name = addTag(4, "CPU_raw_name", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.NTBS);
         CPU_name = addTag(5, "CPU_name", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.NTBS);
         CPU_arch = addTag(6, "CPU_arch", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         CPU_arch_profile = addTag(7, "CPU_arch_profile", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         ARM_ISA_use = addTag(8, "ARM_ISA_use", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         THUMB_ISA_use = addTag(9, "THUMB_ISA_use", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         FP_arch = addTag(10, "FP_arch", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         WMMX_arch = addTag(11, "WMMX_arch", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         Advanced_SIMD_arch = addTag(12, "Advanced_SIMD_arch", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         PCS_config = addTag(13, "PCS_config", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         ABI_PCS_R9_use = addTag(14, "ABI_PCS_R9_use", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         ABI_PCS_RW_data = addTag(15, "ABI_PCS_RW_data", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         ABI_PCS_RO_data = addTag(16, "ABI_PCS_RO_data", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         ABI_PCS_GOT_use = addTag(17, "ABI_PCS_GOT_use", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         ABI_PCS_wchar_t = addTag(18, "ABI_PCS_wchar_t", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         ABI_FP_rounding = addTag(19, "ABI_FP_rounding", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         ABI_FP_denormal = addTag(20, "ABI_FP_denormal", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         ABI_FP_exceptions = addTag(21, "ABI_FP_exceptions", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         ABI_FP_user_exceptions = addTag(22, "ABI_FP_user_exceptions", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         ABI_FP_number_model = addTag(23, "ABI_FP_number_model", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         ABI_align_needed = addTag(24, "ABI_align_needed", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         ABI_align8_preserved = addTag(25, "ABI_align8_preserved", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         ABI_enum_size = addTag(26, "ABI_enum_size", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         ABI_HardFP_use = addTag(27, "ABI_HardFP_use", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         ABI_VFP_args = addTag(28, "ABI_VFP_args", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         ABI_WMMX_args = addTag(29, "ABI_WMMX_args", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         ABI_optimization_goals = addTag(30, "ABI_optimization_goals", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         ABI_FP_optimization_goals = addTag(31, "ABI_FP_optimization_goals", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         compatibility = addTag(32, "compatibility", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.NTBS);
         CPU_unaligned_access = addTag(34, "CPU_unaligned_access", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         FP_HP_extension = addTag(36, "FP_HP_extension", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         ABI_FP_16bit_format = addTag(38, "ABI_FP_16bit_format", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         MPextension_use = addTag(42, "MPextension_use", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         DIV_use = addTag(44, "DIV_use", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         nodefaults = addTag(64, "nodefaults", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         also_compatible_with = addTag(65, "also_compatible_with", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.NTBS);
         conformance = addTag(67, "conformance", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.NTBS);
         T2EE_use = addTag(66, "T2EE_use", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         Virtualization_use = addTag(68, "Virtualization_use", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
         MPextension_use2 = addTag(70, "MPextension_use", ELFAnalyser.ArmAeabiAttributesTag.ParameterType.ULEB128);
      }

      public static enum ParameterType {
         UINT32,
         NTBS,
         ULEB128;
      }
   }

   static class ELFSectionHeaderEntry {
      private final int nameOffset;
      private String name;
      private final int type;
      private final int flags;
      private final int offset;
      private final int size;

      public ELFSectionHeaderEntry(boolean _64bit, ByteBuffer sectionHeaderData) {
         this.nameOffset = sectionHeaderData.getInt(0);
         this.type = sectionHeaderData.getInt(4);
         this.flags = (int)(_64bit ? sectionHeaderData.getLong(8) : (long)sectionHeaderData.getInt(8));
         this.offset = (int)(_64bit ? sectionHeaderData.getLong(24) : (long)sectionHeaderData.getInt(16));
         this.size = (int)(_64bit ? sectionHeaderData.getLong(32) : (long)sectionHeaderData.getInt(20));
      }

      public String getName() {
         return this.name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public int getNameOffset() {
         return this.nameOffset;
      }

      public int getType() {
         return this.type;
      }

      public int getFlags() {
         return this.flags;
      }

      public int getOffset() {
         return this.offset;
      }

      public int getSize() {
         return this.size;
      }

      public String toString() {
         return "ELFSectionHeaderEntry{nameIdx=" + this.nameOffset + ", name=" + this.name + ", type=" + this.type + ", flags=" + this.flags + ", offset=" + this.offset + ", size=" + this.size + '}';
      }
   }

   static class ELFSectionHeaders {
      private final List<ELFSectionHeaderEntry> entries = new ArrayList();

      public ELFSectionHeaders(boolean _64bit, boolean bigEndian, ByteBuffer headerData, RandomAccessFile raf) throws IOException {
         long shoff;
         short shentsize;
         short shnum;
         short shstrndx;
         if (_64bit) {
            shoff = headerData.getLong(40);
            shentsize = headerData.getShort(58);
            shnum = headerData.getShort(60);
            shstrndx = headerData.getShort(62);
         } else {
            shoff = (long)headerData.getInt(32);
            shentsize = headerData.getShort(46);
            shnum = headerData.getShort(48);
            shstrndx = headerData.getShort(50);
         }

         int tableLength = shnum * shentsize;
         ByteBuffer data = ByteBuffer.allocate(tableLength);
         data.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
         raf.getChannel().read(data, shoff);

         ByteBuffer stringBuffer;
         for(int i = 0; i < shnum; ++i) {
            data.position(i * shentsize);
            stringBuffer = data.slice();
            stringBuffer.order(data.order());
            stringBuffer.limit(shentsize);
            this.entries.add(new ELFSectionHeaderEntry(_64bit, stringBuffer));
         }

         ELFSectionHeaderEntry stringTable = (ELFSectionHeaderEntry)this.entries.get(shstrndx);
         stringBuffer = ByteBuffer.allocate(stringTable.getSize());
         stringBuffer.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
         raf.getChannel().read(stringBuffer, (long)stringTable.getOffset());
         stringBuffer.rewind();
         ByteArrayOutputStream baos = new ByteArrayOutputStream(20);

         ELFSectionHeaderEntry eshe;
         for(Iterator var15 = this.entries.iterator(); var15.hasNext(); eshe.setName(baos.toString("ASCII"))) {
            eshe = (ELFSectionHeaderEntry)var15.next();
            baos.reset();
            stringBuffer.position(eshe.getNameOffset());

            while(stringBuffer.position() < stringBuffer.limit()) {
               byte b = stringBuffer.get();
               if (b == 0) {
                  break;
               }

               baos.write(b);
            }
         }

      }

      public List<ELFSectionHeaderEntry> getEntries() {
         return this.entries;
      }
   }
}
