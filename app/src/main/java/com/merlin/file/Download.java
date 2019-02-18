package com.merlin.file;

import android.os.Handler;

import com.merlin.async.AsyncTask;
import com.merlin.async.AsyncTaskRunner;
import com.merlin.debug.Debug;
import com.merlin.string.StringEquals;
import com.merlin.thread.UIThreadPost;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Download extends AsyncTask {
//    private final String mDebug;
    private final URL mSrc;
    private final String mTarget;
    private String mTempFolder;//Temp enable,default disabled
    private boolean mBreakPoint=false;//Break-point download enable,default disabled
    private boolean mEnableProgressLog=false;
    private boolean mEnableLog=false;

    public interface Callback{
        int DOWNLOAD_FAILED_INVALID=14;
        int DOWNLOAD_FAILED_NOT_CREATE_TARGET=16;
        int DOWNLOAD_FAILED_ERROR=20;
        int DOWNLOAD_SUCCEED=22;
        int DOWNLOAD_FAILED_NOT_MATCHED=23;
        int DOWNLOAD_FAILED_URL_FAILED=24;
        int DOWNLOAD_FAILED_RESPONSE_NOT_FOUND=25;
        int DOWNLOAD_FAILED_RESPONSE_ERROR=26;
        int DOWNLOAD_FAILED_NONE_SPACE=27;
        int DOWNLOAD_FAILED_INTERRUPTED=28;
    }

    public static class  DefaultDownloadRetry extends DefaultRetry{

        public DefaultDownloadRetry(){
            this(5,3*1000,0);
        }

        public DefaultDownloadRetry(int maxTry,int delay,float delayFactor ){
           super(maxTry,delay,delayFactor);
        }

        @Override
        public Integer onResolveRetryDelay(Finish finish, int tried, AsyncTask task) {
            Integer  retryDelay=super.onResolveRetryDelay(finish, tried, task);
            if (null!=retryDelay&&(null!=finish&&finish.getWhat()==Callback.DOWNLOAD_FAILED_RESPONSE_NOT_FOUND)){//Not need try again for 404 NOT found src
                return null;//Return null to sure not need try again
            }
            return retryDelay;
        }
    }

    public Download(String src, String target, AsyncTaskRunner.Callback...callbacks){
        this(null,src,target,null,callbacks);
    }

    public Download(String name, String src, String target, AsyncTaskRunner.Callback...callbacks){
        this(name,src,target,null,callbacks);
    }


    public Download(String name, String src, String target, Retry retry, AsyncTaskRunner.Callback...callbacks){
        this(name,src,target,retry,null,callbacks);
    }


    public Download(String name, String src, String target, Retry retry, UiThreadNotifyResolver uiNotifyResolver, AsyncTaskRunner.Callback...callbacks){
        super(name,retry,uiNotifyResolver,callbacks);
        mSrc=null!=src?createURL(src):null;
        mTarget=target;
    }

    public final  boolean enableProgressLog(boolean enable){
        if (mEnableProgressLog!=enable){
            mEnableProgressLog=enable;
            return true;
        }
        return false;
    }

    public final boolean enableLog(boolean enable) {
        if (mEnableLog!=enable){
            mEnableLog=enable;
            return true;
        }
        return false;
    }

    public final boolean isEnableLog() {
        return mEnableLog;
    }

    public final  boolean isProgressLogEnable(){
        return mEnableProgressLog;
    }

    @Override
    public Finish onTaskRun(AsyncTaskRunner runner) {
        final URL url=mSrc;
        final String targetPath=mTarget;
        if (null==url||null==targetPath||targetPath.isEmpty()){
            Debug.W(getClass(),"Can't download task.url="+url+" targetPath="+targetPath);
            return generateFinish(false, Callback.DOWNLOAD_FAILED_INVALID);//Interrupt later codes
        }
//        if (isDownloading(download)){//If downloading
//            return generateFinish(false,Callback.DOWNLOAD_FAILED_ALREADY_EXIST);
//        }
        final File target=new MakeFile().makeFile(targetPath);
        if (null==target){
            Debug.W(getClass(),"Can't download file,Create target failed.targetPath="+targetPath);
            return generateFinish(false, Callback.DOWNLOAD_FAILED_NOT_CREATE_TARGET);
        }
        if (!target.exists()||!target.canWrite()){
            Debug.W(getClass(),"Can't do download file.targetExist="+target.exists()+" targetCanWrite="+target.canWrite()+" url="+url+ "target="+target);
            return generateFinish(false, Callback.DOWNLOAD_FAILED_INVALID);//Interrupt later codes
        }
        String tempFolder=mTempFolder;
        final File temp=null!=tempFolder?new File(tempFolder,target.getName()):null;
        if (null!=tempFolder&&null==temp){
            Debug.W(getClass(),"Can't use temp folder to download file.tempFolder="+tempFolder+" target="+target);
        }
//        final boolean printProgressLog=download.isProgressLog();
        InputStream is=null;
        HttpURLConnection conn=null;
        final boolean enableBreakPoint=mBreakPoint;
        final boolean printProgressLog=mEnableProgressLog;
        int responseCode=-1;
        try{
            if (mEnableLog) {
                Debug.D(getClass(), "Open download file connection. url=" + url);
            }
            conn = (HttpURLConnection) url.openConnection();
            // notifyUpdate(Callback.DOWNLOAD_START,download);
            if (null!=conn){
                if (mEnableLog) {
                    Debug.D(getClass(), "Connect download file url.url=" + url);
                }
                conn.setConnectTimeout(5*1000);
                conn.setDoInput(true);
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setUseCaches(false);
                conn.setRequestProperty("Accept-Encoding", "identity");
                conn.connect();
                responseCode= conn.getResponseCode();
//                            notifyUpdate(false,Callback.DOWNLOAD_RUNNING,download);
                if (mEnableLog) {
                    Debug.D(getClass(), "Responsed.Connect download file.responseCode=" + responseCode + " url=" + url);
                }
                if(responseCode==HttpURLConnection.HTTP_OK){
                    is = conn.getInputStream();
                    if (null!=is){
                        final long length=conn.getContentLength();
                        //Check if exist enough space to download
                        //if ()
                        final long currLength=target.length();
                        if (mEnableLog) {
                            Debug.D(getClass(), "Downloading file.length=" + length + "  url=" + url + " target=" + target);
                        }
                        RandomAccessFile raf = new RandomAccessFile(target.getAbsolutePath(),"rw");
                        final byte[] buffer=new byte[5*1024*1024];
                        int count;
                        long readed=0;
                        float lastProgress=0,progress;
//                        long offset=0;//Reset offset 0 as default
                        if(currLength>0&&enableBreakPoint){//If need resolve break-point download
                            long offset=currLength;
                            Debug.D(getClass(),"DownloadTemp start from break-point.offset="+offset+" url="+url+" target="+target);
                            is.skip(offset);
                            raf.seek(offset);
                        }
                        while(0<(count=is.read(buffer))){
                            readed+=count;
                            progress=(length!=0?(readed*1.0f/length):-1);
                            if (progress-lastProgress>0.1||0==lastProgress){
                                lastProgress=progress;
                                if (printProgressLog){
                                    Debug.D(getClass(),"Downloading.Progress="+progress+" file="+target);
                                }
                            }
                            raf.write(buffer,0,count);
                            // notifyUpdate(count,readed,length,progress,download);
                        }
                        try {
                            raf.getFD().sync();
                        } catch (Exception e){
                            Debug.E(getClass(), "File downloader sync file error.e="+e+" url="+url, e);
                        }
                        long targetLength=target.length();
                        if (length!=targetLength){//Check if download succeed
                            Debug.W(getClass(),"Download failed,file length not after download finish.length="+length+" target="+targetLength);
                            //notifyUpdate(false,Callback.DOWNLOAD_ERROR,download);
                            return generateFinish(false, Callback.DOWNLOAD_FAILED_NOT_MATCHED);//Interrupt later codes
                        }
                        Debug.D(getClass(),"Downloaded file.size="+(null!=target?target.length():-1)+" target="+target+" url="+url);
                        return generateFinish(true, Callback.DOWNLOAD_SUCCEED);//Return to interrupt later codes
                    }
                    Debug.W(getClass(),"Failed download file url.None input stream exist.="+responseCode+" conn="+conn+" url="+url);
                    return generateFinish(false, Callback.DOWNLOAD_FAILED_RESPONSE_ERROR);//Interrupt later codes
                }
                Debug.W(getClass(),"Failed download file url.responseCode="+responseCode+" conn="+conn+" url="+url);
                if (responseCode==HttpURLConnection.HTTP_NOT_FOUND){
                    return generateFinish(false, Callback.DOWNLOAD_FAILED_RESPONSE_NOT_FOUND);//Interrupt later codes
                }
                return generateFinish(false, Callback.DOWNLOAD_FAILED_RESPONSE_ERROR);//Interrupt later codes
            }
            Debug.E(getClass(),"Failed download file url.responseCode="+responseCode+" conn="+conn+" url="+url);
            //notifyUpdate(true,Callback.DOWNLOAD_END,download);
            return generateFinish(false, Callback.DOWNLOAD_FAILED_URL_FAILED);//Return to interrupt later codes
        }catch (Exception e){
            if (e instanceof InterruptedIOException){
                Debug.E(getClass(),"Now cancel failed download while interrupted file. url="+url);
                return generateFinish(false, Callback.DOWNLOAD_FAILED_INTERRUPTED);
            }
            Debug.E(getClass(),"Failed download file.e="+e+" url="+url,e);
            e.printStackTrace();
            return generateFinish(false, Callback.DOWNLOAD_FAILED_ERROR);
        }finally {
            new Closer().close(is);
            if (null!=conn){
                conn.disconnect();
            }
        }


    }

    public final boolean isSrcAndTargetEqueals(Download task){
        if (null!=task){
            String src=task.getSrcPath();
            String target=task.getTarget();
            StringEquals equals=new StringEquals();
            return equals.equal(src,getSrcPath(),false)&&equals.equal(target,mTarget,false);
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)){
            return true;
        }
        if (null!=obj&&obj instanceof Download){
            Download task=(Download)obj;//If src and target same,we treat task as same
            return isSrcAndTargetEqueals(task);
        }
        return false;
    }

    private  URL createURL(String path){
        try {
            return null!=path?new URL(path):null;
        } catch (MalformedURLException e) {
            Debug.E(Download.class,"Can't create path url.e="+e+" path="+path,e);
            e.printStackTrace();
        }
        return null;
    }

    public String  getTempFolder() {
        return mTempFolder;
    }

    public final String getTarget() {
        return mTarget;
    }

    public final URL getSrc() {
        return mSrc;
    }

    public final String getSrcPath(){
        URL url=getSrc();
        return null!=url?url.getPath():null;
    }

    public static final boolean isRemoteAvailable(String url){
        try {
            return null!=url&&!url.isEmpty()?isRemoteAvailable(new URL(url)):false;
        } catch (MalformedURLException e) {
        }
        return false;
    }

    public static final boolean isRemoteAvailable(URL url){
        if (null!=url){
            HttpURLConnection connection=null;
            try {
                 connection = (HttpURLConnection) url.openConnection();
                 if (null!=connection){
                     connection.connect();
                     return  HttpURLConnection.HTTP_NOT_FOUND!=connection.getResponseCode();
                 }
                return false;
            } catch (IOException e) {
                //Do nothing
            }finally {
                if (null!=connection){
                    connection.disconnect();
                }
            }
        }
        return false;
    }
}
