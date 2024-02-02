package cn.hutool.core.math;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Stack;

public class Calculator {
   private final Stack<String> postfixStack = new Stack();
   private final int[] operatPriority = new int[]{0, 3, 2, 1, -1, 1, 0, 2};

   public static double conversion(String expression) {
      return (new Calculator()).calculate(expression);
   }

   public double calculate(String expression) {
      this.prepare(transform(expression));
      Stack<String> resultStack = new Stack();
      Collections.reverse(this.postfixStack);

      while(!this.postfixStack.isEmpty()) {
         String currentOp = (String)this.postfixStack.pop();
         if (!this.isOperator(currentOp.charAt(0))) {
            currentOp = currentOp.replace("~", "-");
            resultStack.push(currentOp);
         } else {
            String secondValue = (String)resultStack.pop();
            String firstValue = (String)resultStack.pop();
            firstValue = firstValue.replace("~", "-");
            secondValue = secondValue.replace("~", "-");
            BigDecimal tempResult = this.calculate(firstValue, secondValue, currentOp.charAt(0));
            resultStack.push(tempResult.toString());
         }
      }

      return Double.parseDouble((String)resultStack.pop());
   }

   private void prepare(String expression) {
      Stack<Character> opStack = new Stack();
      opStack.push(',');
      char[] arr = expression.toCharArray();
      int currentIndex = 0;
      int count = 0;

      for(int i = 0; i < arr.length; ++i) {
         char currentOp = arr[i];
         if (this.isOperator(currentOp)) {
            if (count > 0) {
               this.postfixStack.push(new String(arr, currentIndex, count));
            }

            char peekOp = (Character)opStack.peek();
            if (currentOp == ')') {
               while((Character)opStack.peek() != '(') {
                  this.postfixStack.push(String.valueOf(opStack.pop()));
               }

               opStack.pop();
            } else {
               while(currentOp != '(' && peekOp != ',' && this.compare(currentOp, peekOp)) {
                  this.postfixStack.push(String.valueOf(opStack.pop()));
                  peekOp = (Character)opStack.peek();
               }

               opStack.push(currentOp);
            }

            count = 0;
            currentIndex = i + 1;
         } else {
            ++count;
         }
      }

      if (count > 1 || count == 1 && !this.isOperator(arr[currentIndex])) {
         this.postfixStack.push(new String(arr, currentIndex, count));
      }

      while((Character)opStack.peek() != ',') {
         this.postfixStack.push(String.valueOf(opStack.pop()));
      }

   }

   private boolean isOperator(char c) {
      return c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')' || c == '%';
   }

   private boolean compare(char cur, char peek) {
      int offset = true;
      if (cur == '%') {
         cur = '/';
      }

      if (peek == '%') {
         peek = '/';
      }

      return this.operatPriority[peek - 40] >= this.operatPriority[cur - 40];
   }

   private BigDecimal calculate(String firstValue, String secondValue, char currentOp) {
      BigDecimal result;
      switch (currentOp) {
         case '%':
            result = NumberUtil.toBigDecimal(firstValue).remainder(NumberUtil.toBigDecimal(secondValue));
            break;
         case '&':
         case '\'':
         case '(':
         case ')':
         case ',':
         case '.':
         default:
            throw new IllegalStateException("Unexpected value: " + currentOp);
         case '*':
            result = NumberUtil.mul(firstValue, secondValue);
            break;
         case '+':
            result = NumberUtil.add(firstValue, secondValue);
            break;
         case '-':
            result = NumberUtil.sub(firstValue, secondValue);
            break;
         case '/':
            result = NumberUtil.div(firstValue, secondValue);
      }

      return result;
   }

   private static String transform(String expression) {
      expression = StrUtil.cleanBlank(expression);
      expression = StrUtil.removeSuffix(expression, "=");
      char[] arr = expression.toCharArray();

      for(int i = 0; i < arr.length; ++i) {
         if (arr[i] == '-') {
            if (i == 0) {
               arr[i] = '~';
            } else {
               char c = arr[i - 1];
               if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == 'E' || c == 'e') {
                  arr[i] = '~';
               }
            }
         }
      }

      if (arr[0] == '~' && arr.length > 1 && arr[1] == '(') {
         arr[0] = '-';
         return "0" + new String(arr);
      } else {
         return new String(arr);
      }
   }
}
