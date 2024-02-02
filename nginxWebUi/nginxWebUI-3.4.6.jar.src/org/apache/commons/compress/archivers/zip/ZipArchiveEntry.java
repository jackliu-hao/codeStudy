/*      */ package org.apache.commons.compress.archivers.zip;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.LinkOption;
/*      */ import java.nio.file.Path;
/*      */ import java.nio.file.attribute.FileTime;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Date;
/*      */ import java.util.List;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Objects;
/*      */ import java.util.zip.ZipEntry;
/*      */ import java.util.zip.ZipException;
/*      */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*      */ import org.apache.commons.compress.archivers.EntryStreamOffsets;
/*      */ import org.apache.commons.compress.utils.ByteUtils;
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
/*      */ public class ZipArchiveEntry
/*      */   extends ZipEntry
/*      */   implements ArchiveEntry, EntryStreamOffsets
/*      */ {
/*      */   public static final int PLATFORM_UNIX = 3;
/*      */   public static final int PLATFORM_FAT = 0;
/*      */   public static final int CRC_UNKNOWN = -1;
/*      */   private static final int SHORT_MASK = 65535;
/*      */   private static final int SHORT_SHIFT = 16;
/*      */   
/*      */   public enum NameSource
/*      */   {
/*   78 */     NAME,
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*   84 */     NAME_WITH_EFS_FLAG,
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*   89 */     UNICODE_EXTRA_FIELD;
/*      */   }
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
/*      */   public enum CommentSource
/*      */   {
/*  103 */     COMMENT,
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  108 */     UNICODE_EXTRA_FIELD;
/*      */   }
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
/*  121 */   private int method = -1;
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
/*  132 */   private long size = -1L;
/*      */   
/*      */   private int internalAttributes;
/*      */   private int versionRequired;
/*      */   private int versionMadeBy;
/*  137 */   private int platform = 0;
/*      */   private int rawFlag;
/*      */   private long externalAttributes;
/*      */   private int alignment;
/*      */   private ZipExtraField[] extraFields;
/*      */   private UnparseableExtraFieldData unparseableExtra;
/*      */   private String name;
/*      */   private byte[] rawName;
/*  145 */   private GeneralPurposeBit gpb = new GeneralPurposeBit();
/*  146 */   private long localHeaderOffset = -1L;
/*  147 */   private long dataOffset = -1L;
/*      */   private boolean isStreamContiguous;
/*  149 */   private NameSource nameSource = NameSource.NAME;
/*  150 */   private CommentSource commentSource = CommentSource.COMMENT;
/*      */   private long diskNumberStart;
/*  152 */   static final ZipArchiveEntry[] EMPTY_ZIP_ARCHIVE_ENTRY_ARRAY = new ZipArchiveEntry[0];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ZipArchiveEntry(String name) {
/*  163 */     super(name);
/*  164 */     setName(name);
/*      */   }
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
/*      */   public ZipArchiveEntry(ZipEntry entry) throws ZipException {
/*  177 */     super(entry);
/*  178 */     setName(entry.getName());
/*  179 */     byte[] extra = entry.getExtra();
/*  180 */     if (extra != null) {
/*  181 */       setExtraFields(ExtraFieldUtils.parse(extra, true, ExtraFieldParsingMode.BEST_EFFORT));
/*      */     } else {
/*  183 */       setExtra();
/*      */     } 
/*  185 */     setMethod(entry.getMethod());
/*  186 */     this.size = entry.getSize();
/*      */   }
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
/*      */   public ZipArchiveEntry(ZipArchiveEntry entry) throws ZipException {
/*  199 */     this(entry);
/*  200 */     setInternalAttributes(entry.getInternalAttributes());
/*  201 */     setExternalAttributes(entry.getExternalAttributes());
/*  202 */     setExtraFields(getAllExtraFieldsNoCopy());
/*  203 */     setPlatform(entry.getPlatform());
/*  204 */     GeneralPurposeBit other = entry.getGeneralPurposeBit();
/*  205 */     setGeneralPurposeBit((other == null) ? null : (GeneralPurposeBit)other
/*  206 */         .clone());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected ZipArchiveEntry() {
/*  212 */     this("");
/*      */   }
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
/*      */   public ZipArchiveEntry(File inputFile, String entryName) {
/*  227 */     this((inputFile.isDirectory() && !entryName.endsWith("/")) ? (entryName + "/") : entryName);
/*      */     
/*  229 */     if (inputFile.isFile()) {
/*  230 */       setSize(inputFile.length());
/*      */     }
/*  232 */     setTime(inputFile.lastModified());
/*      */   }
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
/*      */   public ZipArchiveEntry(Path inputPath, String entryName, LinkOption... options) throws IOException {
/*  251 */     this((Files.isDirectory(inputPath, options) && !entryName.endsWith("/")) ? (entryName + "/") : entryName);
/*      */     
/*  253 */     if (Files.isRegularFile(inputPath, options)) {
/*  254 */       setSize(Files.size(inputPath));
/*      */     }
/*  256 */     setTime(Files.getLastModifiedTime(inputPath, options));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTime(FileTime fileTime) {
/*  266 */     setTime(fileTime.toMillis());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object clone() {
/*  275 */     ZipArchiveEntry e = (ZipArchiveEntry)super.clone();
/*      */     
/*  277 */     e.setInternalAttributes(getInternalAttributes());
/*  278 */     e.setExternalAttributes(getExternalAttributes());
/*  279 */     e.setExtraFields(getAllExtraFieldsNoCopy());
/*  280 */     return e;
/*      */   }
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
/*      */   public int getMethod() {
/*  293 */     return this.method;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMethod(int method) {
/*  305 */     if (method < 0) {
/*  306 */       throw new IllegalArgumentException("ZIP compression method can not be negative: " + method);
/*      */     }
/*      */     
/*  309 */     this.method = method;
/*      */   }
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
/*      */   public int getInternalAttributes() {
/*  322 */     return this.internalAttributes;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setInternalAttributes(int value) {
/*  330 */     this.internalAttributes = value;
/*      */   }
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
/*      */   public long getExternalAttributes() {
/*  343 */     return this.externalAttributes;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setExternalAttributes(long value) {
/*  351 */     this.externalAttributes = value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUnixMode(int mode) {
/*  361 */     setExternalAttributes((mode << 16 | (((mode & 0x80) == 0) ? 1 : 0) | (
/*      */ 
/*      */ 
/*      */         
/*  365 */         isDirectory() ? 16 : 0)));
/*      */     
/*  367 */     this.platform = 3;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getUnixMode() {
/*  375 */     return (this.platform != 3) ? 0 : 
/*  376 */       (int)(getExternalAttributes() >> 16L & 0xFFFFL);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isUnixSymlink() {
/*  388 */     return ((getUnixMode() & 0xF000) == 40960);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getPlatform() {
/*  399 */     return this.platform;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setPlatform(int platform) {
/*  407 */     this.platform = platform;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int getAlignment() {
/*  418 */     return this.alignment;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAlignment(int alignment) {
/*  429 */     if ((alignment & alignment - 1) != 0 || alignment > 65535) {
/*  430 */       throw new IllegalArgumentException("Invalid value for alignment, must be power of two and no bigger than 65535 but is " + alignment);
/*      */     }
/*      */     
/*  433 */     this.alignment = alignment;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setExtraFields(ZipExtraField[] fields) {
/*  441 */     this.unparseableExtra = null;
/*  442 */     List<ZipExtraField> newFields = new ArrayList<>();
/*  443 */     if (fields != null) {
/*  444 */       for (ZipExtraField field : fields) {
/*  445 */         if (field instanceof UnparseableExtraFieldData) {
/*  446 */           this.unparseableExtra = (UnparseableExtraFieldData)field;
/*      */         } else {
/*  448 */           newFields.add(field);
/*      */         } 
/*      */       } 
/*      */     }
/*  452 */     this.extraFields = newFields.<ZipExtraField>toArray(ExtraFieldUtils.EMPTY_ZIP_EXTRA_FIELD_ARRAY);
/*  453 */     setExtra();
/*      */   }
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
/*      */   public ZipExtraField[] getExtraFields() {
/*  467 */     return getParseableExtraFields();
/*      */   }
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
/*      */   public ZipExtraField[] getExtraFields(boolean includeUnparseable) {
/*  480 */     return includeUnparseable ? 
/*  481 */       getAllExtraFields() : 
/*  482 */       getParseableExtraFields();
/*      */   }
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
/*      */   public ZipExtraField[] getExtraFields(ExtraFieldParsingBehavior parsingBehavior) throws ZipException {
/*  497 */     if (parsingBehavior == ExtraFieldParsingMode.BEST_EFFORT) {
/*  498 */       return getExtraFields(true);
/*      */     }
/*  500 */     if (parsingBehavior == ExtraFieldParsingMode.ONLY_PARSEABLE_LENIENT) {
/*  501 */       return getExtraFields(false);
/*      */     }
/*  503 */     byte[] local = getExtra();
/*  504 */     List<ZipExtraField> localFields = new ArrayList<>(Arrays.asList(ExtraFieldUtils.parse(local, true, parsingBehavior)));
/*      */     
/*  506 */     byte[] central = getCentralDirectoryExtra();
/*  507 */     List<ZipExtraField> centralFields = new ArrayList<>(Arrays.asList(ExtraFieldUtils.parse(central, false, parsingBehavior)));
/*      */     
/*  509 */     List<ZipExtraField> merged = new ArrayList<>();
/*  510 */     for (ZipExtraField l : localFields) {
/*  511 */       ZipExtraField c = null;
/*  512 */       if (l instanceof UnparseableExtraFieldData) {
/*  513 */         c = findUnparseable(centralFields);
/*      */       } else {
/*  515 */         c = findMatching(l.getHeaderId(), centralFields);
/*      */       } 
/*  517 */       if (c != null) {
/*  518 */         byte[] cd = c.getCentralDirectoryData();
/*  519 */         if (cd != null && cd.length > 0) {
/*  520 */           l.parseFromCentralDirectoryData(cd, 0, cd.length);
/*      */         }
/*  522 */         centralFields.remove(c);
/*      */       } 
/*  524 */       merged.add(l);
/*      */     } 
/*  526 */     merged.addAll(centralFields);
/*  527 */     return merged.<ZipExtraField>toArray(ExtraFieldUtils.EMPTY_ZIP_EXTRA_FIELD_ARRAY);
/*      */   }
/*      */   
/*      */   private ZipExtraField[] getParseableExtraFieldsNoCopy() {
/*  531 */     if (this.extraFields == null) {
/*  532 */       return ExtraFieldUtils.EMPTY_ZIP_EXTRA_FIELD_ARRAY;
/*      */     }
/*  534 */     return this.extraFields;
/*      */   }
/*      */   
/*      */   private ZipExtraField[] getParseableExtraFields() {
/*  538 */     ZipExtraField[] parseableExtraFields = getParseableExtraFieldsNoCopy();
/*  539 */     return (parseableExtraFields == this.extraFields) ? copyOf(parseableExtraFields, parseableExtraFields.length) : parseableExtraFields;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ZipExtraField[] getAllExtraFieldsNoCopy() {
/*  548 */     if (this.extraFields == null) {
/*  549 */       return getUnparseableOnly();
/*      */     }
/*  551 */     return (this.unparseableExtra != null) ? getMergedFields() : this.extraFields;
/*      */   }
/*      */   
/*      */   private ZipExtraField[] getMergedFields() {
/*  555 */     ZipExtraField[] zipExtraFields = copyOf(this.extraFields, this.extraFields.length + 1);
/*  556 */     zipExtraFields[this.extraFields.length] = this.unparseableExtra;
/*  557 */     return zipExtraFields;
/*      */   }
/*      */   
/*      */   private ZipExtraField[] getUnparseableOnly() {
/*  561 */     (new ZipExtraField[1])[0] = this.unparseableExtra; return (this.unparseableExtra == null) ? ExtraFieldUtils.EMPTY_ZIP_EXTRA_FIELD_ARRAY : new ZipExtraField[1];
/*      */   }
/*      */   
/*      */   private ZipExtraField[] getAllExtraFields() {
/*  565 */     ZipExtraField[] allExtraFieldsNoCopy = getAllExtraFieldsNoCopy();
/*  566 */     return (allExtraFieldsNoCopy == this.extraFields) ? copyOf(allExtraFieldsNoCopy, allExtraFieldsNoCopy.length) : allExtraFieldsNoCopy;
/*      */   }
/*      */ 
/*      */   
/*      */   private ZipExtraField findUnparseable(List<ZipExtraField> fs) {
/*  571 */     for (ZipExtraField f : fs) {
/*  572 */       if (f instanceof UnparseableExtraFieldData) {
/*  573 */         return f;
/*      */       }
/*      */     } 
/*  576 */     return null;
/*      */   }
/*      */   
/*      */   private ZipExtraField findMatching(ZipShort headerId, List<ZipExtraField> fs) {
/*  580 */     for (ZipExtraField f : fs) {
/*  581 */       if (headerId.equals(f.getHeaderId())) {
/*  582 */         return f;
/*      */       }
/*      */     } 
/*  585 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addExtraField(ZipExtraField ze) {
/*  597 */     if (ze instanceof UnparseableExtraFieldData) {
/*  598 */       this.unparseableExtra = (UnparseableExtraFieldData)ze;
/*      */     }
/*  600 */     else if (this.extraFields == null) {
/*  601 */       this.extraFields = new ZipExtraField[] { ze };
/*      */     } else {
/*  603 */       if (getExtraField(ze.getHeaderId()) != null) {
/*  604 */         removeExtraField(ze.getHeaderId());
/*      */       }
/*  606 */       ZipExtraField[] zipExtraFields = copyOf(this.extraFields, this.extraFields.length + 1);
/*  607 */       zipExtraFields[zipExtraFields.length - 1] = ze;
/*  608 */       this.extraFields = zipExtraFields;
/*      */     } 
/*      */     
/*  611 */     setExtra();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addAsFirstExtraField(ZipExtraField ze) {
/*  622 */     if (ze instanceof UnparseableExtraFieldData) {
/*  623 */       this.unparseableExtra = (UnparseableExtraFieldData)ze;
/*      */     } else {
/*  625 */       if (getExtraField(ze.getHeaderId()) != null) {
/*  626 */         removeExtraField(ze.getHeaderId());
/*      */       }
/*  628 */       ZipExtraField[] copy = this.extraFields;
/*  629 */       int newLen = (this.extraFields != null) ? (this.extraFields.length + 1) : 1;
/*  630 */       this.extraFields = new ZipExtraField[newLen];
/*  631 */       this.extraFields[0] = ze;
/*  632 */       if (copy != null) {
/*  633 */         System.arraycopy(copy, 0, this.extraFields, 1, this.extraFields.length - 1);
/*      */       }
/*      */     } 
/*  636 */     setExtra();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeExtraField(ZipShort type) {
/*  644 */     if (this.extraFields == null) {
/*  645 */       throw new NoSuchElementException();
/*      */     }
/*      */     
/*  648 */     List<ZipExtraField> newResult = new ArrayList<>();
/*  649 */     for (ZipExtraField extraField : this.extraFields) {
/*  650 */       if (!type.equals(extraField.getHeaderId())) {
/*  651 */         newResult.add(extraField);
/*      */       }
/*      */     } 
/*  654 */     if (this.extraFields.length == newResult.size()) {
/*  655 */       throw new NoSuchElementException();
/*      */     }
/*  657 */     this.extraFields = newResult.<ZipExtraField>toArray(ExtraFieldUtils.EMPTY_ZIP_EXTRA_FIELD_ARRAY);
/*  658 */     setExtra();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeUnparseableExtraFieldData() {
/*  667 */     if (this.unparseableExtra == null) {
/*  668 */       throw new NoSuchElementException();
/*      */     }
/*  670 */     this.unparseableExtra = null;
/*  671 */     setExtra();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ZipExtraField getExtraField(ZipShort type) {
/*  681 */     if (this.extraFields != null) {
/*  682 */       for (ZipExtraField extraField : this.extraFields) {
/*  683 */         if (type.equals(extraField.getHeaderId())) {
/*  684 */           return extraField;
/*      */         }
/*      */       } 
/*      */     }
/*  688 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UnparseableExtraFieldData getUnparseableExtraFieldData() {
/*  699 */     return this.unparseableExtra;
/*      */   }
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
/*      */   public void setExtra(byte[] extra) throws RuntimeException {
/*      */     try {
/*  713 */       ZipExtraField[] local = ExtraFieldUtils.parse(extra, true, ExtraFieldParsingMode.BEST_EFFORT);
/*  714 */       mergeExtraFields(local, true);
/*  715 */     } catch (ZipException e) {
/*      */       
/*  717 */       throw new RuntimeException("Error parsing extra fields for entry: " + 
/*  718 */           getName() + " - " + e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setExtra() {
/*  729 */     super.setExtra(ExtraFieldUtils.mergeLocalFileDataData(getAllExtraFieldsNoCopy()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCentralDirectoryExtra(byte[] b) {
/*      */     try {
/*  738 */       ZipExtraField[] central = ExtraFieldUtils.parse(b, false, ExtraFieldParsingMode.BEST_EFFORT);
/*  739 */       mergeExtraFields(central, false);
/*  740 */     } catch (ZipException e) {
/*      */       
/*  742 */       throw new RuntimeException(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] getLocalFileDataExtra() {
/*  751 */     byte[] extra = getExtra();
/*  752 */     return (extra != null) ? extra : ByteUtils.EMPTY_BYTE_ARRAY;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] getCentralDirectoryExtra() {
/*  760 */     return ExtraFieldUtils.mergeCentralDirectoryData(getAllExtraFieldsNoCopy());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getName() {
/*  772 */     return (this.name == null) ? super.getName() : this.name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isDirectory() {
/*  781 */     String n = getName();
/*  782 */     return (n != null && n.endsWith("/"));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setName(String name) {
/*  790 */     if (name != null && getPlatform() == 0 && 
/*  791 */       !name.contains("/")) {
/*  792 */       name = name.replace('\\', '/');
/*      */     }
/*  794 */     this.name = name;
/*      */   }
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
/*      */   public long getSize() {
/*  808 */     return this.size;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSize(long size) {
/*  819 */     if (size < 0L) {
/*  820 */       throw new IllegalArgumentException("Invalid entry size");
/*      */     }
/*  822 */     this.size = size;
/*      */   }
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
/*      */   protected void setName(String name, byte[] rawName) {
/*  835 */     setName(name);
/*  836 */     this.rawName = rawName;
/*      */   }
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
/*      */   public byte[] getRawName() {
/*  850 */     if (this.rawName != null) {
/*  851 */       return Arrays.copyOf(this.rawName, this.rawName.length);
/*      */     }
/*  853 */     return null;
/*      */   }
/*      */   
/*      */   protected long getLocalHeaderOffset() {
/*  857 */     return this.localHeaderOffset;
/*      */   }
/*      */   
/*      */   protected void setLocalHeaderOffset(long localHeaderOffset) {
/*  861 */     this.localHeaderOffset = localHeaderOffset;
/*      */   }
/*      */ 
/*      */   
/*      */   public long getDataOffset() {
/*  866 */     return this.dataOffset;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setDataOffset(long dataOffset) {
/*  876 */     this.dataOffset = dataOffset;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isStreamContiguous() {
/*  881 */     return this.isStreamContiguous;
/*      */   }
/*      */   
/*      */   protected void setStreamContiguous(boolean isStreamContiguous) {
/*  885 */     this.isStreamContiguous = isStreamContiguous;
/*      */   }
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
/*      */   public int hashCode() {
/*  899 */     String n = getName();
/*  900 */     return ((n == null) ? "" : n).hashCode();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public GeneralPurposeBit getGeneralPurposeBit() {
/*  909 */     return this.gpb;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setGeneralPurposeBit(GeneralPurposeBit b) {
/*  918 */     this.gpb = b;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void mergeExtraFields(ZipExtraField[] f, boolean local) {
/*  930 */     if (this.extraFields == null) {
/*  931 */       setExtraFields(f);
/*      */     } else {
/*  933 */       for (ZipExtraField element : f) {
/*      */         ZipExtraField existing;
/*  935 */         if (element instanceof UnparseableExtraFieldData) {
/*  936 */           existing = this.unparseableExtra;
/*      */         } else {
/*  938 */           existing = getExtraField(element.getHeaderId());
/*      */         } 
/*  940 */         if (existing == null) {
/*  941 */           addExtraField(element);
/*      */         } else {
/*      */           
/*  944 */           byte[] b = local ? element.getLocalFileDataData() : element.getCentralDirectoryData();
/*      */           try {
/*  946 */             if (local) {
/*  947 */               existing.parseFromLocalFileData(b, 0, b.length);
/*      */             } else {
/*  949 */               existing.parseFromCentralDirectoryData(b, 0, b.length);
/*      */             } 
/*  951 */           } catch (ZipException ex) {
/*      */             
/*  953 */             UnrecognizedExtraField u = new UnrecognizedExtraField();
/*  954 */             u.setHeaderId(existing.getHeaderId());
/*  955 */             if (local) {
/*  956 */               u.setLocalFileDataData(b);
/*  957 */               u.setCentralDirectoryData(existing.getCentralDirectoryData());
/*      */             } else {
/*  959 */               u.setLocalFileDataData(existing.getLocalFileDataData());
/*  960 */               u.setCentralDirectoryData(b);
/*      */             } 
/*  962 */             removeExtraField(existing.getHeaderId());
/*  963 */             addExtraField(u);
/*      */           } 
/*      */         } 
/*      */       } 
/*  967 */       setExtra();
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
/*      */ 
/*      */   
/*      */   public Date getLastModifiedDate() {
/*  981 */     return new Date(getTime());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean equals(Object obj) {
/*  989 */     if (this == obj) {
/*  990 */       return true;
/*      */     }
/*  992 */     if (obj == null || getClass() != obj.getClass()) {
/*  993 */       return false;
/*      */     }
/*  995 */     ZipArchiveEntry other = (ZipArchiveEntry)obj;
/*  996 */     String myName = getName();
/*  997 */     String otherName = other.getName();
/*  998 */     if (!Objects.equals(myName, otherName)) {
/*  999 */       return false;
/*      */     }
/* 1001 */     String myComment = getComment();
/* 1002 */     String otherComment = other.getComment();
/* 1003 */     if (myComment == null) {
/* 1004 */       myComment = "";
/*      */     }
/* 1006 */     if (otherComment == null) {
/* 1007 */       otherComment = "";
/*      */     }
/* 1009 */     return (getTime() == other.getTime() && myComment
/* 1010 */       .equals(otherComment) && 
/* 1011 */       getInternalAttributes() == other.getInternalAttributes() && 
/* 1012 */       getPlatform() == other.getPlatform() && 
/* 1013 */       getExternalAttributes() == other.getExternalAttributes() && 
/* 1014 */       getMethod() == other.getMethod() && 
/* 1015 */       getSize() == other.getSize() && 
/* 1016 */       getCrc() == other.getCrc() && 
/* 1017 */       getCompressedSize() == other.getCompressedSize() && 
/* 1018 */       Arrays.equals(getCentralDirectoryExtra(), other
/* 1019 */         .getCentralDirectoryExtra()) && 
/* 1020 */       Arrays.equals(getLocalFileDataExtra(), other
/* 1021 */         .getLocalFileDataExtra()) && this.localHeaderOffset == other.localHeaderOffset && this.dataOffset == other.dataOffset && this.gpb
/*      */ 
/*      */       
/* 1024 */       .equals(other.gpb));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setVersionMadeBy(int versionMadeBy) {
/* 1033 */     this.versionMadeBy = versionMadeBy;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setVersionRequired(int versionRequired) {
/* 1042 */     this.versionRequired = versionRequired;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getVersionRequired() {
/* 1051 */     return this.versionRequired;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getVersionMadeBy() {
/* 1060 */     return this.versionMadeBy;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getRawFlag() {
/* 1069 */     return this.rawFlag;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRawFlag(int rawFlag) {
/* 1078 */     this.rawFlag = rawFlag;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public NameSource getNameSource() {
/* 1087 */     return this.nameSource;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNameSource(NameSource nameSource) {
/* 1096 */     this.nameSource = nameSource;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CommentSource getCommentSource() {
/* 1105 */     return this.commentSource;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCommentSource(CommentSource commentSource) {
/* 1114 */     this.commentSource = commentSource;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getDiskNumberStart() {
/* 1124 */     return this.diskNumberStart;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDiskNumberStart(long diskNumberStart) {
/* 1134 */     this.diskNumberStart = diskNumberStart;
/*      */   }
/*      */   
/*      */   private ZipExtraField[] copyOf(ZipExtraField[] src, int length) {
/* 1138 */     ZipExtraField[] cpy = new ZipExtraField[length];
/* 1139 */     System.arraycopy(src, 0, cpy, 0, Math.min(src.length, length));
/* 1140 */     return cpy;
/*      */   }
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
/*      */   public enum ExtraFieldParsingMode
/*      */     implements ExtraFieldParsingBehavior
/*      */   {
/* 1173 */     BEST_EFFORT((String)ExtraFieldUtils.UnparseableExtraField.READ)
/*      */     {
/*      */       public ZipExtraField fill(ZipExtraField field, byte[] data, int off, int len, boolean local) {
/* 1176 */         return fillAndMakeUnrecognizedOnError(field, data, off, len, local);
/*      */       }
/*      */     },
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
/* 1193 */     STRICT_FOR_KNOW_EXTRA_FIELDS((String)ExtraFieldUtils.UnparseableExtraField.READ),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1202 */     ONLY_PARSEABLE_LENIENT((String)ExtraFieldUtils.UnparseableExtraField.SKIP)
/*      */     {
/*      */       public ZipExtraField fill(ZipExtraField field, byte[] data, int off, int len, boolean local) {
/* 1205 */         return fillAndMakeUnrecognizedOnError(field, data, off, len, local);
/*      */       }
/*      */     },
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
/* 1218 */     ONLY_PARSEABLE_STRICT((String)ExtraFieldUtils.UnparseableExtraField.SKIP),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1224 */     DRACONIC((String)ExtraFieldUtils.UnparseableExtraField.THROW);
/*      */     
/*      */     private final ExtraFieldUtils.UnparseableExtraField onUnparseableData;
/*      */     
/*      */     ExtraFieldParsingMode(ExtraFieldUtils.UnparseableExtraField onUnparseableData) {
/* 1229 */       this.onUnparseableData = onUnparseableData;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public ZipExtraField onUnparseableExtraField(byte[] data, int off, int len, boolean local, int claimedLength) throws ZipException {
/* 1235 */       return this.onUnparseableData.onUnparseableExtraField(data, off, len, local, claimedLength);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public ZipExtraField createExtraField(ZipShort headerId) throws ZipException, InstantiationException, IllegalAccessException {
/* 1241 */       return ExtraFieldUtils.createExtraField(headerId);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public ZipExtraField fill(ZipExtraField field, byte[] data, int off, int len, boolean local) throws ZipException {
/* 1247 */       return ExtraFieldUtils.fillExtraField(field, data, off, len, local);
/*      */     }
/*      */ 
/*      */     
/*      */     private static ZipExtraField fillAndMakeUnrecognizedOnError(ZipExtraField field, byte[] data, int off, int len, boolean local) {
/*      */       try {
/* 1253 */         return ExtraFieldUtils.fillExtraField(field, data, off, len, local);
/* 1254 */       } catch (ZipException ex) {
/* 1255 */         UnrecognizedExtraField u = new UnrecognizedExtraField();
/* 1256 */         u.setHeaderId(field.getHeaderId());
/* 1257 */         if (local) {
/* 1258 */           u.setLocalFileDataData(Arrays.copyOfRange(data, off, off + len));
/*      */         } else {
/* 1260 */           u.setCentralDirectoryData(Arrays.copyOfRange(data, off, off + len));
/*      */         } 
/* 1262 */         return u;
/*      */       } 
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\ZipArchiveEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */