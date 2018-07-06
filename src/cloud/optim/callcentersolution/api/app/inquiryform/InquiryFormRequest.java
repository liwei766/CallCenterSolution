/*
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 */

package cloud.optim.callcentersolution.api.app.inquiryform;

import cloud.optim.callcentersolution.api.entity.Knowledge;
import cloud.optim.callcentersolution.core.common.utility.ToStringHelper;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * InquiryForm API リクエストクラス.
 */
@XmlRootElement(name = "restRequest")
public class InquiryFormRequest implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  /** 検索条件. */
  private SearchForm searchForm;

  /** 取得(編集)条件. */
  private EditForm editForm;

  /**
   * 文字列表現への変換.
   *
   * @return 文字列表現
   */
  @Override
  public String toString() {
    return ToStringHelper.toString(this);
  }

  // -------------------------------------------------------------------------
  // 内部クラス
  // -------------------------------------------------------------------------

  /** 取得(編集)用. */
  public static final class EditForm implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    //--- for Consumer Site

    /** 企業IDハッシュ. */
    private String hashedCompanyId;

    /** エンティティ情報. */
    private Knowledge knowledge;

    /** タグ名称配列. */
    private List<String> tag;

    /**
     * 文字列表現への変換.
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
      return ToStringHelper.toString(this);
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
     * knowledge 取得.
     *
     * @return knowledge
     */
    public Knowledge getKnowledge() {
      return this.knowledge;
    }

    /**
     * knowledge 設定.
     *
     * @param knowledge knowledge に設定する値.
     */
    public void setKnowledge(final Knowledge knowledge) {
      this.knowledge = knowledge;
    }

    /**
     * tag 取得.
     *
     * @return tag
     */
    public List<String> getTag() {
      return tag;
    }

    /**
     * tag 設定.
     *
     * @param tag tag に設定する値.
     */
    public void setTag(List<String> tag) {
      this.tag = tag;
    }

  }

  // -------------------------------------------------------------------------
  // アクセサメソッド
  // -------------------------------------------------------------------------

  /**
   * searchForm 取得.
   *
   * @return searchForm
   */
  public SearchForm getSearchForm() {
    return this.searchForm;
  }

  /**
   * searchForm 設定.
   *
   * @param searchForm searchForm に設定する値.
   */
  public void setSearchForm(SearchForm searchForm) {
    this.searchForm = searchForm;
  }

  /**
   * editForm 取得.
   *
   * @return editForm
   */
  public EditForm getEditForm() {
    return this.editForm;
  }

  /**
   * editForm 設定.
   *
   * @param entity editForm に設定する値.
   */
  public void setEditForm(EditForm entity) {
    this.editForm = entity;
  }

}
