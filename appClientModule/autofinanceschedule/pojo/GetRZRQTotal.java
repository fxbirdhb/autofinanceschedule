package autofinanceschedule.pojo;

import autofinanceschedule.base.MainDB;
import autofinanceschedule.func.Stock;

public class GetRZRQTotal extends OperationAPIBase {

	@Override
	public String dealData(MainDB db, String datestamp) throws Exception {
		
		Stock.updateCurrentRZRQ(db, 1);
		
		return "it is done";
	}

}
