package nextstep.subway.common.exception;

import nextstep.subway.common.response.ErrorCode;

public class SectionDistanceTooLongException extends BusinessException{
    public SectionDistanceTooLongException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public SectionDistanceTooLongException(ErrorCode errorCode) {
        super(errorCode);
    }
}
