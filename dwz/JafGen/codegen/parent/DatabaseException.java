package codegen.parent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DatabaseException extends YakBaseException{
	private final static Log logger = LogFactory.getLog(DatabaseException.class);
	
	public DatabaseException(String errcode,String msg) {
		super(msg);
		this.setErrorCode(errcode);
		//this.setErrorType(msg);
		this.setErrorDesc(msg);
		// TODO Auto-generated constructor stub
	}
	public DatabaseException(Throwable arg1) {
		super(arg1);
		// TODO Auto-generated constructor stub
	}
	public DatabaseException(final Throwable arg1, final String arg2) {
		super(arg1,arg2);
	}
	public DatabaseException( final String arg1,final Throwable arg2) {
		super(arg1,arg2);
	}
	public DatabaseException(final String arg1) {
		super(arg1);
	 }
}