/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：KnowledgeRequest.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.knowledge;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import cloud.optim.callcentersolution.api.entity.Knowledge;
import cloud.optim.callcentersolution.api.entity.Manual;
import cloud.optim.callcentersolution.core.common.utility.ToStringHelper;

/**
 * Knowledge API リクエストクラス.<br/>
 */
@XmlRootElement( name="restRequest" )
public class KnowledgeRequest implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	/** 検索条件 */
	private SearchForm searchForm;

	// -------------------------------------------------------------------------

	/** 1 エンティティ情報 */
	private EditForm editForm;

	// -------------------------------------------------------------------------

	/** 一括処理情報 */
	private List<SearchResult> bulkFormList;

	// -------------------------------------------------------------------------


	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString()
	{
		return ToStringHelper.toString( this );
	}

	// -------------------------------------------------------------------------
	// 内部クラス
	// -------------------------------------------------------------------------

	/** 編集用 */
	public static final class EditForm implements java.io.Serializable
	{
		/** serialVersionUID  */
		private static final long serialVersionUID = 1L;

		/** 1 エンティティ情報 */
		private Knowledge knowledge;

		/** タグ名称配列 */
		private List<String> tag;

		/** 参照先配列 */
		private List<String> reference;

		/** マニュアル配列 */
		private List<Manual> manual;

		/**
		 * 文字列表現への変換
		 *
		 * @return 文字列表現
		 */
		@Override
		public String toString() { return ToStringHelper.toString( this ); }

		/**
		 * knowledge 取得.
		 *
		 * @return knowledge
		 */
		public Knowledge getKnowledge() { return knowledge; }

		/**
		 * knowledge 設定.
		 *
		 * @param knowledge knowledge に設定する値.
		 */
		public void setKnowledge( Knowledge knowledge ) { this.knowledge = knowledge; }

		/**
		 * tag 取得.
		 *
		 * @return tag
		 */
		public List<String> getTag() { return tag; }

		/**
		 * tag 設定.
		 *
		 * @param tag tag に設定する値.
		 */
		public void setTag( List<String> tag ) { this.tag = tag; }

		/**
		 * @return reference
		 */
		public List<String> getReference() {
			return reference;
		}

		/**
		 * @param reference セットする reference
		 */
		public void setReference(List<String> reference) { this.reference = reference; }

		/**
		 * @return manual
		 */
		public List<Manual> getManual() { return manual; }

		/**
		 * @param manual セットする manual
		 */
		public void setManual(List<Manual> manual) {
			this.manual = manual;
		}
	}

	// -------------------------------------------------------------------------
	// アクセサメソッド
	// -------------------------------------------------------------------------

	/**
	 * searchForm 取得.
	 *
	 * @return searchForm
	 */
	public SearchForm getSearchForm() {

		return searchForm;
	}

	/**
	 * searchForm 設定.
	 *
	 * @param searchForm searchForm に設定する値.
	 */
	public void setSearchForm( SearchForm searchForm ) {

		this.searchForm = searchForm;
	}

	/**
	 * editForm 取得.
	 *
	 * @return editForm
	 */
	public EditForm getEditForm() {

		return editForm;
	}

	/**
	 * editForm 設定.
	 *
	 * @param entity editForm に設定する値.
	 */
	public void setEditForm( EditForm entity ) {

		this.editForm = entity;
	}

	/**
	 * bulkFormList 取得.
	 *
	 * @return bulkFormList
	 */
	public List<SearchResult> getBulkFormList() {

		return bulkFormList;
	}

	/**
	 * bulkFormList 設定.
	 *
	 * @param bulkFormList bulkFormList に設定する値.
	 */
	public void setBulkFormList( List<SearchResult> bulkFormList ) {

		this.bulkFormList = bulkFormList;
	}
}
