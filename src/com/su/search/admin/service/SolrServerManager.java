package com.su.search.admin.service;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.su.search.admin.core.Config;
import com.su.search.client.solrj.PaHttpSolrServer;
import com.su.search.utils.DESUtil;

/**
 * 
 * solr server加载到内存里 用的时候直接取
 * 
 */
public class SolrServerManager {

	private static final Logger logger = LoggerFactory
			.getLogger(SolrServerManager.class);

	private static PaHttpSolrServer server;
	private static PaHttpSolrServer wapServer;

	/**
	 * 默认的 pingan core
	 * 
	 * @return
	 */
	public static PaHttpSolrServer getServer() {
		if (null == server) {
			String password = Config.get().getProperty(Config.KEY_PA_AUTH);
			try {
				password = DESUtil.ecryptString(DESUtil.DEFAULT_SEED, password);
			} catch (Exception e) {
				logger.error(ExceptionUtils.getStackTrace(e));
			}
			server = new PaHttpSolrServer(Config.get().getProperty(
					Config.KEY_SEARCH_ADMIN_SERVER), password);
		}
		return server;
	}

	/**
	 * wap core
	 * 
	 * @return
	 */
	public static PaHttpSolrServer getWapServer() {
		if (null == wapServer) {
			String password = Config.get().getProperty(Config.KEY_PA_AUTH);
			try {
				password = DESUtil.ecryptString(DESUtil.DEFAULT_SEED, password);
			} catch (Exception e) {
				logger.error(ExceptionUtils.getStackTrace(e));
			}
			wapServer = new PaHttpSolrServer(Config.get().getProperty(
					Config.KEY_SEARCH_ADMIN_SERVER)
					+ "/" + Config.CORE_WAP, password);
		}
		return wapServer;
	}

}