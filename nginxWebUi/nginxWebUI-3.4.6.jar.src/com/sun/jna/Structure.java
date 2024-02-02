/*      */ package com.sun.jna;
/*      */ 
/*      */ import java.lang.annotation.Documented;
/*      */ import java.lang.annotation.ElementType;
/*      */ import java.lang.annotation.Retention;
/*      */ import java.lang.annotation.RetentionPolicy;
/*      */ import java.lang.annotation.Target;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.nio.Buffer;
/*      */ import java.util.AbstractCollection;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.WeakHashMap;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class Structure
/*      */ {
/*  114 */   private static final Logger LOG = Logger.getLogger(Structure.class.getName());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int ALIGN_DEFAULT = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int ALIGN_NONE = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int ALIGN_GNUC = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int ALIGN_MSVC = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static final int CALCULATE_SIZE = -1;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  147 */   static final Map<Class<?>, LayoutInfo> layoutInfo = new WeakHashMap<Class<?>, LayoutInfo>();
/*  148 */   static final Map<Class<?>, List<String>> fieldOrder = new WeakHashMap<Class<?>, List<String>>();
/*      */   
/*      */   private Pointer memory;
/*      */   
/*  152 */   private int size = -1;
/*      */   
/*      */   private int alignType;
/*      */   
/*      */   private String encoding;
/*      */   private int actualAlignType;
/*      */   private int structAlignment;
/*      */   private Map<String, StructField> structFields;
/*  160 */   private final Map<String, Object> nativeStrings = new HashMap<String, Object>();
/*      */   
/*      */   private TypeMapper typeMapper;
/*      */   
/*      */   private long typeInfo;
/*      */   
/*      */   private boolean autoRead = true;
/*      */   private boolean autoWrite = true;
/*      */   private Structure[] array;
/*      */   private boolean readCalled;
/*      */   
/*      */   protected Structure() {
/*  172 */     this(0);
/*      */   }
/*      */   
/*      */   protected Structure(TypeMapper mapper) {
/*  176 */     this(null, 0, mapper);
/*      */   }
/*      */   
/*      */   protected Structure(int alignType) {
/*  180 */     this((Pointer)null, alignType);
/*      */   }
/*      */   
/*      */   protected Structure(int alignType, TypeMapper mapper) {
/*  184 */     this(null, alignType, mapper);
/*      */   }
/*      */ 
/*      */   
/*      */   protected Structure(Pointer p) {
/*  189 */     this(p, 0);
/*      */   }
/*      */   
/*      */   protected Structure(Pointer p, int alignType) {
/*  193 */     this(p, alignType, null);
/*      */   }
/*      */   
/*      */   protected Structure(Pointer p, int alignType, TypeMapper mapper) {
/*  197 */     setAlignType(alignType);
/*  198 */     setStringEncoding(Native.getStringEncoding(getClass()));
/*  199 */     initializeTypeMapper(mapper);
/*  200 */     validateFields();
/*  201 */     if (p != null) {
/*  202 */       useMemory(p, 0, true);
/*      */     } else {
/*      */       
/*  205 */       allocateMemory(-1);
/*      */     } 
/*  207 */     initializeFields();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Map<String, StructField> fields() {
/*  218 */     return this.structFields;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   TypeMapper getTypeMapper() {
/*  225 */     return this.typeMapper;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void initializeTypeMapper(TypeMapper mapper) {
/*  235 */     if (mapper == null) {
/*  236 */       mapper = Native.getTypeMapper(getClass());
/*      */     }
/*  238 */     this.typeMapper = mapper;
/*  239 */     layoutChanged();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void layoutChanged() {
/*  246 */     if (this.size != -1) {
/*  247 */       this.size = -1;
/*  248 */       if (this.memory instanceof AutoAllocated) {
/*  249 */         this.memory = null;
/*      */       }
/*      */       
/*  252 */       ensureAllocated();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setStringEncoding(String encoding) {
/*  261 */     this.encoding = encoding;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String getStringEncoding() {
/*  269 */     return this.encoding;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setAlignType(int alignType) {
/*  278 */     this.alignType = alignType;
/*  279 */     if (alignType == 0) {
/*  280 */       alignType = Native.getStructureAlignment(getClass());
/*  281 */       if (alignType == 0)
/*  282 */         if (Platform.isWindows()) {
/*  283 */           alignType = 3;
/*      */         } else {
/*  285 */           alignType = 2;
/*      */         }  
/*      */     } 
/*  288 */     this.actualAlignType = alignType;
/*  289 */     layoutChanged();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Memory autoAllocate(int size) {
/*  298 */     return new AutoAllocated(size);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void useMemory(Pointer m) {
/*  308 */     useMemory(m, 0);
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
/*      */   protected void useMemory(Pointer m, int offset) {
/*  320 */     useMemory(m, offset, false);
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
/*      */   void useMemory(Pointer m, int offset, boolean force) {
/*      */     try {
/*  336 */       this.nativeStrings.clear();
/*      */       
/*  338 */       if (this instanceof ByValue && !force) {
/*      */ 
/*      */         
/*  341 */         byte[] buf = new byte[size()];
/*  342 */         m.read(0L, buf, 0, buf.length);
/*  343 */         this.memory.write(0L, buf, 0, buf.length);
/*      */       
/*      */       }
/*      */       else {
/*      */         
/*  348 */         this.memory = m.share(offset);
/*  349 */         if (this.size == -1) {
/*  350 */           this.size = calculateSize(false);
/*      */         }
/*  352 */         if (this.size != -1) {
/*  353 */           this.memory = m.share(offset, this.size);
/*      */         }
/*      */       } 
/*  356 */       this.array = null;
/*  357 */       this.readCalled = false;
/*      */     }
/*  359 */     catch (IndexOutOfBoundsException e) {
/*  360 */       throw new IllegalArgumentException("Structure exceeds provided memory bounds", e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void ensureAllocated() {
/*  367 */     ensureAllocated(false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void ensureAllocated(boolean avoidFFIType) {
/*  376 */     if (this.memory == null) {
/*  377 */       allocateMemory(avoidFFIType);
/*      */     }
/*  379 */     else if (this.size == -1) {
/*  380 */       this.size = calculateSize(true, avoidFFIType);
/*  381 */       if (!(this.memory instanceof AutoAllocated)) {
/*      */         
/*      */         try {
/*  384 */           this.memory = this.memory.share(0L, this.size);
/*      */         }
/*  386 */         catch (IndexOutOfBoundsException e) {
/*  387 */           throw new IllegalArgumentException("Structure exceeds provided memory bounds", e);
/*      */         } 
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void allocateMemory() {
/*  397 */     allocateMemory(false);
/*      */   }
/*      */   
/*      */   private void allocateMemory(boolean avoidFFIType) {
/*  401 */     allocateMemory(calculateSize(true, avoidFFIType));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void allocateMemory(int size) {
/*  412 */     if (size == -1) {
/*      */       
/*  414 */       size = calculateSize(false);
/*      */     }
/*  416 */     else if (size <= 0) {
/*  417 */       throw new IllegalArgumentException("Structure size must be greater than zero: " + size);
/*      */     } 
/*      */ 
/*      */     
/*  421 */     if (size != -1) {
/*  422 */       if (this.memory == null || this.memory instanceof AutoAllocated)
/*      */       {
/*  424 */         this.memory = autoAllocate(size);
/*      */       }
/*  426 */       this.size = size;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int size() {
/*  434 */     ensureAllocated();
/*  435 */     return this.size;
/*      */   }
/*      */ 
/*      */   
/*      */   public void clear() {
/*  440 */     ensureAllocated();
/*  441 */     this.memory.clear(size());
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
/*      */   public Pointer getPointer() {
/*  455 */     ensureAllocated();
/*  456 */     return this.memory;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  465 */   private static final ThreadLocal<Map<Pointer, Structure>> reads = new ThreadLocal<Map<Pointer, Structure>>()
/*      */     {
/*      */       protected synchronized Map<Pointer, Structure> initialValue() {
/*  468 */         return new HashMap<Pointer, Structure>();
/*      */       }
/*      */     };
/*      */ 
/*      */ 
/*      */   
/*  474 */   private static final ThreadLocal<Set<Structure>> busy = new ThreadLocal<Set<Structure>>()
/*      */     {
/*      */       protected synchronized Set<Structure> initialValue() {
/*  477 */         return new Structure.StructureSet();
/*      */       }
/*      */     };
/*      */   
/*      */   static class StructureSet
/*      */     extends AbstractCollection<Structure>
/*      */     implements Set<Structure> {
/*      */     Structure[] elements;
/*      */     private int count;
/*      */     
/*      */     private void ensureCapacity(int size) {
/*  488 */       if (this.elements == null) {
/*  489 */         this.elements = new Structure[size * 3 / 2];
/*      */       }
/*  491 */       else if (this.elements.length < size) {
/*  492 */         Structure[] e = new Structure[size * 3 / 2];
/*  493 */         System.arraycopy(this.elements, 0, e, 0, this.elements.length);
/*  494 */         this.elements = e;
/*      */       } 
/*      */     }
/*      */     public Structure[] getElements() {
/*  498 */       return this.elements;
/*      */     }
/*      */     public int size() {
/*  501 */       return this.count;
/*      */     }
/*      */     public boolean contains(Object o) {
/*  504 */       return (indexOf((Structure)o) != -1);
/*      */     }
/*      */     
/*      */     public boolean add(Structure o) {
/*  508 */       if (!contains(o)) {
/*  509 */         ensureCapacity(this.count + 1);
/*  510 */         this.elements[this.count++] = o;
/*      */       } 
/*  512 */       return true;
/*      */     }
/*      */     private int indexOf(Structure s1) {
/*  515 */       for (int i = 0; i < this.count; i++) {
/*  516 */         Structure s2 = this.elements[i];
/*  517 */         if (s1 == s2 || (s1
/*  518 */           .getClass() == s2.getClass() && s1
/*  519 */           .size() == s2.size() && s1
/*  520 */           .getPointer().equals(s2.getPointer()))) {
/*  521 */           return i;
/*      */         }
/*      */       } 
/*  524 */       return -1;
/*      */     }
/*      */     
/*      */     public boolean remove(Object o) {
/*  528 */       int idx = indexOf((Structure)o);
/*  529 */       if (idx != -1) {
/*  530 */         if (--this.count >= 0) {
/*  531 */           this.elements[idx] = this.elements[this.count];
/*  532 */           this.elements[this.count] = null;
/*      */         } 
/*  534 */         return true;
/*      */       } 
/*  536 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Iterator<Structure> iterator() {
/*  543 */       Structure[] e = new Structure[this.count];
/*  544 */       if (this.count > 0) {
/*  545 */         System.arraycopy(this.elements, 0, e, 0, this.count);
/*      */       }
/*  547 */       return Arrays.<Structure>asList(e).iterator();
/*      */     }
/*      */   }
/*      */   
/*      */   static Set<Structure> busy() {
/*  552 */     return busy.get();
/*      */   }
/*      */   static Map<Pointer, Structure> reading() {
/*  555 */     return reads.get();
/*      */   }
/*      */ 
/*      */   
/*      */   void conditionalAutoRead() {
/*  560 */     if (!this.readCalled) {
/*  561 */       autoRead();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void read() {
/*  570 */     if (this.memory == PLACEHOLDER_MEMORY) {
/*      */       return;
/*      */     }
/*  573 */     this.readCalled = true;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  579 */     ensureAllocated();
/*      */ 
/*      */     
/*  582 */     if (busy().contains(this)) {
/*      */       return;
/*      */     }
/*  585 */     busy().add(this);
/*  586 */     if (this instanceof ByReference) {
/*  587 */       reading().put(getPointer(), this);
/*      */     }
/*      */     try {
/*  590 */       for (StructField structField : fields().values()) {
/*  591 */         readField(structField);
/*      */       }
/*      */     } finally {
/*      */       
/*  595 */       busy().remove(this);
/*  596 */       if (reading().get(getPointer()) == this) {
/*  597 */         reading().remove(getPointer());
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int fieldOffset(String name) {
/*  607 */     ensureAllocated();
/*  608 */     StructField f = fields().get(name);
/*  609 */     if (f == null) {
/*  610 */       throw new IllegalArgumentException("No such field: " + name);
/*      */     }
/*  612 */     return f.offset;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object readField(String name) {
/*  622 */     ensureAllocated();
/*  623 */     StructField f = fields().get(name);
/*  624 */     if (f == null)
/*  625 */       throw new IllegalArgumentException("No such field: " + name); 
/*  626 */     return readField(f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Object getFieldValue(Field field) {
/*      */     try {
/*  636 */       return field.get(this);
/*      */     }
/*  638 */     catch (Exception e) {
/*  639 */       throw new Error("Exception reading field '" + field.getName() + "' in " + getClass(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void setFieldValue(Field field, Object value) {
/*  648 */     setFieldValue(field, value, false);
/*      */   }
/*      */ 
/*      */   
/*      */   private void setFieldValue(Field field, Object value, boolean overrideFinal) {
/*      */     try {
/*  654 */       field.set(this, value);
/*      */     }
/*  656 */     catch (IllegalAccessException e) {
/*  657 */       int modifiers = field.getModifiers();
/*  658 */       if (Modifier.isFinal(modifiers)) {
/*  659 */         if (overrideFinal)
/*      */         {
/*      */           
/*  662 */           throw new UnsupportedOperationException("This VM does not support Structures with final fields (field '" + field.getName() + "' within " + getClass() + ")", e);
/*      */         }
/*  664 */         throw new UnsupportedOperationException("Attempt to write to read-only field '" + field.getName() + "' within " + getClass(), e);
/*      */       } 
/*  666 */       throw new Error("Unexpectedly unable to write to field '" + field.getName() + "' within " + getClass(), e);
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
/*      */   static <T extends Structure> T updateStructureByReference(Class<T> type, T s, Pointer address) {
/*  678 */     if (address == null) {
/*  679 */       s = null;
/*      */     
/*      */     }
/*  682 */     else if (s == null || !address.equals(s.getPointer())) {
/*  683 */       Structure s1 = reading().get(address);
/*  684 */       if (s1 != null && type.equals(s1.getClass())) {
/*  685 */         Structure structure = s1;
/*  686 */         structure.autoRead();
/*      */       } else {
/*      */         
/*  689 */         s = newInstance(type, address);
/*  690 */         s.conditionalAutoRead();
/*      */       } 
/*      */     } else {
/*      */       
/*  694 */       s.autoRead();
/*      */     } 
/*      */     
/*  697 */     return s;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object readField(StructField structField) {
/*      */     Object result;
/*  709 */     int offset = structField.offset;
/*      */ 
/*      */     
/*  712 */     Class<?> fieldType = structField.type;
/*  713 */     FromNativeConverter readConverter = structField.readConverter;
/*  714 */     if (readConverter != null) {
/*  715 */       fieldType = readConverter.nativeType();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  724 */     Object currentValue = (Structure.class.isAssignableFrom(fieldType) || Callback.class.isAssignableFrom(fieldType) || (Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(fieldType)) || Pointer.class.isAssignableFrom(fieldType) || NativeMapped.class.isAssignableFrom(fieldType) || fieldType.isArray()) ? getFieldValue(structField.field) : null;
/*      */ 
/*      */     
/*  727 */     if (fieldType == String.class) {
/*  728 */       Pointer p = this.memory.getPointer(offset);
/*  729 */       result = (p == null) ? null : p.getString(0L, this.encoding);
/*      */     } else {
/*      */       
/*  732 */       result = this.memory.getValue(offset, fieldType, currentValue);
/*      */     } 
/*  734 */     if (readConverter != null) {
/*  735 */       result = readConverter.fromNative(result, structField.context);
/*  736 */       if (currentValue != null && currentValue.equals(result)) {
/*  737 */         result = currentValue;
/*      */       }
/*      */     } 
/*      */     
/*  741 */     if (fieldType.equals(String.class) || fieldType
/*  742 */       .equals(WString.class)) {
/*  743 */       this.nativeStrings.put(structField.name + ".ptr", this.memory.getPointer(offset));
/*  744 */       this.nativeStrings.put(structField.name + ".val", result);
/*      */     } 
/*      */ 
/*      */     
/*  748 */     setFieldValue(structField.field, result, true);
/*  749 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void write() {
/*  757 */     if (this.memory == PLACEHOLDER_MEMORY) {
/*      */       return;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  764 */     ensureAllocated();
/*      */ 
/*      */     
/*  767 */     if (this instanceof ByValue) {
/*  768 */       getTypeInfo();
/*      */     }
/*      */ 
/*      */     
/*  772 */     if (busy().contains(this)) {
/*      */       return;
/*      */     }
/*  775 */     busy().add(this);
/*      */     
/*      */     try {
/*  778 */       for (StructField sf : fields().values()) {
/*  779 */         if (!sf.isVolatile) {
/*  780 */           writeField(sf);
/*      */         }
/*      */       } 
/*      */     } finally {
/*      */       
/*  785 */       busy().remove(this);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeField(String name) {
/*  795 */     ensureAllocated();
/*  796 */     StructField f = fields().get(name);
/*  797 */     if (f == null)
/*  798 */       throw new IllegalArgumentException("No such field: " + name); 
/*  799 */     writeField(f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeField(String name, Object value) {
/*  810 */     ensureAllocated();
/*  811 */     StructField structField = fields().get(name);
/*  812 */     if (structField == null)
/*  813 */       throw new IllegalArgumentException("No such field: " + name); 
/*  814 */     setFieldValue(structField.field, value);
/*  815 */     writeField(structField);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void writeField(StructField structField) {
/*  823 */     if (structField.isReadOnly) {
/*      */       return;
/*      */     }
/*      */     
/*  827 */     int offset = structField.offset;
/*      */ 
/*      */     
/*  830 */     Object value = getFieldValue(structField.field);
/*      */ 
/*      */     
/*  833 */     Class<?> fieldType = structField.type;
/*  834 */     ToNativeConverter converter = structField.writeConverter;
/*  835 */     if (converter != null) {
/*  836 */       value = converter.toNative(value, new StructureWriteContext(this, structField.field));
/*  837 */       fieldType = converter.nativeType();
/*      */     } 
/*      */ 
/*      */     
/*  841 */     if (String.class == fieldType || WString.class == fieldType) {
/*      */ 
/*      */       
/*  844 */       boolean wide = (fieldType == WString.class);
/*  845 */       if (value != null) {
/*      */ 
/*      */         
/*  848 */         if (this.nativeStrings.containsKey(structField.name + ".ptr") && value
/*  849 */           .equals(this.nativeStrings.get(structField.name + ".val"))) {
/*      */           return;
/*      */         }
/*      */ 
/*      */         
/*  854 */         NativeString nativeString = wide ? new NativeString(value.toString(), true) : new NativeString(value.toString(), this.encoding);
/*      */ 
/*      */         
/*  857 */         this.nativeStrings.put(structField.name, nativeString);
/*  858 */         value = nativeString.getPointer();
/*      */       } else {
/*      */         
/*  861 */         this.nativeStrings.remove(structField.name);
/*      */       } 
/*  863 */       this.nativeStrings.remove(structField.name + ".ptr");
/*  864 */       this.nativeStrings.remove(structField.name + ".val");
/*      */     } 
/*      */     
/*      */     try {
/*  868 */       this.memory.setValue(offset, value, fieldType);
/*      */     }
/*  870 */     catch (IllegalArgumentException e) {
/*  871 */       String msg = "Structure field \"" + structField.name + "\" was declared as " + structField.type + ((structField.type == fieldType) ? "" : (" (native type " + fieldType + ")")) + ", which is not supported within a Structure";
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  876 */       throw new IllegalArgumentException(msg, e);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected List<String> getFieldOrder() {
/*  951 */     List<String> fields = new LinkedList<String>();
/*  952 */     for (Class<?> clazz = getClass(); clazz != Structure.class; clazz = clazz.getSuperclass()) {
/*  953 */       FieldOrder order = clazz.<FieldOrder>getAnnotation(FieldOrder.class);
/*  954 */       if (order != null) {
/*  955 */         fields.addAll(0, Arrays.asList(order.value()));
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  960 */     return Collections.unmodifiableList(fields);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void sortFields(List<Field> fields, List<String> names) {
/*  968 */     for (int i = 0; i < names.size(); i++) {
/*  969 */       String name = names.get(i);
/*  970 */       for (int f = 0; f < fields.size(); f++) {
/*  971 */         Field field = fields.get(f);
/*  972 */         if (name.equals(field.getName())) {
/*  973 */           Collections.swap(fields, i, f);
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected List<Field> getFieldList() {
/*  985 */     List<Field> flist = new ArrayList<Field>();
/*  986 */     Class<?> cls = getClass();
/*  987 */     for (; !cls.equals(Structure.class); 
/*  988 */       cls = cls.getSuperclass()) {
/*  989 */       List<Field> classFields = new ArrayList<Field>();
/*  990 */       Field[] fields = cls.getDeclaredFields();
/*  991 */       for (int i = 0; i < fields.length; i++) {
/*  992 */         int modifiers = fields[i].getModifiers();
/*  993 */         if (!Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))
/*      */         {
/*      */           
/*  996 */           classFields.add(fields[i]); } 
/*      */       } 
/*  998 */       flist.addAll(0, classFields);
/*      */     } 
/* 1000 */     return flist;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private List<String> fieldOrder() {
/* 1007 */     Class<?> clazz = getClass();
/* 1008 */     synchronized (fieldOrder) {
/* 1009 */       List<String> list = fieldOrder.get(clazz);
/* 1010 */       if (list == null) {
/* 1011 */         list = getFieldOrder();
/* 1012 */         fieldOrder.put(clazz, list);
/*      */       } 
/* 1014 */       return list;
/*      */     } 
/*      */   }
/*      */   
/*      */   public static List<String> createFieldsOrder(List<String> baseFields, String... extraFields) {
/* 1019 */     return createFieldsOrder(baseFields, Arrays.asList(extraFields));
/*      */   }
/*      */   
/*      */   public static List<String> createFieldsOrder(List<String> baseFields, List<String> extraFields) {
/* 1023 */     List<String> fields = new ArrayList<String>(baseFields.size() + extraFields.size());
/* 1024 */     fields.addAll(baseFields);
/* 1025 */     fields.addAll(extraFields);
/* 1026 */     return Collections.unmodifiableList(fields);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> createFieldsOrder(String field) {
/* 1034 */     return Collections.unmodifiableList(Collections.singletonList(field));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> createFieldsOrder(String... fields) {
/* 1042 */     return Collections.unmodifiableList(Arrays.asList(fields));
/*      */   }
/*      */   
/*      */   private static <T extends Comparable<T>> List<T> sort(Collection<? extends T> c) {
/* 1046 */     List<T> list = new ArrayList<T>(c);
/* 1047 */     Collections.sort(list);
/* 1048 */     return list;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected List<Field> getFields(boolean force) {
/* 1059 */     List<Field> flist = getFieldList();
/* 1060 */     Set<String> names = new HashSet<String>();
/* 1061 */     for (Field f : flist) {
/* 1062 */       names.add(f.getName());
/*      */     }
/*      */     
/* 1065 */     List<String> fieldOrder = fieldOrder();
/* 1066 */     if (fieldOrder.size() != flist.size() && flist.size() > 1) {
/* 1067 */       if (force) {
/* 1068 */         throw new Error("Structure.getFieldOrder() on " + getClass() + (
/* 1069 */             (fieldOrder.size() < flist.size()) ? " does not provide enough" : " provides too many") + " names [" + fieldOrder
/*      */ 
/*      */             
/* 1072 */             .size() + "] (" + 
/*      */             
/* 1074 */             sort(fieldOrder) + ") to match declared fields [" + flist
/* 1075 */             .size() + "] (" + 
/*      */             
/* 1077 */             sort(names) + ")");
/*      */       }
/*      */       
/* 1080 */       return null;
/*      */     } 
/*      */     
/* 1083 */     Set<String> orderedNames = new HashSet<String>(fieldOrder);
/* 1084 */     if (!orderedNames.equals(names)) {
/* 1085 */       throw new Error("Structure.getFieldOrder() on " + getClass() + " returns names (" + 
/*      */           
/* 1087 */           sort(fieldOrder) + ") which do not match declared field names (" + 
/*      */           
/* 1089 */           sort(names) + ")");
/*      */     }
/*      */     
/* 1092 */     sortFields(flist, fieldOrder);
/* 1093 */     return flist;
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
/*      */   protected int calculateSize(boolean force) {
/* 1111 */     return calculateSize(force, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int size(Class<? extends Structure> type) {
/* 1119 */     return size(type, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static <T extends Structure> int size(Class<T> type, T value) {
/*      */     LayoutInfo info;
/* 1129 */     synchronized (layoutInfo) {
/* 1130 */       info = layoutInfo.get(type);
/*      */     } 
/* 1132 */     int sz = (info != null && !info.variable) ? info.size : -1;
/* 1133 */     if (sz == -1) {
/* 1134 */       if (value == null) {
/* 1135 */         value = newInstance(type, PLACEHOLDER_MEMORY);
/*      */       }
/* 1137 */       sz = value.size();
/*      */     } 
/* 1139 */     return sz;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int calculateSize(boolean force, boolean avoidFFIType) {
/*      */     LayoutInfo info;
/* 1150 */     int size = -1;
/* 1151 */     Class<?> clazz = getClass();
/*      */     
/* 1153 */     synchronized (layoutInfo) {
/* 1154 */       info = layoutInfo.get(clazz);
/*      */     } 
/* 1156 */     if (info == null || this.alignType != info
/* 1157 */       .alignType || this.typeMapper != info
/* 1158 */       .typeMapper) {
/* 1159 */       info = deriveLayout(force, avoidFFIType);
/*      */     }
/* 1161 */     if (info != null) {
/* 1162 */       this.structAlignment = info.alignment;
/* 1163 */       this.structFields = info.fields;
/*      */       
/* 1165 */       if (!info.variable) {
/* 1166 */         synchronized (layoutInfo) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1172 */           if (!layoutInfo.containsKey(clazz) || this.alignType != 0 || this.typeMapper != null)
/*      */           {
/*      */             
/* 1175 */             layoutInfo.put(clazz, info);
/*      */           }
/*      */         } 
/*      */       }
/* 1179 */       size = info.size;
/*      */     } 
/* 1181 */     return size;
/*      */   }
/*      */   
/*      */   private static class LayoutInfo
/*      */   {
/*      */     private LayoutInfo() {}
/*      */     
/* 1188 */     private int size = -1;
/* 1189 */     private int alignment = 1;
/* 1190 */     private final Map<String, Structure.StructField> fields = Collections.synchronizedMap(new LinkedHashMap<String, Structure.StructField>());
/* 1191 */     private int alignType = 0;
/*      */     private TypeMapper typeMapper;
/*      */     private boolean variable;
/*      */   }
/*      */   
/*      */   private void validateField(String name, Class<?> type) {
/* 1197 */     if (this.typeMapper != null) {
/* 1198 */       ToNativeConverter toNative = this.typeMapper.getToNativeConverter(type);
/* 1199 */       if (toNative != null) {
/* 1200 */         validateField(name, toNative.nativeType());
/*      */         return;
/*      */       } 
/*      */     } 
/* 1204 */     if (type.isArray()) {
/* 1205 */       validateField(name, type.getComponentType());
/*      */     } else {
/*      */       
/*      */       try {
/* 1209 */         getNativeSize(type);
/*      */       }
/* 1211 */       catch (IllegalArgumentException e) {
/* 1212 */         String msg = "Invalid Structure field in " + getClass() + ", field name '" + name + "' (" + type + "): " + e.getMessage();
/* 1213 */         throw new IllegalArgumentException(msg, e);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void validateFields() {
/* 1220 */     List<Field> fields = getFieldList();
/* 1221 */     for (Field f : fields) {
/* 1222 */       validateField(f.getName(), f.getType());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private LayoutInfo deriveLayout(boolean force, boolean avoidFFIType) {
/* 1231 */     int calculatedSize = 0;
/* 1232 */     List<Field> fields = getFields(force);
/* 1233 */     if (fields == null) {
/* 1234 */       return null;
/*      */     }
/*      */     
/* 1237 */     LayoutInfo info = new LayoutInfo();
/* 1238 */     info.alignType = this.alignType;
/* 1239 */     info.typeMapper = this.typeMapper;
/*      */     
/* 1241 */     boolean firstField = true;
/* 1242 */     for (Iterator<Field> i = fields.iterator(); i.hasNext(); firstField = false) {
/* 1243 */       Field field = i.next();
/* 1244 */       int modifiers = field.getModifiers();
/*      */       
/* 1246 */       Class<?> type = field.getType();
/* 1247 */       if (type.isArray()) {
/* 1248 */         info.variable = true;
/*      */       }
/* 1250 */       StructField structField = new StructField();
/* 1251 */       structField.isVolatile = Modifier.isVolatile(modifiers);
/* 1252 */       structField.isReadOnly = Modifier.isFinal(modifiers);
/* 1253 */       if (structField.isReadOnly) {
/* 1254 */         if (!Platform.RO_FIELDS) {
/* 1255 */           throw new IllegalArgumentException("This VM does not support read-only fields (field '" + field
/* 1256 */               .getName() + "' within " + getClass() + ")");
/*      */         }
/*      */ 
/*      */         
/* 1260 */         field.setAccessible(true);
/*      */       } 
/* 1262 */       structField.field = field;
/* 1263 */       structField.name = field.getName();
/* 1264 */       structField.type = type;
/*      */ 
/*      */       
/* 1267 */       if (Callback.class.isAssignableFrom(type) && !type.isInterface()) {
/* 1268 */         throw new IllegalArgumentException("Structure Callback field '" + field
/* 1269 */             .getName() + "' must be an interface");
/*      */       }
/*      */       
/* 1272 */       if (type.isArray() && Structure.class
/* 1273 */         .equals(type.getComponentType())) {
/* 1274 */         String msg = "Nested Structure arrays must use a derived Structure type so that the size of the elements can be determined";
/*      */ 
/*      */         
/* 1277 */         throw new IllegalArgumentException(msg);
/*      */       } 
/*      */       
/* 1280 */       int fieldAlignment = 1;
/* 1281 */       if (Modifier.isPublic(field.getModifiers())) {
/*      */ 
/*      */ 
/*      */         
/* 1285 */         Object value = getFieldValue(structField.field);
/* 1286 */         if (value == null && type.isArray()) {
/* 1287 */           if (force) {
/* 1288 */             throw new IllegalStateException("Array fields must be initialized");
/*      */           }
/*      */           
/* 1291 */           return null;
/*      */         } 
/* 1293 */         Class<?> nativeType = type;
/* 1294 */         if (NativeMapped.class.isAssignableFrom(type)) {
/* 1295 */           NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
/* 1296 */           nativeType = tc.nativeType();
/* 1297 */           structField.writeConverter = tc;
/* 1298 */           structField.readConverter = tc;
/* 1299 */           structField.context = new StructureReadContext(this, field);
/*      */         }
/* 1301 */         else if (this.typeMapper != null) {
/* 1302 */           ToNativeConverter writeConverter = this.typeMapper.getToNativeConverter(type);
/* 1303 */           FromNativeConverter readConverter = this.typeMapper.getFromNativeConverter(type);
/* 1304 */           if (writeConverter != null && readConverter != null) {
/* 1305 */             value = writeConverter.toNative(value, new StructureWriteContext(this, structField.field));
/*      */             
/* 1307 */             nativeType = (value != null) ? value.getClass() : Pointer.class;
/* 1308 */             structField.writeConverter = writeConverter;
/* 1309 */             structField.readConverter = readConverter;
/* 1310 */             structField.context = new StructureReadContext(this, field);
/*      */           }
/* 1312 */           else if (writeConverter != null || readConverter != null) {
/* 1313 */             String msg = "Structures require bidirectional type conversion for " + type;
/* 1314 */             throw new IllegalArgumentException(msg);
/*      */           } 
/*      */         } 
/*      */         
/* 1318 */         if (value == null) {
/* 1319 */           value = initializeField(structField.field, type);
/*      */         }
/*      */         
/*      */         try {
/* 1323 */           structField.size = getNativeSize(nativeType, value);
/* 1324 */           fieldAlignment = getNativeAlignment(nativeType, value, firstField);
/*      */         }
/* 1326 */         catch (IllegalArgumentException e) {
/*      */           
/* 1328 */           if (!force && this.typeMapper == null) {
/* 1329 */             return null;
/*      */           }
/* 1331 */           String msg = "Invalid Structure field in " + getClass() + ", field name '" + structField.name + "' (" + structField.type + "): " + e.getMessage();
/* 1332 */           throw new IllegalArgumentException(msg, e);
/*      */         } 
/*      */ 
/*      */         
/* 1336 */         if (fieldAlignment == 0) {
/* 1337 */           throw new Error("Field alignment is zero for field '" + structField.name + "' within " + getClass());
/*      */         }
/* 1339 */         info.alignment = Math.max(info.alignment, fieldAlignment);
/* 1340 */         if (calculatedSize % fieldAlignment != 0) {
/* 1341 */           calculatedSize += fieldAlignment - calculatedSize % fieldAlignment;
/*      */         }
/* 1343 */         if (this instanceof Union) {
/* 1344 */           structField.offset = 0;
/* 1345 */           calculatedSize = Math.max(calculatedSize, structField.size);
/*      */         } else {
/*      */           
/* 1348 */           structField.offset = calculatedSize;
/* 1349 */           calculatedSize += structField.size;
/*      */         } 
/*      */ 
/*      */         
/* 1353 */         info.fields.put(structField.name, structField);
/*      */       } 
/*      */     } 
/* 1356 */     if (calculatedSize > 0) {
/* 1357 */       int size = addPadding(calculatedSize, info.alignment);
/*      */       
/* 1359 */       if (this instanceof ByValue && !avoidFFIType) {
/* 1360 */         getTypeInfo();
/*      */       }
/* 1362 */       info.size = size;
/* 1363 */       return info;
/*      */     } 
/*      */     
/* 1366 */     throw new IllegalArgumentException("Structure " + getClass() + " has unknown or zero size (ensure all fields are public)");
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
/*      */   private void initializeFields() {
/* 1378 */     List<Field> flist = getFieldList();
/* 1379 */     for (Field f : flist) {
/*      */       try {
/* 1381 */         Object o = f.get(this);
/* 1382 */         if (o == null) {
/* 1383 */           initializeField(f, f.getType());
/*      */         }
/*      */       }
/* 1386 */       catch (Exception e) {
/* 1387 */         throw new Error("Exception reading field '" + f.getName() + "' in " + getClass(), e);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private Object initializeField(Field field, Class<?> type) {
/* 1393 */     Object value = null;
/* 1394 */     if (Structure.class.isAssignableFrom(type) && 
/* 1395 */       !ByReference.class.isAssignableFrom(type)) {
/*      */       try {
/* 1397 */         value = newInstance(type, PLACEHOLDER_MEMORY);
/* 1398 */         setFieldValue(field, value);
/*      */       }
/* 1400 */       catch (IllegalArgumentException e) {
/* 1401 */         String msg = "Can't determine size of nested structure";
/* 1402 */         throw new IllegalArgumentException(msg, e);
/*      */       }
/*      */     
/* 1405 */     } else if (NativeMapped.class.isAssignableFrom(type)) {
/* 1406 */       NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
/* 1407 */       value = tc.defaultValue();
/* 1408 */       setFieldValue(field, value);
/*      */     } 
/* 1410 */     return value;
/*      */   }
/*      */   
/*      */   private int addPadding(int calculatedSize) {
/* 1414 */     return addPadding(calculatedSize, this.structAlignment);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private int addPadding(int calculatedSize, int alignment) {
/* 1420 */     if (this.actualAlignType != 1 && 
/* 1421 */       calculatedSize % alignment != 0) {
/* 1422 */       calculatedSize += alignment - calculatedSize % alignment;
/*      */     }
/*      */     
/* 1425 */     return calculatedSize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int getStructAlignment() {
/* 1432 */     if (this.size == -1)
/*      */     {
/* 1434 */       calculateSize(true);
/*      */     }
/* 1436 */     return this.structAlignment;
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
/*      */   protected int getNativeAlignment(Class<?> type, Object value, boolean isFirstElement) {
/* 1450 */     int alignment = 1;
/* 1451 */     if (NativeMapped.class.isAssignableFrom(type)) {
/* 1452 */       NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
/* 1453 */       type = tc.nativeType();
/* 1454 */       value = tc.toNative(value, new ToNativeContext());
/*      */     } 
/* 1456 */     int size = Native.getNativeSize(type, value);
/* 1457 */     if (type.isPrimitive() || Long.class == type || Integer.class == type || Short.class == type || Character.class == type || Byte.class == type || Boolean.class == type || Float.class == type || Double.class == type) {
/*      */ 
/*      */ 
/*      */       
/* 1461 */       alignment = size;
/*      */     }
/* 1463 */     else if ((Pointer.class.isAssignableFrom(type) && !Function.class.isAssignableFrom(type)) || (Platform.HAS_BUFFERS && Buffer.class
/* 1464 */       .isAssignableFrom(type)) || Callback.class
/* 1465 */       .isAssignableFrom(type) || WString.class == type || String.class == type) {
/*      */ 
/*      */       
/* 1468 */       alignment = Native.POINTER_SIZE;
/*      */     }
/* 1470 */     else if (Structure.class.isAssignableFrom(type)) {
/* 1471 */       if (ByReference.class.isAssignableFrom(type)) {
/* 1472 */         alignment = Native.POINTER_SIZE;
/*      */       } else {
/*      */         
/* 1475 */         if (value == null)
/* 1476 */           value = newInstance(type, PLACEHOLDER_MEMORY); 
/* 1477 */         alignment = ((Structure)value).getStructAlignment();
/*      */       }
/*      */     
/* 1480 */     } else if (type.isArray()) {
/* 1481 */       alignment = getNativeAlignment(type.getComponentType(), null, isFirstElement);
/*      */     } else {
/*      */       
/* 1484 */       throw new IllegalArgumentException("Type " + type + " has unknown native alignment");
/*      */     } 
/*      */     
/* 1487 */     if (this.actualAlignType == 1) {
/* 1488 */       alignment = 1;
/*      */     }
/* 1490 */     else if (this.actualAlignType == 3) {
/* 1491 */       alignment = Math.min(8, alignment);
/*      */     }
/* 1493 */     else if (this.actualAlignType == 2) {
/*      */ 
/*      */       
/* 1496 */       if (!isFirstElement || !Platform.isMac() || !Platform.isPPC()) {
/* 1497 */         alignment = Math.min(Native.MAX_ALIGNMENT, alignment);
/*      */       }
/* 1499 */       if (!isFirstElement && Platform.isAIX() && (type == double.class || type == Double.class)) {
/* 1500 */         alignment = 4;
/*      */       }
/*      */     } 
/* 1503 */     return alignment;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1513 */     return toString(Boolean.getBoolean("jna.dump_memory"));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString(boolean debug) {
/* 1522 */     return toString(0, true, debug);
/*      */   }
/*      */   
/*      */   private String format(Class<?> type) {
/* 1526 */     String s = type.getName();
/* 1527 */     int dot = s.lastIndexOf(".");
/* 1528 */     return s.substring(dot + 1);
/*      */   }
/*      */   
/*      */   private String toString(int indent, boolean showContents, boolean dumpMemory) {
/* 1532 */     ensureAllocated();
/* 1533 */     String LS = System.getProperty("line.separator");
/* 1534 */     String name = format(getClass()) + "(" + getPointer() + ")";
/* 1535 */     if (!(getPointer() instanceof Memory)) {
/* 1536 */       name = name + " (" + size() + " bytes)";
/*      */     }
/* 1538 */     String prefix = "";
/* 1539 */     for (int idx = 0; idx < indent; idx++) {
/* 1540 */       prefix = prefix + "  ";
/*      */     }
/* 1542 */     String contents = LS;
/* 1543 */     if (!showContents) {
/* 1544 */       contents = "...}";
/*      */     } else {
/* 1546 */       for (Iterator<StructField> i = fields().values().iterator(); i.hasNext(); ) {
/* 1547 */         StructField sf = i.next();
/* 1548 */         Object value = getFieldValue(sf.field);
/* 1549 */         String type = format(sf.type);
/* 1550 */         String index = "";
/* 1551 */         contents = contents + prefix;
/* 1552 */         if (sf.type.isArray() && value != null) {
/* 1553 */           type = format(sf.type.getComponentType());
/* 1554 */           index = "[" + Array.getLength(value) + "]";
/*      */         } 
/* 1556 */         contents = contents + String.format("  %s %s%s@0x%X", new Object[] { type, sf.name, index, Integer.valueOf(sf.offset) });
/* 1557 */         if (value instanceof Structure) {
/* 1558 */           value = ((Structure)value).toString(indent + 1, !(value instanceof ByReference), dumpMemory);
/*      */         }
/* 1560 */         contents = contents + "=";
/* 1561 */         if (value instanceof Long) {
/* 1562 */           contents = contents + String.format("0x%08X", new Object[] { value });
/*      */         }
/* 1564 */         else if (value instanceof Integer) {
/* 1565 */           contents = contents + String.format("0x%04X", new Object[] { value });
/*      */         }
/* 1567 */         else if (value instanceof Short) {
/* 1568 */           contents = contents + String.format("0x%02X", new Object[] { value });
/*      */         }
/* 1570 */         else if (value instanceof Byte) {
/* 1571 */           contents = contents + String.format("0x%01X", new Object[] { value });
/*      */         } else {
/*      */           
/* 1574 */           contents = contents + String.valueOf(value).trim();
/*      */         } 
/* 1576 */         contents = contents + LS;
/* 1577 */         if (!i.hasNext())
/* 1578 */           contents = contents + prefix + "}"; 
/*      */       } 
/*      */     } 
/* 1581 */     if (indent == 0 && dumpMemory) {
/* 1582 */       int BYTES_PER_ROW = 4;
/* 1583 */       contents = contents + LS + "memory dump" + LS;
/* 1584 */       byte[] buf = getPointer().getByteArray(0L, size());
/* 1585 */       for (int i = 0; i < buf.length; i++) {
/* 1586 */         if (i % 4 == 0) contents = contents + "["; 
/* 1587 */         if (buf[i] >= 0 && buf[i] < 16)
/* 1588 */           contents = contents + "0"; 
/* 1589 */         contents = contents + Integer.toHexString(buf[i] & 0xFF);
/* 1590 */         if (i % 4 == 3 && i < buf.length - 1)
/* 1591 */           contents = contents + "]" + LS; 
/*      */       } 
/* 1593 */       contents = contents + "]";
/*      */     } 
/* 1595 */     return name + " {" + contents;
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
/*      */   public Structure[] toArray(Structure[] array) {
/* 1607 */     ensureAllocated();
/* 1608 */     if (this.memory instanceof AutoAllocated) {
/*      */       
/* 1610 */       Memory m = (Memory)this.memory;
/* 1611 */       int requiredSize = array.length * size();
/* 1612 */       if (m.size() < requiredSize) {
/* 1613 */         useMemory(autoAllocate(requiredSize));
/*      */       }
/*      */     } 
/*      */     
/* 1617 */     array[0] = this;
/* 1618 */     int size = size();
/* 1619 */     for (int i = 1; i < array.length; i++) {
/* 1620 */       array[i] = newInstance(getClass(), this.memory.share((i * size), size));
/* 1621 */       array[i].conditionalAutoRead();
/*      */     } 
/*      */     
/* 1624 */     if (!(this instanceof ByValue))
/*      */     {
/* 1626 */       this.array = array;
/*      */     }
/*      */     
/* 1629 */     return array;
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
/*      */   public Structure[] toArray(int size) {
/* 1642 */     return toArray((Structure[])Array.newInstance(getClass(), size));
/*      */   }
/*      */   
/*      */   private Class<?> baseClass() {
/* 1646 */     if ((this instanceof ByReference || this instanceof ByValue) && Structure.class
/*      */       
/* 1648 */       .isAssignableFrom(getClass().getSuperclass())) {
/* 1649 */       return getClass().getSuperclass();
/*      */     }
/* 1651 */     return getClass();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean dataEquals(Structure s) {
/* 1660 */     return dataEquals(s, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean dataEquals(Structure s, boolean clear) {
/* 1670 */     if (clear) {
/* 1671 */       s.getPointer().clear(s.size());
/* 1672 */       s.write();
/* 1673 */       getPointer().clear(size());
/* 1674 */       write();
/*      */     } 
/* 1676 */     byte[] data = s.getPointer().getByteArray(0L, s.size());
/* 1677 */     byte[] ref = getPointer().getByteArray(0L, size());
/* 1678 */     if (data.length == ref.length) {
/* 1679 */       for (int i = 0; i < data.length; i++) {
/* 1680 */         if (data[i] != ref[i]) {
/* 1681 */           return false;
/*      */         }
/*      */       } 
/* 1684 */       return true;
/*      */     } 
/* 1686 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean equals(Object o) {
/* 1694 */     return (o instanceof Structure && o
/* 1695 */       .getClass() == getClass() && ((Structure)o)
/* 1696 */       .getPointer().equals(getPointer()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int hashCode() {
/* 1704 */     Pointer p = getPointer();
/* 1705 */     if (p != null) {
/* 1706 */       return getPointer().hashCode();
/*      */     }
/* 1708 */     return getClass().hashCode();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void cacheTypeInfo(Pointer p) {
/* 1715 */     this.typeInfo = p.peer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   FFIType getFieldTypeInfo(StructField f) {
/* 1723 */     Class<?> type = f.type;
/* 1724 */     Object value = getFieldValue(f.field);
/* 1725 */     if (this.typeMapper != null) {
/* 1726 */       ToNativeConverter nc = this.typeMapper.getToNativeConverter(type);
/* 1727 */       if (nc != null) {
/* 1728 */         type = nc.nativeType();
/* 1729 */         value = nc.toNative(value, new ToNativeContext());
/*      */       } 
/*      */     } 
/* 1732 */     return FFIType.get(value, type);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Pointer getTypeInfo() {
/* 1739 */     Pointer p = getTypeInfo(this).getPointer();
/* 1740 */     cacheTypeInfo(p);
/* 1741 */     return p;
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
/*      */   public void setAutoSynch(boolean auto) {
/* 1765 */     setAutoRead(auto);
/* 1766 */     setAutoWrite(auto);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAutoRead(boolean auto) {
/* 1774 */     this.autoRead = auto;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getAutoRead() {
/* 1782 */     return this.autoRead;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAutoWrite(boolean auto) {
/* 1790 */     this.autoWrite = auto;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getAutoWrite() {
/* 1798 */     return this.autoWrite;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static FFIType getTypeInfo(Object obj) {
/* 1806 */     return FFIType.get(obj);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static <T extends Structure> T newInstance(Class<T> type, long init) {
/*      */     try {
/* 1815 */       T s = newInstance(type, (init == 0L) ? PLACEHOLDER_MEMORY : new Pointer(init));
/* 1816 */       if (init != 0L) {
/* 1817 */         s.conditionalAutoRead();
/*      */       }
/* 1819 */       return s;
/*      */     }
/* 1821 */     catch (Throwable e) {
/* 1822 */       LOG.log(Level.WARNING, "JNA: Error creating structure", e);
/* 1823 */       return null;
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
/*      */   public static <T extends Structure> T newInstance(Class<T> type, Pointer init) throws IllegalArgumentException {
/*      */     try {
/* 1836 */       Constructor<T> ctor = getPointerConstructor(type);
/* 1837 */       if (ctor != null) {
/* 1838 */         return ctor.newInstance(new Object[] { init });
/*      */       
/*      */       }
/*      */     }
/* 1842 */     catch (SecurityException securityException) {
/*      */ 
/*      */     
/* 1845 */     } catch (InstantiationException e) {
/* 1846 */       String msg = "Can't instantiate " + type;
/* 1847 */       throw new IllegalArgumentException(msg, e);
/*      */     }
/* 1849 */     catch (IllegalAccessException e) {
/* 1850 */       String msg = "Instantiation of " + type + " (Pointer) not allowed, is it public?";
/* 1851 */       throw new IllegalArgumentException(msg, e);
/*      */     }
/* 1853 */     catch (InvocationTargetException e) {
/* 1854 */       String msg = "Exception thrown while instantiating an instance of " + type;
/* 1855 */       throw new IllegalArgumentException(msg, e);
/*      */     } 
/* 1857 */     T s = newInstance(type);
/* 1858 */     if (init != PLACEHOLDER_MEMORY) {
/* 1859 */       s.useMemory(init);
/*      */     }
/* 1861 */     return s;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Structure> T newInstance(Class<T> type) throws IllegalArgumentException {
/* 1871 */     Structure structure = Klass.<Structure>newInstance(type);
/* 1872 */     if (structure instanceof ByValue) {
/* 1873 */       structure.allocateMemory();
/*      */     }
/* 1875 */     return (T)structure;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static <T> Constructor<T> getPointerConstructor(Class<T> type) {
/* 1885 */     for (Constructor<T> constructor : type.getConstructors()) {
/* 1886 */       Class[] parameterTypes = constructor.getParameterTypes();
/* 1887 */       if (parameterTypes.length == 1 && parameterTypes[0].equals(Pointer.class)) {
/* 1888 */         return constructor;
/*      */       }
/*      */     } 
/*      */     
/* 1892 */     return null;
/*      */   }
/*      */   
/*      */   protected static class StructField {
/*      */     public String name;
/*      */     public Class<?> type;
/*      */     public Field field;
/* 1899 */     public int size = -1;
/* 1900 */     public int offset = -1;
/*      */     public boolean isVolatile;
/*      */     public boolean isReadOnly;
/*      */     public FromNativeConverter readConverter;
/*      */     public ToNativeConverter writeConverter;
/*      */     public FromNativeContext context;
/*      */     
/*      */     public String toString() {
/* 1908 */       return this.name + "@" + this.offset + "[" + this.size + "] (" + this.type + ")";
/*      */     }
/*      */   }
/*      */   
/*      */   @FieldOrder({"size", "alignment", "type", "elements"})
/*      */   static class FFIType
/*      */     extends Structure
/*      */   {
/*      */     public static class size_t
/*      */       extends IntegerType {
/*      */       private static final long serialVersionUID = 1L;
/*      */       
/*      */       public size_t() {
/* 1921 */         this(0L); } public size_t(long value) {
/* 1922 */         super(Native.SIZE_T_SIZE, value);
/*      */       }
/*      */     }
/* 1925 */     private static final Map<Class, FFIType> typeInfoMap = (Map)new WeakHashMap<Class<?>, FFIType>();
/* 1926 */     private static final Map<Class, FFIType> unionHelper = (Map)new WeakHashMap<Class<?>, FFIType>();
/* 1927 */     private static final Map<Pointer, FFIType> ffiTypeInfo = new HashMap<Pointer, FFIType>(); private static final int FFI_TYPE_STRUCT = 13;
/*      */     public size_t size;
/*      */     public short alignment;
/*      */     public short type;
/*      */     public Pointer elements;
/*      */     
/*      */     private static class FFITypes {
/*      */       private static Pointer ffi_type_void;
/*      */       private static Pointer ffi_type_float;
/*      */       private static Pointer ffi_type_double;
/*      */       private static Pointer ffi_type_longdouble;
/*      */       private static Pointer ffi_type_uint8;
/*      */       private static Pointer ffi_type_sint8;
/*      */       private static Pointer ffi_type_uint16;
/*      */       private static Pointer ffi_type_sint16;
/*      */       private static Pointer ffi_type_uint32;
/*      */       private static Pointer ffi_type_sint32;
/*      */       private static Pointer ffi_type_uint64;
/*      */       private static Pointer ffi_type_sint64;
/*      */       private static Pointer ffi_type_pointer; }
/*      */     
/*      */     private static boolean isIntegerType(FFIType type) {
/* 1949 */       Pointer typePointer = type.getPointer();
/* 1950 */       return (typePointer.equals(FFITypes.ffi_type_uint8) || typePointer
/* 1951 */         .equals(FFITypes.ffi_type_sint8) || typePointer
/* 1952 */         .equals(FFITypes.ffi_type_uint16) || typePointer
/* 1953 */         .equals(FFITypes.ffi_type_sint16) || typePointer
/* 1954 */         .equals(FFITypes.ffi_type_uint32) || typePointer
/* 1955 */         .equals(FFITypes.ffi_type_sint32) || typePointer
/* 1956 */         .equals(FFITypes.ffi_type_uint64) || typePointer
/* 1957 */         .equals(FFITypes.ffi_type_sint64) || typePointer
/* 1958 */         .equals(FFITypes.ffi_type_pointer));
/*      */     }
/*      */     
/*      */     private static boolean isFloatType(FFIType type) {
/* 1962 */       Pointer typePointer = type.getPointer();
/* 1963 */       return (typePointer.equals(FFITypes.ffi_type_float) || typePointer
/* 1964 */         .equals(FFITypes.ffi_type_double));
/*      */     }
/*      */     
/*      */     static {
/* 1968 */       if (Native.POINTER_SIZE == 0)
/* 1969 */         throw new Error("Native library not initialized"); 
/* 1970 */       if (FFITypes.ffi_type_void == null)
/* 1971 */         throw new Error("FFI types not initialized"); 
/* 1972 */       ffiTypeInfo.put(FFITypes.ffi_type_void, Structure.newInstance(FFIType.class, FFITypes.ffi_type_void));
/* 1973 */       ffiTypeInfo.put(FFITypes.ffi_type_float, Structure.newInstance(FFIType.class, FFITypes.ffi_type_float));
/* 1974 */       ffiTypeInfo.put(FFITypes.ffi_type_double, Structure.newInstance(FFIType.class, FFITypes.ffi_type_double));
/* 1975 */       ffiTypeInfo.put(FFITypes.ffi_type_longdouble, Structure.newInstance(FFIType.class, FFITypes.ffi_type_longdouble));
/* 1976 */       ffiTypeInfo.put(FFITypes.ffi_type_uint8, Structure.newInstance(FFIType.class, FFITypes.ffi_type_uint8));
/* 1977 */       ffiTypeInfo.put(FFITypes.ffi_type_sint8, Structure.newInstance(FFIType.class, FFITypes.ffi_type_sint8));
/* 1978 */       ffiTypeInfo.put(FFITypes.ffi_type_uint16, Structure.newInstance(FFIType.class, FFITypes.ffi_type_uint16));
/* 1979 */       ffiTypeInfo.put(FFITypes.ffi_type_sint16, Structure.newInstance(FFIType.class, FFITypes.ffi_type_sint16));
/* 1980 */       ffiTypeInfo.put(FFITypes.ffi_type_uint32, Structure.newInstance(FFIType.class, FFITypes.ffi_type_uint32));
/* 1981 */       ffiTypeInfo.put(FFITypes.ffi_type_sint32, Structure.newInstance(FFIType.class, FFITypes.ffi_type_sint32));
/* 1982 */       ffiTypeInfo.put(FFITypes.ffi_type_uint64, Structure.newInstance(FFIType.class, FFITypes.ffi_type_uint64));
/* 1983 */       ffiTypeInfo.put(FFITypes.ffi_type_sint64, Structure.newInstance(FFIType.class, FFITypes.ffi_type_sint64));
/* 1984 */       ffiTypeInfo.put(FFITypes.ffi_type_pointer, Structure.newInstance(FFIType.class, FFITypes.ffi_type_pointer));
/* 1985 */       for (FFIType f : ffiTypeInfo.values()) {
/* 1986 */         f.read();
/*      */       }
/* 1988 */       typeInfoMap.put(void.class, ffiTypeInfo.get(FFITypes.ffi_type_void));
/* 1989 */       typeInfoMap.put(Void.class, ffiTypeInfo.get(FFITypes.ffi_type_void));
/* 1990 */       typeInfoMap.put(float.class, ffiTypeInfo.get(FFITypes.ffi_type_float));
/* 1991 */       typeInfoMap.put(Float.class, ffiTypeInfo.get(FFITypes.ffi_type_float));
/* 1992 */       typeInfoMap.put(double.class, ffiTypeInfo.get(FFITypes.ffi_type_double));
/* 1993 */       typeInfoMap.put(Double.class, ffiTypeInfo.get(FFITypes.ffi_type_double));
/* 1994 */       typeInfoMap.put(long.class, ffiTypeInfo.get(FFITypes.ffi_type_sint64));
/* 1995 */       typeInfoMap.put(Long.class, ffiTypeInfo.get(FFITypes.ffi_type_sint64));
/* 1996 */       typeInfoMap.put(int.class, ffiTypeInfo.get(FFITypes.ffi_type_sint32));
/* 1997 */       typeInfoMap.put(Integer.class, ffiTypeInfo.get(FFITypes.ffi_type_sint32));
/* 1998 */       typeInfoMap.put(short.class, ffiTypeInfo.get(FFITypes.ffi_type_sint16));
/* 1999 */       typeInfoMap.put(Short.class, ffiTypeInfo.get(FFITypes.ffi_type_sint16));
/*      */       
/* 2001 */       FFIType ctype = (Native.WCHAR_SIZE == 2) ? ffiTypeInfo.get(FFITypes.ffi_type_uint16) : ffiTypeInfo.get(FFITypes.ffi_type_uint32);
/* 2002 */       typeInfoMap.put(char.class, ctype);
/* 2003 */       typeInfoMap.put(Character.class, ctype);
/* 2004 */       typeInfoMap.put(byte.class, ffiTypeInfo.get(FFITypes.ffi_type_sint8));
/* 2005 */       typeInfoMap.put(Byte.class, ffiTypeInfo.get(FFITypes.ffi_type_sint8));
/* 2006 */       typeInfoMap.put(Pointer.class, ffiTypeInfo.get(FFITypes.ffi_type_pointer));
/* 2007 */       typeInfoMap.put(String.class, ffiTypeInfo.get(FFITypes.ffi_type_pointer));
/* 2008 */       typeInfoMap.put(WString.class, ffiTypeInfo.get(FFITypes.ffi_type_pointer));
/* 2009 */       typeInfoMap.put(boolean.class, ffiTypeInfo.get(FFITypes.ffi_type_uint32));
/* 2010 */       typeInfoMap.put(Boolean.class, ffiTypeInfo.get(FFITypes.ffi_type_uint32));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public FFIType(FFIType reference) {
/* 2017 */       this.type = 13;
/*      */ 
/*      */ 
/*      */       
/* 2021 */       this.size = reference.size;
/* 2022 */       this.alignment = reference.alignment;
/* 2023 */       this.type = reference.type;
/* 2024 */       this.elements = reference.elements;
/*      */     } public FFIType() {
/*      */       this.type = 13;
/*      */     }
/*      */     public FFIType(Structure ref) {
/*      */       Pointer[] els;
/*      */       this.type = 13;
/* 2031 */       ref.ensureAllocated(true);
/*      */       
/* 2033 */       if (ref instanceof Union) {
/* 2034 */         FFIType unionType = null;
/* 2035 */         int size = 0;
/* 2036 */         boolean hasInteger = false;
/* 2037 */         for (Structure.StructField sf : ref.fields().values()) {
/* 2038 */           FFIType type = ref.getFieldTypeInfo(sf);
/* 2039 */           if (isIntegerType(type)) {
/* 2040 */             hasInteger = true;
/*      */           }
/* 2042 */           if (unionType == null || size < sf.size || (size == sf.size && Structure.class
/*      */ 
/*      */             
/* 2045 */             .isAssignableFrom(sf.type))) {
/* 2046 */             unionType = type;
/* 2047 */             size = sf.size;
/*      */           } 
/*      */         } 
/* 2050 */         if (!Platform.isWindows() && ((
/* 2051 */           Platform.isIntel() && Platform.is64Bit()) || 
/* 2052 */           Platform.isARM()))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2064 */           if (hasInteger && isFloatType(unionType)) {
/* 2065 */             unionType = new FFIType(unionType);
/* 2066 */             if (unionType.size.intValue() == 4) {
/* 2067 */               unionType.type = ((FFIType)ffiTypeInfo.get(FFITypes.ffi_type_uint32)).type;
/* 2068 */             } else if (unionType.size.intValue() == 8) {
/* 2069 */               unionType.type = ((FFIType)ffiTypeInfo.get(FFITypes.ffi_type_uint64)).type;
/*      */             } 
/* 2071 */             unionType.write();
/*      */           } 
/*      */         }
/*      */         
/* 2075 */         els = new Pointer[] { unionType.getPointer(), null };
/*      */ 
/*      */         
/* 2078 */         unionHelper.put(ref.getClass(), unionType);
/*      */       } else {
/*      */         
/* 2081 */         els = new Pointer[ref.fields().size() + 1];
/* 2082 */         int idx = 0;
/* 2083 */         for (Structure.StructField sf : ref.fields().values()) {
/* 2084 */           els[idx++] = ref.getFieldTypeInfo(sf).getPointer();
/*      */         }
/*      */       } 
/* 2087 */       init(els);
/* 2088 */       write();
/*      */     }
/*      */     public FFIType(Object array, Class<?> type) {
/*      */       this.type = 13;
/* 2092 */       int length = Array.getLength(array);
/* 2093 */       Pointer[] els = new Pointer[length + 1];
/* 2094 */       Pointer p = get((Object)null, type.getComponentType()).getPointer();
/* 2095 */       for (int i = 0; i < length; i++) {
/* 2096 */         els[i] = p;
/*      */       }
/* 2098 */       init(els);
/* 2099 */       write();
/*      */     }
/*      */     
/*      */     private void init(Pointer[] els) {
/* 2103 */       this.elements = new Memory((Native.POINTER_SIZE * els.length));
/* 2104 */       this.elements.write(0L, els, 0, els.length);
/* 2105 */       write();
/*      */     }
/*      */ 
/*      */     
/*      */     static FFIType get(Object obj) {
/* 2110 */       if (obj == null)
/* 2111 */         return typeInfoMap.get(Pointer.class); 
/* 2112 */       if (obj instanceof Class)
/* 2113 */         return get((Object)null, (Class)obj); 
/* 2114 */       return get(obj, obj.getClass());
/*      */     }
/*      */     
/*      */     private static FFIType get(Object obj, Class<?> cls) {
/* 2118 */       TypeMapper mapper = Native.getTypeMapper(cls);
/* 2119 */       if (mapper != null) {
/* 2120 */         ToNativeConverter nc = mapper.getToNativeConverter(cls);
/* 2121 */         if (nc != null) {
/* 2122 */           cls = nc.nativeType();
/*      */         }
/*      */       } 
/* 2125 */       synchronized (typeInfoMap) {
/* 2126 */         FFIType o = typeInfoMap.get(cls);
/* 2127 */         if (o != null) {
/* 2128 */           return o;
/*      */         }
/* 2130 */         if ((Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(cls)) || Callback.class
/* 2131 */           .isAssignableFrom(cls)) {
/* 2132 */           typeInfoMap.put(cls, typeInfoMap.get(Pointer.class));
/* 2133 */           return typeInfoMap.get(Pointer.class);
/*      */         } 
/* 2135 */         if (Structure.class.isAssignableFrom(cls)) {
/* 2136 */           if (obj == null) obj = newInstance(cls, Structure.PLACEHOLDER_MEMORY); 
/* 2137 */           if (Structure.ByReference.class.isAssignableFrom(cls)) {
/* 2138 */             typeInfoMap.put(cls, typeInfoMap.get(Pointer.class));
/* 2139 */             return typeInfoMap.get(Pointer.class);
/*      */           } 
/* 2141 */           FFIType type = new FFIType((Structure)obj);
/* 2142 */           typeInfoMap.put(cls, type);
/* 2143 */           return type;
/*      */         } 
/* 2145 */         if (NativeMapped.class.isAssignableFrom(cls)) {
/* 2146 */           NativeMappedConverter c = NativeMappedConverter.getInstance(cls);
/* 2147 */           return get(c.toNative(obj, new ToNativeContext()), c.nativeType());
/*      */         } 
/* 2149 */         if (cls.isArray()) {
/* 2150 */           FFIType type = new FFIType(obj, cls);
/*      */           
/* 2152 */           typeInfoMap.put(cls, type);
/* 2153 */           return type;
/*      */         } 
/* 2155 */         throw new IllegalArgumentException("Unsupported type " + cls);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private static class AutoAllocated extends Memory {
/*      */     public AutoAllocated(int size) {
/* 2162 */       super(size);
/*      */       
/* 2164 */       clear();
/*      */     }
/*      */     
/*      */     public String toString() {
/* 2168 */       return "auto-" + super.toString();
/*      */     }
/*      */   }
/*      */   
/*      */   private static void structureArrayCheck(Structure[] ss) {
/* 2173 */     if (ByReference[].class.isAssignableFrom(ss.getClass())) {
/*      */       return;
/*      */     }
/* 2176 */     Pointer base = ss[0].getPointer();
/* 2177 */     int size = ss[0].size();
/* 2178 */     for (int si = 1; si < ss.length; si++) {
/* 2179 */       if ((ss[si].getPointer()).peer != base.peer + (size * si)) {
/* 2180 */         String msg = "Structure array elements must use contiguous memory (bad backing address at Structure array index " + si + ")";
/*      */         
/* 2182 */         throw new IllegalArgumentException(msg);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public static void autoRead(Structure[] ss) {
/* 2188 */     structureArrayCheck(ss);
/* 2189 */     if ((ss[0]).array == ss) {
/* 2190 */       ss[0].autoRead();
/*      */     } else {
/*      */       
/* 2193 */       for (int si = 0; si < ss.length; si++) {
/* 2194 */         if (ss[si] != null) {
/* 2195 */           ss[si].autoRead();
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public void autoRead() {
/* 2202 */     if (getAutoRead()) {
/* 2203 */       read();
/* 2204 */       if (this.array != null) {
/* 2205 */         for (int i = 1; i < this.array.length; i++) {
/* 2206 */           this.array[i].autoRead();
/*      */         }
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public static void autoWrite(Structure[] ss) {
/* 2213 */     structureArrayCheck(ss);
/* 2214 */     if ((ss[0]).array == ss) {
/* 2215 */       ss[0].autoWrite();
/*      */     } else {
/*      */       
/* 2218 */       for (int si = 0; si < ss.length; si++) {
/* 2219 */         if (ss[si] != null) {
/* 2220 */           ss[si].autoWrite();
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public void autoWrite() {
/* 2227 */     if (getAutoWrite()) {
/* 2228 */       write();
/* 2229 */       if (this.array != null) {
/* 2230 */         for (int i = 1; i < this.array.length; i++) {
/* 2231 */           this.array[i].autoWrite();
/*      */         }
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int getNativeSize(Class<?> nativeType) {
/* 2243 */     return getNativeSize(nativeType, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int getNativeSize(Class<?> nativeType, Object value) {
/* 2253 */     return Native.getNativeSize(nativeType, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 2259 */   private static final Pointer PLACEHOLDER_MEMORY = new Pointer(0L) {
/*      */       public Pointer share(long offset, long sz) {
/* 2261 */         return this;
/*      */       }
/*      */     };
/*      */ 
/*      */ 
/*      */   
/*      */   static void validate(Class<? extends Structure> cls) {
/*      */     try {
/* 2269 */       cls.getConstructor(new Class[0]);
/*      */       return;
/* 2271 */     } catch (NoSuchMethodException noSuchMethodException) {
/*      */     
/* 2273 */     } catch (SecurityException securityException) {}
/*      */     
/* 2275 */     throw new IllegalArgumentException("No suitable constructor found for class: " + cls.getName());
/*      */   }
/*      */   
/*      */   @Documented
/*      */   @Retention(RetentionPolicy.RUNTIME)
/*      */   @Target({ElementType.TYPE})
/*      */   public static @interface FieldOrder {
/*      */     String[] value();
/*      */   }
/*      */   
/*      */   public static interface ByReference {}
/*      */   
/*      */   public static interface ByValue {}
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\Structure.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */