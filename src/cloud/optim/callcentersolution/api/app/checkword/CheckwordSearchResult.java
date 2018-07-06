/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CheckwordSearchResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.checkword;



import javax.xml.bind.annotation.XmlRootElement ;

import org.apache.commons.beanutils.PropertyUtils ;

import cloud.optim.callcentersolution.api.entity.Checkword ;

/**
 * Checkword 検索結果.<br/>
 */
@XmlRootElement
public class CheckwordSearchResult implements java.io.Serializable {

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
		sb.append("checkwordId").append("='").append(getCheckwordId()).append("' ");
		sb.append("companyId").append("='").append(getCompanyId()).append("' ");
		sb.append("checkword").append("='").append(getCheckword()).append("' ");
		sb.append("colorTheme").append("='").append(getColorTheme()).append("' ");
		sb.append("]");

		return sb.toString();
	}

	/**
	 * エンティティオブジェクトへの変換
	 *
	 * @return 新たに作成した Checkword オブジェクト
	 */
	public Checkword toEntity() {
		Checkword ret = new Checkword() ;

		try {
			PropertyUtils.copyProperties( ret, this ) ;
		}
		catch ( Exception e ) {
			ret = null ;
		}

		return ret ;
	}




	/** チェックワードID */
	private Long checkwordId;

	/** 企業 ID */
	private String companyId;

	/** チェックワード内容 */
	private String checkword;

	/** ワードカラー名称 */
	private String colorTheme;




	/**
	 * checkwordId 取得
	 * @return checkwordId
	 */
	public Long getCheckwordId() {
		return this.checkwordId;
	}

	/**
	 * checkwordId 設定
	 * @param checkwordId チェックワードID
	 */
	public void setCheckwordId(Long checkwordId) {
		this.checkwordId = checkwordId;
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
	 * checkword 取得
	 * @return checkword
	 */
	public String getCheckword() {
		return this.checkword;
	}

	/**
	 * checkword 設定
	 * @param checkword チェックワード内容
	 */
	public void setCheckword(String checkword) {
		this.checkword = checkword;
	}

	/**
	 * colorTheme 取得
	 * @return colorTheme
	 */
	public String getColorTheme() {
		return this.colorTheme;
	}

	/**
	 * colorTheme 設定
	 * @param colorTheme ワードカラー名称
	 */
	public void setColorTheme(String colorTheme) {
		this.colorTheme = colorTheme;
	}



}