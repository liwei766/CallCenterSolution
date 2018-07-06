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
package cloud.optim.callcentersolution.api.app.calllog;

import cloud.optim.callcentersolution.core.common.utility.ToStringHelper;

/**
 * 検索結果 1 行分のデータ.
 */
public class SearchResult implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	/** CallLog */
	private CallLogSearchResult callLog;

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString() { return ToStringHelper.toString( this ); }

	/**
	 * callLog 取得.
	 *
	 * @return callLog
	 */
	public CallLogSearchResult getCallLog() { return callLog; }

	/**
	 * callLog 設定.
	 *
	 * @param callLog callLog に設定する値.
	 */
	public void setCallLog( CallLogSearchResult callLog ) { this.callLog = callLog; }
}
