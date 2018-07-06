/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CheckwordMapper.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.checkword;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * MyBatis CheckwordMapper I/F.<br/>
 */
@Component
public interface CheckwordMapper {

	/**
	 * 検索
	 *
	 * @param form 検索条件
	 * @return 検索結果
	 */
	List<SearchResult> search( SearchForm form );

	/**
	 * 一覧取得
	 *
	 * @param form 検索条件
	 * @return 検索結果
	 */
	List<SearchResult> getCheckwordsForExport( SearchForm form );

	/**
	 * チェックワードテーブルを削除する
	 * @param companyId 企業ID
	 */
	void deleteByCompanyId( @Param("companyId") String companyId  );
}
