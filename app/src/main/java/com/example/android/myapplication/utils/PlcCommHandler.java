package com.example.android.myapplication.utils;

import com.example.android.myapplication.model.QueueWashResponse;
import com.ghgande.j2mod.modbus.msg.ReadCoilsResponse;
import com.ghgande.j2mod.modbus.msg.WriteMultipleCoilsResponse;
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.util.BitVector;

/**
 * Created by abedch on 9/16/2015.
 */
public class PlcCommHandler {

    private static final int PORT = 502;

    private  Integer regRef;
    private  Integer startCoilRef;
    private  Integer lockoutCoilRef;
    private  ModbusTcpClient modbusTcpClient;
    private  String plcAddress;

    public PlcCommHandler(String plcAddress, Integer regRef, Integer startCoilRef,Integer lockoutCoilRef) throws Exception {
        this.plcAddress = plcAddress;
        this.modbusTcpClient  = new ModbusTcpClient(plcAddress,PORT);
        this.regRef = regRef;
        this.startCoilRef = startCoilRef;
        this.lockoutCoilRef = lockoutCoilRef;
    }

    public  String initWash(QueueWashResponse wash,Integer orderNum) throws Exception
    {

        SimpleRegister simpleRegister0 = new SimpleRegister(0);// terminal id;
        SimpleRegister simpleRegister1 = new SimpleRegister(wash.getWashTypeCode());// wash type code
        SimpleRegister simpleRegister2 = new SimpleRegister(wash.getAddonsCode());// wash addon code
        SimpleRegister simpleRegister3 = new SimpleRegister(orderNum+1000);// washcode
        SimpleRegister simpleRegister4 = new SimpleRegister(0);//future
        SimpleRegister simpleRegister5 = new SimpleRegister(0);//future
        SimpleRegister simpleRegister6 = new SimpleRegister(0);//future
        SimpleRegister simpleRegister7 = new SimpleRegister(0);//future
        SimpleRegister simpleRegister8 = new SimpleRegister(0);//future
        SimpleRegister simpleRegister9 = new SimpleRegister(0);// plc Acknowledge INT

        SimpleRegister[] regs = {simpleRegister0, simpleRegister1, simpleRegister2, simpleRegister3
                , simpleRegister4, simpleRegister5, simpleRegister6, simpleRegister7,
                simpleRegister8, simpleRegister9};


        WriteMultipleRegistersResponse response = modbusTcpClient.writeMultipleRegisters(regRef, regs);

        if(response.getWordCount() == 10 && response.getFunctionCode() == 16 && response.getReference()==regRef)
        {
            WriteMultipleCoilsResponse cResponse = startWash();

            String responseMsg = "Response Word Count :"+response.getWordCount() +"Function Code "+response.getFunctionCode()+" response Ref"+response.getReference();

            if(cResponse !=null)
                responseMsg+=" Coil Response func Code"+cResponse.getFunctionCode()+" Coil Response Ref "+cResponse.getReference();

            return responseMsg;

        }
        else
        {
            return "Illegal plc response"+response.getHexMessage();
        }

    }

    private  WriteMultipleCoilsResponse startWash() throws Exception
    {
        BitVector bitVector = new BitVector(1);
        bitVector.setBit(0,true);
        WriteMultipleCoilsResponse response = modbusTcpClient.writeMultipleCoilsRequest(startCoilRef,bitVector);
       return response;
    }

    public  boolean isReadyForWash() throws Exception
    {
        ReadCoilsResponse response = modbusTcpClient.readCoilsResponse(lockoutCoilRef, 1);
        return !response.getCoilStatus(0); // if true then its busy
    }

    public String getPlcAddress(){
        return plcAddress;
    }

}
