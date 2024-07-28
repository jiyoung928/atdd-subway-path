package nextstep.subway.common.exception;

import nextstep.subway.common.response.ErrorCode;

public class SectionDistanceNotValidException extends BusinessException{
    public SectionDistanceNotValidException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public SectionDistanceNotValidException(ErrorCode errorCode) {
        super(errorCode);
    }
}
