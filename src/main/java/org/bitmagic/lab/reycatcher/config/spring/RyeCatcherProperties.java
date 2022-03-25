package org.bitmagic.lab.reycatcher.config.spring;

import lombok.Data;
import org.bitmagic.lab.reycatcher.config.CertificationSystemDefine;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

/**
 * @author yangrd
 * @date 2022/03/06
 */
@ConfigurationProperties("rye-catcher")
@Data
public class RyeCatcherProperties {

    /**
     * key: Path prefix value: info
     */
    private Set<CertificationSystemDefine> certificationSystems;


}
