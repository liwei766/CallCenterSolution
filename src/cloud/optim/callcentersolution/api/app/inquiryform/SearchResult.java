/*
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 */

package cloud.optim.callcentersolution.api.app.inquiryform;

import cloud.optim.callcentersolution.core.common.utility.ToStringHelper;

/**
 * 検索結果.
 */
public class SearchResult implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 検索結果.
   */
  private InquiryFormSearchResult searchResult;

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
   * 検索結果取得.
   *
   * @return 検索結果
   */
  public InquiryFormSearchResult getKnowledge() {
    return searchResult;
  }

  /**
   * 検索結果設定.
   *
   * @param searchResult 検索結果
   */
  public void setKnowledge(final InquiryFormSearchResult searchResult) {
    this.searchResult = searchResult;
  }

}
