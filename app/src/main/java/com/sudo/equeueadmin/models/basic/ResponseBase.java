package com.sudo.equeueadmin.models.basic;


public class ResponseBase<T extends PossibleError> {

    private int code;
    private T body;

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
