---
trigger: always_on
---

## user
* 账号 123
* 密码 123456
* token Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjcsImlhdCI6MTc3NzIwMjY5NSwiZXhwIjoxNzc3ODA3NDk1fQ.Ennh2TTavy4Fv73Pb_fX0ebnCZMfso6A3orempOpRY8
* 使用 common 中的config中的账号和密码
1. PowerShell,bash语法,curl,run_in_terminal


* 在Windows PowerShell环境中，不支持bash/sh语法中的||逻辑或操作符和<重定向操作符，执行curl等命令时需改用PowerShell原生命令如Invoke-WebRequest。（来源：run_in_terminal执行curl -s ... || curl失败）

* Spring Security,401错误,SecurityAutoConfiguration,启动类配置


* Spring Boot项目若引入spring-boot-starter-security依赖，默认会启用全局安全拦截，所有HTTP接口均返回401未授权。需在启动类上添加@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})显式排除Security自动配置才能临时放行接口。（来源：run_in_terminal调用接口返回401，grep确认security依赖存在）


* 在Windows PowerShell环境中，不能直接使用Linux风格的curl命令发送带Headers的HTTP请求，会报错'无法绑定参数Headers'。必须使用Invoke-RestMethod命令，并以哈希表格式传递Headers（如@{"Content-Type"="application/json"}）。（来源：run_in_terminal执行curl失败）
