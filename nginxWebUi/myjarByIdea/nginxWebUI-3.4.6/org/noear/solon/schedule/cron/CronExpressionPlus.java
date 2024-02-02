package org.noear.solon.schedule.cron;

import java.text.ParseException;
import java.util.Collections;
import java.util.Set;

public class CronExpressionPlus extends CronExpression {
   public CronExpressionPlus(String cronExpression) throws ParseException {
      super(cronExpression);
   }

   public CronExpressionPlus(CronExpression expression) {
      super(expression);
   }

   public Set<Integer> getSeconds() {
      return Collections.unmodifiableSet(this.seconds);
   }

   public Set<Integer> getMinutes() {
      return Collections.unmodifiableSet(this.minutes);
   }

   public Set<Integer> getHours() {
      return Collections.unmodifiableSet(this.hours);
   }

   public Set<Integer> getDaysOfMonth() {
      return Collections.unmodifiableSet(this.daysOfMonth);
   }

   public Set<Integer> getMonths() {
      return Collections.unmodifiableSet(this.months);
   }

   public Set<Integer> getDaysOfWeek() {
      return Collections.unmodifiableSet(this.daysOfWeek);
   }

   public Set<Integer> getYears() {
      return Collections.unmodifiableSet(this.years);
   }
}
