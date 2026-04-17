package com.workshop.proxymonitor.proxy;

/**
 * Generic interface for the Proxy Pattern.
 * Every proxy (logging, caching, auth) must implement this contract.
 *
 * @param <T> the return type of the operation
 */
public interface MicroserviceProxy<T> {
    T execute(String operation, Object... params);
}