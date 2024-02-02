package org.antlr.v4.runtime.atn;

import java.io.InvalidClassException;
import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Utils;

public class ATNSerializer {
   public ATN atn;
   private List<String> tokenNames;

   public ATNSerializer(ATN atn) {
      assert atn.grammarType != null;

      this.atn = atn;
   }

   public ATNSerializer(ATN atn, List<String> tokenNames) {
      assert atn.grammarType != null;

      this.atn = atn;
      this.tokenNames = tokenNames;
   }

   public IntegerList serialize() {
      IntegerList data = new IntegerList();
      data.add(ATNDeserializer.SERIALIZED_VERSION);
      this.serializeUUID(data, ATNDeserializer.SERIALIZED_UUID);
      data.add(this.atn.grammarType.ordinal());
      data.add(this.atn.maxTokenType);
      int nedges = 0;
      Map<IntervalSet, Integer> setIndices = new HashMap();
      List<IntervalSet> sets = new ArrayList();
      IntegerList nonGreedyStates = new IntegerList();
      IntegerList precedenceStates = new IntegerList();
      data.add(this.atn.states.size());
      Iterator i$ = this.atn.states.iterator();

      while(true) {
         int nsets;
         int ndecisions;
         int value;
         while(i$.hasNext()) {
            ATNState s = (ATNState)i$.next();
            if (s == null) {
               data.add(0);
            } else {
               nsets = s.getStateType();
               if (s instanceof DecisionState && ((DecisionState)s).nonGreedy) {
                  nonGreedyStates.add(s.stateNumber);
               }

               if (s instanceof RuleStartState && ((RuleStartState)s).isLeftRecursiveRule) {
                  precedenceStates.add(s.stateNumber);
               }

               data.add(nsets);
               if (s.ruleIndex == -1) {
                  data.add(65535);
               } else {
                  data.add(s.ruleIndex);
               }

               if (s.getStateType() == 12) {
                  data.add(((LoopEndState)s).loopBackState.stateNumber);
               } else if (s instanceof BlockStartState) {
                  data.add(((BlockStartState)s).endState.stateNumber);
               }

               if (s.getStateType() != 7) {
                  nedges += s.getNumberOfTransitions();
               }

               for(ndecisions = 0; ndecisions < s.getNumberOfTransitions(); ++ndecisions) {
                  Transition t = s.transition(ndecisions);
                  value = (Integer)Transition.serializationTypes.get(t.getClass());
                  if (value == 7 || value == 8) {
                     SetTransition st = (SetTransition)t;
                     if (!setIndices.containsKey(st.set)) {
                        sets.add(st.set);
                        setIndices.put(st.set, sets.size() - 1);
                     }
                  }
               }
            }
         }

         data.add(nonGreedyStates.size());

         int nrules;
         for(nrules = 0; nrules < nonGreedyStates.size(); ++nrules) {
            data.add(nonGreedyStates.get(nrules));
         }

         data.add(precedenceStates.size());

         for(nrules = 0; nrules < precedenceStates.size(); ++nrules) {
            data.add(precedenceStates.get(nrules));
         }

         nrules = this.atn.ruleToStartState.length;
         data.add(nrules);

         int r;
         for(r = 0; r < nrules; ++r) {
            ATNState ruleStartState = this.atn.ruleToStartState[r];
            data.add(ruleStartState.stateNumber);
            if (this.atn.grammarType == ATNType.LEXER) {
               if (this.atn.ruleToTokenType[r] == -1) {
                  data.add(65535);
               } else {
                  data.add(this.atn.ruleToTokenType[r]);
               }
            }
         }

         r = this.atn.modeToStartState.size();
         data.add(r);
         if (r > 0) {
            Iterator i$ = this.atn.modeToStartState.iterator();

            while(i$.hasNext()) {
               ATNState modeStartState = (ATNState)i$.next();
               data.add(modeStartState.stateNumber);
            }
         }

         nsets = sets.size();
         data.add(nsets);
         Iterator i$ = sets.iterator();

         label245:
         while(i$.hasNext()) {
            IntervalSet set = (IntervalSet)i$.next();
            boolean containsEof = set.contains(-1);
            if (containsEof && ((Interval)set.getIntervals().get(0)).b == -1) {
               data.add(set.getIntervals().size() - 1);
            } else {
               data.add(set.getIntervals().size());
            }

            data.add(containsEof ? 1 : 0);
            Iterator i$ = set.getIntervals().iterator();

            while(true) {
               Interval I;
               while(true) {
                  if (!i$.hasNext()) {
                     continue label245;
                  }

                  I = (Interval)i$.next();
                  if (I.a == -1) {
                     if (I.b == -1) {
                        continue;
                     }

                     data.add(0);
                     break;
                  }

                  data.add(I.a);
                  break;
               }

               data.add(I.b);
            }
         }

         data.add(nedges);
         i$ = this.atn.states.iterator();

         while(true) {
            int trg;
            int edgeType;
            int arg1;
            int arg2;
            int arg3;
            ATNState s;
            do {
               do {
                  if (!i$.hasNext()) {
                     ndecisions = this.atn.decisionToState.size();
                     data.add(ndecisions);
                     Iterator i$ = this.atn.decisionToState.iterator();

                     while(i$.hasNext()) {
                        DecisionState decStartState = (DecisionState)i$.next();
                        data.add(decStartState.stateNumber);
                     }

                     if (this.atn.grammarType == ATNType.LEXER) {
                        data.add(this.atn.lexerActions.length);
                        LexerAction[] arr$ = this.atn.lexerActions;
                        value = arr$.length;

                        for(int i$ = 0; i$ < value; ++i$) {
                           LexerAction action = arr$[i$];
                           data.add(action.getActionType().ordinal());
                           switch (action.getActionType()) {
                              case CHANNEL:
                                 trg = ((LexerChannelAction)action).getChannel();
                                 data.add(trg != -1 ? trg : '\uffff');
                                 data.add(0);
                                 break;
                              case CUSTOM:
                                 edgeType = ((LexerCustomAction)action).getRuleIndex();
                                 arg1 = ((LexerCustomAction)action).getActionIndex();
                                 data.add(edgeType != -1 ? edgeType : '\uffff');
                                 data.add(arg1 != -1 ? arg1 : '\uffff');
                                 break;
                              case MODE:
                                 arg2 = ((LexerModeAction)action).getMode();
                                 data.add(arg2 != -1 ? arg2 : '\uffff');
                                 data.add(0);
                                 break;
                              case MORE:
                                 data.add(0);
                                 data.add(0);
                                 break;
                              case POP_MODE:
                                 data.add(0);
                                 data.add(0);
                                 break;
                              case PUSH_MODE:
                                 arg2 = ((LexerPushModeAction)action).getMode();
                                 data.add(arg2 != -1 ? arg2 : '\uffff');
                                 data.add(0);
                                 break;
                              case SKIP:
                                 data.add(0);
                                 data.add(0);
                                 break;
                              case TYPE:
                                 arg3 = ((LexerTypeAction)action).getType();
                                 data.add(arg3 != -1 ? arg3 : '\uffff');
                                 data.add(0);
                                 break;
                              default:
                                 String message = String.format(Locale.getDefault(), "The specified lexer action type %s is not valid.", action.getActionType());
                                 throw new IllegalArgumentException(message);
                           }
                        }
                     }

                     for(int i = 1; i < data.size(); ++i) {
                        if (data.get(i) < 0 || data.get(i) > 65535) {
                           throw new UnsupportedOperationException("Serialized ATN data element out of range.");
                        }

                        value = data.get(i) + 2 & '\uffff';
                        data.set(i, value);
                     }

                     return data;
                  }

                  s = (ATNState)i$.next();
               } while(s == null);
            } while(s.getStateType() == 7);

            for(value = 0; value < s.getNumberOfTransitions(); ++value) {
               Transition t = s.transition(value);
               if (this.atn.states.get(t.target.stateNumber) == null) {
                  throw new IllegalStateException("Cannot serialize a transition to a removed state.");
               }

               int src = s.stateNumber;
               trg = t.target.stateNumber;
               edgeType = (Integer)Transition.serializationTypes.get(t.getClass());
               arg1 = 0;
               arg2 = 0;
               arg3 = 0;
               switch (edgeType) {
                  case 2:
                     arg1 = ((RangeTransition)t).from;
                     arg2 = ((RangeTransition)t).to;
                     if (arg1 == -1) {
                        arg1 = 0;
                        arg3 = 1;
                     }
                     break;
                  case 3:
                     trg = ((RuleTransition)t).followState.stateNumber;
                     arg1 = ((RuleTransition)t).target.stateNumber;
                     arg2 = ((RuleTransition)t).ruleIndex;
                     arg3 = ((RuleTransition)t).precedence;
                     break;
                  case 4:
                     PredicateTransition pt = (PredicateTransition)t;
                     arg1 = pt.ruleIndex;
                     arg2 = pt.predIndex;
                     arg3 = pt.isCtxDependent ? 1 : 0;
                     break;
                  case 5:
                     arg1 = ((AtomTransition)t).label;
                     if (arg1 == -1) {
                        arg1 = 0;
                        arg3 = 1;
                     }
                     break;
                  case 6:
                     ActionTransition at = (ActionTransition)t;
                     arg1 = at.ruleIndex;
                     arg2 = at.actionIndex;
                     if (arg2 == -1) {
                        arg2 = 65535;
                     }

                     arg3 = at.isCtxDependent ? 1 : 0;
                     break;
                  case 7:
                     arg1 = (Integer)setIndices.get(((SetTransition)t).set);
                     break;
                  case 8:
                     arg1 = (Integer)setIndices.get(((SetTransition)t).set);
                  case 9:
                  default:
                     break;
                  case 10:
                     PrecedencePredicateTransition ppt = (PrecedencePredicateTransition)t;
                     arg1 = ppt.precedence;
               }

               data.add(src);
               data.add(trg);
               data.add(edgeType);
               data.add(arg1);
               data.add(arg2);
               data.add(arg3);
            }
         }
      }
   }

   public String decode(char[] data) {
      data = (char[])data.clone();

      for(int i = 1; i < data.length; ++i) {
         data[i] = (char)(data[i] - 2);
      }

      StringBuilder buf = new StringBuilder();
      int p = 0;
      int version = ATNDeserializer.toInt(data[p++]);
      if (version != ATNDeserializer.SERIALIZED_VERSION) {
         String reason = String.format("Could not deserialize ATN with version %d (expected %d).", version, ATNDeserializer.SERIALIZED_VERSION);
         throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
      } else {
         UUID uuid = ATNDeserializer.toUUID(data, p);
         p += 8;
         if (!uuid.equals(ATNDeserializer.SERIALIZED_UUID)) {
            String reason = String.format(Locale.getDefault(), "Could not deserialize ATN with UUID %s (expected %s).", uuid, ATNDeserializer.SERIALIZED_UUID);
            throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
         } else {
            ++p;
            int maxType = ATNDeserializer.toInt(data[p++]);
            buf.append("max type ").append(maxType).append("\n");
            int nstates = ATNDeserializer.toInt(data[p++]);

            int i;
            int stype;
            int nrules;
            int nsets;
            for(i = 0; i < nstates; ++i) {
               stype = ATNDeserializer.toInt(data[p++]);
               if (stype != 0) {
                  nrules = ATNDeserializer.toInt(data[p++]);
                  if (nrules == 65535) {
                     nrules = -1;
                  }

                  String arg = "";
                  if (stype == 12) {
                     nsets = ATNDeserializer.toInt(data[p++]);
                     arg = " " + nsets;
                  } else if (stype == 4 || stype == 5 || stype == 3) {
                     nsets = ATNDeserializer.toInt(data[p++]);
                     arg = " " + nsets;
                  }

                  buf.append(i).append(":").append((String)ATNState.serializationNames.get(stype)).append(" ").append(nrules).append(arg).append("\n");
               }
            }

            i = ATNDeserializer.toInt(data[p++]);

            for(stype = 0; stype < i; ++stype) {
               ATNDeserializer.toInt(data[p++]);
            }

            stype = ATNDeserializer.toInt(data[p++]);

            for(nrules = 0; nrules < stype; ++nrules) {
               ATNDeserializer.toInt(data[p++]);
            }

            nrules = ATNDeserializer.toInt(data[p++]);

            int nedges;
            int nmodes;
            for(nmodes = 0; nmodes < nrules; ++nmodes) {
               nsets = ATNDeserializer.toInt(data[p++]);
               if (this.atn.grammarType == ATNType.LEXER) {
                  nedges = ATNDeserializer.toInt(data[p++]);
                  buf.append("rule ").append(nmodes).append(":").append(nsets).append(" ").append(nedges).append('\n');
               } else {
                  buf.append("rule ").append(nmodes).append(":").append(nsets).append('\n');
               }
            }

            nmodes = ATNDeserializer.toInt(data[p++]);

            for(nsets = 0; nsets < nmodes; ++nsets) {
               nedges = ATNDeserializer.toInt(data[p++]);
               buf.append("mode ").append(nsets).append(":").append(nedges).append('\n');
            }

            nsets = ATNDeserializer.toInt(data[p++]);

            int ndecisions;
            int i;
            for(nedges = 0; nedges < nsets; ++nedges) {
               ndecisions = ATNDeserializer.toInt(data[p++]);
               buf.append(nedges).append(":");
               boolean containsEof = data[p++] != 0;
               if (containsEof) {
                  buf.append(this.getTokenName(-1));
               }

               for(i = 0; i < ndecisions; ++i) {
                  if (containsEof || i > 0) {
                     buf.append(", ");
                  }

                  buf.append(this.getTokenName(ATNDeserializer.toInt(data[p]))).append("..").append(this.getTokenName(ATNDeserializer.toInt(data[p + 1])));
                  p += 2;
               }

               buf.append("\n");
            }

            nedges = ATNDeserializer.toInt(data[p++]);

            int lexerActionCount;
            for(ndecisions = 0; ndecisions < nedges; ++ndecisions) {
               lexerActionCount = ATNDeserializer.toInt(data[p]);
               i = ATNDeserializer.toInt(data[p + 1]);
               int ttype = ATNDeserializer.toInt(data[p + 2]);
               int arg1 = ATNDeserializer.toInt(data[p + 3]);
               int arg2 = ATNDeserializer.toInt(data[p + 4]);
               int arg3 = ATNDeserializer.toInt(data[p + 5]);
               buf.append(lexerActionCount).append("->").append(i).append(" ").append((String)Transition.serializationNames.get(ttype)).append(" ").append(arg1).append(",").append(arg2).append(",").append(arg3).append("\n");
               p += 6;
            }

            ndecisions = ATNDeserializer.toInt(data[p++]);

            for(lexerActionCount = 0; lexerActionCount < ndecisions; ++lexerActionCount) {
               i = ATNDeserializer.toInt(data[p++]);
               buf.append(lexerActionCount).append(":").append(i).append("\n");
            }

            if (this.atn.grammarType == ATNType.LEXER) {
               lexerActionCount = ATNDeserializer.toInt(data[p++]);

               for(i = 0; i < lexerActionCount; ++i) {
                  LexerActionType actionType = LexerActionType.values()[ATNDeserializer.toInt(data[p++])];
                  ATNDeserializer.toInt(data[p++]);
                  ATNDeserializer.toInt(data[p++]);
               }
            }

            return buf.toString();
         }
      }
   }

   public String getTokenName(int t) {
      if (t == -1) {
         return "EOF";
      } else if (this.atn.grammarType == ATNType.LEXER && t >= 0 && t <= 65535) {
         switch (t) {
            case 8:
               return "'\\b'";
            case 9:
               return "'\\t'";
            case 10:
               return "'\\n'";
            case 12:
               return "'\\f'";
            case 13:
               return "'\\r'";
            case 39:
               return "'\\''";
            case 92:
               return "'\\\\'";
            default:
               if (UnicodeBlock.of((char)t) == UnicodeBlock.BASIC_LATIN && !Character.isISOControl((char)t)) {
                  return '\'' + Character.toString((char)t) + '\'';
               } else {
                  String hex = Integer.toHexString(t | 65536).toUpperCase().substring(1, 5);
                  String unicodeStr = "'\\u" + hex + "'";
                  return unicodeStr;
               }
         }
      } else {
         return this.tokenNames != null && t >= 0 && t < this.tokenNames.size() ? (String)this.tokenNames.get(t) : String.valueOf(t);
      }
   }

   public static String getSerializedAsString(ATN atn) {
      return new String(getSerializedAsChars(atn));
   }

   public static IntegerList getSerialized(ATN atn) {
      return (new ATNSerializer(atn)).serialize();
   }

   public static char[] getSerializedAsChars(ATN atn) {
      return Utils.toCharArray(getSerialized(atn));
   }

   public static String getDecoded(ATN atn, List<String> tokenNames) {
      IntegerList serialized = getSerialized(atn);
      char[] data = Utils.toCharArray(serialized);
      return (new ATNSerializer(atn, tokenNames)).decode(data);
   }

   private void serializeUUID(IntegerList data, UUID uuid) {
      this.serializeLong(data, uuid.getLeastSignificantBits());
      this.serializeLong(data, uuid.getMostSignificantBits());
   }

   private void serializeLong(IntegerList data, long value) {
      this.serializeInt(data, (int)value);
      this.serializeInt(data, (int)(value >> 32));
   }

   private void serializeInt(IntegerList data, int value) {
      data.add((char)value);
      data.add((char)(value >> 16));
   }
}
