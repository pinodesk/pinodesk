package pospino.desktop.service.api;

import javax.annotation.PostConstruct;

import com.github.sitture.unirestcurl.CurlInterceptor;

import kong.unirest.core.Unirest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseApiService {

    @PostConstruct
    protected void init() {
        Unirest.config().interceptor(new CurlInterceptor(s -> log.debug("{}", s)));
    }

}
