/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：UseTimeMapper.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.api.app.usetime;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * MyBatis UseTimeMapper I/F.<br/>
 */
@Component
public interface UseTimeMapper {


//	List<SearchResult> getUsetimeGroupByCompanyID(@Param("startDate") String startDate,@Param("endDate") String endDate);

	List<SearchResult> searchByMonth(SearchForm form);

	List<SearchResult> searchByCompanyId(SearchForm form);


}
