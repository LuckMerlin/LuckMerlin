package com.merlin.classes;

public class DefaultValue {

    public final Object javaDefaultValue(Class<?> type){
        if (null!=type){
             if (type.equals(void.class)){
               return null;
            }else if (type.equals(Integer.class)||type.equals(int.class)){
                return 0;
            }else if (type.equals(Double.class)||type.equals(double.class)){
                return (double)0;
            }else if (type.equals(Boolean.class)||type.equals(boolean.class)){
                return false;
            }else if (type.equals(Float.class)||type.equals(float.class)){
                return (float)0;
            }else if (type.equals(Long.class)||type.equals(long.class)){
                return (long)0;
            }else if (type.equals(Byte.class)||type.equals(byte.class)){
                return (byte)0;
            }else if (type.equals(Short.class)||type.equals(short.class)){
                return (short)0;
            }else if (type.equals(Character.class)||type.equals(char.class)){
                return " ".charAt(0);
            }
        }
        return null;
    }
}
