/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：SearchResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.keyword;

import cloud.optim.callcentersolution.core.common.utility.ToStringHelper;

/**
 * 検索結果 1 行分のデータ.
 */
public class SearchResult implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	/** 抽出したキーワード */
	private String keyword;

	/** タグID */
	private Long tagId;


	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString() { return ToStringHelper.toString( this ); }

	/**
	 * keyword 取得.
	 *
	 * @return keyword
	 */
	public String getKeyword() {
		return keyword;
	}

	/**
	 * keyword 設定.
	 *
	 * @param keyword keyword に設定する値.
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * tagId 取得.
	 *
	 * @return tagId
	 */
	public Long getTagId() {
		return tagId;
	}

	/**
	 * tagId 設定.
	 *
	 * @param tagId tagId に設定する値.
	 */
	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}


}
