/*
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 */

package cloud.optim.callcentersolution.api.app.inquiryform;

import cloud.optim.callcentersolution.api.app.companymanagement.CompanyManagementService;
import cloud.optim.callcentersolution.api.app.knowledge.KnowledgeService;
import cloud.optim.callcentersolution.api.entity.Knowledge;
import cloud.optim.callcentersolution.core.common.utility.HankakuKanaConverter;
import cloud.optim.callcentersolution.core.modules.rest.ResponseCode;
import cloud.optim.callcentersolution.core.modules.rest.RestException;
import cloud.optim.callcentersolution.core.modules.rest.RestResult;
import cloud.optim.callcentersolution.core.modules.rest.RestValidatorUtils;
import cloud.optim.callcentersolution.core.modules.rest.SortForm;
import cloud.optim.callcentersolution.core.modules.rest.SortForm.SortElement;
import java.util.ArrayList;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * InquiryFormRestServiceのバリデータクラス.
 * <p>入力チェックと入力内容の補完をおこなう.</p>
 */
@Component
class InquiryFormRestValidator {

  /** Commons Logging instance. */
  @SuppressWarnings("unused")
  private Log log = LogFactory.getLog(this.getClass());

  @Resource
  private CompanyManagementService companyManagementService;

  @Resource
  private KnowledgeService knowledgeService;

  /** ナレッジ検索取得件数. */
  @Value("${inquiryform.knowledge.result.count}")
  private long knowledgeResultCount;

  /**
   * 検索の入力チェックと入力内容の補完.
   *
   * @param req 入力内容
   */
  public void validateForSearch(InquiryFormRequest req) {
    String name = "#request";
    if (req == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    name = "#searchForm";
    SearchForm searchForm = req.getSearchForm();
    if (searchForm == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    name = "#inquiryFormSearchForm";
    InquiryFormSearchForm inquiryFormSearchForm = searchForm.getInquiryFormSearchForm();
    if (inquiryFormSearchForm == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    name = "#inquiryFormSearchForm.hashedCompanyId";
    String hashedCompanyId = inquiryFormSearchForm.getHashedCompanyId();
    if (hashedCompanyId == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    // ----- 企業ID存在チェック
    String companyId = companyManagementService.getCompanyIdByHash(hashedCompanyId);
    if (StringUtils.isEmpty(companyId)) {
      throw new RestException(new RestResult(ResponseCode.NOT_FOUND, null, name));
    }
    inquiryFormSearchForm.setCompanyId(companyId);

    // ----- 参照URL設定有無
    // TODO 現時点では不要な条件だが必要かも？
    //name = "#knowledge.notNullUrl";
    //String value = knowledgeForm.getNotNullUrl();
    //knowledgeForm.setNotNullUrl(URL_ON.equals(value) ? URL_ON : URL_OFF);

    searchForm.setInquiryFormSearchForm(inquiryFormSearchForm);

    // ----- ソート条件
    SortForm sortForm = new SortForm();
    sortForm.setSortElement(new ArrayList<>());
    sortForm.addSortElement(new SortElement("knowledge.clickCount", false));
    sortForm.addSortElement(new SortElement("knowledge.knowledgeId", true));
    //sortForm.addSortElement(new SortElement("knowledge.updateDate", false));

    // ナレッジ検索のクエリは上限超過をチェックするため設定された上限値+1で検索をするので
    // ここで設定するする値は-1をする
    sortForm.setMaxResult(this.knowledgeResultCount - 1);// ファイルからデフォルトを設定
    sortForm.setOffset(null); // オフセットはとりあえずNULLを設定(今後使用する場合は要削除)

    RestValidatorUtils.sortValidate(sortForm);
    RestValidatorUtils.sortConvert(sortForm);

    searchForm.setSortForm(sortForm);

    req.setSearchForm(searchForm);
  }


  // -------------------------------------------------------------------------

  /**
   * Knowledge 取得の入力チェックと入力内容の補完.
   *
   * @param req 入力内容
   */
  public void validateForGet(InquiryFormRequest req) {
    String name = "#request";
    if (req == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    name = "#editForm";
    cloud.optim.callcentersolution.api.app.inquiryform.InquiryFormRequest.EditForm editForm =
        req.getEditForm();
    if (editForm == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    name = "#editForm.hashedCompanyId";
    String hashedCompanyId = editForm.getHashedCompanyId();
    if (hashedCompanyId == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }
    String companyId = this.companyManagementService.getCompanyIdByHash(hashedCompanyId);
    if (StringUtils.isEmpty(companyId)) {
      throw new RestException(new RestResult(ResponseCode.NOT_FOUND, null, name, companyId));
    }

    name = "#knowledge";
    Knowledge knowledge = editForm.getKnowledge();
    if (knowledge == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    // ----- PK
    name = "#knowledge.knowledgeId";
    Long pk = knowledge.getKnowledgeId();
    RestValidatorUtils.fieldValidate(name, pk, true, null, null);

    // 存在チェック
    Knowledge entity = knowledgeService.getKnowledge(pk, companyId);
    if (entity == null) {
      throw new RestException(new RestResult(ResponseCode.NOT_FOUND, null, name, companyId));
    }

    req.getEditForm().setKnowledge(entity);
  }

  // -------------------------------------------------------------------------

  /**
   * 参照回数更新の入力チェックと入力内容の補完.
   *
   * @param req 入力内容
   */
  public void validateForIncrement(InquiryFormRequest req) {
    String name = "#request";
    if (req == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    name = "#editForm";
    cloud.optim.callcentersolution.api.app.inquiryform.InquiryFormRequest.EditForm editForm =
        req.getEditForm();
    if (editForm == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    name = "#editForm.hashedCompanyId";
    String hashedCompanyId = editForm.getHashedCompanyId();
    if (hashedCompanyId == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }
    String companyId = this.companyManagementService.getCompanyIdByHash(hashedCompanyId);
    if (StringUtils.isEmpty(companyId)) {
      throw new RestException(new RestResult(ResponseCode.NOT_FOUND, null, name, companyId));
    }

    name = "#knowledge";
    Knowledge knowledge = editForm.getKnowledge();
    if (knowledge == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    // ----- PK
    name = "#knowledge.knowledgeId";
    Long pk = knowledge.getKnowledgeId();

    RestValidatorUtils.fieldValidate(name, pk, true, null, null);
    RestValidatorUtils.fieldValidate(name, String.valueOf(pk), false, null, 19);

    // 存在チェック
    Knowledge entity = knowledgeService.getKnowledge(pk, companyId);
    if (entity == null) {
      throw new RestException(new RestResult(ResponseCode.NOT_FOUND, null, name, companyId));
    }

    req.getEditForm().setKnowledge(entity);
  }

  /**
   * ナレッジキーワード抽出の入力チェック.
   *
   * @param req 入力内容
   */
  public void validateForKnowledgeKeyword(InquiryFormRequest req) {
    String name = "#request";
    if (req == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    name = "#searchForm";
    SearchForm searchForm = req.getSearchForm();
    if (searchForm == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    name = "#inquiryFormSearchForm";
    InquiryFormSearchForm inquiryFormSearchForm = searchForm.getInquiryFormSearchForm();
    if (inquiryFormSearchForm == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    name = "#inquiryFormSearchForm.hashedCompanyId";
    String hashedCompanyId = inquiryFormSearchForm.getHashedCompanyId();
    if (hashedCompanyId == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    // ----- 企業ID存在チェック
    String companyId = companyManagementService.getCompanyIdByHash(hashedCompanyId);
    if (StringUtils.isEmpty(companyId)) {
      throw new RestException(new RestResult(ResponseCode.NOT_FOUND, null, name));
    }
    inquiryFormSearchForm.setCompanyId(companyId);

    // ----- 解析テキスト
    String text = inquiryFormSearchForm.getText();
    if (StringUtils.isNotEmpty(text)) {
      text = HankakuKanaConverter.convert(text);
      inquiryFormSearchForm.setText(text);
    }

    searchForm.setInquiryFormSearchForm(inquiryFormSearchForm);

    // ----- ソート条件
    SortForm sortForm = new SortForm();
    sortForm.setSortElement(new ArrayList<>());
    sortForm.addSortElement(new SortElement("knowledge.clickCount", false));
    sortForm.addSortElement(new SortElement("knowledge.knowledgeId", true));
    //sortForm.addSortElement(new SortElement("knowledge.updateDate", false));
    sortForm.setMaxResult(this.knowledgeResultCount); // ファイルからデフォルトを設定
    sortForm.setOffset(null); // オフセットはとりあえずNULLを設定(今後使用する場合は要削除)

    RestValidatorUtils.sortValidate(sortForm);
    RestValidatorUtils.sortConvert(sortForm);

    searchForm.setSortForm(sortForm);

    req.setSearchForm(searchForm);
  }

  /**
   * 企業ID存在判定の入力チェックと入力内容の補完.
   *
   * @param req 入力内容
   */
  public void validateForContains(InquiryFormRequest req) {
    String name = "#request";
    if (req == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    name = "#editForm";
    cloud.optim.callcentersolution.api.app.inquiryform.InquiryFormRequest.EditForm editForm =
        req.getEditForm();
    if (editForm == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    name = "#editForm.hashedCompanyId";
    String hashedCompanyId = editForm.getHashedCompanyId();
    if (hashedCompanyId == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }
    String companyId = this.companyManagementService.getCompanyIdByHash(hashedCompanyId);
    if (StringUtils.isEmpty(companyId)) {
      throw new RestException(new RestResult(ResponseCode.NOT_FOUND, null, name, companyId));
    }
  }

}
