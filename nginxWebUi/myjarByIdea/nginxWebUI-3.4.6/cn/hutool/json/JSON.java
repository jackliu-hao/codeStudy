package cn.hutool.json;

import cn.hutool.core.lang.TypeReference;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;

public interface JSON extends Cloneable, Serializable {
   Object getByPath(String var1);

   void putByPath(String var1, Object var2);

   <T> T getByPath(String var1, Class<T> var2);

   default String toStringPretty() throws JSONException {
      return this.toJSONString(4);
   }

   default String toJSONString(int indentFactor) throws JSONException {
      StringWriter sw = new StringWriter();
      synchronized(sw.getBuffer()) {
         return this.write(sw, indentFactor, 0).toString();
      }
   }

   default Writer write(Writer writer) throws JSONException {
      return this.write(writer, 0, 0);
   }

   Writer write(Writer var1, int var2, int var3) throws JSONException;

   default <T> T toBean(Class<T> clazz) {
      return this.toBean((Type)clazz);
   }

   default <T> T toBean(TypeReference<T> reference) {
      return this.toBean(reference.getType());
   }

   default <T> T toBean(Type type) {
      return this.toBean(type, false);
   }

   default <T> T toBean(Type type, boolean ignoreError) {
      return JSONConverter.jsonConvert(type, this, ignoreError);
   }
}
