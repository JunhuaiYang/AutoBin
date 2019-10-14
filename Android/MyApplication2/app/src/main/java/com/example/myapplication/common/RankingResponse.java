package com.example.myapplication.common;

public class RankingResponse {
        public int user_sum;
        public UserScore[] user_scores;

        public class UserScore {
            public int ranking ;
            public String user_name ;
            public int score;
        }
}
