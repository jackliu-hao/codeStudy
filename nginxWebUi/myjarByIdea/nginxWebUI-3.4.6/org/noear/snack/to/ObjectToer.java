package org.noear.snack.to;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;
import org.noear.snack.ONode;
import org.noear.snack.ONodeData;
import org.noear.snack.OValue;
import org.noear.snack.OValueType;
import org.noear.snack.core.Context;
import org.noear.snack.core.DEFAULTS;
import org.noear.snack.core.Feature;
import org.noear.snack.core.NodeDecoderEntity;
import org.noear.snack.core.exts.ClassWrap;
import org.noear.snack.core.exts.EnumWrap;
import org.noear.snack.core.exts.FieldWrap;
import org.noear.snack.core.utils.BeanUtil;
import org.noear.snack.core.utils.ParameterizedTypeImpl;
import org.noear.snack.core.utils.StringUtil;
import org.noear.snack.core.utils.TypeUtil;
import org.noear.snack.exception.SnackException;

public class ObjectToer implements Toer {
   public void handle(Context ctx) throws Exception {
      ONode o = (ONode)ctx.source;
      if (null != o) {
         ctx.target = this.analyse(ctx, o, ctx.target, ctx.target_clz, ctx.target_type, (Map)null);
      }

   }

   private Object analyse(Context ctx, ONode o, Object rst, Class<?> clz, Type type, Map<TypeVariable, Type> genericInfo) throws Exception {
      if (o == null) {
         return null;
      } else if (clz != null && ONode.class.isAssignableFrom(clz)) {
         return o;
      } else {
         if (o.isObject() || o.isArray()) {
            clz = this.getTypeByNode(ctx, o, clz);
         }

         if (clz != null) {
            Iterator var7 = ctx.options.decoders().iterator();

            while(var7.hasNext()) {
               NodeDecoderEntity decoder = (NodeDecoderEntity)var7.next();
               if (decoder.isDecodable(clz)) {
                  return decoder.decode(o, clz);
               }
            }
         }

         if (String.class == clz) {
            return o.getString();
         } else {
            switch (o.nodeType()) {
               case Value:
                  return this.analyseVal(ctx, o.nodeData(), clz);
               case Object:
                  o.remove(ctx.options.getTypePropertyName());
                  if (Properties.class.isAssignableFrom(clz)) {
                     return this.analyseProps(ctx, o, (Properties)rst, clz, type, genericInfo);
                  } else if (Map.class.isAssignableFrom(clz)) {
                     return this.analyseMap(ctx, o, clz, type, genericInfo);
                  } else {
                     if (StackTraceElement.class.isAssignableFrom(clz)) {
                        String declaringClass = o.get("declaringClass").getString();
                        if (declaringClass == null) {
                           declaringClass = o.get("className").getString();
                        }

                        return new StackTraceElement(declaringClass, o.get("methodName").getString(), o.get("fileName").getString(), o.get("lineNumber").getInt());
                     }

                     if (type instanceof ParameterizedType) {
                        genericInfo = TypeUtil.getGenericInfo(type);
                     }

                     return this.analyseBean(ctx, o, rst, clz, type, genericInfo);
                  }
               case Array:
                  if (clz.isArray()) {
                     return this.analyseArray(ctx, o.nodeData(), clz);
                  }

                  return this.analyseCollection(ctx, o, clz, type, genericInfo);
               default:
                  return null;
            }
         }
      }
   }

   private boolean is(Class<?> s, Class<?> t) {
      return s.isAssignableFrom(t);
   }

   public Object analyseVal(Context ctx, ONodeData d, Class<?> clz) throws Exception {
      OValue v = d.value;
      if (v.type() == OValueType.Null) {
         return null;
      } else if (clz == null) {
         return v.getRaw();
      } else if (!this.is(Byte.class, clz) && clz != Byte.TYPE) {
         if (!this.is(Short.class, clz) && clz != Short.TYPE) {
            if (!this.is(Integer.class, clz) && clz != Integer.TYPE) {
               if (!this.is(Long.class, clz) && clz != Long.TYPE) {
                  if (this.is(LongAdder.class, clz)) {
                     LongAdder tmp = new LongAdder();
                     tmp.add(v.getLong());
                     return tmp;
                  } else if (!this.is(Float.class, clz) && clz != Float.TYPE) {
                     if (!this.is(Double.class, clz) && clz != Double.TYPE) {
                        if (this.is(DoubleAdder.class, clz)) {
                           DoubleAdder tmp = new DoubleAdder();
                           tmp.add(v.getDouble());
                           return tmp;
                        } else if (!this.is(Boolean.class, clz) && clz != Boolean.TYPE) {
                           if (!this.is(Character.class, clz) && clz != Character.TYPE) {
                              if (this.is(String.class, clz)) {
                                 return v.getString();
                              } else if (this.is(Timestamp.class, clz)) {
                                 return new Timestamp(v.getLong());
                              } else if (this.is(Date.class, clz)) {
                                 return new Date(v.getLong());
                              } else if (this.is(Time.class, clz)) {
                                 return new Time(v.getLong());
                              } else if (this.is(java.util.Date.class, clz)) {
                                 return v.getDate();
                              } else if (this.is(LocalDateTime.class, clz)) {
                                 return v.getDate().toInstant().atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toLocalDateTime();
                              } else if (this.is(LocalDate.class, clz)) {
                                 return v.getDate().toInstant().atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toLocalDate();
                              } else if (this.is(LocalTime.class, clz)) {
                                 return v.getDate().toInstant().atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toLocalTime();
                              } else if (this.is(BigDecimal.class, clz)) {
                                 return v.type() == OValueType.Number && v.getRawNumber() instanceof BigDecimal ? v.getRawNumber() : new BigDecimal(v.getString());
                              } else if (this.is(BigInteger.class, clz)) {
                                 return v.type() == OValueType.Number && v.getRawNumber() instanceof BigInteger ? v.getRawNumber() : new BigInteger(v.getString());
                              } else if (clz.isEnum()) {
                                 return this.analyseEnum(ctx, d, clz);
                              } else if (this.is(Class.class, clz)) {
                                 return BeanUtil.loadClass(v.getString());
                              } else if (this.is(Object.class, clz)) {
                                 Object val = v.getRaw();
                                 if (val instanceof String && clz.isInterface()) {
                                    Class<?> valClz = BeanUtil.loadClass((String)val);
                                    return BeanUtil.newInstance(valClz);
                                 } else {
                                    return val;
                                 }
                              } else {
                                 throw new SnackException("unsupport type " + clz.getName());
                              }
                           } else {
                              return v.getChar();
                           }
                        } else {
                           return v.getBoolean();
                        }
                     } else {
                        return v.getDouble();
                     }
                  } else {
                     return v.getFloat();
                  }
               } else {
                  return v.getLong();
               }
            } else {
               return v.getInt();
            }
         } else {
            return v.getShort();
         }
      } else {
         return (byte)((int)v.getLong());
      }
   }

   public Object analyseEnum(Context ctx, ONodeData d, Class<?> target) {
      EnumWrap ew = TypeUtil.createEnum(target);
      return d.value.type() == OValueType.String ? ew.get(d.value.getString()) : ew.get(d.value.getInt());
   }

   public Object analyseArray(Context ctx, ONodeData d, Class<?> target) throws Exception {
      int len = d.array.size();
      int i;
      if (this.is(byte[].class, target)) {
         byte[] val = new byte[len];

         for(i = 0; i < len; ++i) {
            val[i] = (byte)((int)((ONode)d.array.get(i)).getLong());
         }

         return val;
      } else if (this.is(short[].class, target)) {
         short[] val = new short[len];

         for(i = 0; i < len; ++i) {
            val[i] = ((ONode)d.array.get(i)).getShort();
         }

         return val;
      } else if (this.is(int[].class, target)) {
         int[] val = new int[len];

         for(i = 0; i < len; ++i) {
            val[i] = ((ONode)d.array.get(i)).getInt();
         }

         return val;
      } else if (this.is(long[].class, target)) {
         long[] val = new long[len];

         for(i = 0; i < len; ++i) {
            val[i] = ((ONode)d.array.get(i)).getLong();
         }

         return val;
      } else if (this.is(float[].class, target)) {
         float[] val = new float[len];

         for(i = 0; i < len; ++i) {
            val[i] = ((ONode)d.array.get(i)).getFloat();
         }

         return val;
      } else if (this.is(double[].class, target)) {
         double[] val = new double[len];

         for(i = 0; i < len; ++i) {
            val[i] = ((ONode)d.array.get(i)).getDouble();
         }

         return val;
      } else if (this.is(boolean[].class, target)) {
         boolean[] val = new boolean[len];

         for(i = 0; i < len; ++i) {
            val[i] = ((ONode)d.array.get(i)).getBoolean();
         }

         return val;
      } else if (this.is(char[].class, target)) {
         char[] val = new char[len];

         for(i = 0; i < len; ++i) {
            val[i] = ((ONode)d.array.get(i)).getChar();
         }

         return val;
      } else if (this.is(String[].class, target)) {
         String[] val = new String[len];

         for(i = 0; i < len; ++i) {
            val[i] = ((ONode)d.array.get(i)).getString();
         }

         return val;
      } else if (!this.is(Object[].class, target)) {
         throw new SnackException("unsupport type " + target.getName());
      } else {
         Class<?> c = target.getComponentType();
         Object[] val = (Object[])((Object[])Array.newInstance(c, len));

         for(int i = 0; i < len; ++i) {
            val[i] = this.analyse(ctx, (ONode)d.array.get(i), (Object)null, c, c, (Map)null);
         }

         return val;
      }
   }

   public Object analyseCollection(Context ctx, ONode o, Class<?> clz, Type type, Map<TypeVariable, Type> genericInfo) throws Exception {
      Collection list = TypeUtil.createCollection(clz, false);
      if (list == null) {
         return null;
      } else {
         Type itemType = null;
         if (ctx.target_type != null) {
            itemType = TypeUtil.getCollectionItemType(type);
         }

         if (itemType != null && itemType instanceof TypeVariable) {
            itemType = null;
         }

         Iterator var8 = o.nodeData().array.iterator();

         while(var8.hasNext()) {
            ONode o1 = (ONode)var8.next();
            list.add(this.analyse(ctx, o1, (Object)null, (Class)itemType, itemType, genericInfo));
         }

         return list;
      }
   }

   public Object analyseProps(Context ctx, ONode o, Properties rst, Class<?> clz, Type type, Map<TypeVariable, Type> genericInfo) throws Exception {
      if (rst == null) {
         rst = new Properties();
      }

      String prefix = "";
      this.propsLoad0(rst, prefix, o);
      return rst;
   }

   private void propsLoad0(Properties props, String prefix, ONode tmp) {
      if (tmp.isObject()) {
         tmp.forEach((k, vx) -> {
            String prefix2 = prefix + "." + k;
            this.propsLoad0(props, prefix2, vx);
         });
      } else if (!tmp.isArray()) {
         if (tmp.isNull()) {
            this.propsPut0(props, prefix, "");
         } else {
            this.propsPut0(props, prefix, tmp.getString());
         }

      } else {
         int index = 0;

         for(Iterator var5 = tmp.ary().iterator(); var5.hasNext(); ++index) {
            ONode v = (ONode)var5.next();
            String prefix2 = prefix + "[" + index + "]";
            this.propsLoad0(props, prefix2, v);
         }

      }
   }

   private void propsPut0(Properties props, String key, Object val) {
      if (key.startsWith(".")) {
         props.put(key.substring(1), val);
      } else {
         props.put(key, val);
      }

   }

   public Object analyseMap(Context ctx, ONode o, Class<?> clz, Type type, Map<TypeVariable, Type> genericInfo) throws Exception {
      Map<Object, Object> map = TypeUtil.createMap(clz);
      if (type instanceof ParameterizedType) {
         ParameterizedType ptt = (ParameterizedType)type;
         Type kType = ptt.getActualTypeArguments()[0];
         Type vType = ptt.getActualTypeArguments()[1];
         if (kType instanceof ParameterizedType) {
            kType = ((ParameterizedType)kType).getRawType();
         }

         if (vType instanceof ParameterizedType) {
            vType = ((ParameterizedType)vType).getRawType();
         }

         Iterator var10;
         Map.Entry kv;
         if (kType == String.class) {
            var10 = o.nodeData().object.entrySet().iterator();

            while(var10.hasNext()) {
               kv = (Map.Entry)var10.next();
               map.put(kv.getKey(), this.analyse(ctx, (ONode)kv.getValue(), (Object)null, (Class)vType, vType, genericInfo));
            }
         } else {
            var10 = o.nodeData().object.entrySet().iterator();

            while(var10.hasNext()) {
               kv = (Map.Entry)var10.next();
               map.put(TypeUtil.strTo((String)kv.getKey(), (Class)kType), this.analyse(ctx, (ONode)kv.getValue(), (Object)null, (Class)vType, vType, genericInfo));
            }
         }
      } else {
         Iterator var12 = o.nodeData().object.entrySet().iterator();

         while(var12.hasNext()) {
            Map.Entry<String, ONode> kv = (Map.Entry)var12.next();
            map.put(kv.getKey(), this.analyse(ctx, (ONode)kv.getValue(), (Object)null, (Class)null, (Type)null, genericInfo));
         }
      }

      return map;
   }

   public Object analyseBean(Context ctx, ONode o, Object rst, Class<?> clz, Type type, Map<TypeVariable, Type> genericInfo) throws Exception {
      if (this.is(SimpleDateFormat.class, clz)) {
         return new SimpleDateFormat(o.get("val").getString());
      } else if (this.is(InetSocketAddress.class, clz)) {
         return new InetSocketAddress(o.get("address").getString(), o.get("port").getInt());
      } else {
         if (this.is(Throwable.class, clz)) {
            String message = o.get("message").getString();
            if (!StringUtil.isEmpty(message)) {
               try {
                  Constructor fun = clz.getConstructor(String.class);
                  rst = fun.newInstance(message);
               } catch (Throwable var16) {
               }
            }
         }

         ClassWrap clzWrap = ClassWrap.get(clz);
         if (clzWrap.recordable()) {
            Parameter[] argsP = clzWrap.recordParams();
            Object[] argsV = new Object[argsP.length];

            for(int j = 0; j < argsP.length; ++j) {
               Parameter f = argsP[j];
               String fieldK = f.getName();
               if (o.contains(fieldK)) {
                  Class fieldT = f.getType();
                  Type fieldGt = f.getParameterizedType();
                  Object val = this.analyseBeanOfValue(fieldK, fieldT, fieldGt, ctx, o, (Object)null, genericInfo);
                  argsV[j] = val;
               }
            }

            rst = clzWrap.recordConstructor().newInstance(argsV);
         } else {
            if (rst == null) {
               rst = BeanUtil.newInstance(clz);
            }

            if (rst == null) {
               return null;
            }

            boolean disSetter = !ctx.options.hasFeature(Feature.UseSetter);
            Iterator var20 = clzWrap.fieldAllWraps().iterator();

            while(var20.hasNext()) {
               FieldWrap f = (FieldWrap)var20.next();
               if (f.isDeserialize()) {
                  String fieldK = f.getName();
                  if (o.contains(fieldK)) {
                     Class fieldT = f.type;
                     Type fieldGt = f.genericType;
                     if (f.readonly) {
                        this.analyseBeanOfValue(fieldK, fieldT, fieldGt, ctx, o, f.getValue(rst), genericInfo);
                     } else {
                        Object val = this.analyseBeanOfValue(fieldK, fieldT, fieldGt, ctx, o, (Object)null, genericInfo);
                        f.setValue(rst, val, disSetter);
                     }
                  }
               }
            }
         }

         return rst;
      }
   }

   private Object analyseBeanOfValue(String fieldK, Class fieldT, Type fieldGt, Context ctx, ONode o, Object rst, Map<TypeVariable, Type> genericInfo) throws Exception {
      if (genericInfo != null) {
         if (fieldGt instanceof TypeVariable) {
            Type tmp = (Type)genericInfo.get(fieldGt);
            if (tmp != null) {
               fieldGt = tmp;
               if (tmp instanceof Class) {
                  fieldT = (Class)tmp;
               }
            }
         }

         if (fieldGt instanceof ParameterizedType) {
            ParameterizedType fieldGt2 = (ParameterizedType)fieldGt;
            Type[] actualTypes = fieldGt2.getActualTypeArguments();
            boolean actualTypesChanged = false;
            fieldT = (Class)fieldGt2.getRawType();
            int i = 0;

            for(int len = actualTypes.length; i < len; ++i) {
               Type tmp = actualTypes[i];
               if (tmp instanceof TypeVariable) {
                  tmp = (Type)genericInfo.get(tmp);
                  if (tmp != null) {
                     actualTypes[i] = tmp;
                     actualTypesChanged = true;
                  }
               }
            }

            if (actualTypesChanged) {
               fieldGt = new ParameterizedTypeImpl((Class)fieldGt2.getRawType(), actualTypes, fieldGt2.getOwnerType());
            }
         }
      }

      return this.analyse(ctx, o.get(fieldK), rst, fieldT, (Type)fieldGt, genericInfo);
   }

   private Class<?> getTypeByNode(Context ctx, ONode o, Class<?> def) {
      if (ctx.target_type == null) {
         if (o.isObject()) {
            return LinkedHashMap.class;
         }

         if (o.isArray()) {
            return ArrayList.class;
         }
      }

      String typeStr = null;
      ONode n1;
      if (o.isArray() && o.ary().size() == 2) {
         n1 = (ONode)o.ary().get(0);
         if (n1.isObject() && n1.obj().size() == 1) {
            ONode n1 = (ONode)n1.obj().get(ctx.options.getTypePropertyName());
            if (n1 != null) {
               typeStr = n1.val().getString();
            }
         }
      }

      if (o.isObject()) {
         n1 = (ONode)o.obj().get(ctx.options.getTypePropertyName());
         if (n1 != null) {
            typeStr = n1.val().getString();
         }
      }

      if (!StringUtil.isEmpty(typeStr)) {
         Class<?> clz = BeanUtil.loadClass(typeStr);
         if (clz == null) {
            throw new SnackException("unsupport type " + typeStr);
         } else {
            return clz;
         }
      } else {
         if (def == null || def == Object.class) {
            if (o.isObject()) {
               return LinkedHashMap.class;
            }

            if (o.isArray()) {
               return ArrayList.class;
            }
         }

         return def;
      }
   }
}
