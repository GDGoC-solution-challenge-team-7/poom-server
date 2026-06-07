package gdg.challenge.poom.domain.chat.entity.enums;

public enum UploadDomain {
    PROFILE_IMAGE("member/profile"),
    CHAT_IMAGE("chat/message"),
    EXPERT_VERIFICATION("expert/verification"),
    JOURNAL_IMAGE("member/journal");

    private final String path;

    UploadDomain(String path) {
        this.path = path;
    }

    public boolean matches(String imageUrl) {
        String regex = "^" + path + "/\\d+/"
                + "[0-9a-fA-F]{8}-"
                + "[0-9a-fA-F]{4}-"
                + "[0-9a-fA-F]{4}-"
                + "[0-9a-fA-F]{4}-"
                + "[0-9a-fA-F]{12}"
                + "\\.[a-zA-Z0-9]+$";

        return imageUrl.matches(regex);
    }
}
