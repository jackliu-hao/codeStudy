package org.noear.solon.serialization.snack3;

import java.io.IOException;
import org.noear.snack.ONode;
import org.noear.snack.core.Options;
import org.noear.solon.serialization.StringSerializer;

public class SnackSerializer implements StringSerializer {
   final Options options;

   public SnackSerializer(Options options) {
      this.options = options;
   }

   public String serialize(Object obj) throws IOException {
      return ONode.loadObj(obj, this.options).toJson();
   }
}
