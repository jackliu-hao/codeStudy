package org.noear.solon.config.yaml;

import java.io.IOException;
import java.util.Properties;
import org.noear.snack.ONode;

public class PropertiesJson extends Properties {
   public synchronized void loadJson(String text) throws IOException {
      ONode.loadStr(text).bindTo(this);
   }
}
