package com.google.zxing.client.j2se;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import javax.imageio.ImageIO;

public final class ImageReader {
   private static final String BASE64TOKEN = "base64,";

   private ImageReader() {
   }

   public static BufferedImage readImage(URI uri) throws IOException {
      if ("data".equals(uri.getScheme())) {
         return readDataURIImage(uri);
      } else {
         BufferedImage result;
         try {
            result = ImageIO.read(uri.toURL());
         } catch (IllegalArgumentException var3) {
            throw new IOException("Resource not found: " + uri, var3);
         }

         if (result == null) {
            throw new IOException("Could not load " + uri);
         } else {
            return result;
         }
      }
   }

   public static BufferedImage readDataURIImage(URI uri) throws IOException {
      String uriString = uri.getSchemeSpecificPart();
      if (!uriString.startsWith("image/")) {
         throw new IOException("Unsupported data URI MIME type");
      } else {
         int base64Start = uriString.indexOf("base64,");
         if (base64Start < 0) {
            throw new IOException("Unsupported data URI encoding");
         } else {
            String base64DataEncoded = uriString.substring(base64Start + "base64,".length());
            String base64Data = URLDecoder.decode(base64DataEncoded, "UTF-8");
            byte[] imageBytes = Base64Decoder.getInstance().decode(base64Data);
            return ImageIO.read(new ByteArrayInputStream(imageBytes));
         }
      }
   }
}
