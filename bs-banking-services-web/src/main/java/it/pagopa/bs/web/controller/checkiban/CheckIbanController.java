package it.pagopa.bs.web.controller.checkiban;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import lombok.var;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.pagopa.bs.web.mapper.DualMapper;
import reactor.core.publisher.Mono;

@RestController
@CustomLog
@RequiredArgsConstructor
@RequestMapping("${pagopa.bs.api-version-path}")
public class CheckIbanController {

    private final DualMapper dualMapper;

    @PostMapping("/validate-account-holder")
    public Mono<Void> validateAccountHolder() {
        log.info("test");
        var a = dualMapper.getLiveness();
        return Mono.empty();
    }
}
