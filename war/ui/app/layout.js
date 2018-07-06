"use strict";

/*
 * 画面レイアウト処理
 */

$( function () {

	var _util = CCS.util;
	var _prop = CCS.prop;
	var _view = CCS.view;
	var _user = CCS.user;

	var data = {

		component : {

			bar : {			// ----- ナビゲーションバー

				selector : _prop.getProperty( "layout.bar.selector" ),
			},

			userId : {				// ----- ユーザ ID

				selector : _prop.getProperty( "layout.userId.selector" ),
			},

			userName : {			// ----- ユーザ名

				selector : _prop.getProperty( "layout.userName.selector" ),
			},

			logout : {		// ----- ログアウトボタン

				selector : _prop.getProperty( "layout.logout.selector" ),
				handler : [
					[ "click", logout ],
				],
			},

			menu : {		// ----- サイドバー（メニューバー）

				selector : _prop.getProperty( "layout.menu.selector" ),
			},

			operation : {		// ----- オペレーションボタン

				selector : _prop.getProperty( "layout.operation.selector" ),
				handler : [
					[ "click", goOperation ],
				],
			},

			admin : {			// ----- 管理画面ボタン

				selector : _prop.getProperty( "layout.admin.selector" ),
				handler : [
					[ "click", goAdmin ],
				],
			},

			edit : {			// ----- 登録画面ボタン

				selector : _prop.getProperty( "layout.edit.selector" ),
				handler : [
					[ "click", goEdit ],
				],
			},

			csvimport : {		// ----- ナレッジ一括登録画面ボタン

				selector : _prop.getProperty( "layout.csvimport.selector" ),
				handler : [
					[ "click", goCsvImport ],
				],
			},

			lexicon : {			// ----- ユーザ辞書登録画面ボタン

				selector : _prop.getProperty( "layout.lexicon.selector" ),
				handler : [
					[ "click", goLexicon ],
				],
			},

			companySetting : {			// ----- 企業設定画面ボタン

				selector : _prop.getProperty( "layout.companySetting.selector" ),
				handler : [
					[ "click", goCompanySetting ],
				],
			},

			usetime : {			// ----- 利用時間確認画面ボタン

				selector : _prop.getProperty( "layout.usetime.selector" ),
				handler : [
					[ "click", goUsetime ],
				],
			},

			license : {			// -----ライセンス管理画面ボタン

				selector : _prop.getProperty( "layout.license.selector" ),
				handler : [
					[ "click", goLicense ],
				],
			},

			companyManagement : {			// -----企業管理画面ボタン

				selector : _prop.getProperty( "layout.companyManagement.selector" ),
				handler : [
					[ "click", goCompanyManagement ],
				],
			},

			agencyManagement : {			// -----代理店管理画面ボタン

				selector : _prop.getProperty( "layout.agencyManagement.selector" ),
				handler : [
					[ "click", goAgencyManagement ],
				],
			},

			callLogManagement : {			// -----通話履歴管理画面ボタン

				selector : _prop.getProperty( "layout.callLogManagement.selector" ),
				handler : [
					[ "click", goCallLogManagement ],
				],
			},

			checkwordManagement : {			// -----チェックワード管理画面ボタン

				selector : _prop.getProperty( "layout.checkwordManagement.selector" ),
				handler : [
					[ "click", goCheckwordManagement ],
				],
			},

			password: {		// ----- パスワード管理画面ボタン

				selector: _prop.getProperty("layout.password.selector"),
				handler : [
					[ "click", goPassword ],
				],
			},
		},

		root: null // ルートパス

	}; // end of data

	// -------------------------------------------------------------------------
	// イベントハンドラ登録 (メソッド版)
	// -------------------------------------------------------------------------

	// 1. data.component の selector がある項目について、
	//    jquery オブジェクトを作成して $element として格納する
	// 2. data.component の handler がある項目について、
	//    指定されたイベントのイベントハンドラを登録する
	//
	function _setEventHandler() {

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

	}

	/**
	 * 画面遷移やウィンドウを閉じる前に確認ダイアログが表示されるようにする.
	 *
	 * 確認メッセージはブラウザごとに決まっているので、アプリで指定することはできない.
	 */
	function _setBeforeUnload() {

		$(window).on( "beforeunload", function( event ) {

			var msg = "本当に閉じますか？";  // 文言はダミー
			event.returnValue = msg;
			return msg;
		});
	}

	/**
	 * 画面遷移やウィンドウを閉じる前の確認ダイアログ表示を解除する.
	 *
	 * アプリ側で確認済みの場合や、ダイアログ不要の場合に使用する.
	 */
	function _unsetBeforeUnload() {

		$(window).off( "beforeunload" );
	}

	// -------------------------------------------------------------------------
	// 初期表示
	// -------------------------------------------------------------------------

	// ----- 画面遷移／閉じる前の確認
	_setBeforeUnload();

	data.root = _prop.getProperty( "common.root" );

	// ナビメニューHTMLパーツ適用、その後に左サイドバーHTMLパーツ適用、初期表示処理
	var ccsNavbar = _prop.getProperty( "layout.ccsNavbar.selector" );
	if ( $( ccsNavbar ).length ) {

		$( ccsNavbar ).load( data.root + "/ui/app/parts/navbar.html", null, _setUserInfo);
	}

	/**
	 * ユーザ情報を画面にセット
	 */
	function _setUserInfo() {

		var $userId = $( data.component.userId.selector );
		$userId.text( _user.getData( _user.USER_ID ) ).attr( "title", _user.getData( _user.USER_ID ) );

		var $userName = $( data.component.userName.selector );
		$userName.text( _user.getData( _user.USER_NAME ) ).attr( "title", _user.getData( _user.USER_NAME ) );

		// 左サイドバーHTMLパーツ適用
		var sidebar = _prop.getProperty( "layout.sidebar.selector" );

		if ( $( sidebar ).length ) {

			$( sidebar ).load( data.root + "/ui/app/parts/sidebar.html", null, _initialDisp);
		}

		return true;
	}

	/**
	 * 初期表示処理
	 */
	function _initialDisp() {

		var currentView = _util.currentModuleName();

		// main_parts.jsをここで読み込み
		$.getScript( data.root + "/ui/app/lib/optim/main_parts.js" );

		// メニュー
		var $menu = $( data.component.menu.selector) ;
		if ( _util.isNotUndefined( $menu ) ) {

			$menu.find( ".ccs-name-" + currentView ).addClass( "open" );
			$menu.find( ".ccs-name-" + currentView ).children().addClass( "active" );
			$menu.fadeIn(1000);
		}

		// イベントハンドラ登録
		_setEventHandler();

		// ----- 権限反映
		_view.setComponent( $( "body" ) );

		// ----- 描画完了するまで画面全体を非表示

		$( "body" ).removeClass( "invisible" );

		return true;
	}

	// -------------------------------------------------------------------------
	// イベントハンドラ
	// -------------------------------------------------------------------------

	/**
	 * ログアウト押下処理：ログアウト処理
	 *
	 * @param {Event} event イベント
	 */
	function logout( event ) {

		_unsetBeforeUnload();

		_util.clearSession();

		_util.redirect(  _prop.getRedirectMap( "logout.url" ) );

		return true;

	}

	/**
	 * オペレーションボタン押下処理：オペレーション画面に遷移
	 *
	 * @param {Event} event イベント
	 */
	function goOperation( event ) {

		var toView = "operation";
		var currentView = _util.currentModuleName();

		if ( toView == currentView ) return false;

		if ( $( event.currentTarget ).hasClass( "disabled" ) ) return false;

		var msg = _prop.getMessage( "layout.confirm.operation" );

		_view.confirmDialog( msg, {
			ok : _goOk,
			url : _prop.getRedirectMap( "operation.url" )
		 });

		return false;
	}

	/**
	 * 画面遷移確認ダイアログの OK ボタン押下処理.
	 *
	 * @param {Event} event イベント
	 */
	function _goOk( event ) {

		var $dialog = $( event.target ).closest( _prop.getProperty( "layout.dialog.selector" ) );
		var option = $dialog.data( "option" );

		_unsetBeforeUnload();
		_util.redirect( option.url );

		return true;
	}

	/**
	 * 管理画面ボタン押下処理：ナレッジ管理画面に遷移
	 *
	 * @param {Event} event イベント
	 */
	function goAdmin( event ) {

		var toView = "admin";
		var currentView = _util.currentModuleName();

		if ( toView == currentView ) return false;

		if ( $( event.currentTarget ).hasClass( "disabled" ) ) return false;

		var msg = _prop.getMessage( "layout.confirm.admin" );

		_view.confirmDialog( msg, {
			ok : _goOk,
			url : _prop.getRedirectMap( "admin.url" )
		 });

		return false;
	}

	/**
	 * 登録画面ボタン押下処理：ナレッジ登録画面に遷移
	 *
	 * @param {Event} event イベント
	 */
	function goEdit( event ) {

		var toView = "edit";
		var currentView = _util.currentModuleName();

		if ( toView == currentView ) return false;

		// 通話終了からの遷移時はダイアログを表示せずに、即時遷移

		var callEnd = _util.getSession( _prop.getProperty( "common.sessionKey.callLogId" ) );

		if ( callEnd ) {

			_unsetBeforeUnload();
			_util.redirect( _prop.getRedirectMap( "edit.url" ) );
			return false;
		}

		if ( $( event.currentTarget ).hasClass( "disabled" ) ) return false;

		var msg = _prop.getMessage( "layout.confirm.edit" );

		_view.confirmDialog( msg, {
			ok : _goOk,
			url : _prop.getRedirectMap( "edit.url" )
		 });

		return false;
	}

	/**
	 * ナレッジ一括登録ボタン押下処理：CSV インポート画面に遷移
	 *
	 * @param {Event} event イベント
	 */
	function goCsvImport( event ) {

		var toView = "csvimport";
		var currentView = _util.currentModuleName();

		if ( toView == currentView ) return false;

		if ( $( event.currentTarget ).hasClass( "disabled" ) ) return false;

		var msg = _prop.getMessage( "layout.confirm.csvimport" );

		_view.confirmDialog( msg, {
			ok : _goOk,
			url : _prop.getRedirectMap( "csvimport.url" )
		 });

		return false;
	}

	/**
	 * ユーザ辞書登録画面ボタン押下処理：ユーザ辞書登録画面に遷移
	 *
	 * @param {Event} event イベント
	 */
	function goLexicon( event ) {

		var toView = "lexicon";
		var currentView = _util.currentModuleName();

		if ( toView == currentView ) return false;

		if ( $( event.currentTarget ).hasClass( "disabled" ) ) return false;

		var msg = _prop.getMessage( "layout.confirm.lexicon" );

		_view.confirmDialog( msg, {
			ok : _goOk,
			url : _prop.getRedirectMap( "lexicon.url" )
		 });

		return false;
	}

	/**
	 * 企業設定画面ボタン押下処理企業設定画面に遷移
	 *
	 * @param {Event} event イベント
	 */
	function goCompanySetting( event ) {

		var toView = "companySetting";
		var currentView = _util.currentModuleName();

		if ( toView == currentView ) return false;

		if ( $( event.currentTarget ).hasClass( "disabled" ) ) return false;

		var msg = _prop.getMessage( "layout.confirm.companySetting" );

		_view.confirmDialog( msg, {
			ok : _goOk,
			url : _prop.getRedirectMap( "companySetting.url" )
		 });

		return false;
	}


	/**
	 * 利用時間確認画面ボタン押下処理：利用時間確認画面に遷移
	 *
	 * @param {Event} event イベント
	 */
	function goUsetime( event ) {

		var toView = "usetime";
		var currentView = _util.currentModuleName();

		if ( toView == currentView ) return false;

		if ( $( event.currentTarget ).hasClass( "disabled" ) ) return false;

		var msg = _prop.getMessage( "layout.confirm.usetime" );

		_view.confirmDialog( msg, {
			ok : _goOk,
			url : _prop.getRedirectMap( "usetime.url" )
		 });

		return false;
	}

	/**
	 * ライセンス管理画面ボタン押下処理：ライセンス管理画面に遷移
	 *
	 * @param {Event} event イベント
	 */
	function goLicense( event ) {

		var toView = "license";
		var currentView = _util.currentModuleName();

		if ( toView == currentView ) return false;

		if ( $( event.currentTarget ).hasClass( "disabled" ) ) return false;

		var msg = _prop.getMessage( "layout.confirm.license" );

		_view.confirmDialog( msg, {
			ok : _goOk,
			url : _prop.getRedirectMap( "license.url" )
		 });

		return false;
	}


	/**
	 * 企業管理画面ボタン押下処理：企業管理画面に遷移
	 *
	 * @param {Event} event イベント
	 */
	function goCompanyManagement( event ) {

		var toView = "companyManagement";
		var currentView = _util.currentModuleName();

		if ( toView == currentView ) return false;

		if ( $( event.currentTarget ).hasClass( "disabled" ) ) return false;

		var msg = _prop.getMessage( "layout.confirm.companyManagement" );

		_view.confirmDialog( msg, {
			ok : _goOk,
			url : _prop.getRedirectMap( "companyManagement.url" )
		 });

		return false;
	}

	/**
	 * 代理店管理画面ボタン押下処理：代理店管理画面に遷移
	 *
	 * @param {Event} event イベント
	 */
	function goAgencyManagement( event ) {

		var toView = "agencyManagement";
		var currentView = _util.currentModuleName();

		if ( toView == currentView ) return false;

		if ( $( event.currentTarget ).hasClass( "disabled" ) ) return false;

		var msg = _prop.getMessage( "layout.confirm.agencyManagement" );

		_view.confirmDialog( msg, {
			ok : _goOk,
			url : _prop.getRedirectMap( "agencyManagement.url" )
		 });

		return false;
	}

	/**
	 * 通話履歴管理画面ボタン押下処理：通話履歴管理画面に遷移
	 *
	 * @param {Event} event イベント
	 */
	function goCallLogManagement( event ) {

		var toView = "callLogManagement";
		var currentView = _util.currentModuleName();

		if ( toView == currentView ) return false;

		if ( $( event.currentTarget ).hasClass( "disabled" ) ) return false;

		var msg = _prop.getMessage( "layout.confirm.callLogManagement" );

		_view.confirmDialog( msg, {
			ok : _goOk,
			url : _prop.getRedirectMap( "callLogManagement.url" )
		 });

		return false;
	}

	/**
	 * チェックワード管理画面ボタン押下処理：チェックワード管理画面に遷移
	 *
	 * @param {Event} event イベント
	 */
	function goCheckwordManagement( event ) {

		var toView = "checkwordManagement";
		var currentView = _util.currentModuleName();

		if ( toView == currentView ) return false;

		if ( $( event.currentTarget ).hasClass( "disabled" ) ) return false;

		var msg = _prop.getMessage( "layout.confirm.checkwordManagement" );

		_view.confirmDialog( msg, {
			ok : _goOk,
			url : _prop.getRedirectMap( "checkwordManagement.url" )
		 });

		return false;
	}

	/**
	 * パスワード管理ボタン押下処理：パスワード管理画面に遷移
	 *
	 * @param {Event} event イベント
	 */
	function goPassword(event) {

		var toView = "password";
		var currentView = _util.currentModuleName();

		if ( toView == currentView ) return false;

		if ( $( event.currentTarget ).hasClass( "disabled" ) ) return false;

		var msg = _prop.getMessage( "layout.confirm.password" );

		_view.confirmDialog( msg, {
			ok: _goOk,
			url: _prop.getRedirectMap( "password.url" )
		});

		return false;
	}

}); // end of ready-handler

