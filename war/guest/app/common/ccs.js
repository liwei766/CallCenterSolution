"use strict";

/*
 * CallCenterSolution（CCS／CCS.data）
 */

// 空の CallCenterSolution コンテキスト

window.CCS = {

	name : "CCS",

	data : null,
	util : null,
	prop : null,
	view : null,
	api  : null,
	user : null,

	// リリース時に、以下のフラグを全て false にすること

	debug : {
		log : false,		// true のとき、$util.log() でコンソールに出力
		dialog : false,	// true のとき、リダイレクト／ログアウト時にダイアログ表示
	}
};

/*
 * 共通データ設定（CCS.data）
 */
(function( CCS ) {

	// ----------------------------------------------------------------------------
	// ルート URL 取得
	// ----------------------------------------------------------------------------

	var PATH = "/guest/app/common/ccs.js"; // このファイルのパス（ルートは /guest の親ディレクトリ）

	var root = _rootPath();
	var apiroot = root;


	/**
	 * ルートコンテキストパスを取得（http://～/[contextName] 形式）.
	 **/
	function _rootPath() {

		var path;
		var scripts = document.getElementsByTagName( "script" );

		for ( var i = 0 ; i < scripts.length ; i++ ) {

			var src = _absolutePath( scripts[i].src );
			var sub = src.length - PATH.length;

			if ( ( sub >= 0 ) && ( src.lastIndexOf( PATH ) === sub ) ) {
				path = src.substring( 0, sub );
				break;
			}
		}

		return path;
	}

	/**
	 * URL（相対含む）から URL パス絶対表記を取得
	 *
	 * @param {String} path ファイルパス（URL）
	 * @return {String} URL パス絶対表記
	 **/
	function _absolutePath( path ) {

		var elem = document.createElement( "span" );
		elem.innerHTML = "<a href='" + path + "' />";
		return elem.firstChild.href;
	}

	// ----------------------------------------------------------------------------
	// 共通データ定義
	// ----------------------------------------------------------------------------

var ret =
{
	property : {
		common : {
			root : root,
			apiroot : apiroot,

			sessionKey : { // セッションストレージキー名
				errorMessage : "errorMessage",
			},

			apiResponse : {
				success  : "00_00_000",
				partial  : "00_00_002",
				overflow : "00_00_003",
				sysErrorPrefix : "90",
				detailColumnNamePrefix : "#"
			},

			dateFormat : {
				"default" : "yyyy/mm/dd",
				date : "yyyy/mm/dd",
				time : "HH:MM",
				dateTime : "yyyy/mm/dd HH:MM:ss"
			},
		}, // end of common

	}, // end of property


	// ----- リダイレクトによる遷移の一覧

	redirectMap : {

		error : {

			error : { url : root + "/guest/error/error.html" },
		},

		"*" : {} // どれにも該当しない場合に適用する

	}, // end of redirectMap

	// ----- 共通の API 一覧

	apiMap : {

		common : {

		}, // end of common

		inquiryform : {

			search : apiroot + "/api/inquiryform/search",
			get : apiroot + "/api/inquiryform/get",
			increment : apiroot + "/api/inquiryform/increment",
			knowledgeKeyword : apiroot + "/api/inquiryform/knowledgeKeyword",

			contains : apiroot + "/api/inquiryform/contains",

		},

	}, // end of apiMap

	// ----------------------------------------------------------------------------

	label : {

	}, // end of label

	// ----------------------------------------------------------------------------

	message : {

		common : {
			complete : {
				regist : "登録しました。",
				update : "更新しました。",
				del : "削除しました。"
			},

			confirm : {
				logout : "ログアウトします。よろしいですか？",
				regist : "登録します。よろしいですか？",
				update : "更新します。よろしいですか？",
				del : "削除します。よろしいですか？"
			},

			error : {
				regist : "登録できませんでした。",
				update : "更新できませんでした。",
				del : "削除できませんでした。"
			},

			systemerror : {
				parseError : "レスポンス情報の解析に失敗しました。",
				httpStatusError : "HTTPステータスエラー({0})",
				unauthError : "セッションがタイムアウトしました。",
				urlError : "アクセスURLが不正です。"
			},

		}, // end of common

		layout : {

		}, // end of layout

		inquiry : {

			default : "ご利用ありがとうございます。<br />お困りの内容を入力し、「質問する」をクリックしてください。",
			retry : "他の言葉を入力し、「質問する」をクリックしてください。",
			tag : "{0}に関するお問い合わせですね。",
			yes : "はい",
			no : "いいえ",
			reset : "最初に戻る",
			knowledge : "次から選択してください。",
			next : "次の質問を入力し、「質問する」をクリックしてください。",

			error : {

				searchKnowledgeError  : "検索処理に失敗しました。",
				getKnowledgeDetailError  : "回答情報が取得できませんでした。",

			}
		},

	}, // end of message

	// ----------------------------------------------------------------------------

	validatorMessage : {
		common : {
			required : "必須項目です",

			fixlength : "{0} 文字で入力してください",
			rangelength : "{0} 文字以上{1}文字以下で入力してください",
			minlength : "{0} 文字以上を入力してください",
			maxlength : "{0} 文字以下で入力してください",

			range : "{0} から {1} までの値を入力してください",
			min : "{0} 以上の値を入力してください",
			max : "{0} 以下の値を入力してください",

			rangeTime : "{0} から {1} までの日付（時刻）を入力してください",
			minTime : "{0} 以降の日付（時刻）を入力してください",
			maxTime : "{0} 以前の日付（時刻）を入力してください",

			digits : "半角数字を入力してください",
			numeric : "半角数字を入力してください",
			numberInt : "半角で整数を入力してください",
			number : "半角で数値を入力してください",

			rangeInt : "-999999999～999999999までの値を入力してください",
			rangeLong : "-999999999999999～999999999999999までの値を入力してください",

			alpha : "半角英字を入力してください",
			alphaNumeric : "半角英数字を入力してください",
			hankakuAscii : "半角英数記号を入力してください",
			hankakuKana : "半角カタカナを入力してください",
			hankakuAll : "半角文字を入力してください",

			katakana : "全角カタカナを入力してください",
			hirakana : "全角ひらがなを入力してください",
			kana : "全角ひらがな･カタカナを入力してください",
			zenkakuAll : "全角文字を入力してください",
			zenkakuAllAndNewLine : "全角文字または改行を入力してください",

			regexp : "書式が正しくありません",
			ngRegexp : "不正な書式です",

			date : "{0} の形式で入力してください",
			dateTime : "{0} の形式で入力してください",
			dateTimeSec : "{0} の形式で入力してください",
			time : "HH:MM の形式で入力してください",
			timeSec : "HH:MM:SS の形式で入力してください",
			timeFormat : "HH:MM:SS の形式で入力してください",

			isPast : "{1} 以前の日付（時刻）を指定してください",
			isFuture : "{1} 以降の日付（時刻）を指定してください",

			equalTo : "同じ値を入力してください",
			notEqualTo : "異なる値を入力してください",

			url : "URL を入力してください"
		} // end of common
	} // end of validatorMessage

};  // end of ret

	CCS.data = ret;

})( CCS );
