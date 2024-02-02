/*     */ package org.antlr.v4.runtime.atn;
/*     */ 
/*     */ import java.io.InvalidClassException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import org.antlr.v4.runtime.misc.IntegerList;
/*     */ import org.antlr.v4.runtime.misc.Interval;
/*     */ import org.antlr.v4.runtime.misc.IntervalSet;
/*     */ import org.antlr.v4.runtime.misc.Utils;
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
/*     */ public class ATNSerializer
/*     */ {
/*     */   public ATN atn;
/*     */   private List<String> tokenNames;
/*     */   
/*     */   public ATNSerializer(ATN atn) {
/*  52 */     assert atn.grammarType != null;
/*  53 */     this.atn = atn;
/*     */   }
/*     */   
/*     */   public ATNSerializer(ATN atn, List<String> tokenNames) {
/*  57 */     assert atn.grammarType != null;
/*  58 */     this.atn = atn;
/*  59 */     this.tokenNames = tokenNames;
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
/*     */   public IntegerList serialize() {
/*  84 */     IntegerList data = new IntegerList();
/*  85 */     data.add(ATNDeserializer.SERIALIZED_VERSION);
/*  86 */     serializeUUID(data, ATNDeserializer.SERIALIZED_UUID);
/*     */ 
/*     */     
/*  89 */     data.add(this.atn.grammarType.ordinal());
/*  90 */     data.add(this.atn.maxTokenType);
/*  91 */     int nedges = 0;
/*     */     
/*  93 */     Map<IntervalSet, Integer> setIndices = new HashMap<IntervalSet, Integer>();
/*  94 */     List<IntervalSet> sets = new ArrayList<IntervalSet>();
/*     */ 
/*     */     
/*  97 */     IntegerList nonGreedyStates = new IntegerList();
/*  98 */     IntegerList precedenceStates = new IntegerList();
/*  99 */     data.add(this.atn.states.size());
/* 100 */     for (ATNState s : this.atn.states) {
/* 101 */       if (s == null) {
/* 102 */         data.add(0);
/*     */         
/*     */         continue;
/*     */       } 
/* 106 */       int stateType = s.getStateType();
/* 107 */       if (s instanceof DecisionState && ((DecisionState)s).nonGreedy) {
/* 108 */         nonGreedyStates.add(s.stateNumber);
/*     */       }
/*     */       
/* 111 */       if (s instanceof RuleStartState && ((RuleStartState)s).isLeftRecursiveRule) {
/* 112 */         precedenceStates.add(s.stateNumber);
/*     */       }
/*     */       
/* 115 */       data.add(stateType);
/*     */       
/* 117 */       if (s.ruleIndex == -1) {
/* 118 */         data.add(65535);
/*     */       } else {
/*     */         
/* 121 */         data.add(s.ruleIndex);
/*     */       } 
/*     */       
/* 124 */       if (s.getStateType() == 12) {
/* 125 */         data.add(((LoopEndState)s).loopBackState.stateNumber);
/*     */       }
/* 127 */       else if (s instanceof BlockStartState) {
/* 128 */         data.add(((BlockStartState)s).endState.stateNumber);
/*     */       } 
/*     */       
/* 131 */       if (s.getStateType() != 7)
/*     */       {
/* 133 */         nedges += s.getNumberOfTransitions();
/*     */       }
/*     */       
/* 136 */       for (int k = 0; k < s.getNumberOfTransitions(); k++) {
/* 137 */         Transition t = s.transition(k);
/* 138 */         int edgeType = ((Integer)Transition.serializationTypes.get(t.getClass())).intValue();
/* 139 */         if (edgeType == 7 || edgeType == 8) {
/* 140 */           SetTransition st = (SetTransition)t;
/* 141 */           if (!setIndices.containsKey(st.set)) {
/* 142 */             sets.add(st.set);
/* 143 */             setIndices.put(st.set, Integer.valueOf(sets.size() - 1));
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 150 */     data.add(nonGreedyStates.size()); int i;
/* 151 */     for (i = 0; i < nonGreedyStates.size(); i++) {
/* 152 */       data.add(nonGreedyStates.get(i));
/*     */     }
/*     */ 
/*     */     
/* 156 */     data.add(precedenceStates.size());
/* 157 */     for (i = 0; i < precedenceStates.size(); i++) {
/* 158 */       data.add(precedenceStates.get(i));
/*     */     }
/*     */     
/* 161 */     int nrules = this.atn.ruleToStartState.length;
/* 162 */     data.add(nrules);
/* 163 */     for (int r = 0; r < nrules; r++) {
/* 164 */       ATNState ruleStartState = this.atn.ruleToStartState[r];
/* 165 */       data.add(ruleStartState.stateNumber);
/* 166 */       if (this.atn.grammarType == ATNType.LEXER) {
/* 167 */         if (this.atn.ruleToTokenType[r] == -1) {
/* 168 */           data.add(65535);
/*     */         } else {
/*     */           
/* 171 */           data.add(this.atn.ruleToTokenType[r]);
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 176 */     int nmodes = this.atn.modeToStartState.size();
/* 177 */     data.add(nmodes);
/* 178 */     if (nmodes > 0) {
/* 179 */       for (ATNState modeStartState : this.atn.modeToStartState) {
/* 180 */         data.add(modeStartState.stateNumber);
/*     */       }
/*     */     }
/*     */     
/* 184 */     int nsets = sets.size();
/* 185 */     data.add(nsets);
/* 186 */     for (IntervalSet set : sets) {
/* 187 */       boolean containsEof = set.contains(-1);
/* 188 */       if (containsEof && ((Interval)set.getIntervals().get(0)).b == -1) {
/* 189 */         data.add(set.getIntervals().size() - 1);
/*     */       } else {
/*     */         
/* 192 */         data.add(set.getIntervals().size());
/*     */       } 
/*     */       
/* 195 */       data.add(containsEof ? 1 : 0);
/* 196 */       for (Interval I : set.getIntervals()) {
/* 197 */         if (I.a == -1) {
/* 198 */           if (I.b == -1) {
/*     */             continue;
/*     */           }
/*     */           
/* 202 */           data.add(0);
/*     */         }
/*     */         else {
/*     */           
/* 206 */           data.add(I.a);
/*     */         } 
/*     */         
/* 209 */         data.add(I.b);
/*     */       } 
/*     */     } 
/*     */     
/* 213 */     data.add(nedges);
/* 214 */     for (ATNState s : this.atn.states) {
/* 215 */       if (s == null) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */       
/* 220 */       if (s.getStateType() == 7) {
/*     */         continue;
/*     */       }
/*     */       
/* 224 */       for (int k = 0; k < s.getNumberOfTransitions(); k++) {
/* 225 */         PrecedencePredicateTransition ppt; PredicateTransition pt; ActionTransition at; Transition t = s.transition(k);
/*     */         
/* 227 */         if (this.atn.states.get(t.target.stateNumber) == null) {
/* 228 */           throw new IllegalStateException("Cannot serialize a transition to a removed state.");
/*     */         }
/*     */         
/* 231 */         int src = s.stateNumber;
/* 232 */         int trg = t.target.stateNumber;
/* 233 */         int edgeType = ((Integer)Transition.serializationTypes.get(t.getClass())).intValue();
/* 234 */         int arg1 = 0;
/* 235 */         int arg2 = 0;
/* 236 */         int arg3 = 0;
/* 237 */         switch (edgeType) {
/*     */           case 3:
/* 239 */             trg = ((RuleTransition)t).followState.stateNumber;
/* 240 */             arg1 = ((RuleTransition)t).target.stateNumber;
/* 241 */             arg2 = ((RuleTransition)t).ruleIndex;
/* 242 */             arg3 = ((RuleTransition)t).precedence;
/*     */             break;
/*     */           case 10:
/* 245 */             ppt = (PrecedencePredicateTransition)t;
/* 246 */             arg1 = ppt.precedence;
/*     */             break;
/*     */           case 4:
/* 249 */             pt = (PredicateTransition)t;
/* 250 */             arg1 = pt.ruleIndex;
/* 251 */             arg2 = pt.predIndex;
/* 252 */             arg3 = pt.isCtxDependent ? 1 : 0;
/*     */             break;
/*     */           case 2:
/* 255 */             arg1 = ((RangeTransition)t).from;
/* 256 */             arg2 = ((RangeTransition)t).to;
/* 257 */             if (arg1 == -1) {
/* 258 */               arg1 = 0;
/* 259 */               arg3 = 1;
/*     */             } 
/*     */             break;
/*     */           
/*     */           case 5:
/* 264 */             arg1 = ((AtomTransition)t).label;
/* 265 */             if (arg1 == -1) {
/* 266 */               arg1 = 0;
/* 267 */               arg3 = 1;
/*     */             } 
/*     */             break;
/*     */           
/*     */           case 6:
/* 272 */             at = (ActionTransition)t;
/* 273 */             arg1 = at.ruleIndex;
/* 274 */             arg2 = at.actionIndex;
/* 275 */             if (arg2 == -1) {
/* 276 */               arg2 = 65535;
/*     */             }
/*     */             
/* 279 */             arg3 = at.isCtxDependent ? 1 : 0;
/*     */             break;
/*     */           case 7:
/* 282 */             arg1 = ((Integer)setIndices.get(((SetTransition)t).set)).intValue();
/*     */             break;
/*     */           case 8:
/* 285 */             arg1 = ((Integer)setIndices.get(((SetTransition)t).set)).intValue();
/*     */             break;
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 291 */         data.add(src);
/* 292 */         data.add(trg);
/* 293 */         data.add(edgeType);
/* 294 */         data.add(arg1);
/* 295 */         data.add(arg2);
/* 296 */         data.add(arg3);
/*     */       } 
/*     */     } 
/*     */     
/* 300 */     int ndecisions = this.atn.decisionToState.size();
/* 301 */     data.add(ndecisions);
/* 302 */     for (DecisionState decStartState : this.atn.decisionToState) {
/* 303 */       data.add(decStartState.stateNumber);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 309 */     if (this.atn.grammarType == ATNType.LEXER) {
/* 310 */       data.add(this.atn.lexerActions.length);
/* 311 */       for (LexerAction action : this.atn.lexerActions) {
/* 312 */         int channel, ruleIndex, actionIndex, mode, type; String message; data.add(action.getActionType().ordinal());
/* 313 */         switch (action.getActionType()) {
/*     */           case CHANNEL:
/* 315 */             channel = ((LexerChannelAction)action).getChannel();
/* 316 */             data.add((channel != -1) ? channel : 65535);
/* 317 */             data.add(0);
/*     */             break;
/*     */           
/*     */           case CUSTOM:
/* 321 */             ruleIndex = ((LexerCustomAction)action).getRuleIndex();
/* 322 */             actionIndex = ((LexerCustomAction)action).getActionIndex();
/* 323 */             data.add((ruleIndex != -1) ? ruleIndex : 65535);
/* 324 */             data.add((actionIndex != -1) ? actionIndex : 65535);
/*     */             break;
/*     */           
/*     */           case MODE:
/* 328 */             mode = ((LexerModeAction)action).getMode();
/* 329 */             data.add((mode != -1) ? mode : 65535);
/* 330 */             data.add(0);
/*     */             break;
/*     */           
/*     */           case MORE:
/* 334 */             data.add(0);
/* 335 */             data.add(0);
/*     */             break;
/*     */           
/*     */           case POP_MODE:
/* 339 */             data.add(0);
/* 340 */             data.add(0);
/*     */             break;
/*     */           
/*     */           case PUSH_MODE:
/* 344 */             mode = ((LexerPushModeAction)action).getMode();
/* 345 */             data.add((mode != -1) ? mode : 65535);
/* 346 */             data.add(0);
/*     */             break;
/*     */           
/*     */           case SKIP:
/* 350 */             data.add(0);
/* 351 */             data.add(0);
/*     */             break;
/*     */           
/*     */           case TYPE:
/* 355 */             type = ((LexerTypeAction)action).getType();
/* 356 */             data.add((type != -1) ? type : 65535);
/* 357 */             data.add(0);
/*     */             break;
/*     */           
/*     */           default:
/* 361 */             message = String.format(Locale.getDefault(), "The specified lexer action type %s is not valid.", new Object[] { action.getActionType() });
/* 362 */             throw new IllegalArgumentException(message);
/*     */         } 
/*     */ 
/*     */       
/*     */       } 
/*     */     } 
/* 368 */     for (int j = 1; j < data.size(); j++) {
/* 369 */       if (data.get(j) < 0 || data.get(j) > 65535) {
/* 370 */         throw new UnsupportedOperationException("Serialized ATN data element out of range.");
/*     */       }
/*     */       
/* 373 */       int value = data.get(j) + 2 & 0xFFFF;
/* 374 */       data.set(j, value);
/*     */     } 
/*     */     
/* 377 */     return data;
/*     */   }
/*     */   
/*     */   public String decode(char[] data) {
/* 381 */     data = (char[])data.clone();
/*     */     
/* 383 */     for (int i = 1; i < data.length; i++) {
/* 384 */       data[i] = (char)(data[i] - 2);
/*     */     }
/*     */     
/* 387 */     StringBuilder buf = new StringBuilder();
/* 388 */     int p = 0;
/* 389 */     int version = ATNDeserializer.toInt(data[p++]);
/* 390 */     if (version != ATNDeserializer.SERIALIZED_VERSION) {
/* 391 */       String reason = String.format("Could not deserialize ATN with version %d (expected %d).", new Object[] { Integer.valueOf(version), Integer.valueOf(ATNDeserializer.SERIALIZED_VERSION) });
/* 392 */       throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
/*     */     } 
/*     */     
/* 395 */     UUID uuid = ATNDeserializer.toUUID(data, p);
/* 396 */     p += 8;
/* 397 */     if (!uuid.equals(ATNDeserializer.SERIALIZED_UUID)) {
/* 398 */       String reason = String.format(Locale.getDefault(), "Could not deserialize ATN with UUID %s (expected %s).", new Object[] { uuid, ATNDeserializer.SERIALIZED_UUID });
/* 399 */       throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
/*     */     } 
/*     */     
/* 402 */     p++;
/* 403 */     int maxType = ATNDeserializer.toInt(data[p++]);
/* 404 */     buf.append("max type ").append(maxType).append("\n");
/* 405 */     int nstates = ATNDeserializer.toInt(data[p++]);
/* 406 */     for (int j = 0; j < nstates; j++) {
/* 407 */       int stype = ATNDeserializer.toInt(data[p++]);
/* 408 */       if (stype != 0) {
/* 409 */         int ruleIndex = ATNDeserializer.toInt(data[p++]);
/* 410 */         if (ruleIndex == 65535) {
/* 411 */           ruleIndex = -1;
/*     */         }
/*     */         
/* 414 */         String arg = "";
/* 415 */         if (stype == 12) {
/* 416 */           int loopBackStateNumber = ATNDeserializer.toInt(data[p++]);
/* 417 */           arg = " " + loopBackStateNumber;
/*     */         }
/* 419 */         else if (stype == 4 || stype == 5 || stype == 3) {
/* 420 */           int endStateNumber = ATNDeserializer.toInt(data[p++]);
/* 421 */           arg = " " + endStateNumber;
/*     */         } 
/* 423 */         buf.append(j).append(":").append(ATNState.serializationNames.get(stype)).append(" ").append(ruleIndex).append(arg).append("\n");
/*     */       } 
/*     */     } 
/*     */     
/* 427 */     int numNonGreedyStates = ATNDeserializer.toInt(data[p++]);
/* 428 */     for (int k = 0; k < numNonGreedyStates; k++) {
/* 429 */       int stateNumber = ATNDeserializer.toInt(data[p++]);
/*     */     }
/* 431 */     int numPrecedenceStates = ATNDeserializer.toInt(data[p++]);
/* 432 */     for (int m = 0; m < numPrecedenceStates; m++) {
/* 433 */       int stateNumber = ATNDeserializer.toInt(data[p++]);
/*     */     }
/* 435 */     int nrules = ATNDeserializer.toInt(data[p++]);
/* 436 */     for (int n = 0; n < nrules; n++) {
/* 437 */       int s = ATNDeserializer.toInt(data[p++]);
/* 438 */       if (this.atn.grammarType == ATNType.LEXER) {
/* 439 */         int arg1 = ATNDeserializer.toInt(data[p++]);
/* 440 */         buf.append("rule ").append(n).append(":").append(s).append(" ").append(arg1).append('\n');
/*     */       } else {
/*     */         
/* 443 */         buf.append("rule ").append(n).append(":").append(s).append('\n');
/*     */       } 
/*     */     } 
/* 446 */     int nmodes = ATNDeserializer.toInt(data[p++]);
/* 447 */     for (int i1 = 0; i1 < nmodes; i1++) {
/* 448 */       int s = ATNDeserializer.toInt(data[p++]);
/* 449 */       buf.append("mode ").append(i1).append(":").append(s).append('\n');
/*     */     } 
/* 451 */     int nsets = ATNDeserializer.toInt(data[p++]);
/* 452 */     for (int i2 = 0; i2 < nsets; i2++) {
/* 453 */       int nintervals = ATNDeserializer.toInt(data[p++]);
/* 454 */       buf.append(i2).append(":");
/* 455 */       boolean containsEof = (data[p++] != '\000');
/* 456 */       if (containsEof) {
/* 457 */         buf.append(getTokenName(-1));
/*     */       }
/*     */       
/* 460 */       for (int i5 = 0; i5 < nintervals; i5++) {
/* 461 */         if (containsEof || i5 > 0) {
/* 462 */           buf.append(", ");
/*     */         }
/*     */         
/* 465 */         buf.append(getTokenName(ATNDeserializer.toInt(data[p]))).append("..").append(getTokenName(ATNDeserializer.toInt(data[p + 1])));
/* 466 */         p += 2;
/*     */       } 
/* 468 */       buf.append("\n");
/*     */     } 
/* 470 */     int nedges = ATNDeserializer.toInt(data[p++]);
/* 471 */     for (int i3 = 0; i3 < nedges; i3++) {
/* 472 */       int src = ATNDeserializer.toInt(data[p]);
/* 473 */       int trg = ATNDeserializer.toInt(data[p + 1]);
/* 474 */       int ttype = ATNDeserializer.toInt(data[p + 2]);
/* 475 */       int arg1 = ATNDeserializer.toInt(data[p + 3]);
/* 476 */       int arg2 = ATNDeserializer.toInt(data[p + 4]);
/* 477 */       int arg3 = ATNDeserializer.toInt(data[p + 5]);
/* 478 */       buf.append(src).append("->").append(trg).append(" ").append(Transition.serializationNames.get(ttype)).append(" ").append(arg1).append(",").append(arg2).append(",").append(arg3).append("\n");
/*     */ 
/*     */ 
/*     */       
/* 482 */       p += 6;
/*     */     } 
/* 484 */     int ndecisions = ATNDeserializer.toInt(data[p++]);
/* 485 */     for (int i4 = 0; i4 < ndecisions; i4++) {
/* 486 */       int s = ATNDeserializer.toInt(data[p++]);
/* 487 */       buf.append(i4).append(":").append(s).append("\n");
/*     */     } 
/* 489 */     if (this.atn.grammarType == ATNType.LEXER) {
/* 490 */       int lexerActionCount = ATNDeserializer.toInt(data[p++]);
/* 491 */       for (int i5 = 0; i5 < lexerActionCount; i5++) {
/* 492 */         LexerActionType actionType = LexerActionType.values()[ATNDeserializer.toInt(data[p++])];
/* 493 */         int data1 = ATNDeserializer.toInt(data[p++]);
/* 494 */         int data2 = ATNDeserializer.toInt(data[p++]);
/*     */       } 
/*     */     } 
/* 497 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public String getTokenName(int t) {
/* 501 */     if (t == -1) return "EOF";
/*     */     
/* 503 */     if (this.atn.grammarType == ATNType.LEXER && t >= 0 && t <= 65535) {
/*     */ 
/*     */       
/* 506 */       switch (t) {
/*     */         case 10:
/* 508 */           return "'\\n'";
/*     */         case 13:
/* 510 */           return "'\\r'";
/*     */         case 9:
/* 512 */           return "'\\t'";
/*     */         case 8:
/* 514 */           return "'\\b'";
/*     */         case 12:
/* 516 */           return "'\\f'";
/*     */         case 92:
/* 518 */           return "'\\\\'";
/*     */         case 39:
/* 520 */           return "'\\''";
/*     */       } 
/* 522 */       if (Character.UnicodeBlock.of((char)t) == Character.UnicodeBlock.BASIC_LATIN && !Character.isISOControl((char)t))
/*     */       {
/* 524 */         return '\'' + Character.toString((char)t) + '\'';
/*     */       }
/*     */ 
/*     */       
/* 528 */       String hex = Integer.toHexString(t | 0x10000).toUpperCase().substring(1, 5);
/* 529 */       String unicodeStr = "'\\u" + hex + "'";
/* 530 */       return unicodeStr;
/*     */     } 
/*     */ 
/*     */     
/* 534 */     if (this.tokenNames != null && t >= 0 && t < this.tokenNames.size()) {
/* 535 */       return this.tokenNames.get(t);
/*     */     }
/*     */     
/* 538 */     return String.valueOf(t);
/*     */   }
/*     */ 
/*     */   
/*     */   public static String getSerializedAsString(ATN atn) {
/* 543 */     return new String(getSerializedAsChars(atn));
/*     */   }
/*     */   
/*     */   public static IntegerList getSerialized(ATN atn) {
/* 547 */     return (new ATNSerializer(atn)).serialize();
/*     */   }
/*     */   
/*     */   public static char[] getSerializedAsChars(ATN atn) {
/* 551 */     return Utils.toCharArray(getSerialized(atn));
/*     */   }
/*     */   
/*     */   public static String getDecoded(ATN atn, List<String> tokenNames) {
/* 555 */     IntegerList serialized = getSerialized(atn);
/* 556 */     char[] data = Utils.toCharArray(serialized);
/* 557 */     return (new ATNSerializer(atn, tokenNames)).decode(data);
/*     */   }
/*     */   
/*     */   private void serializeUUID(IntegerList data, UUID uuid) {
/* 561 */     serializeLong(data, uuid.getLeastSignificantBits());
/* 562 */     serializeLong(data, uuid.getMostSignificantBits());
/*     */   }
/*     */   
/*     */   private void serializeLong(IntegerList data, long value) {
/* 566 */     serializeInt(data, (int)value);
/* 567 */     serializeInt(data, (int)(value >> 32L));
/*     */   }
/*     */   
/*     */   private void serializeInt(IntegerList data, int value) {
/* 571 */     data.add((char)value);
/* 572 */     data.add((char)(value >> 16));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\ATNSerializer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */