/*    */ package freemarker.core;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class ParameterRole
/*    */ {
/*    */   private final String name;
/* 30 */   static final ParameterRole UNKNOWN = new ParameterRole("[unknown role]");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 35 */   static final ParameterRole LEFT_HAND_OPERAND = new ParameterRole("left-hand operand");
/* 36 */   static final ParameterRole RIGHT_HAND_OPERAND = new ParameterRole("right-hand operand");
/* 37 */   static final ParameterRole ENCLOSED_OPERAND = new ParameterRole("enclosed operand");
/* 38 */   static final ParameterRole ITEM_VALUE = new ParameterRole("item value");
/* 39 */   static final ParameterRole ITEM_KEY = new ParameterRole("item key");
/* 40 */   static final ParameterRole ASSIGNMENT_TARGET = new ParameterRole("assignment target");
/* 41 */   static final ParameterRole ASSIGNMENT_OPERATOR = new ParameterRole("assignment operator");
/* 42 */   static final ParameterRole ASSIGNMENT_SOURCE = new ParameterRole("assignment source");
/* 43 */   static final ParameterRole VARIABLE_SCOPE = new ParameterRole("variable scope");
/* 44 */   static final ParameterRole NAMESPACE = new ParameterRole("namespace");
/* 45 */   static final ParameterRole ERROR_HANDLER = new ParameterRole("error handler");
/* 46 */   static final ParameterRole PASSED_VALUE = new ParameterRole("passed value");
/* 47 */   static final ParameterRole CONDITION = new ParameterRole("condition");
/* 48 */   static final ParameterRole VALUE = new ParameterRole("value");
/* 49 */   static final ParameterRole AST_NODE_SUBTYPE = new ParameterRole("AST-node subtype");
/* 50 */   static final ParameterRole PLACEHOLDER_VARIABLE = new ParameterRole("placeholder variable");
/* 51 */   static final ParameterRole EXPRESSION_TEMPLATE = new ParameterRole("expression template");
/* 52 */   static final ParameterRole LIST_SOURCE = new ParameterRole("list source");
/* 53 */   static final ParameterRole TARGET_LOOP_VARIABLE = new ParameterRole("target loop variable");
/* 54 */   static final ParameterRole TEMPLATE_NAME = new ParameterRole("template name");
/* 55 */   static final ParameterRole PARSE_PARAMETER = new ParameterRole("\"parse\" parameter");
/* 56 */   static final ParameterRole ENCODING_PARAMETER = new ParameterRole("\"encoding\" parameter");
/* 57 */   static final ParameterRole IGNORE_MISSING_PARAMETER = new ParameterRole("\"ignore_missing\" parameter");
/* 58 */   static final ParameterRole PARAMETER_NAME = new ParameterRole("parameter name");
/* 59 */   static final ParameterRole PARAMETER_DEFAULT = new ParameterRole("parameter default");
/* 60 */   static final ParameterRole CATCH_ALL_PARAMETER_NAME = new ParameterRole("catch-all parameter name");
/* 61 */   static final ParameterRole ARGUMENT_NAME = new ParameterRole("argument name");
/* 62 */   static final ParameterRole ARGUMENT_VALUE = new ParameterRole("argument value");
/* 63 */   static final ParameterRole CONTENT = new ParameterRole("content");
/* 64 */   static final ParameterRole EMBEDDED_TEMPLATE = new ParameterRole("embedded template");
/* 65 */   static final ParameterRole VALUE_PART = new ParameterRole("value part");
/* 66 */   static final ParameterRole MINIMUM_DECIMALS = new ParameterRole("minimum decimals");
/* 67 */   static final ParameterRole MAXIMUM_DECIMALS = new ParameterRole("maximum decimals");
/* 68 */   static final ParameterRole NODE = new ParameterRole("node");
/* 69 */   static final ParameterRole CALLEE = new ParameterRole("callee");
/* 70 */   static final ParameterRole MESSAGE = new ParameterRole("message");
/*    */   
/*    */   private ParameterRole(String name) {
/* 73 */     this.name = name;
/*    */   }
/*    */   
/*    */   static ParameterRole forBinaryOperatorOperand(int paramIndex) {
/* 77 */     switch (paramIndex) { case 0:
/* 78 */         return LEFT_HAND_OPERAND;
/* 79 */       case 1: return RIGHT_HAND_OPERAND; }
/* 80 */      throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 85 */     return this.name;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 90 */     return this.name;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ParameterRole.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */