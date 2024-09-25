package com.example.ojbackendgateway.filter;

import cn.hutool.core.text.AntPathMatcher;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Ariel
 * 像内部调用的inner接口还是要过滤一下，防止外部用户访问（比如根据id获取用户信息这种）
 * 网关接收到内部inner请求就要拦截
 * filter就是拿到所有请求信息，根据请求信息的地址判断是否可以访问
 */
@Component
public class GlobalAuthFilter implements GlobalFilter, Ordered {


    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 防止用户调用内部使用的接口
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String path = serverHttpRequest.getURI().getPath();
        // 判断路径中是否包含 inner，只允许内部调用  注意pattern前后不要有多余的空格
        if (antPathMatcher.match("/**/inner/**", path)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            DataBuffer dataBuffer = dataBufferFactory.wrap("无权限".getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(dataBuffer));
        }
        return chain.filter(exchange);
    }

    /**
     * 优先级提到最高
     * 确保对访问内部接口的检查是最优先的，接下来还可以进行统一权限校验
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}

