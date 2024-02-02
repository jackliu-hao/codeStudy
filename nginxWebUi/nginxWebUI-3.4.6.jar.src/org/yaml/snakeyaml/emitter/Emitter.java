/*      */ package org.yaml.snakeyaml.emitter;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.Writer;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Queue;
/*      */ import java.util.Set;
/*      */ import java.util.TreeSet;
/*      */ import java.util.concurrent.ArrayBlockingQueue;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.yaml.snakeyaml.DumperOptions;
/*      */ import org.yaml.snakeyaml.comments.CommentEventsCollector;
/*      */ import org.yaml.snakeyaml.comments.CommentLine;
/*      */ import org.yaml.snakeyaml.comments.CommentType;
/*      */ import org.yaml.snakeyaml.error.YAMLException;
/*      */ import org.yaml.snakeyaml.events.CollectionStartEvent;
/*      */ import org.yaml.snakeyaml.events.DocumentEndEvent;
/*      */ import org.yaml.snakeyaml.events.DocumentStartEvent;
/*      */ import org.yaml.snakeyaml.events.Event;
/*      */ import org.yaml.snakeyaml.events.MappingStartEvent;
/*      */ import org.yaml.snakeyaml.events.NodeEvent;
/*      */ import org.yaml.snakeyaml.events.ScalarEvent;
/*      */ import org.yaml.snakeyaml.events.SequenceStartEvent;
/*      */ import org.yaml.snakeyaml.reader.StreamReader;
/*      */ import org.yaml.snakeyaml.scanner.Constant;
/*      */ import org.yaml.snakeyaml.util.ArrayStack;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class Emitter
/*      */   implements Emitable
/*      */ {
/*      */   public static final int MIN_INDENT = 1;
/*      */   public static final int MAX_INDENT = 10;
/*   74 */   private static final char[] SPACE = new char[] { ' ' };
/*      */   
/*   76 */   private static final Pattern SPACES_PATTERN = Pattern.compile("\\s");
/*   77 */   private static final Set<Character> INVALID_ANCHOR = new HashSet<>();
/*      */   static {
/*   79 */     INVALID_ANCHOR.add(Character.valueOf('['));
/*   80 */     INVALID_ANCHOR.add(Character.valueOf(']'));
/*   81 */     INVALID_ANCHOR.add(Character.valueOf('{'));
/*   82 */     INVALID_ANCHOR.add(Character.valueOf('}'));
/*   83 */     INVALID_ANCHOR.add(Character.valueOf(','));
/*   84 */     INVALID_ANCHOR.add(Character.valueOf('*'));
/*   85 */     INVALID_ANCHOR.add(Character.valueOf('&'));
/*      */   }
/*      */   
/*   88 */   private static final Map<Character, String> ESCAPE_REPLACEMENTS = new HashMap<>();
/*      */   static {
/*   90 */     ESCAPE_REPLACEMENTS.put(Character.valueOf(false), "0");
/*   91 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('\007'), "a");
/*   92 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('\b'), "b");
/*   93 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('\t'), "t");
/*   94 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('\n'), "n");
/*   95 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('\013'), "v");
/*   96 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('\f'), "f");
/*   97 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('\r'), "r");
/*   98 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('\033'), "e");
/*   99 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('"'), "\"");
/*  100 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('\\'), "\\");
/*  101 */     ESCAPE_REPLACEMENTS.put(Character.valueOf(''), "N");
/*  102 */     ESCAPE_REPLACEMENTS.put(Character.valueOf(' '), "_");
/*  103 */     ESCAPE_REPLACEMENTS.put(Character.valueOf(' '), "L");
/*  104 */     ESCAPE_REPLACEMENTS.put(Character.valueOf(' '), "P");
/*      */   }
/*      */   
/*  107 */   private static final Map<String, String> DEFAULT_TAG_PREFIXES = new LinkedHashMap<>();
/*      */   static {
/*  109 */     DEFAULT_TAG_PREFIXES.put("!", "!");
/*  110 */     DEFAULT_TAG_PREFIXES.put("tag:yaml.org,2002:", "!!");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final Writer stream;
/*      */ 
/*      */   
/*      */   private final ArrayStack<EmitterState> states;
/*      */   
/*      */   private EmitterState state;
/*      */   
/*      */   private final Queue<Event> events;
/*      */   
/*      */   private Event event;
/*      */   
/*      */   private final ArrayStack<Integer> indents;
/*      */   
/*      */   private Integer indent;
/*      */   
/*      */   private int flowLevel;
/*      */   
/*      */   private boolean rootContext;
/*      */   
/*      */   private boolean mappingContext;
/*      */   
/*      */   private boolean simpleKeyContext;
/*      */   
/*      */   private int column;
/*      */   
/*      */   private boolean whitespace;
/*      */   
/*      */   private boolean indention;
/*      */   
/*      */   private boolean openEnded;
/*      */   
/*      */   private final Boolean canonical;
/*      */   
/*      */   private final Boolean prettyFlow;
/*      */   
/*      */   private final boolean allowUnicode;
/*      */   
/*      */   private int bestIndent;
/*      */   
/*      */   private final int indicatorIndent;
/*      */   
/*      */   private final boolean indentWithIndicator;
/*      */   
/*      */   private int bestWidth;
/*      */   
/*      */   private final char[] bestLineBreak;
/*      */   
/*      */   private final boolean splitLines;
/*      */   
/*      */   private final int maxSimpleKeyLength;
/*      */   
/*      */   private final boolean emitComments;
/*      */   
/*      */   private Map<String, String> tagPrefixes;
/*      */   
/*      */   private String preparedAnchor;
/*      */   
/*      */   private String preparedTag;
/*      */   
/*      */   private ScalarAnalysis analysis;
/*      */   
/*      */   private DumperOptions.ScalarStyle style;
/*      */   
/*      */   private final CommentEventsCollector blockCommentsCollector;
/*      */   
/*      */   private final CommentEventsCollector inlineCommentsCollector;
/*      */ 
/*      */   
/*      */   public Emitter(Writer stream, DumperOptions opts) {
/*  184 */     this.stream = stream;
/*      */ 
/*      */     
/*  187 */     this.states = new ArrayStack(100);
/*  188 */     this.state = new ExpectStreamStart();
/*      */     
/*  190 */     this.events = new ArrayBlockingQueue<>(100);
/*  191 */     this.event = null;
/*      */     
/*  193 */     this.indents = new ArrayStack(10);
/*  194 */     this.indent = null;
/*      */     
/*  196 */     this.flowLevel = 0;
/*      */     
/*  198 */     this.mappingContext = false;
/*  199 */     this.simpleKeyContext = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  207 */     this.column = 0;
/*  208 */     this.whitespace = true;
/*  209 */     this.indention = true;
/*      */ 
/*      */     
/*  212 */     this.openEnded = false;
/*      */ 
/*      */     
/*  215 */     this.canonical = Boolean.valueOf(opts.isCanonical());
/*  216 */     this.prettyFlow = Boolean.valueOf(opts.isPrettyFlow());
/*  217 */     this.allowUnicode = opts.isAllowUnicode();
/*  218 */     this.bestIndent = 2;
/*  219 */     if (opts.getIndent() > 1 && opts.getIndent() < 10) {
/*  220 */       this.bestIndent = opts.getIndent();
/*      */     }
/*  222 */     this.indicatorIndent = opts.getIndicatorIndent();
/*  223 */     this.indentWithIndicator = opts.getIndentWithIndicator();
/*  224 */     this.bestWidth = 80;
/*  225 */     if (opts.getWidth() > this.bestIndent * 2) {
/*  226 */       this.bestWidth = opts.getWidth();
/*      */     }
/*  228 */     this.bestLineBreak = opts.getLineBreak().getString().toCharArray();
/*  229 */     this.splitLines = opts.getSplitLines();
/*  230 */     this.maxSimpleKeyLength = opts.getMaxSimpleKeyLength();
/*  231 */     this.emitComments = opts.isProcessComments();
/*      */ 
/*      */     
/*  234 */     this.tagPrefixes = new LinkedHashMap<>();
/*      */ 
/*      */     
/*  237 */     this.preparedAnchor = null;
/*  238 */     this.preparedTag = null;
/*      */ 
/*      */     
/*  241 */     this.analysis = null;
/*  242 */     this.style = null;
/*      */ 
/*      */     
/*  245 */     this.blockCommentsCollector = new CommentEventsCollector(this.events, new CommentType[] { CommentType.BLANK_LINE, CommentType.BLOCK });
/*      */     
/*  247 */     this.inlineCommentsCollector = new CommentEventsCollector(this.events, new CommentType[] { CommentType.IN_LINE });
/*      */   }
/*      */ 
/*      */   
/*      */   public void emit(Event event) throws IOException {
/*  252 */     this.events.add(event);
/*  253 */     while (!needMoreEvents()) {
/*  254 */       this.event = this.events.poll();
/*  255 */       this.state.expect();
/*  256 */       this.event = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean needMoreEvents() {
/*  263 */     if (this.events.isEmpty()) {
/*  264 */       return true;
/*      */     }
/*  266 */     Iterator<Event> iter = this.events.iterator();
/*  267 */     Event event = null;
/*  268 */     while (iter.hasNext()) {
/*  269 */       event = iter.next();
/*  270 */       if (event instanceof org.yaml.snakeyaml.events.CommentEvent) {
/*      */         continue;
/*      */       }
/*  273 */       if (event instanceof DocumentStartEvent)
/*  274 */         return needEvents(iter, 1); 
/*  275 */       if (event instanceof SequenceStartEvent)
/*  276 */         return needEvents(iter, 2); 
/*  277 */       if (event instanceof MappingStartEvent)
/*  278 */         return needEvents(iter, 3); 
/*  279 */       if (event instanceof org.yaml.snakeyaml.events.StreamStartEvent)
/*  280 */         return needEvents(iter, 2); 
/*  281 */       if (event instanceof org.yaml.snakeyaml.events.StreamEndEvent) {
/*  282 */         return false;
/*      */       }
/*      */       
/*  285 */       return needEvents(iter, 1);
/*      */     } 
/*      */     
/*  288 */     return true;
/*      */   }
/*      */   
/*      */   private boolean needEvents(Iterator<Event> iter, int count) {
/*  292 */     int level = 0;
/*  293 */     int actualCount = 0;
/*  294 */     while (iter.hasNext()) {
/*  295 */       Event event = iter.next();
/*  296 */       if (event instanceof org.yaml.snakeyaml.events.CommentEvent) {
/*      */         continue;
/*      */       }
/*  299 */       actualCount++;
/*  300 */       if (event instanceof DocumentStartEvent || event instanceof CollectionStartEvent) {
/*  301 */         level++;
/*  302 */       } else if (event instanceof DocumentEndEvent || event instanceof org.yaml.snakeyaml.events.CollectionEndEvent) {
/*  303 */         level--;
/*  304 */       } else if (event instanceof org.yaml.snakeyaml.events.StreamEndEvent) {
/*  305 */         level = -1;
/*  306 */       } else if (event instanceof org.yaml.snakeyaml.events.CommentEvent) {
/*      */       
/*  308 */       }  if (level < 0) {
/*  309 */         return false;
/*      */       }
/*      */     } 
/*  312 */     return (actualCount < count);
/*      */   }
/*      */   
/*      */   private void increaseIndent(boolean flow, boolean indentless) {
/*  316 */     this.indents.push(this.indent);
/*  317 */     if (this.indent == null) {
/*  318 */       if (flow) {
/*  319 */         this.indent = Integer.valueOf(this.bestIndent);
/*      */       } else {
/*  321 */         this.indent = Integer.valueOf(0);
/*      */       } 
/*  323 */     } else if (!indentless) {
/*  324 */       Emitter emitter = this; emitter.indent = Integer.valueOf(emitter.indent.intValue() + this.bestIndent);
/*      */     } 
/*      */   }
/*      */   
/*      */   private class ExpectStreamStart
/*      */     implements EmitterState
/*      */   {
/*      */     private ExpectStreamStart() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  334 */       if (Emitter.this.event instanceof org.yaml.snakeyaml.events.StreamStartEvent) {
/*  335 */         Emitter.this.writeStreamStart();
/*  336 */         Emitter.this.state = new Emitter.ExpectFirstDocumentStart();
/*      */       } else {
/*  338 */         throw new EmitterException("expected StreamStartEvent, but got " + Emitter.this.event);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private class ExpectNothing implements EmitterState {
/*      */     public void expect() throws IOException {
/*  345 */       throw new EmitterException("expecting nothing, but got " + Emitter.this.event);
/*      */     }
/*      */     
/*      */     private ExpectNothing() {} }
/*      */   
/*      */   private class ExpectFirstDocumentStart implements EmitterState { private ExpectFirstDocumentStart() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  353 */       (new Emitter.ExpectDocumentStart(true)).expect();
/*      */     } }
/*      */ 
/*      */   
/*      */   private class ExpectDocumentStart implements EmitterState {
/*      */     private final boolean first;
/*      */     
/*      */     public ExpectDocumentStart(boolean first) {
/*  361 */       this.first = first;
/*      */     }
/*      */     
/*      */     public void expect() throws IOException {
/*  365 */       if (Emitter.this.event instanceof DocumentStartEvent) {
/*  366 */         DocumentStartEvent ev = (DocumentStartEvent)Emitter.this.event;
/*  367 */         if ((ev.getVersion() != null || ev.getTags() != null) && Emitter.this.openEnded) {
/*  368 */           Emitter.this.writeIndicator("...", true, false, false);
/*  369 */           Emitter.this.writeIndent();
/*      */         } 
/*  371 */         if (ev.getVersion() != null) {
/*  372 */           String versionText = Emitter.this.prepareVersion(ev.getVersion());
/*  373 */           Emitter.this.writeVersionDirective(versionText);
/*      */         } 
/*  375 */         Emitter.this.tagPrefixes = (Map)new LinkedHashMap<>(Emitter.DEFAULT_TAG_PREFIXES);
/*  376 */         if (ev.getTags() != null) {
/*  377 */           Set<String> handles = new TreeSet<>(ev.getTags().keySet());
/*  378 */           for (String handle : handles) {
/*  379 */             String prefix = (String)ev.getTags().get(handle);
/*  380 */             Emitter.this.tagPrefixes.put(prefix, handle);
/*  381 */             String handleText = Emitter.this.prepareTagHandle(handle);
/*  382 */             String prefixText = Emitter.this.prepareTagPrefix(prefix);
/*  383 */             Emitter.this.writeTagDirective(handleText, prefixText);
/*      */           } 
/*      */         } 
/*  386 */         boolean implicit = (this.first && !ev.getExplicit() && !Emitter.this.canonical.booleanValue() && ev.getVersion() == null && (ev.getTags() == null || ev.getTags().isEmpty()) && !Emitter.this.checkEmptyDocument());
/*      */ 
/*      */ 
/*      */         
/*  390 */         if (!implicit) {
/*  391 */           Emitter.this.writeIndent();
/*  392 */           Emitter.this.writeIndicator("---", true, false, false);
/*  393 */           if (Emitter.this.canonical.booleanValue()) {
/*  394 */             Emitter.this.writeIndent();
/*      */           }
/*      */         } 
/*  397 */         Emitter.this.state = new Emitter.ExpectDocumentRoot();
/*  398 */       } else if (Emitter.this.event instanceof org.yaml.snakeyaml.events.StreamEndEvent) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  404 */         Emitter.this.writeStreamEnd();
/*  405 */         Emitter.this.state = new Emitter.ExpectNothing();
/*  406 */       } else if (Emitter.this.event instanceof org.yaml.snakeyaml.events.CommentEvent) {
/*  407 */         Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
/*  408 */         Emitter.this.writeBlockComment();
/*      */       } else {
/*      */         
/*  411 */         throw new EmitterException("expected DocumentStartEvent, but got " + Emitter.this.event);
/*      */       } 
/*      */     } }
/*      */   
/*      */   private class ExpectDocumentEnd implements EmitterState { private ExpectDocumentEnd() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  418 */       Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
/*  419 */       Emitter.this.writeBlockComment();
/*  420 */       if (Emitter.this.event instanceof DocumentEndEvent) {
/*  421 */         Emitter.this.writeIndent();
/*  422 */         if (((DocumentEndEvent)Emitter.this.event).getExplicit()) {
/*  423 */           Emitter.this.writeIndicator("...", true, false, false);
/*  424 */           Emitter.this.writeIndent();
/*      */         } 
/*  426 */         Emitter.this.flushStream();
/*  427 */         Emitter.this.state = new Emitter.ExpectDocumentStart(false);
/*      */       } else {
/*  429 */         throw new EmitterException("expected DocumentEndEvent, but got " + Emitter.this.event);
/*      */       } 
/*      */     } }
/*      */   
/*      */   private class ExpectDocumentRoot implements EmitterState { private ExpectDocumentRoot() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  436 */       Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
/*  437 */       if (!Emitter.this.blockCommentsCollector.isEmpty()) {
/*  438 */         Emitter.this.writeBlockComment();
/*  439 */         if (Emitter.this.event instanceof DocumentEndEvent) {
/*  440 */           (new Emitter.ExpectDocumentEnd()).expect();
/*      */           return;
/*      */         } 
/*      */       } 
/*  444 */       Emitter.this.states.push(new Emitter.ExpectDocumentEnd());
/*  445 */       Emitter.this.expectNode(true, false, false);
/*      */     } }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void expectNode(boolean root, boolean mapping, boolean simpleKey) throws IOException {
/*  452 */     this.rootContext = root;
/*  453 */     this.mappingContext = mapping;
/*  454 */     this.simpleKeyContext = simpleKey;
/*  455 */     if (this.event instanceof org.yaml.snakeyaml.events.AliasEvent) {
/*  456 */       expectAlias();
/*  457 */     } else if (this.event instanceof ScalarEvent || this.event instanceof CollectionStartEvent) {
/*  458 */       processAnchor("&");
/*  459 */       processTag();
/*  460 */       if (this.event instanceof ScalarEvent) {
/*  461 */         expectScalar();
/*  462 */       } else if (this.event instanceof SequenceStartEvent) {
/*  463 */         if (this.flowLevel != 0 || this.canonical.booleanValue() || ((SequenceStartEvent)this.event).isFlow() || checkEmptySequence()) {
/*      */           
/*  465 */           expectFlowSequence();
/*      */         } else {
/*  467 */           expectBlockSequence();
/*      */         }
/*      */       
/*  470 */       } else if (this.flowLevel != 0 || this.canonical.booleanValue() || ((MappingStartEvent)this.event).isFlow() || checkEmptyMapping()) {
/*      */         
/*  472 */         expectFlowMapping();
/*      */       } else {
/*  474 */         expectBlockMapping();
/*      */       } 
/*      */     } else {
/*      */       
/*  478 */       throw new EmitterException("expected NodeEvent, but got " + this.event);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void expectAlias() throws IOException {
/*  483 */     if (!(this.event instanceof org.yaml.snakeyaml.events.AliasEvent)) {
/*  484 */       throw new EmitterException("Alias must be provided");
/*      */     }
/*  486 */     processAnchor("*");
/*  487 */     this.state = (EmitterState)this.states.pop();
/*      */   }
/*      */   
/*      */   private void expectScalar() throws IOException {
/*  491 */     increaseIndent(true, false);
/*  492 */     processScalar();
/*  493 */     this.indent = (Integer)this.indents.pop();
/*  494 */     this.state = (EmitterState)this.states.pop();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void expectFlowSequence() throws IOException {
/*  500 */     writeIndicator("[", true, true, false);
/*  501 */     this.flowLevel++;
/*  502 */     increaseIndent(true, false);
/*  503 */     if (this.prettyFlow.booleanValue()) {
/*  504 */       writeIndent();
/*      */     }
/*  506 */     this.state = new ExpectFirstFlowSequenceItem();
/*      */   }
/*      */   private class ExpectFirstFlowSequenceItem implements EmitterState { private ExpectFirstFlowSequenceItem() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  511 */       if (Emitter.this.event instanceof org.yaml.snakeyaml.events.SequenceEndEvent) {
/*  512 */         Emitter.this.indent = (Integer)Emitter.this.indents.pop();
/*  513 */         Emitter.this.flowLevel--;
/*  514 */         Emitter.this.writeIndicator("]", false, false, false);
/*  515 */         Emitter.this.inlineCommentsCollector.collectEvents();
/*  516 */         Emitter.this.writeInlineComments();
/*  517 */         Emitter.this.state = (EmitterState)Emitter.this.states.pop();
/*  518 */       } else if (Emitter.this.event instanceof org.yaml.snakeyaml.events.CommentEvent) {
/*  519 */         Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
/*  520 */         Emitter.this.writeBlockComment();
/*      */       } else {
/*  522 */         if (Emitter.this.canonical.booleanValue() || (Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines) || Emitter.this.prettyFlow.booleanValue()) {
/*  523 */           Emitter.this.writeIndent();
/*      */         }
/*  525 */         Emitter.this.states.push(new Emitter.ExpectFlowSequenceItem());
/*  526 */         Emitter.this.expectNode(false, false, false);
/*  527 */         Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
/*  528 */         Emitter.this.writeInlineComments();
/*      */       } 
/*      */     } }
/*      */   
/*      */   private class ExpectFlowSequenceItem implements EmitterState { private ExpectFlowSequenceItem() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  535 */       if (Emitter.this.event instanceof org.yaml.snakeyaml.events.SequenceEndEvent) {
/*  536 */         Emitter.this.indent = (Integer)Emitter.this.indents.pop();
/*  537 */         Emitter.this.flowLevel--;
/*  538 */         if (Emitter.this.canonical.booleanValue()) {
/*  539 */           Emitter.this.writeIndicator(",", false, false, false);
/*  540 */           Emitter.this.writeIndent();
/*      */         } 
/*  542 */         Emitter.this.writeIndicator("]", false, false, false);
/*  543 */         Emitter.this.inlineCommentsCollector.collectEvents();
/*  544 */         Emitter.this.writeInlineComments();
/*  545 */         if (Emitter.this.prettyFlow.booleanValue()) {
/*  546 */           Emitter.this.writeIndent();
/*      */         }
/*  548 */         Emitter.this.state = (EmitterState)Emitter.this.states.pop();
/*  549 */       } else if (Emitter.this.event instanceof org.yaml.snakeyaml.events.CommentEvent) {
/*  550 */         Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
/*  551 */         Emitter.this.writeBlockComment();
/*      */       } else {
/*  553 */         Emitter.this.writeIndicator(",", false, false, false);
/*  554 */         if (Emitter.this.canonical.booleanValue() || (Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines) || Emitter.this.prettyFlow.booleanValue()) {
/*  555 */           Emitter.this.writeIndent();
/*      */         }
/*  557 */         Emitter.this.states.push(new ExpectFlowSequenceItem());
/*  558 */         Emitter.this.expectNode(false, false, false);
/*  559 */         Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
/*  560 */         Emitter.this.writeInlineComments();
/*      */       } 
/*      */     } }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void expectFlowMapping() throws IOException {
/*  568 */     writeIndicator("{", true, true, false);
/*  569 */     this.flowLevel++;
/*  570 */     increaseIndent(true, false);
/*  571 */     if (this.prettyFlow.booleanValue()) {
/*  572 */       writeIndent();
/*      */     }
/*  574 */     this.state = new ExpectFirstFlowMappingKey();
/*      */   }
/*      */   private class ExpectFirstFlowMappingKey implements EmitterState { private ExpectFirstFlowMappingKey() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  579 */       if (Emitter.this.event instanceof org.yaml.snakeyaml.events.MappingEndEvent) {
/*  580 */         Emitter.this.indent = (Integer)Emitter.this.indents.pop();
/*  581 */         Emitter.this.flowLevel--;
/*  582 */         Emitter.this.writeIndicator("}", false, false, false);
/*  583 */         Emitter.this.inlineCommentsCollector.collectEvents();
/*  584 */         Emitter.this.writeInlineComments();
/*  585 */         Emitter.this.state = (EmitterState)Emitter.this.states.pop();
/*      */       } else {
/*  587 */         if (Emitter.this.canonical.booleanValue() || (Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines) || Emitter.this.prettyFlow.booleanValue()) {
/*  588 */           Emitter.this.writeIndent();
/*      */         }
/*  590 */         if (!Emitter.this.canonical.booleanValue() && Emitter.this.checkSimpleKey()) {
/*  591 */           Emitter.this.states.push(new Emitter.ExpectFlowMappingSimpleValue());
/*  592 */           Emitter.this.expectNode(false, true, true);
/*      */         } else {
/*  594 */           Emitter.this.writeIndicator("?", true, false, false);
/*  595 */           Emitter.this.states.push(new Emitter.ExpectFlowMappingValue());
/*  596 */           Emitter.this.expectNode(false, true, false);
/*      */         } 
/*      */       } 
/*      */     } }
/*      */   
/*      */   private class ExpectFlowMappingKey implements EmitterState { private ExpectFlowMappingKey() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  604 */       if (Emitter.this.event instanceof org.yaml.snakeyaml.events.MappingEndEvent) {
/*  605 */         Emitter.this.indent = (Integer)Emitter.this.indents.pop();
/*  606 */         Emitter.this.flowLevel--;
/*  607 */         if (Emitter.this.canonical.booleanValue()) {
/*  608 */           Emitter.this.writeIndicator(",", false, false, false);
/*  609 */           Emitter.this.writeIndent();
/*      */         } 
/*  611 */         if (Emitter.this.prettyFlow.booleanValue()) {
/*  612 */           Emitter.this.writeIndent();
/*      */         }
/*  614 */         Emitter.this.writeIndicator("}", false, false, false);
/*  615 */         Emitter.this.inlineCommentsCollector.collectEvents();
/*  616 */         Emitter.this.writeInlineComments();
/*  617 */         Emitter.this.state = (EmitterState)Emitter.this.states.pop();
/*      */       } else {
/*  619 */         Emitter.this.writeIndicator(",", false, false, false);
/*  620 */         if (Emitter.this.canonical.booleanValue() || (Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines) || Emitter.this.prettyFlow.booleanValue()) {
/*  621 */           Emitter.this.writeIndent();
/*      */         }
/*  623 */         if (!Emitter.this.canonical.booleanValue() && Emitter.this.checkSimpleKey()) {
/*  624 */           Emitter.this.states.push(new Emitter.ExpectFlowMappingSimpleValue());
/*  625 */           Emitter.this.expectNode(false, true, true);
/*      */         } else {
/*  627 */           Emitter.this.writeIndicator("?", true, false, false);
/*  628 */           Emitter.this.states.push(new Emitter.ExpectFlowMappingValue());
/*  629 */           Emitter.this.expectNode(false, true, false);
/*      */         } 
/*      */       } 
/*      */     } }
/*      */   
/*      */   private class ExpectFlowMappingSimpleValue implements EmitterState { private ExpectFlowMappingSimpleValue() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  637 */       Emitter.this.writeIndicator(":", false, false, false);
/*  638 */       Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
/*  639 */       Emitter.this.writeInlineComments();
/*  640 */       Emitter.this.states.push(new Emitter.ExpectFlowMappingKey());
/*  641 */       Emitter.this.expectNode(false, true, false);
/*  642 */       Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
/*  643 */       Emitter.this.writeInlineComments();
/*      */     } }
/*      */   
/*      */   private class ExpectFlowMappingValue implements EmitterState { private ExpectFlowMappingValue() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  649 */       if (Emitter.this.canonical.booleanValue() || Emitter.this.column > Emitter.this.bestWidth || Emitter.this.prettyFlow.booleanValue()) {
/*  650 */         Emitter.this.writeIndent();
/*      */       }
/*  652 */       Emitter.this.writeIndicator(":", true, false, false);
/*  653 */       Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
/*  654 */       Emitter.this.writeInlineComments();
/*  655 */       Emitter.this.states.push(new Emitter.ExpectFlowMappingKey());
/*  656 */       Emitter.this.expectNode(false, true, false);
/*  657 */       Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
/*  658 */       Emitter.this.writeInlineComments();
/*      */     } }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void expectBlockSequence() throws IOException {
/*  665 */     boolean indentless = (this.mappingContext && !this.indention);
/*  666 */     increaseIndent(false, indentless);
/*  667 */     this.state = new ExpectFirstBlockSequenceItem();
/*      */   }
/*      */   private class ExpectFirstBlockSequenceItem implements EmitterState { private ExpectFirstBlockSequenceItem() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  672 */       (new Emitter.ExpectBlockSequenceItem(true)).expect();
/*      */     } }
/*      */ 
/*      */   
/*      */   private class ExpectBlockSequenceItem implements EmitterState {
/*      */     private final boolean first;
/*      */     
/*      */     public ExpectBlockSequenceItem(boolean first) {
/*  680 */       this.first = first;
/*      */     }
/*      */     
/*      */     public void expect() throws IOException {
/*  684 */       if (!this.first && Emitter.this.event instanceof org.yaml.snakeyaml.events.SequenceEndEvent) {
/*  685 */         Emitter.this.indent = (Integer)Emitter.this.indents.pop();
/*  686 */         Emitter.this.state = (EmitterState)Emitter.this.states.pop();
/*  687 */       } else if (Emitter.this.event instanceof org.yaml.snakeyaml.events.CommentEvent) {
/*  688 */         Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
/*      */       } else {
/*  690 */         Emitter.this.writeIndent();
/*  691 */         if (!Emitter.this.indentWithIndicator || this.first) {
/*  692 */           Emitter.this.writeWhitespace(Emitter.this.indicatorIndent);
/*      */         }
/*  694 */         Emitter.this.writeIndicator("-", true, false, true);
/*  695 */         if (Emitter.this.indentWithIndicator && this.first) {
/*  696 */           Emitter.this.indent = Integer.valueOf(Emitter.this.indent.intValue() + Emitter.this.indicatorIndent);
/*      */         }
/*  698 */         if (!Emitter.this.blockCommentsCollector.isEmpty()) {
/*  699 */           Emitter.this.increaseIndent(false, false);
/*  700 */           Emitter.this.writeBlockComment();
/*  701 */           if (Emitter.this.event instanceof ScalarEvent) {
/*  702 */             Emitter.this.analysis = Emitter.this.analyzeScalar(((ScalarEvent)Emitter.this.event).getValue());
/*  703 */             if (!Emitter.this.analysis.isEmpty()) {
/*  704 */               Emitter.this.writeIndent();
/*      */             }
/*      */           } 
/*  707 */           Emitter.this.indent = (Integer)Emitter.this.indents.pop();
/*      */         } 
/*  709 */         Emitter.this.states.push(new ExpectBlockSequenceItem(false));
/*  710 */         Emitter.this.expectNode(false, false, false);
/*  711 */         Emitter.this.inlineCommentsCollector.collectEvents();
/*  712 */         Emitter.this.writeInlineComments();
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void expectBlockMapping() throws IOException {
/*  719 */     increaseIndent(false, false);
/*  720 */     this.state = new ExpectFirstBlockMappingKey();
/*      */   }
/*      */   private class ExpectFirstBlockMappingKey implements EmitterState { private ExpectFirstBlockMappingKey() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  725 */       (new Emitter.ExpectBlockMappingKey(true)).expect();
/*      */     } }
/*      */ 
/*      */   
/*      */   private class ExpectBlockMappingKey implements EmitterState {
/*      */     private final boolean first;
/*      */     
/*      */     public ExpectBlockMappingKey(boolean first) {
/*  733 */       this.first = first;
/*      */     }
/*      */     
/*      */     public void expect() throws IOException {
/*  737 */       Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
/*  738 */       Emitter.this.writeBlockComment();
/*  739 */       if (!this.first && Emitter.this.event instanceof org.yaml.snakeyaml.events.MappingEndEvent) {
/*  740 */         Emitter.this.indent = (Integer)Emitter.this.indents.pop();
/*  741 */         Emitter.this.state = (EmitterState)Emitter.this.states.pop();
/*      */       } else {
/*  743 */         Emitter.this.writeIndent();
/*  744 */         if (Emitter.this.checkSimpleKey()) {
/*  745 */           Emitter.this.states.push(new Emitter.ExpectBlockMappingSimpleValue());
/*  746 */           Emitter.this.expectNode(false, true, true);
/*      */         } else {
/*  748 */           Emitter.this.writeIndicator("?", true, false, true);
/*  749 */           Emitter.this.states.push(new Emitter.ExpectBlockMappingValue());
/*  750 */           Emitter.this.expectNode(false, true, false);
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private boolean isFoldedOrLiteral(Event event) {
/*  757 */     if (!event.is(Event.ID.Scalar)) {
/*  758 */       return false;
/*      */     }
/*  760 */     ScalarEvent scalarEvent = (ScalarEvent)event;
/*  761 */     DumperOptions.ScalarStyle style = scalarEvent.getScalarStyle();
/*  762 */     return (style == DumperOptions.ScalarStyle.FOLDED || style == DumperOptions.ScalarStyle.LITERAL);
/*      */   }
/*      */   private class ExpectBlockMappingSimpleValue implements EmitterState { private ExpectBlockMappingSimpleValue() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  767 */       Emitter.this.writeIndicator(":", false, false, false);
/*  768 */       Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
/*  769 */       if (!Emitter.this.isFoldedOrLiteral(Emitter.this.event) && 
/*  770 */         Emitter.this.writeInlineComments()) {
/*  771 */         Emitter.this.increaseIndent(true, false);
/*  772 */         Emitter.this.writeIndent();
/*  773 */         Emitter.this.indent = (Integer)Emitter.this.indents.pop();
/*      */       } 
/*      */       
/*  776 */       Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
/*  777 */       if (!Emitter.this.blockCommentsCollector.isEmpty()) {
/*  778 */         Emitter.this.increaseIndent(true, false);
/*  779 */         Emitter.this.writeBlockComment();
/*  780 */         Emitter.this.writeIndent();
/*  781 */         Emitter.this.indent = (Integer)Emitter.this.indents.pop();
/*      */       } 
/*  783 */       Emitter.this.states.push(new Emitter.ExpectBlockMappingKey(false));
/*  784 */       Emitter.this.expectNode(false, true, false);
/*  785 */       Emitter.this.inlineCommentsCollector.collectEvents();
/*  786 */       Emitter.this.writeInlineComments();
/*      */     } }
/*      */   
/*      */   private class ExpectBlockMappingValue implements EmitterState { private ExpectBlockMappingValue() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  792 */       Emitter.this.writeIndent();
/*  793 */       Emitter.this.writeIndicator(":", true, false, true);
/*  794 */       Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
/*  795 */       Emitter.this.writeInlineComments();
/*  796 */       Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
/*  797 */       Emitter.this.writeBlockComment();
/*  798 */       Emitter.this.states.push(new Emitter.ExpectBlockMappingKey(false));
/*  799 */       Emitter.this.expectNode(false, true, false);
/*  800 */       Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
/*  801 */       Emitter.this.writeInlineComments();
/*      */     } }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean checkEmptySequence() {
/*  808 */     return (this.event instanceof SequenceStartEvent && !this.events.isEmpty() && this.events.peek() instanceof org.yaml.snakeyaml.events.SequenceEndEvent);
/*      */   }
/*      */   
/*      */   private boolean checkEmptyMapping() {
/*  812 */     return (this.event instanceof MappingStartEvent && !this.events.isEmpty() && this.events.peek() instanceof org.yaml.snakeyaml.events.MappingEndEvent);
/*      */   }
/*      */   
/*      */   private boolean checkEmptyDocument() {
/*  816 */     if (!(this.event instanceof DocumentStartEvent) || this.events.isEmpty()) {
/*  817 */       return false;
/*      */     }
/*  819 */     Event event = this.events.peek();
/*  820 */     if (event instanceof ScalarEvent) {
/*  821 */       ScalarEvent e = (ScalarEvent)event;
/*  822 */       return (e.getAnchor() == null && e.getTag() == null && e.getImplicit() != null && e.getValue().length() == 0);
/*      */     } 
/*      */     
/*  825 */     return false;
/*      */   }
/*      */   
/*      */   private boolean checkSimpleKey() {
/*  829 */     int length = 0;
/*  830 */     if (this.event instanceof NodeEvent && ((NodeEvent)this.event).getAnchor() != null) {
/*  831 */       if (this.preparedAnchor == null) {
/*  832 */         this.preparedAnchor = prepareAnchor(((NodeEvent)this.event).getAnchor());
/*      */       }
/*  834 */       length += this.preparedAnchor.length();
/*      */     } 
/*  836 */     String tag = null;
/*  837 */     if (this.event instanceof ScalarEvent) {
/*  838 */       tag = ((ScalarEvent)this.event).getTag();
/*  839 */     } else if (this.event instanceof CollectionStartEvent) {
/*  840 */       tag = ((CollectionStartEvent)this.event).getTag();
/*      */     } 
/*  842 */     if (tag != null) {
/*  843 */       if (this.preparedTag == null) {
/*  844 */         this.preparedTag = prepareTag(tag);
/*      */       }
/*  846 */       length += this.preparedTag.length();
/*      */     } 
/*  848 */     if (this.event instanceof ScalarEvent) {
/*  849 */       if (this.analysis == null) {
/*  850 */         this.analysis = analyzeScalar(((ScalarEvent)this.event).getValue());
/*      */       }
/*  852 */       length += this.analysis.getScalar().length();
/*      */     } 
/*  854 */     return (length < this.maxSimpleKeyLength && (this.event instanceof org.yaml.snakeyaml.events.AliasEvent || (this.event instanceof ScalarEvent && !this.analysis.isEmpty() && !this.analysis.isMultiline()) || checkEmptySequence() || checkEmptyMapping()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void processAnchor(String indicator) throws IOException {
/*  862 */     NodeEvent ev = (NodeEvent)this.event;
/*  863 */     if (ev.getAnchor() == null) {
/*  864 */       this.preparedAnchor = null;
/*      */       return;
/*      */     } 
/*  867 */     if (this.preparedAnchor == null) {
/*  868 */       this.preparedAnchor = prepareAnchor(ev.getAnchor());
/*      */     }
/*  870 */     writeIndicator(indicator + this.preparedAnchor, true, false, false);
/*  871 */     this.preparedAnchor = null;
/*      */   }
/*      */   
/*      */   private void processTag() throws IOException {
/*  875 */     String tag = null;
/*  876 */     if (this.event instanceof ScalarEvent) {
/*  877 */       ScalarEvent ev = (ScalarEvent)this.event;
/*  878 */       tag = ev.getTag();
/*  879 */       if (this.style == null) {
/*  880 */         this.style = chooseScalarStyle();
/*      */       }
/*  882 */       if ((!this.canonical.booleanValue() || tag == null) && ((this.style == null && ev.getImplicit().canOmitTagInPlainScalar()) || (this.style != null && ev.getImplicit().canOmitTagInNonPlainScalar()))) {
/*      */ 
/*      */         
/*  885 */         this.preparedTag = null;
/*      */         return;
/*      */       } 
/*  888 */       if (ev.getImplicit().canOmitTagInPlainScalar() && tag == null) {
/*  889 */         tag = "!";
/*  890 */         this.preparedTag = null;
/*      */       } 
/*      */     } else {
/*  893 */       CollectionStartEvent ev = (CollectionStartEvent)this.event;
/*  894 */       tag = ev.getTag();
/*  895 */       if ((!this.canonical.booleanValue() || tag == null) && ev.getImplicit()) {
/*  896 */         this.preparedTag = null;
/*      */         return;
/*      */       } 
/*      */     } 
/*  900 */     if (tag == null) {
/*  901 */       throw new EmitterException("tag is not specified");
/*      */     }
/*  903 */     if (this.preparedTag == null) {
/*  904 */       this.preparedTag = prepareTag(tag);
/*      */     }
/*  906 */     writeIndicator(this.preparedTag, true, false, false);
/*  907 */     this.preparedTag = null;
/*      */   }
/*      */   
/*      */   private DumperOptions.ScalarStyle chooseScalarStyle() {
/*  911 */     ScalarEvent ev = (ScalarEvent)this.event;
/*  912 */     if (this.analysis == null) {
/*  913 */       this.analysis = analyzeScalar(ev.getValue());
/*      */     }
/*  915 */     if ((!ev.isPlain() && ev.getScalarStyle() == DumperOptions.ScalarStyle.DOUBLE_QUOTED) || this.canonical.booleanValue()) {
/*  916 */       return DumperOptions.ScalarStyle.DOUBLE_QUOTED;
/*      */     }
/*  918 */     if (ev.isPlain() && ev.getImplicit().canOmitTagInPlainScalar() && (
/*  919 */       !this.simpleKeyContext || (!this.analysis.isEmpty() && !this.analysis.isMultiline())) && ((this.flowLevel != 0 && this.analysis.isAllowFlowPlain()) || (this.flowLevel == 0 && this.analysis.isAllowBlockPlain())))
/*      */     {
/*  921 */       return null;
/*      */     }
/*      */     
/*  924 */     if (!ev.isPlain() && (ev.getScalarStyle() == DumperOptions.ScalarStyle.LITERAL || ev.getScalarStyle() == DumperOptions.ScalarStyle.FOLDED) && 
/*  925 */       this.flowLevel == 0 && !this.simpleKeyContext && this.analysis.isAllowBlock()) {
/*  926 */       return ev.getScalarStyle();
/*      */     }
/*      */     
/*  929 */     if ((ev.isPlain() || ev.getScalarStyle() == DumperOptions.ScalarStyle.SINGLE_QUOTED) && 
/*  930 */       this.analysis.isAllowSingleQuoted() && (!this.simpleKeyContext || !this.analysis.isMultiline())) {
/*  931 */       return DumperOptions.ScalarStyle.SINGLE_QUOTED;
/*      */     }
/*      */     
/*  934 */     return DumperOptions.ScalarStyle.DOUBLE_QUOTED;
/*      */   }
/*      */   
/*      */   private void processScalar() throws IOException {
/*  938 */     ScalarEvent ev = (ScalarEvent)this.event;
/*  939 */     if (this.analysis == null) {
/*  940 */       this.analysis = analyzeScalar(ev.getValue());
/*      */     }
/*  942 */     if (this.style == null) {
/*  943 */       this.style = chooseScalarStyle();
/*      */     }
/*  945 */     boolean split = (!this.simpleKeyContext && this.splitLines);
/*  946 */     if (this.style == null) {
/*  947 */       writePlain(this.analysis.getScalar(), split);
/*      */     } else {
/*  949 */       switch (this.style) {
/*      */         case DOUBLE_QUOTED:
/*  951 */           writeDoubleQuoted(this.analysis.getScalar(), split);
/*      */           break;
/*      */         case SINGLE_QUOTED:
/*  954 */           writeSingleQuoted(this.analysis.getScalar(), split);
/*      */           break;
/*      */         case FOLDED:
/*  957 */           writeFolded(this.analysis.getScalar(), split);
/*      */           break;
/*      */         case LITERAL:
/*  960 */           writeLiteral(this.analysis.getScalar());
/*      */           break;
/*      */         default:
/*  963 */           throw new YAMLException("Unexpected style: " + this.style);
/*      */       } 
/*      */     } 
/*  966 */     this.analysis = null;
/*  967 */     this.style = null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private String prepareVersion(DumperOptions.Version version) {
/*  973 */     if (version.major() != 1) {
/*  974 */       throw new EmitterException("unsupported YAML version: " + version);
/*      */     }
/*  976 */     return version.getRepresentation();
/*      */   }
/*      */   
/*  979 */   private static final Pattern HANDLE_FORMAT = Pattern.compile("^![-_\\w]*!$");
/*      */   
/*      */   private String prepareTagHandle(String handle) {
/*  982 */     if (handle.length() == 0)
/*  983 */       throw new EmitterException("tag handle must not be empty"); 
/*  984 */     if (handle.charAt(0) != '!' || handle.charAt(handle.length() - 1) != '!')
/*  985 */       throw new EmitterException("tag handle must start and end with '!': " + handle); 
/*  986 */     if (!"!".equals(handle) && !HANDLE_FORMAT.matcher(handle).matches()) {
/*  987 */       throw new EmitterException("invalid character in the tag handle: " + handle);
/*      */     }
/*  989 */     return handle;
/*      */   }
/*      */   
/*      */   private String prepareTagPrefix(String prefix) {
/*  993 */     if (prefix.length() == 0) {
/*  994 */       throw new EmitterException("tag prefix must not be empty");
/*      */     }
/*  996 */     StringBuilder chunks = new StringBuilder();
/*  997 */     int start = 0;
/*  998 */     int end = 0;
/*  999 */     if (prefix.charAt(0) == '!') {
/* 1000 */       end = 1;
/*      */     }
/* 1002 */     while (end < prefix.length()) {
/* 1003 */       end++;
/*      */     }
/* 1005 */     if (start < end) {
/* 1006 */       chunks.append(prefix, start, end);
/*      */     }
/* 1008 */     return chunks.toString();
/*      */   }
/*      */   
/*      */   private String prepareTag(String tag) {
/* 1012 */     if (tag.length() == 0) {
/* 1013 */       throw new EmitterException("tag must not be empty");
/*      */     }
/* 1015 */     if ("!".equals(tag)) {
/* 1016 */       return tag;
/*      */     }
/* 1018 */     String handle = null;
/* 1019 */     String suffix = tag;
/*      */     
/* 1021 */     for (String prefix : this.tagPrefixes.keySet()) {
/* 1022 */       if (tag.startsWith(prefix) && ("!".equals(prefix) || prefix.length() < tag.length())) {
/* 1023 */         handle = prefix;
/*      */       }
/*      */     } 
/* 1026 */     if (handle != null) {
/* 1027 */       suffix = tag.substring(handle.length());
/* 1028 */       handle = this.tagPrefixes.get(handle);
/*      */     } 
/*      */     
/* 1031 */     int end = suffix.length();
/* 1032 */     String suffixText = (end > 0) ? suffix.substring(0, end) : "";
/*      */     
/* 1034 */     if (handle != null) {
/* 1035 */       return handle + suffixText;
/*      */     }
/* 1037 */     return "!<" + suffixText + ">";
/*      */   }
/*      */   
/*      */   static String prepareAnchor(String anchor) {
/* 1041 */     if (anchor.length() == 0) {
/* 1042 */       throw new EmitterException("anchor must not be empty");
/*      */     }
/* 1044 */     for (Character invalid : INVALID_ANCHOR) {
/* 1045 */       if (anchor.indexOf(invalid.charValue()) > -1) {
/* 1046 */         throw new EmitterException("Invalid character '" + invalid + "' in the anchor: " + anchor);
/*      */       }
/*      */     } 
/* 1049 */     Matcher matcher = SPACES_PATTERN.matcher(anchor);
/* 1050 */     if (matcher.find()) {
/* 1051 */       throw new EmitterException("Anchor may not contain spaces: " + anchor);
/*      */     }
/* 1053 */     return anchor;
/*      */   }
/*      */ 
/*      */   
/*      */   private ScalarAnalysis analyzeScalar(String scalar) {
/* 1058 */     if (scalar.length() == 0) {
/* 1059 */       return new ScalarAnalysis(scalar, true, false, false, true, true, false);
/*      */     }
/*      */     
/* 1062 */     boolean blockIndicators = false;
/* 1063 */     boolean flowIndicators = false;
/* 1064 */     boolean lineBreaks = false;
/* 1065 */     boolean specialCharacters = false;
/*      */ 
/*      */     
/* 1068 */     boolean leadingSpace = false;
/* 1069 */     boolean leadingBreak = false;
/* 1070 */     boolean trailingSpace = false;
/* 1071 */     boolean trailingBreak = false;
/* 1072 */     boolean breakSpace = false;
/* 1073 */     boolean spaceBreak = false;
/*      */ 
/*      */     
/* 1076 */     if (scalar.startsWith("---") || scalar.startsWith("...")) {
/* 1077 */       blockIndicators = true;
/* 1078 */       flowIndicators = true;
/*      */     } 
/*      */     
/* 1081 */     boolean preceededByWhitespace = true;
/* 1082 */     boolean followedByWhitespace = (scalar.length() == 1 || Constant.NULL_BL_T_LINEBR.has(scalar.codePointAt(1)));
/*      */     
/* 1084 */     boolean previousSpace = false;
/*      */ 
/*      */     
/* 1087 */     boolean previousBreak = false;
/*      */     
/* 1089 */     int index = 0;
/*      */     
/* 1091 */     while (index < scalar.length()) {
/* 1092 */       int c = scalar.codePointAt(index);
/*      */       
/* 1094 */       if (index == 0) {
/*      */         
/* 1096 */         if ("#,[]{}&*!|>'\"%@`".indexOf(c) != -1) {
/* 1097 */           flowIndicators = true;
/* 1098 */           blockIndicators = true;
/*      */         } 
/* 1100 */         if (c == 63 || c == 58) {
/* 1101 */           flowIndicators = true;
/* 1102 */           if (followedByWhitespace) {
/* 1103 */             blockIndicators = true;
/*      */           }
/*      */         } 
/* 1106 */         if (c == 45 && followedByWhitespace) {
/* 1107 */           flowIndicators = true;
/* 1108 */           blockIndicators = true;
/*      */         } 
/*      */       } else {
/*      */         
/* 1112 */         if (",?[]{}".indexOf(c) != -1) {
/* 1113 */           flowIndicators = true;
/*      */         }
/* 1115 */         if (c == 58) {
/* 1116 */           flowIndicators = true;
/* 1117 */           if (followedByWhitespace) {
/* 1118 */             blockIndicators = true;
/*      */           }
/*      */         } 
/* 1121 */         if (c == 35 && preceededByWhitespace) {
/* 1122 */           flowIndicators = true;
/* 1123 */           blockIndicators = true;
/*      */         } 
/*      */       } 
/*      */       
/* 1127 */       boolean isLineBreak = Constant.LINEBR.has(c);
/* 1128 */       if (isLineBreak) {
/* 1129 */         lineBreaks = true;
/*      */       }
/* 1131 */       if (c != 10 && (32 > c || c > 126)) {
/* 1132 */         if (c == 133 || (c >= 160 && c <= 55295) || (c >= 57344 && c <= 65533) || (c >= 65536 && c <= 1114111)) {
/*      */ 
/*      */ 
/*      */           
/* 1136 */           if (!this.allowUnicode) {
/* 1137 */             specialCharacters = true;
/*      */           }
/*      */         } else {
/* 1140 */           specialCharacters = true;
/*      */         } 
/*      */       }
/*      */       
/* 1144 */       if (c == 32) {
/* 1145 */         if (index == 0) {
/* 1146 */           leadingSpace = true;
/*      */         }
/* 1148 */         if (index == scalar.length() - 1) {
/* 1149 */           trailingSpace = true;
/*      */         }
/* 1151 */         if (previousBreak) {
/* 1152 */           breakSpace = true;
/*      */         }
/* 1154 */         previousSpace = true;
/* 1155 */         previousBreak = false;
/* 1156 */       } else if (isLineBreak) {
/* 1157 */         if (index == 0) {
/* 1158 */           leadingBreak = true;
/*      */         }
/* 1160 */         if (index == scalar.length() - 1) {
/* 1161 */           trailingBreak = true;
/*      */         }
/* 1163 */         if (previousSpace) {
/* 1164 */           spaceBreak = true;
/*      */         }
/* 1166 */         previousSpace = false;
/* 1167 */         previousBreak = true;
/*      */       } else {
/* 1169 */         previousSpace = false;
/* 1170 */         previousBreak = false;
/*      */       } 
/*      */ 
/*      */       
/* 1174 */       index += Character.charCount(c);
/* 1175 */       preceededByWhitespace = (Constant.NULL_BL_T.has(c) || isLineBreak);
/* 1176 */       followedByWhitespace = true;
/* 1177 */       if (index + 1 < scalar.length()) {
/* 1178 */         int nextIndex = index + Character.charCount(scalar.codePointAt(index));
/* 1179 */         if (nextIndex < scalar.length()) {
/* 1180 */           followedByWhitespace = (Constant.NULL_BL_T.has(scalar.codePointAt(nextIndex)) || isLineBreak);
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1185 */     boolean allowFlowPlain = true;
/* 1186 */     boolean allowBlockPlain = true;
/* 1187 */     boolean allowSingleQuoted = true;
/* 1188 */     boolean allowBlock = true;
/*      */     
/* 1190 */     if (leadingSpace || leadingBreak || trailingSpace || trailingBreak) {
/* 1191 */       allowFlowPlain = allowBlockPlain = false;
/*      */     }
/*      */     
/* 1194 */     if (trailingSpace) {
/* 1195 */       allowBlock = false;
/*      */     }
/*      */ 
/*      */     
/* 1199 */     if (breakSpace) {
/* 1200 */       allowFlowPlain = allowBlockPlain = allowSingleQuoted = false;
/*      */     }
/*      */ 
/*      */     
/* 1204 */     if (spaceBreak || specialCharacters) {
/* 1205 */       allowFlowPlain = allowBlockPlain = allowSingleQuoted = allowBlock = false;
/*      */     }
/*      */ 
/*      */     
/* 1209 */     if (lineBreaks) {
/* 1210 */       allowFlowPlain = false;
/*      */     }
/*      */     
/* 1213 */     if (flowIndicators) {
/* 1214 */       allowFlowPlain = false;
/*      */     }
/*      */     
/* 1217 */     if (blockIndicators) {
/* 1218 */       allowBlockPlain = false;
/*      */     }
/*      */     
/* 1221 */     return new ScalarAnalysis(scalar, false, lineBreaks, allowFlowPlain, allowBlockPlain, allowSingleQuoted, allowBlock);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void flushStream() throws IOException {
/* 1228 */     this.stream.flush();
/*      */   }
/*      */ 
/*      */   
/*      */   void writeStreamStart() {}
/*      */ 
/*      */   
/*      */   void writeStreamEnd() throws IOException {
/* 1236 */     flushStream();
/*      */   }
/*      */ 
/*      */   
/*      */   void writeIndicator(String indicator, boolean needWhitespace, boolean whitespace, boolean indentation) throws IOException {
/* 1241 */     if (!this.whitespace && needWhitespace) {
/* 1242 */       this.column++;
/* 1243 */       this.stream.write(SPACE);
/*      */     } 
/* 1245 */     this.whitespace = whitespace;
/* 1246 */     this.indention = (this.indention && indentation);
/* 1247 */     this.column += indicator.length();
/* 1248 */     this.openEnded = false;
/* 1249 */     this.stream.write(indicator);
/*      */   }
/*      */   
/*      */   void writeIndent() throws IOException {
/*      */     int indent;
/* 1254 */     if (this.indent != null) {
/* 1255 */       indent = this.indent.intValue();
/*      */     } else {
/* 1257 */       indent = 0;
/*      */     } 
/*      */     
/* 1260 */     if (!this.indention || this.column > indent || (this.column == indent && !this.whitespace)) {
/* 1261 */       writeLineBreak(null);
/*      */     }
/*      */     
/* 1264 */     writeWhitespace(indent - this.column);
/*      */   }
/*      */   
/*      */   private void writeWhitespace(int length) throws IOException {
/* 1268 */     if (length <= 0) {
/*      */       return;
/*      */     }
/* 1271 */     this.whitespace = true;
/* 1272 */     char[] data = new char[length];
/* 1273 */     for (int i = 0; i < data.length; i++) {
/* 1274 */       data[i] = ' ';
/*      */     }
/* 1276 */     this.column += length;
/* 1277 */     this.stream.write(data);
/*      */   }
/*      */   
/*      */   private void writeLineBreak(String data) throws IOException {
/* 1281 */     this.whitespace = true;
/* 1282 */     this.indention = true;
/* 1283 */     this.column = 0;
/* 1284 */     if (data == null) {
/* 1285 */       this.stream.write(this.bestLineBreak);
/*      */     } else {
/* 1287 */       this.stream.write(data);
/*      */     } 
/*      */   }
/*      */   
/*      */   void writeVersionDirective(String versionText) throws IOException {
/* 1292 */     this.stream.write("%YAML ");
/* 1293 */     this.stream.write(versionText);
/* 1294 */     writeLineBreak(null);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   void writeTagDirective(String handleText, String prefixText) throws IOException {
/* 1300 */     this.stream.write("%TAG ");
/* 1301 */     this.stream.write(handleText);
/* 1302 */     this.stream.write(SPACE);
/* 1303 */     this.stream.write(prefixText);
/* 1304 */     writeLineBreak(null);
/*      */   }
/*      */ 
/*      */   
/*      */   private void writeSingleQuoted(String text, boolean split) throws IOException {
/* 1309 */     writeIndicator("'", true, false, false);
/* 1310 */     boolean spaces = false;
/* 1311 */     boolean breaks = false;
/* 1312 */     int start = 0, end = 0;
/*      */     
/* 1314 */     while (end <= text.length()) {
/* 1315 */       char ch = Character.MIN_VALUE;
/* 1316 */       if (end < text.length()) {
/* 1317 */         ch = text.charAt(end);
/*      */       }
/* 1319 */       if (spaces) {
/* 1320 */         if (ch == '\000' || ch != ' ') {
/* 1321 */           if (start + 1 == end && this.column > this.bestWidth && split && start != 0 && end != text.length()) {
/*      */             
/* 1323 */             writeIndent();
/*      */           } else {
/* 1325 */             int len = end - start;
/* 1326 */             this.column += len;
/* 1327 */             this.stream.write(text, start, len);
/*      */           } 
/* 1329 */           start = end;
/*      */         } 
/* 1331 */       } else if (breaks) {
/* 1332 */         if (ch == '\000' || Constant.LINEBR.hasNo(ch)) {
/* 1333 */           if (text.charAt(start) == '\n') {
/* 1334 */             writeLineBreak(null);
/*      */           }
/* 1336 */           String data = text.substring(start, end);
/* 1337 */           for (char br : data.toCharArray()) {
/* 1338 */             if (br == '\n') {
/* 1339 */               writeLineBreak(null);
/*      */             } else {
/* 1341 */               writeLineBreak(String.valueOf(br));
/*      */             } 
/*      */           } 
/* 1344 */           writeIndent();
/* 1345 */           start = end;
/*      */         }
/*      */       
/* 1348 */       } else if (Constant.LINEBR.has(ch, "\000 '") && 
/* 1349 */         start < end) {
/* 1350 */         int len = end - start;
/* 1351 */         this.column += len;
/* 1352 */         this.stream.write(text, start, len);
/* 1353 */         start = end;
/*      */       } 
/*      */ 
/*      */       
/* 1357 */       if (ch == '\'') {
/* 1358 */         this.column += 2;
/* 1359 */         this.stream.write("''");
/* 1360 */         start = end + 1;
/*      */       } 
/* 1362 */       if (ch != '\000') {
/* 1363 */         spaces = (ch == ' ');
/* 1364 */         breaks = Constant.LINEBR.has(ch);
/*      */       } 
/* 1366 */       end++;
/*      */     } 
/* 1368 */     writeIndicator("'", false, false, false);
/*      */   }
/*      */   
/*      */   private void writeDoubleQuoted(String text, boolean split) throws IOException {
/* 1372 */     writeIndicator("\"", true, false, false);
/* 1373 */     int start = 0;
/* 1374 */     int end = 0;
/* 1375 */     while (end <= text.length()) {
/* 1376 */       Character ch = null;
/* 1377 */       if (end < text.length()) {
/* 1378 */         ch = Character.valueOf(text.charAt(end));
/*      */       }
/* 1380 */       if (ch == null || "\"\\  ﻿".indexOf(ch.charValue()) != -1 || ' ' > ch.charValue() || ch.charValue() > '~') {
/*      */         
/* 1382 */         if (start < end) {
/* 1383 */           int len = end - start;
/* 1384 */           this.column += len;
/* 1385 */           this.stream.write(text, start, len);
/* 1386 */           start = end;
/*      */         } 
/* 1388 */         if (ch != null) {
/*      */           String data;
/* 1390 */           if (ESCAPE_REPLACEMENTS.containsKey(ch)) {
/* 1391 */             data = "\\" + (String)ESCAPE_REPLACEMENTS.get(ch);
/* 1392 */           } else if (!this.allowUnicode || !StreamReader.isPrintable(ch.charValue())) {
/*      */ 
/*      */             
/* 1395 */             if (ch.charValue() <= 'ÿ') {
/* 1396 */               String s = "0" + Integer.toString(ch.charValue(), 16);
/* 1397 */               data = "\\x" + s.substring(s.length() - 2);
/* 1398 */             } else if (ch.charValue() >= '?' && ch.charValue() <= '?') {
/* 1399 */               if (end + 1 < text.length()) {
/* 1400 */                 Character ch2 = Character.valueOf(text.charAt(++end));
/* 1401 */                 String s = "000" + Long.toHexString(Character.toCodePoint(ch.charValue(), ch2.charValue()));
/* 1402 */                 data = "\\U" + s.substring(s.length() - 8);
/*      */               } else {
/* 1404 */                 String s = "000" + Integer.toString(ch.charValue(), 16);
/* 1405 */                 data = "\\u" + s.substring(s.length() - 4);
/*      */               } 
/*      */             } else {
/* 1408 */               String s = "000" + Integer.toString(ch.charValue(), 16);
/* 1409 */               data = "\\u" + s.substring(s.length() - 4);
/*      */             } 
/*      */           } else {
/* 1412 */             data = String.valueOf(ch);
/*      */           } 
/* 1414 */           this.column += data.length();
/* 1415 */           this.stream.write(data);
/* 1416 */           start = end + 1;
/*      */         } 
/*      */       } 
/* 1419 */       if (0 < end && end < text.length() - 1 && (ch.charValue() == ' ' || start >= end) && this.column + end - start > this.bestWidth && split) {
/*      */         String data;
/*      */         
/* 1422 */         if (start >= end) {
/* 1423 */           data = "\\";
/*      */         } else {
/* 1425 */           data = text.substring(start, end) + "\\";
/*      */         } 
/* 1427 */         if (start < end) {
/* 1428 */           start = end;
/*      */         }
/* 1430 */         this.column += data.length();
/* 1431 */         this.stream.write(data);
/* 1432 */         writeIndent();
/* 1433 */         this.whitespace = false;
/* 1434 */         this.indention = false;
/* 1435 */         if (text.charAt(start) == ' ') {
/* 1436 */           data = "\\";
/* 1437 */           this.column += data.length();
/* 1438 */           this.stream.write(data);
/*      */         } 
/*      */       } 
/* 1441 */       end++;
/*      */     } 
/* 1443 */     writeIndicator("\"", false, false, false);
/*      */   }
/*      */   
/*      */   private boolean writeCommentLines(List<CommentLine> commentLines) throws IOException {
/* 1447 */     boolean wroteComment = false;
/* 1448 */     if (this.emitComments) {
/* 1449 */       int indentColumns = 0;
/* 1450 */       boolean firstComment = true;
/* 1451 */       for (CommentLine commentLine : commentLines) {
/* 1452 */         if (commentLine.getCommentType() != CommentType.BLANK_LINE) {
/* 1453 */           if (firstComment) {
/* 1454 */             firstComment = false;
/* 1455 */             writeIndicator("#", (commentLine.getCommentType() == CommentType.IN_LINE), false, false);
/* 1456 */             indentColumns = (this.column > 0) ? (this.column - 1) : 0;
/*      */           } else {
/* 1458 */             writeWhitespace(indentColumns);
/* 1459 */             writeIndicator("#", false, false, false);
/*      */           } 
/* 1461 */           this.stream.write(commentLine.getValue());
/*      */         } 
/* 1463 */         writeLineBreak(null);
/* 1464 */         wroteComment = true;
/*      */       } 
/*      */     } 
/* 1467 */     return wroteComment;
/*      */   }
/*      */   
/*      */   private void writeBlockComment() throws IOException {
/* 1471 */     if (!this.blockCommentsCollector.isEmpty()) {
/* 1472 */       writeIndent();
/* 1473 */       writeCommentLines(this.blockCommentsCollector.consume());
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean writeInlineComments() throws IOException {
/* 1478 */     return writeCommentLines(this.inlineCommentsCollector.consume());
/*      */   }
/*      */   
/*      */   private String determineBlockHints(String text) {
/* 1482 */     StringBuilder hints = new StringBuilder();
/* 1483 */     if (Constant.LINEBR.has(text.charAt(0), " ")) {
/* 1484 */       hints.append(this.bestIndent);
/*      */     }
/* 1486 */     char ch1 = text.charAt(text.length() - 1);
/* 1487 */     if (Constant.LINEBR.hasNo(ch1)) {
/* 1488 */       hints.append("-");
/* 1489 */     } else if (text.length() == 1 || Constant.LINEBR.has(text.charAt(text.length() - 2))) {
/* 1490 */       hints.append("+");
/*      */     } 
/* 1492 */     return hints.toString();
/*      */   }
/*      */   
/*      */   void writeFolded(String text, boolean split) throws IOException {
/* 1496 */     String hints = determineBlockHints(text);
/* 1497 */     writeIndicator(">" + hints, true, false, false);
/* 1498 */     if (hints.length() > 0 && hints.charAt(hints.length() - 1) == '+') {
/* 1499 */       this.openEnded = true;
/*      */     }
/* 1501 */     if (!writeInlineComments()) {
/* 1502 */       writeLineBreak(null);
/*      */     }
/* 1504 */     boolean leadingSpace = true;
/* 1505 */     boolean spaces = false;
/* 1506 */     boolean breaks = true;
/* 1507 */     int start = 0, end = 0;
/* 1508 */     while (end <= text.length()) {
/* 1509 */       char ch = Character.MIN_VALUE;
/* 1510 */       if (end < text.length()) {
/* 1511 */         ch = text.charAt(end);
/*      */       }
/* 1513 */       if (breaks) {
/* 1514 */         if (ch == '\000' || Constant.LINEBR.hasNo(ch)) {
/* 1515 */           if (!leadingSpace && ch != '\000' && ch != ' ' && text.charAt(start) == '\n') {
/* 1516 */             writeLineBreak(null);
/*      */           }
/* 1518 */           leadingSpace = (ch == ' ');
/* 1519 */           String data = text.substring(start, end);
/* 1520 */           for (char br : data.toCharArray()) {
/* 1521 */             if (br == '\n') {
/* 1522 */               writeLineBreak(null);
/*      */             } else {
/* 1524 */               writeLineBreak(String.valueOf(br));
/*      */             } 
/*      */           } 
/* 1527 */           if (ch != '\000') {
/* 1528 */             writeIndent();
/*      */           }
/* 1530 */           start = end;
/*      */         } 
/* 1532 */       } else if (spaces) {
/* 1533 */         if (ch != ' ') {
/* 1534 */           if (start + 1 == end && this.column > this.bestWidth && split) {
/* 1535 */             writeIndent();
/*      */           } else {
/* 1537 */             int len = end - start;
/* 1538 */             this.column += len;
/* 1539 */             this.stream.write(text, start, len);
/*      */           } 
/* 1541 */           start = end;
/*      */         }
/*      */       
/* 1544 */       } else if (Constant.LINEBR.has(ch, "\000 ")) {
/* 1545 */         int len = end - start;
/* 1546 */         this.column += len;
/* 1547 */         this.stream.write(text, start, len);
/* 1548 */         if (ch == '\000') {
/* 1549 */           writeLineBreak(null);
/*      */         }
/* 1551 */         start = end;
/*      */       } 
/*      */       
/* 1554 */       if (ch != '\000') {
/* 1555 */         breaks = Constant.LINEBR.has(ch);
/* 1556 */         spaces = (ch == ' ');
/*      */       } 
/* 1558 */       end++;
/*      */     } 
/*      */   }
/*      */   
/*      */   void writeLiteral(String text) throws IOException {
/* 1563 */     String hints = determineBlockHints(text);
/* 1564 */     writeIndicator("|" + hints, true, false, false);
/* 1565 */     if (hints.length() > 0 && hints.charAt(hints.length() - 1) == '+') {
/* 1566 */       this.openEnded = true;
/*      */     }
/* 1568 */     if (!writeInlineComments()) {
/* 1569 */       writeLineBreak(null);
/*      */     }
/* 1571 */     boolean breaks = true;
/* 1572 */     int start = 0, end = 0;
/* 1573 */     while (end <= text.length()) {
/* 1574 */       char ch = Character.MIN_VALUE;
/* 1575 */       if (end < text.length()) {
/* 1576 */         ch = text.charAt(end);
/*      */       }
/* 1578 */       if (breaks) {
/* 1579 */         if (ch == '\000' || Constant.LINEBR.hasNo(ch)) {
/* 1580 */           String data = text.substring(start, end);
/* 1581 */           for (char br : data.toCharArray()) {
/* 1582 */             if (br == '\n') {
/* 1583 */               writeLineBreak(null);
/*      */             } else {
/* 1585 */               writeLineBreak(String.valueOf(br));
/*      */             } 
/*      */           } 
/* 1588 */           if (ch != '\000') {
/* 1589 */             writeIndent();
/*      */           }
/* 1591 */           start = end;
/*      */         }
/*      */       
/* 1594 */       } else if (ch == '\000' || Constant.LINEBR.has(ch)) {
/* 1595 */         this.stream.write(text, start, end - start);
/* 1596 */         if (ch == '\000') {
/* 1597 */           writeLineBreak(null);
/*      */         }
/* 1599 */         start = end;
/*      */       } 
/*      */       
/* 1602 */       if (ch != '\000') {
/* 1603 */         breaks = Constant.LINEBR.has(ch);
/*      */       }
/* 1605 */       end++;
/*      */     } 
/*      */   }
/*      */   
/*      */   void writePlain(String text, boolean split) throws IOException {
/* 1610 */     if (this.rootContext) {
/* 1611 */       this.openEnded = true;
/*      */     }
/* 1613 */     if (text.length() == 0) {
/*      */       return;
/*      */     }
/* 1616 */     if (!this.whitespace) {
/* 1617 */       this.column++;
/* 1618 */       this.stream.write(SPACE);
/*      */     } 
/* 1620 */     this.whitespace = false;
/* 1621 */     this.indention = false;
/* 1622 */     boolean spaces = false;
/* 1623 */     boolean breaks = false;
/* 1624 */     int start = 0, end = 0;
/* 1625 */     while (end <= text.length()) {
/* 1626 */       char ch = Character.MIN_VALUE;
/* 1627 */       if (end < text.length()) {
/* 1628 */         ch = text.charAt(end);
/*      */       }
/* 1630 */       if (spaces) {
/* 1631 */         if (ch != ' ') {
/* 1632 */           if (start + 1 == end && this.column > this.bestWidth && split) {
/* 1633 */             writeIndent();
/* 1634 */             this.whitespace = false;
/* 1635 */             this.indention = false;
/*      */           } else {
/* 1637 */             int len = end - start;
/* 1638 */             this.column += len;
/* 1639 */             this.stream.write(text, start, len);
/*      */           } 
/* 1641 */           start = end;
/*      */         } 
/* 1643 */       } else if (breaks) {
/* 1644 */         if (Constant.LINEBR.hasNo(ch)) {
/* 1645 */           if (text.charAt(start) == '\n') {
/* 1646 */             writeLineBreak(null);
/*      */           }
/* 1648 */           String data = text.substring(start, end);
/* 1649 */           for (char br : data.toCharArray()) {
/* 1650 */             if (br == '\n') {
/* 1651 */               writeLineBreak(null);
/*      */             } else {
/* 1653 */               writeLineBreak(String.valueOf(br));
/*      */             } 
/*      */           } 
/* 1656 */           writeIndent();
/* 1657 */           this.whitespace = false;
/* 1658 */           this.indention = false;
/* 1659 */           start = end;
/*      */         }
/*      */       
/* 1662 */       } else if (Constant.LINEBR.has(ch, "\000 ")) {
/* 1663 */         int len = end - start;
/* 1664 */         this.column += len;
/* 1665 */         this.stream.write(text, start, len);
/* 1666 */         start = end;
/*      */       } 
/*      */       
/* 1669 */       if (ch != '\000') {
/* 1670 */         spaces = (ch == ' ');
/* 1671 */         breaks = Constant.LINEBR.has(ch);
/*      */       } 
/* 1673 */       end++;
/*      */     } 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\emitter\Emitter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */