/*      */ package org.apache.commons.codec.language;
/*      */ 
/*      */ import java.util.Locale;
/*      */ import org.apache.commons.codec.EncoderException;
/*      */ import org.apache.commons.codec.StringEncoder;
/*      */ import org.apache.commons.codec.binary.StringUtils;
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
/*      */ public class DoubleMetaphone
/*      */   implements StringEncoder
/*      */ {
/*      */   private static final String VOWELS = "AEIOUY";
/*   47 */   private static final String[] SILENT_START = new String[] { "GN", "KN", "PN", "WR", "PS" };
/*      */   
/*   49 */   private static final String[] L_R_N_M_B_H_F_V_W_SPACE = new String[] { "L", "R", "N", "M", "B", "H", "F", "V", "W", " " };
/*      */   
/*   51 */   private static final String[] ES_EP_EB_EL_EY_IB_IL_IN_IE_EI_ER = new String[] { "ES", "EP", "EB", "EL", "EY", "IB", "IL", "IN", "IE", "EI", "ER" };
/*      */   
/*   53 */   private static final String[] L_T_K_S_N_M_B_Z = new String[] { "L", "T", "K", "S", "N", "M", "B", "Z" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   59 */   private int maxCodeLen = 4;
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
/*      */   public String doubleMetaphone(String value) {
/*   75 */     return doubleMetaphone(value, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String doubleMetaphone(String value, boolean alternate) {
/*   86 */     value = cleanInput(value);
/*   87 */     if (value == null) {
/*   88 */       return null;
/*      */     }
/*      */     
/*   91 */     boolean slavoGermanic = isSlavoGermanic(value);
/*   92 */     int index = isSilentStart(value) ? 1 : 0;
/*      */     
/*   94 */     DoubleMetaphoneResult result = new DoubleMetaphoneResult(getMaxCodeLen());
/*      */     
/*   96 */     while (!result.isComplete() && index <= value.length() - 1) {
/*   97 */       switch (value.charAt(index)) {
/*      */         case 'A':
/*      */         case 'E':
/*      */         case 'I':
/*      */         case 'O':
/*      */         case 'U':
/*      */         case 'Y':
/*  104 */           index = handleAEIOUY(result, index);
/*      */           continue;
/*      */         case 'B':
/*  107 */           result.append('P');
/*  108 */           index = (charAt(value, index + 1) == 'B') ? (index + 2) : (index + 1);
/*      */           continue;
/*      */         
/*      */         case 'Ç':
/*  112 */           result.append('S');
/*  113 */           index++;
/*      */           continue;
/*      */         case 'C':
/*  116 */           index = handleC(value, result, index);
/*      */           continue;
/*      */         case 'D':
/*  119 */           index = handleD(value, result, index);
/*      */           continue;
/*      */         case 'F':
/*  122 */           result.append('F');
/*  123 */           index = (charAt(value, index + 1) == 'F') ? (index + 2) : (index + 1);
/*      */           continue;
/*      */         case 'G':
/*  126 */           index = handleG(value, result, index, slavoGermanic);
/*      */           continue;
/*      */         case 'H':
/*  129 */           index = handleH(value, result, index);
/*      */           continue;
/*      */         case 'J':
/*  132 */           index = handleJ(value, result, index, slavoGermanic);
/*      */           continue;
/*      */         case 'K':
/*  135 */           result.append('K');
/*  136 */           index = (charAt(value, index + 1) == 'K') ? (index + 2) : (index + 1);
/*      */           continue;
/*      */         case 'L':
/*  139 */           index = handleL(value, result, index);
/*      */           continue;
/*      */         case 'M':
/*  142 */           result.append('M');
/*  143 */           index = conditionM0(value, index) ? (index + 2) : (index + 1);
/*      */           continue;
/*      */         case 'N':
/*  146 */           result.append('N');
/*  147 */           index = (charAt(value, index + 1) == 'N') ? (index + 2) : (index + 1);
/*      */           continue;
/*      */         
/*      */         case 'Ñ':
/*  151 */           result.append('N');
/*  152 */           index++;
/*      */           continue;
/*      */         case 'P':
/*  155 */           index = handleP(value, result, index);
/*      */           continue;
/*      */         case 'Q':
/*  158 */           result.append('K');
/*  159 */           index = (charAt(value, index + 1) == 'Q') ? (index + 2) : (index + 1);
/*      */           continue;
/*      */         case 'R':
/*  162 */           index = handleR(value, result, index, slavoGermanic);
/*      */           continue;
/*      */         case 'S':
/*  165 */           index = handleS(value, result, index, slavoGermanic);
/*      */           continue;
/*      */         case 'T':
/*  168 */           index = handleT(value, result, index);
/*      */           continue;
/*      */         case 'V':
/*  171 */           result.append('F');
/*  172 */           index = (charAt(value, index + 1) == 'V') ? (index + 2) : (index + 1);
/*      */           continue;
/*      */         case 'W':
/*  175 */           index = handleW(value, result, index);
/*      */           continue;
/*      */         case 'X':
/*  178 */           index = handleX(value, result, index);
/*      */           continue;
/*      */         case 'Z':
/*  181 */           index = handleZ(value, result, index, slavoGermanic);
/*      */           continue;
/*      */       } 
/*  184 */       index++;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  189 */     return alternate ? result.getAlternate() : result.getPrimary();
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
/*      */   public Object encode(Object obj) throws EncoderException {
/*  202 */     if (!(obj instanceof String)) {
/*  203 */       throw new EncoderException("DoubleMetaphone encode parameter is not of type String");
/*      */     }
/*  205 */     return doubleMetaphone((String)obj);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String encode(String value) {
/*  216 */     return doubleMetaphone(value);
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
/*      */   public boolean isDoubleMetaphoneEqual(String value1, String value2) {
/*  230 */     return isDoubleMetaphoneEqual(value1, value2, false);
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
/*      */   public boolean isDoubleMetaphoneEqual(String value1, String value2, boolean alternate) {
/*  244 */     return StringUtils.equals(doubleMetaphone(value1, alternate), doubleMetaphone(value2, alternate));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxCodeLen() {
/*  252 */     return this.maxCodeLen;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMaxCodeLen(int maxCodeLen) {
/*  260 */     this.maxCodeLen = maxCodeLen;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int handleAEIOUY(DoubleMetaphoneResult result, int index) {
/*  269 */     if (index == 0) {
/*  270 */       result.append('A');
/*      */     }
/*  272 */     return index + 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int handleC(String value, DoubleMetaphoneResult result, int index) {
/*  279 */     if (conditionC0(value, index))
/*  280 */     { result.append('K');
/*  281 */       index += 2; }
/*  282 */     else if (index == 0 && contains(value, index, 6, new String[] { "CAESAR" }))
/*  283 */     { result.append('S');
/*  284 */       index += 2; }
/*  285 */     else if (contains(value, index, 2, new String[] { "CH" }))
/*  286 */     { index = handleCH(value, result, index); }
/*  287 */     else if (contains(value, index, 2, new String[] { "CZ"
/*  288 */         }) && !contains(value, index - 2, 4, new String[] { "WICZ" }))
/*      */     
/*  290 */     { result.append('S', 'X');
/*  291 */       index += 2; }
/*  292 */     else if (contains(value, index + 1, 3, new String[] { "CIA" }))
/*      */     
/*  294 */     { result.append('X');
/*  295 */       index += 3; }
/*  296 */     else { if (contains(value, index, 2, new String[] { "CC" }) && (index != 1 || 
/*  297 */         charAt(value, 0) != 'M'))
/*      */       {
/*  299 */         return handleCC(value, result, index); } 
/*  300 */       if (contains(value, index, 2, new String[] { "CK", "CG", "CQ" })) {
/*  301 */         result.append('K');
/*  302 */         index += 2;
/*  303 */       } else if (contains(value, index, 2, new String[] { "CI", "CE", "CY" })) {
/*      */         
/*  305 */         if (contains(value, index, 3, new String[] { "CIO", "CIE", "CIA" })) {
/*  306 */           result.append('S', 'X');
/*      */         } else {
/*  308 */           result.append('S');
/*      */         } 
/*  310 */         index += 2;
/*      */       } else {
/*  312 */         result.append('K');
/*  313 */         if (contains(value, index + 1, 2, new String[] { " C", " Q", " G" })) {
/*      */           
/*  315 */           index += 3;
/*  316 */         } else if (contains(value, index + 1, 1, new String[] { "C", "K", "Q"
/*  317 */             }) && !contains(value, index + 1, 2, new String[] { "CE", "CI" })) {
/*  318 */           index += 2;
/*      */         } else {
/*  320 */           index++;
/*      */         } 
/*      */       }  }
/*      */     
/*  324 */     return index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int handleCC(String value, DoubleMetaphoneResult result, int index) {
/*  331 */     if (contains(value, index + 2, 1, new String[] { "I", "E", "H"
/*  332 */         }) && !contains(value, index + 2, 2, new String[] { "HU" })) {
/*      */       
/*  334 */       if ((index == 1 && charAt(value, index - 1) == 'A') || 
/*  335 */         contains(value, index - 1, 5, new String[] { "UCCEE", "UCCES" })) {
/*      */         
/*  337 */         result.append("KS");
/*      */       } else {
/*      */         
/*  340 */         result.append('X');
/*      */       } 
/*  342 */       index += 3;
/*      */     } else {
/*  344 */       result.append('K');
/*  345 */       index += 2;
/*      */     } 
/*      */     
/*  348 */     return index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int handleCH(String value, DoubleMetaphoneResult result, int index) {
/*  355 */     if (index > 0 && contains(value, index, 4, new String[] { "CHAE" })) {
/*  356 */       result.append('K', 'X');
/*  357 */       return index + 2;
/*  358 */     }  if (conditionCH0(value, index)) {
/*      */       
/*  360 */       result.append('K');
/*  361 */       return index + 2;
/*  362 */     }  if (conditionCH1(value, index)) {
/*      */       
/*  364 */       result.append('K');
/*  365 */       return index + 2;
/*      */     } 
/*  367 */     if (index > 0) {
/*  368 */       if (contains(value, 0, 2, new String[] { "MC" })) {
/*  369 */         result.append('K');
/*      */       } else {
/*  371 */         result.append('X', 'K');
/*      */       } 
/*      */     } else {
/*  374 */       result.append('X');
/*      */     } 
/*  376 */     return index + 2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int handleD(String value, DoubleMetaphoneResult result, int index) {
/*  384 */     if (contains(value, index, 2, new String[] { "DG" })) {
/*      */       
/*  386 */       if (contains(value, index + 2, 1, new String[] { "I", "E", "Y" })) {
/*  387 */         result.append('J');
/*  388 */         index += 3;
/*      */       } else {
/*      */         
/*  391 */         result.append("TK");
/*  392 */         index += 2;
/*      */       } 
/*  394 */     } else if (contains(value, index, 2, new String[] { "DT", "DD" })) {
/*  395 */       result.append('T');
/*  396 */       index += 2;
/*      */     } else {
/*  398 */       result.append('T');
/*  399 */       index++;
/*      */     } 
/*  401 */     return index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int handleG(String value, DoubleMetaphoneResult result, int index, boolean slavoGermanic) {
/*  409 */     if (charAt(value, index + 1) == 'H') {
/*  410 */       index = handleGH(value, result, index);
/*  411 */     } else if (charAt(value, index + 1) == 'N') {
/*  412 */       if (index == 1 && isVowel(charAt(value, 0)) && !slavoGermanic) {
/*  413 */         result.append("KN", "N");
/*  414 */       } else if (!contains(value, index + 2, 2, new String[] { "EY"
/*  415 */           }) && charAt(value, index + 1) != 'Y' && !slavoGermanic) {
/*  416 */         result.append("N", "KN");
/*      */       } else {
/*  418 */         result.append("KN");
/*      */       } 
/*  420 */       index += 2;
/*  421 */     } else if (contains(value, index + 1, 2, new String[] { "LI" }) && !slavoGermanic) {
/*  422 */       result.append("KL", "L");
/*  423 */       index += 2;
/*  424 */     } else if (index == 0 && (
/*  425 */       charAt(value, index + 1) == 'Y' || 
/*  426 */       contains(value, index + 1, 2, ES_EP_EB_EL_EY_IB_IL_IN_IE_EI_ER))) {
/*      */       
/*  428 */       result.append('K', 'J');
/*  429 */       index += 2;
/*  430 */     } else if ((contains(value, index + 1, 2, new String[] { "ER"
/*  431 */         }) || charAt(value, index + 1) == 'Y') && 
/*  432 */       !contains(value, 0, 6, new String[] { "DANGER", "RANGER", "MANGER"
/*  433 */         }) && !contains(value, index - 1, 1, new String[] { "E", "I"
/*  434 */         }) && !contains(value, index - 1, 3, new String[] { "RGY", "OGY" })) {
/*      */       
/*  436 */       result.append('K', 'J');
/*  437 */       index += 2;
/*  438 */     } else if (contains(value, index + 1, 1, new String[] { "E", "I", "Y"
/*  439 */         }) || contains(value, index - 1, 4, new String[] { "AGGI", "OGGI" })) {
/*      */       
/*  441 */       if (contains(value, 0, 4, new String[] { "VAN ", "VON "
/*  442 */           }) || contains(value, 0, 3, new String[] { "SCH"
/*  443 */           }) || contains(value, index + 1, 2, new String[] { "ET" })) {
/*      */         
/*  445 */         result.append('K');
/*  446 */       } else if (contains(value, index + 1, 3, new String[] { "IER" })) {
/*  447 */         result.append('J');
/*      */       } else {
/*  449 */         result.append('J', 'K');
/*      */       } 
/*  451 */       index += 2;
/*  452 */     } else if (charAt(value, index + 1) == 'G') {
/*  453 */       index += 2;
/*  454 */       result.append('K');
/*      */     } else {
/*  456 */       index++;
/*  457 */       result.append('K');
/*      */     } 
/*  459 */     return index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int handleGH(String value, DoubleMetaphoneResult result, int index) {
/*  466 */     if (index > 0 && !isVowel(charAt(value, index - 1))) {
/*  467 */       result.append('K');
/*  468 */       index += 2;
/*  469 */     } else if (index == 0) {
/*  470 */       if (charAt(value, index + 2) == 'I') {
/*  471 */         result.append('J');
/*      */       } else {
/*  473 */         result.append('K');
/*      */       } 
/*  475 */       index += 2;
/*  476 */     } else if ((index > 1 && contains(value, index - 2, 1, new String[] { "B", "H", "D" })) || (index > 2 && 
/*  477 */       contains(value, index - 3, 1, new String[] { "B", "H", "D" })) || (index > 3 && 
/*  478 */       contains(value, index - 4, 1, new String[] { "B", "H" }))) {
/*      */       
/*  480 */       index += 2;
/*      */     } else {
/*  482 */       if (index > 2 && charAt(value, index - 1) == 'U' && 
/*  483 */         contains(value, index - 3, 1, new String[] { "C", "G", "L", "R", "T" })) {
/*      */         
/*  485 */         result.append('F');
/*  486 */       } else if (index > 0 && charAt(value, index - 1) != 'I') {
/*  487 */         result.append('K');
/*      */       } 
/*  489 */       index += 2;
/*      */     } 
/*  491 */     return index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int handleH(String value, DoubleMetaphoneResult result, int index) {
/*  499 */     if ((index == 0 || isVowel(charAt(value, index - 1))) && 
/*  500 */       isVowel(charAt(value, index + 1))) {
/*  501 */       result.append('H');
/*  502 */       index += 2;
/*      */     } else {
/*      */       
/*  505 */       index++;
/*      */     } 
/*  507 */     return index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int handleJ(String value, DoubleMetaphoneResult result, int index, boolean slavoGermanic) {
/*  515 */     if (contains(value, index, 4, new String[] { "JOSE" }) || contains(value, 0, 4, new String[] { "SAN " })) {
/*      */       
/*  517 */       if ((index == 0 && charAt(value, index + 4) == ' ') || value
/*  518 */         .length() == 4 || contains(value, 0, 4, new String[] { "SAN " })) {
/*  519 */         result.append('H');
/*      */       } else {
/*  521 */         result.append('J', 'H');
/*      */       } 
/*  523 */       index++;
/*      */     } else {
/*  525 */       if (index == 0 && !contains(value, index, 4, new String[] { "JOSE" })) {
/*  526 */         result.append('J', 'A');
/*  527 */       } else if (isVowel(charAt(value, index - 1)) && !slavoGermanic && (
/*  528 */         charAt(value, index + 1) == 'A' || charAt(value, index + 1) == 'O')) {
/*  529 */         result.append('J', 'H');
/*  530 */       } else if (index == value.length() - 1) {
/*  531 */         result.append('J', ' ');
/*  532 */       } else if (!contains(value, index + 1, 1, L_T_K_S_N_M_B_Z) && 
/*  533 */         !contains(value, index - 1, 1, new String[] { "S", "K", "L" })) {
/*  534 */         result.append('J');
/*      */       } 
/*      */       
/*  537 */       if (charAt(value, index + 1) == 'J') {
/*  538 */         index += 2;
/*      */       } else {
/*  540 */         index++;
/*      */       } 
/*      */     } 
/*  543 */     return index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int handleL(String value, DoubleMetaphoneResult result, int index) {
/*  550 */     if (charAt(value, index + 1) == 'L') {
/*  551 */       if (conditionL0(value, index)) {
/*  552 */         result.appendPrimary('L');
/*      */       } else {
/*  554 */         result.append('L');
/*      */       } 
/*  556 */       index += 2;
/*      */     } else {
/*  558 */       index++;
/*  559 */       result.append('L');
/*      */     } 
/*  561 */     return index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int handleP(String value, DoubleMetaphoneResult result, int index) {
/*  568 */     if (charAt(value, index + 1) == 'H') {
/*  569 */       result.append('F');
/*  570 */       index += 2;
/*      */     } else {
/*  572 */       result.append('P');
/*  573 */       index = contains(value, index + 1, 1, new String[] { "P", "B" }) ? (index + 2) : (index + 1);
/*      */     } 
/*  575 */     return index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int handleR(String value, DoubleMetaphoneResult result, int index, boolean slavoGermanic) {
/*  583 */     if (index == value.length() - 1 && !slavoGermanic && 
/*  584 */       contains(value, index - 2, 2, new String[] { "IE"
/*  585 */         }) && !contains(value, index - 4, 2, new String[] { "ME", "MA" })) {
/*  586 */       result.appendAlternate('R');
/*      */     } else {
/*  588 */       result.append('R');
/*      */     } 
/*  590 */     return (charAt(value, index + 1) == 'R') ? (index + 2) : (index + 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int handleS(String value, DoubleMetaphoneResult result, int index, boolean slavoGermanic) {
/*  598 */     if (contains(value, index - 1, 3, new String[] { "ISL", "YSL" })) {
/*      */       
/*  600 */       index++;
/*  601 */     } else if (index == 0 && contains(value, index, 5, new String[] { "SUGAR" })) {
/*      */       
/*  603 */       result.append('X', 'S');
/*  604 */       index++;
/*  605 */     } else if (contains(value, index, 2, new String[] { "SH" })) {
/*  606 */       if (contains(value, index + 1, 4, new String[] { "HEIM", "HOEK", "HOLM", "HOLZ" })) {
/*      */         
/*  608 */         result.append('S');
/*      */       } else {
/*  610 */         result.append('X');
/*      */       } 
/*  612 */       index += 2;
/*  613 */     } else if (contains(value, index, 3, new String[] { "SIO", "SIA" }) || contains(value, index, 4, new String[] { "SIAN" })) {
/*      */       
/*  615 */       if (slavoGermanic) {
/*  616 */         result.append('S');
/*      */       } else {
/*  618 */         result.append('S', 'X');
/*      */       } 
/*  620 */       index += 3;
/*  621 */     } else if ((index == 0 && contains(value, index + 1, 1, new String[] { "M", "N", "L", "W"
/*  622 */         })) || contains(value, index + 1, 1, new String[] { "Z" })) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  627 */       result.append('S', 'X');
/*  628 */       index = contains(value, index + 1, 1, new String[] { "Z" }) ? (index + 2) : (index + 1);
/*  629 */     } else if (contains(value, index, 2, new String[] { "SC" })) {
/*  630 */       index = handleSC(value, result, index);
/*      */     } else {
/*  632 */       if (index == value.length() - 1 && contains(value, index - 2, 2, new String[] { "AI", "OI" })) {
/*      */         
/*  634 */         result.appendAlternate('S');
/*      */       } else {
/*  636 */         result.append('S');
/*      */       } 
/*  638 */       index = contains(value, index + 1, 1, new String[] { "S", "Z" }) ? (index + 2) : (index + 1);
/*      */     } 
/*  640 */     return index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int handleSC(String value, DoubleMetaphoneResult result, int index) {
/*  647 */     if (charAt(value, index + 2) == 'H') {
/*      */       
/*  649 */       if (contains(value, index + 3, 2, new String[] { "OO", "ER", "EN", "UY", "ED", "EM" })) {
/*      */         
/*  651 */         if (contains(value, index + 3, 2, new String[] { "ER", "EN" })) {
/*      */           
/*  653 */           result.append("X", "SK");
/*      */         } else {
/*  655 */           result.append("SK");
/*      */         }
/*      */       
/*  658 */       } else if (index == 0 && !isVowel(charAt(value, 3)) && charAt(value, 3) != 'W') {
/*  659 */         result.append('X', 'S');
/*      */       } else {
/*  661 */         result.append('X');
/*      */       }
/*      */     
/*  664 */     } else if (contains(value, index + 2, 1, new String[] { "I", "E", "Y" })) {
/*  665 */       result.append('S');
/*      */     } else {
/*  667 */       result.append("SK");
/*      */     } 
/*  669 */     return index + 3;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int handleT(String value, DoubleMetaphoneResult result, int index) {
/*  676 */     if (contains(value, index, 4, new String[] { "TION" })) {
/*  677 */       result.append('X');
/*  678 */       index += 3;
/*  679 */     } else if (contains(value, index, 3, new String[] { "TIA", "TCH" })) {
/*  680 */       result.append('X');
/*  681 */       index += 3;
/*  682 */     } else if (contains(value, index, 2, new String[] { "TH" }) || contains(value, index, 3, new String[] { "TTH" })) {
/*  683 */       if (contains(value, index + 2, 2, new String[] { "OM", "AM"
/*      */           
/*  685 */           }) || contains(value, 0, 4, new String[] { "VAN ", "VON "
/*  686 */           }) || contains(value, 0, 3, new String[] { "SCH" })) {
/*  687 */         result.append('T');
/*      */       } else {
/*  689 */         result.append('0', 'T');
/*      */       } 
/*  691 */       index += 2;
/*      */     } else {
/*  693 */       result.append('T');
/*  694 */       index = contains(value, index + 1, 1, new String[] { "T", "D" }) ? (index + 2) : (index + 1);
/*      */     } 
/*  696 */     return index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int handleW(String value, DoubleMetaphoneResult result, int index) {
/*  703 */     if (contains(value, index, 2, new String[] { "WR" })) {
/*      */       
/*  705 */       result.append('R');
/*  706 */       index += 2;
/*      */     }
/*  708 */     else if (index == 0 && (isVowel(charAt(value, index + 1)) || 
/*  709 */       contains(value, index, 2, new String[] { "WH" }))) {
/*  710 */       if (isVowel(charAt(value, index + 1))) {
/*      */         
/*  712 */         result.append('A', 'F');
/*      */       } else {
/*      */         
/*  715 */         result.append('A');
/*      */       } 
/*  717 */       index++;
/*  718 */     } else if ((index == value.length() - 1 && isVowel(charAt(value, index - 1))) || 
/*  719 */       contains(value, index - 1, 5, new String[] { "EWSKI", "EWSKY", "OWSKI", "OWSKY"
/*  720 */         }) || contains(value, 0, 3, new String[] { "SCH" })) {
/*      */       
/*  722 */       result.appendAlternate('F');
/*  723 */       index++;
/*  724 */     } else if (contains(value, index, 4, new String[] { "WICZ", "WITZ" })) {
/*      */       
/*  726 */       result.append("TS", "FX");
/*  727 */       index += 4;
/*      */     } else {
/*  729 */       index++;
/*      */     } 
/*      */     
/*  732 */     return index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int handleX(String value, DoubleMetaphoneResult result, int index) {
/*  739 */     if (index == 0) {
/*  740 */       result.append('S');
/*  741 */       index++;
/*      */     } else {
/*  743 */       if (index != value.length() - 1 || (
/*  744 */         !contains(value, index - 3, 3, new String[] { "IAU", "EAU"
/*  745 */           }) && !contains(value, index - 2, 2, new String[] { "AU", "OU" })))
/*      */       {
/*  747 */         result.append("KS");
/*      */       }
/*  749 */       index = contains(value, index + 1, 1, new String[] { "C", "X" }) ? (index + 2) : (index + 1);
/*      */     } 
/*  751 */     return index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int handleZ(String value, DoubleMetaphoneResult result, int index, boolean slavoGermanic) {
/*  759 */     if (charAt(value, index + 1) == 'H') {
/*      */       
/*  761 */       result.append('J');
/*  762 */       index += 2;
/*      */     } else {
/*  764 */       if (contains(value, index + 1, 2, new String[] { "ZO", "ZI", "ZA" }) || (slavoGermanic && index > 0 && 
/*  765 */         charAt(value, index - 1) != 'T')) {
/*  766 */         result.append("S", "TS");
/*      */       } else {
/*  768 */         result.append('S');
/*      */       } 
/*  770 */       index = (charAt(value, index + 1) == 'Z') ? (index + 2) : (index + 1);
/*      */     } 
/*  772 */     return index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean conditionC0(String value, int index) {
/*  781 */     if (contains(value, index, 4, new String[] { "CHIA" }))
/*  782 */       return true; 
/*  783 */     if (index <= 1)
/*  784 */       return false; 
/*  785 */     if (isVowel(charAt(value, index - 2)))
/*  786 */       return false; 
/*  787 */     if (!contains(value, index - 1, 3, new String[] { "ACH" })) {
/*  788 */       return false;
/*      */     }
/*  790 */     char c = charAt(value, index + 2);
/*  791 */     return ((c != 'I' && c != 'E') || 
/*  792 */       contains(value, index - 2, 6, new String[] { "BACHER", "MACHER" }));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean conditionCH0(String value, int index) {
/*  800 */     if (index != 0)
/*  801 */       return false; 
/*  802 */     if (!contains(value, index + 1, 5, new String[] { "HARAC", "HARIS"
/*  803 */         }) && !contains(value, index + 1, 3, new String[] { "HOR", "HYM", "HIA", "HEM" }))
/*  804 */       return false; 
/*  805 */     if (contains(value, 0, 5, new String[] { "CHORE" })) {
/*  806 */       return false;
/*      */     }
/*  808 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean conditionCH1(String value, int index) {
/*  816 */     return (contains(value, 0, 4, new String[] { "VAN ", "VON " }) || contains(value, 0, 3, new String[] { "SCH"
/*  817 */         }) || contains(value, index - 2, 6, new String[] { "ORCHES", "ARCHIT", "ORCHID"
/*  818 */         }) || contains(value, index + 2, 1, new String[] { "T", "S"
/*  819 */         }) || ((contains(value, index - 1, 1, new String[] { "A", "O", "U", "E" }) || index == 0) && (
/*  820 */       contains(value, index + 2, 1, L_R_N_M_B_H_F_V_W_SPACE) || index + 1 == value.length() - 1)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean conditionL0(String value, int index) {
/*  827 */     if (index == value.length() - 3 && 
/*  828 */       contains(value, index - 1, 4, new String[] { "ILLO", "ILLA", "ALLE" }))
/*  829 */       return true; 
/*  830 */     if ((contains(value, value.length() - 2, 2, new String[] { "AS", "OS"
/*  831 */         }) || contains(value, value.length() - 1, 1, new String[] { "A", "O"
/*  832 */         })) && contains(value, index - 1, 4, new String[] { "ALLE" })) {
/*  833 */       return true;
/*      */     }
/*  835 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean conditionM0(String value, int index) {
/*  843 */     if (charAt(value, index + 1) == 'M') {
/*  844 */       return true;
/*      */     }
/*  846 */     return (contains(value, index - 1, 3, new String[] { "UMB" }) && (index + 1 == value
/*  847 */       .length() - 1 || contains(value, index + 2, 2, new String[] { "ER" })));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isSlavoGermanic(String value) {
/*  857 */     return (value.indexOf('W') > -1 || value.indexOf('K') > -1 || value
/*  858 */       .indexOf("CZ") > -1 || value.indexOf("WITZ") > -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isVowel(char ch) {
/*  865 */     return ("AEIOUY".indexOf(ch) != -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isSilentStart(String value) {
/*  874 */     boolean result = false;
/*  875 */     for (String element : SILENT_START) {
/*  876 */       if (value.startsWith(element)) {
/*  877 */         result = true;
/*      */         break;
/*      */       } 
/*      */     } 
/*  881 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String cleanInput(String input) {
/*  888 */     if (input == null) {
/*  889 */       return null;
/*      */     }
/*  891 */     input = input.trim();
/*  892 */     if (input.length() == 0) {
/*  893 */       return null;
/*      */     }
/*  895 */     return input.toUpperCase(Locale.ENGLISH);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char charAt(String value, int index) {
/*  904 */     if (index < 0 || index >= value.length()) {
/*  905 */       return Character.MIN_VALUE;
/*      */     }
/*  907 */     return value.charAt(index);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static boolean contains(String value, int start, int length, String... criteria) {
/*  916 */     boolean result = false;
/*  917 */     if (start >= 0 && start + length <= value.length()) {
/*  918 */       String target = value.substring(start, start + length);
/*      */       
/*  920 */       for (String element : criteria) {
/*  921 */         if (target.equals(element)) {
/*  922 */           result = true;
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     } 
/*  927 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public class DoubleMetaphoneResult
/*      */   {
/*  937 */     private final StringBuilder primary = new StringBuilder(DoubleMetaphone.this.getMaxCodeLen());
/*  938 */     private final StringBuilder alternate = new StringBuilder(DoubleMetaphone.this.getMaxCodeLen());
/*      */     private final int maxLength;
/*      */     
/*      */     public DoubleMetaphoneResult(int maxLength) {
/*  942 */       this.maxLength = maxLength;
/*      */     }
/*      */     
/*      */     public void append(char value) {
/*  946 */       appendPrimary(value);
/*  947 */       appendAlternate(value);
/*      */     }
/*      */     
/*      */     public void append(char primary, char alternate) {
/*  951 */       appendPrimary(primary);
/*  952 */       appendAlternate(alternate);
/*      */     }
/*      */     
/*      */     public void appendPrimary(char value) {
/*  956 */       if (this.primary.length() < this.maxLength) {
/*  957 */         this.primary.append(value);
/*      */       }
/*      */     }
/*      */     
/*      */     public void appendAlternate(char value) {
/*  962 */       if (this.alternate.length() < this.maxLength) {
/*  963 */         this.alternate.append(value);
/*      */       }
/*      */     }
/*      */     
/*      */     public void append(String value) {
/*  968 */       appendPrimary(value);
/*  969 */       appendAlternate(value);
/*      */     }
/*      */     
/*      */     public void append(String primary, String alternate) {
/*  973 */       appendPrimary(primary);
/*  974 */       appendAlternate(alternate);
/*      */     }
/*      */     
/*      */     public void appendPrimary(String value) {
/*  978 */       int addChars = this.maxLength - this.primary.length();
/*  979 */       if (value.length() <= addChars) {
/*  980 */         this.primary.append(value);
/*      */       } else {
/*  982 */         this.primary.append(value.substring(0, addChars));
/*      */       } 
/*      */     }
/*      */     
/*      */     public void appendAlternate(String value) {
/*  987 */       int addChars = this.maxLength - this.alternate.length();
/*  988 */       if (value.length() <= addChars) {
/*  989 */         this.alternate.append(value);
/*      */       } else {
/*  991 */         this.alternate.append(value.substring(0, addChars));
/*      */       } 
/*      */     }
/*      */     
/*      */     public String getPrimary() {
/*  996 */       return this.primary.toString();
/*      */     }
/*      */     
/*      */     public String getAlternate() {
/* 1000 */       return this.alternate.toString();
/*      */     }
/*      */     
/*      */     public boolean isComplete() {
/* 1004 */       return (this.primary.length() >= this.maxLength && this.alternate
/* 1005 */         .length() >= this.maxLength);
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\language\DoubleMetaphone.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */