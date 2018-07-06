"use strict";

/**
 * ゲスト管理クラス（CCS.guest）
 */

( function( CCS ) {

	var _prop = CCS.prop;
	var _util = CCS.util;
	var _api  = CCS.api;

	var MAX_LEN_COMPANY_CODE = 100;

	// -------------------------------------------------------------------------
	// 初期処理
	// -------------------------------------------------------------------------

	var companyCode = _getUrlParam( "cc" );

	if ( !companyCode || companyCode.length == 0 || companyCode.length > MAX_LEN_COMPANY_CODE ) {

		_util.redirectError( "error", _prop.getMessage( "common.systemerror.urlError" ) );
	}

	var checkRet = _checkData( companyCode ) ;

	if ( checkRet ) {

		_util.redirectError( "error", _prop.getMessage( "common.systemerror.urlError" ) );

	}

	// -------------------------------------------------------------------------
	// 内部処理
	// -------------------------------------------------------------------------

	function _getUrlParam( paramName ) {

		var params, ret = "";

		var temp = location.search.substring(1).split( "&" );

		for ( var i = 0; i < temp.length; i++ ) {

			if ( !_util.isEmpty( temp[i] ) ) {

				params = temp[i].split( "=" );

				if ( !_util.isEmpty( params[0] ) && params[0] === paramName ) {

					if ( !_util.isEmpty( params[1] ) ){

						ret = decodeURIComponent( params[1] );

					}

					return ret;
				}

			}

		}

		return ret;
	}

	function _getCompanyCode() {

		return companyCode;

	}

	function _checkData( checkCompanyCode ) {

		var info = null;

		var msg = "";

		var form = {

				editForm : {

					hashedCompanyId : checkCompanyCode
  				}

			};

		var url = _prop.getApiMap( "inquiryform.contains" );

		var json = JSON.stringify( form );

		var option = {

			handleSuccess : function ( retData, status, xhr, option ) {

				if ( ! option.result.ok ) {

					msg = option.result.msgList[0].message;

					if ( msg ) {

						info = msg;
					}
				}

			},
			handleError : function ( xhr, status, errorThrown, option ) {

				msg = option.result.msgList[0].message;

				if ( msg ) {

					info = msg;
				}

			},
		};

		_api.postJSONSync( url, json, option );

		return info;
	}

	// -----------------------------------------------------------------------------
	// 公開情報
	// -----------------------------------------------------------------------------

	var ret = {

		name : "CCS.guest",

		getCompanyCode : _getCompanyCode,

	}; // end of ret

	CCS.guest = ret;

})( CCS );
