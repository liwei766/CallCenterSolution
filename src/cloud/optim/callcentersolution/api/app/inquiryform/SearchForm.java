/*
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 */

package cloud.optim.callcentersolution.api.app.inquiryform;

import cloud.optim.callcentersolution.core.common.utility.ToStringHelper;
import cloud.optim.callcentersolution.core.modules.rest.SortForm;

/**
 * 検索条件.
 */
public class SearchForm implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  /** ソート条件. */
  private SortForm sortForm;

  /** 検索条件. */
  private InquiryFormSearchForm inquiryFormSearchForm;

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
   * ソート条件取得.
   *
   * @return sortForm ソート条件
   */
  public SortForm getSortForm() {
    return this.sortForm;
  }

  /**
   * ソート条件設定.
   *
   * @param sortForm ソート条件
   */
  public void setSortForm(SortForm sortForm) {
    this.sortForm = sortForm;
  }

  /**
   * 検索条件取得.
   *
   * @return 検索条件
   */
  public InquiryFormSearchForm getInquiryFormSearchForm() {
    return this.inquiryFormSearchForm;
  }

  /**
   * 検索条件設定.
   *
   * @param inquiryFormSearchForm 検索条件
   */
  public void setInquiryFormSearchForm(InquiryFormSearchForm inquiryFormSearchForm) {
    this.inquiryFormSearchForm = inquiryFormSearchForm;
  }

}
