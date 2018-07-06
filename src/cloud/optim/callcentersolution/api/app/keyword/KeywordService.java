/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：KnowledgeService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.keyword;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.callcentersolution.api.entity.dao.TagDao;
import cloud.optim.callcentersolution.api.util.ExtractUtil;
import cloud.optim.callcentersolution.core.common.utility.QueryHelper;


/**
 * KnowledgeService実装.<br/>
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Throwable.class, isolation=Isolation.READ_COMMITTED)
public class KeywordService {

	///** Commons Logging instance.  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());

	/** ExtractUtil */
	@Resource private ExtractUtil extractUtil;

	/** TagDao */
	@Resource private TagDao tagDao;

	/** タグ名称最大文字数 */
	private static final int TAG_MAX_LENGTH = 100;

	/**
	 * MyBatis Mapper
	 */
	@Resource
	private KeywordMapper keywordMapper;

	/**
	 * KeywordMapper 取得
	 * @return KeywordMapper
	 */
	public KeywordMapper getKeywordMapper() {
		return keywordMapper;
	}

	/**
	 * キーワード抽出
	 * @param text 解析文字列
	 * @return 解析結果リスト
	 * @throws Exception エラー
	 */
	public List<SearchResult> extract(String text) throws Exception {

		if(StringUtils.isEmpty(text)) return Collections.emptyList();

		// 形態素解析で名詞を抽出する
		List<String> nouns = extractUtil.extractNouns(text);

		return nouns.stream()
				// 100文字を超える名詞は除外する
				.filter(each ->  each != null && each.length() <= TAG_MAX_LENGTH)
				.map(noun -> {
					SearchResult searchResult = new SearchResult();
					searchResult.setKeyword(noun);
					return searchResult;
				})
				.collect(Collectors.toList());
	}

	/**
	 * テキストから解析した名詞でタグ情報を取得する
	 * @param companyId 企業ID
	 * @param text テキスト
	 * @param extractCount 抽出件数
	 * @return 検索結果
	 * @throws Exception
	 */
	public List<SearchResult> getKnowledgeKeywords(String companyId, String text, int extractCount) throws Exception {

		if(StringUtils.isEmpty(text)) return Collections.emptyList();

		// 形態素解析で名詞を抽出する
		List<String> extractResult = extractUtil.extractNounsReverse(text);

		// 名詞が抽出できない場合は空のリストを返す
		if (extractResult == null || extractResult.isEmpty()) return Collections.emptyList();

		// LIKE検索用のエスケープをする
		List<String> nouns = extractResult.stream()
				.map(noun -> QueryHelper.escape(noun) + "%")
				.collect(Collectors.toList());

		// タグテーブル検索
		List<SearchResult> tagList = keywordMapper.search(companyId, nouns);
		if (tagList == null || tagList.isEmpty()) return Collections.emptyList();


		// 抽出件数分タグを抽出する
		List<SearchResult> result = new ArrayList<>();
		for (String each : extractResult) {

			for (SearchResult tag : tagList) {
				if(!tag.getKeyword().startsWith(each)) continue;
				result.add(tag);
				break;
			}

			// 重複削除
			result = result.stream().distinct().collect(Collectors.toList());
			if (result.size() >= extractCount) {
				// 10件以上になった場合は、先頭10件取得
				return result.subList(0, extractCount);
			}
		}
		return result;
	}

	/**
	 * テキストに一致するタグを取得する
	 * @param companyId 企業ID
	 * @param text テキスト
	 * @return 検索結果
	 */
	public SearchResult getKeyword(String companyId, String text) {
		if (StringUtils.isEmpty(text)) return null;
		return keywordMapper.get(companyId, text);
	}

}