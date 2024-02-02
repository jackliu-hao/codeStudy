package org.noear.snack.from;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.Clob;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import org.noear.snack.ONode;
import org.noear.snack.core.Context;
import org.noear.snack.core.DEFAULTS;
import org.noear.snack.core.Feature;
import org.noear.snack.core.NodeEncoderEntity;
import org.noear.snack.core.Options;
import org.noear.snack.core.exts.ClassWrap;
import org.noear.snack.core.exts.FieldWrap;
import org.noear.snack.core.utils.BeanUtil;
import org.noear.snack.core.utils.DateUtil;
import org.noear.snack.core.utils.StringUtil;

public class ObjectFromer implements Fromer {
   public void handle(Context ctx) {
      ctx.target = this.analyse(ctx.options, ctx.source);
   }

   private ONode analyse(Options opt, Object source) {
      ONode rst = new ONode(opt);
      if (source == null) {
         return rst;
      } else {
         Class<?> clz = source.getClass();
         Iterator var5 = opt.encoders().iterator();

         while(var5.hasNext()) {
            NodeEncoderEntity encoder = (NodeEncoderEntity)var5.next();
            if (encoder.isEncodable(clz)) {
               encoder.encode(source, rst);
               return rst;
            }
         }

         if (source instanceof ONode) {
            rst.val(source);
         } else if (source instanceof String) {
            rst.val().setString((String)source);
         } else if (source instanceof Date) {
            rst.val().setDate((Date)source);
         } else {
            Instant instant;
            if (source instanceof LocalDateTime) {
               instant = ((LocalDateTime)source).atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toInstant();
               rst.val().setDate(new Date(instant.getEpochSecond() * 1000L + (long)(instant.getNano() / 1000000)));
            } else if (source instanceof LocalDate) {
               instant = ((LocalDate)source).atTime(LocalTime.MIN).atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toInstant();
               rst.val().setDate(new Date(instant.getEpochSecond() * 1000L));
            } else if (source instanceof LocalTime) {
               instant = ((LocalTime)source).atDate(LocalDate.of(1970, 1, 1)).atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toInstant();
               rst.val().setDate(new Date(instant.getEpochSecond() * 1000L));
            } else if (source instanceof Boolean) {
               rst.val().setBool((Boolean)source);
            } else if (source instanceof Number) {
               rst.val().setNumber((Number)source);
            } else if (source instanceof Throwable) {
               this.analyseBean(opt, rst, clz, source);
            } else if (source instanceof Properties) {
               this.analyseProps(opt, rst, clz, source);
            } else if (!this.analyseArray(opt, rst, clz, source)) {
               if (clz.isEnum()) {
                  Enum em = (Enum)source;
                  if (opt.hasFeature(Feature.EnumUsingName)) {
                     rst.val().setString(em.name());
                  } else {
                     rst.val().setNumber(em.ordinal());
                  }
               } else {
                  Object k;
                  Iterator var10;
                  if (source instanceof Map) {
                     if (opt.hasFeature(Feature.WriteClassName)) {
                        this.typeSet(opt, rst, clz);
                     }

                     rst.asObject();
                     Map map = (Map)source;
                     var10 = map.keySet().iterator();

                     while(var10.hasNext()) {
                        k = var10.next();
                        if (k != null) {
                           rst.setNode(k.toString(), this.analyse(opt, map.get(k)));
                        }
                     }
                  } else if (source instanceof Iterable) {
                     rst.asArray();
                     ONode ary = rst;
                     if (opt.hasFeature(Feature.WriteArrayClassName)) {
                        rst.add(this.typeSet(opt, new ONode(opt), clz));
                        ary = rst.addNew().asArray();
                     }

                     var10 = ((Iterable)source).iterator();

                     while(var10.hasNext()) {
                        k = var10.next();
                        ary.addNode(this.analyse(opt, k));
                     }
                  } else if (source instanceof Enumeration) {
                     rst.asArray();
                     Enumeration o = (Enumeration)source;

                     while(o.hasMoreElements()) {
                        rst.addNode(this.analyse(opt, o.nextElement()));
                     }
                  } else {
                     String clzName = clz.getName();
                     if (clzName.endsWith(".Undefined")) {
                        rst.val().setNull();
                     } else if (!this.analyseOther(opt, rst, clz, source) && !clzName.startsWith("jdk.")) {
                        this.analyseBean(opt, rst, clz, source);
                     }
                  }
               }
            }
         }

         return rst;
      }
   }

   private ONode typeSet(Options cfg, ONode o, Class<?> clz) {
      return o.set(cfg.getTypePropertyName(), clz.getName());
   }

   private boolean analyseArray(Options cfg, ONode rst, Class<?> clz, Object obj) {
      int var6;
      int var7;
      if (obj instanceof Object[]) {
         rst.asArray();
         Object[] var5 = (Object[])((Object[])obj);
         var6 = var5.length;

         for(var7 = 0; var7 < var6; ++var7) {
            Object o = var5[var7];
            rst.addNode(this.analyse(cfg, o));
         }
      } else {
         int o;
         if (obj instanceof byte[]) {
            rst.asArray();
            byte[] var10 = (byte[])((byte[])obj);
            var6 = var10.length;

            for(var7 = 0; var7 < var6; ++var7) {
               o = var10[var7];
               rst.addNode(this.analyse(cfg, Byte.valueOf((byte)o)));
            }
         } else if (obj instanceof short[]) {
            rst.asArray();
            short[] var11 = (short[])((short[])obj);
            var6 = var11.length;

            for(var7 = 0; var7 < var6; ++var7) {
               o = var11[var7];
               rst.addNode(this.analyse(cfg, Short.valueOf((short)o)));
            }
         } else if (obj instanceof int[]) {
            rst.asArray();
            int[] var12 = (int[])((int[])obj);
            var6 = var12.length;

            for(var7 = 0; var7 < var6; ++var7) {
               o = var12[var7];
               rst.addNode(this.analyse(cfg, o));
            }
         } else if (obj instanceof long[]) {
            rst.asArray();
            long[] var13 = (long[])((long[])obj);
            var6 = var13.length;

            for(var7 = 0; var7 < var6; ++var7) {
               long o = var13[var7];
               rst.addNode(this.analyse(cfg, o));
            }
         } else if (obj instanceof float[]) {
            rst.asArray();
            float[] var14 = (float[])((float[])obj);
            var6 = var14.length;

            for(var7 = 0; var7 < var6; ++var7) {
               float o = var14[var7];
               rst.addNode(this.analyse(cfg, o));
            }
         } else if (obj instanceof double[]) {
            rst.asArray();
            double[] var15 = (double[])((double[])obj);
            var6 = var15.length;

            for(var7 = 0; var7 < var6; ++var7) {
               double o = var15[var7];
               rst.addNode(this.analyse(cfg, o));
            }
         } else if (obj instanceof boolean[]) {
            rst.asArray();
            boolean[] var16 = (boolean[])((boolean[])obj);
            var6 = var16.length;

            for(var7 = 0; var7 < var6; ++var7) {
               boolean o = var16[var7];
               rst.addNode(this.analyse(cfg, o));
            }
         } else {
            if (!(obj instanceof char[])) {
               return false;
            }

            rst.asArray();
            char[] var17 = (char[])((char[])obj);
            var6 = var17.length;

            for(var7 = 0; var7 < var6; ++var7) {
               char o = var17[var7];
               rst.addNode(this.analyse(cfg, o));
            }
         }
      }

      return true;
   }

   private boolean analyseProps(Options cfg, ONode rst, Class<?> clz, Object obj) {
      Properties props = (Properties)obj;
      if (props.size() == 0) {
         rst.asNull();
         return true;
      } else {
         List<String> keyVector = new ArrayList();
         props.keySet().forEach((k) -> {
            if (k instanceof String) {
               keyVector.add((String)k);
            }

         });
         Collections.sort(keyVector);
         if (((String)keyVector.get(0)).startsWith("[")) {
            rst.asArray();
         } else {
            rst.asObject();
         }

         Iterator var7 = keyVector.iterator();

         while(var7.hasNext()) {
            String key = (String)var7.next();
            String val = props.getProperty(key);
            String[] keySegments = key.split("\\.");
            ONode n1 = rst;

            for(int i = 0; i < keySegments.length; ++i) {
               String p1 = keySegments[i];
               if (p1.endsWith("]")) {
                  int idx = Integer.parseInt(p1.substring(p1.lastIndexOf(91) + 1, p1.length() - 1));
                  p1 = p1.substring(0, p1.lastIndexOf(91));
                  if (p1.length() > 0) {
                     n1 = n1.getOrNew(p1).getOrNew(idx);
                  } else {
                     n1 = n1.getOrNew(idx);
                  }
               } else {
                  n1 = n1.getOrNew(p1);
               }
            }

            n1.val(val);
         }

         return true;
      }
   }

   private boolean analyseBean(Options cfg, ONode rst, Class<?> clz, Object obj) {
      rst.asObject();
      if (cfg.hasFeature(Feature.WriteClassName)) {
         rst.set(cfg.getTypePropertyName(), clz.getName());
      }

      Collection<FieldWrap> list = ClassWrap.get(clz).fieldAllWraps();
      Iterator var6 = list.iterator();

      while(true) {
         while(true) {
            FieldWrap f;
            do {
               while(true) {
                  do {
                     if (!var6.hasNext()) {
                        return true;
                     }

                     f = (FieldWrap)var6.next();
                  } while(!f.isSerialize());

                  Object val = f.getValue(obj);
                  if (val == null) {
                     break;
                  }

                  if (!val.equals(obj)) {
                     if (!StringUtil.isEmpty(f.getFormat())) {
                        if (val instanceof Date) {
                           String val2 = DateUtil.format((Date)val, f.getFormat());
                           rst.set(f.getName(), val2);
                           continue;
                        }

                        if (val instanceof Number) {
                           NumberFormat format = new DecimalFormat(f.getFormat());
                           String val2 = format.format(val);
                           rst.set(f.getName(), val2);
                           continue;
                        }
                     }

                     rst.setNode(f.getName(), this.analyse(cfg, val));
                  }
               }
            } while(!f.isIncNull());

            if (cfg.hasFeature(Feature.StringFieldInitEmpty) && f.genericType == String.class) {
               rst.setNode(f.getName(), this.analyse(cfg, ""));
            } else if (cfg.hasFeature(Feature.SerializeNulls)) {
               rst.setNode(f.getName(), this.analyse(cfg, (Object)null));
            }
         }
      }
   }

   private boolean analyseOther(Options cfg, ONode rst, Class<?> clz, Object obj) {
      if (obj instanceof SimpleDateFormat) {
         rst.set(cfg.getTypePropertyName(), clz.getName());
         rst.set("val", ((SimpleDateFormat)obj).toPattern());
      } else if (clz == Class.class) {
         rst.val().setString(clz.getName());
      } else if (obj instanceof InetSocketAddress) {
         InetSocketAddress address = (InetSocketAddress)obj;
         InetAddress inetAddress = address.getAddress();
         rst.set("address", inetAddress.getHostAddress());
         rst.set("port", address.getPort());
      } else if (obj instanceof File) {
         rst.val().setString(((File)obj).getPath());
      } else if (obj instanceof InetAddress) {
         rst.val().setString(((InetAddress)obj).getHostAddress());
      } else if (obj instanceof TimeZone) {
         rst.val().setString(((TimeZone)obj).getID());
      } else if (obj instanceof Currency) {
         rst.val().setString(((Currency)obj).getCurrencyCode());
      } else if (obj instanceof Iterator) {
         rst.asArray();
         ((Iterator)obj).forEachRemaining((vx) -> {
            rst.add(this.analyse(cfg, vx));
         });
      } else if (obj instanceof Map.Entry) {
         Map.Entry kv = (Map.Entry)obj;
         Object k = kv.getKey();
         Object v = kv.getValue();
         rst.asObject();
         if (k != null) {
            rst.set(k.toString(), this.analyse(cfg, v));
         }
      } else if (obj instanceof Calendar) {
         rst.val().setDate(((Calendar)obj).getTime());
      } else if (obj instanceof Clob) {
         rst.val().setString(BeanUtil.clobToString((Clob)obj));
      } else {
         if (!(obj instanceof Appendable)) {
            return false;
         }

         rst.val().setString(obj.toString());
      }

      return true;
   }
}
