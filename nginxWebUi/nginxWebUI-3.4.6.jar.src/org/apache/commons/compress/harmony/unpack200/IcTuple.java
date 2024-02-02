/*     */ package org.apache.commons.compress.harmony.unpack200;
/*     */ 
/*     */ import java.util.ArrayList;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IcTuple
/*     */ {
/*     */   private final int cIndex;
/*     */   private final int c2Index;
/*     */   private final int nIndex;
/*     */   private final int tIndex;
/*     */   public static final int NESTED_CLASS_FLAG = 65536;
/*     */   protected String C;
/*     */   protected int F;
/*     */   protected String C2;
/*     */   protected String N;
/*     */   private boolean predictSimple;
/*     */   private boolean predictOuter;
/*     */   private String cachedOuterClassString;
/*     */   private String cachedSimpleClassName;
/*     */   private boolean initialized;
/*     */   private boolean anonymous;
/*     */   private boolean outerIsAnonymous;
/*     */   private boolean member;
/*     */   private int cachedOuterClassIndex;
/*     */   private int cachedSimpleClassNameIndex;
/*     */   private boolean hashcodeComputed;
/*     */   private int cachedHashCode;
/*     */   
/*     */   public IcTuple(String C, int F, String C2, String N, int cIndex, int c2Index, int nIndex, int tIndex) {
/*  79 */     this.member = true;
/*  80 */     this.cachedOuterClassIndex = -1;
/*  81 */     this.cachedSimpleClassNameIndex = -1; this.C = C; this.F = F; this.C2 = C2;
/*     */     this.N = N;
/*     */     this.cIndex = cIndex;
/*     */     this.c2Index = c2Index;
/*     */     this.nIndex = nIndex;
/*     */     this.tIndex = tIndex;
/*     */     if (null == N)
/*     */       this.predictSimple = true; 
/*     */     if (null == C2)
/*     */       this.predictOuter = true; 
/*  91 */     initializeClassStrings(); } public boolean predicted() { return (this.predictOuter || this.predictSimple); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean nestedExplicitFlagSet() {
/* 100 */     return ((this.F & 0x10000) == 65536);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] innerBreakAtDollar(String className) {
/* 110 */     ArrayList<String> resultList = new ArrayList();
/* 111 */     int start = 0;
/* 112 */     int index = 0;
/* 113 */     while (index < className.length()) {
/* 114 */       if (className.charAt(index) <= '$') {
/* 115 */         resultList.add(className.substring(start, index));
/* 116 */         start = index + 1;
/*     */       } 
/* 118 */       index++;
/* 119 */       if (index >= className.length())
/*     */       {
/* 121 */         resultList.add(className.substring(start));
/*     */       }
/*     */     } 
/* 124 */     String[] result = new String[resultList.size()];
/* 125 */     for (int i = 0; i < resultList.size(); i++) {
/* 126 */       result[i] = resultList.get(i);
/*     */     }
/* 128 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String outerClassString() {
/* 137 */     return this.cachedOuterClassString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String simpleClassName() {
/* 146 */     return this.cachedSimpleClassName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String thisClassString() {
/* 155 */     if (predicted()) {
/* 156 */       return this.C;
/*     */     }
/*     */ 
/*     */     
/* 160 */     return this.C2 + "$" + this.N;
/*     */   }
/*     */   
/*     */   public boolean isMember() {
/* 164 */     return this.member;
/*     */   }
/*     */   
/*     */   public boolean isAnonymous() {
/* 168 */     return this.anonymous;
/*     */   }
/*     */   
/*     */   public boolean outerIsAnonymous() {
/* 172 */     return this.outerIsAnonymous;
/*     */   }
/*     */   
/*     */   private boolean computeOuterIsAnonymous() {
/* 176 */     String[] result = innerBreakAtDollar(this.cachedOuterClassString);
/* 177 */     if (result.length == 0) {
/* 178 */       throw new Error("Should have an outer before checking if it's anonymous");
/*     */     }
/*     */     
/* 181 */     for (int index = 0; index < result.length; index++) {
/* 182 */       if (isAllDigits(result[index])) {
/* 183 */         return true;
/*     */       }
/*     */     } 
/* 186 */     return false;
/*     */   }
/*     */   
/*     */   private void initializeClassStrings() {
/* 190 */     if (this.initialized) {
/*     */       return;
/*     */     }
/* 193 */     this.initialized = true;
/*     */     
/* 195 */     if (!this.predictSimple) {
/* 196 */       this.cachedSimpleClassName = this.N;
/*     */     }
/* 198 */     if (!this.predictOuter) {
/* 199 */       this.cachedOuterClassString = this.C2;
/*     */     }
/*     */ 
/*     */     
/* 203 */     String[] nameComponents = innerBreakAtDollar(this.C);
/* 204 */     if (nameComponents.length == 0);
/*     */ 
/*     */ 
/*     */     
/* 208 */     if (nameComponents.length == 1);
/*     */ 
/*     */ 
/*     */     
/* 212 */     if (nameComponents.length < 2) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 220 */     int lastPosition = nameComponents.length - 1;
/* 221 */     this.cachedSimpleClassName = nameComponents[lastPosition];
/* 222 */     this.cachedOuterClassString = "";
/* 223 */     for (int index = 0; index < lastPosition; index++) {
/* 224 */       this.cachedOuterClassString += nameComponents[index];
/* 225 */       if (isAllDigits(nameComponents[index])) {
/* 226 */         this.member = false;
/*     */       }
/* 228 */       if (index + 1 != lastPosition)
/*     */       {
/*     */ 
/*     */         
/* 232 */         this.cachedOuterClassString += '$';
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 237 */     if (!this.predictSimple) {
/* 238 */       this.cachedSimpleClassName = this.N;
/* 239 */       this.cachedSimpleClassNameIndex = this.nIndex;
/*     */     } 
/* 241 */     if (!this.predictOuter) {
/* 242 */       this.cachedOuterClassString = this.C2;
/* 243 */       this.cachedOuterClassIndex = this.c2Index;
/*     */     } 
/* 245 */     if (isAllDigits(this.cachedSimpleClassName)) {
/* 246 */       this.anonymous = true;
/* 247 */       this.member = false;
/* 248 */       if (nestedExplicitFlagSet())
/*     */       {
/* 250 */         this.member = true;
/*     */       }
/*     */     } 
/*     */     
/* 254 */     this.outerIsAnonymous = computeOuterIsAnonymous();
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isAllDigits(String nameString) {
/* 259 */     if (null == nameString) {
/* 260 */       return false;
/*     */     }
/* 262 */     for (int index = 0; index < nameString.length(); index++) {
/* 263 */       if (!Character.isDigit(nameString.charAt(index))) {
/* 264 */         return false;
/*     */       }
/*     */     } 
/* 267 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 272 */     StringBuffer result = new StringBuffer();
/* 273 */     result.append("IcTuple ");
/* 274 */     result.append('(');
/* 275 */     result.append(simpleClassName());
/* 276 */     result.append(" in ");
/* 277 */     result.append(outerClassString());
/* 278 */     result.append(')');
/* 279 */     return result.toString();
/*     */   }
/*     */   
/*     */   public boolean nullSafeEquals(String stringOne, String stringTwo) {
/* 283 */     if (null == stringOne) {
/* 284 */       return (null == stringTwo);
/*     */     }
/* 286 */     return stringOne.equals(stringTwo);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object object) {
/* 291 */     if (object == null || object.getClass() != getClass()) {
/* 292 */       return false;
/*     */     }
/* 294 */     IcTuple compareTuple = (IcTuple)object;
/*     */     
/* 296 */     if (!nullSafeEquals(this.C, compareTuple.C)) {
/* 297 */       return false;
/*     */     }
/*     */     
/* 300 */     if (!nullSafeEquals(this.C2, compareTuple.C2)) {
/* 301 */       return false;
/*     */     }
/*     */     
/* 304 */     if (!nullSafeEquals(this.N, compareTuple.N)) {
/* 305 */       return false;
/*     */     }
/* 307 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void generateHashCode() {
/* 314 */     this.hashcodeComputed = true;
/* 315 */     this.cachedHashCode = 17;
/* 316 */     if (this.C != null) {
/* 317 */       this.cachedHashCode = this.C.hashCode();
/*     */     }
/* 319 */     if (this.C2 != null) {
/* 320 */       this.cachedHashCode = this.C2.hashCode();
/*     */     }
/* 322 */     if (this.N != null) {
/* 323 */       this.cachedHashCode = this.N.hashCode();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 329 */     if (!this.hashcodeComputed) {
/* 330 */       generateHashCode();
/*     */     }
/* 332 */     return this.cachedHashCode;
/*     */   }
/*     */   
/*     */   public String getC() {
/* 336 */     return this.C;
/*     */   }
/*     */   
/*     */   public int getF() {
/* 340 */     return this.F;
/*     */   }
/*     */   
/*     */   public String getC2() {
/* 344 */     return this.C2;
/*     */   }
/*     */   
/*     */   public String getN() {
/* 348 */     return this.N;
/*     */   }
/*     */   
/*     */   public int getTupleIndex() {
/* 352 */     return this.tIndex;
/*     */   }
/*     */   
/*     */   public int thisClassIndex() {
/* 356 */     if (predicted()) {
/* 357 */       return this.cIndex;
/*     */     }
/* 359 */     return -1;
/*     */   }
/*     */   
/*     */   public int outerClassIndex() {
/* 363 */     return this.cachedOuterClassIndex;
/*     */   }
/*     */   
/*     */   public int simpleClassNameIndex() {
/* 367 */     return this.cachedSimpleClassNameIndex;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\IcTuple.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */