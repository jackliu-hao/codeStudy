package cn.hutool.setting.yaml;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Dict;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class YamlUtil {
   public static Dict loadByPath(String path) {
      return (Dict)loadByPath(path, Dict.class);
   }

   public static <T> T loadByPath(String path, Class<T> type) {
      return load(ResourceUtil.getStream(path), type);
   }

   public static <T> T load(InputStream in, Class<T> type) {
      return load((Reader)IoUtil.getBomReader(in), type);
   }

   public static Dict load(Reader reader) {
      return (Dict)load(reader, Dict.class);
   }

   public static <T> T load(Reader reader, Class<T> type) {
      return load(reader, type, true);
   }

   public static <T> T load(Reader reader, Class<T> type, boolean isCloseReader) {
      Assert.notNull(reader, "Reader must be not null !");
      if (null == type) {
         type = Object.class;
      }

      Yaml yaml = new Yaml();

      Object var4;
      try {
         var4 = yaml.loadAs(reader, type);
      } finally {
         if (isCloseReader) {
            IoUtil.close(reader);
         }

      }

      return var4;
   }

   public static void dump(Object object, Writer writer) {
      DumperOptions options = new DumperOptions();
      options.setIndent(2);
      options.setPrettyFlow(true);
      options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
      dump(object, writer, options);
   }

   public static void dump(Object object, Writer writer, DumperOptions dumperOptions) {
      if (null == dumperOptions) {
         dumperOptions = new DumperOptions();
      }

      Yaml yaml = new Yaml(dumperOptions);
      yaml.dump(object, writer);
   }
}
