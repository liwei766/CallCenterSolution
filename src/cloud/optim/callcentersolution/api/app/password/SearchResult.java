/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：AgencySearchResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.callcentersolution.api.app.password;

import javax.xml.bind.annotation.XmlRootElement ;

/**
 * ユーザー 検索結果.<br/>
 */
@XmlRootElement
public class SearchResult implements java.io.Serializable {

  /** serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * 文字列表現への変換.
   *
   * @return このインスタンスの文字列表現
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode()))
        .append(" [");
    sb.append("userId").append("='").append(getUserId()).append("' ");
    sb.append("userName").append("='").append(getUserName()).append("' ");
    sb.append("]");
    return sb.toString();
  }

//  /**
//   * エンティティオブジェクトへの変換.
//   *
//   * @return 新たに作成した Agency オブジェクト
//   */
//  public Agency toEntity() {
//    Agency ret = new Agency();
//
//    try {
//      PropertyUtils.copyProperties(ret, this);
//    } catch (Exception e) {
//      ret = null;
//    }
//
//    return ret;
//  }

  /** ユーザーID. */
  private String userId;

  /** ユーザー名称. */
  private String userName;

  /**
   * userId 取得.
   *
   * @return userId
   */
  public String getUserId() {
    return this.userId;
  }

  /**
   * userId 設定.
   *
   * @param userId ユーザーID
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * userName 取得.
   *
   * @return userName
   */
  public String getUserName() {
    return this.userName;
  }

  /**
   * userName 設定.
   *
   * @param userName ユーザー名称
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }

}
