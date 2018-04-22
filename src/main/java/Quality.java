import com.google.gson.annotations.Expose;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Quality {

    public enum Type {
        FULL_VIDEO,
        VIDEO_ONLY,
        AUDIO_ONLY
    }

    @Expose private String text;
    @Expose private String code;
    @Expose private String extension;
    @Expose private String resolution;
    @Expose private Type type;
    @Expose private String note;



    public static Quality parseAndCreate(String qualityLine) {
        Quality quality = null;
        String qualityLineRegEx = "([\\w-]+)\\s+(\\w{2,5})\\s+((audio only)|(\\w+))\\s+(.*)";
        Pattern qualityLinePattern = Pattern.compile(qualityLineRegEx);
        Matcher qualityLineMatcher = qualityLinePattern.matcher(qualityLine);

        if(qualityLineMatcher.find()) {

            quality = new Quality();

            quality.setCode(qualityLineMatcher.group(1));
            quality.setExtension(qualityLineMatcher.group(2));
            quality.setResolution(qualityLineMatcher.group(3));
            quality.setNote(qualityLineMatcher.group(6));
            if(qualityLine.contains("audio only")) {
                quality.setType(Type.AUDIO_ONLY);
                quality.setText(quality.getExtension() + "   " + quality.getNote());
            } else {
                if(qualityLine.contains("video only"))
                    quality.setType(Type.VIDEO_ONLY);
                else
                    quality.setType(Type.FULL_VIDEO);

                if(quality.getNote().equals("") || quality.getNote() == null)
                    quality.setText(quality.getResolution() + "   " + quality.getExtension());
                else
                    quality.setText(quality.getResolution() + "   " + quality.getExtension() + "   -   " + quality.getNote());
            }

        }

        return quality;
    }

    public Quality() {
        super();
    }

    public Quality(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return text;
    }

}
