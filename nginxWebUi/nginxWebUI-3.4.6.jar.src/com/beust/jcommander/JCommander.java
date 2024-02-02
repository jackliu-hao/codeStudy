/*      */ package com.beust.jcommander;
/*      */ 
/*      */ import com.beust.jcommander.converters.IParameterSplitter;
/*      */ import com.beust.jcommander.converters.NoConverter;
/*      */ import com.beust.jcommander.converters.StringConverter;
/*      */ import com.beust.jcommander.internal.Console;
/*      */ import com.beust.jcommander.internal.DefaultConsole;
/*      */ import com.beust.jcommander.internal.DefaultConverterFactory;
/*      */ import com.beust.jcommander.internal.JDK6Console;
/*      */ import com.beust.jcommander.internal.Lists;
/*      */ import com.beust.jcommander.internal.Maps;
/*      */ import com.beust.jcommander.internal.Nullable;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.FileReader;
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.ParameterizedType;
/*      */ import java.lang.reflect.Type;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.EnumSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.ResourceBundle;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class JCommander
/*      */ {
/*      */   public static final String DEBUG_PROPERTY = "jcommander.debug";
/*      */   private Map<FuzzyMap.IKey, ParameterDescription> m_descriptions;
/*   76 */   private List<Object> m_objects = Lists.newArrayList();
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean m_firstTimeMainParameter = true;
/*      */ 
/*      */ 
/*      */   
/*   84 */   private Parameterized m_mainParameter = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Object m_mainParameterObject;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Parameter m_mainParameterAnnotation;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ParameterDescription m_mainParameterDescription;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  106 */   private Map<Parameterized, ParameterDescription> m_requiredFields = Maps.newHashMap();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  111 */   private Map<Parameterized, ParameterDescription> m_fields = Maps.newHashMap();
/*      */ 
/*      */ 
/*      */   
/*      */   private ResourceBundle m_bundle;
/*      */ 
/*      */ 
/*      */   
/*      */   private IDefaultProvider m_defaultProvider;
/*      */ 
/*      */ 
/*      */   
/*  123 */   private Map<ProgramName, JCommander> m_commands = Maps.newLinkedHashMap();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  128 */   private Map<FuzzyMap.IKey, ProgramName> aliasMap = Maps.newLinkedHashMap();
/*      */ 
/*      */ 
/*      */   
/*      */   private String m_parsedCommand;
/*      */ 
/*      */ 
/*      */   
/*      */   private String m_parsedAlias;
/*      */ 
/*      */   
/*      */   private ProgramName m_programName;
/*      */ 
/*      */ 
/*      */   
/*  143 */   private Comparator<? super ParameterDescription> m_parameterDescriptionComparator = new Comparator<ParameterDescription>()
/*      */     {
/*      */       public int compare(ParameterDescription p0, ParameterDescription p1)
/*      */       {
/*  147 */         return p0.getLongestName().compareTo(p1.getLongestName());
/*      */       }
/*      */     };
/*      */   
/*  151 */   private int m_columnSize = 79;
/*      */   
/*      */   private boolean m_helpWasSpecified;
/*      */   
/*  155 */   private List<String> m_unknownArgs = Lists.newArrayList();
/*      */ 
/*      */   
/*      */   private boolean m_acceptUnknownOptions = false;
/*      */   
/*      */   private boolean m_allowParameterOverwriting = false;
/*      */   
/*      */   private static Console m_console;
/*      */   
/*  164 */   private static LinkedList<IStringConverterFactory> CONVERTER_FACTORIES = Lists.newLinkedList(); private final IVariableArity DEFAULT_VARIABLE_ARITY; private int m_verbose; private boolean m_caseSensitiveOptions; private boolean m_allowAbbreviatedOptions;
/*      */   
/*      */   static {
/*  167 */     CONVERTER_FACTORIES.addFirst(new DefaultConverterFactory());
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Console getConsole() {
/*  214 */     if (m_console == null) {
/*      */       try {
/*  216 */         Method consoleMethod = System.class.getDeclaredMethod("console", new Class[0]);
/*  217 */         Object console = consoleMethod.invoke(null, new Object[0]);
/*  218 */         m_console = (Console)new JDK6Console(console);
/*  219 */       } catch (Throwable t) {
/*  220 */         m_console = (Console)new DefaultConsole();
/*      */       } 
/*      */     }
/*  223 */     return m_console;
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
/*      */   public final void addObject(Object object) {
/*  236 */     if (object instanceof Iterable) {
/*      */       
/*  238 */       for (Object o : object) {
/*  239 */         this.m_objects.add(o);
/*      */       }
/*  241 */     } else if (object.getClass().isArray()) {
/*      */       
/*  243 */       for (Object o : (Object[])object) {
/*  244 */         this.m_objects.add(o);
/*      */       }
/*      */     } else {
/*      */       
/*  248 */       this.m_objects.add(object);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void setDescriptionsBundle(ResourceBundle bundle) {
/*  258 */     this.m_bundle = bundle;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void parse(String... args) {
/*  265 */     parse(true, args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void parseWithoutValidation(String... args) {
/*  272 */     parse(false, args);
/*      */   }
/*      */   
/*      */   private void parse(boolean validate, String... args) {
/*  276 */     StringBuilder sb = new StringBuilder("Parsing \"");
/*  277 */     sb.append(join((Object[])args).append("\"\n  with:").append(join(this.m_objects.toArray())));
/*  278 */     p(sb.toString());
/*      */     
/*  280 */     if (this.m_descriptions == null) createDescriptions(); 
/*  281 */     initializeDefaultValues();
/*  282 */     parseValues(expandArgs(args), validate);
/*  283 */     if (validate) validateOptions(); 
/*      */   }
/*      */   
/*      */   private StringBuilder join(Object[] args) {
/*  287 */     StringBuilder result = new StringBuilder();
/*  288 */     for (int i = 0; i < args.length; i++) {
/*  289 */       if (i > 0) result.append(" "); 
/*  290 */       result.append(args[i]);
/*      */     } 
/*  292 */     return result;
/*      */   }
/*      */   
/*      */   private void initializeDefaultValues() {
/*  296 */     if (this.m_defaultProvider != null) {
/*  297 */       for (ParameterDescription pd : this.m_descriptions.values()) {
/*  298 */         initializeDefaultValue(pd);
/*      */       }
/*      */       
/*  301 */       for (Map.Entry<ProgramName, JCommander> entry : this.m_commands.entrySet()) {
/*  302 */         ((JCommander)entry.getValue()).initializeDefaultValues();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void validateOptions() {
/*  312 */     if (this.m_helpWasSpecified) {
/*      */       return;
/*      */     }
/*      */     
/*  316 */     if (!this.m_requiredFields.isEmpty()) {
/*  317 */       StringBuilder missingFields = new StringBuilder();
/*  318 */       for (ParameterDescription pd : this.m_requiredFields.values()) {
/*  319 */         missingFields.append(pd.getNames()).append(" ");
/*      */       }
/*  321 */       throw new ParameterException("The following " + pluralize(this.m_requiredFields.size(), "option is required: ", "options are required: ") + missingFields);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  326 */     if (this.m_mainParameterDescription != null && 
/*  327 */       this.m_mainParameterDescription.getParameter().required() && !this.m_mainParameterDescription.isAssigned())
/*      */     {
/*  329 */       throw new ParameterException("Main parameters are required (\"" + this.m_mainParameterDescription.getDescription() + "\")");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static String pluralize(int quantity, String singular, String plural) {
/*  336 */     return (quantity == 1) ? singular : plural;
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
/*      */   private String[] expandArgs(String[] originalArgv) {
/*  348 */     List<String> vResult1 = Lists.newArrayList();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  353 */     for (String arg : originalArgv) {
/*      */       
/*  355 */       if (arg.startsWith("@")) {
/*  356 */         String fileName = arg.substring(1);
/*  357 */         vResult1.addAll(readFile(fileName));
/*      */       } else {
/*      */         
/*  360 */         List<String> expanded = expandDynamicArg(arg);
/*  361 */         vResult1.addAll(expanded);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  367 */     List<String> vResult2 = Lists.newArrayList();
/*  368 */     for (int i = 0; i < vResult1.size(); i++) {
/*  369 */       String arg = vResult1.get(i);
/*  370 */       String[] v1 = vResult1.<String>toArray(new String[0]);
/*  371 */       if (isOption(v1, arg)) {
/*  372 */         String sep = getSeparatorFor(v1, arg);
/*  373 */         if (!" ".equals(sep)) {
/*  374 */           String[] sp = arg.split("[" + sep + "]", 2);
/*  375 */           for (String ssp : sp) {
/*  376 */             vResult2.add(ssp);
/*      */           }
/*      */         } else {
/*  379 */           vResult2.add(arg);
/*      */         } 
/*      */       } else {
/*  382 */         vResult2.add(arg);
/*      */       } 
/*      */     } 
/*      */     
/*  386 */     return vResult2.<String>toArray(new String[vResult2.size()]);
/*      */   }
/*      */   
/*      */   private List<String> expandDynamicArg(String arg) {
/*  390 */     for (ParameterDescription pd : this.m_descriptions.values()) {
/*  391 */       if (pd.isDynamicParameter()) {
/*  392 */         for (String name : pd.getParameter().names()) {
/*  393 */           if (arg.startsWith(name) && !arg.equals(name)) {
/*  394 */             return Arrays.asList(new String[] { name, arg.substring(name.length()) });
/*      */           }
/*      */         } 
/*      */       }
/*      */     } 
/*      */     
/*  400 */     return Arrays.asList(new String[] { arg });
/*      */   }
/*      */   
/*      */   private boolean isOption(String[] args, String arg) {
/*  404 */     String prefixes = getOptionPrefixes(args, arg);
/*  405 */     return (arg.length() > 0 && prefixes.indexOf(arg.charAt(0)) >= 0);
/*      */   }
/*      */   
/*      */   private ParameterDescription getPrefixDescriptionFor(String arg) {
/*  409 */     for (Map.Entry<FuzzyMap.IKey, ParameterDescription> es : this.m_descriptions.entrySet()) {
/*  410 */       if (arg.startsWith(((FuzzyMap.IKey)es.getKey()).getName())) return es.getValue();
/*      */     
/*      */     } 
/*  413 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ParameterDescription getDescriptionFor(String[] args, String arg) {
/*  421 */     ParameterDescription result = getPrefixDescriptionFor(arg);
/*  422 */     if (result != null) return result;
/*      */     
/*  424 */     for (String a : args) {
/*  425 */       ParameterDescription pd = getPrefixDescriptionFor(arg);
/*  426 */       if (pd != null) result = pd; 
/*  427 */       if (a.equals(arg)) return result;
/*      */     
/*      */     } 
/*  430 */     throw new ParameterException("Unknown parameter: " + arg);
/*      */   }
/*      */   
/*      */   private String getSeparatorFor(String[] args, String arg) {
/*  434 */     ParameterDescription pd = getDescriptionFor(args, arg);
/*      */ 
/*      */     
/*  437 */     if (pd != null) {
/*  438 */       Parameters p = pd.getObject().getClass().<Parameters>getAnnotation(Parameters.class);
/*  439 */       if (p != null) return p.separators();
/*      */     
/*      */     } 
/*  442 */     return " ";
/*      */   }
/*      */   
/*      */   private String getOptionPrefixes(String[] args, String arg) {
/*  446 */     ParameterDescription pd = getDescriptionFor(args, arg);
/*      */ 
/*      */     
/*  449 */     if (pd != null) {
/*  450 */       Parameters p = pd.getObject().getClass().<Parameters>getAnnotation(Parameters.class);
/*      */       
/*  452 */       if (p != null) return p.optionPrefixes(); 
/*      */     } 
/*  454 */     String result = "-";
/*      */ 
/*      */     
/*  457 */     StringBuilder sb = new StringBuilder();
/*  458 */     for (Object o : this.m_objects) {
/*  459 */       Parameters p = o.getClass().<Parameters>getAnnotation(Parameters.class);
/*  460 */       if (p != null && !"-".equals(p.optionPrefixes())) {
/*  461 */         sb.append(p.optionPrefixes());
/*      */       }
/*      */     } 
/*      */     
/*  465 */     if (!Strings.isStringEmpty(sb.toString())) {
/*  466 */       result = sb.toString();
/*      */     }
/*      */     
/*  469 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static List<String> readFile(String fileName) {
/*  480 */     List<String> result = Lists.newArrayList();
/*      */     
/*      */     try {
/*  483 */       BufferedReader bufRead = new BufferedReader(new FileReader(fileName));
/*      */ 
/*      */       
/*      */       String line;
/*      */       
/*  488 */       while ((line = bufRead.readLine()) != null) {
/*      */         
/*  490 */         if (line.length() > 0 && !line.trim().startsWith("#")) {
/*  491 */           result.add(line);
/*      */         }
/*      */       } 
/*      */       
/*  495 */       bufRead.close();
/*      */     }
/*  497 */     catch (IOException e) {
/*  498 */       throw new ParameterException("Could not read file " + fileName + ": " + e);
/*      */     } 
/*      */     
/*  501 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String trim(String string) {
/*  508 */     String result = string.trim();
/*  509 */     if (result.startsWith("\"") && result.endsWith("\"") && result.length() > 1) {
/*  510 */       result = result.substring(1, result.length() - 1);
/*      */     }
/*  512 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void createDescriptions() {
/*  519 */     this.m_descriptions = Maps.newHashMap();
/*      */     
/*  521 */     for (Object object : this.m_objects) {
/*  522 */       addDescription(object);
/*      */     }
/*      */   }
/*      */   
/*      */   private void addDescription(Object object) {
/*  527 */     Class<?> cls = object.getClass();
/*      */     
/*  529 */     List<Parameterized> parameterizeds = Parameterized.parseArg(object);
/*  530 */     for (Parameterized parameterized : parameterizeds) {
/*  531 */       WrappedParameter wp = parameterized.getWrappedParameter();
/*  532 */       if (wp != null && wp.getParameter() != null) {
/*  533 */         Parameter annotation = wp.getParameter();
/*      */ 
/*      */ 
/*      */         
/*  537 */         Parameter p = annotation;
/*  538 */         if ((p.names()).length == 0) {
/*  539 */           p("Found main parameter:" + parameterized);
/*  540 */           if (this.m_mainParameter != null) {
/*  541 */             throw new ParameterException("Only one @Parameter with no names attribute is allowed, found:" + this.m_mainParameter + " and " + parameterized);
/*      */           }
/*      */           
/*  544 */           this.m_mainParameter = parameterized;
/*  545 */           this.m_mainParameterObject = object;
/*  546 */           this.m_mainParameterAnnotation = p;
/*  547 */           this.m_mainParameterDescription = new ParameterDescription(object, p, parameterized, this.m_bundle, this);
/*      */           continue;
/*      */         } 
/*  550 */         ParameterDescription pd = new ParameterDescription(object, p, parameterized, this.m_bundle, this);
/*      */         
/*  552 */         for (String name : p.names()) {
/*  553 */           if (this.m_descriptions.containsKey(new StringKey(name))) {
/*  554 */             throw new ParameterException("Found the option " + name + " multiple times");
/*      */           }
/*  556 */           p("Adding description for " + name);
/*  557 */           this.m_fields.put(parameterized, pd);
/*  558 */           this.m_descriptions.put(new StringKey(name), pd);
/*      */           
/*  560 */           if (p.required()) this.m_requiredFields.put(parameterized, pd); 
/*      */         }  continue;
/*      */       } 
/*  563 */       if (parameterized.getDelegateAnnotation() != null) {
/*      */ 
/*      */ 
/*      */         
/*  567 */         Object delegateObject = parameterized.get(object);
/*  568 */         if (delegateObject == null) {
/*  569 */           throw new ParameterException("Delegate field '" + parameterized.getName() + "' cannot be null.");
/*      */         }
/*      */         
/*  572 */         addDescription(delegateObject); continue;
/*  573 */       }  if (wp != null && wp.getDynamicParameter() != null) {
/*      */ 
/*      */ 
/*      */         
/*  577 */         DynamicParameter dp = wp.getDynamicParameter();
/*  578 */         for (String name : dp.names()) {
/*  579 */           if (this.m_descriptions.containsKey(name)) {
/*  580 */             throw new ParameterException("Found the option " + name + " multiple times");
/*      */           }
/*  582 */           p("Adding description for " + name);
/*  583 */           ParameterDescription pd = new ParameterDescription(object, dp, parameterized, this.m_bundle, this);
/*      */           
/*  585 */           this.m_fields.put(parameterized, pd);
/*  586 */           this.m_descriptions.put(new StringKey(name), pd);
/*      */           
/*  588 */           if (dp.required()) this.m_requiredFields.put(parameterized, pd);
/*      */         
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void initializeDefaultValue(ParameterDescription pd) {
/*  664 */     for (String optionName : pd.getParameter().names()) {
/*  665 */       String def = this.m_defaultProvider.getDefaultValueFor(optionName);
/*  666 */       if (def != null) {
/*  667 */         p("Initializing " + optionName + " with default value:" + def);
/*  668 */         pd.addValue(def, true);
/*      */         return;
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
/*      */   private void parseValues(String[] args, boolean validate) {
/*  681 */     boolean commandParsed = false;
/*  682 */     int i = 0;
/*  683 */     boolean isDashDash = false;
/*  684 */     while (i < args.length && !commandParsed) {
/*  685 */       String arg = args[i];
/*  686 */       String a = trim(arg);
/*  687 */       args[i] = a;
/*  688 */       p("Parsing arg: " + a);
/*      */       
/*  690 */       JCommander jc = findCommandByAlias(arg);
/*  691 */       int increment = 1;
/*  692 */       if (!isDashDash && !"--".equals(a) && isOption(args, a) && jc == null) {
/*      */ 
/*      */ 
/*      */         
/*  696 */         ParameterDescription pd = findParameterDescription(a);
/*      */         
/*  698 */         if (pd != null) {
/*  699 */           if (pd.getParameter().password())
/*      */           {
/*      */ 
/*      */             
/*  703 */             char[] password = readPassword(pd.getDescription(), pd.getParameter().echoInput());
/*  704 */             pd.addValue(new String(password));
/*  705 */             this.m_requiredFields.remove(pd.getParameterized());
/*      */           }
/*  707 */           else if (pd.getParameter().variableArity())
/*      */           {
/*      */ 
/*      */             
/*  711 */             increment = processVariableArity(args, i, pd);
/*      */           
/*      */           }
/*      */           else
/*      */           {
/*  716 */             Class<?> fieldType = pd.getParameterized().getType();
/*      */ 
/*      */ 
/*      */             
/*  720 */             if ((fieldType == boolean.class || fieldType == Boolean.class) && pd.getParameter().arity() == -1) {
/*      */               
/*  722 */               pd.addValue("true");
/*  723 */               this.m_requiredFields.remove(pd.getParameterized());
/*      */             } else {
/*  725 */               increment = processFixedArity(args, i, pd, fieldType);
/*      */             } 
/*      */             
/*  728 */             if (pd.isHelp()) {
/*  729 */               this.m_helpWasSpecified = true;
/*      */             }
/*      */           }
/*      */         
/*      */         }
/*  734 */         else if (this.m_acceptUnknownOptions) {
/*  735 */           this.m_unknownArgs.add(arg);
/*  736 */           i++;
/*  737 */           while (i < args.length && !isOption(args, args[i])) {
/*  738 */             this.m_unknownArgs.add(args[i++]);
/*      */           }
/*  740 */           increment = 0;
/*      */         } else {
/*  742 */           throw new ParameterException("Unknown option: " + arg);
/*      */ 
/*      */         
/*      */         }
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*  750 */       else if (!Strings.isStringEmpty(arg)) {
/*  751 */         if ("--".equals(arg)) {
/*  752 */           isDashDash = true;
/*  753 */           a = trim(args[++i]);
/*      */         } 
/*  755 */         if (this.m_commands.isEmpty()) {
/*      */ 
/*      */ 
/*      */           
/*  759 */           List<?> mp = getMainParameter(arg);
/*  760 */           String value = a;
/*  761 */           Object convertedValue = value;
/*      */           
/*  763 */           if (this.m_mainParameter.getGenericType() instanceof ParameterizedType) {
/*  764 */             ParameterizedType p = (ParameterizedType)this.m_mainParameter.getGenericType();
/*  765 */             Type cls = p.getActualTypeArguments()[0];
/*  766 */             if (cls instanceof Class) {
/*  767 */               convertedValue = convertValue(this.m_mainParameter, (Class)cls, value);
/*      */             }
/*      */           } 
/*      */           
/*  771 */           ParameterDescription.validateParameter(this.m_mainParameterDescription, this.m_mainParameterAnnotation.validateWith(), "Default", value);
/*      */ 
/*      */ 
/*      */           
/*  775 */           this.m_mainParameterDescription.setAssigned(true);
/*  776 */           mp.add(convertedValue);
/*      */         
/*      */         }
/*      */         else {
/*      */ 
/*      */           
/*  782 */           if (jc == null && validate)
/*  783 */             throw new MissingCommandException("Expected a command, got " + arg); 
/*  784 */           if (jc != null) {
/*  785 */             this.m_parsedCommand = jc.m_programName.m_name;
/*  786 */             this.m_parsedAlias = arg;
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  791 */             jc.parse(subArray(args, i + 1));
/*  792 */             commandParsed = true;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/*  797 */       i += increment;
/*      */     } 
/*      */ 
/*      */     
/*  801 */     for (ParameterDescription parameterDescription : this.m_descriptions.values()) {
/*  802 */       if (parameterDescription.isAssigned())
/*  803 */         ((ParameterDescription)this.m_fields.get(parameterDescription.getParameterized())).setAssigned(true); 
/*      */     } 
/*      */   }
/*      */   
/*      */   private class DefaultVariableArity
/*      */     implements IVariableArity
/*      */   {
/*      */     private DefaultVariableArity() {}
/*      */     
/*      */     public int processVariableArity(String optionName, String[] options) {
/*  813 */       int i = 0;
/*  814 */       while (i < options.length && !JCommander.this.isOption(options, options[i])) {
/*  815 */         i++;
/*      */       }
/*  817 */       return i;
/*      */     }
/*      */   }
/*  820 */   public JCommander() { this.DEFAULT_VARIABLE_ARITY = new DefaultVariableArity();
/*      */     
/*  822 */     this.m_verbose = 0;
/*      */     
/*  824 */     this.m_caseSensitiveOptions = true;
/*  825 */     this.m_allowAbbreviatedOptions = false; } public JCommander(Object object) { this.DEFAULT_VARIABLE_ARITY = new DefaultVariableArity(); this.m_verbose = 0; this.m_caseSensitiveOptions = true; this.m_allowAbbreviatedOptions = false; addObject(object); createDescriptions(); } public JCommander(Object object, @Nullable ResourceBundle bundle) { this.DEFAULT_VARIABLE_ARITY = new DefaultVariableArity(); this.m_verbose = 0; this.m_caseSensitiveOptions = true; this.m_allowAbbreviatedOptions = false; addObject(object); setDescriptionsBundle(bundle); } public JCommander(Object object, ResourceBundle bundle, String... args) { this.DEFAULT_VARIABLE_ARITY = new DefaultVariableArity(); this.m_verbose = 0; this.m_caseSensitiveOptions = true; this.m_allowAbbreviatedOptions = false; addObject(object); setDescriptionsBundle(bundle); parse(args); } public JCommander(Object object, String... args) { this.DEFAULT_VARIABLE_ARITY = new DefaultVariableArity(); this.m_verbose = 0; this.m_caseSensitiveOptions = true; this.m_allowAbbreviatedOptions = false;
/*      */     addObject(object);
/*      */     parse(args); }
/*      */   
/*      */   private int processVariableArity(String[] args, int index, ParameterDescription pd) {
/*      */     IVariableArity va;
/*  831 */     Object arg = pd.getObject();
/*      */     
/*  833 */     if (!(arg instanceof IVariableArity)) {
/*  834 */       va = this.DEFAULT_VARIABLE_ARITY;
/*      */     } else {
/*  836 */       va = (IVariableArity)arg;
/*      */     } 
/*      */     
/*  839 */     List<String> currentArgs = Lists.newArrayList();
/*  840 */     for (int j = index + 1; j < args.length; j++) {
/*  841 */       currentArgs.add(args[j]);
/*      */     }
/*  843 */     int arity = va.processVariableArity(pd.getParameter().names()[0], currentArgs.<String>toArray(new String[0]));
/*      */ 
/*      */     
/*  846 */     int result = processFixedArity(args, index, pd, List.class, arity);
/*  847 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int processFixedArity(String[] args, int index, ParameterDescription pd, Class<?> fieldType) {
/*  854 */     int arity = pd.getParameter().arity();
/*  855 */     int n = (arity != -1) ? arity : 1;
/*      */     
/*  857 */     return processFixedArity(args, index, pd, fieldType, n);
/*      */   }
/*      */ 
/*      */   
/*      */   private int processFixedArity(String[] args, int originalIndex, ParameterDescription pd, Class<?> fieldType, int arity) {
/*  862 */     int index = originalIndex;
/*  863 */     String arg = args[index];
/*      */     
/*  865 */     if (arity == 0 && (Boolean.class.isAssignableFrom(fieldType) || boolean.class.isAssignableFrom(fieldType))) {
/*      */ 
/*      */       
/*  868 */       pd.addValue("true");
/*  869 */       this.m_requiredFields.remove(pd.getParameterized());
/*  870 */     } else if (index < args.length - 1) {
/*  871 */       int offset = "--".equals(args[index + 1]) ? 1 : 0;
/*      */       
/*  873 */       if (index + arity < args.length) {
/*  874 */         for (int j = 1; j <= arity; j++) {
/*  875 */           pd.addValue(trim(args[index + j + offset]));
/*  876 */           this.m_requiredFields.remove(pd.getParameterized());
/*      */         } 
/*  878 */         index += arity + offset;
/*      */       } else {
/*  880 */         throw new ParameterException("Expected " + arity + " values after " + arg);
/*      */       } 
/*      */     } else {
/*  883 */       throw new ParameterException("Expected a value after parameter " + arg);
/*      */     } 
/*      */     
/*  886 */     return arity + 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private char[] readPassword(String description, boolean echoInput) {
/*  894 */     getConsole().print(description + ": ");
/*  895 */     return getConsole().readPassword(echoInput);
/*      */   }
/*      */   
/*      */   private String[] subArray(String[] args, int index) {
/*  899 */     int l = args.length - index;
/*  900 */     String[] result = new String[l];
/*  901 */     System.arraycopy(args, index, result, 0, l);
/*      */     
/*  903 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private List<?> getMainParameter(String arg) {
/*  913 */     if (this.m_mainParameter == null) {
/*  914 */       throw new ParameterException("Was passed main parameter '" + arg + "' but no main parameter was defined");
/*      */     }
/*      */ 
/*      */     
/*  918 */     List<?> result = (List)this.m_mainParameter.get(this.m_mainParameterObject);
/*  919 */     if (result == null) {
/*  920 */       result = Lists.newArrayList();
/*  921 */       if (!List.class.isAssignableFrom(this.m_mainParameter.getType())) {
/*  922 */         throw new ParameterException("Main parameter field " + this.m_mainParameter + " needs to be of type List, not " + this.m_mainParameter.getType());
/*      */       }
/*      */       
/*  925 */       this.m_mainParameter.set(this.m_mainParameterObject, result);
/*      */     } 
/*  927 */     if (this.m_firstTimeMainParameter) {
/*  928 */       result.clear();
/*  929 */       this.m_firstTimeMainParameter = false;
/*      */     } 
/*  931 */     return result;
/*      */   }
/*      */   
/*      */   public String getMainParameterDescription() {
/*  935 */     if (this.m_descriptions == null) createDescriptions(); 
/*  936 */     return (this.m_mainParameterAnnotation != null) ? this.m_mainParameterAnnotation.description() : null;
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
/*      */   public void setProgramName(String name) {
/*  954 */     setProgramName(name, new String[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setProgramName(String name, String... aliases) {
/*  964 */     this.m_programName = new ProgramName(name, Arrays.asList(aliases));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void usage(String commandName) {
/*  971 */     StringBuilder sb = new StringBuilder();
/*  972 */     usage(commandName, sb);
/*  973 */     getConsole().println(sb.toString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void usage(String commandName, StringBuilder out) {
/*  980 */     usage(commandName, out, "");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void usage(String commandName, StringBuilder out, String indent) {
/*  988 */     String description = getCommandDescription(commandName);
/*  989 */     JCommander jc = findCommandByAlias(commandName);
/*  990 */     if (description != null) {
/*  991 */       out.append(indent).append(description);
/*  992 */       out.append("\n");
/*      */     } 
/*  994 */     jc.usage(out, indent);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getCommandDescription(String commandName) {
/* 1001 */     JCommander jc = findCommandByAlias(commandName);
/* 1002 */     if (jc == null) {
/* 1003 */       throw new ParameterException("Asking description for unknown command: " + commandName);
/*      */     }
/*      */     
/* 1006 */     Object arg = jc.getObjects().get(0);
/* 1007 */     Parameters p = arg.getClass().<Parameters>getAnnotation(Parameters.class);
/* 1008 */     ResourceBundle bundle = null;
/* 1009 */     String result = null;
/* 1010 */     if (p != null) {
/* 1011 */       result = p.commandDescription();
/* 1012 */       String bundleName = p.resourceBundle();
/* 1013 */       if (!"".equals(bundleName)) {
/* 1014 */         bundle = ResourceBundle.getBundle(bundleName, Locale.getDefault());
/*      */       } else {
/* 1016 */         bundle = this.m_bundle;
/*      */       } 
/*      */       
/* 1019 */       if (bundle != null) {
/* 1020 */         result = getI18nString(bundle, p.commandDescriptionKey(), p.commandDescription());
/*      */       }
/*      */     } 
/*      */     
/* 1024 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String getI18nString(ResourceBundle bundle, String key, String def) {
/* 1032 */     String s = (bundle != null) ? bundle.getString(key) : null;
/* 1033 */     return (s != null) ? s : def;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void usage() {
/* 1040 */     StringBuilder sb = new StringBuilder();
/* 1041 */     usage(sb);
/* 1042 */     getConsole().println(sb.toString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void usage(StringBuilder out) {
/* 1049 */     usage(out, "");
/*      */   }
/*      */   
/*      */   public void usage(StringBuilder out, String indent) {
/* 1053 */     if (this.m_descriptions == null) createDescriptions(); 
/* 1054 */     boolean hasCommands = !this.m_commands.isEmpty();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1059 */     String programName = (this.m_programName != null) ? this.m_programName.getDisplayName() : "<main class>";
/* 1060 */     out.append(indent).append("Usage: " + programName + " [options]");
/* 1061 */     if (hasCommands) out.append(indent).append(" [command] [command options]"); 
/* 1062 */     if (this.m_mainParameterDescription != null) {
/* 1063 */       out.append(" " + this.m_mainParameterDescription.getDescription());
/*      */     }
/* 1065 */     out.append("\n");
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1070 */     int longestName = 0;
/* 1071 */     List<ParameterDescription> sorted = Lists.newArrayList();
/* 1072 */     for (ParameterDescription pd : this.m_fields.values()) {
/* 1073 */       if (!pd.getParameter().hidden()) {
/* 1074 */         sorted.add(pd);
/*      */         
/* 1076 */         int length = pd.getNames().length() + 2;
/* 1077 */         if (length > longestName) {
/* 1078 */           longestName = length;
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1086 */     Collections.sort(sorted, getParameterDescriptionComparator());
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1091 */     int descriptionIndent = 6;
/* 1092 */     if (sorted.size() > 0) out.append(indent).append("  Options:\n"); 
/* 1093 */     for (ParameterDescription pd : sorted) {
/* 1094 */       WrappedParameter parameter = pd.getParameter();
/* 1095 */       out.append(indent).append("  " + (parameter.required() ? "* " : "  ") + pd.getNames() + "\n" + indent + s(descriptionIndent));
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1100 */       int indentCount = indent.length() + descriptionIndent;
/* 1101 */       wrapDescription(out, indentCount, pd.getDescription());
/* 1102 */       Object def = pd.getDefault();
/* 1103 */       if (pd.isDynamicParameter()) {
/* 1104 */         out.append("\n" + s(indentCount + 1)).append("Syntax: " + parameter.names()[0] + "key" + parameter.getAssignment() + "value");
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1109 */       if (def != null) {
/* 1110 */         String displayedDef = Strings.isStringEmpty(def.toString()) ? "<empty string>" : def.toString();
/*      */ 
/*      */         
/* 1113 */         out.append("\n" + s(indentCount + 1)).append("Default: " + (parameter.password() ? "********" : displayedDef));
/*      */       } 
/*      */       
/* 1116 */       Class<?> type = pd.getParameterized().getType();
/* 1117 */       if (type.isEnum()) {
/* 1118 */         out.append("\n" + s(indentCount + 1)).append("Possible Values: " + EnumSet.allOf(type));
/*      */       }
/*      */       
/* 1121 */       out.append("\n");
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1127 */     if (hasCommands) {
/* 1128 */       out.append("  Commands:\n");
/*      */ 
/*      */       
/* 1131 */       for (Map.Entry<ProgramName, JCommander> commands : this.m_commands.entrySet()) {
/* 1132 */         Object arg = ((JCommander)commands.getValue()).getObjects().get(0);
/* 1133 */         Parameters p = arg.getClass().<Parameters>getAnnotation(Parameters.class);
/* 1134 */         if (!p.hidden()) {
/* 1135 */           ProgramName progName = commands.getKey();
/* 1136 */           String dispName = progName.getDisplayName();
/* 1137 */           out.append(indent).append("    " + dispName);
/*      */ 
/*      */           
/* 1140 */           usage(progName.getName(), out, "      ");
/* 1141 */           out.append("\n");
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private Comparator<? super ParameterDescription> getParameterDescriptionComparator() {
/* 1148 */     return this.m_parameterDescriptionComparator;
/*      */   }
/*      */   
/*      */   public void setParameterDescriptionComparator(Comparator<? super ParameterDescription> c) {
/* 1152 */     this.m_parameterDescriptionComparator = c;
/*      */   }
/*      */   
/*      */   public void setColumnSize(int columnSize) {
/* 1156 */     this.m_columnSize = columnSize;
/*      */   }
/*      */   
/*      */   public int getColumnSize() {
/* 1160 */     return this.m_columnSize;
/*      */   }
/*      */   
/*      */   private void wrapDescription(StringBuilder out, int indent, String description) {
/* 1164 */     int max = getColumnSize();
/* 1165 */     String[] words = description.split(" ");
/* 1166 */     int current = indent;
/* 1167 */     int i = 0;
/* 1168 */     while (i < words.length) {
/* 1169 */       String word = words[i];
/* 1170 */       if (word.length() > max || current + word.length() <= max) {
/* 1171 */         out.append(" ").append(word);
/* 1172 */         current += word.length() + 1;
/*      */       } else {
/* 1174 */         out.append("\n").append(s(indent + 1)).append(word);
/* 1175 */         current = indent;
/*      */       } 
/* 1177 */       i++;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<ParameterDescription> getParameters() {
/* 1187 */     return new ArrayList<ParameterDescription>(this.m_fields.values());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ParameterDescription getMainParameter() {
/* 1194 */     return this.m_mainParameterDescription;
/*      */   }
/*      */   
/*      */   private void p(String string) {
/* 1198 */     if (this.m_verbose > 0 || System.getProperty("jcommander.debug") != null) {
/* 1199 */       getConsole().println("[JCommander] " + string);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDefaultProvider(IDefaultProvider defaultProvider) {
/* 1207 */     this.m_defaultProvider = defaultProvider;
/*      */     
/* 1209 */     for (Map.Entry<ProgramName, JCommander> entry : this.m_commands.entrySet()) {
/* 1210 */       ((JCommander)entry.getValue()).setDefaultProvider(defaultProvider);
/*      */     }
/*      */   }
/*      */   
/*      */   public void addConverterFactory(IStringConverterFactory converterFactory) {
/* 1215 */     CONVERTER_FACTORIES.addFirst(converterFactory);
/*      */   }
/*      */   
/*      */   public <T> Class<? extends IStringConverter<T>> findConverter(Class<T> cls) {
/* 1219 */     for (IStringConverterFactory f : CONVERTER_FACTORIES) {
/* 1220 */       Class<? extends IStringConverter<T>> result = f.getConverter(cls);
/* 1221 */       if (result != null) return result;
/*      */     
/*      */     } 
/* 1224 */     return null;
/*      */   }
/*      */   
/*      */   public Object convertValue(ParameterDescription pd, String value) {
/* 1228 */     return convertValue(pd.getParameterized(), pd.getParameterized().getType(), value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object convertValue(Parameterized parameterized, Class<? extends IStringConverter<?>> type, String value) {
/* 1237 */     Parameter annotation = parameterized.getParameter();
/*      */ 
/*      */     
/* 1240 */     if (annotation == null) return value;
/*      */     
/* 1242 */     Class<? extends IStringConverter<?>> converterClass = annotation.converter();
/* 1243 */     boolean listConverterWasSpecified = (annotation.listConverter() != NoConverter.class);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1248 */     if (converterClass == null || converterClass == NoConverter.class)
/*      */     {
/* 1250 */       if (type.isEnum()) {
/* 1251 */         converterClass = type;
/*      */       } else {
/* 1253 */         converterClass = findConverter(type);
/*      */       } 
/*      */     }
/*      */     
/* 1257 */     if (converterClass == null) {
/* 1258 */       Type elementType = parameterized.findFieldGenericType();
/* 1259 */       converterClass = (elementType != null) ? findConverter((Class)elementType) : (Class)StringConverter.class;
/*      */ 
/*      */ 
/*      */       
/* 1263 */       if (converterClass == null && Enum.class.isAssignableFrom((Class)elementType)) {
/* 1264 */         converterClass = (Class<? extends IStringConverter<?>>)elementType;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1269 */     Object<?> result = null;
/*      */     try {
/* 1271 */       String[] names = annotation.names();
/* 1272 */       String optionName = (names.length > 0) ? names[0] : "[Main class]";
/* 1273 */       if (converterClass != null && converterClass.isEnum()) {
/*      */         try {
/* 1275 */           result = Enum.valueOf(converterClass, value);
/* 1276 */         } catch (IllegalArgumentException e) {
/*      */           try {
/* 1278 */             result = Enum.valueOf(converterClass, value.toUpperCase());
/* 1279 */           } catch (IllegalArgumentException ex) {
/* 1280 */             throw new ParameterException("Invalid value for " + optionName + " parameter. Allowed values:" + EnumSet.allOf(converterClass));
/*      */           }
/*      */         
/* 1283 */         } catch (Exception e) {
/* 1284 */           throw new ParameterException("Invalid value for " + optionName + " parameter. Allowed values:" + EnumSet.allOf(converterClass));
/*      */         } 
/*      */       } else {
/*      */         
/* 1288 */         IStringConverter<?> converter = instantiateConverter(optionName, converterClass);
/* 1289 */         if (type.isAssignableFrom(List.class) && parameterized.getGenericType() instanceof ParameterizedType) {
/*      */ 
/*      */ 
/*      */           
/* 1293 */           if (listConverterWasSpecified) {
/*      */ 
/*      */             
/* 1296 */             IStringConverter<?> listConverter = instantiateConverter(optionName, annotation.listConverter());
/*      */             
/* 1298 */             result = (Object<?>)listConverter.convert(value);
/*      */           }
/*      */           else {
/*      */             
/* 1302 */             result = (Object<?>)convertToList(value, converter, annotation.splitter());
/*      */           } 
/*      */         } else {
/* 1305 */           result = (Object<?>)converter.convert(value);
/*      */         } 
/*      */       } 
/* 1308 */     } catch (InstantiationException e) {
/* 1309 */       throw new ParameterException(e);
/* 1310 */     } catch (IllegalAccessException e) {
/* 1311 */       throw new ParameterException(e);
/* 1312 */     } catch (InvocationTargetException e) {
/* 1313 */       throw new ParameterException(e);
/*      */     } 
/*      */     
/* 1316 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Object convertToList(String value, IStringConverter<?> converter, Class<? extends IParameterSplitter> splitterClass) throws InstantiationException, IllegalAccessException {
/* 1326 */     IParameterSplitter splitter = splitterClass.newInstance();
/* 1327 */     List<Object> result = Lists.newArrayList();
/* 1328 */     for (String param : splitter.split(value)) {
/* 1329 */       result.add(converter.convert(param));
/*      */     }
/* 1331 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private IStringConverter<?> instantiateConverter(String optionName, Class<? extends IStringConverter<?>> converterClass) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
/* 1338 */     Constructor<IStringConverter<?>> ctor = null;
/* 1339 */     Constructor<IStringConverter<?>> stringCtor = null;
/* 1340 */     Constructor[] arrayOfConstructor = (Constructor[])converterClass.getDeclaredConstructors();
/*      */     
/* 1342 */     for (Constructor<IStringConverter<?>> c : arrayOfConstructor) {
/* 1343 */       Class<?>[] types = c.getParameterTypes();
/* 1344 */       if (types.length == 1 && types[0].equals(String.class)) {
/* 1345 */         stringCtor = c;
/* 1346 */       } else if (types.length == 0) {
/* 1347 */         ctor = c;
/*      */       } 
/*      */     } 
/*      */     
/* 1351 */     IStringConverter<?> result = (stringCtor != null) ? stringCtor.newInstance(new Object[] { optionName }) : ((ctor != null) ? ctor.newInstance(new Object[0]) : null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1357 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addCommand(String name, Object object) {
/* 1364 */     addCommand(name, object, new String[0]);
/*      */   }
/*      */   
/*      */   public void addCommand(Object object) {
/* 1368 */     Parameters p = object.getClass().<Parameters>getAnnotation(Parameters.class);
/* 1369 */     if (p != null && (p.commandNames()).length > 0) {
/* 1370 */       for (String commandName : p.commandNames()) {
/* 1371 */         addCommand(commandName, object);
/*      */       }
/*      */     } else {
/* 1374 */       throw new ParameterException("Trying to add command " + object.getClass().getName() + " without specifying its names in @Parameters");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addCommand(String name, Object object, String... aliases) {
/* 1383 */     JCommander jc = new JCommander(object);
/* 1384 */     jc.setProgramName(name, aliases);
/* 1385 */     jc.setDefaultProvider(this.m_defaultProvider);
/* 1386 */     jc.setAcceptUnknownOptions(this.m_acceptUnknownOptions);
/* 1387 */     ProgramName progName = jc.m_programName;
/* 1388 */     this.m_commands.put(progName, jc);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1397 */     this.aliasMap.put(new StringKey(name), progName);
/* 1398 */     for (String a : aliases) {
/* 1399 */       FuzzyMap.IKey alias = new StringKey(a);
/*      */       
/* 1401 */       if (!alias.equals(name)) {
/* 1402 */         ProgramName mappedName = this.aliasMap.get(alias);
/* 1403 */         if (mappedName != null && !mappedName.equals(progName)) {
/* 1404 */           throw new ParameterException("Cannot set alias " + alias + " for " + name + " command because it has already been defined for " + mappedName.m_name + " command");
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 1409 */         this.aliasMap.put(alias, progName);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public Map<String, JCommander> getCommands() {
/* 1415 */     Map<String, JCommander> res = Maps.newLinkedHashMap();
/* 1416 */     for (Map.Entry<ProgramName, JCommander> entry : this.m_commands.entrySet()) {
/* 1417 */       res.put((entry.getKey()).m_name, entry.getValue());
/*      */     }
/* 1419 */     return res;
/*      */   }
/*      */   
/*      */   public String getParsedCommand() {
/* 1423 */     return this.m_parsedCommand;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getParsedAlias() {
/* 1434 */     return this.m_parsedAlias;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String s(int count) {
/* 1441 */     StringBuilder result = new StringBuilder();
/* 1442 */     for (int i = 0; i < count; i++) {
/* 1443 */       result.append(" ");
/*      */     }
/*      */     
/* 1446 */     return result.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<Object> getObjects() {
/* 1454 */     return this.m_objects;
/*      */   }
/*      */   
/*      */   private ParameterDescription findParameterDescription(String arg) {
/* 1458 */     return FuzzyMap.<ParameterDescription>findInMap(this.m_descriptions, new StringKey(arg), this.m_caseSensitiveOptions, this.m_allowAbbreviatedOptions);
/*      */   }
/*      */ 
/*      */   
/*      */   private JCommander findCommand(ProgramName name) {
/* 1463 */     return FuzzyMap.<JCommander>findInMap((Map)this.m_commands, name, this.m_caseSensitiveOptions, this.m_allowAbbreviatedOptions);
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
/*      */   private ProgramName findProgramName(String name) {
/* 1478 */     return FuzzyMap.<ProgramName>findInMap(this.aliasMap, new StringKey(name), this.m_caseSensitiveOptions, this.m_allowAbbreviatedOptions);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private JCommander findCommandByAlias(String commandOrAlias) {
/* 1486 */     ProgramName progName = findProgramName(commandOrAlias);
/* 1487 */     if (progName == null) {
/* 1488 */       return null;
/*      */     }
/* 1490 */     JCommander jc = findCommand(progName);
/* 1491 */     if (jc == null) {
/* 1492 */       throw new IllegalStateException("There appears to be inconsistency in the internal command database.  This is likely a bug. Please report.");
/*      */     }
/*      */ 
/*      */     
/* 1496 */     return jc;
/*      */   }
/*      */ 
/*      */   
/*      */   private static final class ProgramName
/*      */     implements FuzzyMap.IKey
/*      */   {
/*      */     private final String m_name;
/*      */     private final List<String> m_aliases;
/*      */     
/*      */     ProgramName(String name, List<String> aliases) {
/* 1507 */       this.m_name = name;
/* 1508 */       this.m_aliases = aliases;
/*      */     }
/*      */ 
/*      */     
/*      */     public String getName() {
/* 1513 */       return this.m_name;
/*      */     }
/*      */     
/*      */     private String getDisplayName() {
/* 1517 */       StringBuilder sb = new StringBuilder();
/* 1518 */       sb.append(this.m_name);
/* 1519 */       if (!this.m_aliases.isEmpty()) {
/* 1520 */         sb.append("(");
/* 1521 */         Iterator<String> aliasesIt = this.m_aliases.iterator();
/* 1522 */         while (aliasesIt.hasNext()) {
/* 1523 */           sb.append(aliasesIt.next());
/* 1524 */           if (aliasesIt.hasNext()) {
/* 1525 */             sb.append(",");
/*      */           }
/*      */         } 
/* 1528 */         sb.append(")");
/*      */       } 
/* 1530 */       return sb.toString();
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 1535 */       int prime = 31;
/* 1536 */       int result = 1;
/* 1537 */       result = 31 * result + ((this.m_name == null) ? 0 : this.m_name.hashCode());
/* 1538 */       return result;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 1543 */       if (this == obj)
/* 1544 */         return true; 
/* 1545 */       if (obj == null)
/* 1546 */         return false; 
/* 1547 */       if (getClass() != obj.getClass())
/* 1548 */         return false; 
/* 1549 */       ProgramName other = (ProgramName)obj;
/* 1550 */       if (this.m_name == null) {
/* 1551 */         if (other.m_name != null)
/* 1552 */           return false; 
/* 1553 */       } else if (!this.m_name.equals(other.m_name)) {
/* 1554 */         return false;
/* 1555 */       }  return true;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String toString() {
/* 1564 */       return getDisplayName();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void setVerbose(int verbose) {
/* 1570 */     this.m_verbose = verbose;
/*      */   }
/*      */   
/*      */   public void setCaseSensitiveOptions(boolean b) {
/* 1574 */     this.m_caseSensitiveOptions = b;
/*      */   }
/*      */   
/*      */   public void setAllowAbbreviatedOptions(boolean b) {
/* 1578 */     this.m_allowAbbreviatedOptions = b;
/*      */   }
/*      */   
/*      */   public void setAcceptUnknownOptions(boolean b) {
/* 1582 */     this.m_acceptUnknownOptions = b;
/*      */   }
/*      */   
/*      */   public List<String> getUnknownOptions() {
/* 1586 */     return this.m_unknownArgs;
/*      */   }
/*      */   public void setAllowParameterOverwriting(boolean b) {
/* 1589 */     this.m_allowParameterOverwriting = b;
/*      */   }
/*      */   
/*      */   public boolean isParameterOverwritingAllowed() {
/* 1593 */     return this.m_allowParameterOverwriting;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\JCommander.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */