package autofinanceschedule.pojo;

import autofinanceschedule.base.MainDB;

public abstract class OperationAPIBase {

	abstract public String dealData(MainDB db, String datestamp) throws Exception;
	
}
