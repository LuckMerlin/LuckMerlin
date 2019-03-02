package com.merlin.media;
/**
 * 2011-2019, LuckMerlin
 * Author: LuckMerlin
 * Date: 2019/3/2 13:47
 * Description:
 */
public interface OnMediaPlayUpdate extends Callback{
    void onMediaPlayUpdated(boolean finish, int what, Media media);
}
