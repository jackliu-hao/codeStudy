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
/*     */ public abstract class ClassVisitor
/*     */ {
/*     */   protected final int api;
/*     */   protected ClassVisitor cv;
/*     */   
/*     */   public ClassVisitor(int api) {
/*  58 */     this(api, null);
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
/*     */   public ClassVisitor(int api, ClassVisitor classVisitor) {
/*  71 */     if (api != 589824 && api != 524288 && api != 458752 && api != 393216 && api != 327680 && api != 262144 && api != 17432576)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  78 */       throw new IllegalArgumentException("Unsupported api " + api);
/*     */     }
/*  80 */     if (api == 17432576) {
/*  81 */       Constants.checkAsmExperimental(this);
/*     */     }
/*  83 */     this.api = api;
/*  84 */     this.cv = classVisitor;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
/* 111 */     if (this.api < 524288 && (access & 0x10000) != 0) {
/* 112 */       throw new UnsupportedOperationException("Records requires ASM8");
/*     */     }
/* 114 */     if (this.cv != null) {
/* 115 */       this.cv.visit(version, access, name, signature, superName, interfaces);
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
/*     */   public void visitSource(String source, String debug) {
/* 128 */     if (this.cv != null) {
/* 129 */       this.cv.visitSource(source, debug);
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
/*     */   public ModuleVisitor visitModule(String name, int access, String version) {
/* 144 */     if (this.api < 393216) {
/* 145 */       throw new UnsupportedOperationException("Module requires ASM6");
/*     */     }
/* 147 */     if (this.cv != null) {
/* 148 */       return this.cv.visitModule(name, access, version);
/*     */     }
/* 150 */     return null;
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
/*     */   public void visitNestHost(String nestHost) {
/* 164 */     if (this.api < 458752) {
/* 165 */       throw new UnsupportedOperationException("NestHost requires ASM7");
/*     */     }
/* 167 */     if (this.cv != null) {
/* 168 */       this.cv.visitNestHost(nestHost);
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
/*     */   public void visitOuterClass(String owner, String name, String descriptor) {
/* 183 */     if (this.cv != null) {
/* 184 */       this.cv.visitOuterClass(owner, name, descriptor);
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
/*     */   public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
/* 197 */     if (this.cv != null) {
/* 198 */       return this.cv.visitAnnotation(descriptor, visible);
/*     */     }
/* 200 */     return null;
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
/*     */   public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
/* 220 */     if (this.api < 327680) {
/* 221 */       throw new UnsupportedOperationException("TypeAnnotation requires ASM5");
/*     */     }
/* 223 */     if (this.cv != null) {
/* 224 */       return this.cv.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
/*     */     }
/* 226 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitAttribute(Attribute attribute) {
/* 235 */     if (this.cv != null) {
/* 236 */       this.cv.visitAttribute(attribute);
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
/*     */   public void visitNestMember(String nestMember) {
/* 250 */     if (this.api < 458752) {
/* 251 */       throw new UnsupportedOperationException("NestMember requires ASM7");
/*     */     }
/* 253 */     if (this.cv != null) {
/* 254 */       this.cv.visitNestMember(nestMember);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitPermittedSubclass(String permittedSubclass) {
/* 265 */     if (this.api < 589824) {
/* 266 */       throw new UnsupportedOperationException("PermittedSubclasses requires ASM9");
/*     */     }
/* 268 */     if (this.cv != null) {
/* 269 */       this.cv.visitPermittedSubclass(permittedSubclass);
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
/*     */   public void visitInnerClass(String name, String outerName, String innerName, int access) {
/* 287 */     if (this.cv != null) {
/* 288 */       this.cv.visitInnerClass(name, outerName, innerName, access);
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
/*     */   public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
/* 304 */     if (this.api < 524288) {
/* 305 */       throw new UnsupportedOperationException("Record requires ASM8");
/*     */     }
/* 307 */     if (this.cv != null) {
/* 308 */       return this.cv.visitRecordComponent(name, descriptor, signature);
/*     */     }
/* 310 */     return null;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
/* 337 */     if (this.cv != null) {
/* 338 */       return this.cv.visitField(access, name, descriptor, signature, value);
/*     */     }
/* 340 */     return null;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
/* 365 */     if (this.cv != null) {
/* 366 */       return this.cv.visitMethod(access, name, descriptor, signature, exceptions);
/*     */     }
/* 368 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitEnd() {
/* 376 */     if (this.cv != null)
/* 377 */       this.cv.visitEnd(); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\ClassVisitor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */