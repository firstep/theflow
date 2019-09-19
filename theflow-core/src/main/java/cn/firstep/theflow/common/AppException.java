package cn.firstep.theflow.common;

/**
 * Application Exception.
 *
 * @author Alvin4u
 */
public class AppException extends RuntimeException {
    private static final long serialVersionUID = -4947337076998520913L;

    private AppCode code;

    AppException(AppCode code, String message) {
        super(message);
        this.code = code;
    }

    AppException(AppCode code, Object... args) {
        this(code, null, args);
    }

    AppException(AppCode code, String messageKey, Object... args) {
        this(code, AppContext.getMessage(messageKey == null ? code.category() + '_' + code.name() : messageKey, args));
    }

    public AppCode getCode() {
        return code;
    }

    /**
     * @param code 错误码
     * @param args 国际化参数
     * @return
     */
    public static AppException of(AppCode code, Object... args) {
        return new AppException(code, args);
    }

//	/**
//	 *
//	 * @param code
//	 * @param key  国际化key
//	 * @param args 格式化参数
//	 */
//	public static AppException of(Status code, String key, Object ... args) {
//		return new AppException(code, key, args);
//	}
//
//	public static AppException badRequest(String key, Object ... args) {
//		return AppException.of(AppStatus.BAD_REQUEST, key, args);
//	}
//
//	public static AppException unauthorized(String key, Object ... args) {
//		return AppException.of(AppStatus.UNAUTHORIZED, key, args);
//	}
//
//	public static AppException forbidden(String key, Object ... args) {
//		return AppException.of(AppStatus.FORBIDDEN, key, args);
//	}
//
//	public static AppException notFound(String key, Object ... args) {
//		return AppException.of(AppStatus.NOT_FOUND, key, args);
//	}
//
//	public static AppException notAcceptable(String key, Object ... args) {
//		return AppException.of(AppStatus.NOT_ACCEPTABLE, key, args);
//	}
//
//	public static AppException requestTimeout(String key, Object ... args) {
//		return AppException.of(AppStatus.REQUEST_TIMEOUT, key, args);
//	}
//
//	public static AppException gonfilct(String key, Object ... args) {
//		return AppException.of(AppStatus.GONFILCT, key, args);
//	}
//
//	public static AppException gone(String key, Object ... args) {
//		return AppException.of(AppStatus.GONE, key, args);
//	}
//
//	public static AppException payloadTooLarge(String key, Object ... args) {
//		return AppException.of(AppStatus.PAYLOAD_TOO_LARGE, key, args);
//	}
//
//	public static AppException tooManyRequests(String key, Object ... args) {
//		return AppException.of(AppStatus.TOO_MANY_REQUESTS, key, args);
//	}
//
//	public static AppException serverError(String key, Object ... args) {
//		return AppException.of(AppStatus.SERVER_ERROR, key, args);
//	}
//
//	public static AppException serviceUnavailable(String key, Object ... args) {
//		return AppException.of(AppStatus.SERVICE_UNAVAILABLE, key, args);
//	}

}
