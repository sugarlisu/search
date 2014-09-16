package com.su.search.admin.service;


import java.util.Date;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class SitesIndexService {
private static final Logger LOGER = LoggerFactory
.getLogger(SitesIndexService.class);
public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

// 网站平台索引查询
public static SolrDocumentList query(String link, int pageIndex,
int pageSize,String name) {
int offset = (pageIndex - 1) * pageSize;
return new IndexService().queryDocumentList(link ,offset, pageSize,name);
}

// 网站平台索引添加
public static void insert(String user ,String link, String title, String desc,
String content, String tags, String keywords,
String site, String type , Date date , String name) {

//SitesIndexDTO sitesIndex = new SitesIndexDTO();
SolrInputDocument doc = new SolrInputDocument();
if(link !=null && !"".equals(link)){
doc.addField("link", link);
}
if(title !=null && !"".equals(title)){
doc.addField("title",title);
}
if(keywords !=null && !"".equals(keywords)){
doc.addField("keywords", keywords);
}
if(desc !=null && !"".equals(desc)){
doc.addField("desc", desc);
}
if(content !=null && !"".equals(content)){
doc.addField("content", content);
}
if(tags !=null && !"".equals(tags)){
doc.addField("tags", tags);
}
doc.addField("source", "admin");
if(site !=null && !"".equals(site)){
doc.addField("site", site);
}
if(type !=null && !"".equals(type)){
doc.addField("type", type);
}
if(date !=null && !"".equals(date)){
doc.addField("last_modified", DateFormatUtils.format(date, DATE_PATTERN));
}
boolean b = new IndexService().indexDoc(doc , name);
if(true == b){

}
}


// 网站平台索引修改
public static void updateByLink(String user ,String link, String title, String keywords, String desc,
String tags, String content,
String site, String type, Date last_modified , String name) {
SolrDocument solrDoc = new IndexService().queryDocumentByLink(link ,name);
if(solrDoc.size() > 0){
SolrInputDocument doc = new SolrInputDocument();
if(link !=null && !"".equals(link)){
doc.setField("link" , link);
}
if(title !=null && !"".equals(title)){
doc.setField("title" , title);
}
if(desc !=null && !"".equals(desc)){
doc.setField("desc", desc);
}
if(content !=null && !"".equals(content)){
doc.setField("content" , content);
}
if(tags !=null && !"".equals(tags)){
doc.setField("tags",tags);
}
doc.setField("source" , "admin");
if(keywords !=null && !"".equals(keywords)){
doc.setField("keywords",keywords);
}
if(site !=null && !"".equals(site)){
doc.setField("site",site);
}
if(type !=null && !"".equals(type)){
doc.setField("type" , type);
}
if(last_modified !=null && !"".equals(last_modified)){
doc.setField("last_modified" , DateFormatUtils.format(last_modified, DATE_PATTERN));
}
boolean b = new IndexService().indexDoc(doc,name);
if(true == b){

}
}
}

// 网站平台索引删除
public static void deleteByLink(String user ,String link , String name){
LOGER.info("执行Link删除操作，ID:[" + link + "]");
boolean b = new IndexService().deleteIndexByLink(link , name);
if(true == b){

}
}

// 网站平台索引批量删除
public static void deleteAll(String user ,String link , String name){
boolean b = new IndexService().batchDeleteByLink(link,name);
if(true == b){

}
}

// 获取总记录数
public static long getCountByLink(String link , String name) {
return new IndexService().getCountByLink(link , name);
}


}