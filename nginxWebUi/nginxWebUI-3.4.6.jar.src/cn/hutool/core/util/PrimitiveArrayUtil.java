/*      */ package cn.hutool.core.util;
/*      */ 
/*      */ import java.lang.reflect.Array;
/*      */ import java.util.Arrays;
/*      */ import java.util.Random;
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
/*      */ public class PrimitiveArrayUtil
/*      */ {
/*      */   public static final int INDEX_NOT_FOUND = -1;
/*      */   
/*      */   public static boolean isEmpty(long[] array) {
/*   28 */     return (array == null || array.length == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEmpty(int[] array) {
/*   38 */     return (array == null || array.length == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEmpty(short[] array) {
/*   48 */     return (array == null || array.length == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEmpty(char[] array) {
/*   58 */     return (array == null || array.length == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEmpty(byte[] array) {
/*   68 */     return (array == null || array.length == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEmpty(double[] array) {
/*   78 */     return (array == null || array.length == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEmpty(float[] array) {
/*   88 */     return (array == null || array.length == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEmpty(boolean[] array) {
/*   98 */     return (array == null || array.length == 0);
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
/*      */   public static boolean isNotEmpty(long[] array) {
/*  110 */     return (false == isEmpty(array));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNotEmpty(int[] array) {
/*  120 */     return (false == isEmpty(array));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNotEmpty(short[] array) {
/*  130 */     return (false == isEmpty(array));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNotEmpty(char[] array) {
/*  140 */     return (false == isEmpty(array));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNotEmpty(byte[] array) {
/*  150 */     return (false == isEmpty(array));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNotEmpty(double[] array) {
/*  160 */     return (false == isEmpty(array));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNotEmpty(float[] array) {
/*  170 */     return (false == isEmpty(array));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNotEmpty(boolean[] array) {
/*  180 */     return (false == isEmpty(array));
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
/*      */   public static byte[] resize(byte[] bytes, int newSize) {
/*  195 */     if (newSize < 0) {
/*  196 */       return bytes;
/*      */     }
/*  198 */     byte[] newArray = new byte[newSize];
/*  199 */     if (newSize > 0 && isNotEmpty(bytes)) {
/*  200 */       System.arraycopy(bytes, 0, newArray, 0, Math.min(bytes.length, newSize));
/*      */     }
/*  202 */     return newArray;
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
/*      */   public static byte[] addAll(byte[]... arrays) {
/*  216 */     if (arrays.length == 1) {
/*  217 */       return arrays[0];
/*      */     }
/*      */ 
/*      */     
/*  221 */     int length = 0;
/*  222 */     for (byte[] array : arrays) {
/*  223 */       if (null != array) {
/*  224 */         length += array.length;
/*      */       }
/*      */     } 
/*      */     
/*  228 */     byte[] result = new byte[length];
/*  229 */     length = 0;
/*  230 */     for (byte[] array : arrays) {
/*  231 */       if (null != array) {
/*  232 */         System.arraycopy(array, 0, result, length, array.length);
/*  233 */         length += array.length;
/*      */       } 
/*      */     } 
/*  236 */     return result;
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
/*      */   public static int[] addAll(int[]... arrays) {
/*  248 */     if (arrays.length == 1) {
/*  249 */       return arrays[0];
/*      */     }
/*      */ 
/*      */     
/*  253 */     int length = 0;
/*  254 */     for (int[] array : arrays) {
/*  255 */       if (null != array) {
/*  256 */         length += array.length;
/*      */       }
/*      */     } 
/*      */     
/*  260 */     int[] result = new int[length];
/*  261 */     length = 0;
/*  262 */     for (int[] array : arrays) {
/*  263 */       if (null != array) {
/*  264 */         System.arraycopy(array, 0, result, length, array.length);
/*  265 */         length += array.length;
/*      */       } 
/*      */     } 
/*  268 */     return result;
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
/*      */   public static long[] addAll(long[]... arrays) {
/*  280 */     if (arrays.length == 1) {
/*  281 */       return arrays[0];
/*      */     }
/*      */ 
/*      */     
/*  285 */     int length = 0;
/*  286 */     for (long[] array : arrays) {
/*  287 */       if (null != array) {
/*  288 */         length += array.length;
/*      */       }
/*      */     } 
/*      */     
/*  292 */     long[] result = new long[length];
/*  293 */     length = 0;
/*  294 */     for (long[] array : arrays) {
/*  295 */       if (null != array) {
/*  296 */         System.arraycopy(array, 0, result, length, array.length);
/*  297 */         length += array.length;
/*      */       } 
/*      */     } 
/*  300 */     return result;
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
/*      */   public static double[] addAll(double[]... arrays) {
/*  312 */     if (arrays.length == 1) {
/*  313 */       return arrays[0];
/*      */     }
/*      */ 
/*      */     
/*  317 */     int length = 0;
/*  318 */     for (double[] array : arrays) {
/*  319 */       if (null != array) {
/*  320 */         length += array.length;
/*      */       }
/*      */     } 
/*      */     
/*  324 */     double[] result = new double[length];
/*  325 */     length = 0;
/*  326 */     for (double[] array : arrays) {
/*  327 */       if (null != array) {
/*  328 */         System.arraycopy(array, 0, result, length, array.length);
/*  329 */         length += array.length;
/*      */       } 
/*      */     } 
/*  332 */     return result;
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
/*      */   public static float[] addAll(float[]... arrays) {
/*  344 */     if (arrays.length == 1) {
/*  345 */       return arrays[0];
/*      */     }
/*      */ 
/*      */     
/*  349 */     int length = 0;
/*  350 */     for (float[] array : arrays) {
/*  351 */       if (null != array) {
/*  352 */         length += array.length;
/*      */       }
/*      */     } 
/*      */     
/*  356 */     float[] result = new float[length];
/*  357 */     length = 0;
/*  358 */     for (float[] array : arrays) {
/*  359 */       if (null != array) {
/*  360 */         System.arraycopy(array, 0, result, length, array.length);
/*  361 */         length += array.length;
/*      */       } 
/*      */     } 
/*  364 */     return result;
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
/*      */   public static char[] addAll(char[]... arrays) {
/*  376 */     if (arrays.length == 1) {
/*  377 */       return arrays[0];
/*      */     }
/*      */ 
/*      */     
/*  381 */     int length = 0;
/*  382 */     for (char[] array : arrays) {
/*  383 */       if (null != array) {
/*  384 */         length += array.length;
/*      */       }
/*      */     } 
/*      */     
/*  388 */     char[] result = new char[length];
/*  389 */     length = 0;
/*  390 */     for (char[] array : arrays) {
/*  391 */       if (null != array) {
/*  392 */         System.arraycopy(array, 0, result, length, array.length);
/*  393 */         length += array.length;
/*      */       } 
/*      */     } 
/*  396 */     return result;
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
/*      */   public static boolean[] addAll(boolean[]... arrays) {
/*  408 */     if (arrays.length == 1) {
/*  409 */       return arrays[0];
/*      */     }
/*      */ 
/*      */     
/*  413 */     int length = 0;
/*  414 */     for (boolean[] array : arrays) {
/*  415 */       if (null != array) {
/*  416 */         length += array.length;
/*      */       }
/*      */     } 
/*      */     
/*  420 */     boolean[] result = new boolean[length];
/*  421 */     length = 0;
/*  422 */     for (boolean[] array : arrays) {
/*  423 */       if (null != array) {
/*  424 */         System.arraycopy(array, 0, result, length, array.length);
/*  425 */         length += array.length;
/*      */       } 
/*      */     } 
/*  428 */     return result;
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
/*      */   public static short[] addAll(short[]... arrays) {
/*  440 */     if (arrays.length == 1) {
/*  441 */       return arrays[0];
/*      */     }
/*      */ 
/*      */     
/*  445 */     int length = 0;
/*  446 */     for (short[] array : arrays) {
/*  447 */       if (null != array) {
/*  448 */         length += array.length;
/*      */       }
/*      */     } 
/*      */     
/*  452 */     short[] result = new short[length];
/*  453 */     length = 0;
/*  454 */     for (short[] array : arrays) {
/*  455 */       if (null != array) {
/*  456 */         System.arraycopy(array, 0, result, length, array.length);
/*  457 */         length += array.length;
/*      */       } 
/*      */     } 
/*  460 */     return result;
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
/*      */   public static int[] range(int excludedEnd) {
/*  472 */     return range(0, excludedEnd, 1);
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
/*      */   public static int[] range(int includedStart, int excludedEnd) {
/*  484 */     return range(includedStart, excludedEnd, 1);
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
/*      */   public static int[] range(int includedStart, int excludedEnd, int step) {
/*  497 */     if (includedStart > excludedEnd) {
/*  498 */       int tmp = includedStart;
/*  499 */       includedStart = excludedEnd;
/*  500 */       excludedEnd = tmp;
/*      */     } 
/*      */     
/*  503 */     if (step <= 0) {
/*  504 */       step = 1;
/*      */     }
/*      */     
/*  507 */     int deviation = excludedEnd - includedStart;
/*  508 */     int length = deviation / step;
/*  509 */     if (deviation % step != 0) {
/*  510 */       length++;
/*      */     }
/*  512 */     int[] range = new int[length];
/*  513 */     for (int i = 0; i < length; i++) {
/*  514 */       range[i] = includedStart;
/*  515 */       includedStart += step;
/*      */     } 
/*  517 */     return range;
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
/*      */   public static byte[][] split(byte[] array, int len) {
/*  530 */     int amount = array.length / len;
/*  531 */     int remainder = array.length % len;
/*  532 */     if (remainder != 0) {
/*  533 */       amount++;
/*      */     }
/*  535 */     byte[][] arrays = new byte[amount][];
/*      */     
/*  537 */     for (int i = 0; i < amount; i++) {
/*  538 */       byte[] arr; if (i == amount - 1 && remainder != 0) {
/*      */         
/*  540 */         arr = new byte[remainder];
/*  541 */         System.arraycopy(array, i * len, arr, 0, remainder);
/*      */       } else {
/*  543 */         arr = new byte[len];
/*  544 */         System.arraycopy(array, i * len, arr, 0, len);
/*      */       } 
/*  546 */       arrays[i] = arr;
/*      */     } 
/*  548 */     return arrays;
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
/*      */   public static int indexOf(long[] array, long value) {
/*  562 */     if (null != array) {
/*  563 */       for (int i = 0; i < array.length; i++) {
/*  564 */         if (value == array[i]) {
/*  565 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*  569 */     return -1;
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
/*      */   public static int lastIndexOf(long[] array, long value) {
/*  581 */     if (null != array) {
/*  582 */       for (int i = array.length - 1; i >= 0; i--) {
/*  583 */         if (value == array[i]) {
/*  584 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*  588 */     return -1;
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
/*      */   public static boolean contains(long[] array, long value) {
/*  600 */     return (indexOf(array, value) > -1);
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
/*      */   public static int indexOf(int[] array, int value) {
/*  612 */     if (null != array) {
/*  613 */       for (int i = 0; i < array.length; i++) {
/*  614 */         if (value == array[i]) {
/*  615 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*  619 */     return -1;
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
/*      */   public static int lastIndexOf(int[] array, int value) {
/*  631 */     if (null != array) {
/*  632 */       for (int i = array.length - 1; i >= 0; i--) {
/*  633 */         if (value == array[i]) {
/*  634 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*  638 */     return -1;
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
/*      */   public static boolean contains(int[] array, int value) {
/*  650 */     return (indexOf(array, value) > -1);
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
/*      */   public static int indexOf(short[] array, short value) {
/*  662 */     if (null != array) {
/*  663 */       for (int i = 0; i < array.length; i++) {
/*  664 */         if (value == array[i]) {
/*  665 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*  669 */     return -1;
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
/*      */   public static int lastIndexOf(short[] array, short value) {
/*  681 */     if (null != array) {
/*  682 */       for (int i = array.length - 1; i >= 0; i--) {
/*  683 */         if (value == array[i]) {
/*  684 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*  688 */     return -1;
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
/*      */   public static boolean contains(short[] array, short value) {
/*  700 */     return (indexOf(array, value) > -1);
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
/*      */   public static int indexOf(char[] array, char value) {
/*  712 */     if (null != array) {
/*  713 */       for (int i = 0; i < array.length; i++) {
/*  714 */         if (value == array[i]) {
/*  715 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*  719 */     return -1;
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
/*      */   public static int lastIndexOf(char[] array, char value) {
/*  731 */     if (null != array) {
/*  732 */       for (int i = array.length - 1; i >= 0; i--) {
/*  733 */         if (value == array[i]) {
/*  734 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*  738 */     return -1;
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
/*      */   public static boolean contains(char[] array, char value) {
/*  750 */     return (indexOf(array, value) > -1);
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
/*      */   public static int indexOf(byte[] array, byte value) {
/*  762 */     if (null != array) {
/*  763 */       for (int i = 0; i < array.length; i++) {
/*  764 */         if (value == array[i]) {
/*  765 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*  769 */     return -1;
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
/*      */   public static int lastIndexOf(byte[] array, byte value) {
/*  781 */     if (null != array) {
/*  782 */       for (int i = array.length - 1; i >= 0; i--) {
/*  783 */         if (value == array[i]) {
/*  784 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*  788 */     return -1;
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
/*      */   public static boolean contains(byte[] array, byte value) {
/*  800 */     return (indexOf(array, value) > -1);
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
/*      */   public static int indexOf(double[] array, double value) {
/*  812 */     if (null != array) {
/*  813 */       for (int i = 0; i < array.length; i++) {
/*  814 */         if (NumberUtil.equals(value, array[i])) {
/*  815 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*  819 */     return -1;
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
/*      */   public static int lastIndexOf(double[] array, double value) {
/*  831 */     if (null != array) {
/*  832 */       for (int i = array.length - 1; i >= 0; i--) {
/*  833 */         if (NumberUtil.equals(value, array[i])) {
/*  834 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*  838 */     return -1;
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
/*      */   public static boolean contains(double[] array, double value) {
/*  850 */     return (indexOf(array, value) > -1);
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
/*      */   public static int indexOf(float[] array, float value) {
/*  862 */     if (null != array) {
/*  863 */       for (int i = 0; i < array.length; i++) {
/*  864 */         if (NumberUtil.equals(value, array[i])) {
/*  865 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*  869 */     return -1;
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
/*      */   public static int lastIndexOf(float[] array, float value) {
/*  881 */     if (null != array) {
/*  882 */       for (int i = array.length - 1; i >= 0; i--) {
/*  883 */         if (NumberUtil.equals(value, array[i])) {
/*  884 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*  888 */     return -1;
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
/*      */   public static boolean contains(float[] array, float value) {
/*  900 */     return (indexOf(array, value) > -1);
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
/*      */   public static int indexOf(boolean[] array, boolean value) {
/*  912 */     if (null != array) {
/*  913 */       for (int i = 0; i < array.length; i++) {
/*  914 */         if (value == array[i]) {
/*  915 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*  919 */     return -1;
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
/*      */   public static int lastIndexOf(boolean[] array, boolean value) {
/*  931 */     if (null != array) {
/*  932 */       for (int i = array.length - 1; i >= 0; i--) {
/*  933 */         if (value == array[i]) {
/*  934 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*  938 */     return -1;
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
/*      */   public static boolean contains(boolean[] array, boolean value) {
/*  950 */     return (indexOf(array, value) > -1);
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
/*      */   public static Integer[] wrap(int... values) {
/*  962 */     if (null == values) {
/*  963 */       return null;
/*      */     }
/*  965 */     int length = values.length;
/*  966 */     if (0 == length) {
/*  967 */       return new Integer[0];
/*      */     }
/*      */     
/*  970 */     Integer[] array = new Integer[length];
/*  971 */     for (int i = 0; i < length; i++) {
/*  972 */       array[i] = Integer.valueOf(values[i]);
/*      */     }
/*  974 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int[] unWrap(Integer... values) {
/*  984 */     if (null == values) {
/*  985 */       return null;
/*      */     }
/*  987 */     int length = values.length;
/*  988 */     if (0 == length) {
/*  989 */       return new int[0];
/*      */     }
/*      */     
/*  992 */     int[] array = new int[length];
/*  993 */     for (int i = 0; i < length; i++) {
/*  994 */       array[i] = ((Integer)ObjectUtil.defaultIfNull((T)values[i], (T)Integer.valueOf(0))).intValue();
/*      */     }
/*  996 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Long[] wrap(long... values) {
/* 1006 */     if (null == values) {
/* 1007 */       return null;
/*      */     }
/* 1009 */     int length = values.length;
/* 1010 */     if (0 == length) {
/* 1011 */       return new Long[0];
/*      */     }
/*      */     
/* 1014 */     Long[] array = new Long[length];
/* 1015 */     for (int i = 0; i < length; i++) {
/* 1016 */       array[i] = Long.valueOf(values[i]);
/*      */     }
/* 1018 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long[] unWrap(Long... values) {
/* 1028 */     if (null == values) {
/* 1029 */       return null;
/*      */     }
/* 1031 */     int length = values.length;
/* 1032 */     if (0 == length) {
/* 1033 */       return new long[0];
/*      */     }
/*      */     
/* 1036 */     long[] array = new long[length];
/* 1037 */     for (int i = 0; i < length; i++) {
/* 1038 */       array[i] = ((Long)ObjectUtil.defaultIfNull((T)values[i], (T)Long.valueOf(0L))).longValue();
/*      */     }
/* 1040 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Character[] wrap(char... values) {
/* 1050 */     if (null == values) {
/* 1051 */       return null;
/*      */     }
/* 1053 */     int length = values.length;
/* 1054 */     if (0 == length) {
/* 1055 */       return new Character[0];
/*      */     }
/*      */     
/* 1058 */     Character[] array = new Character[length];
/* 1059 */     for (int i = 0; i < length; i++) {
/* 1060 */       array[i] = Character.valueOf(values[i]);
/*      */     }
/* 1062 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static char[] unWrap(Character... values) {
/* 1072 */     if (null == values) {
/* 1073 */       return null;
/*      */     }
/* 1075 */     int length = values.length;
/* 1076 */     if (0 == length) {
/* 1077 */       return new char[0];
/*      */     }
/*      */     
/* 1080 */     char[] array = new char[length];
/* 1081 */     for (int i = 0; i < length; i++) {
/* 1082 */       array[i] = ((Character)ObjectUtil.defaultIfNull((T)values[i], (T)Character.valueOf(false))).charValue();
/*      */     }
/* 1084 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Byte[] wrap(byte... values) {
/* 1094 */     if (null == values) {
/* 1095 */       return null;
/*      */     }
/* 1097 */     int length = values.length;
/* 1098 */     if (0 == length) {
/* 1099 */       return new Byte[0];
/*      */     }
/*      */     
/* 1102 */     Byte[] array = new Byte[length];
/* 1103 */     for (int i = 0; i < length; i++) {
/* 1104 */       array[i] = Byte.valueOf(values[i]);
/*      */     }
/* 1106 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] unWrap(Byte... values) {
/* 1116 */     if (null == values) {
/* 1117 */       return null;
/*      */     }
/* 1119 */     int length = values.length;
/* 1120 */     if (0 == length) {
/* 1121 */       return new byte[0];
/*      */     }
/*      */     
/* 1124 */     byte[] array = new byte[length];
/* 1125 */     for (int i = 0; i < length; i++) {
/* 1126 */       array[i] = ((Byte)ObjectUtil.defaultIfNull((T)values[i], (T)Byte.valueOf((byte)0))).byteValue();
/*      */     }
/* 1128 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Short[] wrap(short... values) {
/* 1138 */     if (null == values) {
/* 1139 */       return null;
/*      */     }
/* 1141 */     int length = values.length;
/* 1142 */     if (0 == length) {
/* 1143 */       return new Short[0];
/*      */     }
/*      */     
/* 1146 */     Short[] array = new Short[length];
/* 1147 */     for (int i = 0; i < length; i++) {
/* 1148 */       array[i] = Short.valueOf(values[i]);
/*      */     }
/* 1150 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static short[] unWrap(Short... values) {
/* 1160 */     if (null == values) {
/* 1161 */       return null;
/*      */     }
/* 1163 */     int length = values.length;
/* 1164 */     if (0 == length) {
/* 1165 */       return new short[0];
/*      */     }
/*      */     
/* 1168 */     short[] array = new short[length];
/* 1169 */     for (int i = 0; i < length; i++) {
/* 1170 */       array[i] = ((Short)ObjectUtil.defaultIfNull((T)values[i], (T)Short.valueOf((short)0))).shortValue();
/*      */     }
/* 1172 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Float[] wrap(float... values) {
/* 1182 */     if (null == values) {
/* 1183 */       return null;
/*      */     }
/* 1185 */     int length = values.length;
/* 1186 */     if (0 == length) {
/* 1187 */       return new Float[0];
/*      */     }
/*      */     
/* 1190 */     Float[] array = new Float[length];
/* 1191 */     for (int i = 0; i < length; i++) {
/* 1192 */       array[i] = Float.valueOf(values[i]);
/*      */     }
/* 1194 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static float[] unWrap(Float... values) {
/* 1204 */     if (null == values) {
/* 1205 */       return null;
/*      */     }
/* 1207 */     int length = values.length;
/* 1208 */     if (0 == length) {
/* 1209 */       return new float[0];
/*      */     }
/*      */     
/* 1212 */     float[] array = new float[length];
/* 1213 */     for (int i = 0; i < length; i++) {
/* 1214 */       array[i] = ((Float)ObjectUtil.defaultIfNull((T)values[i], (T)Float.valueOf(0.0F))).floatValue();
/*      */     }
/* 1216 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Double[] wrap(double... values) {
/* 1226 */     if (null == values) {
/* 1227 */       return null;
/*      */     }
/* 1229 */     int length = values.length;
/* 1230 */     if (0 == length) {
/* 1231 */       return new Double[0];
/*      */     }
/*      */     
/* 1234 */     Double[] array = new Double[length];
/* 1235 */     for (int i = 0; i < length; i++) {
/* 1236 */       array[i] = Double.valueOf(values[i]);
/*      */     }
/* 1238 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double[] unWrap(Double... values) {
/* 1248 */     if (null == values) {
/* 1249 */       return null;
/*      */     }
/* 1251 */     int length = values.length;
/* 1252 */     if (0 == length) {
/* 1253 */       return new double[0];
/*      */     }
/*      */     
/* 1256 */     double[] array = new double[length];
/* 1257 */     for (int i = 0; i < length; i++) {
/* 1258 */       array[i] = ((Double)ObjectUtil.defaultIfNull((T)values[i], (T)Double.valueOf(0.0D))).doubleValue();
/*      */     }
/* 1260 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Boolean[] wrap(boolean... values) {
/* 1270 */     if (null == values) {
/* 1271 */       return null;
/*      */     }
/* 1273 */     int length = values.length;
/* 1274 */     if (0 == length) {
/* 1275 */       return new Boolean[0];
/*      */     }
/*      */     
/* 1278 */     Boolean[] array = new Boolean[length];
/* 1279 */     for (int i = 0; i < length; i++) {
/* 1280 */       array[i] = Boolean.valueOf(values[i]);
/*      */     }
/* 1282 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean[] unWrap(Boolean... values) {
/* 1292 */     if (null == values) {
/* 1293 */       return null;
/*      */     }
/* 1295 */     int length = values.length;
/* 1296 */     if (0 == length) {
/* 1297 */       return new boolean[0];
/*      */     }
/*      */     
/* 1300 */     boolean[] array = new boolean[length];
/* 1301 */     for (int i = 0; i < length; i++) {
/* 1302 */       array[i] = ((Boolean)ObjectUtil.defaultIfNull((T)values[i], (T)Boolean.valueOf(false))).booleanValue();
/*      */     }
/* 1304 */     return array;
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
/*      */   public static byte[] sub(byte[] array, int start, int end) {
/* 1320 */     int length = Array.getLength(array);
/* 1321 */     if (start < 0) {
/* 1322 */       start += length;
/*      */     }
/* 1324 */     if (end < 0) {
/* 1325 */       end += length;
/*      */     }
/* 1327 */     if (start == length) {
/* 1328 */       return new byte[0];
/*      */     }
/* 1330 */     if (start > end) {
/* 1331 */       int tmp = start;
/* 1332 */       start = end;
/* 1333 */       end = tmp;
/*      */     } 
/* 1335 */     if (end > length) {
/* 1336 */       if (start >= length) {
/* 1337 */         return new byte[0];
/*      */       }
/* 1339 */       end = length;
/*      */     } 
/* 1341 */     return Arrays.copyOfRange(array, start, end);
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
/*      */   public static int[] sub(int[] array, int start, int end) {
/* 1355 */     int length = Array.getLength(array);
/* 1356 */     if (start < 0) {
/* 1357 */       start += length;
/*      */     }
/* 1359 */     if (end < 0) {
/* 1360 */       end += length;
/*      */     }
/* 1362 */     if (start == length) {
/* 1363 */       return new int[0];
/*      */     }
/* 1365 */     if (start > end) {
/* 1366 */       int tmp = start;
/* 1367 */       start = end;
/* 1368 */       end = tmp;
/*      */     } 
/* 1370 */     if (end > length) {
/* 1371 */       if (start >= length) {
/* 1372 */         return new int[0];
/*      */       }
/* 1374 */       end = length;
/*      */     } 
/* 1376 */     return Arrays.copyOfRange(array, start, end);
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
/*      */   public static long[] sub(long[] array, int start, int end) {
/* 1390 */     int length = Array.getLength(array);
/* 1391 */     if (start < 0) {
/* 1392 */       start += length;
/*      */     }
/* 1394 */     if (end < 0) {
/* 1395 */       end += length;
/*      */     }
/* 1397 */     if (start == length) {
/* 1398 */       return new long[0];
/*      */     }
/* 1400 */     if (start > end) {
/* 1401 */       int tmp = start;
/* 1402 */       start = end;
/* 1403 */       end = tmp;
/*      */     } 
/* 1405 */     if (end > length) {
/* 1406 */       if (start >= length) {
/* 1407 */         return new long[0];
/*      */       }
/* 1409 */       end = length;
/*      */     } 
/* 1411 */     return Arrays.copyOfRange(array, start, end);
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
/*      */   public static short[] sub(short[] array, int start, int end) {
/* 1425 */     int length = Array.getLength(array);
/* 1426 */     if (start < 0) {
/* 1427 */       start += length;
/*      */     }
/* 1429 */     if (end < 0) {
/* 1430 */       end += length;
/*      */     }
/* 1432 */     if (start == length) {
/* 1433 */       return new short[0];
/*      */     }
/* 1435 */     if (start > end) {
/* 1436 */       int tmp = start;
/* 1437 */       start = end;
/* 1438 */       end = tmp;
/*      */     } 
/* 1440 */     if (end > length) {
/* 1441 */       if (start >= length) {
/* 1442 */         return new short[0];
/*      */       }
/* 1444 */       end = length;
/*      */     } 
/* 1446 */     return Arrays.copyOfRange(array, start, end);
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
/*      */   public static char[] sub(char[] array, int start, int end) {
/* 1460 */     int length = Array.getLength(array);
/* 1461 */     if (start < 0) {
/* 1462 */       start += length;
/*      */     }
/* 1464 */     if (end < 0) {
/* 1465 */       end += length;
/*      */     }
/* 1467 */     if (start == length) {
/* 1468 */       return new char[0];
/*      */     }
/* 1470 */     if (start > end) {
/* 1471 */       int tmp = start;
/* 1472 */       start = end;
/* 1473 */       end = tmp;
/*      */     } 
/* 1475 */     if (end > length) {
/* 1476 */       if (start >= length) {
/* 1477 */         return new char[0];
/*      */       }
/* 1479 */       end = length;
/*      */     } 
/* 1481 */     return Arrays.copyOfRange(array, start, end);
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
/*      */   public static double[] sub(double[] array, int start, int end) {
/* 1495 */     int length = Array.getLength(array);
/* 1496 */     if (start < 0) {
/* 1497 */       start += length;
/*      */     }
/* 1499 */     if (end < 0) {
/* 1500 */       end += length;
/*      */     }
/* 1502 */     if (start == length) {
/* 1503 */       return new double[0];
/*      */     }
/* 1505 */     if (start > end) {
/* 1506 */       int tmp = start;
/* 1507 */       start = end;
/* 1508 */       end = tmp;
/*      */     } 
/* 1510 */     if (end > length) {
/* 1511 */       if (start >= length) {
/* 1512 */         return new double[0];
/*      */       }
/* 1514 */       end = length;
/*      */     } 
/* 1516 */     return Arrays.copyOfRange(array, start, end);
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
/*      */   public static float[] sub(float[] array, int start, int end) {
/* 1530 */     int length = Array.getLength(array);
/* 1531 */     if (start < 0) {
/* 1532 */       start += length;
/*      */     }
/* 1534 */     if (end < 0) {
/* 1535 */       end += length;
/*      */     }
/* 1537 */     if (start == length) {
/* 1538 */       return new float[0];
/*      */     }
/* 1540 */     if (start > end) {
/* 1541 */       int tmp = start;
/* 1542 */       start = end;
/* 1543 */       end = tmp;
/*      */     } 
/* 1545 */     if (end > length) {
/* 1546 */       if (start >= length) {
/* 1547 */         return new float[0];
/*      */       }
/* 1549 */       end = length;
/*      */     } 
/* 1551 */     return Arrays.copyOfRange(array, start, end);
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
/*      */   public static boolean[] sub(boolean[] array, int start, int end) {
/* 1565 */     int length = Array.getLength(array);
/* 1566 */     if (start < 0) {
/* 1567 */       start += length;
/*      */     }
/* 1569 */     if (end < 0) {
/* 1570 */       end += length;
/*      */     }
/* 1572 */     if (start == length) {
/* 1573 */       return new boolean[0];
/*      */     }
/* 1575 */     if (start > end) {
/* 1576 */       int tmp = start;
/* 1577 */       start = end;
/* 1578 */       end = tmp;
/*      */     } 
/* 1580 */     if (end > length) {
/* 1581 */       if (start >= length) {
/* 1582 */         return new boolean[0];
/*      */       }
/* 1584 */       end = length;
/*      */     } 
/* 1586 */     return Arrays.copyOfRange(array, start, end);
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
/*      */   public static long[] remove(long[] array, int index) throws IllegalArgumentException {
/* 1602 */     return (long[])remove(array, index);
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
/*      */   public static int[] remove(int[] array, int index) throws IllegalArgumentException {
/* 1616 */     return (int[])remove(array, index);
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
/*      */   public static short[] remove(short[] array, int index) throws IllegalArgumentException {
/* 1630 */     return (short[])remove(array, index);
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
/*      */   public static char[] remove(char[] array, int index) throws IllegalArgumentException {
/* 1644 */     return (char[])remove(array, index);
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
/*      */   public static byte[] remove(byte[] array, int index) throws IllegalArgumentException {
/* 1658 */     return (byte[])remove(array, index);
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
/*      */   public static double[] remove(double[] array, int index) throws IllegalArgumentException {
/* 1672 */     return (double[])remove(array, index);
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
/*      */   public static float[] remove(float[] array, int index) throws IllegalArgumentException {
/* 1686 */     return (float[])remove(array, index);
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
/*      */   public static boolean[] remove(boolean[] array, int index) throws IllegalArgumentException {
/* 1700 */     return (boolean[])remove(array, index);
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
/*      */   public static Object remove(Object array, int index) throws IllegalArgumentException {
/* 1715 */     if (null == array) {
/* 1716 */       return null;
/*      */     }
/* 1718 */     int length = Array.getLength(array);
/* 1719 */     if (index < 0 || index >= length) {
/* 1720 */       return array;
/*      */     }
/*      */     
/* 1723 */     Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
/* 1724 */     System.arraycopy(array, 0, result, 0, index);
/* 1725 */     if (index < length - 1)
/*      */     {
/* 1727 */       System.arraycopy(array, index + 1, result, index, length - index - 1);
/*      */     }
/*      */     
/* 1730 */     return result;
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
/*      */   public static long[] removeEle(long[] array, long element) throws IllegalArgumentException {
/* 1746 */     return remove(array, indexOf(array, element));
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
/*      */   public static int[] removeEle(int[] array, int element) throws IllegalArgumentException {
/* 1760 */     return remove(array, indexOf(array, element));
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
/*      */   public static short[] removeEle(short[] array, short element) throws IllegalArgumentException {
/* 1774 */     return remove(array, indexOf(array, element));
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
/*      */   public static char[] removeEle(char[] array, char element) throws IllegalArgumentException {
/* 1788 */     return remove(array, indexOf(array, element));
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
/*      */   public static byte[] removeEle(byte[] array, byte element) throws IllegalArgumentException {
/* 1802 */     return remove(array, indexOf(array, element));
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
/*      */   public static double[] removeEle(double[] array, double element) throws IllegalArgumentException {
/* 1816 */     return remove(array, indexOf(array, element));
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
/*      */   public static float[] removeEle(float[] array, float element) throws IllegalArgumentException {
/* 1830 */     return remove(array, indexOf(array, element));
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
/*      */   public static boolean[] removeEle(boolean[] array, boolean element) throws IllegalArgumentException {
/* 1844 */     return remove(array, indexOf(array, element));
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
/*      */   public static long[] reverse(long[] array, int startIndexInclusive, int endIndexExclusive) {
/* 1859 */     if (isEmpty(array)) {
/* 1860 */       return array;
/*      */     }
/* 1862 */     int i = Math.max(startIndexInclusive, 0);
/* 1863 */     int j = Math.min(array.length, endIndexExclusive) - 1;
/* 1864 */     while (j > i) {
/* 1865 */       swap(array, i, j);
/* 1866 */       j--;
/* 1867 */       i++;
/*      */     } 
/* 1869 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long[] reverse(long[] array) {
/* 1880 */     return reverse(array, 0, array.length);
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
/*      */   public static int[] reverse(int[] array, int startIndexInclusive, int endIndexExclusive) {
/* 1893 */     if (isEmpty(array)) {
/* 1894 */       return array;
/*      */     }
/* 1896 */     int i = Math.max(startIndexInclusive, 0);
/* 1897 */     int j = Math.min(array.length, endIndexExclusive) - 1;
/* 1898 */     while (j > i) {
/* 1899 */       swap(array, i, j);
/* 1900 */       j--;
/* 1901 */       i++;
/*      */     } 
/* 1903 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int[] reverse(int[] array) {
/* 1914 */     return reverse(array, 0, array.length);
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
/*      */   public static short[] reverse(short[] array, int startIndexInclusive, int endIndexExclusive) {
/* 1927 */     if (isEmpty(array)) {
/* 1928 */       return array;
/*      */     }
/* 1930 */     int i = Math.max(startIndexInclusive, 0);
/* 1931 */     int j = Math.min(array.length, endIndexExclusive) - 1;
/* 1932 */     while (j > i) {
/* 1933 */       swap(array, i, j);
/* 1934 */       j--;
/* 1935 */       i++;
/*      */     } 
/* 1937 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static short[] reverse(short[] array) {
/* 1948 */     return reverse(array, 0, array.length);
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
/*      */   public static char[] reverse(char[] array, int startIndexInclusive, int endIndexExclusive) {
/* 1961 */     if (isEmpty(array)) {
/* 1962 */       return array;
/*      */     }
/* 1964 */     int i = Math.max(startIndexInclusive, 0);
/* 1965 */     int j = Math.min(array.length, endIndexExclusive) - 1;
/* 1966 */     while (j > i) {
/* 1967 */       swap(array, i, j);
/* 1968 */       j--;
/* 1969 */       i++;
/*      */     } 
/* 1971 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static char[] reverse(char[] array) {
/* 1982 */     return reverse(array, 0, array.length);
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
/*      */   public static byte[] reverse(byte[] array, int startIndexInclusive, int endIndexExclusive) {
/* 1995 */     if (isEmpty(array)) {
/* 1996 */       return array;
/*      */     }
/* 1998 */     int i = Math.max(startIndexInclusive, 0);
/* 1999 */     int j = Math.min(array.length, endIndexExclusive) - 1;
/* 2000 */     while (j > i) {
/* 2001 */       swap(array, i, j);
/* 2002 */       j--;
/* 2003 */       i++;
/*      */     } 
/* 2005 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] reverse(byte[] array) {
/* 2016 */     return reverse(array, 0, array.length);
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
/*      */   public static double[] reverse(double[] array, int startIndexInclusive, int endIndexExclusive) {
/* 2029 */     if (isEmpty(array)) {
/* 2030 */       return array;
/*      */     }
/* 2032 */     int i = Math.max(startIndexInclusive, 0);
/* 2033 */     int j = Math.min(array.length, endIndexExclusive) - 1;
/* 2034 */     while (j > i) {
/* 2035 */       swap(array, i, j);
/* 2036 */       j--;
/* 2037 */       i++;
/*      */     } 
/* 2039 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double[] reverse(double[] array) {
/* 2050 */     return reverse(array, 0, array.length);
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
/*      */   public static float[] reverse(float[] array, int startIndexInclusive, int endIndexExclusive) {
/* 2063 */     if (isEmpty(array)) {
/* 2064 */       return array;
/*      */     }
/* 2066 */     int i = Math.max(startIndexInclusive, 0);
/* 2067 */     int j = Math.min(array.length, endIndexExclusive) - 1;
/* 2068 */     while (j > i) {
/* 2069 */       swap(array, i, j);
/* 2070 */       j--;
/* 2071 */       i++;
/*      */     } 
/* 2073 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static float[] reverse(float[] array) {
/* 2084 */     return reverse(array, 0, array.length);
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
/*      */   public static boolean[] reverse(boolean[] array, int startIndexInclusive, int endIndexExclusive) {
/* 2097 */     if (isEmpty(array)) {
/* 2098 */       return array;
/*      */     }
/* 2100 */     int i = Math.max(startIndexInclusive, 0);
/* 2101 */     int j = Math.min(array.length, endIndexExclusive) - 1;
/* 2102 */     while (j > i) {
/* 2103 */       swap(array, i, j);
/* 2104 */       j--;
/* 2105 */       i++;
/*      */     } 
/* 2107 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean[] reverse(boolean[] array) {
/* 2118 */     return reverse(array, 0, array.length);
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
/*      */   public static long min(long... numberArray) {
/* 2131 */     if (isEmpty(numberArray)) {
/* 2132 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2134 */     long min = numberArray[0];
/* 2135 */     for (int i = 1; i < numberArray.length; i++) {
/* 2136 */       if (min > numberArray[i]) {
/* 2137 */         min = numberArray[i];
/*      */       }
/*      */     } 
/* 2140 */     return min;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int min(int... numberArray) {
/* 2151 */     if (isEmpty(numberArray)) {
/* 2152 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2154 */     int min = numberArray[0];
/* 2155 */     for (int i = 1; i < numberArray.length; i++) {
/* 2156 */       if (min > numberArray[i]) {
/* 2157 */         min = numberArray[i];
/*      */       }
/*      */     } 
/* 2160 */     return min;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static short min(short... numberArray) {
/* 2171 */     if (isEmpty(numberArray)) {
/* 2172 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2174 */     short min = numberArray[0];
/* 2175 */     for (int i = 1; i < numberArray.length; i++) {
/* 2176 */       if (min > numberArray[i]) {
/* 2177 */         min = numberArray[i];
/*      */       }
/*      */     } 
/* 2180 */     return min;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static char min(char... numberArray) {
/* 2191 */     if (isEmpty(numberArray)) {
/* 2192 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2194 */     char min = numberArray[0];
/* 2195 */     for (int i = 1; i < numberArray.length; i++) {
/* 2196 */       if (min > numberArray[i]) {
/* 2197 */         min = numberArray[i];
/*      */       }
/*      */     } 
/* 2200 */     return min;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte min(byte... numberArray) {
/* 2211 */     if (isEmpty(numberArray)) {
/* 2212 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2214 */     byte min = numberArray[0];
/* 2215 */     for (int i = 1; i < numberArray.length; i++) {
/* 2216 */       if (min > numberArray[i]) {
/* 2217 */         min = numberArray[i];
/*      */       }
/*      */     } 
/* 2220 */     return min;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double min(double... numberArray) {
/* 2231 */     if (isEmpty(numberArray)) {
/* 2232 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2234 */     double min = numberArray[0];
/* 2235 */     for (int i = 1; i < numberArray.length; i++) {
/* 2236 */       if (min > numberArray[i]) {
/* 2237 */         min = numberArray[i];
/*      */       }
/*      */     } 
/* 2240 */     return min;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static float min(float... numberArray) {
/* 2251 */     if (isEmpty(numberArray)) {
/* 2252 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2254 */     float min = numberArray[0];
/* 2255 */     for (int i = 1; i < numberArray.length; i++) {
/* 2256 */       if (min > numberArray[i]) {
/* 2257 */         min = numberArray[i];
/*      */       }
/*      */     } 
/* 2260 */     return min;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long max(long... numberArray) {
/* 2271 */     if (isEmpty(numberArray)) {
/* 2272 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2274 */     long max = numberArray[0];
/* 2275 */     for (int i = 1; i < numberArray.length; i++) {
/* 2276 */       if (max < numberArray[i]) {
/* 2277 */         max = numberArray[i];
/*      */       }
/*      */     } 
/* 2280 */     return max;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int max(int... numberArray) {
/* 2291 */     if (isEmpty(numberArray)) {
/* 2292 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2294 */     int max = numberArray[0];
/* 2295 */     for (int i = 1; i < numberArray.length; i++) {
/* 2296 */       if (max < numberArray[i]) {
/* 2297 */         max = numberArray[i];
/*      */       }
/*      */     } 
/* 2300 */     return max;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static short max(short... numberArray) {
/* 2311 */     if (isEmpty(numberArray)) {
/* 2312 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2314 */     short max = numberArray[0];
/* 2315 */     for (int i = 1; i < numberArray.length; i++) {
/* 2316 */       if (max < numberArray[i]) {
/* 2317 */         max = numberArray[i];
/*      */       }
/*      */     } 
/* 2320 */     return max;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static char max(char... numberArray) {
/* 2331 */     if (isEmpty(numberArray)) {
/* 2332 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2334 */     char max = numberArray[0];
/* 2335 */     for (int i = 1; i < numberArray.length; i++) {
/* 2336 */       if (max < numberArray[i]) {
/* 2337 */         max = numberArray[i];
/*      */       }
/*      */     } 
/* 2340 */     return max;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte max(byte... numberArray) {
/* 2351 */     if (isEmpty(numberArray)) {
/* 2352 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2354 */     byte max = numberArray[0];
/* 2355 */     for (int i = 1; i < numberArray.length; i++) {
/* 2356 */       if (max < numberArray[i]) {
/* 2357 */         max = numberArray[i];
/*      */       }
/*      */     } 
/* 2360 */     return max;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double max(double... numberArray) {
/* 2371 */     if (isEmpty(numberArray)) {
/* 2372 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2374 */     double max = numberArray[0];
/* 2375 */     for (int i = 1; i < numberArray.length; i++) {
/* 2376 */       if (max < numberArray[i]) {
/* 2377 */         max = numberArray[i];
/*      */       }
/*      */     } 
/* 2380 */     return max;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static float max(float... numberArray) {
/* 2391 */     if (isEmpty(numberArray)) {
/* 2392 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2394 */     float max = numberArray[0];
/* 2395 */     for (int i = 1; i < numberArray.length; i++) {
/* 2396 */       if (max < numberArray[i]) {
/* 2397 */         max = numberArray[i];
/*      */       }
/*      */     } 
/* 2400 */     return max;
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
/*      */   public static int[] shuffle(int[] array) {
/* 2414 */     return shuffle(array, RandomUtil.getRandom());
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
/*      */   public static int[] shuffle(int[] array, Random random) {
/* 2427 */     if (array == null || random == null || array.length <= 1) {
/* 2428 */       return array;
/*      */     }
/*      */     
/* 2431 */     for (int i = array.length; i > 1; i--) {
/* 2432 */       swap(array, i - 1, random.nextInt(i));
/*      */     }
/*      */     
/* 2435 */     return array;
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
/*      */   public static long[] shuffle(long[] array) {
/* 2447 */     return shuffle(array, RandomUtil.getRandom());
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
/*      */   public static long[] shuffle(long[] array, Random random) {
/* 2460 */     if (array == null || random == null || array.length <= 1) {
/* 2461 */       return array;
/*      */     }
/*      */     
/* 2464 */     for (int i = array.length; i > 1; i--) {
/* 2465 */       swap(array, i - 1, random.nextInt(i));
/*      */     }
/*      */     
/* 2468 */     return array;
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
/*      */   public static double[] shuffle(double[] array) {
/* 2480 */     return shuffle(array, RandomUtil.getRandom());
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
/*      */   public static double[] shuffle(double[] array, Random random) {
/* 2493 */     if (array == null || random == null || array.length <= 1) {
/* 2494 */       return array;
/*      */     }
/*      */     
/* 2497 */     for (int i = array.length; i > 1; i--) {
/* 2498 */       swap(array, i - 1, random.nextInt(i));
/*      */     }
/*      */     
/* 2501 */     return array;
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
/*      */   public static float[] shuffle(float[] array) {
/* 2513 */     return shuffle(array, RandomUtil.getRandom());
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
/*      */   public static float[] shuffle(float[] array, Random random) {
/* 2526 */     if (array == null || random == null || array.length <= 1) {
/* 2527 */       return array;
/*      */     }
/*      */     
/* 2530 */     for (int i = array.length; i > 1; i--) {
/* 2531 */       swap(array, i - 1, random.nextInt(i));
/*      */     }
/*      */     
/* 2534 */     return array;
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
/*      */   public static boolean[] shuffle(boolean[] array) {
/* 2546 */     return shuffle(array, RandomUtil.getRandom());
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
/*      */   public static boolean[] shuffle(boolean[] array, Random random) {
/* 2559 */     if (array == null || random == null || array.length <= 1) {
/* 2560 */       return array;
/*      */     }
/*      */     
/* 2563 */     for (int i = array.length; i > 1; i--) {
/* 2564 */       swap(array, i - 1, random.nextInt(i));
/*      */     }
/*      */     
/* 2567 */     return array;
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
/*      */   public static byte[] shuffle(byte[] array) {
/* 2579 */     return shuffle(array, RandomUtil.getRandom());
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
/*      */   public static byte[] shuffle(byte[] array, Random random) {
/* 2592 */     if (array == null || random == null || array.length <= 1) {
/* 2593 */       return array;
/*      */     }
/*      */     
/* 2596 */     for (int i = array.length; i > 1; i--) {
/* 2597 */       swap(array, i - 1, random.nextInt(i));
/*      */     }
/*      */     
/* 2600 */     return array;
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
/*      */   public static char[] shuffle(char[] array) {
/* 2612 */     return shuffle(array, RandomUtil.getRandom());
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
/*      */   public static char[] shuffle(char[] array, Random random) {
/* 2625 */     if (array == null || random == null || array.length <= 1) {
/* 2626 */       return array;
/*      */     }
/*      */     
/* 2629 */     for (int i = array.length; i > 1; i--) {
/* 2630 */       swap(array, i - 1, random.nextInt(i));
/*      */     }
/*      */     
/* 2633 */     return array;
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
/*      */   public static short[] shuffle(short[] array) {
/* 2645 */     return shuffle(array, RandomUtil.getRandom());
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
/*      */   public static short[] shuffle(short[] array, Random random) {
/* 2658 */     if (array == null || random == null || array.length <= 1) {
/* 2659 */       return array;
/*      */     }
/*      */     
/* 2662 */     for (int i = array.length; i > 1; i--) {
/* 2663 */       swap(array, i - 1, random.nextInt(i));
/*      */     }
/*      */     
/* 2666 */     return array;
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
/*      */   public static int[] swap(int[] array, int index1, int index2) {
/* 2681 */     if (isEmpty(array)) {
/* 2682 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2684 */     int tmp = array[index1];
/* 2685 */     array[index1] = array[index2];
/* 2686 */     array[index2] = tmp;
/* 2687 */     return array;
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
/*      */   public static long[] swap(long[] array, int index1, int index2) {
/* 2700 */     if (isEmpty(array)) {
/* 2701 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2703 */     long tmp = array[index1];
/* 2704 */     array[index1] = array[index2];
/* 2705 */     array[index2] = tmp;
/* 2706 */     return array;
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
/*      */   public static double[] swap(double[] array, int index1, int index2) {
/* 2719 */     if (isEmpty(array)) {
/* 2720 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2722 */     double tmp = array[index1];
/* 2723 */     array[index1] = array[index2];
/* 2724 */     array[index2] = tmp;
/* 2725 */     return array;
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
/*      */   public static float[] swap(float[] array, int index1, int index2) {
/* 2738 */     if (isEmpty(array)) {
/* 2739 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2741 */     float tmp = array[index1];
/* 2742 */     array[index1] = array[index2];
/* 2743 */     array[index2] = tmp;
/* 2744 */     return array;
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
/*      */   public static boolean[] swap(boolean[] array, int index1, int index2) {
/* 2757 */     if (isEmpty(array)) {
/* 2758 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2760 */     boolean tmp = array[index1];
/* 2761 */     array[index1] = array[index2];
/* 2762 */     array[index2] = tmp;
/* 2763 */     return array;
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
/*      */   public static byte[] swap(byte[] array, int index1, int index2) {
/* 2776 */     if (isEmpty(array)) {
/* 2777 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2779 */     byte tmp = array[index1];
/* 2780 */     array[index1] = array[index2];
/* 2781 */     array[index2] = tmp;
/* 2782 */     return array;
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
/*      */   public static char[] swap(char[] array, int index1, int index2) {
/* 2795 */     if (isEmpty(array)) {
/* 2796 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2798 */     char tmp = array[index1];
/* 2799 */     array[index1] = array[index2];
/* 2800 */     array[index2] = tmp;
/* 2801 */     return array;
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
/*      */   public static short[] swap(short[] array, int index1, int index2) {
/* 2814 */     if (isEmpty(array)) {
/* 2815 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 2817 */     short tmp = array[index1];
/* 2818 */     array[index1] = array[index2];
/* 2819 */     array[index2] = tmp;
/* 2820 */     return array;
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
/*      */   public static boolean isSorted(byte[] array) {
/* 2832 */     return isSortedASC(array);
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
/*      */   public static boolean isSortedASC(byte[] array) {
/* 2844 */     if (array == null) {
/* 2845 */       return false;
/*      */     }
/*      */     
/* 2848 */     for (int i = 0; i < array.length - 1; i++) {
/* 2849 */       if (array[i] > array[i + 1]) {
/* 2850 */         return false;
/*      */       }
/*      */     } 
/*      */     
/* 2854 */     return true;
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
/*      */   public static boolean isSortedDESC(byte[] array) {
/* 2866 */     if (array == null) {
/* 2867 */       return false;
/*      */     }
/*      */     
/* 2870 */     for (int i = 0; i < array.length - 1; i++) {
/* 2871 */       if (array[i] < array[i + 1]) {
/* 2872 */         return false;
/*      */       }
/*      */     } 
/*      */     
/* 2876 */     return true;
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
/*      */   public static boolean isSorted(short[] array) {
/* 2888 */     return isSortedASC(array);
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
/*      */   public static boolean isSortedASC(short[] array) {
/* 2900 */     if (array == null) {
/* 2901 */       return false;
/*      */     }
/*      */     
/* 2904 */     for (int i = 0; i < array.length - 1; i++) {
/* 2905 */       if (array[i] > array[i + 1]) {
/* 2906 */         return false;
/*      */       }
/*      */     } 
/*      */     
/* 2910 */     return true;
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
/*      */   public static boolean isSortedDESC(short[] array) {
/* 2922 */     if (array == null) {
/* 2923 */       return false;
/*      */     }
/*      */     
/* 2926 */     for (int i = 0; i < array.length - 1; i++) {
/* 2927 */       if (array[i] < array[i + 1]) {
/* 2928 */         return false;
/*      */       }
/*      */     } 
/*      */     
/* 2932 */     return true;
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
/*      */   public static boolean isSorted(char[] array) {
/* 2944 */     return isSortedASC(array);
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
/*      */   public static boolean isSortedASC(char[] array) {
/* 2956 */     if (array == null) {
/* 2957 */       return false;
/*      */     }
/*      */     
/* 2960 */     for (int i = 0; i < array.length - 1; i++) {
/* 2961 */       if (array[i] > array[i + 1]) {
/* 2962 */         return false;
/*      */       }
/*      */     } 
/*      */     
/* 2966 */     return true;
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
/*      */   public static boolean isSortedDESC(char[] array) {
/* 2978 */     if (array == null) {
/* 2979 */       return false;
/*      */     }
/*      */     
/* 2982 */     for (int i = 0; i < array.length - 1; i++) {
/* 2983 */       if (array[i] < array[i + 1]) {
/* 2984 */         return false;
/*      */       }
/*      */     } 
/*      */     
/* 2988 */     return true;
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
/*      */   public static boolean isSorted(int[] array) {
/* 3000 */     return isSortedASC(array);
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
/*      */   public static boolean isSortedASC(int[] array) {
/* 3012 */     if (array == null) {
/* 3013 */       return false;
/*      */     }
/*      */     
/* 3016 */     for (int i = 0; i < array.length - 1; i++) {
/* 3017 */       if (array[i] > array[i + 1]) {
/* 3018 */         return false;
/*      */       }
/*      */     } 
/*      */     
/* 3022 */     return true;
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
/*      */   public static boolean isSortedDESC(int[] array) {
/* 3034 */     if (array == null) {
/* 3035 */       return false;
/*      */     }
/*      */     
/* 3038 */     for (int i = 0; i < array.length - 1; i++) {
/* 3039 */       if (array[i] < array[i + 1]) {
/* 3040 */         return false;
/*      */       }
/*      */     } 
/*      */     
/* 3044 */     return true;
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
/*      */   public static boolean isSorted(long[] array) {
/* 3056 */     return isSortedASC(array);
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
/*      */   public static boolean isSortedASC(long[] array) {
/* 3068 */     if (array == null) {
/* 3069 */       return false;
/*      */     }
/*      */     
/* 3072 */     for (int i = 0; i < array.length - 1; i++) {
/* 3073 */       if (array[i] > array[i + 1]) {
/* 3074 */         return false;
/*      */       }
/*      */     } 
/*      */     
/* 3078 */     return true;
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
/*      */   public static boolean isSortedDESC(long[] array) {
/* 3090 */     if (array == null) {
/* 3091 */       return false;
/*      */     }
/*      */     
/* 3094 */     for (int i = 0; i < array.length - 1; i++) {
/* 3095 */       if (array[i] < array[i + 1]) {
/* 3096 */         return false;
/*      */       }
/*      */     } 
/*      */     
/* 3100 */     return true;
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
/*      */   public static boolean isSorted(double[] array) {
/* 3112 */     return isSortedASC(array);
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
/*      */   public static boolean isSortedASC(double[] array) {
/* 3124 */     if (array == null) {
/* 3125 */       return false;
/*      */     }
/*      */     
/* 3128 */     for (int i = 0; i < array.length - 1; i++) {
/* 3129 */       if (array[i] > array[i + 1]) {
/* 3130 */         return false;
/*      */       }
/*      */     } 
/*      */     
/* 3134 */     return true;
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
/*      */   public static boolean isSortedDESC(double[] array) {
/* 3146 */     if (array == null) {
/* 3147 */       return false;
/*      */     }
/*      */     
/* 3150 */     for (int i = 0; i < array.length - 1; i++) {
/* 3151 */       if (array[i] < array[i + 1]) {
/* 3152 */         return false;
/*      */       }
/*      */     } 
/*      */     
/* 3156 */     return true;
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
/*      */   public static boolean isSorted(float[] array) {
/* 3168 */     return isSortedASC(array);
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
/*      */   public static boolean isSortedASC(float[] array) {
/* 3180 */     if (array == null) {
/* 3181 */       return false;
/*      */     }
/*      */     
/* 3184 */     for (int i = 0; i < array.length - 1; i++) {
/* 3185 */       if (array[i] > array[i + 1]) {
/* 3186 */         return false;
/*      */       }
/*      */     } 
/*      */     
/* 3190 */     return true;
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
/*      */   public static boolean isSortedDESC(float[] array) {
/* 3202 */     if (array == null) {
/* 3203 */       return false;
/*      */     }
/*      */     
/* 3206 */     for (int i = 0; i < array.length - 1; i++) {
/* 3207 */       if (array[i] < array[i + 1]) {
/* 3208 */         return false;
/*      */       }
/*      */     } 
/*      */     
/* 3212 */     return true;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\PrimitiveArrayUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */