package com.lck.demo.commonutils.utils.rest;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lck.demo.commonutils.spring.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * restTemplate Rest 工具类
 *
 * @author ckli01
 * @date 2018/7/4
 */
@Slf4j
public class RestUtils {


    /**
     * get 请求返回体
     *
     * @param url 请求路由
     * @param map 请求参数
     * @return
     * @throws Exception
     */
    private static ResponseEntity<String> getResponseEntity(String url, Map<String, Object> map) throws Exception {
        RestTemplate restTemplate = getRestTemplate();

        ResponseEntity<String> result;
        if (CollectionUtils.isEmpty(map)) {
            result = restTemplate.getForEntity(url, String.class);
        } else {
            result = restTemplate.getForEntity(url, String.class, map);
        }
        return result;
    }

    /**
     * 直接返回rest 请求内容
     *
     * @param url    请求地址
     * @param result 请求返回内容
     * @return
     */
    private static String resultResponse(String url, ResponseEntity<String> result) {
        log.info("rest request for url:{} get responseCode: {}", url, result.getStatusCode().toString());
        if (HttpStatus.OK.equals(result.getStatusCode())) {
            return result.getBody();
        } else {
            log.error("rest request may got a wrong response for: {} ", result.getBody());
            return "";
        }
    }

    /**
     * rest get 请求返回String 请求体
     *
     * @param url 请求路由
     * @param map 请求参数
     * @return
     */
    public static String get(String url, Map<String, Object> map) {
        try {
            if (StringUtils.isEmpty(url)) {
                log.error("rest request url can't be null or empty");
                return "";
            }
            ResponseEntity<String> result = getResponseEntity(url, map);
            return resultResponse(url, result);
        } catch (Exception e) {
            log.error("{} for url: {}", e.getMessage(), url, e);
            return "";
        }
    }


    /**
     * get 请求 返回 HttpRestResult 封装体
     *
     * @param url 请求路由
     * @param map 请求参数
     * @return
     */
    public static <T> HttpRestResult<T> getForHttpRestResult(String url, Map<String, Object> map) {
        try {
            if (StringUtils.isEmpty(url)) {
                return new HttpRestResult<>(false, null, "", "url can't be null or empty");
            }
            ResponseEntity<String> result = getResponseEntity(url, map);
            return resultEntityResponse(url, result);
        } catch (Exception e) {
            log.error("{} for url: {}", e.getMessage(), url, e);
            return new HttpRestResult<>(false, null, "", e.getMessage());
        }
    }


    /**
     * 构造返回结构体
     *
     * @param url    请求地址
     * @param result 请求返回内容
     * @param <T>    请求封装转换类
     * @return¬
     */
    private static <T> HttpRestResult<T> resultEntityResponse(String url, ResponseEntity<String> result) {
        log.info("rest request for url:{} get responseCode: {}", url, result.getStatusCode().toString());
        if (HttpStatus.OK.equals(result.getStatusCode())) {
            return JSONObject.parseObject(result.getBody(), new TypeReference<HttpRestResult<T>>() {
            });
        } else {
            log.error("rest request may got a wrong response for: {} ", result.getBody());
            return new HttpRestResult<>(false, null, result.getStatusCode().toString(), result.getBody());
        }
    }

    /**
     * post 请求返回体
     *
     * @param url
     * @param object
     * @return
     * @throws Exception
     */
    private static ResponseEntity<String> postResponseEntity(String url, Object object) throws Exception {
        RestTemplate restTemplate = getRestTemplate();

        HttpHeaders httpHeaders = getHeaders();

        String requestBody = "";
        if (object != null) {
            requestBody = JSONObject.toJSONString(object);
        }
        //利用容器实现数据封装，发送
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);

        return restTemplate.postForEntity(url, entity, String.class);
    }


    /**
     * POST 请求返回封装HttpRestResult
     *
     * @param url    请求路由
     * @param object 实体
     * @param <T>    返回类型实体类
     * @return
     */
    public static <T> HttpRestResult<T> postForHttpRestResult(String url, Object object) {
        try {
            if (StringUtils.isEmpty(url)) {
                return new HttpRestResult<>(false, null, "", "url can't be null or empty");
            }
            ResponseEntity<String> result = postResponseEntity(url, object);
            return resultEntityResponse(url, result);
        } catch (Exception e) {
            log.error("{} for url: {}", e.getMessage(), url, e);
            return new HttpRestResult<>(false, null, "", e.getMessage());
        }
    }

    /**
     * rest post 请求直接返回
     *
     * @param url
     * @param object
     * @return
     */
    public static String post(String url, Object object) {
        try {
            if (StringUtils.isEmpty(url)) {
                log.error("rest request url can't be null or empty");
                return "";
            }
            ResponseEntity<String> result = postResponseEntity(url, object);
            return resultResponse(url, result);
        } catch (Exception e) {
            log.error("{} for url: {}", e.getMessage(), url, e);
            return "";
        }
    }


    /**
     * rest delete 请求 无返回
     *
     * @param url
     * @param map
     */
    public static void delete(String url, Map<String, Object> map) {
        try {
            if (StringUtils.isEmpty(url)) {
                log.error("rest request url can't be null or empty");
            } else {
                RestTemplate restTemplate = getRestTemplate();

                if (CollectionUtils.isEmpty(map)) {
                    restTemplate.delete(url);
                } else {
                    restTemplate.delete(url, map);
                }
            }
        } catch (Exception e) {
            log.error("{} for url: {}", e.getMessage(), url, e);
        }
    }

    /**
     * 获取restTemplate 实例
     *
     * @return
     */
    private static RestTemplate getRestTemplate() {
        return SpringContextUtil.getBean("restTemplate");
    }

    /**
     * 设置HTTP请求头信息，实现编码,返回接受类型等
     *
     * @return
     */
    private static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        return headers;
    }


}
