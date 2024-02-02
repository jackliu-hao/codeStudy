/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExtensionRegistryLite
/*     */ {
/*     */   private static volatile boolean eagerlyParseMessageSets = false;
/*     */   private static boolean doFullRuntimeInheritanceCheck = true;
/*     */   static final String EXTENSION_CLASS_NAME = "com.google.protobuf.Extension";
/*     */   private static volatile ExtensionRegistryLite emptyRegistry;
/*     */   
/*     */   private static class ExtensionClassHolder
/*     */   {
/*  88 */     static final Class<?> INSTANCE = resolveExtensionClass();
/*     */ 
/*     */     
/*     */     static Class<?> resolveExtensionClass() {
/*     */       try {
/*  93 */         return Class.forName("com.google.protobuf.Extension");
/*  94 */       } catch (ClassNotFoundException e) {
/*     */         
/*  96 */         return null;
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public static boolean isEagerlyParseMessageSets() {
/* 102 */     return eagerlyParseMessageSets;
/*     */   }
/*     */   
/*     */   public static void setEagerlyParseMessageSets(boolean isEagerlyParse) {
/* 106 */     eagerlyParseMessageSets = isEagerlyParse;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExtensionRegistryLite newInstance() {
/* 116 */     return doFullRuntimeInheritanceCheck ? 
/* 117 */       ExtensionRegistryFactory.create() : new ExtensionRegistryLite();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExtensionRegistryLite getEmptyRegistry() {
/* 128 */     ExtensionRegistryLite result = emptyRegistry;
/* 129 */     if (result == null) {
/* 130 */       synchronized (ExtensionRegistryLite.class) {
/* 131 */         result = emptyRegistry;
/* 132 */         if (result == null)
/*     */         {
/*     */ 
/*     */           
/* 136 */           result = emptyRegistry = doFullRuntimeInheritanceCheck ? ExtensionRegistryFactory.createEmpty() : EMPTY_REGISTRY_LITE;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 141 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ExtensionRegistryLite getUnmodifiable() {
/* 147 */     return new ExtensionRegistryLite(this);
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
/*     */   public <ContainingType extends MessageLite> GeneratedMessageLite.GeneratedExtension<ContainingType, ?> findLiteExtensionByNumber(ContainingType containingTypeDefaultInstance, int fieldNumber) {
/* 159 */     return (GeneratedMessageLite.GeneratedExtension<ContainingType, ?>)this.extensionsByNumber
/* 160 */       .get(new ObjectIntPair(containingTypeDefaultInstance, fieldNumber));
/*     */   }
/*     */ 
/*     */   
/*     */   public final void add(GeneratedMessageLite.GeneratedExtension<?, ?> extension) {
/* 165 */     this.extensionsByNumber.put(new ObjectIntPair(extension
/* 166 */           .getContainingTypeDefaultInstance(), extension.getNumber()), extension);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void add(ExtensionLite<?, ?> extension) {
/* 175 */     if (GeneratedMessageLite.GeneratedExtension.class.isAssignableFrom(extension.getClass())) {
/* 176 */       add((GeneratedMessageLite.GeneratedExtension<?, ?>)extension);
/*     */     }
/* 178 */     if (doFullRuntimeInheritanceCheck && ExtensionRegistryFactory.isFullRegistry(this)) {
/*     */       try {
/* 180 */         getClass().getMethod("add", new Class[] { ExtensionClassHolder.INSTANCE }).invoke(this, new Object[] { extension });
/* 181 */       } catch (Exception e) {
/* 182 */         throw new IllegalArgumentException(
/* 183 */             String.format("Could not invoke ExtensionRegistry#add for %s", new Object[] { extension }), e);
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
/*     */   ExtensionRegistryLite() {
/* 195 */     this.extensionsByNumber = new HashMap<>();
/*     */   }
/*     */ 
/*     */   
/* 199 */   static final ExtensionRegistryLite EMPTY_REGISTRY_LITE = new ExtensionRegistryLite(true);
/*     */   
/*     */   ExtensionRegistryLite(ExtensionRegistryLite other) {
/* 202 */     if (other == EMPTY_REGISTRY_LITE) {
/* 203 */       this.extensionsByNumber = Collections.emptyMap();
/*     */     } else {
/* 205 */       this.extensionsByNumber = Collections.unmodifiableMap(other.extensionsByNumber);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private final Map<ObjectIntPair, GeneratedMessageLite.GeneratedExtension<?, ?>> extensionsByNumber;
/*     */   
/*     */   ExtensionRegistryLite(boolean empty) {
/* 213 */     this.extensionsByNumber = Collections.emptyMap();
/*     */   }
/*     */   
/*     */   private static final class ObjectIntPair
/*     */   {
/*     */     private final Object object;
/*     */     private final int number;
/*     */     
/*     */     ObjectIntPair(Object object, int number) {
/* 222 */       this.object = object;
/* 223 */       this.number = number;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 228 */       return System.identityHashCode(this.object) * 65535 + this.number;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 233 */       if (!(obj instanceof ObjectIntPair)) {
/* 234 */         return false;
/*     */       }
/* 236 */       ObjectIntPair other = (ObjectIntPair)obj;
/* 237 */       return (this.object == other.object && this.number == other.number);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\ExtensionRegistryLite.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */