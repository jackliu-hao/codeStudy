package oshi.util.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.time.OffsetDateTime;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.Constants;
import oshi.util.ParseUtil;

@ThreadSafe
public final class WmiUtil {
   public static final String OHM_NAMESPACE = "ROOT\\OpenHardwareMonitor";
   private static final String CLASS_CAST_MSG = "%s is not a %s type. CIM Type is %d and VT type is %d";

   private WmiUtil() {
   }

   public static <T extends Enum<T>> String queryToString(WbemcliUtil.WmiQuery<T> query) {
      T[] props = (Enum[])query.getPropertyEnum().getEnumConstants();
      StringBuilder sb = new StringBuilder("SELECT ");
      sb.append(props[0].name());

      for(int i = 1; i < props.length; ++i) {
         sb.append(',').append(props[i].name());
      }

      sb.append(" FROM ").append(query.getWmiClassName());
      return sb.toString();
   }

   public static <T extends Enum<T>> String getString(WbemcliUtil.WmiResult<T> result, T property, int index) {
      if (result.getCIMType(property) == 8) {
         return getStr(result, property, index);
      } else {
         throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", property.name(), "String", result.getCIMType(property), result.getVtType(property)));
      }
   }

   public static <T extends Enum<T>> String getDateString(WbemcliUtil.WmiResult<T> result, T property, int index) {
      OffsetDateTime dateTime = getDateTime(result, property, index);
      return dateTime.equals(Constants.UNIX_EPOCH) ? "" : dateTime.toLocalDate().toString();
   }

   public static <T extends Enum<T>> OffsetDateTime getDateTime(WbemcliUtil.WmiResult<T> result, T property, int index) {
      if (result.getCIMType(property) == 101) {
         return ParseUtil.parseCimDateTimeToOffset(getStr(result, property, index));
      } else {
         throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", property.name(), "DateTime", result.getCIMType(property), result.getVtType(property)));
      }
   }

   public static <T extends Enum<T>> String getRefString(WbemcliUtil.WmiResult<T> result, T property, int index) {
      if (result.getCIMType(property) == 102) {
         return getStr(result, property, index);
      } else {
         throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", property.name(), "Reference", result.getCIMType(property), result.getVtType(property)));
      }
   }

   private static <T extends Enum<T>> String getStr(WbemcliUtil.WmiResult<T> result, T property, int index) {
      Object o = result.getValue(property, index);
      if (o == null) {
         return "";
      } else if (result.getVtType(property) == 8) {
         return (String)o;
      } else {
         throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", property.name(), "String-mapped", result.getCIMType(property), result.getVtType(property)));
      }
   }

   public static <T extends Enum<T>> long getUint64(WbemcliUtil.WmiResult<T> result, T property, int index) {
      Object o = result.getValue(property, index);
      if (o == null) {
         return 0L;
      } else if (result.getCIMType(property) == 21 && result.getVtType(property) == 8) {
         return ParseUtil.parseLongOrDefault((String)o, 0L);
      } else {
         throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", property.name(), "UINT64", result.getCIMType(property), result.getVtType(property)));
      }
   }

   public static <T extends Enum<T>> int getUint32(WbemcliUtil.WmiResult<T> result, T property, int index) {
      if (result.getCIMType(property) == 19) {
         return getInt(result, property, index);
      } else {
         throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", property.name(), "UINT32", result.getCIMType(property), result.getVtType(property)));
      }
   }

   public static <T extends Enum<T>> long getUint32asLong(WbemcliUtil.WmiResult<T> result, T property, int index) {
      if (result.getCIMType(property) == 19) {
         return (long)getInt(result, property, index) & 4294967295L;
      } else {
         throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", property.name(), "UINT32", result.getCIMType(property), result.getVtType(property)));
      }
   }

   public static <T extends Enum<T>> int getSint32(WbemcliUtil.WmiResult<T> result, T property, int index) {
      if (result.getCIMType(property) == 3) {
         return getInt(result, property, index);
      } else {
         throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", property.name(), "SINT32", result.getCIMType(property), result.getVtType(property)));
      }
   }

   public static <T extends Enum<T>> int getUint16(WbemcliUtil.WmiResult<T> result, T property, int index) {
      if (result.getCIMType(property) == 18) {
         return getInt(result, property, index);
      } else {
         throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", property.name(), "UINT16", result.getCIMType(property), result.getVtType(property)));
      }
   }

   private static <T extends Enum<T>> int getInt(WbemcliUtil.WmiResult<T> result, T property, int index) {
      Object o = result.getValue(property, index);
      if (o == null) {
         return 0;
      } else if (result.getVtType(property) == 3) {
         return (Integer)o;
      } else {
         throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", property.name(), "32-bit integer", result.getCIMType(property), result.getVtType(property)));
      }
   }

   public static <T extends Enum<T>> float getFloat(WbemcliUtil.WmiResult<T> result, T property, int index) {
      Object o = result.getValue(property, index);
      if (o == null) {
         return 0.0F;
      } else if (result.getCIMType(property) == 4 && result.getVtType(property) == 4) {
         return (Float)o;
      } else {
         throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", property.name(), "Float", result.getCIMType(property), result.getVtType(property)));
      }
   }
}
