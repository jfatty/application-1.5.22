package com.babeeta.butterfly.application.management.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/***
 * 自定义分页标签
 * @author xiazeyong
 * @date Apr 18, 2011
 * @comp eagleLink
 */
public class Page extends TagSupport {

	/**
	 * 唯一ID
	 */
	private static final long serialVersionUID = 1673491971683216709L;
	private String path = null;
	private String name = null;
	private String parameter = null;
	private String formName = null;

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @param path The path to set.
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @param parameter The parameter to set.
	 */
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	/**
	 * @param formName The formName to set.
	 */
	public void setFormName(String formName) {
		this.formName = formName;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		// TODO Auto-generated method stub
		PageModel PageModel = null;

		if(this.path == null){
			throw new NullPointerException("path属性不能为空");
		}

		if(this.name == null){
			this.name = "PageModel";
		}

		if(this.parameter == null){
			this.parameter = "page";
		}

		PageModel = (PageModel)pageContext.getRequest().getAttribute(name);

		if(PageModel == null) {
			PageModel =new PageModel();
			
			//throw new NullPointerException(name + "在request中为空!");
		}

		try{
			JspWriter out = pageContext.getOut();

			String contextPath = encodeURL(this.path, this.parameter);
			if(this.formName != null && this.formName.length() > 0){
				if(PageModel.isHasPreviousPage()){
					out.println("<a class=\"PageModel\" href=\"" + contextPath + "1\" onclick=\"PageModelSubmit('1');return false;\" title=\"第一页\">第一页</a>");
					out.println("<a class=\"PageModel\" href=\"" + contextPath + PageModel.getPreviousPage() + "\" onclick=\"PageModelSubmit('" + PageModel.getPreviousPage() + "');return false;\" title=\"上一页\">上一页</a>");
				}else{
					out.println("<span title=\"第一页\">第一页</span>");
					out.println("<span title=\"上一页\">上一页</span>");
				}

				if(PageModel.isHasNextPage()){
					out.println("<a class=\"PageModel\" href=\"" + contextPath + PageModel.getNextPage() + "\" onclick=\"PageModelSubmit('" + PageModel.getNextPage() + "');return false;\"  title=\"下一页\">下一页</a>");
					out.println("<a class=\"PageModel\" href=\"" + contextPath + PageModel.getTotalPage() + "\" onclick=\"PageModelSubmit('" + PageModel.getTotalPage() + "');return false;\" title=\"最后一页\">最后一页</a>");
				}else{
					out.println("<span title=\"下一页\">下一页</span>");
					out.println("<span title=\"最后一页\">最后一页</span>");
				}
			}else{
				if(PageModel.isHasPreviousPage()){
					out.println("<a class=\"PageModel\" href=\"" + contextPath + "1\" title=\"第一页\">第一页</a>");
					out.println("<a class=\"PageModel\" href=\"" + contextPath + PageModel.getPreviousPage() + "\" title=\"上一页\">上一页</a>");
				}else{
					out.println("<span title=\"第一页\">第一页</span>");
					out.println("<span title=\"上一页\">上一页</span>");
				}

				if(PageModel.isHasNextPage()){
					out.println("<a class=\"PageModel\" href=\"" + contextPath + PageModel.getNextPage() + "\" title=\"下一页\">下一页</a>");
					out.println("<a class=\"PageModel\" href=\"" + contextPath + PageModel.getTotalPage() + "\" title=\"最后一页\">最后一页</a>");
				}else{
					out.println("<span title=\"下一页\">下一页</span>");
					out.println("<span title=\"最后一页\">最后一页</span>");
				}
			}

			out.println("&nbsp;");
			out.println("共有" + PageModel.getTotalCount() + "条");
			out.print("&nbsp;");
			out.println("每页" + PageModel.getCount() + "条");
			out.print("&nbsp;");
			out.println("共" + PageModel.getTotalPage() + "页");
			out.print("&nbsp;");
			out.println("本页是第" + PageModel.getCurrPage() + "页");
//			out.println("&nbsp;");
//			out.println("跳转到第<input type=\"text\" id=\"" + this.parameter + "\" class=\"textF\" size=\"3\" maxlength=\"5\">页");
//			out.println("&nbsp;");
//			out.println("<button onclick=\"PageModelGoto();\" style=\"border:1px ridge #d6edef;background-color:#ffffff\">跳转</button>");
			out.println("<br />");
			/*创建CSS内容*/
			createCSS(out);
			if(this.formName != null && this.formName.length() > 0) {
				/*创建js内容*/
				createJS(out, contextPath, PageModel.getTotalPage());
			}else{
				createNoFormJS(out, this.path, PageModel.getTotalPage());
			}
			out.println();
		}catch(Exception e){
			throw new JspException(e);
		}

		return SKIP_BODY;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException {
		// TODO Auto-generated method stub
		return EVAL_PAGE;
	}

	/**
	 * 改变URL使它能访问到action或servlet中去
	 * @param href 从页面传来的href
	 * @param param
	 * @param queryStr 查询内容
	 * @return
	 * @author 夏泽勇
	 */
	private final String encodeURL(String href, String param){
		StringBuffer buffer = new StringBuffer(100);

		HttpServletRequest request =
			(HttpServletRequest)this.pageContext.getRequest();
		buffer.append(request.getContextPath() + "/" + href);

		int question = href.indexOf("?");
		if(question > 0 ){
			buffer.append("&" + param + "=");
		}else{
			buffer.append("?" + param + "=");
		}

		return buffer.toString();
	}

	/**
	 * 创建js内容如果提交form就用js提交
	 * @param out
	 * @param contextPath
	 * @throws Exception
	 * @author 夏泽勇
	 */
	private final void createJS(JspWriter out, String contextPath, int totalPage) throws Exception {
		out.println("<script language=\"javascript\">");
		out.println("function PageModelSubmit(pageNum_) { ");
		out.println("  document." + formName + ".action=\'" + contextPath + "\' + pageNum_ + \'\';");
		out.println("  document." + formName + ".submit();");
		out.println("}");
		/*跳转内容*/
		HttpServletRequest request =
			(HttpServletRequest)this.pageContext.getRequest();
		String contPath = request.getContextPath() + "/" + this.path;
		out.println("function PageModelGoto() { ");
//        out.println("  if(!controlNotNull(document.all." + this.parameter + ", '跳转页面')) {");
//		out.println("    return;");
//		out.println("  }");
//		out.println("  ");
//		out.println("  if(!controlIsInt(document.all." + this.parameter + ", '跳转页面')) {");
//		out.println("    return;");
//		out.println("  }");
		out.println("  ");
        out.println("  pageNum_ = document.all." + this.parameter + ".value");
		out.println("  if(pageNum_ > 0 && pageNum_ <= " + totalPage + ") {");
		out.println("    document." + formName + ".action=\'" + contPath + "&" + this.parameter + "=\' + pageNum_ + \'\';");
		out.println("    document." + formName + ".submit();");
		out.println("  } else { ");
		out.println("    alert(\"您输入的页数超出范围\");");
		out.println("  }");
		out.println("}");
		out.println("</script>");
		out.println();
	}

	/**
	 * 创建没有form时的js
	 * @param out
	 * @param contextPath
	 * @throws Exception
	 * @author 夏泽勇
	 */
	private final void createNoFormJS(JspWriter out, String href, int totalPage) throws Exception {
		HttpServletRequest request =
			(HttpServletRequest)this.pageContext.getRequest();
		String contPath = request.getContextPath() + "/" + href;

		out.println("<script language=\"javascript\">");
		out.println("function PageModelGoto() { ");
//		out.println("  if(!controlNotNull(document.all." + this.parameter + ", '跳转页面')) {");
//		out.println("    return;");
//		out.println("  }");
//		out.println("  ");
//		out.println("  if(!controlIsInt(document.all." + this.parameter + ", '跳转页面')) {");
//		out.println("    return;");
//		out.println("  }");
		out.println("  ");
		out.println("  pageNum_ = document.all." + this.parameter + ".value");
		out.println("  if(pageNum_ > 0 && pageNum_ <= " + totalPage + ") {");
		out.println("    document.location.href=\'" + contPath + "&" + this.parameter + "=\' + pageNum_ + \'\';");
		out.println("  } else { ");
		out.println("    alert(\"您输入的页数超出范围\");");
		out.println("  }");
		out.println("}");
		/****************************************************/
		//out.println("function controlNotNull(id,info) { ");
		/****************************************************/
		out.println("</script>");
		out.println();
	}

	/**
	 * 创建css内容
	 * @param out
	 * @throws Exception
	 * @author 夏泽勇
	 */
	private final void createCSS(JspWriter out)throws Exception {
		out.println("<style type=\"text/css\">");
		out.println("a.PageModel:visited { text-decoration: underline; color: #808080; } ");//#CCCCFF #c0c0c0
		out.println("a.PageModel:link { text-decoration: underline; color: blue; } ");
		out.println("a.PageModel:hover { text-decoration: none; color: #009933; } ");
		out.println("a.PageModel:active { text-decoration: none; color: orangered; } ");
		out.println("</style>");
	}
}
