package com.mj.poker.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PayType {

    private int payType;
    private String price;
    private String time;
}
