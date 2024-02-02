package freemarker.core;

import freemarker.template.Template;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

/** @deprecated */
@Deprecated
public class FreeMarkerTree extends JTree {
   public FreeMarkerTree(Template template) {
      super(template.getRootTreeNode());
   }

   public void setTemplate(Template template) {
      this.setModel(new DefaultTreeModel(template.getRootTreeNode()));
      this.invalidate();
   }

   public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      return value instanceof TemplateElement ? ((TemplateElement)value).getDescription() : value.toString();
   }
}
