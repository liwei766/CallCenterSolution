/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：CallRestValidator.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.call;


import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import cloud.optim.callcentersolution.api.entity.dao.CallLogDao;
import cloud.optim.callcentersolution.core.modules.loginutil.LoginUtility;
import cloud.optim.callcentersolution.core.modules.rest.ResponseCode;
import cloud.optim.callcentersolution.core.modules.rest.RestException;
import cloud.optim.callcentersolution.core.modules.rest.RestResult;
import cloud.optim.callcentersolution.core.modules.rest.RestValidatorUtils;

/**
 * CallRestValidator のバリデータクラス
 * （入力チェックと入力内容の補完を行う）
 */
@Component
class CallRestValidator
{
	/** Commons Logging instance.  */
	@SuppressWarnings( "unused" )
	private Log log = LogFactory.getLog( this.getClass() ) ;

	/** LoginUtility */
	@Resource private LoginUtility loginUtility ;

	/** CallLogDao */
	@Resource private CallLogDao callLogDao;

	/**
	 * 通話開始の入力チェック.
	 *
	 * @param req 入力内容
	 */
	public void validateForStart( CallRequest req ) {

		String name = "";

		// ********** リクエスト

		name = "#request";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) ) ;
		}
	}

	/**
	 * 通話再開の入力チェック.
	 *
	 * @param req 入力内容
	 */
	public void validateForResume( CallRequest req ) {


		String name = "";

		// ********** リクエスト

		name = "#request";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) ) ;
		}

		// ********** 個別項目

		// ----- 通話ログ ID

		{
			name = "#callLog.callLogId" ;
			Long value = req.getCallLogId() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, null );
			RestValidatorUtils.fieldValidate( name, String.valueOf( value ), false, null, 19 ) ;
		}
	}
}


