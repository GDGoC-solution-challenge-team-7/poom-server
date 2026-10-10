package gdg.challenge.poom.domain.journal.converter;

import gdg.challenge.poom.domain.journal.dto.request.enums.JournalAuthor;
import gdg.challenge.poom.global.error.code.status.JournalErrorCode;
import gdg.challenge.poom.global.error.exception.handler.JournalException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class JournalAuthorConverter implements Converter<String, JournalAuthor> {

    @Override
    public JournalAuthor convert(String source) {
        return switch (source.toLowerCase()) {
            case "me" -> JournalAuthor.ME;
            case "partner" -> JournalAuthor.PARTNER;
            default -> throw new JournalException(JournalErrorCode.JOURNAL_INVALID_AUTHOR);
        };
    }
}
