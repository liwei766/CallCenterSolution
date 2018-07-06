/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：KnowledgeResponse.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.knowledge;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import cloud.optim.callcentersolution.api.entity.Knowledge;
import cloud.optim.callcentersolution.api.entity.Manual;
import cloud.optim.callcentersolution.api.entity.Reference;
import cloud.optim.callcentersolution.api.entity.Tag;
import cloud.optim.callcentersolution.core.common.utility.ToStringHelper;
import cloud.optim.callcentersolution.core.modules.rest.RestResult;

/**
 * Knowledge API レスポンスクラス.<br/>
 */
@XmlRootElement( name="restResponse" )
public class KnowledgeResponse implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	/** 処理結果 */
	private List<RestResult> resultList = new ArrayList<RestResult>();

	// -------------------------------------------------------------------------

	/** 検索結果 */
	private List<SearchResult> searchResultList;

	// -------------------------------------------------------------------------

	/** 1 エンティティ情報 */
	private EditResult editResult;

	// -------------------------------------------------------------------------

	/** 一括処理結果 */
	private List<BulkResult> bulkResultList;

	/** CSV取込データ件数 */
	private Integer dataCount;

	/** CSV取込エラー件数 */
	private Integer errorCount;

	// -------------------------------------------------------------------------

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString()
	{
		return ToStringHelper.toString( this );
	}

	// -------------------------------------------------------------------------
	// 処理結果を扱う処理
	// -------------------------------------------------------------------------

	/**
	 * 処理結果数を取得する.
	 *
	 * @return 登録されている処理結果数
	 */
	@XmlTransient
	@JsonIgnore
	public int getResultLength() {

		if ( resultList == null ) return 0;

		return resultList.size();
	}

	/**
	 * 処理結果を登録する.
	 *
	 * @param result 登録する処理結果
	 */
	public void addResult( RestResult result ) {

		if ( resultList == null )
		{
			resultList = new ArrayList<RestResult>();
		}

		resultList.add( result );
	}

	/**
	 * 処理結果を登録する.
	 *
	 * @param result 登録する処理結果
	 */
	public void setResult( RestResult result ) {

		if ( resultList == null )
		{
			resultList = new ArrayList<RestResult>();
		}

		resultList.clear();
		resultList.add( result );
	}

	// -------------------------------------------------------------------------
	// 内部クラス
	// -------------------------------------------------------------------------

	/** 一括処理結果 */
	public static final class BulkResult extends SearchResult {

		/** serialVersionUID  */
		private static final long serialVersionUID = 1L;

		/** 処理結果 */
		private List<RestResult> resultList = new ArrayList<RestResult>();

		/** データ番号 */
		private Integer number;

		/**
		 * 処理結果を登録する.
		 *
		 * @param result 登録する処理結果
		 */
		public void addResult( RestResult result ) {

			if ( resultList == null ) {

				resultList = new ArrayList<RestResult>();
			}

			resultList.add( result );
		}

		/**
		 * 処理結果を登録する.
		 *
		 * @param result 登録する処理結果
		 */
		public void setResult( RestResult result ) {

			if ( resultList == null )
			{
				resultList = new ArrayList<RestResult>();
			}

			resultList.clear();
			resultList.add( result );
		}

		/**
		 * resultList 取得.
		 *
		 * @return resultList
		 */
		@XmlElementWrapper( name="resultList" )
		@XmlElement( name="result" )
		@JsonProperty( "resultList" )
		public List<RestResult> getResultList() {

			return resultList;
		}

		/**
		 * resultList 設定.
		 *
		 * @param resultList resultList に設定する値.
		 */
		public void setResultList( List<RestResult> resultList ) {

			this.resultList = resultList;
		}

		/**
		 * number 取得.
		 *
		 * @return number
		 */
		public Integer getNumber() {
			return number;
		}

		/**
		 * number 設定.
		 *
		 * @param number number に設定する値.
		 */
		public void setNumber(Integer number) {
			this.number = number;
		}


	}

	/** 1 件返却用 */
	public static final class EditResult implements java.io.Serializable {

		/** serialVersionUID  */
		private static final long serialVersionUID = 1L;

		/** 1 エンティティ情報 */
		private Knowledge knowledge;

		private List<Tag> tag;

		private List<Reference> reference;

		private List<Manual> manual;

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
		public Knowledge getKnowledge() { return knowledge; }

		/**
		 * knowledge 設定.
		 *
		 * @param knowledge knowledge に設定する値.
		 */
		public void setKnowledge( Knowledge knowledge ) { this.knowledge = knowledge; }

		/**
		 * tag 取得.
		 *
		 * @return tag
		 */
		public List<Tag> getTag() { return tag; }

		/**
		 * tag 設定.
		 *
		 * @param tag tag に設定する値.
		 */
		public void setTag( List<Tag> tag ) { this.tag = tag; }

		/**
		 * @return reference
		 */
		public List<Reference> getReference() {
			return reference;
		}

		/**
		 * @param reference セットする reference
		 */
		public void setReference(List<Reference> reference) {
			this.reference = reference;
		}

		/**
		 * @return manual
		 */
		public List<Manual> getManual() {
			return manual;
		}

		/**
		 * @param manual セットする manual
		 */
		public void setManual(List<Manual> manual) {
			this.manual = manual;
		}
	}

	// -------------------------------------------------------------------------
	// アクセサメソッド
	// -------------------------------------------------------------------------

	/**
	 * resultList 取得.
	 *
	 * @return resultList
	 */
	@XmlElementWrapper( name="resultList" )
	@XmlElement( name="result" )
	@JsonProperty( "resultList" )
	public List<RestResult> getResultList() {

		return resultList;
	}

	/**
	 * resultList 設定.
	 *
	 * @param resultList resultList に設定する値.
	 */
	public void setResultList( List<RestResult> resultList ) {

		this.resultList = resultList;
	}

	/**
	 * searchResultList 取得.
	 *
	 * @return searchResultList
	 */
	@XmlElementWrapper( name="searchResultList" )
	@XmlElement( name="searchResult" )
	@JsonProperty( "searchResultList" )
	public List<SearchResult> getSearchResultList() {

		return searchResultList;
	}

	/**
	 * searchResultList 設定.
	 *
	 * @param searchResultList searchResultList に設定する値.
	 */
	public void setSearchResultList( List<SearchResult> searchResultList ) {

		this.searchResultList = searchResultList;
	}

	/**
	 * editResult 取得.
	 *
	 * @return editResult
	 */
	public EditResult getEditResult() {

		return editResult;
	}

	/**
	 * editResult 設定.
	 *
	 * @param editResult editResult に設定する値.
	 */
	public void setEditResult( EditResult editResult ) {

		this.editResult = editResult;
	}

	/**
	 * bulkResultList 取得.
	 *
	 * @return bulkResultList
	 */
	@XmlElementWrapper( name="bulkResultList" )
	@XmlElement( name="bulkResult" )
	@JsonProperty( "bulkResultList" )
	public List<BulkResult> getBulkResultList() {

		return bulkResultList;
	}

	/**
	 * bulkResultList 設定.
	 *
	 * @param bulkResultList bulkResultList に設定する値.
	 */
	public void setBulkResultList( List<BulkResult> bulkResultList ) {

		this.bulkResultList = bulkResultList;
	}

	/**
	 * dataCount 取得.
	 *
	 * @return dataCount
	 */
	public Integer getDataCount() {
		return dataCount;
	}

	/**
	 * dataCount 設定.
	 *
	 * @param dataCount dataCount に設定する値.
	 */
	public void setDataCount(Integer dataCount) {
		this.dataCount = dataCount;
	}

	/**
	 * errorCount 取得.
	 *
	 * @return errorCount
	 */
	public Integer getErrorCount() {
		return errorCount;
	}

	/**
	 * errorCount 設定.
	 *
	 * @param errorCount errorCount に設定する値.
	 */
	public void setErrorCount(Integer errorCount) {
		this.errorCount = errorCount;
	}
}
