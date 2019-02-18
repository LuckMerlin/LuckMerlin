package com.merlin.file;

import android.os.Handler;

import com.merlin.async.AsyncTask;
import com.merlin.async.AsyncTaskRunner;
import com.merlin.debug.Debug;
import com.merlin.thread.UIThreadPost;

import java.io.File;

public class UnzipTask  extends AsyncTask {
    private final String mZipPath;
    private final String mTargetFolder;
    private final Zip mZip=new Zip();
    public final static int UNZIP_FAILED_FILE_INVALID=50000;


    public UnzipTask(String zipPath,String targetFolder){
        mZipPath=zipPath;
        mTargetFolder=targetFolder;
    }

    @Override
    public Finish onTaskRun(AsyncTaskRunner runner) {
        final String zipPath=mZipPath;
        final String targetFolder=mTargetFolder;
        if (null==zipPath||zipPath.isEmpty()){
            Debug.W(getClass(),"Can't unzip in task.zipPath="+zipPath);
            return generateFinish(false,UNZIP_FAILED_FILE_INVALID);
        }
        final File zipFile=new File(zipPath);
        final boolean exist=zipFile.exists();
        final boolean canRead=zipFile.canRead();
        if (!exist||!canRead){
            Debug.W(getClass(),"Can't unzip in task.exist="+exist+" canRead="+canRead+" zipPath="+zipPath);
            return generateFinish(false,UNZIP_FAILED_FILE_INVALID);
        }
        boolean succeed=mZip.unzip(zipFile,new File(targetFolder),null);
        if (!succeed){
            Debug.W(getClass(),"Unzip task finish.succeed="+succeed);
        }
        return generateFinish(succeed,succeed?AsyncTaskRunner.Callback.TASK_FINISH_SUCCEED:AsyncTaskRunner.Callback.TASK_FINISH_FAILED_UNKNOW);
    }
}