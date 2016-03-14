package codegen.parent;

 

import com.cfcc.jaf.common.util.StringUtil;

public class DbHelper {
	 /**
	    *返回数据库类型
	    */
	    public static String getTimestampType()
	    {
	       String databasetype=PersistenceGlobalProperties.getGlobalDatabaseType();
	       if(!StringUtil.hasText(databasetype))
	       {
	    	   databasetype=DbConstants.DATABASETYPE;
	       }
	       if(databasetype.equals("INFORMIX"))
	       {
	          return "CURRENT";
	       }else if(databasetype.equals("ORACLE"))
	       {
	          return "SYSTIMESTAMP";
	       }
	       else if(databasetype.equals("SYBASE"))
	       {
	          return "getdate()";
	       }else
	       {
	          return "CURRENT TIMESTAMP";
	       }
	    }
	    
	    /**
		    *返回数据库日期类型
		    */
		    public static String getCurrentDateType()
		    {
		       String databasetype=PersistenceGlobalProperties.getGlobalDatabaseType();
		       if(!StringUtil.hasText(databasetype))
		       {
		    	   databasetype=DbConstants.DATABASETYPE;
		       }
		       if(databasetype.equals("INFORMIX"))
		       {
		          return "CURRENT";
		       }else if(databasetype.equals("ORACLE"))
		       {
		          return "SYSDATE";
		       }
		       else if(databasetype.equals("SYBASE"))
		       {
		          return "getdate()";
		       }else
		       {
		          return "CURRENT DATE";
		       }
		    }
	    /**
		    *返回数据库 DUAL 
		    */
		    public static String getDualType()
		    {
		       String databasetype=PersistenceGlobalProperties.getGlobalDatabaseType();
		       if(!StringUtil.hasText(databasetype))
		       {
		    	   databasetype=DbConstants.DATABASETYPE;
		       }
		       if(databasetype.equals("INFORMIX"))
		       {
		          return "DUAL";
		       }else if(databasetype.equals("ORACLE"))
		       {
		          return "DUAL";
		       }
		       else if(databasetype.equals("SYBASE"))
		       {
		          return "DUAL";
		       }else
		       {
		          return "SYSIBM.DUAL";
		       }
		    }
		    
		    //返回不同数据库的Sequences
		    public static String getSequencesType(String seqName)
		    {
		       String databasetype=PersistenceGlobalProperties.getGlobalDatabaseType();
		       if(!StringUtil.hasText(databasetype))
		       {
		    	   databasetype=DbConstants.DATABASETYPE;
		       }
		       if(databasetype.equals("INFORMIX"))
		       {
		          return "DUAL";
		       }else if(databasetype.equals("ORACLE"))
		       {
		          return "DUAL";
		       }
		       else if(databasetype.equals("SYBASE"))
		       {
		          return "DUAL";
		       }else
		       {
		          return "char(nextval for "+ seqName + ")";
		       }
		    }
		    
		    	/**
			    *根据数据库类型，得到判空字符串
			    */
			    public static String getIsNotNull(boolean isNull)
			    {
			    	String databasetype=PersistenceGlobalProperties.getGlobalDatabaseType();
			       if(!StringUtil.hasText(databasetype))
			       {
			    	   databasetype=DbConstants.DATABASETYPE;
			       }
			       if(databasetype.equals("ORACLE"))
			       {
			    	   return isNull?" IS NULL ":" IS NOT NULL ";
			       }else
			       {
			    	   return isNull?" ='' ":" !='' ";
			       }
			    }
	
	    /**
		  *根据不同的数据类型处理date类型
		    */
		    public static String getDateString(Object date)
		    {
		       String databasetype=PersistenceGlobalProperties.getGlobalDatabaseType();
		       if(!StringUtil.hasText(databasetype))
		       {
		    	   databasetype=DbConstants.DATABASETYPE;
		       }
		       if(databasetype.equals("INFORMIX"))
		       {
		          return "CURRENT";
		       }else if(databasetype.equals("ORACLE"))
		       {
		          return "TO_DATE('"+date+"','yyyy-MM-dd')";
		       }
		       else if(databasetype.equals("SYBASE"))
		       {
		          return "getdate()";
		       }else
		       {
		          return "'"+date+"'";
		       }
		    }
		  //返回不同数据库的返回 update rs
		    public static String getRsForUpdate()
		    {
		    	String databasetype=PersistenceGlobalProperties.getGlobalDatabaseType();
		       if(!StringUtil.hasText(databasetype))
		       {
		    	   databasetype=DbConstants.DATABASETYPE;
		       }
		       if(databasetype.equals("DB2"))
		       {
		          return " WITH RS ";
		       } else
		       {
		          return " ";
		       }
		    }
		    
		    /**
			  *汇总核对表主键信息 int to char
			    */
			    public static String getSeqString(Object date)
			    {
			       String databasetype=PersistenceGlobalProperties.getGlobalDatabaseType();
			       if(!StringUtil.hasText(databasetype))
			       {
			    	   databasetype=DbConstants.DATABASETYPE;
			       }
			       if(databasetype.equals("INFORMIX"))
			       {
			          return "CURRENT";
			       }else if(databasetype.equals("ORACLE"))
			       {
			          return "to_char("+date+".nextval)";
			       }
			       else if(databasetype.equals("SYBASE"))
			       {
			          return "getdate()";
			       }else
			       {
			          return "char(nextval for "+date+")";
			       }
			    }
}
