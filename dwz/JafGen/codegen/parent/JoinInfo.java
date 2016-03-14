package codegen.parent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rengm
 * 
 */
public class JoinInfo {
	/**
	 * 表名
	 */
	private String tableName;
	
	/**
	 * 别名，可不填，默认为转化为小写的表名
	 */
	private String alias;
	
	/**
	 * 关联字段，指传入的Sql所属的表的关联字段
	 */
	private String joinCol;
	
	/**
	 * this.tableName表中被关联的字段，写成传入sql中的表名.字段名
	 */
	private String selfCol;
	
	/**
	 * 需要查出来的属于this.tableName的字段，可以写成"字段名 [as] 字段别名"
	 */
	private List<String> additionCols;
	
	/**
	 * 针对关联表的查询条件
	 */
	private String additionWhereCon; 

	public JoinInfo(String tableName, String alias, String joinCol,
			String selfCol, List<String> additionCols ,String additionWhereCon) {
		this.tableName = (null == tableName ? "" : tableName);
		if (null == alias || alias.length() == 0) {
			this.alias = this.tableName;
		} else {
			this.alias = alias;
		}
		this.joinCol = (null == joinCol ? "" : joinCol);
		this.selfCol = (null == selfCol ? "" : selfCol);
		if (null == additionCols) {
			this.additionCols = new ArrayList<String>();
		} else {
			this.additionCols = additionCols;
		}
		
		this.additionWhereCon = additionWhereCon;
	}

	public JoinInfo(String tableName, String joinCol, String selfCol,
			List<String> additionCols) {
	}
	
	public JoinInfo(String tableName, String alias, String joinCol,
			String selfCol, List<String> additionCols)
	{
		this.tableName = (null == tableName ? "" : tableName);
		if (null == alias || alias.length() == 0) {
			this.alias = this.tableName;
		} else {
			this.alias = alias;
		}
		this.joinCol = (null == joinCol ? "" : joinCol);
		this.selfCol = (null == selfCol ? "" : selfCol);
		if (null == additionCols) {
			this.additionCols = new ArrayList<String>();
		} else {
			this.additionCols = additionCols;
		}
	}

	public JoinInfo() {
		additionCols = new ArrayList<String>();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;

		if (!(alias != null && alias.length() > 0)) {
			alias = tableName.toLowerCase();
		}
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getJoinCol() {
		return joinCol;
	}

	public void setJoinCol(String joinCol) {
		this.joinCol = joinCol;
	}

	public String getSelfCol() {
		return selfCol;
	}

	public void setSelfCol(String selfCol) {
		this.selfCol = selfCol;
	}

	public List<String> getAdditionCols() {
		return additionCols;
	}

	public void setAdditionCols(List<String> additionCols) {
		this.additionCols = additionCols;
	}

	public String getAdditionWhereCon() {
		return additionWhereCon;
	}

	public void setAdditionWhereCon(String additionWhereCon) {
		this.additionWhereCon = additionWhereCon;
	}
}