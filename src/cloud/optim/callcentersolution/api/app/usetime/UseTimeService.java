/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：UseTimeService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.usetime;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * UseTimeService実装.<br/>
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Throwable.class, isolation=Isolation.READ_COMMITTED)
public class UseTimeService {


	/**
	 * MyBatis Mapper
	 */
	@Resource
	private UseTimeMapper useTimeMapper;

	/**
	 * UseTimeMapper 取得
	 * @return UseTimeMapper
	 */
	public UseTimeMapper getKeywordMapper() {
		return useTimeMapper;
	}


	/**
	 * テキストから解析した名詞でタグ情報を取得する
	 * @param year 年
	 * @param month 月
	 * @return 検索結果
	 * @throws Exception
	 */
	public List<SearchResult> searchByMonth(SearchForm searchForm) throws Exception {

		List<SearchResult> useTimeList = useTimeMapper.searchByMonth( searchForm);

		if (useTimeList == null) return null;
		return useTimeList;
	}


	/**
	 * テキストから解析した名詞でタグ情報を取得する
	 * @param companyId 企業ID
	 * @return 検索結果
	 * @throws Exception
	 */
	public List<SearchResult> searchByCompanyId(SearchForm searchForm) throws Exception {

		List<SearchResult> useTimeList = useTimeMapper.searchByCompanyId( searchForm);

		if (useTimeList == null) return null;
		return useTimeList;
	}

}