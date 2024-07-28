package nextstep.subway.common.exception;

import nextstep.subway.common.response.ErrorCode;

public class NotLastStationException extends BusinessException {
    public NotLastStationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
