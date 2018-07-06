/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CallLogDetailService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.callcentersolution.api.app.calllogdetail;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.callcentersolution.api.entity.CallLogDetail;
import cloud.optim.callcentersolution.api.entity.VoiceEncodeQueue;
import cloud.optim.callcentersolution.api.entity.dao.CallLogDetailDao;
import cloud.optim.callcentersolution.api.entity.dao.VoiceEncodeQueueDao;
import cloud.optim.callcentersolution.core.common.utility.Cryptor;

/**
 * CallLogDetailService実装.<br/>
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Throwable.class, isolation=Isolation.READ_COMMITTED)
public class CallLogDetailService {

	//** Commons Logging instance.  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());

	/** 暗号ユーティリティの共通鍵. */
	@Value("${cryptor.key}")
	private String cryptorKey;

	/**
	 * HibernateDAO
	 */
	@Resource
	private CallLogDetailDao callLogDetailDao;

	@Resource
	private VoiceEncodeQueueDao voiceEncodeQueueDao;

	/**
	 * CallLogDetailDao 取得
	 * @return CallLogDetailDao
	 */
	public CallLogDetailDao getCallLogDetailDao() {
		return callLogDetailDao;
	}

	/**
	 * MyBatis Mapper
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
	 * 一件検索.
	 * @param id エンティティの識別ID
	 * @return エンティティ
	 */
	public CallLogDetail getCallLogDetail(Serializable id) {
		CallLogDetail entity = this.callLogDetailDao.get(id);
		entity = this.decrypt(entity);
		return entity;
	}

	/**
	 * 登録.
	 * @param entity エンティティ
	 * @return 登録したエンティティ
	 */
	public CallLogDetail save(CallLogDetail entity) {
		entity = this.encrypt(entity);
		this.callLogDetailDao.save(entity);
		return entity;
	}


	/**
	 * 更新.
	 * @param entity エンティティ
	 * @return 更新したエンティティ(更新後の通話内容は復号化する)
	 */
	public CallLogDetail update(CallLogDetail entity) {
		entity = this.encrypt(entity);
		this.callLogDetailDao.update(entity);
		this.callLogDetailDao.evict(entity);
		return this.decrypt(entity);
	}

	/**
	 * 論理削除
	 * @param entity エンティティ
	 */
	public void delete( CallLogDetail entity ) {
		entity.setDeleteDate(new Date());
		this.callLogDetailDao.update( entity );
	}

	/**
	 *  通話ログIDに紐づく詳細ログを一括論理削除
	 * @param callLogId 通話ログID
	 * @param companyId 企業ID
	 * @param updateUserId 更新ユーザ ID
	 * @param updateUserName 更新ユーザ名
	 * @param deleteDate 削除日時
	 */
	public void logicalDeleteAllCallLogDetails( Long callLogId, String companyId, String updateUserId, String updateUserName, Date deleteDate) {
		callLogDetailMapper.logicalDeleteAllCallLogDetails(callLogId, companyId, updateUserId, updateUserName, deleteDate);
	}

	/**
	 * 削除
	 * @param id エンティティの識別ID
	 */
	public void delete( Serializable id ) {
		this.callLogDetailDao.delete( this.callLogDetailDao.get(id) );
	}

	/**
	 * 通話ログIDによる検索.
	 * @param companyId 企業ID
	 * @param callLogId 通話ログID
	 * @return 通話ログ詳細情報のリスト
	 */
	public List<CallLogDetail> getDetails(String companyId, Long callLogId) {
		return this.decrypt(this.callLogDetailMapper.getDetails(companyId, callLogId));
	}

	/**
	 * 音声が作成されていない通話ログ詳細を検索する.
	 * @param companyId 企業ID
	 * @param callLogId 通話ログID
	 * @return 音声無し通話ログ詳細情報のリスト
	 */
	public List<CallLogDetail> searchNoVoice(String companyId, Long callLogId) {
		return this.decrypt(this.callLogDetailMapper.searchNoVoice(companyId, callLogId));
	}

	/**
	 * 音声のある通話ログ詳細を検索する.
	 * @param companyId 企業ID
	 * @param callLogId 通話ログID
	 * @return 音声有り通話ログ詳細情報のリスト 画面表示で使用しないので内容は復号化しない
	 */
	public List<CallLogDetail> searchExistVoice(String companyId, Long callLogId) {
		return this.callLogDetailMapper.searchExistVoice(companyId, callLogId);
	}

	/**
	 * 音声のある通話ログ詳細を論理削除も含めて検索する.
	 * @param companyId 企業ID
	 * @param callLogId 通話ログID
	 * @return 音声有り通話ログ詳細情報のリスト 画面表示で使用しないので内容は復号化しない
	 */
	public List<CallLogDetail> searchAllExistVoice(String companyId, Long callLogId) {
		return this.callLogDetailMapper.searchAllExistVoice(companyId, callLogId);
	}

	/**
	 * 更新して圧縮キューに登録する
	 * @param entity エンティティ
	 * @return 更新したエンティティ(更新後の通話内容は復号化する)
	 */
	public CallLogDetail updateAndRegistQueue(CallLogDetail entity) {
		entity = this.update(entity);
		Date now = new Date();
		VoiceEncodeQueue queue = new VoiceEncodeQueue();
		queue.setCompanyId(entity.getCompanyId());
		queue.setCallLogId(entity.getCallLogId());
		queue.setCallLogDetailId(entity.getCallLogDetailId());
		queue.setCreateDate(now);
		queue.setCreateUserId(entity.getUpdateUserId());
		queue.setCreateUserName(entity.getUpdateUserName());
		queue.setUpdateDate(now);
		voiceEncodeQueueDao.save(queue);
		return entity;
	}

	private CallLogDetail encrypt(CallLogDetail entity) {
		entity.setLog(Cryptor.encrypt(this.cryptorKey, entity.getLog()));
		return entity;
	}

	private CallLogDetail decrypt(CallLogDetail entity) {
		entity.setLog(Cryptor.decrypt(this.cryptorKey, entity.getLog()));
		return entity;
	}

	private List<CallLogDetail> decrypt(List<CallLogDetail> callLogDetailList) {
		for (CallLogDetail entity : callLogDetailList) {
			String log = entity.getLog();
			if (!entity.getBorder() && StringUtils.isNotEmpty(log)) { // 区切でない場合だけ
				entity = this.decrypt(entity);
			}
		}
		return callLogDetailList;
	}

}
