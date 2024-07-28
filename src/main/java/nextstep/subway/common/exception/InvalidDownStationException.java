package nextstep.subway.common.exception;

import nextstep.subway.common.response.ErrorCode;

public class InvalidDownStationException extends BusinessException {
    public InvalidDownStationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
