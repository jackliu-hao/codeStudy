package org.noear.solon.serialization.snack3;

import org.noear.snack.core.NodeEncoder;
import org.noear.snack.core.Options;
import org.noear.solon.serialization.JsonConverter;
import org.noear.solon.serialization.JsonRenderFactory;

public abstract class SnackRenderFactoryBase implements JsonRenderFactory {
   protected abstract Options config();

   public <T> void addEncoder(Class<T> clz, NodeEncoder<T> encoder) {
      this.config().addEncoder(clz, encoder);
   }

   public <T> void addConvertor(Class<T> clz, JsonConverter<T> converter) {
      this.addEncoder(clz, (source, target) -> {
         Object val = converter.convert(source);
         if (val == null) {
            target.asNull();
         } else if (val instanceof String) {
            target.val().setString((String)val);
         } else {
            if (!(val instanceof Number)) {
               throw new IllegalArgumentException("The result type of the converter is not supported: " + val.getClass().getName());
            }

            target.val().setNumber((Number)val);
         }

      });
   }
}
