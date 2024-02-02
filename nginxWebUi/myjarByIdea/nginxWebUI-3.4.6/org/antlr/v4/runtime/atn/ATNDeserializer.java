package org.antlr.v4.runtime.atn;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Pair;

public class ATNDeserializer {
   public static final int SERIALIZED_VERSION = 3;
   private static final UUID BASE_SERIALIZED_UUID = UUID.fromString("33761B2D-78BB-4A43-8B0B-4F5BEE8AACF3");
   private static final UUID ADDED_PRECEDENCE_TRANSITIONS = UUID.fromString("1DA0C57D-6C06-438A-9B27-10BCB3CE0F61");
   private static final UUID ADDED_LEXER_ACTIONS = UUID.fromString("AADB8D7E-AEEF-4415-AD2B-8204D6CF042E");
   private static final List<UUID> SUPPORTED_UUIDS = new ArrayList();
   public static final UUID SERIALIZED_UUID;
   private final ATNDeserializationOptions deserializationOptions;

   public ATNDeserializer() {
      this(ATNDeserializationOptions.getDefaultOptions());
   }

   public ATNDeserializer(ATNDeserializationOptions deserializationOptions) {
      if (deserializationOptions == null) {
         deserializationOptions = ATNDeserializationOptions.getDefaultOptions();
      }

      this.deserializationOptions = deserializationOptions;
   }

   protected boolean isFeatureSupported(UUID feature, UUID actualUuid) {
      int featureIndex = SUPPORTED_UUIDS.indexOf(feature);
      if (featureIndex < 0) {
         return false;
      } else {
         return SUPPORTED_UUIDS.indexOf(actualUuid) >= featureIndex;
      }
   }

   public ATN deserialize(char[] data) {
      data = (char[])data.clone();

      int p;
      for(p = 1; p < data.length; ++p) {
         data[p] = (char)(data[p] - 2);
      }

      int p = 0;
      p = p + 1;
      int version = toInt(data[p]);
      if (version != SERIALIZED_VERSION) {
         String reason = String.format(Locale.getDefault(), "Could not deserialize ATN with version %d (expected %d).", version, SERIALIZED_VERSION);
         throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
      } else {
         UUID uuid = toUUID(data, p);
         p += 8;
         if (!SUPPORTED_UUIDS.contains(uuid)) {
            String reason = String.format(Locale.getDefault(), "Could not deserialize ATN with UUID %s (expected %s or a legacy UUID).", uuid, SERIALIZED_UUID);
            throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
         } else {
            boolean supportsPrecedencePredicates = this.isFeatureSupported(ADDED_PRECEDENCE_TRANSITIONS, uuid);
            boolean supportsLexerActions = this.isFeatureSupported(ADDED_LEXER_ACTIONS, uuid);
            ATNType grammarType = ATNType.values()[toInt(data[p++])];
            int maxTokenType = toInt(data[p++]);
            ATN atn = new ATN(grammarType, maxTokenType);
            List<Pair<LoopEndState, Integer>> loopBackStateNumbers = new ArrayList();
            List<Pair<BlockStartState, Integer>> endStateNumbers = new ArrayList();
            int nstates = toInt(data[p++]);

            int numNonGreedyStates;
            int nrules;
            int nmodes;
            ATNState state;
            int nsets;
            for(numNonGreedyStates = 0; numNonGreedyStates < nstates; ++numNonGreedyStates) {
               nrules = toInt(data[p++]);
               if (nrules == 0) {
                  atn.addState((ATNState)null);
               } else {
                  nmodes = toInt(data[p++]);
                  if (nmodes == 65535) {
                     nmodes = -1;
                  }

                  state = this.stateFactory(nrules, nmodes);
                  if (nrules == 12) {
                     nsets = toInt(data[p++]);
                     loopBackStateNumbers.add(new Pair((LoopEndState)state, nsets));
                  } else if (state instanceof BlockStartState) {
                     nsets = toInt(data[p++]);
                     endStateNumbers.add(new Pair((BlockStartState)state, nsets));
                  }

                  atn.addState(state);
               }
            }

            Iterator i$;
            Pair pair;
            for(i$ = loopBackStateNumbers.iterator(); i$.hasNext(); ((LoopEndState)pair.a).loopBackState = (ATNState)atn.states.get((Integer)pair.b)) {
               pair = (Pair)i$.next();
            }

            for(i$ = endStateNumbers.iterator(); i$.hasNext(); ((BlockStartState)pair.a).endState = (BlockEndState)atn.states.get((Integer)pair.b)) {
               pair = (Pair)i$.next();
            }

            numNonGreedyStates = toInt(data[p++]);

            for(nrules = 0; nrules < numNonGreedyStates; ++nrules) {
               nmodes = toInt(data[p++]);
               ((DecisionState)atn.states.get(nmodes)).nonGreedy = true;
            }

            int i;
            if (supportsPrecedencePredicates) {
               nrules = toInt(data[p++]);

               for(nmodes = 0; nmodes < nrules; ++nmodes) {
                  i = toInt(data[p++]);
                  ((RuleStartState)atn.states.get(i)).isLeftRecursiveRule = true;
               }
            }

            nrules = toInt(data[p++]);
            if (atn.grammarType == ATNType.LEXER) {
               atn.ruleToTokenType = new int[nrules];
            }

            atn.ruleToStartState = new RuleStartState[nrules];

            int nedges;
            for(nmodes = 0; nmodes < nrules; ++nmodes) {
               i = toInt(data[p++]);
               RuleStartState startState = (RuleStartState)atn.states.get(i);
               atn.ruleToStartState[nmodes] = startState;
               if (atn.grammarType == ATNType.LEXER) {
                  nedges = toInt(data[p++]);
                  if (nedges == 65535) {
                     nedges = -1;
                  }

                  atn.ruleToTokenType[nmodes] = nedges;
                  if (!this.isFeatureSupported(ADDED_LEXER_ACTIONS, uuid)) {
                     toInt(data[p++]);
                  }
               }
            }

            atn.ruleToStopState = new RuleStopState[nrules];
            Iterator i$ = atn.states.iterator();

            while(i$.hasNext()) {
               state = (ATNState)i$.next();
               if (state instanceof RuleStopState) {
                  RuleStopState stopState = (RuleStopState)state;
                  atn.ruleToStopState[state.ruleIndex] = stopState;
                  atn.ruleToStartState[state.ruleIndex].stopState = stopState;
               }
            }

            nmodes = toInt(data[p++]);

            for(i = 0; i < nmodes; ++i) {
               nsets = toInt(data[p++]);
               atn.modeToStartState.add((TokensStartState)atn.states.get(nsets));
            }

            List<IntervalSet> sets = new ArrayList();
            nsets = toInt(data[p++]);

            int ndecisions;
            int data1;
            for(nedges = 0; nedges < nsets; ++nedges) {
               ndecisions = toInt(data[p]);
               ++p;
               IntervalSet set = new IntervalSet(new int[0]);
               sets.add(set);
               boolean containsEof = toInt(data[p++]) != 0;
               if (containsEof) {
                  set.add(-1);
               }

               for(data1 = 0; data1 < ndecisions; ++data1) {
                  set.add(toInt(data[p]), toInt(data[p + 1]));
                  p += 2;
               }
            }

            nedges = toInt(data[p++]);

            int data2;
            int outermostPrecedenceReturn;
            int ruleIndex;
            ATNState maybeLoopEndState;
            int i;
            int s;
            for(ndecisions = 0; ndecisions < nedges; ++ndecisions) {
               i = toInt(data[p]);
               s = toInt(data[p + 1]);
               data1 = toInt(data[p + 2]);
               data2 = toInt(data[p + 3]);
               outermostPrecedenceReturn = toInt(data[p + 4]);
               ruleIndex = toInt(data[p + 5]);
               Transition trans = this.edgeFactory(atn, data1, i, s, data2, outermostPrecedenceReturn, ruleIndex, sets);
               maybeLoopEndState = (ATNState)atn.states.get(i);
               maybeLoopEndState.addTransition(trans);
               p += 6;
            }

            Iterator i$ = atn.states.iterator();

            ATNState state;
            while(i$.hasNext()) {
               state = (ATNState)i$.next();

               for(s = 0; s < state.getNumberOfTransitions(); ++s) {
                  Transition t = state.transition(s);
                  if (t instanceof RuleTransition) {
                     RuleTransition ruleTransition = (RuleTransition)t;
                     outermostPrecedenceReturn = -1;
                     if (atn.ruleToStartState[ruleTransition.target.ruleIndex].isLeftRecursiveRule && ruleTransition.precedence == 0) {
                        outermostPrecedenceReturn = ruleTransition.target.ruleIndex;
                     }

                     EpsilonTransition returnTransition = new EpsilonTransition(ruleTransition.followState, outermostPrecedenceReturn);
                     atn.ruleToStopState[ruleTransition.target.ruleIndex].addTransition(returnTransition);
                  }
               }
            }

            i$ = atn.states.iterator();

            while(i$.hasNext()) {
               state = (ATNState)i$.next();
               if (state instanceof BlockStartState) {
                  if (((BlockStartState)state).endState == null) {
                     throw new IllegalStateException();
                  }

                  if (((BlockStartState)state).endState.startState != null) {
                     throw new IllegalStateException();
                  }

                  ((BlockStartState)state).endState.startState = (BlockStartState)state;
               }

               ATNState target;
               if (state instanceof PlusLoopbackState) {
                  PlusLoopbackState loopbackState = (PlusLoopbackState)state;

                  for(data1 = 0; data1 < loopbackState.getNumberOfTransitions(); ++data1) {
                     target = loopbackState.transition(data1).target;
                     if (target instanceof PlusBlockStartState) {
                        ((PlusBlockStartState)target).loopBackState = loopbackState;
                     }
                  }
               } else if (state instanceof StarLoopbackState) {
                  StarLoopbackState loopbackState = (StarLoopbackState)state;

                  for(data1 = 0; data1 < loopbackState.getNumberOfTransitions(); ++data1) {
                     target = loopbackState.transition(data1).target;
                     if (target instanceof StarLoopEntryState) {
                        ((StarLoopEntryState)target).loopBackState = loopbackState;
                     }
                  }
               }
            }

            ndecisions = toInt(data[p++]);

            for(i = 1; i <= ndecisions; ++i) {
               s = toInt(data[p++]);
               DecisionState decState = (DecisionState)atn.states.get(s);
               atn.decisionToState.add(decState);
               decState.decision = i - 1;
            }

            Transition excludeTransition;
            if (atn.grammarType == ATNType.LEXER) {
               if (supportsLexerActions) {
                  atn.lexerActions = new LexerAction[toInt(data[p++])];

                  for(i = 0; i < atn.lexerActions.length; ++i) {
                     LexerActionType actionType = LexerActionType.values()[toInt(data[p++])];
                     data1 = toInt(data[p++]);
                     if (data1 == 65535) {
                        data1 = -1;
                     }

                     data2 = toInt(data[p++]);
                     if (data2 == 65535) {
                        data2 = -1;
                     }

                     LexerAction lexerAction = this.lexerActionFactory(actionType, data1, data2);
                     atn.lexerActions[i] = lexerAction;
                  }
               } else {
                  List<LexerAction> legacyLexerActions = new ArrayList();
                  Iterator i$ = atn.states.iterator();

                  while(i$.hasNext()) {
                     ATNState state = (ATNState)i$.next();

                     for(data2 = 0; data2 < state.getNumberOfTransitions(); ++data2) {
                        excludeTransition = state.transition(data2);
                        if (excludeTransition instanceof ActionTransition) {
                           ruleIndex = ((ActionTransition)excludeTransition).ruleIndex;
                           int actionIndex = ((ActionTransition)excludeTransition).actionIndex;
                           LexerCustomAction lexerAction = new LexerCustomAction(ruleIndex, actionIndex);
                           state.setTransition(data2, new ActionTransition(excludeTransition.target, ruleIndex, legacyLexerActions.size(), false));
                           legacyLexerActions.add(lexerAction);
                        }
                     }
                  }

                  atn.lexerActions = (LexerAction[])legacyLexerActions.toArray(new LexerAction[legacyLexerActions.size()]);
               }
            }

            this.markPrecedenceDecisions(atn);
            if (this.deserializationOptions.isVerifyATN()) {
               this.verifyATN(atn);
            }

            if (this.deserializationOptions.isGenerateRuleBypassTransitions() && atn.grammarType == ATNType.PARSER) {
               atn.ruleToTokenType = new int[atn.ruleToStartState.length];

               for(i = 0; i < atn.ruleToStartState.length; ++i) {
                  atn.ruleToTokenType[i] = atn.maxTokenType + i + 1;
               }

               for(i = 0; i < atn.ruleToStartState.length; ++i) {
                  BasicBlockStartState bypassStart = new BasicBlockStartState();
                  bypassStart.ruleIndex = i;
                  atn.addState(bypassStart);
                  BlockEndState bypassStop = new BlockEndState();
                  bypassStop.ruleIndex = i;
                  atn.addState(bypassStop);
                  bypassStart.endState = bypassStop;
                  atn.defineDecisionState(bypassStart);
                  bypassStop.startState = bypassStart;
                  excludeTransition = null;
                  Object endState;
                  Iterator i$;
                  ATNState state;
                  if (!atn.ruleToStartState[i].isLeftRecursiveRule) {
                     endState = atn.ruleToStopState[i];
                  } else {
                     endState = null;
                     i$ = atn.states.iterator();

                     while(i$.hasNext()) {
                        state = (ATNState)i$.next();
                        if (state.ruleIndex == i && state instanceof StarLoopEntryState) {
                           maybeLoopEndState = state.transition(state.getNumberOfTransitions() - 1).target;
                           if (maybeLoopEndState instanceof LoopEndState && maybeLoopEndState.epsilonOnlyTransitions && maybeLoopEndState.transition(0).target instanceof RuleStopState) {
                              endState = state;
                              break;
                           }
                        }
                     }

                     if (endState == null) {
                        throw new UnsupportedOperationException("Couldn't identify final state of the precedence rule prefix section.");
                     }

                     excludeTransition = ((StarLoopEntryState)endState).loopBackState.transition(0);
                  }

                  i$ = atn.states.iterator();

                  while(i$.hasNext()) {
                     state = (ATNState)i$.next();
                     Iterator i$ = state.transitions.iterator();

                     while(i$.hasNext()) {
                        Transition transition = (Transition)i$.next();
                        if (transition != excludeTransition && transition.target == endState) {
                           transition.target = bypassStop;
                        }
                     }
                  }

                  while(atn.ruleToStartState[i].getNumberOfTransitions() > 0) {
                     Transition transition = atn.ruleToStartState[i].removeTransition(atn.ruleToStartState[i].getNumberOfTransitions() - 1);
                     bypassStart.addTransition(transition);
                  }

                  atn.ruleToStartState[i].addTransition(new EpsilonTransition(bypassStart));
                  bypassStop.addTransition(new EpsilonTransition((ATNState)endState));
                  ATNState matchState = new BasicState();
                  atn.addState(matchState);
                  matchState.addTransition(new AtomTransition(bypassStop, atn.ruleToTokenType[i]));
                  bypassStart.addTransition(new EpsilonTransition(matchState));
               }

               if (this.deserializationOptions.isVerifyATN()) {
                  this.verifyATN(atn);
               }
            }

            return atn;
         }
      }
   }

   protected void markPrecedenceDecisions(ATN atn) {
      Iterator i$ = atn.states.iterator();

      while(i$.hasNext()) {
         ATNState state = (ATNState)i$.next();
         if (state instanceof StarLoopEntryState && atn.ruleToStartState[state.ruleIndex].isLeftRecursiveRule) {
            ATNState maybeLoopEndState = state.transition(state.getNumberOfTransitions() - 1).target;
            if (maybeLoopEndState instanceof LoopEndState && maybeLoopEndState.epsilonOnlyTransitions && maybeLoopEndState.transition(0).target instanceof RuleStopState) {
               ((StarLoopEntryState)state).isPrecedenceDecision = true;
            }
         }
      }

   }

   protected void verifyATN(ATN atn) {
      Iterator i$ = atn.states.iterator();

      while(true) {
         while(true) {
            ATNState state;
            do {
               if (!i$.hasNext()) {
                  return;
               }

               state = (ATNState)i$.next();
            } while(state == null);

            this.checkCondition(state.onlyHasEpsilonTransitions() || state.getNumberOfTransitions() <= 1);
            if (state instanceof PlusBlockStartState) {
               this.checkCondition(((PlusBlockStartState)state).loopBackState != null);
            }

            if (state instanceof StarLoopEntryState) {
               StarLoopEntryState starLoopEntryState = (StarLoopEntryState)state;
               this.checkCondition(starLoopEntryState.loopBackState != null);
               this.checkCondition(starLoopEntryState.getNumberOfTransitions() == 2);
               if (starLoopEntryState.transition(0).target instanceof StarBlockStartState) {
                  this.checkCondition(starLoopEntryState.transition(1).target instanceof LoopEndState);
                  this.checkCondition(!starLoopEntryState.nonGreedy);
               } else {
                  if (!(starLoopEntryState.transition(0).target instanceof LoopEndState)) {
                     throw new IllegalStateException();
                  }

                  this.checkCondition(starLoopEntryState.transition(1).target instanceof StarBlockStartState);
                  this.checkCondition(starLoopEntryState.nonGreedy);
               }
            }

            if (state instanceof StarLoopbackState) {
               this.checkCondition(state.getNumberOfTransitions() == 1);
               this.checkCondition(state.transition(0).target instanceof StarLoopEntryState);
            }

            if (state instanceof LoopEndState) {
               this.checkCondition(((LoopEndState)state).loopBackState != null);
            }

            if (state instanceof RuleStartState) {
               this.checkCondition(((RuleStartState)state).stopState != null);
            }

            if (state instanceof BlockStartState) {
               this.checkCondition(((BlockStartState)state).endState != null);
            }

            if (state instanceof BlockEndState) {
               this.checkCondition(((BlockEndState)state).startState != null);
            }

            if (state instanceof DecisionState) {
               DecisionState decisionState = (DecisionState)state;
               this.checkCondition(decisionState.getNumberOfTransitions() <= 1 || decisionState.decision >= 0);
            } else {
               this.checkCondition(state.getNumberOfTransitions() <= 1 || state instanceof RuleStopState);
            }
         }
      }
   }

   protected void checkCondition(boolean condition) {
      this.checkCondition(condition, (String)null);
   }

   protected void checkCondition(boolean condition, String message) {
      if (!condition) {
         throw new IllegalStateException(message);
      }
   }

   protected static int toInt(char c) {
      return c;
   }

   protected static int toInt32(char[] data, int offset) {
      return data[offset] | data[offset + 1] << 16;
   }

   protected static long toLong(char[] data, int offset) {
      long lowOrder = (long)toInt32(data, offset) & 4294967295L;
      return lowOrder | (long)toInt32(data, offset + 2) << 32;
   }

   protected static UUID toUUID(char[] data, int offset) {
      long leastSigBits = toLong(data, offset);
      long mostSigBits = toLong(data, offset + 4);
      return new UUID(mostSigBits, leastSigBits);
   }

   protected Transition edgeFactory(ATN atn, int type, int src, int trg, int arg1, int arg2, int arg3, List<IntervalSet> sets) {
      ATNState target = (ATNState)atn.states.get(trg);
      switch (type) {
         case 1:
            return new EpsilonTransition(target);
         case 2:
            if (arg3 != 0) {
               return new RangeTransition(target, -1, arg2);
            }

            return new RangeTransition(target, arg1, arg2);
         case 3:
            RuleTransition rt = new RuleTransition((RuleStartState)atn.states.get(arg1), arg2, arg3, target);
            return rt;
         case 4:
            PredicateTransition pt = new PredicateTransition(target, arg1, arg2, arg3 != 0);
            return pt;
         case 5:
            if (arg3 != 0) {
               return new AtomTransition(target, -1);
            }

            return new AtomTransition(target, arg1);
         case 6:
            ActionTransition a = new ActionTransition(target, arg1, arg2, arg3 != 0);
            return a;
         case 7:
            return new SetTransition(target, (IntervalSet)sets.get(arg1));
         case 8:
            return new NotSetTransition(target, (IntervalSet)sets.get(arg1));
         case 9:
            return new WildcardTransition(target);
         case 10:
            return new PrecedencePredicateTransition(target, arg1);
         default:
            throw new IllegalArgumentException("The specified transition type is not valid.");
      }
   }

   protected ATNState stateFactory(int type, int ruleIndex) {
      Object s;
      switch (type) {
         case 0:
            return null;
         case 1:
            s = new BasicState();
            break;
         case 2:
            s = new RuleStartState();
            break;
         case 3:
            s = new BasicBlockStartState();
            break;
         case 4:
            s = new PlusBlockStartState();
            break;
         case 5:
            s = new StarBlockStartState();
            break;
         case 6:
            s = new TokensStartState();
            break;
         case 7:
            s = new RuleStopState();
            break;
         case 8:
            s = new BlockEndState();
            break;
         case 9:
            s = new StarLoopbackState();
            break;
         case 10:
            s = new StarLoopEntryState();
            break;
         case 11:
            s = new PlusLoopbackState();
            break;
         case 12:
            s = new LoopEndState();
            break;
         default:
            String message = String.format(Locale.getDefault(), "The specified state type %d is not valid.", type);
            throw new IllegalArgumentException(message);
      }

      ((ATNState)s).ruleIndex = ruleIndex;
      return (ATNState)s;
   }

   protected LexerAction lexerActionFactory(LexerActionType type, int data1, int data2) {
      switch (type) {
         case CHANNEL:
            return new LexerChannelAction(data1);
         case CUSTOM:
            return new LexerCustomAction(data1, data2);
         case MODE:
            return new LexerModeAction(data1);
         case MORE:
            return LexerMoreAction.INSTANCE;
         case POP_MODE:
            return LexerPopModeAction.INSTANCE;
         case PUSH_MODE:
            return new LexerPushModeAction(data1);
         case SKIP:
            return LexerSkipAction.INSTANCE;
         case TYPE:
            return new LexerTypeAction(data1);
         default:
            String message = String.format(Locale.getDefault(), "The specified lexer action type %d is not valid.", type);
            throw new IllegalArgumentException(message);
      }
   }

   static {
      SUPPORTED_UUIDS.add(BASE_SERIALIZED_UUID);
      SUPPORTED_UUIDS.add(ADDED_PRECEDENCE_TRANSITIONS);
      SUPPORTED_UUIDS.add(ADDED_LEXER_ACTIONS);
      SERIALIZED_UUID = ADDED_LEXER_ACTIONS;
   }
}
