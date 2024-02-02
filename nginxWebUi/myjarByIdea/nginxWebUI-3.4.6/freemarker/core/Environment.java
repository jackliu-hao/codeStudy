package freemarker.core;

import freemarker.cache.TemplateNameFormat;
import freemarker.cache._CacheAPI;
import freemarker.ext.beans.BeansWrapper;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleSequence;
import freemarker.template.Template;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template.TransformControl;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.DateUtil;
import freemarker.template.utility.NullWriter;
import freemarker.template.utility.StringUtil;
import freemarker.template.utility.TemplateModelUtils;
import freemarker.template.utility.UndeclaredThrowableException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public final class Environment extends Configurable {
   private static final ThreadLocal threadEnv = new ThreadLocal();
   private static final Logger LOG = Logger.getLogger("freemarker.runtime");
   private static final Logger ATTEMPT_LOGGER = Logger.getLogger("freemarker.runtime.attempt");
   private static final DecimalFormat C_NUMBER_FORMAT_ICI_2_3_20;
   private static final DecimalFormat C_NUMBER_FORMAT_ICI_2_3_21;
   private final Configuration configuration;
   private final boolean incompatibleImprovementsGE2328;
   private final TemplateHashModel rootDataModel;
   private TemplateElement[] instructionStack = new TemplateElement[16];
   private int instructionStackSize = 0;
   private final ArrayList recoveredErrorStack = new ArrayList();
   private TemplateNumberFormat cachedTemplateNumberFormat;
   private Map<String, TemplateNumberFormat> cachedTemplateNumberFormats;
   private TemplateDateFormat[] cachedTempDateFormatArray;
   private HashMap<String, TemplateDateFormat>[] cachedTempDateFormatsByFmtStrArray;
   private static final int CACHED_TDFS_ZONELESS_INPUT_OFFS = 4;
   private static final int CACHED_TDFS_SQL_D_T_TZ_OFFS = 8;
   private static final int CACHED_TDFS_LENGTH = 16;
   private Boolean cachedSQLDateAndTimeTimeZoneSameAsNormal;
   /** @deprecated */
   @Deprecated
   private NumberFormat cNumberFormat;
   private DateUtil.DateToISO8601CalendarFactory isoBuiltInCalendarFactory;
   private Collator cachedCollator;
   private Writer out;
   private Macro.Context currentMacroContext;
   private LocalContextStack localContextStack;
   private final Namespace mainNamespace;
   private Namespace currentNamespace;
   private Namespace globalNamespace;
   private HashMap<String, Namespace> loadedLibs;
   private Configurable legacyParent;
   private boolean inAttemptBlock;
   private Throwable lastThrowable;
   private TemplateModel lastReturnValue;
   private Map<Object, Namespace> macroToNamespaceLookup = new IdentityHashMap();
   private TemplateNodeModel currentVisitorNode;
   private TemplateSequenceModel nodeNamespaces;
   private int nodeNamespaceIndex;
   private String currentNodeName;
   private String currentNodeNS;
   private String cachedURLEscapingCharset;
   private boolean cachedURLEscapingCharsetSet;
   private boolean fastInvalidReferenceExceptions;
   private static final TemplateModel[] NO_OUT_ARGS;
   private static final int TERSE_MODE_INSTRUCTION_STACK_TRACE_LIMIT = 10;
   private IdentityHashMap<Object, Object> customStateVariables;
   private static final Writer EMPTY_BODY_WRITER;

   public static Environment getCurrentEnvironment() {
      return (Environment)threadEnv.get();
   }

   static void setCurrentEnvironment(Environment env) {
      threadEnv.set(env);
   }

   public Environment(Template template, TemplateHashModel rootDataModel, Writer out) {
      super((Configurable)template);
      this.configuration = template.getConfiguration();
      this.incompatibleImprovementsGE2328 = this.configuration.getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_28;
      this.globalNamespace = new Namespace((Template)null);
      this.currentNamespace = this.mainNamespace = new Namespace(template);
      this.out = out;
      this.rootDataModel = rootDataModel;
      this.importMacros(template);
   }

   /** @deprecated */
   @Deprecated
   public Template getTemplate() {
      return (Template)this.getParent();
   }

   Template getTemplate230() {
      Template legacyParent = (Template)this.legacyParent;
      return legacyParent != null ? legacyParent : this.getTemplate();
   }

   public Template getMainTemplate() {
      return this.mainNamespace.getTemplate();
   }

   public Template getCurrentTemplate() {
      int ln = this.instructionStackSize;
      return ln == 0 ? this.getMainTemplate() : this.instructionStack[ln - 1].getTemplate();
   }

   public DirectiveCallPlace getCurrentDirectiveCallPlace() {
      int ln = this.instructionStackSize;
      if (ln == 0) {
         return null;
      } else {
         TemplateElement te = this.instructionStack[ln - 1];
         if (te instanceof UnifiedCall) {
            return (UnifiedCall)te;
         } else {
            return te instanceof Macro && ln > 1 && this.instructionStack[ln - 2] instanceof UnifiedCall ? (UnifiedCall)this.instructionStack[ln - 2] : null;
         }
      }
   }

   private void clearCachedValues() {
      this.cachedTemplateNumberFormats = null;
      this.cachedTemplateNumberFormat = null;
      this.cachedTempDateFormatArray = null;
      this.cachedTempDateFormatsByFmtStrArray = null;
      this.cachedCollator = null;
      this.cachedURLEscapingCharset = null;
      this.cachedURLEscapingCharsetSet = false;
   }

   public void process() throws TemplateException, IOException {
      Object savedEnv = threadEnv.get();
      threadEnv.set(this);

      try {
         this.clearCachedValues();

         try {
            this.doAutoImportsAndIncludes(this);
            this.visit(this.getTemplate().getRootTreeNode());
            if (this.getAutoFlush()) {
               this.out.flush();
            }
         } finally {
            this.clearCachedValues();
         }
      } finally {
         threadEnv.set(savedEnv);
      }

   }

   void visit(TemplateElement element) throws IOException, TemplateException {
      this.pushElement(element);

      try {
         TemplateElement[] templateElementsToVisit = element.accept(this);
         if (templateElementsToVisit != null) {
            TemplateElement[] var3 = templateElementsToVisit;
            int var4 = templateElementsToVisit.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               TemplateElement el = var3[var5];
               if (el == null) {
                  break;
               }

               this.visit(el);
            }
         }
      } catch (TemplateException var10) {
         this.handleTemplateException(var10);
      } finally {
         this.popElement();
      }

   }

   final void visit(TemplateElement[] elementBuffer) throws IOException, TemplateException {
      if (elementBuffer != null) {
         TemplateElement[] var2 = elementBuffer;
         int var3 = elementBuffer.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            TemplateElement element = var2[var4];
            if (element == null) {
               break;
            }

            this.pushElement(element);

            try {
               TemplateElement[] templateElementsToVisit = element.accept(this);
               if (templateElementsToVisit != null) {
                  TemplateElement[] var7 = templateElementsToVisit;
                  int var8 = templateElementsToVisit.length;

                  for(int var9 = 0; var9 < var8; ++var9) {
                     TemplateElement el = var7[var9];
                     if (el == null) {
                        break;
                     }

                     this.visit(el);
                  }
               }
            } catch (TemplateException var14) {
               this.handleTemplateException(var14);
            } finally {
               this.popElement();
            }
         }

      }
   }

   final void visit(TemplateElement[] elementBuffer, Writer out) throws IOException, TemplateException {
      Writer prevOut = this.out;
      this.out = out;

      try {
         this.visit(elementBuffer);
      } finally {
         this.out = prevOut;
      }

   }

   private TemplateElement replaceTopElement(TemplateElement element) {
      return this.instructionStack[this.instructionStackSize - 1] = element;
   }

   /** @deprecated */
   @Deprecated
   public void visit(TemplateElement element, TemplateDirectiveModel directiveModel, Map args, List bodyParameterNames) throws TemplateException, IOException {
      this.visit(new TemplateElement[]{element}, directiveModel, args, bodyParameterNames);
   }

   void visit(TemplateElement[] childBuffer, TemplateDirectiveModel directiveModel, Map args, final List bodyParameterNames) throws TemplateException, IOException {
      NestedElementTemplateDirectiveBody nested;
      if (childBuffer == null) {
         nested = null;
      } else {
         nested = new NestedElementTemplateDirectiveBody(childBuffer);
      }

      final TemplateModel[] outArgs;
      if (bodyParameterNames != null && !bodyParameterNames.isEmpty()) {
         outArgs = new TemplateModel[bodyParameterNames.size()];
      } else {
         outArgs = NO_OUT_ARGS;
      }

      if (outArgs.length > 0) {
         this.pushLocalContext(new LocalContext() {
            public TemplateModel getLocalVariable(String name) {
               int index = bodyParameterNames.indexOf(name);
               return index != -1 ? outArgs[index] : null;
            }

            public Collection getLocalVariableNames() {
               return bodyParameterNames;
            }
         });
      }

      try {
         directiveModel.execute(this, args, outArgs, nested);
      } catch (FlowControlException var14) {
         throw var14;
      } catch (TemplateException var15) {
         throw var15;
      } catch (IOException var16) {
         throw var16;
      } catch (Exception var17) {
         if (EvalUtil.shouldWrapUncheckedException(var17, this)) {
            throw new _MiscTemplateException(var17, this, "Directive has thrown an unchecked exception; see the cause exception.");
         }

         if (var17 instanceof RuntimeException) {
            throw (RuntimeException)var17;
         }

         throw new UndeclaredThrowableException(var17);
      } finally {
         if (outArgs.length > 0) {
            this.localContextStack.pop();
         }

      }

   }

   void visitAndTransform(TemplateElement[] elementBuffer, TemplateTransformModel transform, Map args) throws TemplateException, IOException {
      try {
         Writer tw = transform.getWriter(this.out, args);
         if (tw == null) {
            tw = EMPTY_BODY_WRITER;
         }

         TransformControl tc = tw instanceof TransformControl ? (TransformControl)tw : null;
         Writer prevOut = this.out;
         this.out = tw;

         try {
            if (tc == null || tc.onStart() != 0) {
               do {
                  this.visit(elementBuffer);
               } while(tc != null && tc.afterBody() == 0);
            }
         } catch (Throwable var17) {
            Throwable t = var17;

            try {
               if (tc == null || t instanceof FlowControlException && this.getConfiguration().getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_27) {
                  throw t;
               }

               tc.onError(t);
            } catch (IOException | Error | TemplateException var15) {
               throw var15;
            } catch (Throwable var16) {
               if (EvalUtil.shouldWrapUncheckedException(var16, this)) {
                  throw new _MiscTemplateException(var16, this, "Transform has thrown an unchecked exception; see the cause exception.");
               }

               if (var16 instanceof RuntimeException) {
                  throw (RuntimeException)var16;
               }

               throw new UndeclaredThrowableException(var16);
            }
         } finally {
            this.out = prevOut;
            if (prevOut != tw) {
               tw.close();
            }

         }
      } catch (TemplateException var19) {
         this.handleTemplateException(var19);
      }

   }

   void visitAttemptRecover(AttemptBlock attemptBlock, TemplateElement attemptedSection, RecoveryBlock recoverySection) throws TemplateException, IOException {
      Writer prevOut = this.out;
      StringWriter sw = new StringWriter();
      this.out = sw;
      TemplateException thrownException = null;
      boolean lastFIRE = this.setFastInvalidReferenceExceptions(false);
      boolean lastInAttemptBlock = this.inAttemptBlock;

      try {
         this.inAttemptBlock = true;
         this.visit(attemptedSection);
      } catch (TemplateException var19) {
         thrownException = var19;
      } finally {
         this.inAttemptBlock = lastInAttemptBlock;
         this.setFastInvalidReferenceExceptions(lastFIRE);
         this.out = prevOut;
      }

      if (thrownException != null) {
         if (ATTEMPT_LOGGER.isDebugEnabled()) {
            ATTEMPT_LOGGER.debug("Error in attempt block " + attemptBlock.getStartLocationQuoted(), thrownException);
         }

         try {
            this.recoveredErrorStack.add(thrownException);
            this.visit((TemplateElement)recoverySection);
         } finally {
            this.recoveredErrorStack.remove(this.recoveredErrorStack.size() - 1);
         }
      } else {
         this.out.write(sw.toString());
      }

   }

   String getCurrentRecoveredErrorMessage() throws TemplateException {
      if (this.recoveredErrorStack.isEmpty()) {
         throw new _MiscTemplateException(this, ".error is not available outside of a #recover block");
      } else {
         return ((Throwable)this.recoveredErrorStack.get(this.recoveredErrorStack.size() - 1)).getMessage();
      }
   }

   public boolean isInAttemptBlock() {
      return this.inAttemptBlock;
   }

   void invokeNestedContent(BodyInstruction.Context bodyCtx) throws TemplateException, IOException {
      Macro.Context invokingMacroContext = this.getCurrentMacroContext();
      LocalContextStack prevLocalContextStack = this.localContextStack;
      TemplateObject callPlace = invokingMacroContext.callPlace;
      TemplateElement[] nestedContentBuffer = callPlace instanceof TemplateElement ? ((TemplateElement)callPlace).getChildBuffer() : null;
      if (nestedContentBuffer != null) {
         this.currentMacroContext = invokingMacroContext.prevMacroContext;
         this.currentNamespace = invokingMacroContext.nestedContentNamespace;
         boolean parentReplacementOn = this.isBeforeIcI2322();
         Configurable prevParent = this.getParent();
         if (parentReplacementOn) {
            this.setParent(this.currentNamespace.getTemplate());
         } else {
            this.legacyParent = this.currentNamespace.getTemplate();
         }

         this.localContextStack = invokingMacroContext.prevLocalContextStack;
         if (invokingMacroContext.nestedContentParameterNames != null) {
            this.pushLocalContext(bodyCtx);
         }

         try {
            this.visit(nestedContentBuffer);
         } finally {
            if (invokingMacroContext.nestedContentParameterNames != null) {
               this.localContextStack.pop();
            }

            this.currentMacroContext = invokingMacroContext;
            this.currentNamespace = this.getMacroNamespace(invokingMacroContext.getMacro());
            if (parentReplacementOn) {
               this.setParent(prevParent);
            } else {
               this.legacyParent = prevParent;
            }

            this.localContextStack = prevLocalContextStack;
         }
      }

   }

   boolean visitIteratorBlock(IteratorBlock.IterationContext ictxt) throws TemplateException, IOException {
      this.pushLocalContext(ictxt);

      boolean var3;
      try {
         boolean var2 = ictxt.accept(this);
         return var2;
      } catch (TemplateException var7) {
         this.handleTemplateException(var7);
         var3 = true;
      } finally {
         this.localContextStack.pop();
      }

      return var3;
   }

   IteratorBlock.IterationContext findEnclosingIterationContextWithVisibleVariable(String loopVarName) {
      return this.findEnclosingIterationContext(loopVarName);
   }

   IteratorBlock.IterationContext findClosestEnclosingIterationContext() {
      return this.findEnclosingIterationContext((String)null);
   }

   private IteratorBlock.IterationContext findEnclosingIterationContext(String visibleLoopVarName) {
      LocalContextStack ctxStack = this.getLocalContextStack();
      if (ctxStack != null) {
         for(int i = ctxStack.size() - 1; i >= 0; --i) {
            Object ctx = ctxStack.get(i);
            if (ctx instanceof IteratorBlock.IterationContext && (visibleLoopVarName == null || ((IteratorBlock.IterationContext)ctx).hasVisibleLoopVar(visibleLoopVarName))) {
               return (IteratorBlock.IterationContext)ctx;
            }
         }
      }

      return null;
   }

   TemplateModel evaluateWithNewLocal(Expression exp, String lambdaArgName, TemplateModel lamdaArgValue) throws TemplateException {
      this.pushLocalContext(new LocalContextWithNewLocal(lambdaArgName, lamdaArgValue));

      TemplateModel var4;
      try {
         var4 = exp.eval(this);
      } finally {
         this.localContextStack.pop();
      }

      return var4;
   }

   void invokeNodeHandlerFor(TemplateNodeModel node, TemplateSequenceModel namespaces) throws TemplateException, IOException {
      if (this.nodeNamespaces == null) {
         SimpleSequence ss = new SimpleSequence(1, _TemplateAPI.SAFE_OBJECT_WRAPPER);
         ss.add(this.currentNamespace);
         this.nodeNamespaces = ss;
      }

      int prevNodeNamespaceIndex = this.nodeNamespaceIndex;
      String prevNodeName = this.currentNodeName;
      String prevNodeNS = this.currentNodeNS;
      TemplateSequenceModel prevNodeNamespaces = this.nodeNamespaces;
      TemplateNodeModel prevVisitorNode = this.currentVisitorNode;
      this.currentVisitorNode = node;
      if (namespaces != null) {
         this.nodeNamespaces = namespaces;
      }

      try {
         TemplateModel macroOrTransform = this.getNodeProcessor(node);
         if (macroOrTransform instanceof Macro) {
            this.invokeMacro((Macro)macroOrTransform, (Map)null, (List)null, (List)null, (TemplateObject)null);
         } else if (macroOrTransform instanceof TemplateTransformModel) {
            this.visitAndTransform((TemplateElement[])null, (TemplateTransformModel)macroOrTransform, (Map)null);
         } else {
            String nodeType = node.getNodeType();
            if (nodeType == null) {
               throw new _MiscTemplateException(this, this.noNodeHandlerDefinedDescription(node, node.getNodeNamespace(), "default"));
            }

            if (nodeType.equals("text") && node instanceof TemplateScalarModel) {
               this.out.write(((TemplateScalarModel)node).getAsString());
            } else if (nodeType.equals("document")) {
               this.recurse(node, namespaces);
            } else if (!nodeType.equals("pi") && !nodeType.equals("comment") && !nodeType.equals("document_type")) {
               throw new _MiscTemplateException(this, this.noNodeHandlerDefinedDescription(node, node.getNodeNamespace(), nodeType));
            }
         }
      } finally {
         this.currentVisitorNode = prevVisitorNode;
         this.nodeNamespaceIndex = prevNodeNamespaceIndex;
         this.currentNodeName = prevNodeName;
         this.currentNodeNS = prevNodeNS;
         this.nodeNamespaces = prevNodeNamespaces;
      }

   }

   private Object[] noNodeHandlerDefinedDescription(TemplateNodeModel node, String ns, String nodeType) throws TemplateModelException {
      String nsPrefix;
      if (ns != null) {
         if (ns.length() > 0) {
            nsPrefix = " and namespace ";
         } else {
            nsPrefix = " and no namespace";
         }
      } else {
         nsPrefix = "";
         ns = "";
      }

      return new Object[]{"No macro or directive is defined for node named ", new _DelayedJQuote(node.getNodeName()), nsPrefix, ns, ", and there is no fallback handler called @", nodeType, " either."};
   }

   void fallback() throws TemplateException, IOException {
      TemplateModel macroOrTransform = this.getNodeProcessor(this.currentNodeName, this.currentNodeNS, this.nodeNamespaceIndex);
      if (macroOrTransform instanceof Macro) {
         this.invokeMacro((Macro)macroOrTransform, (Map)null, (List)null, (List)null, (TemplateObject)null);
      } else if (macroOrTransform instanceof TemplateTransformModel) {
         this.visitAndTransform((TemplateElement[])null, (TemplateTransformModel)macroOrTransform, (Map)null);
      }

   }

   void invokeMacro(Macro macro, Map<String, ? extends Expression> namedArgs, List<? extends Expression> positionalArgs, List<String> bodyParameterNames, TemplateObject callPlace) throws TemplateException, IOException {
      this.invokeMacroOrFunctionCommonPart(macro, namedArgs, positionalArgs, bodyParameterNames, callPlace);
   }

   TemplateModel invokeFunction(Environment env, Macro func, List<? extends Expression> argumentExps, TemplateObject callPlace) throws TemplateException {
      env.setLastReturnValue((TemplateModel)null);
      if (!func.isFunction()) {
         throw new _MiscTemplateException(env, "A macro cannot be called in an expression. (Functions can be.)");
      } else {
         Writer prevOut = env.getOut();

         try {
            env.setOut(NullWriter.INSTANCE);
            env.invokeMacro(func, (Map)null, argumentExps, (List)null, callPlace);
         } catch (IOException var10) {
            throw new TemplateException("Unexpected exception during function execution", var10, env);
         } finally {
            env.setOut(prevOut);
         }

         return env.getLastReturnValue();
      }
   }

   private void invokeMacroOrFunctionCommonPart(Macro macroOrFunction, Map<String, ? extends Expression> namedArgs, List<? extends Expression> positionalArgs, List<String> bodyParameterNames, TemplateObject callPlace) throws TemplateException, IOException {
      if (macroOrFunction != Macro.DO_NOTHING_MACRO) {
         boolean elementPushed;
         if (!this.incompatibleImprovementsGE2328) {
            this.pushElement(macroOrFunction);
            elementPushed = true;
         } else {
            elementPushed = false;
         }

         try {
            Macro.Context macroCtx = macroOrFunction.new Context(this, callPlace, bodyParameterNames);
            this.setMacroContextLocalsFromArguments(macroCtx, macroOrFunction, namedArgs, positionalArgs);
            if (!elementPushed) {
               this.pushElement(macroOrFunction);
               elementPushed = true;
            }

            Macro.Context prevMacroCtx = this.currentMacroContext;
            this.currentMacroContext = macroCtx;
            LocalContextStack prevLocalContextStack = this.localContextStack;
            this.localContextStack = null;
            Namespace prevNamespace = this.currentNamespace;
            this.currentNamespace = this.getMacroNamespace(macroOrFunction);

            try {
               macroCtx.checkParamsSetAndApplyDefaults(this);
               this.visit(macroOrFunction.getChildBuffer());
            } catch (ReturnInstruction.Return var22) {
            } catch (TemplateException var23) {
               this.handleTemplateException(var23);
            } finally {
               this.currentMacroContext = prevMacroCtx;
               this.localContextStack = prevLocalContextStack;
               this.currentNamespace = prevNamespace;
            }
         } finally {
            if (elementPushed) {
               this.popElement();
            }

         }

      }
   }

   private void setMacroContextLocalsFromArguments(Macro.Context macroCtx, Macro macro, Map<String, ? extends Expression> namedArgs, List<? extends Expression> positionalArgs) throws TemplateException {
      String catchAllParamName = macro.getCatchAll();
      SimpleHash namedCatchAllParamValue = null;
      SimpleSequence positionalCatchAllParamValue = null;
      int nextPositionalArgToAssignIdx = 0;
      WithArgsState withArgsState = getWithArgState(macro);
      TemplateModel argValue;
      String[] argNames;
      int totalPositionalArgCnt;
      int withArgIdx;
      String argName;
      if (withArgsState != null) {
         TemplateHashModelEx byNameWithArgs = withArgsState.byName;
         TemplateSequenceModel byPositionWithArgs = withArgsState.byPosition;
         if (byNameWithArgs != null) {
            TemplateHashModelEx2.KeyValuePairIterator withArgsKVPIter = TemplateModelUtils.getKeyValuePairIterator(byNameWithArgs);

            while(withArgsKVPIter.hasNext()) {
               TemplateHashModelEx2.KeyValuePair withArgKVP = withArgsKVPIter.next();
               argValue = withArgKVP.getKey();
               if (!(argValue instanceof TemplateScalarModel)) {
                  throw new _TemplateModelException(new Object[]{"Expected string keys in the \"with args\" hash, but one of the keys was ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(argValue)), "."});
               }

               String argName = EvalUtil.modelToString((TemplateScalarModel)argValue, (Expression)null, (Environment)null);
               argValue = withArgKVP.getValue();
               boolean isArgNameDeclared = macro.hasArgNamed(argName);
               if (isArgNameDeclared) {
                  macroCtx.setLocalVar(argName, argValue);
               } else {
                  if (catchAllParamName == null) {
                     throw this.newUndeclaredParamNameException(macro, argName);
                  }

                  if (namedCatchAllParamValue == null) {
                     namedCatchAllParamValue = initNamedCatchAllParameter(macroCtx, catchAllParamName);
                  }

                  if (!withArgsState.orderLast) {
                     namedCatchAllParamValue.put(argName, argValue);
                  } else {
                     List<NameValuePair> orderLastByNameCatchAll = withArgsState.orderLastByNameCatchAll;
                     if (orderLastByNameCatchAll == null) {
                        orderLastByNameCatchAll = new ArrayList();
                        withArgsState.orderLastByNameCatchAll = (List)orderLastByNameCatchAll;
                     }

                     ((List)orderLastByNameCatchAll).add(new NameValuePair(argName, argValue));
                  }
               }
            }
         } else if (byPositionWithArgs != null) {
            if (!withArgsState.orderLast) {
               argNames = macro.getArgumentNamesNoCopy();
               withArgIdx = byPositionWithArgs.size();
               if (argNames.length < withArgIdx && catchAllParamName == null) {
                  throw this.newTooManyArgumentsException(macro, argNames, withArgIdx);
               }

               for(int argIdx = 0; argIdx < withArgIdx; ++argIdx) {
                  argValue = byPositionWithArgs.get(argIdx);

                  try {
                     if (nextPositionalArgToAssignIdx < argNames.length) {
                        argName = argNames[nextPositionalArgToAssignIdx++];
                        macroCtx.setLocalVar(argName, argValue);
                     } else {
                        if (positionalCatchAllParamValue == null) {
                           positionalCatchAllParamValue = initPositionalCatchAllParameter(macroCtx, catchAllParamName);
                        }

                        positionalCatchAllParamValue.add(argValue);
                     }
                  } catch (RuntimeException var19) {
                     throw new _MiscTemplateException(var19, this);
                  }
               }
            } else {
               if (namedArgs != null && !namedArgs.isEmpty() && byPositionWithArgs.size() != 0) {
                  throw new _MiscTemplateException("Call can't pass parameters by name, as there's \"with args last\" in effect that specifies parameters by position.");
               }

               if (catchAllParamName == null) {
                  totalPositionalArgCnt = (positionalArgs != null ? positionalArgs.size() : 0) + byPositionWithArgs.size();
                  if (totalPositionalArgCnt > macro.getArgumentNamesNoCopy().length) {
                     throw this.newTooManyArgumentsException(macro, macro.getArgumentNamesNoCopy(), totalPositionalArgCnt);
                  }
               }
            }
         }
      }

      Iterator var20;
      int withArgCnt;
      Expression argValueExp;
      if (namedArgs != null) {
         if (catchAllParamName != null && namedCatchAllParamValue == null && positionalCatchAllParamValue == null) {
            if (namedArgs.isEmpty() && withArgsState != null && withArgsState.byPosition != null) {
               positionalCatchAllParamValue = initPositionalCatchAllParameter(macroCtx, catchAllParamName);
            } else {
               namedCatchAllParamValue = initNamedCatchAllParameter(macroCtx, catchAllParamName);
            }
         }

         var20 = namedArgs.entrySet().iterator();

         while(var20.hasNext()) {
            Map.Entry<String, ? extends Expression> argNameAndValExp = (Map.Entry)var20.next();
            String argName = (String)argNameAndValExp.getKey();
            boolean isArgNameDeclared = macro.hasArgNamed(argName);
            if (!isArgNameDeclared && namedCatchAllParamValue == null) {
               if (positionalCatchAllParamValue != null) {
                  throw this.newBothNamedAndPositionalCatchAllParamsException(macro);
               }

               throw this.newUndeclaredParamNameException(macro, argName);
            }

            argValueExp = (Expression)argNameAndValExp.getValue();
            argValue = argValueExp.eval(this);
            if (isArgNameDeclared) {
               macroCtx.setLocalVar(argName, argValue);
            } else {
               namedCatchAllParamValue.put(argName, argValue);
            }
         }
      } else if (positionalArgs != null) {
         if (catchAllParamName != null && positionalCatchAllParamValue == null && namedCatchAllParamValue == null) {
            if (positionalArgs.isEmpty() && withArgsState != null && withArgsState.byName != null) {
               namedCatchAllParamValue = initNamedCatchAllParameter(macroCtx, catchAllParamName);
            } else {
               positionalCatchAllParamValue = initPositionalCatchAllParameter(macroCtx, catchAllParamName);
            }
         }

         String[] argNames = macro.getArgumentNamesNoCopy();
         withArgCnt = positionalArgs.size();
         totalPositionalArgCnt = withArgCnt + nextPositionalArgToAssignIdx;
         if (argNames.length < totalPositionalArgCnt && positionalCatchAllParamValue == null) {
            if (namedCatchAllParamValue != null) {
               throw this.newBothNamedAndPositionalCatchAllParamsException(macro);
            }

            throw this.newTooManyArgumentsException(macro, argNames, totalPositionalArgCnt);
         }

         for(withArgIdx = 0; withArgIdx < withArgCnt; ++withArgIdx) {
            argValueExp = (Expression)positionalArgs.get(withArgIdx);

            try {
               argValue = argValueExp.eval(this);
            } catch (RuntimeException var18) {
               throw new _MiscTemplateException(var18, this);
            }

            if (nextPositionalArgToAssignIdx < argNames.length) {
               argName = argNames[nextPositionalArgToAssignIdx++];
               macroCtx.setLocalVar(argName, argValue);
            } else {
               positionalCatchAllParamValue.add(argValue);
            }
         }
      }

      if (withArgsState != null && withArgsState.orderLast) {
         if (withArgsState.orderLastByNameCatchAll != null) {
            var20 = withArgsState.orderLastByNameCatchAll.iterator();

            while(var20.hasNext()) {
               NameValuePair nameValuePair = (NameValuePair)var20.next();
               if (!namedCatchAllParamValue.containsKey(nameValuePair.name)) {
                  namedCatchAllParamValue.put(nameValuePair.name, nameValuePair.value);
               }
            }
         } else if (withArgsState.byPosition != null) {
            TemplateSequenceModel byPosition = withArgsState.byPosition;
            withArgCnt = byPosition.size();
            argNames = macro.getArgumentNamesNoCopy();

            for(withArgIdx = 0; withArgIdx < withArgCnt; ++withArgIdx) {
               TemplateModel withArgValue = byPosition.get(withArgIdx);
               if (nextPositionalArgToAssignIdx < argNames.length) {
                  String argName = argNames[nextPositionalArgToAssignIdx++];
                  macroCtx.setLocalVar(argName, withArgValue);
               } else {
                  positionalCatchAllParamValue.add(withArgValue);
               }
            }
         }
      }

   }

   private static WithArgsState getWithArgState(Macro macro) {
      Macro.WithArgs withArgs = macro.getWithArgs();
      return withArgs == null ? null : new WithArgsState(withArgs.getByName(), withArgs.getByPosition(), withArgs.isOrderLast());
   }

   private _MiscTemplateException newTooManyArgumentsException(Macro macro, String[] argNames, int argsCnt) {
      return new _MiscTemplateException(this, new Object[]{macro.isFunction() ? "Function " : "Macro ", new _DelayedJQuote(macro.getName()), " only accepts ", new _DelayedToString(argNames.length), " parameters, but got ", new _DelayedToString(argsCnt), "."});
   }

   private static SimpleSequence initPositionalCatchAllParameter(Macro.Context macroCtx, String catchAllParamName) {
      SimpleSequence positionalCatchAllParamValue = new SimpleSequence(_TemplateAPI.SAFE_OBJECT_WRAPPER);
      macroCtx.setLocalVar(catchAllParamName, positionalCatchAllParamValue);
      return positionalCatchAllParamValue;
   }

   private static SimpleHash initNamedCatchAllParameter(Macro.Context macroCtx, String catchAllParamName) {
      SimpleHash namedCatchAllParamValue = new SimpleHash(new LinkedHashMap(), _TemplateAPI.SAFE_OBJECT_WRAPPER, 0);
      macroCtx.setLocalVar(catchAllParamName, namedCatchAllParamValue);
      return namedCatchAllParamValue;
   }

   private _MiscTemplateException newUndeclaredParamNameException(Macro macro, String argName) {
      return new _MiscTemplateException(this, new Object[]{macro.isFunction() ? "Function " : "Macro ", new _DelayedJQuote(macro.getName()), " has no parameter with name ", new _DelayedJQuote(argName), ". Valid parameter names are: ", new _DelayedJoinWithComma(macro.getArgumentNamesNoCopy())});
   }

   private _MiscTemplateException newBothNamedAndPositionalCatchAllParamsException(Macro macro) {
      return new _MiscTemplateException(this, new Object[]{macro.isFunction() ? "Function " : "Macro ", new _DelayedJQuote(macro.getName()), " call can't have both named and positional arguments that has to go into catch-all parameter."});
   }

   void visitMacroDef(Macro macro) {
      this.macroToNamespaceLookup.put(macro.getNamespaceLookupKey(), this.currentNamespace);
      this.currentNamespace.put(macro.getName(), macro);
   }

   Namespace getMacroNamespace(Macro macro) {
      return (Namespace)this.macroToNamespaceLookup.get(macro.getNamespaceLookupKey());
   }

   void recurse(TemplateNodeModel node, TemplateSequenceModel namespaces) throws TemplateException, IOException {
      if (node == null) {
         node = this.getCurrentVisitorNode();
         if (node == null) {
            throw new _TemplateModelException("The target node of recursion is missing or null.");
         }
      }

      TemplateSequenceModel children = node.getChildNodes();
      if (children != null) {
         int size = children.size();

         for(int i = 0; i < size; ++i) {
            TemplateNodeModel child = (TemplateNodeModel)children.get(i);
            if (child != null) {
               this.invokeNodeHandlerFor(child, namespaces);
            }
         }

      }
   }

   Macro.Context getCurrentMacroContext() {
      return this.currentMacroContext;
   }

   private void handleTemplateException(TemplateException templateException) throws TemplateException {
      if (templateException instanceof TemplateModelException && ((TemplateModelException)templateException).getReplaceWithCause() && templateException.getCause() instanceof TemplateException) {
         templateException = (TemplateException)templateException.getCause();
      }

      if (this.lastThrowable == templateException) {
         throw templateException;
      } else {
         this.lastThrowable = templateException;
         if (this.getLogTemplateExceptions() && LOG.isErrorEnabled() && !this.isInAttemptBlock()) {
            LOG.error("Error executing FreeMarker template", templateException);
         }

         try {
            if (templateException instanceof StopException) {
               throw templateException;
            } else {
               this.getTemplateExceptionHandler().handleTemplateException(templateException, this, this.out);
            }
         } catch (TemplateException var3) {
            if (this.isInAttemptBlock()) {
               this.getAttemptExceptionReporter().report(templateException, this);
            }

            throw var3;
         }
      }
   }

   public void setTemplateExceptionHandler(TemplateExceptionHandler templateExceptionHandler) {
      super.setTemplateExceptionHandler(templateExceptionHandler);
      this.lastThrowable = null;
   }

   public void setLocale(Locale locale) {
      Locale prevLocale = this.getLocale();
      super.setLocale(locale);
      if (!locale.equals(prevLocale)) {
         this.cachedTemplateNumberFormats = null;
         if (this.cachedTemplateNumberFormat != null && this.cachedTemplateNumberFormat.isLocaleBound()) {
            this.cachedTemplateNumberFormat = null;
         }

         if (this.cachedTempDateFormatArray != null) {
            for(int i = 0; i < 16; ++i) {
               TemplateDateFormat f = this.cachedTempDateFormatArray[i];
               if (f != null && f.isLocaleBound()) {
                  this.cachedTempDateFormatArray[i] = null;
               }
            }
         }

         this.cachedTempDateFormatsByFmtStrArray = null;
         this.cachedCollator = null;
      }

   }

   public void setTimeZone(TimeZone timeZone) {
      TimeZone prevTimeZone = this.getTimeZone();
      super.setTimeZone(timeZone);
      if (!timeZone.equals(prevTimeZone)) {
         int i;
         if (this.cachedTempDateFormatArray != null) {
            for(i = 0; i < 8; ++i) {
               TemplateDateFormat f = this.cachedTempDateFormatArray[i];
               if (f != null && f.isTimeZoneBound()) {
                  this.cachedTempDateFormatArray[i] = null;
               }
            }
         }

         if (this.cachedTempDateFormatsByFmtStrArray != null) {
            for(i = 0; i < 8; ++i) {
               this.cachedTempDateFormatsByFmtStrArray[i] = null;
            }
         }

         this.cachedSQLDateAndTimeTimeZoneSameAsNormal = null;
      }

   }

   public void setSQLDateAndTimeTimeZone(TimeZone timeZone) {
      TimeZone prevTimeZone = this.getSQLDateAndTimeTimeZone();
      super.setSQLDateAndTimeTimeZone(timeZone);
      if (!nullSafeEquals(timeZone, prevTimeZone)) {
         int i;
         if (this.cachedTempDateFormatArray != null) {
            for(i = 8; i < 16; ++i) {
               TemplateDateFormat format = this.cachedTempDateFormatArray[i];
               if (format != null && format.isTimeZoneBound()) {
                  this.cachedTempDateFormatArray[i] = null;
               }
            }
         }

         if (this.cachedTempDateFormatsByFmtStrArray != null) {
            for(i = 8; i < 16; ++i) {
               this.cachedTempDateFormatsByFmtStrArray[i] = null;
            }
         }

         this.cachedSQLDateAndTimeTimeZoneSameAsNormal = null;
      }

   }

   private static boolean nullSafeEquals(Object o1, Object o2) {
      if (o1 == o2) {
         return true;
      } else {
         return o1 != null && o2 != null ? o1.equals(o2) : false;
      }
   }

   boolean isSQLDateAndTimeTimeZoneSameAsNormal() {
      if (this.cachedSQLDateAndTimeTimeZoneSameAsNormal == null) {
         this.cachedSQLDateAndTimeTimeZoneSameAsNormal = this.getSQLDateAndTimeTimeZone() == null || this.getSQLDateAndTimeTimeZone().equals(this.getTimeZone());
      }

      return this.cachedSQLDateAndTimeTimeZoneSameAsNormal;
   }

   public void setURLEscapingCharset(String urlEscapingCharset) {
      this.cachedURLEscapingCharsetSet = false;
      super.setURLEscapingCharset(urlEscapingCharset);
   }

   public void setOutputEncoding(String outputEncoding) {
      this.cachedURLEscapingCharsetSet = false;
      super.setOutputEncoding(outputEncoding);
   }

   String getEffectiveURLEscapingCharset() {
      if (!this.cachedURLEscapingCharsetSet) {
         this.cachedURLEscapingCharset = this.getURLEscapingCharset();
         if (this.cachedURLEscapingCharset == null) {
            this.cachedURLEscapingCharset = this.getOutputEncoding();
         }

         this.cachedURLEscapingCharsetSet = true;
      }

      return this.cachedURLEscapingCharset;
   }

   Collator getCollator() {
      if (this.cachedCollator == null) {
         this.cachedCollator = Collator.getInstance(this.getLocale());
      }

      return this.cachedCollator;
   }

   public boolean applyEqualsOperator(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
      return EvalUtil.compare(leftValue, 1, rightValue, this);
   }

   public boolean applyEqualsOperatorLenient(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
      return EvalUtil.compareLenient(leftValue, 1, rightValue, this);
   }

   public boolean applyLessThanOperator(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
      return EvalUtil.compare(leftValue, 3, rightValue, this);
   }

   public boolean applyLessThanOrEqualsOperator(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
      return EvalUtil.compare(leftValue, 5, rightValue, this);
   }

   public boolean applyGreaterThanOperator(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
      return EvalUtil.compare(leftValue, 4, rightValue, this);
   }

   public boolean applyWithGreaterThanOrEqualsOperator(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
      return EvalUtil.compare(leftValue, 6, rightValue, this);
   }

   public void setOut(Writer out) {
      this.out = out;
   }

   public Writer getOut() {
      return this.out;
   }

   public void setNumberFormat(String formatName) {
      super.setNumberFormat(formatName);
      this.cachedTemplateNumberFormat = null;
   }

   String formatNumberToPlainText(TemplateNumberModel number, Expression exp, boolean useTempModelExc) throws TemplateException {
      return this.formatNumberToPlainText(number, this.getTemplateNumberFormat(exp, useTempModelExc), exp, useTempModelExc);
   }

   String formatNumberToPlainText(TemplateNumberModel number, TemplateNumberFormat format, Expression exp, boolean useTempModelExc) throws TemplateException {
      try {
         return EvalUtil.assertFormatResultNotNull(format.formatToPlainText(number));
      } catch (TemplateValueFormatException var6) {
         throw _MessageUtil.newCantFormatNumberException(format, exp, var6, useTempModelExc);
      }
   }

   String formatNumberToPlainText(Number number, BackwardCompatibleTemplateNumberFormat format, Expression exp) throws TemplateModelException, _MiscTemplateException {
      try {
         return format.format(number);
      } catch (UnformattableValueException var5) {
         throw new _MiscTemplateException(exp, var5, this, new Object[]{"Failed to format number with ", new _DelayedJQuote(format.getDescription()), ": ", var5.getMessage()});
      }
   }

   public TemplateNumberFormat getTemplateNumberFormat() throws TemplateValueFormatException {
      TemplateNumberFormat format = this.cachedTemplateNumberFormat;
      if (format == null) {
         format = this.getTemplateNumberFormat(this.getNumberFormat(), false);
         this.cachedTemplateNumberFormat = format;
      }

      return format;
   }

   public TemplateNumberFormat getTemplateNumberFormat(String formatString) throws TemplateValueFormatException {
      return this.getTemplateNumberFormat(formatString, true);
   }

   public TemplateNumberFormat getTemplateNumberFormat(String formatString, Locale locale) throws TemplateValueFormatException {
      if (locale.equals(this.getLocale())) {
         this.getTemplateNumberFormat(formatString);
      }

      return this.getTemplateNumberFormatWithoutCache(formatString, locale);
   }

   TemplateNumberFormat getTemplateNumberFormat(Expression exp, boolean useTempModelExc) throws TemplateException {
      try {
         TemplateNumberFormat format = this.getTemplateNumberFormat();
         return format;
      } catch (TemplateValueFormatException var6) {
         _ErrorDescriptionBuilder desc = (new _ErrorDescriptionBuilder(new Object[]{"Failed to get number format object for the current number format string, ", new _DelayedJQuote(this.getNumberFormat()), ": ", var6.getMessage()})).blame(exp);
         throw useTempModelExc ? new _TemplateModelException(var6, this, desc) : new _MiscTemplateException(var6, this, desc);
      }
   }

   TemplateNumberFormat getTemplateNumberFormat(String formatString, Expression exp, boolean useTempModelExc) throws TemplateException {
      try {
         TemplateNumberFormat format = this.getTemplateNumberFormat(formatString);
         return format;
      } catch (TemplateValueFormatException var7) {
         _ErrorDescriptionBuilder desc = (new _ErrorDescriptionBuilder(new Object[]{"Failed to get number format object for the ", new _DelayedJQuote(formatString), " number format string: ", var7.getMessage()})).blame(exp);
         throw useTempModelExc ? new _TemplateModelException(var7, this, desc) : new _MiscTemplateException(var7, this, desc);
      }
   }

   private TemplateNumberFormat getTemplateNumberFormat(String formatString, boolean cacheResult) throws TemplateValueFormatException {
      TemplateNumberFormat format;
      if (this.cachedTemplateNumberFormats == null) {
         if (cacheResult) {
            this.cachedTemplateNumberFormats = new HashMap();
         }
      } else {
         format = (TemplateNumberFormat)this.cachedTemplateNumberFormats.get(formatString);
         if (format != null) {
            return format;
         }
      }

      format = this.getTemplateNumberFormatWithoutCache(formatString, this.getLocale());
      if (cacheResult) {
         this.cachedTemplateNumberFormats.put(formatString, format);
      }

      return format;
   }

   private TemplateNumberFormat getTemplateNumberFormatWithoutCache(String formatString, Locale locale) throws TemplateValueFormatException {
      int formatStringLen = formatString.length();
      if (formatStringLen > 1 && formatString.charAt(0) == '@' && (this.isIcI2324OrLater() || this.hasCustomFormats()) && Character.isLetter(formatString.charAt(1))) {
         int endIdx;
         for(endIdx = 1; endIdx < formatStringLen; ++endIdx) {
            char c = formatString.charAt(endIdx);
            if (c == ' ' || c == '_') {
               break;
            }
         }

         String name = formatString.substring(1, endIdx);
         String params = endIdx < formatStringLen ? formatString.substring(endIdx + 1) : "";
         TemplateNumberFormatFactory formatFactory = this.getCustomNumberFormat(name);
         if (formatFactory == null) {
            throw new UndefinedCustomFormatException("No custom number format was defined with name " + StringUtil.jQuote(name));
         } else {
            return formatFactory.get(params, locale, this);
         }
      } else {
         return JavaTemplateNumberFormatFactory.INSTANCE.get(formatString, locale, this);
      }
   }

   public NumberFormat getCNumberFormat() {
      if (this.cNumberFormat == null) {
         if (this.configuration.getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_31) {
            this.cNumberFormat = (DecimalFormat)C_NUMBER_FORMAT_ICI_2_3_21.clone();
         } else {
            this.cNumberFormat = (DecimalFormat)C_NUMBER_FORMAT_ICI_2_3_20.clone();
         }
      }

      return this.cNumberFormat;
   }

   String transformNumberFormatGlobalCacheKey(String keyPart) {
      return this.configuration.getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_31 && "computer".equals(keyPart) ? "computer\u00002" : keyPart;
   }

   public void setTimeFormat(String timeFormat) {
      String prevTimeFormat = this.getTimeFormat();
      super.setTimeFormat(timeFormat);
      if (!timeFormat.equals(prevTimeFormat) && this.cachedTempDateFormatArray != null) {
         for(int i = 0; i < 16; i += 4) {
            this.cachedTempDateFormatArray[i + 1] = null;
         }
      }

   }

   public void setDateFormat(String dateFormat) {
      String prevDateFormat = this.getDateFormat();
      super.setDateFormat(dateFormat);
      if (!dateFormat.equals(prevDateFormat) && this.cachedTempDateFormatArray != null) {
         for(int i = 0; i < 16; i += 4) {
            this.cachedTempDateFormatArray[i + 2] = null;
         }
      }

   }

   public void setDateTimeFormat(String dateTimeFormat) {
      String prevDateTimeFormat = this.getDateTimeFormat();
      super.setDateTimeFormat(dateTimeFormat);
      if (!dateTimeFormat.equals(prevDateTimeFormat) && this.cachedTempDateFormatArray != null) {
         for(int i = 0; i < 16; i += 4) {
            this.cachedTempDateFormatArray[i + 3] = null;
         }
      }

   }

   public Configuration getConfiguration() {
      return this.configuration;
   }

   TemplateModel getLastReturnValue() {
      return this.lastReturnValue;
   }

   void setLastReturnValue(TemplateModel lastReturnValue) {
      this.lastReturnValue = lastReturnValue;
   }

   void clearLastReturnValue() {
      this.lastReturnValue = null;
   }

   String formatDateToPlainText(TemplateDateModel tdm, Expression tdmSourceExpr, boolean useTempModelExc) throws TemplateException {
      TemplateDateFormat format = this.getTemplateDateFormat(tdm, tdmSourceExpr, useTempModelExc);

      try {
         return EvalUtil.assertFormatResultNotNull(format.formatToPlainText(tdm));
      } catch (TemplateValueFormatException var6) {
         throw _MessageUtil.newCantFormatDateException(format, tdmSourceExpr, var6, useTempModelExc);
      }
   }

   String formatDateToPlainText(TemplateDateModel tdm, String formatString, Expression blamedDateSourceExp, Expression blamedFormatterExp, boolean useTempModelExc) throws TemplateException {
      Date date = EvalUtil.modelToDate(tdm, blamedDateSourceExp);
      TemplateDateFormat format = this.getTemplateDateFormat(formatString, tdm.getDateType(), date.getClass(), blamedDateSourceExp, blamedFormatterExp, useTempModelExc);

      try {
         return EvalUtil.assertFormatResultNotNull(format.formatToPlainText(tdm));
      } catch (TemplateValueFormatException var9) {
         throw _MessageUtil.newCantFormatDateException(format, blamedDateSourceExp, var9, useTempModelExc);
      }
   }

   public TemplateDateFormat getTemplateDateFormat(int dateType, Class<? extends Date> dateClass) throws TemplateValueFormatException {
      boolean isSQLDateOrTime = isSQLDateOrTimeClass(dateClass);
      return this.getTemplateDateFormat(dateType, this.shouldUseSQLDTTimeZone(isSQLDateOrTime), isSQLDateOrTime);
   }

   public TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, Class<? extends Date> dateClass) throws TemplateValueFormatException {
      boolean isSQLDateOrTime = isSQLDateOrTimeClass(dateClass);
      return this.getTemplateDateFormat(formatString, dateType, this.shouldUseSQLDTTimeZone(isSQLDateOrTime), isSQLDateOrTime, true);
   }

   public TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, Class<? extends Date> dateClass, Locale locale) throws TemplateValueFormatException {
      boolean isSQLDateOrTime = isSQLDateOrTimeClass(dateClass);
      boolean useSQLDTTZ = this.shouldUseSQLDTTimeZone(isSQLDateOrTime);
      return this.getTemplateDateFormat(formatString, dateType, locale, useSQLDTTZ ? this.getSQLDateAndTimeTimeZone() : this.getTimeZone(), isSQLDateOrTime);
   }

   public TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, Class<? extends Date> dateClass, Locale locale, TimeZone timeZone, TimeZone sqlDateAndTimeTimeZone) throws TemplateValueFormatException {
      boolean isSQLDateOrTime = isSQLDateOrTimeClass(dateClass);
      boolean useSQLDTTZ = this.shouldUseSQLDTTimeZone(isSQLDateOrTime);
      return this.getTemplateDateFormat(formatString, dateType, locale, useSQLDTTZ ? sqlDateAndTimeTimeZone : timeZone, isSQLDateOrTime);
   }

   public TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput) throws TemplateValueFormatException {
      Locale currentLocale = this.getLocale();
      if (locale.equals(currentLocale)) {
         TimeZone currentTimeZone = this.getTimeZone();
         byte equalCurrentTZ;
         if (timeZone.equals(currentTimeZone)) {
            equalCurrentTZ = 1;
         } else {
            TimeZone currentSQLDTTimeZone = this.getSQLDateAndTimeTimeZone();
            if (timeZone.equals(currentSQLDTTimeZone)) {
               equalCurrentTZ = 2;
            } else {
               equalCurrentTZ = 0;
            }
         }

         if (equalCurrentTZ != 0) {
            return this.getTemplateDateFormat(formatString, dateType, equalCurrentTZ == 2, zonelessInput, true);
         }
      }

      return this.getTemplateDateFormatWithoutCache(formatString, dateType, locale, timeZone, zonelessInput);
   }

   TemplateDateFormat getTemplateDateFormat(TemplateDateModel tdm, Expression tdmSourceExpr, boolean useTempModelExc) throws TemplateModelException, TemplateException {
      Date date = EvalUtil.modelToDate(tdm, tdmSourceExpr);
      TemplateDateFormat format = this.getTemplateDateFormat(tdm.getDateType(), date.getClass(), tdmSourceExpr, useTempModelExc);
      return format;
   }

   TemplateDateFormat getTemplateDateFormat(int dateType, Class<? extends Date> dateClass, Expression blamedDateSourceExp, boolean useTempModelExc) throws TemplateException {
      try {
         return this.getTemplateDateFormat(dateType, dateClass);
      } catch (UnknownDateTypeFormattingUnsupportedException var9) {
         throw _MessageUtil.newCantFormatUnknownTypeDateException(blamedDateSourceExp, var9);
      } catch (TemplateValueFormatException var10) {
         String settingName;
         String settingValue;
         switch (dateType) {
            case 1:
               settingName = "time_format";
               settingValue = this.getTimeFormat();
               break;
            case 2:
               settingName = "date_format";
               settingValue = this.getDateFormat();
               break;
            case 3:
               settingName = "datetime_format";
               settingValue = this.getDateTimeFormat();
               break;
            default:
               settingName = "???";
               settingValue = "???";
         }

         _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder(new Object[]{"The value of the \"", settingName, "\" FreeMarker configuration setting is a malformed date/time/datetime format string: ", new _DelayedJQuote(settingValue), ". Reason given: ", var10.getMessage()});
         throw useTempModelExc ? new _TemplateModelException(var10, new Object[]{desc}) : new _MiscTemplateException(var10, new Object[]{desc});
      }
   }

   TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, Class<? extends Date> dateClass, Expression blamedDateSourceExp, Expression blamedFormatterExp, boolean useTempModelExc) throws TemplateException {
      try {
         return this.getTemplateDateFormat(formatString, dateType, dateClass);
      } catch (UnknownDateTypeFormattingUnsupportedException var9) {
         throw _MessageUtil.newCantFormatUnknownTypeDateException(blamedDateSourceExp, var9);
      } catch (TemplateValueFormatException var10) {
         _ErrorDescriptionBuilder desc = (new _ErrorDescriptionBuilder(new Object[]{"Can't create date/time/datetime format based on format string ", new _DelayedJQuote(formatString), ". Reason given: ", var10.getMessage()})).blame(blamedFormatterExp);
         throw useTempModelExc ? new _TemplateModelException(var10, new Object[]{desc}) : new _MiscTemplateException(var10, new Object[]{desc});
      }
   }

   private TemplateDateFormat getTemplateDateFormat(int dateType, boolean useSQLDTTZ, boolean zonelessInput) throws TemplateValueFormatException {
      if (dateType == 0) {
         throw new UnknownDateTypeFormattingUnsupportedException();
      } else {
         int cacheIdx = this.getTemplateDateFormatCacheArrayIndex(dateType, zonelessInput, useSQLDTTZ);
         TemplateDateFormat[] cachedTemplateDateFormats = this.cachedTempDateFormatArray;
         if (cachedTemplateDateFormats == null) {
            cachedTemplateDateFormats = new TemplateDateFormat[16];
            this.cachedTempDateFormatArray = cachedTemplateDateFormats;
         }

         TemplateDateFormat format = cachedTemplateDateFormats[cacheIdx];
         if (format == null) {
            String formatString;
            switch (dateType) {
               case 1:
                  formatString = this.getTimeFormat();
                  break;
               case 2:
                  formatString = this.getDateFormat();
                  break;
               case 3:
                  formatString = this.getDateTimeFormat();
                  break;
               default:
                  throw new IllegalArgumentException("Invalid date type enum: " + dateType);
            }

            format = this.getTemplateDateFormat(formatString, dateType, useSQLDTTZ, zonelessInput, false);
            cachedTemplateDateFormats[cacheIdx] = format;
         }

         return format;
      }
   }

   private TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, boolean useSQLDTTimeZone, boolean zonelessInput, boolean cacheResult) throws TemplateValueFormatException {
      HashMap cachedFormatsByFormatString;
      label37: {
         HashMap<String, TemplateDateFormat>[] cachedTempDateFormatsByFmtStrArray = this.cachedTempDateFormatsByFmtStrArray;
         if (cachedTempDateFormatsByFmtStrArray == null) {
            if (!cacheResult) {
               cachedFormatsByFormatString = null;
               break label37;
            }

            cachedTempDateFormatsByFmtStrArray = new HashMap[16];
            this.cachedTempDateFormatsByFmtStrArray = cachedTempDateFormatsByFmtStrArray;
         }

         int cacheArrIdx = this.getTemplateDateFormatCacheArrayIndex(dateType, zonelessInput, useSQLDTTimeZone);
         cachedFormatsByFormatString = cachedTempDateFormatsByFmtStrArray[cacheArrIdx];
         TemplateDateFormat format;
         if (cachedFormatsByFormatString == null) {
            if (!cacheResult) {
               break label37;
            }

            cachedFormatsByFormatString = new HashMap(4);
            cachedTempDateFormatsByFmtStrArray[cacheArrIdx] = cachedFormatsByFormatString;
            format = null;
         } else {
            format = (TemplateDateFormat)cachedFormatsByFormatString.get(formatString);
         }

         if (format != null) {
            return format;
         }
      }

      TemplateDateFormat format = this.getTemplateDateFormatWithoutCache(formatString, dateType, this.getLocale(), useSQLDTTimeZone ? this.getSQLDateAndTimeTimeZone() : this.getTimeZone(), zonelessInput);
      if (cacheResult) {
         cachedFormatsByFormatString.put(formatString, format);
      }

      return format;
   }

   private TemplateDateFormat getTemplateDateFormatWithoutCache(String formatString, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput) throws TemplateValueFormatException {
      int formatStringLen = formatString.length();
      char firstChar = formatStringLen != 0 ? formatString.charAt(0) : 0;
      String formatParams;
      Object formatFactory;
      if (firstChar == 'x' && formatStringLen > 1 && formatString.charAt(1) == 's') {
         formatFactory = XSTemplateDateFormatFactory.INSTANCE;
         formatParams = formatString;
      } else if (firstChar == 'i' && formatStringLen > 2 && formatString.charAt(1) == 's' && formatString.charAt(2) == 'o') {
         formatFactory = ISOTemplateDateFormatFactory.INSTANCE;
         formatParams = formatString;
      } else if (firstChar == '@' && formatStringLen > 1 && (this.isIcI2324OrLater() || this.hasCustomFormats()) && Character.isLetter(formatString.charAt(1))) {
         int endIdx;
         for(endIdx = 1; endIdx < formatStringLen; ++endIdx) {
            char c = formatString.charAt(endIdx);
            if (c == ' ' || c == '_') {
               break;
            }
         }

         String name = formatString.substring(1, endIdx);
         formatParams = endIdx < formatStringLen ? formatString.substring(endIdx + 1) : "";
         formatFactory = this.getCustomDateFormat(name);
         if (formatFactory == null) {
            throw new UndefinedCustomFormatException("No custom date format was defined with name " + StringUtil.jQuote(name));
         }
      } else {
         formatParams = formatString;
         formatFactory = JavaTemplateDateFormatFactory.INSTANCE;
      }

      return ((TemplateDateFormatFactory)formatFactory).get(formatParams, dateType, locale, timeZone, zonelessInput, this);
   }

   boolean shouldUseSQLDTTZ(Class dateClass) {
      return dateClass != Date.class && !this.isSQLDateAndTimeTimeZoneSameAsNormal() && isSQLDateOrTimeClass(dateClass);
   }

   private boolean shouldUseSQLDTTimeZone(boolean sqlDateOrTime) {
      return sqlDateOrTime && !this.isSQLDateAndTimeTimeZoneSameAsNormal();
   }

   private static boolean isSQLDateOrTimeClass(Class dateClass) {
      return dateClass != Date.class && (dateClass == java.sql.Date.class || dateClass == Time.class || dateClass != Timestamp.class && (java.sql.Date.class.isAssignableFrom(dateClass) || Time.class.isAssignableFrom(dateClass)));
   }

   private int getTemplateDateFormatCacheArrayIndex(int dateType, boolean zonelessInput, boolean sqlDTTZ) {
      return dateType + (zonelessInput ? 4 : 0) + (sqlDTTZ ? 8 : 0);
   }

   DateUtil.DateToISO8601CalendarFactory getISOBuiltInCalendarFactory() {
      if (this.isoBuiltInCalendarFactory == null) {
         this.isoBuiltInCalendarFactory = new DateUtil.TrivialDateToISO8601CalendarFactory();
      }

      return this.isoBuiltInCalendarFactory;
   }

   TemplateTransformModel getTransform(Expression exp) throws TemplateException {
      TemplateTransformModel ttm = null;
      TemplateModel tm = exp.eval(this);
      if (tm instanceof TemplateTransformModel) {
         ttm = (TemplateTransformModel)tm;
      } else if (exp instanceof Identifier) {
         tm = this.configuration.getSharedVariable(exp.toString());
         if (tm instanceof TemplateTransformModel) {
            ttm = (TemplateTransformModel)tm;
         }
      }

      return ttm;
   }

   public TemplateModel getLocalVariable(String name) throws TemplateModelException {
      TemplateModel val = this.getNullableLocalVariable(name);
      return val != TemplateNullModel.INSTANCE ? val : null;
   }

   private final TemplateModel getNullableLocalVariable(String name) throws TemplateModelException {
      if (this.localContextStack != null) {
         for(int i = this.localContextStack.size() - 1; i >= 0; --i) {
            LocalContext lc = this.localContextStack.get(i);
            TemplateModel tm = lc.getLocalVariable(name);
            if (tm != null) {
               return tm;
            }
         }
      }

      return this.currentMacroContext == null ? null : this.currentMacroContext.getLocalVariable(name);
   }

   public TemplateModel getVariable(String name) throws TemplateModelException {
      TemplateModel result = this.getNullableLocalVariable(name);
      if (result != null) {
         return result != TemplateNullModel.INSTANCE ? result : null;
      } else {
         result = this.currentNamespace.get(name);
         return result != null ? result : this.getGlobalVariable(name);
      }
   }

   public TemplateModel getGlobalVariable(String name) throws TemplateModelException {
      TemplateModel result = this.globalNamespace.get(name);
      return result != null ? result : this.getDataModelOrSharedVariable(name);
   }

   public TemplateModel getDataModelOrSharedVariable(String name) throws TemplateModelException {
      TemplateModel dataModelVal = this.rootDataModel.get(name);
      return dataModelVal != null ? dataModelVal : this.configuration.getSharedVariable(name);
   }

   public void setGlobalVariable(String name, TemplateModel value) {
      this.globalNamespace.put(name, value);
   }

   public void setVariable(String name, TemplateModel value) {
      this.currentNamespace.put(name, value);
   }

   public void setLocalVariable(String name, TemplateModel value) {
      if (this.currentMacroContext == null) {
         throw new IllegalStateException("Not executing macro body");
      } else {
         this.currentMacroContext.setLocalVar(name, value);
      }
   }

   public Set getKnownVariableNames() throws TemplateModelException {
      Set set = this.configuration.getSharedVariableNames();
      TemplateModelIterator tmi;
      if (this.rootDataModel instanceof TemplateHashModelEx) {
         tmi = ((TemplateHashModelEx)this.rootDataModel).keys().iterator();

         while(tmi.hasNext()) {
            set.add(((TemplateScalarModel)tmi.next()).getAsString());
         }
      }

      tmi = this.globalNamespace.keys().iterator();

      while(tmi.hasNext()) {
         set.add(((TemplateScalarModel)tmi.next()).getAsString());
      }

      tmi = this.currentNamespace.keys().iterator();

      while(tmi.hasNext()) {
         set.add(((TemplateScalarModel)tmi.next()).getAsString());
      }

      if (this.currentMacroContext != null) {
         set.addAll(this.currentMacroContext.getLocalVariableNames());
      }

      if (this.localContextStack != null) {
         for(int i = this.localContextStack.size() - 1; i >= 0; --i) {
            LocalContext lc = this.localContextStack.get(i);
            set.addAll(lc.getLocalVariableNames());
         }
      }

      return set;
   }

   public void outputInstructionStack(PrintWriter pw) {
      outputInstructionStack(this.getInstructionStackSnapshot(), false, pw);
      pw.flush();
   }

   static void outputInstructionStack(TemplateElement[] instructionStackSnapshot, boolean terseMode, Writer w) {
      PrintWriter pw = (PrintWriter)((PrintWriter)(w instanceof PrintWriter ? w : null));

      try {
         if (instructionStackSnapshot != null) {
            int totalFrames = instructionStackSnapshot.length;
            int framesToPrint = terseMode ? (totalFrames <= 10 ? totalFrames : 9) : totalFrames;
            boolean hideNestringRelatedFrames = terseMode && framesToPrint < totalFrames;
            int nestingRelatedFramesHidden = 0;
            int trailingFramesHidden = 0;
            int framesPrinted = 0;

            for(int frameIdx = 0; frameIdx < totalFrames; ++frameIdx) {
               TemplateElement stackEl = instructionStackSnapshot[frameIdx];
               boolean nestingRelatedElement = frameIdx > 0 && stackEl instanceof BodyInstruction || frameIdx > 1 && instructionStackSnapshot[frameIdx - 1] instanceof BodyInstruction;
               if (framesPrinted < framesToPrint) {
                  if (nestingRelatedElement && hideNestringRelatedFrames) {
                     ++nestingRelatedFramesHidden;
                  } else {
                     w.write(frameIdx == 0 ? "\t- Failed at: " : (nestingRelatedElement ? "\t~ Reached through: " : "\t- Reached through: "));
                     w.write(instructionStackItemToString(stackEl));
                     if (pw != null) {
                        pw.println();
                     } else {
                        w.write(10);
                     }

                     ++framesPrinted;
                  }
               } else {
                  ++trailingFramesHidden;
               }
            }

            boolean hadClosingNotes = false;
            if (trailingFramesHidden > 0) {
               w.write("\t... (Had ");
               w.write(String.valueOf(trailingFramesHidden + nestingRelatedFramesHidden));
               w.write(" more, hidden for tersenes)");
               hadClosingNotes = true;
            }

            if (nestingRelatedFramesHidden > 0) {
               if (hadClosingNotes) {
                  w.write(32);
               } else {
                  w.write(9);
               }

               w.write("(Hidden " + nestingRelatedFramesHidden + " \"~\" lines for terseness)");
               if (pw != null) {
                  pw.println();
               } else {
                  w.write(10);
               }

               hadClosingNotes = true;
            }

            if (hadClosingNotes) {
               if (pw != null) {
                  pw.println();
               } else {
                  w.write(10);
               }
            }
         } else {
            w.write("(The stack was empty)");
            if (pw != null) {
               pw.println();
            } else {
               w.write(10);
            }
         }
      } catch (IOException var13) {
         LOG.error("Failed to print FTL stack trace", var13);
      }

   }

   TemplateElement[] getInstructionStackSnapshot() {
      int requiredLength = 0;
      int ln = this.instructionStackSize;

      for(int i = 0; i < ln; ++i) {
         TemplateElement stackEl = this.instructionStack[i];
         if (i == ln - 1 || stackEl.isShownInStackTrace()) {
            ++requiredLength;
         }
      }

      if (requiredLength == 0) {
         return null;
      } else {
         TemplateElement[] result = new TemplateElement[requiredLength];
         int dstIdx = requiredLength - 1;

         for(int i = 0; i < ln; ++i) {
            TemplateElement stackEl = this.instructionStack[i];
            if (i == ln - 1 || stackEl.isShownInStackTrace()) {
               result[dstIdx--] = stackEl;
            }
         }

         return result;
      }
   }

   static String instructionStackItemToString(TemplateElement stackEl) {
      StringBuilder sb = new StringBuilder();
      appendInstructionStackItem(stackEl, sb);
      return sb.toString();
   }

   static void appendInstructionStackItem(TemplateElement stackEl, StringBuilder sb) {
      sb.append(_MessageUtil.shorten(stackEl.getDescription(), 40));
      sb.append("  [");
      Macro enclosingMacro = getEnclosingMacro(stackEl);
      if (enclosingMacro != null) {
         sb.append(_MessageUtil.formatLocationForEvaluationError(enclosingMacro, stackEl.beginLine, stackEl.beginColumn));
      } else {
         sb.append(_MessageUtil.formatLocationForEvaluationError(stackEl.getTemplate(), stackEl.beginLine, stackEl.beginColumn));
      }

      sb.append("]");
   }

   private static Macro getEnclosingMacro(TemplateElement stackEl) {
      while(stackEl != null) {
         if (stackEl instanceof Macro) {
            return (Macro)stackEl;
         }

         stackEl = stackEl.getParentElement();
      }

      return null;
   }

   private void pushLocalContext(LocalContext localContext) {
      if (this.localContextStack == null) {
         this.localContextStack = new LocalContextStack();
      }

      this.localContextStack.push(localContext);
   }

   LocalContextStack getLocalContextStack() {
      return this.localContextStack;
   }

   public Namespace getNamespace(String name) {
      if (name.startsWith("/")) {
         name = name.substring(1);
      }

      return this.loadedLibs != null ? (Namespace)this.loadedLibs.get(name) : null;
   }

   public Namespace getMainNamespace() {
      return this.mainNamespace;
   }

   public Namespace getCurrentNamespace() {
      return this.currentNamespace;
   }

   public Namespace getGlobalNamespace() {
      return this.globalNamespace;
   }

   public TemplateHashModel getDataModel() {
      return (TemplateHashModel)(this.rootDataModel instanceof TemplateHashModelEx ? new TemplateHashModelEx() {
         public boolean isEmpty() throws TemplateModelException {
            return false;
         }

         public TemplateModel get(String key) throws TemplateModelException {
            return Environment.this.getDataModelOrSharedVariable(key);
         }

         public TemplateCollectionModel values() throws TemplateModelException {
            return ((TemplateHashModelEx)Environment.this.rootDataModel).values();
         }

         public TemplateCollectionModel keys() throws TemplateModelException {
            return ((TemplateHashModelEx)Environment.this.rootDataModel).keys();
         }

         public int size() throws TemplateModelException {
            return ((TemplateHashModelEx)Environment.this.rootDataModel).size();
         }
      } : new TemplateHashModel() {
         public boolean isEmpty() {
            return false;
         }

         public TemplateModel get(String key) throws TemplateModelException {
            TemplateModel value = Environment.this.rootDataModel.get(key);
            return value != null ? value : Environment.this.configuration.getSharedVariable(key);
         }
      });
   }

   public TemplateHashModel getGlobalVariables() {
      return new TemplateHashModel() {
         public boolean isEmpty() {
            return false;
         }

         public TemplateModel get(String key) throws TemplateModelException {
            TemplateModel result = Environment.this.globalNamespace.get(key);
            if (result == null) {
               result = Environment.this.rootDataModel.get(key);
            }

            if (result == null) {
               result = Environment.this.configuration.getSharedVariable(key);
            }

            return result;
         }
      };
   }

   private void pushElement(TemplateElement element) {
      int newSize = ++this.instructionStackSize;
      TemplateElement[] instructionStack = this.instructionStack;
      if (newSize > instructionStack.length) {
         TemplateElement[] newInstructionStack = new TemplateElement[newSize * 2];

         for(int i = 0; i < instructionStack.length; ++i) {
            newInstructionStack[i] = instructionStack[i];
         }

         instructionStack = newInstructionStack;
         this.instructionStack = newInstructionStack;
      }

      instructionStack[newSize - 1] = element;
   }

   private void popElement() {
      --this.instructionStackSize;
   }

   void replaceElementStackTop(TemplateElement instr) {
      this.instructionStack[this.instructionStackSize - 1] = instr;
   }

   public TemplateNodeModel getCurrentVisitorNode() {
      return this.currentVisitorNode;
   }

   public void setCurrentVisitorNode(TemplateNodeModel node) {
      this.currentVisitorNode = node;
   }

   TemplateModel getNodeProcessor(TemplateNodeModel node) throws TemplateException {
      String nodeName = node.getNodeName();
      if (nodeName == null) {
         throw new _MiscTemplateException(this, "Node name is null.");
      } else {
         TemplateModel result = this.getNodeProcessor(nodeName, node.getNodeNamespace(), 0);
         if (result == null) {
            String type = node.getNodeType();
            if (type == null) {
               type = "default";
            }

            result = this.getNodeProcessor("@" + type, (String)null, 0);
         }

         return result;
      }
   }

   private TemplateModel getNodeProcessor(String nodeName, String nsURI, int startIndex) throws TemplateException {
      TemplateModel result = null;
      int size = this.nodeNamespaces.size();

      int i;
      for(i = startIndex; i < size; ++i) {
         Namespace ns = null;

         try {
            ns = (Namespace)this.nodeNamespaces.get(i);
         } catch (ClassCastException var9) {
            throw new _MiscTemplateException(this, "A \"using\" clause should contain a sequence of namespaces or strings that indicate the location of importable macro libraries.");
         }

         result = this.getNodeProcessor(ns, nodeName, nsURI);
         if (result != null) {
            break;
         }
      }

      if (result != null) {
         this.nodeNamespaceIndex = i + 1;
         this.currentNodeName = nodeName;
         this.currentNodeNS = nsURI;
      }

      return result;
   }

   private TemplateModel getNodeProcessor(Namespace ns, String localName, String nsURI) throws TemplateException {
      TemplateModel result = null;
      if (nsURI == null) {
         result = ns.get(localName);
         if (!(result instanceof Macro) && !(result instanceof TemplateTransformModel)) {
            result = null;
         }
      } else {
         Template template = ns.getTemplate();
         String prefix = template.getPrefixForNamespace(nsURI);
         if (prefix == null) {
            return null;
         }

         if (prefix.length() > 0) {
            result = ns.get(prefix + ":" + localName);
            if (!(result instanceof Macro) && !(result instanceof TemplateTransformModel)) {
               result = null;
            }
         } else {
            if (nsURI.length() == 0) {
               result = ns.get("N:" + localName);
               if (!(result instanceof Macro) && !(result instanceof TemplateTransformModel)) {
                  result = null;
               }
            }

            if (nsURI.equals(template.getDefaultNS())) {
               result = ns.get("D:" + localName);
               if (!(result instanceof Macro) && !(result instanceof TemplateTransformModel)) {
                  result = null;
               }
            }

            if (result == null) {
               result = ns.get(localName);
               if (!(result instanceof Macro) && !(result instanceof TemplateTransformModel)) {
                  result = null;
               }
            }
         }
      }

      return result;
   }

   public void include(String name, String encoding, boolean parse) throws IOException, TemplateException {
      this.include(this.getTemplateForInclusion(name, encoding, parse));
   }

   public Template getTemplateForInclusion(String name, String encoding, boolean parse) throws IOException {
      return this.getTemplateForInclusion(name, encoding, parse, false);
   }

   public Template getTemplateForInclusion(String name, String encoding, boolean parseAsFTL, boolean ignoreMissing) throws IOException {
      return this.configuration.getTemplate(name, this.getLocale(), this.getIncludedTemplateCustomLookupCondition(), encoding != null ? encoding : this.getIncludedTemplateEncoding(), parseAsFTL, ignoreMissing);
   }

   private Object getIncludedTemplateCustomLookupCondition() {
      return this.getTemplate().getCustomLookupCondition();
   }

   private String getIncludedTemplateEncoding() {
      String encoding = this.getTemplate().getEncoding();
      if (encoding == null) {
         encoding = this.configuration.getEncoding(this.getLocale());
      }

      return encoding;
   }

   public void include(Template includedTemplate) throws TemplateException, IOException {
      boolean parentReplacementOn = this.isBeforeIcI2322();
      Template prevTemplate = this.getTemplate();
      if (parentReplacementOn) {
         this.setParent(includedTemplate);
      } else {
         this.legacyParent = includedTemplate;
      }

      this.importMacros(includedTemplate);

      try {
         this.visit(includedTemplate.getRootTreeNode());
      } finally {
         if (parentReplacementOn) {
            this.setParent(prevTemplate);
         } else {
            this.legacyParent = prevTemplate;
         }

      }

   }

   public Namespace importLib(String templateName, String targetNsVarName) throws IOException, TemplateException {
      return this.importLib(templateName, targetNsVarName, this.getLazyImports());
   }

   public Namespace importLib(Template loadedTemplate, String targetNsVarName) throws IOException, TemplateException {
      return this.importLib((String)null, loadedTemplate, targetNsVarName);
   }

   public Namespace importLib(String templateName, String targetNsVarName, boolean lazy) throws IOException, TemplateException {
      return lazy ? this.importLib(templateName, (Template)null, targetNsVarName) : this.importLib((String)null, this.getTemplateForImporting(templateName), targetNsVarName);
   }

   public Template getTemplateForImporting(String name) throws IOException {
      return this.getTemplateForInclusion(name, (String)null, true);
   }

   private Namespace importLib(String templateName, Template loadedTemplate, String targetNsVarName) throws IOException, TemplateException {
      boolean lazyImport;
      if (loadedTemplate != null) {
         lazyImport = false;
         templateName = loadedTemplate.getName();
      } else {
         lazyImport = true;
         TemplateNameFormat tnf = this.getConfiguration().getTemplateNameFormat();
         templateName = _CacheAPI.normalizeRootBasedName(tnf, templateName);
      }

      if (this.loadedLibs == null) {
         this.loadedLibs = new HashMap();
      }

      Namespace existingNamespace = (Namespace)this.loadedLibs.get(templateName);
      if (existingNamespace != null) {
         if (targetNsVarName != null) {
            this.setVariable(targetNsVarName, existingNamespace);
            if (this.isIcI2324OrLater() && this.currentNamespace == this.mainNamespace) {
               this.globalNamespace.put(targetNsVarName, existingNamespace);
            }
         }

         if (!lazyImport && existingNamespace instanceof LazilyInitializedNamespace) {
            ((LazilyInitializedNamespace)existingNamespace).ensureInitializedTME();
         }
      } else {
         Namespace newNamespace = lazyImport ? new LazilyInitializedNamespace(templateName) : new Namespace(loadedTemplate);
         this.loadedLibs.put(templateName, newNamespace);
         if (targetNsVarName != null) {
            this.setVariable(targetNsVarName, (TemplateModel)newNamespace);
            if (this.currentNamespace == this.mainNamespace) {
               this.globalNamespace.put(targetNsVarName, newNamespace);
            }
         }

         if (!lazyImport) {
            this.initializeImportLibNamespace((Namespace)newNamespace, loadedTemplate);
         }
      }

      return (Namespace)this.loadedLibs.get(templateName);
   }

   private void initializeImportLibNamespace(Namespace newNamespace, Template loadedTemplate) throws TemplateException, IOException {
      Namespace prevNamespace = this.currentNamespace;
      this.currentNamespace = newNamespace;
      Writer prevOut = this.out;
      this.out = NullWriter.INSTANCE;

      try {
         this.include(loadedTemplate);
      } finally {
         this.out = prevOut;
         this.currentNamespace = prevNamespace;
      }

   }

   public String toFullTemplateName(String baseName, String targetName) throws MalformedTemplateNameException {
      return !this.isClassicCompatible() && baseName != null ? _CacheAPI.toRootBasedName(this.configuration.getTemplateNameFormat(), baseName, targetName) : targetName;
   }

   public String rootBasedToAbsoluteTemplateName(String rootBasedName) throws MalformedTemplateNameException {
      return _CacheAPI.rootBasedNameToAbsoluteName(this.configuration.getTemplateNameFormat(), rootBasedName);
   }

   String renderElementToString(TemplateElement te) throws IOException, TemplateException {
      Writer prevOut = this.out;

      String var4;
      try {
         StringWriter sw = new StringWriter();
         this.out = sw;
         this.visit(te);
         var4 = sw.toString();
      } finally {
         this.out = prevOut;
      }

      return var4;
   }

   void importMacros(Template template) {
      Iterator it = template.getMacros().values().iterator();

      while(it.hasNext()) {
         this.visitMacroDef((Macro)it.next());
      }

   }

   public String getNamespaceForPrefix(String prefix) {
      return this.currentNamespace.getTemplate().getNamespaceForPrefix(prefix);
   }

   public String getPrefixForNamespace(String nsURI) {
      return this.currentNamespace.getTemplate().getPrefixForNamespace(nsURI);
   }

   public String getDefaultNS() {
      return this.currentNamespace.getTemplate().getDefaultNS();
   }

   public Object __getitem__(String key) throws TemplateModelException {
      return BeansWrapper.getDefaultInstance().unwrap(this.getVariable(key));
   }

   public void __setitem__(String key, Object o) throws TemplateException {
      this.setGlobalVariable(key, this.getObjectWrapper().wrap(o));
   }

   public Object getCustomState(Object identityKey) {
      return this.customStateVariables == null ? null : this.customStateVariables.get(identityKey);
   }

   public Object setCustomState(Object identityKey, Object value) {
      IdentityHashMap<Object, Object> customStateVariables = this.customStateVariables;
      if (customStateVariables == null) {
         customStateVariables = new IdentityHashMap();
         this.customStateVariables = customStateVariables;
      }

      return customStateVariables.put(identityKey, value);
   }

   private boolean isBeforeIcI2322() {
      return this.configuration.getIncompatibleImprovements().intValue() < _TemplateAPI.VERSION_INT_2_3_22;
   }

   boolean isIcI2324OrLater() {
      return this.configuration.getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_24;
   }

   boolean getFastInvalidReferenceExceptions() {
      return this.fastInvalidReferenceExceptions;
   }

   boolean setFastInvalidReferenceExceptions(boolean b) {
      boolean res = this.fastInvalidReferenceExceptions;
      this.fastInvalidReferenceExceptions = b;
      return res;
   }

   static {
      C_NUMBER_FORMAT_ICI_2_3_20 = new DecimalFormat("0.################", new DecimalFormatSymbols(Locale.US));
      C_NUMBER_FORMAT_ICI_2_3_20.setGroupingUsed(false);
      C_NUMBER_FORMAT_ICI_2_3_20.setDecimalSeparatorAlwaysShown(false);
      C_NUMBER_FORMAT_ICI_2_3_21 = (DecimalFormat)C_NUMBER_FORMAT_ICI_2_3_20.clone();
      DecimalFormatSymbols symbols = C_NUMBER_FORMAT_ICI_2_3_21.getDecimalFormatSymbols();
      symbols.setInfinity("INF");
      symbols.setNaN("NaN");
      C_NUMBER_FORMAT_ICI_2_3_21.setDecimalFormatSymbols(symbols);
      NO_OUT_ARGS = new TemplateModel[0];
      EMPTY_BODY_WRITER = new Writer() {
         public void write(char[] cbuf, int off, int len) throws IOException {
            if (len > 0) {
               throw new IOException("This transform does not allow nested content.");
            }
         }

         public void flush() {
         }

         public void close() {
         }
      };
   }

   class LazilyInitializedNamespace extends Namespace {
      private final String templateName;
      private final Locale locale;
      private final String encoding;
      private final Object customLookupCondition;
      private InitializationStatus status;

      private LazilyInitializedNamespace(String templateName) {
         super((Template)null);
         this.status = Environment.InitializationStatus.UNINITIALIZED;
         this.templateName = templateName;
         this.locale = Environment.this.getLocale();
         this.encoding = Environment.this.getIncludedTemplateEncoding();
         this.customLookupCondition = Environment.this.getIncludedTemplateCustomLookupCondition();
      }

      private void ensureInitializedTME() throws TemplateModelException {
         if (this.status != Environment.InitializationStatus.INITIALIZED && this.status != Environment.InitializationStatus.INITIALIZING) {
            if (this.status == Environment.InitializationStatus.FAILED) {
               throw new TemplateModelException("Lazy initialization of the imported namespace for " + StringUtil.jQuote(this.templateName) + " has already failed earlier; won't retry it.");
            }

            try {
               this.status = Environment.InitializationStatus.INITIALIZING;
               this.initialize();
               this.status = Environment.InitializationStatus.INITIALIZED;
            } catch (Exception var5) {
               throw new TemplateModelException("Lazy initialization of the imported namespace for " + StringUtil.jQuote(this.templateName) + " has failed; see cause exception", var5);
            } finally {
               if (this.status != Environment.InitializationStatus.INITIALIZED) {
                  this.status = Environment.InitializationStatus.FAILED;
               }

            }
         }

      }

      private void ensureInitializedRTE() {
         try {
            this.ensureInitializedTME();
         } catch (TemplateModelException var2) {
            throw new RuntimeException(var2.getMessage(), var2.getCause());
         }
      }

      private void initialize() throws IOException, TemplateException {
         this.setTemplate(Environment.this.configuration.getTemplate(this.templateName, this.locale, this.customLookupCondition, this.encoding, true, false));
         Locale lastLocale = Environment.this.getLocale();

         try {
            Environment.this.setLocale(this.locale);
            Environment.this.initializeImportLibNamespace(this, this.getTemplate());
         } finally {
            Environment.this.setLocale(lastLocale);
         }

      }

      protected Map copyMap(Map map) {
         this.ensureInitializedRTE();
         return super.copyMap(map);
      }

      public Template getTemplate() {
         this.ensureInitializedRTE();
         return super.getTemplate();
      }

      public void put(String key, Object value) {
         this.ensureInitializedRTE();
         super.put(key, value);
      }

      public void put(String key, boolean b) {
         this.ensureInitializedRTE();
         super.put(key, b);
      }

      public TemplateModel get(String key) throws TemplateModelException {
         this.ensureInitializedTME();
         return super.get(key);
      }

      public boolean containsKey(String key) {
         this.ensureInitializedRTE();
         return super.containsKey(key);
      }

      public void remove(String key) {
         this.ensureInitializedRTE();
         super.remove(key);
      }

      public void putAll(Map m) {
         this.ensureInitializedRTE();
         super.putAll(m);
      }

      public Map toMap() throws TemplateModelException {
         this.ensureInitializedTME();
         return super.toMap();
      }

      public String toString() {
         this.ensureInitializedRTE();
         return super.toString();
      }

      public int size() {
         this.ensureInitializedRTE();
         return super.size();
      }

      public boolean isEmpty() {
         this.ensureInitializedRTE();
         return super.isEmpty();
      }

      public TemplateCollectionModel keys() {
         this.ensureInitializedRTE();
         return super.keys();
      }

      public TemplateCollectionModel values() {
         this.ensureInitializedRTE();
         return super.values();
      }

      public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() {
         this.ensureInitializedRTE();
         return super.keyValuePairIterator();
      }

      // $FF: synthetic method
      LazilyInitializedNamespace(String x1, Object x2) {
         this(x1);
      }
   }

   private static enum InitializationStatus {
      UNINITIALIZED,
      INITIALIZING,
      INITIALIZED,
      FAILED;
   }

   public class Namespace extends SimpleHash {
      private Template template;

      Namespace() {
         super((ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
         this.template = Environment.this.getTemplate();
      }

      Namespace(Template template) {
         super((ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
         this.template = template;
      }

      public Template getTemplate() {
         return this.template == null ? Environment.this.getTemplate() : this.template;
      }

      void setTemplate(Template template) {
         this.template = template;
      }
   }

   final class NestedElementTemplateDirectiveBody implements TemplateDirectiveBody {
      private final TemplateElement[] childBuffer;

      private NestedElementTemplateDirectiveBody(TemplateElement[] childBuffer) {
         this.childBuffer = childBuffer;
      }

      public void render(Writer newOut) throws TemplateException, IOException {
         Writer prevOut = Environment.this.out;
         Environment.this.out = newOut;

         try {
            Environment.this.visit(this.childBuffer);
         } finally {
            Environment.this.out = prevOut;
         }

      }

      TemplateElement[] getChildrenBuffer() {
         return this.childBuffer;
      }

      // $FF: synthetic method
      NestedElementTemplateDirectiveBody(TemplateElement[] x1, Object x2) {
         this(x1);
      }
   }

   private static final class NameValuePair {
      private final String name;
      private final TemplateModel value;

      public NameValuePair(String name, TemplateModel value) {
         this.name = name;
         this.value = value;
      }
   }

   private static final class WithArgsState {
      private final TemplateHashModelEx byName;
      private final TemplateSequenceModel byPosition;
      private final boolean orderLast;
      private List<NameValuePair> orderLastByNameCatchAll;

      public WithArgsState(TemplateHashModelEx byName, TemplateSequenceModel byPosition, boolean orderLast) {
         this.byName = byName;
         this.byPosition = byPosition;
         this.orderLast = orderLast;
      }
   }

   private static class LocalContextWithNewLocal implements LocalContext {
      private final String lambdaArgName;
      private final TemplateModel lambdaArgValue;

      public LocalContextWithNewLocal(String lambdaArgName, TemplateModel lambdaArgValue) {
         this.lambdaArgName = lambdaArgName;
         this.lambdaArgValue = lambdaArgValue;
      }

      public TemplateModel getLocalVariable(String name) throws TemplateModelException {
         return name.equals(this.lambdaArgName) ? this.lambdaArgValue : null;
      }

      public Collection getLocalVariableNames() throws TemplateModelException {
         return Collections.singleton(this.lambdaArgName);
      }
   }
}
