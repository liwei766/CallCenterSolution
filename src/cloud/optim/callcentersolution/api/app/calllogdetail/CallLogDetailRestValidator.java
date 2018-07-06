/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CallLogDetailRestValidator.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.calllogdetail;


import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import cloud.optim.callcentersolution.api.app.calllog.CallLogService;
import cloud.optim.callcentersolution.api.app.calllogdetail.CallLogDetailRequest.EditForm;
import cloud.optim.callcentersolution.api.entity.CallLog;
import cloud.optim.callcentersolution.api.entity.CallLogDetail;
import cloud.optim.callcentersolution.api.entity.dao.CallLogDetailDao;
import cloud.optim.callcentersolution.api.util.AuthUtil;
import cloud.optim.callcentersolution.core.common.utility.HankakuKanaConverter;
import cloud.optim.callcentersolution.core.modules.loginutil.CustomUser;
import cloud.optim.callcentersolution.core.modules.loginutil.LoginUtility ;
import cloud.optim.callcentersolution.core.modules.rest.ResponseCode;
import cloud.optim.callcentersolution.core.modules.rest.RestException;
import cloud.optim.callcentersolution.core.modules.rest.RestResult;
import cloud.optim.callcentersolution.core.modules.rest.RestValidatorUtils;

/**
 * CallLogDetailRestService のバリデータクラス
 * （入力チェックと入力内容の補完を行う）
 */
@Component
class CallLogDetailRestValidator
{
	/** Commons Logging instance.  */
	@SuppressWarnings( "unused" )
	private Log log = LogFactory.getLog( this.getClass() ) ;

	/** LoginUtility */
	@Resource private LoginUtility loginUtility ;

	/** AuthUtil */
	@Resource private AuthUtil authUtil;

	/** CallLogDetailDao */
	@Resource private CallLogDetailDao callLogDetailDao;

	/** CallLogService */
	@Resource private CallLogService callLogService;


	// -------------------------------------------------------------------------

	/**
	 * 更新の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForUpdate( CallLogDetailRequest req ) {

		String name = "";

		// ----- 削除条件

		name = "#request";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#bulkFormList";

		List<EditForm> bulkFormList = req.getBulkFormList();

		if ( bulkFormList == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}
	}

	/**
	 * 更新の入力チェックと入力内容の補完.
	 * 内容と更新者、更新日時以外は更新しない
	 *
	 * @param req 入力内容
	 *
	 * @throws SQLException 例外発生時
	 */
	public void validateForUpdateOne( EditForm req ) throws SQLException {

		String name = "";

		// ********** 更新内容

		name = "#editForm";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#callLogDetail";

		CallLogDetail callLogDetail = req.getCallLogDetail();

		if ( callLogDetail == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		// ********** 個別項目

		CustomUser customUser = loginUtility.getCustomUser() ;

		// ----- 通話ログ詳細 ID

		{
			name = "#callLogDetail.callLogDetailId" ;
			Long value = callLogDetail.getCallLogDetailId() ;


			RestValidatorUtils.fieldValidate( name, value, true, null, null );
			RestValidatorUtils.fieldValidate( name, String.valueOf( value ), false, null, 19 ) ;

		}

		// 存在チェック
		CallLogDetail entity = callLogDetailDao.get( callLogDetail.getCallLogDetailId() ) ;
		{
			Long value = callLogDetail.getCallLogDetailId() ;
			name = "#callLogDetail.callLogDetailId" ;

			if ( entity == null ) // 存在しない
			{
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, name, value ) ) ;
			}

			// 企業IDのチェック

			if ( !customUser.getCompanyId().equals(entity.getCompanyId()) ) // 存在しない
			{
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, name, value ) ) ;
			}

			// 削除日時
			if ( entity.getDeleteDate() != null ) // 存在しない
			{
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, name, value ) ) ;
			}

			// 参照可否チェック
			// 企業管理者権限を保持している場合には、ユーザ企業ID配下の全ユーザの通話ログを取得可能
			CallLog callLog = null;
			if (this.authUtil.isAdmin()) {
				callLog = callLogService.getCallLogCompanyAllUser(entity.getCallLogId(), customUser.getCompanyId());
			} else {
				callLog = callLogService.getCallLog(entity.getCallLogId(), customUser.getCompanyId(), customUser.getUserId());
			}

			if (callLog == null )
			{
				throw new RestException( new RestResult(
						ResponseCode.NOT_FOUND, null, name, value ) ) ;
			}
		}


		// ----- 更新日時

		{
			name = "#callLogDetail.updateDate" ;
			Date value = callLogDetail.getUpdateDate() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, null, null );
		}

		// ----- 企業 ID
		// 更新しないのでDBから取得した値で上書き
		callLogDetail.setCompanyId(entity.getCompanyId());

		// ----- 通話ログ ID
		// 更新しないのでDBから取得した値で上書き
		callLogDetail.setCallLogId(entity.getCallLogId());

		// ----- 内容

		{
			name = "#callLogDetail.log" ;
			String value = callLogDetail.getLog() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 2147483647 );

			// 半角カナを全角カナに変換
			callLogDetail.setLog(HankakuKanaConverter.convert(value));
		}

		// ----- 開始秒数
		// 更新しないのでDBから取得した値で上書き
		callLogDetail.setBegin(entity.getBegin());

		// ----- 終了秒数
		// 更新しないのでDBから取得した値で上書き
		callLogDetail.setEnd(entity.getEnd());

		// ----- 音声有無フラグ
		// 更新しないのでDBから取得した値で上書き
		callLogDetail.setVoiceExistence(entity.getVoiceExistence());

		// ----- 作成日時
		// 更新しないのでDBから取得した値で上書き
		callLogDetail.setCreateDate(entity.getCreateDate());

		// ----- 作成ユーザ ID
		// 更新しないのでDBから取得した値で上書き
		callLogDetail.setCreateUserId(entity.getCreateUserId());

		// ----- 作成ユーザ名
		// 更新しないのでDBから取得した値で上書き
		callLogDetail.setCreateUserName(entity.getCreateUserName());


		// ----- 更新ユーザ ID

		{
			name = "#callLogDetail.updateUserId" ;
			String value = callLogDetail.getUpdateUserId() ;

			// 常にログインユーザ ID を設定
			value = customUser.getUserId() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 32 );

			// 補完内容を反映
			callLogDetail.setUpdateUserId( value );
		}

		// ----- 更新ユーザ名

		{
			name = "#callLogDetail.updateUserName" ;
			String value = callLogDetail.getUpdateUserName() ;

			// 常にログインユーザ 名 を設定
			value = customUser.getUserName() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 100 );

			// 補完内容を反映
			callLogDetail.setUpdateUserName( value );
		}

		// ----- 削除日時
		// 更新しないのでDBから取得した値で上書き
		callLogDetail.setDeleteDate(entity.getDeleteDate());

	}


	// -------------------------------------------------------------------------

	/**
	 * 削除の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForDelete( CallLogDetailRequest req ) {

		String name = "";

		// ----- 削除条件

		name = "#request";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#bulkFormList";

		List<EditForm> bulkFormList = req.getBulkFormList();

		if ( bulkFormList == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}
	}

	/**
	 * 削除の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public CallLogDetail validateForDeleteOne( EditForm req ) {

		String name = "";

		// ----- 削除条件

		name = "#editForm";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#callLogDetail";

		CallLogDetail callLogDetail = req.getCallLogDetail();

		if ( callLogDetail == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		// ----- PK

		name = "#callLogDetail.callLogDetailId";
		Long pkvalue = callLogDetail.getCallLogDetailId();

		RestValidatorUtils.fieldValidate( name, pkvalue, true, null, null );

		// 存在チェック
		CustomUser customUser = loginUtility.getCustomUser() ;

		CallLogDetail entity = callLogDetailDao.get( pkvalue );

		if ( entity == null ) {

			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name ) );
		}

		// 企業IDのチェック
		if ( !customUser.getCompanyId().equals(entity.getCompanyId()) ) // 存在しない
		{
			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name ) ) ;
		}

		// 削除日時
		if ( entity.getDeleteDate() != null ) // 存在しない
		{
			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name ) ) ;
		}

		// 参照可否チェック
		// 企業管理者権限を保持している場合には、ユーザ企業ID配下の全ユーザの通話ログを取得可能
		CallLog callLog = null;
		if (this.authUtil.isAdmin()) {
			callLog = callLogService.getCallLogCompanyAllUser(entity.getCallLogId(), customUser.getCompanyId());
		} else {
			callLog = callLogService.getCallLog(entity.getCallLogId(), customUser.getCompanyId(), customUser.getUserId());
		}
		
		if (callLog == null )
		{
			throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, name ) ) ;
		}


		// ----- 更新ユーザ ID
		entity.setUpdateUserId( customUser.getUserId() );

		// ----- 更新ユーザ名
		entity.setUpdateUserName( customUser.getUserName() );

		return entity;
	}

	/**
	 * 音声ファイルダウンロードの入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public CallLogDetail validateForVoice( Long req ) {

		String name = "";

		// ----- 削除条件

		name = "#callLogDetailId";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		CustomUser customUser = loginUtility.getCustomUser();

		// 存在チェック
		CallLogDetail entity = callLogDetailDao.get( req );
		if ( entity == null ) {

			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name ) );
		}

		// 企業IDのチェック
		if (!customUser.getCompanyId().equals(entity.getCompanyId())) {

			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name, req ) );
		}

		// 削除日時のチェック
		if (entity.getDeleteDate() != null) {

			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name, req ) );
		}

		// 参照可否チェック
		// 企業管理者権限を保持している場合には、ユーザ企業ID配下の全ユーザの通話ログを取得可能
		CallLog callLog = null;
		if (this.authUtil.isAdmin()) {
			callLog = callLogService.getCallLogCompanyAllUser(entity.getCallLogId(), customUser.getCompanyId());
		} else {
			callLog = callLogService.getCallLog(entity.getCallLogId(), customUser.getCompanyId(), customUser.getUserId());
		}

		if (callLog == null )
		{
			throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, name, req ) ) ;
		}

		return entity;
	}

}
