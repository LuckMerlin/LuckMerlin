package com.merlin.media;
/**
 * 2011-2019, LuckMerlin
 * Author: LuckMerlin
 * Date: 2019/3/2 13:47
 * Description:
 */
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;

import com.merlin.debug.Debug;

import java.io.File;


public class MediaPlayer {
    private float mLeftVolume=1,mRightVolume=1;
    private android.media.MediaPlayer mMediaPlayer;
    private Playing mPlaying;
    private final Handler mHandler;

    public MediaPlayer(Looper looper){
        mHandler=null!=looper?new Handler(looper):null;
    }

    public MediaPlayer(){
       this(null);
    }

    public final boolean play(Media media, String debug, Callback...callbacks){
         return play(media,0,debug,callbacks);
    }

    private final boolean play(final Media media, final int tryCount, final String debug, final Callback...callbacks){
          final int maxTry=3;
          final String path=null!=media?media.getPath():null;
          if (null==path||path.isEmpty()){
              Debug.W(getClass(),"Can't play media "+(null!=debug?debug:".")+" path="+path);
              notifyFinish(OnMediaPlayFinish.FINISH_INVALID,media,callbacks);
              return false;
          }
         if (path.startsWith(File.separator)){
             final File file=new File(path);
             if (!file.exists()||file.length()<=0){
                 Debug.W(getClass(),"Can't play media "+(null!=debug?debug:".")+" existed="+file.exists()+" length="+file.length()+" path="+path);
                 notifyFinish(OnMediaPlayFinish.FINISH_INVALID,media,callbacks);
                 return false;
             }
         }
         final Media play=getPlay();
         if (null!=play&&tryCount<=0){//If exist playing
             stop(play,"Before start play new media.path="+path);
         }
         final android.media.MediaPlayer player=createPlayer(false,"Before play media.");
         if (null==player){
             Debug.W(getClass(),"Can't play media "+(null!=debug?debug:".")+" player="+player+" path="+path);
             notifyFinish(OnMediaPlayFinish.FINISH_ERROR,media,callbacks);
             return false;
         }
        player.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener(){
            @Override
            public void onPrepared(android.media.MediaPlayer mp) {
                notifyPlayUpdate(false,Callback.MEDIA_LOADED,media,callbacks);
                final Playing playing=mPlaying;
                final Media playingMedia=null!=playing?playing.mMedia:null;
                if (null!=playingMedia&&playingMedia==media){
                    if (playing.isLoading()){
                        final int position=media.getPosition();
                        if (position>0){
                            notifyPlayUpdate(false,Callback.MEDIA_SEEK,media,callbacks);
                            playingMedia.setPosition(0);//Reset
                            mp.seekTo(position);
                        }
                        Debug.D(getClass(),"Play media "+(null!=debug?debug:".")+" position="+position+" path="+(null!=playingMedia?playingMedia.getPath():null));
                        playing.mState= Playing.STATE_PLAYING;
                        notifyPlayUpdate(false,Callback.MEDIA_PLAYING,media,callbacks);
                        mp.start();
                    }
                }
            }
        });
        final long loadTime=System.currentTimeMillis();
        player.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(android.media.MediaPlayer mp) {
                final Playing playing=mPlaying;
                final Media playingMedia=null!=playing?playing.mMedia:null;
                if (null!=playingMedia&&(playingMedia==media||playingMedia.equals(media))){
                    int duration=getDuration(-1);
                    long time=System.currentTimeMillis()-loadTime;
                    final int hourSecs=7200000;
                    if (duration<0||(time*2<duration&&duration<hourSecs)) {//Check if play normal
                        notifyPlayUpdate(false,Callback.MEDIA_ERROR,media,callbacks);
                        if (tryCount<maxTry){
                            Debug.D(getClass(), "Media complete not normal,try replay. path=" + path + " time=" + time + " duration=" + duration);
                            notifyPlayUpdate(false,Callback.MEDIA_RETRY,media,callbacks);
                            play(media,tryCount+1,debug,callbacks);
                            return;
                        }else{
                            Debug.W(getClass(),"Give up play media.tryCount="+tryCount+" duration="+duration+" time="+time+" path="+path);
                            notifyPlayUpdate(false,Callback.MEDIA_GIVE_UP,media,callbacks);
                        }
                    }
                    playing.mState= Playing.STATE_COMPLETE;
                    notifyFinish(OnMediaPlayFinish.FINISH_COMPLETE,media,callbacks);
                }
            }
        });
        try{
            mPlaying=new Playing(media,callbacks);
            mPlaying.mState= Playing.STATE_LOADING;
            player.reset();
            Debug.D(getClass(),"Loading media "+(null!=debug?debug:".")+" path="+path);
            notifyPlayUpdate(false,Callback.MEDIA_LOADING,media,callbacks);
            player.setDataSource(path);
            player.prepareAsync();
            return true;//Interrupt later codes
        }catch (Exception e){
            notifyPlayUpdate(false,Callback.MEDIA_ERROR,media,callbacks);
            if (tryCount<maxTry){
                Debug.E(getClass(), "Media start failed,try replay. e="+e+" path=" + path,e);
                createPlayer(true,"Before retry.");
                notifyPlayUpdate(false,Callback.MEDIA_RETRY,media,callbacks);
                return play(media,tryCount+1,debug,callbacks);
            }else{
                Debug.E(getClass(),"Give up play media.tryCount="+tryCount+" e="+e+" path="+path,e);
                notifyPlayUpdate(false,Callback.MEDIA_GIVE_UP,media,callbacks);
            }
            notifyFinish(OnMediaPlayFinish.FINISH_ERROR,media,callbacks);
            e.printStackTrace();
            return false;
        }
    }

    public boolean isPlaying(Object obj){
         if (isPlay(obj)){
            final android.media.MediaPlayer player=mMediaPlayer;
            if (null!=player){
                try {
                    return player.isPlaying();
                }catch (Exception e){
                    Debug.E(getClass(),"Can't judge is playing media.e="+e+" mPlaying="+mPlaying);
                    e.printStackTrace();
                }
               return false;
            }
            Debug.W(getClass(),"Can't judge is playing media.player="+player+" mPlaying="+mPlaying);
            return false;
         }
        return false;
    }

    public boolean isPlay(Object obj){
        if (null!=obj){
            final Media playing=null!=mPlaying?mPlaying.mMedia:null;
            if (null!=playing){
                if (obj instanceof String){
                    String playingPath=playing.getPath();
                    return null!=playingPath&&playingPath.equals(obj);
                }
                return playing.equals(obj);
            }
            return false;
        }
        return false;
    }

    public boolean seekTo(float percent,String debug){
        int duration=getDuration(-1);
        int position=(int)(duration<0?-1:duration*((percent<0||percent>1)?0:percent));
        return position>=0&&seekTo(position,debug);
    }

    public boolean seekTo(int position,String debug) {
        final Playing playing=mPlaying;
        if (null!=playing&&playing.isPlaying()){
            final String path=null!=playing.mMedia?playing.mMedia.getPath():null;
            final android.media.MediaPlayer player=mMediaPlayer;
            if (null!=player){
                try {
                    Debug.D(getClass(),"Seek media to "+position+" "+(null!=debug?debug:".")+" path="+path);
                    notifyPlayUpdate(false,Callback.MEDIA_SEEK,playing.mMedia,playing.mCallbacks);
                    player.seekTo(position);
                    return true;
                }catch (Exception e){
                    Debug.E(getClass(),"Can't seek playing media to "+position+" "+(null!=debug?debug:".")+" e="+e+" path="+path,e);
                }
            }
        }
        return false;
    }

    public int getDuration(int def){
        final Playing playing=mPlaying;
        if (null!=playing&&(playing.isPaused()||playing.isPlaying())){
            final android.media.MediaPlayer player=mMediaPlayer;
            try{
                return null!=player?player.getDuration():def;
            }catch (Exception e){
                Debug.E(getClass(),"Can't get playing media duration.e="+e+" playing="+playing,e);
                e.printStackTrace();
            }
        }
        return def;
    }

    public Media getPlaying(Object ...objects){
         Media play=getPlay(objects);
         return isPlaying(play)?play:null;
    }

    public Media getPlay(Object ...objects) {
        final Media play=null!=mPlaying?mPlaying.mMedia:null;
        if (null!=play){
            if (isArrayEmpty(objects)){
                return play;
            }
            final int count=null!=objects?objects.length:0;
            final String playPath=play.getPath();
            Object object;
            for (int i = 0; i < count; i++) {
                if (null!=(object=objects[i])&&((object instanceof String&&playPath.equals(object))||
                        (play.equals(object)))){
                     return play;
                }
            }
        }
        return null;
    }

    public boolean pause(String debug,Object ...objects){
        final int count=null!=objects?objects.length:0;
        if (count>0){
            Object obj;
            for (int i = 0; i < count; i++) {
                if (null!=(obj=objects[i])&&pause(obj,debug)){
                    return true;
                }
            }
            return false;
        }
        return pause(null,debug);
    }

    private boolean pause(Object obj,String debug){
        final Playing playing=(null==obj||isPlaying(obj))?mPlaying:null;
        if (null!=playing&&!playing.isPaused()){
            final android.media.MediaPlayer player=mMediaPlayer;
            if (null!=player){
                  try {
                      Debug.D(getClass(),"Pause media "+(null!=debug?debug:".")+" playing="+playing.mMedia);
                      boolean loading=playing.isLoading();
                      playing.mState= Playing.STATE_PAUSED;//Set pause flag
                      if (!loading){
                          notifyPlayUpdate(false,Callback.MEDIA_PAUSE,playing.mMedia,playing.mCallbacks);
                          player.pause();
                      }
                      return true;
                  }catch (Exception e){
                      Debug.E(getClass(),"Can't pause media "+(null!=debug?debug:".")+" e="+e+" playing="+playing,e);
                      e.printStackTrace();
                  }
                  return false;
            }
            Debug.E(getClass(),"Can't pause media "+(null!=debug?debug:".")+" player="+player+" playing="+playing);
            return false;
        }
        return false;
    }

    public boolean isPaused(Object ...objects){
        final int count=null!=objects?objects.length:0;
        final Playing playing=mPlaying;
        if ((count==1&&null!=objects[0])||count>1){
            Object obj;
            final Media playingMedia=null!=playing?playing.mMedia:null;
            if (null!=playing&&playing.isPaused()){
                for (int i = 0; i < count; i++) {
                    if (null!=(obj=objects[i])){
                        if (obj instanceof String){
                            String playingPath=playingMedia.getPath();
                            return null!=playingPath&&playingPath.equals(obj);
                        }else if(obj.equals(playingMedia)){
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        return null!=playing&&playing.isPaused();
    }

    public boolean replay(String debug,Object ...objects){
        final int count=null!=objects?objects.length:0;
        if (count>0){
            Object obj;
            for (int i = 0; i < count; i++) {
                if (null!=(obj=objects[i])&&replay(obj,debug)){
                    return true;
                }
            }
            return false;
        }
        return replay(null,debug);
    }

    private boolean replay(Object obj,String debug) {
        final Playing playing=mPlaying;
        if (isPaused(obj)){
            final android.media.MediaPlayer player=mMediaPlayer;
            if (null!=player){
                Debug.D(getClass(),"Replay media "+(null!=debug?debug:".")+" mPlaying="+mPlaying);
                try {
                    notifyPlayUpdate(false,Callback.MEDIA_REPLAY,playing.mMedia,playing.mCallbacks);
                    player.start();
                    playing.mState= Playing.STATE_PLAYING;
                    return true;
                }catch (Exception e){
                    Debug.E(getClass(),"Can't replay media "+(null!=debug?debug:".")+" e="+e+" mPlaying="+mPlaying,e);
                    e.printStackTrace();
                }
                return false;
            }
            return false;
        }
       return false;
    }

    public boolean stop(String debug,Object ...objects){
        final int count=null!=objects?objects.length:0;
        if (count>0){
            Object obj;
            for (int i = 0; i < count; i++) {
                if (null!=(obj=objects[i])&&stop(obj,debug)){
                    return true;
                }
            }
            return false;
        }
        return stop(null,debug);
    }

    private boolean stop(Object obj,String debug) {
        final Playing play=(null==obj||isPlay(obj))?mPlaying:null;
        if (null!=play){
            final String path=null!=play.mMedia?play.mMedia.getPath():null;
            final android.media.MediaPlayer player=mMediaPlayer;
            if (null!=player){
                try {
                    Debug.D(getClass(),"Stop media "+(null!=debug?debug:".")+" path="+path);
                    notifyPlayUpdate(false,Callback.MEDIA_STOP,play.mMedia,play.mCallbacks);
                    player.stop();
                    notifyFinish(OnMediaPlayFinish.FINISH_USER,play.mMedia,play.mCallbacks);
                    return true;
                }catch (Exception e){
                    Debug.E(getClass(),"Can't stop media "+(null!=debug?debug:".")+" e="+e+" path="+path,e);
                    e.printStackTrace();
                }
                return false;
            }
            Debug.E(getClass(),"Can't stop media "+(null!=debug?debug:".")+" player="+player+" path="+path);
            return false;
        }
        return false;
    }

    public float getLeftVolume() {
        return mLeftVolume;
    }

    public float getRightVolume() {
        return mRightVolume;
    }

    public boolean mute(){
        return !isMute()&&setVolume(0,0);
    }

    public boolean isMute(){
        return mLeftVolume==mRightVolume&&mLeftVolume==0;
    }

    public boolean setVolume(float leftVolume, float rightVolume){
        mLeftVolume=leftVolume<0?1:leftVolume;
        mRightVolume=rightVolume<0?1:rightVolume;
        if (null!=mMediaPlayer){
            final Playing playing=mPlaying;
            if (null!=playing){
                notifyPlayUpdate(false,Callback.MEDIA_VOLUME_CHANGED,playing.mMedia,playing.mCallbacks);
                if (mLeftVolume==0&&mLeftVolume==mRightVolume){
                    notifyPlayUpdate(false,Callback.MEDIA_MUTED,playing.mMedia,playing.mCallbacks);
                }
            }
            mMediaPlayer.setVolume(mLeftVolume,mRightVolume);
            return true;
        }
        return false;
    }

    private android.media.MediaPlayer createPlayer(boolean force, String debug){
        if (force||null==mMediaPlayer){
            if (null!=mMediaPlayer){
                mMediaPlayer.release();
            }
            mMediaPlayer=new android.media.MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            setVolume(mLeftVolume,mRightVolume);
        }
        return mMediaPlayer;
    }


    private void notifyFinish(final int finish, final Media media, Callback...callbacks){
         final Media playing=null!=mPlaying?mPlaying.mMedia:null;
         if (null!=playing&&null!=media&&(playing==media||playing.equals(media))){
             mPlaying=null;
         }
         final Handler handler=mHandler;
         if ((null!=callbacks?callbacks.length:0)>0){
             for (Callback f: callbacks) {
                if (null!=f){
                    if (f instanceof OnMediaPlayFinish){
                         if (null!=handler){
                             final Callback callback=f;
                             handler.post(new Runnable() {
                                 @Override
                                 public void run() {
                                     ((OnMediaPlayFinish)callback).onMediaPlayFinish(finish,media);
                                 }
                             });
                         }else{
                             ((OnMediaPlayFinish)f).onMediaPlayFinish(finish,media);
                         }
                    }
                }
             }
         }
         notifyPlayUpdate(true,finish,media,callbacks);
    }

    private void notifyPlayUpdate(final boolean finish, final int what, final Media media, Callback...callbacks){
        final Handler handler=mHandler;
        if ((null!=callbacks?callbacks.length:0)>0){
            for (Callback f: callbacks) {
                if (null!=f){
                    if (f instanceof OnMediaPlayUpdate){
                        if (null!=handler){
                            final Callback callback=f;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ((OnMediaPlayUpdate)callback).onMediaPlayUpdated(finish,what,media);
                                }
                            });
                        }else{
                            ((OnMediaPlayUpdate)f).onMediaPlayUpdated(finish,what,media);
                        }
                    }
                }
            }
        }
    }

    private boolean isArrayEmpty(Object ...objects){
        return null==objects||(objects.length<1||(objects.length==1&&null==objects[0]));
    }

    private static class Playing{
        private final Callback[] mCallbacks;
        private final Media mMedia;
        final static int STATE_IDLE=342;
        final static int STATE_PAUSED=343;
        final static int STATE_LOADING=344;
        final static int STATE_PLAYING =345;
        final static int STATE_COMPLETE =346;
        private int mState=STATE_IDLE;

        private Playing(Media media, Callback...callbacks){
            mCallbacks=callbacks;
            mMedia=media;
        }

        public boolean isPaused(){
            return mState==STATE_PAUSED;
        }

        public boolean isPlaying(){
            return mState==STATE_PLAYING;
        }

        public boolean isLoading() {
            return mState==STATE_LOADING;
        }

        @Override
        public String toString() {
            return ""+mMedia;
        }
    }
}
