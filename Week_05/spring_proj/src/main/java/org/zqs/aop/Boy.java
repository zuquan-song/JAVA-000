package org.zqs.aop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Boy implements Serializable {
    private String name;

    public void init() {
        System.out.println("hello! " + this.name);
    }

    public Boy create() {
        return new Boy("Zuquan Song");
    }

}
