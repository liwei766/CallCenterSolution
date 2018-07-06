"use strict";


/*
 * 利用時間管理画面（疎通テスト用）
 */

$( function () {

	var _util = CCS.util;
	var _prop = CCS.prop;
	var _api  = CCS.api;
	var _view = CCS.view;

	var data = {

		// 画面構成要素の定義
		// 初期処理で各要素の jQuery オブジェクトを $element として追加する

		component : {

			search_by_month : {				// ----- 検索ボタン

				selector : "#search_by_month",
				handler : [
					[ "click", searchByMonth ]
				]
			},

			useTimeParent : {		// ----- 一覧

				selector : "#main-table tbody",	// <tbody>
			},

			listCompanyId : {

				selector : ".ccs-useTime-companyId",		// 企業IDを選択
			},

			search_by_companyID : {				// ----- 検索ボタン

				selector : "#search_by_companyID",
				handler : [
					[ "click", searchByCompanyID ]
				]
			}

		}, // end of component

		startYear : 2017,
		maxResult : 300,	// 一覧の最大表示件数

	}; // end of data

	// -------------------------------------------------------------------------
	// イベントハンドラ登録
	// -------------------------------------------------------------------------

	$.each( data.component, function ( i, component ) {

		if ( _util.isEmpty( component.selector ) ) return true; // continue

		var $component = $( component.selector );

		if ( $component.length < 1 ) return true; // continue

		component.$element = $component;

		if ( component.handler && _util.isArray( component.handler ) ) {

			$.each( component.handler, function( j, handler ) {

				var selector = handler[0];
				var func = handler[1];

				if ( selector && _util.isFunction( func ) ) {

					$component.on( selector, func );
				}
			});
		}
	});

	// ----- 企業IDを click

	data.component.useTimeParent.$element.on( "click", data.component.listCompanyId.selector, detail );


	// -------------------------------------------------------------------------
	// 検索用の年月の値を用意。ループ処理（スタート数字、終了数字、表示id名、デフォルト数字）
	// -------------------------------------------------------------------------
	function optionLoop(start, end, id, this_day) {
		var i, opt;

		opt = null;
		for (i = start; i <= end ; i++) {
			if (i === this_day) {
				opt += "<option value='" + i + "' selected>" + i + "</option>";
			} else {
				opt += "<option value='" + i + "'>" + i + "</option>";
			}
		}
		document.getElementById(id).innerHTML = opt;
	}

	var today = new Date();
	var this_year = today.getFullYear();
	var this_month = today.getMonth() + 1;
	/*
	 *関数設定（スタート数字[必須]、終了数字[必須]、表示id名[省略可能]、デフォルト数字[省略可能]）
	 */
	optionLoop(data.startYear, this_year, 'id_year', this_year);
	optionLoop(1, 12, 'id_month', this_month);




	function searchByMonth( event ) {

		var form = { searchForm : {
			useTime : {

				year : $("#id_year").val(),
				month : $("#id_month").val()

			},
			sortForm : { sortElement : [

				{ name : "useTime", asc : true }
			]}
		}};

		var url = _prop.getApiMap( "usetime.searchByMonth" );
		var json = JSON.stringify( form );

		var option = {

			handleError : _searchError,
			handleSuccess : _searchSuccess
		};

		_api.postJSON( url, json, option );
	}

	function _searchError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "useTime.error.listError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );
	}

	function _searchSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_searchError( xhr, status, null, option );
			return;
		}

		// ----- 正常終了


		// 一覧表示

		var table = $("#main-table");
		table.find("tr").each(function(i){
			if(i !== 0){
				this.remove();
			}
		});

		for (var i = 0; i < response.searchResultList.length; i++) {
			var tr = $("<tr>", {
				//align: "left",
				height: "36"
			});

			var usetime = response.searchResultList[i];

			$( "<td></td>" ).addClass( "ccs-useTime-companyId" ).attr( "data-ccs-id", usetime.companyId )
				.text( usetime.companyId ).appendTo( tr );

			//var usetimeTrim = Math.floor( usetime.useTime/1000/60 * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
			$( "<td></td>" ).css('align','left').text( millisecondToDate(usetime.useTime) ).appendTo( tr );

			$( "<td></td>" ).text( usetime.useCount ).appendTo( tr );

			tr.appendTo(table);
		}

	}


	/**
	 * 一覧上の企業ID選択処理：年月で集計内容取得／表示
	 *
	 * @param {Event} event イベント
	 */
	function detail( event ) {

		var id = $( event.target ).attr( "data-ccs-id" );

		if ( _util.isEmpty( id ) ) return false;

		$("#company_id").val(id);

		// ----- API 呼び出し
		var form = { searchForm : {
			useTime : {

				companyId : id

			},
			sortForm : { sortElement : [

				{ name : "yyyyMm", asc : false }
			]}
		}};

		var url = _prop.getApiMap( "usetime.searchByCompanyId" );
		var json = JSON.stringify( form );

		var option = {

			handleError : _searchError,
			handleSuccess : _searchSuccess_companyID
		};

		_api.postJSON( url, json, option );

	}


	function searchByCompanyID( event ) {

		var form = { searchForm : {
			useTime : {

				companyId : $("#company_id").val()

			},
			sortForm : { sortElement : [

				//{ name : "yyyyMm", asc : false }
				//{ name : "yyyyMm", asc : true }
				//{ name : "useTime", asc : true }
				//{ name : "useTime", asc : false }

				{ name : "yyyyMm", asc : false }
			]}
		}};

		var url = _prop.getApiMap( "usetime.searchByCompanyId" );
		var json = JSON.stringify( form );

		var option = {

			handleError : _searchError,
			handleSuccess : _searchSuccess_companyID
		};

		_api.postJSON( url, json, option );
	}



	function _searchSuccess_companyID( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_searchError( xhr, status, null, option );
			return;
		}

		// ----- 正常終了


		// 一覧表示
		var table = $("#main-table2");
		table.find("tr").each(function(i){
			if(i !== 0){
				this.remove();
			}
		});

		for (var i = 0; i < response.searchResultList.length; i++) {
			var tr = $("<tr>", {
				//align: "left",
				height: "36"
			});

			var usetime = response.searchResultList[i];

			$( "<td></td>" ).text( usetime.yearMonth ).appendTo( tr );

			//var usetimeTrim = Math.floor( usetime.useTime/1000/60 * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
			$( "<td></td>" ).text( millisecondToDate(usetime.useTime) ).appendTo( tr );

			$( "<td></td>" ).text( usetime.useCount ).appendTo( tr );

			tr.appendTo(table);
		}

	}


	function millisecondToDate(msd) {

		let oneDay = 3600*24*1000;
		let days = Math.floor(msd/oneDay);

		if( days<1 ){

			return new Date(msd).toISOString().slice(11, -5);

		}else{

			let time = new Date(msd % oneDay).toISOString().slice(11, -5);

			return parseInt(time.substr(0,2)) + 24*days + time.substr(2);
		}

	}

	// -------------------------------------------------------------------------
	// 共通処理
	// -------------------------------------------------------------------------

	/**
	 * エラーダイアログ表示.
	 *
	 * @param {String|String[]} message エラーメッセージ.
	 */
	function _dispError( message ) {

		if ( _util.isArray( message ) ) {

			if ( message.length < 1 ) return; // 空の配列
		}
		else {

			if ( _util.isEmpty( message ) ) return; // 空のメッセージ
			message = [ message ];
		}

		var msg = "";

		for ( var i = 0 ; i < message.length ; i++ ) {

			if ( i !== 0 ) msg += "\n";
			msg += message[i];
		}

		_view.errorDialog( msg );
	}

}); // end of ready-handler

