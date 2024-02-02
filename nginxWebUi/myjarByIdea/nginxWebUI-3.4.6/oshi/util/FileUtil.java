package oshi.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class FileUtil {
   private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);
   private static final String READING_LOG = "Reading file {}";
   private static final String READ_LOG = "Read {}";

   private FileUtil() {
   }

   public static List<String> readFile(String filename) {
      return readFile(filename, true);
   }

   public static List<String> readFile(String filename, boolean reportError) {
      if ((new File(filename)).canRead()) {
         if (LOG.isDebugEnabled()) {
            LOG.debug((String)"Reading file {}", (Object)filename);
         }

         try {
            return Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
         } catch (IOException var3) {
            if (reportError) {
               LOG.error((String)"Error reading file {}. {}", (Object)filename, (Object)var3.getMessage());
            }
         }
      } else if (reportError) {
         LOG.warn((String)"File not found or not readable: {}", (Object)filename);
      }

      return new ArrayList();
   }

   public static long getLongFromFile(String filename) {
      if (LOG.isDebugEnabled()) {
         LOG.debug((String)"Reading file {}", (Object)filename);
      }

      List<String> read = readFile(filename, false);
      if (!read.isEmpty()) {
         if (LOG.isTraceEnabled()) {
            LOG.trace("Read {}", read.get(0));
         }

         return ParseUtil.parseLongOrDefault((String)read.get(0), 0L);
      } else {
         return 0L;
      }
   }

   public static long getUnsignedLongFromFile(String filename) {
      if (LOG.isDebugEnabled()) {
         LOG.debug((String)"Reading file {}", (Object)filename);
      }

      List<String> read = readFile(filename, false);
      if (!read.isEmpty()) {
         if (LOG.isTraceEnabled()) {
            LOG.trace("Read {}", read.get(0));
         }

         return ParseUtil.parseUnsignedLongOrDefault((String)read.get(0), 0L);
      } else {
         return 0L;
      }
   }

   public static int getIntFromFile(String filename) {
      if (LOG.isDebugEnabled()) {
         LOG.debug((String)"Reading file {}", (Object)filename);
      }

      try {
         List<String> read = readFile(filename, false);
         if (!read.isEmpty()) {
            if (LOG.isTraceEnabled()) {
               LOG.trace("Read {}", read.get(0));
            }

            return Integer.parseInt((String)read.get(0));
         }
      } catch (NumberFormatException var2) {
         LOG.warn((String)"Unable to read value from {}. {}", (Object)filename, (Object)var2.getMessage());
      }

      return 0;
   }

   public static String getStringFromFile(String filename) {
      if (LOG.isDebugEnabled()) {
         LOG.debug((String)"Reading file {}", (Object)filename);
      }

      List<String> read = readFile(filename, false);
      if (!read.isEmpty()) {
         if (LOG.isTraceEnabled()) {
            LOG.trace("Read {}", read.get(0));
         }

         return (String)read.get(0);
      } else {
         return "";
      }
   }

   public static Map<String, String> getKeyValueMapFromFile(String filename, String separator) {
      Map<String, String> map = new HashMap();
      if (LOG.isDebugEnabled()) {
         LOG.debug((String)"Reading file {}", (Object)filename);
      }

      List<String> lines = readFile(filename, false);
      Iterator var4 = lines.iterator();

      while(var4.hasNext()) {
         String line = (String)var4.next();
         String[] parts = line.split(separator);
         if (parts.length == 2) {
            map.put(parts[0], parts[1].trim());
         }
      }

      return map;
   }

   public static Properties readPropertiesFromFilename(String propsFilename) {
      Properties archProps = new Properties();
      if (!readPropertiesFromClassLoader(propsFilename, archProps, Thread.currentThread().getContextClassLoader()) && !readPropertiesFromClassLoader(propsFilename, archProps, ClassLoader.getSystemClassLoader()) && !readPropertiesFromClassLoader(propsFilename, archProps, FileUtil.class.getClassLoader())) {
         LOG.warn("Failed to load default configuration");
         return archProps;
      } else {
         return archProps;
      }
   }

   private static boolean readPropertiesFromClassLoader(String propsFilename, Properties archProps, ClassLoader loader) {
      if (loader == null) {
         return false;
      } else {
         try {
            List<URL> resources = Collections.list(loader.getResources(propsFilename));
            if (resources.isEmpty()) {
               LOG.info((String)"No {} file found from ClassLoader {}", (Object)propsFilename, (Object)loader);
               return false;
            } else if (resources.size() > 1) {
               LOG.warn((String)"Configuration conflict: there is more than one {} file on the classpath", (Object)propsFilename);
               return true;
            } else {
               InputStream in = ((URL)resources.get(0)).openStream();

               try {
                  if (in != null) {
                     archProps.load(in);
                  }
               } catch (Throwable var8) {
                  if (in != null) {
                     try {
                        in.close();
                     } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                     }
                  }

                  throw var8;
               }

               if (in != null) {
                  in.close();
               }

               return true;
            }
         } catch (IOException var9) {
            return false;
         }
      }
   }
}
