package freemarker.core;

import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateCollectionModelEx;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateSequenceModel;
import java.util.Arrays;

public class UnexpectedTypeException extends TemplateException {
   public UnexpectedTypeException(Environment env, String description) {
      super(description, env);
   }

   UnexpectedTypeException(Environment env, _ErrorDescriptionBuilder description) {
      super((Throwable)null, env, (Expression)null, description);
   }

   UnexpectedTypeException(Expression blamed, TemplateModel model, String expectedTypesDesc, Class[] expectedTypes, Environment env) throws InvalidReferenceException {
      super((Throwable)null, env, blamed, newDescriptionBuilder(blamed, (String)null, model, expectedTypesDesc, expectedTypes, env));
   }

   UnexpectedTypeException(Expression blamed, TemplateModel model, String expectedTypesDesc, Class[] expectedTypes, String tip, Environment env) throws InvalidReferenceException {
      super((Throwable)null, env, blamed, newDescriptionBuilder(blamed, (String)null, model, expectedTypesDesc, expectedTypes, env).tip(tip));
   }

   UnexpectedTypeException(Expression blamed, TemplateModel model, String expectedTypesDesc, Class[] expectedTypes, Object[] tips, Environment env) throws InvalidReferenceException {
      super((Throwable)null, env, blamed, newDescriptionBuilder(blamed, (String)null, model, expectedTypesDesc, expectedTypes, env).tips(tips));
   }

   UnexpectedTypeException(String blamedAssignmentTargetVarName, TemplateModel model, String expectedTypesDesc, Class[] expectedTypes, Object[] tips, Environment env) throws InvalidReferenceException {
      super((Throwable)null, env, (Expression)null, newDescriptionBuilder((Expression)null, blamedAssignmentTargetVarName, model, expectedTypesDesc, expectedTypes, env).tips(tips));
   }

   private static _ErrorDescriptionBuilder newDescriptionBuilder(Expression blamed, String blamedAssignmentTargetVarName, TemplateModel model, String expectedTypesDesc, Class[] expectedTypes, Environment env) throws InvalidReferenceException {
      if (model == null) {
         throw InvalidReferenceException.getInstance(blamed, env);
      } else {
         _ErrorDescriptionBuilder errorDescBuilder = (new _ErrorDescriptionBuilder(unexpectedTypeErrorDescription(expectedTypesDesc, blamed, blamedAssignmentTargetVarName, model))).blame(blamed).showBlamer(true);
         if (model instanceof _UnexpectedTypeErrorExplainerTemplateModel) {
            Object[] tip = ((_UnexpectedTypeErrorExplainerTemplateModel)model).explainTypeError(expectedTypes);
            if (tip != null) {
               errorDescBuilder.tip(tip);
            }
         }

         if (model instanceof TemplateCollectionModel && (Arrays.asList(expectedTypes).contains(TemplateSequenceModel.class) || Arrays.asList(expectedTypes).contains(TemplateCollectionModelEx.class))) {
            errorDescBuilder.tip("As the problematic value contains a collection of items, you could convert it to a sequence like someValue?sequence. Be sure though that you won't have a large number of items, as all will be held in memory the same time.");
         }

         return errorDescBuilder;
      }
   }

   private static Object[] unexpectedTypeErrorDescription(String expectedTypesDesc, Expression blamed, String blamedAssignmentTargetVarName, TemplateModel model) {
      return new Object[]{"Expected ", new _DelayedAOrAn(expectedTypesDesc), ", but ", blamedAssignmentTargetVarName == null ? (blamed != null ? "this" : "the expression") : new Object[]{"assignment target variable ", new _DelayedJQuote(blamedAssignmentTargetVarName)}, " has evaluated to ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(model)), blamed != null ? ":" : "."};
   }
}
