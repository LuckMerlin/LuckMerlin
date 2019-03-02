package com.merlin.array;
/**
 * 2011-2019, LuckMerlin
 * Author: LuckMerlin
 * Date: 2019/3/1 17:52
 * Description:
 */
import com.merlin.debug.Debug;

import java.lang.reflect.Array;

public class Joint {

    public <T> T[] joint(Class<T> cls,T[] src,T...appends){
        if (null!=cls){
            final int srcLength=null!=src?src.length:0;
            final int appendLength=null!=appends?appends.length:0;
            if (srcLength<=0||appendLength<=0){
                return srcLength<=0?appends:src;
            }
            final Object array= Array.newInstance(cls,srcLength+appendLength);
            System.arraycopy(src,0,array,0,srcLength);
            System.arraycopy(appends,0,array,srcLength,appendLength);
            return (T[])array;
        }
        Debug.W(getClass(),"Can't joint array.cls="+cls+" src="+src+" appends="+appends);
        return null;
    }

}
