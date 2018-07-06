/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：ExtractUtil.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.util;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cloud.optim.callcentersolution.api.util.morphologicalanalyze.MorphologicalAnalyzerHolder;

/**
 * キーワード抽出Util
 * @author raifuyor
 *
 */
@Component
public class ExtractUtil {
	/** 形態素解析エンジン */
	@Resource private MorphologicalAnalyzerHolder analyzerHolder;

	/**
	 * テキスト先頭から解析して名詞を抽出する
	 * @param text 解析テキスト
	 * @return 抽出した名詞
	 */
	public List<String> extractNouns(String text) {
		return analyzerHolder.getAnalyzer().extractNouns(text, false);
	}

	/**
	 * テキスト末尾から解析して名詞を抽出する
	 * @param text 解析テキスト
	 * @return 抽出した名詞
	 */
	public List<String> extractNounsReverse(String text) {
		return analyzerHolder.getAnalyzer().extractNouns(text, true);
	}

	/**
	 * ユーザ辞書を更新する
	 * @throws Exception
	 */
	public void reloadUserDictionary() throws Exception {
		analyzerHolder.getAnalyzer().reloadUserDictionary();
	}

	/**
	 * 形態素解析インスタンスを追加する
	 * @param 企業管理ID
	 * @throws Exception
	 */
	public void addAnalyzer(Long companyManagementId) throws Exception {
		analyzerHolder.addAnalyzer(companyManagementId);
	}

	/**
	 * 形態素解析インスタンスを削除する
	 * @param 企業管理ID
	 */
	public void removeAnalyzer(Long companyManagementId) {
		analyzerHolder.removeAnalyzer(companyManagementId);
	}
}
