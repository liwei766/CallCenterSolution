/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CallService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.call;


import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import cloud.optim.callcentersolution.api.app.calllog.CallLogService;
import cloud.optim.callcentersolution.api.app.calllogdetail.CallLogDetailService;
import cloud.optim.callcentersolution.api.app.calluser.CallUserService;
import cloud.optim.callcentersolution.api.constant.CallLogStatus;
import cloud.optim.callcentersolution.api.entity.CallLog;
import cloud.optim.callcentersolution.api.entity.CallLogDetail;
import cloud.optim.callcentersolution.api.entity.CallUser;
import cloud.optim.callcentersolution.api.entity.UseTime;
import cloud.optim.callcentersolution.api.recaius.result.SpeechNBestResult;
import cloud.optim.callcentersolution.api.recaius.result.SpeechNBestResultDetail;
import cloud.optim.callcentersolution.api.recaius.result.SpeechResult;
import cloud.optim.callcentersolution.api.recaius.service.RecaiusAuthService;
import cloud.optim.callcentersolution.api.recaius.service.RecaiusSpeechService;
import cloud.optim.callcentersolution.api.recaius.util.Util;
import cloud.optim.callcentersolution.core.modules.loginutil.CustomUser;
import cloud.optim.callcentersolution.core.modules.loginutil.LoginUtility;
import cloud.optim.callcentersolution.core.modules.rest.ExceptionUtil;
import cloud.optim.callcentersolution.core.modules.rest.MessageUtility;
import cloud.optim.callcentersolution.core.modules.rest.ResponseCode;
import cloud.optim.callcentersolution.core.modules.rest.RestException;
import cloud.optim.callcentersolution.core.modules.rest.RestLog;
import cloud.optim.callcentersolution.core.modules.rest.RestResult;

/**
 * CallService 実装.<br/>
 */
@Path( "/call" )
@Consumes( { "application/json", "application/xml" } )
@Produces( { "application/json", "application/xml" } )
@Component
public class CallRestService
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() );

	/** PK の項目名 */
	private static final String CALL_LOG_NAME_PK = "#callLog.callLogId";

	// -------------------------------------------------------------------------

	/** バリデータ */
	@Resource private CallRestValidator validator;

	/** RecaiusAuthService */
	@Resource private RecaiusAuthService authService;

	/** RecaiusSpeechService */
	@Resource private RecaiusSpeechService speechService;

	/** CallService */
	@Resource private CallService callService;

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

	/** LoginUtility */
	@Resource private LoginUtility loginUtility;

	/** 通話中情報(セッションスコープに格納されている) */
	@Resource private CallingInfo callingInfo;

	/** オーディオフォーマット */
	@Resource(name="defaultAudioFormat") private AudioFormat audioFormat;

	// -------------------------------------------------------------------------

	/** リカイアス音声解析サービスID */
	@Value( "${recaius.service.speech.type}" )
	private String serviceType;

	/** リカイアスセッション有効時間(秒) */
	@Value( "${recaius.session.expiry.sec}" )
	private long expirySec;

	/** リカイアスセッション延長閾値(ミリ秒) */
	@Value( "${recaius.session.extention.threshold}" )
	private long extentionThreshold;

	/** 音声ファイル保存ルートディレクトリ */
	@Value( "${call.voice.file.root.directory}" )
	private String voiceFileRootDirectory;

	/** 一時ファイル保存ディレクトリ */
	@Value( "${call.tmp.file.directory}" )
	private String tmpFileDirectory;

	/** 分割音声ファイル名 */
	@Value( "${call.voice.file.name}" )
	private String voiceFileName;



	// -------------------------------------------------------------------------

	/**
	 * 通話開始
	 *
	 * @param req
	 *
	 * @return 結果
	 */
	@POST
	@Path( "/start" )
	public CallResponse start(CallRequest req) {

		String MNAME = "start";
		restlog.start( log, MNAME, req );

		// 前回までの結果、利用時間をリセットする
		callingInfo.reset();

		String token = null;
		String uuid = null;

		try {

			// バリデート
			validator.validateForStart(req);

			// ユーザ情報を取得する
			CustomUser customUser = loginUtility.getCustomUser();
			Integer threshold = customUser.getRecaiusEnergyThreshold();
			Integer modelId = customUser.getRecaiusModelId();
			String serviceId = customUser.getRecaiusServiceId();
			String password = customUser.getRecaiusPassword();

			if(threshold == null || modelId == null || serviceId == null || password == null ) {
				throw new RestException( new RestResult(ResponseCode.CALL_RECAIUS_ERROR) );
			}

			// ----- 通話ログ取得
			CallLog callLog = getCallLog(req.getCallLogId(), customUser.getCompanyId());

			// 通話中の場合はエラー返す
			if (CallLogStatus.CALLING.matches(callLog.getStatus())) {
				throw new RestException( new RestResult(ResponseCode.CALL_CALLING_ERROR) );
			}

			// ----- 認証処理でトークンを取得する
			token = authService.auth(serviceType, serviceId, password);
			callingInfo.setSessionStartTime(System.currentTimeMillis());

			// ----- 音声解析開始
			uuid = speechService.startSession(token, threshold, modelId);

			// ----- 通話ログ、通話者ログ、利用時間の登録
			CallUser callUser = new CallUser();
			UseTime useTime = new UseTime();
			callService.start(callLog, callUser, useTime, customUser.getCompanyId(), customUser.getUserId(), customUser.getUserName());

			// 音声保存する企業は音声ファイル名の生成、保存ディレクトリの作成を行う
			if (customUser.isSaveVoice()) {
				preparationForSaveVoice(customUser.getCompanyId(), callLog.getCallLogId(),  token, uuid);
			}


			// ----- レスポンス作成
			CallResponse res = new CallResponse();
			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setToken(token);
			res.setUuid(uuid);
			res.setCallLogId(callLog.getCallLogId());
			res.setCallLogNo(callLog.getCallLogNo());
			res.setCallUserId(callUser.getCallUserId());
			res.setUseTimeId(useTime.getUseTimeId());

			return res;
		}
		catch ( Exception ex ) {

			// セッション終了する
			endSession(token, uuid);

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	/**
	 * 通話ログを取得する
	 * @param callLogId 通話ログID
	 * @param companyId 企業ID
	 * @return 通話ログ
	 */
	private CallLog getCallLog(Long callLogId, String companyId) {
		if(callLogId == null) return new CallLog();

		// 通話ログを取得する
		CallLog result = callLogService.get(callLogId);

		// 取得できない
		if (result == null) {
			throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, CALL_LOG_NAME_PK, callLogId ) );
		}

		// 企業IDが違う
		if (!companyId.equals(result.getCompanyId())) {
			throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, CALL_LOG_NAME_PK, callLogId ) );
		}

		return result;
	}

	// -------------------------------------------------------------------------

	/**
	 * 通話再開
	 *
	 * @param req
	 *
	 * @return 結果
	 */
	@POST
	@Path( "/resume" )
	public CallResponse resume(CallRequest req) {

		String MNAME = "start";
		restlog.start( log, MNAME, req );

		String token = null;
		String uuid = null;

		try {

			// バリデート
			validator.validateForResume(req);

			// ユーザ情報を取得する
			CustomUser customUser = loginUtility.getCustomUser();
			Integer threshold = customUser.getRecaiusEnergyThreshold();
			Integer modelId = customUser.getRecaiusModelId();
			String serviceId = customUser.getRecaiusServiceId();
			String password = customUser.getRecaiusPassword();

			if(threshold == null || modelId == null || serviceId == null || password == null ) {
				throw new RestException( new RestResult(ResponseCode.CALL_RECAIUS_ERROR) );
			}

			// ----- 通話ログ取得
			CallLog callLog = getCallLog(req.getCallLogId(), customUser.getCompanyId());

			// 最終通話者がログインユーザかチェックする
			CallUser callUser = callUserService.getLatest(customUser.getCompanyId(), callLog.getCallLogId());
			if (callUser == null || !customUser.getUserId().equals(callUser.getUserId())) {
				throw new RestException( new RestResult(ResponseCode.CALL_RESUME_ERROR) );
			}

			// ----- 認証処理でトークンを取得する
			token = authService.auth(serviceType, serviceId, password);
			callingInfo.setSessionStartTime(System.currentTimeMillis());

			// ----- 音声解析開始
			uuid = speechService.startSession(token, threshold, modelId);

			// ----- 通話ログ更新
			callLog.setStatus(CallLogStatus.CALLING.getValue());
			callLog.setUpdateUserId(customUser.getUserId());
			callLog.setUpdateUserName(customUser.getUserName());
			callLogService.update(callLog);

			// 音声保存する企業は音声ファイル名の生成、保存ディレクトリの作成を行う
			if (customUser.isSaveVoice()) {
				preparationForSaveVoice(customUser.getCompanyId(), callLog.getCallLogId(),  token, uuid);
			}


			// ----- レスポンス作成
			CallResponse res = new CallResponse();
			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setToken(token);
			res.setUuid(uuid);

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			// セッション終了する
			endSession(token, uuid);

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	// -------------------------------------------------------------------------


	/**
	 * 通話更新API
	 *
	 * @param req
	 *
	 * @return 結果
	 */
	@POST
	@Path( "/update/{callLogId}/{callUserId}/{useTimeId}/{token}/{uuid}/{voiceId}" )
	@Consumes( "multipart/form-data" )
	public CallResponse update(
			@RequestBody byte[] body,
			@PathParam( "callLogId" ) Long callLogId, @PathParam( "callUserId" ) Long callUserId, @PathParam( "useTimeId" ) Long useTimeId,
			@PathParam( "token" ) String token, @PathParam( "uuid" ) String uuid, @PathParam( "voiceId" ) int voiceId) {

		String MNAME = "update";
		restlog.start( log, MNAME, "" );
		try {

			// ----- リカイアスのセッション有効時間が切れそうなら延長する(残り一分ぐらい)
			if (expirySec * 1000L - (System.currentTimeMillis() - callingInfo.getSessionStartTime()) < extentionThreshold) {
				// リカイアスセッション開始からの経過時間が延長閾値を下回った場合はセッション有効時間の延長を行う
				try {
					CustomUser customUser = loginUtility.getCustomUser();
					String serviceId = customUser.getRecaiusServiceId();
					String password = customUser.getRecaiusPassword();

					authService.extention(serviceType, serviceId, password, token);
					callingInfo.setSessionStartTime(System.currentTimeMillis());
				} catch(Exception e) {
					// 延長できなくても送られてきた音声データの解析を行わないと通話ログがなくなるので握りつぶす
					log.error(e);
				}
			}

			// ----- 音声データをリカイアスへ送信する
			SpeechResult result = speechService.sendData(token, uuid, voiceId, body);


			// ユーザ情報を取得するする
			CustomUser customUser = loginUtility.getCustomUser();

			// 音声保存する企業は音声ファイル名の生成、保存ディレクトリの作成を行う
			if (customUser.isSaveVoice()) {
				try {
					saveTmpFile(body);
				} catch (Exception e) {
					// 例外発生時は以降の処理を続行するためログのみ出力する
					log.error(e);
				}
			}

			// ----- 利用時間をセッションに加算する
			// 音声ファイルの長さを利用時間として登録する(ミリ秒)
			// 1ミリ秒辺りの配列要素数 = サンプリング周波数÷1000 * フレームサイズ
			callingInfo.addTime(body.length / (((int)audioFormat.getFrameRate() / 1000) * audioFormat.getFrameSize()));

			// 音声解析結果を取得する
			List<SpeechNBestResult> speechResult = result.getResultList();

			// リカイアスの解析結果から最終結果のみを抽出する
			List<SpeechNBestResultDetail> resultDetails = Util.extractResult(speechResult);

			// リカイアスの解析結果に最終結果が1件以上ある場合は通話ログ、通話者ログ、利用時間の更新を行う
			if (!resultDetails.isEmpty()) {
				// 更新内容のログテキストを生成
				List<SpeechNBestResultDetail> logs = new ArrayList<>(callingInfo.getUncommittedLog());
				logs.addAll(resultDetails);
				callingInfo.clearUncommittedLog();

				// ----- 通話ログの更新
				Date now = new Date();
				try {
					callService.updateCallLog(
							callLogId, logs,
							customUser.getCompanyId(), customUser.getUserId(), customUser.getUserName(), false, now);
				} catch (Exception e) {
					// 登録できなかった通話をグの内容をログ出力内する
					String text = callingInfo.getUncommittedLog().stream().map(each -> each.getResult()).collect(Collectors.joining("\r\n\r\n"));
					log.error(String.format("通話ログを更新できませんでした：[%s], 利用時間：%d", text, callingInfo.getTime()), e);
					// 更新処理でエラーになった場合は更新できなかったログをセッションに保持する
					callingInfo.addAllLog(logs);
				}

				// 通話者ログと利用時間テーブルの更新
				callService.updateCallUserAndUseTime(
						callUserId, useTimeId, callingInfo.getTime(),
						customUser.getCompanyId(), customUser.getUserId(), customUser.getUserName(), now);
			}

			// ----- レスポンス作成

			CallResponse res = new CallResponse();
			res.setResult( new RestResult( ResponseCode.OK ) );

			// リスト配列に変換
			res.setAnalyzeResult(getAnalyzeResult(speechResult));

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, null, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {
			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	// -------------------------------------------------------------------------


	/**
	 * 通話終了API
	 *
	 * @param req
	 *
	 * @return 結果
	 */
	@POST
	@Path( "/end" )
	public CallResponse end(CallRequest req) {
		return endOrPause(req, true);
	}

	/**
	 * 通話一時停止API
	 *
	 * @param req
	 *
	 * @return 結果
	 */
	@POST
	@Path( "/pause" )
	public CallResponse pause(CallRequest req) {
		return endOrPause(req, false);
	}

	/**
	 * 通話終了API
	 *
	 * @param req
	 *
	 * @return 結果
	 */
	private CallResponse endOrPause(CallRequest req, boolean end) {

		String MNAME = "end";
		restlog.start( log, MNAME, req );

		SpeechResult result = null;

		// 音声解析を終了していない場合のみ終了処理を行う
		if (req.getToken() != null && req.getUuid() != null) {
			try {
				// エラーが発生しても終了処理、更新処理は行うため処理は続行する
				result = speechService.flush(req.getToken(), req.getUuid(), req.getVoiceId());
			} catch (Exception e) {
				log.error(e);
			}

			// セッション終了する(例外は発生しても無視する)
			endSession(req.getToken(), req.getUuid());
		}

		try {
			// ユーザ情報を取得するする
			CustomUser customUser = loginUtility.getCustomUser();

			// 音声解析結果を取得する
			List<SpeechNBestResult> speechResult = result == null ? new ArrayList<>() : result.getResultList();

			// リカイアスの解析結果から最終結果のみを抽出する
			List<SpeechNBestResultDetail> resultDetails = Util.extractResult(speechResult);

			// 更新内容のログテキストを生成
			List<SpeechNBestResultDetail> logs = new ArrayList<>(callingInfo.getUncommittedLog());
			logs.addAll(resultDetails);
			callingInfo.clearUncommittedLog();

			// ----- 通話ログの更新
			Date now = new Date();
			try {
				callService.updateCallLog(
						req.getCallLogId(), logs,
						customUser.getCompanyId(), customUser.getUserId(), customUser.getUserName(), end, now);
			} catch (Exception e) {
				// 登録できなかった通話をグの内容をログ出力内する
				String text = callingInfo.getUncommittedLog().stream().map(each -> each.getResult()).collect(Collectors.joining("\r\n\r\n"));
				log.error(String.format("通話ログを更新できませんでした：[%s], 利用時間：%d", text, callingInfo.getTime()), e);
			}

			// 通話者ログと利用時間テーブルの更新
			callService.updateCallUserAndUseTime(
					req.getCallUserId(), req.getUseTimeId(), callingInfo.getTime(),
					customUser.getCompanyId(), customUser.getUserId(), customUser.getUserName(), now);


			// 音声保存する企業は音声ファイルの分割を行い再生可能な形式に保存する
			if (customUser.isSaveVoice()) {
				try{
					divideAudioData(req.getCallLogId());
				} catch (Exception e) {
					// 例外発生時は以降の処理を続行するためログのみ出力する
					log.error(e);
				}
			}


			// ----- レスポンス作成

			CallResponse res = new CallResponse();
			res.setResult( new RestResult( ResponseCode.OK ) );

			// リスト配列に変換
			res.setAnalyzeResult(getAnalyzeResult(speechResult));

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
	 *  リカイアスの解析結果をレスポンスの形式に変換する
	 * @param list リカイアス解析結果
	 * @return レスポンスの形式の解析結果
	 */
	private List<List<String>> getAnalyzeResult(List<SpeechNBestResult> list) {
		List<List<String>> result = new ArrayList<>();
		for (SpeechNBestResult each : list) {
			List<String> detail = new ArrayList<>();
			detail.add(each.getType());
			detail.add(each.getResultDetail().getResult());
			result.add(detail);
		}
		return result;
	}

	// -------------------------------------------------------------------------

	/**
	 * リカイアス音声認識を終了する。終了時に発生した例外はスローしない。
	 * @param token 認証トークン
	 * @param uuid UUID
	 */
	private void endSession (String token, String uuid) {
		// リカイアス音声認識を終了する
		if (uuid != null) {
			try {
				speechService.stopSession(token, uuid);
			} catch (Exception e) {
				log.error(e);
			}
		}

		// リカイアス認証トークンを削除する
		if (token != null) {
			try {
				authService.disconnect(token);
			} catch (Exception e) {
				log.error(e);
			}
		}
	}

	// -------------------------------------------------------------------------
	// 音声ファイル操作系の処理
	// -------------------------------------------------------------------------

	/**
	 *
	 * @param companyId
	 * @param callLogId
	 * @param token
	 * @param uuid
	 * @throws IOException
	 */
	private void preparationForSaveVoice(String companyId, Long callLogId, String token, String uuid) throws IOException {
		// 一時ファイル名の生成
		callingInfo.setTmpFileName(String.format("%s%d_%s_%s", tmpFileDirectory, callLogId, token, uuid));

		// 音声ファイル保存ディレクトリの作成
		Files.createDirectories(Paths.get(voiceFileRootDirectory, companyId, callLogId.toString()));
	}

	/**
	 * 音声データを一時ファイルに保存する
	 * @param data 音声データ
	 * @throws IOException
	 */
	private void saveTmpFile(byte[] data) throws IOException {
		try (FileOutputStream out = new FileOutputStream(Paths.get(callingInfo.getTmpFileName()).toFile(), true)) {
			out.write( data ) ;
			out.flush() ;
		}
	}

	/**
	 * 一時保存した音声データ音声データをリカイアス解析結果の文節毎に分割し、再生可能なファイルに保存する
	 * @throws Exception
	 */
	private void divideAudioData(Long callLogId) throws Exception {

		// ユーザ情報を取得するする
		CustomUser customUser = loginUtility.getCustomUser();

		// 音声が生成されていない通話ログ詳細を取得する
		List<CallLogDetail> detailList = callLogDetailService.searchNoVoice(customUser.getCompanyId(), callLogId);

		try (FileInputStream tmpFile = new FileInputStream(Paths.get(callingInfo.getTmpFileName()).toFile())) {

			CallLogDetail prev = null;
			for(int i = 0; i < detailList.size(); i++) {
				// 対象の要素と次の要素を取得する
				CallLogDetail each = detailList.get(i);
				CallLogDetail next = i + 1 < detailList.size() ? detailList.get(i + 1) : null;

				// 開始秒数の取得
				// 先頭の要素の場合は始めから、それ以外の場合は前の要素の終了+1から開始する
				Integer begin = prev == null ?   0 : prev.getEnd() + 1;

				// 終了秒数の取得
				// 末尾の要素の場合はファイルの最後まで、それ以外の場合は対象要素の終了+1から開始する
				Integer end = next == null ?  null : each.getEnd();

				// 読み込むサイズの取得
				int size = end == null ? tmpFile.available() :  (end - begin) * ((int)audioFormat.getFrameRate() / 1000) * audioFormat.getFrameSize();

				// バイトデータを一時ファイルから読む
				byte[] buf = new byte[size];
				tmpFile.read(buf);

				// バイト配列をwaveファイルにする
				try (
					ByteArrayInputStream byteArrayInput = new ByteArrayInputStream(buf);
					AudioInputStream audioFile = new AudioInputStream(byteArrayInput, audioFormat, size / audioFormat.getFrameSize())) {

					String fileName = String.format(voiceFileName, each.getCallLogDetailId());
					AudioSystem.write(
							audioFile,
							AudioFileFormat.Type.WAVE,
							Paths.get(voiceFileRootDirectory, customUser.getCompanyId(), callLogId.toString(), fileName).toFile());
				}

		        // 通話ログ詳細を更新する
				each.setVoiceExistence(true);
				each.setUpdateUserId(customUser.getUserId());
				each.setUpdateUserName(customUser.getUserName());
				callLogDetailService.updateAndRegistQueue(each);

				// 前要素設定
				prev = each;
			}
		}

		// 一時ファイル削除する
		Files.delete(Paths.get(callingInfo.getTmpFileName()));
	}
}