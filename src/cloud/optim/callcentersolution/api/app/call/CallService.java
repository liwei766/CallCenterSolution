/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：CallService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.call;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.callcentersolution.api.app.calllog.CallLogService;
import cloud.optim.callcentersolution.api.constant.CallLogStatus;
import cloud.optim.callcentersolution.api.constant.UseTimeType;
import cloud.optim.callcentersolution.api.entity.CallLog;
import cloud.optim.callcentersolution.api.entity.CallLogDetail;
import cloud.optim.callcentersolution.api.entity.CallUser;
import cloud.optim.callcentersolution.api.entity.UseTime;
import cloud.optim.callcentersolution.api.entity.dao.CallLogDao;
import cloud.optim.callcentersolution.api.entity.dao.CallLogDetailDao;
import cloud.optim.callcentersolution.api.entity.dao.CallUserDao;
import cloud.optim.callcentersolution.api.entity.dao.UseTimeDao;
import cloud.optim.callcentersolution.api.recaius.result.SpeechNBestResultDetail;
import cloud.optim.callcentersolution.core.common.utility.Cryptor;
import cloud.optim.callcentersolution.core.modules.rest.ResponseCode;
import cloud.optim.callcentersolution.core.modules.rest.RestException;
import cloud.optim.callcentersolution.core.modules.rest.RestResult;


/**
 * CallService実装.<br/>
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Throwable.class, isolation=Isolation.READ_COMMITTED)
public class CallService {

	///** Commons Logging instance.  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());

	/** CallLogService */
	@Resource private CallLogService callLogService;

	/** CallLogDao */
	@Resource private CallLogDao callLogDao;

	/** CallUserDao */
	@Resource private CallUserDao callUserDao;

	/** UseTimeDao */
	@Resource private UseTimeDao useTimeDao;

	/** CallLogDetailDao */
	@Resource private CallLogDetailDao callLogDetailDao;

  /** 暗号ユーティリティの共通鍵. */
  @Value("${cryptor.key}")
  private String cryptorKey;


	//-----------------------------------------------------------------------

	private static final String FORMAT = "yyyy/MM/dd HH:mm:ss";

	private static final String LOG_BORDER_BASE = "----- %s %s -----";

	/**
	 * 通話開始
	 * @param callLog 通話ログエンティティ
	 * @param callUser 通話者ログエンティティ
	 * @param useTime 利用時間エンティティ
	 * @param companyId 企業ID
	 * @param userId ユーザID
	 * @param userName ユーザ名
	 */
	public void start( CallLog callLog, CallUser callUser, UseTime useTime, String companyId, String userId, String userName ) {
		// ----- 通話ログの登録又は更新
		Date now = new Date();
		if (callLog.getCallLogId() == null) {
			callLog.setCompanyId(companyId);
			callLog.setCallLogNo(callLogService.getMaxCallLogNo(companyId));
			callLog.setStartDate(now);
			callLog.setCreateDate(now);
			callLog.setCreateUserId(userId);
			callLog.setCreateUserName(userName);
			callLog.setUpdateDate(now);
		}
		callLog.setEndDate(now);
		callLog.setStatus(CallLogStatus.CALLING.getValue());
		callLog.setUpdateUserId(userId);
		callLog.setUpdateUserName(userName);
		callLogDao.saveOrUpdate(callLog);

		// ----- 通話者ログ登録
		callUser.setCompanyId(companyId);
		callUser.setCallLogId(callLog.getCallLogId());
		callUser.setUserId(userId);
		callUser.setUserName(userName);
		callUser.setStartDate(now);
		callUser.setEndDate(now);
		callUser.setCreateDate(now);
		callUser.setCreateUserId(userId);
		callUser.setCreateUserName(userName);
		callUser.setUpdateDate(now);
		callUser.setUpdateUserId(userId);
		callUser.setUpdateUserName(userName);
		callUserDao.save(callUser);

		// 利用時間登録
		useTime.setCompanyId(companyId);
		useTime.setUserId(userId);
		useTime.setUserName(userName);
		useTime.setType(UseTimeType.SPEECH.getValue());
		useTime.setStartDate(now);
		useTime.setEndDate(now);
		useTime.setUseTime(0L);
		useTime.setCreateDate(now);
		useTime.setCreateUserId(userId);
		useTime.setCreateUserName(userName);
		useTime.setUpdateDate(now);
		useTime.setUpdateUserId(userId);
		useTime.setUpdateUserName(userName);
		useTimeDao.save(useTime);
	}


	/**
	 * 通話ログテーブルの更新
	 * @param callLogId 通話ログID
	 * @param logs 更新内容
	 * @param companyId 企業ID
	 * @param userId ユーザID
	 * @param userName ユーザ名
	 * @param end 通話終了フラグ
	 */
	public void updateCallLog( Long callLogId, List<SpeechNBestResultDetail> logs, String companyId, String userId, String userName, boolean end, Date now ) {
		if (callLogId == null) {
			throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_REQUIRED, null, "#callLog.callLogId", callLogId ) );
		}

		// 以下処理は本来バリデータでやる処理だが、通話更新は呼ばれる頻度が高いのでDBアクセスを少なくするためサービスクラスでチェックする
		CallLog callLog =  callLogDao.get(callLogId);
		// 取得できない、企業IDが違う
		if (callLog == null || !companyId.equals(callLog.getCompanyId())) {
			throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, "#callLog.callLogId", callLogId ) );
		}

		// ----- 通話ログの更新
		if (end) {
			callLog.setStatus(CallLogStatus.FREE.getValue());
		}
		callLog.setEndDate(now);
		callLog.setUpdateUserId(userId);
		callLog.setUpdateUserName(userName);
		callLogDao.update(callLog);


		// ----- 通話ログ詳細の登録

		// リカイアス解析結果の登録
		for (SpeechNBestResultDetail each : logs) {
			CallLogDetail callLogDetail = new CallLogDetail();
			callLogDetail.setCallLogId(callLogId);
			callLogDetail.setCompanyId(companyId);
			callLogDetail.setLog(Cryptor.encrypt(this.cryptorKey, each.getResult()));
			callLogDetail.setBegin(each.getBegin());
			callLogDetail.setEnd(each.getEnd());
			callLogDetail.setVoiceExistence(false);
			callLogDetail.setBorder(false);
			callLogDetail.setCreateDate(now);
			callLogDetail.setCreateUserId(userId);
			callLogDetail.setCreateUserName(userName);
			callLogDetail.setUpdateDate(now);
			callLogDetail.setUpdateUserId(userId);
			callLogDetail.setUpdateUserName(userName);
			callLogDetailDao.save(callLogDetail);
		}

		// 終了時は通話区切りを登録
		if (end) {
			String border = String.format(LOG_BORDER_BASE, DateFormatUtils.format(now, FORMAT), userId);
			CallLogDetail callLogDetail = new CallLogDetail();
			callLogDetail.setCallLogId(callLogId);
			callLogDetail.setCompanyId(companyId);
			callLogDetail.setLog(border); // 区切りはパフォーマンスの問題もあるので暗号化しない
			callLogDetail.setBegin(null);
			callLogDetail.setEnd(null);
			callLogDetail.setVoiceExistence(false);
			callLogDetail.setBorder(true);
			callLogDetail.setCreateDate(now);
			callLogDetail.setCreateUserId(userId);
			callLogDetail.setCreateUserName(userName);
			callLogDetail.setUpdateDate(now);
			callLogDetail.setUpdateUserId(userId);
			callLogDetail.setUpdateUserName(userName);
			callLogDetailDao.save(callLogDetail);
		}
	}

	/**
	 * 通話者ログテーブルと利用時間テーブルの更新
	 * @param callUserId 通話者ログID
	 * @param useTimeId 利用時間ID
	 * @param time 利用時間
	 * @param companyId 企業ID
	 * @param userId ユーザID
	 * @param userName ユーザ名
	 */
	public void updateCallUserAndUseTime( Long callUserId, Long useTimeId, Long time, String companyId, String userId, String userName, Date now ) {
		if (callUserId == null) {
			throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_REQUIRED, null, "#callUser.callUserId", callUserId ) );
		}

		if (useTimeId == null) {
			throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_REQUIRED, null, "#useTime.useTimeId", useTimeId ) );
		}

		// ----- 通話者ログの取得
		CallUser callUser =  callUserDao.get(callUserId);
		// 以下処理は本来バリデータでやる処理だが、通話更新は呼ばれる頻度が高いのでDBアクセスを少なくするためサービスクラスでチェックする
		// 取得できない、企業IDが違う、ユーザIDが違う
		if (callUser == null || !companyId.equals(callUser.getCompanyId()) || !userId.equals(callUser.getUserId())) {
			throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, "#callUser.callUserId", callUserId ) );
		}

		// ----- 通話者ログの更新
		callUser.setEndDate(now);
		callUser.setUpdateUserId(userId);
		callUser.setUpdateUserName(userName);
		callUserDao.update(callUser);


		// ----- 利用時間の取得
		UseTime useTime =  useTimeDao.get(useTimeId);
		// 以下処理は本来バリデータでやる処理だが、通話更新は呼ばれる頻度が高いのでDBアクセスを少なくするためサービスクラスでチェックする
		// 取得できない、企業IDが違う
		if (useTime == null || !companyId.equals(useTime.getCompanyId()) || !userId.equals(useTime.getUserId())) {
			throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, "#useTime.useTimeId", useTimeId ) );
		}

		// ----- 利用時間の更新
		useTime.setUseTime(time);
		useTime.setEndDate(now);
		useTime.setUpdateUserId(userId);
		useTime.setUpdateUserName(userName);
		useTimeDao.update(useTime);
	}
}