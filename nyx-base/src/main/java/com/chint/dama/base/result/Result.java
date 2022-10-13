package com.chint.dama.base.result;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Result<T> extends R {

    private String data;
    private T jsonData;
    private long ts = System.currentTimeMillis();

}
