/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：KeywordMapper.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.keyword;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * MyBatis KeywordMapper I/F.<br/>
 */
@Component
public interface KeywordMapper {
	/**
	 * タグ検索
	 *
	 * @param companyId 検索条件
	 * @param nouns 名詞リスト
	 * @return 検索結果
	 */
	List<SearchResult> search(@Param("companyId") String companyId, @Param("nouns") List<String> nouns);

	/**
	 * タグ取得
	 *
	 * @param companyId 検索条件
	 * @param tagName タグ名称
	 * @return 検索結果
	 */
	SearchResult get(@Param("companyId") String companyId, @Param("tagName") String tagName);
}
