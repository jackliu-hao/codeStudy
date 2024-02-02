package freemarker.core;

import freemarker.template.Configuration;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;

class FMParserTokenManager implements FMParserConstants {
   private static final String PLANNED_DIRECTIVE_HINT = "(If you have seen this directive in use elsewhere, this was a planned directive, so maybe you need to upgrade FreeMarker.)";
   String noparseTag;
   private FMParser parser;
   private int postInterpolationLexState = -1;
   private int curlyBracketNesting;
   private int parenthesisNesting;
   private int bracketNesting;
   private boolean inFTLHeader;
   boolean strictSyntaxMode;
   boolean squBracTagSyntax;
   boolean autodetectTagSyntax;
   boolean tagSyntaxEstablished;
   boolean inInvocation;
   int interpolationSyntax;
   int initialNamingConvention;
   int namingConvention;
   Token namingConventionEstabilisher;
   int incompatibleImprovements;
   public PrintStream debugStream;
   static final long[] jjbitVec0 = new long[]{-2L, -1L, -1L, -1L};
   static final long[] jjbitVec2 = new long[]{0L, 0L, -1L, -1L};
   static final long[] jjbitVec3 = new long[]{-4503595332403202L, -8193L, -17386027614209L, 1585267068842803199L};
   static final long[] jjbitVec4 = new long[]{0L, 0L, 297241973452963840L, -36028797027352577L};
   static final long[] jjbitVec5 = new long[]{0L, -9222809086901354496L, 536805376L, 0L};
   static final long[] jjbitVec6 = new long[]{-864764451093480316L, 17376L, 24L, 0L};
   static final long[] jjbitVec7 = new long[]{-140737488355329L, -2147483649L, -1L, 3509778554814463L};
   static final long[] jjbitVec8 = new long[]{-245465970900993L, 141836999983103L, 9187201948305063935L, 2139062143L};
   static final long[] jjbitVec9 = new long[]{140737488355328L, 0L, 0L, 0L};
   static final long[] jjbitVec10 = new long[]{1746833705466331232L, -1L, -1L, -1L};
   static final long[] jjbitVec11 = new long[]{-1L, -1L, 576460748008521727L, -281474976710656L};
   static final long[] jjbitVec12 = new long[]{-1L, -1L, 0L, 0L};
   static final long[] jjbitVec13 = new long[]{-1L, -1L, 18014398509481983L, 0L};
   static final long[] jjbitVec14 = new long[]{-1L, -1L, 8191L, 4611686018427322368L};
   static final long[] jjbitVec15 = new long[]{17592185987071L, -9223231299366420481L, -4278190081L, 274877906943L};
   static final long[] jjbitVec16 = new long[]{-12893290496L, -1L, 8791799069183L, -72057594037927936L};
   static final long[] jjbitVec17 = new long[]{34359736251L, 4503599627370495L, 4503599627370492L, 647392446501552128L};
   static final long[] jjbitVec18 = new long[]{-281200098803713L, 2305843004918726783L, 2251799813685232L, 67076096L};
   static final long[] jjbitVec19 = new long[]{2199023255551L, 324259168942755831L, 4495436853045886975L, 7890092085477381L};
   static final long[] jjbitVec20 = new long[]{140183445864062L, 0L, 0L, 287948935534739455L};
   static final long[] jjbitVec21 = new long[]{-1L, -1L, -281406257233921L, 1152921504606845055L};
   static final long[] jjbitVec22 = new long[]{6881498030004502655L, -37L, 1125899906842623L, -524288L};
   static final long[] jjbitVec23 = new long[]{4611686018427387903L, -65536L, -196609L, 1152640029630136575L};
   static final long[] jjbitVec24 = new long[]{0L, -9288674231451648L, -1L, 2305843009213693951L};
   static final long[] jjbitVec25 = new long[]{576460743780532224L, -274743689218L, Long.MAX_VALUE, 486341884L};
   static final int[] jjnextStates = new int[]{10, 12, 4, 5, 3, 4, 5, 697, 712, 369, 370, 371, 372, 373, 374, 375, 376, 377, 378, 379, 380, 381, 382, 383, 384, 385, 386, 387, 388, 389, 390, 391, 392, 393, 394, 395, 396, 397, 398, 404, 405, 413, 414, 423, 424, 431, 432, 443, 444, 455, 456, 467, 468, 477, 478, 488, 489, 499, 500, 512, 513, 522, 523, 539, 540, 551, 552, 570, 571, 583, 584, 597, 598, 608, 609, 610, 611, 612, 613, 614, 615, 616, 617, 618, 619, 620, 621, 622, 623, 624, 625, 626, 636, 637, 638, 650, 651, 656, 662, 663, 665, 12, 21, 24, 31, 36, 45, 50, 58, 65, 70, 77, 84, 90, 98, 105, 114, 120, 130, 136, 141, 148, 153, 161, 174, 183, 199, 209, 218, 227, 234, 242, 253, 262, 269, 277, 278, 286, 291, 296, 305, 314, 321, 331, 339, 350, 357, 367, 5, 6, 14, 15, 38, 41, 47, 48, 178, 179, 187, 188, 201, 202, 211, 212, 222, 223, 229, 230, 231, 236, 237, 238, 244, 245, 246, 255, 256, 257, 264, 265, 266, 271, 272, 273, 279, 280, 281, 283, 284, 285, 288, 289, 290, 293, 294, 295, 298, 299, 307, 308, 309, 323, 324, 325, 341, 342, 343, 361, 362, 400, 401, 407, 408, 416, 417, 426, 427, 434, 435, 446, 447, 460, 461, 470, 471, 480, 481, 491, 492, 502, 503, 515, 516, 527, 528, 544, 545, 556, 557, 573, 574, 586, 587, 600, 601, 628, 629, 642, 643, 700, 701, 703, 708, 709, 704, 710, 703, 705, 706, 708, 709, 369, 370, 371, 372, 373, 374, 375, 376, 377, 378, 379, 380, 381, 382, 383, 384, 385, 386, 387, 388, 389, 390, 391, 392, 393, 394, 395, 396, 397, 667, 668, 669, 670, 671, 672, 673, 674, 675, 676, 677, 678, 679, 680, 681, 682, 683, 684, 609, 610, 611, 612, 613, 614, 615, 616, 617, 618, 619, 620, 621, 622, 623, 624, 625, 685, 637, 686, 651, 689, 692, 663, 693, 193, 198, 562, 567, 658, 659, 699, 711, 708, 709, 58, 59, 60, 81, 84, 87, 91, 92, 101, 54, 56, 47, 51, 44, 45, 13, 14, 17, 6, 7, 10, 67, 69, 71, 74, 77, 20, 23, 8, 11, 15, 18, 21, 22, 24, 25, 55, 56, 57, 78, 81, 84, 88, 89, 98, 51, 53, 44, 48, 64, 66, 68, 71, 74, 3, 5, 54, 55, 56, 77, 80, 83, 87, 88, 97, 50, 52, 43, 47, 40, 41, 8, 9, 12, 1, 2, 5, 63, 65, 67, 70, 73, 3, 6, 10, 13, 16, 17, 19, 20, 60, 61, 62, 83, 86, 89, 93, 94, 103, 56, 58, 49, 53, 46, 47, 69, 71, 73, 76, 79};
   public static final String[] jjstrLiteralImages = new String[]{"", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "${", "#{", "[=", null, null, null, null, null, null, null, null, null, null, "false", "true", null, null, ".", "..", null, "..*", "?", "??", "=", "==", "!=", "+=", "-=", "*=", "/=", "%=", "++", "--", null, null, null, null, null, "+", "-", "*", "**", "...", "/", "%", null, null, "!", ",", ";", ":", "[", "]", "(", ")", "{", "}", "in", "as", "using", null, null, null, null, null, null, ">", null, ">", ">=", null, null, null, null, null, null};
   int curLexState;
   int defaultLexState;
   int jjnewStateCnt;
   int jjround;
   int jjmatchedPos;
   int jjmatchedKind;
   public static final String[] lexStateNames = new String[]{"DEFAULT", "NO_DIRECTIVE", "FM_EXPRESSION", "IN_PAREN", "NAMED_PARAMETER_EXPRESSION", "EXPRESSION_COMMENT", "NO_SPACE_EXPRESSION", "NO_PARSE"};
   public static final int[] jjnewLexState = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2, 2, -1, -1, -1, -1};
   static final long[] jjtoToken = new long[]{-63L, -534773761L, 1072758783L};
   static final long[] jjtoSkip = new long[]{0L, 266338304L, 0L};
   protected SimpleCharStream input_stream;
   private final int[] jjrounds;
   private final int[] jjstateSet;
   private final StringBuilder jjimage;
   private StringBuilder image;
   private int jjimageLen;
   private int lengthOfMatch;
   protected int curChar;

   void setParser(FMParser parser) {
      this.parser = parser;
   }

   private void handleTagSyntaxAndSwitch(Token tok, int tokenNamingConvention, int newLexState) {
      String image = tok.image;
      if (!this.strictSyntaxMode && tokenNamingConvention == 12 && !this.isStrictTag(image)) {
         tok.kind = 80;
      } else {
         char firstChar = image.charAt(0);
         if (this.autodetectTagSyntax && !this.tagSyntaxEstablished) {
            this.squBracTagSyntax = firstChar == '[';
         }

         if (firstChar == '[' && !this.squBracTagSyntax || firstChar == '<' && this.squBracTagSyntax) {
            tok.kind = 80;
         } else if (!this.strictSyntaxMode) {
            this.checkNamingConvention(tok, tokenNamingConvention);
            this.SwitchTo(newLexState);
         } else if (!this.squBracTagSyntax && !this.isStrictTag(image)) {
            tok.kind = 80;
         } else {
            this.tagSyntaxEstablished = true;
            if (this.incompatibleImprovements >= _TemplateAPI.VERSION_INT_2_3_28 || this.interpolationSyntax == 22) {
               char lastChar = image.charAt(image.length() - 1);
               if ((lastChar == ']' || lastChar == '>') && (!this.squBracTagSyntax && lastChar != '>' || this.squBracTagSyntax && lastChar != ']')) {
                  throw new TokenMgrError("The tag shouldn't end with \"" + lastChar + "\".", 0, tok.beginLine, tok.beginColumn, tok.endLine, tok.endColumn);
               }
            }

            this.checkNamingConvention(tok, tokenNamingConvention);
            this.SwitchTo(newLexState);
         }
      }
   }

   void checkNamingConvention(Token tok) {
      this.checkNamingConvention(tok, _CoreStringUtils.getIdentifierNamingConvention(tok.image));
   }

   void checkNamingConvention(Token tok, int tokenNamingConvention) {
      if (tokenNamingConvention != 10) {
         if (this.namingConvention == 10) {
            this.namingConvention = tokenNamingConvention;
            this.namingConventionEstabilisher = tok;
         } else if (this.namingConvention != tokenNamingConvention) {
            throw this.newNameConventionMismatchException(tok);
         }
      }

   }

   private TokenMgrError newNameConventionMismatchException(Token tok) {
      return new TokenMgrError("Naming convention mismatch. Identifiers that are part of the template language (not the user specified ones) " + (this.initialNamingConvention == 10 ? "must consistently use the same naming convention within the same template. This template uses " : "must use the configured naming convention, which is the ") + (this.namingConvention == 12 ? "camel case naming convention (like: exampleName) " : (this.namingConvention == 11 ? "legacy naming convention (directive (tag) names are like examplename, everything else is like example_name) " : "??? (internal error)")) + (this.namingConventionEstabilisher != null ? "estabilished by auto-detection at " + _MessageUtil.formatPosition(this.namingConventionEstabilisher.beginLine, this.namingConventionEstabilisher.beginColumn) + " by token " + StringUtil.jQuote(this.namingConventionEstabilisher.image.trim()) : "") + ", but the problematic token, " + StringUtil.jQuote(tok.image.trim()) + ", uses a different convention.", 0, tok.beginLine, tok.beginColumn, tok.endLine, tok.endColumn);
   }

   private void handleTagSyntaxAndSwitch(Token tok, int newLexState) {
      this.handleTagSyntaxAndSwitch(tok, 10, newLexState);
   }

   private boolean isStrictTag(String image) {
      return image.length() > 2 && (image.charAt(1) == '#' || image.charAt(2) == '#');
   }

   private static int getTagNamingConvention(Token tok, int charIdxInName) {
      return _CoreStringUtils.isUpperUSASCII(getTagNameCharAt(tok, charIdxInName)) ? 12 : 11;
   }

   static char getTagNameCharAt(Token tok, int charIdxInName) {
      String image = tok.image;
      int idx = 0;

      while(true) {
         char c = image.charAt(idx);
         if (c != '<' && c != '[' && c != '/' && c != '#') {
            return image.charAt(idx + charIdxInName);
         }

         ++idx;
      }
   }

   private void unifiedCall(Token tok) {
      char firstChar = tok.image.charAt(0);
      if (this.autodetectTagSyntax && !this.tagSyntaxEstablished) {
         this.squBracTagSyntax = firstChar == '[';
      }

      if (this.squBracTagSyntax && firstChar == '<') {
         tok.kind = 80;
      } else if (!this.squBracTagSyntax && firstChar == '[') {
         tok.kind = 80;
      } else {
         this.tagSyntaxEstablished = true;
         this.SwitchTo(6);
      }
   }

   private void unifiedCallEnd(Token tok) {
      char firstChar = tok.image.charAt(0);
      if (this.squBracTagSyntax && firstChar == '<') {
         tok.kind = 80;
      } else if (!this.squBracTagSyntax && firstChar == '[') {
         tok.kind = 80;
      }
   }

   private void startInterpolation(Token tok) {
      if (this.interpolationSyntax == 20 && tok.kind == 84 || this.interpolationSyntax == 21 && tok.kind != 82 || this.interpolationSyntax == 22 && tok.kind != 84) {
         tok.kind = 80;
      } else if (this.postInterpolationLexState != -1) {
         char c = tok.image.charAt(0);
         throw new TokenMgrError("You can't start an interpolation (" + tok.image + "..." + (this.interpolationSyntax == 22 ? "]" : "}") + ") here as you are inside another interpolation.)", 0, tok.beginLine, tok.beginColumn, tok.endLine, tok.endColumn);
      } else {
         this.postInterpolationLexState = this.curLexState;
         this.SwitchTo(2);
      }
   }

   private void endInterpolation(Token closingTk) {
      this.SwitchTo(this.postInterpolationLexState);
      this.postInterpolationLexState = -1;
   }

   private TokenMgrError newUnexpectedClosingTokenException(Token closingTk) {
      return new TokenMgrError("You can't have an \"" + closingTk.image + "\" here, as there's nothing open that it could close.", 0, closingTk.beginLine, closingTk.beginColumn, closingTk.endLine, closingTk.endColumn);
   }

   private void eatNewline() {
      int charsRead = 0;

      try {
         char c;
         do {
            c = this.input_stream.readChar();
            ++charsRead;
            if (!Character.isWhitespace(c)) {
               this.input_stream.backup(charsRead);
               return;
            }

            if (c == '\r') {
               char next = this.input_stream.readChar();
               ++charsRead;
               if (next != '\n') {
                  this.input_stream.backup(1);
               }

               return;
            }
         } while(c != '\n');

      } catch (IOException var4) {
         this.input_stream.backup(charsRead);
      }
   }

   private void ftlHeader(Token matchedToken) {
      if (!this.tagSyntaxEstablished) {
         this.squBracTagSyntax = matchedToken.image.charAt(0) == '[';
         this.tagSyntaxEstablished = true;
         this.autodetectTagSyntax = false;
      }

      String img = matchedToken.image;
      char firstChar = img.charAt(0);
      char lastChar = img.charAt(img.length() - 1);
      if (firstChar == '[' && !this.squBracTagSyntax || firstChar == '<' && this.squBracTagSyntax) {
         matchedToken.kind = 80;
      }

      if (matchedToken.kind != 80) {
         if (lastChar != '>' && lastChar != ']') {
            this.SwitchTo(2);
            this.inFTLHeader = true;
         } else {
            this.eatNewline();
         }
      }

   }

   public void setDebugStream(PrintStream ds) {
      this.debugStream = ds;
   }

   private int jjMoveStringLiteralDfa0_7() {
      return this.jjMoveNfa_7(0, 0);
   }

   private int jjMoveNfa_7(int startState, int curPos) {
      int startsAt = 0;
      this.jjnewStateCnt = 13;
      int i = 1;
      this.jjstateSet[0] = startState;
      int kind = Integer.MAX_VALUE;

      while(true) {
         if (++this.jjround == Integer.MAX_VALUE) {
            this.ReInitRounds();
         }

         long l;
         if (this.curChar < 64) {
            l = 1L << this.curChar;

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 0:
                     if ((-1152956688978935809L & l) != 0L) {
                        if (kind > 156) {
                           kind = 156;
                        }

                        this.jjCheckNAdd(6);
                     } else if ((1152956688978935808L & l) != 0L && kind > 157) {
                        kind = 157;
                     }

                     if (this.curChar == 45) {
                        this.jjAddStates(0, 1);
                     } else if (this.curChar == 60) {
                        this.jjstateSet[this.jjnewStateCnt++] = 1;
                     }
                     break;
                  case 1:
                     if (this.curChar == 47) {
                        this.jjCheckNAddTwoStates(2, 3);
                     }
                     break;
                  case 2:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(3);
                     }
                  case 3:
                  case 11:
                  default:
                     break;
                  case 4:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(2, 3);
                     }
                     break;
                  case 5:
                     if (this.curChar == 62 && kind > 155) {
                        kind = 155;
                     }
                     break;
                  case 6:
                     if ((-1152956688978935809L & l) != 0L) {
                        if (kind > 156) {
                           kind = 156;
                        }

                        this.jjCheckNAdd(6);
                     }
                     break;
                  case 7:
                     if ((1152956688978935808L & l) != 0L && kind > 157) {
                        kind = 157;
                     }
                     break;
                  case 8:
                     if (this.curChar == 45) {
                        this.jjAddStates(0, 1);
                     }
                     break;
                  case 9:
                     if (this.curChar == 62 && kind > 154) {
                        kind = 154;
                     }
                     break;
                  case 10:
                     if (this.curChar == 45) {
                        this.jjstateSet[this.jjnewStateCnt++] = 9;
                     }
                     break;
                  case 12:
                     if (this.curChar == 45) {
                        this.jjstateSet[this.jjnewStateCnt++] = 11;
                     }
               }
            } while(i != startsAt);
         } else if (this.curChar < 128) {
            l = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 0:
                     if ((-134217729L & l) != 0L) {
                        if (kind > 156) {
                           kind = 156;
                        }

                        this.jjCheckNAdd(6);
                     } else if (this.curChar == 91 && kind > 157) {
                        kind = 157;
                     }

                     if (this.curChar == 91) {
                        this.jjstateSet[this.jjnewStateCnt++] = 1;
                     }
                  case 1:
                  case 2:
                  case 4:
                  case 8:
                  case 9:
                  case 10:
                  default:
                     break;
                  case 3:
                     if ((576460743847706622L & l) != 0L) {
                        this.jjAddStates(4, 6);
                     }
                     break;
                  case 5:
                     if (this.curChar == 93 && kind > 155) {
                        kind = 155;
                     }
                     break;
                  case 6:
                     if ((-134217729L & l) != 0L) {
                        if (kind > 156) {
                           kind = 156;
                        }

                        this.jjCheckNAdd(6);
                     }
                     break;
                  case 7:
                     if (this.curChar == 91 && kind > 157) {
                        kind = 157;
                     }
                     break;
                  case 11:
                     if (this.curChar == 93 && kind > 154) {
                        kind = 154;
                     }
               }
            } while(i != startsAt);
         } else {
            int hiByte = this.curChar >> 8;
            int i1 = hiByte >> 6;
            long l1 = 1L << (hiByte & 63);
            int i2 = (this.curChar & 255) >> 6;
            long l2 = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 0:
                  case 6:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        if (kind > 156) {
                           kind = 156;
                        }

                        this.jjCheckNAdd(6);
                     }
                     break;
                  default:
                     if (i1 != 0 && l1 != 0L && i2 != 0 && l2 == 0L) {
                     }
               }
            } while(i != startsAt);
         }

         if (kind != Integer.MAX_VALUE) {
            this.jjmatchedKind = kind;
            this.jjmatchedPos = curPos;
            kind = Integer.MAX_VALUE;
         }

         ++curPos;
         if ((i = this.jjnewStateCnt) == (startsAt = 13 - (this.jjnewStateCnt = startsAt))) {
            return curPos;
         }

         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var13) {
            return curPos;
         }
      }
   }

   private final int jjStopStringLiteralDfa_0(int pos, long active0, long active1) {
      switch (pos) {
         case 0:
            if ((active1 & 1048576L) != 0L) {
               this.jjmatchedKind = 81;
               return 697;
            } else {
               if ((active1 & 786432L) != 0L) {
                  this.jjmatchedKind = 81;
                  return -1;
               }

               return -1;
            }
         default:
            return -1;
      }
   }

   private final int jjStartNfa_0(int pos, long active0, long active1) {
      return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0, active1), pos + 1);
   }

   private int jjStopAtPos(int pos, int kind) {
      this.jjmatchedKind = kind;
      this.jjmatchedPos = pos;
      return pos + 1;
   }

   private int jjMoveStringLiteralDfa0_0() {
      switch (this.curChar) {
         case 35:
            return this.jjMoveStringLiteralDfa1_0(524288L);
         case 36:
            return this.jjMoveStringLiteralDfa1_0(262144L);
         case 91:
            return this.jjMoveStringLiteralDfa1_0(1048576L);
         default:
            return this.jjMoveNfa_0(2, 0);
      }
   }

   private int jjMoveStringLiteralDfa1_0(long active1) {
      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var4) {
         this.jjStopStringLiteralDfa_0(0, 0L, active1);
         return 1;
      }

      switch (this.curChar) {
         case 61:
            if ((active1 & 1048576L) != 0L) {
               return this.jjStopAtPos(1, 84);
            }
            break;
         case 123:
            if ((active1 & 262144L) != 0L) {
               return this.jjStopAtPos(1, 82);
            }

            if ((active1 & 524288L) != 0L) {
               return this.jjStopAtPos(1, 83);
            }
      }

      return this.jjStartNfa_0(0, 0L, active1);
   }

   private int jjMoveNfa_0(int startState, int curPos) {
      int startsAt = 0;
      this.jjnewStateCnt = 713;
      int i = 1;
      this.jjstateSet[0] = startState;
      int kind = Integer.MAX_VALUE;

      while(true) {
         if (++this.jjround == Integer.MAX_VALUE) {
            this.ReInitRounds();
         }

         long l;
         if (this.curChar < 64) {
            l = 1L << this.curChar;

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 0:
                     if ((4294977024L & l) != 0L) {
                        if (kind > 79) {
                           kind = 79;
                        }

                        this.jjCheckNAdd(0);
                     }
                     break;
                  case 1:
                     if ((-1152921611981039105L & l) != 0L) {
                        if (kind > 80) {
                           kind = 80;
                        }

                        this.jjCheckNAdd(1);
                     }
                     break;
                  case 2:
                     if ((-1152921611981039105L & l) != 0L) {
                        if (kind > 80) {
                           kind = 80;
                        }

                        this.jjCheckNAdd(1);
                     } else if ((4294977024L & l) != 0L) {
                        if (kind > 79) {
                           kind = 79;
                        }

                        this.jjCheckNAdd(0);
                     } else if ((1152921607686062080L & l) != 0L && kind > 81) {
                        kind = 81;
                     }

                     if (this.curChar == 60) {
                        this.jjAddStates(7, 8);
                     }

                     if (this.curChar == 60) {
                        this.jjCheckNAddStates(9, 100);
                     }

                     if (this.curChar == 60) {
                        this.jjCheckNAddStates(101, 147);
                     }
                     break;
                  case 3:
                     if (this.curChar == 60) {
                        this.jjCheckNAddStates(101, 147);
                     }
                  case 4:
                  case 7:
                  case 8:
                  case 9:
                  case 10:
                  case 11:
                  case 12:
                  case 13:
                  case 16:
                  case 17:
                  case 18:
                  case 19:
                  case 20:
                  case 21:
                  case 22:
                  case 24:
                  case 25:
                  case 26:
                  case 27:
                  case 29:
                  case 30:
                  case 31:
                  case 32:
                  case 34:
                  case 35:
                  case 36:
                  case 37:
                  case 39:
                  case 41:
                  case 42:
                  case 43:
                  case 44:
                  case 45:
                  case 46:
                  case 49:
                  case 50:
                  case 51:
                  case 52:
                  case 53:
                  case 55:
                  case 56:
                  case 57:
                  case 58:
                  case 59:
                  case 61:
                  case 62:
                  case 63:
                  case 64:
                  case 65:
                  case 66:
                  case 68:
                  case 69:
                  case 70:
                  case 71:
                  case 73:
                  case 74:
                  case 75:
                  case 76:
                  case 77:
                  case 78:
                  case 80:
                  case 81:
                  case 82:
                  case 83:
                  case 84:
                  case 85:
                  case 87:
                  case 88:
                  case 89:
                  case 90:
                  case 91:
                  case 93:
                  case 94:
                  case 95:
                  case 96:
                  case 97:
                  case 98:
                  case 99:
                  case 101:
                  case 102:
                  case 103:
                  case 104:
                  case 105:
                  case 106:
                  case 108:
                  case 109:
                  case 110:
                  case 111:
                  case 112:
                  case 113:
                  case 114:
                  case 115:
                  case 117:
                  case 118:
                  case 119:
                  case 120:
                  case 121:
                  case 123:
                  case 124:
                  case 125:
                  case 126:
                  case 127:
                  case 128:
                  case 129:
                  case 130:
                  case 131:
                  case 133:
                  case 134:
                  case 135:
                  case 136:
                  case 137:
                  case 139:
                  case 140:
                  case 141:
                  case 142:
                  case 144:
                  case 145:
                  case 146:
                  case 147:
                  case 148:
                  case 149:
                  case 151:
                  case 152:
                  case 153:
                  case 154:
                  case 156:
                  case 157:
                  case 158:
                  case 159:
                  case 160:
                  case 161:
                  case 162:
                  case 163:
                  case 164:
                  case 166:
                  case 167:
                  case 168:
                  case 169:
                  case 170:
                  case 171:
                  case 172:
                  case 173:
                  case 174:
                  case 175:
                  case 176:
                  case 177:
                  case 180:
                  case 181:
                  case 182:
                  case 183:
                  case 184:
                  case 185:
                  case 186:
                  case 189:
                  case 190:
                  case 191:
                  case 192:
                  case 193:
                  case 194:
                  case 195:
                  case 196:
                  case 197:
                  case 198:
                  case 199:
                  case 200:
                  case 203:
                  case 204:
                  case 205:
                  case 206:
                  case 207:
                  case 208:
                  case 209:
                  case 210:
                  case 213:
                  case 214:
                  case 215:
                  case 216:
                  case 217:
                  case 218:
                  case 219:
                  case 220:
                  case 221:
                  case 224:
                  case 225:
                  case 226:
                  case 227:
                  case 228:
                  case 232:
                  case 233:
                  case 234:
                  case 235:
                  case 239:
                  case 240:
                  case 241:
                  case 242:
                  case 243:
                  case 247:
                  case 248:
                  case 249:
                  case 250:
                  case 251:
                  case 252:
                  case 253:
                  case 254:
                  case 258:
                  case 259:
                  case 260:
                  case 261:
                  case 262:
                  case 263:
                  case 267:
                  case 268:
                  case 269:
                  case 270:
                  case 274:
                  case 275:
                  case 276:
                  case 277:
                  case 278:
                  case 282:
                  case 286:
                  case 287:
                  case 291:
                  case 292:
                  case 296:
                  case 297:
                  case 300:
                  case 301:
                  case 302:
                  case 303:
                  case 304:
                  case 305:
                  case 306:
                  case 310:
                  case 311:
                  case 312:
                  case 313:
                  case 314:
                  case 315:
                  case 317:
                  case 318:
                  case 319:
                  case 320:
                  case 321:
                  case 322:
                  case 326:
                  case 327:
                  case 328:
                  case 329:
                  case 330:
                  case 331:
                  case 332:
                  case 334:
                  case 335:
                  case 336:
                  case 337:
                  case 338:
                  case 339:
                  case 340:
                  case 344:
                  case 345:
                  case 346:
                  case 347:
                  case 348:
                  case 349:
                  case 350:
                  case 351:
                  case 353:
                  case 354:
                  case 355:
                  case 356:
                  case 357:
                  case 358:
                  case 359:
                  case 360:
                  case 363:
                  case 364:
                  case 365:
                  case 366:
                  case 367:
                  case 399:
                  case 402:
                  case 406:
                  case 409:
                  case 410:
                  case 411:
                  case 415:
                  case 418:
                  case 419:
                  case 420:
                  case 421:
                  case 425:
                  case 428:
                  case 429:
                  case 433:
                  case 436:
                  case 437:
                  case 438:
                  case 439:
                  case 440:
                  case 441:
                  case 445:
                  case 448:
                  case 449:
                  case 450:
                  case 451:
                  case 452:
                  case 453:
                  case 457:
                  case 458:
                  case 459:
                  case 462:
                  case 463:
                  case 464:
                  case 465:
                  case 469:
                  case 472:
                  case 473:
                  case 474:
                  case 475:
                  case 479:
                  case 482:
                  case 483:
                  case 484:
                  case 485:
                  case 486:
                  case 490:
                  case 493:
                  case 494:
                  case 495:
                  case 496:
                  case 497:
                  case 501:
                  case 504:
                  case 505:
                  case 506:
                  case 507:
                  case 508:
                  case 509:
                  case 510:
                  case 514:
                  case 517:
                  case 518:
                  case 519:
                  case 520:
                  case 524:
                  case 525:
                  case 526:
                  case 529:
                  case 530:
                  case 531:
                  case 532:
                  case 533:
                  case 534:
                  case 535:
                  case 536:
                  case 537:
                  case 541:
                  case 542:
                  case 543:
                  case 546:
                  case 547:
                  case 548:
                  case 549:
                  case 553:
                  case 554:
                  case 555:
                  case 558:
                  case 559:
                  case 560:
                  case 561:
                  case 562:
                  case 563:
                  case 564:
                  case 565:
                  case 566:
                  case 567:
                  case 568:
                  case 572:
                  case 575:
                  case 576:
                  case 577:
                  case 578:
                  case 579:
                  case 580:
                  case 581:
                  case 585:
                  case 588:
                  case 589:
                  case 590:
                  case 591:
                  case 592:
                  case 593:
                  case 594:
                  case 595:
                  case 599:
                  case 602:
                  case 603:
                  case 604:
                  case 605:
                  case 606:
                  case 627:
                  case 630:
                  case 631:
                  case 632:
                  case 633:
                  case 634:
                  case 639:
                  case 640:
                  case 641:
                  case 644:
                  case 645:
                  case 646:
                  case 647:
                  case 648:
                  case 651:
                  case 652:
                  case 654:
                  case 655:
                  case 657:
                  case 660:
                  case 661:
                  case 664:
                  case 666:
                  case 687:
                  case 688:
                  case 690:
                  case 691:
                  case 698:
                  case 701:
                  case 706:
                  case 710:
                  case 711:
                  default:
                     break;
                  case 5:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(148, 149);
                     }
                     break;
                  case 6:
                     if (this.curChar == 62 && kind > 6) {
                        kind = 6;
                     }
                     break;
                  case 14:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(150, 151);
                     }
                     break;
                  case 15:
                     if (this.curChar == 62 && kind > 7) {
                        kind = 7;
                     }
                     break;
                  case 23:
                     if ((4294977024L & l) != 0L && kind > 8) {
                        kind = 8;
                     }
                     break;
                  case 28:
                     if ((4294977024L & l) != 0L && kind > 9) {
                        kind = 9;
                     }
                     break;
                  case 33:
                     if ((4294977024L & l) != 0L && kind > 10) {
                        kind = 10;
                     }
                     break;
                  case 38:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(152, 153);
                     }
                     break;
                  case 40:
                     if ((4294977024L & l) != 0L && kind > 11) {
                        kind = 11;
                     }
                     break;
                  case 47:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(154, 155);
                     }
                     break;
                  case 48:
                     if (this.curChar == 62 && kind > 12) {
                        kind = 12;
                     }
                     break;
                  case 54:
                     if ((4294977024L & l) != 0L && kind > 13) {
                        kind = 13;
                     }
                     break;
                  case 60:
                     if ((4294977024L & l) != 0L && kind > 14) {
                        kind = 14;
                     }
                     break;
                  case 67:
                     if ((4294977024L & l) != 0L && kind > 15) {
                        kind = 15;
                     }
                     break;
                  case 72:
                     if ((4294977024L & l) != 0L && kind > 16) {
                        kind = 16;
                     }
                     break;
                  case 79:
                     if ((4294977024L & l) != 0L && kind > 17) {
                        kind = 17;
                     }
                     break;
                  case 86:
                     if ((4294977024L & l) != 0L && kind > 18) {
                        kind = 18;
                     }
                     break;
                  case 92:
                     if ((4294977024L & l) != 0L && kind > 19) {
                        kind = 19;
                     }
                     break;
                  case 100:
                     if ((4294977024L & l) != 0L && kind > 20) {
                        kind = 20;
                     }
                     break;
                  case 107:
                     if ((4294977024L & l) != 0L && kind > 21) {
                        kind = 21;
                     }
                     break;
                  case 116:
                     if ((4294977024L & l) != 0L && kind > 22) {
                        kind = 22;
                     }
                     break;
                  case 122:
                     if ((4294977024L & l) != 0L && kind > 23) {
                        kind = 23;
                     }
                     break;
                  case 132:
                     if ((4294977024L & l) != 0L && kind > 24) {
                        kind = 24;
                     }
                     break;
                  case 138:
                     if ((4294977024L & l) != 0L && kind > 25) {
                        kind = 25;
                     }
                     break;
                  case 143:
                     if ((4294977024L & l) != 0L && kind > 26) {
                        kind = 26;
                     }
                     break;
                  case 150:
                     if ((4294977024L & l) != 0L && kind > 27) {
                        kind = 27;
                     }
                     break;
                  case 155:
                     if ((4294977024L & l) != 0L && kind > 28) {
                        kind = 28;
                     }
                     break;
                  case 165:
                     if ((4294977024L & l) != 0L && kind > 29) {
                        kind = 29;
                     }
                     break;
                  case 178:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(156, 157);
                     }
                     break;
                  case 179:
                     if (this.curChar == 62 && kind > 30) {
                        kind = 30;
                     }
                     break;
                  case 187:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(158, 159);
                     }
                     break;
                  case 188:
                     if (this.curChar == 62 && kind > 31) {
                        kind = 31;
                     }
                     break;
                  case 201:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(160, 161);
                     }
                     break;
                  case 202:
                     if (this.curChar == 62 && kind > 32) {
                        kind = 32;
                     }
                     break;
                  case 211:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(162, 163);
                     }
                     break;
                  case 212:
                     if (this.curChar == 62 && kind > 33) {
                        kind = 33;
                     }
                     break;
                  case 222:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(164, 165);
                     }
                     break;
                  case 223:
                     if (this.curChar == 62 && kind > 35) {
                        kind = 35;
                     }
                     break;
                  case 229:
                     if ((4294977024L & l) != 0L) {
                        this.jjCheckNAddStates(166, 168);
                     }
                     break;
                  case 230:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(231);
                     }
                     break;
                  case 231:
                     if (this.curChar == 62 && kind > 54) {
                        kind = 54;
                     }
                     break;
                  case 236:
                     if ((4294977024L & l) != 0L) {
                        this.jjCheckNAddStates(169, 171);
                     }
                     break;
                  case 237:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(238);
                     }
                     break;
                  case 238:
                     if (this.curChar == 62 && kind > 55) {
                        kind = 55;
                     }
                     break;
                  case 244:
                     if ((4294977024L & l) != 0L) {
                        this.jjCheckNAddStates(172, 174);
                     }
                     break;
                  case 245:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(246);
                     }
                     break;
                  case 246:
                     if (this.curChar == 62 && kind > 56) {
                        kind = 56;
                     }
                     break;
                  case 255:
                     if ((4294977024L & l) != 0L) {
                        this.jjCheckNAddStates(175, 177);
                     }
                     break;
                  case 256:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(257);
                     }
                     break;
                  case 257:
                     if (this.curChar == 62 && kind > 57) {
                        kind = 57;
                     }
                     break;
                  case 264:
                     if ((4294977024L & l) != 0L) {
                        this.jjCheckNAddStates(178, 180);
                     }
                     break;
                  case 265:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(266);
                     }
                     break;
                  case 266:
                     if (this.curChar == 62 && kind > 58) {
                        kind = 58;
                     }
                     break;
                  case 271:
                     if ((4294977024L & l) != 0L) {
                        this.jjCheckNAddStates(181, 183);
                     }
                     break;
                  case 272:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(273);
                     }
                     break;
                  case 273:
                     if (this.curChar == 62 && kind > 59) {
                        kind = 59;
                     }
                     break;
                  case 279:
                     if ((4294977024L & l) != 0L) {
                        this.jjCheckNAddStates(184, 186);
                     }
                     break;
                  case 280:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(281);
                     }
                     break;
                  case 281:
                     if (this.curChar == 62 && kind > 60) {
                        kind = 60;
                     }
                     break;
                  case 283:
                     if ((4294977024L & l) != 0L) {
                        this.jjCheckNAddStates(187, 189);
                     }
                     break;
                  case 284:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(285);
                     }
                     break;
                  case 285:
                     if (this.curChar == 62 && kind > 61) {
                        kind = 61;
                     }
                     break;
                  case 288:
                     if ((4294977024L & l) != 0L) {
                        this.jjCheckNAddStates(190, 192);
                     }
                     break;
                  case 289:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(290);
                     }
                     break;
                  case 290:
                     if (this.curChar == 62 && kind > 62) {
                        kind = 62;
                     }
                     break;
                  case 293:
                     if ((4294977024L & l) != 0L) {
                        this.jjCheckNAddStates(193, 195);
                     }
                     break;
                  case 294:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(295);
                     }
                     break;
                  case 295:
                     if (this.curChar == 62 && kind > 63) {
                        kind = 63;
                     }
                     break;
                  case 298:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(196, 197);
                     }
                     break;
                  case 299:
                     if (this.curChar == 62 && kind > 64) {
                        kind = 64;
                     }
                     break;
                  case 307:
                     if ((4294977024L & l) != 0L) {
                        this.jjCheckNAddStates(198, 200);
                     }
                     break;
                  case 308:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(309);
                     }
                     break;
                  case 309:
                     if (this.curChar == 62 && kind > 65) {
                        kind = 65;
                     }
                     break;
                  case 316:
                     if ((4294977024L & l) != 0L && kind > 66) {
                        kind = 66;
                     }
                     break;
                  case 323:
                     if ((4294977024L & l) != 0L) {
                        this.jjCheckNAddStates(201, 203);
                     }
                     break;
                  case 324:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(325);
                     }
                     break;
                  case 325:
                     if (this.curChar == 62 && kind > 67) {
                        kind = 67;
                     }
                     break;
                  case 333:
                     if ((4294977024L & l) != 0L && kind > 68) {
                        kind = 68;
                     }
                     break;
                  case 341:
                     if ((4294977024L & l) != 0L) {
                        this.jjCheckNAddStates(204, 206);
                     }
                     break;
                  case 342:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(343);
                     }
                     break;
                  case 343:
                     if (this.curChar == 62 && kind > 69) {
                        kind = 69;
                     }
                     break;
                  case 352:
                     if ((4294977024L & l) != 0L && kind > 70) {
                        kind = 70;
                     }
                     break;
                  case 361:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(207, 208);
                     }
                     break;
                  case 362:
                     if (this.curChar == 62 && kind > 72) {
                        kind = 72;
                     }
                     break;
                  case 368:
                     if (this.curChar == 60) {
                        this.jjCheckNAddStates(9, 100);
                     }
                     break;
                  case 369:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(12);
                     }
                     break;
                  case 370:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(21);
                     }
                     break;
                  case 371:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(24);
                     }
                     break;
                  case 372:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(31);
                     }
                     break;
                  case 373:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(36);
                     }
                     break;
                  case 374:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(45);
                     }
                     break;
                  case 375:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(50);
                     }
                     break;
                  case 376:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(58);
                     }
                     break;
                  case 377:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(65);
                     }
                     break;
                  case 378:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(70);
                     }
                     break;
                  case 379:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(77);
                     }
                     break;
                  case 380:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(84);
                     }
                     break;
                  case 381:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(90);
                     }
                     break;
                  case 382:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(98);
                     }
                     break;
                  case 383:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(105);
                     }
                     break;
                  case 384:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(114);
                     }
                     break;
                  case 385:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(120);
                     }
                     break;
                  case 386:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(130);
                     }
                     break;
                  case 387:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(136);
                     }
                     break;
                  case 388:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(141);
                     }
                     break;
                  case 389:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(148);
                     }
                     break;
                  case 390:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(153);
                     }
                     break;
                  case 391:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(161);
                     }
                     break;
                  case 392:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(174);
                     }
                     break;
                  case 393:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(183);
                     }
                     break;
                  case 394:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(199);
                     }
                     break;
                  case 395:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(209);
                     }
                     break;
                  case 396:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(218);
                     }
                     break;
                  case 397:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(227);
                     }
                     break;
                  case 398:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(402);
                     }
                     break;
                  case 400:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(209, 210);
                     }
                     break;
                  case 401:
                     if (this.curChar == 62 && kind > 36) {
                        kind = 36;
                     }
                     break;
                  case 403:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(402);
                     }
                     break;
                  case 404:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(403);
                     }
                     break;
                  case 405:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(411);
                     }
                     break;
                  case 407:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(211, 212);
                     }
                     break;
                  case 408:
                     if (this.curChar == 62 && kind > 37) {
                        kind = 37;
                     }
                     break;
                  case 412:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(411);
                     }
                     break;
                  case 413:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(412);
                     }
                     break;
                  case 414:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(421);
                     }
                     break;
                  case 416:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(213, 214);
                     }
                     break;
                  case 417:
                     if (this.curChar == 62 && kind > 38) {
                        kind = 38;
                     }
                     break;
                  case 422:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(421);
                     }
                     break;
                  case 423:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(422);
                     }
                     break;
                  case 424:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(429);
                     }
                     break;
                  case 426:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(215, 216);
                     }
                     break;
                  case 427:
                     if (this.curChar == 62 && kind > 39) {
                        kind = 39;
                     }
                     break;
                  case 430:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(429);
                     }
                     break;
                  case 431:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(430);
                     }
                     break;
                  case 432:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(441);
                     }
                     break;
                  case 434:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(217, 218);
                     }
                     break;
                  case 435:
                     if (this.curChar == 62 && kind > 40) {
                        kind = 40;
                     }
                     break;
                  case 442:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(441);
                     }
                     break;
                  case 443:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(442);
                     }
                     break;
                  case 444:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(453);
                     }
                     break;
                  case 446:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(219, 220);
                     }
                     break;
                  case 447:
                     if (this.curChar == 62 && kind > 41) {
                        kind = 41;
                     }
                     break;
                  case 454:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(453);
                     }
                     break;
                  case 455:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(454);
                     }
                     break;
                  case 456:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(465);
                     }
                     break;
                  case 460:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(221, 222);
                     }
                     break;
                  case 461:
                     if (this.curChar == 62 && kind > 42) {
                        kind = 42;
                     }
                     break;
                  case 466:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(465);
                     }
                     break;
                  case 467:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(466);
                     }
                     break;
                  case 468:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(475);
                     }
                     break;
                  case 470:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(223, 224);
                     }
                     break;
                  case 471:
                     if (this.curChar == 62 && kind > 43) {
                        kind = 43;
                     }
                     break;
                  case 476:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(475);
                     }
                     break;
                  case 477:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(476);
                     }
                     break;
                  case 478:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(486);
                     }
                     break;
                  case 480:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(225, 226);
                     }
                     break;
                  case 481:
                     if (this.curChar == 62 && kind > 44) {
                        kind = 44;
                     }
                     break;
                  case 487:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(486);
                     }
                     break;
                  case 488:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(487);
                     }
                     break;
                  case 489:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(497);
                     }
                     break;
                  case 491:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(227, 228);
                     }
                     break;
                  case 492:
                     if (this.curChar == 62 && kind > 45) {
                        kind = 45;
                     }
                     break;
                  case 498:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(497);
                     }
                     break;
                  case 499:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(498);
                     }
                     break;
                  case 500:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(510);
                     }
                     break;
                  case 502:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(229, 230);
                     }
                     break;
                  case 503:
                     if (this.curChar == 62 && kind > 46) {
                        kind = 46;
                     }
                     break;
                  case 511:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(510);
                     }
                     break;
                  case 512:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(511);
                     }
                     break;
                  case 513:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(520);
                     }
                     break;
                  case 515:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(231, 232);
                     }
                     break;
                  case 516:
                     if (this.curChar == 62 && kind > 47) {
                        kind = 47;
                     }
                     break;
                  case 521:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(520);
                     }
                     break;
                  case 522:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(521);
                     }
                     break;
                  case 523:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(537);
                     }
                     break;
                  case 527:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(233, 234);
                     }
                     break;
                  case 528:
                     if (this.curChar == 62 && kind > 48) {
                        kind = 48;
                     }
                     break;
                  case 538:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(537);
                     }
                     break;
                  case 539:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(538);
                     }
                     break;
                  case 540:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(549);
                     }
                     break;
                  case 544:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(235, 236);
                     }
                     break;
                  case 545:
                     if (this.curChar == 62 && kind > 49) {
                        kind = 49;
                     }
                     break;
                  case 550:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(549);
                     }
                     break;
                  case 551:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(550);
                     }
                     break;
                  case 552:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(568);
                     }
                     break;
                  case 556:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(237, 238);
                     }
                     break;
                  case 557:
                     if (this.curChar == 62 && kind > 50) {
                        kind = 50;
                     }
                     break;
                  case 569:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(568);
                     }
                     break;
                  case 570:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(569);
                     }
                     break;
                  case 571:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(581);
                     }
                     break;
                  case 573:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(239, 240);
                     }
                     break;
                  case 574:
                     if (this.curChar == 62 && kind > 51) {
                        kind = 51;
                     }
                     break;
                  case 582:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(581);
                     }
                     break;
                  case 583:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(582);
                     }
                     break;
                  case 584:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(595);
                     }
                     break;
                  case 586:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(241, 242);
                     }
                     break;
                  case 587:
                     if (this.curChar == 62 && kind > 52) {
                        kind = 52;
                     }
                     break;
                  case 596:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(595);
                     }
                     break;
                  case 597:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(596);
                     }
                     break;
                  case 598:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(606);
                     }
                     break;
                  case 600:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(243, 244);
                     }
                     break;
                  case 601:
                     if (this.curChar == 62 && kind > 53) {
                        kind = 53;
                     }
                     break;
                  case 607:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(606);
                     }
                     break;
                  case 608:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(607);
                     }
                     break;
                  case 609:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(234);
                     }
                     break;
                  case 610:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(242);
                     }
                     break;
                  case 611:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(253);
                     }
                     break;
                  case 612:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(262);
                     }
                     break;
                  case 613:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(269);
                     }
                     break;
                  case 614:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(277);
                     }
                     break;
                  case 615:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(278);
                     }
                     break;
                  case 616:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(286);
                     }
                     break;
                  case 617:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(291);
                     }
                     break;
                  case 618:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(296);
                     }
                     break;
                  case 619:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(305);
                     }
                     break;
                  case 620:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(314);
                     }
                     break;
                  case 621:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(321);
                     }
                     break;
                  case 622:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(331);
                     }
                     break;
                  case 623:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(339);
                     }
                     break;
                  case 624:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(350);
                     }
                     break;
                  case 625:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(357);
                     }
                     break;
                  case 626:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(634);
                     }
                     break;
                  case 628:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(245, 246);
                     }
                     break;
                  case 629:
                     if (this.curChar == 62 && kind > 71) {
                        kind = 71;
                     }
                     break;
                  case 635:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(634);
                     }
                     break;
                  case 636:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(635);
                     }
                     break;
                  case 637:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(367);
                     }
                     break;
                  case 638:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(648);
                     }
                     break;
                  case 642:
                     if ((4294977024L & l) != 0L) {
                        this.jjAddStates(247, 248);
                     }
                     break;
                  case 643:
                     if (this.curChar == 62 && kind > 73) {
                        kind = 73;
                     }
                     break;
                  case 649:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(648);
                     }
                     break;
                  case 650:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(649);
                     }
                     break;
                  case 653:
                     if ((4294977024L & l) != 0L && kind > 76) {
                        kind = 76;
                     }
                     break;
                  case 656:
                     if (this.curChar == 35) {
                        this.jjstateSet[this.jjnewStateCnt++] = 655;
                     }
                     break;
                  case 658:
                     if (this.curChar == 47) {
                        this.jjstateSet[this.jjnewStateCnt++] = 659;
                     }
                     break;
                  case 659:
                     if (this.curChar == 62 && kind > 77) {
                        kind = 77;
                     }
                     break;
                  case 662:
                     if (this.curChar == 35) {
                        this.jjstateSet[this.jjnewStateCnt++] = 661;
                     }
                     break;
                  case 663:
                     if (this.curChar == 35) {
                        this.jjstateSet[this.jjnewStateCnt++] = 664;
                     }
                     break;
                  case 665:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(663);
                     }
                     break;
                  case 667:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(403);
                     }
                     break;
                  case 668:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(412);
                     }
                     break;
                  case 669:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(422);
                     }
                     break;
                  case 670:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(430);
                     }
                     break;
                  case 671:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(442);
                     }
                     break;
                  case 672:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(454);
                     }
                     break;
                  case 673:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(466);
                     }
                     break;
                  case 674:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(476);
                     }
                     break;
                  case 675:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(487);
                     }
                     break;
                  case 676:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(498);
                     }
                     break;
                  case 677:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(511);
                     }
                     break;
                  case 678:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(521);
                     }
                     break;
                  case 679:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(538);
                     }
                     break;
                  case 680:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(550);
                     }
                     break;
                  case 681:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(569);
                     }
                     break;
                  case 682:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(582);
                     }
                     break;
                  case 683:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(596);
                     }
                     break;
                  case 684:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(607);
                     }
                     break;
                  case 685:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(635);
                     }
                     break;
                  case 686:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(649);
                     }
                     break;
                  case 689:
                     if (this.curChar == 35) {
                        this.jjstateSet[this.jjnewStateCnt++] = 688;
                     }
                     break;
                  case 692:
                     if (this.curChar == 35) {
                        this.jjstateSet[this.jjnewStateCnt++] = 691;
                     }
                     break;
                  case 693:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(663);
                     }
                     break;
                  case 694:
                     if (this.curChar == 60) {
                        this.jjAddStates(7, 8);
                     }
                     break;
                  case 695:
                     if (this.curChar == 45 && kind > 34) {
                        kind = 34;
                     }
                     break;
                  case 696:
                     if (this.curChar == 45) {
                        this.jjstateSet[this.jjnewStateCnt++] = 695;
                     }
                     break;
                  case 697:
                     if (this.curChar == 47) {
                        this.jjCheckNAdd(663);
                     } else if (this.curChar == 35) {
                        this.jjstateSet[this.jjnewStateCnt++] = 664;
                     }

                     if (this.curChar == 35) {
                        this.jjstateSet[this.jjnewStateCnt++] = 691;
                     } else if (this.curChar == 47) {
                        this.jjstateSet[this.jjnewStateCnt++] = 698;
                     }

                     if (this.curChar == 35) {
                        this.jjstateSet[this.jjnewStateCnt++] = 688;
                     } else if (this.curChar == 47) {
                        this.jjCheckNAdd(649);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(367);
                     } else if (this.curChar == 47) {
                        this.jjCheckNAdd(635);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(357);
                     } else if (this.curChar == 47) {
                        this.jjCheckNAdd(607);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(350);
                     } else if (this.curChar == 47) {
                        this.jjCheckNAdd(596);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(339);
                     } else if (this.curChar == 47) {
                        this.jjCheckNAdd(582);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(331);
                     } else if (this.curChar == 47) {
                        this.jjCheckNAdd(569);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(321);
                     } else if (this.curChar == 47) {
                        this.jjCheckNAdd(550);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(314);
                     } else if (this.curChar == 47) {
                        this.jjCheckNAdd(538);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(305);
                     } else if (this.curChar == 47) {
                        this.jjCheckNAdd(521);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(296);
                     } else if (this.curChar == 47) {
                        this.jjCheckNAdd(511);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(291);
                     } else if (this.curChar == 47) {
                        this.jjCheckNAdd(498);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(286);
                     } else if (this.curChar == 47) {
                        this.jjCheckNAdd(487);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(278);
                     } else if (this.curChar == 47) {
                        this.jjCheckNAdd(476);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(277);
                     } else if (this.curChar == 47) {
                        this.jjCheckNAdd(466);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(269);
                     } else if (this.curChar == 47) {
                        this.jjCheckNAdd(454);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(262);
                     } else if (this.curChar == 47) {
                        this.jjCheckNAdd(442);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(253);
                     } else if (this.curChar == 47) {
                        this.jjCheckNAdd(430);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(242);
                     } else if (this.curChar == 47) {
                        this.jjCheckNAdd(422);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(234);
                     } else if (this.curChar == 47) {
                        this.jjCheckNAdd(412);
                     }

                     if (this.curChar == 47) {
                        this.jjCheckNAdd(403);
                     } else if (this.curChar == 35) {
                        this.jjCheckNAdd(227);
                     }

                     if (this.curChar == 35) {
                        this.jjstateSet[this.jjnewStateCnt++] = 696;
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(218);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(209);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(199);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(183);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(174);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(161);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(153);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(148);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(141);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(136);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(130);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(120);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(114);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(105);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(98);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(90);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(84);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(77);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(70);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(65);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(58);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(50);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(45);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(36);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(31);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(24);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(21);
                     }

                     if (this.curChar == 35) {
                        this.jjCheckNAdd(12);
                     }
                     break;
                  case 699:
                     if (this.curChar == 36) {
                        this.jjCheckNAddStates(249, 253);
                     }
                     break;
                  case 700:
                     if ((287948969894477824L & l) != 0L) {
                        this.jjCheckNAddStates(249, 253);
                     }
                     break;
                  case 702:
                     if ((288335963627716608L & l) != 0L) {
                        this.jjCheckNAddStates(249, 253);
                     }
                     break;
                  case 703:
                     if (this.curChar == 46) {
                        this.jjAddStates(254, 255);
                     }
                     break;
                  case 704:
                     if (this.curChar == 36) {
                        this.jjCheckNAddStates(256, 260);
                     }
                     break;
                  case 705:
                     if ((287948969894477824L & l) != 0L) {
                        this.jjCheckNAddStates(256, 260);
                     }
                     break;
                  case 707:
                     if ((288335963627716608L & l) != 0L) {
                        this.jjCheckNAddStates(256, 260);
                     }
                     break;
                  case 708:
                     if ((4294977024L & l) != 0L) {
                        this.jjCheckNAddTwoStates(708, 709);
                     }
                     break;
                  case 709:
                     if (this.curChar == 62 && kind > 75) {
                        kind = 75;
                     }
                     break;
                  case 712:
                     if (this.curChar == 47) {
                        this.jjstateSet[this.jjnewStateCnt++] = 698;
                     }
               }
            } while(i != startsAt);
         } else if (this.curChar < 128) {
            l = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 1:
                     if ((-576460752437641217L & l) != 0L) {
                        if (kind > 80) {
                           kind = 80;
                        }

                        this.jjCheckNAdd(1);
                     }
                     break;
                  case 2:
                     if ((-576460752437641217L & l) != 0L) {
                        if (kind > 80) {
                           kind = 80;
                        }

                        this.jjCheckNAdd(1);
                     } else if ((576460752437641216L & l) != 0L && kind > 81) {
                        kind = 81;
                     }

                     if (this.curChar == 91) {
                        this.jjAddStates(7, 8);
                     }

                     if (this.curChar == 91) {
                        this.jjAddStates(261, 332);
                     }
                  case 3:
                  case 5:
                  case 14:
                  case 23:
                  case 28:
                  case 33:
                  case 38:
                  case 40:
                  case 47:
                  case 54:
                  case 60:
                  case 67:
                  case 72:
                  case 79:
                  case 86:
                  case 92:
                  case 100:
                  case 107:
                  case 116:
                  case 122:
                  case 132:
                  case 138:
                  case 143:
                  case 150:
                  case 155:
                  case 165:
                  case 178:
                  case 187:
                  case 201:
                  case 211:
                  case 222:
                  case 229:
                  case 230:
                  case 236:
                  case 237:
                  case 244:
                  case 245:
                  case 255:
                  case 256:
                  case 264:
                  case 265:
                  case 271:
                  case 272:
                  case 279:
                  case 280:
                  case 283:
                  case 284:
                  case 288:
                  case 289:
                  case 293:
                  case 294:
                  case 298:
                  case 307:
                  case 308:
                  case 316:
                  case 323:
                  case 324:
                  case 333:
                  case 341:
                  case 342:
                  case 352:
                  case 361:
                  case 368:
                  case 369:
                  case 370:
                  case 371:
                  case 372:
                  case 373:
                  case 374:
                  case 375:
                  case 376:
                  case 377:
                  case 378:
                  case 379:
                  case 380:
                  case 381:
                  case 382:
                  case 383:
                  case 384:
                  case 385:
                  case 386:
                  case 387:
                  case 388:
                  case 389:
                  case 390:
                  case 391:
                  case 392:
                  case 393:
                  case 394:
                  case 395:
                  case 396:
                  case 397:
                  case 398:
                  case 400:
                  case 403:
                  case 404:
                  case 405:
                  case 407:
                  case 412:
                  case 413:
                  case 414:
                  case 416:
                  case 422:
                  case 423:
                  case 424:
                  case 426:
                  case 430:
                  case 431:
                  case 432:
                  case 434:
                  case 442:
                  case 443:
                  case 444:
                  case 446:
                  case 454:
                  case 455:
                  case 456:
                  case 460:
                  case 466:
                  case 467:
                  case 468:
                  case 470:
                  case 476:
                  case 477:
                  case 478:
                  case 480:
                  case 487:
                  case 488:
                  case 489:
                  case 491:
                  case 498:
                  case 499:
                  case 500:
                  case 502:
                  case 511:
                  case 512:
                  case 513:
                  case 515:
                  case 521:
                  case 522:
                  case 523:
                  case 527:
                  case 538:
                  case 539:
                  case 540:
                  case 544:
                  case 550:
                  case 551:
                  case 552:
                  case 556:
                  case 569:
                  case 570:
                  case 571:
                  case 573:
                  case 582:
                  case 583:
                  case 584:
                  case 586:
                  case 596:
                  case 597:
                  case 598:
                  case 600:
                  case 607:
                  case 608:
                  case 609:
                  case 610:
                  case 611:
                  case 612:
                  case 613:
                  case 614:
                  case 615:
                  case 616:
                  case 617:
                  case 618:
                  case 619:
                  case 620:
                  case 621:
                  case 622:
                  case 623:
                  case 624:
                  case 625:
                  case 626:
                  case 628:
                  case 635:
                  case 636:
                  case 637:
                  case 638:
                  case 642:
                  case 649:
                  case 650:
                  case 653:
                  case 656:
                  case 658:
                  case 662:
                  case 663:
                  case 665:
                  case 667:
                  case 668:
                  case 669:
                  case 670:
                  case 671:
                  case 672:
                  case 673:
                  case 674:
                  case 675:
                  case 676:
                  case 677:
                  case 678:
                  case 679:
                  case 680:
                  case 681:
                  case 682:
                  case 683:
                  case 684:
                  case 685:
                  case 686:
                  case 689:
                  case 692:
                  case 693:
                  case 695:
                  case 696:
                  case 702:
                  case 703:
                  case 707:
                  case 708:
                  default:
                     break;
                  case 4:
                     if (this.curChar == 116) {
                        this.jjAddStates(148, 149);
                     }
                     break;
                  case 6:
                     if (this.curChar == 93 && kind > 6) {
                        kind = 6;
                     }
                     break;
                  case 7:
                     if (this.curChar == 112) {
                        this.jjstateSet[this.jjnewStateCnt++] = 4;
                     }
                     break;
                  case 8:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 7;
                     }
                     break;
                  case 9:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 8;
                     }
                     break;
                  case 10:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 9;
                     }
                     break;
                  case 11:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 10;
                     }
                     break;
                  case 12:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 11;
                     }
                     break;
                  case 13:
                     if (this.curChar == 114) {
                        this.jjAddStates(150, 151);
                     }
                     break;
                  case 15:
                     if (this.curChar == 93 && kind > 7) {
                        kind = 7;
                     }
                     break;
                  case 16:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 13;
                     }
                     break;
                  case 17:
                     if (this.curChar == 118) {
                        this.jjstateSet[this.jjnewStateCnt++] = 16;
                     }
                     break;
                  case 18:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 17;
                     }
                     break;
                  case 19:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 18;
                     }
                     break;
                  case 20:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 19;
                     }
                     break;
                  case 21:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 20;
                     }
                     break;
                  case 22:
                     if (this.curChar == 102) {
                        this.jjstateSet[this.jjnewStateCnt++] = 23;
                     }
                     break;
                  case 24:
                     if (this.curChar == 105) {
                        this.jjstateSet[this.jjnewStateCnt++] = 22;
                     }
                     break;
                  case 25:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 26;
                     }
                     break;
                  case 26:
                     if ((2199023256064L & l) != 0L) {
                        this.jjstateSet[this.jjnewStateCnt++] = 27;
                     }
                     break;
                  case 27:
                     if (this.curChar == 102) {
                        this.jjstateSet[this.jjnewStateCnt++] = 28;
                     }
                     break;
                  case 29:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 25;
                     }
                     break;
                  case 30:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 29;
                     }
                     break;
                  case 31:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 30;
                     }
                     break;
                  case 32:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 33;
                     }
                     break;
                  case 34:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 32;
                     }
                     break;
                  case 35:
                     if (this.curChar == 105) {
                        this.jjstateSet[this.jjnewStateCnt++] = 34;
                     }
                     break;
                  case 36:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 35;
                     }
                     break;
                  case 37:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 38;
                     }
                     break;
                  case 39:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 40;
                     }
                     break;
                  case 41:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 39;
                     }
                     break;
                  case 42:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 37;
                     }
                     break;
                  case 43:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 42;
                     }
                     break;
                  case 44:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 43;
                     }
                     break;
                  case 45:
                     if (this.curChar == 105) {
                        this.jjstateSet[this.jjnewStateCnt++] = 44;
                     }
                     break;
                  case 46:
                     if (this.curChar == 112) {
                        this.jjAddStates(154, 155);
                     }
                     break;
                  case 48:
                     if (this.curChar == 93 && kind > 12) {
                        kind = 12;
                     }
                     break;
                  case 49:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 46;
                     }
                     break;
                  case 50:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 49;
                     }
                     break;
                  case 51:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 52;
                     }
                     break;
                  case 52:
                     if ((137438953504L & l) != 0L) {
                        this.jjstateSet[this.jjnewStateCnt++] = 56;
                     }
                     break;
                  case 53:
                     if (this.curChar == 104) {
                        this.jjstateSet[this.jjnewStateCnt++] = 54;
                     }
                     break;
                  case 55:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 53;
                     }
                     break;
                  case 56:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 55;
                     }
                     break;
                  case 57:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 51;
                     }
                     break;
                  case 58:
                     if (this.curChar == 102) {
                        this.jjstateSet[this.jjnewStateCnt++] = 57;
                     }
                     break;
                  case 59:
                     if (this.curChar == 104) {
                        this.jjstateSet[this.jjnewStateCnt++] = 60;
                     }
                     break;
                  case 61:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 59;
                     }
                     break;
                  case 62:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 61;
                     }
                     break;
                  case 63:
                     if (this.curChar == 105) {
                        this.jjstateSet[this.jjnewStateCnt++] = 62;
                     }
                     break;
                  case 64:
                     if (this.curChar == 119) {
                        this.jjstateSet[this.jjnewStateCnt++] = 63;
                     }
                     break;
                  case 65:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 64;
                     }
                     break;
                  case 66:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 67;
                     }
                     break;
                  case 68:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 66;
                     }
                     break;
                  case 69:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 68;
                     }
                     break;
                  case 70:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 69;
                     }
                     break;
                  case 71:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 72;
                     }
                     break;
                  case 73:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 71;
                     }
                     break;
                  case 74:
                     if (this.curChar == 105) {
                        this.jjstateSet[this.jjnewStateCnt++] = 73;
                     }
                     break;
                  case 75:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 74;
                     }
                     break;
                  case 76:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 75;
                     }
                     break;
                  case 77:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 76;
                     }
                     break;
                  case 78:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 79;
                     }
                     break;
                  case 80:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 78;
                     }
                     break;
                  case 81:
                     if (this.curChar == 98) {
                        this.jjstateSet[this.jjnewStateCnt++] = 80;
                     }
                     break;
                  case 82:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 81;
                     }
                     break;
                  case 83:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 82;
                     }
                     break;
                  case 84:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 83;
                     }
                     break;
                  case 85:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 86;
                     }
                     break;
                  case 87:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 85;
                     }
                     break;
                  case 88:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 87;
                     }
                     break;
                  case 89:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 88;
                     }
                     break;
                  case 90:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 89;
                     }
                     break;
                  case 91:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 92;
                     }
                     break;
                  case 93:
                     if (this.curChar == 100) {
                        this.jjstateSet[this.jjnewStateCnt++] = 91;
                     }
                     break;
                  case 94:
                     if (this.curChar == 117) {
                        this.jjstateSet[this.jjnewStateCnt++] = 93;
                     }
                     break;
                  case 95:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 94;
                     }
                     break;
                  case 96:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 95;
                     }
                     break;
                  case 97:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 96;
                     }
                     break;
                  case 98:
                     if (this.curChar == 105) {
                        this.jjstateSet[this.jjnewStateCnt++] = 97;
                     }
                     break;
                  case 99:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 100;
                     }
                     break;
                  case 101:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 99;
                     }
                     break;
                  case 102:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 101;
                     }
                     break;
                  case 103:
                     if (this.curChar == 112) {
                        this.jjstateSet[this.jjnewStateCnt++] = 102;
                     }
                     break;
                  case 104:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 103;
                     }
                     break;
                  case 105:
                     if (this.curChar == 105) {
                        this.jjstateSet[this.jjnewStateCnt++] = 104;
                     }
                     break;
                  case 106:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 107;
                     }
                     break;
                  case 108:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 106;
                     }
                     break;
                  case 109:
                     if (this.curChar == 105) {
                        this.jjstateSet[this.jjnewStateCnt++] = 108;
                     }
                     break;
                  case 110:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 109;
                     }
                     break;
                  case 111:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 110;
                     }
                     break;
                  case 112:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 111;
                     }
                     break;
                  case 113:
                     if (this.curChar == 117) {
                        this.jjstateSet[this.jjnewStateCnt++] = 112;
                     }
                     break;
                  case 114:
                     if (this.curChar == 102) {
                        this.jjstateSet[this.jjnewStateCnt++] = 113;
                     }
                     break;
                  case 115:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 116;
                     }
                     break;
                  case 117:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 115;
                     }
                     break;
                  case 118:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 117;
                     }
                     break;
                  case 119:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 118;
                     }
                     break;
                  case 120:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 119;
                     }
                     break;
                  case 121:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 122;
                     }
                     break;
                  case 123:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 121;
                     }
                     break;
                  case 124:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 123;
                     }
                     break;
                  case 125:
                     if (this.curChar == 102) {
                        this.jjstateSet[this.jjnewStateCnt++] = 124;
                     }
                     break;
                  case 126:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 125;
                     }
                     break;
                  case 127:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 126;
                     }
                     break;
                  case 128:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 127;
                     }
                     break;
                  case 129:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 128;
                     }
                     break;
                  case 130:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 129;
                     }
                     break;
                  case 131:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 132;
                     }
                     break;
                  case 133:
                     if (this.curChar == 105) {
                        this.jjstateSet[this.jjnewStateCnt++] = 131;
                     }
                     break;
                  case 134:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 133;
                     }
                     break;
                  case 135:
                     if (this.curChar == 105) {
                        this.jjstateSet[this.jjnewStateCnt++] = 134;
                     }
                     break;
                  case 136:
                     if (this.curChar == 118) {
                        this.jjstateSet[this.jjnewStateCnt++] = 135;
                     }
                     break;
                  case 137:
                     if (this.curChar == 112) {
                        this.jjstateSet[this.jjnewStateCnt++] = 138;
                     }
                     break;
                  case 139:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 137;
                     }
                     break;
                  case 140:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 139;
                     }
                     break;
                  case 141:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 140;
                     }
                     break;
                  case 142:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 143;
                     }
                     break;
                  case 144:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 142;
                     }
                     break;
                  case 145:
                     if (this.curChar == 117) {
                        this.jjstateSet[this.jjnewStateCnt++] = 144;
                     }
                     break;
                  case 146:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 145;
                     }
                     break;
                  case 147:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 146;
                     }
                     break;
                  case 148:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 147;
                     }
                     break;
                  case 149:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 150;
                     }
                     break;
                  case 151:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 149;
                     }
                     break;
                  case 152:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 151;
                     }
                     break;
                  case 153:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 152;
                     }
                     break;
                  case 154:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 155;
                     }
                     break;
                  case 156:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 154;
                     }
                     break;
                  case 157:
                     if (this.curChar == 105) {
                        this.jjstateSet[this.jjnewStateCnt++] = 156;
                     }
                     break;
                  case 158:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 157;
                     }
                     break;
                  case 159:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 158;
                     }
                     break;
                  case 160:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 159;
                     }
                     break;
                  case 161:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 160;
                     }
                     break;
                  case 162:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 163;
                     }
                     break;
                  case 163:
                     if ((274877907008L & l) != 0L) {
                        this.jjstateSet[this.jjnewStateCnt++] = 169;
                     }
                     break;
                  case 164:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 165;
                     }
                     break;
                  case 166:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 164;
                     }
                     break;
                  case 167:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 166;
                     }
                     break;
                  case 168:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 167;
                     }
                     break;
                  case 169:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 168;
                     }
                     break;
                  case 170:
                     if (this.curChar == 117) {
                        this.jjstateSet[this.jjnewStateCnt++] = 162;
                     }
                     break;
                  case 171:
                     if (this.curChar == 112) {
                        this.jjstateSet[this.jjnewStateCnt++] = 170;
                     }
                     break;
                  case 172:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 171;
                     }
                     break;
                  case 173:
                     if (this.curChar == 117) {
                        this.jjstateSet[this.jjnewStateCnt++] = 172;
                     }
                     break;
                  case 174:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 173;
                     }
                     break;
                  case 175:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 176;
                     }
                     break;
                  case 176:
                     if ((137438953504L & l) != 0L) {
                        this.jjstateSet[this.jjnewStateCnt++] = 180;
                     }
                     break;
                  case 177:
                     if (this.curChar == 99) {
                        this.jjAddStates(156, 157);
                     }
                     break;
                  case 179:
                     if (this.curChar == 93 && kind > 30) {
                        kind = 30;
                     }
                     break;
                  case 180:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 177;
                     }
                     break;
                  case 181:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 175;
                     }
                     break;
                  case 182:
                     if (this.curChar == 117) {
                        this.jjstateSet[this.jjnewStateCnt++] = 181;
                     }
                     break;
                  case 183:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 182;
                     }
                     break;
                  case 184:
                     if (this.curChar == 111) {
                        this.jjAddStates(333, 334);
                     }
                     break;
                  case 185:
                     if (this.curChar == 101) {
                        this.jjCheckNAdd(189);
                     }
                     break;
                  case 186:
                     if (this.curChar == 99) {
                        this.jjAddStates(158, 159);
                     }
                     break;
                  case 188:
                     if (this.curChar == 93 && kind > 31) {
                        kind = 31;
                     }
                     break;
                  case 189:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 186;
                     }
                     break;
                  case 190:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 185;
                     }
                     break;
                  case 191:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 190;
                     }
                     break;
                  case 192:
                     if (this.curChar == 117) {
                        this.jjstateSet[this.jjnewStateCnt++] = 191;
                     }
                     break;
                  case 193:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 192;
                     }
                     break;
                  case 194:
                     if (this.curChar == 69) {
                        this.jjCheckNAdd(189);
                     }
                     break;
                  case 195:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 194;
                     }
                     break;
                  case 196:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 195;
                     }
                     break;
                  case 197:
                     if (this.curChar == 117) {
                        this.jjstateSet[this.jjnewStateCnt++] = 196;
                     }
                     break;
                  case 198:
                     if (this.curChar == 65) {
                        this.jjstateSet[this.jjnewStateCnt++] = 197;
                     }
                     break;
                  case 199:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 184;
                     }
                     break;
                  case 200:
                     if (this.curChar == 115) {
                        this.jjAddStates(160, 161);
                     }
                     break;
                  case 202:
                     if (this.curChar == 93 && kind > 32) {
                        kind = 32;
                     }
                     break;
                  case 203:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 200;
                     }
                     break;
                  case 204:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 203;
                     }
                     break;
                  case 205:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 204;
                     }
                     break;
                  case 206:
                     if (this.curChar == 112) {
                        this.jjstateSet[this.jjnewStateCnt++] = 205;
                     }
                     break;
                  case 207:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 206;
                     }
                     break;
                  case 208:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 207;
                     }
                     break;
                  case 209:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 208;
                     }
                     break;
                  case 210:
                     if (this.curChar == 116) {
                        this.jjAddStates(162, 163);
                     }
                     break;
                  case 212:
                     if (this.curChar == 93 && kind > 33) {
                        kind = 33;
                     }
                     break;
                  case 213:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 210;
                     }
                     break;
                  case 214:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 213;
                     }
                     break;
                  case 215:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 214;
                     }
                     break;
                  case 216:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 215;
                     }
                     break;
                  case 217:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 216;
                     }
                     break;
                  case 218:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 217;
                     }
                     break;
                  case 219:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 220;
                     }
                     break;
                  case 220:
                     if ((281474976776192L & l) != 0L) {
                        this.jjstateSet[this.jjnewStateCnt++] = 226;
                     }
                     break;
                  case 221:
                     if (this.curChar == 101) {
                        this.jjAddStates(164, 165);
                     }
                     break;
                  case 223:
                     if (this.curChar == 93 && kind > 35) {
                        kind = 35;
                     }
                     break;
                  case 224:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 221;
                     }
                     break;
                  case 225:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 224;
                     }
                     break;
                  case 226:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 225;
                     }
                     break;
                  case 227:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 219;
                     }
                     break;
                  case 228:
                     if (this.curChar == 101) {
                        this.jjAddStates(166, 168);
                     }
                     break;
                  case 231:
                     if (this.curChar == 93 && kind > 54) {
                        kind = 54;
                     }
                     break;
                  case 232:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 228;
                     }
                     break;
                  case 233:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 232;
                     }
                     break;
                  case 234:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 233;
                     }
                     break;
                  case 235:
                     if (this.curChar == 107) {
                        this.jjAddStates(169, 171);
                     }
                     break;
                  case 238:
                     if (this.curChar == 93 && kind > 55) {
                        kind = 55;
                     }
                     break;
                  case 239:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 235;
                     }
                     break;
                  case 240:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 239;
                     }
                     break;
                  case 241:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 240;
                     }
                     break;
                  case 242:
                     if (this.curChar == 98) {
                        this.jjstateSet[this.jjnewStateCnt++] = 241;
                     }
                     break;
                  case 243:
                     if (this.curChar == 101) {
                        this.jjAddStates(172, 174);
                     }
                     break;
                  case 246:
                     if (this.curChar == 93 && kind > 56) {
                        kind = 56;
                     }
                     break;
                  case 247:
                     if (this.curChar == 117) {
                        this.jjstateSet[this.jjnewStateCnt++] = 243;
                     }
                     break;
                  case 248:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 247;
                     }
                     break;
                  case 249:
                     if (this.curChar == 105) {
                        this.jjstateSet[this.jjnewStateCnt++] = 248;
                     }
                     break;
                  case 250:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 249;
                     }
                     break;
                  case 251:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 250;
                     }
                     break;
                  case 252:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 251;
                     }
                     break;
                  case 253:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 252;
                     }
                     break;
                  case 254:
                     if (this.curChar == 110) {
                        this.jjAddStates(175, 177);
                     }
                     break;
                  case 257:
                     if (this.curChar == 93 && kind > 57) {
                        kind = 57;
                     }
                     break;
                  case 258:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 254;
                     }
                     break;
                  case 259:
                     if (this.curChar == 117) {
                        this.jjstateSet[this.jjnewStateCnt++] = 258;
                     }
                     break;
                  case 260:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 259;
                     }
                     break;
                  case 261:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 260;
                     }
                     break;
                  case 262:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 261;
                     }
                     break;
                  case 263:
                     if (this.curChar == 112) {
                        this.jjAddStates(178, 180);
                     }
                     break;
                  case 266:
                     if (this.curChar == 93 && kind > 58) {
                        kind = 58;
                     }
                     break;
                  case 267:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 263;
                     }
                     break;
                  case 268:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 267;
                     }
                     break;
                  case 269:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 268;
                     }
                     break;
                  case 270:
                     if (this.curChar == 104) {
                        this.jjAddStates(181, 183);
                     }
                     break;
                  case 273:
                     if (this.curChar == 93 && kind > 59) {
                        kind = 59;
                     }
                     break;
                  case 274:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 270;
                     }
                     break;
                  case 275:
                     if (this.curChar == 117) {
                        this.jjstateSet[this.jjnewStateCnt++] = 274;
                     }
                     break;
                  case 276:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 275;
                     }
                     break;
                  case 277:
                     if (this.curChar == 102) {
                        this.jjstateSet[this.jjnewStateCnt++] = 276;
                     }
                     break;
                  case 278:
                     if (this.curChar == 116) {
                        this.jjAddStates(184, 186);
                     }
                     break;
                  case 281:
                     if (this.curChar == 93 && kind > 60) {
                        kind = 60;
                     }
                     break;
                  case 282:
                     if (this.curChar == 116) {
                        this.jjAddStates(187, 189);
                     }
                     break;
                  case 285:
                     if (this.curChar == 93 && kind > 61) {
                        kind = 61;
                     }
                     break;
                  case 286:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 282;
                     }
                     break;
                  case 287:
                     if (this.curChar == 116) {
                        this.jjAddStates(190, 192);
                     }
                     break;
                  case 290:
                     if (this.curChar == 93 && kind > 62) {
                        kind = 62;
                     }
                     break;
                  case 291:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 287;
                     }
                     break;
                  case 292:
                     if (this.curChar == 116) {
                        this.jjAddStates(193, 195);
                     }
                     break;
                  case 295:
                     if (this.curChar == 93 && kind > 63) {
                        kind = 63;
                     }
                     break;
                  case 296:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 292;
                     }
                     break;
                  case 297:
                     if (this.curChar == 116) {
                        this.jjAddStates(196, 197);
                     }
                     break;
                  case 299:
                     if (this.curChar == 93 && kind > 64) {
                        kind = 64;
                     }
                     break;
                  case 300:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 297;
                     }
                     break;
                  case 301:
                     if (this.curChar == 117) {
                        this.jjstateSet[this.jjnewStateCnt++] = 300;
                     }
                     break;
                  case 302:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 301;
                     }
                     break;
                  case 303:
                     if (this.curChar == 102) {
                        this.jjstateSet[this.jjnewStateCnt++] = 302;
                     }
                     break;
                  case 304:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 303;
                     }
                     break;
                  case 305:
                     if (this.curChar == 100) {
                        this.jjstateSet[this.jjnewStateCnt++] = 304;
                     }
                     break;
                  case 306:
                     if (this.curChar == 100) {
                        this.jjAddStates(198, 200);
                     }
                     break;
                  case 309:
                     if (this.curChar == 93 && kind > 65) {
                        kind = 65;
                     }
                     break;
                  case 310:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 306;
                     }
                     break;
                  case 311:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 310;
                     }
                     break;
                  case 312:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 311;
                     }
                     break;
                  case 313:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 312;
                     }
                     break;
                  case 314:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 313;
                     }
                     break;
                  case 315:
                     if (this.curChar == 100) {
                        this.jjstateSet[this.jjnewStateCnt++] = 316;
                     }
                     break;
                  case 317:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 315;
                     }
                     break;
                  case 318:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 317;
                     }
                     break;
                  case 319:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 318;
                     }
                     break;
                  case 320:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 319;
                     }
                     break;
                  case 321:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 320;
                     }
                     break;
                  case 322:
                     if (this.curChar == 101) {
                        this.jjAddStates(201, 203);
                     }
                     break;
                  case 325:
                     if (this.curChar == 93 && kind > 67) {
                        kind = 67;
                     }
                     break;
                  case 326:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 322;
                     }
                     break;
                  case 327:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 326;
                     }
                     break;
                  case 328:
                     if (this.curChar == 117) {
                        this.jjstateSet[this.jjnewStateCnt++] = 327;
                     }
                     break;
                  case 329:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 328;
                     }
                     break;
                  case 330:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 329;
                     }
                     break;
                  case 331:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 330;
                     }
                     break;
                  case 332:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 333;
                     }
                     break;
                  case 334:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 332;
                     }
                     break;
                  case 335:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 334;
                     }
                     break;
                  case 336:
                     if (this.curChar == 117) {
                        this.jjstateSet[this.jjnewStateCnt++] = 335;
                     }
                     break;
                  case 337:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 336;
                     }
                     break;
                  case 338:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 337;
                     }
                     break;
                  case 339:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 338;
                     }
                     break;
                  case 340:
                     if (this.curChar == 107) {
                        this.jjAddStates(204, 206);
                     }
                     break;
                  case 343:
                     if (this.curChar == 93 && kind > 69) {
                        kind = 69;
                     }
                     break;
                  case 344:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 340;
                     }
                     break;
                  case 345:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 344;
                     }
                     break;
                  case 346:
                     if (this.curChar == 98) {
                        this.jjstateSet[this.jjnewStateCnt++] = 345;
                     }
                     break;
                  case 347:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 346;
                     }
                     break;
                  case 348:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 347;
                     }
                     break;
                  case 349:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 348;
                     }
                     break;
                  case 350:
                     if (this.curChar == 102) {
                        this.jjstateSet[this.jjnewStateCnt++] = 349;
                     }
                     break;
                  case 351:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 352;
                     }
                     break;
                  case 353:
                     if (this.curChar == 112) {
                        this.jjstateSet[this.jjnewStateCnt++] = 351;
                     }
                     break;
                  case 354:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 353;
                     }
                     break;
                  case 355:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 354;
                     }
                     break;
                  case 356:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 355;
                     }
                     break;
                  case 357:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 356;
                     }
                     break;
                  case 358:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 359;
                     }
                     break;
                  case 359:
                     if ((137438953504L & l) != 0L) {
                        this.jjstateSet[this.jjnewStateCnt++] = 366;
                     }
                     break;
                  case 360:
                     if (this.curChar == 101) {
                        this.jjAddStates(207, 208);
                     }
                     break;
                  case 362:
                     if (this.curChar == 93 && kind > 72) {
                        kind = 72;
                     }
                     break;
                  case 363:
                     if (this.curChar == 112) {
                        this.jjstateSet[this.jjnewStateCnt++] = 360;
                     }
                     break;
                  case 364:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 363;
                     }
                     break;
                  case 365:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 364;
                     }
                     break;
                  case 366:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 365;
                     }
                     break;
                  case 367:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 358;
                     }
                     break;
                  case 399:
                     if (this.curChar == 102) {
                        this.jjAddStates(209, 210);
                     }
                     break;
                  case 401:
                     if (this.curChar == 93 && kind > 36) {
                        kind = 36;
                     }
                     break;
                  case 402:
                     if (this.curChar == 105) {
                        this.jjstateSet[this.jjnewStateCnt++] = 399;
                     }
                     break;
                  case 406:
                     if (this.curChar == 116) {
                        this.jjAddStates(211, 212);
                     }
                     break;
                  case 408:
                     if (this.curChar == 93 && kind > 37) {
                        kind = 37;
                     }
                     break;
                  case 409:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 406;
                     }
                     break;
                  case 410:
                     if (this.curChar == 105) {
                        this.jjstateSet[this.jjnewStateCnt++] = 409;
                     }
                     break;
                  case 411:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 410;
                     }
                     break;
                  case 415:
                     if (this.curChar == 115) {
                        this.jjAddStates(213, 214);
                     }
                     break;
                  case 417:
                     if (this.curChar == 93 && kind > 38) {
                        kind = 38;
                     }
                     break;
                  case 418:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 415;
                     }
                     break;
                  case 419:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 418;
                     }
                     break;
                  case 420:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 419;
                     }
                     break;
                  case 421:
                     if (this.curChar == 105) {
                        this.jjstateSet[this.jjnewStateCnt++] = 420;
                     }
                     break;
                  case 425:
                     if (this.curChar == 112) {
                        this.jjAddStates(215, 216);
                     }
                     break;
                  case 427:
                     if (this.curChar == 93 && kind > 39) {
                        kind = 39;
                     }
                     break;
                  case 428:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 425;
                     }
                     break;
                  case 429:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 428;
                     }
                     break;
                  case 433:
                     if (this.curChar == 114) {
                        this.jjAddStates(217, 218);
                     }
                     break;
                  case 435:
                     if (this.curChar == 93 && kind > 40) {
                        kind = 40;
                     }
                     break;
                  case 436:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 433;
                     }
                     break;
                  case 437:
                     if (this.curChar == 118) {
                        this.jjstateSet[this.jjnewStateCnt++] = 436;
                     }
                     break;
                  case 438:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 437;
                     }
                     break;
                  case 439:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 438;
                     }
                     break;
                  case 440:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 439;
                     }
                     break;
                  case 441:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 440;
                     }
                     break;
                  case 445:
                     if (this.curChar == 116) {
                        this.jjAddStates(219, 220);
                     }
                     break;
                  case 447:
                     if (this.curChar == 93 && kind > 41) {
                        kind = 41;
                     }
                     break;
                  case 448:
                     if (this.curChar == 112) {
                        this.jjstateSet[this.jjnewStateCnt++] = 445;
                     }
                     break;
                  case 449:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 448;
                     }
                     break;
                  case 450:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 449;
                     }
                     break;
                  case 451:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 450;
                     }
                     break;
                  case 452:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 451;
                     }
                     break;
                  case 453:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 452;
                     }
                     break;
                  case 457:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 458;
                     }
                     break;
                  case 458:
                     if ((137438953504L & l) != 0L) {
                        this.jjstateSet[this.jjnewStateCnt++] = 463;
                     }
                     break;
                  case 459:
                     if (this.curChar == 104) {
                        this.jjAddStates(221, 222);
                     }
                     break;
                  case 461:
                     if (this.curChar == 93 && kind > 42) {
                        kind = 42;
                     }
                     break;
                  case 462:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 459;
                     }
                     break;
                  case 463:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 462;
                     }
                     break;
                  case 464:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 457;
                     }
                     break;
                  case 465:
                     if (this.curChar == 102) {
                        this.jjstateSet[this.jjnewStateCnt++] = 464;
                     }
                     break;
                  case 469:
                     if (this.curChar == 108) {
                        this.jjAddStates(223, 224);
                     }
                     break;
                  case 471:
                     if (this.curChar == 93 && kind > 43) {
                        kind = 43;
                     }
                     break;
                  case 472:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 469;
                     }
                     break;
                  case 473:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 472;
                     }
                     break;
                  case 474:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 473;
                     }
                     break;
                  case 475:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 474;
                     }
                     break;
                  case 479:
                     if (this.curChar == 108) {
                        this.jjAddStates(225, 226);
                     }
                     break;
                  case 481:
                     if (this.curChar == 93 && kind > 44) {
                        kind = 44;
                     }
                     break;
                  case 482:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 479;
                     }
                     break;
                  case 483:
                     if (this.curChar == 98) {
                        this.jjstateSet[this.jjnewStateCnt++] = 482;
                     }
                     break;
                  case 484:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 483;
                     }
                     break;
                  case 485:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 484;
                     }
                     break;
                  case 486:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 485;
                     }
                     break;
                  case 490:
                     if (this.curChar == 110) {
                        this.jjAddStates(227, 228);
                     }
                     break;
                  case 492:
                     if (this.curChar == 93 && kind > 45) {
                        kind = 45;
                     }
                     break;
                  case 493:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 490;
                     }
                     break;
                  case 494:
                     if (this.curChar == 105) {
                        this.jjstateSet[this.jjnewStateCnt++] = 493;
                     }
                     break;
                  case 495:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 494;
                     }
                     break;
                  case 496:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 495;
                     }
                     break;
                  case 497:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 496;
                     }
                     break;
                  case 501:
                     if (this.curChar == 110) {
                        this.jjAddStates(229, 230);
                     }
                     break;
                  case 503:
                     if (this.curChar == 93 && kind > 46) {
                        kind = 46;
                     }
                     break;
                  case 504:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 501;
                     }
                     break;
                  case 505:
                     if (this.curChar == 105) {
                        this.jjstateSet[this.jjnewStateCnt++] = 504;
                     }
                     break;
                  case 506:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 505;
                     }
                     break;
                  case 507:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 506;
                     }
                     break;
                  case 508:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 507;
                     }
                     break;
                  case 509:
                     if (this.curChar == 117) {
                        this.jjstateSet[this.jjnewStateCnt++] = 508;
                     }
                     break;
                  case 510:
                     if (this.curChar == 102) {
                        this.jjstateSet[this.jjnewStateCnt++] = 509;
                     }
                     break;
                  case 514:
                     if (this.curChar == 111) {
                        this.jjAddStates(231, 232);
                     }
                     break;
                  case 516:
                     if (this.curChar == 93 && kind > 47) {
                        kind = 47;
                     }
                     break;
                  case 517:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 514;
                     }
                     break;
                  case 518:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 517;
                     }
                     break;
                  case 519:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 518;
                     }
                     break;
                  case 520:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 519;
                     }
                     break;
                  case 524:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 525;
                     }
                     break;
                  case 525:
                     if ((274877907008L & l) != 0L) {
                        this.jjstateSet[this.jjnewStateCnt++] = 532;
                     }
                     break;
                  case 526:
                     if (this.curChar == 116) {
                        this.jjAddStates(233, 234);
                     }
                     break;
                  case 528:
                     if (this.curChar == 93 && kind > 48) {
                        kind = 48;
                     }
                     break;
                  case 529:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 526;
                     }
                     break;
                  case 530:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 529;
                     }
                     break;
                  case 531:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 530;
                     }
                     break;
                  case 532:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 531;
                     }
                     break;
                  case 533:
                     if (this.curChar == 117) {
                        this.jjstateSet[this.jjnewStateCnt++] = 524;
                     }
                     break;
                  case 534:
                     if (this.curChar == 112) {
                        this.jjstateSet[this.jjnewStateCnt++] = 533;
                     }
                     break;
                  case 535:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 534;
                     }
                     break;
                  case 536:
                     if (this.curChar == 117) {
                        this.jjstateSet[this.jjnewStateCnt++] = 535;
                     }
                     break;
                  case 537:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 536;
                     }
                     break;
                  case 541:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 542;
                     }
                     break;
                  case 542:
                     if ((137438953504L & l) != 0L) {
                        this.jjstateSet[this.jjnewStateCnt++] = 546;
                     }
                     break;
                  case 543:
                     if (this.curChar == 99) {
                        this.jjAddStates(235, 236);
                     }
                     break;
                  case 545:
                     if (this.curChar == 93 && kind > 49) {
                        kind = 49;
                     }
                     break;
                  case 546:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 543;
                     }
                     break;
                  case 547:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 541;
                     }
                     break;
                  case 548:
                     if (this.curChar == 117) {
                        this.jjstateSet[this.jjnewStateCnt++] = 547;
                     }
                     break;
                  case 549:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 548;
                     }
                     break;
                  case 553:
                     if (this.curChar == 111) {
                        this.jjAddStates(335, 336);
                     }
                     break;
                  case 554:
                     if (this.curChar == 101) {
                        this.jjCheckNAdd(558);
                     }
                     break;
                  case 555:
                     if (this.curChar == 99) {
                        this.jjAddStates(237, 238);
                     }
                     break;
                  case 557:
                     if (this.curChar == 93 && kind > 50) {
                        kind = 50;
                     }
                     break;
                  case 558:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 555;
                     }
                     break;
                  case 559:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 554;
                     }
                     break;
                  case 560:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 559;
                     }
                     break;
                  case 561:
                     if (this.curChar == 117) {
                        this.jjstateSet[this.jjnewStateCnt++] = 560;
                     }
                     break;
                  case 562:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 561;
                     }
                     break;
                  case 563:
                     if (this.curChar == 69) {
                        this.jjCheckNAdd(558);
                     }
                     break;
                  case 564:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 563;
                     }
                     break;
                  case 565:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 564;
                     }
                     break;
                  case 566:
                     if (this.curChar == 117) {
                        this.jjstateSet[this.jjnewStateCnt++] = 565;
                     }
                     break;
                  case 567:
                     if (this.curChar == 65) {
                        this.jjstateSet[this.jjnewStateCnt++] = 566;
                     }
                     break;
                  case 568:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 553;
                     }
                     break;
                  case 572:
                     if (this.curChar == 115) {
                        this.jjAddStates(239, 240);
                     }
                     break;
                  case 574:
                     if (this.curChar == 93 && kind > 51) {
                        kind = 51;
                     }
                     break;
                  case 575:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 572;
                     }
                     break;
                  case 576:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 575;
                     }
                     break;
                  case 577:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 576;
                     }
                     break;
                  case 578:
                     if (this.curChar == 112) {
                        this.jjstateSet[this.jjnewStateCnt++] = 577;
                     }
                     break;
                  case 579:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 578;
                     }
                     break;
                  case 580:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 579;
                     }
                     break;
                  case 581:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 580;
                     }
                     break;
                  case 585:
                     if (this.curChar == 109) {
                        this.jjAddStates(241, 242);
                     }
                     break;
                  case 587:
                     if (this.curChar == 93 && kind > 52) {
                        kind = 52;
                     }
                     break;
                  case 588:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 585;
                     }
                     break;
                  case 589:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 588;
                     }
                     break;
                  case 590:
                     if (this.curChar == 102) {
                        this.jjstateSet[this.jjnewStateCnt++] = 589;
                     }
                     break;
                  case 591:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 590;
                     }
                     break;
                  case 592:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 591;
                     }
                     break;
                  case 593:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 592;
                     }
                     break;
                  case 594:
                     if (this.curChar == 114) {
                        this.jjstateSet[this.jjnewStateCnt++] = 593;
                     }
                     break;
                  case 595:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 594;
                     }
                     break;
                  case 599:
                     if (this.curChar == 104) {
                        this.jjAddStates(243, 244);
                     }
                     break;
                  case 601:
                     if (this.curChar == 93 && kind > 53) {
                        kind = 53;
                     }
                     break;
                  case 602:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 599;
                     }
                     break;
                  case 603:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 602;
                     }
                     break;
                  case 604:
                     if (this.curChar == 105) {
                        this.jjstateSet[this.jjnewStateCnt++] = 603;
                     }
                     break;
                  case 605:
                     if (this.curChar == 119) {
                        this.jjstateSet[this.jjnewStateCnt++] = 604;
                     }
                     break;
                  case 606:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 605;
                     }
                     break;
                  case 627:
                     if (this.curChar == 101) {
                        this.jjAddStates(245, 246);
                     }
                     break;
                  case 629:
                     if (this.curChar == 93 && kind > 71) {
                        kind = 71;
                     }
                     break;
                  case 630:
                     if (this.curChar == 112) {
                        this.jjstateSet[this.jjnewStateCnt++] = 627;
                     }
                     break;
                  case 631:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 630;
                     }
                     break;
                  case 632:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 631;
                     }
                     break;
                  case 633:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 632;
                     }
                     break;
                  case 634:
                     if (this.curChar == 101) {
                        this.jjstateSet[this.jjnewStateCnt++] = 633;
                     }
                     break;
                  case 639:
                     if (this.curChar == 111) {
                        this.jjstateSet[this.jjnewStateCnt++] = 640;
                     }
                     break;
                  case 640:
                     if ((137438953504L & l) != 0L) {
                        this.jjstateSet[this.jjnewStateCnt++] = 647;
                     }
                     break;
                  case 641:
                     if (this.curChar == 101) {
                        this.jjAddStates(247, 248);
                     }
                     break;
                  case 643:
                     if (this.curChar == 93 && kind > 73) {
                        kind = 73;
                     }
                     break;
                  case 644:
                     if (this.curChar == 112) {
                        this.jjstateSet[this.jjnewStateCnt++] = 641;
                     }
                     break;
                  case 645:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 644;
                     }
                     break;
                  case 646:
                     if (this.curChar == 99) {
                        this.jjstateSet[this.jjnewStateCnt++] = 645;
                     }
                     break;
                  case 647:
                     if (this.curChar == 115) {
                        this.jjstateSet[this.jjnewStateCnt++] = 646;
                     }
                     break;
                  case 648:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 639;
                     }
                     break;
                  case 651:
                  case 697:
                     if (this.curChar == 64 && kind > 74) {
                        kind = 74;
                     }
                     break;
                  case 652:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 653;
                     }
                     break;
                  case 654:
                  case 687:
                     if (this.curChar == 116) {
                        this.jjCheckNAdd(652);
                     }
                     break;
                  case 655:
                     if (this.curChar == 102) {
                        this.jjstateSet[this.jjnewStateCnt++] = 654;
                     }
                     break;
                  case 657:
                     if (this.curChar == 108) {
                        this.jjAddStates(337, 338);
                     }
                     break;
                  case 659:
                     if (this.curChar == 93 && kind > 77) {
                        kind = 77;
                     }
                     break;
                  case 660:
                  case 690:
                     if (this.curChar == 116) {
                        this.jjCheckNAdd(657);
                     }
                     break;
                  case 661:
                     if (this.curChar == 102) {
                        this.jjstateSet[this.jjnewStateCnt++] = 660;
                     }
                     break;
                  case 664:
                     if ((576460745995190270L & l) != 0L) {
                        if (kind > 78) {
                           kind = 78;
                        }

                        this.jjstateSet[this.jjnewStateCnt++] = 664;
                     }
                     break;
                  case 666:
                     if (this.curChar == 91) {
                        this.jjAddStates(261, 332);
                     }
                     break;
                  case 688:
                     if (this.curChar == 102) {
                        this.jjstateSet[this.jjnewStateCnt++] = 687;
                     }
                     break;
                  case 691:
                     if (this.curChar == 102) {
                        this.jjstateSet[this.jjnewStateCnt++] = 690;
                     }
                     break;
                  case 694:
                     if (this.curChar == 91) {
                        this.jjAddStates(7, 8);
                     }
                     break;
                  case 698:
                     if (this.curChar == 64) {
                        this.jjCheckNAddStates(339, 342);
                     }
                     break;
                  case 699:
                  case 700:
                     if ((576460745995190271L & l) != 0L) {
                        this.jjCheckNAddStates(249, 253);
                     }
                     break;
                  case 701:
                  case 711:
                     if (this.curChar == 92) {
                        this.jjCheckNAdd(702);
                     }
                     break;
                  case 704:
                  case 705:
                     if ((576460745995190271L & l) != 0L) {
                        this.jjCheckNAddStates(256, 260);
                     }
                     break;
                  case 706:
                  case 710:
                     if (this.curChar == 92) {
                        this.jjCheckNAdd(707);
                     }
                     break;
                  case 709:
                     if (this.curChar == 93 && kind > 75) {
                        kind = 75;
                     }
               }
            } while(i != startsAt);
         } else {
            int hiByte = this.curChar >> 8;
            int i1 = hiByte >> 6;
            long l1 = 1L << (hiByte & 63);
            int i2 = (this.curChar & 255) >> 6;
            long l2 = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 1:
                  case 2:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        if (kind > 80) {
                           kind = 80;
                        }

                        this.jjCheckNAdd(1);
                     }
                     break;
                  case 699:
                  case 700:
                     if (jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                        this.jjCheckNAddStates(249, 253);
                     }
                     break;
                  case 704:
                  case 705:
                     if (jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                        this.jjCheckNAddStates(256, 260);
                     }
                     break;
                  default:
                     if (i1 != 0 && l1 != 0L && i2 != 0 && l2 == 0L) {
                     }
               }
            } while(i != startsAt);
         }

         if (kind != Integer.MAX_VALUE) {
            this.jjmatchedKind = kind;
            this.jjmatchedPos = curPos;
            kind = Integer.MAX_VALUE;
         }

         ++curPos;
         if ((i = this.jjnewStateCnt) == (startsAt = 713 - (this.jjnewStateCnt = startsAt))) {
            return curPos;
         }

         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var13) {
            return curPos;
         }
      }
   }

   private final int jjStopStringLiteralDfa_2(int pos, long active0, long active1, long active2) {
      switch (pos) {
         case 0:
            if ((active2 & 32L) != 0L) {
               return 2;
            } else {
               if ((active1 & 6442450944L) == 0L && (active2 & 14336L) == 0L) {
                  if ((active1 & 2305983746702049280L) != 0L) {
                     return 44;
                  }

                  if ((active1 & 1152921882563969024L) != 0L) {
                     return 54;
                  }

                  if ((active1 & 145276272354787328L) != 0L) {
                     return 47;
                  }

                  return -1;
               }

               this.jjmatchedKind = 142;
               return 104;
            }
         case 1:
            if ((active2 & 6144L) != 0L) {
               return 104;
            } else if ((active1 & 1152921848204230656L) != 0L) {
               return 53;
            } else {
               if ((active1 & 6442450944L) == 0L && (active2 & 8192L) == 0L) {
                  return -1;
               }

               if (this.jjmatchedPos != 1) {
                  this.jjmatchedKind = 142;
                  this.jjmatchedPos = 1;
               }

               return 104;
            }
         case 2:
            if ((active1 & 6442450944L) == 0L && (active2 & 8192L) == 0L) {
               return -1;
            }

            this.jjmatchedKind = 142;
            this.jjmatchedPos = 2;
            return 104;
         case 3:
            if ((active1 & 4294967296L) != 0L) {
               return 104;
            } else {
               if ((active1 & 2147483648L) == 0L && (active2 & 8192L) == 0L) {
                  return -1;
               }

               this.jjmatchedKind = 142;
               this.jjmatchedPos = 3;
               return 104;
            }
         default:
            return -1;
      }
   }

   private final int jjStartNfa_2(int pos, long active0, long active1, long active2) {
      return this.jjMoveNfa_2(this.jjStopStringLiteralDfa_2(pos, active0, active1, active2), pos + 1);
   }

   private int jjMoveStringLiteralDfa0_2() {
      switch (this.curChar) {
         case 33:
            this.jjmatchedKind = 129;
            return this.jjMoveStringLiteralDfa1_2(8796093022208L, 0L);
         case 34:
         case 35:
         case 36:
         case 38:
         case 39:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
         case 60:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 92:
         case 94:
         case 95:
         case 96:
         case 98:
         case 99:
         case 100:
         case 101:
         case 103:
         case 104:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 115:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 124:
         default:
            return this.jjMoveNfa_2(1, 0);
         case 37:
            this.jjmatchedKind = 126;
            return this.jjMoveStringLiteralDfa1_2(281474976710656L, 0L);
         case 40:
            return this.jjStopAtPos(0, 135);
         case 41:
            return this.jjStopAtPos(0, 136);
         case 42:
            this.jjmatchedKind = 122;
            return this.jjMoveStringLiteralDfa1_2(576531121047601152L, 0L);
         case 43:
            this.jjmatchedKind = 120;
            return this.jjMoveStringLiteralDfa1_2(580542139465728L, 0L);
         case 44:
            return this.jjStopAtPos(0, 130);
         case 45:
            this.jjmatchedKind = 121;
            return this.jjMoveStringLiteralDfa1_2(1161084278931456L, 0L);
         case 46:
            this.jjmatchedKind = 99;
            return this.jjMoveStringLiteralDfa1_2(1152921848204230656L, 0L);
         case 47:
            this.jjmatchedKind = 125;
            return this.jjMoveStringLiteralDfa1_2(140737488355328L, 0L);
         case 58:
            return this.jjStopAtPos(0, 132);
         case 59:
            return this.jjStopAtPos(0, 131);
         case 61:
            this.jjmatchedKind = 105;
            return this.jjMoveStringLiteralDfa1_2(4398046511104L, 0L);
         case 62:
            return this.jjStopAtPos(0, 148);
         case 63:
            this.jjmatchedKind = 103;
            return this.jjMoveStringLiteralDfa1_2(1099511627776L, 0L);
         case 91:
            return this.jjStartNfaWithStates_2(0, 133, 2);
         case 93:
            return this.jjStopAtPos(0, 134);
         case 97:
            return this.jjMoveStringLiteralDfa1_2(0L, 4096L);
         case 102:
            return this.jjMoveStringLiteralDfa1_2(2147483648L, 0L);
         case 105:
            return this.jjMoveStringLiteralDfa1_2(0L, 2048L);
         case 116:
            return this.jjMoveStringLiteralDfa1_2(4294967296L, 0L);
         case 117:
            return this.jjMoveStringLiteralDfa1_2(0L, 8192L);
         case 123:
            return this.jjStopAtPos(0, 137);
         case 125:
            return this.jjStopAtPos(0, 138);
      }
   }

   private int jjMoveStringLiteralDfa1_2(long active1, long active2) {
      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var6) {
         this.jjStopStringLiteralDfa_2(0, 0L, active1, active2);
         return 1;
      }

      switch (this.curChar) {
         case 42:
            if ((active1 & 576460752303423488L) != 0L) {
               return this.jjStopAtPos(1, 123);
            }
            break;
         case 43:
            if ((active1 & 562949953421312L) != 0L) {
               return this.jjStopAtPos(1, 113);
            }
            break;
         case 45:
            if ((active1 & 1125899906842624L) != 0L) {
               return this.jjStopAtPos(1, 114);
            }
            break;
         case 46:
            if ((active1 & 68719476736L) != 0L) {
               this.jjmatchedKind = 100;
               this.jjmatchedPos = 1;
            }

            return this.jjMoveStringLiteralDfa2_2(active1, 1152921779484753920L, active2, 0L);
         case 61:
            if ((active1 & 4398046511104L) != 0L) {
               return this.jjStopAtPos(1, 106);
            }

            if ((active1 & 8796093022208L) != 0L) {
               return this.jjStopAtPos(1, 107);
            }

            if ((active1 & 17592186044416L) != 0L) {
               return this.jjStopAtPos(1, 108);
            }

            if ((active1 & 35184372088832L) != 0L) {
               return this.jjStopAtPos(1, 109);
            }

            if ((active1 & 70368744177664L) != 0L) {
               return this.jjStopAtPos(1, 110);
            }

            if ((active1 & 140737488355328L) != 0L) {
               return this.jjStopAtPos(1, 111);
            }

            if ((active1 & 281474976710656L) != 0L) {
               return this.jjStopAtPos(1, 112);
            }
            break;
         case 63:
            if ((active1 & 1099511627776L) != 0L) {
               return this.jjStopAtPos(1, 104);
            }
            break;
         case 97:
            return this.jjMoveStringLiteralDfa2_2(active1, 2147483648L, active2, 0L);
         case 110:
            if ((active2 & 2048L) != 0L) {
               return this.jjStartNfaWithStates_2(1, 139, 104);
            }
            break;
         case 114:
            return this.jjMoveStringLiteralDfa2_2(active1, 4294967296L, active2, 0L);
         case 115:
            if ((active2 & 4096L) != 0L) {
               return this.jjStartNfaWithStates_2(1, 140, 104);
            }

            return this.jjMoveStringLiteralDfa2_2(active1, 0L, active2, 8192L);
      }

      return this.jjStartNfa_2(0, 0L, active1, active2);
   }

   private int jjMoveStringLiteralDfa2_2(long old1, long active1, long old2, long active2) {
      if (((active1 &= old1) | (active2 &= old2)) == 0L) {
         return this.jjStartNfa_2(0, 0L, old1, old2);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var10) {
            this.jjStopStringLiteralDfa_2(1, 0L, active1, active2);
            return 2;
         }

         switch (this.curChar) {
            case 42:
               if ((active1 & 274877906944L) != 0L) {
                  return this.jjStopAtPos(2, 102);
               }
               break;
            case 46:
               if ((active1 & 1152921504606846976L) != 0L) {
                  return this.jjStopAtPos(2, 124);
               }
               break;
            case 105:
               return this.jjMoveStringLiteralDfa3_2(active1, 0L, active2, 8192L);
            case 108:
               return this.jjMoveStringLiteralDfa3_2(active1, 2147483648L, active2, 0L);
            case 117:
               return this.jjMoveStringLiteralDfa3_2(active1, 4294967296L, active2, 0L);
         }

         return this.jjStartNfa_2(1, 0L, active1, active2);
      }
   }

   private int jjMoveStringLiteralDfa3_2(long old1, long active1, long old2, long active2) {
      if (((active1 &= old1) | (active2 &= old2)) == 0L) {
         return this.jjStartNfa_2(1, 0L, old1, old2);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var10) {
            this.jjStopStringLiteralDfa_2(2, 0L, active1, active2);
            return 3;
         }

         switch (this.curChar) {
            case 101:
               if ((active1 & 4294967296L) != 0L) {
                  return this.jjStartNfaWithStates_2(3, 96, 104);
               }
            default:
               return this.jjStartNfa_2(2, 0L, active1, active2);
            case 110:
               return this.jjMoveStringLiteralDfa4_2(active1, 0L, active2, 8192L);
            case 115:
               return this.jjMoveStringLiteralDfa4_2(active1, 2147483648L, active2, 0L);
         }
      }
   }

   private int jjMoveStringLiteralDfa4_2(long old1, long active1, long old2, long active2) {
      if (((active1 &= old1) | (active2 &= old2)) == 0L) {
         return this.jjStartNfa_2(2, 0L, old1, old2);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var10) {
            this.jjStopStringLiteralDfa_2(3, 0L, active1, active2);
            return 4;
         }

         switch (this.curChar) {
            case 101:
               if ((active1 & 2147483648L) != 0L) {
                  return this.jjStartNfaWithStates_2(4, 95, 104);
               }
               break;
            case 103:
               if ((active2 & 8192L) != 0L) {
                  return this.jjStartNfaWithStates_2(4, 141, 104);
               }
         }

         return this.jjStartNfa_2(3, 0L, active1, active2);
      }
   }

   private int jjStartNfaWithStates_2(int pos, int kind, int state) {
      this.jjmatchedKind = kind;
      this.jjmatchedPos = pos;

      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var5) {
         return pos + 1;
      }

      return this.jjMoveNfa_2(state, pos + 1);
   }

   private int jjMoveNfa_2(int startState, int curPos) {
      int startsAt = 0;
      this.jjnewStateCnt = 104;
      int i = 1;
      this.jjstateSet[0] = startState;
      int kind = Integer.MAX_VALUE;

      while(true) {
         if (++this.jjround == Integer.MAX_VALUE) {
            this.ReInitRounds();
         }

         long l;
         if (this.curChar < 64) {
            l = 1L << this.curChar;

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 0:
                     if ((4294977024L & l) != 0L) {
                        if (kind > 85) {
                           kind = 85;
                        }

                        this.jjCheckNAdd(0);
                     }
                     break;
                  case 1:
                     if ((287948901175001088L & l) != 0L) {
                        if (kind > 97) {
                           kind = 97;
                        }

                        this.jjCheckNAddStates(343, 345);
                     } else if ((4294977024L & l) != 0L) {
                        if (kind > 85) {
                           kind = 85;
                        }

                        this.jjCheckNAdd(0);
                     } else if (this.curChar == 38) {
                        this.jjAddStates(346, 351);
                     } else if (this.curChar == 46) {
                        this.jjAddStates(352, 353);
                     } else if (this.curChar == 45) {
                        this.jjAddStates(354, 355);
                     } else if (this.curChar == 47) {
                        this.jjAddStates(356, 357);
                     } else if (this.curChar == 35) {
                        this.jjCheckNAdd(38);
                     } else if (this.curChar == 36) {
                        this.jjCheckNAdd(38);
                     } else if (this.curChar == 60) {
                        this.jjCheckNAdd(27);
                     } else if (this.curChar == 39) {
                        this.jjCheckNAddStates(358, 360);
                     } else if (this.curChar == 34) {
                        this.jjCheckNAddStates(361, 363);
                     }

                     if (this.curChar == 36) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     } else if (this.curChar == 38) {
                        if (kind > 127) {
                           kind = 127;
                        }
                     } else if (this.curChar == 60 && kind > 115) {
                        kind = 115;
                     }

                     if (this.curChar == 60) {
                        this.jjstateSet[this.jjnewStateCnt++] = 2;
                     }
                     break;
                  case 2:
                     if ((42949672960L & l) != 0L) {
                        this.jjstateSet[this.jjnewStateCnt++] = 4;
                     } else if (this.curChar == 61 && kind > 143) {
                        kind = 143;
                     }
                     break;
                  case 3:
                     if (this.curChar == 45 && kind > 86) {
                        kind = 86;
                     }
                     break;
                  case 4:
                     if (this.curChar == 45) {
                        this.jjstateSet[this.jjnewStateCnt++] = 3;
                     }
                     break;
                  case 5:
                     if (this.curChar == 34) {
                        this.jjCheckNAddStates(361, 363);
                     }
                     break;
                  case 6:
                     if ((-17179869185L & l) != 0L) {
                        this.jjCheckNAddStates(361, 363);
                     }
                  case 7:
                  case 8:
                  case 14:
                  case 15:
                  case 19:
                  case 30:
                  case 31:
                  case 32:
                  case 35:
                  case 37:
                  case 38:
                  case 42:
                  case 45:
                  case 49:
                  case 50:
                  case 62:
                  case 63:
                  case 64:
                  case 65:
                  case 66:
                  case 67:
                  case 68:
                  case 69:
                  case 70:
                  case 71:
                  case 72:
                  case 73:
                  case 74:
                  case 75:
                  case 76:
                  case 77:
                  case 80:
                  case 81:
                  case 83:
                  case 84:
                  case 86:
                  case 87:
                  case 90:
                  case 91:
                  case 94:
                  case 95:
                  case 96:
                  case 99:
                  case 100:
                  case 101:
                  case 102:
                  case 103:
                  default:
                     break;
                  case 9:
                     if ((287948901175001088L & l) != 0L) {
                        this.jjCheckNAddStates(361, 363);
                     }
                     break;
                  case 10:
                     if (this.curChar == 34 && kind > 93) {
                        kind = 93;
                     }
                     break;
                  case 11:
                     if ((2305843576149377024L & l) != 0L) {
                        this.jjCheckNAddStates(361, 363);
                     }
                     break;
                  case 12:
                     if (this.curChar == 39) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 13:
                     if ((-549755813889L & l) != 0L) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 16:
                     if ((287948901175001088L & l) != 0L) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 17:
                     if (this.curChar == 39 && kind > 93) {
                        kind = 93;
                     }
                     break;
                  case 18:
                     if ((2305843576149377024L & l) != 0L) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 20:
                     if (this.curChar == 34) {
                        this.jjCheckNAddTwoStates(21, 22);
                     }
                     break;
                  case 21:
                     if ((-17179869185L & l) != 0L) {
                        this.jjCheckNAddTwoStates(21, 22);
                     }
                     break;
                  case 22:
                     if (this.curChar == 34 && kind > 94) {
                        kind = 94;
                     }
                     break;
                  case 23:
                     if (this.curChar == 39) {
                        this.jjCheckNAddTwoStates(24, 25);
                     }
                     break;
                  case 24:
                     if ((-549755813889L & l) != 0L) {
                        this.jjCheckNAddTwoStates(24, 25);
                     }
                     break;
                  case 25:
                     if (this.curChar == 39 && kind > 94) {
                        kind = 94;
                     }
                     break;
                  case 26:
                     if (this.curChar == 60 && kind > 115) {
                        kind = 115;
                     }
                     break;
                  case 27:
                     if (this.curChar == 61 && kind > 116) {
                        kind = 116;
                     }
                     break;
                  case 28:
                     if (this.curChar == 60) {
                        this.jjCheckNAdd(27);
                     }
                     break;
                  case 29:
                  case 92:
                     if (this.curChar == 38 && kind > 127) {
                        kind = 127;
                     }
                     break;
                  case 33:
                     if (this.curChar == 36) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  case 34:
                  case 104:
                     if ((287948969894477824L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  case 36:
                     if ((288335963627716608L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  case 39:
                     if (this.curChar == 36) {
                        this.jjCheckNAdd(38);
                     }
                     break;
                  case 40:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(38);
                     }
                     break;
                  case 41:
                     if (this.curChar == 61 && kind > 143) {
                        kind = 143;
                     }
                     break;
                  case 43:
                     if (this.curChar == 47) {
                        this.jjAddStates(356, 357);
                     }
                     break;
                  case 44:
                     if (this.curChar == 62 && kind > 149) {
                        kind = 149;
                     }
                     break;
                  case 46:
                     if (this.curChar == 45) {
                        this.jjAddStates(354, 355);
                     }
                     break;
                  case 47:
                     if (this.curChar == 38) {
                        this.jjstateSet[this.jjnewStateCnt++] = 50;
                     } else if (this.curChar == 62 && kind > 119) {
                        kind = 119;
                     }
                     break;
                  case 48:
                     if (this.curChar == 59 && kind > 119) {
                        kind = 119;
                     }
                     break;
                  case 51:
                     if (this.curChar == 38) {
                        this.jjstateSet[this.jjnewStateCnt++] = 50;
                     }
                     break;
                  case 52:
                     if (this.curChar == 46) {
                        this.jjAddStates(352, 353);
                     }
                     break;
                  case 53:
                     if (this.curChar == 33) {
                        if (kind > 101) {
                           kind = 101;
                        }
                     } else if (this.curChar == 60 && kind > 101) {
                        kind = 101;
                     }
                     break;
                  case 54:
                     if (this.curChar == 46) {
                        this.jjstateSet[this.jjnewStateCnt++] = 55;
                     }

                     if (this.curChar == 46) {
                        this.jjstateSet[this.jjnewStateCnt++] = 53;
                     }
                     break;
                  case 55:
                     if (this.curChar == 33 && kind > 101) {
                        kind = 101;
                     }
                     break;
                  case 56:
                     if (this.curChar == 46) {
                        this.jjstateSet[this.jjnewStateCnt++] = 55;
                     }
                     break;
                  case 57:
                     if ((287948901175001088L & l) != 0L) {
                        if (kind > 97) {
                           kind = 97;
                        }

                        this.jjCheckNAddStates(343, 345);
                     }
                     break;
                  case 58:
                     if ((287948901175001088L & l) != 0L) {
                        if (kind > 97) {
                           kind = 97;
                        }

                        this.jjCheckNAdd(58);
                     }
                     break;
                  case 59:
                     if ((287948901175001088L & l) != 0L) {
                        this.jjCheckNAddTwoStates(59, 60);
                     }
                     break;
                  case 60:
                     if (this.curChar == 46) {
                        this.jjCheckNAdd(61);
                     }
                     break;
                  case 61:
                     if ((287948901175001088L & l) != 0L) {
                        if (kind > 98) {
                           kind = 98;
                        }

                        this.jjCheckNAdd(61);
                     }
                     break;
                  case 78:
                     if (this.curChar == 38) {
                        this.jjAddStates(346, 351);
                     }
                     break;
                  case 79:
                     if (this.curChar == 59 && kind > 115) {
                        kind = 115;
                     }
                     break;
                  case 82:
                     if (this.curChar == 59) {
                        this.jjCheckNAdd(27);
                     }
                     break;
                  case 85:
                     if (this.curChar == 59 && kind > 117) {
                        kind = 117;
                     }
                     break;
                  case 88:
                     if (this.curChar == 61 && kind > 118) {
                        kind = 118;
                     }
                     break;
                  case 89:
                     if (this.curChar == 59) {
                        this.jjstateSet[this.jjnewStateCnt++] = 88;
                     }
                     break;
                  case 93:
                     if (this.curChar == 59 && kind > 127) {
                        kind = 127;
                     }
                     break;
                  case 97:
                     if (this.curChar == 38) {
                        this.jjstateSet[this.jjnewStateCnt++] = 96;
                     }
                     break;
                  case 98:
                     if (this.curChar == 59) {
                        this.jjstateSet[this.jjnewStateCnt++] = 97;
                     }
               }
            } while(i != startsAt);
         } else if (this.curChar < 128) {
            l = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 1:
                     if ((576460745995190271L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     } else if (this.curChar == 92) {
                        this.jjAddStates(364, 368);
                     } else if (this.curChar == 91) {
                        this.jjstateSet[this.jjnewStateCnt++] = 41;
                     } else if (this.curChar == 124) {
                        this.jjstateSet[this.jjnewStateCnt++] = 31;
                     }

                     if (this.curChar == 103) {
                        this.jjCheckNAddTwoStates(70, 103);
                     } else if (this.curChar == 108) {
                        this.jjCheckNAddTwoStates(63, 65);
                     } else if (this.curChar == 92) {
                        this.jjCheckNAdd(36);
                     } else if (this.curChar == 124) {
                        if (kind > 128) {
                           kind = 128;
                        }
                     } else if (this.curChar == 114) {
                        this.jjAddStates(369, 370);
                     } else if (this.curChar == 91) {
                        this.jjstateSet[this.jjnewStateCnt++] = 2;
                     }
                  case 2:
                  case 3:
                  case 4:
                  case 5:
                  case 10:
                  case 12:
                  case 17:
                  case 20:
                  case 22:
                  case 23:
                  case 25:
                  case 26:
                  case 27:
                  case 28:
                  case 29:
                  case 36:
                  case 39:
                  case 40:
                  case 41:
                  case 43:
                  case 45:
                  case 46:
                  case 47:
                  case 48:
                  case 51:
                  case 52:
                  case 53:
                  case 54:
                  case 55:
                  case 56:
                  case 57:
                  case 58:
                  case 59:
                  case 60:
                  case 61:
                  case 78:
                  case 79:
                  case 82:
                  case 85:
                  case 88:
                  case 89:
                  case 92:
                  case 93:
                  case 97:
                  case 98:
                  default:
                     break;
                  case 6:
                     if ((-268435457L & l) != 0L) {
                        this.jjCheckNAddStates(361, 363);
                     }
                     break;
                  case 7:
                     if (this.curChar == 92) {
                        this.jjAddStates(371, 372);
                     }
                     break;
                  case 8:
                     if (this.curChar == 120) {
                        this.jjstateSet[this.jjnewStateCnt++] = 9;
                     }
                     break;
                  case 9:
                     if ((541165879422L & l) != 0L) {
                        this.jjCheckNAddStates(361, 363);
                     }
                     break;
                  case 11:
                     if ((582179063439818752L & l) != 0L) {
                        this.jjCheckNAddStates(361, 363);
                     }
                     break;
                  case 13:
                     if ((-268435457L & l) != 0L) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 14:
                     if (this.curChar == 92) {
                        this.jjAddStates(373, 374);
                     }
                     break;
                  case 15:
                     if (this.curChar == 120) {
                        this.jjstateSet[this.jjnewStateCnt++] = 16;
                     }
                     break;
                  case 16:
                     if ((541165879422L & l) != 0L) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 18:
                     if ((582179063439818752L & l) != 0L) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 19:
                     if (this.curChar == 114) {
                        this.jjAddStates(369, 370);
                     }
                     break;
                  case 21:
                     this.jjAddStates(375, 376);
                     break;
                  case 24:
                     this.jjAddStates(377, 378);
                     break;
                  case 30:
                  case 31:
                     if (this.curChar == 124 && kind > 128) {
                        kind = 128;
                     }
                     break;
                  case 32:
                     if (this.curChar == 124) {
                        this.jjstateSet[this.jjnewStateCnt++] = 31;
                     }
                     break;
                  case 33:
                     if ((576460745995190271L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  case 34:
                     if ((576460745995190271L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  case 35:
                     if (this.curChar == 92) {
                        this.jjCheckNAdd(36);
                     }
                     break;
                  case 37:
                     if (this.curChar == 92) {
                        this.jjCheckNAdd(36);
                     }
                     break;
                  case 38:
                     if (this.curChar == 123 && kind > 143) {
                        kind = 143;
                     }
                     break;
                  case 42:
                     if (this.curChar == 91) {
                        this.jjstateSet[this.jjnewStateCnt++] = 41;
                     }
                     break;
                  case 44:
                     if (this.curChar == 93 && kind > 149) {
                        kind = 149;
                     }
                     break;
                  case 49:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 48;
                     }
                     break;
                  case 50:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 49;
                     }
                     break;
                  case 62:
                     if (this.curChar == 108) {
                        this.jjCheckNAddTwoStates(63, 65);
                     }
                     break;
                  case 63:
                     if (this.curChar == 116 && kind > 115) {
                        kind = 115;
                     }
                     break;
                  case 64:
                     if (this.curChar == 101 && kind > 116) {
                        kind = 116;
                     }
                     break;
                  case 65:
                  case 68:
                     if (this.curChar == 116) {
                        this.jjCheckNAdd(64);
                     }
                     break;
                  case 66:
                     if (this.curChar == 92) {
                        this.jjAddStates(364, 368);
                     }
                     break;
                  case 67:
                     if (this.curChar == 108) {
                        this.jjCheckNAdd(63);
                     }
                     break;
                  case 69:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 68;
                     }
                     break;
                  case 70:
                     if (this.curChar == 116 && kind > 117) {
                        kind = 117;
                     }
                     break;
                  case 71:
                     if (this.curChar == 103) {
                        this.jjCheckNAdd(70);
                     }
                     break;
                  case 72:
                     if (this.curChar == 101 && kind > 118) {
                        kind = 118;
                     }
                     break;
                  case 73:
                  case 103:
                     if (this.curChar == 116) {
                        this.jjCheckNAdd(72);
                     }
                     break;
                  case 74:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 73;
                     }
                     break;
                  case 75:
                     if (this.curChar == 100 && kind > 127) {
                        kind = 127;
                     }
                     break;
                  case 76:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 75;
                     }
                     break;
                  case 77:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 76;
                     }
                     break;
                  case 80:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 79;
                     }
                     break;
                  case 81:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 80;
                     }
                     break;
                  case 83:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 82;
                     }
                     break;
                  case 84:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 83;
                     }
                     break;
                  case 86:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 85;
                     }
                     break;
                  case 87:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 86;
                     }
                     break;
                  case 90:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 89;
                     }
                     break;
                  case 91:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 90;
                     }
                     break;
                  case 94:
                     if (this.curChar == 112) {
                        this.jjstateSet[this.jjnewStateCnt++] = 93;
                     }
                     break;
                  case 95:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 94;
                     }
                     break;
                  case 96:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 95;
                     }
                     break;
                  case 99:
                     if (this.curChar == 112) {
                        this.jjstateSet[this.jjnewStateCnt++] = 98;
                     }
                     break;
                  case 100:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 99;
                     }
                     break;
                  case 101:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 100;
                     }
                     break;
                  case 102:
                     if (this.curChar == 103) {
                        this.jjCheckNAddTwoStates(70, 103);
                     }
                     break;
                  case 104:
                     if ((576460745995190271L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     } else if (this.curChar == 92) {
                        this.jjCheckNAdd(36);
                     }
               }
            } while(i != startsAt);
         } else {
            int hiByte = this.curChar >> 8;
            int i1 = hiByte >> 6;
            long l1 = 1L << (hiByte & 63);
            int i2 = (this.curChar & 255) >> 6;
            long l2 = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 1:
                     if (jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  case 6:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        this.jjAddStates(361, 363);
                     }
                     break;
                  case 13:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        this.jjAddStates(358, 360);
                     }
                     break;
                  case 21:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        this.jjAddStates(375, 376);
                     }
                     break;
                  case 24:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        this.jjAddStates(377, 378);
                     }
                     break;
                  case 34:
                  case 104:
                     if (jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  default:
                     if (i1 != 0 && l1 != 0L && i2 != 0 && l2 == 0L) {
                     }
               }
            } while(i != startsAt);
         }

         if (kind != Integer.MAX_VALUE) {
            this.jjmatchedKind = kind;
            this.jjmatchedPos = curPos;
            kind = Integer.MAX_VALUE;
         }

         ++curPos;
         if ((i = this.jjnewStateCnt) == (startsAt = 104 - (this.jjnewStateCnt = startsAt))) {
            return curPos;
         }

         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var13) {
            return curPos;
         }
      }
   }

   private final int jjStopStringLiteralDfa_3(int pos, long active0, long active1, long active2) {
      switch (pos) {
         case 0:
            if ((active2 & 32L) != 0L) {
               return 2;
            } else {
               if ((active1 & 6442450944L) == 0L && (active2 & 14336L) == 0L) {
                  if ((active1 & 1152921882563969024L) != 0L) {
                     return 51;
                  }

                  if ((active1 & 145276272354787328L) != 0L) {
                     return 44;
                  }

                  return -1;
               }

               this.jjmatchedKind = 142;
               return 101;
            }
         case 1:
            if ((active2 & 6144L) != 0L) {
               return 101;
            } else if ((active1 & 1152921848204230656L) != 0L) {
               return 50;
            } else {
               if ((active1 & 6442450944L) == 0L && (active2 & 8192L) == 0L) {
                  return -1;
               }

               if (this.jjmatchedPos != 1) {
                  this.jjmatchedKind = 142;
                  this.jjmatchedPos = 1;
               }

               return 101;
            }
         case 2:
            if ((active1 & 6442450944L) == 0L && (active2 & 8192L) == 0L) {
               return -1;
            }

            this.jjmatchedKind = 142;
            this.jjmatchedPos = 2;
            return 101;
         case 3:
            if ((active1 & 4294967296L) != 0L) {
               return 101;
            } else {
               if ((active1 & 2147483648L) == 0L && (active2 & 8192L) == 0L) {
                  return -1;
               }

               this.jjmatchedKind = 142;
               this.jjmatchedPos = 3;
               return 101;
            }
         default:
            return -1;
      }
   }

   private final int jjStartNfa_3(int pos, long active0, long active1, long active2) {
      return this.jjMoveNfa_3(this.jjStopStringLiteralDfa_3(pos, active0, active1, active2), pos + 1);
   }

   private int jjMoveStringLiteralDfa0_3() {
      switch (this.curChar) {
         case 33:
            this.jjmatchedKind = 129;
            return this.jjMoveStringLiteralDfa1_3(8796093022208L, 0L);
         case 34:
         case 35:
         case 36:
         case 38:
         case 39:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
         case 60:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 92:
         case 94:
         case 95:
         case 96:
         case 98:
         case 99:
         case 100:
         case 101:
         case 103:
         case 104:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 115:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 124:
         default:
            return this.jjMoveNfa_3(1, 0);
         case 37:
            this.jjmatchedKind = 126;
            return this.jjMoveStringLiteralDfa1_3(281474976710656L, 0L);
         case 40:
            return this.jjStopAtPos(0, 135);
         case 41:
            return this.jjStopAtPos(0, 136);
         case 42:
            this.jjmatchedKind = 122;
            return this.jjMoveStringLiteralDfa1_3(576531121047601152L, 0L);
         case 43:
            this.jjmatchedKind = 120;
            return this.jjMoveStringLiteralDfa1_3(580542139465728L, 0L);
         case 44:
            return this.jjStopAtPos(0, 130);
         case 45:
            this.jjmatchedKind = 121;
            return this.jjMoveStringLiteralDfa1_3(1161084278931456L, 0L);
         case 46:
            this.jjmatchedKind = 99;
            return this.jjMoveStringLiteralDfa1_3(1152921848204230656L, 0L);
         case 47:
            this.jjmatchedKind = 125;
            return this.jjMoveStringLiteralDfa1_3(140737488355328L, 0L);
         case 58:
            return this.jjStopAtPos(0, 132);
         case 59:
            return this.jjStopAtPos(0, 131);
         case 61:
            this.jjmatchedKind = 105;
            return this.jjMoveStringLiteralDfa1_3(4398046511104L, 0L);
         case 62:
            this.jjmatchedKind = 150;
            return this.jjMoveStringLiteralDfa1_3(0L, 8388608L);
         case 63:
            this.jjmatchedKind = 103;
            return this.jjMoveStringLiteralDfa1_3(1099511627776L, 0L);
         case 91:
            return this.jjStartNfaWithStates_3(0, 133, 2);
         case 93:
            return this.jjStopAtPos(0, 134);
         case 97:
            return this.jjMoveStringLiteralDfa1_3(0L, 4096L);
         case 102:
            return this.jjMoveStringLiteralDfa1_3(2147483648L, 0L);
         case 105:
            return this.jjMoveStringLiteralDfa1_3(0L, 2048L);
         case 116:
            return this.jjMoveStringLiteralDfa1_3(4294967296L, 0L);
         case 117:
            return this.jjMoveStringLiteralDfa1_3(0L, 8192L);
         case 123:
            return this.jjStopAtPos(0, 137);
         case 125:
            return this.jjStopAtPos(0, 138);
      }
   }

   private int jjMoveStringLiteralDfa1_3(long active1, long active2) {
      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var6) {
         this.jjStopStringLiteralDfa_3(0, 0L, active1, active2);
         return 1;
      }

      switch (this.curChar) {
         case 42:
            if ((active1 & 576460752303423488L) != 0L) {
               return this.jjStopAtPos(1, 123);
            }
            break;
         case 43:
            if ((active1 & 562949953421312L) != 0L) {
               return this.jjStopAtPos(1, 113);
            }
            break;
         case 45:
            if ((active1 & 1125899906842624L) != 0L) {
               return this.jjStopAtPos(1, 114);
            }
            break;
         case 46:
            if ((active1 & 68719476736L) != 0L) {
               this.jjmatchedKind = 100;
               this.jjmatchedPos = 1;
            }

            return this.jjMoveStringLiteralDfa2_3(active1, 1152921779484753920L, active2, 0L);
         case 61:
            if ((active1 & 4398046511104L) != 0L) {
               return this.jjStopAtPos(1, 106);
            }

            if ((active1 & 8796093022208L) != 0L) {
               return this.jjStopAtPos(1, 107);
            }

            if ((active1 & 17592186044416L) != 0L) {
               return this.jjStopAtPos(1, 108);
            }

            if ((active1 & 35184372088832L) != 0L) {
               return this.jjStopAtPos(1, 109);
            }

            if ((active1 & 70368744177664L) != 0L) {
               return this.jjStopAtPos(1, 110);
            }

            if ((active1 & 140737488355328L) != 0L) {
               return this.jjStopAtPos(1, 111);
            }

            if ((active1 & 281474976710656L) != 0L) {
               return this.jjStopAtPos(1, 112);
            }

            if ((active2 & 8388608L) != 0L) {
               return this.jjStopAtPos(1, 151);
            }
            break;
         case 63:
            if ((active1 & 1099511627776L) != 0L) {
               return this.jjStopAtPos(1, 104);
            }
            break;
         case 97:
            return this.jjMoveStringLiteralDfa2_3(active1, 2147483648L, active2, 0L);
         case 110:
            if ((active2 & 2048L) != 0L) {
               return this.jjStartNfaWithStates_3(1, 139, 101);
            }
            break;
         case 114:
            return this.jjMoveStringLiteralDfa2_3(active1, 4294967296L, active2, 0L);
         case 115:
            if ((active2 & 4096L) != 0L) {
               return this.jjStartNfaWithStates_3(1, 140, 101);
            }

            return this.jjMoveStringLiteralDfa2_3(active1, 0L, active2, 8192L);
      }

      return this.jjStartNfa_3(0, 0L, active1, active2);
   }

   private int jjMoveStringLiteralDfa2_3(long old1, long active1, long old2, long active2) {
      if (((active1 &= old1) | (active2 &= old2)) == 0L) {
         return this.jjStartNfa_3(0, 0L, old1, old2);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var10) {
            this.jjStopStringLiteralDfa_3(1, 0L, active1, active2);
            return 2;
         }

         switch (this.curChar) {
            case 42:
               if ((active1 & 274877906944L) != 0L) {
                  return this.jjStopAtPos(2, 102);
               }
               break;
            case 46:
               if ((active1 & 1152921504606846976L) != 0L) {
                  return this.jjStopAtPos(2, 124);
               }
               break;
            case 105:
               return this.jjMoveStringLiteralDfa3_3(active1, 0L, active2, 8192L);
            case 108:
               return this.jjMoveStringLiteralDfa3_3(active1, 2147483648L, active2, 0L);
            case 117:
               return this.jjMoveStringLiteralDfa3_3(active1, 4294967296L, active2, 0L);
         }

         return this.jjStartNfa_3(1, 0L, active1, active2);
      }
   }

   private int jjMoveStringLiteralDfa3_3(long old1, long active1, long old2, long active2) {
      if (((active1 &= old1) | (active2 &= old2)) == 0L) {
         return this.jjStartNfa_3(1, 0L, old1, old2);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var10) {
            this.jjStopStringLiteralDfa_3(2, 0L, active1, active2);
            return 3;
         }

         switch (this.curChar) {
            case 101:
               if ((active1 & 4294967296L) != 0L) {
                  return this.jjStartNfaWithStates_3(3, 96, 101);
               }
            default:
               return this.jjStartNfa_3(2, 0L, active1, active2);
            case 110:
               return this.jjMoveStringLiteralDfa4_3(active1, 0L, active2, 8192L);
            case 115:
               return this.jjMoveStringLiteralDfa4_3(active1, 2147483648L, active2, 0L);
         }
      }
   }

   private int jjMoveStringLiteralDfa4_3(long old1, long active1, long old2, long active2) {
      if (((active1 &= old1) | (active2 &= old2)) == 0L) {
         return this.jjStartNfa_3(2, 0L, old1, old2);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var10) {
            this.jjStopStringLiteralDfa_3(3, 0L, active1, active2);
            return 4;
         }

         switch (this.curChar) {
            case 101:
               if ((active1 & 2147483648L) != 0L) {
                  return this.jjStartNfaWithStates_3(4, 95, 101);
               }
               break;
            case 103:
               if ((active2 & 8192L) != 0L) {
                  return this.jjStartNfaWithStates_3(4, 141, 101);
               }
         }

         return this.jjStartNfa_3(3, 0L, active1, active2);
      }
   }

   private int jjStartNfaWithStates_3(int pos, int kind, int state) {
      this.jjmatchedKind = kind;
      this.jjmatchedPos = pos;

      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var5) {
         return pos + 1;
      }

      return this.jjMoveNfa_3(state, pos + 1);
   }

   private int jjMoveNfa_3(int startState, int curPos) {
      int startsAt = 0;
      this.jjnewStateCnt = 101;
      int i = 1;
      this.jjstateSet[0] = startState;
      int kind = Integer.MAX_VALUE;

      while(true) {
         if (++this.jjround == Integer.MAX_VALUE) {
            this.ReInitRounds();
         }

         long l;
         if (this.curChar < 64) {
            l = 1L << this.curChar;

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 0:
                     if ((4294977024L & l) != 0L) {
                        if (kind > 85) {
                           kind = 85;
                        }

                        this.jjCheckNAdd(0);
                     }
                     break;
                  case 1:
                     if ((287948901175001088L & l) != 0L) {
                        if (kind > 97) {
                           kind = 97;
                        }

                        this.jjCheckNAddStates(379, 381);
                     } else if ((4294977024L & l) != 0L) {
                        if (kind > 85) {
                           kind = 85;
                        }

                        this.jjCheckNAdd(0);
                     } else if (this.curChar == 38) {
                        this.jjAddStates(382, 387);
                     } else if (this.curChar == 46) {
                        this.jjAddStates(388, 389);
                     } else if (this.curChar == 45) {
                        this.jjAddStates(390, 391);
                     } else if (this.curChar == 35) {
                        this.jjCheckNAdd(38);
                     } else if (this.curChar == 36) {
                        this.jjCheckNAdd(38);
                     } else if (this.curChar == 60) {
                        this.jjCheckNAdd(27);
                     } else if (this.curChar == 39) {
                        this.jjCheckNAddStates(358, 360);
                     } else if (this.curChar == 34) {
                        this.jjCheckNAddStates(361, 363);
                     }

                     if (this.curChar == 36) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     } else if (this.curChar == 38) {
                        if (kind > 127) {
                           kind = 127;
                        }
                     } else if (this.curChar == 60 && kind > 115) {
                        kind = 115;
                     }

                     if (this.curChar == 60) {
                        this.jjstateSet[this.jjnewStateCnt++] = 2;
                     }
                     break;
                  case 2:
                     if ((42949672960L & l) != 0L) {
                        this.jjstateSet[this.jjnewStateCnt++] = 4;
                     } else if (this.curChar == 61 && kind > 143) {
                        kind = 143;
                     }
                     break;
                  case 3:
                     if (this.curChar == 45 && kind > 86) {
                        kind = 86;
                     }
                     break;
                  case 4:
                     if (this.curChar == 45) {
                        this.jjstateSet[this.jjnewStateCnt++] = 3;
                     }
                     break;
                  case 5:
                     if (this.curChar == 34) {
                        this.jjCheckNAddStates(361, 363);
                     }
                     break;
                  case 6:
                     if ((-17179869185L & l) != 0L) {
                        this.jjCheckNAddStates(361, 363);
                     }
                  case 7:
                  case 8:
                  case 14:
                  case 15:
                  case 19:
                  case 30:
                  case 31:
                  case 32:
                  case 35:
                  case 37:
                  case 38:
                  case 42:
                  case 46:
                  case 47:
                  case 59:
                  case 60:
                  case 61:
                  case 62:
                  case 63:
                  case 64:
                  case 65:
                  case 66:
                  case 67:
                  case 68:
                  case 69:
                  case 70:
                  case 71:
                  case 72:
                  case 73:
                  case 74:
                  case 77:
                  case 78:
                  case 80:
                  case 81:
                  case 83:
                  case 84:
                  case 87:
                  case 88:
                  case 91:
                  case 92:
                  case 93:
                  case 96:
                  case 97:
                  case 98:
                  case 99:
                  case 100:
                  default:
                     break;
                  case 9:
                     if ((287948901175001088L & l) != 0L) {
                        this.jjCheckNAddStates(361, 363);
                     }
                     break;
                  case 10:
                     if (this.curChar == 34 && kind > 93) {
                        kind = 93;
                     }
                     break;
                  case 11:
                     if ((2305843576149377024L & l) != 0L) {
                        this.jjCheckNAddStates(361, 363);
                     }
                     break;
                  case 12:
                     if (this.curChar == 39) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 13:
                     if ((-549755813889L & l) != 0L) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 16:
                     if ((287948901175001088L & l) != 0L) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 17:
                     if (this.curChar == 39 && kind > 93) {
                        kind = 93;
                     }
                     break;
                  case 18:
                     if ((2305843576149377024L & l) != 0L) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 20:
                     if (this.curChar == 34) {
                        this.jjCheckNAddTwoStates(21, 22);
                     }
                     break;
                  case 21:
                     if ((-17179869185L & l) != 0L) {
                        this.jjCheckNAddTwoStates(21, 22);
                     }
                     break;
                  case 22:
                     if (this.curChar == 34 && kind > 94) {
                        kind = 94;
                     }
                     break;
                  case 23:
                     if (this.curChar == 39) {
                        this.jjCheckNAddTwoStates(24, 25);
                     }
                     break;
                  case 24:
                     if ((-549755813889L & l) != 0L) {
                        this.jjCheckNAddTwoStates(24, 25);
                     }
                     break;
                  case 25:
                     if (this.curChar == 39 && kind > 94) {
                        kind = 94;
                     }
                     break;
                  case 26:
                     if (this.curChar == 60 && kind > 115) {
                        kind = 115;
                     }
                     break;
                  case 27:
                     if (this.curChar == 61 && kind > 116) {
                        kind = 116;
                     }
                     break;
                  case 28:
                     if (this.curChar == 60) {
                        this.jjCheckNAdd(27);
                     }
                     break;
                  case 29:
                  case 89:
                     if (this.curChar == 38 && kind > 127) {
                        kind = 127;
                     }
                     break;
                  case 33:
                     if (this.curChar == 36) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  case 34:
                  case 101:
                     if ((287948969894477824L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  case 36:
                     if ((288335963627716608L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  case 39:
                     if (this.curChar == 36) {
                        this.jjCheckNAdd(38);
                     }
                     break;
                  case 40:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(38);
                     }
                     break;
                  case 41:
                     if (this.curChar == 61 && kind > 143) {
                        kind = 143;
                     }
                     break;
                  case 43:
                     if (this.curChar == 45) {
                        this.jjAddStates(390, 391);
                     }
                     break;
                  case 44:
                     if (this.curChar == 38) {
                        this.jjstateSet[this.jjnewStateCnt++] = 47;
                     } else if (this.curChar == 62 && kind > 119) {
                        kind = 119;
                     }
                     break;
                  case 45:
                     if (this.curChar == 59 && kind > 119) {
                        kind = 119;
                     }
                     break;
                  case 48:
                     if (this.curChar == 38) {
                        this.jjstateSet[this.jjnewStateCnt++] = 47;
                     }
                     break;
                  case 49:
                     if (this.curChar == 46) {
                        this.jjAddStates(388, 389);
                     }
                     break;
                  case 50:
                     if (this.curChar == 33) {
                        if (kind > 101) {
                           kind = 101;
                        }
                     } else if (this.curChar == 60 && kind > 101) {
                        kind = 101;
                     }
                     break;
                  case 51:
                     if (this.curChar == 46) {
                        this.jjstateSet[this.jjnewStateCnt++] = 52;
                     }

                     if (this.curChar == 46) {
                        this.jjstateSet[this.jjnewStateCnt++] = 50;
                     }
                     break;
                  case 52:
                     if (this.curChar == 33 && kind > 101) {
                        kind = 101;
                     }
                     break;
                  case 53:
                     if (this.curChar == 46) {
                        this.jjstateSet[this.jjnewStateCnt++] = 52;
                     }
                     break;
                  case 54:
                     if ((287948901175001088L & l) != 0L) {
                        if (kind > 97) {
                           kind = 97;
                        }

                        this.jjCheckNAddStates(379, 381);
                     }
                     break;
                  case 55:
                     if ((287948901175001088L & l) != 0L) {
                        if (kind > 97) {
                           kind = 97;
                        }

                        this.jjCheckNAdd(55);
                     }
                     break;
                  case 56:
                     if ((287948901175001088L & l) != 0L) {
                        this.jjCheckNAddTwoStates(56, 57);
                     }
                     break;
                  case 57:
                     if (this.curChar == 46) {
                        this.jjCheckNAdd(58);
                     }
                     break;
                  case 58:
                     if ((287948901175001088L & l) != 0L) {
                        if (kind > 98) {
                           kind = 98;
                        }

                        this.jjCheckNAdd(58);
                     }
                     break;
                  case 75:
                     if (this.curChar == 38) {
                        this.jjAddStates(382, 387);
                     }
                     break;
                  case 76:
                     if (this.curChar == 59 && kind > 115) {
                        kind = 115;
                     }
                     break;
                  case 79:
                     if (this.curChar == 59) {
                        this.jjCheckNAdd(27);
                     }
                     break;
                  case 82:
                     if (this.curChar == 59 && kind > 117) {
                        kind = 117;
                     }
                     break;
                  case 85:
                     if (this.curChar == 61 && kind > 118) {
                        kind = 118;
                     }
                     break;
                  case 86:
                     if (this.curChar == 59) {
                        this.jjstateSet[this.jjnewStateCnt++] = 85;
                     }
                     break;
                  case 90:
                     if (this.curChar == 59 && kind > 127) {
                        kind = 127;
                     }
                     break;
                  case 94:
                     if (this.curChar == 38) {
                        this.jjstateSet[this.jjnewStateCnt++] = 93;
                     }
                     break;
                  case 95:
                     if (this.curChar == 59) {
                        this.jjstateSet[this.jjnewStateCnt++] = 94;
                     }
               }
            } while(i != startsAt);
         } else if (this.curChar < 128) {
            l = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 1:
                     if ((576460745995190271L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     } else if (this.curChar == 92) {
                        this.jjAddStates(392, 396);
                     } else if (this.curChar == 91) {
                        this.jjstateSet[this.jjnewStateCnt++] = 41;
                     } else if (this.curChar == 124) {
                        this.jjstateSet[this.jjnewStateCnt++] = 31;
                     }

                     if (this.curChar == 103) {
                        this.jjCheckNAddTwoStates(67, 100);
                     } else if (this.curChar == 108) {
                        this.jjCheckNAddTwoStates(60, 62);
                     } else if (this.curChar == 92) {
                        this.jjCheckNAdd(36);
                     } else if (this.curChar == 124) {
                        if (kind > 128) {
                           kind = 128;
                        }
                     } else if (this.curChar == 114) {
                        this.jjAddStates(369, 370);
                     } else if (this.curChar == 91) {
                        this.jjstateSet[this.jjnewStateCnt++] = 2;
                     }
                  case 2:
                  case 3:
                  case 4:
                  case 5:
                  case 10:
                  case 12:
                  case 17:
                  case 20:
                  case 22:
                  case 23:
                  case 25:
                  case 26:
                  case 27:
                  case 28:
                  case 29:
                  case 36:
                  case 39:
                  case 40:
                  case 41:
                  case 43:
                  case 44:
                  case 45:
                  case 48:
                  case 49:
                  case 50:
                  case 51:
                  case 52:
                  case 53:
                  case 54:
                  case 55:
                  case 56:
                  case 57:
                  case 58:
                  case 75:
                  case 76:
                  case 79:
                  case 82:
                  case 85:
                  case 86:
                  case 89:
                  case 90:
                  case 94:
                  case 95:
                  default:
                     break;
                  case 6:
                     if ((-268435457L & l) != 0L) {
                        this.jjCheckNAddStates(361, 363);
                     }
                     break;
                  case 7:
                     if (this.curChar == 92) {
                        this.jjAddStates(371, 372);
                     }
                     break;
                  case 8:
                     if (this.curChar == 120) {
                        this.jjstateSet[this.jjnewStateCnt++] = 9;
                     }
                     break;
                  case 9:
                     if ((541165879422L & l) != 0L) {
                        this.jjCheckNAddStates(361, 363);
                     }
                     break;
                  case 11:
                     if ((582179063439818752L & l) != 0L) {
                        this.jjCheckNAddStates(361, 363);
                     }
                     break;
                  case 13:
                     if ((-268435457L & l) != 0L) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 14:
                     if (this.curChar == 92) {
                        this.jjAddStates(373, 374);
                     }
                     break;
                  case 15:
                     if (this.curChar == 120) {
                        this.jjstateSet[this.jjnewStateCnt++] = 16;
                     }
                     break;
                  case 16:
                     if ((541165879422L & l) != 0L) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 18:
                     if ((582179063439818752L & l) != 0L) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 19:
                     if (this.curChar == 114) {
                        this.jjAddStates(369, 370);
                     }
                     break;
                  case 21:
                     this.jjAddStates(375, 376);
                     break;
                  case 24:
                     this.jjAddStates(377, 378);
                     break;
                  case 30:
                  case 31:
                     if (this.curChar == 124 && kind > 128) {
                        kind = 128;
                     }
                     break;
                  case 32:
                     if (this.curChar == 124) {
                        this.jjstateSet[this.jjnewStateCnt++] = 31;
                     }
                     break;
                  case 33:
                     if ((576460745995190271L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  case 34:
                     if ((576460745995190271L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  case 35:
                     if (this.curChar == 92) {
                        this.jjCheckNAdd(36);
                     }
                     break;
                  case 37:
                     if (this.curChar == 92) {
                        this.jjCheckNAdd(36);
                     }
                     break;
                  case 38:
                     if (this.curChar == 123 && kind > 143) {
                        kind = 143;
                     }
                     break;
                  case 42:
                     if (this.curChar == 91) {
                        this.jjstateSet[this.jjnewStateCnt++] = 41;
                     }
                     break;
                  case 46:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 45;
                     }
                     break;
                  case 47:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 46;
                     }
                     break;
                  case 59:
                     if (this.curChar == 108) {
                        this.jjCheckNAddTwoStates(60, 62);
                     }
                     break;
                  case 60:
                     if (this.curChar == 116 && kind > 115) {
                        kind = 115;
                     }
                     break;
                  case 61:
                     if (this.curChar == 101 && kind > 116) {
                        kind = 116;
                     }
                     break;
                  case 62:
                  case 65:
                     if (this.curChar == 116) {
                        this.jjCheckNAdd(61);
                     }
                     break;
                  case 63:
                     if (this.curChar == 92) {
                        this.jjAddStates(392, 396);
                     }
                     break;
                  case 64:
                     if (this.curChar == 108) {
                        this.jjCheckNAdd(60);
                     }
                     break;
                  case 66:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 65;
                     }
                     break;
                  case 67:
                     if (this.curChar == 116 && kind > 117) {
                        kind = 117;
                     }
                     break;
                  case 68:
                     if (this.curChar == 103) {
                        this.jjCheckNAdd(67);
                     }
                     break;
                  case 69:
                     if (this.curChar == 101 && kind > 118) {
                        kind = 118;
                     }
                     break;
                  case 70:
                  case 100:
                     if (this.curChar == 116) {
                        this.jjCheckNAdd(69);
                     }
                     break;
                  case 71:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 70;
                     }
                     break;
                  case 72:
                     if (this.curChar == 100 && kind > 127) {
                        kind = 127;
                     }
                     break;
                  case 73:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 72;
                     }
                     break;
                  case 74:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 73;
                     }
                     break;
                  case 77:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 76;
                     }
                     break;
                  case 78:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 77;
                     }
                     break;
                  case 80:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 79;
                     }
                     break;
                  case 81:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 80;
                     }
                     break;
                  case 83:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 82;
                     }
                     break;
                  case 84:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 83;
                     }
                     break;
                  case 87:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 86;
                     }
                     break;
                  case 88:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 87;
                     }
                     break;
                  case 91:
                     if (this.curChar == 112) {
                        this.jjstateSet[this.jjnewStateCnt++] = 90;
                     }
                     break;
                  case 92:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 91;
                     }
                     break;
                  case 93:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 92;
                     }
                     break;
                  case 96:
                     if (this.curChar == 112) {
                        this.jjstateSet[this.jjnewStateCnt++] = 95;
                     }
                     break;
                  case 97:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 96;
                     }
                     break;
                  case 98:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 97;
                     }
                     break;
                  case 99:
                     if (this.curChar == 103) {
                        this.jjCheckNAddTwoStates(67, 100);
                     }
                     break;
                  case 101:
                     if ((576460745995190271L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     } else if (this.curChar == 92) {
                        this.jjCheckNAdd(36);
                     }
               }
            } while(i != startsAt);
         } else {
            int hiByte = this.curChar >> 8;
            int i1 = hiByte >> 6;
            long l1 = 1L << (hiByte & 63);
            int i2 = (this.curChar & 255) >> 6;
            long l2 = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 1:
                     if (jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  case 6:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        this.jjAddStates(361, 363);
                     }
                     break;
                  case 13:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        this.jjAddStates(358, 360);
                     }
                     break;
                  case 21:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        this.jjAddStates(375, 376);
                     }
                     break;
                  case 24:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        this.jjAddStates(377, 378);
                     }
                     break;
                  case 34:
                  case 101:
                     if (jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  default:
                     if (i1 != 0 && l1 != 0L && i2 != 0 && l2 == 0L) {
                     }
               }
            } while(i != startsAt);
         }

         if (kind != Integer.MAX_VALUE) {
            this.jjmatchedKind = kind;
            this.jjmatchedPos = curPos;
            kind = Integer.MAX_VALUE;
         }

         ++curPos;
         if ((i = this.jjnewStateCnt) == (startsAt = 101 - (this.jjnewStateCnt = startsAt))) {
            return curPos;
         }

         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var13) {
            return curPos;
         }
      }
   }

   private final int jjStopStringLiteralDfa_5(int pos, long active0, long active1) {
      switch (pos) {
         default:
            return -1;
      }
   }

   private final int jjStartNfa_5(int pos, long active0, long active1) {
      return this.jjMoveNfa_5(this.jjStopStringLiteralDfa_5(pos, active0, active1), pos + 1);
   }

   private int jjMoveStringLiteralDfa0_5() {
      switch (this.curChar) {
         case 45:
            return this.jjStartNfaWithStates_5(0, 90, 3);
         default:
            return this.jjMoveNfa_5(1, 0);
      }
   }

   private int jjStartNfaWithStates_5(int pos, int kind, int state) {
      this.jjmatchedKind = kind;
      this.jjmatchedPos = pos;

      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var5) {
         return pos + 1;
      }

      return this.jjMoveNfa_5(state, pos + 1);
   }

   private int jjMoveNfa_5(int startState, int curPos) {
      int startsAt = 0;
      this.jjnewStateCnt = 6;
      int i = 1;
      this.jjstateSet[0] = startState;
      int kind = Integer.MAX_VALUE;

      while(true) {
         if (++this.jjround == Integer.MAX_VALUE) {
            this.ReInitRounds();
         }

         long l;
         if (this.curChar < 64) {
            l = 1L << this.curChar;

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 0:
                     if ((-4611721202799476737L & l) != 0L) {
                        kind = 87;
                        this.jjCheckNAdd(0);
                     }
                     break;
                  case 1:
                     if ((-4611721202799476737L & l) != 0L) {
                        if (kind > 87) {
                           kind = 87;
                        }

                        this.jjCheckNAdd(0);
                     } else if (this.curChar == 45) {
                        this.jjAddStates(397, 398);
                     }
                     break;
                  case 2:
                     if (this.curChar == 62) {
                        kind = 91;
                     }
                     break;
                  case 3:
                     if (this.curChar == 45) {
                        this.jjstateSet[this.jjnewStateCnt++] = 4;
                     }

                     if (this.curChar == 45) {
                        this.jjstateSet[this.jjnewStateCnt++] = 2;
                     }
                  case 4:
                  default:
                     break;
                  case 5:
                     if (this.curChar == 45) {
                        this.jjstateSet[this.jjnewStateCnt++] = 4;
                     }
               }
            } while(i != startsAt);
         } else if (this.curChar < 128) {
            l = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 0:
                  case 1:
                     if ((-536870913L & l) != 0L) {
                        kind = 87;
                        this.jjCheckNAdd(0);
                     }
                  case 2:
                  case 3:
                  default:
                     break;
                  case 4:
                     if (this.curChar == 93) {
                        kind = 91;
                     }
               }
            } while(i != startsAt);
         } else {
            int hiByte = this.curChar >> 8;
            int i1 = hiByte >> 6;
            long l1 = 1L << (hiByte & 63);
            int i2 = (this.curChar & 255) >> 6;
            long l2 = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 0:
                  case 1:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        if (kind > 87) {
                           kind = 87;
                        }

                        this.jjCheckNAdd(0);
                     }
                     break;
                  default:
                     if (i1 != 0 && l1 != 0L && i2 != 0 && l2 == 0L) {
                     }
               }
            } while(i != startsAt);
         }

         if (kind != Integer.MAX_VALUE) {
            this.jjmatchedKind = kind;
            this.jjmatchedPos = curPos;
            kind = Integer.MAX_VALUE;
         }

         ++curPos;
         if ((i = this.jjnewStateCnt) == (startsAt = 6 - (this.jjnewStateCnt = startsAt))) {
            return curPos;
         }

         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var13) {
            return curPos;
         }
      }
   }

   private final int jjStopStringLiteralDfa_1(int pos, long active0, long active1) {
      switch (pos) {
         case 0:
            if ((active1 & 1835008L) != 0L) {
               this.jjmatchedKind = 81;
               return -1;
            }

            return -1;
         default:
            return -1;
      }
   }

   private final int jjStartNfa_1(int pos, long active0, long active1) {
      return this.jjMoveNfa_1(this.jjStopStringLiteralDfa_1(pos, active0, active1), pos + 1);
   }

   private int jjMoveStringLiteralDfa0_1() {
      switch (this.curChar) {
         case 35:
            return this.jjMoveStringLiteralDfa1_1(524288L);
         case 36:
            return this.jjMoveStringLiteralDfa1_1(262144L);
         case 91:
            return this.jjMoveStringLiteralDfa1_1(1048576L);
         default:
            return this.jjMoveNfa_1(2, 0);
      }
   }

   private int jjMoveStringLiteralDfa1_1(long active1) {
      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var4) {
         this.jjStopStringLiteralDfa_1(0, 0L, active1);
         return 1;
      }

      switch (this.curChar) {
         case 61:
            if ((active1 & 1048576L) != 0L) {
               return this.jjStopAtPos(1, 84);
            }
            break;
         case 123:
            if ((active1 & 262144L) != 0L) {
               return this.jjStopAtPos(1, 82);
            }

            if ((active1 & 524288L) != 0L) {
               return this.jjStopAtPos(1, 83);
            }
      }

      return this.jjStartNfa_1(0, 0L, active1);
   }

   private int jjMoveNfa_1(int startState, int curPos) {
      int startsAt = 0;
      this.jjnewStateCnt = 3;
      int i = 1;
      this.jjstateSet[0] = startState;
      int kind = Integer.MAX_VALUE;

      while(true) {
         if (++this.jjround == Integer.MAX_VALUE) {
            this.ReInitRounds();
         }

         long l;
         if (this.curChar < 64) {
            l = 1L << this.curChar;

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 0:
                     if ((4294977024L & l) != 0L) {
                        kind = 79;
                        this.jjCheckNAdd(0);
                     }
                     break;
                  case 1:
                     if ((-1152921611981039105L & l) != 0L) {
                        kind = 80;
                        this.jjCheckNAdd(1);
                     }
                     break;
                  case 2:
                     if ((-1152921611981039105L & l) != 0L) {
                        if (kind > 80) {
                           kind = 80;
                        }

                        this.jjCheckNAdd(1);
                     } else if ((4294977024L & l) != 0L) {
                        if (kind > 79) {
                           kind = 79;
                        }

                        this.jjCheckNAdd(0);
                     } else if ((1152921607686062080L & l) != 0L && kind > 81) {
                        kind = 81;
                     }
               }
            } while(i != startsAt);
         } else if (this.curChar < 128) {
            l = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 1:
                     if ((-576460752437641217L & l) != 0L) {
                        kind = 80;
                        this.jjCheckNAdd(1);
                     }
                     break;
                  case 2:
                     if ((-576460752437641217L & l) != 0L) {
                        if (kind > 80) {
                           kind = 80;
                        }

                        this.jjCheckNAdd(1);
                     } else if ((576460752437641216L & l) != 0L && kind > 81) {
                        kind = 81;
                     }
               }
            } while(i != startsAt);
         } else {
            int hiByte = this.curChar >> 8;
            int i1 = hiByte >> 6;
            long l1 = 1L << (hiByte & 63);
            int i2 = (this.curChar & 255) >> 6;
            long l2 = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 1:
                  case 2:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        if (kind > 80) {
                           kind = 80;
                        }

                        this.jjCheckNAdd(1);
                     }
                     break;
                  default:
                     if (i1 != 0 && l1 != 0L && i2 != 0 && l2 == 0L) {
                     }
               }
            } while(i != startsAt);
         }

         if (kind != Integer.MAX_VALUE) {
            this.jjmatchedKind = kind;
            this.jjmatchedPos = curPos;
            kind = Integer.MAX_VALUE;
         }

         ++curPos;
         if ((i = this.jjnewStateCnt) == (startsAt = 3 - (this.jjnewStateCnt = startsAt))) {
            return curPos;
         }

         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var13) {
            return curPos;
         }
      }
   }

   private final int jjStopStringLiteralDfa_6(int pos, long active0, long active1, long active2) {
      switch (pos) {
         case 0:
            if ((active2 & 32L) != 0L) {
               return 36;
            } else if ((active1 & 2305983746702049280L) != 0L) {
               return 40;
            } else if ((active1 & 145276272354787328L) != 0L) {
               return 43;
            } else if ((active1 & 1152921882563969024L) != 0L) {
               return 50;
            } else {
               if ((active1 & 6442450944L) == 0L && (active2 & 14336L) == 0L) {
                  return -1;
               }

               this.jjmatchedKind = 142;
               return 100;
            }
         case 1:
            if ((active2 & 6144L) != 0L) {
               return 100;
            } else if ((active1 & 1152921848204230656L) != 0L) {
               return 49;
            } else {
               if ((active1 & 6442450944L) == 0L && (active2 & 8192L) == 0L) {
                  return -1;
               }

               if (this.jjmatchedPos != 1) {
                  this.jjmatchedKind = 142;
                  this.jjmatchedPos = 1;
               }

               return 100;
            }
         case 2:
            if ((active1 & 6442450944L) == 0L && (active2 & 8192L) == 0L) {
               return -1;
            }

            this.jjmatchedKind = 142;
            this.jjmatchedPos = 2;
            return 100;
         case 3:
            if ((active1 & 4294967296L) != 0L) {
               return 100;
            } else {
               if ((active1 & 2147483648L) == 0L && (active2 & 8192L) == 0L) {
                  return -1;
               }

               this.jjmatchedKind = 142;
               this.jjmatchedPos = 3;
               return 100;
            }
         default:
            return -1;
      }
   }

   private final int jjStartNfa_6(int pos, long active0, long active1, long active2) {
      return this.jjMoveNfa_6(this.jjStopStringLiteralDfa_6(pos, active0, active1, active2), pos + 1);
   }

   private int jjMoveStringLiteralDfa0_6() {
      switch (this.curChar) {
         case 33:
            this.jjmatchedKind = 129;
            return this.jjMoveStringLiteralDfa1_6(8796093022208L, 0L);
         case 34:
         case 35:
         case 36:
         case 38:
         case 39:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
         case 60:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 92:
         case 94:
         case 95:
         case 96:
         case 98:
         case 99:
         case 100:
         case 101:
         case 103:
         case 104:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 115:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 124:
         default:
            return this.jjMoveNfa_6(0, 0);
         case 37:
            this.jjmatchedKind = 126;
            return this.jjMoveStringLiteralDfa1_6(281474976710656L, 0L);
         case 40:
            return this.jjStopAtPos(0, 135);
         case 41:
            return this.jjStopAtPos(0, 136);
         case 42:
            this.jjmatchedKind = 122;
            return this.jjMoveStringLiteralDfa1_6(576531121047601152L, 0L);
         case 43:
            this.jjmatchedKind = 120;
            return this.jjMoveStringLiteralDfa1_6(580542139465728L, 0L);
         case 44:
            return this.jjStopAtPos(0, 130);
         case 45:
            this.jjmatchedKind = 121;
            return this.jjMoveStringLiteralDfa1_6(1161084278931456L, 0L);
         case 46:
            this.jjmatchedKind = 99;
            return this.jjMoveStringLiteralDfa1_6(1152921848204230656L, 0L);
         case 47:
            this.jjmatchedKind = 125;
            return this.jjMoveStringLiteralDfa1_6(140737488355328L, 0L);
         case 58:
            return this.jjStopAtPos(0, 132);
         case 59:
            return this.jjStopAtPos(0, 131);
         case 61:
            this.jjmatchedKind = 105;
            return this.jjMoveStringLiteralDfa1_6(4398046511104L, 0L);
         case 62:
            return this.jjStopAtPos(0, 148);
         case 63:
            this.jjmatchedKind = 103;
            return this.jjMoveStringLiteralDfa1_6(1099511627776L, 0L);
         case 91:
            return this.jjStartNfaWithStates_6(0, 133, 36);
         case 93:
            return this.jjStopAtPos(0, 134);
         case 97:
            return this.jjMoveStringLiteralDfa1_6(0L, 4096L);
         case 102:
            return this.jjMoveStringLiteralDfa1_6(2147483648L, 0L);
         case 105:
            return this.jjMoveStringLiteralDfa1_6(0L, 2048L);
         case 116:
            return this.jjMoveStringLiteralDfa1_6(4294967296L, 0L);
         case 117:
            return this.jjMoveStringLiteralDfa1_6(0L, 8192L);
         case 123:
            return this.jjStopAtPos(0, 137);
         case 125:
            return this.jjStopAtPos(0, 138);
      }
   }

   private int jjMoveStringLiteralDfa1_6(long active1, long active2) {
      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var6) {
         this.jjStopStringLiteralDfa_6(0, 0L, active1, active2);
         return 1;
      }

      switch (this.curChar) {
         case 42:
            if ((active1 & 576460752303423488L) != 0L) {
               return this.jjStopAtPos(1, 123);
            }
            break;
         case 43:
            if ((active1 & 562949953421312L) != 0L) {
               return this.jjStopAtPos(1, 113);
            }
            break;
         case 45:
            if ((active1 & 1125899906842624L) != 0L) {
               return this.jjStopAtPos(1, 114);
            }
            break;
         case 46:
            if ((active1 & 68719476736L) != 0L) {
               this.jjmatchedKind = 100;
               this.jjmatchedPos = 1;
            }

            return this.jjMoveStringLiteralDfa2_6(active1, 1152921779484753920L, active2, 0L);
         case 61:
            if ((active1 & 4398046511104L) != 0L) {
               return this.jjStopAtPos(1, 106);
            }

            if ((active1 & 8796093022208L) != 0L) {
               return this.jjStopAtPos(1, 107);
            }

            if ((active1 & 17592186044416L) != 0L) {
               return this.jjStopAtPos(1, 108);
            }

            if ((active1 & 35184372088832L) != 0L) {
               return this.jjStopAtPos(1, 109);
            }

            if ((active1 & 70368744177664L) != 0L) {
               return this.jjStopAtPos(1, 110);
            }

            if ((active1 & 140737488355328L) != 0L) {
               return this.jjStopAtPos(1, 111);
            }

            if ((active1 & 281474976710656L) != 0L) {
               return this.jjStopAtPos(1, 112);
            }
            break;
         case 63:
            if ((active1 & 1099511627776L) != 0L) {
               return this.jjStopAtPos(1, 104);
            }
            break;
         case 97:
            return this.jjMoveStringLiteralDfa2_6(active1, 2147483648L, active2, 0L);
         case 110:
            if ((active2 & 2048L) != 0L) {
               return this.jjStartNfaWithStates_6(1, 139, 100);
            }
            break;
         case 114:
            return this.jjMoveStringLiteralDfa2_6(active1, 4294967296L, active2, 0L);
         case 115:
            if ((active2 & 4096L) != 0L) {
               return this.jjStartNfaWithStates_6(1, 140, 100);
            }

            return this.jjMoveStringLiteralDfa2_6(active1, 0L, active2, 8192L);
      }

      return this.jjStartNfa_6(0, 0L, active1, active2);
   }

   private int jjMoveStringLiteralDfa2_6(long old1, long active1, long old2, long active2) {
      if (((active1 &= old1) | (active2 &= old2)) == 0L) {
         return this.jjStartNfa_6(0, 0L, old1, old2);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var10) {
            this.jjStopStringLiteralDfa_6(1, 0L, active1, active2);
            return 2;
         }

         switch (this.curChar) {
            case 42:
               if ((active1 & 274877906944L) != 0L) {
                  return this.jjStopAtPos(2, 102);
               }
               break;
            case 46:
               if ((active1 & 1152921504606846976L) != 0L) {
                  return this.jjStopAtPos(2, 124);
               }
               break;
            case 105:
               return this.jjMoveStringLiteralDfa3_6(active1, 0L, active2, 8192L);
            case 108:
               return this.jjMoveStringLiteralDfa3_6(active1, 2147483648L, active2, 0L);
            case 117:
               return this.jjMoveStringLiteralDfa3_6(active1, 4294967296L, active2, 0L);
         }

         return this.jjStartNfa_6(1, 0L, active1, active2);
      }
   }

   private int jjMoveStringLiteralDfa3_6(long old1, long active1, long old2, long active2) {
      if (((active1 &= old1) | (active2 &= old2)) == 0L) {
         return this.jjStartNfa_6(1, 0L, old1, old2);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var10) {
            this.jjStopStringLiteralDfa_6(2, 0L, active1, active2);
            return 3;
         }

         switch (this.curChar) {
            case 101:
               if ((active1 & 4294967296L) != 0L) {
                  return this.jjStartNfaWithStates_6(3, 96, 100);
               }
            default:
               return this.jjStartNfa_6(2, 0L, active1, active2);
            case 110:
               return this.jjMoveStringLiteralDfa4_6(active1, 0L, active2, 8192L);
            case 115:
               return this.jjMoveStringLiteralDfa4_6(active1, 2147483648L, active2, 0L);
         }
      }
   }

   private int jjMoveStringLiteralDfa4_6(long old1, long active1, long old2, long active2) {
      if (((active1 &= old1) | (active2 &= old2)) == 0L) {
         return this.jjStartNfa_6(2, 0L, old1, old2);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var10) {
            this.jjStopStringLiteralDfa_6(3, 0L, active1, active2);
            return 4;
         }

         switch (this.curChar) {
            case 101:
               if ((active1 & 2147483648L) != 0L) {
                  return this.jjStartNfaWithStates_6(4, 95, 100);
               }
               break;
            case 103:
               if ((active2 & 8192L) != 0L) {
                  return this.jjStartNfaWithStates_6(4, 141, 100);
               }
         }

         return this.jjStartNfa_6(3, 0L, active1, active2);
      }
   }

   private int jjStartNfaWithStates_6(int pos, int kind, int state) {
      this.jjmatchedKind = kind;
      this.jjmatchedPos = pos;

      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var5) {
         return pos + 1;
      }

      return this.jjMoveNfa_6(state, pos + 1);
   }

   private int jjMoveNfa_6(int startState, int curPos) {
      int startsAt = 0;
      this.jjnewStateCnt = 100;
      int i = 1;
      this.jjstateSet[0] = startState;
      int kind = Integer.MAX_VALUE;

      while(true) {
         if (++this.jjround == Integer.MAX_VALUE) {
            this.ReInitRounds();
         }

         long l;
         if (this.curChar < 64) {
            l = 1L << this.curChar;

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 0:
                     if ((287948901175001088L & l) != 0L) {
                        if (kind > 97) {
                           kind = 97;
                        }

                        this.jjCheckNAddStates(399, 401);
                     } else if ((4294977024L & l) != 0L) {
                        if (kind > 152) {
                           kind = 152;
                        }

                        this.jjCheckNAdd(38);
                     } else if (this.curChar == 38) {
                        this.jjAddStates(402, 407);
                     } else if (this.curChar == 46) {
                        this.jjAddStates(408, 409);
                     } else if (this.curChar == 45) {
                        this.jjAddStates(410, 411);
                     } else if (this.curChar == 47) {
                        this.jjAddStates(412, 413);
                     } else if (this.curChar == 35) {
                        this.jjCheckNAdd(33);
                     } else if (this.curChar == 36) {
                        this.jjCheckNAdd(33);
                     } else if (this.curChar == 60) {
                        this.jjCheckNAdd(22);
                     } else if (this.curChar == 39) {
                        this.jjCheckNAddStates(414, 416);
                     } else if (this.curChar == 34) {
                        this.jjCheckNAddStates(417, 419);
                     }

                     if (this.curChar == 36) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(29, 30);
                     } else if (this.curChar == 38) {
                        if (kind > 127) {
                           kind = 127;
                        }
                     } else if (this.curChar == 60 && kind > 115) {
                        kind = 115;
                     }
                     break;
                  case 1:
                     if ((-17179869185L & l) != 0L) {
                        this.jjCheckNAddStates(417, 419);
                     }
                  case 2:
                  case 3:
                  case 9:
                  case 10:
                  case 14:
                  case 25:
                  case 26:
                  case 27:
                  case 30:
                  case 32:
                  case 33:
                  case 37:
                  case 41:
                  case 45:
                  case 46:
                  case 58:
                  case 59:
                  case 60:
                  case 61:
                  case 62:
                  case 63:
                  case 64:
                  case 65:
                  case 66:
                  case 67:
                  case 68:
                  case 69:
                  case 70:
                  case 71:
                  case 72:
                  case 73:
                  case 76:
                  case 77:
                  case 79:
                  case 80:
                  case 82:
                  case 83:
                  case 86:
                  case 87:
                  case 90:
                  case 91:
                  case 92:
                  case 95:
                  case 96:
                  case 97:
                  case 98:
                  case 99:
                  default:
                     break;
                  case 4:
                     if ((287948901175001088L & l) != 0L) {
                        this.jjCheckNAddStates(417, 419);
                     }
                     break;
                  case 5:
                     if (this.curChar == 34 && kind > 93) {
                        kind = 93;
                     }
                     break;
                  case 6:
                     if ((2305843576149377024L & l) != 0L) {
                        this.jjCheckNAddStates(417, 419);
                     }
                     break;
                  case 7:
                     if (this.curChar == 39) {
                        this.jjCheckNAddStates(414, 416);
                     }
                     break;
                  case 8:
                     if ((-549755813889L & l) != 0L) {
                        this.jjCheckNAddStates(414, 416);
                     }
                     break;
                  case 11:
                     if ((287948901175001088L & l) != 0L) {
                        this.jjCheckNAddStates(414, 416);
                     }
                     break;
                  case 12:
                     if (this.curChar == 39 && kind > 93) {
                        kind = 93;
                     }
                     break;
                  case 13:
                     if ((2305843576149377024L & l) != 0L) {
                        this.jjCheckNAddStates(414, 416);
                     }
                     break;
                  case 15:
                     if (this.curChar == 34) {
                        this.jjCheckNAddTwoStates(16, 17);
                     }
                     break;
                  case 16:
                     if ((-17179869185L & l) != 0L) {
                        this.jjCheckNAddTwoStates(16, 17);
                     }
                     break;
                  case 17:
                     if (this.curChar == 34 && kind > 94) {
                        kind = 94;
                     }
                     break;
                  case 18:
                     if (this.curChar == 39) {
                        this.jjCheckNAddTwoStates(19, 20);
                     }
                     break;
                  case 19:
                     if ((-549755813889L & l) != 0L) {
                        this.jjCheckNAddTwoStates(19, 20);
                     }
                     break;
                  case 20:
                     if (this.curChar == 39 && kind > 94) {
                        kind = 94;
                     }
                     break;
                  case 21:
                     if (this.curChar == 60 && kind > 115) {
                        kind = 115;
                     }
                     break;
                  case 22:
                     if (this.curChar == 61 && kind > 116) {
                        kind = 116;
                     }
                     break;
                  case 23:
                     if (this.curChar == 60) {
                        this.jjCheckNAdd(22);
                     }
                     break;
                  case 24:
                  case 88:
                     if (this.curChar == 38 && kind > 127) {
                        kind = 127;
                     }
                     break;
                  case 28:
                     if (this.curChar == 36) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(29, 30);
                     }
                     break;
                  case 29:
                  case 100:
                     if ((287948969894477824L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(29, 30);
                     }
                     break;
                  case 31:
                     if ((288335963627716608L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(29, 30);
                     }
                     break;
                  case 34:
                     if (this.curChar == 36) {
                        this.jjCheckNAdd(33);
                     }
                     break;
                  case 35:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(33);
                     }
                     break;
                  case 36:
                     if (this.curChar == 61 && kind > 143) {
                        kind = 143;
                     }
                     break;
                  case 38:
                     if ((4294977024L & l) != 0L) {
                        if (kind > 152) {
                           kind = 152;
                        }

                        this.jjCheckNAdd(38);
                     }
                     break;
                  case 39:
                     if (this.curChar == 47) {
                        this.jjAddStates(412, 413);
                     }
                     break;
                  case 40:
                     if (this.curChar == 62 && kind > 149) {
                        kind = 149;
                     }
                     break;
                  case 42:
                     if (this.curChar == 45) {
                        this.jjAddStates(410, 411);
                     }
                     break;
                  case 43:
                     if (this.curChar == 38) {
                        this.jjstateSet[this.jjnewStateCnt++] = 46;
                     } else if (this.curChar == 62 && kind > 119) {
                        kind = 119;
                     }
                     break;
                  case 44:
                     if (this.curChar == 59 && kind > 119) {
                        kind = 119;
                     }
                     break;
                  case 47:
                     if (this.curChar == 38) {
                        this.jjstateSet[this.jjnewStateCnt++] = 46;
                     }
                     break;
                  case 48:
                     if (this.curChar == 46) {
                        this.jjAddStates(408, 409);
                     }
                     break;
                  case 49:
                     if (this.curChar == 33) {
                        if (kind > 101) {
                           kind = 101;
                        }
                     } else if (this.curChar == 60 && kind > 101) {
                        kind = 101;
                     }
                     break;
                  case 50:
                     if (this.curChar == 46) {
                        this.jjstateSet[this.jjnewStateCnt++] = 51;
                     }

                     if (this.curChar == 46) {
                        this.jjstateSet[this.jjnewStateCnt++] = 49;
                     }
                     break;
                  case 51:
                     if (this.curChar == 33 && kind > 101) {
                        kind = 101;
                     }
                     break;
                  case 52:
                     if (this.curChar == 46) {
                        this.jjstateSet[this.jjnewStateCnt++] = 51;
                     }
                     break;
                  case 53:
                     if ((287948901175001088L & l) != 0L) {
                        if (kind > 97) {
                           kind = 97;
                        }

                        this.jjCheckNAddStates(399, 401);
                     }
                     break;
                  case 54:
                     if ((287948901175001088L & l) != 0L) {
                        if (kind > 97) {
                           kind = 97;
                        }

                        this.jjCheckNAdd(54);
                     }
                     break;
                  case 55:
                     if ((287948901175001088L & l) != 0L) {
                        this.jjCheckNAddTwoStates(55, 56);
                     }
                     break;
                  case 56:
                     if (this.curChar == 46) {
                        this.jjCheckNAdd(57);
                     }
                     break;
                  case 57:
                     if ((287948901175001088L & l) != 0L) {
                        if (kind > 98) {
                           kind = 98;
                        }

                        this.jjCheckNAdd(57);
                     }
                     break;
                  case 74:
                     if (this.curChar == 38) {
                        this.jjAddStates(402, 407);
                     }
                     break;
                  case 75:
                     if (this.curChar == 59 && kind > 115) {
                        kind = 115;
                     }
                     break;
                  case 78:
                     if (this.curChar == 59) {
                        this.jjCheckNAdd(22);
                     }
                     break;
                  case 81:
                     if (this.curChar == 59 && kind > 117) {
                        kind = 117;
                     }
                     break;
                  case 84:
                     if (this.curChar == 61 && kind > 118) {
                        kind = 118;
                     }
                     break;
                  case 85:
                     if (this.curChar == 59) {
                        this.jjstateSet[this.jjnewStateCnt++] = 84;
                     }
                     break;
                  case 89:
                     if (this.curChar == 59 && kind > 127) {
                        kind = 127;
                     }
                     break;
                  case 93:
                     if (this.curChar == 38) {
                        this.jjstateSet[this.jjnewStateCnt++] = 92;
                     }
                     break;
                  case 94:
                     if (this.curChar == 59) {
                        this.jjstateSet[this.jjnewStateCnt++] = 93;
                     }
               }
            } while(i != startsAt);
         } else if (this.curChar < 128) {
            l = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 0:
                     if ((576460745995190271L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(29, 30);
                     } else if (this.curChar == 92) {
                        this.jjAddStates(420, 424);
                     } else if (this.curChar == 91) {
                        this.jjstateSet[this.jjnewStateCnt++] = 36;
                     } else if (this.curChar == 124) {
                        this.jjstateSet[this.jjnewStateCnt++] = 26;
                     }

                     if (this.curChar == 103) {
                        this.jjCheckNAddTwoStates(66, 99);
                     } else if (this.curChar == 108) {
                        this.jjCheckNAddTwoStates(59, 61);
                     } else if (this.curChar == 92) {
                        this.jjCheckNAdd(31);
                     } else if (this.curChar == 124) {
                        if (kind > 128) {
                           kind = 128;
                        }
                     } else if (this.curChar == 114) {
                        this.jjAddStates(373, 374);
                     }
                     break;
                  case 1:
                     if ((-268435457L & l) != 0L) {
                        this.jjCheckNAddStates(417, 419);
                     }
                     break;
                  case 2:
                     if (this.curChar == 92) {
                        this.jjAddStates(425, 426);
                     }
                     break;
                  case 3:
                     if (this.curChar == 120) {
                        this.jjstateSet[this.jjnewStateCnt++] = 4;
                     }
                     break;
                  case 4:
                     if ((541165879422L & l) != 0L) {
                        this.jjCheckNAddStates(417, 419);
                     }
                  case 5:
                  case 7:
                  case 12:
                  case 15:
                  case 17:
                  case 18:
                  case 20:
                  case 21:
                  case 22:
                  case 23:
                  case 24:
                  case 31:
                  case 34:
                  case 35:
                  case 36:
                  case 38:
                  case 39:
                  case 41:
                  case 42:
                  case 43:
                  case 44:
                  case 47:
                  case 48:
                  case 49:
                  case 50:
                  case 51:
                  case 52:
                  case 53:
                  case 54:
                  case 55:
                  case 56:
                  case 57:
                  case 74:
                  case 75:
                  case 78:
                  case 81:
                  case 84:
                  case 85:
                  case 88:
                  case 89:
                  case 93:
                  case 94:
                  default:
                     break;
                  case 6:
                     if ((582179063439818752L & l) != 0L) {
                        this.jjCheckNAddStates(417, 419);
                     }
                     break;
                  case 8:
                     if ((-268435457L & l) != 0L) {
                        this.jjCheckNAddStates(414, 416);
                     }
                     break;
                  case 9:
                     if (this.curChar == 92) {
                        this.jjAddStates(427, 428);
                     }
                     break;
                  case 10:
                     if (this.curChar == 120) {
                        this.jjstateSet[this.jjnewStateCnt++] = 11;
                     }
                     break;
                  case 11:
                     if ((541165879422L & l) != 0L) {
                        this.jjCheckNAddStates(414, 416);
                     }
                     break;
                  case 13:
                     if ((582179063439818752L & l) != 0L) {
                        this.jjCheckNAddStates(414, 416);
                     }
                     break;
                  case 14:
                     if (this.curChar == 114) {
                        this.jjAddStates(373, 374);
                     }
                     break;
                  case 16:
                     this.jjAddStates(429, 430);
                     break;
                  case 19:
                     this.jjAddStates(431, 432);
                     break;
                  case 25:
                  case 26:
                     if (this.curChar == 124 && kind > 128) {
                        kind = 128;
                     }
                     break;
                  case 27:
                     if (this.curChar == 124) {
                        this.jjstateSet[this.jjnewStateCnt++] = 26;
                     }
                     break;
                  case 28:
                     if ((576460745995190271L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(29, 30);
                     }
                     break;
                  case 29:
                     if ((576460745995190271L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(29, 30);
                     }
                     break;
                  case 30:
                     if (this.curChar == 92) {
                        this.jjCheckNAdd(31);
                     }
                     break;
                  case 32:
                     if (this.curChar == 92) {
                        this.jjCheckNAdd(31);
                     }
                     break;
                  case 33:
                     if (this.curChar == 123 && kind > 143) {
                        kind = 143;
                     }
                     break;
                  case 37:
                     if (this.curChar == 91) {
                        this.jjstateSet[this.jjnewStateCnt++] = 36;
                     }
                     break;
                  case 40:
                     if (this.curChar == 93 && kind > 149) {
                        kind = 149;
                     }
                     break;
                  case 45:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 44;
                     }
                     break;
                  case 46:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 45;
                     }
                     break;
                  case 58:
                     if (this.curChar == 108) {
                        this.jjCheckNAddTwoStates(59, 61);
                     }
                     break;
                  case 59:
                     if (this.curChar == 116 && kind > 115) {
                        kind = 115;
                     }
                     break;
                  case 60:
                     if (this.curChar == 101 && kind > 116) {
                        kind = 116;
                     }
                     break;
                  case 61:
                  case 64:
                     if (this.curChar == 116) {
                        this.jjCheckNAdd(60);
                     }
                     break;
                  case 62:
                     if (this.curChar == 92) {
                        this.jjAddStates(420, 424);
                     }
                     break;
                  case 63:
                     if (this.curChar == 108) {
                        this.jjCheckNAdd(59);
                     }
                     break;
                  case 65:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 64;
                     }
                     break;
                  case 66:
                     if (this.curChar == 116 && kind > 117) {
                        kind = 117;
                     }
                     break;
                  case 67:
                     if (this.curChar == 103) {
                        this.jjCheckNAdd(66);
                     }
                     break;
                  case 68:
                     if (this.curChar == 101 && kind > 118) {
                        kind = 118;
                     }
                     break;
                  case 69:
                  case 99:
                     if (this.curChar == 116) {
                        this.jjCheckNAdd(68);
                     }
                     break;
                  case 70:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 69;
                     }
                     break;
                  case 71:
                     if (this.curChar == 100 && kind > 127) {
                        kind = 127;
                     }
                     break;
                  case 72:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 71;
                     }
                     break;
                  case 73:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 72;
                     }
                     break;
                  case 76:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 75;
                     }
                     break;
                  case 77:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 76;
                     }
                     break;
                  case 79:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 78;
                     }
                     break;
                  case 80:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 79;
                     }
                     break;
                  case 82:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 81;
                     }
                     break;
                  case 83:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 82;
                     }
                     break;
                  case 86:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 85;
                     }
                     break;
                  case 87:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 86;
                     }
                     break;
                  case 90:
                     if (this.curChar == 112) {
                        this.jjstateSet[this.jjnewStateCnt++] = 89;
                     }
                     break;
                  case 91:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 90;
                     }
                     break;
                  case 92:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 91;
                     }
                     break;
                  case 95:
                     if (this.curChar == 112) {
                        this.jjstateSet[this.jjnewStateCnt++] = 94;
                     }
                     break;
                  case 96:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 95;
                     }
                     break;
                  case 97:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 96;
                     }
                     break;
                  case 98:
                     if (this.curChar == 103) {
                        this.jjCheckNAddTwoStates(66, 99);
                     }
                     break;
                  case 100:
                     if ((576460745995190271L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(29, 30);
                     } else if (this.curChar == 92) {
                        this.jjCheckNAdd(31);
                     }
               }
            } while(i != startsAt);
         } else {
            int hiByte = this.curChar >> 8;
            int i1 = hiByte >> 6;
            long l1 = 1L << (hiByte & 63);
            int i2 = (this.curChar & 255) >> 6;
            long l2 = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 0:
                     if (jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(29, 30);
                     }
                     break;
                  case 1:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        this.jjAddStates(417, 419);
                     }
                     break;
                  case 8:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        this.jjAddStates(414, 416);
                     }
                     break;
                  case 16:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        this.jjAddStates(429, 430);
                     }
                     break;
                  case 19:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        this.jjAddStates(431, 432);
                     }
                     break;
                  case 29:
                  case 100:
                     if (jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(29, 30);
                     }
                     break;
                  default:
                     if (i1 != 0 && l1 != 0L && i2 != 0 && l2 == 0L) {
                     }
               }
            } while(i != startsAt);
         }

         if (kind != Integer.MAX_VALUE) {
            this.jjmatchedKind = kind;
            this.jjmatchedPos = curPos;
            kind = Integer.MAX_VALUE;
         }

         ++curPos;
         if ((i = this.jjnewStateCnt) == (startsAt = 100 - (this.jjnewStateCnt = startsAt))) {
            return curPos;
         }

         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var13) {
            return curPos;
         }
      }
   }

   private final int jjStopStringLiteralDfa_4(int pos, long active0, long active1, long active2) {
      switch (pos) {
         case 0:
            if ((active2 & 32L) != 0L) {
               return 2;
            } else {
               if ((active1 & 6442450944L) == 0L && (active2 & 14336L) == 0L) {
                  if ((active1 & 2305983746702049280L) != 0L) {
                     return 46;
                  }

                  if ((active1 & 1152921882563969024L) != 0L) {
                     return 56;
                  }

                  if ((active1 & 145276272354787328L) != 0L) {
                     return 49;
                  }

                  if ((active1 & 8796093022208L) == 0L && (active2 & 2L) == 0L) {
                     return -1;
                  }

                  return 44;
               }

               this.jjmatchedKind = 142;
               return 106;
            }
         case 1:
            if ((active2 & 6144L) != 0L) {
               return 106;
            } else if ((active1 & 1152921848204230656L) != 0L) {
               return 55;
            } else {
               if ((active1 & 6442450944L) == 0L && (active2 & 8192L) == 0L) {
                  return -1;
               }

               if (this.jjmatchedPos != 1) {
                  this.jjmatchedKind = 142;
                  this.jjmatchedPos = 1;
               }

               return 106;
            }
         case 2:
            if ((active1 & 6442450944L) == 0L && (active2 & 8192L) == 0L) {
               return -1;
            }

            this.jjmatchedKind = 142;
            this.jjmatchedPos = 2;
            return 106;
         case 3:
            if ((active1 & 4294967296L) != 0L) {
               return 106;
            } else {
               if ((active1 & 2147483648L) == 0L && (active2 & 8192L) == 0L) {
                  return -1;
               }

               this.jjmatchedKind = 142;
               this.jjmatchedPos = 3;
               return 106;
            }
         default:
            return -1;
      }
   }

   private final int jjStartNfa_4(int pos, long active0, long active1, long active2) {
      return this.jjMoveNfa_4(this.jjStopStringLiteralDfa_4(pos, active0, active1, active2), pos + 1);
   }

   private int jjMoveStringLiteralDfa0_4() {
      switch (this.curChar) {
         case 33:
            this.jjmatchedKind = 129;
            return this.jjMoveStringLiteralDfa1_4(8796093022208L, 0L);
         case 34:
         case 35:
         case 36:
         case 38:
         case 39:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
         case 60:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 92:
         case 94:
         case 95:
         case 96:
         case 98:
         case 99:
         case 100:
         case 101:
         case 103:
         case 104:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 115:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 124:
         default:
            return this.jjMoveNfa_4(1, 0);
         case 37:
            this.jjmatchedKind = 126;
            return this.jjMoveStringLiteralDfa1_4(281474976710656L, 0L);
         case 40:
            return this.jjStopAtPos(0, 135);
         case 41:
            return this.jjStopAtPos(0, 136);
         case 42:
            this.jjmatchedKind = 122;
            return this.jjMoveStringLiteralDfa1_4(576531121047601152L, 0L);
         case 43:
            this.jjmatchedKind = 120;
            return this.jjMoveStringLiteralDfa1_4(580542139465728L, 0L);
         case 44:
            return this.jjStopAtPos(0, 130);
         case 45:
            this.jjmatchedKind = 121;
            return this.jjMoveStringLiteralDfa1_4(1161084278931456L, 0L);
         case 46:
            this.jjmatchedKind = 99;
            return this.jjMoveStringLiteralDfa1_4(1152921848204230656L, 0L);
         case 47:
            this.jjmatchedKind = 125;
            return this.jjMoveStringLiteralDfa1_4(140737488355328L, 0L);
         case 58:
            return this.jjStopAtPos(0, 132);
         case 59:
            return this.jjStopAtPos(0, 131);
         case 61:
            this.jjmatchedKind = 105;
            return this.jjMoveStringLiteralDfa1_4(4398046511104L, 0L);
         case 62:
            return this.jjStopAtPos(0, 148);
         case 63:
            this.jjmatchedKind = 103;
            return this.jjMoveStringLiteralDfa1_4(1099511627776L, 0L);
         case 91:
            return this.jjStartNfaWithStates_4(0, 133, 2);
         case 93:
            return this.jjStopAtPos(0, 134);
         case 97:
            return this.jjMoveStringLiteralDfa1_4(0L, 4096L);
         case 102:
            return this.jjMoveStringLiteralDfa1_4(2147483648L, 0L);
         case 105:
            return this.jjMoveStringLiteralDfa1_4(0L, 2048L);
         case 116:
            return this.jjMoveStringLiteralDfa1_4(4294967296L, 0L);
         case 117:
            return this.jjMoveStringLiteralDfa1_4(0L, 8192L);
         case 123:
            return this.jjStopAtPos(0, 137);
         case 125:
            return this.jjStopAtPos(0, 138);
      }
   }

   private int jjMoveStringLiteralDfa1_4(long active1, long active2) {
      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var6) {
         this.jjStopStringLiteralDfa_4(0, 0L, active1, active2);
         return 1;
      }

      switch (this.curChar) {
         case 42:
            if ((active1 & 576460752303423488L) != 0L) {
               return this.jjStopAtPos(1, 123);
            }
            break;
         case 43:
            if ((active1 & 562949953421312L) != 0L) {
               return this.jjStopAtPos(1, 113);
            }
            break;
         case 45:
            if ((active1 & 1125899906842624L) != 0L) {
               return this.jjStopAtPos(1, 114);
            }
            break;
         case 46:
            if ((active1 & 68719476736L) != 0L) {
               this.jjmatchedKind = 100;
               this.jjmatchedPos = 1;
            }

            return this.jjMoveStringLiteralDfa2_4(active1, 1152921779484753920L, active2, 0L);
         case 61:
            if ((active1 & 4398046511104L) != 0L) {
               return this.jjStopAtPos(1, 106);
            }

            if ((active1 & 8796093022208L) != 0L) {
               return this.jjStopAtPos(1, 107);
            }

            if ((active1 & 17592186044416L) != 0L) {
               return this.jjStopAtPos(1, 108);
            }

            if ((active1 & 35184372088832L) != 0L) {
               return this.jjStopAtPos(1, 109);
            }

            if ((active1 & 70368744177664L) != 0L) {
               return this.jjStopAtPos(1, 110);
            }

            if ((active1 & 140737488355328L) != 0L) {
               return this.jjStopAtPos(1, 111);
            }

            if ((active1 & 281474976710656L) != 0L) {
               return this.jjStopAtPos(1, 112);
            }
            break;
         case 63:
            if ((active1 & 1099511627776L) != 0L) {
               return this.jjStopAtPos(1, 104);
            }
            break;
         case 97:
            return this.jjMoveStringLiteralDfa2_4(active1, 2147483648L, active2, 0L);
         case 110:
            if ((active2 & 2048L) != 0L) {
               return this.jjStartNfaWithStates_4(1, 139, 106);
            }
            break;
         case 114:
            return this.jjMoveStringLiteralDfa2_4(active1, 4294967296L, active2, 0L);
         case 115:
            if ((active2 & 4096L) != 0L) {
               return this.jjStartNfaWithStates_4(1, 140, 106);
            }

            return this.jjMoveStringLiteralDfa2_4(active1, 0L, active2, 8192L);
      }

      return this.jjStartNfa_4(0, 0L, active1, active2);
   }

   private int jjMoveStringLiteralDfa2_4(long old1, long active1, long old2, long active2) {
      if (((active1 &= old1) | (active2 &= old2)) == 0L) {
         return this.jjStartNfa_4(0, 0L, old1, old2);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var10) {
            this.jjStopStringLiteralDfa_4(1, 0L, active1, active2);
            return 2;
         }

         switch (this.curChar) {
            case 42:
               if ((active1 & 274877906944L) != 0L) {
                  return this.jjStopAtPos(2, 102);
               }
               break;
            case 46:
               if ((active1 & 1152921504606846976L) != 0L) {
                  return this.jjStopAtPos(2, 124);
               }
               break;
            case 105:
               return this.jjMoveStringLiteralDfa3_4(active1, 0L, active2, 8192L);
            case 108:
               return this.jjMoveStringLiteralDfa3_4(active1, 2147483648L, active2, 0L);
            case 117:
               return this.jjMoveStringLiteralDfa3_4(active1, 4294967296L, active2, 0L);
         }

         return this.jjStartNfa_4(1, 0L, active1, active2);
      }
   }

   private int jjMoveStringLiteralDfa3_4(long old1, long active1, long old2, long active2) {
      if (((active1 &= old1) | (active2 &= old2)) == 0L) {
         return this.jjStartNfa_4(1, 0L, old1, old2);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var10) {
            this.jjStopStringLiteralDfa_4(2, 0L, active1, active2);
            return 3;
         }

         switch (this.curChar) {
            case 101:
               if ((active1 & 4294967296L) != 0L) {
                  return this.jjStartNfaWithStates_4(3, 96, 106);
               }
            default:
               return this.jjStartNfa_4(2, 0L, active1, active2);
            case 110:
               return this.jjMoveStringLiteralDfa4_4(active1, 0L, active2, 8192L);
            case 115:
               return this.jjMoveStringLiteralDfa4_4(active1, 2147483648L, active2, 0L);
         }
      }
   }

   private int jjMoveStringLiteralDfa4_4(long old1, long active1, long old2, long active2) {
      if (((active1 &= old1) | (active2 &= old2)) == 0L) {
         return this.jjStartNfa_4(2, 0L, old1, old2);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var10) {
            this.jjStopStringLiteralDfa_4(3, 0L, active1, active2);
            return 4;
         }

         switch (this.curChar) {
            case 101:
               if ((active1 & 2147483648L) != 0L) {
                  return this.jjStartNfaWithStates_4(4, 95, 106);
               }
               break;
            case 103:
               if ((active2 & 8192L) != 0L) {
                  return this.jjStartNfaWithStates_4(4, 141, 106);
               }
         }

         return this.jjStartNfa_4(3, 0L, active1, active2);
      }
   }

   private int jjStartNfaWithStates_4(int pos, int kind, int state) {
      this.jjmatchedKind = kind;
      this.jjmatchedPos = pos;

      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var5) {
         return pos + 1;
      }

      return this.jjMoveNfa_4(state, pos + 1);
   }

   private int jjMoveNfa_4(int startState, int curPos) {
      int startsAt = 0;
      this.jjnewStateCnt = 106;
      int i = 1;
      this.jjstateSet[0] = startState;
      int kind = Integer.MAX_VALUE;

      while(true) {
         if (++this.jjround == Integer.MAX_VALUE) {
            this.ReInitRounds();
         }

         long l;
         if (this.curChar < 64) {
            l = 1L << this.curChar;

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 0:
                     if ((4294977024L & l) != 0L) {
                        if (kind > 85) {
                           kind = 85;
                        }

                        this.jjCheckNAdd(0);
                     }
                     break;
                  case 1:
                     if ((287948901175001088L & l) != 0L) {
                        if (kind > 97) {
                           kind = 97;
                        }

                        this.jjCheckNAddStates(433, 435);
                     } else if ((4294977024L & l) != 0L) {
                        if (kind > 85) {
                           kind = 85;
                        }

                        this.jjCheckNAdd(0);
                     } else if (this.curChar == 38) {
                        this.jjAddStates(436, 441);
                     } else if (this.curChar == 46) {
                        this.jjAddStates(442, 443);
                     } else if (this.curChar == 45) {
                        this.jjAddStates(444, 445);
                     } else if (this.curChar == 47) {
                        this.jjAddStates(446, 447);
                     } else if (this.curChar == 33) {
                        this.jjCheckNAdd(44);
                     } else if (this.curChar == 35) {
                        this.jjCheckNAdd(38);
                     } else if (this.curChar == 36) {
                        this.jjCheckNAdd(38);
                     } else if (this.curChar == 60) {
                        this.jjCheckNAdd(27);
                     } else if (this.curChar == 39) {
                        this.jjCheckNAddStates(358, 360);
                     } else if (this.curChar == 34) {
                        this.jjCheckNAddStates(361, 363);
                     }

                     if (this.curChar == 36) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     } else if (this.curChar == 38) {
                        if (kind > 127) {
                           kind = 127;
                        }
                     } else if (this.curChar == 60 && kind > 115) {
                        kind = 115;
                     }

                     if (this.curChar == 60) {
                        this.jjstateSet[this.jjnewStateCnt++] = 2;
                     }
                     break;
                  case 2:
                     if ((42949672960L & l) != 0L) {
                        this.jjstateSet[this.jjnewStateCnt++] = 4;
                     } else if (this.curChar == 61 && kind > 143) {
                        kind = 143;
                     }
                     break;
                  case 3:
                     if (this.curChar == 45 && kind > 86) {
                        kind = 86;
                     }
                     break;
                  case 4:
                     if (this.curChar == 45) {
                        this.jjstateSet[this.jjnewStateCnt++] = 3;
                     }
                     break;
                  case 5:
                     if (this.curChar == 34) {
                        this.jjCheckNAddStates(361, 363);
                     }
                     break;
                  case 6:
                     if ((-17179869185L & l) != 0L) {
                        this.jjCheckNAddStates(361, 363);
                     }
                  case 7:
                  case 8:
                  case 14:
                  case 15:
                  case 19:
                  case 30:
                  case 31:
                  case 32:
                  case 35:
                  case 37:
                  case 38:
                  case 42:
                  case 47:
                  case 51:
                  case 52:
                  case 64:
                  case 65:
                  case 66:
                  case 67:
                  case 68:
                  case 69:
                  case 70:
                  case 71:
                  case 72:
                  case 73:
                  case 74:
                  case 75:
                  case 76:
                  case 77:
                  case 78:
                  case 79:
                  case 82:
                  case 83:
                  case 85:
                  case 86:
                  case 88:
                  case 89:
                  case 92:
                  case 93:
                  case 96:
                  case 97:
                  case 98:
                  case 101:
                  case 102:
                  case 103:
                  case 104:
                  case 105:
                  default:
                     break;
                  case 9:
                     if ((287948901175001088L & l) != 0L) {
                        this.jjCheckNAddStates(361, 363);
                     }
                     break;
                  case 10:
                     if (this.curChar == 34 && kind > 93) {
                        kind = 93;
                     }
                     break;
                  case 11:
                     if ((2305843576149377024L & l) != 0L) {
                        this.jjCheckNAddStates(361, 363);
                     }
                     break;
                  case 12:
                     if (this.curChar == 39) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 13:
                     if ((-549755813889L & l) != 0L) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 16:
                     if ((287948901175001088L & l) != 0L) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 17:
                     if (this.curChar == 39 && kind > 93) {
                        kind = 93;
                     }
                     break;
                  case 18:
                     if ((2305843576149377024L & l) != 0L) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 20:
                     if (this.curChar == 34) {
                        this.jjCheckNAddTwoStates(21, 22);
                     }
                     break;
                  case 21:
                     if ((-17179869185L & l) != 0L) {
                        this.jjCheckNAddTwoStates(21, 22);
                     }
                     break;
                  case 22:
                     if (this.curChar == 34 && kind > 94) {
                        kind = 94;
                     }
                     break;
                  case 23:
                     if (this.curChar == 39) {
                        this.jjCheckNAddTwoStates(24, 25);
                     }
                     break;
                  case 24:
                     if ((-549755813889L & l) != 0L) {
                        this.jjCheckNAddTwoStates(24, 25);
                     }
                     break;
                  case 25:
                     if (this.curChar == 39 && kind > 94) {
                        kind = 94;
                     }
                     break;
                  case 26:
                     if (this.curChar == 60 && kind > 115) {
                        kind = 115;
                     }
                     break;
                  case 27:
                     if (this.curChar == 61 && kind > 116) {
                        kind = 116;
                     }
                     break;
                  case 28:
                     if (this.curChar == 60) {
                        this.jjCheckNAdd(27);
                     }
                     break;
                  case 29:
                  case 94:
                     if (this.curChar == 38 && kind > 127) {
                        kind = 127;
                     }
                     break;
                  case 33:
                     if (this.curChar == 36) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  case 34:
                  case 106:
                     if ((287948969894477824L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  case 36:
                     if ((288335963627716608L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  case 39:
                     if (this.curChar == 36) {
                        this.jjCheckNAdd(38);
                     }
                     break;
                  case 40:
                     if (this.curChar == 35) {
                        this.jjCheckNAdd(38);
                     }
                     break;
                  case 41:
                     if (this.curChar == 61 && kind > 143) {
                        kind = 143;
                     }
                     break;
                  case 43:
                     if (this.curChar == 33) {
                        this.jjCheckNAdd(44);
                     }
                     break;
                  case 44:
                     if ((4294977024L & l) != 0L) {
                        if (kind > 153) {
                           kind = 153;
                        }

                        this.jjCheckNAdd(44);
                     }
                     break;
                  case 45:
                     if (this.curChar == 47) {
                        this.jjAddStates(446, 447);
                     }
                     break;
                  case 46:
                     if (this.curChar == 62 && kind > 149) {
                        kind = 149;
                     }
                     break;
                  case 48:
                     if (this.curChar == 45) {
                        this.jjAddStates(444, 445);
                     }
                     break;
                  case 49:
                     if (this.curChar == 38) {
                        this.jjstateSet[this.jjnewStateCnt++] = 52;
                     } else if (this.curChar == 62 && kind > 119) {
                        kind = 119;
                     }
                     break;
                  case 50:
                     if (this.curChar == 59 && kind > 119) {
                        kind = 119;
                     }
                     break;
                  case 53:
                     if (this.curChar == 38) {
                        this.jjstateSet[this.jjnewStateCnt++] = 52;
                     }
                     break;
                  case 54:
                     if (this.curChar == 46) {
                        this.jjAddStates(442, 443);
                     }
                     break;
                  case 55:
                     if (this.curChar == 33) {
                        if (kind > 101) {
                           kind = 101;
                        }
                     } else if (this.curChar == 60 && kind > 101) {
                        kind = 101;
                     }
                     break;
                  case 56:
                     if (this.curChar == 46) {
                        this.jjstateSet[this.jjnewStateCnt++] = 57;
                     }

                     if (this.curChar == 46) {
                        this.jjstateSet[this.jjnewStateCnt++] = 55;
                     }
                     break;
                  case 57:
                     if (this.curChar == 33 && kind > 101) {
                        kind = 101;
                     }
                     break;
                  case 58:
                     if (this.curChar == 46) {
                        this.jjstateSet[this.jjnewStateCnt++] = 57;
                     }
                     break;
                  case 59:
                     if ((287948901175001088L & l) != 0L) {
                        if (kind > 97) {
                           kind = 97;
                        }

                        this.jjCheckNAddStates(433, 435);
                     }
                     break;
                  case 60:
                     if ((287948901175001088L & l) != 0L) {
                        if (kind > 97) {
                           kind = 97;
                        }

                        this.jjCheckNAdd(60);
                     }
                     break;
                  case 61:
                     if ((287948901175001088L & l) != 0L) {
                        this.jjCheckNAddTwoStates(61, 62);
                     }
                     break;
                  case 62:
                     if (this.curChar == 46) {
                        this.jjCheckNAdd(63);
                     }
                     break;
                  case 63:
                     if ((287948901175001088L & l) != 0L) {
                        if (kind > 98) {
                           kind = 98;
                        }

                        this.jjCheckNAdd(63);
                     }
                     break;
                  case 80:
                     if (this.curChar == 38) {
                        this.jjAddStates(436, 441);
                     }
                     break;
                  case 81:
                     if (this.curChar == 59 && kind > 115) {
                        kind = 115;
                     }
                     break;
                  case 84:
                     if (this.curChar == 59) {
                        this.jjCheckNAdd(27);
                     }
                     break;
                  case 87:
                     if (this.curChar == 59 && kind > 117) {
                        kind = 117;
                     }
                     break;
                  case 90:
                     if (this.curChar == 61 && kind > 118) {
                        kind = 118;
                     }
                     break;
                  case 91:
                     if (this.curChar == 59) {
                        this.jjstateSet[this.jjnewStateCnt++] = 90;
                     }
                     break;
                  case 95:
                     if (this.curChar == 59 && kind > 127) {
                        kind = 127;
                     }
                     break;
                  case 99:
                     if (this.curChar == 38) {
                        this.jjstateSet[this.jjnewStateCnt++] = 98;
                     }
                     break;
                  case 100:
                     if (this.curChar == 59) {
                        this.jjstateSet[this.jjnewStateCnt++] = 99;
                     }
               }
            } while(i != startsAt);
         } else if (this.curChar < 128) {
            l = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 1:
                     if ((576460745995190271L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     } else if (this.curChar == 92) {
                        this.jjAddStates(448, 452);
                     } else if (this.curChar == 91) {
                        this.jjstateSet[this.jjnewStateCnt++] = 41;
                     } else if (this.curChar == 124) {
                        this.jjstateSet[this.jjnewStateCnt++] = 31;
                     }

                     if (this.curChar == 103) {
                        this.jjCheckNAddTwoStates(72, 105);
                     } else if (this.curChar == 108) {
                        this.jjCheckNAddTwoStates(65, 67);
                     } else if (this.curChar == 92) {
                        this.jjCheckNAdd(36);
                     } else if (this.curChar == 124) {
                        if (kind > 128) {
                           kind = 128;
                        }
                     } else if (this.curChar == 114) {
                        this.jjAddStates(369, 370);
                     } else if (this.curChar == 91) {
                        this.jjstateSet[this.jjnewStateCnt++] = 2;
                     }
                  case 2:
                  case 3:
                  case 4:
                  case 5:
                  case 10:
                  case 12:
                  case 17:
                  case 20:
                  case 22:
                  case 23:
                  case 25:
                  case 26:
                  case 27:
                  case 28:
                  case 29:
                  case 36:
                  case 39:
                  case 40:
                  case 41:
                  case 43:
                  case 44:
                  case 45:
                  case 47:
                  case 48:
                  case 49:
                  case 50:
                  case 53:
                  case 54:
                  case 55:
                  case 56:
                  case 57:
                  case 58:
                  case 59:
                  case 60:
                  case 61:
                  case 62:
                  case 63:
                  case 80:
                  case 81:
                  case 84:
                  case 87:
                  case 90:
                  case 91:
                  case 94:
                  case 95:
                  case 99:
                  case 100:
                  default:
                     break;
                  case 6:
                     if ((-268435457L & l) != 0L) {
                        this.jjCheckNAddStates(361, 363);
                     }
                     break;
                  case 7:
                     if (this.curChar == 92) {
                        this.jjAddStates(371, 372);
                     }
                     break;
                  case 8:
                     if (this.curChar == 120) {
                        this.jjstateSet[this.jjnewStateCnt++] = 9;
                     }
                     break;
                  case 9:
                     if ((541165879422L & l) != 0L) {
                        this.jjCheckNAddStates(361, 363);
                     }
                     break;
                  case 11:
                     if ((582179063439818752L & l) != 0L) {
                        this.jjCheckNAddStates(361, 363);
                     }
                     break;
                  case 13:
                     if ((-268435457L & l) != 0L) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 14:
                     if (this.curChar == 92) {
                        this.jjAddStates(373, 374);
                     }
                     break;
                  case 15:
                     if (this.curChar == 120) {
                        this.jjstateSet[this.jjnewStateCnt++] = 16;
                     }
                     break;
                  case 16:
                     if ((541165879422L & l) != 0L) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 18:
                     if ((582179063439818752L & l) != 0L) {
                        this.jjCheckNAddStates(358, 360);
                     }
                     break;
                  case 19:
                     if (this.curChar == 114) {
                        this.jjAddStates(369, 370);
                     }
                     break;
                  case 21:
                     this.jjAddStates(375, 376);
                     break;
                  case 24:
                     this.jjAddStates(377, 378);
                     break;
                  case 30:
                  case 31:
                     if (this.curChar == 124 && kind > 128) {
                        kind = 128;
                     }
                     break;
                  case 32:
                     if (this.curChar == 124) {
                        this.jjstateSet[this.jjnewStateCnt++] = 31;
                     }
                     break;
                  case 33:
                     if ((576460745995190271L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  case 34:
                     if ((576460745995190271L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  case 35:
                     if (this.curChar == 92) {
                        this.jjCheckNAdd(36);
                     }
                     break;
                  case 37:
                     if (this.curChar == 92) {
                        this.jjCheckNAdd(36);
                     }
                     break;
                  case 38:
                     if (this.curChar == 123 && kind > 143) {
                        kind = 143;
                     }
                     break;
                  case 42:
                     if (this.curChar == 91) {
                        this.jjstateSet[this.jjnewStateCnt++] = 41;
                     }
                     break;
                  case 46:
                     if (this.curChar == 93 && kind > 149) {
                        kind = 149;
                     }
                     break;
                  case 51:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 50;
                     }
                     break;
                  case 52:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 51;
                     }
                     break;
                  case 64:
                     if (this.curChar == 108) {
                        this.jjCheckNAddTwoStates(65, 67);
                     }
                     break;
                  case 65:
                     if (this.curChar == 116 && kind > 115) {
                        kind = 115;
                     }
                     break;
                  case 66:
                     if (this.curChar == 101 && kind > 116) {
                        kind = 116;
                     }
                     break;
                  case 67:
                  case 70:
                     if (this.curChar == 116) {
                        this.jjCheckNAdd(66);
                     }
                     break;
                  case 68:
                     if (this.curChar == 92) {
                        this.jjAddStates(448, 452);
                     }
                     break;
                  case 69:
                     if (this.curChar == 108) {
                        this.jjCheckNAdd(65);
                     }
                     break;
                  case 71:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 70;
                     }
                     break;
                  case 72:
                     if (this.curChar == 116 && kind > 117) {
                        kind = 117;
                     }
                     break;
                  case 73:
                     if (this.curChar == 103) {
                        this.jjCheckNAdd(72);
                     }
                     break;
                  case 74:
                     if (this.curChar == 101 && kind > 118) {
                        kind = 118;
                     }
                     break;
                  case 75:
                  case 105:
                     if (this.curChar == 116) {
                        this.jjCheckNAdd(74);
                     }
                     break;
                  case 76:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 75;
                     }
                     break;
                  case 77:
                     if (this.curChar == 100 && kind > 127) {
                        kind = 127;
                     }
                     break;
                  case 78:
                     if (this.curChar == 110) {
                        this.jjstateSet[this.jjnewStateCnt++] = 77;
                     }
                     break;
                  case 79:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 78;
                     }
                     break;
                  case 82:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 81;
                     }
                     break;
                  case 83:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 82;
                     }
                     break;
                  case 85:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 84;
                     }
                     break;
                  case 86:
                     if (this.curChar == 108) {
                        this.jjstateSet[this.jjnewStateCnt++] = 85;
                     }
                     break;
                  case 88:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 87;
                     }
                     break;
                  case 89:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 88;
                     }
                     break;
                  case 92:
                     if (this.curChar == 116) {
                        this.jjstateSet[this.jjnewStateCnt++] = 91;
                     }
                     break;
                  case 93:
                     if (this.curChar == 103) {
                        this.jjstateSet[this.jjnewStateCnt++] = 92;
                     }
                     break;
                  case 96:
                     if (this.curChar == 112) {
                        this.jjstateSet[this.jjnewStateCnt++] = 95;
                     }
                     break;
                  case 97:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 96;
                     }
                     break;
                  case 98:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 97;
                     }
                     break;
                  case 101:
                     if (this.curChar == 112) {
                        this.jjstateSet[this.jjnewStateCnt++] = 100;
                     }
                     break;
                  case 102:
                     if (this.curChar == 109) {
                        this.jjstateSet[this.jjnewStateCnt++] = 101;
                     }
                     break;
                  case 103:
                     if (this.curChar == 97) {
                        this.jjstateSet[this.jjnewStateCnt++] = 102;
                     }
                     break;
                  case 104:
                     if (this.curChar == 103) {
                        this.jjCheckNAddTwoStates(72, 105);
                     }
                     break;
                  case 106:
                     if ((576460745995190271L & l) != 0L) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     } else if (this.curChar == 92) {
                        this.jjCheckNAdd(36);
                     }
               }
            } while(i != startsAt);
         } else {
            int hiByte = this.curChar >> 8;
            int i1 = hiByte >> 6;
            long l1 = 1L << (hiByte & 63);
            int i2 = (this.curChar & 255) >> 6;
            long l2 = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 1:
                     if (jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  case 6:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        this.jjAddStates(361, 363);
                     }
                     break;
                  case 13:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        this.jjAddStates(358, 360);
                     }
                     break;
                  case 21:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        this.jjAddStates(375, 376);
                     }
                     break;
                  case 24:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        this.jjAddStates(377, 378);
                     }
                     break;
                  case 34:
                  case 106:
                     if (jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                        if (kind > 142) {
                           kind = 142;
                        }

                        this.jjCheckNAddTwoStates(34, 35);
                     }
                     break;
                  default:
                     if (i1 != 0 && l1 != 0L && i2 != 0 && l2 == 0L) {
                     }
               }
            } while(i != startsAt);
         }

         if (kind != Integer.MAX_VALUE) {
            this.jjmatchedKind = kind;
            this.jjmatchedPos = curPos;
            kind = Integer.MAX_VALUE;
         }

         ++curPos;
         if ((i = this.jjnewStateCnt) == (startsAt = 106 - (this.jjnewStateCnt = startsAt))) {
            return curPos;
         }

         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var13) {
            return curPos;
         }
      }
   }

   private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
      switch (hiByte) {
         case 0:
            return (jjbitVec2[i2] & l2) != 0L;
         default:
            return (jjbitVec0[i1] & l1) != 0L;
      }
   }

   private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2) {
      switch (hiByte) {
         case 0:
            return (jjbitVec4[i2] & l2) != 0L;
         case 32:
            return (jjbitVec5[i2] & l2) != 0L;
         case 33:
            return (jjbitVec6[i2] & l2) != 0L;
         case 44:
            return (jjbitVec7[i2] & l2) != 0L;
         case 45:
            return (jjbitVec8[i2] & l2) != 0L;
         case 46:
            return (jjbitVec9[i2] & l2) != 0L;
         case 48:
            return (jjbitVec10[i2] & l2) != 0L;
         case 49:
            return (jjbitVec11[i2] & l2) != 0L;
         case 51:
            return (jjbitVec12[i2] & l2) != 0L;
         case 77:
            return (jjbitVec13[i2] & l2) != 0L;
         case 164:
            return (jjbitVec14[i2] & l2) != 0L;
         case 166:
            return (jjbitVec15[i2] & l2) != 0L;
         case 167:
            return (jjbitVec16[i2] & l2) != 0L;
         case 168:
            return (jjbitVec17[i2] & l2) != 0L;
         case 169:
            return (jjbitVec18[i2] & l2) != 0L;
         case 170:
            return (jjbitVec19[i2] & l2) != 0L;
         case 171:
            return (jjbitVec20[i2] & l2) != 0L;
         case 215:
            return (jjbitVec21[i2] & l2) != 0L;
         case 251:
            return (jjbitVec22[i2] & l2) != 0L;
         case 253:
            return (jjbitVec23[i2] & l2) != 0L;
         case 254:
            return (jjbitVec24[i2] & l2) != 0L;
         case 255:
            return (jjbitVec25[i2] & l2) != 0L;
         default:
            return (jjbitVec3[i1] & l1) != 0L;
      }
   }

   protected Token jjFillToken() {
      String im = jjstrLiteralImages[this.jjmatchedKind];
      String curTokenImage = im == null ? this.input_stream.GetImage() : im;
      int beginLine = this.input_stream.getBeginLine();
      int beginColumn = this.input_stream.getBeginColumn();
      int endLine = this.input_stream.getEndLine();
      int endColumn = this.input_stream.getEndColumn();
      Token t = Token.newToken(this.jjmatchedKind, curTokenImage);
      t.beginLine = beginLine;
      t.endLine = endLine;
      t.beginColumn = beginColumn;
      t.endColumn = endColumn;
      return t;
   }

   public Token getNextToken() {
      int curPos = 0;

      while(true) {
         Token matchedToken;
         label97:
         while(true) {
            try {
               this.curChar = this.input_stream.BeginToken();
            } catch (Exception var8) {
               this.jjmatchedKind = 0;
               this.jjmatchedPos = -1;
               matchedToken = this.jjFillToken();
               return matchedToken;
            }

            this.image = this.jjimage;
            this.image.setLength(0);
            this.jjimageLen = 0;
            switch (this.curLexState) {
               case 0:
                  this.jjmatchedKind = Integer.MAX_VALUE;
                  this.jjmatchedPos = 0;
                  curPos = this.jjMoveStringLiteralDfa0_0();
                  break label97;
               case 1:
                  this.jjmatchedKind = Integer.MAX_VALUE;
                  this.jjmatchedPos = 0;
                  curPos = this.jjMoveStringLiteralDfa0_1();
                  break label97;
               case 2:
                  this.jjmatchedKind = Integer.MAX_VALUE;
                  this.jjmatchedPos = 0;
                  curPos = this.jjMoveStringLiteralDfa0_2();
                  break label97;
               case 3:
                  this.jjmatchedKind = Integer.MAX_VALUE;
                  this.jjmatchedPos = 0;
                  curPos = this.jjMoveStringLiteralDfa0_3();
                  break label97;
               case 4:
                  this.jjmatchedKind = Integer.MAX_VALUE;
                  this.jjmatchedPos = 0;
                  curPos = this.jjMoveStringLiteralDfa0_4();
                  break label97;
               case 5:
                  try {
                     this.input_stream.backup(0);

                     while(this.curChar < 64 && (4611686018427387904L & 1L << this.curChar) != 0L || this.curChar >> 6 == 1 && (536870912L & 1L << (this.curChar & 63)) != 0L) {
                        this.curChar = this.input_stream.BeginToken();
                     }
                  } catch (IOException var10) {
                     break;
                  }

                  this.jjmatchedKind = Integer.MAX_VALUE;
                  this.jjmatchedPos = 0;
                  curPos = this.jjMoveStringLiteralDfa0_5();
                  break label97;
               case 6:
                  this.jjmatchedKind = Integer.MAX_VALUE;
                  this.jjmatchedPos = 0;
                  curPos = this.jjMoveStringLiteralDfa0_6();
                  break label97;
               case 7:
                  this.jjmatchedKind = Integer.MAX_VALUE;
                  this.jjmatchedPos = 0;
                  curPos = this.jjMoveStringLiteralDfa0_7();
               default:
                  break label97;
            }
         }

         if (this.jjmatchedKind == Integer.MAX_VALUE) {
            int error_line = this.input_stream.getEndLine();
            int error_column = this.input_stream.getEndColumn();
            String error_after = null;
            boolean EOFSeen = false;

            try {
               this.input_stream.readChar();
               this.input_stream.backup(1);
            } catch (IOException var9) {
               EOFSeen = true;
               error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
               if (this.curChar != 10 && this.curChar != 13) {
                  ++error_column;
               } else {
                  ++error_line;
                  error_column = 0;
               }
            }

            if (!EOFSeen) {
               this.input_stream.backup(1);
               error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
            }

            throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
         }

         if (this.jjmatchedPos + 1 < curPos) {
            this.input_stream.backup(curPos - this.jjmatchedPos - 1);
         }

         if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
            matchedToken = this.jjFillToken();
            this.TokenLexicalActions(matchedToken);
            if (jjnewLexState[this.jjmatchedKind] != -1) {
               this.curLexState = jjnewLexState[this.jjmatchedKind];
            }

            return matchedToken;
         }

         this.SkipLexicalActions((Token)null);
         if (jjnewLexState[this.jjmatchedKind] != -1) {
            this.curLexState = jjnewLexState[this.jjmatchedKind];
         }
      }
   }

   void SkipLexicalActions(Token matchedToken) {
      switch (this.jjmatchedKind) {
         case 91:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            if (this.parenthesisNesting > 0) {
               this.SwitchTo(3);
            } else if (this.inInvocation) {
               this.SwitchTo(4);
            } else {
               this.SwitchTo(2);
            }
         default:
      }
   }

   void TokenLexicalActions(Token matchedToken) {
      int index;
      String dn;
      int srcLn;
      switch (this.jjmatchedKind) {
         case 6:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 7:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 8:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
            break;
         case 9:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 4), 2);
            break;
         case 10:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
            break;
         case 11:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
         case 12:
         case 79:
         case 80:
         case 81:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 91:
         case 92:
         case 93:
         case 94:
         case 95:
         case 96:
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         case 105:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 115:
         case 116:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 123:
         case 124:
         case 125:
         case 126:
         case 127:
         case 128:
         case 129:
         case 130:
         case 131:
         case 132:
         case 139:
         case 140:
         case 141:
         case 144:
         case 145:
         case 146:
         case 147:
         case 150:
         case 151:
         case 152:
         case 153:
         default:
            break;
         case 13:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 3), 2);
            break;
         case 14:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
            break;
         case 15:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
            break;
         case 16:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
            break;
         case 17:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
            break;
         case 18:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
            break;
         case 19:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
            break;
         case 20:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
            break;
         case 21:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
            break;
         case 22:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
            break;
         case 23:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
            break;
         case 24:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
            break;
         case 25:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
            break;
         case 26:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
            break;
         case 27:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
            break;
         case 28:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
            break;
         case 29:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 6), 2);
            break;
         case 30:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 4), 0);
            break;
         case 31:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 2), 0);
            break;
         case 32:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 33:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 7);
            this.noparseTag = "comment";
            break;
         case 34:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.noparseTag = "-->";
            this.handleTagSyntaxAndSwitch(matchedToken, 7);
            break;
         case 35:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            int tagNamingConvention = getTagNamingConvention(matchedToken, 2);
            this.handleTagSyntaxAndSwitch(matchedToken, tagNamingConvention, 7);
            this.noparseTag = tagNamingConvention == 12 ? "noParse" : "noparse";
            break;
         case 36:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 37:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 38:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 39:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 40:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 41:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 42:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 3), 0);
            break;
         case 43:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 44:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 45:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 46:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 47:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 48:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 6), 0);
            break;
         case 49:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 4), 0);
            break;
         case 50:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 2), 0);
            break;
         case 51:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 52:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 53:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 54:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 55:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 56:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 57:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 58:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 59:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 60:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 61:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 62:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 63:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 64:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 65:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 66:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
            break;
         case 67:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 68:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
            break;
         case 69:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 70:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 2);
            break;
         case 71:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, 0);
            break;
         case 72:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 2), 0);
            break;
         case 73:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 2), 0);
            break;
         case 74:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.unifiedCall(matchedToken);
            break;
         case 75:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.unifiedCallEnd(matchedToken);
            break;
         case 76:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.ftlHeader(matchedToken);
            break;
         case 77:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            this.ftlHeader(matchedToken);
            break;
         case 78:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            if (!this.tagSyntaxEstablished && this.incompatibleImprovements < _TemplateAPI.VERSION_INT_2_3_19) {
               matchedToken.kind = 80;
            } else {
               char firstChar = matchedToken.image.charAt(0);
               if (!this.tagSyntaxEstablished && this.autodetectTagSyntax) {
                  this.squBracTagSyntax = firstChar == '[';
                  this.tagSyntaxEstablished = true;
               }

               if (firstChar == '<' && this.squBracTagSyntax) {
                  matchedToken.kind = 80;
               } else if (firstChar == '[' && !this.squBracTagSyntax) {
                  matchedToken.kind = 80;
               } else if (this.strictSyntaxMode) {
                  dn = matchedToken.image;
                  index = dn.indexOf(35);
                  dn = dn.substring(index + 1);
                  if (_CoreAPI.ALL_BUILT_IN_DIRECTIVE_NAMES.contains(dn)) {
                     throw new TokenMgrError("#" + dn + " is an existing directive, but the tag is malformed.  (See FreeMarker Manual / Directive Reference.)", 0, matchedToken.beginLine, matchedToken.beginColumn + 1, matchedToken.endLine, matchedToken.endColumn);
                  }

                  String tip = null;
                  if (!dn.equals("set") && !dn.equals("var")) {
                     if (!dn.equals("else_if") && !dn.equals("elif")) {
                        if (dn.equals("no_escape")) {
                           tip = "Use #noescape instead.";
                        } else if (dn.equals("method")) {
                           tip = "Use #function instead.";
                        } else if (!dn.equals("head") && !dn.equals("template") && !dn.equals("fm")) {
                           if (!dn.equals("try") && !dn.equals("atempt")) {
                              if (!dn.equals("for") && !dn.equals("each") && !dn.equals("iterate") && !dn.equals("iterator")) {
                                 if (dn.equals("prefix")) {
                                    tip = "You may meant #import. (If you have seen this directive in use elsewhere, this was a planned directive, so maybe you need to upgrade FreeMarker.)";
                                 } else if (!dn.equals("item") && !dn.equals("row") && !dn.equals("rows")) {
                                    if (!dn.equals("separator") && !dn.equals("separate") && !dn.equals("separ")) {
                                       tip = "Help (latest version): http://freemarker.org/docs/ref_directive_alphaidx.html; you're using FreeMarker " + Configuration.getVersion() + ".";
                                    } else {
                                       tip = "You may meant #sep.";
                                    }
                                 } else {
                                    tip = "You may meant #items.";
                                 }
                              } else {
                                 tip = "You may meant #list (http://freemarker.org/docs/ref_directive_list.html).";
                              }
                           } else {
                              tip = "You may meant #attempt.";
                           }
                        } else {
                           tip = "You may meant #ftl.";
                        }
                     } else {
                        tip = "Use #elseif.";
                     }
                  } else {
                     tip = "Use #assign or #local or #global, depending on the intented scope (#assign is template-scope). (If you have seen this directive in use elsewhere, this was a planned directive, so maybe you need to upgrade FreeMarker.)";
                  }

                  throw new TokenMgrError("Unknown directive: #" + dn + (tip != null ? ". " + tip : ""), 0, matchedToken.beginLine, matchedToken.beginColumn + 1, matchedToken.endLine, matchedToken.endColumn);
               }
            }
            break;
         case 82:
            this.image.append(jjstrLiteralImages[82]);
            this.lengthOfMatch = jjstrLiteralImages[82].length();
            this.startInterpolation(matchedToken);
            break;
         case 83:
            this.image.append(jjstrLiteralImages[83]);
            this.lengthOfMatch = jjstrLiteralImages[83].length();
            this.startInterpolation(matchedToken);
            break;
         case 84:
            this.image.append(jjstrLiteralImages[84]);
            this.lengthOfMatch = jjstrLiteralImages[84].length();
            this.startInterpolation(matchedToken);
            break;
         case 133:
            this.image.append(jjstrLiteralImages[133]);
            this.lengthOfMatch = jjstrLiteralImages[133].length();
            ++this.bracketNesting;
            break;
         case 134:
            this.image.append(jjstrLiteralImages[134]);
            this.lengthOfMatch = jjstrLiteralImages[134].length();
            if (this.bracketNesting > 0) {
               --this.bracketNesting;
            } else {
               if (this.interpolationSyntax == 22 && this.postInterpolationLexState != -1) {
                  this.endInterpolation(matchedToken);
                  break;
               }

               if (!this.squBracTagSyntax && (this.incompatibleImprovements >= _TemplateAPI.VERSION_INT_2_3_28 || this.interpolationSyntax == 22) || this.postInterpolationLexState != -1) {
                  throw this.newUnexpectedClosingTokenException(matchedToken);
               }

               matchedToken.kind = 148;
               if (this.inFTLHeader) {
                  this.eatNewline();
                  this.inFTLHeader = false;
               }

               this.SwitchTo(0);
            }
            break;
         case 135:
            this.image.append(jjstrLiteralImages[135]);
            this.lengthOfMatch = jjstrLiteralImages[135].length();
            ++this.parenthesisNesting;
            if (this.parenthesisNesting == 1) {
               this.SwitchTo(3);
            }
            break;
         case 136:
            this.image.append(jjstrLiteralImages[136]);
            this.lengthOfMatch = jjstrLiteralImages[136].length();
            --this.parenthesisNesting;
            if (this.parenthesisNesting == 0) {
               if (this.inInvocation) {
                  this.SwitchTo(4);
               } else {
                  this.SwitchTo(2);
               }
            }
            break;
         case 137:
            this.image.append(jjstrLiteralImages[137]);
            this.lengthOfMatch = jjstrLiteralImages[137].length();
            ++this.curlyBracketNesting;
            break;
         case 138:
            this.image.append(jjstrLiteralImages[138]);
            this.lengthOfMatch = jjstrLiteralImages[138].length();
            if (this.curlyBracketNesting > 0) {
               --this.curlyBracketNesting;
            } else {
               if (this.interpolationSyntax == 22 || this.postInterpolationLexState == -1) {
                  throw this.newUnexpectedClosingTokenException(matchedToken);
               }

               this.endInterpolation(matchedToken);
            }
            break;
         case 142:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            String s = matchedToken.image;
            if (s.indexOf(92) != -1) {
               srcLn = s.length();
               char[] newS = new char[srcLn - 1];
               int dstIdx = 0;

               for(int srcIdx = 0; srcIdx < srcLn; ++srcIdx) {
                  char c = s.charAt(srcIdx);
                  if (c != '\\') {
                     newS[dstIdx++] = c;
                  }
               }

               matchedToken.image = new String(newS, 0, dstIdx);
            }
            break;
         case 143:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            if ("".length() == 0) {
               srcLn = matchedToken.image.charAt(0) != '[' ? 125 : 93;
               throw new TokenMgrError("You can't use " + matchedToken.image + "..." + srcLn + " (an interpolation) here as you are already in FreeMarker-expression-mode. Thus, instead of " + matchedToken.image + "myExpression" + srcLn + ", just write myExpression. (" + matchedToken.image + "..." + srcLn + " is only used where otherwise static text is expected, i.e., outside FreeMarker tags and interpolations, or inside string literals.)", 0, matchedToken.beginLine, matchedToken.beginColumn, matchedToken.endLine, matchedToken.endColumn);
            }
            break;
         case 148:
            this.image.append(jjstrLiteralImages[148]);
            this.lengthOfMatch = jjstrLiteralImages[148].length();
            if (this.inFTLHeader) {
               this.eatNewline();
               this.inFTLHeader = false;
            }

            if (!this.squBracTagSyntax && this.postInterpolationLexState == -1) {
               this.SwitchTo(0);
            } else {
               matchedToken.kind = 150;
            }
            break;
         case 149:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            if (this.tagSyntaxEstablished && (this.incompatibleImprovements >= _TemplateAPI.VERSION_INT_2_3_28 || this.interpolationSyntax == 22)) {
               dn = matchedToken.image;
               index = dn.charAt(dn.length() - 1);
               if (!this.squBracTagSyntax && index != 62 || this.squBracTagSyntax && index != 93) {
                  throw new TokenMgrError("The tag shouldn't end with \"" + index + "\".", 0, matchedToken.beginLine, matchedToken.beginColumn, matchedToken.endLine, matchedToken.endColumn);
               }
            }

            if (this.inFTLHeader) {
               this.eatNewline();
               this.inFTLHeader = false;
            }

            this.SwitchTo(0);
            break;
         case 154:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            if (this.noparseTag.equals("-->")) {
               boolean squareBracket = matchedToken.image.endsWith("]");
               if (this.squBracTagSyntax && squareBracket || !this.squBracTagSyntax && !squareBracket) {
                  matchedToken.image = matchedToken.image + ";";
                  this.SwitchTo(0);
               }
            }
            break;
         case 155:
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
            StringTokenizer st = new StringTokenizer(this.image.toString(), " \t\n\r<>[]/#", false);
            if (st.nextToken().equals(this.noparseTag)) {
               matchedToken.image = matchedToken.image + ";";
               this.SwitchTo(0);
            }
      }

   }

   private void jjCheckNAdd(int state) {
      if (this.jjrounds[state] != this.jjround) {
         this.jjstateSet[this.jjnewStateCnt++] = state;
         this.jjrounds[state] = this.jjround;
      }

   }

   private void jjAddStates(int start, int end) {
      do {
         this.jjstateSet[this.jjnewStateCnt++] = jjnextStates[start];
      } while(start++ != end);

   }

   private void jjCheckNAddTwoStates(int state1, int state2) {
      this.jjCheckNAdd(state1);
      this.jjCheckNAdd(state2);
   }

   private void jjCheckNAddStates(int start, int end) {
      do {
         this.jjCheckNAdd(jjnextStates[start]);
      } while(start++ != end);

   }

   public FMParserTokenManager(SimpleCharStream stream) {
      this.debugStream = System.out;
      this.curLexState = 0;
      this.defaultLexState = 0;
      this.jjrounds = new int[713];
      this.jjstateSet = new int[1426];
      this.jjimage = new StringBuilder();
      this.image = this.jjimage;
      this.input_stream = stream;
   }

   public FMParserTokenManager(SimpleCharStream stream, int lexState) {
      this.debugStream = System.out;
      this.curLexState = 0;
      this.defaultLexState = 0;
      this.jjrounds = new int[713];
      this.jjstateSet = new int[1426];
      this.jjimage = new StringBuilder();
      this.image = this.jjimage;
      this.ReInit(stream);
      this.SwitchTo(lexState);
   }

   public void ReInit(SimpleCharStream stream) {
      this.jjmatchedPos = this.jjnewStateCnt = 0;
      this.curLexState = this.defaultLexState;
      this.input_stream = stream;
      this.ReInitRounds();
   }

   private void ReInitRounds() {
      this.jjround = -2147483647;

      for(int i = 713; i-- > 0; this.jjrounds[i] = Integer.MIN_VALUE) {
      }

   }

   public void ReInit(SimpleCharStream stream, int lexState) {
      this.ReInit(stream);
      this.SwitchTo(lexState);
   }

   public void SwitchTo(int lexState) {
      if (lexState < 8 && lexState >= 0) {
         this.curLexState = lexState;
      } else {
         throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
      }
   }
}
