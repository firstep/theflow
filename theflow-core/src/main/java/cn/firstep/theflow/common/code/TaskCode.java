package cn.firstep.theflow.common.code;

import cn.firstep.theflow.common.AppCode;

/**
 * @author Alvin4u
 */
public enum TaskCode implements AppCode {
    NOT_FOUND(404001),

    HAS_CLAIMED(409001),

    HAS_DELEGATED(409002),

    PROCESS_ERROR(500001);

    private final int value;

    TaskCode(int value) {
        this.value = value;
    }

    @Override
    public String category() {
        return "TASK";
    }

    @Override
    public int value() {
        return this.value;
    }
}
