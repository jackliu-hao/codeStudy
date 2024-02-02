/*     */ package freemarker.template;
/*     */ 
/*     */ import freemarker.ext.util.WrapperTemplateModel;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Array;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class DefaultArrayAdapter
/*     */   extends WrappingTemplateModel
/*     */   implements TemplateSequenceModel, AdapterTemplateModel, WrapperTemplateModel, Serializable
/*     */ {
/*     */   public static DefaultArrayAdapter adapt(Object array, ObjectWrapperAndUnwrapper wrapper) {
/*  60 */     Class<?> componentType = array.getClass().getComponentType();
/*  61 */     if (componentType == null) {
/*  62 */       throw new IllegalArgumentException("Not an array");
/*     */     }
/*     */     
/*  65 */     if (componentType.isPrimitive()) {
/*  66 */       if (componentType == int.class) {
/*  67 */         return new IntArrayAdapter((int[])array, wrapper);
/*     */       }
/*  69 */       if (componentType == double.class) {
/*  70 */         return new DoubleArrayAdapter((double[])array, wrapper);
/*     */       }
/*  72 */       if (componentType == long.class) {
/*  73 */         return new LongArrayAdapter((long[])array, wrapper);
/*     */       }
/*  75 */       if (componentType == boolean.class) {
/*  76 */         return new BooleanArrayAdapter((boolean[])array, wrapper);
/*     */       }
/*  78 */       if (componentType == float.class) {
/*  79 */         return new FloatArrayAdapter((float[])array, wrapper);
/*     */       }
/*  81 */       if (componentType == char.class) {
/*  82 */         return new CharArrayAdapter((char[])array, wrapper);
/*     */       }
/*  84 */       if (componentType == short.class) {
/*  85 */         return new ShortArrayAdapter((short[])array, wrapper);
/*     */       }
/*  87 */       if (componentType == byte.class) {
/*  88 */         return new ByteArrayAdapter((byte[])array, wrapper);
/*     */       }
/*  90 */       return new GenericPrimitiveArrayAdapter(array, wrapper);
/*     */     } 
/*  92 */     return new ObjectArrayAdapter((Object[])array, wrapper);
/*     */   }
/*     */ 
/*     */   
/*     */   private DefaultArrayAdapter(ObjectWrapper wrapper) {
/*  97 */     super(wrapper);
/*     */   }
/*     */ 
/*     */   
/*     */   public final Object getAdaptedObject(Class hint) {
/* 102 */     return getWrappedObject();
/*     */   }
/*     */   
/*     */   private static class ObjectArrayAdapter
/*     */     extends DefaultArrayAdapter {
/*     */     private final Object[] array;
/*     */     
/*     */     private ObjectArrayAdapter(Object[] array, ObjectWrapper wrapper) {
/* 110 */       super(wrapper);
/* 111 */       this.array = array;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(int index) throws TemplateModelException {
/* 116 */       return (index >= 0 && index < this.array.length) ? wrap(this.array[index]) : null;
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() throws TemplateModelException {
/* 121 */       return this.array.length;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getWrappedObject() {
/* 126 */       return this.array;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ByteArrayAdapter
/*     */     extends DefaultArrayAdapter
/*     */   {
/*     */     private final byte[] array;
/*     */     
/*     */     private ByteArrayAdapter(byte[] array, ObjectWrapper wrapper) {
/* 136 */       super(wrapper);
/* 137 */       this.array = array;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(int index) throws TemplateModelException {
/* 142 */       return (index >= 0 && index < this.array.length) ? wrap(Byte.valueOf(this.array[index])) : null;
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() throws TemplateModelException {
/* 147 */       return this.array.length;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getWrappedObject() {
/* 152 */       return this.array;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ShortArrayAdapter
/*     */     extends DefaultArrayAdapter
/*     */   {
/*     */     private final short[] array;
/*     */     
/*     */     private ShortArrayAdapter(short[] array, ObjectWrapper wrapper) {
/* 162 */       super(wrapper);
/* 163 */       this.array = array;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(int index) throws TemplateModelException {
/* 168 */       return (index >= 0 && index < this.array.length) ? wrap(Short.valueOf(this.array[index])) : null;
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() throws TemplateModelException {
/* 173 */       return this.array.length;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getWrappedObject() {
/* 178 */       return this.array;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class IntArrayAdapter
/*     */     extends DefaultArrayAdapter
/*     */   {
/*     */     private final int[] array;
/*     */     
/*     */     private IntArrayAdapter(int[] array, ObjectWrapper wrapper) {
/* 188 */       super(wrapper);
/* 189 */       this.array = array;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(int index) throws TemplateModelException {
/* 194 */       return (index >= 0 && index < this.array.length) ? wrap(Integer.valueOf(this.array[index])) : null;
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() throws TemplateModelException {
/* 199 */       return this.array.length;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getWrappedObject() {
/* 204 */       return this.array;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class LongArrayAdapter
/*     */     extends DefaultArrayAdapter
/*     */   {
/*     */     private final long[] array;
/*     */     
/*     */     private LongArrayAdapter(long[] array, ObjectWrapper wrapper) {
/* 214 */       super(wrapper);
/* 215 */       this.array = array;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(int index) throws TemplateModelException {
/* 220 */       return (index >= 0 && index < this.array.length) ? wrap(Long.valueOf(this.array[index])) : null;
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() throws TemplateModelException {
/* 225 */       return this.array.length;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getWrappedObject() {
/* 230 */       return this.array;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class FloatArrayAdapter
/*     */     extends DefaultArrayAdapter
/*     */   {
/*     */     private final float[] array;
/*     */     
/*     */     private FloatArrayAdapter(float[] array, ObjectWrapper wrapper) {
/* 240 */       super(wrapper);
/* 241 */       this.array = array;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(int index) throws TemplateModelException {
/* 246 */       return (index >= 0 && index < this.array.length) ? wrap(Float.valueOf(this.array[index])) : null;
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() throws TemplateModelException {
/* 251 */       return this.array.length;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getWrappedObject() {
/* 256 */       return this.array;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class DoubleArrayAdapter
/*     */     extends DefaultArrayAdapter
/*     */   {
/*     */     private final double[] array;
/*     */     
/*     */     private DoubleArrayAdapter(double[] array, ObjectWrapper wrapper) {
/* 266 */       super(wrapper);
/* 267 */       this.array = array;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(int index) throws TemplateModelException {
/* 272 */       return (index >= 0 && index < this.array.length) ? wrap(Double.valueOf(this.array[index])) : null;
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() throws TemplateModelException {
/* 277 */       return this.array.length;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getWrappedObject() {
/* 282 */       return this.array;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class CharArrayAdapter
/*     */     extends DefaultArrayAdapter
/*     */   {
/*     */     private final char[] array;
/*     */     
/*     */     private CharArrayAdapter(char[] array, ObjectWrapper wrapper) {
/* 292 */       super(wrapper);
/* 293 */       this.array = array;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(int index) throws TemplateModelException {
/* 298 */       return (index >= 0 && index < this.array.length) ? wrap(Character.valueOf(this.array[index])) : null;
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() throws TemplateModelException {
/* 303 */       return this.array.length;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getWrappedObject() {
/* 308 */       return this.array;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class BooleanArrayAdapter
/*     */     extends DefaultArrayAdapter
/*     */   {
/*     */     private final boolean[] array;
/*     */     
/*     */     private BooleanArrayAdapter(boolean[] array, ObjectWrapper wrapper) {
/* 318 */       super(wrapper);
/* 319 */       this.array = array;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(int index) throws TemplateModelException {
/* 324 */       return (index >= 0 && index < this.array.length) ? wrap(Boolean.valueOf(this.array[index])) : null;
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() throws TemplateModelException {
/* 329 */       return this.array.length;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getWrappedObject() {
/* 334 */       return this.array;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class GenericPrimitiveArrayAdapter
/*     */     extends DefaultArrayAdapter
/*     */   {
/*     */     private final Object array;
/*     */     
/*     */     private final int length;
/*     */ 
/*     */     
/*     */     private GenericPrimitiveArrayAdapter(Object array, ObjectWrapper wrapper) {
/* 348 */       super(wrapper);
/* 349 */       this.array = array;
/* 350 */       this.length = Array.getLength(array);
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(int index) throws TemplateModelException {
/* 355 */       return (index >= 0 && index < this.length) ? wrap(Array.get(this.array, index)) : null;
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() throws TemplateModelException {
/* 360 */       return this.length;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getWrappedObject() {
/* 365 */       return this.array;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\DefaultArrayAdapter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */