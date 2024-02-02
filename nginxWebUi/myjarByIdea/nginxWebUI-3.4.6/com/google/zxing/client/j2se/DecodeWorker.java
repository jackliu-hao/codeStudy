package com.google.zxing.client.j2se;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import com.google.zxing.multi.MultipleBarcodeReader;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import javax.imageio.ImageIO;

final class DecodeWorker implements Callable<Integer> {
   private static final int RED = -65536;
   private static final int BLACK = -16777216;
   private static final int WHITE = -1;
   private final DecoderConfig config;
   private final Queue<URI> inputs;
   private final Map<DecodeHintType, ?> hints;

   DecodeWorker(DecoderConfig config, Queue<URI> inputs) {
      this.config = config;
      this.inputs = inputs;
      this.hints = config.buildHints();
   }

   public Integer call() throws IOException {
      int successful = 0;

      URI input;
      while((input = (URI)this.inputs.poll()) != null) {
         Result[] results = this.decode(input, this.hints);
         if (results != null) {
            ++successful;
            if (this.config.dumpResults) {
               dumpResult(input, results);
            }
         }
      }

      return successful;
   }

   private static Path buildOutputPath(URI input, String suffix) throws IOException {
      Path outDir;
      String inputFileName;
      if ("file".equals(input.getScheme())) {
         Path inputPath = Paths.get(input);
         outDir = inputPath.getParent();
         inputFileName = inputPath.getFileName().toString();
      } else {
         outDir = Paths.get(".").toRealPath();
         String[] pathElements = input.getPath().split("/");
         inputFileName = pathElements[pathElements.length - 1];
      }

      int pos = inputFileName.lastIndexOf(46);
      if (pos > 0) {
         inputFileName = inputFileName.substring(0, pos) + suffix;
      } else {
         inputFileName = inputFileName + suffix;
      }

      return outDir.resolve(inputFileName);
   }

   private static void dumpResult(URI input, Result... results) throws IOException {
      Collection<String> resultTexts = new ArrayList();
      Result[] var3 = results;
      int var4 = results.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Result result = var3[var5];
         resultTexts.add(result.getText());
      }

      Files.write(buildOutputPath(input, ".txt"), resultTexts, StandardCharsets.UTF_8);
   }

   private Result[] decode(URI uri, Map<DecodeHintType, ?> hints) throws IOException {
      BufferedImage image = ImageReader.readImage(uri);
      BufferedImageLuminanceSource source;
      if (this.config.crop == null) {
         source = new BufferedImageLuminanceSource(image);
      } else {
         List<Integer> crop = this.config.crop;
         source = new BufferedImageLuminanceSource(image, (Integer)crop.get(0), (Integer)crop.get(1), (Integer)crop.get(2), (Integer)crop.get(3));
      }

      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
      if (this.config.dumpBlackPoint) {
         dumpBlackPoint(uri, image, bitmap);
      }

      MultiFormatReader multiFormatReader = new MultiFormatReader();

      Result[] results;
      try {
         if (this.config.multi) {
            MultipleBarcodeReader reader = new GenericMultipleBarcodeReader(multiFormatReader);
            results = reader.decodeMultiple(bitmap, hints);
         } else {
            results = new Result[]{multiFormatReader.decode(bitmap, hints)};
         }
      } catch (NotFoundException var14) {
         System.out.println(uri + ": No barcode found");
         return null;
      }

      if (this.config.brief) {
         System.out.println(uri + ": Success");
      } else {
         StringWriter output = new StringWriter();

         for(int resultIndex = 0; resultIndex < results.length; ++resultIndex) {
            Result result = results[resultIndex];
            ParsedResult parsedResult = ResultParser.parseResult(result);
            output.write(uri + " (format: " + result.getBarcodeFormat() + ", type: " + parsedResult.getType() + "):\nRaw result:\n" + result.getText() + "\nParsed result:\n" + parsedResult.getDisplayResult() + "\n");
            output.write("Found " + result.getResultPoints().length + " result points.\n");

            for(int pointIndex = 0; pointIndex < result.getResultPoints().length; ++pointIndex) {
               ResultPoint rp = result.getResultPoints()[pointIndex];
               output.write("  Point " + pointIndex + ": (" + rp.getX() + ',' + rp.getY() + ')');
               if (pointIndex != result.getResultPoints().length - 1) {
                  output.write(10);
               }
            }

            if (resultIndex != results.length - 1) {
               output.write(10);
            }
         }

         System.out.println(output);
      }

      return results;
   }

   private static void dumpBlackPoint(URI uri, BufferedImage image, BinaryBitmap bitmap) throws IOException {
      int width = bitmap.getWidth();
      int height = bitmap.getHeight();
      int stride = width * 3;
      int[] pixels = new int[stride * height];
      int[] argb = new int[width];

      for(int y = 0; y < height; ++y) {
         image.getRGB(0, y, width, 1, argb, 0, width);
         System.arraycopy(argb, 0, pixels, y * stride, width);
      }

      BitArray row = new BitArray(width);

      int y;
      int offset;
      for(y = 0; y < height; ++y) {
         try {
            row = bitmap.getBlackRow(y, row);
         } catch (NotFoundException var14) {
            offset = y * stride + width;
            Arrays.fill(pixels, offset, offset + width, -65536);
            continue;
         }

         int offset = y * stride + width;

         for(offset = 0; offset < width; ++offset) {
            pixels[offset + offset] = row.get(offset) ? -16777216 : -1;
         }
      }

      try {
         for(y = 0; y < height; ++y) {
            BitMatrix matrix = bitmap.getBlackMatrix();
            offset = y * stride + width * 2;

            for(int x = 0; x < width; ++x) {
               pixels[offset + x] = matrix.get(x, y) ? -16777216 : -1;
            }
         }
      } catch (NotFoundException var13) {
      }

      writeResultImage(stride, height, pixels, uri, ".mono.png");
   }

   private static void writeResultImage(int stride, int height, int[] pixels, URI input, String suffix) throws IOException {
      BufferedImage result = new BufferedImage(stride, height, 2);
      result.setRGB(0, 0, stride, height, pixels, 0, stride);
      Path imagePath = buildOutputPath(input, suffix);

      try {
         if (!ImageIO.write(result, "png", imagePath.toFile())) {
            System.err.println("Could not encode an image to " + imagePath);
         }
      } catch (IOException var8) {
         System.err.println("Could not write to " + imagePath);
      }

   }
}
