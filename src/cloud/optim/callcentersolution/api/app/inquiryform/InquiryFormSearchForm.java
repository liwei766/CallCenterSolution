/*
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 */

package cloud.optim.callcentersolution.api.app.inquiryform;

import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Knowledge 検索フォーム.
 */
public class InquiryFormSearchForm implements java.io.Serializable {

  private static final long serialVersionUID = 1L;


  /**
   * 文字列表現への変換.
   *
   * @return このインスタンスの文字列表現
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
  }


  // ------------------------------------------------------------
  // 検索条件
  // ------------------------------------------------------------

  /** 企業IDハッシュ. */
  private String hashedCompanyId = null;

  /**
   * 企業IDハッシュ取得.
   * 
   * @return companyId 企業ID
   */
  public String getHashedCompanyId() {
    return this.hashedCompanyId;
  }

  /**
   * 企業IDハッシュ設定.
   * 
   * @param hashedCompanyId 企業IDハッシュ
   */
  public void setHashedCompanyId(String hashedCompanyId) {
    this.hashedCompanyId = hashedCompanyId;
  }

  /** 企業ID. */
  private String companyId = null;

  /**
   *  企業ID取得.
   *  @return companyId 企業ID
   */
  public String getCompanyId() {
    return companyId;
  }

  /**
   *  企業ID設定.
   *  @param companyId 企業ID
   */
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  /** タグID. */
  private List<Long> tagId = null;

  /**
   * タグID取得.
   * 
   * @return tagId タグID
   */
  public List<Long> getTagId() {
    return tagId;
  }

  /*
   * タグID件数取得.
   * 
   * @return tagId タグID件数
   */
  /*
  public int getTagIdCount() {
    return tagId.size();
  }
  */

  /**
   * タグID設定.
   * 
   * @param tagId タグID
   */
  public void setTagId(List<Long> tagId) {
    this.tagId = tagId;
  }

  /** (解析)テキスト. */
  private String text;

  /**
   * (解析)テキスト取得.
   * @return (解析)テキスト
   */
  public String getText() {
    return this.text;
  }

  /**
   * (解析)テキスト設定.
   * 
   * @param text (解析)テキスト
   */
  public void setText(final String text) {
    this.text = text;
  }

}
