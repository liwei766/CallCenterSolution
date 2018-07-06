/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：Knowledge.java
 */

package cloud.optim.callcentersolution.api.entity;
// Generated by Hibernate Tools 3.2.2.CR1


import java.util.Date;

/**

 */
public class Knowledge  implements java.io.Serializable {


	/**
	 * ナレッジ ID
	 */
	private Long knowledgeId;

	/**
	 * 更新日時
	 */
	private Date updateDate;

	/**
	 * 企業 ID
	 */
	private String companyId;

	/**
	 * ナレッジ番号
	 */
	private Long knowledgeNo;

	/**
	 * タイトル
	 */
	private String title;

	/**
	 * 回答
	 */
	private String content;

	/**
	 * スクリプト
	 */
	private String script;

	/**
	 * 参照回数
	 */
	private Long clickCount;

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
	 * Knowledge デフォルトコンストラクター
	 */
	public Knowledge() {
	}

	/**
	 * Knowledge 最小コンストラクター
	 * @param companyId 企業 ID
	 * @param knowledgeNo ナレッジ番号
	 * @param title タイトル
	 * @param content 回答
	 * @param clickCount 参照回数
	 * @param createDate 作成日時
	 * @param createUserId 作成ユーザ ID
	 * @param createUserName 作成ユーザ名
	 * @param updateUserId 更新ユーザ ID
	 * @param updateUserName 更新ユーザ名
	 */
	public Knowledge(String companyId, Long knowledgeNo, String title, String content, Long clickCount, Date createDate, String createUserId, String createUserName, String updateUserId, String updateUserName) {
		this.companyId = companyId;
		this.knowledgeNo = knowledgeNo;
		this.title = title;
		this.content = content;
		this.clickCount = clickCount;
		this.createDate = createDate;
		this.createUserId = createUserId;
		this.createUserName = createUserName;
		this.updateUserId = updateUserId;
		this.updateUserName = updateUserName;
    }
	/**
	 * Knowledge フルコンストラクター
	 * @param companyId 企業 ID
	 * @param knowledgeNo ナレッジ番号
	 * @param title タイトル
	 * @param content 回答
	 * @param script スクリプト
	 * @param clickCount 参照回数
	 * @param createDate 作成日時
	 * @param createUserId 作成ユーザ ID
	 * @param createUserName 作成ユーザ名
	 * @param updateUserId 更新ユーザ ID
	 * @param updateUserName 更新ユーザ名
	 */
	public Knowledge(String companyId, Long knowledgeNo, String title, String content, String script, Long clickCount, Date createDate, String createUserId, String createUserName, String updateUserId, String updateUserName) {
		this.companyId = companyId;
		this.knowledgeNo = knowledgeNo;
		this.title = title;
		this.content = content;
		this.script = script;
		this.clickCount = clickCount;
		this.createDate = createDate;
		this.createUserId = createUserId;
		this.createUserName = createUserName;
		this.updateUserId = updateUserId;
		this.updateUserName = updateUserName;
	}

	// * ナレッジ ID */
	/**
	 * @return knowledgeId (ナレッジ ID)
	 */
	public Long getKnowledgeId() {
		return this.knowledgeId;
	}

	/**
	 * @param knowledgeId ナレッジ ID
	 */
	public void setKnowledgeId(Long knowledgeId) {
		this.knowledgeId = knowledgeId;
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

	// * ナレッジ番号 */
	/**
	 * @return knowledgeNo (ナレッジ番号)
	 */
	public Long getKnowledgeNo() {
		return this.knowledgeNo;
	}

	/**
	 * @param knowledgeNo ナレッジ番号
	 */
	public void setKnowledgeNo(Long knowledgeNo) {
		this.knowledgeNo = knowledgeNo;
	}

	// * タイトル */
	/**
	 * @return title (タイトル)
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @param title タイトル
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	// * 回答 */
	/**
	 * @return content (回答)
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * @param content 回答
	 */
	public void setContent(String content) {
		this.content = content;
	}

	// * スクリプト */
	/**
	 * @return script (スクリプト)
	 */
	public String getScript() {
		return this.script;
	}

	/**
	 * @param script スクリプト
	 */
	public void setScript(String script) {
		this.script = script;
	}

	// * 参照回数 */
	/**
	 * @return clickCount (参照回数)
	 */
	public Long getClickCount() {
		return this.clickCount;
	}

	/**
	 * @param clickCount 参照回数
	 */
	public void setClickCount(Long clickCount) {
		this.clickCount = clickCount;
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


	/**
	 * toString
	 * @return String
	*/
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
		buffer.append("knowledgeId").append("='").append(getKnowledgeId()).append("' ");
		buffer.append("updateDate").append("='").append(getUpdateDate()).append("' ");
		buffer.append("companyId").append("='").append(getCompanyId()).append("' ");
		buffer.append("knowledgeNo").append("='").append(getKnowledgeNo()).append("' ");
		buffer.append("title").append("='").append(getTitle()).append("' ");
		buffer.append("content").append("='").append(getContent()).append("' ");
		buffer.append("script").append("='").append(getScript()).append("' ");
		buffer.append("clickCount").append("='").append(getClickCount()).append("' ");
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
		if ( !(other instanceof Knowledge) ) return false;
		Knowledge castOther = ( Knowledge ) other;

		return ( (this.getKnowledgeId()==castOther.getKnowledgeId()) || ( this.getKnowledgeId()!=null && castOther.getKnowledgeId()!=null && this.getKnowledgeId().equals(castOther.getKnowledgeId()) ) )
 && ( (this.getUpdateDate()==castOther.getUpdateDate()) || ( this.getUpdateDate()!=null && castOther.getUpdateDate()!=null && this.getUpdateDate().equals(castOther.getUpdateDate()) ) )
 && ( (this.getCompanyId()==castOther.getCompanyId()) || ( this.getCompanyId()!=null && castOther.getCompanyId()!=null && this.getCompanyId().equals(castOther.getCompanyId()) ) )
 && ( (this.getKnowledgeNo()==castOther.getKnowledgeNo()) || ( this.getKnowledgeNo()!=null && castOther.getKnowledgeNo()!=null && this.getKnowledgeNo().equals(castOther.getKnowledgeNo()) ) )
 && ( (this.getTitle()==castOther.getTitle()) || ( this.getTitle()!=null && castOther.getTitle()!=null && this.getTitle().equals(castOther.getTitle()) ) )
 && ( (this.getContent()==castOther.getContent()) || ( this.getContent()!=null && castOther.getContent()!=null && this.getContent().equals(castOther.getContent()) ) )
 && ( (this.getScript()==castOther.getScript()) || ( this.getScript()!=null && castOther.getScript()!=null && this.getScript().equals(castOther.getScript()) ) )
 && ( (this.getCreateDate()==castOther.getCreateDate()) || ( this.getCreateDate()!=null && castOther.getCreateDate()!=null && this.getCreateDate().equals(castOther.getCreateDate()) ) )
 && ( (this.getCreateUserId()==castOther.getCreateUserId()) || ( this.getCreateUserId()!=null && castOther.getCreateUserId()!=null && this.getCreateUserId().equals(castOther.getCreateUserId()) ) )
 && ( (this.getCreateUserName()==castOther.getCreateUserName()) || ( this.getCreateUserName()!=null && castOther.getCreateUserName()!=null && this.getCreateUserName().equals(castOther.getCreateUserName()) ) )
 && ( (this.getUpdateUserId()==castOther.getUpdateUserId()) || ( this.getUpdateUserId()!=null && castOther.getUpdateUserId()!=null && this.getUpdateUserId().equals(castOther.getUpdateUserId()) ) )
 && ( (this.getUpdateUserName()==castOther.getUpdateUserName()) || ( this.getUpdateUserName()!=null && castOther.getUpdateUserName()!=null && this.getUpdateUserName().equals(castOther.getUpdateUserName()) ) );
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + ( getKnowledgeId() == null ? 0 : this.getKnowledgeId().hashCode() );
		result = 37 * result + ( getUpdateDate() == null ? 0 : this.getUpdateDate().hashCode() );
		result = 37 * result + ( getCompanyId() == null ? 0 : this.getCompanyId().hashCode() );
		result = 37 * result + ( getKnowledgeNo() == null ? 0 : this.getKnowledgeNo().hashCode() );
		result = 37 * result + ( getTitle() == null ? 0 : this.getTitle().hashCode() );
		result = 37 * result + ( getContent() == null ? 0 : this.getContent().hashCode() );
		result = 37 * result + ( getScript() == null ? 0 : this.getScript().hashCode() );
		
		result = 37 * result + ( getCreateDate() == null ? 0 : this.getCreateDate().hashCode() );
		result = 37 * result + ( getCreateUserId() == null ? 0 : this.getCreateUserId().hashCode() );
		result = 37 * result + ( getCreateUserName() == null ? 0 : this.getCreateUserName().hashCode() );
		result = 37 * result + ( getUpdateUserId() == null ? 0 : this.getUpdateUserId().hashCode() );
		result = 37 * result + ( getUpdateUserName() == null ? 0 : this.getUpdateUserName().hashCode() );
		return result;
	}

  // The following is extra code specified in the hbm.xml files

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;
		
  // end of extra code specified in the hbm.xml files

}

