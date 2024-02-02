package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.COM.Dispatch;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;

class Convert {
   public static Variant.VARIANT toVariant(Object value) {
      if (value instanceof Variant.VARIANT) {
         return (Variant.VARIANT)value;
      } else if (value instanceof Byte) {
         return new Variant.VARIANT((Byte)value);
      } else if (value instanceof Character) {
         return new Variant.VARIANT((Character)value);
      } else if (value instanceof Short) {
         return new Variant.VARIANT((Short)value);
      } else if (value instanceof Integer) {
         return new Variant.VARIANT((Integer)value);
      } else if (value instanceof Long) {
         return new Variant.VARIANT((Long)value);
      } else if (value instanceof Float) {
         return new Variant.VARIANT((Float)value);
      } else if (value instanceof Double) {
         return new Variant.VARIANT((Double)value);
      } else if (value instanceof String) {
         return new Variant.VARIANT((String)value);
      } else if (value instanceof Boolean) {
         return new Variant.VARIANT((Boolean)value);
      } else if (value instanceof Dispatch) {
         return new Variant.VARIANT((Dispatch)value);
      } else if (value instanceof Date) {
         return new Variant.VARIANT((Date)value);
      } else if (value instanceof Proxy) {
         InvocationHandler ih = Proxy.getInvocationHandler(value);
         ProxyObject pobj = (ProxyObject)ih;
         return new Variant.VARIANT(pobj.getRawDispatch());
      } else if (value instanceof IComEnum) {
         IComEnum enm = (IComEnum)value;
         return new Variant.VARIANT(new WinDef.LONG(enm.getValue()));
      } else {
         Constructor<Variant.VARIANT> constructor = null;
         if (value != null) {
            Constructor[] var2 = (Constructor[])Variant.VARIANT.class.getConstructors();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Constructor<Variant.VARIANT> m = var2[var4];
               Class<?>[] parameters = m.getParameterTypes();
               if (parameters.length == 1 && parameters[0].isAssignableFrom(value.getClass())) {
                  constructor = m;
               }
            }
         }

         if (constructor != null) {
            try {
               return (Variant.VARIANT)constructor.newInstance(value);
            } catch (Exception var7) {
               throw new RuntimeException(var7);
            }
         } else {
            return null;
         }
      }
   }

   public static Object toJavaObject(Variant.VARIANT value, Class<?> targetClass, ObjectFactory factory, boolean addReference, boolean freeValue) {
      if (null != value && value.getVarType().intValue() != 0 && value.getVarType().intValue() != 1) {
         if (targetClass != null && !targetClass.isAssignableFrom(Object.class)) {
            if (targetClass.isAssignableFrom(value.getClass())) {
               return value;
            }

            Object vobj = value.getValue();
            if (vobj != null && targetClass.isAssignableFrom(vobj.getClass())) {
               return vobj;
            }
         }

         Variant.VARIANT inputValue = value;
         if (value.getVarType().intValue() == 16396) {
            value = (Variant.VARIANT)value.getValue();
         }

         if (targetClass == null || targetClass.isAssignableFrom(Object.class)) {
            targetClass = null;
            int varType = value.getVarType().intValue();
            switch (value.getVarType().intValue()) {
               case 2:
                  targetClass = Short.class;
                  break;
               case 3:
               case 19:
               case 22:
               case 23:
                  targetClass = Integer.class;
                  break;
               case 4:
                  targetClass = Float.class;
                  break;
               case 5:
                  targetClass = Double.class;
                  break;
               case 6:
                  targetClass = OaIdl.CURRENCY.class;
                  break;
               case 7:
                  targetClass = Date.class;
                  break;
               case 8:
                  targetClass = String.class;
                  break;
               case 9:
                  targetClass = IDispatch.class;
                  break;
               case 10:
                  targetClass = WinDef.SCODE.class;
                  break;
               case 11:
                  targetClass = Boolean.class;
                  break;
               case 13:
                  targetClass = com.sun.jna.platform.win32.COM.IUnknown.class;
                  break;
               case 16:
               case 17:
                  targetClass = Byte.class;
                  break;
               case 18:
                  targetClass = Character.class;
                  break;
               case 20:
               case 21:
                  targetClass = Long.class;
                  break;
               case 36:
               default:
                  if ((varType & 8192) > 0) {
                     targetClass = OaIdl.SAFEARRAY.class;
                  }
                  break;
               case 16384:
                  targetClass = WinDef.PVOID.class;
                  break;
               case 16396:
                  targetClass = Variant.class;
                  break;
               case 16398:
                  targetClass = OaIdl.DECIMAL.class;
            }
         }

         Object result;
         if (!Byte.class.equals(targetClass) && !Byte.TYPE.equals(targetClass)) {
            if (!Short.class.equals(targetClass) && !Short.TYPE.equals(targetClass)) {
               if (!Character.class.equals(targetClass) && !Character.TYPE.equals(targetClass)) {
                  if (!Integer.class.equals(targetClass) && !Integer.TYPE.equals(targetClass)) {
                     if (!Long.class.equals(targetClass) && !Long.TYPE.equals(targetClass) && !IComEnum.class.isAssignableFrom(targetClass)) {
                        if (!Float.class.equals(targetClass) && !Float.TYPE.equals(targetClass)) {
                           if (!Double.class.equals(targetClass) && !Double.TYPE.equals(targetClass)) {
                              if (!Boolean.class.equals(targetClass) && !Boolean.TYPE.equals(targetClass)) {
                                 if (Date.class.equals(targetClass)) {
                                    result = value.dateValue();
                                 } else if (String.class.equals(targetClass)) {
                                    result = value.stringValue();
                                 } else if (value.getValue() instanceof Dispatch) {
                                    Dispatch d = (Dispatch)value.getValue();
                                    if (targetClass != null && targetClass.isInterface()) {
                                       Object proxy = factory.createProxy(targetClass, d);
                                       if (!addReference) {
                                          int var9 = d.Release();
                                       }

                                       result = proxy;
                                    } else {
                                       result = d;
                                    }
                                 } else {
                                    result = value.getValue();
                                 }
                              } else {
                                 result = value.booleanValue();
                              }
                           } else {
                              result = value.doubleValue();
                           }
                        } else {
                           result = value.floatValue();
                        }
                     } else {
                        result = value.longValue();
                     }
                  } else {
                     result = value.intValue();
                  }
               } else {
                  result = (char)value.intValue();
               }
            } else {
               result = value.shortValue();
            }
         } else {
            result = value.byteValue();
         }

         if (IComEnum.class.isAssignableFrom(targetClass)) {
            result = targetClass.cast(toComEnum(targetClass, result));
         }

         if (freeValue) {
            free(inputValue, result);
         }

         return result;
      } else {
         return null;
      }
   }

   public static <T extends IComEnum> T toComEnum(Class<T> enumType, Object value) {
      try {
         Method m = enumType.getMethod("values");
         T[] values = (IComEnum[])((IComEnum[])m.invoke((Object)null));
         IComEnum[] var4 = values;
         int var5 = values.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            T t = var4[var6];
            if (value.equals(t.getValue())) {
               return t;
            }
         }
      } catch (NoSuchMethodException var8) {
      } catch (IllegalAccessException var9) {
      } catch (IllegalArgumentException var10) {
      } catch (InvocationTargetException var11) {
      }

      return null;
   }

   public static void free(Variant.VARIANT variant, Class<?> javaType) {
      if ((javaType == null || !WTypes.BSTR.class.isAssignableFrom(javaType)) && variant != null && variant.getVarType().intValue() == 8) {
         Object value = variant.getValue();
         if (value instanceof WTypes.BSTR) {
            OleAuto.INSTANCE.SysFreeString((WTypes.BSTR)value);
         }
      }

   }

   public static void free(Variant.VARIANT variant, Object value) {
      free(variant, value == null ? null : value.getClass());
   }
}
