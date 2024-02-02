package freemarker.core;

import freemarker.template.TemplateException;

public class InvalidReferenceException extends TemplateException {
   static final InvalidReferenceException FAST_INSTANCE;
   private static final Object[] TIP;
   private static final Object[] TIP_MISSING_ASSIGNMENT_TARGET;
   private static final String TIP_NO_DOLLAR = "Variable references must not start with \"$\", unless the \"$\" is really part of the variable name.";
   private static final String TIP_LAST_STEP_DOT = "It's the step after the last dot that caused this error, not those before it.";
   private static final String TIP_LAST_STEP_SQUARE_BRACKET = "It's the final [] step that caused this error, not those before it.";
   private static final String TIP_JSP_TAGLIBS = "The \"JspTaglibs\" variable isn't a core FreeMarker feature; it's only available when templates are invoked through freemarker.ext.servlet.FreemarkerServlet (or other custom FreeMarker-JSP integration solution).";

   public InvalidReferenceException(Environment env) {
      super("Invalid reference: The expression has evaluated to null or refers to something that doesn't exist.", env);
   }

   public InvalidReferenceException(String description, Environment env) {
      super(description, env);
   }

   InvalidReferenceException(_ErrorDescriptionBuilder description, Environment env, Expression expression) {
      super((Throwable)null, env, expression, description);
   }

   static InvalidReferenceException getInstance(Expression blamed, Environment env) {
      if (env != null && env.getFastInvalidReferenceExceptions()) {
         return FAST_INSTANCE;
      } else if (blamed == null) {
         return new InvalidReferenceException(env);
      } else {
         _ErrorDescriptionBuilder errDescBuilder = (new _ErrorDescriptionBuilder("The following has evaluated to null or missing:")).blame(blamed);
         if (endsWithDollarVariable(blamed)) {
            errDescBuilder.tips("Variable references must not start with \"$\", unless the \"$\" is really part of the variable name.", TIP);
         } else if (blamed instanceof Dot) {
            String rho = ((Dot)blamed).getRHO();
            String nameFixTip = null;
            if ("size".equals(rho)) {
               nameFixTip = "To query the size of a collection or map use ?size, like myList?size";
            } else if ("length".equals(rho)) {
               nameFixTip = "To query the length of a string use ?length, like myString?size";
            }

            errDescBuilder.tips(nameFixTip == null ? new Object[]{"It's the step after the last dot that caused this error, not those before it.", TIP} : new Object[]{"It's the step after the last dot that caused this error, not those before it.", nameFixTip, TIP});
         } else if (blamed instanceof DynamicKeyName) {
            errDescBuilder.tips("It's the final [] step that caused this error, not those before it.", TIP);
         } else if (blamed instanceof Identifier && ((Identifier)blamed).getName().equals("JspTaglibs")) {
            errDescBuilder.tips("The \"JspTaglibs\" variable isn't a core FreeMarker feature; it's only available when templates are invoked through freemarker.ext.servlet.FreemarkerServlet (or other custom FreeMarker-JSP integration solution).", TIP);
         } else {
            errDescBuilder.tip(TIP);
         }

         return new InvalidReferenceException(errDescBuilder, env, blamed);
      }
   }

   static InvalidReferenceException getInstance(int scope, String missingAssignedVarName, String assignmentOperator, Environment env) {
      if (env != null && env.getFastInvalidReferenceExceptions()) {
         return FAST_INSTANCE;
      } else {
         _ErrorDescriptionBuilder errDescBuilder = new _ErrorDescriptionBuilder(new Object[]{"The target variable of the assignment, ", new _DelayedJQuote(missingAssignedVarName), ", was null or missing in the " + Assignment.scopeAsString(scope) + ", and the \"", assignmentOperator, "\" operator must get its value from there before assigning to it."});
         if (missingAssignedVarName.startsWith("$")) {
            errDescBuilder.tips("Variable references must not start with \"$\", unless the \"$\" is really part of the variable name.", TIP_MISSING_ASSIGNMENT_TARGET);
         } else {
            errDescBuilder.tip(TIP_MISSING_ASSIGNMENT_TARGET);
         }

         return new InvalidReferenceException(errDescBuilder, env, (Expression)null);
      }
   }

   private static boolean endsWithDollarVariable(Expression blame) {
      return blame instanceof Identifier && ((Identifier)blame).getName().startsWith("$") || blame instanceof Dot && ((Dot)blame).getRHO().startsWith("$");
   }

   static {
      Environment prevEnv = Environment.getCurrentEnvironment();

      try {
         Environment.setCurrentEnvironment((Environment)null);
         FAST_INSTANCE = new InvalidReferenceException("Invalid reference. Details are unavilable, as this should have been handled by an FTL construct. If it wasn't, that's problably a bug in FreeMarker.", (Environment)null);
      } finally {
         Environment.setCurrentEnvironment(prevEnv);
      }

      TIP = new Object[]{"If the failing expression is known to legally refer to something that's sometimes null or missing, either specify a default value like myOptionalVar!myDefault, or use ", "<#if myOptionalVar??>", "when-present", "<#else>", "when-missing", "</#if>", ". (These only cover the last step of the expression; to cover the whole expression, use parenthesis: (myOptionalVar.foo)!myDefault, (myOptionalVar.foo)??"};
      TIP_MISSING_ASSIGNMENT_TARGET = new Object[]{"If the target variable is known to be legally null or missing sometimes, instead of something like ", "<#assign x += 1>", ", you could write ", "<#if x??>", "<#assign x += 1>", "</#if>", " or ", "<#assign x = (x!0) + 1>"};
   }
}
