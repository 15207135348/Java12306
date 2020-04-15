package com.yy.util;

import java.io.*;


public class BlobUtil {

    public static byte[] writeObject(Object o)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(baos);
            out.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return baos.toByteArray();
    }

    public static Object readObject(byte[] bytes)
    {
        ByteArrayInputStream bais;
        ObjectInputStream in = null;
        try{
            bais = new ByteArrayInputStream(bytes);
            in = new ObjectInputStream(bais);
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally{
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
