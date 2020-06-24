package com.wshy.billcheck.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


/**
 * @author wshy
 * @data 2020/6/19
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class vertifyEntity {

        @NotNull(message = "名称不能为空")
        private String name;
//        private String orderId;
//        private String contractId;


}
