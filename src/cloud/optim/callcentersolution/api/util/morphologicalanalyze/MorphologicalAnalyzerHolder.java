/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：MorphologicalAnalyzer.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.util.morphologicalanalyze;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cloud.optim.callcentersolution.api.entity.CompanyManagement;
import cloud.optim.callcentersolution.api.entity.dao.CompanyManagementDao;
import cloud.optim.callcentersolution.core.modules.loginutil.CustomUser;
import cloud.optim.callcentersolution.core.modules.loginutil.LoginUtility;

@Component
public class MorphologicalAnalyzerHolder {

	/** CompanyManagementDao */
	@Resource private CompanyManagementDao companyManagementDao;

	/** デフォルト企業管理ID */
	private static final Long DEFAULT_COMPANY_ID = -1L;

	/** 抽出対象外品詞 */
	@Value( "${morphlogical.analyze.ng.part.of.speech}" )
	private String[] ngPartOfSpeech;

	/** 抽出対象の品詞  */
	@Value( "${morphlogical.analyze.extract.part.of.speech}" )
	private String extractPartOfSpeech;

	/** 形態素解析ユーザ辞書ファイル名パス */
	@Value( "${morphlogical.analyze.user.dictionary.base.path}" )
	private String dictionaryPath;

	/** LoginUtility */
	@Resource private LoginUtility loginUtility;

	/** kuromojiインスタンスマップ */
	private Map<Long, MorphologicalAnalyzer> analyzerMap;

	/** 除外品詞リスト */
	private List<String> ngPartOfSpeechList;

	/**
	 * kuromojiインスタンス初期化処理
	 * 企業管理テーブルの企業分kuromojiインスタンスを生成する
	 * コンポーネントスキャン実行後に実行する処理
	 * @throws Exception
	 */
	@PostConstruct
	private void initialize() throws Exception {
		analyzerMap = new HashMap<>();

		ngPartOfSpeechList = Arrays.asList(ngPartOfSpeech);

		// デフォルトのインスタンスを生成する
		analyzerMap.put(DEFAULT_COMPANY_ID, new KuromojiAnalyzer(ngPartOfSpeechList, extractPartOfSpeech, null));

		// 企業一覧を取得する
		List<CompanyManagement> list = companyManagementDao.findByExample(new CompanyManagement());

		for(CompanyManagement each : list) {
			String path = String.format(dictionaryPath, each.getCompanyManagementId());
			analyzerMap.put(each.getCompanyManagementId(), new KuromojiAnalyzer(ngPartOfSpeechList, extractPartOfSpeech, path));
		}
	}

	/**
	 * ログインユーザの企業に紐付く形態素解析インスタンスを取得する
	 * 企業管理IDが取得できない場合はデフォルトのインスタンスを取得する
	 * @return 形態素解析インスタン
	 */
	public MorphologicalAnalyzer getAnalyzer() {
		CustomUser customUser = loginUtility.getCustomUser();
		return analyzerMap.get(customUser.getCompanyManagementId() == null ? DEFAULT_COMPANY_ID : customUser.getCompanyManagementId());
	}

	/**
	 * 指定企業の形態素解析インスタンスを追加する
	 * 既に存在している場合は何もしない
	 * @param 企業管理ID
	 * @throws Exception
	 */
	public void addAnalyzer(Long companyManagementId) throws Exception {
		if(companyManagementId == null) return;
		if(analyzerMap.containsKey(companyManagementId)) return;
		String path = String.format(dictionaryPath, companyManagementId);
		analyzerMap.put(companyManagementId, new KuromojiAnalyzer(ngPartOfSpeechList, extractPartOfSpeech, path));
	}

	/**
	 * 指定企業の形態素解析インスタンスを削除する
	 * @param 企業管理ID
	 */
	public void removeAnalyzer(Long companyManagementId) {
		if(companyManagementId == null) return;
		analyzerMap.remove(companyManagementId);
	}
}
