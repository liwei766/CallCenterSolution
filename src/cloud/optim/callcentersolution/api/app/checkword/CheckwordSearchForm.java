/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CheckwordSearchForm.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.checkword;

import java.util.ArrayList ;
import java.util.List ;

import org.apache.commons.lang3.builder.ToStringBuilder ;
import org.apache.commons.lang3.builder.ToStringStyle ;

import cloud.optim.callcentersolution.core.common.utility.QueryHelper;

/**
 * Checkword 検索フォーム.<br/>
 *
 */
public class CheckwordSearchForm implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;


	/**
	 * 文字列表現への変換
	 *
	 * @return このインスタンスの文字列表現
	 */
	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(
			this, ToStringStyle.DEFAULT_STYLE ) ;
	}


	// ------------------------------------------------------------
	// 検索条件
	// ------------------------------------------------------------

	// ------------------------------------------------------------
	/** チェックワードID  */
	private String checkwordId = null;

	/**
	 *	チェックワードID取得
	 *	@return checkwordId チェックワードID
	 */
	public String getCheckwordId() {
		return this.checkwordId;
	}

	/**
	 *	チェックワードID設定
	 *	@param checkwordId チェックワードID
	 */
	public void setCheckwordId(String checkwordId) {
		this.checkwordId = checkwordId;
	}


	/** チェックワードID検索範囲（開始）  */
	private String checkwordIdFrom = null;
	/** チェックワードID検索範囲（終了）  */
	private String checkwordIdTo = null;
	/** チェックワードID未登録検索設定項目  */
	private Boolean checkwordIdNull = null;

	/**
	 *	チェックワードID検索範囲（開始）取得
	 *	@return checkwordIdFrom チェックワードID検索範囲（開始）
	 */
	public String getCheckwordIdFrom() {
		return this.checkwordIdFrom;
	}

	/**
	 *	チェックワードID検索範囲（開始）設定
	 *	@param checkwordIdFrom チェックワードID検索範囲（開始）
	 */
	public void setCheckwordIdFrom(String checkwordIdFrom) {
		this.checkwordIdFrom = checkwordIdFrom;
	}

	/**
	 *	チェックワードID検索範囲（終了）取得
	 *	@return checkwordIdTo チェックワードID検索範囲（終了）
	 */
	public String getCheckwordIdTo() {
		return this.checkwordIdTo;
	}

	/**
	 *	チェックワードID検索範囲（終了）設定
	 *	@param checkwordIdTo チェックワードID検索範囲（終了）
	 */
	public void setCheckwordIdTo(String checkwordIdTo) {
		this.checkwordIdTo = checkwordIdTo;
	}

	/**
	 *	チェックワードID未登録検索設定項目取得
	 *	@return checkwordIdNull チェックワードID未登録検索設定項目
	 */
	public Boolean getCheckwordIdNull() {
		return this.checkwordIdNull;
	}

	/**
	 *	チェックワードID未登録検索設定項目設定
	 *	@param checkwordIdNull チェックワードID未登録検索設定項目
	 */
	public void setCheckwordIdNull(Boolean checkwordIdNull) {
		this.checkwordIdNull = checkwordIdNull;
	}


	// ------------------------------------------------------------
	/** 企業 ID  */
	private String companyId = null;
	/** 企業 ID未登録検索設定項目  */
	private Boolean companyIdNull = null;
	/** 企業 ID検索オプション項目  */
	private String companyIdOption = null;

	/**
	 *	企業 ID取得
	 *	@return companyId 企業 ID
	 */
	public String getCompanyId() {
		return this.companyId;
	}

	/**
	 *	企業 ID設定
	 *	@param companyId 企業 ID
	 */
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	/**
	 *	企業 ID未登録検索設定項目取得
	 *	@return companyId 企業 ID
	 */
	public Boolean getCompanyIdNull() {
		return this.companyIdNull;
	}

	/**
	 *	企業 ID未登録検索設定項目設定
	 *	@param companyIdNull 企業 ID未登録検索設定項目
	 */
	public void setCompanyIdNull(Boolean companyIdNull) {
		this.companyIdNull = companyIdNull;
	}

	/**
	 *	企業 ID検索オプション項目取得
	 *	@return companyIdOption 企業 ID検索オプション項目
	 */
	public String getCompanyIdOption() {
		return this.companyIdOption;
	}

	/**
	 *	企業 ID検索オプション項目設定
	 *	@param companyIdOption 企業 ID検索オプション項目
	 */
	public void setCompanyIdOption(String companyIdOption) {
		this.companyIdOption = companyIdOption;
	}

	/**
	 *	企業 IDのクエリでの使用値取得
	 *	@return companyIdQuery 企業 IDQuery
	 */
	public String getCompanyIdQuery() {

		String query = QueryHelper.escape(this.companyId);
		if( this.companyIdOption.equals("1") ){
			return query + "%";
		}else if( this.companyIdOption.equals("2") ){
			return "%" + query;
		}else if( this.companyIdOption.equals("3") ){
			return "%" + query + "%";
		}

		return query;
	}


	// ------------------------------------------------------------
	/** チェックワード内容  */
	private String checkword = null;
	/** チェックワード内容未登録検索設定項目  */
	private Boolean checkwordNull = null;
	/** チェックワード内容検索オプション項目  */
	private String checkwordOption = null;

	/**
	 *	チェックワード内容取得
	 *	@return checkword チェックワード内容
	 */
	public String getCheckword() {
		return this.checkword;
	}

	/**
	 *	チェックワード内容設定
	 *	@param checkword チェックワード内容
	 */
	public void setCheckword(String checkword) {
		this.checkword = checkword;
	}

	/**
	 *	チェックワード内容未登録検索設定項目取得
	 *	@return checkword チェックワード内容
	 */
	public Boolean getCheckwordNull() {
		return this.checkwordNull;
	}

	/**
	 *	チェックワード内容未登録検索設定項目設定
	 *	@param checkwordNull チェックワード内容未登録検索設定項目
	 */
	public void setCheckwordNull(Boolean checkwordNull) {
		this.checkwordNull = checkwordNull;
	}

	/**
	 *	チェックワード内容検索オプション項目取得
	 *	@return checkwordOption チェックワード内容検索オプション項目
	 */
	public String getCheckwordOption() {
		return this.checkwordOption;
	}

	/**
	 *	チェックワード内容検索オプション項目設定
	 *	@param checkwordOption チェックワード内容検索オプション項目
	 */
	public void setCheckwordOption(String checkwordOption) {
		this.checkwordOption = checkwordOption;
	}

	/**
	 *	チェックワード内容のクエリでの使用値取得
	 *	@return checkwordQuery チェックワード内容Query
	 */
	public String getCheckwordQuery() {

		String query = QueryHelper.escape(this.checkword);
		if( this.checkwordOption.equals("1") ){
			return query + "%";
		}else if( this.checkwordOption.equals("2") ){
			return "%" + query;
		}else if( this.checkwordOption.equals("3") ){
			return "%" + query + "%";
		}

		return query;
	}


	// ------------------------------------------------------------
	/** ワードカラー名称  */
	private String colorTheme = null;
	/** ワードカラー名称未登録検索設定項目  */
	private Boolean colorThemeNull = null;
	/** ワードカラー名称検索オプション項目  */
	private String colorThemeOption = null;

	/**
	 *	ワードカラー名称取得
	 *	@return colorTheme ワードカラー名称
	 */
	public String getColorTheme() {
		return this.colorTheme;
	}

	/**
	 *	ワードカラー名称設定
	 *	@param colorTheme ワードカラー名称
	 */
	public void setColorTheme(String colorTheme) {
		this.colorTheme = colorTheme;
	}

	/**
	 *	ワードカラー名称未登録検索設定項目取得
	 *	@return colorTheme ワードカラー名称
	 */
	public Boolean getColorThemeNull() {
		return this.colorThemeNull;
	}

	/**
	 *	ワードカラー名称未登録検索設定項目設定
	 *	@param colorThemeNull ワードカラー名称未登録検索設定項目
	 */
	public void setColorThemeNull(Boolean colorThemeNull) {
		this.colorThemeNull = colorThemeNull;
	}

	/**
	 *	ワードカラー名称検索オプション項目取得
	 *	@return colorThemeOption ワードカラー名称検索オプション項目
	 */
	public String getColorThemeOption() {
		return this.colorThemeOption;
	}

	/**
	 *	ワードカラー名称検索オプション項目設定
	 *	@param colorThemeOption ワードカラー名称検索オプション項目
	 */
	public void setColorThemeOption(String colorThemeOption) {
		this.colorThemeOption = colorThemeOption;
	}

	/**
	 *	ワードカラー名称のクエリでの使用値取得
	 *	@return colorThemeQuery ワードカラー名称Query
	 */
	public String getColorThemeQuery() {

		String query = QueryHelper.escape(this.colorTheme);
		if( this.colorThemeOption.equals("1") ){
			return query + "%";
		}else if( this.colorThemeOption.equals("2") ){
			return "%" + query;
		}else if( this.colorThemeOption.equals("3") ){
			return "%" + query + "%";
		}

		return query;
	}



}





