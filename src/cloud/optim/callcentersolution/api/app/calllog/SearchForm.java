/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SearchForm.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.calllog;

import cloud.optim.callcentersolution.core.common.utility.ToStringHelper;
import cloud.optim.callcentersolution.core.modules.rest.SortForm;

/**
 * 検索条件.
 */
public class SearchForm implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	/** 取得条件 */
	private SortForm sortForm;

	/** 検索条件 */
	private CallLogSearchForm callLog;

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString() { return ToStringHelper.toString( this ); }

	/**
	 * sortForm 取得.
	 *
	 * @return sortForm
	 */
	public SortForm getSortForm() {

		return sortForm;
	}

	/**
	 * sortForm 設定.
	 *
	 * @param sortForm sortForm に設定する値.
	 */
	public void setSortForm( SortForm sortForm ) {

		this.sortForm = sortForm;
	}

	/**
	 * callLog 取得.
	 *
	 * @return callLog
	 */
	public CallLogSearchForm getCallLog() { return callLog; }

	/**
	 * callLog 設定.
	 *
	 * @param callLog callLog に設定する値.
	 */
	public void setCallLog( CallLogSearchForm callLog ) { this.callLog = callLog; }
}
