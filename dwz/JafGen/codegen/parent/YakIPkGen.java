package codegen.parent;

/**
 * 主键生成接口
 * @author Administrator
 *
 */
public interface YakIPkGen {
	/**
	 * 主键生成规则
	 * @param tablename
	 * @param columnName
	 * @param yakdto
	 * @return
	 */
public Object getGenValue(String tablename,String columnName,YakIDto yakdto) ;
}
