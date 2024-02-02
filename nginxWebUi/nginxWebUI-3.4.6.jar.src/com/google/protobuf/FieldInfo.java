/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class FieldInfo
/*     */   implements Comparable<FieldInfo>
/*     */ {
/*     */   private final Field field;
/*     */   private final FieldType type;
/*     */   private final Class<?> messageClass;
/*     */   private final int fieldNumber;
/*     */   private final Field presenceField;
/*     */   private final int presenceMask;
/*     */   private final boolean required;
/*     */   private final boolean enforceUtf8;
/*     */   private final OneofInfo oneof;
/*     */   private final Field cachedSizeField;
/*     */   private final Class<?> oneofStoredType;
/*     */   private final Object mapDefaultEntry;
/*     */   private final Internal.EnumVerifier enumVerifier;
/*     */   
/*     */   public static FieldInfo forField(Field field, int fieldNumber, FieldType fieldType, boolean enforceUtf8) {
/*  66 */     checkFieldNumber(fieldNumber);
/*  67 */     Internal.checkNotNull(field, "field");
/*  68 */     Internal.checkNotNull(fieldType, "fieldType");
/*  69 */     if (fieldType == FieldType.MESSAGE_LIST || fieldType == FieldType.GROUP_LIST) {
/*  70 */       throw new IllegalStateException("Shouldn't be called for repeated message fields.");
/*     */     }
/*  72 */     return new FieldInfo(field, fieldNumber, fieldType, null, null, 0, false, enforceUtf8, null, null, null, null, null);
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
/*     */   public static FieldInfo forPackedField(Field field, int fieldNumber, FieldType fieldType, Field cachedSizeField) {
/*  91 */     checkFieldNumber(fieldNumber);
/*  92 */     Internal.checkNotNull(field, "field");
/*  93 */     Internal.checkNotNull(fieldType, "fieldType");
/*  94 */     if (fieldType == FieldType.MESSAGE_LIST || fieldType == FieldType.GROUP_LIST) {
/*  95 */       throw new IllegalStateException("Shouldn't be called for repeated message fields.");
/*     */     }
/*  97 */     return new FieldInfo(field, fieldNumber, fieldType, null, null, 0, false, false, null, null, null, null, cachedSizeField);
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
/*     */   public static FieldInfo forRepeatedMessageField(Field field, int fieldNumber, FieldType fieldType, Class<?> messageClass) {
/* 116 */     checkFieldNumber(fieldNumber);
/* 117 */     Internal.checkNotNull(field, "field");
/* 118 */     Internal.checkNotNull(fieldType, "fieldType");
/* 119 */     Internal.checkNotNull(messageClass, "messageClass");
/* 120 */     return new FieldInfo(field, fieldNumber, fieldType, messageClass, null, 0, false, false, null, null, null, null, null);
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
/*     */   public static FieldInfo forFieldWithEnumVerifier(Field field, int fieldNumber, FieldType fieldType, Internal.EnumVerifier enumVerifier) {
/* 138 */     checkFieldNumber(fieldNumber);
/* 139 */     Internal.checkNotNull(field, "field");
/* 140 */     return new FieldInfo(field, fieldNumber, fieldType, null, null, 0, false, false, null, null, null, enumVerifier, null);
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
/*     */   public static FieldInfo forPackedFieldWithEnumVerifier(Field field, int fieldNumber, FieldType fieldType, Internal.EnumVerifier enumVerifier, Field cachedSizeField) {
/* 162 */     checkFieldNumber(fieldNumber);
/* 163 */     Internal.checkNotNull(field, "field");
/* 164 */     return new FieldInfo(field, fieldNumber, fieldType, null, null, 0, false, false, null, null, null, enumVerifier, cachedSizeField);
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
/*     */   public static FieldInfo forProto2OptionalField(Field field, int fieldNumber, FieldType fieldType, Field presenceField, int presenceMask, boolean enforceUtf8, Internal.EnumVerifier enumVerifier) {
/* 189 */     checkFieldNumber(fieldNumber);
/* 190 */     Internal.checkNotNull(field, "field");
/* 191 */     Internal.checkNotNull(fieldType, "fieldType");
/* 192 */     Internal.checkNotNull(presenceField, "presenceField");
/* 193 */     if (presenceField != null && !isExactlyOneBitSet(presenceMask)) {
/* 194 */       throw new IllegalArgumentException("presenceMask must have exactly one bit set: " + presenceMask);
/*     */     }
/*     */     
/* 197 */     return new FieldInfo(field, fieldNumber, fieldType, null, presenceField, presenceMask, false, enforceUtf8, null, null, null, enumVerifier, null);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FieldInfo forOneofMemberField(int fieldNumber, FieldType fieldType, OneofInfo oneof, Class<?> oneofStoredType, boolean enforceUtf8, Internal.EnumVerifier enumVerifier) {
/* 232 */     checkFieldNumber(fieldNumber);
/* 233 */     Internal.checkNotNull(fieldType, "fieldType");
/* 234 */     Internal.checkNotNull(oneof, "oneof");
/* 235 */     Internal.checkNotNull(oneofStoredType, "oneofStoredType");
/* 236 */     if (!fieldType.isScalar()) {
/* 237 */       throw new IllegalArgumentException("Oneof is only supported for scalar fields. Field " + fieldNumber + " is of type " + fieldType);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 243 */     return new FieldInfo(null, fieldNumber, fieldType, null, null, 0, false, enforceUtf8, oneof, oneofStoredType, null, enumVerifier, null);
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
/*     */   private static void checkFieldNumber(int fieldNumber) {
/* 260 */     if (fieldNumber <= 0) {
/* 261 */       throw new IllegalArgumentException("fieldNumber must be positive: " + fieldNumber);
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
/*     */   public static FieldInfo forProto2RequiredField(Field field, int fieldNumber, FieldType fieldType, Field presenceField, int presenceMask, boolean enforceUtf8, Internal.EnumVerifier enumVerifier) {
/* 274 */     checkFieldNumber(fieldNumber);
/* 275 */     Internal.checkNotNull(field, "field");
/* 276 */     Internal.checkNotNull(fieldType, "fieldType");
/* 277 */     Internal.checkNotNull(presenceField, "presenceField");
/* 278 */     if (presenceField != null && !isExactlyOneBitSet(presenceMask)) {
/* 279 */       throw new IllegalArgumentException("presenceMask must have exactly one bit set: " + presenceMask);
/*     */     }
/*     */     
/* 282 */     return new FieldInfo(field, fieldNumber, fieldType, null, presenceField, presenceMask, true, enforceUtf8, null, null, null, enumVerifier, null);
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
/*     */   public static FieldInfo forMapField(Field field, int fieldNumber, Object mapDefaultEntry, Internal.EnumVerifier enumVerifier) {
/* 300 */     Internal.checkNotNull(mapDefaultEntry, "mapDefaultEntry");
/* 301 */     checkFieldNumber(fieldNumber);
/* 302 */     Internal.checkNotNull(field, "field");
/* 303 */     return new FieldInfo(field, fieldNumber, FieldType.MAP, null, null, 0, false, true, null, null, mapDefaultEntry, enumVerifier, null);
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
/*     */ 
/*     */ 
/*     */   
/*     */   private FieldInfo(Field field, int fieldNumber, FieldType type, Class<?> messageClass, Field presenceField, int presenceMask, boolean required, boolean enforceUtf8, OneofInfo oneof, Class<?> oneofStoredType, Object mapDefaultEntry, Internal.EnumVerifier enumVerifier, Field cachedSizeField) {
/* 333 */     this.field = field;
/* 334 */     this.type = type;
/* 335 */     this.messageClass = messageClass;
/* 336 */     this.fieldNumber = fieldNumber;
/* 337 */     this.presenceField = presenceField;
/* 338 */     this.presenceMask = presenceMask;
/* 339 */     this.required = required;
/* 340 */     this.enforceUtf8 = enforceUtf8;
/* 341 */     this.oneof = oneof;
/* 342 */     this.oneofStoredType = oneofStoredType;
/* 343 */     this.mapDefaultEntry = mapDefaultEntry;
/* 344 */     this.enumVerifier = enumVerifier;
/* 345 */     this.cachedSizeField = cachedSizeField;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getFieldNumber() {
/* 350 */     return this.fieldNumber;
/*     */   }
/*     */ 
/*     */   
/*     */   public Field getField() {
/* 355 */     return this.field;
/*     */   }
/*     */ 
/*     */   
/*     */   public FieldType getType() {
/* 360 */     return this.type;
/*     */   }
/*     */ 
/*     */   
/*     */   public OneofInfo getOneof() {
/* 365 */     return this.oneof;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> getOneofStoredType() {
/* 374 */     return this.oneofStoredType;
/*     */   }
/*     */ 
/*     */   
/*     */   public Internal.EnumVerifier getEnumVerifier() {
/* 379 */     return this.enumVerifier;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(FieldInfo o) {
/* 384 */     return this.fieldNumber - o.fieldNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> getListElementType() {
/* 392 */     return this.messageClass;
/*     */   }
/*     */ 
/*     */   
/*     */   public Field getPresenceField() {
/* 397 */     return this.presenceField;
/*     */   }
/*     */   
/*     */   public Object getMapDefaultEntry() {
/* 401 */     return this.mapDefaultEntry;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPresenceMask() {
/* 409 */     return this.presenceMask;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRequired() {
/* 414 */     return this.required;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEnforceUtf8() {
/* 421 */     return this.enforceUtf8;
/*     */   }
/*     */   
/*     */   public Field getCachedSizeField() {
/* 425 */     return this.cachedSizeField;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> getMessageFieldClass() {
/* 433 */     switch (this.type) {
/*     */       case MESSAGE:
/*     */       case GROUP:
/* 436 */         return (this.field != null) ? this.field.getType() : this.oneofStoredType;
/*     */       case MESSAGE_LIST:
/*     */       case GROUP_LIST:
/* 439 */         return this.messageClass;
/*     */     } 
/* 441 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Builder newBuilder() {
/* 446 */     return new Builder();
/*     */   }
/*     */ 
/*     */   
/*     */   public static final class Builder
/*     */   {
/*     */     private Field field;
/*     */     
/*     */     private FieldType type;
/*     */     
/*     */     private int fieldNumber;
/*     */     
/*     */     private Field presenceField;
/*     */     private int presenceMask;
/*     */     private boolean required;
/*     */     private boolean enforceUtf8;
/*     */     private OneofInfo oneof;
/*     */     private Class<?> oneofStoredType;
/*     */     private Object mapDefaultEntry;
/*     */     private Internal.EnumVerifier enumVerifier;
/*     */     private Field cachedSizeField;
/*     */     
/*     */     private Builder() {}
/*     */     
/*     */     public Builder withField(Field field) {
/* 471 */       if (this.oneof != null) {
/* 472 */         throw new IllegalStateException("Cannot set field when building a oneof.");
/*     */       }
/* 474 */       this.field = field;
/* 475 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder withType(FieldType type) {
/* 480 */       this.type = type;
/* 481 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder withFieldNumber(int fieldNumber) {
/* 486 */       this.fieldNumber = fieldNumber;
/* 487 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder withPresence(Field presenceField, int presenceMask) {
/* 492 */       this.presenceField = Internal.<Field>checkNotNull(presenceField, "presenceField");
/* 493 */       this.presenceMask = presenceMask;
/* 494 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder withOneof(OneofInfo oneof, Class<?> oneofStoredType) {
/* 506 */       if (this.field != null || this.presenceField != null) {
/* 507 */         throw new IllegalStateException("Cannot set oneof when field or presenceField have been provided");
/*     */       }
/*     */       
/* 510 */       this.oneof = oneof;
/* 511 */       this.oneofStoredType = oneofStoredType;
/* 512 */       return this;
/*     */     }
/*     */     
/*     */     public Builder withRequired(boolean required) {
/* 516 */       this.required = required;
/* 517 */       return this;
/*     */     }
/*     */     
/*     */     public Builder withMapDefaultEntry(Object mapDefaultEntry) {
/* 521 */       this.mapDefaultEntry = mapDefaultEntry;
/* 522 */       return this;
/*     */     }
/*     */     
/*     */     public Builder withEnforceUtf8(boolean enforceUtf8) {
/* 526 */       this.enforceUtf8 = enforceUtf8;
/* 527 */       return this;
/*     */     }
/*     */     
/*     */     public Builder withEnumVerifier(Internal.EnumVerifier enumVerifier) {
/* 531 */       this.enumVerifier = enumVerifier;
/* 532 */       return this;
/*     */     }
/*     */     
/*     */     public Builder withCachedSizeField(Field cachedSizeField) {
/* 536 */       this.cachedSizeField = cachedSizeField;
/* 537 */       return this;
/*     */     }
/*     */     
/*     */     public FieldInfo build() {
/* 541 */       if (this.oneof != null) {
/* 542 */         return FieldInfo.forOneofMemberField(this.fieldNumber, this.type, this.oneof, this.oneofStoredType, this.enforceUtf8, this.enumVerifier);
/*     */       }
/*     */       
/* 545 */       if (this.mapDefaultEntry != null) {
/* 546 */         return FieldInfo.forMapField(this.field, this.fieldNumber, this.mapDefaultEntry, this.enumVerifier);
/*     */       }
/* 548 */       if (this.presenceField != null) {
/* 549 */         if (this.required) {
/* 550 */           return FieldInfo.forProto2RequiredField(this.field, this.fieldNumber, this.type, this.presenceField, this.presenceMask, this.enforceUtf8, this.enumVerifier);
/*     */         }
/*     */         
/* 553 */         return FieldInfo.forProto2OptionalField(this.field, this.fieldNumber, this.type, this.presenceField, this.presenceMask, this.enforceUtf8, this.enumVerifier);
/*     */       } 
/*     */ 
/*     */       
/* 557 */       if (this.enumVerifier != null) {
/* 558 */         if (this.cachedSizeField == null) {
/* 559 */           return FieldInfo.forFieldWithEnumVerifier(this.field, this.fieldNumber, this.type, this.enumVerifier);
/*     */         }
/* 561 */         return FieldInfo.forPackedFieldWithEnumVerifier(this.field, this.fieldNumber, this.type, this.enumVerifier, this.cachedSizeField);
/*     */       } 
/*     */ 
/*     */       
/* 565 */       if (this.cachedSizeField == null) {
/* 566 */         return FieldInfo.forField(this.field, this.fieldNumber, this.type, this.enforceUtf8);
/*     */       }
/* 568 */       return FieldInfo.forPackedField(this.field, this.fieldNumber, this.type, this.cachedSizeField);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isExactlyOneBitSet(int value) {
/* 575 */     return (value != 0 && (value & value - 1) == 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\FieldInfo.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */