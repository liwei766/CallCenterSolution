/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：UseTimeRestService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.usetime;


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
 * UseTimeRestService 実装.<br/>
 */
@Path( "/usetime" )
@Consumes( { "application/json", "application/xml" } )
@Produces( { "application/json", "application/xml" } )
@Component
public class UseTimeRestService
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() );

	// -------------------------------------------------------------------------

	/** バリデータ */
	@Resource private UseTimeRestValidator validator;

	/** UseTimeService */
	@Resource private UseTimeService useTimeService;

	/** RestLog */
	@Resource private RestLog restlog;

	/** LoginUtility */
	@Resource private LoginUtility loginUtility ;

	/** MessageUtility */
	@Resource private MessageUtility messageUtility;

	// -------------------------------------------------------------------------

	/**
	 * 検索
	 *
	 * @param req 検索条件
	 *
	 * @return 検索結果
	 */
	@POST
	@Path( "/searchByMonth" )
	public UseTimeResponse searchByMonth( UseTimeRequest req ) {

		String MNAME = "searchByMonth";
		restlog.start( log, MNAME, req );

		try {

			UseTimeResponse res = new UseTimeResponse();

			// ----- 入力チェック

			// 検索条件の指定がない場合は全検索として扱う

			if ( req == null ) req = new UseTimeRequest();

			validator.validateForSearchByMonth(req);

			// ----- 検索

			SearchForm form = req.getSearchForm();
			form.getUseTime().setStartEnd();
		    form.getUseTime().setAgencyCompanyId(this.loginUtility.getCustomUser().getCompanyId());


			List<SearchResult> list = useTimeService.searchByMonth( form );

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


	@POST
	@Path( "/searchByCompanyId" )
	public UseTimeResponse searchByCompanyId( UseTimeRequest req ) {

		String MNAME = "searchByCompanyId";
		restlog.start( log, MNAME, req );

		try {

			UseTimeResponse res = new UseTimeResponse();

			// ----- 入力チェック

			// 検索条件の指定がない場合は全検索として扱う

			if ( req == null ) req = new UseTimeRequest();

			validator.validateForSearchByCompanyId(req);

			// ----- 検索

			SearchForm form = req.getSearchForm();
			form.getUseTime().setAgencyCompanyId(this.loginUtility.getCustomUser().getCompanyId());

			List<SearchResult> list = useTimeService.searchByCompanyId( form );

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
}