/*    */ package com.google.zxing.client.j2se;
/*    */ 
/*    */ import com.beust.jcommander.JCommander;
/*    */ import com.google.zxing.EncodeHintType;
/*    */ import com.google.zxing.MultiFormatWriter;
/*    */ import com.google.zxing.common.BitMatrix;
/*    */ import java.nio.file.Paths;
/*    */ import java.util.EnumMap;
/*    */ import java.util.Locale;
/*    */ import java.util.Map;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class CommandLineEncoder
/*    */ {
/*    */   public static void main(String[] args) throws Exception {
/* 41 */     EncoderConfig config = new EncoderConfig();
/* 42 */     JCommander jCommander = new JCommander(config, args);
/* 43 */     jCommander.setProgramName(CommandLineEncoder.class.getSimpleName());
/* 44 */     if (config.help) {
/* 45 */       jCommander.usage();
/*    */       
/*    */       return;
/*    */     } 
/* 49 */     String outFileString = config.outputFileBase;
/* 50 */     if ("out".equals(outFileString)) {
/* 51 */       outFileString = outFileString + '.' + config.imageFormat.toLowerCase(Locale.ENGLISH);
/*    */     }
/* 53 */     Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
/* 54 */     if (config.errorCorrectionLevel != null) {
/* 55 */       hints.put(EncodeHintType.ERROR_CORRECTION, config.errorCorrectionLevel);
/*    */     }
/* 57 */     BitMatrix matrix = (new MultiFormatWriter()).encode(config.contents
/* 58 */         .get(0), config.barcodeFormat, config.width, config.height, hints);
/*    */     
/* 60 */     MatrixToImageWriter.writeToPath(matrix, config.imageFormat, 
/* 61 */         Paths.get(outFileString, new String[0]));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\j2se\CommandLineEncoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */