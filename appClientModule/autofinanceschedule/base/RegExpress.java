package autofinanceschedule.base;

public class RegExpress {

	public RegExpress() {
		// TODO Auto-generated constructor stub
	}
	
	
	public final static String REG_CSV = "[^\",]+";
	
	public final static String REG_CSV1 = ",(.*?),";
	
	public final String REG_PAGEMAIN = "(<!-- 开始：具体的内容 -->)(.*?)(<!-- 结束：具体的内容 -->)";
	
	public final String REG_SHSTOCK = "<A href=.*?>([0-9]+)</td><td>(.*?)</td>";
	
	public final String REG_SZSTOCK = "";
	//to match the word
	public static final String EACH_WORD = "[a-zA-Z]+";
	
	//to match the tag of html
	public static final String HTML_TAG = "<.*?>";
	
	public static final String STOCK_CURRENTDATA_TOTALSTOCK = "(总股本)：<span>(.*?)</span>";
	
	public static final String STOCK_CURRENTDATA_TOTALVALUE = "(总市值)：<span>(.*?)</span>";
	
	public static final String STOCK_CURRENTDATA_PELYR = "(市盈率LYR/TTM)：<span>(.*?)</span>";
	
	public static final String STOCK_CURRENTDATA_PBTTM = "(市净率TTM)：<span>(.*?)</span>";
	
	public static final String STOCK_CURRENTDATA_PSTTM = "(市销率TTM)：<span>(.*?)</span>";
	
	public static final String STOCK_CURRENTDATA_CURRENTPRICCE = "(昨收)：<span>(.*?)</span>";
	
	
}
