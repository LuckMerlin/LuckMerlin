package com.merlin.media;
/**
 * 2011-2019, LuckMerlin
 * Author: LuckMerlin
 * Date: 2019/3/2 13:47
 * Description:
 */
public interface Callback {
    int FINISH_INVALID=342342;
    int FINISH_ERROR=3242352;
    int FINISH_USER=25434523;
    int FINISH_COMPLETE=3532623;
    //
    int MEDIA_LOADING=6666;
    int MEDIA_LOADED=66667;
    int MEDIA_SEEK=6668;
    int MEDIA_PLAYING=6669;
    int MEDIA_RETRY=6670;
    int MEDIA_ERROR=6671;
    int MEDIA_GIVE_UP=6672;
    int MEDIA_PAUSE=6677;
    int MEDIA_STOP=6678;
    int MEDIA_REPLAY=6679;
    int MEDIA_VOLUME_CHANGED=6680;
    int MEDIA_MUTED=6681;
}
