import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Mr.M
 * @version 1.0
 * @description Gateway authentication filter
 * @date 2022/9/27 12:10
 */
@Component
@Slf4j
public class GatewayAuthFilter implements GlobalFilter, Ordered {


    //Whitelist
    private static List<String> whitelist = null;

    static {
        //Load whitelist
        try (
                InputStream resourceAsStream = GatewayAuthFilter.class.getResourceAsStream("/security-whitelist.properties");
        ) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            Set<String> strings = properties.stringPropertyNames();
            whitelist= new ArrayList<>(strings);

        } catch (Exception e) {
            log.error("Load/security-whitelist.properties Error:{}",e.getMessage());
            e.printStackTrace();
        }


    }

    @Autowired
    private TokenStore tokenStore;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //Requested URL
        String requestUrl = exchange.getRequest().getPath().value();
        AntPathMatcher pathMatcher = new AntPathMatcher();
        //Whitelist bypass
        for (String url : whitelist) {
            if (pathMatcher.match(url, requestUrl)) {
                return chain.filter(exchange);
            }
        }

        //Check if the token exists
        String token = getToken(exchange);
        if (StringUtils.isBlank(token)) {
            return buildReturnMono("No authentication",exchange);
        }
        //Determine if the token is valid
        OAuth2AccessToken oAuth2AccessToken;
        try {
            oAuth2AccessToken = tokenStore.readAccessToken(token);

            boolean expired = oAuth2AccessToken.isExpired();
            if (expired) {
                return buildReturnMono("Authentication token has expired",exchange);
            }
            return chain.filter(exchange);
        } catch (InvalidTokenException e) {
            log.info("Authentication token is invalid: {}", token);
            return buildReturnMono("Authentication token is invalid",exchange);
        }

    }

    /**
     * Get token
     */
    private String getToken(ServerWebExchange exchange) {
        String tokenStr = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (StringUtils.isBlank(tokenStr)) {
            return null;
        }
        String token = tokenStr.split(" ")[1];
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return token;
    }




    private Mono<Void> buildReturnMono(String error, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        String jsonString = JSON.toJSONString(new RestErrorResponse(error));
        byte[] bits = jsonString.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
