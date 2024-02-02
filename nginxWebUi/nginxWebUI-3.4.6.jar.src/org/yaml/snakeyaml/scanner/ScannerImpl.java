/*      */ package org.yaml.snakeyaml.scanner;
/*      */ 
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.charset.CharacterCodingException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.regex.Pattern;
/*      */ import org.yaml.snakeyaml.DumperOptions;
/*      */ import org.yaml.snakeyaml.comments.CommentType;
/*      */ import org.yaml.snakeyaml.error.Mark;
/*      */ import org.yaml.snakeyaml.error.YAMLException;
/*      */ import org.yaml.snakeyaml.reader.StreamReader;
/*      */ import org.yaml.snakeyaml.tokens.AliasToken;
/*      */ import org.yaml.snakeyaml.tokens.AnchorToken;
/*      */ import org.yaml.snakeyaml.tokens.BlockEndToken;
/*      */ import org.yaml.snakeyaml.tokens.BlockEntryToken;
/*      */ import org.yaml.snakeyaml.tokens.BlockMappingStartToken;
/*      */ import org.yaml.snakeyaml.tokens.BlockSequenceStartToken;
/*      */ import org.yaml.snakeyaml.tokens.CommentToken;
/*      */ import org.yaml.snakeyaml.tokens.DirectiveToken;
/*      */ import org.yaml.snakeyaml.tokens.DocumentEndToken;
/*      */ import org.yaml.snakeyaml.tokens.DocumentStartToken;
/*      */ import org.yaml.snakeyaml.tokens.FlowEntryToken;
/*      */ import org.yaml.snakeyaml.tokens.FlowMappingEndToken;
/*      */ import org.yaml.snakeyaml.tokens.FlowMappingStartToken;
/*      */ import org.yaml.snakeyaml.tokens.FlowSequenceEndToken;
/*      */ import org.yaml.snakeyaml.tokens.FlowSequenceStartToken;
/*      */ import org.yaml.snakeyaml.tokens.KeyToken;
/*      */ import org.yaml.snakeyaml.tokens.ScalarToken;
/*      */ import org.yaml.snakeyaml.tokens.StreamEndToken;
/*      */ import org.yaml.snakeyaml.tokens.StreamStartToken;
/*      */ import org.yaml.snakeyaml.tokens.TagToken;
/*      */ import org.yaml.snakeyaml.tokens.TagTuple;
/*      */ import org.yaml.snakeyaml.tokens.Token;
/*      */ import org.yaml.snakeyaml.tokens.ValueToken;
/*      */ import org.yaml.snakeyaml.util.ArrayStack;
/*      */ import org.yaml.snakeyaml.util.UriEncoder;
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
/*      */ public final class ScannerImpl
/*      */   implements Scanner
/*      */ {
/*   91 */   private static final Pattern NOT_HEXA = Pattern.compile("[^0-9A-Fa-f]");
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
/*  102 */   public static final Map<Character, String> ESCAPE_REPLACEMENTS = new HashMap<>();
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
/*  118 */   public static final Map<Character, Integer> ESCAPE_CODES = new HashMap<>();
/*      */   private final StreamReader reader;
/*      */   
/*      */   static {
/*  122 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('0'), "\000");
/*      */     
/*  124 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('a'), "\007");
/*      */     
/*  126 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('b'), "\b");
/*      */     
/*  128 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('t'), "\t");
/*      */     
/*  130 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('n'), "\n");
/*      */     
/*  132 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('v'), "\013");
/*      */     
/*  134 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('f'), "\f");
/*      */     
/*  136 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('r'), "\r");
/*      */     
/*  138 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('e'), "\033");
/*      */     
/*  140 */     ESCAPE_REPLACEMENTS.put(Character.valueOf(' '), " ");
/*      */     
/*  142 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('"'), "\"");
/*      */     
/*  144 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('\\'), "\\");
/*      */     
/*  146 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('N'), "");
/*      */     
/*  148 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('_'), " ");
/*      */     
/*  150 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('L'), " ");
/*      */     
/*  152 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('P'), " ");
/*      */ 
/*      */     
/*  155 */     ESCAPE_CODES.put(Character.valueOf('x'), Integer.valueOf(2));
/*      */     
/*  157 */     ESCAPE_CODES.put(Character.valueOf('u'), Integer.valueOf(4));
/*      */     
/*  159 */     ESCAPE_CODES.put(Character.valueOf('U'), Integer.valueOf(8));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean done = false;
/*      */ 
/*      */   
/*  167 */   private int flowLevel = 0;
/*      */ 
/*      */   
/*      */   private List<Token> tokens;
/*      */ 
/*      */   
/*  173 */   private int tokensTaken = 0;
/*      */ 
/*      */   
/*  176 */   private int indent = -1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ArrayStack<Integer> indents;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean emitComments;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean allowSimpleKey = true;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Map<Integer, SimpleKey> possibleSimpleKeys;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ScannerImpl(StreamReader reader) {
/*  220 */     this.emitComments = false;
/*  221 */     this.reader = reader;
/*  222 */     this.tokens = new ArrayList<>(100);
/*  223 */     this.indents = new ArrayStack(10);
/*      */     
/*  225 */     this.possibleSimpleKeys = new LinkedHashMap<>();
/*  226 */     fetchStreamStart();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ScannerImpl setEmitComments(boolean emitComments) {
/*  235 */     this.emitComments = emitComments;
/*  236 */     return this;
/*      */   }
/*      */   
/*      */   public boolean isEmitComments() {
/*  240 */     return this.emitComments;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean checkToken(Token.ID... choices) {
/*  247 */     while (needMoreTokens()) {
/*  248 */       fetchMoreTokens();
/*      */     }
/*  250 */     if (!this.tokens.isEmpty()) {
/*  251 */       if (choices.length == 0) {
/*  252 */         return true;
/*      */       }
/*      */ 
/*      */       
/*  256 */       Token.ID first = ((Token)this.tokens.get(0)).getTokenId();
/*  257 */       for (int i = 0; i < choices.length; i++) {
/*  258 */         if (first == choices[i]) {
/*  259 */           return true;
/*      */         }
/*      */       } 
/*      */     } 
/*  263 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Token peekToken() {
/*  270 */     while (needMoreTokens()) {
/*  271 */       fetchMoreTokens();
/*      */     }
/*  273 */     return this.tokens.get(0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Token getToken() {
/*  280 */     this.tokensTaken++;
/*  281 */     return this.tokens.remove(0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean needMoreTokens() {
/*  290 */     if (this.done) {
/*  291 */       return false;
/*      */     }
/*      */     
/*  294 */     if (this.tokens.isEmpty()) {
/*  295 */       return true;
/*      */     }
/*      */ 
/*      */     
/*  299 */     stalePossibleSimpleKeys();
/*  300 */     return (nextPossibleSimpleKey() == this.tokensTaken);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchMoreTokens() {
/*  308 */     scanToNextToken();
/*      */     
/*  310 */     stalePossibleSimpleKeys();
/*      */ 
/*      */     
/*  313 */     unwindIndent(this.reader.getColumn());
/*      */ 
/*      */     
/*  316 */     int c = this.reader.peek();
/*  317 */     switch (c) {
/*      */       
/*      */       case 0:
/*  320 */         fetchStreamEnd();
/*      */         return;
/*      */       
/*      */       case 37:
/*  324 */         if (checkDirective()) {
/*  325 */           fetchDirective();
/*      */           return;
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 45:
/*  331 */         if (checkDocumentStart()) {
/*  332 */           fetchDocumentStart();
/*      */           return;
/*      */         } 
/*  335 */         if (checkBlockEntry()) {
/*  336 */           fetchBlockEntry();
/*      */           return;
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 46:
/*  342 */         if (checkDocumentEnd()) {
/*  343 */           fetchDocumentEnd();
/*      */           return;
/*      */         } 
/*      */         break;
/*      */ 
/*      */       
/*      */       case 91:
/*  350 */         fetchFlowSequenceStart();
/*      */         return;
/*      */       
/*      */       case 123:
/*  354 */         fetchFlowMappingStart();
/*      */         return;
/*      */       
/*      */       case 93:
/*  358 */         fetchFlowSequenceEnd();
/*      */         return;
/*      */       
/*      */       case 125:
/*  362 */         fetchFlowMappingEnd();
/*      */         return;
/*      */       
/*      */       case 44:
/*  366 */         fetchFlowEntry();
/*      */         return;
/*      */ 
/*      */       
/*      */       case 63:
/*  371 */         if (checkKey()) {
/*  372 */           fetchKey();
/*      */           return;
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 58:
/*  378 */         if (checkValue()) {
/*  379 */           fetchValue();
/*      */           return;
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 42:
/*  385 */         fetchAlias();
/*      */         return;
/*      */       
/*      */       case 38:
/*  389 */         fetchAnchor();
/*      */         return;
/*      */       
/*      */       case 33:
/*  393 */         fetchTag();
/*      */         return;
/*      */       
/*      */       case 124:
/*  397 */         if (this.flowLevel == 0) {
/*  398 */           fetchLiteral();
/*      */           return;
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 62:
/*  404 */         if (this.flowLevel == 0) {
/*  405 */           fetchFolded();
/*      */           return;
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 39:
/*  411 */         fetchSingle();
/*      */         return;
/*      */       
/*      */       case 34:
/*  415 */         fetchDouble();
/*      */         return;
/*      */     } 
/*      */     
/*  419 */     if (checkPlain()) {
/*  420 */       fetchPlain();
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  426 */     String chRepresentation = String.valueOf(Character.toChars(c));
/*  427 */     for (Character s : ESCAPE_REPLACEMENTS.keySet()) {
/*  428 */       String v = ESCAPE_REPLACEMENTS.get(s);
/*  429 */       if (v.equals(chRepresentation)) {
/*  430 */         chRepresentation = "\\" + s;
/*      */         break;
/*      */       } 
/*      */     } 
/*  434 */     if (c == 9)
/*  435 */       chRepresentation = chRepresentation + "(TAB)"; 
/*  436 */     String text = String.format("found character '%s' that cannot start any token. (Do not use %s for indentation)", new Object[] { chRepresentation, chRepresentation });
/*      */ 
/*      */     
/*  439 */     throw new ScannerException("while scanning for the next token", null, text, this.reader.getMark());
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
/*      */   private int nextPossibleSimpleKey() {
/*  454 */     if (!this.possibleSimpleKeys.isEmpty()) {
/*  455 */       return ((SimpleKey)this.possibleSimpleKeys.values().iterator().next()).getTokenNumber();
/*      */     }
/*  457 */     return -1;
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
/*      */   private void stalePossibleSimpleKeys() {
/*  471 */     if (!this.possibleSimpleKeys.isEmpty()) {
/*  472 */       Iterator<SimpleKey> iterator = this.possibleSimpleKeys.values().iterator();
/*  473 */       while (iterator.hasNext()) {
/*  474 */         SimpleKey key = iterator.next();
/*  475 */         if (key.getLine() != this.reader.getLine() || this.reader.getIndex() - key.getIndex() > 1024) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  481 */           if (key.isRequired())
/*      */           {
/*      */             
/*  484 */             throw new ScannerException("while scanning a simple key", key.getMark(), "could not find expected ':'", this.reader.getMark());
/*      */           }
/*      */           
/*  487 */           iterator.remove();
/*      */         } 
/*      */       } 
/*      */     } 
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
/*      */   private void savePossibleSimpleKey() {
/*  506 */     boolean required = (this.flowLevel == 0 && this.indent == this.reader.getColumn());
/*      */     
/*  508 */     if (this.allowSimpleKey || !required) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  518 */       if (this.allowSimpleKey) {
/*  519 */         removePossibleSimpleKey();
/*  520 */         int tokenNumber = this.tokensTaken + this.tokens.size();
/*  521 */         SimpleKey key = new SimpleKey(tokenNumber, required, this.reader.getIndex(), this.reader.getLine(), this.reader.getColumn(), this.reader.getMark());
/*      */         
/*  523 */         this.possibleSimpleKeys.put(Integer.valueOf(this.flowLevel), key);
/*      */       } 
/*      */       return;
/*      */     } 
/*      */     throw new YAMLException("A simple key is required only if it is the first token in the current line");
/*      */   }
/*      */   
/*      */   private void removePossibleSimpleKey() {
/*  531 */     SimpleKey key = this.possibleSimpleKeys.remove(Integer.valueOf(this.flowLevel));
/*  532 */     if (key != null && key.isRequired()) {
/*  533 */       throw new ScannerException("while scanning a simple key", key.getMark(), "could not find expected ':'", this.reader.getMark());
/*      */     }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void unwindIndent(int col) {
/*  563 */     if (this.flowLevel != 0) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  568 */     while (this.indent > col) {
/*  569 */       Mark mark = this.reader.getMark();
/*  570 */       this.indent = ((Integer)this.indents.pop()).intValue();
/*  571 */       this.tokens.add(new BlockEndToken(mark, mark));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean addIndent(int column) {
/*  579 */     if (this.indent < column) {
/*  580 */       this.indents.push(Integer.valueOf(this.indent));
/*  581 */       this.indent = column;
/*  582 */       return true;
/*      */     } 
/*  584 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchStreamStart() {
/*  595 */     Mark mark = this.reader.getMark();
/*      */ 
/*      */     
/*  598 */     StreamStartToken streamStartToken = new StreamStartToken(mark, mark);
/*  599 */     this.tokens.add(streamStartToken);
/*      */   }
/*      */ 
/*      */   
/*      */   private void fetchStreamEnd() {
/*  604 */     unwindIndent(-1);
/*      */ 
/*      */     
/*  607 */     removePossibleSimpleKey();
/*  608 */     this.allowSimpleKey = false;
/*  609 */     this.possibleSimpleKeys.clear();
/*      */ 
/*      */     
/*  612 */     Mark mark = this.reader.getMark();
/*      */ 
/*      */     
/*  615 */     StreamEndToken streamEndToken = new StreamEndToken(mark, mark);
/*  616 */     this.tokens.add(streamEndToken);
/*      */ 
/*      */     
/*  619 */     this.done = true;
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
/*      */   private void fetchDirective() {
/*  631 */     unwindIndent(-1);
/*      */ 
/*      */     
/*  634 */     removePossibleSimpleKey();
/*  635 */     this.allowSimpleKey = false;
/*      */ 
/*      */     
/*  638 */     List<Token> tok = scanDirective();
/*  639 */     this.tokens.addAll(tok);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchDocumentStart() {
/*  646 */     fetchDocumentIndicator(true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchDocumentEnd() {
/*  653 */     fetchDocumentIndicator(false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchDocumentIndicator(boolean isDocumentStart) {
/*      */     DocumentEndToken documentEndToken;
/*  662 */     unwindIndent(-1);
/*      */ 
/*      */ 
/*      */     
/*  666 */     removePossibleSimpleKey();
/*  667 */     this.allowSimpleKey = false;
/*      */ 
/*      */     
/*  670 */     Mark startMark = this.reader.getMark();
/*  671 */     this.reader.forward(3);
/*  672 */     Mark endMark = this.reader.getMark();
/*      */     
/*  674 */     if (isDocumentStart) {
/*  675 */       DocumentStartToken documentStartToken = new DocumentStartToken(startMark, endMark);
/*      */     } else {
/*  677 */       documentEndToken = new DocumentEndToken(startMark, endMark);
/*      */     } 
/*  679 */     this.tokens.add(documentEndToken);
/*      */   }
/*      */   
/*      */   private void fetchFlowSequenceStart() {
/*  683 */     fetchFlowCollectionStart(false);
/*      */   }
/*      */   
/*      */   private void fetchFlowMappingStart() {
/*  687 */     fetchFlowCollectionStart(true);
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
/*      */   private void fetchFlowCollectionStart(boolean isMappingStart) {
/*      */     FlowSequenceStartToken flowSequenceStartToken;
/*  704 */     savePossibleSimpleKey();
/*      */ 
/*      */     
/*  707 */     this.flowLevel++;
/*      */ 
/*      */     
/*  710 */     this.allowSimpleKey = true;
/*      */ 
/*      */     
/*  713 */     Mark startMark = this.reader.getMark();
/*  714 */     this.reader.forward(1);
/*  715 */     Mark endMark = this.reader.getMark();
/*      */     
/*  717 */     if (isMappingStart) {
/*  718 */       FlowMappingStartToken flowMappingStartToken = new FlowMappingStartToken(startMark, endMark);
/*      */     } else {
/*  720 */       flowSequenceStartToken = new FlowSequenceStartToken(startMark, endMark);
/*      */     } 
/*  722 */     this.tokens.add(flowSequenceStartToken);
/*      */   }
/*      */   
/*      */   private void fetchFlowSequenceEnd() {
/*  726 */     fetchFlowCollectionEnd(false);
/*      */   }
/*      */   
/*      */   private void fetchFlowMappingEnd() {
/*  730 */     fetchFlowCollectionEnd(true);
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
/*      */   private void fetchFlowCollectionEnd(boolean isMappingEnd) {
/*      */     FlowSequenceEndToken flowSequenceEndToken;
/*  745 */     removePossibleSimpleKey();
/*      */ 
/*      */     
/*  748 */     this.flowLevel--;
/*      */ 
/*      */     
/*  751 */     this.allowSimpleKey = false;
/*      */ 
/*      */     
/*  754 */     Mark startMark = this.reader.getMark();
/*  755 */     this.reader.forward();
/*  756 */     Mark endMark = this.reader.getMark();
/*      */     
/*  758 */     if (isMappingEnd) {
/*  759 */       FlowMappingEndToken flowMappingEndToken = new FlowMappingEndToken(startMark, endMark);
/*      */     } else {
/*  761 */       flowSequenceEndToken = new FlowSequenceEndToken(startMark, endMark);
/*      */     } 
/*  763 */     this.tokens.add(flowSequenceEndToken);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchFlowEntry() {
/*  774 */     this.allowSimpleKey = true;
/*      */ 
/*      */     
/*  777 */     removePossibleSimpleKey();
/*      */ 
/*      */     
/*  780 */     Mark startMark = this.reader.getMark();
/*  781 */     this.reader.forward();
/*  782 */     Mark endMark = this.reader.getMark();
/*  783 */     FlowEntryToken flowEntryToken = new FlowEntryToken(startMark, endMark);
/*  784 */     this.tokens.add(flowEntryToken);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchBlockEntry() {
/*  794 */     if (this.flowLevel == 0) {
/*      */       
/*  796 */       if (!this.allowSimpleKey) {
/*  797 */         throw new ScannerException(null, null, "sequence entries are not allowed here", this.reader.getMark());
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  802 */       if (addIndent(this.reader.getColumn())) {
/*  803 */         Mark mark = this.reader.getMark();
/*  804 */         this.tokens.add(new BlockSequenceStartToken(mark, mark));
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  811 */     this.allowSimpleKey = true;
/*      */ 
/*      */     
/*  814 */     removePossibleSimpleKey();
/*      */ 
/*      */     
/*  817 */     Mark startMark = this.reader.getMark();
/*  818 */     this.reader.forward();
/*  819 */     Mark endMark = this.reader.getMark();
/*  820 */     BlockEntryToken blockEntryToken = new BlockEntryToken(startMark, endMark);
/*  821 */     this.tokens.add(blockEntryToken);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchKey() {
/*  831 */     if (this.flowLevel == 0) {
/*      */       
/*  833 */       if (!this.allowSimpleKey) {
/*  834 */         throw new ScannerException(null, null, "mapping keys are not allowed here", this.reader.getMark());
/*      */       }
/*      */ 
/*      */       
/*  838 */       if (addIndent(this.reader.getColumn())) {
/*  839 */         Mark mark = this.reader.getMark();
/*  840 */         this.tokens.add(new BlockMappingStartToken(mark, mark));
/*      */       } 
/*      */     } 
/*      */     
/*  844 */     this.allowSimpleKey = (this.flowLevel == 0);
/*      */ 
/*      */     
/*  847 */     removePossibleSimpleKey();
/*      */ 
/*      */     
/*  850 */     Mark startMark = this.reader.getMark();
/*  851 */     this.reader.forward();
/*  852 */     Mark endMark = this.reader.getMark();
/*  853 */     KeyToken keyToken = new KeyToken(startMark, endMark);
/*  854 */     this.tokens.add(keyToken);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchValue() {
/*  864 */     SimpleKey key = this.possibleSimpleKeys.remove(Integer.valueOf(this.flowLevel));
/*  865 */     if (key != null) {
/*      */       
/*  867 */       this.tokens.add(key.getTokenNumber() - this.tokensTaken, new KeyToken(key.getMark(), key.getMark()));
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  872 */       if (this.flowLevel == 0 && 
/*  873 */         addIndent(key.getColumn())) {
/*  874 */         this.tokens.add(key.getTokenNumber() - this.tokensTaken, new BlockMappingStartToken(key.getMark(), key.getMark()));
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  879 */       this.allowSimpleKey = false;
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/*  885 */       if (this.flowLevel == 0)
/*      */       {
/*      */ 
/*      */         
/*  889 */         if (!this.allowSimpleKey) {
/*  890 */           throw new ScannerException(null, null, "mapping values are not allowed here", this.reader.getMark());
/*      */         }
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  898 */       if (this.flowLevel == 0 && 
/*  899 */         addIndent(this.reader.getColumn())) {
/*  900 */         Mark mark = this.reader.getMark();
/*  901 */         this.tokens.add(new BlockMappingStartToken(mark, mark));
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  906 */       this.allowSimpleKey = (this.flowLevel == 0);
/*      */ 
/*      */       
/*  909 */       removePossibleSimpleKey();
/*      */     } 
/*      */     
/*  912 */     Mark startMark = this.reader.getMark();
/*  913 */     this.reader.forward();
/*  914 */     Mark endMark = this.reader.getMark();
/*  915 */     ValueToken valueToken = new ValueToken(startMark, endMark);
/*  916 */     this.tokens.add(valueToken);
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
/*      */   private void fetchAlias() {
/*  931 */     savePossibleSimpleKey();
/*      */ 
/*      */     
/*  934 */     this.allowSimpleKey = false;
/*      */ 
/*      */     
/*  937 */     Token tok = scanAnchor(false);
/*  938 */     this.tokens.add(tok);
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
/*      */   private void fetchAnchor() {
/*  952 */     savePossibleSimpleKey();
/*      */ 
/*      */     
/*  955 */     this.allowSimpleKey = false;
/*      */ 
/*      */     
/*  958 */     Token tok = scanAnchor(true);
/*  959 */     this.tokens.add(tok);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchTag() {
/*  969 */     savePossibleSimpleKey();
/*      */ 
/*      */     
/*  972 */     this.allowSimpleKey = false;
/*      */ 
/*      */     
/*  975 */     Token tok = scanTag();
/*  976 */     this.tokens.add(tok);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchLiteral() {
/*  987 */     fetchBlockScalar('|');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchFolded() {
/*  997 */     fetchBlockScalar('>');
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
/*      */   private void fetchBlockScalar(char style) {
/* 1009 */     this.allowSimpleKey = true;
/*      */ 
/*      */     
/* 1012 */     removePossibleSimpleKey();
/*      */ 
/*      */     
/* 1015 */     List<Token> tok = scanBlockScalar(style);
/* 1016 */     this.tokens.addAll(tok);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchSingle() {
/* 1023 */     fetchFlowScalar('\'');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchDouble() {
/* 1030 */     fetchFlowScalar('"');
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
/*      */   private void fetchFlowScalar(char style) {
/* 1042 */     savePossibleSimpleKey();
/*      */ 
/*      */     
/* 1045 */     this.allowSimpleKey = false;
/*      */ 
/*      */     
/* 1048 */     Token tok = scanFlowScalar(style);
/* 1049 */     this.tokens.add(tok);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchPlain() {
/* 1057 */     savePossibleSimpleKey();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1062 */     this.allowSimpleKey = false;
/*      */ 
/*      */     
/* 1065 */     Token tok = scanPlain();
/* 1066 */     this.tokens.add(tok);
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
/*      */   private boolean checkDirective() {
/* 1079 */     return (this.reader.getColumn() == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean checkDocumentStart() {
/* 1088 */     if (this.reader.getColumn() == 0 && 
/* 1089 */       "---".equals(this.reader.prefix(3)) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3))) {
/* 1090 */       return true;
/*      */     }
/*      */     
/* 1093 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean checkDocumentEnd() {
/* 1102 */     if (this.reader.getColumn() == 0 && 
/* 1103 */       "...".equals(this.reader.prefix(3)) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3))) {
/* 1104 */       return true;
/*      */     }
/*      */     
/* 1107 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean checkBlockEntry() {
/* 1115 */     return Constant.NULL_BL_T_LINEBR.has(this.reader.peek(1));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean checkKey() {
/* 1123 */     if (this.flowLevel != 0) {
/* 1124 */       return true;
/*      */     }
/*      */     
/* 1127 */     return Constant.NULL_BL_T_LINEBR.has(this.reader.peek(1));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean checkValue() {
/* 1136 */     if (this.flowLevel != 0) {
/* 1137 */       return true;
/*      */     }
/*      */     
/* 1140 */     return Constant.NULL_BL_T_LINEBR.has(this.reader.peek(1));
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
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean checkPlain() {
/* 1164 */     int c = this.reader.peek();
/*      */ 
/*      */     
/* 1167 */     return (Constant.NULL_BL_T_LINEBR.hasNo(c, "-?:,[]{}#&*!|>'\"%@`") || (Constant.NULL_BL_T_LINEBR.hasNo(this.reader.peek(1)) && (c == 45 || (this.flowLevel == 0 && "?:".indexOf(c) != -1))));
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
/*      */   private void scanToNextToken() {
/* 1198 */     if (this.reader.getIndex() == 0 && this.reader.peek() == 65279) {
/* 1199 */       this.reader.forward();
/*      */     }
/* 1201 */     boolean found = false;
/* 1202 */     int inlineStartColumn = -1;
/* 1203 */     while (!found) {
/* 1204 */       Mark startMark = this.reader.getMark();
/* 1205 */       boolean commentSeen = false;
/* 1206 */       int ff = 0;
/*      */ 
/*      */       
/* 1209 */       while (this.reader.peek(ff) == 32) {
/* 1210 */         ff++;
/*      */       }
/* 1212 */       if (ff > 0) {
/* 1213 */         this.reader.forward(ff);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1219 */       if (this.reader.peek() == 35) {
/* 1220 */         CommentType type; commentSeen = true;
/*      */         
/* 1222 */         if (startMark.getColumn() != 0 && (startMark.getColumn() != this.indent + ff || this.reader.peek(-ff - 1) != 45)) {
/* 1223 */           type = CommentType.IN_LINE;
/* 1224 */           inlineStartColumn = this.reader.getColumn();
/* 1225 */         } else if (inlineStartColumn == this.reader.getColumn()) {
/* 1226 */           type = CommentType.IN_LINE;
/*      */         } else {
/* 1228 */           inlineStartColumn = -1;
/* 1229 */           type = CommentType.BLOCK;
/*      */         } 
/* 1231 */         CommentToken token = scanComment(type);
/* 1232 */         if (this.emitComments) {
/* 1233 */           this.tokens.add(token);
/*      */         }
/*      */       } 
/*      */ 
/*      */       
/* 1238 */       String breaks = scanLineBreak();
/* 1239 */       if (breaks.length() != 0) {
/* 1240 */         if (this.emitComments && !commentSeen && 
/* 1241 */           startMark.getColumn() == 0) {
/* 1242 */           Mark endMark = this.reader.getMark();
/* 1243 */           this.tokens.add(new CommentToken(CommentType.BLANK_LINE, breaks, startMark, endMark));
/*      */         } 
/*      */         
/* 1246 */         if (this.flowLevel == 0)
/*      */         {
/*      */           
/* 1249 */           this.allowSimpleKey = true; } 
/*      */         continue;
/*      */       } 
/* 1252 */       found = true;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private CommentToken scanComment(CommentType type) {
/* 1259 */     Mark startMark = this.reader.getMark();
/* 1260 */     this.reader.forward();
/* 1261 */     int length = 0;
/* 1262 */     while (Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(length))) {
/* 1263 */       length++;
/*      */     }
/* 1265 */     String value = this.reader.prefixForward(length);
/* 1266 */     Mark endMark = this.reader.getMark();
/* 1267 */     return new CommentToken(type, value, startMark, endMark);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private List<Token> scanDirective() {
/* 1273 */     Mark endMark, startMark = this.reader.getMark();
/*      */     
/* 1275 */     this.reader.forward();
/* 1276 */     String name = scanDirectiveName(startMark);
/* 1277 */     List<?> value = null;
/* 1278 */     if ("YAML".equals(name)) {
/* 1279 */       value = scanYamlDirectiveValue(startMark);
/* 1280 */       endMark = this.reader.getMark();
/* 1281 */     } else if ("TAG".equals(name)) {
/* 1282 */       value = scanTagDirectiveValue(startMark);
/* 1283 */       endMark = this.reader.getMark();
/*      */     } else {
/* 1285 */       endMark = this.reader.getMark();
/* 1286 */       int ff = 0;
/* 1287 */       while (Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(ff))) {
/* 1288 */         ff++;
/*      */       }
/* 1290 */       if (ff > 0) {
/* 1291 */         this.reader.forward(ff);
/*      */       }
/*      */     } 
/* 1294 */     CommentToken commentToken = scanDirectiveIgnoredLine(startMark);
/* 1295 */     DirectiveToken token = new DirectiveToken(name, value, startMark, endMark);
/* 1296 */     return makeTokenList(new Token[] { (Token)token, (Token)commentToken });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String scanDirectiveName(Mark startMark) {
/* 1307 */     int length = 0;
/*      */ 
/*      */ 
/*      */     
/* 1311 */     int c = this.reader.peek(length);
/* 1312 */     while (Constant.ALPHA.has(c)) {
/* 1313 */       length++;
/* 1314 */       c = this.reader.peek(length);
/*      */     } 
/*      */     
/* 1317 */     if (length == 0) {
/* 1318 */       String s = String.valueOf(Character.toChars(c));
/* 1319 */       throw new ScannerException("while scanning a directive", startMark, "expected alphabetic or numeric character, but found " + s + "(" + c + ")", this.reader.getMark());
/*      */     } 
/*      */ 
/*      */     
/* 1323 */     String value = this.reader.prefixForward(length);
/* 1324 */     c = this.reader.peek();
/* 1325 */     if (Constant.NULL_BL_LINEBR.hasNo(c)) {
/* 1326 */       String s = String.valueOf(Character.toChars(c));
/* 1327 */       throw new ScannerException("while scanning a directive", startMark, "expected alphabetic or numeric character, but found " + s + "(" + c + ")", this.reader.getMark());
/*      */     } 
/*      */ 
/*      */     
/* 1331 */     return value;
/*      */   }
/*      */ 
/*      */   
/*      */   private List<Integer> scanYamlDirectiveValue(Mark startMark) {
/* 1336 */     while (this.reader.peek() == 32) {
/* 1337 */       this.reader.forward();
/*      */     }
/* 1339 */     Integer major = scanYamlDirectiveNumber(startMark);
/* 1340 */     int c = this.reader.peek();
/* 1341 */     if (c != 46) {
/* 1342 */       String s = String.valueOf(Character.toChars(c));
/* 1343 */       throw new ScannerException("while scanning a directive", startMark, "expected a digit or '.', but found " + s + "(" + c + ")", this.reader.getMark());
/*      */     } 
/*      */ 
/*      */     
/* 1347 */     this.reader.forward();
/* 1348 */     Integer minor = scanYamlDirectiveNumber(startMark);
/* 1349 */     c = this.reader.peek();
/* 1350 */     if (Constant.NULL_BL_LINEBR.hasNo(c)) {
/* 1351 */       String s = String.valueOf(Character.toChars(c));
/* 1352 */       throw new ScannerException("while scanning a directive", startMark, "expected a digit or ' ', but found " + s + "(" + c + ")", this.reader.getMark());
/*      */     } 
/*      */ 
/*      */     
/* 1356 */     List<Integer> result = new ArrayList<>(2);
/* 1357 */     result.add(major);
/* 1358 */     result.add(minor);
/* 1359 */     return result;
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
/*      */   private Integer scanYamlDirectiveNumber(Mark startMark) {
/* 1371 */     int c = this.reader.peek();
/* 1372 */     if (!Character.isDigit(c)) {
/* 1373 */       String s = String.valueOf(Character.toChars(c));
/* 1374 */       throw new ScannerException("while scanning a directive", startMark, "expected a digit, but found " + s + "(" + c + ")", this.reader.getMark());
/*      */     } 
/*      */     
/* 1377 */     int length = 0;
/* 1378 */     while (Character.isDigit(this.reader.peek(length))) {
/* 1379 */       length++;
/*      */     }
/* 1381 */     Integer value = Integer.valueOf(Integer.parseInt(this.reader.prefixForward(length)));
/* 1382 */     return value;
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
/*      */   private List<String> scanTagDirectiveValue(Mark startMark) {
/* 1399 */     while (this.reader.peek() == 32) {
/* 1400 */       this.reader.forward();
/*      */     }
/* 1402 */     String handle = scanTagDirectiveHandle(startMark);
/* 1403 */     while (this.reader.peek() == 32) {
/* 1404 */       this.reader.forward();
/*      */     }
/* 1406 */     String prefix = scanTagDirectivePrefix(startMark);
/* 1407 */     List<String> result = new ArrayList<>(2);
/* 1408 */     result.add(handle);
/* 1409 */     result.add(prefix);
/* 1410 */     return result;
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
/*      */   private String scanTagDirectiveHandle(Mark startMark) {
/* 1422 */     String value = scanTagHandle("directive", startMark);
/* 1423 */     int c = this.reader.peek();
/* 1424 */     if (c != 32) {
/* 1425 */       String s = String.valueOf(Character.toChars(c));
/* 1426 */       throw new ScannerException("while scanning a directive", startMark, "expected ' ', but found " + s + "(" + c + ")", this.reader.getMark());
/*      */     } 
/*      */     
/* 1429 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String scanTagDirectivePrefix(Mark startMark) {
/* 1439 */     String value = scanTagUri("directive", startMark);
/* 1440 */     int c = this.reader.peek();
/* 1441 */     if (Constant.NULL_BL_LINEBR.hasNo(c)) {
/* 1442 */       String s = String.valueOf(Character.toChars(c));
/* 1443 */       throw new ScannerException("while scanning a directive", startMark, "expected ' ', but found " + s + "(" + c + ")", this.reader.getMark());
/*      */     } 
/*      */ 
/*      */     
/* 1447 */     return value;
/*      */   }
/*      */ 
/*      */   
/*      */   private CommentToken scanDirectiveIgnoredLine(Mark startMark) {
/* 1452 */     while (this.reader.peek() == 32) {
/* 1453 */       this.reader.forward();
/*      */     }
/* 1455 */     CommentToken commentToken = null;
/* 1456 */     if (this.reader.peek() == 35) {
/* 1457 */       Mark commentStartMark = this.reader.getMark();
/* 1458 */       int length = 0;
/* 1459 */       while (Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(length))) {
/* 1460 */         length++;
/*      */       }
/* 1462 */       String comment = this.reader.prefixForward(length);
/* 1463 */       if (this.emitComments) {
/* 1464 */         Mark commentEndMark = this.reader.getMark();
/* 1465 */         commentToken = new CommentToken(CommentType.IN_LINE, comment, commentStartMark, commentEndMark);
/*      */       } 
/*      */     } 
/* 1468 */     int c = this.reader.peek();
/* 1469 */     String lineBreak = scanLineBreak();
/* 1470 */     if (lineBreak.length() == 0 && c != 0) {
/* 1471 */       String s = String.valueOf(Character.toChars(c));
/* 1472 */       throw new ScannerException("while scanning a directive", startMark, "expected a comment or a line break, but found " + s + "(" + c + ")", this.reader.getMark());
/*      */     } 
/*      */ 
/*      */     
/* 1476 */     return commentToken;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Token scanAnchor(boolean isAnchor) {
/*      */     AliasToken aliasToken;
/* 1488 */     Mark startMark = this.reader.getMark();
/* 1489 */     int indicator = this.reader.peek();
/* 1490 */     String name = (indicator == 42) ? "alias" : "anchor";
/* 1491 */     this.reader.forward();
/* 1492 */     int length = 0;
/* 1493 */     int c = this.reader.peek(length);
/* 1494 */     while (Constant.NULL_BL_T_LINEBR.hasNo(c, ":,[]{}/.*&")) {
/* 1495 */       length++;
/* 1496 */       c = this.reader.peek(length);
/*      */     } 
/* 1498 */     if (length == 0) {
/* 1499 */       String s = String.valueOf(Character.toChars(c));
/* 1500 */       throw new ScannerException("while scanning an " + name, startMark, "unexpected character found " + s + "(" + c + ")", this.reader.getMark());
/*      */     } 
/*      */     
/* 1503 */     String value = this.reader.prefixForward(length);
/* 1504 */     c = this.reader.peek();
/* 1505 */     if (Constant.NULL_BL_T_LINEBR.hasNo(c, "?:,]}%@`")) {
/* 1506 */       String s = String.valueOf(Character.toChars(c));
/* 1507 */       throw new ScannerException("while scanning an " + name, startMark, "unexpected character found " + s + "(" + c + ")", this.reader.getMark());
/*      */     } 
/*      */     
/* 1510 */     Mark endMark = this.reader.getMark();
/*      */     
/* 1512 */     if (isAnchor) {
/* 1513 */       AnchorToken anchorToken = new AnchorToken(value, startMark, endMark);
/*      */     } else {
/* 1515 */       aliasToken = new AliasToken(value, startMark, endMark);
/*      */     } 
/* 1517 */     return (Token)aliasToken;
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
/*      */   private Token scanTag() {
/* 1555 */     Mark startMark = this.reader.getMark();
/*      */ 
/*      */     
/* 1558 */     int c = this.reader.peek(1);
/* 1559 */     String handle = null;
/* 1560 */     String suffix = null;
/*      */     
/* 1562 */     if (c == 60) {
/*      */ 
/*      */       
/* 1565 */       this.reader.forward(2);
/* 1566 */       suffix = scanTagUri("tag", startMark);
/* 1567 */       c = this.reader.peek();
/* 1568 */       if (c != 62) {
/*      */ 
/*      */         
/* 1571 */         String s = String.valueOf(Character.toChars(c));
/* 1572 */         throw new ScannerException("while scanning a tag", startMark, "expected '>', but found '" + s + "' (" + c + ")", this.reader.getMark());
/*      */       } 
/*      */ 
/*      */       
/* 1576 */       this.reader.forward();
/* 1577 */     } else if (Constant.NULL_BL_T_LINEBR.has(c)) {
/*      */ 
/*      */       
/* 1580 */       suffix = "!";
/* 1581 */       this.reader.forward();
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/* 1587 */       int length = 1;
/* 1588 */       boolean useHandle = false;
/* 1589 */       while (Constant.NULL_BL_LINEBR.hasNo(c)) {
/* 1590 */         if (c == 33) {
/* 1591 */           useHandle = true;
/*      */           break;
/*      */         } 
/* 1594 */         length++;
/* 1595 */         c = this.reader.peek(length);
/*      */       } 
/*      */ 
/*      */       
/* 1599 */       if (useHandle) {
/* 1600 */         handle = scanTagHandle("tag", startMark);
/*      */       } else {
/* 1602 */         handle = "!";
/* 1603 */         this.reader.forward();
/*      */       } 
/* 1605 */       suffix = scanTagUri("tag", startMark);
/*      */     } 
/* 1607 */     c = this.reader.peek();
/*      */ 
/*      */     
/* 1610 */     if (Constant.NULL_BL_LINEBR.hasNo(c)) {
/* 1611 */       String s = String.valueOf(Character.toChars(c));
/* 1612 */       throw new ScannerException("while scanning a tag", startMark, "expected ' ', but found '" + s + "' (" + c + ")", this.reader.getMark());
/*      */     } 
/*      */     
/* 1615 */     TagTuple value = new TagTuple(handle, suffix);
/* 1616 */     Mark endMark = this.reader.getMark();
/* 1617 */     return (Token)new TagToken(value, startMark, endMark);
/*      */   }
/*      */   
/*      */   private List<Token> scanBlockScalar(char style) {
/*      */     boolean folded;
/*      */     String breaks;
/*      */     int indent;
/*      */     Mark mark1;
/* 1625 */     if (style == '>') {
/* 1626 */       folded = true;
/*      */     } else {
/* 1628 */       folded = false;
/*      */     } 
/* 1630 */     StringBuilder chunks = new StringBuilder();
/* 1631 */     Mark startMark = this.reader.getMark();
/*      */     
/* 1633 */     this.reader.forward();
/* 1634 */     Chomping chompi = scanBlockScalarIndicators(startMark);
/* 1635 */     int increment = chompi.getIncrement();
/* 1636 */     CommentToken commentToken = scanBlockScalarIgnoredLine(startMark);
/*      */ 
/*      */     
/* 1639 */     int minIndent = this.indent + 1;
/* 1640 */     if (minIndent < 1) {
/* 1641 */       minIndent = 1;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1647 */     if (increment == -1) {
/* 1648 */       Object[] brme = scanBlockScalarIndentation();
/* 1649 */       breaks = (String)brme[0];
/* 1650 */       int maxIndent = ((Integer)brme[1]).intValue();
/* 1651 */       mark1 = (Mark)brme[2];
/* 1652 */       indent = Math.max(minIndent, maxIndent);
/*      */     } else {
/* 1654 */       indent = minIndent + increment - 1;
/* 1655 */       Object[] brme = scanBlockScalarBreaks(indent);
/* 1656 */       breaks = (String)brme[0];
/* 1657 */       mark1 = (Mark)brme[1];
/*      */     } 
/*      */     
/* 1660 */     String lineBreak = "";
/*      */ 
/*      */     
/* 1663 */     while (this.reader.getColumn() == indent && this.reader.peek() != 0) {
/* 1664 */       chunks.append(breaks);
/* 1665 */       boolean leadingNonSpace = (" \t".indexOf(this.reader.peek()) == -1);
/* 1666 */       int length = 0;
/* 1667 */       while (Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(length))) {
/* 1668 */         length++;
/*      */       }
/* 1670 */       chunks.append(this.reader.prefixForward(length));
/* 1671 */       lineBreak = scanLineBreak();
/* 1672 */       Object[] brme = scanBlockScalarBreaks(indent);
/* 1673 */       breaks = (String)brme[0];
/* 1674 */       mark1 = (Mark)brme[1];
/* 1675 */       if (this.reader.getColumn() == indent && this.reader.peek() != 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1680 */         if (folded && "\n".equals(lineBreak) && leadingNonSpace && " \t".indexOf(this.reader.peek()) == -1) {
/*      */           
/* 1682 */           if (breaks.length() == 0)
/* 1683 */             chunks.append(" "); 
/*      */           continue;
/*      */         } 
/* 1686 */         chunks.append(lineBreak);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1695 */     if (chompi.chompTailIsNotFalse()) {
/* 1696 */       chunks.append(lineBreak);
/*      */     }
/* 1698 */     CommentToken blankLineCommentToken = null;
/* 1699 */     if (chompi.chompTailIsTrue()) {
/* 1700 */       if (this.emitComments) {
/* 1701 */         blankLineCommentToken = new CommentToken(CommentType.BLANK_LINE, breaks, startMark, mark1);
/*      */       }
/* 1703 */       chunks.append(breaks);
/*      */     } 
/*      */     
/* 1706 */     ScalarToken scalarToken = new ScalarToken(chunks.toString(), false, startMark, mark1, DumperOptions.ScalarStyle.createStyle(Character.valueOf(style)));
/* 1707 */     return makeTokenList(new Token[] { (Token)commentToken, (Token)scalarToken, (Token)blankLineCommentToken });
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
/*      */   private Chomping scanBlockScalarIndicators(Mark startMark) {
/* 1727 */     Boolean chomping = null;
/* 1728 */     int increment = -1;
/* 1729 */     int c = this.reader.peek();
/* 1730 */     if (c == 45 || c == 43) {
/* 1731 */       if (c == 43) {
/* 1732 */         chomping = Boolean.TRUE;
/*      */       } else {
/* 1734 */         chomping = Boolean.FALSE;
/*      */       } 
/* 1736 */       this.reader.forward();
/* 1737 */       c = this.reader.peek();
/* 1738 */       if (Character.isDigit(c)) {
/* 1739 */         String s = String.valueOf(Character.toChars(c));
/* 1740 */         increment = Integer.parseInt(s);
/* 1741 */         if (increment == 0) {
/* 1742 */           throw new ScannerException("while scanning a block scalar", startMark, "expected indentation indicator in the range 1-9, but found 0", this.reader.getMark());
/*      */         }
/*      */ 
/*      */         
/* 1746 */         this.reader.forward();
/*      */       } 
/* 1748 */     } else if (Character.isDigit(c)) {
/* 1749 */       String s = String.valueOf(Character.toChars(c));
/* 1750 */       increment = Integer.parseInt(s);
/* 1751 */       if (increment == 0) {
/* 1752 */         throw new ScannerException("while scanning a block scalar", startMark, "expected indentation indicator in the range 1-9, but found 0", this.reader.getMark());
/*      */       }
/*      */ 
/*      */       
/* 1756 */       this.reader.forward();
/* 1757 */       c = this.reader.peek();
/* 1758 */       if (c == 45 || c == 43) {
/* 1759 */         if (c == 43) {
/* 1760 */           chomping = Boolean.TRUE;
/*      */         } else {
/* 1762 */           chomping = Boolean.FALSE;
/*      */         } 
/* 1764 */         this.reader.forward();
/*      */       } 
/*      */     } 
/* 1767 */     c = this.reader.peek();
/* 1768 */     if (Constant.NULL_BL_LINEBR.hasNo(c)) {
/* 1769 */       String s = String.valueOf(Character.toChars(c));
/* 1770 */       throw new ScannerException("while scanning a block scalar", startMark, "expected chomping or indentation indicators, but found " + s + "(" + c + ")", this.reader.getMark());
/*      */     } 
/*      */ 
/*      */     
/* 1774 */     return new Chomping(chomping, increment);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private CommentToken scanBlockScalarIgnoredLine(Mark startMark) {
/* 1785 */     while (this.reader.peek() == 32) {
/* 1786 */       this.reader.forward();
/*      */     }
/*      */ 
/*      */     
/* 1790 */     CommentToken commentToken = null;
/* 1791 */     if (this.reader.peek() == 35) {
/* 1792 */       commentToken = scanComment(CommentType.IN_LINE);
/*      */     }
/*      */ 
/*      */     
/* 1796 */     int c = this.reader.peek();
/* 1797 */     String lineBreak = scanLineBreak();
/* 1798 */     if (lineBreak.length() == 0 && c != 0) {
/* 1799 */       String s = String.valueOf(Character.toChars(c));
/* 1800 */       throw new ScannerException("while scanning a block scalar", startMark, "expected a comment or a line break, but found " + s + "(" + c + ")", this.reader.getMark());
/*      */     } 
/*      */ 
/*      */     
/* 1804 */     return commentToken;
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
/*      */   private Object[] scanBlockScalarIndentation() {
/* 1816 */     StringBuilder chunks = new StringBuilder();
/* 1817 */     int maxIndent = 0;
/* 1818 */     Mark endMark = this.reader.getMark();
/*      */ 
/*      */ 
/*      */     
/* 1822 */     while (Constant.LINEBR.has(this.reader.peek(), " \r")) {
/* 1823 */       if (this.reader.peek() != 32) {
/*      */ 
/*      */         
/* 1826 */         chunks.append(scanLineBreak());
/* 1827 */         endMark = this.reader.getMark();
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 1832 */       this.reader.forward();
/* 1833 */       if (this.reader.getColumn() > maxIndent) {
/* 1834 */         maxIndent = this.reader.getColumn();
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1839 */     return new Object[] { chunks.toString(), Integer.valueOf(maxIndent), endMark };
/*      */   }
/*      */ 
/*      */   
/*      */   private Object[] scanBlockScalarBreaks(int indent) {
/* 1844 */     StringBuilder chunks = new StringBuilder();
/* 1845 */     Mark endMark = this.reader.getMark();
/* 1846 */     int col = this.reader.getColumn();
/*      */ 
/*      */     
/* 1849 */     while (col < indent && this.reader.peek() == 32) {
/* 1850 */       this.reader.forward();
/* 1851 */       col++;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1856 */     String lineBreak = null;
/* 1857 */     while ((lineBreak = scanLineBreak()).length() != 0) {
/* 1858 */       chunks.append(lineBreak);
/* 1859 */       endMark = this.reader.getMark();
/*      */ 
/*      */       
/* 1862 */       col = this.reader.getColumn();
/* 1863 */       while (col < indent && this.reader.peek() == 32) {
/* 1864 */         this.reader.forward();
/* 1865 */         col++;
/*      */       } 
/*      */     } 
/*      */     
/* 1869 */     return new Object[] { chunks.toString(), endMark };
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
/*      */   
/*      */   private Token scanFlowScalar(char style) {
/*      */     boolean _double;
/* 1892 */     if (style == '"') {
/* 1893 */       _double = true;
/*      */     } else {
/* 1895 */       _double = false;
/*      */     } 
/* 1897 */     StringBuilder chunks = new StringBuilder();
/* 1898 */     Mark startMark = this.reader.getMark();
/* 1899 */     int quote = this.reader.peek();
/* 1900 */     this.reader.forward();
/* 1901 */     chunks.append(scanFlowScalarNonSpaces(_double, startMark));
/* 1902 */     while (this.reader.peek() != quote) {
/* 1903 */       chunks.append(scanFlowScalarSpaces(startMark));
/* 1904 */       chunks.append(scanFlowScalarNonSpaces(_double, startMark));
/*      */     } 
/* 1906 */     this.reader.forward();
/* 1907 */     Mark endMark = this.reader.getMark();
/* 1908 */     return (Token)new ScalarToken(chunks.toString(), false, startMark, endMark, DumperOptions.ScalarStyle.createStyle(Character.valueOf(style)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String scanFlowScalarNonSpaces(boolean doubleQuoted, Mark startMark) {
/* 1916 */     StringBuilder chunks = new StringBuilder();
/*      */ 
/*      */     
/*      */     while (true) {
/* 1920 */       int length = 0;
/* 1921 */       while (Constant.NULL_BL_T_LINEBR.hasNo(this.reader.peek(length), "'\"\\")) {
/* 1922 */         length++;
/*      */       }
/* 1924 */       if (length != 0) {
/* 1925 */         chunks.append(this.reader.prefixForward(length));
/*      */       }
/*      */ 
/*      */       
/* 1929 */       int c = this.reader.peek();
/* 1930 */       if (!doubleQuoted && c == 39 && this.reader.peek(1) == 39) {
/* 1931 */         chunks.append("'");
/* 1932 */         this.reader.forward(2); continue;
/* 1933 */       }  if ((doubleQuoted && c == 39) || (!doubleQuoted && "\"\\".indexOf(c) != -1)) {
/* 1934 */         chunks.appendCodePoint(c);
/* 1935 */         this.reader.forward(); continue;
/* 1936 */       }  if (doubleQuoted && c == 92) {
/* 1937 */         this.reader.forward();
/* 1938 */         c = this.reader.peek();
/* 1939 */         if (!Character.isSupplementaryCodePoint(c) && ESCAPE_REPLACEMENTS.containsKey(Character.valueOf((char)c))) {
/*      */ 
/*      */ 
/*      */           
/* 1943 */           chunks.append(ESCAPE_REPLACEMENTS.get(Character.valueOf((char)c)));
/* 1944 */           this.reader.forward(); continue;
/* 1945 */         }  if (!Character.isSupplementaryCodePoint(c) && ESCAPE_CODES.containsKey(Character.valueOf((char)c))) {
/*      */ 
/*      */           
/* 1948 */           length = ((Integer)ESCAPE_CODES.get(Character.valueOf((char)c))).intValue();
/* 1949 */           this.reader.forward();
/* 1950 */           String hex = this.reader.prefix(length);
/* 1951 */           if (NOT_HEXA.matcher(hex).find()) {
/* 1952 */             throw new ScannerException("while scanning a double-quoted scalar", startMark, "expected escape sequence of " + length + " hexadecimal numbers, but found: " + hex, this.reader.getMark());
/*      */           }
/*      */ 
/*      */ 
/*      */           
/* 1957 */           int decimal = Integer.parseInt(hex, 16);
/* 1958 */           String unicode = new String(Character.toChars(decimal));
/* 1959 */           chunks.append(unicode);
/* 1960 */           this.reader.forward(length); continue;
/* 1961 */         }  if (scanLineBreak().length() != 0) {
/* 1962 */           chunks.append(scanFlowScalarBreaks(startMark)); continue;
/*      */         } 
/* 1964 */         String s = String.valueOf(Character.toChars(c));
/* 1965 */         throw new ScannerException("while scanning a double-quoted scalar", startMark, "found unknown escape character " + s + "(" + c + ")", this.reader.getMark());
/*      */       } 
/*      */       
/*      */       break;
/*      */     } 
/* 1970 */     return chunks.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String scanFlowScalarSpaces(Mark startMark) {
/* 1977 */     StringBuilder chunks = new StringBuilder();
/* 1978 */     int length = 0;
/*      */ 
/*      */     
/* 1981 */     while (" \t".indexOf(this.reader.peek(length)) != -1) {
/* 1982 */       length++;
/*      */     }
/* 1984 */     String whitespaces = this.reader.prefixForward(length);
/* 1985 */     int c = this.reader.peek();
/* 1986 */     if (c == 0)
/*      */     {
/* 1988 */       throw new ScannerException("while scanning a quoted scalar", startMark, "found unexpected end of stream", this.reader.getMark());
/*      */     }
/*      */ 
/*      */     
/* 1992 */     String lineBreak = scanLineBreak();
/* 1993 */     if (lineBreak.length() != 0) {
/* 1994 */       String breaks = scanFlowScalarBreaks(startMark);
/* 1995 */       if (!"\n".equals(lineBreak)) {
/* 1996 */         chunks.append(lineBreak);
/* 1997 */       } else if (breaks.length() == 0) {
/* 1998 */         chunks.append(" ");
/*      */       } 
/* 2000 */       chunks.append(breaks);
/*      */     } else {
/* 2002 */       chunks.append(whitespaces);
/*      */     } 
/* 2004 */     return chunks.toString();
/*      */   }
/*      */ 
/*      */   
/*      */   private String scanFlowScalarBreaks(Mark startMark) {
/* 2009 */     StringBuilder chunks = new StringBuilder();
/*      */ 
/*      */     
/*      */     while (true) {
/* 2013 */       String prefix = this.reader.prefix(3);
/* 2014 */       if (("---".equals(prefix) || "...".equals(prefix)) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3)))
/*      */       {
/* 2016 */         throw new ScannerException("while scanning a quoted scalar", startMark, "found unexpected document separator", this.reader.getMark());
/*      */       }
/*      */ 
/*      */       
/* 2020 */       while (" \t".indexOf(this.reader.peek()) != -1) {
/* 2021 */         this.reader.forward();
/*      */       }
/*      */ 
/*      */       
/* 2025 */       String lineBreak = scanLineBreak();
/* 2026 */       if (lineBreak.length() != 0) {
/* 2027 */         chunks.append(lineBreak); continue;
/*      */       }  break;
/* 2029 */     }  return chunks.toString();
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
/*      */   private Token scanPlain() {
/* 2046 */     StringBuilder chunks = new StringBuilder();
/* 2047 */     Mark startMark = this.reader.getMark();
/* 2048 */     Mark endMark = startMark;
/* 2049 */     int indent = this.indent + 1;
/* 2050 */     String spaces = "";
/*      */     
/*      */     do {
/* 2053 */       int length = 0;
/*      */       
/* 2055 */       if (this.reader.peek() == 35) {
/*      */         break;
/*      */       }
/*      */       while (true) {
/* 2059 */         int c = this.reader.peek(length);
/* 2060 */         if (Constant.NULL_BL_T_LINEBR.has(c) || (c == 58 && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(length + 1), (this.flowLevel != 0) ? ",[]{}" : "")) || (this.flowLevel != 0 && ",?[]{}".indexOf(c) != -1)) {
/*      */           break;
/*      */         }
/*      */ 
/*      */         
/* 2065 */         length++;
/*      */       } 
/* 2067 */       if (length == 0) {
/*      */         break;
/*      */       }
/* 2070 */       this.allowSimpleKey = false;
/* 2071 */       chunks.append(spaces);
/* 2072 */       chunks.append(this.reader.prefixForward(length));
/* 2073 */       endMark = this.reader.getMark();
/* 2074 */       spaces = scanPlainSpaces();
/*      */     }
/* 2076 */     while (spaces.length() != 0 && this.reader.peek() != 35 && (this.flowLevel != 0 || this.reader.getColumn() >= indent));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2081 */     return (Token)new ScalarToken(chunks.toString(), startMark, endMark, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean atEndOfPlain() {
/* 2088 */     int wsLength = 0;
/* 2089 */     int wsColumn = this.reader.getColumn();
/*      */     
/*      */     int c;
/* 2092 */     while ((c = this.reader.peek(wsLength)) != 0 && Constant.NULL_BL_T_LINEBR.has(c)) {
/* 2093 */       wsLength++;
/* 2094 */       if (!Constant.LINEBR.has(c) && (c != 13 || this.reader.peek(wsLength + 1) != 10) && c != 65279) {
/* 2095 */         wsColumn++; continue;
/*      */       } 
/* 2097 */       wsColumn = 0;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2104 */     if (this.reader.peek(wsLength) == 35 || this.reader.peek(wsLength + 1) == 0 || (this.flowLevel == 0 && wsColumn < this.indent))
/*      */     {
/* 2106 */       return true;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 2111 */     if (this.flowLevel == 0)
/*      */     {
/* 2113 */       for (int extra = 1; (c = this.reader.peek(wsLength + extra)) != 0 && !Constant.NULL_BL_T_LINEBR.has(c); extra++) {
/* 2114 */         if (c == 58 && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(wsLength + extra + 1))) {
/* 2115 */           return true;
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 2121 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String scanPlainSpaces() {
/* 2129 */     int length = 0;
/* 2130 */     while (this.reader.peek(length) == 32 || this.reader.peek(length) == 9) {
/* 2131 */       length++;
/*      */     }
/* 2133 */     String whitespaces = this.reader.prefixForward(length);
/* 2134 */     String lineBreak = scanLineBreak();
/* 2135 */     if (lineBreak.length() != 0) {
/* 2136 */       this.allowSimpleKey = true;
/* 2137 */       String prefix = this.reader.prefix(3);
/* 2138 */       if ("---".equals(prefix) || ("...".equals(prefix) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3))))
/*      */       {
/* 2140 */         return "";
/*      */       }
/* 2142 */       if (this.emitComments && atEndOfPlain()) {
/* 2143 */         return "";
/*      */       }
/* 2145 */       StringBuilder breaks = new StringBuilder();
/*      */       while (true) {
/* 2147 */         while (this.reader.peek() == 32) {
/* 2148 */           this.reader.forward();
/*      */         }
/* 2150 */         String lb = scanLineBreak();
/* 2151 */         if (lb.length() != 0) {
/* 2152 */           breaks.append(lb);
/* 2153 */           prefix = this.reader.prefix(3);
/* 2154 */           if ("---".equals(prefix) || ("...".equals(prefix) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3))))
/*      */           {
/* 2156 */             return "";
/*      */           }
/*      */           
/*      */           continue;
/*      */         } 
/*      */         break;
/*      */       } 
/* 2163 */       if (!"\n".equals(lineBreak))
/* 2164 */         return lineBreak + breaks; 
/* 2165 */       if (breaks.length() == 0) {
/* 2166 */         return " ";
/*      */       }
/* 2168 */       return breaks.toString();
/*      */     } 
/* 2170 */     return whitespaces;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String scanTagHandle(String name, Mark startMark) {
/* 2196 */     int c = this.reader.peek();
/* 2197 */     if (c != 33) {
/* 2198 */       String s = String.valueOf(Character.toChars(c));
/* 2199 */       throw new ScannerException("while scanning a " + name, startMark, "expected '!', but found " + s + "(" + c + ")", this.reader.getMark());
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2205 */     int length = 1;
/* 2206 */     c = this.reader.peek(length);
/* 2207 */     if (c != 32) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2212 */       while (Constant.ALPHA.has(c)) {
/* 2213 */         length++;
/* 2214 */         c = this.reader.peek(length);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 2219 */       if (c != 33) {
/* 2220 */         this.reader.forward(length);
/* 2221 */         String s = String.valueOf(Character.toChars(c));
/* 2222 */         throw new ScannerException("while scanning a " + name, startMark, "expected '!', but found " + s + "(" + c + ")", this.reader.getMark());
/*      */       } 
/*      */       
/* 2225 */       length++;
/*      */     } 
/* 2227 */     String value = this.reader.prefixForward(length);
/* 2228 */     return value;
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
/*      */   private String scanTagUri(String name, Mark startMark) {
/* 2249 */     StringBuilder chunks = new StringBuilder();
/*      */ 
/*      */ 
/*      */     
/* 2253 */     int length = 0;
/* 2254 */     int c = this.reader.peek(length);
/* 2255 */     while (Constant.URI_CHARS.has(c)) {
/* 2256 */       if (c == 37) {
/* 2257 */         chunks.append(this.reader.prefixForward(length));
/* 2258 */         length = 0;
/* 2259 */         chunks.append(scanUriEscapes(name, startMark));
/*      */       } else {
/* 2261 */         length++;
/*      */       } 
/* 2263 */       c = this.reader.peek(length);
/*      */     } 
/*      */ 
/*      */     
/* 2267 */     if (length != 0) {
/* 2268 */       chunks.append(this.reader.prefixForward(length));
/*      */     }
/* 2270 */     if (chunks.length() == 0) {
/*      */       
/* 2272 */       String s = String.valueOf(Character.toChars(c));
/* 2273 */       throw new ScannerException("while scanning a " + name, startMark, "expected URI, but found " + s + "(" + c + ")", this.reader.getMark());
/*      */     } 
/*      */     
/* 2276 */     return chunks.toString();
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
/*      */   private String scanUriEscapes(String name, Mark startMark) {
/* 2293 */     int length = 1;
/* 2294 */     while (this.reader.peek(length * 3) == 37) {
/* 2295 */       length++;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2301 */     Mark beginningMark = this.reader.getMark();
/* 2302 */     ByteBuffer buff = ByteBuffer.allocate(length);
/* 2303 */     while (this.reader.peek() == 37) {
/* 2304 */       this.reader.forward();
/*      */       try {
/* 2306 */         byte code = (byte)Integer.parseInt(this.reader.prefix(2), 16);
/* 2307 */         buff.put(code);
/* 2308 */       } catch (NumberFormatException nfe) {
/* 2309 */         int c1 = this.reader.peek();
/* 2310 */         String s1 = String.valueOf(Character.toChars(c1));
/* 2311 */         int c2 = this.reader.peek(1);
/* 2312 */         String s2 = String.valueOf(Character.toChars(c2));
/* 2313 */         throw new ScannerException("while scanning a " + name, startMark, "expected URI escape sequence of 2 hexadecimal numbers, but found " + s1 + "(" + c1 + ") and " + s2 + "(" + c2 + ")", this.reader.getMark());
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2319 */       this.reader.forward(2);
/*      */     } 
/* 2321 */     buff.flip();
/*      */     try {
/* 2323 */       return UriEncoder.decode(buff);
/* 2324 */     } catch (CharacterCodingException e) {
/* 2325 */       throw new ScannerException("while scanning a " + name, startMark, "expected URI in UTF-8: " + e.getMessage(), beginningMark);
/*      */     } 
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
/*      */   
/*      */   private String scanLineBreak() {
/* 2348 */     int c = this.reader.peek();
/* 2349 */     if (c == 13 || c == 10 || c == 133) {
/* 2350 */       if (c == 13 && 10 == this.reader.peek(1)) {
/* 2351 */         this.reader.forward(2);
/*      */       } else {
/* 2353 */         this.reader.forward();
/*      */       } 
/* 2355 */       return "\n";
/* 2356 */     }  if (c == 8232 || c == 8233) {
/* 2357 */       this.reader.forward();
/* 2358 */       return String.valueOf(Character.toChars(c));
/*      */     } 
/* 2360 */     return "";
/*      */   }
/*      */   
/*      */   private List<Token> makeTokenList(Token... tokens) {
/* 2364 */     List<Token> tokenList = new ArrayList<>();
/* 2365 */     for (int ix = 0; ix < tokens.length; ix++) {
/* 2366 */       if (tokens[ix] != null)
/*      */       {
/*      */         
/* 2369 */         if (this.emitComments || !(tokens[ix] instanceof CommentToken))
/*      */         {
/*      */           
/* 2372 */           tokenList.add(tokens[ix]); }  } 
/*      */     } 
/* 2374 */     return tokenList;
/*      */   }
/*      */ 
/*      */   
/*      */   private static class Chomping
/*      */   {
/*      */     private final Boolean value;
/*      */     
/*      */     private final int increment;
/*      */     
/*      */     public Chomping(Boolean value, int increment) {
/* 2385 */       this.value = value;
/* 2386 */       this.increment = increment;
/*      */     }
/*      */     
/*      */     public boolean chompTailIsNotFalse() {
/* 2390 */       return (this.value == null || this.value.booleanValue());
/*      */     }
/*      */     
/*      */     public boolean chompTailIsTrue() {
/* 2394 */       return (this.value != null && this.value.booleanValue());
/*      */     }
/*      */     
/*      */     public int getIncrement() {
/* 2398 */       return this.increment;
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\scanner\ScannerImpl.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */