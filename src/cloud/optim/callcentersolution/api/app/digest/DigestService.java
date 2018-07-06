/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：DigestService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.digest;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.callcentersolution.api.constant.UseTimeType;
import cloud.optim.callcentersolution.api.entity.UseTime;
import cloud.optim.callcentersolution.api.entity.dao.UseTimeDao;


/**
 * DigestService実装.<br/>
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Throwable.class, isolation=Isolation.READ_COMMITTED)
public class DigestService {

	///** Commons Logging instance.  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());

	/** UseTimeDao */
	@Resource private UseTimeDao useTimeDao;

	//-----------------------------------------------------------------------

	/**
	 * 利用時間を登録する
	 * @param callLog 通話ログエンティティ
	 * @param callUser 通話者ログエンティティ
	 * @param useTime 利用時間エンティティ
	 * @param companyId 企業ID
	 * @param userId ユーザID
	 * @param userName ユーザ名
	 */
	public void registUseTime( Long time, String companyId, String userId, String userName ) {
		Date now = new Date();
		// 利用時間登録
		UseTime useTime = new UseTime();
		useTime.setCompanyId(companyId);
		useTime.setUserId(userId);
		useTime.setUserName(userName);
		useTime.setType(UseTimeType.DIGEST.getValue());
		useTime.setStartDate(now);
		useTime.setEndDate(now);
		useTime.setUseTime(time);
		useTime.setCreateDate(now);
		useTime.setCreateUserId(userId);
		useTime.setCreateUserName(userName);
		useTime.setUpdateDate(now);
		useTime.setUpdateUserId(userId);
		useTime.setUpdateUserName(userName);
		useTimeDao.save(useTime);
	}
}