package com.example.myapplication.common;

public class BinState {
    public String BinName;
    public int BinStatus;

    public BinState(String binName, int binStatus)
    {
        BinName = binName;
        BinStatus = binStatus;
    }
    public BinState(String binName, String binStatus)
    {
        BinName = binName;
        BinStatus = Integer.parseInt(binStatus);
    }
}
