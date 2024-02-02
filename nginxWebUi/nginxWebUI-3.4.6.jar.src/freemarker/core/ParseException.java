/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.Template;
/*     */ import freemarker.template.utility.SecurityUtilities;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Set;
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
/*     */ public class ParseException
/*     */   extends IOException
/*     */   implements FMParserConstants
/*     */ {
/*     */   private static final String END_TAG_SYNTAX_HINT = "(Note that FreeMarker end-tags must have # or @ after the / character.)";
/*     */   public Token currentToken;
/*     */   private static volatile Boolean jbossToolsMode;
/*     */   private boolean messageAndDescriptionRendered;
/*     */   private String message;
/*     */   private String description;
/*     */   public int columnNumber;
/*     */   public int lineNumber;
/*     */   public int endColumnNumber;
/*     */   public int endLineNumber;
/*     */   public int[][] expectedTokenSequences;
/*     */   public String[] tokenImage;
/*  84 */   protected String eol = SecurityUtilities.getSystemProperty("line.separator", "\n");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected boolean specialConstructor;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String templateName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseException(Token currentTokenVal, int[][] expectedTokenSequencesVal, String[] tokenImageVal) {
/* 106 */     super("");
/* 107 */     this.currentToken = currentTokenVal;
/* 108 */     this.specialConstructor = true;
/* 109 */     this.expectedTokenSequences = expectedTokenSequencesVal;
/* 110 */     this.tokenImage = tokenImageVal;
/* 111 */     this.lineNumber = this.currentToken.next.beginLine;
/* 112 */     this.columnNumber = this.currentToken.next.beginColumn;
/* 113 */     this.endLineNumber = this.currentToken.next.endLine;
/* 114 */     this.endColumnNumber = this.currentToken.next.endColumn;
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
/*     */   @Deprecated
/*     */   protected ParseException() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ParseException(String description, int lineNumber, int columnNumber) {
/* 138 */     this(description, null, lineNumber, columnNumber, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseException(String description, Template template, int lineNumber, int columnNumber, int endLineNumber, int endColumnNumber) {
/* 146 */     this(description, template, lineNumber, columnNumber, endLineNumber, endColumnNumber, (Throwable)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseException(String description, Template template, int lineNumber, int columnNumber, int endLineNumber, int endColumnNumber, Throwable cause) {
/* 155 */     this(description, (template == null) ? null : template
/* 156 */         .getSourceName(), lineNumber, columnNumber, endLineNumber, endColumnNumber, cause);
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
/*     */   @Deprecated
/*     */   public ParseException(String description, Template template, int lineNumber, int columnNumber) {
/* 169 */     this(description, template, lineNumber, columnNumber, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ParseException(String description, Template template, int lineNumber, int columnNumber, Throwable cause) {
/* 179 */     this(description, (template == null) ? null : template
/* 180 */         .getSourceName(), lineNumber, columnNumber, 0, 0, cause);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseException(String description, Template template, Token tk) {
/* 190 */     this(description, template, tk, (Throwable)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseException(String description, Template template, Token tk, Throwable cause) {
/* 197 */     this(description, (template == null) ? null : template
/* 198 */         .getSourceName(), tk.beginLine, tk.beginColumn, tk.endLine, tk.endColumn, cause);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseException(String description, TemplateObject tobj) {
/* 208 */     this(description, tobj, (Throwable)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseException(String description, TemplateObject tobj, Throwable cause) {
/* 215 */     this(description, 
/* 216 */         (tobj.getTemplate() == null) ? null : tobj.getTemplate().getSourceName(), tobj.beginLine, tobj.beginColumn, tobj.endLine, tobj.endColumn, cause);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ParseException(String description, String templateName, int lineNumber, int columnNumber, int endLineNumber, int endColumnNumber, Throwable cause) {
/* 226 */     super(description);
/*     */     try {
/* 228 */       initCause(cause);
/* 229 */     } catch (Exception exception) {}
/*     */ 
/*     */     
/* 232 */     this.description = description;
/* 233 */     this.templateName = templateName;
/* 234 */     this.lineNumber = lineNumber;
/* 235 */     this.columnNumber = columnNumber;
/* 236 */     this.endLineNumber = endLineNumber;
/* 237 */     this.endColumnNumber = endColumnNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTemplateName(String templateName) {
/* 246 */     this.templateName = templateName;
/* 247 */     synchronized (this) {
/* 248 */       this.messageAndDescriptionRendered = false;
/* 249 */       this.message = null;
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
/*     */   
/*     */   public String getMessage() {
/* 263 */     synchronized (this) {
/* 264 */       if (this.messageAndDescriptionRendered) return this.message; 
/*     */     } 
/* 266 */     renderMessageAndDescription();
/* 267 */     synchronized (this) {
/* 268 */       return this.message;
/*     */     } 
/*     */   }
/*     */   
/*     */   private String getDescription() {
/* 273 */     synchronized (this) {
/* 274 */       if (this.messageAndDescriptionRendered) return this.description; 
/*     */     } 
/* 276 */     renderMessageAndDescription();
/* 277 */     synchronized (this) {
/* 278 */       return this.description;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getEditorMessage() {
/* 288 */     return getDescription();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getTemplateName() {
/* 298 */     return this.templateName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLineNumber() {
/* 305 */     return this.lineNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getColumnNumber() {
/* 312 */     return this.columnNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getEndLineNumber() {
/* 321 */     return this.endLineNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getEndColumnNumber() {
/* 331 */     return this.endColumnNumber;
/*     */   }
/*     */   
/*     */   private void renderMessageAndDescription() {
/* 335 */     String prefix, desc = getOrRenderDescription();
/*     */ 
/*     */     
/* 338 */     if (!isInJBossToolsMode()) {
/*     */       
/* 340 */       prefix = "Syntax error " + _MessageUtil.formatLocationForSimpleParsingError(this.templateName, this.lineNumber, this.columnNumber) + ":\n";
/*     */     } else {
/*     */       
/* 343 */       prefix = "[col. " + this.columnNumber + "] ";
/*     */     } 
/*     */     
/* 346 */     String msg = prefix + desc;
/* 347 */     desc = msg.substring(prefix.length());
/*     */     
/* 349 */     synchronized (this) {
/* 350 */       this.message = msg;
/* 351 */       this.description = desc;
/* 352 */       this.messageAndDescriptionRendered = true;
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean isInJBossToolsMode() {
/* 357 */     if (jbossToolsMode == null) {
/*     */       try {
/* 359 */         jbossToolsMode = Boolean.valueOf(
/* 360 */             (ParseException.class.getClassLoader().toString().indexOf("[org.jboss.ide.eclipse.freemarker:") != -1));
/*     */       }
/* 362 */       catch (Throwable e) {
/* 363 */         jbossToolsMode = Boolean.FALSE;
/*     */       } 
/*     */     }
/* 366 */     return jbossToolsMode.booleanValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getOrRenderDescription() {
/*     */     Set<String> expectedEndTokenDescs;
/* 374 */     synchronized (this) {
/* 375 */       if (this.description != null) return this.description;
/*     */     
/*     */     } 
/* 378 */     if (this.currentToken == null) {
/* 379 */       return null;
/*     */     }
/*     */     
/* 382 */     Token unexpectedTok = this.currentToken.next;
/*     */     
/* 384 */     if (unexpectedTok.kind == 0) {
/* 385 */       Set<String> endTokenDescs = getExpectedEndTokenDescs();
/* 386 */       return "Unexpected end of file reached." + (
/* 387 */         (endTokenDescs.size() == 0) ? "" : (" You have an unclosed " + 
/*     */         
/* 389 */         joinWithAnds(endTokenDescs) + ". Check if the FreeMarker end-tags are present, and aren't malformed. " + "(Note that FreeMarker end-tags must have # or @ after the / character.)"));
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 394 */     int maxExpectedTokenSequenceLength = 0;
/* 395 */     for (int i = 0; i < this.expectedTokenSequences.length; i++) {
/* 396 */       int[] expectedTokenSequence = this.expectedTokenSequences[i];
/* 397 */       if (maxExpectedTokenSequenceLength < expectedTokenSequence.length) {
/* 398 */         maxExpectedTokenSequenceLength = expectedTokenSequence.length;
/*     */       }
/*     */     } 
/*     */     
/* 402 */     StringBuilder tokenErrDesc = new StringBuilder();
/* 403 */     tokenErrDesc.append("Encountered ");
/* 404 */     boolean encounteredEndTag = false;
/* 405 */     for (int j = 0; j < maxExpectedTokenSequenceLength; j++) {
/* 406 */       if (j != 0) {
/* 407 */         tokenErrDesc.append(" ");
/*     */       }
/* 409 */       if (unexpectedTok.kind == 0) {
/* 410 */         tokenErrDesc.append(this.tokenImage[0]);
/*     */         
/*     */         break;
/*     */       } 
/* 414 */       String image = unexpectedTok.image;
/* 415 */       if (j == 0 && (
/* 416 */         image.startsWith("</") || image.startsWith("[/"))) {
/* 417 */         encounteredEndTag = true;
/*     */       }
/*     */       
/* 420 */       tokenErrDesc.append(StringUtil.jQuote(image));
/* 421 */       unexpectedTok = unexpectedTok.next;
/*     */     } 
/*     */     
/* 424 */     int unexpTokKind = this.currentToken.next.kind;
/* 425 */     if (getIsEndToken(unexpTokKind) || unexpTokKind == 54 || unexpTokKind == 9) {
/* 426 */       expectedEndTokenDescs = new LinkedHashSet<>(getExpectedEndTokenDescs());
/* 427 */       if (unexpTokKind == 54 || unexpTokKind == 9) {
/*     */         
/* 429 */         expectedEndTokenDescs.remove(getEndTokenDescIfIsEndToken(36));
/*     */       } else {
/* 431 */         expectedEndTokenDescs.remove(getEndTokenDescIfIsEndToken(unexpTokKind));
/*     */       } 
/*     */     } else {
/* 434 */       expectedEndTokenDescs = Collections.emptySet();
/*     */     } 
/*     */     
/* 437 */     if (!expectedEndTokenDescs.isEmpty()) {
/* 438 */       if (unexpTokKind == 54 || unexpTokKind == 9) {
/* 439 */         tokenErrDesc.append(", which can only be used where an #if");
/* 440 */         if (unexpTokKind == 54) {
/* 441 */           tokenErrDesc.append(" or #list");
/*     */         }
/* 443 */         tokenErrDesc.append(" could be closed");
/*     */       } 
/* 445 */       tokenErrDesc.append(", but at this place only ");
/* 446 */       tokenErrDesc.append((expectedEndTokenDescs.size() > 1) ? "these" : "this");
/* 447 */       tokenErrDesc.append(" can be closed: ");
/* 448 */       boolean first = true;
/* 449 */       for (String expectedEndTokenDesc : expectedEndTokenDescs) {
/* 450 */         if (!first) {
/* 451 */           tokenErrDesc.append(", ");
/*     */         } else {
/* 453 */           first = false;
/*     */         } 
/* 455 */         tokenErrDesc.append(
/* 456 */             !expectedEndTokenDesc.startsWith("\"") ? 
/* 457 */             StringUtil.jQuote(expectedEndTokenDesc) : expectedEndTokenDesc);
/*     */       } 
/*     */       
/* 460 */       tokenErrDesc.append(".");
/* 461 */       if (encounteredEndTag) {
/* 462 */         tokenErrDesc.append(" This usually because of wrong nesting of FreeMarker directives, like a missed or malformed end-tag somewhere. (Note that FreeMarker end-tags must have # or @ after the / character.)");
/*     */       }
/*     */       
/* 465 */       tokenErrDesc.append(this.eol);
/* 466 */       tokenErrDesc.append("Was ");
/*     */     } else {
/* 468 */       tokenErrDesc.append(", but was ");
/*     */     } 
/*     */     
/* 471 */     if (this.expectedTokenSequences.length == 1) {
/* 472 */       tokenErrDesc.append("expecting pattern:");
/*     */     } else {
/* 474 */       tokenErrDesc.append("expecting one of these patterns:");
/*     */     } 
/* 476 */     tokenErrDesc.append(this.eol);
/*     */     
/* 478 */     for (int k = 0; k < this.expectedTokenSequences.length; k++) {
/* 479 */       if (k != 0) {
/* 480 */         tokenErrDesc.append(this.eol);
/*     */       }
/* 482 */       tokenErrDesc.append("    ");
/* 483 */       int[] expectedTokenSequence = this.expectedTokenSequences[k];
/* 484 */       for (int m = 0; m < expectedTokenSequence.length; m++) {
/* 485 */         if (m != 0) {
/* 486 */           tokenErrDesc.append(' ');
/*     */         }
/* 488 */         tokenErrDesc.append(this.tokenImage[expectedTokenSequence[m]]);
/*     */       } 
/*     */     } 
/*     */     
/* 492 */     return tokenErrDesc.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Set<String> getExpectedEndTokenDescs() {
/* 500 */     Set<String> endTokenDescs = new LinkedHashSet<>();
/* 501 */     for (int i = 0; i < this.expectedTokenSequences.length; i++) {
/* 502 */       int[] sequence = this.expectedTokenSequences[i];
/* 503 */       for (int j = 0; j < sequence.length; j++) {
/* 504 */         int token = sequence[j];
/* 505 */         String endTokenDesc = getEndTokenDescIfIsEndToken(token);
/* 506 */         if (endTokenDesc != null) {
/* 507 */           endTokenDescs.add(endTokenDesc);
/*     */         }
/*     */       } 
/*     */     } 
/* 511 */     return endTokenDescs;
/*     */   }
/*     */   
/*     */   private boolean getIsEndToken(int token) {
/* 515 */     return (getEndTokenDescIfIsEndToken(token) != null);
/*     */   }
/*     */   
/*     */   private String getEndTokenDescIfIsEndToken(int token) {
/* 519 */     String endTokenDesc = null;
/* 520 */     switch (token) {
/*     */       case 42:
/* 522 */         endTokenDesc = "#foreach";
/*     */         break;
/*     */       case 37:
/* 525 */         endTokenDesc = "#list";
/*     */         break;
/*     */       case 39:
/* 528 */         endTokenDesc = "#sep";
/*     */         break;
/*     */       case 38:
/* 531 */         endTokenDesc = "#items";
/*     */         break;
/*     */       case 53:
/* 534 */         endTokenDesc = "#switch";
/*     */         break;
/*     */       case 36:
/* 537 */         endTokenDesc = "#if";
/*     */         break;
/*     */       case 51:
/* 540 */         endTokenDesc = "#compress";
/*     */         break;
/*     */       case 46:
/*     */       case 47:
/* 544 */         endTokenDesc = "#macro or #function";
/*     */         break;
/*     */       case 52:
/* 547 */         endTokenDesc = "#transform";
/*     */         break;
/*     */       case 71:
/* 550 */         endTokenDesc = "#escape";
/*     */         break;
/*     */       case 73:
/* 553 */         endTokenDesc = "#noescape";
/*     */         break;
/*     */       case 43:
/*     */       case 44:
/*     */       case 45:
/* 558 */         endTokenDesc = "#assign or #local or #global";
/*     */         break;
/*     */       case 41:
/* 561 */         endTokenDesc = "#attempt";
/*     */         break;
/*     */       case 138:
/* 564 */         endTokenDesc = "\"{\"";
/*     */         break;
/*     */       case 134:
/* 567 */         endTokenDesc = "\"[\"";
/*     */         break;
/*     */       case 136:
/* 570 */         endTokenDesc = "\"(\"";
/*     */         break;
/*     */       case 75:
/* 573 */         endTokenDesc = "@...";
/*     */         break;
/*     */     } 
/* 576 */     return endTokenDesc;
/*     */   }
/*     */   
/*     */   private String joinWithAnds(Collection<String> strings) {
/* 580 */     StringBuilder sb = new StringBuilder();
/* 581 */     for (String s : strings) {
/* 582 */       if (sb.length() != 0) {
/* 583 */         sb.append(" and ");
/*     */       }
/* 585 */       sb.append(s);
/*     */     } 
/* 587 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String add_escapes(String str) {
/* 596 */     StringBuilder retval = new StringBuilder();
/*     */     
/* 598 */     for (int i = 0; i < str.length(); i++) {
/* 599 */       char ch; switch (str.charAt(i)) {
/*     */         case '\000':
/*     */           break;
/*     */         
/*     */         case '\b':
/* 604 */           retval.append("\\b");
/*     */           break;
/*     */         case '\t':
/* 607 */           retval.append("\\t");
/*     */           break;
/*     */         case '\n':
/* 610 */           retval.append("\\n");
/*     */           break;
/*     */         case '\f':
/* 613 */           retval.append("\\f");
/*     */           break;
/*     */         case '\r':
/* 616 */           retval.append("\\r");
/*     */           break;
/*     */         case '"':
/* 619 */           retval.append("\\\"");
/*     */           break;
/*     */         case '\'':
/* 622 */           retval.append("\\'");
/*     */           break;
/*     */         case '\\':
/* 625 */           retval.append("\\\\");
/*     */           break;
/*     */         default:
/* 628 */           if ((ch = str.charAt(i)) < ' ' || ch > '~') {
/* 629 */             String s = "0000" + Integer.toString(ch, 16);
/* 630 */             retval.append("\\u" + s.substring(s.length() - 4, s.length())); break;
/*     */           } 
/* 632 */           retval.append(ch);
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 637 */     return retval.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ParseException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */