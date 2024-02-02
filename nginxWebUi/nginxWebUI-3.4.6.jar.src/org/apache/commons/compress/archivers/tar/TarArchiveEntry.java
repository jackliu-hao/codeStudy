/*      */ package org.apache.commons.compress.archivers.tar;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.nio.file.DirectoryStream;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.LinkOption;
/*      */ import java.nio.file.Path;
/*      */ import java.nio.file.attribute.BasicFileAttributes;
/*      */ import java.nio.file.attribute.DosFileAttributes;
/*      */ import java.nio.file.attribute.FileTime;
/*      */ import java.nio.file.attribute.PosixFileAttributes;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.stream.Collectors;
/*      */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*      */ import org.apache.commons.compress.archivers.EntryStreamOffsets;
/*      */ import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*      */ import org.apache.commons.compress.utils.ArchiveUtils;
/*      */ import org.apache.commons.compress.utils.IOUtils;
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
/*      */ public class TarArchiveEntry
/*      */   implements ArchiveEntry, TarConstants, EntryStreamOffsets
/*      */ {
/*  165 */   private static final TarArchiveEntry[] EMPTY_TAR_ARCHIVE_ENTRY_ARRAY = new TarArchiveEntry[0];
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final long UNKNOWN = -1L;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  175 */   private String name = "";
/*      */ 
/*      */   
/*      */   private final boolean preserveAbsolutePath;
/*      */ 
/*      */   
/*      */   private int mode;
/*      */ 
/*      */   
/*      */   private long userId;
/*      */ 
/*      */   
/*      */   private long groupId;
/*      */ 
/*      */   
/*      */   private long size;
/*      */ 
/*      */   
/*      */   private long modTime;
/*      */ 
/*      */   
/*      */   private boolean checkSumOK;
/*      */ 
/*      */   
/*      */   private byte linkFlag;
/*      */ 
/*      */   
/*  202 */   private String linkName = "";
/*      */ 
/*      */   
/*  205 */   private String magic = "ustar\000";
/*      */   
/*  207 */   private String version = "00";
/*      */ 
/*      */   
/*      */   private String userName;
/*      */ 
/*      */   
/*  213 */   private String groupName = "";
/*      */ 
/*      */ 
/*      */   
/*      */   private int devMajor;
/*      */ 
/*      */   
/*      */   private int devMinor;
/*      */ 
/*      */   
/*      */   private List<TarArchiveStructSparse> sparseHeaders;
/*      */ 
/*      */   
/*      */   private boolean isExtended;
/*      */ 
/*      */   
/*      */   private long realSize;
/*      */ 
/*      */   
/*      */   private boolean paxGNUSparse;
/*      */ 
/*      */   
/*      */   private boolean paxGNU1XSparse;
/*      */ 
/*      */   
/*      */   private boolean starSparse;
/*      */ 
/*      */   
/*      */   private final Path file;
/*      */ 
/*      */   
/*      */   private final LinkOption[] linkOptions;
/*      */ 
/*      */   
/*  247 */   private final Map<String, String> extraPaxHeaders = new HashMap<>();
/*      */ 
/*      */   
/*      */   public static final int MAX_NAMELEN = 31;
/*      */ 
/*      */   
/*      */   public static final int DEFAULT_DIR_MODE = 16877;
/*      */ 
/*      */   
/*      */   public static final int DEFAULT_FILE_MODE = 33188;
/*      */ 
/*      */   
/*      */   public static final int MILLIS_PER_SECOND = 1000;
/*      */   
/*  261 */   private long dataOffset = -1L;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private TarArchiveEntry(boolean preserveAbsolutePath) {
/*  267 */     String user = System.getProperty("user.name", "");
/*      */     
/*  269 */     if (user.length() > 31) {
/*  270 */       user = user.substring(0, 31);
/*      */     }
/*      */     
/*  273 */     this.userName = user;
/*  274 */     this.file = null;
/*  275 */     this.linkOptions = IOUtils.EMPTY_LINK_OPTIONS;
/*  276 */     this.preserveAbsolutePath = preserveAbsolutePath;
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
/*      */   public TarArchiveEntry(String name) {
/*  290 */     this(name, false);
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
/*      */   public TarArchiveEntry(String name, boolean preserveAbsolutePath) {
/*  309 */     this(preserveAbsolutePath);
/*      */     
/*  311 */     name = normalizeFileName(name, preserveAbsolutePath);
/*  312 */     boolean isDir = name.endsWith("/");
/*      */     
/*  314 */     this.name = name;
/*  315 */     this.mode = isDir ? 16877 : 33188;
/*  316 */     this.linkFlag = isDir ? 53 : 48;
/*  317 */     this.modTime = System.currentTimeMillis() / 1000L;
/*  318 */     this.userName = "";
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
/*      */   public TarArchiveEntry(String name, byte linkFlag) {
/*  333 */     this(name, linkFlag, false);
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
/*      */   public TarArchiveEntry(String name, byte linkFlag, boolean preserveAbsolutePath) {
/*  352 */     this(name, preserveAbsolutePath);
/*  353 */     this.linkFlag = linkFlag;
/*  354 */     if (linkFlag == 76) {
/*  355 */       this.magic = "ustar ";
/*  356 */       this.version = " \000";
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TarArchiveEntry(File file) {
/*  379 */     this(file, file.getPath());
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
/*      */   public TarArchiveEntry(Path file) throws IOException {
/*  398 */     this(file, file.toString(), new LinkOption[0]);
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
/*      */   public TarArchiveEntry(File file, String fileName) {
/*  420 */     String normalizedName = normalizeFileName(fileName, false);
/*  421 */     this.file = file.toPath();
/*  422 */     this.linkOptions = IOUtils.EMPTY_LINK_OPTIONS;
/*      */     
/*      */     try {
/*  425 */       readFileMode(this.file, normalizedName, new LinkOption[0]);
/*  426 */     } catch (IOException e) {
/*      */ 
/*      */       
/*  429 */       if (!file.isDirectory()) {
/*  430 */         this.size = file.length();
/*      */       }
/*      */     } 
/*      */     
/*  434 */     this.userName = "";
/*      */     try {
/*  436 */       readOsSpecificProperties(this.file, new LinkOption[0]);
/*  437 */     } catch (IOException e) {
/*      */ 
/*      */       
/*  440 */       this.modTime = file.lastModified() / 1000L;
/*      */     } 
/*  442 */     this.preserveAbsolutePath = false;
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
/*      */   public TarArchiveEntry(Path file, String fileName, LinkOption... linkOptions) throws IOException {
/*  462 */     String normalizedName = normalizeFileName(fileName, false);
/*  463 */     this.file = file;
/*  464 */     this.linkOptions = (linkOptions == null) ? IOUtils.EMPTY_LINK_OPTIONS : linkOptions;
/*      */     
/*  466 */     readFileMode(file, normalizedName, linkOptions);
/*      */     
/*  468 */     this.userName = "";
/*  469 */     readOsSpecificProperties(file, new LinkOption[0]);
/*  470 */     this.preserveAbsolutePath = false;
/*      */   }
/*      */   
/*      */   private void readOsSpecificProperties(Path file, LinkOption... options) throws IOException {
/*  474 */     Set<String> availableAttributeViews = file.getFileSystem().supportedFileAttributeViews();
/*  475 */     if (availableAttributeViews.contains("posix")) {
/*  476 */       PosixFileAttributes posixFileAttributes = Files.<PosixFileAttributes>readAttributes(file, PosixFileAttributes.class, options);
/*  477 */       setModTime(posixFileAttributes.lastModifiedTime());
/*  478 */       this.userName = posixFileAttributes.owner().getName();
/*  479 */       this.groupName = posixFileAttributes.group().getName();
/*  480 */       if (availableAttributeViews.contains("unix")) {
/*  481 */         this.userId = ((Number)Files.getAttribute(file, "unix:uid", options)).longValue();
/*  482 */         this.groupId = ((Number)Files.getAttribute(file, "unix:gid", options)).longValue();
/*      */       } 
/*  484 */     } else if (availableAttributeViews.contains("dos")) {
/*  485 */       DosFileAttributes dosFileAttributes = Files.<DosFileAttributes>readAttributes(file, DosFileAttributes.class, options);
/*  486 */       setModTime(dosFileAttributes.lastModifiedTime());
/*  487 */       this.userName = Files.getOwner(file, options).getName();
/*      */     } else {
/*  489 */       BasicFileAttributes basicFileAttributes = Files.readAttributes(file, BasicFileAttributes.class, options);
/*  490 */       setModTime(basicFileAttributes.lastModifiedTime());
/*  491 */       this.userName = Files.getOwner(file, options).getName();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void readFileMode(Path file, String normalizedName, LinkOption... options) throws IOException {
/*  496 */     if (Files.isDirectory(file, options)) {
/*  497 */       this.mode = 16877;
/*  498 */       this.linkFlag = 53;
/*      */       
/*  500 */       int nameLength = normalizedName.length();
/*  501 */       if (nameLength == 0 || normalizedName.charAt(nameLength - 1) != '/') {
/*  502 */         this.name = normalizedName + "/";
/*      */       } else {
/*  504 */         this.name = normalizedName;
/*      */       } 
/*      */     } else {
/*  507 */       this.mode = 33188;
/*  508 */       this.linkFlag = 48;
/*  509 */       this.name = normalizedName;
/*  510 */       this.size = Files.size(file);
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
/*      */   public TarArchiveEntry(byte[] headerBuf) {
/*  522 */     this(false);
/*  523 */     parseTarHeader(headerBuf);
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
/*      */   public TarArchiveEntry(byte[] headerBuf, ZipEncoding encoding) throws IOException {
/*  538 */     this(headerBuf, encoding, false);
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
/*      */   public TarArchiveEntry(byte[] headerBuf, ZipEncoding encoding, boolean lenient) throws IOException {
/*  555 */     this(false);
/*  556 */     parseTarHeader(headerBuf, encoding, false, lenient);
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
/*      */   public TarArchiveEntry(byte[] headerBuf, ZipEncoding encoding, boolean lenient, long dataOffset) throws IOException {
/*  572 */     this(headerBuf, encoding, lenient);
/*  573 */     setDataOffset(dataOffset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean equals(TarArchiveEntry it) {
/*  584 */     return (it != null && getName().equals(it.getName()));
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
/*      */   public boolean equals(Object it) {
/*  596 */     if (it == null || getClass() != it.getClass()) {
/*  597 */       return false;
/*      */     }
/*  599 */     return equals((TarArchiveEntry)it);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int hashCode() {
/*  609 */     return getName().hashCode();
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
/*      */   public boolean isDescendent(TarArchiveEntry desc) {
/*  621 */     return desc.getName().startsWith(getName());
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
/*  633 */     return this.name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setName(String name) {
/*  642 */     this.name = normalizeFileName(name, this.preserveAbsolutePath);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMode(int mode) {
/*  651 */     this.mode = mode;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getLinkName() {
/*  660 */     return this.linkName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLinkName(String link) {
/*  671 */     this.linkName = link;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public int getUserId() {
/*  683 */     return (int)(this.userId & 0xFFFFFFFFFFFFFFFFL);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUserId(int userId) {
/*  692 */     setUserId(userId);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getLongUserId() {
/*  702 */     return this.userId;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUserId(long userId) {
/*  712 */     this.userId = userId;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public int getGroupId() {
/*  724 */     return (int)(this.groupId & 0xFFFFFFFFFFFFFFFFL);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setGroupId(int groupId) {
/*  733 */     setGroupId(groupId);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getLongGroupId() {
/*  743 */     return this.groupId;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setGroupId(long groupId) {
/*  753 */     this.groupId = groupId;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getUserName() {
/*  762 */     return this.userName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUserName(String userName) {
/*  771 */     this.userName = userName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getGroupName() {
/*  780 */     return this.groupName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setGroupName(String groupName) {
/*  789 */     this.groupName = groupName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setIds(int userId, int groupId) {
/*  799 */     setUserId(userId);
/*  800 */     setGroupId(groupId);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNames(String userName, String groupName) {
/*  810 */     setUserName(userName);
/*  811 */     setGroupName(groupName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setModTime(long time) {
/*  821 */     this.modTime = time / 1000L;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setModTime(Date time) {
/*  830 */     this.modTime = time.getTime() / 1000L;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setModTime(FileTime time) {
/*  840 */     this.modTime = time.to(TimeUnit.SECONDS);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getModTime() {
/*  849 */     return new Date(this.modTime * 1000L);
/*      */   }
/*      */ 
/*      */   
/*      */   public Date getLastModifiedDate() {
/*  854 */     return getModTime();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isCheckSumOK() {
/*  865 */     return this.checkSumOK;
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
/*      */   public File getFile() {
/*  877 */     if (this.file == null) {
/*  878 */       return null;
/*      */     }
/*  880 */     return this.file.toFile();
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
/*      */   public Path getPath() {
/*  893 */     return this.file;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMode() {
/*  902 */     return this.mode;
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
/*      */   public long getSize() {
/*  915 */     return this.size;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSparseHeaders(List<TarArchiveStructSparse> sparseHeaders) {
/*  924 */     this.sparseHeaders = sparseHeaders;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<TarArchiveStructSparse> getSparseHeaders() {
/*  934 */     return this.sparseHeaders;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<TarArchiveStructSparse> getOrderedSparseHeaders() throws IOException {
/*  945 */     if (this.sparseHeaders == null || this.sparseHeaders.isEmpty()) {
/*  946 */       return Collections.emptyList();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  951 */     List<TarArchiveStructSparse> orderedAndFiltered = (List<TarArchiveStructSparse>)this.sparseHeaders.stream().filter(s -> (s.getOffset() > 0L || s.getNumbytes() > 0L)).sorted(Comparator.comparingLong(TarArchiveStructSparse::getOffset)).collect(Collectors.toList());
/*      */     
/*  953 */     int numberOfHeaders = orderedAndFiltered.size();
/*  954 */     for (int i = 0; i < numberOfHeaders; i++) {
/*  955 */       TarArchiveStructSparse str = orderedAndFiltered.get(i);
/*  956 */       if (i + 1 < numberOfHeaders && str
/*  957 */         .getOffset() + str.getNumbytes() > ((TarArchiveStructSparse)orderedAndFiltered.get(i + 1)).getOffset()) {
/*  958 */         throw new IOException("Corrupted TAR archive. Sparse blocks for " + 
/*  959 */             getName() + " overlap each other.");
/*      */       }
/*  961 */       if (str.getOffset() + str.getNumbytes() < 0L)
/*      */       {
/*  963 */         throw new IOException("Unreadable TAR archive. Offset and numbytes for sparse block in " + 
/*  964 */             getName() + " too large.");
/*      */       }
/*      */     } 
/*  967 */     if (!orderedAndFiltered.isEmpty()) {
/*  968 */       TarArchiveStructSparse last = orderedAndFiltered.get(numberOfHeaders - 1);
/*  969 */       if (last.getOffset() + last.getNumbytes() > getRealSize()) {
/*  970 */         throw new IOException("Corrupted TAR archive. Sparse block extends beyond real size of the entry");
/*      */       }
/*      */     } 
/*      */     
/*  974 */     return orderedAndFiltered;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isPaxGNU1XSparse() {
/*  984 */     return this.paxGNU1XSparse;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSize(long size) {
/*  994 */     if (size < 0L) {
/*  995 */       throw new IllegalArgumentException("Size is out of range: " + size);
/*      */     }
/*  997 */     this.size = size;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getDevMajor() {
/* 1007 */     return this.devMajor;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDevMajor(int devNo) {
/* 1018 */     if (devNo < 0) {
/* 1019 */       throw new IllegalArgumentException("Major device number is out of range: " + devNo);
/*      */     }
/*      */     
/* 1022 */     this.devMajor = devNo;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getDevMinor() {
/* 1032 */     return this.devMinor;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDevMinor(int devNo) {
/* 1043 */     if (devNo < 0) {
/* 1044 */       throw new IllegalArgumentException("Minor device number is out of range: " + devNo);
/*      */     }
/*      */     
/* 1047 */     this.devMinor = devNo;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isExtended() {
/* 1057 */     return this.isExtended;
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
/*      */   public long getRealSize() {
/* 1070 */     if (!isSparse()) {
/* 1071 */       return getSize();
/*      */     }
/* 1073 */     return this.realSize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isGNUSparse() {
/* 1082 */     return (isOldGNUSparse() || isPaxGNUSparse());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isOldGNUSparse() {
/* 1093 */     return (this.linkFlag == 83);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isPaxGNUSparse() {
/* 1104 */     return this.paxGNUSparse;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isStarSparse() {
/* 1114 */     return this.starSparse;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isGNULongLinkEntry() {
/* 1123 */     return (this.linkFlag == 75);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isGNULongNameEntry() {
/* 1132 */     return (this.linkFlag == 76);
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
/*      */   public boolean isPaxHeader() {
/* 1144 */     return (this.linkFlag == 120 || this.linkFlag == 88);
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
/*      */   public boolean isGlobalPaxHeader() {
/* 1156 */     return (this.linkFlag == 103);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isDirectory() {
/* 1166 */     if (this.file != null) {
/* 1167 */       return Files.isDirectory(this.file, this.linkOptions);
/*      */     }
/*      */     
/* 1170 */     if (this.linkFlag == 53) {
/* 1171 */       return true;
/*      */     }
/*      */     
/* 1174 */     return (!isPaxHeader() && !isGlobalPaxHeader() && getName().endsWith("/"));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isFile() {
/* 1184 */     if (this.file != null) {
/* 1185 */       return Files.isRegularFile(this.file, this.linkOptions);
/*      */     }
/* 1187 */     if (this.linkFlag == 0 || this.linkFlag == 48) {
/* 1188 */       return true;
/*      */     }
/* 1190 */     return !getName().endsWith("/");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isSymbolicLink() {
/* 1200 */     return (this.linkFlag == 50);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isLink() {
/* 1210 */     return (this.linkFlag == 49);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isCharacterDevice() {
/* 1220 */     return (this.linkFlag == 51);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isBlockDevice() {
/* 1230 */     return (this.linkFlag == 52);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isFIFO() {
/* 1240 */     return (this.linkFlag == 54);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isSparse() {
/* 1250 */     return (isGNUSparse() || isStarSparse());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getDataOffset() {
/* 1259 */     return this.dataOffset;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDataOffset(long dataOffset) {
/* 1268 */     if (dataOffset < 0L) {
/* 1269 */       throw new IllegalArgumentException("The offset can not be smaller than 0");
/*      */     }
/* 1271 */     this.dataOffset = dataOffset;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isStreamContiguous() {
/* 1280 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map<String, String> getExtraPaxHeaders() {
/* 1289 */     return Collections.unmodifiableMap(this.extraPaxHeaders);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearExtraPaxHeaders() {
/* 1297 */     this.extraPaxHeaders.clear();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addPaxHeader(String name, String value) {
/*      */     try {
/* 1309 */       processPaxHeader(name, value);
/* 1310 */     } catch (IOException ex) {
/* 1311 */       throw new IllegalArgumentException("Invalid input", ex);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getExtraPaxHeader(String name) {
/* 1322 */     return this.extraPaxHeaders.get(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void updateEntryFromPaxHeaders(Map<String, String> headers) throws IOException {
/* 1331 */     for (Map.Entry<String, String> ent : headers.entrySet()) {
/* 1332 */       String key = ent.getKey();
/* 1333 */       String val = ent.getValue();
/* 1334 */       processPaxHeader(key, val, headers);
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
/*      */   private void processPaxHeader(String key, String val) throws IOException {
/* 1346 */     processPaxHeader(key, val, this.extraPaxHeaders);
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
/*      */ 
/*      */   
/*      */   private void processPaxHeader(String key, String val, Map<String, String> headers) throws IOException {
/*      */     long size;
/*      */     int devMinor, devMajor;
/* 1381 */     switch (key) {
/*      */       case "path":
/* 1383 */         setName(val);
/*      */         return;
/*      */       case "linkpath":
/* 1386 */         setLinkName(val);
/*      */         return;
/*      */       case "gid":
/* 1389 */         setGroupId(Long.parseLong(val));
/*      */         return;
/*      */       case "gname":
/* 1392 */         setGroupName(val);
/*      */         return;
/*      */       case "uid":
/* 1395 */         setUserId(Long.parseLong(val));
/*      */         return;
/*      */       case "uname":
/* 1398 */         setUserName(val);
/*      */         return;
/*      */       case "size":
/* 1401 */         size = Long.parseLong(val);
/* 1402 */         if (size < 0L) {
/* 1403 */           throw new IOException("Corrupted TAR archive. Entry size is negative");
/*      */         }
/* 1405 */         setSize(size);
/*      */         return;
/*      */       case "mtime":
/* 1408 */         setModTime((long)(Double.parseDouble(val) * 1000.0D));
/*      */         return;
/*      */       case "SCHILY.devminor":
/* 1411 */         devMinor = Integer.parseInt(val);
/* 1412 */         if (devMinor < 0) {
/* 1413 */           throw new IOException("Corrupted TAR archive. Dev-Minor is negative");
/*      */         }
/* 1415 */         setDevMinor(devMinor);
/*      */         return;
/*      */       case "SCHILY.devmajor":
/* 1418 */         devMajor = Integer.parseInt(val);
/* 1419 */         if (devMajor < 0) {
/* 1420 */           throw new IOException("Corrupted TAR archive. Dev-Major is negative");
/*      */         }
/* 1422 */         setDevMajor(devMajor);
/*      */         return;
/*      */       case "GNU.sparse.size":
/* 1425 */         fillGNUSparse0xData(headers);
/*      */         return;
/*      */       case "GNU.sparse.realsize":
/* 1428 */         fillGNUSparse1xData(headers);
/*      */         return;
/*      */       case "SCHILY.filetype":
/* 1431 */         if ("sparse".equals(val)) {
/* 1432 */           fillStarSparseData(headers);
/*      */         }
/*      */         return;
/*      */     } 
/* 1436 */     this.extraPaxHeaders.put(key, val);
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
/*      */   public TarArchiveEntry[] getDirectoryEntries() {
/* 1452 */     if (this.file == null || !isDirectory()) {
/* 1453 */       return EMPTY_TAR_ARCHIVE_ENTRY_ARRAY;
/*      */     }
/*      */     
/* 1456 */     List<TarArchiveEntry> entries = new ArrayList<>();
/* 1457 */     try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(this.file)) {
/* 1458 */       Iterator<Path> iterator = dirStream.iterator();
/* 1459 */       while (iterator.hasNext()) {
/* 1460 */         Path p = iterator.next();
/* 1461 */         entries.add(new TarArchiveEntry(p));
/*      */       } 
/* 1463 */     } catch (IOException e) {
/* 1464 */       return EMPTY_TAR_ARCHIVE_ENTRY_ARRAY;
/*      */     } 
/* 1466 */     return entries.<TarArchiveEntry>toArray(EMPTY_TAR_ARCHIVE_ENTRY_ARRAY);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeEntryHeader(byte[] outbuf) {
/*      */     try {
/* 1478 */       writeEntryHeader(outbuf, TarUtils.DEFAULT_ENCODING, false);
/* 1479 */     } catch (IOException ex) {
/*      */       try {
/* 1481 */         writeEntryHeader(outbuf, TarUtils.FALLBACK_ENCODING, false);
/* 1482 */       } catch (IOException ex2) {
/*      */         
/* 1484 */         throw new RuntimeException(ex2);
/*      */       } 
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
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeEntryHeader(byte[] outbuf, ZipEncoding encoding, boolean starMode) throws IOException {
/* 1502 */     int offset = 0;
/*      */     
/* 1504 */     offset = TarUtils.formatNameBytes(this.name, outbuf, offset, 100, encoding);
/*      */     
/* 1506 */     offset = writeEntryHeaderField(this.mode, outbuf, offset, 8, starMode);
/* 1507 */     offset = writeEntryHeaderField(this.userId, outbuf, offset, 8, starMode);
/*      */     
/* 1509 */     offset = writeEntryHeaderField(this.groupId, outbuf, offset, 8, starMode);
/*      */     
/* 1511 */     offset = writeEntryHeaderField(this.size, outbuf, offset, 12, starMode);
/* 1512 */     offset = writeEntryHeaderField(this.modTime, outbuf, offset, 12, starMode);
/*      */ 
/*      */     
/* 1515 */     int csOffset = offset;
/*      */     
/* 1517 */     for (int c = 0; c < 8; c++) {
/* 1518 */       outbuf[offset++] = 32;
/*      */     }
/*      */     
/* 1521 */     outbuf[offset++] = this.linkFlag;
/* 1522 */     offset = TarUtils.formatNameBytes(this.linkName, outbuf, offset, 100, encoding);
/*      */     
/* 1524 */     offset = TarUtils.formatNameBytes(this.magic, outbuf, offset, 6);
/* 1525 */     offset = TarUtils.formatNameBytes(this.version, outbuf, offset, 2);
/* 1526 */     offset = TarUtils.formatNameBytes(this.userName, outbuf, offset, 32, encoding);
/*      */     
/* 1528 */     offset = TarUtils.formatNameBytes(this.groupName, outbuf, offset, 32, encoding);
/*      */     
/* 1530 */     offset = writeEntryHeaderField(this.devMajor, outbuf, offset, 8, starMode);
/*      */     
/* 1532 */     offset = writeEntryHeaderField(this.devMinor, outbuf, offset, 8, starMode);
/*      */ 
/*      */     
/* 1535 */     while (offset < outbuf.length) {
/* 1536 */       outbuf[offset++] = 0;
/*      */     }
/*      */     
/* 1539 */     long chk = TarUtils.computeCheckSum(outbuf);
/*      */     
/* 1541 */     TarUtils.formatCheckSumOctalBytes(chk, outbuf, csOffset, 8);
/*      */   }
/*      */ 
/*      */   
/*      */   private int writeEntryHeaderField(long value, byte[] outbuf, int offset, int length, boolean starMode) {
/* 1546 */     if (!starMode && (value < 0L || value >= 1L << 3 * (length - 1)))
/*      */     {
/*      */ 
/*      */ 
/*      */       
/* 1551 */       return TarUtils.formatLongOctalBytes(0L, outbuf, offset, length);
/*      */     }
/* 1553 */     return TarUtils.formatLongOctalOrBinaryBytes(value, outbuf, offset, length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void parseTarHeader(byte[] header) {
/*      */     try {
/* 1565 */       parseTarHeader(header, TarUtils.DEFAULT_ENCODING);
/* 1566 */     } catch (IOException ex) {
/*      */       try {
/* 1568 */         parseTarHeader(header, TarUtils.DEFAULT_ENCODING, true, false);
/* 1569 */       } catch (IOException ex2) {
/*      */         
/* 1571 */         throw new RuntimeException(ex2);
/*      */       } 
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
/*      */ 
/*      */   
/*      */   public void parseTarHeader(byte[] header, ZipEncoding encoding) throws IOException {
/* 1588 */     parseTarHeader(header, encoding, false, false);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void parseTarHeader(byte[] header, ZipEncoding encoding, boolean oldStyle, boolean lenient) throws IOException {
/*      */     try {
/* 1595 */       parseTarHeaderUnwrapped(header, encoding, oldStyle, lenient);
/* 1596 */     } catch (IllegalArgumentException ex) {
/* 1597 */       throw new IOException("Corrupted TAR archive.", ex);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void parseTarHeaderUnwrapped(byte[] header, ZipEncoding encoding, boolean oldStyle, boolean lenient) throws IOException {
/*      */     String xstarPrefix;
/* 1604 */     int offset = 0;
/*      */     
/* 1606 */     this
/* 1607 */       .name = oldStyle ? TarUtils.parseName(header, offset, 100) : TarUtils.parseName(header, offset, 100, encoding);
/* 1608 */     offset += 100;
/* 1609 */     this.mode = (int)parseOctalOrBinary(header, offset, 8, lenient);
/* 1610 */     offset += 8;
/* 1611 */     this.userId = (int)parseOctalOrBinary(header, offset, 8, lenient);
/* 1612 */     offset += 8;
/* 1613 */     this.groupId = (int)parseOctalOrBinary(header, offset, 8, lenient);
/* 1614 */     offset += 8;
/* 1615 */     this.size = TarUtils.parseOctalOrBinary(header, offset, 12);
/* 1616 */     if (this.size < 0L) {
/* 1617 */       throw new IOException("broken archive, entry with negative size");
/*      */     }
/* 1619 */     offset += 12;
/* 1620 */     this.modTime = parseOctalOrBinary(header, offset, 12, lenient);
/* 1621 */     offset += 12;
/* 1622 */     this.checkSumOK = TarUtils.verifyCheckSum(header);
/* 1623 */     offset += 8;
/* 1624 */     this.linkFlag = header[offset++];
/* 1625 */     this
/* 1626 */       .linkName = oldStyle ? TarUtils.parseName(header, offset, 100) : TarUtils.parseName(header, offset, 100, encoding);
/* 1627 */     offset += 100;
/* 1628 */     this.magic = TarUtils.parseName(header, offset, 6);
/* 1629 */     offset += 6;
/* 1630 */     this.version = TarUtils.parseName(header, offset, 2);
/* 1631 */     offset += 2;
/* 1632 */     this
/* 1633 */       .userName = oldStyle ? TarUtils.parseName(header, offset, 32) : TarUtils.parseName(header, offset, 32, encoding);
/* 1634 */     offset += 32;
/* 1635 */     this
/* 1636 */       .groupName = oldStyle ? TarUtils.parseName(header, offset, 32) : TarUtils.parseName(header, offset, 32, encoding);
/* 1637 */     offset += 32;
/* 1638 */     if (this.linkFlag == 51 || this.linkFlag == 52) {
/* 1639 */       this.devMajor = (int)parseOctalOrBinary(header, offset, 8, lenient);
/* 1640 */       offset += 8;
/* 1641 */       this.devMinor = (int)parseOctalOrBinary(header, offset, 8, lenient);
/* 1642 */       offset += 8;
/*      */     } else {
/* 1644 */       offset += 16;
/*      */     } 
/*      */     
/* 1647 */     int type = evaluateType(header);
/* 1648 */     switch (type) {
/*      */       case 2:
/* 1650 */         offset += 12;
/* 1651 */         offset += 12;
/* 1652 */         offset += 12;
/* 1653 */         offset += 4;
/* 1654 */         offset++;
/* 1655 */         this
/* 1656 */           .sparseHeaders = new ArrayList<>(TarUtils.readSparseStructs(header, offset, 4));
/* 1657 */         offset += 96;
/* 1658 */         this.isExtended = TarUtils.parseBoolean(header, offset);
/* 1659 */         offset++;
/* 1660 */         this.realSize = TarUtils.parseOctal(header, offset, 12);
/* 1661 */         offset += 12;
/*      */         return;
/*      */ 
/*      */ 
/*      */       
/*      */       case 4:
/* 1667 */         xstarPrefix = oldStyle ? TarUtils.parseName(header, offset, 131) : TarUtils.parseName(header, offset, 131, encoding);
/* 1668 */         if (!xstarPrefix.isEmpty()) {
/* 1669 */           this.name = xstarPrefix + "/" + this.name;
/*      */         }
/*      */         return;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1677 */     String prefix = oldStyle ? TarUtils.parseName(header, offset, 155) : TarUtils.parseName(header, offset, 155, encoding);
/*      */ 
/*      */     
/* 1680 */     if (isDirectory() && !this.name.endsWith("/")) {
/* 1681 */       this.name += "/";
/*      */     }
/* 1683 */     if (!prefix.isEmpty()) {
/* 1684 */       this.name = prefix + "/" + this.name;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private long parseOctalOrBinary(byte[] header, int offset, int length, boolean lenient) {
/* 1691 */     if (lenient) {
/*      */       try {
/* 1693 */         return TarUtils.parseOctalOrBinary(header, offset, length);
/* 1694 */       } catch (IllegalArgumentException ex) {
/* 1695 */         return -1L;
/*      */       } 
/*      */     }
/* 1698 */     return TarUtils.parseOctalOrBinary(header, offset, length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String normalizeFileName(String fileName, boolean preserveAbsolutePath) {
/* 1707 */     if (!preserveAbsolutePath) {
/* 1708 */       String osname = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
/*      */       
/* 1710 */       if (osname != null)
/*      */       {
/*      */ 
/*      */ 
/*      */         
/* 1715 */         if (osname.startsWith("windows")) {
/* 1716 */           if (fileName.length() > 2) {
/* 1717 */             char ch1 = fileName.charAt(0);
/* 1718 */             char ch2 = fileName.charAt(1);
/*      */             
/* 1720 */             if (ch2 == ':' && ((ch1 >= 'a' && ch1 <= 'z') || (ch1 >= 'A' && ch1 <= 'Z')))
/*      */             {
/*      */               
/* 1723 */               fileName = fileName.substring(2);
/*      */             }
/*      */           } 
/* 1726 */         } else if (osname.contains("netware")) {
/* 1727 */           int colon = fileName.indexOf(':');
/* 1728 */           if (colon != -1) {
/* 1729 */             fileName = fileName.substring(colon + 1);
/*      */           }
/*      */         } 
/*      */       }
/*      */     } 
/*      */     
/* 1735 */     fileName = fileName.replace(File.separatorChar, '/');
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1740 */     while (!preserveAbsolutePath && fileName.startsWith("/")) {
/* 1741 */       fileName = fileName.substring(1);
/*      */     }
/* 1743 */     return fileName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int evaluateType(byte[] header) {
/* 1753 */     if (ArchiveUtils.matchAsciiBuffer("ustar ", header, 257, 6)) {
/* 1754 */       return 2;
/*      */     }
/* 1756 */     if (ArchiveUtils.matchAsciiBuffer("ustar\000", header, 257, 6)) {
/* 1757 */       if (ArchiveUtils.matchAsciiBuffer("tar\000", header, 508, 4))
/*      */       {
/* 1759 */         return 4;
/*      */       }
/* 1761 */       return 3;
/*      */     } 
/* 1763 */     return 0;
/*      */   }
/*      */   
/*      */   void fillGNUSparse0xData(Map<String, String> headers) {
/* 1767 */     this.paxGNUSparse = true;
/* 1768 */     this.realSize = Integer.parseInt(headers.get("GNU.sparse.size"));
/* 1769 */     if (headers.containsKey("GNU.sparse.name"))
/*      */     {
/* 1771 */       this.name = headers.get("GNU.sparse.name");
/*      */     }
/*      */   }
/*      */   
/*      */   void fillGNUSparse1xData(Map<String, String> headers) throws IOException {
/* 1776 */     this.paxGNUSparse = true;
/* 1777 */     this.paxGNU1XSparse = true;
/* 1778 */     if (headers.containsKey("GNU.sparse.name")) {
/* 1779 */       this.name = headers.get("GNU.sparse.name");
/*      */     }
/* 1781 */     if (headers.containsKey("GNU.sparse.realsize")) {
/*      */       try {
/* 1783 */         this.realSize = Integer.parseInt(headers.get("GNU.sparse.realsize"));
/* 1784 */       } catch (NumberFormatException ex) {
/* 1785 */         throw new IOException("Corrupted TAR archive. GNU.sparse.realsize header for " + this.name + " contains non-numeric value");
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   void fillStarSparseData(Map<String, String> headers) throws IOException {
/* 1792 */     this.starSparse = true;
/* 1793 */     if (headers.containsKey("SCHILY.realsize"))
/*      */       try {
/* 1795 */         this.realSize = Long.parseLong(headers.get("SCHILY.realsize"));
/* 1796 */       } catch (NumberFormatException ex) {
/* 1797 */         throw new IOException("Corrupted TAR archive. SCHILY.realsize header for " + this.name + " contains non-numeric value");
/*      */       }  
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\tar\TarArchiveEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */