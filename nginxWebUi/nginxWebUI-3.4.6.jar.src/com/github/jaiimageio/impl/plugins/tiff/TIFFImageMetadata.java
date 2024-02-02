/*      */ package com.github.jaiimageio.impl.plugins.tiff;
/*      */ 
/*      */ import com.github.jaiimageio.plugins.tiff.TIFFField;
/*      */ import com.github.jaiimageio.plugins.tiff.TIFFTag;
/*      */ import com.github.jaiimageio.plugins.tiff.TIFFTagSet;
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.StringTokenizer;
/*      */ import javax.imageio.metadata.IIOInvalidTreeException;
/*      */ import javax.imageio.metadata.IIOMetadata;
/*      */ import javax.imageio.metadata.IIOMetadataNode;
/*      */ import javax.imageio.stream.ImageInputStream;
/*      */ import org.w3c.dom.NamedNodeMap;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TIFFImageMetadata
/*      */   extends IIOMetadata
/*      */ {
/*      */   public static final String nativeMetadataFormatName = "com_sun_media_imageio_plugins_tiff_image_1.0";
/*      */   public static final String nativeMetadataFormatClassName = "com.github.jaiimageio.impl.plugins.tiff.TIFFImageMetadataFormat";
/*      */   List tagSets;
/*      */   TIFFIFD rootIFD;
/*      */   
/*      */   public TIFFImageMetadata(List tagSets) {
/*   88 */     super(true, "com_sun_media_imageio_plugins_tiff_image_1.0", "com.github.jaiimageio.impl.plugins.tiff.TIFFImageMetadataFormat", null, null);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*   93 */     this.tagSets = tagSets;
/*   94 */     this.rootIFD = new TIFFIFD(tagSets);
/*      */   }
/*      */   
/*      */   public TIFFImageMetadata(TIFFIFD ifd) {
/*   98 */     super(true, "com_sun_media_imageio_plugins_tiff_image_1.0", "com.github.jaiimageio.impl.plugins.tiff.TIFFImageMetadataFormat", null, null);
/*      */ 
/*      */ 
/*      */     
/*  102 */     this.tagSets = ifd.getTagSetList();
/*  103 */     this.rootIFD = ifd;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void initializeFromStream(ImageInputStream stream, boolean ignoreUnknownFields) throws IOException {
/*  109 */     this.rootIFD.initialize(stream, ignoreUnknownFields);
/*      */   }
/*      */   
/*      */   public void addShortOrLongField(int tagNumber, int value) {
/*  113 */     TIFFField field = new TIFFField(this.rootIFD.getTag(tagNumber), value);
/*  114 */     this.rootIFD.addTIFFField(field);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isReadOnly() {
/*  135 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private Node getIFDAsTree(TIFFIFD ifd, String parentTagName, int parentTagNumber) {
/*  140 */     IIOMetadataNode IFDRoot = new IIOMetadataNode("TIFFIFD");
/*  141 */     if (parentTagNumber != 0) {
/*  142 */       IFDRoot.setAttribute("parentTagNumber", 
/*  143 */           Integer.toString(parentTagNumber));
/*      */     }
/*  145 */     if (parentTagName != null) {
/*  146 */       IFDRoot.setAttribute("parentTagName", parentTagName);
/*      */     }
/*      */     
/*  149 */     List tagSets = ifd.getTagSetList();
/*  150 */     if (tagSets.size() > 0) {
/*  151 */       Iterator<TIFFTagSet> iterator = tagSets.iterator();
/*  152 */       String tagSetNames = "";
/*  153 */       while (iterator.hasNext()) {
/*  154 */         TIFFTagSet tagSet = iterator.next();
/*  155 */         tagSetNames = tagSetNames + tagSet.getClass().getName();
/*  156 */         if (iterator.hasNext()) {
/*  157 */           tagSetNames = tagSetNames + ",";
/*      */         }
/*      */       } 
/*      */       
/*  161 */       IFDRoot.setAttribute("tagSets", tagSetNames);
/*      */     } 
/*      */     
/*  164 */     Iterator<TIFFField> iter = ifd.iterator();
/*  165 */     while (iter.hasNext()) {
/*  166 */       TIFFField f = iter.next();
/*  167 */       int tagNumber = f.getTagNumber();
/*  168 */       TIFFTag tag = TIFFIFD.getTag(tagNumber, tagSets);
/*      */       
/*  170 */       Node node = null;
/*  171 */       if (tag == null) {
/*  172 */         node = f.getAsNativeNode();
/*  173 */       } else if (tag.isIFDPointer()) {
/*  174 */         TIFFIFD subIFD = (TIFFIFD)f.getData();
/*      */ 
/*      */         
/*  177 */         node = getIFDAsTree(subIFD, tag.getName(), tag.getNumber());
/*      */       } else {
/*  179 */         node = f.getAsNativeNode();
/*      */       } 
/*      */       
/*  182 */       if (node != null) {
/*  183 */         IFDRoot.appendChild(node);
/*      */       }
/*      */     } 
/*      */     
/*  187 */     return IFDRoot;
/*      */   }
/*      */   
/*      */   public Node getAsTree(String formatName) {
/*  191 */     if (formatName.equals("com_sun_media_imageio_plugins_tiff_image_1.0"))
/*  192 */       return getNativeTree(); 
/*  193 */     if (formatName
/*  194 */       .equals("javax_imageio_1.0")) {
/*  195 */       return getStandardTree();
/*      */     }
/*  197 */     throw new IllegalArgumentException("Not a recognized format!");
/*      */   }
/*      */ 
/*      */   
/*      */   private Node getNativeTree() {
/*  202 */     IIOMetadataNode root = new IIOMetadataNode("com_sun_media_imageio_plugins_tiff_image_1.0");
/*      */     
/*  204 */     Node IFDNode = getIFDAsTree(this.rootIFD, null, 0);
/*  205 */     root.appendChild(IFDNode);
/*      */     
/*  207 */     return root;
/*      */   }
/*      */   
/*  210 */   private static final String[] colorSpaceNames = new String[] { "GRAY", "GRAY", "RGB", "RGB", "GRAY", "CMYK", "YCbCr", "Lab", "Lab" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public IIOMetadataNode getStandardChromaNode() {
/*  223 */     IIOMetadataNode chroma_node = new IIOMetadataNode("Chroma");
/*  224 */     IIOMetadataNode node = null;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  229 */     int photometricInterpretation = -1;
/*  230 */     boolean isPaletteColor = false;
/*  231 */     TIFFField f = getTIFFField(262);
/*  232 */     if (f != null) {
/*  233 */       photometricInterpretation = f.getAsInt(0);
/*      */       
/*  235 */       isPaletteColor = (photometricInterpretation == 3);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  241 */     int numChannels = -1;
/*  242 */     if (isPaletteColor) {
/*  243 */       numChannels = 3;
/*      */     } else {
/*  245 */       f = getTIFFField(277);
/*  246 */       if (f != null) {
/*  247 */         numChannels = f.getAsInt(0);
/*      */       } else {
/*  249 */         f = getTIFFField(258);
/*  250 */         if (f != null) {
/*  251 */           numChannels = f.getCount();
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/*  256 */     if (photometricInterpretation != -1) {
/*  257 */       if (photometricInterpretation >= 0 && photometricInterpretation < colorSpaceNames.length) {
/*      */         String csName;
/*  259 */         node = new IIOMetadataNode("ColorSpaceType");
/*      */         
/*  261 */         if (photometricInterpretation == 5 && numChannels == 3) {
/*      */ 
/*      */           
/*  264 */           csName = "CMY";
/*      */         } else {
/*  266 */           csName = colorSpaceNames[photometricInterpretation];
/*      */         } 
/*  268 */         node.setAttribute("name", csName);
/*  269 */         chroma_node.appendChild(node);
/*      */       } 
/*      */       
/*  272 */       node = new IIOMetadataNode("BlackIsZero");
/*  273 */       node.setAttribute("value", (photometricInterpretation == 0) ? "FALSE" : "TRUE");
/*      */ 
/*      */ 
/*      */       
/*  277 */       chroma_node.appendChild(node);
/*      */     } 
/*      */     
/*  280 */     if (numChannels != -1) {
/*  281 */       node = new IIOMetadataNode("NumChannels");
/*  282 */       node.setAttribute("value", Integer.toString(numChannels));
/*  283 */       chroma_node.appendChild(node);
/*      */     } 
/*      */     
/*  286 */     f = getTIFFField(320);
/*  287 */     if (f != null) {
/*      */ 
/*      */ 
/*      */       
/*  291 */       boolean hasAlpha = false;
/*      */       
/*  293 */       node = new IIOMetadataNode("Palette");
/*  294 */       int len = f.getCount() / (hasAlpha ? 4 : 3);
/*  295 */       for (int i = 0; i < len; i++) {
/*  296 */         IIOMetadataNode entry = new IIOMetadataNode("PaletteEntry");
/*      */         
/*  298 */         entry.setAttribute("index", Integer.toString(i));
/*      */         
/*  300 */         int r = f.getAsInt(i) * 255 / 65535;
/*  301 */         int g = f.getAsInt(len + i) * 255 / 65535;
/*  302 */         int b = f.getAsInt(2 * len + i) * 255 / 65535;
/*      */         
/*  304 */         entry.setAttribute("red", Integer.toString(r));
/*  305 */         entry.setAttribute("green", Integer.toString(g));
/*  306 */         entry.setAttribute("blue", Integer.toString(b));
/*  307 */         if (hasAlpha) {
/*  308 */           int alpha = 0;
/*  309 */           entry.setAttribute("alpha", Integer.toString(alpha));
/*      */         } 
/*  311 */         node.appendChild(entry);
/*      */       } 
/*  313 */       chroma_node.appendChild(node);
/*      */     } 
/*      */     
/*  316 */     return chroma_node;
/*      */   }
/*      */   
/*      */   public IIOMetadataNode getStandardCompressionNode() {
/*  320 */     IIOMetadataNode compression_node = new IIOMetadataNode("Compression");
/*  321 */     IIOMetadataNode node = null;
/*      */ 
/*      */ 
/*      */     
/*  325 */     TIFFField f = getTIFFField(259);
/*  326 */     if (f != null) {
/*  327 */       String compressionTypeName = null;
/*  328 */       int compression = f.getAsInt(0);
/*  329 */       boolean isLossless = true;
/*  330 */       if (compression == 1) {
/*  331 */         compressionTypeName = "None";
/*  332 */         isLossless = true;
/*      */       } else {
/*  334 */         int[] compressionNumbers = TIFFImageWriter.compressionNumbers;
/*  335 */         for (int i = 0; i < compressionNumbers.length; i++) {
/*  336 */           if (compression == compressionNumbers[i]) {
/*  337 */             compressionTypeName = TIFFImageWriter.compressionTypes[i];
/*      */             
/*  339 */             isLossless = TIFFImageWriter.isCompressionLossless[i];
/*      */             
/*      */             break;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/*  346 */       if (compressionTypeName != null) {
/*  347 */         node = new IIOMetadataNode("CompressionTypeName");
/*  348 */         node.setAttribute("value", compressionTypeName);
/*  349 */         compression_node.appendChild(node);
/*      */         
/*  351 */         node = new IIOMetadataNode("Lossless");
/*  352 */         node.setAttribute("value", isLossless ? "TRUE" : "FALSE");
/*  353 */         compression_node.appendChild(node);
/*      */       } 
/*      */     } 
/*      */     
/*  357 */     node = new IIOMetadataNode("NumProgressiveScans");
/*  358 */     node.setAttribute("value", "1");
/*  359 */     compression_node.appendChild(node);
/*      */     
/*  361 */     return compression_node;
/*      */   }
/*      */   
/*      */   private String repeat(String s, int times) {
/*  365 */     if (times == 1) {
/*  366 */       return s;
/*      */     }
/*  368 */     StringBuffer sb = new StringBuffer((s.length() + 1) * times - 1);
/*  369 */     sb.append(s);
/*  370 */     for (int i = 1; i < times; i++) {
/*  371 */       sb.append(" ");
/*  372 */       sb.append(s);
/*      */     } 
/*  374 */     return sb.toString();
/*      */   }
/*      */   
/*      */   public IIOMetadataNode getStandardDataNode() {
/*  378 */     IIOMetadataNode data_node = new IIOMetadataNode("Data");
/*  379 */     IIOMetadataNode node = null;
/*      */ 
/*      */ 
/*      */     
/*  383 */     boolean isPaletteColor = false;
/*  384 */     TIFFField f = getTIFFField(262);
/*  385 */     if (f != null)
/*      */     {
/*  387 */       isPaletteColor = (f.getAsInt(0) == 3);
/*      */     }
/*      */ 
/*      */     
/*  391 */     f = getTIFFField(284);
/*  392 */     String planarConfiguration = "PixelInterleaved";
/*  393 */     if (f != null && f
/*  394 */       .getAsInt(0) == 2) {
/*  395 */       planarConfiguration = "PlaneInterleaved";
/*      */     }
/*      */     
/*  398 */     node = new IIOMetadataNode("PlanarConfiguration");
/*  399 */     node.setAttribute("value", planarConfiguration);
/*  400 */     data_node.appendChild(node);
/*      */     
/*  402 */     f = getTIFFField(262);
/*  403 */     if (f != null) {
/*  404 */       int photometricInterpretation = f.getAsInt(0);
/*  405 */       String sampleFormat = "UnsignedIntegral";
/*      */       
/*  407 */       if (photometricInterpretation == 3) {
/*      */         
/*  409 */         sampleFormat = "Index";
/*      */       } else {
/*  411 */         f = getTIFFField(339);
/*  412 */         if (f != null) {
/*  413 */           int format = f.getAsInt(0);
/*  414 */           if (format == 2) {
/*      */             
/*  416 */             sampleFormat = "SignedIntegral";
/*  417 */           } else if (format == 1) {
/*      */             
/*  419 */             sampleFormat = "UnsignedIntegral";
/*  420 */           } else if (format == 3) {
/*      */             
/*  422 */             sampleFormat = "Real";
/*      */           } else {
/*  424 */             sampleFormat = null;
/*      */           } 
/*      */         } 
/*      */       } 
/*  428 */       if (sampleFormat != null) {
/*  429 */         node = new IIOMetadataNode("SampleFormat");
/*  430 */         node.setAttribute("value", sampleFormat);
/*  431 */         data_node.appendChild(node);
/*      */       } 
/*      */     } 
/*      */     
/*  435 */     f = getTIFFField(258);
/*  436 */     int[] bitsPerSample = null;
/*  437 */     if (f != null) {
/*  438 */       bitsPerSample = f.getAsInts();
/*      */     } else {
/*  440 */       f = getTIFFField(259);
/*      */       
/*  442 */       int compression = (f != null) ? f.getAsInt(0) : 1;
/*  443 */       if (getTIFFField(34665) != null || compression == 7 || compression == 6 || 
/*      */ 
/*      */ 
/*      */         
/*  447 */         getTIFFField(513) != null) {
/*      */         
/*  449 */         f = getTIFFField(262);
/*  450 */         if (f != null && (f
/*  451 */           .getAsInt(0) == 0 || f
/*      */           
/*  453 */           .getAsInt(0) == 1)) {
/*      */           
/*  455 */           bitsPerSample = new int[] { 8 };
/*      */         } else {
/*  457 */           bitsPerSample = new int[] { 8, 8, 8 };
/*      */         } 
/*      */       } else {
/*  460 */         bitsPerSample = new int[] { 1 };
/*      */       } 
/*      */     } 
/*  463 */     StringBuffer sb = new StringBuffer();
/*  464 */     for (int i = 0; i < bitsPerSample.length; i++) {
/*  465 */       if (i > 0) {
/*  466 */         sb.append(" ");
/*      */       }
/*  468 */       sb.append(Integer.toString(bitsPerSample[i]));
/*      */     } 
/*  470 */     node = new IIOMetadataNode("BitsPerSample");
/*  471 */     if (isPaletteColor) {
/*  472 */       node.setAttribute("value", repeat(sb.toString(), 3));
/*      */     } else {
/*  474 */       node.setAttribute("value", sb.toString());
/*      */     } 
/*  476 */     data_node.appendChild(node);
/*      */ 
/*      */     
/*  479 */     f = getTIFFField(266);
/*      */     
/*  481 */     int fillOrder = (f != null) ? f.getAsInt(0) : 1;
/*  482 */     sb = new StringBuffer();
/*  483 */     for (int j = 0; j < bitsPerSample.length; j++) {
/*  484 */       if (j > 0) {
/*  485 */         sb.append(" ");
/*      */       }
/*  487 */       int maxBitIndex = (bitsPerSample[j] == 1) ? 7 : (bitsPerSample[j] - 1);
/*      */       
/*  489 */       int msb = (fillOrder == 1) ? maxBitIndex : 0;
/*      */ 
/*      */       
/*  492 */       sb.append(Integer.toString(msb));
/*      */     } 
/*  494 */     node = new IIOMetadataNode("SampleMSB");
/*  495 */     if (isPaletteColor) {
/*  496 */       node.setAttribute("value", repeat(sb.toString(), 3));
/*      */     } else {
/*  498 */       node.setAttribute("value", sb.toString());
/*      */     } 
/*  500 */     data_node.appendChild(node);
/*      */     
/*  502 */     return data_node;
/*      */   }
/*      */   
/*  505 */   private static final String[] orientationNames = new String[] { null, "Normal", "FlipH", "Rotate180", "FlipV", "FlipHRotate90", "Rotate270", "FlipVRotate90", "Rotate90" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public IIOMetadataNode getStandardDimensionNode() {
/*  518 */     IIOMetadataNode dimension_node = new IIOMetadataNode("Dimension");
/*  519 */     IIOMetadataNode node = null;
/*      */ 
/*      */ 
/*      */     
/*  523 */     long[] xres = null;
/*  524 */     long[] yres = null;
/*      */     
/*  526 */     TIFFField f = getTIFFField(282);
/*  527 */     if (f != null) {
/*  528 */       xres = (long[])f.getAsRational(0).clone();
/*      */     }
/*      */     
/*  531 */     f = getTIFFField(283);
/*  532 */     if (f != null) {
/*  533 */       yres = (long[])f.getAsRational(0).clone();
/*      */     }
/*      */     
/*  536 */     if (xres != null && yres != null) {
/*  537 */       node = new IIOMetadataNode("PixelAspectRatio");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  543 */       float ratio = (float)(xres[1] * yres[0]) / (float)(xres[0] * yres[1]);
/*  544 */       node.setAttribute("value", Float.toString(ratio));
/*  545 */       dimension_node.appendChild(node);
/*      */     } 
/*      */     
/*  548 */     if (xres != null || yres != null) {
/*      */       
/*  550 */       f = getTIFFField(296);
/*      */ 
/*      */ 
/*      */       
/*  554 */       int i = (f != null) ? f.getAsInt(0) : 2;
/*      */ 
/*      */       
/*  557 */       boolean gotPixelSize = (i != 1);
/*      */ 
/*      */ 
/*      */       
/*  561 */       if (i == 2) {
/*      */         
/*  563 */         if (xres != null) {
/*  564 */           xres[0] = xres[0] * 100L;
/*  565 */           xres[1] = xres[1] * 254L;
/*      */         } 
/*      */ 
/*      */         
/*  569 */         if (yres != null) {
/*  570 */           yres[0] = yres[0] * 100L;
/*  571 */           yres[1] = yres[1] * 254L;
/*      */         } 
/*      */       } 
/*      */       
/*  575 */       if (gotPixelSize) {
/*  576 */         if (xres != null) {
/*  577 */           float horizontalPixelSize = (float)(10.0D * xres[1] / xres[0]);
/*  578 */           node = new IIOMetadataNode("HorizontalPixelSize");
/*  579 */           node.setAttribute("value", 
/*  580 */               Float.toString(horizontalPixelSize));
/*  581 */           dimension_node.appendChild(node);
/*      */         } 
/*      */         
/*  584 */         if (yres != null) {
/*  585 */           float verticalPixelSize = (float)(10.0D * yres[1] / yres[0]);
/*  586 */           node = new IIOMetadataNode("VerticalPixelSize");
/*  587 */           node.setAttribute("value", 
/*  588 */               Float.toString(verticalPixelSize));
/*  589 */           dimension_node.appendChild(node);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  594 */     f = getTIFFField(296);
/*      */     
/*  596 */     int resolutionUnit = (f != null) ? f.getAsInt(0) : 2;
/*  597 */     if (resolutionUnit == 2 || resolutionUnit == 3) {
/*      */       
/*  599 */       f = getTIFFField(286);
/*  600 */       if (f != null) {
/*  601 */         long[] xpos = f.getAsRational(0);
/*  602 */         float xPosition = (float)xpos[0] / (float)xpos[1];
/*      */         
/*  604 */         if (resolutionUnit == 2) {
/*  605 */           xPosition *= 254.0F;
/*      */         } else {
/*  607 */           xPosition *= 10.0F;
/*      */         } 
/*  609 */         node = new IIOMetadataNode("HorizontalPosition");
/*  610 */         node.setAttribute("value", 
/*  611 */             Float.toString(xPosition));
/*  612 */         dimension_node.appendChild(node);
/*      */       } 
/*      */       
/*  615 */       f = getTIFFField(287);
/*  616 */       if (f != null) {
/*  617 */         long[] ypos = f.getAsRational(0);
/*  618 */         float yPosition = (float)ypos[0] / (float)ypos[1];
/*      */         
/*  620 */         if (resolutionUnit == 2) {
/*  621 */           yPosition *= 254.0F;
/*      */         } else {
/*  623 */           yPosition *= 10.0F;
/*      */         } 
/*  625 */         node = new IIOMetadataNode("VerticalPosition");
/*  626 */         node.setAttribute("value", 
/*  627 */             Float.toString(yPosition));
/*  628 */         dimension_node.appendChild(node);
/*      */       } 
/*      */     } 
/*      */     
/*  632 */     f = getTIFFField(274);
/*  633 */     if (f != null) {
/*  634 */       int o = f.getAsInt(0);
/*  635 */       if (o >= 0 && o < orientationNames.length) {
/*  636 */         node = new IIOMetadataNode("ImageOrientation");
/*  637 */         node.setAttribute("value", orientationNames[o]);
/*  638 */         dimension_node.appendChild(node);
/*      */       } 
/*      */     } 
/*      */     
/*  642 */     return dimension_node;
/*      */   }
/*      */   
/*      */   public IIOMetadataNode getStandardDocumentNode() {
/*  646 */     IIOMetadataNode document_node = new IIOMetadataNode("Document");
/*  647 */     IIOMetadataNode node = null;
/*      */ 
/*      */ 
/*      */     
/*  651 */     node = new IIOMetadataNode("FormatVersion");
/*  652 */     node.setAttribute("value", "6.0");
/*  653 */     document_node.appendChild(node);
/*      */     
/*  655 */     TIFFField f = getTIFFField(254);
/*  656 */     if (f != null) {
/*  657 */       int newSubFileType = f.getAsInt(0);
/*  658 */       String value = null;
/*  659 */       if ((newSubFileType & 0x4) != 0) {
/*      */         
/*  661 */         value = "TransparencyMask";
/*  662 */       } else if ((newSubFileType & 0x1) != 0) {
/*      */         
/*  664 */         value = "ReducedResolution";
/*  665 */       } else if ((newSubFileType & 0x2) != 0) {
/*      */         
/*  667 */         value = "SinglePage";
/*      */       } 
/*  669 */       if (value != null) {
/*  670 */         node = new IIOMetadataNode("SubimageInterpretation");
/*  671 */         node.setAttribute("value", value);
/*  672 */         document_node.appendChild(node);
/*      */       } 
/*      */     } 
/*      */     
/*  676 */     f = getTIFFField(306);
/*  677 */     if (f != null) {
/*  678 */       String s = f.getAsString(0);
/*      */ 
/*      */       
/*  681 */       if (s.length() == 19) {
/*  682 */         boolean appendNode; node = new IIOMetadataNode("ImageCreationTime");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         try {
/*  690 */           node.setAttribute("year", s.substring(0, 4));
/*  691 */           node.setAttribute("month", s.substring(5, 7));
/*  692 */           node.setAttribute("day", s.substring(8, 10));
/*  693 */           node.setAttribute("hour", s.substring(11, 13));
/*  694 */           node.setAttribute("minute", s.substring(14, 16));
/*  695 */           node.setAttribute("second", s.substring(17, 19));
/*  696 */           appendNode = true;
/*  697 */         } catch (IndexOutOfBoundsException e) {
/*  698 */           appendNode = false;
/*      */         } 
/*      */         
/*  701 */         if (appendNode) {
/*  702 */           document_node.appendChild(node);
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/*  707 */     return document_node;
/*      */   }
/*      */   
/*      */   public IIOMetadataNode getStandardTextNode() {
/*  711 */     IIOMetadataNode text_node = null;
/*  712 */     IIOMetadataNode node = null;
/*      */ 
/*      */ 
/*      */     
/*  716 */     int[] textFieldTagNumbers = { 269, 270, 271, 272, 285, 305, 315, 316, 333, 33432 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  729 */     for (int i = 0; i < textFieldTagNumbers.length; i++) {
/*  730 */       TIFFField f = getTIFFField(textFieldTagNumbers[i]);
/*  731 */       if (f != null) {
/*  732 */         String value = f.getAsString(0);
/*  733 */         if (text_node == null) {
/*  734 */           text_node = new IIOMetadataNode("Text");
/*      */         }
/*  736 */         node = new IIOMetadataNode("TextEntry");
/*  737 */         node.setAttribute("keyword", f.getTag().getName());
/*  738 */         node.setAttribute("value", value);
/*  739 */         text_node.appendChild(node);
/*      */       } 
/*      */     } 
/*      */     
/*  743 */     return text_node;
/*      */   }
/*      */   
/*      */   public IIOMetadataNode getStandardTransparencyNode() {
/*  747 */     IIOMetadataNode transparency_node = new IIOMetadataNode("Transparency");
/*      */     
/*  749 */     IIOMetadataNode node = null;
/*      */ 
/*      */ 
/*      */     
/*  753 */     node = new IIOMetadataNode("Alpha");
/*  754 */     String value = "none";
/*      */     
/*  756 */     TIFFField f = getTIFFField(338);
/*  757 */     if (f != null) {
/*  758 */       int[] extraSamples = f.getAsInts();
/*  759 */       for (int i = 0; i < extraSamples.length; i++) {
/*  760 */         if (extraSamples[i] == 1) {
/*      */           
/*  762 */           value = "premultiplied"; break;
/*      */         } 
/*  764 */         if (extraSamples[i] == 2) {
/*      */           
/*  766 */           value = "nonpremultiplied";
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     } 
/*  772 */     node.setAttribute("value", value);
/*  773 */     transparency_node.appendChild(node);
/*      */     
/*  775 */     return transparency_node;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static void fatal(Node node, String reason) throws IIOInvalidTreeException {
/*  781 */     throw new IIOInvalidTreeException(reason, node);
/*      */   }
/*      */   
/*      */   private int[] listToIntArray(String list) {
/*  785 */     StringTokenizer st = new StringTokenizer(list, " ");
/*  786 */     ArrayList<Integer> intList = new ArrayList();
/*  787 */     while (st.hasMoreTokens()) {
/*  788 */       String nextInteger = st.nextToken();
/*  789 */       Integer nextInt = new Integer(nextInteger);
/*  790 */       intList.add(nextInt);
/*      */     } 
/*      */     
/*  793 */     int[] intArray = new int[intList.size()];
/*  794 */     for (int i = 0; i < intArray.length; i++) {
/*  795 */       intArray[i] = ((Integer)intList.get(i)).intValue();
/*      */     }
/*      */     
/*  798 */     return intArray;
/*      */   }
/*      */   
/*      */   private char[] listToCharArray(String list) {
/*  802 */     StringTokenizer st = new StringTokenizer(list, " ");
/*  803 */     ArrayList<Integer> intList = new ArrayList();
/*  804 */     while (st.hasMoreTokens()) {
/*  805 */       String nextInteger = st.nextToken();
/*  806 */       Integer nextInt = new Integer(nextInteger);
/*  807 */       intList.add(nextInt);
/*      */     } 
/*      */     
/*  810 */     char[] charArray = new char[intList.size()];
/*  811 */     for (int i = 0; i < charArray.length; i++) {
/*  812 */       charArray[i] = (char)((Integer)intList.get(i)).intValue();
/*      */     }
/*      */     
/*  815 */     return charArray;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void mergeStandardTree(Node root) throws IIOInvalidTreeException {
/*  823 */     Node node = root;
/*      */     
/*  825 */     if (!node.getNodeName().equals("javax_imageio_1.0")) {
/*  826 */       fatal(node, "Root must be javax_imageio_1.0");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  831 */     String sampleFormat = null;
/*  832 */     Node dataNode = getChildNode(root, "Data");
/*  833 */     boolean isPaletteColor = false;
/*  834 */     if (dataNode != null) {
/*  835 */       Node sampleFormatNode = getChildNode(dataNode, "SampleFormat");
/*  836 */       if (sampleFormatNode != null) {
/*  837 */         sampleFormat = getAttribute(sampleFormatNode, "value");
/*  838 */         isPaletteColor = sampleFormat.equals("Index");
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  843 */     if (!isPaletteColor) {
/*  844 */       Node chromaNode = getChildNode(root, "Chroma");
/*  845 */       if (chromaNode != null && 
/*  846 */         getChildNode(chromaNode, "Palette") != null) {
/*  847 */         isPaletteColor = true;
/*      */       }
/*      */     } 
/*      */     
/*  851 */     node = node.getFirstChild();
/*  852 */     while (node != null) {
/*  853 */       String name = node.getNodeName();
/*      */       
/*  855 */       if (name.equals("Chroma")) {
/*  856 */         String colorSpaceType = null;
/*  857 */         String blackIsZero = null;
/*  858 */         boolean gotPalette = false;
/*  859 */         Node child = node.getFirstChild();
/*  860 */         while (child != null) {
/*  861 */           String childName = child.getNodeName();
/*  862 */           if (childName.equals("ColorSpaceType")) {
/*  863 */             colorSpaceType = getAttribute(child, "name");
/*  864 */           } else if (childName.equals("NumChannels")) {
/*  865 */             TIFFTag tag = this.rootIFD.getTag(277);
/*      */             
/*  867 */             int samplesPerPixel = isPaletteColor ? 1 : Integer.parseInt(getAttribute(child, "value"));
/*  868 */             TIFFField f = new TIFFField(tag, samplesPerPixel);
/*  869 */             this.rootIFD.addTIFFField(f);
/*  870 */           } else if (childName.equals("BlackIsZero")) {
/*  871 */             blackIsZero = getAttribute(child, "value");
/*  872 */           } else if (childName.equals("Palette")) {
/*  873 */             Node entry = child.getFirstChild();
/*  874 */             HashMap<Object, Object> palette = new HashMap<Object, Object>();
/*  875 */             int maxIndex = -1;
/*  876 */             while (entry != null) {
/*  877 */               String entryName = entry.getNodeName();
/*  878 */               if (entryName.equals("PaletteEntry")) {
/*  879 */                 String idx = getAttribute(entry, "index");
/*  880 */                 int id = Integer.parseInt(idx);
/*  881 */                 if (id > maxIndex) {
/*  882 */                   maxIndex = id;
/*      */                 }
/*      */                 
/*  885 */                 char red = (char)Integer.parseInt(getAttribute(entry, "red"));
/*      */ 
/*      */                 
/*  888 */                 char green = (char)Integer.parseInt(getAttribute(entry, "green"));
/*      */ 
/*      */                 
/*  891 */                 char blue = (char)Integer.parseInt(getAttribute(entry, "blue"));
/*      */                 
/*  893 */                 palette.put(new Integer(id), new char[] { red, green, blue });
/*      */ 
/*      */                 
/*  896 */                 gotPalette = true;
/*      */               } 
/*  898 */               entry = entry.getNextSibling();
/*      */             } 
/*      */             
/*  901 */             if (gotPalette) {
/*  902 */               int mapSize = maxIndex + 1;
/*  903 */               int paletteLength = 3 * mapSize;
/*  904 */               char[] paletteEntries = new char[paletteLength];
/*  905 */               Iterator<Integer> paletteIter = palette.keySet().iterator();
/*  906 */               while (paletteIter.hasNext()) {
/*  907 */                 Integer index = paletteIter.next();
/*  908 */                 char[] rgb = (char[])palette.get(index);
/*  909 */                 int idx = index.intValue();
/*  910 */                 paletteEntries[idx] = (char)(rgb[0] * 65535 / 255);
/*      */                 
/*  912 */                 paletteEntries[mapSize + idx] = (char)(rgb[1] * 65535 / 255);
/*      */                 
/*  914 */                 paletteEntries[2 * mapSize + idx] = (char)(rgb[2] * 65535 / 255);
/*      */               } 
/*      */ 
/*      */               
/*  918 */               TIFFTag tag = this.rootIFD.getTag(320);
/*  919 */               TIFFField f = new TIFFField(tag, 3, paletteLength, paletteEntries);
/*      */               
/*  921 */               this.rootIFD.addTIFFField(f);
/*      */             } 
/*      */           } 
/*      */           
/*  925 */           child = child.getNextSibling();
/*      */         } 
/*      */         
/*  928 */         int photometricInterpretation = -1;
/*  929 */         if ((colorSpaceType == null || colorSpaceType.equals("GRAY")) && blackIsZero != null && blackIsZero
/*      */           
/*  931 */           .equalsIgnoreCase("FALSE")) {
/*  932 */           photometricInterpretation = 0;
/*      */         }
/*  934 */         else if (colorSpaceType != null) {
/*  935 */           if (colorSpaceType.equals("GRAY")) {
/*  936 */             boolean isTransparency = false;
/*  937 */             if (root instanceof IIOMetadataNode) {
/*  938 */               IIOMetadataNode iioRoot = (IIOMetadataNode)root;
/*      */               
/*  940 */               NodeList siNodeList = iioRoot.getElementsByTagName("SubimageInterpretation");
/*  941 */               if (siNodeList.getLength() == 1) {
/*  942 */                 Node siNode = siNodeList.item(0);
/*  943 */                 String value = getAttribute(siNode, "value");
/*  944 */                 if (value.equals("TransparencyMask")) {
/*  945 */                   isTransparency = true;
/*      */                 }
/*      */               } 
/*      */             } 
/*  949 */             if (isTransparency) {
/*  950 */               photometricInterpretation = 4;
/*      */             } else {
/*      */               
/*  953 */               photometricInterpretation = 1;
/*      */             }
/*      */           
/*  956 */           } else if (colorSpaceType.equals("RGB")) {
/*  957 */             photometricInterpretation = gotPalette ? 3 : 2;
/*      */ 
/*      */           
/*      */           }
/*  961 */           else if (colorSpaceType.equals("YCbCr")) {
/*  962 */             photometricInterpretation = 6;
/*      */           }
/*  964 */           else if (colorSpaceType.equals("CMYK")) {
/*  965 */             photometricInterpretation = 5;
/*      */           }
/*  967 */           else if (colorSpaceType.equals("Lab")) {
/*  968 */             photometricInterpretation = 8;
/*      */           } 
/*      */         } 
/*      */ 
/*      */         
/*  973 */         if (photometricInterpretation != -1) {
/*  974 */           TIFFTag tag = this.rootIFD.getTag(262);
/*  975 */           TIFFField f = new TIFFField(tag, photometricInterpretation);
/*  976 */           this.rootIFD.addTIFFField(f);
/*      */         } 
/*  978 */       } else if (name.equals("Compression")) {
/*  979 */         Node child = node.getFirstChild();
/*  980 */         while (child != null) {
/*  981 */           String childName = child.getNodeName();
/*  982 */           if (childName.equals("CompressionTypeName")) {
/*  983 */             int compression = -1;
/*      */             
/*  985 */             String compressionTypeName = getAttribute(child, "value");
/*  986 */             if (compressionTypeName.equalsIgnoreCase("None")) {
/*  987 */               compression = 1;
/*      */             } else {
/*      */               
/*  990 */               String[] compressionNames = TIFFImageWriter.compressionTypes;
/*      */               
/*  992 */               for (int i = 0; i < compressionNames.length; i++) {
/*  993 */                 if (compressionNames[i].equalsIgnoreCase(compressionTypeName)) {
/*  994 */                   compression = TIFFImageWriter.compressionNumbers[i];
/*      */                   
/*      */                   break;
/*      */                 } 
/*      */               } 
/*      */             } 
/*      */             
/* 1001 */             if (compression != -1) {
/* 1002 */               TIFFTag tag = this.rootIFD.getTag(259);
/* 1003 */               TIFFField f = new TIFFField(tag, compression);
/* 1004 */               this.rootIFD.addTIFFField(f);
/*      */             } 
/*      */           } 
/*      */ 
/*      */ 
/*      */           
/* 1010 */           child = child.getNextSibling();
/*      */         } 
/* 1012 */       } else if (name.equals("Data")) {
/* 1013 */         Node child = node.getFirstChild();
/* 1014 */         while (child != null) {
/* 1015 */           String childName = child.getNodeName();
/*      */           
/* 1017 */           if (childName.equals("PlanarConfiguration")) {
/* 1018 */             String pc = getAttribute(child, "value");
/* 1019 */             int planarConfiguration = -1;
/* 1020 */             if (pc.equals("PixelInterleaved")) {
/* 1021 */               planarConfiguration = 1;
/*      */             }
/* 1023 */             else if (pc.equals("PlaneInterleaved")) {
/* 1024 */               planarConfiguration = 2;
/*      */             } 
/*      */             
/* 1027 */             if (planarConfiguration != -1) {
/* 1028 */               TIFFTag tag = this.rootIFD.getTag(284);
/* 1029 */               TIFFField f = new TIFFField(tag, planarConfiguration);
/* 1030 */               this.rootIFD.addTIFFField(f);
/*      */             } 
/* 1032 */           } else if (childName.equals("BitsPerSample")) {
/* 1033 */             TIFFField f; String bps = getAttribute(child, "value");
/* 1034 */             char[] bitsPerSample = listToCharArray(bps);
/* 1035 */             TIFFTag tag = this.rootIFD.getTag(258);
/* 1036 */             if (isPaletteColor) {
/* 1037 */               f = new TIFFField(tag, 3, 1, new char[] { bitsPerSample[0] });
/*      */             } else {
/*      */               
/* 1040 */               f = new TIFFField(tag, 3, bitsPerSample.length, bitsPerSample);
/*      */             } 
/*      */ 
/*      */             
/* 1044 */             this.rootIFD.addTIFFField(f);
/* 1045 */           } else if (childName.equals("SampleMSB")) {
/*      */ 
/*      */ 
/*      */             
/* 1049 */             String sMSB = getAttribute(child, "value");
/* 1050 */             int[] sampleMSB = listToIntArray(sMSB);
/* 1051 */             boolean isRightToLeft = true;
/* 1052 */             for (int i = 0; i < sampleMSB.length; i++) {
/* 1053 */               if (sampleMSB[i] != 0) {
/* 1054 */                 isRightToLeft = false;
/*      */                 break;
/*      */               } 
/*      */             } 
/* 1058 */             int fillOrder = isRightToLeft ? 2 : 1;
/*      */ 
/*      */ 
/*      */             
/* 1062 */             TIFFTag tag = this.rootIFD.getTag(266);
/* 1063 */             TIFFField f = new TIFFField(tag, fillOrder);
/* 1064 */             this.rootIFD.addTIFFField(f);
/*      */           } 
/*      */           
/* 1067 */           child = child.getNextSibling();
/*      */         } 
/* 1069 */       } else if (name.equals("Dimension")) {
/* 1070 */         float pixelAspectRatio = -1.0F;
/* 1071 */         boolean gotPixelAspectRatio = false;
/*      */         
/* 1073 */         float horizontalPixelSize = -1.0F;
/* 1074 */         boolean gotHorizontalPixelSize = false;
/*      */         
/* 1076 */         float verticalPixelSize = -1.0F;
/* 1077 */         boolean gotVerticalPixelSize = false;
/*      */         
/* 1079 */         boolean sizeIsAbsolute = false;
/*      */         
/* 1081 */         float horizontalPosition = -1.0F;
/* 1082 */         boolean gotHorizontalPosition = false;
/*      */         
/* 1084 */         float verticalPosition = -1.0F;
/* 1085 */         boolean gotVerticalPosition = false;
/*      */         
/* 1087 */         Node child = node.getFirstChild();
/* 1088 */         while (child != null) {
/* 1089 */           String childName = child.getNodeName();
/* 1090 */           if (childName.equals("PixelAspectRatio")) {
/* 1091 */             String par = getAttribute(child, "value");
/* 1092 */             pixelAspectRatio = Float.parseFloat(par);
/* 1093 */             gotPixelAspectRatio = true;
/* 1094 */           } else if (childName.equals("ImageOrientation")) {
/* 1095 */             String orientation = getAttribute(child, "value");
/* 1096 */             for (int i = 0; i < orientationNames.length; i++) {
/* 1097 */               if (orientation.equals(orientationNames[i])) {
/* 1098 */                 char[] oData = new char[1];
/* 1099 */                 oData[0] = (char)i;
/*      */ 
/*      */                 
/* 1102 */                 TIFFField tIFFField = new TIFFField(this.rootIFD.getTag(274), 3, 1, oData);
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/* 1107 */                 this.rootIFD.addTIFFField(tIFFField);
/*      */                 
/*      */                 break;
/*      */               } 
/*      */             } 
/* 1112 */           } else if (childName.equals("HorizontalPixelSize")) {
/* 1113 */             String hps = getAttribute(child, "value");
/* 1114 */             horizontalPixelSize = Float.parseFloat(hps);
/* 1115 */             gotHorizontalPixelSize = true;
/* 1116 */           } else if (childName.equals("VerticalPixelSize")) {
/* 1117 */             String vps = getAttribute(child, "value");
/* 1118 */             verticalPixelSize = Float.parseFloat(vps);
/* 1119 */             gotVerticalPixelSize = true;
/* 1120 */           } else if (childName.equals("HorizontalPosition")) {
/* 1121 */             String hp = getAttribute(child, "value");
/* 1122 */             horizontalPosition = Float.parseFloat(hp);
/* 1123 */             gotHorizontalPosition = true;
/* 1124 */           } else if (childName.equals("VerticalPosition")) {
/* 1125 */             String vp = getAttribute(child, "value");
/* 1126 */             verticalPosition = Float.parseFloat(vp);
/* 1127 */             gotVerticalPosition = true;
/*      */           } 
/*      */           
/* 1130 */           child = child.getNextSibling();
/*      */         } 
/*      */         
/* 1133 */         sizeIsAbsolute = (gotHorizontalPixelSize || gotVerticalPixelSize);
/*      */ 
/*      */ 
/*      */         
/* 1137 */         if (gotPixelAspectRatio) {
/* 1138 */           if (gotHorizontalPixelSize && !gotVerticalPixelSize) {
/* 1139 */             verticalPixelSize = horizontalPixelSize / pixelAspectRatio;
/*      */             
/* 1141 */             gotVerticalPixelSize = true;
/* 1142 */           } else if (gotVerticalPixelSize && !gotHorizontalPixelSize) {
/*      */             
/* 1144 */             horizontalPixelSize = verticalPixelSize * pixelAspectRatio;
/*      */             
/* 1146 */             gotHorizontalPixelSize = true;
/* 1147 */           } else if (!gotHorizontalPixelSize && !gotVerticalPixelSize) {
/*      */             
/* 1149 */             horizontalPixelSize = pixelAspectRatio;
/* 1150 */             verticalPixelSize = 1.0F;
/* 1151 */             gotHorizontalPixelSize = true;
/* 1152 */             gotVerticalPixelSize = true;
/*      */           } 
/*      */         }
/*      */ 
/*      */         
/* 1157 */         if (gotHorizontalPixelSize) {
/* 1158 */           float xResolution = (sizeIsAbsolute ? 10.0F : 1.0F) / horizontalPixelSize;
/*      */           
/* 1160 */           long[][] hData = new long[1][2];
/* 1161 */           hData[0] = new long[2];
/* 1162 */           hData[0][0] = (long)(xResolution * 10000.0F);
/* 1163 */           hData[0][1] = 10000L;
/*      */ 
/*      */           
/* 1166 */           TIFFField tIFFField = new TIFFField(this.rootIFD.getTag(282), 5, 1, hData);
/*      */ 
/*      */ 
/*      */           
/* 1170 */           this.rootIFD.addTIFFField(tIFFField);
/*      */         } 
/*      */         
/* 1173 */         if (gotVerticalPixelSize) {
/* 1174 */           float yResolution = (sizeIsAbsolute ? 10.0F : 1.0F) / verticalPixelSize;
/*      */           
/* 1176 */           long[][] vData = new long[1][2];
/* 1177 */           vData[0] = new long[2];
/* 1178 */           vData[0][0] = (long)(yResolution * 10000.0F);
/* 1179 */           vData[0][1] = 10000L;
/*      */ 
/*      */           
/* 1182 */           TIFFField tIFFField = new TIFFField(this.rootIFD.getTag(283), 5, 1, vData);
/*      */ 
/*      */ 
/*      */           
/* 1186 */           this.rootIFD.addTIFFField(tIFFField);
/*      */         } 
/*      */ 
/*      */         
/* 1190 */         char[] res = new char[1];
/* 1191 */         res[0] = (char)(sizeIsAbsolute ? '\003' : '\001');
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1196 */         TIFFField f = new TIFFField(this.rootIFD.getTag(296), 3, 1, res);
/*      */ 
/*      */ 
/*      */         
/* 1200 */         this.rootIFD.addTIFFField(f);
/*      */ 
/*      */         
/* 1203 */         if (sizeIsAbsolute) {
/* 1204 */           if (gotHorizontalPosition) {
/*      */ 
/*      */             
/* 1207 */             long[][] hData = new long[1][2];
/* 1208 */             hData[0][0] = (long)(horizontalPosition * 10000.0F);
/* 1209 */             hData[0][1] = 100000L;
/*      */ 
/*      */             
/* 1212 */             f = new TIFFField(this.rootIFD.getTag(286), 5, 1, hData);
/*      */ 
/*      */ 
/*      */             
/* 1216 */             this.rootIFD.addTIFFField(f);
/*      */           } 
/*      */           
/* 1219 */           if (gotVerticalPosition) {
/*      */ 
/*      */             
/* 1222 */             long[][] vData = new long[1][2];
/* 1223 */             vData[0][0] = (long)(verticalPosition * 10000.0F);
/* 1224 */             vData[0][1] = 100000L;
/*      */ 
/*      */             
/* 1227 */             f = new TIFFField(this.rootIFD.getTag(287), 5, 1, vData);
/*      */ 
/*      */ 
/*      */             
/* 1231 */             this.rootIFD.addTIFFField(f);
/*      */           } 
/*      */         } 
/* 1234 */       } else if (name.equals("Document")) {
/* 1235 */         Node child = node.getFirstChild();
/* 1236 */         while (child != null) {
/* 1237 */           String childName = child.getNodeName();
/*      */           
/* 1239 */           if (childName.equals("SubimageInterpretation")) {
/* 1240 */             String si = getAttribute(child, "value");
/* 1241 */             int newSubFileType = -1;
/* 1242 */             if (si.equals("TransparencyMask")) {
/* 1243 */               newSubFileType = 4;
/*      */             }
/* 1245 */             else if (si.equals("ReducedResolution")) {
/* 1246 */               newSubFileType = 1;
/*      */             }
/* 1248 */             else if (si.equals("SinglePage")) {
/* 1249 */               newSubFileType = 2;
/*      */             } 
/*      */             
/* 1252 */             if (newSubFileType != -1) {
/*      */               
/* 1254 */               TIFFTag tag = this.rootIFD.getTag(254);
/* 1255 */               TIFFField f = new TIFFField(tag, newSubFileType);
/* 1256 */               this.rootIFD.addTIFFField(f);
/*      */             } 
/*      */           } 
/*      */           
/* 1260 */           if (childName.equals("ImageCreationTime")) {
/* 1261 */             String year = getAttribute(child, "year");
/* 1262 */             String month = getAttribute(child, "month");
/* 1263 */             String day = getAttribute(child, "day");
/* 1264 */             String hour = getAttribute(child, "hour");
/* 1265 */             String minute = getAttribute(child, "minute");
/* 1266 */             String second = getAttribute(child, "second");
/*      */             
/* 1268 */             StringBuffer sb = new StringBuffer();
/* 1269 */             sb.append(year);
/* 1270 */             sb.append(":");
/* 1271 */             if (month.length() == 1) {
/* 1272 */               sb.append("0");
/*      */             }
/* 1274 */             sb.append(month);
/* 1275 */             sb.append(":");
/* 1276 */             if (day.length() == 1) {
/* 1277 */               sb.append("0");
/*      */             }
/* 1279 */             sb.append(day);
/* 1280 */             sb.append(" ");
/* 1281 */             if (hour.length() == 1) {
/* 1282 */               sb.append("0");
/*      */             }
/* 1284 */             sb.append(hour);
/* 1285 */             sb.append(":");
/* 1286 */             if (minute.length() == 1) {
/* 1287 */               sb.append("0");
/*      */             }
/* 1289 */             sb.append(minute);
/* 1290 */             sb.append(":");
/* 1291 */             if (second.length() == 1) {
/* 1292 */               sb.append("0");
/*      */             }
/* 1294 */             sb.append(second);
/*      */             
/* 1296 */             String[] dt = new String[1];
/* 1297 */             dt[0] = sb.toString();
/*      */ 
/*      */             
/* 1300 */             TIFFField f = new TIFFField(this.rootIFD.getTag(306), 2, 1, dt);
/*      */ 
/*      */ 
/*      */             
/* 1304 */             this.rootIFD.addTIFFField(f);
/*      */           } 
/*      */           
/* 1307 */           child = child.getNextSibling();
/*      */         } 
/* 1309 */       } else if (name.equals("Text")) {
/* 1310 */         Node child = node.getFirstChild();
/* 1311 */         String theAuthor = null;
/* 1312 */         String theDescription = null;
/* 1313 */         String theTitle = null;
/* 1314 */         while (child != null) {
/* 1315 */           String childName = child.getNodeName();
/* 1316 */           if (childName.equals("TextEntry")) {
/* 1317 */             int tagNumber = -1;
/* 1318 */             NamedNodeMap childAttrs = child.getAttributes();
/* 1319 */             Node keywordNode = childAttrs.getNamedItem("keyword");
/* 1320 */             if (keywordNode != null) {
/* 1321 */               String keyword = keywordNode.getNodeValue();
/* 1322 */               String value = getAttribute(child, "value");
/* 1323 */               if (!keyword.equals("") && !value.equals("")) {
/* 1324 */                 if (keyword.equalsIgnoreCase("DocumentName")) {
/* 1325 */                   tagNumber = 269;
/*      */                 }
/* 1327 */                 else if (keyword.equalsIgnoreCase("ImageDescription")) {
/* 1328 */                   tagNumber = 270;
/*      */                 }
/* 1330 */                 else if (keyword.equalsIgnoreCase("Make")) {
/* 1331 */                   tagNumber = 271;
/*      */                 }
/* 1333 */                 else if (keyword.equalsIgnoreCase("Model")) {
/* 1334 */                   tagNumber = 272;
/*      */                 }
/* 1336 */                 else if (keyword.equalsIgnoreCase("PageName")) {
/* 1337 */                   tagNumber = 285;
/*      */                 }
/* 1339 */                 else if (keyword.equalsIgnoreCase("Software")) {
/* 1340 */                   tagNumber = 305;
/*      */                 }
/* 1342 */                 else if (keyword.equalsIgnoreCase("Artist")) {
/* 1343 */                   tagNumber = 315;
/*      */                 }
/* 1345 */                 else if (keyword.equalsIgnoreCase("HostComputer")) {
/* 1346 */                   tagNumber = 316;
/*      */                 }
/* 1348 */                 else if (keyword.equalsIgnoreCase("InkNames")) {
/* 1349 */                   tagNumber = 333;
/*      */                 }
/* 1351 */                 else if (keyword.equalsIgnoreCase("Copyright")) {
/* 1352 */                   tagNumber = 33432;
/*      */                 }
/* 1354 */                 else if (keyword.equalsIgnoreCase("author")) {
/* 1355 */                   theAuthor = value;
/* 1356 */                 } else if (keyword.equalsIgnoreCase("description")) {
/* 1357 */                   theDescription = value;
/* 1358 */                 } else if (keyword.equalsIgnoreCase("title")) {
/* 1359 */                   theTitle = value;
/*      */                 } 
/* 1361 */                 if (tagNumber != -1) {
/* 1362 */                   TIFFField f = new TIFFField(this.rootIFD.getTag(tagNumber), 2, 1, new String[] { value });
/*      */ 
/*      */ 
/*      */                   
/* 1366 */                   this.rootIFD.addTIFFField(f);
/*      */                 } 
/*      */               } 
/*      */             } 
/*      */           } 
/* 1371 */           child = child.getNextSibling();
/*      */         } 
/* 1373 */         if (theAuthor != null && 
/* 1374 */           getTIFFField(315) == null) {
/* 1375 */           TIFFField f = new TIFFField(this.rootIFD.getTag(315), 2, 1, new String[] { theAuthor });
/*      */ 
/*      */ 
/*      */           
/* 1379 */           this.rootIFD.addTIFFField(f);
/*      */         } 
/* 1381 */         if (theDescription != null && 
/* 1382 */           getTIFFField(270) == null) {
/* 1383 */           TIFFField f = new TIFFField(this.rootIFD.getTag(270), 2, 1, new String[] { theDescription });
/*      */ 
/*      */ 
/*      */           
/* 1387 */           this.rootIFD.addTIFFField(f);
/*      */         } 
/* 1389 */         if (theTitle != null && 
/* 1390 */           getTIFFField(269) == null) {
/* 1391 */           TIFFField f = new TIFFField(this.rootIFD.getTag(269), 2, 1, new String[] { theTitle });
/*      */ 
/*      */ 
/*      */           
/* 1395 */           this.rootIFD.addTIFFField(f);
/*      */         } 
/* 1397 */       } else if (name.equals("Transparency")) {
/* 1398 */         Node child = node.getFirstChild();
/* 1399 */         while (child != null) {
/* 1400 */           String childName = child.getNodeName();
/*      */           
/* 1402 */           if (childName.equals("Alpha")) {
/* 1403 */             String alpha = getAttribute(child, "value");
/*      */             
/* 1405 */             TIFFField f = null;
/* 1406 */             if (alpha.equals("premultiplied")) {
/*      */               
/* 1408 */               f = new TIFFField(this.rootIFD.getTag(338), 1);
/*      */             }
/* 1410 */             else if (alpha.equals("nonpremultiplied")) {
/*      */               
/* 1412 */               f = new TIFFField(this.rootIFD.getTag(338), 2);
/*      */             } 
/*      */             
/* 1415 */             if (f != null) {
/* 1416 */               this.rootIFD.addTIFFField(f);
/*      */             }
/*      */           } 
/*      */           
/* 1420 */           child = child.getNextSibling();
/*      */         } 
/*      */       } 
/*      */       
/* 1424 */       node = node.getNextSibling();
/*      */     } 
/*      */ 
/*      */     
/* 1428 */     if (sampleFormat != null) {
/*      */       
/* 1430 */       int sf = -1;
/* 1431 */       if (sampleFormat.equals("SignedIntegral")) {
/* 1432 */         sf = 2;
/* 1433 */       } else if (sampleFormat.equals("UnsignedIntegral")) {
/* 1434 */         sf = 1;
/* 1435 */       } else if (sampleFormat.equals("Real")) {
/* 1436 */         sf = 3;
/* 1437 */       } else if (sampleFormat.equals("Index")) {
/* 1438 */         sf = 1;
/*      */       } 
/*      */       
/* 1441 */       if (sf != -1) {
/*      */         
/* 1443 */         int count = 1;
/*      */ 
/*      */         
/* 1446 */         TIFFField f = getTIFFField(277);
/* 1447 */         if (f != null) {
/* 1448 */           count = f.getAsInt(0);
/*      */         } else {
/*      */           
/* 1451 */           f = getTIFFField(258);
/* 1452 */           if (f != null) {
/* 1453 */             count = f.getCount();
/*      */           }
/*      */         } 
/*      */         
/* 1457 */         char[] sampleFormatArray = new char[count];
/* 1458 */         Arrays.fill(sampleFormatArray, (char)sf);
/*      */ 
/*      */         
/* 1461 */         TIFFTag tag = this.rootIFD.getTag(339);
/* 1462 */         f = new TIFFField(tag, 3, sampleFormatArray.length, sampleFormatArray);
/*      */         
/* 1464 */         this.rootIFD.addTIFFField(f);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private static String getAttribute(Node node, String attrName) {
/* 1470 */     NamedNodeMap attrs = node.getAttributes();
/* 1471 */     Node attr = attrs.getNamedItem(attrName);
/* 1472 */     return (attr != null) ? attr.getNodeValue() : null;
/*      */   }
/*      */   
/*      */   private Node getChildNode(Node node, String childName) {
/* 1476 */     Node childNode = null;
/* 1477 */     if (node.hasChildNodes()) {
/* 1478 */       NodeList childNodes = node.getChildNodes();
/* 1479 */       int length = childNodes.getLength();
/* 1480 */       for (int i = 0; i < length; i++) {
/* 1481 */         Node item = childNodes.item(i);
/* 1482 */         if (item.getNodeName().equals(childName)) {
/* 1483 */           childNode = item;
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     } 
/* 1488 */     return childNode;
/*      */   }
/*      */   
/*      */   public static TIFFIFD parseIFD(Node node) throws IIOInvalidTreeException {
/* 1492 */     if (!node.getNodeName().equals("TIFFIFD")) {
/* 1493 */       fatal(node, "Expected \"TIFFIFD\" node");
/*      */     }
/*      */     
/* 1496 */     String tagSetNames = getAttribute(node, "tagSets");
/* 1497 */     List<TIFFTagSet> tagSets = new ArrayList(5);
/*      */     
/* 1499 */     if (tagSetNames != null) {
/* 1500 */       StringTokenizer st = new StringTokenizer(tagSetNames, ",");
/* 1501 */       while (st.hasMoreTokens()) {
/* 1502 */         String className = st.nextToken();
/*      */         
/* 1504 */         Object o = null;
/*      */         try {
/* 1506 */           Class<?> setClass = Class.forName(className);
/*      */           
/* 1508 */           Method getInstanceMethod = setClass.getMethod("getInstance", (Class[])null);
/* 1509 */           o = getInstanceMethod.invoke(null, (Object[])null);
/* 1510 */         } catch (NoSuchMethodException e) {
/* 1511 */           throw new RuntimeException(e);
/* 1512 */         } catch (IllegalAccessException e) {
/* 1513 */           throw new RuntimeException(e);
/* 1514 */         } catch (InvocationTargetException e) {
/* 1515 */           throw new RuntimeException(e);
/* 1516 */         } catch (ClassNotFoundException e) {
/* 1517 */           throw new RuntimeException(e);
/*      */         } 
/*      */         
/* 1520 */         if (!(o instanceof TIFFTagSet)) {
/* 1521 */           fatal(node, "Specified tag set class \"" + className + "\" is not an instance of TIFFTagSet");
/*      */           
/*      */           continue;
/*      */         } 
/* 1525 */         tagSets.add((TIFFTagSet)o);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1530 */     TIFFIFD ifd = new TIFFIFD(tagSets);
/*      */     
/* 1532 */     node = node.getFirstChild();
/* 1533 */     while (node != null) {
/* 1534 */       String name = node.getNodeName();
/*      */       
/* 1536 */       TIFFField f = null;
/* 1537 */       if (name.equals("TIFFIFD")) {
/* 1538 */         int type; TIFFIFD subIFD = parseIFD(node);
/* 1539 */         String parentTagName = getAttribute(node, "parentTagName");
/* 1540 */         String parentTagNumber = getAttribute(node, "parentTagNumber");
/* 1541 */         TIFFTag tag = null;
/* 1542 */         if (parentTagName != null) {
/* 1543 */           tag = TIFFIFD.getTag(parentTagName, tagSets);
/* 1544 */         } else if (parentTagNumber != null) {
/*      */           
/* 1546 */           int tagNumber = Integer.valueOf(parentTagNumber).intValue();
/* 1547 */           tag = TIFFIFD.getTag(tagNumber, tagSets);
/*      */         } 
/*      */         
/* 1550 */         if (tag == null) {
/* 1551 */           tag = new TIFFTag("unknown", 0, 0, null);
/*      */         }
/*      */ 
/*      */         
/* 1555 */         if (tag.isDataTypeOK(13)) {
/* 1556 */           type = 13;
/*      */         } else {
/* 1558 */           type = 4;
/*      */         } 
/*      */         
/* 1561 */         f = new TIFFField(tag, type, 1, subIFD);
/* 1562 */       } else if (name.equals("TIFFField")) {
/* 1563 */         int number = Integer.parseInt(getAttribute(node, "number"));
/*      */         
/* 1565 */         TIFFTagSet tagSet = null;
/* 1566 */         Iterator<TIFFTagSet> iter = tagSets.iterator();
/* 1567 */         while (iter.hasNext()) {
/* 1568 */           TIFFTagSet t = iter.next();
/* 1569 */           if (t.getTag(number) != null) {
/* 1570 */             tagSet = t;
/*      */             
/*      */             break;
/*      */           } 
/*      */         } 
/* 1575 */         f = TIFFField.createFromMetadataNode(tagSet, node);
/*      */       } else {
/* 1577 */         fatal(node, "Expected either \"TIFFIFD\" or \"TIFFField\" node, got " + name);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1582 */       ifd.addTIFFField(f);
/* 1583 */       node = node.getNextSibling();
/*      */     } 
/*      */     
/* 1586 */     return ifd;
/*      */   }
/*      */   
/*      */   private void mergeNativeTree(Node root) throws IIOInvalidTreeException {
/* 1590 */     Node node = root;
/* 1591 */     if (!node.getNodeName().equals("com_sun_media_imageio_plugins_tiff_image_1.0")) {
/* 1592 */       fatal(node, "Root must be com_sun_media_imageio_plugins_tiff_image_1.0");
/*      */     }
/*      */     
/* 1595 */     node = node.getFirstChild();
/* 1596 */     if (node == null || !node.getNodeName().equals("TIFFIFD")) {
/* 1597 */       fatal(root, "Root must have \"TIFFIFD\" child");
/*      */     }
/* 1599 */     TIFFIFD ifd = parseIFD(node);
/*      */     
/* 1601 */     List rootIFDTagSets = this.rootIFD.getTagSetList();
/* 1602 */     Iterator tagSetIter = ifd.getTagSetList().iterator();
/* 1603 */     while (tagSetIter.hasNext()) {
/* 1604 */       Object o = tagSetIter.next();
/* 1605 */       if (o instanceof TIFFTagSet && !rootIFDTagSets.contains(o)) {
/* 1606 */         this.rootIFD.addTagSet((TIFFTagSet)o);
/*      */       }
/*      */     } 
/*      */     
/* 1610 */     Iterator<TIFFField> ifdIter = ifd.iterator();
/* 1611 */     while (ifdIter.hasNext()) {
/* 1612 */       TIFFField field = ifdIter.next();
/* 1613 */       this.rootIFD.addTIFFField(field);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {
/* 1619 */     if (formatName.equals("com_sun_media_imageio_plugins_tiff_image_1.0")) {
/* 1620 */       if (root == null) {
/* 1621 */         throw new IllegalArgumentException("root == null!");
/*      */       }
/* 1623 */       mergeNativeTree(root);
/* 1624 */     } else if (formatName
/* 1625 */       .equals("javax_imageio_1.0")) {
/* 1626 */       if (root == null) {
/* 1627 */         throw new IllegalArgumentException("root == null!");
/*      */       }
/* 1629 */       mergeStandardTree(root);
/*      */     } else {
/* 1631 */       throw new IllegalArgumentException("Not a recognized format!");
/*      */     } 
/*      */   }
/*      */   
/*      */   public void reset() {
/* 1636 */     this.rootIFD = new TIFFIFD(this.tagSets);
/*      */   }
/*      */   
/*      */   public TIFFIFD getRootIFD() {
/* 1640 */     return this.rootIFD;
/*      */   }
/*      */   
/*      */   public TIFFField getTIFFField(int tagNumber) {
/* 1644 */     return this.rootIFD.getTIFFField(tagNumber);
/*      */   }
/*      */   
/*      */   public void removeTIFFField(int tagNumber) {
/* 1648 */     this.rootIFD.removeTIFFField(tagNumber);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TIFFImageMetadata getShallowClone() {
/* 1657 */     return new TIFFImageMetadata(this.rootIFD.getShallowClone());
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFImageMetadata.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */