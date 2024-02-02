package freemarker.ext.dom;

import freemarker.template.TemplateScalarModel;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;

class CharacterDataNodeModel extends NodeModel implements TemplateScalarModel {
   public CharacterDataNodeModel(CharacterData text) {
      super(text);
   }

   public String getAsString() {
      return ((CharacterData)this.node).getData();
   }

   public String getNodeName() {
      return this.node instanceof Comment ? "@comment" : "@text";
   }

   public boolean isEmpty() {
      return true;
   }
}
