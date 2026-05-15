package org.example.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayParamFlowItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.*;

/**
 * Sentinel 网关配置
 * 配置网关层的限流和熔断规则
 */
@Slf4j
@Configuration
public class SentinelGatewayConfig {

        private final List<ViewResolver> viewResolvers;

        public SentinelGatewayConfig(ObjectProvider<List<ViewResolver>> viewResolversProvider) {
                this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        }

        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE)
        public SentinelGatewayFilter sentinelGatewayFilter() {
                return new SentinelGatewayFilter();
        }

        @PostConstruct
        public void initGatewayRules() {
                Set<GatewayFlowRule> rules = new HashSet<>();

                rules.add(createGatewayFlowRule("user-service", 100, 1));
                rules.add(createGatewayFlowRule("order-service", 100, 1));
                rules.add(createGatewayFlowRule("task-service", 100, 1));
                rules.add(createGatewayFlowRule("evaluation-service", 100, 1));
                rules.add(createGatewayFlowRule("message-service", 100, 1));
                rules.add(createGatewayFlowRule("search-service", 100, 1));
                rules.add(createGatewayFlowRule("admin-service", 50, 1));

                GatewayRuleManager.loadRules(rules);
                log.info("Sentinel 网关限流规则初始化完成，共加载 {} 条规则", rules.size());
        }

        private GatewayFlowRule createGatewayFlowRule(String resourceName, int count, int intervalSec) {
                GatewayFlowRule rule = new GatewayFlowRule(resourceName);
                rule.setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_ROUTE_ID);
                rule.setCount(count);
                rule.setIntervalSec(intervalSec);

                GatewayParamFlowItem paramItem = new GatewayParamFlowItem();
                paramItem.setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM);
                rule.setParamItem(paramItem);

                return rule;
        }

        @PostConstruct
        public void initCustomizedApis() {
                Set<ApiDefinition> definitions = new HashSet<>();

                ApiDefinition userApi = new ApiDefinition("user_api");
                Set<ApiPredicateItem> userPredicates = new HashSet<>();
                ApiPathPredicateItem userPattern1 = new ApiPathPredicateItem();
                userPattern1.setPattern("/user/**");
                userPredicates.add(userPattern1);
                ApiPathPredicateItem userPattern2 = new ApiPathPredicateItem();
                userPattern2.setPattern("/api/user/**");
                userPredicates.add(userPattern2);
                userApi.setPredicateItems(userPredicates);

                ApiDefinition orderApi = new ApiDefinition("order_api");
                Set<ApiPredicateItem> orderPredicates = new HashSet<>();
                ApiPathPredicateItem orderPattern1 = new ApiPathPredicateItem();
                orderPattern1.setPattern("/order/**");
                orderPredicates.add(orderPattern1);
                ApiPathPredicateItem orderPattern2 = new ApiPathPredicateItem();
                orderPattern2.setPattern("/api/order/**");
                orderPredicates.add(orderPattern2);
                orderApi.setPredicateItems(orderPredicates);

                ApiDefinition taskApi = new ApiDefinition("task_api");
                Set<ApiPredicateItem> taskPredicates = new HashSet<>();
                ApiPathPredicateItem taskPattern1 = new ApiPathPredicateItem();
                taskPattern1.setPattern("/task/**");
                taskPredicates.add(taskPattern1);
                ApiPathPredicateItem taskPattern2 = new ApiPathPredicateItem();
                taskPattern2.setPattern("/api/task/**");
                taskPredicates.add(taskPattern2);
                taskApi.setPredicateItems(taskPredicates);

                definitions.add(userApi);
                definitions.add(orderApi);
                definitions.add(taskApi);

                GatewayApiDefinitionManager.loadApiDefinitions(definitions);
                log.info("Sentinel API 定义初始化完成，共加载 {} 个 API 分组", definitions.size());
        }

        @PostConstruct
        public void initBlockHandlers() {
                BlockRequestHandler blockRequestHandler = (exchange, throwable) -> {
                        log.warn("网关限流触发: {}", throwable.getClass().getSimpleName());

                        Map<String, Object> result = new HashMap<>();
                        result.put("code", 429);
                        result.put("message", "请求过于频繁，请稍后重试");
                        result.put("data", null);

                        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(result);
                };

                GatewayCallbackManager.setBlockHandler(blockRequestHandler);
        }
}
