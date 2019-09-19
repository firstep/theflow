package cn.firstep.theflow.common.code;

import cn.firstep.theflow.common.AppCode;

/**
 * @author Alvin4u
 */
public enum ProcessCode implements AppCode {

    ILLEGAL_ARGS(400001),

    NOT_FOUND(404001),

    START_ERROR(500001),

    DIAGRAM_IS_EMPTY(500002),

    ;

    private final int value;

    ProcessCode(int value) {
        this.value = value;
    }

    @Override
    public String category() {
        return "PROCESS";
    }

    @Override
    public int value() {
        return this.value;
    }
}
