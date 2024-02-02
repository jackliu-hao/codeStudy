package freemarker.core;

import freemarker.template.AdapterTemplateModel;
import freemarker.template.SimpleDate;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.DateUtil;
import freemarker.template.utility.UnrecognizedTimeZoneException;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

class BuiltInsForDates {
   private BuiltInsForDates() {
   }

   abstract static class AbstractISOBI extends BuiltInForDate {
      protected final Boolean showOffset;
      protected final int accuracy;

      protected AbstractISOBI(Boolean showOffset, int accuracy) {
         this.showOffset = showOffset;
         this.accuracy = accuracy;
      }

      protected void checkDateTypeNotUnknown(int dateType) throws TemplateException {
         if (dateType == 0) {
            throw new _MiscTemplateException((new _ErrorDescriptionBuilder(new Object[]{"The value of the following has unknown date type, but ?", this.key, " needs a value where it's known if it's a date (no time part), time, or date-time value:"})).blame(this.target).tip("Use ?date, ?time, or ?datetime to tell FreeMarker the exact type."));
         }
      }

      protected boolean shouldShowOffset(Date date, int dateType, Environment env) {
         if (dateType == 2) {
            return false;
         } else if (this.showOffset != null) {
            return this.showOffset;
         } else {
            return !(date instanceof Time) || _TemplateAPI.getTemplateLanguageVersionAsInt((TemplateObject)this) < _TemplateAPI.VERSION_INT_2_3_21;
         }
      }
   }

   static class iso_utc_or_local_BI extends AbstractISOBI {
      private final boolean useUTC;

      iso_utc_or_local_BI(Boolean showOffset, int accuracy, boolean useUTC) {
         super(showOffset, accuracy);
         this.useUTC = useUTC;
      }

      protected TemplateModel calculateResult(Date date, int dateType, Environment env) throws TemplateException {
         this.checkDateTypeNotUnknown(dateType);
         return new SimpleScalar(DateUtil.dateToISO8601String(date, dateType != 1, dateType != 2, this.shouldShowOffset(date, dateType, env), this.accuracy, this.useUTC ? DateUtil.UTC : (env.shouldUseSQLDTTZ(date.getClass()) ? env.getSQLDateAndTimeTimeZone() : env.getTimeZone()), env.getISOBuiltInCalendarFactory()));
      }
   }

   static class iso_BI extends AbstractISOBI {
      iso_BI(Boolean showOffset, int accuracy) {
         super(showOffset, accuracy);
      }

      protected TemplateModel calculateResult(Date date, int dateType, Environment env) throws TemplateException {
         this.checkDateTypeNotUnknown(dateType);
         return new Result(date, dateType, env);
      }

      class Result implements TemplateMethodModelEx {
         private final Date date;
         private final int dateType;
         private final Environment env;

         Result(Date date, int dateType, Environment env) {
            this.date = date;
            this.dateType = dateType;
            this.env = env;
         }

         public Object exec(List args) throws TemplateModelException {
            iso_BI.this.checkMethodArgCount(args, 1);
            TemplateModel tzArgTM = (TemplateModel)args.get(0);
            TimeZone tzArg;
            Object adaptedObj;
            if (tzArgTM instanceof AdapterTemplateModel && (adaptedObj = ((AdapterTemplateModel)tzArgTM).getAdaptedObject(TimeZone.class)) instanceof TimeZone) {
               tzArg = (TimeZone)adaptedObj;
            } else {
               if (!(tzArgTM instanceof TemplateScalarModel)) {
                  throw _MessageUtil.newMethodArgUnexpectedTypeException("?" + iso_BI.this.key, 0, "string or java.util.TimeZone", tzArgTM);
               }

               String tzName = EvalUtil.modelToString((TemplateScalarModel)tzArgTM, (Expression)null, (Environment)null);

               try {
                  tzArg = DateUtil.getTimeZone(tzName);
               } catch (UnrecognizedTimeZoneException var7) {
                  throw new _TemplateModelException(new Object[]{"The time zone string specified for ?", iso_BI.this.key, "(...) is not recognized as a valid time zone name: ", new _DelayedJQuote(tzName)});
               }
            }

            return new SimpleScalar(DateUtil.dateToISO8601String(this.date, this.dateType != 1, this.dateType != 2, iso_BI.this.shouldShowOffset(this.date, this.dateType, this.env), iso_BI.this.accuracy, tzArg, this.env.getISOBuiltInCalendarFactory()));
         }
      }
   }

   static class dateType_if_unknownBI extends BuiltIn {
      private final int dateType;

      dateType_if_unknownBI(int dateType) {
         this.dateType = dateType;
      }

      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel model = this.target.eval(env);
         if (model instanceof TemplateDateModel) {
            TemplateDateModel tdm = (TemplateDateModel)model;
            int tdmDateType = tdm.getDateType();
            return (TemplateModel)(tdmDateType != 0 ? tdm : new SimpleDate(EvalUtil.modelToDate(tdm, this.target), this.dateType));
         } else {
            throw BuiltInForDate.newNonDateException(env, model, this.target);
         }
      }

      protected TemplateModel calculateResult(Date date, int dateType, Environment env) throws TemplateException {
         return null;
      }
   }
}
