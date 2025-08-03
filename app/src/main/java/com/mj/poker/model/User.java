package com.mj.poker.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class User {

    private int userId;

    private String username;

    private String password;

    private String email;

    private String phoneNum;

    private long createTime;

    private long vip;

    private int vipType;

    private int money;
}
