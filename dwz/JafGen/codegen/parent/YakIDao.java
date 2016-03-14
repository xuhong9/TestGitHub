package codegen.parent;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.cfcc.jaf.persistence.jaform.parent.IDao;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public interface YakIDao extends IDao {
	/**
	 * 结果集 生成dto 可传递dto 类名 主要适应  dto 有继承的情况
	 * @param _rs
	 * @param dtoclass
	 * @param maxSize
	 * @param isLobSupport
	 * @return
	 * @throws SQLException
	 */
	public List getResults(ResultSet _rs, Class dtoclass,int maxSize, boolean isLobSupport)
	throws SQLException;
	 
    public List getResults(ResultSet rs, Class dtoclass,int maxSize, boolean isLobSupport,IDto backfillBean
) throws SQLException;
    /**
     * 增加处理回填数据方法
     * @param rs
     * @param maxSize
     * @param isLobSupport
     * @param backfillBean
     * @return
     * @throws SQLException
     */
    public IDto getResults(ResultSet rs,  int maxSize, boolean isLobSupport,IDto backfillBean
    ) throws SQLException;
    
    public IDto findForUpdate(IPK _pk, Connection conn, boolean isLobSupport) throws SQLException;
    
	public IDto find(IPK _pk, Connection conn, boolean isLobSupport,Class dtoclass) throws SQLException;
    
	public IDto[] find(IPK[] _pks, Connection conn, boolean isLobSupport,Class dtoclass) throws SQLException;
	   /**
	    * 增加处理回填数据方法
	    * @param _pk
	    * @param conn
	    * @param isLobSupport
	    * @param backfillBean
	    * @return
	    * @throws SQLException
	    */
	public IDto findBackfill(IPK _pk, Connection conn, boolean isLobSupport,IDto backfillBean) throws SQLException;
	
	public IDto find(IPK _pk, Connection conn, boolean isLobSupport,Class dtoclass,IDto backfillBean) throws SQLException;
	   
}
