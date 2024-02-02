/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateException;
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
/*     */ public class InvalidReferenceException
/*     */   extends TemplateException
/*     */ {
/*     */   static final InvalidReferenceException FAST_INSTANCE;
/*     */   
/*     */   static {
/*  32 */     Environment prevEnv = Environment.getCurrentEnvironment();
/*     */     try {
/*  34 */       Environment.setCurrentEnvironment(null);
/*  35 */       FAST_INSTANCE = new InvalidReferenceException("Invalid reference. Details are unavilable, as this should have been handled by an FTL construct. If it wasn't, that's problably a bug in FreeMarker.", null);
/*     */     
/*     */     }
/*     */     finally {
/*     */       
/*  40 */       Environment.setCurrentEnvironment(prevEnv);
/*     */     } 
/*     */   }
/*     */   
/*  44 */   private static final Object[] TIP = new Object[] { "If the failing expression is known to legally refer to something that's sometimes null or missing, either specify a default value like myOptionalVar!myDefault, or use ", "<#if myOptionalVar??>", "when-present", "<#else>", "when-missing", "</#if>", ". (These only cover the last step of the expression; to cover the whole expression, use parenthesis: (myOptionalVar.foo)!myDefault, (myOptionalVar.foo)??" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  52 */   private static final Object[] TIP_MISSING_ASSIGNMENT_TARGET = new Object[] { "If the target variable is known to be legally null or missing sometimes, instead of something like ", "<#assign x += 1>", ", you could write ", "<#if x??>", "<#assign x += 1>", "</#if>", " or ", "<#assign x = (x!0) + 1>" };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String TIP_NO_DOLLAR = "Variable references must not start with \"$\", unless the \"$\" is really part of the variable name.";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String TIP_LAST_STEP_DOT = "It's the step after the last dot that caused this error, not those before it.";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String TIP_LAST_STEP_SQUARE_BRACKET = "It's the final [] step that caused this error, not those before it.";
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String TIP_JSP_TAGLIBS = "The \"JspTaglibs\" variable isn't a core FreeMarker feature; it's only available when templates are invoked through freemarker.ext.servlet.FreemarkerServlet (or other custom FreeMarker-JSP integration solution).";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InvalidReferenceException(Environment env) {
/*  77 */     super("Invalid reference: The expression has evaluated to null or refers to something that doesn't exist.", env);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InvalidReferenceException(String description, Environment env) {
/*  87 */     super(description, env);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   InvalidReferenceException(_ErrorDescriptionBuilder description, Environment env, Expression expression) {
/*  98 */     super(null, env, expression, description);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static InvalidReferenceException getInstance(Expression blamed, Environment env) {
/* 106 */     if (env != null && env.getFastInvalidReferenceExceptions()) {
/* 107 */       return FAST_INSTANCE;
/*     */     }
/* 109 */     if (blamed != null) {
/*     */       
/* 111 */       _ErrorDescriptionBuilder errDescBuilder = (new _ErrorDescriptionBuilder("The following has evaluated to null or missing:")).blame(blamed);
/* 112 */       if (endsWithDollarVariable(blamed)) {
/* 113 */         errDescBuilder.tips(new Object[] { "Variable references must not start with \"$\", unless the \"$\" is really part of the variable name.", TIP });
/* 114 */       } else if (blamed instanceof Dot) {
/* 115 */         String rho = ((Dot)blamed).getRHO();
/* 116 */         String nameFixTip = null;
/* 117 */         if ("size".equals(rho)) {
/* 118 */           nameFixTip = "To query the size of a collection or map use ?size, like myList?size";
/* 119 */         } else if ("length".equals(rho)) {
/* 120 */           nameFixTip = "To query the length of a string use ?length, like myString?size";
/*     */         } 
/* 122 */         (new Object[2])[0] = "It's the step after the last dot that caused this error, not those before it."; (new Object[2])[1] = TIP; (new Object[3])[0] = "It's the step after the last dot that caused this error, not those before it."; (new Object[3])[1] = nameFixTip; (new Object[3])[2] = TIP; errDescBuilder.tips((nameFixTip == null) ? new Object[2] : new Object[3]);
/*     */ 
/*     */       
/*     */       }
/* 126 */       else if (blamed instanceof DynamicKeyName) {
/* 127 */         errDescBuilder.tips(new Object[] { "It's the final [] step that caused this error, not those before it.", TIP });
/* 128 */       } else if (blamed instanceof Identifier && ((Identifier)blamed)
/* 129 */         .getName().equals("JspTaglibs")) {
/* 130 */         errDescBuilder.tips(new Object[] { "The \"JspTaglibs\" variable isn't a core FreeMarker feature; it's only available when templates are invoked through freemarker.ext.servlet.FreemarkerServlet (or other custom FreeMarker-JSP integration solution).", TIP });
/*     */       } else {
/* 132 */         errDescBuilder.tip(TIP);
/*     */       } 
/* 134 */       return new InvalidReferenceException(errDescBuilder, env, blamed);
/*     */     } 
/* 136 */     return new InvalidReferenceException(env);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static InvalidReferenceException getInstance(int scope, String missingAssignedVarName, String assignmentOperator, Environment env) {
/* 146 */     if (env != null && env.getFastInvalidReferenceExceptions()) {
/* 147 */       return FAST_INSTANCE;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 152 */     _ErrorDescriptionBuilder errDescBuilder = new _ErrorDescriptionBuilder(new Object[] { "The target variable of the assignment, ", new _DelayedJQuote(missingAssignedVarName), ", was null or missing in the " + Assignment.scopeAsString(scope) + ", and the \"", assignmentOperator, "\" operator must get its value from there before assigning to it." });
/*     */ 
/*     */     
/* 155 */     if (missingAssignedVarName.startsWith("$")) {
/* 156 */       errDescBuilder.tips(new Object[] { "Variable references must not start with \"$\", unless the \"$\" is really part of the variable name.", TIP_MISSING_ASSIGNMENT_TARGET });
/*     */     } else {
/* 158 */       errDescBuilder.tip(TIP_MISSING_ASSIGNMENT_TARGET);
/*     */     } 
/* 160 */     return new InvalidReferenceException(errDescBuilder, env, null);
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean endsWithDollarVariable(Expression blame) {
/* 165 */     return ((blame instanceof Identifier && ((Identifier)blame).getName().startsWith("$")) || (blame instanceof Dot && ((Dot)blame)
/* 166 */       .getRHO().startsWith("$")));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\InvalidReferenceException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */