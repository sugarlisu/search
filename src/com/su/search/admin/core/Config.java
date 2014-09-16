package com.su.search.admin.core;

import java.io.InputStream;
import java.util.Properties;


import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * search配置文件
 * 
 * @author WANGQIANG406
 * 
 */
public class Config extends Properties {

	private static final long serialVersionUID = 1;
	private static final Logger logger = LoggerFactory.getLogger(Config.class);
	/**
	 * search配置文件名
	 */
	private static final String CONFIG_FILE_NAME = "search.properties";
	/**
	 * opencms环境配置文件名
	 */
	public static final String ENV_FILE_NAME = "env.properties";
	/**
	 * 默认的主站索引库
	 */
	public static final String CORE_DEFAULT = "pingan";
	/**
	 * WAP索引库名称
	 */
	public static final String CORE_WAP = "wap";
	/**
	 * 搜索后台url
	 */
	public static final String KEY_SEARCH_ADMIN_SERVER = "search.admin.server";
	/**
	 * 搜索前台url
	 */
	public static final String KEY_SEARCH_SERVER = "search.server";
	/**
	 * 词典路径
	 */
	public static final String KEY_DIC_PATH = "dic.path";
	/**
	 * index-home路径
	 */
	public static final String KEY_INDEX_HOME = "index.home";
	/**
	 * 备份文件夹路径
	 */
	public static final String KEY_BACKUP_DEST = "backup.dest";
	/**
	 * 备份定时间隔
	 */
	public static final String KEY_CRON_BACKUP = "cron.backup";
	/**
	 * 词典同步定时间隔
	 */
	public static final String KEY_CRON_DIC = "cron.dic";
	/**
	 * 更新索引验证的密码
	 */
	public static final String KEY_PA_AUTH = "pa.auth";
	/**
	 * 人工干预配置文件路径后缀
	 */
	public static final String KEY_ELEVATE_CONFIG_SUFFIX = "elevate.config.suffix";
	/**
	 * 人工干预影响的条数限制
	 */
	public static final String KEY_ELEVATE_LIMIT = "elevate.limit";
	/**
	 * 网站人工干预的所有站点
	 */
	public static final String KEY_ELEVATE_SITE = "elevate.site";
	/**
	 * 网站人工干预的所有类型
	 */
	public static final String KEY_ELEVATE_TYPE = "elevate.type";
	/**
	 * WAP人工干预的所有类型
	 */
	public static final String KEY_ELEVATE_WAP_TYPE = "elevate.wap.type";
	/**
	 * 记录客户搜索结果的链接后缀
	 */
	public static final String KEY_RECORD_URL_SUFFIX = "record.url.suffix";

	private static String adminPassword = null;

	private static Config instance = new Config();

	private Config() {

		try {
			InputStream stream = ClassLoader
					.getSystemResourceAsStream(CONFIG_FILE_NAME);
			this.load(stream);
		} catch (Exception e) {
			logger.error("加载配置文件 [" + CONFIG_FILE_NAME + "] 异常!");
			logger.error(ExceptionUtils.getStackTrace(e));
		}

		logger.info("加载配置文件 [" + CONFIG_FILE_NAME + "] OK...");
	}

	public static synchronized Config get() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}

	/**
	 * 取cyberark密码
	 * 
	 * @return
	 */
	public String getAdminPassword() {
		if (null != adminPassword) {
			return adminPassword;
		}
		String credFilePath = getProperty("cyberark.credFilePath");
		String safe = getProperty("cyberark.safe");
		String folder = getProperty("cyberark.folder");
		String object = getProperty("cyberark.object");
		try {
			adminPassword = CyberArkUtils.getPassword(credFilePath, safe,
					folder, object);
		} catch (PSDKException e) {
			adminPassword = "";
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		return adminPassword;
	}

	/**
	 * index-home: /nfsc/data/pa18-wcm/search/index-home
	 * 
	 */
	public String getIndexHome() {
		return getProperty(KEY_INDEX_HOME).trim();
	}

	/**
	 * 人工干预配置文件路径后缀: conf/elevate.xml
	 */
	private String getElevateConfigSuffix() {
		return getProperty(KEY_ELEVATE_CONFIG_SUFFIX);
	}

	/**
	 * 拼接人工干预配置文件路径:
	 * /nfsc/data/pa18-wcm/search/index-home/pingan/conf/elevate.xml
	 * 
	 * @return
	 */
	public String getElevateConfigPath() {
		String filePath = getIndexHome().concat("/").concat(CORE_DEFAULT)
				.concat("/").concat(getElevateConfigSuffix());
		return FilenameUtils.normalize(filePath);
	}

	/**
	 * 拼接WAP人工干预配置文件路径:
	 * /nfsc/data/pa18-wcm/search/index-home/wap/conf/elevate.xml
	 * 
	 * @return
	 */
	public String getWapElevateConfigPath() {
		String filePath = getIndexHome().concat("/").concat(CORE_WAP)
				.concat("/").concat(getElevateConfigSuffix());
		return FilenameUtils.normalize(filePath);
	}

	/**
	 * 人工干预影响的条数限制
	 * 
	 * @return
	 */
	public int getElevateLimit() {
		return Integer.valueOf(getProperty(KEY_ELEVATE_LIMIT).trim());
	}

	/**
	 * 搜索后台 url
	 * 
	 * @return
	 */
	public String getAdminServerUrl() {
		return getProperty(KEY_SEARCH_ADMIN_SERVER).trim();
	}

	/**
	 * 搜索前台url http://search.pingan.com
	 * 
	 * @return
	 */
	public String getSearchServerUrl() {
		return getProperty(KEY_SEARCH_SERVER).trim();
	}

	/**
	 * 记录客户搜索结果的URL http://pa18-wcm.paic.com.cn/search/search-report-add.do
	 * 
	 * @return
	 */
	public String getRecourUrl() {
		return getAdminServerUrl().concat("/").concat(
				getProperty(KEY_RECORD_URL_SUFFIX));
	}
}