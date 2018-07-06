/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：ExtractUtil.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.util;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cloud.optim.callcentersolution.core.Role;
import cloud.optim.callcentersolution.core.modules.loginutil.LoginUtility;

/**
 * 権限Util
 */
@Component
public class AuthUtil {

	/** LoginUtility */
	@Resource private LoginUtility loginUtility;

	/**
	 * ログインユーザが一般ユーザ権限を持つか判定する
	 * @return 一般ユーザ権限を持つ場合true、それ以外false
	 */
	public boolean isUser() {
		return loginUtility.getCustomUser().getAuthList().contains(Role.USER.getRole());
	}

	/**
	 * ログインユーザが管理者権を持つか判定する
	 * @return 管理者権を持つ場合true、それ以外false
	 */
	public boolean isAdmin() {
		return loginUtility.getCustomUser().getAuthList().contains(Role.ADMIN.getRole());
	}

	/**
	 * ログインユーザが代理店権限を持つか判定する
	 * @return 代理店権限を持つ場合true、それ以外false
	 */
	public boolean isAgency() {
		return loginUtility.getCustomUser().getAuthList().contains(Role.AGENCY.getRole());
	}

	/**
	 * ログインユーザがシステム管理者権限を持つか判定する
	 * @return システム管理者権限を持つ場合true、それ以外false
	 */
	public boolean isSysAdmin() {
		return loginUtility.getCustomUser().getAuthList().contains(Role.SYS_ADMIN.getRole());
	}


	/**
	 * ログインユーザがシステム管理者権限を持つか判定する
	 * @return システム管理者権限を持つ場合true、それ以外false
	 */
	public boolean isAnonymous() {
		return loginUtility.getCustomUser().getAuthList().contains(Role.ANONYMOUS.getRole());
	}
}
