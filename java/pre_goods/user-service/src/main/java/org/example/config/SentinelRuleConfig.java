package org.example.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Sentinel 限流熔断规则初始化配置
 * 在应用启动时自动加载限流和熔断规则
 */
@Slf4j
@Configuration
public class SentinelRuleConfig {

    @PostConstruct
    public void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();

        // 1. 登录接口限流：QPS = 20（防止暴力破解）
        FlowRule loginRule = new FlowRule();
        loginRule.setResource("user_login");
        loginRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        loginRule.setCount(20);
        loginRule.setLimitApp("default");
        rules.add(loginRule);

        // 2. 注册接口限流：QPS = 10（防止恶意注册）
        FlowRule registerRule = new FlowRule();
        registerRule.setResource("user_register");
        registerRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        registerRule.setCount(10);
        registerRule.setLimitApp("default");
        rules.add(registerRule);

        // 3. 获取用户信息限流：QPS = 100
        FlowRule profileRule = new FlowRule();
        profileRule.setResource("user_profile");
        profileRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        profileRule.setCount(100);
        profileRule.setLimitApp("default");
        rules.add(profileRule);

        // 4. 更新用户信息限流：QPS = 50
        FlowRule updateProfileRule = new FlowRule();
        updateProfileRule.setResource("user_update_profile");
        updateProfileRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        updateProfileRule.setCount(50);
        updateProfileRule.setLimitApp("default");
        rules.add(updateProfileRule);

        // 5. 获取积分限流：QPS = 100
        FlowRule pointsRule = new FlowRule();
        pointsRule.setResource("user_points");
        pointsRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        pointsRule.setCount(100);
        pointsRule.setLimitApp("default");
        rules.add(pointsRule);

        // 6. 获取信用限流：QPS = 100
        FlowRule creditRule = new FlowRule();
        creditRule.setResource("user_credit");
        creditRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        creditRule.setCount(100);
        creditRule.setLimitApp("default");
        rules.add(creditRule);

        // 7. 根据ID获取用户信息限流：QPS = 200（供其他服务调用）
        FlowRule getProfileByIdRule = new FlowRule();
        getProfileByIdRule.setResource("user_get_profile_by_id");
        getProfileByIdRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        getProfileByIdRule.setCount(200);
        getProfileByIdRule.setLimitApp("default");
        rules.add(getProfileByIdRule);

        // 8. 根据学号获取用户信息限流：QPS = 200（供其他服务调用）
        FlowRule getByStudentIdRule = new FlowRule();
        getByStudentIdRule.setResource("user_get_by_student_id");
        getByStudentIdRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        getByStudentIdRule.setCount(200);
        getByStudentIdRule.setLimitApp("default");
        rules.add(getByStudentIdRule);

        FlowRuleManager.loadRules(rules);
        log.info("Sentinel 限流规则初始化完成，共加载 {} 条规则", rules.size());
    }

    @PostConstruct
    public void initDegradeRules() {
        List<DegradeRule> rules = new ArrayList<>();

        // 1. 登录接口熔断：响应时间超过 1000ms 的比例 > 50%，时间窗口 10s，最小请求数 5
        DegradeRule loginDegradeRule = new DegradeRule();
        loginDegradeRule.setResource("user_login");
        loginDegradeRule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
        loginDegradeRule.setCount(1000);
        loginDegradeRule.setTimeWindow(10);
        loginDegradeRule.setMinRequestAmount(5);
        loginDegradeRule.setStatIntervalMs(10000);
        rules.add(loginDegradeRule);

        // 2. 注册接口熔断：响应时间超过 1500ms，时间窗口 10s
        DegradeRule registerDegradeRule = new DegradeRule();
        registerDegradeRule.setResource("user_register");
        registerDegradeRule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
        registerDegradeRule.setCount(1500);
        registerDegradeRule.setTimeWindow(10);
        registerDegradeRule.setMinRequestAmount(5);
        registerDegradeRule.setStatIntervalMs(10000);
        rules.add(registerDegradeRule);

        // 3. 获取用户信息熔断：响应时间超过 800ms，时间窗口 10s
        DegradeRule profileDegradeRule = new DegradeRule();
        profileDegradeRule.setResource("user_profile");
        profileDegradeRule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
        profileDegradeRule.setCount(800);
        profileDegradeRule.setTimeWindow(10);
        profileDegradeRule.setMinRequestAmount(5);
        profileDegradeRule.setStatIntervalMs(10000);
        rules.add(profileDegradeRule);

        // 4. 获取积分熔断：异常比例 > 50%，时间窗口 10s
        DegradeRule pointsDegradeRule = new DegradeRule();
        pointsDegradeRule.setResource("user_points");
        pointsDegradeRule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO);
        pointsDegradeRule.setCount(0.5);
        pointsDegradeRule.setTimeWindow(10);
        pointsDegradeRule.setMinRequestAmount(5);
        pointsDegradeRule.setStatIntervalMs(10000);
        rules.add(pointsDegradeRule);

        // 5. 获取信用熔断：异常比例 > 50%，时间窗口 10s
        DegradeRule creditDegradeRule = new DegradeRule();
        creditDegradeRule.setResource("user_credit");
        creditDegradeRule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO);
        creditDegradeRule.setCount(0.5);
        creditDegradeRule.setTimeWindow(10);
        creditDegradeRule.setMinRequestAmount(5);
        creditDegradeRule.setStatIntervalMs(10000);
        rules.add(creditDegradeRule);

        DegradeRuleManager.loadRules(rules);
        log.info("Sentinel 熔断规则初始化完成，共加载 {} 条规则", rules.size());
    }
}
