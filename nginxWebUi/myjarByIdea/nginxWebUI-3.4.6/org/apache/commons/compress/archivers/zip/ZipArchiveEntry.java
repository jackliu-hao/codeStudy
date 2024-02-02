package org.apache.commons.compress.archivers.zip;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.EntryStreamOffsets;
import org.apache.commons.compress.utils.ByteUtils;

public class ZipArchiveEntry extends ZipEntry implements ArchiveEntry, EntryStreamOffsets {
   public static final int PLATFORM_UNIX = 3;
   public static final int PLATFORM_FAT = 0;
   public static final int CRC_UNKNOWN = -1;
   private static final int SHORT_MASK = 65535;
   private static final int SHORT_SHIFT = 16;
   private int method;
   private long size;
   private int internalAttributes;
   private int versionRequired;
   private int versionMadeBy;
   private int platform;
   private int rawFlag;
   private long externalAttributes;
   private int alignment;
   private ZipExtraField[] extraFields;
   private UnparseableExtraFieldData unparseableExtra;
   private String name;
   private byte[] rawName;
   private GeneralPurposeBit gpb;
   private long localHeaderOffset;
   private long dataOffset;
   private boolean isStreamContiguous;
   private NameSource nameSource;
   private CommentSource commentSource;
   private long diskNumberStart;
   static final ZipArchiveEntry[] EMPTY_ZIP_ARCHIVE_ENTRY_ARRAY = new ZipArchiveEntry[0];

   public ZipArchiveEntry(String name) {
      super(name);
      this.method = -1;
      this.size = -1L;
      this.platform = 0;
      this.gpb = new GeneralPurposeBit();
      this.localHeaderOffset = -1L;
      this.dataOffset = -1L;
      this.nameSource = ZipArchiveEntry.NameSource.NAME;
      this.commentSource = ZipArchiveEntry.CommentSource.COMMENT;
      this.setName(name);
   }

   public ZipArchiveEntry(ZipEntry entry) throws ZipException {
      super(entry);
      this.method = -1;
      this.size = -1L;
      this.platform = 0;
      this.gpb = new GeneralPurposeBit();
      this.localHeaderOffset = -1L;
      this.dataOffset = -1L;
      this.nameSource = ZipArchiveEntry.NameSource.NAME;
      this.commentSource = ZipArchiveEntry.CommentSource.COMMENT;
      this.setName(entry.getName());
      byte[] extra = entry.getExtra();
      if (extra != null) {
         this.setExtraFields(ExtraFieldUtils.parse(extra, true, (ExtraFieldParsingBehavior)ZipArchiveEntry.ExtraFieldParsingMode.BEST_EFFORT));
      } else {
         this.setExtra();
      }

      this.setMethod(entry.getMethod());
      this.size = entry.getSize();
   }

   public ZipArchiveEntry(ZipArchiveEntry entry) throws ZipException {
      this((ZipEntry)entry);
      this.setInternalAttributes(entry.getInternalAttributes());
      this.setExternalAttributes(entry.getExternalAttributes());
      this.setExtraFields(this.getAllExtraFieldsNoCopy());
      this.setPlatform(entry.getPlatform());
      GeneralPurposeBit other = entry.getGeneralPurposeBit();
      this.setGeneralPurposeBit(other == null ? null : (GeneralPurposeBit)other.clone());
   }

   protected ZipArchiveEntry() {
      this("");
   }

   public ZipArchiveEntry(File inputFile, String entryName) {
      this(inputFile.isDirectory() && !entryName.endsWith("/") ? entryName + "/" : entryName);
      if (inputFile.isFile()) {
         this.setSize(inputFile.length());
      }

      this.setTime(inputFile.lastModified());
   }

   public ZipArchiveEntry(Path inputPath, String entryName, LinkOption... options) throws IOException {
      this(Files.isDirectory(inputPath, options) && !entryName.endsWith("/") ? entryName + "/" : entryName);
      if (Files.isRegularFile(inputPath, options)) {
         this.setSize(Files.size(inputPath));
      }

      this.setTime(Files.getLastModifiedTime(inputPath, options));
   }

   public void setTime(FileTime fileTime) {
      this.setTime(fileTime.toMillis());
   }

   public Object clone() {
      ZipArchiveEntry e = (ZipArchiveEntry)super.clone();
      e.setInternalAttributes(this.getInternalAttributes());
      e.setExternalAttributes(this.getExternalAttributes());
      e.setExtraFields(this.getAllExtraFieldsNoCopy());
      return e;
   }

   public int getMethod() {
      return this.method;
   }

   public void setMethod(int method) {
      if (method < 0) {
         throw new IllegalArgumentException("ZIP compression method can not be negative: " + method);
      } else {
         this.method = method;
      }
   }

   public int getInternalAttributes() {
      return this.internalAttributes;
   }

   public void setInternalAttributes(int value) {
      this.internalAttributes = value;
   }

   public long getExternalAttributes() {
      return this.externalAttributes;
   }

   public void setExternalAttributes(long value) {
      this.externalAttributes = value;
   }

   public void setUnixMode(int mode) {
      this.setExternalAttributes((long)(mode << 16 | ((mode & 128) == 0 ? 1 : 0) | (this.isDirectory() ? 16 : 0)));
      this.platform = 3;
   }

   public int getUnixMode() {
      return this.platform != 3 ? 0 : (int)(this.getExternalAttributes() >> 16 & 65535L);
   }

   public boolean isUnixSymlink() {
      return (this.getUnixMode() & '\uf000') == 40960;
   }

   public int getPlatform() {
      return this.platform;
   }

   protected void setPlatform(int platform) {
      this.platform = platform;
   }

   protected int getAlignment() {
      return this.alignment;
   }

   public void setAlignment(int alignment) {
      if ((alignment & alignment - 1) == 0 && alignment <= 65535) {
         this.alignment = alignment;
      } else {
         throw new IllegalArgumentException("Invalid value for alignment, must be power of two and no bigger than 65535 but is " + alignment);
      }
   }

   public void setExtraFields(ZipExtraField[] fields) {
      this.unparseableExtra = null;
      List<ZipExtraField> newFields = new ArrayList();
      if (fields != null) {
         ZipExtraField[] var3 = fields;
         int var4 = fields.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            ZipExtraField field = var3[var5];
            if (field instanceof UnparseableExtraFieldData) {
               this.unparseableExtra = (UnparseableExtraFieldData)field;
            } else {
               newFields.add(field);
            }
         }
      }

      this.extraFields = (ZipExtraField[])newFields.toArray(ExtraFieldUtils.EMPTY_ZIP_EXTRA_FIELD_ARRAY);
      this.setExtra();
   }

   public ZipExtraField[] getExtraFields() {
      return this.getParseableExtraFields();
   }

   public ZipExtraField[] getExtraFields(boolean includeUnparseable) {
      return includeUnparseable ? this.getAllExtraFields() : this.getParseableExtraFields();
   }

   public ZipExtraField[] getExtraFields(ExtraFieldParsingBehavior parsingBehavior) throws ZipException {
      if (parsingBehavior == ZipArchiveEntry.ExtraFieldParsingMode.BEST_EFFORT) {
         return this.getExtraFields(true);
      } else if (parsingBehavior == ZipArchiveEntry.ExtraFieldParsingMode.ONLY_PARSEABLE_LENIENT) {
         return this.getExtraFields(false);
      } else {
         byte[] local = this.getExtra();
         List<ZipExtraField> localFields = new ArrayList(Arrays.asList(ExtraFieldUtils.parse(local, true, parsingBehavior)));
         byte[] central = this.getCentralDirectoryExtra();
         List<ZipExtraField> centralFields = new ArrayList(Arrays.asList(ExtraFieldUtils.parse(central, false, parsingBehavior)));
         List<ZipExtraField> merged = new ArrayList();

         ZipExtraField l;
         for(Iterator var7 = localFields.iterator(); var7.hasNext(); merged.add(l)) {
            l = (ZipExtraField)var7.next();
            ZipExtraField c = null;
            if (l instanceof UnparseableExtraFieldData) {
               c = this.findUnparseable(centralFields);
            } else {
               c = this.findMatching(l.getHeaderId(), centralFields);
            }

            if (c != null) {
               byte[] cd = c.getCentralDirectoryData();
               if (cd != null && cd.length > 0) {
                  l.parseFromCentralDirectoryData(cd, 0, cd.length);
               }

               centralFields.remove(c);
            }
         }

         merged.addAll(centralFields);
         return (ZipExtraField[])merged.toArray(ExtraFieldUtils.EMPTY_ZIP_EXTRA_FIELD_ARRAY);
      }
   }

   private ZipExtraField[] getParseableExtraFieldsNoCopy() {
      return this.extraFields == null ? ExtraFieldUtils.EMPTY_ZIP_EXTRA_FIELD_ARRAY : this.extraFields;
   }

   private ZipExtraField[] getParseableExtraFields() {
      ZipExtraField[] parseableExtraFields = this.getParseableExtraFieldsNoCopy();
      return parseableExtraFields == this.extraFields ? this.copyOf(parseableExtraFields, parseableExtraFields.length) : parseableExtraFields;
   }

   private ZipExtraField[] getAllExtraFieldsNoCopy() {
      if (this.extraFields == null) {
         return this.getUnparseableOnly();
      } else {
         return this.unparseableExtra != null ? this.getMergedFields() : this.extraFields;
      }
   }

   private ZipExtraField[] getMergedFields() {
      ZipExtraField[] zipExtraFields = this.copyOf(this.extraFields, this.extraFields.length + 1);
      zipExtraFields[this.extraFields.length] = this.unparseableExtra;
      return zipExtraFields;
   }

   private ZipExtraField[] getUnparseableOnly() {
      return this.unparseableExtra == null ? ExtraFieldUtils.EMPTY_ZIP_EXTRA_FIELD_ARRAY : new ZipExtraField[]{this.unparseableExtra};
   }

   private ZipExtraField[] getAllExtraFields() {
      ZipExtraField[] allExtraFieldsNoCopy = this.getAllExtraFieldsNoCopy();
      return allExtraFieldsNoCopy == this.extraFields ? this.copyOf(allExtraFieldsNoCopy, allExtraFieldsNoCopy.length) : allExtraFieldsNoCopy;
   }

   private ZipExtraField findUnparseable(List<ZipExtraField> fs) {
      Iterator var2 = fs.iterator();

      ZipExtraField f;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         f = (ZipExtraField)var2.next();
      } while(!(f instanceof UnparseableExtraFieldData));

      return f;
   }

   private ZipExtraField findMatching(ZipShort headerId, List<ZipExtraField> fs) {
      Iterator var3 = fs.iterator();

      ZipExtraField f;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         f = (ZipExtraField)var3.next();
      } while(!headerId.equals(f.getHeaderId()));

      return f;
   }

   public void addExtraField(ZipExtraField ze) {
      if (ze instanceof UnparseableExtraFieldData) {
         this.unparseableExtra = (UnparseableExtraFieldData)ze;
      } else if (this.extraFields == null) {
         this.extraFields = new ZipExtraField[]{ze};
      } else {
         if (this.getExtraField(ze.getHeaderId()) != null) {
            this.removeExtraField(ze.getHeaderId());
         }

         ZipExtraField[] zipExtraFields = this.copyOf(this.extraFields, this.extraFields.length + 1);
         zipExtraFields[zipExtraFields.length - 1] = ze;
         this.extraFields = zipExtraFields;
      }

      this.setExtra();
   }

   public void addAsFirstExtraField(ZipExtraField ze) {
      if (ze instanceof UnparseableExtraFieldData) {
         this.unparseableExtra = (UnparseableExtraFieldData)ze;
      } else {
         if (this.getExtraField(ze.getHeaderId()) != null) {
            this.removeExtraField(ze.getHeaderId());
         }

         ZipExtraField[] copy = this.extraFields;
         int newLen = this.extraFields != null ? this.extraFields.length + 1 : 1;
         this.extraFields = new ZipExtraField[newLen];
         this.extraFields[0] = ze;
         if (copy != null) {
            System.arraycopy(copy, 0, this.extraFields, 1, this.extraFields.length - 1);
         }
      }

      this.setExtra();
   }

   public void removeExtraField(ZipShort type) {
      if (this.extraFields == null) {
         throw new NoSuchElementException();
      } else {
         List<ZipExtraField> newResult = new ArrayList();
         ZipExtraField[] var3 = this.extraFields;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            ZipExtraField extraField = var3[var5];
            if (!type.equals(extraField.getHeaderId())) {
               newResult.add(extraField);
            }
         }

         if (this.extraFields.length == newResult.size()) {
            throw new NoSuchElementException();
         } else {
            this.extraFields = (ZipExtraField[])newResult.toArray(ExtraFieldUtils.EMPTY_ZIP_EXTRA_FIELD_ARRAY);
            this.setExtra();
         }
      }
   }

   public void removeUnparseableExtraFieldData() {
      if (this.unparseableExtra == null) {
         throw new NoSuchElementException();
      } else {
         this.unparseableExtra = null;
         this.setExtra();
      }
   }

   public ZipExtraField getExtraField(ZipShort type) {
      if (this.extraFields != null) {
         ZipExtraField[] var2 = this.extraFields;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            ZipExtraField extraField = var2[var4];
            if (type.equals(extraField.getHeaderId())) {
               return extraField;
            }
         }
      }

      return null;
   }

   public UnparseableExtraFieldData getUnparseableExtraFieldData() {
      return this.unparseableExtra;
   }

   public void setExtra(byte[] extra) throws RuntimeException {
      try {
         ZipExtraField[] local = ExtraFieldUtils.parse(extra, true, (ExtraFieldParsingBehavior)ZipArchiveEntry.ExtraFieldParsingMode.BEST_EFFORT);
         this.mergeExtraFields(local, true);
      } catch (ZipException var3) {
         throw new RuntimeException("Error parsing extra fields for entry: " + this.getName() + " - " + var3.getMessage(), var3);
      }
   }

   protected void setExtra() {
      super.setExtra(ExtraFieldUtils.mergeLocalFileDataData(this.getAllExtraFieldsNoCopy()));
   }

   public void setCentralDirectoryExtra(byte[] b) {
      try {
         ZipExtraField[] central = ExtraFieldUtils.parse(b, false, (ExtraFieldParsingBehavior)ZipArchiveEntry.ExtraFieldParsingMode.BEST_EFFORT);
         this.mergeExtraFields(central, false);
      } catch (ZipException var3) {
         throw new RuntimeException(var3.getMessage(), var3);
      }
   }

   public byte[] getLocalFileDataExtra() {
      byte[] extra = this.getExtra();
      return extra != null ? extra : ByteUtils.EMPTY_BYTE_ARRAY;
   }

   public byte[] getCentralDirectoryExtra() {
      return ExtraFieldUtils.mergeCentralDirectoryData(this.getAllExtraFieldsNoCopy());
   }

   public String getName() {
      return this.name == null ? super.getName() : this.name;
   }

   public boolean isDirectory() {
      String n = this.getName();
      return n != null && n.endsWith("/");
   }

   protected void setName(String name) {
      if (name != null && this.getPlatform() == 0 && !name.contains("/")) {
         name = name.replace('\\', '/');
      }

      this.name = name;
   }

   public long getSize() {
      return this.size;
   }

   public void setSize(long size) {
      if (size < 0L) {
         throw new IllegalArgumentException("Invalid entry size");
      } else {
         this.size = size;
      }
   }

   protected void setName(String name, byte[] rawName) {
      this.setName(name);
      this.rawName = rawName;
   }

   public byte[] getRawName() {
      return this.rawName != null ? Arrays.copyOf(this.rawName, this.rawName.length) : null;
   }

   protected long getLocalHeaderOffset() {
      return this.localHeaderOffset;
   }

   protected void setLocalHeaderOffset(long localHeaderOffset) {
      this.localHeaderOffset = localHeaderOffset;
   }

   public long getDataOffset() {
      return this.dataOffset;
   }

   protected void setDataOffset(long dataOffset) {
      this.dataOffset = dataOffset;
   }

   public boolean isStreamContiguous() {
      return this.isStreamContiguous;
   }

   protected void setStreamContiguous(boolean isStreamContiguous) {
      this.isStreamContiguous = isStreamContiguous;
   }

   public int hashCode() {
      String n = this.getName();
      return (n == null ? "" : n).hashCode();
   }

   public GeneralPurposeBit getGeneralPurposeBit() {
      return this.gpb;
   }

   public void setGeneralPurposeBit(GeneralPurposeBit b) {
      this.gpb = b;
   }

   private void mergeExtraFields(ZipExtraField[] f, boolean local) {
      if (this.extraFields == null) {
         this.setExtraFields(f);
      } else {
         ZipExtraField[] var3 = f;
         int var4 = f.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            ZipExtraField element = var3[var5];
            Object existing;
            if (element instanceof UnparseableExtraFieldData) {
               existing = this.unparseableExtra;
            } else {
               existing = this.getExtraField(element.getHeaderId());
            }

            if (existing == null) {
               this.addExtraField(element);
            } else {
               byte[] b = local ? element.getLocalFileDataData() : element.getCentralDirectoryData();

               try {
                  if (local) {
                     ((ZipExtraField)existing).parseFromLocalFileData(b, 0, b.length);
                  } else {
                     ((ZipExtraField)existing).parseFromCentralDirectoryData(b, 0, b.length);
                  }
               } catch (ZipException var11) {
                  UnrecognizedExtraField u = new UnrecognizedExtraField();
                  u.setHeaderId(((ZipExtraField)existing).getHeaderId());
                  if (local) {
                     u.setLocalFileDataData(b);
                     u.setCentralDirectoryData(((ZipExtraField)existing).getCentralDirectoryData());
                  } else {
                     u.setLocalFileDataData(((ZipExtraField)existing).getLocalFileDataData());
                     u.setCentralDirectoryData(b);
                  }

                  this.removeExtraField(((ZipExtraField)existing).getHeaderId());
                  this.addExtraField(u);
               }
            }
         }

         this.setExtra();
      }

   }

   public Date getLastModifiedDate() {
      return new Date(this.getTime());
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         ZipArchiveEntry other = (ZipArchiveEntry)obj;
         String myName = this.getName();
         String otherName = other.getName();
         if (!Objects.equals(myName, otherName)) {
            return false;
         } else {
            String myComment = this.getComment();
            String otherComment = other.getComment();
            if (myComment == null) {
               myComment = "";
            }

            if (otherComment == null) {
               otherComment = "";
            }

            return this.getTime() == other.getTime() && myComment.equals(otherComment) && this.getInternalAttributes() == other.getInternalAttributes() && this.getPlatform() == other.getPlatform() && this.getExternalAttributes() == other.getExternalAttributes() && this.getMethod() == other.getMethod() && this.getSize() == other.getSize() && this.getCrc() == other.getCrc() && this.getCompressedSize() == other.getCompressedSize() && Arrays.equals(this.getCentralDirectoryExtra(), other.getCentralDirectoryExtra()) && Arrays.equals(this.getLocalFileDataExtra(), other.getLocalFileDataExtra()) && this.localHeaderOffset == other.localHeaderOffset && this.dataOffset == other.dataOffset && this.gpb.equals(other.gpb);
         }
      } else {
         return false;
      }
   }

   public void setVersionMadeBy(int versionMadeBy) {
      this.versionMadeBy = versionMadeBy;
   }

   public void setVersionRequired(int versionRequired) {
      this.versionRequired = versionRequired;
   }

   public int getVersionRequired() {
      return this.versionRequired;
   }

   public int getVersionMadeBy() {
      return this.versionMadeBy;
   }

   public int getRawFlag() {
      return this.rawFlag;
   }

   public void setRawFlag(int rawFlag) {
      this.rawFlag = rawFlag;
   }

   public NameSource getNameSource() {
      return this.nameSource;
   }

   public void setNameSource(NameSource nameSource) {
      this.nameSource = nameSource;
   }

   public CommentSource getCommentSource() {
      return this.commentSource;
   }

   public void setCommentSource(CommentSource commentSource) {
      this.commentSource = commentSource;
   }

   public long getDiskNumberStart() {
      return this.diskNumberStart;
   }

   public void setDiskNumberStart(long diskNumberStart) {
      this.diskNumberStart = diskNumberStart;
   }

   private ZipExtraField[] copyOf(ZipExtraField[] src, int length) {
      ZipExtraField[] cpy = new ZipExtraField[length];
      System.arraycopy(src, 0, cpy, 0, Math.min(src.length, length));
      return cpy;
   }

   public static enum ExtraFieldParsingMode implements ExtraFieldParsingBehavior {
      BEST_EFFORT(ExtraFieldUtils.UnparseableExtraField.READ) {
         public ZipExtraField fill(ZipExtraField field, byte[] data, int off, int len, boolean local) {
            return ZipArchiveEntry.ExtraFieldParsingMode.fillAndMakeUnrecognizedOnError(field, data, off, len, local);
         }
      },
      STRICT_FOR_KNOW_EXTRA_FIELDS(ExtraFieldUtils.UnparseableExtraField.READ),
      ONLY_PARSEABLE_LENIENT(ExtraFieldUtils.UnparseableExtraField.SKIP) {
         public ZipExtraField fill(ZipExtraField field, byte[] data, int off, int len, boolean local) {
            return ZipArchiveEntry.ExtraFieldParsingMode.fillAndMakeUnrecognizedOnError(field, data, off, len, local);
         }
      },
      ONLY_PARSEABLE_STRICT(ExtraFieldUtils.UnparseableExtraField.SKIP),
      DRACONIC(ExtraFieldUtils.UnparseableExtraField.THROW);

      private final ExtraFieldUtils.UnparseableExtraField onUnparseableData;

      private ExtraFieldParsingMode(ExtraFieldUtils.UnparseableExtraField onUnparseableData) {
         this.onUnparseableData = onUnparseableData;
      }

      public ZipExtraField onUnparseableExtraField(byte[] data, int off, int len, boolean local, int claimedLength) throws ZipException {
         return this.onUnparseableData.onUnparseableExtraField(data, off, len, local, claimedLength);
      }

      public ZipExtraField createExtraField(ZipShort headerId) throws ZipException, InstantiationException, IllegalAccessException {
         return ExtraFieldUtils.createExtraField(headerId);
      }

      public ZipExtraField fill(ZipExtraField field, byte[] data, int off, int len, boolean local) throws ZipException {
         return ExtraFieldUtils.fillExtraField(field, data, off, len, local);
      }

      private static ZipExtraField fillAndMakeUnrecognizedOnError(ZipExtraField field, byte[] data, int off, int len, boolean local) {
         try {
            return ExtraFieldUtils.fillExtraField(field, data, off, len, local);
         } catch (ZipException var7) {
            UnrecognizedExtraField u = new UnrecognizedExtraField();
            u.setHeaderId(field.getHeaderId());
            if (local) {
               u.setLocalFileDataData(Arrays.copyOfRange(data, off, off + len));
            } else {
               u.setCentralDirectoryData(Arrays.copyOfRange(data, off, off + len));
            }

            return u;
         }
      }

      // $FF: synthetic method
      ExtraFieldParsingMode(ExtraFieldUtils.UnparseableExtraField x2, Object x3) {
         this(x2);
      }
   }

   public static enum CommentSource {
      COMMENT,
      UNICODE_EXTRA_FIELD;
   }

   public static enum NameSource {
      NAME,
      NAME_WITH_EFS_FLAG,
      UNICODE_EXTRA_FIELD;
   }
}
