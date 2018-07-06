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
package cloud.optim.callcentersolution.api.app.knowledge;

import cloud.optim.callcentersolution.core.common.utility.ToStringHelper;

/**
 * 検索結果 1 行分のデータ.
 */
public class SearchResult implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	/** Knowledge */
	private KnowledgeSearchResult knowledge;

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString() { return ToStringHelper.toString( this ); }

	/**
	 * knowledge 取得.
	 *
	 * @return knowledge
	 */
	public KnowledgeSearchResult getKnowledge() { return knowledge; }

	/**
	 * knowledge 設定.
	 *
	 * @param knowledge knowledge に設定する値.
	 */
	public void setKnowledge( KnowledgeSearchResult knowledge ) { this.knowledge = knowledge; }
}
