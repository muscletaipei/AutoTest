package com.msi.autotest;

import android.util.Log;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Locale;

public class Storage {
    private static String TAG = Storage.class.getCanonicalName();
    private static boolean Debug = false;

    @Nullable
    public static String getMD5Hash (File file) throws IOException {
        if(!file.exists()) throw new IOException("The file is not exist.");
        FileInputStream fis = null;
        DigestInputStream dis = null;
        byte[] buff = new byte[1024];
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            dis = new DigestInputStream(fis, md);

            // Read bytes from the file.
            while(dis.read(buff) != -1);
            byte[] md5Digests = md.digest();
            if(Debug) {
                Log.d(TAG, "Checksum_MD5:" + byteArray2Hex(md5Digests));
            }
            return byteArray2Hex(md5Digests);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            buff = null;
            if(fis != null) fis.close();
            if(dis != null) dis.close();
        }
        return null;
    }
    public static String byteArray2Hex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public static String getExternalMounts(String type) {
        String reg = "";
        if(Debug){
            Log.d(TAG,"Type:"+type);
        }
        if(type.equals("SDCard")){
            reg = "(?i).*vold/public:179.*(vfat).*rw.*";
        }else if(type.equals("USB_Type_A")){
            reg = "(?i).*vold/public:8.*(vfat).*rw.*";
        }

        String s = "";
        try {
            final Process process = new ProcessBuilder().command("mount").redirectErrorStream(true).start();
            process.waitFor();
            final InputStream is = process.getInputStream();
            final byte[] buffer = new byte[1024];
            while (is.read(buffer) != -1) {
                s = s + new String(buffer);
            }
            is.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        // parse output
        final String[] lines = s.split("\n");
        for (String line : lines) {
            if (!line.toLowerCase(Locale.US).contains("asec")) {
                if (line.matches(reg)) {
                    String[] parts = line.split(" ");
                    for (String part : parts) {
                        if (part.startsWith("/"))
                            if (!part.toLowerCase(Locale.US).contains("vold")) {
                                if(Debug) {
                                    Log.d(TAG, "part:" + part);
                                }
                                return part;
                            }
                    }
                }
            }
        }
        return "";
    }

    public static String readParameter(String index){
        if(Debug){
            Log.d(TAG,"index:"+index);
        }
        //System.out.println("index:"+index);
        String conf_file = getExternalMounts("SDCard") +"/";
        if(conf_file.equals("/")){
            return "SDCard not found.";
        }

        File parameter = new File(conf_file+"Parameter.txt");

        if(!parameter.exists()){
            return "Parameter.txt not found.";
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(parameter)));
            String line = "";
            while((line = br.readLine())!= null){
                if(line.contains(index)){
                    //System.out.println("index:OK");
                    if(Debug){
                        Log.d(TAG,"index:OK");
                    }
                    String[] value = line.trim().split(":");
                    return value[1];
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "fail";
    }




}
