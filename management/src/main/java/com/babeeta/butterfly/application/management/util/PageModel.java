package com.babeeta.butterfly.application.management.util;

/***
 * 分页类
 * @author xiazeyong
 * @date Apr 18, 2011
 * @comp eagleLink
 */
public class PageModel {
	/**
	 * 每页显示行数
	 */
	private int count = 10; //每页显示行数
	
	/**
	 * 总行数
	 */
	private int totalCount = 0; //总行数
	
	/**
	 * 总页数
	 */
	private int totalPage = 0; //总页数
	
	/**
	 * 从页面传过来的页数
	 */
	private int page = 0; //从页面传过来的页数
	
	/**
	 * 是否有下一页
	 */
	private boolean hasNextPage=false;//是否有下一页

	/**
	 * 是否有前一页
	 */
	private boolean hasPreviousPage=false;//是否有前一页
	
	

	/**
	 * @return Returns the count.
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count The count to set.
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return Returns the currPage.
	 */
	public int getCurrPage() {
		return page;
	}

	/**
	 * @return Returns the totalPage.
	 */
	public int getTotalPage() {
		totalPage = totalCount/count;
		if(totalCount%count > 0 || totalPage == 0){
			totalPage += 1;
		}
		return totalPage;
	}

	/**
	 * @return Returns the page.
	 */
	public int getPage() {
		return this.page;
	}

	/**
	 * @param page The page to set.
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * @return Returns the totalCount.
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * @param totalCount The totalCount to set.
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * @return Returns the hasNextPage.
	 */
	public boolean isHasNextPage() {
		if(getCurrPage() >= getTotalPage()){
			hasNextPage = false;
		}else{
			hasNextPage = true;
		}
		return hasNextPage;
	}

	/**
	 * @return Returns the hasPreviousPage.
	 */
	public boolean isHasPreviousPage() {
		if((getCurrPage() -1)>0) {
            hasPreviousPage=true;
        }else{
            hasPreviousPage=false;
        }
		return hasPreviousPage;
	}
	
	/**
	 * 获得下一页的页数
	 * @return
	 */
	public int getNextPage(){
		return page + 1;
	}
	
	/**
	 * 获得上一页的页数
	 * @return
	 */
	public int getPreviousPage(){
		return page - 1;
	}
}
