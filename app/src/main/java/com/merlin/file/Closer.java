package com.merlin.file;

import java.io.Closeable;
import java.io.IOException;

public class Closer {

    public interface  CloseExceptionHolder{
        void onCloseException(boolean succeed, Closeable closeable, Exception e);
    }

    public boolean close(Closeable closeable){
        return  close(closeable,null);
    }

    public int close(Closeable ...closeables){
         return  close(null,closeables);
    }

    public int close(CloseExceptionHolder holder,Closeable ...closeables){
           int success=0;
           if (null!=closeables){
               for (Closeable f:closeables) {
                   success=close(f,holder)?++success:success;
               }
           }
           return  success;
    }

    public boolean close(Closeable closeable,CloseExceptionHolder holder){
        IOException exception=null;
        boolean succeed=false;
        try {
            succeed= closeIO(closeable);
        }catch (IOException e){
            exception=e;
        }
        if (null!=holder){
            holder.onCloseException(succeed&&null==exception,closeable,exception);
        }
        return  succeed;
    }

    private boolean closeIO(Closeable closeable) throws IOException{
        if (null!=closeable){
            closeable.close();
            return  true;
        }
        return  false;
    }
}
