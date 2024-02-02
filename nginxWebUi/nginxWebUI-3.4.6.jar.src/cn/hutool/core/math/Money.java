/*     */ package cn.hutool.core.math;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.File;
/*     */ import java.io.Serializable;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import java.util.Currency;
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
/*     */ public class Money
/*     */   implements Serializable, Comparable<Money>
/*     */ {
/*     */   private static final long serialVersionUID = -1004117971993390293L;
/*     */   public static final String DEFAULT_CURRENCY_CODE = "CNY";
/*  61 */   public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_EVEN;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  70 */   private static final int[] CENT_FACTORS = new int[] { 1, 10, 100, 1000 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long cent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Currency currency;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Money() {
/*  91 */     this(0.0D);
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
/*     */   
/*     */   public Money(long yuan, int cent) {
/* 104 */     this(yuan, cent, Currency.getInstance("CNY"));
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
/*     */ 
/*     */   
/*     */   public Money(long yuan, int cent, Currency currency) {
/* 118 */     this.currency = currency;
/*     */     
/* 120 */     if (0L == yuan) {
/* 121 */       this.cent = cent;
/*     */     } else {
/* 123 */       this.cent = yuan * getCentFactor() + (cent % getCentFactor());
/*     */     } 
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
/*     */   public Money(String amount) {
/* 136 */     this(amount, Currency.getInstance("CNY"));
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
/*     */   
/*     */   public Money(String amount, Currency currency) {
/* 149 */     this(new BigDecimal(amount), currency);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public Money(String amount, Currency currency, RoundingMode roundingMode) {
/* 164 */     this(new BigDecimal(amount), currency, roundingMode);
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
/*     */   public Money(double amount) {
/* 190 */     this(amount, Currency.getInstance("CNY"));
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
/*     */   public Money(double amount, Currency currency) {
/* 217 */     this.currency = currency;
/* 218 */     this.cent = Math.round(amount * getCentFactor());
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
/*     */   
/*     */   public Money(BigDecimal amount) {
/* 231 */     this(amount, Currency.getInstance("CNY"));
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
/*     */ 
/*     */   
/*     */   public Money(BigDecimal amount, RoundingMode roundingMode) {
/* 245 */     this(amount, Currency.getInstance("CNY"), roundingMode);
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
/*     */ 
/*     */   
/*     */   public Money(BigDecimal amount, Currency currency) {
/* 259 */     this(amount, currency, DEFAULT_ROUNDING_MODE);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public Money(BigDecimal amount, Currency currency, RoundingMode roundingMode) {
/* 274 */     this.currency = currency;
/* 275 */     this.cent = rounding(amount.movePointRight(currency.getDefaultFractionDigits()), roundingMode);
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
/*     */   public BigDecimal getAmount() {
/* 287 */     return BigDecimal.valueOf(this.cent, this.currency.getDefaultFractionDigits());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAmount(BigDecimal amount) {
/* 296 */     if (amount != null) {
/* 297 */       this.cent = rounding(amount.movePointRight(2), DEFAULT_ROUNDING_MODE);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCent() {
/* 307 */     return this.cent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Currency getCurrency() {
/* 316 */     return this.currency;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCentFactor() {
/* 325 */     return CENT_FACTORS[this.currency.getDefaultFractionDigits()];
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 345 */     return (other instanceof Money && equals((Money)other));
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Money other) {
/* 361 */     return (this.currency.equals(other.currency) && this.cent == other.cent);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 371 */     return (int)(this.cent ^ this.cent >>> 32L);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int compareTo(Money other) {
/* 390 */     assertSameCurrencyAs(other);
/* 391 */     return Long.compare(this.cent, other.cent);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean greaterThan(Money other) {
/* 407 */     return (compareTo(other) > 0);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Money add(Money other) {
/* 425 */     assertSameCurrencyAs(other);
/*     */     
/* 427 */     return newMoneyWithSameCurrency(this.cent + other.cent);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public Money addTo(Money other) {
/* 442 */     assertSameCurrencyAs(other);
/*     */     
/* 444 */     this.cent += other.cent;
/*     */     
/* 446 */     return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Money subtract(Money other) {
/* 462 */     assertSameCurrencyAs(other);
/*     */     
/* 464 */     return newMoneyWithSameCurrency(this.cent - other.cent);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public Money subtractFrom(Money other) {
/* 479 */     assertSameCurrencyAs(other);
/*     */     
/* 481 */     this.cent -= other.cent;
/*     */     
/* 483 */     return this;
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
/*     */ 
/*     */   
/*     */   public Money multiply(long val) {
/* 497 */     return newMoneyWithSameCurrency(this.cent * val);
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
/*     */   
/*     */   public Money multiplyBy(long val) {
/* 510 */     this.cent *= val;
/*     */     
/* 512 */     return this;
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
/*     */ 
/*     */   
/*     */   public Money multiply(double val) {
/* 526 */     return newMoneyWithSameCurrency(Math.round(this.cent * val));
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
/*     */ 
/*     */   
/*     */   public Money multiplyBy(double val) {
/* 540 */     this.cent = Math.round(this.cent * val);
/*     */     
/* 542 */     return this;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public Money multiply(BigDecimal val) {
/* 557 */     return multiply(val, DEFAULT_ROUNDING_MODE);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public Money multiplyBy(BigDecimal val) {
/* 572 */     return multiplyBy(val, DEFAULT_ROUNDING_MODE);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Money multiply(BigDecimal val, RoundingMode roundingMode) {
/* 588 */     BigDecimal newCent = BigDecimal.valueOf(this.cent).multiply(val);
/*     */     
/* 590 */     return newMoneyWithSameCurrency(rounding(newCent, roundingMode));
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Money multiplyBy(BigDecimal val, RoundingMode roundingMode) {
/* 606 */     BigDecimal newCent = BigDecimal.valueOf(this.cent).multiply(val);
/*     */     
/* 608 */     this.cent = rounding(newCent, roundingMode);
/*     */     
/* 610 */     return this;
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
/*     */ 
/*     */   
/*     */   public Money divide(double val) {
/* 624 */     return newMoneyWithSameCurrency(Math.round(this.cent / val));
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
/*     */ 
/*     */   
/*     */   public Money divideBy(double val) {
/* 638 */     this.cent = Math.round(this.cent / val);
/*     */     
/* 640 */     return this;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public Money divide(BigDecimal val) {
/* 655 */     return divide(val, DEFAULT_ROUNDING_MODE);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Money divide(BigDecimal val, RoundingMode roundingMode) {
/* 671 */     BigDecimal newCent = BigDecimal.valueOf(this.cent).divide(val, roundingMode);
/*     */     
/* 673 */     return newMoneyWithSameCurrency(newCent.longValue());
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
/*     */ 
/*     */ 
/*     */   
/*     */   public Money divideBy(BigDecimal val) {
/* 688 */     return divideBy(val, DEFAULT_ROUNDING_MODE);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Money divideBy(BigDecimal val, RoundingMode roundingMode) {
/* 704 */     BigDecimal newCent = BigDecimal.valueOf(this.cent).divide(val, roundingMode);
/*     */     
/* 706 */     this.cent = newCent.longValue();
/*     */     
/* 708 */     return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Money[] allocate(int targets) {
/* 724 */     Money[] results = new Money[targets];
/*     */     
/* 726 */     Money lowResult = newMoneyWithSameCurrency(this.cent / targets);
/* 727 */     Money highResult = newMoneyWithSameCurrency(lowResult.cent + 1L);
/*     */     
/* 729 */     int remainder = (int)this.cent % targets;
/*     */     int i;
/* 731 */     for (i = 0; i < remainder; i++) {
/* 732 */       results[i] = highResult;
/*     */     }
/*     */     
/* 735 */     for (i = remainder; i < targets; i++) {
/* 736 */       results[i] = lowResult;
/*     */     }
/*     */     
/* 739 */     return results;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public Money[] allocate(long[] ratios) {
/* 754 */     Money[] results = new Money[ratios.length];
/*     */     
/* 756 */     long total = 0L;
/*     */     
/* 758 */     for (long element : ratios) {
/* 759 */       total += element;
/*     */     }
/*     */     
/* 762 */     long remainder = this.cent;
/*     */     int i;
/* 764 */     for (i = 0; i < results.length; i++) {
/* 765 */       results[i] = newMoneyWithSameCurrency(this.cent * ratios[i] / total);
/* 766 */       remainder -= (results[i]).cent;
/*     */     } 
/*     */     
/* 769 */     for (i = 0; i < remainder; i++) {
/* 770 */       (results[i]).cent++;
/*     */     }
/*     */     
/* 773 */     return results;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 783 */     return getAmount().toString();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void assertSameCurrencyAs(Money other) {
/* 799 */     if (!this.currency.equals(other.currency)) {
/* 800 */       throw new IllegalArgumentException("Money math currency mismatch.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected long rounding(BigDecimal val, RoundingMode roundingMode) {
/* 812 */     return val.setScale(0, roundingMode).longValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Money newMoneyWithSameCurrency(long cent) {
/* 822 */     Money money = new Money(0.0D, this.currency);
/*     */     
/* 824 */     money.cent = cent;
/*     */     
/* 826 */     return money;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String dump() {
/* 837 */     return StrUtil.builder()
/* 838 */       .append("cent = ")
/* 839 */       .append(this.cent)
/* 840 */       .append(File.separatorChar)
/* 841 */       .append("currency = ")
/* 842 */       .append(this.currency)
/* 843 */       .toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCent(long cent) {
/* 852 */     this.cent = cent;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\math\Money.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */