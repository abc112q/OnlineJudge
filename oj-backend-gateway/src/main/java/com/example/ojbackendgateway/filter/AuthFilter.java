package com.example.ojbackendgateway.filter;

import com.example.ojbackendcommon.utils.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ariel
* 统一权限校验
*/
 // @Component
public class AuthFilter implements GlobalFilter, Ordered {
    /**
     * 放行白名单
     */
    @Value("#{'${gateway.excludedUrls}'.split(',')}")
    private List<String> excludedUrls;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取当前请求的路径
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String path = serverHttpRequest.getURI().getPath();
        // 1.如果当前路径不需要校验 放行
        if(excludedUrls.contains(path)){
            return chain.filter(exchange);
        }
        //2. 获取token并进行校验
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if(!StringUtils.isEmpty(token)){
            // 不为空时把 ("Bearer"去掉) 有时候前端传来的 token是带这个的
            token = token.replace("Bearer","");
        }
        final ServerHttpResponse response = exchange.getResponse();
        final boolean verifyToken = JwtUtils.verifyToken(token);
        if(!verifyToken){
            // 返回错误信息
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("errCode", 401);
            responseData.put("errMessage", "用户未登录");
            return responseError(response, responseData);
        }
        return chain.filter(exchange);
    }

    /**
     * 响应错误数据 这个方法就是将这个map转化为JSON
     *
     * @param response
     * @param responseData
     * @return
     */
    private Mono<Void> responseError(ServerHttpResponse response, Map<String, Object> responseData) {
        // 将信息转换为 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] data = new byte[0];
        try {
            data = objectMapper.writeValueAsBytes(responseData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // 输出错误信息到页面
        DataBuffer buffer = response.bufferFactory().wrap(data);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }


    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
