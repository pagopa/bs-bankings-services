package it.pagopa.bs.common.client;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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

    public <T, R> Mono<T> post(String uri, HttpHeaders httpHeaders, R body, ParameterizedTypeReference<T> type) {
        return ((WebClient.RequestBodySpec)((WebClient.RequestBodySpec)this.webClient.post().uri(uri, new Object[0])).headers((headers) -> {
            headers.addAll(httpHeaders);
        })).body(BodyInserters.fromValue(body)).retrieve().bodyToMono(type);
    }

    public <T, R> Mono<ResponseEntity<T>> postToEntity(String uri, HttpHeaders httpHeaders, R body, ParameterizedTypeReference<T> type) {
        return ((WebClient.RequestBodySpec)((WebClient.RequestBodySpec)this.webClient.post().uri(uri, new Object[0])).headers((headers) -> {
            headers.addAll(httpHeaders);
        })).body(BodyInserters.fromValue(body)).exchangeToMono((clientResponse) -> {
            return this.elaborateResponse(clientResponse, type);
        });
    }

    protected <T> Mono<ResponseEntity<T>> elaborateResponse(ClientResponse clientResponse, ParameterizedTypeReference<T> type) {
        return clientResponse.statusCode().isError() ? this.elaborateError(clientResponse, (Function)null) : clientResponse.toEntity(type);
    }

    protected <T> Mono<ResponseEntity<T>> elaborateError(ClientResponse clientResponse, Function<ClientResponse, Mono<? extends Throwable>> onError) {
        return onError != null ? ((Mono)onError.apply(clientResponse)) : clientResponse.bodyToMono(ByteArrayResource.class).defaultIfEmpty(new ByteArrayResource("{}".getBytes())).map(ByteArrayResource::getByteArray).flatMap((bytes) -> {
            WebClientException webClientException = new WebClientResponseException(clientResponse.rawStatusCode(), clientResponse.statusCode().getReasonPhrase(), clientResponse.headers().asHttpHeaders(), bytes, StandardCharsets.UTF_8);
            return Mono.error(webClientException);
        });
    }
}
