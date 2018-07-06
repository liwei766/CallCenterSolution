/*
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 */

package cloud.optim.callcentersolution.api.app.inquiryform;

import cloud.optim.callcentersolution.core.common.utility.ToStringHelper;
import cloud.optim.callcentersolution.core.modules.rest.RestResult;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * お問い合わせ画面APIレスポンスクラス.
 */
@XmlRootElement(name = "restResponse")
public class InquiryFormResponse implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  /** 処理結果. */
  private List<RestResult> resultList = new ArrayList<RestResult>();

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
  // 処理結果を扱う処理
  // -------------------------------------------------------------------------

  /**
   * 処理結果数を取得する.
   *
   * @return 登録されている処理結果数
   */
  @XmlTransient
  @JsonIgnore
  public int getResultLength() {
    if (resultList == null) {
      return 0;
    }
    return resultList.size();
  }

  /**
   * 処理結果を登録する.
   *
   * @param result 登録する処理結果
   */
  public void addResult(RestResult result) {
    if (resultList == null) {
      resultList = new ArrayList<RestResult>();
    }
    resultList.add(result);
  }

  /**
   * 処理結果を登録する.
   *
   * @param result 登録する処理結果
   */
  public void setResult(RestResult result) {
    if (resultList == null) {
      resultList = new ArrayList<RestResult>();
    }
    resultList.clear();
    resultList.add(result);
  }

  // -------------------------------------------------------------------------
  // アクセサメソッド
  // -------------------------------------------------------------------------

  /**
   * resultList 取得.
   *
   * @return resultList
   */
  @XmlElementWrapper(name = "resultList")
  @XmlElement(name = "result")
  @JsonProperty("resultList")
  public List<RestResult> getResultList() {
    return resultList;
  }

  /**
   * resultList 設定.
   *
   * @param resultList resultList に設定する値.
   */
  public void setResultList(List<RestResult> resultList) {
    this.resultList = resultList;
  }

}
