/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：KnowledgeRestValidator.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.keyword;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cloud.optim.callcentersolution.core.common.utility.HankakuKanaConverter;
import cloud.optim.callcentersolution.core.modules.validator.ValidatorUtils;

/**
 * KnowledgeRestService のバリデータクラス
 * （入力チェックと入力内容の補完を行う）
 */
@Component
class KeywordRestValidator
{
	/** Commons Logging instance.  */
	@SuppressWarnings( "unused" )
	private Log log = LogFactory.getLog( this.getClass() ) ;


	@Value( "${keyword.max.extract.count}" )
	private int maxExtractCount;

	/**
	 * キーワード抽出の入力チェック.
	 *
	 * @param req 入力内容
	 */
	public void validateForExtract( KeywordRequest req ) {

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


		// ----- 解析テキスト

		// 半角カナを全角カナに変換する
		searchForm.setText(HankakuKanaConverter.convert(searchForm.getText()));
	}

	/**
	 * ナレッジキーワード抽出の入力チェック.
	 *
	 * @param req 入力内容
	 */
	public void validateForKnowledgeKeyword( KeywordRequest req ) {

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


		// ----- 解析テキスト

		// 半角カナを全角カナに変換する
		searchForm.setText(HankakuKanaConverter.convert(searchForm.getText()));


		// ----- 取得件数
		Integer value = searchForm.getExtractCount();
		if(value == null || value > maxExtractCount) searchForm.setExtractCount(maxExtractCount);
	}

	/**
	 * キーワード抽出の入力チェック.
	 *
	 * @param req 入力内容
	 */
	public void validateForGet( KeywordRequest req ) {

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


		// ----- 解析テキスト

		// トリムする
		searchForm.setText(ValidatorUtils.trim(searchForm.getText()));

		// 半角カナを全角カナに変換する
		searchForm.setText(HankakuKanaConverter.convert(searchForm.getText()));
	}


//
//
//	// -------------------------------------------------------------------------
//
//	/**
//	 * Knowledge 取得の入力チェックと入力内容の補完.
//	 *
//	 * @param req 入力内容
//	 */
//	public void validateForGet( KeywordRequest req ) {
//
//		String name = "";
//
//		// ----- 取得条件
//
//		name = "#request";
//
//		if ( req == null ) {
//
//			throw new RestException( new RestResult(
//				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
//		}
//
//		name = "#editForm";
//
//		EditForm editForm = req.getEditForm();
//
//		if ( editForm == null ) {
//
//			throw new RestException( new RestResult(
//				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
//		}
//
//		name = "#knowledge";
//
//		Knowledge knowledge = editForm.getKnowledge();
//
//		if ( knowledge == null ) {
//
//			throw new RestException( new RestResult(
//				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
//		}
//
//		// ----- PK
//
//		name = "#knowledge.knowledgeId";
//		Long pkvalue = knowledge.getKnowledgeId();
//
//		RestValidatorUtils.fieldValidate( name, pkvalue, true, null, null );
//	}
//
//
//	// -------------------------------------------------------------------------
//
//	/**
//	 * 登録の入力チェックと入力内容の補完.
//	 *
//	 * @param req 入力内容
//	 *
//	 * @throws SQLException 例外発生時
//	 */
//	public void validateForPut( KeywordRequest req ) throws SQLException {
//
//		String name = "";
//
//		// ********** 登録内容
//
//		name = "#request";
//
//		if ( req == null ) {
//
//			throw new RestException( new RestResult(
//				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
//		}
//
//		name = "#editForm";
//
//		EditForm editForm = req.getEditForm();
//
//		if ( editForm == null ) {
//
//			throw new RestException( new RestResult(
//				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
//		}
//
//		name = "#knowledge";
//
//		Knowledge knowledge = editForm.getKnowledge();
//
//		if ( knowledge == null ) {
//
//			throw new RestException( new RestResult(
//				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
//		}
//
//		// ********** 個別項目
//
//		// ユーザ情報からの設定
//		CustomUser customUser = loginUtility.getCustomUser();
//
//		// ----- タイトル
//
//		{
//			name = "#knowledge.title" ;
//			String value = knowledge.getTitle() ;
//
//			RestValidatorUtils.fieldValidate( name, value, true, null, 100 );
//
//			// タイトルの重複チェック
//			Knowledge example = new Knowledge();
//			example.setCompanyId(customUser.getCompanyId());
//			example.setTitle(value);
//
//			List<Knowledge> knowledgeList = knowledgeDao.findByExample(example);
//			if ( knowledgeList != null && !knowledgeList.isEmpty()) {
//				throw new RestException( new RestResult(
//					ResponseCode.KNOWLEDGE_DUPLICATE_TITLE, null, name ) );
//			}
//		}
//
//		// ----- 内容
//
//		{
//			name = "#knowledge.content" ;
//			String value = knowledge.getContent() ;
//
//			RestValidatorUtils.fieldValidate( name, value, true, null, -1 );
//		}
//
//		// ----- URL
//
//		{
//			name = "#knowledge.url" ;
//			String value = knowledge.getUrl() ;
//
//			RestValidatorUtils.fieldValidate( name, value, false, null, 255 );
//		}
//
//		// ----- マニュアル名
//
//		{
//			name = "#knowledge.manualName" ;
//			String value = knowledge.getManualName() ;
//
//			RestValidatorUtils.fieldValidate( name, value, false, null, 100 );
//
//			if ( ! ValidatorUtils.zenkakuAll( value ) )
//			{
//				throw new RestException( new RestResult(
//					ResponseCode.INPUT_ERROR_LETTER_TYPE, null, name ) );
//			}
//		}
//
//		// ----- マニュアルページ
//
//		{
//			name = "#knowledge.manualPage" ;
//			String value = knowledge.getManualPage() ;
//
//			RestValidatorUtils.fieldValidate( name, value, false, null, 10 );
//		}
//
//		// ----- マニュアル URL
//
//		{
//			name = "#knowledge.manualUrl" ;
//			String value = knowledge.getManualUrl() ;
//
//			RestValidatorUtils.fieldValidate( name, value, false, null, 255 );
//		}
//
//		// ----- スクリプト
//
//		{
//			name = "#knowledge.script" ;
//			String value = knowledge.getScript() ;
//
//			RestValidatorUtils.fieldValidate( name, value, false, null, -1 );
//
//			if ( ! ValidatorUtils.zenkakuAll( value ) )
//			{
//				throw new RestException( new RestResult(
//					ResponseCode.INPUT_ERROR_LETTER_TYPE, null, name ) );
//			}
//		}
//
//		// ----- 参照回数
//		knowledge.setClickCount(0L) ;
//
//		Date now = new Date();
//
//		// ----- 更新日時
//		knowledge.setUpdateDate(now) ;
//
//		// ----- 企業 ID
//		knowledge.setCompanyId( customUser.getCompanyId() );
//
//		// ----- 作成日時
//		knowledge.setCreateDate(now) ;
//
//		// ----- 作成ユーザ ID
//		knowledge.setCreateUserId( customUser.getUserId() );
//
//		// ----- 作成ユーザ名
//		knowledge.setCreateUserName( customUser.getUserName() );
//
//		// ----- 更新ユーザ ID
//		knowledge.setUpdateUserId( customUser.getUserId() );
//
//
//		// ----- 更新ユーザ名
//		knowledge.setUpdateUserName( customUser.getUserName() );
//
//
//		// タグのチェック
//		name = "#tag.tagName" ;
//
//		List<String> tags = editForm.getTag() ;
//		if ( tags == null || tags.isEmpty()) {
//			throw new RestException( new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
//		}
//
//		for(String tag : tags) {
//			RestValidatorUtils.fieldValidate( name, tag, true, null, 100 );
//		}
//	}
//
//
//	// -------------------------------------------------------------------------
//
//	/**
//	 * 更新の入力チェックと入力内容の補完.
//	 *
//	 * @param req 入力内容
//	 *
//	 * @throws SQLException 例外発生時
//	 */
//	public void validateForUpdate( KeywordRequest req ) throws SQLException {
//
//		String name = "";
//
//		// ********** 更新内容
//
//		name = "#request";
//
//		if ( req == null ) {
//
//			throw new RestException( new RestResult(
//				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) ) ;
//		}
//
//		name = "#editForm";
//
//		EditForm editForm = req.getEditForm();
//
//		if ( editForm == null ) {
//
//			throw new RestException( new RestResult(
//				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
//		}
//
//		name = "#knowledge";
//
//		Knowledge knowledge = editForm.getKnowledge();
//
//		if ( knowledge == null ) {
//
//			throw new RestException( new RestResult(
//				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
//		}
//
//		// ********** 個別項目
//
//		// ----- ナレッジ ID
//
//		{
//			name = "#knowledge.knowledgeId" ;
//			Long value = knowledge.getKnowledgeId() ;
//
//
//			RestValidatorUtils.fieldValidate( name, value, true, null, null );
//			RestValidatorUtils.fieldValidate( name, String.valueOf( value ), false, null, 19 ) ;
//
//			// 存在チェック
//
//			Knowledge entity = knowledgeDao.get( value ) ;
//
//			if ( entity == null ) // 存在しない
//			{
//				throw new RestException( new RestResult(
//					ResponseCode.NOT_FOUND, null, name, value ) ) ;
//			}
//		}
//
//		// ----- 更新日時
//
//		{
//			name = "#knowledge.updateDate" ;
//			Date value = knowledge.getUpdateDate() ;
//
//			RestValidatorUtils.fieldValidate( name, value, true, null, null, null );
//		}
//
//		// ----- 企業 ID
//
//		{
//			name = "#knowledge.companyId" ;
//			String value = knowledge.getCompanyId() ;
//
//			RestValidatorUtils.fieldValidate( name, value, true, null, 32 );
//		}
//
//		// ----- タイトル
//
//		{
//			name = "#knowledge.title" ;
//			String value = knowledge.getTitle() ;
//
//			RestValidatorUtils.fieldValidate( name, value, true, null, 100 );
//		}
//
//		// ----- 内容
//
//		{
//			name = "#knowledge.content" ;
//			String value = knowledge.getContent() ;
//
//			RestValidatorUtils.fieldValidate( name, value, true, null, 255 );
//		}
//
//		// ----- URL
//
//		{
//			name = "#knowledge.url" ;
//			String value = knowledge.getUrl() ;
//
//			RestValidatorUtils.fieldValidate( name, value, false, null, 255 );
//		}
//
//		// ----- マニュアル名
//
//		{
//			name = "#knowledge.manualName" ;
//			String value = knowledge.getManualName() ;
//
//			RestValidatorUtils.fieldValidate( name, value, false, null, 100 );
//
//			if ( ! ValidatorUtils.zenkakuAll( value ) )
//			{
//				throw new RestException( new RestResult(
//					ResponseCode.INPUT_ERROR_LETTER_TYPE, null, name ) );
//			}
//		}
//
//		// ----- マニュアルページ
//
//		{
//			name = "#knowledge.manualPage" ;
//			String value = knowledge.getManualPage() ;
//
//			RestValidatorUtils.fieldValidate( name, value, false, null, 10 );
//		}
//
//		// ----- マニュアル URL
//
//		{
//			name = "#knowledge.manualUrl" ;
//			String value = knowledge.getManualUrl() ;
//
//			RestValidatorUtils.fieldValidate( name, value, false, null, 255 );
//		}
//
//		// ----- スクリプト
//
//		{
//			name = "#knowledge.script" ;
//			String value = knowledge.getScript() ;
//
//			RestValidatorUtils.fieldValidate( name, value, false, null, 255 );
//
//			if ( ! ValidatorUtils.zenkakuAll( value ) )
//			{
//				throw new RestException( new RestResult(
//					ResponseCode.INPUT_ERROR_LETTER_TYPE, null, name ) );
//			}
//		}
//
//		// ----- 参照回数
//
//		{
//			name = "#knowledge.clickCount" ;
//			Long value = knowledge.getClickCount() ;
//
//
//			RestValidatorUtils.fieldValidate( name, value, true, null, null );
//			RestValidatorUtils.fieldValidate( name, String.valueOf( value ), false, null, 19 ) ;
//		}
//
//		// ----- 作成日時
//
//		{
//			name = "#knowledge.createDate" ;
//			Date value = knowledge.getCreateDate() ;
//
//			RestValidatorUtils.fieldValidate( name, value, true, null, null, null );
//		}
//
//		// ----- 作成ユーザ ID
//
//		{
//			name = "#knowledge.createUserId" ;
//			String value = knowledge.getCreateUserId() ;
//
//			RestValidatorUtils.fieldValidate( name, value, true, null, 32 );
//		}
//
//		// ----- 作成ユーザ名
//
//		{
//			name = "#knowledge.createUserName" ;
//			String value = knowledge.getCreateUserName() ;
//
//			RestValidatorUtils.fieldValidate( name, value, true, null, 32 );
//		}
//
//		// ----- 更新ユーザ ID
//
//		{
//			name = "#knowledge.updateUserId" ;
//			String value = knowledge.getUpdateUserId() ;
//
//			// 常にログインユーザ ID を設定
//			value = loginUtility.getLoginName() ;
//
//			RestValidatorUtils.fieldValidate( name, value, true, null, 32 );
//
//			// 補完内容を反映
//			knowledge.setUpdateUserId( value );
//		}
//
//		// ----- 更新ユーザ名
//
//		{
//			name = "#knowledge.updateUserName" ;
//			String value = knowledge.getUpdateUserName() ;
//
//			RestValidatorUtils.fieldValidate( name, value, true, null, 32 );
//		}
//
//	}
}
