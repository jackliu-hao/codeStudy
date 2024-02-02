/*     */ package org.yaml.snakeyaml.parser;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.yaml.snakeyaml.DumperOptions;
/*     */ import org.yaml.snakeyaml.comments.CommentType;
/*     */ import org.yaml.snakeyaml.error.Mark;
/*     */ import org.yaml.snakeyaml.error.YAMLException;
/*     */ import org.yaml.snakeyaml.events.AliasEvent;
/*     */ import org.yaml.snakeyaml.events.CommentEvent;
/*     */ import org.yaml.snakeyaml.events.DocumentEndEvent;
/*     */ import org.yaml.snakeyaml.events.DocumentStartEvent;
/*     */ import org.yaml.snakeyaml.events.Event;
/*     */ import org.yaml.snakeyaml.events.ImplicitTuple;
/*     */ import org.yaml.snakeyaml.events.MappingEndEvent;
/*     */ import org.yaml.snakeyaml.events.MappingStartEvent;
/*     */ import org.yaml.snakeyaml.events.ScalarEvent;
/*     */ import org.yaml.snakeyaml.events.SequenceEndEvent;
/*     */ import org.yaml.snakeyaml.events.SequenceStartEvent;
/*     */ import org.yaml.snakeyaml.events.StreamEndEvent;
/*     */ import org.yaml.snakeyaml.events.StreamStartEvent;
/*     */ import org.yaml.snakeyaml.reader.StreamReader;
/*     */ import org.yaml.snakeyaml.scanner.Scanner;
/*     */ import org.yaml.snakeyaml.scanner.ScannerImpl;
/*     */ import org.yaml.snakeyaml.tokens.AliasToken;
/*     */ import org.yaml.snakeyaml.tokens.AnchorToken;
/*     */ import org.yaml.snakeyaml.tokens.BlockEntryToken;
/*     */ import org.yaml.snakeyaml.tokens.CommentToken;
/*     */ import org.yaml.snakeyaml.tokens.DirectiveToken;
/*     */ import org.yaml.snakeyaml.tokens.ScalarToken;
/*     */ import org.yaml.snakeyaml.tokens.StreamEndToken;
/*     */ import org.yaml.snakeyaml.tokens.StreamStartToken;
/*     */ import org.yaml.snakeyaml.tokens.TagToken;
/*     */ import org.yaml.snakeyaml.tokens.TagTuple;
/*     */ import org.yaml.snakeyaml.tokens.Token;
/*     */ import org.yaml.snakeyaml.util.ArrayStack;
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
/*     */ public class ParserImpl
/*     */   implements Parser
/*     */ {
/* 121 */   private static final Map<String, String> DEFAULT_TAGS = new HashMap<>(); protected final Scanner scanner;
/*     */   static {
/* 123 */     DEFAULT_TAGS.put("!", "!");
/* 124 */     DEFAULT_TAGS.put("!!", "tag:yaml.org,2002:");
/*     */   }
/*     */ 
/*     */   
/*     */   private Event currentEvent;
/*     */   private final ArrayStack<Production> states;
/*     */   private final ArrayStack<Mark> marks;
/*     */   private Production state;
/*     */   private VersionTagsTuple directives;
/*     */   
/*     */   public ParserImpl(StreamReader reader) {
/* 135 */     this((Scanner)new ScannerImpl(reader));
/*     */   }
/*     */   
/*     */   public ParserImpl(StreamReader reader, boolean emitComments) {
/* 139 */     this((Scanner)(new ScannerImpl(reader)).setEmitComments(emitComments));
/*     */   }
/*     */   
/*     */   public ParserImpl(Scanner scanner) {
/* 143 */     this.scanner = scanner;
/* 144 */     this.currentEvent = null;
/* 145 */     this.directives = new VersionTagsTuple(null, new HashMap<>(DEFAULT_TAGS));
/* 146 */     this.states = new ArrayStack(100);
/* 147 */     this.marks = new ArrayStack(10);
/* 148 */     this.state = new ParseStreamStart();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean checkEvent(Event.ID choice) {
/* 155 */     peekEvent();
/* 156 */     return (this.currentEvent != null && this.currentEvent.is(choice));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Event peekEvent() {
/* 163 */     if (this.currentEvent == null && 
/* 164 */       this.state != null) {
/* 165 */       this.currentEvent = this.state.produce();
/*     */     }
/*     */     
/* 168 */     return this.currentEvent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Event getEvent() {
/* 175 */     peekEvent();
/* 176 */     Event value = this.currentEvent;
/* 177 */     this.currentEvent = null;
/* 178 */     return value;
/*     */   }
/*     */   
/*     */   private CommentEvent produceCommentEvent(CommentToken token) {
/* 182 */     Mark startMark = token.getStartMark();
/* 183 */     Mark endMark = token.getEndMark();
/* 184 */     String value = token.getValue();
/* 185 */     CommentType type = token.getCommentType();
/*     */ 
/*     */ 
/*     */     
/* 189 */     return new CommentEvent(type, value, startMark, endMark);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private class ParseStreamStart
/*     */     implements Production
/*     */   {
/*     */     private ParseStreamStart() {}
/*     */ 
/*     */ 
/*     */     
/*     */     public Event produce() {
/* 202 */       StreamStartToken token = (StreamStartToken)ParserImpl.this.scanner.getToken();
/* 203 */       StreamStartEvent streamStartEvent = new StreamStartEvent(token.getStartMark(), token.getEndMark());
/*     */       
/* 205 */       ParserImpl.this.state = new ParserImpl.ParseImplicitDocumentStart();
/* 206 */       return (Event)streamStartEvent;
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseImplicitDocumentStart implements Production { private ParseImplicitDocumentStart() {}
/*     */     
/*     */     public Event produce() {
/* 213 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment }))
/* 214 */         return (Event)ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken()); 
/* 215 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Directive, Token.ID.DocumentStart, Token.ID.StreamEnd })) {
/* 216 */         ParserImpl.this.directives = new VersionTagsTuple(null, ParserImpl.DEFAULT_TAGS);
/* 217 */         Token token = ParserImpl.this.scanner.peekToken();
/* 218 */         Mark startMark = token.getStartMark();
/* 219 */         Mark endMark = startMark;
/* 220 */         DocumentStartEvent documentStartEvent = new DocumentStartEvent(startMark, endMark, false, null, null);
/*     */         
/* 222 */         ParserImpl.this.states.push(new ParserImpl.ParseDocumentEnd());
/* 223 */         ParserImpl.this.state = new ParserImpl.ParseBlockNode();
/* 224 */         return (Event)documentStartEvent;
/*     */       } 
/* 226 */       Production p = new ParserImpl.ParseDocumentStart();
/* 227 */       return p.produce();
/*     */     } }
/*     */   
/*     */   private class ParseDocumentStart implements Production {
/*     */     private ParseDocumentStart() {}
/*     */     
/*     */     public Event produce() {
/* 234 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 235 */         return (Event)ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/*     */       }
/*     */       
/* 238 */       while (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.DocumentEnd })) {
/* 239 */         ParserImpl.this.scanner.getToken();
/*     */       }
/* 241 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 242 */         return (Event)ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/*     */       }
/*     */ 
/*     */       
/* 246 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.StreamEnd })) {
/* 247 */         Token token1 = ParserImpl.this.scanner.peekToken();
/* 248 */         Mark startMark = token1.getStartMark();
/* 249 */         VersionTagsTuple tuple = ParserImpl.this.processDirectives();
/* 250 */         while (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment }))
/*     */         {
/* 252 */           ParserImpl.this.scanner.getToken();
/*     */         }
/* 254 */         if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.StreamEnd })) {
/* 255 */           if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.DocumentStart })) {
/* 256 */             throw new ParserException(null, null, "expected '<document start>', but found '" + ParserImpl.this.scanner.peekToken().getTokenId() + "'", ParserImpl.this.scanner.peekToken().getStartMark());
/*     */           }
/*     */           
/* 259 */           token1 = ParserImpl.this.scanner.getToken();
/* 260 */           Mark endMark = token1.getEndMark();
/* 261 */           DocumentStartEvent documentStartEvent = new DocumentStartEvent(startMark, endMark, true, tuple.getVersion(), tuple.getTags());
/*     */           
/* 263 */           ParserImpl.this.states.push(new ParserImpl.ParseDocumentEnd());
/* 264 */           ParserImpl.this.state = new ParserImpl.ParseDocumentContent();
/* 265 */           return (Event)documentStartEvent;
/*     */         } 
/*     */       } 
/*     */       
/* 269 */       StreamEndToken token = (StreamEndToken)ParserImpl.this.scanner.getToken();
/* 270 */       StreamEndEvent streamEndEvent = new StreamEndEvent(token.getStartMark(), token.getEndMark());
/* 271 */       if (!ParserImpl.this.states.isEmpty()) {
/* 272 */         throw new YAMLException("Unexpected end of stream. States left: " + ParserImpl.this.states);
/*     */       }
/* 274 */       if (!ParserImpl.this.marks.isEmpty()) {
/* 275 */         throw new YAMLException("Unexpected end of stream. Marks left: " + ParserImpl.this.marks);
/*     */       }
/* 277 */       ParserImpl.this.state = null;
/*     */       
/* 279 */       return (Event)streamEndEvent;
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseDocumentEnd implements Production { private ParseDocumentEnd() {}
/*     */     
/*     */     public Event produce() {
/* 286 */       Token token = ParserImpl.this.scanner.peekToken();
/* 287 */       Mark startMark = token.getStartMark();
/* 288 */       Mark endMark = startMark;
/* 289 */       boolean explicit = false;
/* 290 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.DocumentEnd })) {
/* 291 */         token = ParserImpl.this.scanner.getToken();
/* 292 */         endMark = token.getEndMark();
/* 293 */         explicit = true;
/*     */       } 
/* 295 */       DocumentEndEvent documentEndEvent = new DocumentEndEvent(startMark, endMark, explicit);
/*     */       
/* 297 */       ParserImpl.this.state = new ParserImpl.ParseDocumentStart();
/* 298 */       return (Event)documentEndEvent;
/*     */     } }
/*     */   
/*     */   private class ParseDocumentContent implements Production { private ParseDocumentContent() {}
/*     */     
/*     */     public Event produce() {
/* 304 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 305 */         return (Event)ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/*     */       }
/*     */       
/* 308 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Directive, Token.ID.DocumentStart, Token.ID.DocumentEnd, Token.ID.StreamEnd })) {
/*     */         
/* 310 */         Event event = ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
/* 311 */         ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
/* 312 */         return event;
/*     */       } 
/* 314 */       Production p = new ParserImpl.ParseBlockNode();
/* 315 */       return p.produce();
/*     */     } }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private VersionTagsTuple processDirectives() {
/* 322 */     DumperOptions.Version yamlVersion = null;
/* 323 */     HashMap<String, String> tagHandles = new HashMap<>();
/* 324 */     while (this.scanner.checkToken(new Token.ID[] { Token.ID.Directive })) {
/*     */       
/* 326 */       DirectiveToken token = (DirectiveToken)this.scanner.getToken();
/* 327 */       if (token.getName().equals("YAML")) {
/* 328 */         if (yamlVersion != null) {
/* 329 */           throw new ParserException(null, null, "found duplicate YAML directive", token.getStartMark());
/*     */         }
/*     */         
/* 332 */         List<Integer> value = token.getValue();
/* 333 */         Integer major = value.get(0);
/* 334 */         if (major.intValue() != 1) {
/* 335 */           throw new ParserException(null, null, "found incompatible YAML document (version 1.* is required)", token.getStartMark());
/*     */         }
/*     */ 
/*     */         
/* 339 */         Integer minor = value.get(1);
/* 340 */         switch (minor.intValue()) {
/*     */           case 0:
/* 342 */             yamlVersion = DumperOptions.Version.V1_0;
/*     */             continue;
/*     */         } 
/*     */         
/* 346 */         yamlVersion = DumperOptions.Version.V1_1;
/*     */         continue;
/*     */       } 
/* 349 */       if (token.getName().equals("TAG")) {
/* 350 */         List<String> value = token.getValue();
/* 351 */         String handle = value.get(0);
/* 352 */         String prefix = value.get(1);
/* 353 */         if (tagHandles.containsKey(handle)) {
/* 354 */           throw new ParserException(null, null, "duplicate tag handle " + handle, token.getStartMark());
/*     */         }
/*     */         
/* 357 */         tagHandles.put(handle, prefix);
/*     */       } 
/*     */     } 
/* 360 */     if (yamlVersion != null || !tagHandles.isEmpty()) {
/*     */       
/* 362 */       for (String key : DEFAULT_TAGS.keySet()) {
/*     */         
/* 364 */         if (!tagHandles.containsKey(key)) {
/* 365 */           tagHandles.put(key, DEFAULT_TAGS.get(key));
/*     */         }
/*     */       } 
/* 368 */       this.directives = new VersionTagsTuple(yamlVersion, tagHandles);
/*     */     } 
/* 370 */     return this.directives;
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
/*     */   private class ParseBlockNode
/*     */     implements Production
/*     */   {
/*     */     private ParseBlockNode() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Event produce() {
/* 395 */       return ParserImpl.this.parseNode(true, false);
/*     */     }
/*     */   }
/*     */   
/*     */   private Event parseFlowNode() {
/* 400 */     return parseNode(false, false);
/*     */   }
/*     */   
/*     */   private Event parseBlockNodeOrIndentlessSequence() {
/* 404 */     return parseNode(true, true);
/*     */   }
/*     */   
/*     */   private Event parseNode(boolean block, boolean indentlessSequence) {
/*     */     ScalarEvent scalarEvent;
/* 409 */     Mark startMark = null;
/* 410 */     Mark endMark = null;
/* 411 */     Mark tagMark = null;
/* 412 */     if (this.scanner.checkToken(new Token.ID[] { Token.ID.Alias })) {
/* 413 */       AliasToken token = (AliasToken)this.scanner.getToken();
/* 414 */       AliasEvent aliasEvent = new AliasEvent(token.getValue(), token.getStartMark(), token.getEndMark());
/* 415 */       this.state = (Production)this.states.pop();
/*     */     } else {
/* 417 */       String anchor = null;
/* 418 */       TagTuple tagTokenTag = null;
/* 419 */       if (this.scanner.checkToken(new Token.ID[] { Token.ID.Anchor })) {
/* 420 */         AnchorToken token = (AnchorToken)this.scanner.getToken();
/* 421 */         startMark = token.getStartMark();
/* 422 */         endMark = token.getEndMark();
/* 423 */         anchor = token.getValue();
/* 424 */         if (this.scanner.checkToken(new Token.ID[] { Token.ID.Tag })) {
/* 425 */           TagToken tagToken = (TagToken)this.scanner.getToken();
/* 426 */           tagMark = tagToken.getStartMark();
/* 427 */           endMark = tagToken.getEndMark();
/* 428 */           tagTokenTag = tagToken.getValue();
/*     */         } 
/*     */       } else {
/* 431 */         TagToken tagToken = (TagToken)this.scanner.getToken();
/* 432 */         startMark = tagToken.getStartMark();
/* 433 */         tagMark = startMark;
/* 434 */         endMark = tagToken.getEndMark();
/* 435 */         tagTokenTag = tagToken.getValue();
/* 436 */         if (this.scanner.checkToken(new Token.ID[] { Token.ID.Tag }) && this.scanner.checkToken(new Token.ID[] { Token.ID.Anchor })) {
/* 437 */           AnchorToken token = (AnchorToken)this.scanner.getToken();
/* 438 */           endMark = token.getEndMark();
/* 439 */           anchor = token.getValue();
/*     */         } 
/*     */       } 
/* 442 */       String tag = null;
/* 443 */       if (tagTokenTag != null) {
/* 444 */         String handle = tagTokenTag.getHandle();
/* 445 */         String suffix = tagTokenTag.getSuffix();
/* 446 */         if (handle != null) {
/* 447 */           if (!this.directives.getTags().containsKey(handle)) {
/* 448 */             throw new ParserException("while parsing a node", startMark, "found undefined tag handle " + handle, tagMark);
/*     */           }
/*     */           
/* 451 */           tag = (String)this.directives.getTags().get(handle) + suffix;
/*     */         } else {
/* 453 */           tag = suffix;
/*     */         } 
/*     */       } 
/* 456 */       if (startMark == null) {
/* 457 */         startMark = this.scanner.peekToken().getStartMark();
/* 458 */         endMark = startMark;
/*     */       } 
/* 460 */       Event event = null;
/* 461 */       boolean implicit = (tag == null || tag.equals("!"));
/* 462 */       if (indentlessSequence && this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEntry })) {
/* 463 */         endMark = this.scanner.peekToken().getEndMark();
/* 464 */         SequenceStartEvent sequenceStartEvent = new SequenceStartEvent(anchor, tag, implicit, startMark, endMark, DumperOptions.FlowStyle.BLOCK);
/*     */         
/* 466 */         this.state = new ParseIndentlessSequenceEntryKey();
/*     */       }
/* 468 */       else if (this.scanner.checkToken(new Token.ID[] { Token.ID.Scalar })) {
/* 469 */         ImplicitTuple implicitValues; ScalarToken token = (ScalarToken)this.scanner.getToken();
/* 470 */         endMark = token.getEndMark();
/*     */         
/* 472 */         if ((token.getPlain() && tag == null) || "!".equals(tag)) {
/* 473 */           implicitValues = new ImplicitTuple(true, false);
/* 474 */         } else if (tag == null) {
/* 475 */           implicitValues = new ImplicitTuple(false, true);
/*     */         } else {
/* 477 */           implicitValues = new ImplicitTuple(false, false);
/*     */         } 
/* 479 */         scalarEvent = new ScalarEvent(anchor, tag, implicitValues, token.getValue(), startMark, endMark, token.getStyle());
/*     */         
/* 481 */         this.state = (Production)this.states.pop();
/* 482 */       } else if (this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 483 */         CommentEvent commentEvent = produceCommentEvent((CommentToken)this.scanner.getToken());
/* 484 */       } else if (this.scanner.checkToken(new Token.ID[] { Token.ID.FlowSequenceStart })) {
/* 485 */         endMark = this.scanner.peekToken().getEndMark();
/* 486 */         SequenceStartEvent sequenceStartEvent = new SequenceStartEvent(anchor, tag, implicit, startMark, endMark, DumperOptions.FlowStyle.FLOW);
/*     */         
/* 488 */         this.state = new ParseFlowSequenceFirstEntry();
/* 489 */       } else if (this.scanner.checkToken(new Token.ID[] { Token.ID.FlowMappingStart })) {
/* 490 */         endMark = this.scanner.peekToken().getEndMark();
/* 491 */         MappingStartEvent mappingStartEvent = new MappingStartEvent(anchor, tag, implicit, startMark, endMark, DumperOptions.FlowStyle.FLOW);
/*     */         
/* 493 */         this.state = new ParseFlowMappingFirstKey();
/* 494 */       } else if (block && this.scanner.checkToken(new Token.ID[] { Token.ID.BlockSequenceStart })) {
/* 495 */         endMark = this.scanner.peekToken().getStartMark();
/* 496 */         SequenceStartEvent sequenceStartEvent = new SequenceStartEvent(anchor, tag, implicit, startMark, endMark, DumperOptions.FlowStyle.BLOCK);
/*     */         
/* 498 */         this.state = new ParseBlockSequenceFirstEntry();
/* 499 */       } else if (block && this.scanner.checkToken(new Token.ID[] { Token.ID.BlockMappingStart })) {
/* 500 */         endMark = this.scanner.peekToken().getStartMark();
/* 501 */         MappingStartEvent mappingStartEvent = new MappingStartEvent(anchor, tag, implicit, startMark, endMark, DumperOptions.FlowStyle.BLOCK);
/*     */         
/* 503 */         this.state = new ParseBlockMappingFirstKey();
/* 504 */       } else if (anchor != null || tag != null) {
/*     */ 
/*     */         
/* 507 */         scalarEvent = new ScalarEvent(anchor, tag, new ImplicitTuple(implicit, false), "", startMark, endMark, DumperOptions.ScalarStyle.PLAIN);
/*     */         
/* 509 */         this.state = (Production)this.states.pop();
/*     */       } else {
/*     */         String node;
/* 512 */         if (block) {
/* 513 */           node = "block";
/*     */         } else {
/* 515 */           node = "flow";
/*     */         } 
/* 517 */         Token token = this.scanner.peekToken();
/* 518 */         throw new ParserException("while parsing a " + node + " node", startMark, "expected the node content, but found '" + token.getTokenId() + "'", token.getStartMark());
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 524 */     return (Event)scalarEvent;
/*     */   }
/*     */   
/*     */   private class ParseBlockSequenceFirstEntry
/*     */     implements Production {
/*     */     private ParseBlockSequenceFirstEntry() {}
/*     */     
/*     */     public Event produce() {
/* 532 */       Token token = ParserImpl.this.scanner.getToken();
/* 533 */       ParserImpl.this.marks.push(token.getStartMark());
/* 534 */       return (new ParserImpl.ParseBlockSequenceEntryKey()).produce();
/*     */     } }
/*     */   
/*     */   private class ParseBlockSequenceEntryKey implements Production { private ParseBlockSequenceEntryKey() {}
/*     */     
/*     */     public Event produce() {
/* 540 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 541 */         return (Event)ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/*     */       }
/* 543 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEntry })) {
/* 544 */         BlockEntryToken blockEntryToken = (BlockEntryToken)ParserImpl.this.scanner.getToken();
/* 545 */         return (new ParserImpl.ParseBlockSequenceEntryValue(blockEntryToken)).produce();
/*     */       } 
/* 547 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEnd })) {
/* 548 */         Token token1 = ParserImpl.this.scanner.peekToken();
/* 549 */         throw new ParserException("while parsing a block collection", (Mark)ParserImpl.this.marks.pop(), "expected <block end>, but found '" + token1.getTokenId() + "'", token1.getStartMark());
/*     */       } 
/*     */ 
/*     */       
/* 553 */       Token token = ParserImpl.this.scanner.getToken();
/* 554 */       SequenceEndEvent sequenceEndEvent = new SequenceEndEvent(token.getStartMark(), token.getEndMark());
/* 555 */       ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
/* 556 */       ParserImpl.this.marks.pop();
/* 557 */       return (Event)sequenceEndEvent;
/*     */     } }
/*     */ 
/*     */   
/*     */   private class ParseBlockSequenceEntryValue implements Production {
/*     */     BlockEntryToken token;
/*     */     
/*     */     public ParseBlockSequenceEntryValue(BlockEntryToken token) {
/* 565 */       this.token = token;
/*     */     }
/*     */     
/*     */     public Event produce() {
/* 569 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 570 */         ParserImpl.this.state = new ParseBlockSequenceEntryValue(this.token);
/* 571 */         return (Event)ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/*     */       } 
/* 573 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEntry, Token.ID.BlockEnd })) {
/* 574 */         ParserImpl.this.states.push(new ParserImpl.ParseBlockSequenceEntryKey());
/* 575 */         return (new ParserImpl.ParseBlockNode()).produce();
/*     */       } 
/* 577 */       ParserImpl.this.state = new ParserImpl.ParseBlockSequenceEntryKey();
/* 578 */       return ParserImpl.this.processEmptyScalar(this.token.getEndMark());
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseIndentlessSequenceEntryKey
/*     */     implements Production {
/*     */     private ParseIndentlessSequenceEntryKey() {}
/*     */     
/*     */     public Event produce() {
/* 587 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 588 */         return (Event)ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/*     */       }
/* 590 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEntry })) {
/* 591 */         BlockEntryToken blockEntryToken = (BlockEntryToken)ParserImpl.this.scanner.getToken();
/* 592 */         return (new ParserImpl.ParseIndentlessSequenceEntryValue(blockEntryToken)).produce();
/*     */       } 
/* 594 */       Token token = ParserImpl.this.scanner.peekToken();
/* 595 */       SequenceEndEvent sequenceEndEvent = new SequenceEndEvent(token.getStartMark(), token.getEndMark());
/* 596 */       ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
/* 597 */       return (Event)sequenceEndEvent;
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseIndentlessSequenceEntryValue implements Production {
/*     */     BlockEntryToken token;
/*     */     
/*     */     public ParseIndentlessSequenceEntryValue(BlockEntryToken token) {
/* 605 */       this.token = token;
/*     */     }
/*     */     
/*     */     public Event produce() {
/* 609 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 610 */         ParserImpl.this.state = new ParseIndentlessSequenceEntryValue(this.token);
/* 611 */         return (Event)ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/*     */       } 
/* 613 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEntry, Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd })) {
/*     */         
/* 615 */         ParserImpl.this.states.push(new ParserImpl.ParseIndentlessSequenceEntryKey());
/* 616 */         return (new ParserImpl.ParseBlockNode()).produce();
/*     */       } 
/* 618 */       ParserImpl.this.state = new ParserImpl.ParseIndentlessSequenceEntryKey();
/* 619 */       return ParserImpl.this.processEmptyScalar(this.token.getEndMark());
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseBlockMappingFirstKey implements Production { private ParseBlockMappingFirstKey() {}
/*     */     
/*     */     public Event produce() {
/* 626 */       Token token = ParserImpl.this.scanner.getToken();
/* 627 */       ParserImpl.this.marks.push(token.getStartMark());
/* 628 */       return (new ParserImpl.ParseBlockMappingKey()).produce();
/*     */     } }
/*     */   
/*     */   private class ParseBlockMappingKey implements Production { private ParseBlockMappingKey() {}
/*     */     
/*     */     public Event produce() {
/* 634 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 635 */         return (Event)ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/*     */       }
/* 637 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Key })) {
/* 638 */         Token token1 = ParserImpl.this.scanner.getToken();
/* 639 */         if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd })) {
/* 640 */           ParserImpl.this.states.push(new ParserImpl.ParseBlockMappingValue());
/* 641 */           return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
/*     */         } 
/* 643 */         ParserImpl.this.state = new ParserImpl.ParseBlockMappingValue();
/* 644 */         return ParserImpl.this.processEmptyScalar(token1.getEndMark());
/*     */       } 
/*     */       
/* 647 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEnd })) {
/* 648 */         Token token1 = ParserImpl.this.scanner.peekToken();
/* 649 */         throw new ParserException("while parsing a block mapping", (Mark)ParserImpl.this.marks.pop(), "expected <block end>, but found '" + token1.getTokenId() + "'", token1.getStartMark());
/*     */       } 
/*     */ 
/*     */       
/* 653 */       Token token = ParserImpl.this.scanner.getToken();
/* 654 */       MappingEndEvent mappingEndEvent = new MappingEndEvent(token.getStartMark(), token.getEndMark());
/* 655 */       ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
/* 656 */       ParserImpl.this.marks.pop();
/* 657 */       return (Event)mappingEndEvent;
/*     */     } }
/*     */   
/*     */   private class ParseBlockMappingValue implements Production { private ParseBlockMappingValue() {}
/*     */     
/*     */     public Event produce() {
/* 663 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Value })) {
/* 664 */         Token token1 = ParserImpl.this.scanner.getToken();
/* 665 */         if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 666 */           ParserImpl.this.state = new ParserImpl.ParseBlockMappingValueComment();
/* 667 */           return ParserImpl.this.state.produce();
/* 668 */         }  if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd })) {
/* 669 */           ParserImpl.this.states.push(new ParserImpl.ParseBlockMappingKey());
/* 670 */           return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
/*     */         } 
/* 672 */         ParserImpl.this.state = new ParserImpl.ParseBlockMappingKey();
/* 673 */         return ParserImpl.this.processEmptyScalar(token1.getEndMark());
/*     */       } 
/* 675 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Scalar })) {
/* 676 */         ParserImpl.this.states.push(new ParserImpl.ParseBlockMappingKey());
/* 677 */         return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
/*     */       } 
/* 679 */       ParserImpl.this.state = new ParserImpl.ParseBlockMappingKey();
/* 680 */       Token token = ParserImpl.this.scanner.peekToken();
/* 681 */       return ParserImpl.this.processEmptyScalar(token.getStartMark());
/*     */     } }
/*     */   
/*     */   private class ParseBlockMappingValueComment implements Production { private ParseBlockMappingValueComment() {}
/*     */     
/*     */     public Event produce() {
/* 687 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment }))
/* 688 */         return ParserImpl.this.parseBlockNodeOrIndentlessSequence(); 
/* 689 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd })) {
/* 690 */         ParserImpl.this.states.push(new ParserImpl.ParseBlockMappingKey());
/* 691 */         return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
/*     */       } 
/* 693 */       ParserImpl.this.state = new ParserImpl.ParseBlockMappingKey();
/* 694 */       Token token = ParserImpl.this.scanner.getToken();
/* 695 */       return ParserImpl.this.processEmptyScalar(token.getEndMark());
/*     */     } }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class ParseFlowSequenceFirstEntry
/*     */     implements Production
/*     */   {
/*     */     private ParseFlowSequenceFirstEntry() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Event produce() {
/* 715 */       Token token = ParserImpl.this.scanner.getToken();
/* 716 */       ParserImpl.this.marks.push(token.getStartMark());
/* 717 */       return (new ParserImpl.ParseFlowSequenceEntry(true)).produce();
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseFlowSequenceEntry implements Production {
/*     */     private boolean first = false;
/*     */     
/*     */     public ParseFlowSequenceEntry(boolean first) {
/* 725 */       this.first = first;
/*     */     }
/*     */     
/*     */     public Event produce() {
/* 729 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowSequenceEnd })) {
/* 730 */         if (!this.first) {
/* 731 */           if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowEntry })) {
/* 732 */             ParserImpl.this.scanner.getToken();
/*     */           } else {
/* 734 */             Token token1 = ParserImpl.this.scanner.peekToken();
/* 735 */             throw new ParserException("while parsing a flow sequence", (Mark)ParserImpl.this.marks.pop(), "expected ',' or ']', but got " + token1.getTokenId(), token1.getStartMark());
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/* 740 */         if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Key })) {
/* 741 */           Token token1 = ParserImpl.this.scanner.peekToken();
/* 742 */           MappingStartEvent mappingStartEvent = new MappingStartEvent(null, null, true, token1.getStartMark(), token1.getEndMark(), DumperOptions.FlowStyle.FLOW);
/*     */           
/* 744 */           ParserImpl.this.state = new ParserImpl.ParseFlowSequenceEntryMappingKey();
/* 745 */           return (Event)mappingStartEvent;
/* 746 */         }  if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowSequenceEnd })) {
/* 747 */           ParserImpl.this.states.push(new ParseFlowSequenceEntry(false));
/* 748 */           return ParserImpl.this.parseFlowNode();
/*     */         } 
/*     */       } 
/* 751 */       Token token = ParserImpl.this.scanner.getToken();
/* 752 */       SequenceEndEvent sequenceEndEvent = new SequenceEndEvent(token.getStartMark(), token.getEndMark());
/* 753 */       ParserImpl.this.marks.pop();
/* 754 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 755 */         ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
/*     */       } else {
/* 757 */         ParserImpl.this.state = new ParserImpl.ParseFlowEndComment();
/*     */       } 
/* 759 */       return (Event)sequenceEndEvent;
/*     */     } }
/*     */   
/*     */   private class ParseFlowEndComment implements Production { private ParseFlowEndComment() {}
/*     */     
/*     */     public Event produce() {
/* 765 */       CommentEvent commentEvent = ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/* 766 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 767 */         ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
/*     */       }
/* 769 */       return (Event)commentEvent;
/*     */     } }
/*     */ 
/*     */   
/*     */   private class ParseFlowSequenceEntryMappingKey implements Production { public Event produce() {
/* 774 */       Token token = ParserImpl.this.scanner.getToken();
/* 775 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowSequenceEnd })) {
/* 776 */         ParserImpl.this.states.push(new ParserImpl.ParseFlowSequenceEntryMappingValue());
/* 777 */         return ParserImpl.this.parseFlowNode();
/*     */       } 
/* 779 */       ParserImpl.this.state = new ParserImpl.ParseFlowSequenceEntryMappingValue();
/* 780 */       return ParserImpl.this.processEmptyScalar(token.getEndMark());
/*     */     }
/*     */     private ParseFlowSequenceEntryMappingKey() {} }
/*     */   
/*     */   private class ParseFlowSequenceEntryMappingValue implements Production { private ParseFlowSequenceEntryMappingValue() {}
/*     */     
/*     */     public Event produce() {
/* 787 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Value })) {
/* 788 */         Token token1 = ParserImpl.this.scanner.getToken();
/* 789 */         if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowEntry, Token.ID.FlowSequenceEnd })) {
/* 790 */           ParserImpl.this.states.push(new ParserImpl.ParseFlowSequenceEntryMappingEnd());
/* 791 */           return ParserImpl.this.parseFlowNode();
/*     */         } 
/* 793 */         ParserImpl.this.state = new ParserImpl.ParseFlowSequenceEntryMappingEnd();
/* 794 */         return ParserImpl.this.processEmptyScalar(token1.getEndMark());
/*     */       } 
/*     */       
/* 797 */       ParserImpl.this.state = new ParserImpl.ParseFlowSequenceEntryMappingEnd();
/* 798 */       Token token = ParserImpl.this.scanner.peekToken();
/* 799 */       return ParserImpl.this.processEmptyScalar(token.getStartMark());
/*     */     } }
/*     */   
/*     */   private class ParseFlowSequenceEntryMappingEnd implements Production {
/*     */     private ParseFlowSequenceEntryMappingEnd() {}
/*     */     
/*     */     public Event produce() {
/* 806 */       ParserImpl.this.state = new ParserImpl.ParseFlowSequenceEntry(false);
/* 807 */       Token token = ParserImpl.this.scanner.peekToken();
/* 808 */       return (Event)new MappingEndEvent(token.getStartMark(), token.getEndMark());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class ParseFlowMappingFirstKey
/*     */     implements Production
/*     */   {
/*     */     private ParseFlowMappingFirstKey() {}
/*     */ 
/*     */ 
/*     */     
/*     */     public Event produce() {
/* 823 */       Token token = ParserImpl.this.scanner.getToken();
/* 824 */       ParserImpl.this.marks.push(token.getStartMark());
/* 825 */       return (new ParserImpl.ParseFlowMappingKey(true)).produce();
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseFlowMappingKey implements Production {
/*     */     private boolean first = false;
/*     */     
/*     */     public ParseFlowMappingKey(boolean first) {
/* 833 */       this.first = first;
/*     */     }
/*     */     
/*     */     public Event produce() {
/* 837 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowMappingEnd })) {
/* 838 */         if (!this.first) {
/* 839 */           if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowEntry })) {
/* 840 */             ParserImpl.this.scanner.getToken();
/*     */           } else {
/* 842 */             Token token1 = ParserImpl.this.scanner.peekToken();
/* 843 */             throw new ParserException("while parsing a flow mapping", (Mark)ParserImpl.this.marks.pop(), "expected ',' or '}', but got " + token1.getTokenId(), token1.getStartMark());
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/* 848 */         if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Key })) {
/* 849 */           Token token1 = ParserImpl.this.scanner.getToken();
/* 850 */           if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowMappingEnd })) {
/*     */             
/* 852 */             ParserImpl.this.states.push(new ParserImpl.ParseFlowMappingValue());
/* 853 */             return ParserImpl.this.parseFlowNode();
/*     */           } 
/* 855 */           ParserImpl.this.state = new ParserImpl.ParseFlowMappingValue();
/* 856 */           return ParserImpl.this.processEmptyScalar(token1.getEndMark());
/*     */         } 
/* 858 */         if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowMappingEnd })) {
/* 859 */           ParserImpl.this.states.push(new ParserImpl.ParseFlowMappingEmptyValue());
/* 860 */           return ParserImpl.this.parseFlowNode();
/*     */         } 
/*     */       } 
/* 863 */       Token token = ParserImpl.this.scanner.getToken();
/* 864 */       MappingEndEvent mappingEndEvent = new MappingEndEvent(token.getStartMark(), token.getEndMark());
/* 865 */       ParserImpl.this.marks.pop();
/* 866 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 867 */         ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
/*     */       } else {
/* 869 */         ParserImpl.this.state = new ParserImpl.ParseFlowEndComment();
/*     */       } 
/* 871 */       return (Event)mappingEndEvent;
/*     */     } }
/*     */   
/*     */   private class ParseFlowMappingValue implements Production { private ParseFlowMappingValue() {}
/*     */     
/*     */     public Event produce() {
/* 877 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Value })) {
/* 878 */         Token token1 = ParserImpl.this.scanner.getToken();
/* 879 */         if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowEntry, Token.ID.FlowMappingEnd })) {
/* 880 */           ParserImpl.this.states.push(new ParserImpl.ParseFlowMappingKey(false));
/* 881 */           return ParserImpl.this.parseFlowNode();
/*     */         } 
/* 883 */         ParserImpl.this.state = new ParserImpl.ParseFlowMappingKey(false);
/* 884 */         return ParserImpl.this.processEmptyScalar(token1.getEndMark());
/*     */       } 
/*     */       
/* 887 */       ParserImpl.this.state = new ParserImpl.ParseFlowMappingKey(false);
/* 888 */       Token token = ParserImpl.this.scanner.peekToken();
/* 889 */       return ParserImpl.this.processEmptyScalar(token.getStartMark());
/*     */     } }
/*     */   
/*     */   private class ParseFlowMappingEmptyValue implements Production {
/*     */     private ParseFlowMappingEmptyValue() {}
/*     */     
/*     */     public Event produce() {
/* 896 */       ParserImpl.this.state = new ParserImpl.ParseFlowMappingKey(false);
/* 897 */       return ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
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
/*     */   private Event processEmptyScalar(Mark mark) {
/* 910 */     return (Event)new ScalarEvent(null, null, new ImplicitTuple(true, false), "", mark, mark, DumperOptions.ScalarStyle.PLAIN);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\parser\ParserImpl.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */