/*     */ package cn.hutool.core.codec;
/*     */ 
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class Morse
/*     */ {
/*  20 */   private static final Map<Integer, String> ALPHABETS = new HashMap<>();
/*  21 */   private static final Map<String, Integer> DICTIONARIES = new HashMap<>();
/*     */   
/*     */   private final char dit;
/*     */   
/*     */   private final char dah;
/*     */   
/*     */   private final char split;
/*     */   
/*     */   private static void registerMorse(Character abc, String dict) {
/*  30 */     ALPHABETS.put(Integer.valueOf(abc.charValue()), dict);
/*  31 */     DICTIONARIES.put(dict, Integer.valueOf(abc.charValue()));
/*     */   }
/*     */ 
/*     */   
/*     */   static {
/*  36 */     registerMorse(Character.valueOf('A'), "01");
/*  37 */     registerMorse(Character.valueOf('B'), "1000");
/*  38 */     registerMorse(Character.valueOf('C'), "1010");
/*  39 */     registerMorse(Character.valueOf('D'), "100");
/*  40 */     registerMorse(Character.valueOf('E'), "0");
/*  41 */     registerMorse(Character.valueOf('F'), "0010");
/*  42 */     registerMorse(Character.valueOf('G'), "110");
/*  43 */     registerMorse(Character.valueOf('H'), "0000");
/*  44 */     registerMorse(Character.valueOf('I'), "00");
/*  45 */     registerMorse(Character.valueOf('J'), "0111");
/*  46 */     registerMorse(Character.valueOf('K'), "101");
/*  47 */     registerMorse(Character.valueOf('L'), "0100");
/*  48 */     registerMorse(Character.valueOf('M'), "11");
/*  49 */     registerMorse(Character.valueOf('N'), "10");
/*  50 */     registerMorse(Character.valueOf('O'), "111");
/*  51 */     registerMorse(Character.valueOf('P'), "0110");
/*  52 */     registerMorse(Character.valueOf('Q'), "1101");
/*  53 */     registerMorse(Character.valueOf('R'), "010");
/*  54 */     registerMorse(Character.valueOf('S'), "000");
/*  55 */     registerMorse(Character.valueOf('T'), "1");
/*  56 */     registerMorse(Character.valueOf('U'), "001");
/*  57 */     registerMorse(Character.valueOf('V'), "0001");
/*  58 */     registerMorse(Character.valueOf('W'), "011");
/*  59 */     registerMorse(Character.valueOf('X'), "1001");
/*  60 */     registerMorse(Character.valueOf('Y'), "1011");
/*  61 */     registerMorse(Character.valueOf('Z'), "1100");
/*     */     
/*  63 */     registerMorse(Character.valueOf('0'), "11111");
/*  64 */     registerMorse(Character.valueOf('1'), "01111");
/*  65 */     registerMorse(Character.valueOf('2'), "00111");
/*  66 */     registerMorse(Character.valueOf('3'), "00011");
/*  67 */     registerMorse(Character.valueOf('4'), "00001");
/*  68 */     registerMorse(Character.valueOf('5'), "00000");
/*  69 */     registerMorse(Character.valueOf('6'), "10000");
/*  70 */     registerMorse(Character.valueOf('7'), "11000");
/*  71 */     registerMorse(Character.valueOf('8'), "11100");
/*  72 */     registerMorse(Character.valueOf('9'), "11110");
/*     */     
/*  74 */     registerMorse(Character.valueOf('.'), "010101");
/*  75 */     registerMorse(Character.valueOf(','), "110011");
/*  76 */     registerMorse(Character.valueOf('?'), "001100");
/*  77 */     registerMorse(Character.valueOf('\''), "011110");
/*  78 */     registerMorse(Character.valueOf('!'), "101011");
/*  79 */     registerMorse(Character.valueOf('/'), "10010");
/*  80 */     registerMorse(Character.valueOf('('), "10110");
/*  81 */     registerMorse(Character.valueOf(')'), "101101");
/*  82 */     registerMorse(Character.valueOf('&'), "01000");
/*  83 */     registerMorse(Character.valueOf(':'), "111000");
/*  84 */     registerMorse(Character.valueOf(';'), "101010");
/*  85 */     registerMorse(Character.valueOf('='), "10001");
/*  86 */     registerMorse(Character.valueOf('+'), "01010");
/*  87 */     registerMorse(Character.valueOf('-'), "100001");
/*  88 */     registerMorse(Character.valueOf('_'), "001101");
/*  89 */     registerMorse(Character.valueOf('"'), "010010");
/*  90 */     registerMorse(Character.valueOf('$'), "0001001");
/*  91 */     registerMorse(Character.valueOf('@'), "011010");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Morse() {
/* 102 */     this('.', '-', '/');
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Morse(char dit, char dah, char split) {
/* 113 */     this.dit = dit;
/* 114 */     this.dah = dah;
/* 115 */     this.split = split;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String encode(String text) {
/* 125 */     Assert.notNull(text, "Text should not be null.", new Object[0]);
/*     */     
/* 127 */     text = text.toUpperCase();
/* 128 */     StringBuilder morseBuilder = new StringBuilder();
/* 129 */     int len = text.codePointCount(0, text.length());
/* 130 */     for (int i = 0; i < len; i++) {
/* 131 */       int codePoint = text.codePointAt(i);
/* 132 */       String word = ALPHABETS.get(Integer.valueOf(codePoint));
/* 133 */       if (word == null) {
/* 134 */         word = Integer.toBinaryString(codePoint);
/*     */       }
/* 136 */       morseBuilder.append(word.replace('0', this.dit).replace('1', this.dah)).append(this.split);
/*     */     } 
/* 138 */     return morseBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String decode(String morse) {
/* 148 */     Assert.notNull(morse, "Morse should not be null.", new Object[0]);
/*     */     
/* 150 */     char dit = this.dit;
/* 151 */     char dah = this.dah;
/* 152 */     char split = this.split;
/* 153 */     if (false == StrUtil.containsOnly(morse, new char[] { dit, dah, split })) {
/* 154 */       throw new IllegalArgumentException("Incorrect morse.");
/*     */     }
/* 156 */     List<String> words = StrUtil.split(morse, split);
/* 157 */     StringBuilder textBuilder = new StringBuilder();
/*     */     
/* 159 */     for (String word : words) {
/* 160 */       if (StrUtil.isEmpty(word)) {
/*     */         continue;
/*     */       }
/* 163 */       word = word.replace(dit, '0').replace(dah, '1');
/* 164 */       Integer codePoint = DICTIONARIES.get(word);
/* 165 */       if (codePoint == null) {
/* 166 */         codePoint = Integer.valueOf(word, 2);
/*     */       }
/* 168 */       textBuilder.appendCodePoint(codePoint.intValue());
/*     */     } 
/* 170 */     return textBuilder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\codec\Morse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */