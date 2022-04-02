package org.bitmagic.lab.reycatcher.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author yangrd
 */
@Getter
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class Configuration {

    private final Environment environment;

    private Collection<CertificationSystemDefine> certificationSystemDefines = new HashSet<>();

    public void addCertificationSystemDefine(CertificationSystemDefine certificationSystemDefine) {
        certificationSystemDefines.add(certificationSystemDefine);
    }

}
