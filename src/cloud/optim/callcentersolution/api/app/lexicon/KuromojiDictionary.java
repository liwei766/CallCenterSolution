package cloud.optim.callcentersolution.api.app.lexicon;

public class KuromojiDictionary {
	private static final String NOUN = "名詞";

	/** 表記 */
	private String surface;

	/** セグメント値 */
	private String segmentationValue;

	/** 読み */
	private String readingsValue;

	/** 品詞 */
	private String partOfSpeech;

	/**
	 * コンストラクタ
	 * @param surface 表記
	 * @param readingsValue 読み
	 */
	public KuromojiDictionary(String surface, String readingsValue) {
		this.surface = surface;
		this.segmentationValue = surface;
		this.readingsValue = readingsValue;
		this.partOfSpeech = NOUN;
	}

	/**
	 * @return surface
	 */
	public String getSurface() {
		return surface;
	}
	/**
	 * @param surface セットする surface
	 */
	public void setSurface(String surface) {
		this.surface = surface;
	}
	/**
	 * @return segmentationValue
	 */
	public String getSegmentationValue() {
		return segmentationValue;
	}
	/**
	 * @param segmentationValue セットする segmentationValue
	 */
	public void setSegmentationValue(String segmentationValue) {
		this.segmentationValue = segmentationValue;
	}
	/**
	 * @return readingsValue
	 */
	public String getReadingsValue() {
		return readingsValue;
	}
	/**
	 * @param readingsValue セットする readingsValue
	 */
	public void setReadingsValue(String readingsValue) {
		this.readingsValue = readingsValue;
	}
	/**
	 * @return partOfSpeech
	 */
	public String getPartOfSpeech() {
		return partOfSpeech;
	}
	/**
	 * @param partOfSpeech セットする partOfSpeech
	 */
	public void setPartOfSpeech(String partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}
}
