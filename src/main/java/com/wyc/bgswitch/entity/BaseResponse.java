package com.wyc.bgswitch.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wyc
 */
@AllArgsConstructor
@Data
public class BaseResponse<T> implements Serializable {
    private T data;
}
