/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：KnowledgeSearchResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.knowledge;



import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement ;

import org.apache.commons.beanutils.PropertyUtils ;

import cloud.optim.callcentersolution.api.entity.Knowledge;

/**
 * Knowledge 検索結果.<br/>
 */
@XmlRootElement
public class KnowledgeSearchResult implements java.io.Serializable {

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
		sb.append("knowledgeId").append("='").append(getKnowledgeId()).append("' ");
		sb.append("companyId").append("='").append(getCompanyId()).append("' ");
		sb.append("title").append("='").append(getTitle()).append("' ");
		sb.append("content").append("='").append(getContent()).append("' ");
		sb.append("script").append("='").append(getScript()).append("' ");
		sb.append("clickCount").append("='").append(getClickCount()).append("' ");
		sb.append("]");

		return sb.toString();
	}

	/**
	 * エンティティオブジェクトへの変換
	 *
	 * @return 新たに作成した Knowledge オブジェクト
	 */
	public Knowledge toEntity() {
		Knowledge ret = new Knowledge() ;

		try {
			PropertyUtils.copyProperties( ret, this ) ;
		}
		catch ( Exception e ) {
			ret = null ;
		}

		return ret ;
	}




	/** ナレッジ ID */
	private Long knowledgeId;

	/** 企業 ID */
	private String companyId;

	/** ナレッジ 番号 */
	private Long knowledgeNo;

	/** タイトル */
	private String title;

	/** 内容 */
	private String content;

	/** スクリプト */
	private String script;

	/** 参照回数 */
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
	 * 更新日時
	 */
	private Date updateDate;

	/**
	 * 更新ユーザ ID
	 */
	private String updateUserId;

	/**
	 * 更新ユーザ名
	 */
	private String updateUserName;




	/**
	 * knowledgeId 取得
	 * @return knowledgeId
	 */
	public Long getKnowledgeId() {
		return this.knowledgeId;
	}

	/**
	 * knowledgeId 設定
	 * @param knowledgeId ナレッジ ID
	 */
	public void setKnowledgeId(Long knowledgeId) {
		this.knowledgeId = knowledgeId;
	}

	/**
	 * companyId 取得
	 * @return companyId
	 */
	public String getCompanyId() {
		return this.companyId;
	}

	/**
	 * companyId 設定
	 * @param companyId 企業 ID
	 */
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	/**
	 * knowledgeNo 取得
	 * @return knowledgeNo
	 */
	public Long getKnowledgeNo() {
		return this.knowledgeNo;
	}

	/**
	 * knowledgeNo 設定
	 * @param knowledgeNo ナレッジ ID
	 */
	public void setKnowledgeNo(Long knowledgeNo) {
		this.knowledgeNo = knowledgeNo;
	}

	/**
	 * title 取得
	 * @return title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * title 設定
	 * @param title タイトル
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * content 取得
	 * @return content
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * content 設定
	 * @param content 内容
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * script 取得
	 * @return script
	 */
	public String getScript() {
		return this.script;
	}

	/**
	 * script 設定
	 * @param script スクリプト
	 */
	public void setScript(String script) {
		this.script = script;
	}

	/**
	 * clickCount 取得
	 * @return clickCount
	 */
	public Long getClickCount() {
		return this.clickCount;
	}

	/**
	 * clickCount 設定
	 * @param clickCount 参照回数
	 */
	public void setClickCount(Long clickCount) {
		this.clickCount = clickCount;
	}

	/**
	 * createDate 取得
	 * @return createDate
	 */
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * createDate 設定
	 * @param createDate 作成日時
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * createUserId 取得
	 * @return createUserId
	 */
	public String getCreateUserId() {
		return createUserId;
	}

	/**
	 * createUserId 設定
	 * @param createUserId 作成者ID
	 */
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	/**
	 * createUserName 取得
	 * @return createUserName
	 */
	public String getCreateUserName() {
		return createUserName;
	}

	/**
	 * createUserName 設定
	 * @param createUserName 作成者名
	 */
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	/**
	 * updateDate 取得
	 * @return updateDate
	 */
	public Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * updateDate 設定
	 * @param clicupdateDatekCount 更新日時
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * updateUserId 取得
	 * @return updateUserId
	 */
	public String getUpdateUserId() {
		return updateUserId;
	}

	/**
	 * updateUserId 設定
	 * @param updateUserId 更新者ID
	 */
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	/**
	 * updateUserName 取得
	 * @return updateUserName
	 */
	public String getUpdateUserName() {
		return updateUserName;
	}

	/**
	 * updateUserName 設定
	 * @param updateUserName 更新者名
	 */
	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}
}