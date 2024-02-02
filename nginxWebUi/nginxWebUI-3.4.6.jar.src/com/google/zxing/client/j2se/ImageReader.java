/*    */ package com.google.zxing.client.j2se;
/*    */ 
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.IOException;
/*    */ import java.net.URI;
/*    */ import java.net.URLDecoder;
/*    */ import javax.imageio.ImageIO;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ImageReader
/*    */ {
/*    */   private static final String BASE64TOKEN = "base64,";
/*    */   
/*    */   public static BufferedImage readImage(URI uri) throws IOException {
/*    */     BufferedImage result;
/* 39 */     if ("data".equals(uri.getScheme())) {
/* 40 */       return readDataURIImage(uri);
/*    */     }
/*    */     
/*    */     try {
/* 44 */       result = ImageIO.read(uri.toURL());
/* 45 */     } catch (IllegalArgumentException iae) {
/* 46 */       throw new IOException("Resource not found: " + uri, iae);
/*    */     } 
/* 48 */     if (result == null) {
/* 49 */       throw new IOException("Could not load " + uri);
/*    */     }
/* 51 */     return result;
/*    */   }
/*    */   
/*    */   public static BufferedImage readDataURIImage(URI uri) throws IOException {
/* 55 */     String uriString = uri.getSchemeSpecificPart();
/* 56 */     if (!uriString.startsWith("image/")) {
/* 57 */       throw new IOException("Unsupported data URI MIME type");
/*    */     }
/* 59 */     int base64Start = uriString.indexOf("base64,");
/* 60 */     if (base64Start < 0) {
/* 61 */       throw new IOException("Unsupported data URI encoding");
/*    */     }
/* 63 */     String base64DataEncoded = uriString.substring(base64Start + "base64,".length());
/* 64 */     String base64Data = URLDecoder.decode(base64DataEncoded, "UTF-8");
/* 65 */     byte[] imageBytes = Base64Decoder.getInstance().decode(base64Data);
/* 66 */     return ImageIO.read(new ByteArrayInputStream(imageBytes));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\j2se\ImageReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */