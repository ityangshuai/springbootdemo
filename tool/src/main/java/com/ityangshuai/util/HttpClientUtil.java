package com.ityangshuai.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

public class HttpClientUtil {

    protected final Log LOG = LogFactory.getLog(HttpClientUtil.class);
    private static HttpClientUtil instance;
    protected Charset charset;

    private HttpClientUtil(){}

    public static HttpClientUtil getInstance() {
        return getInstance(Charset.defaultCharset());
    }

    public static HttpClientUtil getInstance(Charset charset){
        if(instance == null){
            instance = new HttpClientUtil();
        }
        instance.setCharset(charset);
        return instance;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /**
     * post请求
     */
    public String doPost(String url) throws Exception {
        return doPost(url, null, null);
    }

    public String doPost(String url, Map<String, Object> params) throws Exception {
        return doPost(url, params, null);
    }

    public String doPost(String url, Map<String, Object> params, Map<String, String> header) throws Exception {
        String body = null;
        try {
            // Post请求
            LOG.debug(" protocol: POST");
            LOG.debug("      url: " + url);
            HttpPost httpPost = new HttpPost(url.trim());
            // 设置参数
            LOG.debug("   params: " + JSON.toJSONString(params));
            httpPost.setEntity(new UrlEncodedFormEntity(map2NameValuePairList(params), charset));
            // 设置Header
            if (header != null && !header.isEmpty()) {
                LOG.debug("   header: " + JSON.toJSONString(header));
                for (Iterator<Map.Entry<String, String>> it = header.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
                    httpPost.setHeader(new BasicHeader(entry.getKey(), entry.getValue()));
                }
            }
            // 发送请求,获取返回数据
            body = execute(httpPost);
        } catch (Exception e) {
            throw e;
        }
        LOG.debug("   result: " + body);
        return body;
    }

    /**
     * postJson请求
     */
    public String doPostJson(String url, Map<String, Object> params) throws Exception {
        return doPostJson(url, params, null);
    }

    public String doPostJson(String url, Map<String, Object> params, Map<String, String> header) throws Exception {
        String json = null;
        if (params != null && !params.isEmpty()) {
            for (Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
                Object object = entry.getValue();
                if (object == null) {
                    it.remove();
                }
            }
            json = JSON.toJSONString(params);
        }
        return postJson(url, json, header);
    }

    public String doPostJson(String url, String json) throws Exception {
        return doPostJson(url, json, null);
    }

    public String doPostJson(String url, String json, Map<String, String> header) throws Exception {
        return postJson(url, json, header);
    }

    private String postJson(String url, String json, Map<String, String> header) throws Exception {
        String body = null;
        try {
            // Post请求
            LOG.debug(" protocol: POST");
            LOG.debug("      url: " + url);
            HttpPost httpPost = new HttpPost(url.trim());
            // 设置参数
            LOG.debug("   params: " + json);
            httpPost.setEntity(new StringEntity(json, ContentType.DEFAULT_TEXT.withCharset(charset)));
            httpPost.setHeader(new BasicHeader("Content-Type", "application/json"));
            LOG.debug("     type: JSON");
            // 设置Header
            if (header != null && !header.isEmpty()) {
                LOG.debug("   header: " + JSON.toJSONString(header));
                for (Iterator<Map.Entry<String, String>> it = header.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
                    httpPost.setHeader(new BasicHeader(entry.getKey(), entry.getValue()));
                }
            }
            // 发送请求,获取返回数据
            body = execute(httpPost);
        } catch (Exception e) {
            throw e;
        }
        LOG.debug("  result: " + body);
        return body;
    }

    /**
     * get请求
     */
    public String doGet(String url) throws Exception {
        return doGet(url, null, null);
    }

    public String doGet(String url, Map<String, String> header) throws Exception {
        return doGet(url, null, header);
    }

    public String doGet(String url, Map<String, Object> params, Map<String, String> header) throws Exception {
        String body = null;
        try {
            // Get请求
            LOG.debug("protocol: GET");
            HttpGet httpGet = new HttpGet(url.trim());
            // 设置参数
            if (params != null && !params.isEmpty()) {
                String str = EntityUtils.toString(new UrlEncodedFormEntity(map2NameValuePairList(params), charset));
                String uri = httpGet.getURI().toString();
                if(uri.indexOf("?") >= 0){
                    httpGet.setURI(new URI(httpGet.getURI().toString() + "&" + str));
                }else {
                    httpGet.setURI(new URI(httpGet.getURI().toString() + "?" + str));
                }
            }
            LOG.debug("     url: " + httpGet.getURI());
            // 设置Header
            if (header != null && !header.isEmpty()) {
                LOG.debug("   header: " + header);
                for (Iterator<Map.Entry<String, String>> it = header.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
                    httpGet.setHeader(new BasicHeader(entry.getKey(), entry.getValue()));
                }
            }
            // 发送请求,获取返回数据
            body =  execute(httpGet);
        } catch (Exception e) {
            throw e;
        }
        LOG.debug("  result: " + body);
        return body;
    }

    /**
     * 下载文件
     */
    public void doDownload(String url, String path) throws Exception {
        download(url, null, path);
    }

    public void doDownload(String url, Map<String, Object> params, String path) throws Exception {
        download(url, params, path);
    }

    /**
     * 上传文件
     */
    public String doUpload(String url, String name, String path) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(name, new File(path));
        return doUpload(url, params);
    }

    public String doUpload(String url, Map<String, Object> params) throws Exception {
        String body = null;
        // Post请求
        HttpPost httpPost = new HttpPost(url.trim());
        // 设置参数
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        entityBuilder.setCharset(charset);
        if (params != null && !params.isEmpty()) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Object value = params.get(key);
                if (value instanceof File) {
                    FileBody fileBody = new FileBody((File) value);
                    entityBuilder.addPart(key, fileBody);
                } else {
                    entityBuilder.addPart(key, new StringBody(String.valueOf(value), ContentType.DEFAULT_TEXT.withCharset(charset)));
                }
            }
        }
        HttpEntity entity = entityBuilder.build();
        httpPost.setEntity(entity);
        // 发送请求,获取返回数据
        body = execute(httpPost);
        return body;
    }

    private void download(String url, Map<String, Object> params, String path) throws Exception {
        // Get请求
        HttpGet httpGet = new HttpGet(url.trim());
        if (params != null && !params.isEmpty()) {
            // 设置参数
            String str = EntityUtils.toString(new UrlEncodedFormEntity(map2NameValuePairList(params)));
            String uri = httpGet.getURI().toString();
            if (uri.indexOf("?") >= 0) {
                httpGet.setURI(new URI(httpGet.getURI().toString() + "&" + str));
            } else {
                httpGet.setURI(new URI(httpGet.getURI().toString() + "?" + str));
            }
        }
        // 发送请求,下载文件
        downloadFile(httpGet, path);
    }

    private void downloadFile(HttpRequestBase requestBase, String path) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = httpclient.execute(requestBase);
            try {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    byte[] b = EntityUtils.toByteArray(entity);
                    OutputStream out = new BufferedOutputStream(new FileOutputStream(new File(path)));
                    out.write(b);
                    out.flush();
                    out.close();
                }
                EntityUtils.consume(entity);
            } catch (Exception e) {
                throw e;
            } finally {
                response.close();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            httpclient.close();
        }
    }

    private String execute(HttpRequestBase requestBase) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String body = null;
        try {
            CloseableHttpResponse response = httpclient.execute(requestBase);
            try {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    body = EntityUtils.toString(entity, charset.toString());
                }
                EntityUtils.consume(entity);
            } catch (Exception e) {
                throw e;
            }finally {
                response.close();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            httpclient.close();
        }
        return body;
    }

    private List<NameValuePair> map2NameValuePairList(Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                if(params.get(key) != null) {
                    String value = String.valueOf(params.get(key));
                    list.add(new BasicNameValuePair(key, value));
                }
            }
            return list;
        }
        return null;
    }

    //发送restful的get post put 和 delete 请求
    public String doPut(String url, String entityString, Map<String,String> headerMap) throws Exception {
        String body = null;
        HttpPut httpput = new HttpPut(url);
        try{
            httpput.setEntity(new StringEntity(entityString));

            for(Map.Entry<String, String> entry : headerMap.entrySet()) {
                httpput.addHeader(entry.getKey(), entry.getValue());
            }
            //发送put请求
            body = execute(httpput);
        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return body;
    }

    public String doDelete(String url, Map<String, String> header) throws Exception{
        String body = null;
        HttpDelete httpdel = new HttpDelete(url);

        try{
            // 设置Header
            if (header != null && !header.isEmpty()) {
                LOG.debug("   header: " + header);
                for (Iterator<Map.Entry<String, String>> it = header.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
                    httpdel.setHeader(new BasicHeader(entry.getKey(), entry.getValue()));
                }
            }
            body = execute(httpdel);
        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return body;
    }


}
