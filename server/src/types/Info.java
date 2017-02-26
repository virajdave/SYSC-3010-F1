package types;

import java.io.Serializable;
import java.util.List;

public class Info implements Serializable {
	private static final long serialVersionUID = -4593511951244989556L;
	
	List<InOut> inputs;
	List<InOut> outputs;
	List<Conf> config;
}
