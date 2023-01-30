package it.pagopa.bs.web.service.crypto;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.pgpainless.sop.SOPImpl;
import org.springframework.stereotype.Service;

import io.github.cdimascio.dotenv.Dotenv;
import it.pagopa.bs.common.exception.CryptoException;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import sop.SOP;

@Service
@RequiredArgsConstructor
@CustomLog
public class PGPCryptoService {

    private String pagopaPgpPublicKey;

    @PostConstruct
    public void initKeys() {
        final Dotenv dotenv = Dotenv.load();
        pagopaPgpPublicKey = dotenv.get("PGP_PUBLIC_KEY");
    }

    public String encryptFieldPGP(String field) {
        return this.makePgp(field);
    }

    public String decryptFieldPGP(String field) {
        return null; // TODO: verify
    }

    private String makePgp(String data) {
        try {
            if(StringUtils.isEmpty(data)) {
                return data;
            }

            final String fixedPublicKey = pagopaPgpPublicKey.replace("\\n", "\n");

            final SOP sop = new SOPImpl();

            byte[] ciphertext = sop.encrypt()
                .withCert(fixedPublicKey.getBytes())
                .plaintext(data.getBytes())
                .getBytes();

            return new String(ciphertext);
        } catch(Throwable e) {
            log.error("Failed to encrypt PGP");
            throw new CryptoException(e.getMessage());
        }
    }
}
