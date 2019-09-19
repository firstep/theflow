package cn.firstep.theflow.common.code;

import cn.firstep.theflow.common.AppCode;

/**
 * @author Alvin4u
 */
public enum SystemCode implements AppCode {
    TOKEN_IS_NULL(400001),

    BAD_TOKEN(400002),

    SIGN_TOKEN_FAILD(400003),

    BAD_REQUEST(400101),

    AUTHENTICATE_FAILD(400102),

    UNAUTHENTICATION(401001),

    UNAUTHORIZATION(403001),

    UNSUPPORTED_MEDIA_TYPE(415001),

    START_CONFIG_ERROR(500001),

    UNKNOW_ERROR(500000),

    ;

    private final int value;

    SystemCode(int value) {
        this.value = value;
    }

    @Override
    public String category() {
        return "SYSTEM";
    }

    @Override
    public int value() {
        return this.value;
    }

}
