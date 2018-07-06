/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：UseTimeRestValidator.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.usetime;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cloud.optim.callcentersolution.core.modules.rest.ResponseCode;
import cloud.optim.callcentersolution.core.modules.rest.RestException;
import cloud.optim.callcentersolution.core.modules.rest.RestResult;
import cloud.optim.callcentersolution.core.modules.rest.RestValidatorUtils;
import cloud.optim.callcentersolution.core.modules.rest.SortForm;

/**
 * UseTimeRestService のバリデータクラス
 * （入力チェックと入力内容の補完を行う）
 */
@Component
class UseTimeRestValidator
{
	/** Commons Logging instance.  */
	@SuppressWarnings( "unused" )
	private Log log = LogFactory.getLog( this.getClass() ) ;


	@Value( "${keyword.max.extract.count}" )
	private int maxExtractCount;

	/**
	 * 利用時間抽出の入力チェック.
	 *
	 * @param req 入力内容
	 */
	public void validateForSearchByMonth( UseTimeRequest req ) {

		String name = "";

		name = "#request";

		if ( req == null ) {

			throw new NullPointerException( name );
		}


		name = "#searchForm";

		SearchForm searchForm = req.getSearchForm();

		if ( searchForm == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}


		name = "#useTime";

		UseTimeSearchForm useTimeSearchForm = searchForm.getUseTime();

		if ( useTimeSearchForm == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}


		// ----- 入力年月

		{
			name = "#useTime.year" ;
			String year = useTimeSearchForm.getYear() ;
			RestValidatorUtils.fieldValidate( name, year, true, 4, 4 );

			if (!year.matches("^[0-9]*$")) {
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_FORMAT, null, name ));
			}


			name = "#useTime.month" ;
			String month = useTimeSearchForm.getMonth() ;
			RestValidatorUtils.fieldValidate( name, month, true, 1, 2 );

			if (!month.matches("^[0-9]*$") || Integer.parseInt(month)<1 || Integer.parseInt(month)>12) {
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_FORMAT, null, name ));
			}
		}

		// ----- ソート条件

		name = "#sortForm";

		SortForm sortForm = searchForm.getSortForm();

		if ( sortForm == null ) {
			sortForm = new SortForm();
			searchForm.setSortForm( sortForm );
		}

		// TODO 上限値を設定？
		// sortForm.setMaxResult(maxResult);;

		RestValidatorUtils.sortValidate( sortForm );
		RestValidatorUtils.sortConvert(sortForm);

	}


	/**
	 * 利用時間抽出の入力チェック.
	 *
	 * @param req 入力内容
	 */
	public void validateForSearchByCompanyId( UseTimeRequest req ) {

		String name = "";

		name = "#request";

		if ( req == null ) {

			throw new NullPointerException( name );
		}


		name = "#searchForm";

		SearchForm searchForm = req.getSearchForm();

		if ( searchForm == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}


		name = "#useTime";

		UseTimeSearchForm useTimeSearchForm = searchForm.getUseTime();

		if ( useTimeSearchForm == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}


		// ----- 企業ID

		{
			name = "#useTime.companyId" ;
			String companyId = useTimeSearchForm.getCompanyId() ;

			RestValidatorUtils.fieldValidate( name, companyId, true, null, 32 );
		}

		// ----- ソート条件

		name = "#sortForm";

		SortForm sortForm = searchForm.getSortForm();

		if ( sortForm == null ) {
			sortForm = new SortForm();
			searchForm.setSortForm( sortForm );
		}

		// TODO 上限値を設定？
		// sortForm.setMaxResult(maxResult);;

		RestValidatorUtils.sortValidate( sortForm );
		RestValidatorUtils.sortConvert(sortForm);

	}

}
