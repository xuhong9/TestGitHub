package codegen.parent;

import java.util.Map;

import com.cfcc.jaf.persistence.jaform.parent.IDto;

public interface YakIDto extends IDto {
/**
 * 自动规则生成主键，主键规则生成器
 */
public 	void doAutoGenPk(YakIPkGen pkgen);
/**
 * 生成主键信息到map	
 */
public Map getPkToMap();	
/**
 * 	将map 赋值给主键
 */
public void doMapToPk(Map map);	
 
/**
 * 将dto 属性 信息 生成map	
 */
public Map getColumnValueToMap(); 	
/**
 * 将map 信息 存入dto  	
 */
public void doMapToColumnValue(Map map);	

/**
 * 获取dto columnNames
 * */
public Map getColumnNames();
/**
 * 获取TABLENAME
 * */
public  String getTableName();
 
}
