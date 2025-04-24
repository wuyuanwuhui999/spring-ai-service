package com.player.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.util.Timeout;
import org.springframework.util.StringUtils;
import java.io.*;
import java.util.UUID;

public class HttpUtils {

    public static PoolingHttpClientConnectionManager getCm(){
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        // 设置最大连接数
        cm.setMaxTotal(100);
        // 设置每个主机的最大连接数
        cm.setDefaultMaxPerRoute(10);
        return cm;
    }

    /**
     * 根据请求地址下载页面数据
     */
    public static String doGet(String url) {
        return doGet(url, "c.y.qq.com");
    }

    public static String doGet(String url, String host) {
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(getCm())
                .build()) {

            HttpGet httpGet = new HttpGet(url);
            configureRequest(httpGet, host);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                if (response.getCode() == 200) {
                    return EntityUtils.toString(response.getEntity(), "UTF-8");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 下载图片
     */
    public static String doGetFile(String url, String path) {
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(getCm())
                .build()) {

            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(getConfig());
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");

            try (CloseableHttpResponse response = httpClient.execute(httpGet);
                 OutputStream outputStream = new FileOutputStream(getOutputFile(url, path))) {

                if (response.getCode() == 200) {
                    response.getEntity().writeTo(outputStream);
                    return getFileName(url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static void configureRequest(HttpGet httpGet, String host) {
        httpGet.setConfig(getConfig());
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
        httpGet.addHeader("Referer", "https://" + host + "/");
        httpGet.addHeader("Host", host);
    }

    private static RequestConfig getConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(1000))
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(500))
                .setResponseTimeout(Timeout.ofMilliseconds(10000))
                .build();
    }

    private static String getFileName(String url) {
        String extName = url.contains(".") ? url.substring(url.lastIndexOf(".")) : ".jpg";
        return UUID.randomUUID() + extName;
    }

    private static File getOutputFile(String url, String path) throws IOException {
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, getFileName(url));
    }

    // 以下方法保持原样
    public static String getPath(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        return !StringUtils.isEmpty(queryString) ? requestURI + "?" + queryString : requestURI;
    }

    public static String getRandom() {
        String random = Math.random() + "";
        return random.substring(2);
    }
}
