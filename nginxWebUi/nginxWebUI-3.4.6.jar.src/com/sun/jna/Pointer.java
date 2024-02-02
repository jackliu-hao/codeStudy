/*      */ package com.sun.jna;
/*      */ 
/*      */ import java.io.PrintWriter;
/*      */ import java.io.StringWriter;
/*      */ import java.lang.reflect.Array;
/*      */ import java.nio.Buffer;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.ByteOrder;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
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
/*      */ public class Pointer
/*      */ {
/*   51 */   public static final Pointer NULL = null;
/*      */   protected long peer;
/*      */   
/*      */   public static final Pointer createConstant(long peer) {
/*   55 */     return new Opaque(peer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final Pointer createConstant(int peer) {
/*   63 */     return new Opaque(peer & 0xFFFFFFFFFFFFFFFFL);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Pointer() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Pointer(long peer) {
/*   79 */     this.peer = peer;
/*      */   }
/*      */ 
/*      */   
/*      */   public Pointer share(long offset) {
/*   84 */     return share(offset, 0L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Pointer share(long offset, long sz) {
/*   91 */     if (offset == 0L) {
/*   92 */       return this;
/*      */     }
/*   94 */     return new Pointer(this.peer + offset);
/*      */   }
/*      */ 
/*      */   
/*      */   public void clear(long size) {
/*   99 */     setMemory(0L, size, (byte)0);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean equals(Object o) {
/*  104 */     if (o == this) {
/*  105 */       return true;
/*      */     }
/*  107 */     if (o == null) {
/*  108 */       return false;
/*      */     }
/*  110 */     return (o instanceof Pointer && ((Pointer)o).peer == this.peer);
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/*  115 */     return (int)((this.peer >>> 32L) + (this.peer & 0xFFFFFFFFFFFFFFFFL));
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
/*      */   public long indexOf(long offset, byte value) {
/*  127 */     return Native.indexOf(this, this.peer, offset, value);
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
/*      */   public void read(long offset, byte[] buf, int index, int length) {
/*  140 */     Native.read(this, this.peer, offset, buf, index, length);
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
/*      */   public void read(long offset, short[] buf, int index, int length) {
/*  153 */     Native.read(this, this.peer, offset, buf, index, length);
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
/*      */   public void read(long offset, char[] buf, int index, int length) {
/*  166 */     Native.read(this, this.peer, offset, buf, index, length);
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
/*      */   public void read(long offset, int[] buf, int index, int length) {
/*  179 */     Native.read(this, this.peer, offset, buf, index, length);
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
/*      */   public void read(long offset, long[] buf, int index, int length) {
/*  192 */     Native.read(this, this.peer, offset, buf, index, length);
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
/*      */   public void read(long offset, float[] buf, int index, int length) {
/*  205 */     Native.read(this, this.peer, offset, buf, index, length);
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
/*      */   public void read(long offset, double[] buf, int index, int length) {
/*  218 */     Native.read(this, this.peer, offset, buf, index, length);
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
/*      */   public void read(long offset, Pointer[] buf, int index, int length) {
/*  231 */     for (int i = 0; i < length; i++) {
/*  232 */       Pointer p = getPointer(offset + (i * Native.POINTER_SIZE));
/*  233 */       Pointer oldp = buf[i + index];
/*      */       
/*  235 */       if (oldp == null || p == null || p.peer != oldp.peer) {
/*  236 */         buf[i + index] = p;
/*      */       }
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
/*      */   public void write(long offset, byte[] buf, int index, int length) {
/*  257 */     Native.write(this, this.peer, offset, buf, index, length);
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
/*      */   public void write(long offset, short[] buf, int index, int length) {
/*  271 */     Native.write(this, this.peer, offset, buf, index, length);
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
/*      */   public void write(long offset, char[] buf, int index, int length) {
/*  285 */     Native.write(this, this.peer, offset, buf, index, length);
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
/*      */   public void write(long offset, int[] buf, int index, int length) {
/*  299 */     Native.write(this, this.peer, offset, buf, index, length);
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
/*      */   public void write(long offset, long[] buf, int index, int length) {
/*  313 */     Native.write(this, this.peer, offset, buf, index, length);
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
/*      */   public void write(long offset, float[] buf, int index, int length) {
/*  327 */     Native.write(this, this.peer, offset, buf, index, length);
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
/*      */   public void write(long offset, double[] buf, int index, int length) {
/*  341 */     Native.write(this, this.peer, offset, buf, index, length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void write(long bOff, Pointer[] buf, int index, int length) {
/*  352 */     for (int i = 0; i < length; i++) {
/*  353 */       setPointer(bOff + (i * Native.POINTER_SIZE), buf[index + i]);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Object getValue(long offset, Class<?> type, Object currentValue) {
/*  363 */     Object result = null;
/*  364 */     if (Structure.class.isAssignableFrom(type)) {
/*  365 */       Structure s = (Structure)currentValue;
/*  366 */       if (Structure.ByReference.class.isAssignableFrom(type)) {
/*  367 */         s = Structure.updateStructureByReference(type, s, getPointer(offset));
/*      */       } else {
/*  369 */         s.useMemory(this, (int)offset, true);
/*  370 */         s.read();
/*      */       } 
/*  372 */       result = s;
/*  373 */     } else if (type == boolean.class || type == Boolean.class) {
/*  374 */       result = Function.valueOf((getInt(offset) != 0));
/*  375 */     } else if (type == byte.class || type == Byte.class) {
/*  376 */       result = Byte.valueOf(getByte(offset));
/*  377 */     } else if (type == short.class || type == Short.class) {
/*  378 */       result = Short.valueOf(getShort(offset));
/*  379 */     } else if (type == char.class || type == Character.class) {
/*  380 */       result = Character.valueOf(getChar(offset));
/*  381 */     } else if (type == int.class || type == Integer.class) {
/*  382 */       result = Integer.valueOf(getInt(offset));
/*  383 */     } else if (type == long.class || type == Long.class) {
/*  384 */       result = Long.valueOf(getLong(offset));
/*  385 */     } else if (type == float.class || type == Float.class) {
/*  386 */       result = Float.valueOf(getFloat(offset));
/*  387 */     } else if (type == double.class || type == Double.class) {
/*  388 */       result = Double.valueOf(getDouble(offset));
/*  389 */     } else if (Pointer.class.isAssignableFrom(type)) {
/*  390 */       Pointer p = getPointer(offset);
/*  391 */       if (p != null) {
/*  392 */         Pointer oldp = (currentValue instanceof Pointer) ? (Pointer)currentValue : null;
/*      */         
/*  394 */         if (oldp == null || p.peer != oldp.peer) {
/*  395 */           result = p;
/*      */         } else {
/*  397 */           result = oldp;
/*      */         } 
/*      */       } 
/*  400 */     } else if (type == String.class) {
/*  401 */       Pointer p = getPointer(offset);
/*  402 */       result = (p != null) ? p.getString(0L) : null;
/*  403 */     } else if (type == WString.class) {
/*  404 */       Pointer p = getPointer(offset);
/*  405 */       result = (p != null) ? new WString(p.getWideString(0L)) : null;
/*  406 */     } else if (Callback.class.isAssignableFrom(type)) {
/*      */ 
/*      */       
/*  409 */       Pointer fp = getPointer(offset);
/*  410 */       if (fp == null) {
/*  411 */         result = null;
/*      */       } else {
/*  413 */         Callback cb = (Callback)currentValue;
/*  414 */         Pointer oldfp = CallbackReference.getFunctionPointer(cb);
/*  415 */         if (!fp.equals(oldfp)) {
/*  416 */           cb = CallbackReference.getCallback(type, fp);
/*      */         }
/*  418 */         result = cb;
/*      */       } 
/*  420 */     } else if (Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(type)) {
/*  421 */       Pointer bp = getPointer(offset);
/*  422 */       if (bp == null) {
/*  423 */         result = null;
/*      */       } else {
/*      */         
/*  426 */         Pointer oldbp = (currentValue == null) ? null : Native.getDirectBufferPointer((Buffer)currentValue);
/*  427 */         if (oldbp == null || !oldbp.equals(bp)) {
/*  428 */           throw new IllegalStateException("Can't autogenerate a direct buffer on memory read");
/*      */         }
/*  430 */         result = currentValue;
/*      */       } 
/*  432 */     } else if (NativeMapped.class.isAssignableFrom(type)) {
/*  433 */       NativeMapped nm = (NativeMapped)currentValue;
/*  434 */       if (nm != null) {
/*  435 */         Object value = getValue(offset, nm.nativeType(), null);
/*  436 */         result = nm.fromNative(value, new FromNativeContext(type));
/*  437 */         if (nm.equals(result)) {
/*  438 */           result = nm;
/*      */         }
/*      */       } else {
/*  441 */         NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
/*  442 */         Object value = getValue(offset, tc.nativeType(), null);
/*  443 */         result = tc.fromNative(value, new FromNativeContext(type));
/*      */       } 
/*  445 */     } else if (type.isArray()) {
/*  446 */       result = currentValue;
/*  447 */       if (result == null) {
/*  448 */         throw new IllegalStateException("Need an initialized array");
/*      */       }
/*  450 */       readArray(offset, result, type.getComponentType());
/*      */     } else {
/*  452 */       throw new IllegalArgumentException("Reading \"" + type + "\" from memory is not supported");
/*      */     } 
/*  454 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   private void readArray(long offset, Object o, Class<?> cls) {
/*  459 */     int length = 0;
/*  460 */     length = Array.getLength(o);
/*  461 */     Object result = o;
/*      */     
/*  463 */     if (cls == byte.class) {
/*  464 */       read(offset, (byte[])result, 0, length);
/*      */     }
/*  466 */     else if (cls == short.class) {
/*  467 */       read(offset, (short[])result, 0, length);
/*      */     }
/*  469 */     else if (cls == char.class) {
/*  470 */       read(offset, (char[])result, 0, length);
/*      */     }
/*  472 */     else if (cls == int.class) {
/*  473 */       read(offset, (int[])result, 0, length);
/*      */     }
/*  475 */     else if (cls == long.class) {
/*  476 */       read(offset, (long[])result, 0, length);
/*      */     }
/*  478 */     else if (cls == float.class) {
/*  479 */       read(offset, (float[])result, 0, length);
/*      */     }
/*  481 */     else if (cls == double.class) {
/*  482 */       read(offset, (double[])result, 0, length);
/*      */     }
/*  484 */     else if (Pointer.class.isAssignableFrom(cls)) {
/*  485 */       read(offset, (Pointer[])result, 0, length);
/*      */     }
/*  487 */     else if (Structure.class.isAssignableFrom(cls)) {
/*  488 */       Structure[] sarray = (Structure[])result;
/*  489 */       if (Structure.ByReference.class.isAssignableFrom(cls)) {
/*  490 */         Pointer[] parray = getPointerArray(offset, sarray.length);
/*  491 */         for (int i = 0; i < sarray.length; i++) {
/*  492 */           sarray[i] = Structure.updateStructureByReference(cls, sarray[i], parray[i]);
/*      */         }
/*      */       } else {
/*      */         
/*  496 */         Structure first = sarray[0];
/*  497 */         if (first == null) {
/*  498 */           first = Structure.newInstance(cls, share(offset));
/*  499 */           first.conditionalAutoRead();
/*  500 */           sarray[0] = first;
/*      */         } else {
/*      */           
/*  503 */           first.useMemory(this, (int)offset, true);
/*  504 */           first.read();
/*      */         } 
/*  506 */         Structure[] tmp = first.toArray(sarray.length);
/*  507 */         for (int i = 1; i < sarray.length; i++) {
/*  508 */           if (sarray[i] == null) {
/*      */             
/*  510 */             sarray[i] = tmp[i];
/*      */           } else {
/*      */             
/*  513 */             sarray[i].useMemory(this, (int)(offset + (i * sarray[i].size())), true);
/*  514 */             sarray[i].read();
/*      */           }
/*      */         
/*      */         } 
/*      */       } 
/*  519 */     } else if (NativeMapped.class.isAssignableFrom(cls)) {
/*  520 */       NativeMapped[] array = (NativeMapped[])result;
/*  521 */       NativeMappedConverter tc = NativeMappedConverter.getInstance(cls);
/*  522 */       int size = Native.getNativeSize(result.getClass(), result) / array.length;
/*  523 */       for (int i = 0; i < array.length; i++) {
/*  524 */         Object value = getValue(offset + (size * i), tc.nativeType(), array[i]);
/*  525 */         array[i] = (NativeMapped)tc.fromNative(value, new FromNativeContext(cls));
/*      */       } 
/*      */     } else {
/*      */       
/*  529 */       throw new IllegalArgumentException("Reading array of " + cls + " from memory not supported");
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
/*      */   public byte getByte(long offset) {
/*  544 */     return Native.getByte(this, this.peer, offset);
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
/*      */   public char getChar(long offset) {
/*  556 */     return Native.getChar(this, this.peer, offset);
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
/*      */   public short getShort(long offset) {
/*  568 */     return Native.getShort(this, this.peer, offset);
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
/*      */   public int getInt(long offset) {
/*  580 */     return Native.getInt(this, this.peer, offset);
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
/*      */   public long getLong(long offset) {
/*  592 */     return Native.getLong(this, this.peer, offset);
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
/*      */   public NativeLong getNativeLong(long offset) {
/*  604 */     return new NativeLong((NativeLong.SIZE == 8) ? getLong(offset) : getInt(offset));
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
/*      */   public float getFloat(long offset) {
/*  616 */     return Native.getFloat(this, this.peer, offset);
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
/*      */   public double getDouble(long offset) {
/*  628 */     return Native.getDouble(this, this.peer, offset);
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
/*      */   public Pointer getPointer(long offset) {
/*  642 */     return Native.getPointer(this.peer + offset);
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
/*      */   public ByteBuffer getByteBuffer(long offset, long length) {
/*  654 */     return Native.getDirectByteBuffer(this, this.peer, offset, length).order(ByteOrder.nativeOrder());
/*      */   }
/*      */ 
/*      */   
/*      */   public String getWideString(long offset) {
/*  659 */     return Native.getWideString(this, this.peer, offset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getString(long offset) {
/*  670 */     return getString(offset, Native.getDefaultStringEncoding());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getString(long offset, String encoding) {
/*  681 */     return Native.getString(this, offset, encoding);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] getByteArray(long offset, int arraySize) {
/*  688 */     byte[] buf = new byte[arraySize];
/*  689 */     read(offset, buf, 0, arraySize);
/*  690 */     return buf;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public char[] getCharArray(long offset, int arraySize) {
/*  697 */     char[] buf = new char[arraySize];
/*  698 */     read(offset, buf, 0, arraySize);
/*  699 */     return buf;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public short[] getShortArray(long offset, int arraySize) {
/*  706 */     short[] buf = new short[arraySize];
/*  707 */     read(offset, buf, 0, arraySize);
/*  708 */     return buf;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int[] getIntArray(long offset, int arraySize) {
/*  715 */     int[] buf = new int[arraySize];
/*  716 */     read(offset, buf, 0, arraySize);
/*  717 */     return buf;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long[] getLongArray(long offset, int arraySize) {
/*  724 */     long[] buf = new long[arraySize];
/*  725 */     read(offset, buf, 0, arraySize);
/*  726 */     return buf;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public float[] getFloatArray(long offset, int arraySize) {
/*  733 */     float[] buf = new float[arraySize];
/*  734 */     read(offset, buf, 0, arraySize);
/*  735 */     return buf;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double[] getDoubleArray(long offset, int arraySize) {
/*  742 */     double[] buf = new double[arraySize];
/*  743 */     read(offset, buf, 0, arraySize);
/*  744 */     return buf;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Pointer[] getPointerArray(long offset) {
/*  751 */     List<Pointer> array = new ArrayList<Pointer>();
/*  752 */     int addOffset = 0;
/*  753 */     Pointer p = getPointer(offset);
/*  754 */     while (p != null) {
/*  755 */       array.add(p);
/*  756 */       addOffset += Native.POINTER_SIZE;
/*  757 */       p = getPointer(offset + addOffset);
/*      */     } 
/*  759 */     return array.<Pointer>toArray(new Pointer[0]);
/*      */   }
/*      */ 
/*      */   
/*      */   public Pointer[] getPointerArray(long offset, int arraySize) {
/*  764 */     Pointer[] buf = new Pointer[arraySize];
/*  765 */     read(offset, buf, 0, arraySize);
/*  766 */     return buf;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String[] getStringArray(long offset) {
/*  777 */     return getStringArray(offset, -1, Native.getDefaultStringEncoding());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String[] getStringArray(long offset, String encoding) {
/*  785 */     return getStringArray(offset, -1, encoding);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String[] getStringArray(long offset, int length) {
/*  795 */     return getStringArray(offset, length, Native.getDefaultStringEncoding());
/*      */   }
/*      */   
/*      */   public String[] getWideStringArray(long offset) {
/*  799 */     return getWideStringArray(offset, -1);
/*      */   }
/*      */   
/*      */   public String[] getWideStringArray(long offset, int length) {
/*  803 */     return getStringArray(offset, length, "--WIDE-STRING--");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String[] getStringArray(long offset, int length, String encoding) {
/*  814 */     List<String> strings = new ArrayList<String>();
/*      */     
/*  816 */     int addOffset = 0;
/*  817 */     if (length != -1) {
/*  818 */       Pointer p = getPointer(offset + addOffset);
/*  819 */       int count = 0;
/*  820 */       while (count++ < length) {
/*      */ 
/*      */ 
/*      */         
/*  824 */         String s = (p == null) ? null : ("--WIDE-STRING--".equals(encoding) ? p.getWideString(0L) : p.getString(0L, encoding));
/*  825 */         strings.add(s);
/*  826 */         if (count < length) {
/*  827 */           addOffset += Native.POINTER_SIZE;
/*  828 */           p = getPointer(offset + addOffset);
/*      */         } 
/*      */       } 
/*      */     } else {
/*  832 */       Pointer p; while ((p = getPointer(offset + addOffset)) != null) {
/*      */ 
/*      */         
/*  835 */         String s = "--WIDE-STRING--".equals(encoding) ? p.getWideString(0L) : p.getString(0L, encoding);
/*  836 */         strings.add(s);
/*  837 */         addOffset += Native.POINTER_SIZE;
/*      */       } 
/*      */     } 
/*  840 */     return strings.<String>toArray(new String[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void setValue(long offset, Object value, Class<?> type) {
/*  850 */     if (type == boolean.class || type == Boolean.class) {
/*  851 */       setInt(offset, Boolean.TRUE.equals(value) ? -1 : 0);
/*  852 */     } else if (type == byte.class || type == Byte.class) {
/*  853 */       setByte(offset, (value == null) ? 0 : ((Byte)value).byteValue());
/*  854 */     } else if (type == short.class || type == Short.class) {
/*  855 */       setShort(offset, (value == null) ? 0 : ((Short)value).shortValue());
/*  856 */     } else if (type == char.class || type == Character.class) {
/*  857 */       setChar(offset, (value == null) ? Character.MIN_VALUE : ((Character)value).charValue());
/*  858 */     } else if (type == int.class || type == Integer.class) {
/*  859 */       setInt(offset, (value == null) ? 0 : ((Integer)value).intValue());
/*  860 */     } else if (type == long.class || type == Long.class) {
/*  861 */       setLong(offset, (value == null) ? 0L : ((Long)value).longValue());
/*  862 */     } else if (type == float.class || type == Float.class) {
/*  863 */       setFloat(offset, (value == null) ? 0.0F : ((Float)value).floatValue());
/*  864 */     } else if (type == double.class || type == Double.class) {
/*  865 */       setDouble(offset, (value == null) ? 0.0D : ((Double)value).doubleValue());
/*  866 */     } else if (type == Pointer.class) {
/*  867 */       setPointer(offset, (Pointer)value);
/*  868 */     } else if (type == String.class) {
/*  869 */       setPointer(offset, (Pointer)value);
/*  870 */     } else if (type == WString.class) {
/*  871 */       setPointer(offset, (Pointer)value);
/*  872 */     } else if (Structure.class.isAssignableFrom(type)) {
/*  873 */       Structure s = (Structure)value;
/*  874 */       if (Structure.ByReference.class.isAssignableFrom(type)) {
/*  875 */         setPointer(offset, (s == null) ? null : s.getPointer());
/*  876 */         if (s != null) {
/*  877 */           s.autoWrite();
/*      */         }
/*      */       } else {
/*      */         
/*  881 */         s.useMemory(this, (int)offset, true);
/*  882 */         s.write();
/*      */       } 
/*  884 */     } else if (Callback.class.isAssignableFrom(type)) {
/*  885 */       setPointer(offset, CallbackReference.getFunctionPointer((Callback)value));
/*  886 */     } else if (Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(type)) {
/*      */       
/*  888 */       Pointer p = (value == null) ? null : Native.getDirectBufferPointer((Buffer)value);
/*  889 */       setPointer(offset, p);
/*  890 */     } else if (NativeMapped.class.isAssignableFrom(type)) {
/*  891 */       NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
/*  892 */       Class<?> nativeType = tc.nativeType();
/*  893 */       setValue(offset, tc.toNative(value, new ToNativeContext()), nativeType);
/*  894 */     } else if (type.isArray()) {
/*  895 */       writeArray(offset, value, type.getComponentType());
/*      */     } else {
/*  897 */       throw new IllegalArgumentException("Writing " + type + " to memory is not supported");
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void writeArray(long offset, Object value, Class<?> cls) {
/*  903 */     if (cls == byte.class) {
/*  904 */       byte[] buf = (byte[])value;
/*  905 */       write(offset, buf, 0, buf.length);
/*  906 */     } else if (cls == short.class) {
/*  907 */       short[] buf = (short[])value;
/*  908 */       write(offset, buf, 0, buf.length);
/*  909 */     } else if (cls == char.class) {
/*  910 */       char[] buf = (char[])value;
/*  911 */       write(offset, buf, 0, buf.length);
/*  912 */     } else if (cls == int.class) {
/*  913 */       int[] buf = (int[])value;
/*  914 */       write(offset, buf, 0, buf.length);
/*  915 */     } else if (cls == long.class) {
/*  916 */       long[] buf = (long[])value;
/*  917 */       write(offset, buf, 0, buf.length);
/*  918 */     } else if (cls == float.class) {
/*  919 */       float[] buf = (float[])value;
/*  920 */       write(offset, buf, 0, buf.length);
/*  921 */     } else if (cls == double.class) {
/*  922 */       double[] buf = (double[])value;
/*  923 */       write(offset, buf, 0, buf.length);
/*  924 */     } else if (Pointer.class.isAssignableFrom(cls)) {
/*  925 */       Pointer[] buf = (Pointer[])value;
/*  926 */       write(offset, buf, 0, buf.length);
/*  927 */     } else if (Structure.class.isAssignableFrom(cls)) {
/*  928 */       Structure[] sbuf = (Structure[])value;
/*  929 */       if (Structure.ByReference.class.isAssignableFrom(cls)) {
/*  930 */         Pointer[] buf = new Pointer[sbuf.length];
/*  931 */         for (int i = 0; i < sbuf.length; i++) {
/*  932 */           if (sbuf[i] == null) {
/*  933 */             buf[i] = null;
/*      */           } else {
/*  935 */             buf[i] = sbuf[i].getPointer();
/*  936 */             sbuf[i].write();
/*      */           } 
/*      */         } 
/*  939 */         write(offset, buf, 0, buf.length);
/*      */       } else {
/*  941 */         Structure first = sbuf[0];
/*  942 */         if (first == null) {
/*  943 */           first = Structure.newInstance(cls, share(offset));
/*  944 */           sbuf[0] = first;
/*      */         } else {
/*  946 */           first.useMemory(this, (int)offset, true);
/*      */         } 
/*  948 */         first.write();
/*  949 */         Structure[] tmp = first.toArray(sbuf.length);
/*  950 */         for (int i = 1; i < sbuf.length; i++) {
/*  951 */           if (sbuf[i] == null) {
/*  952 */             sbuf[i] = tmp[i];
/*      */           } else {
/*  954 */             sbuf[i].useMemory(this, (int)(offset + (i * sbuf[i].size())), true);
/*      */           } 
/*  956 */           sbuf[i].write();
/*      */         } 
/*      */       } 
/*  959 */     } else if (NativeMapped.class.isAssignableFrom(cls)) {
/*  960 */       NativeMapped[] buf = (NativeMapped[])value;
/*  961 */       NativeMappedConverter tc = NativeMappedConverter.getInstance(cls);
/*  962 */       Class<?> nativeType = tc.nativeType();
/*  963 */       int size = Native.getNativeSize(value.getClass(), value) / buf.length;
/*  964 */       for (int i = 0; i < buf.length; i++) {
/*  965 */         Object element = tc.toNative(buf[i], new ToNativeContext());
/*  966 */         setValue(offset + (i * size), element, nativeType);
/*      */       } 
/*      */     } else {
/*  969 */       throw new IllegalArgumentException("Writing array of " + cls + " to memory not supported");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMemory(long offset, long length, byte value) {
/*  980 */     Native.setMemory(this, this.peer, offset, length, value);
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
/*      */   public void setByte(long offset, byte value) {
/*  993 */     Native.setByte(this, this.peer, offset, value);
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
/*      */   public void setShort(long offset, short value) {
/* 1006 */     Native.setShort(this, this.peer, offset, value);
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
/*      */   public void setChar(long offset, char value) {
/* 1019 */     Native.setChar(this, this.peer, offset, value);
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
/*      */   public void setInt(long offset, int value) {
/* 1032 */     Native.setInt(this, this.peer, offset, value);
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
/*      */   public void setLong(long offset, long value) {
/* 1045 */     Native.setLong(this, this.peer, offset, value);
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
/*      */   public void setNativeLong(long offset, NativeLong value) {
/* 1058 */     if (NativeLong.SIZE == 8) {
/* 1059 */       setLong(offset, value.longValue());
/*      */     } else {
/* 1061 */       setInt(offset, value.intValue());
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
/*      */   public void setFloat(long offset, float value) {
/* 1075 */     Native.setFloat(this, this.peer, offset, value);
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
/*      */   public void setDouble(long offset, double value) {
/* 1088 */     Native.setDouble(this, this.peer, offset, value);
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
/*      */   public void setPointer(long offset, Pointer value) {
/* 1103 */     Native.setPointer(this, this.peer, offset, (value != null) ? value.peer : 0L);
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
/*      */   public void setWideString(long offset, String value) {
/* 1115 */     Native.setWideString(this, this.peer, offset, value);
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
/*      */   public void setString(long offset, WString value) {
/* 1127 */     setWideString(offset, (value == null) ? null : value.toString());
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
/*      */   public void setString(long offset, String value) {
/* 1140 */     setString(offset, value, Native.getDefaultStringEncoding());
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
/*      */   public void setString(long offset, String value, String encoding) {
/* 1153 */     byte[] data = Native.getBytes(value, encoding);
/* 1154 */     write(offset, data, 0, data.length);
/* 1155 */     setByte(offset + data.length, (byte)0);
/*      */   }
/*      */ 
/*      */   
/*      */   public String dump(long offset, int size) {
/* 1160 */     int BYTES_PER_ROW = 4;
/* 1161 */     String TITLE = "memory dump";
/*      */     
/* 1163 */     StringWriter sw = new StringWriter("memory dump".length() + 2 + size * 2 + size / 4 * 4);
/* 1164 */     PrintWriter out = new PrintWriter(sw);
/* 1165 */     out.println("memory dump");
/*      */     
/* 1167 */     for (int i = 0; i < size; i++) {
/*      */       
/* 1169 */       byte b = getByte(offset + i);
/* 1170 */       if (i % 4 == 0) out.print("["); 
/* 1171 */       if (b >= 0 && b < 16)
/* 1172 */         out.print("0"); 
/* 1173 */       out.print(Integer.toHexString(b & 0xFF));
/* 1174 */       if (i % 4 == 3 && i < size - 1)
/* 1175 */         out.println("]"); 
/*      */     } 
/* 1177 */     if (sw.getBuffer().charAt(sw.getBuffer().length() - 2) != ']') {
/* 1178 */       out.println("]");
/*      */     }
/* 1180 */     return sw.toString();
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1185 */     return "native@0x" + Long.toHexString(this.peer);
/*      */   }
/*      */ 
/*      */   
/*      */   public static long nativeValue(Pointer p) {
/* 1190 */     return (p == null) ? 0L : p.peer;
/*      */   }
/*      */ 
/*      */   
/*      */   public static void nativeValue(Pointer p, long value) {
/* 1195 */     p.peer = value;
/*      */   }
/*      */   
/*      */   private static class Opaque extends Pointer {
/*      */     private Opaque(long peer) {
/* 1200 */       super(peer);
/* 1201 */       this.MSG = "This pointer is opaque: " + this;
/*      */     } private final String MSG;
/*      */     public Pointer share(long offset, long size) {
/* 1204 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void clear(long size) {
/* 1208 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public long indexOf(long offset, byte value) {
/* 1212 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void read(long bOff, byte[] buf, int index, int length) {
/* 1216 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void read(long bOff, char[] buf, int index, int length) {
/* 1220 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void read(long bOff, short[] buf, int index, int length) {
/* 1224 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void read(long bOff, int[] buf, int index, int length) {
/* 1228 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void read(long bOff, long[] buf, int index, int length) {
/* 1232 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void read(long bOff, float[] buf, int index, int length) {
/* 1236 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void read(long bOff, double[] buf, int index, int length) {
/* 1240 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void read(long bOff, Pointer[] buf, int index, int length) {
/* 1244 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void write(long bOff, byte[] buf, int index, int length) {
/* 1248 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void write(long bOff, char[] buf, int index, int length) {
/* 1252 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void write(long bOff, short[] buf, int index, int length) {
/* 1256 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void write(long bOff, int[] buf, int index, int length) {
/* 1260 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void write(long bOff, long[] buf, int index, int length) {
/* 1264 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void write(long bOff, float[] buf, int index, int length) {
/* 1268 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void write(long bOff, double[] buf, int index, int length) {
/* 1272 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void write(long bOff, Pointer[] buf, int index, int length) {
/* 1276 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public ByteBuffer getByteBuffer(long offset, long length) {
/* 1280 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public byte getByte(long bOff) {
/* 1284 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public char getChar(long bOff) {
/* 1288 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public short getShort(long bOff) {
/* 1292 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public int getInt(long bOff) {
/* 1296 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public long getLong(long bOff) {
/* 1300 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public float getFloat(long bOff) {
/* 1304 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public double getDouble(long bOff) {
/* 1308 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public Pointer getPointer(long bOff) {
/* 1312 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public String getString(long bOff, String encoding) {
/* 1316 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public String getWideString(long bOff) {
/* 1320 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void setByte(long bOff, byte value) {
/* 1324 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void setChar(long bOff, char value) {
/* 1328 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void setShort(long bOff, short value) {
/* 1332 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void setInt(long bOff, int value) {
/* 1336 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void setLong(long bOff, long value) {
/* 1340 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void setFloat(long bOff, float value) {
/* 1344 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void setDouble(long bOff, double value) {
/* 1348 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void setPointer(long offset, Pointer value) {
/* 1352 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void setString(long offset, String value, String encoding) {
/* 1356 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void setWideString(long offset, String value) {
/* 1360 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public void setMemory(long offset, long size, byte value) {
/* 1364 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public String dump(long offset, int size) {
/* 1368 */       throw new UnsupportedOperationException(this.MSG);
/*      */     }
/*      */     
/*      */     public String toString() {
/* 1372 */       return "const@0x" + Long.toHexString(this.peer);
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\Pointer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */