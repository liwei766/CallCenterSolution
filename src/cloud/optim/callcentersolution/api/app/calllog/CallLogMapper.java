/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CallLogMapper.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.calllog;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import cloud.optim.callcentersolution.api.entity.CallLog;

/**
 * MyBatis CallLogMapper I/F.<br/>
 */
@Component
public interface CallLogMapper {

	/**
	 * 検索
	 *
	 * @param form 検索条件
	 * @return 検索結果
	 */
	List<SearchResult> search( SearchForm form );


	/**
	 * 検索
	 *
	 * @param form 検索条件
	 * @return 検索結果
	 */
	List<SearchResult> searchByName( SearchForm form );


	/**
	 * 通話詳細取得
	 *
	 * @param callLogId 通話ログID
	 * @param companyId 検索条件
	 * @param userId ユーザID
	 * @return 検索結果
	 */
	CallLog get(@Param("callLogId") Serializable callLogId,@Param("companyId") String companyId, @Param("userId") String userId);

	/**
	 * 通話詳細取得(企業ID配下の全ユーザ)
	 *
	 * @param callLogId 通話ログID
	 * @param companyId 検索条件
	 * @return 検索結果
	 */
	CallLog getCallLogCompanyAllUser(@Param("callLogId") Serializable callLogId,@Param("companyId") String companyId);

	/**
	 * 企業内の問い合わせ番号の最大値を取得する
	 * @param companyId 企業ID
	 * @return 問い合わせ番号の最大値
	 */
	Long getMaxCallLogNo(@Param("companyId")String companyId);

	/**
	 * 通話ログの通話時間合計を取得する
	 * @param callLogId 通話ログID
	 * @return 通話時間合計を取得
	 */
	Long sumCallTime(@Param("callLogId")Long callLogId);

	/**
	 * 削除対象の通話ログを検索する
	 * @param defaultLogKeepDays デフォルト保存期間
	 * @return 削除対象の通話ログIDのリスト
	 */
	List<CallLog> searchForLogDelete(@Param("defaultLogKeepDays")Long defaultLogKeepDays);

	/**
	 * 音声ファイル削除対象の通話ログを検索する
	 * @param defaultKeppDays デフォルト保存期間
	 * @return 音声ファイル削除対象の通話ログIDのリスト
	 */
	List<CallLog> searchForVoiceDelete(@Param("defaultKeppDays")Long defaultKeppDays);
}
