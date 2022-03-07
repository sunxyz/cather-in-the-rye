package org.bitmagic.lab.reycatcher.impl;

import org.bitmagic.lab.reycatcher.SessionToken;
import org.bitmagic.lab.reycatcher.TokenGenService;
import org.bitmagic.lab.reycatcher.utils.StringUtils;
import org.bitmagic.lab.reycatcher.utils.ValidateUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public class CompositeTokenGenService implements TokenGenService {

    public CompositeTokenGenService(List<TokenGenService> tokenGenServices) {
        this.tokenGenServiceMap = tokenGenServices.stream().collect(Collectors.toMap(tokenGenService -> StringUtils.toUnderlineCase(tokenGenService.getClass().getSimpleName().replaceAll(TokenGenService.class.getSimpleName(),"")), Function.identity()));
    }

    final Map<String,TokenGenService> tokenGenServiceMap;

    @Override
    public SessionToken genToken(Object id, String deviceType, String sessionTokenType, Object clientExtMeta) {
        TokenGenService tokenGenService = tokenGenServiceMap.get(sessionTokenType);
        ValidateUtils.notNull(tokenGenService,sessionTokenType+": There is no corresponding token generator");
        return tokenGenService.genToken(id, deviceType, sessionTokenType, clientExtMeta);
    }
}
