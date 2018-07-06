"use strict";

/*
 * ナレッジ一括登録画面（CSV インポート）
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

			importForm : {

				selector : "#importForm",
			},

			fileName : {		// ----- 日付

				selector : "#fileName",
				handler : [
					[ "change", changeFileName ]
				],
			},

			message : {			// ----- 処理結果メッセージ

				selector : "#message",
			},

			checkword : {		// ----- 保存ボタン

				selector : "#import",
				handler : [
					[ "click", checkword ]
				],
			},

			exportFile : {		// ----- 保存ボタン

				selector : "#exportFile",
				handler : [
					[ "click", csvExport ]
				],
			}

		}, // end of component

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

	// -------------------------------------------------------------------------
	// 初期表示処理
	// -------------------------------------------------------------------------

	init();

	function init() {

		// NOOP
	}

	function _enableForm() {

		data.component.importForm.$element.find( "*" ).prop( "disabled", false );
	}

	function _disableForm() {

		data.component.importForm.$element.find( "*" ).prop( "disabled", true );
	}

	// -------------------------------------------------------------------------
	// イベントハンドラ
	// -------------------------------------------------------------------------

	function _clearMessage() {

		data.component.message.$element.empty().attr( "hidden", "" ).removeClass( "bg-error text-info" );
	}

	/**
	 * メッセージをクリアする
	 */
	function changeFileName( event ) {

		_clearMessage();

		$('#photoCover').val($(this).val().replace(/\\/g, '/').replace(/.*\//, ''));
	}

	/**
	 * CSV インポート
	 */
	function checkword( event ) {

		_clearMessage();	// メッセージクリア

		var fileList = data.component.fileName.$element[0].files;

		if ( _util.isUndefined( fileList ) || fileList.length < 1 ) {

			_view.errorDialog( _prop.getMessage( "checkwordManagement.error.noFile" ) );
			return false;
		}

		var file = fileList[0];

		if ( _util.isUndefined( file ) || file.size < 1 ) {

			_view.errorDialog( _prop.getMessage( "checkwordManagement.error.emptyFile" ) );
			return false;
		}

		_view.confirmDialog( _prop.getMessage( "checkwordManagement.confirm" ), {
			ok : _importOk,
			fileList : fileList,
			file : file,
		});

		return false;
	}


	function _importOk( event ) {

		_clearMessage();

		var fileList = _view.getDialog().data( "option" ).fileList;
		var file = _view.getDialog().data( "option" ).file;

		var fd = new window.FormData();

		fd.append( "fileName", file );

		var url = _prop.getApiMap( "checkwordManagement.csvimport" );

		var option = {

			handleError : _importError,
			handleSuccess : _importSuccess,

			ajaxOption : {
				processData : false,
				contentType : false,
			}
		};

		_disableForm();
		_api.postJSON( url, fd, option );
	}

	function _importError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "checkwordManagement.error.importError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

//		setTimeout( function() {

			_dispError( msgList );

			_enableForm();
			_clearMessage();
//		}, 150 );	// ダイアログの非表示に最大 150ms かかるのでかぶらないように待つ
	}

	function _importSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_importError( xhr, status, null, option );
			return;
		}

		// ----- 正常終了

		_view.dialogClose();
		_enableForm();

		var $parent = data.component.message.$element;
		var msg = null;

		// 取り込み完了

		if ( response.resultList[0].code === _prop.getProperty( "common.apiResponse.success" ) ) {

			msg = _util.format( _prop.getMessage( "checkwordManagement.complete" ), response.dataCount );
			$parent.addClass( "text-info" ).text( msg );
			$parent.removeAttr( "hidden" );
			return;
		}

		// 部分エラーの場合はエラーメッセージを表示

		$parent.addClass( "bg-error" );

		msg = _util.format( _prop.getMessage( "checkwordManagement.error.partialError" ), response.errorCount );

		$( "<p></p>" ).text( msg ).appendTo( $parent );

		var msgList = option.result.msgList[0].sublist;

		if ( msgList.length < response.errorCount ) {

			msg = _util.format(
				_prop.getMessage( "checkwordManagement.error.tooManyError" ), msgList.length );

			$( "<p></p>" ).text( msg ).appendTo( $parent );
		}

		var $list = $( "<url></ul>" );
		$.each( msgList, function( i, msg ) {

			$( "<li></li>" ).text( response.bulkResultList[i].number + "件目：" + msg.message ).appendTo( $list );
		});

		$list.appendTo( $parent );
		$parent.removeAttr( "hidden" );
	}

	/**
	 * 出力ボタン
	 */
	function csvExport( event ) {

		_view.confirmDialog( _prop.getMessage( "checkwordManagement.exportConfirm" ), { ok : _download } );

		return false;
	}

	/**
	 * CSV エクスポート
	 */
	function _download( event ) {
		// ダウンロード
		_api.download( _prop.getApiMap( "checkwordManagement.csvexport" ));

		// ダイアログを閉じる
		return true;
	}

	// -------------------------------------------------------------------------
	// 共通処理
	// -------------------------------------------------------------------------

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

function downloadError () {
	var _prop = CCS.prop;
	var _view = CCS.view;
	_view.errorDialog(_prop.getMessage( "checkwordManagement.error.exportError"));
}