package freemarker.core;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.NullArgumentException;

public class DefaultTruncateBuiltinAlgorithm extends TruncateBuiltinAlgorithm {
   public static final String STANDARD_ASCII_TERMINATOR = "[...]";
   public static final String STANDARD_UNICODE_TERMINATOR = "[…]";
   public static final TemplateHTMLOutputModel STANDARD_M_TERMINATOR;
   public static final double DEFAULT_WORD_BOUNDARY_MIN_LENGTH = 0.75;
   private static final int FALLBACK_M_TERMINATOR_LENGTH = 3;
   public static final DefaultTruncateBuiltinAlgorithm ASCII_INSTANCE;
   public static final DefaultTruncateBuiltinAlgorithm UNICODE_INSTANCE;
   private final TemplateScalarModel defaultTerminator;
   private final int defaultTerminatorLength;
   private final boolean defaultTerminatorRemovesDots;
   private final TemplateMarkupOutputModel<?> defaultMTerminator;
   private final Integer defaultMTerminatorLength;
   private final boolean defaultMTerminatorRemovesDots;
   private final double wordBoundaryMinLength;
   private final boolean addSpaceAtWordBoundary;

   public DefaultTruncateBuiltinAlgorithm(String defaultTerminator, TemplateMarkupOutputModel<?> defaultMTerminator, boolean addSpaceAtWordBoundary) {
      this(defaultTerminator, (Integer)null, (Boolean)null, defaultMTerminator, (Integer)null, (Boolean)null, addSpaceAtWordBoundary, (Double)null);
   }

   public DefaultTruncateBuiltinAlgorithm(String defaultTerminator, boolean addSpaceAtWordBoundary) {
      this(defaultTerminator, (Integer)null, (Boolean)null, (TemplateMarkupOutputModel)null, (Integer)null, (Boolean)null, addSpaceAtWordBoundary, (Double)null);
   }

   public DefaultTruncateBuiltinAlgorithm(String defaultTerminator, Integer defaultTerminatorLength, Boolean defaultTerminatorRemovesDots, TemplateMarkupOutputModel<?> defaultMTerminator, Integer defaultMTerminatorLength, Boolean defaultMTerminatorRemovesDots, boolean addSpaceAtWordBoundary, Double wordBoundaryMinLength) {
      NullArgumentException.check("defaultTerminator", defaultTerminator);
      this.defaultTerminator = new SimpleScalar(defaultTerminator);

      try {
         this.defaultTerminatorLength = defaultTerminatorLength != null ? defaultTerminatorLength : defaultTerminator.length();
         this.defaultTerminatorRemovesDots = defaultTerminatorRemovesDots != null ? defaultTerminatorRemovesDots : this.getTerminatorRemovesDots(defaultTerminator);
      } catch (TemplateModelException var11) {
         throw new IllegalArgumentException("Failed to examine defaultTerminator", var11);
      }

      this.defaultMTerminator = defaultMTerminator;
      if (defaultMTerminator != null) {
         try {
            this.defaultMTerminatorLength = defaultMTerminatorLength != null ? defaultMTerminatorLength : this.getMTerminatorLength(defaultMTerminator);
            this.defaultMTerminatorRemovesDots = defaultMTerminatorRemovesDots != null ? defaultMTerminatorRemovesDots : this.getMTerminatorRemovesDots(defaultMTerminator);
         } catch (TemplateModelException var10) {
            throw new IllegalArgumentException("Failed to examine defaultMTerminator", var10);
         }
      } else {
         this.defaultMTerminatorLength = null;
         this.defaultMTerminatorRemovesDots = false;
      }

      if (wordBoundaryMinLength == null) {
         wordBoundaryMinLength = 0.75;
      } else if (wordBoundaryMinLength < 0.0 || wordBoundaryMinLength > 1.0) {
         throw new IllegalArgumentException("wordBoundaryMinLength must be between 0.0 and 1.0 (inclusive)");
      }

      this.wordBoundaryMinLength = wordBoundaryMinLength;
      this.addSpaceAtWordBoundary = addSpaceAtWordBoundary;
   }

   public TemplateScalarModel truncate(String s, int maxLength, TemplateScalarModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
      return (TemplateScalarModel)this.unifiedTruncate(s, maxLength, terminator, terminatorLength, DefaultTruncateBuiltinAlgorithm.TruncationMode.AUTO, false);
   }

   public TemplateScalarModel truncateW(String s, int maxLength, TemplateScalarModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
      return (TemplateScalarModel)this.unifiedTruncate(s, maxLength, terminator, terminatorLength, DefaultTruncateBuiltinAlgorithm.TruncationMode.WORD_BOUNDARY, false);
   }

   public TemplateScalarModel truncateC(String s, int maxLength, TemplateScalarModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
      return (TemplateScalarModel)this.unifiedTruncate(s, maxLength, terminator, terminatorLength, DefaultTruncateBuiltinAlgorithm.TruncationMode.CHAR_BOUNDARY, false);
   }

   public TemplateModel truncateM(String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
      return this.unifiedTruncate(s, maxLength, terminator, terminatorLength, DefaultTruncateBuiltinAlgorithm.TruncationMode.AUTO, true);
   }

   public TemplateModel truncateWM(String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
      return this.unifiedTruncate(s, maxLength, terminator, terminatorLength, DefaultTruncateBuiltinAlgorithm.TruncationMode.WORD_BOUNDARY, true);
   }

   public TemplateModel truncateCM(String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
      return this.unifiedTruncate(s, maxLength, terminator, terminatorLength, DefaultTruncateBuiltinAlgorithm.TruncationMode.CHAR_BOUNDARY, true);
   }

   public String getDefaultTerminator() {
      try {
         return this.defaultTerminator.getAsString();
      } catch (TemplateModelException var2) {
         throw new IllegalStateException(var2);
      }
   }

   public int getDefaultTerminatorLength() {
      return this.defaultTerminatorLength;
   }

   public boolean getDefaultTerminatorRemovesDots() {
      return this.defaultTerminatorRemovesDots;
   }

   public TemplateMarkupOutputModel<?> getDefaultMTerminator() {
      return this.defaultMTerminator;
   }

   public Integer getDefaultMTerminatorLength() {
      return this.defaultMTerminatorLength;
   }

   public boolean getDefaultMTerminatorRemovesDots() {
      return this.defaultMTerminatorRemovesDots;
   }

   public double getWordBoundaryMinLength() {
      return this.wordBoundaryMinLength;
   }

   public boolean getAddSpaceAtWordBoundary() {
      return this.addSpaceAtWordBoundary;
   }

   protected int getMTerminatorLength(TemplateMarkupOutputModel<?> mTerminator) throws TemplateModelException {
      MarkupOutputFormat format = mTerminator.getOutputFormat();
      return this.isHTMLOrXML(format) ? getLengthWithoutTags(format.getMarkupString(mTerminator)) : 3;
   }

   protected boolean getTerminatorRemovesDots(String terminator) throws TemplateModelException {
      return terminator.startsWith(".") || terminator.startsWith("…");
   }

   protected boolean getMTerminatorRemovesDots(TemplateMarkupOutputModel terminator) throws TemplateModelException {
      return this.isHTMLOrXML(terminator.getOutputFormat()) ? doesHtmlOrXmlStartWithDot(terminator.getOutputFormat().getMarkupString(terminator)) : true;
   }

   private TemplateModel unifiedTruncate(String s, int maxLength, TemplateModel terminator, Integer terminatorLength, TruncationMode mode, boolean allowMarkupResult) throws TemplateException {
      if (s.length() <= maxLength) {
         return new SimpleScalar(s);
      } else if (maxLength < 0) {
         throw new IllegalArgumentException("maxLength can't be negative");
      } else {
         Boolean terminatorRemovesDots;
         if (terminator == null) {
            if (allowMarkupResult && this.defaultMTerminator != null) {
               terminator = this.defaultMTerminator;
               terminatorLength = this.defaultMTerminatorLength;
               terminatorRemovesDots = this.defaultMTerminatorRemovesDots;
            } else {
               terminator = this.defaultTerminator;
               terminatorLength = this.defaultTerminatorLength;
               terminatorRemovesDots = this.defaultTerminatorRemovesDots;
            }
         } else {
            if (terminatorLength != null) {
               if (terminatorLength < 0) {
                  throw new IllegalArgumentException("terminatorLength can't be negative");
               }
            } else {
               terminatorLength = this.getTerminatorLength((TemplateModel)terminator);
            }

            terminatorRemovesDots = null;
         }

         StringBuilder truncatedS = this.unifiedTruncateWithoutTerminatorAdded(s, maxLength, (TemplateModel)terminator, terminatorLength, terminatorRemovesDots, mode);
         if (truncatedS != null && truncatedS.length() != 0) {
            if (terminator instanceof TemplateScalarModel) {
               truncatedS.append(((TemplateScalarModel)terminator).getAsString());
               return new SimpleScalar(truncatedS.toString());
            } else if (terminator instanceof TemplateMarkupOutputModel) {
               TemplateMarkupOutputModel markup = (TemplateMarkupOutputModel)terminator;
               MarkupOutputFormat outputFormat = markup.getOutputFormat();
               return outputFormat.concat(outputFormat.fromPlainTextByEscaping(truncatedS.toString()), markup);
            } else {
               throw new IllegalArgumentException("Unsupported terminator type: " + ClassUtil.getFTLTypeDescription((TemplateModel)terminator));
            }
         } else {
            return (TemplateModel)terminator;
         }
      }
   }

   private StringBuilder unifiedTruncateWithoutTerminatorAdded(String s, int maxLength, TemplateModel terminator, int terminatorLength, Boolean terminatorRemovesDots, TruncationMode mode) throws TemplateModelException {
      int cbInitialLastCIdx = maxLength - terminatorLength - 1;
      int cbLastCIdx = this.skipTrailingWS(s, cbInitialLastCIdx);
      if (cbLastCIdx < 0) {
         return null;
      } else {
         if (mode == DefaultTruncateBuiltinAlgorithm.TruncationMode.AUTO && this.wordBoundaryMinLength < 1.0 || mode == DefaultTruncateBuiltinAlgorithm.TruncationMode.WORD_BOUNDARY) {
            StringBuilder truncedS = null;
            int wordTerminatorLength = this.addSpaceAtWordBoundary ? terminatorLength + 1 : terminatorLength;
            int minIdx = mode == DefaultTruncateBuiltinAlgorithm.TruncationMode.AUTO ? Math.max((int)Math.ceil((double)maxLength * this.wordBoundaryMinLength) - wordTerminatorLength - 1, 0) : 0;
            int wbLastCIdx = Math.min(maxLength - wordTerminatorLength - 1, cbLastCIdx);

            for(boolean followingCIsWS = s.length() > wbLastCIdx + 1 ? Character.isWhitespace(s.charAt(wbLastCIdx + 1)) : true; wbLastCIdx >= minIdx; --wbLastCIdx) {
               char curC = s.charAt(wbLastCIdx);
               boolean curCIsWS = Character.isWhitespace(curC);
               if (!curCIsWS && followingCIsWS) {
                  if (!this.addSpaceAtWordBoundary && isDot(curC)) {
                     if (terminatorRemovesDots == null) {
                        terminatorRemovesDots = this.getTerminatorRemovesDots(terminator);
                     }

                     if (terminatorRemovesDots) {
                        while(wbLastCIdx >= minIdx && isDotOrWS(s.charAt(wbLastCIdx))) {
                           --wbLastCIdx;
                        }

                        if (wbLastCIdx < minIdx) {
                           break;
                        }
                     }
                  }

                  truncedS = new StringBuilder(wbLastCIdx + 1 + wordTerminatorLength);
                  truncedS.append(s, 0, wbLastCIdx + 1);
                  if (this.addSpaceAtWordBoundary) {
                     truncedS.append(' ');
                  }
                  break;
               }

               followingCIsWS = curCIsWS;
            }

            if (truncedS != null || mode == DefaultTruncateBuiltinAlgorithm.TruncationMode.WORD_BOUNDARY || mode == DefaultTruncateBuiltinAlgorithm.TruncationMode.AUTO && this.wordBoundaryMinLength == 0.0) {
               return truncedS;
            }
         }

         if (cbLastCIdx == cbInitialLastCIdx && this.addSpaceAtWordBoundary && this.isWordEnd(s, cbLastCIdx)) {
            --cbLastCIdx;
            if (cbLastCIdx < 0) {
               return null;
            }
         }

         boolean skippedDots;
         do {
            skippedDots = false;
            cbLastCIdx = this.skipTrailingWS(s, cbLastCIdx);
            if (cbLastCIdx < 0) {
               return null;
            }

            if (isDot(s.charAt(cbLastCIdx)) && (!this.addSpaceAtWordBoundary || !this.isWordEnd(s, cbLastCIdx))) {
               if (terminatorRemovesDots == null) {
                  terminatorRemovesDots = this.getTerminatorRemovesDots(terminator);
               }

               if (terminatorRemovesDots) {
                  cbLastCIdx = this.skipTrailingDots(s, cbLastCIdx);
                  if (cbLastCIdx < 0) {
                     return null;
                  }

                  skippedDots = true;
               }
            }
         } while(skippedDots);

         boolean addWordBoundarySpace = this.addSpaceAtWordBoundary && this.isWordEnd(s, cbLastCIdx);
         StringBuilder truncatedS = new StringBuilder(cbLastCIdx + 1 + (addWordBoundarySpace ? 1 : 0) + terminatorLength);
         truncatedS.append(s, 0, cbLastCIdx + 1);
         if (addWordBoundarySpace) {
            truncatedS.append(' ');
         }

         return truncatedS;
      }
   }

   private int getTerminatorLength(TemplateModel terminator) throws TemplateModelException {
      return terminator instanceof TemplateScalarModel ? ((TemplateScalarModel)terminator).getAsString().length() : this.getMTerminatorLength((TemplateMarkupOutputModel)terminator);
   }

   private boolean getTerminatorRemovesDots(TemplateModel terminator) throws TemplateModelException {
      return terminator instanceof TemplateScalarModel ? this.getTerminatorRemovesDots(((TemplateScalarModel)terminator).getAsString()) : this.getMTerminatorRemovesDots((TemplateMarkupOutputModel)terminator);
   }

   private int skipTrailingWS(String s, int lastCIdx) {
      while(lastCIdx >= 0 && Character.isWhitespace(s.charAt(lastCIdx))) {
         --lastCIdx;
      }

      return lastCIdx;
   }

   private int skipTrailingDots(String s, int lastCIdx) {
      while(lastCIdx >= 0 && isDot(s.charAt(lastCIdx))) {
         --lastCIdx;
      }

      return lastCIdx;
   }

   private boolean isWordEnd(String s, int lastCIdx) {
      return lastCIdx + 1 >= s.length() || Character.isWhitespace(s.charAt(lastCIdx + 1));
   }

   private static boolean isDot(char c) {
      return c == '.' || c == 8230;
   }

   private static boolean isDotOrWS(char c) {
      return isDot(c) || Character.isWhitespace(c);
   }

   private boolean isHTMLOrXML(MarkupOutputFormat<?> outputFormat) {
      return outputFormat instanceof HTMLOutputFormat || outputFormat instanceof XMLOutputFormat;
   }

   static int getLengthWithoutTags(String s) {
      int result = 0;
      int i = 0;
      int len = s.length();

      while(i < len) {
         char c = s.charAt(i++);
         if (c == '<') {
            if (s.startsWith("!--", i)) {
               for(i += 3; i + 2 < len && (s.charAt(i) != '-' || s.charAt(i + 1) != '-' || s.charAt(i + 2) != '>'); ++i) {
               }

               i += 3;
               if (i >= len) {
                  break;
               }
            } else if (s.startsWith("![CDATA[", i)) {
               for(i += 8; i < len && (s.charAt(i) != ']' || i + 2 >= len || s.charAt(i + 1) != ']' || s.charAt(i + 2) != '>'); ++i) {
                  ++result;
               }

               i += 3;
               if (i >= len) {
                  break;
               }
            } else {
               while(i < len && s.charAt(i) != '>') {
                  ++i;
               }

               ++i;
               if (i >= len) {
                  break;
               }
            }
         } else if (c != '&') {
            ++result;
         } else {
            while(i < len && s.charAt(i) != ';') {
               ++i;
            }

            ++i;
            ++result;
            if (i >= len) {
               break;
            }
         }
      }

      return result;
   }

   static boolean doesHtmlOrXmlStartWithDot(String s) {
      int i = 0;
      int len = s.length();

      while(i < len) {
         char c = s.charAt(i++);
         if (c != '<') {
            if (c != '&') {
               return isDot(c);
            }

            int start;
            for(start = i; i < len && s.charAt(i) != ';'; ++i) {
            }

            return isDotCharReference(s.substring(start, i));
         }

         if (s.startsWith("!--", i)) {
            for(i += 3; i + 2 < len && (s.charAt(i) != '-' || s.charAt(i + 1) != '-' || s.charAt(i + 2) != '>'); ++i) {
            }

            i += 3;
            if (i >= len) {
               break;
            }
         } else if (s.startsWith("![CDATA[", i)) {
            i += 8;
            if (i >= len || (c = s.charAt(i)) == ']' && i + 2 < len && s.charAt(i + 1) == ']' && s.charAt(i + 2) == '>') {
               i += 3;
               if (i < len) {
                  continue;
               }
               break;
            }

            return isDot(c);
         } else {
            while(i < len && s.charAt(i) != '>') {
               ++i;
            }

            ++i;
            if (i >= len) {
               break;
            }
         }
      }

      return false;
   }

   static boolean isDotCharReference(String name) {
      if (name.length() > 2 && name.charAt(0) == '#') {
         int charCode = getCodeFromNumericalCharReferenceName(name);
         return charCode == 8230 || charCode == 46;
      } else {
         return name.equals("hellip") || name.equals("period");
      }
   }

   static int getCodeFromNumericalCharReferenceName(String name) {
      char c = name.charAt(1);
      boolean hex = c == 'x' || c == 'X';
      int code = 0;

      for(int pos = hex ? 2 : 1; pos < name.length(); ++pos) {
         c = name.charAt(pos);
         code *= hex ? 16 : 10;
         if (c >= '0' && c <= '9') {
            code += c - 48;
         } else if (hex && c >= 'a' && c <= 'f') {
            code += c - 97 + 10;
         } else {
            if (!hex || c < 'A' || c > 'F') {
               return -1;
            }

            code += c - 65 + 10;
         }
      }

      return code;
   }

   static {
      try {
         STANDARD_M_TERMINATOR = (TemplateHTMLOutputModel)HTMLOutputFormat.INSTANCE.fromMarkup("<span class='truncateTerminator'>[&#8230;]</span>");
      } catch (TemplateModelException var1) {
         throw new IllegalStateException(var1);
      }

      ASCII_INSTANCE = new DefaultTruncateBuiltinAlgorithm("[...]", STANDARD_M_TERMINATOR, true);
      UNICODE_INSTANCE = new DefaultTruncateBuiltinAlgorithm("[…]", STANDARD_M_TERMINATOR, true);
   }

   private static enum TruncationMode {
      CHAR_BOUNDARY,
      WORD_BOUNDARY,
      AUTO;
   }
}
