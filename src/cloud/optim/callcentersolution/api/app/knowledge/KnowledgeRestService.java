/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：KnowledgeRestService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.knowledge;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListReader;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.AlwaysQuoteMode;

import cloud.optim.callcentersolution.api.app.knowledge.KnowledgeRequest.EditForm;
import cloud.optim.callcentersolution.api.app.knowledge.KnowledgeResponse.BulkResult;
import cloud.optim.callcentersolution.api.app.knowledge.KnowledgeResponse.EditResult;
import cloud.optim.callcentersolution.api.app.manual.ManualService;
import cloud.optim.callcentersolution.api.app.reference.ReferenceService;
import cloud.optim.callcentersolution.api.app.tag.TagService;
import cloud.optim.callcentersolution.api.entity.Knowledge;
import cloud.optim.callcentersolution.api.entity.Manual;
import cloud.optim.callcentersolution.api.entity.Reference;
import cloud.optim.callcentersolution.api.entity.Tag;
import cloud.optim.callcentersolution.api.util.ExtractUtil;
import cloud.optim.callcentersolution.core.modules.loginutil.CustomUser;
import cloud.optim.callcentersolution.core.modules.loginutil.LoginUtility;
import cloud.optim.callcentersolution.core.modules.rest.CustomExceptionMapper.RestResponse;
import cloud.optim.callcentersolution.core.modules.rest.ExceptionUtil;
import cloud.optim.callcentersolution.core.modules.rest.MessageUtility;
import cloud.optim.callcentersolution.core.modules.rest.ResponseCode;
import cloud.optim.callcentersolution.core.modules.rest.RestException;
import cloud.optim.callcentersolution.core.modules.rest.RestLog;
import cloud.optim.callcentersolution.core.modules.rest.RestResult;
import cloud.optim.callcentersolution.core.modules.rest.app.fileupload.FileUploadUtility;

/**
 * KnowledgeRestService 実装.<br/>
 */
@Path( "/knowledge" )
@Consumes( { "application/json", "application/xml" } )
@Produces( { "application/json", "application/xml" } )
@Component
public class KnowledgeRestService
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() );

	/** PK の項目名 */
	private static final String NAME_PK = "#knowledge.knowledgeId";

	// -------------------------------------------------------------------------

	/** バリデータ */
	@Resource private KnowledgeRestValidator validator;

	/** KnowledgeService */
	@Resource private KnowledgeService knowledgeService;

	/** TagService */
	@Resource private TagService tagService;

	/** ReferenceService */
	@Resource private ReferenceService referenceService;

	/** ManualService */
	@Resource private ManualService manualService;

	/** RestLog */
	@Resource private RestLog restlog;

	/** MessageUtility */
	@Resource private MessageUtility messageUtility;

	/** LoginUtility */
	@Resource private LoginUtility loginUtility;

	/** FileUploadUtility */
	@Resource private FileUploadUtility fileUploadUtility;

	/** ExtractUtil*/
	@Resource private ExtractUtil extractUtil;

	/** インポート返却エラー数上限値 */
	@Value( "${knowledge.import.max.error.count}" )
	private int maxImportErrorCount;

	/** CSVファイルにナレッジ情報のカラム数 */
	private int knowledgeColumnCount = 3;

	/** CSVファイル文字エンコード */
	@Value( "${knowledge.import.char.encoding}" )
	private String csvCharEncoding;

	/** 最大参照先IMPORT/EXPORT数 */
	@Value( "${knowledge.reference.limit}" )
	private int maxReferenceCount;

	/** 最大マニュアルIMPORT/EXPORT数 */
	@Value( "${knowledge.manual.limit}" )
	private int maxManualCount;

	/** ダウンロードエラーページURL */
	@Value( "${download.error.page.url}" )
	private String downloadErrorPageUrl;

	// -------------------------------------------------------------------------

	/**
	 * 検索
	 *
	 * @param req 検索条件
	 *
	 * @return 検索結果
	 */
	@POST
	@Path( "/search" )
	public KnowledgeResponse search( KnowledgeRequest req ) {

		String MNAME = "search";
		restlog.start( log, MNAME, req );

		try {

			KnowledgeResponse res = new KnowledgeResponse();

			// ----- 入力チェック

			// 検索条件の指定がない場合は全検索として扱う

			if ( req == null ) req = new KnowledgeRequest();

			validator.validateForSearch( req );

			// ----- 検索

			SearchForm form = req.getSearchForm();

			List<SearchResult> list = knowledgeService.search( form );



			// ----- レスポンス作成
			Long limit = form.getSortForm().getMaxResult();
			if (list.size() > limit) {
				res.setResult( new RestResult(ResponseCode.TOO_MANY_SEARCH_RESULT, new Object[]{limit}, limit.toString()));
				list = list.subList(0, list.size() - 1);
			} else {
				res.setResult( new RestResult( ResponseCode.OK ) );
			}

			res.setSearchResultList( list );

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * 取得
	 *
	 * @param req 取得条件（PK 項目のみ使用）
	 *
	 * @return 取得エンティティ
	 */
	@POST
	@Path( "/get" )
	public KnowledgeResponse get( KnowledgeRequest req ) {

		String MNAME = "get";
		restlog.start( log, MNAME, req );

		try {

			KnowledgeResponse res = new KnowledgeResponse();

			// ----- 入力チェック

			validator.validateForGet( req );

			// ----- 入力データ取得
			CustomUser customUser = loginUtility.getCustomUser();

			Knowledge knowledge = req.getEditForm().getKnowledge();

			// ----- 取得

			Knowledge entity = knowledgeService.getKnowledge( knowledge.getKnowledgeId(), customUser.getCompanyId() );

			if ( entity == null ) {

				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, NAME_PK, knowledge.getKnowledgeId() ) );
			}

			// ナレッジに紐付くタグを取得する
			List<Tag> tagList = tagService.searchTagByKnowledgeId(entity.getKnowledgeId(), customUser.getCompanyId());

			// ナレッジに紐付く参照先を取得する
			List<Reference> referenceList = referenceService.searchReferenceByKnowledgeId(entity.getKnowledgeId(), customUser.getCompanyId());

			// ナレッジに紐付くマニュアルを取得する
			List<Manual> manualList = manualService.searchManualByKnowledgeId(entity.getKnowledgeId(), customUser.getCompanyId());
			// ----- レスポンス作成

			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setEditResult( new EditResult() );
			res.getEditResult().setKnowledge( entity );
			res.getEditResult().setTag( tagList) ;
			res.getEditResult().setReference( referenceList) ;
			res.getEditResult().setManual( manualList) ;

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * 登録
	 *
	 * @param req 登録内容
	 *
	 * @return 処理結果と登録内容
	 */
	@POST
	@Path( "/put" )
	public KnowledgeResponse put( KnowledgeRequest req ) {

		String MNAME = "put";
		restlog.start( log, MNAME, req );

		try {

			KnowledgeResponse res = new KnowledgeResponse();

			// ----- 入力チェック

			validator.validateForPut( req, true );

			// ----- 入力データ取得

			Knowledge knowledge = req.getEditForm().getKnowledge();
			List<String> tags = req.getEditForm().getTag();
			List<String> references = req.getEditForm().getReference();
			List<Manual> manuals = req.getEditForm().getManual();

			// ----- 登録

			knowledge = knowledgeService.save( knowledge, tags, references, manuals );

			// ----- レスポンス作成

			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setEditResult( new EditResult() );

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * 更新
	 *
	 * @param req 更新内容
	 *
	 * @return 処理結果と更新内容
	 */
	@POST
	@Path( "/update" )
	public KnowledgeResponse update( KnowledgeRequest req ) {

		String MNAME = "update";
		restlog.start( log, MNAME, req );

		try {

			KnowledgeResponse res = new KnowledgeResponse();

			// ----- 入力チェック

			validator.validateForUpdate( req );

			// ----- 入力データ取得

			Knowledge knowledge = req.getEditForm().getKnowledge();
			List<String> tags = req.getEditForm().getTag();
			List<String> references = req.getEditForm().getReference();
			List<Manual> manuals = req.getEditForm().getManual();

			// ----- 更新

			knowledge = knowledgeService.update( knowledge, tags, references, manuals );

			// ナレッジに紐付かなくなったタグを削除する
			tagService.deleteUnlinkedTags(knowledge.getCompanyId());

			// ----- レスポンス作成

			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setEditResult( new EditResult() );

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	/**
	 * 参照回数更新
	 *
	 * @param req 更新内容
	 *
	 * @return 処理結果
	 */
	@POST
	@Path( "/increment" )
	public KnowledgeResponse increment( KnowledgeRequest req ) {

		String MNAME = "increment";
		restlog.start( log, MNAME, req );

		try {

			KnowledgeResponse res = new KnowledgeResponse();

			// ----- 入力チェック

			validator.validateForIncrement( req );

			// ----- 入力データ取得

			CustomUser customUser = loginUtility.getCustomUser();
			Knowledge knowledge = req.getEditForm().getKnowledge();

			// ----- 取得

			knowledgeService.increment( knowledge.getKnowledgeId(), customUser.getCompanyId() );

			// ----- レスポンス作成

			res.setResult( new RestResult( ResponseCode.OK ) );

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}


	// -------------------------------------------------------------------------

	/**
	 * 一括削除
	 *
	 * @param req 取得条件（PK 項目のみ使用）
	 *
	 * @return 処理結果ステータスのみ
	 */
	@POST
	@Path( "/delete" )
	public KnowledgeResponse delete( KnowledgeRequest req ) {

		String MNAME = "delete";
		restlog.start( log, MNAME, req );

		try {

			KnowledgeResponse res = new KnowledgeResponse();
			res.setBulkResultList( new ArrayList<BulkResult>() );

			// ----- 入力チェック

			validator.validateForDelete( req );

			// ----- 入力データ取得

			boolean error = false;

			// ----- 1 件ずつ削除

			for ( SearchResult form : req.getBulkFormList() ) {

				BulkResult result = new BulkResult();

				try {

					deleteOne( form );

					// ----- レスポンス作成

					result.setResult( new RestResult( ResponseCode.OK ) );
					result.setKnowledge( form.getKnowledge() );

					messageUtility.fillMessage( result.getResultList() );
					restlog.endOne( log, MNAME, result, result.getResultList() );
				}
				catch ( Exception ex ) {

					error = true;

					result.setResultList( // 応答結果を作成
						ExceptionUtil.handleException( log, ResponseCode.SYS_ERROR, null, null, null, ex )
						.getRestResultList() );
					result.setKnowledge( form.getKnowledge() );

					messageUtility.fillMessage( result.getResultList() );
					restlog.abortOne( log, MNAME, result, result.getResultList(), ex );
				}

				res.getBulkResultList().add( result );
			}

			// ナレッジに紐付かなくなったタグを削除する
			CustomUser customUser = loginUtility.getCustomUser();
			tagService.deleteUnlinkedTags(customUser.getCompanyId());

			// ----- レスポンス作成

			if ( error ) res.setResult( new RestResult( ResponseCode.PARTIAL ) );
			else res.setResult( new RestResult( ResponseCode.OK ) );

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );

		}
	}

	/**
	 * 1 件削除
	 *
	 * @param form 削除する 1 コンテンツの情報
	 *
	 * @throws Exception エラー発生時
	 */
	private void deleteOne( SearchResult form ) throws Exception {

		// ----- 入力チェック

		validator.validateForDeleteOne( form );

		// ----- 削除

		knowledgeService.delete( form.getKnowledge().getKnowledgeId() );

		// 参照先の削除
		referenceService.deleteReferences(form.getKnowledge().getKnowledgeId(), form.getKnowledge().getCompanyId());

		// マニュアルの削除
		manualService.deleteManuals(form.getKnowledge().getKnowledgeId(), form.getKnowledge().getCompanyId());
	}


	// -------------------------------------------------------------------------

	/**
	 * CSVインポート
	 *
	 * @param req 登録内容
	 *
	 * @return 処理結果と登録内容
	 */
	@POST
	@Path( "/import" )
	@Consumes( "multipart/form-data" )
	@Produces( { "application/json", "text/json" } )
	public KnowledgeResponse importCsv(@Context HttpServletRequest req) {

		String MNAME = "import";
		restlog.start( log, MNAME, null );

		try {

			// CSVファイルを読み込む
			KnowledgeResponse res = new KnowledgeResponse();
			List<EditForm> importList = readCsv(req, res);

			// ----- データインポート
			knowledgeService.importKnowledge(loginUtility.getCustomUser().getCompanyId(), importList);

			// ----- レスポンス作成
			if (res.getBulkResultList().isEmpty()) {
				res.addResult( new RestResult( ResponseCode.OK ) );
			} else {
				res.addResult( new RestResult( ResponseCode.PARTIAL ) );
			}
			messageUtility.fillMessage( res.getResultList() );

			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	/**
	 * リクエストの入力ストリームからCSVファイルを読み込む
	 * @param req リクエスト
	 * @return 編集フォームリスト
	 * @throws Exception
	 */
	private List<EditForm> readCsv(HttpServletRequest req, KnowledgeResponse res) throws Exception {
		List<EditForm> importList = new ArrayList<>();
		List<BulkResult> bulkResultList =new ArrayList<>();

		// アップロードファイルからCSVファイルの内容を読み込む
		try(
			InputStreamReader inputReader = new InputStreamReader(fileUploadUtility.getUploadInputStream(req), csvCharEncoding);
			ICsvListReader listReader = new CsvListReader(inputReader, CsvPreference.STANDARD_PREFERENCE)) {

			// ヘッダ行を読み飛ばす
			listReader.getHeader(true);

			// データ行を1行ずつ読み込み入力チェックを行う
			List<String> fieldsInCurrentRow;
			KnowledgeRequest knowledgeRequest = new KnowledgeRequest();
			Set<String> titles  = new HashSet<>();
			int errorCount = 0;
			while ((fieldsInCurrentRow = listReader.read()) != null) {
				// カラム数ミニマムチェック
				int minColumnCount = knowledgeColumnCount + maxReferenceCount + (maxManualCount * 3);
				if (fieldsInCurrentRow.size() < minColumnCount) {
					if (++errorCount > maxImportErrorCount) continue;

					// エラー結果の生成
					BulkResult result = new BulkResult();
					result.setResult(new RestResult(ResponseCode.KNOWLEDGE_CSV_FORMAT_ERROR));
					result.setNumber(listReader.getRowNumber() - 1);
					messageUtility.fillMessage( result.getResultList() );
					bulkResultList.add(result);
					continue;
				}

				// セルプロセッサ生成
				CellProcessor[] proccessor = generateProccessor(fieldsInCurrentRow.size());

				// 行の読込
				EditForm editForm = rowToEditForm(listReader.executeProcessors(proccessor));

				// 入力チェック
				knowledgeRequest.setEditForm(editForm);
				try {
					validator.validateForImport( knowledgeRequest, titles );

					titles.add(editForm.getKnowledge().getTitle());

					importList.add(editForm);
				} catch (RestException ex) {
					// エラー返却上限数を超えている場合は結果リストに追加しない
					if (++errorCount > maxImportErrorCount) continue;

					BulkResult result = new BulkResult();
					// 応答結果を作成
					result.setResultList(
							ExceptionUtil.handleException( log, ResponseCode.SYS_ERROR, null, null, null, ex )
							.getRestResultList() );

					// データ番号の設定
					result.setNumber(listReader.getRowNumber() - 1);

					messageUtility.fillMessage( result.getResultList() );
					bulkResultList.add(result);
				}
			}
			res.setBulkResultList(bulkResultList);
			res.setDataCount(importList.size() + errorCount);
			res.setErrorCount(errorCount);
		}
		return importList;
	}

	/**
	 * superCsv用のセルプロセッサを生成する
	 * @param size カラム数
	 * @return セルプロセッサ
	 */
	private CellProcessor[] generateProccessor (int size) {
		CellProcessor[] proccessor = new CellProcessor[size];
		for (int i = 0; i < proccessor.length; i++) {
			proccessor[i] = new Optional();
		}
		return proccessor;
	}

	/**
	 * CSVの行情報をナレッジの編集フォームに変換する
	 * @param fields
	 * @return
	 */
	private EditForm rowToEditForm(List<Object> fields) {

		// ナレッジ情報部分の取得
		Knowledge knowledge = new Knowledge();
		int i = 0;
		knowledge.setTitle((String) fields.get(i++));
		knowledge.setContent((String) fields.get(i++));

		// 参照先情報部分の取得
		List<String> references = new ArrayList<>();
		for (int j = 0; j < maxReferenceCount; j++) {
			String ref = (String) fields.get(i++);
			if(ref != null && ref.length() > 0) references.add(ref);
		}

		// マニュアル情報部分の取得
		List<Manual> manuals = new ArrayList<>();
		for (int j = 0; j < maxManualCount; j++) {
			Manual manual = new Manual();
			String manualNm = (String) fields.get(i++);
			String manualPg = (String) fields.get(i++);
			String manualUrl = (String) fields.get(i++);

			if ( ( manualNm != null && manualNm.length() > 0 ) ||
					( manualPg != null && manualPg.length() > 0 ) ||
						( manualUrl != null && manualUrl.length() > 0 ) ) {

				manual.setManualName(manualNm);
				manual.setManualPage(manualPg);;
				manual.setManualUrl(manualUrl);
				manuals.add(manual);
			}
		}

		knowledge.setScript((String) fields.get(i++));
		try {
			Long clictCount = Long.parseLong( (String) fields.get(i++) );
			knowledge.setClickCount(clictCount);
		}catch(Exception e) {
		}
		// タグ情報部分の取得
		List<String> tags = new ArrayList<>();
		for (; i < fields.size(); i++) {
			String tag = (String) fields.get(i);
			if(tag != null && tag.length() > 0) tags.add(tag);
		}

		// タグが未設定の場合タイトルと回答からkuromojiで名詞を抽出してタグに設定する
		if (tags.isEmpty()) {
			StringBuilder text = new StringBuilder();
			text.append(knowledge.getTitle()).append("\r\n").append(knowledge.getContent());
			tags.addAll(extractUtil.extractNouns(text.toString()));
		}

		// 大文字小文字を区別なくタグの重複を削除する
		tags = tags.stream()
				.filter(new ConcurrentSkipListSet <> (String.CASE_INSENSITIVE_ORDER)::add)
				.distinct()
				.collect(Collectors.toList());

		// 編集フォーム生成
		EditForm result = new EditForm();
		result.setKnowledge(knowledge);
		result.setTag(tags);
		result.setReference(references);
		result.setManual(manuals);

		return result;
	}

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM + ";charset=Windows-31J")
	@Path("/export")
	public Response export(@Context HttpServletRequest req, @Context HttpServletResponse res ) throws IOException {

		String MNAME = "export";
		restlog.start( log, MNAME, null );

		try {
			// ユーザ情報を取得するする
			CustomUser customUser = loginUtility.getCustomUser();

			// ----- ナレッジ情報取得
			//LexiconGetResult lexicons = speechService.getLexicon(token, modelId);

			final List<KnowledgeExportBean> list =  knowledgeService.searchForExport(customUser.getCompanyId());



			// ----- レスポンス作成
			StreamingOutput output = new StreamingOutput() {
				@Override
				public void write(OutputStream output) throws IOException, WebApplicationException {

					// 全カラムダブルコーテーションで括る
					CsvPreference preference = new CsvPreference.Builder(CsvPreference.EXCEL_PREFERENCE).useQuoteMode(new AlwaysQuoteMode()).build();
					try(
						OutputStreamWriter outputWriter = 	new OutputStreamWriter(output, csvCharEncoding);
						ICsvListWriter listWriter = new CsvListWriter(outputWriter, preference)) {

						// ヘッダーを出力する
						String[] exportHeader = makeCsvExportHeader();
						listWriter.writeHeader(exportHeader);
						for(KnowledgeExportBean each : list) {
							List<String> exportData = reFormCsvExportData(each);
							final CellProcessor[] processors = generateProccessor(exportData.size());
							listWriter.write(exportData, processors);
						}
					}
				}
			};

			return Response.ok().entity(output)
					.header("Content-Disposition", "attachment; filename=knowledge.csv").build();

		}	catch ( Exception ex ) {
			RestException restException = ExceptionUtil.handleException( log, ResponseCode.SYS_ERROR, null, null, null, ex );

			RestResponse restResponse = new RestResponse() ;
			restResponse.setResultList( restException.getRestResultList() ) ;
			messageUtility.fillMessage( restResponse.getResultList() ) ;
			restlog.abort( restException.getLogger() != null ? restException.getLogger() : log, restResponse, restResponse.getResultList(), restException ) ;

			res.sendRedirect(req.getContextPath() + downloadErrorPageUrl);
			return null;
		}
	}

	/**
	 * CSVファイルの ヘッダーを生成する
	 * @return CSVファイルの ヘッダー
	 */
	private String[] makeCsvExportHeader() {

		List<String> csvHead = new ArrayList<String>();

		csvHead.add("タイトル");
		csvHead.add("回答");

		for ( int i = 0; i < maxReferenceCount; i++ ) {
			csvHead.add("参照URL" + (i + 1 ));
		}

		for ( int i = 0; i < maxManualCount; i++ ) {
			csvHead.add("マニュアル" + (i + 1 ));
			csvHead.add("ページ数" + (i + 1 ));
			csvHead.add("URL" + (i + 1 ));
		}

		csvHead.add("スクリプト");
		csvHead.add("参照回数");
		csvHead.add("タグ");

		String[] exportHead = csvHead.toArray(new String[csvHead.size()]);

		return exportHead;
	}

	/**
	 * CSVファイルのデータを整形する
	 * @param exportData
	 * @return CSVファイルの ヘッダー
	 */
	private  List<String> reFormCsvExportData( KnowledgeExportBean exportData ) {

		List<String> result = new ArrayList<>();

		List<Reference> references = exportData.getReferences();
		List<Manual> manuals = exportData.getManuals();
		List<Tag> tags = exportData.getTags();

		result.add(exportData.getKnowledge().getTitle());
		result.add(exportData.getKnowledge().getContent());

		// 参照先設定
		if ( references != null ) {
			for ( int i = 0; i < references.size(); i++ ) {
				if ( i < maxReferenceCount ) {
					if ( references.get(i) != null
							&& references.get(i).getReferenceUrl() != null
								&& references.get(i).getReferenceUrl().length() >  0 ) {
						result.add(references.get(i).getReferenceUrl());
					} else {
						result.add("");
					}
				} else {
					break;
				}
			}
			// 足りない場合、補正する
			for (int i = 0; i < ( maxReferenceCount - references.size()); i++ ) {
				result.add("");
			}
		} else {
			for ( int i = 0; i < maxReferenceCount; i++ ) {
				result.add("");
			}
		}

		// マニュアル設定
		if ( manuals != null ) {
			//maxManualCount
			for ( int i = 0; i < manuals.size(); i++ ) {
				if ( i < maxManualCount ) {
					if ( manuals.get(i) != null ) {
						if (manuals.get(i).getManualName() != null
								&& manuals.get(i).getManualName().length() >  0) {
							result.add(manuals.get(i).getManualName());
						} else {
							result.add("");
						}

						if (manuals.get(i).getManualPage() != null
								&& manuals.get(i).getManualPage().length() >  0) {
							result.add(manuals.get(i).getManualPage());
						} else {
							result.add("");
						}

						if (manuals.get(i).getManualUrl() != null
								&& manuals.get(i).getManualUrl().length() >  0) {
							result.add(manuals.get(i).getManualUrl());
						} else {
							result.add("");
						}
					} else {
						result.add("");
						result.add("");
						result.add("");
					}
				} else {
					break;
				}
			}

			// 足りない場合、補正する
			for (int i = 0; i < ( maxManualCount - manuals.size()); i++ ) {
				result.add("");
				result.add("");
				result.add("");
			}

		} else {
			for ( int i = 0; i < maxManualCount; i++ ) {
				result.add("");
				result.add("");
				result.add("");
			}
		}

		result.add(exportData.getKnowledge().getScript());
		result.add(""+exportData.getKnowledge().getClickCount());

		// タグ設定
		if ( tags != null ) {
			for ( int i = 0; i < tags.size(); i++ ) {
				result.add(tags.get(i).getTagName());
			}
		} else {
			result.add("");
		}

	return result;
	}
}