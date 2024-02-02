package freemarker.core;

import freemarker.template.Template;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.ClassUtil;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class _CoreAPI {
   public static final String ERROR_MESSAGE_HR = "----";
   public static final Set<String> ALL_BUILT_IN_DIRECTIVE_NAMES;
   public static final Set<String> LEGACY_BUILT_IN_DIRECTIVE_NAMES;
   public static final Set<String> CAMEL_CASE_BUILT_IN_DIRECTIVE_NAMES;

   private _CoreAPI() {
   }

   private static void addName(Set<String> allNames, Set<String> lcNames, Set<String> ccNames, String commonName) {
      allNames.add(commonName);
      lcNames.add(commonName);
      ccNames.add(commonName);
   }

   private static void addName(Set<String> allNames, Set<String> lcNames, Set<String> ccNames, String lcName, String ccName) {
      allNames.add(lcName);
      allNames.add(ccName);
      lcNames.add(lcName);
      ccNames.add(ccName);
   }

   public static Set<String> getSupportedBuiltInNames(int namingConvention) {
      Set names;
      if (namingConvention == 10) {
         names = BuiltIn.BUILT_INS_BY_NAME.keySet();
      } else if (namingConvention == 11) {
         names = BuiltIn.SNAKE_CASE_NAMES;
      } else {
         if (namingConvention != 12) {
            throw new IllegalArgumentException("Unsupported naming convention constant: " + namingConvention);
         }

         names = BuiltIn.CAMEL_CASE_NAMES;
      }

      return Collections.unmodifiableSet(names);
   }

   public static void appendInstructionStackItem(TemplateElement stackEl, StringBuilder sb) {
      Environment.appendInstructionStackItem(stackEl, sb);
   }

   public static TemplateElement[] getInstructionStackSnapshot(Environment env) {
      return env.getInstructionStackSnapshot();
   }

   public static void outputInstructionStack(TemplateElement[] instructionStackSnapshot, boolean terseMode, Writer pw) {
      Environment.outputInstructionStack(instructionStackSnapshot, terseMode, pw);
   }

   public static final void addThreadInterruptedChecks(Template template) {
      try {
         (new ThreadInterruptionSupportTemplatePostProcessor()).postProcess(template);
      } catch (TemplatePostProcessorException var2) {
         throw new RuntimeException("Template post-processing failed", var2);
      }
   }

   public static final void checkHasNoNestedContent(TemplateDirectiveBody body) throws NestedContentNotSupportedException {
      NestedContentNotSupportedException.check(body);
   }

   public static final void replaceText(TextBlock textBlock, String text) {
      textBlock.replaceText(text);
   }

   public static void checkSettingValueItemsType(String somethingsSentenceStart, Class<?> expectedClass, Collection<?> values) {
      if (values != null) {
         Iterator var3 = values.iterator();

         Object value;
         do {
            if (!var3.hasNext()) {
               return;
            }

            value = var3.next();
         } while(expectedClass.isInstance(value));

         throw new IllegalArgumentException(somethingsSentenceStart + " must be instances of " + ClassUtil.getShortClassName(expectedClass) + ", but one of them was a(n) " + ClassUtil.getShortClassNameOfObject(value) + ".");
      }
   }

   public static TemplateModelException ensureIsTemplateModelException(String modelOpMsg, TemplateException e) {
      return (TemplateModelException)(e instanceof TemplateModelException ? (TemplateModelException)e : new _TemplateModelException(_TemplateAPI.getBlamedExpression(e), e.getCause(), e.getEnvironment(), modelOpMsg));
   }

   public static TemplateElement getParentElement(TemplateElement te) {
      return te.getParentElement();
   }

   public static TemplateElement getChildElement(TemplateElement te, int index) {
      return te.getChild(index);
   }

   public static void setPreventStrippings(FMParser parser, boolean preventStrippings) {
      parser.setPreventStrippings(preventStrippings);
   }

   public static boolean isLazilyGeneratedSequenceModel(TemplateCollectionModel model) {
      return model instanceof LazilyGeneratedCollectionModel && ((LazilyGeneratedCollectionModel)model).isSequence();
   }

   static {
      Set<String> allNames = new TreeSet();
      Set<String> lcNames = new TreeSet();
      Set<String> ccNames = new TreeSet();
      addName(allNames, lcNames, ccNames, "assign");
      addName(allNames, lcNames, ccNames, "attempt");
      addName(allNames, lcNames, ccNames, "autoesc", "autoEsc");
      addName(allNames, lcNames, ccNames, "break");
      addName(allNames, lcNames, ccNames, "call");
      addName(allNames, lcNames, ccNames, "case");
      addName(allNames, lcNames, ccNames, "comment");
      addName(allNames, lcNames, ccNames, "compress");
      addName(allNames, lcNames, ccNames, "continue");
      addName(allNames, lcNames, ccNames, "default");
      addName(allNames, lcNames, ccNames, "else");
      addName(allNames, lcNames, ccNames, "elseif", "elseIf");
      addName(allNames, lcNames, ccNames, "escape");
      addName(allNames, lcNames, ccNames, "fallback");
      addName(allNames, lcNames, ccNames, "flush");
      addName(allNames, lcNames, ccNames, "foreach", "forEach");
      addName(allNames, lcNames, ccNames, "ftl");
      addName(allNames, lcNames, ccNames, "function");
      addName(allNames, lcNames, ccNames, "global");
      addName(allNames, lcNames, ccNames, "if");
      addName(allNames, lcNames, ccNames, "import");
      addName(allNames, lcNames, ccNames, "include");
      addName(allNames, lcNames, ccNames, "items");
      addName(allNames, lcNames, ccNames, "list");
      addName(allNames, lcNames, ccNames, "local");
      addName(allNames, lcNames, ccNames, "lt");
      addName(allNames, lcNames, ccNames, "macro");
      addName(allNames, lcNames, ccNames, "nested");
      addName(allNames, lcNames, ccNames, "noautoesc", "noAutoEsc");
      addName(allNames, lcNames, ccNames, "noescape", "noEscape");
      addName(allNames, lcNames, ccNames, "noparse", "noParse");
      addName(allNames, lcNames, ccNames, "nt");
      addName(allNames, lcNames, ccNames, "outputformat", "outputFormat");
      addName(allNames, lcNames, ccNames, "recover");
      addName(allNames, lcNames, ccNames, "recurse");
      addName(allNames, lcNames, ccNames, "return");
      addName(allNames, lcNames, ccNames, "rt");
      addName(allNames, lcNames, ccNames, "sep");
      addName(allNames, lcNames, ccNames, "setting");
      addName(allNames, lcNames, ccNames, "stop");
      addName(allNames, lcNames, ccNames, "switch");
      addName(allNames, lcNames, ccNames, "t");
      addName(allNames, lcNames, ccNames, "transform");
      addName(allNames, lcNames, ccNames, "visit");
      ALL_BUILT_IN_DIRECTIVE_NAMES = Collections.unmodifiableSet(allNames);
      LEGACY_BUILT_IN_DIRECTIVE_NAMES = Collections.unmodifiableSet(lcNames);
      CAMEL_CASE_BUILT_IN_DIRECTIVE_NAMES = Collections.unmodifiableSet(ccNames);
   }
}
