package autofinanceschedule.pojo;


import autofinanceschedule.base.MainDB;
import autofinanceschedule.func.Stock;

public class GetCurrentPriceFromXueqiu extends OperationAPIBase {

	@Override
	public String dealData(MainDB db, String datestamp) throws Exception {
		
		String result = "It is done";
		
		//Stock.updateCurrentPrice(db, datestamp);
		
		return result;
	}

}
