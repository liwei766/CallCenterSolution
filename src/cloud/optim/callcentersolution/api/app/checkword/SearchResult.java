/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SearchResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.checkword;

import cloud.optim.callcentersolution.core.common.utility.ToStringHelper;

/**
 * 検索結果 1 行分のデータ.
 */
public class SearchResult implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	/** Checkword */
	private CheckwordSearchResult checkword;

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString() { return ToStringHelper.toString( this ); }

	/**
	 * checkword 取得.
	 *
	 * @return checkword
	 */
	public CheckwordSearchResult getCheckword() { return checkword; }

	/**
	 * checkword 設定.
	 *
	 * @param checkword checkword に設定する値.
	 */
	public void setCheckword( CheckwordSearchResult checkword ) { this.checkword = checkword; }
}
