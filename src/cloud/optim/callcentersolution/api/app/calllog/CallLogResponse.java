/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CallLogResponse.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.calllog;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import cloud.optim.callcentersolution.api.entity.CallLog;
import cloud.optim.callcentersolution.api.entity.CallLogDetail;
import cloud.optim.callcentersolution.core.common.utility.ToStringHelper;
import cloud.optim.callcentersolution.core.modules.rest.RestResult;

/**
 * CallLog API レスポンスクラス.<br/>
 */
@XmlRootElement( name="restResponse" )
public class CallLogResponse implements java.io.Serializable {

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
	
	// -------------------------------------------------------------------------

	/** 音声ダウンロード用ファイルID */
	private String fileId;

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
	}

	/** 1 件返却用 */
	public static final class EditResult implements java.io.Serializable {

		/** serialVersionUID  */
		private static final long serialVersionUID = 1L;

		/** 1 エンティティ情報 */
		private CallLog callLog;

		/** 通話ログ詳細 */
		private List<CallLogDetail> callLogDetails;

		/** 通話時間合計 */
		private Long callTimeSum;

		/** 通話ログ詳細の内容を連結した文字列 */
		private String logs;

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
		public CallLog getCallLog() { return callLog; }

		/**
		 * callLog 設定.
		 *
		 * @param callLog callLog に設定する値.
		 */
		public void setCallLog( CallLog callLog ) { this.callLog = callLog; }

		/**
		 * @return callLogDetails
		 */
		public List<CallLogDetail> getCallLogDetails() {
			return callLogDetails;
		}

		/**
		 * @param callLogDetails セットする callLogDetails
		 */
		public void setCallLogDetails(List<CallLogDetail> callLogDetails) {
			this.callLogDetails = callLogDetails;
		}

		/**
		 * callTimeSum 取得.
		 *
		 * @return callTimeSum
		 */
		public Long getCallTimeSum() {
			return callTimeSum;
		}

		/**
		 * callTimeSum 設定.
		 *
		 * @param callTimeSum callTimeSum に設定する値.
		 */
		public void setCallTimeSum(Long callTimeSum) {
			this.callTimeSum = callTimeSum;
		}

		/**
		 * @return logs
		 */
		public String getLogs() {
			return logs;
		}

		/**
		 * @param logs セットする logs
		 */
		public void setLogs(String logs) {
			this.logs = logs;
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
	 * fileId 取得.
	 *
	 * @return fileId
	 */
	public String getFileId() {
		return fileId;
	}

	/**
	 * fileId 設定.
	 *
	 * @param fileId fileId に設定する値.
	 */
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}


}
