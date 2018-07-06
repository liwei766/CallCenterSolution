/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CallUserMapper.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.calluser;

import java.util.Date;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import cloud.optim.callcentersolution.api.entity.CallUser;

/**
 * MyBatis CallUserMapper I/F.<br/>
 */
@Component
public interface CallUserMapper {

	/**
	 * 通話ログIDに紐付く最新の通話者ログエンティティを取得する
	 *
	 * @param companyId 企業ID
	 * @param callLogId 通話ログID
	 *
	 * @return 通話者ログエンティティ
	 */
	CallUser getLatest(@Param("companyId") String companyId, @Param("callLogId") Long callLogId);


	/**
	 * 通話ログIDに紐づく通話者ログを一括論理削除
	 * @param callLogId 通話ログID
	 * @param companyId 企業ID
	 * @param updateUserId 更新ユーザID
	 * @param updateUserName 更新ユーザ名
	 * @param deleteDate 削除日時
	 */
	void logicalDeleteAllCallUsers( @Param("callLogId") Long callLogId, @Param("companyId") String companyId,
		@Param("updateUserId") String updateUserId, @Param("updateUserName") String updateUserName,
		@Param("deleteDate") Date deleteDate);

	/**
	 * 通話ログIDに紐づく通話者ログを一括削除
	 * @param callLogId 通話ログID
	 */
	void deleteAllCallUsers( @Param("callLogId") Long callLogId);


}
