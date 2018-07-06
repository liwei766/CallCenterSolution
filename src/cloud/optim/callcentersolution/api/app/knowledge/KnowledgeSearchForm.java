/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：KnowledgeSearchForm.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.knowledge;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder ;
import org.apache.commons.lang3.builder.ToStringStyle ;

import cloud.optim.callcentersolution.core.common.utility.QueryHelper;

/**
 * Knowledge 検索フォーム.<br/>
 *
 */
public class KnowledgeSearchForm implements java.io.Serializable {

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
	/** 企業ID */
	private String companyId = null;

	/**
	 *	企業ID取得
	 *	@return companyId 企業ID
	 */
	public String getCompanyId() {
		return companyId;
	}

	/**
	 *	企業ID設定
	 *	@param companyId 企業ID
	 */
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}


	// ------------------------------------------------------------
	/** タイトル  */
	private String title = null;
	/** タイトル検索オプション項目  */
	private String titleOption = null;

	/**
	 *	タイトル取得
	 *	@return title タイトル
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 *	タイトル設定
	 *	@param title タイトル
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 *	タイトル検索オプション項目取得
	 *	@return titleOption タイトル検索オプション項目
	 */
	public String getTitleOption() {
		return this.titleOption;
	}

	/**
	 *	タイトル検索オプション項目設定
	 *	@param titleOption タイトル検索オプション項目
	 */
	public void setTitleOption(String titleOption) {
		this.titleOption = titleOption;
	}

	/**
	 *	タイトルのクエリでの使用値取得
	 *	@return titleQuery タイトルQuery
	 */
	public String getTitleQuery() {

		String query = QueryHelper.escape(this.title);
		if( this.titleOption.equals("1") ){
			return query + "%";
		}else if( this.titleOption.equals("2") ){
			return "%" + query;
		}else if( this.titleOption.equals("3") ){
			return "%" + query + "%";
		}

		return query;
	}

	// ------------------------------------------------------------
	/** タグID  */
	private  List<Long> tagId = null;

	/**
	 *	タグID取得
	 *	@return tagId タグID
	 */
	public  List<Long> getTagId() {
		return tagId;
	}

	/**
	 *	タグID件数取得
	 *	@return tagId タグID件数
	 */
	public int getTagIdCount() {
		return tagId.size();
	}

	/**
	 *	タグID設定
	 *	@param tagId タグID
	 */
	public void setTagId(List<Long>tagId) {
		this.tagId = tagId;
	}
}
