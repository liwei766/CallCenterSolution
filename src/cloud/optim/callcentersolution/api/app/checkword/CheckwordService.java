/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CheckwordService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.checkword;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.callcentersolution.api.entity.Checkword;
import cloud.optim.callcentersolution.api.entity.dao.CheckwordDao;
import cloud.optim.callcentersolution.core.common.DaoException;


/**
 * CheckwordService実装.<br/>
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Throwable.class, isolation=Isolation.READ_COMMITTED)
public class CheckwordService {

	///** Commons Logging instance.  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());


	/**
	 * HibernateDAO
	 */
	@Resource
	private CheckwordDao checkwordDao;

	/**
	 * CheckwordDao 取得
	 * @return CheckwordDao
	 */
	public CheckwordDao getCheckwordDao() {
		return checkwordDao;
	}

	/**
	 * MyBatis Mapper
	 */
	@Resource
	private CheckwordMapper checkwordMapper;

	/**
	 * CheckwordMapper 取得
	 * @return CheckwordMapper
	 */
	public CheckwordMapper getCheckwordMapper() {
		return checkwordMapper;
	}


	/**
	 * 一件検索
	 * @param id エンティティの識別ID
	 * @return エンティティ
	 */
	public Checkword getCheckword( Serializable id ) {
		return this.checkwordDao.get(id);
	}


	/**
	 * 一件検索（検索結果形式）
	 * @param id エンティティの識別ID
	 * @return 検索結果
	 * @throws Exception エラー
	 */
	public SearchResult getSearchResult( Long id ) throws Exception {

		SearchForm searchForm = new SearchForm();
		CheckwordSearchForm entityForm = new CheckwordSearchForm();
		entityForm.setCheckwordId( String.valueOf( id ) );
		searchForm.setCheckword( entityForm );

		SearchResult ret = null;

		List<SearchResult> list = checkwordMapper.search(searchForm);

		if ( list.size() > 1 ) throw new DaoException( "More than one records are found. : " + list.size() );
		if ( list.size() == 1 ) ret = list.get( 0 );

		return ret;
	}


	/**
	 * 複数検索
	 * @param searchForm 検索フォーム
	 * @return 検索結果リスト
	 * @throws Exception エラー
	 */
	public List<SearchResult> search(SearchForm searchForm) throws Exception {
		List<SearchResult> list = checkwordMapper.search(searchForm);
		return list;
	}

	/**
	 * エクスポートリスト取得
	 * @param searchForm 検索フォーム
	 * @return 検索結果リスト
	 * @throws Exception エラー
	 */
	public List<SearchResult> getCheckwordsForExport(String companyId) throws Exception {
		// ----- 検索条件用意
		SearchForm searchForm = new SearchForm();

		CheckwordSearchForm checkwordForm =  new CheckwordSearchForm();

		checkwordForm.setCompanyId(companyId);

		searchForm.setCheckword( checkwordForm );

		List<SearchResult> list = checkwordMapper.getCheckwordsForExport(searchForm);

		return list;
	}


	/**
	 * 登録
	 * @param entity エンティティ
	 * @return 登録したエンティティ
	 */
	public Checkword save( Checkword entity ) {
		this.checkwordDao.save(entity);
		return entity;
	}


	/**
	 * 更新
	 * @param entity エンティティ
	 * @return 更新したエンティティ
	 */
	public Checkword update( Checkword entity ) {
		this.checkwordDao.update(entity);
		return entity;
	}


	/**
	 * 削除
	 * @param id エンティティの識別ID
	 */
	public void delete( Serializable id ) {
		this.checkwordDao.delete( this.checkwordDao.get(id) );
	}

	/**
	 * CSVインポート
	 * @param companyId
	 * @param checkwordList
	 */
	public void importCheckword(String companyId,String userId,String userName, List<Checkword> checkwordList) {

		if (checkwordList == null || checkwordList.isEmpty()) return;

		checkwordMapper.deleteByCompanyId(companyId);

		// チェックワードテーブル登録
		for(Checkword each : checkwordList) {
			each.setCompanyId(companyId);

			// 現在日時の取得
			Date now = new Date();

			each.setCreateDate(now);
			each.setCreateUserId(userId);
			each.setCreateUserName(userName);
			each.setUpdateDate(now);
			each.setUpdateUserId(userId);
			each.setUpdateUserName(userName);

			checkwordDao.saveNoFlush(each);
		}
		checkwordDao.flush();

	}

}