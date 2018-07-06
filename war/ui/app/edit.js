"use strict";

/*
 * ナレッジ登録画面
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

			back : {		// ----- 戻るボタン

				selector : "#back",
				handler : [
					[ "click", back ]
				],
			},

			// ---------- ナレッジ登録エリア

			editForm : {

				selector : "#editForm",
			},

			title : {		// ----- タイトル

				selector : "#inputTitle",
				handler : [
					[ "change", extractKeyword ]	// キーワード抽出
				],
			},

			content : {		// ----- 回答

				selector : "#inputContent",
				handler : [
					[ "change", extractKeyword ]	// キーワード抽出
				],
			},

			script : {		// ----- スクリプト

				selector : "#inputScript",
			},

			tagParent : {	// ----- タグリストコンテナ

				selector : ".card-block .badge-list",
			},

			tagList : {		// ----- タグリスト

				selector : ".ccs-edit-tag",				// 全てのタグ <span>：タグリストコンテナからの相対
			},

			deleteTag : {	// ----- タグ削除ボタン

				selector : ".ccs-edit-delete-tag",		// 全てのタグ削除 <span>：タグリストコンテナからの相対
			},

			newTagName : {	// ----- 追加するタグ名

				selector : "#inputNewTagName",
				handler : [
					[  ]
				],
			},

			addTag : {		// ----- タグ追加

				selector : "#addTag",
				handler : [
					[ "click", addTag ]
				],
			},

			save : {		// ----- 登録ボタン

				selector : "#save",
				handler : [
					[ "click", save ]
				],
			},

			// ---------- 通話履歴 一覧

			logParent : {		// ----- 一覧

				selector : "#logListArea",
			},

			logList : {		// ----- 通話履歴リスト

				selector : ".ccs-log",		// 全ての行 ：一覧コンテナからの相対
			},

			listTitle : {

				selector : ".ccs-edit-dispTitle",		// タイトル ：一覧コンテナからの相対
			},

			logDate : {		// ----- 日付

				selector : "#inputDate",
				handler : [
					[ "change", dispTimeList ]	// 通話履歴一覧取得
				],
			},

			// ---------- 通話履歴 詳細

			editLogArea : {			// ----- 通話履歴詳細エリア

				selector : ".ccs-edit-log-area",
			},

			startDate : {			// ----- 通話開始時間

				selector : "#startDate",
			},

			callTime : {			// ----- 通話時間

				selector : "#callTime",
			},

			log : {			// ----- 通話履歴内容

				selector : "#dispLog",
				handler : [
					[ "mousedown", dispLogScroll ],
					[ "wheel", dispLogScroll ]
				],
			},

			rennzoku : {		// ----- 連続再生ボタン

				selector : ".rennzoku",
				handler : [
					[ "click", rennzoku ]
				],
			},

			stop : {		// ----- 停止ボタン

				selector : ".stop",
				handler : [
					[ "click", stop ]
				],
			},

			detailLog : {		// ----- ログ詳細（文節）

				selector : ".detail-log"

			},

			play : {		// ----- 再生ボタン

				selector : ".play",

			},

			detailTxt : {		// ----- ログ詳細（文節）内容

				selector : ".detail-txt",

			},

			detailTxtInput : {		// ----- ログ詳細（文節）内容 入力フォーム

				selector : ".detail-txt > textarea",

			},

			update : {		// ----- 更新ボタン

				selector : "#update",
				handler : [
					[ "click", updateLog ]
				],
			},

			refUrlListContainer : {		// ----- 参照URLコンテナ

				selector : "#refUrlList",
			},

			refUrl : {					// ----- 参照URLセレクタ

				selector : "input",
			},

			addRefUrl : {				// ----- 参照URL追加ボタン

				selector : "#addRefUrl",
				handler : [
					[ "click", addInputRefUrl ]
				],
			},

			manualListContainer : {			// ----- マニュアルコンテナ

				selector : "#manualList",
			},

			manualListContainerSelector : {	// ----- マニュアルコンテナセレクタ

				selector : ".row",
			},

			manualTitleContainer : {		// ----- マニュアルタイトルコンテナ

				selector : ".col-lg-8",
			},

			manualTitle : {					// ----- マニュアルタイトルセレクタ

				selector : "input",
			},

			manualPageContainer : {		// ----- マニュアルページコンテナ

				selector : ".col-lg-4",
			},

			manualPage : {				// ----- マニュアルページセレクタ

				selector : "input",
			},

			manualUrlContainer : {		// ----- マニュアル参照URLコンテナ

				selector : ".col-lg-12",
			},

			manualUrl : {				// ----- マニュアル参照URLセレクタ

				selector : "input",

			},

			addManual : {		// ----- マニュアル追加ボタン

				selector : "#addManual",
				handler : [
					[ "click", addInputManual ]
				],
			},

		}, // end of component

		callLogId : null,				// 通話履歴ID
		logScrollManualUseFlg : false,	// 通話履歴スクロール手動利用中フラグ
		scDispPos : 0,					// 通話履歴スクロール表示位置
		editLogContentsFlg : false,		// 編集ログ内容有無フラグ

		// ログ音声再生関連
		saveVoice : false,		// 音声ファイル利用
		playFlg : false,		// 連続再生フラグ
		index : 0,				// 音声ファイル位置
		audios : [],			// 音声ファイル
		callLogDetailId : null,
		preGetNum : 9,			// 先行読み込み数 （選択位置 + 先行読み込み数）個を読む
		voiceExistenceFlg : false, // 音声ファイル有無フラグ

		refUrlMax : 3,	 // 参照URL表示最大件数
		refUrlCount : 1, // 参照URL表示件数
		manualMax : 3,   // マニュアル表示最大件数
		manualCount : 1  // マニュアル表示件数

	}; // end of data

	// -------------------------------------------------------------------------
	// イベントハンドラ登録
	// -------------------------------------------------------------------------

	// 1. data.component の selector がある項目について、
	//    jquery オブジェクトを作成して $element として格納する
	// 2. data.component の handler がある項目について、
	//    指定されたイベントのイベントハンドラを登録する

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

	// ----- ログ(時刻)リスト click
	data.component.logParent.$element.on( "click", data.component.logList.selector, dispLog );

	// ----- タグ削除 click
	data.component.tagParent.$element.on( "click", data.component.deleteTag.selector, deleteTag );

	// ----- 再生
	data.component.log.$element.on( "click", data.component.play.selector, onPlay);

	// ----- ログ文節クリック
	data.component.log.$element.on( "click", data.component.detailLog.selector, editTxt);

	// ----- ログテキストフォーム
	data.component.log.$element.on( "blur", data.component.detailTxtInput.selector, editTxtEnd);

	// -------------------------------------------------------------------------
	// 初期表示処理
	// -------------------------------------------------------------------------

	init();

	/**
	 * 初期表示処理.
	 */
	function init() {

		_setSaveVoice();	// 音声ファイル利用設定セット

		_disableLogForm();	// 通話履歴詳細エリア非活性化

		data.callLogId = _util.flashSession( _prop.getProperty( "common.sessionKey.callLogId" ) );

		if ( data.callLogId ) {

			// ----- 通話終了からの遷移

			data.component.back.$element.removeAttr( "hidden" ); // 戻るボタン表示
		}
		else {

			// ----- その他の遷移

			data.component.back.$element.attr( "hidden", "" ); // 戻るボタン非表示
		}

		// 通話履歴 日付選択 初期値
		var today = _getToday();
		data.component.logDate.$element.val( today );
		data.component.logDate.$element.change();

		data.refUrlCount = 1;	// 参照URL表示件数
		data.manualCount = 1;	// マニュアル表示件数
	}

	/**
	 * 今日の日付取得(yyyy-MM-dd形式 0埋め).
	 */
	function _getToday() {

		var now = new Date();

		var yyyy = now.getFullYear();
		var MM   = ( "0" + (now.getMonth() + 1) ).slice(-2);
		var dd   = ( "0" + now.getDate() ).slice(-2);

		var dateString = yyyy + "-" + MM + "-" + dd;

		return dateString;
	}

	/**
	 * 音声ファイル利用設定セット
	 */
	function _setSaveVoice() {

		var _user = CCS.user;
		data.saveVoice = _user.getData( _user.SAVE_VOICE );

	}


	// -------------------------------------------------------------------------
	// 共通の表示制御
	// -------------------------------------------------------------------------

	/**
	 * 通話履歴詳細エリア非活性化.
	 */
	function _disableLogForm() {

		_disableVoiceLogForm();

		data.component.editLogArea.$element.find( "form" ).prop( "disabled", true );
		data.component.update.$element.prop( "disabled", true ).addClass( "cursor-default" );

		if ( ! data.saveVoice ) {
			data.component.rennzoku.$element.prop( "hidden", true );
			data.component.stop.$element.prop( "hidden", true );
		}

	}

	/**
	 * 通話履歴詳細エリア活性化.
	 */
	function _enableLogForm() {

		if ( data.saveVoice && data.voiceExistenceFlg) {

			_enableVoiceLogForm();
		}

		data.component.editLogArea.$element.find( "form" ).prop( "disabled", false );

		if ( data.editLogContentsFlg ) {

			data.component.update.$element.prop( "disabled", false ).removeClass( "cursor-default" );
		}

	}

	/**
	 * 通話履歴詳細エリア 音声関連 非活性化.
	 */
	function _disableVoiceLogForm() {

		data.component.rennzoku.$element.prop( "disabled", true ).addClass( "cursor-default" );
		data.component.stop.$element.prop( "disabled", true ).addClass( "cursor-default" );
	}

	/**
	 * 通話履歴詳細エリア 音声関連 活性化.
	 */
	function _enableVoiceLogForm() {

		data.component.rennzoku.$element.prop( "disabled", false ).removeClass( "cursor-default" );
		data.component.stop.$element.prop( "disabled", false ).removeClass( "cursor-default" );
	}

	/**
	 * ログ一覧の選択状態解除.
	 */
	function _clearSelect() {

		data.component.logParent.$element.find( data.component.logList.selector ).removeClass( "bg-primary" );
	}

	/**
	 * ログ一覧の指定行を選択状態にする.
	 *
	 * @param {Element|jQuery} target 選択状態にする
	 */
	function _applySelect( target ) {

		$( target ).addClass( "bg-primary" );
	}

	/**
	 * ログ内容表示をクリア.
	 */
	function _clearLog() {

		data.component.startDate.$element.empty();
		data.component.callTime.$element.text( "00:00:00" );
		data.component.log.$element.find( "form" ).empty();
		data.component.log.$element.prop('hidden', false);

	}

	// -------------------------------------------------------------------------
	// イベントハンドラ：ナレッジ登録
	// -------------------------------------------------------------------------

	/**
	 * タグ削除ボタン押下処理.
	 *
	 * @param {Event} event イベント
	 */
	function deleteTag( event ) {

		var $target = $( event.target );
		var $tag = $target.closest( data.component.tagList.selector );

		if ( $tag.length <= 0 ) return false;

		$tag.remove();

		return false;
	}

	// -------------------------------------------------------------------------

	/**
	 * タグ追加ボタン押下処理.
	 *
	 * @param {Event} event イベント
	 */
	function addTag( event ) {

		var $newTagName = data.component.newTagName.$element;
		var newTagName = $newTagName.val();

		if ( _util.isEmpty( newTagName ) ) return false; // 入力なし

		var $parent = data.component.tagParent.$element;

		if ( _existTag( $parent, newTagName ) ) return false; // 登録済み

		_addTag( $parent, newTagName );

		$newTagName.val( "" );

		return false;
	}

	/**
	 * 指定されたタグ名が表示済みか調べる.
	 *
	 * @param {jQuery} $parent 調べる範囲（指定された要素内に表示済みか調べる）
	 * @param {String} tagName タグ名
	 *
	 * @return {boolean} true : 表示済み　　false : 未表示
	 */
	function _existTag( $parent, tagName ) {

		tagName = _trimTag( tagName );
		$parent = $parent || data.component.tagParent.$element;

		var $sameTag = $parent.find( "[data-ccs-tag='" +
			tagName.replace( /([ #;&,.+*~\'\\:"!^$[\]()=>|\/@])/g,'\\$1' ) + "']" );

		if ( $sameTag.length > 0 ) return true; // 登録済み
	}

	/**
	 * タグ名のトリミング.
	 * 先頭と末尾の、半角スペース／全角スペース／タブを除去する
	 *
	 * @param {String} tagName トリミング対象の文字列
	 * @return {String} トリミング後の文字列
	 */
	function _trimTag( tagName ) {

		if ( _util.isUndefined( tagName ) ) return tagName;

		return tagName.replace( /^[ 　\t]*/g, "" ).replace( /[ 　\t]*$/g, "" );
	}

	/**
	 * 指定されたタグを追加表示する.
	 *
	 * <span
	 * 	class="badge badge-pill badge-default ccs-edit-tag">
	 * 	data-ccs-tag="（タグ名）"
	 * >
	 * タグ名
	 * 		<span class="ccs-edit-delete-tag" aria-hidden="true"> ×<s/span>
	 * </span>
	 *
	 * @param {jQuery} $parent 追加先要素
	 * @param {String} tagName 追加表示するタグ名
	 */
	function _addTag( $parent, tagName ) {

		tagName = _trimTag( tagName );
		$parent = $parent || data.component.tagParent.$element;

		var $newTag = $( "<span></span>" )
			.addClass( "badge badge-pill badge-default" )
			.addClass( "ccs-edit-tag" )
			.attr( "data-ccs-tag", tagName )
			.text( tagName );

		var $button = $( "<span></span>" )
			.attr( "aria-hidden", true )
			.addClass( "ccs-edit-delete-tag" )
			.text( " ×" );

		$newTag.append( $button ).appendTo( $parent );
	}

	// -------------------------------------------------------------------------

	/**
	 * タイトルと回答の change 処理：キーワード抽出.
	 *
	 * @param {Event} event イベント
	 */
	function extractKeyword( event ) {

		// 入力値取得

		var input = event.target;
		var form = { searchForm : { text : input.value } };

		// API 送信

		var url = _prop.getApiMap( "keyword.extract" );
		var json = JSON.stringify( form );
		var option = {

			handleError : _extractError,
			handleSuccess : _extractSuccess
		};

		_api.postJSON( url, json, option );

		return false;
	}

	/**
	 * キーワード抽出 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _extractError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "edit.error.keywordError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );
	}

	/**
	 * キーワード抽出 API 正常終了処理.
	 *
	 * @param {Object} response レスポンス内容（JSON をパースしたオブジェクト）
	 * @param {String} status 通信結果
	 * @param {jqXHR} xhr
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _extractSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_extractError( xhr, status, null, option );
			return;
		}

		// タグ表示追加

		var $parent = data.component.tagParent.$element;

		$.each( response.searchResultList, function ( i, result ) {

			var tagName = result.keyword;

			if ( ! _existTag( $parent, tagName ) ) {

				_addTag( $parent, tagName );
			}
		});
	}


	// -------------------------------------------------------------------------

	/**
	 * 戻るボタン押下処理：オペレーション画面に戻る
	 *
	 * @param {Event} event イベント
	 */
	function back( event ) {

		$( _prop.getProperty( "layout.operation.selector" ) ).click();
	}

	// -------------------------------------------------------------------------

	/**
	 * 登録ボタン押下処理.
	 *
	 * @param {Event} event イベント
	 */
	function save( event ) {

		_view.confirmDialog( _prop.getMessage( "common.confirm.regist" ), { ok : _saveOk } );
	}

	/**
	 * 登録確認ダイアログの OK ボタン押下処理
	 *
	 * @param {Event} event イベント
	 */
	function _saveOk( event ) {

		// 入力値取得

		var form = _createSaveForm();

		// API 送信

		var url = _prop.getApiMap( "knowledge.put" );
		var json = JSON.stringify( form );
		var option = {

			handleError : _saveError,
			handleSuccess : _saveSuccess
		};

		_api.postJSONSync( url, json, option );

		return false;
	}

	/**
	 * フォーム入力内容からナレッジ登録 API への送信オブジェクトを作成する.
	 *
	 * @return {Object} ナレッジ登録 API への送信オブジェクト
	 */
	function _createSaveForm() {

		var know = {};

		know.title = data.component.title.$element.val();
		know.content = data.component.content.$element.val();
		know.script = data.component.script.$element.val();

		var tagList = [];

		data.component.tagParent.$element.find( data.component.tagList.selector ).each( function( idx, element ){

			tagList.push( $( element ).attr( "data-ccs-tag" ) );
		} );

		// 参照URLリスト
		var refUrlList = [];

		var $refUrlList = $( data.component.refUrlListContainer.$element );
		$refUrlList.find( data.component.refUrl.selector ).each( function( idx, element ){

			if ( $( element ).val() != "" ) {

				refUrlList.push( $( element ).val() );
			}
		} );

		// マニュアルリスト
		var manualList = [];
		var $manualListCon = $( data.component.manualListContainer.$element );

		$manualListCon.find( data.component.manualListContainerSelector.selector ).each( function( idx, element ) {

			var title = "";
			var page = "";
			var url = "";

			// タイトル
			$( element ).find( data.component.manualTitleContainer.selector ).each( function( idx, element ) {
				$( element ).find( data.component.manualTitle.selector ).each( function( idx, element ) {

					title = $( element ).val();
				} );
			} );

			// ページ
			$(element ).find( data.component.manualPageContainer.selector ).each( function( idx, element ) {
				$( element ).find( data.component.manualPage.selector ).each( function( idx, element ) {

					page = $(element).val();
				} );
			} );

			// 参照URL
			$( element ).find( data.component.manualUrlContainer.selector ).each( function( idx, element ) {
				$( element ).find( data.component.manualUrl.selector ).each( function( idx, element ) {

					url = $( element ).val();
				} );
			} );

			if ( title != "" || page != "" || url != "" ) {

				var manualObj = { manualName:title, manualPage:page, manualUrl:url };
				manualList.push( manualObj );
			}
		} );

		var ret = { editForm : {

				knowledge : know,
				tag : tagList,
				reference : refUrlList,
				manual : manualList
			}
		};

		return ret;
	}

	/**
	 * ナレッジ登録 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _saveError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "common.error.regist" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );
	}

	/**
	 * ナレッジ登録 API 正常終了処理.
	 *
	 * @param {Object} response レスポンス内容（JSON をパースしたオブジェクト）
	 * @param {String} status 通信結果
	 * @param {jqXHR} xhr
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _saveSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_saveError( xhr, status, null, option );
			return;
		}

		// 正常終了

		_view.infoDialog( _prop.getMessage( "common.complete.regist" ) );

		_clearForm();
	}

	/**
	 * ナレッジ登録完了後のフォームクリア.
	 */
	function _clearForm() {

		// 参照先初期化
		$( data.component.refUrlListContainer.selector ).html( '' );
        data.refUrlCount = 1;

        var content = "<div class='col-lg-12'>";
        content += "<input type='text' id='inputUrl0' class='form-control w-100' maxlength='255' placeholder=''>";
        content += "</div>";

        $( "#refUrlList" ).append( content );

        data.component.addRefUrl.$element.find( "i" ).removeClass( "del_btn_disabed_color" );

		// マニュアル初期化
		$( data.component.manualListContainer.selector ).html( '' );
        data.manualCount = 1;

        var mcontent = "<div class='row'>&nbsp;</div>";
        mcontent += "<div class='row'>";
        mcontent += "<div class='col-lg-8'>";
        mcontent += "<label for='inputManualName '>タイトル</label>";
        mcontent += "<input type='text' class='form-control w-100' id='inputManualName0' maxlength='100' placeholder=''>";
        mcontent += "</div>";
        mcontent += "<div class='col-lg-4'>";
        mcontent += "<label for='inputManualPage'>ページ</label>";
        mcontent += "<input type='text' class='form-control w-100' id='inputManualPage0' maxlength='10' placeholder=''>";
        mcontent += "</div>";
        mcontent += "<div class='col-lg-12'>";
        mcontent += "<label for='inputManualUrl '>参照URL</label>";
        mcontent += "<input type='text' class='form-control w-100' id='inputManualUrl0' maxlength='255' placeholder=''>";
        mcontent += "</div>";
        mcontent += "</div>";

        $( "#manualList" ).append( mcontent );

        data.component.addManual.$element.find( "i" ).removeClass( "del_btn_disabed_color" );

		data.component.editForm.$element.find( "form" )[0].reset();
		data.component.tagParent.$element.empty();
	}


	// -------------------------------------------------------------------------
	// イベントハンドラ：通話履歴 一覧
	// -------------------------------------------------------------------------

	/**
	 * 日付選択（change）処理：ログ(時刻)一覧表示.
	 *
	 * @param {Event} event イベント
	 */
	function dispTimeList( event ) {

		var inputDate = data.component.logDate.$element.val();

		if ( _util.isEmpty( inputDate ) ) {

			data.component.logParent.$element.empty();
			_clearLog();
			_disableLogForm();

			return false;
		}

		var form = { searchForm : {
			callLog : {

				startDateFrom : inputDate + "T00:00:00.000",
				startDateTo   : inputDate + "T23:59:59.999",

			},
			sortForm : { sortElement : [

				{ name : "startDate", asc : true }
			]}
		}};

		var url = _prop.getApiMap( "callLog.search" );
		var json = JSON.stringify( form );

		var option = {

			handleError : _timeError,
			handleSuccess : _timeSuccess
		};

		_api.postJSON( url, json, option );
	}

	/**
	 * 通話ログ検索 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _timeError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "edit.error.timeError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );

		data.component.logParent.$element.empty();

		_clearLog();
		_disableLogForm();

		data.callLogId = null;
	}

	/**
	 * 通話ログ検索 API 正常終了処理.
	 *
	 * @param {Object} response レスポンス内容（JSON をパースしたオブジェクト）
	 * @param {String} status 通信結果
	 * @param {jqXHR} xhr
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _timeSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_timeError( xhr, status, null, option );
			return;
		}

		// 正常終了

		// 一覧表示

		var $parent = data.component.logParent.$element;
		$parent.empty();

		var empty = true;

		if ( !_util.isEmpty( response.searchResultList ) ) {

			$.each( response.searchResultList, function ( i, val ) {

				var log  = val.callLog;

				if ( !_util.isEmpty( log.callLogId ) ) {

					var date = '';
					var callTime = '';
					if ( !_util.isEmpty( log.startDate ) ) {
						date = log.startDate.substr( 0, 19 );

						if ( !_util.isEmpty( log.endDate ) ) {
							callTime = _getCallTime(log.startDate, log.endDate);
						}
					}

					var $div = $( "<div></div>" ).
						addClass( "card border-top-0 border-left-0 border-right-0 border-left-0 ccs-log" ).
						append(
							$( "<div></div>" ).addClass( "card-block" ).
							append(
								$( "<div></div>" ).addClass( "card-body d-flex justify-content-start" ).append(
									$( "<p></p>" ).addClass( "text-date ccs-edit-dispTitle" ).attr( "data-ccs-id", log.callLogId ).text( date ),
									$( "<p></p>" ).addClass( "text-id ml-auto" ).text( callTime )
								)
							)
						).appendTo( $parent );

					empty = false;
				}

			});

		}

		if ( empty ) {

			_clearLog();
			_disableLogForm();

			data.callLogId = null;

			var $div = $( "<div></div>" ).
				addClass( "card border-top-0 border-left-0 border-right-0 border-left-0 ccs-log" ).
				append(
					$( "<div></div>" ).addClass( "card-block" ).
					append(
						$( "<div></div>" ).addClass( "card-body d-flex justify-content-start" ).append(
							$( "<p></p>" ).text( _prop.getMessage( "edit.noTime" ) )
						)
					)
				).appendTo( $parent );

		} else {

			// リストの対象をクリック

			if ( _util.isEmpty(data.callLogId) ) {

				data.component.logParent.$element.find( data.component.logList.selector + ":first" ).click();

			} else {

				data.component.logParent.$element.find( "[data-ccs-id='" + data.callLogId + "']" ).click();
				data.callLogId = null;

			}
		}

	}

	/**
	 * 経過時間取得
	 *
	 * @param {string} startDate 開始時間
	 * @param {string} endDate   終了時間
	 */
	function _getCallTime(startDate, endDate) {

		var start = reformDateTime(startDate);
		var end = reformDateTime(endDate);

		var diffTime = end.getTime() - start.getTime();

		return _getCallTimeByMillis(diffTime);
	}

	/**
	 * 日付時間再設定（Microsoft Edge ブラウザで文字化け対応）
	 *
	 * @param {string} inputTime 日付時間
	 */
	function reformDateTime(inputTime) {

		//2018/03/19 09:14:07.554
		let year = parseInt(inputTime.substring(0,4));
		let month = parseInt(inputTime.substring(5,7));
		let date = parseInt(inputTime.substring(8,10),10);

		let hour =  parseInt(inputTime.substring(11,13),10);
		let minutes =  parseInt(inputTime.substring(14,16),10);
		let seconds =  parseInt(inputTime.substring(17,19),10);
		let millsec =  parseInt(inputTime.substring(20,23),10);

		return new Date(year,month,date,hour,minutes,seconds,millsec);
	}

	/**
	 * 経過時間取得
	 *
	 * @param {number} diffTime 経過時間(ミリ秒)
	 */
	function _getCallTimeByMillis(diffTime) {
		var milli_sec = diffTime % 1000;
		diffTime = (diffTime - milli_sec) / 1000;
		var sec = diffTime % 60;
		diffTime = (diffTime - sec) / 60;
		var min = diffTime % 60;
		var hour = (diffTime - min) / 60;

		// 0埋め
		var hourNum = ('0' + hour).slice(-2);
		var minNum = ('0' + min).slice(-2);
		var secNum = ('0' + sec).slice(-2);

		var retTime = hourNum + ':' + minNum + ':' + secNum;

		return retTime;
	}

	/**
	 * ログリスト(時刻)選択（click）処理：ログ内容表示.
	 *
	 * @param {Event} event イベント
	 */
	function dispLog( event ) {

		var logId = $( event.target ).closest( data.component.logList.selector )
					.find( data.component.listTitle.selector ).attr( "data-ccs-id" );

		if ( _util.isEmpty( logId ) ) {

			_clearLog();
			_disableLogForm();

			return false;
		}

		_clearSelect();
		_applySelect( $( event.target ).closest( ".card" ) );

		_refreshLog( logId );
	}

	// -------------------------------------------------------------------------
	// イベントハンドラ：通話履歴 詳細
	// -------------------------------------------------------------------------

	/**
	 * 通話ログを取得して表示.
	 *
	 * @param {string|number} callLogId 通話ログ ID
	 */
	function _refreshLog( callLogId ) {

		if ( _util.isEmpty( callLogId ) ) return; // 通話ログ ID 未指定なら何もしない

		_clearLog();
		_disableLogForm();

		var form = { editForm : { callLog : {

			callLogId : callLogId,

		} } };

		var url = _prop.getApiMap( "callLog.get" );
		var json = JSON.stringify( form );

		var option = {

			handleError : _logError,
			handleSuccess : _logSuccess
		};

		_api.postJSON( url, json, option );
	}

	/**
	 * 通話ログ取得 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _logError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "edit.error.logError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );

	}

	/**
	 * 通話ログ取得 API 正常終了処理：ログ表示エリアに通話ログ内容を表示.
	 *
	 * @param {Object} response レスポンス内容（JSON をパースしたオブジェクト）
	 * @param {String} status 通信結果
	 * @param {jqXHR} xhr
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _logSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_logError( xhr, status, null, option );
			return;
		}

		// 正常終了

		var start = response.editResult.callLog.startDate.substr( 0, 19 );
		data.component.startDate.$element.text(start);

		if (response.editResult.callLog.endDate) {
			var callTime = _getCallTimeByMillis(response.editResult.callTimeSum);
			data.component.callTime.$element.text(callTime);
		}

		var $parent = data.component.log.$element.find( "form" );
		$parent.empty();

		data.voiceExistenceFlg = false;
		data.editLogContentsFlg = false;

		if ( !_util.isEmpty( response.editResult.callLogDetails ) ) {

			var url = _prop.getApiMap( "calllogdetail.voice" );
			url = url + "/";

			if ( data.saveVoice ) {
				// 音声ファイル利用

				$.each( response.editResult.callLogDetails, function ( i, log ) {

					if ( !_util.isEmpty( log.callLogDetailId ) ) {

						// 共通
						var $div = $( "<div></div>" ).
							addClass( "detail-log" ).
							attr({"data-call-log-detail-date" : log.updateDate,
									"data-call-log-detail-id": log.callLogDetailId
							});

						if ( log.border ) {
							// 区切行用
							$div.append(
									$( "<span></span>" ),
									$( "<span></span>" ).addClass( "detail-txt no-edit mx-1" ).html(  nl2brDetailLog( log.log ) )
								);

						} else if ( !log.voiceExistence ) {
							// 音声無し
							$div.append(
									$( "<span></span>" ).addClass( "fa fa-play-circle no-play" ),
									$( "<span></span>" ).addClass( "detail-txt mx-1" ).html( nl2brDetailLog( log.log ) )
								);

							data.editLogContentsFlg = true;

						} else {
							// 音声あり
							$div.append(
									$( "<span></span>" ).addClass( "fa fa-play-circle play" ),
									$( "<audio></audio>" ).attr({
										"data-call-log-detail-id-v": log.callLogDetailId
									}),
									$( "<span></span>" ).addClass( "detail-txt mx-1" ).html( nl2brDetailLog( log.log ) )
								);

							data.editLogContentsFlg = true;
							data.voiceExistenceFlg = true;
						}

						$div.appendTo( $parent ).after( "<br>" );

					}

				});

			} else {
				// 音声ファイル利用しない

				$.each( response.editResult.callLogDetails, function ( i, log ) {

					if ( !_util.isEmpty( log.callLogDetailId ) ) {

						// 共通
						var $div = $( "<div></div>" ).
							addClass( "detail-log" ).
							attr({"data-call-log-detail-date" : log.updateDate,
									"data-call-log-detail-id": log.callLogDetailId
							});

						if ( log.border ) {
							// 区切行用
							$div.append(
									$( "<span></span>" ).addClass( "detail-txt no-edit mr-1" ).html(  nl2brDetailLog( log.log ) )
								);

						} else {
							// 音声無し
							$div.append(
									$( "<span></span>" ).addClass( "detail-txt mr-1" ).html( nl2brDetailLog( log.log ) )
								);

							data.editLogContentsFlg = true;
						}

						$div.appendTo( $parent ).after( "<br>" );
					}

				});
			}
		}

		_enableLogForm();

		data.index = 0;
		data.audios = $( "audio" );

		if ( _util.isNotEmpty( data.audios ) ) {

			data.audios.each(function(i, v){
				v.addEventListener("ended", ended, false);
				v.addEventListener("error", audioError, false);
			});

		}

	}

	/**
	 * テキストをBRタグ付きHTMLに変換
	 *
	 * @param {String} txt テキスト
	 * @return {String} HTML
	 */
	function nl2brDetailLog ( txt ) {

		var $tmp = $( '<p></p>' ).text( txt );
		var addHtml = $tmp.html();

		addHtml = addHtml.replace(/\r?\n/g, "<br>\n");

		return addHtml;
	}

	/**
	 * 音声ファイル読み取りエラー時処理.
	 *
	 * @param {Event} event イベント
	 */
	function audioError( event ) {

		var $target = $( event.target );

		var detailId = $target.attr( "data-call-log-detail-id-v" );

		var $detailTarget = $( data.component.log.selector ).find( "[data-call-log-detail-id='" + detailId + "']" );

		$detailTarget.find( data.component.play.selector ).removeClass( "fa-play-circle" ).addClass( "fa-exclamation-triangle" );

		return false;
	}

	/**
	 * 連続再生ボタン押下時処理.
	 *
	 * @param {Event} event イベント
	 */
	function rennzoku( event ) {

		stop();

		data.playFlg = true;
		data.logScrollManualUseFlg = false;

		data.audios = [];

		var $audios = $( 'audio' );

		if ( $audios.length > 0 ) {

			data.audios = $audios;

			var detailId = '';

			detailId = $( data.audios[data.index] ).attr( "data-call-log-detail-id-v" );

			var $target = $( data.component.log.selector ).find( "[data-call-log-detail-id='" +detailId + "']" );

			if ( !$target.hasClass("bg-warning") ) {

				$target.addClass("bg-primary");
			}

			preSetVoiceSrc( data.index );

			if ( !$target.find( data.component.play.selector ).hasClass( "fa-exclamation-triangle" ) ) {

				var playPromise = data.audios[data.index].play();

				if ( playPromise !== undefined ) {

					playPromise.then( function() {

						$target.find( data.component.play.selector ).addClass( "play-now" );

						if ( !data.logScrollManualUseFlg ) {

							// 現在の表示位置をセット
							data.scDispPos = $target.prop( "offsetTop" );

							// 通話履歴スクロール位置取得
							var pos = _getLogScrollPosition( $target );

							data.component.log.$element.animate( { scrollTop: pos }, 300 );

						}

					} ).catch( function(error) {

					} );
				}

			} else {

				ended();
			}

		}

	}

	/**
	 * 音声ファイル事前読み込み処理.
	 *
	 * @param {number} index 読み込み開始位置
	 */
	function preSetVoiceSrc( index ) {

		if ( _util.isEmpty( index ) ) {

			index = 0;
		}

		var $audios = $( 'audio' );

		data.audios = $audios;

		var audiosLength = $audios.length;

		if ( audiosLength > 0 ) {

			var audio = '';

			var maxSetIndex = index + data.preGetNum;

			if ( maxSetIndex > ( audiosLength - 1 ) ) {

				maxSetIndex = audiosLength - 1;

			}

			for ( var i = index ; i <= maxSetIndex ; i++ ) {

				if ( _util.isNotEmpty( $audios[i] ) ) {

					var $audio = $( $audios[i] );

					// srcのチェック、無いなら追加
					var src = $audio.attr( "src" );
					if ( _util.isUndefined( src ) ) {

						var $parent = $audio.closest( data.component.detailLog.selector );

						var detailId = $parent.attr( "data-call-log-detail-id" );

						var url = _prop.getApiMap( "calllogdetail.voice" );
							src = url + "/" + detailId;

						$audio.attr({"src" : src});

					}

				}

			}
		}

	}

	/**
	 *  停止ボタン押下時処理.
	 *
	 * @param {Event} event イベント
	 */
	function stop( event ) {

		_stop();

		$( data.component.log.selector ).find( data.component.play.selector ).removeClass( "play-now" );

	}

	/**
	 *  再生中のaudioを全て停止.
	 *
	 * @param {Event} event イベント
	 */
	function _stop( event ) {

		if ( _util.isNotEmpty( data.audios ) ) {

			// 再生中のaudioを全て停止する
			data.audios.each(function(i, audio){
				audio.pause();
				audio.currentTime = 0;
			});

		}

	}

	/**
	 *  再生ボタン押下時の処理.
	 *
	 * @param {Event} event イベント
	 */
	function onPlay( event ) {

		stop();

		data.playFlg = false;
		data.logScrollManualUseFlg = false;

		var $target = $( event.target );

		var $parent = $target.closest( data.component.detailLog.selector );
		var $audio  = $parent.find( "audio" );


		var index = $( "audio" ).index( $audio );

		data.index = index;

		$( data.component.log.selector ).find( data.component.detailLog.selector ).removeClass( "bg-primary" );
		$( data.component.log.selector ).find( data.component.play.selector ).removeClass( "play-now" );

		if ( !$parent.hasClass( "bg-warning" ) ) {

			$parent.addClass( "bg-primary" );
		}

		// 音声ファイル無し
		if ( $target.hasClass( "fa-exclamation-triangle" ) ) {

			return false;

		}

		preSetVoiceSrc( data.index );

		var playPromise = $audio[0].play();

		if ( playPromise !== undefined ) {

			playPromise.then( function() {

				$parent.find( data.component.play.selector ).addClass( "play-now" );

			} ).catch( function( error ) {

			} );
		}

	}

	/**
	 *  再生終了したら次の音声を選んでまた再生.
	 *
	 * @param {Event} event イベント
	 */
	function ended( event ) {

		var $oldTarget = $( data.component.log.selector + " .play-now:first" ).closest( data.component.detailLog.selector );

		if ( $oldTarget.length < 1 && data.audios.length > 0 ) {

			var oldDetailId = $( data.audios[data.index] ).attr( "data-call-log-detail-id-v" );
			$oldTarget = $( data.component.log.selector ).find( "[data-call-log-detail-id='" + oldDetailId + "']" );
		}

		$( data.component.log.selector ).find( data.component.play.selector ).removeClass( "play-now" );

		if ( data.playFlg ) {

			$( data.component.log.selector ).find( data.component.detailLog.selector ).removeClass( "bg-primary" );

			var detailId = "";
			var $target = {};

			var nextFlg = false;

			for ( var i = 0 ; i < data.audios.length ; i++ ) {

				data.index++;

				if ( data.index >= data.audios.length ) {

					break;

				}

				detailId = $( data.audios[data.index] ).attr( "data-call-log-detail-id-v" );

				$target = $( data.component.log.selector ).find( "[data-call-log-detail-id='" +detailId + "']" );

				if ( $target.find( data.component.play.selector ).hasClass( "fa-play-circle" ) ) {
					nextFlg = true;
					break;
				}

			}

			if ( !nextFlg ) {

				data.index = 0;
				return;
			}

			// テキスト編集中の黄色が無いなら青くする
			if ( !$target.hasClass( "bg-warning" ) ) {

				$target.addClass( "bg-primary" );
			}

			preSetVoiceSrc( data.index );

			var playPromise = data.audios[ data.index ].play();

			if ( playPromise !== undefined ) {

				playPromise.then( function() {

					$target.find( data.component.play.selector ).addClass( "play-now" );

					if ( !data.logScrollManualUseFlg ) {

						// 現在の表示位置をセット
						if ( $oldTarget.length > 0 ) {

							data.scDispPos = $oldTarget.prop( "offsetTop" );
						}

						// 通話履歴スクロール位置取得
						var pos = _getLogScrollPosition( $target );

						data.component.log.$element.animate( { scrollTop: pos }, 750 );

					}

				} ).catch( function(error) {

					$target.removeClass( "bg-primary" );

				} );
			}

		}

	}

	/**
	 *  通話履歴スクロール時.
	 *
	 * @param {Event} event イベント
	 */
	function dispLogScroll( event ) {

		if ( data.saveVoice ) {

			if ( !data.logScrollManualUseFlg ) {

				data.logScrollManualUseFlg = true;
			}

		}

	}

	/**
	 *  通話履歴スクロール位置取得.
	 *
	 * @param {Object} $target 移動先対象オブジェクト
	 *
	 * @return {Number} スクロール位置
	 */
	function _getLogScrollPosition( $target ) {

		var targePos = $target.prop( "offsetTop" );

		// 現在のスクロール位置を取得
		var scTop = data.component.log.$element.scrollTop();

		if ( scTop > 5 ) {
			// スクロールしている場合は調整
			data.scDispPos = data.scDispPos - scTop;
		}

		var dspHeight = data.component.log.$element.innerHeight();

		// 非表示位置の場合は初期位置を再セット
		if ( data.scDispPos <= 0 || targePos >= ( scTop + dspHeight ) ) {

			data.scDispPos = $( data.component.log.selector + " div:first" ).prop( "offsetTop" );
		}

		var pos = targePos - data.scDispPos;

		return pos;
	}

	/**
	 *  ログ編集テキストフォーム表示.
	 *
	 * @param {Event} event イベント
	 */
	function editTxt( event ) {

		var $target = $( event.target );

		if ( $target.hasClass( "no-edit" ) || !$target.hasClass( "detail-txt" ) ) {

			$target = $target.find( data.component.detailTxt.selector );

		}

		if ( $target.hasClass( "no-edit" ) || !$target.hasClass( "detail-txt" ) ) {

			return false;
		}

		if ( !$target.hasClass( "on" ) ) {

			$target.addClass( "on" );
			var txt = $target.text();

			$target.closest( data.component.detailLog.selector ).addClass( "d-flex" );
			$target.html( "" ).append( '<textarea class="form-control w-100" data-value=""></textarea>' ).addClass( "w-input-form" );

			$target.find( ".form-control" ).attr("data-value", txt).val( txt );

			var textarea = $( data.component.detailTxtInput.selector )["0"];

			if ( textarea.scrollHeight > textarea.offsetHeight ) {
				textarea.style.height = textarea.scrollHeight + 'px';
			}

			$( data.component.detailTxtInput.selector ).focus();

		}

	}

	/**
	 *  ログ編集テキストフォーム閉じる.
	 *
	 * @param {Event} event イベント
	 */
	function editTxtEnd( event ) {

		var $target = $( event.target );
		var inputVal = $target.val();

		var defaultValue = $target.attr( "data-value" );

		if ( inputVal.replace(/\r\n/g, "\n") !== defaultValue.replace(/\r\n/g, "\n") ) {

			$target.closest( data.component.detailLog.selector ).addClass( "change bg-warning" ).removeClass( "bg-primary" );

		}

		$target.parents().find( data.component.detailLog.selector ).removeClass( "d-flex" );
		$target.parents().find( ".w-input-form" ).removeClass( "w-input-form" );

		$target.parent().removeClass( "on" ).html( nl2brDetailLog( inputVal ) );

	}

	// -------------------------------------------------------------------------

	/**
	 * 更新ボタン押下処理.
	 *
	 * @param {Event} event イベント
	 */
	function updateLog( event ) {

		var $edit = $( data.component.log.selector ).find( ".change" );

		if ( !$edit || $edit.length < 1) {

			return false;
		}

		_view.confirmDialog( _prop.getMessage( "common.confirm.update" ), { ok : _updateLogOk } );
	}

	/**
	 * 更新確認ダイアログの OK ボタン押下処理
	 *
	 * @param {Event} event イベント
	 */
	function _updateLogOk( event ) {


		// 入力値取得
		var form = _createUpdateLogForm();

		// API 送信
		var url = _prop.getApiMap( "calllogdetail.update" );
		var json = JSON.stringify( form );
		var option = {

			handleError : _updateLogError,
			handleSuccess : _updateLogSuccess
		};

		_api.postJSONSync( url, json, option );

		return false;
	}

	/**
	 * フォーム入力内容からログ詳細更新 API への送信オブジェクトを作成する.
	 *
	 * @return {Object} ログ詳細更新 API への送信オブジェクト
	 */
	function _createUpdateLogForm() {

		var $edit = $( data.component.log.selector ).find( ".change" );

		var bulkFormList = [];

		data.component.log.$element.find( ".change" ).each( function( idx, element ){

			var log = {};
			log.callLogDetailId = $( element ).attr( "data-call-log-detail-id" );
			log.updateDate = $( element ).attr( "data-call-log-detail-date" );
			log.log = $( element ).find( data.component.detailTxt.selector ).text();

			var callLogDetail = {
					"callLogDetail" : log
				};

			bulkFormList.push( callLogDetail );

		});

		var form = {
				"bulkFormList" : bulkFormList
			};

		return form;

	}

	/**
	 * ログ詳細更新 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _updateLogError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "common.error.update" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );
	}

	/**
	 * ログ詳細更新 API 正常終了処理.
	 *
	 * @param {Object} response レスポンス内容（JSON をパースしたオブジェクト）
	 * @param {String} status 通信結果
	 * @param {jqXHR} xhr
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _updateLogSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_updateLogError( xhr, status, null, option );
			return;
		}


		if ( response.resultList[0].code === _prop.getProperty( "common.apiResponse.success" ) ) {
			// 正常終了

			_view.infoDialog( _prop.getMessage( "common.complete.update" ) );

		} else {

			// 部分エラー
			_view.infoDialog( _prop.getMessage( "edit.error.partialError" ) );

		}

		if ( !_util.isEmpty( response.bulkResultList ) ) {

			$.each( response.bulkResultList, function ( i, val ) {

				var resultList  = val.resultList;
				var editResult  = val.editResult;

				// 部分正常終了のみ表示更新
				if ( resultList[0].code === _prop.getProperty( "common.apiResponse.success" ) ) {

					var log = editResult.callLogDetail;

					var detailId = log.callLogDetailId;
					var updateDate = log.updateDate;

					var $target = $( data.component.log.selector ).find( "[data-call-log-detail-id='" +detailId + "']" );

					if ( $target ) {

						$target.removeClass("change bg-warning");
						$target.attr({"data-call-log-detail-date" : log.updateDate});
						$target.find( data.component.detailTxt.selector ).html( nl2brDetailLog( log.log ) );

					}

				}

			});
		}

	}

	// -------------------------------------------------------------------------
	// 共通処理
	// -------------------------------------------------------------------------

	/**
	 * エラー表示.
	 * 複数メッセージが指定された場合は改行で区切って表示する.
	 *
	 * @param {String|String[]} message 表示するエラーメッセージ
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

	/**
	 * 参照URL追加.
	 */
	function addInputRefUrl() {

		if ( data.refUrlCount < data.refUrlMax ) {

	        var content = "<div class='col-lg-12'>";
	        content += "<label for='inputUrl'>参照URL</label>";
	        content += "<input type='text' id='inputUrl" + data.refUrlCount + "' class='form-control w-100' maxlength='255' placeholder=''>";
	        content += "</div>";

	        $( "#refUrlList" ).append( content );

	        data.refUrlCount ++ ;

	        if ( data.refUrlCount == data.refUrlMax ) {

	        	data.component.addRefUrl.$element.find( "i" ).addClass( "del_btn_disabed_color" );
	        }
		}
	}

	/**
	 * マニュアル追加.
	 */
	function addInputManual() {

		if ( data.manualCount < data.manualMax ) {

	        var content = "<div class='row'>&nbsp;</div>";
	        content += "<div class='row'>";
	        content += "<div class='col-lg-8'>";
	        content += "<label for='inputManualName '>タイトル</label>";
	        content += "<input type='text' class='form-control w-100' id='inputManualName" + data.manualCount + "' maxlength='100' placeholder=''>";
	        content += "</div>";
	        content += "<div class='col-lg-4'>";
	        content += "<label for='inputManualPage'>ページ</label>";
	        content += "<input type='text' class='form-control w-100' id='inputManualPage" + data.manualCount + "' maxlength='10' placeholder=''>";
	        content += "</div>";
	        content += "<div class='col-lg-12'>";
	        content += "<label for='inputManualUrl '>参照URL</label>";
	        content += "<input type='text' class='form-control w-100' id='inputManualUrl" + data.manualCount + "' maxlength='255' placeholder=''>";
	        content += "</div>";
	        content += "</div>";

	        $( "#manualList" ).append( content );

	        data.manualCount ++ ;

	        if ( data.manualCount == data.manualMax ) {

	        	data.component.addManual.$element.find( "i" ).addClass( "del_btn_disabed_color" );
	        }
		}
	}

}); // end of ready-handler