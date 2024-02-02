/*     */ package com.google.zxing.client.j2se;
/*     */ 
/*     */ import com.google.zxing.Binarizer;
/*     */ import com.google.zxing.BinaryBitmap;
/*     */ import com.google.zxing.DecodeHintType;
/*     */ import com.google.zxing.LuminanceSource;
/*     */ import com.google.zxing.MultiFormatReader;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.Reader;
/*     */ import com.google.zxing.Result;
/*     */ import com.google.zxing.ResultPoint;
/*     */ import com.google.zxing.client.result.ParsedResult;
/*     */ import com.google.zxing.client.result.ResultParser;
/*     */ import com.google.zxing.common.BitArray;
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ import com.google.zxing.common.HybridBinarizer;
/*     */ import com.google.zxing.multi.GenericMultipleBarcodeReader;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.IOException;
/*     */ import java.io.StringWriter;
/*     */ import java.net.URI;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.Callable;
/*     */ import javax.imageio.ImageIO;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class DecodeWorker
/*     */   implements Callable<Integer>
/*     */ {
/*     */   private static final int RED = -65536;
/*     */   private static final int BLACK = -16777216;
/*     */   private static final int WHITE = -1;
/*     */   private final DecoderConfig config;
/*     */   private final Queue<URI> inputs;
/*     */   private final Map<DecodeHintType, ?> hints;
/*     */   
/*     */   DecodeWorker(DecoderConfig config, Queue<URI> inputs) {
/*  67 */     this.config = config;
/*  68 */     this.inputs = inputs;
/*  69 */     this.hints = config.buildHints();
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer call() throws IOException {
/*  74 */     int successful = 0; URI input;
/*  75 */     while ((input = this.inputs.poll()) != null) {
/*  76 */       Result[] results = decode(input, this.hints);
/*  77 */       if (results != null) {
/*  78 */         successful++;
/*  79 */         if (this.config.dumpResults) {
/*  80 */           dumpResult(input, results);
/*     */         }
/*     */       } 
/*     */     } 
/*  84 */     return Integer.valueOf(successful);
/*     */   }
/*     */   
/*     */   private static Path buildOutputPath(URI input, String suffix) throws IOException {
/*     */     Path outDir;
/*     */     String inputFileName;
/*  90 */     if ("file".equals(input.getScheme())) {
/*  91 */       Path inputPath = Paths.get(input);
/*  92 */       outDir = inputPath.getParent();
/*  93 */       inputFileName = inputPath.getFileName().toString();
/*     */     } else {
/*  95 */       outDir = Paths.get(".", new String[0]).toRealPath(new java.nio.file.LinkOption[0]);
/*  96 */       String[] pathElements = input.getPath().split("/");
/*  97 */       inputFileName = pathElements[pathElements.length - 1];
/*     */     } 
/*     */ 
/*     */     
/* 101 */     int pos = inputFileName.lastIndexOf('.');
/* 102 */     if (pos > 0) {
/* 103 */       inputFileName = inputFileName.substring(0, pos) + suffix;
/*     */     } else {
/* 105 */       inputFileName = inputFileName + suffix;
/*     */     } 
/*     */     
/* 108 */     return outDir.resolve(inputFileName);
/*     */   }
/*     */   
/*     */   private static void dumpResult(URI input, Result... results) throws IOException {
/* 112 */     Collection<String> resultTexts = new ArrayList<>();
/* 113 */     for (Result result : results) {
/* 114 */       resultTexts.add(result.getText());
/*     */     }
/* 116 */     Files.write(buildOutputPath(input, ".txt"), (Iterable)resultTexts, StandardCharsets.UTF_8, new java.nio.file.OpenOption[0]);
/*     */   } private Result[] decode(URI uri, Map<DecodeHintType, ?> hints) throws IOException {
/*     */     LuminanceSource source;
/*     */     Result[] results;
/* 120 */     BufferedImage image = ImageReader.readImage(uri);
/*     */ 
/*     */     
/* 123 */     if (this.config.crop == null) {
/* 124 */       source = new BufferedImageLuminanceSource(image);
/*     */     } else {
/* 126 */       List<Integer> crop = this.config.crop;
/*     */       
/* 128 */       source = new BufferedImageLuminanceSource(image, ((Integer)crop.get(0)).intValue(), ((Integer)crop.get(1)).intValue(), ((Integer)crop.get(2)).intValue(), ((Integer)crop.get(3)).intValue());
/*     */     } 
/*     */     
/* 131 */     BinaryBitmap bitmap = new BinaryBitmap((Binarizer)new HybridBinarizer(source));
/* 132 */     if (this.config.dumpBlackPoint) {
/* 133 */       dumpBlackPoint(uri, image, bitmap);
/*     */     }
/*     */     
/* 136 */     MultiFormatReader multiFormatReader = new MultiFormatReader();
/*     */     
/*     */     try {
/* 139 */       if (this.config.multi) {
/* 140 */         GenericMultipleBarcodeReader genericMultipleBarcodeReader = new GenericMultipleBarcodeReader((Reader)multiFormatReader);
/* 141 */         results = genericMultipleBarcodeReader.decodeMultiple(bitmap, hints);
/*     */       } else {
/* 143 */         results = new Result[] { multiFormatReader.decode(bitmap, hints) };
/*     */       } 
/* 145 */     } catch (NotFoundException ignored) {
/* 146 */       System.out.println(uri + ": No barcode found");
/* 147 */       return null;
/*     */     } 
/*     */     
/* 150 */     if (this.config.brief) {
/* 151 */       System.out.println(uri + ": Success");
/*     */     } else {
/* 153 */       StringWriter output = new StringWriter();
/* 154 */       for (int resultIndex = 0; resultIndex < results.length; resultIndex++) {
/* 155 */         Result result = results[resultIndex];
/* 156 */         ParsedResult parsedResult = ResultParser.parseResult(result);
/* 157 */         output.write(uri + " (format: " + result
/* 158 */             .getBarcodeFormat() + ", type: " + parsedResult
/* 159 */             .getType() + "):\nRaw result:\n" + result
/*     */             
/* 161 */             .getText() + "\nParsed result:\n" + parsedResult
/*     */             
/* 163 */             .getDisplayResult() + "\n");
/* 164 */         output.write("Found " + (result.getResultPoints()).length + " result points.\n");
/* 165 */         for (int pointIndex = 0; pointIndex < (result.getResultPoints()).length; pointIndex++) {
/* 166 */           ResultPoint rp = result.getResultPoints()[pointIndex];
/* 167 */           output.write("  Point " + pointIndex + ": (" + rp.getX() + ',' + rp.getY() + ')');
/* 168 */           if (pointIndex != (result.getResultPoints()).length - 1) {
/* 169 */             output.write(10);
/*     */           }
/*     */         } 
/* 172 */         if (resultIndex != results.length - 1) {
/* 173 */           output.write(10);
/*     */         }
/*     */       } 
/* 176 */       System.out.println(output);
/*     */     } 
/*     */     
/* 179 */     return results;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void dumpBlackPoint(URI uri, BufferedImage image, BinaryBitmap bitmap) throws IOException {
/* 188 */     int width = bitmap.getWidth();
/* 189 */     int height = bitmap.getHeight();
/* 190 */     int stride = width * 3;
/* 191 */     int[] pixels = new int[stride * height];
/*     */ 
/*     */     
/* 194 */     int[] argb = new int[width];
/* 195 */     for (int y = 0; y < height; y++) {
/* 196 */       image.getRGB(0, y, width, 1, argb, 0, width);
/* 197 */       System.arraycopy(argb, 0, pixels, y * stride, width);
/*     */     } 
/*     */ 
/*     */     
/* 201 */     BitArray row = new BitArray(width); int i;
/* 202 */     for (i = 0; i < height; i++) {
/*     */       try {
/* 204 */         row = bitmap.getBlackRow(i, row);
/* 205 */       } catch (NotFoundException nfe) {
/*     */         
/* 207 */         int j = i * stride + width;
/* 208 */         Arrays.fill(pixels, j, j + width, -65536);
/*     */       } 
/*     */ 
/*     */       
/* 212 */       int offset = i * stride + width;
/* 213 */       for (int x = 0; x < width; x++) {
/* 214 */         pixels[offset + x] = row.get(x) ? -16777216 : -1;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 220 */       for (i = 0; i < height; i++) {
/* 221 */         BitMatrix matrix = bitmap.getBlackMatrix();
/* 222 */         int offset = i * stride + width * 2;
/* 223 */         for (int x = 0; x < width; x++) {
/* 224 */           pixels[offset + x] = matrix.get(x, i) ? -16777216 : -1;
/*     */         }
/*     */       } 
/* 227 */     } catch (NotFoundException notFoundException) {}
/*     */ 
/*     */ 
/*     */     
/* 231 */     writeResultImage(stride, height, pixels, uri, ".mono.png");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void writeResultImage(int stride, int height, int[] pixels, URI input, String suffix) throws IOException {
/* 239 */     BufferedImage result = new BufferedImage(stride, height, 2);
/* 240 */     result.setRGB(0, 0, stride, height, pixels, 0, stride);
/* 241 */     Path imagePath = buildOutputPath(input, suffix);
/*     */     try {
/* 243 */       if (!ImageIO.write(result, "png", imagePath.toFile())) {
/* 244 */         System.err.println("Could not encode an image to " + imagePath);
/*     */       }
/* 246 */     } catch (IOException ignored) {
/* 247 */       System.err.println("Could not write to " + imagePath);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\j2se\DecodeWorker.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */