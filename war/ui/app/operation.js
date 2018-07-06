"use strict";

/*
 * オペレーション画面
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

			// ---------- 通話内容と通話制御

			// 問い合わせ番号 表示
			callLogNo : {

				selector : "#callLogNo",
			},

			// 回答候補を表示ボタン
			answerCandidate : {

				selector : "#answerCandidate",
				handler : [
					[ "click", dispAnswerCandidate ],
				],
			},

			// 通話内容
			callContents : {

				selector : "#callContents",
			},

			// 通話状況
			callStat : {

				selector : "#callStat",
			},

			// 通話中
			calling : {

				selector : "#calling",
			},

			// 準備中
			starting : {

				selector : "#starting",
			},

			// 終了中
			ending : {

				selector : "#ending",
			},

			// カウントタイマー
			countClock : {

				selector : "#countClock",
			},

			// 一時停止ボタン
			pauseCall : {
				selector : "#pauseCall",
				handler : [
					[ "click", pauseCall ],
				],
			},

			// エスカレーションボタン
			changeEscalation : {
				parentSelector : ".card-block .badge-list",
				selector : "#changeEscalation",
				handler : [
					[ "click", changeEscalation ],
				],
			},

			// エスカレーション解除ボタン
			cancelEscalation : {
				selector : "#cancelEscalation",
				handler : [
					[ "click", cancelEscalation ],
				],
			},

			// 通話開始ボタン
			startCall : {

				selector : "#startCall",
				handler : [
					[ "click", startCallDisplay ],	// ステータス表示の更新
					[ "startCall", startCall ]		// startCallDisplay から起動
				],

			},

			// 引継開始ボタン
			startHandOver : {

				selector : "#startHandOver",
				handler : [
					[ "click", startCallDisplay ]
				],

			},

			// 通話終了ボタン
			endCall : {

				selector : "#endCall",
				handler : [
					[ "click", endCallDisplay ],	// ステータス表示の更新
					[ "endCall", endCall ]			// endCallDisplay から起動
				],
			},

			// ---------- タグ

			// 追加タグ
			newTagName : {

				selector : "#inputNewTagName",
			},

			//  タグ追加ボタン
			addTag : {

				selector : "#addTag",
				handler : [
					[ "click", addTag ],
				],
			},

			//  タグ削除ボタン
			deleteTag : {

				selector : ".ccs-edit-delete-tag",
			},

			//  タグ追加モードトグル
			tagAddMode : {

				selector : "#tagAddMode",
				handler : [
					[ "change", changeTagAddMode ]
				],
			},

			// 回答候補タグエリア
			tagListArea : {

				selector : "#tagListArea",
			},

			// 回答候補タグを囲むAタグ
			tagListA : {
				parentSelector : "#tagListArea",
				selector : ".ccs-a-tag",
			},

			// 回答候補タグ
			tagList : {

				parentSelector : "#tagListArea",
				selector : ".ccs-op-tag",
			},

			// ---------- 回答候補一覧

			// ナレッジタイトルエリア
			knowledgeTitleArea : {

				selector : "#knowledgeTitleArea",
			},

			// ナレッジタイトル
			knowledgeTitle : {
				parentSelector : "#knowledgeTitleArea",
				selector : ".ccs-knowledge-title",
			},

			// ナレッジタイトルを囲むAタグ
			knowledgeTitleA : {
				parentSelector : "#knowledgeTitleArea",
				selector : ".ccs-knowledge-a-tag",
			},

			// ---------- 回答内容表示エリア

			// ナレッジ番号
			qaNo : {

				selector : "#qaNo",
			},

			// ナレッジ更新日時
			qaUpdateDate : {

				selector : "#qaUpdateDate",
			},

			// タイトル
			qaTitle : {

				selector : "#qaTitle",
			},

			// 回答
			qaContents : {

				selector : "#qaContents",
			},

			// 参照 URL
			qaUrl : {

				selector : "#qaUrl",
			},

			// マニュアル
			manualContents : {

				selector : "#manualContents",
			},

			// スクリプト
			scriptdataContents : {

				selector : "#scriptdataContents",
			},

			// ---------- エスカレーションダイアログ

			// エスカレーションダイアログ
			escalationDialog : {

				selector : "#escalationDialog",
				handler : [
					[ "shown.bs.modal", _escalationDialogFocusInput ],
				],
			},

			// 問い合わせ番号 入力フォーム
			inputCallLogNo : {

				selector : "#inputCallLogNo",
			},

			// エスカレーションダイアログ OKボタン
			escalationDialogOK : {
				selector : "#escalationDialog .modal-footer .btn1-button",
				handler : [
					[ "click", _dialogBtn1Handler ],
				],
			},

			//  エスカレーションダイアログ キャンセルボタン
			escalationDialogCancel : {

				selector : "#escalationDialog .modal-footer .btn2-button",
				handler : [
					[ "click", _dialogBtn2Handler ],
				],
			},

			// ---------- 一時停止ダイアログ

			// 一時停止ダイアログ
			pauseCallDialog : {

				selector : "#pauseCallDialog",
			},

			// 一時停止ダイアログ 再開ボタン
			pauseCallDialogResume : {

				selector : "#pauseCallDialog .modal-footer .btn1-button",
				handler : [
					[ "click", _dialogBtn1Handler ],
				],
			},

			// 一時停止ダイアログ 終了ボタン
			pauseCallDialogEndCall : {

				selector : "#pauseCallDialog .modal-footer .btn2-button",
				handler : [
					[ "click", _dialogBtn2Handler ],
				],
			},

			// 一時停止ダイアログ エスカレーションボタン
			pauseCallDialogEscalation : {

				selector : "#pauseCallDialog .modal-footer .btn3-button",
				handler : [
					[ "click", _dialogBtn3Handler ],
				],
			},

			// ---------- ダイアログ共通

			// dialog 共通
			dialog : {

				selector : ".modal[role=opdialog]",
			},

			// dialog 共通 メッセージ
			dialogMessage : {

				selector : ".modal-body p", // dialog からの相対位置
			},

		},

		// 最後に押下されたタグ
		lastSelectedTag: null,

		// タイマー関連
		nowDate : '',
		targetDate : '',
		diffTime : '',
		milli_sec : 0,
		sec : 0,
		min : 0,
		hour : 0,
		timer: '',
		secNum  : '00',
		minNum  : '00',
		hourNum : '00',

		// ナレッジキーワード抽出
		extractStringCountMAX : 10000, // 抽出用文字列最大文字数
		extractCountTag : 10,  // 抽出個数
		autoExtractCountTag : 100, // 自動の場合抽出個数

		addMAXTag : 10,  // 追加個数(選択状態のものが最大10個)

		// 通話時間
		callTimeData  : [], // 通話開始、終了時間の保持用配列
		callTimeStart : '', // 通話開始時間
		callTimeEnd   : '', // 通話終了時間

		// 音声解析関連
		analyze : {

		},
		buffersize : 16384, // 256, 512, 1024, 2048, 4096, 8192, 16384
//		energy_threshold: 170,
//		model_id : 1791,
		mic : null,
		analyzeInfo : null,
		arm : null,

		inProgress : false, // 通話開始処理中／通話終了処理中は true にする

		inEscalation : false, // エスカレーション時は true にする

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

				var event = handler[0];
				var func = handler[1];

				if ( event && _util.isFunction( func ) ) {

					$component.on( event, func );
				}
			});
		}
	});

	// タグ選択
	var $selectTagParent = $( data.component.tagList.parentSelector );
	$selectTagParent.on( "click", data.component.tagList.selector, selectTag );

	// タグ削除
	$selectTagParent.on( "click", data.component.deleteTag.selector, deleteTag );

	// ナレッジ選択
	var $knowledgeTitleAParent = $( data.component.knowledgeTitle.parentSelector );
	$knowledgeTitleAParent.on( "click", data.component.knowledgeTitle.selector, incrementAndGetKnowledgeDetail );

	// -------------------------------------------------------------------------
	// 初期表示処理
	// -------------------------------------------------------------------------

	// 初期画面表示用に各フォームをクリア
	_prepareDefaultDisp();

	/**
	 * 初期画面表示用に各フォームをクリア
	 */
	function _prepareDefaultDisp() {
		// 問い合わせ番号
		data.component.callLogNo.$element.html('');

		// 通話内容
		data.component.callContents.$element.val('');

		// 回答候補タグエリア
		data.component.tagListArea.$element.html('');

		// ナレッジタイトル
		data.component.knowledgeTitleArea.$element.html('');

		// ナレッジ情報タブエリアをクリア
		_clearKnowledgeDetailTabArea();

		// 回答候補を表示ボタン活性化
		$(data.component.answerCandidate.selector).prop('disabled', false);
	}

	/**
	 * ナレッジ情報表示エリアをクリア
	 */
	function _clearKnowledgeDetailTabArea() {

		data.component.qaNo.$element.html('');
		data.component.qaUpdateDate.$element.html('');

		data.component.qaTitle.$element.html('').closest( ".card-block" ).prop( 'hidden', true );
		data.component.qaContents.$element.html('').closest( ".card-block" ).prop( 'hidden', true );
		data.component.qaUrl.$element.html('').closest( ".card-block" ).prop( 'hidden', true );
		data.component.manualContents.$element.html('').closest( ".card-block" ).prop( 'hidden', true );
		data.component.scriptdataContents.$element.html('').closest( ".card-block" ).prop( 'hidden', true );

	}

	// ダイアログ関連 初期値セット
	_setComponentOp(data.component.escalationDialog.selector);
	_setComponentOp(data.component.pauseCallDialog.selector);

	// -------------------------------------------------------------------------
	// イベントハンドラ
	// -------------------------------------------------------------------------
	/**
	 * タグを指定
	 */
	function selectTag( event ) {

		data.lastSelectedTag = null;

		var $target = $( event.target ).closest( data.component.tagList.selector );

		if ( $target.length <= 0 ) return false;

		data.lastSelectedTag = $target;

		// タグ選択/非選択状態を切り替え
		_changeTag( $target );

		_searchKnowledge();

		return false;
	}

	/**
	 * タグ選択/非選択状態を切り替え
	 *
	 * data.lastSelectedTag は、タグ選択に失敗した場合の切り戻しに使用する.
	 */
	function _changeTag( $target ) {

		if ( ! $target ) $target = data.lastSelectedTag;

		if ( _util.isEmpty( $target ) ) return false;

		if ( $target.hasClass( "badge-primary") ) {
			$target.removeClass( "badge-primary text-white" );

		} else {

			$target.addClass( "badge-primary text-white" );
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * タグ追加
	 */
	function addTag( event ) {

		var $newTagName = $( data.component.newTagName.selector );
		var newTagName = $newTagName.val();

		if ( _util.isEmpty( newTagName ) ) return false; // 入力なし

		// 入力キーワードのAPIチェック
		_getKeywordTag(newTagName);

		return false;
	}

	/**
	 * 追加タグの入力キーワードをAPIで問い合わせ
	 */
	function _getKeywordTag(newTagName) {

		// 送信データ生成
		var form = { searchForm : {

				text : newTagName,
			}
		};

		// API 送信
		var url = _prop.getApiMap( "keyword.get" );
		var json = JSON.stringify( form );

		var option = {

			handleError : _keywordSearchError,
			handleSuccess : _keywordSearchSuccess
		};

		_api.postJSON( url, json, option );

		return false;

	}

	/**
	 * キーワード取得API用エラーハンドラ
	 */
	function _keywordSearchError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "operation.error.searchKeywordError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );
	}

	/**
	 * キーワード取得API用成功ハンドラ
	 */
	function _keywordSearchSuccess( retData, status, xhr, option ) {

		var msgList = option.result.msgList;

		if ( ! option.result.ok ) {
			// エラー表示
			_dispError( $.map( msgList, function ( val, idx ) { return val.message; } ) );

			return false;
		}

		if ( _util.isEmpty( retData.searchResult ) ) {

			msgList = [ _prop.getMessage( "operation.error.searchKeywordUnregisteredError" ) ];
			_dispError( msgList );
			return false;
		}

		var searchResult = _util.resolve( retData.searchResult );

		var newTagId = _util.resolve( searchResult.tagId );

		if ( _util.isEmpty( newTagId ) ) {

			msgList = [ _prop.getMessage( "operation.error.searchKeywordUnregisteredError" ) ];
			_dispError( msgList );
			return false;
		}

		var $parent = $( data.component.tagList.parentSelector );
		var $sameTag = $parent.find( "[data-ccs-tag-id='" + newTagId + "']" );

		if ( $sameTag.length > 0 ) {

			// 登録済み
			msgList = [ _prop.getMessage( "operation.error.addSameTagError" ) ];
			_dispError( msgList );
			return false;
		}

		var newTagName = searchResult.keyword;

		var $newTag = _addNewTagElement( newTagId, newTagName, true ); // 選択状態

		var $newTagName = $( data.component.newTagName.selector );
		$newTagName.val( "" );

		_searchKnowledge();

		return;

	}

	/**
	 * タグ要素を作成して追加する.
	 *
	 * @param {string|number} tagId タグ ID
	 * @param {string} tagName タグ名
	 * @param {boolean} selected 選択状態で追加する場合に true（デフォルトは false）
	 */
	function _addNewTagElement( tagId, tagName, selected ) {

		var $parent = $( data.component.tagList.parentSelector );

		var $tag =
			$( "<span></span>" ).addClass( "badge badge-pill badge-default ccs-op-tag" ).
				attr( "data-ccs-tag-id", tagId ).
				attr( "data-ccs-tag", tagName ).
				text( tagName );

		//	削除ボタン追加
		var $button = $( "<span></span>" )
			.attr( "aria-hidden", true )
			.addClass( "ccs-edit-delete-tag" )
			.text( " ×" );

		$tag.append( $button ).appendTo( $parent );

		$( "<a></a>" ).attr( "href", "" ).addClass( "ccs-a-tag" ).append( $tag ).appendTo( $parent );

		if ( selected ) $tag.trigger( "click" ); // 選択状態にする
	}

	// -------------------------------------------------------------------------

	/**
	 * 通話準備中表示
	 */
	function startCallDisplay() {

		if ( data.inProgress ) return false; // 開始中／終了中なので何もしない

		$(data.component.starting.selector).prop( 'hidden', false );
		$(data.component.calling.selector).prop( 'hidden', true );
		$(data.component.ending.selector).prop( 'hidden', true );

		$(data.component.pauseCall.selector).removeClass( 'invisible' );

		setTimeout( function() {
			$(data.component.startCall.selector).trigger( "startCall" );
		}, 0);
	}

	/**
	 * 通話開始
	 */
	function startCall() {

		if ( data.inProgress ) return false; // 開始中／終了中なので何もしない

		data.inProgress = true;

		try {

			var handOverCallLogId = null;

			data.inEscalation = false;

			// メニューボタンを非活性
			$( _prop.getProperty( "layout.menuItem.selector" ) ).addClass( "disabled" );

			// 各ボタン表示／非表示
			$(data.component.startCall.selector).prop('disabled', true).prop('hidden', true);
			$(data.component.endCall.selector).prop('disabled', false).prop('hidden', false);

			if ( $(data.component.startHandOver.selector).prop('hidden') ) {
				// 引継開始でない

				// 各フォームをクリア
				_prepareDefaultDisp();

			} else {
				// 引継開始の場合
				if ( !_util.isEmpty(data.analyzeInfo.callLogId) ) {
					handOverCallLogId = data.analyzeInfo.callLogId;
				}

				// 引継開始ボタンを非表示
				$(data.component.startHandOver.selector).prop('disabled', true).prop('hidden', true);
				// エスカレーション解除ボタンを非表示
				$(data.component.cancelEscalation.selector).prop('disabled', true).prop('hidden', true);
				// エスカレーションボタンを表示
				$(data.component.changeEscalation.selector).prop('disabled', false).prop('hidden', false);

			}

			// 通話内容非活性
			$(data.component.callContents.selector).prop('disabled', true);
			// 自動モードの場合
			if($(data.component.tagAddMode.selector).prop('checked')) {
				// 回答候補を表示ボタン非活性化
				$(data.component.answerCandidate.selector).prop('disabled', true);
			} else {
				// 回答候補を表示ボタン活性化
				$(data.component.answerCandidate.selector).prop('disabled', false);
			}

			// 通話開始時間
			// 00:00:00から開始
			data.sec = 0;
			data.min = 0;
			data.hour = 0;

			// 現在時刻を記録
			data.callTimeStart = _getNowTime();

			// 通話時間クリア
			$(data.component.countClock.selector).html('00:00:00');

			// 通話時間表示更新
			data.timer = setInterval(_updateCallTime, 1000);

			// 解析スタート
			_startAnalyze(handOverCallLogId);
		}
		catch ( ex ) {

			data.inProgress = false;
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * 現在時刻取得
	 */
	function _getNowTime() {

		var now = new Date();

		var y = now.getFullYear();
		var m = now.getMonth() + 1;
		var d = now.getDate();
		var w = now.getDay();
		var h = now.getHours();
		var mi = now.getMinutes();
		var s = now.getSeconds();

		var mm = ("0" + m).slice(-2);
		var dd = ("0" + d).slice(-2);
		var hh = ("0" + h).slice(-2);
		var mmi = ("0" + mi).slice(-2);
		var ss = ("0" + s).slice(-2);

		var timeString = y + "/" + mm + "/" + dd + " " + hh + ":" + mmi + ":" + ss;

		return timeString;
	}

	/**
	 * 通話時間表示更新
	 */
	function _updateCallTime() {

		data.nowDate = new Date();
		data.targetDate = new Date(data.callTimeStart);
		data.diffTime = data.nowDate.getTime() - data.targetDate.getTime();

		data.milli_sec = data.diffTime % 1000;
		data.diffTime = (data.diffTime - data.milli_sec) / 1000;
		data.sec = data.diffTime % 60;
		data.diffTime = (data.diffTime - data.sec) / 60;
		data.min = data.diffTime % 60;
		data.hour = (data.diffTime - data.min) / 60;

		// 0埋め
		data.hourNum = ('0' + data.hour).slice(-2);
		data.minNum = ('0' + data.min).slice(-2);
		data.secNum = ('0' + data.sec).slice(-2);

		$(data.component.countClock.selector).html(data.hourNum + ':' +  data.minNum + ':' + data.secNum);
	}

	// -------------------------------------------------------------------------

	/**
	 * 解析スタート
	 */
	function _startAnalyze(handOverCallLogId){

		data.analyzeInfo = {
			token : null,
			uuid : null,
			voiceId : 1,
			callLogId : null,
			callLogNo : null,
			callUserId : null,
			useTimeId : null
		};

		data.arm = new AnalyzeResultManager();

		var callContents = $(data.component.callContents.selector).val().trim();
		if (!_util.isEmpty( callContents )) {
			data.arm.result.push( callContents );
		}

		// 送信データ生成
		var form = {
			callLogId : handOverCallLogId
		};

		// API 送信
		var url = _prop.getApiMap( "call.start" );
		var json = JSON.stringify( form );

		var option = {
			handleError :_callStartError,
			handleSuccess : _callStartSuccess
		};

		_api.postJSONSync( url, json, option );

		if (_util.isEmpty(data.analyzeInfo.callLogNo)) {
			// 処理を終了
			return false;
		}

		// 問い合わせ番号表示
		$(data.component.callLogNo.selector).text(
			_prop.getLabel( "operation.callLogNo" ) + " #" + data.analyzeInfo.callLogNo );

		var mic = new Mic(data.buffersize);
		data.mic = mic;

		// 解析準備完了
		data.inProgress = false;
		$(data.component.starting.selector).prop( 'hidden', true );
		$(data.component.calling.selector).prop( 'hidden', false );
		$(data.component.ending.selector).prop( 'hidden', true );

		$(data.component.pauseCall.selector).removeClass( 'invisible' );

		// 解析処理
		_analyze(mic, form);

	}

	/**
	 * 解析処理
	 */
	function _analyze(mic, form) {

		mic.start( function(blob, ex) {

			if ( blob === false ) { // エラー
				_analyzeError( ex );
				return;
			}

			if ( $( data.component.calling.selector ).prop( 'hidden' ) ) {
				// 連打すると止まらないことがある
				mic.stop();
				return;
			}

			// 送信データ生成
			var fd = new window.FormData();
			fd.append('voice', blob);

			// API 送信
			var url = _prop.getApiMap( "call.update" ) +
				"/" + data.analyzeInfo.callLogId +
				"/" + data.analyzeInfo.callUserId +
				"/" + data.analyzeInfo.useTimeId +
				"/" + data.analyzeInfo.token +
				"/" + data.analyzeInfo.uuid +
				"/" + data.analyzeInfo.voiceId;

			var json = JSON.stringify( form );

			var option = {

				handleError : function () {}, // 何もしない
				handleSuccess : _onResultProcess,

				ajaxOption : {
					processData : false,
					contentType : 'multipart/form-data',
				}
			};

			_api.postJSONSync( url, blob, option );
			data.analyzeInfo.voiceId++;
		});
	}

	/**
	 * 解析再開
	 */
	function _resumeAnalyze(){

		var mic = null;
		if (_util.isEmpty( data.mic )) {
			mic = new Mic(data.buffersize);
		} else {
			mic = data.mic;
		}

		if (_util.isEmpty( data.arm )) {
			data.arm = new AnalyzeResultManager();
			var callContents = $(data.component.callContents.selector).val().trim();
			if (!_util.isEmpty( callContents )) {
				data.arm.result.push( callContents );
			}
		}

		data.inProgress = false;

		_dialogCloseOp(data.component.pauseCallDialog.selector);

		// 送信データ生成
		var form = {
		};

		// 解析処理
		_analyze(mic, form);

	}

	/**
	 * 通話開始API用エラーハンドラ
	 */
	function _callStartError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "operation.error.callStartError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );

		// メニューボタンを非活性
		$( _prop.getProperty( "layout.menuItem.selector" ) ).removeClass( "disabled" );

		// 各ボタン活性/非活性
		$(data.component.startCall.selector).prop('disabled', false).prop('hidden', false);
		$(data.component.endCall.selector).prop('disabled', true).prop('hidden', true);

		data.inProgress = false;
		$(data.component.starting.selector).prop( 'hidden', true );
		$(data.component.calling.selector).prop( 'hidden', true );
		$(data.component.ending.selector).prop( 'hidden', true );

		$(data.component.pauseCall.selector).addClass( 'invisible' );

		// 通話時間クリア
		$(data.component.countClock.selector).html('00:00:00');

		// 通話内容活性
		$(data.component.callContents.selector).prop('disabled', false);

		// 回答候補を表示ボタン活性化
		$(data.component.answerCandidate.selector).prop('disabled', false);

		// 通話時間表示更新を停止
		clearInterval(data.timer);

	}

	/**
	 * 通話開始API用成功ハンドラ
	 */
	function _callStartSuccess( retData, status, xhr, option ) {

		var msgList = option.result.msgList;

		if ( ! option.result.ok ) {
			// エラー表示
			_callStartError( xhr, status, null, option );

			return false;
		}

		data.analyzeInfo.token = retData.token;
		data.analyzeInfo.uuid = retData.uuid;
		data.analyzeInfo.callLogId = retData.callLogId;
		data.analyzeInfo.callLogNo = retData.callLogNo;
		data.analyzeInfo.callUserId = retData.callUserId;
		data.analyzeInfo.useTimeId = retData.useTimeId;
	}


	/**
	 * 音声解析出力
	 */
	function _onResultProcess(result){

		if (result === "" || _util.isEmpty(result.analyzeResult)) return;

		var _result = "";
		_result = data.arm.update(result.analyzeResult, $(data.component.callContents.selector)[0]);

		// 自動の場合、音声内容からタグ追加
		if ($(data.component.tagAddMode.selector).prop('checked')) {

			if (_result === "" || _util.isEmpty(_result)) return;

			_extractKeyword(_result);
		}
	}

	/**
	 * 解析エラー処理
	 */
	function _analyzeError( ex ) {

		// 通話開始時にマイクが接続されていない場合は通話開始を取り消す
		// 通話途中なら続行する

		if ( ex.name && ex.name === "DevicesNotFoundError" &&
			_util.isEmpty( $(data.component.callContents.selector).val().trim() ) ) {

			_stopCall();
			_dispError( _prop.getMessage( "operation.error.noMic" ) );

			// 取消完了
			data.inProgress = false;
			$(data.component.starting.selector).prop( 'hidden', true );
			$(data.component.calling.selector).prop( 'hidden', true );
			$(data.component.ending.selector).prop( 'hidden', true );

			$(data.component.pauseCall.selector).addClass( 'invisible' );

		}
		else {

			_util.log( "Analyze error" );
			_util.log( ex );
		}
	}


	function _stopCall() {

		data.inProgress = true;

		try {
			// 解析ストップ
			_analyzeStop();

			// 通話時間表示更新を停止
			clearInterval(data.timer);

			// メニューボタンを非活性
			$( _prop.getProperty( "layout.menuItem.selector" ) ).removeClass( "disabled" );

			// 各ボタン活性/非活性
			$(data.component.startCall.selector).prop('disabled', false).prop('hidden', false);
			$(data.component.endCall.selector).prop('disabled', true).prop('hidden', true);

			// 通話内容活性
			$(data.component.callContents.selector).prop('disabled', false);
			// 回答候補を表示ボタン活性化
			$(data.component.answerCandidate.selector).prop('disabled', false);

			// 通話終了時間を記録
			data.callTimeEnd = _getNowTime();
		}
		catch ( ex ) {

			data.inProgress = false;
		}
	}

	/**
	 * 解析ストップ
	 */
	function _analyzeStop(){
		data.mic.stop();

		// 送信データ生成
		var form = {
			callLogId : data.analyzeInfo.callLogId,
			callUserId : data.analyzeInfo.callUserId,
			useTimeId : data.analyzeInfo.useTimeId,
			token : data.analyzeInfo.token,
			uuid : data.analyzeInfo.uuid,
			voiceId : data.analyzeInfo.voiceId
		};

		// API 送信
		var url = _prop.getApiMap( "call.end" );
		var json = JSON.stringify( form );

		var option = {
			handleError : _callEndError,
			handleSuccess : _onResultProcess
		};

		_api.postJSONSync( url, json, option );

	}

	/**
	 * 通話終了API用エラーハンドラ
	 */
	function _callEndError( xhr, status, errorThrown, option ) {
		if ( data.inEscalation) {

			var msgList = [ _prop.getMessage( "operation.error.escalationError") ];
			_dispError( msgList );
		}
		return false;
	}

	// -------------------------------------------------------------------------

	/**
	 * 通話終了中表示
	 */
	function endCallDisplay() {

		if ( data.inProgress ) return false; // 開始中／終了中なので何もしない

		$(data.component.starting.selector).prop( 'hidden', true );
		$(data.component.calling.selector).prop( 'hidden', true );
		$(data.component.ending.selector).prop( 'hidden', false );

		$(data.component.pauseCall.selector).addClass( 'invisible' );

		setTimeout( function() {
			$(data.component.endCall.selector).triggerHandler( "endCall" );
		}, 0);
	}

	/**
	 * 通話終了
	 */
	function endCall() {

		if ( data.inProgress ) return false; // 開始中／終了中なので何もしない

		_stopCall();

		// 通話時間
		var callTime = $(data.component.countClock.selector).html();

		// 通話開始、終了時間を配列に入れる
		var addCallTime = {	'callStartTime' : data.callTimeStart,
							'callEndTime' : data.callTimeEnd,
							'callTime' : callTime
							};

		data.callTimeData.push(addCallTime);

		// 終了完了
		data.inProgress = false;
		$(data.component.starting.selector).prop( 'hidden', true );
		$(data.component.calling.selector).prop( 'hidden', true );
		$(data.component.ending.selector).prop( 'hidden', true );

		$(data.component.pauseCall.selector).addClass( 'invisible' );

		// 会話内容を取得
		var callContents= $(data.component.callContents.selector).val().trim();

		if ( !data.inEscalation && !_util.isEmpty( callContents )) {
			// セッションストレージ登録
			_saveCallLogIdSessions();

			// 登録画面へ遷移
			_moveEdit();
		}

		data.inEscalation = false;
	}


	/**
	 * 通話ログIDセッション保存処理
	 */
	function _saveCallLogIdSessions() {

		if ( !_util.isEmpty(data.analyzeInfo.callLogId))  {
			var _sessionName = _prop.getProperty( "common.sessionKey.callLogId" );
			_util.setSession( _sessionName, data.analyzeInfo.callLogId );
		}
	}

	function _moveEdit() {

		// 登録画面ボタンクリック
		var $edit = $( _prop.getProperty( "layout.edit.selector" ) );
		$edit.trigger("click");
	}

	// -------------------------------------------------------------------------

	/**
	 * 回答候補を表示
	 */
	function dispAnswerCandidate() {

		data.lastSelectedTag = null;

		var selectTagList = _getSelectTagList();
		if (selectTagList.length >= data.addMAXTag) {
			// すでに最大選択数
			var msgList = [ _prop.getMessage( "operation.error.selectTagMaxError") ];
			_dispError( msgList );
			return false;
		}

		// 入力値取得
		var callContents = $(data.component.callContents.selector).val().trim();
		callContents =  callContents.slice(-data.extractStringCountMAX);

		if (_util.isEmpty(callContents)) return false;

		var form = { searchForm : {

				text : callContents,
				extractCount : data.extractCountTag
			}
		};

		// API 送信
		var url = _prop.getApiMap( "keyword.knowledgeKeyword" );
		var json = JSON.stringify( form );

		var option = {

			handleError : _knowledgeKeywordExtractError,
			handleSuccess : _knowledgeKeywordExtractSuccess
		};

		_api.postJSON( url, json, option );

		return false;

	}

	/**
	 * ナレッジキーワード抽出API用エラーハンドラ
	 */
	function _knowledgeKeywordExtractError( xhr, status, errorThrown, option ) {

		return false;
	}

	/**
	 * ナレッジキーワード抽出API用成功ハンドラ
	 */
	function _knowledgeKeywordExtractSuccess( data, status, xhr, option ) {

		var msgList = option.result.msgList;

		if ( ! option.result.ok ) {
			// エラー表示
			_dispError( $.map( msgList, function ( val, idx ) { return val.message; } ) );

			return false;
		}

		var searchResultList = _util.resolve( data.searchResultList );

		_dispExtractTags(searchResultList);

		_searchKnowledge();

	}

	/**
	 * ナレッジキーワード抽出APIからの戻り値で回答候補タグを追加する
	 */
	function _dispExtractTags( searchResultList ) {

		var cntSelectTag = 0;

		// 回答候補で選択していないものは消す
		$( data.component.tagList.parentSelector ).find( data.component.tagList.selector ).each( function( idx, element ){

			if ( $( element ).hasClass( "badge-primary")) {

				cntSelectTag++;

			} else {

				var $tag = $( element ).closest( data.component.tagListA.selector  );

				if ( $tag.length <= 0 ) return false;

				$tag.remove();
			}
		});


		if ( _util.isArray( searchResultList ) ) {

			if ( searchResultList.length < 1 ) return; // 空の配列

		} else {

			if ( _util.isEmpty( searchResultList ) ) return; // 空のメッセージ

			searchResultList = [ searchResultList ];
		}

		var $parent = $( data.component.tagList.parentSelector );
		var newTagId = '';
		var newTagName = '';

		for ( var i = 0 ; i < searchResultList.length ; i++ ) {

			newTagName = searchResultList[i].keyword;
			if ( _util.isEmpty( newTagName ) ) continue;

			newTagId = searchResultList[i].tagId;
			if ( _util.isEmpty( newTagId ) ) continue;

			var $sameTag = $parent.find( "[data-ccs-tag-id='" + newTagId + "']" );

			if ( $sameTag.length > 0 ) continue; // 登録済み

			if ( cntSelectTag >= data.addMAXTag ) {
				return false;
			}

			_addNewTagElement( newTagId, newTagName );

			cntSelectTag++;
		}
	}

	/**
	 * 改行ありテキストを各行Pタグのリストに変換
	 */
	function _textToPtagList(text) {

		var $ret = $(); // 空

		if ( _util.isEmpty( text ) ) return $ret;

		var list = text.split( /\r?\n/ );

		for ( var i = 0 ; i < list.length ; i++ ) {

			$ret = $ret.add(
				$( '<p></p>' ).text( list[i] ) );
		}

		return $ret;
	}

	/**
	 * 回答候補選択タグからナレッジタイトルを表示
	 */
	function _searchKnowledge() {

		// 入力値取得
		var selectTagList = _getSelectTagList();
		if (selectTagList.length === 0) {
			// 検索結果、ナレッジ詳細タブエリアをクリア
			$(data.component.knowledgeTitleArea.selector).html('');
			_clearKnowledgeDetailTabArea();

			return false;
		}

		var form = { searchForm : {

				knowledge : {
								tagId: selectTagList,
								notNullUrl: 0
				}
			}
		};

		// API 送信
		var url = _prop.getApiMap( "knowledge.search" );
		var json = JSON.stringify( form );

		var option = {

			handleError : _knowledgeSearchError,
			handleSuccess : _knowledgeSearchSuccess
		};

		_api.postJSON( url, json, option );

		return false;

	}

	/**
	 * 選択状態の回答候補タグを取得
	 */
	function _getSelectTagList() {

		var selectTagList = [];

		$( data.component.tagList.parentSelector ).find( data.component.tagList.selector ).each( function( idx, element ){

			if ( $( element ).hasClass( "badge-primary") ) {

				var addTagId = $( element ).attr( "data-ccs-tag-id" );

				selectTagList.push(addTagId);
			}
		});

		return selectTagList;
	}

	/**
	 * ナレッジ検索API(タイトル)用エラーハンドラ
	 */
	function _knowledgeSearchError( xhr, status, errorThrown, option ) {
		// 直前に選択したタグを戻す
		_changeTag();

		var msgList = [ _prop.getMessage( "operation.error.searchKnowledgeError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );

	}

	/**
	 * ナレッジ検索API(タイトル)用成功ハンドラ
	 */
	function _knowledgeSearchSuccess( retData, status, xhr, option ) {

		var msgList = option.result.msgList;

		if ( ! option.result.ok ) {
			// 直前に選択したタグを戻す
			_changeTag();

			// エラー表示
			msgList = [ _prop.getMessage( "operation.error.searchKnowledgeError" ) ];
			_dispError( msgList );

			return false;
		}

		// 検索結果をいったんクリア
		$(data.component.knowledgeTitleArea.selector).html('');

		// ナレッジ詳細タブエリアをクリア
		_clearKnowledgeDetailTabArea();

		if ( _util.isEmpty( retData.searchResultList ) ) {

			return false;
		}

		var searchResultList = _util.resolve( retData.searchResultList );

		var $parent = $( data.component.knowledgeTitleArea.selector );
		var knowledgeId = '';
		var title = '';

		for ( var i = 0 ; i < searchResultList.length ; i++ ) {

			var knowledge = searchResultList[i].knowledge;

			if ( _util.isEmpty( knowledge.knowledgeId ) ) continue;
			if ( _util.isEmpty( knowledge.title ) ) continue;

			var $div = $( "<div></div>" ).
				addClass( "card border-top-0 border-left-0 border-right-0 border-left-0" ).
				addClass( "ccs-knowledge-title" ).
				attr( "data-ccs-knowledge-id", knowledge.knowledgeId ).
				attr( "data-ccs-knowledge-title", knowledge.title ).
				append(
				$( "<div></div>" ).addClass( "card-block" ).append(
					$( "<div></div>" ).addClass( "text-body d-flex justify-content-start" ).append(
						$( "<p></p>" ).addClass( "text-id" ).text( "#" + knowledge.knowledgeNo ),
						$( "<p></p>" ).addClass( "text-date ml-auto" ).text(
							_util.dateFormat( knowledge.updateDate, "yyyy/mm/dd HH:MM:ss" ) + " 更新" )
					),
					$( "<div></div>" ).addClass( "text-body" ).append(
						$( "<div></div>" ).addClass( "text-content" ).append(
							$( "<a></a>" ).addClass( "ccs-knowledge-a-tag" ).text( knowledge.title )
						)
					)
				)).appendTo( $parent );
		}

		return;
	}

	// -------------------------------------------------------------------------

	/**
	 * ナレッジ参照回数更新と詳細取得
	 */
	function incrementAndGetKnowledgeDetail(event) {

		var $target = $( event.currentTarget );

		_clearSelect();
		_applySelect( $target );

		var selectKnowledgeId = $target.attr( "data-ccs-knowledge-id" );

		if ( _util.isEmpty( selectKnowledgeId ) ) return false;

		// ナレッジ参照回数更新
		_incrementKnowledge(selectKnowledgeId);

		// ナレッジ詳細取得
		_getKnowledgeDetail(selectKnowledgeId);

	}

	function _clearSelect() {

		$( data.component.knowledgeTitle.selector ).removeClass( "bg-primary" );
	}

	function _applySelect( target ) {

		$( target ).addClass( "bg-primary" );
	}

	/**
	 * ナレッジ参照回数更新
	 */
	function _incrementKnowledge(selectKnowledgeId) {

		// 送信データ生成
		var form = { editForm : {

				knowledge :{
					knowledgeId : selectKnowledgeId
				}
			}
		};

		// API 送信
		var url = _prop.getApiMap( "knowledge.increment" );
		var json = JSON.stringify( form );

		_api.postJSONSync( url, json);

		return false;

	}

	/**
	 * ナレッジ詳細取得
	 */
	function _getKnowledgeDetail(selectKnowledgeId) {

		// 送信データ生成
		var form = { editForm : {

				knowledge :{
					knowledgeId : selectKnowledgeId
				}
			}
		};

		// ナレッジ取得API 送信
		var url = _prop.getApiMap( "knowledge.get" );
		var json = JSON.stringify( form );
		var option = {

			handleError : _getKnowledgeDetailError,
			handleSuccess : _getKnowledgeDetailSuccess
		};

		_api.postJSON( url, json, option );

		return false;

	}

	/**
	 * ナレッジ取得API用エラーハンドラ
	 */
	function _getKnowledgeDetailError( xhr, status, errorThrown, option ) {

		_clearSelect();
		_clearKnowledgeDetailTabArea();

		var msgList = [ _prop.getMessage( "operation.error.getKnowledgeDetailError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );
	}

	/**
	 * ナレッジ取得API用成功ハンドラ
	 */
	function _getKnowledgeDetailSuccess( retData, status, xhr, option ) {

		var msgList = option.result.msgList;

		if ( ! option.result.ok ) {
			// エラー表示
			_dispError( $.map( msgList, function ( val, idx ) { return val.message; } ) );

			return false;
		}

		if ( _util.isEmpty( retData.editResult.knowledge ) ) {

			return false;
		}

		var knowledge = _util.resolve( retData.editResult.knowledge );
		var idx = 0;

		_clearKnowledgeDetailTabArea();

		// ----- ナレッジ番号

		data.component.qaNo.$element.text( "#" + knowledge.knowledgeNo );

		// ----- 更新日時

		data.component.qaUpdateDate.$element.text( _util.dateFormat( knowledge.updateDate ) + " 更新" );

		// ----- タイトル

		if ( !_util.isEmpty( knowledge.title ) ) {

			data.component.qaTitle.$element.text( knowledge.title )
				.closest( ".card-block" ).prop( 'hidden', false );

		} else {

			data.component.qaTitle.$element.closest( ".card-block" ).prop( 'hidden', true );

		}

		// ----- 回答

		if ( !_util.isEmpty( knowledge.title ) ) {

			data.component.qaContents.$element.empty().append( _textToPtagList( knowledge.content ) )
				.closest( ".card-block" ).prop( 'hidden', false );

		} else {

			data.component.qaContents.$element.closest( ".card-block" ).prop( 'hidden', true );

		}

		// ----- 参照 URL

		var reference = _util.resolve( retData.editResult.reference );
		var $qaUrl = data.component.qaUrl.$element;
		$qaUrl.empty();

		var referenceUrlFlg = false;

		if ( _util.isArray( reference ) ) {

			for ( var i = 0 ; i < reference.length ; i++ ) {

				if ( !_util.isEmpty( reference[i].referenceUrl ) ) {

					var $p1 = $('<p></p>').
							 append (
								$('<a></a>').addClass( "card-link" ).
									attr( 'href', reference[i].referenceUrl ).
									attr( 'target', '_blank' ).
									text( reference[i].referenceUrl )
							 ).appendTo( $qaUrl );

					referenceUrlFlg = true;

				}

			}

		}

		if ( referenceUrlFlg ) {

			$qaUrl.closest( ".card-block" ).prop( 'hidden', false );

		} else {

			$qaUrl.closest( ".card-block" ).prop( 'hidden', true );
		}

		// ----- マニュアル

		var manual = _util.resolve( retData.editResult.manual );
		var $manContents = data.component.manualContents.$element;
		$manContents.empty();
		var manualFlg = false;

		if ( _util.isArray( manual ) ) {

			for ( var j = 0 ; j < manual.length ; j++ ) {

				var manualName = "";
				var manualUrl = "";

				if ( !_util.isEmpty( manual[j].manualName ) ) {

					manualName += manual[j].manualName;
				}

				if ( !_util.isEmpty( manual[j].manualUrl ) ) {

					manualUrl = manual[j].manualUrl;
				}

				if ( !_util.isEmpty( manual[j].manualPage ) ) {

					manualName += "(" + manual[j].manualPage + " ページ)";

					// PDF ファイルの場合は URL にページ数を付加
					// （.pdf で終わる URL を PDF ファイルとみなす）

					if ( manualUrl.length > 0 ) {

						var lower = manualUrl.toLowerCase();
						idx = lower.lastIndexOf( ".pdf" );

						if ( idx === ( lower.length - ".pdf".length ) ) {

							manualUrl += "#page=" + encodeURIComponent( manual[j].manualPage );
						}
					}
				}

				if ( _util.isEmpty( manualUrl ) ) { // URL がなければマニュアル名のみ表示

					var $p2 = $('<p></p>').
								addClass( "card-link" ).
								text( manualName ).
								appendTo( $manContents );

				}
				else { // URL があればリンクとして表示

					if ( _util.isEmpty( manualName ) ) manualName = manualUrl;

					var $p3 = $('<p></p>').
								append (
									$('<a></a>').addClass( "card-link" ).
										attr( 'href', manualUrl ).
										attr( 'target', '_blank' ).
										text( manualName )
								).appendTo( $manContents );

				}

				if ( !_util.isEmpty( manualName ) ) {

					manualFlg = true;

				}

			}

		}

		if ( manualFlg ) {

			$manContents.closest( ".card-block" ).prop( 'hidden', false );

		} else {

			$manContents.closest( ".card-block" ).prop( 'hidden', true );
		}

		// ----- スクリプト

		if ( !_util.isEmpty( knowledge.script ) ) {

			data.component.scriptdataContents.$element.empty().append( _textToPtagList( knowledge.script ) )
				.closest( ".card-block" ).prop( 'hidden', false );

		} else {

			data.component.scriptdataContents.$element.closest( ".card-block" ).prop( 'hidden', true );

		}

		return;
	}

	// -------------------------------------------------------------------------

	// エスカレーション
	function changeEscalation() {

		if ( data.inProgress ) return false; // 開始中／終了中なので何もしない

		// 回答候補を表示ボタン活性化
		$(data.component.answerCandidate.selector).prop('disabled', false);

		if ( $( data.component.calling.selector ).prop( 'hidden' ) ) {
			// エスカレーション(先)
			$(data.component.inputCallLogNo.selector).val('');

			_escalationDialog( _prop.getMessage( "operation.inputCallLogNo" ),
								{ btn1 : _getCallLog });

		} else {

			data.inEscalation = true;

			// 通話終了
			endCallDisplay();
		}
	}

	/**
	 * エスカレーションダイアログの入力フォームにフォーカスをセット
	 */
	function _escalationDialogFocusInput() {

		$(data.component.inputCallLogNo.selector).focus();

	}


	/**
	 * 通話ログ取得
	 */
	function _getCallLog( ) {

		_dialogCloseOp(data.component.escalationDialog.selector);

		var inputCallLogNo = $(data.component.inputCallLogNo.selector).val();

		if ( _util.isEmpty( inputCallLogNo ) ) return false; // 空

		// 送信データ生成
		var form = { editForm : {

				callLog : {
							callLogNo : inputCallLogNo
				}
			}
		};

		var url = _prop.getApiMap( "callLog.getByNo" );
		var json = JSON.stringify( form );

		var option = {

			handleError : _getLogError,
			handleSuccess : _getLogSuccess
		};

		_api.postJSON( url, json, option );
	}

	/**
	 * 通話ログ取得API用エラーハンドラ
	 */
	function _getLogError( xhr, status, errorThrown, option ) {

		_view.errorDialog( _prop.getMessage( "operation.error.getLogError" ) );
	}

	/**
	 * 通話ログ取得API用成功ハンドラ
	 */
	function _getLogSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_getLogError( xhr, status, null, option );
			return;
		}

		// 正常終了
		data.analyzeInfo = {
			callLogId : response.editResult.callLog.callLogId,
			callLogNo : response.editResult.callLog.callLogNo
		};

		// 通話ログ画面へセット
		var callLogNo = $(data.component.inputCallLogNo.selector).val();

		if (_util.isEmpty(callLogNo)) {
			return false;
		}

		var callLogTxt = response.editResult.logs;

		// 問い合わせ番号表示
		$(data.component.callLogNo.selector).text(
			_prop.getLabel( "operation.callLogNo" ) + " #" + callLogNo );


		// 通話内容表示
		$(data.component.callContents.selector).val(callLogTxt);


		// 通話開始ボタンを非表示
		$(data.component.startCall.selector).prop('disabled', true).prop('hidden', true);
		// 引継開始ボタンを表示
		$(data.component.startHandOver.selector).prop('disabled', false).prop('hidden', false);
		// 通話開始ボタンを非表示(念のため)
		$(data.component.endCall.selector).prop('disabled', true).prop('hidden', true);

		// エスカレーション非表示
		$(data.component.changeEscalation.selector).prop('disabled', true).prop('hidden', true);
		// エスカレーション解除表示
		$(data.component.cancelEscalation.selector).prop('disabled', false).prop('hidden', false);

		// 通話内容非活性
		$(data.component.callContents.selector).prop('disabled', true);

		// 回答候補タグエリア
		$(data.component.tagListArea.selector).html('');

		// ナレッジタイトル
		$(data.component.knowledgeTitleArea.selector).html('');

		// ナレッジ情報タブエリアをクリア
		_clearKnowledgeDetailTabArea();

	}

	// エスカレーション解除
	function cancelEscalation() {

		// 引継開始ボタンを非表示
		$(data.component.startHandOver.selector).prop('disabled', true).prop('hidden', true);
		// 通話開始ボタンを表示
		$(data.component.startCall.selector).prop('disabled', false).prop('hidden', false);
		// エスカレーション解除ボタンを非表示
		$(data.component.cancelEscalation.selector).prop('disabled', true).prop('hidden', true);
		// エスカレーションボタンを表示
		$(data.component.changeEscalation.selector).prop('disabled', false).prop('hidden', false);

		// 通話内容活性
		$(data.component.callContents.selector).prop('disabled', false);

		// 各フォームをクリア
		_prepareDefaultDisp();

		data.analyzeInfo = {};
	}

	/**
	 * 一時停止
	 */
	function pauseCall() {

		if ( data.inProgress ) return false; // 開始中／終了中なので何もしない

		if ( $( data.component.calling.selector ).prop( 'hidden' ) ) {
			// 通話中でない場合、何もしない
			return false;
		}

		if ( _util.isEmpty( data.analyzeInfo ) ||
			 _util.isEmpty( data.analyzeInfo.callLogId )){
			// IDが取れない場合、何もしない
			return false;
		}

		// 通話中の場合は、一時停止
		$(data.component.inputCallLogNo.selector).val('');

		// 一時停止ダイアログ表示
		_pauseCallDialog( _prop.getMessage( "operation.pauseCall" ),
							 {	btn1 : _resumeCallFromPause,
								btn2 : _endCallFromPause,
								btn3 : _escalationFromPause});

		// 一時停止API
		_pause();
	}

	/**
	 * 一時停止から再開
	 */
	function _resumeCallFromPause() {

		if ( data.inProgress ) return false; // 開始中／終了中なので何もしない

		data.inProgress = true;

		_resume();
	}

	/**
	 * 一時停止から終了
	 */
	function _endCallFromPause() {
		_dialogCloseOp(data.component.pauseCallDialog.selector); // すぐ閉じる
		if ( !_util.isEmpty( data.analyzeInfo.token ) ) {
			data.analyzeInfo.token = null;
		}
		if ( !_util.isEmpty( data.analyzeInfo.uuid ) ) {
			data.analyzeInfo.uuid = null;
		}
		endCallDisplay();
		return false;
	}


	/**
	 * 一時停止からエスカレーション(元)
	 */
	function _escalationFromPause() {
		_dialogCloseOp(data.component.pauseCallDialog.selector); // すぐ閉じる
		data.inEscalation = true;
		if ( !_util.isEmpty( data.analyzeInfo.token ) ) {
			data.analyzeInfo.token = null;
		}
		if ( !_util.isEmpty( data.analyzeInfo.uuid ) ) {
			data.analyzeInfo.uuid = null;
		}
		endCallDisplay();
		return false;
	}

	/**
	 * 通話一時停止API
	 */
	function _pause() {
		data.mic.stop();

		// 送信データ生成
		var form = {
			callLogId : data.analyzeInfo.callLogId,
			callUserId : data.analyzeInfo.callUserId,
			useTimeId : data.analyzeInfo.useTimeId,
			token : data.analyzeInfo.token,
			uuid: data.analyzeInfo.uuid,
			voiceId : data.analyzeInfo.voiceId
		};

		// API 送信
		var url = _prop.getApiMap( "call.pause" );
		var json = JSON.stringify( form );

		var option = {
			handleError : function () {}, // 何もしない
			handleSuccess : _onResultProcess
		};

		_api.postJSONSync( url, json, option );

	}

	/**
	 * 通話再開
	 */
	function _resume() {

		if ( _util.isEmpty( data.analyzeInfo ) || _util.isEmpty( data.analyzeInfo.callLogId )) {
			return false;
		}

		// 送信データ生成
		var form = {
			callLogId: data.analyzeInfo.callLogId
		};

		var url = _prop.getApiMap( "call.resume" );
		var json = JSON.stringify( form );

		var option = {
			handleError : _resumeError,
			handleSuccess : _resumeSuccess
		};

		_api.postJSON( url, json, option );
	}

	/**
	 * 通話再開API用エラーハンドラ
	 */
	function _resumeError( xhr, status, errorThrown, option ) {

		data.inProgress = false;

		var $dialog = $( data.component.pauseCallDialog.selector );
		var $msg = $dialog.find( _prop.getProperty( "layout.dialogMessage.selector" ) );
		$msg.html( _prop.getMessage( "operation.error.resumeError" ) );
		$msg.addClass( "text-error" );

	}

	/**
	 * 通話再開API用成功ハンドラ
	 */
	function _resumeSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {
			_resumeError( xhr, status, null, option );
			return;
		}

		data.analyzeInfo.token = response.token;
		data.analyzeInfo.uuid = response.uuid;
		data.analyzeInfo.voiceId = 1;

		// 解析再開
		_resumeAnalyze();

	}

	// -------------------------------------------------------------------------
	// ダイアログ
	// -------------------------------------------------------------------------
	/**
	 * @param message {String} 表示メッセージ
	 * @param option {Object} 表示オプション
	 *
	 * option は、ハンドラ内で $dialog.data( "option" ) として取得可能
	 */
	function _escalationDialog( message, option ) {

		return _dialogOp( "escalation", message, option );
	}

	function _pauseCallDialog( message, option ) {

		return _dialogOp( "pauseCall", message, option );
	}

	/**
	 * @private
	 * @param message {String} 表示メッセージ
	 * @param option {Object} 表示オプション
	 *
	 * @return {Element} ダイアログ要素
	 */
	var _dialogOptionName = "option";
	function _dialogOp( type, message, option ) {

		var $dialog = "";
		if ( type === "escalation" ) {
			$dialog = $( data.component.escalationDialog.selector );
		}
		else if ( type === "pauseCall" ) {
			$dialog = $( data.component.pauseCallDialog.selector );
		}
		else {
			return false;
		}

		var $msg = $dialog.find( data.component.dialogMessage.selector );
		message = _util.escapeHTML( message );
		message = message.replace( /\n|\r\n|\r/, "<br/>" );
		$msg.html( message );

		if ( type === "pauseCall" ) {
			$msg.removeClass( "text-error" );
		}

		$dialog.data( _dialogOptionName, option || null );

		if ( ! _dialogIsOpenOp($dialog) ) $dialog.modal();

		return $dialog[0];
	}

	function _dialogIsOpenOp( selector ) {

		var $dialog = _getDialogOp( selector );

		return $dialog.hasClass( "show" );
	}

	function _dialogCloseOp( selector ) {

		if ( _dialogIsOpenOp(selector) ) {

			var $dialog = _getDialogOp(selector);
			$dialog.modal( "hide" );
		}
	}

	function _getDialogOp( selector ) {
		return $( selector );
	}

	function _setComponentOp( selector ) {

		// ダイアログ
		var $dialog = $( selector );

		$dialog.data( "keyboard", false );			// ESC キーによるクローズを禁止
		$dialog.data( "backdrop", "static" );		// 余白のクリックで閉じない
		$dialog.data( _dialogOptionName, null );
	}

	/**
	 * @private
	 */
	function _dialogBtn1Handler( event ) {

		var $dialog = $( event.target ).closest( data.component.dialog.selector );

		var close = true;

		if ( $dialog.data( _dialogOptionName ) && $dialog.data( _dialogOptionName ).btn1 &&
			_util.isFunction( $dialog.data( _dialogOptionName ).btn1 ) ) {

			close = $dialog.data( _dialogOptionName ).btn1( event );
		}

		if ( close && _dialogIsOpenOp($dialog) ) {

			$dialog.modal( "hide" );
		}
	}


	/**
	 * @private
	 */
	function _dialogBtn2Handler( event ) {

		var $dialog = $( event.target ).closest( data.component.dialog.selector );

		var close = true;

		if ( $dialog.data( _dialogOptionName ) && $dialog.data( _dialogOptionName ).btn2 &&
			_util.isFunction( $dialog.data( _dialogOptionName ).btn2 ) ) {

			close = $dialog.data( _dialogOptionName ).btn2( event );
		}

		if ( close && _dialogIsOpenOp($dialog) ) {

			$dialog.modal( "hide" );
		}
	}


	/**
	 * @private
	 */
	function _dialogBtn3Handler( event ) {

		var $dialog = $( event.target ).closest( data.component.dialog.selector );

		var close = true;

		if ( $dialog.data( _dialogOptionName ) && $dialog.data( _dialogOptionName ).btn3 &&
			_util.isFunction( $dialog.data( _dialogOptionName ).btn3 ) ) {

			close = $dialog.data( _dialogOptionName ).btn3( event );
		}

		if ( close && _dialogIsOpenOp($dialog) ) {

			$dialog.modal( "hide" );
		}
	}


	/**
	 * エラー表示
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
	 * タグ削除ボタン押下処理.
	 *
	 * @param {Event} event イベント
	 */
	function deleteTag( event ) {

		var $target = $( event.target );
		var $target1 = $( event.target ).closest( data.component.tagList.selector );

		var $tag = $target.closest( data.component.tagList.selector );

		if ( $tag.length <= 0 ) return false;

		$tag.remove();

		if ( $target1.hasClass( "badge-primary") ) {
			_searchKnowledge() ;
		}

		return false;
	}

	/**
	 * ナレッジキーワード抽出.
	 *
	 */
	function _extractKeyword( result ) {

		var form = { searchForm : {

			text : result,
			extractCount : data.autoExtractCountTag
		}
	};

	// API 送信
	var url = _prop.getApiMap( "keyword.knowledgeKeyword" );
	var json = JSON.stringify( form );

	var option = {

		handleError :  function () {}, // 何もしない
		handleSuccess : _knowledgeKeywordSearchSuccessFromCall
	};

	_api.postJSON( url, json, option );
	}

	/**
	 * キーワード取得API用成功ハンドラ.
	 */
	function _knowledgeKeywordSearchSuccessFromCall( retData, status, xhr, option ) {

		var msgList = option.result.msgList;

		// エラー発生
		if ( ! option.result.ok ) {
			return false;
		}

		//  operation.error.searchKeywordUnregisteredError
		if ( _util.isEmpty( retData.searchResultList ) ) {
			return false;
		}

		var searchResultList = _util.resolve( retData.searchResultList );

		if ( _util.isArray( searchResultList ) ) {

			if ( searchResultList.length < 1 ) return; // 空の配列

		} else {

			if ( _util.isEmpty( searchResultList ) ) return; // 空のメッセージ

			searchResultList = [ searchResultList ];
		}

		var $parent = $( data.component.tagList.parentSelector );
		var newTagId = '';
		var newTagName = '';

		for ( var i = 0 ; i < searchResultList.length ; i++ ) {

			newTagName = searchResultList[i].keyword;
			if ( _util.isEmpty( newTagName ) ) continue;

			newTagId = searchResultList[i].tagId;
			if ( _util.isEmpty( newTagId ) ) continue;

			if ( _existTag( $parent, newTagName ) ) continue;

			_addNewTagElement( newTagId, newTagName, false ); // 非選択状態
		}

		return;
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
		$parent = $parent || data.component.tagParent;

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
	 * タグモードの切替（自動/手動）
	 */
	function changeTagAddMode( event ) {

		// 自動モードの場合
		if($(data.component.tagAddMode.selector).prop('checked')) {

			// 通話中の場合
			if( $(data.component.callContents.selector).prop('disabled')) {
				// 回答候補を表示ボタン非活性化
				$(data.component.answerCandidate.selector).prop('disabled', true);
			}

			// エスカレーションの場合
			if($(data.component.changeEscalation.selector).prop('hidden')) {
				// 回答候補を表示ボタン活性化
				$(data.component.answerCandidate.selector).prop('disabled', false);
			}

		} else {

			// 回答候補を表示ボタン活性化
			$(data.component.answerCandidate.selector).prop('disabled', false);

			// 回答候補タグエリア
			data.component.tagListArea.$element.html('');

			// ナレッジタイトル
			$(data.component.knowledgeTitleArea.selector).html('');

			// ナレッジ情報タブエリアをクリア
			_clearKnowledgeDetailTabArea();
		}
	}

}); // end of ready-handler
