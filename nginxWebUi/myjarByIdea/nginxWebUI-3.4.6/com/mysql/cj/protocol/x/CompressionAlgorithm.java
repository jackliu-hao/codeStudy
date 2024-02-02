package com.mysql.cj.protocol.x;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.InflaterInputStream;

public class CompressionAlgorithm {
   private static final Map<String, String> ALIASES = new HashMap();
   private String algorithmIdentifier;
   private CompressionMode compressionMode;
   private String inputStreamClassFqn;
   private Class<?> inputStreamClass = null;
   private String outputStreamClassFqn;
   private Class<?> outputStreamClass = null;

   public static Map<String, CompressionAlgorithm> getDefaultInstances() {
      HashMap<String, CompressionAlgorithm> defaultInstances = new HashMap();
      defaultInstances.put("deflate_stream", new CompressionAlgorithm("deflate_stream", InflaterInputStream.class.getName(), SyncFlushDeflaterOutputStream.class.getName()));
      return defaultInstances;
   }

   public static String getNormalizedAlgorithmName(String name) {
      return (String)ALIASES.getOrDefault(name, name);
   }

   public CompressionAlgorithm(String name, String inputStreamClassFqn, String outputStreamClassFqn) {
      this.algorithmIdentifier = getNormalizedAlgorithmName(name);
      String[] nameMode = this.algorithmIdentifier.split("_");
      if (nameMode.length != 2) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Protocol.Compression.4", new Object[]{name}));
      } else {
         try {
            CompressionMode mode = CompressionMode.valueOf(nameMode[1].toUpperCase());
            this.compressionMode = mode;
         } catch (IllegalArgumentException var6) {
            throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Protocol.Compression.5", new Object[]{nameMode[1]}));
         }

         this.inputStreamClassFqn = inputStreamClassFqn;
         this.outputStreamClassFqn = outputStreamClassFqn;
      }
   }

   public String getAlgorithmIdentifier() {
      return this.algorithmIdentifier;
   }

   public CompressionMode getCompressionMode() {
      return this.compressionMode;
   }

   public Class<?> getInputStreamClass() {
      if (this.inputStreamClass == null) {
         try {
            this.inputStreamClass = Class.forName(this.inputStreamClassFqn);
         } catch (ClassNotFoundException var2) {
            throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)Messages.getString("Protocol.Compression.3", new Object[]{this.inputStreamClassFqn}), (Throwable)var2);
         }
      }

      return this.inputStreamClass;
   }

   public Class<?> getOutputStreamClass() {
      if (this.outputStreamClass == null) {
         try {
            this.outputStreamClass = Class.forName(this.outputStreamClassFqn);
         } catch (ClassNotFoundException var2) {
            throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)Messages.getString("Protocol.Compression.3", new Object[]{this.outputStreamClassFqn}), (Throwable)var2);
         }
      }

      return this.outputStreamClass;
   }

   static {
      ALIASES.put("deflate", "deflate_stream");
      ALIASES.put("lz4", "lz4_message");
      ALIASES.put("zstd", "zstd_stream");
   }
}
