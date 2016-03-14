package codegen.parent;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.SqlTimestampConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.BatchRetriever;
import com.cfcc.jaf.persistence.jaform.config.OrmapFactory;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutorFactory;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.CommonQto;
import com.cfcc.jaf.persistence.util.SqlUtil;
 


public class DatabaseFacade {
	private static Log log = LogFactory.getLog(DatabaseFacade.class);
	/** 时间标准格式 */
	public static final String STAND_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	static{
		// 进行类型注册。
		ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
		ConvertUtils.register(new SqlTimestampConverter(null), Timestamp.class);
		
		// 增加对oracle数据库支持
		ConvertUtils.register(new Converter() {

			public Object convert(Class type, Object value) {
				if (value == null) {
					return null;
				}
				if (value instanceof java.sql.Timestamp) {
					return new java.sql.Date(((java.sql.Timestamp) value).getTime());
				} else if (value instanceof String && ((String)value).length()>=10) {
					return java.sql.Date.valueOf(((String) value).substring(0, 10));
				}
				return value;
			}
		}, java.sql.Date.class); 
	}
	
	/**
	 * SQL类型转换成Java类型
	 */

	public static DatabaseFacade getDb(String dbName) {
		return (DatabaseFacade) ContextFactory.getApplicationContext().getBean(
				DbConstants.DATABASE_FACADE_DB_PREFIX + dbName);

	}

	public static DatabaseFacade getODB() {

		return (DatabaseFacade) ContextFactory.getApplicationContext().getBean(
				DbConstants.DATABASE_FACADE_DB_PREFIX + DbConstants.DB_NAME);
	}
	 
	IbeanOrmTemplate ormTemplate;

	SQLExecutorFactory sqlExecutorFactory;

	/**
	 * 
	 * 数据库增加一条记录
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void create(IDto _dto) throws JAFDatabaseException { 
		ormTemplate.create(_dto);
	}
	
	/**
	 * 新增加是否对ibean进行全部插入
	 */
	public void createYakIbean(IDto dto,Map contextmap) throws JAFDatabaseException {
		ormTemplate.create(dto, false, false, false, true, contextmap);
	}

	/**
	 * 数据库增加一条记录,返回增加的结果
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto createWithResult(IDto _dto) throws JAFDatabaseException {
		 IDto res= ormTemplate.createWithResult(_dto);
		return res;

	}

	/**
	 * 数据库增加一条记录,并返回带自增序列的一条完整的记录
	 * 
	 * @param _dto
	 * @return IDto
	 * @throws JAFDatabaseException
	 */
	public IDto createForGenerateKeyOnly(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.createWithResultForGeneratedKeyOnly(_dto);
	}

	/**
	 * 通过主键删除数据库中的一条记录
	 * 
	 * @param _pk
	 * @throws JAFDatabaseException
	 */
	public void delete(IPK _pk) throws JAFDatabaseException {
		ormTemplate.delete(_pk);

	}

	/**
	 * 通过需要删除的对象删除数据库中的一条记录
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void delete(IDto _dto) throws JAFDatabaseException {
		ormTemplate.delete(_dto);

	}

	/**
	 * 检查更新状态确定删除数据库中的一条记录
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void deleteWithCheck(IDto _dto) throws JAFDatabaseException {
		ormTemplate.deleteWithCheck(_dto);
		 
	}

	/**
	 * 检查更新状态确定删除数据库中的一组记录
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void deleteWithCheck(IDto[] _dtos) throws JAFDatabaseException {
		ormTemplate.deleteWithCheck(_dtos);
	}

	/**
	 * 检查更新状态进行及联删除数据库中的记录（适用于父子表的结构）
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void deleteCascadeWithCheck(IDto _dto) throws JAFDatabaseException {
		ormTemplate.deleteCascadeWithCheck(_dto);

	}

	/**
	 * 通过父表中的主键进行及联删除数据库中的记录（适用于父子表的结构）不检查更新状态
	 * 
	 * @param _pk
	 * @throws JAFDatabaseException
	 */
	public void deleteCascade(IPK _pk) throws JAFDatabaseException {
		ormTemplate.deleteCascade(_pk);

	}

	/**
	 * 通过需要删除的数据进行及联删除数据库中的记录（适用于父子表的结构）,不检查更新状态
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void deleteCascade(IDto _dto) throws JAFDatabaseException {
		ormTemplate.deleteCascade(_dto);

	}

	/**
	 * 通过主键查找一条记录
	 * 
	 * @param _pk
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto find(IPK _pk) throws JAFDatabaseException {
		return ormTemplate.find(_pk);

	}
	
	/**
	 * 通过主键查找一条记录
	 * 
	 * @param _pk
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto find(IPK _pk,boolean isLobSupport) throws JAFDatabaseException {
		return ormTemplate.find(_pk, true);

	}
	
	/**
	 * 通过主键查找IBEAN全部记录
	 * 
	 * @param _pk
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto findYakIbean(IPK _pk, boolean isLobSupport,boolean isIbeanCascade,Class _dtoClass,boolean dtocascade) throws JAFDatabaseException {
         return ormTemplate.find(_pk, isLobSupport, isIbeanCascade, _dtoClass,dtocascade);
	}
	
	/**
	 * 通过主键查找IBEAN全部记录
	 * 
	 * @param _pk
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto[] findYakIbean(IPK[] _pk, boolean isLobSupport,boolean isIbeanCascade,Class _dtoClass,boolean dtocascade) throws JAFDatabaseException {
         return ormTemplate.find(_pk, isLobSupport, isIbeanCascade, _dtoClass,dtocascade);
	}
	/**
	 * 分页查询-ibean进行全部查询
	 * return ArrayList<_dtoClass>
	 * */
	public List findYakIbean(Class _dtoClass, String where, List params, int start,
			int size,boolean isIbeanCascade) throws JAFDatabaseException {
		return ormTemplate.find(_dtoClass, where, params, start, size, isIbeanCascade);
	}
	
	/**
	 * ibean进行全部查询
	 * return ArrayList<_dtoClass>
	 * */
	public List findYakIbean(Class _dtoClass, String where, List params) throws JAFDatabaseException {
		return ormTemplate.find(_dtoClass, where, params, 1, ormTemplate.maxRows_yak, true);
	}
	/**
	 * ibean进行全部查询 是否连带查询
	 * @param _dtoClass
	 * @param where
	 * @param params
	 * @param isIbeanCascade
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List findYakIbean(Class _dtoClass, String where, List params,boolean isIbeanCascade) throws JAFDatabaseException {
		return ormTemplate.find(_dtoClass, where, params, 1, ormTemplate.maxRows_yak, isIbeanCascade);
	}
	
	/**
	 * 分页查询
	 * return ArrayList<_dtoClass>
	 * */
	public List find(Class _dtoClass, String where, List params, int start,
			int size) throws JAFDatabaseException {
		return ormTemplate.find(_dtoClass, where, params, start, size);
	}

	/**
	 * 通过数据查找一条记录
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto find(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.find(_dto);
	}
	/**
	 * 查询记录并增加悲观锁
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto findForUpdate(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.findForUpdate(_dto.getPK(),false);
	}
	/**
	 * 使用 悲观锁 ,校验 时间戳
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public void  checkForUpdate(IDto _dto) throws JAFDatabaseException {
		  ormTemplate.checkForUpdate(_dto );
	}
	/**
	 * 查询大字段
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List find(String sql,Class _dtoclass,boolean isLobSupport) throws JAFDatabaseException {
		return ormTemplate.find(sql,_dtoclass,isLobSupport);
	}
	/**
	 * 通过数据查找一条记录--大字段查询
	 * 	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto find(IDto _dto,boolean isLobSupport) throws JAFDatabaseException {
		return ormTemplate.find(_dto,isLobSupport);
	}
	/** 
	 * 通过查询回填数据
	 * @param _dto
	 * @param isLobSupport
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto findBackfill(IDto _dto,boolean isLobSupport) throws JAFDatabaseException{
		return ormTemplate.findBackfill(_dto.getPK(), isLobSupport, _dto);
	}
 
	/**
	 * 通过父表中的主键进行及联查找数据库中的记录（适用于父子表的结构）
	 * 
	 * @param _pk
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto findCascade(IPK _pk) throws JAFDatabaseException {

		return ormTemplate.findCascade(_pk);

	}

	/**
	 * 通过父表中的数据进行及联查找数据库中的记录（适用于父子表的结构）
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto findCascade(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.findCascade(_dto);

	}
	/**
	 * 根据Dto中设置的条件查找相应的记录并返回获取的记录集,DTO 中无条件,返回全数据
	 * 
	 * @param _dto
	 * @return List
	 * @throws JAFDatabaseException
	 */
	public List findRsByDto(IDto _dto) throws JAFDatabaseException {
      return ormTemplate.findRsByDto(_dto);
   }


	/**
	 * 更新数据库中的一条记录
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void update(IDto _dto) throws JAFDatabaseException {
		ormTemplate.update(_dto);
	}
	/**
	 * 更新数据库中的一条记录
	 * 
	 * @param _dto
	 * @param isLobSupport=true 更新大字段
	 * @throws JAFDatabaseException
	 */
	public void update(IDto _dto,boolean isLobSupport) throws JAFDatabaseException {
		ormTemplate.update(_dto,isLobSupport);
	}

	/**
	 * 更新数据库中的一批记录
	 * 
	 * @param _dtos
	 * @throws JAFDatabaseException
	 */
	public void update(IDto[] _dtos) throws JAFDatabaseException {
		if (_dtos != null && _dtos.length > 0) {
			ormTemplate.update(_dtos);
			 
		}

	}

	/**
	 * 更新数据库中的一条记录,返回更新结果
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto updateWithResult(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.updateWithResult(_dto);

	}

	/**
	 * 更新数据库中的一批记录,返回更新结果
	 * 
	 * @param _dtos
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto[] updateWithResult(IDto[] _dtos) throws JAFDatabaseException {
		if (_dtos != null && _dtos.length > 0) {
			return ormTemplate.updateWithResult(_dtos);
		}
		return null;

	}

	/**
	 * 通过检查更新状态进行更新数据库中的一条记录
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void updateWithCheck(IDto _dto) throws JAFDatabaseException {
		ormTemplate.updateWithCheck(_dto);

	}
	
	/**
	 * 通过检查更新状态进行更新数据库中的一批记录
	 * 
	 * @param _dtos
	 * @throws JAFDatabaseException
	 */
	public void updateWithCheck(IDto[] _dtos) throws JAFDatabaseException {
		if (_dtos != null && _dtos.length > 0) {
			ormTemplate.updateWithCheck(_dtos);
		}

	}

	/**
	 * 通过检查更新状态进行更新数据库中的一条记录，返回更新结果
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto updateWithResultCheck(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.updateWithResultCheck(_dto);

	}

	/**
	 * 通过检查更新状态进行更新数据库中的一批记录，返回更新结果
	 * 
	 * @param _dtos
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto[] updateWithResultCheck(IDto[] _dtos) throws JAFDatabaseException {
		if (_dtos != null && _dtos.length > 0) {
			return ormTemplate.updateWithResultCheck(_dtos);
		}
		return null;

	}

	/**
	 * 及联更新数据库中的记录（适用于父子表的结构）
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void updateCascade(IDto _dto) throws JAFDatabaseException {
		ormTemplate.updateCascade(_dto);

	}

	/**
	 * 及联更新数据库中的一批记录（适用于父子表的结构）
	 * 
	 * @param _dtos
	 * @throws JAFDatabaseException
	 */
	public void updateCascade(IDto[] _dtos) throws JAFDatabaseException {
		if (_dtos != null && _dtos.length > 0) {
			ormTemplate.updateCascade(_dtos);
		}

	}

	/**
	 * 及联更新数据库中的记录（适用于父子表的结构），返回更新结果
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto updateCascadeWithResult(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.updateCascadeWithResult(_dto);

	}

	/**
	 * 及联更新数据库中的一批记录（适用于父子表的结构），返回更新结果
	 * 
	 * @param _dtos
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto[] updateCascadeWithResult(IDto[] _dtos) throws JAFDatabaseException {

		if (_dtos != null && _dtos.length > 0) {
			return ormTemplate.updateCascadeWithResult(_dtos);
		}
		return null;

	}

	/**
	 * 通过检查更新状态进行及联更新数据库中的记录（适用于父子表的结构）
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void updateCascadeWithCheck(IDto _dto) throws JAFDatabaseException {
		ormTemplate.updateCascadeWithCheck(_dto);

	}

	/**
	 * 通过检查更新状态进行及联更新数据库中的一批记录（适用于父子表的结构）
	 * 
	 * @param _dtos
	 * @throws JAFDatabaseException
	 */
	public void updateCascadeWithCheck(IDto[] _dtos) throws JAFDatabaseException {
		if (_dtos != null && _dtos.length > 0) {
			ormTemplate.updateCascadeWithCheck(_dtos);
		}

	}

	/**
	 * 通过检查更新状态进行及联更新数据库中的记录（适用于父子表的结构），返回更新结果
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto updateCascadeWithResultCheck(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.updateCascadeWithResultCheck(_dto);

	}

	/**
	 * 通过检查更新状态进行及联更新数据库中的一批记录（适用于父子表的结构），返回更新结果
	 * 
	 * @param _dtos
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto[] updateCascadeWithResultCheck(IDto[] _dtos) throws JAFDatabaseException {

		if (_dtos != null && _dtos.length > 0) {
			return ormTemplate.updateCascadeWithResultCheck(_dtos);

		}

		return null;
	}

	/**
	 * 根据SQL查询返回多条值，值以IDto的方式存放在Collection中
	 * 
	 * @param sql
	 *            查询条件
	 * @param dtoClass
	 *            需要查询IDto对象
	 * @return
	 * @throws JAFDatabaseException
	 */
	public Collection findValuesBySQL(String sql, Class dtoClass) throws JAFDatabaseException {
		// 进行类型注册。
		ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
		ConvertUtils.register(new SqlTimestampConverter(null), Timestamp.class);
		SQLResults res = null;
		SQLExecutor sqlExec = sqlExecutorFactory.getSQLExecutor();
		
		res = sqlExec.runQueryCloseCon(sql, dtoClass);

		if (res.getDtoCollection() == null && res.getDtoCollection().size() == 0) {
			return null;
		}
		return res.getDtoCollection();

	}

	/**
	 * Retrive batch record from Database. it only find the first layer dto 查询
	 * 批量数据， 如果where 子句为空则查询所有的数据
	 * 
	 * @param _dtoClass
	 * @param where
	 * @param params
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List find(Class _dtoClass, String where, List params) throws JAFDatabaseException {
		return ormTemplate.find(_dtoClass, where, params);
	}

	/**
	 * 查询批量数据,如果where 子句为空则查询所有的数据 支持for update
	 * 
	 * @param _dtoClass
	 * @param where
	 * @param params
	 * @return
	 * @throws JAFDatabaseException
	 */
	// public List findForUpdate(Class _dtoClass, String where, List params)
	// throws JAFDatabaseException {
	// BatchRetriever retriever =
	// DatabaseFacade.getODB().getBatchRetriever(_dtoClass);
	// retriever.setResultSetConcurrency(ResultSet.CONCUR_UPDATABLE);// 支持for
	// update
	// retriever.runQuery(where, params);
	// return retriever.getResults();
	//
	// }
	/**
	 * Retrive batch record from Database. it only find the first layer dto 查询
	 * 批量数据， 如果where 子句为空则查询所有的数据，使用脏读方式查询
	 * 
	 * @param _dtoClass
	 * @param where
	 * @param params
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List findWithUR(Class _dtoClass, String where, List params) throws JAFDatabaseException {
		if (where == null) {
			where = "";
		}
		return ormTemplate.find(_dtoClass, where, params);
	}

	/**
	 * 根据传入的CommonQto对象查询相应的记录
	 * 
	 * @param _dtoClass
	 * @param _qto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List find(Class _dtoClass, CommonQto _qto) throws JAFDatabaseException {
		if (_qto == null) {
			return ormTemplate.find(_dtoClass, null, null);
		} else {
			return ormTemplate.find(_dtoClass, _qto.getSWhereClause(), _qto.getLParams());
		}
	}

	/**
	 * 根据传入的CommonQto对象查询相应的记录,使用脏读方式
	 * 
	 * @param _dtoClass
	 * @param _qto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List findWithUR(Class _dtoClass, CommonQto _qto) throws JAFDatabaseException {
		if (_qto == null) {
			return ormTemplate.find(_dtoClass, "");
		} else {
			return ormTemplate.find(_dtoClass, _qto.getSWhereClause() + "", _qto.getLParams());
		}
	}

	/**
	 * Retrive batch record from Database. it only find the first layer dto 查询
	 * 批量数据， 如果where 子句为空则查询所有的数据
	 * 
	 * @param _dtoClass
	 * @param where
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List find(Class _dtoClass, String where) throws JAFDatabaseException {
		return ormTemplate.find(_dtoClass, where);
	}

	/**
	 * Retrive batch record from Database. it only find the first layer dto 查询
	 * 批量数据， 如果where 子句为空则查询所有的数据,使用脏读方式查询
	 * 
	 * @param _dtoClass
	 * @param where
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List findWithUR(Class _dtoClass, String where) throws JAFDatabaseException {
		if (where == null) {
			where = " ";
		}
		return ormTemplate.find(_dtoClass, where);
	}

	/**
	 * Retrive count from Database. 如果where子句为则查询表的数据总数
	 * 
	 * @param _dtoClass
	 *            dto类
	 * @param where
	 *            查询条件
	 * @param params
	 *            查询条件的参数集合
	 * @return 满足查询条件的数据总数
	 * @throws JAFDatabaseException
	 */
	public int countFind(Class _dtoClass, String where, List params) throws JAFDatabaseException {
		if (where == null) {
			where = "";
		}
		return ormTemplate.countFind(_dtoClass, where, params,0);
	}

	/**
	 * 得到某个查询语句执行结果的返回记录条数
	 * 
	 * @param _dtoClass
	 *            查询结果对应的DTO对象
	 * @param _qto
	 *            查询条件对象
	 * @return 满足查询条件的数据总数
	 * @throws JAFDatabaseException
	 */
	public int countFind(Class _dtoClass, CommonQto _qto) throws JAFDatabaseException {
		return ormTemplate.countFind(_qto, _dtoClass);
	}

	/**
	 * 根据DTO对象查询，并分页。客户端查询使用 此分页功能仅适用于单表查询，查询条件在此表对应的dto中 added by Ren Yong,
	 * 2008-03-15
	 * 
	 * @param dto
	 *            表DTO的查询条件
	 * @param request
	 *            分页请求信息
	 * @return PageResponse
	 * @throws JAFDatabaseException
	 */
	public PageResponse findByDtoPaging(IDto dto, PageRequest request) throws JAFDatabaseException {
		CommonQto qto = SqlUtil.IDto2CommonQto(dto);
		PageResponse response = new PageResponse(request);
		if (response.getTotalCount() == 0) {
			// 初始化总记录数
			int totalCount = ormTemplate.countFind(qto, dto.getClass());
			response.setTotalCount(totalCount);
		}
		String tableName = OrmapFactory.getTableName(dto.getClass().getName());
		String sql = "select * from " + tableName;
		SQLExecutor sqlExe = getSqlExecutorFactory().getSQLExecutor();
		if (qto != null) {
			sql += qto.getSWhereClause();
			sqlExe.addParam(qto.getLParams());
		}
		SQLResults res = sqlExe.runQueryCloseCon(sql, dto.getClass(), request.getStartPosition(),
				request.getPageSize(), true);
		List list = new ArrayList();
		list.addAll(res.getDtoCollection());
		response.getData().clear();
		response.setData(list);
		return response;
	}

	/**
	 * 获取批量查询 BatchRetriever
	 * 
	 * @param _dtoClass
	 * @return
	 */
	public BatchRetriever getBatchRetriever(Class _dtoClass) {
		return ormTemplate.getBatchRetriever(_dtoClass);
	}

	/**
	 * 批量更新数据库
	 * 
	 * @param dtos
	 * @throws JAFDatabaseException
	 */
	public void create(IDto[] dtos) throws JAFDatabaseException {
		if (dtos != null && dtos.length > 0) {
			ormTemplate.create(dtos);

		}

	}

	/**
	 * 根据一批主键查找一批数据
	 * 
	 * @param iPKs
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto[] find(IPK[] iPKs) throws JAFDatabaseException {
		return ormTemplate.find(iPKs);
	}

	/**
	 * 根据一批主键查找一批级联数据
	 * 
	 * @param iPKs
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto[] findCascade(IPK[] iPKs) throws JAFDatabaseException {
		return ormTemplate.findCascade(iPKs);
	}

	/**
	 * 根据一批主表数据查找一批级联数据
	 * 
	 * @param iDtos
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto[] findCascade(IDto[] iDtos) throws JAFDatabaseException {
		return ormTemplate.findCascade(iDtos);
	}

	/**
	 * 根据一批主键级联删除一批数据
	 * 
	 * @param iPKs
	 * @throws JAFDatabaseException
	 */
	public void deleteCascade(IPK[] iPKs) throws JAFDatabaseException {
		if (iPKs != null && iPKs.length > 0) {
			ormTemplate.deleteCascade(iPKs);

		}

	}

	/**
	 * 根据一批主表信息级联删除一批数据
	 * 
	 * @param iDtos
	 * @throws JAFDatabaseException
	 */
	public void deleteCascade(IDto[] iDtos) throws JAFDatabaseException {
		ormTemplate.deleteCascade(iDtos);
	}

	/**
	 * 根据一批表信息删除数据
	 * 
	 * @param iDtos
	 * @throws JAFDatabaseException
	 */
	public void delete(IDto[] iDtos) throws JAFDatabaseException {
		ormTemplate.delete(iDtos);
	}

	/**
	 * 根据一批主键信息删除数据
	 * 
	 * @param iDtos
	 * @throws JAFDatabaseException
	 */
	public void delete(IPK[] iPKs) throws JAFDatabaseException {
		ormTemplate.delete(iPKs);
	}

	/**
	 * 根据传入的dto类对象，删除条件语句及参数列表删除单表中满足条件的记录集
	 * 
	 * @param _dtoClass
	 * @param _whereClause
	 *            不含Where的条件语句
	 * @param _paramList
	 * @throws JAFDatabaseException
	 */
	public void delete(Class _dtoClass, String _whereClause, List _paramList) throws JAFDatabaseException {
		StringBuffer sb_sql = new StringBuffer();
		SQLExecutor sqlExec = sqlExecutorFactory.getSQLExecutor();

		String sTableName = OrmapFactory.getTableName(_dtoClass.getName());
		if ((_whereClause == null) || (_whereClause.trim().length() == 0)) {
			sb_sql.append("delete from ").append(sTableName);
		} else {
			sb_sql.append("delete from ").append(sTableName).append(" where ").append(_whereClause);
		}
		if ((_paramList == null) || (_paramList.size() == 0)) {
			sqlExec.runQueryCloseCon(sb_sql.toString());
		} else {
			Iterator iter = _paramList.iterator();
			while (iter.hasNext()) {
				sqlExec.addParam(iter.next());
			}
			sqlExec.runQueryCloseCon(sb_sql.toString());
		}
	}

	/**
	 * 根据传入的dto类对象和查询条件qto对象删除相应的记录
	 * 
	 * @param _dtoClass
	 * @param _qto
	 * @throws JAFDatabaseException
	 */
	public void delete(Class _dtoClass, CommonQto _qto) throws JAFDatabaseException {
		StringBuffer sb_sql = new StringBuffer();
		SQLExecutor sqlExec = sqlExecutorFactory.getSQLExecutor();

		String sTableName = OrmapFactory.getTableName(_dtoClass.getName());
		if (_qto == null) {
			sb_sql.append("delete from ").append(sTableName);
			sqlExec.runQueryCloseCon(sb_sql.toString());
			return;
		} else {
			sb_sql.append("delete from ").append(sTableName).append(" where ").append(_qto.getSClause());
		}

		List paramList = _qto.getLParams();
		if (paramList.size() > 0) {
			Iterator iter = paramList.iterator();
			while (iter.hasNext()) {
				sqlExec.addParam(iter.next());
			}
			sqlExec.runQueryCloseCon(sb_sql.toString());
		} else {
			sqlExec.runQueryCloseCon(sb_sql.toString());
		}

	}

	/**
	 * 根据数据库表名查找该标的所有数据
	 * 
	 * @param dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List find(Class dto) throws JAFDatabaseException {
		return ormTemplate.find(dto, "");

	}

	/**
	 * 使用脏读的方式，根据数据库表名查找该标的所有数据
	 * 
	 * @param dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List findWithUR(Class dto) throws JAFDatabaseException {
		return ormTemplate.find(dto, "");

	}

	/**
	 * @return 返回 ormTemplate
	 */
	public IbeanOrmTemplate getOrmTemplate() {
		return ormTemplate;
	}

	/**
	 * @param ormTemplate
	 *            要设置的 ormTemplate
	 */
	public void setOrmTemplate(IbeanOrmTemplate ormTemplate) {
		this.ormTemplate = ormTemplate;
	}

	/**
	 * @return 返回 sqlExecutorFactory
	 */
	public SQLExecutorFactory getSqlExecutorFactory() {
		// 增加对oracle数据库支持
		/*ConvertUtils.register(new Converter() {

			public Object convert(Class type, Object value) {
				if (value == null) {
					return null;
				}
				if (value instanceof java.sql.Timestamp) {
					return new java.sql.Date(((java.sql.Timestamp) value).getTime());
				} else if (value instanceof String) {
					if ((((String) value).trim()).length()==10) {
						return java.sql.Date.valueOf(((String) value).substring(0, 10));
					}
				}
				return value;
			}
		}, java.sql.Date.class);*/
		return sqlExecutorFactory;
	}

	/**
	 * @param sqlExecutorFactory
	 *            要设置的 sqlExecutorFactory
	 */
	public void setSqlExecutorFactory(SQLExecutorFactory sqlExecutorFactory) {
		this.sqlExecutorFactory = sqlExecutorFactory;
	}

	// 以下针对翻页增加查询方法
	/**
	 * 
	 * @param request
	 * @param primitiveDto
	 * @param compareDto
	 * @param symbolMap
	 * @param orderby
	 * @param joinInfos
	 * @param addsql
	 * @return
	 * @throws UppCheckedException
	 */
	public PageResponse findWithAddSql(PageRequest request, IDto primitiveDto, IDto compareDto, Map symbolMap,
			String orderby, List joinInfos, String addsql) throws JAFDatabaseException {
		boolean onlyChanged = false;
		boolean isSelected = true;
		boolean topage = true;
		return findWithAddSql(request, primitiveDto, compareDto, symbolMap, orderby, onlyChanged, isSelected, topage,
				joinInfos, addsql);
	}

	/**
	 * 
	 * @param request
	 *            保存翻页信息
	 * @param primitiveDto
	 * @param compareDto
	 * @param symbolMap
	 * @param orderby
	 * @param onlyChanged
	 * @param isSelected
	 * @param topage
	 * @param joinInfos
	 * @param addsql
	 * @return
	 * @throws JAFDatabaseException
	 */
	public PageResponse findWithAddSql(PageRequest request, IDto primitiveDto, IDto compareDto, Map symbolMap,
			String orderby, boolean onlyChanged, boolean isSelected, boolean topage, List joinInfos, String addsql)
			throws JAFDatabaseException {
		PageResponse response;
		if (request != null) {
			response = new PageResponse(request);
		} else {
			response = new PageResponse();
		}
		response.setData((List) selectWithAddSql(request, primitiveDto, compareDto, symbolMap, orderby, onlyChanged,
				isSelected, topage, joinInfos, addsql));
		if (request != null) {
			response.setTotalCount(request.getTotalCount());
		}
		return response;
	}

	/**
	 * 
	 * @param request
	 * @param primitiveDto
	 * @param compareDto
	 * @param symbolMap
	 * @param orderby
	 * @param onlyChanged
	 * @param isSelected
	 * @param topage
	 * @param joinInfos
	 * @param addsql
	 * @return
	 * @throws JAFDatabaseException
	 */
	public Collection selectWithAddSql(PageRequest request, IDto primitiveDto, IDto compareDto, Map symbolMap,
			String orderby, boolean onlyChanged, boolean isSelected, boolean topage, List joinInfos, String addsql)
			throws JAFDatabaseException {
		SQLExecutor exec = sqlExecutorFactory.getSQLExecutor();
		StringBuffer sql = new StringBuffer(toSqlFromIDto(primitiveDto, compareDto, symbolMap, null, onlyChanged,
				addsql));
		List param = SqlUtil.fillParamsFromIDto(primitiveDto, compareDto, onlyChanged);
		// 如果分页
		if (null != request && topage) {
			if (param.size() > 0) {
				exec.addParam(param);
			}
			// int countNum = exec.getQueryRowCountCloseCon(sql.toString());
			// if (countNum <= 0) {
			// return new ArrayList(0);
			// } else {
			// request.setTotalCount(countNum);
			//
			// if (!request.isFirstPage()) {
			// if (request.getStartPosition() > (request.getTotalCount())) {
			// // 对分页进行操作。删除最后一页的记录后返回前一页记录。
			// request.setPageNum(request.getPageNum() - 1);
			// }
			// }
			// }
		}
		// 如果有参数
		if (param.size() > 0) {
			exec.addParam(param);
		}
		// 执行sql语句
		SQLResults results = null;

		if (orderby != null && !"".equals(orderby)) {
			sql.append(" order by ").append(orderby);
		}
		if (null != joinInfos && joinInfos.size() > 0) {
			sql = new StringBuffer(convertSQLWithJoin(sql.toString(), joinInfos));
		}
		if (null != request && topage) {
			results = exec.runQueryCloseCon(sql.toString(), primitiveDto.getClass(), request.getStartPosition(),
					request.getPageSize(), isSelected);
		} else {
			
			
			results = exec.runQueryCloseCon(sql.toString(), primitiveDto.getClass());
		}
		return results.getDtoCollection();
	}

	/**
	 * 
	 * 将一个dto中所有非空属性拼接成一个select语句的where条件 operations中存放各个数据库字段在条件语句中对应的操作（例如 = >
	 * < like 等等），默认?? orderbyClause参数是排序语句，以order by开??
	 * onlyChanged为true则只处理修改过的属性，否则处理所有非空属??
	 * 
	 * 如果primitiveDto中有的数据，compareDto中也有，则就作为范围使用，primitiveDto.age = 10
	 * compareDto.age = 20 则sql就是 select .... age > ? and age < ?
	 * 
	 * @param primitiveDto
	 * @param compareDto
	 * @param operations
	 * @param orderby
	 * @param onlyChanged
	 * @param addsql
	 * @return
	 */
	public String toSqlFromIDto(IDto primitiveDto, IDto compareDto, Map operations, String orderby,
			boolean onlyChanged, String addsql) {
		StringBuffer sb = new StringBuffer();
		if (primitiveDto != null) {
			String tableName = SqlUtil.toTableName(primitiveDto.getClass());
			sb.append("select " + tableName + ".* from " + tableName + " where 1=1 " + addsql);
			List fields1 = new ArrayList();
			List fields2 = new ArrayList();
			if (onlyChanged) {
				fields1 = primitiveDto.getChangedFields();
				fields2 = compareDto.getChangedFields();
			} else {
				// all non-null fields processing
				PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(primitiveDto);

				for (int i = 0; i < pds.length; i++) {
					PropertyDescriptor pd = pds[i];
					// 必须有get和set方法
					if (pd.getReadMethod() == null || pd.getWriteMethod() == null) {
						continue;
					}
					// 属性是Number/Boolean/String
					Class propertyType = pd.getPropertyType();
					if (Number.class.isAssignableFrom(propertyType) || String.class == propertyType
							|| Boolean.class == propertyType || java.util.Date.class == propertyType
							|| java.sql.Date.class == propertyType || Time.class == propertyType
							|| Timestamp.class == propertyType) {
						// do nothing here
					} else {
						continue;
					}
					// 获取数据库表字段名称
					String columnName = SqlUtil.toColumnName(primitiveDto.getClass(), pd.getName());
					Object objData1 = null;
					Object objData2 = null;
					try {
						objData1 = pd.getReadMethod().invoke(primitiveDto, new Object[0]);
						objData2 = pd.getReadMethod().invoke(compareDto, new Object[0]);
					} catch (Exception e) {
						log.error(e);
					//	e.printStackTrace();
						return null;
					}
					// 生成查询条件Map
					if (columnName != null && objData1 != null) {
						if (objData1.getClass().isAssignableFrom(String.class)) {
							if (!objData1.toString().equalsIgnoreCase("")) {
								if (columnName.indexOf(".") <= 0) {
									fields1.add(tableName + "." + columnName);
									if (columnName != null && objData2 != null) {
										fields2.add(tableName + "." + columnName);
									}
								} else {
									fields1.add(columnName);
									if (columnName != null && objData2 != null) {
										fields2.add(columnName);
									}
								}
							}
						} else {
							if (columnName.indexOf(".") <= 0) {
								fields1.add(tableName + "." + columnName);
								if (columnName != null && objData2 != null) {
									fields2.add(tableName + "." + columnName);
								}
							} else {
								fields1.add(columnName);
								if (columnName != null && objData2 != null) {
									fields2.add(columnName);
								}
							}
						}
					}
				}
			}

			if (fields1.size() > 0) {
				for (Iterator iterator = fields1.iterator(); iterator.hasNext();) {
					String fieldName = (String) iterator.next();

					boolean isNormal = fields2.indexOf(fieldName) == -1;
					sb.append(" and ");

					if (isNormal) {
						String op = (String) operations.get(fieldName);
						if (op == null) {
							op = "=";
						}
						sb.append(" " + fieldName + " " + op + " ? ");
					} else {
						sb.append(" " + fieldName + " >=? and " + fieldName + " <=?");
					}
				}
			}
			if (orderby != null) {
				sb.append(" " + orderby);
			}
		}
		return sb.toString();
	}

	/**
	 * 根据单表的sql和关联信息生成Join的sql，该sql中的from、where、order by必须是小写的
	 * 
	 * @param sql
	 * @param joinInfos
	 *            表关联对应信息JoinInfo的List
	 * @return
	 */
	public static String convertSQLWithJoin(String sql, List joinInfos) {
		if ((sql.indexOf(" from ") == -1) || (sql.indexOf(" where ") == -1)) {
			return sql;
		}
		String[] splitFrom = sql.split(" from ");
		String[] splitWhere = splitFrom[1].split(" where ");
		String[] splitOrder = splitWhere[1].split(" order by ");

		StringBuffer partSel2Form = new StringBuffer(splitFrom[0]);
		StringBuffer partFrom2Where = new StringBuffer(splitWhere[0]);
		StringBuffer partWhere2Order = new StringBuffer(splitOrder[0]);

		String partOrder2End = "";
		if (splitOrder.length > 1) {
			partOrder2End = splitOrder[1];
		}

		StringBuffer sqlBuf = new StringBuffer();

		for (Iterator it = joinInfos.iterator(); it.hasNext();) {
			JoinInfo info = (JoinInfo) it.next();
			for (Iterator itCols = info.getAdditionCols().iterator(); itCols.hasNext();) {
				String col = (String) itCols.next();
				partSel2Form.append(",").append(info.getAlias()).append(".").append(col);
			}
			partFrom2Where.append(",").append(info.getTableName()).append(" ").append(info.getAlias());
			partWhere2Order.append(" and ").append(info.getAlias()).append(".").append(info.getSelfCol()).append("=")
					.append(info.getJoinCol());
			if (null != info.getAdditionWhereCon()) {
				partWhere2Order.append(info.getAdditionWhereCon());
			}
		}
		sqlBuf.append(partSel2Form);
		sqlBuf.append(" from ").append(partFrom2Where).append(" where ").append(partWhere2Order);
		if (splitOrder.length > 1) {
			sqlBuf.append(" order by ").append(partOrder2End);
		}
		return sqlBuf.toString();
	}
	public  Timestamp getSysDate()
	{
		String time=null;
		Timestamp times=null;
		try {
	    	SQLExecutor sqlexec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
		    if("ORACLE".equals(ormTemplate.databaseType_yak))
		    {
				//SQLResults rs=sqlexec.runQueryCloseCon("select SYSTIMESTAMP  from dual");
				SQLResults rs=sqlexec.runQueryCloseCon("select to_timestamp(to_char(systimestamp,'yyyy-MM-dd hh24:mi:ss.ff'),'yyyy-MM-dd hh24:mi:ss.ff') as sys_timestamp from dual");
/*				SQLResults rs=sqlexec.runQuery("select to_char(sysdate,'yyyy-MM-dd hh24:mi:ss') as sys_date from dual");
*/
				if(rs.getRowCount()>0)
				{
					times=rs.getTimestamp(0, 0);
				}

		    }else  if("DB2".equals(ormTemplate.databaseType_yak))
		    {
				SQLResults rs=sqlexec.runQueryCloseCon("select current timestamp from sysibm.sysdummy1");
				if(rs.getRowCount()>0)
				{
					//time=rs.getString(0, 0);
					times=rs.getTimestamp(0, 0);
				}
		    }
		   // SimpleDateFormat format = new SimpleDateFormat(STAND_DATE_TIME_FORMAT);
			//java.util.Date timeDate = format.parse(time);
			return times;//new Timestamp(timeDate.getTime());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	public  String getTimestamptype()
    {
    	String datatype=ormTemplate.databaseType_yak;
    	if("INFORMIX".equals(datatype))
    	{
    		return "CURRENT";
    	}else if("ORACLE".equals(datatype))
    	{
    		return "SYSTIMESTAMP";
    	}else if("SYBASE".equals(datatype))
    	{
    		return "getdate()";
    	}else
    	{
    		return "CURRENT TIMESTAMP";
    	}
    }
}
