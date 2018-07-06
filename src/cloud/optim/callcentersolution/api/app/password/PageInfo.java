package cloud.optim.callcentersolution.api.app.password;

public class PageInfo {

	/**  ページNo */
	private String pageNo;
	/**  ページ表示件数 */
	private String pageSize;
	/**  総件数 */
	private String totalNumber;
	/**  総ページ数 */
	private String totalPage;
	/**  取得開始件数 */
	private String offset;

	/**
	 * @return pageNo
	 */
	public String getPageNo() {
		return pageNo;
	}
	/**
	 * @param pageNo セットする pageNo
	 */
	public void setPageNo(String pageNo) {
		this.pageSize = pageNo;
	}
	/**
	 * @return pageSize
	 */
	public String getPageSize() {
		return pageSize;
	}
	/**
	 * @param pageSize セットする pageSize
	 */
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
	/**
	 * @return totalNumber
	 */
	public String getTotalNumber() {
		return totalNumber;
	}
	/**
	 * @param totalNumber セットする totalNumber
	 */
	public void setTotalNumber(String totalNumber) {
		this.totalNumber = totalNumber;
	}
	/**
	 * @return totalPage
	 */
	public String getTotalPage() {
		return totalPage;
	}
	/**
	 * @param totalPage セットする totalPage
	 */
	public void setTotalPage(String totalPage) {
		this.totalPage = totalPage;
	}
	/**
	 * @return offset
	 */
	public String getOffset() {
		return offset;
	}
	/**
	 * @param offset セットする offset
	 */
	public void setOffset(String offset) {
		this.offset = offset;
	}

}
