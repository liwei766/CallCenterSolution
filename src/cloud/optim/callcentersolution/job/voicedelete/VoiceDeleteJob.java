/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：VoiceEncodeJob.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.job.voicedelete;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;

import cloud.optim.callcentersolution.api.app.calllog.CallLogService;
import cloud.optim.callcentersolution.api.app.calllogdetail.CallLogDetailService;
import cloud.optim.callcentersolution.api.entity.CallLog;
import cloud.optim.callcentersolution.api.entity.CallLogDetail;

/**
 * 音声ファイル削除処理ジョブ<br/>
 */
public class VoiceDeleteJob {

	/** Commons Logging instance.  */
	private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());

	/** CallLogService. */
	@Resource
	private CallLogService callLogService;

	/** CallLogDetailService. */
	@Resource
	private CallLogDetailService callLogDetailService;

	/** VoiceDeleteService. */
	@Resource
	private VoiceDeleteService voiceDeleteService;

	// -------------------------------------------------------------------------
	/** 論理削除通話ログデフォルト保存日数 */
	@Value( "${call.log.delete.keep.days}" )
	private long defaultLogKeepDays;

	/** 音声ファイルデフォルト保存日数 */
	@Value( "${voice.delete.keep.days}" )
	private long defaultKeepDays;

	/** 音声ファイル保存ルートディレクトリ */
	@Value( "${call.voice.file.root.directory}" )
	private String voiceFileRootDirectory;

	// -------------------------------------------------------------------------

	/**
	 * 削除処理
	 */
	public void delete() {

		log.info("### START-LOG-DEL");

		try {
			// ログ削除処理を実行する
			executeLogDeleteProcessing();

			log.info(String.format("### END-LOG-DEL"));
		} catch (Exception e) {
			log.error("### ABORT-LOG-DEL", e);
		}

		log.info("### START-VOICE-DEL");

		try {
			// 音声ファイル削除処理を実行する
			executeVoiceDeleteProcessing();

			log.info(String.format("### END-VOICE-DEL"));
		} catch (Exception e) {
			log.error("### ABORT-VOICE-DEL", e);
		}
	}

	/**
	 * ログ削除処理を実行する
	 * （論理削除された通話ログを物理削除、通話ログに紐づく音声ファイルを削除）
	 * @throws Exception
	 */
	private void executeLogDeleteProcessing() throws Exception {

		// 削除対象の通話ログを取得する
		List<CallLog> targetCallLogList = callLogService.searchForLogDelete(defaultLogKeepDays);

		// 削除対象の通話ログが無ければ処理終了
		if (targetCallLogList == null || targetCallLogList.isEmpty()) return;

		for (CallLog each : targetCallLogList) {
			// 音声有り通話ログ詳細を取得する
			List<CallLogDetail> detailList = callLogDetailService.searchAllExistVoice(each.getCompanyId(), each.getCallLogId());

			if (detailList == null || detailList.isEmpty()) {
				// 音声有りの通話ログ詳細が無い場合
				try {
					// 残りの音声無し通話ログ詳細(レコードがあれば)、通話者ログ、通話ログを削除する
					// 空振り可
					voiceDeleteService.deleteLog(each.getCallLogId());
				} catch (Exception e) {
					log.error(String.format("callLogId : %d\n", each.getCallLogId()), e);
				}

				// 音声ディレクトリの削除
				deleteDir( each );

			} else {

				// 音声有りの通話ログ詳細がある場合
				// 詳細ログと対応する音声ファイルを削除する
				for (CallLogDetail detail : detailList) {
					try {
						voiceDeleteService.deleteVoice(detail, true);
					} catch (Exception e) {
						log.error(String.format("callLogId : %d, callLogDetailId : %d\n", detail.getCallLogId(), detail.getCallLogDetailId()), e);
					}
				}

				// 音声ディレクトリの削除
				if ( deleteDir( each ) ) {
					// ディレクトリなし、またはディレクトリ削除が成功した場合
					// 残りの音声無し通話ログ詳細(レコードがあれば)、通話者ログ、通話ログを削除する
					// 空振り可
					try {
						voiceDeleteService.deleteLog(each.getCallLogId());
					} catch (Exception e) {
						log.error(String.format("callLogId : %d\n", each.getCallLogId()), e);
					}
				} else {
					// ディレクトリがあり、ディレクトリ削除できない場合、何もしない
				}
			}
		}
	}

	/**
	 * 音声ファイル削除処理を実行する
	 * (音声ファイルの削除、対応する通話ログ詳細の音声有無フラグの更新)
	 * @throws Exception
	 */
	private void executeVoiceDeleteProcessing() throws Exception {

		// 音声ファイル削除対象の通話ログを取得する
		List<CallLog> targetCallLogList = callLogService.searchForVoiceDelete(defaultKeepDays);

		// 削除対象の通話ログが無ければ処理終了
		if (targetCallLogList == null || targetCallLogList.isEmpty()) return;

		for (CallLog each : targetCallLogList) {
			// 通話ログ詳細を取得する
			List<CallLogDetail> detailList = callLogDetailService.searchAllExistVoice(each.getCompanyId(), each.getCallLogId());

			// 通話ログ詳細が無い場合はファイル削除処理は行わない
			// ただし、ディレクトリは作成されている可能性があるので以下のディレクトリ削除処理へ
			if (detailList == null || detailList.isEmpty()) {
				// 何もしない
			} else {
				// 音声ファイル削除、通話ログ詳細の音声有無フラグを更新する
				for (CallLogDetail detail : detailList) {
					try {
						voiceDeleteService.deleteVoice(detail, false);
					} catch (Exception e) {
						log.error(String.format("callLogId : %d, callLogDetailId : %d\n", detail.getCallLogId(), detail.getCallLogDetailId()), e);
					}
				}
			}

			// 音声ディレクトリの削除
			deleteDir( each );
		}
	}

	/**
	 * 音声ディレクトリの削除
	 * @param entity 通話ログエンティティ
	 * @return 削除結果
	 * @throws Exception
	 */
	private Boolean deleteDir(CallLog entity) throws Exception {

		// ディレクトリの取得
		File dir = Paths.get(voiceFileRootDirectory, entity.getCompanyId(), entity.getCallLogId().toString()).toFile();

		// 存在チェック
		if ( !dir.exists() ) return true;

		// 削除処理中にエスカレーションが行われた場合はディレクトリを削除すると音声ファイルが消えるので
		// ディレクトリ内にファイルがある場合はディレクトリの削除はしない
		File[] files = dir.listFiles();
		if (files != null && files.length > 0) return false;

		// ディレクトリを削除
		if (dir.exists() && !dir.delete()) {
			log.error(String.format("callLogId : %d  ディレクトリの削除に失敗しました", entity.getCallLogId()));
			return false;
		}

		return true;
	}

}
