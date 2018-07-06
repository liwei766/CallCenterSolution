/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.constant;

/**
 * 通話ログステータスの列挙型
 */
public enum CallLogStatus {
	FREE("0"),
	CALLING("1");

	private String value;

	CallLogStatus (String value) {
		this.value = value;
	}

	public String getValue(){
		return this.value;
	}

	public boolean matches(String value) {
		return this.value.equals(value);
	}
}

