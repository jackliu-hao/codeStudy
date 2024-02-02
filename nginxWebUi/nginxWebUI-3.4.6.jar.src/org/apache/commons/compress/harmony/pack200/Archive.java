/*     */ package org.apache.commons.compress.harmony.pack200;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarFile;
/*     */ import java.util.jar.JarInputStream;
/*     */ import java.util.zip.GZIPOutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Archive
/*     */ {
/*     */   private final JarInputStream jarInputStream;
/*     */   private final OutputStream outputStream;
/*     */   private JarFile jarFile;
/*     */   private long currentSegmentSize;
/*     */   private final PackingOptions options;
/*     */   
/*     */   public Archive(JarInputStream inputStream, OutputStream outputStream, PackingOptions options) throws IOException {
/*  54 */     this.jarInputStream = inputStream;
/*  55 */     if (options == null)
/*     */     {
/*  57 */       options = new PackingOptions();
/*     */     }
/*  59 */     this.options = options;
/*  60 */     if (options.isGzip()) {
/*  61 */       outputStream = new GZIPOutputStream(outputStream);
/*     */     }
/*  63 */     this.outputStream = new BufferedOutputStream(outputStream);
/*  64 */     PackingUtils.config(options);
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
/*     */   public Archive(JarFile jarFile, OutputStream outputStream, PackingOptions options) throws IOException {
/*  76 */     if (options == null) {
/*  77 */       options = new PackingOptions();
/*     */     }
/*  79 */     this.options = options;
/*  80 */     if (options.isGzip()) {
/*  81 */       outputStream = new GZIPOutputStream(outputStream);
/*     */     }
/*  83 */     this.outputStream = new BufferedOutputStream(outputStream);
/*  84 */     this.jarFile = jarFile;
/*  85 */     this.jarInputStream = null;
/*  86 */     PackingUtils.config(options);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void pack() throws Pack200Exception, IOException {
/*  96 */     if (0 == this.options.getEffort()) {
/*  97 */       doZeroEffortPack();
/*     */     } else {
/*  99 */       doNormalPack();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void doZeroEffortPack() throws IOException, Pack200Exception {
/* 104 */     PackingUtils.log("Start to perform a zero-effort packing");
/* 105 */     if (this.jarInputStream != null) {
/* 106 */       PackingUtils.copyThroughJar(this.jarInputStream, this.outputStream);
/*     */     } else {
/* 108 */       PackingUtils.copyThroughJar(this.jarFile, this.outputStream);
/*     */     } 
/*     */   }
/*     */   private void doNormalPack() throws IOException, Pack200Exception {
/*     */     List packingFileList;
/* 113 */     PackingUtils.log("Start to perform a normal packing");
/*     */     
/* 115 */     if (this.jarInputStream != null) {
/* 116 */       packingFileList = PackingUtils.getPackingFileListFromJar(this.jarInputStream, this.options.isKeepFileOrder());
/*     */     } else {
/* 118 */       packingFileList = PackingUtils.getPackingFileListFromJar(this.jarFile, this.options.isKeepFileOrder());
/*     */     } 
/*     */     
/* 121 */     List<SegmentUnit> segmentUnitList = splitIntoSegments(packingFileList);
/* 122 */     int previousByteAmount = 0;
/* 123 */     int packedByteAmount = 0;
/*     */     
/* 125 */     int segmentSize = segmentUnitList.size();
/*     */     
/* 127 */     for (int index = 0; index < segmentSize; index++) {
/* 128 */       SegmentUnit segmentUnit = segmentUnitList.get(index);
/* 129 */       (new Segment()).pack(segmentUnit, this.outputStream, this.options);
/* 130 */       previousByteAmount += segmentUnit.getByteAmount();
/* 131 */       packedByteAmount += segmentUnit.getPackedByteAmount();
/*     */     } 
/*     */     
/* 134 */     PackingUtils.log("Total: Packed " + previousByteAmount + " input bytes of " + packingFileList.size() + " files into " + packedByteAmount + " bytes in " + segmentSize + " segments");
/*     */ 
/*     */     
/* 137 */     this.outputStream.close();
/*     */   }
/*     */   
/*     */   private List splitIntoSegments(List<PackingFile> packingFileList) throws IOException, Pack200Exception {
/* 141 */     List<SegmentUnit> segmentUnitList = new ArrayList();
/* 142 */     List classes = new ArrayList();
/* 143 */     List files = new ArrayList();
/* 144 */     long segmentLimit = this.options.getSegmentLimit();
/*     */     
/* 146 */     int size = packingFileList.size();
/*     */     
/* 148 */     for (int index = 0; index < size; index++) {
/* 149 */       PackingFile packingFile = packingFileList.get(index);
/* 150 */       if (!addJarEntry(packingFile, classes, files)) {
/*     */         
/* 152 */         segmentUnitList.add(new SegmentUnit(classes, files));
/* 153 */         classes = new ArrayList();
/* 154 */         files = new ArrayList();
/* 155 */         this.currentSegmentSize = 0L;
/*     */         
/* 157 */         addJarEntry(packingFile, classes, files);
/*     */         
/* 159 */         this.currentSegmentSize = 0L;
/* 160 */       } else if (segmentLimit == 0L && estimateSize(packingFile) > 0L) {
/*     */         
/* 162 */         segmentUnitList.add(new SegmentUnit(classes, files));
/* 163 */         classes = new ArrayList();
/* 164 */         files = new ArrayList();
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 169 */     if (classes.size() > 0 || files.size() > 0) {
/* 170 */       segmentUnitList.add(new SegmentUnit(classes, files));
/*     */     }
/* 172 */     return segmentUnitList;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean addJarEntry(PackingFile packingFile, List<Pack200ClassReader> javaClasses, List<PackingFile> files) throws IOException, Pack200Exception {
/* 177 */     long segmentLimit = this.options.getSegmentLimit();
/* 178 */     if (segmentLimit != -1L && segmentLimit != 0L) {
/*     */ 
/*     */ 
/*     */       
/* 182 */       long packedSize = estimateSize(packingFile);
/* 183 */       if (packedSize + this.currentSegmentSize > segmentLimit && this.currentSegmentSize > 0L)
/*     */       {
/* 185 */         return false;
/*     */       }
/*     */       
/* 188 */       this.currentSegmentSize += packedSize;
/*     */     } 
/*     */     
/* 191 */     String name = packingFile.getName();
/* 192 */     if (name.endsWith(".class") && !this.options.isPassFile(name)) {
/* 193 */       Pack200ClassReader classParser = new Pack200ClassReader(packingFile.contents);
/* 194 */       classParser.setFileName(name);
/* 195 */       javaClasses.add(classParser);
/* 196 */       packingFile.contents = new byte[0];
/*     */     } 
/* 198 */     files.add(packingFile);
/* 199 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private long estimateSize(PackingFile packingFile) {
/* 205 */     String name = packingFile.getName();
/* 206 */     if (name.startsWith("META-INF") || name.startsWith("/META-INF")) {
/* 207 */       return 0L;
/*     */     }
/* 209 */     long fileSize = packingFile.contents.length;
/* 210 */     if (fileSize < 0L) {
/* 211 */       fileSize = 0L;
/*     */     }
/* 213 */     return name.length() + fileSize + 5L;
/*     */   }
/*     */ 
/*     */   
/*     */   static class SegmentUnit
/*     */   {
/*     */     private final List classList;
/*     */     
/*     */     private final List fileList;
/* 222 */     private int byteAmount = 0;
/*     */     
/* 224 */     private int packedByteAmount = 0;
/*     */     
/*     */     public SegmentUnit(List classes, List files) {
/* 227 */       this.classList = classes;
/* 228 */       this.fileList = files;
/*     */ 
/*     */ 
/*     */       
/* 232 */       for (Iterator<Pack200ClassReader> iterator = this.classList.iterator(); iterator.hasNext(); ) {
/* 233 */         Pack200ClassReader classReader = iterator.next();
/* 234 */         this.byteAmount += classReader.b.length;
/*     */       } 
/*     */ 
/*     */       
/* 238 */       for (Iterator<Archive.PackingFile> iterator1 = this.fileList.iterator(); iterator1.hasNext(); ) {
/* 239 */         Archive.PackingFile file = iterator1.next();
/* 240 */         this.byteAmount += file.contents.length;
/*     */       } 
/*     */     }
/*     */     
/*     */     public List getClassList() {
/* 245 */       return this.classList;
/*     */     }
/*     */     
/*     */     public int classListSize() {
/* 249 */       return this.classList.size();
/*     */     }
/*     */     
/*     */     public int fileListSize() {
/* 253 */       return this.fileList.size();
/*     */     }
/*     */     
/*     */     public List getFileList() {
/* 257 */       return this.fileList;
/*     */     }
/*     */     
/*     */     public int getByteAmount() {
/* 261 */       return this.byteAmount;
/*     */     }
/*     */     
/*     */     public int getPackedByteAmount() {
/* 265 */       return this.packedByteAmount;
/*     */     }
/*     */     
/*     */     public void addPackedByteAmount(int amount) {
/* 269 */       this.packedByteAmount += amount;
/*     */     }
/*     */   }
/*     */   
/*     */   static class PackingFile
/*     */   {
/*     */     private final String name;
/*     */     private byte[] contents;
/*     */     private final long modtime;
/*     */     private final boolean deflateHint;
/*     */     private final boolean isDirectory;
/*     */     
/*     */     public PackingFile(String name, byte[] contents, long modtime) {
/* 282 */       this.name = name;
/* 283 */       this.contents = contents;
/* 284 */       this.modtime = modtime;
/* 285 */       this.deflateHint = false;
/* 286 */       this.isDirectory = false;
/*     */     }
/*     */     
/*     */     public PackingFile(byte[] bytes, JarEntry jarEntry) {
/* 290 */       this.name = jarEntry.getName();
/* 291 */       this.contents = bytes;
/* 292 */       this.modtime = jarEntry.getTime();
/* 293 */       this.deflateHint = (jarEntry.getMethod() == 8);
/* 294 */       this.isDirectory = jarEntry.isDirectory();
/*     */     }
/*     */     
/*     */     public byte[] getContents() {
/* 298 */       return this.contents;
/*     */     }
/*     */     
/*     */     public String getName() {
/* 302 */       return this.name;
/*     */     }
/*     */     
/*     */     public long getModtime() {
/* 306 */       return this.modtime;
/*     */     }
/*     */     
/*     */     public void setContents(byte[] contents) {
/* 310 */       this.contents = contents;
/*     */     }
/*     */     
/*     */     public boolean isDefalteHint() {
/* 314 */       return this.deflateHint;
/*     */     }
/*     */     
/*     */     public boolean isDirectory() {
/* 318 */       return this.isDirectory;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 323 */       return this.name;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\Archive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */