package org.zqs.aop;

import lombok.Data;

import java.util.List;

@Data
public class Kclass {

    List<Boy> boys;

    public void dong() {
        System.out.println(this.boys);
    }
}
