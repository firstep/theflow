package cn.firstep.theflow.common.code;

import cn.firstep.theflow.common.AppCode;

/**
 * @author Alvin4u
 */
public enum FormCode implements AppCode {

    NOT_FOUND(404001),

    NOT_FOUND_MODEL(404002),

    NOT_FOUND_DEF(404003),

    DEPLOY_ERROR(500001),

    ;

    private final int value;

    FormCode(int value) {
        this.value = value;
    }

    @Override
    public String category() {
        return "FORM";
    }

    @Override
    public int value() {
        return this.value;
    }
}
