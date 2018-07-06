/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：VoiceEncodeQueueService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.job.voicedelete;

import java.io.File;
import java.nio.file.Paths;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.callcentersolution.api.app.calllog.CallLogService;
import cloud.optim.callcentersolution.api.app.calllogdetail.CallLogDetailService;
import cloud.optim.callcentersolution.api.app.calllogdetail.CallLogDetailMapper;
import cloud.optim.callcentersolution.api.app.calluser.CallUserMapper;
import cloud.optim.callcentersolution.api.entity.CallLogDetail;


/**
 * VoiceEncodeQueueService実装.<br/>
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Throwable.class, isolation=Isolation.READ_COMMITTED)
public class VoiceDeleteService {

	///** Commons Logging instance.  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());

	/** CallLogService */
	@Resource
	private CallLogService callLogService;

	/** CallLogDetailService */
	@Resource
	private CallLogDetailService callLogDetailService;

	/**
	 * MyBatis CallLogDetailMapper
	 */
	@Resource
	private CallLogDetailMapper callLogDetailMapper;

	/**
	 * CallLogDetailMapper 取得
	 * @return CallLogDetailMapper
	 */
	public CallLogDetailMapper getCallLogDetailMapper() {
		return callLogDetailMapper;
	}

	/**
	 * MyBatis CallUserMapper
	 */
	@Resource
	private CallUserMapper callUserMapper;

	/**
	 * CallUserMapper 取得
	 * @return CallUserMapper
	 */
	public CallUserMapper getCallUserMapper() {
		return callUserMapper;
	}

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
	 * 通話ログ詳細の削除または音声有無フラグの更新を行い、音声ファイルを削除する
	 * @param entity 通話ログ詳細エンティティ
	 * @param callLogDetailDeleteFlg 通話ログ詳細の削除フラグ
	 * @throws Exception
	 */
	public void deleteVoice( CallLogDetail entity, Boolean callLogDetailDeleteFlg) throws Exception {

		if (callLogDetailDeleteFlg) {
			// 通話ログ詳細の削除
			callLogDetailService.delete(entity.getCallLogDetailId());
		} else {
			// 音声有無フラグの更新
			callLogDetailMapper.updateVoiceExistence(entity.getCallLogDetailId());
		}

		// ファイル削除
		String dirPath  = Paths.get(voiceFileRootDirectory, entity.getCompanyId(), entity.getCallLogId().toString()).toString();

		// 圧縮音声ファイルの有無チェック
		File targetFile = Paths.get(dirPath, String.format(encodedFileName, entity.getCallLogDetailId())).toFile();
		if(!targetFile.exists()) {
			// 圧縮音声が無い場合はwav形式のファイルを設定する
			targetFile = Paths.get(dirPath, String.format(voiceFileName, entity.getCallLogDetailId())).toFile();
		}

		// ファイルの削除
		// 圧縮形式とWAV形式のファイルが両方とも存在しない場合はDBの更新のみ行う
		if(targetFile.exists()) {
			if(!targetFile.delete()) {
				throw new Exception("ファイルの削除に失敗しました。");
			}
		}
	}

	/**
	 * 通話ログ詳細、通話者ログ、通話ログテーブルを削除する
	 * @param id 通話ログID
	 * @throws Exception
	 */
	public void deleteLog( Long callLogId) throws Exception {

		// 通話ログ詳細の一括削除
		callLogDetailMapper.deleteAllCallLogDetails(callLogId);

		// 通話者ログの一括削除
		callUserMapper.deleteAllCallUsers(callLogId);

		// 通話ログの削除
		callLogService.delete(callLogId);
	}

}