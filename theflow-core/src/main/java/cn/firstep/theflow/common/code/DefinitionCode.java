package cn.firstep.theflow.common.code;

import cn.firstep.theflow.common.AppCode;

/**
 * @author Alvin4u
 */
public enum DefinitionCode implements AppCode {

    NO_START_FORM(400005),

    NOT_FOUND(404001),

    NOT_FOUND_MODEL(404002),

    NO_PROCESS_DEFINE(409001),

    NO_FORM_RELATION(423001),

    DEPLOY_FAILD(500001),

    DEPLOY_ERROR(500002);

    private final int value;

    DefinitionCode(int value) {
        this.value = value;
    }

    @Override
    public String category() {
        return "DEF";
    }

    @Override
    public int value() {
        return this.value;
    }
}
