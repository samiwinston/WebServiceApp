package com.example.android.myapplication.utils;


import android.os.AsyncTask;
import android.util.Log;

import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.msg.ReadCoilsRequest;
import com.ghgande.j2mod.modbus.msg.ReadCoilsResponse;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteMultipleCoilsRequest;
import com.ghgande.j2mod.modbus.msg.WriteMultipleCoilsResponse;
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.util.BitVector;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A wrapper class around the standard Jamod Modbus TCP connection and
 * transaction objects to allow easier usage
 * @author Saad Farooq
 *
 */
public class ModbusTcpClient {

    private static final String TAG = "ModbusTCPClient";


    ModbusTCPTransaction trans;
    TCPMasterConnection conn;

    public ModbusTcpClient() {
        trans = new ModbusTCPTransaction();
        conn = new TCPMasterConnection(null);
    }

    public ModbusTcpClient(String address,int port) throws UnknownHostException {
        trans = new ModbusTCPTransaction();
        conn = new TCPMasterConnection(InetAddress.getByName(address));
        conn.setPort(port);
    }

    public void setServer(String address, int port) throws UnknownHostException {
        conn.setAddress(InetAddress.getByName(address));
        conn.setPort(port);
    }

    public ReadMultipleRegistersResponse readMultipleRegisters(int ref, int count) throws Exception {
        ReadMultipleRegistersRequest req = new ReadMultipleRegistersRequest(ref,count);
        return (ReadMultipleRegistersResponse) getResponse(req);
    }

    public WriteMultipleRegistersResponse writeMultipleRegisters(int ref, SimpleRegister[] registers) throws Exception {
        WriteMultipleRegistersRequest req = new WriteMultipleRegistersRequest(ref,registers);
        return (WriteMultipleRegistersResponse) getResponse(req);
    }

    public WriteMultipleCoilsResponse writeMultipleCoilsRequest(int ref, BitVector bitVector) throws Exception {
        WriteMultipleCoilsRequest req = new WriteMultipleCoilsRequest(ref,bitVector);
        return (WriteMultipleCoilsResponse) getResponse(req);
    }

    public ReadCoilsResponse readCoilsResponse(int ref, int count) throws Exception {
        ReadCoilsRequest req = new ReadCoilsRequest(ref,count);
        return (ReadCoilsResponse) getResponse(req);
    }



    /**
     * Get the response for the passed ModbusRequest subclass in a background thread.
     * The method waits until a response is received.
     * @param req the Modbus request to transmit
     * @return the ModbusResponse object returned by server
     * @throws Exception
     */
    private ModbusResponse getResponse(final ModbusRequest req) throws Exception {
        ModbusResponse response = new AsyncTask<Void, Void, ModbusResponse>() {
            @Override
            protected ModbusResponse doInBackground(Void... params) {
                try {
                    conn.connect();
                    trans.setConnection(conn);
                    trans.setRequest(req);
                    trans.execute();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    return null;
                }
                ModbusResponse response = trans.getResponse();
                closeConnection();
                return response;
            }

        }.execute().get();

        return response;

    }

    public void closeConnection(){

        conn.close();
    }

    public ModbusResponse getPollResponse(final ModbusRequest req) throws Exception {
        ModbusResponse response = new AsyncTask<Void, Void, ModbusResponse>() {
            @Override
            protected ModbusResponse doInBackground(Void... params) {
                try {
                    conn.connect();
                    conn.setTimeout(1000);
                    Log.e(TAG, "Timeout: "+conn.getTimeout());
                    trans.setConnection(conn);
                    trans.setRequest(req);
                    trans.execute();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    return null;
                }

                return trans.getResponse();
            }

        }.execute().get();

        return response;
    }

}