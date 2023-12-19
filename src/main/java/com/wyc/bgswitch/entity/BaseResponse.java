package com.wyc.bgswitch.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * @author wyc
 */
@Data
public class BaseResponse<T> implements Serializable {
    private T data;
}
