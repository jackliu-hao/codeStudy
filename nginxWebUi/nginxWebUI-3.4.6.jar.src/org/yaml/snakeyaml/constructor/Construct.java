package org.yaml.snakeyaml.constructor;

import org.yaml.snakeyaml.nodes.Node;

public interface Construct {
  Object construct(Node paramNode);
  
  void construct2ndStep(Node paramNode, Object paramObject);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\constructor\Construct.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */