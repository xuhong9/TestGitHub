package codegen.parent;

import java.io.Serializable;
import java.util.Map;

/**
 * 定义内部bean 统一接口
 * @author Administrator
 *
 */
public interface YakIbean extends  Serializable{
	/**
	 * 根据上下文属性信息初始化 默认值， 
	 * 注意此处对于已存在的值
	 * 不要强行再次复制
	 * @param contextProperty 上下文 属性信息
	 */
public void initDefaultValue(Map contextProperty)throws DatabaseException;
/**
 * 在业务上 严格就有 主-子属性 关系 
 * @return
 */
public Object[] getSubIbeans();
public void  setSubIbeans(Object[] values);
/**
 *  获取关联对象  初始实例
 */
public Object[] getSubIbeanInstance();

}
