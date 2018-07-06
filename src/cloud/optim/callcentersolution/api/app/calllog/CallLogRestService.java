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
package cloud.optim.callcentersolution.api.app.calllog;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cloud.optim.callcentersolution.api.app.calllog.CallLogResponse.BulkResult;
import cloud.optim.callcentersolution.api.app.calllog.CallLogResponse.EditResult;
import cloud.optim.callcentersolution.api.app.calllogdetail.CallLogDetailService;
import cloud.optim.callcentersolution.api.app.calluser.CallUserService;
import cloud.optim.callcentersolution.api.entity.CallLog;
import cloud.optim.callcentersolution.api.entity.CallLogDetail;
import cloud.optim.callcentersolution.api.util.AuthUtil;
import cloud.optim.callcentersolution.core.modules.ffmpeg.FFmpeg;
import cloud.optim.callcentersolution.core.modules.loginutil.CustomUser;
import cloud.optim.callcentersolution.core.modules.loginutil.LoginUtility;
import cloud.optim.callcentersolution.core.modules.rest.CustomExceptionMapper.RestResponse;
import cloud.optim.callcentersolution.core.modules.rest.ExceptionUtil;
import cloud.optim.callcentersolution.core.modules.rest.MessageUtility;
import cloud.optim.callcentersolution.core.modules.rest.ResponseCode;
import cloud.optim.callcentersolution.core.modules.rest.RestException;
import cloud.optim.callcentersolution.core.modules.rest.RestLog;
import cloud.optim.callcentersolution.core.modules.rest.RestResult;
import cloud.optim.callcentersolution.core.modules.rest.app.fileupload.FileUploadUtility;

/**
 * CallLogRestService 実装.<br/>
 */
@Path( "/callLog" )
@Consumes( { "application/json", "application/xml" } )
@Produces( { "application/json", "application/xml" } )
@Component
public class CallLogRestService
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() );

	/** PK の項目名 */
	private static final String NAME_PK = "#callLog.callLogId";

	/** LoginUtility */
	@Resource private LoginUtility loginUtility ;

	// -------------------------------------------------------------------------

	/** AuthUtil */
	@Resource private AuthUtil authUtil;

	/** バリデータ */
	@Resource private CallLogRestValidator validator;

	/** CallLogService */
	@Resource private CallLogService callLogService;

	/** CallLogDetailService */
	@Resource private CallLogDetailService callLogDetailService;

	/** CallUserService */
	@Resource private CallUserService callUserService;

	/** RestLog */
	@Resource private RestLog restlog;

	/** MessageUtility */
	@Resource private MessageUtility messageUtility;

	/** MessageUtility */
	@Resource private FileUploadUtility fileUploadUtility;

	/** FFmpeg */
	@Resource private FFmpeg ffmpeg;

	/** FileIdHolder */
	@Resource private FileIdHolder fileIdHolder ;

	/** ExecutorService */
	@Resource private ExecutorService executorService ;

	// -------------------------------------------------------------------------

	/** 一時ファイル保存ディレクトリ */
	@Value( "${call.tmp.file.directory}" )
	private String tmpFileDirectory;

	/** 音声ファイル保存ルートディレクトリ */
	@Value( "${call.voice.file.root.directory}" )
	private String voiceFileRootDirectory;

	/** 分割音声ファイル名 */
	@Value( "${call.voice.file.name}" )
	private String voiceFileName;

	/** 圧縮音声ファイル名 */
	@Value( "${encode.output.file.name}" )
	private String encodedFileName;

	/** ダウンロードエラーページURL */
	@Value( "${download.error.page.url}" )
	private String downloadErrorPageUrl;

	/** Content-Disposition レスポンスヘッダー */
	@Value( "${call.log.download.header.content.disposition}" )
	private String downloadHeader;

	/** ダウンロードファイル拡張子 */
	@Value( "${call.log.download.file.ext}" )
	private String downloadfileExt;

	// -------------------------------------------------------------------------

	/**
	 * 検索
	 *
	 * @param req 検索条件
	 *
	 * @return 検索結果
	 */
	@POST
	@Path( "/search" )
	public CallLogResponse search( CallLogRequest req ) {

		String MNAME = "search";
		restlog.start( log, MNAME, req );

		try {

			CallLogResponse res = new CallLogResponse();

			// ----- 入力チェック

			// 検索条件の指定がない場合は全検索として扱う

			if ( req == null ) req = new CallLogRequest();

			validator.validateForSearch( req );

			// ----- 検索

			SearchForm form = req.getSearchForm();

			List<SearchResult> list = callLogService.search( form );

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
	 * 検索2 企業単位での検索
	 *
	 * @param req 検索条件
	 *
	 * @return 検索結果
	 */
	@POST
	@Path( "/searchByName" )
	public CallLogResponse searchByName( CallLogRequest req ) {

		String MNAME = "searchByName";
		restlog.start( log, MNAME, req );

		try {

			CallLogResponse res = new CallLogResponse();

			// ----- 入力チェック

			// 検索条件の指定がない場合は全検索として扱う

			if ( req == null ) req = new CallLogRequest();

			validator.validateForSearchByName( req );

			// ----- 検索

			SearchForm form = req.getSearchForm();

			List<SearchResult> list = callLogService.searchByName( form );

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
	 * 取得
	 *
	 * @param req 取得条件（PK 項目のみ使用）
	 *
	 * @return 取得エンティティ
	 */
	@POST
	@Path( "/get" )
	public CallLogResponse get( CallLogRequest req ) {

		String MNAME = "get";
		restlog.start( log, MNAME, req );

		try {

			CallLogResponse res = new CallLogResponse();

			// ----- 入力チェック

			validator.validateForGet( req );

			// ----- 入力データ取得

			CallLog callLog = req.getEditForm().getCallLog();

			// ----- 取得
			// ユーザ情報取得
			CustomUser customUser = loginUtility.getCustomUser();

			// 通話ログ取得
			CallLog entity = null;
			// 企業管理者権限を保持している場合には、ユーザ企業ID配下の全ユーザの通話ログを取得可能
			if (this.authUtil.isAdmin()) {
				entity = callLogService.getCallLogCompanyAllUser( callLog.getCallLogId(), customUser.getCompanyId() );
			} else {
				entity = callLogService.getCallLog( callLog.getCallLogId(), customUser.getCompanyId(), customUser.getUserId() );
			}

			if ( entity == null ) {

				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, NAME_PK, callLog.getCallLogId() ) );
			}

			// 通話ログ詳細取得
			List<CallLogDetail> details = callLogDetailService.getDetails(customUser.getCompanyId(), entity.getCallLogId());

			// 通話時間合計取得
			Long callTime = callLogService.sumCallTime(entity.getCallLogId());


			// ----- レスポンス作成

			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setEditResult( new EditResult() );
			res.getEditResult().setCallLog( entity );
			res.getEditResult().setCallLogDetails( details );
			res.getEditResult().setCallTimeSum( callTime );

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
	 * 問い合わせ番号による取得
	 *
	 * @param req 取得条件（PK 項目のみ使用）
	 *
	 * @return 取得エンティティ
	 */
	@POST
	@Path( "/getByNo" )
	public CallLogResponse getByNo( CallLogRequest req ) {

		String MNAME = "getByNo";
		restlog.start( log, MNAME, req );

		try {

			CallLogResponse res = new CallLogResponse();

			// ----- 入力チェック

			validator.validateForGetByNo( req );

			// ----- 入力データ取得

			CallLog callLog = req.getEditForm().getCallLog();

			// ----- 取得
			// ユーザ情報取得
			CustomUser customUser = loginUtility.getCustomUser();

			CallLog entity = callLogService.getByNo( callLog.getCallLogNo(), customUser.getCompanyId() );

			if ( entity == null ) {
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, NAME_PK, callLog.getCallLogId() ) );
			}

			// 論理削除されている
			if ( entity.getDeleteDate() != null ) { 
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, NAME_PK, callLog.getCallLogId() ) );
			}

			// 通話ログ詳細取得
			List<CallLogDetail> details = callLogDetailService.getDetails(customUser.getCompanyId(), entity.getCallLogId());

			String logs = details.stream().map(each -> each.getLog()).collect(Collectors.joining("\r\n\r\n"));

			// ----- レスポンス作成

			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setEditResult( new EditResult() );
			res.getEditResult().setCallLog( entity );
			res.getEditResult().setLogs( logs );

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
	 * ダウンロード用音声ファイル生成
	 *
	 * @param req 取得条件（PK 項目のみ使用）
	 *
	 * @return ファイルID
	 */
	@POST
	@Path( "/generateVoice" )
	public CallLogResponse generateVoice( CallLogRequest req ) {

		String MNAME = "generateVoice";
		restlog.start( log, MNAME, req );

		try {
			// ----- 入力チェック

			validator.validateForGenerateVoice( req );

			// ----- 入力データ取得

			CallLog callLog = req.getEditForm().getCallLog();

			// ----- 通話ログ取得

			// ユーザ情報取得
			CustomUser customUser = loginUtility.getCustomUser();

			// 通話ログ取得
			CallLog entity = callLogService.get( callLog.getCallLogId() );
			if ( entity == null ) {
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, NAME_PK, callLog.getCallLogId() ) );
			}

			// 論理削除されている
			if ( entity.getDeleteDate() != null ) { 
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, NAME_PK, callLog.getCallLogId() ) );
			}

			// 企業IDが異なる
			if ( !entity.getCompanyId().equals(customUser.getCompanyId()) ) {
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, NAME_PK, callLog.getCallLogId() ) );
			}

			// ----- 通話ログ詳細一覧取得
			// 音声の有る通話ログ詳細取得
			List<CallLogDetail> details = callLogDetailService.searchExistVoice(customUser.getCompanyId(), entity.getCallLogId());
			if (details == null || details.isEmpty()) {
				throw new RestException( new RestResult(
						ResponseCode.CALL_LOG_DWONLOAD_NO_VOICE, null, NAME_PK, entity.getCallLogId() ) );
			}

			// ファイル名リスト生成
			List<java.nio.file.Path> paths = new ArrayList<>();
			// 音声ファイル名の取得
			boolean partialError = false;
			String dirPath  = Paths.get(voiceFileRootDirectory, customUser.getCompanyId(), callLog.getCallLogId().toString()).toString();
			for (CallLogDetail each : details) {

				// 圧縮音声ファイルの有無チェック
				java.nio.file.Path filePath = Paths.get(dirPath, String.format(encodedFileName, each.getCallLogDetailId()));
				if(filePath.toFile().exists()) {
					paths.add(filePath);
					continue;
				}

				// 圧縮音声が無い場合はwav形式のファイルのパスを設定する
				filePath = Paths.get(dirPath, String.format(voiceFileName, each.getCallLogDetailId()));
				if(filePath.toFile().exists()) {
					paths.add(filePath);
					continue;
				}

				// 一部エラー有にする
				partialError = true;
			}

			// ファイルIDの生成
			String fileId = fileUploadUtility.getNextUploadId("voice");

			// セッションにファイルIDを設定する
			fileIdHolder.setFileId(fileId, entity.getCallLogNo());

			// 出力ファイル名
			java.nio.file.Path outputFile = Paths.get(tmpFileDirectory, fileId);

			// 音声ファイルのマージをする
			// 設定ファイルで定義した多重度以上実行されない
			Future<Exception> future = executorService.submit(() -> {
				try {
					ffmpeg.marge(paths, outputFile);
				} catch ( Exception e ) {
					return e;
				}
				return null;
			});

			// 処理終了まで待機する
			Exception exception = future.get();
			if (exception != null) {
				throw new RestException(ResponseCode.CALL_LOG_DWONLOAD_FAIL_MERGE, exception);
			}

			// ----- レスポンス作成
			CallLogResponse res = new CallLogResponse();
			if (partialError) {
				res.setResult( new RestResult( ResponseCode.PARTIAL ) );
			} else {
				res.setResult( new RestResult( ResponseCode.OK ) );
			}
			res.setFileId(fileId);

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
	 * 音声データダウンロード
	 *
	 * @param req Httpリクエスト
	 * @param res Httpレスポンス
	 * @param fileId ファイルID
	 *
	 * @return レスポンス
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path( "/voice/{fileId}" )
	public Response voice(@Context HttpServletRequest req, @Context HttpServletResponse res, @PathParam( "fileId" ) String fileId ) throws IOException {

		String MNAME = "voice";
		restlog.start( log, MNAME, fileId );

		try {

			// ----- 入力チェック

			// セッションにファイルIDがあるかチェックする
			Long callLogNo = fileIdHolder.isExistFileId(fileId);
			if (callLogNo == null) {
				throw new RestException( new RestResult(ResponseCode.CALL_LOG_DWONLOAD_INVALID_FILE_ID) );
			}

			// ----- レスポンス作成
			StreamingOutput output = new StreamingOutput() {
				@Override
				public void write(OutputStream output) throws IOException, WebApplicationException {
					File tmpFile = Paths.get(tmpFileDirectory, fileId).toFile();
					try (InputStream audioFile = Files.newInputStream(tmpFile.toPath())){
						byte[] buf = new byte[16384];
						int size;
						while ((size = audioFile.read(buf)) != -1) {
							output.write(buf, 0, size);
						}
						output.flush();
					};

					// 生成した一時ファイルを削除する
					tmpFile.delete();
				}
			};

			return Response
					.ok()
					.entity(output)
					.header("Content-Disposition", String.format(downloadHeader, callLogNo, downloadfileExt))
					.build();
		}
		catch ( Exception ex ) {
			RestException restException = ExceptionUtil.handleException( log, ResponseCode.SYS_ERROR, null, null, null, ex );

			RestResponse restResponse = new RestResponse() ;
			restResponse.setResultList( restException.getRestResultList() ) ;
			messageUtility.fillMessage( restResponse.getResultList() ) ;
			restlog.abort( restException.getLogger() != null ? restException.getLogger() : log, restResponse, restResponse.getResultList(), restException ) ;

			// ダウンロードエラーページへリダイレクト
			res.sendRedirect(req.getContextPath() + downloadErrorPageUrl);
			return null;
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * 一括削除
	 *
	 * @param req 取得条件（PK 項目のみ使用）
	 *
	 * @return 処理結果ステータスのみ
	 */
	@POST
	@Path( "/delete" )
	public CallLogResponse delete( CallLogRequest req ) {

		String MNAME = "delete";
		restlog.start( log, MNAME, req );

		try {

			CallLogResponse res = new CallLogResponse();
			res.setBulkResultList( new ArrayList<BulkResult>() );

			// ----- 入力チェック

			validator.validateForDelete( req );

			// ----- 入力データ取得

			boolean error = false;

			// ----- 1 件ずつ削除

			for ( SearchResult form : req.getBulkFormList() ) {

				BulkResult result = new BulkResult();

				try {

					deleteOne( form );

					// ----- レスポンス作成

					result.setResult( new RestResult( ResponseCode.OK ) );
					result.setCallLog( form.getCallLog() );

					messageUtility.fillMessage( result.getResultList() );
					restlog.endOne( log, MNAME, result, result.getResultList() );
				}
				catch ( Exception ex ) {

					error = true;

					result.setResultList( // 応答結果を作成
						ExceptionUtil.handleException( log, ResponseCode.SYS_ERROR, null, null, null, ex )
						.getRestResultList() );
					result.setCallLog( form.getCallLog() );

					messageUtility.fillMessage( result.getResultList() );
					restlog.abortOne( log, MNAME, result, result.getResultList(), ex );
				}

				res.getBulkResultList().add( result );
			}

			// ----- レスポンス作成

			if ( error ) res.setResult( new RestResult( ResponseCode.PARTIAL ) );
			else res.setResult( new RestResult( ResponseCode.OK ) );

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
	 * 1 件削除
	 *
	 * @param form 削除する 1 コンテンツの情報
	 *
	 * @throws Exception エラー発生時
	 */
	private void deleteOne( SearchResult form ) throws Exception {

		// ----- 入力チェック

		CallLog entity = validator.validateForDeleteOne( form );


		// ----- 論理削除

		// ユーザ情報取得
		CustomUser customUser = loginUtility.getCustomUser();

		// 論理削除日時取得
		Date deleteDate = new Date();

		// 通話ログ 1件 論理削除
		entity.setDeleteDate(deleteDate);
		callLogService.update( entity );

		// 通話ログに紐づく通話ログ詳細 全件  論理削除
		callLogDetailService.logicalDeleteAllCallLogDetails( form.getCallLog().getCallLogId(),
			customUser.getCompanyId(), customUser.getUserId(), customUser.getUserName(), deleteDate);

		// 通話ログに紐づく通話者ログ 全件  論理削除
		callUserService.logicalDeleteAllCallUsers( form.getCallLog().getCallLogId(),
			customUser.getCompanyId(), customUser.getUserId(), customUser.getUserName(), deleteDate);

	}

}