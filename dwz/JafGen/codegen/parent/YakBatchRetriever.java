package codegen.parent;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.jaf.persistence.dao.exception.DatabaseExceptionFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.BatchRetriever;
import com.cfcc.jaf.persistence.jaform.parent.IDao;
import com.cfcc.jaf.persistence.jdbc.core.IDataSource;

/**
 * 执行一个DAO中的查询sql语句，但不限制它总的返回记录数，每次返回指定数量的记录 在访问数据库中的一个表时使用。
 * 使用BatchRetriever后，必须调用BatchRetriever.clearConnection释放占用的资源
 * 
 * 在runQuery(String where, List params)的时候，BatchRetriever打开一个ResultSet，
 * 如果有什么特殊操作，得以得到这个resultSet直接使用，但不建议这样做。
 * 
 * @author Rengm
 */
public class YakBatchRetriever extends BatchRetriever {
	private static Log _logger = LogFactory.getLog(YakBatchRetriever.class);
	private Class dtoClass;
	private IDao yakidao;
	private String Databasetype_yak;

	public YakBatchRetriever(IDataSource dataSource, String databaseType,
			IDao dao, int maxRows,Class dtoclass) {
		 super(dataSource,databaseType,dao,maxRows);
		 this.setDtoClass(dtoclass);
		 this.setYakidao(dao);
		 Databasetype_yak=databaseType;
	}

	public IDao getYakidao() {
		return yakidao;
	}

	public void setYakidao(IDao yakidao) {
		this.yakidao = yakidao;
	}

	public Class getDtoClass() {
		return dtoClass;
	}

	public void setDtoClass(Class dtoClass) {
		this.dtoClass = dtoClass;
	}
	 
	/**
	 * 获得记录查询结果的记录集，如果结果集中记录的数量很多，那么每次返回指定数量的记录
	 * 
	 * @param isLobSupport
	 *            是否支持lob
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List getResults(boolean isLobSupport ) throws JAFDatabaseException {
		try {
			List results=null ;
			if (yakidao instanceof YakIDao){
				 results =((YakIDao) yakidao).getResults(getResultSet(),this.getDtoClass(), _maxRows, isLobSupport);
			} else{
			 results = yakidao.getResults(getResultSet(), _maxRows, isLobSupport);
			}
			if (results != null && results.size() > 0) {

				if (results.size() < _maxRows) {
					_hasMore = false;
				} else {
					_hasMore = true;
				}

				return results;

			}
			_hasMore = false;
			return results;

		} catch (SQLException ex) {
			_logger.error("获取批量数据结果错误:" + ex.getMessage());

			throw DatabaseExceptionFactory.getException(  Databasetype_yak,
					"获取批量数据结果错误.", ex);
		}
	}
 

}
