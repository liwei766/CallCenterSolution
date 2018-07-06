/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：KnowledgeExportBean.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.knowledge;

import java.util.ArrayList;
import java.util.List;

import cloud.optim.callcentersolution.api.entity.Knowledge;
import cloud.optim.callcentersolution.api.entity.Manual;
import cloud.optim.callcentersolution.api.entity.Reference;
import cloud.optim.callcentersolution.api.entity.Tag;

/**
 * CSV出力用のナレッジ情報を保持する
 */
public class KnowledgeExportBean {

	/**
	 * ナレッジ情報
	 */
	private Knowledge knowledge;

	/**
	 * タグリスト
	 */
	private List<Tag> tags;

	/**
	 * 参照URL
	 */
	private List<Reference> references;

	/**
	 * マニュアル
	 */
	private List<Manual> manuals;

	/**
	 * @return knowledge
	 */
	public Knowledge getKnowledge() {
		return knowledge;
	}

	/**
	 * @param knowledge セットする knowledge
	 */
	public void setKnowledge(Knowledge knowledge) {
		this.knowledge = knowledge;
	}

	/**
	 * @return tags
	 */
	public List<Tag> getTags() {
		return tags;
	}

	/**
	 * @param tags セットする tags
	 */
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	/**
	 * @return references
	 */
	public List<Reference> getReferences() {
		return references;
	}

	/**
	 * @param references セットする references
	 */
	public void setReferences(List<Reference> references) {
		this.references = references;
	}

	/**
	 * @return manuals
	 */
	public List<Manual> getManuals() {
		return manuals;
	}

	/**
	 * @param manuals セットする manuals
	 */
	public void setManuals(List<Manual> manuals) {
		this.manuals = manuals;
	}

}
