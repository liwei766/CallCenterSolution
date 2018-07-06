/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：UseTimeSearchForm.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.usetime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * CallLog 検索フォーム.<br/>
 *
 */
public class UseTimeSearchForm implements java.io.Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** 年 */
	private String year = null;

	/** 月 */
	private String month = null;

	private String startDate = null;

	private String endDate = null;

	private String companyId = null;

	/** 代理店企業ID. */
	private String agencyCompanyId = null;


	/**
	 * 文字列表現への変換
	 *
	 * @return このインスタンスの文字列表現
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

	// ------------------------------------------------------------
	// 検索条件
	// ------------------------------------------------------------

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public void setStartEnd() {

		// DateFormat
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		// カレンダークラスのインスタンスを取得
		Calendar cal = Calendar.getInstance();

		cal.set(Integer.parseInt(year), Integer.parseInt(month), 1);

		this.endDate = df.format(cal.getTime());

		// 1カ月を加算
		cal.add(Calendar.MONTH, -1);

		this.startDate = df.format(cal.getTime());
	}

	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	/**
	 * 代理店企業ID取得.
	 *
	 * @return agencyCompanyId 代理店企業ID
	 */
	public String getAgencyCompanyId() {
		return this.agencyCompanyId;
	}

	/**
	 * 代理店企業ID設定.
	 *
	 * @param agencyCompanyId
	 *            代理店企業ID
	 */
	public void setAgencyCompanyId(String agencyCompanyId) {
		this.agencyCompanyId = agencyCompanyId;
	}

}
