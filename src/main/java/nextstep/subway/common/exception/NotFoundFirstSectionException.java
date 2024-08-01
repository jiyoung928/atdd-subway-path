package nextstep.subway.common.exception;

import nextstep.subway.common.response.ErrorCode;

public class NotFoundFirstSectionException extends BusinessException{
    public NotFoundFirstSectionException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public NotFoundFirstSectionException(ErrorCode errorCode) {
        super(errorCode);
    }
}
