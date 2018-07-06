/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CallLogRestValidator.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.calllog;


import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import cloud.optim.callcentersolution.api.app.calllog.CallLogRequest.EditForm;
import cloud.optim.callcentersolution.api.entity.CallLog;
import cloud.optim.callcentersolution.api.entity.dao.CallLogDao;
import cloud.optim.callcentersolution.core.modules.loginutil.CustomUser;
import cloud.optim.callcentersolution.core.modules.loginutil.LoginUtility ;
import cloud.optim.callcentersolution.core.modules.rest.ResponseCode;
import cloud.optim.callcentersolution.core.modules.rest.RestException;
import cloud.optim.callcentersolution.core.modules.rest.RestResult;
import cloud.optim.callcentersolution.core.modules.rest.RestValidatorUtils;
import cloud.optim.callcentersolution.core.modules.rest.SortForm;
import cloud.optim.callcentersolution.core.modules.rest.SortForm.SortElement;
import cloud.optim.callcentersolution.core.modules.validator.ValidatorUtils;

/**
 * CallLogRestService のバリデータクラス
 * （入力チェックと入力内容の補完を行う）
 */
@Component
class CallLogRestValidator
{
	/** Commons Logging instance.  */
	@SuppressWarnings( "unused" )
	private Log log = LogFactory.getLog( this.getClass() ) ;

	/** LoginUtility */
	@Resource private LoginUtility loginUtility ;

	/** CallLogDao */
	@Resource private CallLogDao callLogDao;

	/**
	 * 検索の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForSearch( CallLogRequest req ) {

		String name = "";

		name = "#request";

		if ( req == null ) {

			throw new NullPointerException( name );
		}

		// ----- 取得条件（指定がなければ全検索）

		name = "#searchForm";

		SearchForm searchForm = req.getSearchForm();

		if ( searchForm == null ) searchForm = new SearchForm();

		req.setSearchForm( searchForm );

		// ----- ソート条件

		name = "#sortForm";

		SortForm sortForm = searchForm.getSortForm();

		if ( sortForm == null ) {
			sortForm = new SortForm();

			searchForm.setSortForm( sortForm );
			sortForm.addSortElement( new SortElement( "callLog.startDate", true ) ); // デフォルトは 通話開始日時 の昇順

		}

		// TODO 上限値を設定？
		// sortForm.setMaxResult(maxResult);;

		RestValidatorUtils.sortValidate( sortForm );
		RestValidatorUtils.sortConvert( sortForm );

		// ----- 検索条件

		name = "#callLogForm";

		CallLogSearchForm callLogForm = searchForm.getCallLog();

		if ( callLogForm == null ) callLogForm = new CallLogSearchForm();

		searchForm.setCallLog( callLogForm );

		// ユーザ情報取得
		CustomUser customUser = loginUtility.getCustomUser();

		// ----- 企業 ID
		// ユーザ情報の企業IDを設定する
		callLogForm.setCompanyId(customUser.getCompanyId());

		// ----- ユーザ ID
		// ユーザ情報のユーザIDを設定する
		callLogForm.setUserId(customUser.getUserId());

		// ----- 開始日時From
		{
			name = "#callLog.startDateFrom" ;
			Date value = callLogForm.getStartDateFrom() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, null, null );
		}

		// ----- 開始日時To
		{
			name = "#callLog.startDateTo" ;
			Date value = callLogForm.getStartDateTo() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, null, null );
		}
	}


	// -------------------------------------------------------------------------

	/**
	 * 検索の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForSearchByName( CallLogRequest req ) {

		String name = "";

		name = "#request";

		if ( req == null ) {

			throw new NullPointerException( name );
		}

		// ----- 取得条件（指定がなければ全検索）

		name = "#searchForm";

		SearchForm searchForm = req.getSearchForm();

		if ( searchForm == null ) searchForm = new SearchForm();

		req.setSearchForm( searchForm );

		// ----- ソート条件

		name = "#sortForm";

		SortForm sortForm = searchForm.getSortForm();

		if ( sortForm == null ) {
			sortForm = new SortForm();

			searchForm.setSortForm( sortForm );
			sortForm.addSortElement( new SortElement( "callLog.startDate", true ) ); // デフォルトは 通話開始日時 の昇順

		}

		// TODO 上限値を設定？
		// sortForm.setMaxResult(maxResult);;

		RestValidatorUtils.sortValidate( sortForm );
		RestValidatorUtils.sortConvert( sortForm );

		// ----- 検索条件

		name = "#callLogForm";

		CallLogSearchForm callLogForm = searchForm.getCallLog();

		if ( callLogForm == null ) callLogForm = new CallLogSearchForm();

		searchForm.setCallLog( callLogForm );

		// ユーザ情報取得
		CustomUser customUser = loginUtility.getCustomUser();

		// ----- 企業 ID
		// ユーザ情報の企業IDを設定する
		callLogForm.setCompanyId(customUser.getCompanyId());

		// ----- 開始日時From
		{
			name = "#callLog.startDateFrom" ;
			Date value = callLogForm.getStartDateFrom() ;

			RestValidatorUtils.fieldValidate( name, value, false, null, null, null );
		}

		// ----- 開始日時To
		{
			name = "#callLog.startDateTo" ;
			Date value = callLogForm.getStartDateTo() ;

			RestValidatorUtils.fieldValidate( name, value, false, null, null, null );
		}

		// ----- ユーザ名
		{
			name = "#callLog.userNameOption";
			String value = callLogForm.getUserNameOption();

			RestValidatorUtils.fieldValidate( name, value,
				ValidatorUtils.required( callLogForm.getUserName() ), null, null );
			RestValidatorUtils.in( name, value, "0", "1", "2", "3" );
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * CallLog 取得の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForGet( CallLogRequest req ) {

		String name = "";

		// ----- 取得条件

		name = "#request";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#editForm";

		EditForm editForm = req.getEditForm();

		if ( editForm == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#callLog";

		CallLog callLog = editForm.getCallLog();

		if ( callLog == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		// ----- PK

		name = "#callLog.callLogId";
		Long pkvalue = callLog.getCallLogId();

		RestValidatorUtils.fieldValidate( name, pkvalue, true, null, null );



	}


	// -------------------------------------------------------------------------

	/**
	 * CallLog 取得の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForGetByNo( CallLogRequest req ) {

		String name = "";

		// ----- 取得条件

		name = "#request";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#editForm";

		EditForm editForm = req.getEditForm();

		if ( editForm == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#callLog";

		CallLog callLog = editForm.getCallLog();

		if ( callLog == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		// ----- PK

		name = "#callLog.callLogNo";
		Long pkvalue = callLog.getCallLogNo();

		RestValidatorUtils.fieldValidate( name, pkvalue, true, null, null );



	}

	/**
	 * CallLog 音声生成の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForGenerateVoice( CallLogRequest req ) {

		String name = "";

		// ----- 取得条件

		name = "#request";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#editForm";

		EditForm editForm = req.getEditForm();

		if ( editForm == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#callLog";

		CallLog callLog = editForm.getCallLog();

		if ( callLog == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		// ----- PK

		name = "#callLog.callLogId";
		Long pkvalue = callLog.getCallLogId();

		RestValidatorUtils.fieldValidate( name, pkvalue, true, null, null );



	}


	// -------------------------------------------------------------------------

	/**
	 * 削除の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForDelete( CallLogRequest req ) {

		String name = "";

		// ----- 削除条件

		name = "#request";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#bulkFormList";

		List<SearchResult> bulkFormList = req.getBulkFormList();

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
	public CallLog validateForDeleteOne( SearchResult req ) {

		String name = "";

		// ----- 削除条件

		name = "#bulkForm";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#callLog";

		CallLogSearchResult callLog = req.getCallLog();

		if ( callLog == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		// ----- PK

		name = "#callLog.callLogId";
		Long pkvalue = callLog.getCallLogId();

		RestValidatorUtils.fieldValidate( name, pkvalue, true, null, null );

		// 存在チェック

		CallLog entity = callLogDao.get( pkvalue );

		if ( entity == null ) {

			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name ) );
		}

		// 論理削除されている
		if ( entity.getDeleteDate() != null ) { 
			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name ) ) ;
		}

		// ユーザ情報取得
		CustomUser customUser = loginUtility.getCustomUser() ;

		// 企業IDのチェック
		if ( !customUser.getCompanyId().equals(entity.getCompanyId()) ) {
			 // 存在しない
			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name ) ) ;
		}

		// ----- 更新ユーザ ID
		entity.setUpdateUserId( customUser.getUserId() );

		// ----- 更新ユーザ名
		entity.setUpdateUserName( customUser.getUserName() );


		return entity;

	}


}
