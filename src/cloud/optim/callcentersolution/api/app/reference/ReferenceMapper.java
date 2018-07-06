/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：ReferenceMapper.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.reference;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import cloud.optim.callcentersolution.api.entity.Reference;

/**
 * MyBatis ReferenceMapper I/F.<br/>
 */
@Component
public interface ReferenceMapper {

	/**
	 * 取得
	 *
	 * @param knowledgeId ナレッジID
	 * @param companyId 企業ID
	 * @return 参照先
	 */
	List<Reference> findByKnowledgeId( @Param("knowledgeId") Long knowledgeId , @Param("companyId") String companyId );

	/**
	 * 参照先を物理削除する
	 * @param knowledgeId ナレッジID
	 * @param companyId 企業ID
	 */
	void deleteReferences( @Param("knowledgeId") Long knowledgeId , @Param("companyId") String companyId );

	/**
	 * 企業IDに紐付く参照先を物理削除する
	 * @param companyId 企業ID
	 */
	void deleteAll(@Param("companyId") String companyId);
}
