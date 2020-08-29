package com.kunbo.app.view.lemonhello.interfaces;

import com.kunbo.app.view.lemonhello.LemonHelloAction;
import com.kunbo.app.view.lemonhello.LemonHelloInfo;
import com.kunbo.app.view.lemonhello.LemonHelloView;

/**
 * LemonHello - 事件回调代理
 */

public interface LemonHelloActionDelegate {

    void onClick(
            LemonHelloView helloView,
            LemonHelloInfo helloInfo,
            LemonHelloAction helloAction
    );

}
