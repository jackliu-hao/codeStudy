/*     */ package org.objectweb.asm;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class ModuleWriter
/*     */   extends ModuleVisitor
/*     */ {
/*     */   private final SymbolTable symbolTable;
/*     */   private final int moduleNameIndex;
/*     */   private final int moduleFlags;
/*     */   private final int moduleVersionIndex;
/*     */   private int requiresCount;
/*     */   private final ByteVector requires;
/*     */   private int exportsCount;
/*     */   private final ByteVector exports;
/*     */   private int opensCount;
/*     */   private final ByteVector opens;
/*     */   private int usesCount;
/*     */   private final ByteVector usesIndex;
/*     */   private int providesCount;
/*     */   private final ByteVector provides;
/*     */   private int packageCount;
/*     */   private final ByteVector packageIndex;
/*     */   private int mainClassIndex;
/*     */   
/*     */   ModuleWriter(SymbolTable symbolTable, int name, int access, int version) {
/*  97 */     super(589824);
/*  98 */     this.symbolTable = symbolTable;
/*  99 */     this.moduleNameIndex = name;
/* 100 */     this.moduleFlags = access;
/* 101 */     this.moduleVersionIndex = version;
/* 102 */     this.requires = new ByteVector();
/* 103 */     this.exports = new ByteVector();
/* 104 */     this.opens = new ByteVector();
/* 105 */     this.usesIndex = new ByteVector();
/* 106 */     this.provides = new ByteVector();
/* 107 */     this.packageIndex = new ByteVector();
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitMainClass(String mainClass) {
/* 112 */     this.mainClassIndex = (this.symbolTable.addConstantClass(mainClass)).index;
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitPackage(String packaze) {
/* 117 */     this.packageIndex.putShort((this.symbolTable.addConstantPackage(packaze)).index);
/* 118 */     this.packageCount++;
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitRequire(String module, int access, String version) {
/* 123 */     this.requires
/* 124 */       .putShort((this.symbolTable.addConstantModule(module)).index)
/* 125 */       .putShort(access)
/* 126 */       .putShort((version == null) ? 0 : this.symbolTable.addConstantUtf8(version));
/* 127 */     this.requiresCount++;
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitExport(String packaze, int access, String... modules) {
/* 132 */     this.exports.putShort((this.symbolTable.addConstantPackage(packaze)).index).putShort(access);
/* 133 */     if (modules == null) {
/* 134 */       this.exports.putShort(0);
/*     */     } else {
/* 136 */       this.exports.putShort(modules.length);
/* 137 */       for (String module : modules) {
/* 138 */         this.exports.putShort((this.symbolTable.addConstantModule(module)).index);
/*     */       }
/*     */     } 
/* 141 */     this.exportsCount++;
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitOpen(String packaze, int access, String... modules) {
/* 146 */     this.opens.putShort((this.symbolTable.addConstantPackage(packaze)).index).putShort(access);
/* 147 */     if (modules == null) {
/* 148 */       this.opens.putShort(0);
/*     */     } else {
/* 150 */       this.opens.putShort(modules.length);
/* 151 */       for (String module : modules) {
/* 152 */         this.opens.putShort((this.symbolTable.addConstantModule(module)).index);
/*     */       }
/*     */     } 
/* 155 */     this.opensCount++;
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitUse(String service) {
/* 160 */     this.usesIndex.putShort((this.symbolTable.addConstantClass(service)).index);
/* 161 */     this.usesCount++;
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitProvide(String service, String... providers) {
/* 166 */     this.provides.putShort((this.symbolTable.addConstantClass(service)).index);
/* 167 */     this.provides.putShort(providers.length);
/* 168 */     for (String provider : providers) {
/* 169 */       this.provides.putShort((this.symbolTable.addConstantClass(provider)).index);
/*     */     }
/* 171 */     this.providesCount++;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitEnd() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getAttributeCount() {
/* 186 */     return 1 + ((this.packageCount > 0) ? 1 : 0) + ((this.mainClassIndex > 0) ? 1 : 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int computeAttributesSize() {
/* 196 */     this.symbolTable.addConstantUtf8("Module");
/*     */     
/* 198 */     int size = 22 + this.requires.length + this.exports.length + this.opens.length + this.usesIndex.length + this.provides.length;
/*     */     
/* 200 */     if (this.packageCount > 0) {
/* 201 */       this.symbolTable.addConstantUtf8("ModulePackages");
/*     */       
/* 203 */       size += 8 + this.packageIndex.length;
/*     */     } 
/* 205 */     if (this.mainClassIndex > 0) {
/* 206 */       this.symbolTable.addConstantUtf8("ModuleMainClass");
/*     */       
/* 208 */       size += 8;
/*     */     } 
/* 210 */     return size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void putAttributes(ByteVector output) {
/* 221 */     int moduleAttributeLength = 16 + this.requires.length + this.exports.length + this.opens.length + this.usesIndex.length + this.provides.length;
/*     */     
/* 223 */     output
/* 224 */       .putShort(this.symbolTable.addConstantUtf8("Module"))
/* 225 */       .putInt(moduleAttributeLength)
/* 226 */       .putShort(this.moduleNameIndex)
/* 227 */       .putShort(this.moduleFlags)
/* 228 */       .putShort(this.moduleVersionIndex)
/* 229 */       .putShort(this.requiresCount)
/* 230 */       .putByteArray(this.requires.data, 0, this.requires.length)
/* 231 */       .putShort(this.exportsCount)
/* 232 */       .putByteArray(this.exports.data, 0, this.exports.length)
/* 233 */       .putShort(this.opensCount)
/* 234 */       .putByteArray(this.opens.data, 0, this.opens.length)
/* 235 */       .putShort(this.usesCount)
/* 236 */       .putByteArray(this.usesIndex.data, 0, this.usesIndex.length)
/* 237 */       .putShort(this.providesCount)
/* 238 */       .putByteArray(this.provides.data, 0, this.provides.length);
/* 239 */     if (this.packageCount > 0) {
/* 240 */       output
/* 241 */         .putShort(this.symbolTable.addConstantUtf8("ModulePackages"))
/* 242 */         .putInt(2 + this.packageIndex.length)
/* 243 */         .putShort(this.packageCount)
/* 244 */         .putByteArray(this.packageIndex.data, 0, this.packageIndex.length);
/*     */     }
/* 246 */     if (this.mainClassIndex > 0)
/* 247 */       output
/* 248 */         .putShort(this.symbolTable.addConstantUtf8("ModuleMainClass"))
/* 249 */         .putInt(2)
/* 250 */         .putShort(this.mainClassIndex); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\ModuleWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */