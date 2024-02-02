package javax.mail.search;

import java.util.Date;

public abstract class DateTerm extends ComparisonTerm {
   protected Date date;
   private static final long serialVersionUID = 4818873430063720043L;

   protected DateTerm(int comparison, Date date) {
      this.comparison = comparison;
      this.date = date;
   }

   public Date getDate() {
      return new Date(this.date.getTime());
   }

   public int getComparison() {
      return this.comparison;
   }

   protected boolean match(Date d) {
      switch (this.comparison) {
         case 1:
            return d.before(this.date) || d.equals(this.date);
         case 2:
            return d.before(this.date);
         case 3:
            return d.equals(this.date);
         case 4:
            return !d.equals(this.date);
         case 5:
            return d.after(this.date);
         case 6:
            return d.after(this.date) || d.equals(this.date);
         default:
            return false;
      }
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof DateTerm)) {
         return false;
      } else {
         DateTerm dt = (DateTerm)obj;
         return dt.date.equals(this.date) && super.equals(obj);
      }
   }

   public int hashCode() {
      return this.date.hashCode() + super.hashCode();
   }
}
