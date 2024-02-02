/*     */ package org.apache.commons.compress.harmony.unpack200;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarOutputStream;
/*     */ import java.util.zip.CRC32;
/*     */ import org.apache.commons.compress.harmony.pack200.Pack200Exception;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.Attribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPField;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPMethod;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPUTF8;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.ClassConstantPool;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFile;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.InnerClassesAttribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.SourceFileAttribute;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Segment
/*     */ {
/*     */   public static final int LOG_LEVEL_VERBOSE = 2;
/*     */   public static final int LOG_LEVEL_STANDARD = 1;
/*     */   public static final int LOG_LEVEL_QUIET = 0;
/*     */   private SegmentHeader header;
/*     */   private CpBands cpBands;
/*     */   private AttrDefinitionBands attrDefinitionBands;
/*     */   private IcBands icBands;
/*     */   private ClassBands classBands;
/*     */   private BcBands bcBands;
/*     */   private FileBands fileBands;
/*     */   private boolean overrideDeflateHint;
/*     */   private boolean deflateHint;
/*     */   private boolean doPreRead;
/*     */   private int logLevel;
/*     */   private PrintWriter logStream;
/*     */   private byte[][] classFilesContents;
/*     */   private boolean[] fileDeflate;
/*     */   private boolean[] fileIsClass;
/*     */   private InputStream internalBuffer;
/*     */   
/*     */   private ClassFile buildClassFile(int classNum) throws Pack200Exception {
/* 109 */     ClassFile classFile = new ClassFile();
/* 110 */     int[] major = this.classBands.getClassVersionMajor();
/* 111 */     int[] minor = this.classBands.getClassVersionMinor();
/* 112 */     if (major != null) {
/* 113 */       classFile.major = major[classNum];
/* 114 */       classFile.minor = minor[classNum];
/*     */     } else {
/* 116 */       classFile.major = this.header.getDefaultClassMajorVersion();
/* 117 */       classFile.minor = this.header.getDefaultClassMinorVersion();
/*     */     } 
/*     */     
/* 120 */     ClassConstantPool cp = classFile.pool;
/* 121 */     int fullNameIndexInCpClass = this.classBands.getClassThisInts()[classNum];
/* 122 */     String fullName = this.cpBands.getCpClass()[fullNameIndexInCpClass];
/*     */     
/* 124 */     int i = fullName.lastIndexOf("/") + 1;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 129 */     ArrayList<Attribute> classAttributes = this.classBands.getClassAttributes()[classNum];
/* 130 */     SourceFileAttribute sourceFileAttribute = null;
/* 131 */     for (int index = 0; index < classAttributes.size(); index++) {
/* 132 */       if (((Attribute)classAttributes.get(index)).isSourceFileAttribute()) {
/* 133 */         sourceFileAttribute = (SourceFileAttribute)classAttributes.get(index);
/*     */       }
/*     */     } 
/*     */     
/* 137 */     if (sourceFileAttribute == null) {
/*     */ 
/*     */ 
/*     */       
/* 141 */       AttributeLayout SOURCE_FILE = this.attrDefinitionBands.getAttributeDefinitionMap().getAttributeLayout("SourceFile", 0);
/* 142 */       if (SOURCE_FILE.matches(this.classBands.getRawClassFlags()[classNum])) {
/* 143 */         int firstDollar = -1;
/* 144 */         for (int n = 0; n < fullName.length(); n++) {
/* 145 */           if (fullName.charAt(n) <= '$') {
/* 146 */             firstDollar = n;
/*     */           }
/*     */         } 
/* 149 */         String fileName = null;
/*     */         
/* 151 */         if (firstDollar > -1 && i <= firstDollar) {
/* 152 */           fileName = fullName.substring(i, firstDollar) + ".java";
/*     */         } else {
/* 154 */           fileName = fullName.substring(i) + ".java";
/*     */         } 
/* 156 */         sourceFileAttribute = new SourceFileAttribute(this.cpBands.cpUTF8Value(fileName, false));
/* 157 */         classFile.attributes = new Attribute[] { (Attribute)cp.add((ClassFileEntry)sourceFileAttribute) };
/*     */       } else {
/* 159 */         classFile.attributes = new Attribute[0];
/*     */       } 
/*     */     } else {
/* 162 */       classFile.attributes = new Attribute[] { (Attribute)cp.add((ClassFileEntry)sourceFileAttribute) };
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 169 */     ArrayList<Attribute> classAttributesWithoutSourceFileAttribute = new ArrayList(classAttributes.size());
/* 170 */     for (int j = 0; j < classAttributes.size(); j++) {
/* 171 */       Attribute attrib = classAttributes.get(j);
/* 172 */       if (!attrib.isSourceFileAttribute()) {
/* 173 */         classAttributesWithoutSourceFileAttribute.add(attrib);
/*     */       }
/*     */     } 
/* 176 */     Attribute[] originalAttributes = classFile.attributes;
/* 177 */     classFile
/* 178 */       .attributes = new Attribute[originalAttributes.length + classAttributesWithoutSourceFileAttribute.size()];
/* 179 */     System.arraycopy(originalAttributes, 0, classFile.attributes, 0, originalAttributes.length);
/* 180 */     for (int k = 0; k < classAttributesWithoutSourceFileAttribute.size(); k++) {
/* 181 */       Attribute attrib = classAttributesWithoutSourceFileAttribute.get(k);
/* 182 */       cp.add((ClassFileEntry)attrib);
/* 183 */       classFile.attributes[originalAttributes.length + k] = attrib;
/*     */     } 
/*     */ 
/*     */     
/* 187 */     ClassFileEntry cfThis = cp.add((ClassFileEntry)this.cpBands.cpClassValue(fullNameIndexInCpClass));
/* 188 */     ClassFileEntry cfSuper = cp.add((ClassFileEntry)this.cpBands.cpClassValue(this.classBands.getClassSuperInts()[classNum]));
/*     */     
/* 190 */     ClassFileEntry[] cfInterfaces = new ClassFileEntry[(this.classBands.getClassInterfacesInts()[classNum]).length];
/* 191 */     for (i = 0; i < cfInterfaces.length; i++) {
/* 192 */       cfInterfaces[i] = cp.add((ClassFileEntry)this.cpBands.cpClassValue(this.classBands.getClassInterfacesInts()[classNum][i]));
/*     */     }
/*     */     
/* 195 */     ClassFileEntry[] cfFields = new ClassFileEntry[this.classBands.getClassFieldCount()[classNum]];
/*     */     
/* 197 */     for (i = 0; i < cfFields.length; i++) {
/* 198 */       int descriptorIndex = this.classBands.getFieldDescrInts()[classNum][i];
/* 199 */       int nameIndex = this.cpBands.getCpDescriptorNameInts()[descriptorIndex];
/* 200 */       int typeIndex = this.cpBands.getCpDescriptorTypeInts()[descriptorIndex];
/* 201 */       CPUTF8 name = this.cpBands.cpUTF8Value(nameIndex);
/* 202 */       CPUTF8 descriptor = this.cpBands.cpSignatureValue(typeIndex);
/* 203 */       cfFields[i] = cp.add((ClassFileEntry)new CPField(name, descriptor, this.classBands.getFieldFlags()[classNum][i], this.classBands
/* 204 */             .getFieldAttributes()[classNum][i]));
/*     */     } 
/*     */     
/* 207 */     ClassFileEntry[] cfMethods = new ClassFileEntry[this.classBands.getClassMethodCount()[classNum]];
/*     */     
/* 209 */     for (i = 0; i < cfMethods.length; i++) {
/* 210 */       int descriptorIndex = this.classBands.getMethodDescrInts()[classNum][i];
/* 211 */       int nameIndex = this.cpBands.getCpDescriptorNameInts()[descriptorIndex];
/* 212 */       int typeIndex = this.cpBands.getCpDescriptorTypeInts()[descriptorIndex];
/* 213 */       CPUTF8 name = this.cpBands.cpUTF8Value(nameIndex);
/* 214 */       CPUTF8 descriptor = this.cpBands.cpSignatureValue(typeIndex);
/* 215 */       cfMethods[i] = cp.add((ClassFileEntry)new CPMethod(name, descriptor, this.classBands.getMethodFlags()[classNum][i], this.classBands
/* 216 */             .getMethodAttributes()[classNum][i]));
/*     */     } 
/* 218 */     cp.addNestedEntries();
/*     */ 
/*     */     
/* 221 */     boolean addInnerClassesAttr = false;
/* 222 */     IcTuple[] ic_local = getClassBands().getIcLocal()[classNum];
/* 223 */     boolean ic_local_sent = (ic_local != null);
/* 224 */     InnerClassesAttribute innerClassesAttribute = new InnerClassesAttribute("InnerClasses");
/* 225 */     IcTuple[] ic_relevant = getIcBands().getRelevantIcTuples(fullName, cp);
/* 226 */     List<IcTuple> ic_stored = computeIcStored(ic_local, ic_relevant);
/* 227 */     for (int m = 0; m < ic_stored.size(); m++) {
/* 228 */       IcTuple icStored = ic_stored.get(m);
/* 229 */       int innerClassIndex = icStored.thisClassIndex();
/* 230 */       int outerClassIndex = icStored.outerClassIndex();
/* 231 */       int simpleClassNameIndex = icStored.simpleClassNameIndex();
/*     */       
/* 233 */       String innerClassString = icStored.thisClassString();
/* 234 */       String outerClassString = icStored.outerClassString();
/* 235 */       String simpleClassName = icStored.simpleClassName();
/*     */       
/* 237 */       CPClass innerClass = null;
/* 238 */       CPUTF8 innerName = null;
/* 239 */       CPClass outerClass = null;
/*     */ 
/*     */       
/* 242 */       innerClass = (innerClassIndex != -1) ? this.cpBands.cpClassValue(innerClassIndex) : this.cpBands.cpClassValue(innerClassString);
/* 243 */       if (!icStored.isAnonymous())
/*     */       {
/* 245 */         innerName = (simpleClassNameIndex != -1) ? this.cpBands.cpUTF8Value(simpleClassNameIndex) : this.cpBands.cpUTF8Value(simpleClassName);
/*     */       }
/*     */       
/* 248 */       if (icStored.isMember())
/*     */       {
/* 250 */         outerClass = (outerClassIndex != -1) ? this.cpBands.cpClassValue(outerClassIndex) : this.cpBands.cpClassValue(outerClassString);
/*     */       }
/* 252 */       int flags = icStored.F;
/* 253 */       innerClassesAttribute.addInnerClassesEntry(innerClass, outerClass, innerName, flags);
/* 254 */       addInnerClassesAttr = true;
/*     */     } 
/*     */ 
/*     */     
/* 258 */     if (ic_local_sent && ic_local.length == 0) {
/* 259 */       addInnerClassesAttr = false;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 264 */     if (!ic_local_sent && ic_relevant.length == 0) {
/* 265 */       addInnerClassesAttr = false;
/*     */     }
/*     */     
/* 268 */     if (addInnerClassesAttr) {
/*     */ 
/*     */       
/* 271 */       Attribute[] originalAttrs = classFile.attributes;
/* 272 */       Attribute[] newAttrs = new Attribute[originalAttrs.length + 1];
/* 273 */       for (int n = 0; n < originalAttrs.length; n++) {
/* 274 */         newAttrs[n] = originalAttrs[n];
/*     */       }
/* 276 */       newAttrs[newAttrs.length - 1] = (Attribute)innerClassesAttribute;
/* 277 */       classFile.attributes = newAttrs;
/* 278 */       cp.addWithNestedEntries((ClassFileEntry)innerClassesAttribute);
/*     */     } 
/*     */     
/* 281 */     cp.resolve(this);
/*     */ 
/*     */     
/* 284 */     classFile.accessFlags = (int)this.classBands.getClassFlags()[classNum];
/* 285 */     classFile.thisClass = cp.indexOf(cfThis);
/* 286 */     classFile.superClass = cp.indexOf(cfSuper);
/*     */     
/* 288 */     classFile.interfaces = new int[cfInterfaces.length];
/* 289 */     for (i = 0; i < cfInterfaces.length; i++) {
/* 290 */       classFile.interfaces[i] = cp.indexOf(cfInterfaces[i]);
/*     */     }
/* 292 */     classFile.fields = cfFields;
/* 293 */     classFile.methods = cfMethods;
/* 294 */     return classFile;
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
/*     */   private List computeIcStored(IcTuple[] ic_local, IcTuple[] ic_relevant) {
/* 306 */     List<IcTuple> result = new ArrayList(ic_relevant.length);
/* 307 */     List<IcTuple> duplicates = new ArrayList(ic_relevant.length);
/* 308 */     Set<IcTuple> isInResult = new HashSet(ic_relevant.length);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 314 */     if (ic_local != null) {
/* 315 */       for (int i = 0; i < ic_local.length; i++) {
/* 316 */         if (isInResult.add(ic_local[i])) {
/* 317 */           result.add(ic_local[i]);
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*     */     int index;
/* 323 */     for (index = 0; index < ic_relevant.length; index++) {
/* 324 */       if (isInResult.add(ic_relevant[index])) {
/* 325 */         result.add(ic_relevant[index]);
/*     */       } else {
/* 327 */         duplicates.add(ic_relevant[index]);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 332 */     for (index = 0; index < duplicates.size(); index++) {
/* 333 */       IcTuple tuple = duplicates.get(index);
/* 334 */       result.remove(tuple);
/*     */     } 
/*     */     
/* 337 */     return result;
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
/*     */   private void readSegment(InputStream in) throws IOException, Pack200Exception {
/* 349 */     log(2, "-------");
/* 350 */     this.cpBands = new CpBands(this);
/* 351 */     this.cpBands.read(in);
/* 352 */     this.attrDefinitionBands = new AttrDefinitionBands(this);
/* 353 */     this.attrDefinitionBands.read(in);
/* 354 */     this.icBands = new IcBands(this);
/* 355 */     this.icBands.read(in);
/* 356 */     this.classBands = new ClassBands(this);
/* 357 */     this.classBands.read(in);
/* 358 */     this.bcBands = new BcBands(this);
/* 359 */     this.bcBands.read(in);
/* 360 */     this.fileBands = new FileBands(this);
/* 361 */     this.fileBands.read(in);
/*     */     
/* 363 */     this.fileBands.processFileBits();
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
/*     */   private void parseSegment() throws IOException, Pack200Exception {
/* 375 */     this.header.unpack();
/* 376 */     this.cpBands.unpack();
/* 377 */     this.attrDefinitionBands.unpack();
/* 378 */     this.icBands.unpack();
/* 379 */     this.classBands.unpack();
/* 380 */     this.bcBands.unpack();
/* 381 */     this.fileBands.unpack();
/*     */     
/* 383 */     int classNum = 0;
/* 384 */     int numberOfFiles = this.header.getNumberOfFiles();
/* 385 */     String[] fileName = this.fileBands.getFileName();
/* 386 */     int[] fileOptions = this.fileBands.getFileOptions();
/* 387 */     SegmentOptions options = this.header.getOptions();
/*     */     
/* 389 */     this.classFilesContents = new byte[numberOfFiles][];
/* 390 */     this.fileDeflate = new boolean[numberOfFiles];
/* 391 */     this.fileIsClass = new boolean[numberOfFiles];
/*     */     
/* 393 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/* 394 */     DataOutputStream dos = new DataOutputStream(bos);
/*     */     
/* 396 */     for (int i = 0; i < numberOfFiles; i++) {
/* 397 */       String name = fileName[i];
/*     */       
/* 399 */       boolean nameIsEmpty = (name == null || name.equals(""));
/* 400 */       boolean isClass = ((fileOptions[i] & 0x2) == 2 || nameIsEmpty);
/* 401 */       if (isClass && nameIsEmpty) {
/* 402 */         name = this.cpBands.getCpClass()[this.classBands.getClassThisInts()[classNum]] + ".class";
/* 403 */         fileName[i] = name;
/*     */       } 
/*     */       
/* 406 */       if (!this.overrideDeflateHint) {
/* 407 */         this.fileDeflate[i] = ((fileOptions[i] & 0x1) == 1 || options.shouldDeflate());
/*     */       } else {
/* 409 */         this.fileDeflate[i] = this.deflateHint;
/*     */       } 
/*     */       
/* 412 */       this.fileIsClass[i] = isClass;
/*     */       
/* 414 */       if (isClass) {
/* 415 */         ClassFile classFile = buildClassFile(classNum);
/* 416 */         classFile.write(dos);
/* 417 */         dos.flush();
/*     */         
/* 419 */         this.classFilesContents[classNum] = bos.toByteArray();
/* 420 */         bos.reset();
/*     */         
/* 422 */         classNum++;
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
/*     */   public void unpack(InputStream in, JarOutputStream out) throws IOException, Pack200Exception {
/* 436 */     unpackRead(in);
/* 437 */     unpackProcess();
/* 438 */     unpackWrite(out);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void unpackRead(InputStream in) throws IOException, Pack200Exception {
/* 445 */     if (!in.markSupported()) {
/* 446 */       in = new BufferedInputStream(in);
/*     */     }
/*     */     
/* 449 */     this.header = new SegmentHeader(this);
/* 450 */     this.header.read(in);
/*     */     
/* 452 */     int size = (int)this.header.getArchiveSize() - this.header.getArchiveSizeOffset();
/*     */     
/* 454 */     if (this.doPreRead && this.header.getArchiveSize() != 0L) {
/* 455 */       byte[] data = new byte[size];
/* 456 */       in.read(data);
/* 457 */       this.internalBuffer = new BufferedInputStream(new ByteArrayInputStream(data));
/*     */     } else {
/* 459 */       readSegment(in);
/*     */     } 
/*     */   }
/*     */   
/*     */   void unpackProcess() throws IOException, Pack200Exception {
/* 464 */     if (this.internalBuffer != null) {
/* 465 */       readSegment(this.internalBuffer);
/*     */     }
/* 467 */     parseSegment();
/*     */   }
/*     */   
/*     */   void unpackWrite(JarOutputStream out) throws IOException, Pack200Exception {
/* 471 */     writeJar(out);
/* 472 */     if (this.logStream != null) {
/* 473 */       this.logStream.close();
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
/*     */   public void writeJar(JarOutputStream out) throws IOException, Pack200Exception {
/* 488 */     String[] fileName = this.fileBands.getFileName();
/* 489 */     int[] fileModtime = this.fileBands.getFileModtime();
/* 490 */     long[] fileSize = this.fileBands.getFileSize();
/* 491 */     byte[][] fileBits = this.fileBands.getFileBits();
/*     */ 
/*     */     
/* 494 */     int classNum = 0;
/* 495 */     int numberOfFiles = this.header.getNumberOfFiles();
/* 496 */     long archiveModtime = this.header.getArchiveModtime();
/*     */     
/* 498 */     for (int i = 0; i < numberOfFiles; i++) {
/* 499 */       String name = fileName[i];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 506 */       long modtime = 1000L * (archiveModtime + fileModtime[i]);
/* 507 */       boolean deflate = this.fileDeflate[i];
/*     */       
/* 509 */       JarEntry entry = new JarEntry(name);
/* 510 */       if (deflate) {
/* 511 */         entry.setMethod(8);
/*     */       } else {
/* 513 */         entry.setMethod(0);
/* 514 */         CRC32 crc = new CRC32();
/* 515 */         if (this.fileIsClass[i]) {
/* 516 */           crc.update(this.classFilesContents[classNum]);
/* 517 */           entry.setSize((this.classFilesContents[classNum]).length);
/*     */         } else {
/* 519 */           crc.update(fileBits[i]);
/* 520 */           entry.setSize(fileSize[i]);
/*     */         } 
/* 522 */         entry.setCrc(crc.getValue());
/*     */       } 
/*     */       
/* 525 */       entry.setTime(modtime - TimeZone.getDefault().getRawOffset());
/* 526 */       out.putNextEntry(entry);
/*     */ 
/*     */       
/* 529 */       if (this.fileIsClass[i]) {
/* 530 */         entry.setSize((this.classFilesContents[classNum]).length);
/* 531 */         out.write(this.classFilesContents[classNum]);
/* 532 */         classNum++;
/*     */       } else {
/* 534 */         entry.setSize(fileSize[i]);
/* 535 */         out.write(fileBits[i]);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public SegmentConstantPool getConstantPool() {
/* 541 */     return this.cpBands.getConstantPool();
/*     */   }
/*     */   
/*     */   public SegmentHeader getSegmentHeader() {
/* 545 */     return this.header;
/*     */   }
/*     */   
/*     */   public void setPreRead(boolean value) {
/* 549 */     this.doPreRead = value;
/*     */   }
/*     */   
/*     */   protected AttrDefinitionBands getAttrDefinitionBands() {
/* 553 */     return this.attrDefinitionBands;
/*     */   }
/*     */   
/*     */   protected ClassBands getClassBands() {
/* 557 */     return this.classBands;
/*     */   }
/*     */   
/*     */   protected CpBands getCpBands() {
/* 561 */     return this.cpBands;
/*     */   }
/*     */   
/*     */   protected IcBands getIcBands() {
/* 565 */     return this.icBands;
/*     */   }
/*     */   
/*     */   public void setLogLevel(int logLevel) {
/* 569 */     this.logLevel = logLevel;
/*     */   }
/*     */   
/*     */   public void setLogStream(OutputStream logStream) {
/* 573 */     this.logStream = new PrintWriter(logStream);
/*     */   }
/*     */   
/*     */   public void log(int logLevel, String message) {
/* 577 */     if (this.logLevel >= logLevel) {
/* 578 */       this.logStream.println(message);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void overrideDeflateHint(boolean deflateHint) {
/* 588 */     this.overrideDeflateHint = true;
/* 589 */     this.deflateHint = deflateHint;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\Segment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */