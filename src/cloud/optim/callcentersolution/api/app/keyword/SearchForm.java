/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：SearchForm.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.keyword;

import cloud.optim.callcentersolution.core.common.utility.ToStringHelper;

/**
 * 検索条件.
 */
public class SearchForm implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	/** 解析テキスト */
	private String text;

	/** 抽出件数 */
	private Integer extractCount;


	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString() { return ToStringHelper.toString( this ); }

	/**
	 * text 取得.
	 *
	 * @return text
	 */
	public String getText() {
		return text;
	}

	/**
	 * text 設定.
	 *
	 * @param text text に設定する値.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * extractCount 取得.
	 *
	 * @return extractCount
	 */
	public Integer getExtractCount() {
		return extractCount;
	}

	/**
	 * extractCount 設定.
	 *
	 * @param extractCount extractCount に設定する値.
	 */
	public void setExtractCount(Integer extractCount) {
		this.extractCount = extractCount;
	}
}
