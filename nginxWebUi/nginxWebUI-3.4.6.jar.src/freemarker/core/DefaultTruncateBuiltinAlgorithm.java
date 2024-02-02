/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.utility.ClassUtil;
/*     */ import freemarker.template.utility.NullArgumentException;
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
/*     */ public class DefaultTruncateBuiltinAlgorithm
/*     */   extends TruncateBuiltinAlgorithm
/*     */ {
/*     */   public static final String STANDARD_ASCII_TERMINATOR = "[...]";
/*     */   public static final String STANDARD_UNICODE_TERMINATOR = "[…]";
/*     */   public static final TemplateHTMLOutputModel STANDARD_M_TERMINATOR;
/*     */   public static final double DEFAULT_WORD_BOUNDARY_MIN_LENGTH = 0.75D;
/*     */   private static final int FALLBACK_M_TERMINATOR_LENGTH = 3;
/*     */   
/*     */   static {
/*     */     try {
/*  57 */       STANDARD_M_TERMINATOR = HTMLOutputFormat.INSTANCE.fromMarkup("<span class='truncateTerminator'>[&#8230;]</span>");
/*     */     }
/*  59 */     catch (TemplateModelException e) {
/*  60 */       throw new IllegalStateException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private enum TruncationMode
/*     */   {
/*  74 */     CHAR_BOUNDARY, WORD_BOUNDARY, AUTO;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  85 */   public static final DefaultTruncateBuiltinAlgorithm ASCII_INSTANCE = new DefaultTruncateBuiltinAlgorithm("[...]", STANDARD_M_TERMINATOR, true);
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
/*  98 */   public static final DefaultTruncateBuiltinAlgorithm UNICODE_INSTANCE = new DefaultTruncateBuiltinAlgorithm("[…]", STANDARD_M_TERMINATOR, true);
/*     */ 
/*     */   
/*     */   private final TemplateScalarModel defaultTerminator;
/*     */ 
/*     */   
/*     */   private final int defaultTerminatorLength;
/*     */ 
/*     */   
/*     */   private final boolean defaultTerminatorRemovesDots;
/*     */   
/*     */   private final TemplateMarkupOutputModel<?> defaultMTerminator;
/*     */   
/*     */   private final Integer defaultMTerminatorLength;
/*     */   
/*     */   private final boolean defaultMTerminatorRemovesDots;
/*     */   
/*     */   private final double wordBoundaryMinLength;
/*     */   
/*     */   private final boolean addSpaceAtWordBoundary;
/*     */ 
/*     */   
/*     */   public DefaultTruncateBuiltinAlgorithm(String defaultTerminator, TemplateMarkupOutputModel<?> defaultMTerminator, boolean addSpaceAtWordBoundary) {
/* 121 */     this(defaultTerminator, null, null, defaultMTerminator, null, null, addSpaceAtWordBoundary, null);
/*     */   }
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
/*     */   public DefaultTruncateBuiltinAlgorithm(String defaultTerminator, boolean addSpaceAtWordBoundary) {
/* 135 */     this(defaultTerminator, null, null, null, null, null, addSpaceAtWordBoundary, null);
/*     */   }
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
/*     */   public DefaultTruncateBuiltinAlgorithm(String defaultTerminator, Integer defaultTerminatorLength, Boolean defaultTerminatorRemovesDots, TemplateMarkupOutputModel<?> defaultMTerminator, Integer defaultMTerminatorLength, Boolean defaultMTerminatorRemovesDots, boolean addSpaceAtWordBoundary, Double wordBoundaryMinLength) {
/* 192 */     NullArgumentException.check("defaultTerminator", defaultTerminator);
/* 193 */     this.defaultTerminator = (TemplateScalarModel)new SimpleScalar(defaultTerminator);
/*     */     try {
/* 195 */       this
/* 196 */         .defaultTerminatorLength = (defaultTerminatorLength != null) ? defaultTerminatorLength.intValue() : defaultTerminator.length();
/*     */       
/* 198 */       this
/* 199 */         .defaultTerminatorRemovesDots = (defaultTerminatorRemovesDots != null) ? defaultTerminatorRemovesDots.booleanValue() : getTerminatorRemovesDots(defaultTerminator);
/* 200 */     } catch (TemplateModelException e) {
/* 201 */       throw new IllegalArgumentException("Failed to examine defaultTerminator", e);
/*     */     } 
/*     */     
/* 204 */     this.defaultMTerminator = defaultMTerminator;
/* 205 */     if (defaultMTerminator != null) {
/*     */       try {
/* 207 */         this.defaultMTerminatorLength = Integer.valueOf((defaultMTerminatorLength != null) ? defaultMTerminatorLength.intValue() : 
/* 208 */             getMTerminatorLength(defaultMTerminator));
/*     */         
/* 210 */         this
/*     */           
/* 212 */           .defaultMTerminatorRemovesDots = (defaultMTerminatorRemovesDots != null) ? defaultMTerminatorRemovesDots.booleanValue() : getMTerminatorRemovesDots(defaultMTerminator);
/* 213 */       } catch (TemplateModelException e) {
/* 214 */         throw new IllegalArgumentException("Failed to examine defaultMTerminator", e);
/*     */       } 
/*     */     } else {
/*     */       
/* 218 */       this.defaultMTerminatorLength = null;
/* 219 */       this.defaultMTerminatorRemovesDots = false;
/*     */     } 
/*     */     
/* 222 */     if (wordBoundaryMinLength == null) {
/* 223 */       wordBoundaryMinLength = Double.valueOf(0.75D);
/* 224 */     } else if (wordBoundaryMinLength.doubleValue() < 0.0D || wordBoundaryMinLength.doubleValue() > 1.0D) {
/* 225 */       throw new IllegalArgumentException("wordBoundaryMinLength must be between 0.0 and 1.0 (inclusive)");
/*     */     } 
/* 227 */     this.wordBoundaryMinLength = wordBoundaryMinLength.doubleValue();
/*     */     
/* 229 */     this.addSpaceAtWordBoundary = addSpaceAtWordBoundary;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateScalarModel truncate(String s, int maxLength, TemplateScalarModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
/* 237 */     return (TemplateScalarModel)unifiedTruncate(s, maxLength, (TemplateModel)terminator, terminatorLength, TruncationMode.AUTO, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateScalarModel truncateW(String s, int maxLength, TemplateScalarModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
/* 247 */     return (TemplateScalarModel)unifiedTruncate(s, maxLength, (TemplateModel)terminator, terminatorLength, TruncationMode.WORD_BOUNDARY, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateScalarModel truncateC(String s, int maxLength, TemplateScalarModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
/* 257 */     return (TemplateScalarModel)unifiedTruncate(s, maxLength, (TemplateModel)terminator, terminatorLength, TruncationMode.CHAR_BOUNDARY, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModel truncateM(String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
/* 267 */     return unifiedTruncate(s, maxLength, terminator, terminatorLength, TruncationMode.AUTO, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModel truncateWM(String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
/* 277 */     return unifiedTruncate(s, maxLength, terminator, terminatorLength, TruncationMode.WORD_BOUNDARY, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModel truncateCM(String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
/* 287 */     return unifiedTruncate(s, maxLength, terminator, terminatorLength, TruncationMode.CHAR_BOUNDARY, true);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDefaultTerminator() {
/*     */     try {
/* 294 */       return this.defaultTerminator.getAsString();
/* 295 */     } catch (TemplateModelException e) {
/* 296 */       throw new IllegalStateException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDefaultTerminatorLength() {
/* 305 */     return this.defaultTerminatorLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getDefaultTerminatorRemovesDots() {
/* 313 */     return this.defaultTerminatorRemovesDots;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateMarkupOutputModel<?> getDefaultMTerminator() {
/* 321 */     return this.defaultMTerminator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getDefaultMTerminatorLength() {
/* 329 */     return this.defaultMTerminatorLength;
/*     */   }
/*     */   
/*     */   public boolean getDefaultMTerminatorRemovesDots() {
/* 333 */     return this.defaultMTerminatorRemovesDots;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getWordBoundaryMinLength() {
/* 341 */     return this.wordBoundaryMinLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getAddSpaceAtWordBoundary() {
/* 349 */     return this.addSpaceAtWordBoundary;
/*     */   }
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
/*     */   protected int getMTerminatorLength(TemplateMarkupOutputModel<?> mTerminator) throws TemplateModelException {
/* 364 */     MarkupOutputFormat<?> format = mTerminator.getOutputFormat();
/* 365 */     return isHTMLOrXML(format) ? 
/* 366 */       getLengthWithoutTags(format.getMarkupString(mTerminator)) : 3;
/*     */   }
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
/*     */   protected boolean getTerminatorRemovesDots(String terminator) throws TemplateModelException {
/* 380 */     return (terminator.startsWith(".") || terminator.startsWith("…"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean getMTerminatorRemovesDots(TemplateMarkupOutputModel<?> terminator) throws TemplateModelException {
/* 392 */     return isHTMLOrXML(terminator.getOutputFormat()) ? 
/* 393 */       doesHtmlOrXmlStartWithDot(terminator.getOutputFormat().getMarkupString(terminator)) : true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TemplateModel unifiedTruncate(String s, int maxLength, TemplateModel<?> terminator, Integer terminatorLength, TruncationMode mode, boolean allowMarkupResult) throws TemplateException {
/*     */     TemplateScalarModel templateScalarModel;
/*     */     Boolean terminatorRemovesDots;
/* 405 */     if (s.length() <= maxLength) {
/* 406 */       return (TemplateModel)new SimpleScalar(s);
/*     */     }
/* 408 */     if (maxLength < 0) {
/* 409 */       throw new IllegalArgumentException("maxLength can't be negative");
/*     */     }
/*     */ 
/*     */     
/* 413 */     if (terminator == null) {
/* 414 */       if (allowMarkupResult && this.defaultMTerminator != null) {
/* 415 */         terminator = this.defaultMTerminator;
/* 416 */         terminatorLength = this.defaultMTerminatorLength;
/* 417 */         terminatorRemovesDots = Boolean.valueOf(this.defaultMTerminatorRemovesDots);
/*     */       } else {
/* 419 */         templateScalarModel = this.defaultTerminator;
/* 420 */         terminatorLength = Integer.valueOf(this.defaultTerminatorLength);
/* 421 */         terminatorRemovesDots = Boolean.valueOf(this.defaultTerminatorRemovesDots);
/*     */       } 
/*     */     } else {
/* 424 */       if (terminatorLength != null) {
/* 425 */         if (terminatorLength.intValue() < 0) {
/* 426 */           throw new IllegalArgumentException("terminatorLength can't be negative");
/*     */         }
/*     */       } else {
/* 429 */         terminatorLength = Integer.valueOf(getTerminatorLength((TemplateModel)templateScalarModel));
/*     */       } 
/* 431 */       terminatorRemovesDots = null;
/*     */     } 
/*     */     
/* 434 */     StringBuilder truncatedS = unifiedTruncateWithoutTerminatorAdded(s, maxLength, (TemplateModel)templateScalarModel, terminatorLength
/*     */ 
/*     */         
/* 437 */         .intValue(), terminatorRemovesDots, mode);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 442 */     if (truncatedS == null || truncatedS.length() == 0) {
/* 443 */       return (TemplateModel)templateScalarModel;
/*     */     }
/*     */     
/* 446 */     if (templateScalarModel instanceof TemplateScalarModel) {
/* 447 */       truncatedS.append(templateScalarModel.getAsString());
/* 448 */       return (TemplateModel)new SimpleScalar(truncatedS.toString());
/* 449 */     }  if (templateScalarModel instanceof TemplateMarkupOutputModel) {
/* 450 */       TemplateMarkupOutputModel markup = (TemplateMarkupOutputModel)templateScalarModel;
/* 451 */       MarkupOutputFormat<TemplateModel> outputFormat = markup.getOutputFormat();
/* 452 */       return outputFormat.concat(outputFormat.fromPlainTextByEscaping(truncatedS.toString()), markup);
/*     */     } 
/* 454 */     throw new IllegalArgumentException("Unsupported terminator type: " + 
/* 455 */         ClassUtil.getFTLTypeDescription(templateScalarModel));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private StringBuilder unifiedTruncateWithoutTerminatorAdded(String s, int maxLength, TemplateModel terminator, int terminatorLength, Boolean terminatorRemovesDots, TruncationMode mode) throws TemplateModelException {
/*     */     boolean skippedDots;
/* 463 */     int cbInitialLastCIdx = maxLength - terminatorLength - 1;
/* 464 */     int cbLastCIdx = cbInitialLastCIdx;
/*     */ 
/*     */ 
/*     */     
/* 468 */     cbLastCIdx = skipTrailingWS(s, cbLastCIdx);
/* 469 */     if (cbLastCIdx < 0) {
/* 470 */       return null;
/*     */     }
/*     */     
/* 473 */     if ((mode == TruncationMode.AUTO && this.wordBoundaryMinLength < 1.0D) || mode == TruncationMode.WORD_BOUNDARY) {
/*     */ 
/*     */       
/* 476 */       StringBuilder truncedS = null;
/*     */       
/* 478 */       int wordTerminatorLength = this.addSpaceAtWordBoundary ? (terminatorLength + 1) : terminatorLength;
/*     */       
/* 480 */       int minIdx = (mode == TruncationMode.AUTO) ? Math.max((int)Math.ceil(maxLength * this.wordBoundaryMinLength) - wordTerminatorLength - 1, 0) : 0;
/*     */ 
/*     */       
/* 483 */       int wbLastCIdx = Math.min(maxLength - wordTerminatorLength - 1, cbLastCIdx);
/*     */       
/* 485 */       boolean followingCIsWS = (s.length() > wbLastCIdx + 1) ? Character.isWhitespace(s.charAt(wbLastCIdx + 1)) : true;
/*     */       
/* 487 */       while (wbLastCIdx >= minIdx) {
/* 488 */         char curC = s.charAt(wbLastCIdx);
/* 489 */         boolean curCIsWS = Character.isWhitespace(curC);
/* 490 */         if (!curCIsWS && followingCIsWS) {
/*     */           
/* 492 */           if (!this.addSpaceAtWordBoundary && isDot(curC)) {
/* 493 */             if (terminatorRemovesDots == null) {
/* 494 */               terminatorRemovesDots = Boolean.valueOf(getTerminatorRemovesDots(terminator));
/*     */             }
/* 496 */             if (terminatorRemovesDots.booleanValue()) {
/* 497 */               while (wbLastCIdx >= minIdx && isDotOrWS(s.charAt(wbLastCIdx))) {
/* 498 */                 wbLastCIdx--;
/*     */               }
/* 500 */               if (wbLastCIdx < minIdx) {
/*     */                 break;
/*     */               }
/*     */             } 
/*     */           } 
/*     */           
/* 506 */           truncedS = new StringBuilder(wbLastCIdx + 1 + wordTerminatorLength);
/* 507 */           truncedS.append(s, 0, wbLastCIdx + 1);
/* 508 */           if (this.addSpaceAtWordBoundary) {
/* 509 */             truncedS.append(' ');
/*     */           }
/*     */           
/*     */           break;
/*     */         } 
/* 514 */         followingCIsWS = curCIsWS;
/* 515 */         wbLastCIdx--;
/*     */       } 
/*     */       
/* 518 */       if (truncedS != null || mode == TruncationMode.WORD_BOUNDARY || (mode == TruncationMode.AUTO && this.wordBoundaryMinLength == 0.0D))
/*     */       {
/*     */         
/* 521 */         return truncedS;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 531 */     cbLastCIdx--;
/* 532 */     if (cbLastCIdx == cbInitialLastCIdx && this.addSpaceAtWordBoundary && isWordEnd(s, cbLastCIdx) && cbLastCIdx < 0) {
/* 533 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     do {
/* 540 */       skippedDots = false;
/*     */       
/* 542 */       cbLastCIdx = skipTrailingWS(s, cbLastCIdx);
/* 543 */       if (cbLastCIdx < 0) {
/* 544 */         return null;
/*     */       }
/*     */ 
/*     */       
/* 548 */       if (!isDot(s.charAt(cbLastCIdx)) || (this.addSpaceAtWordBoundary && isWordEnd(s, cbLastCIdx)))
/* 549 */         continue;  if (terminatorRemovesDots == null) {
/* 550 */         terminatorRemovesDots = Boolean.valueOf(getTerminatorRemovesDots(terminator));
/*     */       }
/* 552 */       if (!terminatorRemovesDots.booleanValue())
/* 553 */         continue;  cbLastCIdx = skipTrailingDots(s, cbLastCIdx);
/* 554 */       if (cbLastCIdx < 0) {
/* 555 */         return null;
/*     */       }
/* 557 */       skippedDots = true;
/*     */     
/*     */     }
/* 560 */     while (skippedDots);
/*     */     
/* 562 */     boolean addWordBoundarySpace = (this.addSpaceAtWordBoundary && isWordEnd(s, cbLastCIdx));
/* 563 */     StringBuilder truncatedS = new StringBuilder(cbLastCIdx + 1 + (addWordBoundarySpace ? 1 : 0) + terminatorLength);
/* 564 */     truncatedS.append(s, 0, cbLastCIdx + 1);
/* 565 */     if (addWordBoundarySpace) {
/* 566 */       truncatedS.append(' ');
/*     */     }
/* 568 */     return truncatedS;
/*     */   }
/*     */   
/*     */   private int getTerminatorLength(TemplateModel terminator) throws TemplateModelException {
/* 572 */     return (terminator instanceof TemplateScalarModel) ? ((TemplateScalarModel)terminator)
/* 573 */       .getAsString().length() : 
/* 574 */       getMTerminatorLength((TemplateMarkupOutputModel)terminator);
/*     */   }
/*     */   
/*     */   private boolean getTerminatorRemovesDots(TemplateModel terminator) throws TemplateModelException {
/* 578 */     return (terminator instanceof TemplateScalarModel) ? 
/* 579 */       getTerminatorRemovesDots(((TemplateScalarModel)terminator).getAsString()) : 
/* 580 */       getMTerminatorRemovesDots((TemplateMarkupOutputModel)terminator);
/*     */   }
/*     */   
/*     */   private int skipTrailingWS(String s, int lastCIdx) {
/* 584 */     while (lastCIdx >= 0 && Character.isWhitespace(s.charAt(lastCIdx))) {
/* 585 */       lastCIdx--;
/*     */     }
/* 587 */     return lastCIdx;
/*     */   }
/*     */   
/*     */   private int skipTrailingDots(String s, int lastCIdx) {
/* 591 */     while (lastCIdx >= 0 && isDot(s.charAt(lastCIdx))) {
/* 592 */       lastCIdx--;
/*     */     }
/* 594 */     return lastCIdx;
/*     */   }
/*     */   
/*     */   private boolean isWordEnd(String s, int lastCIdx) {
/* 598 */     return (lastCIdx + 1 >= s.length() || Character.isWhitespace(s.charAt(lastCIdx + 1)));
/*     */   }
/*     */   
/*     */   private static boolean isDot(char c) {
/* 602 */     return (c == '.' || c == '…');
/*     */   }
/*     */   
/*     */   private static boolean isDotOrWS(char c) {
/* 606 */     return (isDot(c) || Character.isWhitespace(c));
/*     */   }
/*     */   
/*     */   private boolean isHTMLOrXML(MarkupOutputFormat<?> outputFormat) {
/* 610 */     return (outputFormat instanceof HTMLOutputFormat || outputFormat instanceof XMLOutputFormat);
/*     */   }
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
/*     */   static int getLengthWithoutTags(String s) {
/* 623 */     int result = 0;
/* 624 */     int i = 0;
/* 625 */     int len = s.length();
/* 626 */     while (i < len) {
/* 627 */       char c = s.charAt(i++);
/* 628 */       if (c == '<') {
/* 629 */         if (s.startsWith("!--", i)) {
/*     */           
/* 631 */           i += 3;
/* 632 */           while (i + 2 < len && (s.charAt(i) != '-' || s.charAt(i + 1) != '-' || s.charAt(i + 2) != '>')) {
/* 633 */             i++;
/*     */           }
/* 635 */           i += 3;
/* 636 */           if (i >= len)
/*     */             break;  continue;
/*     */         } 
/* 639 */         if (s.startsWith("![CDATA[", i)) {
/*     */           
/* 641 */           i += 8;
/* 642 */           while (i < len && (s
/* 643 */             .charAt(i) != ']' || i + 2 >= len || s
/* 644 */             .charAt(i + 1) != ']' || s.charAt(i + 2) != '>')) {
/* 645 */             result++;
/* 646 */             i++;
/*     */           } 
/* 648 */           i += 3;
/* 649 */           if (i >= len) {
/*     */             break;
/*     */           }
/*     */           continue;
/*     */         } 
/* 654 */         while (i < len && s.charAt(i) != '>') {
/* 655 */           i++;
/*     */         }
/* 657 */         i++;
/* 658 */         if (i >= len)
/*     */           break; 
/*     */         continue;
/*     */       } 
/* 662 */       if (c == '&') {
/*     */         
/* 664 */         while (i < len && s.charAt(i) != ';') {
/* 665 */           i++;
/*     */         }
/* 667 */         i++;
/* 668 */         result++;
/* 669 */         if (i >= len)
/*     */           break; 
/*     */         continue;
/*     */       } 
/* 673 */       result++;
/*     */     } 
/*     */     
/* 676 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean doesHtmlOrXmlStartWithDot(String s) {
/* 686 */     int i = 0;
/* 687 */     int len = s.length();
/* 688 */     while (i < len) {
/* 689 */       char c = s.charAt(i++);
/* 690 */       if (c == '<') {
/* 691 */         if (s.startsWith("!--", i)) {
/*     */           
/* 693 */           i += 3;
/* 694 */           while (i + 2 < len && ((
/* 695 */             c = s.charAt(i)) != '-' || s.charAt(i + 1) != '-' || s.charAt(i + 2) != '>')) {
/* 696 */             i++;
/*     */           }
/* 698 */           i += 3;
/* 699 */           if (i >= len)
/*     */             break;  continue;
/*     */         } 
/* 702 */         if (s.startsWith("![CDATA[", i)) {
/*     */           
/* 704 */           i += 8;
/* 705 */           if (i < len && ((
/* 706 */             c = s.charAt(i)) != ']' || i + 2 >= len || s
/*     */             
/* 708 */             .charAt(i + 1) != ']' || s.charAt(i + 2) != '>')) {
/* 709 */             return isDot(c);
/*     */           }
/* 711 */           i += 3;
/* 712 */           if (i >= len) {
/*     */             break;
/*     */           }
/*     */           continue;
/*     */         } 
/* 717 */         while (i < len && s.charAt(i) != '>') {
/* 718 */           i++;
/*     */         }
/* 720 */         i++;
/* 721 */         if (i >= len)
/*     */           break; 
/*     */         continue;
/*     */       } 
/* 725 */       if (c == '&') {
/*     */         
/* 727 */         int start = i;
/* 728 */         while (i < len && s.charAt(i) != ';') {
/* 729 */           i++;
/*     */         }
/* 731 */         return isDotCharReference(s.substring(start, i));
/*     */       } 
/* 733 */       return isDot(c);
/*     */     } 
/*     */     
/* 736 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   static boolean isDotCharReference(String name) {
/* 741 */     if (name.length() > 2 && name.charAt(0) == '#') {
/* 742 */       int charCode = getCodeFromNumericalCharReferenceName(name);
/* 743 */       return (charCode == 8230 || charCode == 46);
/*     */     } 
/* 745 */     return (name.equals("hellip") || name.equals("period"));
/*     */   }
/*     */ 
/*     */   
/*     */   static int getCodeFromNumericalCharReferenceName(String name) {
/* 750 */     char c = name.charAt(1);
/* 751 */     boolean hex = (c == 'x' || c == 'X');
/* 752 */     int code = 0;
/* 753 */     for (int pos = hex ? 2 : 1; pos < name.length(); pos++) {
/* 754 */       c = name.charAt(pos);
/* 755 */       code *= hex ? 16 : 10;
/* 756 */       if (c >= '0' && c <= '9') {
/* 757 */         code += c - 48;
/* 758 */       } else if (hex && c >= 'a' && c <= 'f') {
/* 759 */         code += c - 97 + 10;
/* 760 */       } else if (hex && c >= 'A' && c <= 'F') {
/* 761 */         code += c - 65 + 10;
/*     */       } else {
/* 763 */         return -1;
/*     */       } 
/*     */     } 
/* 766 */     return code;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\DefaultTruncateBuiltinAlgorithm.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */