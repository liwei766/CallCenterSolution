/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CallLogRequest.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.call;

import javax.xml.bind.annotation.XmlRootElement;

import cloud.optim.callcentersolution.core.common.utility.ToStringHelper;

/**
 * CallLog API リクエストクラス.<br/>
 */
@XmlRootElement( name="restRequest" )
public class CallRequest implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	private String token;

	private String uuid;

	private Integer voiceId;

	private Integer energyThreshold;

	private Integer modelId;

	/** 通話ログID */
	private Long callLogId;

	/** 通話者ログID */
	private Long callUserId;

	/** 利用時間ID */
	private Long useTimeId;


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

	/**
	 * @return token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token セットする token
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid セットする uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return voiceId
	 */
	public Integer getVoiceId() {
		return voiceId;
	}

	/**
	 * @param voiceId セットする voiceId
	 */
	public void setVoiceId(Integer voiceId) {
		this.voiceId = voiceId;
	}

	/**
	 * @return energyThreshold
	 */
	public Integer getEnergyThreshold() {
		return energyThreshold;
	}

	/**
	 * @param energyThreshold セットする energyThreshold
	 */
	public void setEnergyThreshold(Integer energyThreshold) {
		this.energyThreshold = energyThreshold;
	}

	/**
	 * @return modelId
	 */
	public Integer getModelId() {
		return modelId;
	}

	/**
	 * @param modelId セットする modelId
	 */
	public void setModelId(Integer modelId) {
		this.modelId = modelId;
	}

	/**
	 * @return callLogId
	 */
	public Long getCallLogId() {
		return callLogId;
	}

	/**
	 * @param callLogId セットする callLogId
	 */
	public void setCallLogId(Long callLogId) {
		this.callLogId = callLogId;
	}

	/**
	 * @return callUserId
	 */
	public Long getCallUserId() {
		return callUserId;
	}

	/**
	 * @param callUserId セットする callUserId
	 */
	public void setCallUserId(Long callUserId) {
		this.callUserId = callUserId;
	}

	/**
	 * @return useTimeId
	 */
	public Long getUseTimeId() {
		return useTimeId;
	}

	/**
	 * @param useTimeId セットする useTimeId
	 */
	public void setUseTimeId(Long useTimeId) {
		this.useTimeId = useTimeId;
	}
}
