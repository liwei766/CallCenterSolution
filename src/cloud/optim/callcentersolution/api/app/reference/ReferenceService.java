/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：ReferenceService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.reference;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.callcentersolution.api.entity.Reference;
import cloud.optim.callcentersolution.api.entity.dao.ReferenceDao;


/**
 * ReferenceService実装.<br/>
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Throwable.class, isolation=Isolation.READ_COMMITTED)
public class ReferenceService {

	///** Commons Logging instance.  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());


	/**
	 * HibernateDAO
	 */
	@Resource
	private ReferenceDao referenceDao;

	/**
	 * ReferenceDao 取得
	 * @return ReferenceDao
	 */
	public ReferenceDao getReferenceDao() {
		return referenceDao;
	}

	/**
	 * MyBatis Mapper
	 */
	@Resource
	private ReferenceMapper referenceMapper;

	/**
	 * ReferenceMapper 取得
	 * @return ReferenceMapper
	 */
	public ReferenceMapper getReferenceMapper() {
		return referenceMapper;
	}

	/**
	 * ナレッジIDに紐付く参照先を検索する
	 * @param knowledgeId ナレッジID
	 * @param companyId 企業ID
	 * @return 検索結果
	 * @throws Exception エラー
	 */
	public List<Reference> searchReferenceByKnowledgeId( Long knowledgeId, String companyId ) throws Exception {
		return referenceMapper.findByKnowledgeId(knowledgeId, companyId);
	}

	/**
	 *参照先を削除する
	 * @param knowledgeId ナレッジID
	 * @param companyId 企業ID
	 */
	public void deleteReferences( Long knowledgeId, String companyId ) {
		referenceMapper.deleteReferences(knowledgeId, companyId);
	}

	/**
	 * 企業IDに紐付く参照先を全て削除する
	 * @param companyId 企業ID
	 */
	public void deleteAllReferences(String companyId) {
		referenceMapper.deleteAll(companyId);
	}
}