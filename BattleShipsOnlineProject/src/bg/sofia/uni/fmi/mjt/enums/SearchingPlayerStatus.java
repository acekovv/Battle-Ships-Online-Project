package bg.sofia.uni.fmi.mjt.enums;

public enum SearchingPlayerStatus {
	WAITING("waiting"), FOUND("found"), NOT_FOUND("not found");

	private final String searchingPlayerStatusCode;

	private SearchingPlayerStatus(String searchingPlayerStatusCode) {
		this.searchingPlayerStatusCode = searchingPlayerStatusCode;
	}
} 
