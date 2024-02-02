package freemarker.template;

import freemarker.ext.util.WrapperTemplateModel;
import java.io.Serializable;
import java.lang.reflect.Array;

public abstract class DefaultArrayAdapter extends WrappingTemplateModel implements TemplateSequenceModel, AdapterTemplateModel, WrapperTemplateModel, Serializable {
   public static DefaultArrayAdapter adapt(Object array, ObjectWrapperAndUnwrapper wrapper) {
      Class componentType = array.getClass().getComponentType();
      if (componentType == null) {
         throw new IllegalArgumentException("Not an array");
      } else if (componentType.isPrimitive()) {
         if (componentType == Integer.TYPE) {
            return new IntArrayAdapter((int[])((int[])array), wrapper);
         } else if (componentType == Double.TYPE) {
            return new DoubleArrayAdapter((double[])((double[])array), wrapper);
         } else if (componentType == Long.TYPE) {
            return new LongArrayAdapter((long[])((long[])array), wrapper);
         } else if (componentType == Boolean.TYPE) {
            return new BooleanArrayAdapter((boolean[])((boolean[])array), wrapper);
         } else if (componentType == Float.TYPE) {
            return new FloatArrayAdapter((float[])((float[])array), wrapper);
         } else if (componentType == Character.TYPE) {
            return new CharArrayAdapter((char[])((char[])array), wrapper);
         } else if (componentType == Short.TYPE) {
            return new ShortArrayAdapter((short[])((short[])array), wrapper);
         } else {
            return (DefaultArrayAdapter)(componentType == Byte.TYPE ? new ByteArrayAdapter((byte[])((byte[])array), wrapper) : new GenericPrimitiveArrayAdapter(array, wrapper));
         }
      } else {
         return new ObjectArrayAdapter((Object[])((Object[])array), wrapper);
      }
   }

   private DefaultArrayAdapter(ObjectWrapper wrapper) {
      super(wrapper);
   }

   public final Object getAdaptedObject(Class hint) {
      return this.getWrappedObject();
   }

   // $FF: synthetic method
   DefaultArrayAdapter(ObjectWrapper x0, Object x1) {
      this(x0);
   }

   private static class GenericPrimitiveArrayAdapter extends DefaultArrayAdapter {
      private final Object array;
      private final int length;

      private GenericPrimitiveArrayAdapter(Object array, ObjectWrapper wrapper) {
         super(wrapper, null);
         this.array = array;
         this.length = Array.getLength(array);
      }

      public TemplateModel get(int index) throws TemplateModelException {
         return index >= 0 && index < this.length ? this.wrap(Array.get(this.array, index)) : null;
      }

      public int size() throws TemplateModelException {
         return this.length;
      }

      public Object getWrappedObject() {
         return this.array;
      }

      // $FF: synthetic method
      GenericPrimitiveArrayAdapter(Object x0, ObjectWrapper x1, Object x2) {
         this(x0, x1);
      }
   }

   private static class BooleanArrayAdapter extends DefaultArrayAdapter {
      private final boolean[] array;

      private BooleanArrayAdapter(boolean[] array, ObjectWrapper wrapper) {
         super(wrapper, null);
         this.array = array;
      }

      public TemplateModel get(int index) throws TemplateModelException {
         return index >= 0 && index < this.array.length ? this.wrap(this.array[index]) : null;
      }

      public int size() throws TemplateModelException {
         return this.array.length;
      }

      public Object getWrappedObject() {
         return this.array;
      }

      // $FF: synthetic method
      BooleanArrayAdapter(boolean[] x0, ObjectWrapper x1, Object x2) {
         this(x0, x1);
      }
   }

   private static class CharArrayAdapter extends DefaultArrayAdapter {
      private final char[] array;

      private CharArrayAdapter(char[] array, ObjectWrapper wrapper) {
         super(wrapper, null);
         this.array = array;
      }

      public TemplateModel get(int index) throws TemplateModelException {
         return index >= 0 && index < this.array.length ? this.wrap(this.array[index]) : null;
      }

      public int size() throws TemplateModelException {
         return this.array.length;
      }

      public Object getWrappedObject() {
         return this.array;
      }

      // $FF: synthetic method
      CharArrayAdapter(char[] x0, ObjectWrapper x1, Object x2) {
         this(x0, x1);
      }
   }

   private static class DoubleArrayAdapter extends DefaultArrayAdapter {
      private final double[] array;

      private DoubleArrayAdapter(double[] array, ObjectWrapper wrapper) {
         super(wrapper, null);
         this.array = array;
      }

      public TemplateModel get(int index) throws TemplateModelException {
         return index >= 0 && index < this.array.length ? this.wrap(this.array[index]) : null;
      }

      public int size() throws TemplateModelException {
         return this.array.length;
      }

      public Object getWrappedObject() {
         return this.array;
      }

      // $FF: synthetic method
      DoubleArrayAdapter(double[] x0, ObjectWrapper x1, Object x2) {
         this(x0, x1);
      }
   }

   private static class FloatArrayAdapter extends DefaultArrayAdapter {
      private final float[] array;

      private FloatArrayAdapter(float[] array, ObjectWrapper wrapper) {
         super(wrapper, null);
         this.array = array;
      }

      public TemplateModel get(int index) throws TemplateModelException {
         return index >= 0 && index < this.array.length ? this.wrap(this.array[index]) : null;
      }

      public int size() throws TemplateModelException {
         return this.array.length;
      }

      public Object getWrappedObject() {
         return this.array;
      }

      // $FF: synthetic method
      FloatArrayAdapter(float[] x0, ObjectWrapper x1, Object x2) {
         this(x0, x1);
      }
   }

   private static class LongArrayAdapter extends DefaultArrayAdapter {
      private final long[] array;

      private LongArrayAdapter(long[] array, ObjectWrapper wrapper) {
         super(wrapper, null);
         this.array = array;
      }

      public TemplateModel get(int index) throws TemplateModelException {
         return index >= 0 && index < this.array.length ? this.wrap(this.array[index]) : null;
      }

      public int size() throws TemplateModelException {
         return this.array.length;
      }

      public Object getWrappedObject() {
         return this.array;
      }

      // $FF: synthetic method
      LongArrayAdapter(long[] x0, ObjectWrapper x1, Object x2) {
         this(x0, x1);
      }
   }

   private static class IntArrayAdapter extends DefaultArrayAdapter {
      private final int[] array;

      private IntArrayAdapter(int[] array, ObjectWrapper wrapper) {
         super(wrapper, null);
         this.array = array;
      }

      public TemplateModel get(int index) throws TemplateModelException {
         return index >= 0 && index < this.array.length ? this.wrap(this.array[index]) : null;
      }

      public int size() throws TemplateModelException {
         return this.array.length;
      }

      public Object getWrappedObject() {
         return this.array;
      }

      // $FF: synthetic method
      IntArrayAdapter(int[] x0, ObjectWrapper x1, Object x2) {
         this(x0, x1);
      }
   }

   private static class ShortArrayAdapter extends DefaultArrayAdapter {
      private final short[] array;

      private ShortArrayAdapter(short[] array, ObjectWrapper wrapper) {
         super(wrapper, null);
         this.array = array;
      }

      public TemplateModel get(int index) throws TemplateModelException {
         return index >= 0 && index < this.array.length ? this.wrap(this.array[index]) : null;
      }

      public int size() throws TemplateModelException {
         return this.array.length;
      }

      public Object getWrappedObject() {
         return this.array;
      }

      // $FF: synthetic method
      ShortArrayAdapter(short[] x0, ObjectWrapper x1, Object x2) {
         this(x0, x1);
      }
   }

   private static class ByteArrayAdapter extends DefaultArrayAdapter {
      private final byte[] array;

      private ByteArrayAdapter(byte[] array, ObjectWrapper wrapper) {
         super(wrapper, null);
         this.array = array;
      }

      public TemplateModel get(int index) throws TemplateModelException {
         return index >= 0 && index < this.array.length ? this.wrap(this.array[index]) : null;
      }

      public int size() throws TemplateModelException {
         return this.array.length;
      }

      public Object getWrappedObject() {
         return this.array;
      }

      // $FF: synthetic method
      ByteArrayAdapter(byte[] x0, ObjectWrapper x1, Object x2) {
         this(x0, x1);
      }
   }

   private static class ObjectArrayAdapter extends DefaultArrayAdapter {
      private final Object[] array;

      private ObjectArrayAdapter(Object[] array, ObjectWrapper wrapper) {
         super(wrapper, null);
         this.array = array;
      }

      public TemplateModel get(int index) throws TemplateModelException {
         return index >= 0 && index < this.array.length ? this.wrap(this.array[index]) : null;
      }

      public int size() throws TemplateModelException {
         return this.array.length;
      }

      public Object getWrappedObject() {
         return this.array;
      }

      // $FF: synthetic method
      ObjectArrayAdapter(Object[] x0, ObjectWrapper x1, Object x2) {
         this(x0, x1);
      }
   }
}
