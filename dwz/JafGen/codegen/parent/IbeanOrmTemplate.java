package codegen.parent;

 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.jaf.common.statistics.ServiceMethodStatistics;
import com.cfcc.jaf.core.invoker.transation.TransactionStatusInquirer;
import com.cfcc.jaf.persistence.dao.exception.DatabaseExceptionFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.BatchRetriever;
import com.cfcc.jaf.persistence.jaform.IOrmTemplate;
import com.cfcc.jaf.persistence.jaform.JAFOrmTemplate;
import com.cfcc.jaf.persistence.jaform.config.OrmapFactory;
import com.cfcc.jaf.persistence.jaform.parent.IDao;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.jaf.persistence.jdbc.core.IDataSource;

/**
 * JafOrm中进行提供数据库操作方法的类，该类继承com.cfcc.jaf.persistence.jaform.IOrmTemplate接口
 * yupei, 2008-07-04, 如果没有事务时，应指定异常时的行为
 * 
 * @author tucd
 * @version 1.0
 */
public class IbeanOrmTemplate extends JAFOrmTemplate implements IOrmTemplate {
	static Log _logger = LogFactory.getLog(IbeanOrmTemplate.class);

	/** 数据源 */
	private IDataSource dataSource_yak;

	/** 数据库类型 */
	public String databaseType_yak; 

	/** 结果集中记录的最大数量 */
	public int maxRows_yak = 1000;

	/** 当查询结果的row数超过maxRows时的缺省查询策略：忽略多余的记录 */
	private String exceedRowLimitStrategy_yak = EXCEED_STRATEGY_IGNORE;

	/** 是否对SQL语句的执行时间、效率等参数进行统计 */
	boolean statistics_yak = false;
	/**
	 * 主键 规则生成 工具
	 */
	private YakIPkGen pkgenerator;
	/**
	 * 数据字段中对 行序号 的标识 系统自动注入
	 */
	private String row_seq_key;
	
	public String getRow_seq_key() {
		return row_seq_key;
	}
	public void setRow_seq_key(String rowSeqKey) {
		row_seq_key = rowSeqKey;
	}
	@Override
	public IDto create(IDto dto, boolean withResult, boolean generateKey,
			boolean cascade ) throws JAFDatabaseException {
		return create(dto,withResult,generateKey,cascade,false,null);
	}
	/**
	 * 新增加是否对ibean进行全部插入
	 */
	public IDto create(IDto dto, boolean withResult, boolean generateKey,
			boolean cascade,boolean ibeancascade,Map contextmap) throws JAFDatabaseException {
		// 从数据库连接池获得数据库连接
		Connection conn=null;
		try {
		conn = dataSource_yak.getConnection();
		return createatom(dto,withResult,generateKey,cascade,ibeancascade,contextmap,conn);
		} catch (SQLException ex1) { 
			throw DatabaseExceptionFactory.getException(databaseType_yak,
					"插入数据错误.", ex1);
		} finally {
			// 释放数据库连接
			dataSource_yak.closeConnection(conn);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.persistence.jaform.IOrmTemplate#find(com.cfcc.jaf.persistence
	 * .jaform.parent.IPK, boolean)
	 */
	public IDto findForUpdate(IPK _pk, boolean isLobSupport) throws JAFDatabaseException {
		if (_logger.isDebugEnabled())
			_logger.debug("查找一条记录：" + _pk);

		Connection conn = null;
		try {
			// 从数据库连接池，获取数据库连接
			conn = dataSource_yak.getConnection();
			String name = _pk.getClass().getName();
			// 根据DTO的名字获得其相应的DAO对象
			IDao dao = OrmapFactory.getDao(name);

			long lBegin = 0, lEnd = 0;
			// 如果需要统计SQL语句的执行效率，那么记录执行开始时间
			if (statistics_yak)
				lBegin = System.currentTimeMillis();
			// 根据主键查找相应一条记录，对于父子表的情况，只返回父DTO的内容
			IDto dto;
			if (dao instanceof YakIDao){
				dto = (IDto)((YakIDao) dao).findForUpdate(_pk, conn, isLobSupport);
			}else{
				  dto = dao.find(_pk, conn, isLobSupport);
			}
			// 如果需要统计SQL语句的执行效率，那么记录执行结束时间
			if (statistics_yak) {
				lEnd = System.currentTimeMillis();
				ServiceMethodStatistics.record(OrmapFactory.getTableName(name),
						"SELECT", lEnd - lBegin);
			}
			// dataSource.closeConnection(conn);
			return dto;
		} catch (SQLException ex1) {
			_logger.error("查找数据错误:" + ex1.getMessage());
		 
			if (this.isRollbackOnExceptionIfNoTran()
					&& (!TransactionStatusInquirer.isTransactionExisting())) {
				rollbackTrans(conn);
			}
			// dataSource.closeConnection(conn);
			throw DatabaseExceptionFactory.getException(databaseType_yak,
					"查找数据错误.", ex1);
		} finally {
			dataSource_yak.closeConnection(conn);
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.persistence.jaform.IOrmTemplate#find(com.cfcc.jaf.persistence
	 * .jaform.parent.IPK, boolean)
	 */
	public void checkForUpdate(IDto dto ) throws JAFDatabaseException {
		if (_logger.isDebugEnabled())
			_logger.debug("查找一条记录：" + dto.getPK());

		Connection conn = null;
		try {
			// 从数据库连接池，获取数据库连接
			conn = dataSource_yak.getConnection();
			String name = dto.getPK().getClass().getName();
			// 根据DTO的名字获得其相应的DAO对象
			IDao dao = OrmapFactory.getDao(name);

			long lBegin = 0, lEnd = 0;
			// 如果需要统计SQL语句的执行效率，那么记录执行开始时间
			if (statistics_yak)
				lBegin = System.currentTimeMillis();
			// 根据主键查找相应一条记录，对于父子表的情况，只返回父DTO的内容
			  
				  (dao).check(dto, conn);
			 
			// 如果需要统计SQL语句的执行效率，那么记录执行结束时间
			if (statistics_yak) {
				lEnd = System.currentTimeMillis();
				ServiceMethodStatistics.record(OrmapFactory.getTableName(name),
						"SELECT", lEnd - lBegin);
			}
			// dataSource.closeConnection(conn);
			return ;
		} catch (SQLException ex1) {
			_logger.error("查找数据错误:" + ex1.getMessage());
		 
			if (this.isRollbackOnExceptionIfNoTran()
					&& (!TransactionStatusInquirer.isTransactionExisting())) {
				rollbackTrans(conn);
			}
			// dataSource.closeConnection(conn);
			throw DatabaseExceptionFactory.getException(databaseType_yak,
					"查找数据错误.", ex1);
		} finally {
			dataSource_yak.closeConnection(conn);
		}
	}
	/**
	 * 新增加是否对ibean进行全部插入
	 */
	public IDto createatom(IDto dto, boolean withResult, boolean generateKey,
			boolean cascade,boolean ibeancascade,Map contextmap,Connection conn) throws JAFDatabaseException {
		IDto _dto = dto;
		if (_logger.isDebugEnabled()) {
			_logger.debug("插入一条记录：" + _dto);
		}
 
		IDto gendto = null; // 返回的dto
		try {
			long lBegin = 0, lEnd = 0;
			String dtoName = _dto.getClass().getName();
			// 根据DTO的名字，获得其对应的DAO
			IDao dtoDao = OrmapFactory.getDao(dtoName); 
			// 先保存主表数据 
			/**
			 * tucd change 增加主键赋值 生成 begin
			 * TODO
			 */
			if (dto instanceof YakIDto){
				//将初始值 设置 
				 if (dto instanceof YakIbean){
					 try {
						((YakIbean) dto).initDefaultValue(contextmap);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						_logger.error("Ibean初始化数据异常");
						throw new SQLException();
					}
				 }
				 //生成主键 
				((YakIDto) dto).doAutoGenPk(this.getPkgenerator()); 
			}
			/**
			 * TUCD CHANGE END
			 */
			
			// 如果需要统计SQL语句的执行效率，那么记录执行开始时间
			if (statistics_yak) {
				lBegin = System.currentTimeMillis();
			}

			// 新增一条记录，对于父子表关系的DTO，此处只增加父表记录
			if (generateKey) {
				gendto = dtoDao.createWithResult(_dto, conn);
			} else {
				dtoDao.create(_dto, conn);
			}

			// 如果需要统计SQL语句的执行效率，那么记录执行结束时间
			if (statistics_yak) {
				lEnd = System.currentTimeMillis();
				// 将SQL语句的执行情况记录到日志中
				ServiceMethodStatistics.record(OrmapFactory
						.getTableName(dtoName), "INSERT", lEnd - lBegin);
			}

			// 如果DTO包含子表信息，那么保存子表内容
			if (_dto.isParent() && _dto.getChildren() != null) {
				// 将维护父子表的外键信息，保存到子DTO中
				if (generateKey) {
					if (gendto != null) {
						gendto.setChildren(_dto.getChildren());
						dtoDao.syncToChildren(gendto);
						_dto = gendto;
					}
					
				} else {
					dtoDao.syncToChildren(_dto);
				}
				// 保存子表内容
				create(_dto.getChildren());
			}
			//如果是ibean 生成
			/**
			 * tucd change 2011 03 03 begin
			 * TODO
			 */
			 if (dto instanceof YakIbean){
				 /**
				  * 有subean 并且 ibeancascade 是 允许的
				  */
				 YakIbean yakbean=(YakIbean) dto;
				 if ((yakbean.getSubIbeans()!=null) && (yakbean.getSubIbeans().length>0) && ibeancascade){
					 
					Map pkmap=((YakIDto)dto).getPkToMap();
 
					 for (int n=0;n<yakbean.getSubIbeans().length;n++){
						 if (yakbean.getSubIbeans()[n] ==null){
							 continue;
						 }
						 if (yakbean.getSubIbeans()[n] instanceof YakIDto){
							//((YakIDto)yakbean.getSubIbeans()[n]).doMapToPk(pkmap);
							((YakIDto)yakbean.getSubIbeans()[n]).doMapToColumnValue(pkmap);
							 createatom((IDto)yakbean.getSubIbeans()[n],false,generateKey,cascade,ibeancascade,contextmap,conn);
						 }else if (yakbean.getSubIbeans()[n] instanceof List){
							 //list 情况 处理 
							 List list=(List) yakbean.getSubIbeans()[n];
							 for (int list_row=0;list_row<list.size();list_row++){ 
								  if (list.get(list_row)!=null){
								   pkmap.put(this.getRow_seq_key(), String.valueOf(list_row+1));
								   //((YakIDto)list.get(list_row)).doMapToPk(pkmap);
								   ((YakIDto)list.get(list_row)).doMapToColumnValue(pkmap);
								   createatom((YakIDto)list.get(list_row),false,generateKey,cascade,ibeancascade,contextmap,conn);
								 }
							  }
						 }
					 }
				 }
			 } 
			 
			/**
			 * tucd change end
			 */
			
			// 如果有返回
			IDto result = null;
			if (withResult && cascade) {
				result = this.findCascade(_dto);
			} else if (withResult && (!cascade) && generateKey) {
				result = this.find(gendto);
			}
			// fix bug see logfile "JAF修改记录_2.0 item"
			else if (withResult && (!cascade) && (!generateKey)) {
				result = this.find(_dto);
			} else {
				//
			}
			 
			return result;
		} catch (SQLException ex1) {
			_logger.error("插入数据错误:" + ex1.getMessage());
			if (this.isRollbackOnExceptionIfNoTran()
					&& (!TransactionStatusInquirer.isTransactionExisting())) {
				rollbackTrans(conn);
			} 
			throw DatabaseExceptionFactory.getException(databaseType_yak,
					"插入数据错误.", ex1);
		} 
	}
	@Override
	public void create(IDto[] _dtos ) throws JAFDatabaseException {
		super.create(  _dtos);
	}
	public void create(IDto[] _dtos,boolean isIbeanCascade,Map contextmap ) throws JAFDatabaseException {
		// 从数据库连接池获得数据库连接
		Connection conn=null;
		try {
		conn = dataSource_yak.getConnection();
		for (int i=0;i<_dtos.length;i++){
			 createatom(_dtos[i],false,false,false,isIbeanCascade,contextmap,conn);
		} 
		} catch (SQLException ex1) { 
			throw DatabaseExceptionFactory.getException(databaseType_yak,
					"插入数据错误.", ex1);
		} finally {
			// 释放数据库连接
			dataSource_yak.closeConnection(conn);
		}
	}
 
    @Override
	public List find(Class _dtoClass, String where, List params, int start,
			int size) throws JAFDatabaseException {
		return find(_dtoClass,where,params,start,size,false);
	}
	
	/**
	 * Retrive batch record from Database. it only find the first layer dto<br>
	 * 查询批量数据，如果where 子句为空则查询所有的数据，同时将查询的结果存放到相应的DTO对象中
	 * 
	 * @param _dtoClass
	 *            查询结果对应的DTO对象
	 * @param where
	 *            Where子句
	 * @param params
	 *            Where子句中的参数
	 * @param start
	 *            所返回结果开始的位置，The first row is row 1, the second is row 2, and so
	 *            on.（见ResultSet.absolute方法）
	 * @param size
	 *            返回的结结果数量
	 *   isIbeanCascade
	 *   	是否连带查询         
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List find(Class _dtoClass, String where, List params, int start,
			int size,boolean isIbeanCascade) throws JAFDatabaseException {

		if (_logger.isDebugEnabled()) {
			_logger.debug("查找多条记录：" + _dtoClass + ";" + where + ";" + params);
		}

		if (size < 1) {
			throw new IllegalArgumentException("每次取结果集中的记录条数应该为正数.");
		}

		// 获取数据库批量查询对象
		BatchRetriever br = this.getBatchRetriever(_dtoClass);
		long lBegin = 0, lEnd = 0;

		try {
			// 如果需要记录SQL语句的执行效率，那么记录执行开始时间
			if (statistics_yak) {
				lBegin = System.currentTimeMillis();
			}
			//tucd 解决JAF 默认无 size 问题，现增加 SIZE
			br.runQuery(where, params, start,size);
			// 设置每次从结果中取得得记录数量
			br.setMaxRows(size);

			// 获得结果集
			List result = br.getResults();
			
			// 检查查询策略，如果查询结果记录超过最大值，那么是抛出例外还是忽略
			if (EXCEED_STRATEGY_EXCEPTION
					.equalsIgnoreCase(this.exceedRowLimitStrategy_yak)) // 抛出Exception
			{
				if (result.size() >= maxRows_yak) {
					throw DatabaseExceptionFactory.getException(databaseType_yak,
							"查询结果记录数超出设定最大值：" + maxRows_yak);
				}
			}
			if (isIbeanCascade){
				 for(int n=0;n<result.size();n++){
					 if((result.get(n) instanceof  YakIDto ) && (result.get(n) instanceof  YakIbean ) )
					 {
						 YakIbean yakbean=(YakIbean)result.get(n);
	                     
						 find( yakbean,  1, maxRows_yak, isIbeanCascade);
					 }
					 
						 
					 
				 }
				//result.
			}
			br.closeConnection();
			return result;
		} finally {
			br.closeConnection();
			// 如果需要记录SQL语句的执行效率，那么记录执行结束时间
			if (statistics_yak) {
				lEnd = System.currentTimeMillis();
				ServiceMethodStatistics.record(OrmapFactory
						.getTableName(_dtoClass.getName()), where, lEnd
						- lBegin);
			}
		}

	}  
	/*
	 *  tucd 改动方法
	 * 
	 * @see
	 * com.cfcc.jaf.persistence.jaform.IOrmTemplate#getBatchRetriever(java.lang
	 * .Class)
	 */
	public BatchRetriever getBatchRetriever(Class _dtoClass) {
		// 获得DTO对应的DAO对象
		IDao dao = OrmapFactory.getDao(_dtoClass.getName());
		// 获得执行批量查询对象
		YakBatchRetriever br = new YakBatchRetriever(this.dataSource_yak,
				this.databaseType_yak, dao, maxRows_yak,_dtoClass);
		return br;
	} 
	/**
	 * 根据Dto中设置的条件查找相应的记录并返回获取的记录集,如果Dto值为空,返回全表数据
	 * 
	 * @param _dto
	 * @return List
	 * @throws JAFDatabaseException
	 */
	public List findRsByDto(IDto _dto) throws JAFDatabaseException {
		String where="where";
		List params=new ArrayList();
		Map ColumnValueMap=((YakIDto)_dto).getColumnValueToMap();
		 StringBuffer buf = new StringBuffer();
		 buf.append(where);
        //通过dto columnvalue map构造查询条件
		 Iterator iter = ColumnValueMap.entrySet().iterator();
		 while(iter.hasNext()){
		   Entry entry = (Entry)iter.next();
		   if(null!=entry.getValue()&&!"".equals(entry.getValue()))
		   {
			   buf.append(" ");
			   buf.append((String)entry.getKey());
			   buf.append("=? and"); 
			   params.add(entry.getValue());
		   }
		 }	
		 where=buf.toString();
		 if(where.equals("where"))
		 {
			 where="";
		 }else
		 {
			 where=where.substring(0,where.length()-3);
		 }
		return find(_dto.getClass(), where, params)	;	
	}
	@Override
	public IDto find(IPK _pk, boolean isLobSupport) throws JAFDatabaseException {
		return find(_pk,isLobSupport,false);
	}
	
	public IDto find(IPK _pk, boolean isLobSupport,boolean isIbeanCascade) throws JAFDatabaseException {
	    return find( _pk,  isLobSupport, isIbeanCascade,null,false);
	}
	/**
	 * 新增方法
	 * @param _pk
	 * @param isLobSupport
	 * @param backfillBean
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto findBackfill(IPK _pk,   boolean isLobSupport,IDto backfillBean) throws JAFDatabaseException {
		return  find(  _pk,   isLobSupport,  false,  null,  false,backfillBean);
	}
	public IDto find(IPK _pk, boolean isLobSupport,boolean isIbeanCascade,Class _dtoClass,boolean dtocascade) throws JAFDatabaseException {
		return  find(  _pk,   isLobSupport,  isIbeanCascade,  _dtoClass,  dtocascade,null);
	}
	public IDto find(IPK _pk, boolean isLobSupport,boolean isIbeanCascade,Class _dtoClass,boolean dtocascade,IDto backfillBean) throws JAFDatabaseException {
		if (_logger.isDebugEnabled()) {
			_logger.debug("查找一条记录：" + _pk);
		} 
		Connection conn = null;
		try {
			// 从数据库连接池，获取数据库连接
			conn = dataSource_yak.getConnection();
			String name = _pk.getClass().getName();
			// 根据DTO的名字获得其相应的DAO对象
			YakIDao dao = (YakIDao)OrmapFactory.getDao(name);

			long lBegin = 0, lEnd = 0;
			// 如果需要统计SQL语句的执行效率，那么记录执行开始时间
			if (statistics_yak) {
				lBegin = System.currentTimeMillis();
			}
			// 根据主键查找相应一条记录，对于父子表的情况，只返回父DTO的内容
			IDto dto =null;
			if (backfillBean!=null){
				//tucd 增加 处理回填数据问题
				 dto = dao.findBackfill(_pk, conn, isLobSupport,backfillBean);
			}else{
			 dto = dao.find(_pk, conn, isLobSupport,_dtoClass);
			}
			
			if(isIbeanCascade)
			{
				//根据pk 查询出跟此DTO有子主关系的所有信息
				 YakIbean yakbean=(YakIbean)dto;
				 find( yakbean, 1, maxRows_yak, isIbeanCascade);
			}
			// 如果需要统计SQL语句的执行效率，那么记录执行结束时间
			if (statistics_yak) {
				lEnd = System.currentTimeMillis();
				ServiceMethodStatistics.record(OrmapFactory.getTableName(name),
						"SELECT", lEnd - lBegin);
			}
			if(dtocascade){
				if (dto != null) // 找到Parent
				{
					// 得到children
					if (dto.isParent()) {
						// 如果DTO是父DTO，那么查询该DTO的子DTO
						// 如果需要记录SQL语句执行效率，那么记录执行开始时间
						if (statistics_yak) {
							lBegin = System.currentTimeMillis();
						}
						// 获取子表信息，即子DTO
						IDto[] dtos = dao.findChildren(dto, conn, false);
						// 如果需要记录SQL语句执行效率，那么记录执行结束时间
						if (statistics_yak) {
							lEnd = System.currentTimeMillis();
							ServiceMethodStatistics.record(OrmapFactory
									.getTableName(name), "SELECT Children",
									lEnd - lBegin);
						}
						// 将相应的子表信息（子DTO），添加到父DTO中
						dto.setChildren(dtos);
						// 对children进行递归查找，查找所有子DTO的子子DTO
						if (dtos != null) {
							for (int i = 0; i < dto.getChildren().length; i++) {
								if (dtos[i].isParent()) {
									dtos[i] = findCascade(dtos[i]);
								}
							}
						}
					}
				}
			}
			// dataSource.closeConnection(conn);
			return (IDto) dto;
		} catch (SQLException ex1) {
			_logger.error("查找数据错误:" + ex1.getMessage());
			// yupei, 2008-07-04, 如果没有事务时，应指定异常时的行为
			if (this.isRollbackOnExceptionIfNoTran()
					&& (!TransactionStatusInquirer.isTransactionExisting())) {
				rollbackTrans(conn);
			}
			// dataSource.closeConnection(conn);
			throw DatabaseExceptionFactory.getException(databaseType_yak,
					"查找数据错误.", ex1);
		} finally {
			dataSource_yak.closeConnection(conn);
		}
	}
	public List  find(String sql,Class _dtoclass,boolean isLobSupport) throws JAFDatabaseException {
		// 从数据库连接池获得数据库连接
		Connection conn=null;
		PreparedStatement ps = null;
	    ResultSet rs = null;
		try {
		conn = dataSource_yak.getConnection();
		String name = _dtoclass.getName();
		// 根据DTO的名字获得其相应的DAO对象
		YakIDao dao = (YakIDao)OrmapFactory.getDao(name);
	     ps = conn.prepareStatement(sql);
         rs = ps.executeQuery();
         List results = dao.getResults(rs,_dtoclass,0,isLobSupport);
         return results;
		} catch (SQLException ex1) { 
			throw DatabaseExceptionFactory.getException(databaseType_yak,
					"查询数据错误.", ex1);
		} finally {
			// 释放数据库连接
			dataSource_yak.closeConnection(conn);
			if(null!=ps)
			{
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
			if(null!=rs)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		}
		
	}
	public IDto[] find(IPK[] _pks, boolean isLobSupport)
	throws JAFDatabaseException {
		return find(_pks, isLobSupport, false);
	}
	public IDto[] find(IPK[] _pks, boolean isLobSupport,boolean isIbeanCascade)
	throws JAFDatabaseException {
		return find(_pks, isLobSupport, isIbeanCascade,null,false);
	}
	
	public IDto[] find(IPK[] _pks, boolean isLobSupport,boolean isIbeanCascade,Class _dtoClass,boolean dtocascade)
			throws JAFDatabaseException {

		if (_pks == null || _pks.length == 0) {
			return null;
		}

		if (_logger.isDebugEnabled()) {
			StringBuffer sb = new StringBuffer("查找多条记录：");
			// for (int i = 0; i < _pks.length; i++)
			// {
			// sb.append(_pks[i]).append(";");
			// }
			sb.append(_pks.length).append(";");
			sb.append(_pks[0].getClass().getName());

			_logger.debug(sb.toString());
		}

		Connection conn = null;
		try {
			// 从数据库连接池，获取数据库连接
			conn = dataSource_yak.getConnection();
			String name = _pks[0].getClass().getName();
			// 根据DTO的名字获得其相应的DAO对象
			YakIDao  dao = (YakIDao)OrmapFactory.getDao(name);

			
			long lBegin = 0, lEnd = 0;
			// 如果需要统计SQL语句的执行效率，那么记录执行开始时间
			if (statistics_yak) {
				lBegin = System.currentTimeMillis();
			}
			// 根据主键查找相应一组记录，对于父子表的情况，只返回父DTO的内容
			IDto[] dtos = dao.find(_pks, conn, isLobSupport,_dtoClass);
			
			
			if(isIbeanCascade)
			{
				
				

				
				
				//根据pk 查询出跟此DTO有子主关系的所有信息
				for(int n=0;n<dtos.length;n++)
				{
					YakIbean yakbean=(YakIbean)dtos[n];
					find( yakbean, 1, maxRows_yak, isIbeanCascade);
					
				}
				
			}
			if (dtocascade){
				if (dtos != null && dtos.length != 0) // 找到Parent
				{
					// 得到children
					if (dtos[0].isParent()) {
						// 对于每一个DTO，如果是父DTO，那么查询该DTO的子DTO
						for (int i = 0; i < dtos.length; i++) {
							// 如果需要记录SQL语句执行效率，那么记录执行开始时间
							if (statistics_yak) {
								lBegin = System.currentTimeMillis();
							}
							// 获取子表信息，即子DTO
							IDto[] dtosChildren = dao.findChildren(dtos[i], conn,
									false);
							// 如果需要记录SQL语句执行效率，那么记录执行结束时间
							if (statistics_yak) {
								lEnd = System.currentTimeMillis();
								ServiceMethodStatistics.record(OrmapFactory
										.getTableName(name), "SELECT Children",
										lEnd - lBegin);
							}
							// 将相应的子表信息（子DTO），添加到父DTO中
							dtos[i].setChildren(dtosChildren);
							// 对children进行递归查找，查找所有子DTO的子子DTO
							if (dtosChildren != null) {
								for (int j = 0; j < dtosChildren.length; j++) {
									if (dtosChildren[j].isParent()) {
										dtosChildren[j] = findCascade(dtosChildren[j]);
									}
								}
							}
						}
					}
	
				}
			}
			// 如果需要统计SQL语句的执行效率，那么记录执行结束时间
			if (statistics_yak) {
				lEnd = System.currentTimeMillis();
				ServiceMethodStatistics.record(OrmapFactory.getTableName(name),
						"SELECT Multi", lEnd - lBegin);
			}
			// dataSource.closeConnection(conn);
			return dtos;
		} catch (SQLException ex1) {
			_logger.error("查找数据错误:" + ex1.getMessage());
			// yupei, 2008-07-04, 如果没有事务时，应指定异常时的行为
			if (this.isRollbackOnExceptionIfNoTran()
					&& (!TransactionStatusInquirer.isTransactionExisting())) {
				rollbackTrans(conn);
			}
			// dataSource.closeConnection(conn);			
			throw DatabaseExceptionFactory.getException(databaseType_yak,
					"查找数据错误.", ex1);
		} finally {
			dataSource_yak.closeConnection(conn);
		}
	}
   
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.persistence.jaform.IOrmTemplate#findCascade(com.cfcc.jaf
	 * .persistence.jaform.parent.IDto)
	 */
	public IDto findCascade(IDto _dto) throws JAFDatabaseException {
		// 获得DTO的PK，然后根据PK去查找
		IPK _pk = _dto.getPK();
		return findCascade(_pk);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.persistence.jaform.IOrmTemplate#findCascade(com.cfcc.jaf
	 * .persistence.jaform.parent.IDto[])
	 */
	public IDto[] findCascade(IDto[] _dtos) throws JAFDatabaseException {
		// 获得所有DTO的PK，然后根据PK去查找一组记录
		IPK[] pks = new IPK[_dtos.length];
		for (int i = 0; i < _dtos.length; i++) {
			pks[i] = _dtos[i].getPK();
		}
		return findCascade(pks);
	}
 
	public IDto findCascade(IPK _pk) throws JAFDatabaseException{
		
		return find( _pk, false,false,null,true) ;
	}
	
 
	
	 

	public IDto[] findCascade(IPK[] _pks ) throws JAFDatabaseException {
		return find(_pks, false);
	}
	
	/**
	 * 设置数据库类型
	 */
	@Override
	public void setDatabaseType(String string) {
		super.setDatabaseType(string);
		databaseType_yak = string;
	}

	/**
	 * 设置数据库连接池
	 */
	public void setDataSource(IDataSource source) {
		super.setDataSource(source);
		dataSource_yak = source;
	}

	/**
	 * 设置是否统计SQL语句执行效率
	 */
	@Override
	public void setStatistics(boolean b) {
		super.setStatistics(b);
		statistics_yak = b;
	}

	/**
	 * 设置查询结果集中的最大记录数量
	 */
	@Override
	public void setMaxRows(int i) {
		super.setMaxRows(i);
		maxRows_yak = i;
	}

	/**
	 * <pre>
	 * 设置查询策略
	 * &lt;br&gt;
	 *    当查询结果的row数超过maxRows时，所采取的策略
	 * &lt;br&gt;
	 *    ignore：忽略多余的记录
	 * &lt;br&gt;
	 *    exception：存在多余的记录则抛出异常
	 * &lt;br&gt;
	 *    缺省执行策略为：ignore
	 * </pre>
	 */
	@Override
	public void setExceedRowLimitStrategy(String string) {
		super.setExceedRowLimitStrategy(string);
		exceedRowLimitStrategy_yak = string;
	}
	public YakIPkGen getPkgenerator() {
		return pkgenerator;
	}
	public void setPkgenerator(YakIPkGen pkgenerator) {
		this.pkgenerator = pkgenerator;
	}
	public Object [] getSelectCondition(YakIDto yakdto,YakIDto sundto)
	{
		 Object []o=new Object[2];
		 String where="where";
		 List params=new ArrayList();

		 //当前bean属性值put到MAP
		// Map beanMap=yakdto.getColumnValueToMap();
		 
		 //通过当前BEAN属性值Set到子bean PK
		// sundto.doMapToPk(beanMap);
		// sundto.doMapToColumnValue(beanMap);
		 StringBuffer buf = new StringBuffer();
		 buf.append(where);
        //通过子BEAN PKMAP 构造查询条件
		 Map pkmap=yakdto.getPkToMap();
		 Iterator iter = pkmap.entrySet().iterator();
		 while(iter.hasNext()){
		   Entry entry = (Entry)iter.next();
		   if(entry.getValue()!=null)
		   {
			   buf.append(" ");
			   buf.append((String)entry.getKey());
			   buf.append("=? and"); 
			   params.add(entry.getValue());
		   }
		 }	
		 where=buf.toString();
		 if(!where.equals("where"))
		 {
			 where=where.substring(0,where.length()-3);
		 }
		 o[0]=where;
		 o[1]=params;
		 return o;
	}
	//通过IBEAN查询 全数据
	public void find(YakIbean yakbean,int start,int size,boolean isIbeanCascade) throws JAFDatabaseException
	{
		 Object []O=yakbean.getSubIbeanInstance();
		 Object []subs=yakbean.getSubIbeans();
		 if(yakbean.getSubIbeanInstance()!=null)
		 {
			 for(int i=0;i<yakbean.getSubIbeanInstance().length;i++)
			 {
				    if(O[i] instanceof YakIbean  )
				 {
					 Object []paras=getSelectCondition((YakIDto)yakbean,(YakIDto)yakbean.getSubIbeanInstance()[i]);
					 List list=find( O[i].getClass(),  (String)paras[0],  (List)paras[1],  start,size, isIbeanCascade);
					 if(list.size()>0)
					 {
						 subs[i]=list.get(0);
					 }
				 }else if (O[i] instanceof YakIDto ){
					 Object []paras=getSelectCondition((YakIDto)yakbean,(YakIDto)yakbean.getSubIbeanInstance()[i]);
					 List list=find( O[i].getClass(),  (String)paras[0],  (List)paras[1],  start,size, false);
					 if(list.size()>0)
					 {
						 subs[i]=list.get(0);
					 }
				 }
				 else if(O[i] instanceof List)
				 {
					 
					 List _list=(List) yakbean.getSubIbeanInstance()[i];
					 if (_list!=null && _list.size()>0){
						 Object []paras=getSelectCondition((YakIDto)yakbean,(YakIDto)_list.get(0));
						 List list=find( _list.get(0).getClass(),  (String)paras[0],  (List)paras[1],  start,size, isIbeanCascade);
						 if(list.size()>0)
						 {
							 subs[i]=list;
						 }
					 }
				 }
				 
			 }
		 }
		 
		 yakbean.setSubIbeans(subs);
	}
	public  String getTimestamptype()
    {
    	String datatype=databaseType_yak;
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
