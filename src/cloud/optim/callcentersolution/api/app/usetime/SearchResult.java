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
package cloud.optim.callcentersolution.api.app.usetime;

import cloud.optim.callcentersolution.core.common.utility.ToStringHelper;

/**
 * 検索結果 1 行分のデータ.
 */
public class SearchResult implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	/** 企業ID */
	private String companyId;

	/** 利用時間 */
	private Long useTime;

	/** 利用回数 */
	private Long useCount;

	/** 年月 */
	private String yearMonth;


	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString() { return ToStringHelper.toString( this ); }


	public String getCompanyId() {
		return companyId;
	}


	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}


	public Long getUseTime() {
		return useTime;
	}


	public void setUseTime(Long useTime) {
		this.useTime = useTime;
	}


	public String getYearMonth() {
		return yearMonth;
	}


	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}


	public Long getUseCount() {
		return useCount;
	}


	public void setUseCount(Long useCount) {
		this.useCount = useCount;
	}



}
