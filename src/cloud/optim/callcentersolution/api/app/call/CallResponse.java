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
package cloud.optim.callcentersolution.api.app.call;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import cloud.optim.callcentersolution.core.common.utility.ToStringHelper;
import cloud.optim.callcentersolution.core.modules.rest.RestResult;

/**
 * CallLog API レスポンスクラス.<br/>
 */
/**
 * @author raifuyor
 *
 */
@XmlRootElement( name="restResponse" )
public class CallResponse implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	/** 処理結果 */
	private List<RestResult> resultList = new ArrayList<RestResult>();


	/** リカイアス認証トークン */
	private String token;

	/** リカイアスUUID */
	private String uuid;

	/** 通話ログID */
	private Long callLogId;

	/** 問い合わせ番号 */
	private Long callLogNo;

	/** 通話者ログID */
	private Long callUserId;

	/** 利用時間ID */
	private Long useTimeId;

	/** 解析結果 */
	private List<List<String>> analyzeResult;


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
	 * token 取得.
	 *
	 * @return token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * token 設定.
	 *
	 * @param token token に設定する値.
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * uuid 取得.
	 *
	 * @return uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * uuid 設定.
	 *
	 * @param uuid uuid に設定する値.
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * callLogId 取得.
	 *
	 * @return callLogId
	 */
	public Long getCallLogId() {
		return callLogId;
	}

	/**
	 * callLogId 設定.
	 *
	 * @param callLogId callLogId に設定する値.
	 */
	public void setCallLogId(Long callLogId) {
		this.callLogId = callLogId;
	}

	/**
	 * callLogNo 取得.
	 *
	 * @return callLogNo
	 */
	public Long getCallLogNo() {
		return callLogNo;
	}

	/**
	 * callLogNo 設定.
	 *
	 * @param callLogNo callLogNo に設定する値.
	 */
	public void setCallLogNo(Long callLogNo) {
		this.callLogNo = callLogNo;
	}

	/**
	 * callUserId 取得.
	 *
	 * @return callUserId
	 */
	public Long getCallUserId() {
		return callUserId;
	}

	/**
	 * callUserId 設定.
	 *
	 * @param callUserId callUserId に設定する値.
	 */
	public void setCallUserId(Long callUserId) {
		this.callUserId = callUserId;
	}

	/**
	 * useTimeId 取得.
	 *
	 * @return useTimeId
	 */
	public Long getUseTimeId() {
		return useTimeId;
	}

	/**
	 * useTimeId 設定.
	 *
	 * @param useTimeId useTimeId に設定する値.
	 */
	public void setUseTimeId(Long useTimeId) {
		this.useTimeId = useTimeId;
	}

	/**
	 * analyzeResult 取得.
	 *
	 * @return analyzeResult
	 */
	public List<List<String>> getAnalyzeResult() {
		return analyzeResult;
	}

	/**
	 * analyzeResult 設定.
	 *
	 * @param analyzeResult analyzeResult に設定する値.
	 */
	public void setAnalyzeResult(List<List<String>> analyzeResult) {
		this.analyzeResult = analyzeResult;
	}
}
