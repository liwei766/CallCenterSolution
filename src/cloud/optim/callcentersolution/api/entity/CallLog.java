/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CallLog.java
 */

package cloud.optim.callcentersolution.api.entity;
// Generated by Hibernate Tools 3.2.2.CR1


import java.util.Date;

/**

 */
public class CallLog  implements java.io.Serializable {


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
	 * 問い合わせ番号
	 */
	private Long callLogNo;

	/**
	 * ステータス
	 */
	private String status;

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
	 * 削除日時
	 */
	private Date deleteDate;


	/**
	 * CallLog デフォルトコンストラクター
	 */
	public CallLog() {
	}

	/**
	 * CallLog 最小コンストラクター
	 * @param companyId 企業 ID
	 * @param callLogNo 問い合わせ番号
	 * @param status ステータス
	 * @param startDate 開始日時
	 * @param endDate 終了日時
	 * @param createDate 作成日時
	 * @param createUserId 作成ユーザ ID
	 * @param createUserName 作成ユーザ名
	 * @param updateUserId 更新ユーザ ID
	 * @param updateUserName 更新ユーザ名
	 */
	public CallLog(String companyId, Long callLogNo, String status, Date startDate, Date endDate, Date createDate, String createUserId, String createUserName, String updateUserId, String updateUserName) {
		this.companyId = companyId;
		this.callLogNo = callLogNo;
		this.status = status;
		this.startDate = startDate;
		this.endDate = endDate;
		this.createDate = createDate;
		this.createUserId = createUserId;
		this.createUserName = createUserName;
		this.updateUserId = updateUserId;
		this.updateUserName = updateUserName;
    }
	/**
	 * CallLog フルコンストラクター
	 * @param companyId 企業 ID
	 * @param callLogNo 問い合わせ番号
	 * @param status ステータス
	 * @param startDate 開始日時
	 * @param endDate 終了日時
	 * @param createDate 作成日時
	 * @param createUserId 作成ユーザ ID
	 * @param createUserName 作成ユーザ名
	 * @param updateUserId 更新ユーザ ID
	 * @param updateUserName 更新ユーザ名
	 * @param deleteDate 削除日時
	 */
	public CallLog(String companyId, Long callLogNo, String status, Date startDate, Date endDate, Date createDate, String createUserId, String createUserName, String updateUserId, String updateUserName, Date deleteDate) {
		this.companyId = companyId;
		this.callLogNo = callLogNo;
		this.status = status;
		this.startDate = startDate;
		this.endDate = endDate;
		this.createDate = createDate;
		this.createUserId = createUserId;
		this.createUserName = createUserName;
		this.updateUserId = updateUserId;
		this.updateUserName = updateUserName;
		this.deleteDate = deleteDate;
	}

	// * 通話ログ ID */
	/**
	 * @return callLogId (通話ログ ID)
	 */
	public Long getCallLogId() {
		return this.callLogId;
	}

	/**
	 * @param callLogId 通話ログ ID
	 */
	public void setCallLogId(Long callLogId) {
		this.callLogId = callLogId;
	}

	// * 更新日時 */
	/**
	 * @return updateDate (更新日時)
	 */
	public Date getUpdateDate() {
		return this.updateDate;
	}

	/**
	 * @param updateDate 更新日時
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	// * 企業 ID */
	/**
	 * @return companyId (企業 ID)
	 */
	public String getCompanyId() {
		return this.companyId;
	}

	/**
	 * @param companyId 企業 ID
	 */
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	// * 問い合わせ番号 */
	/**
	 * @return callLogNo (問い合わせ番号)
	 */
	public Long getCallLogNo() {
		return this.callLogNo;
	}

	/**
	 * @param callLogNo 問い合わせ番号
	 */
	public void setCallLogNo(Long callLogNo) {
		this.callLogNo = callLogNo;
	}

	// * ステータス */
	/**
	 * @return status (ステータス)
	 */
	public String getStatus() {
		return this.status;
	}

	/**
	 * @param status ステータス
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	// * 開始日時 */
	/**
	 * @return startDate (開始日時)
	 */
	public Date getStartDate() {
		return this.startDate;
	}

	/**
	 * @param startDate 開始日時
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	// * 終了日時 */
	/**
	 * @return endDate (終了日時)
	 */
	public Date getEndDate() {
		return this.endDate;
	}

	/**
	 * @param endDate 終了日時
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	// * 作成日時 */
	/**
	 * @return createDate (作成日時)
	 */
	public Date getCreateDate() {
		return this.createDate;
	}

	/**
	 * @param createDate 作成日時
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	// * 作成ユーザ ID */
	/**
	 * @return createUserId (作成ユーザ ID)
	 */
	public String getCreateUserId() {
		return this.createUserId;
	}

	/**
	 * @param createUserId 作成ユーザ ID
	 */
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	// * 作成ユーザ名 */
	/**
	 * @return createUserName (作成ユーザ名)
	 */
	public String getCreateUserName() {
		return this.createUserName;
	}

	/**
	 * @param createUserName 作成ユーザ名
	 */
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	// * 更新ユーザ ID */
	/**
	 * @return updateUserId (更新ユーザ ID)
	 */
	public String getUpdateUserId() {
		return this.updateUserId;
	}

	/**
	 * @param updateUserId 更新ユーザ ID
	 */
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	// * 更新ユーザ名 */
	/**
	 * @return updateUserName (更新ユーザ名)
	 */
	public String getUpdateUserName() {
		return this.updateUserName;
	}

	/**
	 * @param updateUserName 更新ユーザ名
	 */
	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

	// * 削除日時 */
	/**
	 * @return deleteDate (削除日時)
	 */
	public Date getDeleteDate() {
		return this.deleteDate;
	}

	/**
	 * @param deleteDate 削除日時
	 */
	public void setDeleteDate(Date deleteDate) {
		this.deleteDate = deleteDate;
	}


	/**
	 * toString
	 * @return String
	*/
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
		buffer.append("callLogId").append("='").append(getCallLogId()).append("' ");
		buffer.append("updateDate").append("='").append(getUpdateDate()).append("' ");
		buffer.append("companyId").append("='").append(getCompanyId()).append("' ");
		buffer.append("callLogNo").append("='").append(getCallLogNo()).append("' ");
		buffer.append("status").append("='").append(getStatus()).append("' ");
		buffer.append("startDate").append("='").append(getStartDate()).append("' ");
		buffer.append("endDate").append("='").append(getEndDate()).append("' ");
		buffer.append("createDate").append("='").append(getCreateDate()).append("' ");
		buffer.append("createUserId").append("='").append(getCreateUserId()).append("' ");
		buffer.append("updateUserId").append("='").append(getUpdateUserId()).append("' ");
		buffer.append("]");

		return buffer.toString();
	}

	@Override
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( (other == null ) ) return false;
		if ( !(other instanceof CallLog) ) return false;
		CallLog castOther = ( CallLog ) other;

		return ( (this.getCallLogId()==castOther.getCallLogId()) || ( this.getCallLogId()!=null && castOther.getCallLogId()!=null && this.getCallLogId().equals(castOther.getCallLogId()) ) )
 && ( (this.getUpdateDate()==castOther.getUpdateDate()) || ( this.getUpdateDate()!=null && castOther.getUpdateDate()!=null && this.getUpdateDate().equals(castOther.getUpdateDate()) ) )
 && ( (this.getCompanyId()==castOther.getCompanyId()) || ( this.getCompanyId()!=null && castOther.getCompanyId()!=null && this.getCompanyId().equals(castOther.getCompanyId()) ) )
 && ( (this.getCallLogNo()==castOther.getCallLogNo()) || ( this.getCallLogNo()!=null && castOther.getCallLogNo()!=null && this.getCallLogNo().equals(castOther.getCallLogNo()) ) )
 && ( (this.getStatus()==castOther.getStatus()) || ( this.getStatus()!=null && castOther.getStatus()!=null && this.getStatus().equals(castOther.getStatus()) ) )
 && ( (this.getStartDate()==castOther.getStartDate()) || ( this.getStartDate()!=null && castOther.getStartDate()!=null && this.getStartDate().equals(castOther.getStartDate()) ) )
 && ( (this.getEndDate()==castOther.getEndDate()) || ( this.getEndDate()!=null && castOther.getEndDate()!=null && this.getEndDate().equals(castOther.getEndDate()) ) )
 && ( (this.getCreateDate()==castOther.getCreateDate()) || ( this.getCreateDate()!=null && castOther.getCreateDate()!=null && this.getCreateDate().equals(castOther.getCreateDate()) ) )
 && ( (this.getCreateUserId()==castOther.getCreateUserId()) || ( this.getCreateUserId()!=null && castOther.getCreateUserId()!=null && this.getCreateUserId().equals(castOther.getCreateUserId()) ) )
 && ( (this.getCreateUserName()==castOther.getCreateUserName()) || ( this.getCreateUserName()!=null && castOther.getCreateUserName()!=null && this.getCreateUserName().equals(castOther.getCreateUserName()) ) )
 && ( (this.getUpdateUserId()==castOther.getUpdateUserId()) || ( this.getUpdateUserId()!=null && castOther.getUpdateUserId()!=null && this.getUpdateUserId().equals(castOther.getUpdateUserId()) ) )
 && ( (this.getUpdateUserName()==castOther.getUpdateUserName()) || ( this.getUpdateUserName()!=null && castOther.getUpdateUserName()!=null && this.getUpdateUserName().equals(castOther.getUpdateUserName()) ) )
 && ( (this.getDeleteDate()==castOther.getDeleteDate()) || ( this.getDeleteDate()!=null && castOther.getDeleteDate()!=null && this.getDeleteDate().equals(castOther.getDeleteDate()) ) );
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + ( getCallLogId() == null ? 0 : this.getCallLogId().hashCode() );
		result = 37 * result + ( getUpdateDate() == null ? 0 : this.getUpdateDate().hashCode() );
		result = 37 * result + ( getCompanyId() == null ? 0 : this.getCompanyId().hashCode() );
		result = 37 * result + ( getCallLogNo() == null ? 0 : this.getCallLogNo().hashCode() );
		result = 37 * result + ( getStatus() == null ? 0 : this.getStatus().hashCode() );
		result = 37 * result + ( getStartDate() == null ? 0 : this.getStartDate().hashCode() );
		result = 37 * result + ( getEndDate() == null ? 0 : this.getEndDate().hashCode() );
		result = 37 * result + ( getCreateDate() == null ? 0 : this.getCreateDate().hashCode() );
		result = 37 * result + ( getCreateUserId() == null ? 0 : this.getCreateUserId().hashCode() );
		result = 37 * result + ( getCreateUserName() == null ? 0 : this.getCreateUserName().hashCode() );
		result = 37 * result + ( getUpdateUserId() == null ? 0 : this.getUpdateUserId().hashCode() );
		result = 37 * result + ( getUpdateUserName() == null ? 0 : this.getUpdateUserName().hashCode() );
		result = 37 * result + ( getDeleteDate() == null ? 0 : this.getDeleteDate().hashCode() );
		return result;
	}

  // The following is extra code specified in the hbm.xml files

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;
		
  // end of extra code specified in the hbm.xml files

}


