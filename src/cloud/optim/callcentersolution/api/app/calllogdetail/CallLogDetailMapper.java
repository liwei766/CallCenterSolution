/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CallLogDetailMapper.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.calllogdetail;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import cloud.optim.callcentersolution.api.entity.CallLogDetail;

/**
 * MyBatis CallLogDetailMapper I/F.<br/>
 */
@Component
public interface CallLogDetailMapper {


	/**
	 * 通話ログIDによる検索
	 * @param companyId 企業ID
	 * @param callLogId 通話ログID
	 * @return 通話ログ詳細情報のリスト
	 */
	List<CallLogDetail> getDetails(@Param("companyId") String companyId, @Param("callLogId") Long callLogId);


	/**
	 * 音声無し通話ログ詳細情報検索
	 * @param companyId 企業ID
	 * @param callLogId 通話ログID
	 * @return 音声無し通話ログ詳細情報のリスト
	 */
	List<CallLogDetail> searchNoVoice(@Param("companyId") String companyId, @Param("callLogId") Long callLogId);

	/**
	 * 音声有り通話ログ詳細情報検索(論理削除も含む)
	 * @param companyId 企業ID
	 * @param callLogId 通話ログID
	 * @return 音声有り通話ログ詳細情報のリスト
	 */
	List<CallLogDetail> searchAllExistVoice(@Param("companyId") String companyId, @Param("callLogId") Long callLogId);

	/**
	 * 音声有り通話ログ詳細情報検索
	 * @param companyId 企業ID
	 * @param callLogId 通話ログID
	 * @return 音声有り通話ログ詳細情報のリスト
	 */
	List<CallLogDetail> searchExistVoice(@Param("companyId") String companyId, @Param("callLogId") Long callLogId);

	/**
	 * 音声有無フラグ無しに更新する
	 * @param callLogDetailId 通話ログ詳細ID
	 */
	void updateVoiceExistence (@Param("callLogDetailId") Long callLogDetailId);

	/**
	 * 通話ログIDに紐づく詳細ログを一括論理削除
	 * @param callLogId  通話ログID
	 * @param companyId 企業ID
	 * @param updateUserId 更新ユーザID
	 * @param updateUserName 更新ユーザ名
	 * @param deleteDate 削除日時
	 */
	void logicalDeleteAllCallLogDetails( @Param("callLogId") Long callLogId, @Param("companyId") String companyId,
		@Param("updateUserId") String updateUserId, @Param("updateUserName") String updateUserName,
		@Param("deleteDate") Date deleteDate);

	/**
	 * 通話ログIDに紐づく詳細ログを一括削除
	 * @param callLogId 通話ログID
	 */
	void deleteAllCallLogDetails( @Param("callLogId") Long callLogId);

}
