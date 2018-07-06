/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：TagMapper.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.tag;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import cloud.optim.callcentersolution.api.entity.Tag;

/**
 * MyBatis TagMapper I/F.<br/>
 */
@Component
public interface TagMapper {

	/**
	 * 取得
	 *
	 * @param knowledgeId ナレッジID
	 * @param companyId 企業ID
	 * @return タグ
	 */
	List<Tag> findByKnowledgeId( @Param("knowledgeId") Long knowledgeId , @Param("companyId") String companyId);


	/**
	 * ナレッジに紐付かないタグを物理削除する
	 * @param companyId 企業ID
	 */
	void deleteUnlinkedTags(@Param("companyId") String companyId);

	/**
	 * 企業IDに紐付くタグを物理削除する
	 * @param companyId 企業ID
	 */
	void deleteAll(@Param("companyId") String companyId);
}
