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
package cloud.optim.callcentersolution.api.app.knowledge;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cloud.optim.callcentersolution.api.app.knowledge.KnowledgeRequest.EditForm;
import cloud.optim.callcentersolution.api.entity.Knowledge;
import cloud.optim.callcentersolution.api.entity.Manual;
import cloud.optim.callcentersolution.api.entity.dao.KnowledgeDao;
import cloud.optim.callcentersolution.core.common.utility.HankakuKanaConverter;
import cloud.optim.callcentersolution.core.modules.loginutil.CustomUser;
import cloud.optim.callcentersolution.core.modules.loginutil.LoginUtility;
import cloud.optim.callcentersolution.core.modules.rest.ResponseCode;
import cloud.optim.callcentersolution.core.modules.rest.RestException;
import cloud.optim.callcentersolution.core.modules.rest.RestResult;
import cloud.optim.callcentersolution.core.modules.rest.RestValidatorUtils;
import cloud.optim.callcentersolution.core.modules.rest.SortForm;
import cloud.optim.callcentersolution.core.modules.rest.SortForm.SortElement;
import cloud.optim.callcentersolution.core.modules.validator.ValidatorUtils;

/**
 * KnowledgeRestService のバリデータクラス
 * （入力チェックと入力内容の補完を行う）
 */
@Component
class KnowledgeRestValidator
{
	/** Commons Logging instance.  */
	@SuppressWarnings( "unused" )
	private Log log = LogFactory.getLog( this.getClass() ) ;

	/** LoginUtility */
	@Resource private LoginUtility loginUtility ;

	/** KnowledgeDao */
	@Resource private KnowledgeDao knowledgeDao;

	/** 表示順：参照回数 */
	private static final String SEARCH_ORDER_CLICK_COUNT = "0";

	/** 表示順：更新日時 */
	private static final String SEARCH_ORDER_UPDATE_DATE = "1";

	/** 最大取得件数 */
	@Value( "${knowledge.max.result.count}" )
	private long maxResultCount;

	/** 最大参照先IMPORT/EXPORT数 */
	@Value( "${knowledge.reference.limit}" )
	private int maxReferenceCount;

	/** 最大マニュアルIMPORT/EXPORT数 */
	@Value( "${knowledge.manual.limit}" )
	private int maxManualCount;

	/**
	 * 検索の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForSearch( KnowledgeRequest req ) {

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


		// ----- 検索条件

		name = "#knowledgeForm";

		KnowledgeSearchForm knowledgeForm = searchForm.getKnowledge();

		if ( knowledgeForm == null ) knowledgeForm = new KnowledgeSearchForm();

		searchForm.setKnowledge( knowledgeForm );


		// ----- ソート条件

		name = "#sortForm";

		SortForm sortForm = searchForm.getSortForm();

		if ( sortForm == null ) {
			sortForm = new SortForm();
			searchForm.setSortForm( sortForm );
		}

		// 検索条件の設定
		if (sortForm.getSortElement() == null || sortForm.getSortElement().isEmpty()) {
			sortForm.setSortElement(new ArrayList<>());
			sortForm.addSortElement( new SortElement( "knowledge.clickCount", false ) );
			sortForm.addSortElement( new SortElement( "knowledge.knowledgeId", true ) );
		}

		// sortForm.addSortElement( new SortElement( "knowledge.updateDate", false ) );

		// 最大取得件数 リクエストの件数+1で検索する
		Long limit = sortForm.getMaxResult();
		// 最大取得件数がnull、負の値、システム上限以上の場合はシステム上限値+1を設定する
		if (limit == null || limit < 0L || limit > maxResultCount) sortForm.setMaxResult(maxResultCount);
		sortForm.setMaxResult(sortForm.getMaxResult());

		// オフセットはとりあえずNULLを設定(今後使用する場合は要削除)
		sortForm.setOffset(null);

		RestValidatorUtils.sortValidate( sortForm );
		RestValidatorUtils.sortConvert( sortForm );

		// ----- 企業ID

		{
			// ユーザ情報からの設定
			CustomUser customUser = loginUtility.getCustomUser();
			knowledgeForm.setCompanyId(customUser.getCompanyId());

		}

		// ----- タイトル

		{
			name = "#knowledge.title";
			String value = knowledgeForm.getTitle();

			// 半角カナを全角カナに変換する
			knowledgeForm.setTitle(HankakuKanaConverter.convert(value));

		}

		// ----- タイトル検索オプション

		{
			name = "#knowledge.titleOption";
			String value = knowledgeForm.getTitleOption();

			// タイトルが指定されている場合は必須
			RestValidatorUtils.fieldValidate( name, value, ValidatorUtils.required( knowledgeForm.getTitle() ), null, null );
			RestValidatorUtils.in( name, value, "0", "1", "2", "3" );
		}

	}


	// -------------------------------------------------------------------------

	/**
	 * Knowledge 取得の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForGet( KnowledgeRequest req ) {

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

		name = "#knowledge";

		Knowledge knowledge = editForm.getKnowledge();

		if ( knowledge == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		// ----- PK

		name = "#knowledge.knowledgeId";
		Long pkvalue = knowledge.getKnowledgeId();

		RestValidatorUtils.fieldValidate( name, pkvalue, true, null, null );
	}


	// -------------------------------------------------------------------------

	/**
	 * 登録の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 *
	 * @throws SQLException 例外発生時
	 */
	public void validateForPut( KnowledgeRequest req, boolean checkDuplicateTitle ) throws SQLException {

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

		name = "#knowledge";

		Knowledge knowledge = editForm.getKnowledge();

		if ( knowledge == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}


		// ----- ナレッジ ID(自動採番のためnullを設定)
		knowledge.setKnowledgeId(null) ;

		// ********** 個別項目
		validateKnowledge(knowledge, checkDuplicateTitle);

		// ユーザ情報の取得
		CustomUser customUser = loginUtility.getCustomUser();

		Date now = new Date();

		// ----- 更新日時
		knowledge.setUpdateDate(now) ;

		// ----- 企業 ID
		knowledge.setCompanyId( customUser.getCompanyId() );

		// ----- 作成日時
		knowledge.setCreateDate(now) ;

		// ----- 作成ユーザ ID
		knowledge.setCreateUserId( customUser.getUserId() );

		// ----- 作成ユーザ名
		knowledge.setCreateUserName( customUser.getUserName() );

		// ----- 更新ユーザ ID
		knowledge.setUpdateUserId( customUser.getUserId() );


		// ----- 更新ユーザ名
		knowledge.setUpdateUserName( customUser.getUserName() );

		// タグのチェック
		validateTag(editForm.getTag());

		// 参照先のチェック
		validateReference(editForm.getReference());

		// マニュアルのチェック
		validateManual(editForm.getManual());
	}


	// -------------------------------------------------------------------------

	/**
	 * 更新の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 *
	 * @throws SQLException 例外発生時
	 */
	public void validateForUpdate( KnowledgeRequest req ) throws SQLException {

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

		name = "#knowledge";

		Knowledge knowledge = editForm.getKnowledge();

		if ( knowledge == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		// ********** 個別項目
		// ユーザ情報の取得
		CustomUser customUser = loginUtility.getCustomUser();

		// ----- ナレッジ ID

		Knowledge entity;
		{
			name = "#knowledge.knowledgeId" ;
			Long value = knowledge.getKnowledgeId() ;


			RestValidatorUtils.fieldValidate( name, value, true, null, null );
			RestValidatorUtils.fieldValidate( name, String.valueOf( value ), false, null, 19 ) ;

			// 存在チェック

			entity = knowledgeDao.get( value ) ;

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

			// ----- ナレッジ番号

			if ( !entity.getKnowledgeNo().equals(knowledge.getKnowledgeNo()) ) // 存在しない
			{
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, name, value ) ) ;
			}
		}

		// ----- 更新日時

		{
			name = "#knowledge.updateDate" ;
			Date value = knowledge.getUpdateDate() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, null, null );
		}

		// ----- 企業 ID

		{
			name = "#knowledge.companyId" ;
			String value = knowledge.getCompanyId() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 32 );

			// 企業IDのチェック

			if ( !customUser.getCompanyId().equals(knowledge.getCompanyId()) ) // 不正リクエスト
			{
				throw new RestException( new RestResult(
					ResponseCode.AUTH_INSUFFICIENT, null, name, value ) ) ;
			}
		}

		// ********** 個別項目の共通チェック
		validateKnowledge(knowledge, false);

		// ----- タイトル重複チェック
		{
			// タイトルが変更されている場合のみタイトル重複チェックを行う
			if(!knowledge.getTitle().equals(entity.getTitle())) {
				name = "#knowledge.title" ;
				// タイトルの重複チェック
				Knowledge example = new Knowledge();
				example.setCompanyId(customUser.getCompanyId());

				// 変換後の内容でチェックするのでvalueではなくgetTitleで取得する
				example.setTitle(knowledge.getTitle());

				List<Knowledge> knowledgeList = knowledgeDao.findByExample(example);
				if ( knowledgeList != null && !knowledgeList.isEmpty()) {
					throw new RestException( new RestResult(
						ResponseCode.KNOWLEDGE_DUPLICATE_TITLE, null, name ) );
				}
			}
		}


		// ----- 参照回数は更新しないためチェックしない

		// ----- 作成日時

		{
			name = "#knowledge.createDate" ;
			Date value = knowledge.getCreateDate() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, null, null );
		}

		// ----- 作成ユーザ ID

		{
			name = "#knowledge.createUserId" ;
			String value = knowledge.getCreateUserId() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 32 );
		}

		// ----- 作成ユーザ名

		{
			name = "#knowledge.createUserName" ;
			String value = knowledge.getCreateUserName() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 100 );
		}

		// ----- 更新ユーザ ID

		{
			name = "#knowledge.updateUserId" ;
			String value = knowledge.getUpdateUserId() ;

			// 常にログインユーザ ID を設定
			value = customUser.getUserId() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 32 );

			// 補完内容を反映
			knowledge.setUpdateUserId( value );
		}

		// ----- 更新ユーザ名

		{
			name = "#knowledge.updateUserName" ;
			String value = knowledge.getUpdateUserName() ;

			value = customUser.getUserName();

			RestValidatorUtils.fieldValidate( name, value, true, null, 100 );

			// 補完内容を反映
			knowledge.setUpdateUserName( value );
		}

		// タグのチェック
		validateTag(editForm.getTag());

		// 参照先のチェック
		validateReference(editForm.getReference());

		// マニュアルのチェック
		validateManual(editForm.getManual());
	}

	/**
	 * 参照回数更新の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForIncrement( KnowledgeRequest req ) {

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

		name = "#knowledge";

		Knowledge knowledge = editForm.getKnowledge();

		if ( knowledge == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		// ----- PK
		{
			name = "#knowledge.knowledgeId" ;
			Long value = knowledge.getKnowledgeId() ;


			RestValidatorUtils.fieldValidate( name, value, true, null, null );
			RestValidatorUtils.fieldValidate( name, String.valueOf( value ), false, null, 19 ) ;

			// 存在チェック

			Knowledge entity = knowledgeDao.get( value ) ;

			if ( entity == null ) // 存在しない
			{
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, name, value ) ) ;
			}

			// 企業IDのチェック
			// ユーザ情報の取得
			CustomUser customUser = loginUtility.getCustomUser();

			if ( !customUser.getCompanyId().equals(entity.getCompanyId()) ) // 存在しない
			{
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, name, value ) ) ;
			}

		}
	}

	// -------------------------------------------------------------------------

	/**
	 * 削除の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForDelete( KnowledgeRequest req ) {

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

		name = "#knowledge";

		KnowledgeSearchResult knowledge = req.getKnowledge();

		if ( knowledge == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		// ----- PK

		name = "#knowledge.knowledgeId";
		Long pkvalue = knowledge.getKnowledgeId();

		RestValidatorUtils.fieldValidate( name, pkvalue, true, null, null );

		// 存在チェック

		Knowledge entity = knowledgeDao.get( pkvalue );

		if ( entity == null ) {

			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name ) );
		}

		// 企業IDのチェック
		// ユーザ情報の取得
		CustomUser customUser = loginUtility.getCustomUser();

		if ( !customUser.getCompanyId().equals(entity.getCompanyId()) ) // 存在しない
		{
			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name, pkvalue ) ) ;
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * CSVインポートの入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 * @throws SQLException
	 */
	public void validateForImport( KnowledgeRequest req, Set<String> titles ) throws SQLException {

		// 登録用バリデータで入力内容のチェック
		this.validateForPut( req, false );

		// CSVファイル内のタイトル重複チェック
		String title = req.getEditForm().getKnowledge().getTitle();
		if (titles.contains(title)) {
			throw new RestException( new RestResult(ResponseCode.KNOWLEDGE_DUPLICATE_TITLE, null, "#knowledge.title" ) );
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * ナレッジの個別項目のチェックを行う
	 * @param knowledge ナレッジ情報
	 */
	private void validateKnowledge(Knowledge knowledge, boolean checkDuplicate) {
		String name = "";

		// ユーザ情報からの設定
		CustomUser customUser = loginUtility.getCustomUser();

		// ----- タイトル

		{
			name = "#knowledge.title" ;
			String value = knowledge.getTitle() ;

			// トリムする
			value = ValidatorUtils.trim(value);

			RestValidatorUtils.fieldValidate( name, value, true, null, 100 );

			// 半角カナを全角カナに変換する
			knowledge.setTitle(HankakuKanaConverter.convert(value)) ;

			if (checkDuplicate) {
				// タイトルの重複チェック
				Knowledge example = new Knowledge();
				example.setCompanyId(customUser.getCompanyId());

				// 変換後の内容でチェックするのでvalueではなくgetTitleで取得する
				example.setTitle(knowledge.getTitle());

				List<Knowledge> knowledgeList = knowledgeDao.findByExample(example);
				if ( knowledgeList != null && !knowledgeList.isEmpty()) {
					throw new RestException( new RestResult(
						ResponseCode.KNOWLEDGE_DUPLICATE_TITLE, null, name ) );
				}
			}
		}

		// ----- 内容

		{
			name = "#knowledge.content" ;
			String value = knowledge.getContent() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, -1 );

			// 半角カナを全角カナに変換する
			knowledge.setContent(HankakuKanaConverter.convert(value)) ;
		}

		// ----- スクリプト

		{
			name = "#knowledge.script" ;
			String value = knowledge.getScript() ;

			RestValidatorUtils.fieldValidate( name, value, false, null, -1 );

			// 半角カナを全角カナに変換する
			knowledge.setScript(HankakuKanaConverter.convert(value)) ;
		}

		// ----- 参照回数

		{
			name = "#knowledge.clickCount" ;
			Long value = knowledge.getClickCount() ;

			RestValidatorUtils.fieldValidate( name, value, true, 0L, null );

		}
	}

	private void validateTag(List<String> tags) {
		// タグのチェック
		String name = "#tag.tagName" ;
		if ( tags == null || tags.isEmpty()) {
			throw new RestException( new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		for(int i = 0; i < tags.size(); i++) {
			String value = tags.get(i);

			// トリムする
			value = ValidatorUtils.trim(value);

			RestValidatorUtils.fieldValidate( name, value, true, null, 100 );

			// 半角カナを全角カナに変換する
			tags.set(i, HankakuKanaConverter.convert(value)) ;

			// 大文字小文字を区別なくタグの重複を削除する
			List<String> distinctTags = tags.stream()
					.filter(new ConcurrentSkipListSet <> (String.CASE_INSENSITIVE_ORDER)::add)
					.distinct()
					.collect(Collectors.toList());
			tags.clear();
			tags.addAll(distinctTags);
		}
	}

	private void validateReference(List<String> references) {

		// 参照先のチェック
		String name = "#reference.url" ;

		if ( references.size() > maxReferenceCount ) {
			throw new RestException( new RestResult(ResponseCode.INPUT_ERROR_LIST_SIZE, null, name ) ) ;
		}

		for(int i = 0; i < references.size(); i++) {

			String value = references.get(i);

			// トリムする
			value = ValidatorUtils.trim(value);

			// 文字数チェック(URLはバイト数でチェックする)
			if (!ValidatorUtils.bytelength(value, -1, 255)) {
				throw new RestException( new RestResult(ResponseCode.INPUT_ERROR_LENGTH, null, name ) ) ;
			}

			// 文字種チェック(半角英数記号のみ)
			if (!ValidatorUtils.ascii(value)) {
				throw new RestException( new RestResult(ResponseCode.INPUT_ERROR_LETTER_TYPE, null, name ) ) ;
			}

			// URL形式チェック(https://、http://で始まるかチェックする)
			if (!ValidatorUtils.url(value)) {
				throw new RestException( new RestResult(ResponseCode.INPUT_ERROR_FORMAT, null, name ) ) ;
			}
		}
	}

	private void validateManual(List<Manual> manuals) {

		// マニュアルのチェック
		String name = "#manual.manual";
		String manualName = "#manual.manualName" ;
		String manualpage= "#manual.manualPage" ;
		String manualurl = "#manual.manualUrl" ;

		if ( manuals.size() > maxManualCount ) {
			throw new RestException( new RestResult(ResponseCode.INPUT_ERROR_LIST_SIZE, null, name ) ) ;
		}

		for(int i = 0; i < manuals.size(); i++) {

			// マニュアル名のチェック
			String value = ValidatorUtils.trim(manuals.get(i).getManualName());

			RestValidatorUtils.fieldValidate( manualName, value, false, null, 100 );

			// 半角カナを全角カナに変換する
			manuals.get(i).setManualName(HankakuKanaConverter.convert(value)) ;


			// マニュアルページのチェック
			value = ValidatorUtils.trim(manuals.get(i).getManualPage()) ;

			RestValidatorUtils.fieldValidate( manualpage, value, false, null, 10 );

			// 半角カナを全角カナに変換する
			manuals.get(i).setManualPage(HankakuKanaConverter.convert(value)) ;

			value = ValidatorUtils.trim(manuals.get(i).getManualUrl()) ;

			// 文字数チェック(URLはバイト数でチェックする)
			if (!ValidatorUtils.bytelength(value, -1, 255)) {
				throw new RestException( new RestResult(ResponseCode.INPUT_ERROR_LENGTH, null, manualurl ) ) ;
			}

			// 文字種チェック(半角英数記号のみ)
			if (!ValidatorUtils.ascii(value)) {
				throw new RestException( new RestResult(ResponseCode.INPUT_ERROR_LETTER_TYPE, null, manualurl ) ) ;
			}

			// URL形式チェック(https://、http://で始まるかチェックする)
			if (!ValidatorUtils.url(value)) {
				throw new RestException( new RestResult(ResponseCode.INPUT_ERROR_FORMAT, null, manualurl ) ) ;
			}
		}
	}
}
