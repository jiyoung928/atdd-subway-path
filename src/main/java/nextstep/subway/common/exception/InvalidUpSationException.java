package nextstep.subway.common.exception;

import nextstep.subway.common.response.ErrorCode;

public class InvalidUpSationException extends BusinessException {
    public InvalidUpSationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
