package com.kunbo.app.view.lemonhello.adapter;

import com.kunbo.app.view.lemonhello.LemonHelloAction;
import com.kunbo.app.view.lemonhello.LemonHelloInfo;
import com.kunbo.app.view.lemonhello.LemonHelloView;
import com.kunbo.app.view.lemonhello.interfaces.LemonHelloEventDelegate;

/**
 * LemonHello 事件代理适配器
 */

public abstract class LemonHelloEventDelegateAdapter implements LemonHelloEventDelegate {

    @Override
    public void onActionDispatch(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {

    }

    @Override
    public void onMaskTouch(LemonHelloView helloView, LemonHelloInfo helloInfo) {

    }
    
}
