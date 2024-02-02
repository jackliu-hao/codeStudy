package javax.servlet.descriptor;

import java.util.Collection;

public interface JspConfigDescriptor {
   Collection<TaglibDescriptor> getTaglibs();

   Collection<JspPropertyGroupDescriptor> getJspPropertyGroups();
}
