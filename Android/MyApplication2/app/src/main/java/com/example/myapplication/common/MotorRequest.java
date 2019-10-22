package com.example.myapplication.common;

public class MotorRequest {
    public int user_id;
    public int motor;
    public int dirc;

    public MotorRequest(int u, int m, int dir)
    {
        user_id = u;
        motor = m;
        dirc = dir;
    }
}
