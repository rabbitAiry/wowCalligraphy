package com.airy.wowcalligraphy.util;

import java.util.Random;

public enum DoneComments {
    PERFECT("太棒了", new String[]{"先生的手指落在屏幕上，就犹如春天的雨打在遍布树叶的路上，一样的清新曼妙~"
            ,"我能把你比作21世纪的王羲之吗？","等到仲恺改名叫大学，我第一个推荐你去题字","细腻的字迹行云流水，有如丝袜奶茶般柔顺"}),
    GOOD("不错哦", new String[]{"温暖了四季(/▽＼)","手指挺灵活的一个小伙子~"}),
    OK("还行啦", new String[]{"你挺会写的嘛~","未来可期"}),
    BAD("勉强", new String[]{"不小心点进来了？","这个字迹就是逊啦~","能看就好"});

    private final String title;
    private final String[] messages;
    DoneComments(String title, String[] messages){
        this.title = title;
        this.messages = messages;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage(){
        Random random = new Random();
        return messages[random.nextInt(messages.length)];
    }
}
