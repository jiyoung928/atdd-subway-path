package nextstep.subway.common.exception;

import nextstep.subway.common.response.ErrorCode;

public class InsufficientStationsException extends BusinessException {
    public InsufficientStationsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
