/*
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 */

package cloud.optim.callcentersolution.api.app.inquiryform;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.beanutils.PropertyUtils;

import cloud.optim.callcentersolution.api.entity.Knowledge;

/**
 * Knowledge 検索結果.
 */
@XmlRootElement
public class InquiryFormSearchResult implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 文字列表現への変換.
   *
   * @return このインスタンスの文字列表現
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode()));
    sb.append(" [");
    sb.append("knowledgeId").append("='").append(getKnowledgeId()).append("' ");
    sb.append("companyId").append("='").append(getCompanyId()).append("' ");
    sb.append("hashedCompanyId").append("='").append(getCompanyId()).append("' ");
    sb.append("title").append("='").append(getTitle()).append("' ");
    sb.append("content").append("='").append(getContent()).append("' ");
    sb.append("url").append("='").append(getUrl()).append("' ");
    sb.append("manualName").append("='").append(getManualName()).append("' ");
    sb.append("manualPage").append("='").append(getManualPage()).append("' ");
    sb.append("manualUrl").append("='").append(getManualUrl()).append("' ");
    //sb.append("script").append("='").append(getScript()).append("' ");
    sb.append("clickCount").append("='").append(getClickCount()).append("' ");
    sb.append("]");
    return sb.toString();
  }

  /**
   * エンティティオブジェクトへの変換.
   *
   * @return 新たに作成した Knowledge オブジェクト
   */
  public Knowledge toEntity() {
    Knowledge result = new Knowledge();
    try {
      PropertyUtils.copyProperties(result, this);
    } catch (Exception e) {
      result = null;
    }
    return result;
  }

  /** ナレッジID. */
  private Long knowledgeId;

  /** 企業ID. */
  private String companyId;

  /** 企業IDハッシュ. */
  private String hashedCompanyId;

  /** ナレッジ番号. */
  private Long knowledgeNo;

  /** タイトル. */
  private String title;

  /** 内容. */
  private String content;

  /** URL. */
  private String url;

  /** マニュアル名. */
  private String manualName;

  /** マニュアルページ. */
  private String manualPage;

  /** マニュアルURL. */
  private String manualUrl;

  /* スクリプト. */
  //private String script;

  /** 参照回数. */
  private Long clickCount;

  /** 作成日時. */
  private Date createDate;

  /** 作成ユーザID. */
  private String createUserId;

  /** 作成ユーザ名. */
  private String createUserName;

  /** 更新日時. */
  private Date updateDate;

  /** 更新ユーザID. */
  private String updateUserId;

  /** 更新ユーザ名. */
  private String updateUserName;

  /**
   * ナレッジID取得.
   *
   * @return ナレッジID
   */
  public Long getKnowledgeId() {
    return this.knowledgeId;
  }

  /**
   * ナレッジID設定.
   *
   * @param knowledgeId ナレッジID
   */
  public void setKnowledgeId(Long knowledgeId) {
    this.knowledgeId = knowledgeId;
  }

  /**
   * 企業ID取得.
   *
   * @return 企業ID
   */
  public String getCompanyId() {
    return this.companyId;
  }

  /**
   * 企業ID設定.
   *
   * @param companyId 企業ID
   */
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  /**
   * 企業IDハッシュ取得.
   *
   * @return 企業IDハッシュ
   */
  public String getHashedCompanyId() {
    return this.hashedCompanyId;
  }

  /**
   * 企業IDハッシュ設定.
   *
   * @param hashedCompanyId 企業IDハッシュ
   */
  public void setHashedCompanyId(final String hashedCompanyId) {
    this.hashedCompanyId = hashedCompanyId;
  }

  /**
   * ナレッジ番号取得.
   *
   * @return knowledgeNo
   */
  public Long getKnowledgeNo() {
    return this.knowledgeNo;
  }

  /**
   * ナレッジ番号設定.
   *
   * @param knowledgeNo ナレッジ番号
   */
  public void setKnowledgeNo(Long knowledgeNo) {
    this.knowledgeNo = knowledgeNo;
  }

  /**
   * タイトル取得.
   *
   * @return タイトル
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * タイトル設定.
   *
   * @param title タイトル
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * 内容取得.
   *
   * @return 内容
   */
  public String getContent() {
    return this.content;
  }

  /**
   * 内容設定.
   *
   * @param content 内容
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * URL取得.
   *
   * @return URL
   */
  public String getUrl() {
    return this.url;
  }

  /**
   * URL設定.
   *
   * @param url URL
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * マニュアル名取得.
   *
   * @return マニュアル名
   */
  public String getManualName() {
    return this.manualName;
  }

  /**
   * マニュアル名設定.
   *
   * @param manualName マニュアル名
   */
  public void setManualName(String manualName) {
    this.manualName = manualName;
  }

  /**
   * マニュアルページ取得.
   *
   * @return マニュアルページ
   */
  public String getManualPage() {
    return this.manualPage;
  }

  /**
   * マニュアルページ設定.
   *
   * @param manualPage マニュアルページ
   */
  public void setManualPage(String manualPage) {
    this.manualPage = manualPage;
  }

  /**
   * マニュアルURL取得.
   *
   * @return マニュアルURL
   */
  public String getManualUrl() {
    return this.manualUrl;
  }

  /**
   * マニュアルURL設定.
   *
   * @param manualUrl マニュアルURL
   */
  public void setManualUrl(String manualUrl) {
    this.manualUrl = manualUrl;
  }

  /*
   * script 取得
   *
   * @return script
   */
  //public String getScript() {
  //  return this.script;
  //}

  /*
   * script 設定
   *
   * @param script スクリプト
   */
  //public void setScript(String script) {
  //  this.script = script;
  //}

  /**
   * 参照回数取得.
   *
   * @return 参照回数
   */
  public Long getClickCount() {
    return this.clickCount;
  }

  /**
   * 参照回数設定.
   *
   * @param clickCount 参照回数
   */
  public void setClickCount(Long clickCount) {
    this.clickCount = clickCount;
  }

  /**
   * 作成日時取得.
   *
   * @return 作成日時
   */
  public Date getCreateDate() {
    return createDate;
  }

  /**
   * 作成日時設定.
   *
   * @param createDate 作成日時
   */
  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  /**
   * 作成ユーザID取得.
   *
   * @return 作成ユーザID
   */
  public String getCreateUserId() {
    return createUserId;
  }

  /**
   * 作成ユーザID設定.
   *
   * @param createUserId 作成ユーザID
   */
  public void setCreateUserId(String createUserId) {
    this.createUserId = createUserId;
  }

  /**
   * 作成ユーザ名取得.
   *
   * @return 作成ユーザ名
   */
  public String getCreateUserName() {
    return createUserName;
  }

  /**
   * 作成ユーザ名設定.
   *
   * @param createUserName 作成ユーザ名
   */
  public void setCreateUserName(String createUserName) {
    this.createUserName = createUserName;
  }

  /**
   * 更新日時取得.
   *
   * @return 更新日時
   */
  public Date getUpdateDate() {
    return updateDate;
  }

  /**
   * 更新日時設定.
   *
   * @param updateDate 更新日時
   */
  public void setUpdateDate(Date updateDate) {
    this.updateDate = updateDate;
  }

  /**
   * 更新ユーザID取得.
   *
   * @return 更新ユーザID
   */
  public String getUpdateUserId() {
    return updateUserId;
  }

  /**
   * 更新ユーザID設定.
   *
   * @param updateUserId 更新ユーザID
   */
  public void setUpdateUserId(String updateUserId) {
    this.updateUserId = updateUserId;
  }

  /**
   * 更新ユーザ名取得.
   *
   * @return updateUserName
   */
  public String getUpdateUserName() {
    return updateUserName;
  }

  /**
   * 更新ユーザ名設定.
   *
   * @param updateUserName 更新ユーザ名
   */
  public void setUpdateUserName(String updateUserName) {
    this.updateUserName = updateUserName;
  }

}
