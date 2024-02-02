/*      */ package org.h2.mvstore.type;
/*      */ 
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.lang.reflect.Array;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.Arrays;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.UUID;
/*      */ import org.h2.mvstore.DataUtils;
/*      */ import org.h2.mvstore.WriteBuffer;
/*      */ import org.h2.util.Utils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ObjectDataType
/*      */   extends BasicDataType<Object>
/*      */ {
/*      */   static final int TYPE_NULL = 0;
/*      */   static final int TYPE_BOOLEAN = 1;
/*      */   static final int TYPE_BYTE = 2;
/*      */   static final int TYPE_SHORT = 3;
/*      */   static final int TYPE_INT = 4;
/*      */   static final int TYPE_LONG = 5;
/*      */   static final int TYPE_BIG_INTEGER = 6;
/*      */   static final int TYPE_FLOAT = 7;
/*      */   static final int TYPE_DOUBLE = 8;
/*      */   static final int TYPE_BIG_DECIMAL = 9;
/*      */   static final int TYPE_CHAR = 10;
/*      */   static final int TYPE_STRING = 11;
/*      */   static final int TYPE_UUID = 12;
/*      */   static final int TYPE_DATE = 13;
/*      */   static final int TYPE_ARRAY = 14;
/*      */   static final int TYPE_SERIALIZED_OBJECT = 19;
/*      */   static final int TAG_BOOLEAN_TRUE = 32;
/*      */   static final int TAG_INTEGER_NEGATIVE = 33;
/*      */   static final int TAG_INTEGER_FIXED = 34;
/*      */   static final int TAG_LONG_NEGATIVE = 35;
/*      */   static final int TAG_LONG_FIXED = 36;
/*      */   static final int TAG_BIG_INTEGER_0 = 37;
/*      */   static final int TAG_BIG_INTEGER_1 = 38;
/*      */   static final int TAG_BIG_INTEGER_SMALL = 39;
/*      */   static final int TAG_FLOAT_0 = 40;
/*      */   static final int TAG_FLOAT_1 = 41;
/*      */   static final int TAG_FLOAT_FIXED = 42;
/*      */   static final int TAG_DOUBLE_0 = 43;
/*      */   static final int TAG_DOUBLE_1 = 44;
/*      */   static final int TAG_DOUBLE_FIXED = 45;
/*      */   static final int TAG_BIG_DECIMAL_0 = 46;
/*      */   static final int TAG_BIG_DECIMAL_1 = 47;
/*      */   static final int TAG_BIG_DECIMAL_SMALL = 48;
/*      */   static final int TAG_BIG_DECIMAL_SMALL_SCALED = 49;
/*      */   static final int TAG_INTEGER_0_15 = 64;
/*      */   static final int TAG_LONG_0_7 = 80;
/*      */   static final int TAG_STRING_0_15 = 88;
/*      */   static final int TAG_BYTE_ARRAY_0_15 = 104;
/*   85 */   static final int FLOAT_ZERO_BITS = Float.floatToIntBits(0.0F);
/*   86 */   static final int FLOAT_ONE_BITS = Float.floatToIntBits(1.0F);
/*   87 */   static final long DOUBLE_ZERO_BITS = Double.doubleToLongBits(0.0D);
/*   88 */   static final long DOUBLE_ONE_BITS = Double.doubleToLongBits(1.0D);
/*      */   
/*   90 */   static final Class<?>[] COMMON_CLASSES = new Class[] { boolean.class, byte.class, short.class, char.class, int.class, long.class, float.class, double.class, Object.class, Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Long.class, BigInteger.class, Float.class, Double.class, BigDecimal.class, String.class, UUID.class, Date.class };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class Holder
/*      */   {
/*   98 */     private static final HashMap<Class<?>, Integer> COMMON_CLASSES_MAP = new HashMap<>(32); static {
/*      */       byte b;
/*      */       int i;
/*  101 */       for (b = 0, i = ObjectDataType.COMMON_CLASSES.length; b < i; b++) {
/*  102 */         COMMON_CLASSES_MAP.put(ObjectDataType.COMMON_CLASSES[b], Integer.valueOf(b));
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     static Integer getCommonClassId(Class<?> param1Class) {
/*  113 */       return COMMON_CLASSES_MAP.get(param1Class);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*  118 */   private AutoDetectDataType<Object> last = selectDataType(0);
/*      */ 
/*      */   
/*      */   public Object[] createStorage(int paramInt) {
/*  122 */     return new Object[paramInt];
/*      */   }
/*      */ 
/*      */   
/*      */   public int compare(Object paramObject1, Object paramObject2) {
/*  127 */     int i = getTypeId(paramObject1);
/*  128 */     int j = i - getTypeId(paramObject2);
/*  129 */     if (j == 0) {
/*  130 */       return newType(i).compare(paramObject1, paramObject2);
/*      */     }
/*  132 */     return Integer.signum(j);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getMemory(Object paramObject) {
/*  137 */     return switchType(paramObject).getMemory(paramObject);
/*      */   }
/*      */ 
/*      */   
/*      */   public void write(WriteBuffer paramWriteBuffer, Object paramObject) {
/*  142 */     switchType(paramObject).write(paramWriteBuffer, paramObject);
/*      */   }
/*      */ 
/*      */   
/*      */   private AutoDetectDataType<Object> newType(int paramInt) {
/*  147 */     if (paramInt == this.last.typeId) {
/*  148 */       return this.last;
/*      */     }
/*  150 */     return selectDataType(paramInt);
/*      */   }
/*      */ 
/*      */   
/*      */   private AutoDetectDataType selectDataType(int paramInt) {
/*  155 */     switch (paramInt) {
/*      */       case 0:
/*  157 */         return NullType.INSTANCE;
/*      */       case 1:
/*  159 */         return BooleanType.INSTANCE;
/*      */       case 2:
/*  161 */         return ByteType.INSTANCE;
/*      */       case 3:
/*  163 */         return ShortType.INSTANCE;
/*      */       case 10:
/*  165 */         return CharacterType.INSTANCE;
/*      */       case 4:
/*  167 */         return IntegerType.INSTANCE;
/*      */       case 5:
/*  169 */         return LongType.INSTANCE;
/*      */       case 7:
/*  171 */         return FloatType.INSTANCE;
/*      */       case 8:
/*  173 */         return DoubleType.INSTANCE;
/*      */       case 6:
/*  175 */         return BigIntegerType.INSTANCE;
/*      */       case 9:
/*  177 */         return BigDecimalType.INSTANCE;
/*      */       case 11:
/*  179 */         return StringType.INSTANCE;
/*      */       case 12:
/*  181 */         return UUIDType.INSTANCE;
/*      */       case 13:
/*  183 */         return DateType.INSTANCE;
/*      */       case 14:
/*  185 */         return new ObjectArrayType();
/*      */       case 19:
/*  187 */         return new SerializedObjectType(this);
/*      */     } 
/*  189 */     throw DataUtils.newMVStoreException(3, "Unsupported type {0}", new Object[] {
/*  190 */           Integer.valueOf(paramInt)
/*      */         });
/*      */   }
/*      */   
/*      */   public Object read(ByteBuffer paramByteBuffer) {
/*      */     byte b1;
/*  196 */     byte b = paramByteBuffer.get();
/*      */     
/*  198 */     if (b <= 19) {
/*  199 */       b1 = b;
/*      */     } else {
/*  201 */       switch (b) {
/*      */         case 32:
/*  203 */           b1 = 1;
/*      */           break;
/*      */         case 33:
/*      */         case 34:
/*  207 */           b1 = 4;
/*      */           break;
/*      */         case 35:
/*      */         case 36:
/*  211 */           b1 = 5;
/*      */           break;
/*      */         case 37:
/*      */         case 38:
/*      */         case 39:
/*  216 */           b1 = 6;
/*      */           break;
/*      */         case 40:
/*      */         case 41:
/*      */         case 42:
/*  221 */           b1 = 7;
/*      */           break;
/*      */         case 43:
/*      */         case 44:
/*      */         case 45:
/*  226 */           b1 = 8;
/*      */           break;
/*      */         case 46:
/*      */         case 47:
/*      */         case 48:
/*      */         case 49:
/*  232 */           b1 = 9;
/*      */           break;
/*      */         default:
/*  235 */           if (b >= 64 && b <= 79) {
/*  236 */             b1 = 4; break;
/*  237 */           }  if (b >= 88 && b <= 103) {
/*      */             
/*  239 */             b1 = 11; break;
/*  240 */           }  if (b >= 80 && b <= 87) {
/*  241 */             b1 = 5; break;
/*  242 */           }  if (b >= 104 && b <= 119) {
/*      */             
/*  244 */             b1 = 14; break;
/*      */           } 
/*  246 */           throw DataUtils.newMVStoreException(6, "Unknown tag {0}", new Object[] {
/*      */                 
/*  248 */                 Integer.valueOf(b)
/*      */               });
/*      */       } 
/*      */     } 
/*  252 */     AutoDetectDataType<Object> autoDetectDataType = this.last;
/*  253 */     if (b1 != autoDetectDataType.typeId) {
/*  254 */       this.last = autoDetectDataType = newType(b1);
/*      */     }
/*  256 */     return autoDetectDataType.read(paramByteBuffer, b);
/*      */   }
/*      */   
/*      */   private static int getTypeId(Object paramObject) {
/*  260 */     if (paramObject instanceof Integer)
/*  261 */       return 4; 
/*  262 */     if (paramObject instanceof String)
/*  263 */       return 11; 
/*  264 */     if (paramObject instanceof Long)
/*  265 */       return 5; 
/*  266 */     if (paramObject instanceof Double)
/*  267 */       return 8; 
/*  268 */     if (paramObject instanceof Float)
/*  269 */       return 7; 
/*  270 */     if (paramObject instanceof Boolean)
/*  271 */       return 1; 
/*  272 */     if (paramObject instanceof UUID)
/*  273 */       return 12; 
/*  274 */     if (paramObject instanceof Byte)
/*  275 */       return 2; 
/*  276 */     if (paramObject instanceof Short)
/*  277 */       return 3; 
/*  278 */     if (paramObject instanceof Character)
/*  279 */       return 10; 
/*  280 */     if (paramObject == null)
/*  281 */       return 0; 
/*  282 */     if (isDate(paramObject))
/*  283 */       return 13; 
/*  284 */     if (isBigInteger(paramObject))
/*  285 */       return 6; 
/*  286 */     if (isBigDecimal(paramObject))
/*  287 */       return 9; 
/*  288 */     if (paramObject.getClass().isArray()) {
/*  289 */       return 14;
/*      */     }
/*  291 */     return 19;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   AutoDetectDataType<Object> switchType(Object paramObject) {
/*  301 */     int i = getTypeId(paramObject);
/*  302 */     AutoDetectDataType<Object> autoDetectDataType = this.last;
/*  303 */     if (i != autoDetectDataType.typeId) {
/*  304 */       this.last = autoDetectDataType = newType(i);
/*      */     }
/*  306 */     return autoDetectDataType;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static boolean isBigInteger(Object paramObject) {
/*  316 */     return (paramObject != null && paramObject.getClass() == BigInteger.class);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static boolean isBigDecimal(Object paramObject) {
/*  326 */     return (paramObject != null && paramObject.getClass() == BigDecimal.class);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static boolean isDate(Object paramObject) {
/*  336 */     return (paramObject != null && paramObject.getClass() == Date.class);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static boolean isArray(Object paramObject) {
/*  346 */     return (paramObject != null && paramObject.getClass().isArray());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] serialize(Object paramObject) {
/*      */     try {
/*  357 */       ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/*  358 */       ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
/*  359 */       objectOutputStream.writeObject(paramObject);
/*  360 */       return byteArrayOutputStream.toByteArray();
/*  361 */     } catch (Throwable throwable) {
/*  362 */       throw DataUtils.newIllegalArgumentException("Could not serialize {0}", new Object[] { paramObject, throwable });
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
/*      */   public static Object deserialize(byte[] paramArrayOfbyte) {
/*      */     try {
/*  375 */       ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
/*  376 */       ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
/*  377 */       return objectInputStream.readObject();
/*  378 */     } catch (Throwable throwable) {
/*  379 */       throw DataUtils.newIllegalArgumentException("Could not deserialize {0}", new Object[] {
/*  380 */             Arrays.toString(paramArrayOfbyte), throwable
/*      */           });
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
/*      */   public static int compareNotNull(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
/*  397 */     if (paramArrayOfbyte1 == paramArrayOfbyte2) {
/*  398 */       return 0;
/*      */     }
/*  400 */     int i = Math.min(paramArrayOfbyte1.length, paramArrayOfbyte2.length);
/*  401 */     for (byte b = 0; b < i; b++) {
/*  402 */       int j = paramArrayOfbyte1[b] & 0xFF;
/*  403 */       int k = paramArrayOfbyte2[b] & 0xFF;
/*  404 */       if (j != k) {
/*  405 */         return (j > k) ? 1 : -1;
/*      */       }
/*      */     } 
/*  408 */     return Integer.signum(paramArrayOfbyte1.length - paramArrayOfbyte2.length);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static abstract class AutoDetectDataType<T>
/*      */     extends BasicDataType<T>
/*      */   {
/*      */     private final ObjectDataType base;
/*      */ 
/*      */     
/*      */     final int typeId;
/*      */ 
/*      */ 
/*      */     
/*      */     AutoDetectDataType(int param1Int) {
/*  424 */       this.base = null;
/*  425 */       this.typeId = param1Int;
/*      */     }
/*      */     
/*      */     AutoDetectDataType(ObjectDataType param1ObjectDataType, int param1Int) {
/*  429 */       this.base = param1ObjectDataType;
/*  430 */       this.typeId = param1Int;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getMemory(T param1T) {
/*  435 */       return getType(param1T).getMemory(param1T);
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(WriteBuffer param1WriteBuffer, T param1T) {
/*  440 */       getType(param1T).write(param1WriteBuffer, param1T);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     DataType<Object> getType(Object param1Object) {
/*  450 */       return this.base.switchType(param1Object);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     abstract Object read(ByteBuffer param1ByteBuffer, int param1Int);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class NullType
/*      */     extends AutoDetectDataType<Object>
/*      */   {
/*  472 */     static final NullType INSTANCE = new NullType();
/*      */     
/*      */     private NullType() {
/*  475 */       super(0);
/*      */     }
/*      */ 
/*      */     
/*      */     public Object[] createStorage(int param1Int) {
/*  480 */       return null;
/*      */     }
/*      */ 
/*      */     
/*      */     public int compare(Object param1Object1, Object param1Object2) {
/*  485 */       return 0;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getMemory(Object param1Object) {
/*  490 */       return 0;
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(WriteBuffer param1WriteBuffer, Object param1Object) {
/*  495 */       param1WriteBuffer.put((byte)0);
/*      */     }
/*      */ 
/*      */     
/*      */     public Object read(ByteBuffer param1ByteBuffer) {
/*  500 */       return null;
/*      */     }
/*      */ 
/*      */     
/*      */     public Object read(ByteBuffer param1ByteBuffer, int param1Int) {
/*  505 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class BooleanType
/*      */     extends AutoDetectDataType<Boolean>
/*      */   {
/*  518 */     static final BooleanType INSTANCE = new BooleanType();
/*      */     
/*      */     private BooleanType() {
/*  521 */       super(1);
/*      */     }
/*      */ 
/*      */     
/*      */     public Boolean[] createStorage(int param1Int) {
/*  526 */       return new Boolean[param1Int];
/*      */     }
/*      */ 
/*      */     
/*      */     public int compare(Boolean param1Boolean1, Boolean param1Boolean2) {
/*  531 */       return param1Boolean1.compareTo(param1Boolean2);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getMemory(Boolean param1Boolean) {
/*  536 */       return 0;
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(WriteBuffer param1WriteBuffer, Boolean param1Boolean) {
/*  541 */       boolean bool = param1Boolean.booleanValue() ? true : true;
/*  542 */       param1WriteBuffer.put((byte)bool);
/*      */     }
/*      */ 
/*      */     
/*      */     public Boolean read(ByteBuffer param1ByteBuffer) {
/*  547 */       return (param1ByteBuffer.get() == 32) ? Boolean.TRUE : Boolean.FALSE;
/*      */     }
/*      */ 
/*      */     
/*      */     public Boolean read(ByteBuffer param1ByteBuffer, int param1Int) {
/*  552 */       return (param1Int == 1) ? Boolean.FALSE : Boolean.TRUE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class ByteType
/*      */     extends AutoDetectDataType<Byte>
/*      */   {
/*  564 */     static final ByteType INSTANCE = new ByteType();
/*      */     
/*      */     private ByteType() {
/*  567 */       super(2);
/*      */     }
/*      */ 
/*      */     
/*      */     public Byte[] createStorage(int param1Int) {
/*  572 */       return new Byte[param1Int];
/*      */     }
/*      */ 
/*      */     
/*      */     public int compare(Byte param1Byte1, Byte param1Byte2) {
/*  577 */       return param1Byte1.compareTo(param1Byte2);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getMemory(Byte param1Byte) {
/*  582 */       return 1;
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(WriteBuffer param1WriteBuffer, Byte param1Byte) {
/*  587 */       param1WriteBuffer.put((byte)2);
/*  588 */       param1WriteBuffer.put(param1Byte.byteValue());
/*      */     }
/*      */ 
/*      */     
/*      */     public Byte read(ByteBuffer param1ByteBuffer) {
/*  593 */       return Byte.valueOf(param1ByteBuffer.get());
/*      */     }
/*      */ 
/*      */     
/*      */     public Object read(ByteBuffer param1ByteBuffer, int param1Int) {
/*  598 */       return Byte.valueOf(param1ByteBuffer.get());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class CharacterType
/*      */     extends AutoDetectDataType<Character>
/*      */   {
/*  611 */     static final CharacterType INSTANCE = new CharacterType();
/*      */     
/*      */     private CharacterType() {
/*  614 */       super(10);
/*      */     }
/*      */ 
/*      */     
/*      */     public Character[] createStorage(int param1Int) {
/*  619 */       return new Character[param1Int];
/*      */     }
/*      */ 
/*      */     
/*      */     public int compare(Character param1Character1, Character param1Character2) {
/*  624 */       return param1Character1.compareTo(param1Character2);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getMemory(Character param1Character) {
/*  629 */       return 24;
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(WriteBuffer param1WriteBuffer, Character param1Character) {
/*  634 */       param1WriteBuffer.put((byte)10);
/*  635 */       param1WriteBuffer.putChar(param1Character.charValue());
/*      */     }
/*      */ 
/*      */     
/*      */     public Character read(ByteBuffer param1ByteBuffer) {
/*  640 */       return Character.valueOf(param1ByteBuffer.getChar());
/*      */     }
/*      */ 
/*      */     
/*      */     public Character read(ByteBuffer param1ByteBuffer, int param1Int) {
/*  645 */       return Character.valueOf(param1ByteBuffer.getChar());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class ShortType
/*      */     extends AutoDetectDataType<Short>
/*      */   {
/*  657 */     static final ShortType INSTANCE = new ShortType();
/*      */     
/*      */     private ShortType() {
/*  660 */       super(3);
/*      */     }
/*      */ 
/*      */     
/*      */     public Short[] createStorage(int param1Int) {
/*  665 */       return new Short[param1Int];
/*      */     }
/*      */ 
/*      */     
/*      */     public int compare(Short param1Short1, Short param1Short2) {
/*  670 */       return param1Short1.compareTo(param1Short2);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getMemory(Short param1Short) {
/*  675 */       return 24;
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(WriteBuffer param1WriteBuffer, Short param1Short) {
/*  680 */       param1WriteBuffer.put((byte)3);
/*  681 */       param1WriteBuffer.putShort(param1Short.shortValue());
/*      */     }
/*      */ 
/*      */     
/*      */     public Short read(ByteBuffer param1ByteBuffer) {
/*  686 */       return read(param1ByteBuffer, param1ByteBuffer.get());
/*      */     }
/*      */ 
/*      */     
/*      */     public Short read(ByteBuffer param1ByteBuffer, int param1Int) {
/*  691 */       return Short.valueOf(param1ByteBuffer.getShort());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class IntegerType
/*      */     extends AutoDetectDataType<Integer>
/*      */   {
/*  703 */     static final IntegerType INSTANCE = new IntegerType();
/*      */     
/*      */     private IntegerType() {
/*  706 */       super(4);
/*      */     }
/*      */ 
/*      */     
/*      */     public Integer[] createStorage(int param1Int) {
/*  711 */       return new Integer[param1Int];
/*      */     }
/*      */ 
/*      */     
/*      */     public int compare(Integer param1Integer1, Integer param1Integer2) {
/*  716 */       return param1Integer1.compareTo(param1Integer2);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getMemory(Integer param1Integer) {
/*  721 */       return 24;
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(WriteBuffer param1WriteBuffer, Integer param1Integer) {
/*  726 */       int i = param1Integer.intValue();
/*  727 */       if (i < 0) {
/*      */         
/*  729 */         if (-i < 0 || -i > 2097151) {
/*  730 */           param1WriteBuffer.put((byte)34).putInt(i);
/*      */         } else {
/*  732 */           param1WriteBuffer.put((byte)33).putVarInt(-i);
/*      */         } 
/*  734 */       } else if (i <= 15) {
/*  735 */         param1WriteBuffer.put((byte)(64 + i));
/*  736 */       } else if (i <= 2097151) {
/*  737 */         param1WriteBuffer.put((byte)4).putVarInt(i);
/*      */       } else {
/*  739 */         param1WriteBuffer.put((byte)34).putInt(i);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public Integer read(ByteBuffer param1ByteBuffer) {
/*  745 */       return read(param1ByteBuffer, param1ByteBuffer.get());
/*      */     }
/*      */ 
/*      */     
/*      */     public Integer read(ByteBuffer param1ByteBuffer, int param1Int) {
/*  750 */       switch (param1Int) {
/*      */         case 4:
/*  752 */           return Integer.valueOf(DataUtils.readVarInt(param1ByteBuffer));
/*      */         case 33:
/*  754 */           return Integer.valueOf(-DataUtils.readVarInt(param1ByteBuffer));
/*      */         case 34:
/*  756 */           return Integer.valueOf(param1ByteBuffer.getInt());
/*      */       } 
/*  758 */       return Integer.valueOf(param1Int - 64);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class LongType
/*      */     extends AutoDetectDataType<Long>
/*      */   {
/*  770 */     static final LongType INSTANCE = new LongType();
/*      */     
/*      */     private LongType() {
/*  773 */       super(5);
/*      */     }
/*      */ 
/*      */     
/*      */     public Long[] createStorage(int param1Int) {
/*  778 */       return new Long[param1Int];
/*      */     }
/*      */ 
/*      */     
/*      */     public int compare(Long param1Long1, Long param1Long2) {
/*  783 */       return param1Long1.compareTo(param1Long2);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getMemory(Long param1Long) {
/*  788 */       return 30;
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(WriteBuffer param1WriteBuffer, Long param1Long) {
/*  793 */       long l = param1Long.longValue();
/*  794 */       if (l < 0L) {
/*      */         
/*  796 */         if (-l < 0L || -l > 562949953421311L) {
/*  797 */           param1WriteBuffer.put((byte)36);
/*  798 */           param1WriteBuffer.putLong(l);
/*      */         } else {
/*  800 */           param1WriteBuffer.put((byte)35);
/*  801 */           param1WriteBuffer.putVarLong(-l);
/*      */         } 
/*  803 */       } else if (l <= 7L) {
/*  804 */         param1WriteBuffer.put((byte)(int)(80L + l));
/*  805 */       } else if (l <= 562949953421311L) {
/*  806 */         param1WriteBuffer.put((byte)5);
/*  807 */         param1WriteBuffer.putVarLong(l);
/*      */       } else {
/*  809 */         param1WriteBuffer.put((byte)36);
/*  810 */         param1WriteBuffer.putLong(l);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public Long read(ByteBuffer param1ByteBuffer) {
/*  816 */       return read(param1ByteBuffer, param1ByteBuffer.get());
/*      */     }
/*      */ 
/*      */     
/*      */     public Long read(ByteBuffer param1ByteBuffer, int param1Int) {
/*  821 */       switch (param1Int) {
/*      */         case 5:
/*  823 */           return Long.valueOf(DataUtils.readVarLong(param1ByteBuffer));
/*      */         case 35:
/*  825 */           return Long.valueOf(-DataUtils.readVarLong(param1ByteBuffer));
/*      */         case 36:
/*  827 */           return Long.valueOf(param1ByteBuffer.getLong());
/*      */       } 
/*  829 */       return Long.valueOf((param1Int - 80));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class FloatType
/*      */     extends AutoDetectDataType<Float>
/*      */   {
/*  841 */     static final FloatType INSTANCE = new FloatType();
/*      */     
/*      */     private FloatType() {
/*  844 */       super(7);
/*      */     }
/*      */ 
/*      */     
/*      */     public Float[] createStorage(int param1Int) {
/*  849 */       return new Float[param1Int];
/*      */     }
/*      */ 
/*      */     
/*      */     public int compare(Float param1Float1, Float param1Float2) {
/*  854 */       return param1Float1.compareTo(param1Float2);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getMemory(Float param1Float) {
/*  859 */       return 24;
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(WriteBuffer param1WriteBuffer, Float param1Float) {
/*  864 */       float f = param1Float.floatValue();
/*  865 */       int i = Float.floatToIntBits(f);
/*  866 */       if (i == ObjectDataType.FLOAT_ZERO_BITS) {
/*  867 */         param1WriteBuffer.put((byte)40);
/*  868 */       } else if (i == ObjectDataType.FLOAT_ONE_BITS) {
/*  869 */         param1WriteBuffer.put((byte)41);
/*      */       } else {
/*  871 */         int j = Integer.reverse(i);
/*  872 */         if (j >= 0 && j <= 2097151) {
/*  873 */           param1WriteBuffer.put((byte)7).putVarInt(j);
/*      */         } else {
/*  875 */           param1WriteBuffer.put((byte)42).putFloat(f);
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public Float read(ByteBuffer param1ByteBuffer) {
/*  882 */       return read(param1ByteBuffer, param1ByteBuffer.get());
/*      */     }
/*      */ 
/*      */     
/*      */     public Float read(ByteBuffer param1ByteBuffer, int param1Int) {
/*  887 */       switch (param1Int) {
/*      */         case 40:
/*  889 */           return Float.valueOf(0.0F);
/*      */         case 41:
/*  891 */           return Float.valueOf(1.0F);
/*      */         case 42:
/*  893 */           return Float.valueOf(param1ByteBuffer.getFloat());
/*      */       } 
/*  895 */       return Float.valueOf(Float.intBitsToFloat(Integer.reverse(
/*  896 */               DataUtils.readVarInt(param1ByteBuffer))));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class DoubleType
/*      */     extends AutoDetectDataType<Double>
/*      */   {
/*  909 */     static final DoubleType INSTANCE = new DoubleType();
/*      */     
/*      */     private DoubleType() {
/*  912 */       super(8);
/*      */     }
/*      */ 
/*      */     
/*      */     public Double[] createStorage(int param1Int) {
/*  917 */       return new Double[param1Int];
/*      */     }
/*      */ 
/*      */     
/*      */     public int compare(Double param1Double1, Double param1Double2) {
/*  922 */       return param1Double1.compareTo(param1Double2);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getMemory(Double param1Double) {
/*  927 */       return 30;
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(WriteBuffer param1WriteBuffer, Double param1Double) {
/*  932 */       double d = param1Double.doubleValue();
/*  933 */       long l = Double.doubleToLongBits(d);
/*  934 */       if (l == ObjectDataType.DOUBLE_ZERO_BITS) {
/*  935 */         param1WriteBuffer.put((byte)43);
/*  936 */       } else if (l == ObjectDataType.DOUBLE_ONE_BITS) {
/*  937 */         param1WriteBuffer.put((byte)44);
/*      */       } else {
/*  939 */         long l1 = Long.reverse(l);
/*  940 */         if (l1 >= 0L && l1 <= 562949953421311L) {
/*  941 */           param1WriteBuffer.put((byte)8);
/*  942 */           param1WriteBuffer.putVarLong(l1);
/*      */         } else {
/*  944 */           param1WriteBuffer.put((byte)45);
/*  945 */           param1WriteBuffer.putDouble(d);
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public Double read(ByteBuffer param1ByteBuffer) {
/*  952 */       return read(param1ByteBuffer, param1ByteBuffer.get());
/*      */     }
/*      */ 
/*      */     
/*      */     public Double read(ByteBuffer param1ByteBuffer, int param1Int) {
/*  957 */       switch (param1Int) {
/*      */         case 43:
/*  959 */           return Double.valueOf(0.0D);
/*      */         case 44:
/*  961 */           return Double.valueOf(1.0D);
/*      */         case 45:
/*  963 */           return Double.valueOf(param1ByteBuffer.getDouble());
/*      */       } 
/*  965 */       return Double.valueOf(Double.longBitsToDouble(Long.reverse(
/*  966 */               DataUtils.readVarLong(param1ByteBuffer))));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class BigIntegerType
/*      */     extends AutoDetectDataType<BigInteger>
/*      */   {
/*  978 */     static final BigIntegerType INSTANCE = new BigIntegerType();
/*      */     
/*      */     private BigIntegerType() {
/*  981 */       super(6);
/*      */     }
/*      */ 
/*      */     
/*      */     public BigInteger[] createStorage(int param1Int) {
/*  986 */       return new BigInteger[param1Int];
/*      */     }
/*      */ 
/*      */     
/*      */     public int compare(BigInteger param1BigInteger1, BigInteger param1BigInteger2) {
/*  991 */       return param1BigInteger1.compareTo(param1BigInteger2);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getMemory(BigInteger param1BigInteger) {
/*  996 */       return 100;
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(WriteBuffer param1WriteBuffer, BigInteger param1BigInteger) {
/* 1001 */       if (BigInteger.ZERO.equals(param1BigInteger)) {
/* 1002 */         param1WriteBuffer.put((byte)37);
/* 1003 */       } else if (BigInteger.ONE.equals(param1BigInteger)) {
/* 1004 */         param1WriteBuffer.put((byte)38);
/*      */       } else {
/* 1006 */         int i = param1BigInteger.bitLength();
/* 1007 */         if (i <= 63) {
/* 1008 */           param1WriteBuffer.put((byte)39).putVarLong(param1BigInteger
/* 1009 */               .longValue());
/*      */         } else {
/* 1011 */           byte[] arrayOfByte = param1BigInteger.toByteArray();
/* 1012 */           param1WriteBuffer.put((byte)6).putVarInt(arrayOfByte.length)
/* 1013 */             .put(arrayOfByte);
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public BigInteger read(ByteBuffer param1ByteBuffer) {
/* 1020 */       return read(param1ByteBuffer, param1ByteBuffer.get());
/*      */     }
/*      */ 
/*      */     
/*      */     public BigInteger read(ByteBuffer param1ByteBuffer, int param1Int) {
/* 1025 */       switch (param1Int) {
/*      */         case 37:
/* 1027 */           return BigInteger.ZERO;
/*      */         case 38:
/* 1029 */           return BigInteger.ONE;
/*      */         case 39:
/* 1031 */           return BigInteger.valueOf(DataUtils.readVarLong(param1ByteBuffer));
/*      */       } 
/* 1033 */       int i = DataUtils.readVarInt(param1ByteBuffer);
/* 1034 */       byte[] arrayOfByte = Utils.newBytes(i);
/* 1035 */       param1ByteBuffer.get(arrayOfByte);
/* 1036 */       return new BigInteger(arrayOfByte);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class BigDecimalType
/*      */     extends AutoDetectDataType<BigDecimal>
/*      */   {
/* 1048 */     static final BigDecimalType INSTANCE = new BigDecimalType();
/*      */     
/*      */     private BigDecimalType() {
/* 1051 */       super(9);
/*      */     }
/*      */ 
/*      */     
/*      */     public BigDecimal[] createStorage(int param1Int) {
/* 1056 */       return new BigDecimal[param1Int];
/*      */     }
/*      */ 
/*      */     
/*      */     public int compare(BigDecimal param1BigDecimal1, BigDecimal param1BigDecimal2) {
/* 1061 */       return param1BigDecimal1.compareTo(param1BigDecimal2);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getMemory(BigDecimal param1BigDecimal) {
/* 1066 */       return 150;
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(WriteBuffer param1WriteBuffer, BigDecimal param1BigDecimal) {
/* 1071 */       if (BigDecimal.ZERO.equals(param1BigDecimal)) {
/* 1072 */         param1WriteBuffer.put((byte)46);
/* 1073 */       } else if (BigDecimal.ONE.equals(param1BigDecimal)) {
/* 1074 */         param1WriteBuffer.put((byte)47);
/*      */       } else {
/* 1076 */         int i = param1BigDecimal.scale();
/* 1077 */         BigInteger bigInteger = param1BigDecimal.unscaledValue();
/* 1078 */         int j = bigInteger.bitLength();
/* 1079 */         if (j < 64) {
/* 1080 */           if (i == 0) {
/* 1081 */             param1WriteBuffer.put((byte)48);
/*      */           } else {
/* 1083 */             param1WriteBuffer.put((byte)49)
/* 1084 */               .putVarInt(i);
/*      */           } 
/* 1086 */           param1WriteBuffer.putVarLong(bigInteger.longValue());
/*      */         } else {
/* 1088 */           byte[] arrayOfByte = bigInteger.toByteArray();
/* 1089 */           param1WriteBuffer.put((byte)9).putVarInt(i)
/* 1090 */             .putVarInt(arrayOfByte.length).put(arrayOfByte);
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public BigDecimal read(ByteBuffer param1ByteBuffer) {
/* 1097 */       return read(param1ByteBuffer, param1ByteBuffer.get());
/*      */     }
/*      */ 
/*      */     
/*      */     public BigDecimal read(ByteBuffer param1ByteBuffer, int param1Int) {
/* 1102 */       switch (param1Int) {
/*      */         case 46:
/* 1104 */           return BigDecimal.ZERO;
/*      */         case 47:
/* 1106 */           return BigDecimal.ONE;
/*      */         case 48:
/* 1108 */           return BigDecimal.valueOf(DataUtils.readVarLong(param1ByteBuffer));
/*      */         case 49:
/* 1110 */           i = DataUtils.readVarInt(param1ByteBuffer);
/* 1111 */           return BigDecimal.valueOf(DataUtils.readVarLong(param1ByteBuffer), i);
/*      */       } 
/* 1113 */       int i = DataUtils.readVarInt(param1ByteBuffer);
/* 1114 */       int j = DataUtils.readVarInt(param1ByteBuffer);
/* 1115 */       byte[] arrayOfByte = Utils.newBytes(j);
/* 1116 */       param1ByteBuffer.get(arrayOfByte);
/* 1117 */       BigInteger bigInteger = new BigInteger(arrayOfByte);
/* 1118 */       return new BigDecimal(bigInteger, i);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class StringType
/*      */     extends AutoDetectDataType<String>
/*      */   {
/* 1131 */     static final StringType INSTANCE = new StringType();
/*      */     
/*      */     private StringType() {
/* 1134 */       super(11);
/*      */     }
/*      */ 
/*      */     
/*      */     public String[] createStorage(int param1Int) {
/* 1139 */       return new String[param1Int];
/*      */     }
/*      */ 
/*      */     
/*      */     public int getMemory(String param1String) {
/* 1144 */       return 24 + 2 * param1String.length();
/*      */     }
/*      */ 
/*      */     
/*      */     public int compare(String param1String1, String param1String2) {
/* 1149 */       return param1String1.compareTo(param1String2);
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(WriteBuffer param1WriteBuffer, String param1String) {
/* 1154 */       int i = param1String.length();
/* 1155 */       if (i <= 15) {
/* 1156 */         param1WriteBuffer.put((byte)(88 + i));
/*      */       } else {
/* 1158 */         param1WriteBuffer.put((byte)11).putVarInt(i);
/*      */       } 
/* 1160 */       param1WriteBuffer.putStringData(param1String, i);
/*      */     }
/*      */ 
/*      */     
/*      */     public String read(ByteBuffer param1ByteBuffer) {
/* 1165 */       return read(param1ByteBuffer, param1ByteBuffer.get());
/*      */     }
/*      */ 
/*      */     
/*      */     public String read(ByteBuffer param1ByteBuffer, int param1Int) {
/*      */       int i;
/* 1171 */       if (param1Int == 11) {
/* 1172 */         i = DataUtils.readVarInt(param1ByteBuffer);
/*      */       } else {
/* 1174 */         i = param1Int - 88;
/*      */       } 
/* 1176 */       return DataUtils.readString(param1ByteBuffer, i);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class UUIDType
/*      */     extends AutoDetectDataType<UUID>
/*      */   {
/* 1189 */     static final UUIDType INSTANCE = new UUIDType();
/*      */     
/*      */     private UUIDType() {
/* 1192 */       super(12);
/*      */     }
/*      */ 
/*      */     
/*      */     public UUID[] createStorage(int param1Int) {
/* 1197 */       return new UUID[param1Int];
/*      */     }
/*      */ 
/*      */     
/*      */     public int getMemory(UUID param1UUID) {
/* 1202 */       return 40;
/*      */     }
/*      */ 
/*      */     
/*      */     public int compare(UUID param1UUID1, UUID param1UUID2) {
/* 1207 */       return param1UUID1.compareTo(param1UUID2);
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(WriteBuffer param1WriteBuffer, UUID param1UUID) {
/* 1212 */       param1WriteBuffer.put((byte)12);
/* 1213 */       param1WriteBuffer.putLong(param1UUID.getMostSignificantBits());
/* 1214 */       param1WriteBuffer.putLong(param1UUID.getLeastSignificantBits());
/*      */     }
/*      */ 
/*      */     
/*      */     public UUID read(ByteBuffer param1ByteBuffer) {
/* 1219 */       return read(param1ByteBuffer, param1ByteBuffer.get());
/*      */     }
/*      */ 
/*      */     
/*      */     public UUID read(ByteBuffer param1ByteBuffer, int param1Int) {
/* 1224 */       long l1 = param1ByteBuffer.getLong(), l2 = param1ByteBuffer.getLong();
/* 1225 */       return new UUID(l1, l2);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class DateType
/*      */     extends AutoDetectDataType<Date>
/*      */   {
/* 1238 */     static final DateType INSTANCE = new DateType();
/*      */     
/*      */     private DateType() {
/* 1241 */       super(13);
/*      */     }
/*      */ 
/*      */     
/*      */     public Date[] createStorage(int param1Int) {
/* 1246 */       return new Date[param1Int];
/*      */     }
/*      */ 
/*      */     
/*      */     public int getMemory(Date param1Date) {
/* 1251 */       return 40;
/*      */     }
/*      */ 
/*      */     
/*      */     public int compare(Date param1Date1, Date param1Date2) {
/* 1256 */       return param1Date1.compareTo(param1Date2);
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(WriteBuffer param1WriteBuffer, Date param1Date) {
/* 1261 */       param1WriteBuffer.put((byte)13);
/* 1262 */       param1WriteBuffer.putLong(param1Date.getTime());
/*      */     }
/*      */ 
/*      */     
/*      */     public Date read(ByteBuffer param1ByteBuffer) {
/* 1267 */       return read(param1ByteBuffer, param1ByteBuffer.get());
/*      */     }
/*      */ 
/*      */     
/*      */     public Date read(ByteBuffer param1ByteBuffer, int param1Int) {
/* 1272 */       long l = param1ByteBuffer.getLong();
/* 1273 */       return new Date(l);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static class ObjectArrayType
/*      */     extends AutoDetectDataType<Object>
/*      */   {
/* 1282 */     private final ObjectDataType elementType = new ObjectDataType();
/*      */     
/*      */     ObjectArrayType() {
/* 1285 */       super(14);
/*      */     }
/*      */ 
/*      */     
/*      */     public Object[] createStorage(int param1Int) {
/* 1290 */       return new Object[param1Int];
/*      */     }
/*      */ 
/*      */     
/*      */     public int getMemory(Object param1Object) {
/* 1295 */       if (!ObjectDataType.isArray(param1Object)) {
/* 1296 */         return super.getMemory(param1Object);
/*      */       }
/* 1298 */       int i = 64;
/* 1299 */       Class<?> clazz = param1Object.getClass().getComponentType();
/* 1300 */       if (clazz.isPrimitive()) {
/* 1301 */         int j = Array.getLength(param1Object);
/* 1302 */         if (clazz == boolean.class || clazz == byte.class) {
/* 1303 */           i += j;
/* 1304 */         } else if (clazz == char.class || clazz == short.class) {
/* 1305 */           i += j * 2;
/* 1306 */         } else if (clazz == int.class || clazz == float.class) {
/* 1307 */           i += j * 4;
/* 1308 */         } else if (clazz == double.class || clazz == long.class) {
/* 1309 */           i += j * 8;
/*      */         } 
/*      */       } else {
/* 1312 */         for (Object object : (Object[])param1Object) {
/* 1313 */           if (object != null) {
/* 1314 */             i += this.elementType.getMemory(object);
/*      */           }
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 1320 */       return i * 2;
/*      */     }
/*      */ 
/*      */     
/*      */     public int compare(Object param1Object1, Object param1Object2) {
/* 1325 */       if (!ObjectDataType.isArray(param1Object1) || !ObjectDataType.isArray(param1Object2)) {
/* 1326 */         return super.compare(param1Object1, param1Object2);
/*      */       }
/* 1328 */       if (param1Object1 == param1Object2) {
/* 1329 */         return 0;
/*      */       }
/* 1331 */       Class<?> clazz1 = param1Object1.getClass().getComponentType();
/* 1332 */       Class<?> clazz2 = param1Object2.getClass().getComponentType();
/* 1333 */       if (clazz1 != clazz2) {
/* 1334 */         Integer integer1 = ObjectDataType.Holder.getCommonClassId(clazz1);
/* 1335 */         Integer integer2 = ObjectDataType.Holder.getCommonClassId(clazz2);
/* 1336 */         if (integer1 != null) {
/* 1337 */           if (integer2 != null) {
/* 1338 */             return integer1.compareTo(integer2);
/*      */           }
/* 1340 */           return -1;
/* 1341 */         }  if (integer2 != null) {
/* 1342 */           return 1;
/*      */         }
/* 1344 */         return clazz1.getName().compareTo(clazz2.getName());
/*      */       } 
/* 1346 */       int i = Array.getLength(param1Object1);
/* 1347 */       int j = Array.getLength(param1Object2);
/* 1348 */       int k = Math.min(i, j);
/* 1349 */       if (clazz1.isPrimitive()) {
/* 1350 */         if (clazz1 == byte.class) {
/* 1351 */           byte[] arrayOfByte1 = (byte[])param1Object1;
/* 1352 */           byte[] arrayOfByte2 = (byte[])param1Object2;
/* 1353 */           return ObjectDataType.compareNotNull(arrayOfByte1, arrayOfByte2);
/*      */         } 
/* 1355 */         for (byte b = 0; b < k; b++) {
/*      */           int m;
/* 1357 */           if (clazz1 == boolean.class) {
/* 1358 */             m = Integer.signum((((boolean[])param1Object1)[b] ? 1 : 0) - (((boolean[])param1Object2)[b] ? 1 : 0));
/*      */           }
/* 1360 */           else if (clazz1 == char.class) {
/* 1361 */             m = Integer.signum(((char[])param1Object1)[b] - ((char[])param1Object2)[b]);
/*      */           }
/* 1363 */           else if (clazz1 == short.class) {
/* 1364 */             m = Integer.signum(((short[])param1Object1)[b] - ((short[])param1Object2)[b]);
/*      */           }
/* 1366 */           else if (clazz1 == int.class) {
/* 1367 */             int n = ((int[])param1Object1)[b];
/* 1368 */             int i1 = ((int[])param1Object2)[b];
/* 1369 */             m = Integer.compare(n, i1);
/* 1370 */           } else if (clazz1 == float.class) {
/* 1371 */             m = Float.compare(((float[])param1Object1)[b], ((float[])param1Object2)[b]);
/*      */           }
/* 1373 */           else if (clazz1 == double.class) {
/* 1374 */             m = Double.compare(((double[])param1Object1)[b], ((double[])param1Object2)[b]);
/*      */           } else {
/*      */             
/* 1377 */             long l1 = ((long[])param1Object1)[b];
/* 1378 */             long l2 = ((long[])param1Object2)[b];
/* 1379 */             m = Long.compare(l1, l2);
/*      */           } 
/* 1381 */           if (m != 0) {
/* 1382 */             return m;
/*      */           }
/*      */         } 
/*      */       } else {
/* 1386 */         Object[] arrayOfObject1 = (Object[])param1Object1;
/* 1387 */         Object[] arrayOfObject2 = (Object[])param1Object2;
/* 1388 */         for (byte b = 0; b < k; b++) {
/* 1389 */           int m = this.elementType.compare(arrayOfObject1[b], arrayOfObject2[b]);
/* 1390 */           if (m != 0) {
/* 1391 */             return m;
/*      */           }
/*      */         } 
/*      */       } 
/* 1395 */       return Integer.compare(i, j);
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(WriteBuffer param1WriteBuffer, Object param1Object) {
/* 1400 */       if (!ObjectDataType.isArray(param1Object)) {
/* 1401 */         super.write(param1WriteBuffer, param1Object);
/*      */         return;
/*      */       } 
/* 1404 */       Class<?> clazz = param1Object.getClass().getComponentType();
/* 1405 */       Integer integer = ObjectDataType.Holder.getCommonClassId(clazz);
/* 1406 */       if (integer != null) {
/* 1407 */         if (clazz.isPrimitive()) {
/* 1408 */           if (clazz == byte.class) {
/* 1409 */             byte[] arrayOfByte = (byte[])param1Object;
/* 1410 */             int k = arrayOfByte.length;
/* 1411 */             if (k <= 15) {
/* 1412 */               param1WriteBuffer.put((byte)(104 + k));
/*      */             } else {
/* 1414 */               param1WriteBuffer.put((byte)14)
/* 1415 */                 .put((byte)integer.intValue())
/* 1416 */                 .putVarInt(k);
/*      */             } 
/* 1418 */             param1WriteBuffer.put(arrayOfByte);
/*      */             return;
/*      */           } 
/* 1421 */           int j = Array.getLength(param1Object);
/* 1422 */           param1WriteBuffer.put((byte)14).put((byte)integer.intValue())
/* 1423 */             .putVarInt(j);
/* 1424 */           for (byte b = 0; b < j; b++) {
/* 1425 */             if (clazz == boolean.class) {
/* 1426 */               param1WriteBuffer.put((byte)(((boolean[])param1Object)[b] ? 1 : 0));
/* 1427 */             } else if (clazz == char.class) {
/* 1428 */               param1WriteBuffer.putChar(((char[])param1Object)[b]);
/* 1429 */             } else if (clazz == short.class) {
/* 1430 */               param1WriteBuffer.putShort(((short[])param1Object)[b]);
/* 1431 */             } else if (clazz == int.class) {
/* 1432 */               param1WriteBuffer.putInt(((int[])param1Object)[b]);
/* 1433 */             } else if (clazz == float.class) {
/* 1434 */               param1WriteBuffer.putFloat(((float[])param1Object)[b]);
/* 1435 */             } else if (clazz == double.class) {
/* 1436 */               param1WriteBuffer.putDouble(((double[])param1Object)[b]);
/*      */             } else {
/* 1438 */               param1WriteBuffer.putLong(((long[])param1Object)[b]);
/*      */             } 
/*      */           } 
/*      */           return;
/*      */         } 
/* 1443 */         param1WriteBuffer.put((byte)14).put((byte)integer.intValue());
/*      */       } else {
/* 1445 */         param1WriteBuffer.put((byte)14).put((byte)-1);
/* 1446 */         String str = clazz.getName();
/* 1447 */         StringDataType.INSTANCE.write(param1WriteBuffer, str);
/*      */       } 
/* 1449 */       Object[] arrayOfObject = (Object[])param1Object;
/* 1450 */       int i = arrayOfObject.length;
/* 1451 */       param1WriteBuffer.putVarInt(i);
/* 1452 */       for (Object object : arrayOfObject) {
/* 1453 */         this.elementType.write(param1WriteBuffer, object);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public Object read(ByteBuffer param1ByteBuffer) {
/* 1459 */       return read(param1ByteBuffer, param1ByteBuffer.get());
/*      */     }
/*      */     public Object read(ByteBuffer param1ByteBuffer, int param1Int) {
/*      */       Class<?> clazz;
/*      */       Object object;
/* 1464 */       if (param1Int != 14) {
/*      */         
/* 1466 */         int j = param1Int - 104;
/* 1467 */         byte[] arrayOfByte = Utils.newBytes(j);
/* 1468 */         param1ByteBuffer.get(arrayOfByte);
/* 1469 */         return arrayOfByte;
/*      */       } 
/* 1471 */       byte b = param1ByteBuffer.get();
/*      */ 
/*      */       
/* 1474 */       if (b == -1) {
/* 1475 */         String str = StringDataType.INSTANCE.read(param1ByteBuffer);
/*      */         try {
/* 1477 */           clazz = Class.forName(str);
/* 1478 */         } catch (Exception exception) {
/* 1479 */           throw DataUtils.newMVStoreException(8, "Could not get class {0}", new Object[] { str, exception });
/*      */         }
/*      */       
/*      */       } else {
/*      */         
/* 1484 */         clazz = ObjectDataType.COMMON_CLASSES[b];
/*      */       } 
/* 1486 */       int i = DataUtils.readVarInt(param1ByteBuffer);
/*      */       try {
/* 1488 */         object = Array.newInstance(clazz, i);
/* 1489 */       } catch (Exception exception) {
/* 1490 */         throw DataUtils.newMVStoreException(8, "Could not create array of type {0} length {1}", new Object[] { clazz, 
/*      */ 
/*      */               
/* 1493 */               Integer.valueOf(i), exception });
/*      */       } 
/* 1495 */       if (clazz.isPrimitive()) {
/* 1496 */         for (byte b1 = 0; b1 < i; b1++) {
/* 1497 */           if (clazz == boolean.class) {
/* 1498 */             ((boolean[])object)[b1] = (param1ByteBuffer.get() == 1);
/* 1499 */           } else if (clazz == byte.class) {
/* 1500 */             ((byte[])object)[b1] = param1ByteBuffer.get();
/* 1501 */           } else if (clazz == char.class) {
/* 1502 */             ((char[])object)[b1] = param1ByteBuffer.getChar();
/* 1503 */           } else if (clazz == short.class) {
/* 1504 */             ((short[])object)[b1] = param1ByteBuffer.getShort();
/* 1505 */           } else if (clazz == int.class) {
/* 1506 */             ((int[])object)[b1] = param1ByteBuffer.getInt();
/* 1507 */           } else if (clazz == float.class) {
/* 1508 */             ((float[])object)[b1] = param1ByteBuffer.getFloat();
/* 1509 */           } else if (clazz == double.class) {
/* 1510 */             ((double[])object)[b1] = param1ByteBuffer.getDouble();
/*      */           } else {
/* 1512 */             ((long[])object)[b1] = param1ByteBuffer.getLong();
/*      */           } 
/*      */         } 
/*      */       } else {
/* 1516 */         Object[] arrayOfObject = (Object[])object;
/* 1517 */         for (byte b1 = 0; b1 < i; b1++) {
/* 1518 */           arrayOfObject[b1] = this.elementType.read(param1ByteBuffer);
/*      */         }
/*      */       } 
/* 1521 */       return object;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class SerializedObjectType
/*      */     extends AutoDetectDataType<Object>
/*      */   {
/* 1531 */     private int averageSize = 10000;
/*      */     
/*      */     SerializedObjectType(ObjectDataType param1ObjectDataType) {
/* 1534 */       super(param1ObjectDataType, 19);
/*      */     }
/*      */ 
/*      */     
/*      */     public Object[] createStorage(int param1Int) {
/* 1539 */       return new Object[param1Int];
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public int compare(Object param1Object1, Object param1Object2) {
/* 1545 */       if (param1Object1 == param1Object2) {
/* 1546 */         return 0;
/*      */       }
/* 1548 */       DataType<Object> dataType = getType(param1Object1);
/* 1549 */       DataType dataType1 = getType(param1Object2);
/* 1550 */       if (dataType != this || dataType1 != this) {
/* 1551 */         if (dataType == dataType1) {
/* 1552 */           return dataType.compare(param1Object1, param1Object2);
/*      */         }
/* 1554 */         return super.compare(param1Object1, param1Object2);
/*      */       } 
/*      */ 
/*      */       
/* 1558 */       if (param1Object1 instanceof Comparable && 
/* 1559 */         param1Object1.getClass().isAssignableFrom(param1Object2.getClass())) {
/* 1560 */         return ((Comparable<Object>)param1Object1).compareTo(param1Object2);
/*      */       }
/*      */       
/* 1563 */       if (param1Object2 instanceof Comparable && 
/* 1564 */         param1Object2.getClass().isAssignableFrom(param1Object1.getClass())) {
/* 1565 */         return -((Comparable)param1Object2).compareTo((T)param1Object1);
/*      */       }
/*      */       
/* 1568 */       byte[] arrayOfByte1 = ObjectDataType.serialize(param1Object1);
/* 1569 */       byte[] arrayOfByte2 = ObjectDataType.serialize(param1Object2);
/* 1570 */       return ObjectDataType.compareNotNull(arrayOfByte1, arrayOfByte2);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getMemory(Object param1Object) {
/* 1575 */       DataType<Object> dataType = getType(param1Object);
/* 1576 */       if (dataType == this) {
/* 1577 */         return this.averageSize;
/*      */       }
/* 1579 */       return dataType.getMemory(param1Object);
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(WriteBuffer param1WriteBuffer, Object param1Object) {
/* 1584 */       DataType<Object> dataType = getType(param1Object);
/* 1585 */       if (dataType != this) {
/* 1586 */         dataType.write(param1WriteBuffer, param1Object);
/*      */         return;
/*      */       } 
/* 1589 */       byte[] arrayOfByte = ObjectDataType.serialize(param1Object);
/*      */ 
/*      */       
/* 1592 */       int i = arrayOfByte.length * 2;
/*      */ 
/*      */       
/* 1595 */       this.averageSize = (int)((i + 15L * this.averageSize) / 16L);
/* 1596 */       param1WriteBuffer.put((byte)19).putVarInt(arrayOfByte.length)
/* 1597 */         .put(arrayOfByte);
/*      */     }
/*      */ 
/*      */     
/*      */     public Object read(ByteBuffer param1ByteBuffer) {
/* 1602 */       return read(param1ByteBuffer, param1ByteBuffer.get());
/*      */     }
/*      */ 
/*      */     
/*      */     public Object read(ByteBuffer param1ByteBuffer, int param1Int) {
/* 1607 */       int i = DataUtils.readVarInt(param1ByteBuffer);
/* 1608 */       byte[] arrayOfByte = Utils.newBytes(i);
/* 1609 */       int j = arrayOfByte.length * 2;
/*      */ 
/*      */       
/* 1612 */       this.averageSize = (int)((j + 15L * this.averageSize) / 16L);
/* 1613 */       param1ByteBuffer.get(arrayOfByte);
/* 1614 */       return ObjectDataType.deserialize(arrayOfByte);
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\type\ObjectDataType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */