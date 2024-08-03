package nextstep.subway.common.exception;

import nextstep.subway.common.response.ErrorCode;

public class NotExistStationException extends BusinessException {
    public NotExistStationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public NotExistStationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
