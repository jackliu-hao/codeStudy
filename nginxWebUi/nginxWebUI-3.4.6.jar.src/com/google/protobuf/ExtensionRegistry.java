/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExtensionRegistry
/*     */   extends ExtensionRegistryLite
/*     */ {
/*     */   private final Map<String, ExtensionInfo> immutableExtensionsByName;
/*     */   private final Map<String, ExtensionInfo> mutableExtensionsByName;
/*     */   private final Map<DescriptorIntPair, ExtensionInfo> immutableExtensionsByNumber;
/*     */   private final Map<DescriptorIntPair, ExtensionInfo> mutableExtensionsByNumber;
/*     */   
/*     */   public static ExtensionRegistry newInstance() {
/*  94 */     return new ExtensionRegistry();
/*     */   }
/*     */ 
/*     */   
/*     */   public static ExtensionRegistry getEmptyRegistry() {
/*  99 */     return EMPTY_REGISTRY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExtensionRegistry getUnmodifiable() {
/* 106 */     return new ExtensionRegistry(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class ExtensionInfo
/*     */   {
/*     */     public final Descriptors.FieldDescriptor descriptor;
/*     */ 
/*     */     
/*     */     public final Message defaultInstance;
/*     */ 
/*     */ 
/*     */     
/*     */     private ExtensionInfo(Descriptors.FieldDescriptor descriptor) {
/* 121 */       this.descriptor = descriptor;
/* 122 */       this.defaultInstance = null;
/*     */     }
/*     */     
/*     */     private ExtensionInfo(Descriptors.FieldDescriptor descriptor, Message defaultInstance) {
/* 126 */       this.descriptor = descriptor;
/* 127 */       this.defaultInstance = defaultInstance;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ExtensionInfo findExtensionByName(String fullName) {
/* 134 */     return findImmutableExtensionByName(fullName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExtensionInfo findImmutableExtensionByName(String fullName) {
/* 144 */     return this.immutableExtensionsByName.get(fullName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExtensionInfo findMutableExtensionByName(String fullName) {
/* 154 */     return this.mutableExtensionsByName.get(fullName);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ExtensionInfo findExtensionByNumber(Descriptors.Descriptor containingType, int fieldNumber) {
/* 161 */     return findImmutableExtensionByNumber(containingType, fieldNumber);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExtensionInfo findImmutableExtensionByNumber(Descriptors.Descriptor containingType, int fieldNumber) {
/* 171 */     return this.immutableExtensionsByNumber.get(new DescriptorIntPair(containingType, fieldNumber));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExtensionInfo findMutableExtensionByNumber(Descriptors.Descriptor containingType, int fieldNumber) {
/* 181 */     return this.mutableExtensionsByNumber.get(new DescriptorIntPair(containingType, fieldNumber));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<ExtensionInfo> getAllMutableExtensionsByExtendedType(String fullName) {
/* 191 */     HashSet<ExtensionInfo> extensions = new HashSet<>();
/* 192 */     for (DescriptorIntPair pair : this.mutableExtensionsByNumber.keySet()) {
/* 193 */       if (pair.descriptor.getFullName().equals(fullName)) {
/* 194 */         extensions.add(this.mutableExtensionsByNumber.get(pair));
/*     */       }
/*     */     } 
/* 197 */     return extensions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<ExtensionInfo> getAllImmutableExtensionsByExtendedType(String fullName) {
/* 208 */     HashSet<ExtensionInfo> extensions = new HashSet<>();
/* 209 */     for (DescriptorIntPair pair : this.immutableExtensionsByNumber.keySet()) {
/* 210 */       if (pair.descriptor.getFullName().equals(fullName)) {
/* 211 */         extensions.add(this.immutableExtensionsByNumber.get(pair));
/*     */       }
/*     */     } 
/* 214 */     return extensions;
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(Extension<?, ?> extension) {
/* 219 */     if (extension.getExtensionType() != Extension.ExtensionType.IMMUTABLE && extension
/* 220 */       .getExtensionType() != Extension.ExtensionType.MUTABLE) {
/*     */       return;
/*     */     }
/*     */     
/* 224 */     add(newExtensionInfo(extension), extension.getExtensionType());
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(GeneratedMessage.GeneratedExtension<?, ?> extension) {
/* 229 */     add(extension);
/*     */   }
/*     */   
/*     */   static ExtensionInfo newExtensionInfo(Extension<?, ?> extension) {
/* 233 */     if (extension.getDescriptor().getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 234 */       if (extension.getMessageDefaultInstance() == null) {
/* 235 */         throw new IllegalStateException("Registered message-type extension had null default instance: " + extension
/*     */             
/* 237 */             .getDescriptor().getFullName());
/*     */       }
/* 239 */       return new ExtensionInfo(extension
/* 240 */           .getDescriptor(), extension.getMessageDefaultInstance());
/*     */     } 
/* 242 */     return new ExtensionInfo(extension.getDescriptor(), null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(Descriptors.FieldDescriptor type) {
/* 248 */     if (type.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 249 */       throw new IllegalArgumentException("ExtensionRegistry.add() must be provided a default instance when adding an embedded message extension.");
/*     */     }
/*     */ 
/*     */     
/* 253 */     ExtensionInfo info = new ExtensionInfo(type, null);
/* 254 */     add(info, Extension.ExtensionType.IMMUTABLE);
/* 255 */     add(info, Extension.ExtensionType.MUTABLE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(Descriptors.FieldDescriptor type, Message defaultInstance) {
/* 260 */     if (type.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 261 */       throw new IllegalArgumentException("ExtensionRegistry.add() provided a default instance for a non-message extension.");
/*     */     }
/*     */     
/* 264 */     add(new ExtensionInfo(type, defaultInstance), Extension.ExtensionType.IMMUTABLE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ExtensionRegistry() {
/* 271 */     this.immutableExtensionsByName = new HashMap<>();
/* 272 */     this.mutableExtensionsByName = new HashMap<>();
/* 273 */     this.immutableExtensionsByNumber = new HashMap<>();
/* 274 */     this.mutableExtensionsByNumber = new HashMap<>();
/*     */   }
/*     */   
/*     */   private ExtensionRegistry(ExtensionRegistry other) {
/* 278 */     super(other);
/* 279 */     this.immutableExtensionsByName = Collections.unmodifiableMap(other.immutableExtensionsByName);
/* 280 */     this.mutableExtensionsByName = Collections.unmodifiableMap(other.mutableExtensionsByName);
/* 281 */     this
/* 282 */       .immutableExtensionsByNumber = Collections.unmodifiableMap(other.immutableExtensionsByNumber);
/* 283 */     this.mutableExtensionsByNumber = Collections.unmodifiableMap(other.mutableExtensionsByNumber);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ExtensionRegistry(boolean empty) {
/* 292 */     super(EMPTY_REGISTRY_LITE);
/* 293 */     this.immutableExtensionsByName = Collections.emptyMap();
/* 294 */     this.mutableExtensionsByName = Collections.emptyMap();
/* 295 */     this.immutableExtensionsByNumber = Collections.emptyMap();
/* 296 */     this.mutableExtensionsByNumber = Collections.emptyMap();
/*     */   }
/*     */   
/* 299 */   static final ExtensionRegistry EMPTY_REGISTRY = new ExtensionRegistry(true); private void add(ExtensionInfo extension, Extension.ExtensionType extensionType) {
/*     */     Map<String, ExtensionInfo> extensionsByName;
/*     */     Map<DescriptorIntPair, ExtensionInfo> extensionsByNumber;
/* 302 */     if (!extension.descriptor.isExtension()) {
/* 303 */       throw new IllegalArgumentException("ExtensionRegistry.add() was given a FieldDescriptor for a regular (non-extension) field.");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 310 */     switch (extensionType) {
/*     */       case IMMUTABLE:
/* 312 */         extensionsByName = this.immutableExtensionsByName;
/* 313 */         extensionsByNumber = this.immutableExtensionsByNumber;
/*     */         break;
/*     */       case MUTABLE:
/* 316 */         extensionsByName = this.mutableExtensionsByName;
/* 317 */         extensionsByNumber = this.mutableExtensionsByNumber;
/*     */         break;
/*     */       
/*     */       default:
/*     */         return;
/*     */     } 
/*     */     
/* 324 */     extensionsByName.put(extension.descriptor.getFullName(), extension);
/* 325 */     extensionsByNumber.put(new DescriptorIntPair(extension.descriptor
/*     */           
/* 327 */           .getContainingType(), extension.descriptor.getNumber()), extension);
/*     */ 
/*     */     
/* 330 */     Descriptors.FieldDescriptor field = extension.descriptor;
/* 331 */     if (field.getContainingType().getOptions().getMessageSetWireFormat() && field
/* 332 */       .getType() == Descriptors.FieldDescriptor.Type.MESSAGE && field
/* 333 */       .isOptional() && field
/* 334 */       .getExtensionScope() == field.getMessageType())
/*     */     {
/*     */ 
/*     */       
/* 338 */       extensionsByName.put(field.getMessageType().getFullName(), extension);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class DescriptorIntPair
/*     */   {
/*     */     private final Descriptors.Descriptor descriptor;
/*     */     private final int number;
/*     */     
/*     */     DescriptorIntPair(Descriptors.Descriptor descriptor, int number) {
/* 348 */       this.descriptor = descriptor;
/* 349 */       this.number = number;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 354 */       return this.descriptor.hashCode() * 65535 + this.number;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 359 */       if (!(obj instanceof DescriptorIntPair)) {
/* 360 */         return false;
/*     */       }
/* 362 */       DescriptorIntPair other = (DescriptorIntPair)obj;
/* 363 */       return (this.descriptor == other.descriptor && this.number == other.number);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\ExtensionRegistry.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */