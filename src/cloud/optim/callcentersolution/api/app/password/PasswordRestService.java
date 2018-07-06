/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CallLogRestService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.password;


import java.util.ArrayList;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cloud.optim.callcentersolution.api.app.password.PasswordResponse.EditResult;
import cloud.optim.callcentersolution.api.util.AuthUtil;
import cloud.optim.callcentersolution.core.modules.loginutil.CustomUser;
import cloud.optim.callcentersolution.core.modules.loginutil.LoginUtility;
import cloud.optim.callcentersolution.core.modules.rest.ExceptionUtil;
import cloud.optim.callcentersolution.core.modules.rest.MessageUtility;
import cloud.optim.callcentersolution.core.modules.rest.ResponseCode;
import cloud.optim.callcentersolution.core.modules.rest.RestLog;
import cloud.optim.callcentersolution.core.modules.rest.RestResult;
import cloud.optim.callcentersolution.core.modules.xauth.OptimalBizService;
import cloud.optim.callcentersolution.core.modules.xauth.OptimalBizUserInfo;

/**
 * PasswordRestService 実装.<br/>
 */
@Path( "/password" )
@Consumes( { "application/json", "application/xml" } )
@Produces( { "application/json", "application/xml" } )
@Component
public class PasswordRestService
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() );

	/** LoginUtility */
	@Resource private LoginUtility loginUtility ;

	// -------------------------------------------------------------------------

	/** AuthUtil */
	@Resource private AuthUtil authUtil;

	/** バリデータ */
//	@Resource private PasswordRestValidator validator;

	/** RestLog */
	@Resource private RestLog restlog;

	/** MessageUtility */
	@Resource private MessageUtility messageUtility;

	// -------------------------------------------------------------------------

	/** OptimalBiz URL */
	@Value( "${optimalbiz.url}" )
	private String optimalBizUrl;

	/** OptimalBiz 認証コンシューマキー */
	@Value( "${optimalbiz.oAuthConsumer.Token}" )
	private String oAuthConsumerToken;

	/** OptimalBiz 認証コンシューマシークレット */
	@Value( "${optimalbiz.oAuthConsumer.Secret}" )
	private String oAuthConsumerSecret;

	/** OptimalBiz 認証API */
	@Value( "${optimalbiz.accessToken.api}" )
	private String tokenApi;

	/** OptimalBiz 企業情報取得API */
	@Value( "${optimalbiz.companyInfo.api}" )
	private String companyInfoApi;

	/** OptimalBiz ユーザ一覧情報取得API */
	@Value( "${optimalbiz.userListInfo.api}" )
	private String userListInfoApi;

	/** OptimalBiz ユーザパスワード更新API */
	@Value( "${optimalbiz.updateUserPassword.api}" )
	private String updateUserPasswordApi;

	/** OptimalBiz ユーザパスワード更新用データフォーマット */
	@Value( "${optimalbiz.updateUserPassword.xmlStrFormat}" )
	private String updatePasswordFormat;

	/** ページサイズ */
	@Value( "${password.pageSize}" )
	private Long pageSize;

//-------------------------------------------------------------------------

	/**
	 * 認証
	 *
	 * @param req 認証条件
	 *
	 * @return 認証結果
	 */
	@POST
	@Path( "/xauth" )
	public PasswordResponse xauth( PasswordRequest req ) {

		String MNAME = "xauth";
		restlog.start( log, MNAME, req );

		try {

			PasswordResponse res = new PasswordResponse();

			// ----- 入力チェック

			//validator.validateForGet( req );

			// ----- 入力データ取得
//			String xauthUserId = req.getInputForm().getUserId();
//			String xauthPassword = req.getInputForm().getPassword();

			// ----- 企業ID取得
			CustomUser customUser = loginUtility.getCustomUser();
//			String xauthCompanyId = customUser.getCompanyId();

			String xauthCompanyId = "optim-develop";
			String xauthUserId = "mitsuko.kumakura";
			String xauthPassword = "kumakura";

			// ----- 認証＆トークン取得
			String url = optimalBizUrl  + String.format(tokenApi, xauthCompanyId, xauthUserId, xauthPassword);
	        String method = "POST";

	        OptimalBizService xauthToken = new OptimalBizService(url, method, oAuthConsumerToken, oAuthConsumerSecret, null, null);

	        int status = xauthToken.getOptimalBizTokenInfo();

			String oauthToken = "";
			String oauthTokenSecret = "";

	        if (status == HttpStatus.SC_OK) {
				oauthToken = xauthToken.getOAuthToken();
				oauthTokenSecret = xauthToken.getOAuthTokenSecret();

				// ユーザ情報にトークン情報を設定
				customUser.setOptimalBizToken(oauthToken);
				customUser.setOptimalBizTokenSecret(oauthTokenSecret);

				// ----- 企業Guid取得
			    String companyUrl  = String.format("%s%s", optimalBizUrl, companyInfoApi);
				String companyMethod = "GET";

				// ----- トークン情報取得

			    OptimalBizService companyInfo = new OptimalBizService(companyUrl, companyMethod, oAuthConsumerToken, oAuthConsumerSecret, oauthToken, oauthTokenSecret);

				status = companyInfo.getOptimalBizCompanyInfo();

				String company_guid = "";
				if (status == HttpStatus.SC_OK) {
					company_guid = companyInfo.getOptimalBizCompanyGuid();
					customUser.setCompanyGuid(company_guid);

					// ----- レスポンス作成
					res.setResult( new RestResult( ResponseCode.OK ) );
				} else {
					// ----- レスポンス作成??????????????????????????
					res.setResult( new RestResult( ResponseCode.AUTH_ERROR ) );
				}
	        } else {
				// ----- レスポンス作成
				res.setResult( new RestResult( ResponseCode.AUTH_ERROR ) );
	        }

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
	 * 検索 ページ番号での検索
	 *
	 * @param req 検索条件
	 *
	 * @return 検索結果
	 */
	@POST
	@Path( "/search" )
	public PasswordResponse search( PasswordRequest req ) {

		String MNAME = "search";
		restlog.start( log, MNAME, req );

		try {

			PasswordResponse res = new PasswordResponse();

			// ----- 入力チェック

			//validator.validateForGet( req );

			// ----- 入力データ取得
			Long pageNo = req.getSearchForm().getSortForm().getPageNo();

			// ----- 取得
			// ユーザ情報取得
			CustomUser customUser = loginUtility.getCustomUser();

			String company_guid = customUser.getCompanyGuid();
			String oauthToken = customUser.getOptimalBizToken();
			String oauthTokenSecret = customUser.getOptimalBizTokenSecret();

			//???仮実装
			company_guid = "90e7e1b84368e3c96f8dd8b5060727060357f9ab2b57d230ffe3140c44cc2ec6";
			pageNo = 1L;

			String url = optimalBizUrl  + String.format(userListInfoApi, company_guid, pageSize, pageNo);
	        String method = "GET";

	        OptimalBizService userList = new OptimalBizService(url, method, oAuthConsumerToken, oAuthConsumerSecret, oauthToken, oauthTokenSecret);

	        int status = userList.getOptimalUserListInfo();

			if (status == HttpStatus.SC_OK) {

				String pagesize = userList.getPageSize();
				String pageno = userList.getPageNo();
				String total = userList.getTotal();

				String totalpage = "";
				String offset = "";

				PageInfo pageinfo = new PageInfo();
				pageinfo.setPageNo(pageno);
				pageinfo.setPageSize(pagesize);
				pageinfo.setTotalNumber(total);
				pageinfo.setTotalPage(totalpage);
				pageinfo.setOffset(offset);

				ArrayList<OptimalBizUserInfo> userListInfo =  userList.getUserList();

				// ----- レスポンス作成

				res.setResult( new RestResult( ResponseCode.OK ) );
				res.setEditResult(new EditResult());
				res.getEditResult().setUserListInfo( userListInfo );
				res.getEditResult().setPageInfo( pageinfo );

			} else {
				// ----- レスポンス作成??????????????????????????
				res.setResult( new RestResult( ResponseCode.AUTH_ERROR ) );
			}

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
	 * ユーザーにのパスワード更新
	 *
	 * @param req 更新条件
	 *
	 * @return 更新結果
	 */
	@POST
	@Path( "/update" )
	public PasswordResponse searchByName( PasswordRequest req ) {

		String MNAME = "update";
		restlog.start( log, MNAME, req );

		try {

			PasswordResponse res = new PasswordResponse();

			// ----- 入力チェック

			//validator.validateForGet( req );

			// ----- 入力データ取得
			String userGuid = req.getEditForm().getUserGuid();
			String password = req.getEditForm().getPassword();
			String confirmPassword = req.getEditForm().getConfrimPassword();

			if (!password.equals(confirmPassword)) {
				//????????????????????
			}

			//???仮実装
			password = "kumakura";
			userGuid="94e4bbb5a42e456fa69c17aa69054ef2c12a29b03222ad647beed9eda8d61d0f";

			// ----- 取得
			// ユーザ情報取得
			CustomUser customUser = loginUtility.getCustomUser();

			String company_guid = customUser.getCompanyGuid();
			String oauthToken = customUser.getOptimalBizToken();
			String oauthTokenSecret = customUser.getOptimalBizTokenSecret();

			//???仮実装
			company_guid = "90e7e1b84368e3c96f8dd8b5060727060357f9ab2b57d230ffe3140c44cc2ec6";

			String url = optimalBizUrl  + String.format(updateUserPasswordApi, company_guid, userGuid);
	        String method = "PUT";
			String xmldata =  String.format(updatePasswordFormat, password);

	        OptimalBizService passupdate = new OptimalBizService(url, method, oAuthConsumerToken, oAuthConsumerSecret, oauthToken, oauthTokenSecret, xmldata);

	        int statusUpdatePass = passupdate.updateOptimalBizUserPassword();

			if (statusUpdatePass == HttpStatus.SC_NO_CONTENT) {

				// ----- レスポンス作成
				res.setResult( new RestResult( ResponseCode.OK ) );

			} else {
				// ----- レスポンス作成??????????????????????????
				res.setResult( new RestResult( ResponseCode.AUTH_ERROR ) );
			}

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