package cn.hutool.crypto;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

public class PemUtil {
   public static PrivateKey readPemPrivateKey(InputStream pemStream) {
      return (PrivateKey)readPemKey(pemStream);
   }

   public static PublicKey readPemPublicKey(InputStream pemStream) {
      return (PublicKey)readPemKey(pemStream);
   }

   public static Key readPemKey(InputStream keyStream) {
      PemObject object = readPemObject(keyStream);
      String type = object.getType();
      if (StrUtil.isNotBlank(type)) {
         if (type.endsWith("EC PRIVATE KEY")) {
            return KeyUtil.generatePrivateKey("EC", object.getContent());
         }

         if (type.endsWith("PRIVATE KEY")) {
            return KeyUtil.generateRSAPrivateKey(object.getContent());
         }

         if (type.endsWith("EC PUBLIC KEY")) {
            return KeyUtil.generatePublicKey("EC", object.getContent());
         }

         if (type.endsWith("PUBLIC KEY")) {
            return KeyUtil.generateRSAPublicKey(object.getContent());
         }

         if (type.endsWith("CERTIFICATE")) {
            return KeyUtil.readPublicKeyFromCert(IoUtil.toStream(object.getContent()));
         }
      }

      return null;
   }

   public static byte[] readPem(InputStream keyStream) {
      PemObject pemObject = readPemObject(keyStream);
      return null != pemObject ? pemObject.getContent() : null;
   }

   public static PemObject readPemObject(InputStream keyStream) {
      return readPemObject((Reader)IoUtil.getUtf8Reader(keyStream));
   }

   public static PemObject readPemObject(Reader reader) {
      PemReader pemReader = null;

      PemObject var2;
      try {
         pemReader = new PemReader(reader);
         var2 = pemReader.readPemObject();
      } catch (IOException var6) {
         throw new IORuntimeException(var6);
      } finally {
         IoUtil.close(pemReader);
      }

      return var2;
   }

   public static PrivateKey readSm2PemPrivateKey(InputStream keyStream) {
      PrivateKey var1;
      try {
         var1 = KeyUtil.generatePrivateKey("sm2", ECKeyUtil.createOpenSSHPrivateKeySpec(readPem(keyStream)));
      } finally {
         IoUtil.close(keyStream);
      }

      return var1;
   }

   public static String toPem(String type, byte[] content) {
      StringWriter stringWriter = new StringWriter();
      writePemObject(type, content, (Writer)stringWriter);
      return stringWriter.toString();
   }

   public static void writePemObject(String type, byte[] content, OutputStream keyStream) {
      writePemObject(new PemObject(type, content), (OutputStream)keyStream);
   }

   public static void writePemObject(String type, byte[] content, Writer writer) {
      writePemObject(new PemObject(type, content), (Writer)writer);
   }

   public static void writePemObject(PemObjectGenerator pemObject, OutputStream keyStream) {
      writePemObject(pemObject, (Writer)IoUtil.getUtf8Writer(keyStream));
   }

   public static void writePemObject(PemObjectGenerator pemObject, Writer writer) {
      PemWriter pemWriter = new PemWriter(writer);

      try {
         pemWriter.writeObject(pemObject);
      } catch (IOException var7) {
         throw new IORuntimeException(var7);
      } finally {
         IoUtil.close(pemWriter);
      }

   }
}
