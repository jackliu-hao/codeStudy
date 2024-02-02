package com.github.odiszapc.nginxparser.javacc;

import java.io.IOException;
import java.io.PrintStream;

public class NginxConfigParserTokenManager implements NginxConfigParserConstants {
   public PrintStream debugStream;
   static final long[] jjbitVec0 = new long[]{0L, 0L, -1L, -1L};
   static final int[] jjnextStates = new int[]{7, 8, 10, 7, 8, 12, 10, 9, 11, 13, 16, 17};
   public static final String[] jjstrLiteralImages = new String[]{"", null, null, null, null, "(", ")", "{", "}", ";", "if", null, null, null, null, null, null, null, null};
   int curLexState;
   int defaultLexState;
   int jjnewStateCnt;
   int jjround;
   int jjmatchedPos;
   int jjmatchedKind;
   public static final String[] lexStateNames = new String[]{"DEFAULT"};
   static final long[] jjtoToken = new long[]{131041L};
   static final long[] jjtoSkip = new long[]{30L};
   protected SimpleCharStream input_stream;
   private final int[] jjrounds;
   private final int[] jjstateSet;
   protected char curChar;

   public void setDebugStream(PrintStream ds) {
      this.debugStream = ds;
   }

   private final int jjStopStringLiteralDfa_0(int pos, long active0) {
      switch (pos) {
         case 0:
            if ((active0 & 1024L) != 0L) {
               this.jjmatchedKind = 13;
               return 5;
            } else if ((active0 & 32L) != 0L) {
               return 20;
            } else {
               if ((active0 & 64L) != 0L) {
                  return 5;
               }

               return -1;
            }
         default:
            return -1;
      }
   }

   private final int jjStartNfa_0(int pos, long active0) {
      return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0), pos + 1);
   }

   private int jjStopAtPos(int pos, int kind) {
      this.jjmatchedKind = kind;
      this.jjmatchedPos = pos;
      return pos + 1;
   }

   private int jjMoveStringLiteralDfa0_0() {
      switch (this.curChar) {
         case '(':
            return this.jjStartNfaWithStates_0(0, 5, 20);
         case ')':
            return this.jjStartNfaWithStates_0(0, 6, 5);
         case ';':
            return this.jjStopAtPos(0, 9);
         case 'i':
            return this.jjMoveStringLiteralDfa1_0(1024L);
         case '{':
            return this.jjStopAtPos(0, 7);
         case '}':
            return this.jjStopAtPos(0, 8);
         default:
            return this.jjMoveNfa_0(0, 0);
      }
   }

   private int jjMoveStringLiteralDfa1_0(long active0) {
      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var4) {
         this.jjStopStringLiteralDfa_0(0, active0);
         return 1;
      }

      switch (this.curChar) {
         case 'f':
            if ((active0 & 1024L) != 0L) {
               return this.jjStartNfaWithStates_0(1, 10, 5);
            }
         default:
            return this.jjStartNfa_0(0, active0);
      }
   }

   private int jjStartNfaWithStates_0(int pos, int kind, int state) {
      this.jjmatchedKind = kind;
      this.jjmatchedPos = pos;

      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var5) {
         return pos + 1;
      }

      return this.jjMoveNfa_0(state, pos + 1);
   }

   private int jjMoveNfa_0(int startState, int curPos) {
      int startsAt = 0;
      this.jjnewStateCnt = 20;
      int i = 1;
      this.jjstateSet[0] = startState;
      int kind = Integer.MAX_VALUE;

      while(true) {
         if (++this.jjround == Integer.MAX_VALUE) {
            this.ReInitRounds();
         }

         long l;
         if (this.curChar < '@') {
            l = 1L << this.curChar;

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 0:
                     if ((-6341086623437946880L & l) != 0L) {
                        if (kind > 13) {
                           kind = 13;
                        }

                        this.jjCheckNAdd(5);
                     } else if (this.curChar == '#') {
                        if (kind > 16) {
                           kind = 16;
                        }

                        this.jjCheckNAdd(19);
                     } else if (this.curChar == '\'') {
                        this.jjCheckNAddTwoStates(16, 17);
                     } else if (this.curChar == '"') {
                        this.jjCheckNAddStates(0, 2);
                     }

                     if ((287667426198290432L & l) != 0L) {
                        if (kind > 12) {
                           kind = 12;
                        }

                        this.jjCheckNAdd(4);
                     } else if (this.curChar == '(') {
                        this.jjCheckNAdd(1);
                     }
                     break;
                  case 1:
                     if ((-2199023255553L & l) != 0L) {
                        this.jjCheckNAddTwoStates(1, 2);
                     }
                     break;
                  case 2:
                     if (this.curChar == ')' && kind > 11) {
                        kind = 11;
                     }
                     break;
                  case 3:
                     if ((287667426198290432L & l) != 0L) {
                        if (kind > 12) {
                           kind = 12;
                        }

                        this.jjCheckNAdd(4);
                     }
                     break;
                  case 4:
                     if ((287948901175001088L & l) != 0L) {
                        if (kind > 12) {
                           kind = 12;
                        }

                        this.jjCheckNAdd(4);
                     }
                     break;
                  case 5:
                     if ((-6341086623437946880L & l) != 0L) {
                        if (kind > 13) {
                           kind = 13;
                        }

                        this.jjCheckNAdd(5);
                     }
                     break;
                  case 6:
                     if (this.curChar == '"') {
                        this.jjCheckNAddStates(0, 2);
                     }
                     break;
                  case 7:
                     if ((-17179869185L & l) != 0L) {
                        this.jjCheckNAddStates(0, 2);
                     }
                  case 8:
                  default:
                     break;
                  case 9:
                     if ((566935683072L & l) != 0L) {
                        this.jjCheckNAddStates(0, 2);
                     }
                     break;
                  case 10:
                     if (this.curChar == '"' && kind > 14) {
                        kind = 14;
                     }
                     break;
                  case 11:
                     if ((71776119061217280L & l) != 0L) {
                        this.jjCheckNAddStates(3, 6);
                     }
                     break;
                  case 12:
                     if ((71776119061217280L & l) != 0L) {
                        this.jjCheckNAddStates(0, 2);
                     }
                     break;
                  case 13:
                     if ((4222124650659840L & l) != 0L) {
                        this.jjstateSet[this.jjnewStateCnt++] = 14;
                     }
                     break;
                  case 14:
                     if ((71776119061217280L & l) != 0L) {
                        this.jjCheckNAdd(12);
                     }
                     break;
                  case 15:
                     if (this.curChar == '\'') {
                        this.jjCheckNAddTwoStates(16, 17);
                     }
                     break;
                  case 16:
                     if ((-549755813889L & l) != 0L) {
                        this.jjCheckNAddTwoStates(16, 17);
                     }
                     break;
                  case 17:
                     if (this.curChar == '\'' && kind > 15) {
                        kind = 15;
                     }
                     break;
                  case 18:
                     if (this.curChar == '#') {
                        if (kind > 16) {
                           kind = 16;
                        }

                        this.jjCheckNAdd(19);
                     }
                     break;
                  case 19:
                     if ((-9217L & l) != 0L) {
                        if (kind > 16) {
                           kind = 16;
                        }

                        this.jjCheckNAdd(19);
                     }
                     break;
                  case 20:
                     if ((-2199023255553L & l) != 0L) {
                        this.jjCheckNAddTwoStates(1, 2);
                     }

                     if ((-6341086623437946880L & l) != 0L) {
                        if (kind > 13) {
                           kind = 13;
                        }

                        this.jjCheckNAdd(5);
                     }
               }
            } while(i != startsAt);
         } else if (this.curChar < 128) {
            l = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 0:
                  case 5:
                     if ((6341068270371602431L & l) != 0L) {
                        if (kind > 13) {
                           kind = 13;
                        }

                        this.jjCheckNAdd(5);
                     }
                     break;
                  case 1:
                     this.jjCheckNAddTwoStates(1, 2);
                  case 2:
                  case 3:
                  case 4:
                  case 6:
                  case 10:
                  case 11:
                  case 12:
                  case 13:
                  case 14:
                  case 15:
                  case 17:
                  case 18:
                  default:
                     break;
                  case 7:
                     if ((-268435457L & l) != 0L) {
                        this.jjCheckNAddStates(0, 2);
                     }
                     break;
                  case 8:
                     if (this.curChar == '\\') {
                        this.jjAddStates(7, 9);
                     }
                     break;
                  case 9:
                     if ((5700160604602368L & l) != 0L) {
                        this.jjCheckNAddStates(0, 2);
                     }
                     break;
                  case 16:
                     this.jjAddStates(10, 11);
                     break;
                  case 19:
                     if (kind > 16) {
                        kind = 16;
                     }

                     this.jjstateSet[this.jjnewStateCnt++] = 19;
                     break;
                  case 20:
                     this.jjCheckNAddTwoStates(1, 2);
                     if ((6341068270371602431L & l) != 0L) {
                        if (kind > 13) {
                           kind = 13;
                        }

                        this.jjCheckNAdd(5);
                     }
               }
            } while(i != startsAt);
         } else {
            int i2 = (this.curChar & 255) >> 6;
            long l2 = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 1:
                  case 20:
                     if ((jjbitVec0[i2] & l2) != 0L) {
                        this.jjCheckNAddTwoStates(1, 2);
                     }
                     break;
                  case 7:
                     if ((jjbitVec0[i2] & l2) != 0L) {
                        this.jjAddStates(0, 2);
                     }
                     break;
                  case 16:
                     if ((jjbitVec0[i2] & l2) != 0L) {
                        this.jjAddStates(10, 11);
                     }
                     break;
                  case 19:
                     if ((jjbitVec0[i2] & l2) != 0L) {
                        if (kind > 16) {
                           kind = 16;
                        }

                        this.jjstateSet[this.jjnewStateCnt++] = 19;
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
         if ((i = this.jjnewStateCnt) == (startsAt = 20 - (this.jjnewStateCnt = startsAt))) {
            return curPos;
         }

         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var9) {
            return curPos;
         }
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
      int curPos = false;

      while(true) {
         Token matchedToken;
         try {
            this.curChar = this.input_stream.BeginToken();
         } catch (IOException var8) {
            this.jjmatchedKind = 0;
            this.jjmatchedPos = -1;
            matchedToken = this.jjFillToken();
            return matchedToken;
         }

         try {
            this.input_stream.backup(0);

            while(this.curChar <= ' ' && (4294977024L & 1L << this.curChar) != 0L) {
               this.curChar = this.input_stream.BeginToken();
            }
         } catch (IOException var10) {
            continue;
         }

         this.jjmatchedKind = Integer.MAX_VALUE;
         this.jjmatchedPos = 0;
         int curPos = this.jjMoveStringLiteralDfa0_0();
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
               if (this.curChar != '\n' && this.curChar != '\r') {
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
            return matchedToken;
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

   public NginxConfigParserTokenManager(SimpleCharStream stream) {
      this.debugStream = System.out;
      this.curLexState = 0;
      this.defaultLexState = 0;
      this.jjrounds = new int[20];
      this.jjstateSet = new int[40];
      this.input_stream = stream;
   }

   public NginxConfigParserTokenManager(SimpleCharStream stream, int lexState) {
      this.debugStream = System.out;
      this.curLexState = 0;
      this.defaultLexState = 0;
      this.jjrounds = new int[20];
      this.jjstateSet = new int[40];
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

      for(int i = 20; i-- > 0; this.jjrounds[i] = Integer.MIN_VALUE) {
      }

   }

   public void ReInit(SimpleCharStream stream, int lexState) {
      this.ReInit(stream);
      this.SwitchTo(lexState);
   }

   public void SwitchTo(int lexState) {
      if (lexState < 1 && lexState >= 0) {
         this.curLexState = lexState;
      } else {
         throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
      }
   }
}
