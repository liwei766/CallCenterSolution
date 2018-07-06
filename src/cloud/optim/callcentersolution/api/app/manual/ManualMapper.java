/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：ManualMapper.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.manual;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import cloud.optim.callcentersolution.api.entity.Manual;

/**
 * MyBatis ManualMapper I/F.<br/>
 */
@Component
public interface ManualMapper {

	/**
	 * 取得
	 *
	 * @param knowledgeId ナレッジID
	 * @param companyId 企業ID
	 * @return マニュアル
	 */
	List<Manual> findByKnowledgeId( @Param("knowledgeId") Long knowledgeId , @Param("companyId") String companyId );

	/**
	 * マニュアルを物理削除する
	 * @param knowledgeId ナレッジID
	 * @param companyId 企業ID
	 */
	void deleteManuals( @Param("knowledgeId") Long knowledgeId , @Param("companyId") String companyId );

	/**
	 * 企業IDに紐付くマニュアルを物理削除する
	 * @param companyId 企業ID
	 */
	void deleteAll(@Param("companyId") String companyId);
}
