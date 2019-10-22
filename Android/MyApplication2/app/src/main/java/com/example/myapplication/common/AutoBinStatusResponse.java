package com.example.myapplication.common;

public class AutoBinStatusResponse
{
    public int status;
    public float angel;
    public float temp;
    public AutoBinStatusResponse(int s, float a, float t)
    {
        status = s;
        angel = a;
        temp = t;
    }
}
