package com.kunbo.app.view.lemonbubble.interfaces;

import com.kunbo.app.view.lemonbubble.LemonBubbleInfo;
import com.kunbo.app.view.lemonbubble.LemonBubbleView;

/**
 * 柠檬泡泡控件的蒙版被触摸的回调上下文
 */

public interface LemonBubbleMaskOnTouchContext {

    void onTouch(LemonBubbleInfo bubbleInfo, LemonBubbleView bubbleView);

}
