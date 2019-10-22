package com.example.myapplication.common;

public class BinInfoResponse {
    public int sum;
    public BinInfoItem[] bin_info;

    public class BinInfoItem
    {
        public int bin_id;
        public int status ;
        public float angel;
        public float temp;
        public String ip_address;
    }

    public BinInfoResponse(int su,int b_id, int stat, float a, float t, String ip)
    {
        sum = su;
        bin_info = new BinInfoItem[1];
        bin_info[0] = new BinInfoItem();
        bin_info[0].bin_id = b_id;
        bin_info[0].status = stat;
        bin_info[0].angel = a;
        bin_info[0].temp = t;
        bin_info[0].ip_address = ip;

    }

}
