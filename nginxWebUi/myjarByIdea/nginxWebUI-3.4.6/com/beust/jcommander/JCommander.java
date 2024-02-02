package com.beust.jcommander;

import com.beust.jcommander.converters.IParameterSplitter;
import com.beust.jcommander.converters.NoConverter;
import com.beust.jcommander.converters.StringConverter;
import com.beust.jcommander.internal.Console;
import com.beust.jcommander.internal.DefaultConsole;
import com.beust.jcommander.internal.DefaultConverterFactory;
import com.beust.jcommander.internal.JDK6Console;
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.beust.jcommander.internal.Nullable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class JCommander {
   public static final String DEBUG_PROPERTY = "jcommander.debug";
   private Map<FuzzyMap.IKey, ParameterDescription> m_descriptions;
   private List<Object> m_objects = Lists.newArrayList();
   private boolean m_firstTimeMainParameter = true;
   private Parameterized m_mainParameter = null;
   private Object m_mainParameterObject;
   private Parameter m_mainParameterAnnotation;
   private ParameterDescription m_mainParameterDescription;
   private Map<Parameterized, ParameterDescription> m_requiredFields = Maps.newHashMap();
   private Map<Parameterized, ParameterDescription> m_fields = Maps.newHashMap();
   private java.util.ResourceBundle m_bundle;
   private IDefaultProvider m_defaultProvider;
   private Map<ProgramName, JCommander> m_commands = Maps.newLinkedHashMap();
   private Map<FuzzyMap.IKey, ProgramName> aliasMap = Maps.newLinkedHashMap();
   private String m_parsedCommand;
   private String m_parsedAlias;
   private ProgramName m_programName;
   private Comparator<? super ParameterDescription> m_parameterDescriptionComparator = new Comparator<ParameterDescription>() {
      public int compare(ParameterDescription p0, ParameterDescription p1) {
         return p0.getLongestName().compareTo(p1.getLongestName());
      }
   };
   private int m_columnSize = 79;
   private boolean m_helpWasSpecified;
   private List<String> m_unknownArgs = Lists.newArrayList();
   private boolean m_acceptUnknownOptions = false;
   private boolean m_allowParameterOverwriting = false;
   private static Console m_console;
   private static LinkedList<IStringConverterFactory> CONVERTER_FACTORIES = Lists.newLinkedList();
   private final IVariableArity DEFAULT_VARIABLE_ARITY = new DefaultVariableArity();
   private int m_verbose = 0;
   private boolean m_caseSensitiveOptions = true;
   private boolean m_allowAbbreviatedOptions = false;

   public JCommander() {
   }

   public JCommander(Object object) {
      this.addObject(object);
      this.createDescriptions();
   }

   public JCommander(Object object, @Nullable java.util.ResourceBundle bundle) {
      this.addObject(object);
      this.setDescriptionsBundle(bundle);
   }

   public JCommander(Object object, java.util.ResourceBundle bundle, String... args) {
      this.addObject(object);
      this.setDescriptionsBundle(bundle);
      this.parse(args);
   }

   public JCommander(Object object, String... args) {
      this.addObject(object);
      this.parse(args);
   }

   public static Console getConsole() {
      if (m_console == null) {
         try {
            Method consoleMethod = System.class.getDeclaredMethod("console");
            Object console = consoleMethod.invoke((Object)null);
            m_console = new JDK6Console(console);
         } catch (Throwable var2) {
            m_console = new DefaultConsole();
         }
      }

      return m_console;
   }

   public final void addObject(Object object) {
      if (object instanceof Iterable) {
         Iterator i$ = ((Iterable)object).iterator();

         while(i$.hasNext()) {
            Object o = i$.next();
            this.m_objects.add(o);
         }
      } else if (object.getClass().isArray()) {
         Object[] arr$ = (Object[])((Object[])object);
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Object o = arr$[i$];
            this.m_objects.add(o);
         }
      } else {
         this.m_objects.add(object);
      }

   }

   public final void setDescriptionsBundle(java.util.ResourceBundle bundle) {
      this.m_bundle = bundle;
   }

   public void parse(String... args) {
      this.parse(true, args);
   }

   public void parseWithoutValidation(String... args) {
      this.parse(false, args);
   }

   private void parse(boolean validate, String... args) {
      StringBuilder sb = new StringBuilder("Parsing \"");
      sb.append(this.join(args).append("\"\n  with:").append(this.join(this.m_objects.toArray())));
      this.p(sb.toString());
      if (this.m_descriptions == null) {
         this.createDescriptions();
      }

      this.initializeDefaultValues();
      this.parseValues(this.expandArgs(args), validate);
      if (validate) {
         this.validateOptions();
      }

   }

   private StringBuilder join(Object[] args) {
      StringBuilder result = new StringBuilder();

      for(int i = 0; i < args.length; ++i) {
         if (i > 0) {
            result.append(" ");
         }

         result.append(args[i]);
      }

      return result;
   }

   private void initializeDefaultValues() {
      if (this.m_defaultProvider != null) {
         Iterator i$ = this.m_descriptions.values().iterator();

         while(i$.hasNext()) {
            ParameterDescription pd = (ParameterDescription)i$.next();
            this.initializeDefaultValue(pd);
         }

         i$ = this.m_commands.entrySet().iterator();

         while(i$.hasNext()) {
            Map.Entry<ProgramName, JCommander> entry = (Map.Entry)i$.next();
            ((JCommander)entry.getValue()).initializeDefaultValues();
         }
      }

   }

   private void validateOptions() {
      if (!this.m_helpWasSpecified) {
         if (this.m_requiredFields.isEmpty()) {
            if (this.m_mainParameterDescription != null && this.m_mainParameterDescription.getParameter().required() && !this.m_mainParameterDescription.isAssigned()) {
               throw new ParameterException("Main parameters are required (\"" + this.m_mainParameterDescription.getDescription() + "\")");
            }
         } else {
            StringBuilder missingFields = new StringBuilder();
            Iterator i$ = this.m_requiredFields.values().iterator();

            while(i$.hasNext()) {
               ParameterDescription pd = (ParameterDescription)i$.next();
               missingFields.append(pd.getNames()).append(" ");
            }

            throw new ParameterException("The following " + pluralize(this.m_requiredFields.size(), "option is required: ", "options are required: ") + missingFields);
         }
      }
   }

   private static String pluralize(int quantity, String singular, String plural) {
      return quantity == 1 ? singular : plural;
   }

   private String[] expandArgs(String[] originalArgv) {
      List<String> vResult1 = Lists.newArrayList();
      String[] arr$ = originalArgv;
      int i = originalArgv.length;

      String sep;
      for(int i$ = 0; i$ < i; ++i$) {
         String arg = arr$[i$];
         if (arg.startsWith("@")) {
            sep = arg.substring(1);
            vResult1.addAll(readFile(sep));
         } else {
            List<String> expanded = this.expandDynamicArg(arg);
            vResult1.addAll(expanded);
         }
      }

      List<String> vResult2 = Lists.newArrayList();

      for(i = 0; i < vResult1.size(); ++i) {
         String arg = (String)vResult1.get(i);
         String[] v1 = (String[])vResult1.toArray(new String[0]);
         if (this.isOption(v1, arg)) {
            sep = this.getSeparatorFor(v1, arg);
            if (!" ".equals(sep)) {
               String[] sp = arg.split("[" + sep + "]", 2);
               String[] arr$ = sp;
               int len$ = sp.length;

               for(int i$ = 0; i$ < len$; ++i$) {
                  String ssp = arr$[i$];
                  vResult2.add(ssp);
               }
            } else {
               vResult2.add(arg);
            }
         } else {
            vResult2.add(arg);
         }
      }

      return (String[])vResult2.toArray(new String[vResult2.size()]);
   }

   private List<String> expandDynamicArg(String arg) {
      Iterator i$ = this.m_descriptions.values().iterator();

      while(true) {
         ParameterDescription pd;
         do {
            if (!i$.hasNext()) {
               return Arrays.asList(arg);
            }

            pd = (ParameterDescription)i$.next();
         } while(!pd.isDynamicParameter());

         String[] arr$ = pd.getParameter().names();
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            String name = arr$[i$];
            if (arg.startsWith(name) && !arg.equals(name)) {
               return Arrays.asList(name, arg.substring(name.length()));
            }
         }
      }
   }

   private boolean isOption(String[] args, String arg) {
      String prefixes = this.getOptionPrefixes(args, arg);
      return arg.length() > 0 && prefixes.indexOf(arg.charAt(0)) >= 0;
   }

   private ParameterDescription getPrefixDescriptionFor(String arg) {
      Iterator i$ = this.m_descriptions.entrySet().iterator();

      Map.Entry es;
      do {
         if (!i$.hasNext()) {
            return null;
         }

         es = (Map.Entry)i$.next();
      } while(!arg.startsWith(((FuzzyMap.IKey)es.getKey()).getName()));

      return (ParameterDescription)es.getValue();
   }

   private ParameterDescription getDescriptionFor(String[] args, String arg) {
      ParameterDescription result = this.getPrefixDescriptionFor(arg);
      if (result != null) {
         return result;
      } else {
         String[] arr$ = args;
         int len$ = args.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            String a = arr$[i$];
            ParameterDescription pd = this.getPrefixDescriptionFor(arg);
            if (pd != null) {
               result = pd;
            }

            if (a.equals(arg)) {
               return result;
            }
         }

         throw new ParameterException("Unknown parameter: " + arg);
      }
   }

   private String getSeparatorFor(String[] args, String arg) {
      ParameterDescription pd = this.getDescriptionFor(args, arg);
      if (pd != null) {
         Parameters p = (Parameters)pd.getObject().getClass().getAnnotation(Parameters.class);
         if (p != null) {
            return p.separators();
         }
      }

      return " ";
   }

   private String getOptionPrefixes(String[] args, String arg) {
      ParameterDescription pd = this.getDescriptionFor(args, arg);
      if (pd != null) {
         Parameters p = (Parameters)pd.getObject().getClass().getAnnotation(Parameters.class);
         if (p != null) {
            return p.optionPrefixes();
         }
      }

      String result = "-";
      StringBuilder sb = new StringBuilder();
      Iterator i$ = this.m_objects.iterator();

      while(i$.hasNext()) {
         Object o = i$.next();
         Parameters p = (Parameters)o.getClass().getAnnotation(Parameters.class);
         if (p != null && !"-".equals(p.optionPrefixes())) {
            sb.append(p.optionPrefixes());
         }
      }

      if (!Strings.isStringEmpty(sb.toString())) {
         result = sb.toString();
      }

      return result;
   }

   private static List<String> readFile(String fileName) {
      List<String> result = Lists.newArrayList();

      try {
         BufferedReader bufRead = new BufferedReader(new FileReader(fileName));

         String line;
         while((line = bufRead.readLine()) != null) {
            if (line.length() > 0 && !line.trim().startsWith("#")) {
               result.add(line);
            }
         }

         bufRead.close();
         return result;
      } catch (IOException var4) {
         throw new ParameterException("Could not read file " + fileName + ": " + var4);
      }
   }

   private static String trim(String string) {
      String result = string.trim();
      if (result.startsWith("\"") && result.endsWith("\"") && result.length() > 1) {
         result = result.substring(1, result.length() - 1);
      }

      return result;
   }

   private void createDescriptions() {
      this.m_descriptions = Maps.newHashMap();
      Iterator i$ = this.m_objects.iterator();

      while(i$.hasNext()) {
         Object object = i$.next();
         this.addDescription(object);
      }

   }

   private void addDescription(Object object) {
      Class<?> cls = object.getClass();
      List<Parameterized> parameterizeds = Parameterized.parseArg(object);
      Iterator i$ = parameterizeds.iterator();

      while(true) {
         while(true) {
            while(i$.hasNext()) {
               Parameterized parameterized = (Parameterized)i$.next();
               WrappedParameter wp = parameterized.getWrappedParameter();
               if (wp != null && wp.getParameter() != null) {
                  Parameter annotation = wp.getParameter();
                  Parameter p = annotation;
                  if (annotation.names().length == 0) {
                     this.p("Found main parameter:" + parameterized);
                     if (this.m_mainParameter != null) {
                        throw new ParameterException("Only one @Parameter with no names attribute is allowed, found:" + this.m_mainParameter + " and " + parameterized);
                     }

                     this.m_mainParameter = parameterized;
                     this.m_mainParameterObject = object;
                     this.m_mainParameterAnnotation = annotation;
                     this.m_mainParameterDescription = new ParameterDescription(object, annotation, parameterized, this.m_bundle, this);
                  } else {
                     ParameterDescription pd = new ParameterDescription(object, annotation, parameterized, this.m_bundle, this);
                     String[] arr$ = annotation.names();
                     int len$ = arr$.length;

                     for(int i$ = 0; i$ < len$; ++i$) {
                        String name = arr$[i$];
                        if (this.m_descriptions.containsKey(new StringKey(name))) {
                           throw new ParameterException("Found the option " + name + " multiple times");
                        }

                        this.p("Adding description for " + name);
                        this.m_fields.put(parameterized, pd);
                        this.m_descriptions.put(new StringKey(name), pd);
                        if (p.required()) {
                           this.m_requiredFields.put(parameterized, pd);
                        }
                     }
                  }
               } else if (parameterized.getDelegateAnnotation() != null) {
                  Object delegateObject = parameterized.get(object);
                  if (delegateObject == null) {
                     throw new ParameterException("Delegate field '" + parameterized.getName() + "' cannot be null.");
                  }

                  this.addDescription(delegateObject);
               } else if (wp != null && wp.getDynamicParameter() != null) {
                  DynamicParameter dp = wp.getDynamicParameter();
                  String[] arr$ = dp.names();
                  int len$ = arr$.length;

                  for(int i$ = 0; i$ < len$; ++i$) {
                     String name = arr$[i$];
                     if (this.m_descriptions.containsKey(name)) {
                        throw new ParameterException("Found the option " + name + " multiple times");
                     }

                     this.p("Adding description for " + name);
                     ParameterDescription pd = new ParameterDescription(object, dp, parameterized, this.m_bundle, this);
                     this.m_fields.put(parameterized, pd);
                     this.m_descriptions.put(new StringKey(name), pd);
                     if (dp.required()) {
                        this.m_requiredFields.put(parameterized, pd);
                     }
                  }
               }
            }

            return;
         }
      }
   }

   private void initializeDefaultValue(ParameterDescription pd) {
      String[] arr$ = pd.getParameter().names();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String optionName = arr$[i$];
         String def = this.m_defaultProvider.getDefaultValueFor(optionName);
         if (def != null) {
            this.p("Initializing " + optionName + " with default value:" + def);
            pd.addValue(def, true);
            return;
         }
      }

   }

   private void parseValues(String[] args, boolean validate) {
      boolean commandParsed = false;
      int i = 0;

      int increment;
      for(boolean isDashDash = false; i < args.length && !commandParsed; i += increment) {
         String arg = args[i];
         String a = trim(arg);
         args[i] = a;
         this.p("Parsing arg: " + a);
         JCommander jc = this.findCommandByAlias(arg);
         increment = 1;
         if (!isDashDash && !"--".equals(a) && this.isOption(args, a) && jc == null) {
            ParameterDescription pd = this.findParameterDescription(a);
            if (pd != null) {
               if (pd.getParameter().password()) {
                  char[] password = this.readPassword(pd.getDescription(), pd.getParameter().echoInput());
                  pd.addValue(new String(password));
                  this.m_requiredFields.remove(pd.getParameterized());
               } else if (pd.getParameter().variableArity()) {
                  increment = this.processVariableArity(args, i, pd);
               } else {
                  Class<?> fieldType = pd.getParameterized().getType();
                  if ((fieldType == Boolean.TYPE || fieldType == Boolean.class) && pd.getParameter().arity() == -1) {
                     pd.addValue("true");
                     this.m_requiredFields.remove(pd.getParameterized());
                  } else {
                     increment = this.processFixedArity(args, i, pd, fieldType);
                  }

                  if (pd.isHelp()) {
                     this.m_helpWasSpecified = true;
                  }
               }
            } else {
               if (!this.m_acceptUnknownOptions) {
                  throw new ParameterException("Unknown option: " + arg);
               }

               this.m_unknownArgs.add(arg);
               ++i;

               while(i < args.length && !this.isOption(args, args[i])) {
                  this.m_unknownArgs.add(args[i++]);
               }

               increment = 0;
            }
         } else if (!Strings.isStringEmpty(arg)) {
            if ("--".equals(arg)) {
               isDashDash = true;
               ++i;
               a = trim(args[i]);
            }

            if (this.m_commands.isEmpty()) {
               List mp = this.getMainParameter(arg);
               Object convertedValue = a;
               if (this.m_mainParameter.getGenericType() instanceof ParameterizedType) {
                  ParameterizedType p = (ParameterizedType)this.m_mainParameter.getGenericType();
                  Type cls = p.getActualTypeArguments()[0];
                  if (cls instanceof Class) {
                     convertedValue = this.convertValue(this.m_mainParameter, (Class)cls, a);
                  }
               }

               ParameterDescription.validateParameter(this.m_mainParameterDescription, this.m_mainParameterAnnotation.validateWith(), "Default", a);
               this.m_mainParameterDescription.setAssigned(true);
               mp.add(convertedValue);
            } else {
               if (jc == null && validate) {
                  throw new MissingCommandException("Expected a command, got " + arg);
               }

               if (jc != null) {
                  this.m_parsedCommand = jc.m_programName.m_name;
                  this.m_parsedAlias = arg;
                  jc.parse(this.subArray(args, i + 1));
                  commandParsed = true;
               }
            }
         }
      }

      Iterator i$ = this.m_descriptions.values().iterator();

      while(i$.hasNext()) {
         ParameterDescription parameterDescription = (ParameterDescription)i$.next();
         if (parameterDescription.isAssigned()) {
            ((ParameterDescription)this.m_fields.get(parameterDescription.getParameterized())).setAssigned(true);
         }
      }

   }

   private int processVariableArity(String[] args, int index, ParameterDescription pd) {
      Object arg = pd.getObject();
      IVariableArity va;
      if (!(arg instanceof IVariableArity)) {
         va = this.DEFAULT_VARIABLE_ARITY;
      } else {
         va = (IVariableArity)arg;
      }

      List<String> currentArgs = Lists.newArrayList();

      int arity;
      for(arity = index + 1; arity < args.length; ++arity) {
         currentArgs.add(args[arity]);
      }

      arity = va.processVariableArity(pd.getParameter().names()[0], (String[])currentArgs.toArray(new String[0]));
      int result = this.processFixedArity(args, index, pd, List.class, arity);
      return result;
   }

   private int processFixedArity(String[] args, int index, ParameterDescription pd, Class<?> fieldType) {
      int arity = pd.getParameter().arity();
      int n = arity != -1 ? arity : 1;
      return this.processFixedArity(args, index, pd, fieldType, n);
   }

   private int processFixedArity(String[] args, int originalIndex, ParameterDescription pd, Class<?> fieldType, int arity) {
      int index = originalIndex;
      String arg = args[originalIndex];
      if (arity == 0 && (Boolean.class.isAssignableFrom(fieldType) || Boolean.TYPE.isAssignableFrom(fieldType))) {
         pd.addValue("true");
         this.m_requiredFields.remove(pd.getParameterized());
      } else {
         if (originalIndex >= args.length - 1) {
            throw new ParameterException("Expected a value after parameter " + arg);
         }

         int offset = "--".equals(args[originalIndex + 1]) ? 1 : 0;
         if (originalIndex + arity >= args.length) {
            throw new ParameterException("Expected " + arity + " values after " + arg);
         }

         for(int j = 1; j <= arity; ++j) {
            pd.addValue(trim(args[index + j + offset]));
            this.m_requiredFields.remove(pd.getParameterized());
         }

         int var10000 = index + arity + offset;
      }

      return arity + 1;
   }

   private char[] readPassword(String description, boolean echoInput) {
      getConsole().print(description + ": ");
      return getConsole().readPassword(echoInput);
   }

   private String[] subArray(String[] args, int index) {
      int l = args.length - index;
      String[] result = new String[l];
      System.arraycopy(args, index, result, 0, l);
      return result;
   }

   private List<?> getMainParameter(String arg) {
      if (this.m_mainParameter == null) {
         throw new ParameterException("Was passed main parameter '" + arg + "' but no main parameter was defined");
      } else {
         List<?> result = (List)this.m_mainParameter.get(this.m_mainParameterObject);
         if (result == null) {
            result = Lists.newArrayList();
            if (!List.class.isAssignableFrom(this.m_mainParameter.getType())) {
               throw new ParameterException("Main parameter field " + this.m_mainParameter + " needs to be of type List, not " + this.m_mainParameter.getType());
            }

            this.m_mainParameter.set(this.m_mainParameterObject, result);
         }

         if (this.m_firstTimeMainParameter) {
            result.clear();
            this.m_firstTimeMainParameter = false;
         }

         return result;
      }
   }

   public String getMainParameterDescription() {
      if (this.m_descriptions == null) {
         this.createDescriptions();
      }

      return this.m_mainParameterAnnotation != null ? this.m_mainParameterAnnotation.description() : null;
   }

   public void setProgramName(String name) {
      this.setProgramName(name);
   }

   public void setProgramName(String name, String... aliases) {
      this.m_programName = new ProgramName(name, Arrays.asList(aliases));
   }

   public void usage(String commandName) {
      StringBuilder sb = new StringBuilder();
      this.usage(commandName, sb);
      getConsole().println(sb.toString());
   }

   public void usage(String commandName, StringBuilder out) {
      this.usage(commandName, out, "");
   }

   public void usage(String commandName, StringBuilder out, String indent) {
      String description = this.getCommandDescription(commandName);
      JCommander jc = this.findCommandByAlias(commandName);
      if (description != null) {
         out.append(indent).append(description);
         out.append("\n");
      }

      jc.usage(out, indent);
   }

   public String getCommandDescription(String commandName) {
      JCommander jc = this.findCommandByAlias(commandName);
      if (jc == null) {
         throw new ParameterException("Asking description for unknown command: " + commandName);
      } else {
         Object arg = jc.getObjects().get(0);
         Parameters p = (Parameters)arg.getClass().getAnnotation(Parameters.class);
         java.util.ResourceBundle bundle = null;
         String result = null;
         if (p != null) {
            result = p.commandDescription();
            String bundleName = p.resourceBundle();
            if (!"".equals(bundleName)) {
               bundle = java.util.ResourceBundle.getBundle(bundleName, Locale.getDefault());
            } else {
               bundle = this.m_bundle;
            }

            if (bundle != null) {
               result = this.getI18nString(bundle, p.commandDescriptionKey(), p.commandDescription());
            }
         }

         return result;
      }
   }

   private String getI18nString(java.util.ResourceBundle bundle, String key, String def) {
      String s = bundle != null ? bundle.getString(key) : null;
      return s != null ? s : def;
   }

   public void usage() {
      StringBuilder sb = new StringBuilder();
      this.usage(sb);
      getConsole().println(sb.toString());
   }

   public void usage(StringBuilder out) {
      this.usage(out, "");
   }

   public void usage(StringBuilder out, String indent) {
      if (this.m_descriptions == null) {
         this.createDescriptions();
      }

      boolean hasCommands = !this.m_commands.isEmpty();
      String programName = this.m_programName != null ? this.m_programName.getDisplayName() : "<main class>";
      out.append(indent).append("Usage: " + programName + " [options]");
      if (hasCommands) {
         out.append(indent).append(" [command] [command options]");
      }

      if (this.m_mainParameterDescription != null) {
         out.append(" " + this.m_mainParameterDescription.getDescription());
      }

      out.append("\n");
      int longestName = 0;
      List<ParameterDescription> sorted = Lists.newArrayList();
      Iterator i$ = this.m_fields.values().iterator();

      while(i$.hasNext()) {
         ParameterDescription pd = (ParameterDescription)i$.next();
         if (!pd.getParameter().hidden()) {
            sorted.add(pd);
            int length = pd.getNames().length() + 2;
            if (length > longestName) {
               longestName = length;
            }
         }
      }

      Collections.sort(sorted, this.getParameterDescriptionComparator());
      int descriptionIndent = 6;
      if (sorted.size() > 0) {
         out.append(indent).append("  Options:\n");
      }

      String dispName;
      Iterator i$;
      for(i$ = sorted.iterator(); i$.hasNext(); out.append("\n")) {
         ParameterDescription pd = (ParameterDescription)i$.next();
         WrappedParameter parameter = pd.getParameter();
         out.append(indent).append("  " + (parameter.required() ? "* " : "  ") + pd.getNames() + "\n" + indent + this.s(descriptionIndent));
         int indentCount = indent.length() + descriptionIndent;
         this.wrapDescription(out, indentCount, pd.getDescription());
         Object def = pd.getDefault();
         if (pd.isDynamicParameter()) {
            out.append("\n" + this.s(indentCount + 1)).append("Syntax: " + parameter.names()[0] + "key" + parameter.getAssignment() + "value");
         }

         if (def != null) {
            dispName = Strings.isStringEmpty(def.toString()) ? "<empty string>" : def.toString();
            out.append("\n" + this.s(indentCount + 1)).append("Default: " + (parameter.password() ? "********" : dispName));
         }

         Class<?> type = pd.getParameterized().getType();
         if (type.isEnum()) {
            out.append("\n" + this.s(indentCount + 1)).append("Possible Values: " + EnumSet.allOf(type));
         }
      }

      if (hasCommands) {
         out.append("  Commands:\n");
         i$ = this.m_commands.entrySet().iterator();

         while(i$.hasNext()) {
            Map.Entry<ProgramName, JCommander> commands = (Map.Entry)i$.next();
            Object arg = ((JCommander)commands.getValue()).getObjects().get(0);
            Parameters p = (Parameters)arg.getClass().getAnnotation(Parameters.class);
            if (!p.hidden()) {
               ProgramName progName = (ProgramName)commands.getKey();
               dispName = progName.getDisplayName();
               out.append(indent).append("    " + dispName);
               this.usage(progName.getName(), out, "      ");
               out.append("\n");
            }
         }
      }

   }

   private Comparator<? super ParameterDescription> getParameterDescriptionComparator() {
      return this.m_parameterDescriptionComparator;
   }

   public void setParameterDescriptionComparator(Comparator<? super ParameterDescription> c) {
      this.m_parameterDescriptionComparator = c;
   }

   public void setColumnSize(int columnSize) {
      this.m_columnSize = columnSize;
   }

   public int getColumnSize() {
      return this.m_columnSize;
   }

   private void wrapDescription(StringBuilder out, int indent, String description) {
      int max = this.getColumnSize();
      String[] words = description.split(" ");
      int current = indent;

      for(int i = 0; i < words.length; ++i) {
         String word = words[i];
         if (word.length() <= max && current + word.length() > max) {
            out.append("\n").append(this.s(indent + 1)).append(word);
            current = indent;
         } else {
            out.append(" ").append(word);
            current += word.length() + 1;
         }
      }

   }

   public List<ParameterDescription> getParameters() {
      return new ArrayList(this.m_fields.values());
   }

   public ParameterDescription getMainParameter() {
      return this.m_mainParameterDescription;
   }

   private void p(String string) {
      if (this.m_verbose > 0 || System.getProperty("jcommander.debug") != null) {
         getConsole().println("[JCommander] " + string);
      }

   }

   public void setDefaultProvider(IDefaultProvider defaultProvider) {
      this.m_defaultProvider = defaultProvider;
      Iterator i$ = this.m_commands.entrySet().iterator();

      while(i$.hasNext()) {
         Map.Entry<ProgramName, JCommander> entry = (Map.Entry)i$.next();
         ((JCommander)entry.getValue()).setDefaultProvider(defaultProvider);
      }

   }

   public void addConverterFactory(IStringConverterFactory converterFactory) {
      CONVERTER_FACTORIES.addFirst(converterFactory);
   }

   public <T> Class<? extends IStringConverter<T>> findConverter(Class<T> cls) {
      Iterator i$ = CONVERTER_FACTORIES.iterator();

      Class result;
      do {
         if (!i$.hasNext()) {
            return null;
         }

         IStringConverterFactory f = (IStringConverterFactory)i$.next();
         result = f.getConverter(cls);
      } while(result == null);

      return result;
   }

   public Object convertValue(ParameterDescription pd, String value) {
      return this.convertValue(pd.getParameterized(), pd.getParameterized().getType(), value);
   }

   public Object convertValue(Parameterized parameterized, Class type, String value) {
      Parameter annotation = parameterized.getParameter();
      if (annotation == null) {
         return value;
      } else {
         Class<? extends IStringConverter<?>> converterClass = annotation.converter();
         boolean listConverterWasSpecified = annotation.listConverter() != NoConverter.class;
         if (converterClass == null || converterClass == NoConverter.class) {
            if (type.isEnum()) {
               converterClass = type;
            } else {
               converterClass = this.findConverter(type);
            }
         }

         if (converterClass == null) {
            Type elementType = parameterized.findFieldGenericType();
            converterClass = elementType != null ? this.findConverter((Class)elementType) : StringConverter.class;
            if (converterClass == null && Enum.class.isAssignableFrom((Class)elementType)) {
               converterClass = (Class)elementType;
            }
         }

         Object result = null;

         try {
            String[] names = annotation.names();
            String optionName = names.length > 0 ? names[0] : "[Main class]";
            if (converterClass != null && converterClass.isEnum()) {
               try {
                  result = Enum.valueOf(converterClass, value);
               } catch (IllegalArgumentException var14) {
                  try {
                     result = Enum.valueOf(converterClass, value.toUpperCase());
                  } catch (IllegalArgumentException var13) {
                     throw new ParameterException("Invalid value for " + optionName + " parameter. Allowed values:" + EnumSet.allOf(converterClass));
                  }
               } catch (Exception var15) {
                  throw new ParameterException("Invalid value for " + optionName + " parameter. Allowed values:" + EnumSet.allOf(converterClass));
               }
            } else {
               IStringConverter<?> converter = this.instantiateConverter(optionName, converterClass);
               if (type.isAssignableFrom(List.class) && parameterized.getGenericType() instanceof ParameterizedType) {
                  if (listConverterWasSpecified) {
                     IStringConverter<?> listConverter = this.instantiateConverter(optionName, annotation.listConverter());
                     result = listConverter.convert(value);
                  } else {
                     result = this.convertToList(value, converter, annotation.splitter());
                  }
               } else {
                  result = converter.convert(value);
               }
            }

            return result;
         } catch (InstantiationException var16) {
            throw new ParameterException(var16);
         } catch (IllegalAccessException var17) {
            throw new ParameterException(var17);
         } catch (InvocationTargetException var18) {
            throw new ParameterException(var18);
         }
      }
   }

   private Object convertToList(String value, IStringConverter<?> converter, Class<? extends IParameterSplitter> splitterClass) throws InstantiationException, IllegalAccessException {
      IParameterSplitter splitter = (IParameterSplitter)splitterClass.newInstance();
      List<Object> result = Lists.newArrayList();
      Iterator i$ = splitter.split(value).iterator();

      while(i$.hasNext()) {
         String param = (String)i$.next();
         result.add(converter.convert(param));
      }

      return result;
   }

   private IStringConverter<?> instantiateConverter(String optionName, Class<? extends IStringConverter<?>> converterClass) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
      Constructor<IStringConverter<?>> ctor = null;
      Constructor<IStringConverter<?>> stringCtor = null;
      Constructor<IStringConverter<?>>[] ctors = (Constructor[])converterClass.getDeclaredConstructors();
      Constructor[] arr$ = ctors;
      int len$ = ctors.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Constructor<IStringConverter<?>> c = arr$[i$];
         Class<?>[] types = c.getParameterTypes();
         if (types.length == 1 && types[0].equals(String.class)) {
            stringCtor = c;
         } else if (types.length == 0) {
            ctor = c;
         }
      }

      IStringConverter<?> result = stringCtor != null ? (IStringConverter)stringCtor.newInstance(optionName) : (ctor != null ? (IStringConverter)ctor.newInstance() : null);
      return result;
   }

   public void addCommand(String name, Object object) {
      this.addCommand(name, object);
   }

   public void addCommand(Object object) {
      Parameters p = (Parameters)object.getClass().getAnnotation(Parameters.class);
      if (p != null && p.commandNames().length > 0) {
         String[] arr$ = p.commandNames();
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            String commandName = arr$[i$];
            this.addCommand(commandName, object);
         }

      } else {
         throw new ParameterException("Trying to add command " + object.getClass().getName() + " without specifying its names in @Parameters");
      }
   }

   public void addCommand(String name, Object object, String... aliases) {
      JCommander jc = new JCommander(object);
      jc.setProgramName(name, aliases);
      jc.setDefaultProvider(this.m_defaultProvider);
      jc.setAcceptUnknownOptions(this.m_acceptUnknownOptions);
      ProgramName progName = jc.m_programName;
      this.m_commands.put(progName, jc);
      this.aliasMap.put(new StringKey(name), progName);
      String[] arr$ = aliases;
      int len$ = aliases.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String a = arr$[i$];
         FuzzyMap.IKey alias = new StringKey(a);
         if (!alias.equals(name)) {
            ProgramName mappedName = (ProgramName)this.aliasMap.get(alias);
            if (mappedName != null && !mappedName.equals(progName)) {
               throw new ParameterException("Cannot set alias " + alias + " for " + name + " command because it has already been defined for " + mappedName.m_name + " command");
            }

            this.aliasMap.put(alias, progName);
         }
      }

   }

   public Map<String, JCommander> getCommands() {
      Map<String, JCommander> res = Maps.newLinkedHashMap();
      Iterator i$ = this.m_commands.entrySet().iterator();

      while(i$.hasNext()) {
         Map.Entry<ProgramName, JCommander> entry = (Map.Entry)i$.next();
         res.put(((ProgramName)entry.getKey()).m_name, entry.getValue());
      }

      return res;
   }

   public String getParsedCommand() {
      return this.m_parsedCommand;
   }

   public String getParsedAlias() {
      return this.m_parsedAlias;
   }

   private String s(int count) {
      StringBuilder result = new StringBuilder();

      for(int i = 0; i < count; ++i) {
         result.append(" ");
      }

      return result.toString();
   }

   public List<Object> getObjects() {
      return this.m_objects;
   }

   private ParameterDescription findParameterDescription(String arg) {
      return (ParameterDescription)FuzzyMap.findInMap(this.m_descriptions, new StringKey(arg), this.m_caseSensitiveOptions, this.m_allowAbbreviatedOptions);
   }

   private JCommander findCommand(ProgramName name) {
      return (JCommander)FuzzyMap.findInMap(this.m_commands, name, this.m_caseSensitiveOptions, this.m_allowAbbreviatedOptions);
   }

   private ProgramName findProgramName(String name) {
      return (ProgramName)FuzzyMap.findInMap(this.aliasMap, new StringKey(name), this.m_caseSensitiveOptions, this.m_allowAbbreviatedOptions);
   }

   private JCommander findCommandByAlias(String commandOrAlias) {
      ProgramName progName = this.findProgramName(commandOrAlias);
      if (progName == null) {
         return null;
      } else {
         JCommander jc = this.findCommand(progName);
         if (jc == null) {
            throw new IllegalStateException("There appears to be inconsistency in the internal command database.  This is likely a bug. Please report.");
         } else {
            return jc;
         }
      }
   }

   public void setVerbose(int verbose) {
      this.m_verbose = verbose;
   }

   public void setCaseSensitiveOptions(boolean b) {
      this.m_caseSensitiveOptions = b;
   }

   public void setAllowAbbreviatedOptions(boolean b) {
      this.m_allowAbbreviatedOptions = b;
   }

   public void setAcceptUnknownOptions(boolean b) {
      this.m_acceptUnknownOptions = b;
   }

   public List<String> getUnknownOptions() {
      return this.m_unknownArgs;
   }

   public void setAllowParameterOverwriting(boolean b) {
      this.m_allowParameterOverwriting = b;
   }

   public boolean isParameterOverwritingAllowed() {
      return this.m_allowParameterOverwriting;
   }

   static {
      CONVERTER_FACTORIES.addFirst(new DefaultConverterFactory());
   }

   private static final class ProgramName implements FuzzyMap.IKey {
      private final String m_name;
      private final List<String> m_aliases;

      ProgramName(String name, List<String> aliases) {
         this.m_name = name;
         this.m_aliases = aliases;
      }

      public String getName() {
         return this.m_name;
      }

      private String getDisplayName() {
         StringBuilder sb = new StringBuilder();
         sb.append(this.m_name);
         if (!this.m_aliases.isEmpty()) {
            sb.append("(");
            Iterator<String> aliasesIt = this.m_aliases.iterator();

            while(aliasesIt.hasNext()) {
               sb.append((String)aliasesIt.next());
               if (aliasesIt.hasNext()) {
                  sb.append(",");
               }
            }

            sb.append(")");
         }

         return sb.toString();
      }

      public int hashCode() {
         int prime = true;
         int result = 1;
         result = 31 * result + (this.m_name == null ? 0 : this.m_name.hashCode());
         return result;
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            ProgramName other = (ProgramName)obj;
            if (this.m_name == null) {
               if (other.m_name != null) {
                  return false;
               }
            } else if (!this.m_name.equals(other.m_name)) {
               return false;
            }

            return true;
         }
      }

      public String toString() {
         return this.getDisplayName();
      }
   }

   private class DefaultVariableArity implements IVariableArity {
      private DefaultVariableArity() {
      }

      public int processVariableArity(String optionName, String[] options) {
         int i;
         for(i = 0; i < options.length && !JCommander.this.isOption(options, options[i]); ++i) {
         }

         return i;
      }

      // $FF: synthetic method
      DefaultVariableArity(Object x1) {
         this();
      }
   }
}
