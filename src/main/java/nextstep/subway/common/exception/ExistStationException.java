package nextstep.subway.common.exception;

import nextstep.subway.common.response.ErrorCode;

public class ExistStationException extends BusinessException {
    public ExistStationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ExistStationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
