/*      */ package freemarker.core;
/*      */ 
/*      */ import freemarker.cache.TemplateNameFormat;
/*      */ import freemarker.cache._CacheAPI;
/*      */ import freemarker.ext.beans.BeansWrapper;
/*      */ import freemarker.log.Logger;
/*      */ import freemarker.template.Configuration;
/*      */ import freemarker.template.MalformedTemplateNameException;
/*      */ import freemarker.template.ObjectWrapper;
/*      */ import freemarker.template.SimpleHash;
/*      */ import freemarker.template.SimpleSequence;
/*      */ import freemarker.template.Template;
/*      */ import freemarker.template.TemplateCollectionModel;
/*      */ import freemarker.template.TemplateDateModel;
/*      */ import freemarker.template.TemplateDirectiveBody;
/*      */ import freemarker.template.TemplateDirectiveModel;
/*      */ import freemarker.template.TemplateException;
/*      */ import freemarker.template.TemplateExceptionHandler;
/*      */ import freemarker.template.TemplateHashModel;
/*      */ import freemarker.template.TemplateHashModelEx;
/*      */ import freemarker.template.TemplateHashModelEx2;
/*      */ import freemarker.template.TemplateModel;
/*      */ import freemarker.template.TemplateModelException;
/*      */ import freemarker.template.TemplateModelIterator;
/*      */ import freemarker.template.TemplateNodeModel;
/*      */ import freemarker.template.TemplateNumberModel;
/*      */ import freemarker.template.TemplateScalarModel;
/*      */ import freemarker.template.TemplateSequenceModel;
/*      */ import freemarker.template.TemplateTransformModel;
/*      */ import freemarker.template.TransformControl;
/*      */ import freemarker.template._TemplateAPI;
/*      */ import freemarker.template.utility.DateUtil;
/*      */ import freemarker.template.utility.NullWriter;
/*      */ import freemarker.template.utility.StringUtil;
/*      */ import freemarker.template.utility.TemplateModelUtils;
/*      */ import freemarker.template.utility.UndeclaredThrowableException;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.StringWriter;
/*      */ import java.io.Writer;
/*      */ import java.sql.Date;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.text.Collator;
/*      */ import java.text.DecimalFormat;
/*      */ import java.text.DecimalFormatSymbols;
/*      */ import java.text.NumberFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class Environment
/*      */   extends Configurable
/*      */ {
/*  101 */   private static final ThreadLocal threadEnv = new ThreadLocal();
/*      */   
/*  103 */   private static final Logger LOG = Logger.getLogger("freemarker.runtime");
/*  104 */   private static final Logger ATTEMPT_LOGGER = Logger.getLogger("freemarker.runtime.attempt");
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  109 */   private static final DecimalFormat C_NUMBER_FORMAT_ICI_2_3_20 = new DecimalFormat("0.################", new DecimalFormatSymbols(Locale.US));
/*      */ 
/*      */   
/*      */   static {
/*  113 */     C_NUMBER_FORMAT_ICI_2_3_20.setGroupingUsed(false);
/*  114 */     C_NUMBER_FORMAT_ICI_2_3_20.setDecimalSeparatorAlwaysShown(false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  120 */   private static final DecimalFormat C_NUMBER_FORMAT_ICI_2_3_21 = (DecimalFormat)C_NUMBER_FORMAT_ICI_2_3_20.clone();
/*      */   static {
/*  122 */     DecimalFormatSymbols symbols = C_NUMBER_FORMAT_ICI_2_3_21.getDecimalFormatSymbols();
/*  123 */     symbols.setInfinity("INF");
/*  124 */     symbols.setNaN("NaN");
/*  125 */     C_NUMBER_FORMAT_ICI_2_3_21.setDecimalFormatSymbols(symbols);
/*      */   }
/*      */   
/*      */   private final Configuration configuration;
/*      */   private final boolean incompatibleImprovementsGE2328;
/*      */   private final TemplateHashModel rootDataModel;
/*  131 */   private TemplateElement[] instructionStack = new TemplateElement[16];
/*  132 */   private int instructionStackSize = 0;
/*  133 */   private final ArrayList recoveredErrorStack = new ArrayList();
/*      */ 
/*      */   
/*      */   private TemplateNumberFormat cachedTemplateNumberFormat;
/*      */ 
/*      */   
/*      */   private Map<String, TemplateNumberFormat> cachedTemplateNumberFormats;
/*      */ 
/*      */   
/*      */   private TemplateDateFormat[] cachedTempDateFormatArray;
/*      */ 
/*      */   
/*      */   private HashMap<String, TemplateDateFormat>[] cachedTempDateFormatsByFmtStrArray;
/*      */ 
/*      */   
/*      */   private static final int CACHED_TDFS_ZONELESS_INPUT_OFFS = 4;
/*      */ 
/*      */   
/*      */   private static final int CACHED_TDFS_SQL_D_T_TZ_OFFS = 8;
/*      */ 
/*      */   
/*      */   private static final int CACHED_TDFS_LENGTH = 16;
/*      */ 
/*      */   
/*      */   private Boolean cachedSQLDateAndTimeTimeZoneSameAsNormal;
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   private NumberFormat cNumberFormat;
/*      */ 
/*      */   
/*      */   private DateUtil.DateToISO8601CalendarFactory isoBuiltInCalendarFactory;
/*      */   
/*      */   private Collator cachedCollator;
/*      */   
/*      */   private Writer out;
/*      */   
/*      */   private Macro.Context currentMacroContext;
/*      */   
/*      */   private LocalContextStack localContextStack;
/*      */   
/*      */   private final Namespace mainNamespace;
/*      */   
/*      */   private Namespace currentNamespace;
/*      */   
/*      */   private Namespace globalNamespace;
/*      */   
/*      */   private HashMap<String, Namespace> loadedLibs;
/*      */   
/*      */   private Configurable legacyParent;
/*      */   
/*      */   private boolean inAttemptBlock;
/*      */   
/*      */   private Throwable lastThrowable;
/*      */   
/*      */   private TemplateModel lastReturnValue;
/*      */   
/*  190 */   private Map<Object, Namespace> macroToNamespaceLookup = new IdentityHashMap<>();
/*      */   
/*      */   private TemplateNodeModel currentVisitorNode;
/*      */   
/*      */   private TemplateSequenceModel nodeNamespaces;
/*      */   
/*      */   private int nodeNamespaceIndex;
/*      */   
/*      */   private String currentNodeName;
/*      */   
/*      */   private String currentNodeNS;
/*      */   
/*      */   private String cachedURLEscapingCharset;
/*      */   
/*      */   private boolean cachedURLEscapingCharsetSet;
/*      */   
/*      */   private boolean fastInvalidReferenceExceptions;
/*      */ 
/*      */   
/*      */   public static Environment getCurrentEnvironment() {
/*  210 */     return threadEnv.get();
/*      */   }
/*      */   
/*      */   static void setCurrentEnvironment(Environment env) {
/*  214 */     threadEnv.set(env);
/*      */   }
/*      */   
/*      */   public Environment(Template template, TemplateHashModel rootDataModel, Writer out) {
/*  218 */     super((Configurable)template);
/*  219 */     this.configuration = template.getConfiguration();
/*  220 */     this.incompatibleImprovementsGE2328 = (this.configuration.getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_28);
/*  221 */     this.globalNamespace = new Namespace(null);
/*  222 */     this.currentNamespace = this.mainNamespace = new Namespace(template);
/*  223 */     this.out = out;
/*  224 */     this.rootDataModel = rootDataModel;
/*  225 */     importMacros(template);
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
/*      */   @Deprecated
/*      */   public Template getTemplate() {
/*  240 */     return (Template)getParent();
/*      */   }
/*      */ 
/*      */   
/*      */   Template getTemplate230() {
/*  245 */     Template legacyParent = (Template)this.legacyParent;
/*  246 */     return (legacyParent != null) ? legacyParent : getTemplate();
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
/*      */   public Template getMainTemplate() {
/*  259 */     return this.mainNamespace.getTemplate();
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
/*      */   public Template getCurrentTemplate() {
/*  276 */     int ln = this.instructionStackSize;
/*  277 */     return (ln == 0) ? getMainTemplate() : this.instructionStack[ln - 1].getTemplate();
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
/*      */   public DirectiveCallPlace getCurrentDirectiveCallPlace() {
/*  290 */     int ln = this.instructionStackSize;
/*  291 */     if (ln == 0) return null; 
/*  292 */     TemplateElement te = this.instructionStack[ln - 1];
/*  293 */     if (te instanceof UnifiedCall) return (UnifiedCall)te; 
/*  294 */     if (te instanceof Macro && ln > 1 && this.instructionStack[ln - 2] instanceof UnifiedCall) {
/*  295 */       return (UnifiedCall)this.instructionStack[ln - 2];
/*      */     }
/*  297 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void clearCachedValues() {
/*  304 */     this.cachedTemplateNumberFormats = null;
/*  305 */     this.cachedTemplateNumberFormat = null;
/*      */     
/*  307 */     this.cachedTempDateFormatArray = null;
/*  308 */     this.cachedTempDateFormatsByFmtStrArray = null;
/*      */     
/*  310 */     this.cachedCollator = null;
/*  311 */     this.cachedURLEscapingCharset = null;
/*  312 */     this.cachedURLEscapingCharsetSet = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void process() throws TemplateException, IOException {
/*  319 */     Object savedEnv = threadEnv.get();
/*  320 */     threadEnv.set(this);
/*      */     
/*      */     try {
/*  323 */       clearCachedValues();
/*      */       try {
/*  325 */         doAutoImportsAndIncludes(this);
/*  326 */         visit(getTemplate().getRootTreeNode());
/*      */         
/*  328 */         if (getAutoFlush()) {
/*  329 */           this.out.flush();
/*      */         }
/*      */       } finally {
/*      */         
/*  333 */         clearCachedValues();
/*      */       } 
/*      */     } finally {
/*  336 */       threadEnv.set(savedEnv);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void visit(TemplateElement element) throws IOException, TemplateException {
/*  345 */     pushElement(element);
/*      */     try {
/*  347 */       TemplateElement[] templateElementsToVisit = element.accept(this);
/*  348 */       if (templateElementsToVisit != null) {
/*  349 */         for (TemplateElement el : templateElementsToVisit) {
/*  350 */           if (el == null) {
/*      */             break;
/*      */           }
/*  353 */           visit(el);
/*      */         } 
/*      */       }
/*  356 */     } catch (TemplateException te) {
/*  357 */       handleTemplateException(te);
/*      */     } finally {
/*  359 */       popElement();
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
/*      */   final void visit(TemplateElement[] elementBuffer) throws IOException, TemplateException {
/*  371 */     if (elementBuffer == null) {
/*      */       return;
/*      */     }
/*  374 */     for (TemplateElement element : elementBuffer) {
/*  375 */       if (element == null) {
/*      */         break;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  381 */       pushElement(element);
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
/*      */   final void visit(TemplateElement[] elementBuffer, Writer out) throws IOException, TemplateException {
/*  407 */     Writer prevOut = this.out;
/*  408 */     this.out = out;
/*      */     try {
/*  410 */       visit(elementBuffer);
/*      */     } finally {
/*  412 */       this.out = prevOut;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private TemplateElement replaceTopElement(TemplateElement element) {
/*  418 */     this.instructionStack[this.instructionStackSize - 1] = element; return element;
/*      */   }
/*      */   
/*  421 */   private static final TemplateModel[] NO_OUT_ARGS = new TemplateModel[0];
/*      */   
/*      */   private static final int TERSE_MODE_INSTRUCTION_STACK_TRACE_LIMIT = 10;
/*      */   
/*      */   private IdentityHashMap<Object, Object> customStateVariables;
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void visit(TemplateElement element, TemplateDirectiveModel directiveModel, Map args, List bodyParameterNames) throws TemplateException, IOException {
/*  430 */     visit(new TemplateElement[] { element }, directiveModel, args, bodyParameterNames);
/*      */   }
/*      */ 
/*      */   
/*      */   void visit(TemplateElement[] childBuffer, TemplateDirectiveModel directiveModel, Map args, final List bodyParameterNames) throws TemplateException, IOException {
/*      */     TemplateDirectiveBody nested;
/*      */     final TemplateModel[] outArgs;
/*  437 */     if (childBuffer == null) {
/*  438 */       nested = null;
/*      */     } else {
/*  440 */       nested = new NestedElementTemplateDirectiveBody(childBuffer);
/*      */     } 
/*      */     
/*  443 */     if (bodyParameterNames == null || bodyParameterNames.isEmpty()) {
/*  444 */       outArgs = NO_OUT_ARGS;
/*      */     } else {
/*  446 */       outArgs = new TemplateModel[bodyParameterNames.size()];
/*      */     } 
/*  448 */     if (outArgs.length > 0) {
/*  449 */       pushLocalContext(new LocalContext()
/*      */           {
/*      */             public TemplateModel getLocalVariable(String name)
/*      */             {
/*  453 */               int index = bodyParameterNames.indexOf(name);
/*  454 */               return (index != -1) ? outArgs[index] : null;
/*      */             }
/*      */ 
/*      */             
/*      */             public Collection getLocalVariableNames() {
/*  459 */               return bodyParameterNames;
/*      */             }
/*      */           });
/*      */     }
/*      */     try {
/*  464 */       directiveModel.execute(this, args, outArgs, nested);
/*  465 */     } catch (FlowControlException e) {
/*  466 */       throw e;
/*  467 */     } catch (TemplateException e) {
/*  468 */       throw e;
/*  469 */     } catch (IOException e) {
/*      */       
/*  471 */       throw e;
/*  472 */     } catch (Exception e) {
/*  473 */       if (EvalUtil.shouldWrapUncheckedException(e, this)) {
/*  474 */         throw new _MiscTemplateException(e, this, "Directive has thrown an unchecked exception; see the cause exception.");
/*      */       }
/*  476 */       if (e instanceof RuntimeException) {
/*  477 */         throw (RuntimeException)e;
/*      */       }
/*  479 */       throw new UndeclaredThrowableException(e);
/*      */     } finally {
/*      */       
/*  482 */       if (outArgs.length > 0) {
/*  483 */         this.localContextStack.pop();
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
/*      */   
/*      */   void visitAndTransform(TemplateElement[] elementBuffer, TemplateTransformModel transform, Map args) throws TemplateException, IOException {
/*      */     try {
/*  503 */       Writer tw = transform.getWriter(this.out, args);
/*  504 */       if (tw == null) tw = EMPTY_BODY_WRITER; 
/*  505 */       TransformControl tc = (tw instanceof TransformControl) ? (TransformControl)tw : null;
/*      */ 
/*      */ 
/*      */       
/*  509 */       Writer prevOut = this.out;
/*  510 */       this.out = tw;
/*      */       try {
/*  512 */         if (tc == null || tc.onStart() != 0) {
/*      */           do {
/*  514 */             visit(elementBuffer);
/*  515 */           } while (tc != null && tc.afterBody() == 0);
/*      */         }
/*  517 */       } catch (Throwable t) {
/*      */         try {
/*  519 */           if (tc != null && (!(t instanceof FlowControlException) || 
/*      */             
/*  521 */             getConfiguration().getIncompatibleImprovements().intValue() < _TemplateAPI.VERSION_INT_2_3_27)) {
/*      */             
/*  523 */             tc.onError(t);
/*      */           } else {
/*  525 */             throw t;
/*      */           } 
/*  527 */         } catch (TemplateException|IOException|Error e) {
/*  528 */           throw e;
/*  529 */         } catch (Throwable e) {
/*  530 */           if (EvalUtil.shouldWrapUncheckedException(e, this)) {
/*  531 */             throw new _MiscTemplateException(e, this, "Transform has thrown an unchecked exception; see the cause exception.");
/*      */           }
/*  533 */           if (e instanceof RuntimeException) {
/*  534 */             throw (RuntimeException)e;
/*      */           }
/*  536 */           throw new UndeclaredThrowableException(e);
/*      */         } 
/*      */       } finally {
/*      */         
/*  540 */         this.out = prevOut;
/*  541 */         if (prevOut != tw) {
/*  542 */           tw.close();
/*      */         }
/*      */       } 
/*  545 */     } catch (TemplateException te) {
/*  546 */       handleTemplateException(te);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void visitAttemptRecover(AttemptBlock attemptBlock, TemplateElement attemptedSection, RecoveryBlock recoverySection) throws TemplateException, IOException {
/*  556 */     Writer prevOut = this.out;
/*  557 */     StringWriter sw = new StringWriter();
/*  558 */     this.out = sw;
/*  559 */     TemplateException thrownException = null;
/*  560 */     boolean lastFIRE = setFastInvalidReferenceExceptions(false);
/*  561 */     boolean lastInAttemptBlock = this.inAttemptBlock;
/*      */     try {
/*  563 */       this.inAttemptBlock = true;
/*  564 */       visit(attemptedSection);
/*  565 */     } catch (TemplateException te) {
/*  566 */       thrownException = te;
/*      */     } finally {
/*  568 */       this.inAttemptBlock = lastInAttemptBlock;
/*  569 */       setFastInvalidReferenceExceptions(lastFIRE);
/*  570 */       this.out = prevOut;
/*      */     } 
/*  572 */     if (thrownException != null) {
/*  573 */       if (ATTEMPT_LOGGER.isDebugEnabled()) {
/*  574 */         ATTEMPT_LOGGER.debug("Error in attempt block " + attemptBlock
/*  575 */             .getStartLocationQuoted(), (Throwable)thrownException);
/*      */       }
/*      */       try {
/*  578 */         this.recoveredErrorStack.add(thrownException);
/*  579 */         visit(recoverySection);
/*      */       } finally {
/*  581 */         this.recoveredErrorStack.remove(this.recoveredErrorStack.size() - 1);
/*      */       } 
/*      */     } else {
/*  584 */       this.out.write(sw.toString());
/*      */     } 
/*      */   }
/*      */   
/*      */   String getCurrentRecoveredErrorMessage() throws TemplateException {
/*  589 */     if (this.recoveredErrorStack.isEmpty()) {
/*  590 */       throw new _MiscTemplateException(this, ".error is not available outside of a #recover block");
/*      */     }
/*  592 */     return ((Throwable)this.recoveredErrorStack.get(this.recoveredErrorStack.size() - 1)).getMessage();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isInAttemptBlock() {
/*  603 */     return this.inAttemptBlock;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void invokeNestedContent(BodyInstruction.Context bodyCtx) throws TemplateException, IOException {
/*  610 */     Macro.Context invokingMacroContext = getCurrentMacroContext();
/*  611 */     LocalContextStack prevLocalContextStack = this.localContextStack;
/*  612 */     TemplateObject callPlace = invokingMacroContext.callPlace;
/*      */     
/*  614 */     TemplateElement[] nestedContentBuffer = (callPlace instanceof TemplateElement) ? ((TemplateElement)callPlace).getChildBuffer() : null;
/*  615 */     if (nestedContentBuffer != null) {
/*  616 */       this.currentMacroContext = invokingMacroContext.prevMacroContext;
/*  617 */       this.currentNamespace = invokingMacroContext.nestedContentNamespace;
/*      */ 
/*      */       
/*  620 */       boolean parentReplacementOn = isBeforeIcI2322();
/*  621 */       Configurable prevParent = getParent();
/*  622 */       if (parentReplacementOn) {
/*  623 */         setParent((Configurable)this.currentNamespace.getTemplate());
/*      */       } else {
/*  625 */         this.legacyParent = (Configurable)this.currentNamespace.getTemplate();
/*      */       } 
/*      */       
/*  628 */       this.localContextStack = invokingMacroContext.prevLocalContextStack;
/*  629 */       if (invokingMacroContext.nestedContentParameterNames != null) {
/*  630 */         pushLocalContext(bodyCtx);
/*      */       }
/*      */       try {
/*  633 */         visit(nestedContentBuffer);
/*      */       } finally {
/*  635 */         if (invokingMacroContext.nestedContentParameterNames != null) {
/*  636 */           this.localContextStack.pop();
/*      */         }
/*  638 */         this.currentMacroContext = invokingMacroContext;
/*  639 */         this.currentNamespace = getMacroNamespace(invokingMacroContext.getMacro());
/*  640 */         if (parentReplacementOn) {
/*  641 */           setParent(prevParent);
/*      */         } else {
/*  643 */           this.legacyParent = prevParent;
/*      */         } 
/*  645 */         this.localContextStack = prevLocalContextStack;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean visitIteratorBlock(IteratorBlock.IterationContext ictxt) throws TemplateException, IOException {
/*  655 */     pushLocalContext(ictxt);
/*      */     try {
/*  657 */       return ictxt.accept(this);
/*  658 */     } catch (TemplateException te) {
/*  659 */       handleTemplateException(te);
/*  660 */       return true;
/*      */     } finally {
/*  662 */       this.localContextStack.pop();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   IteratorBlock.IterationContext findEnclosingIterationContextWithVisibleVariable(String loopVarName) {
/*  673 */     return findEnclosingIterationContext(loopVarName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   IteratorBlock.IterationContext findClosestEnclosingIterationContext() {
/*  680 */     return findEnclosingIterationContext((String)null);
/*      */   }
/*      */   
/*      */   private IteratorBlock.IterationContext findEnclosingIterationContext(String visibleLoopVarName) {
/*  684 */     LocalContextStack ctxStack = getLocalContextStack();
/*  685 */     if (ctxStack != null) {
/*  686 */       for (int i = ctxStack.size() - 1; i >= 0; i--) {
/*  687 */         Object ctx = ctxStack.get(i);
/*  688 */         if (ctx instanceof IteratorBlock.IterationContext && (visibleLoopVarName == null || ((IteratorBlock.IterationContext)ctx)
/*      */ 
/*      */           
/*  691 */           .hasVisibleLoopVar(visibleLoopVarName))) {
/*  692 */           return (IteratorBlock.IterationContext)ctx;
/*      */         }
/*      */       } 
/*      */     }
/*  696 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   TemplateModel evaluateWithNewLocal(Expression exp, String lambdaArgName, TemplateModel lamdaArgValue) throws TemplateException {
/*  704 */     pushLocalContext(new LocalContextWithNewLocal(lambdaArgName, lamdaArgValue));
/*      */     try {
/*  706 */       return exp.eval(this);
/*      */     } finally {
/*  708 */       this.localContextStack.pop();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private static class LocalContextWithNewLocal
/*      */     implements LocalContext
/*      */   {
/*      */     private final String lambdaArgName;
/*      */     private final TemplateModel lambdaArgValue;
/*      */     
/*      */     public LocalContextWithNewLocal(String lambdaArgName, TemplateModel lambdaArgValue) {
/*  720 */       this.lambdaArgName = lambdaArgName;
/*  721 */       this.lambdaArgValue = lambdaArgValue;
/*      */     }
/*      */ 
/*      */     
/*      */     public TemplateModel getLocalVariable(String name) throws TemplateModelException {
/*  726 */       return name.equals(this.lambdaArgName) ? this.lambdaArgValue : null;
/*      */     }
/*      */ 
/*      */     
/*      */     public Collection getLocalVariableNames() throws TemplateModelException {
/*  731 */       return Collections.singleton(this.lambdaArgName);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void invokeNodeHandlerFor(TemplateNodeModel node, TemplateSequenceModel namespaces) throws TemplateException, IOException {
/*  740 */     if (this.nodeNamespaces == null) {
/*  741 */       SimpleSequence ss = new SimpleSequence(1, (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/*  742 */       ss.add(this.currentNamespace);
/*  743 */       this.nodeNamespaces = (TemplateSequenceModel)ss;
/*      */     } 
/*  745 */     int prevNodeNamespaceIndex = this.nodeNamespaceIndex;
/*  746 */     String prevNodeName = this.currentNodeName;
/*  747 */     String prevNodeNS = this.currentNodeNS;
/*  748 */     TemplateSequenceModel prevNodeNamespaces = this.nodeNamespaces;
/*  749 */     TemplateNodeModel prevVisitorNode = this.currentVisitorNode;
/*  750 */     this.currentVisitorNode = node;
/*  751 */     if (namespaces != null) {
/*  752 */       this.nodeNamespaces = namespaces;
/*      */     }
/*      */     try {
/*  755 */       TemplateModel macroOrTransform = getNodeProcessor(node);
/*  756 */       if (macroOrTransform instanceof Macro) {
/*  757 */         invokeMacro((Macro)macroOrTransform, (Map<String, ? extends Expression>)null, (List<? extends Expression>)null, (List<String>)null, (TemplateObject)null);
/*  758 */       } else if (macroOrTransform instanceof TemplateTransformModel) {
/*  759 */         visitAndTransform((TemplateElement[])null, (TemplateTransformModel)macroOrTransform, (Map)null);
/*      */       } else {
/*  761 */         String nodeType = node.getNodeType();
/*  762 */         if (nodeType != null) {
/*      */           
/*  764 */           if (nodeType.equals("text") && node instanceof TemplateScalarModel) {
/*  765 */             this.out.write(((TemplateScalarModel)node).getAsString());
/*  766 */           } else if (nodeType.equals("document")) {
/*  767 */             recurse(node, namespaces);
/*      */ 
/*      */           
/*      */           }
/*  771 */           else if (!nodeType.equals("pi") && 
/*  772 */             !nodeType.equals("comment") && 
/*  773 */             !nodeType.equals("document_type")) {
/*  774 */             throw new _MiscTemplateException(this, 
/*  775 */                 noNodeHandlerDefinedDescription(node, node.getNodeNamespace(), nodeType));
/*      */           } 
/*      */         } else {
/*  778 */           throw new _MiscTemplateException(this, 
/*  779 */               noNodeHandlerDefinedDescription(node, node.getNodeNamespace(), "default"));
/*      */         } 
/*      */       } 
/*      */     } finally {
/*  783 */       this.currentVisitorNode = prevVisitorNode;
/*  784 */       this.nodeNamespaceIndex = prevNodeNamespaceIndex;
/*  785 */       this.currentNodeName = prevNodeName;
/*  786 */       this.currentNodeNS = prevNodeNS;
/*  787 */       this.nodeNamespaces = prevNodeNamespaces;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private Object[] noNodeHandlerDefinedDescription(TemplateNodeModel node, String ns, String nodeType) throws TemplateModelException {
/*      */     String nsPrefix;
/*  795 */     if (ns != null) {
/*  796 */       if (ns.length() > 0) {
/*  797 */         nsPrefix = " and namespace ";
/*      */       } else {
/*  799 */         nsPrefix = " and no namespace";
/*      */       } 
/*      */     } else {
/*  802 */       nsPrefix = "";
/*  803 */       ns = "";
/*      */     } 
/*  805 */     return new Object[] { "No macro or directive is defined for node named ", new _DelayedJQuote(node
/*  806 */           .getNodeName()), nsPrefix, ns, ", and there is no fallback handler called @", nodeType, " either." };
/*      */   }
/*      */ 
/*      */   
/*      */   void fallback() throws TemplateException, IOException {
/*  811 */     TemplateModel macroOrTransform = getNodeProcessor(this.currentNodeName, this.currentNodeNS, this.nodeNamespaceIndex);
/*  812 */     if (macroOrTransform instanceof Macro) {
/*  813 */       invokeMacro((Macro)macroOrTransform, (Map<String, ? extends Expression>)null, (List<? extends Expression>)null, (List<String>)null, (TemplateObject)null);
/*  814 */     } else if (macroOrTransform instanceof TemplateTransformModel) {
/*  815 */       visitAndTransform((TemplateElement[])null, (TemplateTransformModel)macroOrTransform, (Map)null);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void invokeMacro(Macro macro, Map<String, ? extends Expression> namedArgs, List<? extends Expression> positionalArgs, List<String> bodyParameterNames, TemplateObject callPlace) throws TemplateException, IOException {
/*  825 */     invokeMacroOrFunctionCommonPart(macro, namedArgs, positionalArgs, bodyParameterNames, callPlace);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   TemplateModel invokeFunction(Environment env, Macro func, List<? extends Expression> argumentExps, TemplateObject callPlace) throws TemplateException {
/*  834 */     env.setLastReturnValue((TemplateModel)null);
/*  835 */     if (!func.isFunction()) {
/*  836 */       throw new _MiscTemplateException(env, "A macro cannot be called in an expression. (Functions can be.)");
/*      */     }
/*  838 */     Writer prevOut = env.getOut();
/*      */     try {
/*  840 */       env.setOut((Writer)NullWriter.INSTANCE);
/*  841 */       env.invokeMacro(func, (Map<String, ? extends Expression>)null, argumentExps, (List<String>)null, callPlace);
/*  842 */     } catch (IOException e) {
/*      */       
/*  844 */       throw new TemplateException("Unexpected exception during function execution", e, env);
/*      */     } finally {
/*  846 */       env.setOut(prevOut);
/*      */     } 
/*  848 */     return env.getLastReturnValue();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void invokeMacroOrFunctionCommonPart(Macro macroOrFunction, Map<String, ? extends Expression> namedArgs, List<? extends Expression> positionalArgs, List<String> bodyParameterNames, TemplateObject callPlace) throws TemplateException, IOException {
/*      */     boolean elementPushed;
/*  855 */     if (macroOrFunction == Macro.DO_NOTHING_MACRO) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  860 */     if (!this.incompatibleImprovementsGE2328) {
/*      */ 
/*      */       
/*  863 */       pushElement(macroOrFunction);
/*  864 */       elementPushed = true;
/*      */     } else {
/*  866 */       elementPushed = false;
/*      */     } 
/*      */     try {
/*  869 */       macroOrFunction.getClass(); Macro.Context macroCtx = new Macro.Context(macroOrFunction, this, callPlace, bodyParameterNames);
/*      */       
/*  871 */       setMacroContextLocalsFromArguments(macroCtx, macroOrFunction, namedArgs, positionalArgs);
/*      */       
/*  873 */       if (!elementPushed) {
/*  874 */         pushElement(macroOrFunction);
/*  875 */         elementPushed = true;
/*      */       } 
/*      */       
/*  878 */       Macro.Context prevMacroCtx = this.currentMacroContext;
/*  879 */       this.currentMacroContext = macroCtx;
/*      */       
/*  881 */       LocalContextStack prevLocalContextStack = this.localContextStack;
/*  882 */       this.localContextStack = null;
/*      */       
/*  884 */       Namespace prevNamespace = this.currentNamespace;
/*  885 */       this.currentNamespace = getMacroNamespace(macroOrFunction);
/*      */       
/*      */       try {
/*  888 */         macroCtx.checkParamsSetAndApplyDefaults(this);
/*  889 */         visit(macroOrFunction.getChildBuffer());
/*  890 */       } catch (Return return_) {
/*      */       
/*  892 */       } catch (TemplateException te) {
/*  893 */         handleTemplateException(te);
/*      */       } finally {
/*  895 */         this.currentMacroContext = prevMacroCtx;
/*  896 */         this.localContextStack = prevLocalContextStack;
/*  897 */         this.currentNamespace = prevNamespace;
/*      */       } 
/*      */     } finally {
/*  900 */       if (elementPushed) {
/*  901 */         popElement();
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
/*      */   private void setMacroContextLocalsFromArguments(Macro.Context macroCtx, Macro macro, Map<String, ? extends Expression> namedArgs, List<? extends Expression> positionalArgs) throws TemplateException {
/*  914 */     String catchAllParamName = macro.getCatchAll();
/*  915 */     SimpleHash namedCatchAllParamValue = null;
/*  916 */     SimpleSequence positionalCatchAllParamValue = null;
/*  917 */     int nextPositionalArgToAssignIdx = 0;
/*      */ 
/*      */     
/*  920 */     WithArgsState withArgsState = getWithArgState(macro);
/*  921 */     if (withArgsState != null) {
/*  922 */       TemplateHashModelEx byNameWithArgs = withArgsState.byName;
/*  923 */       TemplateSequenceModel byPositionWithArgs = withArgsState.byPosition;
/*      */       
/*  925 */       if (byNameWithArgs != null) {
/*      */         
/*  927 */         TemplateHashModelEx2.KeyValuePairIterator withArgsKVPIter = TemplateModelUtils.getKeyValuePairIterator(byNameWithArgs);
/*  928 */         while (withArgsKVPIter.hasNext()) {
/*  929 */           TemplateHashModelEx2.KeyValuePair withArgKVP = withArgsKVPIter.next();
/*      */ 
/*      */ 
/*      */           
/*  933 */           TemplateModel argNameTM = withArgKVP.getKey();
/*  934 */           if (!(argNameTM instanceof TemplateScalarModel)) {
/*  935 */             throw new _TemplateModelException(new Object[] { "Expected string keys in the \"with args\" hash, but one of the keys was ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(argNameTM)), "." });
/*      */           }
/*      */ 
/*      */           
/*  939 */           String argName = EvalUtil.modelToString((TemplateScalarModel)argNameTM, null, null);
/*      */ 
/*      */           
/*  942 */           TemplateModel argValue = withArgKVP.getValue();
/*      */ 
/*      */ 
/*      */           
/*  946 */           boolean isArgNameDeclared = macro.hasArgNamed(argName);
/*  947 */           if (isArgNameDeclared) {
/*  948 */             macroCtx.setLocalVar(argName, argValue); continue;
/*  949 */           }  if (catchAllParamName != null) {
/*  950 */             if (namedCatchAllParamValue == null) {
/*  951 */               namedCatchAllParamValue = initNamedCatchAllParameter(macroCtx, catchAllParamName);
/*      */             }
/*  953 */             if (!withArgsState.orderLast) {
/*  954 */               namedCatchAllParamValue.put(argName, argValue); continue;
/*      */             } 
/*  956 */             List<NameValuePair> orderLastByNameCatchAll = withArgsState.orderLastByNameCatchAll;
/*  957 */             if (orderLastByNameCatchAll == null) {
/*  958 */               orderLastByNameCatchAll = new ArrayList<>();
/*  959 */               withArgsState.orderLastByNameCatchAll = orderLastByNameCatchAll;
/*      */             } 
/*  961 */             orderLastByNameCatchAll.add(new NameValuePair(argName, argValue));
/*      */             continue;
/*      */           } 
/*  964 */           throw newUndeclaredParamNameException(macro, argName);
/*      */         }
/*      */       
/*  967 */       } else if (byPositionWithArgs != null) {
/*  968 */         if (!withArgsState.orderLast) {
/*  969 */           String[] argNames = macro.getArgumentNamesNoCopy();
/*  970 */           int argsCnt = byPositionWithArgs.size();
/*  971 */           if (argNames.length < argsCnt && catchAllParamName == null) {
/*  972 */             throw newTooManyArgumentsException(macro, argNames, argsCnt);
/*      */           }
/*  974 */           for (int argIdx = 0; argIdx < argsCnt; argIdx++) {
/*  975 */             TemplateModel argValue = byPositionWithArgs.get(argIdx);
/*      */             try {
/*  977 */               if (nextPositionalArgToAssignIdx < argNames.length) {
/*  978 */                 String argName = argNames[nextPositionalArgToAssignIdx++];
/*  979 */                 macroCtx.setLocalVar(argName, argValue);
/*      */               } else {
/*  981 */                 if (positionalCatchAllParamValue == null) {
/*  982 */                   positionalCatchAllParamValue = initPositionalCatchAllParameter(macroCtx, catchAllParamName);
/*      */                 }
/*  984 */                 positionalCatchAllParamValue.add(argValue);
/*      */               } 
/*  986 */             } catch (RuntimeException re) {
/*  987 */               throw new _MiscTemplateException(re, this);
/*      */             } 
/*      */           } 
/*      */         } else {
/*  991 */           if (namedArgs != null && !namedArgs.isEmpty() && byPositionWithArgs.size() != 0)
/*      */           {
/*      */             
/*  994 */             throw new _MiscTemplateException("Call can't pass parameters by name, as there's \"with args last\" in effect that specifies parameters by position.");
/*      */           }
/*      */           
/*  997 */           if (catchAllParamName == null) {
/*      */ 
/*      */             
/* 1000 */             int totalPositionalArgCnt = ((positionalArgs != null) ? positionalArgs.size() : 0) + byPositionWithArgs.size();
/* 1001 */             if (totalPositionalArgCnt > (macro.getArgumentNamesNoCopy()).length) {
/* 1002 */               throw newTooManyArgumentsException(macro, macro.getArgumentNamesNoCopy(), totalPositionalArgCnt);
/*      */             }
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 1009 */     if (namedArgs != null) {
/* 1010 */       if (catchAllParamName != null && namedCatchAllParamValue == null && positionalCatchAllParamValue == null)
/*      */       {
/*      */         
/* 1013 */         if (namedArgs.isEmpty() && withArgsState != null && withArgsState.byPosition != null) {
/* 1014 */           positionalCatchAllParamValue = initPositionalCatchAllParameter(macroCtx, catchAllParamName);
/*      */         } else {
/* 1016 */           namedCatchAllParamValue = initNamedCatchAllParameter(macroCtx, catchAllParamName);
/*      */         } 
/*      */       }
/*      */       
/* 1020 */       for (Map.Entry<String, ? extends Expression> argNameAndValExp : namedArgs.entrySet()) {
/* 1021 */         String argName = argNameAndValExp.getKey();
/* 1022 */         boolean isArgNameDeclared = macro.hasArgNamed(argName);
/* 1023 */         if (isArgNameDeclared || namedCatchAllParamValue != null) {
/* 1024 */           Expression argValueExp = argNameAndValExp.getValue();
/* 1025 */           TemplateModel argValue = argValueExp.eval(this);
/* 1026 */           if (isArgNameDeclared) {
/* 1027 */             macroCtx.setLocalVar(argName, argValue); continue;
/*      */           } 
/* 1029 */           namedCatchAllParamValue.put(argName, argValue);
/*      */           continue;
/*      */         } 
/* 1032 */         if (positionalCatchAllParamValue != null) {
/* 1033 */           throw newBothNamedAndPositionalCatchAllParamsException(macro);
/*      */         }
/* 1035 */         throw newUndeclaredParamNameException(macro, argName);
/*      */       }
/*      */     
/*      */     }
/* 1039 */     else if (positionalArgs != null) {
/* 1040 */       if (catchAllParamName != null && positionalCatchAllParamValue == null && namedCatchAllParamValue == null) {
/* 1041 */         if (positionalArgs.isEmpty() && withArgsState != null && withArgsState.byName != null) {
/* 1042 */           namedCatchAllParamValue = initNamedCatchAllParameter(macroCtx, catchAllParamName);
/*      */         } else {
/* 1044 */           positionalCatchAllParamValue = initPositionalCatchAllParameter(macroCtx, catchAllParamName);
/*      */         } 
/*      */       }
/*      */       
/* 1048 */       String[] argNames = macro.getArgumentNamesNoCopy();
/* 1049 */       int argsCnt = positionalArgs.size();
/* 1050 */       int argsWithWithArgsCnt = argsCnt + nextPositionalArgToAssignIdx;
/* 1051 */       if (argNames.length < argsWithWithArgsCnt && positionalCatchAllParamValue == null) {
/* 1052 */         if (namedCatchAllParamValue != null) {
/* 1053 */           throw newBothNamedAndPositionalCatchAllParamsException(macro);
/*      */         }
/* 1055 */         throw newTooManyArgumentsException(macro, argNames, argsWithWithArgsCnt);
/*      */       } 
/*      */       
/* 1058 */       for (int srcPosArgIdx = 0; srcPosArgIdx < argsCnt; srcPosArgIdx++) {
/* 1059 */         TemplateModel argValue; Expression argValueExp = positionalArgs.get(srcPosArgIdx);
/*      */         
/*      */         try {
/* 1062 */           argValue = argValueExp.eval(this);
/* 1063 */         } catch (RuntimeException e) {
/* 1064 */           throw new _MiscTemplateException(e, this);
/*      */         } 
/* 1066 */         if (nextPositionalArgToAssignIdx < argNames.length) {
/* 1067 */           String argName = argNames[nextPositionalArgToAssignIdx++];
/* 1068 */           macroCtx.setLocalVar(argName, argValue);
/*      */         } else {
/* 1070 */           positionalCatchAllParamValue.add(argValue);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 1075 */     if (withArgsState != null && withArgsState.orderLast) {
/* 1076 */       if (withArgsState.orderLastByNameCatchAll != null) {
/* 1077 */         for (NameValuePair nameValuePair : withArgsState.orderLastByNameCatchAll) {
/* 1078 */           if (!namedCatchAllParamValue.containsKey(nameValuePair.name)) {
/* 1079 */             namedCatchAllParamValue.put(nameValuePair.name, nameValuePair.value);
/*      */           }
/*      */         } 
/* 1082 */       } else if (withArgsState.byPosition != null) {
/* 1083 */         TemplateSequenceModel byPosition = withArgsState.byPosition;
/* 1084 */         int withArgCnt = byPosition.size();
/* 1085 */         String[] argNames = macro.getArgumentNamesNoCopy();
/* 1086 */         for (int withArgIdx = 0; withArgIdx < withArgCnt; withArgIdx++) {
/* 1087 */           TemplateModel withArgValue = byPosition.get(withArgIdx);
/* 1088 */           if (nextPositionalArgToAssignIdx < argNames.length) {
/* 1089 */             String argName = argNames[nextPositionalArgToAssignIdx++];
/* 1090 */             macroCtx.setLocalVar(argName, withArgValue);
/*      */           } else {
/*      */             
/* 1093 */             positionalCatchAllParamValue.add(withArgValue);
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private static WithArgsState getWithArgState(Macro macro) {
/* 1101 */     Macro.WithArgs withArgs = macro.getWithArgs();
/* 1102 */     return (withArgs == null) ? null : new WithArgsState(withArgs.getByName(), withArgs.getByPosition(), withArgs
/* 1103 */         .isOrderLast());
/*      */   }
/*      */   
/*      */   private static final class WithArgsState {
/*      */     private final TemplateHashModelEx byName;
/*      */     private final TemplateSequenceModel byPosition;
/*      */     private final boolean orderLast;
/*      */     private List<Environment.NameValuePair> orderLastByNameCatchAll;
/*      */     
/*      */     public WithArgsState(TemplateHashModelEx byName, TemplateSequenceModel byPosition, boolean orderLast) {
/* 1113 */       this.byName = byName;
/* 1114 */       this.byPosition = byPosition;
/* 1115 */       this.orderLast = orderLast;
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class NameValuePair {
/*      */     private final String name;
/*      */     private final TemplateModel value;
/*      */     
/*      */     public NameValuePair(String name, TemplateModel value) {
/* 1124 */       this.name = name;
/* 1125 */       this.value = value;
/*      */     }
/*      */   }
/*      */   
/*      */   private _MiscTemplateException newTooManyArgumentsException(Macro macro, String[] argNames, int argsCnt) {
/* 1130 */     return new _MiscTemplateException(this, new Object[] {
/* 1131 */           macro.isFunction() ? "Function " : "Macro ", new _DelayedJQuote(macro.getName()), " only accepts ", new _DelayedToString(argNames.length), " parameters, but got ", new _DelayedToString(argsCnt), "."
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static SimpleSequence initPositionalCatchAllParameter(Macro.Context macroCtx, String catchAllParamName) {
/* 1138 */     SimpleSequence positionalCatchAllParamValue = new SimpleSequence((ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/* 1139 */     macroCtx.setLocalVar(catchAllParamName, (TemplateModel)positionalCatchAllParamValue);
/* 1140 */     return positionalCatchAllParamValue;
/*      */   }
/*      */ 
/*      */   
/*      */   private static SimpleHash initNamedCatchAllParameter(Macro.Context macroCtx, String catchAllParamName) {
/* 1145 */     SimpleHash namedCatchAllParamValue = new SimpleHash(new LinkedHashMap<>(), (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER, 0);
/*      */     
/* 1147 */     macroCtx.setLocalVar(catchAllParamName, (TemplateModel)namedCatchAllParamValue);
/* 1148 */     return namedCatchAllParamValue;
/*      */   }
/*      */   
/*      */   private _MiscTemplateException newUndeclaredParamNameException(Macro macro, String argName) {
/* 1152 */     return new _MiscTemplateException(this, new Object[] {
/* 1153 */           macro.isFunction() ? "Function " : "Macro ", new _DelayedJQuote(macro.getName()), " has no parameter with name ", new _DelayedJQuote(argName), ". Valid parameter names are: ", new _DelayedJoinWithComma(macro
/*      */             
/* 1155 */             .getArgumentNamesNoCopy()) });
/*      */   }
/*      */   
/*      */   private _MiscTemplateException newBothNamedAndPositionalCatchAllParamsException(Macro macro) {
/* 1159 */     return new _MiscTemplateException(this, new Object[] {
/* 1160 */           macro.isFunction() ? "Function " : "Macro ", new _DelayedJQuote(macro.getName()), " call can't have both named and positional arguments that has to go into catch-all parameter."
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void visitMacroDef(Macro macro) {
/* 1168 */     this.macroToNamespaceLookup.put(macro.getNamespaceLookupKey(), this.currentNamespace);
/* 1169 */     this.currentNamespace.put(macro.getName(), macro);
/*      */   }
/*      */   
/*      */   Namespace getMacroNamespace(Macro macro) {
/* 1173 */     return this.macroToNamespaceLookup.get(macro.getNamespaceLookupKey());
/*      */   }
/*      */ 
/*      */   
/*      */   void recurse(TemplateNodeModel node, TemplateSequenceModel namespaces) throws TemplateException, IOException {
/* 1178 */     if (node == null) {
/* 1179 */       node = getCurrentVisitorNode();
/* 1180 */       if (node == null) {
/* 1181 */         throw new _TemplateModelException("The target node of recursion is missing or null.");
/*      */       }
/*      */     } 
/*      */     
/* 1185 */     TemplateSequenceModel children = node.getChildNodes();
/* 1186 */     if (children == null) {
/*      */       return;
/*      */     }
/* 1189 */     int size = children.size();
/* 1190 */     for (int i = 0; i < size; i++) {
/* 1191 */       TemplateNodeModel child = (TemplateNodeModel)children.get(i);
/* 1192 */       if (child != null) {
/* 1193 */         invokeNodeHandlerFor(child, namespaces);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   Macro.Context getCurrentMacroContext() {
/* 1199 */     return this.currentMacroContext;
/*      */   }
/*      */ 
/*      */   
/*      */   private void handleTemplateException(TemplateException templateException) throws TemplateException {
/* 1204 */     if (templateException instanceof TemplateModelException && ((TemplateModelException)templateException)
/* 1205 */       .getReplaceWithCause() && templateException
/* 1206 */       .getCause() instanceof TemplateException) {
/* 1207 */       templateException = (TemplateException)templateException.getCause();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1212 */     if (this.lastThrowable == templateException) {
/* 1213 */       throw templateException;
/*      */     }
/* 1215 */     this.lastThrowable = (Throwable)templateException;
/*      */     
/* 1217 */     if (getLogTemplateExceptions() && LOG.isErrorEnabled() && 
/* 1218 */       !isInAttemptBlock()) {
/* 1219 */       LOG.error("Error executing FreeMarker template", (Throwable)templateException);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 1225 */       if (templateException instanceof StopException) {
/* 1226 */         throw templateException;
/*      */       }
/*      */ 
/*      */       
/* 1230 */       getTemplateExceptionHandler().handleTemplateException(templateException, this, this.out);
/* 1231 */     } catch (TemplateException e) {
/*      */       
/* 1233 */       if (isInAttemptBlock()) {
/* 1234 */         getAttemptExceptionReporter().report(templateException, this);
/*      */       }
/* 1236 */       throw e;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setTemplateExceptionHandler(TemplateExceptionHandler templateExceptionHandler) {
/* 1242 */     super.setTemplateExceptionHandler(templateExceptionHandler);
/* 1243 */     this.lastThrowable = null;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setLocale(Locale locale) {
/* 1248 */     Locale prevLocale = getLocale();
/* 1249 */     super.setLocale(locale);
/* 1250 */     if (!locale.equals(prevLocale)) {
/* 1251 */       this.cachedTemplateNumberFormats = null;
/* 1252 */       if (this.cachedTemplateNumberFormat != null && this.cachedTemplateNumberFormat.isLocaleBound()) {
/* 1253 */         this.cachedTemplateNumberFormat = null;
/*      */       }
/*      */       
/* 1256 */       if (this.cachedTempDateFormatArray != null) {
/* 1257 */         for (int i = 0; i < 16; i++) {
/* 1258 */           TemplateDateFormat f = this.cachedTempDateFormatArray[i];
/* 1259 */           if (f != null && f.isLocaleBound()) {
/* 1260 */             this.cachedTempDateFormatArray[i] = null;
/*      */           }
/*      */         } 
/*      */       }
/*      */       
/* 1265 */       this.cachedTempDateFormatsByFmtStrArray = null;
/*      */       
/* 1267 */       this.cachedCollator = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setTimeZone(TimeZone timeZone) {
/* 1273 */     TimeZone prevTimeZone = getTimeZone();
/* 1274 */     super.setTimeZone(timeZone);
/*      */     
/* 1276 */     if (!timeZone.equals(prevTimeZone)) {
/* 1277 */       if (this.cachedTempDateFormatArray != null) {
/* 1278 */         for (int i = 0; i < 8; i++) {
/* 1279 */           TemplateDateFormat f = this.cachedTempDateFormatArray[i];
/* 1280 */           if (f != null && f.isTimeZoneBound()) {
/* 1281 */             this.cachedTempDateFormatArray[i] = null;
/*      */           }
/*      */         } 
/*      */       }
/* 1285 */       if (this.cachedTempDateFormatsByFmtStrArray != null) {
/* 1286 */         for (int i = 0; i < 8; i++) {
/* 1287 */           this.cachedTempDateFormatsByFmtStrArray[i] = null;
/*      */         }
/*      */       }
/*      */       
/* 1291 */       this.cachedSQLDateAndTimeTimeZoneSameAsNormal = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setSQLDateAndTimeTimeZone(TimeZone timeZone) {
/* 1297 */     TimeZone prevTimeZone = getSQLDateAndTimeTimeZone();
/* 1298 */     super.setSQLDateAndTimeTimeZone(timeZone);
/*      */     
/* 1300 */     if (!nullSafeEquals(timeZone, prevTimeZone)) {
/* 1301 */       if (this.cachedTempDateFormatArray != null) {
/* 1302 */         for (int i = 8; i < 16; i++) {
/* 1303 */           TemplateDateFormat format = this.cachedTempDateFormatArray[i];
/* 1304 */           if (format != null && format.isTimeZoneBound()) {
/* 1305 */             this.cachedTempDateFormatArray[i] = null;
/*      */           }
/*      */         } 
/*      */       }
/* 1309 */       if (this.cachedTempDateFormatsByFmtStrArray != null) {
/* 1310 */         for (int i = 8; i < 16; i++) {
/* 1311 */           this.cachedTempDateFormatsByFmtStrArray[i] = null;
/*      */         }
/*      */       }
/*      */       
/* 1315 */       this.cachedSQLDateAndTimeTimeZoneSameAsNormal = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private static boolean nullSafeEquals(Object o1, Object o2) {
/* 1321 */     if (o1 == o2) return true; 
/* 1322 */     if (o1 == null || o2 == null) return false; 
/* 1323 */     return o1.equals(o2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean isSQLDateAndTimeTimeZoneSameAsNormal() {
/* 1331 */     if (this.cachedSQLDateAndTimeTimeZoneSameAsNormal == null) {
/* 1332 */       this.cachedSQLDateAndTimeTimeZoneSameAsNormal = Boolean.valueOf((
/* 1333 */           getSQLDateAndTimeTimeZone() == null || 
/* 1334 */           getSQLDateAndTimeTimeZone().equals(getTimeZone())));
/*      */     }
/* 1336 */     return this.cachedSQLDateAndTimeTimeZoneSameAsNormal.booleanValue();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setURLEscapingCharset(String urlEscapingCharset) {
/* 1341 */     this.cachedURLEscapingCharsetSet = false;
/* 1342 */     super.setURLEscapingCharset(urlEscapingCharset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setOutputEncoding(String outputEncoding) {
/* 1352 */     this.cachedURLEscapingCharsetSet = false;
/* 1353 */     super.setOutputEncoding(outputEncoding);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   String getEffectiveURLEscapingCharset() {
/* 1361 */     if (!this.cachedURLEscapingCharsetSet) {
/* 1362 */       this.cachedURLEscapingCharset = getURLEscapingCharset();
/* 1363 */       if (this.cachedURLEscapingCharset == null) {
/* 1364 */         this.cachedURLEscapingCharset = getOutputEncoding();
/*      */       }
/* 1366 */       this.cachedURLEscapingCharsetSet = true;
/*      */     } 
/* 1368 */     return this.cachedURLEscapingCharset;
/*      */   }
/*      */   
/*      */   Collator getCollator() {
/* 1372 */     if (this.cachedCollator == null) {
/* 1373 */       this.cachedCollator = Collator.getInstance(getLocale());
/*      */     }
/* 1375 */     return this.cachedCollator;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean applyEqualsOperator(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
/* 1385 */     return EvalUtil.compare(leftValue, 1, rightValue, this);
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
/*      */   public boolean applyEqualsOperatorLenient(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
/* 1397 */     return EvalUtil.compareLenient(leftValue, 1, rightValue, this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean applyLessThanOperator(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
/* 1407 */     return EvalUtil.compare(leftValue, 3, rightValue, this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean applyLessThanOrEqualsOperator(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
/* 1417 */     return EvalUtil.compare(leftValue, 5, rightValue, this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean applyGreaterThanOperator(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
/* 1427 */     return EvalUtil.compare(leftValue, 4, rightValue, this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean applyWithGreaterThanOrEqualsOperator(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
/* 1437 */     return EvalUtil.compare(leftValue, 6, rightValue, this);
/*      */   }
/*      */   
/*      */   public void setOut(Writer out) {
/* 1441 */     this.out = out;
/*      */   }
/*      */   
/*      */   public Writer getOut() {
/* 1445 */     return this.out;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setNumberFormat(String formatName) {
/* 1450 */     super.setNumberFormat(formatName);
/* 1451 */     this.cachedTemplateNumberFormat = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   String formatNumberToPlainText(TemplateNumberModel number, Expression exp, boolean useTempModelExc) throws TemplateException {
/* 1462 */     return formatNumberToPlainText(number, getTemplateNumberFormat(exp, useTempModelExc), exp, useTempModelExc);
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
/*      */   String formatNumberToPlainText(TemplateNumberModel number, TemplateNumberFormat format, Expression exp, boolean useTempModelExc) throws TemplateException {
/*      */     try {
/* 1476 */       return EvalUtil.assertFormatResultNotNull(format.formatToPlainText(number));
/* 1477 */     } catch (TemplateValueFormatException e) {
/* 1478 */       throw _MessageUtil.newCantFormatNumberException(format, exp, e, useTempModelExc);
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
/*      */   String formatNumberToPlainText(Number number, BackwardCompatibleTemplateNumberFormat format, Expression exp) throws TemplateModelException, _MiscTemplateException {
/*      */     try {
/* 1491 */       return format.format(number);
/* 1492 */     } catch (UnformattableValueException e) {
/* 1493 */       throw new _MiscTemplateException(exp, e, this, new Object[] { "Failed to format number with ", new _DelayedJQuote(format
/* 1494 */               .getDescription()), ": ", e
/* 1495 */             .getMessage() });
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
/*      */   public TemplateNumberFormat getTemplateNumberFormat() throws TemplateValueFormatException {
/* 1511 */     TemplateNumberFormat format = this.cachedTemplateNumberFormat;
/* 1512 */     if (format == null) {
/* 1513 */       format = getTemplateNumberFormat(getNumberFormat(), false);
/* 1514 */       this.cachedTemplateNumberFormat = format;
/*      */     } 
/* 1516 */     return format;
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
/*      */   public TemplateNumberFormat getTemplateNumberFormat(String formatString) throws TemplateValueFormatException {
/* 1532 */     return getTemplateNumberFormat(formatString, true);
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
/*      */   public TemplateNumberFormat getTemplateNumberFormat(String formatString, Locale locale) throws TemplateValueFormatException {
/* 1555 */     if (locale.equals(getLocale())) {
/* 1556 */       getTemplateNumberFormat(formatString);
/*      */     }
/*      */     
/* 1559 */     return getTemplateNumberFormatWithoutCache(formatString, locale);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   TemplateNumberFormat getTemplateNumberFormat(Expression exp, boolean useTempModelExc) throws TemplateException {
/*      */     TemplateNumberFormat format;
/*      */     try {
/* 1568 */       format = getTemplateNumberFormat();
/* 1569 */     } catch (TemplateValueFormatException e) {
/*      */ 
/*      */ 
/*      */       
/* 1573 */       _ErrorDescriptionBuilder desc = (new _ErrorDescriptionBuilder(new Object[] { "Failed to get number format object for the current number format string, ", new _DelayedJQuote(getNumberFormat()), ": ", e.getMessage() })).blame(exp);
/* 1574 */       throw useTempModelExc ? new _TemplateModelException(e, this, desc) : new _MiscTemplateException(e, this, desc);
/*      */     } 
/*      */     
/* 1577 */     return format;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   TemplateNumberFormat getTemplateNumberFormat(String formatString, Expression exp, boolean useTempModelExc) throws TemplateException {
/*      */     TemplateNumberFormat format;
/*      */     try {
/* 1590 */       format = getTemplateNumberFormat(formatString);
/* 1591 */     } catch (TemplateValueFormatException e) {
/*      */ 
/*      */ 
/*      */       
/* 1595 */       _ErrorDescriptionBuilder desc = (new _ErrorDescriptionBuilder(new Object[] { "Failed to get number format object for the ", new _DelayedJQuote(formatString), " number format string: ", e.getMessage() })).blame(exp);
/* 1596 */       throw useTempModelExc ? new _TemplateModelException(e, this, desc) : new _MiscTemplateException(e, this, desc);
/*      */     } 
/*      */     
/* 1599 */     return format;
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
/*      */   private TemplateNumberFormat getTemplateNumberFormat(String formatString, boolean cacheResult) throws TemplateValueFormatException {
/* 1613 */     if (this.cachedTemplateNumberFormats == null) {
/* 1614 */       if (cacheResult) {
/* 1615 */         this.cachedTemplateNumberFormats = new HashMap<>();
/*      */       }
/*      */     } else {
/* 1618 */       TemplateNumberFormat templateNumberFormat = this.cachedTemplateNumberFormats.get(formatString);
/* 1619 */       if (templateNumberFormat != null) {
/* 1620 */         return templateNumberFormat;
/*      */       }
/*      */     } 
/*      */     
/* 1624 */     TemplateNumberFormat format = getTemplateNumberFormatWithoutCache(formatString, getLocale());
/*      */     
/* 1626 */     if (cacheResult) {
/* 1627 */       this.cachedTemplateNumberFormats.put(formatString, format);
/*      */     }
/* 1629 */     return format;
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
/*      */   private TemplateNumberFormat getTemplateNumberFormatWithoutCache(String formatString, Locale locale) throws TemplateValueFormatException {
/* 1643 */     int formatStringLen = formatString.length();
/* 1644 */     if (formatStringLen > 1 && formatString
/* 1645 */       .charAt(0) == '@' && (
/* 1646 */       isIcI2324OrLater() || hasCustomFormats()) && 
/* 1647 */       Character.isLetter(formatString.charAt(1))) {
/*      */       int endIdx;
/*      */ 
/*      */ 
/*      */       
/* 1652 */       for (endIdx = 1; endIdx < formatStringLen; endIdx++) {
/* 1653 */         char c = formatString.charAt(endIdx);
/* 1654 */         if (c == ' ' || c == '_') {
/*      */           break;
/*      */         }
/*      */       } 
/* 1658 */       String name = formatString.substring(1, endIdx);
/* 1659 */       String params = (endIdx < formatStringLen) ? formatString.substring(endIdx + 1) : "";
/*      */ 
/*      */       
/* 1662 */       TemplateNumberFormatFactory formatFactory = getCustomNumberFormat(name);
/* 1663 */       if (formatFactory == null) {
/* 1664 */         throw new UndefinedCustomFormatException("No custom number format was defined with name " + 
/* 1665 */             StringUtil.jQuote(name));
/*      */       }
/*      */       
/* 1668 */       return formatFactory.get(params, locale, this);
/*      */     } 
/* 1670 */     return JavaTemplateNumberFormatFactory.INSTANCE.get(formatString, locale, this);
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
/*      */   public NumberFormat getCNumberFormat() {
/* 1682 */     if (this.cNumberFormat == null) {
/* 1683 */       if (this.configuration.getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_31) {
/* 1684 */         this.cNumberFormat = (DecimalFormat)C_NUMBER_FORMAT_ICI_2_3_21.clone();
/*      */       } else {
/* 1686 */         this.cNumberFormat = (DecimalFormat)C_NUMBER_FORMAT_ICI_2_3_20.clone();
/*      */       } 
/*      */     }
/* 1689 */     return this.cNumberFormat;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   String transformNumberFormatGlobalCacheKey(String keyPart) {
/* 1697 */     if (this.configuration.getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_31 && "computer"
/* 1698 */       .equals(keyPart)) {
/* 1699 */       return "computer\0002";
/*      */     }
/* 1701 */     return keyPart;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setTimeFormat(String timeFormat) {
/* 1706 */     String prevTimeFormat = getTimeFormat();
/* 1707 */     super.setTimeFormat(timeFormat);
/* 1708 */     if (!timeFormat.equals(prevTimeFormat) && 
/* 1709 */       this.cachedTempDateFormatArray != null) {
/* 1710 */       for (int i = 0; i < 16; i += 4) {
/* 1711 */         this.cachedTempDateFormatArray[i + 1] = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDateFormat(String dateFormat) {
/* 1719 */     String prevDateFormat = getDateFormat();
/* 1720 */     super.setDateFormat(dateFormat);
/* 1721 */     if (!dateFormat.equals(prevDateFormat) && 
/* 1722 */       this.cachedTempDateFormatArray != null) {
/* 1723 */       for (int i = 0; i < 16; i += 4) {
/* 1724 */         this.cachedTempDateFormatArray[i + 2] = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDateTimeFormat(String dateTimeFormat) {
/* 1732 */     String prevDateTimeFormat = getDateTimeFormat();
/* 1733 */     super.setDateTimeFormat(dateTimeFormat);
/* 1734 */     if (!dateTimeFormat.equals(prevDateTimeFormat) && 
/* 1735 */       this.cachedTempDateFormatArray != null) {
/* 1736 */       for (int i = 0; i < 16; i += 4) {
/* 1737 */         this.cachedTempDateFormatArray[i + 3] = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public Configuration getConfiguration() {
/* 1744 */     return this.configuration;
/*      */   }
/*      */   
/*      */   TemplateModel getLastReturnValue() {
/* 1748 */     return this.lastReturnValue;
/*      */   }
/*      */   
/*      */   void setLastReturnValue(TemplateModel lastReturnValue) {
/* 1752 */     this.lastReturnValue = lastReturnValue;
/*      */   }
/*      */   
/*      */   void clearLastReturnValue() {
/* 1756 */     this.lastReturnValue = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   String formatDateToPlainText(TemplateDateModel tdm, Expression tdmSourceExpr, boolean useTempModelExc) throws TemplateException {
/* 1765 */     TemplateDateFormat format = getTemplateDateFormat(tdm, tdmSourceExpr, useTempModelExc);
/*      */     
/*      */     try {
/* 1768 */       return EvalUtil.assertFormatResultNotNull(format.formatToPlainText(tdm));
/* 1769 */     } catch (TemplateValueFormatException e) {
/* 1770 */       throw _MessageUtil.newCantFormatDateException(format, tdmSourceExpr, e, useTempModelExc);
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
/*      */   String formatDateToPlainText(TemplateDateModel tdm, String formatString, Expression blamedDateSourceExp, Expression blamedFormatterExp, boolean useTempModelExc) throws TemplateException {
/* 1783 */     Date date = EvalUtil.modelToDate(tdm, blamedDateSourceExp);
/*      */     
/* 1785 */     TemplateDateFormat format = getTemplateDateFormat(formatString, tdm
/* 1786 */         .getDateType(), (Class)date.getClass(), blamedDateSourceExp, blamedFormatterExp, useTempModelExc);
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 1791 */       return EvalUtil.assertFormatResultNotNull(format.formatToPlainText(tdm));
/* 1792 */     } catch (TemplateValueFormatException e) {
/* 1793 */       throw _MessageUtil.newCantFormatDateException(format, blamedDateSourceExp, e, useTempModelExc);
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
/*      */   public TemplateDateFormat getTemplateDateFormat(int dateType, Class<? extends Date> dateClass) throws TemplateValueFormatException {
/* 1811 */     boolean isSQLDateOrTime = isSQLDateOrTimeClass(dateClass);
/* 1812 */     return getTemplateDateFormat(dateType, shouldUseSQLDTTimeZone(isSQLDateOrTime), isSQLDateOrTime);
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
/*      */   public TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, Class<? extends Date> dateClass) throws TemplateValueFormatException {
/* 1834 */     boolean isSQLDateOrTime = isSQLDateOrTimeClass(dateClass);
/* 1835 */     return getTemplateDateFormat(formatString, dateType, 
/*      */         
/* 1837 */         shouldUseSQLDTTimeZone(isSQLDateOrTime), isSQLDateOrTime, true);
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
/*      */   public TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, Class<? extends Date> dateClass, Locale locale) throws TemplateValueFormatException {
/* 1862 */     boolean isSQLDateOrTime = isSQLDateOrTimeClass(dateClass);
/* 1863 */     boolean useSQLDTTZ = shouldUseSQLDTTimeZone(isSQLDateOrTime);
/* 1864 */     return getTemplateDateFormat(formatString, dateType, locale, useSQLDTTZ ? 
/*      */         
/* 1866 */         getSQLDateAndTimeTimeZone() : getTimeZone(), isSQLDateOrTime);
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
/*      */   public TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, Class<? extends Date> dateClass, Locale locale, TimeZone timeZone, TimeZone sqlDateAndTimeTimeZone) throws TemplateValueFormatException {
/* 1894 */     boolean isSQLDateOrTime = isSQLDateOrTimeClass(dateClass);
/* 1895 */     boolean useSQLDTTZ = shouldUseSQLDTTimeZone(isSQLDateOrTime);
/* 1896 */     return getTemplateDateFormat(formatString, dateType, locale, useSQLDTTZ ? sqlDateAndTimeTimeZone : timeZone, isSQLDateOrTime);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput) throws TemplateValueFormatException {
/* 1939 */     Locale currentLocale = getLocale();
/* 1940 */     if (locale.equals(currentLocale)) {
/*      */       int equalCurrentTZ;
/* 1942 */       TimeZone currentTimeZone = getTimeZone();
/* 1943 */       if (timeZone.equals(currentTimeZone)) {
/* 1944 */         equalCurrentTZ = 1;
/*      */       } else {
/* 1946 */         TimeZone currentSQLDTTimeZone = getSQLDateAndTimeTimeZone();
/* 1947 */         if (timeZone.equals(currentSQLDTTimeZone)) {
/* 1948 */           equalCurrentTZ = 2;
/*      */         } else {
/* 1950 */           equalCurrentTZ = 0;
/*      */         } 
/*      */       } 
/* 1953 */       if (equalCurrentTZ != 0) {
/* 1954 */         return getTemplateDateFormat(formatString, dateType, (equalCurrentTZ == 2), zonelessInput, true);
/*      */       }
/*      */     } 
/*      */     
/* 1958 */     return getTemplateDateFormatWithoutCache(formatString, dateType, locale, timeZone, zonelessInput);
/*      */   }
/*      */ 
/*      */   
/*      */   TemplateDateFormat getTemplateDateFormat(TemplateDateModel tdm, Expression tdmSourceExpr, boolean useTempModelExc) throws TemplateModelException, TemplateException {
/* 1963 */     Date date = EvalUtil.modelToDate(tdm, tdmSourceExpr);
/*      */     
/* 1965 */     TemplateDateFormat format = getTemplateDateFormat(tdm
/* 1966 */         .getDateType(), (Class)date.getClass(), tdmSourceExpr, useTempModelExc);
/*      */     
/* 1968 */     return format;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   TemplateDateFormat getTemplateDateFormat(int dateType, Class<? extends Date> dateClass, Expression blamedDateSourceExp, boolean useTempModelExc) throws TemplateException {
/*      */     try {
/* 1978 */       return getTemplateDateFormat(dateType, dateClass);
/* 1979 */     } catch (UnknownDateTypeFormattingUnsupportedException e) {
/* 1980 */       throw _MessageUtil.newCantFormatUnknownTypeDateException(blamedDateSourceExp, e);
/* 1981 */     } catch (TemplateValueFormatException e) {
/*      */       String settingName, settingValue;
/*      */       
/* 1984 */       switch (dateType) {
/*      */         case 1:
/* 1986 */           settingName = "time_format";
/* 1987 */           settingValue = getTimeFormat();
/*      */           break;
/*      */         case 2:
/* 1990 */           settingName = "date_format";
/* 1991 */           settingValue = getDateFormat();
/*      */           break;
/*      */         case 3:
/* 1994 */           settingName = "datetime_format";
/* 1995 */           settingValue = getDateTimeFormat();
/*      */           break;
/*      */         default:
/* 1998 */           settingName = "???";
/* 1999 */           settingValue = "???";
/*      */           break;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2006 */       _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder(new Object[] { "The value of the \"", settingName, "\" FreeMarker configuration setting is a malformed date/time/datetime format string: ", new _DelayedJQuote(settingValue), ". Reason given: ", e.getMessage() });
/* 2007 */       throw useTempModelExc ? new _TemplateModelException(e, new Object[] { desc }) : new _MiscTemplateException(e, new Object[] { desc });
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
/*      */   TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, Class<? extends Date> dateClass, Expression blamedDateSourceExp, Expression blamedFormatterExp, boolean useTempModelExc) throws TemplateException {
/*      */     try {
/* 2021 */       return getTemplateDateFormat(formatString, dateType, dateClass);
/* 2022 */     } catch (UnknownDateTypeFormattingUnsupportedException e) {
/* 2023 */       throw _MessageUtil.newCantFormatUnknownTypeDateException(blamedDateSourceExp, e);
/* 2024 */     } catch (TemplateValueFormatException e) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2029 */       _ErrorDescriptionBuilder desc = (new _ErrorDescriptionBuilder(new Object[] { "Can't create date/time/datetime format based on format string ", new _DelayedJQuote(formatString), ". Reason given: ", e.getMessage() })).blame(blamedFormatterExp);
/* 2030 */       throw useTempModelExc ? new _TemplateModelException(e, new Object[] { desc }) : new _MiscTemplateException(e, new Object[] { desc });
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private TemplateDateFormat getTemplateDateFormat(int dateType, boolean useSQLDTTZ, boolean zonelessInput) throws TemplateValueFormatException {
/* 2041 */     if (dateType == 0) {
/* 2042 */       throw new UnknownDateTypeFormattingUnsupportedException();
/*      */     }
/* 2044 */     int cacheIdx = getTemplateDateFormatCacheArrayIndex(dateType, zonelessInput, useSQLDTTZ);
/* 2045 */     TemplateDateFormat[] cachedTemplateDateFormats = this.cachedTempDateFormatArray;
/* 2046 */     if (cachedTemplateDateFormats == null) {
/* 2047 */       cachedTemplateDateFormats = new TemplateDateFormat[16];
/* 2048 */       this.cachedTempDateFormatArray = cachedTemplateDateFormats;
/*      */     } 
/* 2050 */     TemplateDateFormat format = cachedTemplateDateFormats[cacheIdx];
/* 2051 */     if (format == null) {
/*      */       String formatString;
/* 2053 */       switch (dateType) {
/*      */         case 1:
/* 2055 */           formatString = getTimeFormat();
/*      */           break;
/*      */         case 2:
/* 2058 */           formatString = getDateFormat();
/*      */           break;
/*      */         case 3:
/* 2061 */           formatString = getDateTimeFormat();
/*      */           break;
/*      */         default:
/* 2064 */           throw new IllegalArgumentException("Invalid date type enum: " + Integer.valueOf(dateType));
/*      */       } 
/*      */       
/* 2067 */       format = getTemplateDateFormat(formatString, dateType, useSQLDTTZ, zonelessInput, false);
/*      */       
/* 2069 */       cachedTemplateDateFormats[cacheIdx] = format;
/*      */     } 
/* 2071 */     return format;
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
/*      */   private TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, boolean useSQLDTTimeZone, boolean zonelessInput, boolean cacheResult) throws TemplateValueFormatException {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield cachedTempDateFormatsByFmtStrArray : [Ljava/util/HashMap;
/*      */     //   4: astore #7
/*      */     //   6: aload #7
/*      */     //   8: ifnonnull -> 38
/*      */     //   11: iload #5
/*      */     //   13: ifeq -> 32
/*      */     //   16: bipush #16
/*      */     //   18: anewarray java/util/HashMap
/*      */     //   21: astore #7
/*      */     //   23: aload_0
/*      */     //   24: aload #7
/*      */     //   26: putfield cachedTempDateFormatsByFmtStrArray : [Ljava/util/HashMap;
/*      */     //   29: goto -> 38
/*      */     //   32: aconst_null
/*      */     //   33: astore #6
/*      */     //   35: goto -> 107
/*      */     //   38: aload_0
/*      */     //   39: iload_2
/*      */     //   40: iload #4
/*      */     //   42: iload_3
/*      */     //   43: invokespecial getTemplateDateFormatCacheArrayIndex : (IZZ)I
/*      */     //   46: istore #9
/*      */     //   48: aload #7
/*      */     //   50: iload #9
/*      */     //   52: aaload
/*      */     //   53: astore #6
/*      */     //   55: aload #6
/*      */     //   57: ifnonnull -> 88
/*      */     //   60: iload #5
/*      */     //   62: ifeq -> 107
/*      */     //   65: new java/util/HashMap
/*      */     //   68: dup
/*      */     //   69: iconst_4
/*      */     //   70: invokespecial <init> : (I)V
/*      */     //   73: astore #6
/*      */     //   75: aload #7
/*      */     //   77: iload #9
/*      */     //   79: aload #6
/*      */     //   81: aastore
/*      */     //   82: aconst_null
/*      */     //   83: astore #8
/*      */     //   85: goto -> 99
/*      */     //   88: aload #6
/*      */     //   90: aload_1
/*      */     //   91: invokevirtual get : (Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   94: checkcast freemarker/core/TemplateDateFormat
/*      */     //   97: astore #8
/*      */     //   99: aload #8
/*      */     //   101: ifnull -> 107
/*      */     //   104: aload #8
/*      */     //   106: areturn
/*      */     //   107: aload_0
/*      */     //   108: aload_1
/*      */     //   109: iload_2
/*      */     //   110: aload_0
/*      */     //   111: invokevirtual getLocale : ()Ljava/util/Locale;
/*      */     //   114: iload_3
/*      */     //   115: ifeq -> 125
/*      */     //   118: aload_0
/*      */     //   119: invokevirtual getSQLDateAndTimeTimeZone : ()Ljava/util/TimeZone;
/*      */     //   122: goto -> 129
/*      */     //   125: aload_0
/*      */     //   126: invokevirtual getTimeZone : ()Ljava/util/TimeZone;
/*      */     //   129: iload #4
/*      */     //   131: invokespecial getTemplateDateFormatWithoutCache : (Ljava/lang/String;ILjava/util/Locale;Ljava/util/TimeZone;Z)Lfreemarker/core/TemplateDateFormat;
/*      */     //   134: astore #7
/*      */     //   136: iload #5
/*      */     //   138: ifeq -> 150
/*      */     //   141: aload #6
/*      */     //   143: aload_1
/*      */     //   144: aload #7
/*      */     //   146: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   149: pop
/*      */     //   150: aload #7
/*      */     //   152: areturn
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #2089	-> 0
/*      */     //   #2090	-> 6
/*      */     //   #2091	-> 11
/*      */     //   #2092	-> 16
/*      */     //   #2093	-> 23
/*      */     //   #2095	-> 32
/*      */     //   #2096	-> 35
/*      */     //   #2102	-> 38
/*      */     //   #2103	-> 48
/*      */     //   #2104	-> 55
/*      */     //   #2105	-> 60
/*      */     //   #2106	-> 65
/*      */     //   #2107	-> 75
/*      */     //   #2108	-> 82
/*      */     //   #2113	-> 88
/*      */     //   #2117	-> 99
/*      */     //   #2118	-> 104
/*      */     //   #2123	-> 107
/*      */     //   #2125	-> 111
/*      */     //   #2123	-> 131
/*      */     //   #2127	-> 136
/*      */     //   #2129	-> 141
/*      */     //   #2131	-> 150
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   35	3	6	cachedFormatsByFormatString	Ljava/util/HashMap;
/*      */     //   85	3	8	format	Lfreemarker/core/TemplateDateFormat;
/*      */     //   48	51	9	cacheArrIdx	I
/*      */     //   6	101	7	cachedTempDateFormatsByFmtStrArray	[Ljava/util/HashMap;
/*      */     //   99	8	8	format	Lfreemarker/core/TemplateDateFormat;
/*      */     //   0	153	0	this	Lfreemarker/core/Environment;
/*      */     //   0	153	1	formatString	Ljava/lang/String;
/*      */     //   0	153	2	dateType	I
/*      */     //   0	153	3	useSQLDTTimeZone	Z
/*      */     //   0	153	4	zonelessInput	Z
/*      */     //   0	153	5	cacheResult	Z
/*      */     //   55	98	6	cachedFormatsByFormatString	Ljava/util/HashMap;
/*      */     //   136	17	7	format	Lfreemarker/core/TemplateDateFormat;
/*      */     // Local variable type table:
/*      */     //   start	length	slot	name	signature
/*      */     //   35	3	6	cachedFormatsByFormatString	Ljava/util/HashMap<Ljava/lang/String;Lfreemarker/core/TemplateDateFormat;>;
/*      */     //   6	101	7	cachedTempDateFormatsByFmtStrArray	[Ljava/util/HashMap<Ljava/lang/String;Lfreemarker/core/TemplateDateFormat;>;
/*      */     //   55	98	6	cachedFormatsByFormatString	Ljava/util/HashMap<Ljava/lang/String;Lfreemarker/core/TemplateDateFormat;>;
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
/*      */   private TemplateDateFormat getTemplateDateFormatWithoutCache(String formatString, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput) throws TemplateValueFormatException {
/*      */     String formatParams;
/*      */     TemplateDateFormatFactory formatFactory;
/* 2149 */     int formatStringLen = formatString.length();
/*      */ 
/*      */ 
/*      */     
/* 2153 */     char firstChar = (formatStringLen != 0) ? formatString.charAt(0) : Character.MIN_VALUE;
/*      */ 
/*      */     
/* 2156 */     if (firstChar == 'x' && formatStringLen > 1 && formatString
/*      */       
/* 2158 */       .charAt(1) == 's') {
/* 2159 */       formatFactory = XSTemplateDateFormatFactory.INSTANCE;
/* 2160 */       formatParams = formatString;
/* 2161 */     } else if (firstChar == 'i' && formatStringLen > 2 && formatString
/*      */       
/* 2163 */       .charAt(1) == 's' && formatString
/* 2164 */       .charAt(2) == 'o') {
/* 2165 */       formatFactory = ISOTemplateDateFormatFactory.INSTANCE;
/* 2166 */       formatParams = formatString;
/* 2167 */     } else if (firstChar == '@' && formatStringLen > 1 && (
/*      */       
/* 2169 */       isIcI2324OrLater() || hasCustomFormats()) && 
/* 2170 */       Character.isLetter(formatString.charAt(1))) {
/*      */       int endIdx;
/*      */ 
/*      */       
/* 2174 */       for (endIdx = 1; endIdx < formatStringLen; endIdx++) {
/* 2175 */         char c = formatString.charAt(endIdx);
/* 2176 */         if (c == ' ' || c == '_') {
/*      */           break;
/*      */         }
/*      */       } 
/* 2180 */       String name = formatString.substring(1, endIdx);
/* 2181 */       formatParams = (endIdx < formatStringLen) ? formatString.substring(endIdx + 1) : "";
/*      */ 
/*      */       
/* 2184 */       formatFactory = getCustomDateFormat(name);
/* 2185 */       if (formatFactory == null) {
/* 2186 */         throw new UndefinedCustomFormatException("No custom date format was defined with name " + 
/* 2187 */             StringUtil.jQuote(name));
/*      */       }
/*      */     } else {
/* 2190 */       formatParams = formatString;
/* 2191 */       formatFactory = JavaTemplateDateFormatFactory.INSTANCE;
/*      */     } 
/*      */     
/* 2194 */     return formatFactory.get(formatParams, dateType, locale, timeZone, zonelessInput, this);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   boolean shouldUseSQLDTTZ(Class<Date> dateClass) {
/* 2200 */     return (dateClass != Date.class && 
/* 2201 */       !isSQLDateAndTimeTimeZoneSameAsNormal() && 
/* 2202 */       isSQLDateOrTimeClass(dateClass));
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean shouldUseSQLDTTimeZone(boolean sqlDateOrTime) {
/* 2207 */     return (sqlDateOrTime && !isSQLDateAndTimeTimeZoneSameAsNormal());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean isSQLDateOrTimeClass(Class<Date> dateClass) {
/* 2215 */     return (dateClass != Date.class && (dateClass == Date.class || dateClass == Time.class || (dateClass != Timestamp.class && (Date.class
/*      */ 
/*      */       
/* 2218 */       .isAssignableFrom(dateClass) || Time.class
/* 2219 */       .isAssignableFrom(dateClass)))));
/*      */   }
/*      */   
/*      */   private int getTemplateDateFormatCacheArrayIndex(int dateType, boolean zonelessInput, boolean sqlDTTZ) {
/* 2223 */     return dateType + (zonelessInput ? 4 : 0) + (sqlDTTZ ? 8 : 0);
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
/*      */   DateUtil.DateToISO8601CalendarFactory getISOBuiltInCalendarFactory() {
/* 2236 */     if (this.isoBuiltInCalendarFactory == null) {
/* 2237 */       this.isoBuiltInCalendarFactory = (DateUtil.DateToISO8601CalendarFactory)new DateUtil.TrivialDateToISO8601CalendarFactory();
/*      */     }
/* 2239 */     return this.isoBuiltInCalendarFactory;
/*      */   }
/*      */   
/*      */   TemplateTransformModel getTransform(Expression exp) throws TemplateException {
/* 2243 */     TemplateTransformModel ttm = null;
/* 2244 */     TemplateModel tm = exp.eval(this);
/* 2245 */     if (tm instanceof TemplateTransformModel) {
/* 2246 */       ttm = (TemplateTransformModel)tm;
/* 2247 */     } else if (exp instanceof Identifier) {
/* 2248 */       tm = this.configuration.getSharedVariable(exp.toString());
/* 2249 */       if (tm instanceof TemplateTransformModel) {
/* 2250 */         ttm = (TemplateTransformModel)tm;
/*      */       }
/*      */     } 
/* 2253 */     return ttm;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateModel getLocalVariable(String name) throws TemplateModelException {
/* 2262 */     TemplateModel val = getNullableLocalVariable(name);
/* 2263 */     return (val != TemplateNullModel.INSTANCE) ? val : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final TemplateModel getNullableLocalVariable(String name) throws TemplateModelException {
/* 2273 */     if (this.localContextStack != null) {
/* 2274 */       for (int i = this.localContextStack.size() - 1; i >= 0; i--) {
/* 2275 */         LocalContext lc = this.localContextStack.get(i);
/* 2276 */         TemplateModel tm = lc.getLocalVariable(name);
/* 2277 */         if (tm != null) {
/* 2278 */           return tm;
/*      */         }
/*      */       } 
/*      */     }
/* 2282 */     return (this.currentMacroContext == null) ? null : this.currentMacroContext.getLocalVariable(name);
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
/*      */   public TemplateModel getVariable(String name) throws TemplateModelException {
/* 2303 */     TemplateModel result = getNullableLocalVariable(name);
/* 2304 */     if (result != null) {
/* 2305 */       return (result != TemplateNullModel.INSTANCE) ? result : null;
/*      */     }
/*      */     
/* 2308 */     result = this.currentNamespace.get(name);
/* 2309 */     if (result != null) {
/* 2310 */       return result;
/*      */     }
/*      */     
/* 2313 */     return getGlobalVariable(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateModel getGlobalVariable(String name) throws TemplateModelException {
/* 2323 */     TemplateModel result = this.globalNamespace.get(name);
/* 2324 */     if (result != null) {
/* 2325 */       return result;
/*      */     }
/*      */     
/* 2328 */     return getDataModelOrSharedVariable(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateModel getDataModelOrSharedVariable(String name) throws TemplateModelException {
/* 2338 */     TemplateModel dataModelVal = this.rootDataModel.get(name);
/* 2339 */     if (dataModelVal != null) {
/* 2340 */       return dataModelVal;
/*      */     }
/*      */     
/* 2343 */     return this.configuration.getSharedVariable(name);
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
/*      */   public void setGlobalVariable(String name, TemplateModel value) {
/* 2360 */     this.globalNamespace.put(name, value);
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
/*      */   public void setVariable(String name, TemplateModel value) {
/* 2374 */     this.currentNamespace.put(name, value);
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
/*      */   public void setLocalVariable(String name, TemplateModel value) {
/* 2393 */     if (this.currentMacroContext == null) {
/* 2394 */       throw new IllegalStateException("Not executing macro body");
/*      */     }
/* 2396 */     this.currentMacroContext.setLocalVar(name, value);
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
/*      */   public Set getKnownVariableNames() throws TemplateModelException {
/* 2410 */     Set<String> set = this.configuration.getSharedVariableNames();
/*      */ 
/*      */     
/* 2413 */     if (this.rootDataModel instanceof TemplateHashModelEx) {
/* 2414 */       TemplateModelIterator rootNames = ((TemplateHashModelEx)this.rootDataModel).keys().iterator();
/* 2415 */       while (rootNames.hasNext()) {
/* 2416 */         set.add(((TemplateScalarModel)rootNames.next()).getAsString());
/*      */       }
/*      */     } 
/*      */     
/*      */     TemplateModelIterator tmi;
/* 2421 */     for (tmi = this.globalNamespace.keys().iterator(); tmi.hasNext();) {
/* 2422 */       set.add(((TemplateScalarModel)tmi.next()).getAsString());
/*      */     }
/*      */ 
/*      */     
/* 2426 */     for (tmi = this.currentNamespace.keys().iterator(); tmi.hasNext();) {
/* 2427 */       set.add(((TemplateScalarModel)tmi.next()).getAsString());
/*      */     }
/*      */ 
/*      */     
/* 2431 */     if (this.currentMacroContext != null) {
/* 2432 */       set.addAll(this.currentMacroContext.getLocalVariableNames());
/*      */     }
/* 2434 */     if (this.localContextStack != null) {
/* 2435 */       for (int i = this.localContextStack.size() - 1; i >= 0; i--) {
/* 2436 */         LocalContext lc = this.localContextStack.get(i);
/* 2437 */         set.addAll(lc.getLocalVariableNames());
/*      */       } 
/*      */     }
/* 2440 */     return set;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void outputInstructionStack(PrintWriter pw) {
/* 2448 */     outputInstructionStack(getInstructionStackSnapshot(), false, pw);
/* 2449 */     pw.flush();
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
/*      */   static void outputInstructionStack(TemplateElement[] instructionStackSnapshot, boolean terseMode, Writer w) {
/* 2464 */     PrintWriter pw = (w instanceof PrintWriter) ? (PrintWriter)w : null;
/*      */     try {
/* 2466 */       if (instructionStackSnapshot != null) {
/* 2467 */         int totalFrames = instructionStackSnapshot.length;
/* 2468 */         int framesToPrint = terseMode ? ((totalFrames <= 10) ? totalFrames : 9) : totalFrames;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2473 */         boolean hideNestringRelatedFrames = (terseMode && framesToPrint < totalFrames);
/* 2474 */         int nestingRelatedFramesHidden = 0;
/* 2475 */         int trailingFramesHidden = 0;
/* 2476 */         int framesPrinted = 0;
/* 2477 */         for (int frameIdx = 0; frameIdx < totalFrames; frameIdx++) {
/* 2478 */           TemplateElement stackEl = instructionStackSnapshot[frameIdx];
/* 2479 */           boolean nestingRelatedElement = ((frameIdx > 0 && stackEl instanceof BodyInstruction) || (frameIdx > 1 && instructionStackSnapshot[frameIdx - 1] instanceof BodyInstruction));
/*      */           
/* 2481 */           if (framesPrinted < framesToPrint) {
/* 2482 */             if (!nestingRelatedElement || !hideNestringRelatedFrames) {
/* 2483 */               w.write((frameIdx == 0) ? "\t- Failed at: " : (nestingRelatedElement ? "\t~ Reached through: " : "\t- Reached through: "));
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 2488 */               w.write(instructionStackItemToString(stackEl));
/* 2489 */               if (pw != null) { pw.println(); }
/*      */               else
/* 2491 */               { w.write(10); }
/* 2492 */                framesPrinted++;
/*      */             } else {
/* 2494 */               nestingRelatedFramesHidden++;
/*      */             } 
/*      */           } else {
/* 2497 */             trailingFramesHidden++;
/*      */           } 
/*      */         } 
/*      */         
/* 2501 */         boolean hadClosingNotes = false;
/* 2502 */         if (trailingFramesHidden > 0) {
/* 2503 */           w.write("\t... (Had ");
/* 2504 */           w.write(String.valueOf(trailingFramesHidden + nestingRelatedFramesHidden));
/* 2505 */           w.write(" more, hidden for tersenes)");
/* 2506 */           hadClosingNotes = true;
/*      */         } 
/* 2508 */         if (nestingRelatedFramesHidden > 0) {
/* 2509 */           if (hadClosingNotes) {
/* 2510 */             w.write(32);
/*      */           } else {
/* 2512 */             w.write(9);
/*      */           } 
/* 2514 */           w.write("(Hidden " + nestingRelatedFramesHidden + " \"~\" lines for terseness)");
/* 2515 */           if (pw != null) { pw.println(); }
/*      */           else
/* 2517 */           { w.write(10); }
/* 2518 */            hadClosingNotes = true;
/*      */         } 
/* 2520 */         if (hadClosingNotes)
/* 2521 */           if (pw != null) { pw.println(); }
/*      */           else
/* 2523 */           { w.write(10); }
/*      */            
/*      */       } else {
/* 2526 */         w.write("(The stack was empty)");
/* 2527 */         if (pw != null) { pw.println(); }
/*      */         else
/* 2529 */         { w.write(10); } 
/*      */       } 
/* 2531 */     } catch (IOException e) {
/* 2532 */       LOG.error("Failed to print FTL stack trace", e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   TemplateElement[] getInstructionStackSnapshot() {
/* 2542 */     int requiredLength = 0;
/* 2543 */     int ln = this.instructionStackSize;
/*      */     
/* 2545 */     for (int i = 0; i < ln; i++) {
/* 2546 */       TemplateElement stackEl = this.instructionStack[i];
/* 2547 */       if (i == ln - 1 || stackEl.isShownInStackTrace()) {
/* 2548 */         requiredLength++;
/*      */       }
/*      */     } 
/*      */     
/* 2552 */     if (requiredLength == 0) return null;
/*      */     
/* 2554 */     TemplateElement[] result = new TemplateElement[requiredLength];
/* 2555 */     int dstIdx = requiredLength - 1;
/* 2556 */     for (int j = 0; j < ln; j++) {
/* 2557 */       TemplateElement stackEl = this.instructionStack[j];
/* 2558 */       if (j == ln - 1 || stackEl.isShownInStackTrace()) {
/* 2559 */         result[dstIdx--] = stackEl;
/*      */       }
/*      */     } 
/*      */     
/* 2563 */     return result;
/*      */   }
/*      */   
/*      */   static String instructionStackItemToString(TemplateElement stackEl) {
/* 2567 */     StringBuilder sb = new StringBuilder();
/* 2568 */     appendInstructionStackItem(stackEl, sb);
/* 2569 */     return sb.toString();
/*      */   }
/*      */   
/*      */   static void appendInstructionStackItem(TemplateElement stackEl, StringBuilder sb) {
/* 2573 */     sb.append(_MessageUtil.shorten(stackEl.getDescription(), 40));
/*      */     
/* 2575 */     sb.append("  [");
/* 2576 */     Macro enclosingMacro = getEnclosingMacro(stackEl);
/* 2577 */     if (enclosingMacro != null) {
/* 2578 */       sb.append(_MessageUtil.formatLocationForEvaluationError(enclosingMacro, stackEl.beginLine, stackEl.beginColumn));
/*      */     } else {
/*      */       
/* 2581 */       sb.append(_MessageUtil.formatLocationForEvaluationError(stackEl
/* 2582 */             .getTemplate(), stackEl.beginLine, stackEl.beginColumn));
/*      */     } 
/* 2584 */     sb.append("]");
/*      */   }
/*      */   
/*      */   private static Macro getEnclosingMacro(TemplateElement stackEl) {
/* 2588 */     while (stackEl != null) {
/* 2589 */       if (stackEl instanceof Macro) return (Macro)stackEl; 
/* 2590 */       stackEl = stackEl.getParentElement();
/*      */     } 
/* 2592 */     return null;
/*      */   }
/*      */   
/*      */   private void pushLocalContext(LocalContext localContext) {
/* 2596 */     if (this.localContextStack == null) {
/* 2597 */       this.localContextStack = new LocalContextStack();
/*      */     }
/* 2599 */     this.localContextStack.push(localContext);
/*      */   }
/*      */   
/*      */   LocalContextStack getLocalContextStack() {
/* 2603 */     return this.localContextStack;
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
/*      */   public Namespace getNamespace(String name) {
/* 2615 */     if (name.startsWith("/")) name = name.substring(1); 
/* 2616 */     if (this.loadedLibs != null) {
/* 2617 */       return this.loadedLibs.get(name);
/*      */     }
/* 2619 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Namespace getMainNamespace() {
/* 2627 */     return this.mainNamespace;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Namespace getCurrentNamespace() {
/* 2638 */     return this.currentNamespace;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Namespace getGlobalNamespace() {
/* 2646 */     return this.globalNamespace;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateHashModel getDataModel() {
/* 2654 */     return (this.rootDataModel instanceof TemplateHashModelEx) ? (TemplateHashModel)new TemplateHashModelEx()
/*      */       {
/*      */         public boolean isEmpty() throws TemplateModelException
/*      */         {
/* 2658 */           return false;
/*      */         }
/*      */ 
/*      */         
/*      */         public TemplateModel get(String key) throws TemplateModelException {
/* 2663 */           return Environment.this.getDataModelOrSharedVariable(key);
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         public TemplateCollectionModel values() throws TemplateModelException {
/* 2671 */           return ((TemplateHashModelEx)Environment.this.rootDataModel).values();
/*      */         }
/*      */ 
/*      */         
/*      */         public TemplateCollectionModel keys() throws TemplateModelException {
/* 2676 */           return ((TemplateHashModelEx)Environment.this.rootDataModel).keys();
/*      */         }
/*      */ 
/*      */         
/*      */         public int size() throws TemplateModelException {
/* 2681 */           return ((TemplateHashModelEx)Environment.this.rootDataModel).size();
/*      */         }
/*      */       } : new TemplateHashModel()
/*      */       {
/*      */         public boolean isEmpty()
/*      */         {
/* 2687 */           return false;
/*      */         }
/*      */ 
/*      */         
/*      */         public TemplateModel get(String key) throws TemplateModelException {
/* 2692 */           TemplateModel value = Environment.this.rootDataModel.get(key);
/* 2693 */           return (value != null) ? value : Environment.this.configuration.getSharedVariable(key);
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateHashModel getGlobalVariables() {
/* 2704 */     return new TemplateHashModel()
/*      */       {
/*      */         public boolean isEmpty()
/*      */         {
/* 2708 */           return false;
/*      */         }
/*      */ 
/*      */         
/*      */         public TemplateModel get(String key) throws TemplateModelException {
/* 2713 */           TemplateModel result = Environment.this.globalNamespace.get(key);
/* 2714 */           if (result == null) {
/* 2715 */             result = Environment.this.rootDataModel.get(key);
/*      */           }
/* 2717 */           if (result == null) {
/* 2718 */             result = Environment.this.configuration.getSharedVariable(key);
/*      */           }
/* 2720 */           return result;
/*      */         }
/*      */       };
/*      */   }
/*      */   
/*      */   private void pushElement(TemplateElement element) {
/* 2726 */     int newSize = ++this.instructionStackSize;
/* 2727 */     TemplateElement[] instructionStack = this.instructionStack;
/* 2728 */     if (newSize > instructionStack.length) {
/* 2729 */       TemplateElement[] newInstructionStack = new TemplateElement[newSize * 2];
/* 2730 */       for (int i = 0; i < instructionStack.length; i++) {
/* 2731 */         newInstructionStack[i] = instructionStack[i];
/*      */       }
/* 2733 */       instructionStack = newInstructionStack;
/* 2734 */       this.instructionStack = instructionStack;
/*      */     } 
/* 2736 */     instructionStack[newSize - 1] = element;
/*      */   }
/*      */   
/*      */   private void popElement() {
/* 2740 */     this.instructionStackSize--;
/*      */   }
/*      */   
/*      */   void replaceElementStackTop(TemplateElement instr) {
/* 2744 */     this.instructionStack[this.instructionStackSize - 1] = instr;
/*      */   }
/*      */   
/*      */   public TemplateNodeModel getCurrentVisitorNode() {
/* 2748 */     return this.currentVisitorNode;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCurrentVisitorNode(TemplateNodeModel node) {
/* 2755 */     this.currentVisitorNode = node;
/*      */   }
/*      */   
/*      */   TemplateModel getNodeProcessor(TemplateNodeModel node) throws TemplateException {
/* 2759 */     String nodeName = node.getNodeName();
/* 2760 */     if (nodeName == null) {
/* 2761 */       throw new _MiscTemplateException(this, "Node name is null.");
/*      */     }
/* 2763 */     TemplateModel result = getNodeProcessor(nodeName, node.getNodeNamespace(), 0);
/*      */     
/* 2765 */     if (result == null) {
/* 2766 */       String type = node.getNodeType();
/*      */ 
/*      */       
/* 2769 */       if (type == null) {
/* 2770 */         type = "default";
/*      */       }
/* 2772 */       result = getNodeProcessor("@" + type, (String)null, 0);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2780 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   private TemplateModel getNodeProcessor(String nodeName, String nsURI, int startIndex) throws TemplateException {
/* 2785 */     TemplateModel result = null;
/*      */     
/* 2787 */     int size = this.nodeNamespaces.size(); int i;
/* 2788 */     for (i = startIndex; i < size; i++) {
/* 2789 */       Namespace ns = null;
/*      */       try {
/* 2791 */         ns = (Namespace)this.nodeNamespaces.get(i);
/* 2792 */       } catch (ClassCastException cce) {
/* 2793 */         throw new _MiscTemplateException(this, "A \"using\" clause should contain a sequence of namespaces or strings that indicate the location of importable macro libraries.");
/*      */       } 
/*      */ 
/*      */       
/* 2797 */       result = getNodeProcessor(ns, nodeName, nsURI);
/* 2798 */       if (result != null)
/*      */         break; 
/*      */     } 
/* 2801 */     if (result != null) {
/* 2802 */       this.nodeNamespaceIndex = i + 1;
/* 2803 */       this.currentNodeName = nodeName;
/* 2804 */       this.currentNodeNS = nsURI;
/*      */     } 
/* 2806 */     return result;
/*      */   }
/*      */   
/*      */   private TemplateModel getNodeProcessor(Namespace ns, String localName, String nsURI) throws TemplateException {
/* 2810 */     TemplateModel result = null;
/* 2811 */     if (nsURI == null) {
/* 2812 */       result = ns.get(localName);
/* 2813 */       if (!(result instanceof Macro) && !(result instanceof TemplateTransformModel)) {
/* 2814 */         result = null;
/*      */       }
/*      */     } else {
/* 2817 */       Template template = ns.getTemplate();
/* 2818 */       String prefix = template.getPrefixForNamespace(nsURI);
/* 2819 */       if (prefix == null)
/*      */       {
/*      */         
/* 2822 */         return null;
/*      */       }
/* 2824 */       if (prefix.length() > 0) {
/* 2825 */         result = ns.get(prefix + ":" + localName);
/* 2826 */         if (!(result instanceof Macro) && !(result instanceof TemplateTransformModel)) {
/* 2827 */           result = null;
/*      */         }
/*      */       } else {
/* 2830 */         if (nsURI.length() == 0) {
/* 2831 */           result = ns.get("N:" + localName);
/* 2832 */           if (!(result instanceof Macro) && !(result instanceof TemplateTransformModel)) {
/* 2833 */             result = null;
/*      */           }
/*      */         } 
/* 2836 */         if (nsURI.equals(template.getDefaultNS())) {
/* 2837 */           result = ns.get("D:" + localName);
/* 2838 */           if (!(result instanceof Macro) && !(result instanceof TemplateTransformModel)) {
/* 2839 */             result = null;
/*      */           }
/*      */         } 
/* 2842 */         if (result == null) {
/* 2843 */           result = ns.get(localName);
/* 2844 */           if (!(result instanceof Macro) && !(result instanceof TemplateTransformModel)) {
/* 2845 */             result = null;
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/* 2850 */     return result;
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
/*      */   public void include(String name, String encoding, boolean parse) throws IOException, TemplateException {
/* 2866 */     include(getTemplateForInclusion(name, encoding, parse));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Template getTemplateForInclusion(String name, String encoding, boolean parse) throws IOException {
/* 2875 */     return getTemplateForInclusion(name, encoding, parse, false);
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
/*      */   public Template getTemplateForInclusion(String name, String encoding, boolean parseAsFTL, boolean ignoreMissing) throws IOException {
/* 2913 */     return this.configuration.getTemplate(name, 
/* 2914 */         getLocale(), getIncludedTemplateCustomLookupCondition(), (encoding != null) ? encoding : 
/* 2915 */         getIncludedTemplateEncoding(), parseAsFTL, ignoreMissing);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private Object getIncludedTemplateCustomLookupCondition() {
/* 2921 */     return getTemplate().getCustomLookupCondition();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private String getIncludedTemplateEncoding() {
/* 2927 */     String encoding = getTemplate().getEncoding();
/* 2928 */     if (encoding == null) {
/* 2929 */       encoding = this.configuration.getEncoding(getLocale());
/*      */     }
/* 2931 */     return encoding;
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
/*      */   public void include(Template includedTemplate) throws TemplateException, IOException {
/* 2945 */     boolean parentReplacementOn = isBeforeIcI2322();
/* 2946 */     Template prevTemplate = getTemplate();
/* 2947 */     if (parentReplacementOn) {
/* 2948 */       setParent((Configurable)includedTemplate);
/*      */     } else {
/* 2950 */       this.legacyParent = (Configurable)includedTemplate;
/*      */     } 
/*      */     
/* 2953 */     importMacros(includedTemplate);
/*      */     try {
/* 2955 */       visit(includedTemplate.getRootTreeNode());
/*      */     } finally {
/* 2957 */       if (parentReplacementOn) {
/* 2958 */         setParent((Configurable)prevTemplate);
/*      */       } else {
/* 2960 */         this.legacyParent = (Configurable)prevTemplate;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Namespace importLib(String templateName, String targetNsVarName) throws IOException, TemplateException {
/* 2986 */     return importLib(templateName, targetNsVarName, getLazyImports());
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
/*      */   public Namespace importLib(Template loadedTemplate, String targetNsVarName) throws IOException, TemplateException {
/* 3006 */     return importLib((String)null, loadedTemplate, targetNsVarName);
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
/*      */   public Namespace importLib(String templateName, String targetNsVarName, boolean lazy) throws IOException, TemplateException {
/* 3020 */     return lazy ? 
/* 3021 */       importLib(templateName, (Template)null, targetNsVarName) : 
/* 3022 */       importLib((String)null, getTemplateForImporting(templateName), targetNsVarName);
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
/*      */   public Template getTemplateForImporting(String name) throws IOException {
/* 3037 */     return getTemplateForInclusion(name, (String)null, true);
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
/*      */   private Namespace importLib(String templateName, Template loadedTemplate, String targetNsVarName) throws IOException, TemplateException {
/*      */     boolean lazyImport;
/* 3051 */     if (loadedTemplate != null) {
/* 3052 */       lazyImport = false;
/*      */ 
/*      */ 
/*      */       
/* 3056 */       templateName = loadedTemplate.getName();
/*      */     } else {
/* 3058 */       lazyImport = true;
/*      */ 
/*      */ 
/*      */       
/* 3062 */       TemplateNameFormat tnf = getConfiguration().getTemplateNameFormat();
/* 3063 */       templateName = _CacheAPI.normalizeRootBasedName(tnf, templateName);
/*      */     } 
/*      */     
/* 3066 */     if (this.loadedLibs == null) {
/* 3067 */       this.loadedLibs = new HashMap<>();
/*      */     }
/* 3069 */     Namespace existingNamespace = this.loadedLibs.get(templateName);
/* 3070 */     if (existingNamespace != null) {
/* 3071 */       if (targetNsVarName != null) {
/* 3072 */         setVariable(targetNsVarName, (TemplateModel)existingNamespace);
/* 3073 */         if (isIcI2324OrLater() && this.currentNamespace == this.mainNamespace) {
/* 3074 */           this.globalNamespace.put(targetNsVarName, existingNamespace);
/*      */         }
/*      */       } 
/* 3077 */       if (!lazyImport && existingNamespace instanceof LazilyInitializedNamespace) {
/* 3078 */         ((LazilyInitializedNamespace)existingNamespace).ensureInitializedTME();
/*      */       }
/*      */     } else {
/* 3081 */       Namespace newNamespace = lazyImport ? new LazilyInitializedNamespace(templateName) : new Namespace(loadedTemplate);
/*      */       
/* 3083 */       this.loadedLibs.put(templateName, newNamespace);
/*      */       
/* 3085 */       if (targetNsVarName != null) {
/* 3086 */         setVariable(targetNsVarName, (TemplateModel)newNamespace);
/* 3087 */         if (this.currentNamespace == this.mainNamespace) {
/* 3088 */           this.globalNamespace.put(targetNsVarName, newNamespace);
/*      */         }
/*      */       } 
/*      */       
/* 3092 */       if (!lazyImport) {
/* 3093 */         initializeImportLibNamespace(newNamespace, loadedTemplate);
/*      */       }
/*      */     } 
/* 3096 */     return this.loadedLibs.get(templateName);
/*      */   }
/*      */ 
/*      */   
/*      */   private void initializeImportLibNamespace(Namespace newNamespace, Template loadedTemplate) throws TemplateException, IOException {
/* 3101 */     Namespace prevNamespace = this.currentNamespace;
/* 3102 */     this.currentNamespace = newNamespace;
/* 3103 */     Writer prevOut = this.out;
/* 3104 */     this.out = (Writer)NullWriter.INSTANCE;
/*      */     try {
/* 3106 */       include(loadedTemplate);
/*      */     } finally {
/* 3108 */       this.out = prevOut;
/* 3109 */       this.currentNamespace = prevNamespace;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toFullTemplateName(String baseName, String targetName) throws MalformedTemplateNameException {
/* 3144 */     if (isClassicCompatible() || baseName == null) {
/* 3145 */       return targetName;
/*      */     }
/*      */     
/* 3148 */     return _CacheAPI.toRootBasedName(this.configuration.getTemplateNameFormat(), baseName, targetName);
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
/*      */   public String rootBasedToAbsoluteTemplateName(String rootBasedName) throws MalformedTemplateNameException {
/* 3167 */     return _CacheAPI.rootBasedNameToAbsoluteName(this.configuration.getTemplateNameFormat(), rootBasedName);
/*      */   }
/*      */   
/*      */   String renderElementToString(TemplateElement te) throws IOException, TemplateException {
/* 3171 */     Writer prevOut = this.out;
/*      */     try {
/* 3173 */       StringWriter sw = new StringWriter();
/* 3174 */       this.out = sw;
/* 3175 */       visit(te);
/* 3176 */       return sw.toString();
/*      */     } finally {
/* 3178 */       this.out = prevOut;
/*      */     } 
/*      */   }
/*      */   
/*      */   void importMacros(Template template) {
/* 3183 */     for (Iterator<Macro> it = template.getMacros().values().iterator(); it.hasNext();) {
/* 3184 */       visitMacroDef(it.next());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getNamespaceForPrefix(String prefix) {
/* 3193 */     return this.currentNamespace.getTemplate().getNamespaceForPrefix(prefix);
/*      */   }
/*      */   
/*      */   public String getPrefixForNamespace(String nsURI) {
/* 3197 */     return this.currentNamespace.getTemplate().getPrefixForNamespace(nsURI);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDefaultNS() {
/* 3204 */     return this.currentNamespace.getTemplate().getDefaultNS();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object __getitem__(String key) throws TemplateModelException {
/* 3211 */     return BeansWrapper.getDefaultInstance().unwrap(getVariable(key));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void __setitem__(String key, Object o) throws TemplateException {
/* 3218 */     setGlobalVariable(key, getObjectWrapper().wrap(o));
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
/*      */   public Object getCustomState(Object identityKey) {
/* 3230 */     if (this.customStateVariables == null) {
/* 3231 */       return null;
/*      */     }
/* 3233 */     return this.customStateVariables.get(identityKey);
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
/*      */   public Object setCustomState(Object identityKey, Object value) {
/* 3253 */     IdentityHashMap<Object, Object> customStateVariables = this.customStateVariables;
/* 3254 */     if (customStateVariables == null) {
/* 3255 */       customStateVariables = new IdentityHashMap<>();
/* 3256 */       this.customStateVariables = customStateVariables;
/*      */     } 
/* 3258 */     return customStateVariables.put(identityKey, value);
/*      */   }
/*      */   
/*      */   final class NestedElementTemplateDirectiveBody
/*      */     implements TemplateDirectiveBody {
/*      */     private final TemplateElement[] childBuffer;
/*      */     
/*      */     private NestedElementTemplateDirectiveBody(TemplateElement[] childBuffer) {
/* 3266 */       this.childBuffer = childBuffer;
/*      */     }
/*      */ 
/*      */     
/*      */     public void render(Writer newOut) throws TemplateException, IOException {
/* 3271 */       Writer prevOut = Environment.this.out;
/* 3272 */       Environment.this.out = newOut;
/*      */       try {
/* 3274 */         Environment.this.visit(this.childBuffer);
/*      */       } finally {
/* 3276 */         Environment.this.out = prevOut;
/*      */       } 
/*      */     }
/*      */     
/*      */     TemplateElement[] getChildrenBuffer() {
/* 3281 */       return this.childBuffer;
/*      */     }
/*      */   }
/*      */   
/*      */   public class Namespace
/*      */     extends SimpleHash
/*      */   {
/*      */     private Template template;
/*      */     
/*      */     Namespace() {
/* 3291 */       super((ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/* 3292 */       this.template = Environment.this.getTemplate();
/*      */     }
/*      */     
/*      */     Namespace(Template template) {
/* 3296 */       super((ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/* 3297 */       this.template = template;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Template getTemplate() {
/* 3304 */       return (this.template == null) ? Environment.this.getTemplate() : this.template;
/*      */     }
/*      */     
/*      */     void setTemplate(Template template) {
/* 3308 */       this.template = template;
/*      */     }
/*      */   }
/*      */   
/*      */   private enum InitializationStatus
/*      */   {
/* 3314 */     UNINITIALIZED, INITIALIZING, INITIALIZED, FAILED;
/*      */   }
/*      */   
/*      */   class LazilyInitializedNamespace
/*      */     extends Namespace
/*      */   {
/*      */     private final String templateName;
/*      */     private final Locale locale;
/*      */     private final String encoding;
/*      */     private final Object customLookupCondition;
/* 3324 */     private Environment.InitializationStatus status = Environment.InitializationStatus.UNINITIALIZED;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private LazilyInitializedNamespace(String templateName) {
/* 3331 */       super((Template)null);
/*      */       
/* 3333 */       this.templateName = templateName;
/*      */       
/* 3335 */       this.locale = Environment.this.getLocale();
/* 3336 */       this.encoding = Environment.this.getIncludedTemplateEncoding();
/* 3337 */       this.customLookupCondition = Environment.this.getIncludedTemplateCustomLookupCondition();
/*      */     }
/*      */     
/*      */     private void ensureInitializedTME() throws TemplateModelException {
/* 3341 */       if (this.status != Environment.InitializationStatus.INITIALIZED && this.status != Environment.InitializationStatus.INITIALIZING) {
/* 3342 */         if (this.status == Environment.InitializationStatus.FAILED) {
/* 3343 */           throw new TemplateModelException("Lazy initialization of the imported namespace for " + 
/*      */               
/* 3345 */               StringUtil.jQuote(this.templateName) + " has already failed earlier; won't retry it.");
/*      */         }
/*      */         
/*      */         try {
/* 3349 */           this.status = Environment.InitializationStatus.INITIALIZING;
/* 3350 */           initialize();
/* 3351 */           this.status = Environment.InitializationStatus.INITIALIZED;
/* 3352 */         } catch (Exception e) {
/*      */           
/* 3354 */           throw new TemplateModelException("Lazy initialization of the imported namespace for " + 
/*      */               
/* 3356 */               StringUtil.jQuote(this.templateName) + " has failed; see cause exception", e);
/*      */         } finally {
/*      */           
/* 3359 */           if (this.status != Environment.InitializationStatus.INITIALIZED) {
/* 3360 */             this.status = Environment.InitializationStatus.FAILED;
/*      */           }
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/*      */     private void ensureInitializedRTE() {
/*      */       try {
/* 3368 */         ensureInitializedTME();
/* 3369 */       } catch (TemplateModelException e) {
/* 3370 */         throw new RuntimeException(e.getMessage(), e.getCause());
/*      */       } 
/*      */     }
/*      */     
/*      */     private void initialize() throws IOException, TemplateException {
/* 3375 */       setTemplate(Environment.this.configuration.getTemplate(this.templateName, this.locale, this.customLookupCondition, this.encoding, true, false));
/*      */ 
/*      */       
/* 3378 */       Locale lastLocale = Environment.this.getLocale();
/*      */       try {
/* 3380 */         Environment.this.setLocale(this.locale);
/* 3381 */         Environment.this.initializeImportLibNamespace(this, getTemplate());
/*      */       } finally {
/* 3383 */         Environment.this.setLocale(lastLocale);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     protected Map copyMap(Map map) {
/* 3389 */       ensureInitializedRTE();
/* 3390 */       return super.copyMap(map);
/*      */     }
/*      */ 
/*      */     
/*      */     public Template getTemplate() {
/* 3395 */       ensureInitializedRTE();
/* 3396 */       return super.getTemplate();
/*      */     }
/*      */ 
/*      */     
/*      */     public void put(String key, Object value) {
/* 3401 */       ensureInitializedRTE();
/* 3402 */       super.put(key, value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void put(String key, boolean b) {
/* 3407 */       ensureInitializedRTE();
/* 3408 */       super.put(key, b);
/*      */     }
/*      */ 
/*      */     
/*      */     public TemplateModel get(String key) throws TemplateModelException {
/* 3413 */       ensureInitializedTME();
/* 3414 */       return super.get(key);
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean containsKey(String key) {
/* 3419 */       ensureInitializedRTE();
/* 3420 */       return super.containsKey(key);
/*      */     }
/*      */ 
/*      */     
/*      */     public void remove(String key) {
/* 3425 */       ensureInitializedRTE();
/* 3426 */       super.remove(key);
/*      */     }
/*      */ 
/*      */     
/*      */     public void putAll(Map m) {
/* 3431 */       ensureInitializedRTE();
/* 3432 */       super.putAll(m);
/*      */     }
/*      */ 
/*      */     
/*      */     public Map toMap() throws TemplateModelException {
/* 3437 */       ensureInitializedTME();
/* 3438 */       return super.toMap();
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/* 3443 */       ensureInitializedRTE();
/* 3444 */       return super.toString();
/*      */     }
/*      */ 
/*      */     
/*      */     public int size() {
/* 3449 */       ensureInitializedRTE();
/* 3450 */       return super.size();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isEmpty() {
/* 3455 */       ensureInitializedRTE();
/* 3456 */       return super.isEmpty();
/*      */     }
/*      */ 
/*      */     
/*      */     public TemplateCollectionModel keys() {
/* 3461 */       ensureInitializedRTE();
/* 3462 */       return super.keys();
/*      */     }
/*      */ 
/*      */     
/*      */     public TemplateCollectionModel values() {
/* 3467 */       ensureInitializedRTE();
/* 3468 */       return super.values();
/*      */     }
/*      */ 
/*      */     
/*      */     public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() {
/* 3473 */       ensureInitializedRTE();
/* 3474 */       return super.keyValuePairIterator();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/* 3480 */   private static final Writer EMPTY_BODY_WRITER = new Writer()
/*      */     {
/*      */       public void write(char[] cbuf, int off, int len) throws IOException
/*      */       {
/* 3484 */         if (len > 0) {
/* 3485 */           throw new IOException("This transform does not allow nested content.");
/*      */         }
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public void flush() {}
/*      */ 
/*      */ 
/*      */       
/*      */       public void close() {}
/*      */     };
/*      */ 
/*      */   
/*      */   private boolean isBeforeIcI2322() {
/* 3500 */     return (this.configuration.getIncompatibleImprovements().intValue() < _TemplateAPI.VERSION_INT_2_3_22);
/*      */   }
/*      */   
/*      */   boolean isIcI2324OrLater() {
/* 3504 */     return (this.configuration.getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_24);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean getFastInvalidReferenceExceptions() {
/* 3511 */     return this.fastInvalidReferenceExceptions;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean setFastInvalidReferenceExceptions(boolean b) {
/* 3520 */     boolean res = this.fastInvalidReferenceExceptions;
/* 3521 */     this.fastInvalidReferenceExceptions = b;
/* 3522 */     return res;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\Environment.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */