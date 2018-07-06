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
package cloud.optim.callcentersolution.api.app.knowledge;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.callcentersolution.api.app.knowledge.KnowledgeRequest.EditForm;
import cloud.optim.callcentersolution.api.app.manual.ManualService;
import cloud.optim.callcentersolution.api.app.reference.ReferenceService;
import cloud.optim.callcentersolution.api.app.tag.TagService;
import cloud.optim.callcentersolution.api.entity.Knowledge;
import cloud.optim.callcentersolution.api.entity.KnowledgeTag;
import cloud.optim.callcentersolution.api.entity.Manual;
import cloud.optim.callcentersolution.api.entity.Reference;
import cloud.optim.callcentersolution.api.entity.Tag;
import cloud.optim.callcentersolution.api.entity.dao.KnowledgeDao;
import cloud.optim.callcentersolution.api.entity.dao.KnowledgeTagDao;
import cloud.optim.callcentersolution.api.entity.dao.ManualDao;
import cloud.optim.callcentersolution.api.entity.dao.ReferenceDao;
import cloud.optim.callcentersolution.api.entity.dao.TagDao;


/**
 * KnowledgeService実装.<br/>
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Throwable.class, isolation=Isolation.READ_COMMITTED)
public class KnowledgeService {

	///** Commons Logging instance.  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());


	/**
	 * TagService
	 */
	@Resource
	private TagService tagService;

	/**
	 * KnowledgeDao
	 */
	@Resource
	private KnowledgeDao knowledgeDao;

	/**
	 * TagDao
	 */
	@Resource
	private TagDao tagDao;

	/**
	 * KnowledgeTagDao
	 */
	@Resource
	private KnowledgeTagDao knowledgeTagDao;

	/**
	 * ReferenceService
	 */
	@Resource
	private ReferenceService referenceService;

	/**
	 * ReferenceDao
	 */
	@Resource
	private ReferenceDao referenceDao;

	/**
	 * ManualService
	 */
	@Resource
	private ManualService manualService;

	/**
	 * ManualDao
	 */
	@Resource
	private ManualDao manualDao;

	/**
	 * KnowledgeDao 取得
	 * @return KnowledgeDao
	 */
	public KnowledgeDao getKnowledgeDao() {
		return knowledgeDao;
	}

	/**
	 * MyBatis Mapper
	 */
	@Resource
	private KnowledgeMapper knowledgeMapper;

	/**
	 * KnowledgeMapper 取得
	 * @return KnowledgeMapper
	 */
	public KnowledgeMapper getKnowledgeMapper() {
		return knowledgeMapper;
	}


	/**
	 * 一件検索
	 * @param id エンティティの識別ID
	 * @return エンティティ
	 */
	public Knowledge getKnowledge( Serializable id, String companyId ) {
		Knowledge result = this.knowledgeDao.get(id);
		if (result == null) return null;
		if (!companyId.equals(result.getCompanyId())) return null;
		return result;
	}

	/**
	 * 複数検索
	 * @param searchForm 検索フォーム
	 * @return 検索結果リスト
	 * @throws Exception エラー
	 */
	public List<SearchResult> search(SearchForm searchForm) throws Exception {
		List<SearchResult> list = knowledgeMapper.search(searchForm);
		return list;
	}

	/**
	 * CSV出力用ナレッジ検索
	 * @param searchForm 検索フォーム
	 * @return 検索結果リスト
	 * @throws Exception エラー
	 */
	public List<KnowledgeExportBean> searchForExport(String companyId) throws Exception {
		List<KnowledgeExportBean> list = knowledgeMapper.searchForExport(companyId);
		return list;
	}


	/**
	 * 登録
	 * @param entity エンティティ
	 * @param tagNames タグ名リスト
	 * @param referenceList 参照先リスト
	 * @param manualList マニュアルエンティティ
	 * @return 登録したエンティティ
	 */
	public Knowledge save( Knowledge entity, List<String> tagNames, List<String> referenceList, List<Manual> manualList ) {
		// ナレッジの登録
		Long knowledgeNo = knowledgeMapper.getMaxKnowledgeNo(entity.getCompanyId());
		entity.setKnowledgeNo(knowledgeNo == null ? 1L : knowledgeNo + 1L);
		this.knowledgeDao.save(entity);

		// タグとナレッジタグ関連の登録
		registTags(entity, tagNames);

		// 参照先の登録
		registReferences(entity, referenceList);

		// マニュアルの登録
		registManuals(entity, manualList);

		return entity;
	}


	/**
	 * 更新
	 * @param entity エンティティ
	 * @param tagNames タグ名リスト
	 * @param referenceList 参照先リスト
	 * @param manualList マニュアルエンティティ
	 * @return 更新したエンティティ
	 */
	public Knowledge update( Knowledge entity, List<String> tagNames, List<String> referenceList, List<Manual> manualList ) {
		// ナレッジの更新
		this.knowledgeDao.update(entity);

		// ナレッジタグ関連テーブルの削除
		KnowledgeTag examaple = new KnowledgeTag();
		examaple.setKnowledgeId(entity.getKnowledgeId());
		knowledgeTagDao.deleteAll(knowledgeTagDao.findByExample(examaple));

		// タグとナレッジタグ関連の登録
		registTags(entity, tagNames);

		// 参照先の削除
		Reference reference = new Reference();
		reference.setKnowledgeId(entity.getKnowledgeId());
		reference.setCompanyId(entity.getCompanyId());
		referenceDao.deleteAll(referenceDao.findByExample(reference));

		// 参照先の登録
		registReferences(entity, referenceList);

		// マニュアルの削除
		Manual manual = new Manual();
		manual.setKnowledgeId(entity.getKnowledgeId());
		manual.setCompanyId(entity.getCompanyId());
		manualDao.deleteAll(manualDao.findByExample(manual));

		// マニュアルの登録
		registManuals(entity, manualList);

		return entity;
	}


	/**
	 * タグテーブルとタグ関連テーブルの登録を行う
	 * @param entity ナレッジエンティティ
	 * @param tagNames タグ名称
	 */
	private void registTags (Knowledge entity, List<String> tagNames) {
		// 現在日時の取得
		Date now = new Date();

		// タグの登録
		// 重複しているタグは削除する
		tagNames = tagNames.stream().distinct().collect(Collectors.toList());
		for (String tagName : tagNames) {
			// タグの存在チェック
			Tag tagEntity = new Tag();
			tagEntity.setCompanyId(entity.getCompanyId());
			tagEntity.setTagName(tagName);
			List<Tag> tagList = tagDao.findByExample(tagEntity);

			// 同じタグ名称のタグが無い場合はタグテーブルに登録する
			if (tagList == null || tagList.size() != 1) {
				tagEntity.setCreateDate(now);
				tagEntity.setCreateUserId(entity.getUpdateUserId());
				tagEntity.setCreateUserName(entity.getUpdateUserName());
				tagEntity.setUpdateDate(now);
				tagEntity.setUpdateUserId(entity.getUpdateUserId());
				tagEntity.setUpdateUserName(entity.getUpdateUserName());
				tagDao.save(tagEntity);
			} else {
				tagEntity = tagList.get(0);
			}

			// ナレッジタグ関連テーブル登録
			KnowledgeTag knowledgeTagEntity = new KnowledgeTag();
			knowledgeTagEntity.setKnowledgeId(entity.getKnowledgeId());
			knowledgeTagEntity.setTagId(tagEntity.getTagId());
			knowledgeTagEntity.setCreateDate(now);
			knowledgeTagEntity.setCreateUserId(entity.getUpdateUserId());
			knowledgeTagEntity.setCreateUserName(entity.getUpdateUserName());
			knowledgeTagEntity.setUpdateDate(now);
			knowledgeTagDao.save(knowledgeTagEntity);
		}
	}

	/**
	 * 参照先の登録を行う
	 * @param entity ナレッジエンティティ
	 * @param references 参照URLリスト
	 */
	private void registReferences (Knowledge entity, List<String> references) {
		// 現在日時の取得
		Date now = new Date();

		// 参照先の登録
		for (String reference : references) {

			if ( reference != null && reference.length() >  0 ) {
				Reference referenceEntity = new Reference();
				referenceEntity.setCompanyId(entity.getCompanyId());
				referenceEntity.setKnowledgeId(entity.getKnowledgeId());
				referenceEntity.setReferenceUrl(reference);
				referenceEntity.setCreateDate(now);
				referenceEntity.setCreateUserId(entity.getUpdateUserId());
				referenceEntity.setCreateUserName(entity.getUpdateUserName());
				referenceEntity.setUpdateDate(now);
				referenceEntity.setUpdateUserId(entity.getUpdateUserId());
				referenceEntity.setUpdateUserName(entity.getUpdateUserName());
				referenceDao.save(referenceEntity);
			}
		}
	}

	/**
	 * マニュアルの登録を行う
	 * @param entity ナレッジエンティティ
	 * @param tagNames マニュアルリスト
	 */
	private void registManuals (Knowledge entity, List<Manual> manuals) {
		// 現在日時の取得
		Date now = new Date();

		// マニュアルの登録
		for (Manual manual : manuals) {
			if ( manual != null ) {
				if ( ( manual.getManualName() != null && manual.getManualName().length() >  0 ) ||
						( manual.getManualPage() != null && manual.getManualPage().length() >  0 ) ||
							( manual.getManualUrl() != null && manual.getManualUrl().length() >  0 )
					) {
					Manual manualEntity = new Manual();
					manualEntity.setCompanyId(entity.getCompanyId());
					manualEntity.setKnowledgeId(entity.getKnowledgeId());
					manualEntity.setManualName(manual.getManualName());
					manualEntity.setManualPage(manual.getManualPage());
					manualEntity.setManualUrl(manual.getManualUrl());
					manualEntity.setCreateDate(now);
					manualEntity.setCreateUserId(entity.getUpdateUserId());
					manualEntity.setCreateUserName(entity.getUpdateUserName());
					manualEntity.setUpdateDate(now);
					manualEntity.setUpdateUserId(entity.getUpdateUserId());
					manualEntity.setUpdateUserName(entity.getUpdateUserName());
					manualDao.save(manualEntity);
				}
			}
		}
	}

	/**
	 * 参照回数インクリメント
	 * @param id ナレッジID
	 * @param companyId 企業ID
	 */
	public void increment( Long knowledgeId, String companyId ) {
		knowledgeMapper.incrementClickCount(knowledgeId, companyId);
	}

	/**
	 * 削除
	 * @param id エンティティの識別ID
	 */
	public void delete( Serializable id ) {
		this.knowledgeDao.delete( this.knowledgeDao.get(id) );

		// ナレッジタグ関連テーブルの削除
		KnowledgeTag examaple = new KnowledgeTag();
		examaple.setKnowledgeId((Long) id);
		knowledgeTagDao.deleteAll(knowledgeTagDao.findByExample(examaple));

		// 参照先の削除
		Reference reference = new Reference();
		reference.setKnowledgeId(this.knowledgeDao.get(id).getKnowledgeId());
		reference.setCompanyId(this.knowledgeDao.get(id).getCompanyId());
		referenceDao.deleteAll(referenceDao.findByExample(reference));

		// マニュアルの削除
		Manual manual = new Manual();
		manual.setKnowledgeId(this.knowledgeDao.get(id).getKnowledgeId());
		manual.setCompanyId(this.knowledgeDao.get(id).getCompanyId());
		manualDao.deleteAll(manualDao.findByExample(manual));
	}

	/**
	 * CSVインポート
	 * @param companyId
	 * @param editForms
	 */
	public void importKnowledge( String companyId, List<EditForm> editForms ) {

		// 編集フォームリストが空の場合は処理しない
		if (editForms == null || editForms.isEmpty()) return;

		// 企業IDに紐付くナレッジ、参照先、マニュアル、タグ、ナレッジタグ関連テーブルのデータを削除する
		knowledgeMapper.deleteByCompanyId(companyId);
		tagService.deleteAllTags(companyId);
		referenceService.deleteAllReferences(companyId);
		manualService.deleteAllManuals(companyId);

		// ナレッジテーブル登録
		long knowledgeNo = 1L;
		for(EditForm each : editForms) {
			each.getKnowledge().setKnowledgeNo(knowledgeNo++);
			knowledgeDao.saveNoFlush(each.getKnowledge());
		}
		knowledgeDao.flush();

		// ユーザIDとユーザ名を取得
		Knowledge knowledge = editForms.get(0).getKnowledge();
		String userId = knowledge.getCreateUserId();
		String userName = knowledge.getCreateUserName();


		// 大文字小文字区別なくタグを重複削除して登録
		List<String> tags = editForms.stream()
			.map(each -> each.getTag())
			.collect(Collectors.toList())
			.stream()
			.flatMap( v -> v.stream())
			.filter(new ConcurrentSkipListSet <> (String.CASE_INSENSITIVE_ORDER)::add)
			.distinct()
			.collect(Collectors.toList());

		Map<String, Long> tagMap = new HashMap<>();

		// 現在日時の取得
		Date now = new Date();
		tags.stream().forEach(each -> {
			// 同じタグ名称のタグが無い場合はタグテーブルに登録する
			Tag tag = new Tag();
			tag.setCompanyId(companyId);
			tag.setTagName(each);
			tag.setCreateDate(now);
			tag.setCreateUserId(userId);
			tag.setCreateUserName(userName);
			tag.setUpdateDate(now);
			tag.setUpdateUserId(userId);
			tag.setUpdateUserName(userName);
			tagDao.saveNoFlush(tag);
			tagMap.put(tag.getTagName().toUpperCase(), tag.getTagId());
		});
		tagDao.flush();

		// タグ関連テーブルの登録
		editForms.stream().forEach(each -> {
			each.getTag().stream().distinct().forEach(tag -> {
				// ナレッジ、タグ関連テーブル登録
				KnowledgeTag knowledgeTagEntity = new KnowledgeTag();
				knowledgeTagEntity.setKnowledgeId(each.getKnowledge().getKnowledgeId());
				knowledgeTagEntity.setTagId(tagMap.get(tag.toUpperCase()));
				knowledgeTagEntity.setCreateDate(now);
				knowledgeTagEntity.setCreateUserId(userId);
				knowledgeTagEntity.setCreateUserName(userName);
				knowledgeTagEntity.setUpdateDate(now);
				knowledgeTagDao.saveNoFlush(knowledgeTagEntity);
			});
		});
		knowledgeTagDao.flush();

		// 参照先の登録
		for(EditForm each : editForms) {

			for (String refeach : each.getReference()) {
				if ( refeach != null && refeach.length() >  0 ) {
					Reference reference = new Reference();
					reference.setCompanyId(companyId);
					reference.setKnowledgeId(each.getKnowledge().getKnowledgeId());
					reference.setReferenceUrl(refeach);
					reference.setCreateDate(now);
					reference.setCreateUserId(userId);
					reference.setCreateUserName(userName);
					reference.setUpdateDate(now);
					reference.setUpdateUserId(userId);
					reference.setUpdateUserName(userName);
					referenceDao.saveNoFlush(reference);
				}
				referenceDao.flush();
			}
		}

		// マニュアルの登録
		for(EditForm each : editForms) {
			for (Manual meach : each.getManual()) {
				if ( meach != null ) {
					if ( ( meach.getManualName() != null && meach.getManualName().length() >  0 ) ||
							( meach.getManualPage() != null && meach.getManualPage().length() >  0 ) ||
								( meach.getManualUrl() != null && meach.getManualUrl().length() >  0 )
						) {
						Manual manual = new Manual();
						manual.setCompanyId(companyId);
						manual.setKnowledgeId(each.getKnowledge().getKnowledgeId());
						manual.setManualName(meach.getManualName());
						manual.setManualPage(meach.getManualPage());
						manual.setManualUrl(meach.getManualUrl());
						manual.setCreateDate(now);
						manual.setCreateUserId(userId);
						manual.setCreateUserName(userName);
						manual.setUpdateDate(now);
						manual.setUpdateUserId(userId);
						manual.setUpdateUserName(userName);
						manualDao.saveNoFlush(manual);
					}
				}
				manualDao.flush();
			}
		}
	}
}