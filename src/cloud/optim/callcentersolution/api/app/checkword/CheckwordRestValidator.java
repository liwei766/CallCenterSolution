/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CheckwordRestValidator.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.checkword;


import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cloud.optim.callcentersolution.api.app.checkword.CheckwordRequest.EditForm;
import cloud.optim.callcentersolution.api.entity.Checkword;
import cloud.optim.callcentersolution.api.entity.dao.CheckwordDao;
import cloud.optim.callcentersolution.core.modules.loginutil.LoginUtility ;
import cloud.optim.callcentersolution.core.modules.rest.ResponseCode;
import cloud.optim.callcentersolution.core.modules.rest.RestException;
import cloud.optim.callcentersolution.core.modules.rest.RestResult;
import cloud.optim.callcentersolution.core.modules.rest.RestValidatorUtils;
import cloud.optim.callcentersolution.core.modules.rest.SortForm;
import cloud.optim.callcentersolution.core.modules.rest.SortForm.SortElement;
import cloud.optim.callcentersolution.core.modules.validator.ValidatorUtils;

/**
 * CheckwordRestService のバリデータクラス
 * （入力チェックと入力内容の補完を行う）
 */
@Component
class CheckwordRestValidator
{
	/** Commons Logging instance.  */
	@SuppressWarnings( "unused" )
	private Log log = LogFactory.getLog( this.getClass() ) ;

	/** LoginUtility */
	@Resource private LoginUtility loginUtility ;

	/** CheckwordDao */
	@Resource private CheckwordDao checkwordDao;

	/** チェックワードカラー名称の範囲 */
	@Value( "${checkword.import.colorThemes}" )
	private String[] colorThemes;

	private Set<String> checkwordsSet;

	/**
	 * 検索の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForSearch( CheckwordRequest req ) {

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

		// ----- ソート条件（指定がなければソートしない）

		name = "#sortForm";

		SortForm sortForm = searchForm.getSortForm();

		if ( sortForm == null ) {
			sortForm = new SortForm();
			sortForm.addSortElement( new SortElement( "checkword.checkwordId" ) ); // デフォルトは PK の降順
			searchForm.setSortForm( sortForm );
		}


		RestValidatorUtils.sortValidate( sortForm );
		RestValidatorUtils.sortConvert( sortForm );

		// ----- 検索条件

		name = "#checkwordForm";

		CheckwordSearchForm checkwordForm = searchForm.getCheckword();

		if ( checkwordForm == null ) checkwordForm = new CheckwordSearchForm();

		searchForm.setCheckword( checkwordForm );

		// ----- 企業 ID

		{
			name = "#checkword.companyIdOption";
			String value = checkwordForm.getCompanyIdOption();

			RestValidatorUtils.fieldValidate( name, value,
				ValidatorUtils.required( checkwordForm.getCompanyId() ), null, null );
			RestValidatorUtils.in( name, value, "0", "1", "2", "3" );
		}

		// ----- チェックワード内容

		{
			name = "#checkword.checkwordOption";
			String value = checkwordForm.getCheckwordOption();

			RestValidatorUtils.fieldValidate( name, value,
				ValidatorUtils.required( checkwordForm.getCheckword() ), null, null );
			RestValidatorUtils.in( name, value, "0", "1", "2", "3" );
		}

		// ----- ワードカラー名称

		{
			name = "#checkword.colorThemeOption";
			String value = checkwordForm.getColorThemeOption();

			RestValidatorUtils.fieldValidate( name, value,
				ValidatorUtils.required( checkwordForm.getColorTheme() ), null, null );
			RestValidatorUtils.in( name, value, "0", "1", "2", "3" );
		}

	}


	// -------------------------------------------------------------------------

	/**
	 * Checkword 取得の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForGet( CheckwordRequest req ) {

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

		name = "#checkword";

		Checkword checkword = editForm.getCheckword();

		if ( checkword == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		// ----- PK

		name = "#checkword.checkwordId";
		Long pkvalue = checkword.getCheckwordId();

		RestValidatorUtils.fieldValidate( name, pkvalue, true, null, null );
	}

	// -------------------------------------------------------------------------
	public void resetCheckwordsSet() {
		this.checkwordsSet = new HashSet<>();
	}
	// -------------------------------------------------------------------------

	public void validateForImp( Checkword checkword ) {

		String val = null;

		// ---------------- チェックワード内容 入力チェック：必須チェックと桁数チェック

		val = checkword.getCheckword();

		if (  val == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, val ) );
		}

		// トリムする
		val = ValidatorUtils.trim(val);

		// 重複チェック
		if(!checkwordsSet.add(val)) {
			throw new RestException( new RestResult(ResponseCode.CHECKWORD_CSV_REPEAT, null, val ) ) ;
		}

		// 文字数チェック(チェックワード内容のバイト数が1~100)
		if (!ValidatorUtils.bytelength(val, 1, 100)) {
			throw new RestException( new RestResult(ResponseCode.INPUT_ERROR_LENGTH, null, val ) ) ;
		}

		// ---------------- ワードカラー名称 入力チェック：必須チェックと桁数チェック

		val = checkword.getColorTheme();

		if ( val == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, val ) );
		}

		val = ValidatorUtils.trim(val);

		// 文字数チェック(ワードカラーのバイト数が1~20)
		if (!ValidatorUtils.bytelength(val, 1, 20)) {
			throw new RestException( new RestResult(ResponseCode.INPUT_ERROR_LENGTH, null, val ) ) ;
		}


		// ----------------ワードカラー名称が小文字にして、プロパティファイルに存在するかチェックする
		List<String> colorThemeList = Arrays.asList(colorThemes);

		String colorTheme = val.toLowerCase();

		if(!colorThemeList.contains(colorTheme)) {
			throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_ENUM, null, colorTheme ) );
		}

		checkword.setColorTheme(colorTheme);


	}


	// -------------------------------------------------------------------------

	/**
	 * 登録の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 *
	 * @throws SQLException 例外発生時
	 */
	public void validateForPut( CheckwordRequest req ) throws SQLException {

		String name = "";

		// ********** 登録内容

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

		name = "#checkword";

		Checkword checkword = editForm.getCheckword();

		if ( checkword == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		// ********** 個別項目

		// ----- チェックワードID

		{
			name = "#checkword.checkwordId" ;
			Long value = checkword.getCheckwordId() ;

			RestValidatorUtils.fieldValidate( name, value, false, null, null );
			RestValidatorUtils.fieldValidate( name, String.valueOf( value ), false, null, 19 ) ;
		}

		// ----- 更新日時

		{
			name = "#checkword.updateDate" ;
			Date value = checkword.getUpdateDate() ;

			RestValidatorUtils.fieldValidate( name, value, false, null, null, null );
		}

		// ----- 企業 ID

		{
			name = "#checkword.companyId" ;
			String value = checkword.getCompanyId() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 32 );
		}

		// ----- チェックワード内容

		{
			name = "#checkword.checkword" ;
			String value = checkword.getCheckword() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 100 );
		}

		// ----- ワードカラー名称

		{
			name = "#checkword.colorTheme" ;
			String value = checkword.getColorTheme() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 20 );
		}

		// ----- 作成日時

		{
			name = "#checkword.createDate" ;
			Date value = checkword.getCreateDate() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, null, null );
		}

		// ----- 作成ユーザ ID

		{
			name = "#checkword.createUserId" ;
			String value = checkword.getCreateUserId() ;

			// 未設定の場合はログインユーザ ID を使用

			if ( ! ValidatorUtils.required( value ) )
			{
				value = loginUtility.getLoginName() ;
			}

			RestValidatorUtils.fieldValidate( name, value, true, null, 32 );

			// 補完内容を反映
			checkword.setCreateUserId( value );
		}

		// ----- 作成ユーザ名

		{
			name = "#checkword.createUserName" ;
			String value = checkword.getCreateUserName() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 100 );
		}

		// ----- 更新ユーザ ID

		{
			name = "#checkword.updateUserId" ;
			String value = checkword.getUpdateUserId() ;

			// 常にログインユーザ ID を設定
			value = loginUtility.getLoginName() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 32 );

			// 補完内容を反映
			checkword.setUpdateUserId( value );
		}

		// ----- 更新ユーザ名

		{
			name = "#checkword.updateUserName" ;
			String value = checkword.getUpdateUserName() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 100 );
		}

	}


	// -------------------------------------------------------------------------

	/**
	 * 更新の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 *
	 * @throws SQLException 例外発生時
	 */
	public void validateForUpdate( CheckwordRequest req ) throws SQLException {

		String name = "";

		// ********** 更新内容

		name = "#request";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) ) ;
		}

		name = "#editForm";

		EditForm editForm = req.getEditForm();

		if ( editForm == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#checkword";

		Checkword checkword = editForm.getCheckword();

		if ( checkword == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		// ********** 個別項目

		// ----- チェックワードID

		{
			name = "#checkword.checkwordId" ;
			Long value = checkword.getCheckwordId() ;


			RestValidatorUtils.fieldValidate( name, value, true, null, null );
			RestValidatorUtils.fieldValidate( name, String.valueOf( value ), false, null, 19 ) ;

			// 存在チェック

			Checkword entity = checkwordDao.get( value ) ;

			if ( entity == null ) // 存在しない
			{
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, name, value ) ) ;
			}
		}

		// ----- 更新日時

		{
			name = "#checkword.updateDate" ;
			Date value = checkword.getUpdateDate() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, null, null );
		}

		// ----- 企業 ID

		{
			name = "#checkword.companyId" ;
			String value = checkword.getCompanyId() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 32 );
		}

		// ----- チェックワード内容

		{
			name = "#checkword.checkword" ;
			String value = checkword.getCheckword() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 100 );
		}

		// ----- ワードカラー名称

		{
			name = "#checkword.colorTheme" ;
			String value = checkword.getColorTheme() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 20 );
		}

		// ----- 作成日時

		{
			name = "#checkword.createDate" ;
			Date value = checkword.getCreateDate() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, null, null );
		}

		// ----- 作成ユーザ ID

		{
			name = "#checkword.createUserId" ;
			String value = checkword.getCreateUserId() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 32 );
		}

		// ----- 作成ユーザ名

		{
			name = "#checkword.createUserName" ;
			String value = checkword.getCreateUserName() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 100 );
		}

		// ----- 更新ユーザ ID

		{
			name = "#checkword.updateUserId" ;
			String value = checkword.getUpdateUserId() ;

			// 常にログインユーザ ID を設定
			value = loginUtility.getLoginName() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 32 );

			// 補完内容を反映
			checkword.setUpdateUserId( value );
		}

		// ----- 更新ユーザ名

		{
			name = "#checkword.updateUserName" ;
			String value = checkword.getUpdateUserName() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 100 );
		}

	}


	// -------------------------------------------------------------------------

	/**
	 * 削除の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForDelete( CheckwordRequest req ) {

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
	public void validateForDeleteOne( SearchResult req ) {

		String name = "";

		// ----- 削除条件

		name = "#bulkForm";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#checkword";

		CheckwordSearchResult checkword = req.getCheckword();

		if ( checkword == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		// ----- PK

		name = "#checkword.checkwordId";
		Long pkvalue = checkword.getCheckwordId();

		RestValidatorUtils.fieldValidate( name, pkvalue, true, null, null );

		// 存在チェック

		Checkword entity = checkwordDao.get( pkvalue );

		if ( entity == null ) {

			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name ) );
		}
	}

}
