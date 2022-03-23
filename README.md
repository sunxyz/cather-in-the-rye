# cather-in-the-rye

> 不管怎样，我老是在想象，有那么一群小孩子在一大块麦田里做游戏。几千几万个小孩子，附近没有一个人——没有一个大人，我是说——除了我。我呢，就站在那混帐的悬崖边。我得职务是在那儿守望，要是有哪个孩子往悬崖边奔来，我就把他捉住——我是说孩子们都在狂奔，也不知道自己是在往哪儿跑，我得从什么地方出来，把他们捉住。我整天就干这样的事。我只想当个麦田里的守望者（I'd just be the catcher in the rye and all.）。

## 用户接口
**方法**

登录相关
- RyeCatcher.login(id, deviceType)

拿到session或登录信息
- RyeCatcher.isLogin()
- RyeCatcher.getLogin()
- RyeCatcher.getLoginId()
- RyeCatcher.findSession()
- RyeCatcher.getSession()
- RyeCatcher.getSavedSessionByLogin(id, deviceType)

匹配、校验
- RyeCatcher.allMatch(type,...authKeys)
- RyeCatcher.anyMatch(type,...authKeys)
- RyeCatcher.noMatch(type,...authKeys)
- RyeCatcher.check(type,matchRelation,...authKeys)
- RyeCatcher.checkLogin()

临时切换账号
- RyeCatcher.switchTo(id, deviceType);
- RyeCatcher.stopSwitch();
-
退出、踢出
- RyeCatcher.logout()
- RyeCatcher.kickOut(id, deviceType)

**注解**

jsr-250
- @RolesAllowed
- @PermitAll
- @DenyAll

自定义
- @CheckRoles(matchRelation, ...s)
- @CheckPermissions(matchRelation, ...s)

**路径**
- UriMatcher.matchHandler(matchPath, handler)
- UriMatcher.match(matchPath).handler(handler)
- UriMatcher.notMatch(matchPath)
- UriMatcher.stopNext

示例
```
 ReqMatcherInterceptor reqMatcherInterceptor = new ReqMatcherInterceptor(req ->
        req.matches("/**", RcCheckHelper::noCheck,
                req.matches("/api/**").notMatches("/api/version").childScope(
                        req.matches("/api/hello", (request, handler) -> {
                            handler.returnRes("hello");
                        }),
                        req.matches(HttpMethod.GET, "/api/say", () -> checkAllRole("user"))
                ).build(),
                req.matches(request -> Arrays.stream(request.getCookies()).anyMatch(cookie -> cookie.getName().equals("no-login")),
                        request -> ValidateUtils.check(Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("no-login")).findFirst().map(cookie -> cookie.getValue().equals("true")).orElse(false), "no-login"))
        )
);
ReqMatcherInterceptor demoReqMatcherInterceptor = new ReqMatcherInterceptor(req ->
        req.matches("/**", RcCheckHelper::checkLogin,
                req.matches("/api/**", request -> {
                    RcCheckHelper.checkAllPerm(request.getRequestURI().replaceAll("/api","").replaceAll("/",":"));
                })
        )
);
```
## 使用

目前支持在spring-boot中直接使用

**1.起步**

1.1 pom 引入
```
<dependency>
  <groupId>org.bitmagic.lab</groupId>
  <artifactId>catcher-in-the-rye</artifactId>
  <version>1.0</version>
</dependency>
```
1.2 实现LoadMatchInfoService

```java
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
class CustomAuthMatchInfoProvider implements AuthMatchInfoProvider {
    //ryeCatcherPath: multi-certification-system-info 中匹配到的路径 优先uri长度长的
    public Map<String, Collection<String>> loadAuthMatchInfo(String ryeCatcherPath, Object id, String deviceType) {
        Map<String, Collection<String>> matchInfos = new HashMap<>();
        matchInfos.put("role", Arrays.asList("user", "admin"));
        matchInfos.put("perm", Arrays.asList("user:add", "user:view"));
        return matchInfos;
    }
}
```
1.3 yml 配置
```yml
# 默认值是cookie相关 可以不做配置
rye-catcher:
  # 认证体系
  multi-certification-system-info:
    # 根路径下认证信息
    /:
        # 默认值cookie
        gen-token-type: cookie/jwt_token
        # 默认值JSESSIONID 当有多个路径配置时 不建议使用相同的名称 此值当session-need-out-client: true时会生效
        out-client-token-name: JSESSIONID
        # 默认值30分钟
        session-time-out-millisecond: 180000
        session-need-save: true
        session-need-out-client: true
        login-mutex: true
```
**2.开始**

2.1 登录 退出

```java
import org.bitmagic.lab.reycatcher.LoginInfo;
import org.bitmagic.lab.reycatcher.RyeCatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
class AuthController {

    @PostMapping("/login")
    public void login(String username, String password) {
        loginValidate(username, password);
        RyeCatcher.login(username);
    }

    @PostMapping("/logout")
    public void logout() {
        RyeCatcher.logout();
    }
}
```
2.2 获取当前用户

```java
import org.bitmagic.lab.reycatcher.LoginInfo;
import org.bitmagic.lab.reycatcher.RyeCatcher;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
class MeController {

    @PostMapping("/me")
    public LoginInfo me() {
        return RyeCatcher.getLogin();
    }
}
```
2.3 校验

2.3.1 注解校验
```java
@Configuration
public class RyeCatcherMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AnnotationInterceptor()).addPathPatterns("/**");    
    }
}
```

```java
import org.bitmagic.lab.reycatcher.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/demo")
class DemoController {


    @GetMapping("/hello")
    @CheckRole("user")
    public void hello() {
    }

    @GetMapping("/hello0")
    @CheckRole(value = {"user", "admin"}, matchRelation = MatchRelation.ALL)
    public void hello0() {
    }

    @GetMapping("/hello1")
    @CheckRole(value = {"user", "admin"}, matchRelation = MatchRelation.NONE)
    public void hello1() {
    }

    @GetMapping("/hello2")
    @CheckRole(value = {"user", "admin"}, matchRelation = MatchRelation.ANY)
    public void hello2() {
    }

    @GetMapping("/hello3")
    @CheckPermission(value = {"user:add", "admin:add"}, matchRelation = MatchRelation.ALL)
    public void hello3() {
    }

    @GetMapping("/hello4")
    @CheckPermission(value = {"user:*", "admin:*"}, matchRelation = MatchRelation.NONE)
    public void hello4() {
    }

    @GetMapping("/hello5")
    @CheckPermission(value = {"user:*", "admin:**"}, matchRelation = MatchRelation.ANY)
    public void hello5() {
    }

    @GetMapping("/hello6")
    @RolesAllowed({"user", "admin"})
    public void rolesAllowed() {
    }

    @GetMapping("/hello7")
    @PermitAll
    public void permitAll() {
    }

    @GetMapping("/hello8")
    @DenyAll
    public void denyAll() {
    }
}
```
2.3.2 路径拦截校验

```java
import org.bitmagic.lab.reycatcher.RyeCatcher;

@Configuration
public class RyeCatcherMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(
                new ReqMatcherInterceptor(req ->
                        req.matches("/**", RcCheckHelper::noCheck,
                                req.matches("/api/**").notMatches("/api/version").childScope(
                                        req.matches("/api/hello", (request, handler) -> {
                                            handler.returnRes("hello");
                                        }),
                                        req.matches(HttpMethod.GET, "/api/say", () -> hasRole("user"))
                                ).build(),
                                req.matches(request -> Arrays.stream(request.getCookies()).anyMatch(cookie -> cookie.getName().equals("no-login")),
                                        request -> ValidateUtils.check(Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("no-login")).findFirst().map(cookie -> cookie.getValue().equals("true")).orElse(false), "no-login"))
                        )
                );
        ).addPathPatterns("/**");
    }
}
```
2.3.3 方法校验

```java
import org.bitmagic.lab.reycatcher.MatchRelation;
import org.bitmagic.lab.reycatcher.RyeCatcher;
import org.bitmagic.lab.reycatcher.helper.RcCheckHelper;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/demo")
class DemoController {

    @GetMapping("/hello")
    public void hello() {
        RyeCatcher.check("role", MatchRelation.ANY, "user", "admin");
        RyeCatcher.check("perm", MatchRelation.ANY, "user:add", "user:view");
        RcCheckHelper.checkRole("admin");
        RcCheckHelper.checkPerm("user:view");
        RyeCatcher.checkLogin();
    }

    @GetMapping("/hello0")
    public void hello0() {
        boolean matchFlag = RyeCatcher.allMatch("role", "user", "admin");
        System.out.println(matchFlag);
    }
}
```
## 自定义扩展
**鉴权信息**
```java
import java.util.List;
import java.util.Map;

interface AuthMatchInfoProvider {
    Map<String, List<String>> loadAuthMatchInfo(String ryeCatcherPath, Object id, String deviceType);
}
```
**会话持续时间续订**

```java
public interface SessionDurationRenewal {

    void renewal(SessionToken token);
}

```
**身份切换**

```java
public interface IdentitySwitch {

    void switchId(LoginInfo from, LoginInfo to);

    Optional<LoginInfo> findSwitchIdTo(LoginInfo from);
}
```
**会话存储**
```java
import java.util.Collection;
import java.util.Optional;

interface SessionRepository extends SessionDurationRenewal, IdentitySwitch{

    void save(Session session);

    void remove(Session session);

    Optional<Session> findOne(Object id, String deviceType);

    Optional<Session> findByToken(SessionToken token);

    Page<Session> findAll(Object filterInfo, int size, int page);

    Collection<Session> listAll(Object filterInfo);
}
```
**会话管理**
```java
import java.util.Optional;

interface SessionManager extends SessionRepository {

    Session genSession(Object id, String deviceType, String sessionTokenType, Object meta, Object clientExtMeta);

    Optional<Session> getCurrentSession(String tokenName);

    Optional<ReqTokenInfo> findSessionTokenFromClient(String tokenName);

    void outSession2Client(String tokenName, Session session);
}
```

**参考资料：**

> 方法命名参考了 Apache Shiro , Spring Security, Sa-Token