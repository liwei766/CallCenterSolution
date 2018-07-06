/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：KnowledgeMapper.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.knowledge;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * MyBatis KnowledgeMapper I/F.<br/>
 */
@Component
public interface KnowledgeMapper {

	/**
	 * 検索
	 * @param form 検索条件
	 * @return 検索結果
	 */
	List<SearchResult> search( SearchForm form );

	/**
	 * 参照回数をインクリメントする
	 * @param knowledgeId ナレッジID
	 * @param companyId 企業ID
	 */
	void incrementClickCount (@Param("knowledgeId") Long knowledgeId, @Param("companyId") String companyId);

	/**
	 * ナレッジとナレッジ関連タグテーブルを削除する
	 * @param companyId 企業ID
	 */
	void deleteByCompanyId( @Param("companyId") String companyId  );

	/**
	 * 企業内のナレッジ番号の最大値を取得する
	 * @param companyId 企業ID
	 * @return ナレッジ番号の最大値
	 */
	Long getMaxKnowledgeNo(@Param("companyId")String companyId);

	/**
	 * CSVエクスポート用検索
	 * @param companyId 企業ID
	 * @return 検索結果
	 */
	List<KnowledgeExportBean> searchForExport(@Param("companyId") String companyId);
}
