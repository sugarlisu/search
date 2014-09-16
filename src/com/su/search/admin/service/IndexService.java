package com.su.search.admin.service;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import com.su.search.admin.core.Config;
import com.su.search.client.solrj.PaHttpSolrServer;
import com.su.search.utils.DESUtil;



/**
 * 索引维护
 * 
 */
public class IndexService {

	private static final Logger logger = LoggerFactory
			.getLogger(IndexService.class);

	protected static PaHttpSolrServer server;

	// 正则表达式
	public static final String REGEX = "/c[123]/";

	public IndexService() {
		String password = Config.get().getProperty(Config.KEY_PA_AUTH);
		try {
			password = DESUtil.ecryptString(DESUtil.DEFAULT_SEED, password);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		server = new PaHttpSolrServer(Config.get().getProperty(
				Config.KEY_SEARCH_ADMIN_SERVER), password);
	}

	/**
	 * 新增、更新索引
	 * 
	 * @param dto
	 * @return
	 */
	public boolean indexDoc(SolrInputDocument doc, String name) {

		try {
			if (name != null && "site".equals(name)) {
				SolrServerManager.getServer().add(doc);
				SolrServerManager.getServer().commit();
			}
			if (name != null && "wap".equals(name)) {
				SolrServerManager.getWapServer().add(doc);
				SolrServerManager.getWapServer().commit();
			}
		} catch (SolrServerException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			return false;
		} catch (IOException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			return false;
		}
		return true;

	}

	/**
	 * 返回查询结果集
	 * 
	 * @param link
	 * @param start
	 * @return
	 */
	public SolrDocumentList queryDocumentList(String link, int start, int rows,
			String name) {
		if (link.contains("http:"))
			link = link.replaceAll("http:", "");
		if (link.contains("https:"))
			link = link.replaceAll("https:", "");
		SolrQuery params = null;
		if (name != null && "wap".equals(name)) {
			params = new SolrQuery("link:*" + link + "*");
		}
		if (name != null && "site".equals(name)) {
			params = new SolrQuery("link:*" + link + "*");
		}
		// SolrQuery params = new SolrQuery("link:*" + link + "*" );
		params.setStart(start);
		params.setRows(rows);
		QueryResponse response = null;
		try {
			if (name != null && "wap".equals(name)) {
				response = SolrServerManager.getWapServer().query(params);
			}
			if (name != null && "site".equals(name)) {
				response = SolrServerManager.getServer().query(params);
			}
			return response.getResults();
		} catch (SolrServerException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			return null;
		}
	}

	/**
	 * 按链接查询单个结果
	 * 
	 * @param link
	 * @param start
	 * @return
	 */
	public SolrDocument queryDocumentByLink(String link, String name) {
		SolrQuery params = new SolrQuery("link:" + link);
		QueryResponse response = null;
		try {
			if (name != null && "wap".equals(name)) {
				response = SolrServerManager.getWapServer().query(params);
			}
			if (name != null && "site".equals(name)) {
				response = SolrServerManager.getServer().query(params);
			}
			return response.getResults().get(0);
		} catch (SolrServerException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			return null;
		}
	}

	/**
	 * 按链接查询记录集
	 * 
	 * @param link
	 * @param start
	 * @return
	 */
	public long getCountByLink(String link, String name) {
		SolrQuery params = new SolrQuery("link:*" + link + "*");
		QueryResponse response = null;
		try {
			if (name != null && "wap".equals(name)) {
				response = SolrServerManager.getWapServer().query(params);
			}
			if (name != null && "site".equals(name)) {
				response = SolrServerManager.getServer().query(params);
			}
			return (Long) response.getResults().getNumFound();
		} catch (SolrServerException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		return 0;
	}

	/**
	 * 删除索引
	 * 
	 * @param link
	 * @return
	 */
	public boolean deleteIndexByLink(String link, String name) {
		try {
			if (name != null && "wap".equals(name)) {
				SolrServerManager.getWapServer().deleteById(link);
				SolrServerManager.getWapServer().commit();
			}
			if (name != null && "site".equals(name)) {
				SolrServerManager.getServer().deleteById(link);
				SolrServerManager.getServer().commit();
			}
		} catch (SolrServerException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			return false;
		} catch (IOException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			return false;
		}
		return true;
	}

	/**
	 * 批量删除索引
	 * 
	 * @param link
	 * @return
	 */
	public boolean batchDeleteByLink(String link, String name) {
		try {
			if (name != null && "wap".equals(name)) {
				SolrServerManager.getWapServer().deleteById(link);
				SolrServerManager.getWapServer().deleteByQuery(
						"link:*" + link + "*");
				SolrServerManager.getWapServer().commit();
			}
			if (name != null && "site".equals(name)) {
				SolrServerManager.getServer().deleteById(link);
				SolrServerManager.getServer().deleteByQuery(
						"link:*" + link + "*");
				SolrServerManager.getServer().commit();
			}
		} catch (SolrServerException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			return false;
		} catch (IOException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			return false;
		}
		return true;
	}

}