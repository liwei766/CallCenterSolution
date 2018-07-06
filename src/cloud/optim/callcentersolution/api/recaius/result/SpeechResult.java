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
package cloud.optim.callcentersolution.api.recaius.result;

import java.util.List;

/**
 * 音声認識結果クラス
 */
public class SpeechResult {

	/** 解析結果種別  */
	private List<SpeechNBestResult> resultList;

	/** 利用時間 */
	private long useTime;

	/**
	 * @return resultList
	 */
	public List<SpeechNBestResult> getResultList() {
		return resultList;
	}

	/**
	 * @param resultList セットする resultList
	 */
	public void setResultList(List<SpeechNBestResult> resultList) {
		this.resultList = resultList;
	}

	/**
	 * @return useTime
	 */
	public long getUseTime() {
		return useTime;
	}

	/**
	 * @param useTime セットする useTime
	 */
	public void setUseTime(long useTime) {
		this.useTime = useTime;
	}


}
