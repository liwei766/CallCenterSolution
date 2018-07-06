/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CallUserService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.calluser;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.callcentersolution.api.entity.CallUser;
import cloud.optim.callcentersolution.api.entity.dao.CallUserDao;


/**
 * CallUserService実装.<br/>
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Throwable.class, isolation=Isolation.READ_COMMITTED)
public class CallUserService {

	///** Commons Logging instance.  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());


	/**
	 * HibernateDAO
	 */
	@Resource
	private CallUserDao callUserDao;

	/**
	 * CallUserDao 取得
	 * @return CallUserDao
	 */
	public CallUserDao getCallUserDao() {
		return callUserDao;
	}

	/**
	 * MyBatis Mapper
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

	/**
	 * 最新の通話者ログ取得
	 * @param companyId 企業ID
	 * @param callLogId 通話ログID
	 * @return エンティティ
	 */
	public CallUser getLatest(String companyId, Long callLogId) {
		return callUserMapper.getLatest(companyId, callLogId);
	}

	/**
	 *  通話ログIDに紐づく通話者ログを論理削除
	 * @param callLogId 通話ログID
	 * @param companyId 企業ID
	 * @param updateUserId 更新ユーザ ID
	 * @param updateUserName 更新ユーザ名
	 * @param deleteDate 削除日時
	 */
	public void logicalDeleteAllCallUsers(Long callLogId, String companyId, String updateUserId, String updateUserName, Date deleteDate) {
		callUserMapper.logicalDeleteAllCallUsers(callLogId, companyId, updateUserId, updateUserName, deleteDate);
	}

}