"use strict";

/*
 * ナレッジ管理画面
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

			searchMsgParent : {

				selector : "#searchMsgParent",
			},

			searchMsg : {

				selector : "#searchMsg",
			},

			searchTitle : {			// ----- 検索タイトル

				selector : "#inputSearch",
			},

			search : {				// ----- 検索ボタン

				selector : "#search",
				handler : [
					[ "click", search ]
				]
			},

			knowledgeParent : {		// ----- 一覧

				selector : "#main-table tbody",	// <tbody>
			},

			knowledgeList : {		// ----- ナレッジリスト

				selector : ".ccs-knowledge",		// 全ての行 <tr>：一覧コンテナからの相対
			},

			listTitle : {

				selector : ".ccs-admin-dispTitle",		// タイトル <td>：一覧コンテナからの相対
			},

			editParent : {	// ----- 編集エリア

				selector : "#editParent",
			},

			dispNo : {		// ----- ナレッジ番号

				selector : "#dispNo"
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

				selector : ".ccs-admin-tag",			// 全てのタグ <span>：タグリストコンテナからの相対
			},

			deleteTag : {	// ----- タグ削除ボタン

				selector : ".ccs-admin-delete-tag",		// 全てのタグ削除 <span>：タグリストコンテナからの相対
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

			update : {		// ----- 更新ボタン

				selector : "#update",
				handler : [
					[ "click", update ]
				],
			},

			//-- ナレッジ一覧のソート
			sortNo : {

				selector : "#sortNo",
				handler : [
					[ "click", sort ]
				],
			},

			sortTitle : {

				selector : "#sortTitle",
				handler : [
					[ "click", sort ]
				],
			},

			sortId : {

				selector : "#sortId",
				handler : [
					[ "click", sort ]
				],
			},

			sortTime : {

				selector : "#sortTime",
				handler : [
					[ "click", sort ]
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

		lastSelectedSortItem : null,	// 最後に選択されたソート項目
		maxResult : 1000,	// ナレッジ一覧の最大表示件数
		knowledge : {},	// 編集中のナレッジ情報
		editFlg : 0,     // 編集エリア活性化フラグ
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

	// ----- ナレッジタイトル click

	data.component.knowledgeParent.$element.on( "click", data.component.listTitle.selector, edit );

	// ----- タグ削除 click

	data.component.tagParent.$element.on( "click", data.component.deleteTag.selector, deleteTag );

	// -------------------------------------------------------------------------
	// 初期表示処理
	// -------------------------------------------------------------------------

	init();

	/**
	 * 初期表示処理.
	 */
	function init() {

		_disableForm();	// 編集エリア非活性化
		_clearForm();

		data.component.sortTime.$element.click();	// ナレッジ一覧取得（更新日時降順）

		data.refUrlCount = 1;	// 参照URL表示件数
		data.manualCount = 1;	// マニュアル表示件数
	}

	// -------------------------------------------------------------------------
	// 共通の表示制御
	// -------------------------------------------------------------------------

	/**
	 * 編集エリア非活性化.
	 */
	function _disableForm() {

		data.component.editParent.$element.find( "form *").prop( "disabled", true );
		data.component.update.$element.prop( "disabled", true );
		data.component.refUrlListContainer.$element.find( "input").prop( "disabled", true );
		data.component.manualListContainer.$element.find( "input").prop( "disabled", true );
		data.editFlg = 0;
	}

	/**
	 * 編集エリア活性化.
	 */
	function _enableForm() {

		data.component.editParent.$element.find( "form *").prop( "disabled", false );
		data.component.update.$element.prop( "disabled", false );
		data.component.refUrlListContainer.$element.find( "input").prop( "disabled", false );
		data.component.manualListContainer.$element.find( "input").prop( "disabled", false );
		data.editFlg = 1;
	}

	/**
	 * ナレッジ一覧の選択解除.
	 */
	function _clearSelect() {

		data.component.knowledgeParent.$element.find( data.component.knowledgeList.selector ).removeClass( "bg-primary" );
	}

	/**
	 * ナレッジ一覧の指定行を選択状態にする.
	 *
	 * @param {Element|jQuery} target 選択状態にする <tr> 要素
	 */
	function _applySelect( target ) {

		$( target ).addClass( "bg-primary" );
	}

	// -------------------------------------------------------------------------
	// イベントハンドラ：ナレッジ一覧
	// -------------------------------------------------------------------------

	/**
	 * フォーム入力内容からナレッジ更新 API への送信オブジェクトを作成する.
	 *
	 * @param asc true = 昇順, false = 降順
	 * @return {Object} ナレッジ更新 API への送信オブジェクト
	 */
	function _createSortForm(id, asc) {

		var inputTitle = data.component.searchTitle.$element.val();

		var sortElementName;
		switch (id) {
		case "sortNo":
			sortElementName = "knowledge.knowledgeNo";
			break;
		case "sortTitle":
			sortElementName = "knowledge.title";
			break;
		case "sortId":
			sortElementName = "knowledge.updateUserId";
			break;
		case "sortTime":
			sortElementName = "knowledge.updateDate";
			break;
		}

		var form = { searchForm : {
			knowledge : {
				title : inputTitle,
				titleOption : "3"	// タイトル部分一致
			},
			sortForm : {
				maxResult : data.maxResult,
				sortElement : [
					{
						"name" : sortElementName,
						"asc" : asc
					}
				]
			}
		}};

		return form;
	}


	/**
	 * ソート方向を取得.
	 *
	 * @return ソート方向のboolean
	 */
	function _getOrder( event ) {

		var $target = $( event.target );

		if ( _util.isEmpty( $target ) ) return false;

		data.lastSelectedSortItem = $target;

		$target.closest(":button").blur(); // クリック時の青枠対策のためフォーカス外す

		if ( $target.hasClass("fa-sort-desc") ) {

			return true;

		} else {

			return false;
		}

	}

	/**
	 * ▼の状態を反転する.
	 * ※HTML側の「fa-sort～」クラス属性は「fa」とセットで使用すること
	 *
	 */
	function _flipOrder() {

		var $target = data.lastSelectedSortItem;
		data.lastSelectedSortItem = null;

		if ( _util.isEmpty( $target ) ) {
			// ソート項目クリア
			$("#t1").find('i')
			.removeClass("fa-sort-asc fa-sort-desc")
			.addClass("fa-sort");

			return;
		}

		// ソート項目クリア(選択項目以外)
		$("#t1").find('i').not( $target )
		.removeClass("fa-sort-asc fa-sort-desc")
		.addClass("fa-sort");

		if ( $target.hasClass("fa-sort") ) {

			$target
			.removeClass("fa-sort fa-sort-asc")
			.addClass("fa-sort-desc");

		} else {

			if ( $target.hasClass("fa-sort-desc") ) {

				$target
				.removeClass("fa-sort fa-sort-desc")
				.addClass("fa-sort-asc");

			} else if ($target.hasClass("fa-sort-asc") ) {

				$target
				.removeClass("fa-sort fa-sort-asc")
				.addClass("fa-sort-desc");
			}
		}
	}

	/**
	 * ソート.
	 *
	 * @param {Event} event イベント
	 */
	function sort(event) {

		var asc = _getOrder(event);

		var id = event.target.id;
		var form = _createSortForm(id, asc);

		var url = _prop.getApiMap("knowledge.search");

		var json = JSON.stringify(form);

		var option = {
			handleError : _searchError,
			handleSuccess : _searchSuccess
		};

		_api.postJSON(url, json, option);
	}
	// -------------------------------------------------------------------------

	/**
	 * 検索ボタン押下処理.
	 *
	 * @param {Event} event イベント
	 */
	function search( event ) {

		data.component.sortTime.$element
		.removeClass("fa-sort-asc fa-sort-desc")
		.addClass("fa-sort")
		.click(); // 一覧取得（更新日時降順）

	}

	/**
	 * 再検索
	 *
	 */
	function _searchRe() {

		var sortId = "sortTime";
		var asc = false;

		var $target = $("#t1").find("i.fa-sort-asc");
		if (_util.isNotEmpty( $target[0] )) {

			sortId = $target[0].id;
			asc = true;

		} else {

			$target = $("#t1").find("i.fa-sort-desc");
			if (_util.isNotEmpty( $target[0] )) {
				sortId = $target[0].id;
			}
		}

		var form = _createSortForm(sortId, asc);

		var url = _prop.getApiMap( "knowledge.search" );
		var json = JSON.stringify( form );

		var option = {

			handleError : _searchError,
			handleSuccess : _searchSuccess
		};

		_api.postJSON( url, json, option );
	}

	/**
	 * 検索 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _searchError( xhr, status, errorThrown, option ) {

		data.lastSelectedSortItem = null;

		var msgList = [ _prop.getMessage( "admin.error.listError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );
	}

	/**
	 * 検索 API 正常終了処理.
	 *
	 * @param {Object} response レスポンス内容（JSON をパースしたオブジェクト）
	 * @param {String} status 通信結果
	 * @param {jqXHR} xhr
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _searchSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_searchError( xhr, status, null, option );
			return;
		}

		// ----- 正常終了

		data.component.searchMsg.$element.text( "" );

		// 件数オーバーしている場合はメッセージを表示

		if ( response.resultList[0].code === _prop.getProperty( "common.apiResponse.overflow" ) ) {

			var max = _util.isNotEmpty( response.resultList[0].detailList ) ?
				response.resultList[0].detailList[0] : data.maxResult;

			data.component.searchMsg.$element.text(
				_util.format( _prop.getMessage( "admin.overflow" ), max ) );

			data.component.searchMsgParent.$element.prop('hidden', false);

		} else {

			data.component.searchMsgParent.$element.prop('hidden', true);

		}

		// 一覧表示

		if ( _util.isNotEmpty( data.lastSelectedSortItem ) ) {

			_flipOrder(); // ソート項目表示変更

		}


		var $parent = data.component.knowledgeParent.$element;

		$parent.empty();

		$.each( response.searchResultList, function ( i, val ) {

			var know = val.knowledge;

			var id = know.knowledgeId;
			var no = know.knowledgeNo;
			var title = know.title;
			var user = know.updateUserId;
			var date = know.updateDate.substr( 0, 19 ); // 日付部分のみ
			var stat = _prop.getLabel( "knowledgeStatus" );

			var $tr = $( "<tr></tr>" )
				.addClass( "ccs-knowledge" );

			var $id = $( "<th></th>" ).text( no ).appendTo( $tr );

			var $title = $( "<td></td>" )
				.addClass( "ccs-admin-dispTitle" )
				.attr( "data-ccs-id", id )
				.text( title ).appendTo( $tr );

			var $user = $( "<td></td>" ).text( user ).appendTo( $tr );
			var $date = $( "<td></td>" ).text( date ).appendTo( $tr );
			var $stat = $( "<td></td>" ).text( stat ).appendTo( $tr );

			$tr.appendTo( $parent );
		});

		$( "#t1 tbody" ).empty().append( $parent.children().clone() );

		_disableForm();	// 編集エリア非活性化
		_clearForm();
	}

	// -------------------------------------------------------------------------

	/**
	 * 一覧上のナレッジ選択処理：ナレッジ内容取得／表示
	 *
	 * @param {Event} event イベント
	 */
	function edit( event ) {

		var id = $( event.target ).attr( "data-ccs-id" );

		if ( _util.isEmpty( id ) ) return false;

		// ----- 編集エリアをクリア＆非活化

		_disableForm();
		_clearSelect();
		_applySelect( $( event.target ).closest( "tr" ) );

		data.component.editParent.$element.prop( "disabled", true );
		data.knowledge = {};

		// ----- API 呼び出し

		var form = { editForm : { knowledge : {

			knowledgeId : id,

		} } };

		var url = _prop.getApiMap( "knowledge.get" );
		var json = JSON.stringify( form );

		var option = {

			handleError : _getError,
			handleSuccess : _getSuccess
		};

		_api.postJSON( url, json, option );
	}

	/**
	 * ナレッジ内容取得 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _getError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "admin.error.getError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );
		_clearSelect();
		_clearForm();
	}

	/**
	 * ナレッジ内容取得 API 正常終了処理.
	 *
	 * @param {Object} response レスポンス内容（JSON をパースしたオブジェクト）
	 * @param {String} status 通信結果
	 * @param {jqXHR} xhr
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _getSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_getError( xhr, status, null, option );
			return;
		}

		// 正常終了

		data.knowledge = response.editResult.knowledge;

		var know = response.editResult.knowledge;
		var tagList = response.editResult.tag || [];

		data.component.dispNo.$element.text( "#" + know.knowledgeNo );
		data.component.title.$element.val( know.title );
		data.component.content.$element.val( know.content );
		data.component.script.$element.val( know.script );

		data.component.tagParent.$element.empty();

		$.each( tagList, function ( i, val ) {

			_addTag( data.component.tagParent.$element, val.tagName );
		});

		data.component.newTagName.$element.val( "" );

		// 参照URLとマニュアル表示初期化
		_initReferenceManual();

		// 参照URLリスト
		var i = 0;
		var responseRefUrlList = response.editResult.reference || [];

		if ( responseRefUrlList.length > data.refUrlCount ) {

			for ( i = 0; i < ( responseRefUrlList.length -1 ); i++ ) {

				if ( data.refUrlCount < data.refUrlMax ) {

			        var content = "<div class='col-lg-12'>";
			        content += "<label for='inputUrl'>参照URL</label>";
			        content += "<input type='text' id='inputUrl" + data.refUrlCount + "' class='form-control w-100' maxlength='255' placeholder=''>";
			        content += "</div>";

			        $( "#refUrlList" ).append( content );

			        data.refUrlCount ++ ;

				}
			}
		}

        if ( data.refUrlCount < data.refUrlMax ) {

        	data.component.addRefUrl.$element.find( "i" ).removeClass( "del_btn_disabed_color" );
        }

		if ( responseRefUrlList.length > 0 ) {

			var $refUrlList = $( data.component.refUrlListContainer.$element );

			// 参照URL
			$refUrlList.find( data.component.refUrl.selector ).each(function( idx , element ) {

				$( element ).val( "" );
				$( element ).val( responseRefUrlList[idx].referenceUrl );
			});
		}

		// マニュアルリスト
		var responseManualList = response.editResult.manual || [];

		if ( responseManualList.length > data.manualCount ) {

			for ( i = 0; i < ( responseManualList.length -1 ); i++ ) {

				if ( data.manualCount < data.manualMax ) {

			        var mcontent = "<div class='col-xl-auto'>&nbsp;</div>";
			        mcontent += "<div class='row'>";
			        mcontent += "<div class='col-lg-8'>";
			        mcontent += "<label for='inputManualName '>タイトル</label>";
			        mcontent += "<input type='text' class='form-control w-100' id='inputManualName" + data.manualCount + "' maxlength='100' placeholder=''>";
			        mcontent += "</div>";
			        mcontent += "<div class='col-lg-4'>";
			        mcontent += "<label for='inputManualPage'>ページ</label>";
			        mcontent += "<input type='text' class='form-control w-100' id='inputManualPage" + data.manualCount + "' maxlength='10' placeholder=''>";
			        mcontent += "</div>";
			        mcontent += "<div class='col-lg-12'>";
			        mcontent += "<label for='inputManualUrl '>参照URL</label>";
			        mcontent += "<input type='text' class='form-control w-100' id='inputManualUrl" + data.manualCount + "' maxlength='255' placeholder=''>";
			        mcontent += "</div>";
			        mcontent += "</div>";

			        $( "#manualList" ).append( mcontent );

			        data.manualCount ++ ;
				}
			}
		}

        if ( data.manualCount < data.manualMax ) {

        	data.component.addManual.$element.find( "i" ).removeClass( "del_btn_disabed_color" );
        }

		if ( responseManualList.length > 0 ) {

			var $manualListCon = $( data.component.manualListContainer.$element );

			$manualListCon.find( data.component.manualListContainerSelector.selector ).each( function( idxManual, elementRow ) {

				var page = responseManualList[idxManual].manualPage;
				var url = responseManualList[idxManual].manualUrl;
				var title = responseManualList[idxManual].manualName;

				// タイトル
				$( elementRow ).find( data.component.manualTitleContainer.selector ).each( function( idxTitleCon, elementCol8 ) {
					$( elementCol8 ).find( data.component.manualTitle.selector ).each( function( idxTitle, elementTitle ) {

						$( elementTitle ).val( "" );
						$( elementTitle ).val( title );
					});
				});

				// ページ
				$ (elementRow ).find( data.component.manualPageContainer.selector ).each( function( idxPageCon, elementCol4 ) {
					$( elementCol4 ).find( data.component.manualPage.selector ).each( function( idxPage, elementPage ) {

						$( elementPage ).val( "" );
						$( elementPage ).val( page );
					});
				});

				// 参照URL
				$( elementRow ).find( data.component.manualUrlContainer.selector ).each( function( idxUrlCon, elementCol12 ) {
					$( elementCol12 ).find( data.component.manualUrl.selector ).each( function( idxUrl, elementUrl ) {

						$( elementUrl ).val( "" );
						 $( elementUrl ).val( url );
					});
				});
			});
		}

		_enableForm();
	}

	// -------------------------------------------------------------------------
	// イベントハンドラ：ナレッジ更新
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
	 * 	class="badge-small badge-pill badge-default ccs-admin-tag">
	 * 	data-ccs-tag="（タグ名）"
	 * >
	 * タグ名
	 * 		<span class="ccs-admin-delete-tag" aria-hidden="true"> ×<s/span>
	 * </span>
	 *
	 * @param {jQuery} $parent 追加先要素
	 * @param {String} tagName 追加表示するタグ名
	 */
	function _addTag( $parent, tagName ) {

		tagName = _trimTag( tagName );
		$parent = $parent || data.component.tagParent.$element;

		var $newTag = $("<span></span>")
			.addClass( "badge badge-pill badge-default" )
			.addClass( "ccs-admin-tag" )
			.attr( "data-ccs-tag", tagName )
			.text( tagName );

		var $button = $("<span></span>")
			.attr( "aria-hidden", true )
			.addClass( "ccs-admin-delete-tag" )
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

		var msgList = [ _prop.getMessage( "admin.error.keywordError" ) ];

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
	 * 更新ボタン押下処理.
	 *
	 * @param {Event} event イベント
	 */
	function update( event ) {

		_view.confirmDialog( _prop.getMessage( "common.confirm.update" ), { ok : _updateOk } );
	}

	/**
	 * 更新確認ダイアログの OK ボタン押下処理
	 *
	 * @param {Event} event イベント
	 */
	function _updateOk( event ) {

		// 入力値取得

		var form = _createUpdateForm();

		// API 送信

		var url = _prop.getApiMap( "knowledge.update" );
		var json = JSON.stringify( form );
		var option = {

			handleError : _updateError,
			handleSuccess : _updateSuccess
		};

		_api.postJSONSync( url, json, option );

		return false;
	}

	/**
	 * フォーム入力内容からナレッジ更新 API への送信オブジェクトを作成する.
	 *
	 * @return {Object} ナレッジ更新 API への送信オブジェクト
	 */
	function _createUpdateForm() {

		var know = data.knowledge;

		know.title = data.component.title.$element.val();
		know.content = data.component.content.$element.val();
		know.script = data.component.script.$element.val();

		var tagList = [];

		data.component.tagParent.$element.find( data.component.tagList.selector ).each( function( idx, element ){

			tagList.push( $( element ).attr( "data-ccs-tag" ) );
		});

		// 参照URLリスト
		var refUrlList = [];

		var $refUrlList = $( data.component.refUrlListContainer.$element );
		$refUrlList.find( data.component.refUrl.selector ).each( function( idx, element ){

			if ( $( element ).val() != "" ) {

				refUrlList.push( $( element ).val() );
			}
		});

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
				});
			});

			// ページ
			$( element ).find( data.component.manualPageContainer.selector ).each( function( idx, element ) {
				$( element ).find( data.component.manualPage.selector ).each( function( idx, element ) {

					page = $(element).val();
				});
			});

			// 参照URL
			$( element ).find( data.component.manualUrlContainer.selector ).each( function( idx, element ) {
				$( element ).find( data.component.manualUrl.selector ).each( function( idx, element ) {

					url = $( element ).val();
				});
			});

			if ( title != "" || page != "" || url != "" ) {

				var manualObj = { manualName:title, manualPage:page, manualUrl:url };
				manualList.push( manualObj );
			}
		});

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
	 * ナレッジ更新 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _updateError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "common.error.update" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );
	}

	/**
	 * ナレッジ更新 API 正常終了処理.
	 *
	 * @param {Object} response レスポンス内容（JSON をパースしたオブジェクト）
	 * @param {String} status 通信結果
	 * @param {jqXHR} xhr
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _updateSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_updateError( xhr, status, null, option );
			return;
		}

		// 正常終了

		_view.infoDialog( _prop.getMessage( "common.complete.update" ) );

		_disableForm();
		_clearForm();
		_clearSelect();

		// 再検索
		_searchRe();

	}

	/**
	 * ナレッジ更新完了後のフォームクリア.
	 */
	function _clearForm() {

		_initReferenceManual();

        data.component.editParent.$element.find( "form" )[0].reset();

		data.component.content.$element.val( "" ); // reset() でクリアされない
		data.component.script.$element.val( "" ); // reset() でクリアされない
		data.component.tagParent.$element.empty();

		data.component.dispNo.$element.text( _prop.getMessage( "admin.noSelect" ) );
	}

	/**
	 * 参照先とマニュアル表示初期化.
	 */
	function _initReferenceManual() {

		// 参照先初期化
		$( data.component.refUrlListContainer.selector ).html( '' );
        data.refUrlCount = 1;

        var content = "<div class='col-lg-12'>";
        content += "<input type='text' id='inputUrl0' class='form-control w-100' maxlength='255' placeholder=''>";
        content += "</div>";

        $( "#refUrlList" ).append( content );
		data.component.refUrlListContainer.$element.find( "input" ).prop( "disabled", true );

        data.component.addRefUrl.$element.find( "i" ).addClass( "del_btn_disabed_color" );

		// マニュアル初期化
		$( data.component.manualListContainer.selector ).html( '' );
        data.manualCount = 1;

        var mcontent = "<div class='col-xl-auto'>&nbsp;</div>";
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
		data.component.manualListContainer.$element.find( "input" ).prop( "disabled", true );

        data.component.addManual.$element.find( "i" ).addClass( "del_btn_disabed_color" );
	}

	/**
	 * 参照URL追加.
	 */
	function addInputRefUrl() {

		if ( data.editFlg == 1 && data.refUrlCount < data.refUrlMax ) {

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

		if ( data.editFlg == 1 && data.manualCount < data.manualMax ) {

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

}); // end of ready-handler

