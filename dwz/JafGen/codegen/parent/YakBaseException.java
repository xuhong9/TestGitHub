package codegen.parent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class YakBaseException  extends Exception{

	private final static Log logger = LogFactory.getLog(YakBaseException.class);
	/**
	 * 处理码
	 */
	private String ErrorCode ;
	
	public String getErrorCode() {
		return ErrorCode;
	}

	public void setErrorCode(String errorCode) {
		ErrorCode = errorCode;
	}

	public String getErrorDesc() {
		return ErrorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		ErrorDesc = errorDesc;
	}

	public String getErrorType() {
		return ErrorType;
	}

	public void setErrorType(String errorType) {
		ErrorType = errorType;
	}

	private String ErrorDesc;
	
	private String ErrorType;
	public YakBaseException(String errcode,String msg) {
		
		super(msg);
		this.setErrorCode(errcode);
		//this.setErrorType(msg);
		this.setErrorDesc(msg);
		// TODO Auto-generated constructor stub
	}
	public YakBaseException(Throwable arg1) {
		super("",arg1);
	    logger.error("error:"+this.getStackTrace()[0].getClassName()+":"+this.getStackTrace()[0].getMethodName()+":"+this.getStackTrace()[0].getLineNumber(), arg1);
		// TODO Auto-generated constructor stub
	}
	public YakBaseException(final Throwable arg1, final String arg2) {
		//super(arg1);
		super(arg2,arg1);
		logger.error("error:"+this.getStackTrace()[0].getClassName()+":"+this.getStackTrace()[0].getMethodName()+":"+this.getStackTrace()[0].getLineNumber()+":"+arg2,arg1);
	 
	}
	public YakBaseException( final String arg1,final Throwable arg2) {
		super(arg1,arg2);
		logger.error("error:"+this.getStackTrace()[0].getClassName()+":"+this.getStackTrace()[0].getMethodName()+":"+this.getStackTrace()[0].getLineNumber()+":"+arg1,arg2);
	 
	}
	public YakBaseException(final String arg1) {
		super(arg1);
		logger.error("error:"+this.getStackTrace()[0].getClassName()+":"+this.getStackTrace()[0].getMethodName()+":"+this.getStackTrace()[0].getLineNumber()+":"+arg1);
	 }
}
