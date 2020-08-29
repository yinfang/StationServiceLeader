package com.kunbo.app.net.response;


import com.kunbo.app.net.bean.Result;

public interface OnSuccessAndFaultListener {
    void onSuccess(Result result, String method);

    void onFault(Result result, String method);
}

