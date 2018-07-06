/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：TagService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.tag;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.callcentersolution.api.entity.Tag;
import cloud.optim.callcentersolution.api.entity.dao.TagDao;


/**
 * TagService実装.<br/>
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Throwable.class, isolation=Isolation.READ_COMMITTED)
public class TagService {

	///** Commons Logging instance.  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());


	/**
	 * HibernateDAO
	 */
	@Resource
	private TagDao tagDao;

	/**
	 * TagDao 取得
	 * @return TagDao
	 */
	public TagDao getTagDao() {
		return tagDao;
	}

	/**
	 * MyBatis Mapper
	 */
	@Resource
	private TagMapper tagMapper;

	/**
	 * TagMapper 取得
	 * @return TagMapper
	 */
	public TagMapper getTagMapper() {
		return tagMapper;
	}

	/**
	 * ナレッジIDに紐付くタグを検索する
	 * @param knowledgeId ナレッジID
	 * @param companyId 企業ID
	 * @return 検索結果
	 * @throws Exception エラー
	 */
	public List<Tag> searchTagByKnowledgeId( Long knowledgeId, String companyId) throws Exception {
		return tagMapper.findByKnowledgeId(knowledgeId, companyId);
	}

	/**
	 * ナレッジに紐付かないタグを削除する
	 * @param companyId 企業ID
	 */
	public void deleteUnlinkedTags(String companyId) {
		tagMapper.deleteUnlinkedTags(companyId);
	}

	/**
	 * 企業IDに紐付くタグを全て削除する
	 * @param companyId 企業ID
	 */
	public void deleteAllTags(String companyId) {
		tagMapper.deleteAll(companyId);
	}
}