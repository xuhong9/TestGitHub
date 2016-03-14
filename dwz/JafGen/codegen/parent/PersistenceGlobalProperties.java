package codegen.parent;

/**
 * 持久层公共属性定义
 * @author Administrator
 *
 */
public class PersistenceGlobalProperties {
public final static String DATABASETYPE_INFORMIX="INFORMIX" ;
public final static String DATABASETYPE_DB2="DB2" ;
public final static String DATABASETYPE_ORACLE="ORACLE" ;
public final static String DATABASETYPE_SYBASE="SYBASE";
public final static String DATABASETYPE_MYSQL="MYSQL";

public final static int MAPDTOSTRATEGY_NULL_VALID=1;
public final static int MAPDTOSTRATEGY_NULL_NOVALID=0;


private static String databasetype=null;
private static int maptodtostrategy=0;

public static  synchronized void  registerDatabasetype(String dbtype){
	databasetype=dbtype;
}
public static  synchronized void  registerMapToDtoStrategy(int strategy){
	maptodtostrategy=strategy;
}
public static String getGlobalDatabaseType(){
	if (databasetype==null){
		return System.getProperty("dbtype");
	}else{
	    return databasetype;
	}
}
public static int getGlobalMapToDtoStrategy(){
	return maptodtostrategy;
}

}
