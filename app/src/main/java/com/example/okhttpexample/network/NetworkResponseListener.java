package com.example.okhttpexample.network;

import java.util.Map;

public interface NetworkResponseListener {
    /**
     * called when the server response was not 2xx or when an exception was thrown in the process
     * @param throwable - contains the exception. in case of server error (4xx, 5xx) this is null
     */
    void onFailure(Throwable throwable);

    /**
     * contains the server response
     * @param resMap
     */
    void onSuccess(Map<String, Object> resMap);
}
