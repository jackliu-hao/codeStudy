/*     */ package cn.hutool.core.math;
/*     */ 
/*     */ import cn.hutool.core.util.NumberUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Collections;
/*     */ import java.util.Stack;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Calculator
/*     */ {
/*  18 */   private final Stack<String> postfixStack = new Stack<>();
/*  19 */   private final int[] operatPriority = new int[] { 0, 3, 2, 1, -1, 1, 0, 2 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double conversion(String expression) {
/*  28 */     return (new Calculator()).calculate(expression);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double calculate(String expression) {
/*  38 */     prepare(transform(expression));
/*     */     
/*  40 */     Stack<String> resultStack = new Stack<>();
/*  41 */     Collections.reverse(this.postfixStack);
/*     */     
/*  43 */     while (false == this.postfixStack.isEmpty()) {
/*  44 */       String currentOp = this.postfixStack.pop();
/*  45 */       if (false == isOperator(currentOp.charAt(0))) {
/*  46 */         currentOp = currentOp.replace("~", "-");
/*  47 */         resultStack.push(currentOp); continue;
/*     */       } 
/*  49 */       String secondValue = resultStack.pop();
/*  50 */       String firstValue = resultStack.pop();
/*     */ 
/*     */       
/*  53 */       firstValue = firstValue.replace("~", "-");
/*  54 */       secondValue = secondValue.replace("~", "-");
/*     */       
/*  56 */       BigDecimal tempResult = calculate(firstValue, secondValue, currentOp.charAt(0));
/*  57 */       resultStack.push(tempResult.toString());
/*     */     } 
/*     */     
/*  60 */     return Double.parseDouble(resultStack.pop());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void prepare(String expression) {
/*  69 */     Stack<Character> opStack = new Stack<>();
/*  70 */     opStack.push(Character.valueOf(','));
/*  71 */     char[] arr = expression.toCharArray();
/*  72 */     int currentIndex = 0;
/*  73 */     int count = 0;
/*     */     
/*  75 */     for (int i = 0; i < arr.length; i++) {
/*  76 */       char currentOp = arr[i];
/*  77 */       if (isOperator(currentOp)) {
/*  78 */         if (count > 0) {
/*  79 */           this.postfixStack.push(new String(arr, currentIndex, count));
/*     */         }
/*  81 */         char peekOp = ((Character)opStack.peek()).charValue();
/*  82 */         if (currentOp == ')') {
/*  83 */           while (((Character)opStack.peek()).charValue() != '(') {
/*  84 */             this.postfixStack.push(String.valueOf(opStack.pop()));
/*     */           }
/*  86 */           opStack.pop();
/*     */         } else {
/*  88 */           while (currentOp != '(' && peekOp != ',' && compare(currentOp, peekOp)) {
/*  89 */             this.postfixStack.push(String.valueOf(opStack.pop()));
/*  90 */             peekOp = ((Character)opStack.peek()).charValue();
/*     */           } 
/*  92 */           opStack.push(Character.valueOf(currentOp));
/*     */         } 
/*  94 */         count = 0;
/*  95 */         currentIndex = i + 1;
/*     */       } else {
/*  97 */         count++;
/*     */       } 
/*     */     } 
/* 100 */     if (count > 1 || (count == 1 && !isOperator(arr[currentIndex]))) {
/* 101 */       this.postfixStack.push(new String(arr, currentIndex, count));
/*     */     }
/*     */     
/* 104 */     while (((Character)opStack.peek()).charValue() != ',') {
/* 105 */       this.postfixStack.push(String.valueOf(opStack.pop()));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isOperator(char c) {
/* 116 */     return (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')' || c == '%');
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean compare(char cur, char peek) {
/* 127 */     int offset = 40;
/* 128 */     if (cur == '%')
/*     */     {
/* 130 */       cur = '/';
/*     */     }
/* 132 */     if (peek == '%')
/*     */     {
/* 134 */       peek = '/';
/*     */     }
/*     */     
/* 137 */     return (this.operatPriority[peek - 40] >= this.operatPriority[cur - 40]);
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
/*     */   private BigDecimal calculate(String firstValue, String secondValue, char currentOp) {
/*     */     BigDecimal result;
/* 150 */     switch (currentOp) {
/*     */       case '+':
/* 152 */         result = NumberUtil.add(new String[] { firstValue, secondValue });
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
/* 169 */         return result;case '-': result = NumberUtil.sub(new String[] { firstValue, secondValue }); return result;case '*': result = NumberUtil.mul(firstValue, secondValue); return result;case '/': result = NumberUtil.div(firstValue, secondValue); return result;case '%': result = NumberUtil.toBigDecimal(firstValue).remainder(NumberUtil.toBigDecimal(secondValue)); return result;
/*     */     } 
/*     */     throw new IllegalStateException("Unexpected value: " + currentOp);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String transform(String expression) {
/* 179 */     expression = StrUtil.cleanBlank(expression);
/* 180 */     expression = StrUtil.removeSuffix(expression, "=");
/* 181 */     char[] arr = expression.toCharArray();
/* 182 */     for (int i = 0; i < arr.length; i++) {
/* 183 */       if (arr[i] == '-') {
/* 184 */         if (i == 0) {
/* 185 */           arr[i] = '~';
/*     */         } else {
/* 187 */           char c = arr[i - 1];
/* 188 */           if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == 'E' || c == 'e') {
/* 189 */             arr[i] = '~';
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/* 194 */     if (arr[0] == '~' && arr.length > 1 && arr[1] == '(') {
/* 195 */       arr[0] = '-';
/* 196 */       return "0" + new String(arr);
/*     */     } 
/* 198 */     return new String(arr);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\math\Calculator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */