/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CheckwordRestService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.checkword;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

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
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.AlwaysQuoteMode;

import cloud.optim.callcentersolution.api.app.checkword.CheckwordResponse.BulkResult;
import cloud.optim.callcentersolution.api.app.checkword.CheckwordResponse.EditResult;
import cloud.optim.callcentersolution.api.entity.Checkword;
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
 * CheckwordRestService 実装.<br/>
 */
@Path( "/checkword" )
@Consumes( { "application/json", "application/xml" } )
@Produces( { "application/json", "application/xml" } )
@Component
public class CheckwordRestService
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() );

	/** PK の項目名 */
	private static final String NAME_PK = "#checkword.checkwordId";

	// -------------------------------------------------------------------------

	/** バリデータ */
	@Resource private CheckwordRestValidator validator;

	/** CheckwordService */
	@Resource private CheckwordService checkwordService;

	/** RestLog */
	@Resource private RestLog restlog;

	/** MessageUtility */
	@Resource private MessageUtility messageUtility;

	/** LoginUtility */
	@Resource private LoginUtility loginUtility;

	/** FileUploadUtility */
	@Resource private FileUploadUtility fileUploadUtility;


	/** CSVファイル文字エンコード */
	@Value( "${checkword.import.char.encoding}" )
	private String csvCharEncoding;

	/** インポート返却エラー数上限値 */
	@Value( "${checkword.import.max.error.count}" )
	private int maxImportErrorCount;

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
	public CheckwordResponse search( CheckwordRequest req ) {

		String MNAME = "search";
		restlog.start( log, MNAME, req );

		try {

			CheckwordResponse res = new CheckwordResponse();

			// ----- 入力チェック

			// 検索条件の指定がない場合は全検索として扱う

			if ( req == null ) req = new CheckwordRequest();

			validator.validateForSearch( req );

			// ----- 検索

			SearchForm form = req.getSearchForm();

			List<SearchResult> list = checkwordService.search( form );

			// ----- レスポンス作成

			res.setResult( new RestResult( ResponseCode.OK ) );
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
	public CheckwordResponse get( CheckwordRequest req ) {

		String MNAME = "get";
		restlog.start( log, MNAME, req );

		try {

			CheckwordResponse res = new CheckwordResponse();

			// ----- 入力チェック

			validator.validateForGet( req );

			// ----- 入力データ取得

			Checkword checkword = req.getEditForm().getCheckword();

			// ----- 取得

			Checkword entity = checkwordService.getCheckword( checkword.getCheckwordId() );

			if ( entity == null ) {

				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, NAME_PK, checkword.getCheckwordId() ) );
			}

			// ----- レスポンス作成

			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setEditResult( new EditResult() );
			res.getEditResult().setCheckword( entity );

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
	public CheckwordResponse importCsv(@Context HttpServletRequest req) {

		String MNAME = "import";
		restlog.start( log, MNAME, null );

		try {

			// CSVファイルを読み込む
			CheckwordResponse res = new CheckwordResponse();
			List<Checkword> checkwordList = readCsv(req, res);

			// ----- データインポート
			CustomUser custm = loginUtility.getCustomUser();
			checkwordService.importCheckword(custm.getCompanyId(),custm.getUserId(),custm.getUserName(), checkwordList);

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
	private List<Checkword> readCsv(HttpServletRequest req, CheckwordResponse res) throws Exception {
		List<Checkword> checkwords = new ArrayList<>();
		List<BulkResult> bulkResultList =new ArrayList<>();
		res.setBulkResultList(bulkResultList);

		res.setBulkResultList(bulkResultList);

		// アップロードファイルからCSVファイルの内容を読み込む
		try(
			InputStreamReader inputReader = new InputStreamReader(fileUploadUtility.getUploadInputStream(req), csvCharEncoding);
			ICsvBeanReader beanReader = new CsvBeanReader(inputReader, CsvPreference.STANDARD_PREFERENCE)) {

			// ヘッダ行を読み飛ばす
			beanReader.getHeader(true);

			final String[] headers = new String[]{"checkword","colorTheme"};
			final CellProcessor[] processors = new CellProcessor[] {
					new NotNull(new Trim()),new NotNull(new Trim())};
			int dataNo = 1;
			int errorCount = 0;
			validator.resetCheckwordsSet();
			while (true) {
				try {
					Checkword checkword = beanReader.read(Checkword.class, headers , processors);

					if (checkword == null) break;

					// ----- 入力チェックと入力内容の変換
					validator.validateForImp( checkword );

					checkwords.add(checkword);
				} catch(RestException e) {

					// 上限値超えたら結果に追加せず件数だけインクリメント
					if (++errorCount > maxImportErrorCount) continue;

					BulkResult result = new BulkResult();
					// CSVデータ不正：空白又は範囲外
					result.setResult(e.getRestResultList().get(0));
					result.setNumber(dataNo);
					messageUtility.fillMessage( result.getResultList() );
					bulkResultList.add(result);

				} catch (Exception e) {
					// 上限値超えたら結果に追加せず件数だけインクリメント
					if (++errorCount > maxImportErrorCount) continue;

					BulkResult result = new BulkResult();
					result.setResult(new RestResult(ResponseCode.CHECKWORD_CSV_FORMAT_ERROR));
					result.setNumber(dataNo);
					messageUtility.fillMessage( result.getResultList() );
					bulkResultList.add(result);
				}
				dataNo++;
			}

			res.setDataCount(checkwords.size() + errorCount);
			res.setErrorCount(errorCount);
		}
		return checkwords;
	}



	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM + ";charset=Windows-31J")
	@Path("/export")
	public Response export(@Context HttpServletRequest req, @Context HttpServletResponse res ) throws IOException {

		String MNAME = "export";
		restlog.start( log, MNAME, null );

		try {

			// -----  エクスポートリストを取得。取得条件「企業ID」がセッションからの設定
			List<SearchResult> list = checkwordService.getCheckwordsForExport( loginUtility.getCustomUser().getCompanyId() );

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

						for(SearchResult each : list) {

							List<String> checkwordInfo = new ArrayList<>();
							checkwordInfo.add(each.getCheckword().getCheckword());
							checkwordInfo.add(each.getCheckword().getColorTheme());

							final CellProcessor[] processors = new CellProcessor[] {
									new NotNull(new Trim()),new NotNull(new Trim())};


							listWriter.write(checkwordInfo, processors);
						}
					}
				}
			};

			return Response.ok().entity(output)
					.header("Content-Disposition", "attachment; filename=checkword.csv").build();

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

		csvHead.add("チェックワード内容");
		csvHead.add("ワードカラー名称");

		String[] exportHead = csvHead.toArray(new String[csvHead.size()]);

		return exportHead;
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
	public CheckwordResponse put( CheckwordRequest req ) {

		String MNAME = "put";
		restlog.start( log, MNAME, req );

		try {

			CheckwordResponse res = new CheckwordResponse();

			// ----- 入力チェック

			validator.validateForPut( req );

			// ----- 入力データ取得

			Checkword checkword = req.getEditForm().getCheckword();

			// ----- 登録

			checkword = checkwordService.save( checkword );

			// ----- レスポンス作成

			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setEditResult( new EditResult() );
			res.getEditResult().setCheckword( checkword );

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
	public CheckwordResponse update( CheckwordRequest req ) {

		String MNAME = "update";
		restlog.start( log, MNAME, req );

		try {

			CheckwordResponse res = new CheckwordResponse();

			// ----- 入力チェック

			validator.validateForUpdate( req );

			// ----- 入力データ取得

			Checkword checkword = req.getEditForm().getCheckword();

			// ----- 更新

			checkword = checkwordService.update( checkword );

			// ----- レスポンス作成

			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setEditResult( new EditResult() );
			res.getEditResult().setCheckword( checkword );

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
	public CheckwordResponse delete( CheckwordRequest req ) {

		String MNAME = "delete";
		restlog.start( log, MNAME, req );

		try {

			CheckwordResponse res = new CheckwordResponse();
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
					result.setCheckword( form.getCheckword() );

					messageUtility.fillMessage( result.getResultList() );
					restlog.endOne( log, MNAME, result, result.getResultList() );
				}
				catch ( Exception ex ) {

					error = true;

					result.setResultList( // 応答結果を作成
						ExceptionUtil.handleException( log, ResponseCode.SYS_ERROR, null, null, null, ex )
						.getRestResultList() );
					result.setCheckword( form.getCheckword() );

					messageUtility.fillMessage( result.getResultList() );
					restlog.abortOne( log, MNAME, result, result.getResultList(), ex );
				}

				res.getBulkResultList().add( result );
			}

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

		checkwordService.delete( form.getCheckword().getCheckwordId() );
	}

}