/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CallLogDetailRestService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.calllogdetail;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.annotation.Resource;
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

import cloud.optim.callcentersolution.api.app.calllogdetail.CallLogDetailRequest.EditForm;
import cloud.optim.callcentersolution.api.app.calllogdetail.CallLogDetailResponse.BulkResult;
import cloud.optim.callcentersolution.api.app.calllogdetail.CallLogDetailResponse.EditResult;
import cloud.optim.callcentersolution.api.entity.CallLogDetail;
import cloud.optim.callcentersolution.core.modules.loginutil.CustomUser;
import cloud.optim.callcentersolution.core.modules.loginutil.LoginUtility;
import cloud.optim.callcentersolution.core.modules.rest.CustomExceptionMapper.RestResponse;
import cloud.optim.callcentersolution.core.modules.rest.ExceptionUtil;
import cloud.optim.callcentersolution.core.modules.rest.MessageUtility;
import cloud.optim.callcentersolution.core.modules.rest.ResponseCode;
import cloud.optim.callcentersolution.core.modules.rest.RestException;
import cloud.optim.callcentersolution.core.modules.rest.RestLog;
import cloud.optim.callcentersolution.core.modules.rest.RestResult;

/**
 * CallLogDetailRestService 実装.<br/>
 */
@Path( "/calllogdetail" )
@Consumes( { "application/json", "application/xml" } )
@Produces( { "application/json", "application/xml" } )
@Component
public class CallLogDetailRestService
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() );

	/** PK の項目名 */
	@SuppressWarnings("unused")
	private static final String NAME_PK = "#callLogDetail.callLogDetailId";

	// -------------------------------------------------------------------------

	/** バリデータ */
	@Resource private CallLogDetailRestValidator validator;

	/** CallLogDetailService */
	@Resource private CallLogDetailService callLogDetailService;

	/** RestLog */
	@Resource private RestLog restlog;

	/** LoginUtility */
	@Resource private LoginUtility loginUtility;

	/** MessageUtility */
	@Resource private MessageUtility messageUtility;

	// -------------------------------------------------------------------------

	/** 音声ファイル保存ルートディレクトリ */
	@Value( "${call.voice.file.root.directory}" )
	private String voiceFileRootDirectory;

	/** 分割音声ファイル名 */
	@Value( "${call.voice.file.name}" )
	private String voiceFileName;

	/** 圧縮音声ファイル名 */
	@Value( "${encode.output.file.name}" )
	private String encodedFileName;

	// -------------------------------------------------------------------------

	/**
	 * 一括更新
	 *
	 * @param req 更新内容
	 *
	 * @return 処理結果と更新内容
	 */
	@POST
	@Path( "/update" )
	public CallLogDetailResponse update( CallLogDetailRequest req ) {

		String MNAME = "update";
		restlog.start( log, MNAME, req );

		try {

			CallLogDetailResponse res = new CallLogDetailResponse();
			res.setBulkResultList( new ArrayList<BulkResult>() );

			// ----- 入力チェック

			validator.validateForUpdate( req );

			// ----- 入力データ取得

			boolean error = false;

			// ----- 1 件ずつ更新

			for ( EditForm form : req.getBulkFormList() ) {

				BulkResult result = new BulkResult();

				try {

					updateOne( form );

					// ----- レスポンス作成

					result.setResult( new RestResult( ResponseCode.OK ) );
					EditResult editResult = new EditResult();
					editResult.setCallLogDetail(form.getCallLogDetail());
					result.setEditResult( editResult );

					messageUtility.fillMessage( result.getResultList() );
					restlog.endOne( log, MNAME, result, result.getResultList() );
				}
				catch ( Exception ex ) {

					error = true;

					result.setResultList( // 応答結果を作成
						ExceptionUtil.handleException( log, ResponseCode.SYS_ERROR, null, null, null, ex )
						.getRestResultList() );
					EditResult editResult = new EditResult();
					editResult.setCallLogDetail(form.getCallLogDetail());
					result.setEditResult( editResult );

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
	 * @return
	 *
	 * @throws Exception エラー発生時
	 */
	private void updateOne( EditForm form ) throws Exception {

		// ----- 入力チェック

		validator.validateForUpdateOne( form );

		// ----- 更新

		callLogDetailService.update( form.getCallLogDetail() );
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
	public CallLogDetailResponse delete( CallLogDetailRequest req ) {

		String MNAME = "delete";
		restlog.start( log, MNAME, req );

		try {

			CallLogDetailResponse res = new CallLogDetailResponse();
			res.setBulkResultList( new ArrayList<BulkResult>() );

			// ----- 入力チェック

			validator.validateForDelete( req );

			// ----- 入力データ取得

			boolean error = false;

			// ----- 1 件ずつ削除

			for ( EditForm form : req.getBulkFormList() ) {

				BulkResult result = new BulkResult();

				try {

					deleteOne( form );

					// ----- レスポンス作成

					result.setResult( new RestResult( ResponseCode.OK ) );
					EditResult editResult = new EditResult();
					editResult.setCallLogDetail(form.getCallLogDetail());
					result.setEditResult( editResult );

					messageUtility.fillMessage( result.getResultList() );
					restlog.endOne( log, MNAME, result, result.getResultList() );
				}
				catch ( Exception ex ) {

					error = true;

					result.setResultList( // 応答結果を作成
						ExceptionUtil.handleException( log, ResponseCode.SYS_ERROR, null, null, null, ex )
						.getRestResultList() );
					EditResult editResult = new EditResult();
					editResult.setCallLogDetail(form.getCallLogDetail());
					result.setEditResult( editResult );

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
	private void deleteOne( EditForm form ) throws Exception {

		// ----- 入力チェック

		CallLogDetail entity = validator.validateForDeleteOne( form );

		// ----- 論理削除

		callLogDetailService.delete( entity );
	}



	// -------------------------------------------------------------------------

	/**
	 * 音声データ取得
	 *
	 * @param req 更新内容
	 *
	 * @return 処理結果と更新内容
	 */
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path( "/voice/{callLogDetailId}" )
	public Response voice(@Context HttpServletResponse res, @PathParam( "callLogDetailId" ) Long callLogDetailId ) {

		String MNAME = "voice";
		restlog.start( log, MNAME, callLogDetailId );

		try {

			// ----- 入力チェック

			CallLogDetail entity = validator.validateForVoice( callLogDetailId );

			// ユーザ情報取得
			CustomUser customUser = loginUtility.getCustomUser();

			// ----- レスポンス作成
			StreamingOutput output = new StreamingOutput() {
				@Override
				public void write(OutputStream output) throws IOException, WebApplicationException {
					// 圧縮ファイルがある場合は圧縮ファイルを返す
					String fileName = String.format(encodedFileName, entity.getCallLogDetailId());
					java.nio.file.Path path = Paths.get(voiceFileRootDirectory, customUser.getCompanyId(), entity.getCallLogId().toString(), fileName);

					// ない場合はwavファイルを返す
					if(!path.toFile().exists()) {
						fileName = String.format(voiceFileName, entity.getCallLogDetailId());
						path = Paths.get(voiceFileRootDirectory, customUser.getCompanyId(), entity.getCallLogId().toString(), fileName);
					}

					try (InputStream audioFile = Files.newInputStream(path)){
						byte[] buf = new byte[16384];
						int size;
						while ((size = audioFile.read(buf)) != -1) {
							output.write(buf, 0, size);
						}
					};
				}
			};

			return Response.ok().entity(output).build();
		}
		catch ( Exception ex ) {
			RestException restException = ExceptionUtil.handleException( log, ResponseCode.SYS_ERROR, null, null, null, ex );

			RestResponse restResponse = new RestResponse() ;
			restResponse.setResultList( restException.getRestResultList() ) ;
			messageUtility.fillMessage( restResponse.getResultList() ) ;
			restlog.abort( restException.getLogger() != null ? restException.getLogger() : log, restResponse, restResponse.getResultList(), restException ) ;

			return null;
		}
	}

}