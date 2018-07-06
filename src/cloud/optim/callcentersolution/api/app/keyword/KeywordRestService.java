/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：KnowledgeRestService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.keyword;


import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import cloud.optim.callcentersolution.core.modules.loginutil.LoginUtility;
import cloud.optim.callcentersolution.core.modules.rest.ExceptionUtil;
import cloud.optim.callcentersolution.core.modules.rest.MessageUtility;
import cloud.optim.callcentersolution.core.modules.rest.ResponseCode;
import cloud.optim.callcentersolution.core.modules.rest.RestLog;
import cloud.optim.callcentersolution.core.modules.rest.RestResult;

/**
 * KnowledgeRestService 実装.<br/>
 */
@Path( "/keyword" )
@Consumes( { "application/json", "application/xml" } )
@Produces( { "application/json", "application/xml" } )
@Component
public class KeywordRestService
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() );

	// -------------------------------------------------------------------------

	/** バリデータ */
	@Resource private KeywordRestValidator validator;

	/** KeywordService */
	@Resource private KeywordService keywordService;

	/** RestLog */
	@Resource private RestLog restlog;

	/** LoginUtility */
	@Resource private LoginUtility loginUtility ;

	/** MessageUtility */
	@Resource private MessageUtility messageUtility;

	// -------------------------------------------------------------------------

	/**
	 * キーワード抽出
	 *
	 * @param req 抽出対象テキスト
	 *
	 * @return 抽出結果
	 */
	@POST
	@Path( "/extract" )
	public KeywordResponse extract( KeywordRequest req ) {

		String MNAME = "extract";
		restlog.start( log, MNAME, req );

		try {
			KeywordResponse res = new KeywordResponse();

			// 入力チェック
			validator.validateForExtract(req);

			// ----- 抽出
			List<SearchResult> list = keywordService.extract(req.getSearchForm().getText());

			// ----- レスポンス作成

			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setSearchResultList( list );

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	/**
	 * ナレッジキーワード抽出
	 *
	 * @param req 抽出対象テキスト
	 *
	 * @return 抽出結果
	 */
	@POST
	@Path( "/knowledgeKeyword" )
	public KeywordResponse knowledgeKeyword( KeywordRequest req ) {

		String MNAME = "knowledgeKeyword";
		restlog.start( log, MNAME, req );

		try {
			KeywordResponse res = new KeywordResponse();

			// 入力チェック
			validator.validateForKnowledgeKeyword(req);

			// ----- 抽出
			List<SearchResult> list = keywordService.getKnowledgeKeywords(
					loginUtility.getCustomUser().getCompanyId(),
					req.getSearchForm().getText(),
					req.getSearchForm().getExtractCount());

			// ----- レスポンス作成

			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setSearchResultList( list );

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}


	// -------------------------------------------------------------------------

	/**
	 * キーワード取得
	 *
	 * @param req 抽出対象テキスト
	 *
	 * @return 抽出結果
	 */
	@POST
	@Path( "/get" )
	public KeywordResponse get( KeywordRequest req ) {

		String MNAME = "get";
		restlog.start( log, MNAME, req );

		try {
			KeywordResponse res = new KeywordResponse();

			// 入力チェック
			validator.validateForGet(req);

			// ----- 抽出
			SearchResult result = keywordService.getKeyword(
					loginUtility.getCustomUser().getCompanyId(),
					req.getSearchForm().getText());

			// ----- レスポンス作成

			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setSearchResult( result );

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}
}