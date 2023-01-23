package it.pagopa.bs.common.client;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
public class RestClient {

    private final WebClient webClient;

    public RestClient() {
        this.webClient = WebClient.create();
    }

    public <T, R> Mono<T> post(String uri, HttpHeaders httpHeaders, R body, Class<T> type) {
        return ((this.webClient.post().uri(uri, new Object[0])).headers((headers) -> {
            headers.addAll(httpHeaders);
        })).body(BodyInserters.fromValue(body)).retrieve().bodyToMono(type);
    }
}
