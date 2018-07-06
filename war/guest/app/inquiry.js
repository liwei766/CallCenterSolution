"use strict";

/*
 * お問い合わせ画面
 */

$( function () {

	var _guest = CCS.guest;
	var _util = CCS.util;
	var _prop = CCS.prop;
	var _api  = CCS.api;

	var data = {

		// 画面構成要素の定義
		// 初期処理で各要素の jQuery オブジェクトを $element として追加する

		component : {

			// キーワードを囲むAタグ
			tagListA : {

				selector : ".ccs-a-tag",
			},

			// キーワード
			tagList : {

				selector : ".ccs-op-tag",
			},

			// ナレッジタイトルを囲むAタグ
			knowledgeTitleA : {

				selector : ".ccs-knowledge-a-tag",
			},

			// ナレッジタイトル
			knowledgeTitle : {

				selector : ".ccs-knowledge-title",
			},

			// 質問入力欄でリターンキーを押下時
			messageInput : {

				selector : "#message-to-send",
				handler : [
					[ "keypress", messageInput ]
				],
			},

			// 質問する
			messageSend : {

				selector : "#messageSend",
				handler : [
					[ "click", messageSend ]
				],
			},

			// 発話時間
			hidTime : {

				selector : ".hid-contents .message-data-time",
			},

			// 回答
			hidQaContents : {

				selector : ".hid-contents .qa-contents",
			},

			// 参照 URL
			hidQaUrl : {

				selector : ".hid-contents .qa-url",
			},

			// FAQ ナレッジタイトルエリア
			knowledgeFaqTitleArea : {

				selector : "#knowledgeFaqTitleArea",
			},

		},

		// 最後に押下されたタグ
		lastSelectedTagId: null,

		candidateTagList : [], // 選択候補のタグリスト
		dispTagIndex : -1, // 表示中のタグの配列内の順番号

		// ナレッジキーワード抽出
		extractStringCountMAX : 10000, // 抽出用文字列最大文字数

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
	$( ".chat-history" ).on( "click", data.component.tagListA.selector, selectTag );
	$( ".chat-history" ).on( "click", data.component.tagList.selector, selectTag );

	// ナレッジ選択
	$( ".chat-history" ).on("click", data.component.knowledgeTitleA.selector, incrementAndGetKnowledgeDetail );
	$( ".chat-history" ).on("click", data.component.knowledgeTitle.selector, incrementAndGetKnowledgeDetail );

	// よくある質問（ナレッジ選択）
	$( data.component.knowledgeFaqTitleArea.selector ).on( "click", data.component.knowledgeTitle.selector, incrementAndGetKnowledgeDetail );

	// -------------------------------------------------------------------------
	// 初期表示処理
	// -------------------------------------------------------------------------

// main.js chat start ----------------------------------------------------------------------------------------------

  var chat = {
    messageToSend: '',
    messageResponses: [

// inquiry 未使用

    ],
    init: function() {
      this.cacheDOM();
      this.bindEvents();
      this.render();
    },
    cacheDOM: function() {
      this.$chatHistory = $('.chat-history');
      this.$button = $('button');
      this.$text = $('#message-to-send');
      this.$chatHistoryList =  this.$chatHistory.find('ul');
    },
    bindEvents: function() {

// inquiry 未使用
//      this.$button.on('click', this.addMessage.bind(this));
//      this.$text.on('keyup', this.addMessageEnter.bind(this));
//
    },
    render: function() {
      this.scrollToBottom();
      if (this.messageToSend.trim() !== '') {
        var template = Handlebars.compile( $("#message-template").html());
        var context = {
          messageOutput: this.messageToSend,
          time: this.getCurrentTime()
        };

        this.$chatHistoryList.append(template(context));
        this.scrollToBottom();
        this.$text.val('');

      }
    },
    addMessage: function() {
      this.messageToSend = this.$text.val();
      this.render();
    },
    addMessageEnter: function(event) {
        // enter was pressed
        if (event.keyCode === 13) {
          this.addMessage();
        }
    },
    scrollToBottom: function() {
       this.$chatHistory.scrollTop(this.$chatHistory[0].scrollHeight);
    },
    getCurrentTime: function() {
      return new Date().toLocaleTimeString().
              replace(/([\d]+:[\d]{2})(:[\d]{2})(.*)/, "$1$3");
    },
    getRandomItem: function(arr) {
      return arr[Math.floor(Math.random()*arr.length)];
    },


// inquiry 追加 start
    addMessage2: function(text) {
      this.messageToSend = text;
      this.render();
    }
// inquiry 追加 end

  };

  chat.init();

  var searchFilter = {
    options: { valueNames: ['name'] },
    init: function() {
      var userList = new List('people-list', this.options);
      var noItems = $('<li id="no-items-found">No items found</li>');

      userList.on('updated', function(list) {
        if (list.matchingItems.length === 0) {
          $(list.list).append(noItems);
        } else {
          noItems.detach();
        }
      });
    }
  };

  searchFilter.init();

// main.js chat end ----------------------------------------------------------------------------------------------

	// タグ候補配列クリア
	_clearCandidateTagList();

	// よくある質問を画面に表示
	_dispFaq();

	// 初期チャットを画面に表示
	_dispDefaultChat();

	// タグ
	Handlebars.registerHelper( "tagButton", function( name, id ) {
		name = Handlebars.Utils.escapeExpression( name );
		id  = Handlebars.Utils.escapeExpression( id );

		var result = "<br /><a href='' class='ccs-a-tag'><span class='mt-1 badge badge-pill badge-default ccs-op-tag' data-ccs-tag-id='" +
						id + "' data-ccs-tag='" + name + "'>" + name + "</span></a>";

		return new Handlebars.SafeString( result );
	});

	// ナレッジ
	Handlebars.registerHelper( "knowledgeButton", function( name, id ) {
		name = Handlebars.Utils.escapeExpression( name );
		id  = Handlebars.Utils.escapeExpression( id );

		var result = "<br /><a href='' class='ccs-knowledge-a-tag'><span class='mt-1 badge badge-pill badge-default ccs-knowledge-title' data-ccs-knowledge-id='" +
						id + "' data-ccs-knowledge-title='" + name + "'>" + name + "</span></a>"

		return new Handlebars.SafeString( result );
	});

	/**
	 * 非表示 ナレッジ情報表示エリアをクリア
	 */
	function _clearHidKnowledgeDetailTabArea() {

		data.component.hidTime.$element.html( "" );
		data.component.hidQaContents.$element.html( "" ).closest( "span" ).prop( "hidden", true );
		data.component.hidQaUrl.$element.html( "" ).closest( "span" ).prop( "hidden", true );

	}

	// チャットの履歴のボタンにdisableクラス追加
	function _disableChatHistoryBottun() {

		$( ".chat-history" ).find( data.component.tagListA.selector ).addClass( "disabled cursor-def" );
		$( ".chat-history" ).find( data.component.tagList.selector ).addClass( "disabled" );
		$( ".chat-history" ).find( data.component.knowledgeTitle.selector ).addClass( "disabled");
		$( ".chat-history" ).find( data.component.knowledgeTitleA.selector ).addClass( "disabled cursor-def" );

	}

	/**
	 * 質問するボタン、FAQエリア 活性化
	 */
	function _eableForm() {

		data.component.messageSend.$element.removeClass( "disabled" );
		data.component.knowledgeFaqTitleArea.$element.find( data.component.knowledgeTitle.selector ).removeClass( "disabled" );

	}

	/**
	 * 質問するボタン、FAQエリア、チャットの履歴のボタン 非活性化
	 */
	function _disableForm() {

		data.component.messageSend.$element.addClass( "disabled" );
		data.component.knowledgeFaqTitleArea.$element.find( data.component.knowledgeTitle.selector ).addClass( "disabled" );

		_disableChatHistoryBottun();

	}

	// タグ候補クリア
	function _clearCandidateTagList() {

		data.candidateTagList = [];
		data.dispTagIndex = -1;

	}

	// -------------------------------------------------------------------------
	// イベントハンドラ
	// -------------------------------------------------------------------------

	// よくある質問表示
	function _dispFaq() {

		_searchKnowledgeFaq();

	}

	// 初期チャット
	function _dispDefaultChat() {
		var res = _prop.getMessage( "inquiry.default" );
		var contextResponse = { responseNoEscape: res };
		dispSupportMessage( 0, contextResponse, true );
	};

	//質問入力欄でリターンキーを押下時、質問メッセージを出す
	function messageInput(e) {

		//「Enter」key
		if(e.keyCode === 13){

			messageSend( e );

		}

	}

	// 質問する
	function messageSend( event ) {

		var $target = $( event.target );

		if ( $target.hasClass( "disabled") ) {

			return false;
		}

		var inputText = chat.$text.val().trim();

		inputText = inputText.slice( -data.extractStringCountMAX );

		if ( _util.isEmpty( inputText ) ) {

			return false;
		}

		_disableForm();

		chat.addMessage2( inputText );

		dispAnswerCandidate( inputText );

	}

	/**
	 * タグを指定
	 */
	function selectTag( event ) {

		data.lastSelectedTagId = null;

		var $target = $( event.currentTarget );

		if ( _util.isEmpty( $target ) || _util.isEmpty( $target.attr( "data-ccs-tag" ) ) ) {

			$target = $target.find( data.component.tagList.selector );

		}

		if ( $target.hasClass( "disabled") ) {

			return false;
		}

		_disableForm();

		if ( _util.isEmpty( $target.attr( "data-ccs-tag" ) ) ) {

			dispSupportNextMessage( 1500 );

			return false;

		}

		var id = $target.attr( "data-ccs-tag-id" );

		data.lastSelectedTagId = id;

		var tagName = $target.attr( "data-ccs-tag" );

		chat.addMessage2( tagName );

		// タグ選択/非選択状態を切り替え
		_changeTag( $target );

		if ( _util.isEmpty( id ) ) {
			// 「最初に戻る」
			dispSupportNextMessage( 1500 );

		} else if ( id == -1 ) {
			// 「いいえ」

			_dispTagSuggestion();

		} else {

			_searchKnowledge();
		}

		return false;
	}

	/**
	 * タグ選択/非選択状態を切り替え
	 *
	 */
	function _changeTag( $target ) {

		if ( _util.isEmpty( $target ) ) return false;

		if ( $target.hasClass( "badge-primary") ) {

			$target.removeClass( "badge-primary text-white" );

		} else {

			$target.addClass( "badge-primary text-white" );
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * 回答候補を表示
	 */
	function dispAnswerCandidate( inputText ) {

		data.lastSelectedTagId = null;

		if ( _util.isEmpty( inputText ) ) {

			_eableForm();

			return false;
		}

		var form = { searchForm : {
				inquiryFormSearchForm: {
					hashedCompanyId : _guest.getCompanyCode(),
					text : inputText
				}
			}
		};

		// API 送信
		var url = _prop.getApiMap( "inquiryform.knowledgeKeyword" );

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

		var msgList = [ _prop.getMessage( "inquiry.error.searchKnowledgeError" ) ];

		_dispError( msgList );

	}

	/**
	 * ナレッジキーワード抽出API用成功ハンドラ
	 */
	function _knowledgeKeywordExtractSuccess( retData, status, xhr, option ) {

		var msgList = option.result.msgList;

		if ( ! option.result.ok ) {

			_knowledgeKeywordExtractError( xhr, status, null, option );

			return false;
		}

		var searchResultList = _util.resolve( retData.searchResultList );

		//キーワードが一つの場合、該当キーワードでナレッジ検索処理を行う。
		if(searchResultList.length==1){

			data.lastSelectedTagId = searchResultList[0].tagId;

			_searchKnowledge();

			return false;

		}


		var newTagId = "";
		var newTagName = "";

		var values = {
				listdata : []
			};

		for ( var i = 0 ; i < searchResultList.length ; i++ ) {

			newTagName = searchResultList[i].keyword;
			if ( _util.isEmpty( newTagName ) ) continue;

			newTagId = searchResultList[i].tagId;
			if ( _util.isEmpty( newTagId ) ) continue;

			values.listdata[ values.listdata.length ] = { name : newTagName, id: newTagId };

		}

		data.candidateTagList = values.listdata;

		// タグ選択用発話の表示
		_dispTagSuggestion();

		return false;

	}

	// タグ選択用発話の表示
	function _dispTagSuggestion() {

		var values = {
				listdata : []
			};

		var $objTag = {};
		var $objAdd = {};
		var res = "";
		var contextResponse = {};

		var candidateTagList = data.candidateTagList;

		data.dispTagIndex++;

		if ( _util.isEmpty( candidateTagList[ data.dispTagIndex ] ) ) {

			_clearCandidateTagList();

			res = _prop.getMessage( "inquiry.retry" );
			contextResponse = { response: res };
			dispSupportMessage( 1500, contextResponse, true );

			return false;

		} else {

			$objTag = candidateTagList[ data.dispTagIndex ];

			res = _util.format(
				_prop.getMessage( "inquiry.tag" ), [ $objTag.name ] );

			$objAdd = { name : _prop.getMessage( "inquiry.yes" ), id: $objTag.id };
			values.listdata[values.listdata.length] = $objAdd;

			$objAdd = { name : _prop.getMessage( "inquiry.no" ), id: -1 };
			values.listdata[values.listdata.length] = $objAdd;

			$objAdd = { name : _prop.getMessage( "inquiry.reset" ), id:"" };
			values.listdata[ values.listdata.length ] = $objAdd;

		}

		var contextResponse = {
			response: res,
			listTag : values.listdata
		};

		dispSupportMessage( 1500, contextResponse, true );

		return false;
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
				$( "<p></p>" ).text( list[i] ) );
		}

		return $ret;
	}

	/**
	 * 回答候補選択タグからナレッジタイトルを表示
	 */
	function _searchKnowledge() {

		// 入力値取得
		var	selectedTagId = data.lastSelectedTagId;

		if ( _util.isEmpty( selectedTagId ) ) {

			_eableForm();

			return false;
		}

		var selectTagList = [];

		selectTagList.push( selectedTagId );

		if ( selectTagList.length === 0 ) {

			_eableForm();

			return false;
		}

		var form = { searchForm : {

				inquiryFormSearchForm: {
					hashedCompanyId : _guest.getCompanyCode(),
					tagId: selectTagList
				}
			}
		};

		// API 送信
		var url = _prop.getApiMap( "inquiryform.search" );

		var json = JSON.stringify( form );

		var option = {

			handleError : _knowledgeSearchError,
			handleSuccess : _knowledgeSearchSuccess
		};

		_api.postJSON( url, json, option );

		return false;

	}

	/**
	 * ナレッジ検索API(タイトル)用エラーハンドラ
	 */
	function _knowledgeSearchError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "inquiry.error.searchKnowledgeError" ) ];

		_dispError( msgList );

	}

	/**
	 * ナレッジ検索API(タイトル)用成功ハンドラ
	 */
	function _knowledgeSearchSuccess( retData, status, xhr, option ) {

		var msgList = option.result.msgList;

		if ( ! option.result.ok ) {

			_knowledgeSearchError( xhr, status, null, option );

			return false;
		}

		_clearHidKnowledgeDetailTabArea();

		if ( _util.isEmpty( retData.searchResultList ) ) {

			_eableForm();

			return false;
		}

		var searchResultList = _util.resolve( retData.searchResultList );

		//ナレッジが一つの場合、該当ナレッジの詳細取得処理を行う。
		if(searchResultList.length==1){

			let knowledgeId = searchResultList[0].knowledge.knowledgeId;

			// ナレッジ参照回数更新
			_incrementKnowledge( knowledgeId );

			// ナレッジ詳細取得
			_getKnowledgeDetail( knowledgeId );

			return false;

		}

		var knowledgeId = "";
		var title = "";

		var values = {
				listdata : []
			};

		var $objAdd = {};

		for ( var i = 0 ; i < searchResultList.length ; i++ ) {

			var knowledge = searchResultList[i].knowledge;

			if ( _util.isEmpty( knowledge.knowledgeId ) ) continue;
			if ( _util.isEmpty( knowledge.title ) ) continue;

			$objAdd = { name : knowledge.title, id: knowledge.knowledgeId };

			values.listdata[values.listdata.length] = $objAdd;

		}

		$objAdd = { name : _prop.getMessage( "inquiry.reset" ), id: "" };
		values.listdata[values.listdata.length] = $objAdd;

		var res = _prop.getMessage( "inquiry.knowledge" );
		var contextResponse = {
				response: res,
				listKnowledge : values.listdata
			};

		dispSupportMessage( 1500, contextResponse, true );

		return false;
	}

	// -------------------------------------------------------------------------

	/**
	 * ナレッジ参照回数更新と詳細取得
	 */
	function incrementAndGetKnowledgeDetail(event) {

		var $target = $( event.currentTarget );

		if ( _util.isEmpty( $target ) || _util.isEmpty( $target.attr( "data-ccs-knowledge-title" ) ) ) {

			$target = $target.find( data.component.knowledgeTitle.selector );

		}

		if ( $target.hasClass( "disabled") ) {

			return false;
		}

		_disableForm();

		if ( _util.isEmpty( $target.attr( "data-ccs-knowledge-title" ) ) ) {

			dispSupportNextMessage(1500);

			return false;

		}

		var selectKnowledgeTitle = $target.attr( "data-ccs-knowledge-title" );

		if ( ! $target.hasClass( "faq-title") ) {

			_applySelect( $target );
		}

		var selectKnowledgeId = $target.attr( "data-ccs-knowledge-id" );

		chat.addMessage2( selectKnowledgeTitle );

		if ( _util.isEmpty( selectKnowledgeId ) ) {
			// 最初に戻る
			dispSupportNextMessage( 1500 );

			return false;
		}

		// ナレッジ参照回数更新
		_incrementKnowledge( selectKnowledgeId );

		// ナレッジ詳細取得
		_getKnowledgeDetail( selectKnowledgeId );

		return false;
	}

	function _applySelect( target ) {

		$( target ).addClass( "bg-primary" );
	}

	/**
	 * ナレッジ参照回数更新
	 */
	function _incrementKnowledge( selectKnowledgeId ) {

		// 送信データ生成
		var form = { editForm : {

					hashedCompanyId : _guest.getCompanyCode(),
					knowledge : {
							knowledgeId : selectKnowledgeId
					}

			}
		};

		// API 送信
		var url = _prop.getApiMap( "inquiryform.increment" );

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

					hashedCompanyId : _guest.getCompanyCode(),
					knowledge : {
							knowledgeId : selectKnowledgeId
					}

			}
		};

		// ナレッジ取得API 送信
		var url = _prop.getApiMap( "inquiryform.get" );
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

		_clearHidKnowledgeDetailTabArea();

		var msgList = [ _prop.getMessage( "inquiry.error.getKnowledgeDetailError" ) ];

		_dispError( msgList );
	}

	/**
	 * ナレッジ取得API用成功ハンドラ
	 */
	function _getKnowledgeDetailSuccess( retData, status, xhr, option ) {

		var msgList = option.result.msgList;

		if ( ! option.result.ok ) {

			_getKnowledgeDetailError( xhr, status, null, option );

			return false;
		}

		if ( _util.isEmpty( retData.editResult.knowledge ) ) {

			_eableForm();

			return false;
		}

		var knowledge = _util.resolve( retData.editResult.knowledge );
		var idx = 0;

		_clearHidKnowledgeDetailTabArea();

		// ----- 回答

		if ( !_util.isEmpty( knowledge.content ) ) {

			data.component.hidQaContents.$element.empty().append( _textToPtagList( knowledge.content ) )
				.closest( "span" ).prop( "hidden", false );

		} else {

			data.component.hidQaContents.$element.closest( "span" ).prop( "hidden", true );

		}

		// ----- 参照 URL

		var reference = _util.resolve( retData.editResult.reference );
		var $hidQaUrl = data.component.hidQaUrl.$element;
		$hidQaUrl.empty();

		var referenceUrlFlg = false;

		if ( _util.isArray( reference ) ) {

			for ( var i = 0 ; i < reference.length ; i++ ) {

				if ( !_util.isEmpty( reference[i].referenceUrl ) ) {

					var $p = $("<p></p>").
							 append (
								$("<a></a>").addClass( "card-link" ).
									attr( "href", reference[i].referenceUrl ).
									attr( "target", "_blank" ).
									text( reference[i].referenceUrl )
							 ).appendTo( $hidQaUrl );

					referenceUrlFlg = true;

				}

			}

		}

		if ( referenceUrlFlg ) {

			$hidQaUrl.closest( "span" ).prop( "hidden", false );

		} else {

			$hidQaUrl.closest( "span" ).prop( "hidden", true );
		}


		// 発話時間
		var time = chat.getCurrentTime() + ", Today";
		data.component.hidTime.$element.text( time );

		chat.$chatHistory = $( ".chat-history" );
		chat.$chatHistoryList =  chat.$chatHistory.find( "ul" );

		setTimeout(function() {

			var detailHtml = $( ".hid-contents .knowledge-contents" ).clone(true);

			_clearHidKnowledgeDetailTabArea();

			chat.$chatHistoryList.append( detailHtml );

			chat.scrollToBottom();

			dispSupportNextMessage( 1500 );

		}.bind( chat ), 1500 );

		return false;
	}


	// -------------------------------------------------------------------------
	// FAQ f
	// -------------------------------------------------------------------------

	/**
	 * FAQを表示
	 */
	function _searchKnowledgeFaq() {

		var form = { searchForm : {
				inquiryFormSearchForm: {
					hashedCompanyId : _guest.getCompanyCode(),
					knowledge : {
						tagId: []
					}
				}
			}
		};

		// API 送信
		var url = _prop.getApiMap( "inquiryform.search" );

		var json = JSON.stringify( form );

		var option = {

			handleError : _knowledgeFaqSearchError,
			handleSuccess : _knowledgeFaqSearchSuccess
		};

		_api.postJSON( url, json, option );

		return false;

	}

	/**
	 *  FAQ ナレッジ検索API(タイトル)用エラーハンドラ
	 */
	function _knowledgeFaqSearchError( xhr, status, errorThrown, option ) {

		var msg = _prop.getMessage( "inquiry.error.searchKnowledgeError" );

		var $parent = $( data.component.knowledgeFaqTitleArea.selector );

		var $div = $( "<li></li>" ).
			addClass( "clearfix" ).
			append(
				$( "<span></span>" ).addClass( "text-danger" ).text( msg )
			).appendTo( $parent );

		return false;
	}

	/**
	 * FAQ ナレッジ検索API(タイトル)用成功ハンドラ
	 */
	function _knowledgeFaqSearchSuccess( retData, status, xhr, option ) {

		var msgList = option.result.msgList;

		if ( ! option.result.ok ) {

			_knowledgeFaqSearchError( xhr, status, null, option );

			return false;
		}

		// 検索結果をいったんクリア
		$( data.component.knowledgeFaqTitleArea.selector ).html( "" );

		if ( _util.isEmpty( retData.searchResultList ) ) {

			return false;
		}

		var searchResultList = _util.resolve( retData.searchResultList );

		var $parent = $( data.component.knowledgeFaqTitleArea.selector );

		for ( var i = 0 ; i < searchResultList.length ; i++ ) {

			var knowledge = searchResultList[i].knowledge;

			if ( _util.isEmpty( knowledge.knowledgeId ) ) {

				continue;
			}

			var $div = $( "<li></li>" ).
				addClass( "clearfix" ).
				append(
					$( "<span></span>" ).addClass( "font-weight-bold" ).text( "Q. " ),
					$( "<a></a>" ).
					addClass( "card-link ccs-knowledge-title faq-title" ).
					attr( { "href" : "",
							"data-ccs-knowledge-id" : knowledge.knowledgeId,
							"data-ccs-knowledge-title" : knowledge.title,
						} ).
					text( knowledge.title )
				).appendTo( $parent );
		}

		return;
	}

	// -------------------------------------------------------------------------
	// チャットメッセージ
	// -------------------------------------------------------------------------
	/**
	 * 次メッセージ表示(サポート)
	 */
	function dispSupportNextMessage( timer ) {

		// タグ候補配列クリア
		_clearCandidateTagList();

		var res = _prop.getMessage( "inquiry.next" );
		var contextResponse = {
				response: res
			};

		dispSupportMessage( timer, contextResponse, true );

	}

	/**
	 * メッセージ表示(サポート)
	 */
	function dispSupportMessage( timer, contextResponse , enableFlg ) {

		// チャット履歴のボタン無効化
		_disableChatHistoryBottun();

		var templateResponse = Handlebars.compile( $( "#message-response-template" ).html() );

		contextResponse.time = chat.getCurrentTime();

		chat.$chatHistory = $( ".chat-history" );
		chat.$chatHistoryList = chat.$chatHistory.find( "ul" );

		setTimeout(function() {

			chat.$chatHistoryList.append( templateResponse( contextResponse ) );
			chat.scrollToBottom();

			if ( enableFlg ) {

				_eableForm();

			}
		}.bind( chat ), timer);

	}

	// -------------------------------------------------------------------------
	// エラー
	// -------------------------------------------------------------------------

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

		msg = _util.escapeHTML( msg );
		msg = msg.replace( /\n|\r\n|\r/g, "<br />" );
		msg = '<span class="text-danger">' + msg + "</span>";

		var contextResponse = { responseNoEscape: msg };
		dispSupportMessage( 3000, contextResponse, false );

		dispSupportNextMessage( 4500 );

	}

}); // end of ready-handler
