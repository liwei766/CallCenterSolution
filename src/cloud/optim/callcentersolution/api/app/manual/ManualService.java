/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：ManualService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.manual;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.callcentersolution.api.entity.Manual;
import cloud.optim.callcentersolution.api.entity.dao.ManualDao;


/**
 * ManualService実装.<br/>
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Throwable.class, isolation=Isolation.READ_COMMITTED)
public class ManualService {

	///** Commons Logging instance.  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());

	/**
	 * HibernateDAO
	 */
	@Resource
	private ManualDao manualDao;

	/**
	 * ManualDao 取得
	 * @return ManualDao
	 */
	public ManualDao getManualDao() {
		return manualDao;
	}

	/**
	 * MyBatis Mapper
	 */
	@Resource
	private ManualMapper manualMapper;

	/**
	 * ManualMapper 取得
	 * @return ManualMapper
	 */
	public ManualMapper getManualMapper() {
		return manualMapper;
	}

	/**
	 * ナレッジIDに紐付くマニュアルを検索する
	 * @param knowledgeId ナレッジID
	 * @param companyId 企業ID
	 * @return 検索結果
	 * @throws Exception エラー
	 */
	public List<Manual> searchManualByKnowledgeId( Long knowledgeId, String companyId ) throws Exception {
		return manualMapper.findByKnowledgeId(knowledgeId, companyId);
	}

	/**
	 * ナレッジに紐付かないマニュアルを削除する
	 * @param knowledgeId ナレッジID
	 * @param companyId 企業ID
	 */
	public void deleteManuals( Long knowledgeId, String companyId ) {
		manualMapper.deleteManuals(knowledgeId, companyId);
	}

	/**
	 * 企業IDに紐付くマニュアルを全て削除する
	 * @param companyId 企業ID
	 */
	public void deleteAllManuals(String companyId) {
		manualMapper.deleteAll(companyId);
	}
}