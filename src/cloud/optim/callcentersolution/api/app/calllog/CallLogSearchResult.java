/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CallLogSearchResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.calllog;


import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement ;

import org.apache.commons.beanutils.PropertyUtils ;

import cloud.optim.callcentersolution.api.entity.CallUser;

/**
 * CallLog 検索結果.<br/>
 */
@XmlRootElement
public class CallLogSearchResult implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;


	/**
	 * 文字列表現への変換
	 *
	 * @return このインスタンスの文字列表現
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
		sb.append("callUserId").append("='").append(getCallUserId()).append("' ");
		sb.append("callLogId").append("='").append(getCallLogId()).append("' ");
		sb.append("companyId").append("='").append(getCompanyId()).append("' ");
		sb.append("callLogNo").append("='").append(getCallLogNo()).append("' ");
		sb.append("userId").append("='").append(getUserId()).append("' ");
		sb.append("userName").append("='").append(getUserName()).append("' ");
		sb.append("startDate").append("='").append(getStartDate()).append("' ");
		sb.append("endDate").append("='").append(getEndDate()).append("' ");
		sb.append("createDate").append("='").append(getCreateDate()).append("' ");
		sb.append("createUserId").append("='").append(getCreateUserId()).append("' ");
		sb.append("createUserName").append("='").append(getCreateUserName()).append("' ");
		sb.append("updateDate").append("='").append(getUpdateDate()).append("' ");
		sb.append("updateUserId").append("='").append(getUpdateUserId()).append("' ");
		sb.append("updateUserName").append("='").append(getUpdateUserName()).append("' ");
		sb.append("callTime").append("='").append(getCallTime()).append("' ");
		sb.append("]");
		return sb.toString();
	}

	/**
	 * エンティティオブジェクトへの変換
	 *
	 * @return 新たに作成した CallLog オブジェクト
	 */
	public CallUser toEntity() {
		CallUser ret = new CallUser() ;

		try {
			PropertyUtils.copyProperties( ret, this ) ;
		}
		catch ( Exception e ) {
			ret = null ;
		}

		return ret ;
	}

	/**
	 * 通話者ログ ID
	 */
	private Long callUserId;

	/**
	 * 通話ログ ID
	 */
	private Long callLogId;

	/**
	 * 更新日時
	 */
	private Date updateDate;

	/**
	 * 企業 ID
	 */
	private String companyId;

	/**
	 * 問合わせ番号
	 */
	private Long callLogNo;

	/**
	 * ユーザ ID
	 */
	private String userId;

	/**
	 * ユーザ名
	 */
	private String userName;

	/**
	 * 開始日時
	 */
	private Date startDate;

	/**
	 * 終了日時
	 */
	private Date endDate;

	/**
	 * 作成日時
	 */
	private Date createDate;

	/**
	 * 作成ユーザ ID
	 */
	private String createUserId;

	/**
	 * 作成ユーザ名
	 */
	private String createUserName;

	/**
	 * 更新ユーザ ID
	 */
	private String updateUserId;

	/**
	 * 更新ユーザ名
	 */
	private String updateUserName;

	/**
	 * 通話時間
	 */
	private Long callTime;


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
	 * @return updateDate
	 */
	public Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * @param updateDate セットする updateDate
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * @return companyId
	 */
	public String getCompanyId() {
		return companyId;
	}

	/**
	 * @param companyId セットする companyId
	 */
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	/**
	 * @return callLogNo
	 */
	public Long getCallLogNo() {
		return this.callLogNo;
	}

	/**
	 * @param callLogNo セットする callLogNo  
	 */
	public void setCallLogNo(Long callLogNo) {
		this.callLogNo = callLogNo;
	}

	/**
	 * @return userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId セットする userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName セットする userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate セットする startDate
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate セットする endDate
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return createDate
	 */
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * @param createDate セットする createDate
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * @return createUserId
	 */
	public String getCreateUserId() {
		return createUserId;
	}

	/**
	 * @param createUserId セットする createUserId
	 */
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	/**
	 * @return createUserName
	 */
	public String getCreateUserName() {
		return createUserName;
	}

	/**
	 * @param createUserName セットする createUserName
	 */
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	/**
	 * @return updateUserId
	 */
	public String getUpdateUserId() {
		return updateUserId;
	}

	/**
	 * @param updateUserId セットする updateUserId
	 */
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	/**
	 * @return updateUserName
	 */
	public String getUpdateUserName() {
		return updateUserName;
	}

	/**
	 * @param updateUserName セットする updateUserName
	 */
	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

	public Long getCallUserId() {
		return callUserId;
	}

	public void setCallUserId(Long callUserId) {
		this.callUserId = callUserId;
	}

	/**
	 * @return callTime
	 */
	public Long getCallTime() {
		return callTime;
	}

	/**
	 * @param callTime セットする callTime
	 */
	public void setCallTime(Long callTime) {
		this.callTime = callTime;
	}


}