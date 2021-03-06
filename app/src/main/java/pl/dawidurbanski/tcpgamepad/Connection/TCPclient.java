package pl.dawidurbanski.tcpgamepad.Connection;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import pl.dawidurbanski.tcpgamepad.ByteHelpers;

/**
 * Created by Dawid on 23.01.2016.
 */
public class TCPclient {

    private String mADRESS_IP = "??";
    private int mADRESS_PORT = -1;

    public int CONNECTION_CONNECT_TIMEOUT = 5000; //time to wait for connection
    public int CONNECTION_READ_TIMEOUT = 0;       //0-no timeout

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived { void messageReceived(byte [] message);   }
    public OnMessageReceived mMessageListener = null;

    public interface OnEvent { void run(); }
    public OnEvent onConnected =null;
    public OnEvent onDisconnected =null;

    // while this is true, the server will continue running
    private boolean mRun = false;

    // used to send messages
    //private PrintWriter mBufferOut;
    private DataOutputStream mBufferOut;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPclient() { }

    /**
     * Sends the message entered by client to the server
     *
     * @param myByteArray bytes to send
     */
    public void sendBytes(byte[] myByteArray) throws IOException {
        int len  = myByteArray.length;

        if(mBufferOut==null || len<=0)
        { return;}

        mBufferOut.write(myByteArray, 0, len);
    }

    /**
     * Close the connection and release the members
     */
    public void stop()  {
        Log.i(TCPclient.class.getName(), "stopClient");
        mRun = false;

        try {
            if (mBufferOut != null) {
                mBufferOut.flush();
                mBufferOut.close();
            }
        }catch (IOException e)
        {
            Log.i(TCPclient.class.getName(), "stop(): "+e.toString());
        }

        mMessageListener = null;
        mBufferOut = null;
    }

    private void messageReceived(byte []buffer,int len){
        byte [] b2 = new byte[len];
        for(int i=0;i<len;i++){b2[i]=buffer[i];}
        //Log.w("TCPclient","recived "+len+"bytes: 0x"+ByteHelpers.ByteArrayToHexString(b2));
        mMessageListener.messageReceived(b2); //call the method messageReceived from MyActivity class
    }

    String errorMgs="";
    public boolean run(String adress,int port) {

        errorMgs = "";

        boolean ret = true;
        mADRESS_IP = adress.trim();
        mADRESS_PORT = port;

        mRun = true;

        Socket socket = new Socket();
        try {

            Log.i("TCPclient","connecting "+adress+":"+port+" (timeout:"+CONNECTION_CONNECT_TIMEOUT+")");

            try {
                socket = new Socket();
                socket.setSoTimeout(CONNECTION_READ_TIMEOUT);
                socket.connect(new InetSocketAddress(mADRESS_IP, mADRESS_PORT), CONNECTION_CONNECT_TIMEOUT);
            } catch (Exception e) {
                String errorStr = "Can't connect: " + e.toString();
                Log.w("TCPclient", errorStr);
                errorMgs += errorStr;
                return false;
            }

            Log.i("TCPclient", "connected " + adress + ":" + port);
            if(onConnected!=null)   onConnected.run();

            try {
                mBufferOut = new DataOutputStream( socket.getOutputStream() );
                DataInputStream mDataInputStream = new DataInputStream(socket.getInputStream());
                byte [] buffer = new byte[255];
                int numOfBytes = 0;
                while(mRun)
                {
                    numOfBytes = mDataInputStream.read(buffer);
                    if(numOfBytes>0 && mMessageListener != null) {
                        messageReceived(buffer,numOfBytes);
                    }
                }

            } catch (Exception e) {
                if(mRun!=false) {
                    Log.e(TCPclient.class.getName(), e.toString());
                    errorMgs += e.toString();
                    ret = false;
                }
            } finally {
                stop();
                socket.close();//the socket must be closed.
                if(onDisconnected !=null)   onDisconnected.run();
            }
        } catch(java.net.ConnectException ce)  {
            Log.w(TCPclient.class.getName(), "cant connect " + ce.toString());
            errorMgs+="cant connect"+ce.toString();
            ret=false;
        } catch (Exception e) {
            Log.e(TCPclient.class.getName(), "Error", e);
            errorMgs+=e.toString();
            ret=false;
        }
        try {
            socket.close();//the socket must be closed.
        }catch (Exception e){ Log.e("TCPclient",e.toString());}
        return ret;
    }
}
