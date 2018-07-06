/*
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 */

package cloud.optim.callcentersolution.api.app.inquiryform;

import cloud.optim.callcentersolution.api.app.companymanagement.CompanyManagementService;
import cloud.optim.callcentersolution.api.app.keyword.KeywordResponse;
import cloud.optim.callcentersolution.api.app.keyword.KeywordService;
import cloud.optim.callcentersolution.api.app.knowledge.KnowledgeResponse;
import cloud.optim.callcentersolution.api.app.knowledge.KnowledgeSearchForm;
import cloud.optim.callcentersolution.api.app.knowledge.KnowledgeSearchResult;
import cloud.optim.callcentersolution.api.app.knowledge.KnowledgeService;
import cloud.optim.callcentersolution.api.app.manual.ManualService;
import cloud.optim.callcentersolution.api.app.reference.ReferenceService;
import cloud.optim.callcentersolution.api.app.tag.TagService;
import cloud.optim.callcentersolution.api.entity.Knowledge;
//import cloud.optim.callcentersolution.api.entity.Manual;
import cloud.optim.callcentersolution.api.entity.Reference;
//import cloud.optim.callcentersolution.api.entity.Tag;
import cloud.optim.callcentersolution.core.modules.rest.ExceptionUtil;
import cloud.optim.callcentersolution.core.modules.rest.MessageUtility;
import cloud.optim.callcentersolution.core.modules.rest.ResponseCode;
import cloud.optim.callcentersolution.core.modules.rest.RestException;
import cloud.optim.callcentersolution.core.modules.rest.RestLog;
import cloud.optim.callcentersolution.core.modules.rest.RestResult;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * InquiryFormRestService 実装.
 */
@Path("/inquiryform")
@Consumes({"application/json", "application/xml"})
@Produces({"application/json", "application/xml"})
@Component
public class InquiryFormRestService {

  private Log log = LogFactory.getLog(this.getClass());

  @Resource
  private RestLog restlog;

  /** PK の項目名. */
  private static final String NAME_PK = "#knowledge.knowledgeId";

  // -------------------------------------------------------------------------

  @Resource
  private InquiryFormRestValidator validator;

  @Resource
  private KnowledgeService knowledgeService;

  @Resource
  private TagService tagService;

  @Resource
  private ReferenceService referrenceService;

  @Resource
  private ManualService manualService;

  @Resource
  private KeywordService keywordService;

  @Resource
  private CompanyManagementService companyManagementService;

  @Resource
  private MessageUtility messageUtility;

  // -------------------------------------------------------------------------

  /** ナレッジキーワード取得件数. */
  @Value("${inquiryform.knowledge.keyword.result.count}")
  private int knowledgeKeywordResultCount;

  // -------------------------------------------------------------------------

  /**
   * ナレッジ検索.
   *
   * @param req 検索条件
   *
   * @return 検索結果
   */
  @POST
  @Path("/search")
  public KnowledgeResponse search(InquiryFormRequest req) {
    final String mName = "search";
    this.restlog.start(log, mName, req);

    try {
      // ----- 入力チェック
      if (req == null) { // 検索条件の指定がない場合は全検索として扱う
        req = new InquiryFormRequest();
      }
      this.validator.validateForSearch(req);

      // ----- 検索
      SearchForm form = req.getSearchForm();
      List<cloud.optim.callcentersolution.api.app.knowledge.SearchResult> list =
          this.knowledgeService.search(convert2Knowledge(form));

      // ----- レスポンス作成
      KnowledgeResponse res = new KnowledgeResponse();
      res.setResult(new RestResult(ResponseCode.OK));
      res.setSearchResultList(convert2InquiryFormResponse(list));

      this.messageUtility.fillMessage(res.getResultList());
      this.restlog.end(log, mName, req, res, res.getResultList());

      return res;
    } catch (Exception e) {
      throw ExceptionUtil.handleException(log, ResponseCode.SYS_ERROR, null, null, null, e);
    }
  }

  // -------------------------------------------------------------------------

  /**
   * ナレッジ取得.
   *
   * @param req 取得条件（PK 項目のみ使用）
   *
   * @return 取得エンティティ
   */
  @POST
  @Path("/get")
  public KnowledgeResponse get(InquiryFormRequest req) {
    final String mName = "get";
    this.restlog.start(log, mName, req);

    try {
      // ----- 入力チェック
      this.validator.validateForGet(req);

      // ----- 入力データ取得
      final Long knowledgeId = req.getEditForm().getKnowledge().getKnowledgeId();
      final String companyId = req.getEditForm().getKnowledge().getCompanyId();

      // ----- 取得
      Knowledge entity = this.knowledgeService.getKnowledge(knowledgeId, companyId);
      if (entity == null) {
        throw new RestException(
            new RestResult(ResponseCode.NOT_FOUND, null, NAME_PK, knowledgeId));
      }
      List<Reference> referenceList =
          this.referrenceService.searchReferenceByKnowledgeId(knowledgeId, companyId);
      /* 不要らしいので取らない、セットしない。
      List<Manual> manualList =
          this.manualService.searchManualByKnowledgeId(knowledgeId, companyId);
      List<Tag> tagList = this.tagService.searchTagByKnowledgeId(knowledgeId, companyId);
      */

      // ----- レスポンス作成
      KnowledgeResponse res = new KnowledgeResponse();
      res.setResult(new RestResult(ResponseCode.OK));
      res.setEditResult(
          new cloud.optim.callcentersolution.api.app.knowledge.KnowledgeResponse.EditResult());
      res.getEditResult().setKnowledge(convert2InquiryFormResponse(entity));
      res.getEditResult().setReference(convert2InquiryFormResponseReference(referenceList));
      //res.getEditResult().setManual(convert2InquiryFormResponseManual(manualList));
      //res.getEditResult().setTag(convert2InquiryFormResponseTag(tagList));

      this.messageUtility.fillMessage(res.getResultList());
      this.restlog.end(log, mName, req, res, res.getResultList());

      return res;
    } catch (Exception e) {
      throw ExceptionUtil.handleException(log, ResponseCode.SYS_ERROR, null, null, null, e);
    }
  }

  // -------------------------------------------------------------------------

  /**
   * 参照回数更新.
   *
   * @param req 更新内容
   *
   * @return 処理結果
   */
  @POST
  @Path("/increment")
  public KnowledgeResponse increment(InquiryFormRequest req) {
    final String mName = "increment";
    this.restlog.start(log, mName, req);

    try {
      // ----- 入力チェック
      this.validator.validateForIncrement(req);

      // ----- 入力データ取得
      final Long knowledgeId = req.getEditForm().getKnowledge().getKnowledgeId();
      final String companyId = req.getEditForm().getKnowledge().getCompanyId();

      // ----- 更新
      this.knowledgeService.increment(knowledgeId, companyId);

      // ----- レスポンス作成
      KnowledgeResponse res = new KnowledgeResponse();
      res.setResult(new RestResult(ResponseCode.OK));

      this.messageUtility.fillMessage(res.getResultList());
      this.restlog.end(log, mName, req, res, res.getResultList());

      return res;
    } catch (Exception e) {
      throw ExceptionUtil.handleException(log, ResponseCode.SYS_ERROR, null, null, null, e);
    }
  }

  /**
   * ナレッジキーワード抽出.
   *
   * @param req 抽出対象テキスト
   * @return 抽出結果
   */
  @POST
  @Path("/knowledgeKeyword")
  public KeywordResponse knowledgeKeyword(InquiryFormRequest req) {
    final String mName = "knowledgeKeyword";
    this.restlog.start(log, mName, req);

    try {
      // 入力チェック
      this.validator.validateForKnowledgeKeyword(req);

      // ----- 抽出
      final String companyId = req.getSearchForm().getInquiryFormSearchForm().getCompanyId();
      final String text = req.getSearchForm().getInquiryFormSearchForm().getText();
      List<cloud.optim.callcentersolution.api.app.keyword.SearchResult> list = this.keywordService
          .getKnowledgeKeywords(companyId, text, this.knowledgeKeywordResultCount);

      // ----- レスポンス作成
      KeywordResponse res = new KeywordResponse();
      res.setResult(new RestResult(ResponseCode.OK));
      res.setSearchResultList(list);

      this.messageUtility.fillMessage(res.getResultList());
      this.restlog.end(log, mName, req, res, res.getResultList());

      return res;
    } catch (Exception e) {
      throw ExceptionUtil.handleException(log, ResponseCode.SYS_ERROR, null, null, null, e);
    }
  }

  /**
   * 問い合わせ画面検索フォーム→ナレッジ検索フォーム変換.
   * @param searchForm 問い合わせ画面検索フォーム
   * @return ナレッジ検索フォーム
   */
  private cloud.optim.callcentersolution.api.app.knowledge.SearchForm convert2Knowledge(
      final SearchForm searchForm) {
    InquiryFormSearchForm inquiryFormSearchForm = searchForm.getInquiryFormSearchForm();
    cloud.optim.callcentersolution.api.app.knowledge.SearchForm result =
        new cloud.optim.callcentersolution.api.app.knowledge.SearchForm();
    KnowledgeSearchForm knowledgeSearchForm = new KnowledgeSearchForm();
    knowledgeSearchForm.setCompanyId(inquiryFormSearchForm.getCompanyId());
    knowledgeSearchForm.setTagId(inquiryFormSearchForm.getTagId());
    result.setKnowledge(knowledgeSearchForm);
    result.setSortForm(searchForm.getSortForm());
    return result;
  }

  /**
   * ナレッジ検索で返却されるナレッジのインスタンスのメンバーのうち、
   * 不要なメンバーをレスポンスから取り除く.
   *
   * @param searchResult ナレッジ検索で返却されたインスタンス
   * @return 不要なメンバーを除外したインスタンス
   */
  private cloud.optim.callcentersolution.api.app.knowledge.SearchResult
      convert2InquiryFormResponse(
          cloud.optim.callcentersolution.api.app.knowledge.SearchResult searchResult) {
    KnowledgeSearchResult dest = new KnowledgeSearchResult();
    try {
      BeanUtils.copyProperties(dest, searchResult.getKnowledge());
    } catch (IllegalAccessException | InvocationTargetException e) {
      return null;
    }
    dest.setCompanyId(null);
    dest.setContent(null);
    dest.setScript(null);
    dest.setCreateDate(null);
    dest.setCreateUserId(null);
    dest.setCreateUserName(null);
    dest.setUpdateDate(null);
    dest.setUpdateUserId(null);
    dest.setUpdateUserName(null);
    searchResult.setKnowledge(dest);
    return searchResult;
  }

  /**
   * ナレッジ検索で返却されるナレッジのインスタンスのメンバーのうち、
   * 不要なメンバーをレスポンスから取り除く.
   * <p>コレクション版.</p>
   *
   * @param searchResult ナレッジ検索で返却されたコレクション
   * @return 不要なメンバーを除外したコレクション
   */
  private List<cloud.optim.callcentersolution.api.app.knowledge.SearchResult>
      convert2InquiryFormResponse(
          List<cloud.optim.callcentersolution.api.app.knowledge.SearchResult> searchResults) {
    List<cloud.optim.callcentersolution.api.app.knowledge.SearchResult> result =
        new ArrayList<cloud.optim.callcentersolution.api.app.knowledge.SearchResult>();
    // TODO ↓ write java8 lambda.
    for (cloud.optim.callcentersolution.api.app.knowledge.SearchResult knowledgeSearchResult : searchResults) {
      result.add(convert2InquiryFormResponse(knowledgeSearchResult));
    }
    return result;
  }

  /**
   * ナレッジ取得で返却されるナレッジのエンティティのメンバーのうち、
   * 不要なメンバーをレスポンスから取り除く.
   *
   * @param entity ナレッジ取得で返却されたエンティティ
   * @return 不要なメンバーを除外したエンティティ
   */
  private Knowledge convert2InquiryFormResponse(Knowledge entity) {
    entity.setCompanyId(null);
    entity.setTitle(null);
    entity.setScript(null);
    entity.setClickCount(null);
    entity.setCreateDate(null);
    entity.setCreateUserId(null);
    entity.setCreateUserName(null);
    entity.setUpdateDate(null);
    entity.setUpdateUserId(null);
    entity.setUpdateUserName(null);
    return entity;
  }

  /**
   * ナレッジ取得で返却される参照先のインスタンスのメンバーのうち、
   * 不要なメンバーをレスポンスから取り除く.
   *
   * @param reference 対象インスタンス
   * @return 不要なメンバーを除外したインスタンス
   */
  private Reference convert2InquiryFormResponseReference(Reference reference) {
    reference.setKnowledgeId(null);
    reference.setCompanyId(null);
    reference.setCreateDate(null);
    reference.setCreateUserId(null);
    reference.setCreateUserName(null);
    reference.setUpdateDate(null);
    reference.setUpdateUserId(null);
    reference.setUpdateUserName(null);
    return reference;
  }

  /*
   * ナレッジ取得で返却されるマニュアルのインスタンスのメンバーのうち、
   * 不要なメンバーをレスポンスから取り除く.
   *
   * @param manual 対象インスタンス
   * @return 不要なメンバーを除外したインスタンス
   */
  /*
  private Manual convert2InquiryFormResponseManual(Manual manual) {
    manual.setKnowledgeId(null);
    manual.setCompanyId(null);
    manual.setCreateDate(null);
    manual.setCreateUserId(null);
    manual.setCreateUserName(null);
    manual.setUpdateDate(null);
    manual.setUpdateUserId(null);
    manual.setUpdateUserName(null);
    return manual;
  }
  */

  /*
   * ナレッジ取得で返却されるタグのインスタンスのメンバーのうち、
   * 不要なメンバーをレスポンスから取り除く.
   *
   * @param tag 対象インスタンス
   * @return 不要なメンバーを除外したインスタンス
   */
  /*
  private Tag convert2InquiryFormResponseTag(Tag tag) {
    tag.setCompanyId(null);
    tag.setCreateDate(null);
    tag.setCreateUserId(null);
    tag.setCreateUserName(null);
    tag.setUpdateDate(null);
    tag.setUpdateUserId(null);
    tag.setUpdateUserName(null);
    return tag;
  }
  */

  /**
   * ナレッジ取得で返却される参照先のインスタンスのメンバーのうち、
   * 不要なメンバーをレスポンスから取り除く.
   * <p>コレクション版.</p>
   *
   * @param referenceList ナレッジ検索で返却された参照先のコレクション
   * @return 不要なメンバーを除外したコレクション
   */
  private List<Reference> convert2InquiryFormResponseReference(
      final List<Reference> referenceList) {
    List<Reference> result = new ArrayList<Reference>();
    // TODO ↓ write java8 lambda.
    for (Reference reference : referenceList) {
      result.add(convert2InquiryFormResponseReference(reference));
    }
    return result;
  }

  /*
   * ナレッジ取得で返却される参照先のインスタンスのメンバーのうち、
   * 不要なメンバーをレスポンスから取り除く.
   * <p>コレクション版.</p>
   *
   * @param referenceList ナレッジ検索で返却された参照先のコレクション
   * @return 不要なメンバーを除外したコレクション
   */
  /*
  private  List<Manual> convert2InquiryFormResponseManual(final List<Manual> manualList) {
    List<Manual> result = new ArrayList<Manual>();
    // TODO ↓ write java8 lambda.
    for (Manual manual : manualList) {
      result.add(convert2InquiryFormResponseManual(manual));
    }
    return result;
  }
  */

  /*
   * ナレッジ取得で返却されるタグのインスタンスのメンバーのうち、
   * 不要なメンバーをレスポンスから取り除く.
   * <p>コレクション版.</p>
   *
   * @param tagList ナレッジ検索で返却されたタグのコレクション
   * @return 不要なメンバーを除外したコレクション
   */
  /*
  private  List<Tag> convert2InquiryFormResponseTag(final List<Tag> tagList) {
    List<Tag> result = new ArrayList<Tag>();
    // TODO ↓ write java8 lambda.
    for (Tag tag : tagList) {
      result.add(convert2InquiryFormResponseTag(tag));
    }
    return result;
  }
  */

  /**
   * 企業ID存在判定.
   *
   * @param req 抽出対象テキスト
   * @return 抽出結果
   */
  @POST
  @Path("/contains")
  public InquiryFormResponse contains(InquiryFormRequest req) {
    final String mName = "contains";
    this.restlog.start(log, mName, req);

    try {
      this.validator.validateForContains(req);

      InquiryFormResponse res = new InquiryFormResponse();
      res.setResult(new RestResult(ResponseCode.OK));

      this.messageUtility.fillMessage(res.getResultList());
      this.restlog.end(log, mName, req, res, res.getResultList());

      return res;
    } catch (Exception e) {
      throw ExceptionUtil.handleException(log, ResponseCode.SYS_ERROR, null, null, null, e);
    }
  }

}
