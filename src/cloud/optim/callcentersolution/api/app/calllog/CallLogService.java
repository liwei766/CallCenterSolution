/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CallLogService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.calllog;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.callcentersolution.api.entity.CallLog;
import cloud.optim.callcentersolution.api.entity.dao.CallLogDao;


/**
 * CallLogService実装.<br/>
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Throwable.class, isolation=Isolation.READ_COMMITTED)
public class CallLogService {

	///** Commons Logging instance.  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());


	/**
	 * HibernateDAO
	 */
	@Resource
	private CallLogDao callLogDao;

	/**
	 * CallLogDao 取得
	 * @return CallLogDao
	 */
	public CallLogDao getCallLogDao() {
		return callLogDao;
	}

	/**
	 * MyBatis Mapper
	 */
	@Resource
	private CallLogMapper callLogMapper;

	/**
	 * CallLogMapper 取得
	 * @return CallLogMapper
	 */
	public CallLogMapper getCallLogMapper() {
		return callLogMapper;
	}


	/**
	 * 一件検索
	 * @param id エンティティの識別ID
	 * @param companyId 企業ID
	 * @param userId ユーザID
	 * @return エンティティ
	 */
	public CallLog getCallLog( Serializable id, String companyId, String userId ) {
//		CallLog result = this.callLogDao.get(id);
//		if (result == null) return null;
//		if (!companyId.equals(result.getCompanyId())) return null;
//		if (!userId.equals(result.getUserId())) return null;

		CallLog result = callLogMapper.get(id, companyId, userId);
		if (result == null) return null;
		return result;
	}


	/**
	 * 一件検索(企業ID配下の全ユーザ)
	 * @param id エンティティの識別ID
	 * @param companyId 企業ID
	 * @return エンティティ
	 */
	public CallLog getCallLogCompanyAllUser( Serializable id, String companyId ) {
		CallLog result = callLogMapper.getCallLogCompanyAllUser(id, companyId);

		if (result == null) return null;
		return result;
	}


	/**
	 * 一件検索
	 * @param id エンティティの識別ID
	 * @return エンティティ
	 */
	public CallLog get( Serializable id ) {
		return this.callLogDao.get(id);
	}

	/**
	 * 一件検索
	 * @param id エンティティの識別ID
	 * @return エンティティ
	 */
	public CallLog getByNo( Long callLogNo, String companyId ) {
		CallLog example = new CallLog();
		example.setCallLogNo(callLogNo);
		example.setCompanyId(companyId);
		List<CallLog> result = this.callLogDao.findByExample(example);
		if(result == null || result.size() != 1) return null;
		return result.get(0);
	}

	/**
	 * 問い合わせ番号の最大値+1を取得
	 * @param companyId 企業ID
	 * @return 問い合わせ番号最大値+1
	 */
	public Long sumCallTime( Long callLogId ) {
		Long result = callLogMapper.sumCallTime(callLogId);
		return result == null ? 0L : result;
	}

	/**
	 * 問い合わせ番号の最大値+1を取得
	 * @param companyId 企業ID
	 * @return 問い合わせ番号最大値+1
	 */
	public Long getMaxCallLogNo( String companyId ) {
		Long result = callLogMapper.getMaxCallLogNo(companyId);
		return result == null ? 1L : result + 1L;
	}

	/**
	 * 複数検索
	 * @param searchForm 検索フォーム
	 * @return 検索結果リスト
	 * @throws Exception エラー
	 */
	public List<SearchResult> search(SearchForm searchForm) throws Exception {
		List<SearchResult> list = callLogMapper.search(searchForm);
		return list;
	}

	/**
	 * 複数検索(通話者名検索)
	 * @param searchForm 検索フォーム
	 * @return 検索結果リスト
	 * @throws Exception エラー
	 */
	public List<SearchResult> searchByName(SearchForm searchForm) throws Exception {
		List<SearchResult> list = callLogMapper.searchByName(searchForm);
		return list;
	}

	/**
	 * 登録
	 * @param entity エンティティ
	 * @return 登録したエンティティ
	 */
	public CallLog save( CallLog entity ) {
		this.callLogDao.save(entity);
		return entity;
	}

	/**
	 * 更新
	 * @param entity エンティティ
	 * @return 更新したエンティティ
	 */
	public CallLog update( CallLog entity ) {
		this.callLogDao.update(entity);
		return entity;
	}

	/**
	 * 削除対象の通話ログを検索する
	 * @param callLogId デフォルト保存期間
	 * @return 音声ファイル削除対象の通話ログIDのリスト
	 * @throws Exception エラー
	 */
	public List<CallLog> searchForLogDelete(Long defaultLogKeepDays) throws Exception {
		return callLogMapper.searchForLogDelete(defaultLogKeepDays);
	}

	/**
	 * 音声ファイル削除対象の通話ログを検索する
	 * @param callLogId デフォルト保存期間
	 * @return 音声ファイル削除対象の通話ログIDのリスト
	 * @throws Exception エラー
	 */
	public List<CallLog> searchForVoiceDelete(Long defaultKeepDays) throws Exception {
		return callLogMapper.searchForVoiceDelete(defaultKeepDays);
	}

	/**
	 * 削除
	 * @param id エンティティの識別ID
	 */
	public void delete( Serializable id ) {
		this.callLogDao.delete( this.callLogDao.get(id) );
	}

}