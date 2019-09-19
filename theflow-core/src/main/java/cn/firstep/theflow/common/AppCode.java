package cn.firstep.theflow.common;

/**
 * System error code, subclass is enum type.
 * <p>
 * Examples of subclass code values are as follows:
 * <p>
 * // 2xx Success
 * <p>
 * OK(200000),
 * <p>
 * CREATED(201000),
 * <p>
 * ACCEPTED(202000),
 * <p>
 * NOCONTENT(204000),
 * <p>
 * // 4xx Client Error
 * <p>
 * BAD_REQUEST(400000),
 * <p>
 * UNAUTHORIZED(401000),
 * <p>
 * FORBIDDEN(403000),
 * <p>
 * NOT_FOUND(404000),
 * <p>
 * METHOD_NOT_ALLOWD(405000),
 * <p>
 * NOT_ACCEPTABLE(406000),
 * <p>
 * REQUEST_TIMEOUT(408000),
 * <p>
 * GONFILCT(409000),
 * <p>
 * GONE(410000),
 * <p>
 * PAYLOAD_TOO_LARGE(413000),
 * <p>
 * URI_TOO_LONG(414000),
 * <p>
 * UNSUPPORTED_MEDIA_TYPE(415000),
 * <p>
 * LOCKED(423000),
 * <p>
 * TOO_MANY_REQUESTS(429000),
 * <p>
 * // 5xx Server Error
 * <p>
 * SERVER_ERROR(500000),
 * <p>
 * SERVICE_UNAVAILABLE(503000);
 *
 * @author Alvin4u
 */
public interface AppCode {

    String category();

    String name();

    int value();

    default int httpStatus(int code) {
        return code / 1000;
    }

}
