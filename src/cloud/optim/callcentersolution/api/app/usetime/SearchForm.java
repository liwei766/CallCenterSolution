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
package cloud.optim.callcentersolution.api.app.usetime;

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
	private UseTimeSearchForm useTime;


	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString() { return ToStringHelper.toString( this ); }


	public SortForm getSortForm() {
		return sortForm;
	}


	public void setSortForm(SortForm sortForm) {
		this.sortForm = sortForm;
	}


	public UseTimeSearchForm getUseTime() {
		return useTime;
	}


	public void setUseTime(UseTimeSearchForm useTime) {
		this.useTime = useTime;
	}



}
