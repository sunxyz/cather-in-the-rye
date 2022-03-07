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
- UriMatcher.stopNext

示例
```
new UriMatcherInterceptor()
                .matchHandler("/app", (request, response, uriMatch) ->{ RyeCatcher.check("role", MatchRelation.ANY, "user"); uriMatch.stopNext();})
                .matchHandler("/app/**", (request, response) -> RyeCatcher.check("perm", MatchRelation.ANY, "user"));
```

## 自定义扩展
 **鉴权信息**
```java
import java.util.List;
import java.util.Map;

interface CatcherLoadMatchInfoService {
    Map<String, List<String>> loadMatchInfo(Object id, String deviceType);
}
```
**会话存储**

```java
import java.util.Collection;
import java.util.Optional;

interface SessionRepository {
    void save(Session session);

    void remove(Session session);

    Optional<Session> findOne(Object id, String deviceType);

    Optional<Session> findByToken(String token);

    Page<Session> findAll(Object filterInfo, int size, int page);

    Collection<Session> listAll(Object filterInfo);
}
```
**SessionManager**
```java
import java.util.Optional;

interface SessionManager extends SessionRepository {
    
    Session genSession(Object id, String deviceType, String sessionTokenType, Object meta, Object clientExtMeta);

    Optional<String> findSessionTokenFromClient(String tokenName);
    
    void outSession2Client(String tokenName, Session session);
}
```
